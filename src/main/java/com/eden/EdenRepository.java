package com.eden;


import com.eden.bible.Bible;
import com.eden.bible.BibleList;
import com.eden.bible.Passage;
import com.eden.bible.Reference;

/**
 * An interface for getting verse data from an abstract repository, which might be from a database or from, a RESTful
 * API, or anything else. It is designed to be the single point of entry for accessing Verse data, acting as a simple
 * dependency injector
 */
public abstract class EdenRepository {

    private Bible selectedBible;
    private BibleList bibleList;

    public EdenRepository() {

    }

    public Bible getSelectedBible() {
        if(selectedBible == null) {
            try {
                selectedBible = getBibleClass().getConstructor().newInstance();
                selectedBible.setId(Eden.getInstance().config().getString(this.getClass().getName() + "_selectedBibleId"));
                selectedBible.get();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return selectedBible;
    }

    public Bible getBible(String id) {
        if(selectedBible == null) {
            try {
                selectedBible = getBibleClass().getConstructor().newInstance();
                selectedBible.setId(id);
                selectedBible.get();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return selectedBible;
    }

    public void setSelectedBible(Bible selectedBible) {
        this.selectedBible = selectedBible;
        Eden.getInstance().config().putString(this.getClass().getName() + "_selectedBibleId", selectedBible.getId());
    }

    public void setBibleList(BibleList bibleList) {
        this.bibleList = bibleList;
    }

    public Passage lookupVerse(String reference) {
        Reference.Builder builder = new Reference.Builder();
        builder.setBible(getSelectedBible());
        builder.parseReference(reference);
        Reference ref = builder.create();

        Passage passage;
        try {
            passage = getPassageClass().getConstructor(Reference.class).newInstance(ref);
            passage.get();
        }
        catch(Exception e) {
            e.printStackTrace();
            passage = null;
        }

        return passage;
    }

    public Passage lookupVerse(String reference, String bibleId) {
        Reference.Builder builder = new Reference.Builder();
        builder.setBible(getBible(bibleId));
        builder.parseReference(reference);
        Reference ref = builder.create();

        Passage passage;
        try {
            passage = getPassageClass().getConstructor(Reference.class).newInstance(ref);
            passage.get();
        }
        catch(Exception e) {
            e.printStackTrace();
            passage = null;
        }

        return passage;
    }

    public BibleList getBibleList() {
        if(bibleList == null) {
            try {
                bibleList = getBibleListClass().getConstructor().newInstance();
                bibleList.get();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        return bibleList;
    }

    public abstract Class<? extends BibleList> getBibleListClass();
    public abstract Class<? extends Bible> getBibleClass();
    public abstract Class<? extends Passage> getPassageClass();
}

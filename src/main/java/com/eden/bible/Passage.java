package com.eden.bible;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for a list of Verses of a particular type. As with all implementations of
 * {@link AbstractVerse}, the {@link Reference} is immutable, and so this passage will always refer
 * to the same set of verses within one chapter of the Bible. In most cases, you will want to primarily
 * use some implementation of Passage as opposed to Verse, because it is more powerful, but when set
 * with just a single verse, acts very similar to the Verse class.
 *
 * @see Verse
 */
public class Passage extends AbstractVerse {
    protected List<Verse> verses;

    /**
     * Create this Passage with the given Reference. Upon creation, an unmodifiable list of Verses
     * will be created of the type specified by the type parameter T. As of now, this feature is buggy,
     * and does not work with any deeper subclasses of Passage. As such, only direct descendants of
     * Passage should be created, and those implementations should always be final classes.
     *
     * @param reference the reference of this Passage
     */
    public Passage(Reference reference) {
        super(reference);

        Class<? extends Verse> verseClass = getVerseClass();

        this.verses = new ArrayList<>();
        for (Integer verseNum : this.reference.getVerses()) {
            try {
                Reference ref = new Reference.Builder()
                        .setBook(this.reference.getBook())
                        .setChapter(this.reference.getChapter())
                        .setVerses(verseNum).create();

                Verse verse = new Verse(reference);

                this.verses.add(verse);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Class<? extends Verse> getVerseClass() {
        return null;
    }

    /**
     * Get the list of Verses associated with this Passage
     *
     * @return
     */
    public List<Verse> getVerses() {
        return verses;
    }

    @Override
    public String getFormattedText() {
        if (verses.size() > 0) {
            String text = "";

            text += verseFormatter.onPreFormat(this);

            for (int i = 0; i < verses.size(); i++) {
                Verse verse = verses.get(i);

                text += verseFormatter.onFormatVerseStart(verse.getVerseNumber());
                text += verseFormatter.onFormatText(verse.getText());

                if (i < verses.size() - 1) {
                    text += verseFormatter.onFormatVerseEnd();
                }
            }

            text += verseFormatter.onPostFormat();

            return text.trim();
        } else {
            return "";
        }
    }

    @Override
    public String getText() {
        if (verses.size() > 0) {
            String text = "";

            for (int i = 0; i < verses.size(); i++) {
                if (verses.get(i) != null)
                    text += verses.get(i).getText() + " ";
            }

            return text.trim();
        } else {
            return "";
        }
    }

    @Override
    public int compareTo(AbstractVerse verse) {
        return this.getReference().compareTo(verse.getReference());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        Passage verse = (Passage) o;

        return this.getReference().equals(verse.getReference());
    }

    @Override
    public int hashCode() {
        return this.reference != null ? this.reference.hashCode() : 0;
    }
}

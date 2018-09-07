package com.caseyjbrooks.eden.dummy;

import com.eden.bible.Bible;
import com.eden.bible.BibleList;
import com.eden.bible.Passage;
import com.eden.repositories.EdenRepository;
import com.eden.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class DummyRepository extends EdenRepository {

    @Override
    public Bible getBible(String id) {
        if(!TextUtils.isEmpty(id) && !id.startsWith("list-") && !id.startsWith("create-")) {
            DummyBible bible = new DummyBible();
            bible.setId(id);
            bible.setName("Dummy Bible");
            List<DummyBook> books = new ArrayList<>();
            books.add(new DummyBook(1, "AAAAA", 6, 4, 24, 21, 17, 6));
            bible.setBooks(books);
            return bible;
        }

        return super.getBible(id);
    }

    @Override
    public Class<? extends BibleList> getBibleListClass() {
        return DummyBibleList.class;
    }

    @Override
    public Class<? extends Bible> getBibleClass() {
        return DummyBible.class;
    }

    @Override
    public Class<? extends Passage> getPassageClass() {
        return DummyPassage.class;
    }
}

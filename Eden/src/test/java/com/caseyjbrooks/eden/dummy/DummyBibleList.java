package com.caseyjbrooks.eden.dummy;

import com.eden.bible.Bible;
import com.eden.bible.BibleList;
import com.eden.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class DummyBibleList extends BibleList<DummyBible> {

    @Override
    public boolean hasBible(String id) {
        return (!TextUtils.isEmpty(id) && id.startsWith("list-"));

    }

    @Override
    public Bible getBible(String id) {
        if(!TextUtils.isEmpty(id) && id.startsWith("list-")) {
            DummyBible bible = new DummyBible();
            bible.setId(id);
            bible.setName("Dummy List");
            List<DummyBook> books = new ArrayList<>();
            books.add(new DummyBook(1, "AAAAA", 6, 4, 24, 21, 17, 6));
            bible.setBooks(books);
            return bible;
        }

        return null;
    }
}

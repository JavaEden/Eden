package com.caseyjbrooks.eden.dummy;

import com.eden.bible.Book;

public class DummyBook extends Book {
    public DummyBook(int location, String name, int... chapters) {
        super();
        setName(name);
        setLocation(location);
        setChapters(chapters);

        if(name.length() > 3)
            setAbbreviation(name.substring(0, 3));
        else
            setAbbreviation(name);
    }
}

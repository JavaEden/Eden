package com.eden.simple;

import com.eden.bible.Bible;

public class SimpleBible extends Bible<SimpleBook> {

    public SimpleBible() {
        this.books.add(new SimpleBook());
    }

    @Override
    public SimpleBook parseBook(String bookName) {
        SimpleBook book = new SimpleBook();
        book.setName(bookName);
        return book;
    }
}

package com.caseyjbrooks.eden.dummy;

import com.eden.bible.Bible;

public class DummyBible extends Bible<DummyBook> {
    public DummyBible() {
        this.books.add(new DummyBook(0, "AAAAA"));
        this.books.add(new DummyBook(1, "BBBBB"));
        this.books.add(new DummyBook(2, "CCCCC"));
        this.books.add(new DummyBook(3, "DDDDD"));
        this.books.add(new DummyBook(4, "EEEEE"));

        this.books.add(new DummyBook(5, "ABCDE FGHIJ"));
        this.books.add(new DummyBook(6, "KLMNO PQRST"));
        this.books.add(new DummyBook(7, "UVW XYZ"));

        this.books.add(new DummyBook(8, "AA BB CC DD EE"));
        this.books.add(new DummyBook(9, "AA BB CC DD EE FF GG HH II JJ KK LL MM NN OO PP QQ RR SS TT UU VV WW XX YY ZZ"));

        this.books.add(new DummyBook(10, "ASDF"));
        this.books.add(new DummyBook(11, "QWERTY"));
        this.books.add(new DummyBook(12, "WASD"));
    }
}

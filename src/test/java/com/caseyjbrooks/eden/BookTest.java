package com.caseyjbrooks.eden;

import com.caseyjbrooks.eden.dummy.DummyBook;
import com.eden.bible.Book;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

public class BookTest {

    @Test
    public void testVerseValidations() throws Throwable {
        Book book = new DummyBook(1, "Dummy Book", 45, 22, 76, 12, 5);

        assertThat(book.numChapters(), is(equalTo(5)));
        assertThat(book.numVersesInChapter(3), is(equalTo(76)));
        assertThat(book.numVersesInChapter(0), is(equalTo(-1)));
        assertThat(book.numVersesInChapter(6), is(equalTo(-1)));
        assertThat(book.validateChapter(5), is(true));
        assertThat(book.validateChapter(6), is(false));
        assertThat(book.validateVerseInChapter(3, 76), is(true));
        assertThat(book.validateVerseInChapter(3, 77), is(false));
        assertThat(book.validateVerseInChapter(3, 1), is(true));
        assertThat(book.validateVerseInChapter(3, 0), is(false));
    }

    @Test
    public void testEquality() throws Throwable {
        Book book1 = new DummyBook(1, "Book 1", 45, 22, 76, 12, 5);
        Book book2 = new DummyBook(1, "Book 2", 45, 22, 76, 12, 5);
        Book book3 = new DummyBook(2, "Book 3", 44, 21, 75, 11, 4);

        assertThat(book1, is(equalTo(book2)));
        assertThat(book1, is(not(equalTo(book3))));

        assertThat(book1.hashCode(), is(equalTo(book2.hashCode())));
        assertThat(book1, is(not(equalTo(book3.hashCode()))));

        assertThat(book1.equals(book2), is(true));
        assertThat(book1 == book2, is(false));
        assertThat(book1.equals(null), is(false));
    }

    @Test
    public void testGettersAndSetters() throws Throwable {
        Book book = new DummyBook(1, "Dummy Book", 45, 22, 76, 12, 5);

        book.setId("id-1");
        assertThat(book.getId(), is(equalTo("id-1")));

        book.setLocation(3);
        assertThat(book.getLocation(), is(equalTo(3)));

        book.setName("Book Name");
        assertThat(book.getName(), is(equalTo("Book Name")));

        book.setAbbreviation("BN");
        assertThat(book.getAbbreviation(), is(equalTo("BN")));

        book.setChapters(1, 2, 3, 4, 5);
        assertThat(book.getChapters(), contains(1, 2, 3, 4, 5));

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        book.setChapters(list);
        assertThat(book.getChapters(), contains(1, 2, 3, 4, 5));
    }
}

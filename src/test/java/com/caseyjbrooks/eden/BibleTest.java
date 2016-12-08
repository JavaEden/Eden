package com.caseyjbrooks.eden;

import com.caseyjbrooks.eden.dummy.DummyBible;
import com.caseyjbrooks.eden.dummy.DummyBook;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

// TODO: Add '.equals()' and '.hashcode()' for Bible class
// TODO: Add more intelligent and/or configurable comparator
public class BibleTest {

    @Test
    public void testGettersAndSetters() throws Throwable {
        DummyBible bible = new DummyBible();

        bible.setId("id-1");
        assertThat(bible.getId(), is(equalTo("id-1")));

        bible.setName("Dummy Bible");
        assertThat(bible.getName(), is(equalTo("Dummy Bible")));

        bible.setAbbreviation("DB");
        assertThat(bible.getAbbreviation(), is(equalTo("DB")));

        bible.setCopyright("Copyright 2001");
        assertThat(bible.getCopyright(), is(equalTo("Copyright 2001")));

        bible.setLanguage("English");
        assertThat(bible.getLanguage(), is(equalTo("English")));

        List<DummyBook> books = new ArrayList<>();
        books.add(new DummyBook(0, "Book", 1, 2, 3, 4, 5));

        bible.setBooks(books);
        assertThat(bible.getBooks(), contains(new DummyBook(0, "Book", 1, 2, 3, 4, 5)));

        assertThat(bible.get(), is(true));
    }

    @Test
    public void testParseBook() throws Throwable {
        DummyBible bible = new DummyBible();

        List<DummyBook> books = new ArrayList<>();

        books.add(new DummyBook(0, "AAAAA"));
        books.add(new DummyBook(10, "ASDF"));

        DummyBook book = new DummyBook(13, "Bible Book");
        book.setAbbreviation("something else");
        books.add(book);

        bible.setBooks(books);

        // Test parsing a couple different books in different locations to make sure its parsing the correct book
        assertThat(bible.parseBook("AAAAA"), is(notNullValue()));
        assertThat(bible.parseBook("AAAAA").getLocation(), is(equalTo(0)));

        assertThat(bible.parseBook("ASDF"), is(notNullValue()));
        assertThat(bible.parseBook("ASDF").getLocation(), is(equalTo(10)));

        // Get a book by it's abbreviation, which is the first three letters for a Dummy Book
        assertThat(bible.parseBook("ASD"), is(notNullValue()));
        assertThat(bible.parseBook("ASD").getLocation(), is(equalTo(10)));

        // Bible Book had it's abbreviation set. Make sure we can parse it by its name, abbr, and part of its name and abbr
        assertThat(bible.parseBook("Bible Book"), is(notNullValue()));
        assertThat(bible.parseBook("Bible Book").getLocation(), is(equalTo(13)));

        assertThat(bible.parseBook("something else"), is(notNullValue()));
        assertThat(bible.parseBook("something else").getLocation(), is(equalTo(13)));

        assertThat(bible.parseBook("Bib"), is(notNullValue()));
        assertThat(bible.parseBook("Bib").getLocation(), is(equalTo(13)));

        assertThat(bible.parseBook("some"), is(notNullValue()));
        assertThat(bible.parseBook("some").getLocation(), is(equalTo(13)));

        // Test that trying to find a book not in this Bible is null
        assertThat(bible.parseBook("I'm Not In This Bible"), is(nullValue()));
    }

    @Test
    public void testComparison() {
        DummyBible bibleA = new DummyBible();
        bibleA.setName("AAAAA");

        DummyBible bibleB = new DummyBible();
        bibleB.setName("BBBBB");

        assertThat(bibleA.compareTo(bibleB), is(lessThan(0)));
    }
}

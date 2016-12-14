package com.caseyjbrooks.eden;

import com.caseyjbrooks.eden.dummy.DummyBible;
import com.caseyjbrooks.eden.dummy.DummyBibleList;
import com.caseyjbrooks.eden.dummy.DummyBook;
import com.eden.bible.Book;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

// TODO: Add '.equals()' and '.hashcode()' for Bible class
// TODO: Add more intelligent and/or configurable comparator

/**
 * Testing covers public API for Book.class, Bible.class, and BibleList.class. Assumes all other involved classes
 * work flawlessly. Doesn't test for concurrency or extenuating circumstances caused by invalid state produced from
 * inherited classes, reflection, or anything else.
 */
public class BiblesTest {

// Test Book class functionality
//----------------------------------------------------------------------------------------------------------------------
@Test
public void testBookVerseValidations() throws Throwable {
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
    public void testBookComparison() throws Throwable {
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
    public void testBookGettersAndSetters() throws Throwable {
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

// Test Bible class functionality
//----------------------------------------------------------------------------------------------------------------------
    @Test
    public void testBibleGettersAndSetters() throws Throwable {
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
    public void testBibleComparison() {
        DummyBible bibleA = new DummyBible();
        bibleA.setName("AAAAA");

        DummyBible bibleB = new DummyBible();
        bibleB.setName("BBBBB");

        assertThat(bibleA.compareTo(bibleB), is(lessThan(0)));
    }

// Test BibleList class functionality
//----------------------------------------------------------------------------------------------------------------------

    @Test
    public void testBibleListGettersAndSetters() throws Throwable {
        DummyBibleList bibleList = new DummyBibleList();

        bibleList.setBibles(null);
        assertThat(bibleList.getBibles(), is(nullValue()));

        assertThat(bibleList.get(), is(true));
    }

}

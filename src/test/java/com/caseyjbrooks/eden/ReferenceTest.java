package com.caseyjbrooks.eden;

import com.caseyjbrooks.eden.dummy.DummyBible;
import com.caseyjbrooks.eden.dummy.DummyBook;
import com.eden.bible.Reference;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 * Tests that creation of Reference objects assuming the parser works correctly (see ParserTest). Testing covers public
 * API for Reference.class and Reference.Builder.class. Assumes all other involved classes work flawlessly. Doesn't test
 * for concurrency or extenuating circumstances caused by invalid state produced from reflection or anything else.
 */
public class ReferenceTest {

    @Test
    public void testReferenceNavigation() throws Throwable {
        // Build a custom Bible so we can test how well it grabs chapters, verses, etc. from a fully-populated Bible

        DummyBible bible = new DummyBible();
        List<DummyBook> books = new ArrayList<>();
        books.add(new DummyBook(1, "AAAAA", 6, 4, 24, 21, 17, 6));
        books.add(new DummyBook(2, "BBBBB", 56, 43, 23));
        books.add(new DummyBook(3, "CCCCC", 99));
        bible.setBooks(books);

        Reference.Builder builder = new Reference.Builder();
        builder.setBible(bible);

        builder.parseReference("BBBBB 2:20-40");
        Reference ref = builder.create();
        assertThat(ref.getBible().getClass(), is(equalTo(DummyBible.class)));
        assertThat(ref.getBook().getName(), is(equalTo("BBBBB")));
        assertThat(ref.getChapter(), is(equalTo(2)));
        assertThat(ref.getFirstVerse(), is(equalTo(20)));
        assertThat(ref.getVerses().size(), is(equalTo(21)));

        // Test getting next verses in the middle of a chapter that's in the middle of a book
        // - Next verse after BBBBB 2:20-40 should be BBBBB 2:41 (next single verse after last verse in this passage)
        // - Next chapter after BBBBB 2:20-40 should be BBBBB 3:1-23 (next full chapter)
        // - Next book after BBBBB 2:20-40 should be CCCCC 1:1-99 (first full chapter of next book)
        Reference ref_navigated = ref.next(Reference.TYPE_VERSE).create();
        assertThat(ref_navigated.toString(), is(equalTo("BBBBB 2:41")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("BBBBB")));
        assertThat(ref_navigated.getChapter(), is(equalTo(2)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(41)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(1)));

        ref_navigated = ref.next(Reference.TYPE_CHAPTER).create();
        assertThat(ref_navigated.toString(), is(equalTo("BBBBB 3:1-23")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("BBBBB")));
        assertThat(ref_navigated.getChapter(), is(equalTo(3)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(23)));

        ref_navigated = ref.next(Reference.TYPE_BOOK).create();
        assertThat(ref_navigated.toString(), is(equalTo("CCCCC 1:1-99")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("CCCCC")));
        assertThat(ref_navigated.getChapter(), is(equalTo(1)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(99)));

        // Test getting previous verses in the middle of a chapter that's in the middle of a book
        // - Previous verse after BBBBB 2:20-40 should be BBBBB 2:19 (previous single verse before first verse in this passage)
        // - Previous chapter after BBBBB 2:20-40 should be BBBBB 1:1-56 (previous full chapter)
        // - Previous book after BBBBB 2:20-40 should be AAAAA 1:1-6 (first full chapter of previous book)
        ref_navigated = ref.previous(Reference.TYPE_VERSE).create();
        assertThat(ref_navigated.toString(), is(equalTo("BBBBB 2:19")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("BBBBB")));
        assertThat(ref_navigated.getChapter(), is(equalTo(2)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(19)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(1)));

        ref_navigated = ref.previous(Reference.TYPE_CHAPTER).create();
        assertThat(ref_navigated.toString(), is(equalTo("BBBBB 1:1-56")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("BBBBB")));
        assertThat(ref_navigated.getChapter(), is(equalTo(1)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(56)));

        ref_navigated = ref.previous(Reference.TYPE_BOOK).create();
        assertThat(ref_navigated.toString(), is(equalTo("AAAAA 1:1-6")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("AAAAA")));
        assertThat(ref_navigated.getChapter(), is(equalTo(1)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(6)));

        // Test getting next verses at the end of a chapter that's at the end of a book
        // - Next verse after BBBBB 3:20-23 should be CCCCC 1:1 (first single verse of the first chapter of the next book)
        // - Next chapter after BBBBB 3:20-23 should be CCCCC 1:1-99 (first full chapter of the next book)
        // - Next book after BBBBB 3:20-23 should be CCCCC 1:1-99 (first full chapter of the next book)
        builder.parseReference("BBBBB 3:20-23");
        ref = builder.create();

        ref_navigated = ref.next(Reference.TYPE_VERSE).create();
        assertThat(ref_navigated.toString(), is(equalTo("CCCCC 1:1")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("CCCCC")));
        assertThat(ref_navigated.getChapter(), is(equalTo(1)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(1)));

        ref_navigated = ref.next(Reference.TYPE_CHAPTER).create();
        assertThat(ref_navigated.toString(), is(equalTo("CCCCC 1:1-99")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("CCCCC")));
        assertThat(ref_navigated.getChapter(), is(equalTo(1)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(99)));

        ref_navigated = ref.next(Reference.TYPE_BOOK).create();
        assertThat(ref_navigated.toString(), is(equalTo("CCCCC 1:1-99")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("CCCCC")));
        assertThat(ref_navigated.getChapter(), is(equalTo(1)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(99)));

        // Test getting previous verses at the beginning of the chapter at the beginning of a book
        // - Previous verse after BBBBB 1:1-5 should be AAAAA 6:6 (last single verse of the last chapter of the previous book)
        // - Previous chapter after BBBBB 1:1-5 should be AAAAA 6:1-6 (last full chapter of the previous book)
        // - Previous book after BBBBB 1:1-5 should be AAAAA 1:1-99 (first full chapter of previous book)
        builder.parseReference("BBBBB 1:1-5");
        ref = builder.create();

        ref_navigated = ref.previous(Reference.TYPE_VERSE).create();
        assertThat(ref_navigated.toString(), is(equalTo("AAAAA 6:6")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("AAAAA")));
        assertThat(ref_navigated.getChapter(), is(equalTo(6)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(6)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(1)));

        ref_navigated = ref.previous(Reference.TYPE_CHAPTER).create();
        assertThat(ref_navigated.toString(), is(equalTo("AAAAA 6:1-6")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("AAAAA")));
        assertThat(ref_navigated.getChapter(), is(equalTo(6)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(6)));

        ref_navigated = ref.previous(Reference.TYPE_BOOK).create();
        assertThat(ref_navigated.toString(), is(equalTo("AAAAA 1:1-6")));
        assertThat(ref_navigated.getBook().getName(), is(equalTo("AAAAA")));
        assertThat(ref_navigated.getChapter(), is(equalTo(1)));
        assertThat(ref_navigated.getFirstVerse(), is(equalTo(1)));
        assertThat(ref_navigated.getVerses().size(), is(equalTo(6)));
    }
}

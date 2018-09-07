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
 * Tests the functionality of the Parser classes, as well as verifying basic integration with Reference.Parser. This
 * focuses on the correct lexing and parsing of String inputs, while ReferenceTest focuses on verifying that a parsed
 * String gets correctly converted into a Reference object. Testing covers public API for Token.class, TokenStream.class,
 * and ReferenceParser.class. Assumes all other involved classes work flawlessly. Doesn't test for concurrency or
 * extenuating circumstances caused by invalid state produced from reflection or anything else.
 */
public class ParserTest {

    @Test
    public void testValidParsing() throws Throwable {
        String[] referencesToParse = new String[] {
                "John 3:16",
                "1 John 3:16",
                "2 John 1:2",
                "3 John 1:2",
                "Philippians 4:11",
                "Eph 1:1-8",
                "Eph. 1:1 through 8",
                "Eph 1:1 to 8",
                "Eph 1:1 and 8",
                "Ecc 4:1, 4",
                "Ecc 4:1 and 4",
                "Gen 1:1-3",
                "Psalm 125",
                "Psalms 125",
                "Galatians 2: 1-5, 19-21, 4-8",
                "1 Timothy 4 5-8",
                "Fo Da Galatia Peopo 4:2",
                "Bel And The Dragon 4:2",
                "1 AA BB CC DD EE FF GG 4:2",
                "Proverbs 7:4",
                "Proverb 7:4",
                "James 2 2"
        };

        Reference.Builder[] referenceChecks = new Reference.Builder[]{
                new Reference.Builder().setBook("John").setChapter(3).setVerses(16),
                new Reference.Builder().setBook("1 John").setChapter(3).setVerses(16),
                new Reference.Builder().setBook("2 John").setChapter(1).setVerses(2),
                new Reference.Builder().setBook("3 John").setChapter(1).setVerses(2),
                new Reference.Builder().setBook("Philippians").setChapter(4).setVerses(11),
                new Reference.Builder().setBook("Eph").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
                new Reference.Builder().setBook("Eph").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
                new Reference.Builder().setBook("Eph").setChapter(1).setVerses(1, 2, 3, 4, 5, 6, 7, 8),
                new Reference.Builder().setBook("Eph").setChapter(1).setVerses(1, 8),
                new Reference.Builder().setBook("Ecc").setChapter(4).setVerses(1, 4),
                new Reference.Builder().setBook("Ecc").setChapter(4).setVerses(1, 4),
                new Reference.Builder().setBook("Gen").setChapter(1).setVerses(1, 2, 3),
                new Reference.Builder().setBook("Psalm").setChapter(125).setVerses(),
                new Reference.Builder().setBook("Psalms").setChapter(125).setVerses(),
                new Reference.Builder().setBook("Galatians").setChapter(2).setVerses(1, 2, 3, 4, 5, 19, 20, 21, 6, 7, 8),
                new Reference.Builder().setBook("1 Timothy").setChapter(4).setVerses(5, 6, 7, 8),
                new Reference.Builder().setBook("Fo Da Galatia Peopo").setChapter(4).setVerses(2),
                new Reference.Builder().setBook("Bel And The Dragon").setChapter(4).setVerses(2),
                new Reference.Builder().setBook("1 AA BB CC DD EE FF GG").setChapter(4).setVerses(2),
                new Reference.Builder().setBook("Proverbs").setChapter(7).setVerses(4),
                new Reference.Builder().setBook("Proverb").setChapter(7).setVerses(4),
                new Reference.Builder().setBook("James").setChapter(2).setVerses(2)
        };

        assertThat(referencesToParse.length, is(equalTo(referenceChecks.length)));

        for(int i = 0; i < referencesToParse.length; i++) {
//            Clog.i("Testing #{$1}", new Object[] {referencesToParse[i]});

            Reference.Builder builder = new Reference.Builder();

            Reference parsedReference = builder.parseReference(referencesToParse[i]).create();
            Reference objectReference = referenceChecks[i].create();

            // Test that the Builder always returns something
            assertThat(parsedReference, is(notNullValue()));
            assertThat(objectReference, is(notNullValue()));

            // Test that parsing finished and was successful
            assertThat(builder.checkFlag(Reference.Builder.PARSED), is(true));
            assertThat(builder.checkFlag(Reference.Builder.PARSE_SUCCESS), is(true));
            assertThat(builder.checkFlag(Reference.Builder.PARSE_FAILURE), is(false));

            // Test that the reference that was parsed is equal to the expected prebuilt reference, and that the
            // equality is symmetric
            assertThat(parsedReference.toString(), is(equalTo(objectReference.toString())));
            assertThat(objectReference.toString(), is(equalTo(parsedReference.toString())));

            assertThat(parsedReference, is(equalTo(objectReference)));
            assertThat(objectReference, is(equalTo(parsedReference)));

            assertThat(parsedReference.equals(objectReference), is(true));
            assertThat(objectReference.equals(parsedReference), is(true));

            assertThat(parsedReference.hashCode(), is(equalTo(objectReference.hashCode())));
            assertThat(objectReference.hashCode(), is(equalTo(parsedReference.hashCode())));

            // ensure that any Reference we print can be read back into the an equivalent object
            String s = parsedReference.toString();
            Reference reparsedReference = new Reference.Builder()
                    .parseReference(s)
                    .create();

            assertThat(reparsedReference.toString(), is(equalTo(objectReference.toString())));
            assertThat(objectReference.toString(),   is(equalTo(reparsedReference.toString())));
        }
    }

    @Test
    public void testReferenceBuilder() throws Throwable {
        // Build a custom Bible so we can test how well it grabs chapters, verses, etc. from a fully-populated Bible

        DummyBible bible = new DummyBible();
        List<DummyBook> books = new ArrayList<>();
        books.add(new DummyBook(1, "AAAAA", 6, 4, 24, 21, 17, 6));
        books.add(new DummyBook(2, "BBBBB", 56, 43, 23));
        books.add(new DummyBook(3, "CCCCC", 99));
        books.add(new DummyBook(4, "DDDDD", 1, 2, 4, 8, 16, 32, 64));
        books.add(new DummyBook(5, "EEEEE", 1, 1, 2, 3, 5, 8, 13, 21));
        bible.setBooks(books);

        Reference.Builder builder = new Reference.Builder();
        builder.setBible(bible);

        // Test that if we only specify the book and chapter, the builder will add all verses in that chapter
        Reference reference = builder.parseReference("AAAAA 3").create();
        assertThat(reference, is(notNullValue()));
        assertThat(reference.getChapter(), is(equalTo(3)));
        assertThat(reference.getBook(), is(notNullValue()));
        assertThat(reference.getBook().numVersesInChapter(3), is(equalTo(24)));
        assertThat(reference.toString(), is(equalTo("AAAAA 3:1-24")));

        // Test that if we only specify the book, the builder will pick the first chapter add all verses in that book
        reference = builder.parseReference("AAAAA").create();
        assertThat(reference, is(notNullValue()));
        assertThat(reference.getChapter(), is(equalTo(1)));
        assertThat(reference.getBook(), is(notNullValue()));
        assertThat(reference.getBook().numVersesInChapter(1), is(equalTo(6)));
        assertThat(reference.toString(), is(equalTo("AAAAA 1:1-6")));

        // Test that it will only add a chapter that is in the book, setting it to the default otherwise, which is
        // 1 if the chapter is out of range below, or the last chapter if it's above
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_CHAPTER_FLAG), is(false));
        builder.setChapter(0);
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_CHAPTER_FLAG), is(true));
        assertThat(builder.getChapter(), is(equalTo(1)));
        builder.setChapter(1);
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_CHAPTER_FLAG), is(false));
        assertThat(builder.getChapter(), is(equalTo(1)));
        builder.setChapter(6);
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_CHAPTER_FLAG), is(false));
        assertThat(builder.getChapter(), is(equalTo(6)));
        builder.setChapter(7);
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_CHAPTER_FLAG), is(true));
        assertThat(builder.getChapter(), is(equalTo(6)));

        // Test that it will only add a chapter that is in the book, setting it to the default otherwise, which is
        // 1 if the chapter is out of range below, or the last chapter if it's above
        builder.setChapter(3);
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_CHAPTER_FLAG), is(false));
        assertThat(builder.getChapter(), is(equalTo(3)));

        builder.setDefaultVerses();
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_VERSES_FLAG), is(true));
        assertThat(builder.getVerses(), is(empty()));

        builder.setDefaultVerses();
        builder.setVerses(1, 2, 3, 4, 5);
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_VERSES_FLAG), is(false));
        assertThat(builder.getVerses(), contains(1, 2, 3, 4, 5));

        builder.setDefaultVerses();
        builder.setVerses(25);
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_VERSES_FLAG), is(true));
        assertThat(builder.getVerses(), contains(24));

        builder.setDefaultVerses();
        builder.setVerses(0);
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_VERSES_FLAG), is(true));
        assertThat(builder.getVerses(), contains(1));

        // Test that it will only add a Book that is in the specified Bible. If it can't find the book, it will create
        // a SimpleBook with a name passed in
        builder.setDefaultChapter();
        builder.setDefaultVerses();

        builder.setBook("AAAAA");
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_BOOK_FLAG), is(false));

        builder.setBook("QQQQQ");
        assertThat(builder.checkFlag(Reference.Builder.DEFAULT_BOOK_FLAG), is(true));
    }
}

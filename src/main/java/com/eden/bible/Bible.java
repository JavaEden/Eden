package com.eden.bible;

import com.eden.utils.TextUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// TODO: decide whether I really want to keep language and languageEnglish
// TODO: decide whether a Bible should have a Metadata object for itself to hold other information
// TODO: give it a getter for the Book type, so that other classes, like Reference.Builder can dynamically create the appropriate Books for this Bible

/**
 * A base class to give a verse the Bible translation or version that it is. A bible Bible should
 * have a full name, an abbreviation (which can be derived from the full name), and a list of Books
 * from which we can determine exactly where in the Bible a verse exists.
 */
public abstract class Bible<T extends Book> implements Comparable<Bible> {
    protected String id;
    protected String name;
    protected String abbreviation;
    protected String language;
    protected String copyright;
    protected List<T> books;

    public Bible() {
        this.books = new ArrayList<>();
    }

    /**
     * Get this Bibles's id.
     *
     * @return this Bibles's id
     */
    public String getId() {
        return id;
    }

    /**
     * Most implementations of Bibles have some concept of an Id. This may be a key needed to retrieve
     * its text from a webservice, or its primary key to look it up in a local databaseRegardless of
     * how it is used, it is simpler to keep this in the base class since it is such a common use case.
     *
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the name of this Bible.
     *
     * @return this Bibles's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this Bible.
     *
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;

        if (TextUtils.isEmpty(this.abbreviation)) {
            this.abbreviation = "";
            for (String word : name.split("\\s")) {
                this.abbreviation += word.charAt(0);
            }
        }
    }

    /**
     * Get the abbreviation of this Bible's name.
     *
     * @return this Bibles's abbreviation
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Set the abbreviation of the name of this Bible.
     *
     * @param abbreviation the abbreviation to set
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * Get the name of the language of this Bible in that language
     *
     * @return this Bibles's language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set the name of the language of this Bible in that language.
     *
     * @param language the language to set
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get the copyright statement for this Bible version.
     *
     * @return this Bible's copyright
     */
    public String getCopyright() {
        return copyright;
    }

    /**
     * Set the copyright statement for this Bible version.
     *
     * @param copyright the copyright to set
     */
    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    /**
     * Get this Bibles's listing of books.
     *
     * @return this Bibles's books
     * @see Book
     */
    public List<T> getBooks() {
        return books;
    }

    /**
     * Set the list of Books in this Bible.
     *
     * @param books the books to set
     * @see Book
     */
    public void setBooks(Collection<T> books) {
        this.books = new ArrayList<>();
        this.books.addAll(books);
    }

    /**
     * Attemps to parse a given String and determine the name of the book. Failing to find it within
     * the specified books, return nothing, so that the user can either create a blank Book to use
     * instead, throw an exception, or anything else.
     *
     * @param bookName the text of the book to attempt to parse
     * @return a Book if the name matches one of the Books in this Bible, null otherwise
     */
    public T parseBook(String bookName) {
        for (T book : books) {
            //check equality of the full book name
            if (bookName.equalsIgnoreCase(book.getName())) {
                return book;
            }
            //check equality of the abbreviation
            else if (bookName.equalsIgnoreCase(book.getAbbreviation())) {
                return book;
            }

            //failing equality, check if we have something close
            else {
                int nameSize = Math.min(bookName.length(), book.getName().length());
                int abbrSize = Math.min(bookName.length(), book.getAbbreviation().length());

                if (bookName.substring(0, nameSize).equalsIgnoreCase(book.getName().substring(0, nameSize))) {
                    return book;
                }
                //check equality of the abbreviation
                else if (bookName.substring(0, abbrSize).equalsIgnoreCase(book.getAbbreviation().substring(0, abbrSize))) {
                    return book;
                }
            }
        }
        return null;
    }

    /**
     * Fetch the Bible's data given it's current state.
     *
     * @return boolean  true if the Bible's data was successfully retrieved, false otherwise
     */
    public boolean get() {
        return true;
    }

    @Override
    public int compareTo(Bible another) {
        return this.getName().compareTo(another.getName());
    }
}

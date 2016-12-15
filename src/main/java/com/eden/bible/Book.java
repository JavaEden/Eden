package com.eden.bible;

import com.eden.utils.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * A base class for a single book in the Bible.
 */
public abstract class Book {
    protected String id;
    protected String name;
    protected String abbreviation;
    protected int location;
    protected List<Integer> chapters;

    public Book() {
        chapters = new ArrayList<>();
    }

    /**
     * Get the id of this Book (i.e. eng-ESV:Gen)
     *
     * @return the id of this Book
     */
    public String getId() {
        return id;
    }

    /**
     * Set the id of this Book (i.e. eng-ESV:Gen)
     *
     * @param id the id of this Book
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Set the name of this Book (i.e. Genesis)
     *
     * @param name the name of this Book
     */
    public void setName(String name) {
        this.name = name;

        if (TextUtils.isEmpty(this.abbreviation)) {
            if (name.length() > 3) {
                this.abbreviation = name.substring(0, 3);
            }
            else {
                this.abbreviation = name;
            }

        }
    }

    /**
     * Get the name of this Book
     *
     * @return the name of this Book
     */
    public String getName() {
        return name;
    }

    /**
     * Set the abbreviation of this Book (i.e. Gen)
     *
     * @param abbreviation the name of this Book
     */
    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * Get the abbreviation of this Book
     *
     * @return the abbreviation of this Book
     */
    public String getAbbreviation() {
        return abbreviation;
    }

    /**
     * Set the location of this Book within the Bible. In a standard Bible, Genesis has location 1
     * and Revelation has location 66, but locations are not strict and are used mostly for sorting
     * books into their canonical order.
     *
     * @param location the location of this Book
     */
    public void setLocation(int location) {
        this.location = location;
    }

    /**
     * Get the location of this Book
     *
     * @return the location of this Book
     */
    public int getLocation() {
        return location;
    }

    /**
     * The array of chapters encodes information about the number of verses in each chapter. The
     * size of the array is the number of chapters in the Book, while the value of each item in the
     * array is the number of verses in that chapter. Internally, this array is 0-indexed, but
     * {@link Book#numVersesInChapter(int)} is 1-indexed to be consistent with a printed Bible.
     *
     * @param chapters the count of verses for every chapter in this Book
     */
    public void setChapters(int... chapters) {
        this.chapters = new ArrayList<>();
        for (int i : chapters) {
            this.chapters.add(i);
        }
    }

    /**
     * The array of chapters encodes information about the number of verses in each chapter. The
     * size of the array is the number of chapters in the Book, while the value of each item in the
     * array is the number of verses in that chapter. Internally, this array is 0-indexed, but
     * {@link Book#numVersesInChapter(int)} is 1-indexed to be consistent with a printed Bible.
     *
     * @param chapters the count of verses for every chapter in this Book
     */
    public void setChapters(List<Integer> chapters) {
        this.chapters = chapters;
    }

    /**
     * Get the chapters of this Book
     *
     * @return the chapters of this Book
     */
    public List<Integer> getChapters() {
        return chapters;
    }

    /**
     * Get the number of chapters in this Book.
     *
     * @return the number of chapters in this Book, or -1 if chapters is null
     */
    public int numChapters() {
        return (chapters != null) ? chapters.size() : -1;
    }

    /**
     * Get the number of verses in the specified chapter of this Book.
     *
     * @param chapter the specified chapter, 1-indexed
     * @return the number of verses in this chapter, or -1 if the given chapter cannot be found
     */
    public int numVersesInChapter(int chapter) {
        if ((chapters != null) &&
                (chapters.size() != 0) &&
                (chapter > 0) &&
                (chapter <= chapters.size())) {
            return chapters.get(chapter - 1);
        }
        else {
            return -1;
        }
    }

    public boolean validateChapter(int chapter) {
        return (chapter >= 1) && (chapter <= numChapters());
    }

    public boolean validateVerseInChapter(int chapter, int verse) {
        return (numVersesInChapter(chapter) >= 1) && (verse >= 1) && (verse <= numVersesInChapter(chapter));
    }

    /**
     * Two Books are considered the equal if they represent the same book in the Bible. In other
     * words, if they have the same location, then the books will be sorted into the same position,
     * and are likely representative of the same book but in a different Bible version. Some versions
     * use different numbering for the verses but are still the same Book, so we are just concerned
     * with the Book's location.
     *
     * @param o the other book to compare to
     * @return whether the two books represent the same Book in the Bible
     */
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Book)) {
            return false;
        }

        Book other = (Book) o;

        if (this.location != other.location) {
            return false;
        }

        return true;
    }

    /**
     * The hashcode is simply the exact location in the Bible this Book exists. In the case of two
     * hashcodes being equal, it is assumed that the Books are the same, even if the books are of
     * different languages.
     *
     * @return the hashcode
     */
    @Override
    public int hashCode() {
        return location;
    }
}

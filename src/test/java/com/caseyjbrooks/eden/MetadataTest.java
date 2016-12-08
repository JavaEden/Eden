package com.caseyjbrooks.eden;

import com.caseyjbrooks.eden.dummy.ComparableClass;
import com.caseyjbrooks.eden.dummy.NonComparableClass;
import com.eden.bible.Metadata;
import com.eden.bible.Reference;
import com.eden.bible.Verse;
import com.eden.simple.SimpleBook;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;


/**
 * Testing covers public API for Metadata.class. Assumes all other involved classes work flawlessly. Doesn't test for
 * concurrency or extenuating circumstances caused by invalid state produced from inherited classes, reflection,
 * or anything else.
 */
public class MetadataTest {

    @Test
    public void testAddingKeys() throws Throwable {
        Metadata m = new Metadata();

        // Test that we can safely put in typed objects
        m.putByte("BYTE", (byte) 1);
        m.putShort("SHORT", (short) 2);
        m.putInt("INT", 3);
        m.putLong("LONG", 4L);
        m.putFloat("FLOAT", 5.0f);
        m.putDouble("DOUBLE", 6.0);
        m.putBoolean("BOOLEAN", true);
        m.putChar("CHAR", 'q');
        m.putString("STRING", "Eden");

        // Test that we can safely put in Comparable objects without using a typed setter
        m.put("COMPARABLE_CLASS", new ComparableClass(7));

        // Test that we will throw an exception if we attempt to add a non-comparable class
        try {
            m.put("NON_COMPARABLE_CLASS", new NonComparableClass(2));
            Assert.fail();
        }
        catch (Exception e) {

        }

        // Test the 'meta' methods of Metadata
        assertThat(m.containsKey("STRING"), is(equalTo(true)));
        assertThat(m.checkType("STRING"), is(equalTo(String.class)));
        assertThat(m.size(), is(equalTo(10)));
        assertThat("CHAR", isIn(m.getKeys()));

        // Test that we can get values out of our metadata in the same class that they went in.
        // In otherwords, test boxing and unboxing works as expected
        assertThat(m.get("BYTE").getClass(),             is(equalTo(Byte.class)));
        assertThat(m.get("SHORT").getClass(),            is(equalTo(Short.class)));
        assertThat(m.get("INT").getClass(),              is(equalTo(Integer.class)));
        assertThat(m.get("LONG").getClass(),             is(equalTo(Long.class)));
        assertThat(m.get("FLOAT").getClass(),            is(equalTo(Float.class)));
        assertThat(m.get("DOUBLE").getClass(),           is(equalTo(Double.class)));
        assertThat(m.get("BOOLEAN").getClass(),          is(equalTo(Boolean.class)));
        assertThat(m.get("CHAR").getClass(),             is(equalTo(Character.class)));
        assertThat(m.get("STRING").getClass(),           is(equalTo(String.class)));
        assertThat(m.get("COMPARABLE_CLASS").getClass(), is(equalTo(ComparableClass.class)));



        // Test that the values we put in are what come back out
        assertThat(m.getByte("BYTE"),       is(equalTo((byte) 1)));
        assertThat(m.getShort("SHORT"),     is(equalTo((short) 2)));
        assertThat(m.getInt("INT"),         is(equalTo(3)));
        assertThat(m.getLong("LONG"),       is(equalTo(4L)));
        assertThat(m.getFloat("FLOAT"),     is(equalTo(5.0f)));
        assertThat(m.getDouble("DOUBLE"),   is(equalTo(6.0)));
        assertThat(m.getBoolean("BOOLEAN"), is(equalTo(true)));
        assertThat(m.getChar("CHAR"),       is(equalTo('q')));
        assertThat(m.getString("STRING"),   is(equalTo("Eden")));
        assertThat(((ComparableClass) m.get("COMPARABLE_CLASS")).value, is(equalTo(7)));

        // Test that we get the appropriate defaults
        assertThat(m.getByte("missing BYTE"),       is(not(equalTo((byte) 1))));
        assertThat(m.getShort("missing SHORT"),     is(not(equalTo((short) 2))));
        assertThat(m.getInt("missing INT"),         is(not(equalTo(3))));
        assertThat(m.getLong("missing LONG"),       is(not(equalTo(4L))));
        assertThat(m.getFloat("missing FLOAT"),     is(not(equalTo(5.0f))));
        assertThat(m.getDouble("missing DOUBLE"),   is(not(equalTo(6.0))));
        assertThat(m.getBoolean("missing BOOLEAN"), is(not(equalTo(true))));
        assertThat(m.getChar("missing CHAR"),       is(not(equalTo('q'))));
        assertThat(m.getString("missing STRING"),   is(not(equalTo("Eden"))));

        assertThat(m.getByte("missing BYTE"),         is(equalTo((byte) 0)));
        assertThat(m.getShort("missing SHORT"),       is(equalTo((short) 0)));
        assertThat(m.getInt("missing INT"),           is(equalTo(0)));
        assertThat(m.getLong("missing LONG"),         is(equalTo(0L)));
        assertThat(m.getFloat("missing FLOAT"),       is(equalTo(0.0f)));
        assertThat(m.getDouble("missing DOUBLE"),     is(equalTo(0.0)));
        assertThat(m.getBoolean("missing BOOLEAN"),   is(equalTo(false)));
        assertThat(m.getChar("missing CHAR"),         is(equalTo(Character.MIN_VALUE)));
        assertThat(m.getString("missing STRING"),     is(equalTo("")));
        assertThat(m.get("missing COMPARABLE_CLASS"), is(nullValue()));

        assertThat(m.getByte("missing BYTE", (byte) 1),    is(equalTo((byte) 1)));
        assertThat(m.getShort("missing SHORT", (short) 2), is(equalTo((short) 2)));
        assertThat(m.getInt("missing INT", 3),             is(equalTo(3)));
        assertThat(m.getLong("missing LONG", 4L),          is(equalTo(4L)));
        assertThat(m.getFloat("missing FLOAT", 5.0f),      is(equalTo(5.0f)));
        assertThat(m.getDouble("missing DOUBLE", 6.0),     is(equalTo(6.0)));
        assertThat(m.getBoolean("missing BOOLEAN", true),  is(equalTo(true)));
        assertThat(m.getChar("missing CHAR", 'q'),         is(equalTo('q')));
        assertThat(m.getString("missing STRING", "Eden"),  is(equalTo("Eden")));
        assertThat(((ComparableClass) m.get("missing COMPARABLE_CLASS", new ComparableClass(7))).value, is(equalTo(7)));

        // Test that typedGet will throw an error if the value at a key does not match the class passed in
        try {
            m.typedGet("STRING", Integer.class, null);
            Assert.fail();
        }
        catch(Exception e) {
        }

        // Test that typedGet will return the default value if the key is missing and it matches the passed in class
        assertThat(m.typedGet("missing STRING", Integer.class, 4),  is(equalTo(4)));
        assertThat(m.typedGet("missing STRING", Integer.class, null),  is(nullValue()));

        // Test that typedGet will throw an error if the key is missing and the default value does not match the passed in class
        try {
            m.typedGet("missing STRING", Integer.class, "Eden");
            Assert.fail();
        }
        catch(Exception e) {
        }
    }

    @Test
    public void testComparator() throws Throwable {
        Verse verseA = new Verse(
                new Reference.Builder()
                        .setBook("Book A")
                        .setChapter(3)
                        .setVerses(16)
                        .create());

        Metadata metadataA = new Metadata();
        metadataA.putByte("BYTE", (byte) 0); //int is comparable
        metadataA.putShort("SHORT", (short) 0); //int is comparable
        metadataA.putInt("INTEGER", 0); //int is comparable
        metadataA.putLong("LONG", 0L); //long is comparable
        metadataA.putFloat("FLOAT", 0.0f); //boolean is comparable
        metadataA.putDouble("DOUBLE", 0.0); //boolean is comparable
        metadataA.putBoolean("BOOLEAN", true); //boolean is comparable
        metadataA.putChar("CHARACTER", Character.MIN_VALUE); //boolean is comparable
        metadataA.putString("STRING", ""); //int is comparable
        metadataA.put("COMPARABLE_CLASS", new ComparableClass(0));
        try {
            //I expect this to throw an exception. If it does, catch it and continue.
            //If if does not, throw something that cannot be caught to indicate an error
            metadataA.put("NON_COMPARABLE_CLASS", new NonComparableClass(5));
            throw new Throwable();
        }
        catch (IllegalArgumentException iae) {

        }
        verseA.setMetadata(metadataA);

        Verse verseB = new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setBook("Book B")
                        .setVerses(17)
                        .create());

        Metadata metadataB = new Metadata();
        metadataB.putByte("BYTE", (byte) 0); //int is comparable
        metadataB.putShort("SHORT", (short) 0); //int is comparable
        metadataB.putInt("INTEGER", 0); //int is comparable
        metadataB.putLong("LONG", 0L); //long is comparable
        metadataB.putFloat("FLOAT", 0.0f); //boolean is comparable
        metadataB.putDouble("DOUBLE", 0.0); //boolean is comparable
        metadataB.putBoolean("BOOLEAN", true); //boolean is comparable
        metadataB.putChar("CHARACTER", Character.MIN_VALUE); //boolean is comparable
        metadataB.putString("STRING", ""); //int is comparable
        metadataB.put("COMPARABLE_CLASS", new ComparableClass(0));
        try {
            //I expect this to throw an exception. If it does, catch it and continue.
            //If if does not, throw something that cannot be caught to indicate an error
            metadataB.put("NON_COMPARABLE_CLASS", new NonComparableClass(5));
            throw new Throwable();
        }
        catch (IllegalArgumentException iae) {

        }
        verseB.setMetadata(metadataB);

        assertThat(new Metadata.Comparator("BYTE").compare(verseA, verseB),             is(equalTo(0)));
        assertThat(new Metadata.Comparator("SHORT").compare(verseA, verseB),            is(equalTo(0)));
        assertThat(new Metadata.Comparator("INTEGER").compare(verseA, verseB),          is(equalTo(0)));
        assertThat(new Metadata.Comparator("LONG").compare(verseA, verseB),             is(equalTo(0)));
        assertThat(new Metadata.Comparator("FLOAT").compare(verseA, verseB),            is(equalTo(0)));
        assertThat(new Metadata.Comparator("DOUBLE").compare(verseA, verseB),           is(equalTo(0)));
        assertThat(new Metadata.Comparator("BOOLEAN").compare(verseA, verseB),          is(equalTo(0)));
        assertThat(new Metadata.Comparator("CHARACTER").compare(verseA, verseB),        is(equalTo(0)));
        assertThat(new Metadata.Comparator("STRING").compare(verseA, verseB),           is(equalTo(0)));
        assertThat(new Metadata.Comparator("COMPARABLE_CLASS").compare(verseA, verseB), is(equalTo(0)));

        Verse verseC = new Verse(
                new Reference.Builder()
                        .setBook("Book A")
                        .setChapter(3)
                        .setVerses(16)
                        .create());

        Metadata metadataC = new Metadata();
        metadataC.putString("BYTE", "");
        metadataC.putString("SHORT", "");
        metadataC.putString("INTEGER", "");
        metadataC.putString("LONG", "");
        metadataC.putString("FLOAT", "");
        metadataC.putString("DOUBLE", "");
        metadataC.putString("BOOLEAN", "");
        metadataC.putString("CHARACTER", "");
        metadataC.putInt("STRING", 1);
        metadataC.putString("COMPARABLE_CLASS", "");
        verseC.setMetadata(metadataC);

        try {
            new Metadata.Comparator("COMPARABLE_CLASS").compare(verseA, null);
            Assert.fail();
        }
        catch (IllegalArgumentException iae) {

        }

        try {
            new Metadata.Comparator("COMPARABLE_CLASS").compare(null, verseB);
            Assert.fail();
        }
        catch (IllegalArgumentException iae) {

        }

        assertThat(new Metadata.Comparator(Metadata.Comparator.KEY_REFERENCE_CANONICAL).compare(verseA, verseB), is(greaterThan(0)));
        assertThat(new Metadata.Comparator(Metadata.Comparator.KEY_REFERENCE_CANONICAL).compare(verseB, verseA), is(lessThan(0)));

        assertThat(new Metadata.Comparator(Metadata.Comparator.KEY_REFERENCE_ALPHABETICAL).compare(verseA, verseB), is(lessThan(0)));
        assertThat(new Metadata.Comparator(Metadata.Comparator.KEY_REFERENCE_ALPHABETICAL).compare(verseB, verseA), is(greaterThan(0)));


        try {
            new Metadata.Comparator("STRING").compare(verseA, verseC);
            Assert.fail();
        }
        catch (ClassCastException cce) {

        }
    }

    @Test
    public void testMultiComparator() {
        //sort items by these 5 criteria in this order
        ArrayList<Metadata.Comparator> comparators = new ArrayList<>();
        comparators.add(new Metadata.Comparator("IS_MENS"));
        comparators.add(new Metadata.Comparator("SIZE"));
        comparators.add(new Metadata.Comparator("COLOR"));
        comparators.add(new Metadata.Comparator("POS"));

        Metadata.MultiComparator multiComparator = new Metadata.MultiComparator(comparators);

        ArrayList<Verse> verses = new ArrayList<>();
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(10).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(9).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(8).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(7).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(6).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(5).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(4).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(3).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(2).create()));
        verses.add(new Verse(
                new Reference.Builder()
                        .setBook(new SimpleBook())
                        .setChapter(1)
                        .setVerses(1).create()));

        verses.get(0).getMetadata().putBoolean("IS_MENS", false);
        verses.get(1).getMetadata().putBoolean("IS_MENS", false);
        verses.get(2).getMetadata().putBoolean("IS_MENS", false);
        verses.get(3).getMetadata().putBoolean("IS_MENS", false);
        verses.get(4).getMetadata().putBoolean("IS_MENS", false);
        verses.get(5).getMetadata().putBoolean("IS_MENS", false);
        verses.get(6).getMetadata().putBoolean("IS_MENS", true);
        verses.get(7).getMetadata().putBoolean("IS_MENS", true);
        verses.get(8).getMetadata().putBoolean("IS_MENS", true);
        verses.get(9).getMetadata().putBoolean("IS_MENS", true);

        verses.get(0).getMetadata().putInt("SIZE", 0);
        verses.get(1).getMetadata().putInt("SIZE", 0);
        verses.get(2).getMetadata().putInt("SIZE", 0);
        verses.get(6).getMetadata().putInt("SIZE", 0);
        verses.get(7).getMetadata().putInt("SIZE", 0);
        verses.get(3).getMetadata().putInt("SIZE", 1);
        verses.get(4).getMetadata().putInt("SIZE", 1);
        verses.get(5).getMetadata().putInt("SIZE", 1);
        verses.get(8).getMetadata().putInt("SIZE", 1);
        verses.get(9).getMetadata().putInt("SIZE", 1);

        verses.get(1).getMetadata().putString("COLOR", "blue");
        verses.get(3).getMetadata().putString("COLOR", "blue");
        verses.get(5).getMetadata().putString("COLOR", "blue");
        verses.get(7).getMetadata().putString("COLOR", "blue");
        verses.get(9).getMetadata().putString("COLOR", "blue");
        verses.get(0).getMetadata().putString("COLOR", "red");
        verses.get(2).getMetadata().putString("COLOR", "red");
        verses.get(4).getMetadata().putString("COLOR", "red");
        verses.get(6).getMetadata().putString("COLOR", "red");
        verses.get(8).getMetadata().putString("COLOR", "red");

        verses.get(0).getMetadata().putInt("POS", 0);
        verses.get(1).getMetadata().putInt("POS", 1);
        verses.get(2).getMetadata().putInt("POS", 2);
        verses.get(3).getMetadata().putInt("POS", 3);
        verses.get(4).getMetadata().putInt("POS", 4);
        verses.get(5).getMetadata().putInt("POS", 5);
        verses.get(6).getMetadata().putInt("POS", 6);
        verses.get(7).getMetadata().putInt("POS", 7);
        verses.get(8).getMetadata().putInt("POS", 8);
        verses.get(9).getMetadata().putInt("POS", 9);

        //start by sorting into position
        Collections.sort(verses, new Metadata.Comparator("POS"));
        //sort according to whether item IS MENS or not
        Collections.sort(verses, new Metadata.Comparator("IS_MENS"));
        String simpleSort1 = "";
        for (int i = 0; i < verses.size(); i++) {
            simpleSort1 += verses.get(i).getMetadata().getInt("POS");
        }
        assertThat(simpleSort1, is(equalTo("0123456789")));

        //start by sorting into position
        Collections.sort(verses, new Metadata.Comparator("POS"));
        //sort by SIZE
        Collections.sort(verses, new Metadata.Comparator("SIZE"));
        String simpleSort2 = "";
        for (int i = 0; i < verses.size(); i++) {
            simpleSort2 += verses.get(i).getMetadata().getInt("POS");
        }
        assertThat(simpleSort2, is(equalTo("0126734589")));

        //start by sorting into position
        Collections.sort(verses, new Metadata.Comparator("POS"));
        //sort by COLOR
        Collections.sort(verses, new Metadata.Comparator("COLOR"));
        String simpleSort3 = "";
        for (int i = 0; i < verses.size(); i++) {
            simpleSort3 += verses.get(i).getMetadata().getInt("POS");
        }
        assertThat(simpleSort3, is(equalTo("1357902468")));

        //start by sorting into position
        Collections.sort(verses, new Metadata.Comparator("POS"));
        //sort by reference
        Collections.sort(verses, new Metadata.Comparator(Metadata.Comparator.KEY_REFERENCE_CANONICAL));
        String simpleSort4 = "";
        for (int i = 0; i < verses.size(); i++) {
            simpleSort4 += verses.get(i).getMetadata().getInt("POS");
        }
        assertThat(simpleSort4, is(equalTo("9876543210")));

        //start by sorting into position
        Collections.sort(verses, new Metadata.Comparator("POS"));
        //sort using multicomparator
        Collections.sort(verses, multiComparator);
        String multiSort = "";
        for (int i = 0; i < verses.size(); i++) {
            multiSort += verses.get(i).getMetadata().getInt("POS");
        }
        assertThat(multiSort, is(equalTo("1023547698")));
    }
}


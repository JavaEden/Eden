package com.eden.defaults;

import com.eden.bible.AbstractVerse;
import com.eden.interfaces.VerseFormatter;

import java.util.Random;

/**
 * VerseFormatter is a class that allows for the customization of how verses are printed to the screen.
 * DefaultVerseFormatter provides several common options for formatting verses. All will show the text of
 * a verse without verse numbers, without new lines after verses, and without including the text's
 * reference. Variations of the default can display just the first letters of each word, replace all
 * words with dashed lines, do a combination of both first letters and dashed lines, or randomly
 * replace words with dashes given a threshold between 0 and 1.
 */
// TODO: Remove this formatter and put it with the Scripture Now! app. It does not belong in the core library
public class DefaultVerseFormatter implements VerseFormatter {
    protected AbstractVerse verse;

//Set up default interface values
//--------------------------------------------------------------------------------------------------
    @Override
    public String onPreFormat(AbstractVerse verse) {
        this.verse = verse;
        return "";
    }

    @Override
    public String onFormatVerseStart(int verseNumber) {
        return " ";
    }

    @Override
    public String onFormatText(String verseText) {
        return verseText;
    }

    @Override
    public String onFormatVerseEnd() {
        return "";
    }

    @Override
    public String onPostFormat() {
        return "";
    }


//setup special cases of the default
//--------------------------------------------------------------------------------------------------

    /**
     * Print verse text without any changes
     */
    public static class Normal extends DefaultVerseFormatter {
        @Override
        public String onFormatText(String verseText) {
            return verseText;
        }
    }

    /**
     * Replace all words with underscores, to give a visual representation of how long each word is.
     * Preserves punctuation.
     */
    public static class Dashes extends DefaultVerseFormatter {
        @Override
        public String onFormatText(String verseText) {
            return verseText.replaceAll("\\w", "_") + " ";
        }
    }

    /**
     * Replace all words with the its first letter, to give a hint at what each word is, but not its
     * length. Preserves punctuation, but all letters are shown as upper case.
     */
    public static class FirstLetters extends DefaultVerseFormatter {
        @Override
        public String onFormatText(String verseText) {
            return verseText.toUpperCase().replaceAll("(\\w)(\\w*)", "$1 ") + " ";
        }
    }

    /**
     * Replace all words with underscores but keeps the first letter. Preserves punctuation, but all
     * letters are shown as upper case.
     */
    public static class DashedLetter extends DefaultVerseFormatter {
        @Override
        public String onFormatText(String verseText) {
            return verseText.toUpperCase().replaceAll("(\\B\\w)", "_") + " ";
        }
    }

    /**
     * Randomly replaces words with dashes. Randomizer is seeded with the hashcode of the reference
     * to ensure consistent randomization for equivalent verses, but also unique reference patterns
     * for each verse.
     */
    public static class RandomWords extends DefaultVerseFormatter {
        //the percent of words to randomly be removed
        float level;
        int seedOffset;
        Random randomizer;

        public RandomWords(float level) {
            this.level = level;
            this.seedOffset = 0;
        }

        public RandomWords(float level, int seedOffset) {
            this.level = level;
            this.seedOffset = seedOffset;
        }

        @Override
        public String onFormatText(String verseText) {
            randomizer = new Random(verse.getReference().hashCode() + seedOffset);

            String text = "";
            String[] words = verseText.split("\\s");

            for (String word : words) {
                float randomValue = randomizer.nextFloat();
                if (randomValue > level) {
                    text += word.replaceAll("\\w", "_") + " ";
                } else {
                    text += word + " ";
                }
            }

            return text;
        }
    }
}

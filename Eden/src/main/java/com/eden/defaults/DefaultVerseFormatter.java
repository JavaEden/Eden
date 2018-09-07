package com.eden.defaults;

import com.eden.bible.AbstractVerse;
import com.eden.interfaces.VerseFormatter;

import java.util.Random;

/**
 * VerseFormatter is a class that allows for the customization of how verses are printed to the screen.
 * DefaultVerseFormatter provides several common options for formatting verses. All will show the text of
 * a verse without verse numbers, without new lines after verses, and without including the text's
 * reference. The basic formatter includes support for randomly removing words from the final text, but by
 * this is not implemented in the base class. It is designed so that any removal strategy can be easily added
 * following the same removal method, so that the same words will be removed, but can just be showed in its
 * own custom way. Variations of the default can randomly display just the first letters of each word,
 * replace all words with dashed lines, do a combination of both first letters and dashed lines.
 */
// TODO: Remove this formatter and put it with the Scripture Now! app. It does not belong in the core library
public class DefaultVerseFormatter implements VerseFormatter {
    protected AbstractVerse verse;

    protected float level;
    protected int seedOffset;
    protected Random randomizer;

    public DefaultVerseFormatter() {
        this.level = 1.0f;
        this.seedOffset = 0;
    }

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
        randomizer = new Random(verse.getReference().hashCode() + seedOffset);

        String text = "";
        String[] words = verseText.split("\\s");

        for (String word : words) {
            float randomValue = randomizer.nextFloat();
            if (randomValue > level) {
                text += transformWord(word) + " ";
            }
            else {
                text += word + " ";
            }
        }

        return text;
    }

    @Override
    public String onFormatVerseEnd() {
        return "";
    }

    @Override
    public String onPostFormat() {
        return "";
    }

    public String transformWord(String word) {
        return word;
    }


//setup special cases of the default
//--------------------------------------------------------------------------------------------------

    public static class Dashes extends DefaultVerseFormatter {
        public Dashes(float level) {
            this.level = level;
            this.seedOffset = 0;
        }

        public Dashes(float level, int seedOffset) {
            this.level = level;
            this.seedOffset = seedOffset;
        }

        @Override
        public String transformWord(String word) {
            return word.replaceAll("\\w", "_");
        }
    }

    public static class FirstLetters extends DefaultVerseFormatter {
        public FirstLetters(float level) {
            this.level = level;
            this.seedOffset = 0;
        }

        public FirstLetters(float level, int seedOffset) {
            this.level = level;
            this.seedOffset = seedOffset;
        }

        @Override
        public String transformWord(String word) {
            return word.toUpperCase().replaceAll("(\\w)(\\w*)", "$1");
        }
    }

    public static class DashedLetters extends DefaultVerseFormatter {
        public DashedLetters(float level) {
            this.level = level;
            this.seedOffset = 0;
        }

        public DashedLetters(float level, int seedOffset) {
            this.level = level;
            this.seedOffset = seedOffset;
        }

        @Override
        public String transformWord(String word) {
            return word.toUpperCase().replaceAll("(\\B\\w)", "_") + " ";
        }
    }
}

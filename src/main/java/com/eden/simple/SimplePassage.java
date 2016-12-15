package com.eden.simple;

import com.eden.bible.Passage;
import com.eden.bible.Reference;

public class SimplePassage extends Passage {
    String text;

    public SimplePassage(Reference reference) {
        super(reference);
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getRawText() {
        return text;
    }

    @Override
    public String getText() {
        String text = "";

        text += verseFormatter.onPreFormat(this);
        text += verseFormatter.onFormatVerseStart(reference.getVerses().get(0));
        text += verseFormatter.onFormatText(this.text);
        text += verseFormatter.onPostFormat();

        return text.trim();
    }
}

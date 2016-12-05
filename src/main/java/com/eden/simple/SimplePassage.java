package com.eden.simple;

import com.eden.bible.Passage;
import com.eden.bible.Reference;
import com.eden.bible.Verse;

public class SimplePassage extends Passage {
	String text;

	public SimplePassage(Reference reference) {
		super(reference);
	}

	@Override
	public Class<? extends Verse> getVerseClass() {
		return SimpleVerse.class;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public String getFormattedText() {
		String text = "";

		text += verseFormatter.onPreFormat(this);
		text += verseFormatter.onFormatVerseStart(reference.getVerses().get(0));
		text += verseFormatter.onFormatText(this.text);
		text += verseFormatter.onPostFormat();

		return text.trim();
	}
}
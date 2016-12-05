package com.eden.simple;

import com.eden.bible.Book;

public class SimpleBook extends Book {

	@Override
	public void setName(String name) {
		super.setName(name);
		if(name.length() > 3)
			setAbbreviation(name.substring(0, 3));
		else
			setAbbreviation(name);
	}

	@Override
	public boolean validateChapter(int chapter) {
		return true;
	}

	@Override
	public boolean validateVerseInChapter(int chapter, int verse) {
		return true;
	}
}

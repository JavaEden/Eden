package com.eden.simple;

import com.eden.bible.Book;

public class SimpleBook extends Book {

	@Override
	public boolean validateChapter(int chapter) {
		return true;
	}

	@Override
	public boolean validateVerseInChapter(int chapter, int verse) {
		return true;
	}
}

package com.eden.simple;

import com.eden.bible.Bible;

public class SimpleBible extends Bible<SimpleBook> {
	@Override
	public void setName(String name) {
		super.setName(name);

		String abbr = "";
		for(String word : name.split("\\s")) {
			abbr += word.charAt(0);
		}
		setAbbreviation(abbr);
	}

	@Override
	public SimpleBook parseBook(String bookName) {
		SimpleBook book = new SimpleBook();
		book.setName(bookName);
		return book;
	}
}

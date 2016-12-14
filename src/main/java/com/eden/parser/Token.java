package com.eden.parser;

public class Token {
    public enum Type {
        WORD,
        NUMBER,
        COLON,
        SEMICOLON,
        COMMA,
        DASH,
        SLASH,
        BACKSLASH,
        DOT,
        WHITESPACE,
    }

    private Type type;
    private String stringValue;
    private int intValue;

    Token(Type type) {
        this.type = type;
    }

    Token(Type type, String value) {
        this.type = type;
        this.stringValue = value;
    }

    Token(Type type, int value) {
        this.type = type;
        this.intValue = value;
    }

    String getStringValue() { return stringValue; }

    int getIntValue() { return intValue; }

    public boolean equals(Type type) { return this.type == type; }

    boolean isPunctuation() {
        return (isPunctuationCharacter() || isPunctuationWord());
    }

    boolean isPunctuationCharacter() {
        return this.equals(Token.Type.COLON) ||
            this.equals(Token.Type.SEMICOLON) ||
            this.equals(Token.Type.COMMA) ||
            this.equals(Token.Type.DOT) ||
            this.equals(Token.Type.DASH) ||
            this.equals(Token.Type.SLASH) ||
            this.equals(Token.Type.BACKSLASH);
    }

    boolean isPunctuationWord() {
        return this.equals(Token.Type.WORD) && getTokenFromWord(getStringValue()).isPunctuationCharacter();
    }

    static Token getTokenFromWord(String word) {

        if(word != null) {
            switch(word.toLowerCase()) {
                case "and": return new Token(Type.COMMA);
                case "through": return new Token(Type.DASH);
                case "to": return new Token(Type.DASH);
            }
        }

        return new Token(Type.WORD, word);
    }
}

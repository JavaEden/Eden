package com.eden.interfaces;

public interface TypedKeyValueStore extends KeyValueStore {

    void putByte(String key, byte value);
    byte getByte(String key, byte defValue);
    byte getByte(String key);

    void putShort(String key, short value);
    short getShort(String key, short defValue);
    short getShort(String key);

    int getInt(String key, int defValue);

    void putLong(String key, long value);
    long getLong(String key, long defValue);
    long getLong(String key);

    void putFloat(String key, float value);
    float getFloat(String key, float defValue);
    float getFloat(String key);

    double getDouble(String key, double defValue);

    void putBoolean(String key, boolean value);
    boolean getBoolean(String key, boolean defValue);
    boolean getBoolean(String key);

    void putChar(String key, char value);
    char getChar(String key, char defValue);
    char getChar(String key);

    String getString(String key, String defValue);
}

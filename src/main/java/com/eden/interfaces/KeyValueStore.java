package com.eden.interfaces;

public interface KeyValueStore {
    boolean containsKey(String key);

    void put(String key, Object value);
    Object get(String key);

    void putInt(String key, int value);
    int getInt(String key);

    void putDouble(String key, double value);
    double getDouble(String key);

    void putString(String key, String value);
    String getString(String key);
}

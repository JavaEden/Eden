package com.eden.bible;

import com.caseyjbrooks.clog.Clog;
import com.eden.interfaces.ExtendedKeyValueStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Metadata is a wrapper around a HashMap that maps Strings to {@link Comparable} Object types,
 * and has convenience methods for several common datatypes: int, long, boolean, and String. It is
 * designed to give users the ability to map additional properties to verses which can be used to sort
 * lists of verses. This map cannot make any assumptions about the objects it contains, and so will not
 * be serialized along with verses.
 * <p>
 * Because verses should be sortable by any key in the Metadata, all Objects in the map must
 * implement the Comparable interface, or be added with an appropriate Comparator.
 * <p>
 * Metadata is generally typesafe, but should not be relied upon too heavily. Any of the convenience
 * methods for getting an Object out of the Metadata will throw a {@link ClassCastException} if the
 * object at that key does not match the return type. In addition, sorting pairs of Metadata by a
 * common key requires the classes of the Objects at that key are of the exact same class, and anything
 * other than an exact match, including any derived classes, will also throw a ClassCastException.
 */
public final class Metadata implements ExtendedKeyValueStore {
    private Map<String, Object> items;

    /**
     * Create a new, empty map of String to Comparable Objects.
     */
    public Metadata() {
        items = new HashMap<>();
    }

    /**
     * Check if this Metadata contains the given key.
     *
     * @param key the key to check
     * @return true if the key exists in the map, false otherwise
     */
    public boolean containsKey(String key) {
        return items.containsKey(key);
    }

    /**
     * Get the Class corresponding to a particular key.
     *
     * @param key the key to check the type of
     * @return the Class corresponding to the object at that key, if it exists, otherwise null
     */
    public Class checkType(String key) {
        return (containsKey(key)) ? items.get(key).getClass() : null;
    }

    /**
     * Get the number of items in this map
     *
     * @return the number of items
     */
    public int size() {
        return items.size();
    }

    /**
     * Get the set of keys contained in this map.
     *
     * @return the keys in this map
     */
    public Set<String> getKeys() {
        return items.keySet();
    }

    /**
     * Put an arbitrary Object into the map at the given key. The Object added must be a Comparable
     * type.
     *
     * @param key   the key
     * @param value the Comparable object
     * @throws IllegalArgumentException if value does not implement Comparable interface
     */
    public void put(String key, Object value) {
        if (value instanceof Comparable) {
            items.put(key, value);
        }
        else {
            throw new IllegalArgumentException(
                    Clog.format("Objects must implement #{$1}. [#{$2}] does not name a Comparable type",
                            Comparable.class.getName(),
                            value.getClass().getName()
                    )
            );
        }
    }

    /**
     * Get the Object from the map at the given key. Since items can only be added when they are
     * Comparable, there is no need to check that condition here.
     *
     * @param key the key
     */
    public Object get(String key, Object defValue) {
        if (items.containsKey(key)) {
            return items.get(key);
        }
        return defValue;
    }

    /**
     * Get the Object from the map at the given key. Since items can only be added when they are
     * Comparable, there is no need to check that condition here.
     *
     * @param key the key
     */
    public Object get(String key) {
        return get(key, null);
    }

    @Override
    public void putByte(String key, byte value) { put(key, Byte.valueOf(value)); }

    @Override
    public byte getByte(String key, byte defValue) { return (byte) typedGet(key, Byte.class, defValue); }

    @Override
    public byte getByte(String key) { return getByte(key, Byte.valueOf((byte) 0)); }

    @Override
    public void putShort(String key, short value) { put(key, Short.valueOf(value)); }

    @Override
    public short getShort(String key, short defValue) { return (short) typedGet(key, Short.class, defValue); }

    @Override
    public short getShort(String key) { return getShort(key, Short.valueOf((short) 0)); }

    @Override
    public void putInt(String key, int value) { put(key, Integer.valueOf(value)); }

    @Override
    public int getInt(String key, int defValue) { return (int) typedGet(key, Integer.class, defValue); }

    @Override
    public int getInt(String key) { return getInt(key, Integer.valueOf(0)); }

    @Override
    public void putLong(String key, long value) { put(key, Long.valueOf(value)); }

    @Override
    public long getLong(String key, long defValue) { return (long) typedGet(key, Long.class, defValue); }

    @Override
    public long getLong(String key) { return getLong(key, Long.valueOf(0)); }

    @Override
    public void putFloat(String key, float value) { put(key, Float.valueOf(value)); }

    @Override
    public float getFloat(String key, float defValue) { return (float) typedGet(key, Float.class, defValue); }

    @Override
    public float getFloat(String key) { return getFloat(key, Float.valueOf(0)); }

    @Override
    public void putDouble(String key, double value) { put(key, Double.valueOf(value)); }

    @Override
    public double getDouble(String key, double defValue) { return (double) typedGet(key, Double.class, defValue); }

    @Override
    public double getDouble(String key) { return getDouble(key, Double.valueOf(0)); }

    @Override
    public void putBoolean(String key, boolean value) { put(key, Boolean.valueOf(value)); }

    @Override
    public boolean getBoolean(String key, boolean defValue) { return (boolean) typedGet(key, Boolean.class, defValue); }

    @Override
    public boolean getBoolean(String key) { return getBoolean(key, Boolean.valueOf(false)); }

    @Override
    public void putChar(String key, char value) { put(key, Character.valueOf(value)); }

    @Override
    public char getChar(String key, char defValue) { return (char) typedGet(key, Character.class, defValue); }

    @Override
    public char getChar(String key) { return getChar(key, Character.MIN_VALUE); }

    @Override
    public void putString(String key, String value) { put(key, value); }

    @Override
    public String getString(String key, String defValue) { return (String) typedGet(key, String.class, defValue); }

    @Override
    public String getString(String key) { return getString(key, ""); }

    /**
     * Get an object at a key and enforce its Class
     *
     * @param key         the key of the object in the map
     * @param targetClass the class we want to ensure an object conforms to
     * @param defValue    the default value to return if the key does not exist in the map.
     * @return
     */
    public Object typedGet(String key, Class<?> targetClass, Object defValue) {
        if (items.containsKey(key)) {
            Object item = items.get(key);
            if (!targetClass.isAssignableFrom(item.getClass())) {
                throw new ClassCastException(Clog.format("Key [#{$1}] expected result of type [#{$2}], found [#{$3}]",
                        key,
                        targetClass.getName(),
                        item.getClass().toString()
                ));
            }
            else {
                return targetClass.cast(item);
            }
        }
        else {
            if (defValue != null) {
                if (!targetClass.isAssignableFrom(defValue.getClass())) {
                    throw new ClassCastException(Clog.format("Default value expected result of type [#{$1}], found [#{$2}]",
                            targetClass.getName(),
                            defValue.getClass().toString()
                    ));
                }
                else {
                    return targetClass.cast(defValue);
                }
            }
        }
        return null;
    }


    /**
     * The class responsible for sorting AbstractVerses by their Metadata.
     */
    public static final class Comparator implements java.util.Comparator<AbstractVerse> {
        public static String KEY_REFERENCE_CANONICAL = "KEY_REF_CANONICAL";
        public static String KEY_REFERENCE_ALPHABETICAL = "KEY_REFERENCE_ALPHABETICAL";
        private String key;

        /**
         * Initialize this Comparator with the key of the object in Metadata to sort by.
         *
         * @param key the key
         */
        public Comparator(String key) {
            this.key = key;
        }

        @Override
        public int compare(AbstractVerse a, AbstractVerse b) {
            if (a == null || b == null) {
                throw new IllegalArgumentException("Both objects to compare must be non-null");
            }

            if (key.equals(KEY_REFERENCE_CANONICAL)) {
                return a.getReference().compareTo(b.getReference());
            }
            else if (key.equals(KEY_REFERENCE_ALPHABETICAL)) {
                return a.getReference().toString().compareTo(b.getReference().toString());
            }
            else {
                Object lhs = a.getMetadata().get(key);
                Object rhs = b.getMetadata().get(key);
                if (lhs == null || rhs == null) {
                    throw new NullPointerException(
                            "One or more objects at the given key are null"
                    );
                }
                else if (lhs.getClass().equals(rhs.getClass())) {
                    try {
                        Comparable lhs_c = (Comparable) lhs;
                        Comparable rhs_c = (Comparable) rhs;

                        return lhs_c.compareTo(rhs_c);
                    }
                    catch (ClassCastException e) {
                        throw new ClassCastException(
                                "Object at [" + key + "] of type [" + lhs.getClass().toString() +
                                        " does not name a Comparable type."
                        );
                    }
                }
                else {
                    throw new ClassCastException(
                            "Objects are not of the same Class: " +
                                    lhs.getClass().toString() + " " +
                                    rhs.getClass().toString()
                    );
                }
            }
        }
    }

    /**
     * Sort AbstractVerses according to multiple criteria. In the event that all metadata values
     * in these comparators match, sort by canonical order
     */
    public static class MultiComparator implements java.util.Comparator<AbstractVerse> {
        ArrayList<Comparator> comparisonCriteria;

        public MultiComparator(ArrayList<Comparator> comparisonCriteria) {
            this.comparisonCriteria = comparisonCriteria;
        }

        @Override
        public int compare(AbstractVerse lhs, AbstractVerse rhs) {
            for (Comparator comparator : comparisonCriteria) {
                int comparison = comparator.compare(lhs, rhs);
                if (comparison != 0) {
                    return comparison;
                }
            }
            return lhs.getReference().compareTo(rhs.getReference());
        }
    }
}

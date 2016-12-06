package com.eden.utils;

import com.eden.bible.Verse;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ReferenceExclusionStrategy implements ExclusionStrategy {

    public ReferenceExclusionStrategy() {
    }

    // This method is called for all fields. if the method returns false the
    // field is excluded from serialization
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getName().equals("reference"));
    }

    // This method is called for all classes. If the method returns false the
    // class is excluded.
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return (clazz.equals(Verse.class));
    }

}
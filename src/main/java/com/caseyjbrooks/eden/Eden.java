package com.caseyjbrooks.eden;

import com.caseyjbrooks.eden.bible.Metadata;
import com.caseyjbrooks.eden.bible.Reference;
import com.google.gson.GsonBuilder;

public class Eden {
    private static Eden instance;

    private Metadata metadata;

    public static Eden getInstance() {
        if(instance == null) {
            instance = new Eden();
        }

        return instance;
    }

    private Eden() {
        this.metadata = new Metadata();
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public GsonBuilder getSerializer() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(Reference.class, new Reference.ReferenceJsonizer());
    }

    public GsonBuilder getDeserializer() {
        return new GsonBuilder()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .serializeNulls();
    }
}

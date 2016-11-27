package com.caseyjbrooks.eden;

import com.caseyjbrooks.eden.bible.Metadata;

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
}

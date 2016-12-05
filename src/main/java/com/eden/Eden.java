package com.eden;

import com.eden.bible.Metadata;
import com.eden.bible.Reference;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Eden is a singleton representing the global state of an entire application. An Eden application is considered to be
 * an application which provides any kind of mechanism for viewing Bible verses and/or Bible metadata. It is expected
 * that a common Eden app would need to get data from multiple sources, such as looking up Verses from one or more
 * remote APIs, but also letting users store individual verse lists.
 *
 * The Eden instance represents an injection container for all global app data to be shared, and also for registering
 * all the available EdenRepository instances to be used within an app. New functionality can be added to an Eden app
 * by extending the EdenRepository class and
 */
public final class Eden {
    private static Eden instance;

    private Metadata metadata;

    private GsonBuilder serializer;
    private GsonBuilder deserializer;

    private Map<String, EdenRepository> repositories;

    public static Eden getInstance() {
        if(instance == null) {
            instance = new Eden();
        }

        return instance;
    }

    private Eden() {
        this.metadata = new Metadata();
        this.repositories = new HashMap<>();

        this.serializer = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .serializeNulls()
            .registerTypeAdapter(Reference.class, new Reference.ReferenceJsonizer());

        this.deserializer = new GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .serializeNulls();
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String get(String key) {
        return metadata.getString(key, "");
    }

    public void put(String key, String value) {
        metadata.putString(key, value);
    }

    public GsonBuilder getSerializer() {
        return serializer;
    }

    public void setSerializer(GsonBuilder serializer) {
        this.serializer = serializer;
    }

    public GsonBuilder getDeserializer() {
        return deserializer;
    }

    public void setDeserializer(GsonBuilder deserializer) {
        this.deserializer = deserializer;
    }

    /**
     * Register a new EdenRepository
     *
     * @param repository  the repository instance
     */
    public void registerRepository(EdenRepository repository) {
        this.repositories.put(repository.getClass().getName(), repository);
    }

    public void registerRepository(EdenRepository repository, String alias) {
        this.repositories.put(repository.getClass().getName(), repository);
        this.repositories.put(alias, repository);
    }

    public EdenRepository getRepository(Class<? extends EdenRepository> respositoryClass) {
        return this.repositories.getOrDefault(respositoryClass.getName(), null);
    }

    public EdenRepository getRepository(String repositoryAlias) {
        return this.repositories.getOrDefault(repositoryAlias, null);
    }
}

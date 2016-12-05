package com.eden;

import com.eden.annotations.AnnotationProcessor;
import com.eden.bible.Metadata;
import com.eden.bible.Reference;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Eden is a singleton representing the global state of an entire application and all registered sources of Bible verse
 * data. An Eden application is considered to be an application which provides any kind of mechanism for viewing Bible
 * verses and/or Bible metadata. It is expected that a common Eden app would need to get data from multiple sources,
 * such as looking up Verses from one or more remote APIs, but also letting users store individual verse lists in a
 * database.
 *
 * The Eden instance represents an injection container for all global app data to be shared, and also for registering
 * all the available EdenRepository instances to be used within an app. New functionality can be added to an Eden app
 * by extending the EdenRepository class and adding it to the Eden instance with .registerRepository(). The purpose of
 * keeping all Repositories in one Eden instance is so that any client that need to request data from a Bible source has
 * access to all the data needed to retrieve that data in one location. All API keys, preferences for Bible versions,
 * etc. are all stored within this one location and then either requested or injected wherever needed.
 *
 * Eden supports dependency injection (DI) with the use of the @EdenBibleList, @EdenBible, and @EdenPassage annotations.
 * To inject Eden properties into an object, annotate the injectable fields with the above annotation and call
 * Eden.inject(object); in your initialization code, where 'object' is the Object that your Eden properties should be
 * injected into. This will bootstrap the Eden DI framework, finding or creating the objects, then injecting them into
 * 'object'. Note that some objects, like Bibles and BibleLists, are created and injected as singletons, so modifying
 * the singleton affects the entire app.
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

    /**
     * Register a new EdenRepository and give it an alias. It can then be recalled by either by alias or the
     * fully-qualified class name.
     *
     * @param repository  the repository instance to register
     * @param alias  the alias
     */
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

    public void inject(Object object) {
        AnnotationProcessor.getInstance().processAnnotations(object);
    }
}

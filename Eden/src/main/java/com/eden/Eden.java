package com.eden;

import com.eden.bible.Metadata;
import com.eden.bible.Reference;
import com.eden.injection.EdenInjector;
import com.eden.injection.annotations.EdenBibleDefinition;
import com.eden.injection.annotations.EdenBibleListDefinition;
import com.eden.interfaces.KeyValueStore;
import com.eden.repositories.EdenRepository;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Eden is a singleton representing the global state of an entire application and all registered sources of Bible verse
 * data. An Eden application is considered to be an application which provides any kind of mechanism for viewing Bible
 * verses and/or Bible metadata. It is expected that a common Eden app would need to get data from multiple sources,
 * such as looking up Verses from one or more remote APIs, but also letting users store individual verse lists in a
 * database.
 * <p>
 * The Eden instance represents an injection container for all global app data to be shared, and also for registering
 * all the available EdenRepository instances to be used within an app. New functionality can be added to an Eden app
 * by extending the EdenRepository class and adding it to the Eden instance with .registerRepository(). The purpose of
 * keeping all Repositories in one Eden instance is so that any client that need to request data from a Bible source has
 * access to all the data needed to retrieve that data in one location. All API keys, preferences for Bible versions,
 * etc. are all stored within this one location and then either requested or injected wherever needed.
 * <p>
 * Eden supports dependency injection (DI) with the use of the @EdenBibleList, @EdenBible, and @EdenPassage annotations.
 * To inject Eden properties into an object, annotate the injectable fields with the above annotation and call
 * Eden.inject(object); in your initialization code, where 'object' is the Object that your Eden properties should be
 * injected into. This will bootstrap the Eden DI framework, finding or creating the objects, then injecting them into
 * 'object'. Note that some objects, like Bibles and BibleLists, are created and injected as singletons, so modifying
 * the singleton affects the entire app.
 */

// TODO: Decide if the core library should rely exclusively on GSON or make generic wrappers around JSON parsers
// TODO: ^ if I force GSON, I could make it easier to standardize the JSON api of this library, but it discourages third party integration
// TODO: ^ if I allow other JSON parsers, I could do it like Spring and write wrappers for all the common parsers and includes them in this core lib, making them all conform to the same specificiations
// TODO: Replace Metadata with a persistent key-value storage interface. Metadata should be for sorting verses, not persistence
// TODO: Ensure consistent naming conventions throughout all classes
// TODO: Ensure all classes have defined '.equals', '.hashcode', and 'Comparator' methods
// TODO: Ensure any usage of List, Map, etc. are all exposed with the generic interface and not any explicit
public final class Eden {
    private static Eden instance;

    private KeyValueStore config;

    private GsonBuilder serializer;
    private GsonBuilder deserializer;

    private Map<String, EdenRepository> repositories;
    private final EdenInjector edenInjector;

    ExecutorService executorService;

    public static Eden getInstance() {
        if (instance == null) {
            instance = new Eden();
        }

        return instance;
    }

    private Eden() {
        this.config = new Metadata();
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

        edenInjector = new EdenInjector();
        edenInjector.addAnnotation(new EdenBibleDefinition());
        edenInjector.addAnnotation(new EdenBibleListDefinition());

        executorService = Executors.newFixedThreadPool(4);
    }

    public KeyValueStore config() {
        return config;
    }

    public void setConfig(KeyValueStore config) {
        this.config = config;
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

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Register a new EdenRepository
     *
     * @param repository the repository instance
     */
    public void registerRepository(EdenRepository repository) {
        this.repositories.put(repository.getClass().getName(), repository);
    }

    /**
     * Register a new EdenRepository and give it an alias. It can then be recalled by either by alias or the
     * fully-qualified class name.
     *
     * @param repository the repository instance to register
     * @param alias      the alias
     */
    public void registerRepository(EdenRepository repository, String alias) {
        this.repositories.put(repository.getClass().getName(), repository);
        this.repositories.put(alias, repository);
    }

    /**
     * Get a repository by its Class
     *
     * @param repositoryClass the class of the repository to find
     * @return the repository if it exists, null otherwise
     */
    public EdenRepository getRepository(Class<? extends EdenRepository> repositoryClass) {
        return this.repositories.getOrDefault(repositoryClass.getName(), null);
    }

    /**
     * Get a repository by its String alias. Alternatively, since all Repository keys are strings, the 'alias' could
     * be the fully-qualified class name of the desired EdenRepository instance.
     *
     * @param repositoryAlias the alias or fully-qualified class name of the repository to find
     * @return the repository if it exists, null otherwise
     */
    public EdenRepository getRepository(String repositoryAlias) {
        return this.repositories.getOrDefault(repositoryAlias, null);
    }

    /**
     * Bootstrap the Eden dependency injection for a given object. Eden will search the object for all registered
     * annotations and attempt to inject objects into each annotated field.
     *
     * @param object the annotated object to inject dependencies into
     */
    public void inject(Object object) {
        edenInjector.processAnnotations(object);
    }
}

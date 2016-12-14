package com.eden.repositories;

import com.eden.Eden;
import com.eden.bible.Bible;
import com.eden.bible.BibleList;
import com.eden.bible.Passage;
import com.eden.bible.Reference;
import com.eden.interfaces.KeyValueStore;
import com.eden.utils.TextUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An interface for getting verse data from an abstract repository, which might be from a database or from, a RESTful
 * API, or anything else. It is designed to be the single point of entry for accessing Verse data, acting as a simple
 * dependency injector, finding or creating Objects of the types given by the Bible, BibleList, and Passage classes
 * provided.
 *
 * All methods are expected to be long-running tasks which can be executed synchronously or asynchronously. All
 * asynchronous functions are just the synchronous functions wrapped in a CompletableFuture which is executed on the
 * Executor defined in the main Eden instance. Async functions are defined to either return the CompletableFuture
 * directly, or instead can simply return the result in a callback if all you need is the result, but run on a background
 * thread (such as when used on the Android platform).
 *
 * In most cases, any EdenRepository implementation should only need to define the appropriate classes, and the base
 * EdenRepository class will be able to do everything else.
 */
public abstract class EdenRepository {

    protected Bible selectedBible;
    protected BibleList bibleList;

    public EdenRepository() {
    }

    /**
     * Returns an ID for a Bible that has been set in the main Eden KeyValueStore at the key of the form
     * "{{fully-qualified Repository class name}}_selectedBibleId"
     * @return
     */
    public String getDefaultBibleId() {
        KeyValueStore config = Eden.getInstance().config();
        return (config.containsKey(this.getClass().getName() + "_selectedBibleId"))
                ? config.getString(this.getClass().getName() + "_selectedBibleId")
                : "";
    }

    /**
     * Stores the ID for a Bible in the main Eden KeyValueStore at the key of the form
     * "{{fully-qualified Repository class name}}_selectedBibleId"
     * @return
     */
    public void setDefaultBibleId(String id) {
        Eden.getInstance().config().putString(this.getClass().getName() + "_selectedBibleId", id);
    }

    /**
     * Sets a given Bible instance as the 'selected' Bible. This will be cached and returned in future calls to
     * 'getBible()' when called without an ID. The Bible's ID will also be set as the default Bible ID in
     * EdenRepository#setDefaultBibleId().
     *
     * @param selectedBible
     */
    public void setSelectedBible(Bible selectedBible) {
        this.selectedBible = selectedBible;
        if(this.selectedBible != null) {
            setDefaultBibleId(this.selectedBible.getId());
        }
    }

    /**
     * Synchronously gets the currently selected Bible. If no Bible has been selected yet, it will attempt to find an
     * appropriate Bible ID given by EdenRepository#getDefaultBibleId() and create it according to
     * EdenRepository#getBible(String id).
     *
     * @return
     */
    public Bible getBible() {
        return getBible(null);
    }

    /**
     * Synchronously gets the Bible at a given ID. If a Bible has not yet been selected, it will attempt to create
     * a Bible according to the following rules:
     * <ol>
     *     <li>It will always attempt to look up a Bible by a given ID. If an ID is passed to this method it will use
     *     that, otherwise it will look for a Bible with an ID given by EdenRepository#getDefaultBibleId().</li>
     *     <li>Once we have determined the Bible ID, it will first look in the Repository's BibleList for a Bible at
     *     the given ID. If no such Bible exists, it will use reflection to create a new Bible of the class given by
     *     EdenRepository#getBibleClass().</li>
     * </ol>
     *
     * @return
     */
    public Bible getBible(String id) {
        boolean getAsSelected = false;
        if(TextUtils.isEmpty(id)) {
            getAsSelected = true;
            id = getDefaultBibleId();
        }

        Bible bible = (getAsSelected)
                ? selectedBible
                : null;

        if(bible == null) {
            if (bibleList != null && bibleList.hasBible(id)) {
                bible = bibleList.getBible(id);
            }
            else {
                try {
                    bible = getBibleClass().getConstructor().newInstance();
                }
                catch (NoSuchMethodException
                        | InvocationTargetException
                        | InstantiationException
                        | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            if (bible != null) {
                bible.setId(id);
                bible.get();

                if (getAsSelected) {
                    setSelectedBible(bible);
                }
            }
        }

        return bible;
    }

    /**
     * Gets the default Bible asynchronously, returning the CompletableFuture which is getting the Bible
     *
     * @return CompletableFuture<Bible>  the Future which will eventually return the Bible
     */
    public CompletableFuture<Bible> getBibleAsync() {
        return getBibleAsync(null);
    }

    /**
     * Gets the Bible at the given ID asynchronously, returning the CompletableFuture which is getting the Bible
     *
     * @return CompletableFuture<Bible>  the Future which will eventually return the Bible
     */
    public CompletableFuture<Bible> getBibleAsync(String id) {
        return CompletableFuture.supplyAsync(() -> getBible(id), Eden.getInstance().getExecutorService());
    }

    /**
     * Gets the default Bible asynchronously, calling back to the client when the Future has completed
     *
     * @param success  the callback when the Bible is eventually found
     * @param error  the callback when there is an error finding the Bible
     */
    public void getBibleAsync(Consumer<? super Bible> success, Function<Throwable, ? extends Bible> error) {
        getBibleAsync(null, success, error);
    }

    /**
     * Gets the Bible at the given ID asynchronously, calling back to the client when the Future has completed
     *
     * @param success  the callback when the Bible is eventually found
     * @param error  the callback when there is an error finding the Bible
     */
    public void getBibleAsync(String id, Consumer<? super Bible> success, Function<Throwable, ? extends Bible> error) {
        getBibleAsync(id)
                .exceptionally(error)
                .thenAccept(success);
    }

    /**
     * Synchronously gets the BibleList. If no BibleList has been set yet, it will attempt to create one via reflection
     * using the class at EdenRepository#getBibleListClass() then cache it to be returned on future calls.
     *
     * @return BibleList  the setBibleList
     */
    public BibleList getBibleList() {
        if(bibleList == null) {
            try {
                bibleList = getBibleListClass().getConstructor().newInstance();
                bibleList.get();
            }
            catch(NoSuchMethodException
                    | InvocationTargetException
                    | InstantiationException
                    | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return bibleList;
    }

    /**
     * Gets the BibleList asynchronously, returning the CompletableFuture which is getting the BibleList
     *
     * @return CompletableFuture<BibleList>  the Future which will eventually return the BibleList
     */
    public CompletableFuture<BibleList> getBibleListAsync() {
        return CompletableFuture.supplyAsync(() -> getBibleList(), Eden.getInstance().getExecutorService());
    }

    /**
     * Gets the BibleList asynchronously, calling back to the client when the Future has completed
     *
     * @param success  the callback when the BibleList is eventually found
     * @param error  the callback when there is an error finding the BibleList
     */
    public void getBibleListAsync(Consumer<? super BibleList> success, Function<Throwable, ? extends BibleList> error) {
        getBibleListAsync()
                .exceptionally(error)
                .thenAccept(success);
    }

    /**
     * Sets the BibleList and caches it to be returned on future calls to getBibleList()
     *
     * @param bibleList
     */
    public void setBibleList(BibleList bibleList) {
        this.bibleList = bibleList;
    }

    /**
     * Parses a Reference into a passage, based on the selected Bible
     *
     * @param reference
     * @return the parsed reference
     */
    public Passage lookupVerse(String reference) {
        return lookupVerse(null, reference);
    }

    /**
     * Parses a Reference into a passage, based on the Bible at the given ID
     *
     * @param reference
     * @return the parsed reference
     */
    public Passage lookupVerse(String bibleId, String reference) {
        Reference.Builder builder = new Reference.Builder();
        builder.setBible(getBible(bibleId));
        builder.parseReference(reference);
        Reference ref = builder.create();

        Passage passage;
        try {
            passage = getPassageClass().getConstructor(Reference.class).newInstance(ref);
            passage.get();
        }
        catch(Exception e) {
            e.printStackTrace();
            passage = null;
        }

        return passage;
    }

    /**
     * Specify the class to be used for the BibleList. This BibleList class will be used to create instances of
     * BibleList whenever necessary.
     *
     * @return your implementation's BibleList class
     */
    public abstract Class<? extends BibleList> getBibleListClass();

    /**
     * Specify the class to be used for the Bible. This Bible class will be used to create instances of Bible
     * whenever necessary.
     *
     * @return your implementation's Bible class
     */
    public abstract Class<? extends Bible> getBibleClass();

    /**
     * Specify the class to be used for the Passage. This Passage class will be used to create instances of
     * Passage whenever necessary.
     *
     * @return your implementation's Passage class
     */
    public abstract Class<? extends Passage> getPassageClass();
}

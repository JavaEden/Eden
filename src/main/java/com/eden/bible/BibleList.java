package com.eden.bible;

import java.util.HashMap;
import java.util.Map;

/**
 * A base class for a collection of Bibles, typically offered by an online Bible text provider.
 * A BibleList allows a user to choose their desired Bible version to download verses from. Providers
 * looking to add their own web services should extend this class to implement the necessary
 * functionality. A BibleList class can be used by a
 * to be set as a preference.
 *
 * @param <T>  the type of Bible contained in this list
 *
 * @see Bible
 */
public abstract class BibleList<T extends Bible> {
	protected Map<String, T> bibles;

	/**
	 * No-arg constructor is necessary to allow compatibility with
	 */
	public BibleList() {
	    this.bibles = new HashMap<>();
	}

	/**
	 * Get a HashMap containing the available Bibles, with their primary ID as a map key.
	 *
	 * @return map of available Bibles
	 */
	public Map<String, T> getBibles() {
		return bibles;
	}

	/**
	 * Manually set the list of Bibles. Useful if you need to create the BibleList from another class,
	 * but still want to use it with this class for its compatibility with
	 *
	 * @param bibles  the map of keys to Bibles to set
	 */
	public void setBibles(Map<String, T> bibles) {
		this.bibles = bibles;
	}

    /**
     * Fetch the BibleList's data given it's current state.
     *
     * @return boolean  true if the BibleList's data was successfully retrieved, false otherwise
     */
    public boolean get() {
        return true;
    }
}

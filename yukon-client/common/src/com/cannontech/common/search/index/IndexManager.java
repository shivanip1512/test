package com.cannontech.common.search.index;

import com.cannontech.database.cache.DBChangeListener;

/**
 * Interface which represents a Lucene index manager.
 */
public interface IndexManager extends DBChangeListener {

    /**
     * Method used while the index is being built. This method will give the
     * current percentage of the index that has been built.
     * @return Percentage of completed index.
     */
    public float getPercentDone();

    /**
     * This method is used to get the index name
     * @return Index name
     */
    public String getIndexName();

    /**
     * This method is used to get the index version
     * @return Index version
     */
    public String getVersion();

    /**
     * This method is used to get the date the index was created
     * @return String date index was created
     */
    public String getDateCreated();

    /**
     * This method is used to get the database the index was created for
     * @return String database index was created for
     */
    public String getDatabase();

    /**
     * Method used to determine if the index is currently being built
     * @return True if index is currently being built
     */
    public boolean isBuilding();

    /**
     * Method used to build the index.
     */
    public void rebuildIndex();


    public void shutdown();

    public SearchTemplate getSearchTemplate();

}

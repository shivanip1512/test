package com.cannontech.web.search.lucene.index;


/**
 * Interface which represents a Lucene index manager.
 */
public interface IndexManager {
    /**
     * Method used while the index is being built. This method will give the
     * current percentage of the index that has been built.
     * @return Percentage of completed index.
     */
    float getPercentDone();

    /**
     * This method is used to get the index name
     * @return Index name
     */
    String getIndexName();

    /**
     * This method is used to get the date the index was created
     * @return String date index was created
     */
    String getDateCreated();

    /**
     * Method used to determine if the index is currently being built
     * @return True if index is currently being built
     */
    boolean isBuilding();

    /**
     * Method used to build the index.
     */
    void rebuildIndex();

    void shutdown();

    /**
     * Get an instance of {@link SearchTemplate} which can be used to query this index.
     */
    SearchTemplate getSearchTemplate();
}

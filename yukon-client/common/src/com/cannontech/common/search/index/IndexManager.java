package com.cannontech.common.search.index;

import java.io.File;
import java.util.Date;

import org.apache.lucene.search.IndexSearcher;

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
     * @return Index date created
     */
    public Date getDateCreated();

    /**
     * This method is used to get the date the index was created in a formatted
     * string
     * @return Formatted index date created
     */
    public String getFormattedDateCreated();

    /**
     * Method used to determine if the index is currently being built
     * @return True if index is currently being built
     */
    public boolean isBuilding();

    /**
     * Method used to set the index as currently being built
     * @param isBuilding - True if index is being built
     */
    public void setBuilding(boolean isBuilding);

    /**
     * Method to get the location of the index on the file system. Indexes will
     * live at: {yukon home}/cache/{index name}/index
     * @return The File location of the index
     */
    public File getIndexLocation();

    /**
     * Method used to create the index.
     * @param overwrite - True if any existing index should be overwritten.
     *            False if the index should only be created if it doesn't
     *            already exist or if the index version is out of date.
     */
    public void createIndex(boolean overwrite);

    /**
     * Method used to get an IndexSearcher for the index
     * @return An index searcher
     */
    public IndexSearcher getIndexSearcher();

}

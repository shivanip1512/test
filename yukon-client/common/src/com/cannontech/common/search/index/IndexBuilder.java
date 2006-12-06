package com.cannontech.common.search.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class which manages building of all of the lucene indexes in the system. This
 * class will delegate index-specific requests to the appropriate index manager.
 */
public class IndexBuilder {

    private Map<String, IndexManager> managerMap = null;

    public void setManagerList(List<IndexManager> managerList) {

        this.managerMap = new HashMap<String, IndexManager>();
        for (IndexManager manager : managerList) {
            this.managerMap.put(manager.getIndexName(), manager);

        }
    }

    /**
     * Method which builds all Lucene indexes
     * @param overwrite - True if existing indexes should be overwritten
     */
    public void buildAllIndexes(boolean overwrite) {
        for (String managerName : this.managerMap.keySet()) {
            this.buildIndex(managerName, overwrite);
        }
    }
    
    public void shutdown() {
        for (IndexManager manager : this.managerMap.values()) {
            manager.shutdown();
        }
    }

    /**
     * Method which builds a given Lucene index
     * @param indexName - Name of index to build
     * @param overwrite - True if existing index should be overwritten
     */
    public void buildIndex(String indexName, final boolean overwrite) {

        final IndexManager manager = this.managerMap.get(indexName);

        if (manager != null) {
            manager.buildIndex(overwrite);
        } else {
            throw new IllegalArgumentException("Invalid index name: " + indexName);
        }

    }

    /**
     * Method to get a list of all index managers
     * @return A list of IndexManager
     */
    public List<IndexManager> getIndexList() {
        return new ArrayList<IndexManager>(this.managerMap.values());
    }

    /**
     * Method to get a specific index manager
     * @param index - Name of index to get the index manager for
     * @return Index manager for the given index
     */
    public IndexManager getIndexManager(String index) {
        return this.managerMap.get(index);
    }

}

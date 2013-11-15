package com.cannontech.web.search.lucene.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Class which manages building of all of the lucene indexes in the system. This
 * class will delegate index-specific requests to the appropriate index manager.
 */
public class IndexBuilder {
    private Map<String, IndexManager> managerMap = null;

    @Autowired
    public void setManagerList(List<IndexManager> managerList) {
        managerMap = new HashMap<String, IndexManager>();
        for (IndexManager manager : managerList) {
            managerMap.put(manager.getIndexName(), manager);
        }
    }

    public void shutdown() {
        for (IndexManager manager : managerMap.values()) {
            manager.shutdown();
        }
    }

    /**
     * Method which builds a given Lucene index
     * @param indexName - Name of index to build
     * @param overwrite - True if existing index should be overwritten
     */
    public void buildIndex(String indexName) {
        final IndexManager manager = managerMap.get(indexName);

        if (manager != null) {
            manager.rebuildIndex();
        } else {
            throw new IllegalArgumentException("Invalid index name: " + indexName);
        }

    }

    /**
     * Method to get a list of all index managers
     * @return A list of IndexManager
     */
    public List<IndexManager> getIndexList() {
        return new ArrayList<IndexManager>(managerMap.values());
    }

    /**
     * Method to get a specific index manager
     * @param index - Name of index to get the index manager for
     * @return Index manager for the given index
     */
    public IndexManager getIndexManager(String index) {
        return managerMap.get(index);
    }
}

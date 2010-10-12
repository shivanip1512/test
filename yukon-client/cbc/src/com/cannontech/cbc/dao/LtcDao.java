package com.cannontech.cbc.dao;

import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.model.LoadTapChanger;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.TransactionException;

public interface LtcDao {

    /**
     * Adds a LTC to the database.
     * Returns the new ltc's PAObject id if the add was successful
     * otherwise -1 if add was unsuccessful.
     * @param ltc
     * @return boolean
     * @throws TransactionException 
     */
    public int add(LoadTapChanger ltc);
    
    /**
     * Deletes a LTC from the database.
     * Returns true if the delete was successful.
     * @param ltc
     * @return boolean
     */
    public boolean delete(int id);

    /**
     * Returns a list of all un-assigned Regulators as Search Results.
     * 
     * @param start
     * @param count
     * @return
     */
    public SearchResult<LiteCapControlObject> getOrphans(final int start, final int count);
}

package com.cannontech.cbc.dao;

import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.model.VoltageRegulator;
import com.cannontech.common.search.SearchResult;
import com.cannontech.database.TransactionException;

public interface VoltageRegulatorDao {

    /**
     * Adds a Regulator to the database.
     * Returns the new Regulator's PAObject id if the add was successful
     * otherwise -1 if add was unsuccessful.
     * @param VoltageRegulator
     * @return boolean
     * @throws TransactionException 
     */
    public int add(VoltageRegulator regulator);
    
    /**
     * Deletes a Regulator from the database.
     * Returns true if the delete was successful.
     * @param VoltageRegulator
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

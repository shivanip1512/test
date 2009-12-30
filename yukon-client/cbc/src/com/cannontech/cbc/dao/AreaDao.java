package com.cannontech.cbc.dao;

import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.SpecialArea;
import com.cannontech.database.TransactionException;

public interface AreaDao {
    
	/**
	 * Adds Area to the database. Returns false if failed.
	 * 
	 * @param area
	 * @return
	 * @throws TransactionException 
	 */
    public void add( Area area ) throws TransactionException;
    
    /**
     * Removes Area from the database. Returns false if failed.
     * 
     * @param area
     * @return
     */
    public boolean remove( Area area );
    
    /**
     * Updates area in the database. Including the PAOName in YukonPAObject. 
     * Returns false if failed. It will fail if the YukonPAObject update fails or CapControlArea.
     * 
     * @param area
     * @return
     */
    public boolean update( Area area );
    
    
    public Area getById( int id );
    
    /**
     * Adds SpecialArea to the database. Returns false if failed.
     * @param specialArea
     * @return
     */
    public boolean addSpecialArea(SpecialArea specialArea);
}

package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.SpecialArea;
import com.cannontech.common.pao.PaoIdentifier;

public interface AreaDao {
    
	/**
	 * Adds Area to the database.
	 * 
	 * @param area
	 * @return
	 */
    public void add( Area area );
    
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
    public void addSpecialArea(SpecialArea specialArea);
    
    public List<PaoIdentifier> getAllAreas();
}

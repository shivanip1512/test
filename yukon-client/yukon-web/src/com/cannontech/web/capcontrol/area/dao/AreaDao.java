package com.cannontech.web.capcontrol.area.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.web.capcontrol.area.model.Area;

public interface AreaDao {
    
    /** Returns a list of areas or special areas based on pao type. */
    List<Area> getAreas(PaoType type);
    
    /** Returns an area by id.  Works for normal and special areas. */
    Area getArea(int areaId);
    
    /** 
     * Inserts or updates an area. An insert is attempted if the id is null.
     * Works for normal and special areas.
     * DbChange message is sent when successful. 
     */
    void save(Area area);
    
    /** 
     * Deletes an area.  Works for normal and special areas.
     * DbChange message is sent when successful. 
     */
    void delete(PaoIdentifier area);
    
}
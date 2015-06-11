package com.cannontech.web.capcontrol.area.dao;

import java.util.List;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.web.capcontrol.area.model.Area;

public interface AreaDao {
    
    List<Area> getAreas();
    
    Area getArea(int areaId);
    
    /** 
     * Inserts or updates an area. An insert is attempted if the id is null.
     * DbChange message is sent when successful. 
     */
    void save(Area area);
    
    /** 
     * Deletes an area.
     * DbChange message is sent when successful. 
     */
    void delete(PaoIdentifier area);
    
}
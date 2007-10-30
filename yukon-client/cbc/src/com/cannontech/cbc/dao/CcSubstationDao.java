package com.cannontech.cbc.dao;

import java.util.List;

public interface CcSubstationDao {
    public List<Integer> getAllUnassignedSubstationIds();
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds( Integer areaId );

    public List<Integer> getAllSubstationIds();
    
    public Integer getSubstationIdByName( String name );
}

package com.cannontech.cbc.dao;

import java.util.List;

import com.cannontech.core.dao.NotFoundException;

public interface CcSubstationDao {
    public List<Integer> getAllUnassignedSubstationIds();
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds( Integer areaId );

    public List<Integer> getAllSubstationIds();
    
    public Integer getSubstationIdByName( String name ) throws NotFoundException;
}

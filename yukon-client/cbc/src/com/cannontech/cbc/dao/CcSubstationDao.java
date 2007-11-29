package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

public interface CcSubstationDao {
    public List<Integer> getAllUnassignedSubstationIds();
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds( Integer areaId );

    public List<Integer> getAllSubstationIds();
    
    public Integer getSubstationIdByName( String name )  throws EmptyResultDataAccessException;
}

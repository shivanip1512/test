package com.cannontech.capcontrol.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.capcontrol.model.LiteCapControlObject;
import com.cannontech.capcontrol.model.Substation;
import com.cannontech.common.search.SearchResult;

public interface SubstationDao {
    
    public boolean assignSubstation(int substationId, String areaName);
    
    public boolean assignSubstation(int areaId, int substationId);
    
    public boolean unassignSubstation(int substationId);
    
    public List<Integer> getAllUnassignedSubstationIds();
    
    public SearchResult<LiteCapControlObject> getOrphans(int startIndex, int itemsPerPage);
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds( Integer areaId );

    public List<Integer> getAllSubstationIds();
    
    public int getSubstationIdByName( String name )  throws EmptyResultDataAccessException;

    public Substation getSubstationById(int id);
}

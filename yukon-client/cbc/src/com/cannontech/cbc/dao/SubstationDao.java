package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.Substation;

public interface SubstationDao {
	
    public boolean add( Substation substation );
    
    public boolean remove( Substation substation );
    
    public boolean update( Substation substation );
    
    public Substation getSubstationById( int id );
    
    public int getParentId(Substation station);
    
    public boolean assignSubstation(Area area, Substation substation);
    public boolean assignSubstation(int areaId, int substationId);
    
    public boolean unassignSubstation(Area area, Substation substation);
    public boolean unassignSubstation(int areaId, int substationId);
    
    public List<Integer> getAllUnassignedSubstationIds();
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds( Integer areaId );

    public List<Integer> getAllSubstationIds();
    
    public Integer getSubstationIdByName( String name )  throws EmptyResultDataAccessException;
}

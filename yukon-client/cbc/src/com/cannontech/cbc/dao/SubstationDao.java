package com.cannontech.cbc.dao;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.cbc.model.Area;
import com.cannontech.cbc.model.LiteCapControlObject;
import com.cannontech.cbc.model.Substation;
import com.cannontech.database.TransactionException;

public interface SubstationDao {
	
    public void add( Substation substation ) throws TransactionException;
    
    public boolean remove( Substation substation );
    
    public boolean update( Substation substation );
    
    public Substation getSubstationById( int id );
    
    public int getParentId(Substation station);
    
    public boolean assignSubstation(Area area, Substation substation);
    public boolean assignSubstation(int areaId, int substationId);
    
    public boolean unassignSubstation(Substation substation);
    public boolean unassignSubstation(int substationId);
    
    public List<Integer> getAllUnassignedSubstationIds();
    public List<LiteCapControlObject> getOrphans();
    
    public List<Integer> getAllSpecialAreaUnassignedSubstationIds( Integer areaId );

    public List<Integer> getAllSubstationIds();
    
    public Integer getSubstationIdByName( String name )  throws EmptyResultDataAccessException;
}

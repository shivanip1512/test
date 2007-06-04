package com.cannontech.multispeak.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.multispeak.db.Substation;

public interface SubstationDao {

    public boolean add(Substation substation);
    
    public boolean remove(Substation substation);
    
    public boolean update(Substation substation);
    
    public Substation getByName(String name) throws DataAccessException;
    
    public Substation getById(int id) throws DataAccessException;
    
	public List<Substation> getAll();
    
}

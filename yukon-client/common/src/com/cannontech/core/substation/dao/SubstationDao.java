package com.cannontech.core.substation.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.common.model.Substation;

public interface SubstationDao {

    public boolean add(Substation substation);
    
    public boolean remove(Substation substation);
    
    public boolean update(Substation substation);
    
    public Substation getByName(String name) throws DataAccessException;
    
    public Substation getById(int id) throws DataAccessException;
    
	public List<Substation> getAll();
	
	/**
	 * Get all Substations that are mapped to this energyCompany through the ECToGenericMapping table by the Substation mapping category.
	 */
	public List<Substation> getAllSubstationsByEnergyCompanyId(int energyCompanyId);
}

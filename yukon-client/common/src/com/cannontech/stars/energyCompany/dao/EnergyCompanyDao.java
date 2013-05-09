package com.cannontech.stars.energyCompany.dao;

import java.util.List;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.stars.energyCompany.dao.impl.EnergyCompanyDaoImpl.DisplayableServiceCompany;

public interface EnergyCompanyDao {

	/**
	 * Duplicate of StarsDatabaseCache.DEFAULT_ENERGY_COMPANY_ID
	 */
    public static final int DEFAULT_ENERGY_COMPANY_ID = -1;
    
    /**
     * Returns the LiteEnergyCompany with the given energy company id.
     */
    public LiteEnergyCompany getEnergyCompany(int energyCompanyID);

    /**
     * Return the LiteEnergyCompany this user belongs to.
     */
    public LiteEnergyCompany getEnergyCompany(LiteYukonUser user);

    /**
     * Return the LiteEnergyCompany looking up by energy company name.
     * 
     * @throws NotFoundException
     */
    public LiteEnergyCompany getEnergyCompanyByName(String energyCompanyName);
    
    /**
     * Return the LiteEnergyCompany looking up by energy company name.
     * 
     * @return LiteEnergyCompany or null if not found
     */
    public LiteEnergyCompany findEnergyCompanyByName(String energyCompanyName);

    /**
     * Returns all the LiteEnergyCompany's that have customerID_ in it.
     */
    public LiteEnergyCompany[] getEnergyCompaniesByCustomer(int customerID_);

    /**
     * Returns the LiteYukonUser associated with this energy company.
     */
    public LiteYukonUser getEnergyCompanyUser(LiteEnergyCompany company);

    /**
     * Returns the LiteYukonUser associated with this energy company.
     */
    public LiteYukonUser getEnergyCompanyUser(int energyCompanyID);

    /**
     * Adds a customer and energy company to EnergyCompanyCustomerList
     */
    public void addEnergyCompanyCustomerListEntry(int customerId, int ecId);

    public List<DisplayableServiceCompany> getAllInheritedServiceCompanies(int energyCompanyId);

    public List<Integer> getParentEnergyCompanyIds(int energyCompanyId);

    public List<LiteEnergyCompany> getAllEnergyCompanies();

    public void updateCompanyName(String name, int energyCompanyId);
    
    /**
     * Creates or updates energy company.
     */
    public void save(EnergyCompany energyCompany);

    public String retrieveCompanyName(int energyCompanyId);
    
}
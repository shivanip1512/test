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
    static final int DEFAULT_ENERGY_COMPANY_ID = -1;

    /**
     * Returns the LiteEnergyCompany with the given energy company id.
     */
    LiteEnergyCompany getEnergyCompany(int energyCompanyID);

    /**
     * Return the LiteEnergyCompany this user belongs to.
     */
    LiteEnergyCompany getEnergyCompany(LiteYukonUser user);

    /**
     * Return the LiteEnergyCompany looking up by energy company name.
     *
     * @throws NotFoundException
     */
    LiteEnergyCompany getEnergyCompanyByName(String energyCompanyName);

    /**
     * Return the LiteEnergyCompany looking up by energy company name.
     *
     * @return LiteEnergyCompany or null if not found
     */
    LiteEnergyCompany findEnergyCompanyByName(String energyCompanyName);

    /**
     * Returns all the LiteEnergyCompany's that have customerID_ in it.
     */
    LiteEnergyCompany[] getEnergyCompaniesByCustomer(int customerID_);

    /**
     * Returns the LiteYukonUser associated with this energy company.
     */
    LiteYukonUser getEnergyCompanyUser(LiteEnergyCompany company);

    /**
     * Returns the LiteYukonUser associated with this energy company.
     */
    LiteYukonUser getEnergyCompanyUser(int energyCompanyID);

    /**
     * Adds a customer and energy company to EnergyCompanyCustomerList
     */
    void addEnergyCompanyCustomerListEntry(int customerId, int ecId);

    List<DisplayableServiceCompany> getAllInheritedServiceCompanies(int energyCompanyId);

    List<Integer> getParentEnergyCompanyIds(int energyCompanyId);

    List<LiteEnergyCompany> getAllEnergyCompanies();

    void updateCompanyName(String name, int energyCompanyId);

    /**
     * Creates or updates energy company.
     */
    void save(EnergyCompany energyCompany);

    String retrieveCompanyName(int energyCompanyId);
}
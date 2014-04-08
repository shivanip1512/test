package com.cannontech.stars.energyCompany.dao;

import java.util.List;

import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.stars.energyCompany.dao.impl.EnergyCompanyDaoImpl.DisplayableServiceCompany;

public interface EnergyCompanyDao {

    /**
     * Returns the LiteEnergyCompany with the given energy company id.
     */
    LiteEnergyCompany getEnergyCompany(int energyCompanyID);

    /**
     * Return the LiteEnergyCompany this user belongs to.
     */
    LiteEnergyCompany getEnergyCompany(LiteYukonUser user);

    /**
     * Returns all the LiteEnergyCompany's that have customerID_ in it.
     */
    LiteEnergyCompany[] getEnergyCompaniesByCustomer(int customerID_);

    /**
     * Adds a customer and energy company to EnergyCompanyCustomerList
     */
    void addEnergyCompanyCustomerListEntry(int customerId, int ecId);

    List<DisplayableServiceCompany> getAllServiceCompanies(Iterable<Integer> energyCompanyIds);

    void updateCompanyName(String name, int energyCompanyId);

    /**
     * Creates or updates energy company.
     */
    void save(EnergyCompany energyCompany);
}
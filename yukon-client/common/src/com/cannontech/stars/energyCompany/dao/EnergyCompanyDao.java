package com.cannontech.stars.energyCompany.dao;

import com.cannontech.database.db.company.EnergyCompany;

public interface EnergyCompanyDao {

    void updateCompanyName(String name, int energyCompanyId);

    /**
     * Creates or updates energy company.
     */
    void save(EnergyCompany energyCompany);
}
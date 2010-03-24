package com.cannontech.stars.dr.appliance.dao;

import java.util.List;

import com.cannontech.stars.dr.appliance.model.ApplianceCategory;

public interface ApplianceCategoryDao {

    public ApplianceCategory getById(int applianceCategoryId);

    public List<ApplianceCategory> getByApplianceCategoryName(String applianceCategoryName, 
                                                              List<Integer> energyCompanyIds);

    public List<Integer> getEnergyCompaniesByApplianceCategoryId(int applianceCategoryId);

    public List<ApplianceCategory> findApplianceCategories(int customerAccountId);

    public List<Integer> getApplianceCategoryIds(int accountId);    
}

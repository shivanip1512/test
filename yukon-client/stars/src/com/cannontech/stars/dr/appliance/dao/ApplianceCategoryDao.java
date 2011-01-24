package com.cannontech.stars.dr.appliance.dao;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.cannontech.stars.dr.appliance.model.ApplianceCategory;

public interface ApplianceCategoryDao {

    public ApplianceCategory getById(int applianceCategoryId);

    public List<ApplianceCategory> getByApplianceCategoryName(String applianceCategoryName, 
                                                              Iterable<Integer> energyCompanyIds);

    public List<Integer> getEnergyCompaniesByApplianceCategoryId(int applianceCategoryId);

    public List<ApplianceCategory> findApplianceCategories(int customerAccountId);

    public List<Integer> getApplianceCategoryIdsByAccount(int accountId);    
    public List<Integer> getApplianceCategoryIdsByEC(int energyCompanyId);    

    public Map<Integer, ApplianceCategory> getByApplianceCategoryIds(
            Collection<Integer> applianceCategoryIds);
}

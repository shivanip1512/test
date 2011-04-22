package com.cannontech.stars.dr.appliance.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface ApplianceCategoryDao {

    public ApplianceCategory getById(int applianceCategoryId);

    public List<ApplianceCategory> getByApplianceCategoryName(String applianceCategoryName, 
                                                              Set<Integer> energyCompanyIds);

    public List<Integer> getEnergyCompaniesByApplianceCategoryId(int applianceCategoryId);

    public List<ApplianceCategory> findApplianceCategories(int customerAccountId);

    public List<Integer> getApplianceCategoryIdsByEC(int energyCompanyId);    

    /**
     * This method returns all the appliance categories for a given energy company
     */
    public List<ApplianceCategory> getApplianceCategoriesByEcId(int energyCompanyId);
    
    public int getEnergyCompanyForApplianceCategory(int applianceCategoryId);

    public Map<Integer, ApplianceCategory> getByApplianceCategoryIds(
            Iterable<Integer> applianceCategoryIds);
    
    /**
     * This method will get all the energy company ids that can
     * have an appliance category this energy company can use.
     */
    public Set<Integer> getAppCatEnergyCompanyIds(YukonEnergyCompany yukonEnergyCompany);
}

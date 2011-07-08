package com.cannontech.stars.core.service;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface YukonEnergyCompanyService {

    /**
     * This method gets the energy company that the operator is directly associated with.
     * Once you have this primary energy company you can figure out, which other companies are 
     * accessible and also see the role property values of the operator's energy company.
     */
    public YukonEnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator);
    
    /**
     * This method gets the yukon energy company that is associated with the supplied account.  
     * This method should be used as the primary option when you are working with an account. 
     */
    public YukonEnergyCompany getEnergyCompanyByAccountId(int accountId);

    /**
     * This method gets the yukon energy company that is associated with the supplied inventory.  
     * This method should be used as the primary option when you are working with an inventory. 
     */
    public YukonEnergyCompany getEnergyCompanyByInventoryId(int inventoryId);
    
    /**
     * Returns a list of all energy companies
     */
    public List<YukonEnergyCompany> getAllEnergyCompanies();

    public boolean isDefaultEnergyCompany(YukonEnergyCompany energyCompany);
    
    /**
     * Schedule Modes are determined by the EnergyCompany that an account belongs to.  This method
     * returns a set of ThermostatScheduleModes given an accountId
     * @param accountId
     * @return
     */
    public Set<ThermostatScheduleMode> getAllowedThermostatScheduleModesByAccountId(int accountId);
    
    /**
     * Schedule Modes are determined by the EnergyCompany that an account belongs to.  This method
     * returns a set of ThermostatScheduleModes given an accountId
     * @param accountId
     * @return
     */
    public Set<ThermostatScheduleMode> getAllowedThermostatScheduleModes(YukonEnergyCompany yukonEnergyCompany);
}
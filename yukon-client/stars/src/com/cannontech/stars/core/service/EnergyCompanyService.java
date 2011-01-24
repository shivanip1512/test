package com.cannontech.stars.core.service;

import java.util.List;

import com.cannontech.core.roleproperties.YukonEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface EnergyCompanyService {

    /**
     * Returns all the member energy companies, including itself,  of the supplied energy company.  
     * This option also handles checking the ADMIN_MANAGE_MEMBERS role property to see if the supplied
     * energy company has permissions to access the member energy companies.
     */
    public List<YukonEnergyCompany> getAccessibleChildEnergyCompanies(int energyCompanyId);

    /**
     * Returns all the energy companies that the supplied energy company is a
     * member of, including itself. It will also be ordered from child energy company
     * to the further most parent energy company.
     */
    public List<YukonEnergyCompany> getAccessibleParentEnergyCompanies(int energyCompanyId);    

    /**
     * This method gets the primary energy company that the operator is directly associated with.
     * Once you have this primary energy company you can figure out, which other companies are 
     * accessible and also see the role property values of the operator's energy company.
     */
    public YukonEnergyCompany getPrimaryEnergyCompanyByOperator(LiteYukonUser operator);
    
    /**
     * This method gets the yukon energy company that is associated with the supplied account.  
     * This method should be used as the primary option when you are working with an account. 
     */
    public YukonEnergyCompany getEnergyCompanyByAccountId(int accountId);

    /**
     * This method gets the yukon energy company that is associated with the supplied account.  
     * This method should be used as the primary option when you are working with an inventory. 
     */
    public YukonEnergyCompany getEnergyCompanyByInventoryId(int inventoryId);

}
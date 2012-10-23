package com.cannontech.stars.core.service;

import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface YukonEnergyCompanyService {

    /**
     * This method gets the energy company that the operator is directly associated with.
     * Once you have this primary energy company you can figure out, which other companies are 
     * accessible and also see the role property values of the operator's energy company.
     */
    public YukonEnergyCompany getEnergyCompanyByOperator(LiteYukonUser operator);

    /**
     * This method gets the energy company id for the operator supplied.
     */
    public int getEnergyCompanyIdByOperator(LiteYukonUser operator);
    
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
     * This method returns all of the child energy companies underneath the supplied energy company 
     * no matter how many levels deep that may be.
     * 
     * @param energyCompanyId - The energyCompanyId supplied is not included in the resulting list.
     */
    public List<Integer> getChildEnergyCompanies(int energyCompanyId);

    /**
     * This method returns the direct child energy companies underneath the energy company.
     * It will not return any of the children of the child energy companies.  If you want that data you'll want to
     * use the getChildEnergyCompanies method above.
     * 
     * @param energyCompanyId - The energyCompanyId supplied is not included in the resulting list.
     */
    public List<Integer> getDirectChildEnergyCompanies(int energyCompanyId);

    /**
     * This method returns the parent energy company id of the energy company id supplied.  This will return null
     * if the energy company id is the default energy company, since it has no parent.
     */
    public Integer getParentEnergyCompany(int energyCompanyId);

    
}
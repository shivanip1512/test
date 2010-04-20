package com.cannontech.stars.core.dao;

import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;

/**
 * Data access interface used to get energy company
 */
public interface ECMappingDao {

    /**
     * Method to get an energy company by account
     * @param account - Account to get energy company for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getCustomerAccountEC(CustomerAccount account);

    /**
     * Method to get an energy company by accountId
     * @param accountId - Id of account to get energy company for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getCustomerAccountEC(int accountId);

    /**
     * Method to get the energy company for a given inventory id
     * @param inventoryId - Id of inventory to get ec for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getInventoryEC(int inventoryId);

    /**
     * Method to get the energy company for a given contact id
     * @param contactId - Id of contact to get ec for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getContactEC(int contactId);

    /**
     * Method to add an energy company to account mapping
     * @param ecToAccountMapping
     */
    public void addECToAccountMapping(ECToAccountMapping ecToAccountMapping);
    
    /**
     * Method to delete energy company to customer event mappings
     * @param eventIds
     */
    public void deleteECToCustomerEventMapping(List<Integer> eventIds);

    /**
     * Method to add a call report to call report mapping
     * @param energyCompanyId
     * @param callId
     */
    public void addECToCallReportMapping(int energyCompanyId, int callId);
    
    /**
     * Method to delete energy company to call report mappings
     * @param callReportIds
     */
    public void deleteECToCallReportMapping(List<Integer> callReportIds);

    /**
     * Method to delete energy company to work order mappings
     * @param workOrderIds
     */
    public void deleteECToWorkOrderMapping(List<Integer> workOrderIds);

    /**
     * Method to delete energy company to account mappings 
     * @param accountId
     */
    public void deleteECToAccountMapping(Integer accountId);

    /**
     * Method to delete energy company to inventory mapping 
     * @param inventoryId
     */
    public void deleteECToInventoryMapping(int inventoryId);
    
    /**
     * Method to update energy company for an account
     * @param accountId
     * @param energyCompanyId
     */
    public void updateECToAccountMapping(int accountId, int energyCompanyId);

    /**
     * Get a list of all energyCompanyIds that are parent energyComompanys.
     * There will be no inherited energyCompanys if the user role property INHERIT_PARENT_APP_CATS is not true.
     * @param energyCompany The base energy company from which to check for parent energy companies.
     * Note: The set always includes the base energy company id.
     * @return
     */
    public Set<Integer> getInheritedEnergyCompanyIds(LiteStarsEnergyCompany energyCompany);
}
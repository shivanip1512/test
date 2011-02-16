package com.cannontech.stars.core.dao;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.ECToAccountMapping;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

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
     * Method to get just the energyCompanyId that a given accountId is in.
     */
    public int getEnergyCompanyIdForAccountId(int accountId);
    
    /**
     * Method to get the energy company id for a given inventory id
     * @param inventoryId - Id of inventory to get ec for
     * @return Energy company
     */
    public int getEnergyCompanyIdForInventoryId(int inventoryId);


    /**
     * Method to get the energy company for a given contact id
     * @param contactId - Id of contact to get ec for
     * @return Energy company
     */
    public LiteStarsEnergyCompany getContactEC(int contactId);

    /**
     * This method returns all of the routeIds associated to the given energyCompanyId
     */
    public List<Integer> getRouteIdsForEnergyCompanyId(int energyCompanyId);
    
    /**
     * This method returns all the substationIds associated to the given energyCompanyId
     */
    public List<Integer> getSubstationIdsForEnergyCompanyId(int energycompanyId);

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
     * Method to add an energy company to work order mapping
     * @param ecToWorkOrderMapping
     */
    public void addECToWorkOrderMapping(int energyCompanyId, int workOrderId);
    
    /**
     * Method to add a call report to call report mapping
     * @param energyCompanyId
     * @param callId
     */
    public void addECToCallReportMapping(int energyCompanyId, int callId);
    
    /**
     * Method to add an energy company to route mapping
     * @param energyCompanyId
     * @param routeId
     */
    public void addECToRouteMapping(int energyCompanyId, int routeId);


    /**
     * Method to add an energy company to substation mapping
     * @param energyCompanyId
     * @param substationId
     */
    public void addECToSubstationMapping(int energyCompanyId, int substationId);

    
    /**
     * Method to delete energy company to substation mappings
     * @param routeIds
     */
    public int deleteECToSubstationMapping(int energyCompanyId, int substationId);
    
    /**
     * Method to delete energy company to route mappings
     * @param routeIds
     */
    public int deleteECToRouteMapping(int energyCompanyId, int routeId);

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
     * Returns all the member energy companies, including itself,  of the supplied energy company.  
     */
    public Set<YukonEnergyCompany> getChildEnergyCompanies(int energyCompanyId);

    /**
     * Returns all the member energy companies, including itself,  of the supplied energy company.  
     */
    public Set<Integer> getChildEnergyCompanyIds(int energyCompanyId);  

    /**
     * Returns all the energy companies that the supplied energy company is a
     * member of, including itself. It will also be ordered from child energy company
     * to the further most parent energy company.
     */
    public LinkedHashSet<YukonEnergyCompany> getParentEnergyCompanies(int energyCompanyId);

    /**
     * Returns all the energy company ids that the supplied energy company is a
     * member of, including itself. It will also be ordered from child energy company
     * to the further most parent energy company.
     */
    public LinkedHashSet<Integer> getParentEnergyCompanyIds(int energyCompanyId);

}
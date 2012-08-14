package com.cannontech.stars.core.dao;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.cannontech.core.dao.InUseException;
import com.cannontech.core.users.model.LiteUserGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
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
     * @param liteYukonUsr
     * @param liteStarsEnergyCompany
     */
    public void addEnergyCompanyOperatorLoginListMapping(LiteYukonUser liteYukonUser, LiteStarsEnergyCompany liteStarsEnergyCompany);
    
    /**
     * Method to add a yukon user to energy company as an operator login
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
    public void addEcToRouteMapping(int energyCompanyId, int routeId);


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

    /**
     * Returns a list of the operator user groups for an energy company
     */
    public List<LiteUserGroup> getOperatorUserGroups(int energyCompanyId);

    /**
     * Returns a list of the customer user groups for an energy company
     */
    public List<LiteUserGroup> getResidentialUserGroups(int energyCompanyId);

    /**
     * Adds a list of user groups to the energy companies operator user group list
     */
    public void addECToOperatorUserGroupMapping(int ecId, Iterable<Integer> userGroupIds);

    /**
     * Adds a list of user groups to the energy companies residential customer user group list
     */
    public void addECToResidentialUserGroupMapping(int ecId, List<Integer> userGroupIds);

    /**
     * Removes a user group from the operator user group list for an energy company.
     * @throws InUseException If the mapping cannot be deleted because the energy company has logins
     *             associated with the given group.
     */
    public void deleteECToOperatorUserGroupMapping(int ecId, int userGroupId) throws InUseException;

    /**
     * Removes a user group from the customer user group list for an energy company
     * @throws InUseException If the mapping cannot be deleted because the energy company has logins
     *             associated with the given group.
     */
    public void deleteECToResidentialUserGroupMapping(int ecId, int userGroupId) throws InUseException;

    /**
     * Returns the operator login of an energy company it's parent energy company uses to login to it.
     * Will return null if no parent login has been set.
     */
    public LiteYukonUser findParentLogin(int childEnergyCompanyId);

    /**
     * Sets the parent login for a child energy company, Will delete any existing login and insert this one.
     */
    public void saveParentLogin(int parentEcId, int childEcId, Integer parentLogin);
    
    /**
     * Deletes any existing parent login
     */
    public void removeParentLogin(int parentEcId, int childEcId);

    /**
     * Returns list of account id's for energy company
     */
    public List<Integer> getAccountIds(int ecId);

}
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
import com.cannontech.stars.energyCompany.EcMappingCategory;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface ECMappingDao {

    /**
     * Gets an energy company by account
     * @param account - Account to get energy company for
     * @return Energy company
     */
    LiteStarsEnergyCompany getCustomerAccountEC(CustomerAccount account);

    /**
     * Gets an energy company by accountId
     * @param accountId - Id of account to get energy company for
     * @return Energy company
     */
    LiteStarsEnergyCompany getCustomerAccountEC(int accountId);

    /**
     * Gets just the energyCompanyId that a given accountId is in.
     */
    int getEnergyCompanyIdForAccountId(int accountId);

    /**
     * Gets the energy company id for a given inventory id
     * @param inventoryId - Id of inventory to get ec for
     * @return Energy company
     */
    int getEnergyCompanyIdForInventoryId(int inventoryId);


    /**
     * Gets the energy company for a given contact id
     * @param contactId - Id of contact to get ec for
     * @return Energy company
     */
    LiteStarsEnergyCompany getContactEC(int contactId);

    /**
     * Returns an immutable list of all routeIds associated with the energy company
     */
    List<Integer> getRouteIdsForEnergyCompanyId(int energyCompanyId);

    /**
     * Returns all the substationIds associated to the given energyCompanyId
     */
    List<Integer> getSubstationIdsForEnergyCompanyId(int energycompanyId);

    /**
     * Adds an energy company to operator mapping
     */
    void addEnergyCompanyOperatorLoginListMapping(LiteYukonUser liteYukonUser, LiteStarsEnergyCompany liteStarsEnergyCompany);

    /**
     * Adds an energy company to operator mapping
     */
    void addEnergyCompanyOperatorLoginListMapping(int userId, int energyCompanyId);

    /**
     * Adds a yukon user to energy company as an operator login
     * @param ecToAccountMapping
     */
    void addECToAccountMapping(ECToAccountMapping ecToAccountMapping);

    /**
     * Deletes energy company to customer event mappings
     * @param eventIds
     */
    void deleteECToCustomerEventMapping(List<Integer> eventIds);


    /**
     * Adds an energy company to work order mapping
     * @param ecToWorkOrderMapping
     */
    void addECToWorkOrderMapping(int energyCompanyId, int workOrderId);

    /**
     * Adds a call report to call report mapping
     * @param energyCompanyId
     * @param callId
     */
    void addECToCallReportMapping(int energyCompanyId, int callId);

    /**
     * Adds an energy company to route mapping
     * @param energyCompanyId
     * @param routeId
     */
    void addEcToRouteMapping(int energyCompanyId, int routeId);


    /**
     * Adds an energy company to substation mapping
     * @param energyCompanyId
     * @param substationId
     */
    void addECToSubstationMapping(int energyCompanyId, int substationId);


    /**
     * Deletes energy company to substation mappings
     * @param routeIds
     */
    int deleteECToSubstationMapping(int energyCompanyId, int substationId);

    /**
     * Deletes energy company to route mappings
     * @param routeIds
     */
    int deleteECToRouteMapping(int energyCompanyId, int routeId);

    /**
     * Deletes energy company to call report mappings
     * @param callReportIds
     */
    void deleteECToCallReportMapping(List<Integer> callReportIds);

    /**
     * Deletes energy company to work order mappings
     * @param workOrderIds
     */
    void deleteECToWorkOrderMapping(List<Integer> workOrderIds);

    /**
     * Deletes energy company to account mappings
     * @param accountId
     */
    void deleteECToAccountMapping(Integer accountId);

    /**
     * Updates energy company for an account
     * @param accountId
     * @param energyCompanyId
     */
    void updateECToAccountMapping(int accountId, int energyCompanyId);


    /**
     * Returns all the member energy companies, including itself,  of the supplied energy company.
     */
    Set<YukonEnergyCompany> getChildEnergyCompanies(int energyCompanyId);

    /**
     * Returns all the member energy companies, including itself,  of the supplied energy company.
     */
    Set<Integer> getChildEnergyCompanyIds(int energyCompanyId);

    /**
     * Returns all the energy companies that the supplied energy company is a
     * member of, including itself. It will also be ordered from child energy company
     * to the further most parent energy company.
     */
    LinkedHashSet<YukonEnergyCompany> getParentEnergyCompanies(int energyCompanyId);

    /**
     * Returns all the energy company ids that the supplied energy company is a
     * member of, including itself. It will also be ordered from child energy company
     * to the further most parent energy company.
     */
    LinkedHashSet<Integer> getParentEnergyCompanyIds(int energyCompanyId);

    /**
     * Returns a list of the operator user groups for an energy company
     */
    List<LiteUserGroup> getOperatorUserGroups(int energyCompanyId);

    /**
     * Returns a list of the customer user groups for an energy company
     */
    List<LiteUserGroup> getResidentialUserGroups(int energyCompanyId);

    /**
     * Adds a list of user groups to the energy companies operator user group list
     */
    void addECToOperatorUserGroupMapping(int ecId, Iterable<Integer> userGroupIds);

    /**
     * Adds a list of user groups to the energy companies residential customer user group list
     */
    void addECToResidentialUserGroupMapping(int ecId, List<Integer> userGroupIds);

    /**
     * Removes a user group from the operator user group list for an energy company.
     * @throws InUseException If the mapping cannot be deleted because the energy company has logins
     *             associated with the given group.
     */
    void deleteECToOperatorUserGroupMapping(int ecId, int userGroupId) throws InUseException;

    /**
     * Removes a user group from the customer user group list for an energy company
     * @throws InUseException If the mapping cannot be deleted because the energy company has logins
     *             associated with the given group.
     */
    void deleteECToResidentialUserGroupMapping(int ecId, int userGroupId) throws InUseException;

    /**
     * Returns the operator login of an energy company it's parent energy company uses to login to it.
     * Will return null if no parent login has been set.
     */
    LiteYukonUser findParentLogin(int childEnergyCompanyId);

    /**
     * Sets the parent login for a child energy company, Will delete any existing login and insert this one.
     */
    void saveParentLogin(int parentEcId, int childEcId, Integer parentLogin);

    /**
     * Deletes any existing parent login
     */
    void removeParentLogin(int parentEcId, int childEcId);

    /**
     * Returns list of account id's for energy company
     */
    List<Integer> getAccountIds(int ecId);

    /**
     * Returns "true" if the operator login is part of the operator user group.
     * @param operatorLoginId
     */
    boolean isOperatorInOperatorUserGroup(int operatorLoginId);

    /**
     * Returns list of ItemIds for given energyCompanyId and EcMappingCategory.
     * @param energycompanyId
     * @param category
     * @return ItemIds
     */
    List<Integer> getItemIdsForEnergyCompanyAndCategory(int energycompanyId, EcMappingCategory category);

    /**
     * Removes the user from operator login list.
     */
    void deleteEnergyCompanyOperatorLoginListMapping(int userId, int energyCompanyId);
}
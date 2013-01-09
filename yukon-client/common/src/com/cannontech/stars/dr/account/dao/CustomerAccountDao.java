package com.cannontech.stars.dr.account.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.SetMultimap;

public interface CustomerAccountDao {

    public void add(CustomerAccount account);
    
    public boolean remove(CustomerAccount account);
    
    public void update(CustomerAccount account);
    
    public CustomerAccount getById(int accountId);
    
    /**
     * Uses {@link com.cannontech.stars.database.cache.StarsDatabaseCache#getEnergyCompanyByUser(LiteYukonUser)}
     * to get the Energy Company for the user, then calls {@link #getByAccountNumberForDescendentsOfEnergyCompany(String, YukonEnergyCompany)}.
     * @param accountNumber
     * @param user the user calling the method, only makes sense as an operator
     * @return
     * @deprecated call getByAccountNumberForDescendentsOfEnergyCompany directly, get EC from YukonEnergyCompanyService
     */
    @Deprecated
    public CustomerAccount getByAccountNumber(String accountNumber, LiteYukonUser user);
    
    /**
     * Returns a list all customer accounts for user, 
     *  including Primary and Additional contact relationships.
     * @param user
     * @return
     */
    public List<CustomerAccount> getByUser(LiteYukonUser user);
    
    public List<CustomerAccount> getAll();
    
    public CustomerAccount getAccountByInventoryId(int inventoryId);
    
    /**
     * This method is a performance method.  This allows us to get a huge multimap of inventory ids to customer accounts.
     * This method will cut down on the amount of dao hits, 
     * but will force the user to use a map call to get the inventoryIds for each customer account. 
     */
    public Map<Integer, CustomerAccount> getInventoryIdsToAccountMap(Collection<Integer> inventoryIds);

    /**
     * This method is a performance method.  This allows us to get a huge multimap of customer accounts to inventory ids.
     * This method will cut down on the amount of dao hits, 
     * but will force the user to use a map call to get the inventoryIds for each customer account. 
     */
    public SetMultimap<CustomerAccount, Integer> getAccountToInventoryIdsMap(Collection<Integer> inventoryIds);
    
    /**
     * Returns a map of InventoryIds to AccountIds.
     * @param inventoryIds
     * @return
     */
    public Map<Integer, Integer> getAccountIdsByInventoryIds(Iterable<Integer> inventoryIds);
    
    /**
     * Returns a map of AccountIds to AccountNumbers. 
     * @param accountIds
     * @return
     */
    public Map<Integer, String> getAccountNumbersByAccountIds(Iterable<Integer> accountIds);

    public CustomerAccount getAccountByContactId(int contactId);
    
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByGroupIds(final int ecId, List<Integer> groupIds, Date startDate, Date stopDate);
    
    public CustomerAccountWithNames getAccountWithNamesByCustomerId(final int customerId,
                                                                    final int ecId);

    public Map<Integer, CustomerAccountWithNames> getAccountsWithNamesByAccountId(
            Iterable<Integer> accountIds);

    /**
     * This method returns a customerAccountWithNames object that contains contact information
     * for the account number and energy company id supplied
     */
    public CustomerAccountWithNames getAcountWithNamesByAccountNumber(String accountNumber, 
                                                                       int ecId);


    public CustomerAccount getByAccountNumber(String accountNumber, int energyCompanyId);
    
    public CustomerAccount getByAccountNumber(String accountNumber, List<Integer> energyCompanyIds);
    
    public CustomerAccount findByAccountNumber(String accountNumber, List<Integer> energyCompanyIds);
    
    /**
     * Searches for an account number with one energy company and its descendants. Caller should
     * probably call {@link YukonEnergyCompanyService#getEnergyCompanyByOperator(LiteYukonUser)} before
     * calling this method.
     * @param accountNumber
     * @param yukonEnergyCompany - the top level energy company which will be searched
     * @return
     */
    public CustomerAccount getByAccountNumberForDescendentsOfEnergyCompany(String accountNumber,  YukonEnergyCompany yukonEnergyCompany);
    
    /**
     * Method to get a total count of the number of customer accounts in the system.  If the assignedProgramIds are supplied
     * this method will limit the count to the accounts in those programs.
     */
    public int getTotalNumberOfAccounts(YukonEnergyCompany yukonEnergyCompany, List<Integer> assignedProgramIds);

    /**
     * Returns the LiteYukonUser of the account.
     * @param accountId
     * @return LiteYukonUser user
     */
    public LiteYukonUser getYukonUserByAccountId(int accountId);
    
    /**
     * Helper method to return exactly one CustomerAccount for user.
     *  A warning message is logged if more than one account is found for the user.
     * @param user
     * @return
     * @throws NotAuthorizedException if user is not associated with any accounts.
     */
    public CustomerAccount getCustomerAccount(LiteYukonUser user) throws NotAuthorizedException;
}
package com.cannontech.stars.dr.account.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public interface CustomerAccountDao {
    void add(CustomerAccount account);

    boolean remove(CustomerAccount account);

    void update(CustomerAccount account);

    CustomerAccount getById(int accountId);

    /**
     * Returns the customer account for the user (EC Operator), including any EC Descendants.
     * @param user the user calling the method, only makes sense as an operator
     */
    CustomerAccount getByAccountNumber(String accountNumber, LiteYukonUser ecOperator);

    /**
     * Return a list all customer accounts for user, including Primary and Additional contact relationships.
     * user is assumed to be tied directly to the account, and NOT the ec operator.
     */
    List<CustomerAccount> getByUser(LiteYukonUser user);

    List<CustomerAccount> getAll();

    CustomerAccount getAccountByInventoryId(int inventoryId);

    /**
     * This is a performance method. It allows us to get a huge multimap of inventory ids to customer
     * accounts. It will cut down on the number of DAO hits, but will force the user to use a map call
     * to get the inventoryIds for each customer account.
     */
    Map<Integer, CustomerAccount> getInventoryIdsToAccountMap(Collection<Integer> inventoryIds);

    /**
     * Return a map of InventoryIds to AccountIds.
     */
    Map<Integer, Integer> getAccountIdsByInventoryIds(Iterable<Integer> inventoryIds);

    /**
     * Return a map of AccountIds to AccountNumbers.
     */
    Map<Integer, String> getAccountNumbersByAccountIds(Iterable<Integer> accountIds);

    CustomerAccount getAccountByContactId(int contactId);

    CustomerAccount getAccountByCustomerId(int customerId);

    List<CustomerAccountWithNames> getAllAccountsWithNamesByGroupIds(int ecId, List<Integer> groupIds, Date startDate,
            Date stopDate);

    Map<Integer, CustomerAccountWithNames> getAccountsWithNamesByAccountId(Iterable<Integer> accountIds);

    /**
     * Return a customerAccountWithNames object that contains contact information
     * for the account number and energy company id supplied.
     */
    CustomerAccountWithNames getAcountWithNamesByAccountNumber(String accountNumber, int ecId);

    /**
     * Returns the customer account for the user (EC Operator), NOT including EC Descendants.
     * Only use this method when you are certain that member ECs shall not be included, such as for an "add".
     * Otherwise, use {@link #getByAccountNumber(String, LiteYukonUser)}
     */
    CustomerAccount getByAccountNumber(String accountNumber, int energyCompanyId);

    /**
     * Returns the customer account for the list of energyCompanies. (Expectation is this list is this.EC plus descendants)
     */
    CustomerAccount getByAccountNumber(String accountNumber, Iterable<? extends YukonEnergyCompany> energyCompanies);

    CustomerAccount findByAccountNumber(String accountNumber, Iterable<? extends YukonEnergyCompany> energyCompanies);

    /**
     * Get a total count of the number of customer accounts in the system. If the assignedProgramIds
     * are supplied this method will limit the count to the accounts in those programs.
     */
    int getTotalNumberOfAccounts(YukonEnergyCompany yukonEnergyCompany, List<Integer> assignedProgramIds);

    /**
     * Return the LiteYukonUser of the account.
     */
    LiteYukonUser getYukonUserByAccountId(int accountId);

    /**
     * Helper to return exactly one CustomerAccount for user.
     * A warning message is logged if more than one account is found for the user.
     * 
     * @throws NotAuthorizedException if user is not associated with any accounts.
     */
    CustomerAccount getCustomerAccount(LiteYukonUser user) throws NotAuthorizedException;

    /**
     * Returns list of customer accounts for account numbers
     */
    List<CustomerAccount> getCustomerAccountsByAccountNumbers(Set<String> accountNumbers);
}

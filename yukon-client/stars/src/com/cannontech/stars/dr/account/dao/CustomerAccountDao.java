package com.cannontech.stars.dr.account.dao;

import java.util.Date;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;

public interface CustomerAccountDao {

    public boolean add(CustomerAccount account);
    
    public boolean remove(CustomerAccount account);
    
    public boolean update(CustomerAccount account);
    
    public CustomerAccount getById(int accountId);
    
    public CustomerAccount getByAccountNumber(String accountNumber, LiteYukonUser user);
    
    public List<CustomerAccount> getByUser(LiteYukonUser user);
    
    public List<CustomerAccount> getAll();
    
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByEC(final int ecId);
    
    public CustomerAccount getAccountByInventoryId(int inventoryId);

    public CustomerAccount getAccountByContactId(int contactId);
    
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByGroupIds(final int ecId, List<Integer> groupIds,
                                                                            Date startDate, Date stopDate);
    public CustomerAccountWithNames getAccountWithNamesByCustomerId(final int customerId, final int ecId);

    public CustomerAccount getByAccountNumber(String accountNumber, int energyCompanyId);
    
    public CustomerAccount getByAccountNumber(String accountNumber, List<Integer> energyCompanyIds);
    
    /**
     * Method to get a total count of the number of customer accounts in the system.
     * @param energyCompany - Energy company to get accounts for
     * @return Total number of accounts
     */
    public int getTotalNumberOfAccounts(LiteStarsEnergyCompany energyCompany);
}

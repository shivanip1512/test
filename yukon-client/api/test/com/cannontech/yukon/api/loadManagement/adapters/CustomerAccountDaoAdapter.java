package com.cannontech.yukon.api.loadManagement.adapters;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;

public class CustomerAccountDaoAdapter implements CustomerAccountDao {

    @Override
    public void add(CustomerAccount account) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public boolean remove(CustomerAccount account) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public void update(CustomerAccount account) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount getById(int accountId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount getByAccountNumber(String accountNumber, LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<CustomerAccount> getByUser(LiteYukonUser user) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<CustomerAccount> getAll() {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount getAccountByInventoryId(int inventoryId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount getAccountByContactId(int contactId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount getAccountByCustomerId(int customerId) {
        throw new UnsupportedOperationException("not implemented");
    }
    
    @Override
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByGroupIds(int ecId, List<Integer> groupIds,
            Date startDate, Date stopDate) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Map<Integer, CustomerAccountWithNames> getAccountsWithNamesByAccountId(Iterable<Integer> accountIds) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccountWithNames getAcountWithNamesByAccountNumber(String accountNumber, int ecId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount getByAccountNumber(String accountNumber, int energyCompanyId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount getByAccountNumber(String accountNumber,
            Iterable<? extends YukonEnergyCompany> energyCompanyIds) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount findByAccountNumber(String accountNumber,
            Iterable<? extends YukonEnergyCompany> energyCompanyIds) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public int getTotalNumberOfAccounts(YukonEnergyCompany yukonEnergyCompany, List<Integer> assignedProgramIds) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public LiteYukonUser getYukonUserByAccountId(int accountId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public CustomerAccount getCustomerAccount(LiteYukonUser user) throws NotAuthorizedException {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Map<Integer, Integer> getAccountIdsByInventoryIds(Iterable<Integer> inventoryIds) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Map<Integer, String> getAccountNumbersByAccountIds(Iterable<Integer> accountIds) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public Map<Integer, CustomerAccount> getInventoryIdsToAccountMap(Collection<Integer> inventoryIds) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public List<CustomerAccount> getCustomerAccountsByAccountNumbers(Set<String> accountNumbers, int energyCompanyId) {
        throw new UnsupportedOperationException("not implemented");
    }
}

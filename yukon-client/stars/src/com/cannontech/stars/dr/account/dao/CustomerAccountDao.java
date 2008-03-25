package com.cannontech.stars.dr.account.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.account.model.CustomerAccountWithNames;

public interface CustomerAccountDao {

    public boolean add(CustomerAccount account);
    
    public boolean remove(CustomerAccount account);
    
    public boolean update(CustomerAccount account);
    
    public CustomerAccount getById(int accountId) throws DataAccessException;
    
    public CustomerAccount getByAccountNumber(String accountNumber) throws DataAccessException;
    
    public List<CustomerAccount> getByUser(LiteYukonUser user) throws DataAccessException;
    
    public List<CustomerAccount> getAll();
    
    public List<CustomerAccountWithNames> getAllAccountsWithNamesByEC(final int ecId);
    
}

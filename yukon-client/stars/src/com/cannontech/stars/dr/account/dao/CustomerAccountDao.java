package com.cannontech.stars.dr.account.dao;

import java.util.List;

import org.springframework.dao.DataAccessException;

import com.cannontech.stars.dr.account.model.CustomerAccount;

public interface CustomerAccountDao {

    public boolean add(CustomerAccount account);
    
    public boolean remove(CustomerAccount account);
    
    public boolean update(CustomerAccount account);
    
    public CustomerAccount getById(int accountId) throws DataAccessException;
    
    public CustomerAccount getByAccountNumber(String accountNumber) throws DataAccessException;
    
    public List<CustomerAccount> getAll();
    
}

package com.cannontech.stars.dr.account.dao;

import java.util.List;

import com.cannontech.stars.dr.account.model.AccountSite;

public interface AccountSiteDao {

    public boolean add(AccountSite accountSite);
    
    public boolean update(AccountSite accountSite);
    
    public boolean remove(AccountSite accountSite);
    
    public AccountSite getByAccountSiteId(int accountSiteId);
    
    public List<AccountSite> getAll();

}

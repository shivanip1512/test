package com.cannontech.stars.dr.account.dao;

import com.cannontech.stars.dr.account.model.CustomerResidence;

public interface CustomerResidenceDao {

	public void insert(CustomerResidence customerResidence);

	public void update(CustomerResidence customerResidence);
    
    public CustomerResidence findByAccountSiteId(int accountSiteId);
}

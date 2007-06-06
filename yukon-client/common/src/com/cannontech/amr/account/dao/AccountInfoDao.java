package com.cannontech.amr.account.dao;

import com.cannontech.amr.account.model.AccountInfo;
import com.cannontech.amr.account.model.ServiceLocation;

public interface AccountInfoDao {

    public AccountInfo getAccount(int deviceId);
    
    public ServiceLocation getServiceLocation(int deviceId);

}

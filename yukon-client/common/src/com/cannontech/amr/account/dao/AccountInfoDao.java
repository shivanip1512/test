package com.cannontech.amr.account.dao;

import java.rmi.RemoteException;

import com.cannontech.amr.account.model.AccountInfo;
import com.cannontech.amr.account.model.ServiceLocation;

public interface AccountInfoDao {

    public AccountInfo getAccount(int deviceId) throws RemoteException;
    
    public ServiceLocation getServiceLocation(int deviceId) throws RemoteException;

}

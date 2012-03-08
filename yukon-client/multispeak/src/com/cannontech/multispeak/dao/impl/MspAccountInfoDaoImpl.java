package com.cannontech.multispeak.dao.impl;

import java.rmi.RemoteException;

import com.cannontech.amr.account.dao.AccountInfoDao;
import com.cannontech.amr.account.model.AccountInfo;
import com.cannontech.amr.account.model.ServiceLocation;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.common.model.Address;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.dao.MultispeakDao;

public class MspAccountInfoDaoImpl implements AccountInfoDao {

    public MspObjectDao mspObjectDao;
    public MultispeakFuncs multispeakFuncs;
    public MultispeakDao multispeakDao;
    public MeterDao meterDao;
    
    public void setMspObjectDao(MspObjectDao mspObjectDao) {
        this.mspObjectDao = mspObjectDao;
    }
    
    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }
    
    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }
    
    public void setMeterDao(MeterDao meterDao) {
        this.meterDao = meterDao;
    }
    
    public AccountInfo getAccount(int deviceId) throws RemoteException { 
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        YukonMeter meter = meterDao.getYukonMeterForId(deviceId);
        com.cannontech.multispeak.deploy.service.Customer mspCustomer = mspObjectDao.getMspCustomer(meter, mspVendor);
        return mapToAccountInfo(mspCustomer);
    }
    
    public ServiceLocation getServiceLocation(int deviceId) throws RemoteException {
        MultispeakVendor mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        YukonMeter meter = meterDao.getYukonMeterForId(deviceId);
        com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc = mspObjectDao.getMspServiceLocation(meter, mspVendor);
        return mapToServicLocation(mspServLoc);
    }
    
    public AccountInfo mapToAccountInfo(com.cannontech.multispeak.deploy.service.Customer mspCustomer) {
        final AccountInfo accountInfo = new AccountInfo();
        accountInfo.setFirstName(mspCustomer.getFirstName());
        accountInfo.setLastName(mspCustomer.getLastName());
        accountInfo.setDbaName(mspCustomer.getDBAName());

        final Address address = new Address();
        address.setCityName(mspCustomer.getBillCity());
        address.setStateCode(mspCustomer.getBillState());
        address.setZipCode(mspCustomer.getBillZip());
        address.setLocationAddress1(mspCustomer.getBillAddr1());
        address.setLocationAddress2(mspCustomer.getBillAddr2());
        accountInfo.setAddress(address);
        
        return accountInfo;
    }
    
    public ServiceLocation mapToServicLocation(com.cannontech.multispeak.deploy.service.ServiceLocation mspServLoc) {
        final ServiceLocation servLoc = new ServiceLocation();
        servLoc.setAccountNumber(mspServLoc.getAccountNumber());
        servLoc.setCustomerNumber(mspServLoc.getCustID());
        servLoc.setSiteNumber(mspServLoc.getSiteID());
        servLoc.setMapLocation(mspServLoc.getGridLocation());
        
        final Address address = new Address();
        address.setCityName(mspServLoc.getServCity());
        address.setStateCode(mspServLoc.getServState());
        address.setZipCode(mspServLoc.getServZip());
        address.setLocationAddress1(mspServLoc.getServAddr1());
        address.setLocationAddress2(mspServLoc.getServAddr2());
        servLoc.setAddress(address);
        
        return servLoc;
    }    
}

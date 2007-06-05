package com.cannontech.multispeak.dao.impl;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.CustomerInfoDao;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.multispeak.client.MultispeakFuncs;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MultispeakDao;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;

public class MspCustomerInfoDaoImpl implements CustomerInfoDao {

    public MultispeakDao multispeakDao;
    public MultispeakFuncs multispeakFuncs;
    
    public void setMultispeakDao(MultispeakDao multispeakDao) {
        this.multispeakDao = multispeakDao;
    }

    public void setMultispeakFuncs(MultispeakFuncs multispeakFuncs) {
        this.multispeakFuncs = multispeakFuncs;
    }

    public com.cannontech.multispeak.service.Customer getMspCustomer(int deviceId) {
        
        MultispeakVendor mspVendor;
        mspVendor = multispeakDao.getMultispeakVendor(multispeakFuncs.getPrimaryCIS());
        
        LiteDeviceMeterNumber liteDevMeterNum = DaoFactory.getDeviceDao().getLiteDeviceMeterNumber(deviceId);
        
        String meterNumber = liteDevMeterNum.getMeterNumber();
        com.cannontech.multispeak.service.Customer mspCustomer = null;
            
        try {
            CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            mspCustomer = port.getCustomerByMeterNo(meterNumber);
            
        } catch (ServiceException e) {
            CTILogger.error("CB_MR service is not defined for company(" + mspVendor.getCompanyName()+ ") - getCustomerByMeterNo failed.");
            CTILogger.error("ServiceExceptionDetail: " + e.getMessage());

        } catch (RemoteException e) {
            CTILogger.error("TargetService for company(" + mspVendor.getCompanyName()+ ") - getCustomerByMeterNo failed.");
            CTILogger.error("RemoteExceptionDetail: "+e.getMessage());
        }
        return mspCustomer;
    }
    
    public Customer getCustomer(int deviceId) {        
        com.cannontech.multispeak.service.Customer mspCustomer = getMspCustomer(deviceId);
        return mapMspCustomerToCustomer(mspCustomer);
    }
    
    public Customer mapMspCustomerToCustomer(com.cannontech.multispeak.service.Customer mspCustomer) {
        Customer customer = new Customer();
        customer.setFirstName(mspCustomer.getFirstName());
        customer.setLastName(mspCustomer.getLastName());
        customer.setAddress1(mspCustomer.getBillAddr1());
        customer.setAddress2(mspCustomer.getBillAddr2());
        customer.setCity(mspCustomer.getBillCity());
        customer.setState(mspCustomer.getBillState());
        customer.setZip(mspCustomer.getBillZip());
        return customer;
    }
}

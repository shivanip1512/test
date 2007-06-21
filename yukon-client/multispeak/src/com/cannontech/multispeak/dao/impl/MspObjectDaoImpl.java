package com.cannontech.multispeak.dao.impl;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.Customer;
import com.cannontech.multispeak.service.ServiceLocation;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;

public class MspObjectDaoImpl implements MspObjectDao {

    public Customer getMspCustomer(Meter meter, MultispeakVendor mspVendor) {
        return getMspCustomer(meter.getMeterNumber(), mspVendor);
    }
    
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor) {

        com.cannontech.multispeak.service.Customer mspCustomer = null;
            
        try {
            CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            mspCustomer = port.getCustomerByMeterNo(meterNumber);
            
        } catch (ServiceException e) {
            CTILogger.error("CB_MR service is not defined for company(" + mspVendor.getCompanyName()+ ") - getCustomerByMeterNo failed.");
            CTILogger.error("ServiceExceptionDetail: " + e);

        } catch (RemoteException e) {
            CTILogger.error("TargetService for company(" + mspVendor.getCompanyName()+ ") - getCustomerByMeterNo failed.");
            CTILogger.error("RemoteExceptionDetail: " + e);
        }
        return mspCustomer;
    }

    public ServiceLocation getMspServiceLocation(Meter meter, MultispeakVendor mspVendor) {
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }
    
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) {

        com.cannontech.multispeak.service.ServiceLocation mspServLoc = null;
            
        try {
            CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
            mspServLoc = port.getServiceLocationByMeterNo(meterNumber);
            
        } catch (ServiceException e) {
            CTILogger.error("CB_MR service is not defined for company(" + mspVendor.getCompanyName()+ ") - getServiceLocationByMeterNo failed.");
            CTILogger.error("ServiceExceptionDetail: " + e);

        } catch (RemoteException e) {
            CTILogger.error("TargetService for company(" + mspVendor.getCompanyName()+ ") - getServiceLocationByMeterNo failed.");
            CTILogger.error("RemoteExceptionDetail: " + e);
        }
        return mspServLoc;
    }

}

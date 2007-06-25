package com.cannontech.multispeak.dao.impl;

import java.rmi.RemoteException;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.dao.MspObjectDao;
import com.cannontech.multispeak.service.CB_MRSoap_BindingStub;
import com.cannontech.multispeak.service.Customer;
import com.cannontech.multispeak.service.ServiceLocation;
import com.cannontech.multispeak.service.impl.MultispeakPortFactory;

public class MspObjectDaoImpl implements MspObjectDao {

    public Customer getMspCustomer(Meter meter, MultispeakVendor mspVendor) throws RemoteException {
        return getMspCustomer(meter.getMeterNumber(), mspVendor);
    }
    
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor) throws RemoteException {

        com.cannontech.multispeak.service.Customer mspCustomer = null;
            
        CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
        mspCustomer = port.getCustomerByMeterNo(meterNumber);
        return mspCustomer;
    }

    public ServiceLocation getMspServiceLocation(Meter meter, MultispeakVendor mspVendor) throws RemoteException{
        return getMspServiceLocation(meter.getMeterNumber(), mspVendor);
    }
    
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) throws RemoteException {

        com.cannontech.multispeak.service.ServiceLocation mspServLoc = null;
            
        CB_MRSoap_BindingStub port = MultispeakPortFactory.getCB_MRPort(mspVendor);
        mspServLoc = port.getServiceLocationByMeterNo(meterNumber);
        return mspServLoc;
    }

}

package com.cannontech.multispeak.dao;

import java.rmi.RemoteException;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.Customer;
import com.cannontech.multispeak.service.ServiceLocation;

public interface MspObjectDao {

    public Customer getMspCustomer(Meter meter, MultispeakVendor mspVendor) throws RemoteException;
    
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor) throws RemoteException;

    public ServiceLocation getMspServiceLocation(Meter meter, MultispeakVendor mspVendor) throws RemoteException;
    
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor) throws RemoteException;
}

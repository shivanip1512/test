package com.cannontech.multispeak.dao;

import com.cannontech.amr.meter.model.Meter;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.service.Customer;
import com.cannontech.multispeak.service.ServiceLocation;

public interface MspObjectDao {

    /**
     * Returns a Msp Customer for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found, 
     * an empty Customer object is returned.
     * @param meter The Meter to get the Customer information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Customer getMspCustomer(Meter meter, MultispeakVendor mspVendor);
    
    /**
     * Returns a Msp Customer for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found, 
     * an empty Customer object is returned.
     * @param meterNumber The meter number to get the Customer information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public Customer getMspCustomer(String meterNumber, MultispeakVendor mspVendor);

    /**
     * Returns a Msp ServiceLocation for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * @param meter The Meter to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getMspServiceLocation(Meter meter, MultispeakVendor mspVendor);

    /**
     * Returns a Msp ServiceLocation for the meterNo.
     * If the interface/method is not supported by mspVendor, or if no object is found,
     * an empty ServiceLocation object is returned.
     * @param meterNumber The meter number to get the ServiceLocation information for.
     * @param mspVendor The Multispeak Vendor to ask for the information from.
     * @return
     */
    public ServiceLocation getMspServiceLocation(String meterNumber, MultispeakVendor mspVendor);
}

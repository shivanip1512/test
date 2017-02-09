package com.cannontech.multispeak.service.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.multispeak.Customer;
import com.cannontech.user.YukonUserContext;

public interface MultispeakCustomerInfoService {

    /**
     * Build a List of Customer phone numbers
     * 
     * @param mspCustomer
     * @param yukonUserContext
     */
    List<String> getPhoneNumbers(Customer mspCustomer, YukonUserContext userContext);

    /**
     * Build a List of Customer email addresses
     * 
     * @param mspCustomer
     * @param yukonUserContext
     */
    List<String> getEmailAddresses(Customer mspCustomer, YukonUserContext userContext);
}

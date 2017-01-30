package com.cannontech.multispeak.service.v3;

import java.util.List;

import com.cannontech.msp.beans.v3.Customer;
import com.cannontech.user.YukonUserContext;

public interface MultispeakCustomerInfoService {

	/**
	 * Build a List of Customer phone numbers
	 * @param mspCustomer
	 * @param yukonUserContext
	 * @return List
	 */
    List<String> getPhoneNumbers(Customer mspCustomer, YukonUserContext userContext);
    
    /**
     * Build a List of Customer email addresses
     * @param mspCustomer
     * @param yukonUserContext
     * @return List
     */
    List<String> getEmailAddresses(Customer mspCustomer, YukonUserContext userContext);
}

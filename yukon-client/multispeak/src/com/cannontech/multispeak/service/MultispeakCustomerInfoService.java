package com.cannontech.multispeak.service;

import java.util.List;

import com.cannontech.multispeak.deploy.service.Customer;
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

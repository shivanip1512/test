package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.Customer;
import com.cannontech.msp.beans.v4.EMailAddress;
import com.cannontech.msp.beans.v4.PhoneNumber;
import com.cannontech.user.YukonUserContext;

public interface MultispeakCustomerInfoService {

    /**
     * Build a List of Customer phone numbers
     * 
     * @param phoneNumbers
     * @param yukonUserContext
     */
    public List<String> getPhoneNumbers(List<PhoneNumber> list, YukonUserContext userContext);

    /**
     * Build a List of Customer email addresses
     * 
     * @param emailAddresses
     * @param yukonUserContext
     */
    List<String> getEmailAddresses(List<EMailAddress> list, YukonUserContext userContext);
}

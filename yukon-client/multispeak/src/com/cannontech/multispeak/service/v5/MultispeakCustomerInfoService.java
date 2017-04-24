package com.cannontech.multispeak.service.v5;

import java.util.List;

import com.cannontech.msp.beans.v5.commontypes.EMailAddresses;
import com.cannontech.msp.beans.v5.commontypes.PhoneNumbers;
import com.cannontech.user.YukonUserContext;

public interface MultispeakCustomerInfoService {

    /**
     * Build a List of Customer phone numbers
     * 
     * @param phoneNumbers
     * @param yukonUserContext
     */
    public List<String> getPhoneNumbers(PhoneNumbers phoneNumbers, YukonUserContext userContext);

    /**
     * Build a List of Customer email addresses
     * 
     * @param emailAddresses
     * @param yukonUserContext
     */
    List<String> getEmailAddresses(EMailAddresses emailAddresses, YukonUserContext userContext);
}

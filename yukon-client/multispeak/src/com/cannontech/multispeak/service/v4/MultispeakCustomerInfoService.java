package com.cannontech.multispeak.service.v4;

import java.util.List;

import com.cannontech.msp.beans.v4.ContactInfo;
import com.cannontech.user.YukonUserContext;

public interface MultispeakCustomerInfoService {

    /**
     * Build a List of Customer phone numbers
     * @param ContactInfo
     * @param yukonUserContext
     */
    public List<String> getPhoneNumbers(ContactInfo contactInfo, YukonUserContext userContext);

    /**
     * Build a List of Customer email addresses
     * @param ContactInfo
     * @param yukonUserContext
     */
    List<String> getEmailAddresses(ContactInfo contactInfo, YukonUserContext userContext);

}

package com.cannontech.multispeak.service.impl.v4;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.msp.beans.v4.ContactInfo;
import com.cannontech.msp.beans.v4.Customer;
import com.cannontech.msp.beans.v4.EMailAddress;
import com.cannontech.msp.beans.v4.PhoneNumber;
import com.cannontech.multispeak.service.v4.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class MultispeakCustomerInfoServiceImpl implements MultispeakCustomerInfoService {

    @Override
    public List<String> getPhoneNumbers(ContactInfo contactInfo,
                                        YukonUserContext userContext) {
        List<String> phoneNumbers = Lists.newArrayList();
        if (contactInfo.getPhoneList() == null) return phoneNumbers;
        for (PhoneNumber p : contactInfo.getPhoneList().getPhoneNumber()) {
            StringBuilder fullPhone =  new StringBuilder();
            
            if (p != null) {
                if (p.getPhone() != null) {
                    fullPhone.append(p.getPhone());
                }
                if (p.getPhoneType() != null) {
                    fullPhone.append(p.getPhoneType());
                }
                if (!StringUtils.isBlank(fullPhone)) {
                    phoneNumbers.add(fullPhone.toString());
                }
            }
        }
        return phoneNumbers;
    }

    @Override
    public List<String> getEmailAddresses(ContactInfo contactInfo,
                                          YukonUserContext userContext) {
        List<String> emails = Lists.newArrayList();
        
        if (contactInfo.getEMailList() == null) return emails;
        if (contactInfo.getEMailList() != null) {
            for (EMailAddress e : contactInfo.getEMailList().getEMailAddress()) {
                StringBuilder fullEmail =  new StringBuilder();
                if (e.getEMail() != null) {
                    fullEmail.append(e.getEMail());
                }
                if (fullEmail.length() > 0) {
                    emails.add(fullEmail.toString());
                }
            }
        }
        return emails;
    }

}

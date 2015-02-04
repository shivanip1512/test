package com.cannontech.multispeak.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.multispeak.deploy.service.ContactInfo;
import com.cannontech.multispeak.deploy.service.Customer;
import com.cannontech.multispeak.deploy.service.EMailAddress;
import com.cannontech.multispeak.deploy.service.PhoneNumber;
import com.cannontech.multispeak.service.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class MultispeakCustomerInfoServiceImpl implements MultispeakCustomerInfoService {

    @Override
    public List<String> getPhoneNumbers(Customer mspCustomer,
                                        YukonUserContext userContext) {
        List<String> phoneNumbers = Lists.newArrayList();
        ContactInfo contactInfo = mspCustomer.getContactInfo();
        if (contactInfo == null || contactInfo.getPhoneList() == null) return phoneNumbers;
        for (PhoneNumber p : contactInfo.getPhoneList()) {
            String fullPhone = "";
            if (p.getPhone() != null) {
                fullPhone += p.getPhone();
            }
            if (p.getPhoneType() != null && p.getPhoneType().getValue() != null) {
                fullPhone += " " + p.getPhoneType().getValue();
            }
            if (!StringUtils.isBlank(fullPhone)) {
                phoneNumbers.add(fullPhone);
            }
        }
        return phoneNumbers;
    }

    @Override
    public List<String> getEmailAddresses(Customer mspCustomer,
                                          YukonUserContext userContext) {
        List<String> emails = Lists.newArrayList();
        ContactInfo contactInfo = mspCustomer.getContactInfo();
        if (contactInfo == null || contactInfo.getEMailList() == null) return emails;
        if (contactInfo.getEMailList() != null) {
            for (EMailAddress e : contactInfo.getEMailList()) {
                String fullEmail = "";
                if (e.getEMail() != null) {
                    fullEmail += e.getEMail();
                }
                if (!StringUtils.isBlank(fullEmail)) {
                    emails.add(fullEmail);
                }
            }
        }
        return emails;
    }

}

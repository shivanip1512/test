package com.cannontech.multispeak.service.impl.v5;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import com.cannontech.msp.beans.v5.commontypes.EMailAddress;
import com.cannontech.msp.beans.v5.commontypes.PhoneNumber;
import com.cannontech.msp.beans.v5.multispeak.ContactInfo;
import com.cannontech.msp.beans.v5.multispeak.Customer;
import com.cannontech.multispeak.service.v5.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class MultispeakCustomerInfoServiceImpl implements MultispeakCustomerInfoService {

    @Override
    public List<String> getPhoneNumbers(Customer mspCustomer, YukonUserContext userContext) {
        List<String> phoneNumbers = Lists.newArrayList();
        ContactInfo contactInfo = mspCustomer.getContactInfo();
        if (contactInfo == null || contactInfo.getPhoneNumbers() == null)
            return phoneNumbers;
        for (PhoneNumber p : contactInfo.getPhoneNumbers().getPhoneNumber()) {
            StringBuffer fullPhone = new StringBuffer();
            if (p.getPhone() != null) {
                fullPhone.append(p.getPhone().getAreaCode());
                fullPhone.append(p.getPhone().getLocalNumber());
            }
            if (p.getPhoneType() != null && p.getPhoneType() != null) {
                fullPhone.append(StringUtils.SPACE);
                fullPhone.append(p.getPhoneType().getValue());
            }
            if (!StringUtils.isBlank(fullPhone)) {
                phoneNumbers.add(fullPhone.toString());
            }
        }
        return phoneNumbers;
    }

    @Override
    public List<String> getEmailAddresses(Customer mspCustomer, YukonUserContext userContext) {
        List<String> emails = Lists.newArrayList();
        ContactInfo contactInfo = mspCustomer.getContactInfo();
        if (contactInfo == null || contactInfo.getEMailAddresses() == null)
            return emails;
        if (contactInfo.getEMailAddresses() != null) {
            for (EMailAddress e : contactInfo.getEMailAddresses().getEMailAddress()) {
                StringBuffer fullEmail = new StringBuffer();
                if (e.getEMail() != null) {
                    fullEmail.append(e.getEMail());

                    if (e.getEMailType() != null) {
                        fullEmail.append(StringUtils.SPACE);
                        fullEmail.append(e.getEMailType().getValue());
                    }
                }
                if (!StringUtils.isBlank(fullEmail)) {
                    emails.add(fullEmail.toString());
                }
            }
        }
        return emails;
    }

}

package com.cannontech.multispeak.service.impl.v5;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.msp.beans.v5.commontypes.EMailAddress;
import com.cannontech.msp.beans.v5.commontypes.EMailAddresses;
import com.cannontech.msp.beans.v5.commontypes.PhoneNumber;
import com.cannontech.msp.beans.v5.commontypes.PhoneNumbers;
import com.cannontech.multispeak.service.v5.MultispeakCustomerInfoService;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class MultispeakCustomerInfoServiceImpl implements MultispeakCustomerInfoService {
    @Autowired private PhoneNumberFormattingService phoneNumberFormattingService;

    @Override
    public List<String> getPhoneNumbers(PhoneNumbers phoneNumbers, YukonUserContext userContext) {

        List<String> phoneNumbersList = null;
        if (phoneNumbers != null) {
            phoneNumbersList = Lists.newArrayList();
            for (PhoneNumber p : phoneNumbers.getPhoneNumber()) {
                StringBuffer fullPhone = new StringBuffer();
                if (p.getPhoneType() != null && p.getPhoneType().getValue() != null) {
                    fullPhone.append(p.getPhoneType().getValue());
                    fullPhone.append(":" + StringUtils.SPACE);
                }
                if (p.getPhone() != null) {
                    fullPhone.append(phoneNumberFormattingService.formatPhone(p.getPhone().getAreaCode(),
                            p.getPhone().getLocalNumber()));
                }
                if (!StringUtils.isBlank(fullPhone)) {
                    phoneNumbersList.add(fullPhone.toString());
                }
            }
        }
        return phoneNumbersList;
    }

    @Override
    public List<String> getEmailAddresses(EMailAddresses emailAddresses, YukonUserContext userContext) {
        List<String> emails = null;

        if (emailAddresses != null) {
            emails = Lists.newArrayList();
            for (EMailAddress e : emailAddresses.getEMailAddress()) {
                StringBuffer fullEmail = new StringBuffer();
                if (e.getEMail() != null) {
                    if (e.getEMailType() != null) {
                        fullEmail.append(e.getEMailType().getValue());
                        fullEmail.append(":" + StringUtils.SPACE);
                    }
                    fullEmail.append(e.getEMail());
                }
                if (!StringUtils.isBlank(fullEmail)) {
                    emails.add(fullEmail.toString());
                }
            }
        }
        return emails;
    }

}

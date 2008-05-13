package com.cannontech.core.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;

public class PhoneNumberFormattingServiceImpl implements PhoneNumberFormattingService {
    private static final String keyPrefix = "yukon.common.phoneNumberFormatting.pattern.";
    private static final char replaceChar = '?';
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public String formatPhoneNumber(final String phoneNumber, final YukonUserContext yukonUserContext) {
        String cleanPhoneNumber = removeNonDigits(phoneNumber);

        if (StringUtils.isBlank(cleanPhoneNumber)) return "";
        
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        String code = keyPrefix + cleanPhoneNumber.length();
        String pattern = messageSourceAccessor.getMessage(code);

        String formattedPhoneNumber = formatToPattern(cleanPhoneNumber, pattern);
        
        return formattedPhoneNumber;
    }
    
    private String removeNonDigits(String phoneNumber) {
        String result = phoneNumber.replaceAll("\\D", "");
        return result;
    }

    private String formatToPattern(String phoneNumber, String pattern) {
        final StringBuilder sb = new StringBuilder(pattern.length());
        
        int index = 0;
        for (final Character c : pattern.toCharArray()) {
            Character appCharacter = (c.equals(replaceChar)) ? phoneNumber.charAt(index++) : c;
            sb.append(appCharacter);
        }
        
        String result = sb.toString();
        return result;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}

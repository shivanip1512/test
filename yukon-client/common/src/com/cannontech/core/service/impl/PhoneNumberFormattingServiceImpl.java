package com.cannontech.core.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.NoSuchMessageException;

import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.user.YukonUserContext;
import com.cannontech.util.PhoneNumber;

public class PhoneNumberFormattingServiceImpl implements PhoneNumberFormattingService {
    
	private static final String keyPrefix = "yukon.common.phoneNumberFormatting.pattern.";
    private static final char replaceChar = '?';
    private static final char[] validChars = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', 'x', '(', ')', ' '};
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    
    @Override
    public String formatPhoneNumber(final String phoneNumber, final YukonUserContext yukonUserContext) {
        String formattedPhoneNumber = phoneNumber;
        String cleanPhoneNumber = removeNonDigits(phoneNumber);

        if (StringUtils.isBlank(cleanPhoneNumber)) return "";
        
        MessageSourceAccessor messageSourceAccessor = 
            messageSourceResolver.getMessageSourceAccessor(yukonUserContext);
        
        String code = keyPrefix + cleanPhoneNumber.length();
        try {
            String pattern = messageSourceAccessor.getMessage(code);
            formattedPhoneNumber = formatToPattern(cleanPhoneNumber, pattern);
        } catch (NoSuchMessageException e) {
            // defaults to returning phone number as is, no formatting
        }
        return formattedPhoneNumber;
    }
    
    @Override
    public boolean isHasInvalidCharacters(String phoneNumber) {
    	
    	return !StringUtils.containsOnly(phoneNumber, validChars);
    }
    
    @Override
    public String strip(String phoneNumber) {
        
        return PhoneNumber.strip(phoneNumber);
    }
    
    @Override
    public String removeNonDigits(String phoneNumber) {
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
    

    /**
     * Returns a formatted phone number of (111) 234-5678
     * Where 111 is the areaCode and 234-5678 is phone
     */
    public String formatPhone(String areaCode, String phone) {
        String formattedPhone = "";
        if (!StringUtils.isBlank(areaCode)) {
            formattedPhone += "(" + areaCode + ") ";
        }
        if (!StringUtils.isBlank(phone)) {
            formattedPhone += phone;
        }
        return formattedPhone;
    }
    
    @Autowired
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }
    
}

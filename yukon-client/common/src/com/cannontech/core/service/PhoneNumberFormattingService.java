package com.cannontech.core.service;

import com.cannontech.user.YukonUserContext;

/**
 * Please see the com/cannontech/yukon/common/phoneNumberFormatting.xml file
 * for the actual format strings.
 * 
 * @author nmeverden
 *
 */
public interface PhoneNumberFormattingService {

    public String formatPhoneNumber(String phoneNumber, YukonUserContext yukonUserContext);
    
    public boolean isHasInvalidCharacters(String phoneNumber);
    
    public String strip(String phoneNumber);
    
    public String removeNonDigits(String phoneNumber);
    
}

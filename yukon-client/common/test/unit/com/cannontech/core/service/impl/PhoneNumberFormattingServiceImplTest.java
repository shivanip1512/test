package com.cannontech.core.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.StaticMessageSource;

import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.i18n.YukonUserContextMessageSourceResolverMock;

public class PhoneNumberFormattingServiceImplTest {
    private PhoneNumberFormattingService formattingService;
    
    @BeforeEach
    public void setUp() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("yukon.common.phoneNumberFormatting.pattern.7", Locale.US, "???-????");
        messageSource.addMessage("yukon.common.phoneNumberFormatting.pattern.10", Locale.US, "(???) ???-????");
        messageSource.addMessage("yukon.common.phoneNumberFormatting.pattern.11", Locale.US, "? (???) ???-????");
        
        YukonUserContextMessageSourceResolverMock messageSourceResolver = new YukonUserContextMessageSourceResolverMock();
        messageSourceResolver.setMessageSource(messageSource);
        
        PhoneNumberFormattingServiceImpl serviceImpl = new PhoneNumberFormattingServiceImpl();
        serviceImpl.setMessageSourceResolver(messageSourceResolver);
        formattingService = serviceImpl;
    }
    
    @AfterEach
    public void tearDown() {
        formattingService = null;
    }
    
    @Test
    public void test_format_nonDigits() {
        String phoneNumber = "not a valid phone number";
        String expectedPhoneNumber = "";
        
        String formattedNumber = formattingService.formatPhoneNumber(phoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
        
        phoneNumber = "";
        formattedNumber = formattingService.formatPhoneNumber(phoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
    }
    
    @Test
    public void test_format_LT_7_Digits() {
        String phoneNumber = "12345";
        String expectedPhoneNumber = "12345";
        
        // unformatted phone number
        String formattedNumber = formattingService.formatPhoneNumber(phoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
        
        // preformatted phone number
        formattedNumber = formattingService.formatPhoneNumber(expectedPhoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
    }    
    
    @Test
    public void test_format_7_Digits() {
        String phoneNumber = "1234567";
        String expectedPhoneNumber = "123-4567";
        
        // unformatted phone number
        String formattedNumber = formattingService.formatPhoneNumber(phoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
        
        // preformatted phone number
        formattedNumber = formattingService.formatPhoneNumber(expectedPhoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
    }
    
    @Test
    public void test_format_10_Digits() {
        String phoneNumber = "0123456789";
        String expectedPhoneNumber = "(012) 345-6789";
        
        // unformatted phone number
        String formattedNumber = formattingService.formatPhoneNumber(phoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
        
        // preformatted phone number
        formattedNumber = formattingService.formatPhoneNumber(expectedPhoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
    }
    
    @Test
    public void test_format_11_Digits() {
        String phoneNumber = "00123456789";
        String expectedPhoneNumber = "0 (012) 345-6789";
        
        // unformatted phone number
        String formattedNumber = formattingService.formatPhoneNumber(phoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
        
        // preformatted phone number
        formattedNumber = formattingService.formatPhoneNumber(expectedPhoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
    }
    
    @Test
    public void test_format_GT_11_Digits() {
        String phoneNumber = "82(3456)789012343";
        String expectedPhoneNumber = "82(3456)789012343";
        
        // unformatted phone number
        String formattedNumber = formattingService.formatPhoneNumber(phoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
        
        // preformatted phone number
        formattedNumber = formattingService.formatPhoneNumber(expectedPhoneNumber, null);
        assertEquals(expectedPhoneNumber, formattedNumber);
    }    
    
}

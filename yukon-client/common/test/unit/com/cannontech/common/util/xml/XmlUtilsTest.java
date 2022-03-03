package com.cannontech.common.util.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;

public class XmlUtilsTest {
    
    @Test
    public void convertEnumToXmlRepresentionTest() {
        String xmlRepresention = XmlUtils.toXmlRepresentation(SchedulableThermostatType.UTILITY_PRO_G2);
        
        assertEquals("UtilityPRO G2", xmlRepresention);
    }
    
    @Test
    public void convertEnumToXmlRepresentionTest_NoneXMLRepresentationEnum() {
        String xmlRepresention = XmlUtils.toXmlRepresentation(PasswordPolicyError.INVALID_PASSWORD_LENGTH);
        
        assertEquals("Invalid Password Length", xmlRepresention);
    }
}
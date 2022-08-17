package com.cannontech.common.util.xml;

import org.junit.Assert;
import org.junit.Test;

import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;

public class XmlUtilsTest {
    
    @Test
    public void convertEnumToXmlRepresentionTest() {
        String xmlRepresention = XmlUtils.toXmlRepresentation(SchedulableThermostatType.UTILITY_PRO_G2);
        
        Assert.assertEquals("UtilityPRO G2", xmlRepresention);
    }
    
    @Test
    public void convertEnumToXmlRepresentionTest_NoneXMLRepresentationEnum() {
        String xmlRepresention = XmlUtils.toXmlRepresentation(PasswordPolicyError.INVALID_PASSWORD_LENGTH);
        
        Assert.assertEquals("Invalid Password Length", xmlRepresention);
    }
}
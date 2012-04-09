package com.cannontech.common.util.xml;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Test;

import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;

public class YukonXPathTemplateTest {
    
    
    /**
     * <testElement>
     *      <temperatureElement unit="F">85</temperatureElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsTemperature_unitF_withTemperature() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("temperatureElement");
        booleanElement.setAttribute("unit", "F");
        booleanElement.addContent("85");
        testElement.addContent(booleanElement);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Temperature temperatureValue = yukonTemplate.evaluateAsTemperature("/testElement/temperatureElement");
        
        Assert.assertNotNull(temperatureValue);
        Assert.assertEquals(TemperatureUnit.FAHRENHEIT, temperatureValue.toFahrenheit().getUnit());
        Assert.assertEquals(85.0, temperatureValue.toFahrenheit().getValue());
    }
    
}
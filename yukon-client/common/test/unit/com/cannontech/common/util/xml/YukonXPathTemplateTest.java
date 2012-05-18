package com.cannontech.common.util.xml;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Test;

import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;

public class YukonXPathTemplateTest {
    
    /**
     * <testElement>
     *      <temperatureElement unit="F">abc</temperatureElement>
     * </testElement>
     */
    @Test(expected=NumberFormatException.class)
    public void testEvaluateAsTemperature_unitF_withoutTemperature() {
        Element testElement = new Element("testElement");

        Element temperatureElement = new Element("temperatureElement");
        temperatureElement.setAttribute("unit", "F");
        temperatureElement.addContent("abc");
        testElement.addContent(temperatureElement);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        yukonTemplate.evaluateAsTemperature("/testElement/temperatureElement");
        
    }
    
    /**
     * <testElement>
     *      <temperatureElement unit="F">85</temperatureElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsTemperature_unitF_withTemperature() {
        Element testElement = new Element("testElement");

        Element temperatureElement = new Element("temperatureElement");
        temperatureElement.setAttribute("unit", "F");
        temperatureElement.addContent("85");
        testElement.addContent(temperatureElement);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Temperature temperatureValue = yukonTemplate.evaluateAsTemperature("/testElement/temperatureElement");
        
        Assert.assertNotNull(temperatureValue);
        Assert.assertEquals(TemperatureUnit.FAHRENHEIT, temperatureValue.toFahrenheit().getUnit());
        Assert.assertEquals(85.0, temperatureValue.toFahrenheit().getValue());
    }
    
    /**
     * <testElement>
     *      <temperatureElement unit="C">26</temperatureElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsTemperature_unitC_withTemperature() {
        Element testElement = new Element("testElement");

        Element temperatureElement = new Element("temperatureElement");
        temperatureElement.setAttribute("unit", "C");
        temperatureElement.addContent("26");
        testElement.addContent(temperatureElement);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Temperature temperatureValue = yukonTemplate.evaluateAsTemperature("/testElement/temperatureElement");
        
        Assert.assertNotNull(temperatureValue);
        Assert.assertEquals(TemperatureUnit.CELSIUS, temperatureValue.toCelsius().getUnit());
        Assert.assertEquals(26.0, temperatureValue.toCelsius().getValue());
    }
    
}
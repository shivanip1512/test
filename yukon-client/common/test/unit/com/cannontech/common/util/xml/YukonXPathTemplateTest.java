package com.cannontech.common.util.xml;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Test;
import org.springframework.test.annotation.ExpectedException;

import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;

public class YukonXPathTemplateTest {
    
    /**
     * <testElement>
     *      <temperatureElement unit="F"></temperatureElement>
     * </testElement>
     */
    @Test(expected=NumberFormatException.class)
    public void testEvaluateAsTemperature_unitF_withoutTemperature() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("temperatureElement");
        booleanElement.setAttribute("unit", "F");
        testElement.addContent(booleanElement);

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
    
    /**
     * <testElement>
     *      <temperatureElement unit="C">26</temperatureElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsTemperature_unitC_withTemperature() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("temperatureElement");
        booleanElement.setAttribute("unit", "C");
        booleanElement.addContent("26");
        testElement.addContent(booleanElement);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Temperature temperatureValue = yukonTemplate.evaluateAsTemperature("/testElement/temperatureElement");
        
        Assert.assertNotNull(temperatureValue);
        Assert.assertEquals(TemperatureUnit.CELSIUS, temperatureValue.toCelsius().getUnit());
        Assert.assertEquals(26.0, temperatureValue.toCelsius().getValue());
    }
    
}
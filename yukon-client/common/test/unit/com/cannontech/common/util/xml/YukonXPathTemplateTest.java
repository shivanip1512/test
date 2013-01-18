package com.cannontech.common.util.xml;

import junit.framework.Assert;

import org.jdom.Element;
import org.joda.time.LocalTime;
import org.junit.Test;
import org.springframework.xml.xpath.XPathException;

import com.cannontech.common.temperature.Temperature;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;

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

    /**
     * <testElement>
     *      <enumElement thermostatScheduleMode="Weekday Weekend" />
     * </testElement>
     */
    @Test
    public void testEvaluateAsEnum_attribute() {
        Element testElement = createEnumElement("Weekday Weekend", null);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        ThermostatScheduleMode thermostatScheduleMode = yukonTemplate.evaluateAsEnum("/testElement/enumElement/@thermostatScheduleMode", ThermostatScheduleMode.class);
        
        Assert.assertNotNull(thermostatScheduleMode);
        Assert.assertEquals(ThermostatScheduleMode.WEEKDAY_WEEKEND, thermostatScheduleMode);
    }

    /**
     * <testElement>
     *      <enumElement>WeekDay_WeekEnd<enumElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsEnum_element() {
        Element testElement = createEnumElement(null, "WeekDay_WeekEnd");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        ThermostatScheduleMode thermostatScheduleMode = yukonTemplate.evaluateAsEnum("/testElement/enumElement", ThermostatScheduleMode.class);
        
        Assert.assertNotNull(thermostatScheduleMode);
        Assert.assertEquals(ThermostatScheduleMode.WEEKDAY_WEEKEND, thermostatScheduleMode);
    }

    /**
     * <testElement>
     *      <enumElement />
     * </testElement>
     */
    @Test
    public void testEvaluateAsEnum_nullElement() {
        Element testElement = createEnumElement(null, null);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        ThermostatScheduleMode thermostatScheduleMode = yukonTemplate.evaluateAsEnum("/testElement/enumElement", ThermostatScheduleMode.class);
        
        Assert.assertNull(thermostatScheduleMode);
    }

    /**
     * <testElement>
     *      <enumElement>WeekDay_WeekEndddd<enumElement>
     * </testElement>
     */
    @Test(expected=XPathException.class)
    public void testEvaluateAsEnum_badEnumElement() {
        Element testElement = createEnumElement(null, "WeekDay_WeekEndddd");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        ThermostatScheduleMode thermostatScheduleMode = yukonTemplate.evaluateAsEnum("/testElement/enumElement", ThermostatScheduleMode.class);
        
        Assert.assertNotNull(thermostatScheduleMode);
        Assert.assertEquals(ThermostatScheduleMode.WEEKDAY_WEEKEND, thermostatScheduleMode);
    }

    /**
     * <testElement>
     *      <enumElement>19:24<enumElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLocalTime() {
        Element testElement = createLocalTimeElement("19:24");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        Assert.assertNotNull(localTime);
        Assert.assertEquals(new LocalTime(19, 24), localTime);
    }
    
    /**
     * <testElement>
     *      <enumElement>5:42<enumElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLocalTime_singleHourDigit() {
        Element testElement = createLocalTimeElement("5:42");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        Assert.assertNotNull(localTime);
        Assert.assertEquals(new LocalTime(5, 42), localTime);
    }

    /**
     * <testElement>
     *      <enumElement><enumElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLocalTime_nullElement() {
        Element testElement = createLocalTimeElement(null);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        Assert.assertNull(localTime);
    }

    /**
     * <testElement>
     *      <enumElement>19:4<enumElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLocalTime_singleMinutesDigit() {
        Element testElement = createLocalTimeElement("19:4");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        Assert.assertNotNull(localTime);
        Assert.assertEquals(new LocalTime(19, 4), localTime);
    }

    /**
     * <testElement>
     *      <enumElement>42:4<enumElement>
     * </testElement>
     */
    @Test(expected=XPathException.class)
    public void testEvaluateAsLocalTime_invalidDate() {
        Element testElement = createLocalTimeElement("42:4");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        Assert.assertNotNull(localTime);
        Assert.assertEquals(new LocalTime(), localTime);
    }

    /**
     *  <testElement>
     *      <enumElement thermostatScheduleMode="${attributeValue}">${elementValue}</enumElement>
     * </testElement>
     */
    private Element createEnumElement(String attributeValue, String elementValue) {
        Element testElement = new Element("testElement");

        Element temperatureElement = new Element("enumElement");
        if (attributeValue != null) {
            temperatureElement.setAttribute("thermostatScheduleMode", attributeValue);
        }

        temperatureElement.setText(elementValue);
        testElement.addContent(temperatureElement);
        
        return testElement;
    }

    /**
     *  <testElement>
     *      <localTimeElement>${elementValue}</localTimeElement>
     *  </testElement>
     */
    private Element createLocalTimeElement(String elementValue) {
        Element testElement = new Element("testElement");

        Element temperatureElement = new Element("localTimeElement");
        temperatureElement.setText(elementValue);
        testElement.addContent(temperatureElement);
        
        return testElement;
    }
}
package com.cannontech.common.util.xml;

import static org.junit.Assert.*;

import org.jdom2.Element;
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
        
        assertNotNull(temperatureValue);
        assertEquals(TemperatureUnit.FAHRENHEIT, temperatureValue.toFahrenheit().getUnit());
        assertEquals(85.0, temperatureValue.toFahrenheit().getValue(), 0.001);
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
        
        assertNotNull(temperatureValue);
        assertEquals(TemperatureUnit.CELSIUS, temperatureValue.toCelsius().getUnit());
        assertEquals(26.0, temperatureValue.toCelsius().getValue(), 0.001);
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
        
        assertNotNull(thermostatScheduleMode);
        assertEquals(ThermostatScheduleMode.WEEKDAY_WEEKEND, thermostatScheduleMode);
    }

    /**
     * <testElement>
     *      <enumElement thermostatScheduleMode="Weekday Saturday Sunday" />
     * </testElement>
     */
    @Test
    public void testEvaluateAsEnum_usingXmlRepresentationAnnotation() {
        Element testElement = createEnumElement("Weekday Saturday Sunday", null);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        ThermostatScheduleMode thermostatScheduleMode = yukonTemplate.evaluateAsEnum("/testElement/enumElement/@thermostatScheduleMode", ThermostatScheduleMode.class);
        
        assertNotNull(thermostatScheduleMode);
        assertEquals(ThermostatScheduleMode.WEEKDAY_SAT_SUN, thermostatScheduleMode);
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
        
        assertNotNull(thermostatScheduleMode);
        assertEquals(ThermostatScheduleMode.WEEKDAY_WEEKEND, thermostatScheduleMode);
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
        try {
            
            yukonTemplate.evaluateAsEnum("/testElement/enumElement", ThermostatScheduleMode.class);
            fail(); // we should never get here b/c we should throw an exception
        } catch (Exception e) {
            // we should get into here
        }
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
        yukonTemplate.evaluateAsEnum("/testElement/enumElement", ThermostatScheduleMode.class);
    }

    /**
     * <testElement>
     *      <localTimeElement>19:24</localTimeElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLocalTime() {
        Element testElement = createLocalTimeElement("19:24");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        assertNotNull(localTime);
        assertEquals(new LocalTime(19, 24), localTime);
    }
    
    /**
     * <testElement>
     *      <localTimeElement>5:42</localTimeElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLocalTime_singleHourDigit() {
        Element testElement = createLocalTimeElement("5:42");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        assertNotNull(localTime);
        assertEquals(new LocalTime(5, 42), localTime);
    }

    /**
     * <testElement>
     *      <localTimeElement></localTimeElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLocalTime_nullElement() {
        Element testElement = createLocalTimeElement(null);

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        assertNull(localTime);
    }

    /**
     * <testElement>
     *      <localTimeElement>19:4</localTimeElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLocalTime_singleMinutesDigit() {
        Element testElement = createLocalTimeElement("19:4");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        assertNotNull(localTime);
        assertEquals(new LocalTime(19, 4), localTime);
    }

    /**
     * <testElement>
     *      <localTimeElement>42:4</localTimeElement>
     * </testElement>
     */
    @Test(expected=XPathException.class)
    public void testEvaluateAsLocalTime_invalidDate() {
        Element testElement = createLocalTimeElement("42:4");

        YukonXPathTemplate yukonTemplate = YukonXml.getXPathTemplateForElement(testElement);
        LocalTime localTime= yukonTemplate.evaluateAsLocalTime("/testElement/localTimeElement");
        
        assertNotNull(localTime);
        assertEquals(new LocalTime(), localTime);
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

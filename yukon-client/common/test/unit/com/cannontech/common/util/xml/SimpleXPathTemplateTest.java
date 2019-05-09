package com.cannontech.common.util.xml;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.jdom2.Element;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import com.cannontech.common.exception.InvalidDateFormatException;
import com.google.common.collect.Lists;

public class SimpleXPathTemplateTest {

    private static final DateTimeZone centralTimeZone = DateTimeZone.forOffsetHoursMinutes(-6, 0);
    private static final DateTimeFormatter dateTimeFormmater = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss").withZone(centralTimeZone);
    
    /**
     * Creates a jdom element that can be used for parsing tests.
     * 
     * @param elementType - If this is null, the element wont be added
     * @param elementContent - If this is null, the element will be empty
     * 
     * @return
     * <testElement>
     *     <${elementType}>${elementContent}</${elementType}>
     * </testElement>
     */
    private Element generateElement(String elementType, String... elementContentList) {
        Element testElement = new Element("testElement");
        if (elementType != null) {
            if (elementContentList.length == 0) {
                Element element = new Element(elementType);
                testElement.addContent(element);
                return testElement;
            }
            
            for (String elementContent : elementContentList) {
                Element element = new Element(elementType);
                element.addContent(elementContent);
                testElement.addContent(element);
            }
        }
        return testElement;
    }
    
/************************ evaluateAsBoolean Tests ***************/
    /**
     * <testElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_noNode() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(booleanValue, false);
    }
    
    /**
     * Empty element with default value of true
     * <testElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_noNodeWithDefault() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement", true);
        
        Assert.assertEquals(booleanValue, true);
    }
    
    /**
     * <testElement>
     *      <booleanElement/>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_emptyNode() {
        Element testElement = generateElement("booleanElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(true, booleanValue.booleanValue());
    }
    
    /**
     * <testElement>
     *      <booleanElement>true</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_trueNode() {
        Element testElement = generateElement("booleanElement", "true");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(true, booleanValue.booleanValue());
    }

    /**
     * <testElement>
     *      <booleanElement>TrUe</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_TrUeNode() {
        Element testElement = generateElement("booleanElement", "TrUe");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(true, booleanValue.booleanValue());
    }

    /**
     * <testElement>
     *      <booleanElement>1</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_trueNodeUsingBinaryOne() {
        Element testElement = generateElement("booleanElement", "1");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(true, booleanValue.booleanValue());
    }
    
    /**
     * <testElement>
     *      <booleanElement>false</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_falseNode() {
        Element testElement = generateElement("booleanElement", "false");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(false, booleanValue.booleanValue());
    }

    /**
     * <testElement>
     *      <booleanElement>FaLsE</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_FaLsENode() {
        Element testElement = generateElement("booleanElement", "FaLsE");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(false, booleanValue.booleanValue());
    }
    
    /**
     * <testElement>
     *      <booleanElement>1</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_falseNodeUsingBinaryZero() {
        Element testElement = generateElement("booleanElement", "0");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(false, booleanValue.booleanValue());
    }

    /**
     * <testElement>
     *      <booleanElement>Foo</booleanElement>
     * </testElement>
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsBoolean_invalidBoolean() {
        Element testElement = generateElement("booleanElement", "Foo");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("/testElement/booleanElement");
        
        Assert.assertEquals(false, booleanValue.booleanValue());
    }
    
    /**
     * A null expression passed into evaluateAsBoolean.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsBoolean_noExpression() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsBoolean(null);
    }
    
/************************ evaluateAsDate Tests ***************/
    /**
     * <testElement>
     *      <dateElement/>
     * </testElement>
     */
    @Test
    public void testEvaluateAsDate_emptyNode() {
        Element testElement = generateElement("dateElement");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Date date = simpleXPathTemplate.evaluateAsDate("/testElement/dateElement");
        
        Assert.assertNull(date);
    }
    
    /**
     * <testElement>
     *      <dateElement>Foo</dateElement>
     * </testElement>
     */
    @Test(expected=InvalidDateFormatException.class)
    public void testEvaluateAsDate_invalidDate() {
        Element testElement = generateElement("dateElement", "Foo");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsDate("/testElement/dateElement");
    }
    
    /**
     * <testElement>
     *      <dateElement>12/31/2001 18:12:45</dateElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsDate_dateWithTime() {
        Element testElement = generateElement("dateElement", "2010-12-31T06:30:00-06:00");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Date date = simpleXPathTemplate.evaluateAsDate("/testElement/dateElement");
        
        Date expectedDate = dateTimeFormmater.parseDateTime("12/31/2010 6:30:00").toDate();
        Assert.assertEquals(expectedDate, date);
    }
    
    /**
     * A null expression passed into evaluateAsDate.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsDate_noExpression() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsDate(null);
    }

/************************ evaluateAsLong Tests ***************/
    /**
     * <testElement>
     *      <longElement>Foo</longElement>
     * </testElement>
     */
    @Test(expected=NumberFormatException.class)
    public void testEvaluateAsLong_invalidLong() {
        Element testElement = generateElement("longElement", "Foo");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsLong("/testElement/longElement");
    }
    
    /**
     * <testElement>
     *      <longElement>42</longElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsLong_long() {
        Element testElement = generateElement("longElement","42");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        long longValue = simpleXPathTemplate.evaluateAsLong("/testElement/longElement");
        
        Assert.assertEquals(42, longValue);
    }
    
    /**
     * A null expression passed into evaluateAsLong.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsLong_noExpression() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsLong(null);
    }

    /**
     * A empty longElement element passed into evaluateAsFloat.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsLong_emptyExpression() {
        Element testElement = new Element("longElement", "");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsLong(null);
    }
    
/************************ evaluateAsFloat Tests ***************/
    /**
     * <testElement>
     *      <floatElement>Foo</floatElement>
     * </testElement>
     */
    @Test(expected=NumberFormatException.class)
    public void testEvaluateAsFloat_invalidFloat() {
        Element testElement = generateElement("floatElement", "Foo");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsFloat("/testElement/floatElement");
    }
    
    /**
     * <testElement>
     *      <floatElement>42</floatElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsFloat_float() {
        Element testElement = generateElement("floatElement", "42");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        float floatValue = simpleXPathTemplate.evaluateAsFloat("/testElement/floatElement");
        
        Assert.assertEquals(42.0f, floatValue);
    }
    
    /**
     * A null expression passed into evaluateAsFloat.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsFloat_noExpression() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsFloat(null);
    }
    
    /**
     * A empty floatElement element passed into evaluateAsFloat.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsFloat_emptyExpression() {
        Element testElement = new Element("floatElement", "");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsFloat(null);
    }
    
/************************ evaluateAsInt Tests ***************/
    /**
     * <testElement>
     *      <intElement>Foo</intElement>
     * </testElement>
     */
    @Test(expected=NumberFormatException.class)
    public void testEvaluateAsInt_invalidInt() {
        Element testElement = generateElement("intElement", "Foo");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsInt("/testElement/intElement");
    }
    
    /**
     * <testElement>
     *      <intElement>42</intElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsInt_int() {
        Element testElement = generateElement("intElement", "42");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        int intValue = simpleXPathTemplate.evaluateAsInt("/testElement/intElement");
        
        
        Assert.assertEquals(42, intValue);
    }
    
    /**
     * <testElement>
     *      <intElement>42.0</intElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsInt_float() {
        Element testElement = generateElement("intElement", "42.0");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        int intValue = simpleXPathTemplate.evaluateAsInt("/testElement/intElement");
        
        Assert.assertEquals(42, intValue);
    }
    
    /**
     * A null expression passed into evaluateAsFloat.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsInt_noExpression() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsInt(null);
    }
    
    /**
     * A empty intElement element passed into evaluateAsFloat.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsInt_emptyExpression() {
        Element testElement = new Element("intElement", "");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsInt(null);
    }
    
    /**
     * <testElement>
     *      <intListElement>Foo</intListElement>
     * </testElement>
     */
    @Test(expected=NumberFormatException.class)
    public void testEvaluateAsIntegerList_invalidInt() {
        Element testElement = generateElement("intListElement", "Foo");
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsIntegerList("/testElement/intListElement");
    }
    
    /**
     * <testElement>
     *      <intElement>42</intElement>
     *      <intElement>24</intElement>
     *      <intElement>36</intElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsIntegerList_intList() {
        Element testElement = generateElement("intListElement", "42", "24", "36");
        
        
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        List<Integer> integerList = simpleXPathTemplate.evaluateAsIntegerList("/testElement/intListElement");
        
        Assert.assertEquals(Lists.newArrayList(42,24,36), integerList);
    }
    
    /**
     * A null expression passed into evaluateAsIntegerList.
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsIntegerList_noExpression() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsIntegerList(null);
    }

    /************************ evaluateAsDouble Tests ***************/
    /**
     * <testElement>
     * <doubleElement>10.0</doubleElement>
     * </testElement>
     */
    /**
     * A valid expression passed into evaluateAsDouble
     */
    @Test
    public void testevaluateAsDouble_validNode() {
        Element testElement = generateElement("doubleElement", "10.00");
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Double doubleValue = simpleXPathTemplate.evaluateAsDouble("/testElement/doubleElement");
        Assert.assertEquals(10.00, doubleValue.doubleValue());
    }

    /**
     * A null expression passed into evaluateAsDouble
     */
    @Test(expected = IllegalArgumentException.class)
    public void testevaluateAsDouble_nullNode() {
        Element testElement = generateElement("doubleElement");
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsDouble(null);
    }

    /**
     * A String expression passed into evaluateAsDouble
     */
    @Test(expected = NumberFormatException.class)
    public void testevaluateAsDouble_stringNode() {
        Element testElement = generateElement("doubleElement", "foo");
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsDouble("/testElement/doubleElement");
    }

    /**
     * A empty expression passed into evaluateAsDouble
     */
    @Test(expected = IllegalArgumentException.class)
    public void testevaluateAsDouble_emptyNode() {
        Element testElement = generateElement("doubleElement", "");
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsDouble(null);
    }

    /************************ evaluateAsDouble with defaultWhenEmpty Tests ***************/
    /**
     * A valid expression passed into evaluateAsDouble
     */
    @Test
    public void testevaluateAsDouble_defaultWhenEmpty_validNode() {
        Element testElement = generateElement("doubleElement", "10.00");
        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Double doubleValue = simpleXPathTemplate.evaluateAsDouble("/testElement/doubleElement", 15.00);
        Assert.assertEquals(10.00, doubleValue.doubleValue());
    }

    /**
     * A null expression passed into evaluateAsDouble
     */
    @Test(expected = IllegalArgumentException.class)
    public void testevaluateAsDouble_defaultWhenEmpty_nullNode() {
        Element testElement = generateElement("doubleElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Double doubleValue = simpleXPathTemplate.evaluateAsDouble(null, 10.00);

        Assert.assertEquals(10.00, doubleValue.doubleValue());
    }

    /**
     * A String expression passed into evaluateAsDouble
     */
    @Test(expected = NumberFormatException.class)
    public void testevaluateAsDouble_defaultWhenEmpty_stringNode() {
        Element testElement = generateElement("doubleElement", "foo");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        simpleXPathTemplate.evaluateAsDouble("/testElement/doubleElement", 10.00);
    }

    /**
     * A empty expression passed into evaluateAsDouble
     */
    @Test(expected = IllegalArgumentException.class)
    public void testevaluateAsDouble_defaultWhenEmpty_emptyNode() {
        Element testElement = generateElement("doubleElement", "");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement);
        Double doubleValue = simpleXPathTemplate.evaluateAsDouble(null, 10.00);
        Assert.assertEquals(10.00, doubleValue.doubleValue());
    }
}
package com.cannontech.common.util.xml;

import junit.framework.Assert;

import org.jdom.Element;
import org.junit.Test;

public class SimpleXPathTemplateTest {
    
    /**
     * <testElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_noNode() {
        Element testElement = new Element("testElement");

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("\testElement\booleanElement");
        
        Assert.assertNull(booleanValue);
    }
    
    /**
     * <testElement>
     *      <booleanElement/>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_emptyNode() {
        Element testElement = new Element("testElement");
        testElement.addContent(new Element("booleanElement"));

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("//testElement/booleanElement");
        
        Assert.assertNotNull(booleanValue);
        Assert.assertEquals(true, booleanValue.booleanValue());
    }
    
    /**
     * <testElement>
     *      <booleanElement>true</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_trueNode() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("booleanElement");
        booleanElement.addContent("true");
        testElement.addContent(booleanElement);

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("//testElement/booleanElement");
        
        Assert.assertNotNull(booleanValue);
        Assert.assertEquals(true, booleanValue.booleanValue());
    }

    /**
     * <testElement>
     *      <booleanElement>TrUe</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_TrUeNode() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("booleanElement");
        booleanElement.addContent("TrUe");
        testElement.addContent(booleanElement);

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("//testElement/booleanElement");
        
        Assert.assertNotNull(booleanValue);
        Assert.assertEquals(true, booleanValue.booleanValue());
    }

    /**
     * <testElement>
     *      <booleanElement>1</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_trueNodeUsingBinaryOne() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("booleanElement");
        booleanElement.addContent("1");
        testElement.addContent(booleanElement);

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("//testElement/booleanElement");
        
        Assert.assertNotNull(booleanValue);
        Assert.assertEquals(true, booleanValue.booleanValue());
    }
    
    /**
     * <testElement>
     *      <booleanElement>false</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_falseNode() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("booleanElement");
        booleanElement.addContent("false");
        testElement.addContent(booleanElement);

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("//testElement/booleanElement");
        
        Assert.assertNotNull(booleanValue);
        Assert.assertEquals(false, booleanValue.booleanValue());
    }

    /**
     * <testElement>
     *      <booleanElement>FaLsE</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_FaLsENode() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("booleanElement");
        booleanElement.addContent("FaLsE");
        testElement.addContent(booleanElement);

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("//testElement/booleanElement");
        
        Assert.assertNotNull(booleanValue);
        Assert.assertEquals(false, booleanValue.booleanValue());
    }
    
    /**
     * <testElement>
     *      <booleanElement>1</booleanElement>
     * </testElement>
     */
    @Test
    public void testEvaluateAsBoolean_falseNodeUsingBinaryZero() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("booleanElement");
        booleanElement.addContent("0");
        testElement.addContent(booleanElement);

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("//testElement/booleanElement");
        
        Assert.assertNotNull(booleanValue);
        Assert.assertEquals(false, booleanValue.booleanValue());
    }

    /**
     * <testElement>
     *      <booleanElement>Foo</booleanElement>
     * </testElement>
     */
    @Test(expected=IllegalArgumentException.class)
    public void testEvaluateAsBoolean_invalidNode() {
        Element testElement = new Element("testElement");

        Element booleanElement = new Element("booleanElement");
        booleanElement.addContent("Foo");
        testElement.addContent(booleanElement);

        SimpleXPathTemplate simpleXPathTemplate = YukonXml.getXPathTemplateForElement(testElement );
        Boolean booleanValue = simpleXPathTemplate.evaluateAsBoolean("//testElement/booleanElement");
        
        Assert.assertNotNull(booleanValue);
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
    
}
package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

public class ScenarioStartRequestEndpointTest {

    private ScenarioStartRequestEndpoint impl;
    private LoadControlServiceTest testService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
        testService = new LoadControlServiceTest();
        
        impl = new ScenarioStartRequestEndpoint();
        impl.setLoadControlService(testService);
        impl.initialize();
    }
    
    private class LoadControlServiceTest extends LoadControlServiceAdapter {
        
        private String scenarioName;
        private Date startTime;
        private Date stopTime;
        
        @Override
        public ScenarioStatus startControlByScenarioName(String scenarioName, Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute) throws NotFoundException, TimeoutException {
            
            this.scenarioName = scenarioName;
            this.startTime = startTime;
            this.stopTime = stopTime;
            
            if (scenarioName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            } else if (scenarioName.equals("TIMEOUT")) {
                throw new TimeoutException();
            }
            
            return null;
        }
        
        public String getScenarioName() {
            return scenarioName;
        }
        public Date getStartTime() {
            return startTime;
        }
        public Date getStopTime() {
            return stopTime;
        }
    }
   
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Element responseElement = null;
        Element tmpElement = null;
        SimpleXPathTemplate outputTemplate = null;
        
        // no start time, no stop time
        //==========================================================================================
        requestElement = new Element("scenarioStartRequest", ns);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "Scenario1");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (no start time, no stop time)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (no start time, no stop time)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // inputs
        Assert.assertEquals("Scenario1", testService.getScenarioName());
        Assert.assertEquals(null, testService.getStartTime());
        Assert.assertEquals(null, testService.getStopTime());
        
        // outputs
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:scenarioStartResponse/y:success"));
        
        
        // start time, no stop time
        //==========================================================================================
        requestElement = new Element("scenarioStartRequest", ns);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "Scenario2");
        requestElement.addContent(tmpElement);
        tmpElement = XmlUtils.createStringElement("startDateTime", ns, "2008-10-13T12:30:00Z");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (start time, no stop time)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (start time, no stop time)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // inputs
        Assert.assertEquals("Scenario2", testService.getScenarioName());
        Assert.assertEquals("2008-10-13T12:30:00Z", XmlUtils.formatDate(testService.getStartTime()));
        Assert.assertEquals(null, testService.getStopTime());
        
        // outputs
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:scenarioStartResponse/y:success"));
        
        
        // start time, stop time
        //==========================================================================================
        requestElement = new Element("scenarioStartRequest", ns);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "Scenario3");
        requestElement.addContent(tmpElement);
        tmpElement = XmlUtils.createStringElement("startDateTime", ns, "2008-10-13T12:30:00Z");
        requestElement.addContent(tmpElement);
        tmpElement = XmlUtils.createStringElement("stopDateTime", ns, "2008-10-13T21:49:01Z");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (start time, stop time)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (start time, stop time)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // inputs
        Assert.assertEquals("Scenario3", testService.getScenarioName());
        Assert.assertEquals("2008-10-13T12:30:00Z", XmlUtils.formatDate(testService.getStartTime()));
        Assert.assertEquals("2008-10-13T21:49:01Z", XmlUtils.formatDate(testService.getStopTime()));
        
        // outputs
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:scenarioStartResponse/y:success"));
        
        // not found
        //==========================================================================================
        requestElement = new Element("scenarioStartRequest", ns);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "NOT_FOUND");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (not found)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (not found)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNull(outputTemplate.evaluateAsNode("/y:scenarioStartResponse/y:success"));
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:scenarioStartResponse/y:failure"));
        Assert.assertEquals("InvalidScenarioName", outputTemplate.evaluateAsString("/y:scenarioStartResponse/y:failure/y:errorCode"));
        
        // timeout
        //==========================================================================================
        requestElement = new Element("scenarioStartRequest", ns);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "TIMEOUT");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (timeout)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (timeout)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNull(outputTemplate.evaluateAsNode("/y:scenarioStartResponse/y:success"));
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:scenarioStartResponse/y:failure"));
        Assert.assertEquals("Timeout", outputTemplate.evaluateAsString("/y:scenarioStartResponse/y:failure/y:errorCode"));
    }

}

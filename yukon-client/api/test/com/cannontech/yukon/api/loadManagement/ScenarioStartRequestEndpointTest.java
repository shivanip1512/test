package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ScenarioStartRequestEndpointTest {

    private ScenarioStartRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ScenarioStartRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
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
        SimpleXPathTemplate outputTemplate = null;
        
        // no start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "Scenario1", null, null);
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario1", mockService.getScenarioName());
        Assert.assertEquals("Incorrect startDateTime - should be null.", null, mockService.getStartTime());
        Assert.assertEquals("Incorrect stopDateTime - should be null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStartResponse");
        
        
        // start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "Scenario2", "2008-10-13T12:30:00Z", null);
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario2", mockService.getScenarioName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime - should be null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStartResponse");
        
        
        // start time, stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "Scenario3", "2008-10-13T12:30:00Z", "2008-10-13T21:49:01Z");
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario3", mockService.getScenarioName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:49:01Z", XmlUtils.formatDate(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStartResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "NOT_FOUND", null, null);
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStartResponse", "InvalidScenarioName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "TIMEOUT", null, null);
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStartResponse", "Timeout");
    }

}

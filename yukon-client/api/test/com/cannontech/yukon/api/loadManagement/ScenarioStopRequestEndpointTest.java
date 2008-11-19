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

public class ScenarioStopRequestEndpointTest {

    private ScenarioStopRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ScenarioStopRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
        private String scenarioName;
        private Date stopTime;
        
        @Override
        public ScenarioStatus stopControlByScenarioName(String scenarioName, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute) throws NotFoundException, TimeoutException {

            this.scenarioName = scenarioName;
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
        
        // stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "Scenario1", null, "2008-10-13T21:49:01Z");
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario1", mockService.getScenarioName());
        Assert.assertEquals("Incorrect stopDateTime", "2008-10-13T21:49:01Z", XmlUtils.formatDate(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStopResponse");
        
        // no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "Scenario2", null, null);
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario2", mockService.getScenarioName());
        Assert.assertEquals("Incorrect stopDateTime - should be null", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStopResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "NOT_FOUND", null, null);
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStopResponse", "InvalidScenarioName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "TIMEOUT", null, null);
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStopResponse", "Timeout");
    }

}

package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.loadManagement.endpoint.ScenarioStartRequestEndpoint;
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
        public ScenarioStatus startControlByScenarioName(String scenarioName, Date startTime, Date stopTime, boolean waitForResponse, boolean forceStart, boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException, TimeoutException {
            
            this.scenarioName = scenarioName;
            this.startTime = startTime;
            this.stopTime = stopTime;
            
            if (scenarioName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            } else if (scenarioName.equals("TIMEOUT")) {
                throw new TimeoutException();
            } else if (scenarioName.equals("NOT_AUTH")) {
                throw new NotAuthorizedException("");
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
        Resource requestSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ScenarioStartRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ScenarioStartResponse.xsd", this.getClass());
        
        // no start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "Scenario1", null, null, null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario1", mockService.getScenarioName());
        Assert.assertEquals("Incorrect startDateTime - should be null.", null, mockService.getStartTime());
        Assert.assertEquals("Incorrect stopDateTime - should be null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStartResponse");
        
        
        // start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "Scenario2", "2008-10-13T12:30:00Z", null, null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario2", mockService.getScenarioName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime - should be null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStartResponse");
        
        
        // start time, stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "Scenario3", "2008-10-13T12:30:00Z", "2008-10-13T21:49:01Z", null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario3", mockService.getScenarioName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:49:01Z", XmlUtils.formatDate(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStartResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "NOT_FOUND", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStartResponse", "InvalidScenarioName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "TIMEOUT", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStartResponse", "Timeout");
        
        // not auth
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStartRequest", "scenarioName", "NOT_AUTH", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStartResponse", "UserNotAuthorized");
    }

}

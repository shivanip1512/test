package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ScenarioStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ScenarioStopRequestEndpoint;
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
        
    	private boolean isAsync;
        private String scenarioName;
        private Date stopTime;
        
        @Override
        public ScenarioStatus stopControlByScenarioName(String scenarioName, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException, TimeoutException, NotAuthorizedException {

        	this.isAsync = false;
            this.scenarioName = scenarioName;
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
        
        @Override
        public void asynchStopControlByScenarioName(String scenarioName, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {

        	this.isAsync = true;
            this.scenarioName = scenarioName;
            this.stopTime = stopTime;
            
            if (scenarioName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            } else if (scenarioName.equals("NOT_AUTH")) {
                throw new NotAuthorizedException("");
            }
        }
        
        public String getScenarioName() {
            return scenarioName;
        }
        public Date getStopTime() {
            return stopTime;
        }
        public boolean isAsync() {
			return isAsync;
		}
    }
   
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        Resource requestSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ScenarioStopRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ScenarioStopResponse.xsd", this.getClass());
        
        // stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "Scenario1", null, "2008-10-13T21:49:01Z", null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario1", mockService.getScenarioName());
        Assert.assertEquals("Incorrect stopDateTime", "2008-10-13T21:49:01Z", Iso8601DateUtil.formatIso8601Date(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStopResponse");
        
        // no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "Scenario2", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect scenarioName", "Scenario2", mockService.getScenarioName());
        Assert.assertEquals("Incorrect stopDateTime - should be null", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStopResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "NOT_FOUND", null, null, null, "1.0", true, requestSchemaResource);
        XmlUtils.printElement(requestElement, "requestElement");
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        XmlUtils.printElement(responseElement, "responseElement");
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStopResponse", "InvalidScenarioName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "TIMEOUT", null, null, null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStopResponse", "Timeout");
        
        // not auth
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "NOT_AUTH", null, null, null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStopResponse", "UserNotAuthorized");
        
        // test synchronous method is called
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "SyncTest", null, null, null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Synchronous method should have been called", false, mockService.isAsync());
        
        // test asynchronous method is called
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "AsyncTest", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Synchronous method should have been called", true, mockService.isAsync());
        
        // test asynchronous method is called (invoked due to not including the waitForResponse tag at all)
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "AsyncTest", null, null, null, "1.0", null, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Synchronous method should have been called", true, mockService.isAsync());
    }

}

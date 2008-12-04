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
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProgramStartRequestEndpointTest {

    private ProgramStartRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ProgramStartRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
        private String programName;
        private Date startTime;
        private Date stopTime;
        
        @Override
        public ProgramStatus startControlByProgramName(String programName, Date startTime, Date stopTime, boolean forceStart, boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException, TimeoutException {
            
            this.programName = programName;
            this.startTime = startTime;
            this.stopTime = stopTime;
            
            if (programName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            } else if (programName.equals("TIMEOUT")) {
                throw new TimeoutException();
            } else if (programName.equals("NOT_AUTH")) {
                throw new NotAuthorizedException("");
            }
            
            return null;
        }
        
        public String getProgramName() {
            return programName;
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
        Resource requestSchemaResource = new ClassPathResource("../schemas/loadManagement/ProgramStartRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("../schemas/loadManagement/ProgramStartResponse.xsd", this.getClass());
        
        // no start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "Program1", null, null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", "Program1", mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime - should be null.", null, mockService.getStartTime());
        Assert.assertEquals("Incorrect stopDateTime - should null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        // start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "Program2", "2008-10-13T12:30:00Z", null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", "Program2", mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect startDateTime - should be null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        
        // start time, stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "Program3", "2008-10-13T12:30:00Z", "2008-10-13T21:49:01Z", "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", "Program3", mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:49:01Z", XmlUtils.formatDate(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "NOT_FOUND", null, null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "InvalidProgramName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "TIMEOUT", null, null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "Timeout");
        
        // not auth
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "NOT_AUTH", null, null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "UserNotAuthorized");
    }
}

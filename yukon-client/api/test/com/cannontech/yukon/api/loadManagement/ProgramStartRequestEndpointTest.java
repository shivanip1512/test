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
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.GearNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ProgramStartRequestEndpoint;
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
        public ProgramStatus startControlByProgramName(String programName, Date startTime, Date stopTime, String gearName, boolean forceStart, boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException, TimeoutException {
            
            if (gearName.equals("BAD_GEAR")) {
            	throw new GearNotFoundException("");
            }
            return null;
        }
        
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
        Resource requestSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProgramStartRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProgramStartResponse.xsd", this.getClass());
        
        // no start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "Program1", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", "Program1", mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime - should be null.", null, mockService.getStartTime());
        Assert.assertEquals("Incorrect stopDateTime - should null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        // start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "Program2", "2008-10-13T12:30:00Z", null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", "Program2", mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(mockService.getStartTime()));
        Assert.assertEquals("Incorrect startDateTime - should be null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        
        // start time, stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "Program3", "2008-10-13T12:30:00Z", "2008-10-13T21:49:01Z", null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", "Program3", mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:49:01Z", Iso8601DateUtil.formatIso8601Date(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "NOT_FOUND", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "InvalidProgramName");
        
        //gear  not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "NOT_FOUND", null, null, "BAD_GEAR", "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "InvalidGearName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "TIMEOUT", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "Timeout");
        
        // not auth
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "NOT_AUTH", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "UserNotAuthorized");
    }
}

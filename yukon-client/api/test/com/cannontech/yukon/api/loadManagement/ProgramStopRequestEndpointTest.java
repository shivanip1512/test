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
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ProgramStopRequestEndpoint;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProgramStopRequestEndpointTest {

    private ProgramStopRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ProgramStopRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
        private String programName;
        private Date stopTime;
        
        @Override
        public ProgramStatus stopControlByProgramName(String programName, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute, LiteYukonUser user) throws NotFoundException, TimeoutException {

            this.programName = programName;
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
        Resource requestSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProgramStopRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProgramStopResponse.xsd", this.getClass());
        
        // stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", "Program1", null, "2008-10-13T21:49:01Z", null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", "Program1", mockService.getProgramName());
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:49:01Z", Iso8601DateUtil.formatIso8601Date(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStopResponse");
        
        // no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", "Program2", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", "Program2", mockService.getProgramName());
        Assert.assertEquals("Incorrect stopDateTime - should be null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStopResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", "NOT_FOUND", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStopResponse", "InvalidProgramName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", "TIMEOUT", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStopResponse", "Timeout");
        
        // not auth
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", "NOT_AUTH", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStopResponse", "UserNotAuthorized");
    }
}

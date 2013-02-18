package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.GearNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.program.model.GearAdjustment;
import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.loadManagement.adapters.ProgramServiceAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.RolePropertyDaoAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ProgramStartRequestEndpoint;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProgramStartRequestEndpointTest {

    private ProgramStartRequestEndpoint impl;
    private MockProgramService mockService;    
    private RolePropertyDao mockRolePropertyDao;
    
    private static final String PROG1 = "Program1";
    private static final String PROG2 = "Program2";
    private static final String PROG3= "Program3";
    private static final String PROG_TIMEOUT = "TIMEOUT";
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockProgramService();

        mockRolePropertyDao = new RolePropertyDaoAdapter() {
            @Override
            public void verifyProperty(YukonRoleProperty property, LiteYukonUser user) throws NotAuthorizedException {
                return;
            }

            @Override
            public boolean getPropertyBooleanValue(YukonRoleProperty property, LiteYukonUser user) throws UserNotInRoleException {
                return true; // stopScheduled variable - YukonRoleProperty.SCHEDULE_STOP_CHECKED_BY_DEFAULT
            }
        };

        impl = new ProgramStartRequestEndpoint();
        ReflectionTestUtils.setField(impl, "programService", mockService, ProgramService.class);
        ReflectionTestUtils.setField(impl, "rolePropertyDao", mockRolePropertyDao, RolePropertyDao.class);
        impl.initialize();
    }
    
    private class MockProgramService extends ProgramServiceAdapter {
        private Date startTime;
        private Date stopTime;
        private String programName;
        
        @Override
        public ConstraintViolations getConstraintViolationForStartProgram(int programId, int gearNumber, Date startDate, Duration startOffset, Date stopDate, Duration stopOffset, List<GearAdjustment> gearAdjustments) {
            return new ConstraintViolations(new ArrayList<ConstraintContainer>());
        }
        
        @Override
        public ProgramStatus startProgramByName(String programName, Date startTime, Date stopTime,
                                                String gearName, boolean force,
                                                boolean observeConstraints, LiteYukonUser liteYukonUser)
                                                        throws TimeoutException {
            if (programName.equals(PROG_TIMEOUT)) {
                throw new TimeoutException();
            }else if (programName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            } else if (programName.equals("NOT_AUTH")) {
                throw new NotAuthorizedException("");
            }
            if (gearName.equals("BAD_GEAR")) {
                throw new GearNotFoundException("");
            }
            
            this.programName = programName;
            this.startTime = startTime;
            this.stopTime = stopTime;

            return null;
        }

        public String getProgramName() {return programName;}
        public Date getStartTime() { return startTime; }
        public Date getStopTime() { return stopTime; }
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
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", PROG1, null, null, "", "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", PROG1, mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime - should be null.", null, mockService.getStartTime());
        Assert.assertEquals("Incorrect stopDateTime - should null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        // start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", PROG2, "2008-10-13T12:30:00Z", null, "", "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", PROG2, mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(mockService.getStartTime()));
        Assert.assertEquals("Incorrect startDateTime - should be null.", null, mockService.getStopTime());
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        
        // start time, stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", PROG3, "2008-10-13T12:30:00Z", "2008-10-13T21:49:01Z", "", "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", PROG3, mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:49:01Z", Iso8601DateUtil.formatIso8601Date(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStartResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "NOT_FOUND", null, null, "", "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "InvalidProgramName");
        
        //gear  not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", PROG1, null, null, "BAD_GEAR", "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "InvalidGearName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", PROG_TIMEOUT, null, null, "", "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "Timeout");
        
        // not auth
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStartRequest", "programName", "NOT_AUTH", null, null, "", "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "UserNotAuthorized");
    }
}

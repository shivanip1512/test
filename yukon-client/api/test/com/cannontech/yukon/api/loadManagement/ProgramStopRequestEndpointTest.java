package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.Date;

import org.jdom2.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlProgramDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.ProgramServiceAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.RolePropertyDaoAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ProgramStopRequestEndpoint;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProgramStopRequestEndpointTest {

    private ProgramStopRequestEndpoint impl;
    private MockProgramService mockService;
    private RolePropertyDao mockRolePropertyDao;
    private LoadControlProgramDao mockLoadControlProgramDao;

    private static final String PROG1 = "Program1";
    private static final String PROG2 = "Program2";
    private static final String PROG3= "Program3";
    private static final String PROG_TIMEOUT = "TIMEOUT";
    private static final String PROG_NOT_FOUND = "NOT_FOUND";
    private static final String PROG_NOT_AUTH = "NOT_AUTH";
    
    private static final int PROG1_ID = 2;
    private static final int PROG2_ID = 3;
    private static final int PROG3_ID = 4;
    private static final int PROG_TIMEOUT_ID = 5;
    private static final int PROG_NOT_FOUND_ID = 6;
    private static final int PROG_NOT_AUTH_ID = 7;

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

        mockLoadControlProgramDao = new LoadControlProgramDaoAdapter() {
            @Override
            public int getProgramIdByProgramName(String programName) {
                if (programName.equals(PROG_TIMEOUT)) {
                    return PROG_TIMEOUT_ID;
                }else if (programName.equals(PROG_NOT_FOUND)) {
                    return PROG_NOT_FOUND_ID;
                } else if (programName.equals(PROG_NOT_AUTH)) {
                    return PROG_NOT_AUTH_ID;
                } else if (programName.equals(PROG1)) {
                    return PROG1_ID;
                } else if (programName.equals(PROG2)) {
                    return PROG2_ID;
                } else if (programName.equals(PROG3)) {
                    return PROG3_ID;
                }
                return 0;
            }
        };
        
        impl = new ProgramStopRequestEndpoint();
        ReflectionTestUtils.setField(impl, "programService", mockService,ProgramService.class);
        ReflectionTestUtils.setField(impl, "rolePropertyDao", mockRolePropertyDao, RolePropertyDao.class);
        ReflectionTestUtils.setField(impl, "loadControlProgramDao", mockLoadControlProgramDao);
        impl.initialize();
    }
    
    private class MockProgramService extends ProgramServiceAdapter {
        private int programId;
        private Date stopTime;
        
        @Override
        public ConstraintViolations getConstraintViolationsForStopProgram(int programId, int gearNumber, Date stopDate, ProgramOriginSource programOriginSource) {
            return new ConstraintViolations(new ArrayList<ConstraintContainer>());
        }

        @Override
        public ProgramStatus stopProgram(int programId, Date stopTime,
                                                              boolean force, boolean observeConstraints,
                                                              ProgramOriginSource programOriginSource) throws TimeoutException {
            if (programId == PROG_TIMEOUT_ID) {
                throw new TimeoutException();
            }else if (programId == PROG_NOT_FOUND_ID) {
                throw new NotFoundException("");
            } else if (programId == PROG_NOT_AUTH_ID) {
                throw new NotAuthorizedException("");
            }

            this.stopTime = stopTime;
            this.programId = programId;
            
            return null;
        }
        
        public int getProgramId() { return programId; }
        public Date getStopTime() { return stopTime; }
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
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", PROG1, null, "2008-10-13T21:49:01Z", null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect program.", PROG1_ID, mockService.getProgramId());
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:49:01Z", Iso8601DateUtil.formatIso8601Date(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStopResponse");
        
        // no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", PROG2, null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect program.", PROG2_ID, mockService.getProgramId());
        Assert.assertEquals("Incorrect stopDateTime - should be null.", CtiUtilities.get1990GregCalendar().getTime(), mockService.getStopTime());
        
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
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", PROG_TIMEOUT, null, null, null, "1.0", false, requestSchemaResource);
        
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

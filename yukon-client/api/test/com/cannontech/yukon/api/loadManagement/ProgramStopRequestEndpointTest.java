package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
import org.joda.time.Duration;
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
import com.cannontech.core.dao.GearNotFoundException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.program.service.ConstraintContainer;
import com.cannontech.dr.program.service.ConstraintViolations;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.data.LMProgramDirect;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.ConnectionException;
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
    private static final String PROG_TIMEOUT = "TIMEOUT";

    private final Map<String, Integer> programIds = new HashMap<String, Integer>() {{
        put(PROG1, 1);
        put(PROG2, 2);
        put(PROG_TIMEOUT, 3);
    }};

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
            public int getProgramIdByProgramName(String programName) throws NotFoundException {
                if (programName.equals("NOT_FOUND")) {
                    throw new NotFoundException("");
                } else if (programName.equals("NOT_AUTH")) {
                    throw new NotAuthorizedException("");
                }

                return getProgId(programName);
            }

            @Override
            public int getGearNumberForGearName(int programId, String gearName) throws NotFoundException {
                if (gearName.equals("BAD_GEAR")) {
                    throw new GearNotFoundException("");
                }
                return 1;
            }

        };
        
        impl = new ProgramStopRequestEndpoint();
        ReflectionTestUtils.setField(impl, "programService", mockService,ProgramService.class);
        ReflectionTestUtils.setField(impl, "rolePropertyDao", mockRolePropertyDao, RolePropertyDao.class);
        ReflectionTestUtils.setField(impl, "loadControlProgramDao", mockLoadControlProgramDao, LoadControlProgramDao.class);
        impl.initialize();
    }
    
    private int getProgId(String program) {
        if(programIds.containsKey(program)) {
            return programIds.get(program);
        }
        
        return -1;
    }

    private class MockProgramService extends ProgramServiceAdapter {
        private int programId;
        private Date stopTime;
        
        @Override
        public ConstraintViolations getConstraintViolationsForStopProgram(int programId, int gearNumber, Date stopDate) {
            return new ConstraintViolations(new ArrayList<ConstraintContainer>());
        }

        @Override
        public ProgramStatus scheduleProgramStopBlocking(int programId, Date stopDate,
                                                  Duration stopOffset) throws TimeoutException {
            if (programId == getProgId(PROG_TIMEOUT)) {
                throw new TimeoutException();
            }
            this.programId = programId;
            this.stopTime = stopDate;
            
            return null;
        }
        
        @Override
        public LMProgramBase getProgramSafe(int programId) throws ConnectionException, NotFoundException {
            LMProgramDirect program = new LMProgramDirect();
            program.setCurrentGearNumber(1);
            return program;
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
        
        Assert.assertEquals("Incorrect programName.", getProgId(PROG1), mockService.getProgramId());
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:49:01Z", Iso8601DateUtil.formatIso8601Date(mockService.getStopTime()));
        
        TestUtils.runSuccessAssertion(outputTemplate, "programStopResponse");
        
        // no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("programStopRequest", "programName", PROG2, null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect programName.", getProgId(PROG2), mockService.getProgramId());
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

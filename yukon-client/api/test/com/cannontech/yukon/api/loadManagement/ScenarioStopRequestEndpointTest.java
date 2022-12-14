package com.cannontech.yukon.api.loadManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.UserNotInRoleException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.dr.model.ProgramOriginSource;
import com.cannontech.dr.program.service.ProgramService;
import com.cannontech.loadcontrol.dao.LoadControlProgramDao;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlProgramDaoAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.ProgramServiceAdapter;
import com.cannontech.yukon.api.loadManagement.adapters.RolePropertyDaoAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ScenarioStopRequestEndpoint;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ScenarioStopRequestEndpointTest {

    private ScenarioStopRequestEndpoint impl;
    private MockProgramService mockService;
    private RolePropertyDao mockRolePropertyDao;
    private LoadControlProgramDao mockLoadControlProgramDao;

    private static final String TIMEOUT = "TIMEOUT";
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String NOT_AUTH = "NOT_AUTH";
    private static final String SCEN1 = "Program1";
    private static final String SCEN2 = "Program2";
    private static final String SCEN3= "Program3";

    private static final int SCEN1_ID = 2;
    private static final int SCEN2_ID = 3;
    private static final int SCEN3_ID = 4;
    private static final int TIMEOUT_ID = 5;
    private static final int NOT_FOUND_ID = 6;
    private static final int NOT_AUTH_ID = 7;
    
    @BeforeEach
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
            public int getScenarioIdForScenarioName(String scenarioName) {
                if (scenarioName.equals(TIMEOUT)) {
                    return TIMEOUT_ID;
                }else if (scenarioName.equals(NOT_FOUND)) {
                    return NOT_FOUND_ID;
                } else if (scenarioName.equals(NOT_AUTH)) {
                    return NOT_AUTH_ID;
                } else if (scenarioName.equals(SCEN1)) {
                    return SCEN1_ID;
                } else if (scenarioName.equals(SCEN2)) {
                    return SCEN2_ID;
                } else if (scenarioName.equals(SCEN3)) {
                    return SCEN3_ID;
                }
                return 0;
            }
        };
        
        impl = new ScenarioStopRequestEndpoint();
        ReflectionTestUtils.setField(impl, "programService", mockService,ProgramService.class);
        ReflectionTestUtils.setField(impl, "rolePropertyDao", mockRolePropertyDao, RolePropertyDao.class);
        ReflectionTestUtils.setField(impl, "loadControlProgramDao", mockLoadControlProgramDao);
        impl.initialize();
    }

    private class MockProgramService extends ProgramServiceAdapter {
        
    	private boolean isAsync;
        private int scenarioId;
        private Date stopTime;
        
        @Override
        public List<ProgramStatus> stopScenarioBlocking(int scenarioId, Date stopTime, boolean forceStop, boolean observeConstraintsAndExecute, LiteYukonUser user,
                                                            ProgramOriginSource programOriginSource) throws NotFoundException, TimeoutException, NotAuthorizedException {

            this.isAsync = false;
            this.scenarioId = scenarioId;
            this.stopTime = stopTime;
            
            if (scenarioId == NOT_FOUND_ID) {
                throw new NotFoundException("");
            } else if (scenarioId == TIMEOUT_ID) {
                throw new TimeoutException();
            } else if (scenarioId == NOT_AUTH_ID) {
                throw new NotAuthorizedException("");
            }
            
            return null;
        }
        
        @Override
        public void stopScenario(int scenarioId, Date stopTime, boolean forceStop, 
                                       boolean observeConstraintsAndExecute, LiteYukonUser user,
                                       ProgramOriginSource programOriginSource) 
                                               throws NotFoundException, NotAuthorizedException {

            this.isAsync = true;
            this.scenarioId = scenarioId;
            this.stopTime = stopTime;
            
            if (scenarioId == NOT_FOUND_ID) {
                throw new NotFoundException("");
            } else if (scenarioId == NOT_FOUND_ID) {
                throw new NotAuthorizedException("");
            }
        }
        
        public int getScenarioId() {
            return scenarioId;
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
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName",SCEN1, null, "2008-10-13T21:49:01Z", null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertEquals(SCEN1_ID, mockService.getScenarioId(), "Incorrect scenarioName");
        assertEquals("2008-10-13T21:49:01Z", Iso8601DateUtil.formatIso8601Date(mockService.getStopTime()), "Incorrect stopDateTime");
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStopResponse");
        
        // no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", SCEN2, null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertEquals(SCEN2_ID, mockService.getScenarioId(), "Incorrect scenarioName");
        assertEquals(null, mockService.getStopTime(), "Incorrect stopDateTime - should be null");
        
        TestUtils.runSuccessAssertion(outputTemplate, "scenarioStopResponse");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", NOT_FOUND, null, null, null, "1.0", true, requestSchemaResource);
        XmlUtils.printElement(requestElement, "requestElement");
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        XmlUtils.printElement(responseElement, "responseElement");
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStopResponse", "InvalidScenarioName");
        
        // timeout
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", TIMEOUT, null, null, null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStopResponse", "Timeout");
        
        // not auth
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", NOT_AUTH, null, null, null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "scenarioStopResponse", "UserNotAuthorized");
        
        // test synchronous method is called
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "SyncTest", null, null, null, "1.0", true, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertEquals(false, mockService.isAsync(), "Synchronous method should have been called");
        
        // test asynchronous method is called
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "AsyncTest", null, null, null, "1.0", false, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertEquals(true, mockService.isAsync(), "Synchronous method should have been called");
        
        // test asynchronous method is called (invoked due to not including the waitForResponse tag at all)
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createStartStopRequestElement("scenarioStopRequest", "scenarioName", "AsyncTest", null, null, null, "1.0", null, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertEquals(true, mockService.isAsync(), "Synchronous method should have been called");
    }

}

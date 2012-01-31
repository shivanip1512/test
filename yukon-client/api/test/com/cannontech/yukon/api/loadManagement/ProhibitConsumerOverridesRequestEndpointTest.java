package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.events.service.EventLogMockServiceFactory;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ProhibitConsumerOverridesRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProhibitConsumerOverridesRequestEndpointTest {

    private ProhibitConsumerOverridesRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    private StarsEventLogService mockStarsEventLogService;
    
    private static final String RESP_ELEMENT_NAME = "prohibitConsumerOverridesResponse";
    
    @Before
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        mockStarsEventLogService = EventLogMockServiceFactory.getEventLogMockService(StarsEventLogService.class);
        
        impl = new ProhibitConsumerOverridesRequestEndpoint();
        impl.setOptOutService(mockOptOutService);
        impl.setStarsEventLogService(mockStarsEventLogService);
        impl.setRolePropertyDao(new MockRolePropertyDao());
    }
    
    @Test
    public void testInvokeSuccess() throws Exception {
        
    	// Load schemas    	
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProhibitConsumerOverridesRequest.xsd",
    			this.getClass());
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProhibitConsumerOverridesResponse.xsd",
    			this.getClass());
    	
    	
    	// test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createProhibitOverridesRequestElement(
    			XmlVersionUtils.YUKON_MSG_VERSION_1_0, null, null, reqSchemaResource);
        LiteYukonUser user = MockRolePropertyDao.getUnAuthorizedUser();
        Element respElement = impl.invoke(requestElement, user);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");
    	
    	
        // test with authorized user
        //==========================================================================================
        user = new LiteYukonUser();
        respElement = impl.invoke(requestElement, user);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
        Assert.assertEquals(OptOutEnabled.DISABLED_WITH_COMM, mockOptOutService.getLastValueCalled());

        
        // test with program name - 1.1 backwards compatibility
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProhibitOverridesRequestElement(
                XmlVersionUtils.YUKON_MSG_VERSION_1_1, "Program1", null, reqSchemaResource);
        user = new LiteYukonUser();
        respElement = impl.invoke(requestElement, user);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        Assert.assertEquals(OptOutEnabled.DISABLED_WITH_COMM, mockOptOutService.getLastValueCalled());
        
        // test with program name - comms enabled
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProhibitOverridesRequestElement(
                XmlVersionUtils.YUKON_MSG_VERSION_1_2, "Program1", OptOutEnabled.DISABLED_WITH_COMM, reqSchemaResource);
        user = new LiteYukonUser();
        respElement = impl.invoke(requestElement, user);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        Assert.assertEquals(OptOutEnabled.DISABLED_WITH_COMM, mockOptOutService.getLastValueCalled());
        
        // test with program name - opts + comms disabled
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProhibitOverridesRequestElement(
                XmlVersionUtils.YUKON_MSG_VERSION_1_1, "Program1", OptOutEnabled.DISABLED_WITHOUT_COMM, reqSchemaResource);
        user = new LiteYukonUser();
        respElement = impl.invoke(requestElement, user);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        Assert.assertEquals(OptOutEnabled.DISABLED_WITHOUT_COMM, mockOptOutService.getLastValueCalled());
        
        // test with program name - opts + comms enabled
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProhibitOverridesRequestElement(
                XmlVersionUtils.YUKON_MSG_VERSION_1_1, "Program1", OptOutEnabled.ENABLED, reqSchemaResource);
        user = new LiteYukonUser();
        respElement = impl.invoke(requestElement, user);
        
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        Assert.assertEquals(OptOutEnabled.ENABLED, mockOptOutService.getLastValueCalled());

        // create template and parse response data
        template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        Assert.assertEquals("Wrong program name", "Program1", mockOptOutService.getProgramName());

    }
    
    private class MockOptOutService extends OptOutServiceAdapter {

    	private OptOutEnabled lastValueCalled = null;
    	private String programName = null;

    	public OptOutEnabled getLastValueCalled() {
			return lastValueCalled;
		}

    	public String getProgramName() {
    	    return programName;
    	}

    	@Override
    	public void changeOptOutEnabledStateForToday(LiteYukonUser user, OptOutEnabled optOutsEnabled) {
    		this.lastValueCalled = optOutsEnabled;
    	}
    	
    	@Override
        public void changeOptOutEnabledStateForTodayByProgramName(LiteYukonUser user, OptOutEnabled optOutsEnabled,
                                                                  String programName) throws ProgramNotFoundException {
    	    this.lastValueCalled = optOutsEnabled;
    	    this.programName = programName;
    	}

    }

}


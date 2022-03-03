package com.cannontech.yukon.api.loadManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.EventLogMockServiceFactory;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.CountOverridesTowardsLimitRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class CountOverridesTowardsLimitRequestEndpointTest {

    private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
    private static final LiteYukonUser NOT_AUTH_USER = MockRolePropertyDao.getUnAuthorizedUser();

    private CountOverridesTowardsLimitRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    private StarsEventLogService mockStarsEventLogService;
    
    private static final String RESP_ELEMENT_NAME = "countOverridesTowardsLimitResponse";
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @BeforeEach
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        mockStarsEventLogService = EventLogMockServiceFactory.getEventLogMockService(StarsEventLogService.class);

        impl = new CountOverridesTowardsLimitRequestEndpoint();
        impl.setOptOutService(mockOptOutService);
        impl.setStarsEventLogService(mockStarsEventLogService);
        impl.setRolePropertyDao(new MockRolePropertyDao());
    }
    
    @Test
    public void testInvokeSuccess() throws Exception {

    	// Load schemas
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CountOverridesTowardsLimitRequest.xsd",
    			this.getClass());
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CountOverridesTowardsLimitResponse.xsd",
    			this.getClass());
    	
    	// test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createCountOverridesRequestElement(
    			XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        Element respElement = impl.invoke(requestElement, NOT_AUTH_USER);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");
    	

        // test with authorized user, no program name
        //==========================================================================================
        respElement = impl.invoke(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        assertTrue(mockOptOutService.getLastValueCalled(), "changeOptOutCountStateForToday called with false, expected true");
        
        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
        // test with authorized user, with program name
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createCountOverridesRequestElement(
    			XmlVersionUtils.YUKON_MSG_VERSION_1_1, reqSchemaResource);
        
        Element tmpElement = XmlUtils.createStringElement("programName", ns, "TEST_PROGRAM");
        requestElement.addContent(tmpElement);
        respElement = impl.invoke(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        assertTrue(mockOptOutService.getLastValueCalled(), "changeOptOutCountStateForToday called with false, expected true");
        assertEquals("TEST_PROGRAM", mockOptOutService.getLastProgramNameCalled(), "unexpected programName");
        
        // create template and parse response data
        template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
    }

    private class MockOptOutService extends OptOutServiceAdapter {
    	
    	private Boolean lastValueCalled = null;
    	private String lastProgramNameCalled = null;
    	
    	public Boolean getLastValueCalled() {
    		return lastValueCalled;
    	}
    	public String getLastProgramNameCalled() {
			return lastProgramNameCalled;
		}
    	
    	@Override
    	public void changeOptOutCountStateForToday(LiteYukonUser user,
    			boolean optOutCounts) {
    		this.lastValueCalled = optOutCounts;
    	}
    	
    	@Override
    	public void changeOptOutCountStateForTodayByProgramName(LiteYukonUser user, boolean optOutCounts, String programName) {
    		this.lastValueCalled = optOutCounts;
    		this.lastProgramNameCalled = programName;
    	}
    }

}

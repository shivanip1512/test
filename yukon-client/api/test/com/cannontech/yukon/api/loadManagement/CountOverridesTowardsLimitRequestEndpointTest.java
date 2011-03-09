package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.events.service.EventLogMockServiceFactory;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.CountOverridesTowardsLimitRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlApiUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class CountOverridesTowardsLimitRequestEndpointTest {

    private CountOverridesTowardsLimitRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    private StarsEventLogService mockStarsEventLogService;
    
    private static final String RESP_ELEMENT_NAME = "countOverridesTowardsLimitResponse";
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
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
        LiteYukonUser user = MockRolePropertyDao.getUnAuthorizedUser();
        Element respElement = impl.invoke(requestElement, user);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");
    	

        // test with authorized user, no program name
        //==========================================================================================
        user = new LiteYukonUser();
        
        respElement = impl.invoke(requestElement, user);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        Assert.assertTrue("changeOptOutCountStateForToday called with false, expected true", 
        		mockOptOutService.getLastValueCalled());
        
        // create template and parse response data
        SimpleXPathTemplate template = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
        // test with authorized user, with program name
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createCountOverridesRequestElement(
    			XmlVersionUtils.YUKON_MSG_VERSION_1_1, reqSchemaResource);
        
        user = new LiteYukonUser();
        
        Element tmpElement = XmlUtils.createStringElement("programName", ns, "TEST_PROGRAM");
        requestElement.addContent(tmpElement);
        respElement = impl.invoke(requestElement, user);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        Assert.assertTrue("changeOptOutCountStateForToday called with false, expected true", mockOptOutService.getLastValueCalled());
        Assert.assertEquals("unexpected programName", "TEST_PROGRAM", mockOptOutService.getLastProgramNameCalled());
        
        // create template and parse response data
        template = XmlApiUtils.getXPathTemplateForElement(respElement);
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

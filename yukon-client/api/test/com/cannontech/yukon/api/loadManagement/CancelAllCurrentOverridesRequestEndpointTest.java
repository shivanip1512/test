package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.events.service.EventLogMockServiceFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.CancelAllCurrentOverridesRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlApiUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class CancelAllCurrentOverridesRequestEndpointTest {

    private CancelAllCurrentOverridesRequestEndpoint impl;
    private MockOptOutService mockOptOutService; 
    private StarsEventLogService mockStarsEventLogService;
    
    private static final String RESP_ELEMENT_NAME = "cancelAllCurrentOverridesResponse";
    
    @Before
    public void setUp() throws Exception {
        
    	mockOptOutService = new MockOptOutService();
    	mockStarsEventLogService = EventLogMockServiceFactory.getEventLogMockService(StarsEventLogService.class);
    	
    	impl = new CancelAllCurrentOverridesRequestEndpoint();
    	impl.setOptOutService(mockOptOutService);
    	impl.setStarsEventLogService(mockStarsEventLogService);
    	impl.setRolePropertyDao(new MockRolePropertyDao());
    }
    
    @Test
    public void testInvokeSuccess() throws Exception {

    	// Load schemas
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CancelAllCurrentOverridesRequest.xsd",
    			this.getClass());
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CancelAllCurrentOverridesResponse.xsd",
    			this.getClass());
    	
    	// test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createCancleCurrentOverridesRequestElement(
    			XmlVersionUtils.YUKON_MSG_VERSION_1_0, null, reqSchemaResource);
        LiteYukonUser user = MockRolePropertyDao.getUnAuthorizedUser();
        Element respElement = impl.invoke(requestElement, user);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");

    	
        // test with authorized user
        //==========================================================================================
    	user = new LiteYukonUser();
        respElement = impl.invoke(requestElement, user);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        Assert.assertEquals("cancelAllOptOuts was not called", 1, mockOptOutService.getCallCount());
        
        // create template and parse response data
        SimpleXPathTemplate template = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
        // test with program name
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createCancleCurrentOverridesRequestElement(
    			XmlVersionUtils.YUKON_MSG_VERSION_1_1, "Program1", reqSchemaResource);
    	user = new LiteYukonUser();
        respElement = impl.invoke(requestElement, user);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        Assert.assertEquals("cancelAllOptOuts was not called", 2, mockOptOutService.getCallCount());
        
        // create template and parse response data
        template = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        Assert.assertEquals("Wrong program name", "Program1", mockOptOutService.getProgramName());
        
    }
    
    
    private class MockOptOutService extends OptOutServiceAdapter {
    	
    	int callCount = 0;
    	String programName = null;
    	
    	public int getCallCount() {
    		return callCount;
    	}
    	
    	public String getProgramName() {
			return programName;
		}
    	
    	@Override
    	public void cancelAllOptOuts(LiteYukonUser user) {
    		this.callCount++;
    	}
    	
    	@Override
    	public void cancelAllOptOutsByProgramName(String programName, LiteYukonUser user) {
    		this.programName = programName;
    		this.callCount++;
    	}
    	
    }

}

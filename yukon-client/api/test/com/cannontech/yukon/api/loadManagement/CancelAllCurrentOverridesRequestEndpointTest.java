package com.cannontech.yukon.api.loadManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.events.loggers.StarsEventLogService;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.EventLogMockServiceFactory;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.CancelAllCurrentOverridesRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class CancelAllCurrentOverridesRequestEndpointTest {

	private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
    private static final LiteYukonUser NOT_AUTH_USER = MockRolePropertyDao.getUnAuthorizedUser();
	
    private CancelAllCurrentOverridesRequestEndpoint impl;
    private MockOptOutService mockOptOutService; 
    private StarsEventLogService mockStarsEventLogService;
    
    private static final String RESP_ELEMENT_NAME = "cancelAllCurrentOverridesResponse";
    
    @BeforeEach
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
        Element respElement = impl.invoke(requestElement, NOT_AUTH_USER);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");

    	
        // test with authorized user
        //==========================================================================================
        respElement = impl.invoke(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        assertEquals(1, mockOptOutService.getCallCount(), "cancelAllOptOuts was not called");
        
        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
        // test with program name
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createCancleCurrentOverridesRequestElement(
    			XmlVersionUtils.YUKON_MSG_VERSION_1_1, "Program1", reqSchemaResource);
        respElement = impl.invoke(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        assertEquals(2, mockOptOutService.getCallCount(), "cancelAllOptOuts was not called");
        
        // create template and parse response data
        template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        assertEquals("Program1", mockOptOutService.getProgramName(), "Wrong program name");
        
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

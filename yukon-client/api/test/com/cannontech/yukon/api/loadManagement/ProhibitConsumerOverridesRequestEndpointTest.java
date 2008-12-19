package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.endpoint.ProhibitConsumerOverridesRequestEndpoint;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProhibitConsumerOverridesRequestEndpointTest {

    private ProhibitConsumerOverridesRequestEndpoint impl;
    private MockOptOutService mockOptOutService; 
    
    private static final String RESP_ELEMENT_NAME = "prohibitConsumerOverridesResponse";
    
    @Before
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        
        impl = new ProhibitConsumerOverridesRequestEndpoint();
        impl.setOptOutService(mockOptOutService);
        impl.setAuthDao(new MockAuthDao());
    }
    
    private class MockOptOutService extends OptOutServiceAdapter {
        
    	private Boolean lastValueCalled = null;
    	
    	public Boolean getLastValueCalled() {
			return lastValueCalled;
		}
    	
    	@Override
    	public void changeOptOutEnabledStateForToday(LiteYukonUser user,
    			boolean optOutsEnabled) {

    		this.lastValueCalled = optOutsEnabled;
    	}
    }
    
    private class MockAuthDao extends AuthDaoAdapter {
    	
    	@Override
    	public void verifyTrueProperty(LiteYukonUser user,
    			int... rolePropertyIds) throws NotAuthorizedException {
    		if(user.getUserID() != 1) {
    			throw new NotAuthorizedException("Mock auth dao not authorized");
    		}
    	}
    }
    
    @Test
    public void testInvokeSuccess() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("ProhibitConsumerOverridesRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProhibitConsumerOverridesRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProhibitConsumerOverridesResponse.xsd",
        		this.getClass());

        //invoke test with unauthorized user
        LiteYukonUser user = new LiteYukonUser();
        user.setUserID(-1);
        Element respElement = impl.invoke(reqElement, user);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, "prohibitConsumerOverridesResponse", "UserNotAuthorized");

        
        //invoke test
        user = new LiteYukonUser();
        user.setUserID(1);
        respElement = impl.invoke(reqElement, user);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
        Assert.assertFalse("changeOptOutCountStateForToday called with true, expected false", 
        		mockOptOutService.getLastValueCalled());
        
    }

}


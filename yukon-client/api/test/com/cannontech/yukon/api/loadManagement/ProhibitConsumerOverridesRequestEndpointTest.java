package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
        impl.initialize();
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
    
    @Test
    public void testInvokeSuccess() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("ProhibitConsumerOverridesRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProhibitConsumerOverridesRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test
        LiteYukonUser user = new LiteYukonUser();
        Element respElement = impl.invoke(reqElement, user);
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProhibitConsumerOverridesResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
        Assert.assertFalse("changeOptOutCountStateForToday called with true, expected false", 
        		mockOptOutService.getLastValueCalled());
        
    }

}


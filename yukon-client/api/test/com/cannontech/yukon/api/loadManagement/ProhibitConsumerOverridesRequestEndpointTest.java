package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProhibitConsumerOverridesRequestEndpointTest {

    private ProhibitConsumerOverridesRequestEndpoint impl;
    private MockOverrideService mockOverrideService; 
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private static final String USER_SUCCESS = "Success";
    private static final String USER_FAILURE = "Failure";    
    private static final String RESP_ELEMENT_NAME = "prohibitConsumerOverridesResponse";
    
    //TODO Add/match with actual ErrorCodes thrown here
    private static final String ERROR_CODE = "ERROR_CODE";
    
    @Before
    public void setUp() throws Exception {
        
        mockOverrideService = new MockOverrideService();
        
        impl = new ProhibitConsumerOverridesRequestEndpoint();
        impl.setOverrideService(mockOverrideService);
        impl.initialize();
    }
    
    private class MockOverrideService extends OverrideServiceAdapter {
        
        @Override
        public void prohibitConsumerOverrides(LiteYukonUser user) {
            // TODO Match up here with expected exceptions that may be thrown
            if (!user.getUsername().equals(USER_SUCCESS)) {
                throw new StarsInvalidArgumentException("Invalid Arguments");
            }
        }
    }
    
    @Test
    public void testInvokeSuccess() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("ProhibitConsumerOverridesRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/schemas/loadManagement/ProhibitConsumerOverridesRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test
        LiteYukonUser user = new LiteYukonUser();
        user.setUsername(USER_SUCCESS);
        Element respElement = impl.invoke(reqElement, user);
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/schemas/loadManagement/ProhibitConsumerOverridesResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, RESP_ELEMENT_NAME);
        
    }

    @Test
    public void testInvokeFailure() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("ProhibitConsumerOverridesRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/schemas/loadManagement/ProhibitConsumerOverridesRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test
        LiteYukonUser user = new LiteYukonUser();
        user.setUsername(USER_FAILURE);        
        Element respElement = impl.invoke(reqElement, user);
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/schemas/loadManagement/ProhibitConsumerOverridesResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        //TODO Add/match with actual ErrorCodes thrown here        
        TestUtils.runFailureAssertions(template, RESP_ELEMENT_NAME, ERROR_CODE);
        
    }    
}


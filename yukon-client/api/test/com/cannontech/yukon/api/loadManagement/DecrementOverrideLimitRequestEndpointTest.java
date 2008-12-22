package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.endpoint.DecrementOverrideLimitRequestEndpoint;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class DecrementOverrideLimitRequestEndpointTest {

    private DecrementOverrideLimitRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    
    //test request xml data
    private static final String ACCOUNT_VALID = "ACCOUNT_VALID";
    private static final String SERIAL_VALID = "SERIAL_VALID";    
    private static final String PROGRAM_VALID = "PROGRAM_VALID";

    //test response xml data
    static final String responseElementStr = "/y:decrementDeviceOverrideLimitResponse";   
    
    @Before
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        
        impl = new DecrementOverrideLimitRequestEndpoint();
        impl.setOptOutService(mockOptOutService);
        impl.setAuthDao(new MockAuthDao());
    }
    
    private class MockOptOutService extends OptOutServiceAdapter {
        
        private String accountNumber;
        private String serialNumber;
        private String programName;
        
        //TODO add in the programName parameter to this method
        @Override
        public void allowAdditionalOptOuts(String accountNumber,
                String serialNumber, int additionalOptOuts, LiteYukonUser user)
                throws InventoryNotFoundException, AccountNotFoundException {
            
            this.accountNumber = accountNumber;
            this.serialNumber = serialNumber;
            this.programName = programName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }
        
        public String getSerialNumber() {
            return serialNumber;
        }

        public String getProgramName() {
            return programName;
        }
    }
    
    private class MockAuthDao extends AuthDaoAdapter {
        
        @Override
        public void verifyTrueProperty(LiteYukonUser user,
                int... rolePropertyIds) throws NotAuthorizedException {
            if(user.getUserID() < 1) {
                throw new NotAuthorizedException("Mock auth dao not authorized");
            }
        }
    }
   
    @Test
    public void testInvoke() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("DecrementDeviceOverrideLimitRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/DecrementDeviceOverrideLimitRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test with unauthorized user
        LiteYukonUser user = new LiteYukonUser();
        user.setUserID(-1);
        Element respElement = impl.invoke(reqElement, user);
        
        // validate the response against schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/DecrementDeviceOverrideLimitResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify the results
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, responseElementStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);        
        TestUtils.runFailureAssertions(template, responseElementStr, "UserNotAuthorized");
        
        //invoke test
        user.setUserID(10);
        respElement = impl.invoke(reqElement, user);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT_VALID, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect serialNumber.", SERIAL_VALID, mockOptOutService.getSerialNumber());
        //TODO enable the programName assertion, after it is added to service method
        //Assert.assertEquals("Incorrect programName.", PROGRAM_VALID, mockOptOutService.getProgramName());        
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, responseElementStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        TestUtils.runSuccessAssertion(template, responseElementStr);
    }
}


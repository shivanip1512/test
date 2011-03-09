package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ResetOverrideCountBySerialNumberRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlApiUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ResetOverrideCountBySerialNumberRequestEndpointTest {

    private ResetOverrideCountBySerialNumberRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    
    //test request xml data
    private static final String ACCOUNT1 = "account1";
    private static final String SERIAL1 = "serial1";    
    private static final String INVALID_ACCOUNT = "INVALID_ACCOUNT";
    private static final String INVALID_SERIAL = "INVALID_SERIAL";    
    private static final String INCORRECT_SERIAL = "INCORRECT_SERIAL";    

    //test response xml data
    static final String RESP_ELEMENT_NAME = "resetOverrideCountBySerialNumberResponse";   
    
    @Before
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        
        impl = new ResetOverrideCountBySerialNumberRequestEndpoint();
        impl.setOptOutService(mockOptOutService);
        impl.setRolePropertyDao(new MockRolePropertyDao());
    }
   
    @Test
    public void testInvoke() throws Exception {

    	// Load schemas
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ResetOverrideCountBySerialNumberRequest.xsd", this.getClass());
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ResetOverrideCountBySerialNumberResponse.xsd", this.getClass());
    	
    	// test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createResetOverrideBySerialNumberRequestElement(ACCOUNT1, SERIAL1, XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        LiteYukonUser user = MockRolePropertyDao.getUnAuthorizedUser();
        Element respElement = impl.invoke(requestElement, user);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");
    	
        // test with valid account, serial, authorized user
        //==========================================================================================
    	user = new LiteYukonUser();
        respElement = impl.invoke(requestElement, user);
        
        // validate the response against schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect serialNumber.", SERIAL1, mockOptOutService.getSerialNumber());

        //verify the results
        outputTemplate = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);        
        TestUtils.runSuccessAssertion(outputTemplate, RESP_ELEMENT_NAME);

        // test with invalid account, valid serial, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createResetOverrideBySerialNumberRequestElement(INVALID_ACCOUNT, SERIAL1, XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        respElement = impl.invoke(requestElement, user);
        
        // validate the response against schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", INVALID_ACCOUNT, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect serialNumber.", SERIAL1, mockOptOutService.getSerialNumber());
        
        //verify the results
        outputTemplate = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);        
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "InvalidAccountNumber");
        
        // test with valid account, invalid serial, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createResetOverrideBySerialNumberRequestElement(ACCOUNT1, INVALID_SERIAL, XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        respElement = impl.invoke(requestElement, user);
        
        // validate the response against schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect serialNumber.", INVALID_SERIAL, mockOptOutService.getSerialNumber());
        
        //verify the results
        outputTemplate = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);        
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "InvalidSerialNumber");
        
        // test with valid account, valid serial (not associated with account), authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createResetOverrideBySerialNumberRequestElement(
        		ACCOUNT1, INCORRECT_SERIAL, XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        respElement = impl.invoke(requestElement, user);
        
        // validate the response against schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect serialNumber.", INCORRECT_SERIAL, mockOptOutService.getSerialNumber());
        
        //verify the results
        outputTemplate = XmlApiUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);        
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "InvalidSerialNumber");
    }
    
    private class MockOptOutService extends OptOutServiceAdapter {
        
        private String accountNumber;
        private String serialNumber;
        
        @Override
        public void resetOptOutLimitForInventory(String accountNumber, String serialNumber, LiteYukonUser user) throws InventoryNotFoundException, AccountNotFoundException, IllegalArgumentException {
        	
        	this.accountNumber = accountNumber;
            this.serialNumber = serialNumber;
            
            if(INVALID_ACCOUNT.equals(accountNumber)) {
        		throw new AccountNotFoundException("Account invalid");
        	}
        	
        	if(INVALID_SERIAL.equals(serialNumber)) {
        		throw new InventoryNotFoundException("Serial number invalid");
        	}
        	
        	if(INCORRECT_SERIAL.equals(serialNumber)) {
        		throw new InventoryNotFoundException("Serial number incorrect");
        	}
    	}

        public String getAccountNumber() {
            return accountNumber;
        }
        
        public String getSerialNumber() {
            return serialNumber;
        }
    }
}


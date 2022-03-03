package com.cannontech.yukon.api.loadManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ResetOverrideCountBySerialNumberRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockAccountEventLogService;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ResetOverrideCountBySerialNumberRequestEndpointTest {
	
	private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
	private static final LiteYukonUser NOT_AUTH_USER = MockRolePropertyDao.getUnAuthorizedUser();
	
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
    
    @BeforeEach
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        
        impl = new ResetOverrideCountBySerialNumberRequestEndpoint();
        impl.setOptOutService(mockOptOutService);
        impl.setRolePropertyDao(new MockRolePropertyDao());
        impl.setAccountEventLogService(new MockAccountEventLogService());
    }
   
    @Test
    public void testInvoke() throws Exception {

    	// Load schemas
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ResetOverrideCountBySerialNumberRequest.xsd", this.getClass());
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ResetOverrideCountBySerialNumberResponse.xsd", this.getClass());
    	
    	// test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createResetOverrideBySerialNumberRequestElement(ACCOUNT1, SERIAL1, XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        Element respElement = impl.invoke(requestElement, NOT_AUTH_USER);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "UserNotAuthorized");
    	
        // test with valid account, serial, authorized user
        //==========================================================================================
        respElement = impl.invoke(requestElement, AUTH_USER);
        
        // validate the response against schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        assertEquals(ACCOUNT1, mockOptOutService.getAccountNumber(), "Incorrect accountNumber.");
        assertEquals(SERIAL1, mockOptOutService.getSerialNumber(), "Incorrect serialNumber.");

        //verify the results
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);        
        TestUtils.runSuccessAssertion(outputTemplate, RESP_ELEMENT_NAME);

        // test with invalid account, valid serial, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createResetOverrideBySerialNumberRequestElement(INVALID_ACCOUNT, SERIAL1, XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        respElement = impl.invoke(requestElement, AUTH_USER);
        
        // validate the response against schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        assertEquals(INVALID_ACCOUNT, mockOptOutService.getAccountNumber(), "Incorrect accountNumber.");
        assertEquals(SERIAL1, mockOptOutService.getSerialNumber(), "Incorrect serialNumber.");
        
        //verify the results
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);        
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "InvalidAccountNumber");
        
        // test with valid account, invalid serial, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createResetOverrideBySerialNumberRequestElement(ACCOUNT1, INVALID_SERIAL, XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        respElement = impl.invoke(requestElement, AUTH_USER);
        
        // validate the response against schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        assertEquals(ACCOUNT1, mockOptOutService.getAccountNumber(), "Incorrect accountNumber.");
        assertEquals(INVALID_SERIAL, mockOptOutService.getSerialNumber(), "Incorrect serialNumber.");
        
        //verify the results
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, RESP_ELEMENT_NAME, XmlVersionUtils.YUKON_MSG_VERSION_1_0);        
        TestUtils.runFailureAssertions(outputTemplate, RESP_ELEMENT_NAME, "InvalidSerialNumber");
        
        // test with valid account, valid serial (not associated with account), authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createResetOverrideBySerialNumberRequestElement(
        		ACCOUNT1, INCORRECT_SERIAL, XmlVersionUtils.YUKON_MSG_VERSION_1_0, reqSchemaResource);
        respElement = impl.invoke(requestElement, AUTH_USER);
        
        // validate the response against schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        assertEquals(ACCOUNT1, mockOptOutService.getAccountNumber(), "Incorrect accountNumber.");
        assertEquals(INCORRECT_SERIAL, mockOptOutService.getSerialNumber(), "Incorrect serialNumber.");
        
        //verify the results
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
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


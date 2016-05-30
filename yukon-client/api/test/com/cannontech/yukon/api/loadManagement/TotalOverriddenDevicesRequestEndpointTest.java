package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import org.jdom2.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.TotalOverriddenDevicesRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class TotalOverriddenDevicesRequestEndpointTest {

	private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
	private static final LiteYukonUser NOT_AUTH_USER = MockRolePropertyDao.getUnAuthorizedUser();
	
    private TotalOverriddenDevicesRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    
    //test request xml data
    private static final String ACCOUNT1 = "account1";
    private static final String PROGRAM1 = "program1";    
    private static final String START_DATE_VALID = "2008-09-30T12:00:00Z";
    private static final String STOP_DATE_VALID = "2008-09-30T23:59:59Z";

    private static final String INVALID_ACCOUNT = "INVALID_ACCOUNT";
    private static final String INVALID_PROGRAM = "INVALID_PROGRAM";    
    
    private static final String VERSION_1 = XmlVersionUtils.YUKON_MSG_VERSION_1_0;

    //test response xml data
    static final String byAccountResponseStr = "/y:totalOverriddenDevicesByAccountNumberResponse";   
    static final String byAccountTotalDevicesStr = byAccountResponseStr + "/y:totalDevices";
    
    static final String byProgramResponseStr = "/y:totalOverriddenDevicesByProgramNameResponse";   
    static final String byProgramTotalDevicesStr = byProgramResponseStr + "/y:totalDevices";    
    
    @Before
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        
        impl = new TotalOverriddenDevicesRequestEndpoint();
        impl.setOptOutService(mockOptOutService);
        impl.setRolePropertyDao(new MockRolePropertyDao());
    }
    
    @Test
    public void testInvokeDevicesByAccountSuccess() throws Exception {

    	// Load the schemas
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByAccountNumberRequest.xsd",
    			this.getClass());
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByAccountNumberResponse.xsd",
    			this.getClass());
    	
        
    	// test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createOverridenDevicesByAccountRequestElement(
    			ACCOUNT1, null, START_DATE_VALID, STOP_DATE_VALID, VERSION_1, reqSchemaResource);
        Element respElement = impl.invokeDevicesByAccount(requestElement, NOT_AUTH_USER);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, "totalOverriddenDevicesByAccountNumberResponse", "UserNotAuthorized");
        
        
        // test with valid account number, no program, authorized user
    	//==========================================================================================
        requestElement = LoadManagementTestUtils.createOverridenDevicesByAccountRequestElement(
    			ACCOUNT1, null, START_DATE_VALID, STOP_DATE_VALID, VERSION_1, reqSchemaResource);

        respElement = impl.invokeDevicesByAccount(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertNull("Incorrect programName", mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));

        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, byAccountResponseStr, VERSION_1);
       
        long totalDevicesResult = outputTemplate.evaluateAsLong(byAccountTotalDevicesStr);

        // verify data in the response
        Assert.assertEquals("Incorrect totalDevices", AUTH_USER.getUsername().length(), totalDevicesResult);

        
        // test with invalid account number, no program, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverridenDevicesByAccountRequestElement(
        		INVALID_ACCOUNT, null, START_DATE_VALID, STOP_DATE_VALID, VERSION_1, reqSchemaResource);
        
        respElement = impl.invokeDevicesByAccount(requestElement, AUTH_USER);

        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", INVALID_ACCOUNT, mockOptOutService.getAccountNumber());
        Assert.assertNull("Incorrect programName", mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));
        
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, "totalOverriddenDevicesByAccountNumberResponse", "InvalidAccountNumber");
        
        
        // test with valid account number, valid program, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverridenDevicesByAccountRequestElement(
        		ACCOUNT1, PROGRAM1, START_DATE_VALID, STOP_DATE_VALID, VERSION_1, reqSchemaResource);
        
        respElement = impl.invokeDevicesByAccount(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect accountNumber.", PROGRAM1, mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));
        
        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, byAccountResponseStr, VERSION_1);
       
        totalDevicesResult = outputTemplate.evaluateAsLong(byAccountTotalDevicesStr);

        // verify data in the response
        Assert.assertEquals("Incorrect totalDevices", AUTH_USER.getUsername().length(), totalDevicesResult);

        
        // test with valid account number, invalid program, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverridenDevicesByAccountRequestElement(
        		ACCOUNT1, INVALID_PROGRAM, START_DATE_VALID, STOP_DATE_VALID, VERSION_1, reqSchemaResource);
        
        respElement = impl.invokeDevicesByAccount(requestElement, AUTH_USER);

        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect accountNumber.", INVALID_PROGRAM, mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));
       
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, "totalOverriddenDevicesByAccountNumberResponse", "InvalidProgramName");
        
    }
    
    @Test
    public void testInvokeDevicesByProgramSuccess() throws Exception {
        
    	// Load schemas
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByProgramNameRequest.xsd",
    			this.getClass());
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByProgramNameResponse.xsd",
    			this.getClass());
    	
    	
    	// test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createOverridenDevicesByProgramRequestElement(
    			PROGRAM1, START_DATE_VALID, STOP_DATE_VALID, VERSION_1, reqSchemaResource);
        Element respElement = impl.invokeDevicesByProgram(requestElement, NOT_AUTH_USER);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, "totalOverriddenDevicesByProgramNameResponse", "UserNotAuthorized");
        

        // test with valid program, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverridenDevicesByProgramRequestElement(
        		PROGRAM1, START_DATE_VALID, STOP_DATE_VALID, VERSION_1, reqSchemaResource);

        respElement = impl.invokeDevicesByProgram(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect programName.", PROGRAM1, mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));

        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, byProgramResponseStr, VERSION_1);
       
        long totalDevicesResult = outputTemplate.evaluateAsLong(byProgramTotalDevicesStr);

        // verify data in the response
        Assert.assertEquals("Incorrect totalDevices", totalDevicesResult, AUTH_USER.getUsername().length());
        
        
        // test with valid program, authorized user
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverridenDevicesByProgramRequestElement(
        		INVALID_PROGRAM, START_DATE_VALID, STOP_DATE_VALID, VERSION_1, reqSchemaResource);
        respElement = impl.invokeDevicesByProgram(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect programName.", INVALID_PROGRAM, mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));
        
        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, "totalOverriddenDevicesByProgramNameResponse", "InvalidProgramName");
        
    }
    
    private class MockOptOutService extends OptOutServiceAdapter {
        
        private String accountNumber;
        private Date startTime;
        private Date stopTime;
        private String programName;
        
        @Override
        public int getOptOutDeviceCountForAccount(String accountNumber,
        		Date startTime, Date stopTime, LiteYukonUser user, String programName) {
        	
        	this.accountNumber = accountNumber;
        	this.programName = programName;
        	this.startTime = startTime;
        	this.stopTime = stopTime;

        	if(INVALID_ACCOUNT.equals(accountNumber)) {
        		throw new AccountNotFoundException("Account invalid");
        	}
        	
        	if(INVALID_PROGRAM.equals(programName)) {
        		throw new ProgramNotFoundException("Program invalid");
        	}
        	
        	//Returns the user's username length because we need _some_ value that gets passed all the way through.
        	return user.getUsername().length();
        }
        
        @Override
        public int getOptOutDeviceCountForProgram(String programName,
        		Date startTime, Date stopTime, LiteYukonUser user) {

        	this.programName = programName;            
        	this.startTime = startTime;
        	this.stopTime = stopTime;
        	
        	if(INVALID_PROGRAM.equals(programName)) {
        		throw new ProgramNotFoundException("Program invalid");
        	}
        	
        	//Returns the user's username length because we need _some_ value that gets passed all the way through.
        	return user.getUsername().length();
        }
        
        public String getAccountNumber() {
            return accountNumber;
        }
        public Date getStartTime() {
            return startTime;
        }
        public Date getStopTime() {
            return stopTime;
        }
        public String getProgramName() {
            return programName;
        }
    }
    
}


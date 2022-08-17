package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.jdom2.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.w3c.dom.Node;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.model.OverrideStatus;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.yukon.api.loadManagement.adapters.OptOutServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.OverrideHistoryRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class OverrideHistoryRequestEndpointTest {

	private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();
	private static final LiteYukonUser NOT_AUTH_USER = MockRolePropertyDao.getUnAuthorizedUser();
	
    private OverrideHistoryRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    
    //test request xml data
    private static final String ACCOUNT1 = "account1";
    private static final String ACCOUNT2 = "account2";
    private static final String PROGRAM1 = "program1";
    private static final String PROGRAM2 = "program2";
    private static final String PROGRAM1PAO = "program1Pao";
    private static final String PROGRAM2PAO = "program2Pao";
    private static final String EMTPY_RETURN = "EMPTY";

    private static final String INVALID_ACCOUNT = "ACCOUNT_INVALID";
    private static final String INVALID_PROGRAM = "PROGRAM_INVALID";    

    private static final String START_DATE_VALID = "2008-09-30T12:00:00Z";
    private static final String STOP_DATE_VALID = "2008-09-30T23:59:59Z";
    private static final String SCHEDULED_DATE_VALID = "2008-09-29T12:00:00Z";
    
    private static final String VERSION_1_0 = XmlVersionUtils.YUKON_MSG_VERSION_1_0;
    private static final String VERSION_1_1 = XmlVersionUtils.YUKON_MSG_VERSION_1_1;
    
    private OverrideHistory history1 = null;
    private OverrideHistory history2 = null;
    private OverrideHistory history3 = null;

    private OverrideHistory history1v1_1Result = null;
    private OverrideHistory history2v1_1Result = null;
    private OverrideHistory history3v1_1Result = null;
    
    //test response xml data
    static final String byAccountResponseStr = "/y:overrideHistoryByAccountNumberResponse";   
    static final String byAccountHistoryElementStr = byAccountResponseStr + "/y:overrideHistoryEntries/y:overrideHistory";
    
    static final String byProgramResponseStr = "/y:overrideHistoryByProgramNameResponse";   
    static final String byProgramHistoryElementStr = byProgramResponseStr + "/y:overrideHistoryEntries/y:overrideHistory";    
    
    // both "by acct num" and "by program name v1.0" use the same response format
    private static OverrideHistoryNodeMapper_v1_0 byAccountNumber_overrideHistNodeMapper = new OverrideHistoryNodeMapper_v1_0();
    private static OverrideHistoryNodeMapper_v1_0 byProgramName_overrideHistNodeMapper_v1_0 = new OverrideHistoryNodeMapper_v1_0();
    private static OverrideHistoryNodeMapper_v1_1 byProgramName_overrideHistNodeMapper_v1_1 = new OverrideHistoryNodeMapper_v1_1();
    

    @Before
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        
        impl = new OverrideHistoryRequestEndpoint();
        ReflectionTestUtils.setField(impl, "optOutService", mockOptOutService);
        ReflectionTestUtils.setField(impl, "rolePropertyDao", new MockRolePropertyDao());
        
        // Setup test histories
        history1 = new OverrideHistory();
        history1.setSerialNumber("serial1");
        Program p1 = new Program();
        p1.setProgramName(PROGRAM1);
        p1.setProgramPaoName(PROGRAM1PAO);
        history1.setPrograms(Collections.singletonList(p1));
        history1.setAccountNumber(ACCOUNT1);
        history1.setStatus(OverrideStatus.Active);
        history1.setScheduledDate(Iso8601DateUtil.parseIso8601Date(SCHEDULED_DATE_VALID));
        history1.setStartDate(Iso8601DateUtil.parseIso8601Date(START_DATE_VALID));
        history1.setStopDate(Iso8601DateUtil.parseIso8601Date(STOP_DATE_VALID));
        history1.setUserName("user1");
        history1.setOverrideNumber(1);
        history1.setCountedAgainstLimit(true);

        history2 = new OverrideHistory();
        history2.setSerialNumber("serial2");
        Program p2 = new Program();
        p2.setProgramName(PROGRAM2);
        p2.setProgramPaoName(PROGRAM2PAO);
        history2.setPrograms(Collections.singletonList(p2));
        history2.setAccountNumber(ACCOUNT1);
        history2.setStatus(OverrideStatus.Active);
        history2.setScheduledDate(Iso8601DateUtil.parseIso8601Date(SCHEDULED_DATE_VALID));
        history2.setStartDate(Iso8601DateUtil.parseIso8601Date(START_DATE_VALID));
        history2.setStopDate(Iso8601DateUtil.parseIso8601Date(STOP_DATE_VALID));
        history2.setUserName("user1");
        history2.setOverrideNumber(2);
        history2.setCountedAgainstLimit(true);
        
        history3 = new OverrideHistory();
        history3.setSerialNumber("serial3");
        Program p3 = new Program();
        p3.setProgramName(PROGRAM1);
        p3.setProgramPaoName(PROGRAM1PAO);
        history3.setPrograms(Collections.singletonList(p3));
        history3.setAccountNumber(ACCOUNT2);
        history3.setStatus(OverrideStatus.Active);
        history3.setScheduledDate(Iso8601DateUtil.parseIso8601Date(SCHEDULED_DATE_VALID));
        history3.setStartDate(Iso8601DateUtil.parseIso8601Date(START_DATE_VALID));
        history3.setStopDate(Iso8601DateUtil.parseIso8601Date(STOP_DATE_VALID));
        history3.setUserName("user1");
        history3.setOverrideNumber(2);
        history3.setCountedAgainstLimit(true);

        // Setup test histories
        history1v1_1Result = new OverrideHistory();
        history1v1_1Result.setSerialNumber("serial1");
        Program p1v1_1 = new Program();
        p1v1_1.setProgramName(PROGRAM1PAO);
        history1v1_1Result.setPrograms(Collections.singletonList(p1v1_1));
        history1v1_1Result.setAccountNumber(ACCOUNT1);
        history1v1_1Result.setStatus(OverrideStatus.Active);
        history1v1_1Result.setScheduledDate(Iso8601DateUtil.parseIso8601Date(SCHEDULED_DATE_VALID));
        history1v1_1Result.setStartDate(Iso8601DateUtil.parseIso8601Date(START_DATE_VALID));
        history1v1_1Result.setStopDate(Iso8601DateUtil.parseIso8601Date(STOP_DATE_VALID));
        history1v1_1Result.setUserName("user1");
        history1v1_1Result.setOverrideNumber(1);
        history1v1_1Result.setCountedAgainstLimit(true);

        history2v1_1Result = new OverrideHistory();
        history2v1_1Result.setSerialNumber("serial2");
        Program p2v1_1 = new Program();
        p2v1_1.setProgramName(PROGRAM2PAO);
        p2v1_1.setProgramPaoName(PROGRAM2PAO);
        history2v1_1Result.setPrograms(Collections.singletonList(p2v1_1));
        history2v1_1Result.setAccountNumber(ACCOUNT1);
        history2v1_1Result.setStatus(OverrideStatus.Active);
        history2v1_1Result.setScheduledDate(Iso8601DateUtil.parseIso8601Date(SCHEDULED_DATE_VALID));
        history2v1_1Result.setStartDate(Iso8601DateUtil.parseIso8601Date(START_DATE_VALID));
        history2v1_1Result.setStopDate(Iso8601DateUtil.parseIso8601Date(STOP_DATE_VALID));
        history2v1_1Result.setUserName("user1");
        history2v1_1Result.setOverrideNumber(2);
        history2v1_1Result.setCountedAgainstLimit(true);
        
        history3v1_1Result = new OverrideHistory();
        history3v1_1Result.setSerialNumber("serial3");
        Program p3v1_1 = new Program();
        p3v1_1.setProgramName(PROGRAM1PAO);
        p3v1_1.setProgramPaoName(PROGRAM1PAO);
        history3v1_1Result.setPrograms(Collections.singletonList(p3v1_1));
        history3v1_1Result.setAccountNumber(ACCOUNT2);
        history3v1_1Result.setStatus(OverrideStatus.Active);
        history3v1_1Result.setScheduledDate(Iso8601DateUtil.parseIso8601Date(SCHEDULED_DATE_VALID));
        history3v1_1Result.setStartDate(Iso8601DateUtil.parseIso8601Date(START_DATE_VALID));
        history3v1_1Result.setStopDate(Iso8601DateUtil.parseIso8601Date(STOP_DATE_VALID));
        history3v1_1Result.setUserName("user1");
        history3v1_1Result.setOverrideNumber(2);
        history3v1_1Result.setCountedAgainstLimit(true);
    }
    
    
    @Test
    public void testInvokeHistoryByAccount() throws Exception {
        
    	// Load Schemas
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideHistoryByAccountNumberRequest.xsd",
    			this.getClass());
    	
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideHistoryByAccountNumberResponse.xsd",
    			this.getClass());

        // test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createOverrideHistoryByAccountRequestElement(
    			ACCOUNT1, null, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
    	
        Element respElement = impl.invokeHistoryByAccount(requestElement, NOT_AUTH_USER);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(
        		outputTemplate, "overrideHistoryByAccountNumberResponse", "UserNotAuthorized");
        
        // test with valid account, no program, valid user; to return empty list
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByAccountRequestElement(
                EMTPY_RETURN, null, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
        respElement = impl.invokeHistoryByAccount(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", EMTPY_RETURN, mockOptOutService.getAccountNumber());
        Assert.assertNull(mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));

        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, byAccountResponseStr, VERSION_1_0);
       
        // Check result xml values
        List<OverrideHistory> expected = new ArrayList<OverrideHistory>();
        
        List<OverrideHistory> actual = 
            outputTemplate.evaluate(byAccountHistoryElementStr, byAccountNumber_overrideHistNodeMapper);

        Assert.assertEquals("Result list not as expected", expected, actual);        
        
        // test with valid account, no program, valid user 
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByAccountRequestElement(
    			ACCOUNT1, null, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
        respElement = impl.invokeHistoryByAccount(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertNull(mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));

        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, byAccountResponseStr, VERSION_1_0);
       
        // Check result xml values
        expected = new ArrayList<OverrideHistory>();
        expected.add(history1);
        expected.add(history2);
        
        actual = outputTemplate.evaluate(byAccountHistoryElementStr, byAccountNumber_overrideHistNodeMapper);

        Assert.assertEquals("Result list not as expected", expected, actual);
        
        
        // test with valid account, program, user 
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByAccountRequestElement(
    			ACCOUNT1, PROGRAM1, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
        respElement = impl.invokeHistoryByAccount(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect programName", PROGRAM1, mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));

        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, byAccountResponseStr, VERSION_1_0);
       
        // Check result xml values
        expected = new ArrayList<OverrideHistory>();
        expected.add(history1);
        
        actual = outputTemplate.evaluate(byAccountHistoryElementStr, byAccountNumber_overrideHistNodeMapper);

        Assert.assertEquals("Result list not as expected", expected, actual);

        // test with invalid account, valid user 
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByAccountRequestElement(
        		INVALID_ACCOUNT, null, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
        respElement = impl.invokeHistoryByAccount(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", INVALID_ACCOUNT, mockOptOutService.getAccountNumber());
        Assert.assertNull("Incorrect programName", mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));
        
        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(
        		outputTemplate, "overrideHistoryByAccountNumberResponse", "InvalidAccountNumber");
        
        // test with valid account, invalid program, valid user 
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByAccountRequestElement(
        		ACCOUNT1, INVALID_PROGRAM, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
        respElement = impl.invokeHistoryByAccount(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT1, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect programName", INVALID_PROGRAM, mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, Iso8601DateUtil.formatIso8601Date(mockOptOutService.getStopTime()));
        
        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(
        		outputTemplate, "overrideHistoryByAccountNumberResponse", "InvalidProgramName");
        
    }
    
    @Test
    public void testInvokeOverrideByProgramSuccess() throws Exception {
        
    	// Load Schemas
    	Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideHistoryByProgramNameRequest.xsd",
    			this.getClass());
    	Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideHistoryByProgramNameResponse.xsd",
    			this.getClass());
    	
    	
    	// test with unauthorized user
    	//==========================================================================================
    	Element requestElement = LoadManagementTestUtils.createOverrideHistoryByProgramRequestElement(
    			PROGRAM1, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
    	
        Element respElement = impl.invokeHistoryByProgram(requestElement, NOT_AUTH_USER);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(
        		outputTemplate, "overrideHistoryByProgramNameResponse", "UserNotAuthorized");
        
        // test with valid program, user; to return empty list (v1.0)
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByProgramRequestElement(
                EMTPY_RETURN, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
        
        respElement = impl.invokeHistoryByProgram(requestElement, AUTH_USER);

        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byProgramResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
       
        List<OverrideHistory> actual = template.evaluate(byProgramHistoryElementStr, byProgramName_overrideHistNodeMapper_v1_0);
        // Check result xml values
        List<OverrideHistory> expected = new ArrayList<OverrideHistory>();
        
        Assert.assertEquals("Result list not as expected", expected, actual);
        
        // test with valid program, user; to return empty list (v1.1)
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByProgramRequestElement(
                EMTPY_RETURN, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_1, reqSchemaResource);
        
        respElement = impl.invokeHistoryByProgram(requestElement, AUTH_USER);

        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byProgramResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
       
        actual = template.evaluate(byProgramHistoryElementStr, byProgramName_overrideHistNodeMapper_v1_1);
        // Check result xml values
        expected = new ArrayList<OverrideHistory>();
        
        Assert.assertEquals("Result list not as expected", expected, actual);
        
        // test with valid program, user (v1.0)
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByProgramRequestElement(
    			PROGRAM1, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
    	
        respElement = impl.invokeHistoryByProgram(requestElement, AUTH_USER);

        // verify the respElement is valid according to schema
        XmlUtils.printElement(respElement, "respElement");
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byProgramResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
       
        actual = template.evaluate(byProgramHistoryElementStr, byProgramName_overrideHistNodeMapper_v1_0);
        // Check result xml values
        expected = new ArrayList<OverrideHistory>();
        expected.add(history1);
        expected.add(history3);
        
        Assert.assertEquals("Result list not as expected", expected, actual);
        
        // test with valid program, user (v1.1)
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByProgramRequestElement(
    			PROGRAM1, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_1, reqSchemaResource);
    	
        respElement = impl.invokeHistoryByProgram(requestElement, AUTH_USER);

        // verify the respElement is valid according to schema
        XmlUtils.printElement(respElement, "respElement");
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        template = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byProgramResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
       
        actual = template.evaluate(byProgramHistoryElementStr, byProgramName_overrideHistNodeMapper_v1_1);
        // Check result xml values
        expected = new ArrayList<OverrideHistory>();
        expected.add(history1v1_1Result);
        expected.add(history3v1_1Result);
        
        Assert.assertEquals("Result list not as expected", expected, actual);

        // test with invalid program, valid user (v1.0)
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByProgramRequestElement(
        		INVALID_PROGRAM, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_0, reqSchemaResource);
        
        respElement = impl.invokeHistoryByProgram(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(
        		outputTemplate, "overrideHistoryByProgramNameResponse", "InvalidProgramName");
        
        // test with invalid program, valid user (v1.1)
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createOverrideHistoryByProgramRequestElement(
        		INVALID_PROGRAM, START_DATE_VALID, STOP_DATE_VALID, VERSION_1_1, reqSchemaResource);
        
        respElement = impl.invokeHistoryByProgram(requestElement, AUTH_USER);
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        // create template and parse response data
        outputTemplate = YukonXml.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(
        		outputTemplate, "overrideHistoryByProgramNameResponse", "InvalidProgramName");
        
    }
    
    // Mock Services and Daos
    
    private class MockOptOutService extends OptOutServiceAdapter {
        
        private String accountNumber;
        private Date startTime;
        private Date stopTime;
        private String programName;
        
        @Override
        public List<OverrideHistory> getOptOutHistoryForAccount(
        		String accountNumber, Date startTime, Date stopTime,
        		LiteYukonUser user, String programName) {

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

        	
        	List<OverrideHistory> overrideHistoryList = new ArrayList<OverrideHistory>();
        	if (ACCOUNT1.equals(accountNumber)) {
        		
        		if (programName == null) {
        			overrideHistoryList.add(history1);
        			overrideHistoryList.add(history2);
        		} else if(PROGRAM1.equals(programName)) {
        			overrideHistoryList.add(history1);
        		} else if(PROGRAM2.equals(programName)) {
        			overrideHistoryList.add(history2);
        		} else {
        			throw new UnsupportedOperationException(
        					"Program: " + programName + " not supported");
        		}
        	} else if (ACCOUNT2.equals(accountNumber)) {
        		
        		if (programName == null) {
        			overrideHistoryList.add(history3);
        		} else if(PROGRAM1.equals(programName)) {
        			overrideHistoryList.add(history3);
        		} else if(PROGRAM2.equals(programName)) {
        			// no history
        		} else {
        			throw new UnsupportedOperationException(
        					"Program: " + programName + " not supported");
        		}
        	} else if (EMTPY_RETURN.equals(accountNumber)){
        	    //return empty list
        	}
        	else {
        		throw new UnsupportedOperationException(
    					"Account Number: " + accountNumber + " not supported");
        	}
            
            return overrideHistoryList;
        }
        
        @Override
        public List<OverrideHistory> getOptOutHistoryByProgram(
        		String programName, Date startTime, Date stopTime,
        		LiteYukonUser user) {

        	this.programName = programName;            
        	this.startTime = startTime;
        	this.stopTime = stopTime;

        	if(INVALID_PROGRAM.equals(programName)) {
        		throw new ProgramNotFoundException("Program invalid");
        	}

            List<OverrideHistory> overrideHistoryList = new ArrayList<OverrideHistory>();
        	if (PROGRAM1.equals(programName)) {
        		overrideHistoryList.add(history1);
    			overrideHistoryList.add(history3);
    			
        	} else if (EMTPY_RETURN.equals(programName)) {
        		//return empty list
        		
        	} else {
        		throw new UnsupportedOperationException(
    					"Program name: " + programName + " not supported");
        	}
            
            return overrideHistoryList;
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
    
    /**
     * Mapper class to map an xml node into an OverrideHistory object
     */
    private static class OverrideHistoryNodeMapper_v1_0 implements
            ObjectMapper<Node, OverrideHistory> {

        @Override
        public OverrideHistory map(Node from) throws ObjectMappingException {
            // create template and parse data
            SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(from);

            OverrideHistory overrideHist = new OverrideHistory();
            overrideHist.setSerialNumber(template.evaluateAsString("y:serialNumber"));
            Program p = new Program();
            p.setProgramName(template.evaluateAsString("y:programName"));
            overrideHist.setPrograms(Collections.singletonList(p));
            overrideHist.setAccountNumber(template.evaluateAsString("y:accountNumber"));
			overrideHist.setStatus(OverrideStatus.valueOf(template.evaluateAsString("y:status")));
            overrideHist.setScheduledDate(template.evaluateAsDate("y:scheduledDateTime"));
            overrideHist.setStartDate(template.evaluateAsDate("y:startDateTime"));
            overrideHist.setStopDate(template.evaluateAsDate("y:stopDateTime"));
            overrideHist.setUserName(template.evaluateAsString("y:userName"));
            overrideHist.setOverrideNumber(template.evaluateAsLong("y:overrideNumber"));
            overrideHist.setCountedAgainstLimit(template.evaluateAsBoolean("y:countedAgainstLimit"));

            return overrideHist;
        }
    }
    
    private static class OverrideHistoryNodeMapper_v1_1 implements
	    ObjectMapper<Node, OverrideHistory> {
	
		@Override
		public OverrideHistory map(Node from) throws ObjectMappingException {
		    // create template and parse data
		    SimpleXPathTemplate template = YukonXml.getXPathTemplateForNode(from);
		
		    OverrideHistory overrideHist = new OverrideHistory();
		    overrideHist.setSerialNumber(template.evaluateAsString("y:serialNumber"));
		    
		    List<Program> programList = new ArrayList<Program>();
		    List<Node> programNameNodeList = template.evaluateAsNodeList("y:enrolledProgramList");
		    for (Node n : programNameNodeList) {
		    	SimpleXPathTemplate nt = YukonXml.getXPathTemplateForNode(n);
		    	String programName = nt.evaluateAsString("y:programName");
		    	Program p = new Program();
			    p.setProgramName(programName);
			    programList.add(p);
		    }
		    
		    overrideHist.setPrograms(programList);
		    
		    overrideHist.setAccountNumber(template.evaluateAsString("y:accountNumber"));
			overrideHist.setStatus(OverrideStatus.valueOf(template.evaluateAsString("y:status")));
		    overrideHist.setScheduledDate(template.evaluateAsDate("y:scheduledDateTime"));
		    overrideHist.setStartDate(template.evaluateAsDate("y:startDateTime"));
		    overrideHist.setStopDate(template.evaluateAsDate("y:stopDateTime"));
		    overrideHist.setUserName(template.evaluateAsString("y:userName"));
		    overrideHist.setOverrideNumber(template.evaluateAsLong("y:overrideNumber"));
		    overrideHist.setCountedAgainstLimit(template.evaluateAsBoolean("y:countedAgainstLimit"));
		
		    return overrideHist;
		}
	}
}

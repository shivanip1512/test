package com.cannontech.yukon.api.loadManagement;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Node;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.model.OverrideStatus;
import com.cannontech.yukon.api.loadManagement.endpoint.OverrideHistoryRequestEndpoint;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class OverrideHistoryRequestEndpointTest {

    private OverrideHistoryRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    
    //test request xml data
    private static final String ACCOUNT_VALID = "ACCOUNT_VALID";
    private static final String START_DATE_VALID = "2008-09-30T12:00:00Z";
    private static final String STOP_DATE_VALID = "2008-09-30T23:59:59Z";
    private static final String SCHEDULED_DATE_VALID = "2008-09-29T12:00:00Z";
    private static final String PROGRAM_VALID = "PROGRAM_VALID";    

    private static final String ACCOUNT_INVALID = "ACCOUNT_INVALID";
    private static final String PROGRAM_INVALID = "PROGRAM_INVALID";    

    //test response xml data
    static final String byAccountResponseStr = "/y:overrideHistoryByAccountNumberResponse";   
    static final String byAccountHistoryElementStr = byAccountResponseStr + "/y:overrideHistoryEntries/y:overrideHistory";
    
    static final String byProgramResponseStr = "/y:overrideHistoryByProgramNameResponse";   
    static final String byProgramHistoryElementStr = byProgramResponseStr + "/y:overrideHistoryEntries/y:overrideHistory";    
    
    static final String serialNumberRespStr = "y:serialNumber";
    static final String programNameRespStr = "y:programName";
    static final String accountNumberRespStr = "y:accountNumber";
    static final String statusRespStr = "y:status";
    static final String scheduledDateTimeRespStr = "y:scheduledDateTime";
    static final String startDateTimeRespStr = "y:startDateTime";   
    static final String stopDateTimeRespStr = "y:stopDateTime";
    static final String userNameRespStr = "y:userName";
    static final String overrideNumberRespStr = "y:overrideNumber";
    static final String countedAgainstLimitRespStr = "y:countedAgainstLimit";    
    
    private static OverrideHistoryNodeMapper overrideHistNodeMapper = new OverrideHistoryNodeMapper();
    
    private static final String PROGRAM1_VALID = "PROGRAM1_VALID";
    private static final String SERIAL_NUMBER1_VALID = "SERIAL_NUMBER1_VALID";
    private static final OverrideStatus STATUS1_ACTIVE = OverrideStatus.Active;
    private static final String USER1_VALID = "USER1_VALID";
    private static final long OVERRIDE_NUMBER1_VALID = 10;    

    private static final String PROGRAM2_VALID = "PROGRAM2_VALID";
    private static final String SERIAL_NUMBER2_VALID = "SERIAL_NUMBER2_VALID";
    private static final OverrideStatus STATUS2_SCHEDULED = OverrideStatus.Scheduled;
    private static final String USER2_VALID = "USER2_VALID";
    private static final long OVERRIDE_NUMBER2_VALID = 20;    
    
    @Before
    public void setUp() throws Exception {
        
        mockOptOutService = new MockOptOutService();
        
        impl = new OverrideHistoryRequestEndpoint();
        impl.setOptOutService(mockOptOutService);
        impl.setAuthDao(new MockAuthDao());
    }
    
    private class MockOptOutService extends OptOutServiceAdapter {
        
        private String accountNumber;
        private Date startTime;
        private Date stopTime;
        private String programName;
        
        @Override
        public List<OverrideHistory> getOptOutHistoryForAccount(
        		String accountNumber, Date startTime, Date stopTime,
        		LiteYukonUser user) {

        	this.accountNumber = accountNumber;
            this.startTime = startTime;
            this.stopTime = stopTime;
            
            if(accountNumber == ACCOUNT_INVALID) {
            	throw new NotFoundException("Account invalid");
            }
            
            List<OverrideHistory> overrideHistoryList = createOverrideHistoryResults();
            return overrideHistoryList;
        }
        
        @Override
        public List<OverrideHistory> getOptOutHistoryByProgram(
        		String programName, Date startTime, Date stopTime,
        		LiteYukonUser user) {

        	 this.programName = programName;            
             this.startTime = startTime;
             this.stopTime = stopTime;
             
             if(programName == PROGRAM_INVALID) {
             	throw new NotFoundException("Account invalid");
             }

             List<OverrideHistory> overrideHistoryList = createOverrideHistoryResults();
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
        
        private List<OverrideHistory> createOverrideHistoryResults() {
            List<OverrideHistory> overrideHistoryList = new ArrayList<OverrideHistory>();
            
            OverrideHistory hist1 = new OverrideHistory();
            hist1.setSerialNumber(SERIAL_NUMBER1_VALID);
            hist1.setProgramName(PROGRAM1_VALID);
            hist1.setAccountNumber(ACCOUNT_VALID);
            hist1.setStatus(OverrideStatus.Active);
            hist1.setScheduledDate(XmlUtils.parseDate(SCHEDULED_DATE_VALID));
            hist1.setStartDate(XmlUtils.parseDate(START_DATE_VALID));
            hist1.setStopDate(XmlUtils.parseDate(STOP_DATE_VALID));
            hist1.setUserName(USER1_VALID);
            hist1.setOverrideNumber(OVERRIDE_NUMBER1_VALID);
            hist1.setCountedAgainstLimit(true);
            overrideHistoryList.add(hist1);            
            
            OverrideHistory hist2 = new OverrideHistory();            
            hist2.setSerialNumber(SERIAL_NUMBER2_VALID);
            hist2.setProgramName(PROGRAM2_VALID);
            hist2.setAccountNumber(ACCOUNT_VALID);
            hist2.setStatus(OverrideStatus.Scheduled);
            hist2.setScheduledDate(XmlUtils.parseDate(SCHEDULED_DATE_VALID));
            hist2.setStartDate(XmlUtils.parseDate(START_DATE_VALID));
            hist2.setStopDate(XmlUtils.parseDate(STOP_DATE_VALID));
            hist2.setUserName(USER2_VALID);
            hist2.setOverrideNumber(OVERRIDE_NUMBER2_VALID);
            hist2.setCountedAgainstLimit(false);
            overrideHistoryList.add(hist2);
            
            return overrideHistoryList;            
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
    public void testInvokeOverrideByAccountSuccess() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("OverrideHistoryByAccountNumberRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideHistoryByAccountNumberRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideHistoryByAccountNumberResponse.xsd",
        		this.getClass());

        //invoke test with unauthorized user
        LiteYukonUser user = new LiteYukonUser();
        user.setUserID(-1);
        Element respElement = impl.invokeOverrideByAccount(reqElement, user);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, "overrideHistoryByAccountNumberResponse", "UserNotAuthorized");
        
        //invoke test
        user.setUserID(1);
        respElement = impl.invokeOverrideByAccount(reqElement, user);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT_VALID, mockOptOutService.getAccountNumber());
        Assert.assertNull(mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, XmlUtils.formatDate(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, XmlUtils.formatDate(mockOptOutService.getStopTime()));
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        outputTemplate = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(outputTemplate, byAccountResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
       
        List<OverrideHistory> overrideHistResults = outputTemplate.evaluate(byAccountHistoryElementStr,
                                                                      		overrideHistNodeMapper);

        // verify data in the response
        assertTrue("Incorrect resultSize", overrideHistResults != null && overrideHistResults.size() == 2);
        // results verified by positional check, which validates test node-mapper as well
        OverrideHistory overrideHist = overrideHistResults.get(0);
        assertTrue("Incorrect serialNumber",
                   overrideHist.getSerialNumber().equals(SERIAL_NUMBER1_VALID));
        assertTrue("Incorrect programName", 
                   overrideHist.getProgramName().equals(PROGRAM1_VALID));
        assertTrue("Incorrect accountNumber",
                   overrideHist.getAccountNumber().equals(ACCOUNT_VALID));
        assertTrue("Incorrect status", 
                   overrideHist.getStatus().equals(STATUS1_ACTIVE));
        assertTrue("Incorrect scheduledDate",
                   XmlUtils.formatDate(overrideHist.getScheduledDate()).equals(SCHEDULED_DATE_VALID));
        assertTrue("Incorrect startDate",
                   XmlUtils.formatDate(overrideHist.getStartDate()).equals(START_DATE_VALID));
        assertTrue("Incorrect stopDate",
                   XmlUtils.formatDate(overrideHist.getStopDate()).equals(STOP_DATE_VALID));
        assertTrue("Incorrect userName", overrideHist.getUserName().equals(USER1_VALID));
        assertTrue("Incorrect overrideNumber",
                   overrideHist.getOverrideNumber() == OVERRIDE_NUMBER1_VALID);
        assertTrue("Incorrect countedAgainstLimit",
                   overrideHist.isCountedAgainstLimit());
        
        overrideHist = overrideHistResults.get(1);
        assertTrue("Incorrect serialNumber",
                   overrideHist.getSerialNumber().equals(SERIAL_NUMBER2_VALID));
        assertTrue("Incorrect programName",
                   overrideHist.getProgramName().equals(PROGRAM2_VALID));
        assertTrue("Incorrect accountNumber",
                   overrideHist.getAccountNumber().equals(ACCOUNT_VALID));
        assertTrue("Incorrect status",
                   overrideHist.getStatus().equals(STATUS2_SCHEDULED));
        assertTrue("Incorrect scheduledDate",
                   XmlUtils.formatDate(overrideHist.getScheduledDate()).equals(SCHEDULED_DATE_VALID));
        assertTrue("Incorrect startDate",
                   XmlUtils.formatDate(overrideHist.getStartDate()).equals(START_DATE_VALID));
        assertTrue("Incorrect stopDate",
                   XmlUtils.formatDate(overrideHist.getStopDate()).equals(STOP_DATE_VALID));
        assertTrue("Incorrect userName",
                   overrideHist.getUserName().equals(USER2_VALID));
        assertTrue("Incorrect overrideNumber",
                   overrideHist.getOverrideNumber() == OVERRIDE_NUMBER2_VALID);
        assertTrue("Incorrect countedAgainstLimit",
                   !overrideHist.isCountedAgainstLimit());  
        
        
//        //invoke test with invalid account
//        respElement = impl.invokeOverrideByAccount(reqElement, user);
//
//        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
//        
//        outputTemplate = XmlUtils.getXPathTemplateForElement(respElement);
//        TestUtils.runFailureAssertions(outputTemplate, "overrideHistoryByAccountNumberResponse", "InvalidAccountNumber");
    
    }
    
    @Test
    public void testInvokeOverrideByProgramSuccess() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("OverrideHistoryByProgramNameRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideHistoryByProgramNameRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/OverrideHistoryByProgramNameResponse.xsd",
        		this.getClass());

        //invoke test with unauthorized user
        LiteYukonUser user = new LiteYukonUser();
        user.setUserID(-1);
        Element respElement = impl.invokeOverrideByProgram(reqElement, user);

        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
        
        SimpleXPathTemplate outputTemplate = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runFailureAssertions(outputTemplate, "overrideHistoryByProgramNameResponse", "UserNotAuthorized");
        
        
        //invoke test
        user.setUserID(1);
        respElement = impl.invokeOverrideByProgram(reqElement, user);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect programName.", PROGRAM_VALID, mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, XmlUtils.formatDate(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, XmlUtils.formatDate(mockOptOutService.getStopTime()));
        
        // verify the respElement is valid according to schema
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byProgramResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
       
        List<OverrideHistory> overrideHistResults = template.evaluate(byProgramHistoryElementStr,
                                                                      overrideHistNodeMapper);

        // verify data in the response
        assertTrue("Incorrect resultSize", overrideHistResults != null && overrideHistResults.size() == 2);
        // results verified by positional check, which validates test node-mapper as well
        OverrideHistory overrideHist = overrideHistResults.get(0);
        assertTrue("Incorrect serialNumber",
                   overrideHist.getSerialNumber().equals(SERIAL_NUMBER1_VALID));
        assertTrue("Incorrect programName",
                   overrideHist.getProgramName().equals(PROGRAM1_VALID));
        assertTrue("Incorrect accountNumber",
                   overrideHist.getAccountNumber().equals(ACCOUNT_VALID));
        assertTrue("Incorrect status",
                   overrideHist.getStatus().equals(STATUS1_ACTIVE));
        assertTrue("Incorrect scheduledDate",
                   XmlUtils.formatDate(overrideHist.getScheduledDate()).equals(SCHEDULED_DATE_VALID));
        assertTrue("Incorrect startDate",
                   XmlUtils.formatDate(overrideHist.getStartDate()).equals(START_DATE_VALID));
        assertTrue("Incorrect stopDate",
                   XmlUtils.formatDate(overrideHist.getStopDate()).equals(STOP_DATE_VALID));
        assertTrue("Incorrect userName",
                   overrideHist.getUserName().equals(USER1_VALID));
        assertTrue("Incorrect overrideNumber",
                   overrideHist.getOverrideNumber() == OVERRIDE_NUMBER1_VALID);
        assertTrue("Incorrect countedAgainstLimit",
                   overrideHist.isCountedAgainstLimit());
        
        overrideHist = overrideHistResults.get(1);
        assertTrue("Incorrect serialNumber",
                   overrideHist.getSerialNumber().equals(SERIAL_NUMBER2_VALID));
        assertTrue("Incorrect programName",
                   overrideHist.getProgramName().equals(PROGRAM2_VALID));
        assertTrue("Incorrect accountNumber",
                   overrideHist.getAccountNumber().equals(ACCOUNT_VALID));
        assertTrue("Incorrect status",
                   overrideHist.getStatus().equals(STATUS2_SCHEDULED));
        assertTrue("Incorrect scheduledDate",
                   XmlUtils.formatDate(overrideHist.getScheduledDate()).equals(SCHEDULED_DATE_VALID));
        assertTrue("Incorrect startDate",
                   XmlUtils.formatDate(overrideHist.getStartDate()).equals(START_DATE_VALID));
        assertTrue("Incorrect stopDate",
                   XmlUtils.formatDate(overrideHist.getStopDate()).equals(STOP_DATE_VALID));
        assertTrue("Incorrect userName",
                   overrideHist.getUserName().equals(USER2_VALID));
        assertTrue("Incorrect overrideNumber",
                   overrideHist.getOverrideNumber() == OVERRIDE_NUMBER2_VALID);
        assertTrue("Incorrect countedAgainstLimit",
                   !overrideHist.isCountedAgainstLimit());
        
//        //invoke test with invalid account
//        respElement = impl.invokeOverrideByProgram(reqElement, user);
//
//        TestUtils.validateAgainstSchema(respElement, respSchemaResource);
//        
//        outputTemplate = XmlUtils.getXPathTemplateForElement(respElement);
//        TestUtils.runFailureAssertions(outputTemplate, "overrideHistoryByProgramNameResponse", "InvalidProgramName");
    
        
    }
    
    private static class OverrideHistoryNodeMapper implements
            ObjectMapper<Node, OverrideHistory> {

        @Override
        public OverrideHistory map(Node from) throws ObjectMappingException {
            // create template and parse data
            SimpleXPathTemplate template = XmlUtils.getXPathTemplateForNode(from);

            OverrideHistory overrideHist = new OverrideHistory();
            overrideHist.setSerialNumber(template.evaluateAsString(serialNumberRespStr));
            overrideHist.setProgramName(template.evaluateAsString(programNameRespStr));
            overrideHist.setAccountNumber(template.evaluateAsString(accountNumberRespStr));
			overrideHist.setStatus(OverrideStatus.valueOf(template.evaluateAsString(statusRespStr)));
            overrideHist.setScheduledDate(template.evaluateAsDate(scheduledDateTimeRespStr));
            overrideHist.setStartDate(template.evaluateAsDate(startDateTimeRespStr));
            overrideHist.setStopDate(template.evaluateAsDate(stopDateTimeRespStr));
            overrideHist.setUserName(template.evaluateAsString(userNameRespStr));
            overrideHist.setOverrideNumber(template.evaluateAsLong(overrideNumberRespStr));
            overrideHist.setCountedAgainstLimit(template.evaluateAsBoolean(countedAgainstLimitRespStr));

            return overrideHist;
        }
    }
}

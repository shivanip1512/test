package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.yukon.api.loadManagement.endpoint.TotalOverriddenDevicesRequestEndpoint;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class TotalOverriddenDevicesRequestEndpointTest {

    private TotalOverriddenDevicesRequestEndpoint impl;
    private MockOptOutService mockOptOutService;
    
    //test request xml data
    private static final String ACCOUNT_VALID = "ACCOUNT_VALID";
    private static final String START_DATE_VALID = "2008-09-30T12:00:00Z";
    private static final String STOP_DATE_VALID = "2008-09-30T23:59:59Z";
    private static final String PROGRAM_VALID = "PROGRAM_VALID";    

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
        impl.initialize();
    }
    
    private class MockOptOutService extends OptOutServiceAdapter {
        
        private String accountNumber;
        private Date startTime;
        private Date stopTime;
        private String programName;
        
        @Override
        public int getOptOutDeviceCountForAccount(String accountNumber,
        		Date startTime, Date stopTime, LiteYukonUser user) {
        	
        	this.accountNumber = accountNumber;
        	this.startTime = startTime;
        	this.stopTime = stopTime;
        	
        	return user.getUserID();
        }
        
        @Override
        public int getOptOutDeviceCountForProgram(String programName,
        		Date startTime, Date stopTime, LiteYukonUser user) {

        	this.programName = programName;            
        	this.startTime = startTime;
        	this.stopTime = stopTime;
        	
        	return user.getUserID();
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
   
    @Test
    public void testInvokeDevicesByAccountSuccess() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("TotalOverriddenDevicesByAccountNumberRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByAccountNumberRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        
        LiteYukonUser user = new LiteYukonUser();
        user.setUserID(10);
        //invoke test
        Element respElement = impl.invokeDevicesByAccount(reqElement, user);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT_VALID, mockOptOutService.getAccountNumber());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, XmlUtils.formatDate(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, XmlUtils.formatDate(mockOptOutService.getStopTime()));
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByAccountNumberResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byAccountResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
       
        long totalDevicesResult = template.evaluateAsLong(byAccountTotalDevicesStr);

        // verify data in the response
        Assert.assertEquals("Incorrect totalDevices", user.getUserID(), totalDevicesResult);
    }
    
    @Test
    public void testInvokeDevicesByProgramSuccess() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("TotalOverriddenDevicesByProgramNameRequest.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByProgramNameRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test
        LiteYukonUser user = new LiteYukonUser();
        user.setUserID(22);
        Element respElement = impl.invokeDevicesByProgram(reqElement, user);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect programName.", PROGRAM_VALID, mockOptOutService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, XmlUtils.formatDate(mockOptOutService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, XmlUtils.formatDate(mockOptOutService.getStopTime()));
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByProgramNameResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byProgramResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
       
        long totalDevicesResult = template.evaluateAsLong(byProgramTotalDevicesStr);

        // verify data in the response
        Assert.assertEquals("Incorrect totalDevices", totalDevicesResult, user.getUserID());
    }
    
}


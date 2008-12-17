package com.cannontech.yukon.api.loadManagement;

import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.util.StarsInvalidArgumentException;
import com.cannontech.yukon.api.loadManagement.endpoint.TotalOverriddenDevicesRequestEndpoint;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class TotalOverriddenDevicesRequestEndpointTest {

    private TotalOverriddenDevicesRequestEndpoint impl;
    private MockOverrideService mockService;
    
    //test request xml data
    private static final String ACCOUNT_VALID = "ACCOUNT_VALID";
    private static final String START_DATE_VALID = "2008-09-30T12:00:00Z";
    private static final String STOP_DATE_VALID = "2008-09-30T23:59:59Z";
    private static final String PROGRAM_VALID = "PROGRAM_VALID";    

    private static final String ACCOUNT_INVALID = "ACCOUNT_INVALID";
    private static final String START_DATE_INVALID = "2008-10-30T12:00:00Z";
    private static final String STOP_DATE_INVALID = "2008-10-30T23:59:59Z";
    private static final String PROGRAM_INVALID = "PROGRAM_INVALID";
    
    //test response xml data
    static final String byAccountResponseStr = "/y:totalOverriddenDevicesByAccountNumberResponse";   
    static final String byAccountTotalDevicesStr = byAccountResponseStr + "/y:totalDevices";
    
    static final String byProgramResponseStr = "/y:totalOverriddenDevicesByProgramNameResponse";   
    static final String byProgramTotalDevicesStr = byProgramResponseStr + "/y:totalDevices";    
    
    static final long byAccountTotalDevices = 10;
    static final long byProgramTotalDevices = 20;
    
    //TODO Add/match with actual ErrorCodes thrown here
    private static final String ERROR_CODE = "ERROR_CODE";

    @Before
    public void setUp() throws Exception {
        
        mockService = new MockOverrideService();
        
        impl = new TotalOverriddenDevicesRequestEndpoint();
        impl.setOverrideService(mockService);
        impl.initialize();
    }
    
    private class MockOverrideService extends OverrideServiceAdapter {
        
        private String accountNumber;
        private Date startTime;
        private Date stopTime;
        private String programName;
        
        @Override
        public long totalOverridenDevicesByAccountNumber(String accountNumber, String programName, Date startTime, Date stopTime, LiteYukonUser user) {
            
            this.accountNumber = accountNumber;
            this.startTime = startTime;
            this.stopTime = stopTime;
            this.programName = programName;
            
            //TODO Match up here with expected Exception that may be thrown
            if (accountNumber.equals(ACCOUNT_INVALID)){
                throw new StarsInvalidArgumentException("Invalid Arguments");
            }
            
            return byAccountTotalDevices;
        }
        
        @Override
        public long totalOverridenDevicesByProgramName(String programName, Date startTime, Date stopTime, LiteYukonUser user) {

            this.programName = programName;            
            this.startTime = startTime;
            this.stopTime = stopTime;

            //TODO Match up here with expected Exception that may be thrown
            if (programName.equals(PROGRAM_INVALID)){
                throw new StarsInvalidArgumentException("Invalid Arguments");
            }
            
            return byProgramTotalDevices;
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
        
        //invoke test
        Element respElement = impl.invokeDevicesByAccount(reqElement, null);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT_VALID, mockService.getAccountNumber());
        Assert.assertEquals("Incorrect programName.", PROGRAM_VALID, mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, XmlUtils.formatDate(mockService.getStopTime()));
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByAccountNumberResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byAccountResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
       
        long totalDevicesResult = template.evaluateAsLong(byAccountTotalDevicesStr);

        // verify data in the response
        assertTrue("Incorrect totalDevices", totalDevicesResult == byAccountTotalDevices);
    }
    
    @Test
    public void testInvokeDevicesByAccountFailure() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("TotalOverriddenDevicesByAccountNumberRequest_Invalid.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByAccountNumberRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test
        Element respElement = impl.invokeDevicesByAccount(reqElement, null);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect accountNumber.", ACCOUNT_INVALID, mockService.getAccountNumber());
        Assert.assertEquals("Incorrect programName.", PROGRAM_INVALID, mockService.getProgramName());        
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_INVALID, XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_INVALID, XmlUtils.formatDate(mockService.getStopTime()));
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByAccountNumberResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byAccountResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        //TODO Add/match with actual ErrorCodes thrown here        
        TestUtils.runFailureAssertions(template, byAccountResponseStr, ERROR_CODE);
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
        Element respElement = impl.invokeDevicesByProgram(reqElement, null);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect programName.", PROGRAM_VALID, mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_VALID, XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_VALID, XmlUtils.formatDate(mockService.getStopTime()));
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByProgramNameResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byProgramResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
       
        long totalDevicesResult = template.evaluateAsLong(byProgramTotalDevicesStr);

        // verify data in the response
        assertTrue("Incorrect totalDevices", totalDevicesResult == byProgramTotalDevices);
    }
    
    @Test
    public void testInvokeDevicesByProgramFailure() throws Exception {
        
        // Init with Request XML
        Resource resource = new ClassPathResource("TotalOverriddenDevicesByProgramNameRequest_Invalid.xml", this.getClass());
        Element reqElement = XmlUtils.createElementFromResource(resource);
        
        // verify the reqElement is valid according to schema
        Resource reqSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByProgramNameRequest.xsd",
                                                           this.getClass());
        TestUtils.validateAgainstSchema(reqElement, reqSchemaResource);
        
        //invoke test
        Element respElement = impl.invokeDevicesByProgram(reqElement, null);
        
        //verify mockService was called with correct params
        Assert.assertEquals("Incorrect programName.", PROGRAM_INVALID, mockService.getProgramName());        
        Assert.assertEquals("Incorrect startDateTime.", START_DATE_INVALID, XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", STOP_DATE_INVALID, XmlUtils.formatDate(mockService.getStopTime()));
        
        // verify the respElement is valid according to schema
        Resource respSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/TotalOverriddenDevicesByProgramNameResponse.xsd",
                                                            this.getClass());
        TestUtils.validateAgainstSchema(respElement, respSchemaResource);

        // create template and parse response data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(respElement);
        TestUtils.runVersionAssertion(template, byProgramResponseStr, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        //TODO Add/match with actual ErrorCodes thrown here        
        TestUtils.runFailureAssertions(template, byProgramResponseStr, ERROR_CODE);
    }
}


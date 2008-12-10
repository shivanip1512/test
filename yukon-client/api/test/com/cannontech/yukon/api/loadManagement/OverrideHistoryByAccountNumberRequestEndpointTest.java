package com.cannontech.yukon.api.loadManagement;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.jdom.Element;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.OverrideHistory;
import com.cannontech.loadcontrol.service.data.OverrideStatus;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class OverrideHistoryByAccountNumberRequestEndpointTest {

    private OverrideHistoryByAccountNumberRequestEndpoint impl;
    private MockOverrideService mockService;
    
    private static Date scheduledDate1 = DateUtils.addDays(new Date(), -1);
    private static Date startDateTime1 = DateUtils.addDays(new Date(), -2);
    private static Date stopDateTime1 = DateUtils.addDays(new Date(), -3);
    private static Date scheduledDate2 = DateUtils.addDays(new Date(), -4);
    private static Date startDateTime2 = DateUtils.addDays(new Date(), -5);
    private static Date stopDateTime2 = DateUtils.addDays(new Date(), -6);
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockOverrideService();
        
        impl = new OverrideHistoryByAccountNumberRequestEndpoint();
        impl.setOverrideService(mockService);
        impl.initialize();
    }
    
    private class MockOverrideService extends OverrideServiceAdapter {
        
        private String accountNumber;
        private Date startTime;
        private Date stopTime;
        private String programName;
        
        @Override
        public List<OverrideHistory> overrideHistoryByAccountNumber(String accountNumber, String programName, Date startTime, Date stopTime, LiteYukonUser user) {
        	
        	this.accountNumber = accountNumber;
            this.startTime = startTime;
            this.stopTime = stopTime;
            this.programName = programName;
            
            List<OverrideHistory> overrideHistoryList = new ArrayList<OverrideHistory>();
            
            OverrideHistory hist1 = new OverrideHistory();
            hist1.setSerialNumber("100");
            hist1.setProgramName("program1");
            hist1.setAccountNumber(accountNumber);
            hist1.setStatus(OverrideStatus.ACTIVE);
            hist1.setScheduledDate(scheduledDate1);
            hist1.setStartDate(startDateTime1);
            hist1.setStopDate(stopDateTime1);
            hist1.setUserName("user1");
            hist1.setOverrideNumber(10);
            hist1.setCountedAgainstLimit(true);
            
            OverrideHistory hist2 = new OverrideHistory();
            hist2.setSerialNumber("200");
            hist2.setProgramName("program2");
            hist2.setAccountNumber(accountNumber);
            hist2.setStatus(OverrideStatus.SCHEDULED);
            hist2.setScheduledDate(scheduledDate2);
            hist2.setStartDate(startDateTime2);
            hist2.setStopDate(stopDateTime2);
            hist2.setUserName("user2");
            hist2.setOverrideNumber(20);
            hist2.setCountedAgainstLimit(false);
            
            overrideHistoryList.add(hist1);
            overrideHistoryList.add(hist2);
            
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
   
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        Resource requestSchemaResource = new ClassPathResource("../schemas/loadManagement/OverrideHistoryByAccountNumberRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("../schemas/loadManagement/OverrideHistoryByAccountNumberResponse.xsd", this.getClass());
        
        // account, no program name
        //==========================================================================================
		requestElement = LoadManagementTestUtils.createOverrideHistoryByAccountNumberRequestElement(
																					"overrideHistoryByAccountNumberRequest", 
																					"Account1",
																					null, 
																					"2008-10-13T12:30:00Z",
																					"2008-10-17T20:00:00Z", 
																					"1.0",
																					requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertEquals("Incorrect accountNumber.", "Account1", mockService.getAccountNumber());
        Assert.assertNull(mockService.getProgramName());
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", XmlUtils.formatDate(mockService.getStartTime()));
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-17T20:00:00Z", XmlUtils.formatDate(mockService.getStopTime()));
        
        Assert.assertEquals("Incorrect serialNumber.", null, outputTemplate.evaluateAsLong("/y:overrideHistoryByAccountNumberResponse/y:overrideHistoryEntries/y:overrideHistory[0]/y:SHIT"));
        
        
        
       
        
//        // not found
//        //==========================================================================================
//        requestElement = LoadManagementTestUtils.createOverrideHistoryByAccountNumberRequestElement("overrideHistoryByAccountNumberRequest", "programName", "NOT_FOUND", null, null, "1.0", requestSchemaResource);
//        
//        responseElement = impl.invoke(requestElement, null);
//        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
//        
//        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
//        
//        TestUtils.runFailureAssertions(outputTemplate, "programStartResponse", "InvalidProgramName");
        
       
    }
}

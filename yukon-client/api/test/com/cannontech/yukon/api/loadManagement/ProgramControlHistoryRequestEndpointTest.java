package com.cannontech.yukon.api.loadManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jdom2.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.Iso8601DateUtil;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ProgramControlHistoryRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProgramControlHistoryRequestEndpointTest {

	private ProgramControlHistoryRequestEndpoint impl;
    private MockLoadControlService mockService;
    private static final String EMTPY_RETURN = "EMPTY";
    
    @BeforeEach
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ProgramControlHistoryRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.setRolePropertyDao(new MockRolePropertyDao());
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
        private String programName;
        private Date startTime;
        private Date stopTime;
        
        @Override
        public List<ProgramControlHistory> getControlHistoryByProgramName(String programName, Date fromTime, Date throughTime, LiteYukonUser user) throws NotFoundException, NotAuthorizedException {

        	this.programName = programName;
            this.startTime = fromTime;
            this.stopTime = throughTime;
            
            if (programName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            } else if (programName.equals("NOT_AUTH")) {
                throw new NotAuthorizedException("");
            }
            
            List<ProgramControlHistory> histList = new ArrayList<ProgramControlHistory>();
            if (!programName.equals(EMTPY_RETURN)){            
                ProgramControlHistory h1 = new ProgramControlHistory(1, 1);
                h1.setProgramName("Program1");
                h1.setStartDateTime(Iso8601DateUtil.parseIso8601Date("2008-10-13T12:30:00Z"));
                h1.setStopDateTime(null);
                h1.setGearName("Gear1");
                histList.add(h1);

                ProgramControlHistory h2 = new ProgramControlHistory(2, 2);
                h2.setProgramName("Program2");
                h2.setStartDateTime(Iso8601DateUtil.parseIso8601Date("2008-10-13T12:30:00Z"));
                h2.setStopDateTime(Iso8601DateUtil.parseIso8601Date("2008-10-13T21:49:01Z"));
                h2.setGearName("Gear2");
                histList.add(h2);
            }
            
            return histList;
        }
        
        public String getProgramName() {
            return programName;
        }
        public Date getStartTime() {
            return startTime;
        }
        public Date getStopTime() {
            return stopTime;
        }
    }
    
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        Resource requestSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProgramControlHistoryRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ProgramControlHistoryResponse.xsd", this.getClass());
        
        // start time, no stop time; to return empty list
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProgramControlHistoryRequestElement(EMTPY_RETURN, "2008-10-13T12:30:00Z", null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertEquals(0, outputTemplate.evaluateAsNodeList("/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory").size(), "Incorrect number of controlHistoryEntries nodes.");
        
        assertEquals("2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(mockService.getStartTime()), "Incorrect startDateTime.");
        assertEquals(null, mockService.getStopTime(), "Incorrect stopDateTime.");
        
        // start time, no stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProgramControlHistoryRequestElement("Program1", "2008-10-13T12:30:00Z", null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);

        assertEquals(2, outputTemplate
                .evaluateAsNodeList("/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory").size(),
                "Incorrect number of controlHistoryEntries nodes.");

        assertEquals("Program1", outputTemplate.evaluateAsString(
                "/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory[1]/y:programName"),
                "Incorrect programName.");
        assertEquals("Gear1", outputTemplate.evaluateAsString(
                "/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory[1]/y:gearName"),
                "Incorrect startGearName.");
        assertEquals("2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(outputTemplate.evaluateAsDate(
                "/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory[1]/y:startDateTime")),
                "Incorrect startDateTime.");
        assertEquals(null, outputTemplate.evaluateAsDate(
                "/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory[1]/y:stopDateTime"),
                "Incorrect stopDateTime.");

        assertEquals("2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(mockService.getStartTime()),
                "Incorrect startDateTime.");
        assertEquals(null, mockService.getStopTime(), "Incorrect stopDateTime.");
        
        // start time, stop time
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProgramControlHistoryRequestElement("Program2", "2008-10-13T12:30:00Z", "2008-10-13T21:49:01Z", "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertEquals(2, outputTemplate
                .evaluateAsNodeList("/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory").size(),
                "Incorrect number of controlHistoryEntries nodes.");

        assertEquals("Program2", outputTemplate.evaluateAsString(
                "/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory[2]/y:programName"),
                "Incorrect programName.");
        assertEquals("Gear2", outputTemplate.evaluateAsString(
                "/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory[2]/y:gearName"),
                "Incorrect startGearName.");
        assertEquals("2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(outputTemplate.evaluateAsDate(
                "/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory[2]/y:startDateTime")),
                "Incorrect startDateTime.");
        assertEquals("2008-10-13T21:49:01Z",
                Iso8601DateUtil.formatIso8601Date(outputTemplate.evaluateAsDate(
                        "/y:programControlHistoryResponse/y:controlHistoryEntries/y:programControlHistory[2]/y:stopDateTime")),
                "Incorrect stopDateTime.");

        assertEquals("2008-10-13T12:30:00Z", Iso8601DateUtil.formatIso8601Date(mockService.getStartTime()),
                "Incorrect startDateTime.");
        assertEquals("2008-10-13T21:49:01Z", Iso8601DateUtil.formatIso8601Date(mockService.getStopTime()),
                "Incorrect stopDateTime.");
        
        // not found
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProgramControlHistoryRequestElement("NOT_FOUND", "2008-10-13T12:30:00Z", null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programControlHistoryResponse", "InvalidProgramName");
        
        // not auth
        //==========================================================================================
        requestElement = LoadManagementTestUtils.createProgramControlHistoryRequestElement("NOT_AUTH", "2008-10-13T12:30:00Z", null, "1.0", requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programControlHistoryResponse", "UserNotAuthorized");
    }
}

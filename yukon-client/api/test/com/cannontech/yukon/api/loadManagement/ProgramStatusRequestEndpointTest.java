package com.cannontech.yukon.api.loadManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ProgramStatusRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockProgramStatus;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProgramStatusRequestEndpointTest {

    private ProgramStatusRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @BeforeEach
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ProgramStatusRequestEndpoint();
        impl.setLoadControlService(mockService);
        ReflectionTestUtils.setField(impl, "rolePropertyDao", new MockRolePropertyDao());
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
        @Override
        public ProgramStatus getProgramStatusByProgramName(String programName, LiteYukonUser user) throws NotFoundException {
            
            if (programName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            } else if (programName.equals("NOT_AUTH")) {
                throw new NotAuthorizedException("");
            }
            
            if (programName.equals("Program1")) {
                return new MockProgramStatus("Program1", LMProgramBase.STATUS_ACTIVE, "2008-10-13T12:30:00Z", "2008-10-13T21:40:01Z", "Gear1");
            } else if (programName.equals("Program2")) {
                return new MockProgramStatus("Program2", LMProgramBase.STATUS_INACTIVE, "2008-10-14T13:45:01Z", null, "Gear2");
            }
            
            return null;
        }
    }
   
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Attribute versionAttribute = null;
        Element responseElement = null;
        Element tmpElement = null;
        SimpleXPathTemplate outputTemplate = null;
        
        // Program1
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("programName", ns, "Program1");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        // outputs
        assertNotNull(outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus"), "No programStatus node present.");
        assertEquals("Program1", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:programName"), "Incorrect programName");
        assertEquals("Active", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:currentStatus"), "Incorrect currentStatus");
        assertEquals("2008-10-13T12:30:00Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:startDateTime"), "Incorrect startDateTime");
        assertEquals("2008-10-13T21:40:01Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:stopDateTime"), "Incorrect stopDateTime");
        assertEquals("Gear1", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:gearName"), "Incorrect gearName");
        
        
        // Program2
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("programName", ns, "Program2");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        // outputs
        assertNotNull(outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus"), "No programStatus node present.");
        assertEquals("Program2", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:programName"), "Incorrect programName");
        assertEquals("Inactive", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:currentStatus"), "Incorrect currentStatus");
        assertEquals("2008-10-14T13:45:01Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:startDateTime"), "Incorrect startDateTime");
        assertNull(outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus/y:stopDateTime"), "Incorrect stopDateTime - should be null.");
        assertEquals("Gear2", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:gearName"), "Incorrect gearName");
        
        // not found
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("programName", ns, "NOT_FOUND");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        TestUtils.runFailureAssertions(outputTemplate, "programStatusResponse", "InvalidProgramName");
        
        // not auth
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("programName", ns, "NOT_AUTH");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement, null);
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStatusResponse", "UserNotAuthorized");
    }

}

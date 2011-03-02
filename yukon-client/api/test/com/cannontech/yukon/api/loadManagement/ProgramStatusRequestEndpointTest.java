package com.cannontech.yukon.api.loadManagement;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ProgramStatusRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockProgramStatus;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class ProgramStatusRequestEndpointTest {

    private ProgramStatusRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ProgramStatusRequestEndpoint();
        impl.setLoadControlService(mockService);
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
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNotNull("No programStatus node present.", outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus"));
        Assert.assertEquals("Incorrect programName", "Program1", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:programName"));
        Assert.assertEquals("Incorrect currentStatus", "Active", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:currentStatus"));
        Assert.assertEquals("Incorrect startDateTime", "2008-10-13T12:30:00Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:startDateTime"));
        Assert.assertEquals("Incorrect stopDateTime", "2008-10-13T21:40:01Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:stopDateTime"));
        Assert.assertEquals("Incorrect gearName", "Gear1", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:gearName"));
        
        
        // Program2
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("programName", ns, "Program2");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement, null);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNotNull("No programStatus node present.", outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus"));
        Assert.assertEquals("Incorrect programName", "Program2", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:programName"));
        Assert.assertEquals("Incorrect currentStatus", "Inactive", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:currentStatus"));
        Assert.assertEquals("Incorrect startDateTime", "2008-10-14T13:45:01Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:startDateTime"));
        Assert.assertNull("Incorrect stopDateTime - should be null.", outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus/y:stopDateTime"));
        Assert.assertEquals("Incorrect gearName", "Gear2", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:gearName"));
        
        // not found
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("programName", ns, "NOT_FOUND");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement, null);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        TestUtils.runFailureAssertions(outputTemplate, "programStatusResponse", "InvalidProgramName");
        
        // not auth
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        
        tmpElement = XmlUtils.createStringElement("programName", ns, "NOT_AUTH");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement, null);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "programStatusResponse", "UserNotAuthorized");
    }

}

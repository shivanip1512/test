package com.cannontech.yukon.api.loadManagement;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

public class ProgramStatusRequestEndpointTest {

    private ProgramStatusRequestEndpoint impl;
    private LoadControlServiceTest testService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
        testService = new LoadControlServiceTest();
        
        impl = new ProgramStatusRequestEndpoint();
        impl.setLoadControlService(testService);
        impl.initialize();
    }
    
    private class LoadControlServiceTest extends LoadControlServiceAdapter {
        
        @Override
        public ProgramStatus getProgramStatusByProgramName(String programName) throws NotFoundException {
            
            if (programName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
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
        Element responseElement = null;
        Element tmpElement = null;
        SimpleXPathTemplate outputTemplate = null;
        
        // Program1
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        tmpElement = XmlUtils.createStringElement("programName", ns, "Program1");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (Program1)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (Program1)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus"));
        Assert.assertEquals("Program1", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:programName"));
        Assert.assertEquals("Active", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:currentStatus"));
        Assert.assertEquals("2008-10-13T12:30:00Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:startDateTime"));
        Assert.assertEquals("2008-10-13T21:40:01Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:stopDateTime"));
        Assert.assertEquals("Gear1", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:gearName"));
        
        
        // Program2
        //==========================================================================================
        requestElement = new Element("programStatusRequest", ns);
        tmpElement = XmlUtils.createStringElement("programName", ns, "Program2");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (Program2)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (Program2)");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus"));
        Assert.assertEquals("Program2", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:programName"));
        Assert.assertEquals("Inactive", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:currentStatus"));
        Assert.assertEquals("2008-10-14T13:45:01Z", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:startDateTime"));
        Assert.assertNull(outputTemplate.evaluateAsNode("/y:programStatusResponse/y:programStatus/y:stopDateTime"));
        Assert.assertEquals("Gear2", outputTemplate.evaluateAsString("/y:programStatusResponse/y:programStatus/y:gearName"));
        
    }

}

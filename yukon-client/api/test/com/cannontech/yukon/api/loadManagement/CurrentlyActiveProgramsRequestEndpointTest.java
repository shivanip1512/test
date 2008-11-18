package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

public class CurrentlyActiveProgramsRequestEndpointTest {

    private CurrentlyActiveProgramsRequestEndpoint impl;
    private LoadControlServiceTest testService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
        testService = new LoadControlServiceTest();
        
        impl = new CurrentlyActiveProgramsRequestEndpoint();
        impl.setLoadControlService(testService);
        impl.initialize();
    }
    
    private class LoadControlServiceTest extends LoadControlServiceAdapter {
        
        @Override
        public List<ProgramStatus> getAllCurrentlyActivePrograms() {
            
            List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
            
            MockProgramStatus program1 = new MockProgramStatus("Program1", LMProgramBase.STATUS_ACTIVE, "2008-10-13T12:30:00Z", "2008-10-13T21:40:01Z", "Gear1");
            MockProgramStatus program2 = new MockProgramStatus("Program2", LMProgramBase.STATUS_INACTIVE, "2008-10-14T13:45:01Z", null, "Gear2");
            programStatuses.add(program1);
            programStatuses.add(program2);
            
            return programStatuses;
        }
    }
    
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        
        // 2 programs, one with no stop datetime
        //==========================================================================================
        requestElement = new Element("currentlyActiveProgramsRequest", ns);
        XmlUtils.printElement(requestElement, "REQUEST");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE");
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses"));
        Assert.assertEquals(1, outputTemplate.evaluateAsLong("count(/y:currentlyActiveProgramsResponse/y:programStatuses)"));
        Assert.assertEquals(2, outputTemplate.evaluateAsLong("count(/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus)"));
        
        Assert.assertEquals("Program1", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:programName"));
        Assert.assertEquals("Active", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:currentStatus"));
        Assert.assertEquals("2008-10-13T12:30:00Z", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:startDateTime"));
        Assert.assertEquals("2008-10-13T21:40:01Z", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:stopDateTime"));
        Assert.assertEquals("Gear1", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:gearName"));
        
        Assert.assertEquals("Program2", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:programName"));
        Assert.assertEquals("Inactive", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:currentStatus"));
        Assert.assertEquals("2008-10-14T13:45:01Z", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:startDateTime"));
        Assert.assertNull(outputTemplate.evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:stopDateTime"));
        Assert.assertEquals("Gear2", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:gearName"));
        
        
    }

}

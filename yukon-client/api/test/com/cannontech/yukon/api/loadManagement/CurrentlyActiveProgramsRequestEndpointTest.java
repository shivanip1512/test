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
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new CurrentlyActiveProgramsRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
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
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNotNull("No programStatuses node present.", outputTemplate.evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses"));
        Assert.assertEquals("Incorrect number of programStatuses nodes.", 1, outputTemplate.evaluateAsLong("count(/y:currentlyActiveProgramsResponse/y:programStatuses)"));
        Assert.assertEquals("Incorrect number of programStatus nodes.", 2, outputTemplate.evaluateAsLong("count(/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus)"));
        
        Assert.assertEquals("Incorrect programName.", "Program1", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:programName"));
        Assert.assertEquals("Incorrect currentStatus.", "Active", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:currentStatus"));
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-13T12:30:00Z", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:startDateTime"));
        Assert.assertEquals("Incorrect stopDateTime.", "2008-10-13T21:40:01Z", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:stopDateTime"));
        Assert.assertEquals("Incorrect gearName.", "Gear1", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:gearName"));
        
        Assert.assertEquals("Incorrect programName.", "Program2", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:programName"));
        Assert.assertEquals("Incorrect currentStatus.", "Inactive", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:currentStatus"));
        Assert.assertEquals("Incorrect startDateTime.", "2008-10-14T13:45:01Z", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:startDateTime"));
        Assert.assertNull("Should not contain stopDateTime node.", outputTemplate.evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:stopDateTime"));
        Assert.assertEquals("Incorrect gearName.", "Gear2", outputTemplate.evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:gearName"));
    }

}

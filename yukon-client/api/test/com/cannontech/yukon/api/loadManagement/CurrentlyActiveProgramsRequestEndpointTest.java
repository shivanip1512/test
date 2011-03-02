package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.CurrentlyActiveProgramsRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockProgramStatus;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class CurrentlyActiveProgramsRequestEndpointTest {

    private CurrentlyActiveProgramsRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private static final String EMTPY_RETURN_USER = "EMPTY";
    private static final String LIST_RETURN_USER = "LIST";
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new CurrentlyActiveProgramsRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
        @Override
        public List<ProgramStatus> getAllCurrentlyActivePrograms(LiteYukonUser user) {
            
            List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
            if (!user.getUsername().equals(EMTPY_RETURN_USER)){
                MockProgramStatus program1 = new MockProgramStatus("Program1", LMProgramBase.STATUS_ACTIVE, "2008-10-13T12:30:00Z", "2008-10-13T21:40:01Z", "Gear1");
                MockProgramStatus program2 = new MockProgramStatus("Program2", LMProgramBase.STATUS_INACTIVE, "2008-10-14T13:45:01Z", null, "Gear2");
                programStatuses.add(program1);
                programStatuses.add(program2);
            }
            
            return programStatuses;
        }
    }
    
    @Test
    public void testInvoke() throws Exception {
        
        // init
        Element requestElement = null;
        Attribute versionAttribute = null;
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        Resource requestSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CurrentlyActiveProgramsRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CurrentlyActiveProgramsResponse.xsd", this.getClass());

        // no programs, emtpy list
        //==========================================================================================
        requestElement = new Element("currentlyActiveProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        // run and validate response against xsd
        LiteYukonUser emptyReturnUser = new LiteYukonUser();
        emptyReturnUser.setUsername(EMTPY_RETURN_USER);
        responseElement = impl.invoke(requestElement, emptyReturnUser);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNotNull("No programStatuses node present.", outputTemplate.evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses"));
        Assert.assertEquals("Incorrect number of programStatuses nodes.", 1, outputTemplate.evaluateAsLong("count(/y:currentlyActiveProgramsResponse/y:programStatuses)").longValue());
        Assert.assertEquals("Incorrect number of programStatus nodes.", 0, outputTemplate.evaluateAsLong("count(/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus)").longValue());
        
        // 2 programs, one with no stop datetime
        //==========================================================================================
        requestElement = new Element("currentlyActiveProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        // run and validate response against xsd
        LiteYukonUser user = new LiteYukonUser();
        user.setUsername(LIST_RETURN_USER);
        responseElement = impl.invoke(requestElement, user);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        // outputs
        Assert.assertNotNull("No programStatuses node present.", outputTemplate.evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses"));
        Assert.assertEquals("Incorrect number of programStatuses nodes.", 1, outputTemplate.evaluateAsLong("count(/y:currentlyActiveProgramsResponse/y:programStatuses)").longValue());
        Assert.assertEquals("Incorrect number of programStatus nodes.", 2, outputTemplate.evaluateAsLong("count(/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus)").longValue());
        
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

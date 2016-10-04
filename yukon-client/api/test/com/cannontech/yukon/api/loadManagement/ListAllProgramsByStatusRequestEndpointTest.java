package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.loadcontrol.service.data.ProgramStatusType;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ListAllProgramsByStatusRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockProgramStatus;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.LoadManagementTestUtils;
import com.cannontech.yukon.api.utils.TestUtils;
import com.google.common.collect.Lists;

public class ListAllProgramsByStatusRequestEndpointTest {

    private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();

    private ListAllProgramsByStatusRequestEndpoint impl;
    private MockLoadControlService mockService;
    private Namespace ns = YukonXml.getYukonNamespace();

    @Before
    public void setUp() throws Exception {

        mockService = new MockLoadControlService();

        impl = new ListAllProgramsByStatusRequestEndpoint();
        ReflectionTestUtils.setField(impl, "loadControlService", mockService);
        ReflectionTestUtils.setField(impl, "rolePropertyDao", new MockRolePropertyDao());
    }

    private class MockLoadControlService extends LoadControlServiceAdapter {

        private boolean returnEmptyList = true;

        @Override
        public List<ProgramStatus> getAllProgramStatus(LiteYukonUser user, Set<ProgramStatusType> programStatusTypes) {

            List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
            List<ProgramStatus> filteredProgramStatusSet = new ArrayList<>();
            MockProgramStatus program1 =
                new MockProgramStatus("Program1", LMProgramBase.STATUS_ACTIVE, "2008-10-13T12:30:00Z",
                    "2008-10-13T21:40:01Z", "Gear1");
            MockProgramStatus program2 =
                new MockProgramStatus("Program2", LMProgramBase.STATUS_INACTIVE, "2008-10-14T13:45:01Z", null, "Gear2");
            MockProgramStatus program3 =
                new MockProgramStatus("Program3", LMProgramBase.STATUS_SCHEDULED, "2008-10-14T13:45:01Z", null, "Gear3");
            programStatuses.add(program1);
            programStatuses.add(program2);
            programStatuses.add(program3);

            if (returnEmptyList) {
                return programStatuses;
            } else {
                // Filter the result based on input programStatus (Active, Inactive, Scheduled)
                programStatusTypes.forEach(programStatusType -> {
                    programStatuses.stream().filter(programStatusType::checkProgramStatus).forEach(
                        filteredProgramStatusSet::add);
                });

                return filteredProgramStatusSet;
            }
        }

        public void setReturnEmptyList(boolean isEmpty) {
            returnEmptyList = isEmpty;
        }
    }

    @Test
    public void testInvoke() throws Exception {

        Resource requestSchemaResource =
            new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ListAllProgramsByStatusRequest.xsd",
                this.getClass());
        
        Resource responseSchemaResource =
            new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ListAllProgramsByStatusResponse.xsd",
                this.getClass());
       
        Element requestElement = new Element("listAllProgramsByStatusRequest", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);

        mockService.setReturnEmptyList(true);
        Element responseElement = impl.invoke(requestElement, AUTH_USER);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);

        SimpleXPathTemplate outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);

        // outputs
        Assert.assertNotNull("No programStatuses node present.",
            outputTemplate.evaluateAsNode("/y:listAllProgramsByStatusResponse/y:programStatuses"));
        Assert.assertEquals("Number of programStatuses nodes.", 1,
            outputTemplate.evaluateAsNodeList("/y:listAllProgramsByStatusResponse/y:programStatuses").size());
        //In the case of empty programStatus, return all the programs
        Assert.assertEquals("ProgramStatus nodes.", 3, outputTemplate.
            evaluateAsNodeList("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus").size());

        // Based on program status in request, programs will return
        // ==========================================================================================
        List <String> programStatues =  Lists.newArrayList("Active","Inactive", "Active"); // Request contains programStatus (Active && Inactive)
        requestElement = LoadManagementTestUtils.createProgramsByStatus("listAllProgramsByStatusRequest", programStatues, "1.0",
            requestSchemaResource);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);

        mockService.setReturnEmptyList(false);
        responseElement = impl.invoke(requestElement, AUTH_USER);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);

        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);

        // outputs
        Assert.assertNotNull("No programStatuses node present.",
            outputTemplate.evaluateAsNode("/y:listAllProgramsByStatusResponse/y:programStatuses"));
        Assert.assertEquals("Number of programStatuses nodes.", 1,
            outputTemplate.evaluateAsNodeList("/y:listAllProgramsByStatusResponse/y:programStatuses").size());
        Assert.assertEquals("Number of programStatus nodes.", 2,
            outputTemplate.evaluateAsNodeList("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus").size());

        Assert.assertEquals("ProgramName.", "Program1",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[1]/y:programName"));
        Assert.assertEquals("CurrentStatus.", "Active",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[1]/y:currentStatus"));
        Assert.assertEquals("StartDateTime.", "2008-10-13T12:30:00Z",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[1]/y:startDateTime"));
        Assert.assertEquals("StopDateTime.", "2008-10-13T21:40:01Z",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[1]/y:stopDateTime"));
        Assert.assertEquals("GearName.", "Gear1",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[1]/y:gearName"));

        Assert.assertEquals("ProgramName.", "Program2",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[2]/y:programName"));
        Assert.assertEquals("CurrentStatus.", "Inactive",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[2]/y:currentStatus"));
       Assert.assertEquals("StartDateTime.", "2008-10-14T13:45:01Z",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[2]/y:startDateTime"));
        Assert.assertNull("Should not contain stopDateTime node.",
            outputTemplate.evaluateAsNode("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[2]/y:stopDateTime"));
        Assert.assertEquals("GearName.", "Gear2",
            outputTemplate.evaluateAsString("/y:listAllProgramsByStatusResponse/y:programStatuses/y:programStatus[2]/y:gearName"));

    }

}

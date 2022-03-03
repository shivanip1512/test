package com.cannontech.yukon.api.loadManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.jdom2.Attribute;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.data.LMProgramBase;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.CurrentlyActiveProgramsRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockProgramStatus;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.TestUtils;

public class CurrentlyActiveProgramsRequestEndpointTest {

    private static final LiteYukonUser AUTH_USER = MockRolePropertyDao.getAuthorizedUser();

    private CurrentlyActiveProgramsRequestEndpoint impl;
    private MockLoadControlService mockService;

    private Namespace ns = YukonXml.getYukonNamespace();

    @BeforeEach
    public void setUp() throws Exception {

        mockService = new MockLoadControlService();

        impl = new CurrentlyActiveProgramsRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.setRolePropertyDao(new MockRolePropertyDao());
        impl.initialize();
    }

    private class MockLoadControlService extends LoadControlServiceAdapter {

        private boolean returnEmptyList = true;

        @Override
        public List<ProgramStatus> getAllCurrentlyActivePrograms(LiteYukonUser user) {

            List<ProgramStatus> programStatuses = new ArrayList<ProgramStatus>();
            if (returnEmptyList) {
                return programStatuses;
            } else {
                MockProgramStatus program1 =
                    new MockProgramStatus("Program1", LMProgramBase.STATUS_ACTIVE, "2008-10-13T12:30:00Z",
                        "2008-10-13T21:40:01Z", "Gear1");
                MockProgramStatus program2 =
                    new MockProgramStatus("Program2", LMProgramBase.STATUS_INACTIVE, "2008-10-14T13:45:01Z", null,
                        "Gear2");
                programStatuses.add(program1);
                programStatuses.add(program2);
                return programStatuses;
            }
        }

        public void setReturnEmptyList(boolean isEmpty) {
            returnEmptyList = isEmpty;
        }
    }

    @Test
    public void testInvoke() throws Exception {

        // init
        Element requestElement = null;
        Attribute versionAttribute = null;
        Element responseElement = null;
        SimpleXPathTemplate outputTemplate = null;
        Resource requestSchemaResource =
            new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CurrentlyActiveProgramsRequest.xsd",
                this.getClass());
        Resource responseSchemaResource =
            new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/CurrentlyActiveProgramsResponse.xsd",
                this.getClass());

        // no programs, emtpy list
        // ==========================================================================================
        requestElement = new Element("currentlyActiveProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);

        // run and validate response against xsd
        mockService.setReturnEmptyList(true);
        responseElement = impl.invoke(requestElement, AUTH_USER);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);

        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);

        // outputs
        assertNotNull(outputTemplate.evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses"),
                "No programStatuses node present.");
        assertEquals(1,
                outputTemplate.evaluateAsNodeList("/y:currentlyActiveProgramsResponse/y:programStatuses").size(),
                "Incorrect number of programStatuses nodes.");
        assertEquals(0,
                outputTemplate.evaluateAsNodeList("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus").size(),
                "Incorrect number of programStatus nodes.");

        // 2 programs, one with no stop datetime
        // ==========================================================================================
        requestElement = new Element("currentlyActiveProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);

        // run and validate response against xsd
        mockService.setReturnEmptyList(false);
        responseElement = impl.invoke(requestElement, AUTH_USER);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);

        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);

        // outputs
        assertNotNull(outputTemplate.evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses"),
                "No programStatuses node present.");
        assertEquals(1,
                outputTemplate.evaluateAsNodeList("/y:currentlyActiveProgramsResponse/y:programStatuses").size(),
                "Incorrect number of programStatuses nodes.");
        assertEquals(2,
                outputTemplate.evaluateAsNodeList("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus").size(),
                "Incorrect number of programStatus nodes.");

        assertEquals("Program1",
                outputTemplate.evaluateAsString(
                        "/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:programName"),
                "Incorrect programName.");
        assertEquals("Active",
                outputTemplate.evaluateAsString(
                        "/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:currentStatus"),
                "Incorrect currentStatus.");
        assertEquals("2008-10-13T12:30:00Z",
                outputTemplate.evaluateAsString(
                        "/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:startDateTime"),
                "Incorrect startDateTime.");
        assertEquals("2008-10-13T21:40:01Z",
                outputTemplate.evaluateAsString(
                        "/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:stopDateTime"),
                "Incorrect stopDateTime.");
        assertEquals("Gear1",
                outputTemplate
                        .evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[1]/y:gearName"),
                "Incorrect gearName.");

        assertEquals("Program2",
                outputTemplate.evaluateAsString(
                        "/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:programName"),
                "Incorrect programName.");
        assertEquals("Inactive",
                outputTemplate.evaluateAsString(
                        "/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:currentStatus"),
                "Incorrect currentStatus.");
        assertEquals("2008-10-14T13:45:01Z",
                outputTemplate.evaluateAsString(
                        "/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:startDateTime"),
                "Incorrect startDateTime.");
        assertNull(
                outputTemplate
                        .evaluateAsNode("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:stopDateTime"),
                "Should not contain stopDateTime node.");
        assertEquals("Gear2",
                outputTemplate
                        .evaluateAsString("/y:currentlyActiveProgramsResponse/y:programStatuses/y:programStatus[2]/y:gearName"),
                "Incorrect gearName.");
    }

}

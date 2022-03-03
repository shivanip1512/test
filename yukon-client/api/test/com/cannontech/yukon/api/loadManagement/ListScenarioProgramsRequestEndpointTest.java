package com.cannontech.yukon.api.loadManagement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ListScenarioProgramsRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.TestUtils;

public class ListScenarioProgramsRequestEndpointTest {

    private ListScenarioProgramsRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private static final String EMTPY_RETURN_SCENARIO = "EMPTY";
    
    @BeforeEach
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ListScenarioProgramsRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.setRolePropertyDao(new MockRolePropertyDao());
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
        private String scenarioName;
        
        @Override
        public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String scenarioName, LiteYukonUser user) throws NotFoundException {
            
            this.scenarioName = scenarioName;
            
            if (scenarioName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            }
            
            List<ProgramStartingGear> programStartingGears = new ArrayList<ProgramStartingGear>();
            if (!scenarioName.equals(EMTPY_RETURN_SCENARIO)){            
                ProgramStartingGear programGear1 = new ProgramStartingGear(1, "Program1", "Gear1", 1);
                ProgramStartingGear programGear2 = new ProgramStartingGear(2, "Program2", "Gear2", 1);
                ProgramStartingGear programGear3 = new ProgramStartingGear(3, "Program3", "Gear3", 1);

                programStartingGears.add(programGear1);
                programStartingGears.add(programGear2);
                programStartingGears.add(programGear3);
            }
            
            ScenarioProgramStartingGears scenarioProgramStartingGears = new ScenarioProgramStartingGears(scenarioName, programStartingGears);
            
            return scenarioProgramStartingGears;
        }
        
        public String getScenarioName() {
            return scenarioName;
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
        Resource requestSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ListScenarioProgramsRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ListScenarioProgramsResponse.xsd", this.getClass());
        
        // scenario name, empty program starting gears
        //==========================================================================================
        requestElement = new Element("listScenarioProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, EMTPY_RETURN_SCENARIO);
        requestElement.addContent(tmpElement);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertNotNull(outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioName"), "No scenarioName node present.");
        assertNotNull(outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioProgramsList"), "No scenarioProgramsList node present.");
        assertEquals(EMTPY_RETURN_SCENARIO, mockService.getScenarioName(), "Incorrect scenarioName");
        assertEquals(1, outputTemplate.evaluateAsNodeList("/y:listScenarioProgramsResponse/y:scenarioProgramsList").size(), "Incorrect number of scenarioProgramsList nodes.");
        assertEquals(0, outputTemplate.evaluateAsNodeList("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram").size(), "Incorrect number of scenarioProgram nodes.");
        
        // scenario name, 3 program starting gears
        //==========================================================================================
        requestElement = new Element("listScenarioProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "Test Scenario");
        requestElement.addContent(tmpElement);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertNotNull(outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioName"), "No scenarioName node present.");
        assertNotNull(outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioProgramsList"), "No scenarioProgramsList node present.");
        assertEquals("Test Scenario", mockService.getScenarioName(), "Incorrect scenarioName");
        assertEquals(1, outputTemplate.evaluateAsNodeList("/y:listScenarioProgramsResponse/y:scenarioProgramsList").size(), "Incorrect number of scenarioProgramsList nodes.");
        assertEquals(3, outputTemplate.evaluateAsNodeList("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram").size(), "Incorrect number of scenarioProgram nodes.");
        assertEquals("Program1", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[1]/y:programName"), "Incorrect programName.");
        assertEquals("Gear1", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[1]/y:startGearName"), "Incorrect startGearName.");
        assertEquals("Program2", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[2]/y:programName"), "Incorrect programName.");
        assertEquals("Gear2", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[2]/y:startGearName"), "Incorrect startGearName.");
        assertEquals("Program3", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[3]/y:programName"), "Incorrect programName.");
        assertEquals("Gear3", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[3]/y:startGearName"), "Incorrect startGearName.");
        
        // not found
        //==========================================================================================
        requestElement = new Element("listScenarioProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "NOT_FOUND");
        requestElement.addContent(tmpElement);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "listScenarioProgramsResponse", "InvalidScenarioName");
    }

}

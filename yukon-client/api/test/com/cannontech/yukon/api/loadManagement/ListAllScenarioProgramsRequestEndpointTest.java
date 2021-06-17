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
import com.cannontech.yukon.api.loadManagement.endpoint.ListAllScenarioProgramsRequestEndpoint;
import com.cannontech.yukon.api.loadManagement.mocks.MockRolePropertyDao;
import com.cannontech.yukon.api.utils.TestUtils;
import com.google.common.collect.Lists;

public class ListAllScenarioProgramsRequestEndpointTest {

    private ListAllScenarioProgramsRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private static final String EMTPY_RETURN_SCENARIO = "EMPTY";
    
    @BeforeEach
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ListAllScenarioProgramsRequestEndpoint();
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
        
        @Override
        public List<ScenarioProgramStartingGears> getAllScenarioProgramStartingGears(LiteYukonUser user) {
        	
    		List<ScenarioProgramStartingGears> allScenarioProgramStartingGears = Lists.newArrayListWithExpectedSize(3);
    		
    		List<ProgramStartingGear> programs1 = Lists.newArrayListWithCapacity(1);
    		programs1.add(new ProgramStartingGear(1, "Program1", "Gear1", 1));
            ScenarioProgramStartingGears scenarioProgramStartingGears1 = new ScenarioProgramStartingGears("Scenario1", programs1);
            allScenarioProgramStartingGears.add(scenarioProgramStartingGears1);
            
            List<ProgramStartingGear> programs2 = Lists.newArrayListWithCapacity(2);
    		programs2.add(new ProgramStartingGear(1, "Program1", "Gear1", 1));
    		programs2.add(new ProgramStartingGear(2, "Program2", "Gear2", 1));
            ScenarioProgramStartingGears scenarioProgramStartingGears2 = new ScenarioProgramStartingGears("Scenario2", programs2);
            allScenarioProgramStartingGears.add(scenarioProgramStartingGears2);
            
            List<ProgramStartingGear> programs3 = Lists.newArrayListWithCapacity(2);
    		programs3.add(new ProgramStartingGear(1, "Program1", "Gear1", 1));
    		programs3.add(new ProgramStartingGear(2, "Program2", "Gear2", 1));
    		programs3.add(new ProgramStartingGear(3, "Program3", "Gear3", 1));
            ScenarioProgramStartingGears scenarioProgramStartingGears3 = new ScenarioProgramStartingGears("Scenario3", programs3);
            allScenarioProgramStartingGears.add(scenarioProgramStartingGears3);
        	
        	
        	return allScenarioProgramStartingGears;
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
        Resource requestSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ListAllScenarioProgramsRequest.xsd", this.getClass());
        Resource responseSchemaResource = new ClassPathResource("/com/cannontech/yukon/api/loadManagement/schemas/ListAllScenarioProgramsResponse.xsd", this.getClass());
        
        // no scenario name
        //==========================================================================================
        requestElement = new Element("listAllScenarioProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        XmlUtils.printElement(responseElement, "");
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        XmlUtils.printElement(responseElement, "");
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        XmlUtils.printElement(responseElement, "");
        assertEquals("Scenario1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[1]/y:scenarioName"), "Wrong scenario name");
        assertEquals("Scenario2", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[2]/y:scenarioName"), "Wrong scenario name");
        assertEquals("Scenario3", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[3]/y:scenarioName"), "Wrong scenario name");
        
        assertEquals( 1, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[1]/y:scenarioProgramsList/y:scenarioProgram").size(), "Wrong number of programs");
        assertEquals(2, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[2]/y:scenarioProgramsList/y:scenarioProgram").size(), "Wrong number of programs");
        assertEquals(3, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[3]/y:scenarioProgramsList/y:scenarioProgram").size(), "Wrong number of programs");
        
        assertEquals("Program1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[3]/y:scenarioProgramsList/y:scenarioProgram[1]/y:programName"), "Wrong program name");
        assertEquals("Gear2", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[3]/y:scenarioProgramsList/y:scenarioProgram[2]/y:startGearName"), "Wrong gear name");
        assertEquals("Program1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario[2]/y:scenarioProgramsList/y:scenarioProgram[1]/y:programName"), "Wrong program name");
        
        assertNotNull(outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioList"), "No scenarioList node present.");
        assertEquals(3, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario").size(), "Incorrect number of scenario nodes.");
        assertEquals(0, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenario").size(), "Incorrect number of scenario nodes.");
        
        // scenario name, empty program starting gears
        //==========================================================================================
        requestElement = new Element("listAllScenarioProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, EMTPY_RETURN_SCENARIO);
        requestElement.addContent(tmpElement);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        XmlUtils.printElement(responseElement, "");
        assertNotNull(outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioName"), "No scenarioName node present.");
        assertNotNull(outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario"), "No scenario node present.");
        assertEquals(EMTPY_RETURN_SCENARIO, mockService.getScenarioName(), "Incorrect scenarioName");
        assertEquals(1, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario").size(), "Incorrect number of scenarioProgramsList nodes.");
        assertEquals(0, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioProgram").size(), "Incorrect number of scenarioProgram nodes.");
        
        // scenario name, 3 program starting gears
        //==========================================================================================
        requestElement = new Element("listAllScenarioProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "Test Scenario");
        requestElement.addContent(tmpElement);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        assertNotNull(outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioName"), "No scenarioName node present.");
        assertNotNull(outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse//y:scenarioList/y:scenario"), "No scenario node present.");
        assertEquals("Test Scenario", mockService.getScenarioName(), "Incorrect scenarioName");
        assertEquals(1, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario").size(), "Incorrect number of scenario nodes.");
        
        assertEquals(3, outputTemplate.evaluateAsNodeList("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioProgramsList/y:scenarioProgram").size(), "Incorrect number of scenarioProgram nodes.");
        assertEquals("Program1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioProgramsList/y:scenarioProgram[1]/y:programName"), "Incorrect programName.");
        assertEquals("Gear1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioProgramsList/y:scenarioProgram[1]/y:startGearName"), "Incorrect startGearName.");
        assertEquals("Program2", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioProgramsList/y:scenarioProgram[2]/y:programName"), "Incorrect programName.");
        assertEquals("Gear2", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioProgramsList/y:scenarioProgram[2]/y:startGearName"), "Incorrect startGearName.");
        assertEquals("Program3", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioProgramsList/y:scenarioProgram[3]/y:programName"), "Incorrect programName.");
        assertEquals("Gear3", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioList/y:scenario/y:scenarioProgramsList/y:scenarioProgram[3]/y:startGearName"), "Incorrect startGearName.");
        
        // not found
        //==========================================================================================
        requestElement = new Element("listAllScenarioProgramsRequest", ns);
        versionAttribute = new Attribute("version", "1.0");
        requestElement.setAttribute(versionAttribute);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "NOT_FOUND");
        requestElement.addContent(tmpElement);
        TestUtils.validateAgainstSchema(requestElement, requestSchemaResource);
        
        responseElement = impl.invoke(requestElement, null);
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        outputTemplate = YukonXml.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "listAllScenarioProgramsResponse", "InvalidScenarioName");
    }

}

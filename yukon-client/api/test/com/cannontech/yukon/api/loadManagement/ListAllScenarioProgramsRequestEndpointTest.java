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

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.yukon.api.loadManagement.endpoint.ListAllScenarioProgramsRequestEndpoint;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;
import com.google.common.collect.Lists;

public class ListAllScenarioProgramsRequestEndpointTest {

    private ListAllScenarioProgramsRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private static final String EMTPY_RETURN_SCENARIO = "EMPTY";
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ListAllScenarioProgramsRequestEndpoint();
        impl.setLoadControlService(mockService);
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
        TestUtils.validateAgainstSchema(responseElement, responseSchemaResource);
        
        XmlUtils.printElement(responseElement, "");
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        XmlUtils.printElement(responseElement, "");
        Assert.assertEquals("Wrong scenario name", "Scenario1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[1]/y:scenarioName"));
        Assert.assertEquals("Wrong scenario name", "Scenario2", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[2]/y:scenarioName"));
        Assert.assertEquals("Wrong scenario name", "Scenario3", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[3]/y:scenarioName"));
        
        Assert.assertEquals("Wrong number of programs", 1, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[1]/y:programsList/y:scenarioProgram)").longValue());
        Assert.assertEquals("Wrong number of programs", 2, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[2]/y:programsList/y:scenarioProgram)").longValue());
        Assert.assertEquals("Wrong number of programs", 3, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[3]/y:programsList/y:scenarioProgram)").longValue());
        
        Assert.assertEquals("Wrong number of programs", "Program1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[3]/y:programsList/y:scenarioProgram[1]/y:programName"));
        Assert.assertEquals("Wrong number of programs", "Gear2", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[3]/y:programsList/y:scenarioProgram[2]/y:startGearName"));
        Assert.assertEquals("Wrong number of programs", "Program1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList[2]/y:programsList/y:scenarioProgram[1]/y:programName"));
        
        Assert.assertNotNull("No scenarioProgramsList node present.", outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList"));
        Assert.assertEquals("Incorrect number of scenarioProgramsList nodes.", 3, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList)").longValue());
        Assert.assertEquals("Incorrect number of scenarioProgram nodes.", 0, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram)").longValue());
        
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
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        XmlUtils.printElement(responseElement, "");
        Assert.assertNotNull("No scenarioName node present.", outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioName"));
        Assert.assertNotNull("No scenarioProgramsList node present.", outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList"));
        Assert.assertEquals("Incorrect scenarioName", EMTPY_RETURN_SCENARIO, mockService.getScenarioName());
        Assert.assertEquals("Incorrect number of scenarioProgramsList nodes.", 1, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList)").longValue());
        Assert.assertEquals("Incorrect number of scenarioProgram nodes.", 0, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram)").longValue());
        
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
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        Assert.assertNotNull("No scenarioName node present.", outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioName"));
        Assert.assertNotNull("No scenarioProgramsList node present.", outputTemplate.evaluateAsNode("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList"));
        Assert.assertEquals("Incorrect scenarioName", "Test Scenario", mockService.getScenarioName());
        Assert.assertEquals("Incorrect number of scenarioProgramsList nodes.", 1, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList)").longValue());
        
        Assert.assertEquals("Incorrect number of scenarioProgram nodes.", 3, outputTemplate.evaluateAsLong("count(/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:programsList/y:scenarioProgram)").longValue());
        Assert.assertEquals("Incorrect programName.", "Program1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:programsList/y:scenarioProgram[1]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear1", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:programsList/y:scenarioProgram[1]/y:startGearName"));
        Assert.assertEquals("Incorrect programName.", "Program2", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:programsList/y:scenarioProgram[2]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear2", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:programsList/y:scenarioProgram[2]/y:startGearName"));
        Assert.assertEquals("Incorrect programName.", "Program3", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:programsList/y:scenarioProgram[3]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear3", outputTemplate.evaluateAsString("/y:listAllScenarioProgramsResponse/y:scenarioProgramsList/y:programsList/y:scenarioProgram[3]/y:startGearName"));
        
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
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "listAllScenarioProgramsResponse", "InvalidScenarioName");
    }

}

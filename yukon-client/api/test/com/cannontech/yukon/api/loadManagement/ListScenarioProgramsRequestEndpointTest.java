package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;
import com.cannontech.yukon.api.utils.TestUtils;

public class ListScenarioProgramsRequestEndpointTest {

    private ListScenarioProgramsRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
        mockService = new MockLoadControlService();
        
        impl = new ListScenarioProgramsRequestEndpoint();
        impl.setLoadControlService(mockService);
        impl.initialize();
    }
    
    private class MockLoadControlService extends LoadControlServiceAdapter {
        
        private String scenarioName;
        
        @Override
        public ScenarioProgramStartingGears getScenarioProgramStartingGearsByScenarioName(String scenarioName) throws NotFoundException {
            
            this.scenarioName = scenarioName;
            
            if (scenarioName.equals("NOT_FOUND")) {
                throw new NotFoundException("");
            }
            
            ProgramStartingGear programGear1 = new ProgramStartingGear("Program1", "Gear1", 1);
            ProgramStartingGear programGear2 = new ProgramStartingGear("Program2", "Gear2", 1);
            ProgramStartingGear programGear3 = new ProgramStartingGear("Program3", "Gear3", 1);
            
            List<ProgramStartingGear> programStartingGears = new ArrayList<ProgramStartingGear>();
            programStartingGears.add(programGear1);
            programStartingGears.add(programGear2);
            programStartingGears.add(programGear3);
            
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
        Element responseElement = null;
        Element tmpElement = null;
        SimpleXPathTemplate outputTemplate = null;
        
        // scenario name, 3 program starting gears
        //==========================================================================================
        requestElement = new Element("listScenarioProgramsRequest", ns);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "Test Scenario");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement);
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        Assert.assertNotNull("No scenarioName node present.", outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioName"));
        Assert.assertNotNull("No scenarioProgramsList node present.", outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioProgramsList"));
        Assert.assertEquals("Incorrect scenarioName", "Test Scenario", mockService.getScenarioName());
        Assert.assertEquals("Incorrect number of scenarioProgramsList nodes.", 1, outputTemplate.evaluateAsLong("count(/y:listScenarioProgramsResponse/y:scenarioProgramsList)"));
        Assert.assertEquals("Incorrect number of scenarioProgram nodes.", 3, outputTemplate.evaluateAsLong("count(/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram)"));
        Assert.assertEquals("Incorrect programName.", "Program1", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[1]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear1", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[1]/y:startGearName"));
        Assert.assertEquals("Incorrect programName.", "Program2", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[2]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear2", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[2]/y:startGearName"));
        Assert.assertEquals("Incorrect programName.", "Program3", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[3]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear3", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[3]/y:startGearName"));
        
        // not found
        //==========================================================================================
        requestElement = new Element("listScenarioProgramsRequest", ns);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "NOT_FOUND");
        requestElement.addContent(tmpElement);
        
        responseElement = impl.invoke(requestElement);
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        
        TestUtils.runFailureAssertions(outputTemplate, "listScenarioProgramsResponse", "InvalidScenarioName");
    }

}

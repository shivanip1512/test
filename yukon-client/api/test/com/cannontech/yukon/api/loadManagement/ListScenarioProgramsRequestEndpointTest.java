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

public class ListScenarioProgramsRequestEndpointTest {

    private ListScenarioProgramsRequestEndpoint impl;
    private LoadControlServiceTest testService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @Before
    public void setUp() throws Exception {
        
        testService = new LoadControlServiceTest();
        
        impl = new ListScenarioProgramsRequestEndpoint();
        impl.setLoadControlService(testService);
        impl.initialize();
    }
    
    private class LoadControlServiceTest extends LoadControlServiceAdapter {
        
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
        XmlUtils.printElement(requestElement, "REQUEST (test scenario)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (test scenario)");
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioName"));
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioProgramsList"));
        Assert.assertEquals("Test Scenario", testService.getScenarioName());
        Assert.assertEquals(1, outputTemplate.evaluateAsLong("count(/y:listScenarioProgramsResponse/y:scenarioProgramsList)"));
        Assert.assertEquals(3, outputTemplate.evaluateAsLong("count(/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram)"));
        Assert.assertEquals("Program1", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[1]/y:programName"));
        Assert.assertEquals("Gear1", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[1]/y:startGearName"));
        Assert.assertEquals("Program2", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[2]/y:programName"));
        Assert.assertEquals("Gear2", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[2]/y:startGearName"));
        Assert.assertEquals("Program3", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[3]/y:programName"));
        Assert.assertEquals("Gear3", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[3]/y:startGearName"));
        
        // not found
        //==========================================================================================
        requestElement = new Element("listScenarioProgramsRequest", ns);
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, "NOT_FOUND");
        requestElement.addContent(tmpElement);
        XmlUtils.printElement(requestElement, "REQUEST (not found)");
        
        responseElement = impl.invoke(requestElement);
        XmlUtils.printElement(responseElement, "RESPONSE (not found)");
        
        outputTemplate = XmlUtils.getXPathTemplateForElement(responseElement);
        Assert.assertNotNull(outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:failure"));
        Assert.assertEquals("InvalidScenarioName", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:failure/y:errorCode"));

    }

}

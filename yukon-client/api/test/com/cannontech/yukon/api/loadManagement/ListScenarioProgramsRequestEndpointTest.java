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

import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.yukon.api.loadManagement.adapters.LoadControlServiceAdapter;
import com.cannontech.yukon.api.loadManagement.endpoint.ListScenarioProgramsRequestEndpoint;
import com.cannontech.yukon.api.utils.TestUtils;

public class ListScenarioProgramsRequestEndpointTest {

    private ListScenarioProgramsRequestEndpoint impl;
    private MockLoadControlService mockService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private static final String EMTPY_RETURN_SCENARIO = "EMPTY";
    
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
        
        Assert.assertNotNull("No scenarioName node present.", outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioName"));
        Assert.assertNotNull("No scenarioProgramsList node present.", outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioProgramsList"));
        Assert.assertEquals("Incorrect scenarioName", EMTPY_RETURN_SCENARIO, mockService.getScenarioName());
        Assert.assertEquals("Incorrect number of scenarioProgramsList nodes.", 1, outputTemplate.evaluateAsLong("count(/y:listScenarioProgramsResponse/y:scenarioProgramsList)").longValue());
        Assert.assertEquals("Incorrect number of scenarioProgram nodes.", 0, outputTemplate.evaluateAsLong("count(/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram)").longValue());
        
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
        
        Assert.assertNotNull("No scenarioName node present.", outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioName"));
        Assert.assertNotNull("No scenarioProgramsList node present.", outputTemplate.evaluateAsNode("/y:listScenarioProgramsResponse/y:scenarioProgramsList"));
        Assert.assertEquals("Incorrect scenarioName", "Test Scenario", mockService.getScenarioName());
        Assert.assertEquals("Incorrect number of scenarioProgramsList nodes.", 1, outputTemplate.evaluateAsLong("count(/y:listScenarioProgramsResponse/y:scenarioProgramsList)").longValue());
        Assert.assertEquals("Incorrect number of scenarioProgram nodes.", 3, outputTemplate.evaluateAsLong("count(/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram)").longValue());
        Assert.assertEquals("Incorrect programName.", "Program1", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[1]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear1", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[1]/y:startGearName"));
        Assert.assertEquals("Incorrect programName.", "Program2", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[2]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear2", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[2]/y:startGearName"));
        Assert.assertEquals("Incorrect programName.", "Program3", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[3]/y:programName"));
        Assert.assertEquals("Incorrect startGearName.", "Gear3", outputTemplate.evaluateAsString("/y:listScenarioProgramsResponse/y:scenarioProgramsList/y:scenarioProgram[3]/y:startGearName"));
        
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

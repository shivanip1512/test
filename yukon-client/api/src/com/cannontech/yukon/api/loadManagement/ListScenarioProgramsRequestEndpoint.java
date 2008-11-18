package com.cannontech.yukon.api.loadManagement;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ListScenarioProgramsRequestEndpoint {

    private LoadControlService loadControlService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String scenarioNameExpressionStr = "/y:listScenarioProgramsRequest/y:scenarioName";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="listScenarioProgramsRequest")
    public Element invoke(Element listScenarioProgramsRequest) throws Exception {
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(listScenarioProgramsRequest);
        
        String scenarioName = requestTemplate.evaluateAsString(scenarioNameExpressionStr);

        // run service
        Element resp = new Element("listScenarioProgramsResponse", ns);
        
        ScenarioProgramStartingGears scenarioProgramStartingGears = null;
        try {
            scenarioProgramStartingGears = loadControlService.getScenarioProgramStartingGearsByScenarioName(scenarioName);
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(listScenarioProgramsRequest, e, "InvalidScenarioName", "No scenario named \"" + scenarioName + "\" found.");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        Element tmpElement = null;
        tmpElement = XmlUtils.createStringElement("scenarioName", ns, scenarioProgramStartingGears.getScenarioName());
        resp.addContent(tmpElement);
        
        Element scenarioProgramsList = new Element("scenarioProgramsList", ns);
        
        List<ProgramStartingGear> programStartingGears = scenarioProgramStartingGears.getProgramStartingGears();
        for (ProgramStartingGear programStartingGear : programStartingGears) {
            
            Element scenarioProgram = new Element("scenarioProgram", ns);
            
            tmpElement = XmlUtils.createStringElement("programName", ns, programStartingGear.getProgramName());
            scenarioProgram.addContent(tmpElement);
            tmpElement = XmlUtils.createStringElement("startGearName", ns, programStartingGear.getStartingGearName());
            scenarioProgram.addContent(tmpElement);
            
            scenarioProgramsList.addContent(scenarioProgram);
        }
        
        resp.addContent(scenarioProgramsList);
        
        return resp;
    }
    
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
}

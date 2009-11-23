package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
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
    public Element invoke(Element listScenarioProgramsRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(listScenarioProgramsRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(listScenarioProgramsRequest);
        
        String scenarioName = requestTemplate.evaluateAsString(scenarioNameExpressionStr);

        // init response
        Element resp = new Element("listScenarioProgramsResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        List<ScenarioProgramStartingGears> allScenarioProgramStartingGears = null;
        try {
            
        	if (StringUtils.isBlank(scenarioName)) {
        		allScenarioProgramStartingGears = loadControlService.getAllScenarioProgramStartingGears(user);
        	} else {
        		allScenarioProgramStartingGears = Collections.singletonList(loadControlService.getScenarioProgramStartingGearsByScenarioName(scenarioName, user));
        	}
            
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(listScenarioProgramsRequest, e, "InvalidScenarioName", "No scenario named: " + scenarioName);
            resp.addContent(fe);
            return resp;
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(listScenarioProgramsRequest, e, "UserNotAuthorized", "The scenario is not visible to the user.");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        Element tmpElement = null;
        
        for (ScenarioProgramStartingGears scenarioProgramStartingGears : allScenarioProgramStartingGears) {
        	
        	Element scenarioProgramsList = new Element("scenarioProgramsList", ns);
        	
        	tmpElement = XmlUtils.createStringElement("scenarioName", ns, scenarioProgramStartingGears.getScenarioName());
        	scenarioProgramsList.addContent(tmpElement);
        	
        	Element programsList = new Element("programsList", ns);
        	
	        List<ProgramStartingGear> programStartingGears = scenarioProgramStartingGears.getProgramStartingGears();
	        for (ProgramStartingGear programStartingGear : programStartingGears) {
	            
	            Element scenarioProgram = new Element("scenarioProgram", ns);
	            
	            tmpElement = XmlUtils.createStringElement("programName", ns, programStartingGear.getProgramName());
	            scenarioProgram.addContent(tmpElement);
	            tmpElement = XmlUtils.createStringElement("startGearName", ns, programStartingGear.getStartingGearName());
	            scenarioProgram.addContent(tmpElement);
	            
	            programsList.addContent(scenarioProgram);
	        }
	        
	        scenarioProgramsList.addContent(programsList);
	        
	        resp.addContent(scenarioProgramsList);
        }
        
        return resp;
    }
    
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
}

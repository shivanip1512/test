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
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStartingGear;
import com.cannontech.loadcontrol.service.data.ScenarioProgramStartingGears;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ListAllScenarioProgramsRequestEndpoint {

    private LoadControlService loadControlService;
    private RolePropertyDao rolePropertyDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String scenarioNameExpressionStr = "/y:listAllScenarioProgramsRequest/y:scenarioName";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="listAllScenarioProgramsRequest")
    public Element invoke(Element listAllScenarioProgramsRequest, LiteYukonUser user) throws Exception {
        
    	// version
    	XmlVersionUtils.verifyYukonMessageVersion(listAllScenarioProgramsRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(listAllScenarioProgramsRequest);
        
        String scenarioName = requestTemplate.evaluateAsString(scenarioNameExpressionStr);

        // init response
        Element resp = new Element("listAllScenarioProgramsResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        try {
            
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
        	
        	List<ScenarioProgramStartingGears> allScenarioProgramStartingGears = null;
        	if (StringUtils.isBlank(scenarioName)) {
        		allScenarioProgramStartingGears = loadControlService.getAllScenarioProgramStartingGears(user);
        	} else {
        		allScenarioProgramStartingGears = Collections.singletonList(loadControlService.getScenarioProgramStartingGearsByScenarioName(scenarioName, user));
        	}

        	Element tmpElement = null;
        	Element scenarioList = new Element("scenarioList", ns);
            
            for (ScenarioProgramStartingGears scenarioProgramStartingGears : allScenarioProgramStartingGears) {
            	
            	Element scenario = new Element("scenario", ns);
            	
            	tmpElement = XmlUtils.createStringElement("scenarioName", ns, scenarioProgramStartingGears.getScenarioName());
            	scenario.addContent(tmpElement);
            	
            	Element programsList = new Element("scenarioProgramsList", ns);
            	
    	        List<ProgramStartingGear> programStartingGears = scenarioProgramStartingGears.getProgramStartingGears();
    	        for (ProgramStartingGear programStartingGear : programStartingGears) {
    	            
    	            Element scenarioProgram = new Element("scenarioProgram", ns);
    	            
    	            tmpElement = XmlUtils.createStringElement("programName", ns, programStartingGear.getProgramName());
    	            scenarioProgram.addContent(tmpElement);
    	            tmpElement = XmlUtils.createStringElement("startGearName", ns, programStartingGear.getStartingGearName());
    	            scenarioProgram.addContent(tmpElement);
    	            
    	            programsList.addContent(scenarioProgram);
    	        }
    	        
    	        scenario.addContent(programsList);
    	        
    	        scenarioList.addContent(scenario);
            }
            
            resp.addContent(scenarioList);
            
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(listAllScenarioProgramsRequest, e, "InvalidScenarioName", "No scenario named: " + scenarioName);
            resp.addContent(fe);
            return resp;
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(listAllScenarioProgramsRequest, e, "UserNotAuthorized", "The user is not authorized to request all scenarios.");
            resp.addContent(fe);
            return resp;
        }
        
        return resp;
    }
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}
package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Date;

import javax.annotation.PostConstruct;

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
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ScenarioStopRequestEndpoint {

    private LoadControlService loadControlService;
    private RolePropertyDao rolePropertyDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String scenarioNameExpressionStr = "/y:scenarioStopRequest/y:scenarioName";
    private String stopTimeExpressionStr = "/y:scenarioStopRequest/y:stopDateTime";
    private String waitForResponseExpressionStr = "/y:scenarioStopRequest/y:waitForResponse";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="scenarioStopRequest")
    public Element invoke(Element scenarioStopRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(scenarioStopRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(scenarioStopRequest);
        
        String scenarioName = requestTemplate.evaluateAsString(scenarioNameExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopTimeExpressionStr);
        Boolean waitForResponse = requestTemplate.evaluateAsBooleanWithDefault(waitForResponseExpressionStr, false);

        // init response
        Element resp = new Element("scenarioStopResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        try {
            
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_CONTROL_ACCESS, user);
            
        	if (waitForResponse) {
        		loadControlService.stopControlByScenarioName(scenarioName, stopTime, false, true, user);
        	} else {
        		loadControlService.asynchStopControlByScenarioName(scenarioName, stopTime, false, true, user);
        	}
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "InvalidScenarioName", "No scenario named: " + scenarioName);
            resp.addContent(fe);
            return resp;
        } catch (TimeoutException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "Timeout", "Timeout wating for program update response.");
            resp.addContent(fe);
            return resp;
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "UserNotAuthorized", "The user is not authorized to stop scenario.");
            resp.addContent(fe);
            return resp;
        } catch (BadServerResponseException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStopRequest, e, "ServerCommunicationError", "The communication with the server has failed.");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
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
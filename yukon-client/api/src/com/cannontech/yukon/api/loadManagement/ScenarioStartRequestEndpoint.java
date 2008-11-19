package com.cannontech.yukon.api.loadManagement;

import java.util.Date;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ScenarioStartRequestEndpoint {

    private LoadControlService loadControlService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String scenarioNameExpressionStr = "/y:scenarioStartRequest/y:scenarioName";
    private String startTimeExpressionStr = "/y:scenarioStartRequest/y:startDateTime";
    private String stopTimeExpressionStr = "/y:scenarioStartRequest/y:stopDateTime";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="scenarioStartRequest")
    public Element invoke(Element scenarioStartRequest) throws Exception {
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(scenarioStartRequest);
        
        String scenarioName = requestTemplate.evaluateAsString(scenarioNameExpressionStr);
        Date startTime = requestTemplate.evaluateAsDate(startTimeExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopTimeExpressionStr);

        // run service
        Element resp = new Element("scenarioStartResponse", ns);
        
        try {
            loadControlService.startControlByScenarioName(scenarioName, startTime, stopTime, false, true);
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStartRequest, e, "InvalidScenarioName", "No scenario named: " + scenarioName);
            resp.addContent(fe);
            return resp;
        } catch (TimeoutException e) {
            Element fe = XMLFailureGenerator.generateFailure(scenarioStartRequest, e, "Timeout", "Timeout wating for program update response.");
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
}

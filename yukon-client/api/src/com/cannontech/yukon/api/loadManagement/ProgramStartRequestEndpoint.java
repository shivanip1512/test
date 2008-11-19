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
public class ProgramStartRequestEndpoint {

    private LoadControlService loadControlService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String programNameExpressionStr = "/y:programStartRequest/y:programName";
    private String startTimeExpressionStr = "/y:programStartRequest/y:startDateTime";
    private String stopTimeExpressionStr = "/y:programStartRequest/y:stopDateTime";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programStartRequest")
    public Element invoke(Element programStartRequest) throws Exception {
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(programStartRequest);
        
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);
        Date startTime = requestTemplate.evaluateAsDate(startTimeExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopTimeExpressionStr);

        // run service
        Element resp = new Element("programStartResponse", ns);
        
        try {
            loadControlService.startControlByProgramName(programName, startTime, stopTime, false, true);
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStartRequest, e, "InvalidProgramName", "No program named: " + programName);
            resp.addContent(fe);
            return resp;
        } catch (TimeoutException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStartRequest, e, "Timeout", "Timeout wating for program update response.");
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

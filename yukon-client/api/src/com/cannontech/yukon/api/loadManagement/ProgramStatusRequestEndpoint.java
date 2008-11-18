package com.cannontech.yukon.api.loadManagement;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.core.dao.NotFoundException;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ProgramStatusRequestEndpoint {

    private LoadControlService loadControlService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String programNameExpressionStr = "/y:programStatusRequest/y:programName";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programStatusRequest")
    public Element invoke(Element programStatusRequest) throws Exception {
        
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(programStatusRequest);
        
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);

        // run service
        Element resp = new Element("programStatusResponse", ns);
        
        ProgramStatus programStatus = null;
        try {
            programStatus = loadControlService.getProgramStatusByProgramName(programName);
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStatusRequest, e, "InvalidProgramName", "No program named \"" + programName + "\" found.");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        Element programStatusElement = new Element("programStatus", ns);
        
        programStatusElement.addContent(XmlUtils.createStringElement("programName", ns, programStatus.getProgramName()));
        if (programStatus.isActive()) {
            programStatusElement.addContent(XmlUtils.createStringElement("currentStatus", ns, "Active"));
        } else {
            programStatusElement.addContent(XmlUtils.createStringElement("currentStatus", ns, "Inactive"));
        }
        programStatusElement.addContent(XmlUtils.createDateElement("startDateTime", ns, programStatus.getStartTime()));
        if (programStatus.getStopTime() != null) {
            programStatusElement.addContent(XmlUtils.createDateElement("stopDateTime", ns, programStatus.getStopTime()));
        }
        programStatusElement.addContent(XmlUtils.createStringElement("gearName", ns, programStatus.getGearName()));
        
        resp.addContent(programStatusElement);
        
        return resp;
    }
    
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
}

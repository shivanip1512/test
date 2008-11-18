package com.cannontech.yukon.api.loadManagement;

import java.util.List;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class CurrentlyActiveProgramsRequestEndpoint {

    private LoadControlService loadControlService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="currentlyActiveProgramsRequest")
    public Element invoke(Element currentlyActiveProgramsRequest) throws Exception {
        
        // run service
        Element resp = new Element("currentlyActiveProgramsResponse", ns);
        
        List<ProgramStatus> allCurrentlyActivePrograms = loadControlService.getAllCurrentlyActivePrograms();
        
        // build response
        Element programStatuses = new Element("programStatuses", ns);
        
        for (ProgramStatus programStatus : allCurrentlyActivePrograms) {
            
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
            
            programStatuses.addContent(programStatusElement);
        }
        
        resp.addContent(programStatuses);
        
        return resp;
    }
    
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
}

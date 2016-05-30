package com.cannontech.yukon.api.loadManagement.endpoint;

import javax.annotation.PostConstruct;

import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
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
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.message.util.BadServerResponseException;
import com.cannontech.message.util.ConnectionException;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ProgramStatusRequestEndpoint {

    private LoadControlService loadControlService;
    @Autowired private RolePropertyDao rolePropertyDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String programNameExpressionStr = "/y:programStatusRequest/y:programName";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programStatusRequest")
    public Element invoke(Element programStatusRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(programStatusRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(programStatusRequest);
        
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);

        // init response
        Element resp = new Element("programStatusResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        try {
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
   
            ProgramStatus programStatus = loadControlService.getProgramStatusByProgramName(programName, user);
            
            // build response
            Element programStatusElement = new Element("programStatus", ns);
            
            programStatusElement.addContent(XmlUtils.createStringElement("programName", ns, programStatus.getProgramName()));
            
            programStatusElement.addContent(XmlUtils.createStringElement("currentStatus", ns, getStatus(programStatus)));
            
            programStatusElement.addContent(XmlUtils.createDateElement("startDateTime", ns, programStatus.getStartTime()));
            if (programStatus.getStopTime() != null) {
                programStatusElement.addContent(XmlUtils.createDateElement("stopDateTime", ns, programStatus.getStopTime()));
            }
            programStatusElement.addContent(XmlUtils.createStringElement("gearName", ns, programStatus.getGearName()));
            
            resp.addContent(programStatusElement);
        
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStatusRequest, e, "InvalidProgramName", "No program named: "+ programName);
            resp.addContent(fe);
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStatusRequest, e, "UserNotAuthorized", "The user is not authorized to request program status.");
            resp.addContent(fe);
        } catch (BadServerResponseException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStatusRequest, e, "ServerCommunicationError", e.getMessage());
            resp.addContent(fe);
        } catch (ConnectionException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStatusRequest, e, "ServerCommunicationError", e.getMessage());
            resp.addContent(fe);
        }
        return resp;       
    }    
    
    private String getStatus(ProgramStatus programStatus){
        String status = "";
        if (programStatus.isActive()) {
            status = "Active";
        } else if(programStatus.isScheduled()){
            status = "Scheduled";
        } else if(programStatus.isInactive()){
            status = "Inactive";
        } 
        return status;
    }
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
    
}
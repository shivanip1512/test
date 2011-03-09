package com.cannontech.yukon.api.loadManagement.endpoint;

import javax.annotation.PostConstruct;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramStatus;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlApiUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ProgramStatusRequestEndpoint {

    private LoadControlService loadControlService;
    private RolePropertyDao rolePropertyDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String programNameExpressionStr = "/y:programStatusRequest/y:programName";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programStatusRequest")
    public Element invoke(Element programStatusRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(programStatusRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlApiUtils.getXPathTemplateForElement(programStatusRequest);
        
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);

        // init response
        Element resp = new Element("programStatusResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        ProgramStatus programStatus = null;
        try {
        
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
   
            programStatus = loadControlService.getProgramStatusByProgramName(programName, user);
        
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStatusRequest, e, "InvalidProgramName", "No program named: "+ programName);
            resp.addContent(fe);
            return resp;
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStatusRequest, e, "UserNotAuthorized", "The user is not authorized to request program status.");
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
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
}
package com.cannontech.yukon.api.loadManagement;

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
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.message.util.TimeoutException;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ProgramStopRequestEndpoint {

    private LoadControlService loadControlService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String programNameExpressionStr = "/y:programStopRequest/y:programName";
    private String stopTimeExpressionStr = "/y:programStopRequest/y:stopDateTime";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programStopRequest")
    public Element invoke(Element programStopRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(programStopRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(programStopRequest);
        
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopTimeExpressionStr);

        // init response
        Element resp = new Element("programStopResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        try {
            loadControlService.stopControlByProgramName(programName, stopTime, false, true, user);
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "InvalidProgramName", "No program named: " + programName);
            resp.addContent(fe);
            return resp;
        } catch (TimeoutException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "Timeout", "Timeout wating for program update response.");
            resp.addContent(fe);
            return resp;
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(programStopRequest, e, "UserNotAuthorized", "The program is not visible to the user.");
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

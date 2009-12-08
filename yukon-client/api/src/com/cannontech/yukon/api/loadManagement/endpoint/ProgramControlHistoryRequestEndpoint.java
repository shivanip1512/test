package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.LoadControlService;
import com.cannontech.loadcontrol.service.data.ProgramControlHistory;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class ProgramControlHistoryRequestEndpoint {

	private LoadControlService loadControlService;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    private String programNameExpressionStr = "/y:programControlHistoryRequest/y:programName";
    private String startTimeExpressionStr = "/y:programControlHistoryRequest/y:startDateTime";
    private String stopTimeExpressionStr = "/y:programControlHistoryRequest/y:stopDateTime";
    
	@PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="programControlHistoryRequest")
    public Element invoke(Element programControlHistoryRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(programControlHistoryRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
    	
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(programControlHistoryRequest);
        
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);
        Date startTime = requestTemplate.evaluateAsDate(startTimeExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopTimeExpressionStr);

        // init response
        Element resp = new Element("programControlHistoryResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        List<ProgramControlHistory> programControlHistory = new ArrayList<ProgramControlHistory>();
        try {
        	
        	if (StringUtils.isBlank(programName)) {
        		programControlHistory = loadControlService.getAllControlHistory(startTime, stopTime, user);
        	} else {
        		programControlHistory = loadControlService.getControlHistoryByProgramName(programName, startTime, stopTime, user);
        	}
        	
        } catch (NotFoundException e) {
            Element fe = XMLFailureGenerator.generateFailure(programControlHistoryRequest, e, "InvalidProgramName", "No program named: " + programName);
            resp.addContent(fe);
            return resp;
        } catch (NotAuthorizedException e) {
            Element fe = XMLFailureGenerator.generateFailure(programControlHistoryRequest, e, "UserNotAuthorized", e.getMessage());
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        Element controlHistoryEntries = new Element("controlHistoryEntries", ns);
        
        for (ProgramControlHistory hist : programControlHistory) {
        	
        	Element programControlHistoryElement = new Element("programControlHistory", ns);
        	programControlHistoryElement.addContent(XmlUtils.createStringElement("programName", ns, hist.getProgramName()));
        	programControlHistoryElement.addContent(XmlUtils.createDateElement("startDateTime", ns, hist.getStartDateTime()));
            if (hist.getStopDateTime() != null) {
            	programControlHistoryElement.addContent(XmlUtils.createDateElement("stopDateTime", ns, hist.getStopDateTime()));
            }
            programControlHistoryElement.addContent(XmlUtils.createStringElement("gearName", ns, hist.getGearName()));
            
            controlHistoryEntries.addContent(programControlHistoryElement);
        }
        
        resp.addContent(controlHistoryEntries);
        
        return resp;
    }
    
    
    @Autowired
    public void setLoadControlService(LoadControlService loadControlService) {
        this.loadControlService = loadControlService;
    }
}

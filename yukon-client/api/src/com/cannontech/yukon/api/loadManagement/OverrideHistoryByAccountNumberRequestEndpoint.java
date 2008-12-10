package com.cannontech.yukon.api.loadManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.loadcontrol.service.OverrideService;
import com.cannontech.loadcontrol.service.data.OverrideHistory;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class OverrideHistoryByAccountNumberRequestEndpoint {

	private OverrideService overrideService;
	
    private Namespace ns = YukonXml.getYukonNamespace();
    private String accountNumberExpressionStr = "/y:overrideHistoryByAccountNumberRequest/y:accountNumber";
    private String startDateTimeExpressionStr = "/y:overrideHistoryByAccountNumberRequest/y:startDateTime";
    private String stopDateTimeExpressionStr = "/y:overrideHistoryByAccountNumberRequest/y:stopDateTime";
    private String programNameExpressionStr = "/y:overrideHistoryByAccountNumberRequest/y:programName";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="overrideHistoryByAccountNumberRequest")
    public Element invoke(Element overrideHistoryByAccountNumberRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(overrideHistoryByAccountNumberRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(overrideHistoryByAccountNumberRequest);
        
        XmlUtils.printElement(overrideHistoryByAccountNumberRequest, "++++++++");
        
        String accountNumber = requestTemplate.evaluateAsString(accountNumberExpressionStr);
        Date startTime = requestTemplate.evaluateAsDate(startDateTimeExpressionStr);
        Date stopTime = requestTemplate.evaluateAsDate(stopDateTimeExpressionStr);
        String programName = requestTemplate.evaluateAsString(programNameExpressionStr);
        Long longEl = requestTemplate.evaluateAsLong("/y:overrideHistoryByAccountNumberRequest/y:SHIT");
        
        // init response
        Element resp = new Element("overrideHistoryByAccountNumberResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0");
        resp.setAttribute(versionAttribute);
        
        // run service
        //TODO decide what exception(s) this might throw, add error code details
        List<OverrideHistory> overrideHistoryList = new ArrayList<OverrideHistory>();
        try {
        	overrideHistoryList = overrideService.overrideHistoryByAccountNumber(accountNumber, programName, startTime, stopTime, user);
        } catch (Exception e) {
        	
        	Element fe = XMLFailureGenerator.generateFailure(overrideHistoryByAccountNumberRequest, e, "ERROR_CODE", "ERROR_DESCRIPTION");
            resp.addContent(fe);
            return resp;
        }
        
        // build response
        Element overrideHistoryEntries = new Element("overrideHistoryEntries", ns);
        
        for (OverrideHistory overrideHistory : overrideHistoryList) {
            
            Element overrideHistoryElement = new Element("overrideHistory", ns);
        
            overrideHistoryElement.addContent(XmlUtils.createStringElement("serialNumber", ns, overrideHistory.getSerialNumber()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("programName", ns, overrideHistory.getProgramName()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("accountNumber", ns, overrideHistory.getAccountNumber()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("status", ns, overrideHistory.getStatus().getDescription()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("scheduledDateTime", ns, overrideHistory.getScheduledDate()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("startDateTime", ns, overrideHistory.getStartDate()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("stopDateTime", ns, overrideHistory.getStopDate()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("userName", ns, overrideHistory.getUserName()));
            overrideHistoryElement.addContent(XmlUtils.createLongElement("overrideNumber", ns, overrideHistory.getOverrideNumber()));
            overrideHistoryElement.addContent(XmlUtils.createBooleanElement("countedAgainstLimit", ns, overrideHistory.isCountedAgainstLimit()));
        
            overrideHistoryEntries.addContent(overrideHistoryElement);
        }
        
        resp.addContent(overrideHistoryEntries);
        
        return resp;
    }
    
    @Autowired
    public void setOverrideService(OverrideService overrideService) {
		this.overrideService = overrideService;
	}
}

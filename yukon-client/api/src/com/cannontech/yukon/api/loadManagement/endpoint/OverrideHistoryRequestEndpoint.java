package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Date;
import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.ProgramNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class OverrideHistoryRequestEndpoint {

	private OptOutService optOutService;
	private RolePropertyDao rolePropertyDao;
	
    private Namespace ns = YukonXml.getYukonNamespace();

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="overrideHistoryByAccountNumberRequest")
    public Element invokeHistoryByAccount(Element overrideHistoryByAccountNumberRequest, LiteYukonUser user) throws Exception {
        
        //Verify Request message version
    	XmlVersionUtils.verifyYukonMessageVersion(overrideHistoryByAccountNumberRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        // create template and parse data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(overrideHistoryByAccountNumberRequest);
        
        String accountNumber = 
        	template.evaluateAsString("/y:overrideHistoryByAccountNumberRequest/y:accountNumber");
        String programName = 
        	template.evaluateAsString("/y:overrideHistoryByAccountNumberRequest/y:programName");
        Date startTime = 
        	template.evaluateAsDate("/y:overrideHistoryByAccountNumberRequest/y:startDateTime");
        Date stopTime = 
        	template.evaluateAsDate("/y:overrideHistoryByAccountNumberRequest/y:stopDateTime");
        
        // init response
        Element resp = new Element("overrideHistoryByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        Element resultElement;
        List<OverrideHistory> overrideHistoryList;
        try {
            // Check authorization
        	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
        	
            overrideHistoryList = optOutService.getOptOutHistoryForAccount(accountNumber,
                                                                           startTime,
                                                                           stopTime,
                                                                           user,
                                                                           programName);
            resultElement = buildHistoryEntriesElement_programName(overrideHistoryList);
        } catch (NotAuthorizedException e) {
            resultElement = XMLFailureGenerator.generateFailure(overrideHistoryByAccountNumberRequest,
                                                                e,
                                                                "UserNotAuthorized",
                                                                "The user is not authorized to request override history.");
        } catch (AccountNotFoundException e) {
            resultElement = XMLFailureGenerator.generateFailure(overrideHistoryByAccountNumberRequest,
                                                                e,
                                                                "InvalidAccountNumber",
                                                                "No account with account number: " + accountNumber);
        } catch (ProgramNotFoundException e) {
        	resultElement = XMLFailureGenerator.generateFailure(overrideHistoryByAccountNumberRequest,
        			e,
        			"InvalidProgramName",
        			"No program with program name: " + programName);
        }

        // build response
        resp.addContent(resultElement);
        return resp;
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="overrideHistoryByProgramNameRequest")
    public Element invokeHistoryByProgram(Element overrideHistoryByProgramNameRequest, LiteYukonUser user) throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(overrideHistoryByProgramNameRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0, XmlVersionUtils.YUKON_MSG_VERSION_1_1);
        String version = XmlVersionUtils.getYukonMessageVersion(overrideHistoryByProgramNameRequest);
        
        
        // create template and parse data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(overrideHistoryByProgramNameRequest);

        String programName = template.evaluateAsString("/y:overrideHistoryByProgramNameRequest/y:programName");        
        Date startTime = template.evaluateAsDate("/y:overrideHistoryByProgramNameRequest/y:startDateTime");
        Date stopTime = template.evaluateAsDate("/y:overrideHistoryByProgramNameRequest/y:stopDateTime");
        
        // init response
        Element resp = new Element("overrideHistoryByProgramNameResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, version);
        
        // run service
        Element resultElement;
        List<OverrideHistory> overrideHistoryList;
        try {
            // Check authorization
        	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
        	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
        	
            overrideHistoryList = optOutService.getOptOutHistoryByProgram(programName,
                                                                          startTime,
                                                                          stopTime,
                                                                          user);
            
            if (XmlVersionUtils.YUKON_MSG_VERSION_1_0.equals(version)) {
            	resultElement = buildHistoryEntriesElement_programName(overrideHistoryList);
            } else if (XmlVersionUtils.YUKON_MSG_VERSION_1_1.equals(version)) {
            	resultElement = buildHistoryEntriesElement_programList(overrideHistoryList);
            } else {
            	throw new IllegalArgumentException("Invalid request version: " + version);
            }
            
        } catch (NotAuthorizedException e) {
            resultElement = XMLFailureGenerator.generateFailure(overrideHistoryByProgramNameRequest,
                                                                e,
                                                                "UserNotAuthorized",
                                                                "The user is not authorized to view override history.");

        } catch (ProgramNotFoundException e) {
            resultElement = XMLFailureGenerator.generateFailure(overrideHistoryByProgramNameRequest,
                                                                e,
                                                                "InvalidProgramName",
                                                                "No program with name: " + programName);
        }

        // build response
        resp.addContent(resultElement);
        return resp;
    }    
    
    // builds overrideHistoryEntries element
    private Element buildHistoryEntriesElement_programList(List<OverrideHistory> overrideHistoryList) {
        
        // build overrideHistoryEntries element content
        Element overrideHistoryEntries = new Element("overrideHistoryEntries", ns);
        for (OverrideHistory overrideHistory : overrideHistoryList) {
            Element overrideHistoryElement = new Element("overrideHistory", ns);
            overrideHistoryElement.addContent(XmlUtils.createStringElement("serialNumber", ns, overrideHistory.getSerialNumber()));
            
            Element enrolledProgramList = new Element("enrolledProgramList", ns);
            for (Program program : overrideHistory.getPrograms()) {
                enrolledProgramList.addContent(XmlUtils.createStringElement("programName", ns, program.getProgramPaoName()));
            }
            overrideHistoryElement.addContent(enrolledProgramList);
            
            overrideHistoryElement.addContent(XmlUtils.createStringElement("accountNumber", ns, overrideHistory.getAccountNumber()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("status", ns, overrideHistory.getStatus().name()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("scheduledDateTime", ns, overrideHistory.getScheduledDate()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("startDateTime", ns, overrideHistory.getStartDate()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("stopDateTime", ns, overrideHistory.getStopDate()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("userName", ns, overrideHistory.getUserName()));
            overrideHistoryElement.addContent(XmlUtils.createLongElement("overrideNumber", ns, overrideHistory.getOverrideNumber()));
            overrideHistoryElement.addContent(XmlUtils.createBooleanElement("countedAgainstLimit", ns, overrideHistory.isCountedAgainstLimit()));
            overrideHistoryEntries.addContent(overrideHistoryElement);
        }
        return overrideHistoryEntries;
    }
    
    // builds overrideHistoryEntries element
    private Element buildHistoryEntriesElement_programName(List<OverrideHistory> overrideHistoryList) {
        
        // build overrideHistoryEntries element content
        Element overrideHistoryEntries = new Element("overrideHistoryEntries", ns);
        for (OverrideHistory overrideHistory : overrideHistoryList) {
            Element overrideHistoryElement = new Element("overrideHistory", ns);
            overrideHistoryElement.addContent(XmlUtils.createStringElement("serialNumber", ns, overrideHistory.getSerialNumber()));
            
            Program program = null;
            List<Program> programs = overrideHistory.getPrograms();
            if (programs.size() > 0) {
            	program = programs.get(0);
            }
            
            overrideHistoryElement.addContent(XmlUtils.createStringElement("programName", ns, program == null ? "" : program.getProgramName()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("accountNumber", ns, overrideHistory.getAccountNumber()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("status", ns, overrideHistory.getStatus().name()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("scheduledDateTime", ns, overrideHistory.getScheduledDate()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("startDateTime", ns, overrideHistory.getStartDate()));
            overrideHistoryElement.addContent(XmlUtils.createDateElement("stopDateTime", ns, overrideHistory.getStopDate()));
            overrideHistoryElement.addContent(XmlUtils.createStringElement("userName", ns, overrideHistory.getUserName()));
            overrideHistoryElement.addContent(XmlUtils.createLongElement("overrideNumber", ns, overrideHistory.getOverrideNumber()));
            overrideHistoryElement.addContent(XmlUtils.createBooleanElement("countedAgainstLimit", ns, overrideHistory.isCountedAgainstLimit()));
            overrideHistoryEntries.addContent(overrideHistoryElement);
        }
        return overrideHistoryEntries;
    }
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
    @Autowired
    public void setRolePropertyDao(RolePropertyDao rolePropertyDao) {
		this.rolePropertyDao = rolePropertyDao;
	}
    
}
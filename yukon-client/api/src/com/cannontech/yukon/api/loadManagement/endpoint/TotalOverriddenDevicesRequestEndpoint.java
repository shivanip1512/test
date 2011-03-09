package com.cannontech.yukon.api.loadManagement.endpoint;

import java.util.Date;

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
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class TotalOverriddenDevicesRequestEndpoint {

    private OptOutService optOutService;
    private RolePropertyDao rolePropertyDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();

    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="totalOverriddenDevicesByAccountNumberRequest")
    public Element invokeDevicesByAccount(
    		Element totalOverriddenDevicesByAccountNumberRequest, LiteYukonUser user) 
    	throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(
        		totalOverriddenDevicesByAccountNumberRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(
        		totalOverriddenDevicesByAccountNumberRequest);
        
        String accountNumber = template.evaluateAsString(
        		"/y:totalOverriddenDevicesByAccountNumberRequest/y:accountNumber");
        String programName = template.evaluateAsString(
        	"/y:totalOverriddenDevicesByAccountNumberRequest/y:programName");
        Date startTime = template.evaluateAsDate(
        		"/y:totalOverriddenDevicesByAccountNumberRequest/y:startDateTime");
        Date stopTime = template.evaluateAsDate(
        		"/y:totalOverriddenDevicesByAccountNumberRequest/y:stopDateTime");
        
        // init response
        Element resp = new Element("totalOverriddenDevicesByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        Element resultElement;
        try {
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
            
            int totalDevices = optOutService.getOptOutDeviceCountForAccount(accountNumber,
                                                                            startTime,
                                                                            stopTime,
                                                                            user,
                                                                            programName);
            resultElement = XmlUtils.createLongElement("totalDevices",
                                                       ns,
                                                       totalDevices);
        } catch (NotAuthorizedException e) {
            resultElement = XMLFailureGenerator.generateFailure(totalOverriddenDevicesByAccountNumberRequest,
                                                                e,
                                                                "UserNotAuthorized",
                                                                "The user is not authorized to request total devices overriden.");
        } catch (AccountNotFoundException e) {
            resultElement = XMLFailureGenerator.generateFailure(totalOverriddenDevicesByAccountNumberRequest,
                                                                e,
                                                                "InvalidAccountNumber",
                                                                "No account with account number: " + accountNumber);
        } catch (ProgramNotFoundException e) {
            resultElement = XMLFailureGenerator.generateFailure(totalOverriddenDevicesByAccountNumberRequest,
                    e,
                    "InvalidProgramName",
                    "No program with name: " + programName);
        }
        
        // return response
        resp.addContent(resultElement);
        return resp;
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="totalOverriddenDevicesByProgramNameRequest")
    public Element invokeDevicesByProgram(
    		Element totalOverriddenDevicesByProgramNameRequest, LiteYukonUser user) 
    	throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(
        		totalOverriddenDevicesByProgramNameRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(
        		totalOverriddenDevicesByProgramNameRequest);
        
        String programName = template.evaluateAsString(
        		"/y:totalOverriddenDevicesByProgramNameRequest/y:programName");
        Date startTime = template.evaluateAsDate(
        		"/y:totalOverriddenDevicesByProgramNameRequest/y:startDateTime");
        Date stopTime = template.evaluateAsDate(
        		"/y:totalOverriddenDevicesByProgramNameRequest/y:stopDateTime");
        
        // init response
        Element resp = new Element("totalOverriddenDevicesByProgramNameResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        Element resultElement;
        try {
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
            
            int totalDevices = optOutService.getOptOutDeviceCountForProgram(programName,
                                                                            startTime,
                                                                            stopTime,
                                                                            user);
            resultElement = XmlUtils.createLongElement("totalDevices",
                                                       ns,
                                                       totalDevices);
        } catch (NotAuthorizedException e) {
            resultElement = XMLFailureGenerator.generateFailure(totalOverriddenDevicesByProgramNameRequest,
                                                                e,
                                                                "UserNotAuthorized",
                                                                "The user is not authorized to request total devices overriden.");
        } catch (ProgramNotFoundException e) {
            resultElement = XMLFailureGenerator.generateFailure(totalOverriddenDevicesByProgramNameRequest,
                                                                e,
                                                                "InvalidProgramName",
                                                                "No program with name: " + programName);
        }
        
        // build response
        resp.addContent(resultElement);
        return resp;
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
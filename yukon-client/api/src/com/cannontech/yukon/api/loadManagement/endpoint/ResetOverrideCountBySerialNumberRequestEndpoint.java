package com.cannontech.yukon.api.loadManagement.endpoint;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class ResetOverrideCountBySerialNumberRequestEndpoint {

    private AccountEventLogService accountEventLogService;
	private OptOutService optOutService;
    private Namespace ns = YukonXml.getYukonNamespace();
	private RolePropertyDao rolePropertyDao;
	
	private String accountNumberExpressionStr = "/y:resetOverrideCountBySerialNumberRequest/y:accountNumber";
	private String serialNumberExpressionStr = "/y:resetOverrideCountBySerialNumberRequest/y:serialNumber";
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="resetOverrideCountBySerialNumberRequest")
    public Element invoke(Element resetOverrideCountBySerialNumberRequest, LiteYukonUser user) throws Exception {
        
    	XmlVersionUtils.verifyYukonMessageVersion(resetOverrideCountBySerialNumberRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
    	
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(resetOverrideCountBySerialNumberRequest);
        String accountNumber = requestTemplate.evaluateAsString(accountNumberExpressionStr);
        String serialNumber = requestTemplate.evaluateAsString(serialNumberExpressionStr);
        
    	// init response
        Element resp = new Element("resetOverrideCountBySerialNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        Element resultElement;
        try {
            
            // Log opt out limit reset attempt
            accountEventLogService.optOutLimitResetAttemptedThroughApi(user, 
                                                                       accountNumber, 
                                                                       serialNumber);
            
            // Check authorization
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
        	
        	optOutService.resetOptOutLimitForInventory(accountNumber, serialNumber, user);
            
            resultElement = XmlUtils.createStringElement("success", ns, "");
            
		} catch (NotAuthorizedException e) {
			resultElement = XMLFailureGenerator.generateFailure(resetOverrideCountBySerialNumberRequest, e, "UserNotAuthorized", "The user is not authorized to reset overrides by serial number.");
		} catch (AccountNotFoundException e) {
			resultElement = XMLFailureGenerator.generateFailure(resetOverrideCountBySerialNumberRequest, e, "InvalidAccountNumber", "No account with account number: " + accountNumber);
		} catch (InventoryNotFoundException e) {
			resultElement = XMLFailureGenerator.generateFailure(resetOverrideCountBySerialNumberRequest, e, "InvalidSerialNumber", "No inventory with serial number: " + serialNumber);
		} catch (IllegalArgumentException e) {
			resultElement = XMLFailureGenerator .generateFailure(resetOverrideCountBySerialNumberRequest, e, "InvalidSerialNumber", "The inventory with serial number: " + serialNumber 
																																	+ " is not associated with the account with account number: "
																																	+ accountNumber);
		}

        // build response
        resp.addContent(resultElement);
        return resp;
    }
    
    @Autowired
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
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
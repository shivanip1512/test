package com.cannontech.yukon.api.loadManagement.endpoint;

import org.jdom2.Element;
import org.jdom2.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
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
public class DecrementOverrideLimitRequestEndpoint {

    private AccountEventLogService accountEventLogService;
    private OptOutService optOutService;
    private RolePropertyDao rolePropertyDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace = "http://yukon.cannontech.com/api", localPart = "decrementDeviceOverrideLimitRequest")
    public Element invoke(Element decrementDeviceOverrideLimitRequest,
            LiteYukonUser user) throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(decrementDeviceOverrideLimitRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = YukonXml.getXPathTemplateForElement(decrementDeviceOverrideLimitRequest);
        
        String accountNumber = template.evaluateAsString("/y:decrementDeviceOverrideLimitRequest/y:accountNumber");
        String serialNumber = template.evaluateAsString("/y:decrementDeviceOverrideLimitRequest/y:serialNumber");
        
        // init response
        Element resp = new Element("decrementDeviceOverrideLimitResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // run service
        Element resultElement;
        try {
            accountEventLogService.optOutLimitReductionAttempted(user, accountNumber, serialNumber, EventSource.API);
            
            // Check authorization
        	rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_PROGRAMS_OPT_OUT, user);
            rolePropertyDao.verifyProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_WS_LM_DATA_ACCESS, user);

        	optOutService.allowAdditionalOptOuts(accountNumber, serialNumber, 1, user);
            resultElement = XmlUtils.createStringElement("success", ns, "");
            
        } catch (NotAuthorizedException e) {
            resultElement = XMLFailureGenerator.generateFailure(decrementDeviceOverrideLimitRequest,
                                                                e,
                                                                "UserNotAuthorized",
                                                                "The user is not authorized to change override limit for device.");
        } catch (AccountNotFoundException e) {
            resultElement = XMLFailureGenerator.generateFailure(decrementDeviceOverrideLimitRequest,
                                                                e,
                                                                "InvalidAccountNumber",
                                                                "No account with account number: " + accountNumber);
        } catch (InventoryNotFoundException e) {
            resultElement = XMLFailureGenerator.generateFailure(decrementDeviceOverrideLimitRequest,
                                                                e,
                                                                "InvalidSerialNumber",
                                                                "No inventory with serial number: " + serialNumber);
        } catch (IllegalArgumentException e) {
        	resultElement = 
        		XMLFailureGenerator.generateFailure(decrementDeviceOverrideLimitRequest,
        			e,
        			"InvalidSerialNumber",
        			"The inventory with serial number: " + serialNumber + 
        			" is not associated with the account with account number: " + accountNumber);
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
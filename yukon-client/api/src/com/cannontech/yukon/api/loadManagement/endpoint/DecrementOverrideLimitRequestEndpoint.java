package com.cannontech.yukon.api.loadManagement.endpoint;

import javax.annotation.PostConstruct;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.exception.NotAuthorizedException;
import com.cannontech.core.dao.AccountNotFoundException;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.InventoryNotFoundException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.dr.optout.service.OptOutService;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class DecrementOverrideLimitRequestEndpoint {

    private OptOutService optOutService;
    private AuthDao authDao;
    
    private Namespace ns = YukonXml.getYukonNamespace();

	
    private static String accountNumberStr = "/y:decrementDeviceOverrideLimitRequest/y:accountNumber";
    private static String serialNumberStr = "/y:decrementDeviceOverrideLimitRequest/y:serialNumber";
    
    @PostConstruct
    public void initialize() throws JDOMException {
    }
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="decrementDeviceOverrideLimitRequest")
    public Element invokeDecrementCountToLimitBySerialNumberAndAccount(
    		Element decrementDeviceOverrideLimitRequest, LiteYukonUser user) throws Exception {
        
        //Verify Request message version
        XmlVersionUtils.verifyYukonMessageVersion(decrementDeviceOverrideLimitRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // create template and parse data
        SimpleXPathTemplate template = XmlUtils.getXPathTemplateForElement(decrementDeviceOverrideLimitRequest);
        
        String accountNumber = template.evaluateAsString(accountNumberStr);
        String serialNumber = template.evaluateAsString(serialNumberStr);
        
        // init response
        Element resp = new Element("totalOverriddenDevicesByAccountNumberResponse", ns);
        XmlVersionUtils.addVersionAttribute(resp, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        // Check authorization
        try {
        	authDao.verifyTrueProperty(user, ConsumerInfoRole.CONSUMER_INFO_PROGRAMS_OPT_OUT);
        } catch (NotAuthorizedException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			decrementDeviceOverrideLimitRequest, 
        			e, 
        			"UserNotAuthorized", 
        			"The user is not authorized to change override limit for device.");
        	resp.addContent(fe);
        	return resp;
        }
        
        // run service
        try {
        	optOutService.allowAdditionalOptOuts(accountNumber, serialNumber, 1, user);
        } catch (AccountNotFoundException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			decrementDeviceOverrideLimitRequest, 
        			e, 
        			"InvalidAccountNumber", 
        			"No account with account number: " + accountNumber);
        	resp.addContent(fe);
        	return resp;
        }  catch (InventoryNotFoundException e) {
        	Element fe = XMLFailureGenerator.generateFailure(
        			decrementDeviceOverrideLimitRequest, 
        			e, 
        			"InvalidSerialNumber", 
        			"No inventory with serial number: " + serialNumber);
        	resp.addContent(fe);
        	return resp;
        }
        
        // build response
        resp.addContent(XmlUtils.createStringElement("success", ns, ""));
        
        return resp;
    }
    
    @Autowired
    public void setOptOutService(OptOutService optOutService) {
		this.optOutService = optOutService;
	}
    
    @Autowired
    public void setAuthDao(AuthDao authDao) {
		this.authDao = authDao;
	}
    
}


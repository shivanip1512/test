package com.cannontech.yukon.api.account.endpoint;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlApiUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class RemoveAccountsRequestEndpoint {

    private AccountEventLogService accountEventLogService;
    private AccountService accountService;
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="removeAccountsRequest")
    public Element invoke(Element removeAccountsRequest, LiteYukonUser user) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(removeAccountsRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        SimpleXPathTemplate requestTemplate = XmlApiUtils.getXPathTemplateForElement(removeAccountsRequest);
        
        List<String> accountNumbers = requestTemplate.evaluate("//y:accountsList/y:accountNumber", 
                              new NodeToElementMapperWrapper<String>(new RemoveAccountsRequestMapper()));
        
        Element removeAccountsResponse = new Element("removeAccountsResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0"); 
        removeAccountsResponse.setAttribute(versionAttribute); 
        
        Element removeAccountsResultList = new Element("accountResultList", ns);
        removeAccountsResponse.addContent(removeAccountsResultList);
        
        for (String accountNumber : accountNumbers) {
            accountEventLogService.accountDeletionAttemptedThroughApi(user, accountNumber);
            
            Element removeAccountResult = addAccountResponse(ns, removeAccountsResultList, accountNumber);
            try {
                accountService.deleteAccount(accountNumber, user);
            } catch(InvalidAccountNumberException e) {
                Element fe = XMLFailureGenerator.generateFailure(removeAccountsRequest, e, "InvalidAccountNumber", e.getMessage());
                removeAccountResult.addContent(fe);
                continue;
            }
    
            removeAccountResult.addContent(new Element("success", ns));
        }
        
        return removeAccountsResponse;
        
    }
    
    private Element addAccountResponse(Namespace ns, Element removeAccountsResultList, String accountNumber){
        Element customerAccountResult = new Element("accountResult", ns);
        removeAccountsResultList.addContent(customerAccountResult);
        customerAccountResult.addContent(XmlUtils.createStringElement("accountNumber", ns, accountNumber));
        return customerAccountResult;
    }
    
	private class RemoveAccountsRequestMapper implements ObjectMapper<Element, String> {
	
	    @Override
	    public String map(Element removeAccountsRequestElement) throws ObjectMappingException {
	        SimpleXPathTemplate template = XmlApiUtils.getXPathTemplateForElement(removeAccountsRequestElement);
	        
	        String accountNumber = template.evaluateAsString("//y:accountNumber");
	        return accountNumber;
	    }
	}

	@Autowired
	public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
	
	@Autowired
	public void setAccountService(AccountService accountService) {
		this.accountService = accountService;
	}
}

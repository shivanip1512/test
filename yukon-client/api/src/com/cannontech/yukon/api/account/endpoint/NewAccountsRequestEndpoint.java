package com.cannontech.yukon.api.account.endpoint;

import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.exception.InvalidLoginGroupException;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.account.exception.InvalidSubstationNameException;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.XmlVersionUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class NewAccountsRequestEndpoint {

    private AccountEventLogService accountEventLogService;
    private AccountService accountService;
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="newAccountsRequest")
    public Element invoke(Element newAccountsRequest, LiteYukonUser user) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(newAccountsRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(newAccountsRequest);
        
        List<UpdatableAccount> customerAccounts = requestTemplate.evaluate("//y:accountsList/y:customerAccount", 
                              new NodeToElementMapperWrapper<UpdatableAccount>(new AccountsRequestMapper()));
        
        Element newAccountsResponse = new Element("newAccountsResponse", ns);
        Attribute versionAttribute = new Attribute("version", "1.0"); 
        newAccountsResponse.setAttribute(versionAttribute); 
        
        Element newAccountsResultList = new Element("accountResultList", ns);
        newAccountsResponse.addContent(newAccountsResultList);
        
        for (UpdatableAccount account : customerAccounts) {
            accountEventLogService.accountCreationAttemptedThroughApi(user,account.getAccountNumber());
            
            Element newAccountResult = addAccountResponse(ns, newAccountsResultList, account);
            try {
                accountService.addAccount(account, user);
                newAccountResult.addContent(new Element("success", ns));
            } catch(InvalidAccountNumberException e) {
                Element fe = XMLFailureGenerator.generateFailure(newAccountsRequest, e, "InvalidAccountNumber", e.getMessage());
                newAccountResult.addContent(fe);
            } catch(AccountNumberUnavailableException e) {
                Element fe = XMLFailureGenerator.generateFailure(newAccountsRequest, e, "AccountNumberUnavailable", e.getMessage());
                newAccountResult.addContent(fe);
            } catch(UserNameUnavailableException e) {
                Element fe = XMLFailureGenerator.generateFailure(newAccountsRequest, e, "UserNameUnavailable", e.getMessage());
                newAccountResult.addContent(fe);
            } catch(InvalidLoginGroupException e) {
                Element fe = XMLFailureGenerator.generateFailure(newAccountsRequest, e, "InvalidLoginGroup", e.getMessage());
                newAccountResult.addContent(fe);
            } catch(InvalidSubstationNameException e) {
                Element fe = XMLFailureGenerator.generateFailure(newAccountsRequest, e, "InvalidSubstationName", e.getMessage());
                newAccountResult.addContent(fe);
            }
    
        }
        
        return newAccountsResponse;
        
    }
    
    private Element addAccountResponse(Namespace ns, Element newAccountsResultList, UpdatableAccount account){
        Element customerAccountResult = new Element("accountResult", ns);
        customerAccountResult.addContent(XmlUtils.createStringElement("accountNumber", ns, account.getAccountNumber()));
        newAccountsResultList.addContent(customerAccountResult);
        return customerAccountResult;
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
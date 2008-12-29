package com.cannontech.yukon.api.account.endpoint;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.UserNameUnavailableException;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class NewAccountsRequestEndpoint {

    private AccountService accountService;
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="newAccountsRequest")
    public Element invoke(Element newAccountsRequest, LiteYukonUser user) throws Exception {
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(newAccountsRequest);
        
        List<UpdatableAccount> customerAccounts = requestTemplate.evaluate("//y:accountsList/y:customerAccount", 
                              new NodeToElementMapperWrapper<UpdatableAccount>(new AccountsRequestMapper()));
        
        Element newAccountsResponse = new Element("newAccountsResponse", ns);
        Element newAccountsResultList = new Element("accountResultList", ns);
        newAccountsResponse.addContent(newAccountsResultList);
        
        for (UpdatableAccount account : customerAccounts) {
            Element newAccountResult = addAccountResponse(ns, newAccountsResultList, account);
            try {
                accountService.addAccount(account, user);
                newAccountResult.addContent(new Element("success", ns));
            } catch(AccountNumberUnavailableException e) {
                Element fe = XMLFailureGenerator.generateFailure(newAccountsRequest, e, "AccountNumberUnavailable", e.getMessage());
                newAccountResult.addContent(fe);
            } catch(UserNameUnavailableException e) {
                Element fe = XMLFailureGenerator.generateFailure(newAccountsRequest, e, "UserNameUnavailable", e.getMessage());
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
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
}
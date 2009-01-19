package com.cannontech.yukon.api.account.endpoint;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.bulk.field.impl.UpdatableAccount;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.exception.InvalidSubstationNameException;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.account.service.AccountServiceHelper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.SimpleXPathTemplate;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlUtils;
import com.cannontech.yukon.api.util.YukonXml;

@Endpoint
public class UpdateAccountsRequestEndpoint {

    private AccountService accountService;
    private AccountServiceHelper accountServiceHelper;
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="updateAccountsRequest")
    public Element invoke(Element updateAccountsRequest, LiteYukonUser user) throws Exception {
        SimpleXPathTemplate requestTemplate = XmlUtils.getXPathTemplateForElement(updateAccountsRequest);
        
        List<UpdatableAccount> customerAccounts = requestTemplate.evaluate("//y:accountsList/y:customerAccount", 
                              new NodeToElementMapperWrapper<UpdatableAccount>(new AccountsRequestMapper()));
        
        Element updateAccountsResponse = new Element("updateAccountsResponse", ns);
        Element updateAccountsResultList = new Element("accountResultList", ns);
        updateAccountsResponse.addContent(updateAccountsResultList);
        
        for (UpdatableAccount account : customerAccounts) {
            Element updateAccountResult = addAccountResponse(ns, updateAccountsResultList, account);
            try {
                UpdatableAccount filledAccount = accountServiceHelper.buildFullDto(account, user);
                accountService.updateAccount(filledAccount, user);
                updateAccountResult.addContent(new Element("success", ns));
            } catch(InvalidAccountNumberException e) {
                Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, e, "InvalidAccountNumber", e.getMessage());
                updateAccountResult.addContent(fe);
            } catch(InvalidSubstationNameException e) {
                Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, e, "InvalidSubstationNamer", e.getMessage());
                updateAccountResult.addContent(fe);
            }
    
        }
        
        return updateAccountsResponse;
        
    }
    
    private Element addAccountResponse(Namespace ns, Element updateAccountsResultList, UpdatableAccount account){
        Element customerAccountResult = new Element("accountResult", ns);
        updateAccountsResultList.addContent(customerAccountResult);
        customerAccountResult.addContent(XmlUtils.createStringElement("accountNumber", ns, account.getAccountNumber()));
        return customerAccountResult;
    }
    
    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @Autowired
    public void setAccountServiceHelper(AccountServiceHelper accountServiceHelper) {
        this.accountServiceHelper = accountServiceHelper;
    }
}
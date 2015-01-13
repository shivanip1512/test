package com.cannontech.yukon.api.account.endpoint;

import java.util.List;

import org.jdom.Element;
import org.jdom.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.util.xml.SimpleXPathTemplate;
import com.cannontech.common.util.xml.XmlUtils;
import com.cannontech.common.util.xml.YukonXml;
import com.cannontech.core.dao.UserNameUnavailableException;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.exception.AccountNumberUnavailableException;
import com.cannontech.stars.dr.account.exception.InvalidAccountNumberException;
import com.cannontech.stars.dr.account.exception.InvalidAddressException;
import com.cannontech.stars.dr.account.exception.InvalidLoginGroupException;
import com.cannontech.stars.dr.account.exception.InvalidSubstationNameException;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountService;
import com.cannontech.stars.dr.account.service.AccountServiceHelper;
import com.cannontech.yukon.api.util.NodeToElementMapperWrapper;
import com.cannontech.yukon.api.util.XMLFailureGenerator;
import com.cannontech.yukon.api.util.XmlVersionUtils;

@Endpoint
public class UpdateAccountsRequestEndpoint {

    private AccountEventLogService accountEventLogService;
    private AccountService accountService;
    private AccountServiceHelper accountServiceHelper;
    private Namespace ns = YukonXml.getYukonNamespace();
    
    @PayloadRoot(namespace="http://yukon.cannontech.com/api", localPart="updateAccountsRequest")
    public Element invoke(Element updateAccountsRequest, LiteYukonUser user) throws Exception {
        XmlVersionUtils.verifyYukonMessageVersion(updateAccountsRequest, XmlVersionUtils.YUKON_MSG_VERSION_1_0,XmlVersionUtils.YUKON_MSG_VERSION_1_1);
        SimpleXPathTemplate requestTemplate = YukonXml.getXPathTemplateForElement(updateAccountsRequest);
        
        Boolean add = requestTemplate.evaluateAsBoolean("//y:updateAccountsRequest/@addOnFail", true);
        
        List<UpdatableAccount> customerAccounts = requestTemplate.evaluate("//y:accountsList/y:customerAccount", 
                              new NodeToElementMapperWrapper<UpdatableAccount>(new AccountsRequestMapper()));
        
        Element updateAccountsResponse = new Element("updateAccountsResponse", ns);
        XmlVersionUtils.addVersionAttribute(updateAccountsResponse, XmlVersionUtils.YUKON_MSG_VERSION_1_0);
        
        Element updateAccountsResultList = new Element("accountResultList", ns);
        updateAccountsResponse.addContent(updateAccountsResultList);
        
        for (UpdatableAccount account : customerAccounts) {
            accountEventLogService.accountUpdateAttempted(user,account.getAccountNumber(), EventSource.API);
            
            Element updateAccountResult = addAccountResponse(ns, updateAccountsResultList, account);
            try {
                UpdatableAccount filledAccount = accountServiceHelper.buildFullDto(account, user);
                accountService.updateAccount(filledAccount, user);
                updateAccountResult.addContent(new Element("success", ns));
            } catch(InvalidAccountNumberException e) {
                if(add) {
                    accountEventLogService.accountUpdateCreationAttempted(user,account.getAccountNumber(), EventSource.API);
                    
                    // Update didn't work, try to add it.
                    try {
                        accountService.addAccount(account, user);
                        updateAccountResult.addContent(new Element("success", ns));
                    } catch(InvalidAccountNumberException ex) {
                        Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, ex, "InvalidAccountNumber", ex.getMessage());
                        updateAccountResult.addContent(fe);
                    } catch(AccountNumberUnavailableException ex) {
                        Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, ex, "AccountNumberUnavailable", ex.getMessage());
                        updateAccountResult.addContent(fe);
                    } catch(UserNameUnavailableException ex) {
                        Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, ex, "UserNameUnavailable", ex.getMessage());
                        updateAccountResult.addContent(fe);
                    } catch(InvalidLoginGroupException ex) {
                        Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, ex, "InvalidLoginGroup", ex.getMessage());
                        updateAccountResult.addContent(fe);
                    } catch(InvalidSubstationNameException ex) {
                        Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, ex, "InvalidSubstationName", ex.getMessage());
                        updateAccountResult.addContent(fe);
                    }catch(InvalidAddressException ex) {
                        Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, ex, "InvalidAddress", ex.getMessage());
                        updateAccountResult.addContent(fe);
                    } 
                } else {
                    Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, e, "InvalidAccountNumber", e.getMessage());
                    updateAccountResult.addContent(fe);
                }
            } catch(InvalidSubstationNameException e) {
                Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, e, "InvalidSubstationName", e.getMessage());
                updateAccountResult.addContent(fe);
            } catch(UserNameUnavailableException e) {
                Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, e, "UserNameUnavailable", e.getMessage());
                updateAccountResult.addContent(fe);
            }catch(InvalidAddressException ex) {
                Element fe = XMLFailureGenerator.generateFailure(updateAccountsRequest, ex, "InvalidAddress", ex.getMessage());
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
    public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
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
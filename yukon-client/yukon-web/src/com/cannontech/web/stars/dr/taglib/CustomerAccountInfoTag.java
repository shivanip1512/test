package com.cannontech.web.stars.dr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.ecs.html.Div;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.util.PhoneNumber;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("customerAccountInfoTagPrototype")
public class CustomerAccountInfoTag extends YukonTagSupport {
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private CustomerAccountDao customerAccountDao;
    private CustomerDao customerDao;
    private ContactDao contactDao;
    private AccountSiteDao accountSiteDao;
    private AddressDao addressDao;
    
    private String accountNumber;
    private boolean isAccountNumberSet;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!isAccountNumberSet) throw new JspException("accountNumber is not set.");
        
        MessageSourceAccessor messageSource = messageSourceResolver.getMessageSourceAccessor(getUserContext());
        CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber);
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
        LiteAddress address = addressDao.getByAddressId(accountSite.getStreetAddressId());
        LiteCustomer customer = customerDao.getLiteCustomer(account.getCustomerId());
        LiteContact contact = contactDao.getContact(customer.getPrimaryContactID());
        
        Div mainDiv = new Div();
        
        Div accountNumberDiv = new Div();
        String accountNumberLabel = messageSource.getMessage("yukon.dr.consumer.accountNumberLabel");
        accountNumberDiv.addElement(accountNumberLabel + account.getAccountNumber());
        mainDiv.addElement(accountNumberDiv);
        
        Div fullNameDiv = new Div();
        fullNameDiv.addElement(contact.getContFirstName() + " " + contact.getContLastName());
        mainDiv.addElement(fullNameDiv);

        Div companyNameDiv = getCompanyNameDiv(account.getCustomerId());
        if (companyNameDiv != null) mainDiv.addElement(companyNameDiv); 
        
        Div addressDiv = new Div();
        addressDiv.addElement(address.toHTMLString());
        mainDiv.addElement(addressDiv);
        
        Div phoneNumberDiv = getPhoneNumberDiv(contact);
        if (phoneNumberDiv != null) mainDiv.addElement(phoneNumberDiv);
        
        String output = mainDiv.toString();
        
        JspWriter out = getJspContext().getOut();
        out.write(output);
    }
    
    private Div getCompanyNameDiv(int customerId) {
        LiteCICustomer customer = customerDao.getLiteCICustomer(customerId);
        if (customer == null) return null;
        
        String companyName = customer.getCompanyName();
        
        Div companyNameDiv = new Div();
        companyNameDiv.addElement(companyName);
        return companyNameDiv;
    }
    
    private Div getPhoneNumberDiv(LiteContact contact) {
        LiteContactNotification contactNotification = 
            contactDao.getContactNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
        if (contactNotification == null) return null;
        
        String phoneNumber = PhoneNumber.format(contactNotification.getNotification());
        
        Div phoneNumberDiv = new Div();
        phoneNumberDiv.addElement(phoneNumber);
        return phoneNumberDiv;
    }
    
    public void setAccountNumber(final String accountNumber) {
        isAccountNumberSet = true;
        this.accountNumber = accountNumber;
    }
    
    public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }

    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
    }

    public void setContactDao(ContactDao contactDao) {
        this.contactDao = contactDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void setAccountSiteDao(AccountSiteDao accountSiteDao) {
        this.accountSiteDao = accountSiteDao;
    }

    public void setAddressDao(AddressDao addressDao) {
        this.addressDao = addressDao;
    }
    
}

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
import com.cannontech.core.service.PhoneNumberFormattingService;
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
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("customerAccountInfoTagPrototype")
public class CustomerAccountInfoTag extends YukonTagSupport {
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private CustomerAccountDao customerAccountDao;
    private CustomerDao customerDao;
    private ContactDao contactDao;
    private AccountSiteDao accountSiteDao;
    private AddressDao addressDao;
    private PhoneNumberFormattingService phoneNumberFormattingService;
    
    private String accountNumber;
    private boolean isAccountNumberSet = false;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (!isAccountNumberSet) throw new JspException("accountNumber is not set.");
        
        MessageSourceAccessor messageSource = messageSourceResolver.getMessageSourceAccessor(getUserContext());
        CustomerAccount account = customerAccountDao.getByAccountNumber(accountNumber);
        AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
        LiteAddress address = addressDao.getByAddressId(accountSite.getStreetAddressId());
        LiteCustomer customer = customerDao.getLiteCustomer(account.getCustomerId());
        LiteContact contact = contactDao.getContact(customer.getPrimaryContactID());
        
        final Div mainDiv = new Div();
        mainDiv.addAttribute("class", "customerAccount");
        
        addAccountNumberDiv(mainDiv, account, messageSource);
        
        addNameDiv(mainDiv, contact);

        addCompanyNameDiv(mainDiv, account.getCustomerId());
        
        addAddressDiv(mainDiv, address);
        
        addPhoneNumberDiv(mainDiv, contact, getUserContext());
        
        String output = mainDiv.toString();
        
        JspWriter out = getJspContext().getOut();
        out.write(output);
    }
    
    private void addAccountNumberDiv(final Div mainDiv, final CustomerAccount account, 
            final MessageSourceAccessor messageSource) {
        Div accountNumberDiv = new Div();
        accountNumberDiv.addAttribute("class", "accountNumber");
        String accountNumberLabel = messageSource.getMessage("yukon.dr.consumer.accountNumberLabel", account.getAccountNumber());
        accountNumberDiv.addElement(accountNumberLabel);
        mainDiv.addElement(accountNumberDiv);    
    }
    
    private void addNameDiv(final Div mainDiv, final LiteContact contact) {
        Div fullNameDiv = new Div();
        fullNameDiv.addElement(contact.getContFirstName() + " " + contact.getContLastName());
        mainDiv.addElement(fullNameDiv);    
    }
    
    private void addAddressDiv(final Div mainDiv, final LiteAddress address) {
        Div addressDiv = new Div();
        String formattedAddressString = address.toFormattedString();
        formattedAddressString = formattedAddressString.replaceAll("\\n", "<br>");
        addressDiv.addElement(formattedAddressString);
        mainDiv.addElement(addressDiv);    
    }
    
    private void addCompanyNameDiv(final Div mainDiv, final int customerId) {
        LiteCICustomer customer = customerDao.getLiteCICustomer(customerId);
        if (customer == null) return;

        Div companyNameDiv = new Div();
        companyNameDiv.addElement(customer.getCompanyName());
        mainDiv.addElement(companyNameDiv);
    }
    
    private void addPhoneNumberDiv(final Div mainDiv, final LiteContact contact, 
            final YukonUserContext yukonUserContext) {
        
        LiteContactNotification contactNotification = 
            contactDao.getContactNotification(contact, YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE);
        if (contactNotification == null) return;
        
        String unFormattedPhoneNumber = contactNotification.getNotification();
        String phoneNumber = phoneNumberFormattingService.formatPhoneNumber(unFormattedPhoneNumber, yukonUserContext);
        
        Div phoneNumberDiv = new Div();
        phoneNumberDiv.addElement(phoneNumber);
        mainDiv.addElement(phoneNumberDiv);
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
    
    public void setPhoneNumberFormattingService(
            PhoneNumberFormattingService phoneNumberFormattingService) {
        this.phoneNumberFormattingService = phoneNumberFormattingService;
    }
    
}

package com.cannontech.web.stars.dr.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ecs.html.Div;
import org.springframework.beans.factory.annotation.Configurable;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.i18n.MessageSourceAccessor;
import com.cannontech.core.dao.AddressDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.core.service.PhoneNumberFormattingService;
import com.cannontech.customer.model.CustomerInformation;
import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.i18n.YukonUserContextMessageSourceResolver;
import com.cannontech.stars.dr.account.dao.AccountSiteDao;
import com.cannontech.stars.dr.account.model.AccountSite;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.user.YukonUserContext;
import com.cannontech.web.taglib.YukonTagSupport;

@Configurable("customerAccountInfoTagPrototype")
public class CustomerAccountInfoTag extends YukonTagSupport {
    private YukonUserContextMessageSourceResolver messageSourceResolver;
    private CustomerDao customerDao;
    private AccountSiteDao accountSiteDao;
    private AddressDao addressDao;
    private PhoneNumberFormattingService phoneNumberFormattingService;
    
    private CustomerAccount account = null;
    
    @Override
    public void doTag() throws JspException, IOException {
        if (account == null) throw new JspException("accountNumber is not set.");
        
        MessageSourceAccessor messageSource;
		LiteAddress address;
		CustomerInformation customer;
		LiteCustomer liteCustomer;
		try {
			messageSource = messageSourceResolver.getMessageSourceAccessor(getUserContext());
			AccountSite accountSite = accountSiteDao.getByAccountSiteId(account.getAccountSiteId());
			address = addressDao.getByAddressId(accountSite.getStreetAddressId());
			customer = customerDao.getCustomerInformation(account.getCustomerId());
			liteCustomer = customerDao.getLiteCustomer(customer.getCustomerId());
		} catch (Exception e) {
			CTILogger.warn("Couldn't get customer info", e);
			throw new JspException(e);
		}

        final Div mainDiv = new Div();
        mainDiv.addAttribute("class", "customerAccount");
        
        addAccountNumberDiv(mainDiv, account, messageSource);

        // extra info div
        // note: when adding additional parameters the Operator side should be updated to match. See "accountInfoFragmenet.jsp"
        String extraInfo = messageSource.getMessage("yukon.web.modules.operator.accountInfoFragment.extraInfo", liteCustomer.getAltTrackingNumber());
        if (StringUtils.isNotBlank(extraInfo)) {
        	addExtraInfoDiv(mainDiv, extraInfo);
        }
        
        addNameDiv(mainDiv, customer);

        addCompanyNameDiv(mainDiv, customer);

        addAddressDiv(mainDiv, address);

        addPhoneNumberDiv(mainDiv, customer, getUserContext());

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
    
    private void addExtraInfoDiv(final Div mainDiv, final String extraInfo) {
        Div altTrackingDiv = new Div();
        altTrackingDiv.addElement(extraInfo);
        mainDiv.addElement(altTrackingDiv);    
    }
    
    private void addNameDiv(final Div mainDiv, final CustomerInformation customer) {
        Div fullNameDiv = new Div();
        String first = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(customer.getContactFirstName()));
        String last = StringEscapeUtils.escapeJavaScript(StringEscapeUtils.escapeHtml(customer.getContactLastName()));
        fullNameDiv.addElement(first + " " + last);
        mainDiv.addElement(fullNameDiv);    
    }
    
    private void addAddressDiv(final Div mainDiv, final LiteAddress address) {
        Div addressDiv = new Div();
        String formattedAddressString = address.toFormattedString();
        formattedAddressString = formattedAddressString.replaceAll("\\n", "<br>");
        addressDiv.addElement(formattedAddressString);
        mainDiv.addElement(addressDiv);    
    }

    private void addCompanyNameDiv(final Div mainDiv, CustomerInformation customer) {
        if (customer.getCompanyName() == null)
			return;

        Div companyNameDiv = new Div();
        companyNameDiv.addElement(customer.getCompanyName());
        mainDiv.addElement(companyNameDiv);
    }
    
    private void addPhoneNumberDiv(final Div mainDiv, final CustomerInformation customerInformation, 
            final YukonUserContext yukonUserContext) {
        
    	String unFormattedPhoneNumber = customerInformation.getContactHomePhone();
        if (unFormattedPhoneNumber == null) return;
        
        String phoneNumber = phoneNumberFormattingService.formatPhoneNumber(unFormattedPhoneNumber, yukonUserContext);
        
        Div phoneNumberDiv = new Div();
        phoneNumberDiv.addElement(phoneNumber);
        mainDiv.addElement(phoneNumberDiv);
    }
    
    public void setAccount(CustomerAccount account) {
		this.account = account;
	}
    
    public CustomerAccount getAccount() {
		return account;
	}
    
    public void setMessageSourceResolver(
            YukonUserContextMessageSourceResolver messageSourceResolver) {
        this.messageSourceResolver = messageSourceResolver;
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

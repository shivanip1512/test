package com.cannontech.web.stars.dr.consumer.contactus;

import com.cannontech.stars.dr.account.model.CustomerAccount;

public interface ContactUsDao {

    public ContactUs getContactUsByCustomerAccount(CustomerAccount customerAccount);
    
}

package com.cannontech.web.stars.dr.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;

public class AbstractConsumerController {
    @Autowired private CustomerAccountDao customerAccountDao;

    @ModelAttribute("customerAccount")
    public CustomerAccount getCustomerAccount(LiteYukonUser user) {
        return customerAccountDao.getCustomerAccount(user);
    }
}

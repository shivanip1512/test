package com.cannontech.stars.dr.account.service;

import junit.framework.Assert;

import org.junit.Test;

import com.cannontech.stars.dr.account.model.AccountDto;
import com.cannontech.stars.dr.account.model.UpdatableAccount;
import com.cannontech.stars.dr.account.service.AccountServiceHelper;

public class AccountServiceHelperTest {

    private AccountServiceHelper helper = new AccountServiceHelper();
    
    @Test
    public void testCopyNonNullValues() {
        UpdatableAccount from = new UpdatableAccount();
        AccountDto fromDto = new AccountDto();
        AccountDto to = new AccountDto();
        
        from.setAccountDto(fromDto);

        // should change
        from.getAccountDto().setFirstName("Tester");
        from.getAccountDto().getStreetAddress().setCityName("Slack Jawed Yokel Township");
        from.getAccountDto().getStreetAddress().setCounty(null);
        from.getAccountDto().getSiteInfo().setSubstationName("Flux Capacitor");
        
        // shouldn't change
        to.setLastName("McTesty");
        to.getStreetAddress().setCounty("Cletus County");
        to.getSiteInfo().setServiceVoltage("1.21 Gigawatts");
        
        helper.copyNonNullValues(from, to);
        
        Assert.assertEquals("First Name was not copied properly.", "Tester", to.getFirstName());
        Assert.assertEquals("First Name was not copied properly.", "McTesty", to.getLastName());
        Assert.assertEquals("Street Address: City was not copied properly.", "Slack Jawed Yokel Township", to.getStreetAddress().getCityName());
        Assert.assertEquals("Street Address: County was not copied properly.", "Cletus County", to.getStreetAddress().getCounty());
        Assert.assertEquals("SiteInformation: Substation name was not copied properly.", "Flux Capacitor", to.getSiteInfo().getSubstationName());
        Assert.assertEquals("SiteInformation: Service Voltage was not copied properly.", "1.21 Gigawatts", to.getSiteInfo().getServiceVoltage());
        
        
    }

}

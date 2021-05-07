package com.cannontech.stars.dr.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

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
        
        assertEquals("Tester", to.getFirstName(), "First Name was not copied properly.");
        assertEquals("McTesty", to.getLastName(), "First Name was not copied properly.");
        assertEquals("Slack Jawed Yokel Township", to.getStreetAddress().getCityName(), "Street Address: City was not copied properly.");
        assertEquals("Cletus County", to.getStreetAddress().getCounty(), "Street Address: County was not copied properly.");
        assertEquals("Flux Capacitor", to.getSiteInfo().getSubstationName(), "SiteInformation: Substation name was not copied properly.");
        assertEquals("1.21 Gigawatts", to.getSiteInfo().getServiceVoltage(), "SiteInformation: Service Voltage was not copied properly.");
    }

}

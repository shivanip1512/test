package com.cannontech.stars.dr.account;

import com.cannontech.stars.dr.account.model.AccountDto;

public class AccountDtoMockFactory {
    public static AccountDto getAccountDto1() {
        AccountDto accountDto1 = new AccountDto();
        
        accountDto1.setAccountNumber("999999");
        accountDto1.setFirstName("Bob");
        accountDto1.setLastName("Vila");
        accountDto1.setEmailAddress("bob@aol.com");
        accountDto1.setHomePhone("763 867 5309");
        accountDto1.setWorkPhone("1 800 GHOSTBUSTERS");
        accountDto1.setIsCommercial(false);
        accountDto1.setUserGroup("Residential Customer");
        accountDto1.setMapNumber("123456");
        accountDto1.setUserName("bob");
        accountDto1.setPassword("itsasecrettoeveryone");
        accountDto1.setAltTrackingNumber("123456");
        accountDto1.setAccountNotes("Account Notes");
        accountDto1.setCompanyName("");
        accountDto1.setStreetAddress(AddressMockFactory.getAddress1());
        accountDto1.setBillingAddress(AddressMockFactory.getAddress2());
        accountDto1.setSiteInfo(SiteInfoMockFactory.getSiteInfo1());

        return accountDto1;
    }
}
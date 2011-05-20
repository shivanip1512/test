package com.cannontech.stars.dr.account.model;


public class UpdatableAccount {

    private String accountNumber;
    private AccountDto accountDto;
    private String accountSiteNotes = null;
    private String customerNotes = null;
    
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public AccountDto getAccountDto() {
        return accountDto;
    }
    public void setAccountDto(AccountDto accountDto) {
        this.accountDto = accountDto;
    }
    public String getAccountSiteNotes() {
        return accountSiteNotes;
    }
    public void setAccountSiteNotes(String accountSiteNotes) {
        this.accountSiteNotes = accountSiteNotes;
    }
    public String getCustomerNotes() {
        return customerNotes;
    }
    public void setCustomerNotes(String customerNotes) {
        this.customerNotes = customerNotes;
    }
    
    
}

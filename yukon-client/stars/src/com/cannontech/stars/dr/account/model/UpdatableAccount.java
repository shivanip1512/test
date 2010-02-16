package com.cannontech.stars.dr.account.model;


public class UpdatableAccount {

    private String accountNumber;
    private AccountDto accountDto;
    
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
}

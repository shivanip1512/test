package com.cannontech.stars.dr.account.exception;

import com.cannontech.stars.util.StarsClientRequestException;

public class StarsAccountNotFoundException extends StarsClientRequestException {

    private String accountNumber = "";
    private String energyCompany = "";

    public StarsAccountNotFoundException() {
        super("Account not found");
    }

    public StarsAccountNotFoundException(String message) {
        super(message);
    }

    public StarsAccountNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StarsAccountNotFoundException(String accountNumber,
            String energyCompany) {
        super("Account not found: accountNumber=[" + accountNumber + "], energyCompany=[" + energyCompany + "]");
        this.accountNumber = accountNumber;
        this.energyCompany = energyCompany;
    }

    public StarsAccountNotFoundException(String accountNumber,
            String energyCompany, String message) {
        super(message);
        this.accountNumber = accountNumber;
        this.energyCompany = energyCompany;
    }

    public StarsAccountNotFoundException(String accountNumber,
            String energyCompany, Throwable cause) {
        super("Account not found: accountNumber=[" + accountNumber + "], energyCompany=[" + energyCompany + "]",
              cause);
        this.accountNumber = accountNumber;
        this.energyCompany = energyCompany;
    }

    public StarsAccountNotFoundException(String accountNumber,
            String energyCompany, String message, Throwable cause) {
        super(message, cause);
        this.accountNumber = accountNumber;
        this.energyCompany = energyCompany;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getEnergyCompany() {
        return energyCompany;
    }
}

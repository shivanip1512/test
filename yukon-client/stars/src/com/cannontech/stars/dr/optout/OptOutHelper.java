package com.cannontech.stars.dr.optout;

import java.util.Date;

public class OptOutHelper {
    
    private String accountNumber;
    private String serialNumber;
    private Date startDate;
    private long durationInDays;
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getSerialNumber() {
        return serialNumber;
    }
    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }
    public Date getStartDate() {
        return startDate;
    }
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    public long getDurationInDays() {
        return durationInDays;
    }
    public void setDurationInDays(long durationInDays) {
        this.durationInDays = durationInDays;
    }
    
    

}

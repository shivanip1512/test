package com.cannontech.stars.dr.optout;

import org.joda.time.Instant;
import org.joda.time.Period;

import com.cannontech.stars.dr.optout.model.OptOutCounts;

public class OptOutHelper {
    
    private String accountNumber;
    private String serialNumber;
    private Instant startDate;
    private Period period;
    private OptOutCounts optOutCounts;
    
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
    public Instant getStartDate() {
        return startDate;
    }
    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }
    public OptOutCounts getOptOutCounts() {
        return optOutCounts;
    }
    public void setOptOutCounts(OptOutCounts optOutCounts) {
        this.optOutCounts = optOutCounts;
    }
    public Period getPeriod() {
        return period;
    }
    public void setPeriod(Period period) {
        this.period = period;
    }
    
    
    
}

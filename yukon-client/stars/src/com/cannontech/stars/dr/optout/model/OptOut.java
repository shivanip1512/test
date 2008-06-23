package com.cannontech.stars.dr.optout.model;

import java.util.Date;


public class OptOut {
    private int customerAccountId;
    private Date startDate;
    private Date endDate;
    private int durationInHours;
    
    public OptOut() {
        
    }
    
    public int getCustomerAccountId() {
        return customerAccountId;
    }
    
    public void setCustomerAccountId(int customerAccountId) {
        this.customerAccountId = customerAccountId;
    }
    
    public Date getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
    
    public Date getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    
    public int getDurationInHours() {
        return durationInHours;
    }
    
    public void setDurationInHours(int durationInHours) {
        this.durationInHours = durationInHours;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + customerAccountId;
        result = prime * result + durationInHours;
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
        result = prime * result + ((startDate == null) ? 0
                : startDate.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OptOut other = (OptOut) obj;
        if (customerAccountId != other.customerAccountId)
            return false;
        if (durationInHours != other.durationInHours)
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        return true;
    }

}

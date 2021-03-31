package com.cannontech.web.support;

import com.cannontech.common.util.DateRange;

public class RfSupportBundle {
   
    private String customerName;
  
    private DateRange date = new DateRange();
 
    public String getCustomerName() {
        return customerName;
    }

    public DateRange getDate() {
        return date;
    }

    public void setDate(DateRange date) {
        this.date = date;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

}

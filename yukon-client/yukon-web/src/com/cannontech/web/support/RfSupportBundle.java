package com.cannontech.web.support;

import java.util.Date;

public class RfSupportBundle {
   
    private String customerName;
  
    private Date date = new Date();
 
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String rfCustomerName) {
        this.customerName = rfCustomerName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}

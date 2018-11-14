package com.cannontech.dr.nest.model.v3;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

public class CustomerEnrollments {
    private List<CustomerEnrollment> customerEnrollments = new ArrayList<>();

    public CustomerEnrollments() {
      
    }
    
    @JsonCreator
    public CustomerEnrollments(List<CustomerEnrollment> customerEnrollments) {
        this.customerEnrollments = customerEnrollments;
    }

    public List<CustomerEnrollment> getCustomerEnrollments() {
        return customerEnrollments;
    }

    public void setCustomerEnrollments(List<CustomerEnrollment> customerEnrollments) {
        this.customerEnrollments = customerEnrollments;
    }
    
    public void addEnrollment(CustomerEnrollment customerEnrollment) {
        customerEnrollments.add(customerEnrollment);
    }
}

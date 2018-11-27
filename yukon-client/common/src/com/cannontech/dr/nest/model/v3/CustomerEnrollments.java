package com.cannontech.dr.nest.model.v3;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CustomerEnrollments {
    private List<CustomerEnrollment> customerEnrollments = new ArrayList<>();

    public CustomerEnrollments() {
      
    }
    
    @JsonCreator
    public CustomerEnrollments(@JsonProperty("customerEnrollments") List<CustomerEnrollment> customerEnrollments) {
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

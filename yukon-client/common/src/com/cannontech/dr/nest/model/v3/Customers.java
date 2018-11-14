package com.cannontech.dr.nest.model.v3;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;

public class Customers {
    private List<CustomerInfo> customers = new ArrayList<>();
    private String nextPageToken;
    @JsonCreator
    public Customers(List<CustomerInfo> customers, String nextPageToken) {
        this.customers = customers;
        this.nextPageToken = nextPageToken;
    }
    public List<CustomerInfo> getCustomers() {
        return customers;
    }
    public String getNextPageToken() {
        return nextPageToken;
    }
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}

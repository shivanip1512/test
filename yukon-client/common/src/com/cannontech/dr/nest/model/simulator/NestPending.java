package com.cannontech.dr.nest.model.simulator;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NestPending {
    private String ref;
    private String year;
    private String month;
    private String day;
    private String name;
    private String email;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String accountNumber;
    private String contactApproved;
    private String programs;
    private String nestSerials;
    private String approved;
    private String assignGroup;
    private String group;
    private String rejectionReason;
    private String rejectionNote;
  
    @JsonCreator
    public NestPending(@JsonProperty("REF") String ref, @JsonProperty("YEAR") String year,
            @JsonProperty("MONTH") String month, @JsonProperty("DAY") String day, @JsonProperty("NAME") String name,
            @JsonProperty("EMAIL") String email, @JsonProperty("SERVICE_ADDRESS") String address,
            @JsonProperty("SERVICE_CITY") String city, @JsonProperty("SERVICE_STATE") String state,
            @JsonProperty("SERVICE_POSTAL_CODE") String zipCode, @JsonProperty("ACCOUNT_NUMBER") String accountNumber,
            @JsonProperty("CONTRACT_APPROVED") String contactApproved, @JsonProperty("PROGRAMS") String programs,
            @JsonProperty("NEST_SERIALS") String nestSerials, @JsonProperty("APPROVED") String approved,
            @JsonProperty("ASSIGN_GROUP") String assignGroup, @JsonProperty("GROUP") String group,
            @JsonProperty("REJECTION_REASON") String rejectionReason,
            @JsonProperty("REJECTION_NOTES") String rejectionNote) {
        this.ref = ref;
        this.year = year;
        this.month = month;
        this.day = day;
        this.name = name;
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.accountNumber = accountNumber;
        this.contactApproved = contactApproved;
        this.programs = programs;
        this.nestSerials = nestSerials;
        this.approved = approved;
        this.assignGroup = assignGroup;
        this.group = group;
        this.rejectionReason = rejectionReason;
        this.rejectionNote = rejectionNote;
    }

    @JsonProperty("REF")
    public String getRef() {
        return ref;
    }

    @JsonProperty("NAME")
    public String getName() {
        return name;
    }

    @JsonProperty("EMAIL")
    public String getEmail() {
        return email;
    }

    @JsonProperty("SERVICE_ADDRESS")
    public String getAddress() {
        return address;
    }

    @JsonProperty("SERVICE_CITY")
    public String getCity() {
        return city;
    }

    @JsonProperty("SERVICE_STATE")
    public String getState() {
        return state;
    }

    @JsonProperty("SERVICE_POSTAL_CODE")
    public String getZipCode() {
        return zipCode;
    }

    @JsonProperty("ACCOUNT_NUMBER")
    public String getAccountNumber() {
        return accountNumber;
    }

    @JsonProperty("APPROVED")
    public String getApproved() {
        return approved;
    }

    @JsonProperty("ASSIGN_GROUP")
    public String getAssignGroup() {
        return assignGroup;
    }
    
    @JsonProperty("GROUP")
    public String getGroup() {
        return group;
    }

    @JsonProperty("REJECTION_REASON")
    public String getRejectionReason() {
        return rejectionReason;
    }

    @JsonProperty("REJECTION_NOTES")
    public String getRejectionNote() {
        return rejectionNote;
    }

    @JsonProperty("YEAR")
    public String getYear() {
        return year;
    }

    @JsonProperty("MONTH")
    public String getMonth() {
        return month;
    }

    @JsonProperty("DAY")
    public String getDay() {
        return day;
    }

    @JsonProperty("CONTRACT_APPROVED")
    public String getContactApproved() {
        return contactApproved;
    }

    @JsonProperty("NEST_SERIALS")
    public String getNestSerials() {
        return nestSerials;
    }

    @JsonProperty("PROGRAMS")
    public String getPrograms() {
        return programs;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}

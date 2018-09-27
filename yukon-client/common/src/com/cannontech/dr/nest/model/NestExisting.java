package com.cannontech.dr.nest.model;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class NestExisting {
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
    private String contractApproved;
    private String programs;
    private String winterRhr;
    private String summerRhr;
    private String assignGroup;
    private String group;
    private String dissolve;
    private String dissolveReason;
    private String dissolveNotes;

    @JsonCreator
    public NestExisting(@JsonProperty("REF") String ref, @JsonProperty("YEAR") String year,
            @JsonProperty("MONTH") String month, @JsonProperty("DAY") String day, @JsonProperty("NAME") String name,
            @JsonProperty("EMAIL") String email, @JsonProperty("SERVICE_ADDRESS") String address,
            @JsonProperty("SERVICE_CITY") String city, @JsonProperty("SERVICE_STATE") String state,
            @JsonProperty("SERVICE_POSTAL_CODE") String zipCode, @JsonProperty("ACCOUNT_NUMBER") String accountNumber,
            @JsonProperty("CONTRACT_APPROVED") String contractApproved, @JsonProperty("PROGRAMS") String programs,
            @JsonProperty("WINTER_RHR") String winterRhr, @JsonProperty("SUMMER_RHR") String summerRhr,
            @JsonProperty("ASSIGN_GROUP") String assignGroup, @JsonProperty("GROUP") String group,
            @JsonProperty("DISSOLVE") String dissolve, @JsonProperty("DISSOLVE_REASON") String dissolveReason,
            @JsonProperty("DISSOLVE_NOTES") String dissolveNotes) {
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
        this.contractApproved = contractApproved;
        this.programs = programs;
        this.winterRhr = winterRhr;
        this.summerRhr = summerRhr;
        this.assignGroup = assignGroup;
        this.group = group;
        this.dissolve = dissolve;
        this.dissolveReason = dissolveReason;
        this.dissolveNotes = dissolveNotes;
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


    @JsonProperty("ASSIGN_GROUP")
    public String getAssignGroup() {
        return assignGroup;
    }
    
    public void setAssignGroup(String assignGroup) {
        this.assignGroup = assignGroup;
    }

    @JsonProperty("GROUP")
    public String getGroup() {
        return group;
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
    public String getContractApproved() {
        return contractApproved;
    }

    @JsonProperty("PROGRAMS")
    public String getPrograms() {
        return programs;
    }

    @JsonProperty("WINTER_RHR")
    public String getWinterRhr() {
        return winterRhr;
    }

    @JsonProperty("SUMMER_RHR")
    public String getSummerRhr() {
        return summerRhr;
    }

    @JsonProperty("DISSOLVE")
    public String getDissolve() {
        return dissolve;
    }

    @JsonProperty("DISSOLVE_REASON")
    public String getDissolveReason() {
        return dissolveReason;
    }

    @JsonProperty("DISSOLVE_NOTES")
    public String getDissolveNotes() {
        return dissolveNotes;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
                + System.getProperty("line.separator");
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setDissolve(String dissolve) {
        this.dissolve = dissolve;
    }

    public void setDissolveReason(String dissolveReason) {
        this.dissolveReason = dissolveReason;
    }
}

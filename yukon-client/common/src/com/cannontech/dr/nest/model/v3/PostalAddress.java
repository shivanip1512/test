package com.cannontech.dr.nest.model.v3;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostalAddress {
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    
    @JsonCreator
    public PostalAddress(@JsonProperty("streetAddress") String streetAddress, @JsonProperty("city") String city, 
                         @JsonProperty("state") String state, @JsonProperty("postalCode") String postalCode) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
    }
    public String getStreetAddress() {
        return streetAddress;
    }
    public String getCity() {
        return city;
    }
    public String getState() {
        return state;
    }
    public String getPostalCode() {
        return postalCode;
    }
    
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE)
            + System.getProperty("line.separator");
    }
}

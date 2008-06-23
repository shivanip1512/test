package com.cannontech.stars.xml.serialize;

public abstract class StarsCustomerAddress {
    private int addressId;
    private boolean hasAddressId;
    private String streetAddr1;
    private String streetAddr2;
    private String city;
    private String state;
    private String zip;
    private String county;

    public StarsCustomerAddress() {
        
    }

    public void deleteAddressID() {
        this.hasAddressId= false;
    } 

    public int getAddressID() {
        return this.addressId;
    } 

    public java.lang.String getCity() {
        return this.city;
    } 

    public java.lang.String getCounty() {
        return this.county;
    } 

    public java.lang.String getState() {
        return this.state;
    } 

    public java.lang.String getStreetAddr1() {
        return this.streetAddr1;
    } 

    public java.lang.String getStreetAddr2() {
        return this.streetAddr2;
    } 

    public java.lang.String getZip() {
        return this.zip;
    } 

    public boolean hasAddressID() {
        return this.hasAddressId;
    } 

    public void setAddressID(int addressID) {
        this.addressId = addressID;
        this.hasAddressId = true;
    } 

    public void setCity(java.lang.String city) {
        this.city = city;
    } 

    public void setCounty(java.lang.String county) {
        this.county = county;
    } 

    public void setState(java.lang.String state) {
        this.state = state;
    } 

    public void setStreetAddr1(java.lang.String streetAddr1) {
        this.streetAddr1 = streetAddr1;
    } 

    public void setStreetAddr2(java.lang.String streetAddr2) {
        this.streetAddr2 = streetAddr2;
    } 

    public void setZip(java.lang.String zip) {
        this.zip = zip;
    } 

}

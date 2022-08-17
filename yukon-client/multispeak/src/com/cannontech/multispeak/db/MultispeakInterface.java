package com.cannontech.multispeak.db;

import com.cannontech.multispeak.client.MultiSpeakVersion;

public class MultispeakInterface
{
    public static final String TABLE_NAME = "MSPInterface";
    
    private Integer vendorID = null;
    private String mspInterface = null;
    private String mspEndpoint = null;
    private MultiSpeakVersion version = null;
    private Boolean interfaceEnabled;

    public MultispeakInterface() {

    }
    public MultispeakInterface(Integer vendorID, String mspInterface, String mspEndpoint, MultiSpeakVersion version)
    {
        super();
        this.vendorID = vendorID;
        this.mspInterface = mspInterface;
        this.mspEndpoint = mspEndpoint;
        this.version = version;
    }
    
    public String getMspEndpoint()
    {
        return mspEndpoint;
    }
    public void setMspEndpoint(String mspEndpoint)
    {
        this.mspEndpoint = mspEndpoint;
    }
    public String getMspInterface()
    {
        return mspInterface;
    }
    public void setMspInterface(String mspInterface)
    {
        this.mspInterface = mspInterface;
    }
    public Integer getVendorID()
    {
        return vendorID;
    }
    public void setVendorID(Integer vendorID)
    {
        this.vendorID = vendorID;
    }

    public MultiSpeakVersion getVersion() {
        return version;
    }

    public void setVersion(MultiSpeakVersion version) {
        this.version = version;
    }

    public Boolean getInterfaceEnabled() {
        return interfaceEnabled;
    }

    public void setInterfaceEnabled(Boolean interfaceEnabled) {
        this.interfaceEnabled = interfaceEnabled;
    }
    
}
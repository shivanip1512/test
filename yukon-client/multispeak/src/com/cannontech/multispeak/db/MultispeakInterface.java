package com.cannontech.multispeak.db;

public class MultispeakInterface
{
    public static final String TABLE_NAME = "MSPInterface";
    
    private Integer vendorID = null;
    private String mspInterface = null;
    private String mspEndpoint = null;
    
    public MultispeakInterface(Integer vendorID, String mspInterface, String mspEndpoint)
    {
        super();
        this.vendorID = vendorID;
        this.mspInterface = mspInterface;
        this.mspEndpoint = mspEndpoint;
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
    

}

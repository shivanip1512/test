package com.cannontech.multispeak.db;

import com.cannontech.multispeak.client.MultiSpeakVersion;

public class MultispeakInterface
{
    public static final String TABLE_NAME = "MSPInterface";
    
    private Integer vendorID = null;
    private String mspInterface = null;
    private String mspEndpoint = null;
    private MultiSpeakVersion version = null;
    private String inUserName;
    private String inPassword;
    private String outUserName;
    private String outPassword;
    private Boolean useVendorAuth = true;
    private Boolean validateCertificate;
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
    
    public MultispeakInterface(Integer vendorID, String mspInterface, String mspEndpoint, MultiSpeakVersion version, String inUserName, String inPassword,
            String outUserName, String outPassword, Boolean validateCertificate, Boolean useVendorAuth)
    {
        this(vendorID, mspInterface, mspEndpoint, version);
        this.inUserName = inUserName;
        this.inPassword = inPassword;
        this.outUserName = outUserName;
        this.outPassword = outPassword;
        this.validateCertificate = validateCertificate;
        this.useVendorAuth = useVendorAuth;
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

    public String getInUserName() {
        return inUserName;
    }

    public void setInUserName(String inUserName) {
        this.inUserName = inUserName;
    }

    public String getInPassword() {
        return inPassword;
    }

    public void setInPassword(String inPassword) {
        this.inPassword = inPassword;
    }

    public String getOutUserName() {
        return outUserName;
    }

    public void setOutUserName(String outUserName) {
        this.outUserName = outUserName;
    }

    public String getOutPassword() {
        return outPassword;
    }

    public void setOutPassword(String outPassword) {
        this.outPassword = outPassword;
    }

    public Boolean getUseVendorAuth() {
        return useVendorAuth;
    }

    public void setUseVendorAuth(Boolean useVenderAuthSetting) {
        this.useVendorAuth = useVenderAuthSetting;
    }

    public Boolean getValidateCertificate() {
        return validateCertificate;
    }

    public void setValidateCertificate(Boolean validateCertificate) {
        this.validateCertificate = validateCertificate;
    }
}
package com.cannontech.multispeak.db;

import com.cannontech.multispeak.client.MultiSpeakVersion;

public class MSPEndPointAuth {

    private Integer vendorID = null;
    private String mspInterface = null;
    private MultiSpeakVersion version = null;
    private String inUserName;
    private String inPassword;
    private String outUserName;
    private String outPassword;
    private Boolean useVendorAuth = true;
    private Boolean validateCertificate;

    public Integer getVendorID() {
        return vendorID;
    }
    public void setVendorID(Integer vendorID) {
        this.vendorID = vendorID;
    }
    public String getMspInterface() {
        return mspInterface;
    }
    public void setMspInterface(String mspInterface) {
        this.mspInterface = mspInterface;
    }
    public MultiSpeakVersion getVersion() {
        return version;
    }
    public void setVersion(MultiSpeakVersion version) {
        this.version = version;
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
    public void setUseVendorAuth(Boolean useVendorAuth) {
        this.useVendorAuth = useVendorAuth;
    }
    public Boolean getValidateCertificate() {
        return validateCertificate;
    }
    public void setValidateCertificate(Boolean validateCertificate) {
        this.validateCertificate = validateCertificate;
    }
}

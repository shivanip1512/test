package com.cannontech.web.editor;

import java.util.List;

import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;

public class MultispeakModel {
    /*
     * Fields specific to "Cannon"
     * vendor:mspPrimaryCIS,paoNameAlias,paoNameAliasExtension,paoNameUsesExtension and meterLookupField
     */
    private Integer mspPrimaryCIS;
    private MspPaoNameAliasEnum paoNameAlias;
    private String paoNameAliasExtension;
    private Boolean paoNameUsesExtension;
    private MultispeakMeterLookupFieldEnum meterLookupField;
    
    private String resultMsg;
    private String resultColor;
    private MultispeakVendor mspVendor;
    private List<MultispeakInterface> mspInterfaceList;
    private String service;
    private String endpointURL;

    public Integer getMspPrimaryCIS() {
        return mspPrimaryCIS;
    }

    public void setMspPrimaryCIS(Integer mspPrimaryCIS) {
        this.mspPrimaryCIS = mspPrimaryCIS;
    }

    public MspPaoNameAliasEnum getPaoNameAlias() {
        return paoNameAlias;
    }

    public void setPaoNameAlias(MspPaoNameAliasEnum paoNameAlias) {
        this.paoNameAlias = paoNameAlias;
    }

    public String getPaoNameAliasExtension() {
        return paoNameAliasExtension;
    }

    public void setPaoNameAliasExtension(String paoNameAliasExtension) {
        this.paoNameAliasExtension = paoNameAliasExtension;
    }

    public Boolean getPaoNameUsesExtension() {
        return paoNameUsesExtension;
    }

    public void setPaoNameUsesExtension(Boolean paoNameUsesExtension) {
        this.paoNameUsesExtension = paoNameUsesExtension;
    }

    public MultispeakMeterLookupFieldEnum getMeterLookupField() {
        return meterLookupField;
    }

    public void setMeterLookupField(MultispeakMeterLookupFieldEnum meterLookupField) {
        this.meterLookupField = meterLookupField;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public String getResultColor() {
        return resultColor;
    }

    public void setResultColor(String resultColor) {
        this.resultColor = resultColor;
    }

    public List<MultispeakInterface> getMspInterfaceList() {
        return mspInterfaceList;
    }

    public void setMspInterfaceList(List<MultispeakInterface> mspInterfaceList) {
        this.mspInterfaceList = mspInterfaceList;
    }

    public MultispeakVendor getMspVendor() {
        return mspVendor;
    }

    public void setMspVendor(MultispeakVendor mspVendor) {
        this.mspVendor = mspVendor;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getEndpointURL() {
        return endpointURL;
    }

    public void setEndpointURL(String endpointURL) {
        this.endpointURL = endpointURL;
    }
}

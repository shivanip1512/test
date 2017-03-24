package com.cannontech.web.editor;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.multispeak.client.MultiSpeakVersion;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.db.MultispeakInterface;

public class Multispeak {
    /*
     * private String companyName;
     * private String appName;
     */
    private Integer mspPrimaryCIS;
    private MspPaoNameAliasEnum paoNameAlias;
    private String paoNameAliasExtension;
    private Boolean paoNameUsesExtension;
    private MultispeakMeterLookupFieldEnum meterLookupField;
    private String resultMsg;
    private String resultColor;
    private Map<Pair<String, MultiSpeakVersion>, MultispeakInterface> interfacesMap;
    private List<String> mspInterfaces;
    private List<String> mspEndpoints;
    private MultispeakVendor mspVendor;
    private List<MultispeakInterface> mspInterfaceList;
    private String service;
    private MultiSpeakVersion serviceVersion;

    /*
     * public String getCompanyName() {
     * return companyName;
     * }
     * public void setCompanyName(String companyName) {
     * this.companyName = companyName;
     * }
     * public String getAppName() {
     * return appName;
     * }
     * public void setAppName(String appName) {
     * this.appName = appName;
     * }
     */

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

    public Map<Pair<String, MultiSpeakVersion>, MultispeakInterface> getInterfacesMap() {
        return interfacesMap;
    }

    public void setInterfacesMap(Map<Pair<String, MultiSpeakVersion>, MultispeakInterface> interfacesMap) {
        this.interfacesMap = interfacesMap;
    }

    public List<String> getMspInterfaces() {
        return mspInterfaces;
    }

    public void setMspInterfaces(List<String> mspInterfaces) {
        this.mspInterfaces = mspInterfaces;
    }

    public List<String> getMspEndpoints() {
        return mspEndpoints;
    }

    public void setMspEndpoints(List<String> mspEndpoints) {
        this.mspEndpoints = mspEndpoints;
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

    public MultiSpeakVersion getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(MultiSpeakVersion serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

}

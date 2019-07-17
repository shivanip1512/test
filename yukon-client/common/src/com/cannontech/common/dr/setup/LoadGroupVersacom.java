package com.cannontech.common.dr.setup;

import org.apache.commons.lang.StringUtils;

import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupVersacom;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class LoadGroupVersacom extends LoadGroupBase {
    // Communication Route Id
    private Integer routeId;

    // Addressing
    private Integer utilityAddress ;
    private Integer sectionAddress = 0;
    private Integer classAddress = 0;
    private Integer divisionAddress = 0;
    private String serialAddress = "0";

    // Addressing Usage
    private String addressUsage = StringUtils.EMPTY;
    private String relayUsage = "1";

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public Integer getUtilityAddress() {
        return utilityAddress;
    }

    public void setUtilityAddress(Integer utilityAddress) {
        this.utilityAddress = utilityAddress;
    }

    public Integer getSectionAddress() {
        return sectionAddress;
    }

    public void setSectionAddress(Integer sectionAddress) {
        this.sectionAddress = sectionAddress;
    }

    public Integer getClassAddress() {
        return classAddress;
    }

    public void setClassAddress(Integer classAddress) {
        this.classAddress = classAddress;
    }

    public Integer getDivisionAddress() {
        return divisionAddress;
    }

    public void setDivisionAddress(Integer divisionAddress) {
        this.divisionAddress = divisionAddress;
    }

    public String getAddressUsage() {
        return addressUsage;
    }

    public void setAddressUsage(String addressUsage) {
        this.addressUsage = addressUsage;
    }

    public String getRelayUsage() {
        return relayUsage;
    }

    public void setRelayUsage(String relayUsage) {
        this.relayUsage = relayUsage;
    }

    public String getSerialAddress() {
        return serialAddress;
    }

    public void setSerialAddress(String serialAddress) {
        this.serialAddress = serialAddress;
    }
    
    @Override
    public void buildDBPersistent(LMGroup group) {
        super.buildDBPersistent(group);
        // Set lmGroupVersacom fields
        com.cannontech.database.db.device.lm.LMGroupVersacom lmGroupVersacom =
                           new com.cannontech.database.db.device.lm.LMGroupVersacom();
        lmGroupVersacom.setAddressUsage(getAddressUsage());
        lmGroupVersacom.setClassAddress(getClassAddress());
        lmGroupVersacom.setDivisionAddress(getDivisionAddress());
        lmGroupVersacom.setRelayUsage(getRelayUsage());
        lmGroupVersacom.setRouteID(getRouteId());
        lmGroupVersacom.setSectionAddress(getSectionAddress());
        lmGroupVersacom.setSerialAddress(getSerialAddress());
        lmGroupVersacom.setUtilityAddress(getUtilityAddress());
        lmGroupVersacom.setDeviceID(getId());
        ((LMGroupVersacom) group).setLmGroupVersacom(lmGroupVersacom);
    }
    
    @Override
    public void buildModel(LMGroup loadGroup) {
        super.buildModel(loadGroup);
        LMGroupVersacom lMGroupVersacom = (LMGroupVersacom) loadGroup;
        setRouteId(lMGroupVersacom.getRouteID());
        com.cannontech.database.db.device.lm.LMGroupVersacom lmGroupVersacom = 
                                                lMGroupVersacom.getLmGroupVersacom();
        setUtilityAddress(lmGroupVersacom.getUtilityAddress());
        setSectionAddress(lmGroupVersacom.getSectionAddress());
        setClassAddress(lmGroupVersacom.getClassAddress());
        setDivisionAddress(lmGroupVersacom.getDivisionAddress());
        setSerialAddress(lmGroupVersacom.getSerialAddress());
        setAddressUsage(lmGroupVersacom.getAddressUsage());
        setRelayUsage(lmGroupVersacom.getRelayUsage());
    }

}

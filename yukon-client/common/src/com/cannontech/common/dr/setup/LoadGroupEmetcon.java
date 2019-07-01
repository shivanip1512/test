package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupEmetcon;

public class LoadGroupEmetcon extends LoadGroupBase {
    private Integer goldAddress;
    private Integer silverAddress;
    private Character addressUsage;
    private Character relayUsage;
    private Integer routeID;
    public static final Character addressUsageGold='G';
    public static final Character addressUsageSilver='S';

    public Integer getGoldAddress() {
        return goldAddress;
    }

    public void setGoldAddress(Integer goldAddress) {
        this.goldAddress = goldAddress;
    }

    public Integer getSilverAddress() {
        return silverAddress;
    }

    public void setSilverAddress(Integer silverAddress) {
        this.silverAddress = silverAddress;
    }

    public Character getAddressUsage() {
        return addressUsage;
    }

    public void setAddressUsage(Character addressUsage) {
        this.addressUsage = addressUsage;
    }

    public Character getRelayUsage() {
        return relayUsage;
    }

    public void setRelayUsage(Character relayUsage) {
        this.relayUsage = relayUsage;
    }

    public Integer getRouteID() {
        return routeID;
    }

    public void setRouteID(Integer routeID) {
        this.routeID = routeID;
    }

    @Override
    public void buildModel(LMGroup loadGroup) {
        // Set parent fields
        super.buildModel(loadGroup);

        // Set addressing fields
        setGoldAddress(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getGoldAddress());
        setSilverAddress(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getSilverAddress());
        setAddressUsage(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getAddressUsage());
        setRelayUsage(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getRelayUsage());
        setRouteID(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getRouteID());

    }

    @Override
    public void buildDBPersistent(LMGroup group) {
        // Set parent fields
        super.buildDBPersistent(group);

        // Set LMGroupEmetcon fields.
        com.cannontech.database.db.device.lm.LMGroupEmetcon lmGroupEmetcon =
            ((LMGroupEmetcon) group).getLmGroupEmetcon();
        lmGroupEmetcon.setGoldAddress(getGoldAddress());
        lmGroupEmetcon.setSilverAddress(getSilverAddress());
        lmGroupEmetcon.setAddressUsage(getAddressUsage());
        lmGroupEmetcon.setRelayUsage(getRelayUsage());
        lmGroupEmetcon.setRouteID(getRouteID());

        ((LMGroupEmetcon) group).setLmGroupEmetcon(lmGroupEmetcon);
    }

}

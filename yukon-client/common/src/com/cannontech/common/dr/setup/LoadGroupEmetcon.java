package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupEmetcon;

public class LoadGroupEmetcon extends LoadGroupBase {
    private Integer goldAddress;
    private Integer silverAddress;
    private EmetconAddressUsage addressUsage;
    private EmetconRelayUsage relayUsage;
    private Integer routeId;
    private String routeName;

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

    public EmetconAddressUsage getAddressUsage() {
        return addressUsage;
    }

    public void setAddressUsage(EmetconAddressUsage addressUsage) {
        this.addressUsage = addressUsage;
    }

    public EmetconRelayUsage getRelayUsage() {
        return relayUsage;
    }

    public void setRelayUsage(EmetconRelayUsage relayUsage) {
        this.relayUsage = relayUsage;
    }

    public Integer getRouteId() {
        return routeId;
    }

    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public void buildModel(LMGroup loadGroup) {
        // Set parent fields
        super.buildModel(loadGroup);

        // Set addressing fields
        setGoldAddress(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getGoldAddress());
        setSilverAddress(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getSilverAddress());
        setAddressUsage(EmetconAddressUsage.getDisplayValue(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getAddressUsage()));
        setRelayUsage(EmetconRelayUsage.getDisplayValue(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getRelayUsage()));
        setRouteId(((LMGroupEmetcon) loadGroup).getLmGroupEmetcon().getRouteID());

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
        lmGroupEmetcon.setAddressUsage((Character) getAddressUsage().getDatabaseRepresentation());
        lmGroupEmetcon.setRelayUsage((Character) getRelayUsage().getDatabaseRepresentation());
        lmGroupEmetcon.setRouteID(getRouteId());

        ((LMGroupEmetcon) group).setLmGroupEmetcon(lmGroupEmetcon);
    }

}

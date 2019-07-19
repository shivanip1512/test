package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroupEmetcon;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(value={ "routeName"}, allowGetters= true, ignoreUnknown = true)
public class LoadGroupEmetcon extends LoadGroupBase<LMGroupEmetcon> implements LoadGroupRoute{
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

    @Override
    public Integer getRouteId() {
        return routeId;
    }

    @Override
    public void setRouteId(Integer routeId) {
        this.routeId = routeId;
    }

    @Override
    public String getRouteName() {
        return routeName;
    }

    @Override
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    @Override
    public void buildModel(LMGroupEmetcon lmGroupEmetcon) {
        // Set parent fields
        super.buildModel(lmGroupEmetcon);

        // Set addressing fields
        setGoldAddress(lmGroupEmetcon.getLmGroupEmetcon().getGoldAddress());
        setSilverAddress(lmGroupEmetcon.getLmGroupEmetcon().getSilverAddress());
        setAddressUsage(EmetconAddressUsage.getDisplayValue(lmGroupEmetcon.getLmGroupEmetcon().getAddressUsage()));
        setRelayUsage(EmetconRelayUsage.getDisplayValue(lmGroupEmetcon.getLmGroupEmetcon().getRelayUsage()));
        setRouteId(lmGroupEmetcon.getLmGroupEmetcon().getRouteID());

    }

    @Override
    public void buildDBPersistent(LMGroupEmetcon group) {
        // Set parent fields
        super.buildDBPersistent(group);

        // Set LMGroupEmetcon fields.
        com.cannontech.database.db.device.lm.LMGroupEmetcon lmGroupEmetcon = group.getLmGroupEmetcon();
        lmGroupEmetcon.setGoldAddress(getGoldAddress());
        lmGroupEmetcon.setSilverAddress(getSilverAddress());
        lmGroupEmetcon.setAddressUsage((Character) getAddressUsage().getDatabaseRepresentation());
        lmGroupEmetcon.setRelayUsage((Character) getRelayUsage().getDatabaseRepresentation());
        lmGroupEmetcon.setRouteID(getRouteId());

        group.setLmGroupEmetcon(lmGroupEmetcon);
    }

}

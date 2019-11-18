package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.device.lm.LMGroupMCT;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(value={ "routeName"}, allowGetters= true, ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class LoadGroupMCT extends LoadGroupBase<LMGroupMCT> implements LoadGroupRoute {

    private Integer routeId;
    private String routeName;
    private AddressLevel level;
    private Integer address;
    private Integer mctDeviceId;
    private List<Relays> relayUsage;

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
    public AddressLevel getLevel() {
        return level;
    }
    public void setLevel(AddressLevel level) {
        this.level = level;
    }
    public Integer getAddress() {
        return address;
    }
    public void setAddress(Integer address) {
        this.address = address;
    }
    public Integer getMctDeviceId() {
        return mctDeviceId;
    }
    public void setMctDeviceId(Integer mctDeviceId) {
        this.mctDeviceId = mctDeviceId;
    }
    public List<Relays> getRelayUsage() {
        return relayUsage;
    }
    public void setRelayUsage(List<Relays> relayUsage) {
        this.relayUsage = relayUsage;
    }

    @Override
    public void buildModel(LMGroupMCT lmGroup) {
        super.buildModel(lmGroup);
        setRouteId(lmGroup.getRouteID());

        com.cannontech.database.db.device.lm.LMGroupMCT lmGroupMCT = lmGroup.getLmGroupMCT();
        setLevel(AddressLevel.getForLevel(lmGroupMCT.getLevel()));
        if (lmGroupMCT.getAddress() > 0) {
            setAddress(lmGroupMCT.getAddress());
        }
        if (lmGroupMCT.getMctDeviceID() > 0) {
            setMctDeviceId(lmGroupMCT.getMctDeviceID());
        }

        if (org.apache.commons.lang3.StringUtils.isNotEmpty(lmGroupMCT.getRelayUsage())) {
            String relayUsage = lmGroupMCT.getRelayUsage().trim();
            List<Relays> relays = new ArrayList<>();
            for (int i = 0; i < relayUsage.length(); i++) {
                relays.add(Relays.getDisplayValue(relayUsage.charAt(i)));
            }
            setRelayUsage(relays.isEmpty() ? null : relays);
        }
    }

    @Override
    public void buildDBPersistent(LMGroupMCT lmGroup) {
        super.buildDBPersistent(lmGroup);
        com.cannontech.database.db.device.lm.LMGroupMCT lmGroupMCT = new com.cannontech.database.db.device.lm.LMGroupMCT();
        lmGroupMCT.setDeviceID(getId());
        lmGroupMCT.setRouteID(getRouteId());
        lmGroupMCT.setLevel(getLevel().getLevel().toString());
        if (getLevel() == AddressLevel.MCT_ADDRESS) {
            lmGroupMCT.setMctDeviceID(this.getMctDeviceId());
            lmGroupMCT.setAddress(0);
        } else {
            lmGroupMCT.setAddress(getAddress());
            lmGroupMCT.setMctDeviceID(0);
        }

        if (getRelayUsage() != null && !getRelayUsage().isEmpty()) {
            String relayUsageStr = getRelayUsage().stream()
                                                  .map(e -> e.getRelayNumber())
                                                  .map(String::valueOf)
                                                  .collect(Collectors.joining());
            lmGroupMCT.setRelayUsage(StringUtils.formatStringWithPattern(relayUsageStr, "1234"));
        } else {
            lmGroupMCT.setRelayUsage(org.apache.commons.lang3.StringUtils.EMPTY);
        }
        lmGroup.setLmGroupMCT(lmGroupMCT);
    }
}

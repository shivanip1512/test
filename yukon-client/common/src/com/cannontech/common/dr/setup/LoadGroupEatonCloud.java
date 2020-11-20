package com.cannontech.common.dr.setup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.cannontech.common.util.StringUtils;
import com.cannontech.database.data.device.lm.LMGroupEatonCloud;

public class LoadGroupEatonCloud extends LoadGroupBase<LMGroupEatonCloud> {
    private List<Loads> relayUsage;

    public List<Loads> getRelayUsage() {
        if (relayUsage == null) {
            relayUsage = new ArrayList<Loads>();
        }
        return relayUsage;
    }

    public void setRelayUsage(List<Loads> relayUsage) {
        this.relayUsage = relayUsage;
    }

    @Override
    public void buildModel(LMGroupEatonCloud lmGroup) {
        super.buildModel(lmGroup);
        String loadsString = lmGroup.getRelayUsage();
        if (loadsString != null) {
            loadsString = loadsString.trim();
            List<Loads> loads = new ArrayList<>();
            for (int i = 0; i < loadsString.length(); i++) {
                loads.add(Loads.getForLoads(Character.getNumericValue(loadsString.charAt(i))));
            }
            setRelayUsage(loads);
        }
    }

    @Override
    public void buildDBPersistent(LMGroupEatonCloud lmGroup) {
        super.buildDBPersistent(lmGroup);
        String loads =
                getRelayUsage().stream().map(e -> e.getLoadNumber()).map(String::valueOf).collect(Collectors.joining());
        lmGroup.setRelayUsage(loads);
    }
}

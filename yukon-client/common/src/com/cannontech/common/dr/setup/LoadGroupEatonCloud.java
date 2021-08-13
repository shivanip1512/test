package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroupEatonCloud;

public class LoadGroupEatonCloud extends LoadGroupBase<LMGroupEatonCloud> {
    private Integer virtualRelayId;

    public Integer getVirtualRelayId() {
        return virtualRelayId;
    }

    public void setVirtualRelayId(Integer virtualRelayId) {
        this.virtualRelayId = virtualRelayId;
    }

    @Override
    public void buildModel(LMGroupEatonCloud lmGroup) {
        super.buildModel(lmGroup);
        String loadsString = lmGroup.getRelayUsage();
        if (loadsString != null) {
            loadsString = loadsString.trim();
            setVirtualRelayId(Character.getNumericValue(loadsString.charAt(0)));
        }
    }

    @Override
    public void buildDBPersistent(LMGroupEatonCloud lmGroup) {
        super.buildDBPersistent(lmGroup);
        lmGroup.setRelayUsage(String.valueOf(virtualRelayId));
    }
}

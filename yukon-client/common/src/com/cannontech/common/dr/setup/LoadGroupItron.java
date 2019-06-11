package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupItron;

public class LoadGroupItron extends LoadGroupBase {
    private Integer virtualRelayId;

    public Integer getVirtualRelayId() {
        return virtualRelayId;
    }

    public void setVirtualRelayId(Integer virtualRelayId) {
        this.virtualRelayId = virtualRelayId;
    }

    @Override
    public void buildModel(LMGroup loadGroup) {
        // Set parent fields
        super.buildModel(loadGroup);

        // Set VirtualRelayId field
        setVirtualRelayId(((LMGroupItron) loadGroup).getRelay());
    }

    @Override
    public void buildDBPersistent(LMGroup group) {
        // Set parent fields
        super.buildDBPersistent(group);
        ((LMGroupItron) group).setRelay(getVirtualRelayId());

    }
}

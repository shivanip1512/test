package com.cannontech.common.dr.setup;

import com.cannontech.database.data.device.lm.LMGroupItron;

public class LoadGroupItron extends LoadGroupBase<LMGroupItron> {
    private Integer virtualRelayId;

    public Integer getVirtualRelayId() {
        return virtualRelayId;
    }

    public void setVirtualRelayId(Integer virtualRelayId) {
        this.virtualRelayId = virtualRelayId;
    }

    @Override
    public void buildModel(LMGroupItron lmGroupItron) {
        // Set parent fields
        super.buildModel(lmGroupItron);

        // Set VirtualRelayId field
        setVirtualRelayId(lmGroupItron.getRelay());
    }

    @Override
    public void buildDBPersistent(LMGroupItron lmGroupItron) {
        // Set parent fields
        super.buildDBPersistent(lmGroupItron);
        lmGroupItron.setRelay(getVirtualRelayId());

    }
}

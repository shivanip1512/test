package com.cannontech.message.macs.message;

import com.roguewave.vsj.DefineCollectable;

public class MacCollectableMappings {

    private static DefineCollectable[] MACS_DC_MAPPINGS = {
        new DefineCollectableInfo(),
        new DefineCollectableOverrideRequest(),
        new DefineCollectableRetrieveScript(),
        new DefineCollectableRetrieveSchedule(),
        new DefineCollectableUpdateSchedule(),
        new DefineCollectableAddSchedule(),
        new DefineCollectableDeleteSchedule(),
        new com.cannontech.message.dispatchmessage.DefineCollectableMulti(),
        new DefineCollectableScriptFile(),
        new DefineCollectableSchedule() };

    public static DefineCollectable[] getMappings() {
        return MACS_DC_MAPPINGS;
    }
}

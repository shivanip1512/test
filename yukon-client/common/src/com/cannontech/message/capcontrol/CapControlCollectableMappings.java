package com.cannontech.message.capcontrol;

import com.cannontech.message.dispatchmessage.DefineCollectableMulti;
import com.cannontech.message.dispatchmessage.DefineCollectablePointData;
import com.cannontech.message.thirdparty.DefineCollectableControlHistory;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.CollectableMappings;

public class CapControlCollectableMappings {

    // this MUST contain all the DefineCollectibles of the CBC client
    private static DefineCollectable[] mappings = {
        //Data Structures
        new DefineCollectableCapBankDevice(),
        new DefineCollectableSubBus(),
        new DefineCollectableFeeder(),
        new DefineCollectableState(),
        new DefineCollectableArea(),
        new DefineCollectableSpecialAreas(),
        new DefineCollectableSpecialArea(),
        new DefineCollectableVoltageRegulator(),
        
        //Collectable Mappings
        CollectableMappings.OrderedVector,
        CollectableMappings.CollectableString,
        
        //Messages
        new DefineCollectableStateGroupMessage(),
        new DefineCollectableMulti(),
        new DefineCollectableCapControlCommand(),
        new DefineCollectableItemCommand(),
        new DefineCollectableAreas(),
        new DefineCollectableSubstationBuses(),
        new DefineCollectableCapControlMessage(), // not used except as a superclass
        new DefineCollectablePointData(),
        new DefineCollectableTempMoveCapBank(),
        new DefineCollectableVerifyBanks(),
        new DefineCollectableVerifyInactiveBanks(),
        new DefineCollectableVerifySelectedBank(),
        new DefineCollectableSubStation(),
        new DefineCollectableSubStations(),
        new DefineCollectableCapControlServerResponse(),
        new DefineCollectableVoltageRegulatorMessage(),
        new DefineCollectableDynamicCommand(),
        new DefineCollectableChangeOpState(),
        new DefineCollectableSystemStatus(),
        new DefineCollectableDeleteItem()
    };
    
    public static DefineCollectable[] getMappings() {
        return mappings;
    }
}

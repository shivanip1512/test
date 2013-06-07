package com.cannontech.message.porter.message;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.DefineCollectable;

public class PorterCollectableMappings {

    private static DefineCollectable[] mappings = {
        new com.cannontech.message.dispatchmessage.DefineCollectableMulti(),
        new com.cannontech.message.dispatchmessage.DefineCollectablePointData(),
        new com.cannontech.message.dispatchmessage.DefineCollectableSignal(),

        new com.cannontech.message.porter.message.DefineCollectableRequest(),
        new com.cannontech.message.porter.message.DefineCollectableReturn(),
        new com.cannontech.message.porter.message.DefineCollectableQueueData(),
        new com.cannontech.message.porter.message.DefineCollectableRequestCancel() };

    /**
     * This method was created in VisualAge.
     * @return com.roguewave.vsj.DefineCollectable[]
     */
    public static DefineCollectable[] getMappings() {
        return mappings;
    }
}

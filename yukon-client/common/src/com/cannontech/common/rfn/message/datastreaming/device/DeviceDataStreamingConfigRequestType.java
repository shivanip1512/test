package com.cannontech.common.rfn.message.datastreaming.device;

/**
 * ASSESS is used when Yukon wants NM to assess if the specified configuration changes would be acceptable.
 *     There would be no changes to the data streaming status stored in NM.
 * UPDATE is used when Yukon wants to actually notify NM of configuration changes that it intends to make immediately.
 *     The changes will be affected in NM if they are acceptable.
 * CONFIRM is used whenever Yukon receives a data streaming E2E �response� (Opcode 0x85 or 0x87),
 *     Yukon will send the result to NM to confirm the update.
 */
public enum DeviceDataStreamingConfigRequestType {
    ASSESS,
    UPDATE,
    CONFIRM,
    OTHER, // reserved for future use
}
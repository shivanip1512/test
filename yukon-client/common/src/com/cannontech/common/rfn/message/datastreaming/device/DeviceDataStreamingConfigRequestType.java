package com.cannontech.common.rfn.message.datastreaming.device;

/**
 * TEST_ONLY is used when Yukon wants NM to confirm that the specified configuration changes would be acceptable.
 *     There would be no changes to the data streaming status stored in NM.
 * CONFIG is used when Yukon wants to actually notify NM of configuration changes that it intends to make immediately.
 *     The changes will be affected in NM if they are acceptable.
 * SYNC is used whenever Yukon receives an data streaming E2E “response” (Opcode 0x85 or 0x87),
 *     Yukon will send the result to NM.
 */
public enum DeviceDataStreamingConfigRequestType {
    TEST_ONLY,
    CONFIG,
    SYNC,
    OTHER, // reserved for future use
}
package com.cannontech.dr.rfn.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.rfn.dao.MockRfnDeviceDaoImpl;
import com.cannontech.common.exception.InvalidExpressComSerialNumberException;
import com.cannontech.dr.rfn.service.impl.RawExpressComCommandBuilderImpl;
import com.cannontech.stars.database.data.lite.LiteLmHardwareBase;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommand;
import com.cannontech.stars.dr.hardware.model.LmHardwareCommandType;

public class RawExpressComCommandBuilderTest {
    
    private MockRfnDeviceDaoImpl rfnDeviceDao;
    private RawExpressComCommandBuilder rawExpressComCommandBuilder = new RawExpressComCommandBuilderImpl();
    
    private final int SMALLEST_VALID_BROADCAST_SPID = 1;
    private final int LARGEST_VALID_BROADCAST_SPID = 65534;
    
    
    @BeforeEach
    public void setUp() throws Exception {
        rfnDeviceDao = new MockRfnDeviceDaoImpl();
        ReflectionTestUtils.setField(rawExpressComCommandBuilder, "rfnDeviceDao", rfnDeviceDao);
    }
    
    @Test
    public void testIsValidBroadcastSpid() {
        int spid;
        // All values below 0 are invalid and should return false.
        spid = SMALLEST_VALID_BROADCAST_SPID - 1000;
        assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID - 100;
        assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID - 2;
        assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        
        // Zero represents the 'disabled' state and should return false.
        spid = 0;
        assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        
        // These values are all valid SPID addresses (1-65534).
        spid = SMALLEST_VALID_BROADCAST_SPID;
        assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID + 1000;
        assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID + 10000;
        assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID + 30000;
        assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID + 60000;
        assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = LARGEST_VALID_BROADCAST_SPID;
        assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));

        // All values above 65534 are invalid addresses.
        spid = LARGEST_VALID_BROADCAST_SPID + 1;
        assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = LARGEST_VALID_BROADCAST_SPID + 100;
        assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = LARGEST_VALID_BROADCAST_SPID + 1000;
        assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
    }
    
    @Test
    public void testGetCommandAsHexStringByteArray() {
        
        LiteLmHardwareBase lmhb = new LiteLmHardwareBase(); 
        lmhb.setDeviceID(30); // This matches the LCR in MockRfnDeviceDaoImpl
        
        LmHardwareCommand lmhc = new LmHardwareCommand();
        lmhc.setDevice(lmhb);
        lmhc.setType(LmHardwareCommandType.READ_NOW);
        lmhc.setUser(null);
        
        byte[] hexStringByteArray = null;
        try {
            hexStringByteArray = rawExpressComCommandBuilder.getCommandAsHexStringByteArray(lmhc);
        } catch (InvalidExpressComSerialNumberException e) {
            // ExpressCom serial numbers must be numeric digits only.
            e.printStackTrace();
        }
         
        assertTrue(hexStringByteArray[0]  == (byte)0x73);
        assertTrue(hexStringByteArray[1]  == (byte)0x30);
        assertTrue(hexStringByteArray[2]  == (byte)0x30);
        assertTrue(hexStringByteArray[3]  == (byte)0x30);
        assertTrue(hexStringByteArray[4]  == (byte)0x30);
        assertTrue(hexStringByteArray[5]  == (byte)0x30);
        assertTrue(hexStringByteArray[6]  == (byte)0x30);
        assertTrue(hexStringByteArray[7]  == (byte)0x33);
        assertTrue(hexStringByteArray[8]  == (byte)0x30);
        assertTrue(hexStringByteArray[9]  == (byte)0x33);
        assertTrue(hexStringByteArray[10] == (byte)0x39);
        assertTrue(hexStringByteArray[11] == (byte)0x41);
        assertTrue(hexStringByteArray[12] == (byte)0x32);
        assertTrue(hexStringByteArray[13] == (byte)0x74);
    }
    
    @Test
    public void testGetBroadcastCancelAllTempOutOfServiceCommand() {
        byte[] broadcastCancelAllTempOutOfServiceCommand = rawExpressComCommandBuilder.getBroadcastCancelAllTempOutOfServiceCommand(13);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[0]  == (byte)0x73);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[1]  == (byte)0x38);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[2]  == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[3]  == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[4]  == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[5]  == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[6]  == (byte)0x44);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[7]  == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[8]  == (byte)0x33);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[9]  == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[10] == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[11] == (byte)0x31);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[12] == (byte)0x36);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[13] == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[14] == (byte)0x30);
        assertTrue(broadcastCancelAllTempOutOfServiceCommand[15] == (byte)0x74);
    }
}

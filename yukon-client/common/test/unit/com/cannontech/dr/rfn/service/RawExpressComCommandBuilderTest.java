package com.cannontech.dr.rfn.service;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
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
    
    
    @Before
    public void setUp() throws Exception {
        rfnDeviceDao = new MockRfnDeviceDaoImpl();
        ReflectionTestUtils.setField(rawExpressComCommandBuilder, "rfnDeviceDao", rfnDeviceDao);
    }
    
    @Test
    public void testIsValidBroadcastSpid() {
        int spid;
        // All values below 0 are invalid and should return false.
        spid = SMALLEST_VALID_BROADCAST_SPID - 1000;
        Assert.assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID - 100;
        Assert.assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID - 2;
        Assert.assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        
        // Zero represents the 'disabled' state and should return false.
        spid = 0;
        Assert.assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        
        // These values are all valid SPID addresses (1-65534).
        spid = SMALLEST_VALID_BROADCAST_SPID;
        Assert.assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID + 1000;
        Assert.assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID + 10000;
        Assert.assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID + 30000;
        Assert.assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = SMALLEST_VALID_BROADCAST_SPID + 60000;
        Assert.assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = LARGEST_VALID_BROADCAST_SPID;
        Assert.assertTrue(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));

        // All values above 65534 are invalid addresses.
        spid = LARGEST_VALID_BROADCAST_SPID + 1;
        Assert.assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = LARGEST_VALID_BROADCAST_SPID + 100;
        Assert.assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
        spid = LARGEST_VALID_BROADCAST_SPID + 1000;
        Assert.assertFalse(rawExpressComCommandBuilder.isValidBroadcastSpid(spid));
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
         
        Assert.assertTrue(hexStringByteArray[0]  == (byte)0x73);
        Assert.assertTrue(hexStringByteArray[1]  == (byte)0x30);
        Assert.assertTrue(hexStringByteArray[2]  == (byte)0x30);
        Assert.assertTrue(hexStringByteArray[3]  == (byte)0x30);
        Assert.assertTrue(hexStringByteArray[4]  == (byte)0x30);
        Assert.assertTrue(hexStringByteArray[5]  == (byte)0x30);
        Assert.assertTrue(hexStringByteArray[6]  == (byte)0x30);
        Assert.assertTrue(hexStringByteArray[7]  == (byte)0x33);
        Assert.assertTrue(hexStringByteArray[8]  == (byte)0x30);
        Assert.assertTrue(hexStringByteArray[9]  == (byte)0x33);
        Assert.assertTrue(hexStringByteArray[10] == (byte)0x39);
        Assert.assertTrue(hexStringByteArray[11] == (byte)0x41);
        Assert.assertTrue(hexStringByteArray[12] == (byte)0x32);
        Assert.assertTrue(hexStringByteArray[13] == (byte)0x74);
    }
    
    @Test
    public void testGetBroadcastCancelAllTempOutOfServiceCommand() {
        byte[] broadcastCancelAllTempOutOfServiceCommand = rawExpressComCommandBuilder.getBroadcastCancelAllTempOutOfServiceCommand(13);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[0]  == (byte)0x73);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[1]  == (byte)0x38);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[2]  == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[3]  == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[4]  == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[5]  == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[6]  == (byte)0x44);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[7]  == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[8]  == (byte)0x33);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[9]  == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[10] == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[11] == (byte)0x31);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[12] == (byte)0x36);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[13] == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[14] == (byte)0x30);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[15] == (byte)0x74);
    }
}

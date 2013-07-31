package com.cannontech.dr.rfn.service;

import java.nio.ByteBuffer;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.cannontech.dr.rfn.service.RawExpressComCommandBuilder;
import com.cannontech.dr.rfn.service.impl.RawExpressComCommandBuilderImpl;

public class RawExpressComCommandBuilderTest {
    
    private RawExpressComCommandBuilder rawExpressComCommandBuilder = new RawExpressComCommandBuilderImpl();
    
    private final int SMALLEST_VALID_BROADCAST_SPID = 1;
    private final int LARGEST_VALID_BROADCAST_SPID = 65534;
    
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
    public void testGetBroadcastCancelAllTempOutOfServiceCommand() {
        byte[] broadcastCancelAllTempOutOfServiceCommand = rawExpressComCommandBuilder.getBroadcastCancelAllTempOutOfServiceCommand(16);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[0] == (byte)0x73);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[1] == (byte)0x80);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[2] == (byte)0x00);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[3] == (byte)0x10);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[4] == (byte)0x03);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[5] == (byte)0x00);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[6] == (byte)0x16);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[7] == (byte)0x00);
        Assert.assertTrue(broadcastCancelAllTempOutOfServiceCommand[8] == (byte)0x74);
    }
}

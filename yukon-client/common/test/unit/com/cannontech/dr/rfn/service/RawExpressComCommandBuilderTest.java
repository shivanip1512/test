package com.cannontech.dr.rfn.service;

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

}

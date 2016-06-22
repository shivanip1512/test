package com.cannontech.amr.rfn.dao.impl;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.dynamic.impl.MockAsyncDynamicDataSourceImpl;

public class RfnIdentifierCacheTest {
    private RfnIdentifierCache cache = new RfnIdentifierCache(null, new MockAsyncDynamicDataSourceImpl());
    
    @Test
    public void test_buildSerialRange() {
        List<String> serialRange;

        serialRange = invokeBuildSerialRange("B02984");
        Assert.assertEquals(serialRange.size(), 250);
        Assert.assertEquals(serialRange.get(0), "B02750");
        Assert.assertEquals(serialRange.get(100), "B02850");
        Assert.assertEquals(serialRange.get(249), "B02999");
        
        serialRange = invokeBuildSerialRange("B00249");
        Assert.assertEquals(serialRange.size(), 250);
        Assert.assertEquals(serialRange.get(0), "B00000");
        Assert.assertEquals(serialRange.get(100), "B00100");
        Assert.assertEquals(serialRange.get(249), "B00249");
        
        serialRange = invokeBuildSerialRange("B99994");
        Assert.assertEquals(serialRange.size(), 250);
        Assert.assertEquals(serialRange.get(0), "B99750");
        Assert.assertEquals(serialRange.get(100), "B99850");
        Assert.assertEquals(serialRange.get(249), "B99999");

        serialRange = invokeBuildSerialRange("3141");
        Assert.assertTrue(serialRange.isEmpty());

        serialRange = invokeBuildSerialRange("31415");
        Assert.assertEquals(serialRange.size(), 250);
        Assert.assertEquals(serialRange.get(0), "31250");
        Assert.assertEquals(serialRange.get(100), "31350");
        Assert.assertEquals(serialRange.get(249), "31499");

        serialRange = invokeBuildSerialRange("314159");
        Assert.assertEquals(serialRange.size(), 250);
        Assert.assertEquals(serialRange.get(0), "314000");
        Assert.assertEquals(serialRange.get(100), "314100");
        Assert.assertEquals(serialRange.get(249), "314249");
            
        serialRange = invokeBuildSerialRange("3141592");
        Assert.assertEquals(serialRange.size(), 250);
        Assert.assertEquals(serialRange.get(0), "3141500");
        Assert.assertEquals(serialRange.get(100), "3141600");
        Assert.assertEquals(serialRange.get(249), "3141749");
            
        serialRange = invokeBuildSerialRange("31415927");
        Assert.assertEquals(serialRange.size(), 250);
        Assert.assertEquals(serialRange.get(0), "31415750");
        Assert.assertEquals(serialRange.get(100), "31415850");
        Assert.assertEquals(serialRange.get(249), "31415999");

        Assert.assertEquals(invokeBuildSerialRange("banana"), Collections.emptyList());
    }
    
    private List<String> invokeBuildSerialRange(String param) {
         return ReflectionTestUtils.invokeMethod(cache, "buildSerialRange", param);
    }
}
package com.cannontech.amr.rfn.dao.impl;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.core.dynamic.impl.MockAsyncDynamicDataSourceImpl;
import com.google.common.collect.Range;
import com.google.common.collect.Ranges;

public class RfnIdentifierCacheTest {
    private RfnIdentifierCache cache = new RfnIdentifierCache(null, new MockAsyncDynamicDataSourceImpl());
    
    @Test
    public void test_buildSerialRange() {
        Assert.assertEquals(invokeBuildSerialRange("B02984"), Ranges.openClosed("B02", "B03"));
        Assert.assertEquals(invokeBuildSerialRange("31415927"), Ranges.openClosed("31415", "31416"));
        Assert.assertEquals(invokeBuildSerialRange("3141"), null);
        Assert.assertEquals(invokeBuildSerialRange("B99994"), null);
        Assert.assertEquals(invokeBuildSerialRange("banana"), null);
    }
    
    private Range<String> invokeBuildSerialRange(String param) {
         return ReflectionTestUtils.invokeMethod(cache, "buildSerialRange", param);
    }
}
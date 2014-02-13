package com.cannontech.dr.rfn.service;

import junit.framework.Assert;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.dr.rfn.service.impl.ExiParsingServiceImpl;
import com.cannontech.dr.rfn.service.impl.ExiParsingServiceImpl.Schema;

public class ExiParsingServiceTest {
    private final ExiParsingServiceImpl exiParsingService = new ExiParsingServiceImpl();
    
    private final static byte[] noExpressComHeaderSchema_0_0_2 = { 0x04, 0x31, 0x00, 0x02 };
    private final static byte[] noExpressComHeaderSchema_0_0_3 = { 0x04, 0x31, 0x00, 0x03 };
    private final static byte[] withExpressComHeaderSchema_0_0_2 = { (byte) 0xE2, (byte) 0xFE, (byte) 0xDC, 0x04, 0x31, 0x00, 0x02 };
    private final static byte[] withExpressComHeaderSchema_0_0_3 = { (byte) 0xE2, (byte) 0xBA, (byte) 0x98, 0x04, 0x31, 0x00, 0x03 };
    private final static byte[] fromDRReport_6600_v1_1_1_ex3 = { (byte) 0xE2, 0x31, 0x0D, 0x04, 0x3A, 0x00, 0x03 };
    @Test
    public void testHeaderSchemaVersionParsing() {
        Schema result;
        result = ReflectionTestUtils.invokeMethod(exiParsingService, "getSchema", noExpressComHeaderSchema_0_0_2);
        Assert.assertEquals("The getSchema() method is not returning the expected schema version for this header data.",
                Schema.SCHEMA_0_0_2, result);
        
        result = ReflectionTestUtils.invokeMethod(exiParsingService, "getSchema", noExpressComHeaderSchema_0_0_3);
        Assert.assertEquals("The getSchema() method is not returning the expected schema version for this header data.",
                Schema.SCHEMA_0_0_3, result);
        
        result = ReflectionTestUtils.invokeMethod(exiParsingService, "getSchema", withExpressComHeaderSchema_0_0_2);
        Assert.assertEquals("The getSchema() method is not returning the expected schema version for this header data.",
                Schema.SCHEMA_0_0_2, result);
        
        result = ReflectionTestUtils.invokeMethod(exiParsingService, "getSchema", withExpressComHeaderSchema_0_0_3);
        Assert.assertEquals("The getSchema() method is not returning the expected schema version for this header data.",
                Schema.SCHEMA_0_0_3, result);
        
        result = ReflectionTestUtils.invokeMethod(exiParsingService, "getSchema", fromDRReport_6600_v1_1_1_ex3);
        Assert.assertEquals("The getSchema() method is not returning the expected schema version for this header data.",
                Schema.SCHEMA_0_0_3, result);
        
    }
}

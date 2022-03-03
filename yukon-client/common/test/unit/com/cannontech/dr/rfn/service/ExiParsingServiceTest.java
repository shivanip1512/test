package com.cannontech.dr.rfn.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.cannontech.dr.rfn.service.ParsingService.Schema;

public class ExiParsingServiceTest {

    private final static byte[] noExpressComHeaderSchema_0_0_2 = { 0x04, 0x31, 0x00, 0x02 };
    private final static byte[] noExpressComHeaderSchema_0_0_3 = { 0x04, 0x31, 0x00, 0x03 };
    private final static byte[] withExpressComHeaderSchema_0_0_2 = { (byte) 0xE2, (byte) 0xFE, (byte) 0xDC, 0x04, 0x31, 0x00, 0x02 };
    private final static byte[] withExpressComHeaderSchema_0_0_3 = { (byte) 0xE2, (byte) 0xBA, (byte) 0x98, 0x04, 0x31, 0x00, 0x03 };
    private final static byte[] fromDRReport_6600_v1_1_1_ex3 = { (byte) 0xE2, 0x31, 0x0D, 0x04, 0x3A, 0x00, 0x03 };

    @Test
    public void testHeaderSchemaVersionParsing() {
        Schema result;
        result = ParsingService.getSchema(noExpressComHeaderSchema_0_0_2);
        assertEquals(Schema.SCHEMA_0_0_2, result,
                "The getSchema() method is not returning the expected schema version for this header data.");

        result = ParsingService.getSchema(noExpressComHeaderSchema_0_0_3);
        assertEquals(Schema.SCHEMA_0_0_3, result,
                "The getSchema() method is not returning the expected schema version for this header data.");

        result = ParsingService.getSchema(withExpressComHeaderSchema_0_0_2);
        assertEquals(Schema.SCHEMA_0_0_2, result,
                "The getSchema() method is not returning the expected schema version for this header data.");

        result = ParsingService.getSchema(withExpressComHeaderSchema_0_0_3);
        assertEquals(Schema.SCHEMA_0_0_3, result,
                "The getSchema() method is not returning the expected schema version for this header data.");

        result = ParsingService.getSchema(fromDRReport_6600_v1_1_1_ex3);
        assertEquals(Schema.SCHEMA_0_0_3, result,
                "The getSchema() method is not returning the expected schema version for this header data.");

    }
}

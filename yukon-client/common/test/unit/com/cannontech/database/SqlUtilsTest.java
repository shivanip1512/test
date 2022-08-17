package com.cannontech.database;

import static org.junit.Assert.*;

import org.junit.Test;

public class SqlUtilsTest {

    @Test
    public void testNullStringConversion() {
        // note that everything goes straight through except the whitespace strings
        // we loose our ability to store that, but that's on purpose
        String[] input =  {null, "", " ", "   ", "foo", "  bar  "};
        String[] output = {null, "", "",  "",    "foo", "  bar  "};
        
        for (int i = 0; i < input.length; ++i) {
            String dbValue = SqlUtils.convertStringToDbValue(input[i]);
            String oracleValue = oracleTreatment(dbValue);
            String mssqlValue = mssqlTreatment(dbValue);
            
            assertEquals("oracle conversion is broke", output[i], SqlUtils.convertDbValueToString(oracleValue));
            assertEquals("mssqlValue conversion is broke", output[i], SqlUtils.convertDbValueToString(mssqlValue));
        }
    }
    
    public String oracleTreatment(String dbValue) {
        // in oracle, you'll never get back a ""
        if ("".equals(dbValue)) {
            return null;
        }
        return dbValue;
    }
    
    public String mssqlTreatment(String dbValue) {
        return dbValue;
    }
    
}

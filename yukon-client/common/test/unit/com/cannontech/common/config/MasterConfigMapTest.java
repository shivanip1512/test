package com.cannontech.common.config;

import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.TestCase;

public class MasterConfigMapTest extends TestCase {

    private MasterConfigMap masterConfigMap;
    
    private void asertConfigHelper(String key, String expectedValue) {
        assertEquals(key, expectedValue, masterConfigMap.getRequiredString(key));
    }

    /*
     * Test method for 'com.cannontech.common.config.MasterConfigMap.read(Reader)'
     */
    public void testExistingFields() {
        asertConfigHelper("DB_RWDBDLL", "ora15d.dll");
        asertConfigHelper("DB_SQLSERVER", "yukon");
        asertConfigHelper("FDR_PI_SERVER_NODE_NAME", "127.0.0.1");
    }
    
    public void testCommentedValue() {
        asertConfigHelper("PORTER_ADD_TAP_PREFIX", "FALSE");
        asertConfigHelper("DISPATCH_RELOAD_RATE", "3600");
    }
    
    public void testSpacesInValue() {
        asertConfigHelper("FDR_LIVEDATA_ICCP_QUALITY_PATTERNS", "0x00, 0x00");
    }
    
    public void testColonInValue() {
        asertConfigHelper("KEY_FOR_VALUE_WITH_COLON", "tom: cool");
    }
    
    public void testMissingField() {
        try {
            masterConfigMap.getRequiredString("BLAHBLAH");
            fail("BLAHBLAH was not expected to exist in the file");
        } catch (RuntimeException e) {
            // expected
        }
    }
    
    public void testCommentedField() {
        try {
            masterConfigMap.getRequiredString("YUKON_EMAIL_FROM");
            fail("YUKON_EMAIL_FROM should be commented out in the file");
        } catch (RuntimeException e) {
            // expected
        }
    }
    
    @Override
    protected void setUp() throws Exception {
        InputStream masterCfgResource = getClass().getResourceAsStream("master.cfg"); 
        assertNotNull("Could not find master.cfg in path", masterCfgResource);
        masterConfigMap = new MasterConfigMap();
        masterConfigMap.read(new InputStreamReader(masterCfgResource));
    }

}

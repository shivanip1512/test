package com.cannontech.common.config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;

import org.joda.time.DurationFieldType;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class MasterConfigMapTest {

    private MasterConfigMap masterConfigMap;
    private String aDeprecatedSetting = "DISPATCH_PORT";

    private void asertConfigHelper(String key, String expectedValue) {
        assertEquals(key, expectedValue, masterConfigMap.getRequiredString(key));
    }

    /*
     * Test method for 'com.cannontech.common.config.MasterConfigMap.read(Reader)'
     */
    @Test
    public void testExistingFields() {
        asertConfigHelper("DB_USERNAME", "isoc_tom");
        asertConfigHelper("DB_SQLSERVER", "yukon");
        asertConfigHelper("FDR_PI_SERVER_NODE_NAME", "127.0.0.1");
    }
    
    @Test
    public void testCommentedValue() {
        asertConfigHelper("PORTER_ADD_TAP_PREFIX", "FALSE");
        asertConfigHelper("DISPATCH_RELOAD_RATE", "3600");
    }
    
    @Test
    public void testSpacesInValue() {
        asertConfigHelper("FDR_LIVEDATA_ICCP_QUALITY_PATTERNS", "0x00, 0x00");
    }
    
    @Test
    public void testColonInValue() {
        asertConfigHelper("KEY_FOR_VALUE_WITH_COLON", "tom: cool");
    }
    
    @Test
    public void testMissingField() {
        try {
            masterConfigMap.getRequiredString("BLAHBLAH");
            fail("BLAHBLAH was not expected to exist in the file");
        } catch (RuntimeException e) {
            // expected
        }
    }
    
    @Test
    public void testCommentedField() {
        try {
            masterConfigMap.getRequiredString("YUKON_EMAIL_FROM");
            fail("YUKON_EMAIL_FROM should be commented out in the file");
        } catch (RuntimeException e) {
            // expected
        }
    }
    
    @Test
    public void testPeriods() {
        Period p1 = masterConfigMap.getPeriod("PERIOD_TEST_1", null);
        assertEquals(new Period().withWeeks(2).withDays(1).withHours(6).withSeconds(8), p1);
        Period p2 = masterConfigMap.getPeriod("PERIOD_TEST_2", null);
        assertEquals(new Period().withMinutes(3), p2);
        Period p3 = masterConfigMap.getPeriod("PERIOD_TEST_3", null);
        assertEquals(new Period().withMinutes(3).withSeconds(3).withMillis(400), p3);
        Period p4 = masterConfigMap.getPeriod("PERIOD_TEST_4", null, DurationFieldType.seconds());
        assertEquals(new Period().withSeconds(65), p4);
    }
    
    @Test
    public void testDefaultPeriod() {
        Period defaultPeriod = new Period().withDays(5).withHours(2);
        Period p = masterConfigMap.getPeriod("PERIOD_TEST_9999", defaultPeriod);
        assertEquals(defaultPeriod, p);

    }
    
    @Test
    public void testBadPeriods() {
        try {
            masterConfigMap.getPeriod("PERIOD_TEST_BAD_1", null);
            fail();
        } catch (Exception e) {
        }
        try {
            masterConfigMap.getPeriod("PERIOD_TEST_BAD_2", null);
            fail();
        } catch (Exception e) {
        }
        try {
            masterConfigMap.getPeriod("PERIOD_TEST_4", null);
            fail();
        } catch (Exception e) {
        }
    }

    @Test
    public void testDeprecatedSetting() {
        try {
            masterConfigMap.getBoolean(aDeprecatedSetting, true);
            Assert.fail("getBoolean(String key, boolean defaultValue) shouldn't return for a deprecated CPARM");
        } catch (IllegalArgumentException e) {/* good */}

        try {
            masterConfigMap.getString(aDeprecatedSetting);
            Assert.fail("getString(String key) shouldn't return for a deprecated CPARM");
        } catch (IllegalArgumentException e) {/* good */}

        try {
            masterConfigMap.getInteger(aDeprecatedSetting, 0);
            Assert.fail("getInteger(String key, int defaultValue) shouldn't return for a deprecated CPARM");
        } catch (IllegalArgumentException e) {/* good */}

        try {
            masterConfigMap.getLong(aDeprecatedSetting, 0);
            Assert.fail("getLong(String key, int defaultValue) shouldn't return for a deprecated CPARM");
        } catch (IllegalArgumentException e) {/* good */}

        try {
            masterConfigMap.getString(aDeprecatedSetting, "");
            Assert.fail("getString(String key, String defaultValue) shouldn't return for a deprecated CPARM");
        } catch (IllegalArgumentException e) {/* good */}

        try {
            masterConfigMap.getRequiredString(aDeprecatedSetting);
            Assert.fail("getRequiredString(String key) shouldn't return for a deprecated CPARM");
        } catch (IllegalArgumentException e) {/* good */}

        try {
            masterConfigMap.getRequiredInteger(aDeprecatedSetting);
            Assert.fail("getRequiredInteger(String key) shouldn't return for a deprecated CPARM");
        } catch (IllegalArgumentException e) {/* good */}

        try {
            masterConfigMap.getPeriod(aDeprecatedSetting, null);
            Assert.fail("getPeriod(String key, ReadablePeriod defaultValue) shouldn't return for a deprecated CPARM");
        } catch (IllegalArgumentException e) {/* good */}
    }

    @Before
    public void setUp() throws Exception {
        File masterCfgResource;
        try {
            masterCfgResource = new File(getClass().getResource("master.cfg").toURI());
        } catch(URISyntaxException e) {
            masterCfgResource = new File(getClass().getResource("master.cfg").getPath());
        }
        assertNotNull("Could not find master.cfg in path", masterCfgResource);
        masterConfigMap = new MasterConfigMap(masterCfgResource);
    }

}

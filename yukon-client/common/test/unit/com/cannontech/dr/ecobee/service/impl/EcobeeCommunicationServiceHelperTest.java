package com.cannontech.dr.ecobee.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

public class EcobeeCommunicationServiceHelperTest {
    private EcobeeCommunicationServiceHelper helper;
    private String jobId = "yUo111RE9wtoMmTS1pXXCxhBkOooaf2N";
    private static final List<String> deviceReadColumns = ImmutableList.of("zoneCalendarEvent", "zoneAveTemp",
        "outdoorTemp", "zoneCoolTemp", "zoneHeatTemp", "compCool1", "compHeat1");

    @BeforeEach
    public void setup() {
        helper = new EcobeeCommunicationServiceHelper();
    }

    @Test
    public void test_getSerialNumber() throws Exception {
        Class<EcobeeCommunicationServiceHelper> implClass = EcobeeCommunicationServiceHelper.class;
        Method method = implClass.getDeclaredMethod("getSerialNumber", String.class);
        method.setAccessible(true);
        String text = "123456-" + jobId;
        String thermostatId = (String) method.invoke(helper, text);
        assertEquals("123456", thermostatId, "Expected thermostat number doesn't match returned value");
    }

    @Test
    public void test_parseIntegerData() throws Exception {
        Class<EcobeeCommunicationServiceHelper> implClass = EcobeeCommunicationServiceHelper.class;
        Method method = implClass.getDeclaredMethod("parseIntegerData", String.class);
        method.setAccessible(true);
        Integer nullInteger = (Integer) method.invoke(helper, StringUtils.EMPTY);
        assertEquals(null, nullInteger, "Expected number doesn't match returned value");
        Integer notNullInteger = (Integer) method.invoke(helper, "10");
        assertEquals(Integer.valueOf(10), notNullInteger, "Expected number doesn't match returned value");
    }

    @Test
    public void test_parseFloatData() throws Exception {
        Class<EcobeeCommunicationServiceHelper> implClass = EcobeeCommunicationServiceHelper.class;
        Method method = implClass.getDeclaredMethod("parseFloatData", String.class);
        method.setAccessible(true);
        Float nullFlot = (Float) method.invoke(helper, StringUtils.EMPTY);
        assertEquals(null, nullFlot, "Expected number doesn't match returned value");
        Float notNullFloat = (Float) method.invoke(helper, "10");
        assertEquals(Float.valueOf(10f), notNullFloat, "Expected number doesn't match returned value");
    }

    @Test
    public void test_getRuntime() throws Exception {
        Class<EcobeeCommunicationServiceHelper> implClass = EcobeeCommunicationServiceHelper.class;
        Method method = implClass.getDeclaredMethod("getRuntime", Integer.class, Integer.class);
        method.setAccessible(true);
        Integer setCompCool = (Integer) method.invoke(helper, Integer.valueOf(10), null);
        Integer setCompHeat = (Integer) method.invoke(helper, null, Integer.valueOf(20));
        Integer runtime = (Integer) method.invoke(helper, Integer.valueOf(15), Integer.valueOf(15));
        assertEquals(Integer.valueOf(10), setCompCool, "Expected number runttime doesn't match returned value");
        assertEquals(Integer.valueOf(20), setCompHeat, "Expected number runttime doesn't match returned value");
        assertEquals(Integer.valueOf(30), runtime, "Expected number runttime doesn't match returned value");
    }

    @Test
    public void test_buildHeaderIndex_DefaultOrder() throws Exception {
        Class<EcobeeCommunicationServiceHelper> implClass = EcobeeCommunicationServiceHelper.class;
        Method method = implClass.getDeclaredMethod("buildHeaderIndex", String[].class);
        method.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Integer> headerMap = (Map<String, Integer>) method.invoke(helper,
            new Object[] { deviceReadColumns.toArray(new String[deviceReadColumns.size()]) });
        for (Entry<String, Integer> entry : headerMap.entrySet()) {
            Integer actualHeaderIndex = Integer.valueOf(deviceReadColumns.indexOf(entry.getKey()));
            Integer headerIndex = entry.getValue();
            assertEquals(headerIndex,
                    actualHeaderIndex, "Expected header for index " + entry.getKey() + " doesn't match returned value");
        }
    }

    @Test
    public void test_buildHeaderIndex_alphabeticalOrder() throws Exception {
        Class<EcobeeCommunicationServiceHelper> implClass = EcobeeCommunicationServiceHelper.class;
        Method method = implClass.getDeclaredMethod("buildHeaderIndex", String[].class);
        method.setAccessible(true);
        List<String> deviceReadAlphabeticalOrder = ImmutableList.of("compCool1", "compHeat1", "outdoorTemp",
            "zoneAveTemp", "zoneCalendarEvent", "zoneCoolTemp", "zoneHeatTemp");
        @SuppressWarnings("unchecked")
        Map<String, Integer> headerMap = (Map<String, Integer>) method.invoke(helper,
            new Object[] { deviceReadAlphabeticalOrder.toArray(new String[deviceReadAlphabeticalOrder.size()]) });
        for (Entry<String, Integer> entry : headerMap.entrySet()) {
            Integer actualHeaderIndex = Integer.valueOf(deviceReadAlphabeticalOrder.indexOf(entry.getKey()));
            Integer headerIndex = entry.getValue();
            assertEquals(headerIndex,
                    actualHeaderIndex, "Expected header for index " + entry.getKey() + " doesn't match returned value");
        }
    }

    @Test
    public void test_getDecryptedFileName() throws Exception {
        String fileName = "1234-" + jobId + ".tar.gz";
        String url = "https://test.com/" + fileName + ".gpg";
        assertEquals(fileName, helper.getDecryptedFileName(url), "Expected file name doesn't match returned value");
    }

}

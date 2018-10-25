package com.cannontech.web.stars.dr.operator.importAccounts.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.web.stars.dr.operator.importAccounts.AccountImportResult;

public class AccountImportServiceTest {

    AccountImportService accountService = new AccountImportService();
    AccountImportResult results;

    @Before
    public void setUp() {
        PrintWriter importLog = new PrintWriter(System.out, true);
        ReflectionTestUtils.setField(accountService, "importLog", importLog);
    }

    @Test
    public void testIsNestDeviceInCustomerFile() {
        setCustomerData();
        YukonListEntry entry = new YukonListEntry();
        entry.setYukonDefID(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NEST);
        String[] hwFile = { "A", "B", "C", "D", "Nest Thermostats" };

        boolean isNest =
            ReflectionTestUtils.invokeMethod(accountService, "isNestDevice", entry, results, 1, hwFile, false);

        assertTrue("Is nest thermostat ", isNest);
        assertTrue("Customer Error count ", results.custFileErrors == 1);
        assertTrue("Nest Error ", (results.getCustLines().get(1)[1].contains("Cannot import Nest device type")));
    }

    @Test
    public void testIsNestDeviceInHardwareFile() {
        setHardwareData();
        YukonListEntry entry = new YukonListEntry();
        entry.setYukonDefID(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NEST);
        String[] hwFile = { "A", "B", "C", "D", "Nest Thermostats" };

        boolean isNest =
            ReflectionTestUtils.invokeMethod(accountService, "isNestDevice", entry, results, 1, hwFile, true);
        assertTrue("Is nest thermostat ", isNest);
        assertTrue("Customer Error count ", results.hwFileErrors == 1);
        assertTrue("Nest Error ", (results.getHwLines().get(1)[1].contains("Cannot import Nest device type")));
    }

    @Test
    public void testIsNonNestDeviceInCustomerFile() {
        setCustomerData();
        YukonListEntry entry = new YukonListEntry();
        entry.setYukonDefID(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_HONEYWELL_9000);
        String[] hwFile = { "A", "B", "C", "D", "Honeywell" };

        boolean isNest =
            ReflectionTestUtils.invokeMethod(accountService, "isNestDevice", entry, results, 1, hwFile, false);
        assertFalse("Is nest thermostat ", isNest);
        assertFalse("Hardware Error count ", results.custFileErrors == 1);
        assertFalse("Nest Error ", (results.getCustLines().get(1)[1].contains("Cannot import Nest device type")));
    }

    @Test
    public void testIsNonNestDeviceInHardwareFile() {
        setHardwareData();
        YukonListEntry entry = new YukonListEntry();
        entry.setYukonDefID(YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_HONEYWELL_9000);
        String[] hwFile = { "A", "B", "C", "D", "Honeywell" };

        boolean isNest =
            ReflectionTestUtils.invokeMethod(accountService, "isNestDevice", entry, results, 1, hwFile, true);
        assertFalse("Is nest thermostat ", isNest);
        assertFalse("Hardware Error count ", results.hwFileErrors == 1);
        assertFalse("Nest Error ", (results.getHwLines().get(1)[1].contains("Cannot import Nest device type")));
    }

    private void setCustomerData() {
        results = new AccountImportResult();
        Map<Integer, String[]> dataLines = new HashMap<>();
        String[] header = { "A", "" };
        String[] data = { "A", "B" };
        dataLines.put(1, header);
        dataLines.put(2, data);
        results.setCustLines(dataLines);
    }

    private void setHardwareData() {
        results = new AccountImportResult();
        Map<Integer, String[]> dataLines = new HashMap<>();
        String[] header = { "A", "" };
        String[] data = { "A", "B" };
        dataLines.put(1, header);
        dataLines.put(2, data);
        results.setHwLines(dataLines);
    }
}

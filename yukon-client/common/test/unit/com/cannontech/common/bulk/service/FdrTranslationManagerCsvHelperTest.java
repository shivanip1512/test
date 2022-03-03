package com.cannontech.common.bulk.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.cannontech.common.exception.ImportFileFormatException;
import com.google.common.collect.Lists;

public class FdrTranslationManagerCsvHelperTest {
private static FdrTranslationManagerCsvHelper helper;
    
    @BeforeAll
    public static void setup() {
        helper = new FdrTranslationManagerCsvHelper();
    }
    
    @Test
    public void testFormatOptionForColumnHeader() {
        String optionString = "test test/ test (test)";
        String interfaceName = "INTERFACE";
        
        String output = helper.formatOptionForColumnHeader(optionString, interfaceName);
        assertEquals("INTERFACE_TEST_TEST__TEST_TEST", output);
    }
    
    @Test
    public void testAddDefaultColumnsToList() {
        String[] array = {"ONE", "TWO"};
        List<String> list = Lists.newArrayList(array);
        helper.addDefaultExportColumnsToList(list);
        
        assertTrue(list.contains("ONE"));
        assertTrue(list.contains("TWO"));
        assertTrue(list.contains("DEVICE_NAME"));
        assertTrue(list.contains("DEVICE_TYPE"));
        assertTrue(list.contains("POINT_NAME"));
        assertTrue(list.contains("DIRECTION"));
    }
    
    @Test
    public void testMatchesDefaultColumn() {
        String action = "ACTION";
        String deviceName = "DEVICE_NAME";
        String deviceType = "DEVICE_TYPE";
        String pointName = "POINT_NAME";
        String direction = "DIRECTION";
        String actionLc = "action";
        String deviceNameSpace = "DEVICE NAME";
        String other = "OTHER";
        
        assertTrue(helper.matchesDefaultColumn(action));
        assertTrue(helper.matchesDefaultColumn(deviceName));
        assertTrue(helper.matchesDefaultColumn(deviceType));
        assertTrue(helper.matchesDefaultColumn(pointName));
        assertTrue(helper.matchesDefaultColumn(direction));
        assertFalse(helper.matchesDefaultColumn(actionLc));
        assertFalse(helper.matchesDefaultColumn(other));
        assertFalse(helper.matchesDefaultColumn(deviceNameSpace));
    }
    
    @Test
    public void testCheckForMissingDefaultImportHeaders() {
        String[] allArray = {"ACTION", "DEVICE_NAME", "DEVICE_TYPE", "POINT_NAME", "DIRECTION"};
        List<String> allList = Lists.newArrayList(allArray);
        String[] actionMissingArray = {"DEVICE_NAME", "DEVICE_TYPE", "POINT_NAME", "DIRECTION"};
        List<String> actionMissingList = Lists.newArrayList(actionMissingArray);
        String[] directionMissingArray = {"ACTION", "DEVICE_NAME", "DEVICE_TYPE", "POINT_NAME"};
        List<String> directionMissingList = Lists.newArrayList(directionMissingArray);
        String[] directionMissingArray2 = {"DEVICE_NAME", "DEVICE_TYPE", "ACTION", "POINT_NAME"};
        List<String> directionMissingList2 = Lists.newArrayList(directionMissingArray2);
        String[] actionDirectionMissingArray = {"DEVICE_NAME", "DEVICE_TYPE", "POINT_NAME"};
        List<String> actionDirectionMissingList = Lists.newArrayList(actionDirectionMissingArray);
        
        assertNull(helper.checkForMissingDefaultImportHeaders(allList));
        assertEquals("ACTION", helper.checkForMissingDefaultImportHeaders(actionMissingList));
        assertEquals("DIRECTION", helper.checkForMissingDefaultImportHeaders(directionMissingList));
        assertEquals("DIRECTION", helper.checkForMissingDefaultImportHeaders(directionMissingList2));
        assertEquals("ACTION, DIRECTION", helper.checkForMissingDefaultImportHeaders(actionDirectionMissingList));
    }
    
    @Test
    public void testCleanAndValidateHeaders1() throws ImportFileFormatException {
        String[] array = {" outerspace  ", "inner space"};
        List<String> output = helper.cleanAndValidateHeaders(array);
        
        assertTrue(output.contains("OUTERSPACE"));
        assertTrue(output.contains("INNERSPACE"));
    }
    
    @Test
    public void testCleanAndValidateHeaders2() throws ImportFileFormatException {
        String[] array = { " s i m i l a r", "similar" };
        Assertions.assertThrows(ImportFileFormatException.class, () -> {
            helper.cleanAndValidateHeaders(array); // should throw exception
        });
    }

    @Test
    public void testCleanAndValidateHeaders3() throws ImportFileFormatException {
        String[] array = { "similar", "SIMILAR" };
        Assertions.assertThrows(ImportFileFormatException.class, () -> {
            helper.cleanAndValidateHeaders(array); // should throw exception
        });
    }

}

package com.cannontech.common.bulk.service;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cannontech.common.exception.ImportFileFormatException;
import com.google.common.collect.Lists;

public class FdrTranslationManagerCsvHelperTest {
private static FdrTranslationManagerCsvHelper helper;
    
    @BeforeClass
    public static void setup() {
        helper = new FdrTranslationManagerCsvHelper();
    }
    
    @Test
    public void testFormatOptionForColumnHeader() {
        String optionString = "test test/ test (test)";
        String interfaceName = "INTERFACE";
        
        String output = helper.formatOptionForColumnHeader(optionString, interfaceName);
        Assert.assertEquals("INTERFACE_TEST_TEST__TEST_TEST", output);
    }
    
    @Test
    public void testAddDefaultColumnsToList() {
        String[] array = {"ONE", "TWO"};
        List<String> list = Lists.newArrayList(array);
        helper.addDefaultExportColumnsToList(list);
        
        Assert.assertTrue(list.contains("ONE"));
        Assert.assertTrue(list.contains("TWO"));
        Assert.assertTrue(list.contains("DEVICE_NAME"));
        Assert.assertTrue(list.contains("DEVICE_TYPE"));
        Assert.assertTrue(list.contains("POINT_NAME"));
        Assert.assertTrue(list.contains("DIRECTION"));
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
        
        Assert.assertTrue(helper.matchesDefaultColumn(action));
        Assert.assertTrue(helper.matchesDefaultColumn(deviceName));
        Assert.assertTrue(helper.matchesDefaultColumn(deviceType));
        Assert.assertTrue(helper.matchesDefaultColumn(pointName));
        Assert.assertTrue(helper.matchesDefaultColumn(direction));
        Assert.assertFalse(helper.matchesDefaultColumn(actionLc));
        Assert.assertFalse(helper.matchesDefaultColumn(other));
        Assert.assertFalse(helper.matchesDefaultColumn(deviceNameSpace));
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
        
        Assert.assertNull(helper.checkForMissingDefaultImportHeaders(allList));
        Assert.assertEquals("ACTION", helper.checkForMissingDefaultImportHeaders(actionMissingList));
        Assert.assertEquals("DIRECTION", helper.checkForMissingDefaultImportHeaders(directionMissingList));
        Assert.assertEquals("DIRECTION", helper.checkForMissingDefaultImportHeaders(directionMissingList2));
        Assert.assertEquals("ACTION, DIRECTION", helper.checkForMissingDefaultImportHeaders(actionDirectionMissingList));
    }
    
    @Test
    public void testCleanAndValidateHeaders1() throws ImportFileFormatException {
        String[] array = {" outerspace  ", "inner space"};
        List<String> output = helper.cleanAndValidateHeaders(array);
        
        Assert.assertTrue(output.contains("OUTERSPACE"));
        Assert.assertTrue(output.contains("INNERSPACE"));
    }
    
    @Test(expected=ImportFileFormatException.class)
    public void testCleanAndValidateHeaders2() throws ImportFileFormatException {
        String[] array = {" s i m i l a r", "similar"};
        helper.cleanAndValidateHeaders(array); //should throw exception
    }
    
    @Test(expected=ImportFileFormatException.class)
    public void testCleanAndValidateHeaders3() throws ImportFileFormatException {
        String[] array = {"similar", "SIMILAR"};
        helper.cleanAndValidateHeaders(array); //should throw exception
    }

}

package com.cannontech.common.bulk.service;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cannontech.common.bulk.service.impl.FdrTranslationManagerServiceImpl;
import com.cannontech.common.exception.ImportFileFormatException;
import com.google.common.collect.Lists;

@SuppressWarnings("unused")
public class FDRTranslationManagerServiceTest {
    private static FdrTranslationManagerService service;
    
    @BeforeClass
    public static void setup() {
        service = new FdrTranslationManagerServiceImpl();
    }
    
    @Test
    public void testFormatOptionForColumnHeader() {
        String optionString = "test test/ test (test)";
        String interfaceName = "INTERFACE";
        
        String output = service.formatOptionForColumnHeader(optionString, interfaceName);
        Assert.assertEquals("INTERFACE_TEST_TEST__TEST_TEST", output);
    }
    
    @Test
    public void testAddDefaultColumnsToList() {
        String[] array = {"ONE", "TWO"};
        List<String> list = Lists.newArrayList(array);
        service.addDefaultColumnsToList(list);
        
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
        
        Assert.assertTrue(service.matchesDefaultColumn(action));
        Assert.assertTrue(service.matchesDefaultColumn(deviceName));
        Assert.assertTrue(service.matchesDefaultColumn(deviceType));
        Assert.assertTrue(service.matchesDefaultColumn(pointName));
        Assert.assertTrue(service.matchesDefaultColumn(direction));
        Assert.assertFalse(service.matchesDefaultColumn(actionLc));
        Assert.assertFalse(service.matchesDefaultColumn(other));
        Assert.assertFalse(service.matchesDefaultColumn(deviceNameSpace));
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
        
        Assert.assertNull(service.checkForMissingDefaultImportHeaders(allList));
        Assert.assertEquals("ACTION", service.checkForMissingDefaultImportHeaders(actionMissingList));
        Assert.assertEquals("DIRECTION", service.checkForMissingDefaultImportHeaders(directionMissingList));
        Assert.assertEquals("DIRECTION", service.checkForMissingDefaultImportHeaders(directionMissingList2));
    }
    
    @Test
    public void testCleanAndValidateHeaders1() throws ImportFileFormatException {
        String[] array = {" outerspace  ", "inner space"};
        List<String> output = service.cleanAndValidateHeaders(array);
        
        Assert.assertTrue(output.contains("OUTERSPACE"));
        Assert.assertTrue(output.contains("INNERSPACE"));
    }
    
    @Test(expected=ImportFileFormatException.class)
    public void testCleanAndValidateHeaders2() throws ImportFileFormatException {
        String[] array = {" s i m i l a r", "similar"};
        List<String> output = service.cleanAndValidateHeaders(array); //should throw exception
    }
    
    @Test(expected=ImportFileFormatException.class)
    public void testCleanAndValidateHeaders3() throws ImportFileFormatException {
        String[] array = {"similar", "SIMILAR"};
        List<String> output = service.cleanAndValidateHeaders(array); //should throw exception
    }
    
    /*
     * getFilteredTranslationList
     * getAllInterfaceDisplayables
     * addHeadersFromTranslations
     * populateExportArray
     * validateInterfaceHeadersPresent
     * 
     * getInterfaceInfo
     * 
     */
}

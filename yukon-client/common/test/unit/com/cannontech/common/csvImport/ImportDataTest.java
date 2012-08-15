package com.cannontech.common.csvImport;

import java.util.List;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.Lists;

public class ImportDataTest {
    private static final ImportFileFormat format = new ImportFileFormat();
    private static final String requiredName = "REQUIRED COLUMN";
    private static final String optionalName = "OPTIONAL COLUMN";
    private static final String groupedName = "GROUPED COLUMN";
    private static final String valDepName = "VALUE DEPENDENT COLUMN";
    private static final String groupName = "A GROUP";
    private static final Class<String> class1 = String.class;
    private static final Class<Integer> class2 = Integer.class;
    
    @BeforeClass
    public static void init() {
        //Create first file format, with one column of each type
        format.addRequiredColumn(requiredName, class1);
        format.addOptionalColumn(optionalName, class1);
        format.addGroupedColumn(groupedName, class2, groupName);
        format.addValueDependentColumn(valDepName, class2, requiredName, "ASDF");
    }
    
    @Test
    public void testImportData() {
        String value1 = "value1";
        String value2 = "value2";
        String value3 = "10";
        String value4 = "20";
        String value5 = "value5";
        String value6 = "value6";
        String value7 = "30";
        String value8 = "40";
        String[] line1 = {requiredName, optionalName, groupedName, valDepName};
        String[] line2 = {value1, value2, value3, value4};
        String[] line3 = {value5, value6, value7, value8};
        List<String[]> strings = Lists.newArrayList();
        strings.add(line1);
        strings.add(line2);
        strings.add(line3);
        
        ImportData data = new ImportData(strings, format);
        
        Assert.assertEquals(format, data.getFormat());
        
        List<String> retrievedColumnNames = data.getColumnNames();
        Assert.assertTrue(retrievedColumnNames.contains(requiredName));
        Assert.assertTrue(retrievedColumnNames.contains(optionalName));
        Assert.assertTrue(retrievedColumnNames.contains(groupedName));
        Assert.assertTrue(retrievedColumnNames.contains(valDepName));
        
        Assert.assertEquals(strings.size(), data.getOriginalData().size());
        Assert.assertTrue(strings.containsAll(data.getOriginalData()));
        
        List<ImportRow> rows = data.getRows();
        Assert.assertTrue(data.hasValue(requiredName, 0));
        Assert.assertTrue(data.hasValue(optionalName, 0));
        Assert.assertTrue(data.hasValue(groupedName, 0));
        Assert.assertTrue(data.hasValue(valDepName, 0));
        Assert.assertTrue(data.hasValue(requiredName, 1));
        Assert.assertTrue(data.hasValue(optionalName, 1));
        Assert.assertTrue(data.hasValue(groupedName, 1));
        Assert.assertTrue(data.hasValue(valDepName, 1));
        
        ImportRow row1 = rows.get(0);
        ImportRow row2 = rows.get(1);
        Assert.assertEquals(value1, row1.getValue(requiredName));
        Assert.assertEquals(value2, row1.getValue(optionalName));
        Assert.assertEquals(value3, row1.getValue(groupedName));
        Assert.assertEquals(value4, row1.getValue(valDepName));
        Assert.assertEquals(value5, row2.getValue(requiredName));
        Assert.assertEquals(value6, row2.getValue(optionalName));
        Assert.assertEquals(value7, row2.getValue(groupedName));
        Assert.assertEquals(value8, row2.getValue(valDepName));
        
        Assert.assertEquals(value1, data.getValue(requiredName, 0));
        Assert.assertEquals(value2, data.getValue(optionalName, 0));
        Assert.assertEquals(value3, data.getValue(groupedName, 0));
        Assert.assertEquals(value4, data.getValue(valDepName, 0));
        Assert.assertEquals(value5, data.getValue(requiredName, 1));
        Assert.assertEquals(value6, data.getValue(optionalName, 1));
        Assert.assertEquals(value7, data.getValue(groupedName, 1));
        Assert.assertEquals(value8, data.getValue(valDepName, 1));
        
        data.removeColumn(0);
        Assert.assertNull(data.getValue(requiredName, 0));
        Assert.assertNull(data.getValue(requiredName, 1));
        
        data.removeColumn(optionalName);
        Assert.assertNull(data.getValue(optionalName, 0));
        Assert.assertNull(data.getValue(optionalName, 1));
    }
}
package com.cannontech.common.csvImport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
    
    @BeforeAll
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
        
        assertEquals(format, data.getFormat());
        
        List<String> retrievedColumnNames = data.getColumnNames();
        assertTrue(retrievedColumnNames.contains(requiredName));
        assertTrue(retrievedColumnNames.contains(optionalName));
        assertTrue(retrievedColumnNames.contains(groupedName));
        assertTrue(retrievedColumnNames.contains(valDepName));
        
        assertEquals(strings.size(), data.getOriginalData().size());
        assertTrue(strings.containsAll(data.getOriginalData()));
        
        List<ImportRow> rows = data.getRows();
        assertTrue(data.hasValue(requiredName, 0));
        assertTrue(data.hasValue(optionalName, 0));
        assertTrue(data.hasValue(groupedName, 0));
        assertTrue(data.hasValue(valDepName, 0));
        assertTrue(data.hasValue(requiredName, 1));
        assertTrue(data.hasValue(optionalName, 1));
        assertTrue(data.hasValue(groupedName, 1));
        assertTrue(data.hasValue(valDepName, 1));
        
        ImportRow row1 = rows.get(0);
        ImportRow row2 = rows.get(1);
        assertEquals(value1, row1.getValue(requiredName));
        assertEquals(value2, row1.getValue(optionalName));
        assertEquals(value3, row1.getValue(groupedName));
        assertEquals(value4, row1.getValue(valDepName));
        assertEquals(value5, row2.getValue(requiredName));
        assertEquals(value6, row2.getValue(optionalName));
        assertEquals(value7, row2.getValue(groupedName));
        assertEquals(value8, row2.getValue(valDepName));
        
        assertEquals(value1, data.getValue(requiredName, 0));
        assertEquals(value2, data.getValue(optionalName, 0));
        assertEquals(value3, data.getValue(groupedName, 0));
        assertEquals(value4, data.getValue(valDepName, 0));
        assertEquals(value5, data.getValue(requiredName, 1));
        assertEquals(value6, data.getValue(optionalName, 1));
        assertEquals(value7, data.getValue(groupedName, 1));
        assertEquals(value8, data.getValue(valDepName, 1));
        
        data.removeColumn(0);
        assertNull(data.getValue(requiredName, 0));
        assertNull(data.getValue(requiredName, 1));
        
        data.removeColumn(optionalName);
        assertNull(data.getValue(optionalName, 0));
        assertNull(data.getValue(optionalName, 1));
    }
}
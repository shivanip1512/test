package com.cannontech.common.csvImport;

import java.util.Collection;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class ImportFileFormatTest {
    private static final Class<String> class1 = String.class;
    private static final Class<Integer> class2 = Integer.class;
    
    @Test
    public void testRequiredColumns() {
        final String name1 = "REQUIRED1";
        final String name2 = "REQUIRED2";
        
        ImportFileFormat format = new ImportFileFormat();
        format.addRequiredColumn(name1, class1);
        format.addRequiredColumn(name2, class2);
        
        testColumns(name1, name2, class1, class2, format, ImportRequiredColumnDefinition.class);
    }
    
    @Test
    public void testOptionalColumns() {
        final String name1 = "OPTIONAL1";
        final String name2 = "OPTIONAL2";
        
        ImportFileFormat format = new ImportFileFormat();
        format.addOptionalColumn(name1, class1);
        format.addOptionalColumn(name2, class2);
        
        testColumns(name1, name2, class1, class2, format, ImportOptionalColumnDefinition.class);
    }
    
    @Test
    public void testGroupedColumns() {
        final String name1 = "GROUPED1";
        final String name2 = "GROUPED2";
        final String groupName = "TEST_GROUP";
        
        ImportFileFormat format = new ImportFileFormat();
        format.addGroupedColumn(name1, class1, groupName);
        format.addGroupedColumn(name2, class2, groupName);
        
        testColumns(name1, name2, class1, class2, format, ImportGroupedColumnDefinition.class);
        
        Multimap<String, ImportColumnDefinition> retrievedGrouped = format.getGroupedColumns();
        Assert.assertTrue(retrievedGrouped.size() == 2);
        
        Collection<ImportColumnDefinition> columnsInGroup = retrievedGrouped.get(groupName);
        
        Collection<ImportColumnDefinition> comparison = Sets.newHashSet();
        comparison.add(format.getColumnByName(name1));
        comparison.add(format.getColumnByName(name2));
        
        Assert.assertTrue(columnsInGroup.containsAll(comparison) && comparison.containsAll(columnsInGroup));
    }
    
    @Test
    public void testValueDependentColumns() {
        final String name1 = "VDEP1";
        final String name2 = "VDEP2";
        final String depName = "DEPENDEDUPON";
        final String depVal1 = "ASDF";
        final String depVal2 = "qwerty";
        
        ImportFileFormat format = new ImportFileFormat();
        format.addRequiredColumn(depName, class1);
        format.addValueDependentColumn(name1, class1, depName, depVal1);
        format.addValueDependentColumn(name2, class2, depName, depVal2);
        
        testColumns(name1, name2, class1, class2, format, ImportValueDependentColumnDefinition.class);
        
        Multimap<ImportColumnDefinition, ImportValueDependentColumnDefinition> mmap = format.getValueDependentColumns();
        ImportColumnDefinition dependedUponColumn = format.getColumnByName(depName);
        Collection<ImportValueDependentColumnDefinition> vDepCols = mmap.get(dependedUponColumn);
        Assert.assertEquals(2, vDepCols.size());
        
    }
    
    @Test
    public void testClone() {
        final String requiredName = "REQUIRED COLUMN";
        final String optionalName = "OPTIONAL COLUMN";
        final String groupedName = "GROUPED COLUMN";
        final String valDepName = "VALUE DEPENDENT COLUMN";
        final String requiredName2 = "REQUIRED COLUMN 2";
        final String optionalName2 = "OPTIONAL COLUMN 2";
        final String groupedName2 = "GROUPED COLUMN 2";
        final String valDepName2 = "VALUE DEPENDENT COLUMN 2";
        final String groupName = "A GROUP";
        
        //Create first file format, with one column of each type
        ImportFileFormat format = new ImportFileFormat();
        format.addRequiredColumn(requiredName, class1);
        format.addOptionalColumn(optionalName, class1);
        format.addGroupedColumn(groupedName, class2, groupName);
        format.addValueDependentColumn(valDepName, class2, requiredName, "ASDF");
        
        //Create the clone and check that the original columns are retrievable with getColumnByName
        ImportFileFormat clonedFormat = format.clone();
        
        ImportColumnDefinition requiredColumn = clonedFormat.getColumnByName(requiredName);
        Assert.assertNotNull(requiredColumn);
        
        ImportColumnDefinition optionalColumn = clonedFormat.getColumnByName(optionalName);
        Assert.assertNotNull(optionalColumn);
        
        ImportColumnDefinition groupedColumn = clonedFormat.getColumnByName(groupedName);
        Assert.assertNotNull(groupedColumn);
        
        ImportColumnDefinition valDepColumn = clonedFormat.getColumnByName(valDepName);
        Assert.assertNotNull(valDepColumn);
        
        //Check that getColumns() returns everything
        Set<ImportColumnDefinition> allColumns = clonedFormat.getColumns();
        Assert.assertEquals(allColumns.size(), 4);
        Assert.assertTrue(allColumns.contains(requiredColumn));
        Assert.assertTrue(allColumns.contains(optionalColumn));
        Assert.assertTrue(allColumns.contains(groupedColumn));
        Assert.assertTrue(allColumns.contains(valDepColumn));
        
        //Check that the get__Columns() methods return exact duplicates
        Set<ImportColumnDefinition> requiredColumns = clonedFormat.getRequiredColumns();
        Assert.assertEquals(requiredColumns.size(), 1);
        Assert.assertTrue(requiredColumns.contains(requiredColumn));
        
        Set<ImportColumnDefinition> optionalColumns = clonedFormat.getOptionalColumns();
        Assert.assertEquals(optionalColumns.size(), 1);
        Assert.assertTrue(optionalColumns.contains(optionalColumn));
        
        Multimap<String, ImportColumnDefinition> groupedColumns = clonedFormat.getGroupedColumns();
        Assert.assertEquals(groupedColumns.size(), 1);
        Assert.assertTrue(groupedColumns.containsEntry(groupName, groupedColumn));
        
        Multimap<ImportColumnDefinition, ImportValueDependentColumnDefinition> valDepColumns = clonedFormat.getValueDependentColumns();
        Assert.assertEquals(valDepColumns.size(), 1);
        Assert.assertTrue(valDepColumns.containsEntry(requiredColumn, valDepColumn));
        
        //Add new columns to the copy and ensure that they don't get added to the original
        //due to accidental data sharing
        clonedFormat.addRequiredColumn(requiredName2, class1);
        clonedFormat.addOptionalColumn(optionalName2, class1);
        clonedFormat.addGroupedColumn(groupedName2, class2, groupName);
        clonedFormat.addValueDependentColumn(valDepName2, class2, requiredName, "ASDF");
        
        Assert.assertNull(format.getColumnByName(requiredName2));
        Assert.assertNull(format.getColumnByName(optionalName2));
        Assert.assertNull(format.getColumnByName(groupedName2));
        Assert.assertNull(format.getColumnByName(valDepName2));
    }
    
    private void testColumns(String name1, String name2, Class<?> class1, Class<?> class2, ImportFileFormat format, Class<? extends ImportColumnDefinition> columnClass){
        
        ImportColumnDefinition column1 = format.getColumnByName(name1);
        Assert.assertNotNull(column1);
        Assert.assertEquals(columnClass, column1.getClass());
        Assert.assertTrue(column1.getName().equalsIgnoreCase(name1));
        Assert.assertEquals(class1, column1.getType());
        
        ImportColumnDefinition column2 = format.getColumnByName(name2);
        Assert.assertNotNull(column2);
        Assert.assertEquals(columnClass, column2.getClass());
        Assert.assertTrue(column2.getName().equalsIgnoreCase(name2));
        Assert.assertEquals(class2, column2.getType());
    }
}

package com.cannontech.amr.archivedValueExporter.model;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ExportFieldTest {

    @Test
    public void test_isCustom() {
        ExportField expField = new ExportField();
        expField.setPattern("ddmmyy");
        Field field = new Field();
        field.setType(FieldType.POINT_TIMESTAMP);
        expField.setField(field);
        assertTrue(expField.isCustomPattern(), "Must be true");
        
        expField = new ExportField();
        expField.setPattern("###.###");
        field = new Field();
        field.setType(FieldType.POINT_VALUE);
        expField.setField(field);
        assertFalse(expField.isCustomPattern(), "Must be false");
        
        
        expField = new ExportField();
        expField.setPattern("MM/dd/yyyy");
        field = new Field();
        field.setType(FieldType.RUNTIME);
        expField.setField(field);
        assertFalse(expField.isCustomPattern(), "Must be false");
        
        
        expField = new ExportField();
        expField.setPattern("##");
        field = new Field();
        field.setType(FieldType.POINT_VALUE);
        expField.setField(field);
        assertTrue(expField.isCustomPattern(), "Must be true");
        
    }
}

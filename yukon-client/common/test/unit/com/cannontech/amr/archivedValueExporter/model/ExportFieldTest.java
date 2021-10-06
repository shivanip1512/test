package com.cannontech.amr.archivedValueExporter.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ExportFieldTest {

    @Test
    public void test_isEncrypted() {
        ExportField expField = new ExportField();
        expField.setPattern("ddmmyy");
        Field field = new Field();
        field.setType(FieldType.POINT_TIMESTAMP);
        expField.setField(field);
        assertTrue(expField.isCustomPattern(), "True");
    }
}

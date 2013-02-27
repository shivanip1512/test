package com.cannontech.web.amr.archivedValuesExporter.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.Field;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.google.common.collect.Lists;

public class ArchivedValuesExporterBackingBean{

    private int selectedFieldId;
    private int selectedAttributeId;

    private List<Field> fieldSelect = Lists.newArrayList();

    private ExportFormat format = new ExportFormat();
    private ExportAttribute exportAttribute = new ExportAttribute();
    private ExportField exportField = new ExportField();
    
    private String plainText;
    private String valuePattern;
    private String timestampPattern;

    private int rowIndex = -1;
    private String pageNameKey;

    public ExportFormat getFormat() {
        return format;
    }
    public void setFormat(ExportFormat format) {
        this.format = format;
    }

    public ExportAttribute getExportAttribute() {
        return exportAttribute;
    }
    public void setExportAttribute(ExportAttribute exportAttribute) {
        this.exportAttribute = exportAttribute;
    }

    public int getRowIndex() {
        return rowIndex;
    }
    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public ExportField getSelectedField() {
        return format.getFields().get(getRowIndex());
    }
    public void removeSelectedField() {
        format.getFields().remove(getRowIndex());
    }

    private void removeExportFields(int attributeId) {
        ListIterator<ExportField> it = format.getFields().listIterator();
        while (it.hasNext()) {
            ExportField field = it.next();
            if (field.getAttribute() != null
                && field.getAttribute().getAttributeId() == attributeId) {
                it.remove();
            }
        }
    }

    public ExportAttribute getSelectedAttribute() {
        return format.getAttributes().get(getRowIndex());
    }
    public void removeSelectedAttribute() {
        ExportAttribute attribute = getSelectedAttribute();
        removeExportFields(attribute.getAttributeId());
        format.getAttributes().remove(getRowIndex());
    }
    public void addSelectedAttribute() {
        ExportAttribute exportAttribute = getExportAttribute();
        if (rowIndex == -1) {
            exportAttribute.setAttributeId(getNextAttributeId());
            format.getAttributes().add(exportAttribute);
        } else {
            format.getAttributes().set(rowIndex, exportAttribute);
            Iterator<ExportField> it = format.getFields().iterator();
            while (it.hasNext()) {
                ExportField field = it.next();
                if (field.getAttribute() != null
                    && field.getAttribute().getAttributeId() == exportAttribute.getAttributeId()) {
                    field.setAttribute(exportAttribute);
                }
            }
        }
        setExportAttribute(new ExportAttribute());
    }
    
   private int getNextAttributeId() {
        int nextId = 1;
        if (!format.getAttributes().isEmpty()) {
            List<Integer> ids = new ArrayList<Integer>();
            for (ExportAttribute exportAttribute : format.getAttributes()) {
                ids.add(exportAttribute.getAttributeId());
            }
            nextId = ((int) Collections.max(ids)) + 1;
        }
        return nextId;
    }

    public void addSelectedField() {
        ExportField exportField = getExportField();
        if (FieldType.ATTRIBUTE == exportField.getFieldType()) {
            List<Field> fields = getFieldSelect();
            for (Field field : fields) {
                if (getSelectedFieldId() == field.getFieldId()) {
                    exportField.setAttribute(field.getAttribute());
                    break;
                }
            }
            if(AttributeField.TIMESTAMP == exportField.getAttributeField()){
                exportField.setPattern(getTimestampPattern());
            }else if(AttributeField.VALUE == exportField.getAttributeField()){
                exportField.setPattern(getValuePattern());
            }
        }else if (FieldType.PLAIN_TEXT == exportField.getFieldType()) {
            exportField.setPattern(getPlainText());
        } else if (FieldType.POINT_TIMESTAMP == exportField.getFieldType()){
            exportField.setPattern(getTimestampPattern());
        } else if (FieldType.POINT_VALUE == exportField.getFieldType()){
            exportField.setPattern(getValuePattern());
        }
        
        if (rowIndex == -1) {
            format.getFields().add(exportField);
        } else {
            format.getFields().set(rowIndex, exportField);
        }
        setExportField(new ExportField());
    }
    
    public void copyPattern(ExportField exportField) {
        if (FieldType.ATTRIBUTE == exportField.getFieldType()) {
            if (AttributeField.TIMESTAMP == exportField.getAttributeField()) {
                setTimestampPattern(exportField.getPattern());
            } else if (AttributeField.VALUE == exportField.getAttributeField()) {
                setValuePattern(exportField.getPattern());
            }
        } else if (FieldType.PLAIN_TEXT == exportField.getFieldType()) {
            setPlainText(exportField.getPattern());
        }
    }
    

    public int getSelectedFieldId() {
        return selectedFieldId;
    }
    public void setSelectedFieldId(int selectedFieldId) {
        this.selectedFieldId = selectedFieldId;
    }

    public List<Field> getFieldSelect() {
        int id = 0;
        
        List<FieldType> fieldTypes = null;
        if (format.getFormatType() == ArchivedValuesExportFormatType.FIXED_ATTRIBUTE) {
            fieldTypes = Lists.newArrayList(FieldType.FIXED_ATTRIBUTE_FIELD_TYPES);
        } else if (format.getFormatType() == ArchivedValuesExportFormatType.DYNAMIC_ATTRIBUTE) {
            fieldTypes = Lists.newArrayList(FieldType.DYNAMIC_ATTRIBUTE_FIELD_TYPES);
        }
        
        for (FieldType type : fieldTypes) {
            if (type != FieldType.ATTRIBUTE) {
                Field field = new Field(--id, type, null);
                fieldSelect.add(field);
            }
        }
        
        ListIterator<Field> it = fieldSelect.listIterator();
        while (it.hasNext()) {
            Field field = it.next();
            if (field.getType() == FieldType.ATTRIBUTE) {
                it.remove();
            }
        }
        if (!format.getAttributes().isEmpty()) {
            for (ExportAttribute attribute : format.getAttributes()) {
                Field field =
                    new Field(attribute.getAttributeId(), FieldType.ATTRIBUTE, attribute);
                fieldSelect.add(field);
            }
        }
        return fieldSelect;
    }

    public void moveFieldUp(boolean moveUp) {
        int newIndex = getRowIndex();
        if (moveUp) {
            --newIndex;
        } else {
            ++newIndex;
        }
        ExportField exportField = format.getFields().get(getRowIndex());
        format.getFields().remove(getRowIndex());
        format.getFields().add(newIndex, exportField);
    }

    public int getSelectedAttributeId() {
        return selectedAttributeId;
    }
    public void setSelectedAttributeId(int selectedAttributeId) {
        this.selectedAttributeId = selectedAttributeId;
    }

    public ExportField getExportField() {
        return exportField;
    }
    public void setExportField(ExportField exportField) {
        this.exportField = exportField;
    }

    
    public void resetExportFieldValues() {
        exportField.setFieldType(findSelectedFieldType());
        if(exportField.getMissingAttribute() == MissingAttribute.FIXED_VALUE 
                && StringUtils.isEmpty(getExportField().getMissingAttributeValue())){
            exportField.setMissingAttribute(MissingAttribute.LEAVE_BLANK);
        }if(exportField == null){
            exportField.setMaxLength(0);
        }
        if (exportField.getFieldType() == FieldType.ATTRIBUTE 
            && exportField.getAttributeField() != AttributeField.VALUE) {
                exportField.setRoundingMode(null);
        }
        if(exportField.getPadChar() != null && exportField.getPadChar().isEmpty()){
            if(exportField.getPadSide() == PadSide.LEFT || exportField.getPadSide() ==PadSide.RIGHT){
                exportField.setPadChar(" ");
            }
        }
    }

    public int findSelectedFieldId() {
        int id = 0;
        List<Field> fields = getFieldSelect();
        for (Field field : fields) {
            if (FieldType.ATTRIBUTE == exportField.getFieldType()) {
                if (exportField.getAttribute().getAttributeId() == field.getFieldId()) {
                    id = field.getFieldId();
                    break;
                }
            }
            else if (field.getType() == exportField.getFieldType()) {
               id = field.getFieldId();
               break;
            }
        }
        return id;
    }
    
    public FieldType findSelectedFieldType() {
        FieldType fieldType = null;
        List<Field> fields = getFieldSelect();
        for(Field field:fields){
            if(selectedFieldId == field.getFieldId()){
                fieldType=  field.getType();
                break;
            }
        } 
        return fieldType;
    }
    
    public void resetField(YukonRoundingMode roundingMode) {
        setExportField(new ExportField());
        setRowIndex(-1);
        setSelectedFieldId(0);
        getExportField().setMissingAttributeValue("Leave Blank");
        getExportField().setRoundingMode(roundingMode);
    }
    
    public String getPageNameKey() {
        return pageNameKey;
    }
    public void setPageNameKey(String pageNameKey) {
        this.pageNameKey = pageNameKey;
    }


    public String getValuePattern() {
        return valuePattern;
    }
    public void setValuePattern(String valuePattern) {
        this.valuePattern = valuePattern;
    }

    public String getTimestampPattern() {
        return timestampPattern;
    }
    public void setTimestampPattern(String timeStampPattern) {
        this.timestampPattern = timeStampPattern;
    }

    public String getPlainText() {
        return plainText;
    }
    public void setPlainText(String plainText) {
        this.plainText = plainText;
    }
}
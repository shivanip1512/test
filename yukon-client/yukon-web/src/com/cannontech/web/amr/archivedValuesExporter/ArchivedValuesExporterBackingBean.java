package com.cannontech.web.amr.archivedValuesExporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;

import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.Field;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.util.LazyList;
import com.google.common.collect.Lists;

public class ArchivedValuesExporterBackingBean {

    private DeviceCollection deviceCollection;

    private int selectedFormatId;
    private int selectedFieldId;
    private int selectedAttributeId;

    private List<ExportFormat> allFormats = LazyList.ofInstance(ExportFormat.class);
    private List<Field> fieldSelect = Lists.newArrayList();

    private ExportFormat format = new ExportFormat();
    private ExportAttribute exportAttribute = new ExportAttribute();
    private ExportField exportField = new ExportField();

    private String popupToOpen;
    private int rowIndex = -1;

    private String endDate;

    {
        for (FieldType type : FieldType.values()) {
            if (!type.equals(FieldType.ATTRIBUTE)) {
                Field field = new Field(fieldSelect.size() + 1, type, null, type.getDescription());
                fieldSelect.add(field);
            }
        }
    }

    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }

    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public List<ExportFormat> getAllFormats() {
        return allFormats;
    }

    public void setAllFormats(List<ExportFormat> allFormats) {
        this.allFormats = allFormats;
    }

    public ExportFormat getFormat() {
        return format;
    }

    public void setFormat(ExportFormat format) {
        this.format = format;
    }

    public int getSelectedFormatId() {
        return selectedFormatId;
    }

    public void setSelectedFormatId(int selectedFormatId) {
        this.selectedFormatId = selectedFormatId;
    }

    public ExportAttribute getExportAttribute() {
        return exportAttribute;
    }

    public void setExportAttribute(ExportAttribute exportAttribute) {
        this.exportAttribute = exportAttribute;
    }

    public String getPopupToOpen() {
        return popupToOpen;
    }

    public void setPopupToOpen(String popupToReopen) {
        this.popupToOpen = popupToReopen;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public ExportAttribute getSelectedAttribute() {
        return format.getAttributes().get(getRowIndex());
    }

    public ExportField getSelectedField() {
        return format.getFields().get(getRowIndex());
    }

    public void removeSelectedField() {
        format.getFields().remove(getRowIndex());
    }

    public void removeSelectedAttribute() {
        ExportAttribute attribute = getSelectedAttribute();
        removeExportFields(attribute.getAttributeId());
        format.getAttributes().remove(getRowIndex());
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

    public void addSelectedAttribute() {
        ExportAttribute exportAttribute = getExportAttribute();
        if (rowIndex == -1) {
            exportAttribute.setAttributeId(getNextAttributeId());
            format.getAttributes().add(exportAttribute);
        } else {
            format.getAttributes().set(rowIndex, exportAttribute);
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
        if (rowIndex == -1) {
            format.getFields().add(exportField);
        } else {
            format.getFields().set(rowIndex, exportField);
        }
        setExportField(new ExportField());
    }

    private Field getField() {
        List<Field> fields = getFieldSelect();
        for (Field field : fields) {
            if (field.getFieldId() == getSelectedFieldId()) {
                return field;
            }
        }
        return null;
    }

    public int getSelectedFieldId() {
        return selectedFieldId;
    }

    public void setSelectedFieldId(int selectedFieldId) {
        this.selectedFieldId = selectedFieldId;
    }

    public List<Field> getFieldSelect() {
        ListIterator<Field> it = fieldSelect.listIterator();
        while (it.hasNext()) {
            Field field = it.next();
            if (field.getType().equals(FieldType.ATTRIBUTE)) {
                it.remove();
            }
        }
        if (!format.getAttributes().isEmpty()) {
            for (ExportAttribute attribute : format.getAttributes()) {
                Field field =
                    new Field(fieldSelect.size() + 1, FieldType.ATTRIBUTE, attribute, attribute
                        .getAttribute().getDescription());
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

    public void addFieldTypeToExportField() {
        ExportField exoprtField = getExportField();
        Field field = getField();
        exoprtField.setFieldType(field.getType());
        if (field.getType().equals(FieldType.ATTRIBUTE)) {
            exoprtField.setAttribute(field.getAttribute());
        }
    }

    public void setSelectedFieldId(ExportField exportField) {
        List<Field> fields = getFieldSelect();
        for (Field field : fields) {
            if (field.getType().equals(exportField.getFieldType())) {
                selectedFieldId = field.getFieldId();
            }
        }
    }

    public String getPreview() {
        return ExportPreviewFormatter.format(format);
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public void prefillExportField() {
        addFieldTypeToExportField();
        if (StringUtils.isBlank(exportField.getMissingAttributeValue())) {
            exportField.setMissingAttributeValue("Leave Blank");
        }
        if (exportField.getAttributeField() != null && exportField.getAttributeField()
            .equals(AttributeField.TIMESTAMP)) {
            if (StringUtils.isBlank(exportField.getPattern())) {
                exportField.setPattern("dd/MM/yyyy");
            }
        }
    }

}
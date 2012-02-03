package com.cannontech.web.amr.archivedValuesExporter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.Field;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.LazyList;
import com.cannontech.roles.yukon.BillingRole;
import com.google.common.collect.Lists;

public class ArchivedValuesExporterBackingBean{

    private DeviceCollection deviceCollection;

    private int selectedFormatId;
    private int selectedFieldId;
    private int selectedAttributeId;

    private List<ExportFormat> allFormats = LazyList.ofInstance(ExportFormat.class);
    private List<Field> fieldSelect = Lists.newArrayList();

    private ExportFormat format = new ExportFormat();
    private ExportAttribute exportAttribute = new ExportAttribute();
    private ExportField exportField = new ExportField();
    
    private String plainText;
    private String valuePattern;
    private String timestampPattern;

    private String popupToOpen;
    private int rowIndex = -1;
    private List<String> preview;
    private Date endDate;
    private String pageNameKey;
    private String timezone;

    public ArchivedValuesExporterBackingBean(){
        int id = 0;
        for (FieldType type : FieldType.values()) {
            if (type != FieldType.ATTRIBUTE) {
                Field field = new Field(--id, type, null);
                fieldSelect.add(field);
            }
        }
        endDate = new Date();
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
    
    public  void resetFormat() {
        setFormat(new ExportFormat());
        getFormat().setDelimiter(",");
    }
    
    public void resetField(YukonRoundingMode roundingMode) {
        setExportField(new ExportField());
        setRowIndex(-1);
        setSelectedFieldId(0);
        getExportField().setMissingAttributeValue("Leave Blank");
        getExportField().setRoundingMode(roundingMode);
    }
    

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public String getTimezone() {
        return timezone;
    }
    
    public void setTimezone(DateTimeZone dateTimeZone){
        if (timezone == null) {
            DateTime dt = new DateTime(dateTimeZone);
            DateTimeFormatter fmt = DateTimeFormat.forPattern("z");
            timezone = fmt.print(dt);
        }
    }

    public List<String> getPreview() {
        return preview;
    }

    public void setPreview(List<String> preview) {
        this.preview = preview;
    }
}
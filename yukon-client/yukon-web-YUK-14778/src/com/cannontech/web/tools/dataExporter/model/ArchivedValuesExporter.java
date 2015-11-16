package com.cannontech.web.tools.dataExporter.model;

import java.util.Set;

import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.common.bulk.collection.device.model.DeviceCollection;
import com.cannontech.common.pao.attribute.model.Attribute;

public class ArchivedValuesExporter {

    private int formatId;
    private ArchivedValuesExportFormatType archivedValuesExportFormatType;
    private DeviceCollection deviceCollection;
    private Set<Attribute> attributes;
    private DataRange runDataRange;
    private DataRange scheduleDataRange;
    
    public int getFormatId() {
        return formatId;
    }
    public void setFormatId(int formatId) {
        this.formatId = formatId;
    }

    public ArchivedValuesExportFormatType getArchivedValuesExportFormatType() {
        return archivedValuesExportFormatType;
    }
    public void setArchivedValuesExportFormatType(ArchivedValuesExportFormatType archivedValuesExportFormatType) {
        this.archivedValuesExportFormatType = archivedValuesExportFormatType;
    }

    public DeviceCollection getDeviceCollection() {
        return deviceCollection;
    }
    public void setDeviceCollection(DeviceCollection deviceCollection) {
        this.deviceCollection = deviceCollection;
    }

    public Set<Attribute> getAttributes() {
        return attributes;
    }
    
    public Attribute[] getAttributesArray() {
    	return attributes == null? new Attribute[0] : attributes.toArray(new Attribute[attributes.size()]);
    }
    
    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public DataRange getRunDataRange() {
        return runDataRange;
    }
    public void setRunDataRange(DataRange runDataRange) {
        this.runDataRange = runDataRange;
    }
    
    public DataRange getScheduleDataRange() {
        return scheduleDataRange;
    }
    public void setScheduleDataRange(DataRange scheduleDataRange) {
        this.scheduleDataRange = scheduleDataRange;
    }
}
package com.cannontech.web.amr.archivedValuesExporter.model;

import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRange;
import com.cannontech.common.bulk.collection.device.DeviceCollection;
import com.cannontech.common.pao.attribute.model.Attribute;

public class ArchivedValuesExporter {

    private int formatId;
    private ArchivedValuesExportFormatType archivedValuesExportFormatType;
    private DeviceCollection deviceCollection;
    private Attribute attribute;
    private DataRange dataRange;
    
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

    public Attribute getAttribute() {
        return attribute;
    }
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public DataRange getDataRange() {
        return dataRange;
    }
    public void setDataRange(DataRange dataRange) {
        this.dataRange = dataRange;
    }
}
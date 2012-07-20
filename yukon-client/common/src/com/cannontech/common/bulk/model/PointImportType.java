package com.cannontech.common.bulk.model;

import com.cannontech.common.csvImport.ImportFileFormat;
import com.cannontech.common.i18n.DisplayableEnum;

public enum PointImportType implements DisplayableEnum {
    ANALOG(PointImportFormats.ANALOG_POINT_FORMAT, false),
    STATUS(PointImportFormats.STATUS_POINT_FORMAT, false),
    ACCUMULATOR(PointImportFormats.ACCUMULATOR_POINT_FORMAT, false),
    CALC_ANALOG(PointImportFormats.CALC_ANALOG_POINT_FORMAT, true),
    CALC_STATUS(PointImportFormats.CALC_STATUS_POINT_FORMAT, true),
    ;
    
    private ImportFileFormat format;
    private boolean needsCalcFile;
    
    private PointImportType(ImportFileFormat format, boolean needsCalcFile) {
        this.format = format;
        this.needsCalcFile = needsCalcFile;
    }
    
    public ImportFileFormat getFormat() {
        return getFormat(false);
    }
    
    public ImportFileFormat getFormat(boolean ignoreInvalidHeaders) {
        ImportFileFormat returnFormat = format.clone();
        returnFormat.setIgnoreInvalidHeaders(ignoreInvalidHeaders);
        return returnFormat;
    }
    
    public boolean needsCalcFile() {
        return needsCalcFile;
    }
    
    @Override
    public String getFormatKey() {
        return "yukon.web.modules.amr.pointImport.importType." + this.name();
    }
}

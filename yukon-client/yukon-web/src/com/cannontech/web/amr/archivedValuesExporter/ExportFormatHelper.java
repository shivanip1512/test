package com.cannontech.web.amr.archivedValuesExporter;

import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;

public class ExportFormatHelper {
    public static void preloadFormat(ArchivedValuesExporterBackingBean bean) {
        bean.setFormat(new ExportFormat());
        bean.getFormat().setDelimiter(",");
    }
    
    public static void preloadField(ArchivedValuesExporterBackingBean bean){
        bean.setExportField(new ExportField());
        bean.setRowIndex(-1);
        bean.setSelectedFieldId(0);
        bean.getExportField().setMissingAttributeValue("Leave Blank");        
    }
}

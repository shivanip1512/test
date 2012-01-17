package com.cannontech.web.amr.archivedValuesExporter;

import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.common.login.ClientSession;
import com.cannontech.roles.yukon.BillingRole;

public class ExportFormatHelper {
    public static void defaultFormat(ArchivedValuesExporterBackingBean bean) {
        bean.setFormat(new ExportFormat());
        bean.getFormat().setDelimiter(",");
    }
    
  public static void defaultField(ArchivedValuesExporterBackingBean bean){
        bean.setExportField(new ExportField());
        bean.setRowIndex(-1);
        bean.setSelectedFieldId(0);
        bean.getExportField().setMissingAttributeValue("Leave Blank"); 
        String roundingMode = ClientSession.getInstance().getRolePropertyValue(BillingRole.DEFAULT_ROUNDING_MODE);
        bean.getExportField().setRoundingMode(roundingMode);
    }
    

}

package com.cannontech.common.scheduledFileImport;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

/**
 * This enum defines the type of schedule data import type.
 *
 */
public enum ScheduledImportType implements DisplayableEnum, DatabaseRepresentationSource {

    ASSET_IMPORT("AssetImport");

    private String importType;

    private ScheduledImportType(String importType) {
        this.importType = importType;
    }

    public String getImportType() {
        return importType;
    }

    @Override
    public String getFormatKey() {
        return "yukon.web.modules.operator.scheduledData.importType." + this.name();
    }

    @Override
    public String getDatabaseRepresentation() {
        return getImportType();
    }

    public static ScheduledImportType fromName(String type) {
        for (ScheduledImportType importType : values()) {
            if (importType.getImportType().equalsIgnoreCase(type)) {
                return importType;
            }
        }
        return null;
    }
}

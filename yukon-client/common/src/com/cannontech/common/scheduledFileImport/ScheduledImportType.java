package com.cannontech.common.scheduledFileImport;

import java.util.HashMap;

import com.cannontech.common.i18n.DisplayableEnum;
import com.cannontech.common.util.DatabaseRepresentationSource;

/**
 * This enum defines the type of schedule data import type.
 *
 */
public enum ScheduledImportType implements DisplayableEnum, DatabaseRepresentationSource {

    ASSET_IMPORT("AssetImport");

    private static HashMap<String, ScheduledImportType> importTypeMap;
    static {
        importTypeMap = new HashMap<>();
        importTypeMap.put(ASSET_IMPORT.getImportType(), ASSET_IMPORT);
    }
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

    public static ScheduledImportType fromImportTypeMap(String importType) {
        return importTypeMap.get(importType);
    }
}

package com.cannontech.common.bulk.service;

import java.util.Map;

import com.cannontech.common.bulk.model.PointImportType;
import com.cannontech.common.csvImport.ImportData;
import com.cannontech.common.point.PointCalculation;
import com.cannontech.user.YukonUserContext;

public interface PointImportService {
    
    /**
     * Begins importing the data as an import of the specified point type.
     */
    public String startImport(ImportData data, PointImportType importType, Map<String, PointCalculation> calcMap, YukonUserContext userContext);
}

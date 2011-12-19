package com.cannontech.amr.archivedValueExporter.dao;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;

public interface ArchiveValuesExportAttributeDao {
    
    public ExportAttribute create(ExportAttribute attribute);
        
    public boolean deleteByFormatId(int formatId);
        
    public List<ExportAttribute> getByFormatId(int formatId);
}

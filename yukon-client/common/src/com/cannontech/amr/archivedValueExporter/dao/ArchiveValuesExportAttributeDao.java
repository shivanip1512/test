package com.cannontech.amr.archivedValueExporter.dao;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;

public interface ArchiveValuesExportAttributeDao {
    
    public ExportAttribute create(ExportAttribute attribute);
    
    public ExportAttribute update(ExportAttribute format);
    
    public boolean removeByFormatId(int formatId);
        
    public List<ExportAttribute> create(List<ExportAttribute> attributes);
    
    public List<ExportAttribute> getByFormatId(int formatId);
}

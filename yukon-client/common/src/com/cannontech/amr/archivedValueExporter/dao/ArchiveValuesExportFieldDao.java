package com.cannontech.amr.archivedValueExporter.dao;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportField;

public interface ArchiveValuesExportFieldDao {
    
    public List<ExportField> create(List<ExportField> fields) ;
    
    public boolean removeByFormatId(int formatId);
    
    public List<ExportField> getByFormatId(int formatId);
}

package com.cannontech.amr.archivedValueExporter.dao;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportFormat;


public interface ArchiveValuesExportFormatDao {
    public ExportFormat create(ExportFormat format);

    public ExportFormat update(ExportFormat format);

    public void remove(int formatId);

    public ExportFormat getByFormatId(int formatId);

    public List<ExportFormat> getAllFormats();
    
    public ExportFormat getByFormatName(String formatName);
    
}

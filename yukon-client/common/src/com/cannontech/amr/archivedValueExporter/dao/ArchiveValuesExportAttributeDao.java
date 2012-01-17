package com.cannontech.amr.archivedValueExporter.dao;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;

public interface ArchiveValuesExportAttributeDao {


    /**
     * Creates an Export Attribute.
     * 
     * @param attribute
     * @return
     */
    public ExportAttribute create(ExportAttribute attribute);

  
    /**
     * Delete All Export Attributes by format id.
     * 
     * @param formatId
     * @return true, if successful
     */
    public boolean deleteByFormatId(int formatId);
        

    /**
     * Gets a List of Export Attributes by format id.
     * 
     * @param formatId
     * @return
     */
    public List<ExportAttribute> getByFormatId(int formatId);
}

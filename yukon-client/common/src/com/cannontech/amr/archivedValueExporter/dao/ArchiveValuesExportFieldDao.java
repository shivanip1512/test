
package com.cannontech.amr.archivedValueExporter.dao;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportField;

public interface ArchiveValuesExportFieldDao {
    
    /**
     * Creates Export Fields.
     * 
     * @param fields
     * @return
     */
    public List<ExportField> create(List<ExportField> fields) ;
        
    /**
     * Gets Export Fields by format id.
     * 
     * @param formatId
     * @return 
     */
    public List<ExportField> getByFormatId(int formatId);

    /**
     * Delete All Export Fields by format id.
     * 
     * @param formatId
     * @return true, if successful
     */
    boolean deleteByFormatId(int formatId);
}


package com.cannontech.amr.archivedValueExporter.dao;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportFormat;


public interface ArchiveValuesExportFormatDao {
    
    /**
     * Creates the Export Format.
     * 
     * @param format
     * @return
     */
    public ExportFormat create(ExportFormat format);

    /**
     * Updates ExportFormat.
     * 
     * @param format
     * @return
     */
    public ExportFormat update(ExportFormat format);
    
    /**
     * Gets Format by format id.
     * 
     * @param formatId
     * @return
     */
    public ExportFormat getByFormatId(int formatId);

    /**
     * Gets all formats.
     * 
     * @param
     * @return
     */
    
    public List<ExportFormat> getAllFormats();
    
    /**
     * Gets the by format name.
     * 
     * @param formatName
     * @return
     */
    public ExportFormat findByFormatName(String formatName);

    /**
     * Deletes Format by Format Id.
     * 
     * @param
     */
    public void delete(int formatId);

    /** Returns the name of the format */
    public String getName(int id);
    
}

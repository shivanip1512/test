
package com.cannontech.amr.archivedValueExporter.dao;

import java.util.List;

import com.cannontech.amr.archivedValueExporter.model.ExportAttribute;
import com.cannontech.amr.archivedValueExporter.model.ExportField;
import com.cannontech.amr.archivedValueExporter.model.ExportFormat;
import com.cannontech.amr.archivedValueExporter.model.Field;
import com.cannontech.user.YukonUserContext;


public interface ArchiveValuesExportFormatDao {
    
    /**
     * Creates the Export Format.
     */
    public ExportFormat create(ExportFormat format);

    /**
     * Updates ExportFormat.
     */
    public ExportFormat update(ExportFormat format);
    
    /**
     * Gets Format by format id.
     */
    public ExportFormat getByFormatId(int formatId, YukonUserContext context);

    /**
     * Gets all formats.
     *
     */
    
    public List<ExportFormat> getAllFormats();
    
    /**
     * Gets the by format name. 
     */
    public ExportFormat findByFormatName(String formatName, YukonUserContext context);

    /**
     * Deletes Format by Format Id.
     * 
     */
    public void delete(int formatId);

    /** Returns the name of the format */
    public String getName(int id);

    /**
     * Creates an attribute description and adds it to attribute
     */
    void addAttributeDescription(ExportAttribute attribute, YukonUserContext context);
    
    /**
     * Creates a field description and adds it to field
     */
    void addFieldDescription(ExportField field, YukonUserContext context);
    
    /**
     * Creates a field description and adds it to field
     */
    void addFieldDescription(Field field, YukonUserContext context);
}

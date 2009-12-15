package com.cannontech.common.databaseMigration.service;

import org.jdom.Element;

import com.cannontech.common.databaseMigration.bean.data.DataTable;

public interface ExportXMLGeneratorService {

    /**
     * The buildXmlElement method takes a list of dataTables, which are representations
     * of database tables for a given object, and generates an Xml shell and then 
     * generates each DataTable object into an item.
     * 
     * <data>
     *     <configuration name="label">
     *         <item/>
     *         <item/>
     *     </configuration>
     * </data>
     * 
     * @param data
     * @param label
     * @return
     */
    public Element buildXmlElement(Iterable<DataTable> data, String label);
            
}

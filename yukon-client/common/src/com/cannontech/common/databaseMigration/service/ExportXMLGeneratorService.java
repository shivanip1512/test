package com.cannontech.common.databaseMigration.service;

import org.jdom.Element;

import com.cannontech.common.databaseMigration.bean.data.DataTable;

public interface ExportXMLGeneratorService {

    public Element buildXMLFile(Iterable<DataTable> data, String label);
            
}

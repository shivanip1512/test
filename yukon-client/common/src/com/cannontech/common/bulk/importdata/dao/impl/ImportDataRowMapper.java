package com.cannontech.common.bulk.importdata.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.simple.ParameterizedRowMapper;

import com.cannontech.database.db.importer.ImportData;

public class ImportDataRowMapper implements ParameterizedRowMapper<ImportData> {
    
    public ImportData mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        String address = rs.getString("Address").trim();
        String name = rs.getString("Name").trim();
        String routeName = rs.getString("RouteName").trim();
        String meterNumber = rs.getString("MeterNumber").trim();
        String collectionGrp = rs.getString("CollectionGrp").trim();
        String altGrp = rs.getString("AltGrp").trim();
        String templateName = rs.getString("TemplateName").trim();
        String billGrp = rs.getString("BillGrp").trim();
        String substationName = rs.getString("SubstationName").trim();
        
        return new ImportData( address, name,
                               routeName, meterNumber, collectionGrp, altGrp, 
                               templateName, billGrp, substationName );
    }

}

package com.cannontech.common.bulk.importdata.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.cannontech.database.db.importer.ImportPendingComm;

public class ImportPendingCommRowMapper implements RowMapper<ImportPendingComm> {
    
    public ImportPendingComm mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        Integer deviceID = rs.getInt("DeviceID");
        String address = rs.getString("Address").trim();
        String name = rs.getString("Name").trim();
        String routeName = rs.getString("RouteName").trim();
        String meterNumber = rs.getString("MeterNumber").trim();
        String collectionGrp = rs.getString("CollectionGrp").trim();
        String altGrp = rs.getString("AltGrp").trim();
        String templateName = rs.getString("TemplateName").trim();
        String billGrp = rs.getString("BillGrp").trim();
        String substationName = rs.getString("SubstationName").trim();

        return new ImportPendingComm( deviceID, address, name, routeName, 
                                                      meterNumber, collectionGrp, altGrp, 
                                                      templateName, billGrp, substationName);
    }

}

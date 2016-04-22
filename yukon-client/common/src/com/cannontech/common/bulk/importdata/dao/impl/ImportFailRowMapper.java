package com.cannontech.common.bulk.importdata.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.springframework.jdbc.core.RowMapper;

import com.cannontech.database.db.importer.ImportFail;

public class ImportFailRowMapper implements RowMapper<ImportFail> {
    
    public ImportFail mapRow(ResultSet rs, int rowNum) throws SQLException {
        
        String address = rs.getString("Address").trim();
        String name = rs.getString("Name").trim();
        String routeName = rs.getString("RouteName").trim();
        String meterNumber = rs.getString("MeterNumber").trim();
        String collectionGrp = rs.getString("CollectionGrp").trim();
        String altGrp = rs.getString("AltGrp").trim();
        String templateName = rs.getString("TemplateName").trim();
        String errorMsg = rs.getString("ErrorMsg").trim();
        Date dateTime = rs.getTimestamp("DateTime");
        String billGrp = rs.getString("BillGrp").trim();
        String substationName = rs.getString("SubstationName").trim();
        String failType = rs.getString("FailType").trim();
        
        return new ImportFail( address, name, routeName, meterNumber, 
                               collectionGrp, altGrp, templateName, 
                               errorMsg, dateTime, billGrp, 
                               substationName, failType);
    }

}

package com.cannontech.database.vendor;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

public class MetaDataDatabaseVendorResolver implements DatabaseVendorResolver {
    private DataSource dataSource;

    @Override
    public DatabaseVendor getDatabaseVendor() {
        try {
            DatabaseVendor result = (DatabaseVendor) JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() {
                @Override
                public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                    return DatabaseVendor.MS2008;
                }
            });
            return result;
        } catch (MetaDataAccessException e) {
            throw new RuntimeException("Unable to determine DB Vendor");
        }

    }
    
    @Resource(name="yukonDataSource")
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

}

package com.cannontech.database.vendor;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

public class MetaDataDatabaseVendorResolver implements DatabaseVendorResolver {
    private DatabaseVendor databaseVendor;

    @Override
    public DatabaseVendor getDatabaseVendor() {
        return databaseVendor;
    }
    
    @Resource(name="yukonDataSource")
    public void setDataSource(DataSource dataSource) {
        try {
            DatabaseVendor result = (DatabaseVendor) JdbcUtils.extractDatabaseMetaData(dataSource, new DatabaseMetaDataCallback() {
                @Override
                public Object processMetaData(DatabaseMetaData dbmd) throws SQLException, MetaDataAccessException {
                    String vendorText = dbmd.getDatabaseProductName();
                    String productVersion = dbmd.getDatabaseProductVersion();
                    
                    return DatabaseVendor.getDatabaseVender(vendorText, productVersion);
                }
            });
            this.databaseVendor = result;
        } catch (MetaDataAccessException e) {
            throw new RuntimeException("Unable to determine DB Vendor");
        }
    }

}

package com.cannontech.database.vendor;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import com.cannontech.clientutils.YukonLogManager;

public class MetaDataDatabaseVendorResolver implements DatabaseVendorResolver {
    private Logger logger = YukonLogManager.getLogger(MetaDataDatabaseVendorResolver.class);
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
                    String vendorName = dbmd.getDatabaseProductName();
                    int databaseMajorVersion = dbmd.getDatabaseMajorVersion();
                    int databaseMinorVersion = dbmd.getDatabaseMinorVersion();
                    
                    return DatabaseVendor.getDatabaseVendor(vendorName, 
                                                            databaseMajorVersion,
                                                            databaseMinorVersion);

                }
            });
            this.databaseVendor = result;
            logger.info("Your database version is: " + databaseVendor.toString());
            
        } catch (MetaDataAccessException e) {
            throw new RuntimeException("Unable to determine DB Vendor");
        }
    }

}

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
                    String vendorText = dbmd.getDatabaseProductName();
                    String productVersion = dbmd.getDatabaseProductVersion();
                    
                    for(DatabaseVendor databaseVendor: DatabaseVendor.values())
                        if ((vendorText.equals(databaseVendor.getVenderText())) &&
                            (productVersion.startsWith(databaseVendor.getProductVersionPrefix()))) {
                            return databaseVendor;
                        }
                    
                    logger.warn("Your database is not officially supported by Yukon: " + 
                                vendorText + ", " + productVersion);
                    return DatabaseVendor.UNKNOWN; 
                    
                }
            });
            this.databaseVendor = result;
            logger.info("Your database version is: " + databaseVendor.toString());
            
        } catch (MetaDataAccessException e) {
            throw new RuntimeException("Unable to determine DB Vendor");
        }
    }

}

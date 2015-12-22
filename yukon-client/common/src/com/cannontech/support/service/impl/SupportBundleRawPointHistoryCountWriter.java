package com.cannontech.support.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.vendor.DatabaseVendorResolver;

public class SupportBundleRawPointHistoryCountWriter extends SupportBundleSqlWriter {

    @Autowired
    private DatabaseVendorResolver dbVendorResolver;

    @Override
    protected SqlFragmentSource getSqlFragmentSource() {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (dbVendorResolver.getDatabaseVendor().isSqlServer()) {
            sql.append("DECLARE @TableName sysname");
            sql.append("SET @TableName = 'RawPointHistory'");
            sql.append("SELECT SUM(PART.rows) AS rows");
            sql.append("FROM sys.tables TBL");
            sql.append("INNER JOIN sys.partitions PART ON TBL.object_id = PART.object_id");
            sql.append("INNER JOIN sys.indexes IDX ON PART.object_id = IDX.object_id");
            sql.append("AND PART.index_id = IDX.index_id");
            sql.append("WHERE TBL.name = @TableName");
            sql.append("AND IDX.index_id < 2");
        } else if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            sql.append("SELECT COUNT(*)");
            sql.append("FROM RawPointHistory");
        }
        return sql;
    }

    @Override
    protected String getZipFilename() {
        return "RawPointHistoryCount.csv";
    }
}

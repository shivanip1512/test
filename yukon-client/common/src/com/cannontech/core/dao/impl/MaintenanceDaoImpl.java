package com.cannontech.core.dao.impl;

import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.MaintenanceDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendorResolver;


public class MaintenanceDaoImpl implements MaintenanceDao {
    
    private static final Logger log = YukonLogManager.getLogger(MaintenanceDaoImpl.class);
    private static final int BATCH_SIZE = 1000000;
    
    @Autowired private YukonJdbcTemplate yukonTemplate;
    @Autowired private DatabaseVendorResolver dbVendorResolver;
    
    public int archiveRph(DateTime processEndTime, Date deleteUpto) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        if (dbVendorResolver.getDatabaseVendor().isSqlServer()) {
            sql.append("DELETE TOP (");
            sql.append(BATCH_SIZE);
            sql.append(")");
            sql.append("FROM RAWPOINTHISTORY ");
            sql.append("WHERE TIMESTAMP < '");
            sql.append(deleteUpto);
            sql.append("'");
            
        } else if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            sql.append("DELETE FROM RawPointHistory");
            sql.append("WHERE changeId IN");
            sql.append("  (SELECT changeId FROM RawPointHistory");
            sql.append("  WHERE timestamp > '");
            sql.append(deleteUpto);
            sql.append("'  FETCH FIRST");
            sql.append(BATCH_SIZE);
            sql.append("ROWS ONLY);");
        }

        SqlFragmentSource executeSql = buildBatchSql(sql, processEndTime);
        log.debug(executeSql);
        return yukonTemplate.update(executeSql);
    }
    
    /*
     * Create a batch which executes until the processEndTime is reached or there are no records to delete.
     * The sql passed as mainQuery, will the actual query that will be running in the batch.
     */
    private SqlFragmentSource buildBatchSql(SqlFragmentSource mainQuery, DateTime processEndTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        if (dbVendorResolver.getDatabaseVendor().isSqlServer()) {
            sql.append("DECLARE");
            sql.append("@timeAvailable int = 0, @deletedRows int = 0, @lastDeleted int = 1");
            sql.append("   SELECT @timeAvailable =");
            sql.append(timeCheckSql(processEndTime));
            sql.append("   SET NOCOUNT OFF");
            sql.append("   WHILE @timeAvailable = 0  AND (SELECT @lastDeleted) != 0");
            sql.append("   BEGIN");
            sql.append("   BEGIN TRANSACTION");
            sql.append(mainQuery);
            sql.append("SELECT @lastDeleted = @@ROWCOUNT");
            sql.append("SELECT @deletedRows = @deletedRows + @lastDeleted");
            sql.append("   COMMIT TRANSACTION");
            sql.append("   SELECT @timeAvailable =");
            sql.append(timeCheckSql(processEndTime));
            sql.append("   END");
            sql.append("SELECT @deletedRows;");
            
        } else if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            sql.append("DECLARE");
            sql.append("timeAvailable NUMBER := 0;");
            sql.append("deletedRows NUMBER := 0; ");
            sql.append("lastDeleted NUMBER := 1; ");
            sql.append("  BEGIN");
            sql.append(timeCheckSql(processEndTime));
            sql.append("  WHILE timeAvailable = 0 AND lastDeleted != 0 ");
            sql.append("  LOOP");
            sql.append("    BEGIN");
            sql.append(mainQuery);
            sql.append("    END;");
            sql.append("    COMMIT;");
            sql.append("    lastDeleted := sql%rowcount;");
            sql.append("    deletedRows := deletedRows + lastDeleted;");
            sql.append(timeCheckSql(processEndTime));
            sql.append("  END LOOP;");
            sql.append("  END;");
        }
        return sql;
    }

    /*
     * Sql to check if there is time available for the batch.
     */
    private SqlFragmentSource timeCheckSql(DateTime processEndTime) {
        Timestamp ts = new Timestamp(processEndTime.getMillis());
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        if (dbVendorResolver.getDatabaseVendor().isSqlServer()) {
            sql.append("(SELECT CASE WHEN (SELECT CURRENT_TIMESTAMP) >");
            sql.append("'");
            sql.append(ts);
            sql.append("'");
            sql.append("THEN 0 ELSE 1 END)");
            
        } else if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            sql.append("SELECT (SELECT CASE WHEN (SELECT TO_CHAR(CURRENT_TIMESTAMP, 'YYYY-MM-DD HH:MI:SS') FROM DUAL) >");
            sql.append("'");
            sql.append(ts);
            sql.append("'");
            sql.append("THEN 0 ELSE 1 END FROM DUAL) INTO timeAvailable FROM DUAL ;");
        }
        return sql;
    }
}

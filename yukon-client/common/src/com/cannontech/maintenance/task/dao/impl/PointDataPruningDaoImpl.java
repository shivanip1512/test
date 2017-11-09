package com.cannontech.maintenance.task.dao.impl;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.maintenance.task.dao.PointDataPruningDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendorResolver;

public class PointDataPruningDaoImpl implements PointDataPruningDao {

    private static final Logger log = YukonLogManager.getLogger(PointDataPruningDaoImpl.class);
    private static final int BATCH_SIZE = 100000;

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DatabaseVendorResolver dbVendorResolver;

    @Override
    public int deletePointData(Instant processEndTime, Instant deleteUpto) {
        SqlFragmentSource deleteSql = buildDeleteQuery(deleteUpto);
        SqlFragmentSource executeSql = buildBatchSql(deleteSql, processEndTime);
        log.debug(executeSql);
        return jdbcTemplate.update(executeSql);
    }

    @Override
    public int deletePointData(Instant deleteUpto) {
        SqlFragmentSource deleteSql = buildDeleteQuery(deleteUpto);
        log.debug(deleteSql);
        return jdbcTemplate.update(deleteSql);
    }

    private SqlFragmentSource buildDeleteQuery(Instant deleteUpto) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            sql.append("DELETE FROM RawPointHistory");
            sql.append("WHERE ChangeId IN");
            sql.append("  (SELECT ChangeId FROM RawPointHistory");
            sql.append("  WHERE Timestamp").lt(deleteUpto);
            sql.append("  AND PointId IN (");
            sql.append("    SELECT PointId FROM");
            sql.append("    Point p JOIN yukonPaObject pao");
            sql.append("    ON p.paObjectId = pao.paObjectId)");
            sql.append("  AND ROWNUM").lt(BATCH_SIZE);
            sql.append("  );");
        } else {
            sql.append("DELETE FROM RawPointHistory");
            sql.append("WHERE ChangeId IN (");
            sql.append("  SELECT TOP (");
            sql.append(BATCH_SIZE);
            sql.append("  ) ChangeId");
            sql.append("  FROM RawPointHistory");
            sql.append("  WITH (NOLOCK)");
            sql.append("  WHERE Timestamp").lt(deleteUpto);
            sql.append("  AND PointId IN (");
            sql.append("    SELECT PointId FROM");
            sql.append("    Point p JOIN yukonPaObject pao");
            sql.append("    ON p.paObjectId = pao.paObjectId)");
            sql.append("  )");
        }
        return sql;
    }

    /*
     * Create a batch which executes until the processEndTime is reached or there are no records to delete.
     * The sql passed as mainQuery, will the actual query that will be running in the batch.
     */
    private SqlFragmentSource buildBatchSql(SqlFragmentSource mainQuery, Instant processEndTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        if (dbVendorResolver.getDatabaseVendor().isOracle()) {
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
        } else {
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
        }
        return sql;
    }

    /*
     * Sql to check if there is time available for the batch.
     */
    private SqlFragmentSource timeCheckSql(Instant processEndTime) {
        Timestamp ts = new Timestamp(processEndTime.getMillis());
        SqlStatementBuilder sql = new SqlStatementBuilder();

        if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            sql.append("SELECT (");
            sql.append("  SELECT CASE WHEN");
            sql.append("  (SELECT TO_CHAR(SYS_EXTRACT_UTC(SYSTIMESTAMP), 'YYYY-MM-DD HH:MI:SS') FROM DUAL)").lt(ts);
            sql.append("  THEN 0 ELSE 1 END FROM DUAL) INTO timeAvailable FROM DUAL;");
        } else {
            sql.append("(SELECT CASE WHEN (SELECT GETUTCDATE())").lt(ts);
            sql.append("THEN 0 ELSE 1 END)");
        }
        return sql;
    }
}

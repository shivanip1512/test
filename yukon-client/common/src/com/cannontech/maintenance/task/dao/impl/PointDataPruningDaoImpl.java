package com.cannontech.maintenance.task.dao.impl;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Range;
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
            sql.append("  )");
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

    @Override
    public int deleteDuplicatePointData(Range<Instant> dateRange, boolean noLockRequired) {
        log.debug("Query execution started for date range - " + dateRange);
        SqlStatementBuilder deleteDuplicatePointDataQuery =
            buildDeleteDuplicatePointDataQuery(dateRange, noLockRequired);
        int rowsDeleted = jdbcTemplate.update(deleteDuplicatePointDataQuery);
        log.debug("Query execution finished for date range - " + dateRange);
        log.debug("Rows deleted for this range = " + rowsDeleted);
        return rowsDeleted;
    }

    private SqlStatementBuilder buildDeleteDuplicatePointDataQuery(Range<Instant> dateRange, boolean noLockRequired) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        if (dbVendorResolver.getDatabaseVendor().isOracle()) {
            sql.append("DELETE");
            sql.append("FROM RAWPOINTHISTORY");
            sql.append("WHERE CHANGEID IN (");
            sql.append(    "SELECT CHANGEID ");
            sql.append(    "FROM (");
            sql.append(        "SELECT CHANGEID, ROW_NUMBER() ");
            sql.append(          "OVER (PARTITION BY PointId, Value, Timestamp, Quality ORDER BY ChangeId) RN ");
            sql.append(        "FROM RAWPOINTHISTORY");
            sql.append(        "WHERE Timestamp").gte(dateRange.getMin());
            sql.append(          "AND Timestamp").lte(dateRange.getMax());
            sql.append(          "AND PointId IN (");
            sql.append(              "SELECT PointId ");
            sql.append(              "FROM Point p ");
            sql.append(                "JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId)");
            sql.append(    ") ");
            sql.append(    "WHERE RN > 1");
            sql.append(")");
        } else {
            sql.append("BEGIN");
            sql.append("BEGIN TRANSACTION;");
            sql.append("WITH CTE AS (");
            sql.append("SELECT CHANGEID, ROW_NUMBER() ");
            sql.append(  "OVER (PARTITION BY PointId, Value, Timestamp, Quality ORDER BY ChangeId) RN ");
            sql.append("FROM RAWPOINTHISTORY ");
            if (noLockRequired) {
                sql.append("WITH (NOLOCK)");
            }
            sql.append("WHERE Timestamp").gte(dateRange.getMin());
            sql.append(  "AND Timestamp").lte(dateRange.getMax());
            sql.append(  "AND PointId IN (");
            sql.append(      "SELECT PointId ");
            sql.append(      "FROM Point p ");
            sql.append(        "JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId)");
            sql.append(")");
            sql.append("DELETE");
            sql.append("FROM CTE");
            sql.append("WHERE RN > 1");
            sql.append("COMMIT TRANSACTION");
            sql.append("END;");
        }
        return sql;
    }
}

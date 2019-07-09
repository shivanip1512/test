package com.cannontech.maintenance.task.dao.impl;

import org.apache.logging.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.TransientDataAccessResourceException;

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
        int rowDeleted = 0;
        try {
            rowDeleted = jdbcTemplate.queryForInt(deleteSql);
        } catch (TransientDataAccessResourceException e) {
            log.error("Error when deleting RPH data " + e);
        }
        return rowDeleted;
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
            sql.append("BEGIN");
            sql.append(    "DECLARE @TotalDeleted int");
            sql.append(    "SET @TotalDeleted = 0;");
            sql.append(    "IF OBJECT_ID('#TempPruning', 'U') IS NOT NULL");
            sql.append(    "DROP TABLE #TempPruning ");
            sql.append(    "SELECT CHANGEID INTO #TempPruning");
            sql.append(    "FROM (");
            sql.append(    "SELECT TOP (");
            sql.append(    BATCH_SIZE);
            sql.append(    ") ChangeId");
            sql.append(    "FROM RawPointHistory");
            sql.append(    "WITH (NOLOCK)");
            sql.append(    "WHERE Timestamp").lt(deleteUpto);
            sql.append(    "AND PointId IN (");
            sql.append(        "SELECT PointId FROM");
            sql.append(        "Point p JOIN yukonPaObject pao");
            sql.append(        "ON p.paObjectId = pao.paObjectId)");
            sql.append(    ") a");
            sql.append(    "DECLARE @Rowcount INT = 1");
            sql.append(    "WHILE @Rowcount > 0 ");
            sql.append(    "BEGIN");
            sql.append(    "BEGIN TRANSACTION");
            sql.append(        "DELETE FROM RAWPOINTHISTORY");
            sql.append(        "WHERE CHANGEID IN (");
            sql.append(            "SELECT TOP(10000) changeid");
            sql.append(            "FROM #TempPruning");
            sql.append(            "ORDER BY CHANGEID)");
            sql.append(        "SET @TotalDeleted = (select @TotalDeleted) + (select @@ROWCOUNT)");
            sql.append(        "DELETE FROM #TempPruning");
            sql.append(        "WHERE CHANGEID IN (");
            sql.append(            "SELECT TOP(10000) changeid");
            sql.append(            "FROM #TempPruning");
            sql.append(            "ORDER BY CHANGEID)");
            sql.append(        "SET @Rowcount = @@ROWCOUNT");
            sql.append(    "COMMIT TRANSACTION");
            sql.append(    "END");
            sql.append(        "DROP TABLE #TempPruning");
            sql.append(        "SELECT @TotalDeleted AS totaldeleted");
            sql.append(    "END");
        }
        return sql;
    }

    @Override
    public int deleteDuplicatePointData(Range<Instant> dateRange, boolean noLockRequired) {
        log.debug("Query execution started for date range - " + dateRange);
        SqlStatementBuilder deleteDuplicatePointDataQuery;
        int rowsDeleted = 0;
        try {
            if (dbVendorResolver.getDatabaseVendor().isOracle()) {
                deleteDuplicatePointDataQuery = buildDeleteDuplicatePointDataOracleQuery(dateRange);
                rowsDeleted = jdbcTemplate.update(deleteDuplicatePointDataQuery);
            } else {
                deleteDuplicatePointDataQuery = buildDeleteDuplicatePointDataMSSQLQuery(dateRange, noLockRequired);
                rowsDeleted = jdbcTemplate.queryForInt(deleteDuplicatePointDataQuery);
            }
        } catch (TransientDataAccessResourceException e) {
            log.error("Error when deleting duplicate data " + e);
        }
        log.debug("Query execution finished for date range - " + dateRange);
        log.debug("Rows deleted for this range = " + rowsDeleted);        
        return rowsDeleted;
    }

    private SqlStatementBuilder buildDeleteDuplicatePointDataOracleQuery(Range<Instant> dateRange) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

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
            
        return sql;
    }
    
    private SqlStatementBuilder buildDeleteDuplicatePointDataMSSQLQuery(Range<Instant> dateRange, boolean noLockRequired) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("BEGIN");
        sql.append("DECLARE @TotalDeleted int");
        sql.append("SET @TotalDeleted = 0;");
        sql.append(    "IF OBJECT_ID('#TempRph', 'U') IS NOT NULL");
        sql.append(    "DROP TABLE #TempRph");
        sql.append(    "SELECT CHANGEID INTO #TempRph");
        sql.append(    "FROM (");
        sql.append(        "SELECT CHANGEID, ROW_NUMBER()");
        sql.append(        "OVER (PARTITION BY PointId, Value, Timestamp, Quality ORDER BY ChangeId) RN");
        sql.append(        "FROM RAWPOINTHISTORY");
        if (noLockRequired) {
            sql.append(    "WITH (NOLOCK)");
        }
        sql.append(        "WHERE Timestamp").gte(dateRange.getMin());
        sql.append(        "AND Timestamp").lte(dateRange.getMax());
        sql.append(        "AND PointId IN (");
        sql.append(            "SELECT PointId ");
        sql.append(            "FROM Point p ");
        sql.append(            "JOIN YukonPaobject pao ON p.PaobjectId = pao.PaobjectId)");
        sql.append(    ") a  WHERE RN > 1");

        sql.append(    "DECLARE @Rowcount INT = 1");
        sql.append(    "WHILE @Rowcount > 0");
        sql.append(    "BEGIN");
        sql.append(    "BEGIN TRANSACTION");
        sql.append(        "DELETE");
        sql.append(        "FROM RAWPOINTHISTORY WHERE CHANGEID IN");
        sql.append(        "(SELECT TOP(10000) changeid FROM #TempRph ORDER BY CHANGEID)");
        sql.append(    "SET @TotalDeleted = (SELECT @TotalDeleted) + (SELECT @@ROWCOUNT)");
            
        sql.append(        "DELETE");
        sql.append(        "FROM #TempRph WHERE CHANGEID IN");
        sql.append(         "(SELECT TOP(10000) changeid FROM #TempRph ORDER BY CHANGEID)");
        sql.append(    "SET @Rowcount = @@ROWCOUNT");
        sql.append(    "COMMIT TRANSACTION");
        sql.append(    "END");
        sql.append(    "DROP TABLE #TempRph");
        sql.append(    "SELECT @TotalDeleted AS totaldeleted");
        sql.append("END");
            
        return sql;
    }
}

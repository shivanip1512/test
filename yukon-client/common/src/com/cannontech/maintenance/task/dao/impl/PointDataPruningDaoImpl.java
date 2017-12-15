package com.cannontech.maintenance.task.dao.impl;

import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.maintenance.task.dao.PointDataPruningDao;

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
}

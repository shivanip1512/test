package com.cannontech.yukon.server.cache;

import java.sql.SQLException;

import java.util.Date;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.device.Device;
import com.cannontech.spring.YukonSpringHook;

public class SystemPointLoader implements Runnable {
    private List<LitePoint> allPoints = null;

    public SystemPointLoader(List<LitePoint> pointArray) {
        super();
        this.allPoints = pointArray;
    }

    private static final YukonRowMapper<LitePoint> litePointRowMapper = new YukonRowMapper<LitePoint>() {
        @Override
        public LitePoint mapRow(YukonResultSet rset) throws SQLException {
            int pointID = rset.getInt("PointId");
            String pointName = rset.getString("PointName");
            PointType pointType = rset.getEnum("PointType", PointType.class);
            int paobjectID = rset.getInt("PaobjectId");
            int pointOffset = rset.getInt("PointOffset");
            int stateGroupID = rset.getInt("StateGroupId");
            String formula = rset.getString("Formula");
            Integer uofmID = rset.getNullableInt("UomId");
            if (uofmID == null) { // if uomid is null, set it to an INVALID int
                uofmID = UnitOfMeasure.INVALID.getId();
            }

            // process all the bit mask tags here
            long tags = LitePoint.POINT_UOFM_GRAPH;
            if ("usage".equalsIgnoreCase(formula)) {
                tags = LitePoint.POINT_UOFM_USAGE;
            }

            LitePoint litePoint = new LitePoint(pointID, pointName, pointType.getPointTypeId(), paobjectID, pointOffset, stateGroupID, tags, uofmID);
            return litePoint;
        };
    };

    public void run() {
        Date timerStart = new Date();

        YukonJdbcTemplate jdbcTemplate = YukonSpringHook.getBean(YukonJdbcTemplate.class);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT p.PointId, PointName, PointType, PaobjectId, PointOffset, StateGroupId, um.Formula, um.UomId");
        sql.append("FROM Point p LEFT JOIN PointUnit Pu ON p.PointId = pu.PointId");
        sql.append("LEFT JOIN UnitMeasure um on pu.UomId = um.UomId");
        sql.append("WHERE PaobjectId").eq(Device.SYSTEM_DEVICE_ID);
        sql.append("AND PointType").neq_k(PointType.System);
        sql.append("ORDER BY PaobjectId, PointOffset");

        allPoints.addAll(jdbcTemplate.query(sql, litePointRowMapper));

        CTILogger.info((new Date().getTime() - timerStart.getTime()) * .001 + " Secs for PointLoader (" + allPoints.size() + " found)");
    }
}
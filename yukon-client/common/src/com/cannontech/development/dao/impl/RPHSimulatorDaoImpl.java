package com.cannontech.development.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.point.PointQuality;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendorResolver;
import com.cannontech.development.dao.RPHSimulatorDao;
import com.google.common.collect.Sets;

public class RPHSimulatorDaoImpl implements RPHSimulatorDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private DatabaseVendorResolver dbVendorResolver;
    private static final Set<String> statusPoints = Sets.newHashSet("Status", "StatusOutput", "CalcStatus");

    @Transactional
    @Override
    public void insertPointData(List<Integer> devicesId, String type, double valueLow, double valueHigh, Instant start,
            Instant stop, Duration standardDuration) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (dbVendorResolver.getDatabaseVendor().isSqlServer()) {
            sql.append("CREATE TABLE RphTimeStamps (rownum [integer], times [datetime] NOT NULL)");

            sql.append("BEGIN TRANSACTION");
            sql.append("DECLARE @Counter integer = 1");
            sql.append("DECLARE @VarStartDate datetime = ?");
            sql.append("DECLARE @VarEndDate datetime = ?");
            sql.append("WHILE ").append("@VarEndDate >= @VarStartDate");
            sql.append("BEGIN");
            sql.append("INSERT INTO RphTimeStamps VALUES (@Counter, @VarStartDate)");
            sql.append("SET @Counter = @Counter+1");
            sql.append("DECLARE @Duration integer = ?");
            sql.append("SET @VarStartDate = DATEADD(SECOND, @Duration, @VarStartDate)");
            sql.append("END");
            sql.append("COMMIT");

            sql.append("DECLARE @PointId numeric(18,0)");
            sql.append("DECLARE @SetInsertLoop integer = 0");
            sql.append("DECLARE @ValueHigh integer = ?");
            sql.append("DECLARE @Valuelow integer = ?");
            sql.append("DECLARE @RPHNext numeric(18,0) = (SELECT MAX(changeid) FROM RawPointHistory)");
            sql.append("DECLARE @rphTSCount integer = (SELECT MAX(rownum) FROM RphTimeStamps)");
            sql.append("DECLARE point_cursor CURSOR FOR");
            if (type.equals("Analog Points")) {
                sql.append("SELECT p.PointId ");
                sql.append("FROM Point p ");
                sql.append("  LEFT JOIN PointUnit pu ON p.PointId = pu.PointId" );
                sql.append("  LEFT JOIN UnitMeasure um ON pu.UomId = um.UomId" );
                sql.append("WHERE p.PointType").notIn(statusPoints);
                sql.append(  "AND p.PaobjectId").in(devicesId);
                sql.append(  "AND um.UOMName !=  'kWH'");
            } else if (type.equals("Status Points")) {
                sql.append("SELECT PointId ");
                sql.append("FROM Point ");
                sql.append("WHERE PointType").in(statusPoints);
                sql.append("  AND PaobjectId").in(devicesId);
            } else {
                sql.append("SELECT p.PointId ");
                sql.append("FROM Point p" );
                sql.append("  LEFT JOIN PointUnit pu ON p.PointId = pu.PointId" );
                sql.append("  LEFT JOIN UnitMeasure um ON pu.UomId = um.UomId" );
                sql.append("WHERE p.PointType").notIn(statusPoints);
                sql.append(  "AND p.PaobjectId").in(devicesId);
                sql.append("AND um.UOMName = 'kWH'");
            }

            sql.append("OPEN point_cursor");
            sql.append("FETCH NEXT FROM point_cursor");
            sql.append("INTO @PointId ");
            sql.append("BEGIN TRANSACTION");
            sql.append("WHILE @@FETCH_STATUS = 0");
            sql.append("BEGIN");
            if (type.equals("Analog Points")) {

                sql.append("INSERT INTO RawPointHistory ");
                sql.append("    SELECT @RPHNext + rownum + @SetInsertLoop*@rphTSCount, @PointId, times ," + PointQuality.Normal.getQuality()
                    + ", ");
                sql.append(    "((SELECT (DATEDIFF(S, '2016-01-01', times) +@PointId) / 900) % (@ValueHigh-@Valuelow)), 0 ");
                sql.append("FROM RphTimeStamps");

            } else if (type.equals("Status Points")) {

                sql.append("INSERT INTO RawPointHistory ");
                sql.append("    SELECT @RPHNext + rownum + @SetInsertLoop*@rphTSCount, @PointId, times , " + PointQuality.Normal.getQuality() +
                    ", ");
                sql.append("    ((SELECT (DATEDIFF(S, '2016-01-01', times) +@PointId) / 900) % 1) , 0 " );
                sql.append("FROM RphTimeStamps");
            } else {
                sql.append("INSERT INTO RawPointHistory ");
                sql.append("    SELECT @RPHNext + rownum + @SetInsertLoop*@rphTSCount, @PointId, times ," +PointQuality.Normal.getQuality()
                        + ", ");
                sql.append(    "((SELECT DATEDIFF(S, '2016-01-01', times) + @PointId)/ 900), 0 ");
                sql.append("FROM RphTimeStamps");

            }
            sql.append("SET @SetInsertLoop = @SetInsertLoop + 1");
            sql.append("FETCH NEXT FROM point_cursor");
            sql.append("INTO @PointId");
            sql.append("END");
            sql.append("COMMIT");
            sql.append("CLOSE point_cursor");
            sql.append("DEALLOCATE point_cursor");

            sql.append("DROP TABLE RphTimeStamps");

            jdbcTemplate.update(sql.getSql(), new PreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps) throws SQLException {
                    int position = 5;
                    ps.setTimestamp(1, new Timestamp(start.getMillis()));
                    ps.setTimestamp(2, new Timestamp(stop.getMillis()));
                    ps.setLong(3, standardDuration.getStandardSeconds());
                    ps.setDouble(4, valueHigh);
                    ps.setDouble(5, valueLow);
                    for (String statusPoint : statusPoints) {
                        ps.setString(++position, statusPoint);
                    }
                    for (Integer deviceID : devicesId) {
                        ps.setLong(++position, deviceID);
                    }
                }
            });
        } else if (dbVendorResolver.getDatabaseVendor().isOracle()) {

            sql.append("CREATE TABLE RphTimeStamps(incrementer NUMBER UNIQUE,times TIMESTAMP NOT NULL)");
            try {
                jdbcTemplate.update(sql.getSql());
            } catch (Exception e) {
                jdbcTemplate.update("DROP TABLE RphTimeStamps");
                throw e;
            }

            SqlStatementBuilder oracleSql = new SqlStatementBuilder();
            oracleSql.append("DECLARE");
            oracleSql.append("    v_Counter NUMBER := 1 ;");
            oracleSql.append("    v_VarStartDate TIMESTAMP := ?;");
            oracleSql.append("    v_VarEndDate TIMESTAMP := ?;");
            oracleSql.append("    v_Duration NUMBER := ?;");
            oracleSql.append("    v_PointId NUMBER;");
            oracleSql.append("    v_SetInsertLoop NUMBER := 0;");
            oracleSql.append("    v_ValueHigh NUMBER := ?;");
            oracleSql.append("    v_Valuelow NUMBER := ?;");
            oracleSql.append("    v_RPHNext NUMBER ;");
            oracleSql.append("    v_RphTSCount NUMBER;");
            oracleSql.append("CURSOR point_cursor IS ");

            if (type.equals("Analog Points")) {
                oracleSql.append("SELECT p.PointId ");
                oracleSql.append("FROM Point p");
                oracleSql.append("  LEFT JOIN PointUnit pu ON p.PointId = pu.PointId");
                oracleSql.append("  LEFT JOIN UnitMeasure um ON pu.UomId = um.UomId ");
                oracleSql.append("WHERE p.PointType").notIn(statusPoints);
                oracleSql.append("  AND p.PaobjectId").in(devicesId);
                oracleSql.append("  AND um.UOMName != 'kWH';");
            } else if (type.equals("Status Points")) {
                oracleSql.append("SELECT PointId ");
                oracleSql.append("FROM Point ");
                oracleSql.append("WHERE PointType").in(statusPoints);
                oracleSql.append("  AND PaobjectId").in(devicesId).append(";");
            } else {
                oracleSql.append("SELECT p.PointId ");
                oracleSql.append("FROM Point p");
                oracleSql.append("  LEFT JOIN PointUnit pu ON p.PointId = pu.PointId");
                oracleSql.append("  LEFT JOIN UnitMeasure um ON pu.UomId = um.UomId ");
                oracleSql.append("WHERE p.PointType").notIn(statusPoints);
                oracleSql.append("  AND p.PaobjectId").in(devicesId);
                oracleSql.append("  AND um.UOMName = 'kWH';");
            }

            oracleSql.append("BEGIN");
            oracleSql.append("WHILE v_VarEndDate >= v_VarStartDate");
            oracleSql.append("LOOP");
            oracleSql.append("BEGIN");
            oracleSql.append("INSERT INTO RphTimeStamps VALUES (v_Counter, v_VarStartDate);");
            oracleSql.append("    v_Counter := v_Counter + 1 ;");
            oracleSql.append("    v_VarStartDate := CAST((v_VarStartDate + v_Duration/(24*60*60)) AS TIMESTAMP);");
            oracleSql.append("END;");
            oracleSql.append("COMMIT;");
            oracleSql.append("END LOOP;");

            oracleSql.append("SELECT MAX(changeid) INTO v_RPHNext ");
            oracleSql.append("FROM RawPointHistory;");
            oracleSql.append("SELECT MAX(incrementer) INTO v_RphTSCount");
            oracleSql.append("FROM RphTimeStamps ;");

            oracleSql.append("OPEN point_cursor;");
            oracleSql.append("FETCH point_cursor into v_PointId;");
            oracleSql.append("WHILE(point_cursor%found)");
            oracleSql.append("LOOP");
            oracleSql.append("BEGIN");
            if (type.equals("Analog Points")) {
                oracleSql.append("INSERT INTO RawPointHistory");
                oracleSql.append("    ( SELECT (v_RPHNext + incrementer + (v_RphTSCount * v_SetInsertLoop)) , v_PointId , times,"
                    + PointQuality.Normal.getQuality() + " , ");
                oracleSql.append(    "(SELECT DBMS_RANDOM.VALUE(v_Valuelow, v_ValueHigh) FROM DUAL) , 0");
                oracleSql.append("FROM RphTimeStamps);");
            } else if (type.equals("Status Points")) {
                oracleSql.append("INSERT INTO RawPointHistory");
                oracleSql.append("    (SELECT (v_RPHNext + incrementer + (v_RphTSCount * v_SetInsertLoop)) , v_PointId , times,"
                    + PointQuality.Normal.getQuality() + " , ");
                oracleSql.append(    "(SELECT ROUND(DBMS_RANDOM.VALUE) FROM DUAL) , 0");
                oracleSql.append("FROM RphTimeStamps);");
            } else {
                oracleSql.append("INSERT INTO RawPointHistory");
                oracleSql.append("    (SELECT (v_RPHNext + incrementer + (v_RphTSCount * v_SetInsertLoop)) , v_PointId ,times,"
                    + PointQuality.Normal.getQuality() + " , ");
                oracleSql.append(    "((SELECT (SYSDATE - TO_DATE('01-01-1970 00:00:00', 'DD-MM-YYYY HH24:MI:SS')) * 24 * "
                    + "60 * 60 FROM DUAL) + v_PointId)/900 , 0");
                oracleSql.append("FROM RphTimeStamps);");
            }
            oracleSql.append("    v_SetInsertLoop := v_SetInsertLoop + 1 ;");
            oracleSql.append("FETCH point_cursor INTO v_PointId;");
            oracleSql.append("END;");
            oracleSql.append("END LOOP;");
            oracleSql.append("CLOSE point_cursor;");
            oracleSql.append("COMMIT;");
            oracleSql.append("EXECUTE IMMEDIATE 'DROP TABLE RphTimeStamps';");
            oracleSql.append("END;");
            try {
                jdbcTemplate.update(oracleSql.getSql(), new PreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps) throws SQLException {
                        int position = 5;
                        ps.setTimestamp(1, new Timestamp(start.getMillis()));
                        ps.setTimestamp(2, new Timestamp(stop.getMillis()));
                        ps.setLong(3, standardDuration.getStandardSeconds());
                        ps.setDouble(4, valueHigh);
                        ps.setDouble(5, valueLow);
                        for (String statusPoint : statusPoints) {
                            ps.setString(++position, statusPoint);
                        }
                        for (Integer deviceID : devicesId) {
                            ps.setInt(++position, deviceID);
                        }
                    }
                });
            } catch (Exception e) {
                jdbcTemplate.update("DROP TABLE RphTimeStamps");
                throw e;
            }
        }
    }
}

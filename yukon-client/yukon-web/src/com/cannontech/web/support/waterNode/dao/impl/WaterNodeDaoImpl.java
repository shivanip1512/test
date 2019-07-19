package com.cannontech.web.support.waterNode.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.joda.time.Instant;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.data.point.PointType;
import com.cannontech.web.support.waterNode.dao.WaterNodeDao;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;

public class WaterNodeDaoImpl implements WaterNodeDao {
    @Autowired YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public List<WaterNodeDetails> getWaterNodeDetails(Instant startTime, Instant stopTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT rfa.SerialNumber, ypo.PaObjectId, ypo.PaoName, ypo.Type, rph.Timestamp, rph.Value, dmg.MeterNumber");
        sql.append("FROM YukonPaObject ypo");
        sql.append(  "JOIN Point p ON ypo.PaObjectId = p.PaObjectId");
        sql.append(  "JOIN RawPointHistory rph ON rph.PointId = p.PointId");
        sql.append(  "JOIN RfnAddress rfa ON ypo.PaObjectId = rfa.DeviceId");
        sql.append(  "JOIN DeviceMeterGroup dmg ON dmg.DeviceId = ypo.PaObjectId");
        sql.append("WHERE ypo.Type").in(PaoType.getWaterMeterTypes());
        sql.append(  "AND p.PointType").eq_k(PointType.Analog);
        sql.append(  "AND p.PointOffset").eq(5);
        sql.append(  "AND rph.Timestamp").lte(stopTime);
        sql.append(  "AND rph.Timestamp").gte(startTime);
        sql.append("ORDER BY rph.PointId, rph.Timestamp;");

        final List<WaterNodeDetails> results = new ArrayList<WaterNodeDetails>();
        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int PaObjectId = rs.getInt("PaObjectId");
                int oldPaObjectId = -1;
                int currentNodeIndex = results.size();
                if (currentNodeIndex != 0) {
                    oldPaObjectId = (results.get(currentNodeIndex - 1).getPaObjectId()).intValue();
                }
                if (oldPaObjectId!=PaObjectId) {
                    WaterNodeDetails waterNodeDetails = new WaterNodeDetails();
                    waterNodeDetails.setSerialNumber(rs.getString("SerialNumber"));
                    waterNodeDetails.setMeterNumber(rs.getString("MeterNumber"));
                    waterNodeDetails.setName(rs.getString("PaoName"));
                    waterNodeDetails.setType(rs.getString("Type"));
                    waterNodeDetails.setPaObjectId(PaObjectId);
                    waterNodeDetails.setHighSleepingCurrentIndicator(false);
                    waterNodeDetails.setBatteryLevel(null);
                    results.add(waterNodeDetails);
                } else {
                    // if a new waterNodeDetails object has not been created, desired index is size-1
                    currentNodeIndex--;
                }
                results.get(currentNodeIndex).addTimestamp(rs.getInstant("Timestamp"));
                results.get(currentNodeIndex).addVoltage(rs.getDouble("Value"));
            }
        });
        return results;
    }

}

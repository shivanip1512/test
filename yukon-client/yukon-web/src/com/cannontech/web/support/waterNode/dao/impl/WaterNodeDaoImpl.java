package com.cannontech.web.support.waterNode.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.web.support.waterNode.dao.WaterNodeDao;
import com.cannontech.web.support.waterNode.details.WaterNodeDetails;

public class WaterNodeDaoImpl implements WaterNodeDao {
    @Autowired
    YukonJdbcTemplate yukonJdbcTemplate;

    @Override
    public List<WaterNodeDetails> getWaterNodeDetails(Instant startTime, Instant stopTime) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT rfa.SerialNumber AS 'serial number', ");
        sql.append("ypo.PAObjectID as 'meter number', ");
        sql.append("ypo.PAOName AS name, ypo.type, ");
        sql.append("rph.TIMESTAMP AS timestamp, ");
        sql.append("rph.VALUE AS 'battery voltage' ");

        sql.append("FROM RAWPOINTHISTORY rph ");
        sql.append("LEFT JOIN POINT point ON point.POINTID = rph.pointID ");
        sql.append("LEFT JOIN YukonPAObject ypo ON ypo.PAObjectID = point.PAObjectID ");
        sql.append("LEFT JOIN RfnAddress rfa ON ypo.PAObjectID = rfa.DeviceId ");
        sql.append("WHERE ypo.Type LIKE '%RFW%' ");
        sql.append("AND rph.TIMESTAMP").gt(startTime);
        sql.append("AND rph.TIMESTAMP").lt(stopTime);

        final List<WaterNodeDetails> results = new ArrayList<WaterNodeDetails>();

        yukonJdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                String serialNumber = rs.getString("serial number");
                String oldSerialNumber = StringUtils.EMPTY;
                int currentNodeIndex = results.size();
                if (currentNodeIndex != 0) {
                    oldSerialNumber = results.get(currentNodeIndex - 1).getSerialNumber();
                }
                if (!serialNumber.equals(oldSerialNumber)) {
                    WaterNodeDetails waterNodeDetails = new WaterNodeDetails();
                    waterNodeDetails.setSerialNumber(serialNumber);
                    waterNodeDetails.setMeterNumber(rs.getInt("meter number"));
                    waterNodeDetails.setName(rs.getString("name"));
                    waterNodeDetails.setType(rs.getString("type"));
                    waterNodeDetails.setHighSleepingCurrentIndicator(false);
                    waterNodeDetails.setBatteryLevel(null);
                    results.add(waterNodeDetails);
                } else {
                    // if a new waterNodeDetails object has not been created, desired index is size-1
                    currentNodeIndex--;
                }
                results.get(currentNodeIndex).addTimestamp(rs.getInstant("timestamp"));
                results.get(currentNodeIndex).addVoltage(rs.getDouble("battery voltage"));
            }
        });
        return results;
    }

}

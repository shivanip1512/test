package com.cannontech.dr.dao.impl;

import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.SUCCESS;
import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.UNKNOWN;
import static com.cannontech.dr.model.PerformanceVerificationMessageStatus.UNSUCCESS;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.dao.PerformanceVerificationDao;
import com.cannontech.dr.model.MutablePerformanceVerificationEventStats;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PerformanceVerificationDaoImpl implements PerformanceVerificationDao {
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<PerformanceVerificationEventMessageStats> getReports(Duration duration, Instant stop) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT rbed.RfBroadcastEventId, SendTime, Result");
        sql.append("FROM RfBroadcastEventDevice rbed");
        sql.append("JOIN RfBroadcastEvent rbe ON rbed.RfBroadcastEventId = rbe.RfBroadcastEventId");
        sql.append("WHERE SendTime").gt(stop.minus(duration));
        sql.append("AND SendTime").lt(stop);

        final Map<Integer, MutablePerformanceVerificationEventStats> stats = new HashMap<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int messageId = rs.getInt("rbed.RfBroadcastEventId");
                Instant sendTime = rs.getInstant("SendTime");
                PerformanceVerificationMessageStatus result = 
                        rs.getEnum("Result", PerformanceVerificationMessageStatus.class);

                if(!stats.containsKey(messageId)) {
                    MutablePerformanceVerificationEventStats eventStats = new MutablePerformanceVerificationEventStats();
                    eventStats.setMessageId(messageId);
                    eventStats.setMessageSent(sendTime);
                    stats.put(messageId, eventStats);
                }
                stats.get(messageId).addStatus(result);
            }
        });
        List<PerformanceVerificationEventMessageStats> reports = Lists.newArrayListWithExpectedSize(stats.size());
        for (MutablePerformanceVerificationEventStats mutableStat : stats.values()) {
            reports.add(mutableStat.getImmutableMessageStats());
        }
        return reports;
    }

    @Override
    public PerformanceVerificationEventStats getAverageReport(Duration duration, Instant stop) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT Result, Count(*) as count");
        sql.append("FROM RfBroadcastEventDevice rbed");
        sql.append("JOIN RfBroadcastEvent rbe ON rbed.RfBroadcastEventId = rbe.RfBroadcastEventId");
        sql.append("WHERE SendTime").gt(stop.minus(duration));
        sql.append("AND SendTime").lt(stop);
        sql.append("GROUP BY Result");

        final Map<PerformanceVerificationMessageStatus, Integer> counts =
                Maps.newHashMapWithExpectedSize(PerformanceVerificationMessageStatus.values().length);
        for (PerformanceVerificationMessageStatus status : PerformanceVerificationMessageStatus.values()) {
            counts.put(status, 0);
        }
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                counts.put(rs.getEnum("Result", PerformanceVerificationMessageStatus.class), rs.getInt("count"));
            }
        });

        return new PerformanceVerificationEventStats(counts.get(SUCCESS), counts.get(UNSUCCESS), counts.get(UNKNOWN));
    }
}

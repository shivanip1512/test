package com.cannontech.simulators.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.model.RegulatorEvent;
import com.cannontech.capcontrol.model.RegulatorEvent.EventType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.simulators.dao.RegulatorEventsSimulatorDao;
import com.google.common.collect.ImmutableList;

public class RegulatorEventsSimulatorDaoImpl implements RegulatorEventsSimulatorDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    
    private static final YukonRowMapper<RegulatorOperations> rowMapper = new YukonRowMapper<RegulatorOperations>() {
        
        @Override
        public RegulatorOperations mapRow(YukonResultSet yrs) throws SQLException {
            ResultSet rs = yrs.getResultSet();
            RegulatorOperations result = new RegulatorOperations();
            result.regulatorId = yrs.getInt("RegulatorId");
            result.eventType = yrs.getEnum("EventType", EventType.class);
            result.timeStamp = new Instant(rs.getTimestamp("TimeStamp").getTime());
            
            return result;
        }
    };
    
    public List<RegulatorOperations> getRegulatorTapOperationsAfter(Instant start) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RegulatorId, EventType, TimeStamp");
        sql.append("FROM RegulatorEvents");
        sql.append("WHERE EventType").in(ImmutableList.of(RegulatorEvent.EventType.TAP_DOWN, RegulatorEvent.EventType.TAP_UP));
        sql.append("AND TimeStamp").gt(start);
        
        return yukonJdbcTemplate.query(sql, rowMapper);
    }
}

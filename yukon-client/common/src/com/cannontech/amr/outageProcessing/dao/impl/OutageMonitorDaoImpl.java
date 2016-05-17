package com.cannontech.amr.outageProcessing.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.outageProcessing.OutageMonitor;
import com.cannontech.amr.outageProcessing.dao.OutageMonitorDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.OutageMonitorNotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class OutageMonitorDaoImpl implements OutageMonitorDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<OutageMonitor> template;
    private static final RowMapper<OutageMonitor> rowMapper;
    static {
        rowMapper = OutageMonitorDaoImpl.createRowMapper();
    }

    public void saveOrUpdate(OutageMonitor outageMonitor) {
        try {
            template.save(outageMonitor);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to save outage processor.", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public OutageMonitor getById(final int outageMonitorId) throws OutageMonitorNotFoundException {

        OutageMonitor outageMonitor = null;

        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT * FROM OutageMonitor WHERE OutageMonitorId").eq_k(outageMonitorId);
            outageMonitor = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new OutageMonitorNotFoundException();
        }

        return outageMonitor;
    }

    public boolean processorExistsWithName(String name) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM OutageMonitor WHERE OutageMonitorName").eq(name);
        return yukonJdbcTemplate.queryForInt(sql) > 0;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<OutageMonitor> getAll() {
        final SqlStatementBuilder sql = new SqlStatementBuilder("SELECT * FROM OutageMonitor");
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    public boolean delete(int outageMonitorId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM OutageMonitor WHERE OutageMonitorId").eq(outageMonitorId);
        return yukonJdbcTemplate.update(sql) > 0;
    }

    private static final RowMapper<OutageMonitor> createRowMapper() {
        final RowMapper<OutageMonitor> rowMapper = new RowMapper<OutageMonitor>() {
            public OutageMonitor mapRow(ResultSet rs, int rowNum) throws SQLException {
                OutageMonitor outageMonitor = new OutageMonitor();

                outageMonitor.setOutageMonitorId(rs.getInt("OutageMonitorId"));
                outageMonitor.setOutageMonitorName(rs.getString("OutageMonitorName"));
                outageMonitor.setGroupName(rs.getString("GroupName"));
                outageMonitor.setTimePeriodDays(rs.getInt("TimePeriod"));
                outageMonitor.setNumberOfOutages(rs.getInt("NumberOfOutages"));
                outageMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.valueOf(rs.getString("EvaluatorStatus")));
                return outageMonitor;
            }
        };
        return rowMapper;
    }

    private FieldMapper<OutageMonitor> outageMonitorFieldMapper = new FieldMapper<OutageMonitor>() {
        public void extractValues(MapSqlParameterSource p, OutageMonitor outageMonitor) {
            p.addValue("OutageMonitorName", outageMonitor.getOutageMonitorName());
            p.addValue("GroupName", outageMonitor.getGroupName());
            p.addValue("TimePeriod", outageMonitor.getTimePeriodDays());
            p.addValue("NumberOfOutages", outageMonitor.getNumberOfOutages());
            p.addValue("EvaluatorStatus", outageMonitor.getEvaluatorStatus().name());

        }

        public Number getPrimaryKey(OutageMonitor outageMonitor) {
            return outageMonitor.getOutageMonitorId();
        }

        public void setPrimaryKey(OutageMonitor outageMonitor, int value) {
            outageMonitor.setOutageMonitorId(value);
        }
    };

    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<OutageMonitor>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("OutageMonitor");
        template.setPrimaryKeyField("OutageMonitorId");
        template.setFieldMapper(outageMonitorFieldMapper);
    }
}
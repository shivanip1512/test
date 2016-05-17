package com.cannontech.amr.tamperFlagProcessing.dao.impl;

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
import com.cannontech.amr.tamperFlagProcessing.TamperFlagMonitor;
import com.cannontech.amr.tamperFlagProcessing.dao.TamperFlagMonitorDao;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.TamperFlagMonitorNotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class TamperFlagMonitorDaoImpl implements TamperFlagMonitorDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<TamperFlagMonitor> template;
    private static final RowMapper<TamperFlagMonitor> rowMapper;

    static {
        rowMapper = TamperFlagMonitorDaoImpl.createRowMapper();
    }

    public void saveOrUpdate(TamperFlagMonitor tamperFlagMonitor) {
        try {
            template.save(tamperFlagMonitor);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to save tamper flag monitor.", e);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public TamperFlagMonitor getById(final int tamperFlagMonitorId) throws TamperFlagMonitorNotFoundException {

        TamperFlagMonitor tamperFlagMonitor = null;

        try {
            final SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT * FROM TamperFlagMonitor WHERE TamperFlagMonitorId").eq_k(tamperFlagMonitorId);
            tamperFlagMonitor = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new TamperFlagMonitorNotFoundException();
        }

        return tamperFlagMonitor;
    }

    public boolean processorExistsWithName(String name) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM TamperFlagMonitor WHERE TamperFlagMonitorName").eq(name);
        return yukonJdbcTemplate.queryForInt(sql) > 0;
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<TamperFlagMonitor> getAll() {
        final SqlStatementBuilder sql = new SqlStatementBuilder("SELECT * FROM TamperFlagMonitor");
        return yukonJdbcTemplate.query(sql, rowMapper);
    }

    public boolean delete(int tamperFlagMonitorId) {
        final SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM TamperFlagMonitor WHERE TamperFlagMonitorId").eq(tamperFlagMonitorId);
        return yukonJdbcTemplate.update(sql) > 0;
    }

    private static final RowMapper<TamperFlagMonitor> createRowMapper() {
        final RowMapper<TamperFlagMonitor> rowMapper = new RowMapper<TamperFlagMonitor>() {
            public TamperFlagMonitor mapRow(ResultSet rs, int rowNum) throws SQLException {
                TamperFlagMonitor tamperFlagMonitor = new TamperFlagMonitor();
                tamperFlagMonitor.setTamperFlagMonitorId(rs.getInt("TamperFlagMonitorId"));
                tamperFlagMonitor.setTamperFlagMonitorName(rs.getString("TamperFlagMonitorName"));
                tamperFlagMonitor.setGroupName(rs.getString("GroupName"));
                tamperFlagMonitor.setEvaluatorStatus(MonitorEvaluatorStatus.valueOf(rs.getString("EvaluatorStatus")));
                return tamperFlagMonitor;
            }
        };
        return rowMapper;
    }

    private FieldMapper<TamperFlagMonitor> tamperFlagMonitorFieldMapper = new FieldMapper<TamperFlagMonitor>() {
        public void extractValues(MapSqlParameterSource p, TamperFlagMonitor tamperFlagMonitor) {
            p.addValue("TamperFlagMonitorName", tamperFlagMonitor.getTamperFlagMonitorName());
            p.addValue("GroupName", tamperFlagMonitor.getGroupName());
            p.addValue("EvaluatorStatus", tamperFlagMonitor.getEvaluatorStatus().name());
        }

        public Number getPrimaryKey(TamperFlagMonitor tamperFlagMonitor) {
            return tamperFlagMonitor.getTamperFlagMonitorId();
        }

        public void setPrimaryKey(TamperFlagMonitor tamperFlagMonitor, int value) {
            tamperFlagMonitor.setTamperFlagMonitorId(value);
        }
    };

    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<TamperFlagMonitor>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("TamperFlagMonitor");
        template.setPrimaryKeyField("TamperFlagMonitorId");
        template.setFieldMapper(tamperFlagMonitorFieldMapper);
    }
}
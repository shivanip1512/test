package com.cannontech.amr.porterResponseMonitor.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.StateDao;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SimpleTableAccessTemplate.CascadeMode;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class PorterResponseMonitorDaoImpl implements PorterResponseMonitorDao {

    @Autowired private AttributeService attributeService;
    @Autowired private StateDao stateDao;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private SimpleTableAccessTemplate<PorterResponseMonitor> monitorTemplate;
    private SimpleTableAccessTemplate<PorterResponseMonitorRule> ruleTemplate;
    private SimpleTableAccessTemplate<PorterResponseMonitorErrorCode> errorCodeTemplate;

    private final YukonRowMapper<PorterResponseMonitor> monitorRowMapper = new YukonRowMapper<PorterResponseMonitor>() {
        @Override
        public PorterResponseMonitor mapRow(YukonResultSet rs) throws SQLException {
            PorterResponseMonitor retVal = new PorterResponseMonitor();
            retVal.setMonitorId(rs.getInt("monitorId"));
            retVal.setName(rs.getString("name"));
            retVal.setGroupName(rs.getString("groupName"));
            retVal.setStateGroup(stateDao.getLiteStateGroup(rs.getInt("stateGroupId")));
            String attributeKey = rs.getString("attribute");
            Attribute attribute = attributeService.resolveAttributeName(attributeKey);
            retVal.setAttribute(attribute);
            retVal.setEvaluatorStatus(rs.getEnum("evaluatorStatus", MonitorEvaluatorStatus.class));
            return retVal;
        }
    };

    private final YukonRowMapper<PorterResponseMonitorRule> ruleRowMapper = new YukonRowMapper<PorterResponseMonitorRule>() {
        @Override
        public PorterResponseMonitorRule mapRow(YukonResultSet rs) throws SQLException {
            PorterResponseMonitorRule retVal = new PorterResponseMonitorRule();
            retVal.setRuleId(rs.getInt("ruleId"));
            retVal.setRuleOrder(rs.getInt("ruleOrder"));
            retVal.setSuccess(rs.getBoolean("success"));
            retVal.setMatchStyle(rs.getEnum("matchStyle", PorterResponseMonitorMatchStyle.class));
            retVal.setState(rs.getString("state"));
            return retVal;
        }
    };

    private final YukonRowMapper<PorterResponseMonitorErrorCode> errorCodeRowMapper = new YukonRowMapper<PorterResponseMonitorErrorCode>() {
        @Override
        public PorterResponseMonitorErrorCode mapRow(YukonResultSet rs) throws SQLException {
            PorterResponseMonitorErrorCode retVal = new PorterResponseMonitorErrorCode();
            retVal.setErrorCodeId(rs.getInt("errorCodeId"));
            retVal.setRuleId(rs.getInt("ruleId"));
            retVal.setErrorCode(rs.getInt("errorCode"));
            return retVal;
        }
    };

    @Override
    @Transactional
    public List<PorterResponseMonitor> getAllMonitors() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MonitorId, Name, GroupName, StateGroupId, Attribute, EvaluatorStatus");
        sql.append("FROM PorterResponseMonitor");
        sql.append("ORDER BY MonitorId");

        List<PorterResponseMonitor> monitorList = yukonJdbcTemplate.query(sql, monitorRowMapper);

        for (PorterResponseMonitor monitor : monitorList) {
            List<PorterResponseMonitorRule> rules = getRulesByMonitorId(monitor.getMonitorId());
            monitor.setRules(rules);
        }

        return monitorList;
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public PorterResponseMonitor getMonitorById(final Integer monitorId) throws NotFoundException {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT MonitorId, Name, GroupName, StateGroupId, Attribute, EvaluatorStatus");
        sql.append("FROM PorterResponseMonitor");
        sql.append("WHERE MonitorId").eq(monitorId);

        PorterResponseMonitor monitor = null;

        try {
            monitor = yukonJdbcTemplate.queryForObject(sql, monitorRowMapper);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Porter Response Monitor not found.", e);
        }

        List<PorterResponseMonitorRule> rules = getRulesByMonitorId(monitorId);
        monitor.setRules(rules);

        return monitor;
    }

    private List<PorterResponseMonitorRule> getRulesByMonitorId(int monitorId) {
        SqlStatementBuilder ruleSql = new SqlStatementBuilder();
        ruleSql.append("SELECT RuleId, RuleOrder, Success, MatchStyle, State");
        ruleSql.append("FROM PorterResponseMonitorRule");
        ruleSql.append("WHERE monitorId").eq(monitorId);
        ruleSql.append("ORDER BY RuleOrder");

        List<PorterResponseMonitorRule> ruleList = yukonJdbcTemplate.query(ruleSql, ruleRowMapper);

        for (PorterResponseMonitorRule rule : ruleList) {
            List<PorterResponseMonitorErrorCode> errorCodes = getErrorCodesByRuleId(rule.getRuleId());
            rule.setErrorCodes(errorCodes);
        }

        return ruleList;
    }

    private List<PorterResponseMonitorErrorCode> getErrorCodesByRuleId(int ruleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ErrorCodeId, RuleId, ErrorCode");
        sql.append("FROM PorterResponseMonitorErrorCode");
        sql.append("WHERE RuleId").eq(ruleId);

        List<PorterResponseMonitorErrorCode> errorCodes = yukonJdbcTemplate.query(sql, errorCodeRowMapper);

        return errorCodes;
    }

    @Override
    public boolean deleteMonitor(int monitorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM PorterResponseMonitor");
        sql.append("WHERE MonitorId").eq(monitorId);
        int rowsAffected = yukonJdbcTemplate.update(sql);
        return rowsAffected > 0;
    }

    @Override
    @Transactional
    public void save(PorterResponseMonitor monitor) {
        try {
            monitorTemplate.save(monitor);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateException("Unable to save Porter Response Monitor.", e);
        }
    }

    private final AdvancedFieldMapper<PorterResponseMonitor> monitorFieldMapper = new AdvancedFieldMapper<PorterResponseMonitor>() {
        @Override
        public void extractValues(SqlParameterChildSink p, PorterResponseMonitor monitor) {
            p.addValue("Name", monitor.getName());
            p.addValue("GroupName", monitor.getGroupName());
            p.addValue("StateGroupId", monitor.getStateGroup().getStateGroupID());
            p.addValue("Attribute", monitor.getAttribute());
            p.addValue("EvaluatorStatus", monitor.getEvaluatorStatus());
            p.addChildren(ruleTemplate, monitor.getRules());
        }

        @Override
        public Number getPrimaryKey(PorterResponseMonitor monitor) {
            return monitor.getMonitorId();
        }

        @Override
        public void setPrimaryKey(PorterResponseMonitor monitor, int value) {
            monitor.setMonitorId(value);
        }
    };

    private final AdvancedFieldMapper<PorterResponseMonitorRule> ruleFieldMapper = new AdvancedFieldMapper<PorterResponseMonitorRule>() {
        @Override
        public void extractValues(SqlParameterChildSink p, PorterResponseMonitorRule rule) {
            p.addValue("RuleOrder", rule.getRuleOrder());
            p.addValue("Success", YNBoolean.valueOf(rule.isSuccess()));
            p.addValue("MatchStyle", rule.getMatchStyle());
            p.addValue("State", rule.getState());
            p.addChildren(errorCodeTemplate, rule.getErrorCodes());
        }

        @Override
        public Number getPrimaryKey(PorterResponseMonitorRule rule) {
            return rule.getRuleId();
        }

        @Override
        public void setPrimaryKey(PorterResponseMonitorRule rule, int value) {
            rule.setRuleId(value);
        }
    };

    private final AdvancedFieldMapper<PorterResponseMonitorErrorCode> errorCodeFieldMapper = new AdvancedFieldMapper<PorterResponseMonitorErrorCode>() {
        @Override
        public void extractValues(SqlParameterChildSink p, PorterResponseMonitorErrorCode errorCode) {
            p.addValue("ErrorCode", errorCode.getErrorCode());
        }

        @Override
        public Number getPrimaryKey(PorterResponseMonitorErrorCode errorCode) {
            return errorCode.getErrorCodeId();
        }

        @Override
        public void setPrimaryKey(PorterResponseMonitorErrorCode errorCode, int value) {
            errorCode.setErrorCodeId(value);
        }
    };

    @PostConstruct
    public void init() throws Exception {
        monitorTemplate = new SimpleTableAccessTemplate<PorterResponseMonitor>(yukonJdbcTemplate, nextValueHelper);
        monitorTemplate.setTableName("PorterResponseMonitor");
        monitorTemplate.setPrimaryKeyField("MonitorId");
        monitorTemplate.setAdvancedFieldMapper(monitorFieldMapper);

        ruleTemplate = new SimpleTableAccessTemplate<PorterResponseMonitorRule>(yukonJdbcTemplate, nextValueHelper);
        ruleTemplate.setTableName("PorterResponseMonitorRule");
        ruleTemplate.setPrimaryKeyField("RuleId");
        ruleTemplate.setParentForeignKeyField("MonitorId", CascadeMode.DELETE_ALL_CHILDREN_BEFORE_UPDATE);
        ruleTemplate.setAdvancedFieldMapper(ruleFieldMapper);

        errorCodeTemplate = new SimpleTableAccessTemplate<PorterResponseMonitorErrorCode>(yukonJdbcTemplate, nextValueHelper);
        errorCodeTemplate.setTableName("PorterResponseMonitorErrorCode");
        errorCodeTemplate.setPrimaryKeyField("ErrorCodeId");
        errorCodeTemplate.setParentForeignKeyField("RuleId", CascadeMode.DELETE_ALL_CHILDREN_BEFORE_UPDATE);
        errorCodeTemplate.setAdvancedFieldMapper(errorCodeFieldMapper);
    }
}
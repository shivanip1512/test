package com.cannontech.amr.porterResponseMonitor.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.amr.MonitorEvaluatorStatus;
import com.cannontech.amr.porterResponseMonitor.dao.PorterResponseMonitorDao;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorErrorCode;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorAction;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitor;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorRule;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DuplicateException;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PorterResponseMonitorDaoImpl implements PorterResponseMonitorDao, InitializingBean {

	private YukonJdbcTemplate yukonJdbcTemplate;
	private NextValueHelper nextValueHelper;
	private SimpleTableAccessTemplate<PorterResponseMonitor> monitorTemplate;
	private SimpleTableAccessTemplate<StoredPorterResponseMonitorRule> ruleTemplate;
	private SimpleTableAccessTemplate<StoredPorterResponseMonitorErrorCode> errorCodeTemplate;

	private final YukonRowMapper<PorterResponseMonitor> monitorRowMapper = new YukonRowMapper<PorterResponseMonitor>() {
		@Override
		public PorterResponseMonitor mapRow(YukonResultSet rs) throws SQLException {
			PorterResponseMonitor retVal = new PorterResponseMonitor();
			retVal.setMonitorId(rs.getInt("monitorId"));
			retVal.setName(rs.getString("name"));
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
			retVal.setSuccess(rs.getEnum("success", YNBoolean.class).getBoolean());
			retVal.setMatchStyle(rs.getEnum("matchStyle", PorterResponseMonitorMatchStyle.class));
			retVal.setAction(rs.getEnum("action", PorterResponseMonitorAction.class));
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
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<PorterResponseMonitor> getAllMonitors() {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT MonitorId, Name, EvaluatorStatus");
		sql.append("FROM PorterResponseMonitor");
		sql.append("ORDER BY MonitorId");

		List<PorterResponseMonitor> porterResponseMonitorList = yukonJdbcTemplate.query(sql, monitorRowMapper);

		return porterResponseMonitorList;
	}

	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public PorterResponseMonitor getMonitorById(final Integer monitorId) throws NotFoundException {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT MonitorId, Name, EvaluatorStatus");
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

	@Override
	public List<PorterResponseMonitorRule> getRulesByMonitorId(int monitorId) {
		SqlStatementBuilder ruleSql = new SqlStatementBuilder();
		ruleSql.append("SELECT RuleId, RuleOrder, Success, MatchStyle, Action");
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

	@Override
	public List<PorterResponseMonitorErrorCode> getErrorCodesByRuleId(int ruleId) {
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
	public boolean deleteRule(int ruleId) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("DELETE FROM PorterResponseMonitorRule");
		sql.append("WHERE RuleId").eq(ruleId);
		int rowsAffected = yukonJdbcTemplate.update(sql);

		return rowsAffected > 0;
	}

	@Override
	public void save(PorterResponseMonitor monitor) {
		try {
			List<PorterResponseMonitorRule> existingRuleList = Lists.newArrayList();
			if (monitorTemplate.saveWillUpdate(monitor)) {
				// get existing rules
				existingRuleList = getRulesByMonitorId(monitor.getMonitorId());
			}
			monitorTemplate.save(monitor);

			// delete all rules
			for (PorterResponseMonitorRule rule : existingRuleList) {
				deleteRule(rule.getRuleId());
			}

			List<PorterResponseMonitorRule> newRuleList = monitor.getRules();
			Set<PorterResponseMonitorRule> newRules = Sets.newHashSet(newRuleList);

			// re-add them... plus the new ones (with duplicates removed)
			for (PorterResponseMonitorRule rule : newRules) {
				StoredPorterResponseMonitorRule ruleHolder = new StoredPorterResponseMonitorRule();
				ruleHolder.rule = rule;
				ruleHolder.parent = monitor;

				// we can't do a save here, but it will try to do an update,
				// which will fail
				// --deliberately re-using the ID of the row we just deleted
				// above
				ruleTemplate.insert(ruleHolder);

				for (PorterResponseMonitorErrorCode errorCode : rule.getErrorCodes()) {
					StoredPorterResponseMonitorErrorCode errorCodeHolder = new StoredPorterResponseMonitorErrorCode();
					errorCodeHolder.errorCode = errorCode;
					errorCodeHolder.parent = rule;

					errorCodeTemplate.insert(errorCodeHolder);
				}
			}

		} catch (DataIntegrityViolationException e) {
			throw new DuplicateException(
					"Unable to save Porter Response Monitor.", e);
		}
	}

	private class StoredPorterResponseMonitorRule {
		PorterResponseMonitor parent;
		PorterResponseMonitorRule rule;
	}

	private class StoredPorterResponseMonitorErrorCode {
		PorterResponseMonitorRule parent;
		PorterResponseMonitorErrorCode errorCode;
	}

	private FieldMapper<PorterResponseMonitor> monitorFieldMapper = new FieldMapper<PorterResponseMonitor>() {
		public void extractValues(MapSqlParameterSource p, PorterResponseMonitor monitor) {
			p.addValue("Name", monitor.getName());
			p.addValue("EvaluatorStatus", monitor.getEvaluatorStatus());
		}

		public Number getPrimaryKey(PorterResponseMonitor monitor) {
			return monitor.getMonitorId();
		}

		public void setPrimaryKey(PorterResponseMonitor monitor, int value) {
			monitor.setMonitorId(value);
		}
	};

	private FieldMapper<StoredPorterResponseMonitorRule> ruleFieldMapper = new FieldMapper<StoredPorterResponseMonitorRule>() {
		public void extractValues(MapSqlParameterSource p, StoredPorterResponseMonitorRule holder) {
			p.addValue("MonitorId", holder.parent.getMonitorId());
			p.addValue("RuleOrder", holder.rule.getRuleOrder());
			p.addValue("Success", YNBoolean.valueOf(holder.rule.isSuccess()));
			p.addValue("MatchStyle", holder.rule.getMatchStyle());
			p.addValue("Action", holder.rule.getAction());
		}

		public Number getPrimaryKey(StoredPorterResponseMonitorRule holder) {
			return holder.rule.getRuleId();
		}

		public void setPrimaryKey(StoredPorterResponseMonitorRule holder, int value) {
			holder.rule.setRuleId(value);
		}
	};

	private FieldMapper<StoredPorterResponseMonitorErrorCode> errorCodeFieldMapper = new FieldMapper<StoredPorterResponseMonitorErrorCode>() {
		public void extractValues(MapSqlParameterSource p, StoredPorterResponseMonitorErrorCode holder) {
			p.addValue("RuleId", holder.parent.getRuleId());
			p.addValue("ErrorCode", holder.errorCode.getErrorCode());
		}

		public Number getPrimaryKey(StoredPorterResponseMonitorErrorCode holder) {
			return holder.errorCode.getErrorCodeId();
		}

		public void setPrimaryKey(StoredPorterResponseMonitorErrorCode holder, int value) {
			holder.errorCode.setErrorCodeId(value);
		}
	};

	@Override
	public void afterPropertiesSet() throws Exception {
		monitorTemplate = new SimpleTableAccessTemplate<PorterResponseMonitor>(yukonJdbcTemplate, nextValueHelper);
		monitorTemplate.setTableName("PorterResponseMonitor");
		monitorTemplate.setPrimaryKeyField("MonitorId");
		monitorTemplate.setFieldMapper(monitorFieldMapper);

		ruleTemplate = new SimpleTableAccessTemplate<StoredPorterResponseMonitorRule>(yukonJdbcTemplate, nextValueHelper);
		ruleTemplate.setTableName("PorterResponseMonitorRule");
		ruleTemplate.setPrimaryKeyField("RuleId");
		ruleTemplate.setFieldMapper(ruleFieldMapper);

		errorCodeTemplate = new SimpleTableAccessTemplate<StoredPorterResponseMonitorErrorCode>(yukonJdbcTemplate, nextValueHelper);
		errorCodeTemplate.setTableName("PorterResponseMonitorErrorCode");
		errorCodeTemplate.setPrimaryKeyField("ErrorCodeId");
		errorCodeTemplate.setFieldMapper(errorCodeFieldMapper);
	}

	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}

	@Autowired
	public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
}

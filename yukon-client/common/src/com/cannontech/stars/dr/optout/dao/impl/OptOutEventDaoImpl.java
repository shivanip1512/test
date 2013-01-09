package com.cannontech.stars.dr.optout.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.cannontech.common.util.ChunkingMappedSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.InventoryDao;
import com.cannontech.stars.dr.hardware.model.HardwareSummary;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;
import com.cannontech.stars.dr.optout.dao.OptOutEventDao;
import com.cannontech.stars.dr.optout.model.OptOutAction;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutEvent;
import com.cannontech.stars.dr.optout.model.OptOutEventDto;
import com.cannontech.stars.dr.optout.model.OptOutEventState;
import com.cannontech.stars.dr.optout.model.OptOutLog;
import com.cannontech.stars.dr.optout.model.OverrideHistory;
import com.cannontech.stars.dr.optout.model.OverrideStatus;
import com.cannontech.stars.dr.program.model.Program;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

/**
 * Implementation class for OptOutEventDao
 */
public class OptOutEventDaoImpl implements OptOutEventDao {

	private YukonJdbcTemplate yukonJdbcTemplate;
	private NextValueHelper nextValueHelper;
	
	private EnrollmentDao enrollmentDao;
	private InventoryDao inventoryDao;
	private CustomerAccountDao customerAccountDao;
	private YukonUserDao yukonUserDao;

	@Override
	@Transactional
	public OptOutLog save(OptOutEvent event, OptOutAction action, LiteYukonUser user) {

		SqlStatementBuilder eventSql = new SqlStatementBuilder();
		if(event.getEventId() == null) {
			// No event id, do an insert
			int eventId = nextValueHelper.getNextValue("OptOutEvent");
			event.setEventId(eventId);
			
			eventSql.append("INSERT INTO OptOutEvent");
			eventSql.append("(InventoryId, CustomerAccountId, ScheduledDate, StartDate, StopDate");
			eventSql.append(", EventCounts, EventState, OptOutEventId)");
			eventSql.append("VALUES (?,?,?,?,?,?,?,?)");
		} else {
			// event id exists, do an update
			eventSql.append("UPDATE OptOutEvent");
			eventSql.append("SET InventoryId = ?, CustomerAccountId = ?, ScheduledDate = ?");
			eventSql.append(", StartDate = ?, StopDate = ?, EventCounts = ?, EventState = ?");
			eventSql.append("WHERE OptOutEventId = ?");
		}

		// Add or Update opt out event
		Integer inventoryId = event.getInventoryId();
		Integer customerAccountId = event.getCustomerAccountId();
		Instant scheduledDate = event.getScheduledDate();
		Instant startDate = event.getStartDate();
		Instant stopDate = event.getStopDate();
		String eventCounts = event.getEventCounts().toString();

		yukonJdbcTemplate.update(eventSql.toString(), 
				inventoryId, 
				customerAccountId,
				SqlStatementBuilder.convertArgumentToJdbcObject(scheduledDate),
				SqlStatementBuilder.convertArgumentToJdbcObject(startDate),
				SqlStatementBuilder.convertArgumentToJdbcObject(stopDate),
				eventCounts,
				event.getState().toString(),
				event.getEventId());
		
		OptOutLog log = new OptOutLog();
		log.setAction(action);
		log.setCustomerAccountId(customerAccountId);
		log.setEventCounts(event.getEventCounts());
		log.setEventId(event.getEventId());
		log.setInventoryId(inventoryId);
		log.setStartDate(startDate);
		log.setStopDate(stopDate);
		log.setUserId(user.getUserID());
		
		// Add OptOutEventLog entry
		saveOptOutLog(log);

		return log;
	}
	
	@Override
	@Transactional
	public void saveOptOutLog(OptOutLog optOutLog) {

		SqlStatementBuilder logSql = new SqlStatementBuilder();
		logSql.append("INSERT INTO OptOutEventLog");
		logSql.append("(OptOutEventLogId, InventoryId, CustomerAccountId, EventAction, LogDate");
		logSql.append(", EventStartDate, EventStopDate, LogUserId, OptOutEventId, EventCounts)");
		logSql.append("VALUES (?,?,?,?,?,?,?,?,?,?)");
		
		int logId = nextValueHelper.getNextValue("OptOutEventLog");
		optOutLog.setLogId(logId);
		optOutLog.setLogDate(new Instant());
		
		yukonJdbcTemplate.update(
				logSql.toString(), 
				optOutLog.getLogId(),
				optOutLog.getInventoryId(),
				optOutLog.getCustomerAccountId(),
				optOutLog.getAction().toString(),
				SqlStatementBuilder.convertArgumentToJdbcObject(optOutLog.getLogDate()),
				SqlStatementBuilder.convertArgumentToJdbcObject(optOutLog.getStartDate()),
				SqlStatementBuilder.convertArgumentToJdbcObject(optOutLog.getStopDate()),
				optOutLog.getUserId(),
				optOutLog.getEventId(),
				optOutLog.getEventCounts().toString());
		
	}

	@Override
	public boolean isOptedOut(int inventoryId, int customerAccountId) {

        Instant now = new Instant();
        
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT * ");
		sql.append("FROM OptOutEvent ");
		sql.append("WHERE InventoryId").eq(inventoryId);
		sql.append("    AND CustomerAccountId").eq(customerAccountId);
		sql.append("    AND StartDate").lte(now);
		sql.append("    AND StopDate").gt(now);
		sql.append("    AND EventState").eq(OptOutEventState.START_OPT_OUT_SENT.toString());
		
		try {
			yukonJdbcTemplate.queryForObject(sql, new OptOutEventRowMapper());
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
		
		return true;

	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public List<OptOutEventDto> getOptOutHistoryForAccount(
			int customerAccountId, int... numberOfRecords) {
		
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT * ");
		sql.append("FROM OptOutEvent ");
		sql.append("WHERE CustomerAccountId = ?");
		sql.append("	AND ((StartDate < ? AND StopDate < ?) OR (EventState = ?))");
		
		Date now = new Date();
		
		Integer maxNumberOfRecords = null;
		List<OptOutEventDto> eventList;
		if (numberOfRecords != null && numberOfRecords.length == 1) {
			// If a max records was supplied, only process that number of records
			maxNumberOfRecords = numberOfRecords[0];
		} else {
			// Process and return all records
		    maxNumberOfRecords = Integer.MAX_VALUE;
		}
        eventList = (List<OptOutEventDto>) yukonJdbcTemplate.getJdbcOperations().query(
                                                sql.toString(), 
                                                new Object[]{customerAccountId, now, now, OptOutEventState.SCHEDULE_CANCELED.toString()}, 
                                                new OptOutEventDtoExtractor(maxNumberOfRecords));
		
		return eventList;
	}

	@Override
	@Transactional(readOnly=true)
    public Multimap<OptOutEventDto, OptOutLog> getOptOutEventDetails(
            Iterable<OptOutEventDto> optOutEvents) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT optOutEventLogId, inventoryId, customerAccountId,");
                sql.append(    "eventAction, logDate, eventStartDate, eventStopDate,");
                sql.append(    "logUserId, username, optOutEventId, eventCounts");
                sql.append("FROM optOutEventLog");
                sql.append(    "JOIN yukonUser on userId = logUserId");
                sql.append("WHERE optOutEventId").in(subList);
                sql.append("ORDER BY optOutEventId DESC, logDate DESC");
                return sql;
            }
        };

        final ParameterizedRowMapper<Map.Entry<Integer, OptOutLog>> rowMapper =
            new ParameterizedRowMapper<Map.Entry<Integer, OptOutLog>>() {
            @Override
            public Map.Entry<Integer, OptOutLog> mapRow(ResultSet rs, int rowNum)
                    throws SQLException {
                OptOutLog value = new OptOutLog();
                value.setLogId(rs.getInt("optOutEventLogId"));
                value.setInventoryId(rs.getInt("inventoryId"));
                value.setCustomerAccountId(rs.getInt("customerAccountId"));
                value.setAction(OptOutAction.valueOf(rs.getString("eventAction")));
                Timestamp timestamp = rs.getTimestamp("logDate");
                value.setLogDate(timestamp == null ? null
                        : new Instant(timestamp));
                timestamp = rs.getTimestamp("eventStartDate");
                value.setStartDate(timestamp == null ? null
                        : new Instant(timestamp));
                timestamp = rs.getTimestamp("eventStopDate");
                value.setStopDate(timestamp == null ? null
                        : new Instant(timestamp));
                value.setUserId(rs.getInt("logUserId"));
                value.setUsername(rs.getString("username"));
                value.setEventId(rs.getInt("optOutEventId"));
                value.setEventCounts(OptOutCounts.valueOf(rs.getString("eventCounts")));
                return Maps.immutableEntry(value.getEventId(), value);
            }
        };

        Function<OptOutEventDto, Integer> typeMapper = new Function<OptOutEventDto, Integer>() {
            @Override
            public Integer apply(OptOutEventDto from) {
                return from.getEventId();
            }
        };

        ChunkingMappedSqlTemplate chunkingTemplate = new ChunkingMappedSqlTemplate(yukonJdbcTemplate);
        return chunkingTemplate.multimappedQuery(sqlGenerator, optOutEvents, rowMapper, typeMapper);
	}

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
	public List<OverrideHistory> getOptOutHistoryForAccount(int accountId, Date startDate, Date stopDate) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT * ");
		sql.append("FROM OptOutEvent ");
		sql.append("WHERE CustomerAccountId = ? ");
		sql.append("	AND StartDate <= ?");
		sql.append("	AND StopDate >= ?");
		sql.append("	AND (EventState = ? ");
		sql.append("		OR EventState = ?) ");
		sql.append("ORDER BY StartDate DESC");
		
		List<OverrideHistory> historyList = yukonJdbcTemplate.query(
													sql.toString(), 
													new OverrideHistoryRowMapper(),
													accountId,
													stopDate,
													startDate,
													OptOutEventState.START_OPT_OUT_SENT.toString(),
													OptOutEventState.CANCEL_SENT.toString());
		
		return historyList;
	}
	
    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public List<OverrideHistory> getOptOutHistoryForAccount(int accountId, ReadableInstant startDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT OOE.*");
        sql.append("FROM OptOutEvent OOE");
        sql.append("  JOIN CustomerAccount CA ON CA.AccountId = OOE.CustomerAccountId");
        sql.append("  JOIN Customer Cust ON Cust.CustomerId = CA.CustomerId");
        sql.append("  JOIN Contact Cont ON Cont.ContactId = Cust.PrimaryContactId");
        sql.append("  JOIN YukonUser YU ON YU.UserId = Cont.LoginId");
        sql.append("  JOIN UserGroupToYukonGroupMapping UGYGM ON UGYGM.UserGroupId =YU.UserGroupId");
        sql.append("WHERE OOE.CustomerAccountId").eq(accountId);
        sql.append("  AND UGYGM.GroupId").eq(residentialGroup.getGroupID());
        sql.append("  AND OOE.StartDate").lte(stopDate);
        sql.append("  AND OOE.StopDate").gte(startDate);
        sql.append("  AND (OOE.EventState").eq_k(OptOutEventState.START_OPT_OUT_SENT);
        sql.append("            OR OOE.EventState").eq_k(OptOutEventState.CANCEL_SENT).append(")");
        sql.append("ORDER BY OOE.StartDate DESC");
        
        List<OverrideHistory> historyList = yukonJdbcTemplate.query(sql, new OverrideHistoryRowMapper());
        return historyList;
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
	public List<OverrideHistory> getOptOutHistoryForInventory(int inventoryId, Date startDate, Date stopDate) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT * ");
		sql.append("FROM OptOutEvent ");
		sql.append("WHERE InventoryId = ? ");
		sql.append("	AND StartDate <= ?");
		sql.append("	AND StopDate >= ?");
		sql.append("	AND (EventState = ? ");
		sql.append("		OR EventState = ?) ");
		sql.append("ORDER BY StartDate DESC");
		
		List<OverrideHistory> historyList = yukonJdbcTemplate.query(
													sql.toString(), 
													new OverrideHistoryRowMapper(),
													inventoryId,
													stopDate,
													startDate,
													OptOutEventState.START_OPT_OUT_SENT.toString(),
													OptOutEventState.CANCEL_SENT.toString());
		
		return historyList;
	}

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public List<OverrideHistory> getOptOutHistoryForInventory(int inventoryId, ReadableInstant startDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT OOE.* ");
        sql.append("FROM OptOutEvent OOE");
        sql.append("  JOIN CustomerAccount CA ON CA.AccountId = OOE.CustomerAccountId");
        sql.append("  JOIN Customer Cust ON Cust.CustomerId = CA.CustomerId");
        sql.append("  JOIN Contact Cont ON Cont.ContactId = Cust.PrimaryContactId");
        sql.append("  JOIN YukonUser YU ON YU.UserId = Cont.LoginId");
        sql.append("  JOIN UserGroupToYukonGroupMapping UGYGM ON UGYGM.UserGroupId = YU.UserGroupId");
        sql.append("WHERE OOE.InventoryId").eq(inventoryId);
        sql.append("  AND UGYGM.GroupId").eq(residentialGroup.getGroupID());
        sql.append("  AND OOE.StartDate").lte(stopDate);
        sql.append("  AND OOE.StopDate").gte(startDate);
        sql.append("  AND (OOE.EventState").eq_k(OptOutEventState.START_OPT_OUT_SENT);
        sql.append("            OR OOE.EventState").eq_k(OptOutEventState.CANCEL_SENT).append(")");
        sql.append("ORDER BY OOE.StartDate DESC");
        
        List<OverrideHistory> historyList = yukonJdbcTemplate.query(sql,  new OverrideHistoryRowMapper());
        
        return historyList;
    }

    @Override
    @Transactional(propagation=Propagation.SUPPORTS)
    public List<OverrideHistory> getOptOutHistoryByLogUserId(int logUserId, ReadableInstant startDate, ReadableInstant stopDate, LiteYukonGroup residentialGroup) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT OOE.* ");
        sql.append("FROM OptOutEvent OOE");
        sql.append("  JOIN CustomerAccount CA ON CA.AccountId = OOE.CustomerAccountId");
        sql.append("  JOIN Customer Cust ON Cust.CustomerId = CA.CustomerId");
        sql.append("  JOIN Contact Cont ON Cont.ContactId = Cust.PrimaryContactId");
        sql.append("  JOIN YukonUser YU ON YU.UserId = Cont.LoginId");
        sql.append("  JOIN UserGroupToYukonGroupMapping UGYGM ON UGYGM.UserGroupId = YU.UserGroupId");
        sql.append("WHERE OOE.OptOutEventId").in(getEventsByUserId(logUserId));
        sql.append("  AND UGYGM.GroupId").eq(residentialGroup.getGroupID());
        sql.append("  AND OOE.StartDate").lte(stopDate);
        sql.append("  AND OOE.StopDate").gte(startDate);
        sql.append("  AND (OOE.EventState").eq_k(OptOutEventState.START_OPT_OUT_SENT);
        sql.append("             OR OOE.EventState").eq_k(OptOutEventState.CANCEL_SENT).append(")");
        sql.append("ORDER BY OOE.StartDate DESC");
        
        List<OverrideHistory> historyList = yukonJdbcTemplate.query(sql, new OverrideHistoryRowMapper());
        
        return historyList;
    }

	@Override
	public OptOutEvent getOptOutEventById(int eventId) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT * ");
		sql.append("FROM OptOutEvent ");
		sql.append("WHERE OptOutEventId").eq(eventId);
		
		OptOutEvent event = yukonJdbcTemplate.queryForObject(sql, new OptOutEventRowMapper());
		
		return event;
	}
	
	@Override
	public OptOutEvent findLastEvent(int inventoryId) {
		
		// In English: 
		// Select the most recent event for the inventory
		// with a start date earlier than or equal to today and a current state of ACTIVE_SENT 
		// or CANCEL_SENT
		SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM OptOutEvent");
        sql.append("WHERE StartDate").lte(new Instant());
        sql.append("    AND (EventState").eq(OptOutEventState.START_OPT_OUT_SENT.toString());
        sql.append("         OR EventState").eq(OptOutEventState.CANCEL_SENT.toString()).append(")");
        sql.append("    AND InventoryId").eq(inventoryId);
        sql.append("ORDER BY StopDate DESC");
        
        List<OptOutEvent> events = 
            yukonJdbcTemplate.queryForLimitedResults(sql, new OptOutEventRowMapper(), 1);
        OptOutEvent event = (events.size() > 0) ? events.get(0) : null;

        return event;
	}
	

	@Override
	public OptOutEvent getScheduledOptOutEvent(int inventoryId, int customerAccountId) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT * ");
		sql.append("FROM OptOutEvent ");
		sql.append("WHERE InventoryId ").eq(inventoryId);
		sql.append("    AND CustomerAccountId ").eq(customerAccountId);
		sql.append("    AND EventState ").eq(OptOutEventState.SCHEDULED.toString());
		sql.append("    AND StartDate ").gt(new Instant());
		
		OptOutEvent event = null;
		try {
			event = yukonJdbcTemplate.queryForObject(sql, new OptOutEventRowMapper());
		} catch(EmptyResultDataAccessException e) {
			// no scheduled event, return null
			return null;
		}
		
		return event;
	}
	
	@Override
	public OptOutEvent getOverdueScheduledOptOut(Integer inventoryId,
			int customerAccountId) {

        Instant now = new Instant();
	    
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT * ");
		sql.append("FROM OptOutEvent ");
		sql.append("WHERE InventoryId ").eq(inventoryId);
		sql.append("    AND CustomerAccountId ").eq(customerAccountId);
		sql.append("    AND EventState ").eq(OptOutEventState.SCHEDULED.toString());
		sql.append("    AND StartDate ").lte(now);
		sql.append("    AND StopDate ").gt(now);
		
		OptOutEvent event = null;
		try {
			event = yukonJdbcTemplate.queryForObject(sql, new OptOutEventRowMapper());
		} catch(EmptyResultDataAccessException e) {
			// no overdue scheduled event, return null
			return null;
		}
		
		return event;
	}

	@Override
	public List<OptOutEvent> getAllScheduledOptOutEvents(int customerAccountId) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT * ");
		sql.append("FROM OptOutEvent ");
		sql.append("WHERE CustomerAccountId ").eq(customerAccountId);
		sql.append("    AND EventState ").eq(OptOutEventState.SCHEDULED.toString());
		sql.append("    AND StartDate ").gt(new Instant());
		
		List<OptOutEvent> eventList = 
		    yukonJdbcTemplate.query(sql, new OptOutEventRowMapper());
		return eventList;
	}
	
	@Override
	public List<OptOutEvent> getAllScheduledOptOutEvents(LiteStarsEnergyCompany energyCompany) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ooe.* ");
		sql.append("FROM OptOutEvent ooe");
		sql.append("JOIN ECToAccountMapping ectam ON ectam.AccountId = ooe.CustomerAccountId");
		sql.append("WHERE ooe.EventState ").eq(OptOutEventState.SCHEDULED.toString());
		sql.append("	AND ooe.StartDate ").gt(new Instant());
		sql.append("	AND ectam.EnergyCompanyId ").eq(energyCompany.getEnergyCompanyId());
		
		List<OptOutEvent> eventList = yukonJdbcTemplate.query(sql, new OptOutEventRowMapper());
		return eventList;
	}

	@Override
	@Transactional
	public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId, int inventoryId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT OptOutEventId, InventoryId, ScheduledDate, StartDate, StopDate, EventState");
		sql.append("FROM OptOutEvent");
		sql.append("WHERE CustomerAccountId = ? ");
		sql.append("AND InventoryId = ? ");
		sql.append("AND ((EventState = ? ");
		sql.append("	  AND StopDate > ?) ");
		sql.append("	 OR EventState = ?) ");

		List<OptOutEventDto> eventList = yukonJdbcTemplate.query(
														sql.toString(), 
														new OptOutEventDtoRowMapper(), 
														customerAccountId,
														inventoryId,
														OptOutEventState.START_OPT_OUT_SENT.toString(),
														new Date(),
														OptOutEventState.SCHEDULED.toString());
		
		return eventList;
	}
	
	@Override
	@Transactional
	public List<OptOutEventDto> getCurrentOptOuts(int customerAccountId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT OptOutEventId, InventoryId, ScheduledDate, StartDate, StopDate, EventState");
		sql.append("FROM OptOutEvent");
		sql.append("WHERE CustomerAccountId = ? ");
		sql.append("	AND ((EventState = ? ");
		sql.append("			AND StopDate > ?) ");
		sql.append("		OR EventState = ?) ");

		List<OptOutEventDto> eventList = yukonJdbcTemplate.query(
														sql.toString(), 
														new OptOutEventDtoRowMapper(), 
														customerAccountId,
														OptOutEventState.START_OPT_OUT_SENT.toString(),
														new Date(),
														OptOutEventState.SCHEDULED.toString());
		
		return eventList;
	}

	@Override
	public Integer getNumberOfOptOutsUsed(int inventoryId, int customerAccountId, Instant startDate, Instant endDate) {
		
		// Count the number of opt outs used in the time period
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT COUNT(*)");
		sql.append("FROM OptOutEvent");
		sql.append("WHERE InventoryId").eq(inventoryId);
		sql.append("	AND CustomerAccountId").eq(customerAccountId);
		sql.append("	AND (EventState").eq(OptOutEventState.START_OPT_OUT_SENT);
		sql.append("		OR EventState").eq(OptOutEventState.CANCEL_SENT).append(")");
		sql.append("	AND EventCounts").eq(OptOutCounts.COUNT);
		sql.append("	AND StartDate").lte(endDate);
		sql.append("	AND StopDate").gte(startDate);
		
		int usedOptOuts = yukonJdbcTemplate.queryForInt(sql);
		
		return usedOptOuts;
	}
	

	@Override
    public int getTotalNumberOfActiveOptOuts(YukonEnergyCompany yukonEnergyCompany, List<Integer> assignedProgramIds) {

        Instant now = Instant.now();
		
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(1)");
        sql.append("FROM OptOutEvent OOE");
        sql.append("  JOIN ECToAccountMapping ectam ON OOE.CustomerAccountId = ECTAM.AccountId");
        // If the assigned programs are supplied let's use the active enrollments of those programs to calculate the total number of accounts.
        if (!CollectionUtils.isEmpty(assignedProgramIds)) {
            sql.append("  JOIN LMHardwareControlGroup LMHCG ON (ECTAM.AccountId = LMHCG.AccountId  AND " +
                                                                                                  " LMHCG.GroupEnrollStart IS NOT NULL AND " +
                                                                                                  " LMHCG.GroupEnrollStop IS NULL)");
        }

		sql.append("WHERE OOE.EventState").eq_k(OptOutEventState.START_OPT_OUT_SENT);
        sql.append("	AND OOE.StartDate").lte(now);
        sql.append("	AND OOE.StopDate").gte(now);
        sql.append("	AND ECTAM.EnergyCompanyId").eq_k(yukonEnergyCompany.getEnergyCompanyId());
        if (!CollectionUtils.isEmpty(assignedProgramIds)) {
            sql.append("  AND LMHCG.ProgramId").in(assignedProgramIds);
        }
		
		return yukonJdbcTemplate.queryForInt(sql);
	}
	
	@Override
	public List<Integer> getOptedOutDeviceIdsForAccount(int accountId, Date startTime, Date stopTime) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DISTINCT InventoryId");
		sql.append("FROM OptOutEvent");
		sql.append("WHERE (EventState = ?");
		sql.append("	   OR EventState = ?)");
		sql.append("	AND StartDate <= ?");
		sql.append("	AND StopDate >= ?");
		sql.append("	AND CustomerAccountId = ?");
		
		List<Integer> optOutDeviceList = 
			yukonJdbcTemplate.query(sql.toString(), new IntegerRowMapper(),
					OptOutEventState.START_OPT_OUT_SENT.toString(),
					OptOutEventState.CANCEL_SENT.toString(),
					stopTime,
					startTime,
					accountId);
		
		return optOutDeviceList;
	}

	@Override
    public int getTotalNumberOfScheduledOptOuts(YukonEnergyCompany yukonEnergyCompany, List<Integer> assignedProgramIds) {
		
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(1)");
        sql.append("FROM OptOutEvent OOE");
        sql.append("	JOIN ECToAccountMapping ECTAM ON OOE.CustomerAccountId = ECTAM.AccountId");
	    // If the assigned programs are supplied let's use the active enrollments of those programs to calculate the total number of accounts.
        if (!CollectionUtils.isEmpty(assignedProgramIds)) {
                    sql.append("  JOIN LMHardwareControlGroup LMHCG ON (ECTAM.AccountId = LMHCG.AccountId  AND " +
                                                                                                          " LMHCG.GroupEnrollStart IS NOT NULL AND " +
                                                                                                          " LMHCG.GroupEnrollStop IS NULL)");
        }

		sql.append("WHERE OOE.EventState").eq_k(OptOutEventState.SCHEDULED);
        sql.append("	AND ECTAM.EnergyCompanyId").eq_k(yukonEnergyCompany.getEnergyCompanyId());
        if (!CollectionUtils.isEmpty(assignedProgramIds)) {
            sql.append("  AND LMHCG.ProgramId").in(assignedProgramIds);
        }

		return yukonJdbcTemplate.queryForInt(sql);
	}

	@Override
	public List<OptOutEvent> getAllCurrentOptOuts(LiteStarsEnergyCompany energyCompany) {

		Instant now = new Instant();
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ooe.*");
		sql.append("FROM OptOutEvent ooe");
		sql.append("	JOIN ECToAccountMapping ectam ON ooe.CustomerAccountId = ectam.AccountId");
		sql.append("WHERE ooe.EventState ").eq(OptOutEventState.START_OPT_OUT_SENT.toString());
		sql.append("	AND ooe.StartDate ").lte(now);
		sql.append("	AND ooe.StopDate ").gte(now);
		sql.append("	AND ectam.EnergyCompanyId ").eq(energyCompany.getEnergyCompanyId());
		
		List<OptOutEvent> optOutEvents = yukonJdbcTemplate.query(sql, new OptOutEventRowMapper());
		return optOutEvents;
	}
	
	@Override
	public List<OptOutEvent> getAllCurrentOptOutsByProgramId(int webpublishingProgramId, LiteStarsEnergyCompany energyCompany) {

		Date now = new Date();
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ooe.*");
		sql.append("FROM OptOutEvent ooe");
		sql.append("	JOIN ECToAccountMapping ectam ON ooe.CustomerAccountId = ectam.AccountId");
		sql.append("	JOIN LMHardwareControlGroup lmhcg ON (ooe.InventoryId = lmhcg.InventoryId)");
		sql.append("	JOIN LMProgramWebPublishing pwp ON (lmhcg.ProgramId = pwp.ProgramId)");
		sql.append("WHERE ooe.EventState").eq(OptOutEventState.START_OPT_OUT_SENT.toString());
		sql.append("	AND ooe.StartDate").lte(now);
		sql.append("	AND ooe.StopDate").gte(now);
		sql.append("	AND ectam.EnergyCompanyId").eq(energyCompany.getEnergyCompanyId());
		
		sql.append("	AND pwp.ProgramId").eq(webpublishingProgramId);
		sql.append("	AND lmhcg.Type").eq(LMHardwareControlGroup.OPT_OUT_ENTRY);
		sql.append("	AND lmhcg.ProgramId > 0");
		sql.append("	AND lmhcg.OptOutStart IS NOT NULL");
		sql.append("	AND lmhcg.OptOutStop IS NULL");
		
		List<OptOutEvent> optOutEvents = yukonJdbcTemplate.query(sql, new OptOutEventRowMapper());
		
		return optOutEvents;
	}

	@Override
	@Transactional
	public void changeCurrentOptOutCountState(LiteStarsEnergyCompany energyCompany, OptOutCounts counts) {
		doChangeCurrentOptOutCountState(energyCompany, counts, null);
	}
	
	@Override
	@Transactional
	public void changeCurrentOptOutCountStateForProgramId(LiteStarsEnergyCompany energyCompany, OptOutCounts counts, int webpublishingProgramId) {
		doChangeCurrentOptOutCountState(energyCompany, counts, webpublishingProgramId);
	}
	
	private void doChangeCurrentOptOutCountState(LiteStarsEnergyCompany energyCompany, OptOutCounts counts, Integer webpublishingProgramId) {
		
		Date now = new Date();
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("UPDATE OptOutEvent");
		sql.append("SET EventCounts").eq(counts);
		sql.append("WHERE EXISTS (");
		sql.append("  SELECT ooe.OptOutEventId");
		sql.append("  FROM OptOutEvent ooe");
		sql.append("  JOIN ECToAccountMapping ectam ON ooe.CustomerAccountId = ectam.AccountId");
		
		if (webpublishingProgramId != null) {
			sql.append("  JOIN LMHardwareControlGroup lmhcg ON (lmhcg.InventoryId = ooe.InventoryId)");
		}
		
		sql.append("  WHERE ooe.EventState").eq(OptOutEventState.START_OPT_OUT_SENT.toString());
		sql.append("	AND ooe.StartDate").lte(now);
		sql.append("	AND ooe.StopDate").gte(now);
		sql.append("	AND ectam.EnergyCompanyId").eq(energyCompany.getEnergyCompanyId());
		//this is the ANSI-equivalent of JOIN
		sql.append("    AND OptOutEvent.OptOutEventId = ooe.OptOutEventId");
		
		if (webpublishingProgramId != null) {
			sql.append("    AND lmhcg.ProgramId").eq(webpublishingProgramId);
			sql.append("    AND lmhcg.type").eq(LMHardwareControlGroup.OPT_OUT_ENTRY);
			sql.append("    AND lmhcg.OptOutStart IS NOT NULL");
			sql.append("    AND lmhcg.OptOutStop IS NULL");
		}
		
		sql.append(")");
		
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public List<OptOutEvent> getScheduledOptOutsToBeStarted() {
		
		Instant now = new Instant();
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT *");
		sql.append("FROM OptOutEvent");
		sql.append("WHERE EventState ").eq(OptOutEventState.SCHEDULED.toString());
		sql.append("	AND StartDate ").lte(now);
		sql.append("	AND StopDate ").gt(now);
		
		List<OptOutEvent> scheduledOptOuts = 
		    yukonJdbcTemplate.query(sql, new OptOutEventRowMapper());
		return scheduledOptOuts;
	}

    private int getFirstEventUser(int optOutEventId) {

	    SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LogUserId");
        sql.append("FROM OptOutEventLog ");
        sql.append("WHERE OptOutEventId ").eq(optOutEventId);
        sql.append("ORDER BY LogDate ASC ");
		
        List<Integer> userIds = yukonJdbcTemplate.queryForLimitedResults(sql, new IntegerRowMapper(), 1);
        int userId = (userIds.size() > 0) ? userIds.get(0) : 0;
		
		return userId;
	}

    private SqlStatementBuilder getEventsByUserId(int userId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT OptOutEventId");
        sql.append("FROM OptOutEventLog ");
        sql.append("WHERE LogUserId").eq(userId);
        
        return sql;
    }
	
	/**
	 * Helper class to map a result set row into an OptOutEvent
	 */
	private class OptOutEventRowMapper implements
			YukonRowMapper<OptOutEvent> {

		@Override
		public OptOutEvent mapRow(YukonResultSet rs) throws SQLException {

			OptOutEvent event = new OptOutEvent();
			event.setEventId(rs.getInt("OptOutEventId"));
			event.setInventoryId(rs.getInt("InventoryId"));
			event.setCustomerAccountId(rs.getInt("CustomerAccountId"));
			event.setScheduledDate(rs.getInstant("ScheduledDate"));
			event.setStartDate(rs.getInstant("StartDate"));
			event.setStopDate(rs.getInstant("StopDate"));
			
			String eventCountsString = rs.getString("EventCounts");
			OptOutCounts eventCounts = OptOutCounts.valueOf(eventCountsString);
			event.setEventCounts(eventCounts);
			
			String eventStateString = rs.getString("EventState");
			OptOutEventState eventState = OptOutEventState.valueOf(eventStateString);
			event.setState(eventState);

			return event;
		}

	}
	
	/**
	 * Helper class to map a result set row into an OptOutEventDto. This class will only
	 * process the number of records specified in the constructor arg.  This class must
	 * be used in a transaction because it has a db hit inside the mapRow method.
	 */
	private class OptOutEventDtoExtractor implements ResultSetExtractor {

		private int maxNumberOfRecords = Integer.MAX_VALUE;
		
		public OptOutEventDtoExtractor(int maxNumberOfRecords) {
			this.maxNumberOfRecords = maxNumberOfRecords;
		}
		
		@Override
		public Object extractData(ResultSet rs) throws SQLException,
				DataAccessException {

			List<OptOutEventDto> eventList = new ArrayList<OptOutEventDto>();
			
			while(rs.next()){
				OptOutEventDto event = new OptOutEventDto();
				event.setEventId(rs.getInt("OptOutEventId"));
				event.setScheduledDate(rs.getTimestamp("ScheduledDate"));
				event.setStartDate(rs.getTimestamp("StartDate"));
				event.setStopDate(rs.getTimestamp("StopDate"));
				
				String eventStateString = rs.getString("EventState");
				OptOutEventState eventState = OptOutEventState.valueOf(eventStateString);
				event.setState(eventState);
				event.setInventoryId(rs.getInt("InventoryId"));
				
				eventList.add(event);
			}
			
			//sort here
			Collections.sort(eventList, new OptOutEventDto().getSorter());
			
			//get requested records here
			if (maxNumberOfRecords > eventList.size()) {
			    maxNumberOfRecords = eventList.size();
			}
			eventList = eventList.subList(0, maxNumberOfRecords);
			
			//get the additional event info here
			for (OptOutEventDto event: eventList) {
                HardwareSummary inventory = inventoryDao.findHardwareSummaryById(event.getInventoryId());
		        event.setInventory(inventory);
                    
                List<Program> programList = 
                    enrollmentDao.getEnrolledProgramIdsByInventory(event.getInventoryId(),
                                                                   event.getStartDate(),
                                                                   event.getStopDate());
                event.setProgramList(programList);
			}
			
			return eventList;
		}
		
	}

	/**
	 * Helper class to map a result set row into an OptOutEventDto. This class must
	 * be used in a transaction because it has a db hit inside the mapRow method.
	 */
	private class OptOutEventDtoRowMapper implements ParameterizedRowMapper<OptOutEventDto> {
		
		public OptOutEventDtoRowMapper(){}
		
		@Override
		public OptOutEventDto mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			OptOutEventDto event = new OptOutEventDto();
			event.setEventId(rs.getInt("OptOutEventId"));
			event.setScheduledDate(rs.getTimestamp("ScheduledDate"));
			event.setStartDate(rs.getTimestamp("StartDate"));
			event.setStopDate(rs.getTimestamp("StopDate"));
			
			String eventStateString = rs.getString("EventState");
			OptOutEventState eventState = OptOutEventState.valueOf(eventStateString);
			event.setState(eventState);
			
			int inventoryId = rs.getInt("InventoryId");
			HardwareSummary inventory = inventoryDao.findHardwareSummaryById(inventoryId);
			event.setInventory(inventory);
			
			List<Program> programList = 
				enrollmentDao.getEnrolledProgramIdsByInventory(inventoryId,
                                                               event.getStartDate(),
                                                               event.getStopDate());
			event.setProgramList(programList);
			
				
			return event;
		}
		
	}
	
	private class OverrideHistoryRowMapper implements ParameterizedRowMapper<OverrideHistory> {

		@Override
		public OverrideHistory mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			OverrideHistory history = new OverrideHistory();

			int accountId = rs.getInt("CustomerAccountId");
			CustomerAccount account = customerAccountDao.getById(accountId);
			history.setAccountNumber(account.getAccountNumber());
			history.setOverrideNumber(rs.getInt("OptOutEventId"));

			String eventStateString = rs.getString("EventState");
			OptOutEventState eventState = OptOutEventState.valueOf(eventStateString);
			history.setStatus(OverrideStatus.valueOf(eventState));
			
			String eventCountsString = rs.getString("EventCounts");
			OptOutCounts eventCounts = OptOutCounts.valueOf(eventCountsString);
			history.setCountedAgainstLimit(eventCounts.isCounts());
			
			history.setScheduledDate(rs.getTimestamp("ScheduledDate"));
			history.setStartDate(rs.getTimestamp("StartDate"));
			history.setStopDate(rs.getTimestamp("StopDate"));

			int inventoryId = rs.getInt("InventoryId");
			history.setInventoryId(inventoryId);
			HardwareSummary inventory = inventoryDao.findHardwareSummaryById(inventoryId);
			if (inventory != null){
			    history.setSerialNumber(inventory.getSerialNumber());
			    
			}
			List<Program> enrolledPrograms = 
			    enrollmentDao.getEnrolledProgramIdsByInventory(inventoryId, 
			                                                   history.getStartDate(), 
			                                                   history.getStopDate());
			history.setPrograms(enrolledPrograms);

			int eventId = rs.getInt("OptOutEventId");
			int userId = getFirstEventUser(eventId);
			LiteYukonUser liteYukonUser = yukonUserDao.getLiteYukonUser(userId);
			if(liteYukonUser != null) {
				history.setUserName(liteYukonUser.getUsername());
			} else {
				history.setUserName("User no longer exists");
			}
			
			return history;

		}
		
	}

	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
	
	@Autowired
	public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
	
	@Autowired
	public void setEnrollmentDao(EnrollmentDao enrollmentDao) {
		this.enrollmentDao = enrollmentDao;
	}
	
	@Autowired
    public void setInventoryDao(InventoryDao inventoryDao) {
        this.inventoryDao = inventoryDao;
    }
	
	@Autowired
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
		this.customerAccountDao = customerAccountDao;
	}
	
    @Autowired
	public void setYukonUserDao(YukonUserDao yukonUserDao) {
		this.yukonUserDao = yukonUserDao;
	}

}

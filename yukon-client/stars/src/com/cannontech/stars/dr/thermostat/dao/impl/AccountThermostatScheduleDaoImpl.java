package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.events.loggers.AccountEventLogService;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowAndFieldMapper;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.account.dao.CustomerAccountDao;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.hardware.model.SchedulableThermostatType;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleEntryDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.ThermostatScheduleMode;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.thermostat.service.ThermostatService;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class AccountThermostatScheduleDaoImpl implements AccountThermostatScheduleDao {

    private AccountEventLogService accountEventLogService;
    private CustomerAccountDao customerAccountDao;
	private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private AccountThermostatScheduleEntryDao accountThermostatScheduleEntryDao;
    private ECMappingDao ecMappingDao;
    private ThermostatService thermostatService;
    
    private SimpleTableAccessTemplate<AccountThermostatSchedule> accountThermostatScheduleTemplate;
    
    // SAVE
    @Override
    @Transactional
    public void save(AccountThermostatSchedule ats) {
        
    	accountThermostatScheduleTemplate.save(ats);
    	
    	// remove any current entries
    	accountThermostatScheduleEntryDao.removeAllEntriesForScheduleId(ats.getAccountThermostatScheduleId()); 	
    	
    	// insert new entries
    	List<AccountThermostatScheduleEntry> atsEntries = ats.getScheduleEntries();
    	for (AccountThermostatScheduleEntry atsEntry : atsEntries) {
    		atsEntry.setAccountThermostatScheduleId(ats.getAccountThermostatScheduleId());
    		atsEntry.setAccountThermostatScheduleEntryId(0); // these entries may already have an id if they were pulled from db. reset to 0 so they inserted (otherwise it'll try to update a now-non-existent entry)
    		accountThermostatScheduleEntryDao.save(atsEntry);
    	}
    	
    	// Logging
    	CustomerAccount customerAccount = customerAccountDao.getById(ats.getAccountId());
    	accountEventLogService.thermostatScheduleSaved(customerAccount.getAccountNumber(),
    	                                               ats.getScheduleName());
    }
    
    // GET BY ID
    @Override
    public AccountThermostatSchedule getById(int acctThermostatScheduleId) {
    	
    	return doGetByIds(acctThermostatScheduleId, null);
    }
    
    // GET BY ID AND ACCOUNT ID
    @Override
	public AccountThermostatSchedule findByIdAndAccountId(int acctThermostatScheduleId, int accountId) {
		
    	try {
    		return doGetByIds(acctThermostatScheduleId, accountId);
    	} catch (EmptyResultDataAccessException e) {
    		return null;
    	}
	}
    
    private AccountThermostatSchedule doGetByIds(int acctThermostatScheduleId, Integer accountId) {

    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT ats.*");
    	sql.append("FROM AcctThermostatSchedule ats");
    	sql.append("WHERE ats.AcctThermostatScheduleId").eq(acctThermostatScheduleId);
    	if (accountId != null) {
    		sql.append("AND ats.accountId").eq(accountId);
    	}
    	
    	return getPopulatedAccountThermostatSchedule(sql);
    }
    
    // GET BY INVENTORY ID
	public AccountThermostatSchedule findByInventoryId(int inventoryId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT ats.*");
    	sql.append("FROM AcctThermostatSchedule ats");
    	sql.append("  JOIN InventoryToAcctThermostatSch ITATS ON (ats.AcctThermostatScheduleId = ITATS.AcctThermostatScheduleId)");
    	sql.append("WHERE ITATS.InventoryId").eq(inventoryId);
    	
    	try {
    		return getPopulatedAccountThermostatSchedule(sql);
    	} catch (EmptyResultDataAccessException e) {
    		return null;
    	}
	}
	
	private AccountThermostatSchedule getPopulatedAccountThermostatSchedule(SqlFragmentSource sql) {
		
		AccountThermostatSchedule ats;
		List<AccountThermostatSchedule> atsList = yukonJdbcTemplate.query(sql, accountThermostatScheduleRowAndFieldMapper);
		if (atsList.size() == 0) {
			throw new EmptyResultDataAccessException(1); // expect at least one or throw the error for callers to handle
		}
		ats = atsList.get(0);
		
		List<AccountThermostatScheduleEntry> atsEntries = accountThermostatScheduleEntryDao.getAllEntriesForSchduleId(ats.getAccountThermostatScheduleId());
    	ats.setScheduleEntries(atsEntries);
    	
    	// determine ScheduleMode if null, save it immediately so going forward we won't have to guess what it is. See notes on setAssumedMode().
    	ThermostatScheduleMode scheduleMode = ats.getThermostatScheduleMode();
    	if (scheduleMode == null) {
    		setAssumedMode(ats);
    		save(ats);
    	}
    	
    	return ats;
	}

	// DELETE BY ID
	@Override
	@Transactional
	public void deleteById(int atsId) {
		AccountThermostatSchedule ats = getById(atsId);

		// delete InventoryToAcctThermostatSch
		SqlStatementBuilder deleteInventoryToAcctThermostatSch = new SqlStatementBuilder();
		deleteInventoryToAcctThermostatSch.append("DELETE FROM InventoryToAcctThermostatSch");
		deleteInventoryToAcctThermostatSch.append("WHERE AcctThermostatScheduleId").eq(atsId);
		yukonJdbcTemplate.update(deleteInventoryToAcctThermostatSch);
		
		// delete AcctThermostatScheduleEntry
		accountThermostatScheduleEntryDao.removeAllEntriesForScheduleId(atsId);
		
		// delete
		SqlStatementBuilder deleteAcctThermostatSchedule = new SqlStatementBuilder();
		deleteAcctThermostatSchedule.append("DELETE FROM AcctThermostatSchedule");
		deleteAcctThermostatSchedule.append("WHERE AcctThermostatScheduleId").eq(atsId);
		yukonJdbcTemplate.update(deleteAcctThermostatSchedule);

		CustomerAccount customerAccount = customerAccountDao.getById(ats.getAccountId());
        accountEventLogService.thermostatScheduleDeleted(customerAccount.getAccountNumber(),
                                                         ats.getScheduleName());
	}
    
	// DELETE ALL BY ACCOUNT ID
	@Override
	@Transactional
	public void deleteAllByAccountId(int accountId) {
		
		List<AccountThermostatSchedule> schedules = getAllSchedulesForAccountByType(accountId, null);
		for (AccountThermostatSchedule schedule : schedules) {
			deleteById(schedule.getAccountThermostatScheduleId());
		}
	}
	
	// DELETE BY INVENTORY ID
	@Override
	@Transactional
	public void deleteByInventoryId(int inventoryId) {
		
		AccountThermostatSchedule schedule = findByInventoryId(inventoryId);
		if (schedule != null) {
			deleteById(schedule.getAccountThermostatScheduleId());
		}
	}
	
	// GET ALL SCHEDULES FOR AN EC
	@Override
	@Transactional
	public List<AccountThermostatSchedule> getAllThermostatSchedulesForEC(int ecId){

	    SqlStatementBuilder sql = new SqlStatementBuilder();
	    sql.append("SELECT ats.*");
	    sql.append("FROM AcctThermostatSchedule ats");
	    sql.append("  JOIN ECToAcctThermostatSchedule ectats ON (ats.AcctThermostatScheduleId = ECTATS.AcctThermostatScheduleId)");
	    sql.append("WHERE ECTATS.EnergyCompanyId").eq(ecId);
	    
	    return yukonJdbcTemplate.query(sql, accountThermostatScheduleRowAndFieldMapper);
	}
	
    // GET EC DEFAULT SCHEDULE
    @Override
    @Transactional
    public AccountThermostatSchedule getEnergyCompanyDefaultScheduleByType(int ecId, SchedulableThermostatType type) {
    	
		try {
			
			SqlStatementBuilder sql = new SqlStatementBuilder();
	    	sql.append("SELECT ats.AcctThermostatScheduleId");
			sql.append("FROM AcctThermostatSchedule ats");
			sql.append("  JOIN ECToAcctThermostatSchedule ECTATS ON (ats.AcctThermostatScheduleId = ECTATS.AcctThermostatScheduleId)");
			sql.append("WHERE ECTATS.EnergyCompanyId").eq(ecId);
			sql.append("AND ats.ThermostatType").eq(type);
			
			int atsId = yukonJdbcTemplate.queryForInt(sql);
			return getById(atsId);
			
	    // create default
		} catch (EmptyResultDataAccessException e) {
			return createAndSaveDefaultSchedule(ecId, type);
		}
    }
    
    // GET EC DEFAULT SCHEDULE BY ACCOUNT ID
    public AccountThermostatSchedule getEnergyCompanyDefaultScheduleByAccountAndType(int accountId, SchedulableThermostatType type) {
    	
    	LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(accountId);
    	return getEnergyCompanyDefaultScheduleByType(energyCompany.getEnergyCompanyId(), type);
    }
    
    // MAP THERMOSTATS TO SCHEDULE
	public void mapThermostatsToSchedule(List<Integer> thermostatIds, int atsId) {
		
		// delete existing mappings
		SqlStatementBuilder deleteSql = new SqlStatementBuilder();
		deleteSql.append("DELETE FROM InventoryToAcctThermostatSch");
		deleteSql.append("WHERE InventoryId").in(thermostatIds);
		yukonJdbcTemplate.update(deleteSql);
			
		// insert new mappings
		for (int thermostatId : thermostatIds) {
			
			SqlStatementBuilder insertSql = new SqlStatementBuilder();
			insertSql.append("INSERT INTO InventoryToAcctThermostatSch (InventoryId, AcctThermostatScheduleId)");
			insertSql.append("VALUES (").appendArgumentList(Lists.newArrayList(thermostatId, atsId)).append(")");
			yukonJdbcTemplate.update(insertSql);
		}
	}
	
	// ALL SCHEDULES FOR ACCOUNT BY TYPE
	@Override
	public List<AccountThermostatSchedule> getAllSchedulesForAccountByType(int accountId, SchedulableThermostatType type) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ats.*");
    	sql.append("FROM AcctThermostatSchedule ats");
    	sql.append("WHERE ats.AccountId").eq(accountId);
    	if (type != null) {
    		sql.append("AND ats.ThermostatType").eq(type);
    	}
    	sql.append("ORDER BY ats.ScheduleName");
    	
    	return yukonJdbcTemplate.query(sql, accountThermostatScheduleRowAndFieldMapper);
	}
	
	// ALL SCHEDULES FOR ACCOUNT BY TYPE
    private List<AccountThermostatSchedule> getAllSchedulesAndEntriesForAccountByTypes(int accountId, List<SchedulableThermostatType> types) {
        
        //combine all of the allowable modes
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ATS.*");
        sql.append("FROM AcctThermostatSchedule ATS");
        sql.append("WHERE ATS.AccountId").eq(accountId);
        if (types != null) {
            sql.append("AND ATS.ThermostatType").in(types);
        }
        sql.append("ORDER BY ATS.ScheduleName");
        
        List<AccountThermostatSchedule> schedules = yukonJdbcTemplate.query(sql, accountThermostatScheduleRowAndFieldMapper);
        for(AccountThermostatSchedule schedule : schedules) {
            List<AccountThermostatScheduleEntry> atsEntries = accountThermostatScheduleEntryDao.getAllEntriesForSchduleId(schedule.getAccountThermostatScheduleId());
            schedule.setScheduleEntries(atsEntries);
        }
        
        return schedules;
    }

    @Override
    public AccountThermostatSchedule getSchedulesForAccountByScheduleName(int accountId, String scheduleName){
        List<AccountThermostatSchedule> thermostatSchedules = getSchedulesForAccountByScheduleName(accountId, scheduleName, null);
        
        if (thermostatSchedules.size() < 1) {
            throw new EmptyResultDataAccessException(1);
        }
        
        return thermostatSchedules.get(0);
    }

    @Override
    public AccountThermostatSchedule findSchedulesForAccountByScheduleName(int accountId, String scheduleName){
        try {
            return getSchedulesForAccountByScheduleName(accountId, scheduleName);
        } catch (EmptyResultDataAccessException e) {
            // This is fine.  just return null.
        }

        return null;
    }

    
    // FIND SCHEDULE FOR ACCOUNT BY NAME
    @Override
    public List<AccountThermostatSchedule> getSchedulesForAccountByScheduleName(int accountId, String scheduleName, Integer ignorableScheduleId){
        //  combine all of the allowable modes
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ATS.*");
        sql.append("FROM AcctThermostatSchedule ATS");
        sql.append("WHERE ATS.AccountId").eq(accountId);
        sql.append("AND ATS.ScheduleName").eq(scheduleName);
        if (ignorableScheduleId != null) {
            sql.append("AND AcctThermostatScheduleId").neq(ignorableScheduleId);
        }
        
        List<AccountThermostatSchedule> schedules = yukonJdbcTemplate.query(sql, accountThermostatScheduleRowAndFieldMapper);
        for(AccountThermostatSchedule schedule : schedules) {
            List<AccountThermostatScheduleEntry> atsEntries = accountThermostatScheduleEntryDao.getAllEntriesForSchduleId(schedule.getAccountThermostatScheduleId());
            schedule.setScheduleEntries(atsEntries);
        }
        
        return schedules;
    }
    
    
    // ALL SCHEDULES FOR ACCOUNT BY TYPE and remove schedules which are not allowed for the specified user
    @Override
    public List<AccountThermostatSchedule> getAllAllowedSchedulesAndEntriesForAccountByTypes(int accountId, List<SchedulableThermostatType> types) {
        
        List<AccountThermostatSchedule> schedules = getAllSchedulesAndEntriesForAccountByTypes(accountId, types);
        List<AccountThermostatSchedule> disallowedSchedules = Lists.newArrayList();
        Set<ThermostatScheduleMode> allowedModes = thermostatService.getAllowedThermostatScheduleModesByAccountId(accountId);
        
        for(AccountThermostatSchedule schedule : schedules){
            //mark the disallowed schedules for removal
            if(!schedule.getThermostatType().getAllowedModes(allowedModes).contains(schedule.getThermostatScheduleMode())){
                disallowedSchedules.add(schedule);
            }
        }
        schedules.removeAll(disallowedSchedules);
        
        return schedules;
    }
    
	// GET NUMBER OF THERMOSTATS USING SCHEDULE
	@Override
	public List<Integer> getThermostatIdsUsingSchedule(int atsId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT ITATS.InventoryId");
		sql.append("FROM InventoryToAcctThermostatSch ITATS");
		sql.append("WHERE ITATS.AcctThermostatScheduleId").eq(atsId);
		
		return yukonJdbcTemplate.query(sql, new IntegerRowMapper());
	}
    
    @PostConstruct
    public void init() {
    	
    	accountThermostatScheduleTemplate = new SimpleTableAccessTemplate<AccountThermostatSchedule>(yukonJdbcTemplate, nextValueHelper);
    	accountThermostatScheduleTemplate.setTableName("AcctThermostatSchedule");
    	accountThermostatScheduleTemplate.setPrimaryKeyField("AcctThermostatScheduleId");
    	accountThermostatScheduleTemplate.setFieldMapper(accountThermostatScheduleRowAndFieldMapper);
    	accountThermostatScheduleTemplate.setPrimaryKeyValidOver(-1);
    }
    
    private AccountThermostatSchedule createAndSaveDefaultSchedule(int ecId, SchedulableThermostatType type){
        // create and save schedule
        DefaultAccountThermostatScheduleHelper atsHelper = new DefaultAccountThermostatScheduleHelper();
        AccountThermostatSchedule ats = atsHelper.getDefaultAccountThermostatSchedule(type);
        save(ats);
        
        // insert AcctThermostatScheduleToEc record
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ECToAcctThermostatSchedule(EnergyCompanyId, AcctThermostatScheduleId)");
        sql.values(ecId, ats.getAccountThermostatScheduleId());
        yukonJdbcTemplate.update(sql);
        
        return ats;
    }
    
    // ROW AND FIELD MAPPER
    private YukonRowAndFieldMapper<AccountThermostatSchedule> accountThermostatScheduleRowAndFieldMapper = new YukonRowAndFieldMapper<AccountThermostatSchedule>() {
        
    	@Override
    	public AccountThermostatSchedule mapRow(YukonResultSet rs) throws SQLException {
    	    
    		AccountThermostatSchedule ats = new AccountThermostatSchedule();
    		ats.setAccountThermostatScheduleId(rs.getInt("AcctThermostatScheduleId"));
    		ats.setAccountId(rs.getInt("AccountId"));
    		ats.setScheduleName(rs.getStringSafe("ScheduleName"));
    		ats.setThermostatType(rs.getEnum("ThermostatType", SchedulableThermostatType.class));
    		ats.setThermostatScheduleMode(null);

    		ats.setThermostatScheduleMode(rs.getEnum("ScheduleMode", ThermostatScheduleMode.class));
    		
    		return ats;
    	}
    	
        @Override
        public void extractValues(MapSqlParameterSource p, AccountThermostatSchedule ats) {
        	
            p.addValue("AccountId",ats.getAccountId());
            p.addValue("ScheduleName", SqlUtils.convertStringToDbValue(ats.getScheduleName()));
            p.addValue("ThermostatType", ats.getThermostatType());
            p.addValue("ScheduleMode", ats.getThermostatScheduleMode());
        }

        @Override
        public Number getPrimaryKey(AccountThermostatSchedule object) {
            return object.getAccountThermostatScheduleId();
        }

        @Override
        public void setPrimaryKey(AccountThermostatSchedule object, int value) {
            object.setAccountThermostatScheduleId(value);
        }
    };
    
    // LEGACY ThermostatScheduleMode HELPERS
    // The migration of data to new tables for thermostat schedules could not determine ThermostatScheduleMode.
    // When a AccountThermostatSchedule is retrieved for the first time, this legacy guessing code will be run to determine the ThermostatScheduleMode.
    private static void setAssumedMode(AccountThermostatSchedule ats) {
    	
    	ThermostatScheduleMode mode = ThermostatScheduleMode.WEEKDAY_SAT_SUN;
    	
    	// sort
    	ListMultimap<TimeOfWeek, AccountThermostatScheduleEntry> map = ArrayListMultimap.create();
    	for (AccountThermostatScheduleEntry atsEntry : ats.getScheduleEntries()) {
    		map.put(atsEntry.getTimeOfWeek(), atsEntry);
    	}
    	
    	// separate
    	List<AccountThermostatScheduleEntry> weekdayEntries = map.get(TimeOfWeek.WEEKDAY);
    	if (weekdayEntries.size() == 0) {
    		weekdayEntries = map.get(TimeOfWeek.MONDAY);
    	}
    	List<AccountThermostatScheduleEntry> satEntries = map.get(TimeOfWeek.SATURDAY);
    	List<AccountThermostatScheduleEntry> sunEntries = map.get(TimeOfWeek.SUNDAY);
    	
    	if (hasIdenticalStartTimeAndTemps(satEntries, sunEntries)) {
			if (hasIdenticalStartTimeAndTemps(satEntries, weekdayEntries)) {
				mode = ThermostatScheduleMode.ALL;
			} else {
				mode = ThermostatScheduleMode.WEEKDAY_WEEKEND;
			}
		}
		
		ats.setThermostatScheduleMode(mode);
    }
    
    private static boolean hasIdenticalStartTimeAndTemps(List<AccountThermostatScheduleEntry> list1, List<AccountThermostatScheduleEntry> list2) {
    	
    	if (list1.size() != list2.size()) {
    		return false;
    	}
    	
    	Collections.sort(list1, atsEntryStartTimeComparator);
    	Collections.sort(list2, atsEntryStartTimeComparator);
    	Iterator<AccountThermostatScheduleEntry> iter1 = list1.iterator();
    	Iterator<AccountThermostatScheduleEntry> iter2 = list2.iterator();
    	
    	boolean isListsEqual = true;
    	while (iter1.hasNext()) {
    		if (!iter1.next().isEqualStartTimeAndTemps(iter2.next())) {
    			isListsEqual = false;
    			break;
    		}
    	}
    	
    	return isListsEqual;
    }
    
    private static Comparator<AccountThermostatScheduleEntry> atsEntryStartTimeComparator = new Comparator<AccountThermostatScheduleEntry>() {
    	@Override
		public int compare(AccountThermostatScheduleEntry o1, AccountThermostatScheduleEntry o2) {
			return (new Integer(o1.getStartTime())).compareTo(new Integer(o2.getStartTime()));
		}
	};
    
    
	@Autowired
	public void setAccountEventLogService(AccountEventLogService accountEventLogService) {
        this.accountEventLogService = accountEventLogService;
    }
	
	@Autowired
	public void setCustomerAccountDao(CustomerAccountDao customerAccountDao) {
        this.customerAccountDao = customerAccountDao;
    }
	
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
    
    @Autowired
    public void setAccountThermostatScheduleEntryDao(AccountThermostatScheduleEntryDao accountThermostatScheduleEntryDao) {
		this.accountThermostatScheduleEntryDao = accountThermostatScheduleEntryDao;
	}
    
    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
		this.ecMappingDao = ecMappingDao;
	}
    
    @Autowired
    public void setThermostatService(ThermostatService thermostatService){
        this.thermostatService = thermostatService;
    }
}

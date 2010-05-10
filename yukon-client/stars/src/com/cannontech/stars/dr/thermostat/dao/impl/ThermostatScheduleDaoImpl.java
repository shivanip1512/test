package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlGenerator;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.event.dao.LMCustomerEventBaseDao;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.ScheduleDropDownItem;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.util.YukonListEntryHelper;

/**
 * Implementation class for ThermostatScheduleDao
 */
public class ThermostatScheduleDaoImpl implements ThermostatScheduleDao, InitializingBean {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    private ECMappingDao ecMappingDao;
    private LMCustomerEventBaseDao lmCustomerEventBaseDao;
    private NextValueHelper nextValueHelper;

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        chunkyJdbcTemplate= new ChunkingSqlTemplate(simpleJdbcTemplate);
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }
    
    @Autowired
    public void setLMCustomerEventBaseDao(LMCustomerEventBaseDao lmCustomerEventBaseDao) {
        this.lmCustomerEventBaseDao = lmCustomerEventBaseDao;
    }


    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Override
    @Transactional
    public ThermostatSchedule getEnergyCompanyDefaultSchedule(int accountId,
            HardwareType type) {

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(accountId);
        return this.getCopyOfEnergyCompanyDefaultSchedule(energyCompany, type);
        
    }
    
    @Override
    @Transactional
    public ThermostatSchedule getEnergyCompanyDefaultSchedule(
    		LiteStarsEnergyCompany energyCompany, HardwareType type) {

    	Integer energyCompanyID = energyCompany.getEnergyCompanyID();

    	int typeEntryId = YukonListEntryHelper.getListEntryId(
    			energyCompany, 
    			YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, 
    			type.getDefinitionId());
    	
    	// Get the id of the default thermostat schedule for the hardware type (if one exists) 
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT lmts.ScheduleId");
    	sql.append("FROM LMThermostatSchedule lmts");
    	sql.append("	JOIN ECToGenericMapping ectgm ON ectgm.ItemId = lmts.ScheduleId");
		sql.append("WHERE ectgm.EnergyCompanyId = ").appendArgument(energyCompanyID);
    	sql.append("	AND ectgm.MappingCategory = ").appendArgument("LMThermostatSchedule");
    	sql.append("	AND lmts.ThermostatTypeId = ").appendArgument(typeEntryId);
    	
    	try {
	    	Integer scheduleId = simpleJdbcTemplate.queryForInt(sql.getSql(), sql.getArguments());
	    	
	    	// Load and return the schedule if one exists
	    	return this.getThermostatScheduleById(scheduleId, energyCompany);
	    	
    	} catch(EmptyResultDataAccessException e) {
    		// No default schedule for the given type exists - create one and return it
    	}

    	// Create and save a new default schedule for the given hardware type
    	ThermostatSchedule defaultSchedule = this.createDefaultSchedule(type);
    	this.saveDefaultSchedule(defaultSchedule, energyCompany);
    	
    	return defaultSchedule;
    	
    }
    
    @Override
    @Transactional
    public ThermostatSchedule getCopyOfEnergyCompanyDefaultSchedule(
    		LiteStarsEnergyCompany energyCompany, HardwareType type) {
    	
    	ThermostatSchedule defaultSchedule = energyCompany.getDefaultThermostatSchedule(type);
    	
    	// Null out all of the ids so this is a 'copy' of the default
    	defaultSchedule.setId(null);
    	ThermostatSeason season = defaultSchedule.getSeason();
    	season.setId(null);
    	for(ThermostatSeasonEntry entry : season.getAllSeasonEntries()) {
    		entry.setId(null);
    	}
    	
    	return defaultSchedule;
    	
    }

    @Override
    public ThermostatSchedule getThermostatScheduleById(int scheduleId, int accountId) {
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(accountId);
        return this.getThermostatScheduleById(scheduleId, energyCompany);
    }
    
    @Override
    public ThermostatSchedule getThermostatScheduleByInventoryId(int inventoryId) {

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * from LMThermostatSchedule");
        sql.append("WHERE InventoryId = ?");

        ThermostatSchedule schedule = null;

        try {
            schedule = simpleJdbcTemplate.queryForObject(sql.toString(),
                                                         new ThermostatScheduleMapper(energyCompany),
                                                         inventoryId);
        } catch (EmptyResultDataAccessException e) {
            // ignore - This thermostat has no assigned schedule
        }

        if (schedule != null) {
            ThermostatSeason season = this.getSeason(schedule.getId(), energyCompany);
            schedule.setSeason(season);
        }

        return schedule;
    }

    @Override
    public List<ScheduleDropDownItem> getSavedThermostatSchedulesByAccountId(
            int accountId, HardwareType type) {

    	LiteStarsEnergyCompany customerAccountEC = ecMappingDao.getCustomerAccountEC(accountId);
		int deviceTypeId = YukonListEntryHelper.getListEntryId(
				customerAccountEC, 
				YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE, 
				type.getDefinitionId());
    	
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ScheduleId, ScheduleName");
        sql.append("FROM LMThermostatSchedule");
        sql.append("WHERE AccountId = ?");
        sql.append("AND ThermostatTypeId = ?");
        sql.append("ORDER BY ScheduleName");

        List<ScheduleDropDownItem> items = simpleJdbcTemplate.query(sql.toString(),
                                                                    new ScheduleDropDownItemMapper(),
                                                                    accountId,
                                                                    deviceTypeId);

        return items;
    }
    
    @Override
    public List<ScheduleDropDownItem> getSavedThermostatSchedulesByAccountId(
    		int accountId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT ScheduleId, ScheduleName");
    	sql.append("FROM LMThermostatSchedule");
    	sql.append("WHERE AccountId = ?");
    	sql.append("ORDER BY ScheduleName");
    	
    	List<ScheduleDropDownItem> items = simpleJdbcTemplate.query(sql.toString(),
    			new ScheduleDropDownItemMapper(),
    			accountId);
    	
    	return items;
    }
    
    @Override
    @Transactional
    public void saveDefaultSchedule(ThermostatSchedule schedule,
    		LiteStarsEnergyCompany energyCompany) {

    	// Set AccountId and InventoryId to default values
    	schedule.setAccountId(0);
    	schedule.setInventoryId(0);
    	this.save(schedule, energyCompany);
    	
    	// Insert a row into ECToGenericMapping to tie this new default schedule
    	// to the energy company
    	SqlStatementBuilder ecToGenericSql = new SqlStatementBuilder();
    	ecToGenericSql.append("INSERT INTO ECToGenericMapping");
    	ecToGenericSql.append("(EnergyCompanyId, ItemId, MappingCategory)");
    	ecToGenericSql.append("VALUES (?, ?, ?)");
    	
    	simpleJdbcTemplate.update(ecToGenericSql.getSql(), 
    			energyCompany.getEnergyCompanyID(),
    			schedule.getId(),
    			"LMThermostatSchedule");
    	
    	// Update default schedule cache
    	energyCompany.updateDefaultSchedule(schedule);
    	
    }

    @Override
    @Transactional
    public void save(ThermostatSchedule schedule, LiteStarsEnergyCompany energyCompany) {

        Integer scheduleId = schedule.getId();
        SqlStatementBuilder scheduleSql = new SqlStatementBuilder();

        // Updates are done by delete and then insert
        if(scheduleId != null) {
        	this.delete(scheduleId);
        }

        // Get new id for schedule
        scheduleId = nextValueHelper.getNextValue("LMThermostatSchedule");
        schedule.setId(scheduleId);

        scheduleSql.append("INSERT INTO LMThermostatSchedule");
        scheduleSql.append("(ScheduleName, ThermostatTypeId, AccountId, InventoryId, ScheduleId)");
        scheduleSql.append("VALUES (?,?,?,?,?)");

        Integer inventoryId = schedule.getInventoryId();

        Integer accountId = schedule.getAccountId();

        HardwareType thermostatType = schedule.getThermostatType();
        int thermostatTypeId = YukonListEntryHelper.getListEntryId(energyCompany,
                                                                   YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
                                                                   thermostatType.getDefinitionId());

        simpleJdbcTemplate.update(scheduleSql.toString(),
                                  schedule.getName(),
                                  thermostatTypeId,
                                  accountId,
                                  inventoryId,
                                  scheduleId);

        ThermostatSeason season = schedule.getSeason();
        season.setScheduleId(scheduleId);

        // Cool season is hard-coded to June 15th
        Calendar cal = new GregorianCalendar(1970, 6, 15);
        Date coolDate = cal.getTime();
        season.setCoolStartDate(coolDate);

        // Heat season is hard-coded to October 15th
        cal = new GregorianCalendar(1970, 10, 15);
        Date heatDate = cal.getTime();
        season.setHeatStartDate(heatDate);
        
        this.saveSeason(season, energyCompany);

    }

	@Override
	@Transactional
	public void delete(int scheduleId) {

		// Delete Schedule
		SqlStatementBuilder deleteScheduleSql = new SqlStatementBuilder();
		deleteScheduleSql.append("DELETE FROM ");
		deleteScheduleSql.append("	LMThermostatSchedule");
		deleteScheduleSql.append("WHERE ScheduleId = ?");
		
		simpleJdbcTemplate.update(deleteScheduleSql.toString(), scheduleId);
		
		// Delete Schedule mapping for energy company default schedules
		SqlStatementBuilder deleteMappingSql = new SqlStatementBuilder();
		deleteMappingSql.append("DELETE FROM ");
		deleteMappingSql.append("	ECToGenericMapping");
		deleteMappingSql.append("WHERE ItemId = ").appendArgument(scheduleId);
		deleteMappingSql.append("	AND MappingCategory = ").appendArgument("LMThermostatSchedule");
		
		simpleJdbcTemplate.update(deleteMappingSql.getSql(), deleteMappingSql.getArguments());
		
	}
	
	@Override
	public List<Integer> getInventoryIdsForSchedules(Integer... scheduleIds) {

		String ids = StringUtils.join(scheduleIds, ",");

		SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryId");
        sql.append("FROM LMThermostatSchedule");
        sql.append("WHERE ScheduleId IN (" + ids + ")");

        final List<Integer> inventoryIds = new ArrayList<Integer>();
        simpleJdbcTemplate.getJdbcOperations().query(sql.toString(), new RowCallbackHandler(){
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				int inventoryId = rs.getInt("InventoryId");
				inventoryIds.add(inventoryId);
			}});
		
		return inventoryIds;
	}
	
	/**
	 * Helper method to get a schedule by id and energy company
	 * @param scheduleId - Id of schedule to load
	 * @param energyCompany - Schedule's energy company
	 * @return Schedule
	 */
	private ThermostatSchedule getThermostatScheduleById(
    		int scheduleId, LiteStarsEnergyCompany energyCompany) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT * from LMThermostatSchedule");
    	sql.append("WHERE ScheduleId = ?");
    	
    	ThermostatSchedule schedule = simpleJdbcTemplate.queryForObject(sql.toString(),
    			new ThermostatScheduleMapper(energyCompany),
    			scheduleId);
    	
    	ThermostatSeason season = this.getSeason(schedule.getId(), energyCompany);
    	schedule.setSeason(season);
    	
    	return schedule;
    	
    }

    /**
     * Helper method to get a list of thermostat seasons for a given schedule
     * @param scheduleId - Id of schedule in question
     * @return List of seasons
     */
    private ThermostatSeason getSeason(int scheduleId, LiteStarsEnergyCompany energyCompany) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * from LMThermostatSeason");
        sql.append("WHERE ScheduleId = ?");

        List<ThermostatSeason> seasons = simpleJdbcTemplate.query(sql.toString(),
                                                                  new ThermostatSeasonMapper(),
                                                                  scheduleId);

        // Only use the first season - there should only be one season except for the leagacy
        // schedules in the db
        ThermostatSeason returnSeason = null;
        if(seasons.size() > 0) {
        	returnSeason = seasons.get(0);
        	List<ThermostatSeasonEntry> seasonEntries = 
        		this.getSeasonEntries(returnSeason.getId(), energyCompany);
        	for (ThermostatSeasonEntry entry : seasonEntries) {
        		returnSeason.addSeasonEntry(entry);
        	}
        }
        
        return returnSeason;
    }

    /**
     * Helper method to get a list of thermostat season entries for a given
     * season
     * @param seasonId - Id of season in question
     * @return List of season entries
     */
    private List<ThermostatSeasonEntry> getSeasonEntries(int seasonId,
    		LiteStarsEnergyCompany energyCompany) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * from LMThermostatSeasonEntry");
        sql.append("WHERE SeasonId = ?");
        sql.append("ORDER BY Starttime");

        List<ThermostatSeasonEntry> seasonEntries = simpleJdbcTemplate.query(sql.toString(),
                                                                             new ThermostatSeasonEntryMapper(energyCompany),
                                                                             seasonId);

        return seasonEntries;
    }

    /**
     * Helper method to save a thermostat season - always does insert
     * @param season - Season to save
     * @param energyCompany - Current energy company
     */
    private void saveSeason(ThermostatSeason season, LiteStarsEnergyCompany energyCompany) {

        SqlStatementBuilder seasonSql = new SqlStatementBuilder();

    	Integer seasonId = nextValueHelper.getNextValue("LMThermostatSeason");
        season.setId(seasonId);

        seasonSql.append("INSERT INTO LMThermostatSeason");
        seasonSql.append("(ScheduleId, WebConfigurationId");
        seasonSql.append(", CoolStartDate, HeatStartDate, SeasonId)");
        seasonSql.append("VALUES (?,?,?,?,?)");

        simpleJdbcTemplate.update(seasonSql.toString(),
                                  season.getScheduleId(),
                                  season.getWebConfigurationId(),
                                  season.getCoolStartDate(),
                                  season.getHeatStartDate(),
                                  seasonId);

        // Save each of the season entries for the season

        List<ThermostatSeasonEntry> seasonEntries = season.getAllSeasonEntries();
        for (ThermostatSeasonEntry entry : seasonEntries) {

            SqlStatementBuilder entrySql = new SqlStatementBuilder();
            entrySql.append("INSERT INTO LMThermostatSeasonEntry");
            entrySql.append("(SeasonId, TimeOfWeekId, StartTime");
            entrySql.append(", CoolTemperature, HeatTemperature, EntryId)");
            entrySql.append("VALUES (?,?,?,?,?,?)");

            Integer entryId = nextValueHelper.getNextValue("LMThermostatSeasonEntry");
            entry.setId(entryId);
            entry.setSeasonId(seasonId);

            TimeOfWeek timeOfWeek = entry.getTimeOfWeek();
            int timeOfWeekId = YukonListEntryHelper.getListEntryId(energyCompany,
                                                                   YukonSelectionListDefs.YUK_LIST_NAME_TIME_OF_WEEK,
                                                                   timeOfWeek.getDefinitionId());

            // Start time is seconds from midnight in db
            LocalTime localTime = entry.getStartTime();
            Integer startTimeSeconds = localTime.getMillisOfDay() / 1000;

            simpleJdbcTemplate.update(entrySql.toString(),
                                      seasonId,
                                      timeOfWeekId,
                                      startTimeSeconds,
                                      entry.getCoolTemperature(),
                                      entry.getHeatTemperature(),
                                      entryId);

        }

    }
    
    /**
     * Helper method to get a default thermostat schedule with 4 season entries each
     * for Weekday, Saturday and Sunday time of weeks.
     * @param type - Type of hardware to get schedule for
     * @return Default Schedule
     */
    private ThermostatSchedule createDefaultSchedule(HardwareType type) {
    	
    	ThermostatSchedule schedule = new ThermostatSchedule();
    	schedule.setThermostatType(type);
    	
    	// Add season
    	ThermostatSeason season = new ThermostatSeason();
    	season.setThermostatMode(ThermostatMode.COOL);
    	
    	List<TimeOfWeek> timeOfWeekList = new ArrayList<TimeOfWeek>();
    	timeOfWeekList.add(TimeOfWeek.WEEKDAY);
    	timeOfWeekList.add(TimeOfWeek.SATURDAY);
    	timeOfWeekList.add(TimeOfWeek.SUNDAY);
    	
    	List<LocalTime> timeOfDayList = new ArrayList<LocalTime>();
    	// 6:00 am
    	timeOfDayList.add(new LocalTime(6, 0));
    	// 8:30 am
    	timeOfDayList.add(new LocalTime(8, 30));
    	// 5:00 pm
    	timeOfDayList.add(new LocalTime(5, 0));
    	// 9:00 pm
    	timeOfDayList.add(new LocalTime(9, 0));
    	
    	// Add season entries for each time of week
    	for(TimeOfWeek timeOfWeek : timeOfWeekList) {
    		// Add season entries for each time of day
    		for(LocalTime time : timeOfDayList) {

    			ThermostatSeasonEntry entry = new ThermostatSeasonEntry();
    			entry.setTimeOfWeek(timeOfWeek);
    			entry.setStartTime(time);
    			entry.setCoolTemperature(72);
    			entry.setHeatTemperature(72);
    			season.addSeasonEntry(entry);
    		}
    	}
    	
    	schedule.setSeason(season);

        return schedule;
    	
    }

    /**
     * Helper class to map a result set into a ThermostatSchedule
     */
    private class ThermostatScheduleMapper implements
            ParameterizedRowMapper<ThermostatSchedule> {

        private final LiteStarsEnergyCompany energyCompany;

        public ThermostatScheduleMapper(LiteStarsEnergyCompany energyCompany) {
            this.energyCompany = energyCompany;
        }

        @Override
        public ThermostatSchedule mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int id = rs.getInt("ScheduleId");
            String name = rs.getString("ScheduleName");
            int accountId = rs.getInt("AccountId");
            int inventoryId = rs.getInt("InventoryId");
            int thermostatTypeId = rs.getInt("ThermostatTypeId");

            ThermostatSchedule schedule = new ThermostatSchedule();
            schedule.setId(id);
            schedule.setName(name);
            schedule.setAccountId(accountId);
            schedule.setInventoryId(inventoryId);

            int typeDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                             YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
                                                                             thermostatTypeId);
            HardwareType thermostatType = HardwareType.valueOf(typeDefinitionId);
            schedule.setThermostatType(thermostatType);

            return schedule;
        }

    }

    /**
     * Helper class to map a result set into a ThermostatSeason
     */
    private class ThermostatSeasonMapper implements
            ParameterizedRowMapper<ThermostatSeason> {

        @Override
        public ThermostatSeason mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int id = rs.getInt("SeasonId");
            int scheduleId = rs.getInt("ScheduleId");
            int webConfigurationId = rs.getInt("WebConfigurationId");
            Date coolStartDate = rs.getTimestamp("CoolStartDate");
            Date heatStartDate = rs.getTimestamp("HeatStartDate");

            ThermostatSeason season = new ThermostatSeason();
            season.setId(id);
            season.setScheduleId(scheduleId);
            season.setWebConfigurationId(webConfigurationId);
            season.setCoolStartDate(coolStartDate);
            season.setHeatStartDate(heatStartDate);

            return season;
        }

    }

    /**
     * Helper class to map a result set into a ThermostatSeasonEntry
     */
    private class ThermostatSeasonEntryMapper implements
            ParameterizedRowMapper<ThermostatSeasonEntry> {

        private final LiteStarsEnergyCompany energyCompany;

        public ThermostatSeasonEntryMapper(LiteStarsEnergyCompany energyCompany) {
            this.energyCompany = energyCompany;
        }

        @Override
        public ThermostatSeasonEntry mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int id = rs.getInt("EntryId");
            int seasonId = rs.getInt("SeasonId");
            int timeOfWeekId = rs.getInt("TimeOfWeekId");
            int startTime = rs.getInt("StartTime");
            int coolTemperature = rs.getInt("CoolTemperature");
            int heatTemperature = rs.getInt("HeatTemperature");

            ThermostatSeasonEntry entry = new ThermostatSeasonEntry();
            entry.setId(id);
            entry.setSeasonId(seasonId);

            int timeOfWeekDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                                   YukonSelectionListDefs.YUK_LIST_NAME_TIME_OF_WEEK,
                                                                                   timeOfWeekId);
            TimeOfWeek timeOfWeek = TimeOfWeek.valueOf(timeOfWeekDefinitionId);
            entry.setTimeOfWeek(timeOfWeek);

			// Start time is seconds from midnight in db
            int startMinutes = startTime / 60;
            int startTimeHours = startMinutes / 60;
            int startTimeMinutes = startMinutes % 60;
            if(startTimeMinutes < 0) {
            	startTimeMinutes = 0;
            }
            entry.setStartTime(new LocalTime(startTimeHours, startTimeMinutes));
          	entry.setCoolTemperature(coolTemperature);
          	entry.setHeatTemperature(heatTemperature);

            return entry;
        }

    }

    /**
     * Helper class to map a result set into a ScheduleDropDownItem
     */
    private class ScheduleDropDownItemMapper implements
            ParameterizedRowMapper<ScheduleDropDownItem> {

        @Override
        public ScheduleDropDownItem mapRow(ResultSet rs, int rowNum)
                throws SQLException {

            int id = rs.getInt("ScheduleId");
            String name = rs.getString("ScheduleName");

            ScheduleDropDownItem item = new ScheduleDropDownItem(id, name);

            return item;
        }

    }
    
    @Override
    @Transactional
    public void deleteScheduleForInventory(Integer inventoryId) {
        String sql = "SELECT ScheduleId FROM LMThermostatSchedule WHERE InventoryId = ?";
        List<Integer> scheduleIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), inventoryId);
        for(Integer scheduleId : scheduleIds) {
            delete(scheduleId);
        }
    }
    
    @Override
    public List<Integer> getScheduleIdsForAccount(Integer accountId){
        String sql = "SELECT ScheduleId FROM LMThermostatSchedule WHERE AccountId = ?";
        List<Integer> scheduleIds = new ArrayList<Integer>();
        scheduleIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), accountId);
        
        return scheduleIds;
    }
    
    @Override
    @Transactional
    public void deleteSchedulesForAccount(Integer accountId) {
        List<Integer> scheduleIds = getScheduleIdsForAccount(accountId);
        for(Integer scheduleId : scheduleIds) {
            delete(scheduleId);
        }
    }
    
    @Override
    public List<Integer> getAllManualEventIds(Integer inventoryId) {
        List<Integer> eventIds = new ArrayList<Integer>();
        
        String sql = "SELECT EventId FROM LMThermostatManualEvent WHERE InventoryId = ? ORDER BY EventID";
        eventIds = simpleJdbcTemplate.query(sql, new IntegerRowMapper(), inventoryId);
        
        return eventIds;
    }
    
    @Override
    @Transactional
    public void deleteManualEvents(Integer inventoryId) {
        List<Integer> eventIds = getAllManualEventIds(inventoryId);
        if(!eventIds.isEmpty()) {
            chunkyJdbcTemplate.update(new LMThermostatManualEventDeleteSqlGenerator(), eventIds);
            ecMappingDao.deleteECToCustomerEventMapping(eventIds);
            lmCustomerEventBaseDao.deleteCustomerEvents(eventIds);
        }
    }
    
    /**
     * Sql generator for deleting in LMThermostatManualEvent, useful for bulk deleteing
     * with chunking sql template.
     */
    private class LMThermostatManualEventDeleteSqlGenerator implements SqlGenerator<Integer> {

        @Override
        public String generate(List<Integer> eventIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM LMThermostatManualEvent WHERE EventId IN (", eventIds, ")");
            return sql.toString();
        }
    }

}

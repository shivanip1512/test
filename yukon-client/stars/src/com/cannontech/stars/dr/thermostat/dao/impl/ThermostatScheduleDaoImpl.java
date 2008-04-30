package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSchedule;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeason;
import com.cannontech.database.data.lite.stars.LiteLMThermostatSeasonEntry;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.ECMappingDao;
import com.cannontech.stars.dr.hardware.model.HardwareType;
import com.cannontech.stars.dr.thermostat.dao.ThermostatScheduleDao;
import com.cannontech.stars.dr.thermostat.model.ThermostatMode;
import com.cannontech.stars.dr.thermostat.model.ThermostatSchedule;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeason;
import com.cannontech.stars.dr.thermostat.model.ThermostatSeasonEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;
import com.cannontech.stars.dr.util.YukonListEntryHelper;
import com.cannontech.stars.util.StarsMsgUtils;

/**
 * Implementation class for ThermostatScheduleDao
 */
public class ThermostatScheduleDaoImpl implements ThermostatScheduleDao {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ECMappingDao ecMappingDao;
    private NextValueHelper nextValueHelper;

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

    @Autowired
    public void setEcMappingDao(ECMappingDao ecMappingDao) {
        this.ecMappingDao = ecMappingDao;
    }

    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }

    @Override
    public ThermostatSchedule getEnergyCompanyDefaultSchedule(int accountId,
            HardwareType type) {

        ThermostatSchedule schedule = new ThermostatSchedule();
        schedule.setThermostatType(type);

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(accountId);

        // Convert default LiteLMThermostatSchedule into new ThermostatSchedule
        // model object
        int typeDefinitionId = type.getDefinitionId();
        LiteLMThermostatSchedule defaultSchedule = energyCompany.getDefaultThermostatSchedule(typeDefinitionId);

        List<LiteLMThermostatSeason> defaultSeasonList = defaultSchedule.getThermostatSeasons();
        for (LiteLMThermostatSeason defaultSeason : defaultSeasonList) {

            ThermostatSeason season = new ThermostatSeason();

            long startDate = defaultSeason.getStartDate();
            season.setStartDate(new Date(startDate));

            int webConfigurationID = defaultSeason.getWebConfigurationID();
            season.setWebConfigurationId(webConfigurationID);

            List<LiteLMThermostatSeasonEntry> defaultEntryList = defaultSeason.getSeasonEntries();
            for (LiteLMThermostatSeasonEntry defaultEntry : defaultEntryList) {

                ThermostatSeasonEntry entry = new ThermostatSeasonEntry();

                Integer startTime = defaultEntry.getStartTime();
                entry.setStartTime(startTime);

                int temperature = defaultEntry.getTemperature();
                entry.setTemperature(temperature);

                int timeOfWeekId = defaultEntry.getTimeOfWeekID();
                int timeOfWeekDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                                       YukonSelectionListDefs.YUK_LIST_NAME_TIME_OF_WEEK,
                                                                                       timeOfWeekId);
                TimeOfWeek timeOfWeek = TimeOfWeek.valueOf(timeOfWeekDefinitionId);
                entry.setTimeOfWeek(timeOfWeek);

                season.addSeasonEntry(entry);
            }

            schedule.addSeason(season);
        }

        return schedule;
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
            List<ThermostatSeason> seasons = this.getSeasons(schedule.getId(),
                                                             inventoryId);

            for (ThermostatSeason season : seasons) {
                schedule.addSeason(season);
            }
        }

        return schedule;
    }

    @Override
    @Transactional
    public void save(ThermostatSchedule schedule) {

        Integer scheduleId = schedule.getId();
        SqlStatementBuilder scheduleSql = new SqlStatementBuilder();

        // Update, or Insert if null id
        if (scheduleId == null) {
            scheduleId = nextValueHelper.getNextValue("LMThermostatSchedule");
            schedule.setId(scheduleId);

            scheduleSql.append("INSERT INTO LMThermostatSchedule");
            scheduleSql.append("(ScheduleName, ThermostatTypeId, AccountId, InventoryId, ScheduleId)");
            scheduleSql.append("VALUES (?,?,?,?,?)");
        } else {
            scheduleSql.append("UPDATE LMThermostatSchedule");
            scheduleSql.append("SET ScheduleName = ?, ThermostatTypeId = ?, AccountId = ?, InventoryId = ?");
            scheduleSql.append("WHERE ScheduleId = ?");
        }

        Integer inventoryId = schedule.getInventoryId();

        Integer accountId = schedule.getAccountId();
        LiteStarsEnergyCompany energyCompany = ecMappingDao.getCustomerAccountEC(accountId);

        HardwareType thermostatType = schedule.getThermostatType();
        int thermostatTypeId = YukonListEntryHelper.getListEntryId(energyCompany,
                                                                   YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE,
                                                                   thermostatType.getDefinitionId());

        simpleJdbcTemplate.update(scheduleSql.toString(),
                                  "Schedule " + scheduleId,
                                  thermostatTypeId,
                                  accountId,
                                  inventoryId,
                                  scheduleId);

        Map<ThermostatMode, ThermostatSeason> seasonMap = schedule.getSeasonMap();

        // Save cool season
        ThermostatSeason coolSeason = seasonMap.get(ThermostatMode.COOL);
        coolSeason.setScheduleId(scheduleId);

        // Cool season is hard-coded to June 15th
        Calendar cal = new GregorianCalendar(1970, 6, 15);
        Date coolDate = cal.getTime();
        coolSeason.setStartDate(coolDate);

        int coolWebConfigurationId = StarsMsgUtils.YUK_WEB_CONFIG_ID_COOL;
        coolSeason.setWebConfigurationId(coolWebConfigurationId);

        this.saveSeason(coolSeason, 1, energyCompany);

        // Save heat season
        ThermostatSeason heatSeason = seasonMap.get(ThermostatMode.HEAT);
        heatSeason.setScheduleId(scheduleId);

        // Cool season is hard-coded to October 15th
        cal = new GregorianCalendar(1970, 10, 15);
        Date heatDate = cal.getTime();
        heatSeason.setStartDate(heatDate);

        int heatWebConfigurationId = StarsMsgUtils.YUK_WEB_CONFIG_ID_HEAT;
        heatSeason.setWebConfigurationId(heatWebConfigurationId);

        this.saveSeason(heatSeason, 2, energyCompany);

    }

    /**
     * Helper method to get a list of thermostat seasons for a given schedule
     * @param scheduleId - Id of schedule in question
     * @return List of seasons
     */
    private List<ThermostatSeason> getSeasons(int scheduleId, int inventoryId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * from LMThermostatSeason");
        sql.append("WHERE ScheduleId = ?");
        sql.append("ORDER BY DisplayOrder");

        List<ThermostatSeason> seasons = simpleJdbcTemplate.query(sql.toString(),
                                                                  new ThermostatSeasonMapper(),
                                                                  scheduleId);

        for (ThermostatSeason season : seasons) {
            List<ThermostatSeasonEntry> seasonEntries = this.getSeasonEntries(season.getId(),
                                                                              inventoryId);
            for (ThermostatSeasonEntry entry : seasonEntries) {
                season.addSeasonEntry(entry);
            }
        }

        return seasons;
    }

    /**
     * Helper method to get a list of thermostat season entries for a given
     * season
     * @param seasonId - Id of season in question
     * @return List of season entries
     */
    private List<ThermostatSeasonEntry> getSeasonEntries(int seasonId,
            int inventoryId) {

        LiteStarsEnergyCompany energyCompany = ecMappingDao.getInventoryEC(inventoryId);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT * from LMThermostatSeasonEntry");
        sql.append("WHERE SeasonId = ?");

        List<ThermostatSeasonEntry> seasonEntries = simpleJdbcTemplate.query(sql.toString(),
                                                                             new ThermostatSeasonEntryMapper(energyCompany),
                                                                             seasonId);

        return seasonEntries;
    }

    /**
     * Helper method to save a thermostat season
     * @param season - Season to save
     * @param displayOrder - Display order for the season
     * @param energyCompany - Current energy company
     */
    private void saveSeason(ThermostatSeason season, int displayOrder,
            LiteStarsEnergyCompany energyCompany) {

        Integer seasonId = season.getId();
        SqlStatementBuilder seasonSql = new SqlStatementBuilder();

        // Update, or Insert if null id
        if (seasonId == null) {
            seasonId = nextValueHelper.getNextValue("LMThermostatSeason");
            season.setId(seasonId);

            seasonSql.append("INSERT INTO LMThermostatSeason");
            seasonSql.append("(ScheduleId, WebConfigurationId, StartDate, DisplayOrder, SeasonId)");
            seasonSql.append("VALUES (?,?,?,?,?)");
        } else {
            seasonSql.append("UPDATE LMThermostatSeason");
            seasonSql.append("SET ScheduleId = ?, WebConfigurationId = ?, StartDate = ?, DisplayOrder = ?");
            seasonSql.append("WHERE SeasonId = ?");
        }

        simpleJdbcTemplate.update(seasonSql.toString(),
                                  season.getScheduleId(),
                                  season.getWebConfigurationId(),
                                  season.getStartDate(),
                                  displayOrder,
                                  seasonId);

        // Save each of the season entries for the season

        List<ThermostatSeasonEntry> seasonEntries = season.getAllSeasonEntries();
        for (ThermostatSeasonEntry entry : seasonEntries) {

            Integer entryId = entry.getId();
            SqlStatementBuilder entrySql = new SqlStatementBuilder();

            // Update, or Insert if null id
            if (entryId == null) {
                entryId = nextValueHelper.getNextValue("LMThermostatSeasonEntry");
                entry.setId(entryId);

                entrySql.append("INSERT INTO LMThermostatSeasonEntry");
                entrySql.append("(SeasonId, TimeOfWeekId, StartTime, Temperature, EntryId)");
                entrySql.append("VALUES (?,?,?,?,?)");

            } else {
                entrySql.append("UPDATE LMThermostatSeasonEntry");
                entrySql.append("SET SeasonId = ?, TimeOfWeekId = ?, StartTime = ?, Temperature = ?");
                entrySql.append("WHERE EntryId = ?");
            }

            entry.setSeasonId(seasonId);

            TimeOfWeek timeOfWeek = entry.getTimeOfWeek();
            int timeOfWeekId = YukonListEntryHelper.getListEntryId(energyCompany,
                                                                   YukonSelectionListDefs.YUK_LIST_NAME_TIME_OF_WEEK,
                                                                   timeOfWeek.getDefinitionId());

            Integer temperature = entry.getTemperature();
            Integer startTime = entry.getStartTime();

            simpleJdbcTemplate.update(entrySql.toString(),
                                      seasonId,
                                      timeOfWeekId,
                                      startTime,
                                      temperature,
                                      entryId);

        }

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
            Date startDate = rs.getTimestamp("StartDate");

            ThermostatSeason season = new ThermostatSeason();
            season.setId(id);
            season.setScheduleId(scheduleId);
            season.setWebConfigurationId(webConfigurationId);
            season.setStartDate(startDate);

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
            int temperature = rs.getInt("Temperature");

            ThermostatSeasonEntry entry = new ThermostatSeasonEntry();
            entry.setId(id);
            entry.setSeasonId(seasonId);

            int timeOfWeekDefinitionId = YukonListEntryHelper.getYukonDefinitionId(energyCompany,
                                                                                   YukonSelectionListDefs.YUK_LIST_NAME_TIME_OF_WEEK,
                                                                                   timeOfWeekId);
            TimeOfWeek timeOfWeek = TimeOfWeek.valueOf(timeOfWeekDefinitionId);
            entry.setTimeOfWeek(timeOfWeek);

            entry.setStartTime(startTime);

            entry.setTemperature(temperature);

            return entry;
        }

    }

}

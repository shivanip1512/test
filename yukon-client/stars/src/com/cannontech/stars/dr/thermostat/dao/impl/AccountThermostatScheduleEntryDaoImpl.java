package com.cannontech.stars.dr.thermostat.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.thermostat.dao.AccountThermostatScheduleEntryDao;
import com.cannontech.stars.dr.thermostat.model.AccountThermostatScheduleEntry;
import com.cannontech.stars.dr.thermostat.model.TimeOfWeek;

public class AccountThermostatScheduleEntryDaoImpl implements AccountThermostatScheduleEntryDao {

	private NextValueHelper nextValueHelper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
    private SimpleTableAccessTemplate<AccountThermostatScheduleEntry> accountThermostatScheduleEntryTemplate;
    
	@Override
	public void save(AccountThermostatScheduleEntry atsEntry) {
		
		accountThermostatScheduleEntryTemplate.save(atsEntry);
	}

	@Override
	public AccountThermostatScheduleEntry getById(int atsEntryId) {

		SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT *");
    	sql.append("FROM AcctThermostatScheduleEntry atse");
    	sql.append("WHERE atse.AcctThermostatScheduleEntryId").eq(atsEntryId);
    	
    	return yukonJdbcTemplate.queryForObject(sql, accountThermostatScheduleEntryRowAndFieldMapper);
	}
	
	@Override
	public java.util.List<AccountThermostatScheduleEntry> getAllEntriesForSchduleId(int atsId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT *");
    	sql.append("FROM AcctThermostatScheduleEntry atse");
    	sql.append("WHERE atse.AcctThermostatScheduleId").eq(atsId);
    	sql.append("ORDER BY atse.StartTime");
    	
    	return yukonJdbcTemplate.query(sql, accountThermostatScheduleEntryRowAndFieldMapper);
	}

	@Override
	public void removeAllEntriesForScheduleId(int atsId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("DELETE FROM AcctThermostatScheduleEntry");
		sql.append("WHERE AcctThermostatScheduleId").eq(atsId);
		
		yukonJdbcTemplate.update(sql);
	}

	@PostConstruct
    public void init() {
    	
    	accountThermostatScheduleEntryTemplate = new SimpleTableAccessTemplate<AccountThermostatScheduleEntry>(yukonJdbcTemplate, nextValueHelper);
    	accountThermostatScheduleEntryTemplate.setTableName("AcctThermostatScheduleEntry");
    	accountThermostatScheduleEntryTemplate.setPrimaryKeyField("AcctThermostatScheduleEntryId");
    	accountThermostatScheduleEntryTemplate.setFieldMapper(accountThermostatScheduleEntryRowAndFieldMapper);
    	accountThermostatScheduleEntryTemplate.setPrimaryKeyValidOver(0);
    }
	
	
	private RowAndFieldMapper<AccountThermostatScheduleEntry> accountThermostatScheduleEntryRowAndFieldMapper = new RowAndFieldMapper<AccountThermostatScheduleEntry>() {

    	@Override
    	public AccountThermostatScheduleEntry mapRow(ResultSet rs, int rowNum) throws SQLException {

    		AccountThermostatScheduleEntry atsEntry = new AccountThermostatScheduleEntry();
    		atsEntry.setAccountThermostatScheduleEntryId(rs.getInt("AcctThermostatScheduleEntryId"));
    		atsEntry.setAccountThermostatScheduleId(rs.getInt("AcctThermostatScheduleId"));
    		atsEntry.setStartTime(rs.getInt("StartTime"));
    		atsEntry.setTimeOfWeek(TimeOfWeek.valueOf(rs.getString("TimeOfWeek")));
    		atsEntry.setCoolTemp(new FahrenheitTemperature(rs.getDouble("CoolTemp")));
    		atsEntry.setHeatTemp(new FahrenheitTemperature(rs.getDouble("HeatTemp")));
    		
    		return atsEntry;
    	}
    	
        @Override
        public void extractValues(MapSqlParameterSource p, AccountThermostatScheduleEntry atsEntry) {
        	
            p.addValue("AcctThermostatScheduleId", atsEntry.getAccountThermostatScheduleId());
            p.addValue("StartTime", atsEntry.getStartTime());
            p.addValue("TimeOfWeek", atsEntry.getTimeOfWeek());
            p.addValue("CoolTemp", atsEntry.getCoolTemp().toFahrenheit().getValue());
            p.addValue("HeatTemp", atsEntry.getHeatTemp().toFahrenheit().getValue());
        }

        @Override
        public Number getPrimaryKey(AccountThermostatScheduleEntry object) {
            return object.getAccountThermostatScheduleEntryId();
        }

        @Override
        public void setPrimaryKey(AccountThermostatScheduleEntry object, int value) {
            object.setAccountThermostatScheduleEntryId(value);
        }
    };
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
}

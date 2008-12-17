package com.cannontech.stars.dr.optout.dao.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.EnergyCompanyDao;
import com.cannontech.database.BooleanRowMapper;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideType;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;

/**
 * Implementation class for OptOutEventDao
 */
public class OptOutTemporaryOverrideDaoImpl implements OptOutTemporaryOverrideDao {

	private SimpleJdbcTemplate simpleJdbcTemplate;
	private NextValueHelper nextValueHelper;
	
	private EnergyCompanyDao energyCompanyDao;
	
	@Override
	public boolean getOptOutCounts(LiteEnergyCompany energyCompany) throws NoTemporaryOverrideException {
		return this.getTemporaryOverrideValue(energyCompany, OptOutTemporaryOverrideType.COUNTS);
	}
	
	@Override
	public boolean getOptOutEnabled(LiteEnergyCompany energyCompany) throws NoTemporaryOverrideException {
		return this.getTemporaryOverrideValue(energyCompany, OptOutTemporaryOverrideType.ENABLED);
	}
	

	@Override
	@Transactional
	public void setTemporaryOptOutCounts(LiteYukonUser user, Date startDate,
			Date stopDate, boolean counts) {

		this.setTemporaryOverrideValue(
				OptOutTemporaryOverrideType.COUNTS, 
				user, 
				startDate, 
				stopDate, 
				counts);
		
	}

	@Override
	@Transactional
	public void setTemporaryOptOutEnabled(LiteYukonUser user, Date startDate,
			Date stopDate, boolean enabled) {

		this.setTemporaryOverrideValue(
				OptOutTemporaryOverrideType.ENABLED, 
				user, 
				startDate, 
				stopDate, 
				enabled);
		
	}
	
	/**
	 * Helper method to get a temporarily overridden value
	 * @param energyCompany - Energy company to get value for
	 * @param type - Type of value to get
	 * @return Value if currently overridden
	 * @throws NoTemporaryOverrideException - if there is no temporarily overridden value
	 */
	private boolean getTemporaryOverrideValue(
			LiteEnergyCompany energyCompany, 
			OptOutTemporaryOverrideType type) throws NoTemporaryOverrideException {
		
		Date now = new Date();
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT Value");
		sql.append("FROM OptOutTemporaryOverride");
		sql.append("WHERE Type = ?");
		sql.append("	AND StartDate <= ?");
		sql.append("	AND StopDate > ?");
		sql.append("	AND EnergyCompanyId = ?");
		
		Boolean value;
		try {
			value = simpleJdbcTemplate.queryForObject(sql.toString(), new BooleanRowMapper(), 
					type.toString(),
					now,
					now,
					energyCompany.getEnergyCompanyID());
		} catch (EmptyResultDataAccessException e) {
			// Opt out counts has not been temporarily overridden for this energy company
			throw new NoTemporaryOverrideException();
		}
		
		return value;
	}
	
	/**
	 * Helper method to temporarily override an opt out value
	 * @param type - Type of value to override
	 * @param user - User requesting the change
	 * @param startDate - Date to start temporary change
	 * @param stopDate - Date to stop temporary change
	 * @param value - Value to set as override value
	 */
	private void setTemporaryOverrideValue(OptOutTemporaryOverrideType type,
			LiteYukonUser user, Date startDate, Date stopDate, Object value) {

		LiteEnergyCompany energyCompany = energyCompanyDao.getEnergyCompany(user);
		int energyCompanyID = energyCompany.getEnergyCompanyID();
		String typeString = type.toString();

		// Update any existing override to end when the new override starts
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql = new SqlStatementBuilder();
		sql.append("UPDATE OptOutTemporaryOverride");
		sql.append("SET	StopDate = ?");
		sql.append("WHERE StartDate <= ?");
		sql.append("		AND StopDate > ?");
		sql.append("	AND Type = ?");
		sql.append("	AND EnergyCompanyId = ?");
		
		simpleJdbcTemplate.update(sql.toString(),
				startDate,
				startDate,
				startDate,
				typeString,
				energyCompanyID);
		
		
		sql = new SqlStatementBuilder();
		sql.append("INSERT INTO OptOutTemporaryOverride");
		sql.append("	(OptOutTemporaryOverrideId, UserId, EnergyCompanyId, Type, StartDate");
		sql.append("	, StopDate, Value)");
		sql.append("VALUES (?,?,?,?,?,?,?)");

		int id = nextValueHelper.getNextValue("OptOutTemporaryOverride");
		simpleJdbcTemplate.update(sql.toString(),
				id,
				user.getUserID(),
				energyCompanyID,
				typeString,
				startDate,
				stopDate,
				value);
		
	}
	
	
	@Autowired
	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
	
	@Autowired
	public void setNextValueHelper(NextValueHelper nextValueHelper) {
		this.nextValueHelper = nextValueHelper;
	}
	
	@Autowired
	public void setEnergyCompanyDao(EnergyCompanyDao energyCompanyDao) {
		this.energyCompanyDao = energyCompanyDao;
	}
	
}

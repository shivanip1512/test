package com.cannontech.stars.dr.optout.dao.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideDao;
import com.cannontech.stars.dr.optout.dao.OptOutTemporaryOverrideType;
import com.cannontech.stars.dr.optout.exception.NoTemporaryOverrideException;
import com.cannontech.stars.dr.optout.model.OptOutCounts;
import com.cannontech.stars.dr.optout.model.OptOutCountsTemporaryOverride;
import com.cannontech.stars.dr.optout.model.OptOutEnabled;
import com.cannontech.stars.dr.optout.model.OptOutEnabledTemporaryOverride;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

/**
 * Implementation class for OptOutEventDao
 */
public class OptOutTemporaryOverrideDaoImpl implements OptOutTemporaryOverrideDao {

	@Autowired private YukonJdbcTemplate yukonJdbcTemplate;
	@Autowired private NextValueHelper nextValueHelper;
	@Autowired private EnergyCompanyDao ecService;
	
	@Override
	public List<OptOutCountsTemporaryOverride> getAllOptOutCounts(EnergyCompany energyCompany) throws NoTemporaryOverrideException {
		
		Date now = new Date();
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT *");
		sql.append("FROM OptOutTemporaryOverride");
		sql.append("WHERE OptOutType").eq(OptOutTemporaryOverrideType.COUNTS);
		sql.append("	AND StartDate").lte(now);
		sql.append("	AND StopDate").gt(now);
		sql.append("	AND EnergyCompanyId").eq(energyCompany.getId());
		
		List<OptOutCountsTemporaryOverride> settings = yukonJdbcTemplate.query(sql, new OptOutCountsTemporaryOverrideRowMapper());
			
		if (settings.size() == 0) {
			// Opt out counts has not been temporarily overridden for this energy company
			throw new NoTemporaryOverrideException();
		} 
		
		return settings;
	}

	
	@Override
	public OptOutEnabledTemporaryOverride findCurrentSystemOptOutTemporaryOverrides(int energyCompanyId) {
		
		Date now = new Date();
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT *");
		sql.append("FROM OptOutTemporaryOverride");
		sql.append("WHERE OptOutType").eq(OptOutTemporaryOverrideType.OPT_OUTS);
		sql.append("  AND StartDate").lte(now);
		sql.append("  AND StopDate").gt(now);
		sql.append("  AND EnergyCompanyId").eq(energyCompanyId);
		sql.append("  AND ProgramId IS NULL");
		
		try {
    		return yukonJdbcTemplate.queryForObject(sql, new OptOutEnabledTemporaryOverrideRowMapper());
		} catch (EmptyResultDataAccessException e) {
		    return null;
        }
	}

    @Override
    public List<OptOutEnabledTemporaryOverride> getCurrentProgramOptOutTemporaryOverrides(int energyCompanyId) {
        
        Date now = new Date();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM OptOutTemporaryOverride");
        sql.append("WHERE OptOutType").eq(OptOutTemporaryOverrideType.OPT_OUTS);
        sql.append("  AND StartDate").lte(now);
        sql.append("  AND StopDate").gt(now);
        sql.append("  AND EnergyCompanyId").eq(energyCompanyId);
        sql.append("  AND ProgramId IS NOT NULL");
        
        List<OptOutEnabledTemporaryOverride> optOutTemporaryOverrides = 
            yukonJdbcTemplate.query(sql, new OptOutEnabledTemporaryOverrideRowMapper());
        
        return optOutTemporaryOverrides;
    }

	@Override
	@Transactional
	public void setTemporaryOptOutCounts(LiteYukonUser user, Date startDate, Date stopDate, boolean counts) {
		doSetTemporaryOptOutCounts(user, startDate, stopDate, counts, null);
	}
	
	@Override
	@Transactional
	public void setTemporaryOptOutCountsForProgramId(LiteYukonUser user, Date startDate, Date stopDate, boolean counts, int webpublishingProgramId) {

		doSetTemporaryOptOutCounts(user, startDate, stopDate, counts, webpublishingProgramId);
	}
	
	private void doSetTemporaryOptOutCounts(LiteYukonUser user, Date startDate, Date stopDate, boolean counts, Integer webpublishingProgramId) {
		
		this.setTemporaryOverrideValue(
				OptOutTemporaryOverrideType.COUNTS, 
				user, 
				startDate, 
				stopDate, 
				OptOutCounts.valueOf(counts),
				webpublishingProgramId);
	}

	@Override
	@Transactional
	public void setTemporaryOptOutEnabled(LiteYukonUser user, Date startDate, Date stopDate, OptOutEnabled enabled) {

		this.setTemporaryOverrideValue(
				OptOutTemporaryOverrideType.OPT_OUTS, 
				user, 
				startDate, 
				stopDate, 
				enabled,
				null);
		
	}
	
    @Override
    @Transactional
    public void setTemporaryOptOutEnabled(LiteYukonUser user, Date startDate, Date stopDate, 
                                          OptOutEnabled enabled, int webpublishingProgramId) {

        this.setTemporaryOverrideValue(OptOutTemporaryOverrideType.OPT_OUTS, 
                                       user, 
                                       startDate, 
                                       stopDate, 
                                       enabled,
                                       webpublishingProgramId);
        
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
			LiteYukonUser user, Date startDate, Date stopDate, Object value, Integer webpublishingProgramId) {

		EnergyCompany energyCompany = ecService.getEnergyCompany(user);
		int energyCompanyID = energyCompany.getId();
		String typeString = type.toString();

		// Update any existing override to end when the new override starts
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql = new SqlStatementBuilder();
		sql.append("UPDATE OptOutTemporaryOverride");
		sql.append("SET	StopDate").eq(startDate);
		sql.append("WHERE StartDate").lte(startDate);
		sql.append("AND StopDate").gt(startDate);
		sql.append("AND OptOutType").eq(typeString);
		sql.append("AND EnergyCompanyId").eq(energyCompanyID);
		if (webpublishingProgramId != null) {
			sql.append("AND ProgramId").eq(webpublishingProgramId);
		}
		yukonJdbcTemplate.update(sql);
		
		// insert new override setting
		sql = new SqlStatementBuilder();
		int id = nextValueHelper.getNextValue("OptOutTemporaryOverride");
		sql.append("INSERT INTO OptOutTemporaryOverride");
		sql.append("(OptOutTemporaryOverrideId, UserId, EnergyCompanyId, OptOutType, StartDate, StopDate, OptOutValue, ProgramId)");
		sql.append("VALUES (?,?,?,?,?,?,?,?)");
		yukonJdbcTemplate.update(sql.toString(), id, user.getUserID(), energyCompanyID, typeString, startDate, stopDate, String.valueOf(value), webpublishingProgramId);
	}
	
	// Row Mappers
	private final class OptOutEnabledTemporaryOverrideRowMapper implements YukonRowMapper<OptOutEnabledTemporaryOverride> {

        @Override
        public OptOutEnabledTemporaryOverride mapRow(YukonResultSet rs) throws SQLException {
            
            OptOutEnabledTemporaryOverride result = new OptOutEnabledTemporaryOverride();
            
            result.setOptOutTemporaryOverrideId(rs.getInt("OptOutTemporaryOverrideId"));
            result.setUserId(rs.getInt("UserId"));
            result.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
            result.setStartDate(rs.getInstant("StartDate"));
            result.setStopDate(rs.getInstant("StopDate"));
            result.setOptOutValue(OptOutEnabled.valueOf(rs.getStringSafe("OptOutValue")));
            result.setAssignedProgramId(rs.getNullableInt("ProgramId"));
            
            return result;
        }
    }
	
	private final class OptOutCountsTemporaryOverrideRowMapper implements YukonRowMapper<OptOutCountsTemporaryOverride> {
	    
	    @Override
	    public OptOutCountsTemporaryOverride mapRow(YukonResultSet rs) throws SQLException {
	        
	        OptOutCountsTemporaryOverride result = new OptOutCountsTemporaryOverride();
	        
	        result.setOptOutTemporaryOverrideId(rs.getInt("OptOutTemporaryOverrideId"));
	        result.setUserId(rs.getInt("UserId"));
	        result.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));
	        result.setStartDate(rs.getInstant("StartDate"));
	        result.setStopDate(rs.getInstant("StopDate"));
	        result.setCounting(OptOutCounts.valueOf(rs.getStringSafe("OptOutValue")));
	        result.setAssignedProgramId(rs.getNullableInt("ProgramId"));
	        
	        return result;
	    }
	}
}

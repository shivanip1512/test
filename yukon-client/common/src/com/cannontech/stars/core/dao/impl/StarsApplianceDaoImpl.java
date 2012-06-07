package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.core.dao.StarsApplianceDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsAppliance;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.StarsLiteFactory;
import com.cannontech.stars.database.db.appliance.ApplianceBase;
import com.cannontech.stars.database.db.hardware.LMHardwareConfiguration;

public class StarsApplianceDaoImpl implements StarsApplianceDao {
    private YukonJdbcTemplate yukonJdbcTemplate;
    private StarsDatabaseCache starsDatabaseCache;
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteStarsAppliance> getByAccountId(int accountId, int energyCompanyId) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT AccountID,ApplianceCategoryID,ProgramID,YearManufactured,");
        sqlBuilder.append(" ManufacturerID,LocationID,KWCapacity,EfficiencyRating,Notes,ModelNumber,");
        sqlBuilder.append(" InventoryID,lmhc.ApplianceID,AddressingGroupID,LoadNumber");
        sqlBuilder.append("FROM ApplianceBase ab,LMHardwareConfiguration lmhc");
        sqlBuilder.append("WHERE ab.ApplianceID = lmhc.ApplianceID");
        sqlBuilder.append("AND AccountID = ?");
        final String sql = sqlBuilder.toString();
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        ParameterizedRowMapper<LiteStarsAppliance> rowMapper = createRowMapper(energyCompany);
        
        List<LiteStarsAppliance> list = yukonJdbcTemplate.query(sql, rowMapper, accountId);
        return list;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<LiteStarsAppliance> getUnassignedAppliances(int accountId, int energyCompanyId) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT AccountID,ApplianceID,ApplianceCategoryID,ProgramID,YearManufactured, ");
        sqlBuilder.append("ManufacturerID,LocationID,KWCapacity,EfficiencyRating,Notes,ModelNumber ");
        sqlBuilder.append("FROM ApplianceBase ab ");
        sqlBuilder.append("WHERE ab.AccountID = ? AND NOT EXISTS ");        
        sqlBuilder.append("(SELECT InventoryID FROM  LMHardwareConfiguration lmhc WHERE ab.ApplianceID = lmhc.ApplianceID) ");        
        sqlBuilder.append("ORDER BY ab.ApplianceID DESC ");
        final String sql = sqlBuilder.toString();
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        ParameterizedRowMapper<LiteStarsAppliance> unassignedAppRowMapper = createUnassignedAppRowMapper(energyCompany);
        
        List<LiteStarsAppliance> list = yukonJdbcTemplate.query(sql, unassignedAppRowMapper, accountId);
        return list;
    }    
    
    @Override
    public LiteStarsAppliance getByApplianceIdAndEnergyCompanyId(int applianceId, int energyCompanyId) {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        sqlBuilder.append("SELECT AB.AccountId, AB.ApplianceCategoryId, AB.ProgramId,");
        sqlBuilder.append("       AB.YearManufactured, AB.ManufacturerId, AB.LocationId,");
        sqlBuilder.append("       AB.KWCapacity, AB.EfficiencyRating, AB.Notes, AB.ModelNumber,");
        sqlBuilder.append("       LMHC.InventoryId, AB.ApplianceId, LMHC.AddressingGroupId,");
        sqlBuilder.append("       LMHC.LoadNumber ");
        sqlBuilder.append("FROM ApplianceBase AB");
        sqlBuilder.append("LEFT JOIN LMHardwareConfiguration LMHC ON AB.ApplianceId = LMHC.ApplianceId");
        sqlBuilder.append("WHERE AB.ApplianceId").eq(applianceId);
        
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(energyCompanyId);
        ParameterizedRowMapper<LiteStarsAppliance> rowMapper = createRowMapper(energyCompany);
        
        LiteStarsAppliance liteStarsAppliance = yukonJdbcTemplate.queryForObject(sqlBuilder, rowMapper);
        return liteStarsAppliance;
        
    }

    private ParameterizedRowMapper<LiteStarsAppliance> createRowMapper(final LiteStarsEnergyCompany energyCompany) {
        return new ParameterizedRowMapper<LiteStarsAppliance>() {
            @Override
            public LiteStarsAppliance mapRow(ResultSet rs, int rowNum) throws SQLException {
                final int applianceId = rs.getInt("ApplianceID");
                
                ApplianceBase applianceBase = new ApplianceBase();
                applianceBase.setAccountID(rs.getInt("AccountID"));
                applianceBase.setApplianceID(applianceId);
                applianceBase.setApplianceCategoryID(rs.getInt("ApplianceCategoryID"));
                applianceBase.setProgramID(rs.getInt("ProgramID"));
                applianceBase.setYearManufactured(rs.getInt("YearManufactured"));
                applianceBase.setManufacturerID(rs.getInt("ManufacturerID"));
                applianceBase.setLocationID(rs.getInt("LocationID"));
                applianceBase.setKWCapacity(rs.getDouble("KWCapacity"));
                applianceBase.setEfficiencyRating(rs.getDouble("EfficiencyRating"));
                applianceBase.setNotes(rs.getString("Notes"));
                applianceBase.setModelNumber(rs.getString("ModelNumber"));
                
                LMHardwareConfiguration config = new LMHardwareConfiguration();
                config.setInventoryID(rs.getInt("InventoryID"));
                config.setApplianceID(applianceId);
                config.setAddressingGroupID(rs.getInt("AddressingGroupID"));
                config.setLoadNumber(rs.getInt("LoadNumber"));
                
                com.cannontech.stars.database.data.appliance.ApplianceBase dataApplianceBase =
                    new com.cannontech.stars.database.data.appliance.ApplianceBase();
                dataApplianceBase.setApplianceBase(applianceBase);
                dataApplianceBase.setLMHardwareConfig(config);
                
                LiteStarsAppliance liteAppliance = 
                    StarsLiteFactory.createLiteStarsAppliance(dataApplianceBase, energyCompany);
                return liteAppliance;
            }
        };
    }
    
    private ParameterizedRowMapper<LiteStarsAppliance> createUnassignedAppRowMapper(final LiteStarsEnergyCompany energyCompany) {
        return new ParameterizedRowMapper<LiteStarsAppliance>() {
            @Override
            public LiteStarsAppliance mapRow(ResultSet rs, int rowNum) throws SQLException {
                final int applianceId = rs.getInt("ApplianceID");
                
                ApplianceBase applianceBase = new ApplianceBase();
                applianceBase.setAccountID(rs.getInt("AccountID"));
                applianceBase.setApplianceID(applianceId);
                applianceBase.setApplianceCategoryID(rs.getInt("ApplianceCategoryID"));
                applianceBase.setProgramID(rs.getInt("ProgramID"));
                applianceBase.setYearManufactured(rs.getInt("YearManufactured"));
                applianceBase.setManufacturerID(rs.getInt("ManufacturerID"));
                applianceBase.setLocationID(rs.getInt("LocationID"));
                applianceBase.setKWCapacity(rs.getDouble("KWCapacity"));
                applianceBase.setEfficiencyRating(rs.getDouble("EfficiencyRating"));
                applianceBase.setNotes(rs.getString("Notes"));
                applianceBase.setModelNumber(rs.getString("ModelNumber"));
                
                com.cannontech.stars.database.data.appliance.ApplianceBase dataApplianceBase =
                    new com.cannontech.stars.database.data.appliance.ApplianceBase();
                dataApplianceBase.setApplianceBase(applianceBase);
                
                LiteStarsAppliance liteAppliance = 
                    StarsLiteFactory.createLiteStarsAppliance(dataApplianceBase, energyCompany);
                return liteAppliance;
            }
        };
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setStarsDatabaseCache(StarsDatabaseCache starsDatabaseCache) {
        this.starsDatabaseCache = starsDatabaseCache;
    }

}

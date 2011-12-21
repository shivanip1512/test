package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.SiteInformationDao;

public class SiteInformationDaoImpl implements SiteInformationDao, InitializingBean {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private static String TABLE_NAME = "SiteInformation";
    private static final ParameterizedRowMapper<LiteSiteInformation> rowMapper;
    private SimpleTableAccessTemplate<LiteSiteInformation> siteInfoTemplate;
    
    static {
        rowMapper = SiteInformationDaoImpl.createRowMapper();
    }

    public void afterPropertiesSet() throws Exception {
        siteInfoTemplate = new SimpleTableAccessTemplate<LiteSiteInformation>(yukonJdbcTemplate, nextValueHelper);
        siteInfoTemplate.setTableName(TABLE_NAME);
        siteInfoTemplate.setPrimaryKeyField("SiteId");
        siteInfoTemplate.setFieldMapper(siteInfoFieldMapper); 
    }

    private FieldMapper<LiteSiteInformation> siteInfoFieldMapper = new FieldMapper<LiteSiteInformation>() {

        @Override
        public void extractValues(MapSqlParameterSource p, LiteSiteInformation o) {
            p.addValue("Feeder", SqlUtils.convertStringToDbValue(o.getFeeder()));
            p.addValue("Pole", SqlUtils.convertStringToDbValue(o.getPole()));
            p.addValue("TransformerSize", SqlUtils.convertStringToDbValue(o.getTransformerSize()));
            p.addValue("ServiceVoltage", SqlUtils.convertStringToDbValue(o.getServiceVoltage()));
            p.addValue("SubstationId", o.getSubstationID());
        }

        @Override
        public Number getPrimaryKey(LiteSiteInformation object) {
            return object.getSiteID() == CtiUtilities.NONE_ZERO_ID ? null : object.getSiteID();
        }

        @Override
        public void setPrimaryKey(LiteSiteInformation object, int value) {
            object.setSiteID(value);
        }
    };
    
    @Transactional
    @Override
    public LiteSiteInformation getSiteInfoById(int siteInfoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SiteId, Feeder, Pole, TransformerSize, ServiceVoltage, SubstationId");
        sql.append("FROM SiteInformation");
        sql.append("WHERE SiteId").eq(siteInfoId);

        LiteSiteInformation siteInfo = yukonJdbcTemplate.queryForObject(sql, rowMapper); 
        return siteInfo;
    }
    
    private static final ParameterizedRowMapper<LiteSiteInformation> createRowMapper() {
        final ParameterizedRowMapper<LiteSiteInformation> rowMapper = new ParameterizedRowMapper<LiteSiteInformation>() {
            public LiteSiteInformation mapRow(ResultSet rs, int rowNum) throws SQLException {
                final LiteSiteInformation siteInfo = new LiteSiteInformation();
                siteInfo.setSiteID(rs.getInt("SiteId"));
                siteInfo.setFeeder(SqlUtils.convertDbValueToString(rs, "Feeder"));
                siteInfo.setPole(SqlUtils.convertDbValueToString(rs, "Pole"));
                siteInfo.setTransformerSize(SqlUtils.convertDbValueToString(rs, "TransformerSize"));
                siteInfo.setServiceVoltage(SqlUtils.convertDbValueToString(rs, "ServiceVoltage"));
                siteInfo.setSubstationID(rs.getInt("SubstationId"));
                return siteInfo;
            }
        };
        return rowMapper;
    }
    
    @Override
    public int getSubstationIdByName(String subName) throws NotFoundException{
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SubstationId FROM Substation WHERE SubstationName = ?");
        try {
            int subId = yukonJdbcTemplate.queryForInt(sql.toString(), subName);
            return subId;
        }catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find SubstationId for name: " + subName);
        }
    }
    
    @Override
    public String getSubstationNameById(int subId) throws NotFoundException{
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT SubstationName FROM Substation WHERE SubstationId = ?");
        try {
            String subName = yukonJdbcTemplate.queryForObject(sql.toString(), String.class, subId);
            return subName;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find SubstationName for Id: " + subId);
        }
    }
    
    
    @Override
    @Transactional
    public void add(LiteSiteInformation liteSiteInformation) {
        siteInfoTemplate.insert(liteSiteInformation);
    }

    @Override
    @Transactional
    public void delete(LiteSiteInformation liteSiteInformation) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM SiteInformation");
        sql.append("WHERE SiteId").eq(liteSiteInformation.getSiteID());

        yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public int resetSubstation(int substationId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE SiteInformation");
        sql.append("SET SubstationId = 0");
        sql.append("WHERE SubstationId").eq(substationId);
        
        return yukonJdbcTemplate.update(sql);
    }

    @Override
    @Transactional
    public void update(LiteSiteInformation liteSiteInformation) {
        siteInfoTemplate.update(liteSiteInformation);
    }
}

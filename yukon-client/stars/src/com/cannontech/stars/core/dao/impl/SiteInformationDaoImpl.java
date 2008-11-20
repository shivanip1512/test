package com.cannontech.stars.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.SiteInformationDao;

public class SiteInformationDaoImpl implements SiteInformationDao, InitializingBean {
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private SimpleTableAccessTemplate<LiteSiteInformation> siteInfoTemplate;
    private NextValueHelper nextValueHelper;
    private static String TABLE_NAME = "SiteInformation";
    private static final ParameterizedRowMapper<LiteSiteInformation> rowMapper;
    
    static {
        rowMapper = SiteInformationDaoImpl.createRowMapper();
    }
    
    private FieldMapper<LiteSiteInformation> siteInfoFieldMapper = new FieldMapper<LiteSiteInformation>() {

        @Override
        public void extractValues(MapSqlParameterSource p, LiteSiteInformation o) {
            p.addValue("Feeder", o.getFeeder());
            p.addValue("Pole", o.getPole());
            p.addValue("TransformerSize", o.getTransformerSize());
            p.addValue("ServiceVoltage", o.getServiceVoltage());
            p.addValue("SubstationId", o.getSubstationID());
        }

        @Override
        public Number getPrimaryKey(LiteSiteInformation object) {
            return object.getSiteID();
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
        sql.append("FROM SiteInformation WHERE SiteId = ?");
        LiteSiteInformation siteInfo = simpleJdbcTemplate.queryForObject(sql.toString(), rowMapper, siteInfoId); 
        return siteInfo;
    }
    
    private static final ParameterizedRowMapper<LiteSiteInformation> createRowMapper() {
        final ParameterizedRowMapper<LiteSiteInformation> rowMapper = new ParameterizedRowMapper<LiteSiteInformation>() {
            public LiteSiteInformation mapRow(ResultSet rs, int rowNum) throws SQLException {
                final LiteSiteInformation siteInfo = new LiteSiteInformation();
                siteInfo.setSiteID(rs.getInt("SiteId"));
                siteInfo.setFeeder(rs.getString("Feeder"));
                siteInfo.setPole(rs.getString("Pole"));
                siteInfo.setTransformerSize(rs.getString("TransformerSize"));
                siteInfo.setServiceVoltage(rs.getString("ServiceVoltage"));
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
            int subId = simpleJdbcTemplate.queryForInt(sql.toString(), subName);
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
            String subName = simpleJdbcTemplate.queryForObject(sql.toString(), String.class, subId);
            return subName;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("Unable to find SubstationName for Id: " + subId);
        }
    }
    
    
    @Override
    public void add(LiteSiteInformation liteSiteInformation) {
        siteInfoTemplate.insert(liteSiteInformation);
    }

    @Override
    public void delete(LiteSiteInformation liteSiteInformation) {
        // TODO Auto-generated method stub

    }

    @Override
    public void update(LiteSiteInformation liteSiteInformation) {
        siteInfoTemplate.update(liteSiteInformation);
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(final SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(final NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    public void afterPropertiesSet() throws Exception {
        siteInfoTemplate = new SimpleTableAccessTemplate<LiteSiteInformation>(simpleJdbcTemplate, nextValueHelper);
        siteInfoTemplate.withTableName(TABLE_NAME);
        siteInfoTemplate.withPrimaryKeyField("SiteId");
        siteInfoTemplate.withFieldMapper(siteInfoFieldMapper); 
    }

}

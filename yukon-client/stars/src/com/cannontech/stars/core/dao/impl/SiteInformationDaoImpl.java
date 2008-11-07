package com.cannontech.stars.core.dao.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.data.lite.stars.LiteSiteInformation;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.core.dao.SiteInformationDao;

public class SiteInformationDaoImpl implements SiteInformationDao, InitializingBean {
    
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private static String TABLE_NAME = "SiteInformation";
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
    
    private SimpleTableAccessTemplate<LiteSiteInformation> siteInfoTemplate;
    
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

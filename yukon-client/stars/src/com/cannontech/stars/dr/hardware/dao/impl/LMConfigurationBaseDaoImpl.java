package com.cannontech.stars.dr.hardware.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.stars.dr.hardware.dao.LMConfigurationBaseDao;

public class LMConfigurationBaseDaoImpl implements LMConfigurationBaseDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    @Transactional
    public void delete(int configurationId) {

        //Delete the LM Configuration from child tables and base table
        String deleteChildSql = "DELETE FROM LMConfigurationExpressCom WHERE ConfigurationID = ?";
        simpleJdbcTemplate.update(deleteChildSql, configurationId);
        
        deleteChildSql = "DELETE FROM LMConfigurationVersaCom WHERE ConfigurationID = ?";
        simpleJdbcTemplate.update(deleteChildSql, configurationId);        

        deleteChildSql = "DELETE FROM LMConfigurationSA205 WHERE ConfigurationID = ?";
        simpleJdbcTemplate.update(deleteChildSql, configurationId);        

        deleteChildSql = "DELETE FROM LMConfigurationSA305 WHERE ConfigurationID = ?";
        simpleJdbcTemplate.update(deleteChildSql, configurationId);        
        
        deleteChildSql = "DELETE FROM LMConfigurationSASimple WHERE ConfigurationID = ?";
        simpleJdbcTemplate.update(deleteChildSql, configurationId);        
        
        String deleteBaseSql = "DELETE FROM LMConfigurationBase WHERE ConfigurationID = ?";
        simpleJdbcTemplate.update(deleteBaseSql, configurationId);
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}

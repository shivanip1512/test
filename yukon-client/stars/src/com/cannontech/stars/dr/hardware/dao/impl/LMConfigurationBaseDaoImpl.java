package com.cannontech.stars.dr.hardware.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.stars.dr.hardware.dao.LMConfigurationBaseDao;

public class LMConfigurationBaseDaoImpl implements LMConfigurationBaseDao {
    private SimpleJdbcTemplate simpleJdbcTemplate;

    public static final String[] DEPENDENT_TABLES = {
            "LMConfigurationExpressCom", "LMConfigurationVersaCom",
            "LMConfigurationSA205", "LMConfigurationSA305",
            "LMConfigurationSASimple" };

    /**
     * Deletes the LMConfigurationBase and its child tables
     * @param configurationId
     */
    public void delete(int configurationId) {

        for (String table : DEPENDENT_TABLES) {
            String deleteChildSql = "DELETE FROM " + table + " WHERE ConfigurationID = ?";
            simpleJdbcTemplate.update(deleteChildSql, configurationId);
        }
        String deleteBaseSql = "DELETE FROM LMConfigurationBase WHERE ConfigurationID = ?";
        simpleJdbcTemplate.update(deleteBaseSql, configurationId);
    }

    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }
}

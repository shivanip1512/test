package com.cannontech.core.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.StandaloneRoleDao;
import com.cannontech.roles.YukonGroupRoleDefs;

public class StandaloneRoleDaoImpl implements StandaloneRoleDao {
    private SimpleJdbcTemplate jdbcTemplate;
    private Logger log = YukonLogManager.getLogger(StandaloneRoleDaoImpl.class);

    @Override
    public String getGlobalPropertyValue(int rolePropertyId) {
        // try to get stored value
        String ygrSql =
            "select value " +
            "from YukonGroupRole " +
            "where RolePropertyId = ? " +
            "  and GroupId = ?";
        try {
            String value =
                jdbcTemplate.queryForObject(ygrSql,
                                            String.class,
                                            rolePropertyId,
                                            YukonGroupRoleDefs.GRP_YUKON);
            if (!CtiUtilities.STRING_NONE.equals(value)) {
                return value;
            }
            log.debug("got " + CtiUtilities.STRING_NONE + " value when looking up " + rolePropertyId);
        } catch (IncorrectResultSizeDataAccessException e) {
            log.debug("got " + e + " value when looking up " + rolePropertyId);
        }

        // go get default
        String yrpSql =
            "select DefaultValue " +
            "from YukonRoleProperty " +
            "where RolePropertyId = ?";
        String value = jdbcTemplate.queryForObject(yrpSql, String.class, rolePropertyId);

        // if we can't find the default here, we might as well throw an
        // exception
        return value;
    }

    @Required
    public void setJdbcTemplate(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}

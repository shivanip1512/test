package com.cannontech.dr.eatonCloud.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.dr.eatonCloud.dao.EatonCloudDao;

public class EatonCloudDaoImpl implements EatonCloudDao{
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public int getVirtualRelayId(int yukonLmGroupId) throws NotFoundException {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT RelayUsage");
        sql.append("FROM LMGroupEatonCloud");
        sql.append("WHERE YukonGroupId").eq(yukonLmGroupId);
        try {
            return jdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("VirtualRelayId with YukonGroupId: " + yukonLmGroupId + " was not found.", e);
        }
    }

}

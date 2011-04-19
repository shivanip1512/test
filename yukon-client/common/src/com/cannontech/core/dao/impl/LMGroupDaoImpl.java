package com.cannontech.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMGroupDao;
import com.cannontech.database.YukonJdbcTemplate;

public class LMGroupDaoImpl implements LMGroupDao {

    private YukonJdbcTemplate yukonJdbcTemplate;
    
    @Override
    public byte getUtilityEnrollmentGroupForSepGroup(int groupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT UtilityEnrollmentGroup");
        sql.append("FROM LMGroupSEP");
        sql.append("WHERE DeviceId").eq(groupId);
        
        return (byte)yukonJdbcTemplate.queryForInt(sql);
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}

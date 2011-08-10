package com.cannontech.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RphManagementDao;
import com.cannontech.database.YukonJdbcTemplate;

public class RphManagementDaoImpl implements RphManagementDao {
    private YukonJdbcTemplate yukonTemplate;

    @Override
    public int deleteDuplicates() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH CTE AS (");
        sql.append(     "SELECT ROW_NUMBER() OVER (PARTITION BY PointId, Value, Timestamp, Quality ORDER BY ChangeId) RN");
        sql.append(     "FROM RAWPOINTHISTORY");
        sql.append(")");
        sql.append("DELETE FROM CTE WHERE RN <> 1");
        return yukonTemplate.update(sql);
    }

    @Autowired
    public void setJdbcTemplate(YukonJdbcTemplate yukonTemplate) {
        this.yukonTemplate = yukonTemplate;
    }
}

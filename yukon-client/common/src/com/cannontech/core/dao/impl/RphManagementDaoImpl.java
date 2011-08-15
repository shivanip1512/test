package com.cannontech.core.dao.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.RphManagementDao;
import com.cannontech.database.LongRowMapper;
import com.cannontech.database.YukonJdbcTemplate;

public class RphManagementDaoImpl implements RphManagementDao {
    private YukonJdbcTemplate yukonTemplate;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    
    @PostConstruct
    public void initialize() {
        chunkyJdbcTemplate = new ChunkingSqlTemplate(yukonTemplate);
    }
    
    @Override
    public int deleteDuplicates() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH CTE AS (");
        sql.append(     "SELECT CHANGEID, ROW_NUMBER() OVER (PARTITION BY PointId, Value, Timestamp, Quality ORDER BY ChangeId) RN");
        sql.append(     "FROM RAWPOINTHISTORY");
        sql.append(")");
        sql.append("SELECT CHANGEID FROM CTE WHERE RN <> 1");
        List<Long> changeIds = yukonTemplate.queryForLimitedResults(sql, new LongRowMapper(), 1000000);
        chunkyJdbcTemplate.update(new RphDeleteSqlGenerator(), changeIds);
        return changeIds.size();
    }
    
    private class RphDeleteSqlGenerator implements SqlFragmentGenerator<Long> {
        @Override
        public SqlFragmentSource generate(List<Long> changeIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM RAWPOINTHISTORY WHERE CHANGEID").in(changeIds);
            return sql;
        }
    }
    
    @Autowired
    public void setJdbcTemplate(YukonJdbcTemplate yukonTemplate) {
        this.yukonTemplate = yukonTemplate;
    }
}

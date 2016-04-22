package com.cannontech.core.dao.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DatabaseManagementDao;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.vendor.DatabaseVendorResolver;

public class DatabaseManagementDaoImpl implements DatabaseManagementDao {

    private static final Logger log = YukonLogManager.getLogger(DatabaseManagementDaoImpl.class);
    private final static int ROWS_TO_DELETE = 1000000;

    @Autowired private DatabaseVendorResolver dbVendorResolver;
    @Autowired private YukonJdbcTemplate yukonTemplate;
    private ChunkingSqlTemplate chunkyJdbcTemplate;
    
    @PostConstruct
    public void initialize() {
        chunkyJdbcTemplate = new ChunkingSqlTemplate(yukonTemplate);
    }
    
    @Override
    public int deleteRphDuplicates() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("WITH CTE AS (");
        sql.append(     "SELECT CHANGEID, ROW_NUMBER() OVER (PARTITION BY PointId, Value, Timestamp, Quality ORDER BY ChangeId) RN");
        sql.append(     "FROM RAWPOINTHISTORY");
        sql.append(")");
        sql.append("SELECT CHANGEID FROM CTE WHERE RN <> 1");
        List<Long> changeIds = yukonTemplate.queryForLimitedResults(sql, TypeRowMapper.LONG, ROWS_TO_DELETE);
        chunkyJdbcTemplate.update(new RphDeleteByChangeIdsSqlGenerator(), changeIds);
        return changeIds.size();
    }
    
    @Override
    public int deleteRphDanglingEntries() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointId FROM RAWPOINTHISTORY");
        sql.append(" WHERE PointId NOT IN (SELECT PointId FROM POINT)");
        List<Long> pointIds = yukonTemplate.queryForLimitedResults(sql, TypeRowMapper.LONG, ROWS_TO_DELETE);
        chunkyJdbcTemplate.update(new RphDeleteByPointIdsSqlGenerator(), pointIds);
        return pointIds.size();
    }
    
    @Override
    public int deleteSystemLogDanglingEntries() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PointId FROM SYSTEMLOG");
        sql.append(" WHERE PointId NOT IN (SELECT PointId FROM POINT)");
        List<Long> pointIds = yukonTemplate.queryForLimitedResults(sql, TypeRowMapper.LONG, ROWS_TO_DELETE);
        chunkyJdbcTemplate.update(new SystemLogDeleteSqlGenerator(),  pointIds);
        return  pointIds.size();
    }
    
    @Override
    public void executeSpSmartIndexMaintanence() throws DataAccessException {
        if (dbVendorResolver.getDatabaseVendor().isSqlServer()) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("EXECUTE sp_SmartIndexMaintenance");
            yukonTemplate.update(sql);
        } else {
            log.warn("Smart Index Management is not supported by your database: " + 
                    dbVendorResolver.getDatabaseVendor().toString() + " . No maintenance performed.");
        }
    }
    
    private class RphDeleteByChangeIdsSqlGenerator implements SqlFragmentGenerator<Long> {
        @Override
        public SqlFragmentSource generate(List<Long> changeIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM RAWPOINTHISTORY WHERE CHANGEID").in(changeIds);
            return sql;
        }
    }
    
    private class RphDeleteByPointIdsSqlGenerator implements SqlFragmentGenerator<Long> {
        @Override
        public SqlFragmentSource generate(List<Long> changeIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM RAWPOINTHISTORY WHERE PointId").in(changeIds);
            return sql;
        }
    }
    
    private class SystemLogDeleteSqlGenerator implements SqlFragmentGenerator<Long> {
        @Override
        public SqlFragmentSource generate(List<Long>  pointIds) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM SYSTEMLOG WHERE PointId").in(pointIds);
            return sql;
        }
    }
}

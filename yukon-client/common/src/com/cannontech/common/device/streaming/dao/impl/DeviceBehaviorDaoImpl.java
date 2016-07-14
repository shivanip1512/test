package com.cannontech.common.device.streaming.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorReport;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.google.common.collect.Lists;

public class DeviceBehaviorDaoImpl implements DeviceBehaviorDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    private final static Logger log = YukonLogManager.getLogger(DeviceBehaviorDaoImpl.class);

    @Override
    @Transactional
    public void deleteUnusedBehaviors() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Behavior");
        sql.append("WHERE BehaviorId NOT IN");
        sql.append("  (SELECT BehaviorId");
        sql.append("   FROM DeviceBehaviorMap)");

        jdbcTemplate.update(sql);
    }

    @Override
    @Transactional
    public void assignBehavior(int behaviorId,  BehaviorType type, List<Integer> deviceIds) {
        log.debug("Devices to assign=" + deviceIds.size());

        List<List<Integer>> ids = Lists.partition(deviceIds, ChunkingSqlTemplate.DEFAULT_SIZE);
        ids.forEach(idBatch -> {
            unassignBehaviorForBatch(type, idBatch);
            
            //assign devices
            SqlStatementBuilder insertSql = new SqlStatementBuilder();
            insertSql.append("INSERT INTO DeviceBehaviorMap");
            insertSql.append("(DeviceId, BehaviorId)");
            insertSql.append("values");
            insertSql.append("(?, ?)");
            
            jdbcTemplate.batchUpdate(insertSql.toString(), new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, idBatch.get(i));
                    ps.setInt(2, behaviorId);
                }

                @Override
                public int getBatchSize() {
                    return idBatch.size();
                }
            });
        });
        
        deleteUnusedBehaviors();
    }
    
    @Override
    @Transactional
    public void unassignBehavior(BehaviorType type, List<Integer> deviceIds) {
        log.debug("Devices to ussign=" + deviceIds.size());

        List<List<Integer>> ids = Lists.partition(deviceIds, ChunkingSqlTemplate.DEFAULT_SIZE);
        ids.forEach(idBatch -> {
            unassignBehaviorForBatch(type, idBatch);
        });
        
        deleteUnusedBehaviors();
    }
    
    @Override
    public List<Behavior> getBehaviorsByType(BehaviorType type) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorId");
        sql.append("FROM Behavior");
        sql.append("WHERE BehaviorType").eq_k(type);

        return jdbcTemplate.query(sql, new YukonRowMapper<Behavior>() {
            @Override
            public Behavior mapRow(YukonResultSet rs) throws SQLException {
                Behavior behavior = new Behavior();
                behavior.setId(rs.getInt("BehaviorId"));
                behavior.setType(type);
                Map<String, String> values = getBehaviorValuesByBehaviorId(behavior.getId());
                behavior.setValues(values);
                return behavior;
            }
        });
    }
    
    @Override
    @Transactional
    public int saveBehavior(Behavior behavior) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorId");
        sql.append("FROM Behavior");
        sql.append("WHERE BehaviorId").eq(behavior.getId());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            jdbcTemplate.queryForInt(sql);
            SqlParameterSink params = updateCreateSql.update("Behavior");
            initParameterSink(behavior, params);
            updateCreateSql.append("WHERE BehaviorId").eq(behavior.getId());
            deleteBehaviorValues(behavior.getId());
        } catch (EmptyResultDataAccessException e) {
            behavior.setId(nextValueHelper.getNextValue("Behavior"));
            SqlParameterSink params = updateCreateSql.insertInto("Behavior");
            initParameterSink(behavior, params);
        }

        jdbcTemplate.update(updateCreateSql);
        saveBehaviorValues(behavior.getId(), behavior.getValuesMap());

        return behavior.getId();
    }

    @Override
    @Transactional
    public void deleteBehavior(int behaviorId) {
        deleteBehaviorValues(behaviorId);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM Behavior");
        sql.append("WHERE BehaviorId").eq(behaviorId);

        jdbcTemplate.update(sql);
    }

    @Override
    public Behavior getBehaviorByDeviceIdAndType(int deviceId, BehaviorType type) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Behavior.BehaviorId, BehaviorType");
        sql.append("FROM Behavior");
        sql.append("JOIN DeviceBehaviorMap ON DeviceBehaviorMap.BehaviorId = Behavior.BehaviorId");
        sql.append("WHERE BehaviorType").eq_k(type);
        sql.append("AND DeviceId").eq(deviceId);

        Behavior behavior = jdbcTemplate.queryForObject(sql, new YukonRowMapper<Behavior>() {
            @Override
            public Behavior mapRow(YukonResultSet rs) throws SQLException {
                Behavior behavior = new Behavior();
                behavior.setId(rs.getInt("BehaviorId"));
                behavior.setType(rs.getEnum("BehaviorType", BehaviorType.class));
                return behavior;
            }
        });

        Map<String, String> values = getBehaviorValuesByBehaviorId(behavior.getId());
        behavior.setValues(values);
        return behavior;
    }
    
    @Override
    public Behavior getBehaviorById(int behaviorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorId, BehaviorType");
        sql.append("FROM Behavior");
        sql.append("WHERE BehaviorId").eq(behaviorId);

        Behavior behavior = jdbcTemplate.queryForObject(sql, new YukonRowMapper<Behavior>() {
            @Override
            public Behavior mapRow(YukonResultSet rs) throws SQLException {
                Behavior behavior = new Behavior();
                behavior.setId(behaviorId);
                behavior.setType(rs.getEnum("BehaviorType", BehaviorType.class));
                return behavior;
            }
        });

        Map<String, String> values = getBehaviorValuesByBehaviorId(behaviorId);
        behavior.setValues(values);
        return behavior;
    }
    
    //TODO: save only if newer?
    @Override
    @Transactional
    public int saveBehaviorReport(BehaviorReport report) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT BehaviorReportId");
        sql.append("FROM BehaviorReport");
        sql.append("WHERE deviceId").eq(report.getDeviceId());
        sql.append("AND BehaviorType").eq_k(report.getType());

        SqlStatementBuilder updateCreateSql = new SqlStatementBuilder();
        try {
            report.setId(jdbcTemplate.queryForInt(sql));
            SqlParameterSink params = updateCreateSql.update("BehaviorReport");
            initParameterSink(report, params);
            updateCreateSql.append("WHERE BehaviorReportId").eq(report.getId());
            deleteBehaviorReportValues(report.getId());
        } catch (EmptyResultDataAccessException e) {
            SqlParameterSink params = updateCreateSql.insertInto("BehaviorReport");
            report.setId(nextValueHelper.getNextValue("BehaviorReport"));
            initParameterSink(report, params);
        }
        jdbcTemplate.update(updateCreateSql);
        saveBehaviorReportValues(report.getId(), report.getValues());

        return report.getId();
    }
    
    private void initParameterSink(BehaviorReport report, SqlParameterSink params){
        params.addValue("BehaviorReportId", report.getId());
        params.addValue("DeviceId", report.getDeviceId());
        params.addValue("BehaviorType", report.getType());
        params.addValue("BehaviorStatus", report.getStatus());
        params.addValue("TimeStamp", report.getTimestamp());
    }
    
    private void initParameterSink(Behavior behavior, SqlParameterSink params){
        params.addValue("BehaviorId", behavior.getId());
        params.addValue("BehaviorType", behavior.getType());
    }
    
    private Map<String, String> getBehaviorValuesByBehaviorId(int behaviorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT Name, Value");
        sql.append("FROM BehaviorValue");
        sql.append("WHERE BehaviorId").eq(behaviorId);
        
        Map<String, String> nameValueMap = new HashMap<>();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                nameValueMap.put(rs.getString("Name"), rs.getString("Value"));
            }
        });
        return nameValueMap;
    }
    
    private void saveBehaviorValues(int behaviorId, Map<String, String> values) {
        values.forEach((name, value) -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("BehaviorValue");
            params.addValue("BehaviorId", behaviorId);
            params.addValue("Name", name);
            params.addValue("Value", value);

            jdbcTemplate.update(sql);
        });
    }

    private void deleteBehaviorValues(int behaviorId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM BehaviorValue");
        sql.append("WHERE BehaviorId").eq(behaviorId);

        jdbcTemplate.update(sql);
    }

    private void deleteBehaviorReportValues(int behaviorReportId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM BehaviorReportValue");
        sql.append("WHERE BehaviorReportId").eq(behaviorReportId);

        jdbcTemplate.update(sql);
    }

    private void saveBehaviorReportValues(int behaviorReportId, Map<String, String> values) {
        values.forEach((name, value) -> {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            SqlParameterSink params = sql.insertInto("BehaviorReportValue");
            params.addValue("BehaviorReportId", behaviorReportId);
            params.addValue("Name", name);
            params.addValue("Value", value);

            jdbcTemplate.update(sql);
        });
    }
    
    private void unassignBehaviorForBatch(BehaviorType type, List<Integer> deviceIds) {

        log.debug("Batch=" + deviceIds.size());
        // unassign devices
        SqlStatementBuilder unassignSql = new SqlStatementBuilder();
        unassignSql.append("DELETE dbm");
        unassignSql.append("FROM DeviceBehaviorMap dbm");
        unassignSql.append("JOIN Behavior b");
        unassignSql.append("ON dbm.BehaviorId=b.BehaviorId");
        unassignSql.append("WHERE b.BehaviorType").eq(type);
        unassignSql.append("AND dbm.deviceId").in(deviceIds);

        jdbcTemplate.update(unassignSql);
    }
}

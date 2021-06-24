package com.cannontech.dr.ecobee.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowCallbackHandler;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class EcobeeZeusGroupDaoImpl implements EcobeeZeusGroupDao {

    @Autowired YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<String> getZeusGroupIdsForLmGroup(int yukonGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM LMGroupZeusMapping");
        sql.append("WHERE YukonGroupId").eq(yukonGroupId);
        return jdbcTemplate.query(sql, TypeRowMapper.STRING);
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM ZeusGroupInventoryMapping");
        sql.append("WHERE InventoryID").eq(inventoryId);
        return jdbcTemplate.query(sql, TypeRowMapper.STRING);
    }

    @Override
    public String getZeusGroupId(int yukonGroupId, int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LGZM.EcobeeGroupId FROM LMGroupZeusMapping LGZM");
        sql.append("LEFT JOIN ZeusGroupInventoryMapping ZGIM");
        sql.append("ON LGZM.EcobeeGroupId = ZGIM.EcobeeGroupId");
        sql.append("WHERE LGZM.YukonGroupId").eq(yukonGroupId);
        sql.append("AND ZGIM.InventoryID").eq(inventoryId);
        return jdbcTemplate.queryForString(sql);
    }

    @Override
    public void mapGroupIdToZeusGroup(int yukonGroupId, String zeusGroupId, String zeusGroupName) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("LMGroupZeusMapping");
        sink.addValue("YukonGroupId", yukonGroupId);
        sink.addValue("EcobeeGroupId", zeusGroupId);
        sink.addValue("EcobeeGroupName", zeusGroupName);
        jdbcTemplate.update(sql);
    }

    @Override
    public void removeGroupIdForZeusGroupId(int yukonGroupId, String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMGroupZeusMapping");
        sql.append("WHERE YukonGroupId").eq(yukonGroupId);
        sql.append("AND EcobeeGroupId").eq(zeusGroupId);
        jdbcTemplate.update(sql.toString());
    }

    @Override
    public void mapInventoryToZeusGroupId(int inventoryId, String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("ZeusGroupInventoryMapping");
        sink.addValue("InventoryID", inventoryId);
        sink.addValue("EcobeeGroupId", zeusGroupId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteZeusGroupMappingForInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ZeusGroupInventoryMapping WHERE InventoryID").eq(inventoryId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void updateEventId(String eventId, String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE LMGroupZeusMapping");
        sql.append("SET EcobeeEventId").eq(eventId);
        sql.append("WHERE EcobeeGroupId").eq(zeusGroupId);
        jdbcTemplate.update(sql);
    }

    @Override
    public List<String> getEventIds(int yukonGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeEventId FROM LMGroupZeusMapping");
        sql.append("WHERE YukonGroupId").eq(yukonGroupId);
        return jdbcTemplate.query(sql, TypeRowMapper.STRING);
    }

    @Override
    public int getDeviceCount(String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM ZeusGroupInventoryMapping WHERE EcobeeGroupId").eq(zeusGroupId);
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<Integer> getInventoryIdsForZeusGroupID(String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID FROM ZeusGroupInventoryMapping");
        sql.append("WHERE EcobeeGroupId").eq(zeusGroupId);
        return jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }

    @Override
    public List<Integer> getInventoryIdsForYukonGroupID(String lmGroup) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID FROM ZeusGroupInventoryMapping a, LMGroupZeusMapping b");
        sql.append("WHERE a.EcobeeGroupId = b.EcobeeGroupId");
        sql.append("AND a.EcobeeGroupId").eq(lmGroup);
        return jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }

    
    @Override
    public String getZeusGroupName(String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT EcobeeGroupName FROM LMGroupZeusMapping WHERE EcobeeGroupId").eq(zeusGroupId);
        return jdbcTemplate.queryForString(sql);
    }
    
    @Override
    public int getGroupCount() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM LMGroupZeusMapping");
        return jdbcTemplate.queryForInt(sql);
    }
    
    @Override
    public int getAllThermostatCount() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM ZeusGroupInventoryMapping");
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public void removeEventId(String zeusEventId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("UPDATE LMGroupZeusMapping SET EcobeeEventId = '' WHERE EcobeeEventId").eq(zeusEventId);
        jdbcTemplate.update(sql);
    }

    @Override
    public List<String> getZeusGroupNames(int yukonGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT EcobeeGroupName FROM LMGroupZeusMapping");
        sql.append("WHERE YukonGroupId").eq(yukonGroupId);
        return jdbcTemplate.query(sql, TypeRowMapper.STRING);
    }

    @Override
    public List<Integer> getLmGroupsForInventory(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT LGZM.YukonGroupId FROM LMGroupZeusMapping LGZM");
        sql.append("JOIN ZeusGroupInventoryMapping ZGIM");
        sql.append("ON LGZM.EcobeeGroupId = ZGIM.EcobeeGroupId");
        sql.append("AND ZGIM.InventoryID").eq(inventoryId);
        return jdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }
    
    
    @Override
    public Multimap<String, String> getAllEcobeeGroupToSerialNumberMapping() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        Multimap<String, String> ecobeeGroupInYukonToInventory = ArrayListMultimap.create();
        sql.append("SELECT ManufacturerSerialNumber, EcobeeGroupId");
        sql.append("FROM ZeusGroupInventoryMapping mapping, LMHardwareBase hardwareBase");
        sql.append("WHERE mapping.InventoryID = hardwareBase.InventoryID");
        jdbcTemplate.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                ecobeeGroupInYukonToInventory.put(rs.getString("ManufacturerSerialNumber"), rs.getString("EcobeeGroupId"));
            }
        });
        return ecobeeGroupInYukonToInventory;
    }
    
    @Override
    public List<String> getGroupMapping(Set<Integer> lmGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM LMGroupZeusMapping");
        sql.append("WHERE YukonGroupId").in(lmGroupId);
        return jdbcTemplate.query(sql, TypeRowMapper.STRING);

    }
}
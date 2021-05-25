package com.cannontech.dr.ecobee.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;

public class EcobeeZeusGroupDaoImpl implements EcobeeZeusGroupDao {

    @Autowired YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<String> getZeusGroupIdsForLmGroup(int yukonGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM LMGroupZeusMapping WHERE YukonGroupId = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, yukonGroupId);
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM ZeusGroupInventoryMapping WHERE InventoryID = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, inventoryId);
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
        sql.append("DELETE FROM LMGroupZeusMapping WHERE YukonGroupId = ? AND EcobeeGroupId = ?");
        jdbcTemplate.update(sql.toString(), yukonGroupId, zeusGroupId);
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
        sql.append("SELECT EcobeeEventId FROM LMGroupZeusMapping WHERE YukonGroupId = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, yukonGroupId);
    }

    @Override
    public int getDeviceCount(String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM ZeusGroupInventoryMapping WHERE EcobeeGroupId").eq(zeusGroupId);
        return jdbcTemplate.queryForInt(sql);
    }

    @Override
    public List<Integer> getInventoryIdsForZeusGrouID(String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT InventoryID FROM ZeusGroupInventoryMapping WHERE EcobeeGroupId = ?");
        return jdbcTemplate.queryForList(sql.getSql(), Integer.class, zeusGroupId);
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
}
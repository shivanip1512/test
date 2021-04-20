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
    public List<String> getZeusGroupIdsForLmGroup(String yukonGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM LMGroupZeusMapping WHERE YukonGroupId = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, yukonGroupId);
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(String inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM ZeusGroupInventoryMapping WHERE InventoryID = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, inventoryId);
    }

    @Override
    public String getZeusGroupId(String yukonGroupId, String inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LGZM.EcobeeGroupId FROM LMGroupZeusMapping LGZM");
        sql.append("LEFT JOIN ZeusGroupInventoryMapping ZGIM");
        sql.append("ON LGZM.EcobeeGroupId = ZGIM.EcobeeGroupId");
        sql.append("WHERE LGZM.YukonGroupId").eq(yukonGroupId);
        sql.append("AND ZGIM.InventoryID").eq(inventoryId);
        return jdbcTemplate.queryForString(sql);
    }

    @Override
    public void mapGroupIdToZeusGroupId(String yukonGroupId, String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("LMGroupZeusMapping");
        sink.addValue("YukonGroupId", yukonGroupId);
        sink.addValue("EcobeeGroupId", zeusGroupId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void removeGroupIdForZeusGroupId(String yukonGroupId, String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMGroupZeusMapping WHERE YukonGroupId = ? AND EcobeeGroupId = ?");
        jdbcTemplate.update(sql.toString(), yukonGroupId, zeusGroupId);
    }

    @Override
    public void mapInventoryToZeusGroupId(String inventoryId, String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        SqlParameterSink sink = sql.insertInto("ZeusGroupInventoryMapping");
        sink.addValue("InventoryID", inventoryId);
        sink.addValue("EcobeeGroupId", zeusGroupId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void deleteInventoryToZeusGroupId(String inventoryId) {
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
    public List<String> getEventIds(String yukonGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeEventId FROM LMGroupZeusMapping WHERE YukonGroupId = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, yukonGroupId);
    }

    @Override
    public String getZeusGroupName(String yukonGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupName FROM LMGroupZeusMapping WHERE YukonGroupId = ?");
        return jdbcTemplate.queryForString(sql);
    }

    @Override
    public int getDeviceCount(String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM ZeusGroupInventoryMapping WHERE EcobeeGroupId = ?").eq(zeusGroupId);
        return jdbcTemplate.queryForInt(sql);
    }
}
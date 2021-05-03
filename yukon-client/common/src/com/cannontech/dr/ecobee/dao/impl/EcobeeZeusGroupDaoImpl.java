package com.cannontech.dr.ecobee.dao.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;

public class EcobeeZeusGroupDaoImpl implements EcobeeZeusGroupDao {

    @Autowired YukonJdbcTemplate jdbcTemplate;

    @Override
    public String getZeusGroupIdForLmGroup(int yukonGroupId) {
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT EcobeeGroupId FROM LMGroupZeusMapping WHERE YukonGroupId").eq(yukonGroupId);
            return jdbcTemplate.queryForString(sql);
        } catch (EmptyResultDataAccessException e) {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM ZeusGroupInventoryMapping WHERE InventoryID = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, inventoryId);
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
    public void removeGroupIdForZeusGroupId(String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM LMGroupZeusMapping WHERE EcobeeGroupId = ?");
        jdbcTemplate.update(sql.toString(), zeusGroupId);
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
}
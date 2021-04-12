package com.cannontech.dr.ecobee.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.dr.ecobee.dao.EcobeeZeusGroupDao;

public class EcobeeZeusGroupDaoImpl implements EcobeeZeusGroupDao {

    @Autowired YukonJdbcTemplate jdbcTemplate;

    @Override
    public List<String> getZeusGroupIdsForLmGroup(String lmGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM LMGroupZeusMapping WHERE DeviceID = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, lmGroupId);
    }

    @Override
    public List<String> getZeusGroupIdsForInventoryId(String inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeGroupId FROM ZeusGroupInventoryMapping WHERE InventoryID = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, inventoryId);
    }

    @Override
    public String getZeusGroupId(String lmGroupId, String inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT LGZM.EcobeeGroupId FROM LMGroupZeusMapping LGZM");
        sql.append("LEFT JOIN ZeusGroupInventoryMapping ZGIM");
        sql.append("ON LGZM.EcobeeGroupId = ZGIM.EcobeeGroupId");
        sql.append("WHERE LGZM.DeviceID").eq(lmGroupId);
        sql.append("AND ZGIM.InventoryID").eq(inventoryId);
        return jdbcTemplate.queryForString(sql);
    }

    @Override
    public void mapGroupIdToZeusGroupId(String lmGroupId, String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO LMGroupZeusMapping");
        sql.append("(DeviceID, EcobeeGroupId)");
        sql.values(lmGroupId, zeusGroupId);
        jdbcTemplate.update(sql);
    }

    @Override
    public void mapInventoryToZeusGroupId(String inventoryId, String zeusGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("INSERT INTO ZeusGroupInventoryMapping");
        sql.append("(InventoryID, EcobeeGroupId)");
        sql.values(inventoryId, zeusGroupId);
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
    public List<String> getEventIds(String lmGroupId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EcobeeEventId FROM LMGroupZeusMapping WHERE DeviceID = ?");
        return jdbcTemplate.queryForList(sql.getSql(), String.class, lmGroupId);
    }
}

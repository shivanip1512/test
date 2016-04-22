package com.cannontech.dr.ecobee.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.dr.ecobee.dao.EcobeeGroupDeviceMappingDao;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

public class EcobeeGroupDeviceMappingDaoImpl implements EcobeeGroupDeviceMappingDao {
    @Autowired YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public Multimap<Integer, String> getSerialNumbersByGroupId() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select AddressingGroupId, ManufacturerSerialNumber");
        sql.append("from LmHardwareConfiguration lmhc");
        sql.append("join LmHardwareBase lhb on lhb.InventoryId = lmhc.InventoryId");
        sql.append("join YukonPaObject ypo on ypo.PaObjectId = lmhc.AddressingGroupId");
        sql.append("where ypo.Type").eq_k(PaoType.LM_GROUP_ECOBEE);
        
        final Multimap<Integer, String> groupToDevicesMap = ArrayListMultimap.create();
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                groupToDevicesMap.put(rs.getInt("AddressingGroupId"), rs.getString("ManufacturerSerialNumber"));
            }
        });
        return groupToDevicesMap;
    }
    
    @Override
    public List<String> getAllEcobeeSerialNumbers() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ManufacturerSerialNumber");
        sql.append("from LmHardwareBase lmhb");
        sql.append("join InventoryBase ib on lmhb.InventoryId = ib.InventoryId");
        sql.append("join YukonPaObject ypo on ib.DeviceId = ypo.PaObjectId");
        sql.append("where ypo.Type").in(PaoType.getEcobeeTypes());
        
        return jdbcTemplate.query(sql, TypeRowMapper.STRING);
    }
}

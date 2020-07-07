package com.cannontech.common.device.dao.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.device.dao.DeviceBaseModelDao;
import com.cannontech.common.device.model.DeviceBaseModel;
import com.cannontech.common.device.virtualDevice.VirtualDeviceModel;
import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonRowMapper;

@Service
public class VirtualDeviceModelDaoImpl implements DeviceBaseModelDao<VirtualDeviceModel> {
    
    private YukonRowMapper<DeviceBaseModel> deviceBaseModelRowMapper = rs -> {
        Integer paoId = rs.getInt("PAObjectId");
        String deviceName = rs.getString("PaoName");
        Boolean enable = !rs.getBoolean("disableFlag"); 
        return new DeviceBaseModel(paoId, PaoType.VIRTUAL_SYSTEM, deviceName, enable);
    };
    
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public List<DeviceBaseModel> listDevices(SortBy sortColumn, Direction direction) {
         SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectId, type, paoName, disableFlag");
        sql.append("FROM YukonPAObject");
        sql.append("WHERE type = 'VIRTUAL SYSTEM'");
        
        String sortString;
        
        // Complexity of sorting requires us to setup the sort string
        if (sortColumn == SortBy.DISABLE_FLAG) {
            if (direction == Direction.asc) {
                sortString = "disableFlag ASC, paoName";
            } else {
                sortString = "disableFlag DESC, paoName";
            }
        } else {
            sortString = sortColumn.getDbString() + " " + direction.name();
        }
        
        // Set column ordering
        sql.append("ORDER BY " + sortString);
        List<DeviceBaseModel> virtualDeviceModels = jdbcTemplate.query(sql, deviceBaseModelRowMapper);
        return virtualDeviceModels;
    }

}

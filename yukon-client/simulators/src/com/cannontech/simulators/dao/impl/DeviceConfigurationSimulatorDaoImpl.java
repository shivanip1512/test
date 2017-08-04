package com.cannontech.simulators.dao.impl;

import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.simulators.RegulatorVoltageControlMode;
import com.cannontech.simulators.dao.DeviceConfigurationSimulatorDao;

public class DeviceConfigurationSimulatorDaoImpl implements DeviceConfigurationSimulatorDao {
    private static final Logger log = YukonLogManager.getLogger(DeviceConfigurationSimulatorDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public void getDeviceVoltageControlMode(Map<Integer, RegulatorVoltageControlMode> regulatorConfigMap) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DCDM.DeviceID AS RegulatorId, DCI.ItemValue AS ControlMode");
        sql.append("FROM DeviceConfigCategoryItem DCI");
        sql.append("    JOIN DeviceConfigCategoryMap DCM ON DCM.DeviceConfigCategoryId = DCI.DeviceConfigCategoryId");
        sql.append("    JOIN DeviceConfiguration DC ON DC.DeviceConfigurationId = DCM.DeviceConfigurationId");
        sql.append("    JOIN DeviceConfigCategory DCC ON DCM.DeviceConfigCategoryId = DCC.DeviceConfigCategoryId");
        sql.append(
            "    JOIN DeviceConfigurationDeviceMap DCDM ON DC.DeviceConfigurationId = DCDM.DeviceConfigurationId");
        sql.append("WHERE DCI.ItemName='voltageControlMode'");
        sql.append("    AND DCC.CategoryType ='regulatorCategory'");
        sql.append("    AND DCDM.DeviceID").in(regulatorConfigMap.keySet());

        try {
            jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
                @Override
                public void processRow(YukonResultSet resultSet) throws SQLException {
                    regulatorConfigMap.put(resultSet.getInt("RegulatorId"),
                        RegulatorVoltageControlMode.valueOf(resultSet.getString("ControlMode")));
                }
            });
        } catch (DataAccessException e) {
            log.error("Get device Voltage Control Modes failed ", e);
            throw new NotFoundException("Get device Voltage Control Modes failed ", e);
        }
    }
   
}

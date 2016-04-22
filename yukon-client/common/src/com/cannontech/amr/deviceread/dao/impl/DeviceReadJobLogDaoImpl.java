package com.cannontech.amr.deviceread.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.cannontech.amr.deviceread.dao.DeviceReadJobLogDao;
import com.cannontech.amr.deviceread.model.DeviceReadJobLog;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.db.pao.YukonPAObject;

public class DeviceReadJobLogDaoImpl implements DeviceReadJobLogDao {
  
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    private static final RowMapper<DeviceReadJobLog> deviceReadJobLogRowMapper = new RowMapper<DeviceReadJobLog>() {

		@Override
        public DeviceReadJobLog mapRow(ResultSet rs, int rowNum) throws SQLException {
			return DeviceReadJobLog.createDeviceReadJobLog(rs);
		}
    };
	
	@Override
    public List<DeviceReadJobLog> getAllSchedules() {
		try {
            String sql = "SELECT DeviceReadJobLogID, ScheduleID, PaoName, StartTime, StopTime " +
                         " FROM " + DeviceReadJobLog.TABLE_NAME + ", " + YukonPAObject.TABLE_NAME +
                         " WHERE PAOBJECTID = SCHEDULEID";
            
            List<DeviceReadJobLog> deviceReadJobLogs = jdbcTemplate.query(sql, deviceReadJobLogRowMapper);
            	
            return deviceReadJobLogs;
        } catch (IncorrectResultSizeDataAccessException e) {
            throw new NotFoundException("No results were found for getAllSchedules();");
        }
	}

	@Override
    public String getScheduleDisplayName(DeviceReadJobLog deviceReadJobLog) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		String startTime = dateFormat.format(deviceReadJobLog.getStartTime().getTime());
		String stopTime = dateFormat.format(deviceReadJobLog.getStopTime().getTime());
		return deviceReadJobLog.getScheduleName() + " (" + startTime + " - " + stopTime + ")";
	}
}

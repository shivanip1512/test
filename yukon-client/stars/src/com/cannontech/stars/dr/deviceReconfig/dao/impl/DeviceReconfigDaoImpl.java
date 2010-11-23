package com.cannontech.stars.dr.deviceReconfig.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.stars.dr.deviceReconfig.dao.DeviceReconfigDao;

public class DeviceReconfigDaoImpl implements DeviceReconfigDao {
	
	private YukonJdbcTemplate yukonJdbcTemplate;

	@Override
	public boolean nameInUse(String name) {
	    
	    return false;
	}
	
	@Autowired
	public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
	
}
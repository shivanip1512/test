package com.cannontech.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.dao.DeviceCustomerListDao;

public class DeviceCustomerListDaoImpl implements DeviceCustomerListDao {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    @Override
    public void deleteDeviceListForCustomer(int customerId) {
        String sql = "DELETE FROM DeviceCustomerList WHERE CustomerId = ?";
        simpleJdbcTemplate.update(sql, customerId);
    }
	
	@Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}

package com.cannontech.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.DeviceCustomerListDao;
import com.cannontech.database.YukonJdbcTemplate;

public class DeviceCustomerListDaoImpl implements DeviceCustomerListDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public void deleteDeviceListForCustomer(int customerId) {
        String sql = "DELETE FROM DeviceCustomerList WHERE CustomerId = ?";
        jdbcTemplate.update(sql, customerId);
    }
}

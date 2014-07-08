package com.cannontech.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.core.dao.GraphCustomerListDao;
import com.cannontech.database.YukonJdbcTemplate;

public class GraphCustomerListDaoImpl implements GraphCustomerListDao {

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public void deleteGraphsForCustomer(int customerId) {
        String sql = "DELETE FROM GraphCustomerList WHERE CustomerId = ?";
        jdbcTemplate.update(sql, customerId);
    }
}

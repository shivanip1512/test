package com.cannontech.core.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.core.dao.GraphCustomerListDao;

public class GraphCustomerListDaoImpl implements GraphCustomerListDao {

    private SimpleJdbcTemplate simpleJdbcTemplate;
    
    @Override
    public void deleteGraphsForCustomer(int customerId) {
        String sql = "DELETE FROM GraphCustomerList WHERE CustomerId = ?";
        simpleJdbcTemplate.update(sql, customerId);
    }
    
    @Autowired
    public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
    }

}

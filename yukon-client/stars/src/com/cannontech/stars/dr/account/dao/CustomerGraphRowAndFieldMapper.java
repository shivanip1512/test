package com.cannontech.stars.dr.account.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.stars.dr.account.model.CustomerGraph;

public class CustomerGraphRowAndFieldMapper implements RowAndFieldMapper<CustomerGraph> {

    public Number getPrimaryKey(CustomerGraph customerGraph) {
        return customerGraph.getGraphDefinitionId();
    }
    
    public void setPrimaryKey(CustomerGraph customerGraph, int value) {
    	customerGraph.setGraphDefinitionId(value);
    }
    
    public void extractValues(MapSqlParameterSource p, CustomerGraph customerGraph) {
        p.addValue("CustomerId", customerGraph.getCustomerId());
        p.addValue("CustomerOrder", customerGraph.getCustomerOrder());
    }
    
    public CustomerGraph mapRow(ResultSet rs, int rowNum) throws SQLException {
    	CustomerGraph customerGraph = new CustomerGraph();
    	customerGraph.setGraphDefinitionId(rs.getInt("GraphDefinitionId"));
    	customerGraph.setCustomerId(rs.getInt("CustomerId"));
    	customerGraph.setCustomerOrder(rs.getInt("CustomerOrder"));
        return customerGraph;
    }
}

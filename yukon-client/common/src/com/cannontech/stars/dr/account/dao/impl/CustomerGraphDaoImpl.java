package com.cannontech.stars.dr.account.dao.impl;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.account.dao.CustomerGraphDao;
import com.cannontech.stars.dr.account.dao.CustomerGraphRowAndFieldMapper;
import com.cannontech.stars.dr.account.model.CustomerGraph;

public class CustomerGraphDaoImpl implements CustomerGraphDao, InitializingBean {

	private static final RowAndFieldMapper<CustomerGraph> rowAndFieldMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<CustomerGraph> template;
    
    static {
        rowAndFieldMapper = new CustomerGraphRowAndFieldMapper();
    }
    
	@Override
	public List<CustomerGraph> getByCustomerId(int customerId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT gcl.*");
		sql.append("FROM GraphCustomerList gcl");
		sql.append("WHERE gcl.CustomerId").eq(customerId);
		
		return yukonJdbcTemplate.query(sql, rowAndFieldMapper);
	}
	
	@Override
	public void deleteAllCustomerGraphsByCustomerId(int customerId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("DELETE FROM GraphCustomerList");
		sql.append("WHERE CustomerId").eq(customerId);
		yukonJdbcTemplate.update(sql);
	}
	
	@Override
	public String getGraphName(int graphDefinitionId) {
		
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT gd.Name");
		sql.append("FROM GraphDefinition gd");
		sql.append("WHERE gd.GraphDefinitionId").eq(graphDefinitionId);
		
		return yukonJdbcTemplate.queryForString(sql);
	}
	
	@Override
    public void insert(CustomerGraph customerGraph) {
    	
    	if (customerGraph.getGraphDefinitionId() <= 0) {
    		throw new IllegalArgumentException("graphDefinitionId must be set");
    	}
    	
        template.insert(customerGraph);
    }

    @Override
    public void update(CustomerGraph customerGraph) {
    	
    	if (customerGraph.getGraphDefinitionId() <= 0) {
    		throw new IllegalArgumentException("graphDefinitionId must be set");
    	}
    	
    	template.update(customerGraph);
    }
	
	@Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<CustomerGraph>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("GraphCustomerList");
        template.setPrimaryKeyField("GraphDefinitionId");
        template.setFieldMapper(rowAndFieldMapper); 
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
}

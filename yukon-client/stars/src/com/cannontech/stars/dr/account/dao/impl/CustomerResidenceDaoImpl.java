package com.cannontech.stars.dr.account.dao.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.account.dao.CustomerResidenceDao;
import com.cannontech.stars.dr.account.dao.CustomerResidenceRowAndFieldMapper;
import com.cannontech.stars.dr.account.model.CustomerResidence;

public class CustomerResidenceDaoImpl implements CustomerResidenceDao, InitializingBean {

    private static final RowAndFieldMapper<CustomerResidence> rowAndFieldMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<CustomerResidence> template;
    
    static {
        rowAndFieldMapper = new CustomerResidenceRowAndFieldMapper();
    }
    
    @Override
    public void insert(CustomerResidence customerResidence) {
    	
    	if (customerResidence.getAccountSiteId() <= 0) {
    		throw new IllegalArgumentException("accountSiteId must be set");
    	}
    	
        template.insert(customerResidence);
    }

    @Override
    public void update(CustomerResidence customerResidence) {
    	
    	if (customerResidence.getAccountSiteId() <= 0) {
    		throw new IllegalArgumentException("accountSiteId must be set");
    	}
    	
    	template.update(customerResidence);
    }
    
    public CustomerResidence findByAccountSiteId(int accountSiteId) {
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT cr.* FROM CustomerResidence cr");
        sql.append("WHERE cr.AccountSiteId").eq(accountSiteId);
        
        try {
            return yukonJdbcTemplate.queryForObject(sql.getSql(), rowAndFieldMapper, sql.getArguments());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<CustomerResidence>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CustomerResidence");
        template.setPrimaryKeyField("AccountSiteId");
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

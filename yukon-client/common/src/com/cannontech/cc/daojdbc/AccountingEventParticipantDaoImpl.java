package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.AccountingEventParticipantDao;
import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class AccountingEventParticipantDaoImpl implements InitializingBean, AccountingEventParticipantDao {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<AccountingEventParticipant> template;
    private NextValueHelper nextValueHelper;
    private CustomerStubDao customerStubDao;
    private AccountingEventDao accountingEventDao;

    @Override
    public AccountingEventParticipant getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtAcctEventParticipant");
        sql.append("where CCurtAcctEventParticipantID").eq(id);
        
        AccountingEventParticipant result = yukonJdbcTemplate.queryForObject(sql, new AccountingEventParticipantRowMapper());
        return result;
    }

    @Override
    public List<AccountingEventParticipant> getForEvent(AccountingEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select * from CCurtAcctEventParticipant");
        sql.append("where CCurtAcctEventID").eq(event.getId());
        
        List<AccountingEventParticipant> result = yukonJdbcTemplate.query(sql, new AccountingEventParticipantRowMapper(event));
        return result;
    }

    @Override
    public void save(AccountingEventParticipant participant) {
        template.save(participant);
    }

    @Override
    public void deleteForEvent(AccountingEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtAcctEventParticipant");
        sql.append("where CCurtAcctEventID").eq(event.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    private FieldMapper<AccountingEventParticipant> accountingEventParticipantFieldMapper = new FieldMapper<AccountingEventParticipant>() {
        public void extractValues(MapSqlParameterSource p, AccountingEventParticipant participant) {
            p.addValue("CustomerID", participant.getCustomer().getId());
            p.addValue("CCurtAcctEventID", participant.getEvent().getId());
        }
        public Number getPrimaryKey(AccountingEventParticipant participant) {
            return participant.getId();
        }
        public void setPrimaryKey(AccountingEventParticipant participant, int value) {
            participant.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<AccountingEventParticipant>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtAcctEventParticipant");
        template.setPrimaryKeyField("CCurtAcctEventParticipantID");
        template.setFieldMapper(accountingEventParticipantFieldMapper); 
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setAccountingEventDao(AccountingEventDao accountingEventDao) {
        this.accountingEventDao = accountingEventDao;
    }
    
    @Autowired
    public void setCustomerStubDao(CustomerStubDao customerStubDao) {
        this.customerStubDao = customerStubDao;
    }

    private class AccountingEventParticipantRowMapper implements YukonRowMapper<AccountingEventParticipant> {
        private CachingDaoWrapper<AccountingEvent> cachingAccountingEventDao;
        private CachingDaoWrapper<CICustomerStub> cachingCustomerStubDao;
        
        public AccountingEventParticipantRowMapper(AccountingEvent... initialItems) {
            cachingAccountingEventDao = new CachingDaoWrapper<AccountingEvent>(accountingEventDao, initialItems);
            cachingCustomerStubDao = new CachingDaoWrapper<CICustomerStub>(customerStubDao);
        }
        
        public AccountingEventParticipant mapRow(YukonResultSet rs) throws SQLException {
            AccountingEventParticipant participant = new AccountingEventParticipant();
            participant.setId(rs.getInt("CCurtAcctEventParticipantID"));
            CICustomerStub customer = cachingCustomerStubDao.getForId(rs.getInt("CustomerID"));
            participant.setCustomer(customer);
            AccountingEvent event = cachingAccountingEventDao.getForId(rs.getInt("CCurtAcctEventID"));
            participant.setEvent(event);
            
            return participant;
        }
    }
}

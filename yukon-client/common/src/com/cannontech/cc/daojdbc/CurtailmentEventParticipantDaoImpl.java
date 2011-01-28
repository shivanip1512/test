package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class CurtailmentEventParticipantDaoImpl implements CurtailmentEventParticipantDao, InitializingBean {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<CurtailmentEventParticipant> template;
    private NextValueHelper nextValueHelper;
    private CustomerStubDao customerStubDao;
    private CurtailmentEventDao curtailmentEventDao;
    private CurtailmentEventNotifDao curtailmentEventNotifDao;
    
    
    @Override
    public CurtailmentEventParticipant getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtCEParticipant");
        sql.append("where CCurtCEParticipantID").eq(id);
        
        CurtailmentEventParticipant result = yukonJdbcTemplate.queryForObject(sql, new CurtailmentEventParticipantRowMapper());
        return result;
    }

    @Override
    public List<CurtailmentEventParticipant> getForEvent(CurtailmentEvent event) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtCEParticipant");
        sql.append("where CCurtCurtailmentEventID").eq(event.getId());
        
        List<CurtailmentEventParticipant> result = yukonJdbcTemplate.query(sql, new CurtailmentEventParticipantRowMapper(event));
        return result;
    }

    @Override
    public void save(CurtailmentEventParticipant participant) {
        template.save(participant);
    }

    @Override
    public void delete(CurtailmentEventParticipant participant) {
        curtailmentEventNotifDao.deleteForParticipant(participant);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtCEParticipant");
        sql.append("where CCurtCEParticipantID").eq(participant.getId());
        
        yukonJdbcTemplate.update(sql);
    }
   
    @Override
    public void deleteForEvent(CurtailmentEvent event) {
        curtailmentEventNotifDao.deleteForEvent(event);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtCEParticipant");
        sql.append("where CCurtCurtailmentEventID").eq(event.getId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private FieldMapper<CurtailmentEventParticipant> curtailmentEventParticipantFieldMapper = new FieldMapper<CurtailmentEventParticipant>() {
        public void extractValues(MapSqlParameterSource p, CurtailmentEventParticipant participant) {
            p.addValue("NotifAttribs", participant.getNotifAttribs());
            p.addValue("CustomerID", participant.getCustomer().getId());
            p.addValue("CCurtCurtailmentEventID", participant.getEvent().getId());
        }
        public Number getPrimaryKey(CurtailmentEventParticipant participant) {
            return participant.getId();
        }
        public void setPrimaryKey(CurtailmentEventParticipant participant, int value) {
            participant.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<CurtailmentEventParticipant>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtCEParticipant");
        template.setPrimaryKeyField("CCurtCEParticipantID");
        template.setFieldMapper(curtailmentEventParticipantFieldMapper); 
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
    public void setCurtailmentEventDao(CurtailmentEventDao curtailmentEventDao) {
        this.curtailmentEventDao = curtailmentEventDao;
    }
    
    @Autowired
    public void setCustomerStubDao(CustomerStubDao customerStubDao) {
        this.customerStubDao = customerStubDao;
    }
    
    public void setCurtailmentEventNotifDao(CurtailmentEventNotifDao curtailmentEventNotifDao) {
        this.curtailmentEventNotifDao = curtailmentEventNotifDao;
    }

    private class CurtailmentEventParticipantRowMapper implements YukonRowMapper<CurtailmentEventParticipant> {
        private CachingDaoWrapper<CICustomerStub> cachingCustomerStubDao;
        private CachingDaoWrapper<CurtailmentEvent> cachingCurtailmentEventDao;
        
        public CurtailmentEventParticipantRowMapper(CurtailmentEvent... initialItems) {
            cachingCurtailmentEventDao = new CachingDaoWrapper<CurtailmentEvent>(curtailmentEventDao, initialItems);
            cachingCustomerStubDao = new CachingDaoWrapper<CICustomerStub>(customerStubDao);
        }
        
        public CurtailmentEventParticipant mapRow(YukonResultSet rs) throws SQLException {
            CurtailmentEventParticipant curtailmentEventParticipant = new CurtailmentEventParticipant();
            curtailmentEventParticipant.setId(rs.getInt("CCurtCEParticipantID"));
            curtailmentEventParticipant.setNotifAttribs(rs.getString("NotifAttribs"));
            CICustomerStub customer = cachingCustomerStubDao.getForId(rs.getInt("CustomerID")); 
            curtailmentEventParticipant.setCustomer(customer);
            CurtailmentEvent event = cachingCurtailmentEventDao.getForId(rs.getInt("CCurtCurtailmentEventID"));
            curtailmentEventParticipant.setEvent(event);
            
            return curtailmentEventParticipant;
        }
    }
}

package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class GroupCustomerNotifDaoImpl implements GroupCustomerNotifDao, InitializingBean {
    
    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<GroupCustomerNotif> template;
    private NextValueHelper nextValueHelper;
    private CustomerStubDao customerStubDao;
    private GroupDao groupDao;

    @Override
    public List<GroupCustomerNotif> getAllForGroup(Group group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtGroupCustomerNotif");
        sql.append("where CCurtGroupID").eq(group.getId());
        
        List<GroupCustomerNotif> result = yukonJdbcTemplate.query(sql, new GroupCustomerNotifRowMapper());
        return result;
    }

    @Override
    public void save(GroupCustomerNotif notif) {
        template.save(notif);
    }

    @Override
    public void saveNotifsForGroup(Group group, List<GroupCustomerNotif> required) {
        List<GroupCustomerNotif> existing = getAllForGroup(group);
        for(GroupCustomerNotif notif : required) {
            save(notif);
            existing.remove(notif);
        }
        for(GroupCustomerNotif notif : existing) {
            delete(notif);
        }
    }

    @Override
    public void delete(GroupCustomerNotif notif) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtGroupCustomerNotif");
        sql.append("where CCurtGroupCustomerNotifID").eq(notif.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteFor(Group group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtGroupCustomerNotif");
        sql.append("where CCurtGroupID").eq(group.getId());
        
       yukonJdbcTemplate.update(sql);
    }
    
    @Override
    public void deleteFor(CICustomerStub customer) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtGroupCustomerNotif");
        sql.append("where CustomerID").eq(customer.getId());
        
       yukonJdbcTemplate.update(sql);
    }
    
    private FieldMapper<GroupCustomerNotif> groupCustomerNotifFieldMapper = new FieldMapper<GroupCustomerNotif>() {
        public void extractValues(MapSqlParameterSource p, GroupCustomerNotif notif) {
            p.addValue("Attribs", notif.getAttribs());
            p.addValue("CustomerID", notif.getCustomer().getId());
            p.addValue("CCurtGroupID", notif.getGroup().getId());
        }
        public Number getPrimaryKey(GroupCustomerNotif groupCustomerNotif) {
            return groupCustomerNotif.getId();
        }
        public void setPrimaryKey(GroupCustomerNotif groupCustomerNotif, int value) {
            groupCustomerNotif.setId(value);
        }
    };

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<GroupCustomerNotif>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtGroupCustomerNotif");
        template.setPrimaryKeyField("CCurtGroupCustomerNotifID");
        template.setFieldMapper(groupCustomerNotifFieldMapper); 
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
    public void setCustomerStubDao(CustomerStubDao customerStubDao) {
        this.customerStubDao = customerStubDao;
    }
    
    @Autowired
    public void setGroupDao(GroupDao groupDao) {
        this.groupDao = groupDao;
    }

    private class GroupCustomerNotifRowMapper implements YukonRowMapper<GroupCustomerNotif> {
        CachingDaoWrapper<CICustomerStub> cachingCustomerStubDao;
        
        public GroupCustomerNotifRowMapper(CICustomerStub... initialItems) {
            cachingCustomerStubDao = new CachingDaoWrapper<CICustomerStub>(customerStubDao, initialItems);
        }
        
        public GroupCustomerNotif mapRow(YukonResultSet rs) throws SQLException {
            GroupCustomerNotif notif = new GroupCustomerNotif();
            notif.setId(rs.getInt("CCurtGroupCustomerNotifID"));
            notif.setAttribs(rs.getString("Attribs"));
            CICustomerStub customer = cachingCustomerStubDao.getForId(rs.getInt("CustomerID"));
            notif.setCustomer(customer);
            Group group = groupDao.getForId(rs.getInt("CCurtGroupID"));
            notif.setGroup(group);
            
            return notif;
        }
    }
}

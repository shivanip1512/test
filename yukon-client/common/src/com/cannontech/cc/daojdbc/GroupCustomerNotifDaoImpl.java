package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.cc.dao.CustomerStubDao;
import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.common.util.CachingDaoWrapper;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class GroupCustomerNotifDaoImpl implements GroupCustomerNotifDao {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private CustomerStubDao customerStubDao;
    @Autowired private GroupDao groupDao;
    private ChunkingSqlTemplate chunkingSqlTemplate;
    private SimpleTableAccessTemplate<GroupCustomerNotif> template;
    private GroupCustomerNotifRowMapper rowMapper;
    
    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<GroupCustomerNotif>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtGroupCustomerNotif");
        template.setPrimaryKeyField("CCurtGroupCustomerNotifID");
        template.setFieldMapper(groupCustomerNotifFieldMapper);
        
        chunkingSqlTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
        
        rowMapper = new GroupCustomerNotifRowMapper();
    }
    
    @Override
    public List<GroupCustomerNotif> getByIds(Iterable<Integer> ids) {
        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT CCurtGroupCustomerNotifId, Attribs, CustomerId, CCurtGroupId");
                sql.append("FROM CCurtGroupCustomerNotif");
                sql.append("WHERE CCurtGroupCustomerNotifId").in(subList);
                return sql;
            }
        };
        
        List<GroupCustomerNotif> results = chunkingSqlTemplate.query(sqlGenerator, ids, rowMapper);
        return results;
    }
    
    @Override
    public List<GroupCustomerNotif> getAllForGroup(Group group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CCurtGroupCustomerNotifId, Attribs, CustomerId, CCurtGroupId");
        sql.append("FROM CCurtGroupCustomerNotif");
        sql.append("WHERE CCurtGroupID").eq(group.getId());
        
        List<GroupCustomerNotif> result = yukonJdbcTemplate.query(sql, rowMapper);
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
        sql.append("DELETE");
        sql.append("FROM CCurtGroupCustomerNotif");
        sql.append("WHERE CCurtGroupCustomerNotifID").eq(notif.getId());
        
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void deleteFor(Group group) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE");
        sql.append("FROM CCurtGroupCustomerNotif");
        sql.append("WHERE CCurtGroupID").eq(group.getId());
        
       yukonJdbcTemplate.update(sql);
    }

    private FieldMapper<GroupCustomerNotif> groupCustomerNotifFieldMapper = new FieldMapper<GroupCustomerNotif>() {
        @Override
        public void extractValues(MapSqlParameterSource p, GroupCustomerNotif notif) {
            p.addValue("Attribs", notif.getAttribs());
            p.addValue("CustomerID", notif.getCustomer().getId());
            p.addValue("CCurtGroupID", notif.getGroup().getId());
        }
        @Override
        public Number getPrimaryKey(GroupCustomerNotif groupCustomerNotif) {
            return groupCustomerNotif.getId();
        }
        @Override
        public void setPrimaryKey(GroupCustomerNotif groupCustomerNotif, int value) {
            groupCustomerNotif.setId(value);
        }
    };

    private class GroupCustomerNotifRowMapper implements YukonRowMapper<GroupCustomerNotif> {
        CachingDaoWrapper<CICustomerStub> cachingCustomerStubDao;
        
        public GroupCustomerNotifRowMapper(CICustomerStub... initialItems) {
            cachingCustomerStubDao = new CachingDaoWrapper<CICustomerStub>(customerStubDao, initialItems);
        }
        
        @Override
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

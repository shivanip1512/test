package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.AvailableProgramGroupDao;
import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.Group;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;

public class GroupDaoImpl implements GroupDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private AvailableProgramGroupDao programGroupDao;
    @Autowired private GroupCustomerNotifDao groupCustomerNotifDao;
    private SimpleTableAccessTemplate<Group> template;
    
    @PostConstruct
    public void postInit() throws Exception {
        template = new SimpleTableAccessTemplate<Group>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtGroup");
        template.setPrimaryKeyField("CCurtGroupID");
        template.setFieldMapper(groupFieldMapper); 
    }
    
    @Override
    public List<Group> getForIds(Iterable<Integer> ids) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CCurtGroupId, CCurtGroupName, EnergyCompanyId");
        sql.append("FROM CCurtGroup");
        sql.append("WHERE CCurtGroupId").in(ids);
        
        List<Group> groups = yukonJdbcTemplate.query(sql, rowMapper);
        return groups;
    }
    
    @Override
    public Group getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CCurtGroupId, CCurtGroupName, EnergyCompanyId");
        sql.append("FROM CCurtGroup");
        sql.append("WHERE CCurtGroupID").eq(id);
        
        Group result = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return result;
    }

    @Override
    public List<Group> getGroupsForEnergyCompany(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT CCurtGroupId, CCurtGroupName, EnergyCompanyId");
        sql.append("FROM CCurtGroup");
        sql.append("WHERE EnergyCompanyID").eq(energyCompanyId);
        
        List<Group> result = yukonJdbcTemplate.query(sql, rowMapper);
        return result;
    }

    @Override
    public void save(Group group) {
        template.save(group);
    }

    @Override
    @Transactional
    public void delete(Group group) {
        programGroupDao.deleteFor(group);
        groupCustomerNotifDao.deleteFor(group);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM CCurtGroup");
        sql.append("WHERE CCurtGroupID").eq(group.getId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private FieldMapper<Group> groupFieldMapper = new FieldMapper<Group>() {
        @Override
        public void extractValues(MapSqlParameterSource p, Group group) {
            p.addValue("EnergyCompanyID", group.getEnergyCompanyId());
            p.addValue("CCurtGroupName", group.getName());
        }
        @Override
        public Number getPrimaryKey(Group group) {
            return group.getId();
        }
        @Override
        public void setPrimaryKey(Group group, int value) {
            group.setId(value);
        }
    };

    private YukonRowMapper<Group> rowMapper = new YukonRowMapper<Group>() {
        @Override
        public Group mapRow(YukonResultSet rs) throws SQLException {
            Group group = new Group();
            group.setId(rs.getInt("CCurtGroupID"));
            group.setName(rs.getString("CCurtGroupName"));
            group.setEnergyCompanyId(rs.getInt("EnergyCompanyID"));
            
            return group;
        }
    };
}

package com.cannontech.cc.daojdbc;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Propagation;
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

public class GroupDaoImpl implements GroupDao, InitializingBean {

    private YukonJdbcTemplate yukonJdbcTemplate;
    private SimpleTableAccessTemplate<Group> template;
    private NextValueHelper nextValueHelper;
    private AvailableProgramGroupDao programGroupDao;
    private GroupCustomerNotifDao groupCustomerNotifDao;

    @Override
    public Group getForId(Integer id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtGroup");
        sql.append("where CCurtGroupID").eq(id);
        
        Group result = yukonJdbcTemplate.queryForObject(sql, rowMapper);
        return result;
    }

    @Override
    public List<Group> getGroupsForEnergyCompany(int energyCompanyId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append("from CCurtGroup");
        sql.append("where EnergyCompanyID").eq(energyCompanyId);
        
        List<Group> result = yukonJdbcTemplate.query(sql, rowMapper);
        return result;
    }

    @Override
    public void save(Group group) {
        template.save(group);
    }

    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public void delete(Group group) {
        programGroupDao.deleteFor(group);
        groupCustomerNotifDao.deleteFor(group);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete");
        sql.append("from CCurtGroup");
        sql.append("where CCurtGroupID").eq(group.getId());
        
        yukonJdbcTemplate.update(sql);
    }
    
    private FieldMapper<Group> groupFieldMapper = new FieldMapper<Group>() {
        public void extractValues(MapSqlParameterSource p, Group group) {
            p.addValue("EnergyCompanyID", group.getEnergyCompanyId());
            p.addValue("CCurtGroupName", group.getName());
        }
        public Number getPrimaryKey(Group group) {
            return group.getId();
        }
        public void setPrimaryKey(Group group, int value) {
            group.setId(value);
        }
    };
    
    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<Group>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("CCurtGroup");
        template.setPrimaryKeyField("CCurtGroupID");
        template.setFieldMapper(groupFieldMapper); 
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
    
    public void setprogramGroupDao(AvailableProgramGroupDao programGroupDao) {
        this.programGroupDao = programGroupDao;
    }
    
    public void setGroupCustomerNotifDao(GroupCustomerNotifDao groupCustomerNotifDao) {
        this.groupCustomerNotifDao = groupCustomerNotifDao;
    }

    private YukonRowMapper<Group> rowMapper = new YukonRowMapper<Group>() {
        public Group mapRow(YukonResultSet rs) throws SQLException {
            Group group = new Group();
            group.setId(rs.getInt("CCurtGroupID"));
            group.setName(rs.getString("CCurtGroupName"));
            group.setEnergyCompanyId(rs.getInt("EnergyCompanyID"));
            
            return group;
        }
    };
}

package com.cannontech.common.device.groups.composed.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedGroupDao;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class DeviceGroupComposedGroupDaoImpl implements DeviceGroupComposedGroupDao {

    private DeviceGroupComposedGroupRowAndFieldMapper rowAndFieldMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<DeviceGroupComposedGroup> template;
    
    @Override
    public void saveOrUpdate(DeviceGroupComposedGroup deviceGroupComposedGroup) {
        template.save(deviceGroupComposedGroup);
    }
        
    @Override
    public List<DeviceGroupComposedGroup> getComposedGroupsForId(int deviceGroupComposedId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DGCG.* FROM DeviceGroupComposedGroup DGCG");
        sql.append("WHERE DGCG.DeviceGroupComposedId = ").appendArgument(deviceGroupComposedId);
        
        return yukonJdbcTemplate.query(sql.getSql(), rowAndFieldMapper, sql.getArguments());
    }
    
    @Override
    public void removeAllGroups(int deviceGroupComposedId) {
     
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM DeviceGroupComposedGroup");
        sql.append("WHERE DeviceGroupComposedId = ").appendArgument(deviceGroupComposedId);
        
        yukonJdbcTemplate.update(sql.getSql(), sql.getArguments());
    }

    @Override
    public List<DeviceGroupComposedGroup> getByGroupNames(List<String> groupNames) {
		SqlStatementBuilder sql = new SqlStatementBuilder();
		sql.append("SELECT DGCG.* FROM DeviceGroupComposedGroup DGCG");
		sql.append("WHERE groupName").in(groupNames);
		return yukonJdbcTemplate.query(sql, rowAndFieldMapper);
    }
    
    @PostConstruct
    public void init() throws Exception {
        template = new SimpleTableAccessTemplate<DeviceGroupComposedGroup>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("DeviceGroupComposedGroup");
        template.setPrimaryKeyField("DeviceGroupComposedGroupId");
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
    
    @Autowired
    public void setRowAndFieldMapper(DeviceGroupComposedGroupRowAndFieldMapper rowAndFieldMapper) {
        this.rowAndFieldMapper = rowAndFieldMapper;
    }
}

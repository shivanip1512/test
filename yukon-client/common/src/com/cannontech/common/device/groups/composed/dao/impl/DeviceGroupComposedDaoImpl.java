package com.cannontech.common.device.groups.composed.dao.impl;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.device.groups.composed.dao.DeviceGroupComposedDao;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.RowAndFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;

public class DeviceGroupComposedDaoImpl implements DeviceGroupComposedDao, InitializingBean {

    private static final RowAndFieldMapper<DeviceGroupComposed> rowAndFieldMapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private NextValueHelper nextValueHelper;
    private SimpleTableAccessTemplate<DeviceGroupComposed> template;
    
    static {
        rowAndFieldMapper = new DeviceGroupComposedRowAndFieldMapper();
    }
    
    @Override
    public void saveOrUpdate(DeviceGroupComposed deviceGroupComposed) {
        template.save(deviceGroupComposed);
    }
    
    @Override
    public DeviceGroupComposed findForDeviceGroupId(int deviceGroupId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DGC.* FROM DeviceGroupComposed DGC");
        sql.append("WHERE DGC.DeviceGroupId = ").appendArgument(deviceGroupId);
        
        try {
            return yukonJdbcTemplate.queryForObject(sql.getSql(), rowAndFieldMapper, sql.getArguments());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        template = new SimpleTableAccessTemplate<DeviceGroupComposed>(yukonJdbcTemplate, nextValueHelper);
        template.setTableName("DeviceGroupComposed");
        template.setPrimaryKeyField("DeviceGroupComposedId");
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

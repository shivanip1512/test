package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlStatementBuilder;

public class StaticDeviceGroupProvider extends DeviceGroupProviderBase {
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private SimpleJdbcOperations jdbcTemplate;

    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<YukonDevice> childDevices = deviceGroupMemberEditorDao.getChildDevices(sdg);
        return Collections.unmodifiableList(childDevices);
    }

    @Override
    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<StoredDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(sdg);
        return childGroups;
    }
    
    @Override
    public String getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        String whereString = identifier + " IN ( " +
                            " SELECT DISTINCT YUKONPAOID FROM DEVICEGROUPMEMBER " + 
                            " WHERE DEVICEGROUPID = " + 
                            sdg.getId() + ") ";
        return whereString;
    }
    
    public void removeGroupDependancies(DeviceGroup group) {
        String sql = "DELETE FROM DeviceGroupMember where DeviceGroupId = ?";
        jdbcTemplate.update(sql, getStoredGroup(group).getId());
    }

    private StoredDeviceGroup getStoredGroup(DeviceGroup group) {
        Validate.isTrue(group instanceof StoredDeviceGroup, "Group must be static at this point");
        StoredDeviceGroup sdg = (StoredDeviceGroup) group;
        return sdg;
    }
    
    public Set<? extends DeviceGroup> getGroups(DeviceGroup base,
            YukonDevice device) {

        Set<DeviceGroup> resultSet = new HashSet<DeviceGroup>();
        if(getMainDelegator().isDeviceInGroup(base, device)){
            resultSet.add(base);
        }
        List<? extends DeviceGroup> childGroups = getChildGroups(base);
        for (DeviceGroup group : childGroups) {
            Set<? extends DeviceGroup> tempGroups = getMainDelegator().getGroups(group,
                                                                                device);
            resultSet.addAll(tempGroups);
        }

        return resultSet;
    }
    
    public DeviceGroup getRootGroup() {
        StoredDeviceGroup rootGroup = deviceGroupEditorDao.getRootGroup();
        return rootGroup;
    }
    
    public boolean isDeviceInGroup(DeviceGroup deviceGroup, YukonDevice device) {
            StoredDeviceGroup storedGroup = (StoredDeviceGroup) deviceGroup;
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("select count(*)");
            sql.append("from DeviceGroupMember dgm");
            sql.append("where dgm.DeviceGroupID = ? AND");
            sql.append("dgm.YukonPaoId = ?");
            int count = jdbcTemplate.queryForInt(sql.toString(),
                                           storedGroup.getId(),
                                           device.getDeviceId());
            return (count > 0);
    }
    
    @Required
    public void setJdbcTemplate(SimpleJdbcOperations jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Required
    public void setDeviceGroupEditorDao(DeviceGroupEditorDao deviceGroupEditorDao) {
        this.deviceGroupEditorDao = deviceGroupEditorDao;
    }
    
    @Required
    public void setDeviceGroupMemberEditorDao(DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }

}

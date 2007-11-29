package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
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
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<? extends DeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(sdg);
        
        List<DeviceGroup> result = Collections.unmodifiableList(childGroups);
        return result;
    }
    
    @Override
    public List<DeviceGroup> getGroups(DeviceGroup group) {
        List<DeviceGroup> result = new ArrayList<DeviceGroup>();
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<StoredDeviceGroup> staticGroups = deviceGroupEditorDao.getStaticGroups(sdg);
        result.addAll(staticGroups);

        // now get the non static ones
        List<StoredDeviceGroup> nonStaticGroups = deviceGroupEditorDao.getNonStaticGroups(sdg);
        for (StoredDeviceGroup nonStaticGroup : nonStaticGroups) {
            List<DeviceGroup> tempGroups = getMainDelegator().getGroups(nonStaticGroup);
            result.addAll(tempGroups);
        }
        
        return Collections.unmodifiableList(result);
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
    
    private StoredDeviceGroup getStoredGroup(DeviceGroup group) {
        Validate.isTrue(group instanceof StoredDeviceGroup, "Group must be static at this point");
        StoredDeviceGroup sdg = (StoredDeviceGroup) group;
        return sdg;
    }
    
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, YukonDevice device) {
        StoredDeviceGroup storedGroup = getStoredGroup(base);

        Set<StoredDeviceGroup> groups = deviceGroupMemberEditorDao.getGroupMembership(storedGroup, device);
        
        // create a new set to get around generics weirdness
        Set<DeviceGroup> result = new HashSet<DeviceGroup>(groups);

        List<StoredDeviceGroup> nonStaticGroups =
            deviceGroupEditorDao.getNonStaticGroups(storedGroup);
        for (StoredDeviceGroup nonStaticGroup : nonStaticGroups) {
            Set<? extends DeviceGroup> tempGroups =
                getMainDelegator().getGroupMembership(nonStaticGroup, device);
            result.addAll(tempGroups);
        }

        return result;
    }
    
    public DeviceGroup getRootGroup() {
        StoredDeviceGroup rootGroup = deviceGroupEditorDao.getRootGroup();
        return rootGroup;
    }
    
    public boolean isDeviceInGroup(DeviceGroup deviceGroup, YukonDevice device) {
            StoredDeviceGroup storedGroup = getStoredGroup(deviceGroup);
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

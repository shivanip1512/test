package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SqlStatementBuilder;

public class StaticDeviceGroupProvider extends DeviceGroupProviderSqlBase {
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Override
    public Set<YukonDevice> getChildDevices(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<YukonDevice> childDevices = deviceGroupMemberEditorDao.getChildDevices(sdg);
        return Collections.unmodifiableSet(new HashSet<YukonDevice>(childDevices));
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
        result.addAll(nonStaticGroups);
        for (StoredDeviceGroup nonStaticGroup : nonStaticGroups) {
            List<DeviceGroup> tempGroups = getMainDelegator().getGroups(nonStaticGroup);
            result.addAll(tempGroups);
        }
        
        // because the base implementation of this method returns everything
        // in a pretty nice order, we should try to do the same
        Collections.sort(result);
        
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
    
    @Override
    public String getDeviceGroupSqlWhereClause(DeviceGroup group,
            String identifier) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        // find all static groups
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<StoredDeviceGroup> staticGroups = deviceGroupEditorDao.getStaticGroups(sdg);
        
        // add in this group to ensure that its direct children are included
        staticGroups.add(sdg); 

        // build one SQL to handle
        List<Integer> idList = new MappingList<StoredDeviceGroup, Integer>(staticGroups, new ObjectMapper<StoredDeviceGroup, Integer>() {
            public Integer map(StoredDeviceGroup from) {
                return from.getId();
            }
        });
        
        sql.append(identifier, "in (select yukonpaoid from devicegroupmember where devicegroupid in (", idList, "))");
        
        // now handle the dynamic ones by delegating back to main
        List<StoredDeviceGroup> nonStaticGroups = deviceGroupEditorDao.getNonStaticGroups(sdg);
        for (StoredDeviceGroup nonStaticGroup : nonStaticGroups) {
            String whereFragment = getMainDelegator().getDeviceGroupSqlWhereClause(nonStaticGroup, identifier);
            sql.append("OR", whereFragment);
        }

        return sql.toString();
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
    
    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        StoredDeviceGroup storedGroup = getStoredGroup(group);
        boolean result = deviceGroupMemberEditorDao.isChildDevice(storedGroup,device);
        return result;
    }
    
    @Override
    public DeviceGroup getGroup(DeviceGroup base, String groupName) {
        return deviceGroupEditorDao.getGroupByName(getStoredGroup(base), groupName);
    }
    
    public DeviceGroup getRootGroup() {
        StoredDeviceGroup rootGroup = deviceGroupEditorDao.getRootGroup();
        return rootGroup;
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

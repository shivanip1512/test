package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.collect.Lists;

public class StaticDeviceGroupProvider extends DeviceGroupProviderSqlBase {
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    
    @Override
    public Set<SimpleDevice> getChildDevices(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<SimpleDevice> childDevices = deviceGroupMemberEditorDao.getChildDevices(sdg);
        return Collections.unmodifiableSet(new HashSet<SimpleDevice>(childDevices));
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
        List<StoredDeviceGroup> allGroups = deviceGroupEditorDao.getAllGroups(sdg);
        
        // add in this group to ensure that its direct children are included
        allGroups.add(sdg); 
        
        for (StoredDeviceGroup deviceGroup : allGroups) {
            result.add(deviceGroup);
            if (deviceGroup.getType() != DeviceGroupType.STATIC) {
              List<DeviceGroup> tempGroups = getMainDelegator().getGroups(deviceGroup);
              result.addAll(tempGroups);
            }
        }

        // because the base implementation of this method returns everything
        // in a pretty nice order, we should try to do the same
        Collections.sort(result);
        
        return result;
    }
    
    @Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier, " IN ( ");
        sql.append("SELECT DISTINCT YUKONPAOID FROM DEVICEGROUPMEMBER ");
        sql.append("WHERE DEVICEGROUPID = ").appendArgument(sdg.getId());
        sql.append(") ");
        return sql;
    }
    
    @Override
    public SqlFragmentSource getDeviceGroupSqlWhereClause(DeviceGroup group,
            String identifier) {
        
        if (doesGroupDefinitelyContainAllDevices(group)) {
            return new SimpleSqlFragment("1 = 1");
        }
        
        SqlFragmentCollection sqlCollection = SqlFragmentCollection.newOrCollection();
        
        // find all static groups
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<StoredDeviceGroup> allGroups = deviceGroupEditorDao.getAllGroups(sdg);
        
        // add in this group to ensure that its direct children are included
        allGroups.add(sdg); 
        
        List<Integer> idList = Lists.newArrayListWithExpectedSize(allGroups.size()); // good enough guess
        
        for (StoredDeviceGroup deviceGroup : allGroups) {
            if (deviceGroup.getType() == DeviceGroupType.STATIC) {
                // collect its id
                idList.add(deviceGroup.getId());
            } else {
                if (getMainDelegator().doesGroupDefinitelyContainAllDevices(deviceGroup)) {
                    return new SimpleSqlFragment("1 = 1");
                }
                SqlFragmentSource whereFragment = getMainDelegator().getDeviceGroupSqlWhereClause(deviceGroup, identifier);
                sqlCollection.add(whereFragment);
            }
        }

        SqlStatementBuilder staticSqlClause = new SqlStatementBuilder();
        staticSqlClause.append(identifier, "in (select yukonpaoid from devicegroupmember where devicegroupid in (", idList, "))");
        sqlCollection.add(staticSqlClause);
        
        return sqlCollection;
    }
    
    @Override
    public boolean doesGroupDefinitelyContainAllDevices(DeviceGroup group) {
        // the root group can be assumed to contain everything
        return group.getParent() == null;
    }
    
    private StoredDeviceGroup getStoredGroup(DeviceGroup group) {
        Validate.isTrue(group instanceof StoredDeviceGroup, "Group must be static at this point");
        StoredDeviceGroup sdg = (StoredDeviceGroup) group;
        return sdg;
    }
    
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base, SimpleDevice device) {
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

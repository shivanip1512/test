package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.MappingSet;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.predicate.Predicate;
import com.cannontech.core.dao.NotFoundException;

/**
 * Provides a base implementation of DeviceGroupProvider. For the most part, each of 
 * the "child" methods is abstract. This class then implements the recursive part (e.g. 
 * getDevices() is implemented on top of getChildGroups() and getChildDevices()). One
 * important aspect of this class is that whenever it calls getChildGroups(), it uses
 * the mainDelegator process the result. This is because classes that extends this class
 * will be written for a specific DeviceGroupType, but they may contain sub groups
 * of another type. For this reason, the mainDelegator must be used to ensure that 
 * each group is processed by the correct provider.
 */
public abstract class DeviceGroupProviderBase implements DeviceGroupProvider {
    
    private DeviceGroupProviderDao mainDelegator;
    
    public abstract Set<SimpleDevice> getChildDevices(DeviceGroup group);
    
    public abstract void collectChildDevices(DeviceGroup group, Set<SimpleDevice> deviceSet, int MaxSize);
    
    public abstract SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier);

    public abstract List<DeviceGroup> getChildGroups(DeviceGroup group);
    
    public abstract boolean isChildDevice(DeviceGroup group, YukonDevice device);
    
    @Override
    public Set<DeviceGroup> getGroupMembership(DeviceGroup base,
            YukonDevice device) {
        Set<DeviceGroup> result = new HashSet<DeviceGroup>();
        
        // is device a direct child of base
        if (isChildDevice(base, device)) {
            result.add(base);
        }
        
        // check child groups
        List<? extends DeviceGroup> childGroups = getChildGroups(base);
        for (DeviceGroup childGroup : childGroups) {
             Set<DeviceGroup> childGroupMembership = mainDelegator.getGroupMembership(childGroup, device);
             result.addAll(childGroupMembership);
        }
        
        return result;
    }
    
    public int getChildDeviceCount(DeviceGroup group) {
        return getChildDevices(group).size();
    }
    
    @Override
    public boolean doesGroupDefinitelyContainAllDevices(DeviceGroup group) {
        return false;
    }
    
    public SqlFragmentSource getDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        SqlFragmentCollection whereClause = SqlFragmentCollection.newOrCollection();
        whereClause.add(getChildDeviceGroupSqlWhereClause(group, identifier));
        
        List<? extends DeviceGroup> childGroups = getChildGroups(group);
        for (DeviceGroup childGroup : childGroups) {
            whereClause.add(mainDelegator.getDeviceGroupSqlWhereClause(childGroup, identifier));
        }
        return whereClause;
    }
    
    public DeviceGroup getGroup(DeviceGroup base, String groupName) {
        List<? extends DeviceGroup> childGroups = getChildGroups(base);
        for (DeviceGroup group : childGroups) {
            if (group.getName().equalsIgnoreCase(groupName)) {
                return group;
            }
        }
        throw new NotFoundException("Group " + groupName + " wasn't found under " + base);
    }
    
    public int getDeviceCount(DeviceGroup group) {
        // correct, but slow
        return getDevices(group).size();
    }

    public Set<Integer> getDeviceIds(DeviceGroup group) {
        Set<SimpleDevice> devices = getDevices(group);
        Set<Integer> idList = new MappingSet<SimpleDevice, Integer>(devices, new ObjectMapper<SimpleDevice, Integer>() {
            public Integer map(SimpleDevice from) {
                return from.getDeviceId();
            }
        });
        return idList;
    }

    public Set<SimpleDevice> getDevices(DeviceGroup group) {
        
        Set<SimpleDevice> deviceSet = new HashSet<SimpleDevice>();
        collectDevices(group, deviceSet, Integer.MAX_VALUE);
        return deviceSet;
    }
    
    public void collectDevices(DeviceGroup group, Set<SimpleDevice> deviceSet, int maxSize) {
        
        // Get child devices
        collectChildDevices(group, deviceSet, maxSize);
        
        if (deviceSet.size() < maxSize) {
            
            // Get child group's devices
            List<? extends DeviceGroup> childGroups = getChildGroups(group);
            for (DeviceGroup childGroup : childGroups) {
                
                mainDelegator.collectDevices(childGroup, deviceSet, maxSize);

                if (deviceSet.size() >= maxSize) {
                    break;
                }
            }
        }
    }
    
    public List<DeviceGroup> getGroups(DeviceGroup group) {
        List<DeviceGroup> result = new ArrayList<DeviceGroup>(20);
        List<DeviceGroup> groups = getChildGroups(group);
        result.addAll(groups);
        for (DeviceGroup childGroup : groups) {
            List<DeviceGroup> grandChildren = mainDelegator.getGroups(childGroup);
            result.addAll(grandChildren);
        }
        return Collections.unmodifiableList(result);
    }
    
    @Override
    public boolean isDeviceInGroup(DeviceGroup base, YukonDevice device) {
        // is device a direct child of base
        if (isChildDevice(base, device)) {
            return true;
        }
        
        // check child groups
        List<? extends DeviceGroup> childGroups = getChildGroups(base);
        for (DeviceGroup childGroup : childGroups) {
             if (mainDelegator.isDeviceInGroup(childGroup, device)) {
                 return true;
             }
        }
        
        return false;
    }
    
    @Override
    public final boolean isGroupCanMoveUnderGroup(DeviceGroup groupToMove, DeviceGroup proposedParent) {
        
        return getGroupCanMovePredicate(groupToMove).evaluate(proposedParent);
    }
    
    @Override
    public Predicate<DeviceGroup> getGroupCanMovePredicate(final DeviceGroup groupToMove) {
        
        Predicate<DeviceGroup> canMoveUnderPredicate = new Predicate<DeviceGroup>(){
            @Override
            public boolean evaluate(DeviceGroup newParentGroup) {
                
                if (!newParentGroup.isModifiable()
                    || newParentGroup.isEqualToOrDescendantOf(groupToMove)) {
                    return false;
                }
                
                return true;
            }
        };
        
        return canMoveUnderPredicate;
    }
    
    @Required
    public void setMainDelegator(DeviceGroupProviderDao mainDelegator) {
        this.mainDelegator = mainDelegator;
    }
    
	public DeviceGroupProviderDao getMainDelegator() {
		return mainDelegator;
	}
}

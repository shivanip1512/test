package com.cannontech.common.device.groups.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.authorization.service.PaoCommandAuthorizationService;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.google.common.base.Function;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class DeviceGroupServiceImpl implements DeviceGroupService {
    
    @Autowired private DeviceGroupProviderDao deviceGroupDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private CommandDao commandDao;
    @Autowired private PaoCommandAuthorizationService commandAuthorizationService;

    private static Cache<Pair<DeviceGroup, SimpleDevice>, Boolean> deviceGroupCache = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    private Logger log = YukonLogManager.getLogger(DeviceGroupServiceImpl.class);
    
    @Override
    public SqlFragmentSource getDeviceGroupSqlWhereClause(Collection<? extends DeviceGroup> groups, String identifier) {

        if (groups.isEmpty()) {
            return new SimpleSqlFragment("1=0");
        } else {
            SqlFragmentCollection whereClauseList = SqlFragmentCollection.newOrCollection();
            groups = removeDuplicates(groups);
            for (DeviceGroup group : groups) {
                whereClauseList.add(deviceGroupDao.getDeviceGroupSqlWhereClause(group, identifier));
            }
            return whereClauseList;
        }
    }
    
    private Set<? extends DeviceGroup> removeDuplicates(Collection<? extends DeviceGroup> groups) {
        Set<DeviceGroup> result = new HashSet<DeviceGroup>(groups);
        Iterator<DeviceGroup> iter = result.iterator();
        while (iter.hasNext()) {
            DeviceGroup myParent = iter.next().getParent();
            while (myParent != null) {
                if (result.contains(myParent)) {
                    iter.remove();
                    break;
                }
                myParent = myParent.getParent();
            }
        }
        return result;
    }

    @Override
    public Set<Integer> getDeviceIds(Collection<? extends DeviceGroup> groups) {
        if (groups.isEmpty()) {
            return Collections.emptySet();
        } else {
            groups = removeDuplicates(groups); // doesn't touch passed in collection
            Set<Integer> deviceIds = new HashSet<Integer>();
            for (DeviceGroup group: groups) {
                Set<Integer> groupDeviceIds = deviceGroupDao.getDeviceIds(group);
                deviceIds.addAll(groupDeviceIds);
            }
            return deviceIds;
        }        
    }

    @Override
    public Set<SimpleDevice> getDevices(Collection<? extends DeviceGroup> groups) {
        if (groups.isEmpty()) {
            return Collections.emptySet();
        } else if (groups.size() == 1) {
            Set<SimpleDevice> result = deviceGroupDao.getDevices(Iterables.getOnlyElement(groups));
            return result;
        } else {
            Set<SimpleDevice> result = new LinkedHashSet<SimpleDevice>();
            groups = removeDuplicates(groups); // doesn't touch passed in collection
            
            for (DeviceGroup deviceGroup : groups) {
                Set<SimpleDevice> devices = deviceGroupDao.getDevices(deviceGroup);
                result.addAll(devices);
            }
            return result;
        }
    }
    
    @Override
    public Set<SimpleDevice> getDevices(Collection<? extends DeviceGroup> groups, int maxSize) {
        
        if (groups.isEmpty()) {
            return Collections.emptySet();
            
        } else {
            
            groups = removeDuplicates(groups); // doesn't touch passed in collection
            Set<SimpleDevice> deviceSet = new LinkedHashSet<SimpleDevice>();
            for (DeviceGroup group: groups) {

                deviceGroupDao.collectDevices(group, deviceSet, maxSize);
                
                if (deviceSet.size() >= maxSize) {
                    if (deviceSet.size() > maxSize) {
                        log.warn("Device set size (" + deviceSet.size() + ") larger than maxSize requested (" + maxSize + ").");
                    }
                    break;
                }
            }
            return deviceSet;
        }        
    }
    
    @Override
    public int getDeviceCount(Collection<? extends DeviceGroup> groups) {
        if (groups.isEmpty()) {
            return 0;
        } else if (groups.size() == 1) {
            return deviceGroupDao.getDeviceCount(groups.iterator().next());
        } else {
            // this is not very efficient, but we don't have an easier way
            log.debug("getting device count on " + groups.size() + " groups, this is the slow way");
            return getDeviceIds(groups).size();
        }
    }

    @Override
    public DeviceGroup resolveGroupName(SystemGroupEnum systemGroupEnum) {
        String groupName = getFullPath(systemGroupEnum);
        return resolveGroupName(groupName);
    }
    
    @Override
    public DeviceGroup resolveGroupName(String groupName) {
        Validate.notNull(groupName, "groupName must not be null");
        Validate.isTrue(groupName.startsWith("/"), "Group name isn't valid, must start with '/': " + groupName);
        groupName = groupName.substring(1);
        
        if(StringUtils.isEmpty(groupName)){
            return getRootGroup();
        }
        
        String[] strings = groupName.split("/");
        List<String> names = new LinkedList<String>(Arrays.asList(strings));
        return getRelativeGroup(getRootGroup(), names);

    }
    
    @Override
    public DeviceGroup resolveGroupName(SystemGroupEnum systemGroupEnum, String groupName) {
        String fullPath = getFullPath(systemGroupEnum) + groupName;
        return resolveGroupName(fullPath);
    }
    
    @Override
    public DeviceGroup findGroupName(String groupName) {
        
        try {
            return resolveGroupName(groupName);
        } catch (NotFoundException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    @Override
    public Set<? extends DeviceGroup> resolveGroupNames(Collection<String> groupNames) throws NotFoundException {
        Collection<DeviceGroup> result = new ArrayList<DeviceGroup>(groupNames.size());
        for (String groupName : groupNames) {
            DeviceGroup group = resolveGroupName(groupName);
            result.add(group);
        }
        return removeDuplicates(result);
    }
    
    @Override
    public boolean isDeviceInGroup(DeviceGroup group, YukonPao pao) {
        if (pao.getPaoIdentifier().getPaoType().getPaoCategory() != PaoCategory.DEVICE) {
            return false;
        }
        
        SimpleDevice simpleDevice = new SimpleDevice(pao);
        Pair<DeviceGroup, SimpleDevice> pair = Pair.of(group, simpleDevice);
        Boolean result = deviceGroupCache.getIfPresent(pair);
        if (result == null) {
            result = deviceGroupDao.isDeviceInGroup(group, simpleDevice);
            deviceGroupCache.put(pair, result);
        }
        return result;
    }
    
    private DeviceGroup getRelativeGroup(DeviceGroup rootGroup, List<String> names) {
        if (names.isEmpty()) {
            return rootGroup;
        }
        String string = names.remove(0);
        DeviceGroup childGroup = deviceGroupDao.getGroup(rootGroup, string);
        
        return getRelativeGroup(childGroup, names);
    }
    
    @Override
    public DeviceGroup getRootGroup() {
        return deviceGroupDao.getRootGroup();
    }
    
    @Override
    public String getFullPath(SystemGroupEnum systemGroupEnum) {
        return deviceGroupEditorDao.getFullPath(systemGroupEnum);
    }
    
    @Override
    public List<LiteCommand> getDeviceCommands(List<SimpleDevice> devices, LiteYukonUser user) {
        Map<String, LiteCommand> authorized = new LinkedHashMap<String, LiteCommand>();
        ImmutableMap<Integer, LiteCommand> commands =
            Maps.uniqueIndex(commandDao.getAllCommands(), new Function<LiteCommand, Integer>() {
                @Override
                public Integer apply(LiteCommand from) {
                    return from.getLiteID();
                }
            });

        Set<PaoType> paoTypes = new TreeSet<PaoType>(new Comparator<PaoType>() {
            @Override
            public int compare(PaoType o1, PaoType o2) {
                return o1.getDbString().compareTo(o2.getDbString());
            }
        });
        paoTypes.addAll(Collections2.transform(devices, SimpleDevice.TO_PAO_TYPE));

        for (PaoType type : paoTypes) {
            List<LiteDeviceTypeCommand> all = commandDao.getAllDevTypeCommands(type.getDbString());
            for (LiteDeviceTypeCommand command : all) {
                if (command.isVisible()) {
                    LiteCommand liteCommand = commands.get(command.getCommandId());
                    if (!authorized.containsKey(liteCommand.getCommand())
                        && commandAuthorizationService.isAuthorized(user, liteCommand.getCommand())) {
                        authorized.put(liteCommand.getCommand(), liteCommand);
                    }
                }
            }
        }
        return new ArrayList<LiteCommand>(authorized.values());
    }
}
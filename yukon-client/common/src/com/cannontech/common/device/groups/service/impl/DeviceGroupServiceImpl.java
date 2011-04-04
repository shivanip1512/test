package com.cannontech.common.device.groups.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.data.pao.PaoGroupsWrapper;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

public class DeviceGroupServiceImpl implements DeviceGroupService {
    private DeviceGroupProviderDao deviceGroupDao;
    private PaoDefinitionDao paoDefinitionDao;
    private PaoGroupsWrapper paoGroupsWrapper;
    private YukonJdbcTemplate yukonJdbcTemplate;
    private Logger log = YukonLogManager.getLogger(DeviceGroupServiceImpl.class);
    
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

    public Set<Integer> getDeviceIds(Collection<? extends DeviceGroup> groups) {
        if (groups.isEmpty()) {
            return Collections.emptySet();
//        } else if (groups.size() == 1) {
//            return deviceGroupDao.getDeviceIds(groups.iterator().next());
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
    public List<SimpleDevice> getDevicesInGroupThatSupportAttribute(DeviceGroup group, Attribute attribute) {
        Multimap<PaoType, Attribute> allDefinedAttributes = paoDefinitionDao.getPaoTypeAttributesMultiMap();
        Multimap<Attribute, PaoType> dest = HashMultimap.create();
        Multimaps.invertFrom(allDefinedAttributes, dest);
        Collection<PaoType> collection = dest.get(attribute);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT ypo.paobjectid, ypo.type");
        sql.append("FROM Device d");
        sql.append("JOIN YukonPaObject ypo ON (d.deviceid = ypo.paobjectid)");
        sql.append("WHERE ypo.type").in(collection);
        SqlFragmentSource groupSqlWhereClause = getDeviceGroupSqlWhereClause(Collections.singleton(group), "ypo.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);

        YukonDeviceRowMapper mapper = new YukonDeviceRowMapper(paoGroupsWrapper);
        List<SimpleDevice> devices = yukonJdbcTemplate.query(sql, mapper);

        return devices;
    }

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
    
    public DeviceGroup findGroupName(String groupName) {
        
        try {
            return resolveGroupName(groupName);
        } catch (NotFoundException e) {
            return null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
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
        boolean result = deviceGroupDao.isDeviceInGroup(group, simpleDevice);
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
    
    public DeviceGroup getRootGroup() {
        return deviceGroupDao.getRootGroup();
    }
    
    @Required
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    
    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
    }
    
    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}

package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jdbc.core.simple.SimpleJdbcOperations;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.DeviceGroupType;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.data.pao.PaoGroupsWrapper;

public class StaticDeviceGroupProvider extends DeviceGroupDaoBase {
    private DeviceGroupEditorDao deviceGroupEditorDao;
    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    private SimpleJdbcOperations jdbcTemplate;
    private PaoGroupsWrapper paoGroupsWrapper;

    @Override
    public List<YukonDevice> getChildDevices(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<YukonDevice> childDevices = deviceGroupMemberEditorDao.getChildDevices(sdg);
        return childDevices;
    }

    @Override
    public List<? extends DeviceGroup> getChildGroups(DeviceGroup group) {
        StoredDeviceGroup sdg = getStoredGroup(group);
        List<StoredDeviceGroup> childGroups = deviceGroupEditorDao.getChildGroups(sdg);
        return childGroups;
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
/*            for (YukonDevice yukonDevice : getMainDelegator().getChildDevices(group)) {
                if (yukonDevice.getDeviceId() == device.getDeviceId()) {
                    resultSet.add(group);
                }
            }

//            resultSet.addAll(tempGroups);
*/        
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
    public void setPaoGroupsWrapper(PaoGroupsWrapper paoGroupsWrapper) {
        this.paoGroupsWrapper = paoGroupsWrapper;
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

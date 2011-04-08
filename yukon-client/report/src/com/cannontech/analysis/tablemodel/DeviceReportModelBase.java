package com.cannontech.analysis.tablemodel;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DeviceDao;
import com.google.common.collect.Lists;

public abstract class DeviceReportModelBase<T> extends BareDatedReportModelBase<T> {

    private Logger log = YukonLogManager.getLogger(DeviceReportModelBase.class);
    
    private List<String> groupsFilter;
    private List<String> deviceFilter;

    private DeviceGroupService deviceGroupService;
    private DeviceGroupEditorDao deviceGroupEditorDao;
    protected DeviceDao deviceDao;

	/** 
	 * Returns a set of SimpleDevices for the filter values. If filter values are empty, the DeviceTypes SystemGroup is used.
	 * Only use getDeviceList when a set of simpleDevices is required.
	 * Performance issues if using getDeviceList for a deviceGroup filter values (groupsFilter) as this returns all devices
	 *  from the group instead of "smart" sql where clause.
	 * @param identifier - sql clause identifier
	 * @return
	 */
	protected Iterable<SimpleDevice> getDeviceList() {
		if (!IterableUtils.isEmpty(groupsFilter)) {
            Set<? extends DeviceGroup> groups = deviceGroupService.resolveGroupNames(groupsFilter);
            return deviceGroupService.getDevices(groups);
        } else if (!IterableUtils.isEmpty(deviceFilter)) {
            List<SimpleDevice> devices = Lists.newArrayList();
            for(String deviceName : deviceFilter){
                try {
                    devices.add(deviceDao.getYukonDeviceObjectByName(deviceName));
                } catch (DataAccessException e) {
                    log.error("Unable to find device with name: " + deviceName + ". This device will be skipped.");
                    continue;
                }
            }
            return devices;
        } else {
            /* If they didn't pick anything to filter on, assume all devices. */
            /* Use contents of SystemGroupEnum.DEVICETYPES. */
            DeviceGroup group = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICETYPES);
            return deviceGroupService.getDevices(Collections.singletonList(group));
        }
    }
	
	/** 
	 * Returns an SqlFragment for the filter values. If filter values are empty, the DeviceTypes SystemGroup is used.
	 * @param identifier - sql clause identifier
	 * @return
	 */
	protected SqlFragmentSource getFilterSqlWhereClause(String identifier) {
		Set<? extends DeviceGroup> deviceGroups;
		if(!IterableUtils.isEmpty(groupsFilter)) {
			deviceGroups = deviceGroupService.resolveGroupNames(groupsFilter);
			return deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, identifier);
		} else if (!IterableUtils.isEmpty(deviceFilter)) {
			SqlStatementBuilder sql = new SqlStatementBuilder();
            List<Integer> deviceIds = Lists.newArrayList();
            for(String deviceName : deviceFilter){
                try {
                    deviceIds.add(deviceDao.getYukonDeviceObjectByName(deviceName).getDeviceId());
                } catch (DataAccessException e) {
                    log.error("Unable to find device with name: " + deviceName + ". This device will be skipped.");
                    continue;
                }
            }
            return sql.append(identifier).in(deviceIds); 
		} else {
			deviceGroups = Collections.singleton(deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.DEVICETYPES));
			return deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, identifier);
		}
	}
    public void setGroupsFilter(List<String> groupsFilter) {
		this.groupsFilter = groupsFilter;
	}
    
    public void setDeviceFilter(List<String> deviceFilter) {
		this.deviceFilter = deviceFilter;
	}
    
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}
    
    @Autowired
    public void setDeviceGroupEditorDao(
			DeviceGroupEditorDao deviceGroupEditorDao) {
		this.deviceGroupEditorDao = deviceGroupEditorDao;
	}
    
    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}
    
}

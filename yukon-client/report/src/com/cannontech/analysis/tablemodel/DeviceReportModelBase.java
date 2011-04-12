package com.cannontech.analysis.tablemodel;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public abstract class DeviceReportModelBase<T> extends BareDatedReportModelBase<T> {

    private List<String> groupsFilter;
    private List<String> deviceFilter;

    private DeviceGroupService deviceGroupService;
    
	/** 
     * Returns an SqlFragmentSource that can be placed in an SQL WHERE clause
     * for the identifier field. 
     * The paoIdIdentifier field MUST BE of type PAObjectID or an extension of.
     * The paoNameIdentifier field MUST BE of type PaoName. 
     * The whole SQL statement might look something like:
     *   SELECT * FROM DEVICE WHERE <paoIdIdentifier> IN (SELECT DISTINCT PaobjectId FROM YukonPaobject)
     *    In the above sql, "<paoIdIdentifier> IN (SELECT DISTINCT PaobjectId FROM YukonPaobject)" may be the returned string.    
     * OR  SELECT * FROM YukonPaobject WHERE <paoNameIdentifier> IN (?, ?, ?)
     * 	  In the above sql, "<paoNameIdentifier> IN (?, ?, ?)" may be the returned string.
     * If filter values are empty, 1=1 is returned.
	 * @param paoIdIdentifier - sql clause identifier for PaobjectId
	 * @param paoNameIdentifier - sql clause identifier for PaoName 
	 * @return
	 */
	protected SqlFragmentSource getFilterSqlWhereClause() {
		Set<? extends DeviceGroup> deviceGroups;
		if(!IterableUtils.isEmpty(groupsFilter)) {
			deviceGroups = deviceGroupService.resolveGroupNames(groupsFilter);
			return deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, getPaoIdIdentifier());
		} else if (!IterableUtils.isEmpty(deviceFilter)) {
			SqlStatementBuilder sql = new SqlStatementBuilder();
            return sql.append(getPaoNameIdentifer()).in(deviceFilter); 
		} else {
			return new SimpleSqlFragment("1=1");
		}
	}

	/**
	 * Returns the fully qualified sql identifier for selecting PaobjectId values (or a derivative of).
	 * @return
	 */
	public abstract String getPaoIdIdentifier();

	/**
	 * Returns the fully qualified sql identifier for selecting PaoName values.
	 * @return
	 */
	public abstract String getPaoNameIdentifer();
	
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
}

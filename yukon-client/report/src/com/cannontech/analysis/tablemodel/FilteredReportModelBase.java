package com.cannontech.analysis.tablemodel;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.analysis.ReportFilter;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.editor.dao.impl.YukonDeviceRowMapper;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.StringUtils;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.database.YukonJdbcTemplate;

/**
 * Use this model base when you have "filtered" data.
 * This model can return the filtered data as either a list of YukonPaos 
 *  OR as a WHERE clause for "smart" query building (based on identifiers of type PaobjectId). 
 * @param <T>
 */
public abstract class FilteredReportModelBase<T> extends BareDatedReportModelBase<T> implements FilteredModelAttributes{

    private Logger log = YukonLogManager.getLogger(FilteredReportModelBase.class);

    private FilteredModelHelper filteredModelHelper; 
    private DeviceGroupService deviceGroupService;
    protected DeviceDao deviceDao;
    private YukonJdbcTemplate yukonJdbcTemplate;
    
	@Override
	public void setFilteredModelHelper(FilteredModelHelper filteredModelHelper) {
		this.filteredModelHelper = filteredModelHelper;
	}
    
	@Override
	public SqlFragmentSource getPaoIdentifierWhereClause(String identifier) {
		return getFilterWhereClause(identifier);
	}
	
	/**
	 * Loads and returns a list of YukonPaos for filtered values, specifically _Device_s.
	 */
	@Override
	public List<? extends YukonPao> getYukonPaoList() {
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT PaobjectId, Type");
    	sql.append("FROM YukonPaobject");
    	sql.append(    "JOIN Device ON PaobjectId = DeviceId");
    	sql.append("WHERE").appendFragment(getFilterWhereClause("PaobjectId"));
    	List<SimpleDevice> deviceList = yukonJdbcTemplate.query(sql, new YukonDeviceRowMapper());
		return deviceList;
	}
	
    /**
     * Helper method to build an SQL "where" clause based on the request's ReportFilter and values.
     * If no whereClauseGenerator is created, assume all devices.
     * @param req
     * @return
     */
	private SqlFragmentSource getFilterWhereClause(String identifier) {
		SqlFragmentSource whereClauseFragment = null;
        ReportFilter filter = filteredModelHelper.getFilter();
		if (filter == ReportFilter.GROUPS) {
		    String groups[] = filteredModelHelper.getGroupNameValues();
		    final List<String> deviceGroupList = Arrays.asList(groups);
			if(!IterableUtils.isEmpty(deviceGroupList)) {
    			Set<? extends DeviceGroup> deviceGroups = deviceGroupService.resolveGroupNames(deviceGroupList);
    			whereClauseFragment = deviceGroupService.getDeviceGroupSqlWhereClause(deviceGroups, identifier);
			}
		} else if (filter == ReportFilter.DEVICE) {
			String filterValueList = filteredModelHelper.getDeviceNameValues();
    	    final List<String> deviceNames = StringUtils.parseStringsForList(filterValueList);
    	    
			if (!IterableUtils.isEmpty(deviceNames)) {
		        SqlStatementBuilder deviceNameSql = new SqlStatementBuilder();
		        deviceNameSql.append("SELECT PaobjectId FROM YukonPaobject");
		        deviceNameSql.append("WHERE PaoName").in(deviceNames);

		        SqlStatementBuilder sql = new SqlStatementBuilder();
		        sql.append(identifier).in(deviceNameSql);
		        whereClauseFragment = sql;
			}
		} else if (filter == ReportFilter.METER) {
			String filterValueList = filteredModelHelper.getMeterNumberValues();
    	    final List<String> meterNumbers = StringUtils.parseStringsForList(filterValueList);

    	    if (!IterableUtils.isEmpty(meterNumbers)) {
				SqlStatementBuilder meterNumberSql = new SqlStatementBuilder();
				meterNumberSql.append("SELECT DeviceId FROM DeviceMeterGroup");
				meterNumberSql.append("WHERE MeterNumber").in(meterNumbers);

		        SqlStatementBuilder sql = new SqlStatementBuilder();
		        sql.append(identifier).in(meterNumberSql);
		        whereClauseFragment = sql;
			}
		} // ADD MORE FILTERS HERE IF/WHEN NEEDED 
		else {
			log.warn("No matching ReportFilter " +  filter + " found.");
		}
		
		
		// build up a dummy generator when null
		if (whereClauseFragment == null){
			whereClauseFragment = new SimpleSqlFragment("1=1");
			log.warn("No filter where clause loaded, returning 1=1.");
		}
		return whereClauseFragment;
	}
	
    @Autowired
    public void setDeviceGroupService(DeviceGroupService deviceGroupService) {
		this.deviceGroupService = deviceGroupService;
	}

    @Autowired
    public void setDeviceDao(DeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
		this.yukonJdbcTemplate = yukonJdbcTemplate;
	}
}

package com.cannontech.analysis.tablemodel;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.google.common.collect.Lists;

public abstract class MeterReportModelBase<T> extends DeviceReportModelBase<T> {

private Logger log = YukonLogManager.getLogger(MeterReportModelBase.class);
    
    private List<String> meterNumberFilter;

    @Override
	protected SqlFragmentSource getFilterSqlWhereClause(String identifier) {
        if (!IterableUtils.isEmpty(meterNumberFilter)) {
			SqlStatementBuilder sql = new SqlStatementBuilder();
            List<Integer> deviceIds = Lists.newArrayList();
            for(String meterNumber : meterNumberFilter){
                try {
                    deviceIds.add(deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber).getDeviceId());
                } catch (DataAccessException e) {
                    log.error("Unable to find meter with meterNumber: " + meterNumber + ". This meter will be skipped.");
                    continue;
                }
            }
            return sql.append(identifier).in(deviceIds); 
        } else {
        	return super.getFilterSqlWhereClause(identifier);
        }
    }
	
	public void setMeterNumberFilter(List<String> meterNumberFilter) {
		this.meterNumberFilter = meterNumberFilter;
	}
}

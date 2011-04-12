package com.cannontech.analysis.tablemodel;

import java.util.List;

import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public abstract class MeterReportModelBase<T> extends DeviceReportModelBase<T> {

    private List<String> meterNumberFilter;
    
    @Override
	protected SqlFragmentSource getFilterSqlWhereClause() {
        if (!IterableUtils.isEmpty(meterNumberFilter)) {
			SqlStatementBuilder sql = new SqlStatementBuilder();
            return sql.append(getMeterNumberIdentifier()).in(meterNumberFilter); 
        } else {
        	return super.getFilterSqlWhereClause();
        }
    }
	
	public void setMeterNumberFilter(List<String> meterNumberFilter) {
		this.meterNumberFilter = meterNumberFilter;
	}
	
	public abstract String getMeterNumberIdentifier();
}

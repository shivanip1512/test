package com.cannontech.web.amr.meter;

import java.util.List;

import com.cannontech.amr.meter.search.model.FilterBy;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.web.amr.meter.service.MspMeterSearchMethodResultProvider;

public class MspFilterBy implements FilterBy {

	private String name = null;
    private String filterValue = null;
    private MspMeterSearchMethodResultProvider methodResultProvider;

    public MspFilterBy(String name, MspMeterSearchMethodResultProvider methodResultProvider) {
        this.name = name;
        this.methodResultProvider = methodResultProvider;
    }

    @Override
    public String getFormatKey() {
    	return "yukon.web.mspFilterBy." + getName();
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFilterValue() {
        return filterValue;
    }
    
    @Override
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    @Override
    public SqlFragmentSource getSqlWhereClause() {
    	
    	List<String> meterNumbers = methodResultProvider.getMeterNumbers(filterValue);
    	
    	if (meterNumbers.size() == 0) {
    		return new SimpleSqlFragment("1 = 0");
    	}
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(MeterSearchField.METERNUMBER.getSearchQueryField(), " IN ( ");
        sql.appendArgumentList(meterNumbers);
        sql.append(")");

        return sql;
    }

    @Override
    public String toSearchString() {
    	return methodResultProvider.getSearchField().getName() + " equal to '" + filterValue + "'";
    }
}

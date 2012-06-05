package com.cannontech.amr.meter.search.model;

import com.cannontech.common.util.SqlFragmentSource;

public interface FilterBy {

	/**
	 * Return an SQL fragment source to be added to the meter search where clause
	 * @return
	 */
	public SqlFragmentSource getSqlWhereClause();
	
	/**
     * Method to return a meter search friendly string representation of this FilterBy
     * @return Meter Search friendly string
     */
    public String toSearchString();
    
	public String getName();
	public String getFormatKey();
	public String getFilterValue();
    public void setFilterValue(String filterValue);
	
	
    
    
    
}

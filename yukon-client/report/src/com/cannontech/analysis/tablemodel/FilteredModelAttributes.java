package com.cannontech.analysis.tablemodel;

import java.util.List;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.SqlFragmentSource;


public interface FilteredModelAttributes  {

	public void setFilteredModelHelper(FilteredModelHelper filteredModelHelper);
	
	/**
	 * Override this method to return a list of YukonPaos for use when loading model data.
	 * This method most often will perform database queries or call other daos to load list.
	 * May be expensive so do not call repeatedly.
	 */
	public List<? extends YukonPao> getYukonPaoList();

	/**
	 * Override this method to return a where clause for use in building a "smart" query.
	 *
	 * Returns an SqlFragmentSource that can be placed in an SQL WHERE clause
	 * for the identifier field. 
	 * The identifier field MUST BE of type PAObjectID or an extension of.
	 * The whole SQL statement might look something like:
	 *   select * from device where <identifier> in (select distinct paobjectid from yukonpaobject)
	 * In the above sql, "<identifier> in (select distinct paobjectid from yukonpaobject)" may be the returned string.
	 * 
	 * @return
	 */
	public SqlFragmentSource getPaoIdentifierWhereClause(String identifier);
}

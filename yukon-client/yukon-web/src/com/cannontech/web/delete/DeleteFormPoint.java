package com.cannontech.web.delete;

import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.DBPersistent;

/**
 * @author ryan
 *
 */
public class DeleteFormPoint extends DeleteForm
{
	public DeleteFormPoint() {
		super();
	}

	/**
	 * Retrieves the items that are to be deleted
	 */
	public DBPersistent getDBObj( int itemID ) {
		return LiteFactory.createDBPersistent( PointFuncs.getLitePoint(itemID) );		
	}


}
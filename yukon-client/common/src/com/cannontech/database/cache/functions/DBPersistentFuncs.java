/*
 * Created on Nov 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.cache.functions;

import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.DBPersistent;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DBPersistentFuncs
{
	private DBPersistentFuncs()
	{
		super();
	}
	
	public static DBPersistent retrieveDBPersistent(LiteBase liteObject)
	{
		//create a DBPersistent from a liteBase object
		DBPersistent dbPersistent = null;
		if( liteObject != null)
		{
			dbPersistent = LiteFactory.createDBPersistent(liteObject);
			try {
				Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, dbPersistent);
				dbPersistent = t.execute();
			}
			catch(Exception e) {
				com.cannontech.clientutils.CTILogger.error(e.getMessage(), e);
			}
		}
		return dbPersistent;
	}

}

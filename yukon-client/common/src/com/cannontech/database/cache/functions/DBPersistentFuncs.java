/*
 * Created on Nov 20, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.cache.functions;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * @author snebben
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DBPersistentFuncs
{
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
	/**
	 * Create an UPDATE Transaction and execute.
	 * Write a DBChangeMsg for database CHANGE_TYPE_UPDATE
	 * @param item
	 */
	public static void performDBChange(DBPersistent item, ClientConnection connToDispatch, int transactionType)
	{
		int dbChangeType = -1;
		
		switch(transactionType)
		{
			case Transaction.INSERT:
				dbChangeType = DBChangeMsg.CHANGE_TYPE_ADD;
				break;
			case Transaction.DELETE:
				dbChangeType = DBChangeMsg.CHANGE_TYPE_DELETE;
				break;
			case Transaction.UPDATE:
				dbChangeType = DBChangeMsg.CHANGE_TYPE_UPDATE;
				break;
			default:
				CTILogger.info("Unknown Transaction type " + transactionType + ", no transaction performed");
				return;
		}

		try
		{
			Transaction t = Transaction.createTransaction( transactionType, item);
			item = t.execute();
			
			//write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
			DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)item, dbChangeType);
					
			for( int i = 0; i < dbChange.length; i++)
			{
				DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
				connToDispatch.write(dbChange[i]);
			}
		}
		catch( com.cannontech.database.TransactionException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
	}
}

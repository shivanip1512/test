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
import com.cannontech.message.dispatch.SingletonClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;

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

	/**
	 * Create an INSERT Transaction and execute.
	 * Write a DBChangeMsg for database CHANGE_TYPE_ADD
	 * @param item
	 */
	public static void create(DBPersistent item) 
	{
		if( item != null )
		{
			try
			{
				Transaction t = Transaction.createTransaction(Transaction.INSERT, item);
				item = t.execute();

				//write the DBChangeMessage out to Dispatch since it was a Successfull ADD
				DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)item, DBChangeMsg.CHANGE_TYPE_ADD);
			
				for( int i = 0; i < dbChange.length; i++)
				{
					DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
					SingletonClientConnection.getInstance().getClientConnection().write(dbChange[i]);
				}
			}
			catch( com.cannontech.database.TransactionException e )
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
	}
	/**
	 * Create a DELETE Transaction and execute.
	 * Write a DBChangeMsg for database CHANGE_TYPE_DELETE
	 * @param item
	 */
	public static void delete(DBPersistent item)
	{
		try
		{
			Transaction t = Transaction.createTransaction( Transaction.DELETE, item);
			item = t.execute();
			
			//write the DBChangeMessage out to Dispatch since it was a Successfull DELETE
			DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)item, DBChangeMsg.CHANGE_TYPE_DELETE);
					
			for( int i = 0; i < dbChange.length; i++)
			{
				DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
				SingletonClientConnection.getInstance().getClientConnection().write(dbChange[i]);
			}
		}
		catch( com.cannontech.database.TransactionException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
	}
	/**
	 * Create an UPDATE Transaction and execute.
	 * Write a DBChangeMsg for database CHANGE_TYPE_UPDATE
	 * @param item
	 */
	public static void update(DBPersistent item)
	{
		try
		{
			Transaction t = Transaction.createTransaction( Transaction.UPDATE, item);
			item = t.execute();
			
			//write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
			DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)item, DBChangeMsg.CHANGE_TYPE_UPDATE);
					
			for( int i = 0; i < dbChange.length; i++)
			{
				DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
				SingletonClientConnection.getInstance().getClientConnection().write(dbChange[i]);
			}
		}
		catch( com.cannontech.database.TransactionException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
	}
	
	/**
	 * Valid transType are from com.cannontech.database.Transaction.[UPDATE|INSERT|DELETE|...|]
	 * @param item
	 */
	public static void executeTransaction(DBPersistent item, int transType)
	{
		switch(transType)
		{
			case Transaction.INSERT:
				create(item);
				break;
			case Transaction.DELETE:
				delete(item);
				break;
			case Transaction.UPDATE:
				update(item);
				break;
			default:
				CTILogger.info("Unknown Transaction type " + transType + ", no transaction performed");
		}
	}


}

package com.cannontech.database.cache.functions;

import java.util.Iterator;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.database.Transaction;
import com.cannontech.database.db.CTIDbChange;
/**
 * DBPersistent related data transactions.
 * Using the Transaction class, a dbPersistent may have the selected operation done to it.
 * A DBChangeMessage will be wrote to connToDispatch.
 * @author snebben
 */
public final class DBPersistentFuncs
{
	private DBPersistentFuncs()
	{ 
		
	}

/**
 * Insert the method's description here.
 * Creation date: (02/07/03)
 * @param object com.cannontech.database.db.DBPersistent
 * @param connToDispatch com.cannontech.message.dispatch.ClientConnection
 */
public static boolean update(com.cannontech.database.db.DBPersistent object, com.cannontech.message.dispatch.ClientConnection connToDispatch) 
{
	try
	{
		Transaction t = Transaction.createTransaction( Transaction.UPDATE, object);
		object = t.execute();

		//write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
		DBChangeMsg[] dbChange = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange)object, DBChangeMsg.CHANGE_TYPE_UPDATE);
		
		for( int i = 0; i < dbChange.length; i++)
		{
			DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
			connToDispatch.write(dbChange[i]);
		}
	}
	catch( com.cannontech.database.TransactionException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}
	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (02/07/03)
 * @param object com.cannontech.database.data.lite.LiteBase
 * @param connToDispatch com.cannontech.message.dispatch.ClientConnection
 */
public static boolean update(com.cannontech.database.data.lite.LiteBase liteObject, com.cannontech.message.dispatch.ClientConnection connToDispatch) 
{
	com.cannontech.database.db.DBPersistent object = retrieve(liteObject);
	return update(object, connToDispatch);
}

/**
 * Insert the method's description here.
 * Creation date: (02/07/03)
 * @param object com.cannontech.database.db.DBPersistent
 * @param connToDispatch com.cannontech.message.dispatch.ClientConnection
 */
public static boolean delete(com.cannontech.database.db.DBPersistent object, com.cannontech.message.dispatch.ClientConnection connToDispatch) 
{
	try
	{
		Transaction t = Transaction.createTransaction( Transaction.DELETE, object);
		object = t.execute();

		//write the DBChangeMessage out to Dispatch since it was a Successfull DELETE
		com.cannontech.message.dispatch.message.DBChangeMsg[] dbChange = 
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance().createDBChangeMessages((com.cannontech.database.db.CTIDbChange)object, DBChangeMsg.CHANGE_TYPE_DELETE);
		
		for( int i = 0; i < dbChange.length; i++)
		{
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
			connToDispatch.write(dbChange[i]);
		}
	}
	catch( com.cannontech.database.TransactionException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}
	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (02/07/03)
 * @param object com.cannontech.database.data.lite.LiteBase
 * @param connToDispatch com.cannontech.message.dispatch.ClientConnection
 */
public static boolean delete(com.cannontech.database.data.lite.LiteBase liteObject, com.cannontech.message.dispatch.ClientConnection connToDispatch) 
{
	com.cannontech.database.db.DBPersistent object = retrieve(liteObject);
	return delete(object, connToDispatch);
}
/**
 * Insert the method's description here.
 * Creation date: (02/07/03)
 * @param object com.cannontech.database.db.DBPersistent
 * @param connToDispatch com.cannontech.message.dispatch.ClientConnection
 */
public static boolean add(com.cannontech.database.db.DBPersistent object, com.cannontech.message.dispatch.ClientConnection connToDispatch) 
{
	try
	{
		Transaction t = Transaction.createTransaction( Transaction.INSERT, object);
		object = t.execute();

		//write the DBChangeMessage out to Dispatch since it was a Successfull ADD
		com.cannontech.message.dispatch.message.DBChangeMsg[] dbChange = 
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance().createDBChangeMessages((com.cannontech.database.db.CTIDbChange)object, DBChangeMsg.CHANGE_TYPE_ADD);
		
		for( int i = 0; i < dbChange.length; i++)
		{
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);
			connToDispatch.write(dbChange[i]);
		}
	}
	catch( com.cannontech.database.TransactionException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		return false;
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (02/07/03)
 * @param object com.cannontech.database.data.lite.LiteBase
 * @param connToDispatch com.cannontech.message.dispatch.ClientConnection
 */
public static boolean add(com.cannontech.database.data.lite.LiteBase liteObject, com.cannontech.message.dispatch.ClientConnection connToDispatch) 
{
	com.cannontech.database.db.DBPersistent object = retrieve(liteObject);
	return add(object, connToDispatch);
}

/**
 * Insert the method's description here.
 * Creation date: (02/07/03)
 * @param object com.cannontech.database.db.DBPersistent
 * @param connToDispatch com.cannontech.message.dispatch.ClientConnection
 */
public static com.cannontech.database.db.DBPersistent retrieve(com.cannontech.database.db.DBPersistent object)
{
	try
	{
		Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, object);
		return t.execute();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (02/07/03)
 * @param object com.cannontech.database.db.DBPersistent
 * @param connToDispatch com.cannontech.message.dispatch.ClientConnection
 */
public static com.cannontech.database.db.DBPersistent retrieve(com.cannontech.database.data.lite.LiteBase liteObject)
{
	com.cannontech.database.db.DBPersistent dbPersistent = com.cannontech.database.data.lite.LiteFactory.createDBPersistent(liteObject);
	return retrieve(dbPersistent);
}
}

package com.cannontech.database;

import com.cannontech.yukon.concrete.ResourceFactory;
/**
 * This type was created in VisualAge.
 */

public class Transaction
{
	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int RETRIEVE = 3;
	public static final int DELETE = 4;
	public static final int DELETE_PARTIAL = 5;
	public static final int ADD_PARTIAL = 6;

	private int operation;
	private java.util.Date executeTimeStamp;
   private com.cannontech.yukon.IDBPersistent db = null;
	
	private com.cannontech.database.db.DBPersistent object;
	private String databaseAlias;
/**
 * Transaction constructor comment.
 */
protected Transaction(int operation, com.cannontech.database.db.DBPersistent obj) {
	super();
	this.operation = operation;
	this.object = obj;
	this.databaseAlias = com.cannontech.common.util.CtiUtilities.getDatabaseAlias();
}
/**
 * Transaction constructor comment.
 */
protected Transaction(int operation, com.cannontech.database.db.DBPersistent obj, String databaseAlias) {
	super();
	this.operation = operation;
	this.object = obj;
	this.databaseAlias = databaseAlias;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.Transaction
 * @param operation int
 * @param obj DBPersistent
 */
public static Transaction createTransaction( int operation, com.cannontech.database.db.DBPersistent obj) {
	return new Transaction( operation, obj );
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.Transaction
 * @param operation int
 * @param obj DBPersistent
 */
public static Transaction createTransaction( int operation, com.cannontech.database.db.DBPersistent obj, String databaseAlias) {
	return new Transaction( operation, obj, databaseAlias );
}

private com.cannontech.yukon.IDBPersistent getDB()
{
   if( db == null )
      db = ResourceFactory.getIYukon().createIDBPersistent();
      
   return db;
}

/**
 * execute method comment.
 */
public com.cannontech.database.db.DBPersistent execute() throws TransactionException 
{
   return getDB().execute( this.operation, this.object );
}
}

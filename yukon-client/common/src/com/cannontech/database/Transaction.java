package com.cannontech.database;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;

public class Transaction extends com.cannontech.common.util.Command {
	public static final int INSERT = 1;
	public static final int UPDATE = 2;
	public static final int RETRIEVE = 3;
	public static final int DELETE = 4;
	public static final int DELETE_PARTIAL = 5;
	public static final int ADD_PARTIAL = 6;

	private int operation;
	private java.util.Date executeTimeStamp;
	
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
/**
 * execute method comment.
 */
public void execute() throws TransactionException {
	internalExecute( this.operation, this.object );
}
/**
 * This method was created in VisualAge.
 * @param operation int
 * @param object com.cannontech.database.db.DBPersistent
 */
private  synchronized void internalExecute(int operation, com.cannontech.database.db.DBPersistent object) throws TransactionException{

	java.sql.Connection conn = null;
	boolean autoCommit = false;
	
	try
	{
		conn = PoolManager.getInstance().getConnection( this.databaseAlias );
		autoCommit = conn.getAutoCommit();
		conn.setAutoCommit(false);
		object.setDbConnection( conn );
				
		switch( operation )
		{
			case INSERT:
				object.add();
				break;	

			case UPDATE:
				object.update();
				break;

			case RETRIEVE:
				object.retrieve();
				break;

			case DELETE:
				object.delete();
				break;

			case DELETE_PARTIAL:
				object.deletePartial();
				break;

			case ADD_PARTIAL:	
				object.addPartial();
				break;

			default:
				throw new TransactionException("Unknown operation:  " + operation);
		}

		conn.commit();

		//DO NOT LET THE DBPERSISTENT OBJECT HOLD ONTO A REFERENCE TO THE CONNECTION
		//IT'S JUST A BAD IDEA SINCE WE DON'T KNOW HOW THEY ARE BEING MANAGED
		object.setDbConnection(null);
	}
	catch( java.sql.SQLException e )
	{
		//Attempt a rollback
		try
		{
			conn.rollback();
		}
		catch( java.sql.SQLException e2 )
		{
			throw new TransactionException( e.getMessage() );	
		}
		
		throw new TransactionException( e.getMessage() );
	}
	finally
	{
		try
		{
			if( conn != null )
			{
				conn.setAutoCommit(autoCommit);
				conn.close();
			}
		}
		catch( java.sql.SQLException e )
		{
			e.printStackTrace();
		}
	}
}
}

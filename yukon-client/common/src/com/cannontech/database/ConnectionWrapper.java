package com.cannontech.database;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Map;

/**
 * This class is a wrapper around a Connection, overriding the
 * close method to just inform the pool that it's available for
 * reuse again, and the isClosed method to return the state
 * of the wrapper instead of the Connection.
 */
class ConnectionWrapper implements java.sql.Connection
{
   /* Blank interface to supply upperwards compatibility to */
   /* Uncomment for jkd 1.3, comment out for 1.4 */
   //private interface Savepoint
   //{};
   
   
   // realConn should be private but we use package scope to
   // be able to test removal of bad connections
   Connection realConn;
   private ConnectionPool pool;
   private boolean isClosed = false;

   public ConnectionWrapper(Connection realConn, ConnectionPool pool)
   {
	  this.realConn = realConn;
	  this.pool = pool;
   }
   /*
	* Wrapped methods.
	*/
   public void clearWarnings() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  realConn.clearWarnings();
   }
   /**
	* Inform the ConnectionPool that the ConnectionWrapper
	* is closed.
	*/
   public void close() throws SQLException
   {
      //just in case the user changed the AutoCommit flag, commit any open
      //transactions since the user will no longer have access to this
      //connection and reset the flag to TRUE
      realConn.setAutoCommit( true );

	  isClosed = true;
	  pool.wrapperClosed(realConn);
   }
   public void commit() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  realConn.commit();
   }
   public Statement createStatement() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.createStatement();
   }
   public Statement createStatement( int resultSetType, int resultSetConcurrency) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.createStatement();
   }
/**
 * Creates a <code>Statement</code> object that will generate
 * <code>ResultSet</code> objects with the given type, concurrency,
 * and holdability.
 * This method is the same as the <code>createStatement</code> method
 * above, but it allows the default result set
 * type, concurrency, and holdability to be overridden.
 *
 * @param resultSetType one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
 * @param resultSetConcurrency one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.CONCUR_READ_ONLY</code> or
 *        <code>ResultSet.CONCUR_UPDATABLE</code>
 * @param resultSetHoldability one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
 *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
 * @return a new <code>Statement</code> object that will generate
 *        <code>ResultSet</code> objects with the given type,
 *        concurrency, and holdability
 * @exception SQLException if a database access error occurs
 *           or the given parameters are not <code>ResultSet</code>
 *           constants indicating type, concurrency, and holdability
 * @see ResultSet
 * @since 1.4
 */
public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    throw new Error("Not implemented yet");
}
   public boolean getAutoCommit() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.getAutoCommit();
   }
   public String getCatalog() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.getCatalog();
   }
/**
 * Retrieves the current holdability of <code>ResultSet</code> objects
 * created using this <code>Connection</code> object.
 *
 * @return the holdability, one of
 *       <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
 *       <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
 * @throws SQLException if a database access occurs
 * @see #setHoldability
 * @see ResultSet
 * @since 1.4
 */
public int getHoldability() throws SQLException {
    throw new Error("Not implemented yet");
}
   public DatabaseMetaData getMetaData() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.getMetaData();
   }
   public int getTransactionIsolation() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.getTransactionIsolation();
   }
/**
 * Insert the method's description here.
 * Creation date: (1/3/00 9:48:54 AM)
 * @return java.util.Map
 */
public Map getTypeMap() throws SQLException{
	 if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.getTypeMap();
}
   public SQLWarning getWarnings() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.getWarnings();
   }
   /**
	* Returns true if the ConnectionWrapper is closed, false
	* otherwise.
	*/
   public boolean isClosed() throws SQLException
   {
	  return isClosed;
   }
   public boolean isReadOnly() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.isReadOnly();
   }
   public String nativeSQL(String sql) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.nativeSQL(sql);
   }
   public CallableStatement prepareCall(String sql) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.prepareCall(sql);
   }
   public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.prepareCall(sql, resultSetType, resultSetConcurrency);
   }
/**
 * Creates a <code>CallableStatement</code> object that will generate
 * <code>ResultSet</code> objects with the given type and concurrency.
 * This method is the same as the <code>prepareCall</code> method
 * above, but it allows the default result set
 * type, result set concurrency type and holdability to be overridden.
 *
 * @param sql a <code>String</code> object that is the SQL statement to
 *           be sent to the database; may contain on or more ? parameters
 * @param resultSetType one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
 * @param resultSetConcurrency one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.CONCUR_READ_ONLY</code> or
 *        <code>ResultSet.CONCUR_UPDATABLE</code>
 * @param resultSetHoldability one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
 *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
 * @return a new <code>CallableStatement</code> object, containing the
 *        pre-compiled SQL statement, that will generate
 *        <code>ResultSet</code> objects with the given type,
 *        concurrency, and holdability
 * @exception SQLException if a database access error occurs
 *           or the given parameters are not <code>ResultSet</code>
 *           constants indicating type, concurrency, and holdability
 * @see ResultSet
 * @since 1.4
 */
public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    throw new Error("Not implemented yet");
}
   public PreparedStatement prepareStatement(String sql) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.prepareStatement(sql);
   }
/**
 * Creates a default <code>PreparedStatement</code> object capable
 * of returning the auto-generated keys designated by the given array.
 * This array contains the indexes of the columns in the target
 * table that contain the auto-generated keys that should be made
 * available. This array is ignored if the SQL
 * statement is not an <code>INSERT</code> statement.
 * <P>
 * An SQL statement with or without IN parameters can be
 * pre-compiled and stored in a <code>PreparedStatement</code> object. This
 * object can then be used to efficiently execute this statement
 * multiple times.
 * <P>
 * <B>Note:</B> This method is optimized for handling
 * parametric SQL statements that benefit from precompilation. If
 * the driver supports precompilation,
 * the method <code>prepareStatement</code> will send
 * the statement to the database for precompilation. Some drivers
 * may not support precompilation. In this case, the statement may
 * not be sent to the database until the <code>PreparedStatement</code>
 * object is executed.  This has no direct effect on users; however, it does
 * affect which methods throw certain SQLExceptions.
 * <P>
 * Result sets created using the returned <code>PreparedStatement</code>
 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
 *
 * @param sql an SQL statement that may contain one or more '?' IN
 *       parameter placeholders
 * @param columnIndexes an array of column indexes indicating the columns
 *       that should be returned from the inserted row or rows
 * @return a new <code>PreparedStatement</code> object, containing the
 *        pre-compiled statement, that is capable of returning the
 *        auto-generated keys designated by the given array of column
 *        indexes
 * @exception SQLException if a database access error occurs
 *
 * @since 1.4
 */
public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
    throw new Error("Not implemented yet");
}
/**
 * Creates a default <code>PreparedStatement</code> object capable
 * of returning the auto-generated keys designated by the given array.
 * This array contains the names of the columns in the target
 * table that contain the auto-generated keys that should be returned.
 * This array is ignored if the SQL
 * statement is not an <code>INSERT</code> statement.
 * <P>
 * An SQL statement with or without IN parameters can be
 * pre-compiled and stored in a <code>PreparedStatement</code> object. This
 * object can then be used to efficiently execute this statement
 * multiple times.
 * <P>
 * <B>Note:</B> This method is optimized for handling
 * parametric SQL statements that benefit from precompilation. If
 * the driver supports precompilation,
 * the method <code>prepareStatement</code> will send
 * the statement to the database for precompilation. Some drivers
 * may not support precompilation. In this case, the statement may
 * not be sent to the database until the <code>PreparedStatement</code>
 * object is executed.  This has no direct effect on users; however, it does
 * affect which methods throw certain SQLExceptions.
 * <P>
 * Result sets created using the returned <code>PreparedStatement</code>
 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
 *
 * @param sql an SQL statement that may contain one or more '?' IN
 *       parameter placeholders
 * @param columnNames an array of column names indicating the columns
 *       that should be returned from the inserted row or rows
 * @return a new <code>PreparedStatement</code> object, containing the
 *        pre-compiled statement, that is capable of returning the
 *        auto-generated keys designated by the given array of column
 *        names
 * @exception SQLException if a database access error occurs
 *
 * @since 1.4
 */
public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
    throw new Error("Not implemented yet");
}
/**
 * Creates a default <code>PreparedStatement</code> object that has
 * the capability to retrieve auto-generated keys. The given constant
 * tells the driver whether it should make auto-generated keys
 * available for retrieval.  This parameter is ignored if the SQL
 * statement is not an <code>INSERT</code> statement.
 * <P>
 * <B>Note:</B> This method is optimized for handling
 * parametric SQL statements that benefit from precompilation. If
 * the driver supports precompilation,
 * the method <code>prepareStatement</code> will send
 * the statement to the database for precompilation. Some drivers
 * may not support precompilation. In this case, the statement may
 * not be sent to the database until the <code>PreparedStatement</code>
 * object is executed.  This has no direct effect on users; however, it does
 * affect which methods throw certain SQLExceptions.
 * <P>
 * Result sets created using the returned <code>PreparedStatement</code>
 * object will by default be type <code>TYPE_FORWARD_ONLY</code>
 * and have a concurrency level of <code>CONCUR_READ_ONLY</code>.
 *
 * @param sql an SQL statement that may contain one or more '?' IN
 *       parameter placeholders
 * @param autoGeneratedKeys a flag indicating whether auto-generated keys
 *       should be returned; one of the following <code>Statement</code>
 *       constants:
 * @param autoGeneratedKeys a flag indicating that auto-generated keys should be returned, one of
 *       <code>Statement.RETURN_GENERATED_KEYS</code> or
 * 	      <code>Statement.NO_GENERATED_KEYS</code>.
 * @return a new <code>PreparedStatement</code> object, containing the
 *        pre-compiled SQL statement, that will have the capability of
 *        returning auto-generated keys
 * @exception SQLException if a database access error occurs
 *        or the given parameter is not a <code>Statement</code>
 *        constant indicating whether auto-generated keys should be
 *        returned
 * @since 1.4
 */
public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
    throw new Error("Not implemented yet");
}
   public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  return realConn.prepareStatement(sql, resultSetType, resultSetConcurrency);
   }
/**
 * Creates a <code>PreparedStatement</code> object that will generate
 * <code>ResultSet</code> objects with the given type, concurrency,
 * and holdability.
 * <P>
 * This method is the same as the <code>prepareStatement</code> method
 * above, but it allows the default result set
 * type, concurrency, and holdability to be overridden.
 *
 * @param sql a <code>String</code> object that is the SQL statement to
 *           be sent to the database; may contain one or more ? IN
 *           parameters
 * @param resultSetType one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.TYPE_FORWARD_ONLY</code>,
 *        <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or
 *        <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
 * @param resultSetConcurrency one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.CONCUR_READ_ONLY</code> or
 *        <code>ResultSet.CONCUR_UPDATABLE</code>
 * @param resultSetHoldability one of the following <code>ResultSet</code>
 *       constants:
 *        <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
 *        <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
 * @return a new <code>PreparedStatement</code> object, containing the
 *        pre-compiled SQL statement, that will generate
 *        <code>ResultSet</code> objects with the given type,
 *        concurrency, and holdability
 * @exception SQLException if a database access error occurs
 *           or the given parameters are not <code>ResultSet</code>
 *           constants indicating type, concurrency, and holdability
 * @see ResultSet
 * @since 1.4
 */
public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    throw new Error("Not implemented yet");
}
/**
 * Removes the given <code>Savepoint</code> object from the current
 * transaction. Any reference to the savepoint after it have been removed
 * will cause an <code>SQLException</code> to be thrown.
 *
 * @param savepoint the <code>Savepoint</code> object to be removed
 * @exception SQLException if a database access error occurs or
 *           the given <code>Savepoint</code> object is not a valid
 *           savepoint in the current transaction
 * @since 1.4
 */
public void releaseSavepoint(Savepoint savepoint) throws SQLException {
    throw new Error("Not implemented yet");
}
   public void rollback() throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  realConn.rollback();
   }
/**
 * Undoes all changes made after the given <code>Savepoint</code> object
 * was set.
 * <P>
 * This method should be used only when auto-commit has been disabled.
 *
 * @param savepoint the <code>Savepoint</code> object to roll back to
 * @exception SQLException if a database access error occurs,
 *           the <code>Savepoint</code> object is no longer valid,
 *           or this <code>Connection</code> object is currently in
 *           auto-commit mode
 * @see Savepoint
 * @see #rollback
 * @since 1.4
 */
public void rollback(Savepoint savepoint) throws SQLException 
{
	if (isClosed)
	{
	  throw new SQLException("Pooled connection is closed");
	}
   realConn.rollback( savepoint );
}
   public void setAutoCommit(boolean autoCommit) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  realConn.setAutoCommit(autoCommit);
   }
   public void setCatalog(String catalog) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  realConn.setCatalog(catalog);
   }
/**
 * Changes the holdability of <code>ResultSet</code> objects
 * created using this <code>Connection</code> object to the given
 * holdability.
 *
 * @param holdability a <code>ResultSet</code> holdability constant; one of
 *       <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
 *       <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
 * @throws SQLException if a database access occurs, the given parameter
 *        is not a <code>ResultSet</code> constant indicating holdability,
 *        or the given holdability is not supported
 * @see #getHoldability
 * @see ResultSet
 * @since 1.4
 */
public void setHoldability(int holdability) throws SQLException {
    throw new Error("Not implemented yet");
}
   public void setReadOnly(boolean readOnly) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  realConn.setReadOnly(readOnly);
   }
/**
 * Creates an unnamed savepoint in the current transaction and
 * returns the new <code>Savepoint</code> object that represents it.
 *
 * @return the new <code>Savepoint</code> object
 * @exception SQLException if a database access error occurs
 *           or this <code>Connection</code> object is currently in
 *           auto-commit mode
 * @see Savepoint
 * @since 1.4
 */
public Savepoint setSavepoint() throws SQLException 
{
	if (isClosed)
	{
	  throw new SQLException("Pooled connection is closed");
	}

	return realConn.setSavepoint();
}
/**
 * Creates a savepoint with the given name in the current transaction
 * and returns the new <code>Savepoint</code> object that represents it.
 *
 * @param name a <code>String</code> containing the name of the savepoint
 * @return the new <code>Savepoint</code> object
 * @exception SQLException if a database access error occurs
 *           or this <code>Connection</code> object is currently in
 *           auto-commit mode
 * @see Savepoint
 * @since 1.4
 */
public Savepoint setSavepoint(String name) throws SQLException 
{
	if (isClosed)
	{
	  throw new SQLException("Pooled connection is closed");
	}

	return realConn.setSavepoint( name );
}

   public void setTransactionIsolation(int level) throws SQLException
   {
	  if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  realConn.setTransactionIsolation(level);
   }
/**
 * Insert the method's description here.
 * Creation date: (1/3/00 9:49:49 AM)
 * @param map java.util.Map
 */
public void setTypeMap(Map map) throws SQLException  
{
	 if (isClosed)
	  {
		 throw new SQLException("Pooled connection is closed");
	  }
	  realConn.setTypeMap(map);
}
/**
 * Returns the name of the connection pool the connection came from.
 * Creation date: (1/17/00 11:50:21 AM)
 * @return java.lang.String
 */
public String toString() {
	return pool.getName();
}
}

package com.cannontech.ejb;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TransactionException;
import com.cannontech.database.TransactionType;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDBPersistent;

/**
 * @ejb:bean name="DBPersistent"
 * jndi-name="jndi/DBPersistentBean"
 * type="Stateful" 
**/
public class DBPersistentBean implements IDBPersistent {
    protected static final int ORACLE_FLOAT_PRECISION = SqlUtils.ORACLE_FLOAT_PRECISION;
    private static final Logger log = YukonLogManager.getLogger(DBPersistentBean.class);
    
    private java.sql.Connection dbConnection = null;   
    
    /**
     * @ejb:interface-method
     * tview-type="remote" 
     **/
    public DBPersistent execute( TransactionType operation, DBPersistent obj ) throws TransactionException
    {
        return internalExecute( operation, obj );
    }   
    
    private class TemporaryException extends RuntimeException {
        public final TransactionException e;
        public TemporaryException(TransactionException e) {
            this.e = e;
        }
        public TemporaryException(SQLException e) {
            this.e = new TransactionException("Caught exception while processing transaction",e);
        }
    }
    
    
    private DBPersistent internalExecute(final TransactionType operation, final DBPersistent object ) throws TransactionException
    {
        final boolean objectSuppliedConnection;
        if (object.getDbConnection() != null) {
            objectSuppliedConnection = true;
            setDbConnection(object.getDbConnection());
        } else {
            objectSuppliedConnection = false;
        }
        
        if (log.isDebugEnabled()) {
            log.debug("DBPersistentBean TrX started " + operation);
        }
        
        try {
            ThreadContext.push("DBPersistent=" + object);
            ThreadContext.push("objectSuppliedConnection=" + objectSuppliedConnection);
            TransactionTemplate tt = YukonSpringHook.getTransactionTemplate();
            tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            tt.execute(new TransactionCallback() {
                public Object doInTransaction(org.springframework.transaction.TransactionStatus status) {
                    Connection poolConnection = null;
                    try {
                        log.debug("Starting transaction: " + status);
                        if (getDbConnection() == null) {
                            poolConnection = PoolManager.getYukonConnection();
                            setDbConnection( poolConnection );
                            log.debug("Getting connection from PoolManager for transaction");
                        }
                        log.debug("Setting connection on object: " + getDbConnection());
                        object.setDbConnection( getDbConnection() );
                        log.debug("Using objectSuppliedConnection:{} connecton:{}", objectSuppliedConnection, getDbConnection());
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
                            log.debug("Deleting object:{} objectSuppliedConnection:{}", object, objectSuppliedConnection);
                            object.delete();
                            log.debug("Deleted object:{} objectSuppliedConnection:{}", object, objectSuppliedConnection);
                            break;
                            
                        case DELETE_PARTIAL:
                            object.deletePartial();
                            break;
                            
                        case ADD_PARTIAL: 
                            object.addPartial();
                            break;
                            
                        default:
                            throw new TemporaryException(new TransactionException("Unknown operation:  " + operation));
                        };
                    } catch (SQLException e) {
                        throw new TemporaryException(e);
                    } finally {
                        if (poolConnection != null) {
                            JdbcUtils.closeConnection(poolConnection);
                        }
                    }
                    return null;
                }
            });
            
        } catch (TemporaryException te) {
            // Yes, this is weird. Spring transactions assume
            // that you're throwing a RuntimeException.
            throw te.e;
        } finally {
            if( !objectSuppliedConnection ) {
                //DO NOT LET THE DBPERSISTENT OBJECT HOLD ONTO A REFERENCE TO THE CONNECTION
                //IT'S JUST A BAD IDEA SINCE WE DON'T KNOW HOW THEY ARE BEING MANAGED
                object.setDbConnection(null);
                setDbConnection(null);
            }
            ThreadContext.pop();
            ThreadContext.pop();
        }
        
      
      return object;
   }   
   
   
   public Connection getDbConnection() {
      return dbConnection;
   }
	
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void delete( String tableName, String columnNames[], String columnValues[] ) throws SQLException
   {
      String deleteString = "DELETE FROM " + tableName + " WHERE ";
   
      if( columnNames.length < 1 )
         return;
   
       deleteString += columnNames[0] + "=?";
   
       for( int i = 1; i < columnNames.length; i++ )
         deleteString += " AND " + columnNames[i] + "=?";
   
       PreparedStatement pstmt = null;
//     everything went well, print the SQL to a file if desired
       printSQLToFile( deleteString.toString(), columnValues, null, null );
       try
       {
         pstmt = getDbConnection().prepareStatement(deleteString.toString());
         for( int i = 0; i < columnValues.length; i++ )
             pstmt.setObject(i+1, columnValues[i]);          

         pstmt.executeUpdate();
       }
       catch( SQLException e )
       {
         throw e;
       }
       finally
       {
         if( pstmt != null )
            pstmt.close();
       }
   }


   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void delete( String tableName, String columnName, String columnValue ) throws SQLException
   {
      String deleteString = "DELETE FROM " + tableName + " WHERE " + columnName + "=?";
   
      PreparedStatement pstmt = null;
//    everything went well, print the SQL to a file if desired
      Object [] columnValues = new Object[]{columnValue};
      printSQLToFile( deleteString, columnValues, null, null );
      try
      {
         pstmt = getDbConnection().prepareStatement(deleteString);
         pstmt.setObject(1, columnValue);          

         pstmt.executeUpdate();
      }
      catch( SQLException e )
      {
         throw e;
      }
      finally
      {
         if( pstmt != null )
            pstmt.close();
      }
   }

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public Object[][] retrieve(String[] selectColumns, String tableName, String[] constraintColumns, String[] constraintValues, boolean multipleReturn) throws SQLException
   {
      String selectString = "SELECT ";
      if (selectColumns.length > 0)
      {
         selectString += selectColumns[0];
         for (int i = 1; i < selectColumns.length; i++)
            selectString += "," + selectColumns[i];
      }
      selectString += " FROM " + tableName;
      if (constraintColumns.length > 0)
      {
         selectString += " WHERE " + constraintColumns[0] + "=?";
         for (int j = 1; j < constraintColumns.length; j++)
            selectString += " AND " + constraintColumns[j] + "=?";
      }
      PreparedStatement pstmt = null;
      ResultSet rset = null;
      Object returnObjects[][] = null;
      try
      {
          pstmt = getDbConnection().prepareStatement(selectString.toString());
          for( int i = 0; i < constraintValues.length; i++ )
              pstmt.setObject(i+1, constraintValues[i]);          
          rset = pstmt.executeQuery();
   
         //Get all the rows
         Vector<Vector <Object>> rows = new Vector<Vector <Object>>();
         ResultSetMetaData metaData = rset.getMetaData();
         while (rset.next())
         {
            Vector<Object> columns = new Vector<Object>();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                columns.addElement( SqlUtils.getResultObject(rset, i) );
            }
            
            rows.addElement(columns);
         }
         returnObjects = new Object[rows.size()][rset.getMetaData().getColumnCount()];
         for (int i = 0; i < rows.size(); i++)
            for (int j = 0; j < rset.getMetaData().getColumnCount(); j++)
            {
				Object temp = ((java.util.Vector) rows.elementAt(i)).elementAt(j);
				if (temp instanceof BigDecimal)
				{
					// >>>>>>>>>> Watch this - synchronize with above!
					if (rset.getMetaData().getPrecision(j + 1) == ORACLE_FLOAT_PRECISION )
						temp = new Double(((BigDecimal) temp).doubleValue());
					else
						temp = new Integer(((BigDecimal) temp).intValue());
				}
                  
               returnObjects[i][j] = temp;
            }
      }
      catch (SQLException e)
      {
         throw e;
      }
      finally
      {
         if( rset != null )
            rset.close();
   
         if( pstmt != null )
            pstmt.close();
      }
      return returnObjects;
   }


   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public Object[] retrieve(String selectColumnNames[], String tableName, String keyColumnNames[], String keyColumnValues[]) throws SQLException
   {
      String selectString = "SELECT ";
      if (selectColumnNames.length > 0)
      {
         selectString += selectColumnNames[0];
         for (int i = 1; i < selectColumnNames.length; i++)
            selectString += "," + selectColumnNames[i];
      }
      
      selectString += " FROM " + tableName;
      if (keyColumnNames.length > 0)
      {
         selectString += " WHERE " + keyColumnNames[0] + "=?";
         for (int j = 1; j < keyColumnNames.length; j++)
            selectString += " AND " + keyColumnNames[j] + "=?";
      }
      
      PreparedStatement pstmt = null;
      ResultSet rset = null;
      Object returnObjects[] = null;
   
      try
      {
         pstmt = getDbConnection().prepareStatement(selectString.toString());
         for( int i = 0; i < keyColumnValues.length; i++ )
             pstmt.setObject(i+1, keyColumnValues[i]);          
         rset = pstmt.executeQuery();

         Vector<Object> v = new Vector<Object>();
         ResultSetMetaData metaData = rset.getMetaData();
         int columns = metaData.getColumnCount();
         if (rset.next())
            for (int k = 0; k < columns; k++)
            {
                v.addElement(SqlUtils.getResultObject(rset, k+1));
            }
         
         returnObjects = new Object[v.size()];
   
   
         /* WARNING - The code below converts primitives into objects
            ALL numeric columns seem to come back as java.math.BigDecimal
            This is the case using oracle's type 4(thin) jdbc driver!
            This _should probably_ just return the objects associated with
            numeric columns as BigDecimals
            and allow sub-classes to figure out what they really need.
            As it stands (12/98) there are a lot of sub-classes that rely on
            receiving Integer objects rather than java.math.BigDecimal objects.
            
            Well, how do we determine whether a java.math.BigDecimal was intended
            to be an Integer or Float or a Double or whatever?
         
            An easy hack (FOR THE CURRENT SETUP! 12/98) is to test the precision
            of the column using the java.sql.ResultSetMetaData interface.
            And this is what I did.  
            It so happens that an oracle 'INTEGER' type has a precision of 38,
            while an oracle 'FLOAT' type has a precision of 126. I know this can
            be manipulated using oracle specific stuff, BUT can it be manipulated
            in a cross platform way...? 
            Cheers, ACL 
            >:)
            */
   
   
         for (int n = 0; n < returnObjects.length; n++)
         {
            Object temp = v.elementAt(n);
            if (temp instanceof BigDecimal)
            {
               // >>>>>>>>>> Watch this - synchronize with above!
               if (rset.getMetaData().getPrecision(n + 1) == ORACLE_FLOAT_PRECISION )
                  temp = new Double(((BigDecimal) v.elementAt(n)).doubleValue());
               else
                  temp = new Integer(((BigDecimal) v.elementAt(n)).intValue());
            }
   /* Cant remember why this is here??? 7-29-2002 */
   /*       else
               if (temp instanceof byte[])
               {
                  if (((byte[]) temp).length == 1)
                     temp = new Byte(((byte[]) temp)[0]);
                  else
                  {
                     Byte newTemp[] = new Byte[ ((byte[]) temp).length];
                     for (int i = 0; i < ((byte[]) temp).length; i++)
                        newTemp[i] = new Byte(((byte[]) temp)[i]);
                     temp = newTemp;
                  }
               }
   */
            returnObjects[n] = temp;
         }
      }
      catch (SQLException e)
      {
         throw e;
      }
      finally
      {
         if (rset != null)
            rset.close();
   
         if (pstmt != null)
            pstmt.close();
      }
   
      return returnObjects;
   }
   
   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void update( String tableName, String constraintColumnName[], Object constraintColumnValue[]) throws SQLException
   {
      for( int i = 0; i < constraintColumnValue.length; i++ )
         constraintColumnValue[i] = substituteObject( constraintColumnValue[i] );
   
      StringBuffer pSql = new StringBuffer("UPDATE ");
      pSql.append(tableName);
      pSql.append(" SET ");
      pSql.append(constraintColumnName[0]);
      pSql.append(" = " + constraintColumnValue[0]);
   
      if( constraintColumnName.length > 0 )
      {
         pSql.append(" WHERE ");
         pSql.append(constraintColumnName[0]);
         pSql.append("=?");
   
      }
      
      
      PreparedStatement pstmt = null;
      
      try
      {
         pstmt = getDbConnection().prepareStatement(pSql.toString());
            
         for( int i = 0; i < constraintColumnValue.length; i++ ) {
            pstmt.setObject(i+1, constraintColumnValue[i]);
         }
         pstmt.executeUpdate();
         //everything went well, print the SQL to a file if desired
         printSQLToFile( pSql.toString(), null, constraintColumnValue, null );
 
      }
      catch( SQLException e )
      {
         throw e;
      }
      finally
      {
         if( pstmt != null ) pstmt.close();
      }  
   }



   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void update( String tableName, String setColumnName[], Object setColumnValue[], 
                                    String constraintColumnName[], Object constraintColumnValue[]) throws SQLException
   {
      for( int i = 0; i < setColumnValue.length; i++ )
         setColumnValue[i] = substituteObject( setColumnValue[i] );
   
      for( int i = 0; i < constraintColumnValue.length; i++ )
         constraintColumnValue[i] = substituteObject( constraintColumnValue[i] );
   
      StringBuffer pSql = new StringBuffer("UPDATE ");
      pSql.append(tableName);
      pSql.append(" SET ");
      pSql.append(setColumnName[0]);
      pSql.append("=?");
   
      for( int i = 1; i < setColumnName.length; i++ )
      {
         pSql.append(",");
         pSql.append(setColumnName[i]);
         pSql.append("=?");
      }
   
      if( constraintColumnName.length > 0 )
      {
         pSql.append(" WHERE ");
         pSql.append(constraintColumnName[0]);
         pSql.append("=?");
   
         for( int i = 1; i < constraintColumnName.length; i++ )
         {
            pSql.append(" AND ");
            pSql.append(constraintColumnName[i]);
            pSql.append("=?");
         }
      }
      
      PreparedStatement pstmt = null;
      
      try
      {
         pstmt = getDbConnection().prepareStatement(pSql.toString());
         for( int i = 0; i < setColumnValue.length; i++ )
         {
            /* The Oracle driver does not set the correct object for byte[]
             *  when calling the pstmt.setObject() method. If we do a call like
             *  pstmt.setObject(byte[]) an SQLException is thrown if the byte[]
             *  length > 4k.  */
            if( setColumnValue[i] instanceof byte[] )
            {
               ByteArrayInputStream bs = new ByteArrayInputStream( (byte[])setColumnValue[i] );
   
               pstmt.setBinaryStream( i+1, bs, ((byte[])setColumnValue[i]).length );
            }
            else
               pstmt.setObject(i+1, setColumnValue[i]);
         }
            
         for( int i = 0; i < constraintColumnValue.length; i++ )
            pstmt.setObject(i+setColumnValue.length+1, constraintColumnValue[i]);
            
         pstmt.executeUpdate();
         //everything went well, print the SQL to a file if desired
         printSQLToFile( pSql.toString(), setColumnValue, constraintColumnValue, null );

      }
      catch( SQLException e )
      {
         throw e;
      }
      finally
      {
         if( pstmt != null ) pstmt.close();
      }  
   }

   /**
    * @ejb:interface-method
    *	tview-type="remote" 
   **/
   public void add( String tableName, Object values[] ) throws SQLException
   {      
      for( int i = 0; i < values.length; i++ )
         values[i] = substituteObject(values[i]);
   
      StringBuffer pSql = new StringBuffer("INSERT INTO ");
      pSql.append(tableName);
      pSql.append(" VALUES( ?");
      
      for( int j = 1; j < values.length; j++ )
         pSql.append(",?");
   
      pSql.append(")");
      
      PreparedStatement pstmt = null;
   
      try
      {
         pstmt = getDbConnection().prepareStatement(pSql.toString());
   
   /* ************************************************************************************
    * This commented out block would be nice to add to all DBPersistence objects.
    * By adding this code, each DBPersistence object could query the DB for 
    * the table metadata. With this meatadata, we could allow the insertion of nulls.
    * Also, since the metadata is the table structure, we could remove most metatdata
    * about the DBPersitence object (such as COLUMN_NAMES, TABLE_NAME, etc).
    * Every DBPersistence object would need to do this metatdata query.  The data could
    * be static so the query is only done once.  --RWN 7-30-2002
    * ************************************************************************************
   /*    j(1) = ryan
         j(2) = dbo
         j(3) = StateImage
         j(4) = ImageID
         j(5) = 2
         j(6) = numeric
         j(7) = 18
         j(8) = 20
         j(9) = 0
         j(10) = 10
         j(11) = 0
         j(12) = null
         j(13) = null
         j(14) = 2
         j(15) = null
         j(16) = null
         j(17) = 1
         j(18) = NO      
   */
   /*      ResultSet rs = getDbConnection().getMetaData().getColumns(
                     null, 
                     null, 
                     tableName.toUpperCase(), 
                     "%" );
   
         int[] sh = new int[values.length];
         int i = 0;
         while( rs.next() ) 
         {           
            for( int j = 1; j <= 18; j++ )
               System.out.println("j("+j+") = " + rs.getString(j) );
               
            sh[i++] = rs.getInt(5);
         }
   
         
         for( int k = 0; k < values.length; k++ )
         {
            pstmt.setObject(
               k+1, 
               values[k],
               sh[k] );
         }
   */
         for( int k = 0; k < values.length; k++ )
         {
            /* The Oracle driver does not set the correct object for byte[]
             *  when calling the pstmt.setObject() method. If we do a call like
             *  pstmt.setObject(byte[]) an SQLException is thrown if the byte[]
             *  length > 4k.  */
            if( values[k] instanceof byte[] )
            {
               ByteArrayInputStream bs = new ByteArrayInputStream( (byte[])values[k] );
   
               pstmt.setBinaryStream( k+1, bs, ((byte[])values[k]).length );
            }
            else
               pstmt.setObject( k+1, values[k] );         
         }
   
         pstmt.executeUpdate();
   
         //everything went well, print the SQL to a file if desired
         printSQLToFile( pSql.toString(), values, null, null );
      }
      catch( SQLException e )
      {
         //something bad happened, print the SQL with the Exception to a file if desired
         printSQLToFile( pSql.toString(), values, null, e );
         
         throw e;
      }
      finally
      {
         pstmt.close();
      }
      //add( tableName, strValues );
   }


   private Object substituteObject(Object o) 
   {
      if( o == null )
      {
         return null;
      }
      else
      if( o instanceof Character )
      {
          String str = StringUtils.trimSpaces(o.toString());
      	if( str == null || str.length() <= 0 )
      	{
      		log.warn("A null value was found in a DBPersistent object, using a default value of ' '  (blank char)");
      		str = " ";
      	}
      	
         return str;
      }
      else
      if( o instanceof GregorianCalendar )
      {
         Timestamp ts = new Timestamp( ((GregorianCalendar)o).getTime().getTime());

         return ts;
      }
      else if( o instanceof Date )
      {
         Timestamp ts = new Timestamp( ((Date)o).getTime() );
         return ts;
      }
      else
      if( o instanceof Long )
      {
         return new BigDecimal( ((Long) o).longValue() );
      }
      else
      if( o instanceof String )
      {
          String str = StringUtils.trimSpaces(o.toString());
      	if( str == null || str.length() <= 0 )
      	{
      		log.warn("A null value was found in a DBPersistent object, using a default value of ' '  (blank char)");
      		str = " ";
      	}
      	
         return str;
      }
      else
         return o;
   }
   
   private void printSQLToFile(String line, Object[] columnValues, Object[] constraintValues, SQLException exception )
   {
       Logger logger = YukonLogManager.getLogger("com.cannontech.printsqlfile");
       
       if (!logger.isDebugEnabled()) {
           return;
       }
       
       StringBuffer buffer = new StringBuffer(line);
       boolean missingColumnValue = false;
       boolean missingConstraintValue = false;
       
       if( columnValues != null )
       {
           buffer = new StringBuffer(line);
           for( int i = 0; i < columnValues.length; i++ )
           {
               int index = buffer.toString().indexOf("?");
               if( index != -1 )
                   buffer = buffer.replace( index, index+1, prepareObjectForSQLStatement(columnValues[i]).toString() );
               else
                   missingColumnValue = true;
           }
       }
       
       if( constraintValues != null )
       {
           for( int i = 0; i < constraintValues.length; i++ )
           {
               int index = buffer.toString().indexOf("?");
               if( index != -1 )
                   buffer = buffer.replace( index, index+1, prepareObjectForSQLStatement(constraintValues[i]).toString() );
               else
                   missingConstraintValue = true;
           }
       }
       if( missingColumnValue )
           buffer.insert(0, "/*** MISSING COLUMN VALUE FOUND IN THE BELOW STATEMENT */\n");
       if( missingConstraintValue )
           buffer.insert(0, "/*** MISSING CONSTRAINT VALUE FOUND IN THE BELOW STATEMENT */\n");
       
       logger.debug(buffer);
       
   }
   
   private static String prepareObjectForSQLStatement( Object o ) 
   {
      if( o == null )
    	  return "NULL";
      else
      if( o instanceof Integer ||
          o instanceof Double ||
          o instanceof Long || 
          o instanceof BigDecimal ||
          o instanceof Character ||
          o instanceof String ||
          o instanceof Byte )
          return o.toString();
      else
      if( o instanceof java.sql.Date )
      {
         return "NULL";
      }
      else
      if( o instanceof GregorianCalendar || o instanceof Date )
      {
         if( o instanceof GregorianCalendar )
            o = ((GregorianCalendar)o).getTime();
   
          return "'" + new Timestamp( ((java.util.Date)o).getTime() ).toString() + "'";
      }
      else
      {
         log.info("prepareObjectForSQLStatement - warning unhandled type");
         return o.toString();
      }
      
   }
   

   public void setDbConnection(java.sql.Connection newValue) 
   {
      dbConnection = newValue;
   }

}

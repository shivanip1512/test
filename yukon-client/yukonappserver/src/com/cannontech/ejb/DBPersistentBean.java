package com.cannontech.ejb;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;

import com.cannontech.clientutils.CTILogManager;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.StringUtils;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.yukon.IDBPersistent;

/**
 * @ejb:bean name="DBPersistent"
 * jndi-name="jndi/DBPersistentBean"
 * type="Stateful" 
**/
public class DBPersistentBean implements IDBPersistent
{
	protected static final int ORACLE_FLOAT_PRECISION = SqlUtils.ORACLE_FLOAT_PRECISION;
	
   //public static String sqlFileName = null; 
   //com.cannontech.common.util.CtiUtilities.getLogDirPath() + "DatabaseSQL.sql";

   private java.sql.Connection dbConnection = null;   

   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public DBPersistent execute( int operation, DBPersistent obj ) throws TransactionException
   {
      return internalExecute( operation, obj );
   }   


   private DBPersistent internalExecute(int operation, DBPersistent object ) throws TransactionException
   {
      boolean autoCommit = false;
      java.sql.Connection conn = null;

      try
      {
         conn = object.getDbConnection();
         
         if( conn == null )
            setDbConnection( PoolManager.getInstance().getConnection( CtiUtilities.getDatabaseAlias() ) );         
         else
            setDbConnection( conn );


         try {
         com.cannontech.clientutils.CTILogger.debug( 
         "   DB: DBPersistentBean TrX started " +
         (operation == INSERT ? "(insert)" :
         (operation == UPDATE ? "(update)":
         (operation == RETRIEVE ? "(retrieve)":
         (operation == DELETE ? "(delete)":
         (operation == DELETE_PARTIAL? "(delete partial)":
         (operation == ADD_PARTIAL ? "(add partial)": 
			"(unknown)"))))))         
         + " " + getDbConnection().hashCode() );
         } catch( Throwable t ) {}
         


         autoCommit = getDbConnection().getAutoCommit();
         
         //only do this if the caller did not provide a DB connection on the DBPersistent
         if( conn == null )
             getDbConnection().setAutoCommit(false);

         object.setDbConnection( getDbConnection() );
               
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

         if( conn == null )
            getDbConnection().commit();  //autocommit is still false!!
      }
      catch( java.sql.SQLException e )
      {
         TransactionException t = new TransactionException( e.getMessage() );
         /*Only in JRE 1.4 or greater */
         t.setStackTrace( e.getStackTrace() );
         
         if( conn == null )
         {
            //Attempt a rollback
            try
            {
               getDbConnection().rollback();
            }
            catch( java.sql.SQLException e2 )
            {
               t = new TransactionException( e.getMessage() );
               /* Only in JRE 1.4 or greater */
               t.setStackTrace( e2.getStackTrace() );
               throw t;
            }
            
         }
         
         throw t;
      }
      finally
      {
         if( conn == null )
         {
            try
            {
               if( getDbConnection() != null )
               {                  
                  getDbConnection().setAutoCommit(autoCommit);
                  getDbConnection().close();
               }
   
            }
            catch( java.sql.SQLException e )
            {
               CTILogger.error( e.getMessage(), e );
            }         
            
            //DO NOT LET THE DBPERSISTENT OBJECT HOLD ONTO A REFERENCE TO THE CONNECTION
            //IT'S JUST A BAD IDEA SINCE WE DON'T KNOW HOW THEY ARE BEING MANAGED
            object.setDbConnection(null);
            setDbConnection(null);
         }
      }
   
      
      return object;
   }   
   
   
   public Connection getDbConnection() throws SQLException 
   {
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
   
       deleteString += columnNames[0] + "=" + columnValues[0];
   
       for( int i = 1; i < columnNames.length; i++ )
         deleteString += " AND " + columnNames[i] + "=" + columnValues[i];
   
       java.sql.Statement stmt = null;
       
       try
       {
         stmt = getDbConnection().createStatement();
         stmt.executeUpdate(deleteString);
       }
       catch( SQLException e )
       {
         throw e;
       }
       finally
       {
         if( stmt != null )
            stmt.close();
       }
   }


   /**
    * @ejb:interface-method
    * tview-type="remote" 
   **/
   public void delete( String tableName, String columnName, String columnValue ) throws SQLException
   {
      String deleteString = "DELETE FROM " + tableName + " WHERE " + columnName + "=" + columnValue;
   
      java.sql.Statement stmt = null;
   
      try
      {
         stmt = getDbConnection().createStatement();
         stmt.executeUpdate(deleteString);
      }
      catch( SQLException e )
      {
         throw e;
      }
      finally
      {
         if( stmt != null )
            stmt.close();
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
         selectString += " WHERE " + constraintColumns[0] + "=" + constraintValues[0];
         for (int j = 1; j < constraintColumns.length; j++)
            selectString += " AND " + constraintColumns[j] + "=" + constraintValues[j];
      }
      Statement stmt = null;
      ResultSet rset = null;
      Object returnObjects[][] = null;
      try
      {
         stmt = getDbConnection().createStatement();
         rset = stmt.executeQuery(selectString);
   
         //Get all the rows
         Vector rows = new Vector();
         while (rset.next())
         {
            Vector columns = new Vector();
            for (int i = 1; i <= rset.getMetaData().getColumnCount(); i++)
                  columns.addElement( rset.getObject(i) );
            
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
   
         if( stmt != null )
            stmt.close();
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
         selectString += " WHERE " + keyColumnNames[0] + "=" + keyColumnValues[0];
         for (int j = 1; j < keyColumnNames.length; j++)
            selectString += " AND " + keyColumnNames[j] + "=" + keyColumnValues[j];
      }
      
      Statement stmt = null;
      ResultSet rset = null;
      Object returnObjects[] = null;
   
      try
      {
         stmt = getDbConnection().createStatement(); //PROB!!!
         rset = stmt.executeQuery(selectString);

         Vector v = new Vector();
         int columns = rset.getMetaData().getColumnCount();
         if (rset.next())
            for (int k = 0; k < columns; k++)
            {
               //       if( rset.getObject(k+1) != null )
               v.addElement(rset.getObject(k + 1));
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
   
         if (stmt != null)
            stmt.close();
      }
   
      return returnObjects;
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
         printSQLToFile( pSql.toString(), values, null );
      }
      catch( SQLException e )
      {
         //something bad happened, print the SQL with the Exception to a file if desired
         printSQLToFile( pSql.toString(), values, e );
         
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
      		CTILogger.warn("A null value was found in a DBPersistent object, using a default value of ' '  (blank char)");
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
      		CTILogger.warn("A null value was found in a DBPersistent object, using a default value of ' '  (blank char)");
      		str = " ";
      	}
      	
         return str;
      }
      else
         return o;
   }
   
   private void printSQLToFile(String line, Object[] columnValues, SQLException exception )
   {

	String printSQLfile =
		CTILogManager.ALL_NAMES[CTILogManager.PRINT_SQL_INSERTS_FILE][1]; 

   	if( printSQLfile == null || printSQLfile.trim().length() <= 0 )
   		return;

      // Here we want to print all SQL to a file, creating a
      // script file that could be run later.
      java.io.PrintWriter pw = null;
   
      try
      {
         StringBuffer buffer = new StringBuffer(line);
         boolean missingColumnValue = false;
         
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
   
         if( missingColumnValue )
            buffer.insert(0, "/*** MISSING COLUMN VALUE FOUND IN THE BELOW STATEMENT */\n");
            
         pw = new java.io.PrintWriter(new java.io.FileWriter( printSQLfile, true), true);
         pw.write(buffer + "; \r\n");
         pw.close();
      }
      catch (Exception e) //catch everything and write the Exception to the log file
      {
         if( e instanceof java.io.IOException )
            CTILogger.info("*** Cant find SQL Log file named : " + printSQLfile +
                  " : " + e.getMessage() );
         else
         {
            if( pw != null )
               pw.write("/**** Caught EXCEPTION while trying to write to SQLFile : " +
                  " : " + e.getMessage() + "*/" );
         }
         
      }
      finally
      {
         if( pw != null )
            pw.close();
      }
   
   }
   
   private static String prepareObjectForSQLStatement( Object o ) 
   {
      if( o == null )
         return null;
      else
      if( o instanceof Integer ||
          o instanceof Double ||
          o instanceof Long || 
          o instanceof BigDecimal )
         return o.toString();
      else
      if( o instanceof Character ||
         o instanceof String ||
         o instanceof Byte )
         return "'" + o.toString() + "'";
      else
      if( o instanceof java.sql.Date )
      {
         return new String("NULL").trim();
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
         CTILogger.info("prepareObjectForSQLStatement - warning unhandled type");
         return o.toString();
      }
      
   }
   

   public void setDbConnection(java.sql.Connection newValue) 
   {
      dbConnection = newValue;
   }

}

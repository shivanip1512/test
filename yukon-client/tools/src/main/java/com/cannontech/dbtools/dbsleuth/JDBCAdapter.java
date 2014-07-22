package com.cannontech.dbtools.dbsleuth;

/**
 * An adaptor, transforming the JDBC interface to the TableModel interface.
 *
 */

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.PoolManager;
import com.cannontech.database.SqlUtils;

public class JDBCAdapter extends AbstractTableModel
{
    private Connection connection;
	private Statement statement;
	private ResultSet resultSet;
	private String[] columnNames = {};
	private Vector rows = new Vector();
	private ResultSetMetaData metaData;

    public JDBCAdapter()
    {
    	try
    	{
    		CTILogger.info("Opening db connection");
    
    		connection = PoolManager.getInstance().getConnection(
    			CtiUtilities.getDatabaseAlias() );
    
    		statement = connection.createStatement();
    	}
    	catch (SQLException ex) {
    		CTILogger.error("Cannot connect to this database.", ex);
    	}
    	catch( Exception e )
    	{
    		CTILogger.error( e.getMessage(), e );
    	}
    	
    }

    public void close() throws SQLException 
    {
    	CTILogger.info("Closing db connection");
    
    	if( resultSet != null )
    		resultSet.close();
    		
    	if( statement != null )
    		statement.close();
    		
    	if( connection != null )
    		connection.close();
    }

	public String dbRepresentation(int column, Object value)
    {
		int type;

		if (value == null) {
			return "null";
		}

		try {
			type = metaData.getColumnType(column+1);
		}
		catch (SQLException e) {
			return value.toString();
		}

		switch(type) {
		case Types.INTEGER:
		case Types.DOUBLE:
		case Types.FLOAT:
			return value.toString();
		case Types.BIT:
			return ((Boolean)value).booleanValue() ? "1" : "0";
		case Types.DATE:
			return value.toString(); // This will need some conversion.
		default:
			return "\""+value.toString()+"\"";
		}

	}

    public void executeQuery(String query)
    {
       if (connection == null || statement == null)
       {
          CTILogger.info("There is no database to execute the query.");
    	  return;
       }
    
     
       try
       {
    	  resultSet = statement.executeQuery(query);
    	  metaData = resultSet.getMetaData();
    
    	  int numberOfColumns = metaData.getColumnCount();
    	  columnNames = new String[numberOfColumns];
    	  // Get the column names and cache them.
    	  // Then we can close the connection.
    	  for (int column = 0; column < numberOfColumns; column++)
    	  {
    		 columnNames[column] = metaData.getColumnLabel(column + 1);
    	  }
    
    	  // Get all rows.
    	  rows = new Vector();
    	  while (resultSet.next())
    	  {
    		 Vector newRow = new Vector();
    		 for (int i = 1; i <= getColumnCount(); i++)
    		 {
    			 newRow.addElement(SqlUtils.getResultObject(resultSet, i));
    		 }
    		 rows.addElement(newRow);
    	  }
    	  //  close(); Need to copy the metaData, bug in jdbc:odbc driver.
    	  fireTableChanged(null); // Tell the listeners a new table has arrived.
    
       }
       catch (SQLException ex)
       {
           CTILogger.error("An error has occurred", ex);
       }
    }

	protected void finalize() throws Throwable
    {
		close();
		super.finalize();
	}

	public Class getColumnClass(int column)
    {
		int type;
		try {
			type = metaData.getColumnType(column+1);
		}
		catch (SQLException e) {
			return super.getColumnClass(column);
		}

		switch(type) {
		case Types.CHAR:
		case Types.VARCHAR:
		case Types.LONGVARCHAR:
			return String.class;

		case Types.BIT:
			return Boolean.class;

		case Types.TINYINT:
		case Types.SMALLINT:
		case Types.INTEGER:
			return Integer.class;

		case Types.BIGINT:
			return Long.class;

		case Types.FLOAT:
		case Types.DOUBLE:
			return Double.class;

		case Types.DATE:
			return java.sql.Date.class;

		default:
			return Object.class;
		}
	}

	public int getColumnCount()
    {
		return columnNames.length;
	}
	//////////////////////////////////////////////////////////////////////////
	//
	//             Implementation of the TableModel Interface
	//
	//////////////////////////////////////////////////////////////////////////

	// MetaData

	public String getColumnName(int column) {
		if (columnNames[column] != null) {
			return columnNames[column];
		} else {
			return "";
		}
	}
	// Data methods

	public int getRowCount() {
		return rows.size();
	}
	public Object getValueAt(int aRow, int aColumn) {
		Vector row = (Vector)rows.elementAt(aRow);
		return row.elementAt(aColumn);
	}
	public boolean isCellEditable(int row, int column) {
		try {
			return metaData.isWritable(column+1);
		}
		catch (SQLException e) {
			return false;
		}
	}

    public void setValueAt(Object value, int row, int column)
    {
       try
       {
    	  String tableName = metaData.getTableName(column + 1);
    	  // Some of the drivers seem buggy, tableName should not be null.
    	  if (tableName == null)
    	  {
    		 CTILogger.info("Table name returned null.");
    	  }
    	  String columnName = getColumnName(column);
    	  String query =
    		 "update "
    			+ tableName
    			+ " set "
    			+ columnName
    			+ " = "
    			+ dbRepresentation(column, value)
    			+ " where ";
    	  // We don't have a model of the schema so we don't know the
    	  // primary keys or which columns to lock on. To demonstrate
    	  // that editing is possible, we'll just lock on everything.
    	  for (int col = 0; col < getColumnCount(); col++)
    	  {
    		 String colName = getColumnName(col);
    		 if (colName.equals(""))
    		 {
    			continue;
    		 }
    		 if (col != 0)
    		 {
    			query = query + " and ";
    		 }
    		 query = query + colName + " = " + dbRepresentation(col, getValueAt(row, col));
    	  }
    	  CTILogger.info(query);
    	  CTILogger.info("Not sending update to database");
    	  // statement.executeQuery(query);
       }
       catch (SQLException e)
       {
    	  //     CTILogger.error( e.getMessage(), e );
           CTILogger.error("Update failed", e);
       }

       Vector dataRow = (Vector) rows.elementAt(row);
       dataRow.setElementAt(value, column);
    
    }

}

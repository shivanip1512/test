package com.cannontech.database.db;

/**
 * This type was created in VisualAge.
 */
import java.util.Vector;
import java.sql.*;

import javax.swing.event.*;

public abstract class DBPersistentTableModel extends javax.swing.table.AbstractTableModel {

	private String visibleColumnNames[] = {};
	private String internalColumnNames[] = {};
	
	private Vector rows = new Vector();
	private Vector hiddenRows = new Vector();

	ResultSetMetaData   metaData;
	
	
/**
 * DBPersistentTableModel constructor comment.
 */
public DBPersistentTableModel() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void executeQuery() throws SQLException
{
	Connection conn = null;
	Statement stmt = null;
	ResultSet rset = null;
	
	try
	{
		conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
		stmt = conn.createStatement();

	if( getQuery() == null )
	{
		Error e = new Error("SQL Query String cannot be null");
		e.printStackTrace();
		throw e;
	}
	
	rset = stmt.executeQuery(getQuery());
	metaData = rset.getMetaData();

	int numberOfColumns = metaData.getColumnCount();

	Vector tempColumnNames = new Vector();
	
	for( int i = 1; i <= numberOfColumns; i++ )
		tempColumnNames.addElement( metaData.getColumnName(i) );
	
	if( getInvisibleColumnNames() != null )	
	for( int i = 0; i < getInvisibleColumnNames().length; i++ )
		tempColumnNames.removeElement( getInvisibleColumnNames()[i]);

	visibleColumnNames = new String[tempColumnNames.size()];
	tempColumnNames.copyInto(visibleColumnNames);

	rows = new Vector();
	hiddenRows = new Vector();
	
	while( rset.next() )
	{
		Vector newRow = new Vector();
		Vector newHiddenRow = new Vector();
		
		for( int i = 1; i <= numberOfColumns; i++ )
		{
		
			if( tempColumnNames.contains(metaData.getColumnName(i)) )
				newRow.addElement( com.cannontech.database.DatabaseTypes.convertDBToJavaType( rset.getObject(i) ));
			else
				newHiddenRow.addElement( com.cannontech.database.DatabaseTypes.convertDBToJavaType( rset.getObject(i) ));
		}

		rows.addElement(newRow);
		hiddenRows.addElement(newHiddenRow);
	}
	}
	catch( SQLException e )
	{
		throw e;		
	}
	finally
	{
		if( rset != null )
			rset.close();

		if( stmt != null )
			stmt.close();

		if( conn != null )
			conn.close();
	}
	
	fireTableChanged(null);
	
	
}
/**
 * This method was created in VisualAge.
 * @param newEvent TableModelEvent
 */
public void fireTableChanged(TableModelEvent newEvent) {
	super.fireTableChanged(newEvent);
	
}
/**
 * This method returns the values of all the columns of a given row
 * in (hopefully) the correct order so that the child class can
 * construct an appropriate DBPersistent.
 * @return java.lang.Object[]
 * @param row int
 */
protected Object[] getAllColumnValues(int row) {

	
	
	//Attempt to put the columns in order
	Vector val = new Vector();

	if( getInvisibleColumnNames() != null )
		for( int i = 0; i < getInvisibleColumnNames().length; i++)
			val.addElement( ((Vector)hiddenRows.elementAt(row)).elementAt(i) );

	for( int i = 0; i < getColumnCount(); i++ )
		val.addElement( ((Vector)rows.elementAt(row)).elementAt(i) );

	Object[] returnVal = new Object[val.size()];

	val.copyInto(returnVal);
	
	return returnVal;
}		
	public Class getColumnClass(int column) {

		//we should be returning a legit type
		//but since metaData cannot be trusted to
		//to be a valid reference at this point
		//do something simple
		return String.class;
	/*	int type;
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
		}*/
	}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {

	if( visibleColumnNames != null )
		return visibleColumnNames.length;
	else
		return 0;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param columnIndex int
 */
public String getColumnName(int columnIndex) {

	if( columnIndex > visibleColumnNames.length - 1 )
		return null;
	else
		return visibleColumnNames[columnIndex];
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String[]
 */
public String[] getInvisibleColumnNames()
{
	return null;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public abstract String getQuery();
/**
 * getRowCount method comment.
 */
public int getRowCount() {
	
	return rows.size();
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.DBPersistent
 * @param row int
 */
public abstract DBPersistent getValueAt(int row);
/**
 * getValueAt method comment.
 */
public Object getValueAt(int arg1, int arg2) {
	
	Vector v = (Vector)rows.elementAt(arg1);

	if( v == null )
		return null;
	else
		return v.elementAt(arg2);

}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param arg1 int
 * @param arg2 int
 */
public boolean isCellEditable(int arg1, int arg2) {
	return false;
}
/**
 * This method was created in VisualAge.
 * @param row int
 */
public void removeValueAt(int row)
{
	rows.removeElementAt(row);
	hiddenRows.removeElementAt(row);
	fireTableChanged(null);
}
/**
 * This method tranfers a row in one DBPersistentTableModel
 * to another.  Be warned that if the two DBPersistentTableModels
 * are not of the same Class then an exception is thrown
 * @param fromRow int
 * @param fromModel com.cannontech.database.db.DBPersistentTableModel
 * @param toRow int
 * @param toModel com.cannontech.database.db.DBPersistentTableModel
 */
public static void transferRow(int fromRow, DBPersistentTableModel fromModel, int toRow, DBPersistentTableModel toModel) {

	toModel.rows.insertElementAt( fromModel.rows.elementAt( fromRow ), toRow );
	toModel.hiddenRows.insertElementAt( fromModel.hiddenRows.elementAt( fromRow), toRow );
	
	fromModel.rows.removeElementAt( fromRow );
	fromModel.hiddenRows.removeElementAt( fromRow );
	
}
}

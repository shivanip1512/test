package com.cannontech.tdc.addpoints;
/**
 * Insert the type's description here.
 * Creation date: (3/13/00 11:44:56 AM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.utils.DataBaseInteraction;
import com.cannontech.tdc.utils.TDCDefines;

public class RightTableModel extends javax.swing.table.AbstractTableModel implements com.cannontech.common.gui.dnd.IDroppableTableModel
{
	private long currentDisplayNumber = com.cannontech.tdc.data.Display.UNKNOWN_DISPLAY_NUMBER;
	private static final String[] COLUMN_NAMES = {"Device Name", "Point Name"};

	//data for the rows in the table
	private Vector	rows = new Vector(10);


	public class Line
	{
		private long pointID = -1;
		private String deviceName = null;
		private String pointName = null;
	
		public Line( long id, String dName, String pName )
		{
			pointID = id;
			deviceName = dName;
			pointName = pName;	
		}

		public long getPointID()
		{
			return pointID;
		}
		public String getDeviceName()
		{
			return deviceName;
		}
		public String getPointName()
		{
			return pointName;
		}
	}
	
	public class BlankLine
	{
		private int location = 0;
		
		public BlankLine( int loc )
		{
			location = loc;	
		}

		public int getLocation()
		{
			return location;
		}

	}

/**
 * RigthTableModel constructor comment.
 */
public RightTableModel() 
{
	super();
}


/**
 * This method add a Blank row to the table
 */
 
public void addBlankRow( int location )
{
	if( location < 0 || exceededMaxRows() )
		return;
	
	Vector newRow = new Vector();
	
	if( location >= rows.size() )
	{		
		rows.addElement( new Line( TDCDefines.ROW_BREAK_ID, "BLANK", "BLANK" ) );
	}
	else
	{
		rows.insertElementAt( new Line( TDCDefines.ROW_BREAK_ID, "BLANK", "BLANK" ), location );
	}
	
	fireTableRowsInserted( location, location ); // Tell the listeners a new row has arrived.	
}


/**
 * This method was created in VisualAge.
 */
private String buildTwoColumnRowQuery()
{		
	return ( new String("select y.PAOName, p.pointName, p.pointid " +
					" from display2waydata d, " +
					com.cannontech.database.db.point.Point.TABLE_NAME + " p, " + 
					com.cannontech.database.db.pao.YukonPAObject.TABLE_NAME + " y" + 
					" where d.displaynum = " +
					currentDisplayNumber +
					" and d.pointid = p.pointid " +
					" and p.PAObjectID = y.PAObjectID " +
					" order by d.ordering") );
}


/**
 * Insert the method's description here.
 * Creation date: (1/31/2002 2:47:17 PM)
 * @return boolean
 */
public boolean exceededMaxRows() 
{
	return getRowCount() >= TDCDefines.MAX_ROWS;
}



/**
 * getColumnCount method comment.
 */
public int getColumnCount() 
{
	return COLUMN_NAMES.length;
}


/**
 * Insert the method's description here.
 * Creation date: (3/7/00 2:40:18 PM)
 * @return boolean
 * @param ptID java.lang.String
 */
public String getColumnName(int column) 
{
	return COLUMN_NAMES[column];
}


/**
 * Insert the method's description here.
 * Creation date: (3/7/00 2:40:18 PM)
 * @return boolean
 * @param ptID java.lang.String
 */
public long getPointID( int loc ) 
{
	return getRowAt(loc).getPointID();
}


/**
 * Insert the method's description here.
 * Creation date: (6/19/2002 9:12:46 AM)
 */
protected Line getRowAt( int row )
{
	return (Line)rows.get(row);
}


/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return rows.size();
}


/**
 * getValueAt method comment.
 */
public Object getValueAt(int aRow, int aColumn) 
{
	Line value = getRowAt(aRow);

	if( value.getPointID() == TDCDefines.ROW_BREAK_ID )
		return "";

	switch( aColumn )
	{
		case 0:
			return value.getDeviceName();

		case 1:
			return value.getPointName();

		default:
			return "NULL";
	}

}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION RightTableModel() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}


/**
 * This method creates the table
 */
 
public void insertNewRow ( String ptId )
{
	if( exceededMaxRows() )
		return;

	long pointid = Long.parseLong( ptId );

	
	LitePoint point = PointFuncs.getLitePoint( (int)pointid );
	
	rows.addElement( 
			new Line( pointid, 
					PAOFuncs.getYukonPAOName(point.getPaobjectID()), 
					point.getPointName() ) );


	fireTableDataChanged(); // Tell the listeners a new table has arrived.
}


/**
 * This method add a Blank row to the table
 */
 
public boolean isRowSelectedBlank( int location )
{

	return getPointID(location) == TDCDefines.ROW_BREAK_ID;
}


/**
 * This method creates the table
 */
 
public void makeTable ( )
{

	String query = new String();
	String ptID = null, devName = null, ptName = null;

	if ( !(query = buildTwoColumnRowQuery()).equals("") )
	{
		Object[][] values = DataBaseInteraction.queryResults( query, null );

		for( int i = 0; i < values.length; i++ ) 
		{	
			for (int j = 0; j <= getColumnCount(); j++) 
			{
				switch( j )
				{
					case 0:  // deviceName
					devName = (values[i][j].toString() == null ? "NULL" : values[i][j].toString());
					break;
					
					case 1:  // pointName
					ptName = (values[i][j].toString() == null ? "NULL" : values[i][j].toString());
					break;
					
					case 2:  // pointID
					ptID = (values[i][j].toString() == null ? "NULL" : values[i][j].toString());
					break;
				}

			}

			rows.addElement( new Line( Long.parseLong(ptID), devName, ptName ) );
		}				

		fireTableDataChanged(); // Tell the listeners a new table has arrived.			
	}


}


/**
 * Insert the method's description here.
 * Creation date: (3/7/00 2:40:18 PM)
 * @return boolean
 * @param ptID java.lang.String
 */
public boolean pointExists(String ptID) 
{

	for( int i = 0; i < getRowCount(); i++ )
	{
		if( getPointID(i) == Long.parseLong(ptID) )
			return true;
	}
			
	return false;
}


/**
 * This method add a Blank row to the table
 */
 
public void removeAllRows()
{
	if( rows != null )
	{
		rows.removeAllElements();
	}

	fireTableDataChanged();
}


/**
 * This method add a Blank row to the table
 */
 
public void removeSingleRow( int location )
{
	rows.removeElementAt( location );
	
	fireTableRowsDeleted( location, location ); // Tell the listeners a new row has arrived.
}


/**
 * getValueAt method comment.
 */
public void setCurrentDisplayNumber( long i )
{
	currentDisplayNumber = i;
}
}
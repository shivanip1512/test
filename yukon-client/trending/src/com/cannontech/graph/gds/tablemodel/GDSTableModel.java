package com.cannontech.graph.gds.tablemodel;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.cannontech.common.gui.util.Colors;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.db.graph.GDSTypes;
import com.cannontech.database.db.graph.GDSTypesFuncs;
import com.cannontech.database.db.graph.GraphDataSeries;
import com.cannontech.util.ServletUtil;

/**
 * Insert the type's description here.
 * Creation date: (10/23/2001 9:10:18 AM)
 * @author: 
 */
public class GDSTableModel extends javax.swing.table.AbstractTableModel
{
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int POINT_NAME_COLUMN = 1;
	public final static int LABEL_NAME_COLUMN = 2;
	public final static int COLOR_NAME_COLUMN = 3;
	public final static int AXIS_NAME_COLUMN = 4;
	public final static int TYPE_NAME_COLUMN = 5;
	public final static int MULT_NAME_COLUMN = 6;
//	public final static int SETUP_NAME_COLUMN = 7;
	
	public static String[] columnNames =
	{
		"Device",
		"Point",
		"Label",
		"Color",
		"Axis",
		"Type",
		"Multiplier"
//		, "Setup"
	};

	public static Class[] columnTypes =
	{
		String.class,
		String.class,
		String.class,
		java.awt.Color.class,
		String.class,
		javax.swing.JCheckBox.class,
		Double.class
//		, String.class
	};	

	//Graph data series rows
	public java.util.ArrayList rows = new java.util.ArrayList(10);
	
/**
 * DataSeriesTableModel constructor comment.
 */
public GDSTableModel()
{
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 9:22:50 AM)
 * @param gds com.cannontech.database.db.graph.GraphDataSeries
 */
/**
 * Adds the Graph_Series gds to model.rows array
 * Adds the Peak_Series gds to model.excludedRows array
 * Creation date: (10/25/00 11:21:21 AM)
 */
public void addRow(GraphDataSeries gds) 
{
	getRows().add(gds);
	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 11:03:39 AM)
 * @return com.cannontech.database.db.graph.GraphDataSeries[]
 */
public GraphDataSeries[] getAllDataSeries()
{
	int gdsArraySize = getRowCount();
		
	GraphDataSeries[] retVal = new GraphDataSeries[ gdsArraySize];

	java.util.Iterator iter = getRows().iterator();

	int i = 0;
	while( iter.hasNext() )
	{
		retVal[i++] = (GraphDataSeries) iter.next();
	}

	return retVal;
}
/**
 * Insert the method's description here.
 * Creation date: (10/26/00 1:19:28 PM)
 * @return java.lang.Class
 * @param column int
 */
public Class getColumnClass(int column)
{
	return getColumnTypes()[column];
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() 
{
	return getColumnNames().length;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 2:54:48 PM)
 * @return java.lang.String
 * @param col int
 */
public String getColumnName(int col) {
	return getColumnNames()[col];
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:10:08 PM)
 * @return java.lang.Class[]
 */
public String[] getColumnNames()
{
	return columnNames;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:10:08 PM)
 * @return java.lang.Class[]
 */
public Class[] getColumnTypes()
{
	return columnTypes;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 11:26:55 AM)
 * @return java.lang.Object
 * @param index int
 */
public Object getGDSAttribute(int index, GraphDataSeries gds) {
	switch( index )
	{
		case DEVICE_NAME_COLUMN:
			return gds.getDeviceName();
			
		case POINT_NAME_COLUMN:
			Integer id = gds.getPointID();
			
			//must not have found it so we'll try some predefined points too.
			if( id.intValue() == PointTypes.SYS_PID_THRESHOLD)
				return "Threshold";
			
			LitePoint pt = PointFuncs.getLitePoint( id.intValue() );
			if( pt != null )
				return pt.getPointName();	
		break;
		
		case LABEL_NAME_COLUMN:
			return gds.getLabel();
			
		case COLOR_NAME_COLUMN:	
			return Colors.getColorString(gds.getColor().intValue());
		
		
		case AXIS_NAME_COLUMN:
			Character axis = gds.getAxis();

			if( axis.charValue() == 'R' || axis.charValue() == 'r' )
				return "Right";
			else
				return "Left";

		case TYPE_NAME_COLUMN:
		{
			//Display the String value of the int
			String type = GDSTypesFuncs.getType(gds.getType().intValue());
			if( type.equalsIgnoreCase(GDSTypes.DATE_TYPE_STRING))
			{
				java.text.SimpleDateFormat format = new java.text.SimpleDateFormat("MM/dd/yy");
				//get timestamp from database;
				if( !CtiUtilities.STRING_NONE.equalsIgnoreCase(gds.getMoreData()))
					type = format.format(gds.getSpecificDate());
				else
					type = "mm/dd/yy";
			}
			return type;
		}
		case MULT_NAME_COLUMN:
			return gds.getMultiplier();

/*		case SETUP_NAME_COLUMN:
			return "...";
*/			
	}
	return null;
}

/**
 * Insert the method's description here.
 * Creation date: (10/25/00 11:21:02 AM)
 * @return com.cannontech.database.db.graph.GraphDataSeries
 * @param row int
 */
public GraphDataSeries getRow(int row)
{
	return ( row < getRows().size() ? (GraphDataSeries) getRows().get(row) : null );
}
/**
 * getRowCount method comment.
 */
public int getRowCount()
{
	return getRows().size();
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:05:03 PM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getRows()
{
	return rows;
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col)
{
	if( row < getRows().size() && col < getColumnCount() )
	{
		return getGDSAttribute(col, (GraphDataSeries) getRows().get(row) );
	}
	else
		return null;
			
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 9:26:48 AM)
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column)
{
	boolean editable = false;
	if( column == LABEL_NAME_COLUMN ||
		column == COLOR_NAME_COLUMN || 
		column == AXIS_NAME_COLUMN ||
		column == MULT_NAME_COLUMN )
	{
		editable = true;
	}
	else if( column == TYPE_NAME_COLUMN )
	{
		String value = ((String)getValueAt(row,column));
		
		//When type column value is 'THRESHOLD', the combo box is NOT editable.
		if( !value.equalsIgnoreCase( GDSTypes.THRESHOLD_TYPE_STRING))
			editable = true;
	}
		
	
	return editable;
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 4:42:04 PM)
 * @param row int
 */
public void removeRow(int[] row) 
{
	Object[] toRemove = new Object[row.length]; 

	for( int i = 0; i < row.length; i++ )
		toRemove[i] = getRows().get( row[i] );

	for( int i = 0; i < toRemove.length; i++ )
		getRows().remove(toRemove[i]);

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 4:42:04 PM)
 * @param row int
 */
public void removeRow(int row) 
{
	if( row < getRows().size() )
		getRows().remove(row);

	fireTableDataChanged();
}
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 11:26:04 AM)
 * @param gds com.cannontech.database.db.graph.GraphDataSeries[]
 */
public void setDataSeries(GraphDataSeries[] gds) {}
/**
 * Insert the method's description here.
 * Creation date: (11/6/00 10:07:31 AM)
 * @param value java.lang.Object
 * @param row int
 * @param col int
 */
public void setValueAt(Object value, int row, int col) 
{	
	GraphDataSeries gds = getRow(row);
	
	switch( col )
	{
		case LABEL_NAME_COLUMN:
			String valueString = value.toString();
			if (valueString.length() > 40)
				valueString = valueString.substring(0, 39);
			gds.setLabel( valueString );
		break;
		case COLOR_NAME_COLUMN:
			gds.setColor( new Integer(Colors.getColorID(Colors.getColor(value.toString()))));
			
		break;		
		case AXIS_NAME_COLUMN:
			gds.setAxis( new Character(Character.toUpperCase(value.toString().charAt(0))));;
		break;
		
		case TYPE_NAME_COLUMN:
		{
			//Save the Integer value of the String.
			gds.setType( new Integer(GDSTypesFuncs.getTypeInt(new String(value.toString()))));
			
			String moreData = CtiUtilities.STRING_NONE;
			if( GDSTypesFuncs.isDateType(gds.getType().intValue()))
			{
				java.util.Date date = ServletUtil.parseDateStringLiberally(value.toString());
				if(date != null)
				{
					Long ts = new Long(date.getTime());
					moreData = String.valueOf(ts);
				}
			}
			else if( GDSTypesFuncs.isPeakType(gds.getType().intValue()))
			{
				if( CtiUtilities.STRING_NONE.equalsIgnoreCase(gds.getMoreData()))
				{
					GregorianCalendar peakCal = new GregorianCalendar();
					/*TODO THIS IS ONLY TEMPORARY FOR ACCESSING DATA 
					peakCal.set(Calendar.MONTH, 11);
					peakCal.set(Calendar.YEAR, 2003);
					/*********/
					
					peakCal.set(Calendar.DATE, 1);
					peakCal.set(Calendar.HOUR, 0);
					peakCal.set(Calendar.MINUTE, 0);
					peakCal.set(Calendar.SECOND, 0);
					peakCal.set(Calendar.MILLISECOND, 0);
					System.out.println("###More data " + peakCal.getTime());
					moreData = String.valueOf(peakCal.getTime().getTime());
				}
				else
					moreData = gds.getMoreData();
			}			
			gds.setMoreData(moreData);
		}
		break;
		
		case MULT_NAME_COLUMN:
			gds.setMultiplier( Double.valueOf(value.toString()));
		break;
		
/*		case SETUP_NAME_COLUMN:
			//FIX ME!!! nothing yet!!!;

		break;*/
	}

	fireTableRowsUpdated(row, row);	
}
}

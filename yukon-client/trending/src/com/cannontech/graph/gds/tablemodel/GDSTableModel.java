package com.cannontech.graph.gds.tablemodel;

/**
 * Insert the type's description here.
 * Creation date: (10/23/2001 9:10:18 AM)
 * @author: 
 */
public abstract class GDSTableModel extends javax.swing.table.AbstractTableModel
{
	public final static int DEVICE_NAME_COLUMN = 0;
	public final static int POINT_NAME_COLUMN = 1;
	public final static int LABEL_NAME_COLUMN = 2;
	public final static int COLOR_NAME_COLUMN = 3;
	public final static int AXIS_NAME_COLUMN = 4;
	public final static int TYPE_NAME_COLUMN = 5;
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
public void addRow(com.cannontech.database.db.graph.GraphDataSeries gds)
{
	String type = getIncludeType();
	
	if( gds.getType().equalsIgnoreCase(type) )
	{
		getRows().add(gds);
		fireTableDataChanged();
	}		
	if (gds.getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES)
		&& type.equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES))
	{
		getExcludedRows().add(gds);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2001 11:03:39 AM)
 * @return com.cannontech.database.db.graph.GraphDataSeries[]
 */
public com.cannontech.database.db.graph.GraphDataSeries[] getAllDataSeries()
{
	com.cannontech.database.db.graph.GraphDataSeries[] retVal =
		new com.cannontech.database.db.graph.GraphDataSeries[ getRows().size() + getExcludedRows().size() ];

	java.util.Iterator iter = getRows().iterator();

	int i = 0;
	while( iter.hasNext() )
	{
		retVal[i++] = (com.cannontech.database.db.graph.GraphDataSeries) iter.next();
	}

	iter = getExcludedRows().iterator();
	while( iter.hasNext() )
	{
		retVal[i++] = (com.cannontech.database.db.graph.GraphDataSeries) iter.next();
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
 * Creation date: (10/23/2001 4:10:47 PM)
 * @return java.lang.Class[]
 */
abstract String[] getColumnNames();
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:10:47 PM)
 * @return java.lang.Class[]
 */
abstract Class[] getColumnTypes();
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:06:43 PM)
 * @return java.util.ArrayList
 */
abstract java.util.ArrayList getExcludedRows();
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 11:26:55 AM)
 * @return java.lang.Object
 * @param index int
 */
public Object getGDSAttribute(int index, com.cannontech.database.db.graph.GraphDataSeries gds) {
	switch( index )
	{
		case DEVICE_NAME_COLUMN:
			return gds.getDeviceName();
			
		case POINT_NAME_COLUMN:
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			Long id = gds.getPointID();
			
			synchronized(cache)
			{
				java.util.List points = com.cannontech.database.cache.DefaultDatabaseCache.getInstance().getAllPoints();
				java.util.Iterator iter = points.iterator();
				while( iter.hasNext() )
				{
					com.cannontech.database.data.lite.LitePoint pt = (com.cannontech.database.data.lite.LitePoint) iter.next();
					if( pt.getPointID() == id.intValue() )
						return pt.getPointName();	
				}
			}
		break;
		case LABEL_NAME_COLUMN:
			return gds.getLabel();
			
		case COLOR_NAME_COLUMN:	
			return com.cannontech.common.gui.util.Colors.getColorString(gds.getColor().intValue());
		
		
		case AXIS_NAME_COLUMN:
			Character axis = gds.getAxis();

			if( axis.charValue() == 'R' || axis.charValue() == 'r' )
				return "Right";
			else
				return "Left";

		case TYPE_NAME_COLUMN:
			return gds.getType();
			
	}

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:01:32 PM)
 * @return int
 */
abstract String getIncludeType();
/**
 * Insert the method's description here.
 * Creation date: (10/25/00 11:21:02 AM)
 * @return com.cannontech.database.db.graph.GraphDataSeries
 * @param row int
 */
public com.cannontech.database.db.graph.GraphDataSeries getRow(int row)
{
	return ( row < getRows().size() ? (com.cannontech.database.db.graph.GraphDataSeries) getRows().get(row) : null );
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
 * Creation date: (10/23/2001 4:07:35 PM)
 * @return java.util.ArrayList
 */
abstract java.util.ArrayList getRows();
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col)
{
	if( row < getRows().size() && col < getColumnCount() )
	{
		return getGDSAttribute(col, (com.cannontech.database.db.graph.GraphDataSeries) getRows().get(row) );
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
	return ( column == LABEL_NAME_COLUMN ||
			 column == COLOR_NAME_COLUMN || 
			 column == AXIS_NAME_COLUMN);
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
public void setDataSeries(com.cannontech.database.db.graph.GraphDataSeries[] gds) {}
/**
 * Insert the method's description here.
 * Creation date: (11/6/00 10:07:31 AM)
 * @param value java.lang.Object
 * @param row int
 * @param col int
 */
public void setValueAt(Object value, int row, int col) 
{	
	com.cannontech.database.db.graph.GraphDataSeries gds = getRow(row);
	
	switch( col )
	{
		case LABEL_NAME_COLUMN:
			String valueString = value.toString();
			if (valueString.length() > 40)
				valueString = valueString.substring(0, 39);
			gds.setLabel( valueString );
		break;
		case COLOR_NAME_COLUMN:
			gds.setColor( new Integer(com.cannontech.common.gui.util.Colors.getColorID(com.cannontech.common.gui.util.Colors.getColor(value.toString()))));
			
		break;		
		case AXIS_NAME_COLUMN:
			gds.setAxis( new Character(Character.toUpperCase(value.toString().charAt(0))));;
		break;
		
		case TYPE_NAME_COLUMN:
			gds.setType( new String(value.toString()));
		break;
	}

	fireTableRowsUpdated(row, row);	
}
}

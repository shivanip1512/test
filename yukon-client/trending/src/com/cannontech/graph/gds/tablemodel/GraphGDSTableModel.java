package com.cannontech.graph.gds.tablemodel;

/**
 * TableModel for graphdataseries. 
 * Creation date: (10/25/00 9:53:43 AM)
 * @author: 
 */
public class GraphGDSTableModel extends GDSTableModel
{
	public static String includeType = com.cannontech.database.db.graph.GraphDataSeries.GRAPH_SERIES;
	
	public static String[] columnNames =
	{
		"Device",
		"Point",
		"Label",
		"Color",
		"Axis",
		"Type"
	};

	public static Class[] columnTypes =
	{
		String.class,
		String.class,
		String.class,
		java.awt.Color.class,
		String.class,
		javax.swing.JCheckBox.class
	};	

	
	// rows contains GraphDataSeries
	public java.util.ArrayList rows = new java.util.ArrayList(10);

	// rows that contain non-included GraphDataSeries
	public java.util.ArrayList excludedRows = new java.util.ArrayList();
/**
 * GraphDataSeriesTableModel constructor comment.
 */
public GraphGDSTableModel()
{
	super();
}
/**
 * Adds the Graph_Series gds to model.rows array
 * Adds the Peak_Series gds to model.excludedRows array
 * Creation date: (10/25/00 11:21:21 AM)
 */
public void addRow(com.cannontech.database.db.graph.GraphDataSeries gds) 
{
	if( gds.getType().equalsIgnoreCase(includeType) )
	{
		rows.add(gds);
		fireTableDataChanged();
	}
	else if (gds.getType().equalsIgnoreCase(com.cannontech.database.db.graph.GraphDataSeries.PEAK_SERIES))

	{
		excludedRows.add(gds);
	}
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
 * Creation date: (10/23/2001 4:05:56 PM)
 * @return java.util.ArrayList
 */
public java.util.ArrayList getExcludedRows()
{
	return excludedRows;
}
/**
 * Insert the method's description here.
 * Creation date: (10/23/2001 4:02:45 PM)
 * @return int
 */
public String getIncludeType()
{
	return includeType;
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
}

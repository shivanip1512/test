package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

public class HourTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, com.cannontech.loadcontrol.datamodels.SelectableLMTableModel
{
	HourRowData[] rows = null;
	private boolean editable = true;
		
	//The columns and their column index	
	public static final int HOUR_ENDING = 0;
	public static final int OFFER_PRICE = 1;
	public static final int TARGET = 2;

	//What our watts should be displayed in
	public static final int MODE_MW = 0;
	public static final int MODE_KWH = 1;
	
	//our display type for watts
	private int mode = MODE_MW;
	
	//The column names based on their column index
	public final String[] columnNames =
	{
		"Hour Ending",
		"Offer Price",
		( mode == MODE_MW ? "Target (MW)" : "Committed (kWh)" )
	};

	// default BG color	
	private Color backGroundColor = Color.black;

	
	//The color schemes - based on the validity of the entry
	private Color[] cellColors =
	{
		//Most entries
		Color.green,
		//Selected entry
		Color.orange
	};	
/**
 * ScheduleTableModel constructor comment.
 */
public HourTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/2001 1:42:46 PM)
 */
public synchronized void clear() 
{
	rows = new HourRowData[24];
	
	for( int i = 1; i <= 24; i++ )
	{			
		HourRowData d = new HourRowData();
		d.setHourEnding(i);
		d.setOfferPrice(0.0);
		d.setTarget(0.0);
		
		rows[i-1] = d;
	}

	fireTableDataChanged();
}
/**
 * This method was created in VisualAge.
 * @param event TableModelEvent
 */
public void fireTableChanged(javax.swing.event.TableModelEvent event) {
	super.fireTableChanged(event);
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellBackgroundColor(int row, int col) 
{
	return backGroundColor;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	return cellColors[0];
}
/**
 * getColumnCount method comment.
 */
public int getColumnCount() {
	return columnNames.length;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 * @param index int
 */
public String getColumnName(int index) {
	return columnNames[index];
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 4:15:44 PM)
 * @return int
 */
public int getMode() {
	return mode;
}
/**
 * This method returns the value of a row in the form of a LoadControlGroup object.
 * @param rowIndex int
 */
public HourRowData getRow(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex > (getRowCount() - 1) )
		return null;

	return (HourRowData)getRowAt(rowIndex);
}
/**
 * This method returns the value of a row in the form of a LoadControlGroup object.
 * @param rowIndex int
 */
public Object getRowAt(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex > (getRowCount() - 1) )
		return null;

	return getRows()[rowIndex];		
}
/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return getRows().length;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/00 10:01:03 AM)
 * @return HourRowData[]
 */
private synchronized HourRowData[] getRows() 
{
	if( rows == null )
	{
		rows = new HourRowData[24];
		
		for( int i = 1; i <= 24; i++ )
		{			
			HourRowData d = new HourRowData();
			d.setHourEnding(i);
			d.setOfferPrice(0.0);
			d.setTarget(0.0);
			
			rows[i-1] = d;
		}
		
		
	}
		
	return rows;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 */
public java.awt.Color getSelectedRowColor(int row, int column)
{
	return cellColors[1];
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 4:16:51 PM)
 * @return java.lang.String
 * @param value double
 */
private String getTotal(double value) 
{
	if( getMode() == MODE_MW )
		return java.text.NumberFormat.getNumberInstance().format(value / 1000);
	else if( getMode() == MODE_KWH ) //stored as KWH by default
		return java.text.NumberFormat.getNumberInstance().format(value);
	else
		return null;

}
/**
 * getValueAt method comment.
 */
public synchronized Object getValueAt(int row, int col) 
{
	if( row < getRowCount() && row >= 0 && getRow(row) != null )
	{		
		switch( col )
		{
		 	case HOUR_ENDING:
				return String.valueOf(getRow(row).getHourEnding()) + ":00";

		 	case OFFER_PRICE:
		 		if( isEditable() )
	 				return java.text.NumberFormat.getCurrencyInstance().format(getRow(row).getOfferPrice());
	 			else
	 			{
		 			if( getRow(row).getOfferPrice() <= 0.0 )
		 				return "";
		 			else
		 				return java.text.NumberFormat.getCurrencyInstance().format(getRow(row).getOfferPrice());
	 			}
	
			case TARGET:
				if( isEditable() )				
					return getTotal( getRow(row).getTarget() );
	 			else
	 			{
		 			if( getRow(row).getTarget() <= 0.0 )
		 				return "";
		 			else
						return getTotal( getRow(row).getTarget() );
	 			}
				
					
			default:
				return null;
		}
	
	}
	else
		return null;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) 
{
	return ( isEditable() && (column == OFFER_PRICE || column == TARGET) );
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 9:30:37 AM)
 * @return boolean
 */
public boolean isEditable() {
	return editable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/29/00 1:13:45 PM)
 * @param newBackGroundColor java.awt.Color
 */
public void setBackGroundColor(java.awt.Color newBackGroundColor) 
{
	backGroundColor = newBackGroundColor;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:08:28 AM)
 * @param newCurrentControlArea com.cannontech.loadcontrol.data.LMControlArea
 */
public synchronized void setCurrentControlArea(com.cannontech.loadcontrol.data.LMControlArea newCurrentControlArea) 
{
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 9:30:37 AM)
 * @param newEditable boolean
 */
public void setEditable(boolean newEditable) {
	editable = newEditable;
}
/**
 * Insert the method's description here.
 * Creation date: (8/23/2001 4:15:44 PM)
 * @param newMode int
 */
public void setMode(int newMode) {
	mode = newMode;
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 11:50:32 AM)
 * @param offerPrice double[]
 * @param target double[]
 */
public synchronized void setRowData(double[] offerPrice, double[] target) 
{
	// we must have a offerPrice
	if( target == null || target.length < 24 )
		return;  //ignore the bogus data

	rows = new HourRowData[24];
	
	for( int i = 0; i < target.length; i++ )
	{		
		if( i >= rows.length )
			break; //only add the first 24 values
			
		HourRowData d = new HourRowData();
		d.setHourEnding(i+1);
		d.setTarget( target[i] );

		//if we have no target, lets just assign each cell to zero
		if( offerPrice == null || i >= offerPrice.length )
			d.setOfferPrice( 0.0 );
		else
			d.setOfferPrice( offerPrice[i] );
		
		rows[i] = d;
	}


	fireTableDataChanged();
}
/**
 * getValueAt method comment.
 */
public synchronized void setValueAt(Object o, int row, int col) 
{
	try
	{
		if( row < getRowCount() && row >= 0 && getRow(row) != null )
		{		
			switch( col )
			{
			 	case OFFER_PRICE:
			 		int pos = -1;
			 		if( (pos = o.toString().indexOf("$")) != -1 )
			 			getRow(row).setOfferPrice( Double.parseDouble(o.toString().substring(pos+1, o.toString().length())) );
			 		else
			 			getRow(row).setOfferPrice( Double.parseDouble(o.toString()) );

			 		break;
			 		
				case TARGET:
					if( getMode() == MODE_MW )
						getRow(row).setTarget( Double.parseDouble(o.toString()) * 1000 );
					else
						getRow(row).setTarget( Double.parseDouble(o.toString()) );

					break;
			}

			getRow(row).setValid( true );
		}
	}
	catch( NumberFormatException e )
	{
		getRow(row).setValid( false );
	}
	
}
/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent event ) 
{
	//fireTableDataChanged();
	fireTableChanged( event );
}
}

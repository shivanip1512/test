package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOffer;
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;

public class OfferTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, com.cannontech.loadcontrol.datamodels.ISelectableLMTableModel
{
	private String fontName = "dialog";
	private int fontSize = 12;

  	// the holder for the current LMControlArea
	private LMControlArea currentControlArea = null;
	private java.util.Vector rows = null;
	
	//The columns and their column index	
	public static final int OFFER_ID = 0;
	public static final int PROGRAM_NAME = 1;
  	public static final int STATUS = 2;	
	public static final int NOTIFY_TIME = 3;
	public static final int EXPIRE_TIME = 4;
	public static final int COMMITTED_TOTAL = 5;
	public static final int TARGET_TOTAL = 6;
  	
	//The column names based on their column index
	public static final String[] columnNames =
	{
		"Offer ID",
		"Program",
		"Status",
		"Notify Time",
		"Expire Time",
		"Committed Total (MW)",
		"Target Total (MW)"
	};

	//The color schemes - based on the program status (foreGround, backGround)
	// We will mostly likely later make these a StateGroup in the Database!!! :)
	private Color[][] cellColors =
	{
		//Inactive
		{ Color.white, Color.black },
		//Active, Manual Active & Fully Active
		{ Color.green, Color.black },
		//Scheduled
		{ Color.cyan, Color.black },
		//Notified
		{ Color.orange, Color.black },
		//Stopping
		{ Color.yellow, Color.black },


		//Disabled program
		{ Color.red, Color.black }
	};

/**
 * ScheduleTableModel constructor comment.
 */
public OfferTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	getRows().removeAllElements();
	currentControlArea = null;

	//we can drop the current row selection here
	fireTableDataChanged();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellBackgroundColor(int row, int col) 
{

	return Color.black;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	OfferRowData rowValue = (OfferRowData)getRowAt(row);

	if( rowValue != null  )
	{		
		if( rowValue.getEnergyExchangeProgram().getDisableFlag().booleanValue() )
		{
			return Color.red;
		}
		else if( rowValue.getOwnerOffer().getRunStatus().equalsIgnoreCase(LMEnergyExchangeOffer.RUN_CANCELED) )
		{
			return Color.cyan;
		}
		else if( rowValue.getOwnerOffer().getRunStatus().equalsIgnoreCase(LMEnergyExchangeOffer.RUN_CLOSING)
					|| rowValue.getOwnerOffer().getRunStatus().equalsIgnoreCase(LMEnergyExchangeOffer.RUN_COMPLETED) )
		{
			return Color.white;
		}
		else if( rowValue.getOwnerOffer().getRunStatus().equalsIgnoreCase(LMEnergyExchangeOffer.RUN_CURTAILMENT_ACTIVE)
					|| rowValue.getOwnerOffer().getRunStatus().equalsIgnoreCase(LMEnergyExchangeOffer.RUN_OPEN) )
		{
			return Color.green;
		}
		else if( rowValue.getOwnerOffer().getRunStatus().equalsIgnoreCase(LMEnergyExchangeOffer.RUN_CURTAILMENT_PENDING)
					|| rowValue.getOwnerOffer().getRunStatus().equalsIgnoreCase(LMEnergyExchangeOffer.RUN_SCHEDULED) )
		{
			return Color.yellow;
		}
		else //LMEnergyExchangeOffer.RUN_NULL or any Unknown state
		{
			return Color.darkGray;
		}

		
	}

	return Color.white;
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
 * Creation date: (4/6/2001 10:08:28 AM)
 * @return com.cannontech.loadcontrol.data.LMControlArea
 */
public com.cannontech.loadcontrol.data.LMControlArea getCurrentControlArea() {
	return currentControlArea;
}
/**
 * Insert the method's description here.
 * Creation date: (1/11/2002 10:45:49 AM)
 * @return int
 */
private synchronized int getLastRowIndex() 
{
	return (getRowCount() == 0 ? 0 : getRowCount());
}
/**
 * This method returns the value of a row in the form of a LMProgramBase object.
 */
public synchronized Object getRowAt(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex >= getRowCount() )
		return null;
		

	return (OfferRowData)getRows().get(rowIndex);
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
 * Creation date: (8/23/00 10:01:03 AM)
 * @return Vector
 */
private java.util.Vector getRows()
{
	if( rows == null )
		rows = new java.util.Vector(10);
		
	return rows;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 */
public java.awt.Color getSelectedRowColor(int row, int column)
{
	return getCellForegroundColor( row, column ).brighter();
}
/**
 * getValueAt method comment.
 */
public Object getValueAt(int row, int col) 
{
	
	OfferRowData rowValue = (OfferRowData)getRowAt(row);

	try
	{
		if( row <= getRowCount() && rowValue != null )
		{
			switch( col )
			{
			 	case OFFER_ID:
					return rowValue.getOfferIDString();

			 	case PROGRAM_NAME:
			 		return rowValue.getEnergyExchangeProgram().getYukonName();
		
				case STATUS:
					if( rowValue.getEnergyExchangeProgram().getDisableFlag().booleanValue() )
						return "DISABLED PROGRAM : " + rowValue.getOwnerOffer().getRunStatus();
					else
						return rowValue.getOwnerOffer().getRunStatus();

				case NOTIFY_TIME:
					return new com.cannontech.clientutils.commonutils.ModifiedDate(rowValue.getCurrentRevision().getNotificationDateTime().getTime());
				
				case EXPIRE_TIME:
					return new com.cannontech.clientutils.commonutils.ModifiedDate(rowValue.getCurrentRevision().getOfferExpirationDateTime().getTime());
					
				case COMMITTED_TOTAL:
					return java.text.NumberFormat.getNumberInstance().format(rowValue.getCommittedTotal().doubleValue());
					
				case TARGET_TOTAL:
					return java.text.NumberFormat.getNumberInstance().format(rowValue.getTargetTotal().doubleValue());
					
				default:
					return null;
			}
		}
		else
			return null;
	}
	catch( NullPointerException e )
	{
		if( !rowValue.isValidState() )
		{
			com.cannontech.clientutils.CTILogger.info("-----------------------------------------------------------------------------------");
			com.cannontech.clientutils.CTILogger.info("*** (Row,Col) (" + row + "," + col + ") is in an invalid state in class : " + this.getClass().getName());
			com.cannontech.clientutils.CTILogger.info(rowValue.getInvalidStateMsg());			
			com.cannontech.clientutils.CTILogger.info("-----------------------------------------------------------------------------------");
			return ("[Invalid Cell - See Debug Info]");
		}
		else
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

		return null;
	}

}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param row int
 * @param column int
 */
public boolean isCellEditable(int row, int column) {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:08:28 AM)
 * @param newCurrentControlArea com.cannontech.loadcontrol.data.LMControlArea
 */
public synchronized void setCurrentControlArea(com.cannontech.loadcontrol.data.LMControlArea newCurrentControlArea) 
{
	currentControlArea = newCurrentControlArea;

	if( getCurrentControlArea() == null || getCurrentControlArea().getLmProgramVector() == null )
	{
		clear();
	}
	else
	{
		fireTableRowsDeleted( 0, getLastRowIndex() );
		getRows().removeAllElements();

		for( int i = 0; i < getCurrentControlArea().getLmProgramVector().size(); i++ )
		{
			if( getCurrentControlArea().getLmProgramVector().get(i) 
				 instanceof LMProgramEnergyExchange )
			{
				LMProgramEnergyExchange prg = 
					(LMProgramEnergyExchange)getCurrentControlArea().getLmProgramVector().get(i);
			
				for( int j = 0; j < prg.getEnergyExchangeOffers().size(); j++ )
				{
					OfferRowData row = new OfferRowData(prg);
					
					row.setOwnerOffer( (LMEnergyExchangeOffer)prg.getEnergyExchangeOffers().get(j) );

					getRows().add( row );
				}


			
				//OfferRowData row = new OfferRowData(
					//(LMProgramEnergyExchange)getCurrentControlArea().getLmProgramVector().get(i) );

				//getRows().add( row );
			}
	
		}
	}

	fireTableRowsInserted( 0, getLastRowIndex() );
}
/**
 * This method was created in VisualAge.
 * @param event javax.swing.event.TableModelEvent
 */
public void tableChanged(javax.swing.event.TableModelEvent event ) 
{
	if( event instanceof com.cannontech.loadcontrol.events.LCGenericTableModelEvent )
	{
		if( ((com.cannontech.loadcontrol.events.LCGenericTableModelEvent)event).getType()
			 == com.cannontech.loadcontrol.events.LCGenericTableModelEvent.TYPE_CLEAR )
		{
			clear();
		}

	}

	//fireTableDataChanged();
	fireTableChanged( event );
}
}

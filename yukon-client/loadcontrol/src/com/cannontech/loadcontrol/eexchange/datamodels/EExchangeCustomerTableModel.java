package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;

public class EExchangeCustomerTableModel extends javax.swing.table.AbstractTableModel implements javax.swing.event.TableModelListener, com.cannontech.loadcontrol.datamodels.SelectableLMTableModel
{
	private LMControlArea currentControlArea = null;

	
	private java.util.Vector rows = null;
	
	//The columns and their column index	
	public static final int CUSTOMER_NAME = 0;
	public static final int ACCEPT = 1;
	public static final int IP_ADDRESS = 2;
	public static final int TIME = 3;
  	public static final int TOTAL = 4;
  	
	//The column names based on their column index
	public static final String[] columnNames =
	{
		"Customer",
		"Accept",
		"IP Address",
		"Time",
		"Total (kWh)",
	};

	// default BG color	
	Color backGroundColor = Color.black;

	//The color schemes - based on the customers acceptance
	private Color[] cellColors =
	{
		//Accepted reply
		Color.green,
		//Declined reply
		Color.red,
		//Null reply
		Color.yellow
	};	
/**
 * ScheduleTableModel constructor comment.
 */
public EExchangeCustomerTableModel() {
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
	return backGroundColor;
	
/*	if( schedules != null && schedules.length > row && col < 5 )
	{
		String status = schedules[row].getState();

		if( status.equals(Schedule.Waiting) )
		{
			return cellColors[0][1];	
		}
		else
		if( status.equals(Schedule.Running)  )
		{
			return cellColors[1][1];
		}
		else
		if( status.equals(Schedule.Deactivated) )
		{
			return cellColors[2][1];
		}
		else
		if( status.equals(Schedule.Pending ) )
		{
			return cellColors[3][1];
		}	
	}

	return Color.black;
*/
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public java.awt.Color getCellForegroundColor(int row, int col) 
{
	EExchangeRowData rowValue = (EExchangeRowData)getRowAt(row);

	if( rowValue != null )
	{
		if( rowValue.getOwnerReply() == null )
		{
			//com.cannontech.clientutils.CTILogger.info("*** (null) OwnerReply for customer : " + rowValue.getCustomer().getYukonName() );
			return Color.yellow;
		}
		else if( rowValue.getOwnerReply().getAcceptStatus().equalsIgnoreCase(
			com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply.STRING_ACCEPTED) )
		{
			return Color.green;
		}
		else if( rowValue.getOwnerReply().getAcceptStatus().equalsIgnoreCase(
			com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply.STRING_DECLINED) )
		{
			return Color.red;
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
 * Creation date: (1/11/2002 10:45:49 AM)
 * @return int
 */
private synchronized int getLastRowIndex() 
{
	return (getRowCount() == 0 ? 0 : getRowCount());
}
/**
 * This method returns the value of a row in the form of a LoadControlGroup object.
 * @param rowIndex int
 */
public synchronized Object getRowAt(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex >= getRowCount() )
		return null;

	return (EExchangeRowData)getRows().get(rowIndex);
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
		rows = new java.util.Vector(5);
		
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
	EExchangeRowData rowValue = (EExchangeRowData)getRowAt(row);

	try
	{

		if( rowValue != null )
		{
				
			switch( col )
			{
			 	case CUSTOMER_NAME:
					return rowValue.getCustomer().getYukonName();

			 	case ACCEPT:
					if( rowValue.getOwnerReply() == null )
						return " ----";
					else
			 			return rowValue.getOwnerReply().getAcceptStatus();
		
				case IP_ADDRESS:
					if( rowValue.getOwnerReply() == null )
						return " ----";
					else
			 			return rowValue.getOwnerReply().getIpAddressOfAcceptUser();
				
				case TIME:
					if( rowValue.getOwnerReply() == null ||
						 rowValue.getOwnerReply().getAcceptDateTime().getTime() <=
						 com.cannontech.common.util.CtiUtilities.get1990GregCalendar().getTime().getTime() )
						return " ----";
					else
		 				return new com.cannontech.clientutils.commonutils.ModifiedDate(rowValue.getOwnerReply().getAcceptDateTime().getTime());
		 			
				case TOTAL:
		 			return rowValue.getTotal();
					
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
	currentControlArea = newCurrentControlArea;
		
	if( newCurrentControlArea != null && newCurrentControlArea.getLmProgramVector() != null )
	{
		fireTableRowsDeleted( 0, getLastRowIndex() );
		getRows().removeAllElements();
		
		for( int i = 0; i < currentControlArea.getLmProgramVector().size(); i++ )
		{
			if( currentControlArea.getLmProgramVector().get(i) 
				 instanceof com.cannontech.loadcontrol.data.LMProgramEnergyExchange )
			{
				com.cannontech.loadcontrol.data.LMProgramEnergyExchange prg = 
					(com.cannontech.loadcontrol.data.LMProgramEnergyExchange)currentControlArea.getLmProgramVector().get(i);

				for( int j = 0; j < prg.getEnergyExchangeCustomers().size(); j++ )
				{
					EExchangeRowData e = new EExchangeRowData( (LMEnergyExchangeCustomer)prg.getEnergyExchangeCustomers().get(i) );

					for( int c = 0; c < e.getCustomer().getEnergyExchangeCustomerReplies().size(); c++ )
						e.setOwnerReply( (com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply)e.getCustomer().getEnergyExchangeCustomerReplies().get(c) );

					rows.add( e );
				}
				
			}

		}
				
	}
	else
		clear();

	fireTableRowsInserted( 0, getLastRowIndex() );
}

/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:08:28 AM)

 NOT USED ANYMORE!!!!!!!!
 */
public void setCustomers(java.util.Vector customers) 
{
/*	if( customers == null )
	{
		clear();
		return;
	}


	//we must make a copy of all the customerReplies for our model
	synchronized( getRows() )
	{
		java.util.ArrayList list = new java.util.ArrayList(customers.size() * 2);
		
		for( int i = 0; i < customers.size(); i++ )
		{			
			EExchangeRowData e = new EExchangeRowData( (LMEnergyExchangeCustomer)customers.get(i) );

			for( int j = 0; j < e.getCustomer().getEnergyExchangeCustomerReplies().size(); j++ )
				e.setOwnerReply( (com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply)e.getCustomer().getEnergyExchangeCustomerReplies().get(j) );

			list.add(e);
		}

		list.trimToSize();
		rows = new EExchangeRowData[customers.size()];
		System.arraycopy( list.toArray(), 0, rows, 0, rows.length );
	}
		
	fireTableDataChanged();
*/
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

package com.cannontech.loadcontrol.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.data.LMProgramBase;

public class ControlAreaTableModel extends com.cannontech.tdc.observe.ObservableRowTableModel implements com.cannontech.loadcontrol.gui.ControlAreaListener
{
	private static final java.text.SimpleDateFormat TIME_FORMATTER = new java.text.SimpleDateFormat("HH:mm");

	private String currentView = 
		com.cannontech.loadcontrol.gui.ControlAreaActionListener.ALL_CONTROL_AREAS;

	private final java.util.GregorianCalendar tempTime = new java.util.GregorianCalendar();
	private final java.util.GregorianCalendar tempDailyTime = new java.util.GregorianCalendar();
	
  	// the holder for the current LMControlAreas in our Model
  	//  Objects of type LMControlArea only are allowed in here
	private java.util.Vector currentControlAreas = null;
	
	//The columns and their column index	
	public static final int AREA_NAME = 0;
	public static final int CURRENT_STATE = 1;
	public static final int VALUE_THRESHOLD = 2;
	public static final int START_TIME = 3;
	public static final int STOP_TIME = 4;
  	
	//The column names based on their column index
	public static final String[] columnNames =
	{
		"Area Name",
		"State",
		"Value/Threshold",
		"Start Time",
		"Stop Time"
	};

	//The color schemes - based on the program status (foreGround, backGround)
	// We will mostly likely later make these a StateGroup in the Database!!! :)
	private Color[] cellColors =
	{
		//Inactive
		Color.white,
		//Active, Manual Active & Fully Active
		Color.green,
		//Scheduled
		Color.yellow,

		//Disabled program
		Color.red
	};

/**
 * ScheduleTableModel constructor comment.
 */
public ControlAreaTableModel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:08:28 AM)
 * @param LMControlArea
 */
public synchronized void addControlArea( LMControlArea area )
{
	area = fileterArea(area);

	//FOR NOW, only display LMControlAreas that have 1 or more LMPrograms
	if( area.getLmProgramVector().size() > 0 )
	{
		getCurrentControlAreas().add( area );
		fireTableRowsInserted( 0, getLastRowIndex() );
	}

}
/**
 * Insert the method's description here.
 * Creation date: (3/20/00 11:40:31 AM)
 */
public void clear() 
{
	//this clearControlAreas() method will notify all observers of the
	// cleared ControlAreas, we must catch if so we can clear our list!
	//LoadControlClientConnection.getInstance().clearControlAreas();
	//setCurrentControlAreas(null);

	getCurrentControlAreas().removeAllElements();

	fireTableChanged( new com.cannontech.loadcontrol.events.LCGenericTableModelEvent(
		this, com.cannontech.loadcontrol.events.LCGenericTableModelEvent.TYPE_CLEAR) );
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2002 5:15:56 PM)
 */
private LMControlArea fileterArea( LMControlArea area )
{

	if( getCurrentView().equalsIgnoreCase(
			com.cannontech.loadcontrol.gui.ControlAreaActionListener.ENERGY_EXCHANGE) )
	{
		for( int i = (area.getLmProgramVector().size()-1); i >= 0; i-- )
			if( !(area.getLmProgramVector().get(i) 
				  instanceof com.cannontech.loadcontrol.data.LMProgramEnergyExchange) )
			{
				area.getLmProgramVector().remove(i);
				//break;
			}

	}
	else
	{
		boolean found = false;
		for( int i = (area.getLmProgramVector().size()-1); i >= 0; i-- )
			if( area.getLmProgramVector().get(i)
				 instanceof com.cannontech.loadcontrol.data.LMProgramEnergyExchange )
			{
				area.getLmProgramVector().remove(i);
			}
	}

	return area;
}
/**
 * Insert the method's description here.
 * Creation date: (1/16/2002 5:15:56 PM)
 */
private void filterAllAreas() 
{
	if( getCurrentView().equalsIgnoreCase(com.cannontech.loadcontrol.gui.ControlAreaActionListener.ENERGY_EXCHANGE) )
	{
		//remove all of the NON com.cannontech.loadcontrol.data.LMProgramEnergyExchange
		//  from the Vector
		for( int j = getRowCount()-1; j >= 0; j-- )
			for( int i = (getRowAt(j).getLmProgramVector().size()-1); i >= 0; i-- )
				if( !(getRowAt(j).getLmProgramVector().get(i) 
						instanceof com.cannontech.loadcontrol.data.LMProgramEnergyExchange) )
				{
					removeControlArea( getRowAt(j) );
					break; //
				}
	}
	else
	{
		//remove all of the com.cannontech.loadcontrol.data.LMProgramEnergyExchange
		//  from the Vector
		for( int j = getRowCount()-1; j >= 0; j-- )
			for( int i = (getRowAt(j).getLmProgramVector().size()-1); i >= 0; i-- )
				if( getRowAt(j).getLmProgramVector().get(i) 
					 instanceof com.cannontech.loadcontrol.data.LMProgramEnergyExchange )
				{
					removeControlArea( getRowAt(j) );
					break;
				}
	}
	
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public synchronized java.awt.Color getCellBackgroundColor(int row, int col) 
{
	return Color.black;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 * @param row int
 * @param col int
 */
public synchronized java.awt.Color getCellForegroundColor(int row, int col) 
{
	if( getCurrentControlAreas() != null 
		 && row >= 0 && row < getRowCount()
		 && col >= 0 && col <= getColumnCount() )
	{
		if( getRowAt(row).getDisableFlag().booleanValue() )
		{
			return cellColors[3];
		}
		else if( getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_INACTIVE )
		{
			return cellColors[0];
		}
		else if( getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_ACTIVE
					|| getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_FULLY_ACTIVE
					|| getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_MANUAL_ACTIVE )
		{
			return cellColors[1];
		}
		else if( getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_SCHEDULED )
		{
			return cellColors[2];
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
 * @return Vector
 */
public java.util.Vector getCurrentControlAreas() 
{
	if( currentControlAreas == null )
		currentControlAreas = new java.util.Vector(10);

	return currentControlAreas;
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 3:07:48 PM)
 * @param newCurrentView java.lang.String
 */
public String getCurrentView()
{
	return currentView;
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
 * This method returns the value of a row in the form of a LMControlArea object.
 */
public synchronized LMControlArea getRowAt(int rowIndex) 
{
	if( rowIndex < 0 || rowIndex >= getRowCount() )
		return null;
	else
		return (LMControlArea)getCurrentControlAreas().get(rowIndex);	
}
/**
 * getRowCount method comment.
 */
public int getRowCount() 
{
	return getCurrentControlAreas().size();
}
/**
 * getValueAt method comment.
 */
public synchronized Object getValueAt(int row, int col) 
{

	if( row < getRowCount() && row >= 0 )
	{
		switch( col )
		{
		 	case AREA_NAME:
				return getRowAt(row).getYukonName();

		 	case CURRENT_STATE:
		 		if( getRowAt(row).getDisableFlag().booleanValue() )
					return "DISABLED: " + LMControlArea.getControlAreaStateString( getRowAt(row).getControlAreaState().intValue() );
		 		else
					return LMControlArea.getControlAreaStateString( getRowAt(row).getControlAreaState().intValue() );

			case VALUE_THRESHOLD:
				return getRowAt(row);
					
			case START_TIME:
				if( getRowAt(row).getDisableFlag().booleanValue() )
					return "  ----";
				else
				{					
					if( getRowAt(row).getDefDailyStartTime() == null
						 || getRowAt(row).getDefDailyStartTime().intValue() == LMControlArea.INVAID_INT )
						return "  ----";
					else
					{
						int defStart = getRowAt(row).getDefDailyStartTime().intValue();
						int currStart = getRowAt(row).getCurrentDailyStartTime().intValue();
						
						//set our time to todays date
						tempTime.setTime( new java.util.Date() );
						tempTime.set( tempTime.HOUR_OF_DAY, 0 ); 
						tempTime.set( tempTime.MINUTE, 0 );
						tempTime.set( tempTime.SECOND, defStart );

						if( defStart == currStart || currStart <= LMControlArea.INVAID_INT )
							return TIME_FORMATTER.format( tempTime.getTime() );
						else
						{
							tempDailyTime.setTime( tempTime.getTime() );
							tempDailyTime.set( tempDailyTime.HOUR_OF_DAY, 0 ); 
							tempDailyTime.set( tempDailyTime.MINUTE, 0 );
							tempDailyTime.set( tempDailyTime.SECOND, currStart );

							return 
								"(" + TIME_FORMATTER.format( tempTime.getTime() )
								+ ") "
								+ TIME_FORMATTER.format( tempDailyTime.getTime() );
						}

					}

				}

			case STOP_TIME:
				if( getRowAt(row).getDisableFlag().booleanValue() )
					return "  ----";
				else
				{					
					if( getRowAt(row).getDefDailyStopTime() == null
						 || getRowAt(row).getDefDailyStopTime().intValue() == LMControlArea.INVAID_INT )
						return "  ----";
					else
					{
						int defStop = getRowAt(row).getDefDailyStopTime().intValue();
						int currStop = getRowAt(row).getCurrentDailyStopTime().intValue();
						
						//set our time to todays date
						tempTime.setTime( new java.util.Date() );
						tempTime.set( tempTime.HOUR_OF_DAY, 0 ); 
						tempTime.set( tempTime.MINUTE, 0 );
						tempTime.set( tempTime.SECOND, defStop );
												
						if( defStop == currStop || currStop <= LMControlArea.INVAID_INT )
							return TIME_FORMATTER.format( tempTime.getTime() );
						else
						{
							tempDailyTime.setTime( tempTime.getTime() );
							tempDailyTime.set( tempDailyTime.HOUR_OF_DAY, 0 ); 
							tempDailyTime.set( tempDailyTime.MINUTE, 0 );
							tempDailyTime.set( tempDailyTime.SECOND, currStop );

							return 
								"(" + TIME_FORMATTER.format( tempTime.getTime() )
								+ ") "
								+ TIME_FORMATTER.format( tempDailyTime.getTime() );
						}
					}
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
public boolean isCellEditable(int row, int column) {
	return false;
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:08:28 AM)
 * @param LMControlArea
 */
public void removeControlArea( LMControlArea area )
{
	int loc = getCurrentControlAreas().indexOf(area);
	
	getCurrentControlAreas().remove( loc );
	fireTableRowsDeleted( loc, loc );
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 10:08:28 AM)
 * @param LMControlArea
 */
public void setControlAreaAt( LMControlArea area, int index )
{
	area = fileterArea(area);

	getCurrentControlAreas().setElementAt( area, index );

	fireObservedRowChanged( area );
	fireTableRowsUpdated( index, index );
}
/**
 * Insert the method's description here.
 * Creation date: (8/6/2001 3:07:48 PM)
 * @param newCurrentView java.lang.String
 */
public synchronized void setCurrentView(java.lang.String newCurrentView) 
{
	currentView = newCurrentView;

	filterAllAreas();

	fireTableChanged( new com.cannontech.loadcontrol.events.LCGenericTableModelEvent(
		this, com.cannontech.loadcontrol.events.LCGenericTableModelEvent.TYPE_FILTER_CHANGE) );
}
}

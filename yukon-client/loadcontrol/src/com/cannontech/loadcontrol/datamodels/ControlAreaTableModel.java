package com.cannontech.loadcontrol.datamodels;

/**
 * This type was created in VisualAge.
 */
import java.awt.Color;

import com.cannontech.loadcontrol.LCUtils;
import com.cannontech.loadcontrol.data.LMControlArea;

public class ControlAreaTableModel extends com.cannontech.tdc.observe.ObservableRowTableModel implements IControlAreaTableModel
{
	
  	// the holder for the current LMControlAreas in our Model
  	//  Objects of type LMControlArea only are allowed in here
	private java.util.Vector currentControlAreas = null;
	
	//The columns and their column index	
	public static final int AREA_NAME				= 0;
	public static final int CURRENT_STATE			= 1;	
	public static final int VALUE_THRESHOLD		= 2;
	public static final int PEAK_PROJECTION		= 3;
	public static final int ATKU						= 4;
	public static final int PRIORITY					= 5;
	public static final int TIME_WINDOW				= 6;
  	
	//The column names based on their column index
	public static final String[] COLUMN_NAMES =
	{
		"Area Name",
		"State",
		"Value/Threshold",
		"Peak/Projection",
		"ATKU",
		"Priority",
		"Time Window"
	};

	//The color schemes - based on the program status (foreGround, backGround)
	// We will mostly likely later make these a StateGroup in the Database!!! :)
	public static final Color[] CELL_COLORS =
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
	 * ControlAreaTableModel constructor comment.
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
		addControlAreaAt( area, 0 );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 10:08:28 AM)
	 * @param LMControlArea
	 */
	public synchronized void addControlAreaAt( LMControlArea area, int indx )
	{
		//area = fileterArea(area);
	
		//FOR NOW, only display LMControlAreas that have 1 or more LMPrograms
		if( area.getLmProgramVector().size() > 0 )
		{
			getCurrentControlAreas().insertElementAt( area, indx );
			fireTableRowsInserted( indx, getRowCount() );
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

/*
	private LMControlArea fileterArea( LMControlArea area )
	{
	
		if( getCurrentView().equalsIgnoreCase(
				com.cannontech.loadcontrol.displays.ControlAreaActionListener.ENERGY_EXCHANGE) )
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

	private void filterAllAreas() 
	{
		if( getCurrentView().equalsIgnoreCase(com.cannontech.loadcontrol.displays.ControlAreaActionListener.ENERGY_EXCHANGE) )
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
*/
	
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
				return CELL_COLORS[3];
			}
			else if( getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_INACTIVE )
			{
				return CELL_COLORS[0];
			}
			else if( getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_ACTIVE
						|| getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_FULLY_ACTIVE
						|| getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_MANUAL_ACTIVE )
			{
				return CELL_COLORS[1];
			}
			else if( getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_CNTRL_ATTEMPT )
						 //|| getRowAt(row).getControlAreaState().intValue() == LMControlArea.STATE_SCHEDULED )
			{
				return CELL_COLORS[2];
			}
			
		}
	
		return Color.white;
	}
	/**
	 * getColumnCount method comment.
	 */
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 * @param index int
	 */
	public String getColumnName(int index) {
		return COLUMN_NAMES[index];
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
			LMControlArea lmCntrArea = getRowAt(row);
			return LCUtils.getControlAreaValueAt( lmCntrArea, col );					
		}
		else
			return null;
	}


	/*
	private String getTriggerString(com.cannontech.loadcontrol.data.LMControlAreaTrigger trigger) 
	{
		if( trigger == null )
			return null;
	
		LitePoint point = PointFuncs.getLitePoint( trigger.getPointId().intValue() );
	
		if( trigger.getTriggerType().equalsIgnoreCase(
			   com.cannontech.database.db.device.lm.LMControlAreaTrigger.TYPE_STATUS) )
		{
			return com.cannontech.database.cache.functions.StateFuncs.getLiteState( point.getStateGroupID(), trigger.getPointValue().intValue() ).getStateText() +
				" / " +
				com.cannontech.database.cache.functions.StateFuncs.getLiteState( point.getStateGroupID(), trigger.getThreshold().intValue() ).getStateText();
		}	
		else
		{
			//com.cannontech.database.db.device.lm.LMControlAreaTrigger.TYPE_THRESHOLD
			return numberFormatter.format(trigger.getPointValue()) +
				" / " +
				numberFormatter.format(trigger.getThreshold());
			
		}
	
	
	}
	*/

	
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
		
		//be sure we can find the area specified
		if( loc >= 0 )
		{
			getCurrentControlAreas().remove( loc );
			fireTableRowsDeleted( loc, loc );
		}
		
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 10:08:28 AM)
	 * @param LMControlArea
	 */
	public void setControlAreaAt( LMControlArea area, int index )
	{
		//area = fileterArea(area);
		getCurrentControlAreas().setElementAt( area, index );
	
		fireObservedRowChanged( area );
		fireTableRowsUpdated( index, index );
	}

}

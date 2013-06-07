package com.cannontech.loadcontrol.datamodels;

import javax.swing.event.TableModelListener;

import com.cannontech.messaging.message.loadcontrol.data.ControlAreaItem;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class FilteredControlAreaTableModel extends ControlAreaTableModel
{
	private int[] filteredStates = new int[] { ControlAreaItem.INVALID_INT };
 

	/**
	 * Constructor for FilteredControlAreaTableModel.
	 */
	public FilteredControlAreaTableModel( int[] areaStates_ )
	{
		super();
		filteredStates = areaStates_;
	}

	/**
	 * Constructor for FilteredControlAreaTableModel.
	 */
	public FilteredControlAreaTableModel( int[] areaStates_, TableModelListener[] listeners_ )
	{
		this( areaStates_ );

		//add all the listener references here
		if( listeners_ != null )
			for( int i = 0; i < listeners_.length; i++ )
				addTableModelListener( listeners_[i] );
	}

	protected boolean isValid( int state_ )
	{
		for( int i = 0; i < filteredStates.length; i++ )
			if( state_ == filteredStates[i] )
				return true;
				
		return false;
	}
		
		

	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 10:08:28 AM)
	 * @param ControlAreaItem
	 */
	public synchronized void addControlAreaAt( ControlAreaItem area, int indx )
	{
		if( !isValid(area.getControlAreaState().intValue()) )
			return;
			
		if( area.getProgramVector().size() > 0 )
		{
			getCurrentControlAreas().insertElementAt( area, indx );
			fireTableRowsInserted( indx, getRowCount() );
		}
	
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (4/6/2001 10:08:28 AM)
	 * @param ControlAreaItem
	 */
	public void setControlAreaAt( ControlAreaItem area, int index )
	{
		if( !isValid(area.getControlAreaState().intValue()) )
		{
			removeControlArea( area );
		}
		else
		{
			getCurrentControlAreas().setElementAt( area, index );
		}
		
	
		fireObservedRowChanged( area );
		fireTableRowsUpdated( index, index );
	}

}

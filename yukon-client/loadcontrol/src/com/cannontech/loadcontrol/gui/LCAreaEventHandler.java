package com.cannontech.loadcontrol.gui;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.datamodels.ControlAreaTableModel;
import com.cannontech.loadcontrol.datamodels.IControlAreaTableModel;
import com.cannontech.loadcontrol.events.LCChangeEvent;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LCAreaEventHandler
{
	public LCAreaEventHandler()
	{
		super();
	}


	private int getInsertionIndx( LMControlArea area, IControlAreaTableModel cntrlAreaTbModel )
	{
		int i = 0;
		
		//always keep our main list in order by the AreaName
		for( i = 0; i < cntrlAreaTbModel.getRowCount(); i++ )
		{
			LMControlArea areaRow = (LMControlArea)cntrlAreaTbModel.getRowAt(i);

			int cmpVal = area.getYukonName().compareToIgnoreCase(areaRow.getYukonName());

			// < means the new area is before row area
			if( cmpVal < 0 )
				return i;
		}

		return i;
	}


	protected synchronized void handleChangeEvent( LCChangeEvent msg, IControlAreaTableModel cntrlAreaTbModel )
	{
		if( msg.id == LCChangeEvent.INSERT )
		{
			LMControlArea area = (LMControlArea)msg.arg;

			cntrlAreaTbModel.addControlAreaAt( 
					area,
					getInsertionIndx(area, cntrlAreaTbModel) );
	
		}
		else if( msg.id == LCChangeEvent.UPDATE )
		{
			boolean found = false;
			LMControlArea newArea = (LMControlArea)msg.arg;

			for( int i = 0; i < cntrlAreaTbModel.getRowCount(); i++ )
			{
				LMControlArea areaRow = (LMControlArea)
								cntrlAreaTbModel.getRowAt(i);
					
				if( areaRow.equals(newArea) )
				{
					//update all the the control area's
					cntrlAreaTbModel.setControlAreaAt(
							newArea, i );
	
					found = true;
					break;
				}
			}
	
			if( !found )
				cntrlAreaTbModel.addControlAreaAt( 
					newArea,
					getInsertionIndx(newArea, cntrlAreaTbModel) );

		}
		else if( msg.id == LCChangeEvent.DELETE )
		{
			cntrlAreaTbModel.removeControlArea( 
				(LMControlArea)msg.arg );						
		}
		else if( msg.id == LCChangeEvent.DELETE_ALL ) // remove all items
		{
			cntrlAreaTbModel.clear();				
		}
	
	}

	
}

package com.cannontech.loadcontrol.gui.manualentry;

import com.cannontech.common.gui.panel.MultiSelectRow;
import com.cannontech.common.util.CtiUtilities;

/**
 * @author rneuharth
 *
 * A child model that adds some columns to the parent model.
 * 
 */
public class MultiSelectPrgScenModel extends MultiSelectPrgModel
{
	private final String[] COLUMNS = 
	{
		"Selected",
		"Name",
		"Gear",
		"State",
		"Start",
		"Stop"
	};


	public static final int COL_SELECTED = 0;
	public static final int COL_NAME = 1;
	public static final int COL_GEAR = 2;
	public static final int COL_STATE = 3;
	public static final int COL_START_DELAY = 4;
	public static final int COL_STOP_OFFSET= 5;

	/**
	 * Constructor for MultiSelectPrgModel.
	 */
	public MultiSelectPrgScenModel()
	{
		super();
	}


	public Object getValueAt( int row, int col) 
	{
		MultiSelectRow val = (MultiSelectRow)getRowAt(row);
		MultiSelectProg prg = (MultiSelectProg)val.getObject();

		switch( col )
		{
			case COL_SELECTED:
				return val.isChecked();

			case COL_NAME:
				return prg.getBaseProgram().toString();

			case COL_GEAR:
			{
				if( prg.hasDirectGears() )
					return prg.getGearNum();
				else
					return new Integer(1);
			}

			case COL_STATE:
				return prg.getBaseProgram().getProgramStatusString( 
						prg.getBaseProgram().getProgramStatus().intValue() );

			case COL_START_DELAY:
				return CtiUtilities.decodeSecondsToTime(
								prg.getStartDelay().intValue() );

			case COL_STOP_OFFSET:
				return CtiUtilities.decodeSecondsToTime(
								prg.getStopOffset().intValue() );
		}
	
		//unknown thing here!
		return null;			
	}


	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int col)
	{
		return COLUMNS[col];
	}

}

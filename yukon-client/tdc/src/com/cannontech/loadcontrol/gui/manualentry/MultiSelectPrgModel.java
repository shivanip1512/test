package com.cannontech.loadcontrol.gui.manualentry;

import javax.swing.table.AbstractTableModel;

import com.cannontech.common.gui.panel.IMultiSelectModel;
import com.cannontech.common.gui.panel.MultiSelectRow;
import com.cannontech.loadcontrol.data.IGearProgram;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MultiSelectPrgModel extends AbstractTableModel implements IMultiSelectModel
{
	private java.util.Vector rows = new java.util.Vector(10);
	
	private final String[] COLUMNS = 
	{
		"Selected",
		"Name",
		"Gear",
		"State"
	};


	public static final int COL_SELECTED		= 0;
	public static final int COL_NAME				= 1;
	public static final int COL_GEAR				= 2;
	public static final int COL_STATE			= 3;

	/**
	 * Constructor for MultiSelectPrgModel.
	 */
	public MultiSelectPrgModel() {
		super();
	}

	public boolean isCellEditable( int row, int col )
	{
		return col == COL_SELECTED
				  ||
				  //this must be the gear column and this row must have gears 
				  ( col == COL_GEAR 
				    && ((MultiSelectProg)getRowAt(row).getObject()).hasDirectGears() );
	}

	public int getCheckBoxCol()
	{
		return COL_SELECTED;
	}
	
	public MultiSelectRow getRowAt( int row )
	{
		return (MultiSelectRow)rows.get(row);
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
/*                
				if( prg.hasDirectGears() )
					return prg.getGearNum();
				else
					return new Integer(1);
*/
			}

			case COL_STATE:
				return prg.getBaseProgram().getProgramStatusString( 
						prg.getBaseProgram().getProgramStatus().intValue() );				
		}
	
		//unknown thing here!
		return null;			
	}

	private String getCurrentGear( IGearProgram dPrg )
	{
		LMProgramDirectGear gear = null;
		
		//get the current gear we are in
		for( int i = 0; i < dPrg.getDirectGearVector().size(); i++ )
		{			
			gear = (LMProgramDirectGear)dPrg.getDirectGearVector().get(i);
	
			if( dPrg.getCurrentGearNumber().intValue() == gear.getGearNumber().intValue() )
			{
				//return gear.getGearName();
				return gear.getGearNumber().toString();
			}			
		}
	
		//should not get here
		com.cannontech.clientutils.CTILogger.info(
						"*** Unable to find gear #: " + gear.getGearNumber() + 
						" in " + this.getClass().getName() );
	
		return "(Gear #" + gear.getGearNumber() + " not Found)";	
	}

	public void addRow( MultiSelectRow obj )
	{
		if( obj != null )
			rows.add(obj);
	}

	public void clear()
	{
		rows.clear();
	}

	public int getRowCount()
	{
		return rows.size();
	}

	public int getColumnCount()
	{
		return COLUMNS.length;
	}

	public String getColumnName(int col)
	{
		return COLUMNS[col];
	}
	
	public void setValueAt(Object val, int row, int col) 
	{
		if( col == COL_SELECTED )
			getRowAt(row).setIsSelected( (Boolean)val );

		if( col == COL_GEAR )
		{
			MultiSelectRow ms = (MultiSelectRow)getRowAt(row);
			
            if( val instanceof LMProgramDirectGear )
            {
                ((MultiSelectProg)ms.getObject()).setGearNum(
							((LMProgramDirectGear)val).getGearNumber() );
            }			
            else if( val instanceof Integer )
            {
                ((MultiSelectProg)ms.getObject()).setGearNum( (Integer)val );
            }
            
		}

		
	}
	
	public void setAllGearNumbers( Integer val )
	{
		for( int i = 0; i < getRowCount(); i++ )
			setGearNumber( i, val );		
	}


	public void setGearNumber( int row, Integer val )
	{
		setValueAt( val, row, COL_GEAR );
	}


}

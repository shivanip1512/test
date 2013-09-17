package com.cannontech.loadcontrol.displays;

import javax.swing.table.TableModel;

import com.cannontech.common.tdc.model.IDisplay;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LCDisplayItem implements IDisplay
{
	//indexes into the array of TableModels
	public static final int TYPE_CONTROL_AREA		= 0;
	public static final int TYPE_PROGRAM			= 1;
	public static final int TYPE_GROUP				= 2;
	

	private String name = null;
	private TableModel[] localTableModels = null;
	

	/**
	 * Constructor for LCDisplayItem.
	 */
	public LCDisplayItem( String name_ )
	{
		super();
		setName( name_ );
	}

	/**
	 * Constructor for LCDisplayItem.
	 */
	public LCDisplayItem( String name_, TableModel[] tableModel_ )
	{
		this( name_ );
		setLocalTableModels( tableModel_ );
	}


	@Override
    public String toString()
	{
		return getName();
	}
	
	/**
	 * Returns the name.
	 * @return String
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Returns the localTableModel.
	 * @return TableModel
	 */
	public TableModel[] getLocalTableModels()
	{
		return localTableModels;
	}

	/**
	 * Sets the localTableModel.
	 * @param localTableModel The localTableModel to set
	 */
	public void setLocalTableModels(TableModel[] localTableModels)
	{
		this.localTableModels = localTableModels;
	}

}

package com.cannontech.common.gui.panel;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class MultiSelectRow 
{
	private Object obj = null;
	private Boolean isChecked = Boolean.FALSE;

	public MultiSelectRow( Object obj_ )
	{ 
		super();
		obj = obj_;
		//isChecked = checked;
	}

	public Object getObject()
	{
		return obj;
	}
	public Boolean isChecked()
	{
		return isChecked;
	}

	public void setIsSelected( Boolean selected )
	{
		isChecked = selected;
	}

}

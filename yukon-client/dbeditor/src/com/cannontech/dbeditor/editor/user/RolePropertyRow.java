package com.cannontech.dbeditor.editor.user;

import com.cannontech.database.data.lite.LiteYukonRoleProperty;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RolePropertyRow
{
	private LiteYukonRoleProperty liteProperty = null;
	private String value = null;


	public RolePropertyRow( LiteYukonRoleProperty liteProp_ )
	{
		super();
		setLiteProperty( liteProp_ );
		
		if( liteProp_ == null )
			throw new IllegalArgumentException("Can not create a new instance of " + 
					this.getClass().getName() + " with a null value in the constructor");
	}
	

	/**
	 * Returns the liteProperty.
	 * @return LiteYukonRoleProperty
	 */
	public LiteYukonRoleProperty getLiteProperty()
	{
		return liteProperty;
	}

	/**
	 * Returns the value.
	 * @return String
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the liteProperty.
	 * @param liteProperty The liteProperty to set
	 */
	private void setLiteProperty(LiteYukonRoleProperty liteProperty)
	{
		this.liteProperty = liteProperty;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(String value)
	{
		this.value = value;
	}



	/**
	 * Insert the method's description here.
	 * Creation date: (11/17/00 4:28:38 PM)
	 * @return boolean
	 */
	public boolean equals(Object o)
	{
		if( o == null )
			return false;
		else if( o instanceof RolePropertyRow )
		{
			return getLiteProperty().equals( ((RolePropertyRow)o).getLiteProperty() );
		}
		else
			return false;
	}
	
	/**
	 * keep this consistent with .equals() pleez
	 * o1.equals(o2) => o1.hashCode() == o2.hashCode()
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() 
	{
		return getLiteProperty().hashCode();
	}

}

package com.cannontech.dbeditor.editor.user;

import com.cannontech.common.gui.tree.CheckNode;
import com.cannontech.database.data.lite.LiteBase;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LiteBaseNode extends CheckNode 
{
	private String userValue = null;
	

	/**
	 * Constructor for RoleNode.
	 * @param userObject
	 */
	public LiteBaseNode(LiteBase lite_ ) 
	{
		super( lite_ );
	}

	/**
	 * Constructor for RoleNode.
	 * @param userObject
	 */
	public LiteBaseNode(LiteBase lite_, String userValue_ ) 
	{
		this( lite_ );
		setUserValue( userValue_ );
	}
	
	/**
	 * Returns the userValue.
	 * @return String
	 */
	public String getUserValue() {
		return userValue;
	}

	/**
	 * Sets the userValue.
	 * @param userValue The userValue to set
	 */
	public void setUserValue(String userValue) {
		this.userValue = userValue;
	}

	public void setUserObject( Object obj )
	{
		if( obj instanceof LiteBase )			
			super.setUserObject( obj );
		else
			throw new IllegalArgumentException("Only " + 
					LiteBase.class.getName() + " allowed for the user object.");
	}

	public Object getUserObject()
	{		
		return super.getUserObject();
	}
	
}

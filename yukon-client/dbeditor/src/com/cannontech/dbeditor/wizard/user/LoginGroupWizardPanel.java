package com.cannontech.dbeditor.wizard.user;

import com.cannontech.dbeditor.editor.user.GroupRoleBasePanel;
import com.cannontech.dbeditor.editor.user.UserRolePanel;

/**
 * This type was created in VisualAge.
 */
public class LoginGroupWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private GroupRoleBasePanel groupRoleBasePanel;
	private UserRolePanel userRolePanel;

	/**
	 * UserLoginBasePanel constructor comment.
	 */
	public LoginGroupWizardPanel() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (5/4/2001 11:11:28 AM)
	 * @return java.awt.Dimension
	 */
	public java.awt.Dimension getActualSize() 
	{
		setPreferredSize( new java.awt.Dimension(410, 480) );
	
		return getPreferredSize();
	}
	/** 
	 * This method was created in VisualAge.
	 * @return UserLoginBasePanel
	 */
	public GroupRoleBasePanel getGroupRoleBasePanel() {
		
		if( groupRoleBasePanel == null )
			groupRoleBasePanel = new GroupRoleBasePanel(false);
			
		return groupRoleBasePanel;
	}
	/** 
	 * This method was created in VisualAge.
	 * @return UserLoginBasePanel
	 */
	public UserRolePanel getUserRolePanel() {
		
		if( userRolePanel == null )
			userRolePanel = new UserRolePanel();
			
		return userRolePanel;
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	protected String getHeaderText() {
		return "Login Group Setup";
	}
	/**
	 * getNextInputPanel method comment.
	 */
	protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {
	
		if( currentInputPanel == null ) {
			getGroupRoleBasePanel().setFirstFocus();
            return getGroupRoleBasePanel();
		
		} else if( currentInputPanel == getGroupRoleBasePanel()) {
			getUserRolePanel().setFirstFocus();
            return getUserRolePanel();

		} else {
			throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
		}
	}
	/**
	 * isLastInputPanel method comment.
	 */
	protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
		return ( currentPanel == getUserRolePanel() );
	}
}

package com.cannontech.dbeditor.wizard.user;

import com.cannontech.dbeditor.editor.user.UserLoginBasePanel;

/**
 * This type was created in VisualAge.
 */
public class YukonUserWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private UserLoginBasePanel userLoginBasePanel;

	/**
	 * YukonUserWizardPanel constructor comment.
	 */
	public YukonUserWizardPanel() {
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
	public UserLoginBasePanel getUserLoginBasePanel() {
		
		if( userLoginBasePanel == null )
			userLoginBasePanel = new UserLoginBasePanel();
		
		return userLoginBasePanel;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	protected String getHeaderText() {
		return "User Setup";
	}
	/**
	 * getNextInputPanel method comment.
	 */
	protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {
	
		if( currentInputPanel == null )
		{
			return getUserLoginBasePanel();
		}
		else
			throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
	}
	/**
	 * isLastInputPanel method comment.
	 */
	protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
	
		return ( (currentPanel == getUserLoginBasePanel()) );
	}
}

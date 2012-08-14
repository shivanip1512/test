package com.cannontech.dbeditor.wizard.user;

import com.cannontech.dbeditor.editor.user.UserGroupBasePanel;

public class UserGroupWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private UserGroupBasePanel userGroupBasePanel;

	public UserGroupWizardPanel() {
		super();
	}
	
	public java.awt.Dimension getActualSize() {
		setPreferredSize( new java.awt.Dimension(410, 480) );
		return getPreferredSize();
	}
	
	public UserGroupBasePanel getUserGroupBasePanel() {
		if( userGroupBasePanel == null ) {
		    userGroupBasePanel = new UserGroupBasePanel(false);
		}
		
		return userGroupBasePanel;
	}
	
	protected String getHeaderText() {
		return "User Group Setup";
	}

	protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {
	
		if( currentInputPanel == null ) {
			getUserGroupBasePanel().setFirstFocus();
            return getUserGroupBasePanel();
		
		} else {
			throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
		}
	}

	protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
		return ( currentPanel == getUserGroupBasePanel() );
	}
}
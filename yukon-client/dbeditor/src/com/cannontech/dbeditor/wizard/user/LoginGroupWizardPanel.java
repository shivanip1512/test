package com.cannontech.dbeditor.wizard.user;

import com.cannontech.dbeditor.editor.user.GroupRoleBasePanel;
import com.cannontech.dbeditor.editor.user.UserRolePanel;

public class LoginGroupWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private GroupRoleBasePanel groupRoleBasePanel;
	private UserRolePanel userRolePanel;

	public LoginGroupWizardPanel() {
		super();
	}
	
	public java.awt.Dimension getActualSize() {
		setPreferredSize( new java.awt.Dimension(410, 480) );
		return getPreferredSize();
	}
	
	public GroupRoleBasePanel getGroupRoleBasePanel() {
		if( groupRoleBasePanel == null ) {
			groupRoleBasePanel = new GroupRoleBasePanel(false);
		}
		
		return groupRoleBasePanel;
	}
	
	public UserRolePanel getUserRolePanel() {
		if( userRolePanel == null ) {
			userRolePanel = new UserRolePanel();
		}
		
		return userRolePanel;
	}
	
	protected String getHeaderText() {
		return "Role Group Setup";
	}

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

	protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
		return ( currentPanel == getUserRolePanel() );
	}
}

package com.cannontech.dbeditor.wizard.notification.group;

/**
 * Insert the type's description here.
 * Creation date: (11/16/00 12:50:47 PM)
 * @author: 
 */
public class NotificationGroupWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	//private GroupNameTypePanel groupGroupNameTypePanel;
	private GroupEmailSetup groupEmailSetup;
	private GroupPagerSetup groupPagerSetup;
/**
 * NotificationGroupWizardPanel constructor comment.
 */
public NotificationGroupWizardPanel() {
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
 * Insert the method's description here.
 * Creation date: (11/16/00 4:23:02 PM)
 * @return com.cannontech.dbeditor.wizard.notification.group.GroupEmailSetup
 */
public GroupEmailSetup getGroupEmailSetup() 
{
	if( groupEmailSetup == null )
		groupEmailSetup = new GroupEmailSetup();
		
	return groupEmailSetup;
}
/**
 * Insert the method's description here.
 * Creation date: (11/16/00 4:23:02 PM)
 * @return com.cannontech.dbeditor.wizard.notification.group.GroupPagerSetup
 */
public GroupPagerSetup getGroupPagerSetup() 
{
	if( groupPagerSetup == null )
		groupPagerSetup = new GroupPagerSetup();

	return groupPagerSetup;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Notification Group Setup";
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.common.gui.util.InputPanel
 * @param currentInputPanel com.cannontech.common.gui.util.InputPanel
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) 
{
	if( currentInputPanel == null )
	{
/*		return getGroupNameTypePanel();
	}
	else if( currentInputPanel == getGroupNameTypePanel() )
	{*/
		return getGroupEmailSetup();

		/*
		//Depending on the group type selected
		int type = getGroupNameTypePanel().getSelectedType();

		switch( type )
		{
			case GroupNameTypePanel.EMAIL_TYPE:
				return getGroupEmailSetup();

			case GroupNameTypePanel.PAGER_TYPE:
				//getPointStatusSettingsPanel().setValue(null);
				return getGroupPagerSetup();

			default:
				throw new Error( getClass() + "::"+ "getNextInputPanel() - Unrecognized point type:  " + type );
		}
		*/
	}
	else if( (currentInputPanel == getGroupEmailSetup()) )/* &&
			 (getGroupNameTypePanel().getSelectedType() == GroupNameTypePanel.PAGER_TYPE) )*/
	{
		return getGroupPagerSetup();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param currentPanel com.cannontech.common.gui.util.DataInputPanel
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return ( currentPanel == getGroupEmailSetup() );
}
}

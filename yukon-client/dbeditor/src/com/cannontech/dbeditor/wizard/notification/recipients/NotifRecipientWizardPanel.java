package com.cannontech.dbeditor.wizard.notification.recipients;

/**
 * Insert the type's description here.
 * Creation date: (11/22/00 12:18:12 PM)
 * @author: 
 */
public class NotifRecipientWizardPanel extends com.cannontech.common.wizard.EditorlessWizardPanel
{
	private NotifRecipientEmailPanel notifRecipientEmailPanel;
/**
 * DestinationLocationWizardPanel constructor comment.
 */
public NotifRecipientWizardPanel() {
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
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Notification Recipients Setup";
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
		return getNotifRecipientEmailPanel();
	}
	else
		throw new Error(getClass() + "::getNextInputPanel - Unable to determine next DataInputPanel");
}
/**
 * Insert the method's description here.
 * Creation date: (11/22/00 12:29:58 PM)
 * @return com.cannontech.dbeditor.wizard.notification.destination.NotifRecipientEmailPanel
 */
public NotifRecipientEmailPanel getNotifRecipientEmailPanel() 
{
	if( notifRecipientEmailPanel == null )
		notifRecipientEmailPanel = new NotifRecipientEmailPanel();
		
	return notifRecipientEmailPanel;
}
/**
 * This method was created in VisualAge.
 * @return boolean
 * @param currentPanel com.cannontech.common.gui.util.DataInputPanel
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return ( currentPanel == getNotifRecipientEmailPanel() );
}
}

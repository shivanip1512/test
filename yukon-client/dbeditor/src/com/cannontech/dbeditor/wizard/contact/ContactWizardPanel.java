package com.cannontech.dbeditor.wizard.contact;

import com.cannontech.dbeditor.wizard.customer.AddressPanel;

/**
 * Insert the type's description here.
 * Creation date: (11/22/00 12:18:12 PM)
 * @author: 
 */
public class ContactWizardPanel extends com.cannontech.common.wizard.EditorlessWizardPanel
{
	private ContactPanel contactPanel;
	private AddressPanel addressPanel;
	
	
/**
 * ContactWizardPanel constructor comment.
 */
public ContactWizardPanel() {
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
 * Creation date: (11/22/00 12:29:58 PM)
 * @return com.cannontech.dbeditor.wizard.notification.destination.NotifRecipientEmailPanel
 */
public ContactPanel getContactPanel() 
{
	if( contactPanel == null )
		contactPanel = new ContactPanel();
		
	return contactPanel;
}

public AddressPanel getAddressPanel() 
{
	if( addressPanel == null )
		addressPanel = new AddressPanel();
		
	return addressPanel;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Contact Setup";
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
		return getContactPanel();
	}
	else if( currentInputPanel == getContactPanel() )
	{
		return getAddressPanel();
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
	return ( currentPanel == getAddressPanel() );
}
}

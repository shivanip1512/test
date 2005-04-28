package com.cannontech.dbeditor.wizard.copy.lm;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.device.lm.LMGroup;

/* All Panels used in this WizardPanel MUST be able to handle MultiDBPersistent  */
/*   Objects in their getValue(Object o) method!!! */
public class LMGroupCopyWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private LMGroupCopyNameRoutePanel lmGroupCopyNameSettingsPanel;
	
	private com.cannontech.database.db.DBPersistent copyObject = null;
	private String groupType;

/**
 * DeviceWizardPanel constructor comment.
 */
public LMGroupCopyWizardPanel(com.cannontech.database.db.DBPersistent objectToCopy) 
{
	super();

	setCopyObject( objectToCopy );
	setGroupType();
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
 */
public com.cannontech.database.db.DBPersistent getCopyObject() {
		
	return copyObject;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected LMGroupCopyNameRoutePanel getLMGroupCopyNameSettingsPanel() {
	if( lmGroupCopyNameSettingsPanel == null )
		lmGroupCopyNameSettingsPanel = new LMGroupCopyNameRoutePanel();
		
	return lmGroupCopyNameSettingsPanel;
}

public String getGroupType()
{
	return groupType;
}

protected String getHeaderText() {
	return "Copy LM Group";
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public java.awt.Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(
	com.cannontech.common.gui.util.DataInputPanel currentInputPanel)
{

	if (currentInputPanel == null)
	{
		return getLMGroupCopyNameSettingsPanel();
	}	
	else
		throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	return super.getValue( getCopyObject() );
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
{
	return currentPanel == getLMGroupCopyNameSettingsPanel();
}

  /**
 * This method was created in VisualAge.
 */
public void setCopyObject(com.cannontech.database.db.DBPersistent newObject) 
{
	try 
	{ 
		copyObject = newObject;
	 	
		com.cannontech.database.Transaction t = 
				com.cannontech.database.Transaction.createTransaction(
					com.cannontech.database.Transaction.RETRIEVE, copyObject);

		copyObject = t.execute();
	}
	catch (com.cannontech.database.TransactionException e) 
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
 	
}  
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 9:31:56 AM)
 */
public void setGroupType()
{
	groupType = ((LMGroup)getCopyObject()).getPAOType();
}
}

package com.cannontech.dbeditor.wizard.changetype.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.DataInputPanelEvent;
import com.cannontech.dbeditor.wizard.device.lmgroup.*;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.editor.*;

public class PointChangeTypeWizardPanel extends WizardPanel {

	private com.cannontech.database.db.DBPersistent changeObject = null;



	private PointTypePanel pointTypePanel;

/**
 * DeviceWizardPanel constructor comment.
 */
public PointChangeTypeWizardPanel(com.cannontech.database.db.DBPersistent objectToChange)
{
	super();
	setChangeObject(objectToChange);
	getPointTypePanel().setButtons(objectToChange);

}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 11:11:28 AM)
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
public com.cannontech.database.db.DBPersistent getChangeObject() {
		
	return changeObject;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Change Point Type";
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
		return getPointTypePanel();

	else
		throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected PointTypePanel getPointTypePanel()
{
	if (pointTypePanel == null)
		pointTypePanel = new PointTypePanel();

	return pointTypePanel;
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel)
{
	boolean editPointID = false;

	if (currentPanel == getPointTypePanel())
		return true;
	else
	{
		return false;
	}
}
  /**
 * This method was created in VisualAge.
 */
public void setChangeObject(com.cannontech.database.db.DBPersistent newObject) 
{
 	try 
 	{ 
	 	newObject.setDbConnection(com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias()));
	 	changeObject = newObject;
	 	changeObject.retrieve();
 	}
 	catch (java.sql.SQLException e) 
 	{
	 	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
 	}
 	finally
 	{
	 	if ( changeObject != null ) 
	 	{
	 		try
	 		{
	 			changeObject.getDbConnection().close();
		 		changeObject.setDbConnection(null);
	 		}
	 		catch (java.sql.SQLException e)
	 		{
		 		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	 		}
	 	}
 	}
 } 
}

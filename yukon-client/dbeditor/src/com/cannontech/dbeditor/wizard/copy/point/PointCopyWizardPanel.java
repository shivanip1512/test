package com.cannontech.dbeditor.wizard.copy.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.lite.LiteBase;

public class PointCopyWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private PointCopyNameDevicePanel pointCopyNameDevicePanel;
	private com.cannontech.database.db.DBPersistent copyObject = null;
	private PointCopyOffsetsPanel pointCopyOffsetsPanel;

	private int pointDeviceID = -1;
	private int pointType;
	
/**
 * DeviceWizardPanel constructor comment.
 */
public PointCopyWizardPanel(com.cannontech.database.db.DBPersistent objectToCopy)
{
	super();
	setCopyObject(objectToCopy);
	initialize();

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
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Copy Point";
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
		getPointCopyNameDevicePanel();
		getPointCopyNameDevicePanel().setValueCore(null);
		return getPointCopyNameDevicePanel();
	}
	else if (currentInputPanel == getPointCopyNameDevicePanel())
	{
		getPointCopyOffsetsPanel().setCopyValue(getCopyObject(), ((LiteBase)getPointCopyNameDevicePanel().getSelectedDevice()).getLiteID());
		return getPointCopyOffsetsPanel();
	}
	else
		throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected PointCopyNameDevicePanel getPointCopyNameDevicePanel() {
	if( pointCopyNameDevicePanel == null )
		pointCopyNameDevicePanel = new PointCopyNameDevicePanel();
		
	return pointCopyNameDevicePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (6/5/2001 9:33:10 AM)
 * @return int
 */
public int getPointDeviceID()
{
	return pointDeviceID;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointPhysicalSettingsPanel
 */
protected PointCopyOffsetsPanel getPointCopyOffsetsPanel() {
	if( pointCopyOffsetsPanel == null )
		pointCopyOffsetsPanel = new PointCopyOffsetsPanel();
		
	return pointCopyOffsetsPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (6/5/2001 9:40:05 AM)
 * @return int
 */
public int getPointType()
{
	return pointType;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	return super.getValue( getCopyObject() );
}
/**
 * Insert the method's description here.
 * Creation date: (6/5/2001 9:46:53 AM)
 */
public void initialize()
{
	setPointType();
	setPointDeviceID(getCopyObject());
	getPointCopyNameDevicePanel().setSelectedDeviceIndex(getPointDeviceID());

}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel)
{
	if ((currentPanel == getPointCopyNameDevicePanel()) 
		&& ((getPointType() == PointTypes.CALCULATED_POINT) || (getPointType() == PointTypes.CALCULATED_STATUS_POINT)))
		return true;
	else if (currentPanel == getPointCopyNameDevicePanel())
		return false;
	else
		return true;
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
 * Creation date: (6/5/2001 8:19:35 AM)
 * @param val java.lang.Object
 */
public void setPointDeviceID(Object val)
{
	PointBase point = (PointBase)val;

	LitePoint lPoint = PointFuncs.getLitePoint( point.getPoint().getPointID().intValue() );
	
	pointDeviceID = lPoint.getPaobjectID();
}

/**
 * Insert the method's description here.
 * Creation date: (6/5/2001 9:41:41 AM)
 */
public void setPointType()
{
	pointType = com.cannontech.database.data.point.PointTypes.getType(((PointBase) getCopyObject()).getPoint().getPointType());
}
}

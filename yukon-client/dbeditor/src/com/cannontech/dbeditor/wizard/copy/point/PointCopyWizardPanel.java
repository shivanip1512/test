package com.cannontech.dbeditor.wizard.copy.point;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.dbeditor.wizard.point.PointPhysicalSettingsPanel;
import com.cannontech.dbeditor.wizard.point.PointStatusPhysicalSettingsPanel;

public class PointCopyWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private PointCopyNameDevicePanel pointCopyNameDevicePanel;
	private com.cannontech.database.db.DBPersistent copyObject = null;
	private PointPhysicalSettingsPanel pointPhysicalSettingsPanel;
	private PointStatusPhysicalSettingsPanel pointStatusPhysicalSettingsPanel;
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

		switch (getPointType())
		{
			case PointTypes.PULSE_ACCUMULATOR_POINT :
			case PointTypes.DEMAND_ACCUMULATOR_POINT :
			case PointTypes.ANALOG_POINT :

				getPointPhysicalSettingsPanel().setValue(null);
				getPointPhysicalSettingsPanel().reinitialize(
					new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject) getPointCopyNameDevicePanel().getSelectedDevice()).getYukonID()),
					getPointType());
				return getPointPhysicalSettingsPanel();

			case PointTypes.STATUS_POINT :
				getPointStatusPhysicalSettingsPanel().setValue(null);
				getPointStatusPhysicalSettingsPanel().reinitialize(
					new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject) getPointCopyNameDevicePanel().getSelectedDevice()).getYukonID()),
					getPointType());
				return getPointStatusPhysicalSettingsPanel();

			default :
				throw new Error(getClass() + "::" + "getNextInputPanel() - Unrecognized point type:  ");
		}
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
protected PointPhysicalSettingsPanel getPointPhysicalSettingsPanel()
{
	if (pointPhysicalSettingsPanel == null)
		pointPhysicalSettingsPanel = new PointPhysicalSettingsPanel();

	return pointPhysicalSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointStatusPhysicalSettingsPanel
 */
protected PointStatusPhysicalSettingsPanel getPointStatusPhysicalSettingsPanel()
{

	if (pointStatusPhysicalSettingsPanel == null)
		pointStatusPhysicalSettingsPanel = new PointStatusPhysicalSettingsPanel();

	return pointStatusPhysicalSettingsPanel;
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
	if ((currentPanel == getPointCopyNameDevicePanel()) && (getPointType() == PointTypes.CALCULATED_POINT))
		return true;
	else if (currentPanel == getPointCopyNameDevicePanel())
		return false;
	else
		return true;
}
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {
	try
{
	javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
	com.cannontech.database.db.DBPersistent temp = new com.cannontech.database.data.point.PointBase();
	PointCopyWizardPanel p = new PointCopyWizardPanel(temp);

	javax.swing.JFrame f= new javax.swing.JFrame();

	f.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	f.getContentPane().add( p );
	f.pack();

	f.show();
}
catch( Throwable t)
{
	com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
}
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
	com.cannontech.database.cache.DefaultDatabaseCache cache =
		com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	java.util.List allPoints = cache.getAllPoints();
	PointBase point = ((PointBase) val);

	//matches pointIDs and grabs DeviceID from point in the cache
	synchronized (cache)
	{

		for (int j = 0; j < allPoints.size(); j++)
		{
			if (((com.cannontech.database.data.lite.LitePoint) allPoints.get(j)).getPointID() == point.getPoint().getPointID().intValue())
			{
				pointDeviceID = ((com.cannontech.database.data.lite.LitePoint) allPoints.get(j)).getPaobjectID();
				break;

			}

		}
	}

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

package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.gui.util.DataInputPanelListener;
import com.cannontech.common.gui.util.DataInputPanelEvent;
import com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupVersacomEditorPanel;

/* All Panels used in this WizardPanel MUST be able to handle MultiDBPersistent  */
/*   Objects in their getValue(Object o) method!!! */
public class DeviceCopyWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private DeviceCopyNameAddressPanel deviceCopyNameAddressPanel;
	private DeviceCopyPointPanel deviceCopyPointPanel;
	
	private LMGroupVersacomEditorPanel lmGroupVersacomEditorPanel;
	private RoutePanel routePanel;
	private GoldPanel goldPanel;
	private GoldSilverPanel goldSilverPanel; 
	private EmetconRelayPanel emetconRelayPanel;

	
	private com.cannontech.database.db.DBPersistent copyObject = null;
	private int deviceType;
	private Character addressUsage;
/**
 * DeviceWizardPanel constructor comment.
 */
public DeviceCopyWizardPanel(com.cannontech.database.db.DBPersistent objectToCopy) 
{
	super();

	setCopyObject( objectToCopy );
	setDeviceType();
	if (objectToCopy instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon)
		setAddressUsage();
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
 * Creation date: (6/7/2001 11:37:06 AM)
 * @return java.lang.Character
 */
public Character getAddressUsage()
{
	return addressUsage;
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
protected DeviceCopyNameAddressPanel getDeviceCopyNameAddressPanel() {
	if( deviceCopyNameAddressPanel == null )
		deviceCopyNameAddressPanel = new DeviceCopyNameAddressPanel();
		
	return deviceCopyNameAddressPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected DeviceCopyPointPanel getDeviceCopyPointPanel() {
	if( deviceCopyPointPanel == null )
		deviceCopyPointPanel = new DeviceCopyPointPanel();
		
	return deviceCopyPointPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 9:30:52 AM)
 * @return int
 */
public int getDeviceType()
{
	return deviceType;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.lm.EmetconRelayPanel
 */
public EmetconRelayPanel getEmetconRelayPanel()
{
	if (emetconRelayPanel == null)
	{
		emetconRelayPanel = new EmetconRelayPanel();
	}

	return emetconRelayPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.lm.GoldPanel
 */
public GoldPanel getGoldPanel()
{
	if (goldPanel == null)
	{
		goldPanel = new GoldPanel();
	}

	return goldPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.lm.GoldSilverPanel
 */
public GoldSilverPanel getGoldSilverPanel()
{
	if (goldSilverPanel == null)
	{
		goldSilverPanel = new GoldSilverPanel();
	}

	return goldSilverPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Copy Device";
}
/**
 * Insert the method's description here.
 * Creation date: (10/11/2001 10:54:21 AM)
 * @return LMGroupVersacomEditorPanel
 */
public LMGroupVersacomEditorPanel getLmGroupVersacomEditorPanel() 
{
	if( lmGroupVersacomEditorPanel == null )
		lmGroupVersacomEditorPanel = new LMGroupVersacomEditorPanel();

	return lmGroupVersacomEditorPanel;
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
		return getDeviceCopyNameAddressPanel();
	}	
	else if ( currentInputPanel == getDeviceCopyNameAddressPanel()
				&& getDeviceType() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM )
	{	
		getLmGroupVersacomEditorPanel().setAddresses(((com.cannontech.database.data.device.lm.LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getUtilityAddress(),
												((com.cannontech.database.data.device.lm.LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getSectionAddress(),
												((com.cannontech.database.data.device.lm.LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getClassAddress(),
												((com.cannontech.database.data.device.lm.LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getDivisionAddress());

		getLmGroupVersacomEditorPanel().setRelay( ((com.cannontech.database.data.device.lm.LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getRelayUsage() );
		
		return getLmGroupVersacomEditorPanel();
	}	
	else if ( currentInputPanel == getDeviceCopyNameAddressPanel()
				 && getDeviceType() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EMETCON )
	{
		if (getAddressUsage().charValue() == 'S')
		{

			getGoldSilverPanel().setGoldSilverSpinnerValues(((com.cannontech.database.data.device.lm.LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getGoldAddress(), ((com.cannontech.database.data.device.lm.LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getSilverAddress());
			return getGoldSilverPanel();
		}	
		else
		{
			getGoldPanel().setGoldSpinnerValue(((com.cannontech.database.data.device.lm.LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getGoldAddress());
			return getGoldPanel();

		}
	}
	else if( currentInputPanel == getGoldSilverPanel() 
			   || currentInputPanel == getGoldPanel() )
	{
		getEmetconRelayPanel().setRelay(((com.cannontech.database.data.device.lm.LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getRelayUsage());
		return getEmetconRelayPanel();
	}
	else if( currentInputPanel == getLmGroupVersacomEditorPanel() 
				|| currentInputPanel == getEmetconRelayPanel() )
	{	
		getRoutePanel().setValue(null);

		if (getCopyObject() instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon)
			getRoutePanel().setRoute(((com.cannontech.database.data.device.lm.LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getRouteID());
		else
			getRoutePanel().setRoute(((com.cannontech.database.data.device.lm.LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getRouteID());

		return getRoutePanel();
	}
	else if( currentInputPanel == getDeviceCopyNameAddressPanel() 
				&& getDeviceCopyNameAddressPanel().copyPointsIsChecked() )
	{
		getDeviceCopyPointPanel().setValue(copyObject);
		return getDeviceCopyPointPanel();
	}
	else
		throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.lm.RoutePanel
 */
public RoutePanel getRoutePanel()
{
	if (routePanel == null)
	{
		routePanel = new RoutePanel();
	}

	return routePanel;
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
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
	boolean editPointID = false;
	try
	{
		java.util.ResourceBundle res = java.util.ResourceBundle.getBundle("config");
		String editPointIDString = res.getString("point_id_edit").toLowerCase();
		if( editPointIDString.equals("true") )
			editPointID = true;
	}
	catch( java.util.MissingResourceException se )
	{
		com.cannontech.clientutils.CTILogger.error( se.getMessage(), se );
	}


	if( currentPanel == getRoutePanel() )
	{
		return true;
	}
	else	
	if( editPointID )
	{
		return ( (currentPanel == getDeviceCopyNameAddressPanel() && (!getDeviceCopyNameAddressPanel().copyPointsIsChecked())) ||
							(currentPanel == getDeviceCopyPointPanel()) );
	}
	else 
	if ((currentPanel == getDeviceCopyNameAddressPanel()) && !((getDeviceType() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_EMETCON)
		 || (getDeviceType() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM)) )
	{
		return true;
	}
	else 
	{ 
		return false;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 11:37:26 AM)
 */
public void setAddressUsage()
{
	addressUsage = ((com.cannontech.database.data.device.lm.LMGroupEmetcon) getCopyObject()).getLmGroupEmetcon().getAddressUsage();
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

		t.execute();
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
public void setDeviceType()
{

	deviceType = com.cannontech.database.data.pao.PAOGroups.getDeviceType(
		((com.cannontech.database.data.device.DeviceBase) getCopyObject()).getPAOType() );
}
}

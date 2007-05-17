package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */

import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.common.wizard.WizardPanelEvent;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.lm.LMGroupEmetcon;
import com.cannontech.database.data.device.lm.LMGroupVersacom;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.dbeditor.DatabaseEditor;
import com.cannontech.dbeditor.wizard.device.DeviceRoutePanel;
import com.cannontech.dbeditor.wizard.device.capcontrol.CapBankCntrlCreationPanel;
import com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupVersacomEditorPanel;

/* All Panels used in this WizardPanel MUST be able to handle MultiDBPersistent  */
/*   Objects in their getValue(Object o) method!!! */
public class DeviceCopyWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private DeviceMeterGroupPanel deviceMeterGroupPanel;
    private DeviceCopyNameAddressPanel deviceCopyNameAddressPanel;
	private DeviceCopyPointPanel deviceCopyPointPanel;
	private CapBankCntrlCreationPanel capBankCntrlCreationPanel;
	
	private LMGroupVersacomEditorPanel lmGroupVersacomEditorPanel;
	private RoutePanel routePanel;
	private GoldPanel goldPanel;
	private GoldSilverPanel goldSilverPanel; 
	private EmetconRelayPanel emetconRelayPanel;

	private DeviceRoutePanel deviceRoutePanel = null;
	
	private com.cannontech.database.db.DBPersistent copyObject = null;
	private int deviceType;
	private Character addressUsage;
	private boolean isCapBank = false;
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
	if(objectToCopy instanceof com.cannontech.database.data.capcontrol.CapBank)
		isCapBank = true;
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

protected DeviceMeterGroupPanel getDeviceMeterGroupPanel() {
    if( deviceMeterGroupPanel == null )
        deviceMeterGroupPanel = new DeviceMeterGroupPanel();
        
    return deviceMeterGroupPanel;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceRoutePanel
 */
protected DeviceRoutePanel getDeviceRoutePanel() {
    if( deviceRoutePanel  == null )
        deviceRoutePanel = new DeviceRoutePanel();
        
    return deviceRoutePanel;
}

protected com.cannontech.dbeditor.wizard.device.capcontrol.CapBankCntrlCreationPanel getCapBankCntrlCreationPanel() {
	if( capBankCntrlCreationPanel == null )
		capBankCntrlCreationPanel = new CapBankCntrlCreationPanel();
		
	return capBankCntrlCreationPanel;
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
protected DataInputPanel getNextInputPanel(
	DataInputPanel currentInputPanel)
{

    if (currentInputPanel == null)
	{
		return getDeviceCopyNameAddressPanel();
	}	
	else if ( currentInputPanel == getDeviceCopyNameAddressPanel()
				&& (getDeviceType() == DeviceTypes.REPEATER 
                || getDeviceType() == DeviceTypes.REPEATER_902
                || getDeviceType() == DeviceTypes.REPEATER_800
                || getDeviceType() == DeviceTypes.REPEATER_801
                || getDeviceType() == DeviceTypes.REPEATER_921))
	{
        getDeviceRoutePanel().setValue(null);
        getDeviceRoutePanel().setFirstFocus();
	    return getDeviceRoutePanel();
    }
    else if ( currentInputPanel == getDeviceCopyNameAddressPanel()
            && getDeviceType() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_VERSACOM )
    {	
		getLmGroupVersacomEditorPanel().setAddresses(((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getUtilityAddress(),
												((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getSectionAddress(),
												((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getClassAddress(),
												((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getDivisionAddress());

		getLmGroupVersacomEditorPanel().setRelay( ((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getRelayUsage() );
		getLmGroupVersacomEditorPanel().setFirstFocus();
		return getLmGroupVersacomEditorPanel();
	}	
	else if ( currentInputPanel == getDeviceCopyNameAddressPanel()
				 && getDeviceType() == PAOGroups.LM_GROUP_EMETCON )
	{
		if (getAddressUsage().charValue() == 'S')
		{

			getGoldSilverPanel().setGoldSilverSpinnerValues(((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getGoldAddress(), ((com.cannontech.database.data.device.lm.LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getSilverAddress());
            getGoldSilverPanel().setFirstFocus();
			return getGoldSilverPanel();
		}	
		else
		{
			getGoldPanel().setGoldSpinnerValue(((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getGoldAddress());
            getGoldPanel().setFirstFocus();
			return getGoldPanel();

		}
	}else if ( currentInputPanel == getDeviceCopyNameAddressPanel()
            && (getDeviceType() == DeviceTypes.MCT470
                    || getDeviceType() == DeviceTypes.MCT430S4
                    || getDeviceType() == DeviceTypes.MCT430A
                    || getDeviceType() == DeviceTypes.MCT430SN
                    || getDeviceType() == DeviceTypes.MCT410IL
                    || getDeviceType() == DeviceTypes.MCT410CL
                    || getDeviceType() == DeviceTypes.MCT410FL
                    || getDeviceType() == DeviceTypes.MCT410GL
                    || getDeviceType() == DeviceTypes.MCT370
                    || getDeviceType() == DeviceTypes.MCT360
                    || getDeviceType() == DeviceTypes.MCT318L
                    || getDeviceType() == DeviceTypes.MCT318
                    || getDeviceType() == DeviceTypes.MCT310IM
                    || getDeviceType() == DeviceTypes.MCT250
                    || getDeviceType() == DeviceTypes.MCT248
                    || getDeviceType() == DeviceTypes.MCT240
                    || getDeviceType() == DeviceTypes.MCT213
                    || getDeviceType() == DeviceTypes.MCT210))
    {
	    
        getRoutePanel().setValue(null);

        getRoutePanel().setRoute(((MCTBase)getCopyObject()).getDeviceRoutes().getRouteID());
        
        getRoutePanel().setFirstFocus();
        return getRoutePanel();
        
    }else if( currentInputPanel == getGoldSilverPanel() 
			   || currentInputPanel == getGoldPanel() )
	{
		getEmetconRelayPanel().setRelay(((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getRelayUsage());
        getEmetconRelayPanel().setFirstFocus();
		return getEmetconRelayPanel();
	}
	else if( currentInputPanel == getLmGroupVersacomEditorPanel() 
				|| currentInputPanel == getEmetconRelayPanel() )
	{	
		getRoutePanel().setValue(null);

		if (getCopyObject() instanceof LMGroupEmetcon)
			getRoutePanel().setRoute(((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getRouteID());
		else
			getRoutePanel().setRoute(((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getRouteID());
		getRoutePanel().setFirstFocus();
		return getRoutePanel();
	}

	else if( (currentInputPanel == getDeviceCopyNameAddressPanel() || currentInputPanel == getDeviceCopyPointPanel()) &&
	getDeviceType() == PAOGroups.CAPBANK)
    {
        getCapBankCntrlCreationPanel().setFirstFocus();
		return getCapBankCntrlCreationPanel();
    }else if( currentInputPanel == getRoutePanel() && (getDeviceType() == DeviceTypes.MCT470
                                                    || getDeviceType() == DeviceTypes.MCT430S4
                                                    || getDeviceType() == DeviceTypes.MCT430A
                                                    || getDeviceType() == DeviceTypes.MCT430SN
                                                    || getDeviceType() == DeviceTypes.MCT410IL
                                                    || getDeviceType() == DeviceTypes.MCT410CL
                                                    || getDeviceType() == DeviceTypes.MCT410FL
                                                    || getDeviceType() == DeviceTypes.MCT410GL
                                                    || getDeviceType() == DeviceTypes.MCT370
                                                    || getDeviceType() == DeviceTypes.MCT360
                                                    || getDeviceType() == DeviceTypes.MCT318L
                                                    || getDeviceType() == DeviceTypes.MCT318
                                                    || getDeviceType() == DeviceTypes.MCT310IM
                                                    || getDeviceType() == DeviceTypes.MCT250
                                                    || getDeviceType() == DeviceTypes.MCT248
                                                    || getDeviceType() == DeviceTypes.MCT240
                                                    || getDeviceType() == DeviceTypes.MCT213
                                                    || getDeviceType() == DeviceTypes.MCT210))
    {
        
        getDeviceMeterGroupPanel().setValue(getCopyObject());
        getDeviceMeterGroupPanel().setFirstFocus();
        return getDeviceMeterGroupPanel();
        
    }else
    {
        throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
    }
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

	Object val = null;
	try
	{
		val = super.getValue( getCopyObject() );
	}
	catch (CancelInsertException cie)
	{
		//we haven't got the copy wiz panel
		//let the code deal with it
		if (!cancelAndResetCopyWizPanel());
		{
			throw cie;
		}
	}
	return val;
}
/**
 * @return 
 * 
 */
private boolean cancelAndResetCopyWizPanel() 
{
	DatabaseEditor editor = getDbEditor();
	if (editor != null)
	{
        
        setCancelled(true);
		fireWizardPanelEvent( new WizardPanelEvent( this, WizardPanelEvent.CANCEL_SELECTION ) );
		DefaultMutableTreeNode node = editor.getDefaultTreeNode();
	
		if( node != null )
		{
			DBPersistent toCopy = LiteFactory.createDBPersistent((LiteBase)node.getUserObject());
			if(toCopy instanceof com.cannontech.database.data.device.DeviceBase && !(toCopy instanceof com.cannontech.database.data.device.lm.LMGroup))
		    {
	
				editor.showCopyWizardPanel(toCopy);
				return true;
		    }
		}
	}
	//we haven't got the copy wiz panel
	return false;
}
/**
 * @return
 */
private DatabaseEditor getDbEditor() {
	DatabaseEditor editor = null;
	for (Iterator iter = getListeners().iterator(); iter.hasNext();) {
		Object listener = (Object) iter.next();
		if (listener instanceof DatabaseEditor) 
		{
				editor = (DatabaseEditor) listener;
		}
	}
	return editor;
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
	

	if (isCapBank)
    {
		return (currentPanel == getCapBankCntrlCreationPanel());
    }else if( (currentPanel == getDeviceCopyNameAddressPanel()) && (getDeviceType() == DeviceTypes.MCT470
                                                                 || getDeviceType() == DeviceTypes.MCT430S4
                                                                 || getDeviceType() == DeviceTypes.MCT430A
                                                                 || getDeviceType() == DeviceTypes.MCT430SN
                                                                 || getDeviceType() == DeviceTypes.MCT410IL
                                                                 || getDeviceType() == DeviceTypes.MCT410CL
                                                                 || getDeviceType() == DeviceTypes.MCT410FL
                                                                 || getDeviceType() == DeviceTypes.MCT410GL
                                                                 || getDeviceType() == DeviceTypes.MCT370
                                                                 || getDeviceType() == DeviceTypes.MCT360
                                                                 || getDeviceType() == DeviceTypes.MCT318L
                                                                 || getDeviceType() == DeviceTypes.MCT318
                                                                 || getDeviceType() == DeviceTypes.MCT310IM
                                                                 || getDeviceType() == DeviceTypes.MCT250
                                                                 || getDeviceType() == DeviceTypes.MCT248
                                                                 || getDeviceType() == DeviceTypes.MCT240
                                                                 || getDeviceType() == DeviceTypes.MCT213
                                                                 || getDeviceType() == DeviceTypes.MCT210
                                                                 || getDeviceType() == DeviceTypes.REPEATER
                                                                 || getDeviceType() == DeviceTypes.REPEATER_902
                                                                 || getDeviceType() == DeviceTypes.REPEATER_800
                                                                 || getDeviceType() == DeviceTypes.REPEATER_801
                                                                 || getDeviceType() == DeviceTypes.REPEATER_921))
    {
        return false;
    }else if((currentPanel == getRoutePanel()) && (getDeviceType() == DeviceTypes.MCT470
                                                                || getDeviceType() == DeviceTypes.MCT430S4
                                                                || getDeviceType() == DeviceTypes.MCT430A
                                                                || getDeviceType() == DeviceTypes.MCT430SN
                                                                || getDeviceType() == DeviceTypes.MCT410IL
                                                                || getDeviceType() == DeviceTypes.MCT410CL
                                                                || getDeviceType() == DeviceTypes.MCT410FL
                                                                || getDeviceType() == DeviceTypes.MCT410GL
                                                                || getDeviceType() == DeviceTypes.MCT370
                                                                || getDeviceType() == DeviceTypes.MCT360
                                                                || getDeviceType() == DeviceTypes.MCT318L
                                                                || getDeviceType() == DeviceTypes.MCT318
                                                                || getDeviceType() == DeviceTypes.MCT310IM
                                                                || getDeviceType() == DeviceTypes.MCT250
                                                                || getDeviceType() == DeviceTypes.MCT248
                                                                || getDeviceType() == DeviceTypes.MCT240
                                                                || getDeviceType() == DeviceTypes.MCT213
                                                                || getDeviceType() == DeviceTypes.MCT210))
    {
        return false;
    }else if( currentPanel == getRoutePanel() )
    {
        return true;
    }else if ((currentPanel == getDeviceCopyNameAddressPanel()) && !((getDeviceType() == PAOGroups.LM_GROUP_EMETCON)
		 || (getDeviceType() == PAOGroups.LM_GROUP_VERSACOM)) )
	{
		return true;
	}else if( currentPanel == getDeviceMeterGroupPanel() )
    {
	    return true;
    }else if( currentPanel == getDeviceRoutePanel() )
    {
        return true;
    }else 
	{ 
		return currentPanel == getDeviceCopyNameAddressPanel();
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
public void setDeviceType()
{

	deviceType = PAOGroups.getDeviceType(((DeviceBase) getCopyObject()).getPAOType() );
    
}

public void setDeviceType( DBPersistent device )
{
    setCopyObject(device);
    setDeviceType();
}

}

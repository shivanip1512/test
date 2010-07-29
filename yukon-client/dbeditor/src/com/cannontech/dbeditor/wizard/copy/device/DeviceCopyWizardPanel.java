package com.cannontech.dbeditor.wizard.copy.device;

/**
 * This type was created in VisualAge.
 */

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.wizard.WizardPanelEvent;
import com.cannontech.common.wizard.WizardPanelListener;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.device.CrfBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupEmetcon;
import com.cannontech.database.data.device.lm.LMGroupVersacom;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.device.CrfAddress;
import com.cannontech.dbeditor.DatabaseEditor;
import com.cannontech.dbeditor.wizard.device.DeviceRoutePanel;
import com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupVersacomEditorPanel;

/**
 * All Panels used in this WizardPanel MUST be able to handle MultiDBPersistent
 * objects in their getValue(Object o) method!!!
 */
public class DeviceCopyWizardPanel extends WizardPanel {

    private DeviceMeterGroupPanel deviceMeterGroupPanel;
    private DeviceCopyNameAddressPanel deviceCopyNameAddressPanel;
	private DeviceCopyPointPanel deviceCopyPointPanel;
	private LMGroupVersacomEditorPanel lmGroupVersacomEditorPanel;
	private RoutePanel routePanel;
	private GoldPanel goldPanel;
	private GoldSilverPanel goldSilverPanel; 
	private EmetconRelayPanel emetconRelayPanel;
	private DeviceRoutePanel deviceRoutePanel = null;
	
	private DBPersistent copyObject = null;
	private int deviceType;
	private Character addressUsage;
	
	private static final Logger log = YukonLogManager.getLogger(DeviceCopyWizardPanel.class);

    public DeviceCopyWizardPanel(DBPersistent objectToCopy) {
    	super();
    
    	setCopyObject( objectToCopy );
    	setDeviceType();
    	if (objectToCopy instanceof LMGroupEmetcon) {
    		setAddressUsage();
    	}
    }
    
    public Dimension getActualSize() {
    	setPreferredSize( new Dimension(410, 480) );
    	return getPreferredSize();
    }
    
    public Character getAddressUsage() {
    	return addressUsage;
    }
    
    public DBPersistent getCopyObject() {
    	return copyObject;
    }
    
    protected DeviceCopyNameAddressPanel getDeviceCopyNameAddressPanel() {
    	if( deviceCopyNameAddressPanel == null ) {
    		deviceCopyNameAddressPanel = new DeviceCopyNameAddressPanel();
    	}
    	return deviceCopyNameAddressPanel;
    }
    
    protected DeviceCopyPointPanel getDeviceCopyPointPanel() {
    	if( deviceCopyPointPanel == null ) {
    		deviceCopyPointPanel = new DeviceCopyPointPanel();
    	}
    	return deviceCopyPointPanel;
    }
    
    protected DeviceMeterGroupPanel getDeviceMeterGroupPanel() {
        if( deviceMeterGroupPanel == null ) {
            deviceMeterGroupPanel = new DeviceMeterGroupPanel();
        }
        return deviceMeterGroupPanel;
    }
    
    protected DeviceRoutePanel getDeviceRoutePanel() {
        if( deviceRoutePanel  == null ) {
            deviceRoutePanel = new DeviceRoutePanel();
        }
        return deviceRoutePanel;
    }
    
    public int getDeviceType() {
        return deviceType;
    }

    public EmetconRelayPanel getEmetconRelayPanel() {
    	if (emetconRelayPanel == null) {
    		emetconRelayPanel = new EmetconRelayPanel();
    	}
    
    	return emetconRelayPanel;
    }
    
    public GoldPanel getGoldPanel() {
    	if (goldPanel == null) {
    		goldPanel = new GoldPanel();
    	}
    
    	return goldPanel;
    }
    
    public GoldSilverPanel getGoldSilverPanel() {
    	if (goldSilverPanel == null) {
    		goldSilverPanel = new GoldSilverPanel();
    	}
    
    	return goldSilverPanel;
    }
    
    protected String getHeaderText() {
    	return "Copy Device";
    }
    
    public LMGroupVersacomEditorPanel getLmGroupVersacomEditorPanel() {
    	if( lmGroupVersacomEditorPanel == null ) {
    		lmGroupVersacomEditorPanel = new LMGroupVersacomEditorPanel();
    	}
    	return lmGroupVersacomEditorPanel;
    }
    
    public Dimension getMinimumSize() {
    	return getPreferredSize();
    }
    
    protected DataInputPanel getNextInputPanel(DataInputPanel currentInputPanel) {
        if (currentInputPanel == null) {
            
    		return getDeviceCopyNameAddressPanel();
    		
    	} else if ( currentInputPanel == getDeviceCopyNameAddressPanel()
    				&& (getDeviceType() == DeviceTypes.REPEATER 
    				        || getDeviceType() == DeviceTypes.REPEATER_902
    				        || getDeviceType() == DeviceTypes.REPEATER_800
    				        || getDeviceType() == DeviceTypes.REPEATER_850
    				        || getDeviceType() == DeviceTypes.REPEATER_801
    				        || getDeviceType() == DeviceTypes.REPEATER_921)) {
            
    	    getDeviceRoutePanel().setValue(null);
            getDeviceRoutePanel().setFirstFocus();
    	    return getDeviceRoutePanel();
    	    
        } else if ( currentInputPanel == getDeviceCopyNameAddressPanel() && getDeviceType() == PAOGroups.LM_GROUP_VERSACOM ) {
            
    		getLmGroupVersacomEditorPanel().setAddresses(((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getUtilityAddress(),
    												((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getSectionAddress(),
    												((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getClassAddress(),
    												((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getDivisionAddress());
    
    		getLmGroupVersacomEditorPanel().setRelay( ((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getRelayUsage() );
    		getLmGroupVersacomEditorPanel().setFirstFocus();
    		return getLmGroupVersacomEditorPanel();
    		
    	}	
    	else if ( currentInputPanel == getDeviceCopyNameAddressPanel() && getDeviceType() == PAOGroups.LM_GROUP_EMETCON ) {
    		
    	    if (getAddressUsage().charValue() == 'S') {
    			getGoldSilverPanel().setGoldSilverSpinnerValues(((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getGoldAddress(), ((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getSilverAddress());
                getGoldSilverPanel().setFirstFocus();
    			return getGoldSilverPanel();
    		} else {
    			getGoldPanel().setGoldSpinnerValue(((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getGoldAddress());
                getGoldPanel().setFirstFocus();
    			return getGoldPanel();
    		}
    		
    	} else if ( currentInputPanel == getDeviceCopyNameAddressPanel()
                && (DeviceTypesFuncs.isMCT470(getDeviceType())
                        || DeviceTypesFuncs.isMCT430(getDeviceType())
                        || DeviceTypesFuncs.isMCT410(getDeviceType())
                        || DeviceTypesFuncs.isMCT3xx(getDeviceType())
                        || DeviceTypesFuncs.isMCT410(getDeviceType())
                        || getDeviceType() == DeviceTypes.MCT250
                        || getDeviceType() == DeviceTypes.MCT248
                        || getDeviceType() == DeviceTypes.MCT240
                        || getDeviceType() == DeviceTypes.MCT213
                        || getDeviceType() == DeviceTypes.MCT210)) {
    	    
            getRoutePanel().setValue(null);
            getRoutePanel().setRoute(((MCTBase)getCopyObject()).getDeviceRoutes().getRouteID());
            getRoutePanel().setFirstFocus();
            return getRoutePanel();
            
        } else if (currentInputPanel == getDeviceCopyNameAddressPanel() && DeviceTypesFuncs.isCrf(deviceType)) {
            
            getDeviceMeterGroupPanel().setValue(getCopyObject());
            getDeviceMeterGroupPanel().setFirstFocus();
            return getDeviceMeterGroupPanel();
            
        } else if( currentInputPanel == getGoldSilverPanel() || currentInputPanel == getGoldPanel() ) {
            
    		getEmetconRelayPanel().setRelay(((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getRelayUsage());
            getEmetconRelayPanel().setFirstFocus();
    		return getEmetconRelayPanel();
    		
    	} else if( currentInputPanel == getLmGroupVersacomEditorPanel() || currentInputPanel == getEmetconRelayPanel() ) {	
    		
    	    getRoutePanel().setValue(null);
    		if (getCopyObject() instanceof LMGroupEmetcon) {
    			getRoutePanel().setRoute(((LMGroupEmetcon)getCopyObject()).getLmGroupEmetcon().getRouteID());
    		} else {
    			getRoutePanel().setRoute(((LMGroupVersacom)getCopyObject()).getLmGroupVersacom().getRouteID());
    		}
    		getRoutePanel().setFirstFocus();
    		return getRoutePanel();
    		
    	} else if( currentInputPanel == getRoutePanel() && (DeviceTypesFuncs.isMCT470(getDeviceType())
                                                        || DeviceTypesFuncs.isMCT430(getDeviceType())
                                                        || DeviceTypesFuncs.isMCT410(getDeviceType())
                                                        || DeviceTypesFuncs.isMCT3xx(getDeviceType())
                                                        || DeviceTypesFuncs.isMCT410(getDeviceType())
                                                        || getDeviceType() == DeviceTypes.MCT250
                                                        || getDeviceType() == DeviceTypes.MCT248
                                                        || getDeviceType() == DeviceTypes.MCT240
                                                        || getDeviceType() == DeviceTypes.MCT213
                                                        || getDeviceType() == DeviceTypes.MCT210)) {
            
            getDeviceMeterGroupPanel().setValue(getCopyObject());
            getDeviceMeterGroupPanel().setFirstFocus();
            return getDeviceMeterGroupPanel();
            
        } else {
            throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
        }
    }
    
    public RoutePanel getRoutePanel() {
    	if (routePanel == null) {
    		routePanel = new RoutePanel();
    	}
    
    	return routePanel;
    }

    public Object getValue(Object o) {
    
    	Object val = null;
    	try {
    		val = super.getValue( getCopyObject() );
    	} catch (CancelInsertException cie) {
    		//we haven't got the copy wiz panel
    		//let the code deal with it
    		if (!cancelAndResetCopyWizPanel()); {
    			throw cie;
    		}
    	}
    	return val;
    }
    
    private boolean cancelAndResetCopyWizPanel() {
    	DatabaseEditor editor = getDbEditor();
    	if (editor != null) {
    		fireWizardPanelEvent( new WizardPanelEvent( this, WizardPanelEvent.CANCEL_SELECTION ) );
    		DefaultMutableTreeNode node = editor.getDefaultTreeNode();
    	
    		if( node != null ) {
    			DBPersistent toCopy = LiteFactory.createDBPersistent((LiteBase)node.getUserObject());
    			if(toCopy instanceof DeviceBase && !(toCopy instanceof LMGroup)) {
    				editor.showCopyWizardPanel(toCopy);
    				return true;
    		    }
    		}
    	}
    	//we haven't got the copy wiz panel
    	return false;
    }
    
    private DatabaseEditor getDbEditor() {
    	DatabaseEditor editor = null;
    	for (Iterator<WizardPanelListener> iter = getListeners().iterator(); iter.hasNext();) {
    	    WizardPanelListener listener = iter.next();
    		if (listener instanceof DatabaseEditor) {
    		    editor = (DatabaseEditor) listener;
    		}
    	}
    	return editor;
    }
    
    protected boolean isLastInputPanel(DataInputPanel currentPanel) {
        if ((currentPanel == getDeviceCopyNameAddressPanel()) && ( DeviceTypesFuncs.isMCT470(getDeviceType())
                                                                     || DeviceTypesFuncs.isMCT430(getDeviceType())
                                                                     || DeviceTypesFuncs.isMCT410(getDeviceType())
                                                                     || DeviceTypesFuncs.isMCT3xx(getDeviceType())
                                                                     || DeviceTypesFuncs.isMCT410(getDeviceType())
                                                                     || DeviceTypesFuncs.isCrf(getDeviceType())
                                                                     || getDeviceType() == DeviceTypes.MCT250
                                                                     || getDeviceType() == DeviceTypes.MCT248
                                                                     || getDeviceType() == DeviceTypes.MCT240
                                                                     || getDeviceType() == DeviceTypes.MCT213
                                                                     || getDeviceType() == DeviceTypes.MCT210
                                                                     || getDeviceType() == DeviceTypes.REPEATER
                                                                     || getDeviceType() == DeviceTypes.REPEATER_902
                                                                     || getDeviceType() == DeviceTypes.REPEATER_800
                                                                     || getDeviceType() == DeviceTypes.REPEATER_850
                                                                     || getDeviceType() == DeviceTypes.REPEATER_801
                                                                     || getDeviceType() == DeviceTypes.REPEATER_921)) {
            return false;
        } else if ((currentPanel == getRoutePanel()) && (DeviceTypesFuncs.isMCT470(getDeviceType())
                                                                    || DeviceTypesFuncs.isMCT430(getDeviceType())
                                                                    || DeviceTypesFuncs.isMCT410(getDeviceType())
                                                                    || DeviceTypesFuncs.isMCT3xx(getDeviceType())
                                                                    || DeviceTypesFuncs.isMCT410(getDeviceType())
                                                                    || getDeviceType() == DeviceTypes.MCT250
                                                                    || getDeviceType() == DeviceTypes.MCT248
                                                                    || getDeviceType() == DeviceTypes.MCT240
                                                                    || getDeviceType() == DeviceTypes.MCT213
                                                                    || getDeviceType() == DeviceTypes.MCT210)) {
            return false;
        } else if(currentPanel == getRoutePanel()) {
            return true;
        } else if ((currentPanel == getDeviceCopyNameAddressPanel()) 
                && !((getDeviceType() == PAOGroups.LM_GROUP_EMETCON) || (getDeviceType() == PAOGroups.LM_GROUP_VERSACOM)) ) {
    		return true;
    	} else if(currentPanel == getDeviceMeterGroupPanel()){
    	    return true;
        } else if(currentPanel == getDeviceRoutePanel()) {
            return true;
        } else { 
    		return currentPanel == getDeviceCopyNameAddressPanel();
    	}
    }
    
    public void setAddressUsage(){
    	addressUsage = ((LMGroupEmetcon) getCopyObject()).getLmGroupEmetcon().getAddressUsage();
    }
    
    public void setCopyObject(DBPersistent newObject) {
    	try { 
    		copyObject = newObject;
    	 	
    		Transaction<DBPersistent> t = Transaction.createTransaction(Transaction.RETRIEVE, copyObject);
    		copyObject = t.execute();
    		
    		adjustCopyVersion();
    	} catch (TransactionException e) {
    		log.error( e.getMessage(), e );
    	}
    }
    
    /**
     * Adjust the copy of this DBPersistent, changing things that should not get duplicated.
     * Currently used for resetting the Serial Number, Manufacturer, and Model of CrfBase meters
     * since these fields form a unique constraint.
     */
    private void adjustCopyVersion() {
        if( copyObject instanceof CrfBase) {
            CrfBase crfBase = (CrfBase) copyObject;
            CrfAddress address = new CrfAddress();
            address.setDeviceID(crfBase.getPAObjectID());
            crfBase.setCrfAddress(address);
        }
    }
    
    public void setDeviceType() {
    	deviceType = PAOGroups.getDeviceType(((DeviceBase) getCopyObject()).getPAOType() );
    }
    
    public void setDeviceType(DBPersistent device) {
        setCopyObject(device);
        setDeviceType();
    }

}
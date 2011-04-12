package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.SwingUtilities;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.AddRemovePanel;
import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.MacroGroupAddRemovePanel;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class GroupMacroLoadGroupsPanel extends DataInputPanel implements AddRemovePanelListener {
	private int rightListItemIndex = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private MacroGroupAddRemovePanel ivjLoadGroupsAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GroupMacroLoadGroupsPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (AddRemovePanel1.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AddRemovePanel1.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.routesAddRemovePanel_RightListMouse_mousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.routesAddRemovePanel_RightListMouse_mouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (RoutesAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RouteMacroCommunicationRoutesPanel.routesAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.routesAddRemovePanel_RightListMouse_mouseExited(arg1);
		// user code begin {2}
		// user code end
	} catch (Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the AddRemovePanel1 property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private MacroGroupAddRemovePanel getLoadGroupsAddRemovePanel() {
	if (ivjLoadGroupsAddRemovePanel == null) {
		try {
			ivjLoadGroupsAddRemovePanel = new MacroGroupAddRemovePanel();
			ivjLoadGroupsAddRemovePanel.setName("LoadGroupsAddRemovePanel");
			// user code begin {1}

			ivjLoadGroupsAddRemovePanel.setMode(AddRemovePanel.TRANSFER_MODE );
			ivjLoadGroupsAddRemovePanel.leftListRemoveAll();
			ivjLoadGroupsAddRemovePanel.rightListRemoveAll();


			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			Vector<LiteYukonPAObject> availableDevices = null;
			synchronized(cache)
			{
				List<LiteYukonPAObject> allDevices = cache.getAllLoadManagement();
				Collections.sort( allDevices, LiteComparators.liteStringComparator );
				
				availableDevices = new Vector<LiteYukonPAObject>();
				for (LiteYukonPAObject liteYukonPAObject : allDevices) {
					if( DeviceTypesFuncs.isLmGroup(liteYukonPAObject.getPaoType().getDeviceTypeId())
						 && liteYukonPAObject.getPaoType() != PaoType.MACRO_GROUP ) {
						availableDevices.add(liteYukonPAObject);
					}
				}
			}

			getLoadGroupsAddRemovePanel().leftListSetListData(availableDevices);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadGroupsAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	
	YukonPAObject macro = null;
	
	if( val instanceof MultiDBPersistent )
	{
		macro = (YukonPAObject)
				MultiDBPersistent.getFirstObjectOfType(
		YukonPAObject.class,
				(MultiDBPersistent)val );
	}
	else if( val instanceof SmartMultiDBPersistent )
		macro = (YukonPAObject)
				((SmartMultiDBPersistent)val).getOwnerDBPersistent();
	
	
	if( val instanceof YukonPAObject || macro != null )
	{
		if( macro == null )
			macro = (YukonPAObject) val;
		
		Integer ownerID = macro.getPAObjectID();
	
		Vector<GenericMacro> macroGroupVector = new Vector<GenericMacro>();

		for( int i = 0; i < getLoadGroupsAddRemovePanel().rightListGetModel().getSize(); i++ )
		{
			GenericMacro mGroup = new GenericMacro();
			mGroup.setOwnerID(ownerID);
			mGroup.setChildID( new Integer(((LiteYukonPAObject)
									getLoadGroupsAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()) );
			mGroup.setChildOrder(new Integer(i+1) );
			mGroup.setMacroType(MacroTypes.GROUP);
			macroGroupVector.addElement( mGroup );	
		}

		((MacroGroup) macro).setMacroGroupVector( macroGroupVector );
	}
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getLoadGroupsAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GroupMacroLoadGroupsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 228);

		java.awt.GridBagConstraints constraintsLoadGroupsAddRemovePanel = 
		    new java.awt.GridBagConstraints();
		constraintsLoadGroupsAddRemovePanel.gridx = 0; 
		constraintsLoadGroupsAddRemovePanel.gridy = 0;
		constraintsLoadGroupsAddRemovePanel.gridwidth = 2;
		constraintsLoadGroupsAddRemovePanel.gridheight = 2;
		constraintsLoadGroupsAddRemovePanel.fill = GridBagConstraints.BOTH;
		add(getLoadGroupsAddRemovePanel(), constraintsLoadGroupsAddRemovePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getLoadGroupsAddRemovePanel().rightListGetModel().getSize() < 1 )
	{
		setErrorString("There needs to be at least 1 load group in this group macro");
		return false;
	}
	else
		return true;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}

/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseClicked(EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseEntered(EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseExited(EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC5(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mousePressed(EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC3(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouse_mouseReleased(EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListMouseMotion_mouseDragged(EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void routesAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * Comment
 */
public void routesAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
	rightListItemIndex = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void routesAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
	int indexSelected = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		LiteYukonPAObject itemSelected = null;
		Vector<LiteYukonPAObject> destItems = 
		    new Vector<LiteYukonPAObject>(getLoadGroupsAddRemovePanel().rightListGetModel().getSize() + 1);

		for( int i = 0; i < getLoadGroupsAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement((LiteYukonPAObject) getLoadGroupsAddRemovePanel().rightListGetModel().getElementAt(i));

		itemSelected = destItems.elementAt(rightListItemIndex);
		destItems.removeElementAt(rightListItemIndex);
		destItems.insertElementAt(itemSelected, indexSelected);
		getLoadGroupsAddRemovePanel().rightListSetListData(destItems);

		getLoadGroupsAddRemovePanel().revalidate();
		getLoadGroupsAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	IDatabaseCache cache = DefaultDatabaseCache.getInstance();
	Vector<LiteYukonPAObject> availableGroups = null;
	Vector<LiteYukonPAObject> assignedGroups = null;

	
	MacroGroupAddRemovePanel macroGroupAddRemovePanel = getLoadGroupsAddRemovePanel();
	MacroGroup macroGroup = 
	    (MacroGroup)val;
	DisplayablePaoBase displayableMacroGroup = 
	    new DisplayablePaoBase(new PaoIdentifier(macroGroup.getPAObjectID(), PaoType.MACRO_GROUP), 
	                           macroGroup.getPAOName());
	macroGroupAddRemovePanel.setCurrentMacroGroup(displayableMacroGroup);
	
	synchronized(cache)
	{
		Vector<GenericMacro> macroGroupsVector = ((MacroGroup)val).getMacroGroupVector();
		List<LiteYukonPAObject> allDevices = cache.getAllLoadManagement();
		Collections.sort( allDevices, LiteComparators.liteStringComparator );

		assignedGroups = new Vector<LiteYukonPAObject>();
		int childID;
		for(int i=0;i<macroGroupsVector.size();i++)
		{
			childID = ((GenericMacro)macroGroupsVector.get(i)).getChildID().intValue();
			for(int j=0;j<allDevices.size();j++)
			{
			    if(allDevices.get(j).getYukonID() == childID 
			            && allDevices.get(j).getLiteID() != 
				((MacroGroup)val).getPAObjectID().intValue())
				{
					assignedGroups.addElement(allDevices.get(j));
					break;
				}
			}
		}

		availableGroups = new Vector<LiteYukonPAObject>();
		for (LiteYukonPAObject liteYukonPAObject : allDevices) {
		    if( DeviceTypesFuncs.isLmGroup(liteYukonPAObject.getPaoType().getDeviceTypeId()) 
		        && liteYukonPAObject.getLiteID() != 
		            ((MacroGroup)val).getPAObjectID().intValue())
		    {
				availableGroups.addElement(liteYukonPAObject);
			}
		}		
	}

	macroGroupAddRemovePanel.leftListSetListData(availableGroups);
	macroGroupAddRemovePanel.rightListSetListData(assignedGroups);
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getLoadGroupsAddRemovePanel().requestFocus(); 
        } 
    });    
}

}

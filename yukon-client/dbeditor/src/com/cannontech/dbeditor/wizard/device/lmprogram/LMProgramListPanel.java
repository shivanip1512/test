package com.cannontech.dbeditor.wizard.device.lmprogram;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */

public class LMProgramListPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramListPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) throws IllegalArgumentException{
	Object[] lmProgPAOs = getAddRemovePanel().getLeftList().getSelectedValues();
	for (Object temp: lmProgPAOs) {
	    LiteYukonPAObject lmProgPAO = (LiteYukonPAObject)temp;
	    int loadGroupId = lmProgPAO.getLiteID();
	    LoadGroupDao loadGroupDao = YukonSpringHook.getBean("loadGroupDao", LoadGroupDao.class);
	    boolean loadGroupInUse = loadGroupDao.isLoadGroupInUse(loadGroupId);
	    if(!loadGroupInUse){
	        this.fireInputUpdate();
	    } else {
	        throw new IllegalArgumentException("The load group you are trying to add is currently being used in customer enrollment.  Please unenroll all accounts before removing a load group from its program. ("+lmProgPAO.getPaoName()+")");
	    }
	}
}
/**
 * connEtoC2:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 * @throws IllegalArgumentException 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) throws IllegalArgumentException {
	Object[] lmProgPAOs = getAddRemovePanel().getLeftList().getSelectedValues();
	for (Object temp: lmProgPAOs) {
	    LiteYukonPAObject lmProgPAO = (LiteYukonPAObject)temp;    int loadGroupId = lmProgPAO.getLiteID();
	    LoadGroupDao loadGroupDao = YukonSpringHook.getBean("loadGroupDao", LoadGroupDao.class);
	    boolean loadGroupInUse = loadGroupDao.isLoadGroupInUse(loadGroupId);
	    if(!loadGroupInUse){
	        this.fireInputUpdate();
	    } else {
	        throw new IllegalArgumentException("The load group you are trying to remove is currently being used in customer enrollment.  Please unenroll all accounts before removing a load group from its program. ("+lmProgPAO.getPaoName()+")");
	    }
	}
}
/**
 * Return the AddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getAddRemovePanel() {
	if (ivjAddRemovePanel == null) {
		try {
			ivjAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjAddRemovePanel.setName("AddRemovePanel");
			// user code begin {1}

			ivjAddRemovePanel.setMode( com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE );
			ivjAddRemovePanel.leftListLabelSetText("Available Load Groups");
			ivjAddRemovePanel.rightListLabelSetText("Assigned Load Groups");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddRemovePanel;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramBase program = (com.cannontech.database.data.device.lm.LMProgramBase)o;
	program.getLmProgramStorageVector().removeAllElements();
	
	for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.db.device.lm.LMProgramDirectGroup group = new com.cannontech.database.db.device.lm.LMProgramDirectGroup();

		group.setDeviceID( program.getPAObjectID() );
		group.setGroupOrder( new Integer(i+1) );
		group.setLmGroupDeviceID( new Integer(
					((com.cannontech.database.data.lite.LiteYukonPAObject)getAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID() ) );
		
		program.getLmProgramStorageVector().addElement( group );
	}
	
	return o;
	
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2002 3:15:27 PM)
 * @return boolean
 */
public boolean hasLMGroupPoint() 
{	
	for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.data.lite.LiteYukonPAObject lmGroup = 
					(com.cannontech.database.data.lite.LiteYukonPAObject)getAddRemovePanel().rightListGetModel().getElementAt(i);

		//if our element is a LM_GROUP_POINT object, do not add it to the newList
		if( lmGroup.getType() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_POINT )
			return true;
	}

	return false;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("VersacomRelayPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);

		java.awt.GridBagConstraints constraintsAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsAddRemovePanel.gridx = 1; constraintsAddRemovePanel.gridy = 1;
		constraintsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddRemovePanel.weightx = 1.0;
		constraintsAddRemovePanel.weighty = 1.0;
		constraintsAddRemovePanel.ipadx = 159;
		constraintsAddRemovePanel.ipady = 174;
		constraintsAddRemovePanel.insets = new java.awt.Insets(25, 2, 38, 4);
		add(getAddRemovePanel(), constraintsAddRemovePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2002 2:17:21 PM)
 */
public void initLeftList( boolean hideLMGroupPoints)
{
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List groups = cache.getAllLoadManagement();
		java.util.Collections.sort( groups, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		java.util.Vector newList = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );
		
		for( int i = 0; i < groups.size(); i++ )
		{ 
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isLmGroup( ((com.cannontech.database.data.lite.LiteYukonPAObject)groups.get(i)).getType() )
				 &&
				 ( hideLMGroupPoints ? 
					 ((com.cannontech.database.data.lite.LiteYukonPAObject)groups.get(i)).getType() != com.cannontech.database.data.pao.PAOGroups.LM_GROUP_POINT 
					 : true) )
			{
				newList.addElement( groups.get(i) );
			}

		}

		getAddRemovePanel().leftListSetListData( newList );
	}
	
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getAddRemovePanel().rightListGetModel().getSize() <= 0 )
	{
		setErrorString("At least 1 load group must present in this current program.");
		return false;
	}

	
	return true;
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 * @throws IllegalArgumentException 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) throws IllegalArgumentException {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
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
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
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
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
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
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
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
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
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
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
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
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramDirect dirProg= (com.cannontech.database.data.device.lm.LMProgramDirect)o;


	/**** special case for the LM_GROUP_POINT group type ****/
	boolean isLatching = false;
	for( int i = 0; i < dirProg.getLmProgramDirectGearVector().size(); i++ )
	{
		com.cannontech.database.db.device.lm.LMProgramDirectGear gear = 
			(com.cannontech.database.db.device.lm.LMProgramDirectGear)dirProg.getLmProgramDirectGearVector().get(i);

		//we only can add LM_GROUP_POINTS group types to a program with a LATCHING gear
		if( gear.getControlMethod() == GearControlMethod.Latching )
		{
			isLatching = true;
			break;
		}
	}

	initLeftList( !isLatching );
	/**** END of special case for the LM_GROUP_POINT group type ****/


	
	//init storage that will contain all possible items
	java.util.Vector allItems = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );
	for( int i = 0; i < getAddRemovePanel().leftListGetModel().getSize(); i++ )
		allItems.add( getAddRemovePanel().leftListGetModel().getElementAt(i) );

	java.util.Vector usedItems = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );

	for( int i = 0; i < dirProg.getLmProgramStorageVector().size(); i++ )
	{
		com.cannontech.database.db.device.lm.LMProgramDirectGroup group = (com.cannontech.database.db.device.lm.LMProgramDirectGroup)dirProg.getLmProgramStorageVector().get(i);
		
		for( int j = 0; j < allItems.size(); j++ )
		{
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)allItems.get(j)).getYukonID() ==
				 group.getLmGroupDeviceID().intValue() )
			{
				usedItems.add( allItems.get(j) );
				allItems.removeElementAt(j);				
				break;
			}
			
		}		
	}

	getAddRemovePanel().leftListSetListData( allItems );
	getAddRemovePanel().rightListSetListData( usedItems );		
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getAddRemovePanel().requestFocus(); 
        } 
    });    
}
}

package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.ListModel;

import com.cannontech.common.gui.util.AddRemovePanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */

public class LMProgramListPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private AddRemovePanel ivjAddRemovePanel = null;
	// Temp lists that hold the previous state of the load groups for a given program
	private List<Object> currentAvailableList;
	private List<Object> currentSelectedList;
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
		addSelectedLoadGroup(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * addSelectedLoadGroup:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void addSelectedLoadGroup(java.util.EventObject arg1) {
	try {
		checkForEnrollmentConflicts();
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * removeSelectedLoadGroup:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void removeSelectedLoadGroup(java.util.EventObject arg1) {
	try {
		checkForEnrollmentConflicts();
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
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
 * Takes in a listModel and returns an arrayList
 * 
 * @param listModel
 * @return
 */
private List<Object> getArrayListFromListModel(ListModel listModel){
	List<Object> list = new ArrayList<Object>();
	for (int i = 0; i < listModel.getSize(); i++) {
		list.add(listModel.getElementAt(i));
	}
	
	return list;
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
 * @param isSepProgram 
 */
public void initLeftList( boolean hideLMGroupPoints, PaoType programType)
{
	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		List<LiteYukonPAObject> groups = cache.getAllLoadManagement();
		Collections.sort( groups, LiteComparators.liteStringComparator );
		Vector<LiteYukonPAObject> newList = new Vector<LiteYukonPAObject>( getAddRemovePanel().leftListGetModel().getSize() );
		
		for (LiteYukonPAObject group : groups) {
			PaoType paoType = PaoType.getForId(group.getType());
			if( DeviceTypesFuncs.isLmGroup( group.getType() )
				 &&
				 ( hideLMGroupPoints ? paoType != PaoType.LM_GROUP_POINT : true) )
			{
				boolean isSepProgram =  programType == PaoType.LM_SEP_PROGRAM;
			    // SEP compatible groups are shown for SEP programs and hidden for all others
			    if ( (isSepProgram && isGroupSepCompatible(paoType)) ||
			    	 (!isSepProgram && !isGroupSepCompatible(paoType))) {
			        newList.addElement(group);
			    }
			}
		}

		getAddRemovePanel().leftListSetListData( newList );
		currentAvailableList = new ArrayList<Object>(newList);
		currentSelectedList = new ArrayList<Object>();
		
	}
}

private boolean isGroupSepCompatible(PaoType groupType)
{
    return groupType == PaoType.LM_GROUP_DIGI_SEP;
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

	if(!checkForEnrollmentConflicts()){
		return false;
	}
	
	return true;
}

/**
 * Checks to see if the added/removed load groups  
 *
 * @return
 */
private boolean checkForEnrollmentConflicts(){
	// Check available load group list
	List<Object> availableListDiff = getArrayListFromListModel(getAddRemovePanel().getLeftList().getModel());
	availableListDiff.removeAll(currentAvailableList);

	if(!checkLoadGroupListForEnrollmentIssues(availableListDiff)) {
		getAddRemovePanel().getLeftList().setListData(currentAvailableList.toArray());
		getAddRemovePanel().getRightList().setListData(currentSelectedList.toArray());
		return false;
	}
	
	// Check selected load group list
	List<Object> selectionListDiff = getArrayListFromListModel(getAddRemovePanel().getRightList().getModel());
	selectionListDiff.removeAll(currentSelectedList);
	
	if(!checkLoadGroupListForEnrollmentIssues(selectionListDiff)) {
		getAddRemovePanel().getLeftList().setListData(currentAvailableList.toArray());
		getAddRemovePanel().getRightList().setListData(currentSelectedList.toArray());
		return false;
	}

	// Update the temp lists
	currentAvailableList = getArrayListFromListModel(getAddRemovePanel().getLeftList().getModel());
	currentSelectedList = getArrayListFromListModel(getAddRemovePanel().getRightList().getModel());
	
	return true;
}

/**
 * 
 * 
 * @param lmLoadGroupPAOs
 * @return
 */
private boolean checkLoadGroupListForEnrollmentIssues(List<Object> lmLoadGroupPAOs){
	for (Object temp: lmLoadGroupPAOs) {
	    LiteYukonPAObject lmProgPAO = (LiteYukonPAObject)temp;
	    int loadGroupId = lmProgPAO.getLiteID();
	    LoadGroupDao loadGroupDao = YukonSpringHook.getBean("loadGroupDao", LoadGroupDao.class);
	    if(loadGroupDao.isLoadGroupInUse(loadGroupId)) {
	        setErrorString("The load group you are trying to move is currently being used in customer enrollment. ("+lmProgPAO.getPaoName()+")");
	        JOptionPane.showMessageDialog(null, getErrorString(), "Illegal Operation Exception", JOptionPane.ERROR_MESSAGE);
            return false;
	    }
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
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getAddRemovePanel()) 
		removeSelectedLoadGroup(newEvent);
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
	LMProgramDirect dirProg= (LMProgramDirect)o;
	PaoType paoType = PaoType.getForDbString(dirProg.getPAOType()); 

	/**** special case for the LM_GROUP_POINT group type ****/
	boolean isLatching = false;
	for( int i = 0; i < dirProg.getLmProgramDirectGearVector().size(); i++ )
	{
		LMProgramDirectGear gear = dirProg.getLmProgramDirectGearVector().get(i);

		//we only can add LM_GROUP_POINTS group types to a program with a LATCHING gear
		if( gear.getControlMethod() == GearControlMethod.Latching )
		{
			isLatching = true;
			break;
		}
	}

	initLeftList( !isLatching, paoType);
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
	currentAvailableList = new ArrayList<Object>(allItems);
	
	getAddRemovePanel().rightListSetListData( usedItems );
	currentSelectedList = new ArrayList<Object>(usedItems);

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

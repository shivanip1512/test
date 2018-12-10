package com.cannontech.dbeditor.wizard.device.lmprogram;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.db.device.lm.LMProgramDirect;
import com.cannontech.database.db.point.PointUnit;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */

public class LMProgramDirectNotifGroupListPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener {
	private com.cannontech.common.gui.util.AddRemovePanel ivjAddRemovePanel = null;
	private javax.swing.JLabel jLabelMinNotifyTime = null;
	private javax.swing.JLabel jLabelNotifyInactiveOffset = null;
	private javax.swing.JLabel jLabelNotifyAdjust = null;
	private javax.swing.JLabel jLabelMinutes = null;
	private javax.swing.JTextField jTextFieldNotifyActiveOffset = null;
	private javax.swing.JTextField jTextFieldNotifyInactiveOffset = null;
	private javax.swing.JCheckBox jCheckBoxEnableStart = null;
	private javax.swing.JCheckBox jCheckBoxEnableStop = null;
	private javax.swing.JCheckBox jCheckBoxNotifyAdjust = null;
    private javax.swing.JCheckBox jCheckBoxEnableSchedule = null;
    private javax.swing.JLabel jLabelNotifySchedule = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	
	class IvjEventHandler implements javax.swing.event.CaretListener, java.awt.event.ActionListener 
	{
			@Override
            public void caretUpdate(javax.swing.event.CaretEvent e) 
			{
				if (e.getSource() == getJTextFieldNotifyActiveOffset())
					fireInputUpdate();
				if (e.getSource() == getJTextFieldNotifyInactiveOffset())
					fireInputUpdate();
			};
			
			@Override
            public void actionPerformed(java.awt.event.ActionEvent e) 
			{ 
				if (e.getSource() == getJCheckBoxEnableStart()) 
					handleOffsetEnabling(getJCheckBoxEnableStart().isSelected(), getJCheckBoxEnableStop().isSelected());
				if (e.getSource() == getJCheckBoxEnableStop()) 
					handleOffsetEnabling(getJCheckBoxEnableStart().isSelected(), getJCheckBoxEnableStop().isSelected());
                if (e.getSource() == getJCheckBoxNotifyAdjust()) {
                    getJLabelNotifyAdjust().setEnabled(getJCheckBoxNotifyAdjust().isSelected());
                    fireInputUpdate();
			    }
                if (e.getSource() == getJCheckBoxEnableSchedule()) {
                    getJLabelNotifySchedule().setEnabled(getJCheckBoxEnableSchedule().isSelected());
                    fireInputUpdate();
                }
			};
	}
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramDirectNotifGroupListPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
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
private void connEtoC1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> LMProgramListPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
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

			ivjAddRemovePanel.setPreferredSize(new java.awt.Dimension(410,285));
			ivjAddRemovePanel.setMinimumSize(new java.awt.Dimension(410,285));
			ivjAddRemovePanel.leftListLabelSetText("Notification Groups");
			ivjAddRemovePanel.rightListLabelSetText("Assigned Groups");
			
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
@Override
@SuppressWarnings("unchecked")
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramDirectBase program = (com.cannontech.database.data.device.lm.LMProgramDirectBase)o;
	program.getLmProgramDirectNotifyGroupVector().removeAllElements();
	
	for( int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		com.cannontech.database.db.device.lm.LMDirectNotificationGroupList group = new com.cannontech.database.db.device.lm.LMDirectNotificationGroupList();

		group.setDeviceID( program.getPAObjectID() );
		group.setNotificationGrpID( new Integer(
					((LiteNotificationGroup)getAddRemovePanel().rightListGetModel().getElementAt(i)).getNotificationGroupID() ) );
		
		program.getLmProgramDirectNotifyGroupVector().addElement( group );
		
	}
	
	String programStart = getJTextFieldNotifyActiveOffset().getText();
	String programStop = getJTextFieldNotifyInactiveOffset().getText();
	
	if(getJCheckBoxEnableStart().isSelected())
	{
		if(programStart.length() > 0)
		{
			Integer numStart = new Integer(programStart);
			program.getDirectProgram().setNotifyActiveOffset( new Integer( numStart.intValue() * 60 ) );
		}
		else
		{
			program.getDirectProgram().setNotifyActiveOffset( new Integer(0));
		}
	}		
	else
		program.getDirectProgram().setNotifyActiveOffset(new Integer(-1));

	if(getJCheckBoxEnableStop().isSelected())
	{
		if(programStop.length() > 0)
		{
			Integer numStop = new Integer(programStop);
			program.getDirectProgram().setNotifyInactiveOffset( new Integer( numStop.intValue() * 60 ) );
		}
		else
			program.getDirectProgram().setNotifyInactiveOffset(new Integer(0));
	}
	else
		program.getDirectProgram().setNotifyInactiveOffset(new Integer(-1));

	if (getJCheckBoxNotifyAdjust().isSelected()) {                                 
        program.getDirectProgram().setNotifyAdjust(LMProgramDirect.NOTIFY_ADJUST_ENABLED);           // The values of these class defined variables have been  
	} else {                                                                         // chosen so that the data written to the database is  
        program.getDirectProgram().setNotifyAdjust(LMProgramDirect.NOTIFY_ADJUST_DISABLED);          // consistent with the format of the other Notify columns.  
	}

    if (getJCheckBoxEnableSchedule().isSelected()) {                                 
        program.getDirectProgram().setEnableSchedule(LMProgramDirect.NOTIFY_SCHEDULE_ENABLED);           // The values of these class defined variables have been  
    } else {                                                                         // chosen so that the data written to the database is  
        program.getDirectProgram().setEnableSchedule(LMProgramDirect.NOTIFY_SCHEDULE_DISABLED);          // consistent with the format of the other Notify columns.  
    }

    // Create Status point
    if(program.getPAObjectID() == null){
        PaoDao paoDao = (PaoDao) YukonSpringHook.getBean("paoDao");
        program.setPAObjectID(paoDao.getNextPaoId());

        PointCreationService pointCreationService = (PointCreationService) YukonSpringHook.getBean("pointCreationService");
        PointBase point = pointCreationService.createPoint(PointType.Status.getPointTypeId(),
                                                   "Status",
                                                   new PaoIdentifier(program.getPAObjectID(), PaoType.LM_DIRECT_PROGRAM),
                                                   1,
                                                   0.0,
                                                   0,
                                                   StateGroupUtils.STATEGROUP_TWO_STATE_ACTIVE,
                                                   StateGroupUtils.DEFAULT_STATE,
                                                   PointUnit.DEFAULT_DECIMAL_PLACES,
                                                   StatusControlType.NONE,
                                                   PointArchiveType.NONE,
                                                   PointArchiveInterval.ZERO);

        SmartMultiDBPersistent persistant = new SmartMultiDBPersistent();
        persistant.addOwnerDBPersistent(program);
        persistant.addDBPersistent(point);
        
        return persistant;
    }
    
    return program;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
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
	
	getJTextFieldNotifyActiveOffset().addCaretListener(ivjEventHandler);
	getJTextFieldNotifyInactiveOffset().addCaretListener(ivjEventHandler);
	getJCheckBoxEnableStart().addActionListener(ivjEventHandler);
	getJCheckBoxEnableStop().addActionListener(ivjEventHandler);
    getJCheckBoxNotifyAdjust().addActionListener(ivjEventHandler);
    getJCheckBoxEnableSchedule().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramDirectCustomerListPanel");
		java.awt.GridBagConstraints consGridBagConstraints19 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints20 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints21 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints22 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints23 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints24 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints25 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints26 = new java.awt.GridBagConstraints();
		java.awt.GridBagConstraints consGridBagConstraints27 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints28 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints29 = new java.awt.GridBagConstraints();
        java.awt.GridBagConstraints consGridBagConstraints30 = new java.awt.GridBagConstraints();
		
		consGridBagConstraints19.insets = new java.awt.Insets(20,2,50,4);
        consGridBagConstraints19.ipadx = 12;
        consGridBagConstraints19.gridwidth = 4;
        consGridBagConstraints19.gridy = 4;
        consGridBagConstraints19.gridx = 0;
        consGridBagConstraints19.weightx = 1.0;
        consGridBagConstraints19.weighty = 1.0;
        consGridBagConstraints19.fill = java.awt.GridBagConstraints.BOTH;
		
        consGridBagConstraints20.insets = new java.awt.Insets(20,105,1,3);
        consGridBagConstraints20.gridy = 0;
        consGridBagConstraints20.gridx = 0;
        
        consGridBagConstraints21.insets = new java.awt.Insets(2,105,1,3);
		consGridBagConstraints21.gridy = 1;
		consGridBagConstraints21.gridx = 0;
		
		consGridBagConstraints22.insets = new java.awt.Insets(20,5,3,4);
        consGridBagConstraints22.gridy = 0;
        consGridBagConstraints22.gridx = 1;
        
        consGridBagConstraints23.insets = new java.awt.Insets(20,4,2,4);
        consGridBagConstraints23.fill = java.awt.GridBagConstraints.HORIZONTAL;
        consGridBagConstraints23.gridy = 0;
        consGridBagConstraints23.gridx = 2;
        consGridBagConstraints23.weightx = 0.25;
        
		consGridBagConstraints24.insets = new java.awt.Insets(3,5,3,4);
		consGridBagConstraints24.gridy = 1;
		consGridBagConstraints24.gridx = 1;

		consGridBagConstraints25.insets = new java.awt.Insets(2,4,2,4);
		consGridBagConstraints25.fill = java.awt.GridBagConstraints.HORIZONTAL;
		consGridBagConstraints25.gridy = 1;
		consGridBagConstraints25.gridx = 2;
		consGridBagConstraints25.weightx = 0.25;
				
		consGridBagConstraints26.insets = new java.awt.Insets(30,5,17,30);
		consGridBagConstraints26.gridheight = 2;
		consGridBagConstraints26.gridy = 0;
		consGridBagConstraints26.gridx = 3;
		consGridBagConstraints26.anchor = java.awt.GridBagConstraints.WEST;
		consGridBagConstraints26.weightx = 0.75;
		
		consGridBagConstraints27.insets = new java.awt.Insets(2,105,0,0);
		consGridBagConstraints27.gridy = 2;
		consGridBagConstraints27.gridx = 0;
				
		consGridBagConstraints28.insets = new java.awt.Insets(2,5,0,0);
        consGridBagConstraints28.gridy = 2;
        consGridBagConstraints28.gridx = 1;
        consGridBagConstraints28.gridwidth = 2;
        consGridBagConstraints28.anchor = java.awt.GridBagConstraints.WEST;
		
        consGridBagConstraints29.insets = new java.awt.Insets(2,105,1,3);
        consGridBagConstraints29.gridy = 3;
        consGridBagConstraints29.gridx = 0;
        
        consGridBagConstraints30.insets = new java.awt.Insets(2,5,0,0);
        consGridBagConstraints30.gridy = 3;
        consGridBagConstraints30.gridx = 1;
        consGridBagConstraints30.gridwidth = 2;
        consGridBagConstraints30.anchor = java.awt.GridBagConstraints.WEST;
        
		setLayout(new java.awt.GridBagLayout());
		this.add(getAddRemovePanel(), consGridBagConstraints19);
		this.add(getJCheckBoxEnableStart(), consGridBagConstraints20);
		this.add(getJCheckBoxEnableStop(), consGridBagConstraints21);
		this.add(getJLabelMinNotifyTime(), consGridBagConstraints22);
		this.add(getJTextFieldNotifyActiveOffset(), consGridBagConstraints23);
		this.add(getJLabelNotifyInactiveOffset(), consGridBagConstraints24);
		this.add(getJTextFieldNotifyInactiveOffset(), consGridBagConstraints25);
		this.add(getJLabelMinutes(), consGridBagConstraints26);
		this.add(getJCheckBoxNotifyAdjust(), consGridBagConstraints27);
        this.add(getJLabelNotifyAdjust(), consGridBagConstraints28);
        this.add(getJCheckBoxEnableSchedule(), consGridBagConstraints29);
        this.add(getJLabelNotifySchedule(), consGridBagConstraints30);
		setSize(416, 348);

		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initializeAddPanel();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/21/2001 4:56:13 PM)
 */
@SuppressWarnings("unchecked")
private void initializeAddPanel()
{
	getAddRemovePanel().setMode( com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE );

	IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List groups = cache.getAllContactNotificationGroups();
		java.util.Vector lmNotifies = new java.util.Vector( (int)(groups.size() * .75) );

		lmNotifies.addAll(groups);

		getAddRemovePanel().leftListSetListData(lmNotifies);
	}
}
/**
 * Method to handle events for the AddRemovePanelListener interface.
 * @param newEvent java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
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
@Override
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
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
@Override
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
@Override
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
@Override
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
@Override
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
@Override
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
@Override
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
@Override
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * setValue method comment.
 */
@Override
@SuppressWarnings("unchecked")
public void setValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramDirectBase program = (com.cannontech.database.data.device.lm.LMProgramDirectBase)o;
	
	//init storage that will contain all possible items
	java.util.Vector allItems = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );
	for( int i = 0; i < getAddRemovePanel().leftListGetModel().getSize(); i++ )
		allItems.add( getAddRemovePanel().leftListGetModel().getElementAt(i) );

	java.util.Vector usedItems = new java.util.Vector( getAddRemovePanel().leftListGetModel().getSize() );

	for( int i = 0; i < program.getLmProgramDirectNotifyGroupVector().size(); i++ )
	{
		com.cannontech.database.db.device.lm.LMDirectNotificationGroupList aNotificationGroup = program.getLmProgramDirectNotifyGroupVector().get(i);
		
		for( int j = 0; j < allItems.size(); j++ )
		{
			if( ((LiteNotificationGroup)allItems.get(j)).getNotificationGroupID() ==
				aNotificationGroup.getNotificationGroupID().intValue() )
			{
				usedItems.add( allItems.get(j) );
				allItems.removeElementAt(j);				
				break;
			}
			
		}		
	}

	getAddRemovePanel().leftListSetListData( allItems )	;
	getAddRemovePanel().rightListSetListData( usedItems )	;
	
	Integer numStart = program.getDirectProgram().getNotifyActiveOffset();
	Integer numStop = program.getDirectProgram().getNotifyInactiveOffset();
	
	handleOffsetEnabling(numStart.intValue() != -1, numStop.intValue() != -1);
	
	if(numStart.intValue() != -1)
		getJTextFieldNotifyActiveOffset().setText( new Integer(program.getDirectProgram().getNotifyActiveOffset().intValue() / 60).toString() );
	if(numStop.intValue() != -1)
		getJTextFieldNotifyInactiveOffset().setText( new Integer(program.getDirectProgram().getNotifyInactiveOffset().intValue() / 60).toString() );
	
	if (program.getDirectProgram().getNotifyAdjust() == LMProgramDirect.NOTIFY_ADJUST_ENABLED.intValue()) {
	    getJCheckBoxNotifyAdjust().setSelected(true);
	    getJLabelNotifyAdjust().setEnabled(true);
	}
	if (program.getDirectProgram().getNotifyAdjust() == LMProgramDirect.NOTIFY_ADJUST_DISABLED.intValue()) {
	    getJCheckBoxNotifyAdjust().setSelected(false);
	    getJLabelNotifyAdjust().setEnabled(false);
	}

    if (program.getDirectProgram().shouldNotifyWhenScheduled() == LMProgramDirect.NOTIFY_SCHEDULE_ENABLED.intValue()) {
        getJCheckBoxEnableSchedule().setSelected(true);
        getJLabelNotifySchedule().setEnabled(true);
    } 
    else
    {
        getJCheckBoxEnableSchedule().setSelected(false);
        getJLabelNotifySchedule().setEnabled(false);
    }
}

@Override
public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        @Override
        public void run() 
            { 
            getJCheckBoxEnableStart().requestFocus(); 
        } 
    });    
}

	/**
	 * This method initializes jLabelMinNotifyTime
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelMinNotifyTime() {
		if(jLabelMinNotifyTime == null) {
			jLabelMinNotifyTime = new javax.swing.JLabel();
			jLabelMinNotifyTime.setText("Program Start:");
			jLabelMinNotifyTime.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
			jLabelMinNotifyTime.setMaximumSize(new java.awt.Dimension(108,19));
			jLabelMinNotifyTime.setMinimumSize(new java.awt.Dimension(108,19));
			jLabelMinNotifyTime.setName("ivjJLabelMinNotifyTime");
			jLabelMinNotifyTime.setPreferredSize(new java.awt.Dimension(108,19));
			jLabelMinNotifyTime.setEnabled(false);
		}
		return jLabelMinNotifyTime;
	}
	/**
	 * This method initializes JLabelNotifyInactiveOffset
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelNotifyInactiveOffset() {
		if(jLabelNotifyInactiveOffset == null) {
			jLabelNotifyInactiveOffset = new javax.swing.JLabel();
			jLabelNotifyInactiveOffset.setText("Program Stop: ");
			jLabelNotifyInactiveOffset.setMaximumSize(new java.awt.Dimension(108,19));
			jLabelNotifyInactiveOffset.setMinimumSize(new java.awt.Dimension(108,19));
			jLabelNotifyInactiveOffset.setPreferredSize(new java.awt.Dimension(108,19));
			jLabelNotifyInactiveOffset.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
			jLabelNotifyInactiveOffset.setName("JLabelNotifyInactiveOffset");
			jLabelNotifyInactiveOffset.setEnabled(false);
		}
		return jLabelNotifyInactiveOffset;
	}
	/**
	 * This method initializes JLabelNotifyAdjust
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelNotifyAdjust() {
	    if(jLabelNotifyAdjust == null) {
	        jLabelNotifyAdjust = new javax.swing.JLabel();
	        jLabelNotifyAdjust.setText("Notify on Adjustment");
	        jLabelNotifyAdjust.setMaximumSize(new java.awt.Dimension(200,19));
	        jLabelNotifyAdjust.setMinimumSize(new java.awt.Dimension(200,19));
	        jLabelNotifyAdjust.setPreferredSize(new java.awt.Dimension(200,19));
	        jLabelNotifyAdjust.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
	        jLabelNotifyAdjust.setName("JLabelNotifyAdjust");
	        jLabelNotifyAdjust.setEnabled(false);
	    }
	    return jLabelNotifyAdjust;
	}
	/**
	 * This method initializes jTextFieldNotifyActiveOffset
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldNotifyActiveOffset() {
		if(jTextFieldNotifyActiveOffset == null) {
			jTextFieldNotifyActiveOffset = new javax.swing.JTextField();
			jTextFieldNotifyActiveOffset.setMinimumSize(new java.awt.Dimension(47,20));
			jTextFieldNotifyActiveOffset.setMaximumSize(new java.awt.Dimension(47,20));
			jTextFieldNotifyActiveOffset.setPreferredSize(new java.awt.Dimension(47,20));
			jTextFieldNotifyActiveOffset.setName("jTextFieldNotifyActiveOffset");
			jTextFieldNotifyActiveOffset.setToolTipText("Minutes to wait  before notifying when the program goes active");
			jTextFieldNotifyActiveOffset.setEnabled(false);
		}
		return jTextFieldNotifyActiveOffset;
	}
	/**
	 * This method initializes jTextFieldNotifyInactiveOffset
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldNotifyInactiveOffset() {
		if(jTextFieldNotifyInactiveOffset == null) {
			jTextFieldNotifyInactiveOffset = new javax.swing.JTextField();
			jTextFieldNotifyInactiveOffset.setMaximumSize(new java.awt.Dimension(47,20));
			jTextFieldNotifyInactiveOffset.setMinimumSize(new java.awt.Dimension(47,20));
			jTextFieldNotifyInactiveOffset.setPreferredSize(new java.awt.Dimension(47,20));
			jTextFieldNotifyInactiveOffset.setName("jTextFieldNotifyInactiveOffset");
			jTextFieldNotifyInactiveOffset.setToolTipText("Minutes to wait  before notifying after the program becomes inactive");
			jTextFieldNotifyInactiveOffset.setEnabled(false);
		}
		return jTextFieldNotifyInactiveOffset;
	}
	/**
	 * This method initializes jLabelMinutes
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabelMinutes() {
		if(jLabelMinutes == null) {
			jLabelMinutes = new javax.swing.JLabel();
			jLabelMinutes.setText("(min.)");
			jLabelMinutes.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 10));
			jLabelMinutes.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			jLabelMinutes.setPreferredSize(new java.awt.Dimension(44,16));
			jLabelMinutes.setMinimumSize(new java.awt.Dimension(44,16));
			jLabelMinutes.setMaximumSize(new java.awt.Dimension(44,16));
			jLabelMinutes.setName("jLabelMinutes");
		}
		return jLabelMinutes;
	}
	/**
	 * This method initializes jCheckBoxEnableStart
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getJCheckBoxEnableStart() {
		if(jCheckBoxEnableStart == null) {
			jCheckBoxEnableStart = new javax.swing.JCheckBox();
			jCheckBoxEnableStart.setName("jCheckBoxEnableStart");
		}
		return jCheckBoxEnableStart;
	}
	/**
	 * This method initializes jCheckBoxEnableStop
	 * 
	 * @return javax.swing.JCheckBox
	 */
	private javax.swing.JCheckBox getJCheckBoxEnableStop() {
		if(jCheckBoxEnableStop == null) {
			jCheckBoxEnableStop = new javax.swing.JCheckBox();
			jCheckBoxEnableStop.setName("jCheckBoxEnableStop");
		}
		return jCheckBoxEnableStop;
	}
    /**
     * This method initializes jCheckBoxEnableSchedule
     * 
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getJCheckBoxEnableSchedule() {
        if(jCheckBoxEnableSchedule == null) {
            jCheckBoxEnableSchedule = new javax.swing.JCheckBox();
            jCheckBoxEnableSchedule.setName("jCheckBoxEnableSchedule");
        }
        return jCheckBoxEnableSchedule;
    }
	
	private javax.swing.JCheckBox getJCheckBoxNotifyAdjust() {
	    if(jCheckBoxNotifyAdjust == null) {
	        jCheckBoxNotifyAdjust = new javax.swing.JCheckBox();
	        jCheckBoxNotifyAdjust.setName("jCheckBoxNotifyAdjust");
	    }
	    return jCheckBoxNotifyAdjust;
	}
    /**
     * This method initializes JLabelNotifySchedule
     * 
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getJLabelNotifySchedule() {
        if(jLabelNotifySchedule == null) {
            jLabelNotifySchedule = new javax.swing.JLabel();
            jLabelNotifySchedule.setText("Notify on Schedule ");
            jLabelNotifySchedule.setMaximumSize(new java.awt.Dimension(200,19));
            jLabelNotifySchedule.setMinimumSize(new java.awt.Dimension(200,19));
            jLabelNotifySchedule.setPreferredSize(new java.awt.Dimension(200,19));
            jLabelNotifySchedule.setFont(new java.awt.Font("Dialog", java.awt.Font.BOLD, 14));
            jLabelNotifySchedule.setName("JLabelNotifySchedule");
            jLabelNotifySchedule.setEnabled(false);
        }
        return jLabelNotifySchedule;
    }
	
	public void handleOffsetEnabling(boolean start, boolean stop)
	{
		getJTextFieldNotifyActiveOffset().setEnabled(start);
		getJLabelMinNotifyTime().setEnabled(start);
		
		getJTextFieldNotifyInactiveOffset().setEnabled(stop);
		getJLabelNotifyInactiveOffset().setEnabled(stop);
		
		getJCheckBoxEnableStart().setSelected(start);
		getJCheckBoxEnableStop().setSelected(stop);
		
		fireInputUpdate();
	}
}

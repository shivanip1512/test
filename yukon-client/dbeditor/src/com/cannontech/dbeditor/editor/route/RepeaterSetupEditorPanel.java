package com.cannontech.dbeditor.editor.route;

import com.cannontech.database.db.*;

import com.cannontech.common.gui.util.DataInputPanel;

/**
 * This type was created in VisualAge.
 */
public class RepeaterSetupEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.AddRemovePanelListener, java.awt.event.ActionListener {
	private javax.swing.JButton ivjAdvancedSetupButton = null;
	private AdvancedRepeaterSetupEditorPanel advancedRepeaterSetupEditorPanel = null;
	private Object objectToEdit = null;
	private int rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
	private boolean rightListDragging = false;
	private com.cannontech.common.gui.util.AddRemovePanel ivjRepeatersAddRemovePanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public RepeaterSetupEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getAdvancedSetupButton()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC4(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void advancedSetupButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	getValue(this.objectToEdit);
	getAdvancedRepeaterSetupEditorPanel().setValue(this.objectToEdit);

	java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	
	com.cannontech.common.gui.util.BooleanDialog b = new com.cannontech.common.gui.util.BooleanDialog(getAdvancedRepeaterSetupEditorPanel(), owner);
	b.yesButtonSetText("Ok");
	b.noButtonSetText("Cancel");
	b.setTitle("Advanced Repeater Setup");
	b.setLocationRelativeTo(this);
	b.setSize( new java.awt.Dimension(445, 485) );

	if ( b.getValue() )
	{
		getAdvancedRepeaterSetupEditorPanel().getValue(this.objectToEdit);
		fireInputUpdate();
		setValue(this.objectToEdit);
	}
}
/**
 * connEtoC1:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mousePressed(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mousePressed(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.repeatersAddRemovePanel_RightListMouse_mousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseReleased(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseReleased(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.repeatersAddRemovePanel_RightListMouse_mouseReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (AdvancedSetupButton.action.actionPerformed(java.awt.event.ActionEvent) --> RepeaterSetupEditorPanel.advancedSetupButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.advancedSetupButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (AddRemovePanel.addRemovePanel.addButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if( getRepeatersAddRemovePanel().rightListGetModel().getSize() > 0 )
			getAdvancedSetupButton().setEnabled(true);
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (AddRemovePanel.addRemovePanel.removeButtonAction_actionPerformed(java.util.EventObject) --> RepeaterSetupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if( getRepeatersAddRemovePanel().rightListGetModel().getSize() == 0 )
			getAdvancedSetupButton().setEnabled(false);
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (RepeatersAddRemovePanel.addRemovePanel.rightListMouse_mouseExited(java.util.EventObject) --> RepeaterSetupEditorPanel.repeatersAddRemovePanel_RightListMouse_mouseExited(Ljava.util.EventObject;)V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
	try {
		// user code begin {1}
		// user code end
		this.repeatersAddRemovePanel_RightListMouse_mouseExited(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.database.db.setup.gui.route.AdvancedRepeaterSetupEditorPanel
 */
protected AdvancedRepeaterSetupEditorPanel getAdvancedRepeaterSetupEditorPanel() {
	if( advancedRepeaterSetupEditorPanel == null )
		advancedRepeaterSetupEditorPanel = new AdvancedRepeaterSetupEditorPanel();
		
	return advancedRepeaterSetupEditorPanel;
}
/**
 * Return the AdvancedSetupButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAdvancedSetupButton() {
	if (ivjAdvancedSetupButton == null) {
		try {
			ivjAdvancedSetupButton = new javax.swing.JButton();
			ivjAdvancedSetupButton.setName("AdvancedSetupButton");
			ivjAdvancedSetupButton.setText("Advanced Setup...");
			ivjAdvancedSetupButton.setMaximumSize(new java.awt.Dimension(159, 27));
			ivjAdvancedSetupButton.setActionCommand("Advanced Setup >>");
			ivjAdvancedSetupButton.setPreferredSize(new java.awt.Dimension(159, 27));
			ivjAdvancedSetupButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAdvancedSetupButton.setMinimumSize(new java.awt.Dimension(159, 27));
			ivjAdvancedSetupButton.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedSetupButton;
}
/**
 * Return the AddRemovePanel property value.
 * @return com.cannontech.common.gui.util.AddRemovePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.AddRemovePanel getRepeatersAddRemovePanel() {
	if (ivjRepeatersAddRemovePanel == null) {
		try {
			ivjRepeatersAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
			ivjRepeatersAddRemovePanel.setName("RepeatersAddRemovePanel");
			// user code begin {1}
			ivjRepeatersAddRemovePanel.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
			ivjRepeatersAddRemovePanel.setRightListMax( new Integer(7) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRepeatersAddRemovePanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {

	com.cannontech.database.data.route.CCURoute route = (com.cannontech.database.data.route.CCURoute) val;
	
	//Build up an assigned repeaterRoute Vector
	java.util.Vector repeaterRoute = new java.util.Vector( getRepeatersAddRemovePanel().rightListGetModel().getSize() );
	Integer deviceID = null;
	for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
	{
		deviceID = new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());

		com.cannontech.database.db.route.RepeaterRoute rr = new com.cannontech.database.db.route.RepeaterRoute(
																									route.getRouteID(),
																									deviceID,
																									new Integer(7),
																									new Integer(i+1) );
		
		repeaterRoute.addElement(rr);
	}

	if ( !route.getRepeaterVector().isEmpty() )
	{
		for( int i = 0; i < repeaterRoute.size(); i++ )
		{
			for ( int j = 0; j < route.getRepeaterVector().size(); j++ )
			{
				if ( ((com.cannontech.database.db.route.RepeaterRoute)route.getRepeaterVector().elementAt(j)).getDeviceID().
					equals(((com.cannontech.database.db.route.RepeaterRoute)repeaterRoute.elementAt(i)).getDeviceID()) )
				{
					((com.cannontech.database.db.route.RepeaterRoute)repeaterRoute.elementAt(i)).setVariableBits(
							((com.cannontech.database.db.route.RepeaterRoute)route.getRepeaterVector().elementAt(j)).getVariableBits() );
					break;
				}
			}
		}
	}
	
	route.setRepeaterVector(repeaterRoute);
	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getAdvancedSetupButton().addActionListener(this);
	getRepeatersAddRemovePanel().addAddRemovePanelListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RepeaterSetupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(382, 274);

		java.awt.GridBagConstraints constraintsRepeatersAddRemovePanel = new java.awt.GridBagConstraints();
		constraintsRepeatersAddRemovePanel.gridx = 0; constraintsRepeatersAddRemovePanel.gridy = 0;
		constraintsRepeatersAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
		add(getRepeatersAddRemovePanel(), constraintsRepeatersAddRemovePanel);

		java.awt.GridBagConstraints constraintsAdvancedSetupButton = new java.awt.GridBagConstraints();
		constraintsAdvancedSetupButton.gridx = 0; constraintsAdvancedSetupButton.gridy = 1;
		constraintsAdvancedSetupButton.anchor = java.awt.GridBagConstraints.EAST;
		constraintsAdvancedSetupButton.insets = new java.awt.Insets(10, 0, 0, 0);
		add(getAdvancedSetupButton(), constraintsAdvancedSetupButton);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
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
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		RepeaterSetupEditorPanel aRepeaterSetupEditorPanel;
		aRepeaterSetupEditorPanel = new RepeaterSetupEditorPanel();
		frame.add("Center", aRepeaterSetupEditorPanel);
		frame.setSize(aRepeaterSetupEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC5(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
	rightListItemIndex = -1;
	rightListDragging = false;

	return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
	rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
	rightListDragging = true;

	return;
}
/**
 * Comment
 */
public void repeatersAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
	int indexSelected = getRepeatersAddRemovePanel().rightListGetSelectedIndex();

	if ( rightListDragging &&  indexSelected != -1 && indexSelected != rightListItemIndex )
	{

		Object itemSelected = new Object();
		java.util.Vector destItems = new java.util.Vector( getRepeatersAddRemovePanel().rightListGetModel().getSize() + 1 );

		for( int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++ )
			destItems.addElement( getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i) );

		itemSelected = destItems.elementAt( rightListItemIndex );
		destItems.removeElementAt( rightListItemIndex );
		destItems.insertElementAt( itemSelected, indexSelected );
		getRepeatersAddRemovePanel().rightListSetListData(destItems);

		getRepeatersAddRemovePanel().revalidate();
		getRepeatersAddRemovePanel().repaint();

		// reset the values
		rightListItemIndex = -1;
		fireInputUpdate();
	}

	rightListDragging = false;

	return;
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
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC6(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC1(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	if (newEvent.getSource() == getRepeatersAddRemovePanel()) 
		connEtoC2(newEvent);
	// user code begin {2}
	// user code end
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param newEvent java.util.EventObject
 */
public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.database.db.setup.gui.route.AdvancedRepeaterSetupEditorPanel
 */
protected void setAdvancedRepeaterSetupEditorPanel(AdvancedRepeaterSetupEditorPanel newValue) {
	this.advancedRepeaterSetupEditorPanel = newValue;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	this.objectToEdit = val;
	
	com.cannontech.database.data.route.CCURoute route = (com.cannontech.database.data.route.CCURoute) val;

	java.util.Vector repeaterRoutes = route.getRepeaterVector();
	java.util.Vector assignedRepeaters = new java.util.Vector();
	java.util.Vector availableRepeaters = new java.util.Vector();
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List devices = cache.getAllDevices();
		com.cannontech.database.data.lite.LiteYukonPAObject liteDevice = null;
		int repeaterRouteDeviceID;
		for(int i=0;i<repeaterRoutes.size();i++)
		{
			for(int j=0;j<devices.size();j++)
			{
				liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(j);
				if( com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(liteDevice.getType()) )
				{
					repeaterRouteDeviceID = ((com.cannontech.database.db.route.RepeaterRoute)repeaterRoutes.get(i)).getDeviceID().intValue();
					if( repeaterRouteDeviceID == liteDevice.getYukonID() )
					{
						assignedRepeaters.addElement(liteDevice);
						break;
					}
				}
			}
		}
		boolean alreadyAssigned = false;
		for(int i=0;i<devices.size();i++)
		{
			alreadyAssigned = false;
			liteDevice = (com.cannontech.database.data.lite.LiteYukonPAObject)devices.get(i);
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isRepeater(liteDevice.getType()) )
			{
				for(int j=0;j<assignedRepeaters.size();j++)
				{
					if( ((com.cannontech.database.data.lite.LiteYukonPAObject)assignedRepeaters.get(j)).getYukonID() ==
							liteDevice.getYukonID() )
						alreadyAssigned = true;
				}
				if( !alreadyAssigned )
				{
					availableRepeaters.addElement(liteDevice);
				}
			}
		}
	}

	getRepeatersAddRemovePanel().rightListSetListData( assignedRepeaters );
	getRepeatersAddRemovePanel().leftListSetListData( availableRepeaters );
	
	if( assignedRepeaters.size() > 0 )
		getAdvancedSetupButton().setEnabled(true);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GGF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BAFFD8D45715A4B1EA94FFB546DA1BECCCEAD236E19333F17B393B7EE1293B1FE9CDE3DB5CE4D3F7CBDB3F44FE357B597D5CFEB635594BG43G8372E3B498B5A2A211060EE0B031C83272C384A516AA2124F12BCF668183B37346B7EFE078B9FB4E7D7166B13C81755B70FD07776E39675E73631EF36E39778DF19CD91911EAF190121AC1785F87290454D49372C8E509D7606264A4ABB17C9D85A8A0
	0DDD6970DC8CFDDB9FCA32661301D69B34D7C15B7E05A46B1B70DEC47C2D57AA61A5969F683B7C78EB8726677314D57049246DA369F6F86681A4G8E1F3BE4647FC1FAB147EF62F8812996C23676A04D1574C39CF7865A6DG4B8192FB987D86F8E6D14E77571E61F457162CA43B3E750EA59767B14CA9202C170DED614F9272CC6A4EDE64B5CA6544B1F150DE8B004A2711B7BE2C05E7DD7BC06FC75D0A5759AD79FC0ACF133B7B1C324B2DA92AD3D5021ADCDBFBC5764B12A62BD7E5AD68BFCF3B5AA51F6CA144
	530773FFB35DC66783BBC2FBB745AD9CC11E2B61FDA3C00A0E7FE49471F56EA4EB870047AD747C701DBA2EC75768B3E44FCB3E343DB1741C65E67AA53AA3FAAE8D3E1EF9002E494FB251765B214F5E1FE4AD81E882F0814C82B8C8FD61BF2D3F05E77DCD2DD171FA95DF3D5F5F60F1299FABAE59836F35355043F1D764EEC5F59142567B1E74CA93F92683B6EE519D0FE3B6C9F8CF2CFD6215A7495667AED9B2CDEC32B5214A72F0BF33451A7E08CD986F0DB1F9A777CF764D493C7FC40C3CF74CD652B2CCF82F59
	D2E1594E6759E360DDB900EBED21EBFD885ED339FFB03C1D627D86BC73532554E7309D87FD5B8604EFF46589D95A52D7107C6AB6D3BBACCF2A3734F8188CB7BC51323CC0F946FB984FDCDD162D943F5A00E732FCC67158CE063EF35EA4AB732FDDD9983709505E881082309B205817E42D81E871899FBB1BF972B6FD2CDDD23506205B6312D542466E4A5C8FCFB92429D22BAF20C93EEE39D3D25D52CD0F9CA01C66B719E8031B1346D9EC687B5D4063026C13D5C9F3ABBE18DD724A903C81C613E3ABC09AD5263D
	9DEE2F8C8C3CFEA266AD32954153A38534CF7CAE887A88856BBFE843B59914BAE1C618G5E19DD0E66203D7AC07FB800453A9D165179AE49AA640B5A5AB6C599887ABBD0931262C3BB7F10660ED5703E4EA7ECFC580E38B2A549DA2708F9DE30959A66E9F2F7237812BAACE24CB60A3939DD91BE739FDD42E71AF21FA015DBAE1A7A8C0226B0DF09D722E327104E09F231760A11AE13716D0AF99C5DA9B09E4F1972C87BFF18BFCFDF2B793938D6EBC06E4DGBBF59B5B32CA184FB6C881D9F8699FD4F0B0599531
	B813737B2368EB99DC6EAC6EBF0781F2F4DDFECCE9EC7EA4EB95C097C09CC08240E63F3029B4FA43A01FF13E1C99F4917CEE5F36E313F8259978654EB07EF664B14AD5C45E48427DCB9E14FDDA93EC1FBE973C84F8DFC6B64E47633BC7514764D03C9F72C1A384E0C04797E4E34CCA1A263AEF42068C5B30667675C67A8FE4A32FC07026BF12CA842F12EC54D19902FC20539F4F46D86AD6FCBE19AE2ABE04773FC3E3D97648DE903AD1897AB4067FDD8E464300BC4CF3C9F58E96C90312A708F90955942BA53413
	DFF5FB41D5B03511EB37B04EFF2957C4883BE17D31B62A023E26DBA2E6070E0918AD9BF90C2C3FFE5CE23667ACFF2249326A960B393537A2B14BE4AA351B4BFE0266940E8FB0D92D67D126D8558D0B158DF9B85F4B40E787C03AAAE2E5159D7BC5BE62C15228F8BC4C243A2D176466635A483702B224F1CCFC17F47BAD4FAD0C6A6B6436E47DABF351169EC34E5C2622AD0B47C46DF8885AB5G9DAA3367D215590351ED55692A8230B781F8820CEEFD40C877443820CB61F43BCD69FC93022ECDE3F4AE0D512D51
	0CF475E1C1179FE4F4A7020CAE94B452E591C1174C69F6F03AB3030C0E2DD366F52CE14C6A545E205B99545C38A3C89A8413BF285135E3F5690D8C9B1FE3712028BF63ED381E9B211D0238C161FB5F4D957567520115A465ABEF452C33730718ECB5C3915FAB39CF541FF97948D3033ED9C342D78E669977407AC0C069F6536D172F7337720C3E49F0AF66E54333472D8634D6C89EA16193DB72D0DE5C1FB91A57AB75E3F62E6BF2603B9920A90478BF069D8659EDA1364FD641B30D4E17B0F68AF38BF5DAA7FB34
	4BEA0F22FAE55709FEE9D0F2CAC31A1366B1E7BD6DBE0F6F95E2EDDEBB5B0063DD03D82538E88CB584B5CD71DD2F0F1A2CAD76ECE7ABBAC55D79F851B9AE6B2627D7103AF011456C9CB0F76FE1CBFC08573C2148BA0C7D2DB5ED47DFFE1D49EC12B1AEECC2064127GD8AEEA4CB266A27FB033C982BE0705DDBC636729DE2EAB32D7991429D7B9214914E332DFB735977A23132A75071B7D108DAAF4BF5ED0D14856D4261E4A8A2C9328BB1377BB3A3C49A007FDC448F2FA22E52AACAAFD3F8BF1982FAFCE88DECD
	40E7CC6715952EB761656E6D535A5C816DE3A598102FFB71FF3BAA8782B36B7CDF9FFD8CCF156758C07D24092FDF19E662B65319292BF0FB7507058EEBC07E9D3A8E5F8DB75C1E8ED7A066246914E0BA3C5EF3798EF550A71EC6892643A9A2F4A89B857F9C95BAFC091C3BBD9D4E075CDACC8D187C4AEF5B6ED0FEBEE9CC69D9BDA5E7C41FAD170EC6EA61045129F5B1BBF311AC54F95DA8BBF3E568789C0A5FEC40B39BFD41A6EC348D7ADA4644B96111820C63C4E8EF84C881588DD0BC8E31BEAEF26F7F6EFD0F
	4CF08EEB053DC475C91E2B138AA8F19E4A4775001A07375F4CC7B9E84DC3C4DDF9E1AC5A8E9D6313F577C7355938CF260CFBBB5A1ED16358FA6E3D0A7A537D9097A8648C8CC1BDD8DBFB11E583A26EE56606ED148F58E8DC6CF3634D46FDEE5D5ED3169C137CCA773989463BEA42F016566FD9AE94615E6D073EF800D5G6B81368110F01235ACAC6C7FD87E07B3591F3AD6075288389628F59A236BD25DDEB963F7E76783D346B9235ACDE1B3BBF7264FDCF7CC8EA6E67751ACF19FF6ADAC6A0E3F2279F6A93493
	81B6044579A779AEF57A710CBAED8D1B0DFBE4626E78BDB5E5DC342F6EB265678F5F9D3F09A963227959634C461DA1F7472FF94A38E8FEE5D17C587D45982DB16B202F9D0061BE4D24B5A034636344BADB29CF2C06F6320EAB20B82CFB3106E73865B633737FF4109CB7B54FC643EACE43F8BCFB47BCB2EE03C65CD13038FF32A0767977EF8A4C4F3F4F95181FFF7F3E200061E18E63ED19687FC641746763678B4C4E4707294C4F6F5FCB670612A3C093919EA4F85FEB5ABEE2F2CE3EACF9E52EEF9119984B62B4
	67B52571BD5D468B42E6B65E455741F68FFB9E3AE7725AD5C13BA98A5785ED8FC01C814FF270F79CD85BAF857CEA000DGA9G64DE11FB2F974E38771D8FCD33777D31A8769AF695192F61DF0A705C94D002EA379C3DCEF2D1315E9749722D1A6CC513B29A9F9D2FC9EA2FBCE51D03F4BC6B0BB5FE22A816AF3075EA98B72ED70D3EDEF1D9535D51175C9B2B16A924B5E84D3D2CE6C9557187A97E1C814FEA19D59362DBCC9B742D3ED754FBB993C639CE6B72A6CF42D76B782F060D78D3BADEA1C67C7BBA7E418C
	3C2B71C843CAD0A3A46EBE1465EB99629C6400F6A5C087C05FFD6CBC447640E70730BE68D1A5AF18712ADF6EA657E661D8FF26983B916837826C81B884FDF53304CE8F6D47EFEFD74C6BCEAE97037A2967C71EDF1F21B54CAA18F34DAC11DF9D84F16B217D320E2B27F4BB20ED3BDF60DE2138C350BE2363560DA12E8D5A57F4DC2ADD64EB3F0E2BEE20BC207D320EBBB5C2F9C01B4C96B176AF0E58B172EF8E7398790943BC4F7D54B1FD1E7B05A316EFB3793E18A574F04C967BC9F11868BEFE54707E3509BB5D
	836E84980FEFFE2EBCD8BCFDAFC344138BF4FEF836705B6A9BD81F6AB5B25EBB5456DED05D2E8629370DC6812B555E325A7878645922D6EB31621C76B9D04782544D9135F328DC544E09CF2EA0EDF117CC6F08D2BACED9BA6630FC911A93291D9320F6E6BC17E58A1EEB20FF479C44ED48B45E53E0EC9C37301A0E757FF2D2BF7A816ED1117E93B6E3BF4A7B1BC9637FBC299FED74AE9F4FF47B76F1215B1EEFA7904F5F7DBE667DD74DDC26D397FF36F79EA3E573FE1D4962791F43F8C72C0153AF1E0B5F50D722
	28BC565F25F768DEA96476863DD75DA3B231BFC06FE3E8BCA783FD4ADC11BB3A42481F5DE86076A579DC38A6147E960D6BF6F83F7600207F8525772BF20F2C2A10C8F0F6C11FCD679F037739BA7D7747D04E9E2867994DCFE9DED8836F6B814A6651FBD6FB137035872666FF716DD2C0BA0CC1B8487B0A67C5F2BF635952C075F77B22743FC1F30D8768C37304FC2DA398C3328F2FFDDD02EEC4784D2AF9C2462733040CEB67194BE814F3935024G6C636DDD8699D94C3EBC65CCFFFB317E6ADD0EBBE23D53F1EC
	FFFDAB93E3144C0F5EF70FD13CFDFE743EFB59AA72C4B1740D4D97F788779562BE16826DBDG0EF85887815AG7A62C50E1F332F0A4CD0C7F5A87EB65884BC06344D747BF2665DFD7B7FC6260847B57F70B8D9BB7BD729314E69E371EC5D9F664F6D9D47A81E69375F017AED023E6DG768560B7GE7G5A97887DDE5CD74974432F9F751AA6F577511BE821DC27DB9E0A203188E0B23E78197137A979AB0EDAD2E34868DA40E473AF0864C3169F5E47CF5DBCBE5E8F9F0127CF9EAA512F0A1B5C4092BFB41F28BE
	7D7B4A12D39FD4F07B2C9CB272BE137D016937A204D51C67DA7EDCB767986139694D886F6F05318EA99505C8892DC4CABE7A281453EEAE0E50BEBBF6BBF232F18DEFC746AD4AAE0D9E578C8968C4294BADF9145E524A122A4A126A1A8AF1FF53EA975FAFF6026CC7882B15ECA47247F85C42AEEE179F59055F9CFA6A4B246925DF259E0BE117938B799E35B032A65B7B77E432B9FFD092117B1D115B5117518E0D203CBD12A7007917697143AC21476A0566FA44566D8D7C69954F95A51911778A43FB0775732FE5
	3C1FAB8F9607710531EB49878B4DEB49AF951A57120F96CEFFA67EEAE16CEF46CF979A3F997333E6A9568F5BE17D5297A5D9CFG548134AF92B9242E88FD9BFF55B2B5FF1CF77562F98FFF4922897EC726291B4B9DB37F7E6524833F0F5DD46028604579857E348357CBD37C9449F035345E2617670C14634FB9683EAB77D0B47F7D98068B6F5F6CB88E4FCE291FE7FC76F1D8E8F591E201DD08F7C83D3A1D361451B941AEF3F55B0C6EFFB756BC00ECD7953F2CEA438CCB3FGF31F3BECB76E9D89B05F3AC5535F
	75B0998A4A31A6588C34BBG729743FA815481F4G74810C81AC86C8GD88F108C309DE08F00FDC912358420E609107F6BC7AA26111F9755F401D5490DCB4C65DF11ED143F85663B36E4FA7979192AF41AB3D5694C3E71EAA97D1E8AEB5AB16C677782BFAB45732F52539310F59F5CD91A2F63DA836DCA400D55A66BFB25AC4F98BFECD978389F15E1ACC09927C15EF60F50CF39D70150574B4EE1B24D5B7EA52C86E138FE0A1B4BF17CF7986531752CAC1FD94F536566B1F036FCEA8C1CABB70F0166F2E38CB05C
	25F20C7FDED857AE1D2EDD47FD2A63BE21F3A26ECFEE17562763BF23F2A13EC5C60682AFD13906A82E0B63FA8C38CFB9EE209C578AD46ED3D4B156DFCE6BF53747435A9A25F17BE069F4FB8CD3995DFE49A077040E7BD4DF0BE733457A1CCE93382FE70B75B9EB91777FD239BD324ED4C506BF944A0D60050A047D4E3820B38966CC7CB94950C84164EA4269F486D4D2B8F58F2DDF782FAFA5DEEFF2D8082515D87B68042DA4E388B90644CF739E1DB29FD4A701DBE4028807FA94E64F1DD4D97C53E327DF5D746F
	1D01B4E2EDA5877030F3D5D6294F4AAE3C6764BF62EBA6D9FE8582970FA42DA4BDC09F9981ED58A33791C74F957C01CAC013DD75A29A829A59C73F8D1E440FA0A7DB24C01F061EF9360D247B7067062410F5FBA4DF6FC9969AE729A62975B7B03F52E0A7A0EFD1648B23BECB51BF6EBDFE74623635E94452CC32BD0A64EA06F320223612AC2DCF1583FD0A4795727F4FB9BA11ED88578DEB63B4186E202EBEAA6B6C007851059E88BD921F3BBD379916B724121314924F29CE1D425558A72940CAD6DBB57C792422
	1669FD773C2D153FF0614DEB96124A15C453E7067C9A3176C8DE37E798DED89DC797A5C783E9D5499740EF71A0D9C914E422CBE760FC70792A077E7A75BFD38611219AB942CC89C3CF764A0A77E4FDCF0F5BA76B9300DDE17A437A74C8630C22A98CAD59F7F14BFF5F84BFE8A2E95E2AAA7A3F1A7EBF057F2BA9261AE22ACF01F1F712E97F227BC3C2E76ABCA04FDEAA8FAE3F3399D6D0FB65674FBF7E033A1DBBC03BE612C1BF1F42A264C728AE873DB7E5F5F662C307F316264C47B572232B04F05E3DA1FDC654
	301586FD4A60FB35E9BF79CA1A0524C35030A4884E0B198C1CD7E5B11486C9107A1D71AFBBAAEA30F5E82A0E2C1E2E889B4F993B6FAF0EF94EA8D946FF9734ACD2CF337D78E4AF4DB7000FDFA67654F9DD3FA331EB0B2BDE496351A44FC03D4F05F5C64330A6C7EA371ADE1CBFA0E8F8A74F7D3DA8F73FE14C7F81D0CB8788E2C90C8BC992GGDCB1GGD0CB818294G94G88G88GGF954ACE2C90C8BC992GGDCB1GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB85
	86GGGG81G81GBAGGG0392GGGG
**end of data**/
}
}

package com.cannontech.tdc.roweditor;

/**
 * Insert the type's description here.
 * Creation date: (3/8/00 11:45:00 AM)
 * @author: 
 */
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.commandevents.ControlCommand;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class StatusPanelControlEntry extends ManualEntryJPanel implements RowEditorDialogListener, java.awt.event.ActionListener 
{
	private String[] states = null;

	private javax.swing.JLabel ivjJLabelPtName = null;
	private javax.swing.JLabel ivjJLabelPointDeviceName = null;
	private javax.swing.JOptionPane optionBox = null;
	private javax.swing.JButton ivjJButtonRawState1 = null;
	private javax.swing.JButton ivjJButtonRawState2 = null;
	private javax.swing.JLabel ivjJLabelValue = null;
	private javax.swing.JPanel ivjJPanelControl = null;
	private javax.swing.JLabel ivjJLabelStateText = null;
/**
 * EditDataPanel constructor comment.
 */
private StatusPanelControlEntry() {
	super();
	initialize();
}
/**
 * EditDataPanel constructor comment.
 */
public StatusPanelControlEntry(com.cannontech.tdc.roweditor.EditorDialogData data, java.lang.Object currentValue) {
	super(data, currentValue);
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
	if (e.getSource() == getJButtonRawState1()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonRawState2()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC2:  (JButtonOpen.action.actionPerformed(java.awt.event.ActionEvent) --> StatusPanel.jButtonOpen_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonControl_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonClose.action.actionPerformed(java.awt.event.ActionEvent) --> StatusPanel.jButtonClose_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonControl_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 5:54:35 PM)
 * This method will only work when this panel is inside
 *  a RowEditorDialog, else it will no nohing
 *
 */
private void dispose()
{
	java.awt.Container c = this.getParent();
	while( !(c instanceof RowEditorDialog) && c != null )
		c = c.getParent();

	if( c != null )
		((RowEditorDialog)c).dispose();	
}
/**
 * Insert the method's description here.
 * Creation date: (1/9/2001 3:37:40 PM)
 * @return com.cannontech.tdc.roweditor.EditorDialogData
 */
public EditorDialogData getEditorData() 
{
	return super.getEditorData();
}
/**
 * Return the JButtonOpen property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRawState1() {
	if (ivjJButtonRawState1 == null) {
		try {
			ivjJButtonRawState1 = new javax.swing.JButton();
			ivjJButtonRawState1.setName("JButtonRawState1");
			ivjJButtonRawState1.setText("Open");
			ivjJButtonRawState1.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRawState1;
}
/**
 * Return the JButtonClose property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRawState2() {
	if (ivjJButtonRawState2 == null) {
		try {
			ivjJButtonRawState2 = new javax.swing.JButton();
			ivjJButtonRawState2.setName("JButtonRawState2");
			ivjJButtonRawState2.setText("Close");
			ivjJButtonRawState2.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRawState2;
}
/**
 * Return the JLabelPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPointDeviceName() {
	if (ivjJLabelPointDeviceName == null) {
		try {
			ivjJLabelPointDeviceName = new javax.swing.JLabel();
			ivjJLabelPointDeviceName.setName("JLabelPointDeviceName");
			ivjJLabelPointDeviceName.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelPointDeviceName.setText("POINT/DEVICE NAME");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPointDeviceName;
}
/**
 * Return the JLabelPtName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPtName() {
	if (ivjJLabelPtName == null) {
		try {
			ivjJLabelPtName = new javax.swing.JLabel();
			ivjJLabelPtName.setName("JLabelPtName");
			ivjJLabelPtName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPtName.setText("Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPtName;
}
/**
 * Return the JLabelStateText property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStateText() {
	if (ivjJLabelStateText == null) {
		try {
			ivjJLabelStateText = new javax.swing.JLabel();
			ivjJLabelStateText.setName("JLabelStateText");
			ivjJLabelStateText.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelStateText.setText("STATE_TEXT");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStateText;
}
/**
 * Return the JLabelValue property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelValue() {
	if (ivjJLabelValue == null) {
		try {
			ivjJLabelValue = new javax.swing.JLabel();
			ivjJLabelValue.setName("JLabelValue");
			ivjJLabelValue.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelValue.setText("Current State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelValue;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelControl() {
	if (ivjJPanelControl == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 12));
			ivjLocalBorder.setTitle("Control");
			ivjJPanelControl = new javax.swing.JPanel();
			ivjJPanelControl.setName("JPanelControl");
			ivjJPanelControl.setBorder(ivjLocalBorder);
			ivjJPanelControl.setLayout(new java.awt.GridBagLayout());
			ivjJPanelControl.setVisible(true);

			java.awt.GridBagConstraints constraintsJButtonRawState1 = new java.awt.GridBagConstraints();
			constraintsJButtonRawState1.gridx = 1; constraintsJButtonRawState1.gridy = 1;
			constraintsJButtonRawState1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonRawState1.ipadx = 28;
			constraintsJButtonRawState1.insets = new java.awt.Insets(5, 23, 13, 19);
			getJPanelControl().add(getJButtonRawState1(), constraintsJButtonRawState1);

			java.awt.GridBagConstraints constraintsJButtonRawState2 = new java.awt.GridBagConstraints();
			constraintsJButtonRawState2.gridx = 2; constraintsJButtonRawState2.gridy = 1;
			constraintsJButtonRawState2.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJButtonRawState2.ipadx = 26;
			constraintsJButtonRawState2.insets = new java.awt.Insets(5, 20, 13, 27);
			getJPanelControl().add(getJButtonRawState2(), constraintsJButtonRawState2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelControl;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:39:29 PM)
 * @return java.lang.String
 */
public String getPanelTitle()
{
	return "Status Control Panel";
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:15:37 AM)
 * @return int
 */
private int getSelectedStateIntValue() throws StateNotFoundException
{
	int i = 0;
	for( i = 0; i < getStates().length; i++ )
		if( getJLabelStateText().getText().equalsIgnoreCase(getStates()[i]) )
			return i;

	//we did not find the state, lets throw some junk!
	StringBuffer s = new StringBuffer("{");
	for( int j = 0; j < getStates().length; j++ )
		s.append(getStates()[j] + ",");

	s.deleteCharAt( s.length()-1 );
	s.append("}");
	throw new StateNotFoundException("Unable to find the selected state in the stategroup: " + s);
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:13:21 AM)
 * @return java.lang.String[]
 */
private java.lang.String[] getStates() {
	return states;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION StatusPanel() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJButtonRawState1().addActionListener(this);
	getJButtonRawState2().addActionListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 12:19:47 PM)
 */
private void initData() 
{
	setControlJButtons();

	getJLabelPointDeviceName().setText( getEditorData().getDeviceName().toString() + " / " + getEditorData().getPointName() );

	// check to see if the point can be controlled AND its control is NOT disabled
	if( TagUtils.isControllablePoint(getEditorData().getTags())
		 && TagUtils.isControlEnabled(getEditorData().getTags()) )
	{
		getJPanelControl().setVisible( true );
	}

	
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StatusPanelControlEntry");
		setPreferredSize(new java.awt.Dimension(455, 163));
		setLayout(new java.awt.GridBagLayout());
		setSize(296, 156);
		setMinimumSize(new java.awt.Dimension(100, 100));

		java.awt.GridBagConstraints constraintsJLabelPtName = new java.awt.GridBagConstraints();
		constraintsJLabelPtName.gridx = 1; constraintsJLabelPtName.gridy = 1;
		constraintsJLabelPtName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPtName.ipadx = 3;
		constraintsJLabelPtName.insets = new java.awt.Insets(15, 11, 5, 46);
		add(getJLabelPtName(), constraintsJLabelPtName);

		java.awt.GridBagConstraints constraintsJLabelPointDeviceName = new java.awt.GridBagConstraints();
		constraintsJLabelPointDeviceName.gridx = 2; constraintsJLabelPointDeviceName.gridy = 1;
		constraintsJLabelPointDeviceName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPointDeviceName.ipadx = 72;
		constraintsJLabelPointDeviceName.ipady = 2;
		constraintsJLabelPointDeviceName.insets = new java.awt.Insets(15, 4, 5, 14);
		add(getJLabelPointDeviceName(), constraintsJLabelPointDeviceName);

		java.awt.GridBagConstraints constraintsJLabelValue = new java.awt.GridBagConstraints();
		constraintsJLabelValue.gridx = 1; constraintsJLabelValue.gridy = 2;
		constraintsJLabelValue.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelValue.ipadx = 2;
		constraintsJLabelValue.insets = new java.awt.Insets(6, 11, 8, 3);
		add(getJLabelValue(), constraintsJLabelValue);

		java.awt.GridBagConstraints constraintsJPanelControl = new java.awt.GridBagConstraints();
		constraintsJPanelControl.gridx = 1; constraintsJPanelControl.gridy = 3;
		constraintsJPanelControl.gridwidth = 2;
		constraintsJPanelControl.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelControl.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelControl.weightx = 1.0;
		constraintsJPanelControl.weighty = 1.0;
		constraintsJPanelControl.ipadx = -10;
		constraintsJPanelControl.ipady = -9;
		constraintsJPanelControl.insets = new java.awt.Insets(8, 11, 23, 10);
		add(getJPanelControl(), constraintsJPanelControl);

		java.awt.GridBagConstraints constraintsJLabelStateText = new java.awt.GridBagConstraints();
		constraintsJLabelStateText.gridx = 2; constraintsJLabelStateText.gridy = 2;
		constraintsJLabelStateText.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelStateText.ipadx = 114;
		constraintsJLabelStateText.insets = new java.awt.Insets(6, 4, 8, 14);
		add(getJLabelStateText(), constraintsJLabelStateText);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initData();

	setStateText( getStartingValue().toString() );
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{
}

/**
 * Comment
 */
public void jButtonControl_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	// ask for verification from the user first
	Object[] o = {"Execute", "Cancel" };
	int retValue = optionBox.showOptionDialog(
				this,
				"Execute the Control?",
				"Verification", 
				javax.swing.JOptionPane.YES_NO_OPTION,
				javax.swing.JOptionPane.QUESTION_MESSAGE,
				null,
				o,
				o[1].toString() );

	if( retValue == javax.swing.JOptionPane.YES_OPTION )
	{
		javax.swing.JButton buttonPressed = (javax.swing.JButton)actionEvent.getSource();

		long devID = getEditorData().getDeviceID();
		long ptID = getEditorData().getPointID();
		
		if( buttonPressed.getName().equalsIgnoreCase("JButtonRawState1") )
		{
			ControlCommand.send( devID, ptID, ControlCommand.CONTROL_OPENED );
			TDCMainFrame.messageLog.addMessage("Control OPEN was sent successfully for the point '" +
				getEditorData().getPointName() + "'", MessageBoxFrame.INFORMATION_MSG );
		}
		else // must be the button named JButtonRawState2
		{
			ControlCommand.send( devID, ptID, ControlCommand.CONTROL_CLOSED );
			TDCMainFrame.messageLog.addMessage("Control CLOSE was sent successfully for the point '" +
				getEditorData().getPointName() + "'", MessageBoxFrame.INFORMATION_MSG );
		}

		dispose();
	}
	
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonSendAction_actionPerformed(java.util.EventObject newEvent) 
{
	try
	{
		// Create new point Here
		PointData pt = new PointData();
		pt.setId( getEditorData().getPointID() );
		pt.setTags( getEditorData().getTags() );
		pt.setTimeStamp( new java.util.Date() );
		pt.setTime( new java.util.Date() );
		pt.setType( getEditorData().getPointType() );

	
		pt.setValue( (double)getSelectedStateIntValue() );

		
		pt.setQuality( com.cannontech.database.data.point.PointQualities.MANUAL_QUALITY );		
		pt.setStr("Manual change occurred from " + com.cannontech.common.util.CtiUtilities.getUserName() + " using TDC");
		pt.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
		
		// Now send the new pointData
		SendData.getInstance().sendPointData( pt );
	}
	catch( StateNotFoundException snf )
	{
		handleException(snf);
	}
	finally
	{	
		JButtonCancelAction_actionPerformed(null);
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/28/00 3:22:57 PM)
 */
private void setControlJButtons() 
{
	states = getEditorData().getAllStates();

	if( states.length >= 2 )
	{
		// only insert the first 2 states for control purposes
		getJButtonRawState1().setText( states[0] );
		getJButtonRawState2().setText( states[1] );
	}

	// this inits the JComboBox for Manual Entry
	//for( int i = 0; i < states.length; i++ )
		//getJComboBoxValues().addItem( states[i] );
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:13:21 AM)
 * @param newStates java.lang.String[]
 */
private void setStates(java.lang.String[] newStates) {
	states = newStates;
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/2001 10:08:43 AM)
 * @param state java.lang.String
 */
private void setStateText(final String state)
{

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			getJLabelStateText().setText(state);
		}
	})	;

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GDBF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB81F4D4C512C182C409AE1CC1F94839D15007EBEE4513BB9171194B73F6D171496E4343BB393D38F2E776965C3C375C9EAA37F4B219C990C2A2810D8B2CC1D0C0C2C890DC08C19312E082D18399429842121F4CCFB2491F7913BFFF488492382A6E5FBDFFA6B30960F26FF82F72FFD7F5D7D5D7D7D5D7779F482677932D99191B88493092635F1FB288D9F90410F94F7CFDB6474548A9B6E27A379D60
	ED225B3360B989E8536FCE3165139F9D4B0176CCE8BFF5E70A6DDDF8AFA08F9D5BF4GDE2248D9DEC64854A7FFB6E6E4B9AD27381CEC5ABEF7E6BDBCB7G4C829CBED74AA85FF7265040AFB4709CB2B289D95A09FDEEBB3355409D06F6A340D40024CE567FA9F82E251247159FB67A35CECEA4AB1F5915190BFCCCBCB96C6CE2E36B0CE791F9A8A33D8BE5BDCA7544B1B1501E8B00133710A75FA907E7C5E3EF57679D2A3B24C372F8D40FAEF7F417684E0E92CD6D171DAEDD554A4B1BF4C9777B9AA50F2CD4C38F
	CDD5EA616FGA1CAB772DFF3A60772A364A034D7D15C14C114B99B5E97G248AFC6B9062ABDCA9B6BB00626273BC3A2B4218076372C3A4F609471297E51E31AEB63FA4D7F01E897EB7325F23EBB26DB45ABE8DE86BFBD2ECC5GB5GCE00D800AD54976EB975B1BC2B5A75EA556DD6BDD5DE6FB14529FD25BAE5855E4B4B01E260CE4B9D2A66A4042D7793E70AA368B39230F14FDE73B8E613D91F7035CFBAFDBFD97A48494C6C88B6D9BA2BB4F3FA8F33451C1E20CD186C2C66E8329777047AE6286C3344ACDB9F
	2FDA2C91E44FF9E1CFE61A41E735C9F6F1AF2E75DA3A56DB61BD43708F06AF20F82F894F7C74AE6AB3580E815A32DE6E9B0E8DDC17062CE9A43F2CA1229D2626D4E556A9CC07B6A5DC17C5D4E63C42E4DA05AEAFD1FC22894FF4E927F8EC4F81DA21BB45467CEB069D63F6905AD381E683ACGC8834872C0BF8F77317FBC357FBB7AD8232469477CAE45A9EB040D1DF17A9DF84A81DD136ABDBEDD72F4484D1266125A9559C70CBEC92751866DA1639E263E5F81B23E14BD32A669AE55835CA5372C4B1A0F7519F7
	7AED6C234914EAF739E5906076924EB765F481BC9549271F71BAA5DD8E76E074E7CE631ADC141A01A3B000F7E61717E80EB98C73EF84E893F628EDC2FEA7E58DF2C7F9F9032A767A3DF61C89196DC1BBEFE9465C2143FB3C075BF889452D0476FA157399EC5AE862D3636AC075A54CC14C4F7306B0B797295CE7DEF3F01F29493D0F943FF0A2224FD03A4AFC25D58D0F1D77A8CF540B356F99F4C498DF24C60E23EB85A6E3DEC499163FG7F8DE22DEA9D38D63120F7A2C032307123CDC54CE70FC9BE19FB69B79A
	B8186C8CDA1C6979CFC3BC67DB056EAFD35C0B400FF8394D3A079C26B9D87FC273883559189BAA035966EF6C389F4997E50FDEA3FBE50F93DEFC86ED0E9DFD4E0857CED50C19C771DE086FEA88A81F40A75BB186A5DD57DC6DFEDDEE12F55D65698A5297D0D9BEFF3BB7189A382C676DB8BF359F62DB74DFEC475868D0BD9E19AE1298E250DF2631A9AB329B142ED67D9E1D61DFBD037E5DAB8F98396157E7306839A8A9FE4CB32CC618AD2D43B9E8AEB7ACBD26AA12604538BD50DCE83251EE40E12D538D4FC1AF
	0F417E9DBC86F7DE3A07A4DF78B0B3529EB2753E1A4C25DE96C3695EE08CB21D16B6C7567D17B4C798F81F05621E70E22D972DD8E13E5FFA967965772558F682D474F15F7F73D924737CE2B8FD352AA84C244256A79D7938B6F21FDF06340C096C24305FE94746B0DA33E1CBC6EFF120AD95D38EAC5468FEF8055702F650C781086FE376DC58476C417AAD917D1C509E540677E3EB167355C1721D6A3A2FA40FDFE266B9C18D466A39695436380653B5DE37AD79986D36985A4B8132FCFC0DFF164B6B36045EC412
	BA638FD16B532DBE26E325AF3846C5E3F95DF63F83E546GED120F2F498B8E735ED16573299DAE3AED9976ECBFEB7681066BBA7B96BCBBDD1ACF2F07A08DF05B3B4F223E382F99E8E31F2F3842777919A0F7A9002EA3FE3BED13C977CD002BGE883E03EB6638A9EA7A46A9CA56C51A8EB1D2A66161D3BFB240BD209542F1750FCD1D2C54935783E072F51E75B0F42336718DF57D50F6127972A4278B4C4E774591EE642EB35FB8BAA8DBD977EF89A2938DA109929F60E4B5B16B9DFE736DF268757E070EFED1665
	FFFEB31B5903DC6576A875C3BC7B39BD72AAA359A351AF76C6D1E74E71B35E8B7C41FDF1A93C277BC377C9D6FF1D5C0B7232AE327AEB1D40FFCB71BBCDF8265F3FD2BF44F68D50E6DD64B54321964CC76B7BD3EC3B81AA819CG8100297D5C1F7E6B6CA7E4141A2C9E62C473C8CAD3C872B5FC68C1070565CB237D10434A73A561FB5131AB6175761C7E50F9AF8AEB33F10D43467DB12C3DB2EC9C73233C2677F82C233384CAFC7D301714171FE01E05F1C973F506451C2F5787D83DD58A4F9AG96778E9B0F7B56
	G0F6F216366781E1FF7A0F3DD1478CE8898E710002976966732FFBB0739EBA5507287E00DGEAG3A81E281E68D70B5BA726D515156087A3FDD2D86DC9D64526A70FDCF5854F6757A56E25330F161EB31E0A0F49C335F71CAEE3FD420337AF6AE4D250517D2EC87AFF11C3497F1F550EE953862FDB47FC1FB2A40FDD502387950DEFC094759293D9807E950A617E18F8328GF0DC66748B142EC3BB81E04EE596270B857D150F436BCB9CBFD27D3353609F09467588355F6AE1B2560E401F0FDB970596DE53B1BF
	793CA5FABDD65B92399EBB5992399EBB5572B643838F73E90C533FED9939DE13DAA255EB5B286ECF3F13C7F97BFC320FBAEE0D24CB866DD5FDB309D037FD8D87B6E35E85E4604ADA23CF2D2ED69F90F17F5BBD0511635E340F9F31F0FFDCF9196F63BDD64C1F450390FFG2D035CAF4E5B42FDDE87DA5CA0DB5B69701CBD986A83AC2EBB4F233FBD8534D400D5G79C310D706F8BC7FF74B28B937B6B0C24E7D5539686B1CFEAE72BA7F6E9C56A0BE552FF54861EB79BF678A85AD18B96AF5590DE6E7FDEC2DB8DE
	1734AEF918AF6436968ADA34716F34C673973626C72F1A57749391B7A7AB435720916C689CE2F68F40B3FEA8F48D983F671BD1DF2FA6F74A9AD42DCD2ECB68BFAF8E61FA27517C13886FC9C33CF6896C45BADF1186D43F8EF541956C37E2AF5F9F36C27BA0C0AB00770A392EFC389FE334D3135CB01DA62F5CC11CD7F1EC75BEBEF69174DF861085781DGB5D739CCE7890E0501AE375FCDB5E47A9D580F7AA5C13F39226F7A927451938DD23B2CB46AE8D7065FD534B908D7DD689197DD9DF2C83C948F2D93FDE8
	75ED604FDDC09B1DB09F8B44184775BCB186CF8F329DFC0A303AA7E12F396ED9F6156D1BB9B13CAEDA189B093ED5506F0AC8AF95F4F7E1A4FACD8C0F4B4FADE61FF0GDE09897AC08C3CA744C42A2BFEC163FCD6CC78FD572FA8FEFECC78FD17574A6B3EC5C0BBB8066FC1BA2D8926C2BBC96066D3DCB23497895C36EC3EFFAD953857AA90D7FC9354E3B771FC60B99FBD1E7D67A3477365731173761873A3676D1B4FC70BC326DFFBA51F1B7DA62E73FD15FCEE5D82E72B62F353856E5DC2BE3727GDE3C893F97
	DCF29261D34DFB0B6069117B0BC48DDF6302F38ADE1A6DBEF868D0F1D159271F6CE1B5D6E23F790E7F70DBFF0AF8CE2631B9162DF529711CBFE10731FE1B0B799DFFB750924772BB7E32B62C3B44BE010A1417FF29391C47242E861A8158DEF13EB847987F62D83ED7CC24BC73E321DE83280C65B5E160A3DE93A65DBF0DB4441C0CF866CB359F48F444B2BD87E303B561AC288919CC57BEAEF3AE5053E391F767BEF3BC20AD0FC0079274D3CAFFBA040E6BFB081869EF0650D15FD319E67A3E92B39DEDD4E50CE7
	F3FB76C3BE37554F4EA24A5F9D09FA1EBDBC4E38B7B41E0D4DBB484EDB5E4CE63EB02DB3688B73BE9E41974ACACB4197CACB77987BFAE714FD1DE6A7E637E7E97D0879A1E19C2F5DAD9BB8EE21407DBC17633246F35C4CC20E2B984FFD798C2D93F4E847895CE3EDB4AFC3BBD9608E3773B8D8A6F0F9D6449D1C10E22B1B40F356B20AEB05F6C060FEC3F1F8370177968CF7519A692E73DAGE5185B01896135688F3E370C703BD7EB982D00CC2479506D112140F1F530FD6DDE9B776D0489FCDF5BAF61FD1B8E89
	0E794893DD68A34993307F416C1AD1F4B0C3A20CC158E14818836FC70CF7A64BFA9467FF5165F361BD93536917DF70BB5B1CB848D994B731126BD9984773580A6E20DE5B2AC6524B9AC52FAD864F1D407310714E64D596F29DCA63F8BEF97E1CB97F65CF5B1E19C1A247DCE59CDB5F0638E0BEB14E17AE6A1B001F9E47771AA71DFB6159AC7598D5E7AD3D7622E5A7A09BB555ABEB7AG4332DA01794C16626B7B4E3767BA4735DF73B836172F2179F9FE9C5B4BB384BEE79F62971870EC0F18AC760845C0BBFCB3
	3FBB496F451CB39D5AF38196812481645C82B55CAD5C16B9F2A999250E372B5E8658589553D64C666759F7FD5FBF7FA11B6762B9736EA5F3473F1E916D6E6170AD4C9F9C46B34D3E03620DEFF68A4EAF8EE8B38196G24822C84D8BF114F2FD1AEE673C3F72852F529231B5EAA7249B53B647EA09A0BDFB637C5FB4DF3AB1A48649FB41EF593071F1B59381C8D66F12DC6BF2F710C0DBAEEC92EF95CD423DF1271CC0EBA6E01C2733845C63FB46339BA42B816C7948B1E955AD50D96D66C2CB0D65CA56547C3EDG
	50862047333B86B6EED15B70B3462BEDFCDCFCBCBBE34C0467DCE39C4BB75F340F1687F1BEBC6FAC02314B62D99E4476AAF8DF31E64D1A5FGB0BDBE328E4F03C72DBC87E55D4A757D0906777A462D59E9291F1E834A982D419201F600519E233F93F91F3EC3DEA541FFAC170E7190614FAF2F677E3C75413BC84D1257B3F87E8E1F6726DB59FAB8EF8D6639341E5559CC666BDF50FDC4739B6722FBAB0372FE07E53331F6E78BF9BFA38E70B32FD19B1739BEA4D1F2A3F87B89A8BD3EE96F01432DDD6A92E31639
	D097D1CF3314E950FE6D423A8A0EF73ACBD2DC1768B5FD93F474F4B17F336EA34297B7185E6FAC7E3E7B736879F5768D1541667FDEEF74F3CDD1EF64F34DC73D114FB5453DA35FB79D6C0D7EFD7033DE7377C1366E337BF0ED2603AF4D85D88A108E10F59B4FFDCF77FEC0E86E0B1077EA1DDDF8CF023FC25045EF8F1468F3FDC4997D378B73957CC6532EC2B839113F786D01026B252BDE0AE4381FAAE8A34D55552D332E867EB9055613F2A7C533DA74D5DA9797413CCAEF637569DBC790D783ED3B4029C7F89D
	5BA6F06F537AF9B63413852ED062E7414582770A2801D3856E35DCA177879C67ED25F221ED973897DA05DC01CB267550ECE8A78B5C72AF9137985A4B846ED49D9F1B8EB8361FBFE8613F17291C843576A4FE66FBF55B9E92797BDF175FD56257DDCAF9395D25AB72B1A32D9B77573F2567G64B1FB92BF835A5AD0C69A34496494DB61E4AEBBCF6235C4A9603AA7737345D90957130913B90FF1346F0BB896A0672FD2EC4373E57A9711F0AC27B950750D1CE63706663463BA6E97C5403382208DA0G908FB093E0
	AE40C200D400D5G7937C3CD83F09820812095408B90FBBB0F051FF850F651EAC66360CC83C213DC989206BF4E6D0B9E93CF760D9E938B7A6837CC70E57B005738C7FC2E8FB797353353A70B587BF9DF3E40557B9AD51F8BB3AD334D6F7722EDBAE12D59BE37091E6927425CE65E4E77396DC7C24FA09FDCC45B323DF77C11EBB903AC861E88FC8FCB3B1D1D6B02E710CFB7046A53C0E3ECA574A3897C2CF12EBFA87F0D91777E7033C6387CD260B9596847643DCB65D5G3EAE014FFFC2EB687CAD83C1794BDBC7
	1B3FF58479018496FF4179AFA98C1D7F8A9A6B3150EF125047D2972A4FC717027AF4570DE60F1174C986997FEE3C33FD70E14BE8F5784204C85F431E9CF5DC7877B1B6EE959D1716B03C360C842B219FBB3FDC22B9A7FFCA7839E6AC45EF1D92FE2ED95F46F351EE2045CF61778B1B256BBB0F6D384EF12D57BC0E4D6BF51A9F53074DB7339D712B074DF722483DB9F7C05E3D03672E96BDFA8E127451F3D00F9EF9DF766A4377E53F9EF9DF3E2C1B77E5060B5151CF3BA0F7BAC43FF182772D405DC2F9A26E2C4B
	29F78B7CA42A97626BE4944871F7D03D7AA94EE1606EB2613EB5F009BA66D118F2372A71317769B4B6DD0A425A8F50BEAE8F24D5E10F9FD39DDDDE492477A38207FA3318FD6C8A3C9AB17F7B43D8CB055C759D0784307B83A75445EA578745C5F85DF7E04FC8E70617B3026F89D9C15E6B256D11F9D741C2AA403A4CE0ED0CFD549A64D310936463EC7D2EBA32311F2E8D7249DA9F64137A653569338B2FD20DEF1F498539545E0A4BA3D733DF2C92FEAF1F17054AAE823FCE3B0357BF37DDC05CEAE8675F4963BB
	260D08583B59743E316D461FA9767E9F48D8A3856714539EFC1FAC5DF859BF3A21B20C33F996BA160E15A87A8D617B7667F47D3742BA6F3E136F1377DD30723E421FCEA0AB6E23F8E4B2780E39300B0C605FA56D2A628CFAA77B7FA1B1271E85AEC9AB085527110D436F692BEE7B67A5C997EAB6E5124CFAE26B2679251ED87BB1D18570DCBD83B272CFFDFD04668202DF021CBA497A865230CE2C550A6A1301F11EE0DCCFB3CD6A45CF4AAD2F4C30E412AC604AB2EFBD494274894CB5A6448219170A4008E15A42
	4ECFC5D98DAC1E6F3604E0319F5E16E1664C51090DFEAFAD57495B0D5F54FFED5FDFD35BDCDFDDDB74F555D735BA59D06D57709BDA913D5800CEEFB5592B6C35976C358EBBA81D9BE245FF3C6750FFAC7CC5334FC2EC7564BDACEE1AE40D1E79E5A77E364178E178F132560BA271D3AF4C48C79FD61FBE20488DE4D367E97C1124CF171DD57C146B5349E67AFB377DF8C059DFA7793AF55C1DBEEBA0D99E7C89BB5948480A64695A4F2E96BE23E649282A24B627198974AD207AA21792412560713C8F371FD8B657
	C2B20F133794D5F29E17BA20302FA7EB75EECD76F503CB843CDFB0C6B97DB90675AC40EE0B18BECE36843F8D8B25FB83FF9D1F1B16FBBC934ABF0D6C27BD3116AB91BD1C555D128622E42DDE471F642B5ACEC19B73C77D23673EFC37B513E4981344554B8BF8E13DBAA537CB9900179A6A33F4DD8BF450D613BCBE7CF9A0E8D6942699A78981A537BFDDBA6501B7DA280060D01D3C4FCC89C377F7492AFBFFD5E7A764E141G6C8A6C3789765827A42C4F4640644DA7DED8538EFED0C3AC6E52D27A370C7EBD00FF
	4BA8260CE24A8E00F15349087F426981BEE76ABCA2F8F079CB0E438A6A7F326269FBDF2AC8DF8E33BBCE2C74E7F830887941DEDF7B5D6D32B6BEE94A36F5892993F10D3C68AA81647BEEC0040A55D86C9200BC0A4D8D732A8C0AFDFB18FBE4A1FA7330C532317203B6376398FCA9244DBF503F5B2E5996B86AFD46D0CCE1D7689430D05890F58AA44A94AC21D338B11A1685B595F281EC5F4758585A0DFFBE3EB1BA9F8E5519DB572613EC6377A37F1F352F8C98F74EFB2E1A3F2D2D9AD79875378529F79B7F0771
	6E70EFEB3D747F77157EB0455670C3FE56F85C0E1738516E0F1B5C1222681252DB65F162DD723181DD8E5EDFFA4FA0FF9F6FE390992D7F8C6A5DE39A733FD0CB8788955FE1F1AC95GG9CBAGGD0CB818294G94G88G88GDBF954AC955FE1F1AC95GG9CBAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE695GGGG
**end of data**/
}
}

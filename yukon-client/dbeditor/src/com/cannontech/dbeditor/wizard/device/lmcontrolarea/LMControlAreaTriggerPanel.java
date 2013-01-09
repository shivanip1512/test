package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;

public class LMControlAreaTriggerPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.common.gui.util.DataInputPanelListener, java.awt.event.ActionListener, java.awt.event.ItemListener 
{
	public static final int MAX_TRIGGER_COUNT = 2;

	private javax.swing.JButton ivjJButtonDeleteTrigger = null;
	private javax.swing.JButton ivjJButtonNewTrigger = null;
	private javax.swing.JComboBox ivjJComboBoxTrigger = null;
	private javax.swing.JLabel ivjJLabelTrigger = null;
	private javax.swing.JPanel ivjJPanelCurrTrig = null;
	private LMControlAreaTriggerModifyPanel ivjLMControlAreaTriggerModifyPanel = null;
	private javax.swing.JPanel ivjJPanelButtons = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMControlAreaTriggerPanel() {
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
	if (e.getSource() == getJButtonDeleteTrigger()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonNewTrigger()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxType.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerPanel.jComboBoxType_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonDeleteTrigger_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JComboBoxPoint.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerPanel.jComboBoxPoint_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonNewTrigger_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JComboBoxTrigger.item.itemStateChanged(java.awt.event.ItemEvent) --> LMControlAreaTriggerPanel.jComboBoxTrigger_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxTrigger_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonDeleteTrigger property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonDeleteTrigger() {
	if (ivjJButtonDeleteTrigger == null) {
		try {
			ivjJButtonDeleteTrigger = new javax.swing.JButton();
			ivjJButtonDeleteTrigger.setName("JButtonDeleteTrigger");
			ivjJButtonDeleteTrigger.setMnemonic('d');
			ivjJButtonDeleteTrigger.setText("Delete Trigger");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonDeleteTrigger;
}
/**
 * Return the JButtonNewTrigger property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonNewTrigger() {
	if (ivjJButtonNewTrigger == null) {
		try {
			ivjJButtonNewTrigger = new javax.swing.JButton();
			ivjJButtonNewTrigger.setName("JButtonNewTrigger");
			ivjJButtonNewTrigger.setMnemonic('n');
			ivjJButtonNewTrigger.setText("New Trigger...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonNewTrigger;
}
/**
 * Return the JComboBoxTrigger property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxTrigger() {
	if (ivjJComboBoxTrigger == null) {
		try {
			ivjJComboBoxTrigger = new javax.swing.JComboBox();
			ivjJComboBoxTrigger.setName("JComboBoxTrigger");
			ivjJComboBoxTrigger.setToolTipText("The trigger you want to view, modify or delete");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTrigger;
}
/**
 * Return the JLabelTrigger property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTrigger() {
	if (ivjJLabelTrigger == null) {
		try {
			ivjJLabelTrigger = new javax.swing.JLabel();
			ivjJLabelTrigger.setName("JLabelTrigger");
			ivjJLabelTrigger.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTrigger.setText("Trigger:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTrigger;
}
/**
 * Return the JPanelButtons property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelButtons() {
	if (ivjJPanelButtons == null) {
		try {
			ivjJPanelButtons = new javax.swing.JPanel();
			ivjJPanelButtons.setName("JPanelButtons");
			ivjJPanelButtons.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJButtonNewTrigger = new java.awt.GridBagConstraints();
			constraintsJButtonNewTrigger.gridx = 1; constraintsJButtonNewTrigger.gridy = 1;
			constraintsJButtonNewTrigger.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonNewTrigger.ipadx = 3;
			constraintsJButtonNewTrigger.insets = new java.awt.Insets(2, 4, 5, 6);
			getJPanelButtons().add(getJButtonNewTrigger(), constraintsJButtonNewTrigger);

			java.awt.GridBagConstraints constraintsJButtonDeleteTrigger = new java.awt.GridBagConstraints();
			constraintsJButtonDeleteTrigger.gridx = 2; constraintsJButtonDeleteTrigger.gridy = 1;
			constraintsJButtonDeleteTrigger.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJButtonDeleteTrigger.ipadx = 1;
			constraintsJButtonDeleteTrigger.insets = new java.awt.Insets(2, 6, 5, 69);
			getJPanelButtons().add(getJButtonDeleteTrigger(), constraintsJButtonDeleteTrigger);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelButtons;
}
/**
 * Return the JPanelCurrTrig property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelCurrTrig() {
	if (ivjJPanelCurrTrig == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Current Trigger");
			ivjJPanelCurrTrig = new javax.swing.JPanel();
			ivjJPanelCurrTrig.setName("JPanelCurrTrig");
			ivjJPanelCurrTrig.setBorder(ivjLocalBorder);
			ivjJPanelCurrTrig.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelTrigger = new java.awt.GridBagConstraints();
			constraintsJLabelTrigger.gridx = 1; constraintsJLabelTrigger.gridy = 1;
			constraintsJLabelTrigger.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelTrigger.ipadx = 10;
			constraintsJLabelTrigger.insets = new java.awt.Insets(2, 5, 4, 1);
			getJPanelCurrTrig().add(getJLabelTrigger(), constraintsJLabelTrigger);

			java.awt.GridBagConstraints constraintsJComboBoxTrigger = new java.awt.GridBagConstraints();
			constraintsJComboBoxTrigger.gridx = 2; constraintsJComboBoxTrigger.gridy = 1;
			constraintsJComboBoxTrigger.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxTrigger.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxTrigger.weightx = 1.0;
			constraintsJComboBoxTrigger.ipadx = 105;
			constraintsJComboBoxTrigger.insets = new java.awt.Insets(2, 1, 1, 20);
			getJPanelCurrTrig().add(getJComboBoxTrigger(), constraintsJComboBoxTrigger);

			java.awt.GridBagConstraints constraintsLMControlAreaTriggerModifyPanel = new java.awt.GridBagConstraints();
			constraintsLMControlAreaTriggerModifyPanel.gridx = 1; constraintsLMControlAreaTriggerModifyPanel.gridy = 2;
			constraintsLMControlAreaTriggerModifyPanel.gridwidth = 2;
			constraintsLMControlAreaTriggerModifyPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsLMControlAreaTriggerModifyPanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsLMControlAreaTriggerModifyPanel.weightx = 1.0;
			constraintsLMControlAreaTriggerModifyPanel.weighty = 1.0;
			constraintsLMControlAreaTriggerModifyPanel.ipadx = 295;
			constraintsLMControlAreaTriggerModifyPanel.ipady = 233;
			constraintsLMControlAreaTriggerModifyPanel.insets = new java.awt.Insets(2, 5, 3, 8);
			getJPanelCurrTrig().add(getLMControlAreaTriggerModifyPanel(), constraintsLMControlAreaTriggerModifyPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelCurrTrig;
}
/**
 * Return the LMControlAreaTriggerModifyPanel property value.
 * @return com.cannontech.dbeditor.wizard.device.lmcontrolarea.LMControlAreaTriggerModifyPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private LMControlAreaTriggerModifyPanel getLMControlAreaTriggerModifyPanel() {
	if (ivjLMControlAreaTriggerModifyPanel == null) {
		try {
			ivjLMControlAreaTriggerModifyPanel = new com.cannontech.dbeditor.wizard.device.lmcontrolarea.LMControlAreaTriggerModifyPanel();
			ivjLMControlAreaTriggerModifyPanel.setName("LMControlAreaTriggerModifyPanel");
			// user code begin {1}

			ivjLMControlAreaTriggerModifyPanel.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLMControlAreaTriggerModifyPanel;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	//this stores any changes that are made to the current trigger
	if( getJComboBoxTrigger().getSelectedItem() != null )
		getLMControlAreaTriggerModifyPanel().getValue( getJComboBoxTrigger().getSelectedItem() );

	com.cannontech.database.data.device.lm.LMControlArea controlArea = (com.cannontech.database.data.device.lm.LMControlArea)o;

	//just in case we are editing a LMControlArea with existing triggers
	controlArea.getLmControlAreaTriggerVector().removeAllElements();
	
	for( int i = 0; i < getJComboBoxTrigger().getItemCount(); i++ )
	{
		LMControlAreaTrigger trigger = (LMControlAreaTrigger)getJComboBoxTrigger().getItemAt(i);

		trigger.setTriggerNumber( new Integer(i+1) ); //trigers start at 1
		trigger.setDeviceID( controlArea.getPAObjectID() );
		
		controlArea.getLmControlAreaTriggerVector().addElement( trigger );
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
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getLMControlAreaTriggerModifyPanel().addDataInputPanelListener( this );

	// user code end
	getJButtonDeleteTrigger().addActionListener(this);
	getJButtonNewTrigger().addActionListener(this);
	getJComboBoxTrigger().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMControlAreaTriggerPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(338, 348);

		java.awt.GridBagConstraints constraintsJPanelCurrTrig = new java.awt.GridBagConstraints();
		constraintsJPanelCurrTrig.gridx = 1; constraintsJPanelCurrTrig.gridy = 1;
		constraintsJPanelCurrTrig.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelCurrTrig.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelCurrTrig.weightx = 1.0;
		constraintsJPanelCurrTrig.weighty = 1.0;
		constraintsJPanelCurrTrig.ipadx = -4;
		constraintsJPanelCurrTrig.ipady = -4;
		constraintsJPanelCurrTrig.insets = new java.awt.Insets(5, 5, 1, 9);
		add(getJPanelCurrTrig(), constraintsJPanelCurrTrig);

		java.awt.GridBagConstraints constraintsJPanelButtons = new java.awt.GridBagConstraints();
		constraintsJPanelButtons.gridx = 1; constraintsJPanelButtons.gridy = 2;
		constraintsJPanelButtons.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelButtons.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelButtons.ipadx = 3;
		constraintsJPanelButtons.ipady = 4;
		constraintsJPanelButtons.insets = new java.awt.Insets(1, 5, 6, 13);
		add(getJPanelButtons(), constraintsJPanelButtons);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//getJLabelNormalStateAndThreshold().setText( LMControlAreaTrigger.TYPE_THRESHOLD );
	//getJComboBoxNormalState().setVisible(false);
	//getJTextFieldThreshold().setVisible(true);
	//jComboBoxType_ActionPerformed(null);

	// user code end
}
/**
 * This method was created in VisualAge.
 */
public void inputUpdate(PropertyPanelEvent event)
{
	fireInputUpdate();
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJComboBoxTrigger().getSelectedItem() != null
		 && !getLMControlAreaTriggerModifyPanel().isInputValid() )
	{
		setErrorString( getLMControlAreaTriggerModifyPanel().getErrorString() );
		return false;
	}

	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 4:42:42 PM)
 * @return boolean
 * @param pointType int
 */
private static boolean isStatusPoint(int pointType) 
{
	return (pointType == com.cannontech.database.data.point.PointTypes.STATUS_POINT );
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 4:42:42 PM)
 * @return boolean
 * @param pointType int
 */
private static boolean isThresholdPoint(int pointType) 
{
	return (pointType == com.cannontech.database.data.point.PointTypes.ANALOG_POINT ||
			  pointType == com.cannontech.database.data.point.PointTypes.CALCULATED_POINT ||
			  pointType == com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT ||
			  pointType == com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT );
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxTrigger()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jButtonDeleteTrigger_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxTrigger().getSelectedItem() != null )
	{
		getJComboBoxTrigger().removeItemAt(
				getJComboBoxTrigger().getSelectedIndex() );

		fireInputUpdate();
		
		//we may need to hide the LMControlAreaTrigger panel
		getLMControlAreaTriggerModifyPanel().setVisible( getJComboBoxTrigger().getItemCount() > 0 );
	}
	else
		javax.swing.JOptionPane.showMessageDialog(
			this,
			"A trigger must be selected in the Trigger drop down box.",
			"Unable to Delete Trigger",
			javax.swing.JOptionPane.INFORMATION_MESSAGE );
}
/**
 * Comment
 */
public void jButtonNewTrigger_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//only allow the user to define at most MAX_TRIGGER_COUNT Triggers
	if( getJComboBoxTrigger().getItemCount() >= MAX_TRIGGER_COUNT )
	{
		javax.swing.JOptionPane.showMessageDialog(
			this, 
			"A control area is only allowed " + MAX_TRIGGER_COUNT + " or less triggers to be defined for it.",
			"Trigger Limit Exceeded",
			javax.swing.JOptionPane.INFORMATION_MESSAGE );

		return;
	}


	LMControlAreaTriggerModifyPanel p = new LMControlAreaTriggerModifyPanel();
	OkCancelDialog d = new OkCancelDialog(SwingUtil.getParentFrame(this), "Trigger Creation", true, p);

	
	d.setSize( (int)p.getSize().getWidth(), (int) p.getSize().getHeight() );	
	//d.pack();
	d.setLocationRelativeTo( this );
	d.show();

	if( d.getButtonPressed() == d.OK_PRESSED )
	{
		LMControlAreaTrigger t = (LMControlAreaTrigger)p.getValue(null);
		getJComboBoxTrigger().addItem( t );
		getJComboBoxTrigger().setSelectedItem( t );

		fireInputUpdate();

		//we may need to show the LMControlAreaTrigger panel
		getLMControlAreaTriggerModifyPanel().setVisible( getJComboBoxTrigger().getItemCount() > 0 );
	}	


	d.dispose();	
	return;
}
/**
 * Comment
 */
public void jComboBoxTrigger_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	if( itemEvent != null )
	{
		if( itemEvent.getStateChange() == itemEvent.DESELECTED && isInputValid() )		
			getLMControlAreaTriggerModifyPanel().getValue( itemEvent.getItem() );


		if( itemEvent.getStateChange() == itemEvent.SELECTED )
			getLMControlAreaTriggerModifyPanel().setValue( itemEvent.getItem() );


		if( getJComboBoxTrigger().getSelectedItem() != null )
			for( int i = 0; i < getJComboBoxTrigger().getItemCount(); i++ )
				((LMControlAreaTrigger)getJComboBoxTrigger().getItemAt(i)).clearNames();
	}

	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMControlAreaTriggerPanel aLMControlAreaTriggerPanel;
		aLMControlAreaTriggerPanel = new LMControlAreaTriggerPanel();
		frame.setContentPane(aLMControlAreaTriggerPanel);
		frame.setSize(aLMControlAreaTriggerPanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	com.cannontech.database.data.device.lm.LMControlArea controlArea = (com.cannontech.database.data.device.lm.LMControlArea)o;

	for( int i = 0; i < controlArea.getLmControlAreaTriggerVector().size(); i++ )
	{
		LMControlAreaTrigger trigger = (LMControlAreaTrigger)controlArea.getLmControlAreaTriggerVector().elementAt(i);
		getJComboBoxTrigger().addItem( trigger );
		
	}

	if( getJComboBoxTrigger().getItemCount() > 0 )
		getJComboBoxTrigger().setSelectedIndex( 0 );
		
	//we may need to show the LMControlAreaTrigger panel
	getLMControlAreaTriggerModifyPanel().setVisible( getJComboBoxTrigger().getItemCount() > 0 );
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getJComboBoxTrigger().requestFocus(); 
        } 
    });    
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G67F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD0D4C71A98D151E002ABC62326CA9312E2F75D0B49F93B6E062BB22CDBE7326462D6622E1518046438442BB5B96F625EEA16BD1B8186A47E904584B71A1028D1B78471A702888202868CC928C078C9B86041BC060119F943FBEFE010816F7B3ADF3FF98CB3A016D2754DFB7DF57757DFFFFF7DF57703E4FD38A4B54914C5C8D2AA517E8A128889ADA6447E40DBB60E8B91E34D4470779140BBE4E1
	E38ABCA320AEF7E92CB913BCF2B99D4AD1D06EDE9CEBFE8F5E77111FDFDEF98EDE020C9381F50F3E34DF99FF1C3486BECE9AAD4FAF5A894F5DG51004367969147FF38A8DB4347E9F88EC9A6C2B6F5E11BA70B8EEA38C2A85782C48344F431764F42F3879DF9DAD121562EE54E92326549E326DDC847C01343D9AB6B5B24BDF34843C9249B47FA137209FDA220EE8DGCEBE196C48388A4F625ADE6B45CE491157A9B81D12D395BB3B7386EC57845912E7917BED1DE21E5D5189F8D9328B32A8949555FCD5410A65
	D02C17EDD62BA8578ACE51CE08170EF761C2BA25CFC8A51453ED08CBAFC09ED6C1F9BD40E69D7FB44557403B8540EB6373AEB9D62C4D2BE968E1127887E7721EA0730E3431794658FC730EF43F15F6006A680BC65445D628BB5893EB4E872887D081A2815E273671F5435F61D95E21D6C88E0764ACF739AE59AD72D712C53443FBD19154E838EF45CEC9369042743F2DA8B7GBF63816B17F6E1327D18CC224FF0DBD8766DC3E443BFFEE9CA8BA0138D517926E5BDCC962BFBFCB2E1E317859DBB21E7342D0E9E3B
	1198475EB4DDCACE8DB0760AF5A7CCDBB5BABBFBFDE39777222E3DD45739701E24598743CF277890831E596DEE0A47F28454ED69653651341B73D2133210E41E2A89A80785316526A6BB6341E57767252290E9AF3633B153F4DE3E21789586BC63E59D65854BF1D0176F08B5B37B2AEDC6BF0E0472F200B5G71G44897686106F64B6B63F61648D5AD82DA02B175CB63BC514896BDB5B78AEBCC50FAA8B55CEC5951C1DE203A05B048E3B88910B35E9EEC45EBAC675EB25365F89E3FCA1BAC5D9D0ED1293288B8E
	D195E5053531B63E03ED401331365E6690E1G070BF03AD263BEF85A85C57D4EE591D4515702558FB422CE7A0586208884601D49E578FB14D7AD4C3F9B40234B613FA93DAFC59962850492C96AF53B6AF1A6E40D93655CC8E3C7B83CC7B9390C87FFC05CCEA956FCC462F4CEB46DB5502934F5A27B02BC48FD6C0E820C5505923719B71A384DD46EFA10642E3B9A50E6905AA5E6AB9E495FF742A9CD640B15338A1AA6694763C121B45A7F58981F5D52B1127D4ACC3E2F35202E164078B1G9BF499DB1AF2184DDE
	929411DB69B7B29818E871C91C71D9DD4857989CE3074E7FF7943F85E89EF4451AABDDDCF64F53F9A579713463964D130D31050E0174D37D6AEE4598CCC65FB7633AA4760BCE35D2F409CE8B3CA8DA1D500C73517C36CB527BD8A95E85FEDE810E2568F8C7B37A22202A322D432D0AF5222A5A1CD6DF3D1B0E2538BBDC3E90414772B6639C258170F33D7D14E67491D83B1DA2D5165EC52B1FCAFDD4340B8EE03AC2F2BBD5061F79BD5AF92FB8284508595FE3B254AF585D98EFD86E30DCF89B67A05B9CE0829832
	48DA977A2F73C236C1C6F640E18E94854F15AE6E0B830739AF9E3936142CEABBEA8A3416ACF83052145D47FCA2374F670B0C27119F82739ECAE30506D712A9AE21986DFC1C34C573031CD61A0B40D86DG1EBE6E8724856BF92C519C20C2325B19D8F5F93F5E12097A917B5CA204E88CEADF6AB25C5232572F2EC113A72BCFECC1F95A8D7130C9C6F9069572BCB182780986D85347E41AAEEB799EEDD7F31E370B02F20C6CEB37E1D4BB22671D4715D8F399C03B425AADD6D8BB265F34B64C93826506D637AD4F2D
	5AB06A8AAA98364B2DD2B1325CEFBD0D5F68E39B951E63B51722DCF354D8F3A1C02D4A6D60F73BF80E97593B0444BF70372039ED374AF88BD7FDF610B31567F8BDEDB8E69C54EDD0394E867E4F384E14AB0A54E923CB1CA66FCFDA0DF642F01F36E640334BA6ABEAB5B832076B66D4AB720BEB200656F202C7E8ECC3F9ED06F10F3BE34D2B5C08FF4813E5603D9DF0E1G4BG18BDE61FBF0DFECE0DA70FBDEAC539CB129D2265F80F50AF648983EA9E0DA9F965347AB23E1F603AF9E124821EBD175C2AAAB9ABE1
	8E30ACB3FBEEAB7723D6931C5C05938DDAEEF77F3EB20D5738FFD8C80A2F6FB3854A3543769CB22577B37967777B7460FD541C3C2975AFE9CCA66E73DCA6DEE833221F4B6459F301E432291F6FC53E59D5661B53576240CDCE0872F0220873C086406686B88F1FE698F9F081FE81C04CG57CBC49166E8B6D5F46461CF1D8A79CEC53760340ED5CBB554EBCAE17C57D4DFC47EA13B6B10AEC99E4EFE35BF9DFF7EF5C2483D36EE3DC0F37435833EFCA041703EF5E0EC1E4EF25727281F6D74305CB5D5472FE77EE7
	40B3B93515F2B91D053A859E1EEFEDEB47981E88656CC148E1812AG3A81428639FF7D32758C19A01F2D06F8A1BB85FB5D28850B6715EDA8F3D8E334325206FC50B506F0B97C2250FF2F92B5B8FA6E2B868365CC3F99536F833FF202DFBF2643BDF587F84CC3ADF9721481D8FF0B0A2EB22BC41D50B56E47DE639A17B848F2551CEB316633GAC8E3E6061F130669A0FF743D70C71AEE64FE9535BC162DD68B546DF54B51F1F656AFB5A87BAB006A7C09D99829D811C85E882D081968CF19D7D352DE4A29DD1532B
	17AAGD785EBCF05FF2E204BF4687C4D69E26618FE7E3AD8B9B43A9F135F5B85DCFEEB07F84EF8F24CFE7B46F8A899534F1F07F4EFA0FB305A5F8FEC8F5C4BB5FB48AC8BE88FC0732C175BC3C7C63AC63786F0EC2F33052E1521D0DE2C63FEA8C6DCB414E3F45CA79791978F652DDEAE0BE7CA91F7F098625BB0473DC8F19853B036B21CD85A24353B513C36F092ED5998A325BC4F3E51FE2913E84BEC59A4844F33F78801736CBDC260BCFB3F708E43838D63EE1B579F92464F430F8A017270C3D486CF3C3B0752
	D694D1214E05C912D6771A3A1F8448473F068D39A63FFD640D429DDA1B4B2AD4F1CA3745B7CFE48736C5C36E7532175BD87DB04F3D8E1D43989F8E65A800985DD6DE71787B4433D037D153FDE2G9BE031676F96DCC3B3C7E24D39G15GADG5E919EF39E93A6DC97AEFB46D997FE5691DC4FABBA826B79D79D18B7AA12DB6E947DF579F8C736DE670BEE386A2258D91BA7E9FFD5102D6298DB789D6D4F6A0275FF2EA3183DB01DBEFA5E2853533A5FE496786BE0B148F17908F6F6B7B2D6870CDED711115E6704
	474C2AB29EB3570FF07DEFB90D7A4F3C8E79A9C059F52EFFEB860E5DF4BD302F5B83605958EF97631A25CA123D5E662A87DD10106BF8D63E3B1768ED64E25CAF5A05C1492DC22E9802FC4CAC66B1F2B5345FG1082786C90631E631181245DA58B8E90DF1DCB6CA4BB27E05F120B3C2F875A0783AC830883589442F3370DAA56B3EF28F04BB2A6E01C1F631D3B74BA3614AB3A8EDE757A6B20FE0AEF6E5DD30265DAF328DFF9277867DA0BA9BEE20AFF2EF5789C4F352220AE7D8E9E5B3751BC418E65909DF7E990
	F111D00E52F18EAFDF83165F41FD6D34A5382F1C37847615D2CB6018F859B2FECC7C4A924C4699FF4768FACD26461A3326F21EDD94F79C4A05BAEEDD811FC765D4EE0B4965C69DC482BEFA2ACF6EB1D30369207B8C4AF46DD47FB3DC1562B7CE75BF43FD6BB4BF43A52131E6CBA867E9042E4DEB209C2F637EBD037339A91467EFCF7FE03C0B49DC78118969874A5F0A27B11E2B26795F45BCD24C4F493CD037E29A5F7F1CEA471CCE0F6F38C3A8AA7AC236D9AE89569A6AC94C479BCB52357E1B26F19F1FD602B4
	0FCC0FB5978354CF677CFABE617966320796121A10AF836E2F636BCF1B6C53991F9153FD7CC6C33E49466C3C48470C037A1499547EAF9A77D268E73FB4911287CFD67F6BD175E8BB38867A6A7FB42A9E79FDF0D47FE32546FA1451B12DBF1B5BEF0F7239A57EB61A587F69FC5033031A995A9D00762CEDB8CC0E4C7ACB9A5373DF29DFC5C3DD4C8C2EFB6F8857FD7C8C7D2E6F9C627243E24D35E19CF7BF4561D98B1E4BB05C09F337723C160FB7BA5F3AF4CB4770BF9FE5B1B31346A007D304C55C569992C6F5C2
	F1CF20AC43F85CED9E4433C295828DDB870EB9D0BB8934FD2837F21CF1D33531B82C06BE8807B5BD2E5746C852F93AD8B016276FA86E85005C191C27998523F9EAF37AF85AD4B09ECF499370D4BC1369995F192D7C2D9C472F82FC4BCCEEBF7368F9D48814A3F51CC960B635F2A63795A70DD36933E24DC7G4AE6F17CB50A374FE267F361BA1E1C39913DA3ADE3B9DD23BDD1BFB646019B9A43BFFFCFA6B7F62E4E74B3AFA3F08E71E7492727440C49584EF2185F6AD9CCCFD8379E5E1F5174447256A83C525256
	32EFFB0F4233C1685132534B961BAA49BCBD3D8A61771B0E9EC8746A852BAE177DA5B7F757FB78A67B35CC3A9FDB7B365135A0E196DB7B12F43C69A262B79B70ECBD3A33042FC709D057FEA7BF83DA5D07B957EAA84783EC85488C0FB567835404737CE3E1EFBE19A0572F17DCB51020588DA9851BDF77451B3B83FFBC0D477D953F3A1F2C1C3EADA95899CAFBB83379906D39317EB0453379B5AAB83F65D097873099A0FDF62C79A0405959FCFE2F746632792101142B2A5059CDCF0B79649AEC62008F0D89AB33
	6F2C6131796F2743BC072D1A4D725FF6F82A3359DE1D597C5C1109E213516E43216F1259BE1BDFB11B45099D3A1FA539F15D3D6AFF262A573F6F4E72550FBAC756626DA0BE0FD90EE932DFB2E04CFF8AB38A825E5BA02C1A2DFD4722BDE3E69CA69FD21A900F086F0F797DCCB751757B72CE2E5F03BFDDC4AA7F30AD69F010B1F65E45E8375C654BD9B676A426B15E6D8514F767B1ED0CB77B09BE6FD365281F8EC92689AE53CFD5B957CFA450DB8610G3075AEF6C64C7AADEB9B2B57DF3771FE19F7B33D9E07E7
	715D0CA726578342E4561CFA685BFDB753AB163D703EED7B766D880C0F76DE3A1F2991BAC43B3FCE6F93708E6A2A5FB9B42B7BFDA56AA050BD98782C2DEB105D063133721F1DB166DFE1F7B3B9AC03A7BB73FA2054F86635CA2BBF9881B182E02C2EB71786577F3B74CA0545056DA53E77740B3E77B9A537B267899CF3FFF2DB47E0B165293E60FB36E77B826F591E6B8B3CE70B6F9B7F1CEAE3DF707B627F6CB35E97331878AA3D372A827DD9G42G9683AC0F6071F0D87E1850F898A096DE36D871FC853FCED1
	79786DCA7039DA15093FE9F1A9F89F57A1C1F8F2A0FD0E6FD7D0DF2A6422C8069BD2D0C6E04D5DAAEB4A6F40953A3F963B981A2EF9F543FC4D0B0339254C61E7340B0679BE27F88E5FDBBDC15B2AD08E87081A93483649B8316D2EEBC4176F05A13ADFG9A6B67F0196ED5BFA5C1F291C0564A12CB1455C10625B7469A2D811ADBEF85BAD9BF027DB5C0AD0085408B908190853092E0AD40C600C4004C3910EB821C852882E8194B7978D77709F17850B622D44132E0C395EB79032B1CFF236582DA51F379FE7635
	C3C8EF7C7B63FA1BEA97AFE9E1D8BB43DAD94E4FB06367F29D3CC26F6533A3A136GD48130317F584675588E388511DC0FE75A399E57C6F29AC2BB4D3567451AF3G8A675DE80C5ADC4A74D8B7C208FE965B9564AC16C6E446C34299FE96D7BB0F4B640433B608CCA67D3D25A1E033711EAF655FDC064D63F37E1FA17A4DA5145783EC1E47F57D06A738DF7E49B331DF7EAFBD3392411F6A87DD5A99E872A02E0DD2D717A26A7E1FBE1829632A15DAC9316159A90B354D43B71797DBEF325F12C977E3F9F3498835
	1BFB7C7369AB941FF50FFFBE7D349EDBF220AE749EAE7332A1242947EA664A1A1C3E984238EBF302F884BBD882CA298E9ABA2D2C7ECEEFF0FD7D48BB313E96F9834751255E31F174A1EF60B83A4CEB0C238C7708976D5A8A3121C9EF77F39D572C639E23B49157E833285DBA7EF14A97622BC49C10637F0572B5C0F1CD9AAE4E00EB66E773DE34B918F237A4733E3F77625E3F4BE637EBEBA7EDE3F302896A72F80972E8F389863E93F4DC33EEB37D6593594C46FB825DB706CE584F7F7E11757BE75AAFE57E0D5D
	3FE5C3BBE60B8DB48E664E773751B60AAF1C6FEF239BDB390DD6C2DD74FCFE3E70DFEDB767DB5BEF32DFE17B4D7964BA9A4BF7AE701F6F0B941F3D40FF3E56F6BE5FA3D09736004F77BF4E98475F3400491D5C4B1ED9770E55836B97DFBA9E5F478374E37C75F8110F62FB7D79D6A83E6ADEFF3E9307B85F75D03772DE4EF785DD0B58FB0D61FD5D706D4F115F382DE330FB52D57BF0FFE3F35AEDCE3102FD3DCC78F95B33B4BFB83890F218057CEC2D07FE4BD585651605DCC6ADEDBE39080677B7DAEF3F0C525A
	EE7F984F377B6614E0F8F7345F7E31275F56B118CE49D954E9B76853236BF94D67745BF0A8AF56F11F1D63672A51BA6E4F257A77923ABD383CBE998D9A5ECD5E5BAF2F77EF6B98ECEF7F47F2DCE3D54C282FC8B4BFE76763491728EFAC0AB557AF626798D3DAD3F9DB4277A9F87B44E93DF7162E21C0065118CB659E8A7DA3F49AAFFDBE7A0C3D1406ADF65ED1F9EED2E76C0633DBA4328A46582F3DB3FA3B4BEE64DC0C1F0D45C17F78C53E7313AD707E0D0910E71278DC7BD3FDF3EDED45BD2DD39C4851BF4CC3
	799D7F58823918E47DB8B7A7BFB7675459933C6F47993E3EB11FCF2E6FE5BC2A8C30D79CEF5F510DCBGE17F437669F73FG21AFFB1D2402FED5321B9D5965E8DB82C0FC8D2CE8253C3CBC156455F70BB9AAC3648CCA6E1C81581D6728D2CE3FCD9C484DF150CDC40EA467D8A8A5C5A6FB7419D45314A93E7FCCD1723FBD10ECA2A955444CD248EA1202F9E0B5C925E9E2B5C90694526352D4006956E5G1352C7C6C80A06CD51EAFCD83E54E50D421A357674B2EFD4DB442209435C776085BF64403E1927EBEFC5
	B005DD2314711B251F3F9A77E20312CC4C5564G5AF41DA853B4DB346027B95A7F2DDCA1BBDC926CEB6BG8F73D368A3D5D1876DE28D496A7A963F5BD6D451D24E93F8C5A57B69371CA7F1AFFB32CAD03AD5CC3AAF541094A77E078D594B2A6D0253FA12E573972810124A5B290651F689703B0F720BD472F4AA7B9E5BF374232B89AB1309698A4930CB02650A50897B71EA32C36D16C525DB32DBBC2E52B2CAA8FDG4D86EDAB99483D2FCF9FA71B0757ABBA533D1E7B42F7ED5CF5853C41A3131334A53AF01E5E
	42D251AD48B014A8D72B789FC312FCC42F1B72017A49D3DF3C57E2A2C95AA4D1BF3BBDAE15183B84074DBE88AF1554742966762A402DACB8957CF497B84B71630CD76983644DFDA2FF5E0F5F7A1E8E606B2A128F19A8216BC92BA8B9CE16F7F5C1EA2093G398279C3BAF9EC136757E62FE74E7E2B6B36F7009DD412E4C7FEBE7DBDC5FFCF636FA90AB9C5B127CE03F0B713F17F7C6BBDFC4E54F8F4D7C27567DD818D2A4F3D7E447D4F97EF5E84333BC2D269172E2004CCDF2B2F5D0E8ED11E3EEC5E2137A3636F
	C49D3950D4BCC877DDC6FE67987BC1EC66D8C3C0F4162E8EAC3DC71B9E203F87A7BDB96C75816D1B2373F45423BBA8F77324D14E4CA3B3F2E5524E77CFDCB75EA1EC1D169D748E413514655121CBFD677B6C6CEBCE93D2DC85788DCB797E7A65464FC870BB3DBA07E0372B023D375CE9417B3DCB032A68BBA73D278969AB3C0DD6496A6EEBC23EFB8CFD7E9FD0CB878888601A08FA95GG2CBCGGD0CB818294G94G88G88G67F854AC88601A08FA95GG2CBCGG8CGGGGGGGGGGGGGGG
	GGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3495GGGG
**end of data**/
}
}

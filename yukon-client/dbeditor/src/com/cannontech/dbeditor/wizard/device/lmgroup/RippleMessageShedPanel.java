package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.common.util.CtiUtilities;

/**
 * Insert the type's description here.
 * Creation date: (7/16/2001 11:14:53 AM)
 * @author: 
 */
public class RippleMessageShedPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener 
{
	public static final String CONT_LATCH = "Continuous Latch";
	
	private com.cannontech.common.gui.util.SingleLine25BitTogglePanel ivjControlBitToggle = null;
	private com.cannontech.common.gui.util.SingleLine25BitTogglePanel ivjRestoreBitToggle = null;
	private com.cannontech.common.gui.util.SingleLine25BitTogglePanel ivjControlBitToggle1 = null;
	private com.cannontech.common.gui.util.SingleLine25BitTogglePanel ivjRestoreBitToggle1 = null;
	private javax.swing.JPanel ivjControlPanel = null;
	private javax.swing.JPanel ivjRestorePanel = null;
	private javax.swing.JLabel ivjShedTimeLabel = null;
	private javax.swing.JComboBox ivjShedTimeComboBox = null;
	private javax.swing.JPanel ivjShedTimePanel = null;
	private java.awt.GridLayout ivjControlBitToggleGridLayout = null;
	private java.awt.GridLayout ivjControlBitToggle1GridLayout = null;
/**
 * RippleAddressPanel constructor comment.
 */
public RippleMessageShedPanel() {
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
	if (e.getSource() == getShedTimeComboBox()) 
		connEtoC1(e);
	// user code begin {2}

	if( e.getSource() == getControlBitToggle() )
		fireInputUpdate();
	if( e.getSource() == getControlBitToggle1() )
		fireInputUpdate();
	if( e.getSource() == getRestoreBitToggle() )
		fireInputUpdate();
	if( e.getSource() == getRestoreBitToggle1() )
		fireInputUpdate();

	// user code end
}
/**
 * connEtoC1:  (ShedTimeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> RippleMessageShedPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * Return the DoubleLine50BitTogglePanel1 property value.
 * @return com.cannontech.common.gui.util.DoubleLine50BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.SingleLine25BitTogglePanel getControlBitToggle()
{
	if (ivjControlBitToggle == null)
	{
		try
		{
			ivjControlBitToggle = new com.cannontech.common.gui.util.SingleLine25BitTogglePanel();
			ivjControlBitToggle.setName("ControlBitToggle");
			ivjControlBitToggle.setLayout(getControlBitToggleGridLayout());
			ivjControlBitToggle.setMinimumSize(new java.awt.Dimension(947, 28));
			ivjControlBitToggle.setValue(0);
			// user code begin {1}
			javax.swing.JLabel[] labels = ivjControlBitToggle.getLabels();
			for (int i = 0; i < 25; i++)
			{
				labels[i].setText((new Integer(i+1)).toString());
				labels[i].setFont(new java.awt.Font("dialog", 0, 9));
			}

			// user code end
		}
		catch (java.lang.Throwable ivjExc)
		{
			// user code begin {2}

			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlBitToggle;
}
/**
 * Return the ControlBitToggle1 property value.
 * @return com.cannontech.common.gui.util.SingleLine25BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.SingleLine25BitTogglePanel getControlBitToggle1()
{
	if (ivjControlBitToggle1 == null)
	{
		try
		{
			ivjControlBitToggle1 = new com.cannontech.common.gui.util.SingleLine25BitTogglePanel();
			ivjControlBitToggle1.setName("ControlBitToggle1");
			ivjControlBitToggle1.setLayout(getControlBitToggle1GridLayout());
			ivjControlBitToggle1.setMinimumSize(new java.awt.Dimension(947, 28));
			ivjControlBitToggle1.setValue(0);
			// user code begin {1}
			javax.swing.JLabel[] labels = ivjControlBitToggle1.getLabels();
			for (int i = 0; i < 25; i++)
			{
				labels[i].setText((new Integer(i + 25 +1)).toString());
				labels[i].setFont(new java.awt.Font("dialog", 0, 9));
			}

			// user code end
		}
		catch (java.lang.Throwable ivjExc)
		{
			// user code begin {2}

			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlBitToggle1;
}
/**
 * Return the ControlBitToggle1GridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getControlBitToggle1GridLayout() {
	java.awt.GridLayout ivjControlBitToggle1GridLayout = null;
	try {
		/* Create part */
		ivjControlBitToggle1GridLayout = new java.awt.GridLayout();
		ivjControlBitToggle1GridLayout.setHgap(3);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjControlBitToggle1GridLayout;
}
/**
 * Return the ControlBitToggleGridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getControlBitToggleGridLayout() {
	java.awt.GridLayout ivjControlBitToggleGridLayout = null;
	try {
		/* Create part */
		ivjControlBitToggleGridLayout = new java.awt.GridLayout();
		ivjControlBitToggleGridLayout.setHgap(3);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjControlBitToggleGridLayout;
}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 2:20:26 PM)
 * @return com.cannontech.common.gui.util.TitleBorder
 */
public com.cannontech.common.gui.util.TitleBorder getControlBitToggleTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjControlBitToggleTitleBorder = null;
	try {
		/* Create part */
		ivjControlBitToggleTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjControlBitToggleTitleBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
		ivjControlBitToggleTitleBorder.setTitle("Control");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjControlBitToggleTitleBorder;
}
/**
 * Return the JPanel11 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getControlPanel() {
	if (ivjControlPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("sansserif", 1, 14));
			ivjLocalBorder1.setTitlePosition(2);
			ivjLocalBorder1.setTitleJustification(4);
			ivjLocalBorder1.setTitle("Control");
			ivjControlPanel = new javax.swing.JPanel();
			ivjControlPanel.setName("ControlPanel");
			ivjControlPanel.setBorder(ivjLocalBorder1);
			ivjControlPanel.setLayout(new java.awt.GridBagLayout());
			ivjControlPanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjControlPanel.setMinimumSize(new java.awt.Dimension(0, 0));

			java.awt.GridBagConstraints constraintsControlBitToggle = new java.awt.GridBagConstraints();
			constraintsControlBitToggle.gridx = 0; constraintsControlBitToggle.gridy = 0;
			constraintsControlBitToggle.gridwidth = 2;
			constraintsControlBitToggle.fill = java.awt.GridBagConstraints.BOTH;
			constraintsControlBitToggle.weightx = 1.0;
			constraintsControlBitToggle.weighty = 1.0;
			constraintsControlBitToggle.ipadx = -500;
			constraintsControlBitToggle.insets = new java.awt.Insets(0, 7, 0, 0);
			getControlPanel().add(getControlBitToggle(), constraintsControlBitToggle);

			java.awt.GridBagConstraints constraintsControlBitToggle1 = new java.awt.GridBagConstraints();
			constraintsControlBitToggle1.gridx = 0; constraintsControlBitToggle1.gridy = 2;
			constraintsControlBitToggle1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsControlBitToggle1.weightx = 1.0;
			constraintsControlBitToggle1.weighty = 1.0;
			constraintsControlBitToggle1.ipadx = -500;
			constraintsControlBitToggle1.insets = new java.awt.Insets(8, 7, 22, 0);
			getControlPanel().add(getControlBitToggle1(), constraintsControlBitToggle1);
			// user code begin {1}
			getControlPanel().setBorder(getControlBitToggleTitleBorder());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlPanel;
}
/**
 * Return the DoubleLine50BitTogglePanel2 property value.
 * @return com.cannontech.common.gui.util.DoubleLine50BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.SingleLine25BitTogglePanel getRestoreBitToggle()
{
	if (ivjRestoreBitToggle == null)
	{
		try
		{
			ivjRestoreBitToggle = new com.cannontech.common.gui.util.SingleLine25BitTogglePanel();
			ivjRestoreBitToggle.setName("RestoreBitToggle");
			ivjRestoreBitToggle.setMinimumSize(new java.awt.Dimension(947, 28));
			ivjRestoreBitToggle.setValue(0);
			// user code begin {1}
			javax.swing.JLabel[] labels = ivjRestoreBitToggle.getLabels();
			for (int i = 0; i < 25; i++)
			{
				labels[i].setText((new Integer(i + 1)).toString());
				labels[i].setFont(new java.awt.Font("dialog", 0, 9));
			}

			// user code end
		}
		catch (java.lang.Throwable ivjExc)
		{
			// user code begin {2}
			

			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRestoreBitToggle;
}
/**
 * Return the RestoreBitToggle1 property value.
 * @return com.cannontech.common.gui.util.SingleLine25BitTogglePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.SingleLine25BitTogglePanel getRestoreBitToggle1() {
	if (ivjRestoreBitToggle1 == null) {
		try {
			ivjRestoreBitToggle1 = new com.cannontech.common.gui.util.SingleLine25BitTogglePanel();
			ivjRestoreBitToggle1.setName("RestoreBitToggle1");
			ivjRestoreBitToggle1.setMinimumSize(new java.awt.Dimension(947, 28));
			ivjRestoreBitToggle1.setValue(0);
			// user code begin {1}
			javax.swing.JLabel[] labels = ivjRestoreBitToggle1.getLabels();
			for (int i=0; i<25; i++) {
				labels[i].setText((new Integer (i + 25 + 1)).toString());
				labels[i].setFont(new java.awt.Font("dialog", 0, 9));
			}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}

			
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRestoreBitToggle1;
}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 2:20:26 PM)
 * @return com.cannontech.common.gui.util.TitleBorder
 */
public com.cannontech.common.gui.util.TitleBorder getRestorelBitToggleTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjRestoreBitToggleTitleBorder = null;
	try {
		/* Create part */
		ivjRestoreBitToggleTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjRestoreBitToggleTitleBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
		ivjRestoreBitToggleTitleBorder.setTitle("Restore");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjRestoreBitToggleTitleBorder;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRestorePanel() {
	if (ivjRestorePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("sansserif", 1, 14));
			ivjLocalBorder.setTitlePosition(2);
			ivjLocalBorder.setTitleJustification(4);
			ivjLocalBorder.setTitle("Restore");
			ivjRestorePanel = new javax.swing.JPanel();
			ivjRestorePanel.setName("RestorePanel");
			ivjRestorePanel.setBorder(ivjLocalBorder);
			ivjRestorePanel.setLayout(new java.awt.GridBagLayout());
			ivjRestorePanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjRestorePanel.setMinimumSize(new java.awt.Dimension(0, 0));

			java.awt.GridBagConstraints constraintsRestoreBitToggle = new java.awt.GridBagConstraints();
			constraintsRestoreBitToggle.gridx = 0; constraintsRestoreBitToggle.gridy = 0;
			constraintsRestoreBitToggle.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRestoreBitToggle.weightx = 1.0;
			constraintsRestoreBitToggle.weighty = 1.0;
			constraintsRestoreBitToggle.ipadx = -500;
			constraintsRestoreBitToggle.insets = new java.awt.Insets(0, 7, 0, 0);
			getRestorePanel().add(getRestoreBitToggle(), constraintsRestoreBitToggle);

			java.awt.GridBagConstraints constraintsRestoreBitToggle1 = new java.awt.GridBagConstraints();
			constraintsRestoreBitToggle1.gridx = 0; constraintsRestoreBitToggle1.gridy = 1;
			constraintsRestoreBitToggle1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRestoreBitToggle1.weightx = 1.0;
			constraintsRestoreBitToggle1.weighty = 1.0;
			constraintsRestoreBitToggle1.ipadx = -500;
			constraintsRestoreBitToggle1.insets = new java.awt.Insets(8, 7, 22, 0);
			getRestorePanel().add(getRestoreBitToggle1(), constraintsRestoreBitToggle1);
			// user code begin {1}
			getRestorePanel().setBorder(getRestorelBitToggleTitleBorder());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRestorePanel;
}
/**
 * Return the ScanRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getShedTimeComboBox() {
	if (ivjShedTimeComboBox == null) {
		try {
			javax.swing.plaf.metal.MetalComboBoxEditor.UIResource ivjLocalEditor;
			ivjLocalEditor = new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource();
			ivjLocalEditor.setItem("7.5");
			javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource ivjLocalRenderer;
			ivjLocalRenderer = new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource();
			ivjLocalRenderer.setName("LocalRenderer");
			ivjLocalRenderer.setText("Never");
			ivjLocalRenderer.setMaximumSize(new java.awt.Dimension(35, 16));
			ivjLocalRenderer.setMinimumSize(new java.awt.Dimension(35, 16));
			ivjLocalRenderer.setForeground(new java.awt.Color(0, 0, 0));
			ivjShedTimeComboBox = new javax.swing.JComboBox();
			ivjShedTimeComboBox.setName("ShedTimeComboBox");
			ivjShedTimeComboBox.setEditor(ivjLocalEditor);
			ivjShedTimeComboBox.setRenderer(ivjLocalRenderer);
			ivjShedTimeComboBox.setSelectedIndex(-1);
			ivjShedTimeComboBox.setMinimumSize(new java.awt.Dimension(61, 23));
			// user code begin {1}

			ivjShedTimeComboBox.addItem( "7.5 minute" );
			ivjShedTimeComboBox.addItem( "15  minute" );
			ivjShedTimeComboBox.addItem( "30  minute" );
			ivjShedTimeComboBox.addItem( "60  minute" );
			ivjShedTimeComboBox.addItem( CONT_LATCH );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShedTimeComboBox;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getShedTimeLabel() {
	if (ivjShedTimeLabel == null) {
		try {
			ivjShedTimeLabel = new javax.swing.JLabel();
			ivjShedTimeLabel.setName("ShedTimeLabel");
			ivjShedTimeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjShedTimeLabel.setText("Shed Time:");
			ivjShedTimeLabel.setMaximumSize(new java.awt.Dimension(70, 19));
			ivjShedTimeLabel.setMinimumSize(new java.awt.Dimension(70, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShedTimeLabel;
}
/**
 * Return the ScanRatePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getShedTimePanel() {
	if (ivjShedTimePanel == null) {
		try {
			ivjShedTimePanel = new javax.swing.JPanel();
			ivjShedTimePanel.setName("ShedTimePanel");
			ivjShedTimePanel.setLayout(new java.awt.GridBagLayout());
			ivjShedTimePanel.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjShedTimePanel.setMinimumSize(new java.awt.Dimension(438, 31));

			java.awt.GridBagConstraints constraintsShedTimeComboBox = new java.awt.GridBagConstraints();
			constraintsShedTimeComboBox.gridx = 1; constraintsShedTimeComboBox.gridy = 0;
			constraintsShedTimeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsShedTimeComboBox.weightx = 1.0;
			constraintsShedTimeComboBox.insets = new java.awt.Insets(4, 10, 4, 160);
			getShedTimePanel().add(getShedTimeComboBox(), constraintsShedTimeComboBox);

			java.awt.GridBagConstraints constraintsShedTimeLabel = new java.awt.GridBagConstraints();
			constraintsShedTimeLabel.gridx = 0; constraintsShedTimeLabel.gridy = 0;
			constraintsShedTimeLabel.ipadx = 10;
			constraintsShedTimeLabel.insets = new java.awt.Insets(4, 8, 4, 0);
			getShedTimePanel().add(getShedTimeLabel(), constraintsShedTimeLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShedTimePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 11:15:48 AM)
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object o)
{
	com.cannontech.database.data.device.lm.LMGroupRipple group = null;
	
	if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		group = (com.cannontech.database.data.device.lm.LMGroupRipple)
					((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	else
		group = (com.cannontech.database.data.device.lm.LMGroupRipple) o;

	
	StringBuffer controlBuffer = new StringBuffer();
	StringBuffer restoreBuffer = new StringBuffer();
	javax.swing.JToggleButton[] controlToggleButtons = null;
	javax.swing.JToggleButton[] restoreToggleButtons = null;

	//first 25 bits are appended to respective strings
	controlToggleButtons = getControlBitToggle().getToggleButtons();
	restoreToggleButtons = getRestoreBitToggle().getToggleButtons();

	for (int i = 0; i < 25; i++)
	{
		if (controlToggleButtons[i].isSelected())
		{
			controlBuffer.append("1");
		}
		else
			controlBuffer.append("0");
		if (restoreToggleButtons[i].isSelected())
		{
			restoreBuffer.append("1");
		}
		else
			restoreBuffer.append("0");

	}
	//second 25 bits are appended to respective strings

	controlToggleButtons = getControlBitToggle1().getToggleButtons();
	restoreToggleButtons = getRestoreBitToggle1().getToggleButtons();

	for (int i = 0; i < 25; i++)
	{
		if (controlToggleButtons[i].isSelected())
		{
			controlBuffer.append("1");
		}
		else
			controlBuffer.append("0");
		if (restoreToggleButtons[i].isSelected())
		{
			restoreBuffer.append("1");
		}
		else
			restoreBuffer.append("0");

	}

	//shed time is 0(Infinite) if the selected shed is the a string
	Integer shedTime = new Integer(0);
	String val = getShedTimeComboBox().getSelectedItem().toString();
		
	if( !CONT_LATCH.equalsIgnoreCase(val) )
		shedTime = CtiUtilities.getIntervalSecondsValueFromDecimal( 
							getShedTimeComboBox().getSelectedItem().toString() );
		
	group.getLmGroupRipple().setControl(controlBuffer.toString());
	group.getLmGroupRipple().setRestore(restoreBuffer.toString());
	group.getLmGroupRipple().setShedTime(shedTime);
	
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

	getControlBitToggle().addActionListener(this);
	getControlBitToggle1().addActionListener(this);
	getRestoreBitToggle().addActionListener(this);
	getRestoreBitToggle1().addActionListener(this);
	
	// user code end
	getShedTimeComboBox().addActionListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 11:22:40 AM)
 */
public void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("RippleMessageShedPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(420, 300);

		java.awt.GridBagConstraints constraintsRestorePanel = new java.awt.GridBagConstraints();
		constraintsRestorePanel.gridx = 0; constraintsRestorePanel.gridy = 2;
		constraintsRestorePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsRestorePanel.weightx = 1.0;
		constraintsRestorePanel.weighty = 1.0;
		constraintsRestorePanel.ipadx = 249;
		constraintsRestorePanel.ipady = 272;
		constraintsRestorePanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getRestorePanel(), constraintsRestorePanel);

		java.awt.GridBagConstraints constraintsControlPanel = new java.awt.GridBagConstraints();
		constraintsControlPanel.gridx = 0; constraintsControlPanel.gridy = 1;
		constraintsControlPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsControlPanel.weightx = 1.0;
		constraintsControlPanel.weighty = 1.0;
		constraintsControlPanel.ipadx = 249;
		constraintsControlPanel.ipady = 272;
		constraintsControlPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getControlPanel(), constraintsControlPanel);

		java.awt.GridBagConstraints constraintsShedTimePanel = new java.awt.GridBagConstraints();
		constraintsShedTimePanel.gridx = 0; constraintsShedTimePanel.gridy = 0;
		constraintsShedTimePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsShedTimePanel.weightx = 1.0;
		constraintsShedTimePanel.weighty = 1.0;
		constraintsShedTimePanel.ipadx = 249;
		constraintsShedTimePanel.ipady = 200;
		constraintsShedTimePanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getShedTimePanel(), constraintsShedTimePanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
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
		RippleMessageShedPanel aRippleMessageShedPanel;
		aRippleMessageShedPanel = new RippleMessageShedPanel();
		frame.setContentPane(aRippleMessageShedPanel);
		frame.setSize(aRippleMessageShedPanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (7/16/2001 11:15:25 AM)
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	if (val instanceof com.cannontech.database.data.device.lm.LMGroupRipple)
	{
		Integer shedTimeSec = ((com.cannontech.database.data.device.lm.LMGroupRipple) val).getLmGroupRipple().getShedTime();
		String control = ((com.cannontech.database.data.device.lm.LMGroupRipple) val).getLmGroupRipple().getControl();
		String restore = ((com.cannontech.database.data.device.lm.LMGroupRipple) val).getLmGroupRipple().getRestore();


		if(shedTimeSec.intValue() == 0)
			getShedTimeComboBox().setSelectedItem(CONT_LATCH);
	  	
		else
			CtiUtilities.setIntervalComboBoxSelectedItem(
				getShedTimeComboBox(), shedTimeSec.intValue() );

		//set the Control Bits
		javax.swing.JToggleButton controlToggleButtons[]= getControlBitToggle().getToggleButtons();
		for (int i = 0; i < 25; i++)
		{
			if (control.charAt(i) == '1')
				controlToggleButtons[i].setSelected(true);
		}
		
		javax.swing.JToggleButton controlToggleButtons1[] = getControlBitToggle1().getToggleButtons();
		for (int i = 0; i < 25; i++)
		{
			if (control.charAt(i + 25) == '1')
				controlToggleButtons1[i].setSelected(true);

		}

		//set the Restore bits
		javax.swing.JToggleButton restoreToggleButtons[] = getRestoreBitToggle().getToggleButtons();
		for (int i = 0; i < 25; i++)
		{
			if (restore.charAt(i) == '1')
				restoreToggleButtons[i].setSelected(true);

		}
		javax.swing.JToggleButton restoreToggleButtons1[] = getRestoreBitToggle1().getToggleButtons();
		for (int i = 0; i < 25; i++)
		{
			if (restore.charAt(i + 25) == '1')
				restoreToggleButtons1[i].setSelected(true);

		}
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GACF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8FDCD4E536D4D4D4242658A21911E2D9127FE2B75F466EE3D9371F356C66FBDB3B6EAEEF6335EE6B6E230DCD76BDABF63D8C08181AA8142322916986CA087FE29C7F840A88C5C5C0060660B0F319999DE626BB97997E286FFC5FF74FF7AF438C089B4D6FF73877BB5F3FF34EF73EF34E7767A2643D9D39B2DD17A78869AB8565D71EAE88F127856169620C85088B926333844D6F9D008D42D39FE642
	F39C64599E0F4F4A919ECB4F06F49424D39F0D4F5A846F39C2ECFA55277092201FFC483BBBF45C04217BB978A576330A268B5FDB8B4FB7GE6G0EBCD3C5523F71BD3D02CFD0708869BAC1D852C14AFC71DE3E02B3C03A9EA092A0260315CF0067EA5A734DC603D22E652EC8A1752977F46BC8BB1AB691CA2C2CEE3D72AC90E627AF37123E2ED0BAC91D90C847819066B3040FBD1570BCD2FF51FA4C6C6AACB51B1CCE17D3964D3652EEFB2FC93214DA44CBF633D86A6834CA2EAE375158E0F73B9D62A72247E332
	0A0DB651D2EFF20A8EE8C33611FE1EF8AF1B368BEDC2DA30935C1D07C85F73A13D88E0A947B799893E8A5EDBGE4BB72FB743DA38ABF4DFD330518DFB9B3568760B7544E780C312B7C06F53D3AEAB39D1B2EE6B286A91017F7A1BE2B88208E4081908A30156AC47F173B60D959AE1FF0F5F63A1C15EE77F107C57A44E5919D70EEB4C20E02EB904DAE49A288EC5C035FAB71C34FD0406A0D9FF1BDA613997BD187229B9E90929F7DD8374A0FCC92E7146922AEB0D944DED0E5427A7EE9403E93AF8C545101FDFFA9
	E87B0E3A45153152CF5FB14F966BD214F6B2AF2AFD9F38C846BA1D0E758EF8CFD774036157D03CD703E77ABA016A8CC907C0DE72C5540D66B710163A4C88A1E7EF1DDFB90447D76A6A9D0C861B43171605344FB007AFAD7FC971D18EDFDA4E9A1116984853F746E7B17D3AF2164C5FFEC807834C81D8883094A05389651C28E3BF79F24FB56AD83DC9120FF7599D96D192D85DAB4DEF41D3744A1229566911CDCE33F85AA459CD6D8E51A3A8E5EEEDA6B2E89FD0AF146A3E997AB8A5BAC549A45BDDCEE85D54A94A
	2264E1E5A6B6EFA0E5A41166B659BBC5682053ADE03B77B6674253E17248DF38ADA6D9D4CB307C694DE4CCAE19CEC30B50G3CB3394C7C0A484BG7C57831C67F2A8B8C35A7BD81440E0980DF5AE5745AEF79361C41863A4F2FE1E5A8E995EC31DA86387DB88AE9552EBDD584E17E7B6EA5A29321B8979A629876758AAA3315185AE54193FB4234ED42D1BAE14BC7B11DF1D2179AE26ABADAE5F39330136C968E269C9071A7D54AFF0791FC7A3855647BC3FFDE4FC836D3F41476A4DD6B2D641C0F7A4C09C177178
	B385CCE70F1BBCA2EA69E792A818E8D1A54E687CB915CDBA27FBB1CDAFF6212C4E9B1B83487B1B13D58F6D631B120DAFB019041CA5FEC83CA4BA65AA51ADBAAD7062D172461EA53A264C538E972FB311625DB02FCF40C472F07CBDE7495CB34932E4EF6F1245C6D1166DCE2B1ABF0D766569EAF72BA6817BFA78AC313F2EEE18573C7C6C33E4CE18DDCE27C88707D7D1726752B9A9BA44CEA07A042B4BA9B37CE3DF11313AA876A8B661712FC850F3496468A276054588F3CCEB888F123D93061C18A84162A67375
	959E639090GC7E29D83BC6B5CB8773A5F4539372377FEA12EED274E1F6F881FDE250BF633399367D6679E23A93A45BF6D7328EDD0709EC6EBC48E31170102942673E75AC8FBC950CFAA405A2FD167CB4F11FC34AB0A321FF0B99CCC24DC56DBDBF3485808DFF709E00E09817B184B2F30F523CF5EE9C516ACFFD7AB1125C3E37BE23FA632DCFD98E3419D10BE825072B51367642F19BC98FF2B5A088F77972FD93B6C25DD321DD8C4138CCA686E12A957ACAEB397E5ABEDE4CB98FF1D5ECD64D08169E6G3704E3
	769F6BB07E8A3B98A9A4CD5B92B05E8C1198ED5112BAE685B7E17CB56DBC69B3997252A4147174D62D8F2874F8DCE6BBF5BF0AFC5A5A34E34AF01636576159E117BCF2ADCCBAAF4A72C29B211778A7852D786BFD0757AA3C66F86233EAG12BC84AF7465E9E88F825C8C0084008C5A5E24437B481C2403DD4A9E7522546112BAC5CB518553A5D32929DBAE25733F3412E61FA46F45B8B68B0AB7B2BAC5AAFD66B40B0ACFE39CB5B537C269BBE1D604F064EA2E4EDFDC9B32FE1B2EC8D662F8D915EB7F3C2C0C6456
	57D6B1FBE7AB720D65A64B2A6F19A38F76C3AC46F90A0E79821945B82BB87E17941F2841B3D936D0BCC927C2DEF3977A65FD6DE46E47C2FAB1C08AC076A5083181EAAE61D83F5C36DF98A66E29855D151CA6C763GC3270C6F0C73994CB6A9696873AB51B689E8778D07FD65E039B41077A01FB42BD7BD28DE29CFBA4A279E9BE32BE3AB4EBF22915ED2CFB758ED2371A360315DF55C65D5EC63A7862DED0C3D44E21AA4F8268230B9D95EC7F41D4C2D355DB877AED7EB67DE6C7AFD3AB581661E3B1B51B72EDB55
	11923E76C9B493FB3288721281B23D71D985GD5G6781023DB8C69F180E8EB7C6D4491BDCA7GD783F66B042F0FA17AC663372B5446C4C15B7381986EA74584178869D4AF46830B28CE69FB62334AGEAFA901FC87196C80774303949706D3BB18EA2F5067355E557D006C00DD20E7551477B381698E265B5144136557861F0FB607861F83B7F7821265DFF7C70E97B860607B6342B864CBF5DBEF4FC515A6EAF3E58C679FF722D7534ED0FC7745041A78EC5497B237C2660A74E789496960A6CF285576155CA19
	13326B44DEBEF716956B7D4F9D0D1F3A59C07405540F6EC1BFD55BCB74E2892453G72FAD1AF0E74794647A510D7514B4635C9F95AFA075707FEA8B34E279C1BC7E611780CA8480B83D88C108210530773A735FDD89BF752BB040DFB419CD8A7DEB47B5709E5E6E2B3BC2EAE49AC7A0E7B2BE6BD4FD3E7EA2DACF612A1E2E5B2AC243EEC122C62A03DF95D2267F9016A67DA8269969B7F3B0BC88C69B075383AE470E7FDE48C9FA9C2FBD886691ABEA6EBF79F330BCCBF66F693DB57810BD4E82E51AD1A85CBBF29
	1BD80CF597C179A5G19005F81D0550F3E9BECA8C0832CCFDD12C875C90B87CEE449659800A7B2A021A1D955629E96335D6BCB7C596EE868AB8E0015B9FD4CDF1945101F5C0F7AF93B415747E4DED6F5CC8F6FA5G959A9C7345BF207A5EF4197962159C3F1062ED9ABC73511F75E23C538FF9C917517636D27FD2F5857C76954439AF93DCBF2447F1DC49E1020B0174A20E7BFB8541AD01F49A471D2CC4DB3E76AA62DE24FD34C0DA3E0AF3622298D827A5513FCEFBC57FF66E0AB8341D3BC58C240BCCB64F9671
	FDCF20AF6EAA6A5A5E7379DADBC4A2BB237114E4379CB7D96B1866D25B33311854DF8E75F202A03E89C2BF6D7DA77A696887A2043A200F7D4648C9CD7BF495C1ECFCADC12A1F1E81FE1A593B733B3215FA11103FB80860466C524629C4DF4F68D8FC4272638664937EA18455643FBCA01F50DB1B214D5F31DB1B3FD2D31F7176531D48DB5ACFE7880E6F9E8A987F975C40F82AD21E75275F95F65C7E1A425B2992429B19537DB720ADFF1B6EB744C0BA81E0498D988B0DB906E553F8592EFAD276400D301E84305C
	08BAF73F81630CFE0EFB7E9041C5C2FABE472D6BC55DCC60381D865461249BF11E7E1816AB3B897A3989F1BF27B8328E33F15C973D7E76E38281E90B47A4B78D6F03D89F4E9175210DC32EA5EEE1362F2B0F58E751E287DB0976B9348F6DD988509A893094A06DA63696E575EE2536C3A293DA92A5D6EF4AE52C17978C768C408B909A2C2DF762B075D7A2D9C70B96DC2B458453758535BDAB6097950C6573AA57EA4B036F493C195A29CA32D7319C4A7553F4CBE555B03C62BEAE019A2853827026A2B7774D4C0E
	26F339A4D8087E017331F7F6F5B65AFBC50546377A890D8B207C221BD197FEBBE87DF5ED637B279157E3367F96EA0752B4B4B37CD80A4F3EC545B33B27AFC63B1787F95EDBF05DD6EBA56315886965GB93702CD832881305C0A76BB3F23CC98A626E9F2396B4040BBB4A61971A76E3A3E730D9F2DC21B93737D29427CDBDEC98F346E715E4A66D23872DC5A742EA6DECB34917E96C25E92006C90D877G9CG288BC17EAC9DA50C3F13B085AAE559E436515D86E46E34DD6CD651A490E13C5515E8F9BB8F6D49A1
	6A3C8E017749GF3C2FC7D7984EA739604787A73A9943FB844571F6FECC5FF1E8CF97537214ECDB8266DBF86708BEFD37BCA047794004C5BFD7B7F9335237A5BFD7BCF2578125BFD7B6F2B477EABA0AF8A004D7527781AB67B8EB506AB38032D55589CBA39154EA11357E78E2DB112B9E40032D5G8C77813531C1108E3D8367749143D0F3DABBDF894C07FA89F730B9CD52C9703E8C8A5C150EF3DA9048DE9D46F938C877978356195AE98C48760A569B35BEB2D961B1A794668D15D1CC9F1E896DGDCCBA84695
	D55B8E8B0357B825EE07292334D314CD8E23719372C0CACED2AB3C630BDA88DDE9502E1C23267561B9EAD8A87A439F7413BE93A03D84A00D537244E524A56FCE5073BB1116793B0F8624255D64311B0D4663640134B4A816DDCB8D1E6B3EF18557E541F7A2BDBD74DCAC96524FGA45F09BA70CA8504AB427BFBF1EEC7F6EB4F2F8D2F177B5D83A610B9066914DEF9465E7A2EB20E4749B135525ED1D15BDED9DF3AAEA3C0FB75CABB67B56D3D2D0CFD36003F148F876384E15C98F27EBF452D767B5FEB51C6654F
	3CC7287A55AB696F86687799253F9DE35518EF690534D52C4DE6923EAAFC3DFCE8BBBC1DE2F7815FE02C82E5F4128335228F7616946C5BDF0C3EA31404E94A7BC44D7B766211448CA38556C7792876416CF203B5703A610C557F3A214D6AFF5DA0DA075E7F683486BEDF112D5A739566D70ABC449E3440F8FA81A6834C81D8B096FD0A4ED6A8D01F62471F1C34D849DA1C1C5E4A587F9FEC01F9CD368DFF667BB79B5993EFF749322B13340F7857ECE43CE4171BA299CE309399C9F62BCDE6C595FC161D683BC36C
	20E8969F1BE94C1E84FC250E65EB3C4D98EF6B47A12EF4AB465BC7B86E3B960C376BB94E160F7136E59CBFBBED256BCDC8AF6238A80ACB01F44EDD085B5D7A2F689CE9BFD01E5AEF553F5447D0B1B751A1A603070F21CC4B6EC239AC5C02B2ED6238273723CC3D9C777DD3A853D00EBB5F03B20D6432FA291E4A8F52EB47A36ED50AAB01F49D475DD0BF92FEC9DB244D11590A08917531F298B97A8273F9AF9BB03EF5G0DD16351A76D2177C692A11D8A30F6C2FC9633390BE066214D7DC7CF299050666E5BFB20
	7C00E17F87874A41767277E274874B3A54F636985E8E5C4E017D0612027DFB0B2F515F24FC485AFF2BDB60B2D95691E03F1406AA4CAFD79BF15D3CE3820E71777B060B41DBA678BB93D9B8EC3D6DFE47BB3D5F371EF74250BA43624F3DB4EE891D603B26B1D2FC78845FB54D0F7BF14C23A0AFBB8C79CD38FCFD7C7EE658FA7E794D3D622F1EA30C7591844FB00099E13EF1780BACDE8A736577FFA9FEE118AF3FD70D486FB310E71808EB389FF713B11F8C697993517FFC473E9B1E27CD9714559A0BAB298782E4
	3D64F20B125C433074C4DA71EBBF74523B9A3C1DC4FB61906D10B320C6F029CEAB46C4CFFF250D45F2A25E51A18F3E72C91E486431FC229A93B19A8E7412183F64EED8D3GB8G0281428166GACGD88C108C10791D782CFC00B200AA00E6G9BC0BF40386FA08FFFF1978F41033249C79D32E43293172C7843B2CFE01FDC6E995EA7D7F86859B57852269E3732D7FE52C376DADC9D9D9E117B7E0FBCB99CD76B29F7F96CE40F1D59145E92D23E5DA551CDCE366E3A0B6FC7C7810F31G29G596160D3FBF0ED76
	5696ACD3902E2E4D8CCA99365EBAC7FDF6BD60DA429137047A6CF0C8C705639A6CEA7EF0EBB03459849220DED2383A86CB8DE7EB30F7F48357E04A5E7CF13B5C6432DA9DA266ED7013C74E0C58BA6C172D5AF518B00971D3B5894F74971E73175FBC8965728B3ECF6F1844E4978C4F70C96C0C111579AB175D1CC92A6C96AAE5181C16D35F1C843864C9087BBD754DC59171D987A2D0F65DBD4349AEC383ADD04F9D214AAEB8423F6C14738B3F32734DD3E5D772392FEC88BF63A2D0F64B4A7D65C7C520DDDDBE68
	7C625A7CFF56216B5B631A466E89C7785A47E80ADF9061EB9F8F9EC27B38080C41BDC87747C6ED7F6177A8F7D414E79CBC1F81D8FA0FEA1F5959E6DCAE598F30BB9DF627F8025D1C94B0065353BD40B42833F6B276D3FED960BCD7E85E1F6C9F7D755873235A8763F997DD7397G3FE513B1767C41950CD12BB8EE71A10CD11BB9AE0F7AA8872403393C1699D59925E95EEFB60E3E3C260CEA9F4C2EAF6CA66FFC4D495C2C1277F4F79E55ADB075A6073D17DEF9E21E146547FB095D77A83E15794508BE0137FD32
	E727904027418A73A3888FBFEB3FA01A65A613155729698D6C4FBE6B9D5E1FB5777ADFE336768EDEE31AFB7D2FB16D3D5AB5A643B9FB097F30029FEE6665BC9CF706633C34CD027B52EE11ED9CFF0552C570B5A269907141142EEE0AEBD6F037E9F0E7945C18DEA2EBE059C64EBF98EEC2AF0903BA6C8E0792CF51B2F6A738E8AE0F4914C63B5B24217BFE0EA3F4B33B74EFA5389FB78EF4FF61E45C0F7B63B692178CFDF72F49AEBB44630A7BD7765E9EFB9F775E04FBF11DE35CCC6FD8C33A9E40F6AF6E859E5B
	02B6B188F073F979BE0ACF01F47614782C02A938AE52D13B457C498316D261FA57C546ABEABB2D7979015BA9ABA9A8AB9E591A685CD512E2B2BCFA55B02C8C9BE18EB944BA30627B8EF2372839E3320E5EB1A4FAE11802670FAB4EA14EBD85E5795AB19C479900CB62E5CFFC0863213F8F657BA615EFBD24EDGC177E19B4FED437188875CA2DEBEFFBBBD37090431GB0C462F8CC91D4B94EBCF57D63D12DD35BB95D734D0E071DB42B682679F314C5FDA44AE7973F43939209367F21C302205EEBD75F8F7B3D63
	7E4D5A7E26D16D03793BF63A8E0F83FE97C5228F9C13017EF38947ED4FC47F391C63669CC17F3976FE44DD4E45BD1E027BF1DFFDCFBF31D1F6D96CE47167C99A1338A13F1F17293CC24A48E058D919AF293F9E8779136F473875850A1B8369D80ECB2774AF05F4AAEFEBF7B73DF78B01B979764BE9C15DEA4CD2472F6B7D6BDBFB5FB4ECBD7FEB6FD8DA2FE0AA44FBD38736DBB3F5701EC38BA9C7E348D954DE3A277A46160FD1FC7FD45F5832E2B33FB782F949D379FE61166B0B099BC6DC0F51779233AB53FC69
	DECE71AD53FC693E015BFF9B64A5CCC33AB7368D55FFD99444F3G4DD12A8CD93D4C56216AB920FC90C05820FA699FDE1FDE2C9F361EFF3D282175E6G9D31D1835BDD94B5D8AF16C421DE5CC66535BC4AD73E63A9BEF33A2FFC37F27966C01EBC9D657B07ED5727974BC6DC0F51B70E7A0F4553FD691EC471CB8651DD389D69CE013C268710EE5B673E7D4FFFC015D7823CA78124BE60BB3EFB862D8D339FD44B6443FB99C0158647ECCA8F7DB62883FCB633BBE3295DE906F216875196BDC66DE69824A3B96E94
	351B73A13D0063FECD6DE68A24539E4475F7D86EC84E0F0BE644E79D1941565F84DF2FF9E7366E92E1976323FE92039416179EAC64791332557C79D7C86142CFA162168A157C5855EA7E4F06BA6B5BFF3024E06F875CFFBF773ADAEFF5773554E3FE6397B4D6EC81BE649968CB52B691DC882423B86E141E4A93520BB9EE8245A5C3DAF88875A306ACC514B17F01663DFA73687B6D36EF210F3FEDD1F97A07667D56AD235FF7642876414634388D63083C07F01C6F4E47F84342F11DB47E75C2BA1463363672F3A6
	0E0BDB03F5D3B86EEFC731EE664308EB7B9075D04FF17F23C7BDAC6138BD1BD18FABB84E3399753069E15443311A3189573C3F73AD0C1371DB68636E6DAACF77E95E0B360FFE5F15235A0772ED4867F85EE96363DC59087A954AF15F6BC17DCA6438FB4BD13FD2B82E1A2EB1B2E702FE4DC41C5E086D9D6138F6FE87B414637E0F3797B99357DD9B49B62BE2EFEF4CA076FD003D2D8607D5D4E8311B9CAEEBA12C1840669E60367A6086352EBE137859C2F22928005CAAB296AAF7BEB62AE5269F99421E1713368F
	71367BF255FAD26E355473F717E5087BADF9EA7BFFFE1FB8AB196CC090FE853C2329AB256775A02B381938B648552F4332F800414B176871DB43D4DE3ED53F914BFF5465116D9DF6B37B6CCF296355631AB1B39A6BBC2CDF09F5F85BBF22657220CCC1B47A7C0A361178FC83542B0BD6775C4F437B663495AB52F99F7FCE6D238370DECECB70B96D37E72C5CBD67706EF69427E5DF6BC8767FE3205EC28DAD0903E879933537CB811F9A0DF14F76E358EF41A3088B2176368C52C7B8AEEFB30E031B631AB7231C03
	B8EEB2450D037464C770CCE44667F8A69283388400D4006CD9E03B47A32F4D0D57A2F7BCABAA003A87E6293C564C726575B9BA771BGFFFE965276C4B9729ABA8B47621CE17077A5D7795D444933983DB170DCB80B5DFDE0746EB10E64DCAB896A26EA684D1E6DCB6F9D543664813EE0B652DBE0C0FA6BE623ED71EED26758CB1BF67A4EE1E6DBBCA62747A3CA768EB0AFFB95732254B921A76D6D636931B4FD004F61FB74EA7B051B495896AA87EA440E0DECEF6707B446E36F4736296D361F9B622E43A13A89E5
	A8D64A4EBBC768AB9BF15F266DEA5F2B8B543E7FFC745A7BBEF88C7BE66DD091A8F93F530FDC763BB78F143DEB73C059073C2B367FF7A6FB65C00EF44C56DE0BBE4735D78B68C458EC5C8FFE3A0D885B4FB7A84A87A8384726EF403B208933F17FF0CC83DD1F4E094F4A87B8GB0B2D94F5C214A7A67BBD59E4617DF3B2C7F2B9CE54D78FC105F492983FA026760FE60CB8624BFF68E52AF52333BD4C82F1D9B1F35E36EC8697F6BBFD57A5FAED269CF9E817D6586241F45C13F9C34963E3678E949F5565B56B0DC
	3DA3F3876EDD30B53E2E116E894D75DD7B2F217816393EEB7F30863EA784F989F3B126FE64BA4FF79F3F4EFA294356AB1B670F5FDFD3DD291A674B6F7394DFBF4F175F0B7CDC3885726266E15CF7004E239D0F42FCF994E5105DA8F07AB6E85EA39AC6BF6EFEF4D47BE0BC2F21E733B540EFB36779B94D7E7BD24D7B575F42DE7C6D235A871B579FB710BDA61B5564E6BE595400E7BB0EC7B106A8BCB4300C6190166967E53C8DC4077673F6BFBB04E9767F6462B6114B3651AF8AABBD12301E5B1BDAFAE818F4E9
	3FB1637753B2F4C2E62D10458ED1EB05CCF292DAAB2C24872535C2066CF2FB498DF3C28C597109A46F505A4641562BF24C2FFF955DD6152793F45020CDE4AD2C6CA6C723348DC254D14F574295C1F7F5450A9584D8CA200EB87DFBB4A5A83DF188D0037E3E319A12D0B068930493AE07CBD2BFB53858784AFDAF7F7905715014D7925E608220455227EF3AA9E76C7EAE1D10DDADE4F5189CEE1B89182771F81D30261D4AD314202CAC6454113AFB206E9EDA77F01D20B3FBE4E12B0AB793BC4BEDF41B4CA294D9ED
	B1C997E98B040375DF892BBBA4EBBB6DC096F4DFD5FF855D5294495ED0A73CAE5B4825EEF252A28B1B54266B4915B2A563B09D97E185001B7EE4A1233C7420AC2C24FB04DCF2E46D792ECFCA92568D08D2FEF27F419792FEF75A13A1E4558A1B4986EAA3C4AC64AE01E8A15F67AB7F042BDAD86DF6818564539BD0B78FFD2C7448BD8E10D3DEC7837947B49ED934D462AD85904C1B0CFAF2F83A27466431496496G08A453C97EDD183011E5BBCCCE6B9EF6E5017115DEF91E0E749BF45CA5A11752CBDAA9652D64
	BE3EFE67BB9FAD191FA1682A05579DAE13255AE4064834D6D8AD5BA451E3F3B9ACDE77079534216CEE3C4B1A814DED656C93E6CB1BC01E1C680B5EFBC357ADDDD74DF4E58FADC984DE4ACBD8CE58CC92F4A5CA35B27977E7AEE9874F3BE13B7C4F1F1D5A5422935295A6096837F8E1F8C035BA6D0E9EF8E1EBFDBAA85BE520D60298117CAB98D4E58DE51845BBA81D70E4594407DEBDCBBBD02B4A425B2A1ED8C5D7671E4A0E8E3BD3648D00DC2179ED3CF9D22654274CC66FDDEFFE746C0AF65003AAA123332C0C
	7E5DCB7F6EA3FF77D24CDE0A593B8F041BA28C79734D77A24FD4F938E5A343DFDA8DA3A87F6645A7277E76C8CAB2F0D72DCCC198049C355427DD1D6D22F4CB7444EDEB42126EA0E364A62A62A56D96FAE561F5F5F704ABBD09722A7D245014E084480C0F5EC77D52A9B6FF109EE592F401AC2CE123D95A8E6A066CE531CBA1B4D9C2AA1297B68BF7523FC5A3169C29350BB54899BE888C2FD216E31CA372DB05AF1C6BFCD07CC7F91937B4E5327D1479D7E4C0D09F10BF0651E17A84E1DAD987FDD3CC337B4645D7
	35770D175F2C8F78ADD974634AFD3F47FD6F9B7F087EEF03B4406BE3714E70060A770540772B9ABBCD8E07ECF2DC2CF4DA48DD2B63BD3228FECB74C485E95F03E514CC1677338AC27785CD1D7F87D0CB878813B3D56A8C9DGG68D6GGD0CB818294G94G88G88GACF954AC13B3D56A8C9DGG68D6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC69DGGGG
**end of data**/
}
}

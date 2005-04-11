package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

public class LMProgramControlWindowPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	javax.swing.ButtonGroup buttonGroup = new javax.swing.ButtonGroup();
	private javax.swing.JPanel ivjJPanelOptionalWindow1 = null;
	private javax.swing.JPanel ivjJPanelOptionalWindow2 = null;
	private com.cannontech.common.gui.util.TimeComboJPanel ivjTimeComboStart1 = null;
	private com.cannontech.common.gui.util.TimeComboJPanel ivjTimeComboStart2 = null;
	private com.cannontech.common.gui.util.TimeComboJPanel ivjTimeComboStop1 = null;
	private com.cannontech.common.gui.util.TimeComboJPanel ivjTimeComboStop2 = null;
	private javax.swing.JCheckBox ivjJCheckBoxUse1 = null;
	private javax.swing.JCheckBox ivjJCheckBoxUse2 = null;
	
	private javax.swing.JToggleButton ivjwindowChangePasser = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramControlWindowPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getTimeComboStart1()) 
		connEtoC1(e);
	if (e.getSource() == getTimeComboStop1()) 
		connEtoC2(e);
	if (e.getSource() == getTimeComboStart2()) 
		connEtoC3(e);
	if (e.getSource() == getTimeComboStop2()) 
		connEtoC4(e);
	if (e.getSource() == getJCheckBoxUse2()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxUse1()) 
		connEtoC6(e);
	// user code begin {2}
	if (e.getSource() == getWindowChangePasser())
		setTimedOperationalStateCondition(getWindowChangePasser().isSelected());
	// user code end
}
/**
 * connEtoC1:  (TimeComboStart1.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramControlWindowPanel.fireInputUpdate()V)
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
 * connEtoC2:  (TimeComboStop1.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramControlWindowPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (TimeComboStart2.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramControlWindowPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (TimeComboStop2.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramControlWindowPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
 * connEtoC5:  (JCheckBoxUse2.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramControlWindowPanel.jCheckBoxUse2_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxUse2_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JCheckBoxUse1.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramControlWindowPanel.jCheckBoxUse1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxUse1_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JCheckBoxUse1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxUse1() {
	if (ivjJCheckBoxUse1 == null) {
		try {
			ivjJCheckBoxUse1 = new javax.swing.JCheckBox();
			ivjJCheckBoxUse1.setName("JCheckBoxUse1");
			ivjJCheckBoxUse1.setMnemonic('u');
			ivjJCheckBoxUse1.setText("Use Window 1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxUse1;
}
/**
 * Return the JCheckBoxUse2 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxUse2() {
	if (ivjJCheckBoxUse2 == null) {
		try {
			ivjJCheckBoxUse2 = new javax.swing.JCheckBox();
			ivjJCheckBoxUse2.setName("JCheckBoxUse2");
			ivjJCheckBoxUse2.setMnemonic('u');
			ivjJCheckBoxUse2.setText("Use Window 2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxUse2;
}
/**
 * Return the JPanelOptionalWindow1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOptionalWindow1() {
	if (ivjJPanelOptionalWindow1 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Optional Available For Control Window #1");
			ivjJPanelOptionalWindow1 = new javax.swing.JPanel();
			ivjJPanelOptionalWindow1.setName("JPanelOptionalWindow1");
			ivjJPanelOptionalWindow1.setBorder(ivjLocalBorder);
			ivjJPanelOptionalWindow1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTimeComboStart1 = new java.awt.GridBagConstraints();
			constraintsTimeComboStart1.gridx = 1; constraintsTimeComboStart1.gridy = 2;
			constraintsTimeComboStart1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTimeComboStart1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimeComboStart1.weightx = 1.0;
			constraintsTimeComboStart1.weighty = 1.0;
			constraintsTimeComboStart1.ipadx = 29;
			constraintsTimeComboStart1.ipady = -7;
			constraintsTimeComboStart1.insets = new java.awt.Insets(1, 31, 3, 63);
			getJPanelOptionalWindow1().add(getTimeComboStart1(), constraintsTimeComboStart1);

			java.awt.GridBagConstraints constraintsJCheckBoxUse1 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxUse1.gridx = 1; constraintsJCheckBoxUse1.gridy = 1;
			constraintsJCheckBoxUse1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxUse1.ipadx = 73;
			constraintsJCheckBoxUse1.insets = new java.awt.Insets(0, 13, 0, 131);
			getJPanelOptionalWindow1().add(getJCheckBoxUse1(), constraintsJCheckBoxUse1);

			java.awt.GridBagConstraints constraintsTimeComboStop1 = new java.awt.GridBagConstraints();
			constraintsTimeComboStop1.gridx = 1; constraintsTimeComboStop1.gridy = 3;
			constraintsTimeComboStop1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTimeComboStop1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimeComboStop1.weightx = 1.0;
			constraintsTimeComboStop1.weighty = 1.0;
			constraintsTimeComboStop1.ipadx = 29;
			constraintsTimeComboStop1.ipady = -7;
			constraintsTimeComboStop1.insets = new java.awt.Insets(4, 31, 9, 63);
			getJPanelOptionalWindow1().add(getTimeComboStop1(), constraintsTimeComboStop1);
			// user code begin {1}

			//TitleBorder Orignal Title
			/* Optional Exclusive Control Window */
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOptionalWindow1;
}
/**
 * Return the JPanelOptionalWindow2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOptionalWindow2() {
	if (ivjJPanelOptionalWindow2 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder3;
			ivjLocalBorder3 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder3.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder3.setTitle("Optional Available For Control Window #2");
			ivjJPanelOptionalWindow2 = new javax.swing.JPanel();
			ivjJPanelOptionalWindow2.setName("JPanelOptionalWindow2");
			ivjJPanelOptionalWindow2.setBorder(ivjLocalBorder3);
			ivjJPanelOptionalWindow2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTimeComboStop2 = new java.awt.GridBagConstraints();
			constraintsTimeComboStop2.gridx = 1; constraintsTimeComboStop2.gridy = 3;
			constraintsTimeComboStop2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTimeComboStop2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimeComboStop2.weightx = 1.0;
			constraintsTimeComboStop2.weighty = 1.0;
			constraintsTimeComboStop2.ipadx = 29;
			constraintsTimeComboStop2.ipady = -7;
			constraintsTimeComboStop2.insets = new java.awt.Insets(4, 31, 8, 63);
			getJPanelOptionalWindow2().add(getTimeComboStop2(), constraintsTimeComboStop2);

			java.awt.GridBagConstraints constraintsJCheckBoxUse2 = new java.awt.GridBagConstraints();
			constraintsJCheckBoxUse2.gridx = 1; constraintsJCheckBoxUse2.gridy = 1;
			constraintsJCheckBoxUse2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxUse2.ipadx = 73;
			constraintsJCheckBoxUse2.insets = new java.awt.Insets(0, 15, 1, 129);
			getJPanelOptionalWindow2().add(getJCheckBoxUse2(), constraintsJCheckBoxUse2);

			java.awt.GridBagConstraints constraintsTimeComboStart2 = new java.awt.GridBagConstraints();
			constraintsTimeComboStart2.gridx = 1; constraintsTimeComboStart2.gridy = 2;
			constraintsTimeComboStart2.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTimeComboStart2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTimeComboStart2.weightx = 1.0;
			constraintsTimeComboStart2.weighty = 1.0;
			constraintsTimeComboStart2.ipadx = 29;
			constraintsTimeComboStart2.ipady = -7;
			constraintsTimeComboStart2.insets = new java.awt.Insets(1, 31, 3, 63);
			getJPanelOptionalWindow2().add(getTimeComboStart2(), constraintsTimeComboStart2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOptionalWindow2;
}
/**
 * Return the TimeComboStart1 property value.
 * @return com.cannontech.common.gui.util.TimeComboJPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TimeComboJPanel getTimeComboStart1() {
	if (ivjTimeComboStart1 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Start");
			ivjTimeComboStart1 = new com.cannontech.common.gui.util.TimeComboJPanel();
			ivjTimeComboStart1.setName("TimeComboStart1");
			ivjTimeComboStart1.setBorder(ivjLocalBorder1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeComboStart1;
}
/**
 * Return the TimeComboStart2 property value.
 * @return com.cannontech.common.gui.util.TimeComboJPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TimeComboJPanel getTimeComboStart2() {
	if (ivjTimeComboStart2 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder5;
			ivjLocalBorder5 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder5.setTitle("Start");
			ivjTimeComboStart2 = new com.cannontech.common.gui.util.TimeComboJPanel();
			ivjTimeComboStart2.setName("TimeComboStart2");
			ivjTimeComboStart2.setBorder(ivjLocalBorder5);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeComboStart2;
}
/**
 * Return the TimeComboStop1 property value.
 * @return com.cannontech.common.gui.util.TimeComboJPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TimeComboJPanel getTimeComboStop1() {
	if (ivjTimeComboStop1 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitle("Stop");
			ivjTimeComboStop1 = new com.cannontech.common.gui.util.TimeComboJPanel();
			ivjTimeComboStop1.setName("TimeComboStop1");
			ivjTimeComboStop1.setBorder(ivjLocalBorder2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeComboStop1;
}
/**
 * Return the TimeComboStop2 property value.
 * @return com.cannontech.common.gui.util.TimeComboJPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TimeComboJPanel getTimeComboStop2() {
	if (ivjTimeComboStop2 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder4;
			ivjLocalBorder4 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder4.setTitle("Stop");
			ivjTimeComboStop2 = new com.cannontech.common.gui.util.TimeComboJPanel();
			ivjTimeComboStop2.setName("TimeComboStop2");
			ivjTimeComboStop2.setBorder(ivjLocalBorder4);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeComboStop2;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.device.lm.LMProgramBase program = (com.cannontech.database.data.device.lm.LMProgramBase)o;
	program.getLmProgramControlWindowVector().removeAllElements();
	
	com.cannontech.database.db.device.lm.LMProgramControlWindow window1 = new com.cannontech.database.db.device.lm.LMProgramControlWindow();
	com.cannontech.database.db.device.lm.LMProgramControlWindow window2 = new com.cannontech.database.db.device.lm.LMProgramControlWindow();
	
	if( getJCheckBoxUse1().isSelected() )
	{
		int startTime = getTimeComboStart1().getTimeInSeconds() ;
		int stopTime = getTimeComboStop1().getTimeInSeconds();
		
		if(stopTime < startTime)
		{
			//make sure server knows that this is the next day
			stopTime = stopTime + 86400;
		}
		window1.setAvailableStartTime( new Integer(startTime) );
		window1.setAvailableStopTime( new Integer(stopTime) );

		window1.setDeviceID( program.getPAObjectID() );
		window1.setWindowNumber( new Integer(1) );

		//add the window
		program.getLmProgramControlWindowVector().add( window1 );
	}


	if( getJCheckBoxUse2().isSelected() )
	{
		int startTime = getTimeComboStart2().getTimeInSeconds() ;
		int stopTime = getTimeComboStop2().getTimeInSeconds();
		
		if(stopTime < startTime)
		{
			//make sure server knows that this is the next day
			stopTime = stopTime + 86400;
		}
		
		window2.setAvailableStartTime( new Integer(startTime));
		window2.setAvailableStopTime( new Integer(stopTime));

		window2.setDeviceID( program.getPAObjectID() );
		window2.setWindowNumber( new Integer(2) );
		
		//add the window
		program.getLmProgramControlWindowVector().add( window2 );
	}
	
	return o;
}

public javax.swing.JToggleButton getWindowChangePasser() {
	if (ivjwindowChangePasser == null) {
		try {
			ivjwindowChangePasser = new javax.swing.JToggleButton();
			ivjwindowChangePasser.setName("windowChangePasser");
			ivjwindowChangePasser.setText("");
			ivjwindowChangePasser.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjwindowChangePasser;
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
	getTimeComboStart1().addActionListener(this);
	getTimeComboStop1().addActionListener(this);
	getTimeComboStart2().addActionListener(this);
	getTimeComboStop2().addActionListener(this);
	getJCheckBoxUse2().addActionListener(this);
	getJCheckBoxUse1().addActionListener(this);
	getWindowChangePasser().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramControlWindowPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(332, 333);

		java.awt.GridBagConstraints constraintsJPanelOptionalWindow1 = new java.awt.GridBagConstraints();
		constraintsJPanelOptionalWindow1.gridx = 1; constraintsJPanelOptionalWindow1.gridy = 1;
		constraintsJPanelOptionalWindow1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOptionalWindow1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelOptionalWindow1.weightx = 1.0;
		constraintsJPanelOptionalWindow1.weighty = 1.0;
		constraintsJPanelOptionalWindow1.ipadx = -10;
		constraintsJPanelOptionalWindow1.ipady = -5;
		constraintsJPanelOptionalWindow1.insets = new java.awt.Insets(6, 4, 3, 5);
		add(getJPanelOptionalWindow1(), constraintsJPanelOptionalWindow1);

		java.awt.GridBagConstraints constraintsJPanelOptionalWindow2 = new java.awt.GridBagConstraints();
		constraintsJPanelOptionalWindow2.gridx = 1; constraintsJPanelOptionalWindow2.gridy = 2;
		constraintsJPanelOptionalWindow2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOptionalWindow2.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelOptionalWindow2.weightx = 1.0;
		constraintsJPanelOptionalWindow2.weighty = 1.0;
		constraintsJPanelOptionalWindow2.ipadx = -10;
		constraintsJPanelOptionalWindow2.ipady = -5;
		constraintsJPanelOptionalWindow2.insets = new java.awt.Insets(3, 4, 13, 5);
		add(getJPanelOptionalWindow2(), constraintsJPanelOptionalWindow2);
		
		java.awt.GridBagConstraints constraintswindowChangePasser = new java.awt.GridBagConstraints();
		constraintswindowChangePasser.gridx = 1; constraintswindowChangePasser.gridy = 2;
		constraintswindowChangePasser.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getWindowChangePasser(), constraintswindowChangePasser);
		
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	jCheckBoxUse1_ActionPerformed(null);
	jCheckBoxUse2_ActionPerformed(null);

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if(getWindowChangePasser().isSelected())
	{
		int start = 0, stop = 0;
		
		start = getTimeComboStart1().getTimeInSeconds();
		stop = getTimeComboStop1().getTimeInSeconds();
		
		if(start == 0 && stop == 0)
		{
			setErrorString("A timed program requires a non-zero control time to be specified under the Control Window tab.");
			return false;
		}
	}
	
	/*int start = 0, stop = 0;
	
	if( getJCheckBoxUse1().isSelected() )
	{
		start = getTimeComboStart1().getTimeInSeconds();
		stop = getTimeComboStop1().getTimeInSeconds();

		if( stop <= start )
		{
			setErrorString("Control window #1's stop time can not be less than or equal to its start time");
			return false;
		}
	}

	if( getJCheckBoxUse2().isSelected() )
	{
		start = getTimeComboStart2().getTimeInSeconds();
		stop = getTimeComboStop2().getTimeInSeconds();

		if( stop <= start )
		{
			setErrorString("Control window #2's stop time can not be less than or equal to its start time");
			return false;
		}
	}
	*/
	return true;
}
/**
 * Comment
 */
public void jCheckBoxUse1_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getTimeComboStart1().setEnabled( getJCheckBoxUse1().isSelected() );
	getTimeComboStop1().setEnabled( getJCheckBoxUse1().isSelected() );

	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jCheckBoxUse2_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getTimeComboStart2().setEnabled( getJCheckBoxUse2().isSelected() );
	getTimeComboStop2().setEnabled( getJCheckBoxUse2().isSelected() );

	fireInputUpdate();
	return;
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o != null )
	{
		com.cannontech.database.data.device.lm.LMProgramBase program = (com.cannontech.database.data.device.lm.LMProgramBase)o;

		for( int i = 0; i < program.getLmProgramControlWindowVector().size(); i++ )
		{
			com.cannontech.database.db.device.lm.LMProgramControlWindow window =
				(com.cannontech.database.db.device.lm.LMProgramControlWindow)program.getLmProgramControlWindowVector().get(i);
				
			if( window.getWindowNumber().intValue() == 1 )
			{
				if(! getJCheckBoxUse1().isSelected())
					getJCheckBoxUse1().doClick();
				int startTime = window.getAvailableStartTime().intValue();
				int stopTime = window.getAvailableStopTime().intValue();
				if(stopTime > 86400)
					stopTime = stopTime - 86400;
				getTimeComboStart1().setTimeInSeconds( startTime );
				getTimeComboStop1().setTimeInSeconds( stopTime );
			}
			
			if( window.getWindowNumber().intValue() == 2 )
			{
				if(! getJCheckBoxUse2().isSelected())
					getJCheckBoxUse2().doClick();
				int startTime = window.getAvailableStartTime().intValue();
				int stopTime = window.getAvailableStopTime().intValue();
				if(stopTime > 86400)
					stopTime = stopTime - 86400;
				getTimeComboStart2().setTimeInSeconds( startTime );
				getTimeComboStop2().setTimeInSeconds( stopTime );
			}
		}	
	}
}

public void setTimedOperationalStateCondition(boolean timedOrNot)
{
	if(timedOrNot)
	{
		com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
		ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjLocalBorder.setTitle("Control Times #1");
		getJPanelOptionalWindow1().setBorder(ivjLocalBorder);
		
		getJCheckBoxUse2().setText("Use 2nd Control Time");
		
		com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
		ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
		ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjLocalBorder2.setTitle("Control Times #2");
		getJPanelOptionalWindow2().setBorder(ivjLocalBorder2);
		
		getJCheckBoxUse1().setSelected(timedOrNot);
		getTimeComboStart1().setEnabled( timedOrNot );
		getTimeComboStop1().setEnabled( timedOrNot );
		
	}
	else
	{
		com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
		ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjLocalBorder.setTitle("Optional Available For Control Window #1");
		getJPanelOptionalWindow1().setBorder(ivjLocalBorder);
		
		getJCheckBoxUse2().setText("Use Window 2");
		
		com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
		ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
		ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
		ivjLocalBorder2.setTitle("Optional Available For Control Window #2");
		getJPanelOptionalWindow2().setBorder(ivjLocalBorder2);
		
		if(getJCheckBoxUse1().isSelected())
			getJCheckBoxUse1().doClick();
		
		if(getJCheckBoxUse2().isSelected())
			getJCheckBoxUse2().doClick();
	}
	
	/*//fire it off to enable that window
	if(! getJCheckBoxUse1().isSelected());
		getJCheckBoxUse1().doClick();*/
	
	getJCheckBoxUse1().setVisible(!timedOrNot);


}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB4F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DD8D44735A422A1CA92CCD009BF899A121004C4DF2AADCD4DAB25345506B6B64F3436356F23DFFD097DE27A512F36B529EDAE8B2C1828EC14B4D8A522A8C19FC579CB90B70695D4FE56C8630A24E26242DE58555D3D6B6E654FA03CB3B3775CFB393B6B8672081F07FB674C1CB93FB3F3E666821737A7B6BBC31747F199591C746F7D8C0E4BEF65385DD75F6FC1DC981F2C67D47F76823C492DBF1F
	854FA828CBDA1C2CB7F0068FF3209C87653805497A3761FD877756076B1B60A5881FD4287B6D12E987EE4E6739DD48E793AD3FF2EA8BBC378144830EBC5773047F1B2773A5FC2A04C74850F15C5AEE52267474EE89D78365D600B80044EE56FE85BCB7D34E13EBEB24F69D53E33975BFFCD7371574236A9361C88F2335C84FC2EEE146469E426B7C69AD92CDA414D3G087219703F911E46562BBD473B84D7D917556D965CA25FE5AF6BF7DC37FAEDE5B63E4F514517B9DD9E2F5063353AEAEB4DE7DA59EBBD3474
	8A4E8B8E37CD68EF353AF967001D701AF7BA07754D55C1B94BC1F09F5FA07C17C2F9A540BA993FF318609B617DB2400083F57E60DD2324D37B678B3945BFF3E7668651B94A41F4CDF4A8BAC7773E3AE9A7754F7682620775D017FFA5D9DF8A508A6081088258C563E2676EC370B4F50A75024BA538CD9E4F89274DFBC63071CEF82F2D059A8977B15FA5F8ED9C47FCFF71F4E9GF9EE860C4EB1E6BAE61378CA0C03040F9F62D6FF2DC537A900CDD6479F5145DFE136C83A22580471DE9014F75A1551F1BA1A7785
	CE4DBBEE0A10199D007762678F6A56CB7D64DCD5F857DCA53E7EBE0D0192F84F10620361D7D07C108A4FE2361D62C9B9926A522FE2EC34EFC3D94CD933B9C339B9209DE212CDBA0B134960F4EAE519CE63AE4649F8EE15E509257825AABC13E58F4513F29A5419DD49FA96DF3B7610B11C886515G6BG325C497A7DGF5G96B746D8676E43DFB046DA2DDE71C42F43E9633D9C235DD570F7F8728322575A647609D6F7975FE675BA2C1DCE5E47C9ED7697909BF40E22BBC8E33F8BF81C665D3C57AABA84B774EE
	F571A26F7531B69585EF12B6DE1E56DA9CAE9E9838BC9C76DBDB3003584F6A934FFBECD611D7DA307A938544A7FD56B66891BA00F7E61746C2E2AF91740F8418AB5B61E95ADF8B6F0559822692C1385A6B3190CD38F5EEE267EF5239E3913CAFF3230D9B76925C91A1D95FA8E0BF6B8B362B7AE9F0F491712D5EC19CE329B748BC5DA1E04C7C2E9DE326E16B03DC69734D81E306C03840E2E52E209DBBBF25FD9239D879539B6DE39C47DA6890820FB9E52E687F52BCB2EFD2476C7B64BE622B9410A58D0073200D
	8F3FD348E276045547E3141E75C20071B64562CC4ED9437912BD893FECC9FEE27BB7E4DD9E22ED72217FBAGBBC094C0A2C01A87EDFAE07872CD7A79B2F67037ED9D6595084F567FC7DE4C16877610750B6F635DE2836F615DB6F871C9F525FB88DFE9FCF78BB2CD8545FBE0BE280781681371B5FB4818350A22575159AB7267F8D1F438FB147ABA4A4B575B69D1269264F5FA8F51D5680779C0EEEF5EC346D21760F6735429B209D47F919D4B3C13F7015075C22FDBE4F8CBA1998FD779C1E9AEE9AFA489D31F55
	59CB66A516DFAC3292FBFA3C8E97048A195AB867B5BA674B7991DDABGC77224BAF8DA2E61186DAF42B13B7F7AFCAE6933833AC0EBCE4C038D3A455758B8DAF6CD9933CC26263D01E5BFCB6794896F4B2438ADE5246EA6690DB4DEDEAAA2FD2E85DE9B81723CB8DE1E59CB6AF1CE1286CA3D60F4B2334A76CE5CE7A07E612F7572B01513492FC536613777ED575435C976E475BF58C76C69D44D1B4B3C441E2B07B117AC05F283005D4BEC9A6F15ECC35B450EE03BBC9F64C33EE06DAC9C36EBA5ED02364B4940F6
	1150A6AEE83B25F23BE550A68D0093A51F0B6AF6E1BAEC978178D8D1E917A435E3F13569B31247844ADBFBFA9DE53D22032C8AD691861427D7D46526350DB9D29FB93D1803DADA08BF6B205C8E606945787B49D64CC1232F46F2E98F7CA3E85E9D514BE4CB68D5622FF09266205587884FF5D0373197E365C411FA9DB479FCC21703AE41121FFFDF240ECF067BD3516B706CF6F8FDE293CCA08398939B0B083CE40D1650D24E12BD32C552555087EBA7G9BFB05C755E3AF8C70CB8152G56833031609C29A0F38C
	8D5CB276E8653D5D0257455BCA2ED87B2CE556FE310C4EE7E5A6DAFD123C9FC47F249C5C4EE4652987D872D0F2308D734985BB6AA47E29CF4C660CA3BBF401727B085C825D16FEE6532AFE4536C3CF6AB357FD7A17CDCC4F65321EE150A6319F759C3A98C84FF4284F9AGDFD05A9B9C526E861CE5GE91FB926250D043AC5GA9B2EDF986522E86DC5EA052AE2F8CC4EB067A4B03C87BBFB26DG6096816C3FCE70D1B772D434EB87992D61BA7A452C7BF2FE3970B60969AB75F63E6B6A89E1603C0F2F784C2469
	469C3C1FE3875B187C0F68D0FEA74896F79D654F4E554ACFEC3E7ABA6EB72DFE7C4B47481F2D5D9FB6EA77B1695715DCAA6BF37F3C0A65774B68D84F7F1C657759B27E3FA83ED405E7FA7E3B85752C033A384FB1A7CDA9A6BE4E99823D810CG96G91A0FA8847F8787ECAAEC44E5F84F31657EDF51E9B35D8E36EFD00789C56D729FC7CG11032E2F9C66B0EF0CE86D30E8E8346E291AB2235B69C7D7263527060E79EF1A691FDC28F9176C91C065CE21192D1A1C34663FE19E35665FE0B9FEA9BC6BG587C5CFE
	946767769B725941B66203A8A84783A45DC09F6CABA1EB45AAA82F8758B21C2CAF81B0835881A207518F4B8EFC904A8FB438ACC2BD609AE1ED2D5766C2325D1E9D273DFF95525E8943236918BD96DFC4FB248C23BDFC549E9B211CB782769B415CF992B7BE59267B51E9E5EB9899CD477CD91C9B481F9D5056B3027E4CB4227C91A3A87F7DC3C47EA4A82F82C81765FFBFE3FC721F7423534A1F9396483699D5A8DBE1984A76ABAADBBB14BDG91E1A85BCB63146D95BF3AF7B4655830C0636CF85BAE9CE7E4B69C
	A87375C30EDFDB5B0CB32724438FEE64C83C9243B0EF39F1CA1D37AC4D2D50111CB1D05E12AE71B65C222C2D253D2FEEE232AF540DCF67277D6834BA375E92489F46DC54458E756CBC200D66EB31D0DEAC6376B6935CAAA8275F027E79F18B41655D1A2C2F3995F1BF273825527A4BF017DB463B1FA6FD2B4BB5377A2FB10C476038F9E8776A59815A30B96DC745417713AB0B836FA7FFD19CF8BF19D67CA643C39F6A53A72CDFDBFC737DE6FAF1207DE68155E7795FF3E95FBE9F6F2313ACC94A253A97453738G
	7B4E0F2CAEDE32856449A31B25B6A7C5213EDC9E9FFFB8189FF8FC28F27DFAB346D3472DB83EFBEA48780E06F282C00A9CAB170E2AE3F63546376981FCAD1DE95239B8EB924B9BB2E47C3394EFD06125B34DEDF88E3F9B6A42A6619AD5DA925C1F15A5017DF934046CC7FCC22F370B577A2C2EA4DF2ED3D633A611F79173CAFBE3CAAFDA3DBD3C1F4FDBA9BD2B8BC67FC9C93038E03E53F3EA5FD54863631B975536DEB3E934EDB7CC8AE66B97296D3604EBED1DCE717961DADB8F4A365E8FF5916118C3BFB5CAAE
	122331F879D41EFF5605E33C98AAC99F7BA7C30E87E0190C7112E3D46B601CBCDA66304941F458C14F3423A7EBF558CD71311335BABCB804BAA4C01DE18A6A604ED06BD0A55BD6DF2516CB1CB2DA0E08A941647A9765BFF70AD62EFFD3FC7C942DDC3F1165DA8CF5793721DC7BF4EA392AB965BCD63BA68C81CD54EDCC1E385B7C65E2B95E9F8F127DC4A2542F84D887109581B9F7840E1B3F16044C31CF8E844C31193CAF0C1277BDD9DED32ED65E8660599EA15DA7C4784B4B7AF3B512B3B027F5D06895A10EA2
	082D429BB1CF8A03F28CC092404A8875F946FF7413B53A5BEBF54190BA67613B38E537935A1AE6242D02F2A3009DA082A07EF63479C70748BC4AE63E339EFAACAB1DDA154BF22D298D56C699170DE6354E0629E067290A1EB5D303454EAF691CEA1E4AE2E413B2DEA95E26423358F1D1BCA98FC1DD5AD4DC57C51A7BB7CC83BBCFC35C8F0D38568FCD433117CAD8CBB2BF2FFA7FF45B17BDAB3F99C86771934A03693C02667C2920EF1AEC07A517508ED911E80717D53A7FC975BE729558A166AB6051B5EA8CDAC0
	EFFB249203C3114162B11C4E59D1115AF80C227878C8EDBC7E3D9263B1896A765F01B65FC3E3AF824AF3E5DC56F174C3529D68071DB7945B6FD33DBFB6B471B6CA19D09ECC67823ACFB25C893639936D20FB8F6D50F8A75A6102CA774B2A775FFE85F650FD69FB3250F6788D5DEB45003E89329DCA0789AE894AABE45C74E1344D6ABB65F56AE0707CAE6BD0607C6E75C30173F543211B676B79070265E5CC3EF61A67145E152C2F3B8BE51ECFF196A85BE41C5E00FAF86E42F34B04C6DC7713G3761AEBC57AB3F
	CC4EE264FC1C1C5E55561E76BAECA72CBDE63A9A329CEBBD5D4F917AA3D118E36DEBA2FD5A219C869093057B5801E25C47A6BCB41BB3073584BCFFCF33D46896C53139E0E514320F0D07FD2C1457B5A3CF43F40857698497592CBE27A4EB649C9DBBB7E0757FB92A1E58136CD9147A74D175C45EFBC7516FEBD157939B3DAB51B35D1EB9003AEDFCA61EF3AEB99A74EEA1EABA53E93174ECEDAB6276CF7D4BA666234F689EE79D54F1F7235F9A693EF7BF14EBE45C92B37A3263EE9C2F3F266BF0821417812C1171
	AFD3BC39C3A277AF640E0861FDB5B846C8DB14EF595D7E73704DA0A1C8FB46A3D26631C9E33F317088F6674DF221E9C7C90E56A9F86920E1B95A8279CE043B8772D900D6G5BBD6CAEED51BD244D2F0D8DA1F8CA5FD4D198821AA800A2299C876F4760790757DEFB0D497162C5FFB97EA65FD9EC1064B0C4433C9B4D7C4064C83D94CA0ECDAAB90CC05B9A2D48E10BD664E031F11D0EFD8F604323B1DEEA9D84978F6525B24E654298CA0B461818CA574EA3B31275E6G7B8C444F2478B8692E4FAE47506E4A314E
	53246FC0F833862F702E9EB30FE071220DBB346B8E6EBD233F6F4E9851F761B3186F6642F3518C26BF735D2E63E30921E5C03BFA06623B74995A982A2A721723258A6558A84911B7B3D9DFB2135D4FB2B9F63EB716982A83DA4BCCC58E7BCC2D9C2F5235516566E19366688A1FC93F01246B60CBG62CC5C2B548F13EF86C4D86C164DA45FD78E11AD8A47E40A9F8EA5139A364CCA5693A0B211F933905E6F6114EFC7F6F0C22E3FCCD3F2D12652D9A8D3E26EE819FED82148D4E078B2B299D5B23158FC526F5EAA
	94B03A24F1523DB2E6BA16AB2F27EBF263AC6DF940DFA95EB2CBFB9ED05204F941E528CB1905F7B40F533B50129818C781BAGC400E800C5B1189BBDF1688897E28FEF91BCE6C81E1C2AF4C75AA3B40F6FDB4A6FEC4275F771D38B3844A92FE484BBA3CF09E1734C9A693956D2C471CC3F3AEA22DF5E3D497AA3G96GA7C098C04C3D285F1F8E15B27DCE5A9C22C9942DDDF6FA2B0F4A35B978FE85CDB67FACAE7B4C7EE37B8E798EAD61DEB636D360396ADEF64F4A68BEEB71BF0368EBC13A74FB5999C44E6CE4
	7D6E59CCA7E9DDB10FA5562B0036F136B2A7F4407B4BB09F7CD91E93CEFAC94EAB5F861E9341F96552587CE3BF49F71A5577401C3CBE3F35EDB41D60A947B8FB7EA8FE93EA87FE3133B17FFC3160A0977ADED2F472A7A4F3CAE7FE130E61374A2BE6E3BE7A36111E5D4E01B993208EE08F150B63F2B87C177EE1A09C1BBF7EC60E1124B87B75960C335D0F4D619AFE76CA864E8DDA7B364FE13E081F236430EB2FEC143EF1A86EC03D5320BEFFAE5E2F39BBD07E5639A83F6E92E99B8165D8004439E3157FA97259
	15640B69397EB198AF5F7B2D184BE2F0ADBCB74EE577AC0C6EDF8E7F9834BB10AEEF9E0B41D2F8564DD347E038EBAC6BE3BB50BA67A9B198B6CF9B0391C77CE229C20EB333B686ABE43D9FED700F5D8AA986FB0D72F74940AFE59E7A62E9A37AE263BC74453FBEA0ED2B6E037C8840F65FD8FD712B232444787A2A106F90743378BE647B2DAA643B6EBE64FB361A1EDB5F1F2CB782D86E9FABDF6E189283A5436479EE6F3BD2CC47762BFF9F2166757783FE17C740F9BF73ED1874DCFADB115447339755FD98E66F
	55E58469A3D122CD39DF99977EF2C32EC465DEE8D4641E170BF2333A9749E7G523A78EA13721E592C3C17B5CD7CD94569896541561C2FD786BFEFF83AB270F943F7AB831FB7AC2F3C797D5FCFAA03FFEF3A2AD27D3DA9DBB33FF11C6C0DB700BF73E2E10F8AE086389C0B6BE5F755BE0E2E1781564A13369EF21FC53E029711FFD3F5F0DDDB2BC3FFBB7FC9B5792E2ED390C541C57AC77CE75544DFB0E3D0A443592A090D3C0E9E3B481ACA78AB55A4961DFCB7C53371FA750CBCDEC1374531B8DE3FF6863FBBD8
	970B63F5C6B39D2F73E13C82D86663F94BFDAD18D7B98197B79F7BF821854FB4D64F47BE724C24ED5E8218D7819A960CF54CB757A96340F4ACD4DE5531A0505D7F5910F45AEF8198DDC4C8BAB58CGEF160FCE23F3F16482ED1E9ACD71B18B34F9EA01916D9987F5B98FE01EDAE3A26B0E934A91G89GA9G2BG52813262E01D83A885E8G68G70G048344G4483AC85D89107B1DC7FBE49C75A2CD724EC1624FE0217C6B1A02503B19A48DE2B030432E407520E71593DB6A4DDE03B7BC25229E1CD9C5A3D0F
	66989B6234F69F21782C8735F64F3904F6B7C01D78A046770ADC9CA32BGE7F88863BB5F00E32471A10C6F5B6C24ED9814E3G96BD04FDC4B9306FE500639E46BE6EF3629829F9987BC8F512369EA8C7G44BEBC56B13230DE99A31F9B47672B5BC3528576D5F2C8BAB5A4BE0C3E7A9E4DC1D29F567A6A47943F6AE12D2FAEFC0076DC83F50D71F82EB2D8B5BEFDEF8BC997D85F2410F4EAC808C7FD3FCBF31F24F82D3E29941F9A2F5577FCB56A3B8A6A0C0F601C5051C03FBD027252C7F0DC1FBC763FDC70F15D
	6A95BC3CD79CE4D87A653E3466DDB8C9620675B16D5801E0FD408A57EC6D331E6D3C42F70996EB0FCC736D634157386F9F8F3D46BDCB6F2DF8D81BAC039E697B0D1547C922A7F4F77BF8F9AD7D79F1030CEB72358ABE8739ABE67E38F6E6FC7E9F89C997587F0914EE75A3DF6CFCB97D91747FE63AE6E6BD2A757FF60AB7BC2A757F1CE6791B9C288BFB9463BD2EE5FCE7AECF0E136E25B153B13DA2683A3F46CF5F69941F6E276FBF4C286FC6283B7CD8321EFD0F1534036C079CEE27434D57335F9A64704E68FE
	3ACFCE01362B9EC37BCC2739B6FB1F2DFAAFB2CEFC8EED1CD09E526FC519020F39F32650E32E4394B82F6CB4796715BD2640F9254B244EAB994EEBA26BE28F64906DF23BFE9977090C9B22FD925C8507CD344B78DB29DC845F489306080F20F275D3DC3B043BC3057BC4425DEDA2738628EC973CC8BB4BC44ED03B9DCEA7AB4F21ED9CEE18C6E4FB44D2999D9E2BCA6E87E55CA7F22CCD25E79AEBA04EB64831B6F4C9092F492A77ED17A6BE565E7D8AF87C546FFBA3AD841EDB5704248BBC37969888DDD642971B
	DB7393F0EE7D8B4D41CA9334F3CD964D41EA9234F34D9DF61CEB9A20AEBE817D59619C5F9CF9E51CF48F0C190E69D567225F9CBE2E55370962739F576A7B1CD37E66906AA29E473B331F533321F5D0661EC09BBC23BA0FD829FA3F74414447DB5F0472E0BA7F0C1ECBED81FD8BE51D17D7ABFABE27FA3FD8BD71BA0B934A034DE7568672AE1F153094D02A33B710F38F079BD2AA2B53F11D7E3A5FB9E8686EE176FA161E8B14022D9A1E4038A9205FAE34C359A95BB037C531DB3E6AFDFE73445B70292F00471DE6
	C527D92A77C2734473BEBA21BC583964570F11B5CFA437919C1EDBFE6394A7574F6F702F7F45E9D24FE2220A6E0D87A09E2264B84935533FF78365C4B9CEB29C0A6DDED73D5FE31FF8BBA6FC85BCCE39941D4E2A5ED7B9A71E776FA714870B03F423FF9C7C35D109135FD5795797B6AB75AF1C590C752784FA46446E407FC6671994081395CF60FDCDD533FFDFD3DB143EDA8C017B6A223148ADCC56EFD908FD955B7D7B7ABD9D476C3D7A1C526F1B4D646FAA39797EC27917BEC97FA57BCC10CCBB77159616437F
	2A03B26D74738AED267D0B52323F7FA41C4B854192DE6632C5EE4BF99FDF48BE00AE2C98DDAC77F939DC3947A61A422675D556E67E6601CC9D17554469D9CA5F44E5113C3C094B26E9FB931789A97D00C7920F9CA1FD8810613B468DF3BA0EDD88A2B6CB4686EE1B9330ADC9AB48166496E28399CB7E110D5B24D1D852AFC94909AB74B2F62B8A1BA9F25974DECB6432483D18DF3184BF94AFB475D99DCE3237A9BCA5F88B25BF0C0076AAAA7F62CDAB4036DBC7F96BFB732BDFC87D6FB6DFA627EF62F6F683669C
	6F256BA3EFA33FABA47DE119D35CE60F6000CA400361FD74116D93871D3C194B6B7E187C41841F485BCC38727AC46EAD7A0B440749656F61C62B4FAE92310E1939ACB77993B85CF6D66D343AFB8E33E5789875DE0669B23569B66A91AF37034ACBFAA913FB59712D5C83FB1B57A4E6F23AD35C6BCE41EABBE56D9285EF9337D934FBF91FDDF05A86BC9F56510EF27A09BB5F202E74F23BE4750932E59658C34AC2DF9D189739F56D56D3602A81AFF71836A45F251449ADEC75F62B97D8715EA6117CC99F413BDF2E
	3B659D3178C7275F6E50F1991212446DBB8640157AEE2B4B619C041786302D50C38362EDA8321D1D9C714498E527C09F7197AFAFDF70CBE37ABA686C9417CDFF6998C4B2A82DBE6AF5F5725EA989B38A5E08CE1BC6A47654E1GC24F1BE1E76C76115F68064E8BB5BAE315ACFA59BD4B0F4CF86455FFD35195D2115B431C842407FBF841F558545D6DF073F28760B1683EC06E1E34A9533459BEB07D2D6667DF6B0488EB60B2DDC70E501F6574E78579D9CEB16594D3DE81EECB67EE7ACFDBBF00BA53302405F7C8
	8C8D1037DD2C76CD3F40A258377CA30420F74AFEA2255D64C7A17931179295511F45E31612D015E8247B6700F49FFBF0C4FD9F3BE1F2FE50BB5D2CA5EC4F183FC4390F157EAECA9B7D0698701EA5F8DEFA2C0D7C3EF830334EF3AE2B53A9DA1DD7CDEE9BB977BCB1A8722A5FABECA37D7B300DD4496A7E5CC6643E222279BFD0CB878871450C96D599GGC0D0GGD0CB818294G94G88G88GB4F954AC71450C96D599GGC0D0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4
	E1D0CB8586GGGG81G81GBAGGG0F99GGGG
**end of data**/
}
}

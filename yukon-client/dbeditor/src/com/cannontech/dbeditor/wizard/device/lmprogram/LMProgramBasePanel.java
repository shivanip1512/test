package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.database.data.lite.LiteComparators;
import java.util.Collections;

public class LMProgramBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	//This is for the timed operational state
	private boolean isAWizardOp = false; 
	//Control Methods
	public static final String TIME_REFRESH_CONTROL = "TimeRefresh";
	public static final String SMART_CYCLE_CONTROL = "SmartCycle";
	public static final String MASTER_CYCLE_CONTROL = "MasterCycle";
	public static final String ROTATION_CONTROL = "Rotation";
	public static final String LATCHING_CONTROL = "Latching";
	// Stop Types
	public static final String RESTORE_STOP = "Restore";
	public static final String TIME_IN_STOP = "Time-In";
	private javax.swing.JComboBox ivjJComboBoxOperationalState = null;
	private javax.swing.JLabel ivjJLabelOperationalState = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JLabel ivjJLabelActualProgType = null;
	private javax.swing.JLabel ivjJLabelProgramType = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxConstraint = null;
	private javax.swing.JLabel ivjJLabelConstraint = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramBasePanel.this.getJComboBoxOperationalState()) 
				connEtoC1(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMProgramBasePanel.this.getJTextFieldName()) 
				connEtoC2(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramBasePanel() {
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
	if (e.getSource() == getJComboBoxOperationalState()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldName()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxOperationalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		if(!isAWizardOp)
		{
			/*java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   
			//This makes sure that the user applies the Timed state before going to the control window tab
			StringBuffer message = new StringBuffer("You have selected a Timed operational state.  Please click \n" + 
													 "the Apply button before specifying your control times or \n" + 
													 "the control window panel will not reflect the current operational state");
			int optional = 
						 javax.swing.JOptionPane.showConfirmDialog(
								 this, 
								 message,
							  "Changes should be applied.",
							  JOptionPane.OK_OPTION,
							  JOptionPane.WARNING_MESSAGE);*/
							  
				fireInputDataPanelEvent( new PropertyPanelEvent(
										this, 
										PropertyPanelEvent.EVENT_FORCE_APPLY));
		}
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
 * connEtoC2:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxFallAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
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
 * connEtoC4:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSpringAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
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
 * connEtoC5:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSummerAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
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
 * connEtoC6:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxWinterAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
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
 * connEtoC8:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxConstraint() {
	if (ivjJComboBoxConstraint == null) {
		try {
			ivjJComboBoxConstraint = new javax.swing.JComboBox();
			ivjJComboBoxConstraint.setName("JComboBoxConstraint");
			ivjJComboBoxConstraint.setPreferredSize(new java.awt.Dimension(204, 23));
			ivjJComboBoxConstraint.setMinimumSize(new java.awt.Dimension(204, 23));
			// user code begin {1}
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			java.util.List constraints = cache.getAllLMProgramConstraints();
			Collections.sort( constraints, LiteComparators.liteStringComparator );
			for( int i = 0; i < constraints.size(); i++ )
				ivjJComboBoxConstraint.addItem( constraints.get(i) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxConstraint;
}
/**
 * Return the JComboBoxControl property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOperationalState() {
	if (ivjJComboBoxOperationalState == null) {
		try {
			ivjJComboBoxOperationalState = new javax.swing.JComboBox();
			ivjJComboBoxOperationalState.setName("JComboBoxOperationalState");
			// user code begin {1}
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_AUTOMATIC );
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_MANUALONLY );
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_TIMED);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOperationalState;
}
/**
 * Return the JLabelActualProgType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualProgType() {
	if (ivjJLabelActualProgType == null) {
		try {
			ivjJLabelActualProgType = new javax.swing.JLabel();
			ivjJLabelActualProgType.setName("JLabelActualProgType");
			ivjJLabelActualProgType.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelActualProgType.setText("(unknown)");
			// user code begin {1}

			ivjJLabelActualProgType.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualProgType;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelConstraint() {
	if (ivjJLabelConstraint == null) {
		try {
			ivjJLabelConstraint = new javax.swing.JLabel();
			ivjJLabelConstraint.setName("JLabelConstraint");
			ivjJLabelConstraint.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelConstraint.setText("Program Constraint: ");
			ivjJLabelConstraint.setMaximumSize(new java.awt.Dimension(131, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelConstraint;
}

/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOperationalState() {
	if (ivjJLabelOperationalState == null) {
		try {
			ivjJLabelOperationalState = new javax.swing.JLabel();
			ivjJLabelOperationalState.setName("JLabelOperationalState");
			ivjJLabelOperationalState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelOperationalState.setText("Operational State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOperationalState;
}
/**
 * Return the JLabelProgramType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelProgramType() {
	if (ivjJLabelProgramType == null) {
		try {
			ivjJLabelProgramType = new javax.swing.JLabel();
			ivjJLabelProgramType.setName("JLabelProgramType");
			ivjJLabelProgramType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelProgramType.setText("Program Type:");
			// user code begin {1}

			ivjJLabelProgramType.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelProgramType;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Name of Program");
			// user code begin {1}

			ivjJTextFieldName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramBase program = (LMProgramBase)o;

	program.setName( getJTextFieldName().getText() );
	program.getProgram().setControlType( getJComboBoxOperationalState().getSelectedItem().toString() );
	
	if( getJComboBoxConstraint().getSelectedItem() != null )
		program.getProgram().setConstraintID( new Integer(((com.cannontech.database.data.lite.LiteLMConstraint)getJComboBoxConstraint().getSelectedItem()).getConstraintID() ));

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
	// user code end
	getJComboBoxOperationalState().addActionListener(ivjEventHandler);
	getJTextFieldName().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramBasePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(369, 392);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.ipadx = 11;
		constraintsJLabelName.ipady = -3;
		constraintsJLabelName.insets = new java.awt.Insets(35, 9, 14, 6);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.gridwidth = 3;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 279;
		constraintsJTextFieldName.insets = new java.awt.Insets(35, 7, 10, 13);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelOperationalState = new java.awt.GridBagConstraints();
		constraintsJLabelOperationalState.gridx = 1; constraintsJLabelOperationalState.gridy = 3;
		constraintsJLabelOperationalState.gridwidth = 2;
		constraintsJLabelOperationalState.ipadx = 3;
		constraintsJLabelOperationalState.ipady = -1;
		constraintsJLabelOperationalState.insets = new java.awt.Insets(10, 9, 13, 0);
		add(getJLabelOperationalState(), constraintsJLabelOperationalState);

		java.awt.GridBagConstraints constraintsJComboBoxOperationalState = new java.awt.GridBagConstraints();
		constraintsJComboBoxOperationalState.gridx = 3; constraintsJComboBoxOperationalState.gridy = 3;
		constraintsJComboBoxOperationalState.gridwidth = 2;
		constraintsJComboBoxOperationalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxOperationalState.weightx = 1.0;
		constraintsJComboBoxOperationalState.ipadx = 101;
		constraintsJComboBoxOperationalState.insets = new java.awt.Insets(10, 1, 8, 14);
		add(getJComboBoxOperationalState(), constraintsJComboBoxOperationalState);

		java.awt.GridBagConstraints constraintsJLabelProgramType = new java.awt.GridBagConstraints();
		constraintsJLabelProgramType.gridx = 1; constraintsJLabelProgramType.gridy = 2;
		constraintsJLabelProgramType.gridwidth = 2;
		constraintsJLabelProgramType.ipadx = 5;
		constraintsJLabelProgramType.ipady = 1;
		constraintsJLabelProgramType.insets = new java.awt.Insets(10, 9, 10, 19);
		add(getJLabelProgramType(), constraintsJLabelProgramType);

		java.awt.GridBagConstraints constraintsJLabelActualProgType = new java.awt.GridBagConstraints();
		constraintsJLabelActualProgType.gridx = 3; constraintsJLabelActualProgType.gridy = 2;
		constraintsJLabelActualProgType.gridwidth = 2;
		constraintsJLabelActualProgType.ipadx = 151;
		constraintsJLabelActualProgType.ipady = 4;
		constraintsJLabelActualProgType.insets = new java.awt.Insets(10, 1, 10, 16);
		add(getJLabelActualProgType(), constraintsJLabelActualProgType);

		java.awt.GridBagConstraints constraintsJLabelConstraint = new java.awt.GridBagConstraints();
		constraintsJLabelConstraint.gridx = 1; constraintsJLabelConstraint.gridy = 4;
		constraintsJLabelConstraint.gridwidth = 3;
		constraintsJLabelConstraint.ipadx = 5;
		constraintsJLabelConstraint.ipady = 6;
		constraintsJLabelConstraint.insets = new java.awt.Insets(9, 9, 212, 3);
		add(getJLabelConstraint(), constraintsJLabelConstraint);

		java.awt.GridBagConstraints constraintsJComboBoxConstraint = new java.awt.GridBagConstraints();
		constraintsJComboBoxConstraint.gridx = 4; constraintsJComboBoxConstraint.gridy = 4;
		constraintsJComboBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxConstraint.weightx = 1.0;
		constraintsJComboBoxConstraint.insets = new java.awt.Insets(9, 3, 214, 14);
		add(getJComboBoxConstraint(), constraintsJComboBoxConstraint);
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
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}


	return true;
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
	LMProgramBase program = (LMProgramBase)o;

	getJTextFieldName().setText( program.getPAOName() );
	getJComboBoxOperationalState().setSelectedItem( program.getProgram().getControlType().toString() );

	getJLabelProgramType().setVisible( true );
	getJLabelActualProgType().setVisible( true );
	getJLabelActualProgType().setText( program.getPAOType() );

	for( int i = 0; i < getJComboBoxConstraint().getItemCount(); i++ )
		if( ((com.cannontech.database.data.lite.LiteLMConstraint)getJComboBoxConstraint().getItemAt(i)).getConstraintID()
			== program.getProgram().getConstraintID().intValue() )
			{
				getJComboBoxConstraint().setSelectedIndex(i);
				break;
			}

}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}

public boolean isTimedOperationalState()
{
	return getJComboBoxOperationalState().getSelectedItem().toString().compareTo("Timed") == 0;
}

public boolean getIsAWizardOp()
{
	return isAWizardOp;
}

public void setIsAWizardOp(boolean wizard)
{
	isAWizardOp = wizard;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G7AF2E5B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BD0D4D716CE054CDA154CECD2E3F64844ECE5554C128C191808099A0CE850E0A40A0B3FC91CC8A66C2E29510C3B135AE2126C64528D8A918584130EC0C4D19793ECC0C94590227CC49054D60902E1929F7403FE525D2F6DFEC0E3B2491EF36F3B6FBD1AEE10D46D56D8F5386F1EF34F39671EFB6E39673C57127C3D8F24271A7289C9CDA76A3FCFD289297A1410951F0A759C1772E82219987E6D83
	48A5DBEF26C11BB1B3515C3EB1511CC36C0E8C6897C23F6B65C473FB701CC7A4C7D5A5BC0418A78BE8CB7A7C87471947416759C67B1656ACE8F78294832E91602E19B8FFC52BC545BB203FD3FDC6C8B591527E980EF9712A4500DF250EAB85DA8DC08A9D535ADA285227C29F47C4C19B77989B1B84EDA6556E766A636A382E3BA3486B4F9518F660DC86799CEA9FE73CF635ADA65329A14CE4F5D1B334B5367E3E93BD324B5AA3385D32DB91FB9CD6FB37E8179459EB9D12EE8ADE3B55AE8ECABD22556972F865BE
	2F602A2EEEBBEBE30F7502CF3489EE51396AF154BF754B8CAA9B74077E35D9089B7E92EDE601678A0066D9FC8DABF75428BACE690FA0C953F706DC430CD9CC7738D97A9A2A7E12116E0FC2798A5D93644B00FEADG53A53A156B929D45F5D9F385473E8E7D2C5909668333B97E7E8E4457C03F9D40B91B6B78797FF09DBB3EF91224F41FB0E50750B1ECB653ADF2362E631DE9AF1B5E27FEF33F04FB3F91E8D9CFA41A0B819A816CGE1G8554DF4FBABE04362EDBE910DDAE59DD677154BB6D5E3332DDF442F3F5
	B5D0D45CC531C7765A89E1BE39754B32A07AF0187A44C8FFC4E0EB3BC2388FC5DE7CB5D114AAD3FA1035C52DAEB24DD4E544BE212FAD6589345BE63A3749700CFC484F70A994DF73240EE7FB75BD45E35F863428A7391DBBF6F2DD5A5226111C4ADA5336A03A04AF7A58E41943F42818236B42ECBC540FB66E823C9F60AE008800B8000DF3380DFF6EB8B61E0DDB608CF85D027352C037C770AA75831253AEFAD5DB5FEC9D4B56410071CD1EB09F33CB44B1639E2D1FF5B228DD56C79CB0254DE576A81C2B5B054D
	3DB0645CB5F3C77B46CEED8F4F50B3E11B4B76502C615BA97E1A814F76F6BDBDDB5877GED95G9B1F5B0978637382FD2410625B66857A48748E6EA35700968B4076F7378BE3E369D3F0F6G3AG9400A9GB381E21F62FB7CBBC779AD1EA31BE1FF996FAF25F721957D0AD7E8F17B94415DA3DE963C12506D94FDC49D732804F66D9E4197C54FF78F4CF1DAF40BDEC111E4B7C897DCA2B8130F0D19AF6562982FC8296D12CB0489DC9E4265AE167220F58ABE658B0FDDD0C4FD8423BFAF21CF8C8A17C1A2880067
	7CE834D7910D394930FE924DEDD56FC6DC8774BD515CAE4D57F7C15B8A4EADBA2B2B9B259ED4C3708EF39F7DA315938663336673F379DA87773F469D0F103255A7027A9F0DFF7319AFCD1D9F68FFFF26F2E3811FBC1F5F2337FF59B10E7F239E46FE567C603E9A7A4C7D6338F318837A6993B81F4C4EB3E574553427A1268354BE4DEDFD5B75E26603F8BFF22FBB6F8507916D3A655958C97DF827080322DBE994BD225B8E8FBEF54F7F2EBFD37747DED973151FD0BC462786F0981F067FD9FFAE43038C23F7F37A
	FD14AEA80AD76A9ED044CB2222C86EBE1DFFBA5545B7A274795435BE6E4135F6409ABDG13343516B9738CFE55A64B7D831EF6BCB12AFF2E23777094989F79B45FFFDB8766D589DA7FAA6D6B745FDD99EF6F02814A3BF57FF873874D9168B3E34F71DEA72E1D47C9065BCFF1DBC0D74B823E5E07BABA8C63A62A532D6932084EE11E201F5FAAB0DF586B8A6D4B07DC41FD394C955C170F3A467655EAD7B0DFE534BA972ED19EF29BFC3F4905712FC7F63BC5FAGB596427261575AF19D12DBD22EC876BED139A45D
	94A11FDF00BEDCC5E3D8ADBC37AF6031F12804474683B79FA2B1DF3F9FB4FF8AFF7038A9F281734B1885FAECE4BA1DF66319921D228B8C55A08F3895B5972471DB457B4C9457EC39093C25E760DE6B166BE57FF90FFA53C032224005D1A650F5D9D9E3933D3D3257A55ACB2F8B0302D598D22CF4DF2CF514FC8A1F8F979FB9F222F8F4B2DF56ABF9459637E7C03D0ACA8FDFBEDC41FCA70F641DF4B2BD5AE1AF1BA451E9BF8777DCD98F849D45CA7F2ACCB82F5F6A9B02DDD327EEC06A04E72EE47167DFEF202D12
	40062F83E445707833540DF49EFF54A05BA0BB1DEC1B5137E2E80D5615EF581783B12CFE5B29513BC25259BCCF48B968EF620D819152828CF02D62B03F5B65DD81344B02F3C04F99964B5B21F59AE2A3434F71845F7F27BD988F7BB5790BBCDB895E7DCCA6735719824A74F8A597DCA5986C55BA738F2D46BAF38E2C83EB5294E83396A61A599EBE78AD6ABB5A7CD0ABAD24B9DEA72FC38FC03F8620EBA113B3F5A133899BE7394A47E5AC02BB9020F65148F15FAE22F52DD69716EB38EE0AFB9102536DE9BC5FBD
	1BE32CEB6D3E4A20E70A1E25C5CC7E2AC501F5409E9F4AFD9D70E94FF09F2939E13C236AFCBE39C722C7C75D075F5FB01EB7065BF2639DE8419FFDD09358C5BF5F4B7F3A013AE21E2622557D7855C71F1351F967A9DAC9739420E108FAFF3B5CG29E2B338C303960EAECB629061F678EB1331B60E4EBEE45A9E428E16E7587A0FBF235B4186F5A753695EEA2CF7025578BDD4C9EB5F00E49DD0A4CC8885C520870FB9822B21BB5BB25475791F6135726D6751E7E7C2BF8EA08900656DFF6D64F9FB8A60AAE2A1CF
	20674A721E71DCB9E2D99EF0D7AC7745644E239867A79C3B385DE38F6FE27B9490B738AD2765552AF3477FFA9A2979FEDB509AFEF2F62EE9C6AC33E1FC2CEECB4B0C9526B2G36463A2BFC0D164509E65BE23E46DFB49857B8F9B1DB634C45FC0D91D7516FA798A2F91DFE54F8A6E2128E06BC93310B195E6B96871E09BFAB74CCGBEE389BF93B73C3906B3F1913CD0F077C1E4526EF2FA6FC08CD27BA53EF49E03884FFFBF9B44F597024C63GB6G8740E4ED0E07FD63565B277CE3545BDF3585562E91CB749C
	AFFA4968FC0F55163BED38CF89CBD8ED192B618FD17CDB86BC5B57BB28CFE33F7459C4F35433BCAF3B2DB3F07E5A3811F3F68574995F5D23782C817DC981FC4C177B1C857C3C9ADC233ADA3B7355F3B95B4263E9F89C2F796E9E42D81CG7DCDGE9CBA19E83B482DC8388DB4A7764CF3EE0F1EA641E048AD2FABD4156F97A6A8F334F972378826DB3E3E9B07B9CACAD8CEE9FAD97E176D9594063D674D2EE1F15FEFA86201F769C3745E5E507618C58E4492DE87EEF9E40BBD776CAFD121B63B2A82EC776524412
	619A86C3673CE78603673C364160B96F6541316B372F86C365442C569A2071BA9F56D78A40F0974FA12E8D7A766738DF97580A211D8C7D87G228112GB216819FC0B300FD999FDBC64706ADE3777B0CE5AC9F0805B699A0ADBE51FCA01E0F3D7B7C2400FDB4824AB776F1DEBE9F4721BE76E5216BB8545958CFFAEE74D826CB54183AEC8F42033E109D60F339E348882C55B2030CA92263163E1BCD77556793FD74F061ED2B527EDD59CD0254A518D4931EF34F6B4CD4471CD26406CAED0D73ACE33DD7EC0C9FED
	9B75BD40D0E81F1DB6945CE7A30650BEBEF9405BA3867A6523C3960D26C70B96C5F461B254DC1772AB0297EA2BC07E85141F51C271478D0572FDE6236F2E9AEDF4D433511A8634D1C790DBA0B8C35AE80D9F6F883F56FF01760735796EEC43D86094066581889A1B62693B4BB61EFBCF02FEB8C08C402AF8168F30987658907ADEAF245D207EA50F58C3621EC7DE699C672D02FEB30083E0B2C04473BC0F38B804B96919B621DBF4B287E1788D8710EFE4C546E93F5F7B2E4693D8B272B1E7AAB0C6062CAC35B50B
	36DCCD16DABB348FFBF47A9D9F64EBF448558684BA8A07B07ACFACB99A9D765987BE86A11751B659720CBA68E435BE335B0CB9D64EF2E6532A65BC87AB3A900C5E2C51A32A03513BB47A635941689E0DFE64D8B0FA18C65FEF8E166706AF67F1F825054761C88D37195ED3EFE1FF854735B772F145ABF8EE3D2C0DFF6372GAEE28577254AEBA36EC34C142B2BCFFBA5FB3D5057C6BD135D07F9E779FB282495BC0F2FBBCB3F4BA5C02E8E501C405FEB788F715AA3721753C85B2413C13F4BA5359FB2DDCBE0E7A7
	ACC14FB79F005A034D797767781CB1C0CFC9C05C7CF3C69B613E671A595D4F68FF98C1C7DF43982953771DB752D15FD7CCC67A2591F4345191151F2DED59C13E3637164DA44E59C7C2FFB7DD29FEB7D55BC9AB7610C9D793543A6A0F3D9692B48F211E4D56FF1FADC30DC1D3D772EF126D97B8EE15065BD04DF1D97F42F12FE6F3DC2306339F4BD057712D066BB473F1D100ABCEC45CC6CBE31075986F39ED0467D39037000FG5FEE95178F4F4B087EAEB20F24B5E23CD2E45959AEF9B04290C7A2FEABFFF68FDA
	144D595FB8561CFC2EEDEA9B19B8F24E98F8AEB731B9D94CDEB9AA6F9F8F985F069F48E7B9BBD1BED6CF641D43B3941F486A09D48DDFC8716B8CF8F60ECF1F6567B899E84D2BF8EE7AF0817AD3A47463G12GD2GAC2BA13E2D66396A574395E41C3C3DDD763441256EB4240EECFDD34E7D306F0F8B37717392B56FE1B267471BD3C33D07E8DE4D4E09C3EDAD4B77D0BCDB5F67C5383EF020C5812C8358821036A651DC38062F6F1F074B587A7015C91D2288BD8E7AB606AF8EDF1068E83CB8595A9E309957D631
	064D5F28369DEBC265071D970CFCCEF55CA4351D9212AF395A4897210E0BD65B0410FC2FE59B791255F1A9EA1B35B6941FF44C48D72C0E2BD25BE6E83B56862B313EB2F3BECF9059E1008B8F406F257BGF9BA617F361C9C0DA36A797FGC92A1F3C1C457D2470D753C9635A4D29A5A17CA4D21D732D357A1D51F5ADA31D49D9712571DDC74EAFF61BD2C34829DA4778EB5769F202694FF4FD3E18E8F6F97F1C7E3C5F70FC766C8F799EB431B8537BFFBA87BBC7338AC26776738A026776B185416B51A585E3FF03
	D9DE907A9B4C6A8263B79875F7B07B70FED6E04F6E8208820883D83F0E47001E427D044600A0677F143DCF24C92100C53A7ACD27A874DA4F960D7FAD7FF29166535D322248AE144F71DDC538DF0A6C21C806930A50C6DE294F213021AA3E2F887D4DA976D2B44B611E24F7FF8A2CAB63B7BC2FFB19628A21DF26611AAE70DC6F380673DC603C13B5DCE5BD6226C2BFD2437DC3BD671D23615E2439C4468BF0F73C40F11F536F2FE5502F51F053DAB9EF3306CB4F613C619A6E8EBAAE927A519AEE0B469B2761A651
	5C4472A25473AFF25CF7FB90D7837DB68D774A9E4E5B2161422C080B00FE0C069B20F94DAA68EF51F075651CB7EDBD47DD27E73D9D7A4E757CAEFDF3BF66A21320BF95A086E09D40A600345F02DDG4AGEA81BAGBCGE1G61G11G51G89G493F55FCF26FE1E8AF8B57C73F5187241AEC9B2A956E6B4281227948C3FB8F0612811EFD86D248735D572168ED977AB41E877705766D48FD637B766CFD741B8678A456DF6A795FC75FE975767AC46D8CAD58172361DAFCB659A7E16547F23F65B67C8E55AB3B15
	2A17505E494C2F41B6E9AF711AB4050ED1C09996EB17535FD4323C70AF97464A8B4D81C08CF9A14D19DEE23F65FC736D375FE6735FCB4FC698BC5F254D5FDCBFF27E7FB8244F7FC87DF8739BF3614079E3DEE26F35E07A3759DC3BBE9BE90F524FD01FF8F8DE2769535EBAD21F7ED2DD1F392D636913BE06BE999B924D5BD59A1B5FCA4FECBE600BB7707927854C7F53437A7C5B5A462B8D461A5F86F3142732BB1F4D7573E3A36D71880D83DDB04E2169F34F1E117AAC79D057670DBD6359A31368F5C320BE612A
	BDF6E87A5CE7453ECB70CB2E81977D004F741AC5E34C8C98BFD3536BCEB372F93CE22F68750AF67639DF9D7FA00DBF6BE16C468D3C2E6AAC9FCB57CC225B85A1BFE9E40DD31A245782EC7E9F51F710AE499D206B3DB426D541785AA49E0332CBC24702F7CB460F85FBCB025FF387CBC65FF39F16843F67AACA0C779C43FDDC02EB6B039858210D2B56F01D9A6EA4150938AB12DDF1E878D3D4AF44B70BB8A14737D23D06A82E033FB3B760BAD55C97A5984FE0498ED9FB9FFF35047A2464F42A6F4C6998498D61CD
	33C78F55D17288863D9D9A8E75E63EDA00A5173ADF2BED181B384521E26D23E493B8FE69FE3BA4B8653E7DE54595E54515D507F9CE7C4010B117BD7E4EC7A13F5BB913545AC3EDB701C6EC7E6770D52F7AFC23CA5765E07D2D6A42F827FC2C4B597209AE2732F5E2F272BE5565941C5065FC5F3AAF381CBAF0C4A70829D445283C97CF6AF2326AF5B95EBD9353273CC1173336D11713EE5E9BA827D102EC92FFB8D9DAF98C34B9FA6C304A7746A91D6F1372B1780ED6D4B13E50F5C651E9DD1F5AE6DD6E5CAB37A2
	17658D1B1BD16E9C7005D55A793FD67191891D03583C32C774AA438CCBFFF9227A74FFB623BFE979BBA32934BFB5679376FBA7302DF4133E6F3D84835DFD6C7FCF3CB374AA9C1C484DA4DDA15978C239D86EADD6DF7A7A3CA4DBDBC98BBDDAC903472A4D2FCCB71BC8DA8B594AC2CD8BC943F851C252E9B8E9A1E688B5FE0FEABAFE945E715DA05F519E86837EA126C4A169B8EB35C2F6995EC797539752005C212AD20C598D74B3CB875C7DEEA8B78EABA41F5374342C3A98F45EB1422B967F53477F967F52E51F
	19ECEDA16F63A5F6C974D2DB08F67CBE237E503609E4FA7043A006E8D8100FB669BEE558A936117C5E0B78239F1FA25A6B3815FD8A595D06B31563AFAB4A1B851FC341E87CD99BC9F363CF0249AEC6F68A6E3EF2E6724F28D5D26B2ED1F7C22B9050B70F6A0BD22C1A143C27328F6EBB13BC47CCCCCD649D27AC581B049E5876961229B83C224FA1BB6DFE4F49DAAAA8E3888DCDF38690D720AD9F97EB45F7F91A527D7E7B6F5C31F1C7138952AEAFA927A3714E36EAA36C8D8E418BD3095E968537C3769E50E8BF
	7AC0B9747C69773ACCA4D5DDA4EE69F63F8776856C6F76614FB9C00362G8DB8C993E47D59520AFB9EFA63AA952433AAE4AFB39930167709322B3C2E37D7F20B1AG309F08AF52446398EB4018DD7E3BF71FD97DF6B76CF7A3B13BAAAA685FCA7A77A87E2D2418CA0A29BC8AC65CC2467C97C877D3C74ED61813E8E78437595A84BB253C30F9693497EB36EC0255B511F47AD38AB0F60EBE6A5C002BDB747EB8721E226DD3126E403D7020CBF039DBFB8517649C0607C69A8F7D085F6B574E8FCFC47042AB21584C
	D1DE05589D235D8351391418C77F3EBB6145A31705726E41897121E8242607F4CDF790FECD72737FB72569917F0818043A650554B4B50426E95A363124054D08237EBF16704948A5E01B7337E96C933A262B081EC5E742815AB57A3A2C7B69EF56C6FE5D18EFA2A638E79CA23BD85207B02F24C194EF2F5B2844DA35972F49DFC3742F7619A347906E10073FB766EDA937E7054C5B42B72A7F67EBE3606F2D7E7BA6DAF0A36053DE557EFF57CD7C96956A9D40A51760F4AA02333F4EED47778175430A283FE7B3FD
	0372FDFC0CCAE4344CEFD06F6B861E7F85D0CB87880FF324997F94GGDCB9GGD0CB818294G94G88G88G7AF2E5B00FF324997F94GGDCB9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB995GGGG
**end of data**/
}
}

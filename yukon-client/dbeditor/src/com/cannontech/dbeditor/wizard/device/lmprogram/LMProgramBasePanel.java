package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import java.util.Collections;

import com.cannontech.common.gui.unchanging.DoubleRangeDocument;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.data.device.lm.LMFactory;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.device.lm.LMProgramDirectBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	//This is for the timed operational state
	private boolean isAWizardOp = false;
	private javax.swing.JComboBox<String> ivjJComboBoxOperationalState = null;
	private javax.swing.JLabel ivjJLabelOperationalState = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JLabel ivjJLabelActualProgType = null;
	private javax.swing.JLabel ivjJLabelProgramType = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox<LiteLMConstraint> ivjJComboBoxConstraint = null;
	private javax.swing.JLabel ivjJLabelConstraint = null;
	private javax.swing.JButton ivjActionPasser = null;
	private javax.swing.JPanel ivjJPanelTriggerThreshold = null;
	private javax.swing.JLabel ivjJLabelTriggerOffset = null;
	private javax.swing.JTextField ivjJTextFieldTriggerOffset = null;

	private javax.swing.JLabel jLabel = null;
	private javax.swing.JTextField jTextFieldOffset = null;
	private javax.swing.JPanel jPanel = null;
	
	private PaoType programType;

	class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		@Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramBasePanel.this.getJComboBoxOperationalState()) 
				connEtoC1(e);
			if (e.getSource() == LMProgramBasePanel.this.getJComboBoxConstraint()) 
				connEtoC3(e);
			
		};
		
		@Override
        public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMProgramBasePanel.this.getJTextFieldName()) 
				connEtoC2(e);
			if (e.getSource() == LMProgramBasePanel.this.getJTextFieldTriggerOffset()) 
				connEtoC4(e);
			if (e.getSource() == LMProgramBasePanel.this.getJTextFieldOffset()) 
				connEtoC4(e);
		};
	};

public LMProgramBasePanel(PaoType programType) {
	super();
	this.programType = programType;
	initialize();
}

public void setProgramType(PaoType programType) {
	this.programType = programType;
}

/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
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
@Override
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
			getActionPasser().doClick();
			
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
							  
				/*fireInputDataPanelEvent( new PropertyPanelEvent(
										this, 
										PropertyPanelEvent.EVENT_FORCE_APPLY));*/
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
 * connEtoC3:  (JComboBoxConstraint.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextField1.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * Return the ActionPasser property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getActionPasser() {
	if (ivjActionPasser == null) {
		try {
			ivjActionPasser = new javax.swing.JButton();
			ivjActionPasser.setName("ActionPasser");
			ivjActionPasser.setText("");
			ivjActionPasser.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjActionPasser;
}
public boolean getIsAWizardOp()
{
	return isAWizardOp;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@SuppressWarnings("unchecked")
private javax.swing.JComboBox<LiteLMConstraint> getJComboBoxConstraint() {
	if (ivjJComboBoxConstraint == null) {
		try {
			ivjJComboBoxConstraint = new javax.swing.JComboBox<LiteLMConstraint>();
			ivjJComboBoxConstraint.setName("JComboBoxConstraint");
			ivjJComboBoxConstraint.setPreferredSize(new java.awt.Dimension(204, 23));
			ivjJComboBoxConstraint.setMinimumSize(new java.awt.Dimension(204, 23));
			// user code begin {1}
			IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			java.util.List<LiteLMConstraint> constraints = cache.getAllLMProgramConstraints();
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
private javax.swing.JComboBox<String> getJComboBoxOperationalState() {
	if (ivjJComboBoxOperationalState == null) {
		try {
			ivjJComboBoxOperationalState = new javax.swing.JComboBox<String>();
			ivjJComboBoxOperationalState.setName("JComboBoxOperationalState");
			// user code begin {1}
			if (programType == PaoType.LM_NEST_PROGRAM) {
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_MANUALONLY );
            ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_AUTOMATIC );
            ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_TIMED);
			} else {
    			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_AUTOMATIC );
    			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_MANUALONLY );
    			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_TIMED);
			}
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
public javax.swing.JLabel getJLabelActualProgType() {
	if (ivjJLabelActualProgType == null) {
		try {
			ivjJLabelActualProgType = new javax.swing.JLabel();
			ivjJLabelActualProgType.setName("JLabelActualProgType");
			ivjJLabelActualProgType.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelActualProgType.setText("(unknown)");
			// user code begin {1}

			ivjJLabelActualProgType.setVisible(true);
			
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

			ivjJLabelProgramType.setVisible(true);
			
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTriggerOffset() {
	if (ivjJLabelTriggerOffset == null) {
		try {
			ivjJLabelTriggerOffset = new javax.swing.JLabel();
			ivjJLabelTriggerOffset.setName("JLabelTriggerOffset");
			ivjJLabelTriggerOffset.setText("Trigger Offset: ");
			ivjJLabelTriggerOffset.setMaximumSize(new java.awt.Dimension(104, 20));
			ivjJLabelTriggerOffset.setPreferredSize(new java.awt.Dimension(104, 20));
			ivjJLabelTriggerOffset.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTriggerOffset.setMinimumSize(new java.awt.Dimension(104, 20));
			// user code begin {1}
			ivjJLabelTriggerOffset.setToolTipText("Any postivie float value is valid");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTriggerOffset;
}
/**
 * Return the JPanelTriggerThreshold property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTriggerThreshold() {
	if (ivjJPanelTriggerThreshold == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Trigger Threshold Settings");
			ivjJPanelTriggerThreshold = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints2 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints3 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints4 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints1 = new java.awt.GridBagConstraints();
			consGridBagConstraints2.insets = new java.awt.Insets(10,11,0,2);
			consGridBagConstraints2.ipadx = 3;
			consGridBagConstraints2.gridy = 0;
			consGridBagConstraints2.gridx = 0;
			consGridBagConstraints3.insets = new java.awt.Insets(10,11,10,2);
			consGridBagConstraints3.ipady = 1;
			consGridBagConstraints3.ipadx = 11;
			consGridBagConstraints3.gridy = 1;
			consGridBagConstraints3.gridx = 0;
			consGridBagConstraints4.insets = new java.awt.Insets(10,2,10,150);
			consGridBagConstraints4.ipadx = -10;
			consGridBagConstraints4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints4.weightx = 1.0;
			consGridBagConstraints4.gridy = 1;
			consGridBagConstraints4.gridx = 1;
			consGridBagConstraints4.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints3.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints2.anchor = java.awt.GridBagConstraints.NORTHWEST;
			consGridBagConstraints1.insets = new java.awt.Insets(10,2,0,150);
			consGridBagConstraints1.ipadx = -10;
			consGridBagConstraints1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints1.weightx = 1.0;
			consGridBagConstraints1.gridy = 0;
			consGridBagConstraints1.gridx = 1;
			consGridBagConstraints1.anchor = java.awt.GridBagConstraints.NORTHWEST;
			ivjJPanelTriggerThreshold.setName("JPanelTriggerThreshold");
			ivjJPanelTriggerThreshold.setPreferredSize(new java.awt.Dimension(344,120));
			ivjJPanelTriggerThreshold.setBorder(ivjLocalBorder);
			ivjJPanelTriggerThreshold.setLayout(new java.awt.GridBagLayout());
			ivjJPanelTriggerThreshold.add(getJTextFieldTriggerOffset(), consGridBagConstraints1);
			ivjJPanelTriggerThreshold.add(getJLabelTriggerOffset(), consGridBagConstraints2);
			ivjJPanelTriggerThreshold.add(getJLabel(), consGridBagConstraints3);
			ivjJPanelTriggerThreshold.add(getJTextFieldOffset(), consGridBagConstraints4);
			ivjJPanelTriggerThreshold.setMinimumSize(new java.awt.Dimension(344, 68));

			getJPanelTriggerThreshold().setVisible(false);
			// user code end
			ivjJPanelTriggerThreshold.setEnabled(true);
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTriggerThreshold;
}


public void setTriggerThresholdVisible(boolean visible)
{
	getJPanelTriggerThreshold().setVisible(visible);
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
						PaoUtils.ILLEGAL_NAME_CHARS) );
			
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
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTriggerOffset() {
	if (ivjJTextFieldTriggerOffset == null) {
		try {
			ivjJTextFieldTriggerOffset = new javax.swing.JTextField();
			ivjJTextFieldTriggerOffset.setName("JTextFieldTriggerOffset");
			ivjJTextFieldTriggerOffset.setPreferredSize(new java.awt.Dimension(72, 20));
			ivjJTextFieldTriggerOffset.setMinimumSize(new java.awt.Dimension(72, 20));
			// user code begin {1}
			
			ivjJTextFieldTriggerOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( 0.0000, 99999.9999, 4 ) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTriggerOffset;
}

@Override
public Object getValue(Object o) 
{
    LMProgramBase  program;
	if (isAWizardOp) {
	    program = (LMProgramBase)LMFactory.createLoadManagement(programType);
    }else {
        program = (LMProgramBase)o;
    }
	program.setName( getJTextFieldName().getText() );
	program.getProgram().setControlType( getJComboBoxOperationalState().getSelectedItem().toString() );
	
	if( getJComboBoxConstraint().getSelectedItem() != null )
		program.getProgram().setConstraintID( new Integer(((com.cannontech.database.data.lite.LiteLMConstraint)getJComboBoxConstraint().getSelectedItem()).getConstraintID() ));

	if (program instanceof LMProgramDirectBase)	{
		LMProgramDirectBase prog = (LMProgramDirectBase)program;
		if(getJTextFieldTriggerOffset().getText().length() > 0)
			prog.getDirectProgram().setTriggerOffset(new Double(getJTextFieldTriggerOffset().getText()));

		if(getJTextFieldOffset().getText().length() > 0)
			prog.getDirectProgram().setRestoreOffset(new Double(getJTextFieldOffset().getText()));
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
	getJComboBoxOperationalState().addActionListener(ivjEventHandler);
	getJTextFieldName().addCaretListener(ivjEventHandler);
	getJComboBoxConstraint().addActionListener(ivjEventHandler);
	getJTextFieldTriggerOffset().addCaretListener(ivjEventHandler);
	
	getJTextFieldOffset().addCaretListener(ivjEventHandler);
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
		java.awt.BorderLayout layBorderLayout37 = new java.awt.BorderLayout();
		layBorderLayout37.setHgap(0);
		layBorderLayout37.setVgap(50);
		this.setLayout(layBorderLayout37);
		this.add(getActionPasser(), java.awt.BorderLayout.NORTH);
		this.add(getJPanelTriggerThreshold(), java.awt.BorderLayout.SOUTH);
		this.add(getJPanel(), java.awt.BorderLayout.NORTH);
		setSize(364, 392);

		this.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
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
@Override
public boolean isInputValid() 
{
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}


	return true;
}
public boolean isTimedOperationalState()
{
	return getJComboBoxOperationalState().getSelectedItem().toString().compareTo("Timed") == 0;
}

public void setIsAWizardOp(boolean wizard)
{
	isAWizardOp = wizard;
}
/**
 * setValue method comment.
 */
@Override
public void setValue(Object o) 
{
	LMProgramBase program = (LMProgramBase)o;
	if(!isAWizardOp) {
    	getJTextFieldName().setText( program.getPAOName() );
    	getJComboBoxOperationalState().setSelectedItem( program.getProgram().getControlType().toString() );
    
    	getJLabelProgramType().setVisible( true );
    	getJLabelActualProgType().setVisible( true );
    	getJLabelActualProgType().setText( program.getPaoType().getPaoTypeName() );
    
    	for( int i = 0; i < getJComboBoxConstraint().getItemCount(); i++ )
    		if( getJComboBoxConstraint().getItemAt(i).getConstraintID()
    			== program.getProgram().getConstraintID().intValue() )
    			{
    				getJComboBoxConstraint().setSelectedIndex(i);
    				break;
    			}

        if (program instanceof LMProgramDirectBase) {
    		getJPanelTriggerThreshold().setVisible(true);
    		getJTextFieldTriggerOffset().setText(((LMProgramDirectBase)program).getDirectProgram().getTriggerOffset().toString());
    		getJTextFieldOffset().setText(((LMProgramDirectBase)program).getDirectProgram().getRestoreOffset().toString());
    	}
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
            getJTextFieldName().requestFocus(); 
        } 
    });    
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
	/**
	 * This method initializes jLabel
	 * 
	 * @return javax.swing.JLabel
	 */
	private javax.swing.JLabel getJLabel() {
		if(jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setBounds(16, 57, 107, 20);
			jLabel.setText("Restore Offset:");
			jLabel.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 14));
			jLabel.setName("JLabelRestoreOffset");
			jLabel.setToolTipText("Any postivie or negative float value is valid");
		}
		return jLabel;
	}
	/**
	 * This method initializes jTextField
	 * 
	 * @return javax.swing.JTextField
	 */
	private javax.swing.JTextField getJTextFieldOffset() {
		if(jTextFieldOffset == null) {
			jTextFieldOffset = new javax.swing.JTextField();
			jTextFieldOffset.setPreferredSize(new java.awt.Dimension(72,20));
			jTextFieldOffset.setName("JTextFieldRestoreOffset");
			
			jTextFieldOffset.setDocument( 
				new DoubleRangeDocument( -9999.9999, 99999.9999, 4 ) );
			jTextFieldOffset.setActionCommand("");
		}
		return jTextFieldOffset;
	}
	/**
	 * This method initializes jPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private javax.swing.JPanel getJPanel() {
		if(jPanel == null) {
			jPanel = new javax.swing.JPanel();
			java.awt.GridBagConstraints consGridBagConstraints26 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints27 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints28 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints30 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints31 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints32 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints29 = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints consGridBagConstraints33 = new java.awt.GridBagConstraints();
			consGridBagConstraints26.insets = new java.awt.Insets(5,2,19,3);
			consGridBagConstraints26.ipadx = 5;
			consGridBagConstraints26.gridwidth = 3;
			consGridBagConstraints26.gridy = 3;
			consGridBagConstraints26.gridx = 0;
			consGridBagConstraints27.insets = new java.awt.Insets(5,3,15,5);
			consGridBagConstraints27.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints27.weightx = 1.0;
			consGridBagConstraints27.gridy = 3;
			consGridBagConstraints27.gridx = 3;
			consGridBagConstraints32.insets = new java.awt.Insets(5,7,8,4);
			consGridBagConstraints32.ipadx = 279;
			consGridBagConstraints32.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints32.weightx = 1.0;
			consGridBagConstraints32.gridwidth = 3;
			consGridBagConstraints32.gridy = 0;
			consGridBagConstraints32.gridx = 1;
			consGridBagConstraints31.insets = new java.awt.Insets(5,1,9,7);
			consGridBagConstraints31.ipadx = 151;
			consGridBagConstraints31.gridwidth = 2;
			consGridBagConstraints31.gridy = 1;
			consGridBagConstraints31.gridx = 2;
			consGridBagConstraints28.insets = new java.awt.Insets(5,1,6,5);
			consGridBagConstraints28.ipadx = 196;
			consGridBagConstraints28.fill = java.awt.GridBagConstraints.HORIZONTAL;
			consGridBagConstraints28.weightx = 1.0;
			consGridBagConstraints28.gridwidth = 2;
			consGridBagConstraints28.gridy = 2;
			consGridBagConstraints28.gridx = 2;
			consGridBagConstraints28.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints30.insets = new java.awt.Insets(5,2,7,19);
			consGridBagConstraints30.ipadx = 5;
			consGridBagConstraints30.gridwidth = 2;
			consGridBagConstraints30.gridy = 1;
			consGridBagConstraints30.gridx = 0;
			consGridBagConstraints30.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints31.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints26.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints33.insets = new java.awt.Insets(5,2,9,6);
			consGridBagConstraints33.ipadx = 11;
			consGridBagConstraints33.gridy = 0;
			consGridBagConstraints33.gridx = 0;
			consGridBagConstraints33.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints32.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints27.anchor = java.awt.GridBagConstraints.NORTH;
			consGridBagConstraints29.insets = new java.awt.Insets(5,2,12,0);
			consGridBagConstraints29.ipadx = 3;
			consGridBagConstraints29.gridwidth = 2;
			consGridBagConstraints29.gridy = 2;
			consGridBagConstraints29.gridx = 0;
			consGridBagConstraints29.anchor = java.awt.GridBagConstraints.NORTH;
			jPanel.setLayout(new java.awt.GridBagLayout());
			jPanel.add(getJLabelConstraint(), consGridBagConstraints26);
			jPanel.add(getJComboBoxConstraint(), consGridBagConstraints27);
			jPanel.add(getJComboBoxOperationalState(), consGridBagConstraints28);
			jPanel.add(getJLabelOperationalState(), consGridBagConstraints29);
			jPanel.add(getJLabelProgramType(), consGridBagConstraints30);
			jPanel.add(getJLabelActualProgType(), consGridBagConstraints31);
			jPanel.add(getJTextFieldName(), consGridBagConstraints32);
			jPanel.add(getJLabelName(), consGridBagConstraints33);
			jPanel.setVisible(true);
			jPanel.setPreferredSize(new java.awt.Dimension(374,180));
			jPanel.setName("InfoPanel");
		}
		return jPanel;
	}
}

package com.cannontech.dbeditor.wizard.device.lmcontrolarea;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.db.device.lm.LMProgram;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;

public class LMControlAreaTriggerModifyPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjJComboBoxNormalState = null;
	private javax.swing.JComboBox ivjJComboBoxType = null;
	private javax.swing.JLabel ivjJLabelNormalStateAndThreshold = null;
	private javax.swing.JLabel ivjJLabelType = null;
	private javax.swing.JTextField ivjJTextFieldThreshold = null;
	private javax.swing.JLabel ivjJLabelMinRestOffset = null;
	private javax.swing.JTextField ivjJTextFieldMinRestOffset = null;
	//a mutable lite point used for comparisons
	private static final com.cannontech.database.data.lite.LitePoint DUMMY_LITE_POINT = 
					new com.cannontech.database.data.lite.LitePoint(Integer.MIN_VALUE, "**DUMMY**", 0, 0, 0, 0 );
	private com.cannontech.common.gui.util.JPanelDevicePoint ivjJPanelDevicePoint = null;
	private com.cannontech.common.gui.util.JPanelDevicePoint ivjJPanelDevicePointPeak = null;
	private javax.swing.JCheckBox ivjJCheckBoxPeakTracking = null;
	private javax.swing.JPanel ivjJPanelPeakTracking = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMControlAreaTriggerModifyPanel() {
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
	if (e.getSource() == getJComboBoxType()) 
		connEtoC1(e);
	if (e.getSource() == getJComboBoxNormalState()) 
		connEtoC4(e);
	if (e.getSource() == getJCheckBoxPeakTracking()) 
		connEtoC2(e);
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
	if (e.getSource() == getJTextFieldThreshold()) 
		connEtoC5(e);
	if (e.getSource() == getJTextFieldMinRestOffset()) 
		connEtoC3(e);
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
		this.jComboBoxType_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JCheckBoxPeakTracking.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerModifyPanel.jCheckBoxPeakTracking_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxPeakTracking_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTextFieldMinRestOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlAreaTriggerModifyPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (JComboBoxNormalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerModifyPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JTextFieldThreshold.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlAreaTriggerPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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
 * Return the JCheckBoxPeakTracking property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxPeakTracking() {
	if (ivjJCheckBoxPeakTracking == null) {
		try {
			ivjJCheckBoxPeakTracking = new javax.swing.JCheckBox();
			ivjJCheckBoxPeakTracking.setName("JCheckBoxPeakTracking");
			ivjJCheckBoxPeakTracking.setMnemonic('u');
			ivjJCheckBoxPeakTracking.setText("Use Peak Tracking");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxPeakTracking;
}
/**
 * Return the JComboBoxNormalState property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNormalState() {
	if (ivjJComboBoxNormalState == null) {
		try {
			ivjJComboBoxNormalState = new javax.swing.JComboBox();
			ivjJComboBoxNormalState.setName("JComboBoxNormalState");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxNormalState;
}
/**
 * Return the JComboBoxType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxType() {
	if (ivjJComboBoxType == null) {
		try {
			ivjJComboBoxType = new javax.swing.JComboBox();
			ivjJComboBoxType.setName("JComboBoxType");
			// user code begin {1}

			ivjJComboBoxType.addItem( LMControlAreaTrigger.TYPE_THRESHOLD );
			ivjJComboBoxType.addItem( LMControlAreaTrigger.TYPE_STATUS );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxType;
}
/**
 * Return the JLabelMinRestOffset property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinRestOffset() {
	if (ivjJLabelMinRestOffset == null) {
		try {
			ivjJLabelMinRestOffset = new javax.swing.JLabel();
			ivjJLabelMinRestOffset.setName("JLabelMinRestOffset");
			ivjJLabelMinRestOffset.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMinRestOffset.setText("Min Restore Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinRestOffset;
}
/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNormalStateAndThreshold() {
	if (ivjJLabelNormalStateAndThreshold == null) {
		try {
			ivjJLabelNormalStateAndThreshold = new javax.swing.JLabel();
			ivjJLabelNormalStateAndThreshold.setName("JLabelNormalStateAndThreshold");
			ivjJLabelNormalStateAndThreshold.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNormalStateAndThreshold.setText("Normal State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNormalStateAndThreshold;
}
/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelType() {
	if (ivjJLabelType == null) {
		try {
			ivjJLabelType = new javax.swing.JLabel();
			ivjJLabelType.setName("JLabelType");
			ivjJLabelType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelType.setText("Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelType;
}
/**
 * Return the JPanelDevicePoint property value.
 * @return com.cannontech.common.gui.util.JPanelDevicePoint
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JPanelDevicePoint getJPanelDevicePoint() {
	if (ivjJPanelDevicePoint == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Trigger Identification");
			ivjJPanelDevicePoint = new com.cannontech.common.gui.util.JPanelDevicePoint();
			ivjJPanelDevicePoint.setName("JPanelDevicePoint");
			ivjJPanelDevicePoint.setBorder(ivjLocalBorder);
			ivjJPanelDevicePoint.setFont(new java.awt.Font("Arial", 1, 12));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDevicePoint;
}
/**
 * Return the JPanelDevicePointPeak property value.
 * @return com.cannontech.common.gui.util.JPanelDevicePoint
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JPanelDevicePoint getJPanelDevicePointPeak() {
	if (ivjJPanelDevicePointPeak == null) {
		try {
			ivjJPanelDevicePointPeak = new com.cannontech.common.gui.util.JPanelDevicePoint();
			ivjJPanelDevicePointPeak.setName("JPanelDevicePointPeak");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDevicePointPeak;
}
/**
 * Return the JPanelPeakTracking property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPeakTracking() {
	if (ivjJPanelPeakTracking == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("Peak Tracking");
			ivjJPanelPeakTracking = new javax.swing.JPanel();
			ivjJPanelPeakTracking.setName("JPanelPeakTracking");
			ivjJPanelPeakTracking.setBorder(ivjLocalBorder1);
			ivjJPanelPeakTracking.setLayout(new java.awt.GridBagLayout());
			ivjJPanelPeakTracking.setFont(new java.awt.Font("Arial", 1, 12));

			java.awt.GridBagConstraints constraintsJCheckBoxPeakTracking = new java.awt.GridBagConstraints();
			constraintsJCheckBoxPeakTracking.gridx = 1; constraintsJCheckBoxPeakTracking.gridy = 1;
			constraintsJCheckBoxPeakTracking.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxPeakTracking.ipadx = 112;
			constraintsJCheckBoxPeakTracking.ipady = -5;
			constraintsJCheckBoxPeakTracking.insets = new java.awt.Insets(0, 11, 0, 28);
			getJPanelPeakTracking().add(getJCheckBoxPeakTracking(), constraintsJCheckBoxPeakTracking);

			java.awt.GridBagConstraints constraintsJPanelDevicePointPeak = new java.awt.GridBagConstraints();
			constraintsJPanelDevicePointPeak.gridx = 1; constraintsJPanelDevicePointPeak.gridy = 2;
			constraintsJPanelDevicePointPeak.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelDevicePointPeak.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelDevicePointPeak.weightx = 1.0;
			constraintsJPanelDevicePointPeak.weighty = 1.0;
			constraintsJPanelDevicePointPeak.ipadx = 13;
			constraintsJPanelDevicePointPeak.insets = new java.awt.Insets(0, 11, 0, 28);
			getJPanelPeakTracking().add(getJPanelDevicePointPeak(), constraintsJPanelDevicePointPeak);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPeakTracking;
}
/**
 * Return the JTextFieldMinRestOffset property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldMinRestOffset() {
	if (ivjJTextFieldMinRestOffset == null) {
		try {
			ivjJTextFieldMinRestOffset = new javax.swing.JTextField();
			ivjJTextFieldMinRestOffset.setName("JTextFieldMinRestOffset");
			// user code begin {1}

			ivjJTextFieldMinRestOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999.9999, 99999.9999, 4) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldMinRestOffset;
}
/**
 * Return the JTextFieldThreshold property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldThreshold() {
	if (ivjJTextFieldThreshold == null) {
		try {
			ivjJTextFieldThreshold = new javax.swing.JTextField();
			ivjJTextFieldThreshold.setName("JTextFieldThreshold");
			// user code begin {1}

			ivjJTextFieldThreshold.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.99999999, 999999.99999999, 8 ) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldThreshold;
}
/**
 * Insert the method's description here.
 * Creation date: (3/21/2001 2:08:57 PM)
 * @return int
 * @param state java.lang.String
 */
private int getNormalStateIndex(String state) 
{
	for( int i = 0; i < getJComboBoxNormalState().getItemCount(); i++ )
	{
		if( getJComboBoxNormalState().getItemAt(i).toString().equalsIgnoreCase(state) )
			return i;
	}

	//error
	return -1;
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2002 9:51:17 AM)
 * @return java.lang.Integer
 */
public Integer getSelectedNormalState() 
{
	if( getJComboBoxNormalState().getSelectedItem() != null )
	{
		return new Integer( ((com.cannontech.database.data.lite.LiteState)
					getJComboBoxNormalState().getSelectedItem()).getStateRawState() );
	}
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2002 9:51:17 AM)
 * @return java.lang.Integer
 */
public Integer getSelectedPointID() 
{
	if( getJPanelDevicePoint().getSelectedPoint() != null )
	{
		return new Integer( ((com.cannontech.database.data.lite.LitePoint)
					getJPanelDevicePoint().getSelectedPoint()).getPointID() );
	}
	else
		return null;
}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2002 9:51:17 AM)
 * @return java.lang.Integer
 */
public Double getSelectedThreshold() 
{
	try
	{
		return new Double( getJTextFieldThreshold().getText() );
	}
	catch( NumberFormatException e )
	{
		return new Double(0.0);
	}

}
/**
 * Insert the method's description here.
 * Creation date: (2/6/2002 9:51:17 AM)
 * @return java.lang.Integer
 */
public String getSelectedType() 
{
	return getJComboBoxType().getSelectedItem().toString();
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMControlAreaTrigger trigger = null ;
	if( o == null )
		trigger = new LMControlAreaTrigger();
	else
		trigger = (LMControlAreaTrigger)o;

	
	trigger.setPointID( getSelectedPointID() );
	trigger.setTriggerType( getSelectedType() );

	if( trigger.getTriggerType().equalsIgnoreCase(LMControlAreaTrigger.TYPE_STATUS) )
	{
		trigger.setNormalState( getSelectedNormalState() );
		trigger.setThreshold( new Double(0.0) );
	}
	else
	{
		trigger.setNormalState( new Integer(LMControlAreaTrigger.INVALID_INT_VALUE) );
		trigger.setThreshold( getSelectedThreshold() );

		try
		{
			trigger.setMinRestoreOffset( new Double(getJTextFieldMinRestOffset().getText()) );
		}
		catch( NumberFormatException e )
		{
			trigger.setMinRestoreOffset( new Double(0.0) );
		}
		
	}

	if( getJCheckBoxPeakTracking().isSelected() 
		 && getJPanelDevicePointPeak().getSelectedPoint() != null )
	{
		trigger.setPeakPointID( 
			new Integer(getJPanelDevicePointPeak().getSelectedPoint().getPointID()) );
	}
	else
		trigger.setPeakPointID( new Integer(0) );

	return trigger;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	getJPanelDevicePoint().addComboBoxPropertyChangeListener( this );
	getJPanelDevicePointPeak().addComboBoxPropertyChangeListener( this );

	// user code end
	getJComboBoxType().addActionListener(this);
	getJTextFieldThreshold().addCaretListener(this);
	getJTextFieldMinRestOffset().addCaretListener(this);
	getJComboBoxNormalState().addActionListener(this);
	getJCheckBoxPeakTracking().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMControlAreaTriggerModifyPanel");
		setToolTipText("");
		setPreferredSize(new java.awt.Dimension(303, 194));
		setLayout(new java.awt.GridBagLayout());
		setSize(303, 268);
		setMinimumSize(new java.awt.Dimension(10, 10));

		java.awt.GridBagConstraints constraintsJLabelType = new java.awt.GridBagConstraints();
		constraintsJLabelType.gridx = 1; constraintsJLabelType.gridy = 1;
		constraintsJLabelType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelType.ipadx = 9;
		constraintsJLabelType.ipady = -2;
		constraintsJLabelType.insets = new java.awt.Insets(7, 8, 7, 4);
		add(getJLabelType(), constraintsJLabelType);

		java.awt.GridBagConstraints constraintsJComboBoxType = new java.awt.GridBagConstraints();
		constraintsJComboBoxType.gridx = 2; constraintsJComboBoxType.gridy = 1;
		constraintsJComboBoxType.gridwidth = 3;
		constraintsJComboBoxType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxType.weightx = 1.0;
		constraintsJComboBoxType.ipadx = 103;
		constraintsJComboBoxType.insets = new java.awt.Insets(6, 5, 2, 12);
		add(getJComboBoxType(), constraintsJComboBoxType);

		java.awt.GridBagConstraints constraintsJLabelNormalStateAndThreshold = new java.awt.GridBagConstraints();
		constraintsJLabelNormalStateAndThreshold.gridx = 1; constraintsJLabelNormalStateAndThreshold.gridy = 2;
		constraintsJLabelNormalStateAndThreshold.gridwidth = 2;
		constraintsJLabelNormalStateAndThreshold.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelNormalStateAndThreshold.ipadx = 7;
		constraintsJLabelNormalStateAndThreshold.ipady = -2;
		constraintsJLabelNormalStateAndThreshold.insets = new java.awt.Insets(6, 8, 5, 5);
		add(getJLabelNormalStateAndThreshold(), constraintsJLabelNormalStateAndThreshold);

		java.awt.GridBagConstraints constraintsJComboBoxNormalState = new java.awt.GridBagConstraints();
		constraintsJComboBoxNormalState.gridx = 3; constraintsJComboBoxNormalState.gridy = 2;
		constraintsJComboBoxNormalState.gridwidth = 2;
		constraintsJComboBoxNormalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxNormalState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxNormalState.weightx = 1.0;
		constraintsJComboBoxNormalState.ipadx = 55;
		constraintsJComboBoxNormalState.insets = new java.awt.Insets(3, 5, 2, 12);
		add(getJComboBoxNormalState(), constraintsJComboBoxNormalState);

		java.awt.GridBagConstraints constraintsJTextFieldThreshold = new java.awt.GridBagConstraints();
		constraintsJTextFieldThreshold.gridx = 3; constraintsJTextFieldThreshold.gridy = 2;
		constraintsJTextFieldThreshold.gridwidth = 2;
		constraintsJTextFieldThreshold.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldThreshold.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldThreshold.weightx = 1.0;
		constraintsJTextFieldThreshold.ipadx = 118;
		constraintsJTextFieldThreshold.ipady = 3;
		constraintsJTextFieldThreshold.insets = new java.awt.Insets(3, 5, 2, 71);
		add(getJTextFieldThreshold(), constraintsJTextFieldThreshold);

		java.awt.GridBagConstraints constraintsJLabelMinRestOffset = new java.awt.GridBagConstraints();
		constraintsJLabelMinRestOffset.gridx = 1; constraintsJLabelMinRestOffset.gridy = 3;
		constraintsJLabelMinRestOffset.gridwidth = 3;
		constraintsJLabelMinRestOffset.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinRestOffset.ipadx = 3;
		constraintsJLabelMinRestOffset.ipady = -2;
		constraintsJLabelMinRestOffset.insets = new java.awt.Insets(5, 10, 1, 2);
		add(getJLabelMinRestOffset(), constraintsJLabelMinRestOffset);

		java.awt.GridBagConstraints constraintsJTextFieldMinRestOffset = new java.awt.GridBagConstraints();
		constraintsJTextFieldMinRestOffset.gridx = 4; constraintsJTextFieldMinRestOffset.gridy = 3;
		constraintsJTextFieldMinRestOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMinRestOffset.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldMinRestOffset.weightx = 1.0;
		constraintsJTextFieldMinRestOffset.ipadx = 148;
		constraintsJTextFieldMinRestOffset.insets = new java.awt.Insets(3, 2, 0, 12);
		add(getJTextFieldMinRestOffset(), constraintsJTextFieldMinRestOffset);

		java.awt.GridBagConstraints constraintsJPanelDevicePoint = new java.awt.GridBagConstraints();
		constraintsJPanelDevicePoint.gridx = 1; constraintsJPanelDevicePoint.gridy = 4;
		constraintsJPanelDevicePoint.gridwidth = 4;
		constraintsJPanelDevicePoint.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDevicePoint.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelDevicePoint.weightx = 1.0;
		constraintsJPanelDevicePoint.weighty = 1.0;
		constraintsJPanelDevicePoint.ipadx = 42;
		constraintsJPanelDevicePoint.ipady = -4;
		constraintsJPanelDevicePoint.insets = new java.awt.Insets(1, 10, 1, 10);
		add(getJPanelDevicePoint(), constraintsJPanelDevicePoint);

		java.awt.GridBagConstraints constraintsJPanelPeakTracking = new java.awt.GridBagConstraints();
		constraintsJPanelPeakTracking.gridx = 1; constraintsJPanelPeakTracking.gridy = 5;
		constraintsJPanelPeakTracking.gridwidth = 4;
		constraintsJPanelPeakTracking.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelPeakTracking.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelPeakTracking.weightx = 1.0;
		constraintsJPanelPeakTracking.weighty = 1.0;
		constraintsJPanelPeakTracking.ipadx = -10;
		constraintsJPanelPeakTracking.ipady = -4;
		constraintsJPanelPeakTracking.insets = new java.awt.Insets(2, 10, 5, 10);
		add(getJPanelPeakTracking(), constraintsJPanelPeakTracking);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	getJLabelNormalStateAndThreshold().setText( LMControlAreaTrigger.TYPE_THRESHOLD );
	getJComboBoxNormalState().setVisible(false);
	getJTextFieldThreshold().setVisible(true);


	jComboBoxType_ActionPerformed(null);
	setTrackingEnabled(false);

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJPanelDevicePoint().getSelectedDevice() == null
		 || getJPanelDevicePoint().getSelectedPoint() == null
		 || getJComboBoxType().getSelectedItem() == null )
	{
		setErrorString("A trigger type, device and point must be specified.");
		return false;
	}

	if( getJComboBoxType().getSelectedItem().toString().equalsIgnoreCase(LMControlAreaTrigger.TYPE_THRESHOLD) )
	{
		try
		{			
			if( getJTextFieldThreshold().getText() == null
				 || getJTextFieldThreshold().getText().length() <= 0
				 || Double.parseDouble(getJTextFieldThreshold().getText()) > Double.MAX_VALUE )
			{
				setErrorString("The threshold for this trigger must be a valid number.");
				return false;
			}
		}
		catch( NumberFormatException e )
		{
			return false;
		}
	}

	return true;
}
/**
 * Comment
 */
public void jCheckBoxPeakTracking_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	setTrackingEnabled( getJCheckBoxPeakTracking().isSelected() );

	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jComboBoxType_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxType().getSelectedItem().toString().equalsIgnoreCase( LMControlAreaTrigger.TYPE_THRESHOLD ) )
	{

		getJLabelNormalStateAndThreshold().setText( LMControlAreaTrigger.TYPE_THRESHOLD + ":");
		getJComboBoxNormalState().setVisible(false);
		getJTextFieldThreshold().setVisible(true);
		getJLabelMinRestOffset().setEnabled(true);
		getJTextFieldMinRestOffset().setEnabled(true);
		
		getJCheckBoxPeakTracking().setEnabled(true);

		//initDeviceComboBox( LMControlAreaTrigger.TYPE_THRESHOLD );
		int[] ptType =
		{
			com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
			com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT,
			com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT,
			com.cannontech.database.data.point.PointTypes.CALCULATED_POINT
		};
		
		getJPanelDevicePoint().setPointTypeFilter( ptType );
	}
	else
	{
		getJLabelNormalStateAndThreshold().setText("Normal State:");
		getJComboBoxNormalState().setVisible(true);
		getJTextFieldThreshold().setVisible(false);
		getJLabelMinRestOffset().setEnabled(false);
		getJTextFieldMinRestOffset().setEnabled(false);

		getJCheckBoxPeakTracking().setSelected(false);
		getJCheckBoxPeakTracking().setEnabled(false);
		setTrackingEnabled(false);

		//initDeviceComboBox( LMControlAreaTrigger.TYPE_STATUS );		
		int[] ptType =
		{
			com.cannontech.database.data.point.PointTypes.STATUS_POINT
		};
		
		getJPanelDevicePoint().setPointTypeFilter( ptType );
	}

	updateStates();
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
		LMControlAreaTriggerModifyPanel aLMControlAreaTriggerModifyPanel;
		aLMControlAreaTriggerModifyPanel = new LMControlAreaTriggerModifyPanel();
		frame.setContentPane(aLMControlAreaTriggerModifyPanel);
		frame.setSize(aLMControlAreaTriggerModifyPanel.getSize());
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
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/22/2002 12:22:36 PM)
 */
public void propertyChange(java.beans.PropertyChangeEvent evt)
{
	if( evt.getPropertyName().equalsIgnoreCase(getJPanelDevicePoint().PROPERTY_PAO_UPDATE)
		 || evt.getPropertyName().equalsIgnoreCase(getJPanelDevicePoint().PROPERTY_POINT_UPDATE) )
	{
		if( evt.getSource() == getJPanelDevicePoint() )
		{			
			updateStates();
		}

		if( evt.getSource() == getJPanelDevicePointPeak() )
		{
			fireInputUpdate();
		}
	}

}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 1:25:53 PM)
 * @param value boolean
 */
private void setTrackingEnabled(boolean value) 
{
	for( int i = 0; i < getJPanelDevicePointPeak().getComponentCount(); i++ )
		getJPanelDevicePointPeak().getComponent(i).setEnabled( value );

}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMControlAreaTrigger trigger = null ;

	if( o == null )
		return;
	else
		trigger = (LMControlAreaTrigger)o;

	com.cannontech.database.data.lite.LiteYukonPAObject litePAO = null;
	com.cannontech.database.data.lite.LitePoint litePoint = null;
	com.cannontech.database.data.lite.LiteState liteState = null;
	
	// look for the litePoint here 
	litePoint = com.cannontech.database.cache.functions.PointFuncs.getLitePoint(trigger.getPointID().intValue());
	
	if( litePoint == null )
		throw new RuntimeException("Unable to find the point (ID= " + trigger.getPointID() + ") associated with the LMTrigger of type '" + trigger.getTriggerType() + "'" );

	// look for the litePAO here 
	litePAO = com.cannontech.database.cache.functions.PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() );

	//set the states for the row
	liteState = com.cannontech.database.cache.functions.StateFuncs.getLiteState( ((com.cannontech.database.data.lite.LitePoint)litePoint).getStateGroupID(), trigger.getNormalState().intValue() );

	if( trigger.getTriggerType().equalsIgnoreCase(LMControlAreaTrigger.TYPE_STATUS) )
	{
		if( liteState == null )
			throw new RuntimeException("Unable to find the rawState value of " + 
					trigger.getNormalState() + ", associated with the LMTrigger for the point id = '" + 
					trigger.getPointID() + "'" );

		
		getJComboBoxType().setSelectedItem( trigger.getTriggerType() );
		getJPanelDevicePoint().setSelectedLitePAO( litePAO.getYukonID() );
		getJPanelDevicePoint().setSelectedLitePoint( litePoint.getPointID() );
		getJComboBoxNormalState().setSelectedItem( liteState );

		getJCheckBoxPeakTracking().setEnabled(false);
	}
	else
	{
		getJComboBoxType().setSelectedItem( trigger.getTriggerType() );
		getJPanelDevicePoint().setSelectedLitePAO( litePAO.getYukonID() );
		getJPanelDevicePoint().setSelectedLitePoint( litePoint.getPointID() );
		getJTextFieldThreshold().setText( trigger.getThreshold().toString() );

		getJCheckBoxPeakTracking().setEnabled(true);
	}


	getJCheckBoxPeakTracking().setSelected( trigger.getPeakPointID().intValue() > 0 );
	setTrackingEnabled( trigger.getPeakPointID().intValue() > 0 );
		
	if( trigger.getPeakPointID().intValue() > 0 )
	{
		com.cannontech.database.data.lite.LitePoint lp =
				com.cannontech.database.cache.functions.PointFuncs.getLitePoint( trigger.getPeakPointID().intValue() );
				
		getJPanelDevicePointPeak().setSelectedLitePAO( lp.getPaobjectID() );
		getJPanelDevicePointPeak().setSelectedLitePoint( lp.getPointID() );
	}


	//always do the following settings
	getJTextFieldMinRestOffset().setText( trigger.getMinRestoreOffset().toString() );
}
/**
 * Comment
 */
private void updateStates()
{
	getJComboBoxNormalState().removeAllItems();

	if( getJComboBoxNormalState().isVisible() && getJPanelDevicePoint().getSelectedPoint() != null )
	{
		//set the states for the JCombobox
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized( cache )
		{
			java.util.List allStateGroups = cache.getAllStateGroups();
			
			int stateGroupID = ((com.cannontech.database.data.lite.LitePoint)getJPanelDevicePoint().getSelectedPoint()).getStateGroupID();
			
			//Load the state table combo box
			for(int i=0;i<allStateGroups.size();i++)
			{
				com.cannontech.database.data.lite.LiteStateGroup stateGroup = (com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i);

				if( stateGroup.getStateGroupID() == stateGroupID )
				{
					java.util.Iterator stateIterator = stateGroup.getStatesList().iterator();				
					while( stateIterator.hasNext() )
					{
						getJComboBoxNormalState().addItem( (com.cannontech.database.data.lite.LiteState)stateIterator.next() );
					}

					break;
				}

			}

		}
		
	}

	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB8F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D4571528443FE0021198920D228117CDED3529EDED6BF68D1F6D1A563669D7ED52AE6DC737EECAD353388D5D350DEDAE838C9841BFC8C22268702758D284B595C7C086C68208066A0824E193866681A373674C8386C2F44F39775DB7EF06B700F44D973F9C5E3B675EFB4E39671EFB6EB9775D8949B99215162C49A1A4B90DC87F4EA4939255CCC876421FBD4EF1A1C21C16A87E65836CA71F39
	128A4FF028EBDC95275DC72ED4E5C0B99A4A89AB6234EF40FBB639DED5EE0017A0FCB2206E48507BFB4767D379966713CE4BC78D19705C83908BB8FCA689483F4610AB61B7C9F88E499AC23677E01B778C07A4DCA514DB81D6822C6FE16D1F0567AE4A799EFD25542EE3FE94C97A56B1CD9652D15064D0594B7AF6C8CF9DD9151C5A073C26D3B931CF8454ED8240412710630ED6F8D635767756F4BB6DE55DE607436990056E3E32A16B0859EDA933880356EE214CE66F863C5BE9B33B8533DE5FF2291E958DD0B4
	392D3D3D027B1253E26D99EEB5BB84DB886579ADC38665C1C8A314B32D081B27C7B9B6C0B99EA0C9461B4EA13E955E7BGC22CFC6C55472A243135FF340A2CF9461132B74858A32DEC4C6B2D3E31C78C7CBE7DCDBACF5E03B89FBB21CEF7A3CEFB9A209D408B9089F0105A476D377E84CFC317D86F345B1D8E034BD5E7336030849B3C6B75D0A3612E885DCE370590E683338D25AA720C872C5F42BB6E47F492FB0A5B434AABABC87C97AEEA52D5F4929FDB2ED9FD0369E2638D1FCE986F6F85651DF8435FDE7DF9
	DFA7CA5E51B31DA9E9AA3C57ECB92E59A95159576F63DD5B0FF31DCC673A9C5E13A57BE0785D949F2A40B35BDDC0ED864B91D037231F5BC67B9EAECBCB6AA3E4DFC50B2A9EA2638C1A8E9B13C134854A3211720C32B11E3BE5D9FEC8716B95F8A64B5FA99E4B89D0E73447E919FDCD4B43359C8B658DG71GC9GB90EB8EDB9005141EDEC63DBA7A6E9E32DE637D8B7E035D984B7E1FD279DFC9D1E02D7F41B1B9C9E516C68965A4CEE2B394BA6F80854E66EC154C117DF3F7B296DF7830FF7840760B60BD62783
	281B6D02A838BD2CCD64417D5886D6B3561A2CF6819858DD04530DBA188DCF1B59A3DEF3D94C2260EB416AE38F621C8C1A5B00A29000F726174F9DC6FDD9E07C23GE1329E8A8FA13D0B029B3C8538932733FF40E5421110F8876A79B96ABB2260FD2D036B7871A30853B9633415CECE676F078EA868982D5DA83E59BD4C57D87AB97457ADCEEEB33FEE67B6E34C0AA125DB1AD5ED86416BE436926E8CDCBB7BA9CD140B159F56375F61BA665062D4DFEBE3FD4593D36611B209B6CC3F3979B8D7EBC1264DGC932
	0E9FBC24E3B6DBE776885CCA5FF5030189961F46191C5FBD17A36993751D87F08420C29EC7BC2D4FF4018F8668G8885D869623A6CBC572E180F8A6273BD1399AB67B73EAE07A98F2E175D52B3ED12BC52A71007696C5EBC5C2704C141A19A85176030400BC72AFBA08F7BCB6B3847A9770924F8972C7BFAD8E89E993FA48F5726D9945D562E81D138AA082255516B2BDFC1F9F9863ADCBE1741F9FDB68F47659C02F5AF37FFA28F578C6C678E01CE1E5CC52ADFC3572CE0936CA0F43DF340A1B27CD78F235D778B
	43124F787AE18C10864D368174BFAC1ED8EDC6FD3A5CD6BB18843AB0D2F99357739F64F888E1B34CB746C519D0273B495766D0A1DF1BC5A34B487A8F0BB5EAFBCBE40CD193F2132D1B081B3E3549E4DAF5C4DD76AFD25FA161BDA994E7BC0A36B2C1A8A32D0D3F95A05DB540EFA3C042CD3EB6AAF4D84F7D0F34A86A1DB69BD32D2C7343797BF00E041B838238EDF4F497E5BD96679F8828EB13F44A6A7F120FBA35A9FC64DAB76AF4FF950F9FF7200E81D76EE6FAF53919DED83BADF23BD4CF1C7690C015073573
	FA146DA22BF93BC8402F86F8DA355D7BB53CDD269827AD82B00AEA6D0E1C676DC221FEB140BA295DCE1135E3B6137EA146A2EAB1E86F0035ECC03422E7B70B30D8DC8322A23EF457E5C86B37E50047110FD75298824A0B81568CF05B7AFE960FA3A37A23C842E3EF850D21B78DB059368F78ECCBB70347110B8A11E779E01C36F6105BC0EC3EF2AFB3F8BC4EEEAB5DC625793BDE2034BD066BA8F8951EBDD637C7EC82676065F35DD5007262BEAB212538C3DF15A9694B84FC43G4890623F19152310FD43A05BAF
	3741135978322A1368BB28B11631C72B606EF13A6D02256406F950DCE69E924B281FAAB3506A8B78FE1C4F0DBE9D75F6832215AEE71D53EB9AF6899F9A8248348427F36EF89B537F726CDA2E5F4FBDC22AEEE7EB54627859FB73B40DC3CC6F2EA11F7EC71F5026EC7B6065F4260BBF4A3AD887ED120638AEBA32153AA875B2DD183CDC97C2B546DE5D104108E574AF8BE5289A3CE51EA1F02A52886A31D6D284534306638758FC89548A251E475B787CBCD94DE56A83FED1DEAE53172E2B49742CACD3C94DD4E4E2
	BCC3EBB84FEDC02FE81873FC661C1ACDF48CF31E3F2A192ACDB03E0D67B9DF97500C1579FE25CA49B7817099A3F15A52914EF75579FF4C965F7940C8ED31CF686E87DBEC954C7DA637393B9FF4F407B649620021BA74BB35A01FE90459DA5F08AF9E9B057770917F780C65868DE7F17C51A3ACB74816716D943FD901E77ADACDFD92ADFF843671910FE77FD40CFBE255A86422GAE0059G31G9BC739EFF93E609419A0DFE882DF69F618EDD77D82G9E3797226EE14F164A510528CB3AE7939E8F55D6856602DB
	C77D47CDBE76AF33FE174774BB9CD0AE8A68476631570632D0FF2FB0F33D3E19FB99427C7B9F327879C155475C0FFF5C2074636B761E546CD671A3882244BB62E31F9FA9154F83B69F453DE9B354ED874838856B8720914082B07B9657FFD9D175C47A27C6E5F256832E9176687A4038CA56D7EA755474FCE0CC3F3200F26CADB5BD97179CD2573389E24F862BE033B0BDAF4866FADEFF0B47617FB6C5D937CCA86B0EGD9198CAFDC67B26C3B4DE518DBB3B599A247748B142171361A8CFB2A388C1D328C174FCF
	CD060EB17D8257C664ED35B92BE98B36B6A42FA745530B1A1571746A5B4C47EF06E7A2G9B4FF6BA9E14070444E9D9DEFD9F0D19AA215CAA63FECBF19DD0764A385A8B088B03F2140C7BEC8362B6E0D94605B6A20ECC0B5366CE63BAEBB9DB844FD2A89B81BA8142GD682EC82588630EFBA442A53F97B8FE87B5669AC06F5CDE7B1A546296153D99C3AFABA5BCBD87BAFD506CB63C69EDC871BA49EC81B630C537CF54C21B388DE895BD45A14CF9F0B8B135AB159FEA24B264C3BB1565CCD6B7D67DB0DEF7A84F2
	A16CD541E52B60F81E4AFC59296260B9293ED8BDA72DADD64FC99B0A77B3BC50D01ED4717A4B4563672CED45EAB9EB9E555BD32F6F2534BD9E41C31DAB867FD25DAF445708CA6EFA59EC97A41DE713212ADDD21B8B22335EF7762059B35EF96BE61539E5FB457BA59857A4C2FD6A8C30CB0053G2699FC0F58D6BC619EFD41BB4E9E7D5323416765BF0F2A4F4B2F0E22CDF91C836EEEA1D0773FB91AAB5779F623A6D130231AD81B17E9FF516C6E95464C1D66D82EDC972C7F6EE34166174D4139EA659C3CAD4FC1
	E476F8F3A04E98BB870C5E9F7D681D12697D667AF874E6872577E40D125EE919DE761818C6890B03523B673C129E46394C1F3FF41C46F8E37D39A20F7DC1F30634D6D74F60F92C0666B809D0A62160DBC3396F7B6D98794A214E984A64EA8F8DA6DFCC9D7A9F516934192CAE5C7F09AD943FEF1D4774C3EA13D90FB661F28BBD025BADD82EDAC7F0CDA50622BC4575A863EAF8DF974A63796B97707C47E69EF68E08301F5D036D9C97783ED48AE5A3008DA0649EE53E7F79A1142547ED3603AA2E3A04EEA24E443E
	9B9AF85F84E81F8410837853G26191C6722C66C8B9D2D7681BB15104977853A8F2D03F69B6536DB32B047EEEEB1F789B6CCDC79B8676AF11C4D4A1CD65641015CBC394FE5C8A5E0490A100F999C96D31FDB7074B9ED96396D723C9CA59DC5F3D6CFFA50A77B22AD9F85D6FF22B4DB66F5496A38A2F844F7FBFABC02A85527773D6157DF2D4D0BBADC074C1A0C74D3DD2B536A90E5993F21BFA057AB93A6C0521C6D21334A1CADE9A60BA3F2E7711CEE4BC5357AD239FE635BEA7535D04F62148F0615F10AE9962B
	1F8D4F4533F88C71669E9ED72C1945FDEAF7C9F01FF823C45DA73ACA54772A411271772ADBA541FC9913EFD5B617AFF5B617F9D7BB4719E45C1F2AB8AEFAB6DF2F4FB6AB57EB9260B366409EB2072D0772B9E357AB4BA587697AB74EE139E41A0C1FC6716D8ABC4BB1B364B3919B54BDBD074B14D90DB21D1E8B394DDC0E3BC6F19DD03649382FD6F3593DF3F92E753D77155F1D77BD122FC11EEA39564639EC9C1B677AF2AD66DF52ABFC7E656D8BBAF8BA04A11D1C1C9B2DF641019F0CCACE151FAA55D51CBDFD
	1C710E9AD2722EFC750C6AD9B0C2463D0C67A16939EED6212413059778B7EF9354055D4B3FF9D7F47A65C5A808DE7F0E5BEA29B37736D0EF467C72CFE98C0C7DB75C4B7DF2E683525C89651CB0D887E1DCD75E23BCAFDD3962915292F2D175FCB141F4D2E38C134E9A42FC3A0A053C1471FC3E01730C067A78B044653E23DCE3284BB7B5AC27E1754DFE75E84B6843FC7523FE75A86F07FE7DAD0D4AFA5451B129BF9B5B370B7958F6FEBB16583EF8B66859E951BCB626C669595AD6C80A663E1C4EECA16B144F96
	66D59E8EEE8B9565E08A6572394FCBBDC176D06AB9195E3EFB1667BAA173F87E135942F1EBE55CABEFF35C76F95C9EFEFD2D04A84F1FC52B6260F90CEBE5F4968EF3BAA577F15BC8ECC25B68043297A09CA07EBE44176FB12A68CA296F940996BEE3214FBA00C2296EE9F87F1E24EFC66F1B596351CB177AF13AC5777B532B04728BD2BBC6CF57BE9E3DDD8A59900C81745A21FCBCD9A91F216ACE6433845073C2594947CBFD530BB4BE8A85FC647D5CDF7DEBBE5D6F217C340CBBC34F6613201C9A4EF1DBF338DF
	4B8D67BEF9147A69BEA8074938D994075FA8D64B38BDD57C1BAB368DB61EBB01B0859D4663184CE3B25FE9A7834A3C122D35C26A5B6D8E811293EBB70B17749477AB004D61BCDE32D56377BD91B6D636DE2BB1AC1666747B55634DE9CAG2CD358B04E63D66071B3F87E4FAB2F3C42F22A67A9ED367F4EB8D18C4FB6738DA9F33AE0310ACEB7CF1D1AE1493F5BF583129013994396697C0E2E636DC0B7F3BE0F992ED77CB9989DC0363A1DAE41AD8EB3AC7D42A64D41628BD33B3F7378947B7D7C0E7B313DBF046E
	BD2573595E1FAC6367D0FC25824F763F838DFC7FB3C2DD6CFCFE6E1CF6867DE96683F15A9200DA00F6GAFC04483DC17D19565E4021C5664F435C000E6D304D4ECFCAFB6CC6DDE531369FC1FD9735565E46D4C5FA587BB5B0DFDC03A57A4BD93CD05944F4697D909635B81F5B98BA08E85E88530G0CAE6063CB2CA8E563C3F3B00822393B0FFE1D6303EB338AC3BEB4A6A9ECECBFB82B9CDB788246BFDAFA2EDDB056CFC85F9AAEA97BED125AA5C84F24207DD27F2A6C1791A145BA5233BCA2D83FC5434AFEC6E8
	572EE8EBD36947EC292399EDA6A4A25046840A8F0B883431DFB7F19B0B043A1DGC788BB0B226702745F7673E3F1C44ABF8775C41E7F1FE57279BF7419C544784C6F12397F88143573C1A67F6A87FDB1D4620D1D69EA3C991F04F3D88ADEDFD8BDFEFDC38D16181FFCEA98735EAE271BA691AC6F4D3D48731CC41009AC843B83E8D9484EADD93F9F660F4D17DF48677DAC8BD93EBC8A4F7005ECDC2CDFD1BD7A659E27C3E43E3B136EF33150E64DC29EE33C1CB311FF5EAD69995F37C23F9D8B19FF46F266C39093
	00EF7E9DG637952EDBCE7EBD67B56A64B759F9F51B60181923685BC4B7F665C8A4C7FD09F39402F74A12E173FD563735840B149A6D2EBD157EC1D7E68ED6CA767E64CEDCBF589B47734BA2C2255EC330E50CF6ED7212123172D01198DC8073D5F2FF84FED186ADD2A497B780A3B4A030DFF4F1960B972EBE754F36403E754F3647CB3631F579EBF937C0ED17999659DA3E9FF2E4539B1429CF7820482C4822CFC087B5F2F1FA9A0547F2A785E8B16DEBC37449B0EA2673F2CB278D8E3ABA73E9779794AD7691A95
	C5279D69F37C17ABF13EC4270BA299EEDDA56A48ED6D6D93D9D3899FD7096B4EA674D0B40BAF1F27F947C69857338F7118F32886F15B203CD346455770B8B4A3D27E96F30977ED17F13FCD67FDEDB22EADC34E4DE55C413F523ED0FED64615D13F358D4ABBE55C82A273FD18631E986671F43B0C4B2A673173280C7BC6BE6F9B7EB00F093B1AFD6B28DF711E54F47757D4D6735D64416C76E49D7A5184986FCEG5DA3604781DAGACGDE00B000A80035GCF83EC8558811039A8CEFB88209C4038085B79577410
	46860DC925C387EA6CEEB3BB37E37BEA6DC5FE67399DE805AF62396AAF72105E78F725CCD651A654C95B1374EDF376C51CCB2431F111FC16C16FFB1245600F814A8198EFDD815F539B81973218674EFD857CECE26DE2CEE3A1ED3B9D4A990F023EG188FFF709DC278BE71EC8E7EF642AF57EFG21CB8A8C30C8ED85106B43FFA7F1DC130B9D781E7CAF547E18ECCF5E62632BFC144B76E3FA9F43856559GD10F72B9092F8D6ECBFEDEBB31AF59D6CB6FF3018F4023E3067B2FDA5C5B1D6CE8D6EA775F357BE4DC
	1327556931628E45F4122C775F535F50A34D35A06306C7793EF8DF0DFFBE26EB4171327D7E251AFFA49FCB829E2853BF40FE4F78FF197AA23284F437044FE17DA59FBFFB46C47C14B9FDA03FAA2039D7624746F6722F7E63EF247E2C915A19167071DFA07E636F396C1367F1B211BC6944F7EE91A8CF6892664FD09E9607A548675461CBD87C968B4FF5CB247C02654C54C7EE86DC42929EF74567CFA447AE620B3B76AD0D5396AD7545DD1570FE8D1E98F7C95FD56B025B66253A09ED332DCEFD1F6B289B3B4F19
	6B5477395EBA65BE47F036BA1C5BDE70D76DF23B1BB26EBD99B7C8E9A26E3A55A2764978D1AA97629B85E448715329DCC39457AE61E6A9F06FC93830BADCDFB064BE271B771DDFC7ED48EA33C97754E99B2B83161B2C0FC7280CD617D9A177D299779E61FBCB434509F2D663D235FB9E26897B855E7BE07D6651FE1DCBA7F72EA4C2BB698C007A6C59CB471C81D0FC4452B1E7G795C17C7C1DDEA147C3B3502291DDD1C1EE2BF7B9D77E363FA3590656F0B8A9CEFB6450BD10163DDAC6FDDA1D037D59E6FB7EFA9
	79B7AE7357FB4732E07970E57A5BA35732C07E57A9FEF4D9A07F04331C7FEC285B360C7B526F604F6C247D303F86E3DF157D50825B213397B6C4BC7FBEFD1C67485B7F06FD597BF5130F4EAF536F0C4E48B59F1D8F5AFDF48A481D51B1F692F93C99A3BCCEE9DD8E7B6CF29EAB340E707DF87DF23E9F5B6A306D8EA86FFBACCEDB729857CDF937CF162F645F1DD861AAFE36156E102C4F466F97D6074D6A906A592F2288BFEB0E26F1D2AD48567E980F039F56D1FF8865104738BD4DC4B1F9EE29F84F4D7F84F24B
	CF00473EC25F185ED43CAFAF387B3C3FF6D7F930B97D890D9743E0BE2364F9AE3A4D731D5572BC1B743E317F2F627D3933F7DF8F2FFC82BC760CCCED5FBBB4E1BF75FD6F9A6DC722A7376F65C473FDAF017A0E1268C0BF7CCB0ABF9D9D68075DF55C8F57C2DDF4B40FA9E70CE0FCA2E20604FE0570586EBBF45DE74444E9F3E2F85B5DB4CE20ED09FCDE092F122FCA3A25BC87EC0DC1BA3BEFE3CCE20C617161395B13091BF91C9E817DE2E3FC71613A981633783E0559C638FF5A98436DB649407DD3FC8C375B67
	6AFC367A22625DDBF777EDEA7EDD654166A13157B78F73F4135D3FD87B7C5B58D77AFD39203CE7D07ED132A605281FB9A746B01B5D91633BE7F0E7FB50E5AB116DED911D471295F15A53AB78BEF873865633F308437DE8C71695BCE77DF39F0EB158B9826EE26C64136BA76E26CFBF0906496A0771AA68C73B76C874587FBB616A3B6F0340ABDFA0E930E6BCEEF2E06CE7FB43FDBFFAE66507469C8D51B491ED9F8D5F1BC85A906681DE7CA5F19CA8760467A639C5750B79402BA259FF4DA3687048D8474F17017A
	DE19FA930D669386CF69D3FE7ED80A062482E9165DB411D4CCD100031BF1CA016C067251C82B8ABD001672CBD3E071E0A2C5469229A6538F2B24605F96CF517176886AA04BCF1D5FD87676F31BFE5A66C9A15AA672A6E636D785B7BD1896ACF801D57AE5F483596542D3F23C3B86C37050C71AC79C3689ADA427678A7E8A50A38A9683BF0A750864B57AEB1693F83AF322516C6993B165BB57C2D29D789BEEF200D55B4C0E5E934C8A4ED1BDA49BEA294C5493023C59D4DE24D2A6D3497E5A5E627C662DEBD30826
	013CEAF31AAD8D66EE302CA632CB6457553C2E730CD046907F41DDAA10BBA88F9F87DB065754E4217B3D0F06E5A5E6B500A23DEEF2023644C43ECCEEE1296FB343040B023BC9445F24BB5DC5F25D344362516F3C73C60706A4CB0344A948723AC4226DB15B2D36E1F8B1D2632513339F0AACDD14ED8715D156G6391FF7C42D34B7F3DEA7BB6A056C05268AFDFC024FD3ED617876CDD02FB664A8773F6C7A45C0B923BF0381C72AEC8379D9E7CD9919057850C19D7492217ADF82A7C41FF7E7D5F29683E2EA2B942
	A6893A1E68951C7693061E9E88BFE582B0E3C0BECFA60FED4A825A9C704EFF2DF94BABDDE0E1C612E2AFAF27FFAB685F13783702E2AAA82662A4CC5BF6B26E3F40FAAF9FB3B5CBDA48C59B72625BEBDEF9A12471650BCBC5A4E9F88C2697C9963B812923D7A0211CFD496A50610DC527DB5031DB0BFACA03FE56B5F4D21EFB8857755EB1D6160268ECF92E31F410B64D23FFF5F7BCBE6CD5A00D2BD3AA7F59B7AE5C3E10B73A5E74CF1F24A77DE700A7D85ACF1F24E7FD12F2D7CFDD2AC2D2A697F3CD6A12260E15
	74F57C73567F2F4C35D239F62C4C8849E0577E5B5327CE5B0DBE4989D1ECD1A2F9DD7ABD3B2E894F912DBD56EE7A117453B704D62F940F9D3F2D0C47F65C139B745EE758AA9607C52D725D81E03153722BA8FBBC60F72C627153EF2E7C05843FE3F4955C16CDB45B7A8D8E8B5EB72A9B96855F374FFF3A0A74BD3C0DD4496ABEF795653E216873FFD0CB8788A857977DDE99GG48CAGGD0CB818294G94G88G88GB8F954ACA857977DDE99GG48CAGG8CGGGGGGGGGGGGGGGGGE2F5
	E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG1899GGGG
**end of data**/
}
}

package com.cannontech.dbeditor.wizard.device.customer;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

public class CustomerBaseLinePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JCheckBox ivjJCheckBoxEnableBaseLine = null;
	private javax.swing.JLabel ivjJLabelCalcDays = null;
	private javax.swing.JLabel ivjJLabelLoadPercent = null;
	private javax.swing.JLabel ivjJLabelPerc = null;
	private javax.swing.JLabel ivjJLabelPreviousDays = null;
	private javax.swing.JPanel ivjJPanelBaseLine = null;
	private javax.swing.JTextField ivjJTextFieldLoadPercent = null;
	private javax.swing.JTextField ivjJTextFieldPreviousDays = null;
	private javax.swing.JTextField ivjJTextFieldCalcDays = null;
	private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser ivjJCheckBoxDayChooser = null;
	private javax.swing.JComboBox ivjJComboBoxHoliday = null;
	private javax.swing.JLabel ivjJLabelHoliday = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CustomerBaseLinePanel() {
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
	if (e.getSource() == getJComboBoxHoliday()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxDayChooser()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxEnableBaseLine()) 
		connEtoC6(e);
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
	if (e.getSource() == getJTextFieldPreviousDays()) 
		connEtoC4(e);
	if (e.getSource() == getJTextFieldLoadPercent()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldCalcDays()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldCompanyName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC2:  (JTextFieldCalcDays.caret.caretUpdate(javax.swing.event.CaretEvent) --> CustomerBaseLinePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBaseLinePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
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
 * connEtoC5:  (JCheckBoxDayChooser.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBaseLinePanel.jCheckBoxDayChooser_Action(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxDayChooser_Action(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JCheckBoxEnableBaseLine.action.actionPerformed(java.awt.event.ActionEvent) --> CustomerBaseLinePanel.jCheckBoxEnableBaseLine_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxEnableBaseLine_ActionPerformed(arg1);
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
 * Creation date: (10/18/2001 11:34:59 AM)
 * @param c java.awt.Component
 * @param enabled boolean
 */
private void enableComponent(java.awt.Component c, boolean enabled) 
{

	if( c instanceof java.awt.Container )
	{
		for( int i = 0; i < ((java.awt.Container)c).getComponentCount(); i++ )
			enableComponent( ((java.awt.Container)c).getComponent(i), enabled );
		
	}

	c.setEnabled( enabled );
}
/**
 * Return the JCheckBoxDayChooser property value.
 * @return com.cannontech.common.gui.unchanging.JCheckBoxDayChooser
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.unchanging.JCheckBoxDayChooser getJCheckBoxDayChooser() {
	if (ivjJCheckBoxDayChooser == null) {
		try {
			ivjJCheckBoxDayChooser = new com.cannontech.common.gui.unchanging.JCheckBoxDayChooser();
			ivjJCheckBoxDayChooser.setName("JCheckBoxDayChooser");
			// user code begin {1}

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder2.setTitle("Days Available");
			ivjJCheckBoxDayChooser.setBorder(ivjLocalBorder2);
			
			ivjJCheckBoxDayChooser.setHolidayVisible( false );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDayChooser;
}
/**
 * Return the JCheckBoxEnableBaseLine property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnableBaseLine() {
	if (ivjJCheckBoxEnableBaseLine == null) {
		try {
			ivjJCheckBoxEnableBaseLine = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableBaseLine.setName("JCheckBoxEnableBaseLine");
			ivjJCheckBoxEnableBaseLine.setMnemonic('e');
			ivjJCheckBoxEnableBaseLine.setText("Enable Base Line");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableBaseLine;
}
/**
 * Return the JComboBoxPoint property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxHoliday() {
	if (ivjJComboBoxHoliday == null) {
		try {
			ivjJComboBoxHoliday = new javax.swing.JComboBox();
			ivjJComboBoxHoliday.setName("JComboBoxHoliday");
			ivjJComboBoxHoliday.setToolTipText("Holiday schedule used to exclude control");
			ivjJComboBoxHoliday.setEnabled(true);
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List holidaySch = cache.getAllHolidaySchedules();
				for( int i = 0; i < holidaySch.size(); i++ )
					ivjJComboBoxHoliday.addItem( holidaySch.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxHoliday;
}
/**
 * Return the JLabelPDA property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCalcDays() {
	if (ivjJLabelCalcDays == null) {
		try {
			ivjJLabelCalcDays = new javax.swing.JLabel();
			ivjJLabelCalcDays.setName("JLabelCalcDays");
			ivjJLabelCalcDays.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCalcDays.setText("Days in Calculation:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCalcDays;
}
/**
 * Return the JLabelHoliday property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHoliday() {
	if (ivjJLabelHoliday == null) {
		try {
			ivjJLabelHoliday = new javax.swing.JLabel();
			ivjJLabelHoliday.setName("JLabelHoliday");
			ivjJLabelHoliday.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelHoliday.setText("Holiday:");
			ivjJLabelHoliday.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHoliday;
}
/**
 * Return the JLabelPrimeContact property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLoadPercent() {
	if (ivjJLabelLoadPercent == null) {
		try {
			ivjJLabelLoadPercent = new javax.swing.JLabel();
			ivjJLabelLoadPercent.setName("JLabelLoadPercent");
			ivjJLabelLoadPercent.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLoadPercent.setText("Load Percent:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLoadPercent;
}
/**
 * Return the JLabelPerc property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPerc() {
	if (ivjJLabelPerc == null) {
		try {
			ivjJLabelPerc = new javax.swing.JLabel();
			ivjJLabelPerc.setName("JLabelPerc");
			ivjJLabelPerc.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPerc.setText("%");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPerc;
}
/**
 * Return the JLabelNormalStateAndThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPreviousDays() {
	if (ivjJLabelPreviousDays == null) {
		try {
			ivjJLabelPreviousDays = new javax.swing.JLabel();
			ivjJLabelPreviousDays.setName("JLabelPreviousDays");
			ivjJLabelPreviousDays.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPreviousDays.setText("Previous Days Used:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPreviousDays;
}
/**
 * Return the JPanelBaseLine property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelBaseLine() {
	if (ivjJPanelBaseLine == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Base Line Properties");
			ivjJPanelBaseLine = new javax.swing.JPanel();
			ivjJPanelBaseLine.setName("JPanelBaseLine");
			ivjJPanelBaseLine.setBorder(ivjLocalBorder);
			ivjJPanelBaseLine.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelCalcDays = new java.awt.GridBagConstraints();
			constraintsJLabelCalcDays.gridx = 1; constraintsJLabelCalcDays.gridy = 3;
			constraintsJLabelCalcDays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelCalcDays.ipadx = 6;
			constraintsJLabelCalcDays.ipady = -1;
			constraintsJLabelCalcDays.insets = new java.awt.Insets(5, 6, 3, 4);
			getJPanelBaseLine().add(getJLabelCalcDays(), constraintsJLabelCalcDays);

			java.awt.GridBagConstraints constraintsJLabelLoadPercent = new java.awt.GridBagConstraints();
			constraintsJLabelLoadPercent.gridx = 1; constraintsJLabelLoadPercent.gridy = 2;
			constraintsJLabelLoadPercent.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelLoadPercent.ipadx = 11;
			constraintsJLabelLoadPercent.ipady = -1;
			constraintsJLabelLoadPercent.insets = new java.awt.Insets(6, 6, 4, 35);
			getJPanelBaseLine().add(getJLabelLoadPercent(), constraintsJLabelLoadPercent);

			java.awt.GridBagConstraints constraintsJLabelPreviousDays = new java.awt.GridBagConstraints();
			constraintsJLabelPreviousDays.gridx = 1; constraintsJLabelPreviousDays.gridy = 1;
			constraintsJLabelPreviousDays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPreviousDays.ipadx = 3;
			constraintsJLabelPreviousDays.ipady = -1;
			constraintsJLabelPreviousDays.insets = new java.awt.Insets(1, 6, 5, 1);
			getJPanelBaseLine().add(getJLabelPreviousDays(), constraintsJLabelPreviousDays);

			java.awt.GridBagConstraints constraintsJComboBoxHoliday = new java.awt.GridBagConstraints();
			constraintsJComboBoxHoliday.gridx = 2; constraintsJComboBoxHoliday.gridy = 4;
			constraintsJComboBoxHoliday.gridwidth = 2;
			constraintsJComboBoxHoliday.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxHoliday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxHoliday.weightx = 1.0;
			constraintsJComboBoxHoliday.ipadx = 36;
			constraintsJComboBoxHoliday.insets = new java.awt.Insets(2, 2, 9, 57);
			getJPanelBaseLine().add(getJComboBoxHoliday(), constraintsJComboBoxHoliday);

			java.awt.GridBagConstraints constraintsJTextFieldPreviousDays = new java.awt.GridBagConstraints();
			constraintsJTextFieldPreviousDays.gridx = 2; constraintsJTextFieldPreviousDays.gridy = 1;
			constraintsJTextFieldPreviousDays.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPreviousDays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldPreviousDays.weightx = 1.0;
			constraintsJTextFieldPreviousDays.ipadx = 45;
			constraintsJTextFieldPreviousDays.insets = new java.awt.Insets(1, 2, 3, 1);
			getJPanelBaseLine().add(getJTextFieldPreviousDays(), constraintsJTextFieldPreviousDays);

			java.awt.GridBagConstraints constraintsJTextFieldLoadPercent = new java.awt.GridBagConstraints();
			constraintsJTextFieldLoadPercent.gridx = 2; constraintsJTextFieldLoadPercent.gridy = 2;
			constraintsJTextFieldLoadPercent.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLoadPercent.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldLoadPercent.weightx = 1.0;
			constraintsJTextFieldLoadPercent.ipadx = 45;
			constraintsJTextFieldLoadPercent.insets = new java.awt.Insets(4, 2, 4, 1);
			getJPanelBaseLine().add(getJTextFieldLoadPercent(), constraintsJTextFieldLoadPercent);

			java.awt.GridBagConstraints constraintsJLabelPerc = new java.awt.GridBagConstraints();
			constraintsJLabelPerc.gridx = 3; constraintsJLabelPerc.gridy = 2;
			constraintsJLabelPerc.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelPerc.ipadx = 8;
			constraintsJLabelPerc.ipady = -2;
			constraintsJLabelPerc.insets = new java.awt.Insets(5, 2, 6, 147);
			getJPanelBaseLine().add(getJLabelPerc(), constraintsJLabelPerc);

			java.awt.GridBagConstraints constraintsJTextFieldCalcDays = new java.awt.GridBagConstraints();
			constraintsJTextFieldCalcDays.gridx = 2; constraintsJTextFieldCalcDays.gridy = 3;
			constraintsJTextFieldCalcDays.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCalcDays.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldCalcDays.weightx = 1.0;
			constraintsJTextFieldCalcDays.ipadx = 45;
			constraintsJTextFieldCalcDays.insets = new java.awt.Insets(4, 2, 2, 1);
			getJPanelBaseLine().add(getJTextFieldCalcDays(), constraintsJTextFieldCalcDays);

			java.awt.GridBagConstraints constraintsJLabelHoliday = new java.awt.GridBagConstraints();
			constraintsJLabelHoliday.gridx = 1; constraintsJLabelHoliday.gridy = 4;
			constraintsJLabelHoliday.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelHoliday.ipadx = 11;
			constraintsJLabelHoliday.ipady = -1;
			constraintsJLabelHoliday.insets = new java.awt.Insets(4, 6, 12, 74);
			getJPanelBaseLine().add(getJLabelHoliday(), constraintsJLabelHoliday);

			java.awt.GridBagConstraints constraintsJCheckBoxDayChooser = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDayChooser.gridx = 1; constraintsJCheckBoxDayChooser.gridy = 5;
			constraintsJCheckBoxDayChooser.gridwidth = 3;
			constraintsJCheckBoxDayChooser.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJCheckBoxDayChooser.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDayChooser.weightx = 1.0;
			constraintsJCheckBoxDayChooser.weighty = 1.0;
			constraintsJCheckBoxDayChooser.ipadx = 5;
			constraintsJCheckBoxDayChooser.ipady = 5;
			constraintsJCheckBoxDayChooser.insets = new java.awt.Insets(10, 5, 101, 6);
			getJPanelBaseLine().add(getJCheckBoxDayChooser(), constraintsJCheckBoxDayChooser);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelBaseLine;
}
/**
 * Return the JTextFieldCalcDays property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCalcDays() {
	if (ivjJTextFieldCalcDays == null) {
		try {
			ivjJTextFieldCalcDays = new javax.swing.JTextField();
			ivjJTextFieldCalcDays.setName("JTextFieldCalcDays");
			ivjJTextFieldCalcDays.setText("5");
			// user code begin {1}

			ivjJTextFieldCalcDays.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 9999 ) );
			ivjJTextFieldCalcDays.setText("5");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCalcDays;
}
/**
 * Return the JTextFieldLoadPercent property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLoadPercent() {
	if (ivjJTextFieldLoadPercent == null) {
		try {
			ivjJTextFieldLoadPercent = new javax.swing.JTextField();
			ivjJTextFieldLoadPercent.setName("JTextFieldLoadPercent");
			ivjJTextFieldLoadPercent.setText("75");
			// user code begin {1}

			ivjJTextFieldLoadPercent.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 100 ) );
			ivjJTextFieldLoadPercent.setText("75");
						
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLoadPercent;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldPreviousDays() {
	if (ivjJTextFieldPreviousDays == null) {
		try {
			ivjJTextFieldPreviousDays = new javax.swing.JTextField();
			ivjJTextFieldPreviousDays.setName("JTextFieldPreviousDays");
			ivjJTextFieldPreviousDays.setText("30");
			// user code begin {1}

			ivjJTextFieldPreviousDays.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 9999 ) );
			ivjJTextFieldPreviousDays.setText("30");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldPreviousDays;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return null;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)o;

	if( getJCheckBoxEnableBaseLine().isSelected() )
	{
		customer.getCustomerBaseLine().setDaysUsed( 
				new Integer( Integer.parseInt(getJTextFieldPreviousDays().getText())) );
		
		customer.getCustomerBaseLine().setPercentWindow( 
				new Integer( Integer.parseInt(getJTextFieldLoadPercent().getText())) );
		
		customer.getCustomerBaseLine().setCalcDays(
				new Integer( Integer.parseInt(getJTextFieldCalcDays().getText())) );
		
		customer.getCustomerBaseLine().setExcludedWeekDays(
				getJCheckBoxDayChooser().getSelectedDays8Chars().substring(0,7) );

		if( getJComboBoxHoliday().getSelectedItem() != null )
			customer.getCustomerBaseLine().setHolidaysUsed( 
				new Integer( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getSelectedItem()).getHolidayScheduleID() ) );
		else
			customer.getCustomerBaseLine().setHolidaysUsed( new Integer(0) );
	}
	else
	{
		//Since the base line is not used, set the DBPersistant object to a new
		// dummy like base line so it gets deleted from the database if one already exists
		com.cannontech.database.db.customer.CustomerBaseLine c = new com.cannontech.database.db.customer.CustomerBaseLine();
		c.setCustomerID( customer.getCiCustomerBase().getDeviceID() );
		customer.setCustomerBaseLine( c );
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
	// user code end
	getJTextFieldPreviousDays().addCaretListener(this);
	getJTextFieldLoadPercent().addCaretListener(this);
	getJTextFieldCalcDays().addCaretListener(this);
	getJComboBoxHoliday().addActionListener(this);
	getJCheckBoxDayChooser().addActionListener(this);
	getJCheckBoxEnableBaseLine().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CustomerBaseLinePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(402, 348);

		java.awt.GridBagConstraints constraintsJPanelBaseLine = new java.awt.GridBagConstraints();
		constraintsJPanelBaseLine.gridx = 1; constraintsJPanelBaseLine.gridy = 2;
		constraintsJPanelBaseLine.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelBaseLine.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelBaseLine.weightx = 1.0;
		constraintsJPanelBaseLine.weighty = 1.0;
		constraintsJPanelBaseLine.ipadx = -3;
		constraintsJPanelBaseLine.ipady = -6;
		constraintsJPanelBaseLine.insets = new java.awt.Insets(2, 16, 14, 17);
		add(getJPanelBaseLine(), constraintsJPanelBaseLine);

		java.awt.GridBagConstraints constraintsJCheckBoxEnableBaseLine = new java.awt.GridBagConstraints();
		constraintsJCheckBoxEnableBaseLine.gridx = 1; constraintsJCheckBoxEnableBaseLine.gridy = 1;
		constraintsJCheckBoxEnableBaseLine.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxEnableBaseLine.ipadx = 19;
		constraintsJCheckBoxEnableBaseLine.insets = new java.awt.Insets(13, 16, 1, 245);
		add(getJCheckBoxEnableBaseLine(), constraintsJCheckBoxEnableBaseLine);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//set everything to disabled
	jCheckBoxEnableBaseLine_ActionPerformed(null) ;

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{

	return true;
}
/**
 * Comment
 */
public void jCheckBoxDayChooser_Action(java.awt.event.ActionEvent e) 
{
	fireInputUpdate();

	return;
}
/**
 * Comment
 */
public void jCheckBoxEnableBaseLine_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	for( int i = 0; i < getJPanelBaseLine().getComponentCount(); i++ )
	{
		java.awt.Component c = getJPanelBaseLine().getComponent(i);
		enableComponent( c, getJCheckBoxEnableBaseLine().isSelected() );		
	}

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
		CICustomerBasePanel aCICustomerBasePanel;
		aCICustomerBasePanel = new CICustomerBasePanel();
		frame.setContentPane(aCICustomerBasePanel);
		frame.setSize(aCICustomerBasePanel.getSize());
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
	
	com.cannontech.database.data.customer.CICustomerBase customer = (com.cannontech.database.data.customer.CICustomerBase)o;
	boolean usedBased = false;
	
	Integer temp = customer.getCustomerBaseLine().getDaysUsed();
	if( temp != null )
	{
		getJTextFieldPreviousDays().setText( temp.toString() );
		usedBased = true; temp = null;
	}		
	
	temp = customer.getCustomerBaseLine().getPercentWindow();
	if( temp != null )
	{
		getJTextFieldLoadPercent().setText( temp.toString() );
		usedBased = true; temp = null;
	}
	
	temp = customer.getCustomerBaseLine().getCalcDays();
	if( temp != null )
	{
		getJTextFieldCalcDays().setText( temp.toString() );
		usedBased = true; temp = null;
	}

	String s = customer.getCustomerBaseLine().getExcludedWeekDays();
	if( s != null )
	{
		getJCheckBoxDayChooser().setSelectedCheckBoxes( s + "N" ); //add the holiday column ourself
		usedBased = true;
	}

	Integer holDay = customer.getCustomerBaseLine().getHolidaysUsed();

	if( holDay != null )
		for( int i = 0; i < getJComboBoxHoliday().getItemCount(); i++ )
			if( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getItemAt(i)).getHolidayScheduleID()
				 == holDay.intValue() )
			{
				getJComboBoxHoliday().setSelectedIndex(i);
				break;
			}
		
	if( usedBased )
		getJCheckBoxEnableBaseLine().doClick();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G73F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF4D45535D4BE269295841FA962B38A7AE2CD2B2D781A36F9AD665196AB367409153E26AD2FE535E9DFEC33D6E997D51E1ECCBE84A492C83091D1A3041F248184DFC288167CBF269AA5A6C12364E666A699B219BB4C5CC9A6BF7276BE3FFBE7F2A71138FCCB96BB771EFD4E7E1CFD7659E71FF36E103C1723B3D2EDF90424E6907E2FA4151051BAC20E0EFC31D760E628891944746F95009DE44BC8
	BABC67C1DDE1ECC2E68E69FBAF8B4ACB21DC3FB4A1732F701ECBEED3235E0017B0F2BCD0F764775FFFEBF2B99D2DC24EE6DA7EED45B6F8BE8B9083B8FCEED0D1FEEAE5BE472F62F88129B6C256F7E31BBD15FBB82E944A4DG51G4B3AD97BD5705CC2A54FAEAB656DBA66C7138D5FBFE84BC6BEA61E820AFA98EDB3FF9610FBD2B776202C1A4AED1CA6824A4B813073E9645F9D9570ACEF6E6BF9432E75975B9537DBF36B2A3D37F850B92CF89D458EF540E9D70B6DFE1F2E752B5E3232AA7ED6297854A627DBED
	D65C2A8BF8683DA8675A4AAC4A97F8C219B891D7B2043263203C9AA0C9621F2278EAF86FG501D223FE78E16737E340F5CC3B6BD66CE5B9E263F11CE564FE5CE233F8B7DCFECFE1E0E4D62B69C0394284B3B1810F98820894085908970827509A52D2F423322CB2F527A7BB5F70547D369F2F87F21B9D4973C1715C18D473D2B5AB52F0390B66EEFD696D968B399B03A73D7CC47EC92F3C278C06C3BF712447B5E34ED3630C9E24CB15B520B4C9671978D1BB059B704151DF8B158C703E53791336C64B9DADA0605
	6CE50F9E3625F0BE69FD066C13FDB856F75331AE0477D46E9F8CFFBF4587CCF8662FE7A89E4B91D0175CA7FC237DD921CBD37AA212F33C49528ED1899536E697532157952A4B40B072DE68E2B23325AE33A9BE4E04E73A24D3BC1693212E3EBFA11379579D67F17EAE0372EA00A4G62CE48ACGA887E8F68B9FCBEEBD7AB1FD2CD9716A15FE274B21FA89233D236DB9F82A815D2BB438FD3A62362B2D0A5729F439D49F61ED3E5C06B6688A223B0F7A3E9DE45429EE552B68CE4D8D5C15FED5D73DBE56A63EED87
	36712A343645592F0200FE8F91FCD73465425325787477BC8EC5D70D962CFED59B0E4900528A9C01813CB33B7C477BE8AF8F74BF82A0CA5A212E9D793D29FAA1E01415B5E9DA1F5F5302BDA1C9EE3473DDB4F644427B8A3730718FBEC0DC111610D92D89BE175BF71A78D4BB6D283E629D92F36CABC398235BB561B33FEF97BED31DFD87A9FA345152E790C6B566AB8B35503973CD4A9375E2652A2176C966EC3BE6BD1F2EACD6F4CCAAA36D9370FFD60ED57907B8D671206FDA000D5246DFEFAFE0BE0BE1DEF869
	5BDEF0B055E1D81C69B9BA1C4FF5C61DB2387F206D1F11FD19CB5B64F892B2CB81BA81A2G6E85D86B91B65DBA5CE96ADB862795FCA66B4B8BD7EC5B7DD21610134665E4CFC13F658AE4B1DB5EF39E57AFF5C0F56B552AC7F5BB6045476B7E75BC7263733BDB13B471946F01F8D0859350A7718967F14EAA3A6EF5F679F5751C2A6BCEF70FD17FFDAA4B676F729821C4487A51F96C2BB68871C0367F71F91CCBF64D6DD66920CA92DE3F164EE55525760352D51A5F2DB37C4F5F477950278E71D8724B77B1D99AD0
	DCFE0CCBAC3738D7C1FBFA3C4EFEF0958CED44F1896779F2199BA19040E10ED48A4F26CBE24E8E6E93F336F078B6327C42FE1B551A93F5C735AD76921BEB4BAF99F31669744887563A7F0446940E7725D15C73053B6099AE37E1F3457F9172DB87F2B6GEC730A3972DE87560BF844A7C91566F2B113CADBD7FE1803E323DE722B9046B1703DA96D5770614E103AD6EECBD67F4E07E8CB17A9E646F951168F8C0B9C32904A65G9DDEE64FA8AF3387EBB7EBC434CB77A5E46E81A877D9352B9B956D82D0BFAFEC3B
	A7E53BFB21FEA5C012E53BEF0F09F60774044C33G8E1D35DB2A1B5B4D3CAC5AA583BE5D8FF93C1F35F378D9BB66D31BAFE08EE3152F76781D45FE5D09AB022243E47278F5BAFCACAFFD35AA0B4F73B83F48BF6F2E4571CC01F256G2C5B834277FE14AD724F05FD51E45D125DE1736D528126DB470061FB85B3C57E796D8BA8F3B154458E88DFD97521F98D2C7079B43B13AE3FFC1CC7BEB27BA6434D78E8ABBC3B1DDE1F5EG41A3A0FCE24EC728AF2E4F9C4D73155F51FC856D359C64EE8418B70878EF156619
	F4BF8638F600D1GB6875E9941BC452EF8D53D187EE54B7E210B4A0092A8768DC220A926D12C388AEBEB7075B09F179507F7B29DD5EAF9CEF938D5641037671E65F2D7FDF991A99F4F35D96574915BDF342D9DE436A481432623DF4DCCCB7E68494D2CFFDF9A917DEB02B6338222FF7765D875AFBEA07A17B7BA1D7EB1191ED1A1F3B5704B9A92B29717D849BC8B750C2EF0CC5035GAED25279ECE63A95C30CAEF9C868BAF76CB5F8AAB4009433C7336A6D563C7D2A03AADC2C8C6ADC5B8ADA4D55657AA6C939E4
	98E2ABG0B3D7FB2E40E3D1EE1A6F799BCD78E8B59EFDF1E2EEC668B7B7EFE8A1E972BFAD5FBDF259628D6062AFAB54D27FAAF3056071A42B3B90DAE437B90FBD974E19D6816BFA2EC773DA01BB70F30FCB1B0A2744F9D7FE47ADFFA26562CFF0D9B632DD892F89F8C06D3F506659BC72BB0EEC50E98F9E13469FD594844FC116DC9DE2EC39B2C9CE1FB128C09FF156293CDF8E6332F548A1B25C0DD4B2848235FD2F05C63203C86A099A0FD8CE2ACC0751808CD4FDEB8C1265827B4C02C753A9557392084C36C97
	AE207DA1A760651597D08F1A9390E107146150BDE867D8F05FC7C34A0C6E2989F445A1656890BAB6167B8F613AC2578B53B4AFABEBEC011C2B56293A9CECDEF4551967C55C987329C4F826G30B564CF25D91CEF56E531D60C551A570A786D2F591E3108EB88E7AFB37DBAAF9BF12DC81ED5EC6CC2BD2321AE9EE08DC08AC05EF8C266B10016F1B1C66FF61E19EA0C286B35E8D5002B07FC21AAB43713B63DEFE4FAE37140843A503150470369187DFE17A36C97B9AEF27E01516969B0EB82DD288EABACF5F824C4
	6810A8F5F8F5ECFABA14CE208B55A1EF0615CF7638DE3076C99853AE8DA28E57750BB61133944D903A7EFE1A3AEE1AC077D2C859E1296B9F8E1410C972AD373DD7F177F06DA744FB5E0FDF8C09B3415199A2774AAB41B8358C4A2BG564B7EFD7572747A37FC82DDE87FB6D9766F0D56F0E341FB437B70D409980B3C4FC12ECB4FABEAE9CE5984E50744FD0962BCD01EA5F1019A4445C0F97967C4BF7757A1EEBD1449D582F70C62F0BD467C17613ED69B6E8CG69105E2A6E63821331BA2C0C8C72494F99D85CEA
	D3426F01BFD22C77408E45FA8FFCD15941704043FCE2A66A3D4A64FB64E145EA0F7CA26D6B034FED273CFDBE55C783A9EEA6F85DEFF45CA3CE58AB3F25742B5CCE39646048965E26C6572ACECA9B7E71F03E351F1976A80B6A040F95DEA566492D27F11EF4C0D98708143E72C42955D96E522B781E6A2A09FE415619F18766CE2B20BE89E54C043C8A20F426D8DFF23A264C81EA821364GB6FB78F15EE2379E676DF65CDF79B43F572E060E65F376FCD9E72CE48D3A5A0FE6E7EDDE26743A626DD1A7784281CA4F
	6A42519F35077397B6263F8B9A5363F25E7CA147EA8C5AE7B25B3BE6CE9C83466FCB23E6FE2FC9FEEBCB2C784D60FC960665270C19791D107C6E31A13F988BBA04F8402F8E29E37C76DFB673FBDD72DBBFB4595D45FA8BB9ACD70D2031FE63CC162B26CA7CFC0ACF1FE560D98E3B3BC45CD565C01DBECB64753F8D526BFF25DEE9936642DA20C91E4577113343596D43AA4C55F4CDF335B8BD188F123C59F867E76FA3324D0AAABC57F2A9C31ADF071CE2B66A7753AA11AB36C35983908590BB5BFCCE7115C1645D
	6DD57A41154EF9D4BB091E03345757885AEDD0AEGB88B5089B0E30E68671DEFE1BD0B8CE25BA17479AE3D4BEC345E1BF0FB3CD7EF36C792705DB44730C14E556146292E1C5E51DC4D46E317443FCF716DA6BC9B27BBE8EC42F2A45415C60875EE47A93A36C1F903448D1C92EBA0793C08B5D7BB42470A6B9C5631E22143FACDD86418FCCD385D91EE0EB37DB2F7881D1BBFAFF46E59A1F46E1038BF1408F6319257DCAA5A45C95CF7E96E16F58D0C71B5827737B4512EC8626C14F6944A73A46E49A151EE69B5A2
	5EDF07E9989F5F1BCC6F85A5135D91FCD2E0B2CEFD2AB2D81FF7D3BBEC007EE64D8D364D9EA897C95CCAE96B5239E28F76438F4C7763B90BDE3109F892AAABE2AE0F25F3C36F47532AC54CD989F5A4D25C0F9F6F8C5ABF62BE3F2C2C4E6BF4D4AABDCDB4AA30357B2E6AACCEDF9AA9566E47A8CF8F14A3G22A3053E0183E24F98FB67A252B463CD4B7345F5AD2F5962A2191E0911063EB130E7E4B26F2F91B2732F853F3D96F13F2AB11F0DE04C783A0D6DCBD87D2EDAF3BD4E894CED0C7A3A20FA54F7FF1A39FE
	A8289EEDF41053333EBD34DF74ED53C3B144F57F2930E727D15772FBEA7EECEE5DC78A2FF9F2B39BE7D7BD7587284B3ACE0C3D5EA0F26BA209CBEC947E50FC1D18AB7FA8BF844FE8A84783A482EC82483FBEA1339C2093A0G9085306CFAC153C9E956C0F983C05EBCF6260D6769F8D6FEF29EBB8BEF19474E30994D5D2723C37A04FC2772777210B628E39447214E616892F99FA6633D49C45F99543F9FCF50F54747181B21F7E1B85E39A65F1AACEECCF5E7E73EC743F5E09791EB5C21D3F83724C370E76B313F
	9FBDABE69E7D3E64D475A4BC45FEC5008EB491G7BF85DE2F87F007BA3639D65B6F8DF5EB195EFB37F14905E6973C3F9FFD3B3F87714CE45FB3309F74E7CE05E05A65E4CC6BF33D967AEF5178C1955E9D3495CE26A5331901955D0EE8812B9DC0ABA2AB465F0104E79B407AB95FB7BE6A8B339BB07A713AB6ED9C5FF1782DD5CFCA6974B2B607D995E0649BDCEE5743BD55810B86DCCEEBD45BD8430F63E481B06CB037D66FA2FE10BDFCCEA7F349008B00DAD76BF89E49C646F7C5E9CD3871EDBFC35EFBFBCDB15
	0BFC37D463F06A1AD7EC179AE159F83B6BA2ECBCDA149EA256541E6A69FDBF641FA65D172F180E65FEEF56609A36E1FEE86E5EC2716406505C7DE7F29D5D86F51E9B4479F3070EF96F9AA827GE4AD003C8860ACC04782118FBE51F70CCC3177EC51BCCD10B03ACCA99E6B5FB7EA2677DD55831B45FA356C9B371338B9FFC98DF7FE6BD940E26BBC7ECCEA59C7712CFFCF7831FF4B21AE9100AC04D88DD084D03DD0742F52D5447A078ED2216B0A3D175E72094E35BA55C1830D1B866657B36BF18F5225F9E9C24B
	76A00B6A659E8478FBGE2G6297327B9C5666168651E64DC236CFC906A73911694F66459F9B271A976618178F34C5B79AF375AC3CBF797453CF7F8900691A3697699A1B14AE55D525386C554A10CF4C63211A9D322EC9D39C4DAAEC6A5DBA9175056ACED95F6CD5871C1A5FE7260F48467BF3F9CADA2F391C8EE508557D775B781D14F12AEFC55F5C33AB280D158EF774ECB5F400BA019F5B36B308B634EFD57D59120EABA6F91E78DBAEB16D59CC67236CBEEBD139B927EA3A110D2167C6F15FB5A34B2AFE162C
	FFEB4C2ABECA565FDDEAD59FAB6B7B83D6754B6179B2E16BFD96917FD27EBE91A7563B3503C44E2B7F5AA666551E3BEFA6550F7DA5D54452D09FDB437D31701F0C9CB76962264DD632191CB6B4AD89DFBFFBF8727AEF0D90B9B6B1EC4D707AD526F77420F9AFD13A3544723B93DA4F757570E77C557BACE50657635018214705D2FC9ED48FF2DB2E3E0CF5AC363DD3E33C3FEFFA7FD575273F775B74294AE0F1D155431F8338F46BF3G2FEEFD8E9050A7BF7B1D21077FBE6AEA5D7CFD940B6B5163B8B7E2E00CD7
	GA481EC824839C94474DD7E3D0446F40BF8DE636841B3CB7CEAD3977253864277F56B40545FFA668EE0FC6A52F4DD6BC77E827F7CG0E172EF9A81261DE9AC09BF91DBD3DBAEB4A710583680BAE351B22593A1DD0AF3E37AD023EB9EE927B5F5F3CF818CC753DCD0BD3F72915FCF96277A5DB9B44BD5B621B44FE7881AAE38D141381522344B7925FE990B9439E4035C4097B566CC6313F0D0C92BCCEB7E25B95D0CE84589075F1E31418F3A7101DB8136F8EF3A6CF23B85361C76562CCFE5B9704DEDF4B9238EA
	091BBDA6F02392774BD201DBA6F1CB830843F1C97E02480DDE31619ADC388872A1G8740A840BC009800F80055G6B81B681645D1C10F988209C20992093C0870838D978640B819CB34BFC94107C7002BA26D7F152350F69F1B5BD1FDA8CFC62A42F3451A31370EA76EA9E552B8FB1AC7DE20D7BD84CF8F8BF3FE7FCEABF0F9B275FC101FF368CF978B97D3F0DE39E24F5F77BD4B91F9246F3A42E41572C791CF83E4FE2574A7A29F24235B7DB5DA57EF8CA3A503BC5C6373D8169B65CBC7599GC2566204CC16CB
	3FCE7DFF4F62509C7B8D0ABF34B8B4477EDE3D18AFA721AEEA315863BF54B03D3D41CF26C93733F1FAFB0AFB689C5EB4213FF1941FF5CBE8FFAB9AC5FF7320AEF00BBC53A8B74BCF3C0559BD05BF537FF962B8B03A78ACB3DDBEEFD7441FE74352DD93F4075E425B757267E8D83A5F845DCF4C636D1672E7DCD83A3B82E63AD53C5DBA7E5CG4F2CDB03E9591CF85D96FE6E155B261EFBB5B66BB5067D66A4F80DE936D92FB12DB6739A43F09F50DC2A8762CF3BEC57A9F167A5CE25BC915766F4683D925FC775C2
	FC3D0A82857E9255EB10625AB9EE4004BBAF3E2D34E17C00AE77EADE198F58B07F6BF63ADC2CBC0B36F13AA11CC8FBFC1E6A6874A8A63D2F13B8C36F48B49C8319FB30F0486B962465D13E901E9417F318FE72F98E9A3AFB585D72725CECDA6FF23A55AA762B8DA24E468F5035F48F0C7549DB4539E783BD9F2D07F2472DE2BE1C46639A6EBB552677076BBF7DBC71577F8FB26EEFB47A74AD53FBDD6327AF3B73D315414674F11AEB7442F88664B8BFFA84F111D0DEACF1E5C5624CBBC6629E4992349BA5EEE613
	205D96ADF06B1B84ED3E445DC6F317DEA887A46E55B6CA8B654592F7FD1B14ABF1CF519CE7A31433EE9338625D54DF21DCA4F18BF64BFB9B09DBCEF32188A8C7C95C4CF67A9BA0A847CBDCD23B20DDA9F16F8C09FB337C5B45795F19860C9BBAE623359A4DEB58D961FBF47E94C13BD2593633BEC334A5E27F3A91C3815F0F251F21F18158B09E3B4FD09B813DAEF9A49F89BEEFBCC0FFAF4A4ED55EA91A6ADC45FC5E9BFA5E3894E4E0BCF89A7EB1791B68785E8B78F8GA66F5A4D063CA71B2E649CA7D4DE726D
	6CB732CF4BF31C3831607E7F007A48C6E8173EC4743F22AD387F77E6997A5C5976C94EDBCFAEE1B14D50E75729E0FD0E507B6433502EC96AD333BBD81FC736987A0CF5CE264F66A96CB38AB2F0DF79BFD21F4782417ABC4EFC995AAD147AACEA8F56674FDB8DFD2A76CEE51FAD136833E209B1DECC7E307C46EE7592619F4DBB8CFFBED30F3FDFF62B038572F3E51CA30776D640626F5ADBD4F09C7E1FBCAC67C2EC2EC17B60113D56348EA055FA007898A7E634BF44250E3F5F72D7034FE0531571F9A85F6033F1
	0F41672536AB637342CB861F4182034F5E5DD746E751AB861FBF6FB3752B7D4A78BC3F5F201DBFA44E1F58EF54578CBE8C4E987BB849007825935B09E3702764F51D24829C62A70B85BEFB2F6A703B54823FCFF59468DA019A303B7C8E35408EF23D1ACBA7B96C4B10827CB424G3F8D71F949F629DE834DEF568D1CA8CB7B5512B49BC9EFA019AC5FEBA0691834B5108C1A53B510B44877829E2E2F0D7BDF2A6F9239CCB05C251B306992EB3344EE61D87CF500CDE2D5BE7770FD2B44522FFFG7E8410AD314FF2
	ACBA36CDA7F932F785FCA76AD4FD0046035982273BG8FE97DAE1AEB1569A49B4FFB8B7801AF14F34599F181ED7F9E189230297BF512456DDC86C64B8E9A536F5C767A2FD77D3C5517C6B29B4873B87D4F29DE1A51298E7CD2107F943616EC71E83035BE87F830280FBEB2FC7A10CBEDA2F95D6F624F3CFC3A6A289039A2E83D0B7E1060A89E299C2DD7FC3DBA0E5F69A6126E469F6D121D2C5A2538FB0E324471B49D16540ACE74A6EA1AD450B7176A0BDC0AA5175CEFEE5F7FCA637A38B4E22BA5DBDDE006DA45
	8E9B768632C56F752A3EDE4D6588F87EFE16B24A9A44C8728C9DB9AFF9C1F69FBBDB0C675FD2693E40AD1159C95935B02681AFB9CADBE234AB16AD9CD53D0A97C4295E869DFF042CF98BE55D67DE528FBCDC77578E9BC9651D44D1FEAE6051C9E637526FF48D41CBB51DC1F47CF7C3116573521551984535509F7DBF9FFF70761F1627A48333DA12C1FFF2GAA65982D5E7277F7295EB931B73E784C42F5F3D1E38FF6D7F05E827BA1378FFF51814C8BC27AAC2A246A458B9EBCF663DDCF3CCFD5B7C8F572B29BA4
	A0BD5A23EA7DC7AB3A3B41B9A58398B1E07F22E40FED0AC35A6C8C4C5F55786853DD60E155A42D7F58B17A77B87D7B9A7EBDCEB147A96678EBB0ECA9E452FF21758151E76A1634708A7AD0G5FDEE235F9A10E456C14BF41C352903DC70E93168AE95383746F61AB568D290E0468F4ACA067FF9A0F9398F5B2BEFB1A16840262A2BEF119487C6CE97A062129083DC6DC7FCCE9DAE3E8AAD6168564B3B97ACD06265B383638C9C5CFE0F7CB07474DF7CB9BE767073DDB6A0D65E7CA31461D98BB23A5235827B8402F
	0995E7344DA3FFA3613FBFB8572F38DC3A626A2BF0BB70DB044AA1DDB56EC3B2C6113FCF3461152CEE9B7DCE792209667FGD0CB87883EE09B62C098GG24C7GGD0CB818294G94G88G88G73F854AC3EE09B62C098GG24C7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFA98GGGG
**end of data**/
}
}

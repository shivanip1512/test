package com.cannontech.dbeditor.wizard.baseline;

/**
 * This type was created in VisualAge.
 */
import java.util.Collections;
import java.util.List;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.baseline.Baseline;
import com.cannontech.common.gui.util.TextFieldDocument;

public class BaselineMainPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
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
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == BaselineMainPanel.this.getJComboBoxHoliday()) 
				connEtoC3(e);
			if (e.getSource() == BaselineMainPanel.this.getJCheckBoxDayChooser()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == BaselineMainPanel.this.getJTextFieldPreviousDays()) 
				connEtoC4(e);
			if (e.getSource() == BaselineMainPanel.this.getJTextFieldLoadPercent()) 
				connEtoC1(e);
			if (e.getSource() == BaselineMainPanel.this.getJTextFieldCalcDays()) 
				connEtoC2(e);
			if (e.getSource() == BaselineMainPanel.this.getJTextFieldName()) 
				connEtoC7(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public BaselineMainPanel() {
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
 * connEtoC2:  (JTextFieldCalcDays.caret.caretUpdate(javax.swing.event.CaretEvent) --> BaselineMainPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> BaselineMainPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JCheckBoxDayChooser.action.actionPerformed(java.awt.event.ActionEvent) --> BaselineMainPanel.jCheckBoxDayChooser_Action(Ljava.awt.event.ActionEvent;)V)
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
 * connEtoC7:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> BaselineMainPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(javax.swing.event.CaretEvent arg1) {
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
			ivjLocalBorder2.setTitle("Excluded Days");
			ivjJCheckBoxDayChooser.setBorder(ivjLocalBorder2);
			
			ivjJCheckBoxDayChooser.setHolidayVisible( false );
			
			//init our check box fields so Saturday and Sunday are checked
			ivjJCheckBoxDayChooser.setSelectedCheckBoxes( "YNNNNNYN" );

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
 * Return the JLabelHoliday1 property value.
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
			ivjJLabelName.setEnabled(true);
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
			ivjLocalBorder.setTitle("Baseline Properties");
			ivjJPanelBaseLine = new javax.swing.JPanel();
			ivjJPanelBaseLine.setName("JPanelBaseLine");
			ivjJPanelBaseLine.setBorder(ivjLocalBorder);
			ivjJPanelBaseLine.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelCalcDays = new java.awt.GridBagConstraints();
			constraintsJLabelCalcDays.gridx = 0; constraintsJLabelCalcDays.gridy = 3;
			constraintsJLabelCalcDays.ipadx = 6;
			constraintsJLabelCalcDays.ipady = -1;
			constraintsJLabelCalcDays.insets = new java.awt.Insets(5, 15, 3, 4);
			getJPanelBaseLine().add(getJLabelCalcDays(), constraintsJLabelCalcDays);

			java.awt.GridBagConstraints constraintsJLabelLoadPercent = new java.awt.GridBagConstraints();
			constraintsJLabelLoadPercent.gridx = 0; constraintsJLabelLoadPercent.gridy = 2;
			constraintsJLabelLoadPercent.ipadx = 11;
			constraintsJLabelLoadPercent.ipady = -1;
			constraintsJLabelLoadPercent.insets = new java.awt.Insets(6, 15, 4, 35);
			getJPanelBaseLine().add(getJLabelLoadPercent(), constraintsJLabelLoadPercent);

			java.awt.GridBagConstraints constraintsJLabelPreviousDays = new java.awt.GridBagConstraints();
			constraintsJLabelPreviousDays.gridx = 0; constraintsJLabelPreviousDays.gridy = 1;
			constraintsJLabelPreviousDays.ipadx = 3;
			constraintsJLabelPreviousDays.ipady = -1;
			constraintsJLabelPreviousDays.insets = new java.awt.Insets(3, 15, 5, 1);
			getJPanelBaseLine().add(getJLabelPreviousDays(), constraintsJLabelPreviousDays);

			java.awt.GridBagConstraints constraintsJComboBoxHoliday = new java.awt.GridBagConstraints();
			constraintsJComboBoxHoliday.gridx = 1; constraintsJComboBoxHoliday.gridy = 4;
			constraintsJComboBoxHoliday.gridwidth = 2;
			constraintsJComboBoxHoliday.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxHoliday.weightx = 1.0;
			constraintsJComboBoxHoliday.ipadx = 26;
			constraintsJComboBoxHoliday.insets = new java.awt.Insets(2, 2, 6, 65);
			getJPanelBaseLine().add(getJComboBoxHoliday(), constraintsJComboBoxHoliday);

			java.awt.GridBagConstraints constraintsJTextFieldPreviousDays = new java.awt.GridBagConstraints();
			constraintsJTextFieldPreviousDays.gridx = 1; constraintsJTextFieldPreviousDays.gridy = 1;
			constraintsJTextFieldPreviousDays.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldPreviousDays.weightx = 1.0;
			constraintsJTextFieldPreviousDays.ipadx = 35;
			constraintsJTextFieldPreviousDays.insets = new java.awt.Insets(3, 2, 3, 1);
			getJPanelBaseLine().add(getJTextFieldPreviousDays(), constraintsJTextFieldPreviousDays);

			java.awt.GridBagConstraints constraintsJTextFieldLoadPercent = new java.awt.GridBagConstraints();
			constraintsJTextFieldLoadPercent.gridx = 1; constraintsJTextFieldLoadPercent.gridy = 2;
			constraintsJTextFieldLoadPercent.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldLoadPercent.weightx = 1.0;
			constraintsJTextFieldLoadPercent.ipadx = 35;
			constraintsJTextFieldLoadPercent.insets = new java.awt.Insets(4, 2, 4, 1);
			getJPanelBaseLine().add(getJTextFieldLoadPercent(), constraintsJTextFieldLoadPercent);

			java.awt.GridBagConstraints constraintsJLabelPerc = new java.awt.GridBagConstraints();
			constraintsJLabelPerc.gridx = 2; constraintsJLabelPerc.gridy = 2;
			constraintsJLabelPerc.ipadx = 8;
			constraintsJLabelPerc.ipady = -2;
			constraintsJLabelPerc.insets = new java.awt.Insets(5, 2, 6, 155);
			getJPanelBaseLine().add(getJLabelPerc(), constraintsJLabelPerc);

			java.awt.GridBagConstraints constraintsJTextFieldCalcDays = new java.awt.GridBagConstraints();
			constraintsJTextFieldCalcDays.gridx = 1; constraintsJTextFieldCalcDays.gridy = 3;
			constraintsJTextFieldCalcDays.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldCalcDays.weightx = 1.0;
			constraintsJTextFieldCalcDays.ipadx = 35;
			constraintsJTextFieldCalcDays.insets = new java.awt.Insets(4, 2, 2, 1);
			getJPanelBaseLine().add(getJTextFieldCalcDays(), constraintsJTextFieldCalcDays);

			java.awt.GridBagConstraints constraintsJLabelHoliday = new java.awt.GridBagConstraints();
			constraintsJLabelHoliday.gridx = 0; constraintsJLabelHoliday.gridy = 4;
			constraintsJLabelHoliday.ipadx = 11;
			constraintsJLabelHoliday.ipady = -1;
			constraintsJLabelHoliday.insets = new java.awt.Insets(4, 15, 9, 74);
			getJPanelBaseLine().add(getJLabelHoliday(), constraintsJLabelHoliday);

			java.awt.GridBagConstraints constraintsJCheckBoxDayChooser = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDayChooser.gridx = 0; constraintsJCheckBoxDayChooser.gridy = 5;
			constraintsJCheckBoxDayChooser.gridwidth = 3;
			constraintsJCheckBoxDayChooser.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJCheckBoxDayChooser.weightx = 1.0;
			constraintsJCheckBoxDayChooser.weighty = 1.0;
			constraintsJCheckBoxDayChooser.ipadx = -389;
			constraintsJCheckBoxDayChooser.ipady = 88;
			constraintsJCheckBoxDayChooser.insets = new java.awt.Insets(6, 15, 31, 15);
			getJPanelBaseLine().add(getJCheckBoxDayChooser(), constraintsJCheckBoxDayChooser);

			java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
			constraintsJTextFieldName.gridx = 1; constraintsJTextFieldName.gridy = 0;
			constraintsJTextFieldName.gridwidth = 2;
			constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldName.weightx = 1.0;
			constraintsJTextFieldName.ipadx = 148;
			constraintsJTextFieldName.ipady = 3;
			constraintsJTextFieldName.insets = new java.awt.Insets(24, 2, 2, 65);
			getJPanelBaseLine().add(getJTextFieldName(), constraintsJTextFieldName);

			java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
			constraintsJLabelName.gridx = 0; constraintsJLabelName.gridy = 0;
			constraintsJLabelName.ipadx = 21;
			constraintsJLabelName.ipady = -1;
			constraintsJLabelName.insets = new java.awt.Insets(26, 15, 5, 74);
			getJPanelBaseLine().add(getJLabelName(), constraintsJLabelName);
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
 * Return the JComboBoxHoliday1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Text field that holds the name of the baseline");
			ivjJTextFieldName.setEnabled(true);
			// user code begin {1}
			ivjJTextFieldName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_BASELINE_NAME_LENGTH,
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
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	
	Baseline basil;
	
	if( o != null )
		basil = (Baseline)o;
	else
		basil = new Baseline();
	
	basil.getBaseline().setBaselineName(getJTextFieldName().getText());
		
	basil.getBaseline().setDaysUsed( 
			new Integer( Integer.parseInt(getJTextFieldPreviousDays().getText())) );
		 
	basil.getBaseline().setPercentWindow( 
			new Integer( Integer.parseInt(getJTextFieldLoadPercent().getText())) );
		
	basil.getBaseline().setCalcDays(
			new Integer( Integer.parseInt(getJTextFieldCalcDays().getText())) );
		
	basil.getBaseline().setExcludedWeekdays(
			getJCheckBoxDayChooser().getSelectedDays8Chars().substring(0,7) );

	if( getJComboBoxHoliday().getSelectedItem() != null )
		basil.getBaseline().setHolidaysUsed( 
			new Integer( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getSelectedItem()).getHolidayScheduleID() ) );
	else
		basil.getBaseline().setHolidaysUsed( new Integer(0) );
	
	return basil;
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
	getJTextFieldPreviousDays().addCaretListener(ivjEventHandler);
	getJTextFieldLoadPercent().addCaretListener(ivjEventHandler);
	getJTextFieldCalcDays().addCaretListener(ivjEventHandler);
	getJComboBoxHoliday().addActionListener(ivjEventHandler);
	getJCheckBoxDayChooser().addActionListener(ivjEventHandler);
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
		setName("CustomerBaseLinePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(402, 348);

		java.awt.GridBagConstraints constraintsJPanelBaseLine = new java.awt.GridBagConstraints();
		constraintsJPanelBaseLine.gridx = 1; constraintsJPanelBaseLine.gridy = 1;
		constraintsJPanelBaseLine.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelBaseLine.weightx = 1.0;
		constraintsJPanelBaseLine.weighty = 1.0;
		constraintsJPanelBaseLine.ipadx = -10;
		constraintsJPanelBaseLine.ipady = -20;
		constraintsJPanelBaseLine.insets = new java.awt.Insets(16, 16, 19, 17);
		add(getJPanelBaseLine(), constraintsJPanelBaseLine);
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
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		BaselineMainPanel aBaselineMainPanel;
		aBaselineMainPanel = new BaselineMainPanel();
		frame.setContentPane(aBaselineMainPanel);
		frame.setSize(aBaselineMainPanel.getSize());
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
	Baseline basil;
	
		if( o != null )
			basil = (Baseline)o;
		else
			basil = new Baseline();
	
	String name = basil.getBaseline().getBaselineName();
	if( name != null )
	{
		getJTextFieldName().setText(name);
	}
	
	Integer temp = basil.getBaseline().getDaysUsed();
	if( temp != null )
	{
		getJTextFieldPreviousDays().setText( temp.toString() );
		temp = null;
	}		
	
	temp = basil.getBaseline().getPercentWindow();
	if( temp != null )
	{
		getJTextFieldLoadPercent().setText( temp.toString() );
		temp = null;
	}
	
	temp = basil.getBaseline().getCalcDays();
	if( temp != null )
	{
		getJTextFieldCalcDays().setText( temp.toString() );
		temp = null;
	}

	String s = basil.getBaseline().getExcludedWeekdays();
	if( s != null )
	{
		getJCheckBoxDayChooser().setSelectedCheckBoxes( s + "N" ); 
		
	}

	Integer holDay = basil.getBaseline().getHolidaysUsed();
	if( holDay != null )
		for( int i = 0; i < getJComboBoxHoliday().getItemCount(); i++ )
			if( ((com.cannontech.database.data.lite.LiteHolidaySchedule)getJComboBoxHoliday().getItemAt(i)).getHolidayScheduleID()
				 == holDay.intValue() )
			{
				getJComboBoxHoliday().setSelectedIndex(i);
				break;
			}


}
}

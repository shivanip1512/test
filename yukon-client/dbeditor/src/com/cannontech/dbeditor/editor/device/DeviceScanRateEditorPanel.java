package com.cannontech.dbeditor.editor.device;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.capcontrol.CapBankController6510;
import com.cannontech.database.data.device.CCUBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IEDMeter;
import com.cannontech.database.data.device.LCUBase;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.PagingTapTerminal;
import com.cannontech.database.data.device.RTUBase;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.RepeaterBase;
import com.cannontech.database.data.device.TCUBase;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceScanRate;
import com.cannontech.database.db.device.DeviceWindow;

/**
 * This type was created in VisualAge.
 */
public class DeviceScanRateEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private JCheckBox ivjAccumulatorRateCheckBox = null;
	private JCheckBox ivjIntegrityRateCheckBox = null;
	private JComboBox ivjAccumulatorRateComboBox = null;
	private JComboBox ivjIntegrityRateComboBox = null;
	private JComboBox ivjAccumulatorGroupComboBox = null;
	private JComboBox ivjIntegrityGroupComboBox = null;
	private JCheckBox ivjPeriodicHealthCheckBox = null;
	private JComboBox ivjPeriodicHealthGroupComboBox = null;
	private JComboBox ivjPeriodicHealthIntervalComboBox = null;
	private JComboBox ivjJComboBoxAltAccRate = null;
	private JComboBox ivjJComboBoxAltHealthChk = null;
	private JComboBox ivjJComboBoxAltIntegrityRate = null;
	private JLabel ivjJLabelAltIntervalAcc = null;
	private JLabel ivjJLabelAltIntervalHlth = null;
	private JLabel ivjJLabelAltIntervalInt = null;
	private JLabel ivjJLabelGroupAcc = null;
	private JLabel ivjJLabelGroupHlth = null;
	private JLabel ivjJLabelGroupInt = null;
	private JLabel ivjJLabelIntervalAcc = null;
	private JLabel ivjJLabelIntervalHlth = null;
	private JLabel ivjJLabelIntervalInt = null;
	private JCheckBox ivjJCheckBoxScanWindow = null;
	private JComboBox ivjJComboBoxScanWindowType = null;
	private JLabel ivjJLabelOpen = null;
	private JLabel ivjJLabelType = null;
	private JPanel ivjJPanelScanWindow = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldOpen = null;
	private JPanel ivjJPanelScanConfig = null;
	private JLabel ivjJLabelClose = null;
	private JLabel ivjJLabelhhmm = null;
	private JLabel ivjJLabelhhmm1 = null;
	private com.cannontech.common.gui.util.JTextFieldTimeEntry ivjJTextFieldClose = null;

	
	private static final String[] ALT_SELECTIONS =
	{
		"1 second",
		"2 second",
		"5 second",
		"10 second",
		"15 second",
		"30 second",
		"1 minute",
		"2 minute",
		"3 minute",
		"5 minute",
		"10 minute",
		"15 minute",
		"30 minute",
		"1 hour",
		"2 hour",
		"6 hour",
		"12 hour",
		"1 day"		
	};


/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceScanRateEditorPanel() {
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
	if (e.getSource() == getPeriodicHealthCheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getAccumulatorRateCheckBox()) 
		connEtoC4(e);
	if (e.getSource() == getIntegrityRateCheckBox()) 
		connEtoC2(e);
	if (e.getSource() == getAccumulatorRateComboBox()) 
		connEtoC3(e);
	if (e.getSource() == getPeriodicHealthIntervalComboBox()) 
		connEtoC6(e);
	if (e.getSource() == getIntegrityRateComboBox()) 
		connEtoC8(e);
	if (e.getSource() == getIntegrityGroupComboBox()) 
		connEtoC5(e);
	if (e.getSource() == getAccumulatorGroupComboBox()) 
		connEtoC7(e);
	if (e.getSource() == getPeriodicHealthGroupComboBox()) 
		connEtoC9(e);
	if (e.getSource() == getJComboBoxAltHealthChk()) 
		connEtoC11(e);
	if (e.getSource() == getJComboBoxAltAccRate()) 
		connEtoC10(e);
	if (e.getSource() == getJComboBoxAltIntegrityRate()) 
		connEtoC12(e);
	if (e.getSource() == getJCheckBoxScanWindow()) 
		connEtoC13(e);
	if (e.getSource() == getJComboBoxScanWindowType()) 
		connEtoC16(e);
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
	if (e.getSource() == getJTextFieldOpen()) 
		connEtoC14(e);
	if (e.getSource() == getJTextFieldClose()) 
		connEtoC15(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (GeneralRateCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		getPeriodicHealthIntervalComboBox().setEnabled(getPeriodicHealthCheckBox().isSelected());
		getPeriodicHealthGroupComboBox().setEnabled(getPeriodicHealthCheckBox().isSelected());
		getJComboBoxAltHealthChk().setEnabled(getPeriodicHealthCheckBox().isSelected());
		
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (IntegrityRateCheckBox1.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
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
 * connEtoC11:  (IntegrityRateComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.ActionEvent arg1) {
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
 * connEtoC12:  (IntegrityGroupComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(java.awt.event.ActionEvent arg1) {
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
 * connEtoC13:  (JCheckBoxScanWindow.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.jCheckBoxScanWindow_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxScanWindow_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC14:  (JTextFieldOpen.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC15:  (JTextFieldClose.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC15(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC16:  (JComboBoxScanWindowType.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC16(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (IntegrityRateCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		getIntegrityRateComboBox().setEnabled(getIntegrityRateCheckBox().isSelected());
		getIntegrityGroupComboBox().setEnabled(getIntegrityRateCheckBox().isSelected());
		getJComboBoxAltIntegrityRate().setEnabled(getIntegrityRateCheckBox().isSelected());
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (AccumulatorRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}

		updateAltJComboBox( getAccumulatorRateComboBox(), getJComboBoxAltAccRate() );

		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (AccumulatorRateCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		
		getAccumulatorRateComboBox().setEnabled(getAccumulatorRateCheckBox().isSelected());
		getAccumulatorGroupComboBox().setEnabled(getAccumulatorRateCheckBox().isSelected());
		getJComboBoxAltAccRate().setEnabled(getAccumulatorRateCheckBox().isSelected());

		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (GeneralRateComboBox12.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * connEtoC6:  (GeneralRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}

		updateAltJComboBox( getPeriodicHealthIntervalComboBox(), getJComboBoxAltHealthChk() );

		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (GeneralRateComboBox11.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
 * connEtoC8:  (IntegrityRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}

		updateAltJComboBox( getIntegrityRateComboBox(), getJComboBoxAltIntegrityRate() );

		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (GeneralGroupComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceScanRateEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
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
 * Return the GeneralRateComboBox11 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getAccumulatorGroupComboBox() {
	if (ivjAccumulatorGroupComboBox == null) {
		try {
			ivjAccumulatorGroupComboBox = new javax.swing.JComboBox();
			ivjAccumulatorGroupComboBox.setName("AccumulatorGroupComboBox");
			ivjAccumulatorGroupComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjAccumulatorGroupComboBox.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			ivjAccumulatorGroupComboBox.addItem("Default");
			ivjAccumulatorGroupComboBox.addItem("FirstGroup");
			ivjAccumulatorGroupComboBox.addItem("SecondGroup");
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAccumulatorGroupComboBox;
}
/**
 * Return the AccumulatorRateCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getAccumulatorRateCheckBox() {
	if (ivjAccumulatorRateCheckBox == null) {
		try {
			ivjAccumulatorRateCheckBox = new javax.swing.JCheckBox();
			ivjAccumulatorRateCheckBox.setName("AccumulatorRateCheckBox");
			ivjAccumulatorRateCheckBox.setText("Accumulator Rate");
			ivjAccumulatorRateCheckBox.setVisible(true);
			ivjAccumulatorRateCheckBox.setActionCommand("AccumulatorRateCheckBox");
			ivjAccumulatorRateCheckBox.setFont(new java.awt.Font("Arial", 1, 12));
			ivjAccumulatorRateCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAccumulatorRateCheckBox;
}
/**
 * Return the AccumulatorComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getAccumulatorRateComboBox() {
	if (ivjAccumulatorRateComboBox == null) {
		try {
			ivjAccumulatorRateComboBox = new javax.swing.JComboBox();
			ivjAccumulatorRateComboBox.setName("AccumulatorRateComboBox");
			ivjAccumulatorRateComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjAccumulatorRateComboBox.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			//Accumulator Rate JComboBox
			ivjAccumulatorRateComboBox.addItem("1 minute");
			ivjAccumulatorRateComboBox.addItem("2 minute");
			ivjAccumulatorRateComboBox.addItem("3 minute");
			ivjAccumulatorRateComboBox.addItem("5 minute");
			ivjAccumulatorRateComboBox.addItem("10 minute");
			ivjAccumulatorRateComboBox.addItem("15 minute");
			ivjAccumulatorRateComboBox.addItem("30 minute");
			ivjAccumulatorRateComboBox.addItem("1 hour");
			ivjAccumulatorRateComboBox.addItem("2 hour");
			ivjAccumulatorRateComboBox.addItem("6 hour");
			ivjAccumulatorRateComboBox.addItem("12 hour");
			ivjAccumulatorRateComboBox.addItem("1 day");
			
			//default value
			ivjAccumulatorRateComboBox.setSelectedItem("1 day");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAccumulatorRateComboBox;
}
/**
 * Return the GeneralRateComboBox12 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getIntegrityGroupComboBox() {
	if (ivjIntegrityGroupComboBox == null) {
		try {
			ivjIntegrityGroupComboBox = new javax.swing.JComboBox();
			ivjIntegrityGroupComboBox.setName("IntegrityGroupComboBox");
			ivjIntegrityGroupComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjIntegrityGroupComboBox.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			ivjIntegrityGroupComboBox.addItem("Default");
			ivjIntegrityGroupComboBox.addItem("FirstGroup");
			ivjIntegrityGroupComboBox.addItem("SecondGroup");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIntegrityGroupComboBox;
}
/**
 * Return the IntegrityRateCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getIntegrityRateCheckBox() {
	if (ivjIntegrityRateCheckBox == null) {
		try {
			ivjIntegrityRateCheckBox = new javax.swing.JCheckBox();
			ivjIntegrityRateCheckBox.setName("IntegrityRateCheckBox");
			ivjIntegrityRateCheckBox.setText("Integrity Rate");
			ivjIntegrityRateCheckBox.setVisible(true);
			ivjIntegrityRateCheckBox.setActionCommand("IntegrityRateCheckBox");
			ivjIntegrityRateCheckBox.setFont(new java.awt.Font("Arial", 1, 12));
			ivjIntegrityRateCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIntegrityRateCheckBox;
}
/**
 * Return the IntegrityComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getIntegrityRateComboBox() {
	if (ivjIntegrityRateComboBox == null) {
		try {
			ivjIntegrityRateComboBox = new javax.swing.JComboBox();
			ivjIntegrityRateComboBox.setName("IntegrityRateComboBox");
			ivjIntegrityRateComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjIntegrityRateComboBox.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			//Integerity Rate JComboBox
			ivjIntegrityRateComboBox.addItem("5 seconds");
			ivjIntegrityRateComboBox.addItem("10 seconds");
			ivjIntegrityRateComboBox.addItem("15 seconds");
			ivjIntegrityRateComboBox.addItem("30 seconds");
			ivjIntegrityRateComboBox.addItem("1 minute");
			ivjIntegrityRateComboBox.addItem("2 minute");
			ivjIntegrityRateComboBox.addItem("3 minute");
			ivjIntegrityRateComboBox.addItem("5 minute");
			ivjIntegrityRateComboBox.addItem("10 minute");
			ivjIntegrityRateComboBox.addItem("15 minute");
			ivjIntegrityRateComboBox.addItem("30 minute");
			ivjIntegrityRateComboBox.addItem("1 hour");
			ivjIntegrityRateComboBox.addItem("2 hour");
			ivjIntegrityRateComboBox.addItem("6 hour");
			ivjIntegrityRateComboBox.addItem("12 hour");
			ivjIntegrityRateComboBox.addItem("1 day");

			//default value
			ivjIntegrityRateComboBox.setSelectedItem("5 minute");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIntegrityRateComboBox;
}
/**
 * Return the JCheckBoxScanWindow property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxScanWindow() {
	if (ivjJCheckBoxScanWindow == null) {
		try {
			ivjJCheckBoxScanWindow = new javax.swing.JCheckBox();
			ivjJCheckBoxScanWindow.setName("JCheckBoxScanWindow");
			ivjJCheckBoxScanWindow.setText("Enabled");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxScanWindow;
}
/**
 * Return the JComboBoxAltAccRate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxAltAccRate() {
	if (ivjJComboBoxAltAccRate == null) {
		try {
			ivjJComboBoxAltAccRate = new javax.swing.JComboBox();
			ivjJComboBoxAltAccRate.setName("JComboBoxAltAccRate");
			ivjJComboBoxAltAccRate.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjJComboBoxAltAccRate.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			for( int i = 0; i < ALT_SELECTIONS.length; i++ )
				ivjJComboBoxAltAccRate.addItem( ALT_SELECTIONS[i] );


			//default value
			updateAltJComboBox( getAccumulatorRateComboBox(), getJComboBoxAltAccRate() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxAltAccRate;
}
/**
 * Return the IntegrityRateComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxAltHealthChk() {
	if (ivjJComboBoxAltHealthChk == null) {
		try {
			ivjJComboBoxAltHealthChk = new javax.swing.JComboBox();
			ivjJComboBoxAltHealthChk.setName("JComboBoxAltHealthChk");
			ivjJComboBoxAltHealthChk.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjJComboBoxAltHealthChk.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			for( int i = 0; i < ALT_SELECTIONS.length; i++ )
				ivjJComboBoxAltHealthChk.addItem( ALT_SELECTIONS[i] );


			//default value
			updateAltJComboBox( getPeriodicHealthIntervalComboBox(), getJComboBoxAltHealthChk() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxAltHealthChk;
}
/**
 * Return the JComboBoxAltIntegrityRate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxAltIntegrityRate() {
	if (ivjJComboBoxAltIntegrityRate == null) {
		try {
			ivjJComboBoxAltIntegrityRate = new javax.swing.JComboBox();
			ivjJComboBoxAltIntegrityRate.setName("JComboBoxAltIntegrityRate");
			ivjJComboBoxAltIntegrityRate.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjJComboBoxAltIntegrityRate.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			for( int i = 0; i < ALT_SELECTIONS.length; i++ )
				ivjJComboBoxAltIntegrityRate.addItem( ALT_SELECTIONS[i] );

			//default value
			updateAltJComboBox( getIntegrityRateComboBox(), getJComboBoxAltIntegrityRate() );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxAltIntegrityRate;
}
/**
 * Return the JComboBoxScanWindowType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxScanWindowType() {
	if (ivjJComboBoxScanWindowType == null) {
		try {
			ivjJComboBoxScanWindowType = new javax.swing.JComboBox();
			ivjJComboBoxScanWindowType.setName("JComboBoxScanWindowType");
			ivjJComboBoxScanWindowType.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjJComboBoxScanWindowType.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			ivjJComboBoxScanWindowType.addItem( DeviceWindow.TYPE_SCAN );
			//ivjJComboBoxScanWindowType.addItem( DeviceWindow.TYPE_PEAK );
			//ivjJComboBoxScanWindowType.addItem( DeviceWindow.TYPE_ALT_RATE );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxScanWindowType;
}
/**
 * Return the JLabelAltInterval1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAltIntervalAcc() {
	if (ivjJLabelAltIntervalAcc == null) {
		try {
			ivjJLabelAltIntervalAcc = new javax.swing.JLabel();
			ivjJLabelAltIntervalAcc.setName("JLabelAltIntervalAcc");
			ivjJLabelAltIntervalAcc.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAltIntervalAcc.setText("Alt Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAltIntervalAcc;
}
/**
 * Return the JLabelAltInterval property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAltIntervalHlth() {
	if (ivjJLabelAltIntervalHlth == null) {
		try {
			ivjJLabelAltIntervalHlth = new javax.swing.JLabel();
			ivjJLabelAltIntervalHlth.setName("JLabelAltIntervalHlth");
			ivjJLabelAltIntervalHlth.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAltIntervalHlth.setText("Alt Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAltIntervalHlth;
}
/**
 * Return the JLabelAltInterval2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAltIntervalInt() {
	if (ivjJLabelAltIntervalInt == null) {
		try {
			ivjJLabelAltIntervalInt = new javax.swing.JLabel();
			ivjJLabelAltIntervalInt.setName("JLabelAltIntervalInt");
			ivjJLabelAltIntervalInt.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAltIntervalInt.setText("Alt Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAltIntervalInt;
}
/**
 * Return the JLabelDuration property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelClose() {
	if (ivjJLabelClose == null) {
		try {
			ivjJLabelClose = new javax.swing.JLabel();
			ivjJLabelClose.setName("JLabelClose");
			ivjJLabelClose.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelClose.setText("Close:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelClose;
}
/**
 * Return the ScanGroupLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroupAcc() {
	if (ivjJLabelGroupAcc == null) {
		try {
			ivjJLabelGroupAcc = new javax.swing.JLabel();
			ivjJLabelGroupAcc.setName("JLabelGroupAcc");
			ivjJLabelGroupAcc.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroupAcc.setText("Group:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroupAcc;
}
/**
 * Return the ScanGroupLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroupHlth() {
	if (ivjJLabelGroupHlth == null) {
		try {
			ivjJLabelGroupHlth = new javax.swing.JLabel();
			ivjJLabelGroupHlth.setName("JLabelGroupHlth");
			ivjJLabelGroupHlth.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroupHlth.setText("Group:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroupHlth;
}
/**
 * Return the ScanGroupLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroupInt() {
	if (ivjJLabelGroupInt == null) {
		try {
			ivjJLabelGroupInt = new javax.swing.JLabel();
			ivjJLabelGroupInt.setName("JLabelGroupInt");
			ivjJLabelGroupInt.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroupInt.setText("Group:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroupInt;
}
/**
 * Return the JLabelhhmm property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelhhmm() {
	if (ivjJLabelhhmm == null) {
		try {
			ivjJLabelhhmm = new javax.swing.JLabel();
			ivjJLabelhhmm.setName("JLabelhhmm");
			ivjJLabelhhmm.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJLabelhhmm.setText("(HH:mm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelhhmm;
}
/**
 * Return the JLabelhhmm1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelhhmm1() {
	if (ivjJLabelhhmm1 == null) {
		try {
			ivjJLabelhhmm1 = new javax.swing.JLabel();
			ivjJLabelhhmm1.setName("JLabelhhmm1");
			ivjJLabelhhmm1.setFont(new java.awt.Font("dialog", 0, 10));
			ivjJLabelhhmm1.setText("(HH:mm)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelhhmm1;
}
/**
 * Return the ScanIntervalLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelIntervalAcc() {
	if (ivjJLabelIntervalAcc == null) {
		try {
			ivjJLabelIntervalAcc = new javax.swing.JLabel();
			ivjJLabelIntervalAcc.setName("JLabelIntervalAcc");
			ivjJLabelIntervalAcc.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelIntervalAcc.setText("Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelIntervalAcc;
}
/**
 * Return the ScanIntervalLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelIntervalHlth() {
	if (ivjJLabelIntervalHlth == null) {
		try {
			ivjJLabelIntervalHlth = new javax.swing.JLabel();
			ivjJLabelIntervalHlth.setName("JLabelIntervalHlth");
			ivjJLabelIntervalHlth.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelIntervalHlth.setText("Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelIntervalHlth;
}
/**
 * Return the ScanIntervalLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelIntervalInt() {
	if (ivjJLabelIntervalInt == null) {
		try {
			ivjJLabelIntervalInt = new javax.swing.JLabel();
			ivjJLabelIntervalInt.setName("JLabelIntervalInt");
			ivjJLabelIntervalInt.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelIntervalInt.setText("Interval:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelIntervalInt;
}
/**
 * Return the JLabelOpen property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOpen() {
	if (ivjJLabelOpen == null) {
		try {
			ivjJLabelOpen = new javax.swing.JLabel();
			ivjJLabelOpen.setName("JLabelOpen");
			ivjJLabelOpen.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelOpen.setText("Open:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOpen;
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
 * Return the JPanelScanConfig property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelScanConfig() {
	if (ivjJPanelScanConfig == null) {
		try {
			ivjJPanelScanConfig = new javax.swing.JPanel();
			ivjJPanelScanConfig.setName("JPanelScanConfig");
			ivjJPanelScanConfig.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsPeriodicHealthCheckBox = new java.awt.GridBagConstraints();
			constraintsPeriodicHealthCheckBox.gridx = 1; constraintsPeriodicHealthCheckBox.gridy = 4;
			constraintsPeriodicHealthCheckBox.gridwidth = 2;
			constraintsPeriodicHealthCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeriodicHealthCheckBox.ipadx = 40;
			constraintsPeriodicHealthCheckBox.ipady = -3;
			constraintsPeriodicHealthCheckBox.insets = new java.awt.Insets(2, 5, 0, 7);
			getJPanelScanConfig().add(getPeriodicHealthCheckBox(), constraintsPeriodicHealthCheckBox);

			java.awt.GridBagConstraints constraintsJLabelIntervalHlth = new java.awt.GridBagConstraints();
			constraintsJLabelIntervalHlth.gridx = 1; constraintsJLabelIntervalHlth.gridy = 5;
			constraintsJLabelIntervalHlth.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelIntervalHlth.ipadx = 4;
			constraintsJLabelIntervalHlth.insets = new java.awt.Insets(2, 22, 2, 20);
			getJPanelScanConfig().add(getJLabelIntervalHlth(), constraintsJLabelIntervalHlth);

			java.awt.GridBagConstraints constraintsJLabelAltIntervalHlth = new java.awt.GridBagConstraints();
			constraintsJLabelAltIntervalHlth.gridx = 1; constraintsJLabelAltIntervalHlth.gridy = 6;
			constraintsJLabelAltIntervalHlth.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAltIntervalHlth.ipadx = 4;
			constraintsJLabelAltIntervalHlth.insets = new java.awt.Insets(4, 22, 2, 0);
			getJPanelScanConfig().add(getJLabelAltIntervalHlth(), constraintsJLabelAltIntervalHlth);

			java.awt.GridBagConstraints constraintsJComboBoxAltHealthChk = new java.awt.GridBagConstraints();
			constraintsJComboBoxAltHealthChk.gridx = 2; constraintsJComboBoxAltHealthChk.gridy = 6;
			constraintsJComboBoxAltHealthChk.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxAltHealthChk.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxAltHealthChk.weightx = 1.0;
			constraintsJComboBoxAltHealthChk.ipady = -7;
			constraintsJComboBoxAltHealthChk.insets = new java.awt.Insets(3, 0, 2, 6);
			getJPanelScanConfig().add(getJComboBoxAltHealthChk(), constraintsJComboBoxAltHealthChk);

			java.awt.GridBagConstraints constraintsPeriodicHealthIntervalComboBox = new java.awt.GridBagConstraints();
			constraintsPeriodicHealthIntervalComboBox.gridx = 2; constraintsPeriodicHealthIntervalComboBox.gridy = 5;
			constraintsPeriodicHealthIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPeriodicHealthIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeriodicHealthIntervalComboBox.weightx = 1.0;
			constraintsPeriodicHealthIntervalComboBox.ipady = -7;
			constraintsPeriodicHealthIntervalComboBox.insets = new java.awt.Insets(1, 0, 2, 6);
			getJPanelScanConfig().add(getPeriodicHealthIntervalComboBox(), constraintsPeriodicHealthIntervalComboBox);

			java.awt.GridBagConstraints constraintsJLabelGroupHlth = new java.awt.GridBagConstraints();
			constraintsJLabelGroupHlth.gridx = 3; constraintsJLabelGroupHlth.gridy = 5;
			constraintsJLabelGroupHlth.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGroupHlth.ipadx = 7;
			constraintsJLabelGroupHlth.insets = new java.awt.Insets(2, 7, 2, 0);
			getJPanelScanConfig().add(getJLabelGroupHlth(), constraintsJLabelGroupHlth);

			java.awt.GridBagConstraints constraintsPeriodicHealthGroupComboBox = new java.awt.GridBagConstraints();
			constraintsPeriodicHealthGroupComboBox.gridx = 4; constraintsPeriodicHealthGroupComboBox.gridy = 5;
			constraintsPeriodicHealthGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPeriodicHealthGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeriodicHealthGroupComboBox.weightx = 1.0;
			constraintsPeriodicHealthGroupComboBox.ipadx = 22;
			constraintsPeriodicHealthGroupComboBox.ipady = -7;
			constraintsPeriodicHealthGroupComboBox.insets = new java.awt.Insets(1, 1, 2, 18);
			getJPanelScanConfig().add(getPeriodicHealthGroupComboBox(), constraintsPeriodicHealthGroupComboBox);

			java.awt.GridBagConstraints constraintsAccumulatorGroupComboBox = new java.awt.GridBagConstraints();
			constraintsAccumulatorGroupComboBox.gridx = 4; constraintsAccumulatorGroupComboBox.gridy = 8;
			constraintsAccumulatorGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAccumulatorGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAccumulatorGroupComboBox.weightx = 1.0;
			constraintsAccumulatorGroupComboBox.ipadx = 22;
			constraintsAccumulatorGroupComboBox.ipady = -7;
			constraintsAccumulatorGroupComboBox.insets = new java.awt.Insets(1, 1, 4, 18);
			getJPanelScanConfig().add(getAccumulatorGroupComboBox(), constraintsAccumulatorGroupComboBox);

			java.awt.GridBagConstraints constraintsJLabelGroupAcc = new java.awt.GridBagConstraints();
			constraintsJLabelGroupAcc.gridx = 3; constraintsJLabelGroupAcc.gridy = 8;
			constraintsJLabelGroupAcc.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGroupAcc.ipadx = 7;
			constraintsJLabelGroupAcc.insets = new java.awt.Insets(2, 7, 4, 0);
			getJPanelScanConfig().add(getJLabelGroupAcc(), constraintsJLabelGroupAcc);

			java.awt.GridBagConstraints constraintsJLabelAltIntervalAcc = new java.awt.GridBagConstraints();
			constraintsJLabelAltIntervalAcc.gridx = 1; constraintsJLabelAltIntervalAcc.gridy = 9;
			constraintsJLabelAltIntervalAcc.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAltIntervalAcc.ipadx = 4;
			constraintsJLabelAltIntervalAcc.insets = new java.awt.Insets(2, 22, 5, 0);
			getJPanelScanConfig().add(getJLabelAltIntervalAcc(), constraintsJLabelAltIntervalAcc);

			java.awt.GridBagConstraints constraintsJLabelIntervalAcc = new java.awt.GridBagConstraints();
			constraintsJLabelIntervalAcc.gridx = 1; constraintsJLabelIntervalAcc.gridy = 8;
			constraintsJLabelIntervalAcc.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelIntervalAcc.ipadx = 4;
			constraintsJLabelIntervalAcc.insets = new java.awt.Insets(5, 22, 1, 20);
			getJPanelScanConfig().add(getJLabelIntervalAcc(), constraintsJLabelIntervalAcc);

			java.awt.GridBagConstraints constraintsAccumulatorRateCheckBox = new java.awt.GridBagConstraints();
			constraintsAccumulatorRateCheckBox.gridx = 1; constraintsAccumulatorRateCheckBox.gridy = 7;
			constraintsAccumulatorRateCheckBox.gridwidth = 2;
			constraintsAccumulatorRateCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAccumulatorRateCheckBox.ipadx = 67;
			constraintsAccumulatorRateCheckBox.insets = new java.awt.Insets(3, 5, 0, 7);
			getJPanelScanConfig().add(getAccumulatorRateCheckBox(), constraintsAccumulatorRateCheckBox);

			java.awt.GridBagConstraints constraintsAccumulatorRateComboBox = new java.awt.GridBagConstraints();
			constraintsAccumulatorRateComboBox.gridx = 2; constraintsAccumulatorRateComboBox.gridy = 8;
			constraintsAccumulatorRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAccumulatorRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAccumulatorRateComboBox.weightx = 1.0;
			constraintsAccumulatorRateComboBox.ipady = -7;
			constraintsAccumulatorRateComboBox.insets = new java.awt.Insets(1, 0, 4, 6);
			getJPanelScanConfig().add(getAccumulatorRateComboBox(), constraintsAccumulatorRateComboBox);

			java.awt.GridBagConstraints constraintsJComboBoxAltAccRate = new java.awt.GridBagConstraints();
			constraintsJComboBoxAltAccRate.gridx = 2; constraintsJComboBoxAltAccRate.gridy = 9;
			constraintsJComboBoxAltAccRate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxAltAccRate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxAltAccRate.weightx = 1.0;
			constraintsJComboBoxAltAccRate.ipady = -7;
			constraintsJComboBoxAltAccRate.insets = new java.awt.Insets(1, 0, 5, 6);
			getJPanelScanConfig().add(getJComboBoxAltAccRate(), constraintsJComboBoxAltAccRate);

			java.awt.GridBagConstraints constraintsIntegrityGroupComboBox = new java.awt.GridBagConstraints();
			constraintsIntegrityGroupComboBox.gridx = 4; constraintsIntegrityGroupComboBox.gridy = 2;
			constraintsIntegrityGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsIntegrityGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIntegrityGroupComboBox.weightx = 1.0;
			constraintsIntegrityGroupComboBox.ipadx = 22;
			constraintsIntegrityGroupComboBox.ipady = -7;
			constraintsIntegrityGroupComboBox.insets = new java.awt.Insets(1, 1, 4, 18);
			getJPanelScanConfig().add(getIntegrityGroupComboBox(), constraintsIntegrityGroupComboBox);

			java.awt.GridBagConstraints constraintsJLabelGroupInt = new java.awt.GridBagConstraints();
			constraintsJLabelGroupInt.gridx = 3; constraintsJLabelGroupInt.gridy = 2;
			constraintsJLabelGroupInt.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGroupInt.ipadx = 7;
			constraintsJLabelGroupInt.insets = new java.awt.Insets(2, 7, 4, 0);
			getJPanelScanConfig().add(getJLabelGroupInt(), constraintsJLabelGroupInt);

			java.awt.GridBagConstraints constraintsIntegrityRateComboBox = new java.awt.GridBagConstraints();
			constraintsIntegrityRateComboBox.gridx = 2; constraintsIntegrityRateComboBox.gridy = 2;
			constraintsIntegrityRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsIntegrityRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIntegrityRateComboBox.weightx = 1.0;
			constraintsIntegrityRateComboBox.ipady = -7;
			constraintsIntegrityRateComboBox.insets = new java.awt.Insets(1, 0, 4, 6);
			getJPanelScanConfig().add(getIntegrityRateComboBox(), constraintsIntegrityRateComboBox);

			java.awt.GridBagConstraints constraintsJLabelIntervalInt = new java.awt.GridBagConstraints();
			constraintsJLabelIntervalInt.gridx = 1; constraintsJLabelIntervalInt.gridy = 2;
			constraintsJLabelIntervalInt.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelIntervalInt.ipadx = 4;
			constraintsJLabelIntervalInt.insets = new java.awt.Insets(2, 22, 4, 20);
			getJPanelScanConfig().add(getJLabelIntervalInt(), constraintsJLabelIntervalInt);

			java.awt.GridBagConstraints constraintsJLabelAltIntervalInt = new java.awt.GridBagConstraints();
			constraintsJLabelAltIntervalInt.gridx = 1; constraintsJLabelAltIntervalInt.gridy = 3;
			constraintsJLabelAltIntervalInt.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAltIntervalInt.ipadx = 4;
			constraintsJLabelAltIntervalInt.insets = new java.awt.Insets(5, 22, 1, 0);
			getJPanelScanConfig().add(getJLabelAltIntervalInt(), constraintsJLabelAltIntervalInt);

			java.awt.GridBagConstraints constraintsJComboBoxAltIntegrityRate = new java.awt.GridBagConstraints();
			constraintsJComboBoxAltIntegrityRate.gridx = 2; constraintsJComboBoxAltIntegrityRate.gridy = 3;
			constraintsJComboBoxAltIntegrityRate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxAltIntegrityRate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxAltIntegrityRate.weightx = 1.0;
			constraintsJComboBoxAltIntegrityRate.ipady = -7;
			constraintsJComboBoxAltIntegrityRate.insets = new java.awt.Insets(4, 0, 1, 6);
			getJPanelScanConfig().add(getJComboBoxAltIntegrityRate(), constraintsJComboBoxAltIntegrityRate);

			java.awt.GridBagConstraints constraintsIntegrityRateCheckBox = new java.awt.GridBagConstraints();
			constraintsIntegrityRateCheckBox.gridx = 1; constraintsIntegrityRateCheckBox.gridy = 1;
			constraintsIntegrityRateCheckBox.gridwidth = 2;
			constraintsIntegrityRateCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIntegrityRateCheckBox.ipadx = 94;
			constraintsIntegrityRateCheckBox.insets = new java.awt.Insets(4, 5, 0, 7);
			getJPanelScanConfig().add(getIntegrityRateCheckBox(), constraintsIntegrityRateCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelScanConfig;
}
/**
 * Return the JPanelScanWindow property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelScanWindow() {
	if (ivjJPanelScanWindow == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Optional Scan Window (Time)");
			ivjJPanelScanWindow = new javax.swing.JPanel();
			ivjJPanelScanWindow.setName("JPanelScanWindow");
			ivjJPanelScanWindow.setBorder(ivjLocalBorder);
			ivjJPanelScanWindow.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxScanWindow = new java.awt.GridBagConstraints();
			constraintsJCheckBoxScanWindow.gridx = 1; constraintsJCheckBoxScanWindow.gridy = 1;
			constraintsJCheckBoxScanWindow.gridwidth = 2;
			constraintsJCheckBoxScanWindow.ipadx = 36;
			constraintsJCheckBoxScanWindow.insets = new java.awt.Insets(0, 22, 2, 13);
			getJPanelScanWindow().add(getJCheckBoxScanWindow(), constraintsJCheckBoxScanWindow);

			java.awt.GridBagConstraints constraintsJLabelType = new java.awt.GridBagConstraints();
			constraintsJLabelType.gridx = 4; constraintsJLabelType.gridy = 1;
			constraintsJLabelType.ipadx = 18;
			constraintsJLabelType.insets = new java.awt.Insets(0, 5, 5, 16);
			getJPanelScanWindow().add(getJLabelType(), constraintsJLabelType);

			java.awt.GridBagConstraints constraintsJLabelOpen = new java.awt.GridBagConstraints();
			constraintsJLabelOpen.gridx = 1; constraintsJLabelOpen.gridy = 2;
			constraintsJLabelOpen.ipadx = 3;
			constraintsJLabelOpen.insets = new java.awt.Insets(3, 22, 9, 13);
			getJPanelScanWindow().add(getJLabelOpen(), constraintsJLabelOpen);

			java.awt.GridBagConstraints constraintsJTextFieldOpen = new java.awt.GridBagConstraints();
			constraintsJTextFieldOpen.gridx = 2; constraintsJTextFieldOpen.gridy = 2;
			constraintsJTextFieldOpen.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldOpen.weightx = 1.0;
			constraintsJTextFieldOpen.ipadx = 46;
			constraintsJTextFieldOpen.insets = new java.awt.Insets(2, 4, 9, 0);
			getJPanelScanWindow().add(getJTextFieldOpen(), constraintsJTextFieldOpen);

			java.awt.GridBagConstraints constraintsJLabelClose = new java.awt.GridBagConstraints();
			constraintsJLabelClose.gridx = 4; constraintsJLabelClose.gridy = 2;
			constraintsJLabelClose.ipadx = 22;
			constraintsJLabelClose.insets = new java.awt.Insets(2, 5, 10, 8);
			getJPanelScanWindow().add(getJLabelClose(), constraintsJLabelClose);

			java.awt.GridBagConstraints constraintsJTextFieldClose = new java.awt.GridBagConstraints();
			constraintsJTextFieldClose.gridx = 5; constraintsJTextFieldClose.gridy = 2;
			constraintsJTextFieldClose.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldClose.weightx = 1.0;
			constraintsJTextFieldClose.ipadx = 46;
			constraintsJTextFieldClose.insets = new java.awt.Insets(2, -15, 9, 1);
			getJPanelScanWindow().add(getJTextFieldClose(), constraintsJTextFieldClose);

			java.awt.GridBagConstraints constraintsJComboBoxScanWindowType = new java.awt.GridBagConstraints();
			constraintsJComboBoxScanWindowType.gridx = 5; constraintsJComboBoxScanWindowType.gridy = 1;
			constraintsJComboBoxScanWindowType.gridwidth = 2;
			constraintsJComboBoxScanWindowType.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxScanWindowType.weightx = 1.0;
			constraintsJComboBoxScanWindowType.ipadx = 13;
			constraintsJComboBoxScanWindowType.ipady = -7;
			constraintsJComboBoxScanWindowType.insets = new java.awt.Insets(0, -15, 4, 4);
			getJPanelScanWindow().add(getJComboBoxScanWindowType(), constraintsJComboBoxScanWindowType);

			java.awt.GridBagConstraints constraintsJLabelhhmm = new java.awt.GridBagConstraints();
			constraintsJLabelhhmm.gridx = 3; constraintsJLabelhhmm.gridy = 2;
			constraintsJLabelhhmm.ipadx = 4;
			constraintsJLabelhhmm.insets = new java.awt.Insets(5, 0, 12, 5);
			getJPanelScanWindow().add(getJLabelhhmm(), constraintsJLabelhhmm);

			java.awt.GridBagConstraints constraintsJLabelhhmm1 = new java.awt.GridBagConstraints();
			constraintsJLabelhhmm1.gridx = 6; constraintsJLabelhhmm1.gridy = 2;
			constraintsJLabelhhmm1.ipadx = 4;
			constraintsJLabelhhmm1.insets = new java.awt.Insets(5, 2, 12, 15);
			getJPanelScanWindow().add(getJLabelhhmm1(), constraintsJLabelhhmm1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelScanWindow;
}
/**
 * Return the JTextFieldClose property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldClose() {
	if (ivjJTextFieldClose == null) {
		try {
			ivjJTextFieldClose = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldClose.setName("JTextFieldClose");
			ivjJTextFieldClose.setToolTipText("when to close window from midnight");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldClose;
}
/**
 * Return the JTextFieldOpen property value.
 * @return com.cannontech.common.gui.util.JTextFieldTimeEntry
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.JTextFieldTimeEntry getJTextFieldOpen() {
	if (ivjJTextFieldOpen == null) {
		try {
			ivjJTextFieldOpen = new com.cannontech.common.gui.util.JTextFieldTimeEntry();
			ivjJTextFieldOpen.setName("JTextFieldOpen");
			ivjJTextFieldOpen.setToolTipText("when to open window from midnight");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldOpen;
}
/**
 * Return the GeneralRateCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPeriodicHealthCheckBox() {
	if (ivjPeriodicHealthCheckBox == null) {
		try {
			ivjPeriodicHealthCheckBox = new javax.swing.JCheckBox();
			ivjPeriodicHealthCheckBox.setName("PeriodicHealthCheckBox");
			ivjPeriodicHealthCheckBox.setText("Periodic Health Check");
			ivjPeriodicHealthCheckBox.setMaximumSize(new java.awt.Dimension(150, 21));
			ivjPeriodicHealthCheckBox.setActionCommand("GeneralRateCheckBox");
			ivjPeriodicHealthCheckBox.setPreferredSize(new java.awt.Dimension(150, 21));
			ivjPeriodicHealthCheckBox.setFont(new java.awt.Font("Arial", 1, 12));
			ivjPeriodicHealthCheckBox.setMinimumSize(new java.awt.Dimension(150, 21));
			ivjPeriodicHealthCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicHealthCheckBox;
}
/**
 * Return the GeneralGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPeriodicHealthGroupComboBox() {
	if (ivjPeriodicHealthGroupComboBox == null) {
		try {
			ivjPeriodicHealthGroupComboBox = new javax.swing.JComboBox();
			ivjPeriodicHealthGroupComboBox.setName("PeriodicHealthGroupComboBox");
			ivjPeriodicHealthGroupComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjPeriodicHealthGroupComboBox.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			ivjPeriodicHealthGroupComboBox.addItem("Default");
			ivjPeriodicHealthGroupComboBox.addItem("FirstGroup");
			ivjPeriodicHealthGroupComboBox.addItem("SecondGroup");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicHealthGroupComboBox;
}
/**
 * Return the GeneralComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPeriodicHealthIntervalComboBox() {
	if (ivjPeriodicHealthIntervalComboBox == null) {
		try {
			ivjPeriodicHealthIntervalComboBox = new javax.swing.JComboBox();
			ivjPeriodicHealthIntervalComboBox.setName("PeriodicHealthIntervalComboBox");
			ivjPeriodicHealthIntervalComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjPeriodicHealthIntervalComboBox.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}

			ivjPeriodicHealthIntervalComboBox.addItem("1 second");
			ivjPeriodicHealthIntervalComboBox.addItem("2 second");
			ivjPeriodicHealthIntervalComboBox.addItem("5 second");
			ivjPeriodicHealthIntervalComboBox.addItem("10 second");
			ivjPeriodicHealthIntervalComboBox.addItem("15 second");
			ivjPeriodicHealthIntervalComboBox.addItem("30 second");
			ivjPeriodicHealthIntervalComboBox.addItem("1 minute");
			ivjPeriodicHealthIntervalComboBox.addItem("2 minute");
			ivjPeriodicHealthIntervalComboBox.addItem("3 minute");
			ivjPeriodicHealthIntervalComboBox.addItem("5 minute");
			ivjPeriodicHealthIntervalComboBox.addItem("10 minute");
			ivjPeriodicHealthIntervalComboBox.addItem("15 minute");
			ivjPeriodicHealthIntervalComboBox.addItem("30 minute");
			ivjPeriodicHealthIntervalComboBox.addItem("1 hour");
			ivjPeriodicHealthIntervalComboBox.addItem("2 hour");
			ivjPeriodicHealthIntervalComboBox.addItem("6 hour");
			ivjPeriodicHealthIntervalComboBox.addItem("12 hour");
			ivjPeriodicHealthIntervalComboBox.addItem("1 day");

			//default value
			ivjPeriodicHealthIntervalComboBox.setSelectedItem("30 minute");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeriodicHealthIntervalComboBox;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object device) 
{
   TwoWayDevice val = null;
   
   if( device instanceof com.cannontech.database.data.multi.MultiDBPersistent )
   {
      val = (TwoWayDevice)com.cannontech.database.data.multi.CommonMulti.getFirstObjectOfType(
                  TwoWayDevice.class,
                  (com.cannontech.database.data.multi.MultiDBPersistent)device );
   }         
   else
   	val = (TwoWayDevice)device;

	Integer deviceID = val.getDevice().getDeviceID();
	java.util.Vector oldScanRateVector = val.getDeviceScanRateVector();
	java.util.Vector newScanRateVector = new java.util.Vector(3);


	if( (val instanceof CCUBase) || (val instanceof TCUBase) 
			  || (val instanceof RepeaterBase) ||
			 (val instanceof PagingTapTerminal) )
	{
		if (getPeriodicHealthCheckBox().isSelected() && getPeriodicHealthCheckBox().isVisible() )
		{
			Integer generalRate = CtiUtilities.getIntervalComboBoxSecondsValue(getPeriodicHealthIntervalComboBox());
			Integer altRate = CtiUtilities.getIntervalComboBoxSecondsValue( 
					getJComboBoxAltHealthChk().getSelectedItem().equals(CtiUtilities.STRING_NONE)
					? getPeriodicHealthIntervalComboBox() 
					: getJComboBoxAltHealthChk() );

			Integer generalGroup = new Integer(getPeriodicHealthGroupComboBox().getSelectedIndex());			
			newScanRateVector.addElement(new DeviceScanRate(deviceID, DeviceScanRate.TYPE_STATUS, generalRate, generalGroup, altRate));
		}
	}
	else if ( val instanceof IEDMeter )
	{
		if (getPeriodicHealthCheckBox().isSelected() && getPeriodicHealthCheckBox().isVisible())
		{
			Integer generalRate = CtiUtilities.getIntervalComboBoxSecondsValue(getPeriodicHealthIntervalComboBox());
			Integer altRate = CtiUtilities.getIntervalComboBoxSecondsValue(
					getJComboBoxAltHealthChk().getSelectedItem().equals(CtiUtilities.STRING_NONE)
					? getPeriodicHealthIntervalComboBox()
					: getJComboBoxAltHealthChk() );

			Integer generalGroup = new Integer(getPeriodicHealthGroupComboBox().getSelectedIndex());
			newScanRateVector.addElement(new DeviceScanRate(deviceID, DeviceScanRate.TYPE_GENERAL, generalRate, generalGroup, altRate));
		}
	}
	else if( (val instanceof RTUBase) 
				|| (val instanceof MCTBase) 
				|| (val instanceof LCUBase)
            || (val instanceof CapBankController6510)
            || (val instanceof DNPBase)
            || (val instanceof Series5Base) )
	{
		if( getPeriodicHealthCheckBox().isSelected() && getPeriodicHealthCheckBox().isVisible() )
		{
			if( val instanceof MCTBase )
			{
				Integer generalRate = CtiUtilities.getIntervalComboBoxSecondsValue(getPeriodicHealthIntervalComboBox());
				Integer altRate = CtiUtilities.getIntervalComboBoxSecondsValue(
					getJComboBoxAltHealthChk().getSelectedItem().equals(CtiUtilities.STRING_NONE)
					? getPeriodicHealthIntervalComboBox()
					: getJComboBoxAltHealthChk() );

				Integer generalGroup = new Integer(getPeriodicHealthGroupComboBox().getSelectedIndex());
				newScanRateVector.addElement(new DeviceScanRate(deviceID, DeviceScanRate.TYPE_STATUS, generalRate, generalGroup, altRate));
			}
			else
			{
				Integer generalRate = CtiUtilities.getIntervalComboBoxSecondsValue(getPeriodicHealthIntervalComboBox());
				Integer altRate = CtiUtilities.getIntervalComboBoxSecondsValue(
					getJComboBoxAltHealthChk().getSelectedItem().equals(CtiUtilities.STRING_NONE)
					? getPeriodicHealthIntervalComboBox()
					: getJComboBoxAltHealthChk() );

				Integer generalGroup = new Integer(getPeriodicHealthGroupComboBox().getSelectedIndex());
				newScanRateVector.addElement(new DeviceScanRate(deviceID, DeviceScanRate.TYPE_EXCEPTION, generalRate, generalGroup, altRate));
			}
		}

		if( getAccumulatorRateCheckBox().isSelected() && getAccumulatorRateCheckBox().isVisible() )
		{
			Integer accumulatorRate = CtiUtilities.getIntervalComboBoxSecondsValue(getAccumulatorRateComboBox());
			Integer altRate = CtiUtilities.getIntervalComboBoxSecondsValue(
					getJComboBoxAltAccRate().getSelectedItem().equals(CtiUtilities.STRING_NONE)
					? getAccumulatorRateComboBox()
					: getJComboBoxAltAccRate() );

			Integer accumulatorGroup = new Integer(getAccumulatorGroupComboBox().getSelectedIndex());
			newScanRateVector.addElement(new DeviceScanRate(deviceID, DeviceScanRate.TYPE_ACCUMULATOR, accumulatorRate, accumulatorGroup, altRate));
		}
		
		if( getIntegrityRateCheckBox().isSelected() &&  getIntegrityRateCheckBox().isVisible() )
		{
			Integer integrityRate = CtiUtilities.getIntervalComboBoxSecondsValue(getIntegrityRateComboBox());
			Integer altRate = CtiUtilities.getIntervalComboBoxSecondsValue(
					getJComboBoxAltIntegrityRate().getSelectedItem().equals(CtiUtilities.STRING_NONE)
					? getIntegrityRateComboBox()
					: getJComboBoxAltIntegrityRate() );

			Integer integrityGroup = new Integer(getIntegrityGroupComboBox().getSelectedIndex());
			newScanRateVector.addElement(new DeviceScanRate(deviceID, DeviceScanRate.TYPE_INTEGRITY, integrityRate, integrityGroup, altRate));
		}
	}

	for(int i=0;i<newScanRateVector.size();i++)
	{
		//be sure no scan rates are set to zero (if any rate is zero, we may have a problem)
		if( ((DeviceScanRate)newScanRateVector.get(i)).getIntervalRate().intValue() == 0 
			 || ((DeviceScanRate)newScanRateVector.get(i)).getAlternateRate().intValue() == 0 )
		{
			for(int j=0;j<oldScanRateVector.size();j++)
			{
				if( ((DeviceScanRate)newScanRateVector.get(i)).getScanType().equalsIgnoreCase(((DeviceScanRate)oldScanRateVector.get(j)).getScanType()) )
				{
					//reset to our original scan rate values
					((DeviceScanRate)newScanRateVector.get(i)).setIntervalRate( new Integer(((DeviceScanRate)oldScanRateVector.get(j)).getIntervalRate().intValue()) );
					((DeviceScanRate)newScanRateVector.get(i)).setAlternateRate( new Integer(((DeviceScanRate)oldScanRateVector.get(j)).getAlternateRate().intValue()) );
					break;
				}
			}
		}

	}

	val.setDeviceScanRateVector(newScanRateVector);

	//set the scan window values here! Also we must do this in the Wizard
	val.getDeviceWindow().setType( getJComboBoxScanWindowType().getSelectedItem().toString() );
	
	if( getJCheckBoxScanWindow().isSelected() )
	{
		val.getDeviceWindow().setWinOpen( getJTextFieldOpen().getTimeTotalSeconds() );
		val.getDeviceWindow().setAlternateOpen( getJTextFieldOpen().getTimeTotalSeconds() );

		val.getDeviceWindow().setWinClose( getJTextFieldClose().getTimeTotalSeconds() );
		val.getDeviceWindow().setAlternateClose( getJTextFieldClose().getTimeTotalSeconds() );
	}
	else
	{
		val.getDeviceWindow().setWinOpen( new Integer(0) );
		val.getDeviceWindow().setAlternateOpen( new Integer(0) );

		val.getDeviceWindow().setWinClose( new Integer(0) );
		val.getDeviceWindow().setAlternateClose( new Integer(0) );
	}
	
	return device;
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
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPeriodicHealthCheckBox().addActionListener(this);
	getAccumulatorRateCheckBox().addActionListener(this);
	getIntegrityRateCheckBox().addActionListener(this);
	getAccumulatorRateComboBox().addActionListener(this);
	getPeriodicHealthIntervalComboBox().addActionListener(this);
	getIntegrityRateComboBox().addActionListener(this);
	getIntegrityGroupComboBox().addActionListener(this);
	getAccumulatorGroupComboBox().addActionListener(this);
	getPeriodicHealthGroupComboBox().addActionListener(this);
	getJComboBoxAltHealthChk().addActionListener(this);
	getJComboBoxAltAccRate().addActionListener(this);
	getJComboBoxAltIntegrityRate().addActionListener(this);
	getJCheckBoxScanWindow().addActionListener(this);
	getJTextFieldOpen().addCaretListener(this);
	getJTextFieldClose().addCaretListener(this);
	getJComboBoxScanWindowType().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}


		// user code end
		setName("DeviceScanRateEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(428, 309);

		java.awt.GridBagConstraints constraintsJPanelScanConfig = new java.awt.GridBagConstraints();
		constraintsJPanelScanConfig.gridx = 1; constraintsJPanelScanConfig.gridy = 1;
		constraintsJPanelScanConfig.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelScanConfig.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelScanConfig.weightx = 1.0;
		constraintsJPanelScanConfig.weighty = 1.0;
		constraintsJPanelScanConfig.insets = new java.awt.Insets(13, 11, 4, 16);
		add(getJPanelScanConfig(), constraintsJPanelScanConfig);

		java.awt.GridBagConstraints constraintsJPanelScanWindow = new java.awt.GridBagConstraints();
		constraintsJPanelScanWindow.gridx = 1; constraintsJPanelScanWindow.gridy = 2;
		constraintsJPanelScanWindow.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelScanWindow.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelScanWindow.weightx = 1.0;
		constraintsJPanelScanWindow.weighty = 1.0;
		constraintsJPanelScanWindow.ipadx = 23;
		constraintsJPanelScanWindow.ipady = -10;
		constraintsJPanelScanWindow.insets = new java.awt.Insets(5, 11, 6, 16);
		add(getJPanelScanWindow(), constraintsJPanelScanWindow);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	jCheckBoxScanWindow_ActionPerformed(null);

	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJCheckBoxScanWindow().isSelected() 
		 && (getJTextFieldClose().getTimeTotalSeconds().intValue()
		     == getJTextFieldOpen().getTimeTotalSeconds().intValue()) )
	{
		setErrorString("The Device Window open and close values can not be equal");
		return false;
	}

	return true;
}
/**
 * Comment
 */
public void jCheckBoxScanWindow_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	fireInputUpdate();
	getJLabelOpen().setEnabled( getJCheckBoxScanWindow().isSelected() );
	getJLabelClose().setEnabled( getJCheckBoxScanWindow().isSelected() );
	getJLabelType().setEnabled( getJCheckBoxScanWindow().isSelected() );

	getJTextFieldClose().setEnabled( getJCheckBoxScanWindow().isSelected() );
	getJTextFieldOpen().setEnabled( getJCheckBoxScanWindow().isSelected() );
	getJComboBoxScanWindowType().setEnabled( getJCheckBoxScanWindow().isSelected() );
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new javax.swing.JFrame();
		DeviceScanRateEditorPanel aDeviceScanRateEditorPanel;
		aDeviceScanRateEditorPanel = new DeviceScanRateEditorPanel();
		frame.setContentPane(aDeviceScanRateEditorPanel);
		frame.setSize(aDeviceScanRateEditorPanel.getSize());
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
 * Creation date: (9/10/2001 2:03:32 PM)
 * @param value boolean
 */
private void setAccumulatorObjectsVisible(boolean value) 
{
	getAccumulatorRateCheckBox().setVisible(value);
	getAccumulatorRateComboBox().setVisible(value);
	getAccumulatorGroupComboBox().setVisible(value);
	getJComboBoxAltAccRate().setVisible(value);

	getJLabelAltIntervalAcc().setVisible(value);
	getJLabelIntervalAcc().setVisible(value);
	getJLabelGroupAcc().setVisible(value);
}

private void initComboBoxValues( int type )
{
   
   if( DeviceTypesFuncs.isCCU(type) )
   {
      getPeriodicHealthIntervalComboBox().removeAllItems();

      getPeriodicHealthIntervalComboBox().addItem("5 minute");
      getPeriodicHealthIntervalComboBox().addItem("10 minute");
      getPeriodicHealthIntervalComboBox().addItem("15 minute");
      getPeriodicHealthIntervalComboBox().addItem("30 minute");
      getPeriodicHealthIntervalComboBox().addItem("1 hour");
      getPeriodicHealthIntervalComboBox().addItem("2 hour");
      getPeriodicHealthIntervalComboBox().addItem("6 hour");
      getPeriodicHealthIntervalComboBox().addItem("12 hour");
      getPeriodicHealthIntervalComboBox().addItem("1 day");    
      
      //default value
      getPeriodicHealthIntervalComboBox().setSelectedItem("5 minute");
   }
   else if( DeviceTypesFuncs.isMCT(type) || DeviceTypesFuncs.isRepeater(type) )
   {
      getPeriodicHealthIntervalComboBox().removeAllItems();
   
      getPeriodicHealthIntervalComboBox().addItem("1 minute");
      getPeriodicHealthIntervalComboBox().addItem("3 minute");
      getPeriodicHealthIntervalComboBox().addItem("5 minute");
      getPeriodicHealthIntervalComboBox().addItem("10 minute");
      getPeriodicHealthIntervalComboBox().addItem("15 minute");
      getPeriodicHealthIntervalComboBox().addItem("30 minute");
      getPeriodicHealthIntervalComboBox().addItem("1 hour");
      getPeriodicHealthIntervalComboBox().addItem("2 hour");
      getPeriodicHealthIntervalComboBox().addItem("6 hour");
      getPeriodicHealthIntervalComboBox().addItem("12 hour");
      getPeriodicHealthIntervalComboBox().addItem("1 day");    
      
      //default value
      getPeriodicHealthIntervalComboBox().setSelectedItem("5 minute");
   }

   
}


/**
 * This method was created in VisualAge.
 * @param type java.lang.Object
 */
public void setDeviceType(int type)
{
   //set some devices domain of possible values to a more limited approach
   initComboBoxValues( type );

	setAccumulatorObjectsVisible( false );
	setIntegrityObjectsVisible( false );
	
	getPeriodicHealthCheckBox().setSelected(false);
	getPeriodicHealthIntervalComboBox().setEnabled(false);
	getPeriodicHealthGroupComboBox().setEnabled(false);
	getJComboBoxAltHealthChk().setEnabled(false);

	if( DeviceTypesFuncs.isCCU(type)
			|| DeviceTypesFuncs.isTCU(type)
			|| DeviceTypesFuncs.isRepeater(type)
			|| (type == PAOGroups.TAPTERMINAL)
			|| (type == PAOGroups.WCTP_TERMINAL) )
	{
		getPeriodicHealthCheckBox().setText("Periodic Health Check");

		if( DeviceTypesFuncs.isRepeater(type) )
			getPeriodicHealthIntervalComboBox().setSelectedItem("1 hour");
	}
	else if( type == PAOGroups.DAVISWEATHER 
				|| DeviceTypesFuncs.isMeter(type) )
	{
		getPeriodicHealthCheckBox().setText("General Data Collection");
		getPeriodicHealthIntervalComboBox().setSelectedItem("1 hour");
	}


	if( DeviceTypesFuncs.isRTU(type) 
				|| DeviceTypesFuncs.isMCT(type)
				|| DeviceTypesFuncs.isLCU(type)
            || DeviceTypesFuncs.isCapBankController(type)
            || (type == PAOGroups.SERIES_5_LMI) )
	{		
		if( DeviceTypesFuncs.isMCT3xx(type) || DeviceTypesFuncs.isMCT410KWHONLY(type)  )
		{
			getIntegrityRateCheckBox().setText("Demand & Status Rate");
			getAccumulatorRateCheckBox().setText("Accumulator (Energy) Rate");
		}
		else if (DeviceTypesFuncs.isMCT(type))
		{
			getPeriodicHealthCheckBox().setText("Status Rate");
			getIntegrityRateCheckBox().setText("Demand Rate");
			getAccumulatorRateCheckBox().setText("Accumulator (Energy) Rate");			
		}
		else if( type == PAOGroups.RTUILEX || type == PAOGroups.RTM ) 
		{
			getPeriodicHealthCheckBox().setText("General Scan");
			getPeriodicHealthIntervalComboBox().setSelectedItem("15 second");
			getAccumulatorRateComboBox().setSelectedItem("15 minute");
		}		
		
		else if( type == PAOGroups.RTU_DNP
					 || type == PAOGroups.RTU_DART
                || type == PAOGroups.DNP_CBC_6510 )
      {
			getPeriodicHealthCheckBox().setText("Class 1,2,3 Scan");
			getPeriodicHealthIntervalComboBox().setSelectedItem("15 second");      	

			getIntegrityRateCheckBox().setText("Class 0,1,2,3 Scan");
			getIntegrityRateComboBox().setSelectedItem("5 minute");
      }
	  else if( type == PAOGroups.RTUWELCO )
	  {
			getIntegrityRateCheckBox().setText("Integrity Rate");
			getIntegrityRateComboBox().setSelectedItem("3 minute");
			getPeriodicHealthCheckBox().setText("General Scan");
			getPeriodicHealthIntervalComboBox().setSelectedItem("15 second");
			getAccumulatorRateComboBox().setSelectedItem("15 minute");	
	  }
      else if( type == PAOGroups.LCU415 )
      {
         getPeriodicHealthCheckBox().setText("Status & Analog");
         getAccumulatorRateCheckBox().setText("Accumulator Rate");         
      }
		else if( type == PAOGroups.ION_7700
			       || type == PAOGroups.ION_7330
			       || type == PAOGroups.ION_8300 )
		{
			getAccumulatorRateCheckBox().setText("Eventlog Collection");			
			getAccumulatorRateComboBox().setSelectedItem("1 hour");
		}		
		else
		{
			//LCU's and RTU's			
			getPeriodicHealthCheckBox().setText("Report by Exception");
			getPeriodicHealthIntervalComboBox().setSelectedItem("15 second");
			getAccumulatorRateComboBox().setSelectedItem("15 minute");
			getIntegrityRateComboBox().setSelectedItem("3 minute");
		}

      setIntegrityObjectsVisible(
         !(type == PAOGroups.RTUILEX
          || type == PAOGroups.LCU415 || type == PAOGroups.RTM));
		
      setHealthObjectsVisible( 
            !(type == PAOGroups.DCT_501
              || DeviceTypesFuncs.isMCT3xx(type) || DeviceTypesFuncs.isMCT410KWHONLY(type)
				|| type == PAOGroups.SERIES_5_LMI) );
      
		setAccumulatorObjectsVisible( 
				!(type == PAOGroups.DCT_501 
               || type == PAOGroups.DNP_CBC_6510
	            || type == PAOGroups.RTU_DNP
	            || type == PAOGroups.RTU_DART
					|| type == PAOGroups.LCU_T3026
					|| type == PAOGroups.SERIES_5_LMI
					|| type == PAOGroups.RTM) );
		

		getAccumulatorRateCheckBox().setSelected(false);
		getAccumulatorRateComboBox().setEnabled(false);
		getAccumulatorGroupComboBox().setEnabled(false);
		getJComboBoxAltAccRate().setEnabled(false);
		
		getIntegrityRateCheckBox().setSelected(false);
		getIntegrityRateComboBox().setEnabled(false);
		getIntegrityGroupComboBox().setEnabled(false);
		getJComboBoxAltIntegrityRate().setEnabled(false);
	}


	//update all the alt JComboBoxes
	getJComboBoxAltHealthChk().setSelectedItem( CtiUtilities.STRING_NONE );
	getJComboBoxAltIntegrityRate().setSelectedItem( CtiUtilities.STRING_NONE );
	getJComboBoxAltAccRate().setSelectedItem( CtiUtilities.STRING_NONE );
	
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 2:03:32 PM)
 * @param value boolean
 */
private void setHealthObjectsVisible(boolean value) 
{
	getPeriodicHealthCheckBox().setVisible(value);
	getPeriodicHealthIntervalComboBox().setVisible(value);
	getPeriodicHealthGroupComboBox().setVisible(value);
	getJComboBoxAltHealthChk().setVisible(value);

	getJLabelAltIntervalHlth().setVisible(value);
	getJLabelGroupHlth().setVisible(value);
	getJLabelIntervalHlth().setVisible(value);
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 2:03:32 PM)
 * @param value boolean
 */
private void setIntegrityObjectsVisible(boolean value) 
{
	getIntegrityRateCheckBox().setVisible(value);
	getIntegrityRateComboBox().setVisible(value);
	getIntegrityGroupComboBox().setVisible(value);
	getJComboBoxAltIntegrityRate().setVisible(value);

	getJLabelAltIntervalInt().setVisible(value);
	getJLabelGroupInt().setVisible(value);
	getJLabelIntervalInt().setVisible(value);
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{
	final int deviceType = com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((DeviceBase)val).getPAOType() );

	setDeviceType( deviceType );
	
	//getAccumulatorRateCheckBox().setText("Accumulator Rate");		

	DeviceScanRate dScanRate[] = new DeviceScanRate[((TwoWayDevice) val).getDeviceScanRateVector().size()];
	((TwoWayDevice) val).getDeviceScanRateVector().copyInto(dScanRate);

	if ((val instanceof CCUBase)
		|| (val instanceof TCUBase)
		|| (val instanceof RepeaterBase)
		|| (val instanceof PagingTapTerminal)
		|| (val instanceof IEDMeter))
	{
		if (dScanRate.length > 0)
		{
			if( dScanRate[0].getScanType().equals(DeviceScanRate.TYPE_STATUS)
				 || dScanRate[0].getScanType().equals(DeviceScanRate.TYPE_GENERAL) )
			{
				getPeriodicHealthCheckBox().setSelected(true);
				getPeriodicHealthIntervalComboBox().setEnabled(true);
				getPeriodicHealthGroupComboBox().setEnabled(true);
				getJComboBoxAltHealthChk().setEnabled(true);
				
				getPeriodicHealthGroupComboBox().setSelectedIndex(dScanRate[0].getScanGroup().intValue());
				
				CtiUtilities.setIntervalComboBoxSelectedItem(
					getPeriodicHealthIntervalComboBox(),
					dScanRate[0].getIntervalRate().intValue());

				if( dScanRate[0].getIntervalRate().intValue() == dScanRate[0].getAlternateRate().intValue() )
					getJComboBoxAltHealthChk().setSelectedItem( CtiUtilities.STRING_NONE );
				else
					CtiUtilities.setIntervalComboBoxSelectedItem(
						getJComboBoxAltHealthChk(),
						dScanRate[0].getAlternateRate().intValue());
			}
		}
	}
	else if( (val instanceof RTUBase) 
				|| (val instanceof MCTBase) 
				|| (val instanceof LCUBase)
            || (val instanceof CapBankController6510)
            || (val instanceof DNPBase)
            || (val instanceof Series5Base) )
	{

		for (int i = 0; i < dScanRate.length; i++)
		{
			if (dScanRate[i].getScanType().equals(DeviceScanRate.TYPE_EXCEPTION)
				|| dScanRate[i].getScanType().equals(DeviceScanRate.TYPE_STATUS))
			{
				getPeriodicHealthCheckBox().setSelected(true);
				getPeriodicHealthIntervalComboBox().setEnabled(true);
				getPeriodicHealthGroupComboBox().setEnabled(true);
				getJComboBoxAltHealthChk().setEnabled(true);
				
				getPeriodicHealthGroupComboBox().setSelectedIndex(dScanRate[i].getScanGroup().intValue());
				CtiUtilities.setIntervalComboBoxSelectedItem(
					getPeriodicHealthIntervalComboBox(),
					dScanRate[i].getIntervalRate().intValue());
				
				if( dScanRate[i].getIntervalRate().intValue() == dScanRate[i].getAlternateRate().intValue() )
					getJComboBoxAltHealthChk().setSelectedItem( CtiUtilities.STRING_NONE );
				else
					CtiUtilities.setIntervalComboBoxSelectedItem(
						getJComboBoxAltHealthChk(), dScanRate[i].getAlternateRate().intValue());
			}
			else if (dScanRate[i].getScanType().equals(DeviceScanRate.TYPE_ACCUMULATOR))
			{
				getAccumulatorRateCheckBox().setSelected(true);
				getAccumulatorRateComboBox().setEnabled(true);
				getAccumulatorGroupComboBox().setEnabled(true);
				getJComboBoxAltAccRate().setEnabled(true);
				
				getAccumulatorGroupComboBox().setSelectedIndex(dScanRate[i].getScanGroup().intValue());
				CtiUtilities.setIntervalComboBoxSelectedItem(
					getAccumulatorRateComboBox(), dScanRate[i].getIntervalRate().intValue());
				
				if( dScanRate[i].getIntervalRate().intValue() == dScanRate[i].getAlternateRate().intValue() )
					getJComboBoxAltAccRate().setSelectedItem( CtiUtilities.STRING_NONE );
				else
					CtiUtilities.setIntervalComboBoxSelectedItem(
						getJComboBoxAltAccRate(), dScanRate[i].getAlternateRate().intValue());				
			}
			else if (dScanRate[i].getScanType().equals(DeviceScanRate.TYPE_INTEGRITY))
			{
				getIntegrityRateCheckBox().setSelected(true);
				getIntegrityRateComboBox().setEnabled(true);
				getIntegrityGroupComboBox().setEnabled(true);
				getJComboBoxAltIntegrityRate().setEnabled(true);
				
				getIntegrityGroupComboBox().setSelectedIndex(dScanRate[i].getScanGroup().intValue());
				
				CtiUtilities.setIntervalComboBoxSelectedItem(
					getIntegrityRateComboBox(), dScanRate[i].getIntervalRate().intValue());

				if( dScanRate[i].getIntervalRate().intValue() == dScanRate[i].getAlternateRate().intValue() )
					getJComboBoxAltIntegrityRate().setSelectedItem( CtiUtilities.STRING_NONE );
				else
					CtiUtilities.setIntervalComboBoxSelectedItem(
						getJComboBoxAltIntegrityRate(), dScanRate[i].getAlternateRate().intValue());
			}
		}
	}

	DeviceWindow window = ((TwoWayDevice) val).getDeviceWindow();
	if( window.getWinOpen().intValue() != 0 || window.getWinClose().intValue() != 0 )
	{
		getJCheckBoxScanWindow().doClick();
		java.util.GregorianCalendar cal = new java.util.GregorianCalendar();
		cal.setTime( new java.util.Date() );

		//get the time for the open window	
		int time = window.getWinOpen().intValue();
		int hrs = time/3600;
		cal.set( cal.HOUR_OF_DAY, hrs );
		cal.set( cal.MINUTE, (time-(hrs*3600))/60 );
		cal.set( cal.SECOND, (time-(hrs*3600))%60 );		
		getJTextFieldOpen().setTimeText( cal.getTime() );

		//get the time for the close window
		time = window.getWinClose().intValue();
		hrs = time/3600;
		cal.set( cal.HOUR_OF_DAY, hrs );
		cal.set( cal.MINUTE, (time-(hrs*3600))/60 );
		cal.set( cal.SECOND, (time-(hrs*3600))%60 );		
		getJTextFieldClose().setTimeText( cal.getTime() );
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (2/19/2002 10:33:24 AM)
 * @param box javax.swing.JComboBox
 * @param altBox javax.swing.JComboBox
 */
private void updateAltJComboBox(JComboBox box_, JComboBox altBox_) 
{
	Object selItem_ = box_.getSelectedItem();
	altBox_.removeAllItems();

	for( int i = 0; i < ALT_SELECTIONS.length; i++ )
	{
		if( ALT_SELECTIONS[i].equals(selItem_) )
		{
			altBox_.addItem( CtiUtilities.STRING_NONE );
			altBox_.setSelectedIndex( i );
		}		
		else
			altBox_.addItem( ALT_SELECTIONS[i] );
	}
	
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GB0F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8FDCD4D55ABF251699ED3AD1313D3619D952C6E9C5C5E6AD9135D6EEDA0B493B6B6E3A3B6E463BBF7C45363E7BDA716615BF8388A886181696AA9596A17FD4B2B4D0D4D4AA5291515CE2E4AEB0B04C0CB3F7E0B05477B9675C675CBBF76EB0005B6F675B2747FB4FF31EBF5F731CBF775CF34F99047C77A625ADCF4E970465E9027C5F0E6502E03DD2907E747273231097A4C69B8455FF9B01D68B2F
	6EC8056BF8484BCF08B6648A0F2C4A007494C8AF0A0BB63C8577F9C2542AAA975C7871138DF92F3CEADBB3381F68F67413CE53EB5333613A92A88CF8643AC4A47E4B52574A7C59B29FE9F932A0ACE8A7B20753574B3CEAC8B78105820537B3799838E6D24FE3F6D64BF227A6CC9216BC73E1F28E31233209D45441F49B64EB21B0EDF9E28771A5D11CC4A7885211C02470A942CA31862EB5CD5D9D3B5B6CBD25EDA61B4DEE1344364ED251EC116C4ED23358EBE993F76E2C275723A0F044A4097BE8E61349A6DA2D
	1D447E4C748CEACF902AA03D1472BE7E12781C8E77330096F27EAB14DFE309B698012C96AC672E8FEB64F234FCBFCD0818EFCBD965271C23AD2CFC6196251CA16E57525751BAC96EA4310F073C6C2EE8C3A1D0BD1099E8B450BB34ADE4F6FC84573A5352DEFBCF0F5DD667F06C311A1DDF584D22956EF76E049C19F7C4EC33BB4D02406A7BECFA098E1E4108690D9D369E0BC9D8A556FD7811BB0485779FCECE5709490230326409DDAC9691DDCACC186F477D7A0E6D72EE1B5E3E0F8BEA5FD32E30272469780E18
	37A5B9DE36132472DD52CD6A7A96DA576B61FE395CBE987FCE4AF728782C1D16D3BEC987C15E22EEEC9BADAB91CBE36ACDC2EEF923EE9CC2236B129B2C8CC32BD50B650FDF915B41D666F3A34772BA654729788C4BD20A05240BFA228DE536E883EBDFD9DD245F5658238DADC08E2031C0E1C0B1C0F1F6ECE3C7BA368E310DB5191C529E3745EA961D82534D6AA4831168111C2603B617E43235094DA62745F45AAA42B845E472BBC98CCEFB692D23ED3F8DFC9C90ED2253A4D96CB630EE6A91A55169E2B21BBAD7
	939927C8F30D169E919C74B884347BD1E79EDC2DA617F44CE1064E2EC8307C0ACED2A73D26E6308886601E45253A1B44EBA914BF57817D4E01F1381B5ABBACBAE13458393351EE6FF6BB0C24A40243C1629CC7470EAA38EFF4E00C2B2C04978169595C4E224EB7D5F66AADED843E49590FFD6C76AF49583C5001ED667F36E01B294F39DDA819F7C8374D904AB8A33715B35A3EB30D5AA438D8FA4717AD436C470391416ABBD6B47DCBFD24E852AC3E13FBC8DDB5C0F94DC0234FE00C5F339432B63B4764923115FE
	650486A61A1508B31C71DF554A76C8BACDEEBFD9C0E45CA64F284DD4CE908A0036G116770A7C024DFECGFA0F17F9F99331B591F044GA5G65BB238DB5C0E620E020882085C0292EE8C3891091E8GE88A50EC20A5AE2C23B73EA4150C65CD13FD2C1431654878D64B3C829957BA99BF62DAA963C7BBDB64AB627FD7554F8FC5AC96F5DF29E30175043140FA4AD345E21D5CE6B265B8EC1473D62BEC3FA5DFB1DE7F3F4B9A08D85BAE68A26595FBC51BD4AFBAC41B99EEDCF25E7AAED2DEF93CED37F31DCD146F
	0071F9AF8C08AE4EAF6EA271B0C91253F25AAD09C7C5C93258BA147CB26A4B65BE6DD006F67435230B54013D8F46E7AEDF53C54636B63B4DA652CE46D5647C5DF4EC952DE28F005EEBF75BA4462F6FA663D33758AF0F6D8D5DE4525AEB323A49F3024D71261BC8DDBB1C169E683A64D1A384C9E45CFD194FD1892D879E192B9A615AA961985A379947502233378AD15F3D1F2CB787883D3DBEF9164446328512B206B2CC1FDA7531FFCE47F8196FE2D8CBAE107667F732A96078759B9B3119C44A610EB6943BF17C
	FA44CA727199A18FDCFB6DD6AB8BAB0FF7D8CFAE299F710CDB04C7ABF9989D66B11C567326A62FD90EA74BFF280744532AFA0E4539C9BC6B1AF0BEDF8B6996A00F1B45B4524DE24264523EC63922DE9863FA7D49BD4C65CE010C64D72E67884A0500CCB8506CDEBD397223A8175D97EDA86A73A777E7AED78FB2277C4A7D4408F283A09362D76E0BE3A897C1E47C4AADEFC6398410C9757813FB004B9501CC8D50A90F1EDC7FF1141B887991FE65BEE9C139D848CF70AB7787AE175D8F71832A6FE7F2237B55F237
	1F6076001FG14FDD64FDE67C914EB007C56337E6456FF03F2C1109F8A942911E3FDA77DBBB24F57FB176BF0DBCA5D12054C1ACC928CF08E3724FAF7BB1111A15BD8FA965F51DCD924FF15FD9FEDE8GEA7D9E470367F3709DAD24FB12307036377D3E0BFA3EE758A6FE2F0C870523709D6DFD877139G72923E473EDBE1D34F936BDCAEFB1B05CED165FE77871BFA3CE03C3F58D640355D62F4C987E1C077E09FCD30913CE48EAB3365B9FDD7D336DCD6E1A05AD08DB4F70036296EFC95F68770C201E68225D0FB
	4FB7D54055C48711D2F6E9921D6DF6E70FE8AE6EB2751ACACDFDD2A9FD361456516CFD64FE8B56CD4C16B799CE11C61FCD2C0B37B4633B56643CDA59776CFBEE92EAAE64A56B3D7B0EDD35A1F96DB9964F5AF3CADC876EB324A4FC7BFABAABE36957D846E01009B907E5DC6D56ABE39264B33DBFF23D5C73D06F67D16FA3C9CFAF68BC6A5DF08475C200B71B6BFDD02556CB3D4074CAAEE0CC7F7B48C8E34A7C1EBC0AFEEB412667827ACD2B5473BB7D8262DDE9C43D9860A5F13DFB7B75742A02D0AF1A6BB5GAF
	A888757E3AC3CFAF126B759EC33D5940CB3D8C754669462766B2547B2899751A00B7166B9D6A53530B62FA3F63FA31404B3E9C75AAFA75746AAF477A58F77C626AE372F1745B82B647F33FDF1F5573BB6BF2445B5C02FA8B0017BF8A75FAF46B31F1946A2DB801FA2D408B65FA7FE8563657391037E49416734C090B6B4B1FA46F01EB575ECE312DFB0F5DC326A947ADB6333D6F3BBA0D31C67F56BEA3FD1F1563B9DE0E0C5151065A51D80EAD9EF5B9BC23D9B9A2C7E3B9361DA46FA1EDA627A81552FF65D105B8
	75143A7AE0C2AA7B5DCBF265C2B01F93CE72B68976D247204F4B376B792C9903B15F778DEF13409B4B75A67934C92297B5863106FFB35218337718EFB334EBD831E314F76778B13E6BD8ECED67A57A9CCB9A4356F652B87F954A4F3DC261337275E7E1790AA0EF6C95389E91F1064C1593A01DF1E5342190289A288548F3A5BE3FA4FB2590E03D67A0BC0F1DB6137528570B812E3BB8C82CE0AEAF27CB9D84871D4B8B9887C713B68EA1D7FA17FD7A15FAEB8A67FD74CA3571546831363E3B19E021F38AD5335A39
	739036FE79DD6223EC753BC44215ECADA7F7AC4C130058BCA321BBC3E06F11B5E3F1BEF1EE3FFABEB1F3D5C5B219DB6B4DA7A60DE57822462A5675781A6F37AEB2C7922E0AB62C872A82EA8212004683C5DC05F594695895280EE843B35A77822F816678FB356FE4BC26D5DF0F2CAE767868795445D55EFAACFE8DEE92BFB25FCA388A57BA62C708619F81B1B40D5343E01290C367B844B0714848B07C4CC7CF0BE176B83DB659E17557A66DBD276D24CDB22C7F2C4A10FDAD62D8DB0F0E8C2B4DC7CF0BF57D55FA
	712A29C48CD5D7A306FC6348B0EC725153E2306AE2086FC78C23B906D9A34470E2C08CB3F5B1EC2CC68C319C43403111E19867232745109D2C0761F15E9E0A0291C3D97348B06C765153E2B8250B6132BE44A0F18C0BC6086115009842F5B1D8FA91C38C4790F6FCE4989E745153E2C85245B076ACE2483F86B11CEA999906AE9FBDAD0646EB74B0CC61FD534CB1641E989906F7FD745E5524A7EAB030672633D26F39998132332E416766A747717DFCA14799BBC21CFF74515346EA6D0F74465C0FB6148B015696
	8E99C50F345FA2DA4D640B51BE1BC4BE4D4831EE75E0994AFE04E5387164484AF03B0F1E368C1DBF522B6F0737A306A00E6170B7A343F042C7CF0BA1D29743FC5E7F67C2BE7B7E65266BAF9957425C65DA64552EA03CDAC837DC0BD897E7915EG24C3B9EFA965054A6FB80CF7A1AB5077A3222F658D6870740879888D68A3CD07277D96650F58DC6D51B37E576BE34E682F57BFF3C6FF3DFE6E19550C8FB654DFDBB1BF6E4C606B798B4F682D67EF20E5FAFA4DAAEA5B6592DDF472C89659643C17A472CD45E7DD
	7FCBD30FA847A3CF185A14A94B6C136CFB4B856CF3FF5FC246861DF90EEA6D6EF4B6366908EBF16D2E300B3C43A4C0BAF5BC34297158AE7E562DD7E7D56365BDA3727554F86FF6600074E88DAF945261C0D1AABE1B077F192E954582BF91A8E3823CAB8155CE4079F72FB360BB52BE4FA06FC81DAE7FED426E52EF93EE97192F3A6CEEE71B282D778157DA1E274C748FCAE28F29A2A6B3066ACBA6E7076853EE0229BE4B7327FF3D4BDF5BE2755F7D353A7E2BF89F7B422DD7DF2D93D84CA579BAF602EF3F1D2861
	CD07F40C0647FC3FF7C46D3B027BEE1774FC4749B692642B70E3DF5F799ADEB124EBFE2C677BD7DE3EAB396F53D5B836B66958B7834F23EB6F4623EAFB5B393D3ACA34972CE3EF9270A2F46D9DB62A6DD5F3FB0B7B515EAC9DFB8B001728EBEF19173DED5C5E8755E8AF75BADFFBEB01D7F21D1E3D7B0F296D6D60761E62712B5531E784DE272E3D0EE6353DCF393DAB7A505E000E3D71401B22EB6F1D63ABD4764A4B793BD4AF5A0B5431B79BF88BF56D7D5E5BDE995A3B66AC5ADB22E3AFA384464090BDFB5735
	F8592BC0FBF77276D29662EB2F9EF8AD3A768E1C7032D7094F7C1F71F926B5844768CC3AC6BB8552B301E2C3F00C2E7019CF537EA67B932E774543FCE71D7472DD05E591BD44DE3E0E9E6D1BFE6DBD67EDEF9B5AFBECBB7D466447DE13DFFB17FF63E5EFBB5ADB405B16D9C757833C606B75E29D1CC13EF3DBCD7DF637846FFBB710185E1601F369C92093893490E849756AEFE47776117525F62729870660238E31CD08273A47D7202E91521DC0E3014200E65D00EB18EF9411EF466C69CB96FDE13CEF37F430BC
	294FBB0FAD8833B2F6E4EB6B346CC625CC75B77A161B2DD1B652EF7EADB732B54A0FB93F0B72A3D5FC36F67981FD6E13745A50E80374931CCB66516F6CF3EF02F85C04BC8F65A5C0BA755F107727F642DB8B69AA4EBB5CC1672110BE45F90BCB894F8169A04EFBF9BF4ED7C3B86FE48A61AD02F49267356CA63C5C0951062209B8A7D8AC79FF26AF11741F692FC97A73BC41BD78BCAF436D6FD94C7059699ADB99E02B1D0818E3A92F91522D1C37013EC3CB109E4FF9E3A8EFA2244395395D9817A84EABAD409864
	5E0C3C31F4DDEDBD24CBB8EF3104FE2BB92F1CE101F4A7677D0472A4C80F663CBF37205FD04EF33951EFBC6775D7E17DAE613C81B76AE67C94F953BE453AEC613C16CA5435F25E2FFAD1B708732E59063AF3B9AF75AC6AAE623CD965283B14736639D0376196646D2A66ED1273DE71202E0373CE6FC05DA84E7BADAFEFAC6795F14C711C77C4956AD6CFC25E4DFD285B48F99FECC35DD64E3B2697F527CF72EE9BB110DE48F91FD6216E924EFB1156F97D2DD036DB11B705464A8169E04E3B2F9AF5A7F15EB23A4E
	1788690C49480B22F5349E52551C97A921EEA367B5D13909100E180C4F22E9156AF1EB967097CCD6462A44497E462D5B6933AD75B6B6BE65F17EC314DF256233F1EB95651334997204A90869A0DD77188869F04E3B67B8E20F643C81DA779937433B466DFCAC636D21047366EDC739814E5B3E836546F25E5FE9BD47C1FA89676D60751CF18772BAE9BFE9A169BB707B583399384FFEB470E65E017B6C4BDB691EE1FC17A3DF3BF66EBC6034187718BA9A69D30C4D9572B2B3E47DA5F760DC21AE1358AC199AED28
	87BAB5953F1DF8BE40EFA761F75CA4B4869D565D0B3150D811ACCDE5F595BAD579F69266FEAD1D79BC3782FD46C2FEF698615D35C27DBD0FFE17CAE6EB9CAC7F37DE79E4CCA57BA3157CD5D96AFC0277C40ABA3F4EABBFCD254F4A766C7BD83624E743846B831F7A5DE7E28CE3E572485726664DC251385753D99D1D276F4753A1AFA68C6B6D56153C3F07E1DBBF52ED04EB751D90DF208120C9C0B1C009C059BF03E7A2D08B10849482948194871484347EAEF8DEG1D828A821A84948594FF975AF7D07BE4CFD8
	F6B83449F036EF096C41EA8C1777BA413D956E03014243593EB032170B6CDB22FB2B4259DE2C8438E65F4D76A911BDE14CFEC9D753F25F5810F2E8F9249CFAF3B7A402FF30FC921775F29963D532A4C6A4D6D1817407CAA1B2CE92E79A5BBBD9FC55B2F12A32446814D50F12B4652F1675DAE43E244AE7319D259B5BF49D5B2B8378FEBFC0BE6E397DD0E073B86463BE5DAD817407CA38FF172C59107D3D247FED56483C233A4F9A22DD6DDEE0DC6FD36FAFD66765899FD9497ED589A6F8A5F71373D67D83824F3B
	56CA7C7618BC169EF74FD14BD9D1883B1B745F29F46E6A007B013BF1EE7EDABB098F5BD8C01616CCB6B324194DFB4FA9B6CFF790B907D3EC971DCE51CC2D8A325D6F69DCB7816E17F23B29DB893EF63BCDE2B2EFEFA5B2C577C49B4A00026EA1BC43017AC1E202FB2953647BD650A1C462CD46B3896E3F066B6B4B16AD1377272450F2DBEC1AF2DFC5675333C0BE769E44E75FED20B1F2F6D8ECD85ED74FAB653D426D9DDF1B20647D9FB7E9433D9617056CE1E5BE12FB719C17B08DFDD4757A0F6D04650A3D093D
	5E313A0F5A4A85BB6B27E12C2AF6870A552AC1E25588F62E146F197F08E4457FAD12F7D96753E2B0ACEB5CD82E96DE2E86377FF22DCAD36CCEF7FB176BA9EA2B936CC83CDC235E8DD42E14C14AB5856CBC6AD52E078C8A7649F41EB89DE4E6F2FF57398773C77C2CD6790D1F2678A3F9C953589C6C6F5066180FB8BA3F4F189EED4887E23C4B587C9E520D53516FDFFB83794DD27975CC77769BBCDD6B77C1BA2F8A857E946E7793BA5F1D8B69855C6F6F4B8379CDD3794D38575B6F7AFB35FE5F206F9545402F3A
	977D3653B9F5AB242D77225F4C7E417CE6EA7C06EA7C06C37A93AF3FD75379E6A470E338DF830DC1A22413385F4F860DF32626FE0B6F7376DBF31F56EF82EDC38D40B75E07FE1F22735CA0C8875F07FEF7ED8B64D7DD3F119A3F33FD7CBEC84700B860AF3A8F7B5D44DE7FE37083EC4C389F460C7B91D3D3D5203A4FD1E1EA385F9B53297B153AE77E97CB7A63EB9A6B57A0AF5D0FD89FB96B9FEBACED2713C0B61CE3FD3ABA9056BC9556B88D56849F2CEB4E6AE3AD253E1702FCEA84E21DDF699FEBA26D4BE5A0
	DB9301D867C80130262B30F6C6F8E39D0850E26D285447FA017A9E8B72A191B887FF0A7222A09D4BF93D67F02E3E1473924F63DCFD7D83B8573D2B39882ED6C88783CD811A8994871488147DA0BC1B019A9EC479C729BC592F5F8A3C2087591E7CC98F32BD77E4BFFD945CAF6472854D636598907B64CA6C0DE41EE5157190DC8DFEEC44BD604B47B229F98CDB8D47065FD7834DB983BDEB55673F3424B76FA3B8B2F5EC65892D473D1F3F09C4D4E0ED6D496343F9BE8A8F01BE9019AF12B6168F778765FBF96D5E
	C330C9F63B55E8F110FD8772F3B3AFD371791AE7B84F0CC215CF422F027BFF8A6AE77276950A6DAFF68CE6BBCBE33BD6E35B28324D4A3342633F6FEE26EB992D20E3FD887B6E17030E53B8C6A306D00D7F7007585A2B52F7AF38757B6E0DF4CC0C8479180730FFBC6F3397B790B13D5F0FD0EFED66F07558FAD35E8A32EE1278909B2716F37EFA4ACFD271595A4D01CCDC3B490804D8C7625E5FF5D9645B40DCC8A7G25BE8CE391D08DD04B4338067DD3F71990603B36516EE894FBC52BEAD91A156F7295A37BED
	004753F10DA4E246E4A1720AD7177B5BBBEBFD180DA54172B55E381972D97942695E5D19109787148414BFA35AD08CD4BB834B7797F789AB9FB90CD7A7C92636CEFA8A878B57EC917B94B67930425A5575593E5FE94279DEE1638C761D468A5720998C9BEB5F11AB07B2DE70F99768064FE04FA6120E1A2154AD43E1D9C34E53B709CE0B5DECE9EB90CDD62913EFE146712B1B1C51B954E8BAADDA69C66DDE13358164B07FF75F96707CBA2B64ADA24B6C7E0E4A60CED490C3DF5DDCE64A01F7FD3020B13E05D516
	5D57340A7BBC60343B9D549B4B3B4D730E0F9DAA430D485FE15FA2712EEBEBF377382DA6496E7492124FB73D37525B8F08A30610B379FA65868916BF674355BEF1236A327E2BF5EFF97BA707BAF970194CDF0E6849F8172363E41EA636A0CF64647C3CE3240E89028E27C56A778E853EA39E54149344B16F069D85EAFDAF84A87359C95F32D293F27E42933A3102AB4BAFA9DF27A90317BFEEE77181DFAC3C398A6C4C0523C03D06BAFB86BB9F13B8834FE4C4756A6527BE027973F47357727C825D7C921E6F1074
	72EBF9FE27EE3E1167D7F87472BBF97E4365FA7983BC7F498A3D7C710F607A781CEF543F1B13FB5346E49C9334634914C75858B37D915559827A4C9FC712F21D44351335B91B58D7488F2C54DBE038A3BF02D1DC3E2D2C243022FC0BBCB6CF6AD37B2EDE31C3775CB421C5324F2572F566151B65794612D50A6FBF168E45B7530BDB23687DE97FC62D5EFE3BCDAA5ED46734182C1BCA8A4B617F8A322E781E4066019902729F3377BA691AF2397E100D4F1C75F77D1BD0BF7F5565380621ADD759CCF9BC1F2944B4
	3EABA91D599457A9983F58BD1432B159F5F40DC9F23AC5E6676692454E6A5DC3ADAB530D7AC4517D446DED372CCC497B77166159BDDA2E44AE36C231F346BDD8B94BC8B9F7EC512907444F192D7F2AD46C3E3DCD31DBF5F6A8F6195E2E6D0A5E2C5E0B43533AC341B32ADA31FB77106CB23D0FF7AAFA0F16DF9C1EAFBFD3701C29D16CCE9C12DD269732CB51FB49F3F1F89E5A2D607975670A5D02A159E5FA7FDD2B682D3B487AAA5C2360593BD7313BF4C86D12693557ABFA6FEC3BB8BCD76DD370046FD76C76D4
	8D9D4F1C830ADE6910747C63F925C141D3F0C8319BDFB1F4BC5507953D29559707671BC6854F79AF943B3F5CB1F4BC37FE296A175245610979CA4173D213E2B7E7C8F6196CEFAB497B40E93B13EE4AE16F83CB789908C8F8D64C854AF8945E319EE5FB5A194CAD67D0266AD1764E508857D6D9063DB324DF88744E20FEA77700EE70234AFA56A438A767E997AB6F44DB59FC53671CAF4EA5DB4902G1B6B997B9DA246235E26703FF208B664B71C25F3CFFECAC21DF768C0965759EB353BF0CE3B47314ACBC71D77
	0F12378455DCD241C7414898AD8A164E4E1E9E743747196545AF1767144DFD5AB99509EF7823B867B26A66C7717C5D5B7572E3F97E21CA3D7CF81EFF4F813D7CA44877BFBFD9F1045C7BEF379547874FBF5622582AF4A86D3A64A2473766B30A5DFE27E277134A2158E5EB836B681A13FC7ECFF57FC5663F72B7427457DD5AFFD09F726FABE5795F7B37A5CBFF6F5F56AC7D3DFF5B3286BF4331AB4B7FEFB255E729FF13092D0B9CF41136187B983C73GB5811901BC0F611A488BD91B843AA622339E324F5CC14E
	9310DF6E13507F83597E4BBAB3BB706F7DBD19CD9A40E93BA459FB08FD64BF1BCD4F935A9D14A91FF749A6B1F2DABABAA5A6AA73DF247BE12CE2BBE533F52FF1B9386E9582E51B7D981EBBFBA1877785257E9C77A289AB69EFF7C25A88E47D39DE3FCC7C5CFF1FF8093CAA637916F6BF67DB682844B0CCAD403D5323FF0E387E0B1FDF1C49F9597CDC55924EEB64F2A5D1481BAA71B3FF1CF78B67CD64BC0B87F9311C17D80E3C6C471117D4013C7A47F13DB044AD70F8376710BD9F4D26AEF9C5107D16953612C3
	D06C2FCEF709ED125154415B5DF90AFBG6C05BE0EEDED4E4A0F7D5981E613536E901DD2BF6352DF2B12ED4D68A34F44C830938B3494A8B7BA5AD084D4835484E48672G0D878A831A89B497E8915092200CA760D97C846228DD3BE5909C7206B65A701DA68BDD97E1EDECFD957E2EE799586AFC8277453D34E10B9068DC2151A2D945BD7214C15ED3EF2E42F3CF131E40367944G7D9DCFC8A782E544601E3CDF1C4376DD883C16982C3BD56731FD0F0FC19B3B4E9359D910DEC8F8B143ED5FB710A30FB246EBF8
	1B4CFD92FDDE4DF98D1C675E0E3CA04E3B0C1F9908643CBF9FC0DE6213D89FDF8FF29E2A4595F8CCB1516F60A20C85E452A037D9971923595B5BDDA29F3B3ADD391CF75055E4F7D9482E89761C981FB332F57D1F0ED0EF7E3075583A3FC4472E6227345F834ED1FE55D35A6F81061558EEEAA1EF4AD3787DA33CC06DBF6397ECFDE33DFCAD7B056F77BC2617542B562B1765DA642B552F5E6ABE35DE10AC97A2DF437C6AFD6D65EF26ACB7D73EAE722B97AE2975164AF2593364F51CD97E7426F869D549F28D7275
	14DFBD1BC72D671065C64B57D03FFA7FD92E568B17652264EB2CDF3D65DE7B1A63E539A4791A7B342F9E6B8323FA49BD1FDF30E1D64E9BC757CAADB66896A62B65AC7D593223A0E86BE0794F75796F27737A8277535F7569BF7B977579BE7B7F23CF7F597FF21F7A59AF1F2F68A3E3E8870C5BADDC6ED54EBB41F9EFD01B04F75CE2265FBB98BF5533C266B70844A172D7F8882EBE4AEB11F92BD43C93789B469EB256C011BB6DCE54DD67217B2DACD6ABCBEF20B2969B8CBDBC9E1BBC842345E1D2617E1073CE70
	3A5ED495E82CA85254377C3B90817534677B191E056A556A34A1BDB2029C9B03F650E7D467535A31291672252735E3535383B8B685C15E2227F1EC7A6539110D293F9921DE7E79110D45775267EC43B35A724E20FC63B35A726EBD0F65ED053C18E7303CBF769A9B0B1FE5F12F112FCD4F7A9B8346FB6919E5B90FFC8D762BF7F63BDAEF12AC97A1DFE779553B42EB4CD9A04BA54A5754597E747EFAC02D37D616AB112F35FE7568D2836EDFD65DE72F7C615F9B5F7FC1FD4867F368FB81B903E61C0DFB05620E51
	FD03B7417C1273BEED467DC392676551F1B08652311C371F6A46C3FAA9675DF58CF5D3FF093CE22AEB04341973586FF3F8A0BD1673FE4C4F2904F0DEA7BB978469A51C97F1146E3BFCAE5A3076B9642DB322EE9167BDC15FD1A4C80F663CEF29DCA8244394B92E9B41F90FD05D54B951067CB948BBCA650AA1DD4DF911DC371E734650772090C8CF613C1B695932C8C84F623C5BB621EEAC67E553F723223951062A3948B352FAEB00F48B67BDDC003AE64EDBCB673F91100E613C95EB68DEC2C847F31EFD8DBFC3
	85BC761B8BD1F9E4AF0D45E63558443D6C1770F93B89226F8D754FC79BCEBD4F4FB88E50368469514F6338326B1C52E7762B6E678C7C707DA77E7F010F074EABE50AD25D9FBC7F437BBE7D037AE0F5FAA76D276321BEA771FADEC77BE48424E3B86FBC6F27F3B9EFAA55ADFCA15AD07682725E27F27510B6F25EB82E5B4AF993683B5CF4C8C7F15E9F5692DEAC2497F15E7EF5281B48F9B7337D563F027772DFA1AFAC8B4F4019B9AFB39BF59D1C175DC077D3C2FAAE6785EF26638D2417F05E5FB6232E900B7B00
	93E87FEB0274A9A0769E7B70D952A6647D374D01565555FB0235FB5B03E35977F8E5AF4EC7FD5E7B69EBE93CC3C1EECAAC6EE57D67B16F3D048B3ED770C49F8B04A7E790BC31BEF8BE5760B9C671AC8439840E27DE3337F1590002674EC177B62687084F7AF95106D5DEF8AEE84ED1DCCF1F9B45A0D7B58F71BCFB549B4F26738A1EB2E3A0BC2B8741E3764173EB499BCFA2FD1EB8C0EE0063F19B3D716C3B2060797520F8183EFFBC61733475750286CFBC459389F2B19CCF17868FF9E3C1BCF3826299ACBECBFC
	70CC74E84E3D50E72670E234A17BC5449351620D676A658A1E991B8255D75AA0F8EADE545657668AEFBC1F5267F0834899B91E6E82EFBC8FA7ABF866968C67BB1A964F6897357159DA690DE79F9D6746035CC40EE7639ACDFFCFD170DC31A6901E74C1704C7641F3E395C9CBE4D5116C6390F01F6DB4BAB7088379C59C576901B4149570DB4EB282D536952599BBDE24E3C77E7CE8C361FC34D12699BBB6991432DDBF68581129AA978D08E6DFF49378F85F2BEC0FE97044D2BC27C02E136329516059275A27FDEB
	40FD5AE90360891D4F66CF8A1E306D5EF89E234F47A9A0B71D63D93AD653D7B394BC7FDCB71C73B7DABC8B673373A88A1E88CDDBFC0636458410DBBA9F1FFDD72B761097E68F27EFEA7D9747E95B5E6686CD5F244F6DAA102B0D43F85C3659BB9E7728761DE7948D06A7A7G9E078A8F33DD1A23346559474877AC1DFDCEE60B49EA6F58D4D2D8D6D2D8DE3585F53FA6AE657B3BF395BBBB07E9E76EEA45CEC93EE2E763716159B12ED16C1CD76DAF8BBBBABCBBFBB295BBF7E6A9F60E9807E7E7F636E227D83517
	6F40B06D9C4ED56CCCFDD331B321E5F8F61E49D36CFC205AA358D0B0BCBB875FD26C5C2E0A739B4334B36BED45CE5186454E6783435BEFB766C3C5B7E718EDA62CD841107A31E2E75DB06D6CAAD16C7C24D431B3EF6D706C4C5C2A58B9DC29581918B594BBE55C0E2DCA31730CEA4FDF50666159D9CC7778313F1F79F2F99134207045C21AA494603673C2366F3C106E121104BC76E7672C6A8D4BAE27300AFBBBC817F19776D66ECC7953EDA949C26AC1414016358F8A29E4ED7A2010C6172E8F8AA9125D61217B
	B1547FC9C22EEA1BF8A1F1A489EF7959DD6EBAA31C9712FDCD2C64BB2BE583853ADB2D7D29276F331155F933A4EC6C6B94ED05123D506E00EB9F5DFED3586E34779476D84CB6D28EC95804B2EDE4730E2E900468CD5F4DA0F5BF7A7779615F5567A78B499025CE1105A52D0F2C40534010F17D868AB207271CC20ED755BEF96B363F4C7EDD33ABC5B09C945611A74FD151C9BFD208E672C31572DF715BAFE4BA6C964884BE54040BDE52DCD23FD5EC94725B0F103F066412C4F39DFE7EF0C150685FB758CA3E52EE
	EDB039BAA592144F9A05D49B79FB03421BAC5BEA32F5ECE55FC2BE232817575552B85295DD401BC77192AB255CCA5EA32B5E5FF8E8D1E40A103CDFD8E1351B4C7BCDEDD0398705CC2953A93ABA6DD6334771B9B314C1A7B9E424CE85F36F7062134216122DD39CF4376766601C781C7DD0279E273015CA1287EDA917B06F6DB4B9411568BCA8113F1FE8F796713C4B5E15BE78650137CEA58B4B65C212503F69F1C802215D54E3317643CDBD6DC834D256C012FD6E60DD008423F4BF14C77A756227A77F26A6B181
	0C6D9752682F7C8324DCC56ACBF74FE951F9C578759B32C296DECD90BBC8F151F22664B459DC64AFA80071C2CD19B10BC3AF3D6E69326B6FFC6DA4052E28CA42FB2C12C0F5EB07E86F59DA575EEE31095CG5498185F404D9319D20D4C1B1E89850766ADBB8DAD2CDEC869A9AB237F1653FFAB483F6514D3CEB96595D0ED095AAE657D1FB65F03E5264D12A6B610B664A1F7EFFB77D2BC361A2C6996A4156B537888B75F37959176DADE17A4758E7967BD72CFA1D55AC47FFDFF58A5A1DA9FEACAD02ADF0214CB99
	73B6FD4C29BC6A64131C194B244AF286DE82EF99DF0D64E1EB8C5F8751A0D361B7FFC00D6491E9E0B9D20624112652981A0F65AA8DFFF5C6084C92699B8267E62AA21D2CF017ADDB76B759EE323F5849B2295C1EFEFCC9EBA62DBE07F38DAAAE4CBDB271D455CEC9482933CA052A34019E6AD25FE8A5B2752487B3794ED78E7AE7CF2D05E407EDA15BE2DBD8CCFE63FC0BA42411FDB3A015C6F6739213F407AE5C2497B7B46C6C6959225C297AF3AB0D5FCA9E895F7E1C027D594C7B733F2E97374AE91B47273673
	F98DDED2C85D3EC849FF29179E5281EF2469326CA59853D19E2FFEF789A39D27A0A5E39D567EA508F40202F43920742834CB8FE9687F1A5A3F459B69A5BCCA5D61DB7B64BFF56DB3DC1750CC6EEE4F2597477B3D7BBB7D13149C53A505F40602144C0CF03E14F269A1FDDCC13AC20EA9712F6EC505D4FB45D010567FGC88B6574AC85E936ACC7DB412507F40EB7D2F225BFDF474ECAEF3920BE833EF44CDA3FE740DB9264BD40894AD9E936FFBF117EA6D99870E392789E7CEA72CD435F190223BDA62BD5B2D93B
	6BECE6F23EE0CF3FA4AAE7E07E331A58F7210C1C4972122A896EAE154E7FGD0CB8788AA243CC285A7GG1407GGD0CB818294G94G88G88GB0F954ACAA243CC285A7GG1407GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGBFA7GGGG
**end of data**/
}
}

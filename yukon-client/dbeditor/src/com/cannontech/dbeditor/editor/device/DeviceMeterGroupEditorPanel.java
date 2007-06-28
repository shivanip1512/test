package com.cannontech.dbeditor.editor.device;

import java.util.Iterator;
import java.util.List;

import javax.swing.*;
import java.awt.*;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.groups.dao.impl.DeviceGroupDaoMain;
import com.cannontech.common.device.groups.service.FixedDeviceGroupingHack;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dynamic.DynamicDataSource;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDeviceMeterGroup;
import com.cannontech.database.data.device.IEDMeter;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */
public class DeviceMeterGroupEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.KeyListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel alternateGroupLabel = null;
	private javax.swing.JLabel ivjCycleGroupLabel = null;
    private JLabel customGroup1Label = null;
    private JLabel customGroup2Label = null;
    private JLabel customGroup3Label = null;
	private javax.swing.JComboBox alternateGroupComboBox = null;
	private javax.swing.JComboBox ivjCycleGroupComboBox = null;
    private JComboBox customGroup1ComboBox = null;
    private JComboBox customGroup2ComboBox = null;
    private JComboBox customGroup3ComboBox = null;
	private javax.swing.JComboBox ivjLastIntervalDemandRateComboBox = null;
	private javax.swing.JLabel ivjLastIntervalDemandRateLabel = null;
	private javax.swing.JCheckBox ivjChannel1CheckBox = null;
	private javax.swing.JCheckBox ivjChannel2CheckBox = null;
	private javax.swing.JCheckBox ivjChannel3CheckBox = null;
	private javax.swing.JCheckBox ivjChannel4CheckBox = null;
	private javax.swing.JComboBox ivjLoadProfileDemandRateComboBox = null;
	private javax.swing.JLabel ivjLoadProfileDemandRateLabel = null;
	private javax.swing.JPanel ivjLoadProfileCollectionPanel = null;
	private javax.swing.JPanel ivjDataCollectionPanel = null;
	private javax.swing.JLabel ivjMeterNumberLabel = null;
	private javax.swing.JTextField ivjMeterNumberTextField = null;
	private javax.swing.JComboBox ivjJComboBoxBillingGroup = null;
	private javax.swing.JLabel ivjJLabelBillingGroup = null;
	private javax.swing.JComboBox ivjJComboBoxlVoltInterval = null;
	private javax.swing.JComboBox ivjJComboBoxlVoltRate = null;
	private javax.swing.JLabel ivjJLabelVoltDmdRate = null;
	private javax.swing.JLabel ivjJLabelVoltIntervalDmdRate = null;
	private boolean is410 = false;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
    FixedDeviceGroupingHack hacker = (FixedDeviceGroupingHack) YukonSpringHook.getBean("fixedDeviceGroupingHack");  

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.ItemListener, java.awt.event.KeyListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getCycleGroupComboBox()) 
				connEtoC5(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getAlternateGroupComboBox()) 
				connEtoC6(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getChannel2CheckBox()) 
				connEtoC1(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getChannel1CheckBox()) 
				connEtoC2(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getChannel3CheckBox()) 
				connEtoC7(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getChannel4CheckBox()) 
				connEtoC8(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getLoadProfileDemandRateComboBox()) 
				connEtoC9(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getJComboBoxBillingGroup()) 
				connEtoC3(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getJComboBoxlVoltInterval()) 
				connEtoC13(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getJComboBoxlVoltRate()) 
				connEtoC14(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getMeterNumberTextField()) 
				connEtoC12(e);
		};
		public void itemStateChanged(java.awt.event.ItemEvent e) {
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getLastIntervalDemandRateComboBox()) 
				connEtoC4(e);
		};
		public void keyPressed(java.awt.event.KeyEvent e) {};
		public void keyReleased(java.awt.event.KeyEvent e) {};
		public void keyTyped(java.awt.event.KeyEvent e) {
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getCycleGroupComboBox()) 
				connEtoC10(e);
			if (e.getSource() == DeviceMeterGroupEditorPanel.this.getAlternateGroupComboBox()) 
				connEtoC11(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceMeterGroupEditorPanel() {
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
	if (e.getSource() == getCycleGroupComboBox()) 
		connEtoC5(e);
	if (e.getSource() == getAlternateGroupComboBox()) 
		connEtoC6(e);
	if (e.getSource() == getChannel2CheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getChannel1CheckBox()) 
		connEtoC2(e);
	if (e.getSource() == getChannel3CheckBox()) 
		connEtoC7(e);
	if (e.getSource() == getChannel4CheckBox()) 
		connEtoC8(e);
	if (e.getSource() == getLoadProfileDemandRateComboBox()) 
		connEtoC9(e);
	if (e.getSource() == getJComboBoxBillingGroup()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxlVoltInterval()) 
		connEtoC13(e);
	if (e.getSource() == getJComboBoxlVoltRate()) 
		connEtoC14(e);
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
	if (e.getSource() == getMeterNumberTextField()) 
		connEtoC12(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (Channel2CheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC10:  (CycleGroupComboBox.key.keyTyped(java.awt.event.KeyEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.KeyEvent arg1) {
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
 * connEtoC11:  (AreaCodeGroupComboBox.key.keyTyped(java.awt.event.KeyEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC11(java.awt.event.KeyEvent arg1) {
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
 * connEtoC12:  (MeterNumberTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC12(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC13:  (JComboBoxlVoltInterval.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC13(java.awt.event.ActionEvent arg1) {
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
 * connEtoC14:  (JComboBoxlVoltRate.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC14(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (Channel1CheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JComboBoxBillingGroup.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (DemandIntervalComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ItemEvent arg1) {
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
 * connEtoC5:  (CycleGroupComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC6:  (AreaCodeGroupComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
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
 * connEtoC7:  (Channel3CheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC8:  (Channel4CheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * connEtoC9:  (LoadProfileDemandRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMeterGroupEditorPanel.fireInputUpdate()V)
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
 * Return the AreaCodeGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getAlternateGroupComboBox() {
	if (alternateGroupComboBox == null) {
		try {
			alternateGroupComboBox = new javax.swing.JComboBox();
			alternateGroupComboBox.setName("AreaCodeGroupComboBox");
			alternateGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
			alternateGroupComboBox.setEditable(true);
			alternateGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
            List<String> availableAlternateGroups = hacker.getGroups(FixedDeviceGroups.TESTCOLLECTIONGROUP);
            Iterator<String> iter = availableAlternateGroups.iterator();
            while(iter.hasNext()) {
                alternateGroupComboBox.addItem(iter.next());
            }
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return alternateGroupComboBox;
}

private JComboBox getCustomGroup1ComboBox() {
    if (customGroup1ComboBox == null) {
        try {
            customGroup1ComboBox = new JComboBox();
            customGroup1ComboBox.setName("CustomGroup1ComboBox");
            customGroup1ComboBox.setPreferredSize(new Dimension(200, 25));
            customGroup1ComboBox.setEditable(true);
            customGroup1ComboBox.setMinimumSize(new Dimension(200, 25));

            List<String> availableCustom1Groups = hacker.getGroups(FixedDeviceGroups.CUSTOM1GROUP);
            Iterator<String> iter = availableCustom1Groups.iterator();
            while(iter.hasNext()) {
                customGroup1ComboBox.addItem(iter.next());
            }

        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return customGroup1ComboBox;
}

private JComboBox getCustomGroup2ComboBox() {
    if (customGroup2ComboBox == null) {
        try {
            customGroup2ComboBox = new JComboBox();
            customGroup2ComboBox.setName("CustomGroup2ComboBox");
            customGroup2ComboBox.setPreferredSize(new Dimension(200, 25));
            customGroup2ComboBox.setEditable(true);
            customGroup2ComboBox.setMinimumSize(new Dimension(200, 25));
            List<String> availableCustom2Groups = hacker.getGroups(FixedDeviceGroups.CUSTOM2GROUP);
            Iterator<String> iter = availableCustom2Groups.iterator();
            while(iter.hasNext()) {
                customGroup2ComboBox.addItem(iter.next());
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return customGroup2ComboBox;
}

private JComboBox getCustomGroup3ComboBox() {
    if (customGroup3ComboBox == null) {
        try {
            customGroup3ComboBox = new JComboBox();
            customGroup3ComboBox.setName("CustomGroup3ComboBox");
            customGroup3ComboBox.setPreferredSize(new Dimension(200, 25));
            customGroup3ComboBox.setEditable(true);
            customGroup3ComboBox.setMinimumSize(new Dimension(200, 25));
            List<String> availableCustom3Groups = hacker.getGroups(FixedDeviceGroups.CUSTOM3GROUP);
            Iterator<String> iter = availableCustom3Groups.iterator();
            while(iter.hasNext()) {
                customGroup3ComboBox.addItem(iter.next());
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return customGroup3ComboBox;
}

/**
 * Return the AreaCodeGroupLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlternateGroupLabel() {
	if (alternateGroupLabel == null) {
		try {
			alternateGroupLabel = new javax.swing.JLabel();
			alternateGroupLabel.setName("AreaCodeGroupLabel");
			alternateGroupLabel.setText("Alternate Group:");
			alternateGroupLabel.setMaximumSize(new java.awt.Dimension(114, 16));
			alternateGroupLabel.setPreferredSize(new java.awt.Dimension(114, 16));
			alternateGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
			alternateGroupLabel.setMinimumSize(new java.awt.Dimension(114, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return alternateGroupLabel;
}

private JLabel getCustomGroup1Label() {
    if (customGroup1Label == null) {
        try {
            customGroup1Label = new javax.swing.JLabel();
            customGroup1Label.setName("CustomGroup1Label");
            customGroup1Label.setText("Custom Group 1:");
            customGroup1Label.setMaximumSize(new Dimension(114, 16));
            customGroup1Label.setPreferredSize(new Dimension(114, 16));
            customGroup1Label.setFont(new Font("dialog", 0, 14));
            customGroup1Label.setMinimumSize(new Dimension(114, 16));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return customGroup1Label;
}

private JLabel getCustomGroup2Label() {
    if (customGroup2Label == null) {
        try {
            customGroup2Label = new javax.swing.JLabel();
            customGroup2Label.setName("CustomGroup2Label");
            customGroup2Label.setText("Custom Group 2:");
            customGroup2Label.setMaximumSize(new Dimension(114, 16));
            customGroup2Label.setPreferredSize(new Dimension(114, 16));
            customGroup2Label.setFont(new Font("dialog", 0, 14));
            customGroup2Label.setMinimumSize(new Dimension(114, 16));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return customGroup2Label;
}

private JLabel getCustomGroup3Label() {
    if (customGroup3Label == null) {
        try {
            customGroup3Label = new javax.swing.JLabel();
            customGroup3Label.setName("CustomGroup3Label");
            customGroup3Label.setText("Custom Group 3:");
            customGroup3Label.setMaximumSize(new Dimension(114, 16));
            customGroup3Label.setPreferredSize(new Dimension(114, 16));
            customGroup3Label.setFont(new Font("dialog", 0, 14));
            customGroup3Label.setMinimumSize(new Dimension(114, 16));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return customGroup3Label;
}

/**
 * Return the Channel1CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel1CheckBox() {
	if (ivjChannel1CheckBox == null) {
		try {
			ivjChannel1CheckBox = new javax.swing.JCheckBox();
			ivjChannel1CheckBox.setName("Channel1CheckBox");
			ivjChannel1CheckBox.setText("Channel #1");
			ivjChannel1CheckBox.setMaximumSize(new java.awt.Dimension(150, 27));
			ivjChannel1CheckBox.setPreferredSize(new java.awt.Dimension(150, 27));
			ivjChannel1CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjChannel1CheckBox.setMinimumSize(new java.awt.Dimension(150, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel1CheckBox;
}
/**
 * Return the Channel2CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel2CheckBox() {
	if (ivjChannel2CheckBox == null) {
		try {
			ivjChannel2CheckBox = new javax.swing.JCheckBox();
			ivjChannel2CheckBox.setName("Channel2CheckBox");
			ivjChannel2CheckBox.setText("Channel #2");
			ivjChannel2CheckBox.setMaximumSize(new java.awt.Dimension(150, 27));
			ivjChannel2CheckBox.setPreferredSize(new java.awt.Dimension(150, 27));
			ivjChannel2CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjChannel2CheckBox.setMinimumSize(new java.awt.Dimension(150, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel2CheckBox;
}
/**
 * Return the Channel3CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel3CheckBox() {
	if (ivjChannel3CheckBox == null) {
		try {
			ivjChannel3CheckBox = new javax.swing.JCheckBox();
			ivjChannel3CheckBox.setName("Channel3CheckBox");
			ivjChannel3CheckBox.setText("Channel #3");
			ivjChannel3CheckBox.setMaximumSize(new java.awt.Dimension(150, 27));
			ivjChannel3CheckBox.setPreferredSize(new java.awt.Dimension(150, 27));
			ivjChannel3CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjChannel3CheckBox.setMinimumSize(new java.awt.Dimension(150, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel3CheckBox;
}
/**
 * Return the Channel4CheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getChannel4CheckBox() {
	if (ivjChannel4CheckBox == null) {
		try {
			ivjChannel4CheckBox = new javax.swing.JCheckBox();
			ivjChannel4CheckBox.setName("Channel4CheckBox");
			ivjChannel4CheckBox.setText("Channel #4 ");
			ivjChannel4CheckBox.setMaximumSize(new java.awt.Dimension(150, 27));
			ivjChannel4CheckBox.setPreferredSize(new java.awt.Dimension(150, 27));
			ivjChannel4CheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjChannel4CheckBox.setMinimumSize(new java.awt.Dimension(150, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjChannel4CheckBox;
}
/**
 * Return the CycleGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getCycleGroupComboBox() {
	if (ivjCycleGroupComboBox == null) {
		try {
			ivjCycleGroupComboBox = new javax.swing.JComboBox();
			ivjCycleGroupComboBox.setName("CycleGroupComboBox");
			ivjCycleGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
			ivjCycleGroupComboBox.setEditable(true);
			ivjCycleGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
            List<String> availableCollectionGroups = hacker.getGroups(FixedDeviceGroups.COLLECTIONGROUP);
            Iterator<String> iter = availableCollectionGroups.iterator();
            while(iter.hasNext()) {
                ivjCycleGroupComboBox.addItem(iter.next());
            }
         
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCycleGroupComboBox;
}
/**
 * Return the CycleGroupLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCycleGroupLabel() {
	if (ivjCycleGroupLabel == null) {
		try {
			ivjCycleGroupLabel = new javax.swing.JLabel();
			ivjCycleGroupLabel.setName("CycleGroupLabel");
			ivjCycleGroupLabel.setText("Data Collection Group:");
			ivjCycleGroupLabel.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjCycleGroupLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjCycleGroupLabel.setPreferredSize(new java.awt.Dimension(140, 16));
			ivjCycleGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCycleGroupLabel.setMinimumSize(new java.awt.Dimension(140, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleGroupLabel;
}
/**
 * Return the DataCollectionPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDataCollectionPanel() {
	if (ivjDataCollectionPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Data Collection");
			ivjDataCollectionPanel = new javax.swing.JPanel();
			ivjDataCollectionPanel.setName("DataCollectionPanel");
			ivjDataCollectionPanel.setBorder(ivjLocalBorder1);
			ivjDataCollectionPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsCycleGroupLabel = new java.awt.GridBagConstraints();
			constraintsCycleGroupLabel.gridx = 1; constraintsCycleGroupLabel.gridy = 2;
			constraintsCycleGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCycleGroupLabel.ipadx = 9;
			constraintsCycleGroupLabel.ipady = 2;
			constraintsCycleGroupLabel.insets = new java.awt.Insets(7, 15, 6, 1);
			getDataCollectionPanel().add(getCycleGroupLabel(), constraintsCycleGroupLabel);

			java.awt.GridBagConstraints constraintsAreaCodeGroupLabel = new java.awt.GridBagConstraints();
			constraintsAreaCodeGroupLabel.gridx = 1; constraintsAreaCodeGroupLabel.gridy = 3;
			constraintsAreaCodeGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAreaCodeGroupLabel.ipadx = 35;
			constraintsAreaCodeGroupLabel.ipady = 2;
			constraintsAreaCodeGroupLabel.insets = new java.awt.Insets(7, 15, 6, 1);
			getDataCollectionPanel().add(getAlternateGroupLabel(), constraintsAreaCodeGroupLabel);

			java.awt.GridBagConstraints constraintsCycleGroupComboBox = new java.awt.GridBagConstraints();
			constraintsCycleGroupComboBox.gridx = 2; constraintsCycleGroupComboBox.gridy = 2;
			constraintsCycleGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCycleGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsCycleGroupComboBox.weightx = 1.0;
			constraintsCycleGroupComboBox.insets = new java.awt.Insets(3, 0, 3, 22);
			getDataCollectionPanel().add(getCycleGroupComboBox(), constraintsCycleGroupComboBox);

			java.awt.GridBagConstraints constraintsAreaCodeGroupComboBox = new java.awt.GridBagConstraints();
			constraintsAreaCodeGroupComboBox.gridx = 2; constraintsAreaCodeGroupComboBox.gridy = 3;
			constraintsAreaCodeGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsAreaCodeGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAreaCodeGroupComboBox.weightx = 1.0;
			constraintsAreaCodeGroupComboBox.insets = new java.awt.Insets(3, 0, 3, 22);
			getDataCollectionPanel().add(getAlternateGroupComboBox(), constraintsAreaCodeGroupComboBox);

			java.awt.GridBagConstraints constraintsMeterNumberLabel = new java.awt.GridBagConstraints();
			constraintsMeterNumberLabel.gridx = 1; constraintsMeterNumberLabel.gridy = 1;
			constraintsMeterNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMeterNumberLabel.ipadx = 54;
			constraintsMeterNumberLabel.ipady = 2;
			constraintsMeterNumberLabel.insets = new java.awt.Insets(8, 15, 3, 1);
			getDataCollectionPanel().add(getMeterNumberLabel(), constraintsMeterNumberLabel);

			java.awt.GridBagConstraints constraintsMeterNumberTextField = new java.awt.GridBagConstraints();
			constraintsMeterNumberTextField.gridx = 2; constraintsMeterNumberTextField.gridy = 1;
			constraintsMeterNumberTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMeterNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMeterNumberTextField.weightx = 1.0;
			constraintsMeterNumberTextField.ipadx = 196;
			constraintsMeterNumberTextField.insets = new java.awt.Insets(8, 0, 3, 22);
			getDataCollectionPanel().add(getMeterNumberTextField(), constraintsMeterNumberTextField);

			java.awt.GridBagConstraints constraintsJLabelBillingGroup = new java.awt.GridBagConstraints();
			constraintsJLabelBillingGroup.gridx = 1; constraintsJLabelBillingGroup.gridy = 4;
			constraintsJLabelBillingGroup.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelBillingGroup.ipadx = 9;
			constraintsJLabelBillingGroup.ipady = 2;
			constraintsJLabelBillingGroup.insets = new java.awt.Insets(7, 16, 3, 0);
			getDataCollectionPanel().add(getJLabelBillingGroup(), constraintsJLabelBillingGroup);

			java.awt.GridBagConstraints constraintsJComboBoxBillingGroup = new java.awt.GridBagConstraints();
			constraintsJComboBoxBillingGroup.gridx = 2; constraintsJComboBoxBillingGroup.gridy = 4;
			constraintsJComboBoxBillingGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxBillingGroup.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxBillingGroup.weightx = 1.0;
			constraintsJComboBoxBillingGroup.insets = new java.awt.Insets(3, 1, 3, 21);
			getDataCollectionPanel().add(getJComboBoxBillingGroup(), constraintsJComboBoxBillingGroup);
            
            java.awt.GridBagConstraints constraintsCustomGroup1Label = new java.awt.GridBagConstraints();
            constraintsCustomGroup1Label.gridx = 1; constraintsCustomGroup1Label.gridy = 5;
            constraintsCustomGroup1Label.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup1Label.ipadx = 9;
            constraintsCustomGroup1Label.ipady = 2;
            constraintsCustomGroup1Label.insets = new java.awt.Insets(7, 16, 3, 0);
            getDataCollectionPanel().add(getCustomGroup1Label(), constraintsCustomGroup1Label);

            java.awt.GridBagConstraints constraintsCustomGroup1ComboBox = new java.awt.GridBagConstraints();
            constraintsCustomGroup1ComboBox.gridx = 2; constraintsCustomGroup1ComboBox.gridy = 5;
            constraintsCustomGroup1ComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCustomGroup1ComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup1ComboBox.weightx = 1.0;
            constraintsCustomGroup1ComboBox.insets = new java.awt.Insets(3, 1, 3, 21);
            getDataCollectionPanel().add(getCustomGroup1ComboBox(), constraintsCustomGroup1ComboBox);
            
            java.awt.GridBagConstraints constraintsCustomGroup2Label = new java.awt.GridBagConstraints();
            constraintsCustomGroup2Label.gridx = 1; constraintsCustomGroup2Label.gridy = 6;
            constraintsCustomGroup2Label.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup2Label.ipadx = 9;
            constraintsCustomGroup2Label.ipady = 2;
            constraintsCustomGroup2Label.insets = new java.awt.Insets(7, 16, 3, 0);
            getDataCollectionPanel().add(getCustomGroup2Label(), constraintsCustomGroup2Label);

            java.awt.GridBagConstraints constraintsCustomGroup2ComboBox = new java.awt.GridBagConstraints();
            constraintsCustomGroup2ComboBox.gridx = 2; constraintsCustomGroup2ComboBox.gridy = 6;
            constraintsCustomGroup2ComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCustomGroup2ComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup2ComboBox.weightx = 1.0;
            constraintsCustomGroup2ComboBox.insets = new java.awt.Insets(3, 1, 3, 21);
            getDataCollectionPanel().add(getCustomGroup2ComboBox(), constraintsCustomGroup2ComboBox);
            
            java.awt.GridBagConstraints constraintsCustomGroup3Label = new java.awt.GridBagConstraints();
            constraintsCustomGroup3Label.gridx = 1; constraintsCustomGroup3Label.gridy = 7;
            constraintsCustomGroup3Label.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup3Label.ipadx = 9;
            constraintsCustomGroup3Label.ipady = 2;
            constraintsCustomGroup3Label.insets = new java.awt.Insets(7, 16, 3, 0);
            getDataCollectionPanel().add(getCustomGroup3Label(), constraintsCustomGroup3Label);

            java.awt.GridBagConstraints constraintsCustomGroup3ComboBox = new java.awt.GridBagConstraints();
            constraintsCustomGroup3ComboBox.gridx = 2; constraintsCustomGroup3ComboBox.gridy = 7;
            constraintsCustomGroup3ComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsCustomGroup3ComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCustomGroup3ComboBox.weightx = 1.0;
            constraintsCustomGroup3ComboBox.insets = new java.awt.Insets(3, 1, 3, 21);
            getDataCollectionPanel().add(getCustomGroup3ComboBox(), constraintsCustomGroup3ComboBox);
            
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDataCollectionPanel;
}
/**
 * Return the JComboBoxBillingGroup property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxBillingGroup() {
	if (ivjJComboBoxBillingGroup == null) {
		try {
			ivjJComboBoxBillingGroup = new javax.swing.JComboBox();
			ivjJComboBoxBillingGroup.setName("JComboBoxBillingGroup");
			ivjJComboBoxBillingGroup.setPreferredSize(new java.awt.Dimension(200, 25));
			ivjJComboBoxBillingGroup.setEditable(true);
			ivjJComboBoxBillingGroup.setMinimumSize(new java.awt.Dimension(200, 25));
            List<String> availableBillingsGroups = hacker.getGroups(FixedDeviceGroups.BILLINGGROUP);
            Iterator<String> iter = availableBillingsGroups.iterator();
            while(iter.hasNext()) {
                ivjJComboBoxBillingGroup.addItem(iter.next());
            }
         
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxBillingGroup;
}
/**
 * Return the JComboBoxlVoltInterval property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxlVoltInterval() {
	if (ivjJComboBoxlVoltInterval == null) {
		try {
			ivjJComboBoxlVoltInterval = new javax.swing.JComboBox();
			ivjJComboBoxlVoltInterval.setName("JComboBoxlVoltInterval");
			ivjJComboBoxlVoltInterval.setPreferredSize(new java.awt.Dimension(133, 23));
			ivjJComboBoxlVoltInterval.setEnabled(true);
			ivjJComboBoxlVoltInterval.setMinimumSize(new java.awt.Dimension(133, 23));
			// user code begin {1}

			ivjJComboBoxlVoltInterval.addItem("15 second");
			ivjJComboBoxlVoltInterval.addItem("30 second");
			ivjJComboBoxlVoltInterval.addItem("45 second");
			ivjJComboBoxlVoltInterval.addItem("1 minute");
			ivjJComboBoxlVoltInterval.addItem("2 minute");
			ivjJComboBoxlVoltInterval.addItem("3 minute");
			ivjJComboBoxlVoltInterval.addItem("5 minute");
			ivjJComboBoxlVoltInterval.addItem("6 minute");
			ivjJComboBoxlVoltInterval.addItem("8 minute");
			ivjJComboBoxlVoltInterval.addItem("10 minute");
			ivjJComboBoxlVoltInterval.addItem("15 minute");
			ivjJComboBoxlVoltInterval.addItem("30 minute");
			ivjJComboBoxlVoltInterval.addItem("1 hour");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxlVoltInterval;
}
/**
 * Return the JComboBoxlVoltRate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxlVoltRate() {
	if (ivjJComboBoxlVoltRate == null) {
		try {
			ivjJComboBoxlVoltRate = new javax.swing.JComboBox();
			ivjJComboBoxlVoltRate.setName("JComboBoxlVoltRate");
			ivjJComboBoxlVoltRate.setPreferredSize(new java.awt.Dimension(133, 23));
			ivjJComboBoxlVoltRate.setEnabled(true);
			ivjJComboBoxlVoltRate.setMinimumSize(new java.awt.Dimension(133, 23));
			// user code begin {1}
			
			ivjJComboBoxlVoltRate.addItem("1 minute");
			ivjJComboBoxlVoltRate.addItem("2 minute");
			ivjJComboBoxlVoltRate.addItem("3 minute");
			ivjJComboBoxlVoltRate.addItem("5 minute");
			ivjJComboBoxlVoltRate.addItem("10 minute");
			ivjJComboBoxlVoltRate.addItem("15 minute");
			ivjJComboBoxlVoltRate.addItem("30 minute");
			ivjJComboBoxlVoltRate.addItem("1 hour");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxlVoltRate;
}
/**
 * Return the JLabelBillingGroup property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelBillingGroup() {
	if (ivjJLabelBillingGroup == null) {
		try {
			ivjJLabelBillingGroup = new javax.swing.JLabel();
			ivjJLabelBillingGroup.setName("JLabelBillingGroup");
			ivjJLabelBillingGroup.setText("Billing Group:");
			ivjJLabelBillingGroup.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjJLabelBillingGroup.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjJLabelBillingGroup.setPreferredSize(new java.awt.Dimension(140, 16));
			ivjJLabelBillingGroup.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelBillingGroup.setMinimumSize(new java.awt.Dimension(140, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelBillingGroup;
}
/**
 * Return the JLabelVoltDmdRate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelVoltDmdRate() {
	if (ivjJLabelVoltDmdRate == null) {
		try {
			ivjJLabelVoltDmdRate = new javax.swing.JLabel();
			ivjJLabelVoltDmdRate.setName("JLabelVoltDmdRate");
			ivjJLabelVoltDmdRate.setText("Voltage Profile Interval: ");
			ivjJLabelVoltDmdRate.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjJLabelVoltDmdRate.setPreferredSize(new java.awt.Dimension(175, 16));
			ivjJLabelVoltDmdRate.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelVoltDmdRate.setEnabled(true);
			ivjJLabelVoltDmdRate.setMinimumSize(new java.awt.Dimension(150, 16));
			
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelVoltDmdRate;
}
/**
 * Return the JLabelVoltIntervalDmdRate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelVoltIntervalDmdRate() {
	if (ivjJLabelVoltIntervalDmdRate == null) {
		try {
			ivjJLabelVoltIntervalDmdRate = new javax.swing.JLabel();
			ivjJLabelVoltIntervalDmdRate.setName("JLabelVoltIntervalDmdRate");
			ivjJLabelVoltIntervalDmdRate.setText("Voltage Averaging Interval: ");
			ivjJLabelVoltIntervalDmdRate.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjJLabelVoltIntervalDmdRate.setPreferredSize(new java.awt.Dimension(175, 16));
			ivjJLabelVoltIntervalDmdRate.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelVoltIntervalDmdRate.setEnabled(true);
			ivjJLabelVoltIntervalDmdRate.setMinimumSize(new java.awt.Dimension(150, 16));
			
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelVoltIntervalDmdRate;
}
/**
 * Return the DemandIntervalComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getLastIntervalDemandRateComboBox() {
	if (ivjLastIntervalDemandRateComboBox == null) {
		try {
			ivjLastIntervalDemandRateComboBox = new javax.swing.JComboBox();
			ivjLastIntervalDemandRateComboBox.setName("LastIntervalDemandRateComboBox");
			ivjLastIntervalDemandRateComboBox.setEnabled(true);
			// user code begin {1}
         
		 ivjLastIntervalDemandRateComboBox.addItem("1 minute");
		 ivjLastIntervalDemandRateComboBox.addItem("2 minute");
		 ivjLastIntervalDemandRateComboBox.addItem("3 minute");
		 ivjLastIntervalDemandRateComboBox.addItem("5 minute");
		 ivjLastIntervalDemandRateComboBox.addItem("10 minute");
		 ivjLastIntervalDemandRateComboBox.addItem("15 minute");
		 ivjLastIntervalDemandRateComboBox.addItem("30 minute");
		 ivjLastIntervalDemandRateComboBox.addItem("1 hour");
         
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLastIntervalDemandRateComboBox;
}
/**
 * Return the DemandIntervalLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLastIntervalDemandRateLabel() {
	if (ivjLastIntervalDemandRateLabel == null) {
		try {
			ivjLastIntervalDemandRateLabel = new javax.swing.JLabel();
			ivjLastIntervalDemandRateLabel.setName("LastIntervalDemandRateLabel");
			ivjLastIntervalDemandRateLabel.setText("Last Interval Demand Rate:");
			ivjLastIntervalDemandRateLabel.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjLastIntervalDemandRateLabel.setPreferredSize(new java.awt.Dimension(175, 16));
			ivjLastIntervalDemandRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLastIntervalDemandRateLabel.setEnabled(true);
			ivjLastIntervalDemandRateLabel.setMinimumSize(new java.awt.Dimension(150, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLastIntervalDemandRateLabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getLoadProfileCollectionPanel() {
	if (ivjLoadProfileCollectionPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Load Profile Collection");
			ivjLoadProfileCollectionPanel = new javax.swing.JPanel();
			ivjLoadProfileCollectionPanel.setName("LoadProfileCollectionPanel");
			ivjLoadProfileCollectionPanel.setBorder(ivjLocalBorder);
			ivjLoadProfileCollectionPanel.setLayout(new java.awt.GridBagLayout());
			ivjLoadProfileCollectionPanel.setPreferredSize(new java.awt.Dimension(387, 142));
			ivjLoadProfileCollectionPanel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjLoadProfileCollectionPanel.setMinimumSize(new java.awt.Dimension(387, 142));

			java.awt.GridBagConstraints constraintsChannel1CheckBox = new java.awt.GridBagConstraints();
			constraintsChannel1CheckBox.gridx = 1; constraintsChannel1CheckBox.gridy = 3;
			constraintsChannel1CheckBox.insets = new java.awt.Insets(1, 16, 2, 27);
			getLoadProfileCollectionPanel().add(getChannel1CheckBox(), constraintsChannel1CheckBox);

			java.awt.GridBagConstraints constraintsChannel2CheckBox = new java.awt.GridBagConstraints();
			constraintsChannel2CheckBox.gridx = 2; constraintsChannel2CheckBox.gridy = 3;
			constraintsChannel2CheckBox.insets = new java.awt.Insets(1, 6, 2, 38);
			getLoadProfileCollectionPanel().add(getChannel2CheckBox(), constraintsChannel2CheckBox);

			java.awt.GridBagConstraints constraintsChannel3CheckBox = new java.awt.GridBagConstraints();
			constraintsChannel3CheckBox.gridx = 1; constraintsChannel3CheckBox.gridy = 4;
			constraintsChannel3CheckBox.insets = new java.awt.Insets(2, 16, 15, 27);
			getLoadProfileCollectionPanel().add(getChannel3CheckBox(), constraintsChannel3CheckBox);

			java.awt.GridBagConstraints constraintsChannel4CheckBox = new java.awt.GridBagConstraints();
			constraintsChannel4CheckBox.gridx = 2; constraintsChannel4CheckBox.gridy = 4;
			constraintsChannel4CheckBox.insets = new java.awt.Insets(2, 6, 15, 38);
			getLoadProfileCollectionPanel().add(getChannel4CheckBox(), constraintsChannel4CheckBox);

			java.awt.GridBagConstraints constraintsLoadProfileDemandRateLabel = new java.awt.GridBagConstraints();
			constraintsLoadProfileDemandRateLabel.gridx = 1; constraintsLoadProfileDemandRateLabel.gridy = 1;
			constraintsLoadProfileDemandRateLabel.ipadx = 25;
			constraintsLoadProfileDemandRateLabel.insets = new java.awt.Insets(17, 14, 7, 4);
			getLoadProfileCollectionPanel().add(getLoadProfileDemandRateLabel(), constraintsLoadProfileDemandRateLabel);

			java.awt.GridBagConstraints constraintsLoadProfileDemandRateComboBox = new java.awt.GridBagConstraints();
			constraintsLoadProfileDemandRateComboBox.gridx = 2; constraintsLoadProfileDemandRateComboBox.gridy = 1;
			constraintsLoadProfileDemandRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsLoadProfileDemandRateComboBox.weightx = 1.0;
			constraintsLoadProfileDemandRateComboBox.ipadx = -10;
			constraintsLoadProfileDemandRateComboBox.insets = new java.awt.Insets(16, 4, 1, 67);
			getLoadProfileCollectionPanel().add(getLoadProfileDemandRateComboBox(), constraintsLoadProfileDemandRateComboBox);

			java.awt.GridBagConstraints constraintsJLabelVoltDmdRate = new java.awt.GridBagConstraints();
			constraintsJLabelVoltDmdRate.gridx = 1; constraintsJLabelVoltDmdRate.gridy = 2;
			constraintsJLabelVoltDmdRate.ipadx = 25;
			constraintsJLabelVoltDmdRate.ipady = 6;
			constraintsJLabelVoltDmdRate.insets = new java.awt.Insets(2, 14, 4, 4);
			getLoadProfileCollectionPanel().add(getJLabelVoltDmdRate(), constraintsJLabelVoltDmdRate);

			java.awt.GridBagConstraints constraintsJComboBoxlVoltRate = new java.awt.GridBagConstraints();
			constraintsJComboBoxlVoltRate.gridx = 2; constraintsJComboBoxlVoltRate.gridy = 2;
			constraintsJComboBoxlVoltRate.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxlVoltRate.weightx = 1.0;
			constraintsJComboBoxlVoltRate.ipadx = -10;
			constraintsJComboBoxlVoltRate.insets = new java.awt.Insets(4, 4, 1, 67);
			getLoadProfileCollectionPanel().add(getJComboBoxlVoltRate(), constraintsJComboBoxlVoltRate);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileCollectionPanel;
}
/**
 * Return the LoadProfileDemandRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getLoadProfileDemandRateComboBox() {
	if (ivjLoadProfileDemandRateComboBox == null) {
		try {
			ivjLoadProfileDemandRateComboBox = new javax.swing.JComboBox();
			ivjLoadProfileDemandRateComboBox.setName("LoadProfileDemandRateComboBox");
			ivjLoadProfileDemandRateComboBox.setPreferredSize(new java.awt.Dimension(133, 23));
			ivjLoadProfileDemandRateComboBox.setEnabled(true);
			ivjLoadProfileDemandRateComboBox.setMinimumSize(new java.awt.Dimension(133, 23));
			// user code begin {1}
         
		 ivjLoadProfileDemandRateComboBox.addItem("5 minute");
		 ivjLoadProfileDemandRateComboBox.addItem("15 minute");
		 ivjLoadProfileDemandRateComboBox.addItem("30 minute");
		 ivjLoadProfileDemandRateComboBox.addItem("1 hour");
         
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileDemandRateComboBox;
}
/**
 * Return the LoadProfileDemandRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getLoadProfileDemandRateLabel() {
	if (ivjLoadProfileDemandRateLabel == null) {
		try {
			ivjLoadProfileDemandRateLabel = new javax.swing.JLabel();
			ivjLoadProfileDemandRateLabel.setName("LoadProfileDemandRateLabel");
			ivjLoadProfileDemandRateLabel.setText("Load Profile Demand Rate:");
			ivjLoadProfileDemandRateLabel.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjLoadProfileDemandRateLabel.setPreferredSize(new java.awt.Dimension(175, 16));
			ivjLoadProfileDemandRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjLoadProfileDemandRateLabel.setEnabled(true);
			ivjLoadProfileDemandRateLabel.setMinimumSize(new java.awt.Dimension(150, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileDemandRateLabel;
}
/**
 * Return the MeterNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMeterNumberLabel() {
	if (ivjMeterNumberLabel == null) {
		try {
			ivjMeterNumberLabel = new javax.swing.JLabel();
			ivjMeterNumberLabel.setName("MeterNumberLabel");
			ivjMeterNumberLabel.setText("Meter Number:");
			ivjMeterNumberLabel.setMaximumSize(new java.awt.Dimension(200, 16));
			ivjMeterNumberLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjMeterNumberLabel.setPreferredSize(new java.awt.Dimension(100, 16));
			ivjMeterNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMeterNumberLabel.setMinimumSize(new java.awt.Dimension(95, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterNumberLabel;
}
/**
 * Return the MeterNumberTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMeterNumberTextField()
{
	if (ivjMeterNumberTextField == null)
	{
		try
		{
			ivjMeterNumberTextField = new javax.swing.JTextField();
			ivjMeterNumberTextField.setName("MeterNumberTextField");
			// user code begin {1}
			ivjMeterNumberTextField.setDocument(
				new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_METER_NUMBER_LENGTH));

			// user code end
		}
		catch (java.lang.Throwable ivjExc)
		{
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterNumberTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@SuppressWarnings("deprecation")
public Object getValue(Object val) 
{

	//The default object is either a MCT or a IEDmeter
	if( val instanceof MCTBase || val instanceof IEDMeter )
	{
		DeviceLoadProfile dlp = null;
		
		if( val instanceof MCTBase )
		{
			dlp = ((MCTBase) val).getDeviceLoadProfile();
		}		
		else if( val instanceof IEDMeter )
		{
			dlp = ((IEDMeter) val).getDeviceLoadProfile();
		}
				
		dlp.setLastIntervalDemandRate(
		 CtiUtilities.getIntervalComboBoxSecondsValue(getLastIntervalDemandRateComboBox()) );

		if( getLoadProfileCollectionPanel().isVisible() )
		{
		 dlp.setLoadProfileDemandRate(
			CtiUtilities.getIntervalComboBoxSecondsValue(getLoadProfileDemandRateComboBox()) );

			StringBuffer loadProfileCollection = new StringBuffer();
			if( getChannel1CheckBox().isSelected() )
				loadProfileCollection.append("Y");
			else
				loadProfileCollection.append("N");
				
			if( getChannel2CheckBox().isSelected() )
				loadProfileCollection.append("Y");
			else
				loadProfileCollection.append("N");
            

			if( getChannel3CheckBox().isSelected() )
				loadProfileCollection.append("Y");
			else
				loadProfileCollection.append("N");
				
			if( getChannel4CheckBox().isSelected() )
				loadProfileCollection.append("Y");
			else
				loadProfileCollection.append("N");

			dlp.setLoadProfileCollection(loadProfileCollection.toString());

			dlp.setVoltageDmdInterval(
				CtiUtilities.getIntervalComboBoxSecondsValue(getJComboBoxlVoltInterval()) );

			dlp.setVoltageDmdRate(
				CtiUtilities.getIntervalComboBoxSecondsValue(getJComboBoxlVoltRate()) );

		}
		
	}


	//handle the devicemetergroup data below
	DeviceMeterGroup dmg = ((IDeviceMeterGroup)val).getDeviceMeterGroup();

	String cycleGroup = getCycleGroupComboBox().getSelectedItem().toString();
	String alternateGroup = getAlternateGroupComboBox().getSelectedItem().toString();
	String meterNumber = getMeterNumberTextField().getText();
	String billingGroup = getJComboBoxBillingGroup().getSelectedItem().toString();
    String customGroup1 = getCustomGroup1ComboBox().getSelectedItem().toString();
    String customGroup2 = getCustomGroup2ComboBox().getSelectedItem().toString();
    String customGroup3 = getCustomGroup3ComboBox().getSelectedItem().toString();
    
    LiteYukonPAObject liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((MCTBase) val).getPAObjectID());
    YukonDevice yukonDevice = DaoFactory.getDeviceDao().getYukonDevice(liteYuk);
    hacker.setGroup(FixedDeviceGroups.BILLINGGROUP, yukonDevice, billingGroup);
    hacker.setGroup(FixedDeviceGroups.COLLECTIONGROUP, yukonDevice, cycleGroup);
    hacker.setGroup(FixedDeviceGroups.TESTCOLLECTIONGROUP, yukonDevice, alternateGroup);
    hacker.setGroup(FixedDeviceGroups.CUSTOM1GROUP, yukonDevice, customGroup1);
    hacker.setGroup(FixedDeviceGroups.CUSTOM2GROUP, yukonDevice, customGroup2);
    hacker.setGroup(FixedDeviceGroups.CUSTOM3GROUP, yukonDevice, customGroup3);

    if( meterNumber != null && meterNumber.length() > 0 ) {
	    dmg.setMeterNumber( meterNumber );
    }
	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getLastIntervalDemandRateComboBox().addItemListener(ivjEventHandler);
	getCycleGroupComboBox().addActionListener(ivjEventHandler);
	getAlternateGroupComboBox().addActionListener(ivjEventHandler);
	getChannel2CheckBox().addActionListener(ivjEventHandler);
	getChannel1CheckBox().addActionListener(ivjEventHandler);
	getChannel3CheckBox().addActionListener(ivjEventHandler);
	getChannel4CheckBox().addActionListener(ivjEventHandler);
	getLoadProfileDemandRateComboBox().addActionListener(ivjEventHandler);
	getCycleGroupComboBox().addKeyListener(ivjEventHandler);
	getAlternateGroupComboBox().addKeyListener(ivjEventHandler);
	getMeterNumberTextField().addCaretListener(ivjEventHandler);
	getJComboBoxBillingGroup().addActionListener(ivjEventHandler);
	getJComboBoxlVoltInterval().addActionListener(ivjEventHandler);
	getJComboBoxlVoltRate().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceMeterGroupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(411, 375);

		java.awt.GridBagConstraints constraintsLastIntervalDemandRateComboBox = new java.awt.GridBagConstraints();
		constraintsLastIntervalDemandRateComboBox.gridx = 2; constraintsLastIntervalDemandRateComboBox.gridy = 2;
		constraintsLastIntervalDemandRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsLastIntervalDemandRateComboBox.weightx = 1.0;
		constraintsLastIntervalDemandRateComboBox.ipadx = 7;
		constraintsLastIntervalDemandRateComboBox.insets = new java.awt.Insets(4, 2, 2, 81);
		add(getLastIntervalDemandRateComboBox(), constraintsLastIntervalDemandRateComboBox);

		java.awt.GridBagConstraints constraintsLastIntervalDemandRateLabel = new java.awt.GridBagConstraints();
		constraintsLastIntervalDemandRateLabel.gridx = 1; constraintsLastIntervalDemandRateLabel.gridy = 2;
		constraintsLastIntervalDemandRateLabel.ipadx = 25;
		constraintsLastIntervalDemandRateLabel.insets = new java.awt.Insets(7, 17, 6, 3);
		add(getLastIntervalDemandRateLabel(), constraintsLastIntervalDemandRateLabel);

		java.awt.GridBagConstraints constraintsLoadProfileCollectionPanel = new java.awt.GridBagConstraints();
		constraintsLoadProfileCollectionPanel.gridx = 1; constraintsLoadProfileCollectionPanel.gridy = 4;
		constraintsLoadProfileCollectionPanel.gridwidth = 2;
		constraintsLoadProfileCollectionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLoadProfileCollectionPanel.weightx = 1.0;
		constraintsLoadProfileCollectionPanel.weighty = 1.0;
		constraintsLoadProfileCollectionPanel.insets = new java.awt.Insets(5, 12, 10, 12);
		add(getLoadProfileCollectionPanel(), constraintsLoadProfileCollectionPanel);

		java.awt.GridBagConstraints constraintsDataCollectionPanel = new java.awt.GridBagConstraints();
		constraintsDataCollectionPanel.gridx = 1; constraintsDataCollectionPanel.gridy = 1;
		constraintsDataCollectionPanel.gridwidth = 2;
		constraintsDataCollectionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDataCollectionPanel.weightx = 1.0;
		constraintsDataCollectionPanel.weighty = 1.0;
		constraintsDataCollectionPanel.ipadx = -10;
		constraintsDataCollectionPanel.ipady = -17;
		constraintsDataCollectionPanel.insets = new java.awt.Insets(6, 12, 3, 12);
		add(getDataCollectionPanel(), constraintsDataCollectionPanel);

		java.awt.GridBagConstraints constraintsJLabelVoltIntervalDmdRate = new java.awt.GridBagConstraints();
		constraintsJLabelVoltIntervalDmdRate.gridx = 1; constraintsJLabelVoltIntervalDmdRate.gridy = 3;
		constraintsJLabelVoltIntervalDmdRate.ipadx = 26;
		constraintsJLabelVoltIntervalDmdRate.ipady = 4;
		constraintsJLabelVoltIntervalDmdRate.insets = new java.awt.Insets(4, 17, 6, 2);
		add(getJLabelVoltIntervalDmdRate(), constraintsJLabelVoltIntervalDmdRate);

		java.awt.GridBagConstraints constraintsJComboBoxlVoltInterval = new java.awt.GridBagConstraints();
		constraintsJComboBoxlVoltInterval.gridx = 2; constraintsJComboBoxlVoltInterval.gridy = 3;
		constraintsJComboBoxlVoltInterval.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxlVoltInterval.weightx = 1.0;
		constraintsJComboBoxlVoltInterval.insets = new java.awt.Insets(3, 2, 4, 81);
		add(getJComboBoxlVoltInterval(), constraintsJComboBoxlVoltInterval);
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
/*   if( getJCheckBoxScanWindow().isSelected() 
	   && (getJTextFieldClose().getTimeTotalSeconds().intValue()
		   == getJTextFieldOpen().getTimeTotalSeconds().intValue()) )
   {
	  setErrorString("The Device Window open and close values can not be equal");
	  return false;
   }
*/
	/*if(is410 && getMeterNumberTextField().getText().length() < 7)
	{
	   setErrorString("An MCT410 requires a meter number with seven digits");
	   return false;
	}*/
    return this.checkMeterNumber(getMeterNumberTextField().getText());
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getLastIntervalDemandRateComboBox()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyPressed(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyReleased(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyTyped(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getCycleGroupComboBox()) 
		connEtoC10(e);
	if (e.getSource() == getAlternateGroupComboBox()) 
		connEtoC11(e);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		DeviceMeterGroupEditorPanel aDeviceMeterGroupEditorPanel;
		aDeviceMeterGroupEditorPanel = new DeviceMeterGroupEditorPanel();
		frame.add("Center", aDeviceMeterGroupEditorPanel);
		frame.setSize(aDeviceMeterGroupEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@SuppressWarnings("deprecation")
public void setValue(Object val) 
{
	int deviceType = PAOGroups.getDeviceType( ((DeviceBase)val).getPAOType() );

	getLastIntervalDemandRateLabel().setVisible( val instanceof MCTBase );
	getLastIntervalDemandRateComboBox().setVisible( val instanceof MCTBase );
	getLoadProfileCollectionPanel().setVisible( val instanceof MCTBase );
		
	//are we a voltage channel?
	getJComboBoxlVoltInterval().setVisible( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) );
	getJComboBoxlVoltRate().setVisible( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) );		
	getJLabelVoltDmdRate().setVisible( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) );
	getJLabelVoltIntervalDmdRate().setVisible( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) );

	//The default object is either a MCT or a IEDmeter
	if( val instanceof MCTBase )
	{
		getLastIntervalDemandRateLabel().setVisible(true);
		getLastIntervalDemandRateComboBox().setVisible(true);
		getLoadProfileCollectionPanel().setVisible(true);


		DeviceLoadProfile dlp = ((MCTBase)val).getDeviceLoadProfile();

		String loadProfileCollection = dlp.getLoadProfileCollection();

		CtiUtilities.setIntervalComboBoxSelectedItem(
			getLastIntervalDemandRateComboBox(), dlp.getLastIntervalDemandRate().intValue() );

		CtiUtilities.setIntervalComboBoxSelectedItem(
			getJComboBoxlVoltInterval(), dlp.getVoltageDmdInterval().intValue() );

		if( deviceType == PAOGroups.DCT_501 
			 || deviceType == PAOGroups.LMT_2 )
		{
			//the last interval demand rate can not be edited for DCT_501 & LMT-2
			getLastIntervalDemandRateComboBox().setVisible(false);
			getLastIntervalDemandRateLabel().setVisible(false);
		}
		
		if(deviceType == PAOGroups.MCT410IL
			|| deviceType == PAOGroups.MCT410CL
            || deviceType == PAOGroups.MCT430A
            || deviceType == PAOGroups.MCT430S4
			|| deviceType == PAOGroups.MCT470)
		{
			getChannel2CheckBox().setEnabled(true);
			getChannel3CheckBox().setEnabled(true);
			
            if(deviceType == PAOGroups.MCT470)
            {
                getLoadProfileDemandRateLabel().setText("Load Profile Rate #1: ");
                getJLabelVoltDmdRate().setText("Load Profile Rate #2: ");
                getJLabelVoltIntervalDmdRate().setEnabled(false);
                getJLabelVoltIntervalDmdRate().setVisible(false);
                getJComboBoxlVoltInterval().setEnabled(false);
                getJComboBoxlVoltInterval().setVisible(false);
                
            }else
            {
                getJLabelVoltDmdRate().setText("Voltage Profile Demand Rate: ");
            }
			
			getJComboBoxlVoltRate().removeAllItems();
			getJComboBoxlVoltRate().addItem("5 minute");
			getJComboBoxlVoltRate().addItem("15 minute");
			getJComboBoxlVoltRate().addItem("30 minute");
			getJComboBoxlVoltRate().addItem("1 hour");
			
			CtiUtilities.setCheckBoxState(getChannel1CheckBox(),new Character(loadProfileCollection.charAt(0)));
		}
		
		CtiUtilities.setIntervalComboBoxSelectedItem(
			getJComboBoxlVoltRate(), dlp.getVoltageDmdRate().intValue() );
      	
		CtiUtilities.setIntervalComboBoxSelectedItem(
			getLoadProfileDemandRateComboBox(), dlp.getLoadProfileDemandRate().intValue() );


		if( DeviceTypesFuncs.isLoadProfile1Channel(deviceType) )
		{
			CtiUtilities.setCheckBoxState(getChannel1CheckBox(),new Character(loadProfileCollection.charAt(0)));
			getChannel2CheckBox().setVisible(false);
			getChannel3CheckBox().setVisible(false);
			getChannel4CheckBox().setVisible(false);
		}
	  else if( DeviceTypesFuncs.isLoadProfile3Channel(deviceType) )
	  {
		 CtiUtilities.setCheckBoxState(getChannel1CheckBox(), new Character(loadProfileCollection.charAt(0)));
		 CtiUtilities.setCheckBoxState(getChannel2CheckBox(), new Character(loadProfileCollection.charAt(1)));
		 CtiUtilities.setCheckBoxState(getChannel3CheckBox(), new Character(loadProfileCollection.charAt(2)));
		 getChannel4CheckBox().setVisible(false);
	  }
      
	  else if( deviceType == PAOGroups.MCT470 || deviceType == PAOGroups.MCT430A || deviceType == PAOGroups.MCT430S4)
	  {
	  	getChannel2CheckBox().setEnabled(true);
		getChannel3CheckBox().setEnabled(true);	
		getChannel4CheckBox().setEnabled(true);
		CtiUtilities.setCheckBoxState(getChannel1CheckBox(), new Character(loadProfileCollection.charAt(0)));
		CtiUtilities.setCheckBoxState(getChannel2CheckBox(), new Character(loadProfileCollection.charAt(1)));
		CtiUtilities.setCheckBoxState(getChannel3CheckBox(), new Character(loadProfileCollection.charAt(2)));
		CtiUtilities.setCheckBoxState(getChannel4CheckBox(), new Character(loadProfileCollection.charAt(3)));
	  }
		else if( DeviceTypesFuncs.isLoadProfile4Channel(deviceType) )
		{
			CtiUtilities.setCheckBoxState(getChannel1CheckBox(), new Character(loadProfileCollection.charAt(0)));
			CtiUtilities.setCheckBoxState(getChannel2CheckBox(), new Character(loadProfileCollection.charAt(1)));
			CtiUtilities.setCheckBoxState(getChannel3CheckBox(), new Character(loadProfileCollection.charAt(2)));
			CtiUtilities.setCheckBoxState(getChannel4CheckBox(), new Character(loadProfileCollection.charAt(3)));
			
			if( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) )
			{
				getChannel4CheckBox().setText("Channel #4 (Volts)");
			}
		}
	   else //must not have load profile capabilities
	  {
		 //setting invisible lays out the JPanel in a Funny way, just remove the comps
		 getLoadProfileCollectionPanel().removeAll();
		 getLoadProfileCollectionPanel().setBorder( null );
	  }

	}



	//handle the DeviceMeterGroup data below
	DeviceMeterGroup dmg = 
			((IDeviceMeterGroup)val).getDeviceMeterGroup();
	
	getMeterNumberTextField().setText( dmg.getMeterNumber() );
    
    LiteYukonPAObject liteYuk = liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(((MCTBase) val).getPAObjectID());
    YukonDevice yukonDevice = DaoFactory.getDeviceDao().getYukonDevice(liteYuk);
    String billingGroup = hacker.getGroupForDevice(FixedDeviceGroups.BILLINGGROUP, yukonDevice);
    String alternateGroup = hacker.getGroupForDevice(FixedDeviceGroups.TESTCOLLECTIONGROUP, yukonDevice);
    String collectionGroup = hacker.getGroupForDevice(FixedDeviceGroups.COLLECTIONGROUP, yukonDevice);
    String customGroup1 = hacker.getGroupForDevice(FixedDeviceGroups.CUSTOM1GROUP, yukonDevice);
    String customGroup2 = hacker.getGroupForDevice(FixedDeviceGroups.CUSTOM2GROUP, yukonDevice);
    String customGroup3 = hacker.getGroupForDevice(FixedDeviceGroups.CUSTOM3GROUP, yukonDevice);
	getCycleGroupComboBox().setSelectedItem( collectionGroup );
	getAlternateGroupComboBox().setSelectedItem( alternateGroup );
	getJComboBoxBillingGroup().setSelectedItem( billingGroup );
    getCustomGroup1ComboBox().setSelectedItem(customGroup1);
    getCustomGroup2ComboBox().setSelectedItem(customGroup2);
    getCustomGroup3ComboBox().setSelectedItem(customGroup3);
}
/**
 * Helper method to check meternumber uniqueness
 * @param meterNumber - Meternumber to check
 * @return True if meternumber is unique
 */
private boolean checkMeterNumber( String meterNumber )
{
    int deviceId = ((YukonPAObject) ((DeviceEditorPanel) this.getParent().getParent()).getOriginalObjectToEdit()).getPAObjectID();
    List<String> devices = DeviceMeterGroup.checkMeterNumber(meterNumber, deviceId);

    if (devices.size() > 0) {
        StringBuffer deviceNames = new StringBuffer();
        for (String deviceName : devices) {
            deviceNames.append("          " + deviceName + "\n");
        }

        int response = JOptionPane.showConfirmDialog(this,
                                                     "The meternumber '"
                                                         + meterNumber
                                                         + "' is already used by the following devices,\n"
                                                         + "are you sure you want to use it again?\n"
                                                         + deviceNames.toString(),
                                                     "Meternumber Already Used",
                                                     JOptionPane.YES_NO_OPTION,
                                                     JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.NO_OPTION) {
            setErrorString(null);
            return false;
        }
    }

    return true;
}
}

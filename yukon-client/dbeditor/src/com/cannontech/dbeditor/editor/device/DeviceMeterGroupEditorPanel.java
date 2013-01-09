package com.cannontech.dbeditor.editor.device;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.groups.service.FixedDeviceGroupingHack;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.device.groups.util.DeviceGroupUtil;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.IDeviceMeterGroup;
import com.cannontech.database.data.device.IEDMeter;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.TwoWayLCR;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.device.DeviceLoadProfile;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */
public class DeviceMeterGroupEditorPanel extends DataInputPanel implements ActionListener, ItemListener, KeyListener, CaretListener {
	private javax.swing.JLabel alternateGroupLabel = null;
	private javax.swing.JLabel ivjCycleGroupLabel = null;
	private JTextArea customGroupsDirectionsLabel = null;
	private javax.swing.JComboBox alternateGroupComboBox = null;
	private javax.swing.JComboBox ivjCycleGroupComboBox = null;
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
    @SuppressWarnings("deprecation")
    FixedDeviceGroupingHack hacker = (FixedDeviceGroupingHack) YukonSpringHook.getBean("fixedDeviceGroupingHack");  

/**
 * Constructor
 */
public DeviceMeterGroupEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getCycleGroupComboBox() || 
	    e.getSource() == getAlternateGroupComboBox() || 
	    e.getSource() == getChannel2CheckBox() ||
	    e.getSource() == getChannel1CheckBox() ||
	    e.getSource() == getChannel3CheckBox() ||
	    e.getSource() == getChannel4CheckBox() ||
	    e.getSource() == getLoadProfileDemandRateComboBox() || 
	    e.getSource() == getJComboBoxBillingGroup() || 
	    e.getSource() == getJComboBoxlVoltInterval() || 
	    e.getSource() == getJComboBoxlVoltRate() 
	    ) 
	{
	    fireInputUpdate();
    }
}

/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
public void itemStateChanged(java.awt.event.ItemEvent e) {
    if (e.getSource() == getLastIntervalDemandRateComboBox()) {
        fireInputUpdate();
    }
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
public void keyPressed(java.awt.event.KeyEvent e) {
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
public void keyReleased(java.awt.event.KeyEvent e) {
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
public void keyTyped(java.awt.event.KeyEvent e) {
    if (e.getSource() == getCycleGroupComboBox().getEditor().getEditorComponent() ||
            e.getSource() == getAlternateGroupComboBox().getEditor().getEditorComponent() ||
            e.getSource() == getJComboBoxBillingGroup().getEditor().getEditorComponent() ||
            e.getSource() == getMeterNumberTextField()){
      fireInputUpdate();
    }
}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) {
    if (e.getSource() == getMeterNumberTextField()) {
        fireInputUpdate();
    }
}

/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception {
    getLastIntervalDemandRateComboBox().addItemListener(this);
    
    getJComboBoxlVoltInterval().addActionListener(this);
    getJComboBoxlVoltRate().addActionListener(this);
    getChannel2CheckBox().addActionListener(this);
    getChannel1CheckBox().addActionListener(this);
    getChannel3CheckBox().addActionListener(this);
    getChannel4CheckBox().addActionListener(this);
    getLoadProfileDemandRateComboBox().addActionListener(this);
    
    getCycleGroupComboBox().addActionListener(this);
    getAlternateGroupComboBox().addActionListener(this);
    getJComboBoxBillingGroup().addActionListener(this);

    getCycleGroupComboBox().getEditor().getEditorComponent().addKeyListener(this);
    getAlternateGroupComboBox().getEditor().getEditorComponent().addKeyListener(this);
    getJComboBoxBillingGroup().getEditor().getEditorComponent().addKeyListener(this);
    getMeterNumberTextField().addKeyListener(this);
    
}

/**
 * Return the AreaCodeGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
@SuppressWarnings("deprecation")
private javax.swing.JComboBox getAlternateGroupComboBox() {
	if (alternateGroupComboBox == null) {
		try {
			alternateGroupComboBox = new javax.swing.JComboBox();
			alternateGroupComboBox.setName("AreaCodeGroupComboBox");
			alternateGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
			alternateGroupComboBox.setEditable(true);
			alternateGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
			
			JTextComponent tc = (JTextComponent)alternateGroupComboBox.getEditor().getEditorComponent();
            tc.setDocument(new TextFieldDocument(-1, DeviceGroupUtil.ILLEGAL_NAME_CHARS));
			
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


/**
 * Return the AreaCodeGroupLabel property value.
 * @return javax.swing.JLabel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return alternateGroupLabel;
}

private JTextArea getCustomGroupsDirectionsLabel() {
if (customGroupsDirectionsLabel == null) {
  try {
      customGroupsDirectionsLabel = new javax.swing.JTextArea();
      customGroupsDirectionsLabel.setName("CustomGroupsDirections");
      customGroupsDirectionsLabel.setText("* To further manage the groupings for this device, please log into the Yukon Web operations client. Once there, enter the Metering section and proceed to the Device Details page to update the device's assigned groups.");
      customGroupsDirectionsLabel.setFont(new Font("dialog", 0, 13));
      customGroupsDirectionsLabel.setLineWrap( true );
      customGroupsDirectionsLabel.setWrapStyleWord( true );
      customGroupsDirectionsLabel.setBackground( null );
  } catch (java.lang.Throwable ivjExc) {
      handleException(ivjExc);
  }
}
return customGroupsDirectionsLabel;
}

/**
 * Return the Channel1CheckBox property value.
 * @return javax.swing.JCheckBox
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjChannel1CheckBox;
}
/**
 * Return the Channel2CheckBox property value.
 * @return javax.swing.JCheckBox
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjChannel2CheckBox;
}
/**
 * Return the Channel3CheckBox property value.
 * @return javax.swing.JCheckBox
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjChannel3CheckBox;
}
/**
 * Return the Channel4CheckBox property value.
 * @return javax.swing.JCheckBox
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjChannel4CheckBox;
}
/**
 * Return the CycleGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
@SuppressWarnings("deprecation")
private javax.swing.JComboBox getCycleGroupComboBox() {
	if (ivjCycleGroupComboBox == null) {
		try {
			ivjCycleGroupComboBox = new javax.swing.JComboBox();
			ivjCycleGroupComboBox.setName("CycleGroupComboBox");
			ivjCycleGroupComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
			ivjCycleGroupComboBox.setEditable(true);
			ivjCycleGroupComboBox.setMinimumSize(new java.awt.Dimension(200, 25));
			
			JTextComponent tc = (JTextComponent)ivjCycleGroupComboBox.getEditor().getEditorComponent();
            tc.setDocument(new TextFieldDocument(-1, DeviceGroupUtil.ILLEGAL_NAME_CHARS));
			
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjCycleGroupLabel;
}
/**
 * Return the DataCollectionPanel property value.
 * @return javax.swing.JPanel
 */
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
            
			java.awt.GridBagConstraints constraintsCustomGroupsDirectionsLabel = new java.awt.GridBagConstraints();
			constraintsCustomGroupsDirectionsLabel.gridx = 1; constraintsCustomGroupsDirectionsLabel.gridy = 5;
			constraintsCustomGroupsDirectionsLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsCustomGroupsDirectionsLabel.anchor = java.awt.GridBagConstraints.CENTER;
			constraintsCustomGroupsDirectionsLabel.gridwidth  = 2;
			constraintsCustomGroupsDirectionsLabel.ipadx = 2;
			constraintsCustomGroupsDirectionsLabel.ipady = 2;
			constraintsCustomGroupsDirectionsLabel.insets = new java.awt.Insets(7, 0, 3, 0);
			getDataCollectionPanel().add(getCustomGroupsDirectionsLabel(), constraintsCustomGroupsDirectionsLabel);
            
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
@SuppressWarnings("deprecation")
private javax.swing.JComboBox getJComboBoxBillingGroup() {
	if (ivjJComboBoxBillingGroup == null) {
		try {
			ivjJComboBoxBillingGroup = new javax.swing.JComboBox();
			ivjJComboBoxBillingGroup.setName("JComboBoxBillingGroup");
			ivjJComboBoxBillingGroup.setPreferredSize(new java.awt.Dimension(200, 25));
			ivjJComboBoxBillingGroup.setEditable(true);
			ivjJComboBoxBillingGroup.setMinimumSize(new java.awt.Dimension(200, 25));
			
			JTextComponent tc = (JTextComponent)ivjJComboBoxBillingGroup.getEditor().getEditorComponent();
			tc.setDocument(new TextFieldDocument(-1, DeviceGroupUtil.ILLEGAL_NAME_CHARS));
			
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
private javax.swing.JComboBox getJComboBoxlVoltInterval() {
	if (ivjJComboBoxlVoltInterval == null) {
		try {
			ivjJComboBoxlVoltInterval = new javax.swing.JComboBox();
			ivjJComboBoxlVoltInterval.setName("JComboBoxlVoltInterval");
			ivjJComboBoxlVoltInterval.setPreferredSize(new java.awt.Dimension(133, 23));
			ivjJComboBoxlVoltInterval.setEnabled(true);
			ivjJComboBoxlVoltInterval.setMinimumSize(new java.awt.Dimension(133, 23));
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxlVoltInterval;
}
/**
 * Return the JComboBoxlVoltRate property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getJComboBoxlVoltRate() {
	if (ivjJComboBoxlVoltRate == null) {
		try {
			ivjJComboBoxlVoltRate = new javax.swing.JComboBox();
			ivjJComboBoxlVoltRate.setName("JComboBoxlVoltRate");
			ivjJComboBoxlVoltRate.setPreferredSize(new java.awt.Dimension(133, 23));
			ivjJComboBoxlVoltRate.setEnabled(true);
			ivjJComboBoxlVoltRate.setMinimumSize(new java.awt.Dimension(133, 23));
			ivjJComboBoxlVoltRate.addItem("1 minute");
			ivjJComboBoxlVoltRate.addItem("2 minute");
			ivjJComboBoxlVoltRate.addItem("3 minute");
			ivjJComboBoxlVoltRate.addItem("5 minute");
			ivjJComboBoxlVoltRate.addItem("10 minute");
			ivjJComboBoxlVoltRate.addItem("15 minute");
			ivjJComboBoxlVoltRate.addItem("30 minute");
			ivjJComboBoxlVoltRate.addItem("1 hour");

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxlVoltRate;
}
/**
 * Return the JLabelBillingGroup property value.
 * @return javax.swing.JLabel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelBillingGroup;
}
/**
 * Return the JLabelVoltDmdRate property value.
 * @return javax.swing.JLabel
 */
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
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelVoltDmdRate;
}
/**
 * Return the JLabelVoltIntervalDmdRate property value.
 * @return javax.swing.JLabel
 */
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
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelVoltIntervalDmdRate;
}
/**
 * Return the DemandIntervalComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getLastIntervalDemandRateComboBox() {
	if (ivjLastIntervalDemandRateComboBox == null) {
		try {
			ivjLastIntervalDemandRateComboBox = new javax.swing.JComboBox();
			ivjLastIntervalDemandRateComboBox.setName("LastIntervalDemandRateComboBox");
			ivjLastIntervalDemandRateComboBox.setEnabled(true);
    		ivjLastIntervalDemandRateComboBox.addItem("1 minute");
    		ivjLastIntervalDemandRateComboBox.addItem("2 minute");
    		ivjLastIntervalDemandRateComboBox.addItem("3 minute");
    		ivjLastIntervalDemandRateComboBox.addItem("5 minute");
    		ivjLastIntervalDemandRateComboBox.addItem("10 minute");
    		ivjLastIntervalDemandRateComboBox.addItem("15 minute");
    		ivjLastIntervalDemandRateComboBox.addItem("30 minute");
    		ivjLastIntervalDemandRateComboBox.addItem("1 hour");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLastIntervalDemandRateComboBox;
}
/**
 * Return the DemandIntervalLabel property value.
 * @return javax.swing.JLabel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLastIntervalDemandRateLabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileCollectionPanel;
}
/**
 * Return the LoadProfileDemandRateComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getLoadProfileDemandRateComboBox() {
	if (ivjLoadProfileDemandRateComboBox == null) {
		try {
			ivjLoadProfileDemandRateComboBox = new javax.swing.JComboBox();
			ivjLoadProfileDemandRateComboBox.setName("LoadProfileDemandRateComboBox");
			ivjLoadProfileDemandRateComboBox.setPreferredSize(new java.awt.Dimension(133, 23));
			ivjLoadProfileDemandRateComboBox.setEnabled(true);
			ivjLoadProfileDemandRateComboBox.setMinimumSize(new java.awt.Dimension(133, 23));
    		ivjLoadProfileDemandRateComboBox.addItem("5 minute");
    		ivjLoadProfileDemandRateComboBox.addItem("15 minute");
    		ivjLoadProfileDemandRateComboBox.addItem("30 minute");
    		ivjLoadProfileDemandRateComboBox.addItem("1 hour");
         
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileDemandRateComboBox;
}
/**
 * Return the LoadProfileDemandRateLabel property value.
 * @return javax.swing.JLabel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileDemandRateLabel;
}
/**
 * Return the MeterNumberLabel property value.
 * @return javax.swing.JLabel
 */
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
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMeterNumberLabel;
}
/**
 * Return the MeterNumberTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getMeterNumberTextField()
{
	if (ivjMeterNumberTextField == null)
	{
		try
		{
			ivjMeterNumberTextField = new javax.swing.JTextField();
			ivjMeterNumberTextField.setName("MeterNumberTextField");
			ivjMeterNumberTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_METER_NUMBER_LENGTH));
		}
		catch (java.lang.Throwable ivjExc)
		{
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
	if( val instanceof MCTBase || val instanceof IEDMeter || val instanceof TwoWayLCR)
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
		else if( val instanceof TwoWayLCR )
		{
			dlp = ((TwoWayLCR) val).getDeviceLoadProfile();
		}
		
		dlp.setLastIntervalDemandRate(SwingUtil.getIntervalComboBoxSecondsValue(getLastIntervalDemandRateComboBox()));

		if( getLoadProfileCollectionPanel().isVisible() )
		{
		 dlp.setLoadProfileDemandRate(SwingUtil.getIntervalComboBoxSecondsValue(getLoadProfileDemandRateComboBox()));

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

			dlp.setVoltageDmdInterval(SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxlVoltInterval()) );

			dlp.setVoltageDmdRate(SwingUtil.getIntervalComboBoxSecondsValue(getJComboBoxlVoltRate()) );
		}
	}

	//handle the devicemetergroup data below
	if (val instanceof IDeviceMeterGroup) {
		DeviceMeterGroup dmg = ((IDeviceMeterGroup)val).getDeviceMeterGroup();
	
		String cycleGroup = (String)getCycleGroupComboBox().getSelectedItem();
		String alternateGroup = (String)getAlternateGroupComboBox().getSelectedItem();
		String meterNumber = getMeterNumberTextField().getText();
		String billingGroup = (String)getJComboBoxBillingGroup().getSelectedItem();
	    
	    LiteYukonPAObject liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(dmg.getDeviceID());
	    SimpleDevice yukonDevice = DaoFactory.getDeviceDao().getYukonDevice(liteYuk);
	    
	    billingGroup = hacker.setGroup(FixedDeviceGroups.BILLINGGROUP, yukonDevice, billingGroup);
	    getJComboBoxBillingGroup().setSelectedItem(billingGroup);
	    
	    cycleGroup = hacker.setGroup(FixedDeviceGroups.COLLECTIONGROUP, yukonDevice, cycleGroup);
	    getCycleGroupComboBox().setSelectedItem(cycleGroup);
	    
	    alternateGroup = hacker.setGroup(FixedDeviceGroups.TESTCOLLECTIONGROUP, yukonDevice, alternateGroup);
	    getAlternateGroupComboBox().setSelectedItem(alternateGroup);
	
	    if( meterNumber != null && meterNumber.length() > 0 ) {
		    dmg.setMeterNumber( meterNumber );
	    }
	}
	
	return val;
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
 * Initialize the class.
 */
private void initialize() {
	try {
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
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
    return this.checkMeterNumber(getMeterNumberTextField().getText());
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
	
	DeviceConfigurationDao deviceConfigDao = YukonSpringHook.getBean("deviceConfigurationDao", DeviceConfigurationDao.class);
	
	getLastIntervalDemandRateLabel().setVisible( val instanceof MCTBase);
	getLastIntervalDemandRateComboBox().setVisible( val instanceof MCTBase);
	getLoadProfileCollectionPanel().setVisible( val instanceof MCTBase );
		
	//are we a voltage channel?
	getJComboBoxlVoltInterval().setVisible( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) );
	getJComboBoxlVoltRate().setVisible( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) );		
	getJLabelVoltDmdRate().setVisible( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) );
	getJLabelVoltIntervalDmdRate().setVisible( DeviceTypesFuncs.isLoadProfileVoltage(deviceType) );

	//The default object is either a MCT or a IEDmeter
	if( val instanceof MCTBase )
	{
	    MCTBase mctBase = (MCTBase)val;
		
	    getLastIntervalDemandRateLabel().setVisible(true);
		getLastIntervalDemandRateComboBox().setVisible(true);
		getLoadProfileCollectionPanel().setVisible(true);
		
		DeviceLoadProfile dlp = ((MCTBase)val).getDeviceLoadProfile();

		String loadProfileCollection = dlp.getLoadProfileCollection();

		SwingUtil.setIntervalComboBoxSelectedItem(
			getLastIntervalDemandRateComboBox(), dlp.getLastIntervalDemandRate().intValue() );

		SwingUtil.setIntervalComboBoxSelectedItem(
			getJComboBoxlVoltInterval(), dlp.getVoltageDmdInterval().intValue() );

		if( deviceType == PAOGroups.DCT_501 
			 || deviceType == PAOGroups.LMT_2 )
		{
			//the last interval demand rate can not be edited for DCT_501 & LMT-2
			getLastIntervalDemandRateComboBox().setVisible(false);
			getLastIntervalDemandRateLabel().setVisible(false);
		}
		
		if(DeviceTypesFuncs.isMCT4XX(deviceType)) {
			getChannel2CheckBox().setEnabled(true);
			getChannel3CheckBox().setEnabled(true);
			
			if(deviceType == PAOGroups.MCT470 || DeviceTypesFuncs.isMCT430(deviceType)){
				
				getJLabelVoltDmdRate().setEnabled(false);
				getJLabelVoltDmdRate().setVisible(false);
				getJComboBoxlVoltRate().setEnabled(false);
				getJComboBoxlVoltRate().setVisible(false);
				
				getJLabelVoltIntervalDmdRate().setEnabled(false);
                getJLabelVoltIntervalDmdRate().setVisible(false);
                getJComboBoxlVoltInterval().setEnabled(false);
                getJComboBoxlVoltInterval().setVisible(false);
			}
			else{
				getJLabelVoltDmdRate().setText("Voltage Profile Demand Rate: ");
			}
			
/**
 * May need to be added back at some point, see YUK-4226 comments
 */
//            if(deviceType == PAOGroups.MCT470)
//            {
//                getLoadProfileDemandRateLabel().setText("Load Profile Rate #1: ");
//                getJLabelVoltDmdRate().setText("Load Profile Rate #2: ");
//                getJLabelVoltIntervalDmdRate().setEnabled(false);
//                getJLabelVoltIntervalDmdRate().setVisible(false);
//                getJComboBoxlVoltInterval().setEnabled(false);
//                getJComboBoxlVoltInterval().setVisible(false);
//                
//            }else
//            {
//                getJLabelVoltDmdRate().setText("Voltage Profile Demand Rate: ");
//            }
			
			getJComboBoxlVoltRate().removeAllItems();
			getJComboBoxlVoltRate().addItem("5 minute");
			getJComboBoxlVoltRate().addItem("15 minute");
			getJComboBoxlVoltRate().addItem("30 minute");
			getJComboBoxlVoltRate().addItem("1 hour");
			
			SwingUtil.setCheckBoxState(getChannel1CheckBox(),new Character(loadProfileCollection.charAt(0)));
		}
		
        SwingUtil.setIntervalComboBoxSelectedItem(getJComboBoxlVoltRate(), dlp.getVoltageDmdRate().intValue());

        SwingUtil.setIntervalComboBoxSelectedItem(getLoadProfileDemandRateComboBox(), dlp
            .getLoadProfileDemandRate().intValue());

		if( DeviceTypesFuncs.isLoadProfile1Channel(deviceType) )
		{
			SwingUtil.setCheckBoxState(getChannel1CheckBox(),new Character(loadProfileCollection.charAt(0)));
			getChannel2CheckBox().setVisible(false);
			getChannel3CheckBox().setVisible(false);
			getChannel4CheckBox().setVisible(false);
		}
	  else if( DeviceTypesFuncs.isLoadProfile3Channel(deviceType) )
	  {
		 SwingUtil.setCheckBoxState(getChannel1CheckBox(), new Character(loadProfileCollection.charAt(0)));
		 SwingUtil.setCheckBoxState(getChannel2CheckBox(), new Character(loadProfileCollection.charAt(1)));
		 SwingUtil.setCheckBoxState(getChannel3CheckBox(), new Character(loadProfileCollection.charAt(2)));
		 getChannel4CheckBox().setVisible(false);
	  } else if( deviceType == PAOGroups.MCT470 || DeviceTypesFuncs.isMCT430(deviceType) ) {
	      SimpleDevice device = new SimpleDevice(mctBase.getPAObjectID(), deviceType);
          ConfigurationBase config = deviceConfigDao.findConfigurationForDevice(device);
          SwingUtil.setCheckBoxState(getChannel1CheckBox(), new Character(loadProfileCollection.charAt(0)));
          SwingUtil.setCheckBoxState(getChannel2CheckBox(), new Character(loadProfileCollection.charAt(1)));
          SwingUtil.setCheckBoxState(getChannel3CheckBox(), new Character(loadProfileCollection.charAt(2)));
          SwingUtil.setCheckBoxState(getChannel4CheckBox(), new Character(loadProfileCollection.charAt(3)));
		
          if(config != null) {
              String demandInterval = deviceConfigDao.getValueForFieldName(config.getId(), "Demand Interval");
              String loadProfile = deviceConfigDao.getValueForFieldName(config.getId(), "Load Profile Interval 1");
              String demandString = demandInterval.equalsIgnoreCase("60") ? "1 hour" : demandInterval + " minute";
              String loadProfileString = loadProfile.equalsIgnoreCase("60") ? "1 hour" : loadProfile + " minute";
              getLastIntervalDemandRateComboBox().setSelectedItem(demandString);
              getLoadProfileDemandRateComboBox().setSelectedItem(loadProfileString);
              getLastIntervalDemandRateComboBox().setEnabled(false);
              getLoadProfileDemandRateComboBox().setEnabled(false);
              
          }
	  } else if( DeviceTypesFuncs.isLoadProfile4Channel(deviceType) ) {
			SwingUtil.setCheckBoxState(getChannel1CheckBox(), new Character(loadProfileCollection.charAt(0)));
			SwingUtil.setCheckBoxState(getChannel2CheckBox(), new Character(loadProfileCollection.charAt(1)));
			SwingUtil.setCheckBoxState(getChannel3CheckBox(), new Character(loadProfileCollection.charAt(2)));
			SwingUtil.setCheckBoxState(getChannel4CheckBox(), new Character(loadProfileCollection.charAt(3)));
			
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
	} else if (val instanceof TwoWayLCR) {
		
		getLastIntervalDemandRateLabel().setVisible(true);
		getLastIntervalDemandRateComboBox().setVisible(true);
		
		DeviceLoadProfile dlp = ((TwoWayLCR)val).getDeviceLoadProfile();

        SwingUtil.setIntervalComboBoxSelectedItem(getLastIntervalDemandRateComboBox(),
            dlp.getLastIntervalDemandRate().intValue());
	}

	//handle the DeviceMeterGroup data below
	if (val instanceof IDeviceMeterGroup) {
		
		DeviceMeterGroup dmg = ((IDeviceMeterGroup)val).getDeviceMeterGroup();
		
		getMeterNumberTextField().setText( dmg.getMeterNumber() );
	    
	    LiteYukonPAObject liteYuk = DaoFactory.getPaoDao().getLiteYukonPAO(dmg.getDeviceID());
	    SimpleDevice yukonDevice = DaoFactory.getDeviceDao().getYukonDevice(liteYuk);
	    String billingGroup = hacker.getGroupForDevice(FixedDeviceGroups.BILLINGGROUP, yukonDevice);
	    String alternateGroup = hacker.getGroupForDevice(FixedDeviceGroups.TESTCOLLECTIONGROUP, yukonDevice);
	    String collectionGroup = hacker.getGroupForDevice(FixedDeviceGroups.COLLECTIONGROUP, yukonDevice);
		getCycleGroupComboBox().setSelectedItem( collectionGroup );
		getAlternateGroupComboBox().setSelectedItem( alternateGroup );
		getJComboBoxBillingGroup().setSelectedItem( billingGroup );
	} else {
		
		getDataCollectionPanel().setVisible(false);
	}
	
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

        String message = "The meter number '"
            + meterNumber
            + "' is already used by the following devices,\n"
            + "are you sure you want to use it again?\n";

        int response = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this,
                                                                             message,
                                                                             "Meter Number Already Used",
                                                                             devices);
        if (response == JOptionPane.NO_OPTION) {
            setErrorString(null);
            return false;
        }
    }

    return true;
}
}

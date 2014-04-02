package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.TNPPTerminal;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.db.device.DeviceTNPPSettings;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableBiMap.Builder;

public class DeviceTNPPTerminalPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener, java.awt.event.ActionListener {
    private javax.swing.JPanel ivjJPanel1 = null;
    private javax.swing.JPanel ivjJPanel2 = null;
    private javax.swing.JLabel ivjOriginAddressLabel = null;
    private javax.swing.JTextField ivjOriginAddressTextField = null;
    private javax.swing.JLabel ivjDestinationAddressLabel = null;
    private javax.swing.JTextField ivjDestinationAddressTextField = null;
	private javax.swing.JLabel ivjIdentifierFormatLabel = null;
	private javax.swing.JComboBox ivjIdentifierFormatComboBox = null;
	private javax.swing.JLabel ivjProtocolLabel = null;
	private javax.swing.JComboBox ivjProtocolComboBox = null;
    private javax.swing.JLabel ivjDataFormatLabel = null;
    private javax.swing.JComboBox ivjDataFormatComboBox = null;
	private javax.swing.JLabel ivjChannelLabel = null;
	private javax.swing.JTextField ivjChannelTextField = null;
    private javax.swing.JLabel ivjZoneLabel = null;
    private javax.swing.JTextField ivjZoneTextField = null;
    private javax.swing.JLabel ivjFunctionCodeLabel = null;
    private javax.swing.JTextField ivjFunctionCodeTextField = null;
    private javax.swing.JLabel ivjInertiaLabel = null;
    private javax.swing.JTextField ivjInertiaTextField = null;
    private javax.swing.JLabel ivjPagerIdLabel = null;
    private javax.swing.JTextField ivjPagerIdTextField = null;

    
    BiMap<String, Character> identifierFormatMap;
    BiMap<String, Character> protocolMap;
    BiMap<String, Character> dataFormatMap;
    
/**
 * Constructor
 */
public DeviceTNPPTerminalPanel() {
	super();
	initialize();
}

/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) {
    Object source = e.getSource();
    if (source == getOriginAddressTextField()) 
        originAddressTextFieldEventTriggered(e);
    if (source == getDestinationAddressTextField()) 
        destinationAddressEventTriggered(e);
	if (source == getChannelTextField()) 
		getChannelTextFieldEventTriggered(e);
	if (source == getZoneTextField()) 
		getZoneTextFieldEventTriggered(e);
	if (source == getFunctionCodeTextField())
	    getFunctionCodeTextFieldEventTriggered(e);
    if (source == getInertiaTextField())
        getInertiaTextFieldEventTriggered(e);
    if (source == getPagerIdTextField())
        getPagerIdTextFieldEventTriggered(e);
}

private void originAddressTextFieldEventTriggered(javax.swing.event.CaretEvent arg1) {
    this.eitherTextField_CaretUpdate(arg1);
}

private void destinationAddressEventTriggered(javax.swing.event.CaretEvent arg1) {
    this.eitherTextField_CaretUpdate(arg1);
}

private void getChannelTextFieldEventTriggered(javax.swing.event.CaretEvent caretEvent) {
    this.eitherTextField_CaretUpdate(caretEvent);
}

private void getZoneTextFieldEventTriggered(javax.swing.event.CaretEvent caretEvent) {
    this.eitherTextField_CaretUpdate(caretEvent);
}

private void getFunctionCodeTextFieldEventTriggered(javax.swing.event.CaretEvent caretEvent) {
    this.eitherTextField_CaretUpdate(caretEvent);
}

private void getInertiaTextFieldEventTriggered(javax.swing.event.CaretEvent caretEvent) {
    this.eitherTextField_CaretUpdate(caretEvent);
}

private void getPagerIdTextFieldEventTriggered(javax.swing.event.CaretEvent caretEvent) {
    this.eitherTextField_CaretUpdate(caretEvent);
}

/**
 * Comment
 */
public void deviceNameAddressPanel_ComponentShown(java.awt.event.ComponentEvent componentEvent) {
	return;
}
/**
 * Comment
 */
public void deviceNameAddressPanel_ComponentShown1(java.awt.event.ComponentEvent componentEvent) {
	return;
}
/**
 * Comment
 */
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOriginAddressLabel = new java.awt.GridBagConstraints();
			constraintsOriginAddressLabel.gridx = 0; constraintsOriginAddressLabel.gridy = 0;
			constraintsOriginAddressLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOriginAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getOriginAddressLabel(), constraintsOriginAddressLabel);
			
			java.awt.GridBagConstraints constraintsOriginAddressTextField = new java.awt.GridBagConstraints();
			constraintsOriginAddressTextField.gridx = 1; constraintsOriginAddressTextField.gridy = 0;
			constraintsOriginAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOriginAddressTextField.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getOriginAddressTextField(), constraintsOriginAddressTextField);
			
			java.awt.GridBagConstraints constraintsDestinationAddressLabel = new java.awt.GridBagConstraints();
			constraintsDestinationAddressLabel.gridx = 0; constraintsDestinationAddressLabel.gridy = 1;
            constraintsDestinationAddressLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDestinationAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getDestinationAddressLabel(), constraintsDestinationAddressLabel);
			
			java.awt.GridBagConstraints constraintsDestinationAddressTextField = new java.awt.GridBagConstraints();
			constraintsDestinationAddressTextField.gridx = 1; constraintsDestinationAddressTextField.gridy = 1;
            constraintsDestinationAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDestinationAddressTextField.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getDestinationAddressTextField(), constraintsDestinationAddressTextField);

			java.awt.GridBagConstraints constraintsInertiaLabel = new java.awt.GridBagConstraints();
			constraintsInertiaLabel.gridx = 0; constraintsInertiaLabel.gridy = 2;
            constraintsInertiaLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsInertiaLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getInertiaLabel(), constraintsInertiaLabel);
			
			java.awt.GridBagConstraints constraintsInertiaTextField = new java.awt.GridBagConstraints();
			constraintsInertiaTextField.gridx = 1; constraintsInertiaTextField.gridy = 2;
			constraintsInertiaTextField.gridwidth = GridBagConstraints.REMAINDER;
            constraintsInertiaTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsInertiaTextField.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getInertiaTextField(), constraintsInertiaTextField);
			
			java.awt.GridBagConstraints constraintsProtocolLabel = new java.awt.GridBagConstraints();
			constraintsProtocolLabel.gridx = 0; constraintsProtocolLabel.gridy = 3;
			constraintsProtocolLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsProtocolLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getProtocolLabel(), constraintsProtocolLabel);
			
			java.awt.GridBagConstraints constraintsProtocolComboBox = new java.awt.GridBagConstraints();
			constraintsProtocolComboBox.gridx = 1; constraintsProtocolComboBox.gridy = 3;
			constraintsProtocolComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsProtocolComboBox.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getProtocolComboBox(), constraintsProtocolComboBox);

			java.awt.GridBagConstraints constraintsDataFormatLabel = new java.awt.GridBagConstraints();
			constraintsDataFormatLabel.gridx = 0; constraintsDataFormatLabel.gridy = 4;
			constraintsDataFormatLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDataFormatLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getDataFormatLabel(), constraintsDataFormatLabel);

			java.awt.GridBagConstraints constraintsDataFormatComboBox = new java.awt.GridBagConstraints();
			constraintsDataFormatComboBox.gridx = 1; constraintsDataFormatComboBox.gridy = 4;
			constraintsDataFormatComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDataFormatComboBox.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getDataFormatComboBox(), constraintsDataFormatComboBox);

	        java.awt.GridBagConstraints constraintsIdentifierFormatLabel = new java.awt.GridBagConstraints();
            constraintsIdentifierFormatLabel.gridx = 0; constraintsIdentifierFormatLabel.gridy = 5;
	        constraintsIdentifierFormatLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
	        constraintsIdentifierFormatLabel.anchor = java.awt.GridBagConstraints.WEST;
	        getJPanel1().add(getIdentifierFormatLabel(), constraintsIdentifierFormatLabel);
	            
	        java.awt.GridBagConstraints constraintsIdentifierFormatComboBox = new java.awt.GridBagConstraints();
	        constraintsIdentifierFormatComboBox.gridx = 1; constraintsIdentifierFormatComboBox.gridy = 5;
	        constraintsIdentifierFormatComboBox.anchor = java.awt.GridBagConstraints.WEST;
	        constraintsIdentifierFormatComboBox.insets = new java.awt.Insets(5, 8, 5, 0);
	        getJPanel1().add(getIdentifierFormatComboBox(), constraintsIdentifierFormatComboBox);
	            
	        java.awt.GridBagConstraints constraintsPagerIdLabel = new java.awt.GridBagConstraints();
	        constraintsPagerIdLabel.gridx = 0; constraintsPagerIdLabel.gridy = 6;
	        constraintsPagerIdLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
	        constraintsPagerIdLabel.anchor = java.awt.GridBagConstraints.WEST;
	        getJPanel1().add(getPagerIdLabel(), constraintsPagerIdLabel);
	            
	        java.awt.GridBagConstraints constraintsPagerIdTextField = new java.awt.GridBagConstraints();
	        constraintsPagerIdTextField.gridx = 1; constraintsPagerIdTextField.gridy = 6;
	        constraintsPagerIdTextField.anchor = java.awt.GridBagConstraints.WEST;
	        constraintsPagerIdTextField.insets = new java.awt.Insets(5, 8, 5, 0);
	        getJPanel1().add(getPagerIdTextField(), constraintsPagerIdTextField);
			
	        
	        java.awt.GridBagConstraints constraintsPanel2TextField = new java.awt.GridBagConstraints();
	        constraintsPanel2TextField.gridx = 0; constraintsPagerIdTextField.gridy = 7;
	        constraintsPanel2TextField.gridwidth = 2;
	        constraintsPanel2TextField.anchor = java.awt.GridBagConstraints.WEST;
	        getJPanel1().add(getJPanel2(), constraintsPanel2TextField);

		} catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjJPanel1;
}			
			
			
private javax.swing.JPanel getJPanel2() {
    if (ivjJPanel2 == null) {
        try {
            ivjJPanel2 = new javax.swing.JPanel();
            ivjJPanel2.setName("JPanel2");
            ivjJPanel2.setLayout(new java.awt.GridBagLayout());
            
			
			java.awt.GridBagConstraints constraintsChannelLabel = new java.awt.GridBagConstraints();
            constraintsChannelLabel.gridx = 0; constraintsChannelLabel.gridy = 1;
            constraintsChannelLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsChannelLabel.anchor = java.awt.GridBagConstraints.WEST;
            getJPanel2().add(getChannelLabel(), constraintsChannelLabel);

            java.awt.GridBagConstraints constraintsChannelTextField = new java.awt.GridBagConstraints();
            constraintsChannelTextField.gridx = 1; constraintsChannelTextField.gridy = 1;
            constraintsChannelTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsChannelTextField.insets = new java.awt.Insets(0, 8, 0, 35);
            getJPanel2().add(getChannelTextField(), constraintsChannelTextField);

            java.awt.GridBagConstraints constraintsFunctionCodeLabel = new java.awt.GridBagConstraints();
            constraintsFunctionCodeLabel.gridx = 2; constraintsFunctionCodeLabel.gridy = 1;
            constraintsFunctionCodeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsFunctionCodeLabel.anchor = java.awt.GridBagConstraints.WEST;
            getJPanel2().add(getFunctionCodeLabel(), constraintsFunctionCodeLabel);

            java.awt.GridBagConstraints constraintsFunctionCodeTextField = new java.awt.GridBagConstraints();
            constraintsFunctionCodeTextField.gridx = 3; constraintsFunctionCodeTextField.gridy = 1;
            constraintsFunctionCodeTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsFunctionCodeTextField.insets = new java.awt.Insets(0, 8, 0, 35);
            getJPanel2().add(getFunctionCodeTextField(), constraintsFunctionCodeTextField);

            java.awt.GridBagConstraints constraintsZoneLabel = new java.awt.GridBagConstraints();
            constraintsZoneLabel.gridx = 4; constraintsZoneLabel.gridy = 1;
            constraintsZoneLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsZoneLabel.anchor = java.awt.GridBagConstraints.WEST;
            getJPanel2().add(getZoneLabel(), constraintsZoneLabel);
            
            java.awt.GridBagConstraints constraintsZoneTextField = new java.awt.GridBagConstraints();
            constraintsZoneTextField.gridx = 5; constraintsZoneTextField.gridy = 1;
            constraintsZoneTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsZoneTextField.insets = new java.awt.Insets(0, 8, 0, 8);
            getJPanel2().add(getZoneTextField(), constraintsZoneTextField);
            
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}


/**
 * Return the OriginAddressLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getOriginAddressLabel() {
    if (ivjOriginAddressLabel == null) {
        try {
            ivjOriginAddressLabel = new javax.swing.JLabel();
            ivjOriginAddressLabel.setName("OriginAddressLabel");
            ivjOriginAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjOriginAddressLabel.setText("Origin Address:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjOriginAddressLabel;
}
/**
 * Return the OriginAddressTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getOriginAddressTextField() {
    if (ivjOriginAddressTextField == null) {
        try {
            ivjOriginAddressTextField = new javax.swing.JTextField();
            ivjOriginAddressTextField.setName("OriginAddressTextField");
            ivjOriginAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
            ivjOriginAddressTextField.setColumns(20);
            ivjOriginAddressTextField.setDocument(new LongRangeDocument(0, 65535));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjOriginAddressTextField;
}

/**
 * Return the DestinationAddressLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDestinationAddressLabel() {
    if (ivjDestinationAddressLabel == null) {
        try {
            ivjDestinationAddressLabel = new javax.swing.JLabel();
            ivjDestinationAddressLabel.setName("DestinationAddressLabel");
            ivjDestinationAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjDestinationAddressLabel.setText("Destination Address:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjDestinationAddressLabel;
}

/**
 * Return the DestinationAddressTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getDestinationAddressTextField() {
    if (ivjDestinationAddressTextField == null) {
        try {
            ivjDestinationAddressTextField = new javax.swing.JTextField();
            ivjDestinationAddressTextField.setName("DestinationAddressTextField");
            ivjDestinationAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
            ivjDestinationAddressTextField.setColumns(20);
            ivjDestinationAddressTextField.setDocument(new LongRangeDocument(0, 65535));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjDestinationAddressTextField;
}

/**
 * Return the IdentifierFormatLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getIdentifierFormatLabel() {
	if (ivjIdentifierFormatLabel == null) {
		try {
		    ivjIdentifierFormatLabel = new javax.swing.JLabel();
		    ivjIdentifierFormatLabel.setName("IdentifierFormatLabel");
		    ivjIdentifierFormatLabel.setFont(new java.awt.Font("dialog", 0, 14));
		    ivjIdentifierFormatLabel.setText("Identifier Format:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjIdentifierFormatLabel;
}

/**
 * Return the IdentifierFormatComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getIdentifierFormatComboBox() {
	if (ivjIdentifierFormatComboBox == null) {
		try {
		    ivjIdentifierFormatComboBox = new javax.swing.JComboBox();
		    ivjIdentifierFormatComboBox.setName("NameTextField");
		    ivjIdentifierFormatComboBox.setFont(new java.awt.Font("sansserif", 0, 14));

		    // A map of all the formats and corresponding values
		    Builder<String, Character> builder = ImmutableBiMap.builder();
		    builder.put("Cap Page",'A');
		    builder.put("Id Page",'B');
		    identifierFormatMap = builder.build();
	        
            // Add all the key values to the JComboBox.
            Set<String> keySet = identifierFormatMap.keySet();
            for (String key : keySet) {
                ivjIdentifierFormatComboBox.addItem(key);
            }
		
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjIdentifierFormatComboBox;
}
/**
 * Return the ProtocolLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getProtocolLabel() {
	if (ivjProtocolLabel == null) {
		try {
		    ivjProtocolLabel = new javax.swing.JLabel();
		    ivjProtocolLabel.setName("ProtocolLabel");
		    ivjProtocolLabel.setFont(new java.awt.Font("dialog", 0, 14));
		    ivjProtocolLabel.setText("Protocol:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjProtocolLabel;
}
/**
 * Return the ProtocolComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getProtocolComboBox() {
    if (ivjProtocolComboBox == null) {
        try {
            ivjProtocolComboBox = new javax.swing.JComboBox();
            ivjProtocolComboBox.setName("ProtocolComboBox");
            ivjProtocolComboBox.setFont(new java.awt.Font("sansserif", 0, 14));

            // A map of all the protocol and corresponding values
            Builder<String, Character> builder = ImmutableBiMap.builder();
            builder.put("FLEX",'F');
            builder.put("Golay Sequential Code",'G');
            builder.put("POCSAG - 512 baud (CCIR #1)",'P');
            builder.put("POCSAG - 1200 baud",'p');
            builder.put("POCSAG - 2400 baud",'Q');
            protocolMap = builder.build();
            
            // Add all the key values to the JComboBox.
            Set<String> keySet = protocolMap.keySet();
            for (String key : keySet) {
                ivjProtocolComboBox.addItem(key);
            }
        
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjProtocolComboBox;
}


/**
 * Return the DataFormatLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDataFormatLabel() {
    if (ivjDataFormatLabel == null) {
        try {
            ivjDataFormatLabel = new javax.swing.JLabel();
            ivjDataFormatLabel.setName("DataFormatLabel");
            ivjDataFormatLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjDataFormatLabel.setText("Data Format:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjDataFormatLabel;
}

/**
 * Return the DataFormatComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getDataFormatComboBox() {
    if (ivjDataFormatComboBox == null) {
        try {
            ivjDataFormatComboBox = new javax.swing.JComboBox();
            ivjDataFormatComboBox.setName("DestinationAddressTextField");
            ivjDataFormatComboBox.setFont(new java.awt.Font("sansserif", 0, 14));
            
            // A map of all the data formats and corresponding values
            Builder<String, Character> builder = ImmutableBiMap.builder();
            builder.put("Alphanumeric Display",'A');
            builder.put("Beep Only",'B');
            builder.put("Numeric Display",'N');
            dataFormatMap = builder.build();

            // Add all the key values to the JComboBox.
            Set<String> keySet = dataFormatMap.keySet();
            for (String key : keySet) {
                ivjDataFormatComboBox.addItem(key);
            }
        
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjDataFormatComboBox;
}




/**
 * Return the ChannelLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getChannelLabel() {
    if (ivjChannelLabel == null) {
        try {
            ivjChannelLabel = new javax.swing.JLabel();
            ivjChannelLabel.setName("ChannelLabel");
            ivjChannelLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjChannelLabel.setText("Channel:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjChannelLabel;
}
/**
 * Return the ChannelTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getChannelTextField() {
    if (ivjChannelTextField == null) {
        try {
            ivjChannelTextField = new javax.swing.JTextField();
            ivjChannelTextField.setName("ChannelTextField");
            ivjChannelTextField.setFont(new java.awt.Font("sansserif", 0, 14));
            ivjChannelTextField.setColumns(2);
            ivjChannelTextField.setDocument(new LongRangeDocument(0, 63));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjChannelTextField;
}
/**
 * Return the ZoneLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getZoneLabel() {
    if (ivjZoneLabel == null) {
        try {
            ivjZoneLabel = new javax.swing.JLabel();
            ivjZoneLabel.setName("ZoneLabel");
            ivjZoneLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjZoneLabel.setText("Zone:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjZoneLabel;
}
/**
 * Return the ZoneTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getZoneTextField() {
    if (ivjZoneTextField == null) {
        try {
            ivjZoneTextField = new javax.swing.JTextField();
            ivjZoneTextField.setName("ZoneTextField");
            ivjZoneTextField.setFont(new java.awt.Font("sansserif", 0, 14));
            ivjZoneTextField.setColumns(2);
            ivjZoneTextField.setDocument(new LongRangeDocument(0, 63));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjZoneTextField;
}

/**
 * Return the FunctionCodeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getFunctionCodeLabel() {
    if (ivjFunctionCodeLabel == null) {
        try {
            ivjFunctionCodeLabel = new javax.swing.JLabel();
            ivjFunctionCodeLabel.setName("FunctionCodeLabel");
            ivjFunctionCodeLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjFunctionCodeLabel.setText("Function Code:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjFunctionCodeLabel;
}

/**
 * Return the FunctionCodeTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getFunctionCodeTextField() {
    if (ivjFunctionCodeTextField == null) {
        try {
            ivjFunctionCodeTextField = new javax.swing.JTextField();
            ivjFunctionCodeTextField.setName("FunctionCodeTextField");
            ivjFunctionCodeTextField.setFont(new java.awt.Font("sansserif", 0, 14));
            ivjFunctionCodeTextField.setColumns(2);
            ivjFunctionCodeTextField.setDocument(new LongRangeDocument(0, 63));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjFunctionCodeTextField;
}

/**
 * Return the InertiaLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getInertiaLabel() {
    if (ivjInertiaLabel == null) {
        try {
            ivjInertiaLabel = new javax.swing.JLabel();
            ivjInertiaLabel.setName("InertiaLabel");
            ivjInertiaLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjInertiaLabel.setText("Inertia:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjInertiaLabel;
}

/**
 * Return the InertiaTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getInertiaTextField() {
    if (ivjInertiaTextField == null) {
        try {
            ivjInertiaTextField = new javax.swing.JTextField();
            ivjInertiaTextField.setName("InertiaTextField");
            ivjInertiaTextField.setFont(new java.awt.Font("sansserif", 0, 14));
            ivjInertiaTextField.setColumns(20);
            ivjInertiaTextField.setDocument(new LongRangeDocument(-999999999L, 999999999L));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjInertiaTextField;
}

/**
 * Return the PagerIdLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getPagerIdLabel() {
    if (ivjPagerIdLabel == null) {
        try {
            ivjPagerIdLabel = new javax.swing.JLabel();
            ivjPagerIdLabel.setName("PagerIdLabel");
            ivjPagerIdLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjPagerIdLabel.setText("Pager Id:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjPagerIdLabel;
}

/**
 * Return the PagerIdTextField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getPagerIdTextField() {
    if (ivjPagerIdTextField == null) {
        try {
            ivjPagerIdTextField = new javax.swing.JTextField();
            ivjPagerIdTextField.setName("PagerIdTextField");
            ivjPagerIdTextField.setFont(new java.awt.Font("sansserif", 0, 14));
            ivjPagerIdTextField.setColumns(10);
            ivjPagerIdTextField.setDocument(new TextFieldDocument(10));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjPagerIdTextField;
}

public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}

/**
 * 
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
    Object value = null;
    if (val instanceof SmartMultiDBPersistent) {
        value = ((SmartMultiDBPersistent) val).getOwnerDBPersistent();
    } else {
        value = val;
    }
	TNPPTerminal tnppTerm = (TNPPTerminal)value;

	String originAddress = getOriginAddressTextField().getText();
    tnppTerm.getDeviceTNPPSettings().setOriginAddress(new Integer(originAddress));

	String destinationAddress = getDestinationAddressTextField().getText();
	tnppTerm.getDeviceTNPPSettings().setDestinationAddress(new Integer(destinationAddress));
	
	String identifierFormatStr = (String)getIdentifierFormatComboBox().getSelectedItem();
	tnppTerm.getDeviceTNPPSettings().setIdentifierFormat(identifierFormatMap.get(identifierFormatStr) );

	String protocolStr = (String)getProtocolComboBox().getSelectedItem();
	tnppTerm.getDeviceTNPPSettings().setProtocol(protocolMap.get(protocolStr));

    String dataFormatStr = (String)getDataFormatComboBox().getSelectedItem();
    tnppTerm.getDeviceTNPPSettings().setDataFormat(dataFormatMap.get(dataFormatStr));

    String channelStr = getChannelTextField().getText();
    Integer channelInt = new Integer(channelStr);
    tnppTerm.getDeviceTNPPSettings().setChannel(DeviceTNPPSettings.addMaskAndConvertToChar(channelInt));

    String zoneStr = getZoneTextField().getText();
    Integer zoneInt = new Integer(zoneStr);
    tnppTerm.getDeviceTNPPSettings().setZone(DeviceTNPPSettings.addMaskAndConvertToChar(zoneInt));

    String functionCodeStr = getFunctionCodeTextField().getText();
    Integer functionCodeInt = new Integer(functionCodeStr);
    tnppTerm.getDeviceTNPPSettings().setFunctionCode(DeviceTNPPSettings.addMaskAndConvertToChar(functionCodeInt));
    
    String inertiaStr = getInertiaTextField().getText();
    Integer inertiaInt = new Integer(inertiaStr);
    tnppTerm.getDeviceTNPPSettings().setInertia(inertiaInt.intValue());

    tnppTerm.getDeviceTNPPSettings().setPagerId(getPagerIdTextField().getText());
    
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
private void initConnections() throws java.lang.Exception {

    getIdentifierFormatComboBox().addActionListener(this);
    getProtocolComboBox().addActionListener(this);
    getDataFormatComboBox().addActionListener(this);
    
    getOriginAddressTextField().addCaretListener(this);
    getDestinationAddressTextField().addCaretListener(this);
    getChannelTextField().addCaretListener(this);
    getZoneTextField().addCaretListener(this);
    getFunctionCodeTextField().addCaretListener(this);
    getInertiaTextField().addCaretListener(this);
    getPagerIdTextField().addCaretListener(this);

}

/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("DeviceNameAddressPanel");
		setSize(350, 200);
        setLayout(new java.awt.GridBagLayout());

        java.awt.GridBagConstraints constraintsJPanelScanConfig = new java.awt.GridBagConstraints();
        constraintsJPanelScanConfig.gridx = 1; constraintsJPanelScanConfig.gridy = 1;
        constraintsJPanelScanConfig.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJPanelScanConfig.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJPanelScanConfig.weightx = 1.0;
        constraintsJPanelScanConfig.weighty = 1.0;
        add(getJPanel1(), constraintsJPanelScanConfig);

        initConnections();		
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Method to handle events for the CaretListener interface.
 */
@Override
public void actionPerformed(ActionEvent e) {
    Object source = e.getSource();
    if(source == getIdentifierFormatComboBox())
        identifierFormatComboBoxEventTriggered();
    if(source == getProtocolComboBox())
        protocolComboBoxEventTriggered();
    if(source == getDataFormatComboBox())
        dataFormatComboBoxEventTriggered();
}

private void dataFormatComboBoxEventTriggered() {
    this.fireInputUpdate();
}

private void protocolComboBoxEventTriggered() {
    this.fireInputUpdate();
}

private void identifierFormatComboBoxEventTriggered() {
    this.fireInputUpdate();
}

/**
 * Validates the current values of the panel.
 */
public boolean isInputValid() {

    // Origin Address Validation
    String originAddressErrorMessage = "The Origin Address text field must be filled in and must be a number";
    if( StringUtils.isBlank(getOriginAddressTextField().getText()))
    {
        setErrorString(originAddressErrorMessage);
        return false;
    }
    try {
        new Integer(getOriginAddressTextField().getText());
    } catch (NumberFormatException e) {
        setErrorString(originAddressErrorMessage);
        return false;
    }
    
    // Destination Address Validation
    String destinationAddressErrorMessage = "The Destination Address text field must be filled in and must be a number";
    if( StringUtils.isBlank(getDestinationAddressTextField().getText()))
    {
        setErrorString(destinationAddressErrorMessage);
        return false;
    }
    try {
        new Integer(getDestinationAddressTextField().getText());
    } catch (NumberFormatException e) {
        setErrorString(destinationAddressErrorMessage);
        return false;
    }
    
    // Channel Validation
    String channelErrorMessage = "The Channel value must be a number between 0 - 63";
    if( StringUtils.isBlank(getChannelTextField().getText()))
    {
        setErrorString(channelErrorMessage);
        return false;
    }
    try {
        Integer channelInt = new Integer(getChannelTextField().getText());
        if (channelInt < 0 || channelInt > 63){
            setErrorString(channelErrorMessage);
            return false;
        }
    } catch (NumberFormatException e) {
        setErrorString(channelErrorMessage);
        return false;
    }
    
    // Zone Validation
    String zoneErrorMessage = "The zone value must be a number between 0 - 63";
    if( StringUtils.isBlank(getZoneTextField().getText()))
    {
        setErrorString(zoneErrorMessage);
        return false;
    }
    try {
        Integer zoneInt = new Integer(getZoneTextField().getText());
        if (zoneInt < 0 || zoneInt > 63){
            setErrorString(zoneErrorMessage);
            return false;
        }
    } catch (NumberFormatException e) {
        setErrorString(zoneErrorMessage);
        return false;
    }
    
    
    // Function Code Validation
    String functionCodeErrorMessage = "The function code value must be a number between 0 - 63";
    if( StringUtils.isBlank(getFunctionCodeTextField().getText()))
    {
        setErrorString(functionCodeErrorMessage);
        return false;
    }
    try {
        Integer functionCodeInt = new Integer(getFunctionCodeTextField().getText());
        if (functionCodeInt < 0 || functionCodeInt > 63){
            setErrorString(functionCodeErrorMessage);
            return false;
        }
    } catch (NumberFormatException e) {
        setErrorString(functionCodeErrorMessage);
        return false;
    }
    
    // Inertia Validation
    String InertiaErrorMessage = "The inertia text field must be filled in and be a valid number";
    if( StringUtils.isBlank(getInertiaTextField().getText()))
    {
        setErrorString(InertiaErrorMessage);
        return false;
    }
    try {
        new Integer(getInertiaTextField().getText());
    } catch (NumberFormatException e) {
        setErrorString(InertiaErrorMessage);
        return false;
    }
    
    // Pager Id Validation
    String identifierFormatStr = (String)getIdentifierFormatComboBox().getSelectedItem();
    if(identifierFormatMap.get(identifierFormatStr).equals('A') ){
        String PagerIdFormatErrorMessage = "The pager id must be a number with only eight digits when using the cap page format.";
        if( StringUtils.isBlank(getPagerIdTextField().getText()) ||
            getPagerIdTextField().getText().length() > 8)
        {
            setErrorString(PagerIdFormatErrorMessage);
            return false;
        }
        try {
            new Integer(getPagerIdTextField().getText());
        } catch (NumberFormatException e) {
            setErrorString(PagerIdFormatErrorMessage);
            return false;
        }
    }

    if(identifierFormatMap.get(identifierFormatStr).equals('B') ){
        String PagerIdFormatErrorMessage = "The pager id must be a string with only ten alphanumeric characters when using the id page format.";
        if( StringUtils.isBlank(getPagerIdTextField().getText()) ||
            getPagerIdTextField().getText().length() > 10)
        {
            setErrorString(PagerIdFormatErrorMessage);
            return false;
        }
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
		DeviceNameAddressPanel aDeviceNameAddressPanel;
		aDeviceNameAddressPanel = new DeviceNameAddressPanel();
		frame.getContentPane().add("Center", aDeviceNameAddressPanel);
		frame.setSize(aDeviceNameAddressPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}

/**
 * @param o java.lang.Object
 */
public void setValue(Object o) {

    TNPPTerminal tnppTerm = (TNPPTerminal)o;

    if (tnppTerm != null) {
        DeviceTNPPSettings deviceTNPPSettings = tnppTerm.getDeviceTNPPSettings();
        
        getOriginAddressTextField().setText(deviceTNPPSettings.getOriginAddress().toString());
        getDestinationAddressTextField().setText(deviceTNPPSettings.getDestinationAddress().toString());
        getInertiaTextField().setText(deviceTNPPSettings.getInertia().toString());
        getProtocolComboBox().setSelectedItem(protocolMap.inverse().get(deviceTNPPSettings.getProtocol()));
        getDataFormatComboBox().setSelectedItem(dataFormatMap.inverse().get(deviceTNPPSettings.getDataFormat()));
        getIdentifierFormatComboBox().setSelectedItem(identifierFormatMap.inverse().get(deviceTNPPSettings.getIdentifierFormat()));
        getPagerIdTextField().setText(deviceTNPPSettings.getPagerId());
        Integer channelInt = DeviceTNPPSettings.convertToIntAndRemoveMask(deviceTNPPSettings.getChannel());
        getChannelTextField().setText(channelInt.toString());
        Integer functionCodeInt = DeviceTNPPSettings.convertToIntAndRemoveMask(deviceTNPPSettings.getFunctionCode());
        getFunctionCodeTextField().setText(functionCodeInt.toString());
        Integer zoneInt = DeviceTNPPSettings.convertToIntAndRemoveMask(deviceTNPPSettings.getZone());
        getZoneTextField().setText(zoneInt.toString());
    }
        
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getIdentifierFormatComboBox().requestFocus(); 
        } 
    });    
}

}

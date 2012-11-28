package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;
import java.util.Vector;

import javax.swing.JComboBox;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.AnalogControlType;
import com.cannontech.database.data.point.PointTypes;

public class PointAnalogPhysicalSettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener, com.klg.jclass.util.value.JCValueListener
{
	private javax.swing.JLabel ivjPointOffsetLabel = null;
	private javax.swing.JCheckBox ivjPhysicalPointOffsetCheckBox = null;
	private com.klg.jclass.field.JCSpinField ivjDeadbandSpinner = null;
	private javax.swing.JCheckBox ivjDeadbandCheckBox = null;
	private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
	private Vector<LitePoint> usedPointOffsetsVector = null;
	private javax.swing.JPanel ivjDeadbandPanel = null;
	private javax.swing.JLabel ivjUsedPointOffsetLabel = null;
	private javax.swing.JLabel ivjDataOffsetLabel = null;
	private javax.swing.JTextField ivjDataOffsetTextField = null;
	private javax.swing.JLabel ivjMultiplierLabel = null;
	private javax.swing.JTextField ivjMultiplierTextField = null;
    private javax.swing.JPanel ivjRawValuePanel = null;
    private javax.swing.JPanel ivjControlSettingsPanel = null;
    private javax.swing.JComboBox<String> ivjControlTypeComboBox = null;
    private javax.swing.JLabel ivjControlTypeLabel = null;
    private javax.swing.JLabel ivjControlPointOffsetLabel = null;
    private com.klg.jclass.field.JCSpinField ivjControlPointOffsetSpinner = null;
    private javax.swing.JCheckBox ivjControlInhibitCheckBox = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointAnalogPhysicalSettingsEditorPanel() {
	super();
	initialize();
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
    if (e.getSource() == getControlTypeComboBox()) 
        connEtoC1(e);
    if (e.getSource() == getControlInhibitCheckBox()) 
        connEtoC2(e);
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
	if (e.getSource() == getMultiplierTextField()) 
		connEtoC6(e);
	if (e.getSource() == getDataOffsetTextField()) 
		connEtoC7(e);
	// user code begin {2}
	// user code end
}

private void connEtoC1(java.awt.event.ActionEvent arg1) {
    try {
        this.controlTypeComboBox_ActionPerformed(arg1);
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

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
 * connEtoC3:  (PhysicalPointOffsetCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if ( getPhysicalPointOffsetCheckBox().isSelected() )
		{
			getPointOffsetSpinner().setEnabled(true);
			getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
			getPointOffsetSpinner().setValue(new Integer(1));
			int temp = 2;
			while( getUsedPointOffsetLabel().getText() != "" )
			{
				getPointOffsetSpinner().setValue(new Integer(temp));
				temp++;
			}
			getPointOffsetLabel().setEnabled(true);
		}
		else
		{
			getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(0), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
			getPointOffsetSpinner().setValue(new Integer(0));
			getPointOffsetLabel().setEnabled(false);
			getDeadbandCheckBox().setSelected(false);
			getDeadbandSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(-1), new Integer(-1), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(-1)));
			getDeadbandSpinner().setValue(new Integer(-1));
			getPointOffsetSpinner().setEnabled(false);
		}

		revalidate();
		repaint();
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (DeadbandCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		if ( getDeadbandCheckBox().isSelected() )
		{
			getDeadbandSpinner().setEnabled(true);			
		}
		else
		{
			getDeadbandSpinner().setEnabled(false);
		}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (MultiplierTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC7:  (DataOffsetTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointAnalogPhysicalSettingsEditorPanel.fireInputUpdate()V)
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

public void controlTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
    Object controlType = getControlTypeComboBox().getSelectedItem();

    boolean enabled = ! controlType.toString().equals(AnalogControlType.NONE.getControlName());

    getControlPointOffsetLabel().setEnabled(enabled);
    getControlPointOffsetSpinner().setEnabled(enabled);

    getControlInhibitCheckBox().setEnabled(enabled);    
    
    revalidate();
    repaint();
    fireInputUpdate();
    return;
}

private javax.swing.JCheckBox getControlInhibitCheckBox() {
    if (ivjControlInhibitCheckBox == null) {
        try {
            ivjControlInhibitCheckBox = new javax.swing.JCheckBox();
            ivjControlInhibitCheckBox.setName("ControlInhibitCheckBox");
            ivjControlInhibitCheckBox.setSelected(false);
            ivjControlInhibitCheckBox.setEnabled(false);
            ivjControlInhibitCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
            ivjControlInhibitCheckBox.setText("Control Inhibit");
            ivjControlInhibitCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjControlInhibitCheckBox;
}

private javax.swing.JLabel getControlPointOffsetLabel() {
    if (ivjControlPointOffsetLabel == null) {
        try {
            ivjControlPointOffsetLabel = new javax.swing.JLabel();
            ivjControlPointOffsetLabel.setName("ControlPointOffsetLabel");
            ivjControlPointOffsetLabel.setText("Control Pt Offset:");
            ivjControlPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjControlPointOffsetLabel.setEnabled(false);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjControlPointOffsetLabel;
}

private com.klg.jclass.field.JCSpinField getControlPointOffsetSpinner() {
    if (ivjControlPointOffsetSpinner == null) {
        try {
            ivjControlPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
            ivjControlPointOffsetSpinner.setName("ControlPointOffsetSpinner");
            ivjControlPointOffsetSpinner.setBackground(java.awt.Color.white);
            ivjControlPointOffsetSpinner.setEnabled(false);
            ivjControlPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjControlPointOffsetSpinner;
}

private javax.swing.JPanel getControlSettingsPanel() {
    if (ivjControlSettingsPanel == null) {
        try {
            com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
            ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
            ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
            ivjLocalBorder.setTitle("Control Settings");
            ivjControlSettingsPanel = new javax.swing.JPanel();
            ivjControlSettingsPanel.setName("ControlPanel");
            ivjControlSettingsPanel.setBorder(ivjLocalBorder);
            ivjControlSettingsPanel.setLayout(new java.awt.GridBagLayout());

            GridBagConstraints constraintsControlTypeLabel = new GridBagConstraints();
            constraintsControlTypeLabel.gridx = 0;
            constraintsControlTypeLabel.anchor = GridBagConstraints.LINE_START;
            constraintsControlTypeLabel.insets = new Insets(0, 8, 0, 4);
            getControlSettingsPanel().add(getControlTypeLabel(), constraintsControlTypeLabel);

            GridBagConstraints constraintsControlTypeComboBox = new GridBagConstraints();
            constraintsControlTypeComboBox.gridx = 1;
            constraintsControlTypeComboBox.weightx = 0.5;
            constraintsControlTypeComboBox.anchor = GridBagConstraints.LINE_START;
            constraintsControlTypeComboBox.fill = GridBagConstraints.HORIZONTAL;
            getControlSettingsPanel().add(getControlTypeComboBox(), constraintsControlTypeComboBox);

            GridBagConstraints constraintsControlPointOffsetLabel = new GridBagConstraints();
            constraintsControlPointOffsetLabel.gridx = 2;
            constraintsControlPointOffsetLabel.insets = new Insets(0, 4, 0, 4);
            getControlSettingsPanel().add(getControlPointOffsetLabel(), constraintsControlPointOffsetLabel);

            GridBagConstraints constraintsControlPointOffsetSpinner = new GridBagConstraints();
            constraintsControlPointOffsetSpinner.gridx = 3;
            getControlSettingsPanel().add(getControlPointOffsetSpinner(), constraintsControlPointOffsetSpinner);
            
            GridBagConstraints constraintsControlInhibitCheckBox = new GridBagConstraints();
            constraintsControlInhibitCheckBox.gridy = 1;
            constraintsControlInhibitCheckBox.gridwidth = GridBagConstraints.REMAINDER;
            constraintsControlInhibitCheckBox.anchor = GridBagConstraints.LINE_START;
            constraintsControlInhibitCheckBox.insets = new java.awt.Insets(4, 4, 0, 0);
            getControlSettingsPanel().add(getControlInhibitCheckBox(), constraintsControlInhibitCheckBox);
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjControlSettingsPanel;
}
private JComboBox<String> getControlTypeComboBox() {
    if (ivjControlTypeComboBox == null) {
        try {
            ivjControlTypeComboBox = new JComboBox<String>();
            ivjControlTypeComboBox.setName("ControlTypeComboBox");
            ivjControlTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));

            ivjControlTypeComboBox.addItem( AnalogControlType.NONE.getControlName() );
            ivjControlTypeComboBox.addItem( AnalogControlType.NORMAL.getControlName() );
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjControlTypeComboBox;
}
private javax.swing.JLabel getControlTypeLabel() {
    if (ivjControlTypeLabel == null) {
        try {
            ivjControlTypeLabel = new javax.swing.JLabel();
            ivjControlTypeLabel.setName("ControlTypeLabel");
            ivjControlTypeLabel.setText("Control Type:");
            ivjControlTypeLabel.setMaximumSize(new java.awt.Dimension(86, 23));
            ivjControlTypeLabel.setPreferredSize(new java.awt.Dimension(86, 23));
            ivjControlTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjControlTypeLabel.setMinimumSize(new java.awt.Dimension(86, 23));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjControlTypeLabel;
}
/**
 * Return the DataOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDataOffsetLabel() {
	if (ivjDataOffsetLabel == null) {
		try {
			ivjDataOffsetLabel = new javax.swing.JLabel();
			ivjDataOffsetLabel.setName("DataOffsetLabel");
			ivjDataOffsetLabel.setText("Data Offset:");
			ivjDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataOffsetLabel;
}
/**
 * Return the DataOffsetTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDataOffsetTextField() {
	if (ivjDataOffsetTextField == null) {
		try {
			ivjDataOffsetTextField = new javax.swing.JTextField();
			ivjDataOffsetTextField.setName("DataOffsetTextField");
			ivjDataOffsetTextField.setColumns(17);  // -999999.999999999
			// user code begin {1}

			ivjDataOffsetTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.9999999, 999999.999999999, 9) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDataOffsetTextField;
}
/**
 * Return the DeadbandCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getDeadbandCheckBox() {
	if (ivjDeadbandCheckBox == null) {
		try {
			ivjDeadbandCheckBox = new javax.swing.JCheckBox();
			ivjDeadbandCheckBox.setName("DeadbandCheckBox");
			ivjDeadbandCheckBox.setSelected(true);
			ivjDeadbandCheckBox.setText("Enable");
			ivjDeadbandCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeadbandCheckBox;
}
/**
 * Return the DeadbandPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDeadbandPanel() {
	if (ivjDeadbandPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Deadband");
			ivjDeadbandPanel = new javax.swing.JPanel();
			ivjDeadbandPanel.setName("DeadbandPanel");
			ivjDeadbandPanel.setBorder(ivjLocalBorder);
			ivjDeadbandPanel.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints constraintsDeadbandCheckBox = new GridBagConstraints();
			constraintsDeadbandCheckBox.gridx = 1; constraintsDeadbandCheckBox.gridy = 1;
			getDeadbandPanel().add(getDeadbandCheckBox(), constraintsDeadbandCheckBox);

			GridBagConstraints constraintsDeadbandSpinner = new GridBagConstraints();
			constraintsDeadbandSpinner.gridx = 2; constraintsDeadbandSpinner.gridy = 1;
			getDeadbandPanel().add(getDeadbandSpinner(), constraintsDeadbandSpinner);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeadbandPanel;
}
/**
 * Return the DeadbandSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getDeadbandSpinner() {
	if (ivjDeadbandSpinner == null) {
		try {
			ivjDeadbandSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDeadbandSpinner.setName("DeadbandSpinner");
			ivjDeadbandSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
			ivjDeadbandSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeadbandSpinner.setBackground(java.awt.Color.white);
			ivjDeadbandSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
			// user code begin {1}
			ivjDeadbandSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(-1), new Integer(1000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			ivjDeadbandSpinner.setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(-1), new Integer(100000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(-1)));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeadbandSpinner;
}
/**
 * Return the MultiplierLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMultiplierLabel() {
	if (ivjMultiplierLabel == null) {
		try {
			ivjMultiplierLabel = new javax.swing.JLabel();
			ivjMultiplierLabel.setName("MultiplierLabel");
			ivjMultiplierLabel.setText("Multiplier:");
            ivjMultiplierLabel.setMinimumSize(ivjMultiplierLabel.getPreferredSize());
			ivjMultiplierLabel.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierLabel;
}
/**
 * Return the MultiplierTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMultiplierTextField() {
	if (ivjMultiplierTextField == null) {
		try {
			ivjMultiplierTextField = new javax.swing.JTextField();
			ivjMultiplierTextField.setName("MultiplierTextField");
			ivjMultiplierTextField.setColumns(17);  // -999999.999999999
			// user code begin {1}

			ivjMultiplierTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999999, 999999.999999999, 9) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierTextField;
}
/**
 * Return the PhysicalPointOffsetCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPhysicalPointOffsetCheckBox() {
	if (ivjPhysicalPointOffsetCheckBox == null) {
		try {
			ivjPhysicalPointOffsetCheckBox = new javax.swing.JCheckBox();
			ivjPhysicalPointOffsetCheckBox.setName("PhysicalPointOffsetCheckBox");
			ivjPhysicalPointOffsetCheckBox.setSelected(true);
			ivjPhysicalPointOffsetCheckBox.setText("Physical Point Offset");
			ivjPhysicalPointOffsetCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalPointOffsetCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPointOffsetCheckBox;
}
/**
 * Return the PointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointOffsetLabel() {
	if (ivjPointOffsetLabel == null) {
		try {
			ivjPointOffsetLabel = new javax.swing.JLabel();
			ivjPointOffsetLabel.setName("PointOffsetLabel");
			ivjPointOffsetLabel.setText("Point Offset:");
			ivjPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetLabel;
}
/**
 * Return the PointOffsetField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPointOffsetSpinner() {
	if (ivjPointOffsetSpinner == null) {
		try {
			ivjPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPointOffsetSpinner.setName("PointOffsetSpinner");
			ivjPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
			ivjPointOffsetSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointOffsetSpinner.setBackground(java.awt.Color.white);
			ivjPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
			// user code begin {1}
			ivjPointOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetSpinner;
}
/**
 * Return the RawValuePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRawValuePanel() {
	if (ivjRawValuePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder1.setTitle("Raw Value");
			ivjRawValuePanel = new javax.swing.JPanel();
			ivjRawValuePanel.setName("RawValuePanel");
			ivjRawValuePanel.setBorder(ivjLocalBorder1);
			ivjRawValuePanel.setLayout(new java.awt.GridBagLayout());

			GridBagConstraints constraintsMultiplierLabel = new GridBagConstraints();
			constraintsMultiplierLabel.gridx = 0; constraintsMultiplierLabel.gridy = 0;
			constraintsMultiplierLabel.anchor = GridBagConstraints.LINE_START;
			constraintsMultiplierLabel.insets = new Insets(0, 4, 4, 4);
			getRawValuePanel().add(getMultiplierLabel(), constraintsMultiplierLabel);

			GridBagConstraints constraintsMultiplierTextField = new GridBagConstraints();
			constraintsMultiplierTextField.gridx = 1; constraintsMultiplierTextField.gridy = 0;
			constraintsMultiplierTextField.anchor = GridBagConstraints.LINE_START;
			constraintsMultiplierTextField.insets = new Insets(0, 0, 4, 8);
			getRawValuePanel().add(getMultiplierTextField(), constraintsMultiplierTextField);

			GridBagConstraints constraintsDataOffsetLabel = new GridBagConstraints();
			constraintsDataOffsetLabel.gridx = 0; constraintsDataOffsetLabel.gridy = 1;
			constraintsDataOffsetLabel.anchor = GridBagConstraints.LINE_START;
			constraintsDataOffsetLabel.insets = new Insets(4, 4, 0, 4);
			getRawValuePanel().add(getDataOffsetLabel(), constraintsDataOffsetLabel);

			GridBagConstraints constraintsDataOffsetTextField = new GridBagConstraints();
			constraintsDataOffsetTextField.gridx = 1; constraintsDataOffsetTextField.gridy = 1;
			constraintsDataOffsetTextField.anchor = GridBagConstraints.LINE_START;
			constraintsDataOffsetTextField.insets = new Insets(4, 0, 0, 8);
			getRawValuePanel().add(getDataOffsetTextField(), constraintsDataOffsetTextField);

			GridBagConstraints constraintsPanel = new GridBagConstraints();
	        constraintsPanel.fill = GridBagConstraints.HORIZONTAL;
	        constraintsPanel.weightx = 1.0;
            constraintsPanel.gridy = 0;
            constraintsPanel.gridx = GridBagConstraints.RELATIVE;
	        getRawValuePanel().add(new javax.swing.JPanel(), constraintsPanel);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjRawValuePanel;
}
/**
 * Return the InvalidPointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsedPointOffsetLabel() {
	if (ivjUsedPointOffsetLabel == null) {
		try {
			ivjUsedPointOffsetLabel = new javax.swing.JLabel();
			ivjUsedPointOffsetLabel.setName("UsedPointOffsetLabel");
			ivjUsedPointOffsetLabel.setText("Offset Used");
			ivjUsedPointOffsetLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsedPointOffsetLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@Override
public Object getValue(Object val) 
{
	Integer pointOffset = null;
	Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
	if( pointOffsetSpinVal instanceof Long )
		pointOffset = new Integer( ((Long)pointOffsetSpinVal).intValue() );
	else if( pointOffsetSpinVal instanceof Integer )
		pointOffset = new Integer( ((Integer)pointOffsetSpinVal).intValue() );

	Double deadband = null;
	Object deadbandSpinVal = getDeadbandSpinner().getValue();
	if( deadbandSpinVal instanceof Long )
		deadband = new Double( ((Long)deadbandSpinVal).intValue() );
	else if( deadbandSpinVal instanceof Integer )
		deadband = new Double( ((Integer)deadbandSpinVal).intValue() );
	
	Double multiplier = null;
	Double dataOffset = null;
	
	//set all the values below	
	com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;
	
	try
	{
		multiplier = new Double( getMultiplierTextField().getText() );
		point.getPointAnalog().setMultiplier(multiplier);
	}
	catch( NumberFormatException n )
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
	}

	try
	{
		dataOffset = new Double( getDataOffsetTextField().getText() );	
		point.getPointAnalog().setDataOffset(dataOffset);
	}
	catch( NumberFormatException n )
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
	}

	if ( (getUsedPointOffsetLabel().getText()) == "" )
		point.getPoint().setPointOffset( pointOffset );
	else
		point.getPoint().setPointOffset( null );

	if( getDeadbandCheckBox().isSelected() )
		point.getPointAnalog().setDeadband( deadband );
	else
		point.getPointAnalog().setDeadband(-1.0);
	
    Integer controlPointOffset = null;
    Object controlPointOffsetSpinVal = getControlPointOffsetSpinner().getValue();
    if( controlPointOffsetSpinVal instanceof Long )
        controlPointOffset = new Integer( ((Long)controlPointOffsetSpinVal).intValue() );
    else if( pointOffsetSpinVal instanceof Integer )
        controlPointOffset = new Integer( ((Integer)controlPointOffsetSpinVal).intValue() );

    point.getPointAnalogControl().setControlOffset(controlPointOffset);

    point.getPointAnalogControl().setControlType(getControlTypeComboBox().getSelectedItem().toString());

    point.getPointAnalogControl().setControlInhibited(getControlInhibitCheckBox().isSelected());

    return point;
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

	getControlPointOffsetSpinner().addValueListener( this );
	getDeadbandSpinner().addValueListener( this );
	getPointOffsetSpinner().addValueListener( this );
	
	getPhysicalPointOffsetCheckBox().addItemListener(this);
    getDeadbandCheckBox().addItemListener(this);

	getMultiplierTextField().addCaretListener(this);
	getDataOffsetTextField().addCaretListener(this);

    getControlTypeComboBox().addActionListener(this);
    getControlInhibitCheckBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointAnalogPhysicalSettingsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(399, 305);

		GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new GridBagConstraints();
		constraintsPhysicalPointOffsetCheckBox.gridx = 0; constraintsPhysicalPointOffsetCheckBox.gridy = 0;
        constraintsPhysicalPointOffsetCheckBox.gridwidth = GridBagConstraints.REMAINDER;
        constraintsPhysicalPointOffsetCheckBox.anchor = GridBagConstraints.FIRST_LINE_START;
        constraintsPhysicalPointOffsetCheckBox.insets = new Insets(16, 16, 8, 0);
		add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

        GridBagConstraints constraintsPointOffsetLabel = new GridBagConstraints();
        constraintsPointOffsetLabel.gridx = 0; constraintsPointOffsetLabel.gridy = 1;
        constraintsPointOffsetLabel.insets = new Insets(0, 20, 16, 8);
        add(getPointOffsetLabel(), constraintsPointOffsetLabel);

		GridBagConstraints constraintsPointOffsetSpinner = new GridBagConstraints();
		constraintsPointOffsetSpinner.gridx = 1; constraintsPointOffsetSpinner.gridy = 1;
		constraintsPointOffsetSpinner.insets = new Insets(0, 0, 16, 0);
		add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);

		GridBagConstraints constraintsUsedPointOffsetLabel = new GridBagConstraints();
		constraintsUsedPointOffsetLabel.gridx = 2; constraintsUsedPointOffsetLabel.gridy = 1;
		constraintsUsedPointOffsetLabel.anchor = GridBagConstraints.FIRST_LINE_START;
		constraintsUsedPointOffsetLabel.gridwidth = GridBagConstraints.REMAINDER;
        constraintsUsedPointOffsetLabel.weightx = 1.0;
        constraintsUsedPointOffsetLabel.fill = GridBagConstraints.HORIZONTAL;
		add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);

		GridBagConstraints constraintsDeadbandPanel = new GridBagConstraints();
		constraintsDeadbandPanel.gridx = 0; constraintsDeadbandPanel.gridy = 2;
        constraintsDeadbandPanel.gridwidth = GridBagConstraints.REMAINDER;
        constraintsDeadbandPanel.fill = GridBagConstraints.HORIZONTAL;
        constraintsDeadbandPanel.weightx = 1.0;
        constraintsDeadbandPanel.insets = new Insets(0, 16, 16, 16);
        constraintsDeadbandPanel.ipadx = 16;
        constraintsDeadbandPanel.ipady = 16;
		add(getDeadbandPanel(), constraintsDeadbandPanel);

		GridBagConstraints constraintsRawValuePanel = new GridBagConstraints();
		constraintsRawValuePanel.gridx = 0; constraintsRawValuePanel.gridy = 3;
		constraintsRawValuePanel.gridwidth = GridBagConstraints.REMAINDER;
        constraintsRawValuePanel.fill = GridBagConstraints.HORIZONTAL;
        constraintsRawValuePanel.weightx = 1.0;
        constraintsRawValuePanel.insets = new Insets(0, 16, 16, 16);
        constraintsRawValuePanel.ipadx = 16;
        constraintsRawValuePanel.ipady = 16;
		add(getRawValuePanel(), constraintsRawValuePanel);

        GridBagConstraints constraintsControlPanel = new GridBagConstraints();
        constraintsControlPanel.gridx = 0; constraintsControlPanel.gridy = 4;
        constraintsControlPanel.gridwidth = GridBagConstraints.REMAINDER;
        constraintsControlPanel.fill = GridBagConstraints.HORIZONTAL;
        constraintsControlPanel.weightx = 1.0;
        constraintsControlPanel.insets = new Insets(0, 16, 16, 16);
        constraintsControlPanel.ipadx = 16;
        constraintsControlPanel.ipady = 16;
        add(getControlSettingsPanel(), constraintsControlPanel);
        
        GridBagConstraints constraintsPanel = new GridBagConstraints();
        constraintsPanel.fill = GridBagConstraints.VERTICAL;
        constraintsPanel.weighty = 1.0;
        constraintsPanel.gridy = GridBagConstraints.RELATIVE;
        add(new javax.swing.JPanel(), constraintsPanel);
        
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Helper method to determine if the pointOffset is already in use
 * @param pointOffset - Offset to check
 * @return - True if offset is used by another point
 */
private boolean isPointOffsetInUse(int pointOffset) {

    if (this.usedPointOffsetsVector != null && this.usedPointOffsetsVector.size() > 0) {

        for (LitePoint point : this.usedPointOffsetsVector) {

            if (point.getPointOffset() == pointOffset) {
                return true;
            }
        }
    }

    return false;

}

/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
@Override
public boolean isInputValid() 
{
	if( getMultiplierTextField().getText() != null
		 && getMultiplierTextField().getText().length() >= 1 )
	{
		try
		{
			Double.parseDouble( getMultiplierTextField().getText() );
		}
		catch( NumberFormatException e )
		{
			setErrorString("The Multiplier text field must contain a valid number");
			return false;
		}

	}
	else
	{
		setErrorString("The Multiplier text field must be filled in");
		return false;
	}


	if( getDataOffsetTextField().getText() != null
		 && getDataOffsetTextField().getText().length() >= 1 )
	{
		try
		{
			Double.parseDouble( getDataOffsetTextField().getText() );
		}
		catch( NumberFormatException e )
		{
			setErrorString("The Data Offset text field must contain a valid number");
			return false;
		}

	}
	else
	{
		setErrorString("The Data Offset text field must be filled in");
		return false;
	}
    
	if (getPhysicalPointOffsetCheckBox().isSelected()) {
	    Object value = this.getPointOffsetSpinner().getValue();

	    if (value instanceof Number) {
	        Number numValue = (Number)value;
	        if (this.isPointOffsetInUse(numValue.intValue())) {
	            setErrorString("Analog Point Offset " + numValue.intValue()
	                           + " is in use for this device");
	            return false;
	        }
	    }
	}

	return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPhysicalPointOffsetCheckBox()) 
		connEtoC3(e);
	if (e.getSource() == getDeadbandCheckBox()) 
		connEtoC5(e);
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
			Class<?> aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		PointAnalogPhysicalSettingsEditorPanel aPointAnalogPhysicalSettingsEditorPanel;
		aPointAnalogPhysicalSettingsEditorPanel = new PointAnalogPhysicalSettingsEditorPanel();
		frame.add("Center", aPointAnalogPhysicalSettingsEditorPanel);
		frame.setSize(aPointAnalogPhysicalSettingsEditorPanel.getSize());
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
@Override
public void setValue(Object val) 
{
	com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;
	if ( point.getPointAnalog().getMultiplier() != null )
		getMultiplierTextField().setText( point.getPointAnalog().getMultiplier().toString() );
	if ( point.getPointAnalog().getDataOffset() != null )
		getDataOffsetTextField().setText( point.getPointAnalog().getDataOffset().toString() );
	
	getUsedPointOffsetLabel().setText("");
    

        List<LitePoint> points = DaoFactory.getPointDao()
                                           .getLitePointsByPaObjectId(point.getPoint().getPaoID());
        usedPointOffsetsVector = new Vector<LitePoint>(points.size());
        for (LitePoint currPoint : points) {
            if (point.getPoint().getPointID() != currPoint.getPointID()
                    && point.getPoint()
                            .getPointType()
                            .equals(PointTypes.getType(currPoint.getPointType()))) {
                usedPointOffsetsVector.add(currPoint);
            }
        }
	
    Integer pointOffset = point.getPoint().getPointOffset();
	Double deadband = point.getPointAnalog().getDeadband();

	if( pointOffset != null )
	{
		getPhysicalPointOffsetCheckBox().setSelected(pointOffset.intValue() != 0);
		getPointOffsetSpinner().setValue( pointOffset );
	}
	else
	{
		getPhysicalPointOffsetCheckBox().setSelected(false);
		getPointOffsetSpinner().setValue( new Integer(0) );
	}

    getControlTypeComboBox().setSelectedItem(
        point.getPointAnalogControl().getControlType());

    getControlInhibitCheckBox().setSelected(
        point.getPointAnalogControl().isControlInhibited());

    if (point.getPointAnalogControl().hasControl()) {
        
        getControlPointOffsetSpinner().setValue(
            point.getPointAnalogControl().getControlOffset());

    } else {

        getControlPointOffsetSpinner().setValue(0);
    }

    if( deadband != null )
	{
		getDeadbandCheckBox().setSelected( deadband.intValue() > -1 );
		getDeadbandSpinner().setValue( new Integer(deadband.intValue()) );
		
		//Only display the Deadband panel for RTU Welco device types.
        LiteYukonPAObject pao = DaoFactory.getPaoDao().getLiteYukonPAO(point.getPoint().getPaoID());
        if (pao.getPaoType() != PaoType.RTUWELCO) {
            getDeadbandPanel().setVisible(false);
        }
	}
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
@Override
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	if (arg1.getSource() == getDeadbandSpinner())
	{
		this.fireInputUpdate();
	}

	if (arg1.getSource() == getPointOffsetSpinner())
	{
		this.fireInputUpdate();
	}
	
    if (arg1.getSource() == getControlPointOffsetSpinner())
    {
        this.fireInputUpdate();
    }
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
@Override
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}

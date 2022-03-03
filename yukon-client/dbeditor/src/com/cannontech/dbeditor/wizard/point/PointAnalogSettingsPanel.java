package com.cannontech.dbeditor.wizard.point;

import java.util.List;

import javax.swing.JLabel;

import com.cannontech.database.data.point.UnitOfMeasure;

/**
 * This type was created in VisualAge.
 */

public class PointAnalogSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjUnitOfMeasureLabel = null;
	private javax.swing.JComboBox<UnitOfMeasure> ivjUnitOfMeasureComboBox = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinFieldDecimalPlaces = null;
    private com.klg.jclass.field.JCSpinField meterDialsSpinner = null;
	private javax.swing.JLabel ivjJLabelDecimalPlaces = null;
	private javax.swing.JTextField ivjMultiplierTextField = null;
	private javax.swing.JLabel ivjMultiplierLabel = null;
	private javax.swing.JLabel ivjDataOffsetLabel = null;
	private javax.swing.JTextField ivjDataOffsetTextField = null;
    private JLabel meterDialsLabel = null;
    
public PointAnalogSettingsPanel() {
	super();
	initialize();
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getDataOffsetLabel() {
	if (ivjDataOffsetLabel == null) {
		try {
			ivjDataOffsetLabel = new javax.swing.JLabel();
			ivjDataOffsetLabel.setName("DataOffsetLabel");
			ivjDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDataOffsetLabel.setText("Data Offset:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDataOffsetLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getDataOffsetTextField() {
	if (ivjDataOffsetTextField == null) {
		try {
			ivjDataOffsetTextField = new javax.swing.JTextField();
			ivjDataOffsetTextField.setName("DataOffsetTextField");
			ivjDataOffsetTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjDataOffsetTextField.setColumns(0);
			ivjDataOffsetTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(000000.000000000, 999999.999999999, 10) );
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDataOffsetTextField;
}
/**
 * Return the JCSpinFieldDecimalPlaces property value.
 * @return com.klg.jclass.field.JCSpinField
 */
private com.klg.jclass.field.JCSpinField getJCSpinFieldDecimalPlaces() {
	if (ivjJCSpinFieldDecimalPlaces == null) {
		try {
			ivjJCSpinFieldDecimalPlaces = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinFieldDecimalPlaces.setName("JCSpinFieldDecimalPlaces");
			ivjJCSpinFieldDecimalPlaces.setMinimumSize(new java.awt.Dimension(40, 25));
            ivjJCSpinFieldDecimalPlaces.setPreferredSize(new java.awt.Dimension(40, 25));
			
			ivjJCSpinFieldDecimalPlaces.setDataProperties(new com.klg.jclass.field.DataProperties(
				new com.klg.jclass.field.validate.JCIntegerValidator(
					null, new Integer(0), new Integer(10), null, true, 
					null, new Integer(1), "#,##0.###;-#,##0.###", false, false, 
					false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(
						java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(
							true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCSpinFieldDecimalPlaces;
}

private com.klg.jclass.field.JCSpinField getMeterDialsSpinner() {
    if (meterDialsSpinner == null) {
        try {
            meterDialsSpinner = new com.klg.jclass.field.JCSpinField();
            meterDialsSpinner.setName("JCSpinFieldMeterDials");
            meterDialsSpinner.setMinimumSize(new java.awt.Dimension(40, 25));
            meterDialsSpinner.setPreferredSize(new java.awt.Dimension(40, 25));
            
            meterDialsSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(
                new com.klg.jclass.field.validate.JCIntegerValidator(
                    null, new Integer(0), new Integer(10), null, true, 
                    null, new Integer(1), "#,##0.###;-#,##0.###", false, false, 
                    false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(
                        java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(
                            true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

} catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return meterDialsSpinner;
}

/**
 * Return the JLabelDecimalPlaces property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelDecimalPlaces() {
	if (ivjJLabelDecimalPlaces == null) {
		try {
			ivjJLabelDecimalPlaces = new javax.swing.JLabel();
			ivjJLabelDecimalPlaces.setName("JLabelDecimalPlaces");
			ivjJLabelDecimalPlaces.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDecimalPlaces.setText("Decimal Places:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelDecimalPlaces;
}

private javax.swing.JLabel getMeterDialsLabel() {
    if (meterDialsLabel == null) {
        try {
            meterDialsLabel = new javax.swing.JLabel();
            meterDialsLabel.setName("JLabelMeterDials");
            meterDialsLabel.setFont(new java.awt.Font("dialog", 0, 14));
            meterDialsLabel.setText("Meter Digits (Dials):");
       } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return meterDialsLabel;
}

/**
 * Return the MultiplierTextField property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getMultiplierLabel() {
	if (ivjMultiplierLabel == null) {
		try {
			ivjMultiplierLabel = new javax.swing.JLabel();
			ivjMultiplierLabel.setName("MultiplierLabel");
			ivjMultiplierLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMultiplierLabel.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			ivjMultiplierLabel.setText("Multiplier:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMultiplierLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getMultiplierTextField() {
	if (ivjMultiplierTextField == null) {
		try {
			ivjMultiplierTextField = new javax.swing.JTextField();
			ivjMultiplierTextField.setName("MultiplierTextField");
			ivjMultiplierTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjMultiplierTextField.setColumns(0);

			ivjMultiplierTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999999, 999999.999999999, 9) );

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMultiplierTextField;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox<UnitOfMeasure> getUnitOfMeasureComboBox() {
	if (ivjUnitOfMeasureComboBox == null) {
		try {
			ivjUnitOfMeasureComboBox = new javax.swing.JComboBox<>();
			ivjUnitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
			ivjUnitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureComboBox.setMaximumRowCount(6);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureComboBox;
}
/**
 * Return the UnitOfMeasureLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUnitOfMeasureLabel() {
	if (ivjUnitOfMeasureLabel == null) {
		try {
			ivjUnitOfMeasureLabel = new javax.swing.JLabel();
			ivjUnitOfMeasureLabel.setName("UnitOfMeasureLabel");
			ivjUnitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureLabel.setText("Unit Of Measure:  ");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@Override
public Object getValue(Object val)
{
	//Assuming commonObject is AnalogPoint (real or not)
	Double multiplier = null;
	Double dataOffset = null;

	try
	{
		multiplier = new Double(getMultiplierTextField().getText());
		dataOffset = new Double(getDataOffsetTextField().getText());
	}
	catch (NumberFormatException n)
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		multiplier = new Double(1.0);
		dataOffset = new Double(0.0);
	}

	com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;

	int uOfMeasureID = ((UnitOfMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getId();

	point.getPointUnit().setUomID(uOfMeasureID);
	point.getPoint().setStateGroupID(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG);

	point.getPointUnit().setDecimalPlaces(((Number) getJCSpinFieldDecimalPlaces().getValue()).intValue());
    point.getPointUnit().setMeterDials(((Number) getMeterDialsSpinner().getValue()).intValue());
	point.getPointAnalog().setMultiplier(multiplier);
	point.getPointAnalog().setDataOffset(dataOffset);
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
		setName("PointAnalogSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 300));
        setMinimumSize(new java.awt.Dimension(350, 300));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 300);

		java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureLabel.gridx = 1; constraintsUnitOfMeasureLabel.gridy = 1;
		constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(5,5,5,5);
		add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

		java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureComboBox.gridx = 2; constraintsUnitOfMeasureComboBox.gridy = 1;
		constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(5,5,5,5);
		add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

		java.awt.GridBagConstraints constraintsJLabelDecimalPlaces = new java.awt.GridBagConstraints();
		constraintsJLabelDecimalPlaces.gridx = 1; constraintsJLabelDecimalPlaces.gridy = 2;
		constraintsJLabelDecimalPlaces.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelDecimalPlaces.insets = new java.awt.Insets(5, 5,5,5);
		add(getJLabelDecimalPlaces(), constraintsJLabelDecimalPlaces);

		java.awt.GridBagConstraints constraintsJCSpinFieldDecimalPlaces = new java.awt.GridBagConstraints();
		constraintsJCSpinFieldDecimalPlaces.gridx = 2; constraintsJCSpinFieldDecimalPlaces.gridy = 2;
		constraintsJCSpinFieldDecimalPlaces.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinFieldDecimalPlaces.insets = new java.awt.Insets(5,5,5,5);
		add(getJCSpinFieldDecimalPlaces(), constraintsJCSpinFieldDecimalPlaces);

		java.awt.GridBagConstraints constraintsMultiplierLabel = new java.awt.GridBagConstraints();
		constraintsMultiplierLabel.gridx = 1; constraintsMultiplierLabel.gridy = 3;
		constraintsMultiplierLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMultiplierLabel.insets = new java.awt.Insets(5,5,5,5);
		add(getMultiplierLabel(), constraintsMultiplierLabel);

		java.awt.GridBagConstraints constraintsMultiplierTextField = new java.awt.GridBagConstraints();
		constraintsMultiplierTextField.gridx = 2; constraintsMultiplierTextField.gridy = 3;
		constraintsMultiplierTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMultiplierTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsMultiplierTextField.insets = new java.awt.Insets(5,5,5,5);
		add(getMultiplierTextField(), constraintsMultiplierTextField);

		java.awt.GridBagConstraints constraintsDataOffsetLabel = new java.awt.GridBagConstraints();
		constraintsDataOffsetLabel.gridx = 1; constraintsDataOffsetLabel.gridy = 4;
		constraintsDataOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDataOffsetLabel.insets = new java.awt.Insets(5,5,5,5);
		add(getDataOffsetLabel(), constraintsDataOffsetLabel);

		java.awt.GridBagConstraints constraintsDataOffsetTextField = new java.awt.GridBagConstraints();
		constraintsDataOffsetTextField.gridx = 2; constraintsDataOffsetTextField.gridy = 4;
		constraintsDataOffsetTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDataOffsetTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDataOffsetTextField.insets = new java.awt.Insets(5,5,5,5);
		add(getDataOffsetTextField(), constraintsDataOffsetTextField);
        
        java.awt.GridBagConstraints constraintsMeterDigitsLabel = new java.awt.GridBagConstraints();
        constraintsMeterDigitsLabel.gridx = 1; constraintsMeterDigitsLabel.gridy = 5;
        constraintsMeterDigitsLabel.anchor = java.awt.GridBagConstraints.WEST;
        constraintsMeterDigitsLabel.insets = new java.awt.Insets(5,5,5,5);
        add(getMeterDialsLabel(), constraintsMeterDigitsLabel);

        java.awt.GridBagConstraints constraintsMeterDigitsSpinner = new java.awt.GridBagConstraints();
        constraintsMeterDigitsSpinner.gridx = 2; constraintsMeterDigitsSpinner.gridy = 5;
        constraintsMeterDigitsSpinner.anchor = java.awt.GridBagConstraints.WEST;
        constraintsMeterDigitsSpinner.insets = new java.awt.Insets(5,5,5,5);
        add(getMeterDialsSpinner(), constraintsMeterDigitsSpinner);
        
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}

    List<UnitOfMeasure> unitMeasures = UnitOfMeasure.allValidValues();
    for (UnitOfMeasure um : unitMeasures) {
        getUnitOfMeasureComboBox().addItem(um);
    }
    getUnitOfMeasureComboBox().setSelectedItem(UnitOfMeasure.KW);
		
	getMultiplierTextField().setText("1.0");
	getDataOffsetTextField().setText("0.0");
	getJCSpinFieldDecimalPlaces().getDataProperties().getValueModel().setValue(new Integer (com.cannontech.dbeditor.DatabaseEditor.getDecimalPlaces()));
    getJCSpinFieldDecimalPlaces().getDataProperties().getValueModel().setValue(new Integer ( 0 ));
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		PointAnalogSettingsPanel aPointAnalogSettingsPanel;
		aPointAnalogSettingsPanel = new PointAnalogSettingsPanel();
		frame.add("Center", aPointAnalogSettingsPanel);
		frame.setSize(aPointAnalogSettingsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) {
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
            getUnitOfMeasureComboBox().requestFocus(); 
        } 
    });    
}

}

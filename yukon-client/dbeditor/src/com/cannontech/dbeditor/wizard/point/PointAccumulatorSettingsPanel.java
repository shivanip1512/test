package com.cannontech.dbeditor.wizard.point;

import java.util.List;

import javax.swing.JLabel;

import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */

public class PointAccumulatorSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjDataOffsetLabel = null;
	private javax.swing.JLabel ivjMultiplierLabel = null;
	private javax.swing.JComboBox ivjUnitOfMeasureComboBox = null;
	private javax.swing.JLabel ivjUnitOfMeasureLabel = null;
	private javax.swing.JTextField ivjDataOffsetTextField = null;
	private javax.swing.JTextField ivjMultiplierTextField = null;
	private javax.swing.JRadioButton ivjDemandReadingRadioButton = null;
	private javax.swing.JRadioButton ivjDialReadingRadioButton = null;
	private javax.swing.ButtonGroup buttonGroup = null;
	private javax.swing.JPanel ivjReadingPanel = null;
    private com.klg.jclass.field.JCSpinField meterDialsSpinner = null;
    private JLabel meterDialsLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointAccumulatorSettingsPanel() {
	super();
	initialize();
}
/**
 * connEtoM1:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getDemandReadingRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getDialReadingRadioButton());
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
 * Creation date: (4/30/2001 3:14:26 PM)
 */
public PointType getAccumulatorPointType() 
{

	if(getDialReadingRadioButton().isSelected() )
	{
		return PointType.PulseAccumulator;
	}
	else
	{
		return PointType.DemandAccumulator;	
	}
	
}
/**
 * Return the ButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup() {
	if (buttonGroup == null) {
		try {
			buttonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return buttonGroup;
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
			ivjDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDataOffsetLabel.setText("Data Offset:  ");
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
			ivjDataOffsetTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjDataOffsetTextField.setColumns(0);
			ivjDataOffsetTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(000000.000000000, 999999.999999999, 10) );
			// user code begin {1}

			getDataOffsetTextField().setText( (new Double(0.0)).toString() );
			
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
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDemandReadingRadioButton() {
	if (ivjDemandReadingRadioButton == null) {
		try {
			ivjDemandReadingRadioButton = new javax.swing.JRadioButton();
			ivjDemandReadingRadioButton.setName("DemandReadingRadioButton");
			ivjDemandReadingRadioButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDemandReadingRadioButton.setText("Demand");
			ivjDemandReadingRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			ivjDemandReadingRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandReadingRadioButton;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDialReadingRadioButton() {
	if (ivjDialReadingRadioButton == null) {
		try {
			ivjDialReadingRadioButton = new javax.swing.JRadioButton();
			ivjDialReadingRadioButton.setName("DialReadingRadioButton");
			ivjDialReadingRadioButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDialReadingRadioButton.setText("Dial");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDialReadingRadioButton;
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
			ivjMultiplierLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMultiplierLabel.setText("Multiplier:  ");
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
			ivjMultiplierTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjMultiplierTextField.setColumns(0);
			// user code begin {1}

			ivjMultiplierTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999999, 999999.999999999, 9) );

			getMultiplierTextField().setText( (new Double(1.0)).toString() );

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
 * Return the ReadingPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getReadingPanel() {
	if (ivjReadingPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Reading Type");
			ivjReadingPanel = new javax.swing.JPanel();
			ivjReadingPanel.setName("ReadingPanel");
			ivjReadingPanel.setBorder(ivjLocalBorder);
			ivjReadingPanel.setLayout(new java.awt.GridBagLayout());
			ivjReadingPanel.setFont(new java.awt.Font("dialog", 0, 12));

			java.awt.GridBagConstraints constraintsDialReadingRadioButton = new java.awt.GridBagConstraints();
			constraintsDialReadingRadioButton.gridx = 1; constraintsDialReadingRadioButton.gridy = 1;
			constraintsDialReadingRadioButton.insets = new java.awt.Insets(0, 12, 6, 2);
			getReadingPanel().add(getDialReadingRadioButton(), constraintsDialReadingRadioButton);

			java.awt.GridBagConstraints constraintsDemandReadingRadioButton = new java.awt.GridBagConstraints();
			constraintsDemandReadingRadioButton.gridx = 3; constraintsDemandReadingRadioButton.gridy = 1;
			constraintsDemandReadingRadioButton.insets = new java.awt.Insets(0, 3, 6, 6);
			getReadingPanel().add(getDemandReadingRadioButton(), constraintsDemandReadingRadioButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReadingPanel;
}
/**
 * Return the UnitOfMeasureComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getUnitOfMeasureComboBox() {
	if (ivjUnitOfMeasureComboBox == null) {
		try {
			ivjUnitOfMeasureComboBox = new javax.swing.JComboBox();
			ivjUnitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
			ivjUnitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureComboBox.setMaximumRowCount(6);
			// user code begin {1}
            
			//Add units of measure to the Unit of Measure combo box
            List<LiteUnitMeasure> unitMeasures = 
                 YukonSpringHook.getBean(UnitMeasureDao.class).getLiteUnitMeasures();
            for (LiteUnitMeasure lum : unitMeasures) {
                getUnitOfMeasureComboBox().addItem(lum);
            }			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureComboBox;
}
/**
 * Return the UnitOfMeasureLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUnitOfMeasureLabel() {
	if (ivjUnitOfMeasureLabel == null) {
		try {
			ivjUnitOfMeasureLabel = new javax.swing.JLabel();
			ivjUnitOfMeasureLabel.setName("UnitOfMeasureLabel");
			ivjUnitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureLabel.setText("Unit Of Measure:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
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
public Object getValue(Object val)
{
	//Assuming commonObject is an AccumulatorPoint
	com.cannontech.database.data.point.AccumulatorPoint point = (com.cannontech.database.data.point.AccumulatorPoint) val;

	if( getAccumulatorPointType() == PointType.PulseAccumulator)
		point.getPoint().setPointTypeEnum(PointType.PulseAccumulator);
	else
		point.getPoint().setPointTypeEnum(PointType.DemandAccumulator);
	
	//Make sure the text in correct format
	Double multiplier = null;
	Double dataOffset = null;

	try
	{
		multiplier = new Double(getMultiplierTextField().getText());
	}
	catch (NumberFormatException n)
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		multiplier = new Double(1.0);
	}

	try
	{
		dataOffset = new Double(getDataOffsetTextField().getText());
	}
	catch (NumberFormatException n)
	{
		com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
		dataOffset = new Double(0.0);
	}

	int uOfMeasureID =
		((com.cannontech.database.data.lite.LiteUnitMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getUomID();


	point.getPointAccumulator().setDataOffset(dataOffset);
	point.getPointAccumulator().setMultiplier(multiplier);
	point.getPointUnit().setUomID( new Integer(uOfMeasureID) );
	point.getPointUnit().setDecimalPlaces(new Integer(com.cannontech.dbeditor.DatabaseEditor.getDecimalPlaces()));
	point.getPointUnit().setMeterDials(new Integer(((Number) getMeterDialsSpinner().getValue()).intValue()));
	point.getPoint().setStateGroupID(new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG));

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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		connEtoM1();
		connEtoM2();
		// user code end
		setName("PointAccumulatorSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(445, 214);

		java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureLabel.gridx = 1; constraintsUnitOfMeasureLabel.gridy = 1;
		constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(33, 38, 10, 5);
		add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

		java.awt.GridBagConstraints constraintsMultiplierLabel = new java.awt.GridBagConstraints();
		constraintsMultiplierLabel.gridx = 1; constraintsMultiplierLabel.gridy = 2;
		constraintsMultiplierLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsMultiplierLabel.ipadx = 46;
		constraintsMultiplierLabel.insets = new java.awt.Insets(7, 38, 7, 5);
		add(getMultiplierLabel(), constraintsMultiplierLabel);

		java.awt.GridBagConstraints constraintsDataOffsetLabel = new java.awt.GridBagConstraints();
		constraintsDataOffsetLabel.gridx = 1; constraintsDataOffsetLabel.gridy = 3;
		constraintsDataOffsetLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsDataOffsetLabel.ipadx = 30;
		constraintsDataOffsetLabel.insets = new java.awt.Insets(7, 38, 4, 5);
		add(getDataOffsetLabel(), constraintsDataOffsetLabel);

		java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
		constraintsUnitOfMeasureComboBox.gridx = 2; constraintsUnitOfMeasureComboBox.gridy = 1;
		constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsUnitOfMeasureComboBox.weightx = 1.0;
		constraintsUnitOfMeasureComboBox.ipadx = 21;
		constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(29, 5, 5, 136);
		add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

		java.awt.GridBagConstraints constraintsMultiplierTextField = new java.awt.GridBagConstraints();
		constraintsMultiplierTextField.gridx = 2; constraintsMultiplierTextField.gridy = 2;
		constraintsMultiplierTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsMultiplierTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsMultiplierTextField.weightx = 1.0;
		constraintsMultiplierTextField.ipadx = 143;
		constraintsMultiplierTextField.insets = new java.awt.Insets(5, 5, 5, 136);
		add(getMultiplierTextField(), constraintsMultiplierTextField);

		java.awt.GridBagConstraints constraintsDataOffsetTextField = new java.awt.GridBagConstraints();
		constraintsDataOffsetTextField.gridx = 2; constraintsDataOffsetTextField.gridy = 3;
		constraintsDataOffsetTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDataOffsetTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsDataOffsetTextField.weightx = 1.0;
		constraintsDataOffsetTextField.ipadx = 143;
		constraintsDataOffsetTextField.insets = new java.awt.Insets(5, 5, 2, 136);
		add(getDataOffsetTextField(), constraintsDataOffsetTextField);

        java.awt.GridBagConstraints constraintsMeterDigitsLabel = new java.awt.GridBagConstraints();
        constraintsMeterDigitsLabel.gridx = 1; constraintsMeterDigitsLabel.gridy = 4;
        constraintsMeterDigitsLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
        constraintsMeterDigitsLabel.insets = new java.awt.Insets(7, 38, 4, 5);
        add(getMeterDialsLabel(), constraintsMeterDigitsLabel);

        java.awt.GridBagConstraints constraintsMeterDigitsSpinner = new java.awt.GridBagConstraints();
        constraintsMeterDigitsSpinner.gridx = 2; constraintsMeterDigitsSpinner.gridy = 4;
        constraintsMeterDigitsSpinner.anchor = java.awt.GridBagConstraints.WEST;
        constraintsMeterDigitsSpinner.insets = new java.awt.Insets(5, 5, 5, 136);
        add(getMeterDialsSpinner(), constraintsMeterDigitsSpinner);
        
		java.awt.GridBagConstraints constraintsReadingPanel = new java.awt.GridBagConstraints();
		constraintsReadingPanel.gridx = 1; constraintsReadingPanel.gridy = 5;
		constraintsReadingPanel.gridwidth = 2;
		constraintsReadingPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsReadingPanel.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsReadingPanel.weightx = 1.0;
		constraintsReadingPanel.weighty = 1.0;
		constraintsReadingPanel.ipadx = 120;
		constraintsReadingPanel.insets = new java.awt.Insets(3, 38, 30, 137);
		add(getReadingPanel(), constraintsReadingPanel);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getDialReadingRadioButton().setSelected(true);
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
		PointAccumulatorSettingsPanel aPointAccumulatorSettingsPanel;
		aPointAccumulatorSettingsPanel = new PointAccumulatorSettingsPanel();
		frame.add("Center", aPointAccumulatorSettingsPanel);
		frame.setSize(aPointAccumulatorSettingsPanel.getSize());
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
public void setValue(Object val) {
}

public void setFirstFocus() {
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getUnitOfMeasureComboBox().requestFocus(); 
        } 
    });    
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

}


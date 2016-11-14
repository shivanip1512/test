package com.cannontech.dbeditor.editor.point;

import java.util.List;
import java.util.Vector;

import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.spring.YukonSpringHook;


/**
 * This type was created in VisualAge.
 */

public class PointAccumulatorPhysicalSettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener, com.klg.jclass.util.value.JCValueListener
{
    private javax.swing.JLabel ivjPointOffsetLabel = null;
    private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
    private javax.swing.JCheckBox ivjPhysicalPointOffsetCheckBox = null;
    private javax.swing.JLabel ivjUsedPointOffsetLabel = null;
    private javax.swing.JLabel ivjDataOffsetLabel = null;
    private javax.swing.JTextField ivjDataOffsetTextField = null;
    private javax.swing.JLabel ivjMultiplierLabel = null;
    private javax.swing.JTextField ivjMultiplierTextField = null;
    private javax.swing.JPanel ivjRawValuePanel = null;
    private Vector<LitePoint> usedPointOffsetsVector = null;
    private String pointType;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointAccumulatorPhysicalSettingsEditorPanel() {
    super();
    initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
    // user code begin {1}
    // user code end
    if (e.getSource() == getMultiplierTextField()) 
        connEtoC3(e);
    if (e.getSource() == getDataOffsetTextField()) 
        connEtoC4(e);
    // user code begin {2}
    // user code end
}
/**
 * connEtoC2:  (PhysicalPointOffsetCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointAccumulatorPhysicalSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.fireInputUpdate();
        // user code begin {2}
        if ( getPhysicalPointOffsetCheckBox().isSelected() )
        {
            getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(10000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
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
 * connEtoC3:  (MultiplierTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointAccumulatorPhysicalSettingsEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (DataOffsetTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> PointAccumulatorPhysicalSettingsEditorPanel.fireInputUpdate()V)
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
            ivjDataOffsetLabel.setMaximumSize(new java.awt.Dimension(90, 16));
            ivjDataOffsetLabel.setPreferredSize(new java.awt.Dimension(80, 16));
            ivjDataOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjDataOffsetLabel.setMinimumSize(new java.awt.Dimension(80, 16));
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
            ivjDataOffsetTextField.setMinimumSize(new java.awt.Dimension(55, 21));
            ivjDataOffsetTextField.setColumns(0);
            // user code begin {1}

            ivjDataOffsetTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999, 999999.999999, 3) );

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
            ivjMultiplierLabel.setMaximumSize(new java.awt.Dimension(70, 16));
            ivjMultiplierLabel.setPreferredSize(new java.awt.Dimension(70, 16));
            ivjMultiplierLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjMultiplierLabel.setMinimumSize(new java.awt.Dimension(70, 16));
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
            ivjMultiplierTextField.setMinimumSize(new java.awt.Dimension(55, 21));
            ivjMultiplierTextField.setColumns(0);
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
            ivjPointOffsetLabel.setMaximumSize(new java.awt.Dimension(78, 16));
            ivjPointOffsetLabel.setPreferredSize(new java.awt.Dimension(78, 16));
            ivjPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjPointOffsetLabel.setMinimumSize(new java.awt.Dimension(78, 16));
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
 * Return the PointOffsetSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPointOffsetSpinner() {
    if (ivjPointOffsetSpinner == null) {
        try {
            ivjPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
            ivjPointOffsetSpinner.setName("PointOffsetSpinner");
            ivjPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
            ivjPointOffsetSpinner.setFont(new java.awt.Font("dialog", 0, 14));
            ivjPointOffsetSpinner.setBackground(java.awt.Color.white);
            ivjPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
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
            com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
            ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
            ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
            ivjLocalBorder.setTitle("Raw Value");
            ivjRawValuePanel = new javax.swing.JPanel();
            ivjRawValuePanel.setName("RawValuePanel");
            ivjRawValuePanel.setPreferredSize(new java.awt.Dimension(180, 88));
            ivjRawValuePanel.setBorder(ivjLocalBorder);
            ivjRawValuePanel.setLayout(new java.awt.GridBagLayout());
            ivjRawValuePanel.setMinimumSize(new java.awt.Dimension(180, 88));

            java.awt.GridBagConstraints constraintsMultiplierLabel = new java.awt.GridBagConstraints();
            constraintsMultiplierLabel.gridx = 1; constraintsMultiplierLabel.gridy = 1;
            constraintsMultiplierLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsMultiplierLabel.ipadx = 10;
            constraintsMultiplierLabel.insets = new java.awt.Insets(5, 14, 7, 2);
            getRawValuePanel().add(getMultiplierLabel(), constraintsMultiplierLabel);

            java.awt.GridBagConstraints constraintsMultiplierTextField = new java.awt.GridBagConstraints();
            constraintsMultiplierTextField.gridx = 2; constraintsMultiplierTextField.gridy = 1;
            constraintsMultiplierTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsMultiplierTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsMultiplierTextField.weightx = 1.0;
            constraintsMultiplierTextField.ipadx = 78;
            constraintsMultiplierTextField.ipady = -1;
            constraintsMultiplierTextField.insets = new java.awt.Insets(3, 3, 5, 19);
            getRawValuePanel().add(getMultiplierTextField(), constraintsMultiplierTextField);

            java.awt.GridBagConstraints constraintsDataOffsetLabel = new java.awt.GridBagConstraints();
            constraintsDataOffsetLabel.gridx = 1; constraintsDataOffsetLabel.gridy = 2;
            constraintsDataOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDataOffsetLabel.insets = new java.awt.Insets(7, 14, 17, 2);
            getRawValuePanel().add(getDataOffsetLabel(), constraintsDataOffsetLabel);

            java.awt.GridBagConstraints constraintsDataOffsetTextField = new java.awt.GridBagConstraints();
            constraintsDataOffsetTextField.gridx = 2; constraintsDataOffsetTextField.gridy = 2;
            constraintsDataOffsetTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsDataOffsetTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDataOffsetTextField.weightx = 1.0;
            constraintsDataOffsetTextField.ipadx = 78;
            constraintsDataOffsetTextField.ipady = -1;
            constraintsDataOffsetTextField.insets = new java.awt.Insets(5, 3, 15, 19);
            getRawValuePanel().add(getDataOffsetTextField(), constraintsDataOffsetTextField);
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjRawValuePanel;
}
/**
 * Return the InvalidOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsedPointOffsetLabel() {
    if (ivjUsedPointOffsetLabel == null) {
        try {
            ivjUsedPointOffsetLabel = new javax.swing.JLabel();
            ivjUsedPointOffsetLabel.setName("UsedPointOffsetLabel");
            ivjUsedPointOffsetLabel.setText("Offset Used");
            ivjUsedPointOffsetLabel.setMaximumSize(new java.awt.Dimension(180, 20));
            ivjUsedPointOffsetLabel.setPreferredSize(new java.awt.Dimension(180, 20));
            ivjUsedPointOffsetLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
            ivjUsedPointOffsetLabel.setMinimumSize(new java.awt.Dimension(180, 20));
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
public Object getValue(Object val) 
{
    //Assume that commonObject is an instance of com.cannontech.database.data.point.AccumulatorPoint
    com.cannontech.database.data.point.AccumulatorPoint point = (com.cannontech.database.data.point.AccumulatorPoint) val;

    Integer pointOffset = null;
    Double multiplier = null;
    Double dataOffset = null;    

    Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
    if( pointOffsetSpinVal instanceof Long )
        pointOffset = new Integer( ((Long)pointOffsetSpinVal).intValue() );
    else if( pointOffsetSpinVal instanceof Integer )
        pointOffset = new Integer( ((Integer)pointOffsetSpinVal).intValue() );

    if ( (getUsedPointOffsetLabel().getText()) == "" )
        point.getPoint().setPointOffset( pointOffset );
    else
        point.getPoint().setPointOffset( null );

    try
    {
        multiplier = new Double( getMultiplierTextField().getText() );
        point.getPointAccumulator().setMultiplier(multiplier);
    }
    catch( NumberFormatException n )
    {
        com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
        point.getPointAccumulator().setMultiplier(new Double(1.0));
    }

    try
    {
        dataOffset = new Double( getDataOffsetTextField().getText() );    
        point.getPointAccumulator().setDataOffset(dataOffset);
    }
    catch( NumberFormatException n )
    {
        com.cannontech.clientutils.CTILogger.error( n.getMessage(), n );
        point.getPointAccumulator().setMultiplier(new Double(0.0));
    }

/*    if (pointOffset.intValue() == 0)
        point.getPoint().setPseudoFlag( new Character('P') );
    else
        point.getPoint().setPseudoFlag( new Character('R') );
*/
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
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
    // user code begin {1}

    getPointOffsetSpinner().addValueListener(this);
    
    // user code end
    getPhysicalPointOffsetCheckBox().addItemListener(this);
    getMultiplierTextField().addCaretListener(this);
    getDataOffsetTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
    try {
        // user code begin {1}
        // user code end
        setName("PointAccumulatorPhysicalSettingsEditorPanel");
        setLayout(new java.awt.GridBagLayout());
        setSize(350, 213);

        java.awt.GridBagConstraints constraintsPointOffsetLabel = new java.awt.GridBagConstraints();
        constraintsPointOffsetLabel.gridx = 1; constraintsPointOffsetLabel.gridy = 2;
        constraintsPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
        constraintsPointOffsetLabel.insets = new java.awt.Insets(6, 8, 7, 10);
        add(getPointOffsetLabel(), constraintsPointOffsetLabel);

        java.awt.GridBagConstraints constraintsPointOffsetSpinner = new java.awt.GridBagConstraints();
        constraintsPointOffsetSpinner.gridx = 2; constraintsPointOffsetSpinner.gridy = 2;
        constraintsPointOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
        constraintsPointOffsetSpinner.insets = new java.awt.Insets(3, 10, 4, 2);
        add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);

        java.awt.GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new java.awt.GridBagConstraints();
        constraintsPhysicalPointOffsetCheckBox.gridx = 1; constraintsPhysicalPointOffsetCheckBox.gridy = 1;
        constraintsPhysicalPointOffsetCheckBox.gridwidth = 2;
        constraintsPhysicalPointOffsetCheckBox.anchor = java.awt.GridBagConstraints.WEST;
        constraintsPhysicalPointOffsetCheckBox.insets = new java.awt.Insets(42, 8, 2, 7);
        add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

        java.awt.GridBagConstraints constraintsUsedPointOffsetLabel = new java.awt.GridBagConstraints();
        constraintsUsedPointOffsetLabel.gridx = 3; constraintsUsedPointOffsetLabel.gridy = 2;
        constraintsUsedPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
        constraintsUsedPointOffsetLabel.insets = new java.awt.Insets(4, 3, 5, 9);
        add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);

        java.awt.GridBagConstraints constraintsRawValuePanel = new java.awt.GridBagConstraints();
        constraintsRawValuePanel.gridx = 1; constraintsRawValuePanel.gridy = 3;
        constraintsRawValuePanel.gridwidth = 3;
        constraintsRawValuePanel.fill = java.awt.GridBagConstraints.BOTH;
        constraintsRawValuePanel.anchor = java.awt.GridBagConstraints.WEST;
        constraintsRawValuePanel.weightx = 1.0;
        constraintsRawValuePanel.weighty = 1.0;
        constraintsRawValuePanel.ipadx = 71;
        constraintsRawValuePanel.insets = new java.awt.Insets(5, 8, 25, 91);
        add(getRawValuePanel(), constraintsRawValuePanel);
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
            setErrorString("The value entered for the Multiplier text field must be a valid number");
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
            setErrorString("The value entered for the Data Offset text field must be a valid number");
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
                String pointTypeString = (pointType != null) ? pointType + " " : "";
                setErrorString( pointTypeString + "Point Offset "
                                + numValue.intValue() + " is in use for this device");
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
public void itemStateChanged(java.awt.event.ItemEvent e) {
    // user code begin {1}
    // user code end
    if (e.getSource() == getPhysicalPointOffsetCheckBox()) 
        connEtoC2(e);
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
        PointAccumulatorPhysicalSettingsEditorPanel aPointAccumulatorPhysicalSettingsEditorPanel;
        aPointAccumulatorPhysicalSettingsEditorPanel = new PointAccumulatorPhysicalSettingsEditorPanel();
        frame.add("Center", aPointAccumulatorPhysicalSettingsEditorPanel);
        frame.setSize(aPointAccumulatorPhysicalSettingsEditorPanel.getSize());
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
    //Assume that defaultObject is an instance of com.cannontech.database.data.point.AccumulatorPoint
    com.cannontech.database.data.point.AccumulatorPoint point = (com.cannontech.database.data.point.AccumulatorPoint) val;

    getUsedPointOffsetLabel().setText("");
//    usedPointOffsetsVector = new java.util.Vector();
    Integer pointOffset = point.getPoint().getPointOffset();

    if( pointOffset != null )
    {
        if( pointOffset.intValue() > 0)
        {
            getPhysicalPointOffsetCheckBox().setSelected(true);
            getPointOffsetSpinner().setValue( pointOffset );
        }
        else
        {
            getPhysicalPointOffsetCheckBox().setSelected(false);
            getPointOffsetSpinner().setValue( new Integer(0) );
        }
    }
    else
    {
        getPhysicalPointOffsetCheckBox().setSelected(false);
        getPointOffsetSpinner().setValue( new Integer(0) );
    }
    
    List<LitePoint> points = YukonSpringHook.getBean(PointDao.class)
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
        
        pointType = point.getPoint().getPointType();
    
/*
    IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
    synchronized(cache)
    {
        java.util.List points = cache.getAllPoints();
        java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
        int pointDeviceID = point.getPoint().getPaoID().intValue();
        int pointPointType = com.cannontech.database.data.point.PointTypes.getType(point.getPoint().getPointType());
        int pointPointOffset = point.getPoint().getPointOffset().intValue();
        com.cannontech.database.data.lite.LitePoint litePoint = null;
        for (int i=0; i<points.size(); i++)
        {
            litePoint = ((com.cannontech.database.data.lite.LitePoint)points.get(i));
            if( pointDeviceID == litePoint.getPaobjectID() && pointPointType == litePoint.getPointType() )
            {
                if( (pointPointOffset != litePoint.getPointOffset()) && (pointPointOffset > 0) )
                    usedPointOffsetsVector.addElement(litePoint);
            }
            else if( litePoint.getPaobjectID() > pointDeviceID )
            {
                break;
            }
        }
    }
*/    
    if ( point.getPointAccumulator().getMultiplier() != null )
        getMultiplierTextField().setText( point.getPointAccumulator().getMultiplier().toString() );
    if ( point.getPointAccumulator().getDataOffset() != null )
        getDataOffsetTextField().setText( point.getPointAccumulator().getDataOffset().toString() );

    return;
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
    if (arg1.getSource() == getPointOffsetSpinner())
    {
        this.fireInputUpdate();
    }
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
    D0CB838494G88G88G94F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DD8D457151860CF8C0958EAC395DB2226219B1B5814FEEB3B362556740BDB5A3A7B590D1BCDD35A5A465DB57DEC176D9AE33717615728911128099A09BF09A4947995C2B091A1C48C9A2A04D2FFA20F19B74C48B0B33CF983B30032675C7B6EFDEF06B700366ED73E6F705EBD771EFBFE6E39671EFB6F9BD2F0A0A1AB5DD2C0C8FA96517E2A53895953CC487FCC7EE99F47C5494B3209616FB5009D
    643977B261998BF549DFDA161DC71E58198B6505D0EEDD38ACFBB73C6FA4DFAFCAF897DEA27089C05D4713D75CBDB61F740B1CCF8EAD9FA95D8A4F97819281074FB499791FA85D2361D3B4BC07F48BA16B6D582623F41F062B01F2AB40A200E4BBEB3F9A1E1BA9674935B5DA3B4BB393C85ABF3EE159067D987A64D0534DE8AFEB4FC272C8FA2683F9CD22F2A24DAC28CB81C065B3C8FB7DD9F8563576F43FEB757416D8A5375B63D6E52B23C436B9D50FD26275B85DEAEDEDABBE6A2DD6FF2F5FA5813E559174B9
    2D126B022C2ACEF7376FFD5A3AD5F24BAE349D653922B41772A064B41437BA915799C4B916C379A900B401FFE390712D706EG08F2F25DCF3CD1276956B17488C9FA421D31BD026EF1CE26F332D357FD16FFE34E4BF41C6AAF61F8EC023A42EB4B32AB81BAG82GF1GFB29FF1C3E78A6BC6B3B5486CFEF2F47DD6F751EF25914339E1B6C02775ADA2851F067E52BC7319142FCE0EAE93109BCE381231BFD53F44CA609955C9F961D7F82F96AAB9FDAF2CCEC72D4E229E571B5E60B472F69B6E13CFF9011775AEB
    217E9A4A7BCF4448FB6194CFC616896F249F95D9B6E97D6475683CCF7660D82F22BED08A6F691AFFB07C53949FED40B35F35D3BC16E7C15D069E6E9B9DAFF2D9DAB267103C3296D3BB44AD2B37DCF6B199D4D738ACF3295FA538984F9CA14B03943F4400E7327C0162313C926AAAFB17E5B37FBAF38567F29C1413GD2G56GE43A415FGAA5D5C47E6DEBCB6C19FEB159475145F6932498AE134E7AE6D02279CD09529596DD3A537D5EE179427546512FDC4EBF361924A5695C257C1FD5F8ABCBE105D32A229CE
    0F9BFA17FAE5D5D6FC2C0DF4E98736D1E4DA5B666C1501C12F1770FE1D17F64253A579540F3DB6C91575962C3E6F920EC93F548EBDC287704E6CB270895A2B8D74F7818C8BBB2C237DFDA8AB90B0EAEBDBBC1E9E3F378DB5A1A9EE3473AFE86C080577C4B737316FAA6272BC4B320FFAF8BF07AE679B7AE9F4DAD1FCC98972B976E49063F51D077B4C2FBA384FB4EEFB00947F680C294FD0DF71B0DF0971044F1DB534CF140B15DD410E1B1C4711204EB3FA6EB19E137FEABCB2CCF04C3E6BBAF12C12C00615G6B
    050DBF39DC48FC761464133917FE24000349B65D62CC4E7B8779DC4D52E4E778AFD1FC2EF7D9F6A9C007175B2EE23043A0972779EB68295B2E653672E0B6EA3B026B125CAF3B55C659AB3BED7062536AAEDDC15F53662D5DA3E8A40A7742BCEF00096593786EABB897A5D5D51CDDFED566CB2C2877D0DEBEFF17D78F911CD77F95544FB3G73DC349F3A02F3446AF13BE5BAD802C42B3FCE67286C12FBC168860F5F2DB27C1D1F201F7748C1ADC6CC7D849322FE496547F843720745529654C1F176028BE048A2EB
    3CB8FFB7097C87A18AF01887AD4127174F4501C3FCAE9E9E3C1FA4DFBDE2B1DBCB629EE834947731F9F132CF1F0BCC2640D5F359EF50D82161FDCC562C6DEF4173E6B299B6AF0EF4A10FCE602D82447471F911550975BC76E893224163F2B1B38B7B7F26B38F47CB6E734B9032B148FDA8EC7ADCE7FED8DD3BE6DFD61FCE6D6BB244479A856D1BBA48F34705A04FD200277A180D77A84CC62C5D02A15EAE9A4A71GCB9533F676E15EEE8316FD235BB19B66DC453C41ACDF6C76BBCB7C2A932330240223FB7DAAB5
    A24B856DE539DA9F25BE1E73C5D522DD9DD00E820873F13F7827EDBC671B5513C0D2973C92B15FDD6CE3322D7269FED1F8A74F79BCD664D920AE4BBE2C72B19B160C6BCE3D4F6731BA691227597B2DAE235FB0DCE9578B7034BB951F5A8C93BB404766F8974A0BEB220656F2044DC3DBB5DDEB002F97E00D0A78E75582036C09AADB5BD642B30376679D425C40294A3DA5786F028A6BEC03C3F2F74B36235724FE29C49AD0CBE808A9E9067A77712DC89B1765C579CCC619DA1EAD52C70B5AF95EB6FF67C90DEF4A
    43F3C85D48CE0BD99EBDED7BFECB311F59324D2F5BF478516C0CF51FBC1F4374CB917AA5C01B55FE2E5F2EEA23FEF97D4B32EBGAE77F37DAA06D1BF2B2448EA897D2FC9093A85CAFC83B07BB4759A30D6530F711CB14CF9FA213F44FE4EF3EA500CE7EABFDFDFFA4A42735C34FEFD0D4D9D983D5E327CAF0D7A663E81167FED91F8CB95626B8CF8A65F945A9E4B1721AEF90067AC7FEB43B8F8B830AC7BA4C0870097A096E0F1007B64B73BAA48B8B9E1B34CB1458DC1AAA468EBFE28DA513E902735F2400AF622
    F11A70B5F07BD0389DD282213A2F8DABB33A7D2368CA424ADB0321F44C9F5FED5F4B6304E1F8EBEB4FB4B8E4EB4FA9CFC05BA39E7663FCC39FBD9C64737FFA13F17EAF5DDEEE5992E17EDF56F88F87F5DFAD96FB3EC4BB463425D0379AE0BD4056C148BFGDAG3C035C7E965B09716CCFFD314D53G3853900B9B4257D2E12FFF3EC5BB270EEB6758C1B3BB9FB93A4F5C4EED10EFB4B9E517CDDB0F7B0D6BF162A00BBDA970CC85E0E3F136BAD713E743A01FBF3307EFCD1F8523684275A99D8A25E3B2FCB348E5
    B88D75ECAFF087DDBB2220BCCB604ED6A0EE8914D78EF1D9B7D6E2ACC803F25EB00CB3C087C0E0185767D6E17DACA8A78D33B5F355B00B972CBE26F2A2B9B67617B6E43EC7B6827266F268F6F9E0C2BC369073BCBD9CB61350FC1679F30E9CB997FDD1B64FC58BE473DC7495F98743C39F469DA92FBFA80F1D2B3EA91B652A7B29FEABF6ED27FD7BFC320FCEB0CC9C343AE7541708C94EFA8EB62D1AEDF61226214DDA1B77D5CFC33930F1119FFDAFEDB8F40CF22F3372BE6DD9FADD2FE7716218837D2391701DG
    8100D8004557F91CC816470D536F874608530F5AA30F4B576C666372ADBB66BEBE0FDF314A61367F2EFD0F2853A3922686E8A656660714DE1514EEF954587D8B25E7F51168FFE20FB43EEC8CB68F9B472002703874DFC55837C9DCB264041583391A2F2F3C4EF342C3BB68BE7146326CC2001A9BFCEE1E2F9E6B2C2C955A59EE6863993841F30E149079AA050FFCA7B8D6FFB1C01FE068AFC9747759B25C83392420472F92D2B902B2AFAE633175F1E8178A108B78FDA3463C794B832827DD11FA418C973C321564
    C5A12D2D02538EC37BD80045GAB81560FF01E105790ED5F72115D6E13559629CBF6313A2F16660756DD70BA5DF473476AFD47F19C799E4750CE2C432C5D8B9F216FFF6C13ED66FC969CC4BFBAAF8D506D89BF67E5B95809AAE38E569145561661A81E238D1CB35647EA754BC53D37520CFEF5940F75A7E90E308E4AE492475DC5F185D0BEACF03F7273B521D460FECB5709A8A847CF6273385A91F99E3E6BB01F070D8E7378F846B1F6FCBC6F08B4FF189F3EBD407DB09164DB8A30F2126EF7EB60FD432450B846
    72507B68BC59FA874BC3B78B7CC20ABFEA40337C340862315C88F509F7F07BB82B394D365E49F18D7D083B8CE5D5605E2CC45CB4A8478B5C13C1CE3BD8602E54F0DA924DF1D934DD81140F82B0F9D617717B08BA4029517CBE222CB3A41F41ADCFED6D870A53F6CA6AEE21330E450E4272DC0DFED1B40F9D674A314FB5D05E84D0B01967130157F9BE39688BF3C8CB5407267B495436F2CB71E4E66B5649FABE198879A463B93D02730C017A651391372442683BB8D79ECAE7398E2B7F0F10FA74052B44D83FAF44
    77D15EEFD80C7567C26A51C62FE974CC376F9D613AED7ADEA2F1FD756D08FB652DD334F5CFFB36369FA20727BF1F2345EBFB04F8CDE3G53DF2DC27D31FF4794BEDEB70E3FCE8C6781BD2E6E12EBD61CA1A5F6CCBEB1614708C4B3D11E63BDFC0E477D14A9BC77FC3012771DB915473DBF51796C1371BCC5368526E2BB371F5FA9B9C43B84BF1ED329B0E917CE45BB3B6F6034A50553286F75B71A5842B8DE7CCC901F84E8908EE9B6AC184666913F0F58C936D5220C92DDA9B18D105C2096E1FC1F4763F26D7C3A
    152EED8E200F1E466741C754FEF1D0DEA4F0BFC9C75CCAA82F9138168CBE2FF26F62380FEB915781E56FDDFCCD1C37835705E8A8A7GA483BCA56A7E0E56ED3A0B1D796C1B4E4EF4D85D319DE6B9A876B1DE3E49F90463238DB8466334A90F2CF17A67741BA30C95FFE7E39BD5BD565872B165D0B7DD9FDB2CEB0572BCF87E9E7E5858DE6CC71F04855A596B6F3D609C148973B90545C3E86F184EFD2E3E926534FADC7EDE37CFEB57D1496343ACE847E47CF5F0BC990DF2AE8C13F109C1C646E38A0D2F4B813FD2
    4832AFB8DA160AA017E553F4BE6ECF0FBA8B1948D86CA06BEE116EC8794D5231B52C30022EEDF76BF30F610BA8FE0F814F561236F23E169C063AE9F7733317277BB0262D03F2EE8C0C87C08DC087009A43F301350ED2B2CECE5F667136C03260B2AC6FCC3F999537F6FF7B6D9C9E33133EBE1FAC1972DCFA24730DE9B1ECDEA5EA4F35ED07A81E695767C37DD6C3DD9A401EFBA06F81E88438FC8F572F54D94C7443E36DFAD5152C8EFA324915EBF74A83BA9A93D326DB304AEC7EFA6FE1F2C45FE39EAB62G3FA8
    2CAE994A2B6EB1BBA3F839155BEFAD54EFB234493B9772BA001AFB4D6896196E21DB34361D771A4B2682FEDAD8DDBC14970761581C1A4C569445AF33B1CB98B0FE03D173C23569DD8542F22D3F555AF36954C35A387F7012310F3CB92FD952A37411B7C35B2B4E50731083046D85F2097E47569D52CF04EF7DF4AB772DFD8F4DA50DCFBC17FEA8820FD62D6F84830F3557B665B07D3B87F01D73AA32DDD69459C623A151E2CB94DD5BD6835D1A99BC86592BD010AE0FC213DF36AF3ADE4577B6F931107B81F48228
    316C4C05517DB6FD74FEEA1B05534544327D54C2F8AE09556424F1551AB15E3A4E6F9E71FDA5502E09E5F1954B9B60FD86B12E6B2EDA73589FCD575D4C1970BE134B3C65926E9F4E7ADD2A536BF24A8ACD1134FE2EDE5899D2A70E61347ACF1DC3DABC68B0EC31343AC7F2F706546934EC1F74740E70FD925A31EEA65FC7CD9E350F427AD6289F6DBB6B5FE33EB3B0207B519C3C4250E4B9D079AABC5D72C02138F9E8F4C208404B7D23D5D5450565E5C53C2D27926D08F1B4B21FEF84F5BE7FBD2873599818B81F
    B3C14E074501577138C1FBBFE6F83FD87E573AAB0F3C3679EEAB8F963B1F690B3CCF7DD51F79BEB52D4FFC1F3A31EF6CF3BA0B9279CEF933E23CD3E6EB4FE3C368EBB6702DA80005GCBGD2E672F567BE65A0216B0E491A733E2D9B4F2B708B9615739F74C556F512EF7C6FDE267B700E2E4B232A1EDE6C1F63E3FDB8DE2A47CB118CB75B07B6D21C5D8E15B55570F3FD6837AE59CE51AC6F1ECBF3763520570619BC977F65F144ED7D942CD11F623835CD082B01F21B406552DC4D816500402DE96439FD0C404D
    5B0D3827201CA6F0252D084B7DB47078B4477D67965EDF0D406D2DC7DC8B14ED8277EABF67919038F74FA26EF1A8278A5C8FE83E1A86654CD99CD7C1754D0372D1011BCD77DCF5D06E9038DF533D308B4A4382774988679BABF0F2BB62D2213CE1964F4BFEB6CC6DB79BF84C667ED22C625DFC3BF4CD4B4058ADBC7598C036AA9E2F2C28C10625C7D01A9F7E78BA91632728C7A27581DEF786624BC7DD57E0AF59A6F58BBF4E9D41F564B448E183188610G10841082108A1086107B99489F81EAGDAGBA81D400
    E800B800C51F61FA3C5FDFB4069E5AC186F5FEC5F2227BEBB151192E4744462A8311E3E2D9CDE9F1E1CD8D3F97FD200AFFEF138CB2EC788C5F17BF339F65987B1E3E4D293A64D35A122D6D4D1F2D9277747771F31562F4FACF8F65A800387B7899CE1505675D0B813776BE3E173FEE61FB0F42B85E47C334ED87143DG53624C5724486B47FC8B9136FAACC337D5760E09580A4975D2BEBFE3C8086332AE554F9DBE4B47707BC311E3512A2171E3510F0768B78490C35A02DE6D9C7DE7C31867F8683AAE627BD01E
    40B57BDABDBEA71EAAB03D9E2D62E79AAC8FF91C0E0B83E4D4BF4B73105F9D27B90B94884BD9DE22E7FA3150AEDE347DFDD364FCEE4F87B4B6C05BD5227D019A64EF871FE1ED2AE8ACA3F3E0BC67703D6D730D6365E0463DED7859CA5D1C703315245D66B9D88A0D0F2721FD5B9CAE5F872D1175692431B38A5A46GB0DC41D69A1B203CDC74D1DECF578C495A532DF87CF810435A1E21B1B58D5A1139DC572AB14F11427771613A964E8D57756333663A7652B8DD8C6DEB66F2B9FFD199FA3E35B6D3777FB3D566
    BE50C57D4586FDF8C5BF0B7C11ED76980D717150B6D1344F2D8C7501D769DCD985752942AE7E11316C12B30EDD8A62436DB25CEEEE17D9F49DA904764571FC1EBEB30CBCC4CE4316851E9F8DE3FE825D28CE49659C241F78DC00066EEED63FEFA4729CBFB4B27E9CAF9EB14FB74AC7C6679B55A36679460991E33E41F027C650DE5D30CEF408F69F885C1F852E05760938BFB9ED2AC3604FD33990FFDAC6069C5FC1659A20388E8DF745007B33066B9A41B885AABBBC0A789EF1047A015365E2659E5A46690630A5
    6C6125B2BA3D12C1EE3F407D19703C79212A714ECCBA624D767CDF9B176ED5935F5BC1FEC3695478714FB69126C1BBF65614C0573C787870B328AFD2FCE2FC7899D4F9BADF8B1320EE4FBCFE66760E65564E4E1AEF116E0E1B26E3FA9D48C0790767056BFB1462274D8B57774BE26D1F85F59B043E2F641B6D0BAEFF0E595F7BB973F10886FCDCD85DA2A8A7FF4E4C973EEDFA7E33CAEB3BB6820FCD004F7BFCE85DE1A8570461D80C5DC2FF7B43E252CF0EE32CB8C8AFC68B59563C7620562EA4C7EF776326B176
    3255B54505E5470BF87FFF4C5569FED7BDF13AD7376BF42B9B8F0653B5C130BEFA5086B14F53FD303890521F32CAC1DB0F690676EE5B2177F3715C4479CF4D5769D2370C35F7875E6565026E6785BA5D767A1B133BF917CE3B2B9FBB92F48D9E17C7A999F0B8D51935F56D56F51C31C727DB29CEDC566F3E2C53CD770F17DF161796F11FA95469FED9B1F13A76C3BA5D5D43EF0CE51B12AE0F4B8686AAE386E27441433A4E71C78CFE5BBF91995877B649BBB1B7F03ADDCE375C40FEA9C278DD56E63A5FED01F9E2
    7BBC5F57A5519CBDG65184F73397F853A07E16F0F985EEB52EF7FF94C9F7F9FF89C4950F5AAB13C7F3D657673DEF9DBF930B12D237B23B8984FC4B14E77D3DC9216856EBD3A3FC80572A6010B8F50F30384488B9344B9C987BF8F2894381F53FB29E8A8476BB81AD7AE06F232401D2679EB8A1457885CB7E80E3A814A85770BEF8BA86DD1A857885C21DD1CEF0B402D205F8B44C3B969FE1E5F969E479CCA45DDB04EB142F3619A6A6F0FC33BD5226D77E89ECB5B92BE573E05D75F5ADC3B109F1A237F6C8D3DEE
    C93EF90E3E125AB6F7BE68B31F73E92F0E1C23FB285DCFC25B96517EAB01509CFD859D07G54C74F67B97A3F9F1C489DF8249CBD897AF90998F3740DC1739CBDFF90F9A7C37B94A15F46303DCBD611EE172B9176AE83F46CB7C09F198BF8BF2B46583BBCCB7D22945A5609767B4276AE6554CF9CD02FAE60F6295D75176CDF97AE885F3B24161B5BE583753D4550FEA98235037416EE0787494D477C68E21D3EF24C7C21B4E47DFE708FBA5D4C314E5E4368F2ABF53AA7BBEEEE7DFE2B0A08B9F36E385E4FF347A7
    4EFFD335CE7728FF8C3A37C3698E3C2D53ED2C98032EA614EE66BBBA5D6EDDB727EFD21D2E6F93A774FE2E85A7421F7DD67DEA1D95025322E7C916CADE621F6F959AD3D0156C9D6333BE1FC2368BAE4DF42B165ADFD11B71738599961259CC3259563519E4627E3319E4516DE9B349002DEBG3FE34173B67EA9761B3EBEF203E23204B270849E7923B5293F77DF1FD8F4353140C2AC4046A133FE3386F01BCAFB46E0D2ADFA5CEE68B10BE233C25AE089173EA4C17132A65925908A5AC6A5DB18998A71CBC615EC
    8B4959C5DB4C034F05159432ADE4F49F3B3F6A17A9CF377BB2C8F6B3F999834985D921E789328D2F68349F3AB6114D749772B85D4118BE7A487229C1175CC28A6C677107DDBED5365573D38A9F0CE78BF2BA06073047CECBBE070AA78B6F3410CCB77EA4176433EA17646EBE460EAC5E21F6CB2F6FC4DF224109003CBB293C58CB0968E567BFECBF725A19B5CBB20825093C6072C836A6492AFA14E632D9F5A8324F8121A460FD6FA46DA8D7FCFF0BC339D7280F4A16606523903AA7B0AFE65B5AEDCDE07E00C20E
    51169853CBC48BDB03C3D200152CB42B7893E30FF2D854CDFAD5FD7D7B9F6C3EECA1691A12E87A7C00D7A559F62957698A428B1BCBF4D0F629A02DA239FDF60F528B129506C9462B840312CF2FA81D7D450D97A9831DD4A58718A90174D8376C69BDD6EF37C3FEABBAG3BC2777BC5775826A42CCDFEE066CBE7FE747BAE7003C61251DBDACA7F17517F65783F0CE24AA8262C9C0C3B1E0C7997DE9F60BAD367917396073F2489C6D0FD725995737F2DEE7DBA502E09E4511F7540A064692D4E79FB3BE4E54A2259
    7B374CCA3D9B47480B2E9240FEDF8908F913234D9BBCA04A0EB097F027D5CECBF923BC8E318523DD8751FB44GE2E9AFED3A1F7EAF3CE9F3A055C1CAFBD8E8D10C716C4550DDDF7A833B6F4C1BD16137105CA674A31757A1B5EB76E9A1DB3AE8C01395E82B12BC5D8FA8ADCCB60B95E662DE9DEFC5BC2B3D60152CB89FB75BA425077600B214DDA21B6DCAF797E520924B2526CB401616303A22053C20BA70DE93A706CAF66BDD3762CD0FD64126B27D204BCB7F4470E4EAC3E32173F17400E089773EC9A6257041
    CA0FB0D819FC309AC3E6DBB6BB83E4C6C71A5BB30AF501D16B887DC0B9D4A1187C67250142F6E666AD7A279435AA590A11B7A41C5F2266D8BA017F5E3BBD2A1E8904CC30A613525F5484BB9F9AA6D09EBACCE3E8F1B124547A372753E5DD279D861DF2C265563E1BAA9AB1FE1B35E1721E085FF7E5BE4C4E8B77BC2CFFB7456E121FBF097A35815E7BB03F237870A4A65C116E24AF74CAAE17AA39FA6A5DB63C1FBE95D4E57D1B8CD20F7D7BF89B2DD27B0DD7BD4AFD4DC073FFD0CB87882DE117891099GGC0C9
    GGD0CB818294G94G88G88G94F954AC2DE117891099GGC0C9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4A99GGGG
**end of data**/
}
}

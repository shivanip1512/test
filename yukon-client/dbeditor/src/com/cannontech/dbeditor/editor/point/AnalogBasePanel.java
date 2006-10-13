package com.cannontech.dbeditor.editor.point;

import java.awt.Dimension;
import java.util.List;
import javax.swing.*;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.field.JCSpinField;

public class AnalogBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, com.klg.jclass.util.value.JCValueListener
{
    private JComboBox unitOfMeasureComboBox = null;
    private JLabel unitOfMeasureLabel = null;
    private JComboBox archiveIntervalComboBox = null;
    private JLabel archiveIntervalLabel = null;
    private JComboBox archiveTypeComboBox = null;
    private JLabel archiveTypeLabel = null;
    private JLabel decimalPlacesLabel = null;
    private JCSpinField decimalPlacesSpinner = null;
    private JLabel meterDialsLabel = null;
    private JCSpinField meterDialsSpinner = null;
    private JPanel archivePanel = null;
    private JPanel holderPanel = null;
    private JComboBox stateGroupComboBox = null;
    private JLabel stateGroupLabel = null;
    
/**
 * Constructor
 */
public AnalogBasePanel() {
    super();
    initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
    if (e.getSource() == getArchiveTypeComboBox()) 
        connEtoC1(e);
    if (e.getSource() == getUnitOfMeasureComboBox()) 
        connEtoC4(e);
    if (e.getSource() == getArchiveIntervalComboBox()) 
        connEtoC5(e);
    if (e.getSource() == getStateGroupComboBox()) 
        connEtoC5(e);
}

/**
 * This method is called when an achive type is chosen from the archiveTypeComboBox
 */
public void archiveTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
    fireInputUpdate();

    if( ((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointTypes.ARCHIVE_NONE) ||
            ((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointTypes.ARCHIVE_ON_CHANGE) ||
            ((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointTypes.ARCHIVE_ON_UPDATE) )
    {
        getArchiveIntervalLabel().setEnabled(false);
        getArchiveIntervalComboBox().setEnabled(false);
    }
    else
    {
        getArchiveIntervalLabel().setEnabled(true);
        getArchiveIntervalComboBox().setEnabled(true);
        getArchiveIntervalComboBox().setSelectedItem("5 minute");
        
        if("On Timer Or Update".equalsIgnoreCase(((String)getArchiveTypeComboBox().getSelectedItem())))
            getArchiveIntervalComboBox().setSelectedItem("Daily");
    }

    return;
}

/**
 * connEtoC1:  (UnitOfMeasureComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
    try {
        this.archiveTypeComboBox_ActionPerformed(arg1);
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * connEtoC4:  (ArchiveTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * connEtoC5:  (ArchiveIntervalComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
    try {
        this.fireInputUpdate();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * Return the ArchiveIntervalComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getArchiveIntervalComboBox() {
    if (archiveIntervalComboBox == null) {
        try {
            archiveIntervalComboBox = new javax.swing.JComboBox();
            archiveIntervalComboBox.setName("ArchiveIntervalComboBox");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return archiveIntervalComboBox;
}

/**
 * Return the ArchiveIntervalLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getArchiveIntervalLabel() {
    if (archiveIntervalLabel == null) {
        try {
            archiveIntervalLabel = new javax.swing.JLabel();
            archiveIntervalLabel.setName("ArchiveIntervalLabel");
            archiveIntervalLabel.setText("Interval:");
            archiveIntervalLabel.setMaximumSize(new java.awt.Dimension(78, 16));
            archiveIntervalLabel.setPreferredSize(new java.awt.Dimension(78, 16));
            archiveIntervalLabel.setFont(new java.awt.Font("dialog", 0, 14));
            archiveIntervalLabel.setMinimumSize(new java.awt.Dimension(78, 16));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return archiveIntervalLabel;
}

/**
 * Return the ArchiveTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getArchiveTypeComboBox() {
    if (archiveTypeComboBox == null) {
        try {
            archiveTypeComboBox = new javax.swing.JComboBox();
            archiveTypeComboBox.setName("ArchiveTypeComboBox");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return archiveTypeComboBox;
}

/**
 * Return the ArchiveTypeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getArchiveTypeLabel() {
    if (archiveTypeLabel == null) {
        try {
            archiveTypeLabel = new javax.swing.JLabel();
            archiveTypeLabel.setName("ArchiveTypeLabel");
            archiveTypeLabel.setText("Data Type:");
            archiveTypeLabel.setMaximumSize(new java.awt.Dimension(78, 16));
            archiveTypeLabel.setPreferredSize(new java.awt.Dimension(78, 16));
            archiveTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
            archiveTypeLabel.setMinimumSize(new java.awt.Dimension(78, 16));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return archiveTypeLabel;
}

private com.klg.jclass.field.JCSpinField getDecimalPlacesSpinner() {
    if (decimalPlacesSpinner == null) {
        try {
            decimalPlacesSpinner = new com.klg.jclass.field.JCSpinField();
            decimalPlacesSpinner.setName("DecimalPlacesSpinner");
            decimalPlacesSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
            decimalPlacesSpinner.setBackground(java.awt.Color.white);
            decimalPlacesSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
            decimalPlacesSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return decimalPlacesSpinner;
}

private com.klg.jclass.field.JCSpinField getMeterDialsSpinner() {
    if (meterDialsSpinner == null) {
        try {
            meterDialsSpinner = new com.klg.jclass.field.JCSpinField();
            meterDialsSpinner.setName("MeterDialsSpinner");
            meterDialsSpinner.setPreferredSize(new java.awt.Dimension(50, 20));
            meterDialsSpinner.setBackground(java.awt.Color.white);
            meterDialsSpinner.setMinimumSize(new java.awt.Dimension(50, 20));
            meterDialsSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return meterDialsSpinner;
}

private javax.swing.JLabel getJLabelDecimalPositions() {
    if (decimalPlacesLabel == null) {
        try {
            decimalPlacesLabel = new javax.swing.JLabel();
            decimalPlacesLabel.setName("JLabelDecimalPositions");
            decimalPlacesLabel.setFont(new java.awt.Font("dialog", 0, 14));
            decimalPlacesLabel.setText("Decimal Places:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return decimalPlacesLabel;
}

private javax.swing.JLabel getJLabelMeterDials() {
    if (meterDialsLabel == null) {
        try {
            meterDialsLabel = new javax.swing.JLabel();
            meterDialsLabel.setName("JLabelMeterDials");
            meterDialsLabel.setFont(new java.awt.Font("dialog", 0, 14));
            meterDialsLabel.setText("Meter Dials:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return meterDialsLabel;
}

private javax.swing.JPanel getJPanelArchive() {
    if (archivePanel == null) {
        try {
            com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
            ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
            ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
            ivjLocalBorder.setTitle("Archive");
            archivePanel = new javax.swing.JPanel();
            archivePanel.setName("JPanelArchive");
            archivePanel.setBorder(ivjLocalBorder);
            archivePanel.setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsArchiveTypeLabel = new java.awt.GridBagConstraints();
            constraintsArchiveTypeLabel.gridx = 1; constraintsArchiveTypeLabel.gridy = 1;
            constraintsArchiveTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsArchiveTypeLabel.ipadx = -5;
            constraintsArchiveTypeLabel.insets = new java.awt.Insets(3, 14, 6, 1);
            getJPanelArchive().add(getArchiveTypeLabel(), constraintsArchiveTypeLabel);

            java.awt.GridBagConstraints constraintsArchiveIntervalLabel = new java.awt.GridBagConstraints();
            constraintsArchiveIntervalLabel.gridx = 1; constraintsArchiveIntervalLabel.gridy = 2;
            constraintsArchiveIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsArchiveIntervalLabel.ipadx = -5;
            constraintsArchiveIntervalLabel.insets = new java.awt.Insets(5, 14, 10, 1);
            getJPanelArchive().add(getArchiveIntervalLabel(), constraintsArchiveIntervalLabel);

            java.awt.GridBagConstraints constraintsArchiveIntervalComboBox = new java.awt.GridBagConstraints();
            constraintsArchiveIntervalComboBox.gridx = 2; constraintsArchiveIntervalComboBox.gridy = 2;
            constraintsArchiveIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsArchiveIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsArchiveIntervalComboBox.weightx = 1.0;
            constraintsArchiveIntervalComboBox.ipadx = 10;
            constraintsArchiveIntervalComboBox.insets = new java.awt.Insets(2, 2, 6, 8);
            getJPanelArchive().add(getArchiveIntervalComboBox(), constraintsArchiveIntervalComboBox);

            java.awt.GridBagConstraints constraintsArchiveTypeComboBox = new java.awt.GridBagConstraints();
            constraintsArchiveTypeComboBox.gridx = 2; constraintsArchiveTypeComboBox.gridy = 1;
            constraintsArchiveTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsArchiveTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsArchiveTypeComboBox.weightx = 1.0;
            constraintsArchiveTypeComboBox.ipadx = 10;
            constraintsArchiveTypeComboBox.insets = new java.awt.Insets(0, 2, 2, 8);
            getJPanelArchive().add(getArchiveTypeComboBox(), constraintsArchiveTypeComboBox);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return archivePanel;
}

/**
 * Return the JPanelHolder property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelHolder() {
    if (holderPanel == null) {
        try {
            holderPanel = new javax.swing.JPanel();
            holderPanel.setName("JPanelHolder");
            holderPanel.setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
            constraintsUnitOfMeasureLabel.gridx = 0; constraintsUnitOfMeasureLabel.gridy = 0;
            constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            getJPanelHolder().add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

            java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
            constraintsUnitOfMeasureComboBox.gridx = 1; constraintsUnitOfMeasureComboBox.gridy = 0;
            constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUnitOfMeasureComboBox.weightx = 1.0;
            constraintsUnitOfMeasureComboBox.gridwidth = 3;
            constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

            java.awt.GridBagConstraints constraintsJLabelDecimalPositons = new java.awt.GridBagConstraints();
            constraintsJLabelDecimalPositons.gridx = 0; constraintsJLabelDecimalPositons.gridy = 1;
            constraintsJLabelDecimalPositons.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelDecimalPositons.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getJLabelDecimalPositions(), constraintsJLabelDecimalPositons);

            java.awt.GridBagConstraints constraintsDecimalPlacesSpinner = new java.awt.GridBagConstraints();
            constraintsDecimalPlacesSpinner.gridx = 1; constraintsDecimalPlacesSpinner.gridy = 1;
            constraintsDecimalPlacesSpinner.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDecimalPlacesSpinner.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getDecimalPlacesSpinner(), constraintsDecimalPlacesSpinner);
            
            java.awt.GridBagConstraints constraintsStateGroupLabel = new java.awt.GridBagConstraints();
            constraintsStateGroupLabel.gridx = 2; constraintsStateGroupLabel.gridy = 1;
            constraintsStateGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsStateGroupLabel.insets = new java.awt.Insets( 5, 5, 5, 5);
            getJPanelHolder().add(getStateGroupLabel(), constraintsStateGroupLabel);
            
            java.awt.GridBagConstraints constraintsStateGroupComboBox = new java.awt.GridBagConstraints();
            constraintsStateGroupComboBox.gridx = 3; constraintsStateGroupComboBox.gridy = 1;
            constraintsStateGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsStateGroupComboBox.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getStateGroupComboBox(), constraintsStateGroupComboBox);
            
            java.awt.GridBagConstraints constraintJLabelMeterDials = new java.awt.GridBagConstraints();
            constraintJLabelMeterDials.gridx = 0; constraintJLabelMeterDials.gridy = 2;
            constraintJLabelMeterDials.anchor = java.awt.GridBagConstraints.WEST;
            constraintJLabelMeterDials.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getJLabelMeterDials(), constraintJLabelMeterDials);

            java.awt.GridBagConstraints constraintsMeterDialsSpinner = new java.awt.GridBagConstraints();
            constraintsMeterDialsSpinner.gridx = 1; constraintsMeterDialsSpinner.gridy = 2;
            constraintsMeterDialsSpinner.anchor = java.awt.GridBagConstraints.WEST;
            constraintsMeterDialsSpinner.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getMeterDialsSpinner(), constraintsMeterDialsSpinner);
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return holderPanel;
}

/**
 * Return the UnitOfMeasureComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getUnitOfMeasureComboBox() {
    if (unitOfMeasureComboBox == null) {
        try {
            unitOfMeasureComboBox = new javax.swing.JComboBox();
            unitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
            unitOfMeasureComboBox.setPreferredSize(new java.awt.Dimension(126, 24));
            unitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
            unitOfMeasureComboBox.setMinimumSize(new java.awt.Dimension(90, 24));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return unitOfMeasureComboBox;
}

/**
 * Return the UnitOfMeasureLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUnitOfMeasureLabel() {
    if (unitOfMeasureLabel == null) {
        try {
            unitOfMeasureLabel = new javax.swing.JLabel();
            unitOfMeasureLabel.setName("UnitOfMeasureLabel");
            unitOfMeasureLabel.setText("Unit of Measure:");
            unitOfMeasureLabel.setMaximumSize(new java.awt.Dimension(103, 16));
            unitOfMeasureLabel.setPreferredSize(new java.awt.Dimension(103, 16));
            unitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
            unitOfMeasureLabel.setMinimumSize(new java.awt.Dimension(103, 16));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return unitOfMeasureLabel;
}

/**
 * Return the stateGroupLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStateGroupLabel() {
    if (stateGroupLabel == null) {
        try {
            stateGroupLabel = new javax.swing.JLabel();
            stateGroupLabel.setName("StateGroupLabel");
            stateGroupLabel.setText("State Group:");
            stateGroupLabel.setMinimumSize(new Dimension(85, 22));
            stateGroupLabel.setPreferredSize(new Dimension(85, 22));
            stateGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
         } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return stateGroupLabel;
}

/**
 * Return the StateGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getStateGroupComboBox() {
    if (stateGroupComboBox == null) {
        try {
            stateGroupComboBox = new javax.swing.JComboBox();
            stateGroupComboBox.setName("StateGroupComboBox");
            stateGroupComboBox.setPreferredSize(new Dimension( 120, 20));
            stateGroupComboBox.setMinimumSize(new Dimension( 120, 20));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return stateGroupComboBox;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
    //Assume that commonObject is an instance of com.cannontech.database.data.point.AnalogPoint
    com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;

    int uOfMeasureID = ((com.cannontech.database.data.lite.LiteUnitMeasure)getUnitOfMeasureComboBox().getSelectedItem()).getUomID();
    
    point.getPointUnit().setUomID( new Integer(uOfMeasureID) );
    if(getArchiveTypeComboBox().getSelectedItem().toString().compareTo("On Timer Or Update") == 0)
        point.getPoint().setArchiveType(PointTypes.ARCHIVE_ON_TIMER_OR_UPDATE);
    else
        point.getPoint().setArchiveType((String)getArchiveTypeComboBox().getSelectedItem());
    point.getPoint().setArchiveInterval(CtiUtilities.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));
    
    point.getPointUnit().setDecimalPlaces( new Integer(((Number)getDecimalPlacesSpinner().getValue()).intValue() ) );
    point.getPointUnit().setMeterDials( new Integer(((Number)getMeterDialsSpinner().getValue()).intValue() ) );

    LiteStateGroup stateGroup = (LiteStateGroup) getStateGroupComboBox().getSelectedItem();
    point.getPoint().setStateGroupID( new Integer(stateGroup.getStateGroupID()) );
    
    return point;
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
private void initConnections() throws java.lang.Exception {
    getDecimalPlacesSpinner().addValueListener(this);
    getMeterDialsSpinner().addValueListener(this);
    getArchiveTypeComboBox().addActionListener(this);
    getUnitOfMeasureComboBox().addActionListener(this);
    getArchiveIntervalComboBox().addActionListener(this);
    getStateGroupComboBox().addActionListener(this);
}

/**
 * Initialize the class.
 */
private void initialize() {
    try {
        setName("AnalogBasePanel");
        setPreferredSize(new java.awt.Dimension(300, 102));
        setLayout(new java.awt.GridBagLayout());
        setSize(386, 177);
        setMinimumSize(new java.awt.Dimension(0, 0));

        java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
        constraintsJPanelArchive.gridx = 1; constraintsJPanelArchive.gridy = 2;
        constraintsJPanelArchive.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJPanelArchive.weightx = 1.0;
        constraintsJPanelArchive.weighty = 1.0;
        constraintsJPanelArchive.ipadx = 136;
        constraintsJPanelArchive.ipady = 8;
        constraintsJPanelArchive.insets = new java.awt.Insets(6, 4, 18, 2);
        add(getJPanelArchive(), constraintsJPanelArchive);

        java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
        constraintsJPanelHolder.gridx = 1; constraintsJPanelHolder.gridy = 1;
        constraintsJPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
        constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJPanelHolder.weightx = 1.0;
        constraintsJPanelHolder.weighty = 1.0;
        constraintsJPanelHolder.insets = new java.awt.Insets(2, 4, 6, 2);
        add(getJPanelHolder(), constraintsJPanelHolder);
        initConnections();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }

    //Put a border around the Analog section of panel
    javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Analog Summary");
    border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
    setBorder(border);

    //Load the unit of measure combo box with default possible values
    List<LiteUnitMeasure> unitMeasures = 
        DaoFactory.getUnitMeasureDao().getLiteUnitMeasures();
    for (LiteUnitMeasure lum : unitMeasures) {
        getUnitOfMeasureComboBox().addItem(lum);
    }

    //Load the Archive Type combo box with default possible values
    getArchiveTypeComboBox().addItem(PointTypes.ARCHIVE_NONE);
    getArchiveTypeComboBox().addItem(PointTypes.ARCHIVE_ON_CHANGE);
    getArchiveTypeComboBox().addItem(PointTypes.ARCHIVE_ON_TIMER);
    getArchiveTypeComboBox().addItem(PointTypes.ARCHIVE_ON_UPDATE);
    getArchiveTypeComboBox().addItem("On Timer Or Update"); 
    
    //Load the Archive Interval combo box with default possible values
    getArchiveIntervalComboBox().addItem("1 second");
    getArchiveIntervalComboBox().addItem("2 second");
    getArchiveIntervalComboBox().addItem("5 second");
    getArchiveIntervalComboBox().addItem("10 second");
    getArchiveIntervalComboBox().addItem("15 second");
    getArchiveIntervalComboBox().addItem("30 second");
    getArchiveIntervalComboBox().addItem("1 minute");
    getArchiveIntervalComboBox().addItem("2 minute");
    getArchiveIntervalComboBox().addItem("3 minute");
    getArchiveIntervalComboBox().addItem("5 minute");
    getArchiveIntervalComboBox().addItem("10 minute");
    getArchiveIntervalComboBox().addItem("15 minute");
    getArchiveIntervalComboBox().addItem("30 minute");
    getArchiveIntervalComboBox().addItem("1 hour");
    getArchiveIntervalComboBox().addItem("2 hour");
    getArchiveIntervalComboBox().addItem("6 hour");
    getArchiveIntervalComboBox().addItem("12 hour");
    getArchiveIntervalComboBox().addItem("Daily");
    getArchiveIntervalComboBox().addItem("Weekly");
    getArchiveIntervalComboBox().addItem("Monthly");
    getArchiveIntervalComboBox().setSelectedItem("5 minute");
}

/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{   
    return true;
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
        AnalogBasePanel aAnalogBasePanel;
        aAnalogBasePanel = new AnalogBasePanel();
        frame.add("Center", aAnalogBasePanel);
        frame.setSize(aAnalogBasePanel.getSize());
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
    //Assume that defaultObject is an AnalogPoint
    com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;

    int uOfMeasureID = point.getPointUnit().getUomID().intValue();
    String archiveType = point.getPoint().getArchiveType();
    if(archiveType.compareTo(PointTypes.ARCHIVE_ON_TIMER_OR_UPDATE) == 0)
        archiveType = "On Timer Or Update";
    Integer archiveInteger = point.getPoint().getArchiveInterval();

    getDecimalPlacesSpinner().setValue( point.getPointUnit().getDecimalPlaces() );
    getMeterDialsSpinner().setValue( point.getPointUnit().getMeterDials() );
    getArchiveIntervalLabel().setEnabled(false);
    getArchiveIntervalComboBox().setEnabled(false);
    for(int i=0;i<getUnitOfMeasureComboBox().getModel().getSize();i++)
    {
        if( ((com.cannontech.database.data.lite.LiteUnitMeasure)getUnitOfMeasureComboBox().getItemAt(i)).getUomID()
             == uOfMeasureID )
        {
            getUnitOfMeasureComboBox().setSelectedIndex(i);
            break;
        }
    }

    for(int i=0;i<getArchiveTypeComboBox().getModel().getSize();i++)
    {
        if( ((String)getArchiveTypeComboBox().getItemAt(i)).equalsIgnoreCase(archiveType) )
        {
            getArchiveTypeComboBox().setSelectedIndex(i);
            if( getArchiveIntervalComboBox().isEnabled() )
                CtiUtilities.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(),archiveInteger.intValue());
            break;
        }
    }
    
    //   load and set stategroups
    int stateGroupID = point.getPoint().getStateGroupID().intValue();
    
    //Load all the state groups
    IDatabaseCache cache = DefaultDatabaseCache.getInstance();
    synchronized(cache)
    {
        LiteStateGroup[] allStateGroups = DaoFactory.getStateDao().getAllStateGroups();

        //Load the state table combo box
        for(int i=0;i<allStateGroups.length;i++)
        {
            LiteStateGroup grp = (LiteStateGroup)allStateGroups[i];

           getStateGroupComboBox().addItem( grp );
            if( grp.getStateGroupID() == stateGroupID )
                getStateGroupComboBox().setSelectedItem( grp );
        }
    }
}

/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
    if ((arg1.getSource() == getDecimalPlacesSpinner()) || (arg1.getSource() == getMeterDialsSpinner())) 
        this.fireInputUpdate();
}

/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}
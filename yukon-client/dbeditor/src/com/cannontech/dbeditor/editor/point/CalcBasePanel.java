package com.cannontech.dbeditor.editor.point;

import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.util.value.JCValueListener;

public class CalcBasePanel extends DataInputPanel implements JCValueListener, ActionListener {
    private JComboBox<String> ivjArchiveIntervalComboBox = null;
    private JLabel ivjArchiveIntervalLabel = null;
    private JComboBox<String> ivjArchiveTypeComboBox = null;
    private JLabel ivjArchiveTypeLabel = null;
    private JComboBox<String> ivjPeriodicRateComboBox = null;
    private JLabel ivjPeriodicRateLabel = null;
    private JComboBox<String> ivjUpdateTypeComboBox = null;
    private JLabel ivjUpdateTypeLabel = null;
    private JComboBox<UnitOfMeasure> ivjUnitOfMeasureComboBox = null;
    private JLabel ivjUnitOfMeasureLabel = null;
    private com.klg.jclass.field.JCSpinField ivjDecimalPlacesSpinner = null;
    private JLabel ivjJLabelDecimalPositons = null;
    private javax.swing.JPanel ivjJPanelArchive = null;
    private javax.swing.JPanel ivjJPanelHolder = null;
    private javax.swing.JCheckBox ivjJCheckboxCalcQual = null;
    private JLabel stateGroupLabel = null;
    private JComboBox<LiteStateGroup> stateGroupComboBox = null;

    public CalcBasePanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getPeriodicRateComboBox() ||
                    e.getSource() == getArchiveIntervalComboBox() ||
                    e.getSource() == getUnitOfMeasureComboBox() ||
                    e.getSource() == getJCheckboxCalcQual() ||
                    e.getSource() == getStateGroupComboBox()) {
                fireInputUpdate();
            }
            
            if (e.getSource() == getArchiveTypeComboBox()) {
                archiveTypeComboBox_ActionPerformed(e);
            }
            
            if (e.getSource() == getUpdateTypeComboBox()) {
                updateTypeComboBox_ActionPerformed(e);
            }
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void archiveTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        String val = getArchiveTypeComboBox().getSelectedItem().toString();

        getArchiveIntervalLabel().setEnabled(PointArchiveType.ON_TIMER.getPointArchiveTypeName().equalsIgnoreCase(val));

        getArchiveIntervalComboBox().setEnabled(PointArchiveType.ON_TIMER.getPointArchiveTypeName()
                                                                         .equalsIgnoreCase(val));
        getArchiveIntervalComboBox().setSelectedItem("5 minute");

        if (PointArchiveType.ON_TIMER_OR_UPDATE.getDisplayName().equalsIgnoreCase(val)) {
            getArchiveIntervalLabel().setEnabled(true);
            getArchiveIntervalComboBox().setEnabled(true);
            getArchiveIntervalComboBox().setSelectedItem("Daily");
        }

        fireInputUpdate();

        return;
    }

    private JComboBox<String> getArchiveIntervalComboBox() {
        if (ivjArchiveIntervalComboBox == null) {
            try {
                ivjArchiveIntervalComboBox = new JComboBox<>();
                ivjArchiveIntervalComboBox.setName("ArchiveIntervalComboBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjArchiveIntervalComboBox;
    }

    private JLabel getArchiveIntervalLabel() {
        if (ivjArchiveIntervalLabel == null) {
            try {
                ivjArchiveIntervalLabel = new JLabel();
                ivjArchiveIntervalLabel.setName("ArchiveIntervalLabel");
                ivjArchiveIntervalLabel.setText("Interval:");
                ivjArchiveIntervalLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjArchiveIntervalLabel;
    }

    private JComboBox<String> getArchiveTypeComboBox() {
        if (ivjArchiveTypeComboBox == null) {
            try {
                ivjArchiveTypeComboBox = new JComboBox<>();
                ivjArchiveTypeComboBox.setName("ArchiveTypeComboBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjArchiveTypeComboBox;
    }

    private JLabel getArchiveTypeLabel() {
        if (ivjArchiveTypeLabel == null) {
            try {
                ivjArchiveTypeLabel = new JLabel();
                ivjArchiveTypeLabel.setName("ArchiveTypeLabel");
                ivjArchiveTypeLabel.setText("Data Type:");
                ivjArchiveTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjArchiveTypeLabel;
    }

    private JCSpinField getDecimalPlacesSpinner() {
        if (ivjDecimalPlacesSpinner == null) {
            try {
                ivjDecimalPlacesSpinner = new JCSpinField();
                ivjDecimalPlacesSpinner.setName("DecimalPlacesSpinner");
                ivjDecimalPlacesSpinner.setBackground(java.awt.Color.white);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDecimalPlacesSpinner;
    }

    private JLabel getJLabelDecimalPositons() {
        if (ivjJLabelDecimalPositons == null) {
            try {
                ivjJLabelDecimalPositons = new JLabel();
                ivjJLabelDecimalPositons.setName("JLabelDecimalPositons");
                ivjJLabelDecimalPositons.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelDecimalPositons.setText("Decimal Places:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelDecimalPositons;
    }

    private javax.swing.JPanel getJPanelArchive() {
        if (ivjJPanelArchive == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Archive");
                ivjJPanelArchive = new javax.swing.JPanel();
                ivjJPanelArchive.setName("JPanelArchive");
                ivjJPanelArchive.setBorder(ivjLocalBorder);
                ivjJPanelArchive.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsArchiveTypeLabel = new java.awt.GridBagConstraints();
                constraintsArchiveTypeLabel.gridx = 1;
                constraintsArchiveTypeLabel.gridy = 1;
                constraintsArchiveTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveTypeLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveTypeLabel(), constraintsArchiveTypeLabel);

                java.awt.GridBagConstraints constraintsArchiveIntervalLabel = new java.awt.GridBagConstraints();
                constraintsArchiveIntervalLabel.gridx = 1;
                constraintsArchiveIntervalLabel.gridy = 2;
                constraintsArchiveIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveIntervalLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveIntervalLabel(), constraintsArchiveIntervalLabel);

                java.awt.GridBagConstraints constraintsArchiveIntervalComboBox = new java.awt.GridBagConstraints();
                constraintsArchiveIntervalComboBox.gridx = 2;
                constraintsArchiveIntervalComboBox.gridy = 2;
                constraintsArchiveIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsArchiveIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveIntervalComboBox.weightx = 1.0;
                constraintsArchiveIntervalComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveIntervalComboBox(), constraintsArchiveIntervalComboBox);

                java.awt.GridBagConstraints constraintsArchiveTypeComboBox = new java.awt.GridBagConstraints();
                constraintsArchiveTypeComboBox.gridx = 2;
                constraintsArchiveTypeComboBox.gridy = 1;
                constraintsArchiveTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsArchiveTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveTypeComboBox.weightx = 1.0;
                constraintsArchiveTypeComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveTypeComboBox(), constraintsArchiveTypeComboBox);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelArchive;
    }

    private javax.swing.JPanel getJPanelHolder() {
        if (ivjJPanelHolder == null) {
            try {
                ivjJPanelHolder = new javax.swing.JPanel();
                ivjJPanelHolder.setName("JPanelHolder");
                ivjJPanelHolder.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
                constraintsUnitOfMeasureLabel.gridx = 0;
                constraintsUnitOfMeasureLabel.gridy = 0;
                constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

                java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
                constraintsUnitOfMeasureComboBox.gridx = 1;
                constraintsUnitOfMeasureComboBox.gridy = 0;
                constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsUnitOfMeasureComboBox.weightx = 1.0;
                constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                constraintsUnitOfMeasureComboBox.gridwidth = 4;
                getJPanelHolder().add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

                java.awt.GridBagConstraints constraintsStateGroupLabel = new java.awt.GridBagConstraints();
                constraintsStateGroupLabel.gridx = 0;
                constraintsStateGroupLabel.gridy = 1;
                constraintsStateGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsStateGroupLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getStateGroupLabel(), constraintsStateGroupLabel);

                java.awt.GridBagConstraints constraintsStateGroupComboBox = new java.awt.GridBagConstraints();
                constraintsStateGroupComboBox.gridx = 1;
                constraintsStateGroupComboBox.gridy = 1;
                constraintsStateGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsStateGroupComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getStateGroupComboBox(), constraintsStateGroupComboBox);

                java.awt.GridBagConstraints constraintsJLabelDecimalPositons = new java.awt.GridBagConstraints();
                constraintsJLabelDecimalPositons.gridx = 0;
                constraintsJLabelDecimalPositons.gridy = 2;
                constraintsJLabelDecimalPositons.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelDecimalPositons.insets = new java.awt.Insets(2, 0, 7, 12);
                getJPanelHolder().add(getJLabelDecimalPositons(), constraintsJLabelDecimalPositons);

                java.awt.GridBagConstraints constraintsDecimalPlacesSpinner = new java.awt.GridBagConstraints();
                constraintsDecimalPlacesSpinner.gridx = 1;
                constraintsDecimalPlacesSpinner.gridy = 2;
                constraintsDecimalPlacesSpinner.anchor = java.awt.GridBagConstraints.WEST;
                constraintsDecimalPlacesSpinner.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getDecimalPlacesSpinner(), constraintsDecimalPlacesSpinner);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJPanelHolder;
    }

    private JComboBox<String> getPeriodicRateComboBox() {
        if (ivjPeriodicRateComboBox == null) {
            try {
                ivjPeriodicRateComboBox = new JComboBox<>();
                ivjPeriodicRateComboBox.setName("PeriodicRateComboBox");
                ivjPeriodicRateComboBox.setFont(new java.awt.Font("dialog", 0, 12));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPeriodicRateComboBox;
    }

    private JLabel getPeriodicRateLabel() {
        if (ivjPeriodicRateLabel == null) {
            try {
                ivjPeriodicRateLabel = new JLabel();
                ivjPeriodicRateLabel.setName("PeriodicRateLabel");
                ivjPeriodicRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPeriodicRateLabel.setText("Rate:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPeriodicRateLabel;
    }

    private JComboBox<UnitOfMeasure> getUnitOfMeasureComboBox() {
        if (ivjUnitOfMeasureComboBox == null) {
            try {
                ivjUnitOfMeasureComboBox = new JComboBox<>();
                ivjUnitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
                ivjUnitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUnitOfMeasureComboBox;
    }

    private JLabel getUnitOfMeasureLabel() {
        if (ivjUnitOfMeasureLabel == null) {
            try {
                ivjUnitOfMeasureLabel = new JLabel();
                ivjUnitOfMeasureLabel.setName("UnitOfMeasureLabel");
                ivjUnitOfMeasureLabel.setText("Unit of Measure:");
                ivjUnitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUnitOfMeasureLabel;
    }

    private JComboBox<String> getUpdateTypeComboBox() {
        if (ivjUpdateTypeComboBox == null) {
            try {
                ivjUpdateTypeComboBox = new JComboBox<>();
                ivjUpdateTypeComboBox.setName("UpdateTypeComboBox");
                ivjUpdateTypeComboBox.setFont(new java.awt.Font("dialog", 0, 12));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUpdateTypeComboBox;
    }

    private JLabel getUpdateTypeLabel() {
        if (ivjUpdateTypeLabel == null) {
            try {
                ivjUpdateTypeLabel = new JLabel();
                ivjUpdateTypeLabel.setName("UpdateTypeLabel");
                ivjUpdateTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjUpdateTypeLabel.setText("Update Type:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUpdateTypeLabel;
    }

    private JLabel getStateGroupLabel() {
        if (stateGroupLabel == null) {
            try {
                stateGroupLabel = new JLabel();
                stateGroupLabel.setName("StateGroupLabel");
                stateGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
                stateGroupLabel.setText("State Group:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return stateGroupLabel;
    }

    private JComboBox<LiteStateGroup> getStateGroupComboBox() {
        if (stateGroupComboBox == null) {
            try {
                stateGroupComboBox = new JComboBox<>();
                stateGroupComboBox.setName("stateGroupComboBox");
                stateGroupComboBox.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return stateGroupComboBox;
    }

    private javax.swing.JCheckBox getJCheckboxCalcQual() {
        if (ivjJCheckboxCalcQual == null) {
            try {
                ivjJCheckboxCalcQual = new javax.swing.JCheckBox();
                ivjJCheckboxCalcQual.setName("CalcQualCheckbox");
                ivjJCheckboxCalcQual.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJCheckboxCalcQual.setText("Force Quality Normal");
                ivjJCheckboxCalcQual.setSelected(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckboxCalcQual;
    }

    @Override
    public Object getValue(Object val) {
        // Assume that commonObject is an instance of com.cannontech.database.data.point.CalculatedPoint
        CalculatedPoint calcPoint = (CalculatedPoint) val;

        int uOfMeasureID = ((UnitOfMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getId();

        String selectedArchiveType = getArchiveTypeComboBox().getSelectedItem().toString();
        calcPoint.getPoint().setArchiveType(PointArchiveType.getByDisplayName(selectedArchiveType));

        calcPoint.getPoint().setArchiveInterval(SwingUtil.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));
        calcPoint.getCalcBase().setUpdateType((String) getUpdateTypeComboBox().getSelectedItem());
        calcPoint.getCalcBase().setPeriodicRate(SwingUtil.getIntervalComboBoxSecondsValue(getPeriodicRateComboBox()));

        if (getJCheckboxCalcQual().isSelected()) {
            calcPoint.getCalcBase().setCalculateQuality('Y');
        } else {
            calcPoint.getCalcBase().setCalculateQuality('N');
        }

        calcPoint.getPointUnit().setDecimalPlaces(((Number) getDecimalPlacesSpinner().getValue()).intValue());
        calcPoint.getPointUnit().setUomID(uOfMeasureID);
        LiteStateGroup stateGroup = (LiteStateGroup) getStateGroupComboBox().getSelectedItem();
        calcPoint.getPoint().setStateGroupID(stateGroup.getStateGroupID());

        return calcPoint;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
        ;
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {

        getDecimalPlacesSpinner().addValueListener(this);
        getPeriodicRateComboBox().addActionListener(this);
        getArchiveTypeComboBox().addActionListener(this);
        getArchiveIntervalComboBox().addActionListener(this);
        getUpdateTypeComboBox().addActionListener(this);
        getUnitOfMeasureComboBox().addActionListener(this);
        getJCheckboxCalcQual().addActionListener(this);
        getStateGroupComboBox().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("CalcBasePanel");
            setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
            constraintsJPanelArchive.gridx = 1;
            constraintsJPanelArchive.gridy = 2;
            constraintsJPanelArchive.gridwidth = 4;
            constraintsJPanelArchive.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelArchive.weightx = 1.0;
            constraintsJPanelArchive.weighty = 1.0;
            constraintsJPanelArchive.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getJPanelArchive(), constraintsJPanelArchive);

            java.awt.GridBagConstraints constraintsUpdateTypeLabel = new java.awt.GridBagConstraints();
            constraintsUpdateTypeLabel.gridx = 1;
            constraintsUpdateTypeLabel.gridy = 3;
            constraintsUpdateTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUpdateTypeLabel.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getUpdateTypeLabel(), constraintsUpdateTypeLabel);

            java.awt.GridBagConstraints constraintsUpdateTypeComboBox = new java.awt.GridBagConstraints();
            constraintsUpdateTypeComboBox.gridx = 2;
            constraintsUpdateTypeComboBox.gridy = 3;
            constraintsUpdateTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsUpdateTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUpdateTypeComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getUpdateTypeComboBox(), constraintsUpdateTypeComboBox);

            java.awt.GridBagConstraints constraintsPeriodicRateLabel = new java.awt.GridBagConstraints();
            constraintsPeriodicRateLabel.gridx = 3;
            constraintsPeriodicRateLabel.gridy = 3;
            constraintsPeriodicRateLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPeriodicRateLabel.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getPeriodicRateLabel(), constraintsPeriodicRateLabel);

            java.awt.GridBagConstraints constraintsPeriodicRateComboBox = new java.awt.GridBagConstraints();
            constraintsPeriodicRateComboBox.gridx = 4;
            constraintsPeriodicRateComboBox.gridy = 3;
            constraintsPeriodicRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsPeriodicRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPeriodicRateComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getPeriodicRateComboBox(), constraintsPeriodicRateComboBox);

            java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
            constraintsJPanelHolder.gridx = 1;
            constraintsJPanelHolder.gridy = 1;
            constraintsJPanelHolder.gridwidth = 4;
            constraintsJPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelHolder.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getJPanelHolder(), constraintsJPanelHolder);

            java.awt.GridBagConstraints constraintsJCheckboxCalcQaul = new java.awt.GridBagConstraints();
            constraintsJCheckboxCalcQaul.gridx = 1;
            constraintsJCheckboxCalcQaul.gridy = 4;
            constraintsJCheckboxCalcQaul.gridwidth = 4;
            constraintsJCheckboxCalcQaul.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJCheckboxCalcQaul.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJCheckboxCalcQaul.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getJCheckboxCalcQual(), constraintsJCheckboxCalcQaul);

            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // Put a border around the Calculation section of panel
        javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Calculation Summary");
        border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
        setBorder(border);

        // load unit of measure combo box with all possible values
        List<UnitOfMeasure> unitMeasures = UnitOfMeasure.allValidValues();
        for (UnitOfMeasure uom : unitMeasures) {
            getUnitOfMeasureComboBox().addItem(uom);
        }

        // Load the Archive Type combo box with default possible values
        getArchiveTypeComboBox().addItem(PointArchiveType.NONE.getDisplayName());
        getArchiveTypeComboBox().addItem(PointArchiveType.ON_CHANGE.getDisplayName());
        getArchiveTypeComboBox().addItem(PointArchiveType.ON_TIMER.getDisplayName());
        getArchiveTypeComboBox().addItem(PointArchiveType.ON_UPDATE.getDisplayName());
        getArchiveTypeComboBox().addItem(PointArchiveType.ON_TIMER_OR_UPDATE.getDisplayName());

        // Load the Archive Interval combo box with default possible values
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
        // getArchiveIntervalComboBox().addItem("24 hour");
        getArchiveIntervalComboBox().addItem("Daily");
        getArchiveIntervalComboBox().addItem("Weekly");
        getArchiveIntervalComboBox().addItem("Monthly");
        getArchiveIntervalComboBox().setSelectedItem("5 minute");

        // Load the Update Type combo box with default possible values
        getUpdateTypeComboBox().addItem("On First Change");
        getUpdateTypeComboBox().addItem("On All Change");
        getUpdateTypeComboBox().addItem("On Timer");
        getUpdateTypeComboBox().addItem("On Timer+Change");
        getUpdateTypeComboBox().addItem("Constant");
        getUpdateTypeComboBox().addItem("Historical");
        getUpdateTypeComboBox().addItem("Backfilling");

        // Load the Periodic Rate combo box with default possible values
        getPeriodicRateComboBox().addItem("1 second");
        getPeriodicRateComboBox().addItem("2 second");
        getPeriodicRateComboBox().addItem("5 second");
        getPeriodicRateComboBox().addItem("10 second");
        getPeriodicRateComboBox().addItem("15 second");
        getPeriodicRateComboBox().addItem("30 second");
        getPeriodicRateComboBox().addItem("1 minute");
        getPeriodicRateComboBox().addItem("2 minute");
        getPeriodicRateComboBox().addItem("3 minute");
        getPeriodicRateComboBox().addItem("5 minute");
        getPeriodicRateComboBox().addItem("10 minute");
        getPeriodicRateComboBox().addItem("15 minute");
        getPeriodicRateComboBox().addItem("30 minute");
        getPeriodicRateComboBox().addItem("1 hour");
        getPeriodicRateComboBox().addItem("2 hour");
        getPeriodicRateComboBox().addItem("6 hour");
        getPeriodicRateComboBox().addItem("12 hour");
        getPeriodicRateComboBox().addItem("1 day");
        // user code end
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    @Override
    public void setValue(Object val) {
        // Assume that defaultObject is an CalculatedPoint
        CalculatedPoint calcPoint = (CalculatedPoint) val;

        PointArchiveType archiveType = calcPoint.getPoint().getArchiveType();
        Integer archiveInteger = calcPoint.getPoint().getArchiveInterval();
        int uOfMeasureID = calcPoint.getPointUnit().getUomID().intValue();
        getArchiveIntervalLabel().setEnabled(false);
        getArchiveIntervalComboBox().setEnabled(false);

        for (int i = 0; i < getArchiveTypeComboBox().getModel().getSize(); i++) {
            if (getArchiveTypeComboBox().getItemAt(i).equalsIgnoreCase(archiveType.getDisplayName())) {
                getArchiveTypeComboBox().setSelectedIndex(i);
                if (getArchiveIntervalComboBox().isEnabled()) {
                    SwingUtil.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(), archiveInteger.intValue());
                }
                break;
            }
        }

        for (int i = 0; i < getUnitOfMeasureComboBox().getModel().getSize(); i++) {
            if (getUnitOfMeasureComboBox().getItemAt(i).getId() == uOfMeasureID) {
                getUnitOfMeasureComboBox().setSelectedIndex(i);
                break;
            }
        }

        String updateType = calcPoint.getCalcBase().getUpdateType();
        Integer periodicRate = calcPoint.getCalcBase().getPeriodicRate();

        getPeriodicRateLabel().setEnabled(false);
        getPeriodicRateComboBox().setEnabled(false);

        for (int i = 0; i < getUpdateTypeComboBox().getModel().getSize(); i++) {
            if (getUpdateTypeComboBox().getItemAt(i).equalsIgnoreCase(updateType)) {
                getUpdateTypeComboBox().setSelectedIndex(i);
                if (getPeriodicRateComboBox().isEnabled()) {
                    SwingUtil.setIntervalComboBoxSelectedItem(getPeriodicRateComboBox(), periodicRate.intValue());
                }
                break;
            }
        }

        getDecimalPlacesSpinner().setValue(calcPoint.getPointUnit().getDecimalPlaces());

        if (calcPoint.getCalcBase().getCalculateQuality() == 'Y' || calcPoint.getCalcBase().getCalculateQuality() == 'y') {
            getJCheckboxCalcQual().setSelected(true);
        } else {
            getJCheckboxCalcQual().setSelected(false);
        }

        // load and set stategroups
        int stateGroupID = calcPoint.getPoint().getStateGroupID().intValue();

        // Load all the state groups
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteStateGroup> allStateGroups = YukonSpringHook.getBean(StateGroupDao.class).getAllStateGroups();

            for (LiteStateGroup grp : allStateGroups) {
                getStateGroupComboBox().addItem(grp);
                if (grp.getStateGroupID() == stateGroupID) {
                    getStateGroupComboBox().setSelectedItem(grp);
                }
            }
        }

    }

    public void updateTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        String val = getUpdateTypeComboBox().getSelectedItem().toString();

        getPeriodicRateLabel().setEnabled("On Timer".equalsIgnoreCase(val) || "On Timer+Change".equalsIgnoreCase(val));

        getPeriodicRateComboBox().setEnabled("On Timer".equalsIgnoreCase(val) || "On Timer+Change".equalsIgnoreCase(val));

        fireInputUpdate();

        return;
    }

    @Override
    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
        if (arg1.getSource() == getDecimalPlacesSpinner()) {
            fireInputUpdate();
        }
    }

    @Override
    public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
    }
}
package com.cannontech.dbeditor.editor.point;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.core.dao.UnitMeasureDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.AccumulatorPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.JCValueListener;
import com.klg.jclass.util.value.MutableValueModel;

public class AccumulatorBasePanel extends DataInputPanel implements JCValueListener, ActionListener {
    private JComboBox<LiteUnitMeasure> unitOfMeasureComboBox = null;
    private JLabel unitOfMeasureLabel = null;
    private JComboBox<String> archiveIntervalComboBox = null;
    private JLabel archiveIntervalLabel = null;
    private JComboBox<String> archiveTypeComboBox = null;
    private JLabel archiveTypeLabel = null;
    private JCSpinField decimalPlaceSpinner = null;
    private JLabel decimalPositionsLabel = null;
    private JPanel archivePanel = null;
    private JPanel holderPanel = null;
    private JLabel stateGroupLabel = null;
    private JComboBox<LiteStateGroup> stateGroupComboBox = null;
    private JLabel meterDialsLabel = null;
    private JCSpinField meterDialsSpinner = null;

    public AccumulatorBasePanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getUnitOfMeasureComboBox() ||
                e.getSource() == getArchiveIntervalComboBox() ||
                e.getSource() == getStateGroupComboBox()) {
            try {
                this.fireInputUpdate();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        if (e.getSource() == getArchiveTypeComboBox()) {
            connEtoC3(e);
        }
    }

    /**
     * connEtoC3:
     * (ArchiveTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent)
     * --> AccumulatorBasePanel.fireInputUpdate()V)
     */
    private void connEtoC3(java.awt.event.ActionEvent arg1) {
        try {
            this.fireInputUpdate();
            if (((String) getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointArchiveType.NONE.getPointArchiveTypeName()) || ((String) getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointArchiveType.ON_CHANGE.getPointArchiveTypeName()) || ((String) getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointArchiveType.ON_UPDATE.getPointArchiveTypeName())) {
                getArchiveIntervalLabel().setEnabled(false);
                getArchiveIntervalComboBox().setEnabled(false);
            } else {
                getArchiveIntervalLabel().setEnabled(true);
                getArchiveIntervalComboBox().setEnabled(true);
                getArchiveIntervalComboBox().setSelectedItem("5 minute");

                if (PointArchiveType.ON_TIMER_OR_UPDATE.getDisplayName().equalsIgnoreCase(((String) getArchiveTypeComboBox().getSelectedItem()))) {
                    getArchiveIntervalComboBox().setSelectedItem("Daily");
                }
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JComboBox<String> getArchiveIntervalComboBox() {
        if (archiveIntervalComboBox == null) {
            try {
                archiveIntervalComboBox = new JComboBox<String>();
                archiveIntervalComboBox.setName("ArchiveIntervalComboBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveIntervalComboBox;
    }

    private javax.swing.JLabel getArchiveIntervalLabel() {
        if (archiveIntervalLabel == null) {
            try {
                archiveIntervalLabel = new javax.swing.JLabel();
                archiveIntervalLabel.setName("ArchiveIntervalLabel");
                archiveIntervalLabel.setText("Archive Interval:");
                archiveIntervalLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveIntervalLabel;
    }

    private JComboBox<String> getArchiveTypeComboBox() {
        if (archiveTypeComboBox == null) {
            try {
                archiveTypeComboBox = new JComboBox<String>();
                archiveTypeComboBox.setName("ArchiveTypeComboBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveTypeComboBox;
    }

    private javax.swing.JLabel getArchiveTypeLabel() {
        if (archiveTypeLabel == null) {
            try {
                archiveTypeLabel = new javax.swing.JLabel();
                archiveTypeLabel.setName("ArchiveTypeLabel");
                archiveTypeLabel.setText("Data Archive Type:");
                archiveTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveTypeLabel;
    }

    /**
     * Return the DecimalPlacesSpinner property value.
     * @return com.klg.jclass.field.JCSpinField
     */
    private com.klg.jclass.field.JCSpinField getDecimalPlacesSpinner() {
        if (decimalPlaceSpinner == null) {
            try {
                decimalPlaceSpinner = new com.klg.jclass.field.JCSpinField();
                decimalPlaceSpinner.setName("DecimalPlacesSpinner");
                decimalPlaceSpinner.setBackground(java.awt.Color.white);
                decimalPlaceSpinner.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1),
                                                                                                "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                new JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
                decimalPlaceSpinner.setMinimumSize(new java.awt.Dimension(50, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return decimalPlaceSpinner;
    }

    private javax.swing.JLabel getDecimalPositionsLabel() {
        if (decimalPositionsLabel == null) {
            try {
                decimalPositionsLabel = new javax.swing.JLabel();
                decimalPositionsLabel.setName("JLabelDecimalPositons");
                decimalPositionsLabel.setFont(new java.awt.Font("dialog", 0, 14));
                decimalPositionsLabel.setText("Decimal Places:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return decimalPositionsLabel;
    }

    private javax.swing.JLabel getStateGroupLabel() {
        if (stateGroupLabel == null) {
            try {
                stateGroupLabel = new javax.swing.JLabel();
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
                stateGroupComboBox = new JComboBox<LiteStateGroup>();
                stateGroupComboBox.setName("StateGroupComboBoxl");
                stateGroupComboBox.setFont(new java.awt.Font("dialog", 0, 14));

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return stateGroupComboBox;
    }

    private javax.swing.JPanel getJPanelArchive() {
        if (archivePanel == null) {
            try {
                archivePanel = new javax.swing.JPanel();
                archivePanel.setName("JPanelArchive");
                archivePanel.setLayout(new java.awt.GridBagLayout());

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
                constraintsArchiveTypeComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveTypeComboBox(), constraintsArchiveTypeComboBox);

                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Archive");
                archivePanel.setBorder(ivjLocalBorder);

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archivePanel;
    }

    private JComboBox<LiteUnitMeasure> getUnitOfMeasureComboBox() {
        if (unitOfMeasureComboBox == null) {
            try {
                unitOfMeasureComboBox = new JComboBox<LiteUnitMeasure>();
                unitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
                unitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return unitOfMeasureComboBox;
    }

    private javax.swing.JLabel getUnitOfMeasureLabel() {
        if (unitOfMeasureLabel == null) {
            try {
                unitOfMeasureLabel = new javax.swing.JLabel();
                unitOfMeasureLabel.setName("UnitOfMeasureLabel");
                unitOfMeasureLabel.setText("Unit of Measure:");
                unitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return unitOfMeasureLabel;
    }

    @Override
    public Object getValue(Object val) {
        // Assuming that commonObject is an instance of com.cannontech.database.data.point.AccumulatorPoint
        AccumulatorPoint point = (AccumulatorPoint) val;

        int uOfMeasureID = ((LiteUnitMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getUomID();

        point.getPointUnit().setUomID(new Integer(uOfMeasureID));// setUnit(uOfMeasure);
        point.getPointUnit().setDecimalPlaces(new Integer(((Number) getDecimalPlacesSpinner().getValue()).intValue()));
        point.getPointUnit().setMeterDials(new Integer(((Number) getMeterDialsSpinner().getValue()).intValue()));

        String selectedArchiveType = getArchiveTypeComboBox().getSelectedItem().toString();
        point.getPoint().setArchiveType(PointArchiveType.getByDisplayName(selectedArchiveType));

        point.getPoint().setArchiveInterval(SwingUtil.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));
        LiteStateGroup stateGroup = (LiteStateGroup) getStateGroupComboBox().getSelectedItem();
        point.getPoint().setStateGroupID(new Integer(stateGroup.getStateGroupID()));

        return point;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getDecimalPlacesSpinner().addValueListener(this);
        getMeterDialsSpinner().addValueListener(this);
        getUnitOfMeasureComboBox().addActionListener(this);
        getArchiveTypeComboBox().addActionListener(this);
        getArchiveIntervalComboBox().addActionListener(this);
        getStateGroupComboBox().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("AccumulatorBasePanel");
            setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
            constraintsJPanelHolder.gridx = 0;
            constraintsJPanelHolder.gridy = 0;
            constraintsJPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelHolder.weighty = 1.0;
            constraintsJPanelHolder.weightx = 1.0;
            constraintsJPanelHolder.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getJPanelHolder(), constraintsJPanelHolder);

            java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
            constraintsJPanelArchive.gridx = 0;
            constraintsJPanelArchive.gridy = 1;
            // constraintsJPanelArchive.gridwidth = 2;
            constraintsJPanelArchive.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
            // constraintsJPanelArchive.weightx = 1.0;
            // constraintsJPanelArchive.weighty = 1.0;
            constraintsJPanelArchive.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getJPanelArchive(), constraintsJPanelArchive);

            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // Put a border around the Accumulator section of panel
        javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Accumulator Summary");
        border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
        setBorder(border);

        // Load the unit of measure combo box with default possible values
        List<LiteUnitMeasure> unitMeasures = YukonSpringHook.getBean(UnitMeasureDao.class).getLiteUnitMeasures();
        for (LiteUnitMeasure lum : unitMeasures) {
            getUnitOfMeasureComboBox().addItem(lum);
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
        getArchiveIntervalComboBox().addItem("Daily");
        getArchiveIntervalComboBox().addItem("Weekly");
        getArchiveIntervalComboBox().addItem("Monthly");
        getArchiveIntervalComboBox().setSelectedItem("5 minute");
    }

    private javax.swing.JPanel getJPanelHolder() {
        if (holderPanel == null) {
            try {
                holderPanel = new javax.swing.JPanel();
                holderPanel.setName("JPanelHolder");
                holderPanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
                constraintsUnitOfMeasureLabel.gridx = 0;
                constraintsUnitOfMeasureLabel.gridy = 0;
                constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                holderPanel.add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

                java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
                constraintsUnitOfMeasureComboBox.gridx = 1;
                constraintsUnitOfMeasureComboBox.gridy = 0;
                constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsUnitOfMeasureComboBox.weightx = 1.0;
                constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                holderPanel.add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

                java.awt.GridBagConstraints constraintsStateGroupLabel = new java.awt.GridBagConstraints();
                constraintsStateGroupLabel.gridx = 0;
                constraintsStateGroupLabel.gridy = 1;
                constraintsStateGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsStateGroupLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                holderPanel.add(getStateGroupLabel(), constraintsStateGroupLabel);

                java.awt.GridBagConstraints constraintsStateGroupComboBox = new java.awt.GridBagConstraints();
                constraintsStateGroupComboBox.gridx = 1;
                constraintsStateGroupComboBox.gridy = 1;
                constraintsStateGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsStateGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsStateGroupComboBox.weightx = 1.0;
                constraintsStateGroupComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                holderPanel.add(getStateGroupComboBox(), constraintsStateGroupComboBox);

                java.awt.GridBagConstraints constraintsJLabelDecimalPositons = new java.awt.GridBagConstraints();
                constraintsJLabelDecimalPositons.gridx = 0;
                constraintsJLabelDecimalPositons.gridy = 2;
                constraintsJLabelDecimalPositons.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelDecimalPositons.insets = new java.awt.Insets(2, 2, 2, 2);
                holderPanel.add(getDecimalPositionsLabel(), constraintsJLabelDecimalPositons);

                java.awt.GridBagConstraints constraintsDecimalPlacesSpinner = new java.awt.GridBagConstraints();
                constraintsDecimalPlacesSpinner.gridx = 1;
                constraintsDecimalPlacesSpinner.gridy = 2;
                constraintsDecimalPlacesSpinner.anchor = java.awt.GridBagConstraints.WEST;
                // constraintsDecimalPlacesSpinner.weightx = 1.0;
                constraintsDecimalPlacesSpinner.insets = new java.awt.Insets(2, 2, 2, 2);
                holderPanel.add(getDecimalPlacesSpinner(), constraintsDecimalPlacesSpinner);

                java.awt.GridBagConstraints constraintsJLabelMeterDials = new java.awt.GridBagConstraints();
                constraintsJLabelMeterDials.gridx = 0;
                constraintsJLabelMeterDials.gridy = 3;
                constraintsJLabelMeterDials.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelMeterDials.insets = new java.awt.Insets(2, 2, 2, 2);
                holderPanel.add(getMeterDialsLabel(), constraintsJLabelMeterDials);

                java.awt.GridBagConstraints constraintsMeterDialsSpinner = new java.awt.GridBagConstraints();
                constraintsMeterDialsSpinner.gridx = 1;
                constraintsMeterDialsSpinner.gridy = 3;
                constraintsMeterDialsSpinner.anchor = java.awt.GridBagConstraints.WEST;
                // constraintsMeterDialsSpinner.weightx = 1.0;
                constraintsMeterDialsSpinner.insets = new java.awt.Insets(2, 2, 2, 2);
                holderPanel.add(getMeterDialsSpinner(), constraintsMeterDialsSpinner);

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return holderPanel;
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    @Override
    public void setValue(Object val) {
        // Assuming defaultObject is an instance of com.cannontech.database.data.point.AccumulatorPoint
        AccumulatorPoint point = (AccumulatorPoint) val;

        int uOfMeasureID = point.getPointUnit().getUomID().intValue();
        PointArchiveType archiveType = point.getPoint().getArchiveType();
        Integer archiveInteger = point.getPoint().getArchiveInterval();

        getDecimalPlacesSpinner().setValue(point.getPointUnit().getDecimalPlaces());
        getMeterDialsSpinner().setValue(point.getPointUnit().getMeterDials());
        getArchiveIntervalLabel().setEnabled(false);
        getArchiveIntervalComboBox().setEnabled(false);

        for (int i = 0; i < getUnitOfMeasureComboBox().getModel().getSize(); i++) {
            if (getUnitOfMeasureComboBox().getItemAt(i).getUomID() == uOfMeasureID) {
                getUnitOfMeasureComboBox().setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < getArchiveTypeComboBox().getModel().getSize(); i++) {
            if (getArchiveTypeComboBox().getItemAt(i).equalsIgnoreCase(archiveType.getDisplayName())) {
                getArchiveTypeComboBox().setSelectedIndex(i);
                if (getArchiveIntervalComboBox().isEnabled()) {
                    SwingUtil.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(), archiveInteger.intValue());
                }
                break;
            }
        }

        // load and set stategroups
        int stateGroupID = point.getPoint().getStateGroupID().intValue();

        // Load all the state groups
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteStateGroup> allStateGroups = YukonSpringHook.getBean(StateGroupDao.class).getAllStateGroups();

            // Load the state table combo box
            for (LiteStateGroup grp : allStateGroups) {
                getStateGroupComboBox().addItem(grp);
                if (grp.getStateGroupID() == stateGroupID) {
                    getStateGroupComboBox().setSelectedItem(grp);
                }
            }
        }
    }

    @Override
    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
        if (arg1.getSource() == getDecimalPlacesSpinner() || arg1.getSource() == getMeterDialsSpinner())
            this.fireInputUpdate();
    }

    @Override
    public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
    }

    private com.klg.jclass.field.JCSpinField getMeterDialsSpinner() {
        if (meterDialsSpinner == null) {
            try {
                meterDialsSpinner = new com.klg.jclass.field.JCSpinField();
                meterDialsSpinner.setName("MeterDialsSpinner");
                meterDialsSpinner.setBackground(java.awt.Color.white);
                meterDialsSpinner.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1),
                                                                                              "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                              new MutableValueModel(Integer.class, new Integer(0)),
                                                                                              new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
                meterDialsSpinner.setMinimumSize(new java.awt.Dimension(50, 20));
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
                meterDialsLabel.setName("JLabelMeterDialsSpinner");
                meterDialsLabel.setFont(new java.awt.Font("dialog", 0, 14));
                meterDialsLabel.setText("Meter Digits (Dials):");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return meterDialsLabel;
    }
}
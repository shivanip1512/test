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
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointArchiveType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.JCValueListener;
import com.klg.jclass.util.value.MutableValueModel;

public class AnalogBasePanel extends DataInputPanel implements ActionListener, JCValueListener {
    private JComboBox<LiteUnitMeasure> unitOfMeasureComboBox = null;
    private JLabel unitOfMeasureLabel = null;
    private JComboBox<String> archiveIntervalComboBox = null;
    private JLabel archiveIntervalLabel = null;
    private JComboBox<String> archiveTypeComboBox = null;
    private JLabel archiveTypeLabel = null;
    private JLabel decimalPlacesLabel = null;
    private JCSpinField decimalPlacesSpinner = null;
    private JLabel meterDialsLabel = null;
    private JCSpinField meterDialsSpinner = null;
    private JPanel archivePanel = null;
    private JPanel holderPanel = null;
    private JComboBox<LiteStateGroup> stateGroupComboBox = null;
    private JLabel stateGroupLabel = null;

    public AnalogBasePanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getArchiveTypeComboBox()) {
                this.archiveTypeComboBox_ActionPerformed(e);
            }
            if (e.getSource() == getUnitOfMeasureComboBox() ||
                    e.getSource() == getArchiveIntervalComboBox() ||
                    e.getSource() == getStateGroupComboBox()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void archiveTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        fireInputUpdate();

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

        return;
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
                archiveIntervalLabel.setText("Interval:");
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
                archiveTypeLabel.setText("Data Type:");
                archiveTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return archiveTypeLabel;
    }

    private JCSpinField getDecimalPlacesSpinner() {
        if (decimalPlacesSpinner == null) {
            try {
                decimalPlacesSpinner = new com.klg.jclass.field.JCSpinField();
                decimalPlacesSpinner.setName("DecimalPlacesSpinner");
                decimalPlacesSpinner.setBackground(java.awt.Color.white);
                decimalPlacesSpinner.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1),
                                                                                                 "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                 new MutableValueModel(Integer.class, new Integer(0)),
                                                                                                 new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
                decimalPlacesSpinner.setMinimumSize(new java.awt.Dimension(50, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return decimalPlacesSpinner;
    }

    private JCSpinField getMeterDialsSpinner() {
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
                meterDialsLabel.setText("Meter Digits (Dials):");
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
                constraintsArchiveTypeLabel.gridx = 0;
                constraintsArchiveTypeLabel.gridy = 0;
                constraintsArchiveTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveTypeLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveTypeLabel(), constraintsArchiveTypeLabel);

                java.awt.GridBagConstraints constraintsArchiveIntervalLabel = new java.awt.GridBagConstraints();
                constraintsArchiveIntervalLabel.gridx = 0;
                constraintsArchiveIntervalLabel.gridy = 1;
                constraintsArchiveIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveIntervalLabel.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveIntervalLabel(), constraintsArchiveIntervalLabel);

                java.awt.GridBagConstraints constraintsArchiveIntervalComboBox = new java.awt.GridBagConstraints();
                constraintsArchiveIntervalComboBox.gridx = 1;
                constraintsArchiveIntervalComboBox.gridy = 1;
                constraintsArchiveIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsArchiveIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveIntervalComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelArchive().add(getArchiveIntervalComboBox(), constraintsArchiveIntervalComboBox);

                java.awt.GridBagConstraints constraintsArchiveTypeComboBox = new java.awt.GridBagConstraints();
                constraintsArchiveTypeComboBox.gridx = 1;
                constraintsArchiveTypeComboBox.gridy = 0;
                constraintsArchiveTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsArchiveTypeComboBox.weightx = 1.0;
                constraintsArchiveTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsArchiveTypeComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
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
                // constraintsUnitOfMeasureComboBox.gridwidth = 2;
                constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
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
                constraintsStateGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                // constraintsStateGroupComboBox.gridwidth = 2;
                constraintsStateGroupComboBox.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getStateGroupComboBox(), constraintsStateGroupComboBox);

                java.awt.GridBagConstraints constraintsJLabelDecimalPositons = new java.awt.GridBagConstraints();
                constraintsJLabelDecimalPositons.gridx = 0;
                constraintsJLabelDecimalPositons.gridy = 2;
                constraintsJLabelDecimalPositons.anchor = java.awt.GridBagConstraints.WEST;
                constraintsJLabelDecimalPositons.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getJLabelDecimalPositions(), constraintsJLabelDecimalPositons);

                java.awt.GridBagConstraints constraintsDecimalPlacesSpinner = new java.awt.GridBagConstraints();
                constraintsDecimalPlacesSpinner.gridx = 1;
                constraintsDecimalPlacesSpinner.gridy = 2;
                constraintsDecimalPlacesSpinner.anchor = java.awt.GridBagConstraints.WEST;
                constraintsDecimalPlacesSpinner.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getDecimalPlacesSpinner(), constraintsDecimalPlacesSpinner);

                java.awt.GridBagConstraints constraintJLabelMeterDials = new java.awt.GridBagConstraints();
                constraintJLabelMeterDials.gridx = 0;
                constraintJLabelMeterDials.gridy = 3;
                constraintJLabelMeterDials.anchor = java.awt.GridBagConstraints.WEST;
                constraintJLabelMeterDials.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getJLabelMeterDials(), constraintJLabelMeterDials);

                java.awt.GridBagConstraints constraintsMeterDialsSpinner = new java.awt.GridBagConstraints();
                constraintsMeterDialsSpinner.gridx = 1;
                constraintsMeterDialsSpinner.gridy = 3;
                constraintsMeterDialsSpinner.anchor = java.awt.GridBagConstraints.WEST;
                constraintsMeterDialsSpinner.insets = new java.awt.Insets(2, 2, 2, 2);
                getJPanelHolder().add(getMeterDialsSpinner(), constraintsMeterDialsSpinner);

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return holderPanel;
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

    private javax.swing.JLabel getStateGroupLabel() {
        if (stateGroupLabel == null) {
            try {
                stateGroupLabel = new javax.swing.JLabel();
                stateGroupLabel.setName("StateGroupLabel");
                stateGroupLabel.setText("State Group:");
                stateGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
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
                stateGroupComboBox.setName("StateGroupComboBox");
                stateGroupComboBox.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return stateGroupComboBox;
    }

    @Override
    public Object getValue(Object val) {
        // Assume that commonObject is an instance of com.cannontech.database.data.point.AnalogPoint
        AnalogPoint point = (AnalogPoint) val;

        int uOfMeasureID = ((LiteUnitMeasure) getUnitOfMeasureComboBox().getSelectedItem()).getUomID();

        point.getPointUnit().setUomID(new Integer(uOfMeasureID));

        String selectedArchiveType = getArchiveTypeComboBox().getSelectedItem().toString();
        point.getPoint().setArchiveType(PointArchiveType.getByDisplayName(selectedArchiveType));

        point.getPoint().setArchiveInterval(SwingUtil.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));

        point.getPointUnit().setDecimalPlaces(new Integer(((Number) getDecimalPlacesSpinner().getValue()).intValue()));
        point.getPointUnit().setMeterDials(new Integer(((Number) getMeterDialsSpinner().getValue()).intValue()));

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
            setLayout(new java.awt.GridBagLayout());

            java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
            constraintsJPanelArchive.gridx = 0;
            constraintsJPanelArchive.gridy = 1;
            constraintsJPanelArchive.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelArchive.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getJPanelArchive(), constraintsJPanelArchive);

            java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
            constraintsJPanelHolder.gridx = 0;
            constraintsJPanelHolder.gridy = 0;
            constraintsJPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
            constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJPanelHolder.weighty = 1.0;
            constraintsJPanelHolder.weightx = 1.0;
            constraintsJPanelHolder.insets = new java.awt.Insets(2, 2, 2, 2);
            add(getJPanelHolder(), constraintsJPanelHolder);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }

        // Put a border around the Analog section of panel
        javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Analog Summary");
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

    @Override
    public boolean isInputValid() {
        return true;
    }

    @Override
    public void setValue(Object val) {
        // Assume that defaultObject is an AnalogPoint
        com.cannontech.database.data.point.AnalogPoint point = (com.cannontech.database.data.point.AnalogPoint) val;

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
                if (getArchiveIntervalComboBox().isEnabled())
                    SwingUtil.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(), archiveInteger.intValue());
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
        if ((arg1.getSource() == getDecimalPlacesSpinner()) || (arg1.getSource() == getMeterDialsSpinner()))
            this.fireInputUpdate();
    }

    @Override
    public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
    }
}
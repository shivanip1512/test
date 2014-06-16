package com.cannontech.dbeditor.editor.route;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.JCValueListener;
import com.klg.jclass.util.value.MutableValueModel;

public class CommunicationRouteEditorPanel extends DataInputPanel implements ItemListener, CaretListener, JCValueListener {
    private JCheckBox ivjDefaultRouteCheckBox = null;
    private JLabel ivjBusNumberLabel = null;
    private JCSpinField ivjBusNumberSpinner = null;
    private JLabel ivjRouteNameLabel = null;
    private javax.swing.JTextField ivjRouteNameTextField = null;
    private JComboBox<LiteYukonPAObject> ivjSignalTransmitterComboBox = null;
    private JLabel ivjSignalTransmitterLabel = null;
    private JPanel ivjConfigurationPanel = null;
    private JPanel ivjIdentificationPanel = null;
    private JLabel ivjJLabelPrtNum = null;

    public CommunicationRouteEditorPanel() {
        super();
        initialize();
    }

    /**
     * Method to handle events for the CaretListener interface.
     * @param e javax.swing.event.CaretEvent
     */
    @Override
    public void caretUpdate(javax.swing.event.CaretEvent e) {
        try {
            if (e.getSource() == getRouteNameTextField()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JLabel getBusNumberLabel() {
        if (ivjBusNumberLabel == null) {
            try {
                ivjBusNumberLabel = new JLabel();
                ivjBusNumberLabel.setName("BusNumberLabel");
                ivjBusNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjBusNumberLabel.setText("Bus Number:");
                ivjBusNumberLabel.setVisible(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBusNumberLabel;
    }

    private JCSpinField getBusNumberSpinner() {
        if (ivjBusNumberSpinner == null) {
            try {
                ivjBusNumberSpinner = new JCSpinField();
                ivjBusNumberSpinner.setName("BusNumberSpinner");
                ivjBusNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
                ivjBusNumberSpinner.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjBusNumberSpinner.setBackground(java.awt.Color.white);
                ivjBusNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
                ivjBusNumberSpinner.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(1), new Integer(8), null, true, null, new Integer(1),
                                                                                                "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)),
                                                                                                new MutableValueModel(Integer.class, new Integer(1)),
                                                                                                new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjBusNumberSpinner;
    }

    private JPanel getConfigurationPanel() {
        if (ivjConfigurationPanel == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder1.setTitle("Settings");
                ivjConfigurationPanel = new JPanel();
                ivjConfigurationPanel.setName("ConfigurationPanel");
                ivjConfigurationPanel.setBorder(ivjLocalBorder1);
                ivjConfigurationPanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsDefaultRouteCheckBox = new java.awt.GridBagConstraints();
                constraintsDefaultRouteCheckBox.gridx = 1;
                constraintsDefaultRouteCheckBox.gridy = 1;
                constraintsDefaultRouteCheckBox.gridwidth = 2;
                constraintsDefaultRouteCheckBox.ipadx = 7;
                constraintsDefaultRouteCheckBox.insets = new java.awt.Insets(4, 12, 3, 36);
                getConfigurationPanel().add(getDefaultRouteCheckBox(), constraintsDefaultRouteCheckBox);

                java.awt.GridBagConstraints constraintsBusNumberLabel = new java.awt.GridBagConstraints();
                constraintsBusNumberLabel.gridx = 1;
                constraintsBusNumberLabel.gridy = 2;
                constraintsBusNumberLabel.insets = new java.awt.Insets(6, 12, 16, 4);
                getConfigurationPanel().add(getBusNumberLabel(), constraintsBusNumberLabel);

                java.awt.GridBagConstraints constraintsBusNumberSpinner = new java.awt.GridBagConstraints();
                constraintsBusNumberSpinner.gridx = 2;
                constraintsBusNumberSpinner.gridy = 2;
                constraintsBusNumberSpinner.ipadx = 14;
                constraintsBusNumberSpinner.insets = new java.awt.Insets(4, 5, 15, 1);
                getConfigurationPanel().add(getBusNumberSpinner(), constraintsBusNumberSpinner);

                java.awt.GridBagConstraints constraintsJLabelPrtNum = new java.awt.GridBagConstraints();
                constraintsJLabelPrtNum.gridx = 3;
                constraintsJLabelPrtNum.gridy = 2;
                constraintsJLabelPrtNum.ipadx = 17;
                constraintsJLabelPrtNum.insets = new java.awt.Insets(8, 2, 19, 94);
                getConfigurationPanel().add(getJLabelPrtNum(), constraintsJLabelPrtNum);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjConfigurationPanel;
    }

    private JCheckBox getDefaultRouteCheckBox() {
        if (ivjDefaultRouteCheckBox == null) {
            try {
                ivjDefaultRouteCheckBox = new JCheckBox();
                ivjDefaultRouteCheckBox.setName("DefaultRouteCheckBox");
                ivjDefaultRouteCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDefaultRouteCheckBox.setText("Default Route");
                ivjDefaultRouteCheckBox.setVisible(true);
                ivjDefaultRouteCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDefaultRouteCheckBox;
    }

    private JPanel getIdentificationPanel() {
        if (ivjIdentificationPanel == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
                ivjLocalBorder.setTitle("Identification");
                ivjIdentificationPanel = new JPanel();
                ivjIdentificationPanel.setName("IdentificationPanel");
                ivjIdentificationPanel.setBorder(ivjLocalBorder);
                ivjIdentificationPanel.setLayout(new java.awt.GridBagLayout());

                java.awt.GridBagConstraints constraintsRouteNameLabel = new java.awt.GridBagConstraints();
                constraintsRouteNameLabel.gridx = 0;
                constraintsRouteNameLabel.gridy = 0;
                constraintsRouteNameLabel.anchor = GridBagConstraints.NORTHWEST;
                constraintsRouteNameLabel.fill = GridBagConstraints.HORIZONTAL;
                constraintsRouteNameLabel.insets = new java.awt.Insets(5, 5, 5, 5);
                getIdentificationPanel().add(getRouteNameLabel(), constraintsRouteNameLabel);

                java.awt.GridBagConstraints constraintsRouteNameTextField = new java.awt.GridBagConstraints();
                constraintsRouteNameTextField.gridx = 0;
                constraintsRouteNameTextField.gridy = 1;
                constraintsRouteNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsRouteNameTextField.weightx = 1.0;
                constraintsRouteNameTextField.insets = new java.awt.Insets(5, 5, 5, 5);
                getIdentificationPanel().add(getRouteNameTextField(), constraintsRouteNameTextField);

                java.awt.GridBagConstraints constraintsSignalTransmitterLabel = new java.awt.GridBagConstraints();
                constraintsSignalTransmitterLabel.gridx = 0;
                constraintsSignalTransmitterLabel.gridy = 2;
                constraintsSignalTransmitterLabel.anchor = GridBagConstraints.NORTHWEST;
                constraintsSignalTransmitterLabel.fill = GridBagConstraints.HORIZONTAL;
                constraintsSignalTransmitterLabel.insets = new java.awt.Insets(5, 5, 5, 5);
                getIdentificationPanel().add(getSignalTransmitterLabel(), constraintsSignalTransmitterLabel);

                java.awt.GridBagConstraints constraintsSignalTransmitterComboBox = new java.awt.GridBagConstraints();
                constraintsSignalTransmitterComboBox.gridx = 0;
                constraintsSignalTransmitterComboBox.gridy = 3;
                constraintsSignalTransmitterComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsSignalTransmitterComboBox.weightx = 1.0;
                constraintsSignalTransmitterComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
                getIdentificationPanel().add(getSignalTransmitterComboBox(), constraintsSignalTransmitterComboBox);

            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIdentificationPanel;
    }

    private JLabel getJLabelPrtNum() {
        if (ivjJLabelPrtNum == null) {
            try {
                ivjJLabelPrtNum = new JLabel();
                ivjJLabelPrtNum.setName("JLabelPrtNum");
                ivjJLabelPrtNum.setFont(new java.awt.Font("dialog", 0, 12));
                ivjJLabelPrtNum.setText("(CCU Port #1 - 8)");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelPrtNum;
    }

    private JLabel getRouteNameLabel() {
        if (ivjRouteNameLabel == null) {
            try {
                ivjRouteNameLabel = new JLabel();
                ivjRouteNameLabel.setName("RouteNameLabel");
                ivjRouteNameLabel.setText("Route Name:");
                ivjRouteNameLabel.setMaximumSize(new java.awt.Dimension(81, 16));
                ivjRouteNameLabel.setPreferredSize(new java.awt.Dimension(81, 16));
                ivjRouteNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjRouteNameLabel.setMinimumSize(new java.awt.Dimension(81, 16));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRouteNameLabel;
    }

    private javax.swing.JTextField getRouteNameTextField() {
        if (ivjRouteNameTextField == null) {
            try {
                ivjRouteNameTextField = new javax.swing.JTextField();
                ivjRouteNameTextField.setName("RouteNameTextField");
                ivjRouteNameTextField.setMaximumSize(new java.awt.Dimension(2147483647, 20));
                ivjRouteNameTextField.setColumns(15);
                ivjRouteNameTextField.setPreferredSize(new java.awt.Dimension(132, 20));
                ivjRouteNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjRouteNameTextField.setMinimumSize(new java.awt.Dimension(132, 20));
                ivjRouteNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
                ivjRouteNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
                                                                        PaoUtils.ILLEGAL_NAME_CHARS));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRouteNameTextField;
    }

    private JComboBox<LiteYukonPAObject> getSignalTransmitterComboBox() {
        if (ivjSignalTransmitterComboBox == null) {
            try {
                ivjSignalTransmitterComboBox = new JComboBox<LiteYukonPAObject>();
                ivjSignalTransmitterComboBox.setName("SignalTransmitterComboBox");
                ivjSignalTransmitterComboBox.setPreferredSize(new java.awt.Dimension(170, 20));
                ivjSignalTransmitterComboBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjSignalTransmitterComboBox.setMinimumSize(new java.awt.Dimension(150, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSignalTransmitterComboBox;
    }

    private JLabel getSignalTransmitterLabel() {
        if (ivjSignalTransmitterLabel == null) {
            try {
                ivjSignalTransmitterLabel = new JLabel();
                ivjSignalTransmitterLabel.setName("SignalTransmitterLabel");
                ivjSignalTransmitterLabel.setText("Signal Transmitter:");
                ivjSignalTransmitterLabel.setMaximumSize(new java.awt.Dimension(114, 20));
                ivjSignalTransmitterLabel.setPreferredSize(new java.awt.Dimension(114, 20));
                ivjSignalTransmitterLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjSignalTransmitterLabel.setMinimumSize(new java.awt.Dimension(114, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSignalTransmitterLabel;
    }

    @Override
    public Object getValue(Object val) {
        String routeName = getRouteNameTextField().getText();
        Integer injectorID = new Integer(((LiteYukonPAObject) getSignalTransmitterComboBox().getSelectedItem()).getYukonID());

        if (routeName != null) {
            ((RouteBase) val).setRouteName(routeName);
        }

        if (injectorID != null) {
            ((RouteBase) val).setDeviceID(injectorID);
        }

        if (getDefaultRouteCheckBox().isSelected()) {
            ((RouteBase) val).setDefaultRoute(CtiUtilities.getTrueCharacter().toString());
        } else { 
            ((RouteBase) val).setDefaultRoute(CtiUtilities.getFalseCharacter().toString());
        }

        if (val instanceof CCURoute) {
            CCURoute ccuRoute = (CCURoute) val;

            Object spinVal = getBusNumberSpinner().getValue();
            if (spinVal instanceof Long) {
                ccuRoute.getCarrierRoute().setBusNumber(new Integer(((Long) spinVal).intValue()));
            }  else if (spinVal instanceof Integer) {
                ccuRoute.getCarrierRoute().setBusNumber(new Integer(((Integer) spinVal).intValue()));
            }
        }

        return val;
    }

    /**
     * Called whenever the part throws an exception.
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
        getBusNumberSpinner().addValueListener(this);

        getRouteNameTextField().addCaretListener(this);
        getSignalTransmitterComboBox().addItemListener(this);
        getDefaultRouteCheckBox().addItemListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("CommunicationRouteEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(398, 387);

            java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
            constraintsIdentificationPanel.gridx = 0;
            constraintsIdentificationPanel.gridy = 0;
            constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsIdentificationPanel.weightx = 1.0;
            constraintsIdentificationPanel.weighty = 1.0;
            constraintsIdentificationPanel.ipadx = -10;
            constraintsIdentificationPanel.ipady = -9;
            constraintsIdentificationPanel.insets = new java.awt.Insets(23, 13, 25, 12);
            add(getIdentificationPanel(), constraintsIdentificationPanel);

            java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
            constraintsConfigurationPanel.gridx = 0;
            constraintsConfigurationPanel.gridy = 1;
            constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsConfigurationPanel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsConfigurationPanel.weightx = 1.0;
            constraintsConfigurationPanel.weighty = 1.0;
            constraintsConfigurationPanel.ipadx = -10;
            constraintsConfigurationPanel.ipady = -13;
            constraintsConfigurationPanel.insets = new java.awt.Insets(6, 14, 25, 11);
            add(getConfigurationPanel(), constraintsConfigurationPanel);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        try {
            if (e.getSource() == getSignalTransmitterComboBox() ||
                    e.getSource() == getDefaultRouteCheckBox()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private void loadSignalTransmitterComboBox(PaoType routePaoType) {

        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();
            if (getSignalTransmitterComboBox().getModel().getSize() > 0)
                getSignalTransmitterComboBox().removeAllItems();

            if (routePaoType == PaoType.ROUTE_CCU) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (DeviceTypesFuncs.isCCU(liteDevice.getPaoType().getDeviceTypeId())) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                } // repeaters are not actually signal transmitters, so they should not be in the ComboBox || DeviceTypesFuncs.isRepeater(type)
            } else if (routePaoType == PaoType.ROUTE_TCU) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (DeviceTypesFuncs.isTCU(liteDevice.getPaoType().getDeviceTypeId())) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_LCU) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (DeviceTypesFuncs.isLCU(liteDevice.getPaoType().getDeviceTypeId())) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_TAP_PAGING) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (liteDevice.getPaoType() == PaoType.TAPTERMINAL) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_WCTP_TERMINAL) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (liteDevice.getPaoType() == PaoType.WCTP_TERMINAL) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_SNPP_TERMINAL) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (liteDevice.getPaoType() == PaoType.SNPP_TERMINAL) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_TNPP_TERMINAL) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (liteDevice.getPaoType() == PaoType.TNPP_TERMINAL) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_RDS_TERMINAL) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (liteDevice.getPaoType() == PaoType.RDS_TERMINAL) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_VERSACOM) {
                for (LiteYukonPAObject liteDevice : devices) {
                    int type = liteDevice.getPaoType().getDeviceTypeId();
                    if (DeviceTypesFuncs.isCCU(type) || DeviceTypesFuncs.isTCU(type) || DeviceTypesFuncs.isLCU(type)) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_SERIES_5_LMI) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (liteDevice.getPaoType() == PaoType.SERIES_5_LMI) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            } else if (routePaoType == PaoType.ROUTE_RTC) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (liteDevice.getPaoType() == PaoType.RTC) {
                        getSignalTransmitterComboBox().addItem(liteDevice);
                    }
                }
            }
        }

    }

    @Override
    public void setValue(Object val) {
        getBusNumberSpinner().setVisible(false);

        RouteBase rb = (RouteBase) val;

        String routeName = rb.getRouteName();
        int injectorID = rb.getDeviceID().intValue();

        if (routeName != null) {
            getRouteNameTextField().setText(routeName);
        }

        loadSignalTransmitterComboBox(rb.getPaoType());

        for (int i = 0; i < getSignalTransmitterComboBox().getItemCount(); i++) {
            if ((getSignalTransmitterComboBox().getItemAt(i)).getYukonID() == injectorID) {
                getSignalTransmitterComboBox().setSelectedIndex(i);
                break;
            }
        }

        /*
         * If it is a route that can not have a transmitter device for it, then do not show those values
         */
        if (val instanceof MacroRoute) {
            getDefaultRouteCheckBox().setVisible(false);
        } else {
            getDefaultRouteCheckBox().setVisible(true);
            if (rb.getDefaultRoute() != null) {
                SwingUtil.setCheckBoxState(getDefaultRouteCheckBox(), new Character(rb.getDefaultRoute().charAt(0)));
            } else {
                getDefaultRouteCheckBox().setSelected(true);
            }

            if (val instanceof CCURoute) {
                CCURoute ccuRoute = (CCURoute) val;
                if (ccuRoute.getDefaultRoute() != null) {
                    SwingUtil.setCheckBoxState(getDefaultRouteCheckBox(), new Character(ccuRoute.getDefaultRoute().charAt(0)));
                } else {
                    getDefaultRouteCheckBox().setSelected(true);
                }

                // set all our fields visible that are needed
                getBusNumberLabel().setVisible(true);
                getBusNumberSpinner().setVisible(true);

                if (ccuRoute.getCarrierRoute().getBusNumber() != null) {
                    getBusNumberSpinner().setValue(ccuRoute.getCarrierRoute().getBusNumber());
                } else {
                    getBusNumberSpinner().setValue(new Integer(1));
                }
            } else {
                getBusNumberLabel().setVisible(false);
                getBusNumberSpinner().setVisible(false);
                getJLabelPrtNum().setVisible(false);
            }
        }
    }

    @Override
    public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
        if (arg1.getSource() == getBusNumberSpinner())
            fireInputUpdate();
    }

    @Override
    public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
    }
}

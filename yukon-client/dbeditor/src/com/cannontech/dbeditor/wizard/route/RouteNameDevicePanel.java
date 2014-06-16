package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.yukon.IDatabaseCache;

public class RouteNameDevicePanel extends DataInputPanel implements ItemListener, CaretListener {
    private JLabel ivjRouteNameLabel = null;
    private JTextField ivjRouteNameTextField = null;
    private JLabel ivjSignalTransmitterLabel = null;
    private JComboBox<LiteYukonPAObject> ivjSignalTransmitterComboBox = null;

    public RouteNameDevicePanel() {
        super();
        initialize();
    }

    public boolean allowRebroadcast() {

        if (getSignalTransmitterComboBox().getSelectedItem() == null) {
            return false;
        } else {
            return DeviceTypesFuncs.allowRebroadcast(((LiteYukonPAObject) getSignalTransmitterComboBox().getSelectedItem()).getPaoType()
                                                                                                                           .getDeviceTypeId());
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource() == getRouteNameTextField()) {
            try {
                this.textField_CaretUpdate(e);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    public void comboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        return;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private JLabel getRouteNameLabel() {
        if (ivjRouteNameLabel == null) {
            try {
                ivjRouteNameLabel = new JLabel();
                ivjRouteNameLabel.setName("RouteNameLabel");
                ivjRouteNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjRouteNameLabel.setText("Route Name:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRouteNameLabel;
    }

    /**
     * Return the RouteNameTextField property value.
     * @return JTextField
     */
    private JTextField getRouteNameTextField() {
        if (ivjRouteNameTextField == null) {
            try {
                ivjRouteNameTextField = new JTextField();
                ivjRouteNameTextField.setName("RouteNameTextField");
                ivjRouteNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjRouteNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
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

                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                synchronized (cache) {
                    List<LiteYukonPAObject> allDevices = cache.getAllDevices();
                    for (LiteYukonPAObject litePAO : allDevices) {

                        if (litePAO.getPaoType().getPaoClass() == PaoClass.TRANSMITTER && 
                                !DeviceTypesFuncs.isRepeater(litePAO.getPaoType().getDeviceTypeId()) && 
                                litePAO.getPaoType() != PaoType.DIGIGATEWAY) {
                            getSignalTransmitterComboBox().addItem(litePAO);
                        }
                    }
                }
            } catch (Throwable ivjExc) {
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
                ivjSignalTransmitterLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjSignalTransmitterLabel.setText("Signal Transmitter:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSignalTransmitterLabel;
    }

    @Override
    public Object getValue(Object val) {

        // Determine type of route and its name
        // create that type with the name and return it

        String routeName = getRouteNameTextField().getText().trim();

        Integer deviceID = new Integer(((LiteYukonPAObject) getSignalTransmitterComboBox().getSelectedItem()).getYukonID());

        PaoType paoType = ((LiteYukonPAObject) getSignalTransmitterComboBox().getSelectedItem()).getPaoType();

        if (DeviceTypesFuncs.isCCU(paoType.getDeviceTypeId()) || DeviceTypesFuncs.isRepeater(paoType.getDeviceTypeId())) {
            val = RouteFactory.createRoute(PaoType.ROUTE_CCU);
        } else if (DeviceTypesFuncs.isTCU(paoType.getDeviceTypeId())) {
            val = RouteFactory.createRoute(PaoType.ROUTE_TCU);
        } else if (DeviceTypesFuncs.isLCU(paoType.getDeviceTypeId())) {
            val = RouteFactory.createRoute(PaoType.ROUTE_LCU);
        } else if (paoType == PaoType.TAPTERMINAL) {
            val = RouteFactory.createRoute(PaoType.ROUTE_TAP_PAGING);
        } else if (paoType == PaoType.WCTP_TERMINAL) {
            val = RouteFactory.createRoute(PaoType.ROUTE_WCTP_TERMINAL);
        } else if (paoType == PaoType.SNPP_TERMINAL) {
            val = RouteFactory.createRoute(PaoType.ROUTE_SNPP_TERMINAL);
        } else if (paoType == PaoType.TNPP_TERMINAL) {
            val = RouteFactory.createRoute(PaoType.ROUTE_TNPP_TERMINAL);
        } else if (paoType == PaoType.RTC) {
            val = RouteFactory.createRoute(PaoType.ROUTE_RTC);
        } else if (paoType == PaoType.SERIES_5_LMI) {
            val = RouteFactory.createRoute(PaoType.ROUTE_SERIES_5_LMI);
        } else if (paoType == PaoType.RDS_TERMINAL) {
            val = RouteFactory.createRoute(PaoType.ROUTE_RDS_TERMINAL);
        } else
            // ?
            throw new Error("RouteType2::getValue() - Unknown transmitter type");

        ((RouteBase) val).setDeviceID(deviceID);
        ((RouteBase) val).setDefaultRoute("Y");

        ((RouteBase) val).setRouteName(routeName);

        return val;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        // com.cannontech.clientutils.CTILogger.error( exception.getMessage(),
        // exception );;
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getRouteNameTextField().addCaretListener(this);
        getSignalTransmitterComboBox().addItemListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("RouteType2Panel");
            setLayout(new java.awt.GridBagLayout());
            setSize(374, 211);

            java.awt.GridBagConstraints constraintsRouteNameLabel = new java.awt.GridBagConstraints();
            constraintsRouteNameLabel.gridx = 0;
            constraintsRouteNameLabel.gridy = 0;
            constraintsRouteNameLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRouteNameLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            add(getRouteNameLabel(), constraintsRouteNameLabel);

            java.awt.GridBagConstraints constraintsRouteNameTextField = new java.awt.GridBagConstraints();
            constraintsRouteNameTextField.gridx = 0;
            constraintsRouteNameTextField.gridy = 1;
            constraintsRouteNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsRouteNameTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRouteNameTextField.weightx = 1.0;
            constraintsRouteNameTextField.insets = new java.awt.Insets(5, 5, 5, 5);
            add(getRouteNameTextField(), constraintsRouteNameTextField);

            java.awt.GridBagConstraints constraintsSignalTransmitterLabel = new java.awt.GridBagConstraints();
            constraintsSignalTransmitterLabel.gridx = 0;
            constraintsSignalTransmitterLabel.gridy = 2;
            constraintsSignalTransmitterLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSignalTransmitterLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            add(getSignalTransmitterLabel(), constraintsSignalTransmitterLabel);

            java.awt.GridBagConstraints constraintsSignalTransmitterComboBox = new java.awt.GridBagConstraints();
            constraintsSignalTransmitterComboBox.gridx = 0;
            constraintsSignalTransmitterComboBox.gridy = 3;
            constraintsSignalTransmitterComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsSignalTransmitterComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsSignalTransmitterComboBox.weightx = 1.0;
            constraintsSignalTransmitterComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
            add(getSignalTransmitterComboBox(), constraintsSignalTransmitterComboBox);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        if (getRouteNameTextField().getText().length() <= 0) {
            setErrorString("The Name text field must be filled in");
            return false;
        }

        return true;
    }

    @Override
    public void itemStateChanged(java.awt.event.ItemEvent e) {
        if (e.getSource() == getSignalTransmitterComboBox()) {
            try {
                this.fireInputUpdate();
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    public boolean noRepeaters() {
        boolean noRepeaters = true;

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();
            for (LiteYukonPAObject liteYukonPAObject : devices) {
                if (DeviceTypesFuncs.isRepeater(liteYukonPAObject.getPaoType().getDeviceTypeId())) {
                    noRepeaters = false;
                    break;
                }
            }
        }
        return noRepeaters;
    }

    @Override
    public void setValue(Object val) {
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getRouteNameTextField().requestFocus();
            }
        });
    }

    public void textField_CaretUpdate(CaretEvent caretEvent) {
        fireInputUpdate();
    }
}
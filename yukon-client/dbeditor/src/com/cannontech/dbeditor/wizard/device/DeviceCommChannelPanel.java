package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IDLCBase;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.device.Series5Base;
import com.cannontech.database.data.device.TNPPTerminal;
import com.cannontech.database.data.device.TapTerminalBase;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.db.device.DeviceIDLCRemote;
import com.cannontech.database.db.device.DeviceVerification;
import com.cannontech.database.db.route.CarrierRoute;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class DeviceCommChannelPanel extends DataInputPanel implements ActionListener, MouseListener, ListSelectionListener {
    private JComboBox<LiteYukonPAObject> ivjPortComboBox = null;
    private JLabel ivjPortLabel = null;

    // address attribute for checking against duplicates for ccu's and rtu's
    private int address = 0;
    public static final String TABLE_NAME = "DeviceIDLCRemote";
    private PaoType deviceType;

    public DeviceCommChannelPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getPortComboBox()) {
            try {
                this.fireInputUpdate();
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    public void deviceVirtualPortPanel_MouseClicked(MouseEvent mouseEvent) {
        fireInputUpdate();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    private JComboBox<LiteYukonPAObject> getPortComboBox() {
        if (ivjPortComboBox == null) {
            try {
                ivjPortComboBox = new JComboBox<LiteYukonPAObject>();
                ivjPortComboBox.setName("PortComboBox");
                ivjPortComboBox.setPreferredSize(new Dimension(190, 27));
                ivjPortComboBox.setMinimumSize(new Dimension(170, 27));

                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                synchronized (cache) {
                    List<LiteYukonPAObject> ports = cache.getAllPorts();
                    for (LiteYukonPAObject port : ports) {
                        getPortComboBox().addItem(port);
                    }
                }

            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPortComboBox;
    }

    private JLabel getPortLabel() {
        if (ivjPortLabel == null) {
            try {
                ivjPortLabel = new JLabel();
                ivjPortLabel.setName("PortLabel");
                ivjPortLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPortLabel.setText("Select a Communication Channel:");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPortLabel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private void checkAddress() {
        // hit the database to check for duplicate address on non-dial up comm channels
        LiteYukonPAObject port = ((LiteYukonPAObject) getPortComboBox().getSelectedItem());
        if (port == null) {
            return;
        }

        int portID = port.getLiteID();
        if ((!port.getPaoType().isDialupPort()) && 
                (deviceType.isCcu() || deviceType.isRtu() || deviceType.isIon())) {

            String[] devices = DeviceIDLCRemote.isAddressUnique(address, null, portID);

            if (devices.length > 0) {

                String message = "The address '" + address + "' is already used by the following devices,\n" + "are you sure you want to use it again?\n";

                int res = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this, message, "Address Already Used", devices);
                if (res == JOptionPane.NO_OPTION) {
                    throw new CancelInsertException("Device was not inserted");
                }
            }
        }

    }

    @Override
    public Object getValue(Object value) {

        Object val = null;
        if (value instanceof SmartMultiDBPersistent) {
            val = ((SmartMultiDBPersistent) value).getOwnerDBPersistent();
        } else {
            val = value;
        }
        Integer portID = null;

        if (null == getPortComboBox().getSelectedItem()) {
            JOptionPane.showMessageDialog(this,
                                          "Please create a com channel first.",
                                          "No Com Channels to Assign!",
                                          JOptionPane.WARNING_MESSAGE);
            throw new CancelInsertException("Device was not inserted");
        } else {
            portID = new Integer(((LiteYukonPAObject) getPortComboBox().getSelectedItem()).getYukonID());
        }

        PaoType paoType = ((DeviceBase) val).getPaoType();

        if (val instanceof TapTerminalBase) {
            ((TapTerminalBase) val).getDeviceDirectCommSettings().setPortID(portID);
        } else if (val instanceof TNPPTerminal) {
            ((TNPPTerminal) val).getDeviceDirectCommSettings().setPortID(portID);
        } else if (val instanceof RemoteBase) {
            ((RemoteBase) val).getDeviceDirectCommSettings().setPortID(portID);

            // We need to set the Devices baud rate to be the same as the selected routes baud rate
            // This requires a database hit here, could lead to problems in the future!
            try {
                DirectPort port = (DirectPort) LiteFactory.createDBPersistent((LiteBase) getPortComboBox().getSelectedItem());
                Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, port);

                port = (DirectPort) t.execute();

                ((RemoteBase) val).getDeviceDialupSettings().setBaudRate(port.getPortSettings().getBaudRate());
            } catch (Exception e) {
                // no big deal if we fail, the baud rates for the device and port will not be equal
                CTILogger.error(e.getMessage(), e);
            }

            if (val instanceof IDLCBase) {
                ((IDLCBase) val).getDeviceIDLCRemote().setPostCommWait(new Integer(0));
            }
        } else {
            throw new Error("What kind of device is this?");
        }

        // transmitter is a special case
        if (paoType.isTransmitter()) {

            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            ((DeviceBase) val).setDeviceID(paoDao.getNextPaoId());

            // checks device type and accordingly sets route type

            PaoType routeType;

            if (paoType.isCcu() || paoType.isRepeater())
                routeType = PaoType.ROUTE_CCU;
            else if (paoType.isLcu())
                routeType = PaoType.ROUTE_LCU;
            else if (paoType.isLcu())
                routeType = PaoType.ROUTE_TCU;
            else if (paoType == PaoType.TAPTERMINAL)
                routeType = PaoType.ROUTE_TAP_PAGING;
            else if (paoType == PaoType.TNPP_TERMINAL)
                routeType = PaoType.ROUTE_TNPP_TERMINAL;
            else if (paoType == PaoType.WCTP_TERMINAL)
                routeType = PaoType.ROUTE_WCTP_TERMINAL;
            else if (paoType == PaoType.SNPP_TERMINAL)
                routeType = PaoType.ROUTE_SNPP_TERMINAL;
            else if (paoType == PaoType.SERIES_5_LMI) {
                Integer devID = ((DeviceBase) val).getDevice().getDeviceID();
                ((Series5Base) val).setVerification(new DeviceVerification(devID, devID, "N", "N"));
                routeType = PaoType.ROUTE_SERIES_5_LMI;
            } else if (paoType == PaoType.RTC) {
                routeType = PaoType.ROUTE_RTC;
            } else if (paoType == PaoType.RDS_TERMINAL) {
                routeType = PaoType.ROUTE_RDS_TERMINAL;
            } else {
                return val;
            }

            // A route is automatically added to each transmitter create new route to be added
            RouteBase route = RouteFactory.createRoute(routeType);
            Integer routeID = paoDao.getNextPaoId();

            // make sure the name will fit in the DB!!
            route.setRouteName(StringUtils.left(((DeviceBase) val).getPAOName(), TextFieldDocument.MAX_ROUTE_NAME_LENGTH));

            // set default values for route tables
            route.setDeviceID(((DeviceBase) val).getDevice().getDeviceID());
            route.setDefaultRoute(CtiUtilities.getTrueCharacter().toString());

            if (routeType == PaoType.ROUTE_CCU) {
                ((CCURoute) route).setCarrierRoute(new CarrierRoute(routeID));
            }

            SmartMultiDBPersistent newVal = createSmartDBPersistent((DeviceBase) val);
            newVal.addDBPersistent(route);

            // newVal is a vector that contains: Transmitter device, a route & a status point and returned if device is a transmitter

            checkAddress();

            return newVal;
        } else if (paoType.isIed()) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            ((DeviceBase) val).setDeviceID(paoDao.getNextPaoId());

            SmartMultiDBPersistent smartDB = createSmartDBPersistent((DeviceBase) val);
            return smartDB;
        } else {
            return val;
        }
    }

    /**
     * Returns a SmartMultiDBPersistent for the deviceBase. Includes all points
     * and the deviceBase as the OwnerDBPersistent.
     */
    private SmartMultiDBPersistent createSmartDBPersistent(DeviceBase deviceBase) {
        if (deviceBase == null) {
            return null;
        }

        SmartMultiDBPersistent smartDB = new SmartMultiDBPersistent();
        smartDB.addOwnerDBPersistent(deviceBase);

        PaoDefinitionService paoDefinitionService = (PaoDefinitionService) YukonSpringHook.getBean("paoDefinitionService");
        DeviceDao deviceDao = (DeviceDao) YukonSpringHook.getBean("deviceDao");
        SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice(deviceBase);
        List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);

        for (PointBase point : defaultPoints) {
            smartDB.addDBPersistent(point);
        }

        return smartDB;
    }

    /**
     * Called whenever the part throws an exception.
     * @param exception Throwable
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
    private void initConnections() throws Exception {
        getPortComboBox().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("DeviceVirtualPortPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 200);

            java.awt.GridBagConstraints constraintsPortLabel = new java.awt.GridBagConstraints();
            constraintsPortLabel.gridx = 0;
            constraintsPortLabel.gridy = 0;
            constraintsPortLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsPortLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPortLabel.insets = new java.awt.Insets(5, 0, 5, 0);
            add(getPortLabel(), constraintsPortLabel);

            java.awt.GridBagConstraints constraintsPortComboBox = new java.awt.GridBagConstraints();
            constraintsPortComboBox.gridx = 0;
            constraintsPortComboBox.gridy = 1;
            constraintsPortComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsPortComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsPortComboBox.insets = new java.awt.Insets(5, 0, 5, 0);
            add(getPortComboBox(), constraintsPortComboBox);
            initConnections();
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public boolean isDialupPort() {
        if (getPortComboBox().getSelectedItem() == null) {
            return false;
        } else {
            return ((LiteYukonPAObject) getPortComboBox().getSelectedItem()).getPaoType().isDialupPort();
        }
    }

    @Override
    public void mouseClicked(MouseEvent newEvent) {
    }

    @Override
    public void mouseEntered(MouseEvent newEvent) {
    }

    @Override
    public void mouseExited(MouseEvent newEvent) {
    }

    @Override
    public void mousePressed(MouseEvent newEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent newEvent) {
        fireInputUpdate();
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
                getPortComboBox().requestFocus();
            }
        });
    }

    @Override
    public void valueChanged(ListSelectionEvent newEvent) {

        if (newEvent.getValueIsAdjusting()) {
            return;
        }
        fireInputUpdate();
    }

    public void setAddress(int addressvar) {
        address = addressvar;
    }

    public void setDeviceType(PaoType deviceTypevar) {
        deviceType = deviceTypevar;
    }

    @Override
    public boolean isInputValid() {
        return true;
    }
}

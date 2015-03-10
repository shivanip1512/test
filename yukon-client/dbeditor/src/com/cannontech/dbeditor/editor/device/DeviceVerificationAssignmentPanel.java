package com.cannontech.dbeditor.editor.device;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.table.TableColumn;

import com.cannontech.common.gui.util.CheckBoxTableRenderer;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.RTM;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.DeviceVerification;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class DeviceVerificationAssignmentPanel extends DataInputPanel {
    
    private JCheckBox DeviceVerificationCheckBox;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private JScrollPane ivjAssignedDevicesScrollPane = null;
    private JTable ivjVerifyDevicesTable = null;
    private DeviceVerificationAssignmentTableModel tableModel = null;

    private JList<LiteYukonPAObject> ivjAvailableList = null;
    private JButton ivjJButtonAdd = null;
    private JButton ivjJButtonRemove = null;
    private JScrollPane ivjJScrollPaneAvailable = null;

    class IvjEventHandler implements ActionListener, MouseListener, CaretListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getSource() == DeviceVerificationAssignmentPanel.this.getJButtonAdd()) {
                    DeviceVerificationAssignmentPanel.this.jButtonAdd_ActionPerformed(e);
                    fireInputUpdate();
                }
                
                if (e.getSource() == DeviceVerificationAssignmentPanel.this.getJButtonRemove()) {
                    DeviceVerificationAssignmentPanel.this.jButtonRemove_ActionPerformed(e);
                    fireInputUpdate();
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        };

        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
        };

        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
        };

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
        };

        @Override
        public void mousePressed(java.awt.event.MouseEvent e) {
            if (e.getSource() == DeviceVerificationAssignmentPanel.this.getDeviceVerificationTable())
                connEtoC1(e);
        };

        @Override
        public void mouseReleased(java.awt.event.MouseEvent e) {
        };

        @Override
        public void caretUpdate(CaretEvent e) {
        }
    };

    public DeviceVerificationAssignmentPanel() {
        super();
        setLayout(null);
        initialize();
        DeviceVerificationCheckBox = new JCheckBox();
        DeviceVerificationCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireInputUpdate();
            }
        });
        DeviceVerificationCheckBox.setToolTipText("Select to disable verification for this transmitter.");
        DeviceVerificationCheckBox.setFont(new Font("Arial", Font.BOLD, 12));
        DeviceVerificationCheckBox.setBounds(10, 5, 225, 25);
        add(DeviceVerificationCheckBox);
        DeviceVerificationCheckBox.setText("Disable Verification");
    }

    private void connEtoC1(java.awt.event.MouseEvent arg1) {
        try {
            this.transmittersTable_MousePressed(arg1);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JList<LiteYukonPAObject> getAvailableList() {
        if (ivjAvailableList == null) {
            try {
                ivjAvailableList = new JList<LiteYukonPAObject>();
                ivjAvailableList.setName("AvailableList");
                ivjAvailableList.setBounds(0, 0, 160, 120);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAvailableList;
    }

    private JButton getJButtonAdd() {
        if (ivjJButtonAdd == null) {
            try {
                ivjJButtonAdd = new JButton();
                ivjJButtonAdd.setBounds(15, 180, 149, 25);
                ivjJButtonAdd.setName("JButtonAdd");
                ivjJButtonAdd.setFont(new java.awt.Font("Arial", 1, 12));
                ivjJButtonAdd.setText("Assign to RTM");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonAdd;
    }

    private JButton getJButtonRemove() {
        if (ivjJButtonRemove == null) {
            try {
                ivjJButtonRemove = new JButton();
                ivjJButtonRemove.setBounds(215, 180, 143, 25);
                ivjJButtonRemove.setName("JButtonRemove");
                ivjJButtonRemove.setFont(new java.awt.Font("Arial", 1, 12));
                ivjJButtonRemove.setText("Remove from RTM");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJButtonRemove;
    }

    private JScrollPane getJScrollPaneAvailable() {
        if (ivjJScrollPaneAvailable == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
                ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 12));
                ivjLocalBorder1.setTitle("Available Devices");
                ivjJScrollPaneAvailable = new JScrollPane();
                ivjJScrollPaneAvailable.setBounds(10, 35, 400, 140);
                ivjJScrollPaneAvailable.setName("JScrollPaneAvailable");
                ivjJScrollPaneAvailable.setPreferredSize(new java.awt.Dimension(404, 130));
                ivjJScrollPaneAvailable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                ivjJScrollPaneAvailable.setBorder(ivjLocalBorder1);
                ivjJScrollPaneAvailable.setMinimumSize(new java.awt.Dimension(404, 130));
                getJScrollPaneAvailable().setViewportView(getAvailableList());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJScrollPaneAvailable;
    }

    private JScrollPane getAssignedDevicesScrollPane() {
        if (ivjAssignedDevicesScrollPane == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
                ivjLocalBorder.setTitle("Assigned Devices");
                ivjAssignedDevicesScrollPane = new JScrollPane();
                ivjAssignedDevicesScrollPane.setBounds(10, 210, 400, 166);
                ivjAssignedDevicesScrollPane.setName("AssignedDevicesScrollPane");
                ivjAssignedDevicesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                ivjAssignedDevicesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                ivjAssignedDevicesScrollPane.setBorder(ivjLocalBorder);
                ivjAssignedDevicesScrollPane.setPreferredSize(new java.awt.Dimension(404, 155));
                ivjAssignedDevicesScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAssignedDevicesScrollPane.setMinimumSize(new java.awt.Dimension(404, 155));
                getAssignedDevicesScrollPane().setViewportView(getDeviceVerificationTable());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAssignedDevicesScrollPane;
    }

    private JTable getDeviceVerificationTable() {
        if (ivjVerifyDevicesTable == null) {
            try {
                ivjVerifyDevicesTable = new JTable();
                ivjVerifyDevicesTable.setName("DeviceVerificationTable");
                getAssignedDevicesScrollPane().setColumnHeaderView(ivjVerifyDevicesTable.getTableHeader());

                ivjVerifyDevicesTable.setAutoCreateColumnsFromModel(true);
                ivjVerifyDevicesTable.setModel(getTableModel());
                ivjVerifyDevicesTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
                ivjVerifyDevicesTable.setPreferredSize(new java.awt.Dimension(385, 5000));
                ivjVerifyDevicesTable.setBounds(0, 0, 385, 5000);
                ivjVerifyDevicesTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
                ivjVerifyDevicesTable.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
                ivjVerifyDevicesTable.setGridColor(java.awt.Color.black);
                ivjVerifyDevicesTable.getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
                ivjVerifyDevicesTable.setRowHeight(20);

                // Do any column specific initialization here
                TableColumn nameColumn = getDeviceVerificationTable().getColumnModel().getColumn(DeviceVerificationAssignmentTableModel.DEVICELITEPAO_COLUMN);
                TableColumn resendOnFailColumn = getDeviceVerificationTable().getColumnModel().getColumn(DeviceVerificationAssignmentTableModel.RESENDONFAIL_COLUMN);
                nameColumn.setPreferredWidth(100);
                resendOnFailColumn.setPreferredWidth(60);

                // Create and add the checkbox renderer
                CheckBoxTableRenderer bxRender = new CheckBoxTableRenderer();
                bxRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
                resendOnFailColumn.setCellRenderer(bxRender);

                // Create and add the checkbox cell editor
                javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();
                chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
                chkBox.setBackground(getDeviceVerificationTable().getBackground());
                resendOnFailColumn.setCellEditor(new javax.swing.DefaultCellEditor(chkBox));
            }
            catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjVerifyDevicesTable;
    }

    private DeviceVerificationAssignmentTableModel getTableModel() {
        if (tableModel == null) {
            tableModel = new DeviceVerificationAssignmentTableModel();
        }
        return tableModel;
    }

    @Override
    public Object getValue(Object o) {
        // make sure cells get saved even though they might be currently being edited
        if (getDeviceVerificationTable().isEditing()) {
            getDeviceVerificationTable().getCellEditor().stopCellEditing();
        }

        boolean disabled = getDeviceVerificationCheckBox().isSelected();

        RTM rtm = (RTM) o;

        if (rtm == null) {
            rtm = new RTM();
        }

        Vector<DeviceVerification> assignedDevices = new Vector<DeviceVerification>();

        for (int j = 0; j < getDeviceVerificationTable().getRowCount(); j++) {
            DeviceVerification trans = new DeviceVerification();

            // program name needs to be converted to id for storage
            LiteYukonPAObject thePAO = getTableModel().getDeviceLitePAOAt(j);

            // transmitterID
            trans.setTransmitterID(new Integer(thePAO.getLiteID()));

            // receiverID (RTM deviceID)
            trans.setReceiverID(rtm.getPAObjectID());

            // resend on fail boolean
            boolean resendOrNot = getTableModel().getResendOnFailAt(j);
            if (resendOrNot) {
                trans.setResendOnFail("Y");
            } else {
                trans.setResendOnFail("N");
            }

            if (disabled) {
                trans.setDisable("Y");
            } else {
                trans.setDisable("N");
            }

            assignedDevices.addElement(trans);
        }

        rtm.setDeviceVerificationVector(assignedDevices);
        return rtm;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(java.lang.Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        // System.out.println("--------- UNCAUGHT EXCEPTION ---------");
        // exception.printStackTrace(System.out);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getDeviceVerificationTable().addMouseListener(ivjEventHandler);
        getJButtonAdd().addActionListener(ivjEventHandler);
        getJButtonRemove().addActionListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("RTMVerificationPanel");
            setPreferredSize(new java.awt.Dimension(420, 386));
            setSize(420, 360);
            setMinimumSize(new java.awt.Dimension(420, 360));
            setMaximumSize(new java.awt.Dimension(420, 360));

            add(getAssignedDevicesScrollPane());
            add(getJButtonAdd());
            add(getJButtonRemove());
            add(getJScrollPaneAvailable());

            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void jButtonAdd_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

        List<LiteYukonPAObject> selectedForVerification = getAvailableList().getSelectedValuesList();

        Vector<LiteYukonPAObject> allAvailable = new Vector<LiteYukonPAObject>(getAvailableList().getModel().getSize());
        for (int i = 0; i < getAvailableList().getModel().getSize(); i++) {
            allAvailable.add(getAvailableList().getModel().getElementAt(i));
        }

        for (LiteYukonPAObject thePAO : selectedForVerification) {
            Integer devID = new Integer(thePAO.getLiteID());

            // add the new row
            getTableModel().addRowValue(thePAO, false);

            // make sure that the available programs list is not showing these assigned programs
            for (int y = 0; y < allAvailable.size(); y++) {
                if (devID.intValue() == (allAvailable.elementAt(y).getLiteID())) {
                    allAvailable.removeElementAt(y);
                }
            }
        }

        // update the available programs list
        getAvailableList().setListData(allAvailable);

        repaint();
    }

    public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        int[] selectedRows = getDeviceVerificationTable().getSelectedRows();
        Vector<LiteYukonPAObject> allAvailable = new Vector<LiteYukonPAObject>(getAvailableList().getModel().getSize() + selectedRows.length);

        for (int i = 0; i < getAvailableList().getModel().getSize(); i++) {
            allAvailable.add(getAvailableList().getModel().getElementAt(i));
        }

        for (int u = selectedRows.length - 1; u >= 0; u--) {
            LiteYukonPAObject thePAO = getTableModel().getDeviceLitePAOAt(selectedRows[u]);

            allAvailable.addElement(thePAO);
            getTableModel().removeRowValue(selectedRows[u]);
        }

        getAvailableList().setListData(allAvailable);
        repaint();
    }

    public Vector<LiteYukonPAObject> populateAvailableList() {
        Vector<LiteYukonPAObject> availableDevices = new Vector<LiteYukonPAObject>();

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();
            
            try {
                for (LiteYukonPAObject dev : devices) {
                    if (dev.getPaoType() == PaoType.RTC) {
                        availableDevices.addElement(dev);
                    }
                }
            }

            catch (Exception e2) {
                e2.printStackTrace(); // something is up
            }
        }
        return availableDevices;
    }

    public void transmittersTable_MousePressed(java.awt.event.MouseEvent mouseEvent) {
        fireInputUpdate();
    }

    @Override
    public void setValue(Object o) {

        RTM rtm = (RTM) o;

        if (rtm == null) {
            rtm = new RTM();
        }

        Vector<LiteYukonPAObject> allAvailable = populateAvailableList();

        Vector<DeviceVerification> deviceVerifications = rtm.getDeviceVerificationVector();
        boolean disabled = false;

        for (DeviceVerification veryDevice : deviceVerifications) {
            Integer devID = veryDevice.getTransmitterID();
            LiteYukonPAObject thePAO = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(devID.intValue());

            disabled = veryDevice.getDisable().equalsIgnoreCase("Y");

            boolean resend;
            if (veryDevice.getResendOnFail().equalsIgnoreCase("Y")) {
                resend = true;
            } else {
                resend = false;
            }

            // add the new row
            getTableModel().addRowValue(thePAO, resend);

            // make sure that the available programs list is not showing these assigned programs
            for (int y = 0; y < allAvailable.size(); y++) {
                if (devID.intValue() == (allAvailable.elementAt(y).getLiteID()))
                    allAvailable.removeElementAt(y);
            }
        }
        // update the available programs list
        getAvailableList().setListData(allAvailable);
        getDeviceVerificationCheckBox().setSelected(disabled);

    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    public JCheckBox getDeviceVerificationCheckBox() {
        return DeviceVerificationCheckBox;
    }

    public void setDeviceVerificationCheckBox(JCheckBox box) {
        DeviceVerificationCheckBox = box;
    }
}
package com.cannontech.dbeditor.wizard.point;

import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class PointSettingsPanel extends DataInputPanel implements CaretListener {
    private JComboBox<LiteYukonPAObject> ivjDeviceComboBox = null;
    private JLabel ivjDeviceLabel = null;
    private JLabel ivjNameLabel = null;
    private JTextField ivjNameTextField = null;

    public PointSettingsPanel() {
        super();
        initialize();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        try {
            if (e.getSource() == getNameTextField()) {
                this.nameTextField_CaretUpdate(e);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JComboBox<LiteYukonPAObject> getDeviceComboBox() {
        if (ivjDeviceComboBox == null) {
            try {
                ivjDeviceComboBox = new JComboBox<LiteYukonPAObject>();
                ivjDeviceComboBox.setName("DeviceComboBox");
                ivjDeviceComboBox.setFont(new java.awt.Font("dialog", 0, 14));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeviceComboBox;
    }

    private JLabel getDeviceLabel() {
        if (ivjDeviceLabel == null) {
            try {
                ivjDeviceLabel = new JLabel();
                ivjDeviceLabel.setName("DeviceLabel");
                ivjDeviceLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjDeviceLabel.setText("Device:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDeviceLabel;
    }

    private JLabel getNameLabel() {
        if (ivjNameLabel == null) {
            try {
                ivjNameLabel = new JLabel();
                ivjNameLabel.setName("NameLabel");
                ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjNameLabel.setText("Name:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameLabel;
    }

    private JTextField getNameTextField() {
        if (ivjNameTextField == null) {
            try {
                ivjNameTextField = new JTextField();
                ivjNameTextField.setName("NameTextField");
                ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjNameTextField.setColumns(12);

                ivjNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_POINT_NAME_LENGTH));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameTextField;
    }

    public Integer getPointDeviceID() {

        try {
            return ((LiteYukonPAObject) getDeviceComboBox().getSelectedItem()).getYukonID();
        } catch (NullPointerException npe) {
            return new Integer(1);
        }
    }

    @Override
    public Object getValue(Object val) {
        // Assuming only a PointBase
        PointBase point = (PointBase) val;

        String name = getNameTextField().getText();
        LiteYukonPAObject liteDevice = (LiteYukonPAObject) getDeviceComboBox().getSelectedItem();

        int nextId = YukonSpringHook.getBean(PointDao.class).getNextPointId();
        point.setPointID(nextId);
        point.getPoint().setPointName(name);
        point.getPoint().setPaoID(new Integer(liteDevice.getYukonID()));

        return val;
    }

    /**
     * Called whenever the part throws an exception.
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
        getNameTextField().addCaretListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("PointSettingsPanel");
            setPreferredSize(new java.awt.Dimension(350, 200));
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 200);

            java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
            constraintsNameLabel.gridx = 0;
            constraintsNameLabel.gridy = 0;
            constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            add(getNameLabel(), constraintsNameLabel);

            java.awt.GridBagConstraints constraintsDeviceLabel = new java.awt.GridBagConstraints();
            constraintsDeviceLabel.gridx = 0;
            constraintsDeviceLabel.gridy = 1;
            constraintsDeviceLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsDeviceLabel.anchor = java.awt.GridBagConstraints.WEST;
            add(getDeviceLabel(), constraintsDeviceLabel);

            java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
            constraintsNameTextField.gridx = 1;
            constraintsNameTextField.gridy = 0;
            constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
            constraintsNameTextField.insets = new java.awt.Insets(5, 10, 5, 25);
            add(getNameTextField(), constraintsNameTextField);

            java.awt.GridBagConstraints constraintsDeviceComboBox = new java.awt.GridBagConstraints();
            constraintsDeviceComboBox.gridx = 1;
            constraintsDeviceComboBox.gridy = 1;
            constraintsDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsDeviceComboBox.insets = new java.awt.Insets(5, 10, 5, 25);
            add(getDeviceComboBox(), constraintsDeviceComboBox);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        if (getNameTextField().getText().length() <= 0) {
            setErrorString("The Name text field must be filled in");
            return false;
        }
        return true;
    }

    public void nameTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
        fireInputUpdate();
    }

    public void setSelectedDeviceIndex(int deviceID) {
        // sets selected index of DeviceComboBox to selected device
        for (int i = 0; i < getDeviceComboBox().getItemCount(); i++) {
            if (getDeviceComboBox().getItemAt(i).getYukonID() == deviceID) {
                getDeviceComboBox().setSelectedIndex(i);
                break;
            }
        }
    }

    @Override
    public void setValue(Object val) {
    }

    public void setValueCore(Object val, Integer initialPAOId) {

        // Load the device list
        if (getDeviceComboBox().getModel().getSize() > 0)
            getDeviceComboBox().removeAllItems();
        if (initialPAOId == null || initialPAOId.intValue() != 0) {
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();
            synchronized (cache) {
                List<LiteYukonPAObject> devices = cache.getAllDevices();
                for (LiteYukonPAObject liteYukonPAObject : devices) {
                    if (liteYukonPAObject.getPaoType().getPaoClass().isCore()) {
                        getDeviceComboBox().addItem(liteYukonPAObject);

                        if (initialPAOId != null && initialPAOId.intValue() == liteYukonPAObject.getYukonID()) {
                            getDeviceComboBox().setSelectedIndex(getDeviceComboBox().getItemCount() - 1);
                        }
                    }
                }
            }
        } else {
            getDeviceComboBox().addItem(YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(0));
            getDeviceComboBox().setEnabled(false);
        }

    }

    public void setValueLM(Object val, Integer initialPAOId) {
        // Load the device list
        if (getDeviceComboBox().getModel().getSize() > 0)
            getDeviceComboBox().removeAllItems();

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllLoadManagement();
            java.util.Collections.sort(devices, LiteComparators.liteStringComparator);

            for (LiteYukonPAObject device : devices) {
                if (device.getPaoType().isLoadManagement()) {
                    getDeviceComboBox().addItem(device);

                    if (initialPAOId != null && initialPAOId.intValue() == device.getYukonID()) {
                        getDeviceComboBox().setSelectedIndex(getDeviceComboBox().getItemCount() - 1);
                    }
                }
            }
        }
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getNameTextField().requestFocus();
            }
        });
    }
}
package com.cannontech.dbeditor.wizard.point;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
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
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.klg.jclass.field.DataProperties;
import com.klg.jclass.field.JCInvalidInfo;
import com.klg.jclass.field.JCSpinField;
import com.klg.jclass.field.validate.JCIntegerValidator;
import com.klg.jclass.util.value.JCValueEvent;
import com.klg.jclass.util.value.JCValueListener;
import com.klg.jclass.util.value.MutableValueModel;

public class PointIDSettingsPanel extends DataInputPanel implements JCValueListener, ItemListener, CaretListener {
    private JComboBox<LiteYukonPAObject> ivjDeviceComboBox = null;
    private JLabel ivjDeviceLabel = null;
    private JLabel ivjNameLabel = null;
    private JTextField ivjNameTextField = null;
    private JLabel ivjPointIDLabel = null;
    private JCSpinField ivjPointIDSpinner = null;
    private JLabel ivjUsedPointIDLabel = null;

    public PointIDSettingsPanel() {
        super();
        initialize();
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        if (e.getSource() == getNameTextField()) {
            try {
                this.nameTextField_CaretUpdate(e);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    public void deviceComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
        getUsedPointIDLabel().setText("");
        int nextPointId = YukonSpringHook.getBean(PointDao.class).getNextPointId();
        getPointIDSpinner().setValue(new Integer(nextPointId));

        revalidate();
        repaint();
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
            return new Integer(((LiteYukonPAObject) getDeviceComboBox().getSelectedItem()).getYukonID());
        } catch (NullPointerException npe) {
            return new Integer(1);
        }
    }

    private JLabel getPointIDLabel() {
        if (ivjPointIDLabel == null) {
            try {
                ivjPointIDLabel = new JLabel();
                ivjPointIDLabel.setName("PointIDLabel");
                ivjPointIDLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPointIDLabel.setText("Point ID:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPointIDLabel;
    }

    private com.klg.jclass.field.JCSpinField getPointIDSpinner() {
        if (ivjPointIDSpinner == null) {
            try {
                ivjPointIDSpinner = new com.klg.jclass.field.JCSpinField();
                ivjPointIDSpinner.setName("PointIDSpinner");
                ivjPointIDSpinner.setPreferredSize(new java.awt.Dimension(60, 22));
                ivjPointIDSpinner.setFont(new java.awt.Font("dialog", 0, 14));
                ivjPointIDSpinner.setBackground(java.awt.Color.white);
                ivjPointIDSpinner.setMinimumSize(new java.awt.Dimension(60, 22));
                ivjPointIDSpinner.setDataProperties(new DataProperties(new JCIntegerValidator(null, new Integer(1), new Integer(1000000), null, true,
                                                                                              null, new Integer(1), "#,##0.###;-#,##0.###",
                                                                                              false, false, false, null, new Integer(0)),
                                                                       new MutableValueModel(Integer.class, new Integer(1)),
                                                                       new JCInvalidInfo(true, 2, new Color(0, 0, 0, 255), new Color(255, 255, 255, 255))));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjPointIDSpinner;
    }

    private JLabel getUsedPointIDLabel() {
        if (ivjUsedPointIDLabel == null) {
            try {
                ivjUsedPointIDLabel = new JLabel();
                ivjUsedPointIDLabel.setName("UsedPointIDLabel");
                ivjUsedPointIDLabel.setText("ID Already Assigned");
                ivjUsedPointIDLabel.setMaximumSize(new java.awt.Dimension(140, 20));
                ivjUsedPointIDLabel.setPreferredSize(new java.awt.Dimension(140, 20));
                ivjUsedPointIDLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
                ivjUsedPointIDLabel.setMinimumSize(new java.awt.Dimension(140, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjUsedPointIDLabel;
    }

    @Override
    public Object getValue(Object val) {
        // Assuming only a PointBase
        PointBase point = (PointBase) val;

        Integer pointID = null;
        Object pointNumSpinVal = getPointIDSpinner().getValue();
        if (pointNumSpinVal instanceof Long) {
            pointID = new Integer(((Long) pointNumSpinVal).intValue());
        } else if (pointNumSpinVal instanceof Integer) {
            pointID = new Integer(((Integer) pointNumSpinVal).intValue());
        }

        String name = getNameTextField().getText();
        LiteYukonPAObject liteDevice = (LiteYukonPAObject) getDeviceComboBox().getSelectedItem();

        point.setPointID(pointID);
        point.getPoint().setPointName(name);
        point.getPoint().setPaoID(new Integer(liteDevice.getYukonID()));

        return val;
    }

    private void handleException(Throwable exception) {
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getNameTextField().addCaretListener(this);
        getDeviceComboBox().addItemListener(this);
        getPointIDSpinner().addValueListener(this);
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

            GridBagConstraints constraintsPointIDLabel = new GridBagConstraints();
            constraintsPointIDLabel.gridx = 0;
            constraintsPointIDLabel.gridy = 2;
            constraintsPointIDLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsPointIDLabel.anchor = GridBagConstraints.WEST;
            constraintsPointIDLabel.insets = new java.awt.Insets(5, 0, 5, 0);
            add(getPointIDLabel(), constraintsPointIDLabel);

            GridBagConstraints constraintsNameLabel = new GridBagConstraints();
            constraintsNameLabel.gridx = 0;
            constraintsNameLabel.gridy = 0;
            constraintsNameLabel.fill = GridBagConstraints.HORIZONTAL;
            add(getNameLabel(), constraintsNameLabel);

            GridBagConstraints constraintsDeviceLabel = new GridBagConstraints();
            constraintsDeviceLabel.gridx = 0;
            constraintsDeviceLabel.gridy = 1;
            constraintsDeviceLabel.fill = GridBagConstraints.HORIZONTAL;
            constraintsDeviceLabel.anchor = GridBagConstraints.WEST;
            add(getDeviceLabel(), constraintsDeviceLabel);

            GridBagConstraints constraintsPointIDSpinner = new GridBagConstraints();
            constraintsPointIDSpinner.gridx = 1;
            constraintsPointIDSpinner.gridy = 2;
            constraintsPointIDSpinner.anchor = GridBagConstraints.WEST;
            constraintsPointIDSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
            add(getPointIDSpinner(), constraintsPointIDSpinner);

            GridBagConstraints constraintsNameTextField = new GridBagConstraints();
            constraintsNameTextField.gridx = 1;
            constraintsNameTextField.gridy = 0;
            constraintsNameTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsNameTextField.anchor = GridBagConstraints.WEST;
            constraintsNameTextField.insets = new java.awt.Insets(5, 10, 5, 25);
            add(getNameTextField(), constraintsNameTextField);

            GridBagConstraints constraintsDeviceComboBox = new GridBagConstraints();
            constraintsDeviceComboBox.gridx = 1;
            constraintsDeviceComboBox.gridy = 1;
            constraintsDeviceComboBox.fill = GridBagConstraints.HORIZONTAL;
            constraintsDeviceComboBox.anchor = GridBagConstraints.WEST;
            constraintsDeviceComboBox.insets = new java.awt.Insets(5, 10, 5, 25);
            add(getDeviceComboBox(), constraintsDeviceComboBox);

            GridBagConstraints constraintsUsedPointIDLabel = new GridBagConstraints();
            constraintsUsedPointIDLabel.gridx = 1;
            constraintsUsedPointIDLabel.gridy = 2;
            constraintsUsedPointIDLabel.anchor = GridBagConstraints.WEST;
            constraintsUsedPointIDLabel.insets = new java.awt.Insets(5, 80, 5, 0);
            add(getUsedPointIDLabel(), constraintsUsedPointIDLabel);
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

        if (getUsedPointIDLabel().getText().equalsIgnoreCase("ID Already Assigned")) {
            setErrorString("The PointID selected is already used");
            return false;
        }

        return true;
    }

    /**
     * Method to handle events for the ItemListener interface.
     */
    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == getDeviceComboBox()) {
            try {
                this.deviceComboBox_ItemStateChanged(e);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    public void nameTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
        fireInputUpdate();
    }

    public void PointIDSpinner_ValueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
        Object pointIdVal = getPointIDSpinner().getValue();
        int pointId = -1;
        if (pointIdVal instanceof Long) {
            pointId = ((Long) pointIdVal).intValue();
        } else if (pointIdVal instanceof Integer) {
            pointId = (Integer) pointIdVal;
        }

        LitePoint lp = YukonSpringHook.getBean(PointDao.class).getLitePoint(pointId);
        if (lp != null) {
            getUsedPointIDLabel().setText("ID Already Assigned");
        }

        revalidate();
        repaint();
        fireInputUpdate();
        return;
    }

    @Override
    public void setValue(Object val) {
    }

    public void setValueCore(Object val, Integer initialPAOId) {
        // Load the device list
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();
            Collections.sort(devices, LiteComparators.liteStringComparator);

            if (getDeviceComboBox().getModel().getSize() > 0)
                getDeviceComboBox().removeAllItems();

            for (LiteYukonPAObject liteYukonPAObject : devices) {
                if (DeviceClasses.isCoreDeviceClass(liteYukonPAObject.getPaoType().getPaoClass().getPaoClassId())) {
                    getDeviceComboBox().addItem(liteYukonPAObject);

                    if (initialPAOId != null && initialPAOId.intValue() == liteYukonPAObject.getYukonID()) {
                        getDeviceComboBox().setSelectedIndex(getDeviceComboBox().getItemCount() - 1);
                    }
                }
            }
            getUsedPointIDLabel().setText("");
        }

        int nextId = YukonSpringHook.getBean(PointDao.class).getNextPointId();
        getPointIDSpinner().setValue(nextId);
    }

    public void setValueLM(Object val, Integer initialPAOId) {
        // Load the device list
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllLoadManagement();
            java.util.Collections.sort(devices, LiteComparators.liteStringComparator);

            if (getDeviceComboBox().getModel().getSize() > 0)
                getDeviceComboBox().removeAllItems();

            for (LiteYukonPAObject liteYukonPAObject : devices) {
                if (liteYukonPAObject.getPaoType().getPaoClass() == PaoClass.GROUP) {
                    getDeviceComboBox().addItem(liteYukonPAObject);

                    if (initialPAOId != null && initialPAOId.intValue() == liteYukonPAObject.getYukonID()) {
                        getDeviceComboBox().setSelectedIndex(getDeviceComboBox().getItemCount() - 1);
                    }
                }
            }
        }

        int nextId = YukonSpringHook.getBean(PointDao.class).getNextPointId();
        getPointIDSpinner().setValue(nextId);
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getNameTextField().requestFocus();
            }
        });
    }

    /**
     * Method to handle events for the JCValueListener interface.
     * @param arg1 com.klg.jclass.util.value.JCValueEvent
     */
    @Override
    public void valueChanged(JCValueEvent arg1) {
        if (arg1.getSource() == getPointIDSpinner()) {
            try {
                this.PointIDSpinner_ValueChanged(arg1);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
    }

    /**
     * Method to handle events for the JCValueListener interface.
     * @param arg1 com.klg.jclass.util.value.JCValueEvent
     */
    @Override
    public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
    }
}
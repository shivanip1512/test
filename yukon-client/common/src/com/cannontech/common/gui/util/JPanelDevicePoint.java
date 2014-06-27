package com.cannontech.common.gui.util;

import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class JPanelDevicePoint extends JPanel implements ActionListener {
    public static String PROPERTY_PAO_UPDATE = "UpdatePAOProperty";
    public static String PROPERTY_POINT_UPDATE = "UpdatePointProperty";

    private List<PointType> pointTypeFilter = null;
    private List<PaoType> paoTypeFilters = Lists.newArrayList();
    private JComboBox<LiteYukonPAObject> ivjJComboBoxDevice = null;
    private JLabel ivjJLabelDevice = null;
    private JComboBox<LitePoint> ivjJComboBoxPoint = null;
    private JLabel ivjJLabelPoint = null;
    // a mutable lite point used for comparisons
    private static final LitePoint DUMMY_LITE_POINT = new LitePoint(Integer.MIN_VALUE, "(No Points Found)", 0, 0, 0, 0);

    public JPanelDevicePoint() {
        super();
        initialize();
    }

    public JPanelDevicePoint(boolean showPoints) {
        this();
        setShowPoints(showPoints);
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getJComboBoxDevice()) {
                this.jComboBoxDevice_ActionPerformed(e);
            }
            if (e.getSource() == getJComboBoxPoint()) {
                this.jComboBoxPoint_ActionPerformed(e);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void addComboBoxPropertyChangeListener(PropertyChangeListener list) {
        addPropertyChangeListener(PROPERTY_PAO_UPDATE, list);
        addPropertyChangeListener(PROPERTY_POINT_UPDATE, list);
    }

    private JComboBox<LiteYukonPAObject> getJComboBoxDevice() {
        if (ivjJComboBoxDevice == null) {
            try {
                ivjJComboBoxDevice = new JComboBox<LiteYukonPAObject>();
                ivjJComboBoxDevice.setName("JComboBoxDevice");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxDevice;
    }

    private JComboBox<LitePoint> getJComboBoxPoint() {
        if (ivjJComboBoxPoint == null) {
            try {
                ivjJComboBoxPoint = new JComboBox<LitePoint>();
                ivjJComboBoxPoint.setName("JComboBoxPoint");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxPoint;
    }

    private JLabel getJLabelDevice() {
        if (ivjJLabelDevice == null) {
            try {
                ivjJLabelDevice = new JLabel();
                ivjJLabelDevice.setName("JLabelDevice");
                ivjJLabelDevice.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelDevice.setText("Device:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelDevice;
    }

    private JLabel getJLabelPoint() {
        if (ivjJLabelPoint == null) {
            try {
                ivjJLabelPoint = new JLabel();
                ivjJLabelPoint.setName("JLabelPoint");
                ivjJLabelPoint.setFont(new java.awt.Font("dialog", 0, 14));
                ivjJLabelPoint.setText("Point:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelPoint;
    }

    public LiteYukonPAObject getSelectedDevice() {
        return (LiteYukonPAObject) getJComboBoxDevice().getSelectedItem();
    }

    /**
     * @return null if no point is available or if the point combo is not visible
     */
    public LitePoint getSelectedPoint() {
        if (getJComboBoxPoint().isEnabled() && getJComboBoxPoint().isVisible())
            return (LitePoint) getJComboBoxPoint().getSelectedItem();
        else
            return null;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(java.lang.Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getJComboBoxDevice().addActionListener(this);
        getJComboBoxPoint().addActionListener(this);
    }

    private void initDeviceComboBox() {
        getJComboBoxDevice().removeAllItems();

        // add all of our devices to the ComboBox
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> deviceList = cache.getAllDevices();

            if (deviceList.size() > 0) {
                for (LiteYukonPAObject pao : deviceList) {
                    if (isPAOValid(pao)){
                        getJComboBoxDevice().addItem(pao);
                    }
                }

            }
        }

        // fire the point combo box event to fill the point combo box
        if (getJComboBoxDevice().getModel().getSize() > 0)
            getJComboBoxDevice().setSelectedIndex(0);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("DevicePointJPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(231, 54);

            java.awt.GridBagConstraints constraintsJLabelDevice = new java.awt.GridBagConstraints();
            constraintsJLabelDevice.gridx = 1;
            constraintsJLabelDevice.gridy = 1;
            constraintsJLabelDevice.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelDevice.ipadx = 6;
            constraintsJLabelDevice.ipady = -2;
            constraintsJLabelDevice.insets = new java.awt.Insets(5, 2, 5, 0);
            add(getJLabelDevice(), constraintsJLabelDevice);

            java.awt.GridBagConstraints constraintsJComboBoxDevice = new java.awt.GridBagConstraints();
            constraintsJComboBoxDevice.gridx = 2;
            constraintsJComboBoxDevice.gridy = 1;
            constraintsJComboBoxDevice.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxDevice.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxDevice.weightx = 1.0;
            constraintsJComboBoxDevice.ipadx = 44;
            constraintsJComboBoxDevice.insets = new java.awt.Insets(3, 1, 1, 5);
            add(getJComboBoxDevice(), constraintsJComboBoxDevice);

            java.awt.GridBagConstraints constraintsJLabelPoint = new java.awt.GridBagConstraints();
            constraintsJLabelPoint.gridx = 1;
            constraintsJLabelPoint.gridy = 2;
            constraintsJLabelPoint.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelPoint.ipadx = 10;
            constraintsJLabelPoint.ipady = -2;
            constraintsJLabelPoint.insets = new java.awt.Insets(3, 2, 7, 7);
            add(getJLabelPoint(), constraintsJLabelPoint);

            java.awt.GridBagConstraints constraintsJComboBoxPoint = new java.awt.GridBagConstraints();
            constraintsJComboBoxPoint.gridx = 2;
            constraintsJComboBoxPoint.gridy = 2;
            constraintsJComboBoxPoint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxPoint.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxPoint.weightx = 1.0;
            constraintsJComboBoxPoint.ipadx = 44;
            constraintsJComboBoxPoint.insets = new java.awt.Insets(1, 1, 3, 5);
            add(getJComboBoxPoint(), constraintsJComboBoxPoint);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        initDeviceComboBox();
    }

    private boolean isPAOValid(LiteYukonPAObject pao) {
        // if not set, we want all the PAO's
        if (paoTypeFilters.isEmpty())
            return true;
        else {
            // Watch out now!!
            for (PaoType filterType : paoTypeFilters) {
                if (filterType == pao.getPaoType()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void jComboBoxDevice_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        if (getJComboBoxDevice().getSelectedItem() != null) {
            getJComboBoxPoint().removeAllItems();

            LiteYukonPAObject selectedDevice = (LiteYukonPAObject) getJComboBoxDevice().getSelectedItem();

            List<LitePoint> devicePoints = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(selectedDevice.getYukonID());
            for (LitePoint point : devicePoints) {
                getJComboBoxPoint().addItem(point);
            }

            // disable the point combo if there are no points found
            getJComboBoxPoint().setEnabled(getJComboBoxPoint().getModel().getSize() > 0);

            if (!getJComboBoxPoint().isEnabled())
                getJComboBoxPoint().addItem(DUMMY_LITE_POINT);
        }

        // may as well use and existing messaging scheme
        firePropertyChange(PROPERTY_PAO_UPDATE, 0, 1);

        return;
    }

    public void jComboBoxPoint_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        // may as well use and existing messaging scheme
        firePropertyChange(PROPERTY_POINT_UPDATE, 0, 1);
    }

    public void setDisplayedPAOs(int count) {
        getJComboBoxDevice().setMaximumRowCount(count);
    }

    public void setDisplayedPoints(int count) {
        getJComboBoxPoint().setMaximumRowCount(count);
    }

    /**
     * int[][][] paoTypeFilter = { { {4,6,9} }, // 4(Category), 6(PAOClass),
     * 9(Type) { {2,4,6} }, { {2,0,4} }, { {8,5,9} }, { {3,8,2} } //
     * 3(Category), 8(PAOClass), 2(Type) };
     */
    public void setPAOFilter(List<PaoType> paoTypeFilters) {
        // ensure our array has the correct field lengths
        this.paoTypeFilters = paoTypeFilters;
        initDeviceComboBox();
    }

    public void setPointTypeFilter(List<PointType> pointTypes) {
        pointTypeFilter = pointTypes;
        initDeviceComboBox();
    }

    public void setSelectedLitePAO(int paoID) {
        for (int i = 0; i < getJComboBoxDevice().getModel().getSize(); i++)
            if (getJComboBoxDevice().getItemAt(i).getYukonID() == paoID) {
                getJComboBoxDevice().setSelectedIndex(i);
                break;
            }

    }

    public void setSelectedLitePoint(int pointID) {
        for (int i = 0; i < getJComboBoxPoint().getModel().getSize(); i++)
            if (getJComboBoxPoint().getItemAt(i).getPointID() == pointID) {
                getJComboBoxPoint().setSelectedIndex(i);
                break;
            }

    }

    public void setShowPoints(boolean value) {
        getJLabelPoint().setVisible(value);
        getJComboBoxPoint().setVisible(value);
    }
}
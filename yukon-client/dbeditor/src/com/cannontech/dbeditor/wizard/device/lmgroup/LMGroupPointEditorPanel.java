package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dao.StateGroupDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.lm.LMGroupPoint;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteState;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusPoint;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class LMGroupPointEditorPanel extends DataInputPanel implements
        java.awt.event.ActionListener {
    // localHashTable is used to store all status points with control by their respected PAObject
    private final Hashtable<LiteYukonPAObject, List<LitePoint>> localHashTable = new Hashtable<LiteYukonPAObject, List<LitePoint>>();

    private JComboBox<LiteYukonPAObject> ivjJComboBoxControlDevice = null;
    private JComboBox<LitePoint> ivjJComboBoxControlPoint = null;
    private JComboBox<LiteState> ivjJComboBoxControlStartState = null;
    private JLabel ivjJLabelControlPoint = null;
    private JLabel ivjJLabelControlStartState = null;
    private JLabel ivjJLabelDeviceControl = null;

    public LMGroupPointEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        try {
            if (e.getSource() == getJComboBoxControlStartState() ||
                    e.getSource() == getJComboBoxControlDevice() ||
                    e.getSource() == getJComboBoxControlPoint()) {
                this.fireInputUpdate();
            }
            
            if (e.getSource() == getJComboBoxControlDevice()) {
                this.jComboBoxControlDevice_ActionPerformed(e);
            }
            
            if (e.getSource() == getJComboBoxControlPoint()) {
                this.jComboBoxControlPoint_ActionPerformed(e);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JComboBox<LiteYukonPAObject> getJComboBoxControlDevice() {
        if (ivjJComboBoxControlDevice == null) {
            try {
                ivjJComboBoxControlDevice = new JComboBox<LiteYukonPAObject>();
                ivjJComboBoxControlDevice.setName("JComboBoxControlDevice");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxControlDevice;
    }

    private JComboBox<LitePoint> getJComboBoxControlPoint() {
        if (ivjJComboBoxControlPoint == null) {
            try {
                ivjJComboBoxControlPoint = new JComboBox<LitePoint>();
                ivjJComboBoxControlPoint.setName("JComboBoxControlPoint");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxControlPoint;
    }

    private JComboBox<LiteState> getJComboBoxControlStartState() {
        if (ivjJComboBoxControlStartState == null) {
            try {
                ivjJComboBoxControlStartState = new JComboBox<LiteState>();
                ivjJComboBoxControlStartState.setName("JComboBoxControlStartState");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJComboBoxControlStartState;
    }

    private JLabel getJLabelControlPoint() {
        if (ivjJLabelControlPoint == null) {
            try {
                ivjJLabelControlPoint = new JLabel();
                ivjJLabelControlPoint.setName("JLabelControlPoint");
                ivjJLabelControlPoint.setText("Control Point:");
                ivjJLabelControlPoint.setEnabled(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelControlPoint;
    }

    private JLabel getJLabelControlStartState() {
        if (ivjJLabelControlStartState == null) {
            try {
                ivjJLabelControlStartState = new JLabel();
                ivjJLabelControlStartState.setName("JLabelControlStartState");
                ivjJLabelControlStartState.setText("Control Start State:");
                ivjJLabelControlStartState.setEnabled(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelControlStartState;
    }

    private JLabel getJLabelDeviceControl() {
        if (ivjJLabelDeviceControl == null) {
            try {
                ivjJLabelDeviceControl = new JLabel();
                ivjJLabelDeviceControl.setName("JLabelDeviceControl");
                ivjJLabelDeviceControl.setText("Control Device:");
                ivjJLabelDeviceControl.setEnabled(true);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJLabelDeviceControl;
    }

    @Override
    public Object getValue(Object o) {
        LMGroupPoint group = null;

        if (o instanceof SmartMultiDBPersistent)
            group = (LMGroupPoint) ((SmartMultiDBPersistent) o).getOwnerDBPersistent();
        else
            group = (LMGroupPoint) o;

        if (group != null) {
            group.getLMGroupPoint().setDeviceIDUsage(((LiteYukonPAObject) getJComboBoxControlDevice().getSelectedItem()).getYukonID());
            group.getLMGroupPoint().setPointIDUsage(((LitePoint) getJComboBoxControlPoint().getSelectedItem()).getPointID());
            group.getLMGroupPoint().setStartControlRawState(getJComboBoxControlStartState().getSelectedIndex());
        }

        return o;
    }

    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    private void initComboBoxes() {
        getJComboBoxControlDevice().removeAllItems();
        getJComboBoxControlPoint().removeAllItems();
        getJComboBoxControlStartState().removeAllItems();

        IDatabaseCache cache = DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllYukonPAObjects();
            
            for (LiteYukonPAObject liteDevice : devices) {

                // only do RTUs, MCTs and CAPBANKCONTROLLERS for now!
                if (liteDevice.getPaoType().isRtu() || liteDevice.getPaoType().isIon() ||
                        liteDevice.getPaoType().isCbc() || 
                        liteDevice.getPaoType().isMct()) {
                    List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(liteDevice.getYukonID());

                    // pointList gets inserted into the hashtable
                    List<LitePoint> pointList = new ArrayList<LitePoint>(points.size() / 2);

                    for (LitePoint point : points) {
                        if (point.getPointTypeEnum() == PointType.Status) {
                            // a DBPersistent must be created from the Lite object so you can do a retrieve this process is expensive!!! This is why
                            // LitePoints are stored in the localHashTable
                            StatusPoint dbPoint = (StatusPoint) LiteFactory.createDBPersistent(point);
                            try {
                                Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, dbPoint);

                                dbPoint = (StatusPoint) t.execute();

                                // only add status points that have control
                                if (dbPoint.getPointStatusControl().hasControl()) {
                                    pointList.add(point); // adds a LitePoint to our pointList
                                }
                            } catch (Exception e) {
                                handleException(e);
                            }
                        }
                    }

                    if (pointList.size() > 0) {
                        localHashTable.put(liteDevice, pointList);
                        getJComboBoxControlDevice().addItem(liteDevice);
                    }
                }
            }

            if (getJComboBoxControlPoint().getItemCount() > 0) {
                jComboBoxControlPoint_ActionPerformed(null);
            }
        }
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        // user code begin {1}

        // user code end
        getJComboBoxControlStartState().addActionListener(this);
        getJComboBoxControlDevice().addActionListener(this);
        getJComboBoxControlPoint().addActionListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("LMGroupPointEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(342, 371);

            java.awt.GridBagConstraints constraintsJLabelControlStartState = new java.awt.GridBagConstraints();
            constraintsJLabelControlStartState.gridx = 1;
            constraintsJLabelControlStartState.gridy = 3;
            constraintsJLabelControlStartState.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelControlStartState.ipadx = 6;
            constraintsJLabelControlStartState.insets = new java.awt.Insets(14, 9, 249, 0);
            add(getJLabelControlStartState(), constraintsJLabelControlStartState);

            java.awt.GridBagConstraints constraintsJComboBoxControlStartState = new java.awt.GridBagConstraints();
            constraintsJComboBoxControlStartState.gridx = 2;
            constraintsJComboBoxControlStartState.gridy = 3;
            constraintsJComboBoxControlStartState.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxControlStartState.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxControlStartState.weightx = 1.0;
            constraintsJComboBoxControlStartState.ipadx = 78;
            constraintsJComboBoxControlStartState.insets = new java.awt.Insets(9, 1, 245, 14);
            add(getJComboBoxControlStartState(), constraintsJComboBoxControlStartState);

            java.awt.GridBagConstraints constraintsJLabelDeviceControl = new java.awt.GridBagConstraints();
            constraintsJLabelDeviceControl.gridx = 1;
            constraintsJLabelDeviceControl.gridy = 1;
            constraintsJLabelDeviceControl.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelDeviceControl.ipadx = 6;
            constraintsJLabelDeviceControl.insets = new java.awt.Insets(27, 9, 12, 23);
            add(getJLabelDeviceControl(), constraintsJLabelDeviceControl);

            java.awt.GridBagConstraints constraintsJComboBoxControlDevice = new java.awt.GridBagConstraints();
            constraintsJComboBoxControlDevice.gridx = 2;
            constraintsJComboBoxControlDevice.gridy = 1;
            constraintsJComboBoxControlDevice.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxControlDevice.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxControlDevice.weightx = 1.0;
            constraintsJComboBoxControlDevice.ipadx = 78;
            constraintsJComboBoxControlDevice.insets = new java.awt.Insets(22, 1, 8, 14);
            add(getJComboBoxControlDevice(), constraintsJComboBoxControlDevice);

            java.awt.GridBagConstraints constraintsJLabelControlPoint = new java.awt.GridBagConstraints();
            constraintsJLabelControlPoint.gridx = 1;
            constraintsJLabelControlPoint.gridy = 2;
            constraintsJLabelControlPoint.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelControlPoint.ipadx = 10;
            constraintsJLabelControlPoint.insets = new java.awt.Insets(13, 9, 14, 23);
            add(getJLabelControlPoint(), constraintsJLabelControlPoint);

            java.awt.GridBagConstraints constraintsJComboBoxControlPoint = new java.awt.GridBagConstraints();
            constraintsJComboBoxControlPoint.gridx = 2;
            constraintsJComboBoxControlPoint.gridy = 2;
            constraintsJComboBoxControlPoint.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsJComboBoxControlPoint.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxControlPoint.weightx = 1.0;
            constraintsJComboBoxControlPoint.ipadx = 78;
            constraintsJComboBoxControlPoint.insets = new java.awt.Insets(9, 1, 9, 14);
            add(getJComboBoxControlPoint(), constraintsJComboBoxControlPoint);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        initComboBoxes();
    }

    @Override
    public boolean isInputValid() {
        if (getJComboBoxControlDevice().getSelectedItem() == null) {
            setErrorString("A control device must be selected");
            return false;
        }

        if (getJComboBoxControlPoint().getSelectedItem() == null) {
            setErrorString("A control point must be selected");
            return false;
        }

        if (getJComboBoxControlStartState().getSelectedItem() == null) {
            setErrorString("A control start state must be selected");
            return false;
        }

        return true;
    }

    public void jComboBoxControlDevice_ActionPerformed(ActionEvent actionEvent) {
        if (getJComboBoxControlDevice().getSelectedItem() != null) {
            getJComboBoxControlPoint().removeAllItems();

            List<LitePoint> litePointList = localHashTable.get(getJComboBoxControlDevice().getSelectedItem());

            for (LitePoint litePoint : litePointList) {
                getJComboBoxControlPoint().addItem(litePoint);
            }
        }
        return;
    }

    public void jComboBoxControlPoint_ActionPerformed(ActionEvent actionEvent) {

        if (getJComboBoxControlPoint().getSelectedItem() != null) {
            getJComboBoxControlStartState().removeAllItems();

            LitePoint point = (LitePoint)getJComboBoxControlPoint().getSelectedItem();

            LiteStateGroup stateGroup = YukonSpringHook.getBean(StateGroupDao.class).getStateGroup(point.getStateGroupID());

            for (int j = 0; j < stateGroup.getStatesList().size(); j++) {
                // only add the first 2 states to the ComboBoxes
                if (j == 0 || j == 1) {
                    getJComboBoxControlStartState().addItem(stateGroup.getStatesList().get(j));
                }
            }
        }
        return;
    }

    @Override
    public void setValue(Object o) {
        if (o instanceof LMGroupPoint) {
            LMGroupPoint group = (LMGroupPoint) o;

            for (int i = 0; i < getJComboBoxControlDevice().getItemCount(); i++)
                if (getJComboBoxControlDevice().getItemAt(i).getYukonID() == group.getLMGroupPoint().getDeviceIDUsage().intValue()) {
                    getJComboBoxControlDevice().setSelectedIndex(i);
                }

            for (int i = 0; i < getJComboBoxControlPoint().getItemCount(); i++)
                if (getJComboBoxControlPoint().getItemAt(i).getPointID() == group.getLMGroupPoint().getPointIDUsage().intValue()) {
                    getJComboBoxControlPoint().setSelectedIndex(i);
                }

            int startState = group.getLMGroupPoint().getStartControlRawState().intValue();
            if (startState >= 0 && startState < getJComboBoxControlStartState().getItemCount())
                getJComboBoxControlStartState().setSelectedIndex(startState);
        }
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getJComboBoxControlStartState().requestFocus();
            }
        });
    }
}
package com.cannontech.dbeditor.wizard.customer;


import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import com.cannontech.common.gui.util.AddRemovePanel;
import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.customer.DeviceCustomerList;
import com.cannontech.yukon.IDatabaseCache;

public class CustomerMeterListEditorPanel extends DataInputPanel implements AddRemovePanelListener {
    private int rightListItemIndex = getMeterListAddRemovePanel().rightListGetSelectedIndex();
    private boolean rightListDragging = false;
    private AddRemovePanel ivjMeterListAddRemovePanel = null;

    public CustomerMeterListEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMeterListAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void ccStrategyBankListAddRemovePanel_RightListMouse_mouseExited(EventObject newEvent) {
        rightListItemIndex = -1;
        rightListDragging = false;
    }

    private AddRemovePanel getMeterListAddRemovePanel() {
        if (ivjMeterListAddRemovePanel == null) {
            try {
                ivjMeterListAddRemovePanel = new AddRemovePanel();
                ivjMeterListAddRemovePanel.setName("MeterListAddRemovePanel");

                ivjMeterListAddRemovePanel.setMode(AddRemovePanel.TRANSFER_MODE);
                ivjMeterListAddRemovePanel.leftListLabelSetText("Unowned Meters");
                ivjMeterListAddRemovePanel.rightListLabelSetText("Owned Meters");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMeterListAddRemovePanel;
    }

    @Override
    public Object getValue(Object val) {
        CICustomerBase customer = (CICustomerBase) val;
        customer.getDeviceVector().removeAllElements();

        for (int i = 0; i < getMeterListAddRemovePanel().rightListGetModel().getSize(); i++) {
            DeviceCustomerList deviceCustomerItem = new DeviceCustomerList();

            deviceCustomerItem.setCustomerID(customer.getCustomerID());
            deviceCustomerItem.setDeviceID(((LiteYukonPAObject) getMeterListAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());

            customer.getDeviceVector().addElement(deviceCustomerItem);
        }
        return val;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getMeterListAddRemovePanel().addAddRemovePanelListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("CustomerMeterListEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(447, 311);

            java.awt.GridBagConstraints constraintsMeterListAddRemovePanel = new java.awt.GridBagConstraints();
            constraintsMeterListAddRemovePanel.gridx = 1;
            constraintsMeterListAddRemovePanel.gridy = 1;
            constraintsMeterListAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsMeterListAddRemovePanel.weightx = 1.0;
            constraintsMeterListAddRemovePanel.weighty = 1.0;
            constraintsMeterListAddRemovePanel.ipadx = 185;
            constraintsMeterListAddRemovePanel.ipady = 192;
            constraintsMeterListAddRemovePanel.insets = new java.awt.Insets(4, 5, 4, 6);
            add(getMeterListAddRemovePanel(), constraintsMeterListAddRemovePanel);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {
        return true;
    }

    @Override
    public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
    }

    public void meterAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
        rightListItemIndex = -1;
        rightListDragging = false;
    }

    public void meterAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
        rightListItemIndex = getMeterListAddRemovePanel().rightListGetSelectedIndex();
        rightListDragging = true;
    }

    public void meterAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
        int indexSelected = getMeterListAddRemovePanel().rightListGetSelectedIndex();

        if (rightListDragging && indexSelected != -1 && indexSelected != rightListItemIndex) {

            Vector<LiteYukonPAObject> destItems = new Vector<LiteYukonPAObject>(getMeterListAddRemovePanel().rightListGetModel().getSize() + 1);

            for (int i = 0; i < getMeterListAddRemovePanel().rightListGetModel().getSize(); i++) {
                destItems.addElement((LiteYukonPAObject)getMeterListAddRemovePanel().rightListGetModel().getElementAt(i));
            }

            LiteYukonPAObject itemSelected = destItems.elementAt(rightListItemIndex);
            destItems.removeElementAt(rightListItemIndex);
            destItems.insertElementAt(itemSelected, indexSelected);
            getMeterListAddRemovePanel().rightListSetListData(destItems);

            getMeterListAddRemovePanel().revalidate();
            getMeterListAddRemovePanel().repaint();

            // reset the values
            rightListItemIndex = -1;
            fireInputUpdate();
        }

        rightListDragging = false;
    }

    @Override
    public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMeterListAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void repeatersAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
        rightListItemIndex = -1;
        rightListDragging = false;
    }

    @Override
    public void rightListListSelection_valueChanged(java.util.EventObject newEvent) {
    }

    @Override
    public void rightListMouse_mouseClicked(java.util.EventObject newEvent) {
    }

    @Override
    public void rightListMouse_mouseEntered(java.util.EventObject newEvent) {
    }

    @Override
    public void rightListMouse_mouseExited(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMeterListAddRemovePanel()) {
                this.meterAddRemovePanel_RightListMouse_mouseExited(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMeterListAddRemovePanel()) {
                this.meterAddRemovePanel_RightListMouse_mousePressed(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMeterListAddRemovePanel()) {
                this.meterAddRemovePanel_RightListMouse_mouseReleased(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
    }

    @Override
    public void setValue(Object val) {
        CICustomerBase customer = (CICustomerBase) val;

        Vector<LiteYukonPAObject> availableMeters = null;
        Vector<LiteYukonPAObject> usedMeters = null;
        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();

        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();

            availableMeters = new Vector<LiteYukonPAObject>(devices.size());
            usedMeters = new Vector<LiteYukonPAObject>(customer.getDeviceVector().size());

            for (LiteYukonPAObject liteDevice : devices) {

                if (DeviceTypesFuncs.isMeter(liteDevice.getPaoType().getDeviceTypeId())) {
                    availableMeters.add(liteDevice);

                    for (int j = 0; j < customer.getDeviceVector().size(); j++) {
                        DeviceCustomerList deviceCustomerValue = (customer.getDeviceVector().elementAt(j));
                        if (deviceCustomerValue.getDeviceID().intValue() == liteDevice.getYukonID()) {
                            availableMeters.remove(liteDevice);
                            usedMeters.add(liteDevice);
                            break;
                        }
                    }
                }
            }
        }

        getMeterListAddRemovePanel().leftListSetListData(availableMeters);
        getMeterListAddRemovePanel().rightListSetListData(usedMeters);
    }
}
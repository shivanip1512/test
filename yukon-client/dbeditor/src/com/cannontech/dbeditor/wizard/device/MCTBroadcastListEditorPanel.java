package com.cannontech.dbeditor.wizard.device;

import java.util.List;
import java.util.Vector;

import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.MCT_Broadcast;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.MCTBroadcastMapping;
import com.cannontech.yukon.IDatabaseCache;

public class MCTBroadcastListEditorPanel extends DataInputPanel implements AddRemovePanelListener {
    private int rightListItemIndex = getMCTListAddRemovePanel().rightListGetSelectedIndex();
    private boolean rightListDragging = false;
    private com.cannontech.common.gui.util.AddRemovePanel ivjMCTListAddRemovePanel = null;

    public MCTBroadcastListEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMCTListAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private com.cannontech.common.gui.util.AddRemovePanel getMCTListAddRemovePanel() {
        if (ivjMCTListAddRemovePanel == null) {
            try {
                ivjMCTListAddRemovePanel = new com.cannontech.common.gui.util.AddRemovePanel();
                ivjMCTListAddRemovePanel.setName("MCTListAddRemovePanel");

                ivjMCTListAddRemovePanel.setMode(com.cannontech.common.gui.util.AddRemovePanel.TRANSFER_MODE);
                ivjMCTListAddRemovePanel.leftListLabelSetText("Available MCTs");
                ivjMCTListAddRemovePanel.rightListLabelSetText("Assigned MCTs");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjMCTListAddRemovePanel;
    }

    @Override
    public Object getValue(Object val) {

        MCT_Broadcast broadcaster = (MCT_Broadcast) val;
        broadcaster.getMCTVector().removeAllElements();
        for (int i = 0; i < getMCTListAddRemovePanel().rightListGetModel().getSize(); i++) {

            MCTBroadcastMapping mappingOfMCTs = new MCTBroadcastMapping();
            mappingOfMCTs.setMctID(((LiteYukonPAObject) getMCTListAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());
            mappingOfMCTs.setMctBroadcastID(broadcaster.getDevice().getDeviceID());
            broadcaster.getMCTVector().addElement(mappingOfMCTs);
        }
        return broadcaster;
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
        getMCTListAddRemovePanel().addAddRemovePanelListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("MCTBroadcastListEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(447, 311);

            java.awt.GridBagConstraints constraintsMCTListAddRemovePanel = new java.awt.GridBagConstraints();
            constraintsMCTListAddRemovePanel.gridx = 1;
            constraintsMCTListAddRemovePanel.gridy = 1;
            constraintsMCTListAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsMCTListAddRemovePanel.weightx = 1.0;
            constraintsMCTListAddRemovePanel.weighty = 1.0;
            constraintsMCTListAddRemovePanel.ipadx = 185;
            constraintsMCTListAddRemovePanel.ipady = 192;
            constraintsMCTListAddRemovePanel.insets = new java.awt.Insets(4, 5, 4, 6);
            add(getMCTListAddRemovePanel(), constraintsMCTListAddRemovePanel);
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

    public void MCTAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
        rightListItemIndex = -1;
        rightListDragging = false;
    }

    public void MCTAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
        rightListItemIndex = getMCTListAddRemovePanel().rightListGetSelectedIndex();
        rightListDragging = true;
    }

    public void MCTAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
        int indexSelected = getMCTListAddRemovePanel().rightListGetSelectedIndex();

        if (rightListDragging && indexSelected != -1 && indexSelected != rightListItemIndex) {

            Vector<LiteYukonPAObject> destItems = new Vector<LiteYukonPAObject>(getMCTListAddRemovePanel().rightListGetModel().getSize() + 1);
            for (int i = 0; i < getMCTListAddRemovePanel().rightListGetModel().getSize(); i++) {
                destItems.addElement((LiteYukonPAObject)getMCTListAddRemovePanel().rightListGetModel().getElementAt(i));
            }

            LiteYukonPAObject itemSelected = destItems.elementAt(rightListItemIndex);
            destItems.removeElementAt(rightListItemIndex);
            destItems.insertElementAt(itemSelected, indexSelected);
            getMCTListAddRemovePanel().rightListSetListData(destItems);

            getMCTListAddRemovePanel().revalidate();
            getMCTListAddRemovePanel().repaint();

            // reset the values
            rightListItemIndex = -1;
            fireInputUpdate();
        }

        rightListDragging = false;
    }

    @Override
    public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMCTListAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
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
            if (newEvent.getSource() == getMCTListAddRemovePanel()) {
                this.MCTAddRemovePanel_RightListMouse_mouseExited(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMCTListAddRemovePanel()) {
                this.MCTAddRemovePanel_RightListMouse_mousePressed(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getMCTListAddRemovePanel()) {
                this.MCTAddRemovePanel_RightListMouse_mouseReleased(newEvent);
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

        MCT_Broadcast broadcaster = (MCT_Broadcast) val;
        // This check may be necessary if this is acting as a Wizardpanel rather than an EditorPanel
        if (broadcaster == null) {
            broadcaster = new MCT_Broadcast();
        }
        Vector<LiteYukonPAObject> availableMCTs = null;
        Vector<LiteYukonPAObject> usedMCTs = null;
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();

            availableMCTs = new Vector<LiteYukonPAObject>(devices.size());
            usedMCTs = new Vector<LiteYukonPAObject>(broadcaster.getMCTVector().size());

            for (LiteYukonPAObject liteDevice : devices) {

                if (DeviceTypesFuncs.isMCT(liteDevice.getPaoType().getDeviceTypeId())) {
                    availableMCTs.add(liteDevice);

                    for (MCTBroadcastMapping mappedMCT : broadcaster.getMCTVector()) {

                        if (mappedMCT.getMctID().intValue() == liteDevice.getYukonID()) {
                            availableMCTs.remove(liteDevice);
                            usedMCTs.add(liteDevice);
                            break;
                        }
                    }
                }
            }
        }

        getMCTListAddRemovePanel().leftListSetListData(availableMCTs);
        getMCTListAddRemovePanel().rightListSetListData(usedMCTs);
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getMCTListAddRemovePanel().requestFocus();
            }
        });
    }
}
package com.cannontech.dbeditor.wizard.route;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

import com.cannontech.common.gui.util.AddRemovePanel;
import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteRole;
import com.cannontech.database.data.route.RouteUsageHelper;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.dbeditor.editor.regenerate.RoleConflictDialog;
import com.cannontech.yukon.IDatabaseCache;

public class RepeaterSelectPanel extends DataInputPanel implements AddRemovePanelListener {
    private JLabel ivjRepeaterLabel = null;
    private Frame owner = SwingUtil.getParentFrame(this);
    private AddRemovePanel ivjRepeatersAddRemovePanel = null;
    private int rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
    private boolean rightListDragging = false;

    private JLabel errorMessageLabel = null;

    public RepeaterSelectPanel() {
        super();
        initialize();
    }

    @Override
    public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private JLabel getRepeaterLabel() {
        if (ivjRepeaterLabel == null) {
            try {
                ivjRepeaterLabel = new JLabel();
                ivjRepeaterLabel.setName("RepeaterLabel");
                ivjRepeaterLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjRepeaterLabel.setText("Select the repeater(s) to include in this route:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeaterLabel;
    }

    private AddRemovePanel getRepeatersAddRemovePanel() {
        if (ivjRepeatersAddRemovePanel == null) {
            try {
                ivjRepeatersAddRemovePanel = new AddRemovePanel();
                ivjRepeatersAddRemovePanel.setName("RepeatersAddRemovePanel");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeatersAddRemovePanel;
    }

    @Override
    public Object getValue(Object val) {
        Integer routeID = ((RouteBase) val).getRouteID();

        List<RepeaterRoute> repeaters = ((CCURoute) val).getRepeaters();
        repeaters.clear();;

        for (int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++) {
            RepeaterRoute rRoute = new RepeaterRoute();
            rRoute.setRouteID(routeID);
            rRoute.setDeviceID(new Integer(((LiteYukonPAObject) getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID()));
            rRoute.setVariableBits(new Integer(7));
            rRoute.setRepeaterOrder(new Integer(i + 1));

            repeaters.add(rRoute);
        }
        RouteUsageHelper routeBoss = new RouteUsageHelper();
        RouteRole role = routeBoss.assignRouteLocation((CCURoute) val, null, null);
        if (role.getDuplicates().isEmpty()) {
            ((CCURoute) val).getCarrierRoute().setCcuFixBits(new Integer(role.getFixedBit()));
            ((CCURoute) val).getCarrierRoute().setCcuVariableBits(new Integer(role.getVarbit()));

            int rptVarBit = role.getVarbit();

            for (int j = 0; j < repeaters.size(); j++) {
                RepeaterRoute rpt = repeaters.get(j);
                if (rptVarBit + 1 <= 7)
                    rptVarBit++;
                if (j + 1 == repeaters.size())
                    rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
                rpt.setVariableBits(new Integer(rptVarBit));
            }

        } else { // All route combinations have been used, suggest a suitable role combonation to reuse.

            RoleConflictDialog frame = new RoleConflictDialog(owner, role, (CCURoute) val, routeBoss);
            frame.setLocationRelativeTo(this);
            String choice = frame.getValue();
            boolean finished = false;
            int startingSpot = role.getFixedBit();
            while (!finished) {

                if (choice == "Yes") {
                    finished = true;
                    ((CCURoute) val).getCarrierRoute().setCcuFixBits(new Integer(frame.getRole().getFixedBit()));
                    ((CCURoute) val).getCarrierRoute().setCcuVariableBits(new Integer(frame.getRole().getVarbit()));

                    int rptVarBit = frame.getRole().getVarbit();

                    for (int j = 0; j < repeaters.size(); j++) {
                        RepeaterRoute rpt = repeaters.get(j);
                        if (rptVarBit + 1 <= 7) {
                            rptVarBit++;
                        }
                        if (j + 1 == repeaters.size()) {
                            rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
                        }
                        rpt.setVariableBits(new Integer(rptVarBit));
                    }
                } else if (choice == "Cancel") {
                    finished = true;
                    return null;
                } else {
                    finished = true;
                    return null;
                }
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
        getRepeatersAddRemovePanel().addAddRemovePanelListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("RepeaterSelectPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(370, 243);

            java.awt.GridBagConstraints constraintsRepeaterLabel = new java.awt.GridBagConstraints();
            constraintsRepeaterLabel.gridx = 1;
            constraintsRepeaterLabel.gridy = 1;
            constraintsRepeaterLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRepeaterLabel.insets = new java.awt.Insets(5, 0, 0, 0);
            add(getRepeaterLabel(), constraintsRepeaterLabel);

            java.awt.GridBagConstraints constraintsRepeatersAddRemovePanel = new java.awt.GridBagConstraints();
            constraintsRepeatersAddRemovePanel.gridx = 1;
            constraintsRepeatersAddRemovePanel.gridy = 2;
            constraintsRepeatersAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsRepeatersAddRemovePanel.weightx = 1.0;
            constraintsRepeatersAddRemovePanel.weighty = 1.0;
            add(getRepeatersAddRemovePanel(), constraintsRepeatersAddRemovePanel);

            java.awt.GridBagConstraints constraintsRepeaterErrorLabel = new GridBagConstraints();
            constraintsRepeaterErrorLabel.gridx = 1;
            constraintsRepeaterErrorLabel.gridy = 3;
            constraintsRepeaterErrorLabel.gridwidth = 2;
            constraintsRepeaterErrorLabel.anchor = GridBagConstraints.WEST;
            constraintsRepeaterErrorLabel.insets = new java.awt.Insets(0, 2, 0, 2);
            add(getErrorMessageLabel(), constraintsRepeaterErrorLabel);

            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private JLabel getErrorMessageLabel() {
        if (errorMessageLabel == null) {
            try {
                errorMessageLabel = new JLabel();
                errorMessageLabel.setName("ErrorMessageLabel");
                errorMessageLabel.setFont(new Font("Arial", 1, 10));
                errorMessageLabel.setHorizontalTextPosition(SwingConstants.CENTER);
                errorMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return errorMessageLabel;
    }

    @Override
    public boolean isInputValid() {
        ListModel rightListModel = getRepeatersAddRemovePanel().rightListGetModel();
        if (rightListModel.getSize() < 1) {
            setErrorString("One or more repeaters should be selected");
            getErrorMessageLabel().setText(getErrorString());
            return false;
        }

        for (int i = 0; i < rightListModel.getSize(); i++) {

            LiteYukonPAObject pao = (LiteYukonPAObject) rightListModel.getElementAt(i);

            if (pao.getPaoIdentifier().getPaoType() == PaoType.REPEATER_850) {
                if (i != rightListModel.getSize() - 1) {
                    setErrorString("When present, a Repeater 850 MUST be the last repeater in the repeater chain");
                    getErrorMessageLabel().setText(getErrorString());
                    return false;
                }
            }
        }

        getErrorMessageLabel().setText("");
        return true;
    }

    @Override
    public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
    }

    @Override
    public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void repeatersAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
        rightListItemIndex = -1;
        rightListDragging = false;
        return;
    }

    public void repeatersAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
        rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
        rightListDragging = true;
        return;
    }

    public void repeatersAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
        int indexSelected = getRepeatersAddRemovePanel().rightListGetSelectedIndex();

        if (rightListDragging && indexSelected != -1 && indexSelected != rightListItemIndex) {

            Object itemSelected = new Object();
            java.util.Vector destItems = new java.util.Vector(getRepeatersAddRemovePanel().rightListGetModel().getSize() + 1);

            for (int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++)
                destItems.addElement(getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i));

            itemSelected = destItems.elementAt(rightListItemIndex);
            destItems.removeElementAt(rightListItemIndex);
            destItems.insertElementAt(itemSelected, indexSelected);
            getRepeatersAddRemovePanel().rightListSetListData(destItems);

            getRepeatersAddRemovePanel().revalidate();
            getRepeatersAddRemovePanel().repaint();

            // reset the values
            rightListItemIndex = -1;
            fireInputUpdate();
        }

        rightListDragging = false;

        return;
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
            if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
                this.repeatersAddRemovePanel_RightListMouse_mouseExited(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
                this.repeatersAddRemovePanel_RightListMouse_mousePressed(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
                this.repeatersAddRemovePanel_RightListMouse_mouseReleased(newEvent);
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
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        java.util.Vector<LiteYukonPAObject> allRepeaters = null;
        synchronized (cache) {
            List<LiteYukonPAObject> allDevices = cache.getAllDevices();
            allRepeaters = new Vector<LiteYukonPAObject>();
            for (LiteYukonPAObject liteYukonPAObject : allDevices) {
                if (DeviceTypesFuncs.isRepeater(liteYukonPAObject.getPaoType().getDeviceTypeId())) {
                    allRepeaters.add(liteYukonPAObject);
                }
            }
        }

        com.cannontech.common.gui.util.AddRemovePanel repeatersPanel = getRepeatersAddRemovePanel();
        repeatersPanel.setMode(repeatersPanel.TRANSFER_MODE);
        repeatersPanel.leftListRemoveAll();
        repeatersPanel.rightListRemoveAll();

        repeatersPanel.leftListSetListData(allRepeaters);
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getRepeatersAddRemovePanel().requestFocus();
            }
        });
    }
}
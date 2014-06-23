package com.cannontech.dbeditor.editor.route;

import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.RepeaterAddRemovePanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.RouteRole;
import com.cannontech.database.data.route.RouteUsageHelper;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.dbeditor.editor.regenerate.RoleConflictDialog;
import com.cannontech.yukon.IDatabaseCache;

public class RepeaterSetupEditorPanel extends DataInputPanel implements AddRemovePanelListener, ActionListener {
    private JButton ivjAdvancedSetupButton = null;
    private JLabel idiotLabel = null;
    private JLabel ccuOrderLabel = null;
    private AdvancedRepeaterSetupEditorPanel advancedRepeaterSetupEditorPanel = null;
    private Object objectToEdit = null;
    private int rightListItemIndex = getRepeatersAddRemovePanel().rightListGetSelectedIndex();
    private Frame owner = SwingUtil.getParentFrame(this);
    private boolean rightListDragging = false;
    private RepeaterAddRemovePanel ivjRepeatersAddRemovePanel = null;
    private boolean addOrRemoveHasBeenDone = false;
    private boolean changeUpdated = true;
    private boolean dbRegenerate = false;
    private boolean advancedSettingsDone = false;
    private AdvancedRouteSetupDialog frame = null;

    public RepeaterSetupEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getAdvancedSetupButton()) {
            try {
                this.advancedSetupButton_ActionPerformed(e);
            } catch (java.lang.Throwable ivjExc) {
                if (ivjExc instanceof NullPointerException) {
                    StringBuffer error = new StringBuffer("Advanced Setup has detected a bit field value that is invalid. \n" + "The variable bit fields accept values only between 0 and 7. \n " + "Previous values have been restored.");
                    JOptionPane.showMessageDialog(this, error, "BIT VALUE OUT OF RANGE", JOptionPane.ERROR_MESSAGE);
                    getAdvancedSetupButton().doClick();
                }
                handleException(ivjExc);
            }
        }
    }

    @Override
    public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {

        try {
            if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
                this.fireInputUpdate();
                getAdvancedSetupButton().setEnabled(false);
                getIdiotLabel().setVisible(true);
                addOrRemoveHasBeenDone = true;
                changeUpdated = false;
                dbRegenerate = true;
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void advancedSetupButton_ActionPerformed(ActionEvent actionEvent) {

        Frame owner = SwingUtil.getParentFrame(this);

        frame = new AdvancedRouteSetupDialog(owner, (CCURoute) this.objectToEdit);
        frame.setLocationRelativeTo(this);
        String choice = frame.getValue();

        if (choice.equalsIgnoreCase("OK")) {
            advancedSettingsDone = true;
            dbRegenerate = true;
            fireInputDataPanelEvent(new PropertyPanelEvent(this, PropertyPanelEvent.EVENT_FORCE_APPLY));
            fireInputUpdate();
            setValue(this.objectToEdit);
        }
    }


    protected AdvancedRepeaterSetupEditorPanel getAdvancedRepeaterSetupEditorPanel() {
        if (advancedRepeaterSetupEditorPanel == null) {
            advancedRepeaterSetupEditorPanel = new AdvancedRepeaterSetupEditorPanel();
        }
        return advancedRepeaterSetupEditorPanel;
    }

    private JButton getAdvancedSetupButton() {
        if (ivjAdvancedSetupButton == null) {
            try {
                ivjAdvancedSetupButton = new JButton();
                ivjAdvancedSetupButton.setName("AdvancedSetupButton");
                ivjAdvancedSetupButton.setText("Advanced Setup...");
                ivjAdvancedSetupButton.setMaximumSize(new java.awt.Dimension(159, 27));
                ivjAdvancedSetupButton.setActionCommand("Advanced Setup >>");
                ivjAdvancedSetupButton.setPreferredSize(new java.awt.Dimension(159, 27));
                ivjAdvancedSetupButton.setFont(new java.awt.Font("dialog", 0, 14));
                ivjAdvancedSetupButton.setMinimumSize(new java.awt.Dimension(159, 27));
                ivjAdvancedSetupButton.setEnabled(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAdvancedSetupButton;
    }

    private JLabel getIdiotLabel() {
        if (idiotLabel == null) {
            idiotLabel = new JLabel();
            idiotLabel.setText("* Apply changes to enable advanced settings options.");
            idiotLabel.setVisible(false);
        }
        return idiotLabel;
    }

    private JLabel getCcuOrderLabel() {
        if (ccuOrderLabel == null) {
            ccuOrderLabel = new JLabel();
            ccuOrderLabel.setText("Repeater Order: Closest to CCU on top.");
            ccuOrderLabel.setFont(new java.awt.Font("Arial", 1, 12));
        }
        return ccuOrderLabel;
    }

    private RepeaterAddRemovePanel getRepeatersAddRemovePanel() {
        if (ivjRepeatersAddRemovePanel == null) {
            try {
                ivjRepeatersAddRemovePanel = new RepeaterAddRemovePanel();
                ivjRepeatersAddRemovePanel.setName("RepeatersAddRemovePanel");
                ivjRepeatersAddRemovePanel.setMode(RepeaterAddRemovePanel.TRANSFER_MODE);
                ivjRepeatersAddRemovePanel.setRightListMax(new Integer(7));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRepeatersAddRemovePanel;
    }

    @Override
    @SuppressWarnings({ "cast", "unchecked" })
    public Object getValue(Object val) {
        CCURoute route = (CCURoute) val;

        // Build up an assigned repeaterRoute Vector
        List<RepeaterRoute> repeaterRoutes = new ArrayList<RepeaterRoute>(getRepeatersAddRemovePanel().rightListGetModel().getSize());
        Integer deviceID = null;
        for (int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++) {
            deviceID = new Integer(((LiteYukonPAObject) getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());
            RepeaterRoute rr = new RepeaterRoute(route.getRouteID(), deviceID, new Integer(7), new Integer(i + 1));
            repeaterRoutes.add(rr);
        }

        if (!route.getRepeaters().isEmpty()) {
            for (RepeaterRoute repeaterRoute : repeaterRoutes) {
                for (RepeaterRoute repeater : route.getRepeaters()) {
                    if (repeater.getDeviceID().equals(repeaterRoute.getDeviceID())) {
                        repeaterRoute.setVariableBits(repeater.getVariableBits());
                        break;
                    }
                }
            }
        }
        if (getRepeatersAddRemovePanel().getRightList().getModel().getSize() == 0) {
            dbRegenerate = false;
            route.getCarrierRoute().setCcuFixBits(new Integer(31));
            route.getCarrierRoute().setCcuVariableBits(new Integer(7));
        }

        route.setRepeaters(repeaterRoutes);
        if (dbRegenerate) {
            if (!advancedSettingsDone) {
                String userLocked = route.getCarrierRoute().getUserLocked();
                if (userLocked.equalsIgnoreCase("N")) {

                    List<CCURoute> changingRoutes = new ArrayList<CCURoute>(1);
                    changingRoutes.add(route);
                    RouteUsageHelper routeBoss = new RouteUsageHelper();
                    routeBoss.removeChanginRoutes(changingRoutes);

                    RouteRole role = routeBoss.assignRouteLocation(route, null, null);
                    if (role.getDuplicates().isEmpty()) {
                        route.getCarrierRoute().setCcuFixBits(new Integer(role.getFixedBit()));
                        route.getCarrierRoute().setCcuVariableBits(new Integer(role.getVarbit()));

                        int rptVarBit = role.getVarbit();

                        for (int j = 0; j < repeaterRoutes.size(); j++) {
                            RepeaterRoute rpt = (repeaterRoutes.get(j));
                            if (rptVarBit + 1 <= 7)
                                rptVarBit++;
                            if (j + 1 == repeaterRoutes.size())
                                rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
                            rpt.setVariableBits(new Integer(rptVarBit));
                        }

                    } else { // All route combinations have been used, suggest a suitable role combonation to reuse.

                        RoleConflictDialog frame = new RoleConflictDialog(owner, role, route, routeBoss);
                        frame.setLocationRelativeTo(this);
                        String choice = frame.getValue();
                        boolean finished = false;
                        int startingSpot = role.getFixedBit();
                        while (!finished) {

                            if (choice == "Yes") {
                                finished = true;
                                route.getCarrierRoute().setCcuFixBits(new Integer(frame.getRole().getFixedBit()));
                                route.getCarrierRoute().setCcuVariableBits(new Integer(frame.getRole().getVarbit()));

                                int rptVarBit = frame.getRole().getVarbit();

                                for (int j = 0; j < repeaterRoutes.size(); j++) {
                                    RepeaterRoute rpt = (repeaterRoutes.get(j));
                                    if (rptVarBit + 1 <= 7) {
                                        rptVarBit++;
                                    }
                                    if (j + 1 == repeaterRoutes.size()) {
                                        rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
                                    }
                                    rpt.setVariableBits(new Integer(rptVarBit));
                                }
                            } else if (choice == "Cancel") {
                                return null;
                            } else {
                                finished = true;
                                return null;
                            }
                        }
                    }
                    this.objectToEdit = route;
                }
                dbRegenerate = false;
            } else {
                // advanced settings panel has done the checking work.
                int unMaskedFixedBit = frame.getUnMaskedFixedBit();
                int varBit = frame.getVariableBit();
                route.getCarrierRoute().setCcuFixBits(new Integer(unMaskedFixedBit));
                route.getCarrierRoute().setCcuVariableBits(new Integer(varBit));

                for (int j = 0; j < repeaterRoutes.size(); j++) {
                    RepeaterRoute rr = (repeaterRoutes.get(j));
                    if (j + 1 == repeaterRoutes.size()) {
                        varBit = 7; // Last repeater's variable bit is always lucky 7.
                    } else {
                        varBit = new Integer(frame.repeaterTextFieldArray[j].getText()).intValue();
                    }

                    rr.setVariableBits(new Integer(varBit));
                }
                if (frame.getLockCheckBox().isSelected()) {
                    route.getCarrierRoute().setUserLocked("Y");
                } else {
                    route.getCarrierRoute().setUserLocked("N");
                }
                setValue(route);
                this.objectToEdit = route;
                advancedSettingsDone = false;
                dbRegenerate = false;
            }
        }

        changeUpdated = true;
        if (getRepeatersAddRemovePanel().getRightList().getModel().getSize() > 0) {
            getAdvancedSetupButton().setEnabled(true);

        }
        getIdiotLabel().setVisible(false);

        for (int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++) {
            LiteYukonPAObject liteYukonPAObject = (LiteYukonPAObject) getRepeatersAddRemovePanel().rightListGetModel().getElementAt(i);
            if (liteYukonPAObject.getPaoIdentifier().getPaoType() == PaoType.REPEATER_850) {
                showMessageDialog(this,
                                  "This route contains a Repeater 850, it is recommended that " + "this repeater has no more than 10 devices connected.",
                                  "Information",
                                  javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        }

        return route;
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
        getAdvancedSetupButton().addActionListener(this);
        getRepeatersAddRemovePanel().addAddRemovePanelListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("RepeaterSetupEditorPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(382, 274);

            java.awt.GridBagConstraints constraintsCcuOrderLabel = new java.awt.GridBagConstraints();
            constraintsCcuOrderLabel.gridx = 0;
            constraintsCcuOrderLabel.gridy = 0;
            constraintsCcuOrderLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsCcuOrderLabel.insets = new java.awt.Insets(5, 5, 5, 0);
            add(getCcuOrderLabel(), constraintsCcuOrderLabel);

            java.awt.GridBagConstraints constraintsRepeatersAddRemovePanel = new java.awt.GridBagConstraints();
            constraintsRepeatersAddRemovePanel.gridx = 0;
            constraintsRepeatersAddRemovePanel.gridy = 1;
            constraintsRepeatersAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
            add(getRepeatersAddRemovePanel(), constraintsRepeatersAddRemovePanel);

            java.awt.GridBagConstraints constraintsAdvancedSetupButton = new java.awt.GridBagConstraints();
            constraintsAdvancedSetupButton.gridx = 0;
            constraintsAdvancedSetupButton.gridy = 2;
            constraintsAdvancedSetupButton.anchor = java.awt.GridBagConstraints.EAST;
            constraintsAdvancedSetupButton.insets = new java.awt.Insets(10, 0, 0, 0);
            add(getAdvancedSetupButton(), constraintsAdvancedSetupButton);

            java.awt.GridBagConstraints constraintsIdiotLabel = new java.awt.GridBagConstraints();
            constraintsIdiotLabel.gridx = 0;
            constraintsIdiotLabel.gridy = 3;
            constraintsIdiotLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsIdiotLabel.insets = new java.awt.Insets(5, 0, 0, 0);
            add(getIdiotLabel(), constraintsIdiotLabel);

            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
    }

    @Override
    public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getRepeatersAddRemovePanel()) {
                this.fireInputUpdate();
                getAdvancedSetupButton().setEnabled(false);
                getIdiotLabel().setVisible(true);
                addOrRemoveHasBeenDone = true;
                changeUpdated = false;
                dbRegenerate = true;
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

    protected void setAdvancedRepeaterSetupEditorPanel(AdvancedRepeaterSetupEditorPanel newValue) {
        this.advancedRepeaterSetupEditorPanel = newValue;
    }

    @Override
    public void setValue(Object val) {
        this.objectToEdit = val;

        com.cannontech.database.data.route.CCURoute route = (com.cannontech.database.data.route.CCURoute) val;

        List<RepeaterRoute> repeaterRoutes = route.getRepeaters();
        List<LiteYukonPAObject> assignedRepeaters = new ArrayList<LiteYukonPAObject>();
        List<LiteYukonPAObject> availableRepeaters = new ArrayList<LiteYukonPAObject>();
        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> devices = cache.getAllDevices();
            int repeaterRouteDeviceID;
            for (RepeaterRoute repeaterRoute : repeaterRoutes) {
                for (LiteYukonPAObject liteDevice : devices) {
                    if (liteDevice.getPaoType().isRepeater()) {
                        repeaterRouteDeviceID = repeaterRoute.getDeviceID().intValue();
                        if (repeaterRouteDeviceID == liteDevice.getYukonID()) {
                            assignedRepeaters.add(liteDevice);
                            break;
                        }
                    }
                }
            }
            boolean alreadyAssigned = false;
            for (LiteYukonPAObject liteDevice : devices) {
                alreadyAssigned = false;
                if (liteDevice.getPaoType().isRepeater()) {
                    for (LiteYukonPAObject assignedRepeater : assignedRepeaters) {
                        if (assignedRepeater.getYukonID() == liteDevice.getYukonID())
                            alreadyAssigned = true;
                    }
                    if (!alreadyAssigned) {
                        availableRepeaters.add(liteDevice);
                    }
                }
            }
        }

        LiteYukonPAObject [] assignedRepeatersArray = new LiteYukonPAObject[assignedRepeaters.size()];
        assignedRepeaters.toArray(assignedRepeatersArray);
        getRepeatersAddRemovePanel().rightListSetListData(assignedRepeatersArray);
        
        LiteYukonPAObject [] availableRepeatersArray = new LiteYukonPAObject[availableRepeaters.size()];
        availableRepeaters.toArray(availableRepeatersArray);
        getRepeatersAddRemovePanel().leftListSetListData(availableRepeatersArray);
        
        if (route.getCarrierRoute().getUserLocked().equalsIgnoreCase("Y")) {
            getRepeatersAddRemovePanel().getAddButton().setEnabled(false);
            getRepeatersAddRemovePanel().getRemoveButton().setEnabled(false);
        } else {
            getRepeatersAddRemovePanel().getAddButton().setEnabled(true);
            getRepeatersAddRemovePanel().getRemoveButton().setEnabled(true);
        }
        if (assignedRepeaters.size() > 0) {
            getAdvancedSetupButton().setEnabled(true);
        }
    }

    @Override
    public boolean isInputValid() {
        for (int i = 0; i < getRepeatersAddRemovePanel().rightListGetModel().getSize(); i++) {
            if (((com.cannontech.database.data.lite.LiteYukonPAObject) getRepeatersAddRemovePanel().rightListGetModel()
                                                                                                   .getElementAt(i)).getPaoIdentifier()
                                                                                                                    .getPaoType() == PaoType.REPEATER_850) {
                if (i != getRepeatersAddRemovePanel().rightListGetModel().getSize() - 1) {
                    setErrorString("When present, a repeater 850 MUST be the last repeater in the repeater chain");
                    return false;
                }
            }
        }
        return true;

    }
}
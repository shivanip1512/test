package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.util.EventObject;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.ListModel;
import javax.swing.SwingUtilities;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.AddRemovePanel;
import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.MacroGroupAddRemovePanel;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.yukon.IDatabaseCache;

public class GroupMacroLoadGroupsPanel extends DataInputPanel implements AddRemovePanelListener {
    private int rightListItemIndex = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();
    private boolean rightListDragging = false;
    private MacroGroupAddRemovePanel ivjLoadGroupsAddRemovePanel = null;

    public GroupMacroLoadGroupsPanel() {
        super();
        initialize();
    }

    @Override
    public void addButtonAction_actionPerformed(EventObject newEvent) {
        try {
            if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private boolean isValidLoadGroup(LiteYukonPAObject liteYukonPAObject) {

        if (liteYukonPAObject.getPaoType() != PaoType.LM_GROUP_ECOBEE
            && liteYukonPAObject.getPaoType() != PaoType.LM_GROUP_HONEYWELL
            && liteYukonPAObject.getPaoType() != PaoType.LM_GROUP_NEST
            && liteYukonPAObject.getPaoType() != PaoType.LM_GROUP_ITRON) {
            return true;
        }
        return false;
    }

    private MacroGroupAddRemovePanel getLoadGroupsAddRemovePanel() {
        if (ivjLoadGroupsAddRemovePanel == null) {
            try {
                ivjLoadGroupsAddRemovePanel = new MacroGroupAddRemovePanel();
                ivjLoadGroupsAddRemovePanel.setName("LoadGroupsAddRemovePanel");

                ivjLoadGroupsAddRemovePanel.setMode(AddRemovePanel.TRANSFER_MODE);
                ivjLoadGroupsAddRemovePanel.leftListRemoveAll();
                ivjLoadGroupsAddRemovePanel.rightListRemoveAll();

                IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                Vector<LiteYukonPAObject> availableDevices = null;
                synchronized (cache) {
                    List<LiteYukonPAObject> allDevices = cache.getAllLoadManagement();

                    availableDevices = new Vector<LiteYukonPAObject>();
                    for (LiteYukonPAObject liteYukonPAObject : allDevices) {
                        if (liteYukonPAObject.getPaoType().isLoadGroup()
                            && liteYukonPAObject.getPaoType() != PaoType.MACRO_GROUP) {
                            if (isValidLoadGroup(liteYukonPAObject)) {
                                availableDevices.add(liteYukonPAObject);
                            }
                        }
                    }
                }

                getLoadGroupsAddRemovePanel().leftListSetListData(availableDevices);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjLoadGroupsAddRemovePanel;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    @Override
    public Object getValue(Object val) {

        YukonPAObject macro = null;

        if (val instanceof MultiDBPersistent) {
            macro = (YukonPAObject) MultiDBPersistent.getFirstObjectOfType(YukonPAObject.class, (MultiDBPersistent) val);
        } else if (val instanceof SmartMultiDBPersistent)
            macro = (YukonPAObject) ((SmartMultiDBPersistent) val).getOwnerDBPersistent();

        if (val instanceof YukonPAObject || macro != null) {
            if (macro == null)
                macro = (YukonPAObject) val;

            Integer ownerID = macro.getPAObjectID();

            Vector<GenericMacro> macroGroupVector = new Vector<GenericMacro>();

            for (int i = 0; i < getLoadGroupsAddRemovePanel().rightListGetModel().getSize(); i++) {
                GenericMacro mGroup = new GenericMacro();
                mGroup.setOwnerID(ownerID);
                mGroup.setChildID(new Integer(((LiteYukonPAObject) getLoadGroupsAddRemovePanel().rightListGetModel()
                                                                                                .getElementAt(i)).getYukonID()));
                mGroup.setChildOrder(new Integer(i + 1));
                mGroup.setMacroType(MacroTypes.GROUP);
                macroGroupVector.addElement(mGroup);
            }

            ((MacroGroup) macro).setMacroGroupVector(macroGroupVector);
        }
        return val;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error(exception.getMessage(), exception);
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getLoadGroupsAddRemovePanel().addAddRemovePanelListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("GroupMacroLoadGroupsPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(374, 228);

            java.awt.GridBagConstraints constraintsLoadGroupsAddRemovePanel = new java.awt.GridBagConstraints();
            constraintsLoadGroupsAddRemovePanel.gridx = 0;
            constraintsLoadGroupsAddRemovePanel.gridy = 0;
            constraintsLoadGroupsAddRemovePanel.gridwidth = 2;
            constraintsLoadGroupsAddRemovePanel.gridheight = 2;
            constraintsLoadGroupsAddRemovePanel.fill = GridBagConstraints.BOTH;
            add(getLoadGroupsAddRemovePanel(), constraintsLoadGroupsAddRemovePanel);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public boolean isInputValid() {

        ListModel rightListModel = getLoadGroupsAddRemovePanel().rightListGetModel();
        Set<String> paoTypes = new LinkedHashSet<String>();
        if (rightListModel.getSize() < 1) {
            setErrorString("There needs to be at least 1 load group in this group macro");
            return false;
        }

        for (int i = 0; i < rightListModel.getSize(); i++) {
            LiteYukonPAObject yukonPAObject = (LiteYukonPAObject) rightListModel.getElementAt(i);
            if (yukonPAObject.getPaoType() == PaoType.LM_GROUP_ECOBEE
                || yukonPAObject.getPaoType() == PaoType.LM_GROUP_HONEYWELL
                || yukonPAObject.getPaoType() == PaoType.LM_GROUP_NEST
                || yukonPAObject.getPaoType() == PaoType.LM_GROUP_ITRON) {
                paoTypes.add(yukonPAObject.getPaoType().getDbString());
            }
        }
        if (paoTypes.size() > 0) {
            setErrorString("Remove  " + paoTypes + "  from Assigned list.");
            return false;
        }
        return true;
    }

    @Override
    public void leftListListSelection_valueChanged(EventObject newEvent) {
    }

    @Override
    public void removeButtonAction_actionPerformed(EventObject newEvent) {
        try {
            if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListListSelection_valueChanged(EventObject newEvent) {
    }

    @Override
    public void rightListMouse_mouseClicked(EventObject newEvent) {
    }

    @Override
    public void rightListMouse_mouseEntered(EventObject newEvent) {
    }

    @Override
    public void rightListMouse_mouseExited(EventObject newEvent) {
        try {
            if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) {
                this.routesAddRemovePanel_RightListMouse_mouseExited(newEvent);
            }
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mousePressed(EventObject newEvent) {
        try {
            if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) {
                this.routesAddRemovePanel_RightListMouse_mousePressed(newEvent);
            }
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mouseReleased(EventObject newEvent) {
        try {
            if (newEvent.getSource() == getLoadGroupsAddRemovePanel()) {
                this.routesAddRemovePanel_RightListMouse_mouseReleased(newEvent);
            }
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouseMotion_mouseDragged(EventObject newEvent) {
    }

    public void routesAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
        rightListItemIndex = -1;
        rightListDragging = false;
    }

    public void routesAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
        rightListItemIndex = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();
        rightListDragging = true;
    }

    public void routesAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
        int indexSelected = getLoadGroupsAddRemovePanel().rightListGetSelectedIndex();

        if (rightListDragging && indexSelected != -1 && indexSelected != rightListItemIndex) {

            LiteYukonPAObject itemSelected = null;
            Vector<LiteYukonPAObject> destItems = new Vector<LiteYukonPAObject>(getLoadGroupsAddRemovePanel().rightListGetModel().getSize() + 1);

            for (int i = 0; i < getLoadGroupsAddRemovePanel().rightListGetModel().getSize(); i++) {
                destItems.addElement((LiteYukonPAObject) getLoadGroupsAddRemovePanel().rightListGetModel().getElementAt(i));
            }

            itemSelected = destItems.elementAt(rightListItemIndex);
            destItems.removeElementAt(rightListItemIndex);
            destItems.insertElementAt(itemSelected, indexSelected);
            getLoadGroupsAddRemovePanel().rightListSetListData(destItems);

            getLoadGroupsAddRemovePanel().revalidate();
            getLoadGroupsAddRemovePanel().repaint();

            // reset the values
            rightListItemIndex = -1;
            fireInputUpdate();
        }

        rightListDragging = false;

        return;
    }

    @Override
    public void setValue(Object val) {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        Vector<LiteYukonPAObject> availableGroups = null;
        Vector<LiteYukonPAObject> assignedGroups = null;

        MacroGroupAddRemovePanel macroGroupAddRemovePanel = getLoadGroupsAddRemovePanel();
        MacroGroup macroGroup = (MacroGroup) val;
        DisplayablePaoBase displayableMacroGroup = new DisplayablePaoBase(new PaoIdentifier(macroGroup.getPAObjectID(),
                                                                                            PaoType.MACRO_GROUP),
                                                                          macroGroup.getPAOName());
        macroGroupAddRemovePanel.setCurrentMacroGroup(displayableMacroGroup);

        synchronized (cache) {
            Vector<GenericMacro> macroGroupsVector = ((MacroGroup) val).getMacroGroupVector();
            List<LiteYukonPAObject> allDevices = cache.getAllLoadManagement();
            
            assignedGroups = new Vector<LiteYukonPAObject>();
            int childID;
            for (int i = 0; i < macroGroupsVector.size(); i++) {
                childID = macroGroupsVector.get(i).getChildID().intValue();
                for (int j = 0; j < allDevices.size(); j++) {
                    if (allDevices.get(j).getYukonID() == childID && allDevices.get(j).getLiteID() != ((MacroGroup) val).getPAObjectID().intValue()) {
                        assignedGroups.addElement(allDevices.get(j));
                        break;
                    }
                }
            }

            availableGroups = new Vector<LiteYukonPAObject>();
            for (LiteYukonPAObject liteYukonPAObject : allDevices) {
                if (liteYukonPAObject.getPaoType().isLoadGroup()
                    && liteYukonPAObject.getLiteID() != ((MacroGroup) val).getPAObjectID().intValue()) {
                    if (isValidLoadGroup(liteYukonPAObject)) {
                        availableGroups.addElement(liteYukonPAObject);
                    }
                }
            }
        }

        macroGroupAddRemovePanel.leftListSetListData(availableGroups);
        macroGroupAddRemovePanel.rightListSetListData(assignedGroups);
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getLoadGroupsAddRemovePanel().requestFocus();
            }
        });
    }
}
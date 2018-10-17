package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.ListModel;

import com.cannontech.common.gui.util.AddRemovePanel;
import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.database.data.device.lm.LMProgramDirectBase;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.GearControlMethod;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;
import com.cannontech.database.db.device.lm.LMProgramDirectGroup;
import com.cannontech.loadcontrol.loadgroup.dao.LoadGroupDao;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramListPanel extends DataInputPanel implements AddRemovePanelListener {
    
    private AddRemovePanel ivjAddRemovePanel = null;
    // Temp lists that hold the previous state of the load groups for a given // program
    private List<Object> currentAvailableList;
    private List<Object> currentSelectedList;

    public LMProgramListPanel() {
        super();
        initialize();
    }

    @Override
    public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getAddRemovePanel()) {
                checkForEnrollmentConflicts();
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    private AddRemovePanel getAddRemovePanel() {
        if (ivjAddRemovePanel == null) {
            try {
                ivjAddRemovePanel = new AddRemovePanel();
                ivjAddRemovePanel.setName("AddRemovePanel");

                ivjAddRemovePanel.setMode(AddRemovePanel.TRANSFER_MODE);
                ivjAddRemovePanel.leftListLabelSetText("Available Load Groups");
                ivjAddRemovePanel.rightListLabelSetText("Assigned Load Groups");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAddRemovePanel;
    }

    @Override
    public Object getValue(Object o) {
        LMProgramBase program = (LMProgramBase) o;
        program.getLmProgramStorageVector().removeAllElements();

        for (int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++) {
            LMProgramDirectGroup group = new LMProgramDirectGroup();

            group.setDeviceID(program.getPAObjectID());
            group.setGroupOrder(new Integer(i + 1));
            group.setLmGroupDeviceID(((LiteYukonPAObject) getAddRemovePanel().rightListGetModel().getElementAt(i)).getYukonID());

            program.getLmProgramStorageVector().addElement(group);
        }

        return o;

    }

    private List<Object> getArrayListFromListModel(ListModel<Object> listModel) {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < listModel.getSize(); i++) {
            list.add(listModel.getElementAt(i));
        }

        return list;
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        /* Uncomment the following lines to print uncaught exceptions to stdout */
        com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        com.cannontech.clientutils.CTILogger.error(exception.getMessage(), exception);
    }

    public boolean hasLMGroupPoint() {
        for (int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++) {
            LiteYukonPAObject lmGroup = (LiteYukonPAObject) getAddRemovePanel().rightListGetModel().getElementAt(i);

            // if our element is a LM_GROUP_POINT object, do not add it to the newList
            if (lmGroup.getPaoType() == PaoType.LM_GROUP_POINT) {
                return true;
            }
        }

        return false;
    }

    /**
     * Initializes connections
     */
    private void initConnections() throws java.lang.Exception {
        getAddRemovePanel().addAddRemovePanelListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("VersacomRelayPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(416, 348);

            java.awt.GridBagConstraints constraintsAddRemovePanel = new java.awt.GridBagConstraints();
            constraintsAddRemovePanel.gridx = 1;
            constraintsAddRemovePanel.gridy = 1;
            constraintsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsAddRemovePanel.weightx = 1.0;
            constraintsAddRemovePanel.weighty = 1.0;
            constraintsAddRemovePanel.ipadx = 159;
            constraintsAddRemovePanel.ipady = 174;
            constraintsAddRemovePanel.insets = new java.awt.Insets(25, 2, 38, 4);
            add(getAddRemovePanel(), constraintsAddRemovePanel);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void initLeftList(boolean hideLMGroupPoints, PaoType programType) {
        IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> groups = cache.getAllLoadManagement();
            
            Vector<LiteYukonPAObject> newList = new Vector<LiteYukonPAObject>(getAddRemovePanel().leftListGetModel().getSize());

            for (LiteYukonPAObject group : groups) {
                PaoType paoType = group.getPaoType();
                if (paoType.isLoadGroup() && (hideLMGroupPoints ? paoType != PaoType.LM_GROUP_POINT : true)) {

                    boolean isSepProgram = programType == PaoType.LM_SEP_PROGRAM;
                    boolean isEcobeeProgram = programType == PaoType.LM_ECOBEE_PROGRAM;
                    boolean isHoneywellProgram = programType == PaoType.LM_HONEYWELL_PROGRAM;
                    // SEP compatible groups are shown for SEP programs and
                    // hidden for all others
                    // ecobee compatible groups are shown for ecobee programs
                    // and hidden for all others
                    // honeywell compatible groups are shown for honeywell programs
                    // and hidden for all others
                    if (isSepProgram && isGroupSepCompatible(paoType)) {
                        newList.addElement(group);
                    } else if ((!isSepProgram && !isGroupSepCompatible(paoType))
                        && (!isEcobeeProgram && !isGroupEcobeeCompatible(paoType))
                        && (!isHoneywellProgram && !isGroupHoneywellCompatible(paoType))) {
                        newList.addElement(group);
                    } else if (isEcobeeProgram && isGroupEcobeeCompatible(paoType)) {
                        newList.addElement(group);
                    } else if (isHoneywellProgram && isGroupHoneywellCompatible(paoType)) {
                        newList.addElement(group);
                    }
                }
            }

            getAddRemovePanel().leftListSetListData(newList);
            currentAvailableList = new ArrayList<Object>(newList);
            currentSelectedList = new ArrayList<Object>();

        }
    }

    private boolean isGroupSepCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_DIGI_SEP;
    }

    private boolean isGroupEcobeeCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_ECOBEE;
    }
    
    private boolean isGroupHoneywellCompatible(PaoType groupType) {
        return groupType == PaoType.LM_GROUP_HONEYWELL;
    }

    @Override
    public boolean isInputValid() {
        if (getAddRemovePanel().rightListGetModel().getSize() <= 0) {
            setErrorString("At least 1 load group must present in this current program.");
            return false;
        }

        if (!checkForEnrollmentConflicts()) {
            return false;
        }

        return true;
    }

    /**
     * Checks to see if the added/removed load groups
     */
    private boolean checkForEnrollmentConflicts() {
        // Check available load group list
        List<Object> availableListDiff = getArrayListFromListModel(getAddRemovePanel().getLeftList().getModel());
        availableListDiff.removeAll(currentAvailableList);

        if (!checkLoadGroupListForEnrollmentIssues(availableListDiff)) {
            getAddRemovePanel().getLeftList().setListData(currentAvailableList.toArray());
            getAddRemovePanel().getRightList().setListData(currentSelectedList.toArray());
            return false;
        }

        // Check selected load group list
        List<Object> selectionListDiff = getArrayListFromListModel(getAddRemovePanel().getRightList().getModel());
        selectionListDiff.removeAll(currentSelectedList);

        if (!checkLoadGroupListForEnrollmentIssues(selectionListDiff)) {
            getAddRemovePanel().getLeftList().setListData(currentAvailableList.toArray());
            getAddRemovePanel().getRightList().setListData(currentSelectedList.toArray());
            return false;
        }

        // Update the temp lists
        currentAvailableList = getArrayListFromListModel(getAddRemovePanel().getLeftList().getModel());
        currentSelectedList = getArrayListFromListModel(getAddRemovePanel().getRightList().getModel());

        return true;
    }

    private boolean checkLoadGroupListForEnrollmentIssues(List<Object> lmLoadGroupPAOs) {
        for (Object temp : lmLoadGroupPAOs) {
            LiteYukonPAObject lmProgPAO = (LiteYukonPAObject) temp;
            int loadGroupId = lmProgPAO.getLiteID();
            LoadGroupDao loadGroupDao = YukonSpringHook.getBean("loadGroupDao", LoadGroupDao.class);
            if (loadGroupDao.isLoadGroupInUse(loadGroupId)) {
                setErrorString("The load group you are trying to move is currently being used in customer enrollment. (" + lmProgPAO.getPaoName() + ")");
                JOptionPane.showMessageDialog(null,
                                              getErrorString(),
                                              "Illegal Operation Exception",
                                              JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return true;
    }

    @Override
    public void leftListListSelection_valueChanged(java.util.EventObject newEvent) {
    }

    @Override
    public void removeButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getAddRemovePanel()) {
                checkForEnrollmentConflicts();
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
    }

    @Override
    public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
    }

    @Override
    public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
    }

    @Override
    public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
    }

    @Override
    public void setValue(Object o) {
        LMProgramDirectBase dirProg = (LMProgramDirectBase) o;
        PaoType paoType = dirProg.getPaoType();

        /**** special case for the LM_GROUP_POINT group type ****/
        boolean isLatching = false;
        for (int i = 0; i < dirProg.getLmProgramDirectGearVector().size(); i++) {
            LMProgramDirectGear gear = dirProg.getLmProgramDirectGearVector().get(i);

            // we only can add LM_GROUP_POINTS group types to a program with a LATCHING gear
            if (gear.getControlMethod() == GearControlMethod.Latching) {
                isLatching = true;
                break;
            }
        }

        initLeftList(!isLatching, paoType);
        /**** END of special case for the LM_GROUP_POINT group type ****/

        // init storage that will contain all possible items
        Vector<LiteYukonPAObject> allItems = new Vector<LiteYukonPAObject>(getAddRemovePanel().leftListGetModel().getSize());
        for (int i = 0; i < getAddRemovePanel().leftListGetModel().getSize(); i++)
            allItems.add((LiteYukonPAObject)getAddRemovePanel().leftListGetModel().getElementAt(i));

        Vector<LiteYukonPAObject> usedItems = new Vector<LiteYukonPAObject>(getAddRemovePanel().leftListGetModel().getSize());

        for (int i = 0; i < dirProg.getLmProgramStorageVector().size(); i++) {
            LMProgramDirectGroup group = (LMProgramDirectGroup) dirProg.getLmProgramStorageVector().get(i);

            for (int j = 0; j < allItems.size(); j++) {
                if (allItems.get(j).getYukonID() == group.getLmGroupDeviceID().intValue()) {
                    usedItems.add(allItems.get(j));
                    allItems.removeElementAt(j);
                    break;
                }

            }
        }

        getAddRemovePanel().leftListSetListData(allItems);
        currentAvailableList = new ArrayList<Object>(allItems);

        getAddRemovePanel().rightListSetListData(usedItems);
        currentSelectedList = new ArrayList<Object>(usedItems);

    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getAddRemovePanel().requestFocus();
            }
        });
    }
}
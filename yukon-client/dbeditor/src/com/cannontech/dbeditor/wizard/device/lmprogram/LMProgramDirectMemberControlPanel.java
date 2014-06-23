package com.cannontech.dbeditor.wizard.device.lmprogram;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;

import com.cannontech.common.gui.util.AddRemovePanel;
import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.lm.LMProgramDirectBase;
import com.cannontech.database.data.lite.LiteLMPAOExclusion;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.pao.PAOExclusion;
import com.cannontech.yukon.IDatabaseCache;

public class LMProgramDirectMemberControlPanel extends DataInputPanel implements AddRemovePanelListener {

    private AddRemovePanel ivjAddRemovePanel = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private JCheckBox ivjJCheckBoxActivateMaster = null;

    class IvjEventHandler implements AddRemovePanelListener, ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (e.getSource() == LMProgramDirectMemberControlPanel.this.getJCheckBoxActivateMaster()) {
                    LMProgramDirectMemberControlPanel.this.jCheckBoxActivateMaster_ActionPerformed(e);
                    fireInputUpdate();
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        };

        @Override
        public void addButtonAction_actionPerformed(EventObject newEvent) {
            try {
                if (newEvent.getSource() == LMProgramDirectMemberControlPanel.this.getAddRemovePanel()) {
                    LMProgramDirectMemberControlPanel.this.fireInputUpdate();
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        };

        @Override
        public void leftListListSelection_valueChanged(EventObject newEvent) {
        };

        @Override
        public void removeButtonAction_actionPerformed(EventObject newEvent) {
            try {
                if (newEvent.getSource() == LMProgramDirectMemberControlPanel.this.getAddRemovePanel()) {
                    LMProgramDirectMemberControlPanel.this.fireInputUpdate();
                }
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        };

        @Override
        public void rightListListSelection_valueChanged(EventObject newEvent) {
        };

        @Override
        public void rightListMouse_mouseClicked(EventObject newEvent) {
        };

        @Override
        public void rightListMouse_mouseEntered(EventObject newEvent) {
        };

        @Override
        public void rightListMouse_mouseExited(EventObject newEvent) {
        };

        @Override
        public void rightListMouse_mousePressed(EventObject newEvent) {
        };

        @Override
        public void rightListMouse_mouseReleased(EventObject newEvent) {
        };

        @Override
        public void rightListMouseMotion_mouseDragged(EventObject newEvent) {
        };
    };

    public LMProgramDirectMemberControlPanel() {
        super();
        initialize();
    }

    @Override
    public void addButtonAction_actionPerformed(EventObject newEvent) {
        try {
            if (newEvent.getSource() == getAddRemovePanel()) {
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

                ivjAddRemovePanel.leftListLabelSetText("Available Subordinates");
                ivjAddRemovePanel.rightListLabelSetText("Assigned Subordinates");
                ivjAddRemovePanel.setAddRemoveEnabled(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjAddRemovePanel;
    }

    private JCheckBox getJCheckBoxActivateMaster() {
        if (ivjJCheckBoxActivateMaster == null) {
            try {
                ivjJCheckBoxActivateMaster = new JCheckBox();
                ivjJCheckBoxActivateMaster.setName("JCheckBoxActivateMaster");
                ivjJCheckBoxActivateMaster.setToolTipText("Check to allow this program to become a master program.  \nSubordinate programs can then be assigned to this program.");
                ivjJCheckBoxActivateMaster.setText("Allow Member Control");
                ivjJCheckBoxActivateMaster.setComponentOrientation(java.awt.ComponentOrientation.LEFT_TO_RIGHT);
                ivjJCheckBoxActivateMaster.setFont(new java.awt.Font("Arial", 1, 12));
                ivjJCheckBoxActivateMaster.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
                ivjJCheckBoxActivateMaster.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                ivjJCheckBoxActivateMaster.setSelected(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJCheckBoxActivateMaster;
    }

    @Override
    public Object getValue(Object o) {
        LMProgramDirectBase program = (LMProgramDirectBase) o;

        program.getPAOExclusionVector().removeAllElements();
        if (getJCheckBoxActivateMaster().isSelected()) {
            for (int i = 0; i < getAddRemovePanel().rightListGetModel().getSize(); i++) {
                PAOExclusion subordinateProg = new PAOExclusion();

                subordinateProg.setPaoID(program.getPAObjectID());
                subordinateProg.setExcludedPaoID(((LiteYukonPAObject) getAddRemovePanel().rightListGetModel().getElementAt(i)).getLiteID());

                // this is very important: server needs funcID set to this or it won't recognize the subordinate
                subordinateProg.setFuncName(CtiUtilities.LM_SUBORDINATION_INFO);
                subordinateProg.setFunctionID(CtiUtilities.LM_SUBORDINATION_FUNC_ID);

                program.getPAOExclusionVector().addElement(subordinateProg);
            }
        }
        return o;

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
        getAddRemovePanel().addAddRemovePanelListener(ivjEventHandler);
        getJCheckBoxActivateMaster().addActionListener(ivjEventHandler);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("LMProgramEnergyExchangeCustomerListPanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(416, 348);

            java.awt.GridBagConstraints constraintsAddRemovePanel = new java.awt.GridBagConstraints();
            constraintsAddRemovePanel.gridx = 1;
            constraintsAddRemovePanel.gridy = 2;
            constraintsAddRemovePanel.fill = java.awt.GridBagConstraints.BOTH;
            constraintsAddRemovePanel.weightx = 1.0;
            constraintsAddRemovePanel.weighty = 1.0;
            constraintsAddRemovePanel.ipady = 16;
            constraintsAddRemovePanel.insets = new java.awt.Insets(4, 2, 7, 4);
            add(getAddRemovePanel(), constraintsAddRemovePanel);

            java.awt.GridBagConstraints constraintsJCheckBoxActivateMaster = new java.awt.GridBagConstraints();
            constraintsJCheckBoxActivateMaster.gridx = 1;
            constraintsJCheckBoxActivateMaster.gridy = 1;
            constraintsJCheckBoxActivateMaster.ipadx = 259;
            constraintsJCheckBoxActivateMaster.insets = new java.awt.Insets(11, 2, 3, 5);
            add(getJCheckBoxActivateMaster(), constraintsJCheckBoxActivateMaster);
            initConnections();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
        initializeAddPanel();
    }

    private void initializeAddPanel() {
        getAddRemovePanel().setMode(AddRemovePanel.TRANSFER_MODE);
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {

            List<LiteYukonPAObject> availableSubs = cache.getAllLMPrograms();
            List<LiteLMPAOExclusion> currentlyExcluded = cache.getAllLMPAOExclusions();

            Vector<LiteYukonPAObject> lmSubordinates = new Vector<LiteYukonPAObject>();

            for (LiteYukonPAObject liteYukonPAObject : availableSubs) {
                Integer theID = new Integer(liteYukonPAObject.getLiteID());
                // makes sure it is a direct program and it is not already a master
                if (liteYukonPAObject.getPaoType().isDirectProgram() && !(isMasterProgram(theID.intValue(), currentlyExcluded)))
                    lmSubordinates.addElement(liteYukonPAObject);
            }

            getAddRemovePanel().leftListSetListData(lmSubordinates);
        }
    }

    public void jCheckBoxActivateMaster_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        getAddRemovePanel().setAddRemoveEnabled(getJCheckBoxActivateMaster().isSelected());
    }

    @Override
    public void leftListListSelection_valueChanged(EventObject newEvent) {
    }

    @Override
    public void removeButtonAction_actionPerformed(EventObject newEvent) {
        try {
            if (newEvent.getSource() == getAddRemovePanel()) {
                fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
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
    }

    @Override
    public void rightListMouse_mousePressed(EventObject newEvent) {
    }

    @Override
    public void rightListMouse_mouseReleased(EventObject newEvent) {
    }

    @Override
    public void rightListMouseMotion_mouseDragged(EventObject newEvent) {
    }

    @Override
    public void setValue(Object o) {
        LMProgramDirectBase program = (LMProgramDirectBase) o;

        if (program.getPAOExclusionVector().size() > 0) {
            getJCheckBoxActivateMaster().setSelected(true);
            getAddRemovePanel().setAddRemoveEnabled(true);
        } else {
            getJCheckBoxActivateMaster().setSelected(false);
            getAddRemovePanel().setAddRemoveEnabled(false);
        }

        // init storage that will contain exclusion (member control) information
        Vector<LiteYukonPAObject> allSubordinates = new Vector<LiteYukonPAObject>(getAddRemovePanel().leftListGetModel().getSize());

        for (int i = 0; i < getAddRemovePanel().leftListGetModel().getSize(); i++) {
            // make sure this program itself isn't showing up as an available subordinate
            if (program.getPAObjectID().intValue() != ((LiteYukonPAObject) getAddRemovePanel().leftListGetModel().getElementAt(i)).getLiteID())
                allSubordinates.add((LiteYukonPAObject)getAddRemovePanel().leftListGetModel().getElementAt(i));
        }

        Vector<LiteYukonPAObject> assignedSubordinates = new Vector<LiteYukonPAObject>(getAddRemovePanel().leftListGetModel().getSize());

        for (int j = 0; j < program.getPAOExclusionVector().size(); j++) {
            PAOExclusion subordinateProg = program.getPAOExclusionVector().elementAt(j);

            for (int x = 0; x < allSubordinates.size(); x++) {
                if (allSubordinates.get(x).getLiteID() == subordinateProg.getExcludedPaoID().intValue()) {
                    assignedSubordinates.add(allSubordinates.get(x));
                    allSubordinates.removeElementAt(x);
                    break;
                }
            }
        }

        getAddRemovePanel().leftListSetListData(allSubordinates);
        getAddRemovePanel().rightListSetListData(assignedSubordinates);
    }

    public boolean isMasterProgram(int theID, List<LiteLMPAOExclusion> theList) {
        if (theList != null) {
            for (LiteLMPAOExclusion liteLMPAOExclusion : theList) {
                if (theID == liteLMPAOExclusion.getMasterPaoID()) {
                    return true;
                }
            }
        }
        return false;
    }
}
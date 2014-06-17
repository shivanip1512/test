package com.cannontech.dbeditor.editor.route;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import com.cannontech.common.gui.util.AddRemovePanel;
import com.cannontech.common.gui.util.AddRemovePanelListener;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class MacroRouteEditorPanel extends DataInputPanel implements AddRemovePanelListener, CaretListener {
    private AddRemovePanel ivjRoutesAddRemovePanel = null;
    private JLabel ivjNameLabel = null;
    private JTextField ivjNameTextField = null;
    private int rightListItemIndex = getRoutesAddRemovePanel().rightListGetSelectedIndex();
    private boolean rightListDragging = false;
    private PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
    private DBPersistentDao persistantDao = YukonSpringHook.getBean(DBPersistentDao.class);

    public MacroRouteEditorPanel() {
        super();
        initialize();
    }

    @Override
    public void addButtonAction_actionPerformed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getRoutesAddRemovePanel()) {
                this.fireInputUpdate();
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        try {
            if (e.getSource() == getNameTextField()) {
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

    private JLabel getNameLabel() {
        if (ivjNameLabel == null) {
            try {
                ivjNameLabel = new JLabel();
                ivjNameLabel.setName("NameLabel");
                ivjNameLabel.setText("Route Macro Name:");
                ivjNameLabel.setMaximumSize(new java.awt.Dimension(125, 16));
                ivjNameLabel.setPreferredSize(new java.awt.Dimension(125, 16));
                ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjNameLabel.setMinimumSize(new java.awt.Dimension(125, 16));
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
                ivjNameTextField.setMaximumSize(new java.awt.Dimension(2147483647, 24));
                ivjNameTextField.setColumns(20);
                ivjNameTextField.setPreferredSize(new java.awt.Dimension(132, 24));
                ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
                ivjNameTextField.setMinimumSize(new java.awt.Dimension(132, 24));
                ivjNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_ROUTE_NAME_LENGTH));
                ivjNameTextField.setDocument(new TextFieldDocument(TextFieldDocument.MAX_DEVICE_NAME_LENGTH, PaoUtils.ILLEGAL_NAME_CHARS));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjNameTextField;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private AddRemovePanel getRoutesAddRemovePanel() {
        if (ivjRoutesAddRemovePanel == null) {
            try {
                ivjRoutesAddRemovePanel = new AddRemovePanel();
                ivjRoutesAddRemovePanel.setName("RoutesAddRemovePanel");
                ivjRoutesAddRemovePanel.setMode(AddRemovePanel.COPY_MODE);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRoutesAddRemovePanel;
    }

    @Override
    public Object getValue(Object val) {
        MacroRoute macroRoute = ((MacroRoute) val);
        macroRoute.setRouteName(getNameTextField().getText());

        Integer routeID = macroRoute.getRouteID();
        Vector<com.cannontech.database.db.route.MacroRoute> macroRouteVector = macroRoute.getMacroRouteVector();
        macroRouteVector.removeAllElements();

        ListModel rightListModel = getRoutesAddRemovePanel().rightListGetModel();
        for (int i = 0; i < rightListModel.getSize(); i++) {
            com.cannontech.database.db.route.MacroRoute mRoute = new com.cannontech.database.db.route.MacroRoute();
            mRoute.setRouteID(routeID);
            mRoute.setSingleRouteID(new Integer(((LiteYukonPAObject) rightListModel.getElementAt(i)).getYukonID()));
            mRoute.setRouteOrder(new Integer(i + 1));

            macroRouteVector.addElement(mRoute);
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
        getRoutesAddRemovePanel().addAddRemovePanelListener(this);
        getNameTextField().addCaretListener(this);
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("RouteMacroCommunicationRoutes");
            setLayout(new java.awt.GridBagLayout());
            setSize(487, 354);

            GridBagConstraints constraintsRoutesAddRemovePanel = new GridBagConstraints();
            constraintsRoutesAddRemovePanel.gridx = 1;
            constraintsRoutesAddRemovePanel.gridy = 2;
            constraintsRoutesAddRemovePanel.gridwidth = 2;
            constraintsRoutesAddRemovePanel.insets = new Insets(5, 5, 5, 5);
            constraintsRoutesAddRemovePanel.fill = GridBagConstraints.HORIZONTAL;
            add(getRoutesAddRemovePanel(), constraintsRoutesAddRemovePanel);

            GridBagConstraints constraintsNameLabel = new GridBagConstraints();
            constraintsNameLabel.gridx = 1;
            constraintsNameLabel.gridy = 0;
            constraintsNameLabel.anchor = GridBagConstraints.WEST;
            constraintsNameLabel.insets = new Insets(5, 5, 5, 5);
            add(getNameLabel(), constraintsNameLabel);

            GridBagConstraints constraintsNameTextField = new GridBagConstraints();
            constraintsNameTextField.gridx = 1;
            constraintsNameTextField.gridy = 1;
            constraintsNameTextField.weightx = 1;
            constraintsNameTextField.anchor = GridBagConstraints.WEST;
            constraintsNameTextField.fill = GridBagConstraints.HORIZONTAL;
            constraintsNameTextField.insets = new Insets(5, 5, 5, 5);
            add(getNameTextField(), constraintsNameTextField);
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
            if (newEvent.getSource() == getRoutesAddRemovePanel()) {
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
            if (newEvent.getSource() == getRoutesAddRemovePanel()) {
                this.routesAddRemovePanel_RightListMouse_mouseExited(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mousePressed(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getRoutesAddRemovePanel()) {
                this.routesAddRemovePanel_RightListMouse_mousePressed(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouse_mouseReleased(java.util.EventObject newEvent) {
        try {
            if (newEvent.getSource() == getRoutesAddRemovePanel()) {
                this.routesAddRemovePanel_RightListMouse_mouseReleased(newEvent);
            }
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    @Override
    public void rightListMouseMotion_mouseDragged(java.util.EventObject newEvent) {
    }

    public void routesAddRemovePanel_RightListMouse_mouseExited(java.util.EventObject newEvent) {
        rightListItemIndex = -1;
        rightListDragging = false;
        return;
    }

    public void routesAddRemovePanel_RightListMouse_mousePressed(java.util.EventObject newEvent) {
        rightListItemIndex = getRoutesAddRemovePanel().rightListGetSelectedIndex();
        rightListDragging = true;
        return;
    }

    public void routesAddRemovePanel_RightListMouse_mouseReleased(java.util.EventObject newEvent) {
        int indexSelected = getRoutesAddRemovePanel().rightListGetSelectedIndex();

        if (rightListDragging && indexSelected != -1 && indexSelected != rightListItemIndex) {

            Object itemSelected = new Object();
            Vector destItems = new Vector(getRoutesAddRemovePanel().rightListGetModel().getSize() + 1);

            for (int i = 0; i < getRoutesAddRemovePanel().rightListGetModel().getSize(); i++)
                destItems.addElement(getRoutesAddRemovePanel().rightListGetModel().getElementAt(i));

            itemSelected = destItems.elementAt(rightListItemIndex);
            destItems.removeElementAt(rightListItemIndex);
            destItems.insertElementAt(itemSelected, indexSelected);
            getRoutesAddRemovePanel().rightListSetListData(destItems);

            getRoutesAddRemovePanel().revalidate();
            getRoutesAddRemovePanel().repaint();

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
        Vector<LiteYukonPAObject> availableRoutes = null;
        Vector<LiteYukonPAObject> assignedRoutes = null;
        synchronized (cache) {
            Vector<com.cannontech.database.db.route.MacroRoute> macroRoutesVector = ((MacroRoute) val).getMacroRouteVector();
            List<LiteYukonPAObject> allRoutes = cache.getAllRoutes();

            assignedRoutes = new Vector<LiteYukonPAObject>();
            int singleRouteID;
            for (int i = 0; i < macroRoutesVector.size(); i++) {
                singleRouteID = macroRoutesVector.get(i).getSingleRouteID().intValue();
                for (int j = 0; j < allRoutes.size(); j++) {
                    if (allRoutes.get(j).getYukonID() == singleRouteID) {
                        assignedRoutes.addElement(allRoutes.get(j));
                        break;
                    }
                }
            }

            availableRoutes = new Vector<LiteYukonPAObject>();
            for (LiteYukonPAObject liteRoute : allRoutes) {
                if (liteRoute.getPaoType() != PaoType.ROUTE_MACRO) {
                    availableRoutes.addElement(liteRoute);
                }
            }
        }

        AddRemovePanel routesPanel = getRoutesAddRemovePanel();
        routesPanel.leftListRemoveAll();
        routesPanel.rightListRemoveAll();

        routesPanel.leftListSetListData(availableRoutes);
        routesPanel.rightListSetListData(assignedRoutes);

        getNameTextField().setText(((com.cannontech.database.data.route.MacroRoute) val).getRouteName());
    }

    @Override
    public boolean isInputValid() {

        // This code is going to find all RPT 850's that are not at the end of the list and move them there.
        // The user will be warned if any routes are moved, and a validation error will be returned.
        boolean firstNon850Found = false;
        boolean listModified = false;

        LiteYukonPAObject itemSelected;
        ListModel rightListModel = getRoutesAddRemovePanel().rightListGetModel();
        java.util.Vector<LiteYukonPAObject> newList = new java.util.Vector<LiteYukonPAObject>(rightListModel.getSize());

        for (int i = 0; i < rightListModel.getSize(); i++) {
            if (rightListModel.getElementAt(i) instanceof LiteYukonPAObject)
                newList.addElement((LiteYukonPAObject) rightListModel.getElementAt(i));
        }

        for (int i = rightListModel.getSize() - 1; i >= 0; i--) {
            LiteYukonPAObject device = paoDao.getLiteYukonPAO((((LiteYukonPAObject) rightListModel.getElementAt(i)).getPaoIdentifier().getPaoId()));
            YukonPAObject yukonPaobject = (YukonPAObject) persistantDao.retrieveDBPersistent(device);

            // if it is a CCU Route, check if it has a RPT 850.
            boolean routeHas850 = false;
            if (yukonPaobject instanceof CCURoute) {
                List<RepeaterRoute> repeaterRoutes = ((CCURoute) yukonPaobject).getRepeaters();

                List<Integer> paoIds = Lists.newArrayList();

                for (RepeaterRoute route : repeaterRoutes) {
                    paoIds.add(route.getDeviceID());
                }

                List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(paoIds);

                for (PaoIdentifier identifier : paoIdentifiers) {
                    if (identifier.getPaoType() == PaoType.REPEATER_850) {
                        // We have a repeater 850, this one needs to be at the end of the list!
                        routeHas850 = true;
                        break;
                    }
                }
            }

            if (!routeHas850) {
                firstNon850Found = true;
            }

            // if we already found the first non 850, and we found another who has it, it must be moved!
            if (firstNon850Found && routeHas850) {
                itemSelected = newList.elementAt(i);
                newList.removeElementAt(i);
                newList.add(itemSelected);
                listModified = true;
            }

        }

        if (listModified) {
            getRoutesAddRemovePanel().rightListSetListData(newList);

            getRoutesAddRemovePanel().revalidate();
            getRoutesAddRemovePanel().repaint();

            // reset the values
            rightListItemIndex = -1;
            fireInputUpdate();
            setErrorString("Macro routes containing Repeater 850's automatically moved to end of macro list. Route was not saved.");
            return false;
        }
        return true;
    }
}
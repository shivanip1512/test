package com.cannontech.dbeditor.wizard.device;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.CarrierBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.MCTBase;
import com.cannontech.database.data.device.Repeater850;
import com.cannontech.database.data.device.RepeaterBase;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.route.CCURoute;
import com.cannontech.database.data.route.MacroRoute;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.data.route.RouteFactory;
import com.cannontech.database.data.route.RouteRole;
import com.cannontech.database.data.route.RouteUsageHelper;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.route.RepeaterRoute;
import com.cannontech.dbeditor.editor.regenerate.RoleConflictDialog;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.Lists;

public class DeviceRoutePanel extends DataInputPanel {
    private JLabel ivjRouteLabel = null;
    private JComboBox<LiteYukonPAObject> ivjRouteComboBox = null;
    private Frame owner = SwingUtil.getParentFrame(this);

    public DeviceRoutePanel() {
        super();
        initialize();
    }

    public void addRouteButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) throws Throwable {
    }

    public void editRouteButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
        return;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(350, 200);
    }

    private JComboBox<LiteYukonPAObject> getRouteComboBox() {
        if (ivjRouteComboBox == null) {
            try {
                ivjRouteComboBox = new JComboBox<LiteYukonPAObject>();
                ivjRouteComboBox.setName("RouteComboBox");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRouteComboBox;
    }

    private JLabel getRouteLabel() {
        if (ivjRouteLabel == null) {
            try {
                ivjRouteLabel = new JLabel();
                ivjRouteLabel.setName("RouteLabel");
                ivjRouteLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjRouteLabel.setText("Select the route used with this device:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjRouteLabel;
    }

    @Override
    public boolean isInputValid() {
        return checkRoutesExist();
    };

    private boolean checkRoutesExist() {

        if (getRouteComboBox().getItemCount() < 1) {

            JOptionPane.showConfirmDialog(this,
                                          "Unable to save device.\n\nNo routes are setup.\n\n",
                                          "No Routes Setup",
                                          JOptionPane.DEFAULT_OPTION,
                                          JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    @Override
    public Object getValue(Object val) throws EditorInputValidationException {

        Object value = null;
        if (val instanceof SmartMultiDBPersistent) {
            value = ((SmartMultiDBPersistent) val).getOwnerDBPersistent();
        } else if (val instanceof MultiDBPersistent && MultiDBPersistent.getFirstObjectOfType(RepeaterBase.class, (MultiDBPersistent) val) != null) {
            value = MultiDBPersistent.getFirstObjectOfType(RepeaterBase.class, (MultiDBPersistent) val);
        } else {
            value = val;
        }

        if (value instanceof MCTBase) {
            // The default value for DeviceLoadProfile.loadProfileDemandRate was changed to 3600, therefore,
            // all "legacy" mcts should be set to 300. This is just to keep the DBEditor code working the same for these meter types.
            // New bulk operations will not have this check it it, we're assuming these legacy devices are not being added using those tools.
            PaoType deviceType = ((MCTBase) value).getPaoType();
            if (DeviceTypesFuncs.isMCT2XXORMCT310XX(deviceType) || DeviceTypesFuncs.isMCT3xx(deviceType)) {
                ((MCTBase) value).getDeviceLoadProfile().setLoadProfileDemandRate(new Integer(300));
            }
        }

        ((CarrierBase) value).getDeviceRoutes().setRouteID(new Integer(((LiteYukonPAObject) getRouteComboBox().getSelectedItem()).getYukonID()));

        DBPersistent chosenRoute = LiteFactory.createDBPersistent((LiteBase) getRouteComboBox().getSelectedItem());

        try {
            chosenRoute = Transaction.createTransaction(Transaction.RETRIEVE, chosenRoute).execute();

        } catch (TransactionException t) {
            CTILogger.error(t.getMessage(), t);
        }

        if (value instanceof RepeaterBase) {
            MultiDBPersistent newVal = new MultiDBPersistent();
            newVal.getDBPersistentVector().add((DBPersistent) value);

            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
            ((DeviceBase) value).setDeviceID(paoDao.getNextPaoId());

            // Automatically add default points
            PaoDefinitionService paoDefinitionService = YukonSpringHook.getBean(PaoDefinitionService.class);
            DeviceDao deviceDao = YukonSpringHook.getBean(DeviceDao.class);
            SimpleDevice yukonDevice = deviceDao.getYukonDeviceForDevice((DeviceBase) value);
            List<PointBase> defaultPoints = paoDefinitionService.createDefaultPointsForPao(yukonDevice);
            for (PointBase point : defaultPoints) {
                newVal.getDBPersistentVector().add(point);
            }

            // if the chosen route is a macro route then the generated route will be copied from the first route in the macro
            if (chosenRoute instanceof MacroRoute) {
                if (((MacroRoute) chosenRoute).getMacroRouteVector().size() > 0) {
                    com.cannontech.database.db.route.MacroRoute firstRoute = ((MacroRoute) chosenRoute).getMacroRouteVector().firstElement();

                    IDatabaseCache cache = DefaultDatabaseCache.getInstance();
                    synchronized (cache) {
                        List<LiteYukonPAObject> routes = cache.getAllRoutes();

                        for (int i = 0; i < routes.size(); i++) {

                            if (firstRoute.getSingleRouteID().intValue() == ((LiteBase) routes.get(i)).getLiteID()) {
                                chosenRoute = LiteFactory.createDBPersistent(routes.get(i));
                                break;
                            }
                        }

                        try {
                            chosenRoute = Transaction.createTransaction(Transaction.RETRIEVE, chosenRoute).execute();

                        } catch (TransactionException t) {
                            CTILogger.error(t.getMessage(), t);
                        }
                    }
                }
            }

            // Alert if we are creating a RPT850
            if (value instanceof Repeater850) {
                Object[] o = {
                        "The RPT-850 is designed for use as a last hop repeater to\n" + 
                                "improve communication reliability with up to ten end points (MCT's).\n" + 
                                "Functional limitations are in place to ensure it is only implemented\n" + 
                                "as the last repeater in any route.  To avoid nonessential communications,\n" + 
                                "the operator is encouraged to limit the number of end point devices\n" + 
                                "targeted by this repeater to a maximum of ten.",
                        new JCheckBox("I Understand") };

                JOptionPane.showMessageDialog(this, o, "Implementation Note", JOptionPane.INFORMATION_MESSAGE);

                if (!((JCheckBox) o[1]).isSelected()) {
                    throw new EditorInputValidationException("");
                }
            }

            // create new route to be added - copy from the chosen route and add new repeater to it
            // A route is automatically added to each transmitter
            if (chosenRoute instanceof CCURoute) {
                RouteBase route = RouteFactory.createRoute(PaoType.ROUTE_CCU);

                route.setRouteName(((DeviceBase) value).getPAOName());

                // set default values for route tables possibly using same
                // values in chosen route
                route.setDeviceID(((RouteBase) chosenRoute).getDeviceID());
                ((CCURoute) route).getCarrierRoute().setBusNumber(((CCURoute) chosenRoute).getCarrierRoute().getBusNumber());
                ((CCURoute) route).setRepeaters(((CCURoute) chosenRoute).getRepeaters());

                // Check to be sure this repeater is ok, it must not have a RPT
                // 850
                List<RepeaterRoute> repeaterRoutes = ((CCURoute) chosenRoute).getRepeaters();

                List<Integer> paoIds = Lists.newArrayList();

                for (RepeaterRoute routeElement : repeaterRoutes) {
                    paoIds.add(routeElement.getDeviceID());
                }

                List<PaoIdentifier> paoIdentifiers = paoDao.getPaoIdentifiersForPaoIds(paoIds);

                for (PaoIdentifier paoIdentifier : paoIdentifiers) {
                    if (paoIdentifier.getPaoType() == PaoType.REPEATER_850) {
                        // The old route has a RPT850, this is not allowed
                        throw new EditorInputValidationException("New routes can not be based on routes with Repeater 850's");
                    }
                }

                // add the new repeater to this route
                RepeaterRoute rr = new RepeaterRoute(route.getRouteID(),
                                                     ((DeviceBase) value).getPAObjectID(),
                                                     new Integer(7),
                                                     new Integer(((CCURoute) chosenRoute).getRepeaters().size() + 1));

                if (((CCURoute) route).getRepeaters().size() >= 7)
                    ((CCURoute) route).setRepeaters(new ArrayList<RepeaterRoute>());

                ((CCURoute) route).getRepeaters().add(rr);

                if (value instanceof Repeater850) {
                    route.setDefaultRoute(CtiUtilities.getFalseCharacter().toString());
                } else {
                    route.setDefaultRoute(CtiUtilities.getTrueCharacter().toString());
                }

                RouteUsageHelper routeBoss = new RouteUsageHelper();
                RouteRole role = routeBoss.assignRouteLocation((CCURoute) route, null, null);
                if (role.getDuplicates().isEmpty()) {
                    ((CCURoute) route).getCarrierRoute().setCcuFixBits(new Integer(role.getFixedBit()));
                    ((CCURoute) route).getCarrierRoute().setCcuVariableBits(new Integer(role.getVarbit()));

                    int rptVarBit = role.getVarbit();

                    for (int j = 0; j < ((CCURoute) route).getRepeaters().size(); j++) {
                        RepeaterRoute rpt = (((CCURoute) route).getRepeaters().get(j));
                        if (rptVarBit + 1 <= 7)
                            rptVarBit++;
                        if (j + 1 == ((CCURoute) route).getRepeaters().size())
                            rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
                        rpt.setVariableBits(new Integer(rptVarBit));
                    }

                } else { // All route combinations have been used, suggest a suitable role combonation to reuse.

                    RoleConflictDialog frame = new RoleConflictDialog(owner, role, (CCURoute) route, routeBoss);
                    frame.setLocationRelativeTo(this);
                    String choice = frame.getValue();

                    if (choice == "Yes") {
                        ((CCURoute) route).getCarrierRoute().setCcuFixBits(new Integer(frame.getRole().getFixedBit()));
                        ((CCURoute) route).getCarrierRoute().setCcuVariableBits(new Integer(frame.getRole().getVarbit()));
                        int rptVarBit = frame.getRole().getVarbit();
                        for (int j = 0; j < ((CCURoute) route).getRepeaters().size(); j++) {
                            RepeaterRoute rpt = (((CCURoute) route).getRepeaters().get(j));
                            if (rptVarBit + 1 <= 7) {
                                rptVarBit++;
                            }
                            if (j + 1 == ((CCURoute) route).getRepeaters().size()) {
                                rptVarBit = 7; // Last repeater's variable bit is always lucky 7.
                            }
                            rpt.setVariableBits(new Integer(rptVarBit));
                        }
                    } else if (choice == "Cancel") {
                        return null;
                    }
                }
                newVal.getDBPersistentVector().add(route);
                return newVal;
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
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("DeviceRoutePanel");
            setLayout(new java.awt.GridBagLayout());
            setSize(350, 200);

            java.awt.GridBagConstraints constraintsRouteLabel = new java.awt.GridBagConstraints();
            constraintsRouteLabel.gridx = 0;
            constraintsRouteLabel.gridy = 0;
            constraintsRouteLabel.weightx = 1;
            constraintsRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRouteLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            add(getRouteLabel(), constraintsRouteLabel);

            java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
            constraintsRouteComboBox.gridx = 0;
            constraintsRouteComboBox.gridy = 1;
            constraintsRouteComboBox.weightx = 1;
            constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            constraintsRouteComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsRouteComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
            add(getRouteComboBox(), constraintsRouteComboBox);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    public void routeList_ValueChanged(javax.swing.event.ListSelectionEvent listSelectionEvent) {
        fireInputUpdate();
    }

    @Override
    public void setValue(Object val) {
        IDatabaseCache cache = DefaultDatabaseCache.getInstance();
        synchronized (cache) {
            List<LiteYukonPAObject> allRoutes = cache.getAllRoutes();
            if (getRouteComboBox().getModel().getSize() > 0)
                getRouteComboBox().removeAllItems();

            for (LiteYukonPAObject liteRoute : allRoutes) {
                if (liteRoute.getPaoType() == PaoType.ROUTE_CCU || liteRoute.getPaoType() == PaoType.ROUTE_MACRO) {
                    getRouteComboBox().addItem(liteRoute);
                }

            }
        }
    }

    @Override
    public void setFirstFocus() {
        // Make sure that when its time to display this panel, the focus starts in the top component
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                getRouteComboBox().requestFocus();
            }
        });
    }
}
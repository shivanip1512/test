package com.cannontech.dbeditor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyVetoException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.clientutils.commonutils.GenericEvent;
import com.cannontech.clientutils.popup.PopUpEventListener;
import com.cannontech.clientutils.popup.PopUpMenuShower;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.editor.PropertyPanelListener;
import com.cannontech.common.gui.util.MessagePanel;
import com.cannontech.common.gui.util.TreeViewPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.login.ClientStartupHelper;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.ClientRights;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LoggerEventListener;
import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.wizard.WizardPanelEvent;
import com.cannontech.common.wizard.WizardPanelListener;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DBDeletionDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.DatabaseTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DNPBase;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.IPCMeter;
import com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMProgramDirectBase;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.tou.TOUSchedule;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.model.DBTreeModel;
import com.cannontech.database.model.DBTreeNode;
import com.cannontech.database.model.DbBackgroundTreeModel;
import com.cannontech.database.model.DummyTreeNode;
import com.cannontech.database.model.FrameAware;
import com.cannontech.database.model.LiteBaseTreeModel;
import com.cannontech.database.model.TreeModelEnum;
import com.cannontech.dbeditor.defines.CommonDefines;
import com.cannontech.dbeditor.menu.CoreCreateMenu;
import com.cannontech.dbeditor.menu.EditMenu;
import com.cannontech.dbeditor.menu.FileMenu;
import com.cannontech.dbeditor.menu.HelpMenu;
import com.cannontech.dbeditor.menu.LMCreateMenu;
import com.cannontech.dbeditor.menu.SystemCreateMenu;
import com.cannontech.dbeditor.menu.ViewMenu;
import com.cannontech.dbeditor.wizard.baseline.BaselineWizardPanel;
import com.cannontech.dbeditor.wizard.billing.BillingFileWizardPanel;
import com.cannontech.dbeditor.wizard.changetype.device.DeviceChangeTypeWizardPanel;
import com.cannontech.dbeditor.wizard.changetype.point.PointChangeTypeWizardPanel;
import com.cannontech.dbeditor.wizard.config.ConfigWizardPanel;
import com.cannontech.dbeditor.wizard.contact.ContactWizardPanel;
import com.cannontech.dbeditor.wizard.copy.device.DeviceCopyWizardPanel;
import com.cannontech.dbeditor.wizard.copy.lm.LMGroupCopyWizardPanel;
import com.cannontech.dbeditor.wizard.copy.lm.LMProgramCopyWizardPanel;
import com.cannontech.dbeditor.wizard.copy.lm.LMScenarioCopyWizardPanel;
import com.cannontech.dbeditor.wizard.copy.point.PointCopyWizardPanel;
import com.cannontech.dbeditor.wizard.customer.CustomerWizardPanel;
import com.cannontech.dbeditor.wizard.device.DeviceWizardPanel;
import com.cannontech.dbeditor.wizard.device.lmconstraint.LMConstraintWizardPanel;
import com.cannontech.dbeditor.wizard.device.lmcontrolarea.LMControlAreaWizardPanel;
import com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupWizardPanel;
import com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramWizardPanel;
import com.cannontech.dbeditor.wizard.device.lmscenario.LMScenarioWizardPanel;
import com.cannontech.dbeditor.wizard.holidayschedule.HolidayScheduleWizardPanel;
import com.cannontech.dbeditor.wizard.notification.group.NotificationGroupWizardPanel;
import com.cannontech.dbeditor.wizard.point.PointWizardPanel;
import com.cannontech.dbeditor.wizard.point.lm.LMPointWizardPanel;
import com.cannontech.dbeditor.wizard.port.PortWizardPanel;
import com.cannontech.dbeditor.wizard.route.RouteWizardPanel;
import com.cannontech.dbeditor.wizard.season.SeasonScheduleWizardPanel;
import com.cannontech.dbeditor.wizard.state.StateWizardPanel;
import com.cannontech.dbeditor.wizard.tags.TagWizardPanel;
import com.cannontech.dbeditor.wizard.tou.TOUScheduleWizardPanel;
import com.cannontech.dbeditor.wizard.user.LoginGroupWizardPanel;
import com.cannontech.dbeditor.wizard.user.UserGroupWizardPanel;
import com.cannontech.dbeditor.wizard.user.YukonUserWizardPanel;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.UserUtils;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class DatabaseEditor implements PropertyPanelListener, WizardPanelListener, ActionListener, WindowListener,
        Observer, PopUpEventListener, PopupMenuListener, DBChangeLiteListener {

    // all editor frame sizes
    public static final Dimension EDITOR_FRAME_SIZE = new Dimension(435, 600);

    public static final URL DBEDITOR_IMG_16 = DatabaseEditor.class.getResource("/DatabaseEditor16.png");
    public static final URL DBEDITOR_IMG_24 = DatabaseEditor.class.getResource("/DatabaseEditor24.png");
    public static final URL DBEDITOR_IMG_32 = DatabaseEditor.class.getResource("/DatabaseEditor32.png");
    public static final URL DBEDITOR_IMG_48 = DatabaseEditor.class.getResource("/DatabaseEditor48.png");
    public static final URL DBEDITOR_IMG_64 = DatabaseEditor.class.getResource("/DatabaseEditor64.png");

    // Do not make this static
    // Making this static causes this to initialize our logging before we get a chance to set the application name. Resulting in 'UnknownApplication'
    private final Logger log = YukonLogManager.getLogger(DatabaseEditor.class);

    public static List<Image> getIconsImages() {

        List<Image> iconsImages = new ArrayList<Image>();
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_16));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_24));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_32));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_48));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_64));

        return iconsImages;
    }

    // gui elements of the app
    private DBEditorTreePopUpMenu treeNodePopUpMenu = null;

    private JMenuBar menuBar;
    private JDesktopPane desktopPane;
    private JScrollPane deskTopFrameScrollPane;
    private MessagePanel messagePanel;
    private TreeViewPanel treeViewPanel;
    private JPanel contentPane;
    private JSplitPane splitPane;
    private FileMenu fileMenu;
    private EditMenu editMenu;
    private CoreCreateMenu coreCreateMenu;
    private LMCreateMenu lmCreateMenu;
    private SystemCreateMenu systemCreateMenu;
    private ViewMenu viewMenu;
    private HelpMenu helpMenu;
    private Frame owner = null;
    // File logger
    private LoggerEventListener loggerEventListener;

    // Allow editor frames at a time
    private JTreeEditorFrame[] editorFrames = null;
    private static final int INITIAL_EDITOR_COUNT = 4;

    private DatabaseTypes currentDatabase = DatabaseTypes.CORE_DB;
    // Map of database types and treemodels to use
    // Keep and add to these in alphabetical order

    private static final TreeModelEnum[] CORE_MODELS = { TreeModelEnum.PORT, TreeModelEnum.DEVICE, TreeModelEnum.IED,
            TreeModelEnum.MCT, TreeModelEnum.MCTBROADCAST, TreeModelEnum.TWOWAYCONFIG, TreeModelEnum.METER,
            TreeModelEnum.DEVICE_METERNUMBER, TreeModelEnum.RFMESH, TreeModelEnum.ROUTE, TreeModelEnum.RTU,
            TreeModelEnum.STATEGROUP, TreeModelEnum.TRANSMITTER,

    };
    private static final TreeModelEnum[] LM_MODELS = { TreeModelEnum.LMCONSTRAINT, TreeModelEnum.LMCONTROLAREA,
            TreeModelEnum.LMGROUPDIGISEP, TreeModelEnum.LMGROUPECOBEE, TreeModelEnum.LMGROUPEMETCON, TreeModelEnum.LMGROUPHONEYWELL,
            TreeModelEnum.LMGROUPEXPRESSCOM, TreeModelEnum.GOLAY, TreeModelEnum.LMGROUPS, TreeModelEnum.LMPROGRAM,
            TreeModelEnum.LMGROUPMACRO, TreeModelEnum.LMGROUPNEST, TreeModelEnum.LMSCENARIO, TreeModelEnum.LMGROUPVERSACOM, };

    private static final TreeModelEnum[] LM_MODELS_WITH_SA = { TreeModelEnum.LMCONSTRAINT, TreeModelEnum.LMCONTROLAREA,
            TreeModelEnum.LMGROUPDIGISEP, TreeModelEnum.LMGROUPECOBEE, TreeModelEnum.LMGROUPEMETCON, TreeModelEnum.LMGROUPHONEYWELL,
            TreeModelEnum.LMGROUPEXPRESSCOM, TreeModelEnum.GOLAY, TreeModelEnum.LMGROUPS, TreeModelEnum.LMPROGRAM,
            TreeModelEnum.LMGROUPMACRO, TreeModelEnum.LMGROUPNEST, TreeModelEnum.LMGROUPSA305, TreeModelEnum.LMGROUPSA205,
            TreeModelEnum.LMGROUPSADIGITAL, TreeModelEnum.LMSCENARIO, TreeModelEnum.LMGROUPVERSACOM, };
    private static final TreeModelEnum[] SYSTEM_MODELS = { TreeModelEnum.ALARM_STATES, TreeModelEnum.BASELINE,
            TreeModelEnum.CICUSTOMER, TreeModelEnum.CONTACT, TreeModelEnum.HOLIDAY_SCHEDULE,
            TreeModelEnum.NOTIFICATION_GROUP, TreeModelEnum.ROLE_GROUPS, TreeModelEnum.SEASON,
            TreeModelEnum.SYSTEM_DEVICE, TreeModelEnum.TAG, TreeModelEnum.TOUSCHEDULE, TreeModelEnum.USERS,
            TreeModelEnum.USER_GROUPS, };
    private static final TreeModelEnum[] NONLOGIN_SYSTEM_MODELS = { TreeModelEnum.ALARM_STATES, TreeModelEnum.BASELINE,
            TreeModelEnum.CICUSTOMER, TreeModelEnum.CONTACT, TreeModelEnum.HOLIDAY_SCHEDULE,
            TreeModelEnum.NOTIFICATION_GROUP, TreeModelEnum.SEASON, TreeModelEnum.TAG, TreeModelEnum.TOUSCHEDULE, };

    private Vector<MessageEventListener> messageListeners = new Vector<MessageEventListener>();

    // During an item state change we need to remember previous selection in order query the user whether we should save or not
    private Object lastSelection = null;

    private static int decimalPlaces;
    private IServerConnection connToDispatch;

    private enum ConnectionStatus {
        UNDEFINED, CONNECTED, DISCONNECTED
    }

    // Flag for connection to dispatch server
    private ConnectionStatus lastConnToVanGoghStatus = ConnectionStatus.UNDEFINED;

    private boolean changingObjectType = false;

    // Flag whether billing option should be present in create (core) menu
    private boolean activateBilling;
    private static boolean isSuperuser = false;
    private boolean accessOfLoginNotAllowed = false;

    private static DatabaseEditor editor = null;
    
    private PaoDefinitionService paoDefinitionService = YukonSpringHook.getBean(PaoDefinitionService.class);

    public DatabaseEditor() {
        super();
        this.editor = this;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!(event.getSource() instanceof JMenuItem)) {
            return;
        }

        JMenuItem item = (JMenuItem) event.getSource();

        if (item == viewMenu.coreRadioButtonMenuItem && currentDatabase != DatabaseTypes.CORE_DB) {
            Frame f = SwingUtil.getParentFrame(getContentPane());
            Cursor savedCursor = f.getCursor();
            try {
                f.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                currentDatabase = DatabaseTypes.CORE_DB;
                setDatabase(currentDatabase);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                f.setCursor(savedCursor);
            }
            f.repaint();
            
        } else if (item == viewMenu.lmRadioButtonMenuItem && currentDatabase != DatabaseTypes.LM_DB) {
            Frame f = SwingUtil.getParentFrame(getContentPane());
            Cursor savedCursor = f.getCursor();
            try {
                f.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                currentDatabase = DatabaseTypes.LM_DB;
                setDatabase(currentDatabase);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                f.setCursor(savedCursor);
            }
            f.repaint();
            
        } else if (item == viewMenu.systemRadioButtonMenuItem && currentDatabase != DatabaseTypes.SYSTEM_DB) {
            Frame f = SwingUtil.getParentFrame(getContentPane());
            Cursor savedCursor = f.getCursor();
            try {
                f.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                currentDatabase = DatabaseTypes.SYSTEM_DB;
                setDatabase(currentDatabase);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                f.setCursor(savedCursor);
            }
            f.repaint();
            
        } else if (item == fileMenu.exitMenuItem) {
            if (exitConfirm()) {
                exit();
            }
        } else if (item == editMenu.editMenuItem) {
            executeEditButton_ActionPerformed(event);
        } else if (item == editMenu.deleteMenuItem) {
            executeDeleteButton_ActionPerformed(event);
        } else if (item == editMenu.copyMenuItem) {
            executeCopyButton_ActionPerformed(event);
        } else if (item == editMenu.changeTypeMenuItem) {
            executeChangeTypeButton_ActionPerformed(event);
        } else if (item == editMenu.searchMenuItem) {
            executeFindButton_ActionPerformed(event);
        } else if (item == viewMenu.refreshMenuItem) {
            viewMenuRefreshAction();
        } else if (item == viewMenu.showMessageLogButton) {
            getMessagePanel().setVisible(viewMenu.showMessageLogButton.isSelected());
        } else if (item == helpMenu.aboutMenuItem) {
            executeAboutButton_ActionPerformed(event);
        } else if (item == helpMenu.helpTopicMenuItem) {
            // run and show our help program
            CtiUtilities.showHelp(CommonDefines.HELP_FILE);
        } else {
            displayAWizardPanel(item);
        }

    }

    public void viewMenuRefreshAction() {
        Frame f = SwingUtil.getParentFrame(getContentPane());
        Cursor savedCursor = f.getCursor();

        try {
            f.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            // refresh the cache and the connections state
            DefaultDatabaseCache.getInstance().releaseAllCache();

            // grab the current selected object in the tree
            Object holder = treeViewPanel.getSelectedItem();
            // do the actual refresh of the tree
            treeViewPanel.refresh();
            if (holder != null && holder instanceof LiteBase) {
                // if we tried to refresh a tree that extends DbBackgroundTreeModel (CICustomer, Login, Contact) then we are doing the select after the update and not right here
                if (!(treeViewPanel.getCurrentTreeModel() instanceof DbBackgroundTreeModel)) {
                    treeViewPanel.selectLiteObject((LiteBase) holder);
                }
            }

            if (getConnToDispatch().isValid()) {
                DispatchClientConnection conn = (DispatchClientConnection) getConnToDispatch();
                f.setTitle("Yukon Database Editor [Connected to Dispatch@" + conn.getConnectionUri().getRawAuthority() + "]");
                f.repaint();
            } else {
                f.setTitle("Yukon Database Editor [Not Connected to Dispatch]");
                f.repaint();
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            f.setCursor(savedCursor);
            fireMessage(new MessageEvent(this,
                                         "Tree view, connection state and cache have been refreshed",
                                         MessageEvent.INFORMATION_MESSAGE));
        }
    }

    public void addMessageListener(MessageEventListener listener) {
        if (!messageListeners.contains(listener)) {
            messageListeners.addElement(listener);
        }
    }

    private JTreeEditorFrame createInternalEditorFrame() {
        // we can make the frame final because no one is supposed to reasign it to a new JTreeEditorFrame
        final JTreeEditorFrame frame = new JTreeEditorFrame();
        frame.setResizable(true);
        frame.setVisible(false);
        frame.setMaximizable(true);
        frame.setIconifiable(true);
        frame.setClosable(true);
        frame.setDefaultCloseOperation(JTreeEditorFrame.DO_NOTHING_ON_CLOSE);
        desktopPane.add(frame);

        // set up the listener so when the X box in the right hand corner is pressed
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                // Call the events that make it seem like the CANCEL button was pressed
                if (frame.getContentPane() instanceof PropertyPanel) {
                    PropertyPanel current = (PropertyPanel) frame.getContentPane();

                    if (current != null && lastSelection != null && current.hasChanged()) {
                        // prompt the user to save changes
                        int c = JOptionPane.showConfirmDialog(getParentFrame(),
                                                              "Do you want to save changes made to '" + frame.getOwnerNode() + "'?",
                                                              "Yukon Database Editor",
                                                              JOptionPane.YES_NO_OPTION);

                        if (c == JOptionPane.YES_OPTION) {
                            current.fireOkButtonPressed();
                        } else {
                            current.fireCancelButtonPressed();
                        }
                    } else if (current != null && lastSelection != null) {
                        // act as though the cancel button has been pressed
                        current.fireCancelButtonPressed();
                    }
                }
            }
        });

        ImageIcon editorIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_16));
        frame.setFrameIcon(editorIcon);
        return frame;
    }

    private void displayAWizardPanel(JMenuItem item) {
        DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
        LiteBase selectedItem = null;
        if (node != null && node.getUserObject() instanceof LiteBase) {
            selectedItem = (LiteBase) node.getUserObject();
        }

        if (item == coreCreateMenu.portMenuItem) {
            showWizardPanel(new PortWizardPanel());
        } else if (item == coreCreateMenu.deviceMenuItem) {
            showWizardPanel(new DeviceWizardPanel());
        } else if (item == coreCreateMenu.routeMenuItem) {
            showWizardPanel(new RouteWizardPanel());
        } else if (item == coreCreateMenu.pointMenuItem) {
            if (selectedItem instanceof LiteYukonPAObject) {
                showWizardPanel(new PointWizardPanel(new Integer(((LiteYukonPAObject) selectedItem).getYukonID())));
            } else if (selectedItem instanceof LitePoint) {
                showWizardPanel(new PointWizardPanel(new Integer(((LitePoint) selectedItem).getPaobjectID())));
            } else {
                showWizardPanel(new PointWizardPanel());
            }
        } else if (item == coreCreateMenu.stateGroupMenuItem) {
            showWizardPanel(new StateWizardPanel());
        }
        // new billing addition
        else if (item == coreCreateMenu.billingGroupMenuItem) {
            showWizardPanel(new BillingFileWizardPanel());
        }

        else if (item == coreCreateMenu.config2WayMenuItem) {
            showWizardPanel(new ConfigWizardPanel());

        } else if (item == lmCreateMenu.lmGroupMenuItem) {
            showWizardPanel(new LMGroupWizardPanel());
        } else if (item == lmCreateMenu.lmControlAreaMenuItem) {
            showWizardPanel(new LMControlAreaWizardPanel());
        } else if (item == lmCreateMenu.lmProgramMenuItem) {
            showWizardPanel(new LMProgramWizardPanel());
        } else if (item == lmCreateMenu.pointMenuItem) {
            if (selectedItem instanceof LiteYukonPAObject) {
                showWizardPanel(new LMPointWizardPanel(new Integer(((LiteYukonPAObject) selectedItem).getYukonID())));
            } else if (selectedItem instanceof LitePoint) {
                showWizardPanel(new LMPointWizardPanel(new Integer(((LitePoint) selectedItem).getPointID())));
            } else {
                showWizardPanel(new LMPointWizardPanel());
            }

        } else if (item == lmCreateMenu.lmProgramConstraintMenuItem) {
            showWizardPanel(new LMConstraintWizardPanel());
        } else if (item == lmCreateMenu.lmControlScenarioMenuItem) {
            showWizardPanel(new LMScenarioWizardPanel());
        } else if (item == systemCreateMenu.notificationGroupMenuItem) {
            showWizardPanel(new NotificationGroupWizardPanel());
        } else if (item == systemCreateMenu.contactMenuItem) {
            showWizardPanel(new ContactWizardPanel());
        } else if (item == systemCreateMenu.userMenuItem) {
            showWizardPanel(new YukonUserWizardPanel());
        } else if (item == systemCreateMenu.userGroupMenuItem) {
            showWizardPanel(new UserGroupWizardPanel());
        } else if (item == systemCreateMenu.roleGroupMenuItem) {
            showWizardPanel(new LoginGroupWizardPanel());
        } else if (item == systemCreateMenu.holidayMenuItem) {
            showWizardPanel(new HolidayScheduleWizardPanel());
        } else if (item == systemCreateMenu.customerMenuItem) {
            showWizardPanel(new CustomerWizardPanel());
        } else if (item == systemCreateMenu.baselineMenuItem) {
            showWizardPanel(new BaselineWizardPanel());
        } else if (item == systemCreateMenu.seasonMenuItem) {
            showWizardPanel(new SeasonScheduleWizardPanel());
        } else if (item == systemCreateMenu.tagMenuItem) {
            showWizardPanel(new TagWizardPanel());
        } else if (item == systemCreateMenu.touMenuItem) {
            showWizardPanel(new TOUScheduleWizardPanel());
        } else if (item == systemCreateMenu.systemPointMenuItem) {
            showWizardPanel(new PointWizardPanel(new Integer(0)));
        }
    }

    public void displayDatabaseEditor(JRootPane rPane) {
        initialize(rPane);
    }

    private void executeAboutButton_ActionPerformed(ActionEvent event) {
        Frame f = getParentFrame();
        AboutDialog aboutDialog = new AboutDialog(f, "About DBEditor", true);

        aboutDialog.setLocationRelativeTo(f);
        aboutDialog.setValue(null);
        aboutDialog.show();
    }

    private boolean executeChangeObjectType(WizardPanelEvent event) {
        boolean success = false;
        WizardPanel p = (WizardPanel) event.getSource();

        DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
        LiteBase liteObject = (LiteBase) node.getUserObject();
        DBPersistent selectedObject = LiteFactory.createDBPersistent(liteObject);

        try {
            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, selectedObject);
            selectedObject = t.execute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        boolean changeType = true;
        int currentType = 0;
        int newType = 0;
        String type = " ";
        boolean checkConfigs = false;
        int paoId = -1;

        if (selectedObject instanceof DeviceBase) {
            type = ((DeviceBase) selectedObject).getPaoType().getPaoTypeName();
            currentType = PaoType.getPaoTypeId(type);
            newType = ((PaoType) p.getValue(null)).getDeviceTypeId();
            checkConfigs = true;
            paoId = ((DeviceBase) selectedObject).getPAObjectID();
        } else if (selectedObject instanceof DeviceMeterGroupBase) {
            DeviceDao deviceDao = YukonSpringHook.getBean(DeviceDao.class);
            int deviceId = ((DeviceMeterGroupBase) selectedObject).getDeviceMeterGroup().getDeviceID();
            SimpleDevice device = deviceDao.getYukonDevice(deviceId);

            type = device.getDeviceType().getPaoTypeName();
            currentType = device.getType();
            newType = ((PaoType) p.getValue(null)).getDeviceTypeId();
        } else if (selectedObject instanceof PointBase) {
            type = ((PointBase) selectedObject).getPoint().getPointType();
            currentType = PointTypes.getType(type);
            newType = ((PointType) p.getValue(null)).getPointTypeId();
        }

        int confirm = 0;

        if (currentType == newType) {
            confirm = JOptionPane.showConfirmDialog(getParentFrame(),
                                                    "Same type selected  '" + type + "'\n" + "  Continue to change type" + "?" + "\n" + "\n",
                                                    "Confirm Type Change",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.NO_OPTION) {
                changeType = false;
            }

        }

        if (!(selectedObject instanceof PointBase) && PaoType.getForId(currentType).isTransmitter()) {
            String temp = "is a transmitter \n" + "and COULD be associated with a route." + '\n' + "Continuing may result in a route/device type mismatch." + '\n' + "Continue to change type?" + "\n" + "\n";
            confirm = JOptionPane.showConfirmDialog(getParentFrame(),
                                                    "The device '" + type + " " + temp,
                                                    "Possible Device/Route Type Mismatch",
                                                    JOptionPane.YES_NO_OPTION,
                                                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.NO_OPTION) {
                changeType = false;
            }

        }

        if (changeType) {
            selectedObject = (DBPersistent) p.getValue(selectedObject);

            try {

                // we actually may be INSERTING a new object
                if (selectedObject instanceof SmartMultiDBPersistent) {

                    SmartMultiDBPersistent smarty = (SmartMultiDBPersistent) selectedObject;

                    for (int i = 0; i < smarty.size(); i++) {
                        if (!smarty.getDBPersistent(i).equals(smarty.getOwnerDBPersistent())) {
                            generateDBChangeMsg(smarty.getDBPersistent(i), DbChangeType.ADD);
                        }
                    }

                    selectedObject = smarty.getOwnerDBPersistent();
                }

                Transaction t1 = Transaction.createTransaction(Transaction.UPDATE, selectedObject);
                selectedObject = t1.execute();

                // meters with a device config should only retain the config if
                // it's appropriate for the new type
                if (checkConfigs) {
                    boolean configWasRemoved = false;
                    try {
                        configWasRemoved = DatabaseEditorUtil.unassignDeviceConfigIfInvalid(paoId);
                    } catch (InvalidDeviceTypeException e) {
                        log.error("Unable to unassign device configuration on type change.", e);
                    }
                    if (configWasRemoved) {
                        JOptionPane.showMessageDialog(getParentFrame(),
                                                      "The device configuration " + "associated with this device is not compatible with the new device type " + "and has been removed.",
                                                      "Device Configuration Removed",
                                                      JOptionPane.WARNING_MESSAGE);
                    }
                }

                // always do this
                generateDBChangeMsg(selectedObject, DbChangeType.UPDATE);

                getTreeViewPanel().selectObject(selectedObject);

                // make sure there isnt already an editor frame showing for this node
                if (isEditorAlreadyShowing(node)) {
                    // set the current selected frame to the one found
                    PropertyPanel current = (PropertyPanel) getEditorFrame(node).getContentPane();

                    // just act as though the cancel button was pressed on the editor pane
                    if (current != null) {
                        current.fireCancelButtonPressed();
                    }
                }

                String messageString = selectedObject + " successfully changed Type.";
                fireMessage(new MessageEvent(this, messageString));
                success = true;
            } catch (TransactionException e) {
                log.error(e.getMessage(), e);

                String messageString = "Error changing type of " + selectedObject + " in the database.  Error received:  " + e.getMessage();
                fireMessage(new MessageEvent(this, messageString, MessageEvent.ERROR_MESSAGE));
            }
        }
        return success;
    }

    public void executeChangeTypeButton_ActionPerformed(ActionEvent event) {
        DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
        if (node != null) {
            // a DBPersistent must be created from the Lite object so you can copy it
            DBPersistent userObject = LiteFactory.createDBPersistent((LiteBase) node.getUserObject());

            try {
                Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, userObject);
                userObject = t.execute();
            } catch (Exception e) {
                log.error(e.getMessage(), e);

                fireMessage(new MessageEvent(this,
                                             "Error changing type for " + node + ".  Error received:  " + e.getMessage(),
                                             MessageEvent.ERROR_MESSAGE));
            }

            DeviceDao deviceDao = (DeviceDao) YukonSpringHook.getBean("deviceDao");
            if (userObject instanceof DeviceBase && paoDefinitionService.isPaoTypeChangeable(deviceDao.getYukonDeviceForDevice((DeviceBase) userObject))) {
                showChangeTypeWizardPanel(new DeviceChangeTypeWizardPanel(userObject));
            } else if (userObject instanceof DeviceMeterGroupBase) {
                int deviceId = ((DeviceMeterGroupBase) userObject).getDeviceMeterGroup().getDeviceID();
                SimpleDevice device = deviceDao.getYukonDevice(deviceId);
                if (paoDefinitionService.isPaoTypeChangeable(device)) {
                    YukonPAObject yukonPAObject = PAOFactory.createPAObject(deviceId);
                    showChangeTypeWizardPanel(new DeviceChangeTypeWizardPanel(yukonPAObject));
                }
            } else if (userObject instanceof PointBase) {
                try {
                    new StringBuffer("Are you sure you want to change the type of '" + node.toString() + "'?");

                    DBDeleteResult delRes = new DBDeleteResult(((PointBase) userObject).getPoint().getPointID().intValue(), YukonSpringHook.getBean(DBDeletionDao.class).POINT_TYPE);

                    byte deleteVal = YukonSpringHook.getBean(DBDeletionDao.class).deletionAttempted(delRes);

                    if (deleteVal == YukonSpringHook.getBean(DBDeletionDao.class).STATUS_ALLOW || deleteVal == YukonSpringHook.getBean(DBDeletionDao.class).STATUS_CONFIRM) {
                        showChangeTypeWizardPanel(new PointChangeTypeWizardPanel(userObject));
                    } else {

                        JOptionPane.showConfirmDialog(getParentFrame(),
                                                      ("You cannot change this point '" + node.toString() + "'" + delRes.getDescriptionMsg()
                                                                                                                        .toString()),
                                                      "Unable to change",
                                                      JOptionPane.CLOSED_OPTION,
                                                      JOptionPane.WARNING_MESSAGE);
                    }

                } catch (SQLException e) {
                    log.error(e.getMessage(), e);
                    JOptionPane.showConfirmDialog(getParentFrame(),
                                                  "Are you sure you want to change '" + node + "'?",
                                                  "Confirm Delete",
                                                  JOptionPane.YES_NO_OPTION,
                                                  JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(getParentFrame(),
                                              "Cannot currently change the type of this Object",
                                              "ChangeType Error",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(getParentFrame(),
                                          "You must select something to be changed",
                                          "ChangeType Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }

    }

    private void executeCopyButton_ActionPerformed(ActionEvent event) {
        DefaultMutableTreeNode node = getDefaultTreeNode();

        if (node != null) {
            // a DBPersistent must be created from the Lite object so you can copy it
            DBPersistent toCopy = LiteFactory.createDBPersistent((LiteBase) node.getUserObject());
            if (toCopy instanceof DeviceBase && !(toCopy instanceof LMGroup) && !(toCopy instanceof IPCMeter)) {
                showCopyWizardPanel(toCopy);
            } else if (toCopy instanceof DeviceMeterGroupBase) {
                int deviceId = ((DeviceMeterGroupBase) toCopy).getDeviceMeterGroup().getDeviceID();
                YukonPAObject yukonPAObject = PAOFactory.createPAObject(deviceId);
                showCopyWizardPanel(yukonPAObject);
            } else if (toCopy instanceof LMProgramDirectBase) {
                showCopyWizardPanel(new LMProgramCopyWizardPanel(toCopy), toCopy);
            } else if (toCopy instanceof LMGroup) {
                showCopyWizardPanel(new LMGroupCopyWizardPanel(toCopy), toCopy);
            } else if (toCopy instanceof LMScenario) {
                showCopyWizardPanel(new LMScenarioCopyWizardPanel(toCopy), toCopy);
            } else if (toCopy instanceof PointBase) {
                showCopyWizardPanel(new PointCopyWizardPanel(toCopy, currentDatabase), toCopy);
            } else {
                JOptionPane.showMessageDialog(getParentFrame(),
                                              "Cannot currently copy that type of Object",
                                              "Copy Error",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(getParentFrame(),
                                          "You must select something to be copied",
                                          "Copy Error",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public DefaultMutableTreeNode getDefaultTreeNode() {
        DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
        return node;
    }

    public void showCopyWizardPanel(DBPersistent toCopy) {
        DeviceCopyWizardPanel devicePanel = new DeviceCopyWizardPanel(toCopy);
        devicePanel.setDeviceType(toCopy);
        showCopyWizardPanel(devicePanel, toCopy);
    }

    private void deleteDBPersistent(DBPersistent deletable) {
        try {
            // get dbChangeMsgs BEFORE execute this may be a delete and the dbChangeMsgs may not be retrievable after execute
            DBChangeMsg[] msgs = getDBChangeMsgs(deletable, DbChangeType.DELETE);

            Transaction t = Transaction.createTransaction(Transaction.DELETE, deletable);

            deletable = t.execute();

            // fire DBChange messages out to Dispatch
            queueDBChangeMsgs(deletable, DbChangeType.DELETE, msgs);

            fireMessage(new MessageEvent(this, deletable + " deleted successfully from the database."));
        } catch (TransactionException e) {
            log.error(e.getCause(), e);
            fireMessage(new MessageEvent(this,
                                         "Error deleting " + deletable + " from the database.  Error received:  " + e.getCause(),
                                         MessageEvent.ERROR_MESSAGE));
        }

    }

    private void executeDeleteButton_ActionPerformed(ActionEvent event) {
        TreeItemDeleter tid = new TreeItemDeleter(getTreeViewPanel());
        int confirm = tid.executeDelete();
        DefaultMutableTreeNode[] nodes = tid.getNodes();
        DBPersistent[] deletables = tid.getDeletables();

        if (confirm == JOptionPane.YES_OPTION) {
            for (int i = 0; i < nodes.length; i++) {
                // make sure there isnt already an editor frame showing for this node
                if (isEditorAlreadyShowing(nodes[i])) {
                    // set the current selected frame to the one found
                    PropertyPanel current = (PropertyPanel) getEditorFrame(nodes[i]).getContentPane();

                    // just act as though the cancel button was pressed on the editor pane
                    if (current != null) {
                        current.fireCancelButtonPressed();
                    }
                }

                try {
                    deleteDBPersistent(deletables[i]);
                } finally {
                    // Destroy the frame on an ok or a cancel
                    JTreeEditorFrame frame = getEditorFrame(nodes[i]);
                    if (frame != null) {
                        frame.setVisible(false);
                        PropertyPanel current = (PropertyPanel) frame.getContentPane();
                        current.setChanged(false);
                    }

                }
            }// end for loop
        }
    }

    private void executeEditButton_ActionPerformed(ActionEvent event) {
        Frame owner = SwingUtil.getParentFrame(getTree());
        Cursor savedCursor = owner.getCursor();

        try {
            owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));

            DefaultMutableTreeNode[] nodes = getTreeViewPanel().getSelectedNodes();

            if (nodes != null && nodes.length >= 1) {
                lastSelection = nodes[0];

                for (int i = 0; i < nodes.length; i++) {
                    // make sure there isnt already a editor frame showing for this node
                    if (isEditorAlreadyShowing(nodes[i])) {
                        // set the current selected frame to the one found
                        getEditorFrame(nodes[i]).setSelected(true);
                        continue;
                    }

                    if (nodes[i].getUserObject() instanceof String) {
                        continue;
                    }

                    // do some mapping to get the compatible DBPersistent
                    DBPersistent userObject = LiteFactory.convertLiteToDBPers((LiteBase) nodes[i].getUserObject());

                    try {
                        Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, userObject);
                        userObject = t.execute();
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);

                        fireMessage(new MessageEvent(this,
                                                     "Error retrieving " + nodes[i] + " from the database.  Error received:  " + e.getMessage(),
                                                     MessageEvent.ERROR_MESSAGE));
                    }

                    PropertyPanel panel = EditorPanelFactory.createEditorPanel(userObject);
                    // userObject.getEditorPanel(); //This call takes the most time

                    panel.addPropertyPanelListener(this);
                    panel.setValue(userObject);

                    JTreeEditorFrame frame = getAvailableEditorFrame();
                    frame.setOwnerNode(nodes[i]); // set the editors ownerNode
                    frame.setTitle(userObject.toString() + " : " + panel.toString());
                    frame.setContentPane(panel);

                    // sets the size of EVERY editor frame!!!!!!!
                    frame.setSize(EDITOR_FRAME_SIZE);
                    frame.setMinimumSize(new Dimension(350, 482)); // ~80% of default frame size
                    frame.setMaximum(false); // don't maximize since we are setting the size of the frame already.
                    frame.setLocation(getVisibleEditorFrames() * 10, getVisibleEditorFrames() * 20);

                    frame.setVisible(true);

                    try {
                        frame.setSelected(true);
                    } catch (PropertyVetoException e) {
                        log.error(e.getMessage(), e); // when does this happen??
                    }

                }

            } else if (event.getSource() != null && event.getSource() instanceof DBPersistent) {
                showEditor((DBPersistent) event.getSource());
            } else {
                JOptionPane.showMessageDialog(getParentFrame(),
                                              "You must select something to be edited",
                                              "Edit Error",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            handleException(e);
        } finally {
            owner.setCursor(savedCursor);
        }
    }

    private void showEditor(DBPersistent userObject) {
        Frame owner = SwingUtil.getParentFrame(getTree());
        Cursor savedCursor = owner.getCursor();
        LiteBase liteBase = LiteFactory.createLite(userObject);
        DefaultMutableTreeNode node = new DefaultMutableTreeNode();
        node.setUserObject(liteBase);
        owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        try {
            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, userObject);
            userObject = t.execute();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            fireMessage(new MessageEvent(this,
                                         "Error retrieving " + liteBase + " from the database.  Error received:  " + e.getMessage(),
                                         MessageEvent.ERROR_MESSAGE));
        }
        PropertyPanel panel = EditorPanelFactory.createEditorPanel(userObject);
        panel.addPropertyPanelListener(this);
        panel.setValue(userObject);
        JTreeEditorFrame frame = getAvailableEditorFrame();
        frame.setOwnerNode(node); // set the editors ownerNode
        frame.setTitle(userObject.toString() + " : " + panel.toString());
        frame.setContentPane(panel);

        frame.setSize(EDITOR_FRAME_SIZE);
        frame.setLocation(getVisibleEditorFrames() * 10, getVisibleEditorFrames() * 20);
        frame.setVisible(true);
        try {
            frame.setSelected(true);
        } catch (PropertyVetoException e) {
            log.error(e.getMessage(), e); // when does this happen??
        } finally {
            owner.setCursor(savedCursor);
        }

    }

    public void executeFindButton_ActionPerformed(ActionEvent event) {
        String value = JOptionPane.showInputDialog(getParentFrame(),
                                                   "Name of the item: ",
                                                   "Search",
                                                   JOptionPane.QUESTION_MESSAGE);

        if (value != null) {
            boolean found = getTreeViewPanel().searchFirstLevelString(value);

            if (!found) {
                JOptionPane.showMessageDialog(getParentFrame(),
                                              "Unable to find your selected item",
                                              "Item Not Found",
                                              JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void executeSortByNameButton_ActionPerformed(ActionEvent event) {
        DBTreeModel currentModel = (DBTreeModel) getTreeViewPanel().getSelectedTreeModel();

        DBTreeNode dummyNode = (DBTreeNode) getTreeViewPanel().getSelectedNode();

        currentModel.sortChildNodes(dummyNode, DBTreeModel.SORT_POINT_NAME);
    }

    public void executeSortByOffsetButton_ActionPerformed(ActionEvent event) {
        DBTreeModel currentModel = (DBTreeModel) getTreeViewPanel().getSelectedTreeModel();

        DBTreeNode dummyNode = (DBTreeNode) getTreeViewPanel().getSelectedNode();

        currentModel.sortChildNodes(dummyNode, DBTreeModel.SORT_POINT_OFFSET);
    }

    private void exit() {
        // There may be events in the EventQueue up to this point, let them go first then we can Exit the program.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.exit(0);
            }
        });
    }

    private boolean exitConfirm() {
        boolean retVal = true;
        synchronized (getInternalEditorFrames()) {
            for (int i = 0; i < getInternalEditorFrames().length; i++) {
                if (getInternalEditorFrames()[i].getContentPane() instanceof PropertyPanel) {
                    PropertyPanel current = (PropertyPanel) getInternalEditorFrames()[i].getContentPane();
                    if (current != null) {
                        if (current.hasChanged() && lastSelection != null) {
                            int confirm = JOptionPane.showConfirmDialog(getParentFrame(),
                                                                        "Do you want to save changes made to '" + getInternalEditorFrames()[i].getOwnerNode() + "'?",
                                                                        "Yukon Database Editor",
                                                                        JOptionPane.YES_NO_CANCEL_OPTION);

                            // act as though the cancel button has been pressed
                            if (confirm == JOptionPane.YES_OPTION) {
                                current.fireOkButtonPressed();
                            } else if (confirm == JOptionPane.NO_OPTION) {
                                current.fireCancelButtonPressed();
                            } else {
                                retVal = false;
                            }
                        } else {
                            current.fireCancelButtonPressed();
                        }
                    }

                }

            }
        }

        return retVal;
    }

    public void fireMessage(MessageEvent event) {

        for (int i = messageListeners.size() - 1; i >= 0; i--) {
            messageListeners.elementAt(i).messageEvent(event);
        }
    }

    /**
     * Used only to retrieve the dbChangeMsgs before executing a transaction, since that transaction may be a delete 
     * - in which case you'll want to retrieve dbChangeMsgs, execute, then queue the changes using queueDBChangeMsgs(). 
     * This is because when it is a delete the dbChangeMsgs may not be retrievable after execute. Use the standard
     * generateDBChangeMsg() when not executing a delete, it does both get+queue steps.
     */
    private DBChangeMsg[] getDBChangeMsgs(DBPersistent object, DbChangeType dbChangeType) {

        DBChangeMsg[] dbChanges = null;

        if (object instanceof CTIDbChange) {
            dbChanges = DefaultDatabaseCache.getInstance().createDBChangeMessages((CTIDbChange) object, dbChangeType);
        } else {
            throw new IllegalArgumentException("Non " + CTIDbChange.class.getName() + " class tried to generate a " + DBChangeMsg.class.getName() + " its class was : " + object.getClass()
                                                                                                                                                                                .getName());
        }

        return dbChanges;
    }

    /**
     * Queues dbChanges to dispatch.
     */
    private void queueDBChangeMsgs(DBPersistent object, DbChangeType dbChangeType, DBChangeMsg[] dbChange) {
        for (int i = 0; i < dbChange.length; i++) {
            // handle the DBChangeMsg locally
            LiteBase lBase = DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);

            // if cache isn't able to return us the liteBase, then build it from the DBPersistent itself
            if (lBase == null) {
                lBase = LiteFactory.createLite(object);
            }

            // Special case for point deletion
            if (lBase == null && dbChange[i].getDatabase() == DBChangeMsg.CHANGE_POINT_DB && dbChangeType == DbChangeType.DELETE && object instanceof PointBase) {

                lBase = LiteFactory.createLite(object);
            }

            // tell our tree we may need to change the display
            updateTreePanel(lBase, (dbChange[i]).getDbChangeType());

            getConnToDispatch().queue(dbChange[i]);
        }
    }

    /**
     * Gets and queues dbChangeMsgs. Not to be used when executing a DELETE.
     */
    private void generateDBChangeMsg(DBPersistent object, DbChangeType dbChangeType) {

        DBChangeMsg[] msgs = getDBChangeMsgs(object, dbChangeType);
        queueDBChangeMsgs(object, dbChangeType, msgs);
    }

    public JTreeEditorFrame getAvailableEditorFrame() {
        synchronized (getInternalEditorFrames()) {
            int i = 0;
            for (i = 0; i < getInternalEditorFrames().length; i++) {
                if (!(getInternalEditorFrames()[i]).isVisible()) {
                    return getInternalEditorFrames()[i];
                }
            }

            // didnt find an available one, lets create a new one
            JTreeEditorFrame[] frames = new JTreeEditorFrame[getInternalEditorFrames().length + 1];
            System.arraycopy(getInternalEditorFrames(), 0, frames, 0, getInternalEditorFrames().length);
            editorFrames = frames;

            getInternalEditorFrames()[i] = createInternalEditorFrame();
            return getInternalEditorFrames()[i];
        }

    }

    private IServerConnection getConnToDispatch() {
        if (connToDispatch == null) {
            connToDispatch = ConnPool.getInstance().getDefDispatchConn();
            connToDispatch.addObserver(this);
            updateConnectionStatus(connToDispatch);
        }
        return connToDispatch;
    }

    private Container getContentPane() {

        if (contentPane == null) {
            contentPane = new JPanel();
            contentPane.setLayout(new BorderLayout());

            contentPane.add(getSplitPane(), "Center");
            contentPane.add(getMessagePanel(), "South");
        }

        return contentPane;

    }

    public static int getDecimalPlaces() {
        return decimalPlaces;
    }

    private JScrollPane getDeskTopFrameScrollPane() {
        if (deskTopFrameScrollPane == null) {
            deskTopFrameScrollPane = new JScrollPane();
            deskTopFrameScrollPane.setName("EditorFrameScrollPane");
            deskTopFrameScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            deskTopFrameScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            getDeskTopFrameScrollPane().setViewportView(getDesktopPane());
        }

        return deskTopFrameScrollPane;
    }

    private JDesktopPane getDesktopPane() {
        if (this.desktopPane == null) {
            this.desktopPane = new JDesktopPane();
        }

        return this.desktopPane;
    }

    private JTreeEditorFrame getEditorFrame(DefaultMutableTreeNode ownerNode) {
        synchronized (getInternalEditorFrames()) {
            for (int i = 0; i < getInternalEditorFrames().length; i++) {
                if (getInternalEditorFrames()[i].getOwnerNode() != null && getInternalEditorFrames()[i].getOwnerNode()
                                                                                                       .getUserObject()
                                                                                                       .equals(ownerNode.getUserObject())) {
                    return getInternalEditorFrames()[i];
                }
            }
        }

        return null;
    }

    /**
     * Used to get a listener for logging database editor events
     */
    private LoggerEventListener getLoggerEventListener() {
        if (loggerEventListener == null) {
            loggerEventListener = new LoggerEventListener(DatabaseEditor.class);
        }

        return loggerEventListener;
    }

    private int getFrameLocationByPanel(PropertyPanel panel) {
        // Loop through all the frames on the desktopPane and find out which one contains the PropertyPanel
        synchronized (getInternalEditorFrames()) {
            for (int i = 0; i < getInternalEditorFrames().length; i++) {
                if (getInternalEditorFrames()[i].getContentPane() == panel) {
                    return i;
                }
            }
            // should never get here
            throw new RuntimeException("PropertyPanel '" + panel.toString() + "' found that does not have a parent JInternalFrame");
        }
    }

    private JTreeEditorFrame[] getInternalEditorFrames() {
        if (editorFrames == null) {
            editorFrames = new JTreeEditorFrame[INITIAL_EDITOR_COUNT];

            // create the INITIAL_EDITOR_COUNT amount of JInternalFrames
            for (int i = 0; i < INITIAL_EDITOR_COUNT; i++) {
                editorFrames[i] = createInternalEditorFrame();
            }
        }

        return editorFrames;
    }

    private JMenuBar getMenuBar(DatabaseTypes whichDatabase) {

        if (this.menuBar == null) {
            this.menuBar = new JMenuBar();

            fileMenu = new FileMenu();
            editMenu = new EditMenu();
            coreCreateMenu = new CoreCreateMenu();
            lmCreateMenu = new LMCreateMenu();
            systemCreateMenu = new SystemCreateMenu();
            viewMenu = new ViewMenu();
            helpMenu = new HelpMenu();

            JMenuItem item;

            for (int i = 0; i < fileMenu.getItemCount(); i++) {
                item = fileMenu.getItem(i);

                if (item != null) {
                    fileMenu.getItem(i).addActionListener(this);
                }
            }

            for (int i = 0; i < editMenu.getItemCount(); i++) {
                item = editMenu.getItem(i);

                if (item != null) {
                    editMenu.getItem(i).addActionListener(this);
                }
            }

            for (int i = 0; i < coreCreateMenu.getItemCount(); i++) {
                item = coreCreateMenu.getItem(i);

                if (item != null) {
                    coreCreateMenu.getItem(i).addActionListener(this);
                }
            }

            for (int i = 0; i < lmCreateMenu.getItemCount(); i++) {
                item = lmCreateMenu.getItem(i);

                if (item != null) {
                    lmCreateMenu.getItem(i).addActionListener(this);
                }
            }
            for (int i = 0; i < systemCreateMenu.getItemCount(); i++) {
                item = systemCreateMenu.getItem(i);

                if (item != null) {
                    systemCreateMenu.getItem(i).addActionListener(this);
                }
            }

            for (int i = 0; i < viewMenu.getItemCount(); i++) {
                item = viewMenu.getItem(i);

                if (item != null) {
                    viewMenu.getItem(i).addActionListener(this);
                }
            }

            for (int i = 0; i < helpMenu.getItemCount(); i++) {
                item = helpMenu.getItem(i);

                if (item != null) {
                    helpMenu.getItem(i).addActionListener(this);
                }
            }

            this.menuBar.add(fileMenu);
            this.menuBar.add(editMenu);
            this.menuBar.add(viewMenu);
        }

        this.menuBar.remove(coreCreateMenu);
        this.menuBar.remove(lmCreateMenu);
        this.menuBar.remove(systemCreateMenu);
        this.menuBar.remove(helpMenu);

        JMenuItem item = null;
        // the following create menus will change with the current view change
        if (whichDatabase == DatabaseTypes.CORE_DB) {
            item = coreCreateMenu;
        } else if (whichDatabase == DatabaseTypes.LM_DB) {
            item = lmCreateMenu;
        } else if (whichDatabase == DatabaseTypes.SYSTEM_DB) {
            item = systemCreateMenu;
        }

        this.menuBar.add(item);
        // this.menuBar.add( toolsMenu );
        this.menuBar.add(helpMenu);

        return menuBar;
    }

    private MessagePanel getMessagePanel() {
        if (this.messagePanel == null) {
            this.messagePanel = new MessagePanel();
            addMessageListener(this.messagePanel);
        }

        return this.messagePanel;
    }

    private JFrame getParentFrame() {
        return (JFrame) SwingUtil.getParentFrame(DatabaseEditor.this.getContentPane());
    }

    private JSplitPane getSplitPane() {
        if (this.splitPane == null) {
            this.splitPane = new JSplitPane();
            this.splitPane.setRightComponent(getDeskTopFrameScrollPane()); // getDesktopPane()
                                                                           // );
            this.splitPane.setLeftComponent(getTreeViewPanel());
            this.splitPane.setDividerLocation(230);
        }

        return this.splitPane;
    }

    private JTree getTree() {
        return getTreeViewPanel().getTree();
    }

    private DBEditorTreePopUpMenu getTreeNodePopupMenu() {

        if (treeNodePopUpMenu == null) {
            treeNodePopUpMenu = new DBEditorTreePopUpMenu();
            treeNodePopUpMenu.setName("TreeNodePopupMenu");
        }
        return treeNodePopUpMenu;
    }

    private TreeViewPanel getTreeViewPanel() {

        if (this.treeViewPanel == null) {
            treeViewPanel = new TreeViewPanel();
            getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
            getTree().setLargeModel(true);
        }
        return this.treeViewPanel;
    }

    private int getVisibleEditorFrames() {
        synchronized (getInternalEditorFrames()) {
            int count = 0;
            for (int i = 0; i < getInternalEditorFrames().length; i++) {
                if (getInternalEditorFrames()[i].isVisible()) {
                    count++;
                }
            }

            return count;
        }

    }

    /**
     * Handles incoming database change messages.
     */
    @Override
    public void handleDBChangeMsg(final DBChangeMsg msg, final LiteBase liteBase) {
        // see if the message originated from us
        if (!(msg.getSource().equals(CtiUtilities.DEFAULT_MSG_SOURCE))) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    StringBuffer txtMsg = new StringBuffer(msg.getDbChangeType() + " Database Change Message received from: " + msg.getUserName() + " at " + msg.getSource());

                    if (!SwingUtilities.isEventDispatchThread()) {
                        log.error("oops");
                    }

                    synchronized (getInternalEditorFrames()) {
                        for (int i = 0; i < getInternalEditorFrames().length; i++) {
                            // Be sure we have an owner tree node for each editor frame.
                            JTreeEditorFrame frame = getInternalEditorFrames()[i];
                            if (frame != null && frame.getOwnerNode() != null) {
                                PropertyPanel current = (PropertyPanel) getEditorFrame(frame.getOwnerNode()).getContentPane();
                                // handle the GUI change in a seperate location
                                DBChangeGUIHandler changeGUIHandler = new DBChangeGUIHandler(current, txtMsg);
                                changeGUIHandler.handleGUIChange(msg);
                            }
                        }
                    }

                    // If we get an id of zero, then refresh the whole thing including cache.
                    if (msg.getId() == DBChangeMsg.RELOAD_ALL) {
                        // refresh the cache and the connections state
                        DefaultDatabaseCache.getInstance().releaseAllCache();

                        // do the actual refresh of the tree
                        getTreeViewPanel().refresh();
                    } else {
                        // tell our tree we may need to change the display
                        updateTreePanel(liteBase, msg.getDbChangeType());
                    }

                    // display a message on the message panel telling us about this event...
                    //  Only if its a Device ChangeOR INSERT/UPDATE...other wise don't bother printing out Point Add messages.
                    if (msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB || msg.getDbChangeType() == DbChangeType.DELETE || msg.getDbChangeType() == DbChangeType.UPDATE) {
                        fireMessage(new MessageEvent(DatabaseEditor.this,
                                                     txtMsg.toString(),
                                                     MessageEvent.INFORMATION_MESSAGE));
                    }
                }
            });
        }
    }

    /**
     * Called whenever the part throws an exception.
     */
    private void handleException(Throwable exception) {
        Throwable rootCause = CtiUtilities.getRootCause(exception);
        fireMessage(new MessageEvent(DatabaseEditor.this, rootCause.getMessage(), MessageEvent.ERROR_MESSAGE));
        JOptionPane.showMessageDialog(getParentFrame(),
                                      "An unexpected error has occurred: \n" + rootCause,
                                      "Uncaught Exception",
                                      JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void handlePopUpEvent(GenericEvent event) {
        if (event.getSource() == DatabaseEditor.this.getTreeNodePopupMenu()) {
            switch (event.getEventId()) {
            case DBEditorTreePopUpMenu.DELETE_TREENODE:
                executeDeleteButton_ActionPerformed(new ActionEvent(this,
                                                                    DBEditorTreePopUpMenu.DELETE_TREENODE,
                                                                    "delete"));
                break;

            case DBEditorTreePopUpMenu.EDIT_TREENODE:
                executeEditButton_ActionPerformed(new ActionEvent(this, DBEditorTreePopUpMenu.EDIT_TREENODE, "edit"));
                break;

            case DBEditorTreePopUpMenu.COPY_TREENODE:
                executeCopyButton_ActionPerformed(new ActionEvent(this, DBEditorTreePopUpMenu.COPY_TREENODE, "copy"));
                break;

            case DBEditorTreePopUpMenu.CHANGE_TYPE_TREENODE:
                executeChangeTypeButton_ActionPerformed(new ActionEvent(this,
                                                                        DBEditorTreePopUpMenu.CHANGE_TYPE_TREENODE,
                                                                        "change type"));
                break;

            case DBEditorTreePopUpMenu.SORT_BY_NAME:
                executeSortByNameButton_ActionPerformed(new ActionEvent(this,
                                                                        DBEditorTreePopUpMenu.SORT_BY_NAME,
                                                                        "sort by name"));
                break;

            case DBEditorTreePopUpMenu.SORT_BY_OFFSET:
                executeSortByOffsetButton_ActionPerformed(new ActionEvent(this,
                                                                          DBEditorTreePopUpMenu.SORT_BY_OFFSET,
                                                                          "sort by offset"));
                break;

            default:
                throw new RuntimeException("Unknown eventId received from " + event.getSource().getClass().getName() + ", id = " + event.getEventId());
            }
        }

    }

    private void initConnections() {
        // add the listeners for our popUp menu
        MouseListener listener = new PopUpMenuShower(getTreeNodePopupMenu());
        getTree().addMouseListener(listener);
        getTreeNodePopupMenu().addPopupMenuListener(this);
        getTreeNodePopupMenu().addPopUpEventListener(this);

        AsyncDynamicDataSource dataSource = (AsyncDynamicDataSource) YukonSpringHook.getBean("asyncDynamicDataSource");
        dataSource.addDBChangeLiteListener(this);

        // add the mouselistener for the JTree
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getSource() == editMenu) {
                    popupMenuWillBecomeVisible(new PopupMenuEvent(DatabaseEditor.this.getTreeNodePopupMenu()));
                } else {
                    int selRow = getTree().getRowForLocation(e.getX(), e.getY());

                    if (selRow != -1) {
                        // be sure this is not a multi selection attempt
                        if (!e.isShiftDown() && !e.isControlDown() && getTree().getSelectionCount() <= 1) {
                            getTree().setSelectionRow(selRow);
                        }

                        if (e.getClickCount() == 2) {
                            executeEditButton_ActionPerformed(new ActionEvent(e.getSource(),
                                                                              e.getID(),
                                                                              "MouseDBLClicked"));
                        }
                    }
                }

            }
        };

        getTree().addMouseListener(ml);
        editMenu.addMouseListener(ml);

    }

    private void initialize(JRootPane rootPane) {

        rootPane.setContentPane(getContentPane());

        // make this call so it creates the minimal amount of InternalEditors
        getInternalEditorFrames();

        setDatabase(DatabaseTypes.CORE_DB); // time hog for large DB's!!!!

        // add a loggerEventListern for logging database editor events
        addMessageListener(getLoggerEventListener());
        owner = SwingUtil.getParentFrame(rootPane);

        // get all the config values read in
        readConfigParameters();

        // connect to dispatch
        getConnToDispatch();

        initConnections();
    }

    private boolean isEditorAlreadyShowing(DefaultMutableTreeNode node) {
        return (getEditorFrame(node) != null);
    }

    public static void main(String[] args) {

        try {
            ClientStartupHelper clientStartupHelper = new ClientStartupHelper();
            clientStartupHelper.setAppName(ApplicationId.DATABASE_EDITOR);
            clientStartupHelper.setRequiredRole(YukonRole.DATABASE_EDITOR.getRoleId());
            clientStartupHelper.setContext("com.cannontech.context.dbeditor");
            clientStartupHelper.setSplashUrl(CtiUtilities.DBEDITOR_SPLASH);

            // creates a stupid anonymous class to create a unique class name for the main frame
            // the ClientStartupHelper will use this for picking a Preference node
            final JFrame f = new JFrame("Yukon Database Editor [Not Connected to Dispatch]") {
            };
            clientStartupHelper.setParentFrame(f);
            f.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

            f.setIconImages(getIconsImages());

            clientStartupHelper.doStartup();

            if (ClientSession.getInstance().getUser().getUserID() == UserUtils.USER_ADMIN_ID) {
                isSuperuser = true;
            }

            DatabaseEditor editor = new DatabaseEditor();
            f.addWindowListener(editor);

            editor.displayDatabaseEditor(f.getRootPane());

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    f.setVisible(true);
                }
            });

        } catch (Throwable t) {
            System.out.println("Unable to startup");
            t.printStackTrace();
            System.exit(-1);
        }

    }

    public void mouseClicked(MouseEvent event) {

        // If there was a double click open a new edit window
        if (event.getSource() == DatabaseEditor.this.getTree() && event.getClickCount() == 2) {
            executeEditButton_ActionPerformed(new ActionEvent(event.getSource(), event.getID(), "MouseDBLClicked"));
        }
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent event) {
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent event) {
        if (event.getSource() == DatabaseEditor.this.getTreeNodePopupMenu()) {
            // If nothing selected, default and return
            if (getTreeViewPanel().getSelectedNodes() == null) {
                getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
                getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
                getTreeNodePopupMenu().getJMenuItemDelete().setEnabled(false);
                getTreeNodePopupMenu().getJMenuItemEdit().setEnabled(false);
                getTreeNodePopupMenu().getJMenuSortAllPointsBy().setEnabled(true);

                editMenu.changeTypeMenuItem.setEnabled(false);
                editMenu.copyMenuItem.setEnabled(false);
                editMenu.deleteMenuItem.setEnabled(false);
                editMenu.editMenuItem.setEnabled(false);
                return;
            }

            // defaults value here
            getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(true);
            getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(true);
            getTreeNodePopupMenu().getJMenuItemDelete().setEnabled(true);
            getTreeNodePopupMenu().getJMenuItemEdit().setEnabled(true);
            getTreeNodePopupMenu().getJMenuSortAllPointsBy().setEnabled(false);

            editMenu.changeTypeMenuItem.setEnabled(true);
            editMenu.copyMenuItem.setEnabled(true);
            editMenu.deleteMenuItem.setEnabled(true);
            editMenu.editMenuItem.setEnabled(true);

            // check for multi select
            if (getTreeViewPanel().getSelectedNodes().length > 1) {
                getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
                getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);

                editMenu.changeTypeMenuItem.setEnabled(false);
                editMenu.copyMenuItem.setEnabled(false);
            } else {
                DefaultMutableTreeNode selectedNode = getTreeViewPanel().getSelectedNode();

                if (selectedNode != null) {
                    if (selectedNode instanceof DummyTreeNode || selectedNode.isRoot()) {
                        getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
                        getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
                        getTreeNodePopupMenu().getJMenuItemDelete().setEnabled(false);
                        getTreeNodePopupMenu().getJMenuItemEdit().setEnabled(false);

                        editMenu.changeTypeMenuItem.setEnabled(false);
                        editMenu.copyMenuItem.setEnabled(false);
                        editMenu.deleteMenuItem.setEnabled(false);
                        editMenu.editMenuItem.setEnabled(false);

                        if (!selectedNode.isRoot()) {
                            getTreeNodePopupMenu().getJMenuSortAllPointsBy().setEnabled(true);
                        }
                    }

                    if (selectedNode.getUserObject() instanceof LiteYukonPAObject) {
                        LiteYukonPAObject litYuk = (LiteYukonPAObject) selectedNode.getUserObject();

                        PaoType paoType = litYuk.getPaoType();

                        if ((paoType.getPaoClass() == PaoClass.CAPCONTROL && !paoType.isCbc()) || 
                                paoType == PaoType.LM_GROUP_RIPPLE || 
                                paoType == PaoType.MACRO_GROUP || 
                                paoType.getPaoClass() == PaoClass.LOADMANAGEMENT) {
                            getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
                            editMenu.changeTypeMenuItem.setEnabled(false);
                        } else if (litYuk.getPaoType().getPaoCategory() == PaoCategory.CUSTOMER || litYuk.getPaoType().getPaoClass() == PaoClass.SYSTEM) {
                            getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
                            getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
                            editMenu.changeTypeMenuItem.setEnabled(false);
                            editMenu.copyMenuItem.setEnabled(false);
                        }

                    } else if (selectedNode.getUserObject() instanceof LiteNotificationGroup || 
                            selectedNode.getUserObject() instanceof LiteContactNotification || 
                            selectedNode.getUserObject() instanceof LiteAlarmCategory || 
                            (selectedNode.getUserObject() instanceof LiteYukonPAObject && 
                                    ((LiteYukonPAObject) selectedNode.getUserObject()).getPaoType() == PaoType.CAP_CONTROL_SUBBUS)) {
                        getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
                        getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
                        editMenu.changeTypeMenuItem.setEnabled(false);
                        editMenu.copyMenuItem.setEnabled(false);
                    }
                }
            }
        }
    }

    private void readConfigParameters() {

        try {
            /*
             * If getRolePropertyValue fails (returns ""), we will get a
             * NumberFormatExcption, handled below.
             */
            decimalPlaces = Integer.parseInt(ClientSession.getInstance()
                                                          .getRolePropertyValue(YukonRoleProperty.DECIMAL_PLACES));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            decimalPlaces = 3;
        }

        // Decide whether to put the billing file option into the create (core) menu
        try {
            activateBilling = YukonSpringHook.getBean(GlobalSettingDao.class).getBoolean(GlobalSettingType.WIZ_ACTIVATE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        if (!activateBilling) {
            coreCreateMenu.remove(coreCreateMenu.billingGroupMenuItem);
        }

        // Decide which items to put into the view menu
        boolean showLm = false;
        boolean showSystem = false;

        try {
            showLm = ClientSession.getInstance()
                                  .getRolePropertyValue(YukonRoleProperty.DBEDITOR_LM)
                                  .trim()
                                  .equalsIgnoreCase("TRUE");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        try {
            showSystem = ClientSession.getInstance()
                                      .getRolePropertyValue(YukonRoleProperty.DBEDITOR_SYSTEM)
                                      .trim()
                                      .equalsIgnoreCase("TRUE");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // check whether the user should be able to see anything regarding
        // logins in the dbeditor
        try {
            accessOfLoginNotAllowed = ClientSession.getInstance()
                                                   .getRolePropertyValue(YukonRoleProperty.PERMIT_LOGIN_EDIT)
                                                   .trim()
                                                   .equalsIgnoreCase("FALSE");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // shouldn't be allowed to see login stuff
        if (accessOfLoginNotAllowed && !isSuperuser) {
            systemCreateMenu.remove(systemCreateMenu.roleGroupMenuItem);
            systemCreateMenu.remove(systemCreateMenu.userMenuItem);
            systemCreateMenu.remove(systemCreateMenu.userGroupMenuItem);
        }

        if (!showLm) {
            viewMenu.remove(viewMenu.lmRadioButtonMenuItem);
        }

        if (!showSystem) {
            viewMenu.remove(viewMenu.systemRadioButtonMenuItem);
        }

    }

    public void removeMessageListeners(MessageEventListener listener) {

        if (messageListeners.contains(listener)) {
            messageListeners.removeElement(listener);
        }
    }

    private void removeUnneededEditorFrames() {
        synchronized (getInternalEditorFrames()) {
            for (int i = 0; i < getInternalEditorFrames().length; i++) {
                if (getInternalEditorFrames().length > INITIAL_EDITOR_COUNT) {
                    if ((getInternalEditorFrames()[i]).isVisible()) {
                        continue;
                    } else {
                        ArrayList<JTreeEditorFrame> list = new ArrayList<JTreeEditorFrame>(getInternalEditorFrames().length);
                        for (int j = 0; j < getInternalEditorFrames().length; j++) {
                            list.add(getInternalEditorFrames()[j]);
                        }

                        // we removed the unneeded frame
                        list.remove(i);

                        // remove the excess frame
                        JTreeEditorFrame[] frames = new JTreeEditorFrame[getInternalEditorFrames().length - 1];

                        editorFrames = list.toArray(frames);
                    }
                } else {
                    return;
                }
            }
        }
    }

    @Override
    public void selectionPerformed(PropertyPanelEvent event) {

        // these events tells us the user modified a DB object before clicking the OK/APPLY button
        if (event.getID() == PropertyPanelEvent.EVENT_DB_INSERT) {
            DBPersistent dbPersist = (DBPersistent) event.getDataChanged();
            insertDBPersistent(dbPersist);
            return;

        } else if (event.getID() == PropertyPanelEvent.EVENT_DB_DELETE) {
            DBPersistent dbPersist = (DBPersistent) event.getDataChanged();
            deleteDBPersistent(dbPersist);
            return;

        } else if (event.getID() == PropertyPanelEvent.EVENT_DB_UPDATE) {
            DBPersistent dbPersist = (DBPersistent) event.getDataChanged();
            updateDBPersistent(dbPersist);
            return;
        }

        if (!(event.getSource() instanceof PropertyPanel)) {
            return;
        }

        PropertyPanel panel = (PropertyPanel) event.getSource();
        int frameLocation = getFrameLocationByPanel(panel);

        // if the input entered is not legit and the user wants to commit the changes
        if ((event.getID() == PropertyPanelEvent.APPLY_SELECTION || event.getID() == PropertyPanelEvent.OK_SELECTION) && !panel.isInputValid()) {
            if (panel.getErrorString() != null) {
                JOptionPane.showMessageDialog(panel, panel.getErrorString(), "Input Error", JOptionPane.WARNING_MESSAGE);
            }

            return;
        }

        // result of our DB update
        boolean updateResult = false;

        // Update the object on an apply or ok
        if (event.getID() == PropertyPanelEvent.APPLY_SELECTION || event.getID() == PropertyPanelEvent.OK_SELECTION) {
            try {
                final DBPersistent object = (DBPersistent) panel.getValue(null);

                if (panel.hasChanged()) {
                    panel.setChanged(false);

                    if (DatabaseEditorUtil.showUpdateRouteName(object)) {
                        DatabaseEditorUtil.updateRouteName(this, panel, object);
                    }

                    if (DatabaseEditorUtil.isDisconnectCollarCompatible(object)) {
                        DatabaseEditorUtil.updateDisconnectStatus(this, panel, object);
                    }

                    updateResult = updateDBPersistent(object);

                    if (updateResult) {
                        panel.postSave(object);

                        if (event.getID() == PropertyPanelEvent.APPLY_SELECTION) {
                            /* APPLY ONLY EVENTS GO HERE */
                            synchronized (getInternalEditorFrames()) {
                                getInternalEditorFrames()[frameLocation].setTitle(object.toString() + " : " + panel.toString());
                            }
                        }

                    } else {
                        // try and restore the original values to the Editor Panels
                        panel.setValue(panel.getOriginalObjectToEdit());
                        panel.repaint();
                        return; // the SQLUpdate failed, lets get out of here
                    }
                }
            } catch (EditorInputValidationException e) {
                JOptionPane.showMessageDialog(panel, e.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        // Remove the frame here
        if (event.getID() == PropertyPanelEvent.CANCEL_SELECTION || event.getID() == PropertyPanelEvent.OK_SELECTION) {
            if (event.getID() == PropertyPanelEvent.CANCEL_SELECTION) {
                /* CANCEL ONLY EVENTS GO HERE */
            }

            panel.setChanged(false);
            panel.disposeValue();
            synchronized (getInternalEditorFrames()) {
                getInternalEditorFrames()[frameLocation].setVisible(false); // .dispose() ?? not sure!!???
            }
            if (frameLocation >= INITIAL_EDITOR_COUNT) {
                removeUnneededEditorFrames();
            }

            desktopPane.repaint();
        }

    }

    public boolean insertDBPersistent(DBPersistent newItem) {
        boolean success = false;

        try {
            // insert the newly created item into the DB
            Transaction<DBPersistent> t = Transaction.createTransaction(Transaction.INSERT, newItem);
            newItem = t.execute();

            String messageString = newItem + " inserted successfully into the database.";
            fireMessage(new MessageEvent(this, messageString));

            // fire DBChange messages out to Dispatch
            generateDBChangeMsg(newItem, DbChangeType.ADD);

            success = true;
        } catch (CancelInsertException ci) {
            // inside the getValue(), this exception was thrown
        } catch (TransactionException e) {
            log.error(e.getMessage(), e);
            String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
            String messageString = "Error inserting " + newItem + " into the database.  Error received:  " + cause.trim();
            fireMessage(new MessageEvent(this, messageString, MessageEvent.ERROR_MESSAGE));
        } catch (DataAccessException e) {
            /* Handle the DataAccessExceptions that daos might throw. */
            /*
             * Usually catching DataAccessException is bad practice, but in this
             * case we want to catch all problems occuring due to dao method
             * failures.
             */
            log.error(e.getMessage(), e);
            String messageString = " Error inserting " + newItem + " in the database.  Error received: " + e.getMessage();
            fireMessage(new MessageEvent(this, messageString, MessageEvent.ERROR_MESSAGE));
            return false;
        }

        return success;
    }

    @Override
    public void selectionPerformed(WizardPanelEvent event) {

        // these event tells us the user modified a DB object before clicking the finish button
        if (event.getID() == PropertyPanelEvent.EVENT_DB_INSERT) {
            DBPersistent dbPersist = (DBPersistent) event.getDataChanged();
            insertDBPersistent(dbPersist);
            return;
        } else if (event.getID() == PropertyPanelEvent.EVENT_DB_DELETE) {
            DBPersistent dbPersist = (DBPersistent) event.getDataChanged();
            deleteDBPersistent(dbPersist);
            return;
        } else if (event.getID() == PropertyPanelEvent.EVENT_DB_UPDATE) {
            DBPersistent dbPersist = (DBPersistent) event.getDataChanged();
            updateDBPersistent(dbPersist);
            return;
        }

        if (!(event.getSource() instanceof WizardPanel)) {
            return;
        }

        boolean objTypeChange = false;
        boolean successfullInsertion = false;
        boolean selectInTree = true;
        boolean selectWorked = false;
        DBPersistent newItem = null;

        if (event.getID() == WizardPanelEvent.FINISH_SELECTION) {
            if (changingObjectType) {
                objTypeChange = executeChangeObjectType(event);
            } else {
                WizardPanel p = (WizardPanel) event.getSource();

                // p.getValue(null) may throw a CancelInsertException
                newItem = (DBPersistent) p.getValue(null);

                // Hack - Don't try to select new Categories or TOUSchedules created in the device config UI
                selectInTree = !(newItem instanceof TOUSchedule) || (newItem instanceof TOUSchedule && currentDatabase == DatabaseTypes.SYSTEM_DB);

                // DNP devices need to be assigned the default DNP configuration on creation!
                // If the device is of type RTU_MODBUS, it should not be assigned DNP configuration.
                if (newItem instanceof DNPBase && ((DNPBase) newItem).getPaoType() != PaoType.RTU_MODBUS) {
                    DeviceConfigurationDao configurationDao = YukonSpringHook.getBean("deviceConfigurationDao",
                                                                                      DeviceConfigurationDao.class);

                    DeviceConfiguration config = configurationDao.getDefaultDNPConfiguration();
                    DNPConfiguration defaultConfig = configurationDao.getDnpConfiguration(config);

                    DNPBase dnpBase = (DNPBase) newItem;
                    dnpBase.setDnpConfiguration(defaultConfig);
                }

                // try to insert the object into the DB
                successfullInsertion = insertDBPersistent(newItem);

                if (successfullInsertion) {
                    p.postSave(newItem);
                }

                // IPC meters also create a comm channel. Update the tree to
                // pick it up.
                if (newItem instanceof SmartMultiDBPersistent && ((SmartMultiDBPersistent) newItem).getOwnerDBPersistent() instanceof IPCMeter) {
                    viewMenuRefreshAction();
                }

                // Bring the editor up for the newly created Object
                if (successfullInsertion && selectInTree) {
                    getTreeViewPanel().selectObject(newItem);
                    selectWorked = true;
                }

            }
        }

        if (event.getID() == WizardPanelEvent.CANCEL_SELECTION || 
                (event.getID() == WizardPanelEvent.FINISH_SELECTION && successfullInsertion) || 
                (event.getID() == WizardPanelEvent.FINISH_SELECTION) && objTypeChange) {
            changingObjectType = false;

            // Loop through all the frames on the desktopPane and find out which one contains the WizardPanel responsible for this event
            WizardPanel p = (WizardPanel) event.getSource();

            JInternalFrame frames[] = this.desktopPane.getAllFrames();

            for (int i = 0; i < frames.length; i++) {
                if (frames[i].getContentPane() == p) {
                    // Found a panel so kill the frame
                    this.desktopPane.remove(frames[i]);
                    frames[i].dispose();
                    this.desktopPane.repaint();
                    break;
                }
            }
        }

        if ((successfullInsertion || objTypeChange) && selectInTree) {
            if (selectWorked || objTypeChange) {
                showEditorSelectedObject();
            } else {
                showEditor(newItem);
            }
        }
    }

    public void setDatabase(DatabaseTypes whichDatabase) {
        // First check if there might be changes to update then remove any current editors that are opened
        exitConfirm();

        TreeModelEnum[] models = null;

        // Get a ref to the rootpane
        JFrame frame = (JFrame) SwingUtil.getParentFrame(getContentPane());
        JRootPane rPane = frame.getRootPane();

        switch (whichDatabase) {
        case CORE_DB:
            this.menuBar = getMenuBar(whichDatabase);
            viewMenu.coreRadioButtonMenuItem.setSelected(true);
            models = CORE_MODELS;
            break;
        case LM_DB:
            this.menuBar = getMenuBar(whichDatabase);
            viewMenu.lmRadioButtonMenuItem.setSelected(true);
            // hex value representing some privileges of the user on this machine
            long show_protocol;
            try {
                show_protocol = Long.parseLong(ClientSession.getInstance().getRolePropertyValue(YukonRoleProperty.DATABASE_EDITOR_OPTIONAL_PRODUCT_DEV), 16);
            } catch (Exception e) {
                show_protocol = 0;
            }
            if ((show_protocol & ClientRights.SHOW_ADDITIONAL_PROTOCOLS) != 0) {
                models = LM_MODELS_WITH_SA;
            } else {
                models = LM_MODELS;
            }
            break;
        case SYSTEM_DB:
            this.menuBar = getMenuBar(whichDatabase);
            viewMenu.systemRadioButtonMenuItem.setSelected(true);

            // check to see if user is allowed to see login stuff information at all
            if (accessOfLoginNotAllowed && !isSuperuser) {
                models = NONLOGIN_SYSTEM_MODELS;
                break;
            }

            models = SYSTEM_MODELS;
            break;
        }

        int length = models.length;

        LiteBaseTreeModel[] newModels = new DBTreeModel[length];
        for (int i = 0; i < models.length; i++) {
            LiteBaseTreeModel treeModel = TreeModelEnum.create(models[i]);
            if (treeModel instanceof FrameAware) {
                ((FrameAware) treeModel).setParentFrame(frame);
            }
            newModels[i] = treeModel;
        }

        getTreeViewPanel().setTreeModels(newModels);
        if (models == CORE_MODELS) {
            getTreeViewPanel().setSelectedSortByIndex(Arrays.asList(models).indexOf(TreeModelEnum.DEVICE));
        }
        if (models == LM_MODELS || models == LM_MODELS_WITH_SA) {
            getTreeViewPanel().setSelectedSortByIndex(Arrays.asList(models).indexOf(TreeModelEnum.LMGROUPS));
        }
        if (models == SYSTEM_MODELS) {
            getTreeViewPanel().setSelectedSortByIndex(Arrays.asList(models).indexOf(TreeModelEnum.ROLE_GROUPS));
        }
        if (models == NONLOGIN_SYSTEM_MODELS) {
            getTreeViewPanel().setSelectedSortByIndex(Arrays.asList(models).indexOf(TreeModelEnum.NOTIFICATION_GROUP));
        }

        rPane.setJMenuBar(this.menuBar);
        rPane.revalidate();
        rPane.repaint();
    }

    private void showChangeTypeWizardPanel(WizardPanel wizard) {

        // Set the cursor to wait
        Frame owner = SwingUtil.getParentFrame(this.desktopPane);
        Cursor savedCursor = owner.getCursor();
        owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        wizard.addWizardPanelListener(this);

        JInternalFrame f = new JInternalFrame();

        f.setContentPane(wizard);
        f.setResizable(true);
        f.setSize((int) wizard.getActualSize().getWidth(), (int) wizard.getActualSize().getHeight());// 410,470);

        this.desktopPane.add(f);
        changingObjectType = true;
        wizard.setValue(null);
        f.setVisible(true);
        ImageIcon wizardIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_16));
        f.setFrameIcon(wizardIcon);

        try {
            f.setSelected(true);
        } catch (PropertyVetoException e) {
            // ?
        } finally {
            owner.setCursor(savedCursor);
        }

    }

    private void showCopyWizardPanel(WizardPanel wizard, DBPersistent toCopy) {

        // Set the cursor to wait
        Frame owner = SwingUtil.getParentFrame(this.desktopPane);
        Cursor savedCursor = owner.getCursor();
        owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        wizard.addWizardPanelListener(this);

        JInternalFrame f = new JInternalFrame();

        f.setContentPane(wizard);
        f.setSize(435, 500);
        f.setResizable(true);

        this.desktopPane.add(f);

        wizard.setValue(toCopy);
        ImageIcon wizardIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(DatabaseEditor.DBEDITOR_IMG_16));
        f.setFrameIcon(wizardIcon);
        f.show();

        try {
            f.setSelected(true);
        } catch (PropertyVetoException e) {
            // ?
        } finally {
            owner.setCursor(savedCursor);
        }
    }

    public void showEditorSelectedObject() {
        Frame f = SwingUtil.getParentFrame(this.desktopPane);
        f.validate();

        try {
            executeEditButton_ActionPerformed(new ActionEvent(this, DBEditorTreePopUpMenu.EDIT_TREENODE, "edit"));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            f.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

    }

    private void showWizardPanel(WizardPanel wizard) {

        // Set the cursor to wait
        Frame owner = SwingUtil.getParentFrame(this.desktopPane);
        Cursor savedCursor = owner.getCursor();
        owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));

        wizard.addWizardPanelListener(this);

        JInternalFrame f = new JInternalFrame();

        f.setResizable(true);
        f.setContentPane(wizard);
        f.setSize((int) wizard.getActualSize().getWidth(), (int) wizard.getActualSize().getHeight());// 410,470);

        this.desktopPane.add(f);

        wizard.setValue(null);
        f.setVisible(true);
        ImageIcon wizardIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(DatabaseEditor.DBEDITOR_IMG_16));
        f.setFrameIcon(wizardIcon);

        try {
            f.setSelected(true);
        } catch (PropertyVetoException e) {
            // ?
        } finally {
            owner.setCursor(savedCursor);
        }

    }

    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o instanceof DispatchClientConnection) {
            updateConnectionStatus((IServerConnection) o);
        }
    }

    /**
     * Update the status of out dispatch connection for the user.
     */
    private void updateConnectionStatus(IServerConnection conn) {
        ClientConnection clientConn = (ClientConnection) conn;

        if (conn.isValid()) {
            if (owner != null) {
                owner.setTitle("Yukon Database Editor [Connected to Dispatch@" + clientConn.getConnectionUri().getRawAuthority() + "]");
            }

            if (lastConnToVanGoghStatus != ConnectionStatus.CONNECTED) {
                fireMessage(new MessageEvent(this, "Connection to Dispatch established. " + clientConn.getConnectionUri().getRawAuthority()));

                lastConnToVanGoghStatus = ConnectionStatus.CONNECTED;
                if (owner != null) {
                    owner.repaint();
                }
            }
        } else {
            if (owner != null) {
                owner.setTitle("Yukon Database Editor [Not Connected to Dispatch]");
            }

            if (lastConnToVanGoghStatus == ConnectionStatus.CONNECTED) {
                fireMessage(new MessageEvent(this, "Lost connection to Dispatch. " + clientConn.getConnectionUri().getRawAuthority() + ". Reconnecting.",
                                             MessageEvent.ERROR_MESSAGE));

                lastConnToVanGoghStatus = ConnectionStatus.DISCONNECTED;
                if (owner != null) {
                    owner.repaint();
                }
            } else if (lastConnToVanGoghStatus == ConnectionStatus.UNDEFINED && clientConn.isConnectionFailed()) {
                fireMessage(new MessageEvent(this, "Unable to connect to Dispatch. " + clientConn.getConnectionUri().getRawAuthority() + ". Reconnecting.",
                                             MessageEvent.ERROR_MESSAGE));

                lastConnToVanGoghStatus = ConnectionStatus.DISCONNECTED;
                if (owner != null) {
                    owner.repaint();
                }
            }
        }
    }

    private boolean updateDBPersistent(DBPersistent dbPersistent) {
        try {

            Transaction<DBPersistent> t = Transaction.createTransaction(Transaction.UPDATE, dbPersistent);
            dbPersistent = t.execute();

            // write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
            generateDBChangeMsg(dbPersistent, DbChangeType.UPDATE);

            String messageString = dbPersistent + " updated successfully in the database.";
            fireMessage(new MessageEvent(this, messageString));

        } catch (TransactionException e) {
            /*
             * Handle the normal Transaction exceptions that dbpersistence throw.
             */
            log.error(e.getMessage(), e);
            String messageString = " Error updating " + dbPersistent + " in the database.  Error received: " + e.getMessage();
            fireMessage(new MessageEvent(this, messageString, MessageEvent.ERROR_MESSAGE));
            return false;
        } catch (DataAccessException e) {
            /* Handle the DataAccessExceptions that daos might throw. */
            /*
             * Usually catching DataAccessException is bad practice, but in this
             * case we want to catch all problems occuring due to dao method
             * failures.
             */
            log.error(e.getMessage(), e);
            String messageString = " Error updating " + dbPersistent + " in the database.  Error received: " + e.getMessage();
            fireMessage(new MessageEvent(this, messageString, MessageEvent.ERROR_MESSAGE));
            return false;
        }

        return true;
    }

    /**
     * Helper method to update the tree
     * @param lBase - LiteBase that has changed
     * @param changeType - Type of DBChange
     */
    private void updateTreePanel(LiteBase lBase, DbChangeType dbChangeType) {
        getTreeViewPanel().processDBChange(dbChangeType, lBase);
        getTreeViewPanel().revalidate();
    }

    @Override
    public void windowActivated(WindowEvent event) {
    }

    @Override
    public void windowClosed(WindowEvent event) {
        System.out.println("here");
    }

    @Override
    public void windowClosing(WindowEvent event) {
        if (exitConfirm()) {
            exit();
        }
    }

    @Override
    public void windowDeactivated(WindowEvent event) {
    }

    @Override
    public void windowDeiconified(WindowEvent event) {
    }

    @Override
    public void windowIconified(WindowEvent event) {
    }

    @Override
    public void windowOpened(WindowEvent event) {
    }

    public static DatabaseEditor getInstance() {
        return editor;
    }
}
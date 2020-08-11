package com.cannontech.clientutils.commander;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.tree.TreePath;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.clientutils.commander.model.OutputMessage;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupTreeFactory;
import com.cannontech.common.gui.util.JTextPanePrintable;
import com.cannontech.common.gui.util.TreeViewPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.login.ClientStartupHelper;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.ApplicationId;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileFilter;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.CommandDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.model.CapBankControllerModel;
import com.cannontech.database.model.DeviceMeterGroupModel;
import com.cannontech.database.model.DeviceTreeModel;
import com.cannontech.database.model.EditableExpresscomModel;
import com.cannontech.database.model.EditableSA205Model;
import com.cannontech.database.model.EditableSA305Model;
import com.cannontech.database.model.EditableTextModel;
import com.cannontech.database.model.EditableVersacomModel;
import com.cannontech.database.model.LMGroupsModel;
import com.cannontech.database.model.LiteBaseTreeModel;
import com.cannontech.database.model.TransmitterTreeModel;
import com.cannontech.database.model.TreeModelEnum;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class YukonCommander extends JFrame implements DBChangeLiteListener, ActionListener,
    FocusListener, KeyListener, TreeSelectionListener, MouseListener, Observer {

    private YC yc;
    
    private CommandDao commandDao = YukonSpringHook.getBean(CommandDao.class);
    private PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
    private PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean(PaoDefinitionDao.class);
    private DeviceGroupTreeFactory dgtf = YukonSpringHook.getBean(DeviceGroupTreeFactory.class);
    private AsyncDynamicDataSource dataSource =  YukonSpringHook.getBean(AsyncDynamicDataSource.class);
    
    private TreeModelEnum[] treeModels = null;
    private static final String YC_TITLE = "Commander";
    public static final String HELP_FILE = "Yukon_Commander_Help.chm";
    
    public static final URL COMMANDER_IMG_16 = YukonCommander.class.getResource("/Commander16.png");
    public static final URL COMMANDER_IMG_24 = YukonCommander.class.getResource("/Commander24.png");
    public static final URL COMMANDER_IMG_32 = YukonCommander.class.getResource("/Commander32.png");
    public static final URL COMMANDER_IMG_48 = YukonCommander.class.getResource("/Commander48.png");
    public static final URL COMMANDER_IMG_64 = YukonCommander.class.getResource("/Commander64.png");
    
    public static List<Image> getIconsImages() {
        
        List<Image> iconsImages = new ArrayList<Image>();
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_16));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_24));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_32));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_48));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_64));
        
        return iconsImages;
    }

    private JPanel ivjJFrameContentPane = null;
    private JPanel ivjOutputPanel = null;
    private TreeViewPanel ivjTreeViewPanel = null;
    private CommandPanel ivjCommandPanel = null;
    private SerialRoutePanel ivjSerialRoutePanel = null;
    private JSplitPane ivjSplitPane = null;
    private CommandLogPanel ivjCommandLogPanel = null;
    private JLabel ivjCGPMode = null;
    private ClearPrintButtonPanel ivjClearPrintButtons = null;
    private AdvancedOptionsPanel advOptsPanel = null;
    private LocateRouteDialog locRouteDialog = null;
    private DownloadTOUSchedulePanel downloadTOUPanel = null;
    private YCCommandMenu ivjYCCommandMenu = null;
    private YCFileMenu ivjYCFileMenu = null;
    private YCHelpMenu ivjYCHelpMenu = null;
    private YCViewMenu ivjYCViewMenu = null;
    private JMenuBar ivjYukonCommanderJMenuBar = null;
    private JPanel ivjClearPrintPanel = null;
    private JScrollPane ivjDebugOutputScrollPane = null;
    private JScrollPane ivjDisplayOutputScrollPane = null;
    private JPanel ivjExecutionPanel = null;
    private JPanel ivjNavigatorPanel = null;
    private JSplitPane ivjOutputSplitPane = null;
    
    private JTextPane ivjDebugOutputTextPane = null;
    private JTextPane ivjDisplayOutputTextPane = null;

    private final String CLEAR_OUTPUT_DISPLAY = "Clear Display";
    private final String PRINT_OUTPUT_DISPLAY = "Print Display";

    private final String CLEAR_OUTPUT_DEBUG = "Clear Debug";
    private final String PRINT_OUTPUT_DEBUG = "Print Debug";
    
    private class WriteOutput implements Runnable {
        
        private OutputMessage message = null; 
        private JTextPane textPane = null;
        private String style;
        
        public WriteOutput(JTextPane textPane_, OutputMessage message_) {
            super();
            this.textPane = textPane_;
            this.message = message_;
            this.style = message.getMessageType().getStyleString();
        }

        public void run() {
            try {
                
                HTMLDocument doc = (HTMLDocument)textPane.getStyledDocument();
                int beginOffset = doc.getLength();
                HTMLEditorKit kit = (HTMLEditorKit)textPane.getEditorKit();
                StringReader reader = new StringReader(message.getText());
                kit.read(reader, doc, doc.getLength());
                int endOffset = doc.getLength();
                reader.close();
                // Set text in the range [5, 7) red
                doc.setCharacterAttributes(beginOffset, endOffset - beginOffset, textPane.getStyle("Font"), true);
                if (style != null) {
                    doc.setCharacterAttributes(beginOffset, endOffset - beginOffset, textPane.getStyle(style), false);
                }
                
                textPane.setCaretPosition(doc.getLength());
            } catch (javax.swing.text.BadLocationException ble) {
                ble.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }    
    
    public YukonCommander() {
        super();
        initialize();
    }
    
    /**
     * Displays the about information.
     */
    private void about() {
        Frame f = (Frame) SwingUtil.getParentFrame(YukonCommander.this.getContentPane());
        AboutDialog aboutDialog = new AboutDialog(f, "About Commander", true);

        aboutDialog.setLocationRelativeTo(f);
        aboutDialog.setValue(null);
        aboutDialog.setVisible(true);
    }
    
    private String loadPromptValue(String valueString, Component parent ) {
        
        int promptIndex = valueString.indexOf(CommandDao.DEFAULT_VALUE_PROMPT);
        while (promptIndex > -1) {
            
            // Clear all blanks at the beginning of the prompt text
            String promptString = valueString.substring(promptIndex+1).trim();
            
            char charAt = promptString.charAt(0);
            if (charAt == CommandDao.DEFAULT_VALUE_PROMPT) {
                // Found a double '?', Remove one of the ?s and look for another prompt value
                valueString = valueString.substring(0, promptIndex) + valueString.substring(promptIndex + 1);
                
                promptIndex = promptIndex + 1;
                String substring = valueString.trim().substring(promptIndex);
                int nextPromptIndex = substring.indexOf(CommandDao.DEFAULT_VALUE_PROMPT);
                if (nextPromptIndex == -1) {
                    promptIndex = nextPromptIndex;
                } else {
                    promptIndex += nextPromptIndex;
                }
                continue;
            }
            
            int endIndex = -1;
            String stringEnding = "";
            if (charAt == '\'' || charAt == '\"') {
                // Quoted prompt string
                promptString = promptString.substring(1); // Remove quote from beginning of string
                endIndex = promptString.indexOf('\''); // Locate ending quote
                if (endIndex < 0) { 
                    endIndex = promptString.indexOf('\"');
                }
            } else {
                endIndex = promptString.indexOf(" ");    // Locate string end, space is separator
                
                if (promptString.indexOf('\'') > 0 && (promptString.indexOf('\'')< endIndex || endIndex < 0)) {
                    endIndex = promptString.indexOf('\'');
                } else if( promptString.indexOf('\"') > 0 && (promptString.indexOf('\"')< endIndex || endIndex < 0)) {
                    endIndex = promptString.indexOf('\"');
                }
                
                if (endIndex > 0) {
                    // Add the char back to the beginning of the command.
                    stringEnding +=promptString.charAt(endIndex);
                }
            }
            
            if (endIndex > 0) {
                // Store the end of the command string.
                stringEnding += promptString.substring(endIndex+1);
                // Truncate the end of the string to get just the prompt value.
                promptString = promptString.substring(0, endIndex);
            }
                    
            String value = JOptionPane.showInputDialog(parent, 
                                                       "Command: " + valueString + "\n\n" + promptString + ": ", 
                                                       "Enter the parameter value", 
                                                       JOptionPane.QUESTION_MESSAGE );
            if ( value != null) {
                valueString = (valueString.substring(0, promptIndex) + value + stringEnding).trim();
                int nextIndex = promptIndex + value.length();
                if(nextIndex < valueString.length()) {
                    promptIndex = valueString.trim().substring(nextIndex).indexOf(CommandDao.DEFAULT_VALUE_PROMPT);    //look for another prompt value
                    if(promptIndex != -1) {
                        promptIndex += nextIndex;
                    }
                } else {
                    promptIndex = -1;
                }
            } else {
                // CANCEL
                return null;
            }
        }
        
        return valueString;
    }
    
    public void actionPerformed(ActionEvent event) {
        
        Object source = event.getSource();
        
        if (source == getTreeViewPanel().getSortByComboBox()) {
            treeModelChanged();
        } else if (source == getYCViewMenu().reloadMenuItem) {
            reloadDevices();
        } else if (source == getSerialRoutePanel().getSerialTextField()) {
            serialNumberAction();
        } else if (source == getSerialRoutePanel().getRouteComboBox()) {
            
            Object selected = getSerialRoutePanel().getRouteComboBox().getSelectedItem();
            if (selected instanceof LiteYukonPAObject) {
                setRouteID(((LiteYukonPAObject) selected).getYukonID());
            } else {
                setRouteID(-1); // set it to an invalid route
            }
            
            CTILogger.info(selected);
            
        } else if (source == getCommandPanel().getExecuteButton() 
                || source == getYCCommandMenu().executeMenuItem) {
            
            getYCFileMenu().updateRecentList(getYC().getTreeItem());
            String commandString = (String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim();
            try {
                setCommand(commandString);
                if (isValidSetup()) {
                    getCommandPanel().enter(getCommand());
                    getYC().executeCommand();
                }
            } catch (PaoAuthorizationException e) {
                update(yc, new OutputMessage(OutputMessage.DEBUG_MESSAGE, "\n ** You do not have permission to execute command: " 
                        + e.getPermission(), MessageType.ERROR));
            }
        } else if (source == getCommandPanel().getStopButton() 
                || source == getYCCommandMenu().stopMenuItem) {
            if (!getPilConn().isValid()) {
                getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
                return;
            }
            getYC().stop();
            getDebugOutputTextPane().setCaretPosition(getDebugOutputTextPane().getDocument().getEndPosition().getOffset() - 1);
            
        } else if (source == getCommandPanel().getAvailableCommandsComboBox()) {
            
            if (getCommandPanel().getAvailableCommandsComboBox().getSelectedIndex() > 0) { // 0 is default "select"
                String rawCommandString = getYC().getCommandFromLabel(getCommandPanel().getAvailableCommandsComboBox().getSelectedItem().toString());
                String commandString = loadPromptValue(rawCommandString.trim(), this);
                if (commandString != null) { // null is a cancel from prompt
                    getCommandPanel().getExecuteCommandComboBoxTextField().setText(commandString);
                    getCommandPanel().getExecuteButton().requestFocusInWindow();
                }
            }
        } else if (source == getYCCommandMenu().editCustomCommandFile) {
            if (getTreeViewPanel().getSelectedItem() == null) {
                getCommandLogPanel().addLogElement(" ** Warning: Please make a selection from the tree.");
            } else if (getTreeViewPanel().getSelectedNode().getParent() == null) {
                getCommandLogPanel().addLogElement(" ** Warning: Please select a specific tree item");
            } else {
                DeviceTypeCommandSetupPanel commandEditPanel = new DeviceTypeCommandSetupPanel(getYC().getDeviceType());
                commandEditPanel.setDialogTitle("DeviceType: " + getYC().getDeviceType());
                commandEditPanel.showCommandSetup(this);
                // set the deviceType in order to reload the deviceTypeCommands
                getYC().setDeviceType(getYC().getDeviceType());
                // update the CommandExecute panel
                updateCommandSelection();
            }
        } else if (source == getYCCommandMenu().installAddressing) {
            javax.swing.ImageIcon icon = new javax.swing.ImageIcon(COMMANDER_IMG_24);
            Object[] selections = null;
            // Get an instance of the cache.
            IDatabaseCache cache = DefaultDatabaseCache.getInstance();

            synchronized (cache) {
                List<LiteYukonPAObject> allLM = cache.getAllLoadManagement();
                
                List<LiteYukonPAObject> lmGroups = new ArrayList<>(allLM.size());
                for (LiteYukonPAObject liteYukonPAObject : allLM) {
                    if (liteYukonPAObject.getPaoType() == PaoType.LM_GROUP_EXPRESSCOMM 
                            || liteYukonPAObject.getPaoType() == PaoType.LM_GROUP_RFN_EXPRESSCOMM
                            || liteYukonPAObject.getPaoType() == PaoType.LM_GROUP_VERSACOM) {
                        lmGroups.add(liteYukonPAObject);
                    }
                }
                selections = lmGroups.toArray();
            }

            Object value = JOptionPane.showInputDialog(this, 
                    "Load Group Template", 
                    "Select the Addressing Template to Install", 
                    JOptionPane.QUESTION_MESSAGE, 
                    icon,
                    selections, null);
            
            if (value != null) { // OK selected
                try {
                    setCommand("putconfig template \'" + value.toString() + "\'");
                    if (isValidSetup()) {
                        getCommandPanel().enter(getCommand());
                        getYC().executeCommand();
                    }
                } catch (PaoAuthorizationException e) {
                    update(yc, new OutputMessage(OutputMessage.DEBUG_MESSAGE, 
                            "\n ** You do not have permission to execute command: " + e.getPermission(), 
                            MessageType.ERROR));
                }
            }
        } else if (source == getYCCommandMenu().locateRoute) {
            
            getLocateRouteDialog().getDeviceNameTextField().setText(getTreeViewPanel().getSelectedItem().toString());
            getLocateRouteDialog().showLocateDialog();

            Object selected = getLocateRouteDialog().getRouteComboBox().getSelectedItem();
            if (selected instanceof LiteYukonPAObject) {
                setRouteID(((LiteYukonPAObject) selected).getYukonID());
            }
            
        } else if (source == getYCCommandMenu().downloadSchedule) {
            
            getDownloadTOUDialog().getDeviceNameTextField().setText(getTreeViewPanel().getSelectedItem().toString());
            Object lShedule = getDownloadTOUDialog().showDownloadOptions(this);

            if (lShedule != null && lShedule instanceof LiteTOUSchedule) {
                try {
                    setCommand(commandDao.buildTOUScheduleCommand(((LiteTOUSchedule) lShedule).getScheduleID()));
                    if (isValidSetup()) {
                        getYC().executeCommand();
                    }
                } catch (PaoAuthorizationException e) {
                    update(yc, new OutputMessage(OutputMessage.DEBUG_MESSAGE, 
                            "\n ** You do not have permission to execute command: " + e.getPermission(), 
                            MessageType.ERROR));
                }
            }
        } else if (source == getLocateRouteDialog().getLocateButton()) {
            try {
                setCommand("loop locateroute");
                if (isValidSetup()) {
                    getCommandPanel().enter(getCommand());
                    getYC().executeCommand();
                }
            } catch (PaoAuthorizationException e) {
                update(yc, new OutputMessage(OutputMessage.DEBUG_MESSAGE, 
                        "\n ** You do not have permission to execute command: " + e.getPermission(), 
                        MessageType.ERROR));
            }
        } else if (source == getLocateRouteDialog().getRouteComboBox()) {
            
            Object selected = getLocateRouteDialog().getRouteComboBox().getSelectedItem();
            if (selected instanceof LiteYukonPAObject) {
                setRouteID(((LiteYukonPAObject) selected).getYukonID());
            }
        } else if (source == getYCFileMenu().printMenuItem 
                || source == getClearPrintButtons().getPrintButton()) {
            print(getDebugOutputTextPane());
            print(getDisplayOutputTextPane());
        } else if (source == getClearPrintButtons().getClearButton() 
                || source == getYCViewMenu().clearMenuItem) {
            getDebugOutputTextPane().setText("");
            getDisplayOutputTextPane().setText("");
        } else if (source == getYCFileMenu().saveMenuItem) {
            save(getDebugOutputTextPane());
        } else if (source == getYCViewMenu().searchMenuItem) {
            
            String value = JOptionPane.showInputDialog(this, "Name of the item: ", "Find", JOptionPane.QUESTION_MESSAGE);

            if (value != null) {
                boolean found = getTreeViewPanel().searchFirstLevelString(value);
                if (!found) {
                    JOptionPane.showMessageDialog(this, "Unable to find your selected item", "Item Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else if (source == getYCFileMenu().getRecentItem0()) {
            selectRecentItem(0);
        } else if (source == getYCFileMenu().getRecentItem1()) {
            selectRecentItem(1);
        } else if (source == getYCFileMenu().getRecentItem2()) {
            selectRecentItem(2);
        } else if (source == getYCFileMenu().getRecentItem3()) {
            selectRecentItem(3);
        } else if (source == getYCFileMenu().getRecentItem4()) {
            selectRecentItem(4);
        } else if (source == getYCFileMenu().commandSpecificControl) {
            if (getYC().getCommandMode() == YC.DEFAULT_MODE) {
                // Turn CGP mode ON
                getYC().setCommandMode(YC.CGP_MODE);
                CTILogger.info(" ** CGPMODE IS ON ** ");
                getCGPMode().setText("CGPMODE ON:  Sending \'Execute Command\' string. (CTRL + F5)");
            } else {
                // Turn DEFAULT mode ON
                getYC().setCommandMode(YC.DEFAULT_MODE);
                CTILogger.info(" ** CGPMODE IS OFF ** ");
                getCGPMode().setText("");
            }
        } else if (source == getYCHelpMenu().aboutMenuItem) {
            about();
        } else if (source == getYCHelpMenu().helpTopicMenuItem) {
            CtiUtilities.showHelp(HELP_FILE);
        } else if (source == getYCViewMenu().deleteSerialNumberMenuItem) {
            deleteSerialNumber();
        } else if (source == getYCCommandMenu().advancedOptionsMenuItem) {
            YCDefaults defaults = getAdvOptsPanel().showAdvancedOptions(this);
            if (defaults != null) {
                getCommandLogPanel().setVisible(defaults.getShowMessageLog());
                getYC().setYCDefaults(defaults);
            }
            advOptsPanel = null;
        } else if (source == getYCFileMenu().exitMenuItem) {
            exit();
        } else if (event.getActionCommand().equalsIgnoreCase(CLEAR_OUTPUT_DISPLAY)) {
            getDisplayOutputTextPane().setText("");
        } else if (event.getActionCommand().equalsIgnoreCase(PRINT_OUTPUT_DISPLAY)) {
            print(getDisplayOutputTextPane());
        } else if (event.getActionCommand().equalsIgnoreCase(CLEAR_OUTPUT_DEBUG)) {
            getDebugOutputTextPane().setText("");
        } else if (event.getActionCommand().equalsIgnoreCase(PRINT_OUTPUT_DEBUG)) {
            print(getDebugOutputTextPane());
        }
    }
    
    private void selectRecentItem(int index) {
        
        Object obj = getYCFileMenu().getRecentItems()[index];
        if (obj instanceof LiteBase) {
            getTreeViewPanel().selectLiteObject((LiteBase)obj);
        } else if (obj instanceof String) {
            getTreeViewPanel().selectByString((String)obj);
        }
        
        if (getTreeViewPanel().getSelectedNode() == null) {
            getTreeViewPanel().clearSelection();
            setTreeItem(getYCFileMenu().getRecentItems()[index]);
        }
    }

    /**
     * Returns an integer [YES/NO] indicating the option selected from a yes/no popupFrame.
     * @param message java.lang.String
     */
    private int areYouSure(String message, int messageType) {
        JFrame popupFrame = new JFrame();
        popupFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_24));
        return JOptionPane.showConfirmDialog(popupFrame, message, YC_TITLE, JOptionPane.YES_NO_OPTION, messageType);
    }

    /**
     * Delete the selectedNode from getTreeViewPanel().
     * Removes the selectedNode (serial number) from getSerialNumberVector().
     */
    private void deleteSerialNumber() {
        
        if (TreeModelEnum.isEditableSerial(getModelType())) {
            if (getTreeViewPanel().getSelectedItem() == null) {
                return;
            } else {
                if (getTreeViewPanel().getSelectedNode().getParent() == null) {
                    // can't delete the parent node.
                    return;
                }
                
                ((EditableTextModel)getTreeViewPanel().getSelectedTreeModel()).getEntryVector().remove(getTreeViewPanel().getSelectedItem());
                    
                // Update the selected getTreeViewPanel() model.
                ((EditableTextModel) getTreeViewPanel().getSelectedTreeModel()).update();
                getSerialRoutePanel().getSerialTextField().setText("");
            }
        }
    }

    /**
     * Enables/Disables (value) objects used with serialNumber commands.
     * @param value boolean
     */
    private void enableSerialAndRoute(boolean value) {
        getSerialRoutePanel().setObjectsEnabled(value);
        getYCViewMenu().deleteSerialNumberMenuItem.setEnabled(value);
    }

    /**
     * Exit application.
     */
    private void exit() {
        getYC().getYCDefaults().setOutputDividerLoc(getOutputSplitPane().getDividerLocation());
        getYC().getYCDefaults().writeDefaultsFile();
    
        System.exit(0);
    }

    public void focusGained(FocusEvent event) {}

    public void focusLost(FocusEvent event) {
        if (event.getSource() == getSerialRoutePanel().getSerialTextField()) {
            serialNumberAction();
        }
    }

    public AdvancedOptionsPanel getAdvOptsPanel() {
        if (advOptsPanel == null) {
            advOptsPanel  = new AdvancedOptionsPanel(getYC().getYCDefaults());
        }
        
        return advOptsPanel;
    }
    
    private JLabel getCGPMode() {
        if (ivjCGPMode == null) {
            try {
                ivjCGPMode = new JLabel();
                ivjCGPMode.setName("CGPMode");
                ivjCGPMode.setText("");
                ivjCGPMode.setForeground(Color.red);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjCGPMode;
    }
    
    private ClearPrintButtonPanel getClearPrintButtons() {
        if (ivjClearPrintButtons == null) {
            try {
                ivjClearPrintButtons = new ClearPrintButtonPanel();
                ivjClearPrintButtons.setName("ClearPrintButtons");
                ivjClearPrintButtons.getClearButton().addActionListener(this);
                ivjClearPrintButtons.getPrintButton().addActionListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjClearPrintButtons;
    }
    
    private JPanel getClearPrintPanel() {
        if (ivjClearPrintPanel == null) {
            try {
                ivjClearPrintPanel = new JPanel();
                ivjClearPrintPanel.setName("ClearPrintPanel");
                ivjClearPrintPanel.setLayout(new BorderLayout());
                getClearPrintPanel().add(getCGPMode(), "West");
                getClearPrintPanel().add(getClearPrintButtons(), "East");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjClearPrintPanel;
    }

    private String getCommand() {
        return getYC().getCommandString();
        
    }
    
    public CommandLogPanel getCommandLogPanel() {
        if (ivjCommandLogPanel == null) {
            try {
                ivjCommandLogPanel = new CommandLogPanel();
                ivjCommandLogPanel.setName("CommandLogPanel");
                ivjCommandLogPanel.setVisible(getYC().getYCDefaults().getShowMessageLog());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjCommandLogPanel;
    }
    
    private CommandPanel getCommandPanel() {
        if (ivjCommandPanel == null) {
            try {
                ivjCommandPanel = new CommandPanel();
                ivjCommandPanel.setName("CommandPanel");
                ivjCommandPanel.getExecuteButton().addActionListener(this);
                ivjCommandPanel.getStopButton().addActionListener(this);
    
                ivjCommandPanel.getExecuteCommandComboBoxTextField().addKeyListener(this);
                ivjCommandPanel.getExecuteCommandComboBoxTextField().addActionListener(this);
                ivjCommandPanel.getAvailableCommandsComboBox().addActionListener(this);
                ivjCommandPanel.getExecuteButton().addKeyListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjCommandPanel;
    }

    /**
     * Gets the singleton connection to Pil
     * 
     * @return ClientConnection
     */
    private IServerConnection getPilConn() {
        return ConnPool.getInstance().getDefPorterConn();
    }

    private JScrollPane getDebugOutputScrollPane() {
        if (ivjDebugOutputScrollPane == null) {
            try {
                ivjDebugOutputScrollPane = new JScrollPane();
                ivjDebugOutputScrollPane.setName("DebugOutputScrollPane");
                ivjDebugOutputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                getDebugOutputScrollPane().setViewportView(getDebugOutputTextPane());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjDebugOutputScrollPane;
    }
    
    private JTextPane getDebugOutputTextPane() {
        if (ivjDebugOutputTextPane == null) {
            try {
                ivjDebugOutputTextPane = new JTextPane();
                ivjDebugOutputTextPane.setName("DebugOutputTextPane");
                ivjDebugOutputTextPane.setBounds(0, 0, 11, 6);
                ivjDebugOutputTextPane.setContentType("text/html");
                
                for (MessageType messageType : MessageType.values()) {
                    Style style = ivjDebugOutputTextPane.addStyle(messageType.getStyleString(), null);
                    StyleConstants.setForeground(style, messageType.getColor().getAwtColor());
                }
                
                Style style = ivjDebugOutputTextPane.addStyle("Font", null);
                StyleConstants.setFontSize(style, 12);
                StyleConstants.setFontFamily(style, "sans-serif");

                ivjDebugOutputTextPane.addMouseListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjDebugOutputTextPane;
    }
    
    private JScrollPane getDisplayOutputScrollPane() {
        if (ivjDisplayOutputScrollPane == null) {
            try {
                ivjDisplayOutputScrollPane = new JScrollPane();
                ivjDisplayOutputScrollPane.setName("DisplayOutputScrollPane");
                ivjDisplayOutputScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
                ivjDisplayOutputScrollPane.setViewportView(getDisplayOutputTextPane());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjDisplayOutputScrollPane;
    }
    
    private JTextPane getDisplayOutputTextPane() {
        if (ivjDisplayOutputTextPane == null) {
            try {
                ivjDisplayOutputTextPane = new JTextPane();
                ivjDisplayOutputTextPane.setName("DisplayOutputTextPane");
                ivjDisplayOutputTextPane.setBounds(0, 0, 11, 6);
                ivjDisplayOutputTextPane.setContentType("text/html");
                
                for (MessageType messageType : MessageType.values()) {
                    Style style = ivjDisplayOutputTextPane.addStyle(messageType.getStyleString(), null);
                    StyleConstants.setForeground(style, messageType.getColor().getAwtColor());
                }

                Style style = ivjDisplayOutputTextPane.addStyle("Font", null);
                StyleConstants.setFontSize(style, 12);
                StyleConstants.setFontFamily(style, "sans-serif");
                ivjDisplayOutputTextPane.addMouseListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjDisplayOutputTextPane;
    }
    
    private JPanel getExecutionPanel() {
        if (ivjExecutionPanel == null) {
            try {
                ivjExecutionPanel = new JPanel();
                ivjExecutionPanel.setName("ExecutionPanel");
                ivjExecutionPanel.setLayout(new GridBagLayout());
    
                GridBagConstraints constraintsCommandPanel = new GridBagConstraints();
                constraintsCommandPanel.gridx = 0; constraintsCommandPanel.gridy = 0;
                constraintsCommandPanel.fill = GridBagConstraints.BOTH;
                constraintsCommandPanel.anchor = GridBagConstraints.NORTH;
                constraintsCommandPanel.weightx = 1.0;
                constraintsCommandPanel.insets = new Insets(5, 5, 5, 5);
                getExecutionPanel().add(getCommandPanel(), constraintsCommandPanel);
    
                GridBagConstraints constraintsOutputPanel = new GridBagConstraints();
                constraintsOutputPanel.gridx = 0; constraintsOutputPanel.gridy = 1;
                constraintsOutputPanel.fill = GridBagConstraints.BOTH;
                constraintsOutputPanel.weightx = 1.0;
                constraintsOutputPanel.weighty = 1.0;
                constraintsOutputPanel.insets = new Insets(5, 5, 5, 5);
                getExecutionPanel().add(getOutputPanel(), constraintsOutputPanel);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjExecutionPanel;
    }
    
    private JPanel getJFrameContentPane() {
        if (ivjJFrameContentPane == null) {
            try {
                ivjJFrameContentPane = new JPanel();
                ivjJFrameContentPane.setName("JFrameContentPane");
                ivjJFrameContentPane.setLayout(new BorderLayout());
                getJFrameContentPane().add(getSplitPane(), "Center");
                getJFrameContentPane().add(getCommandLogPanel(), "South");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjJFrameContentPane;
    }
    
    private LocateRouteDialog getLocateRouteDialog() {
        if (locRouteDialog == null) {
            locRouteDialog = new LocateRouteDialog(this, "LocateRoute", true);
            locRouteDialog.getLocateButton().addActionListener(this);
            locRouteDialog.getRouteComboBox().addActionListener(this);
        }
        
        return locRouteDialog;
    }
    
    private DownloadTOUSchedulePanel getDownloadTOUDialog() {
        if (downloadTOUPanel == null) {
            downloadTOUPanel  = new DownloadTOUSchedulePanel();
            downloadTOUPanel.addItems();
            downloadTOUPanel.getTOUScheduleComboBox().addActionListener(this);
        }
        
        return downloadTOUPanel;
    }
    
    private Class<? extends LiteBaseTreeModel> getModelType() {
        return getYC().getModelType();
    }
    
    private JPanel getNavigatorPanel() {
        if (ivjNavigatorPanel == null) {
            try {
                ivjNavigatorPanel = new JPanel();
                ivjNavigatorPanel.setName("NavigatorPanel");
                ivjNavigatorPanel.setLayout(new BorderLayout());
                getNavigatorPanel().add(getSerialRoutePanel(), "North");
                getNavigatorPanel().add(getTreeViewPanel(), "Center");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjNavigatorPanel;
    }
    
    private JPanel getOutputPanel() {
        if (ivjOutputPanel == null) {
            try {
                ivjOutputPanel = new JPanel();
                ivjOutputPanel.setName("OutputPanel");
                ivjOutputPanel.setLayout(new BorderLayout());
                getOutputPanel().add(getClearPrintPanel(), "South");
                getOutputPanel().add(getOutputSplitPane(), "Center");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjOutputPanel;
    }
    
    private JSplitPane getOutputSplitPane() {
        if (ivjOutputSplitPane == null) {
            try {
                ivjOutputSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                ivjOutputSplitPane.setName("OutputSplitPane");
                ivjOutputSplitPane.setDividerSize(10);
                ivjOutputSplitPane.setComponentOrientation(ComponentOrientation.UNKNOWN);
                ivjOutputSplitPane.setDividerLocation(190);
                ivjOutputSplitPane.setOneTouchExpandable(true);
                getOutputSplitPane().add(getDebugOutputScrollPane(), "bottom");
                getOutputSplitPane().add(getDisplayOutputScrollPane(), "top");
                ivjOutputSplitPane.setDividerLocation(getYC().getYCDefaults().getOutputDividerLoc());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjOutputSplitPane;
    }
    
    private String getSerialNumber() {
        return getYC().getSerialNumber();
    }
    
    private SerialRoutePanel getSerialRoutePanel() {
        if (ivjSerialRoutePanel == null) {
            try {
                ivjSerialRoutePanel = new SerialRoutePanel();
                ivjSerialRoutePanel.setName("SerialRoutePanel");
                ivjSerialRoutePanel.getSerialTextField().addKeyListener(this);
                ivjSerialRoutePanel.getSerialTextField().addActionListener(this);
                ivjSerialRoutePanel.getSerialTextField().addFocusListener(this);
                ivjSerialRoutePanel.getRouteComboBox().addActionListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjSerialRoutePanel;
    }
    
    private JSplitPane getSplitPane() {
        if (ivjSplitPane == null) {
            try {
                ivjSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                ivjSplitPane.setName("SplitPane");
                ivjSplitPane.setDividerSize(5);
                getSplitPane().add(getExecutionPanel(), "right");
                getSplitPane().add(getNavigatorPanel(), "left");
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjSplitPane;
    }

    private TreeViewPanel getTreeViewPanel() {
        if (ivjTreeViewPanel == null) {
            try {
                ivjTreeViewPanel = new TreeViewPanel();
                ivjTreeViewPanel.setName("TreeViewPanel");
                ivjTreeViewPanel.addTreeSelectionListener(this);
                ivjTreeViewPanel.getSortByComboBox().addActionListener(this);
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjTreeViewPanel;
    }
    
    private YCCommandMenu getYCCommandMenu() {
        if (ivjYCCommandMenu == null) {
            try {
                ivjYCCommandMenu = new YCCommandMenu();
                ivjYCCommandMenu.setName("YCCommandMenu");
                ivjYCCommandMenu.setText("Command");
                javax.swing.JMenuItem item;
                for (int i = 0; i < ivjYCCommandMenu.getItemCount(); i++)
                {
                    item = ivjYCCommandMenu.getItem(i);
    
                    if (item != null)
                        ivjYCCommandMenu.getItem(i).addActionListener(this);
                }
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjYCCommandMenu;
    }
    
    private YCFileMenu getYCFileMenu() {
        if (ivjYCFileMenu == null) {
            try {
                ivjYCFileMenu = new YCFileMenu();
                ivjYCFileMenu.setName("YCFileMenu");
                ivjYCFileMenu.setText("File");
                JMenuItem item;
                
                for (int i = 0; i < ivjYCFileMenu.getItemCount(); i++) {
                    item = ivjYCFileMenu.getItem(i);
                    if (item != null) {
                        ivjYCFileMenu.getItem(i).addActionListener(this);
                    }
                }
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjYCFileMenu;
    }
    
    private YCHelpMenu getYCHelpMenu() {
        if (ivjYCHelpMenu == null) {
            try {
                ivjYCHelpMenu = new YCHelpMenu();
                ivjYCHelpMenu.setName("YCHelpMenu");
                ivjYCHelpMenu.setText("Help");
                JMenuItem item;
    
                for (int i = 0; i < ivjYCHelpMenu.getItemCount(); i++) {
                    item = ivjYCHelpMenu.getItem(i);
    
                    if (item != null) {
                        ivjYCHelpMenu.getItem(i).addActionListener(this);
                    }
                }
    
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjYCHelpMenu;
    }
    
    private YCViewMenu getYCViewMenu() {
        if (ivjYCViewMenu == null) {
            try {
                ivjYCViewMenu = new YCViewMenu();
                ivjYCViewMenu.setName("YCViewMenu");
                ivjYCViewMenu.setText("View");
                JMenuItem item;
    
                for (int i = 0; i < ivjYCViewMenu.getItemCount(); i++) {
                    item = ivjYCViewMenu.getItem(i);
    
                    if (item != null) {
                        ivjYCViewMenu.getItem(i).addActionListener(this);
                    }
                }
    
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjYCViewMenu;
    }
    
    private JMenuBar getYukonCommanderJMenuBar() {
        if (ivjYukonCommanderJMenuBar == null) {
            try {
                ivjYukonCommanderJMenuBar = new JMenuBar();
                ivjYukonCommanderJMenuBar.setName("YukonCommanderJMenuBar");
                getYukonCommanderJMenuBar().add(getYCFileMenu());
                getYukonCommanderJMenuBar().add(getYCViewMenu());
                getYukonCommanderJMenuBar().add(getYCCommandMenu());
                getYukonCommanderJMenuBar().add(getYCHelpMenu());
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        
        return ivjYukonCommanderJMenuBar;
    }
    
    public void handleDBChangeMsg(final DBChangeMsg msg, final LiteBase object) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Cursor savedCursor = null;
                try {
        
                    if (DBChangeMsg.CAT_YUKON_USER_GROUP.equals(msg.getCategory())) {
                        updateCommandSelection();
                    }
                    
                    savedCursor = getRootPane().getCursor();
                    getRootPane().setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    
                    // Update the route combo box - if necessary
                    if (msg.getCategory().equals(PaoCategory.ROUTE.getDbString())) {
                        updateRouteCombo(msg.getDbChangeType(), object);
                    }
                    
                    // Update the tree
                    Object selectedObject = getTreeViewPanel().getSelectedItem();
                    getTreeViewPanel().processDBChange(msg.getDbChangeType(), object);
                    
                    //if we had something selected we just lost it... so reselect it now
                    if (selectedObject != null) {
                        getTreeViewPanel().selectByString(selectedObject.toString());
                    }
                    
                    // Update TOUSchedule list if a tou schedule msg is received
                    if (msg.getDatabase() == DBChangeMsg.CHANGE_TOU_SCHEDULE_DB
                            && msg.getCategory().equals(DBChangeMsg.CAT_TOU_SCHEDULE)
                            && msg.getObjectType().equals(DBChangeMsg.CAT_TOU_SCHEDULE)) {
                        getDownloadTOUDialog().addItems();
                    }
                    
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    getRootPane().setCursor(savedCursor);
                }
            }
        });
    }
    
    /**
     * Helper method to update the route combo box if a route change msg is received
     * @param typeOfChange - Type of change msg
     * @param liteBase - Route that changed 
     */
    private void updateRouteCombo(DbChangeType dbChangeType, LiteBase liteBase) {

        JComboBox routeComboBox = this.getSerialRoutePanel().getRouteComboBox();

        if (dbChangeType == DbChangeType.UPDATE) {
            for (int i = 0; i < routeComboBox.getItemCount(); i++) {

                Object item = routeComboBox.getItemAt(i);

                if (item instanceof LiteYukonPAObject) {
                    LiteYukonPAObject route = (LiteYukonPAObject) routeComboBox.getItemAt(i);

                    if (liteBase.equals(route)) {
                        route = paoDao.getLiteYukonPAO(route.getLiteID());
                        routeComboBox.update(routeComboBox.getGraphics());
                        break;
                    }
                }
            }
        } else if (dbChangeType == DbChangeType.ADD) {
            routeComboBox.addItem(liteBase);
        } else if (dbChangeType == DbChangeType.DELETE) {
            routeComboBox.removeItem(liteBase);
        }
    }
    
    
    private static void handleException(Throwable exception) {
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        exception.printStackTrace(System.out);
    }

    private void initialize() {
        try {
            setName("YukonCommander");
            setJMenuBar(getYukonCommanderJMenuBar());
            setContentPane(getJFrameContentPane());
        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
              
        //--------Setup treeViewPanel and tree models-------------    
        //This model is created in a weird way.  We can do the model creation similar for all
        // the models except for the Device model.  In order to use a different constructor
        // that shows the device types without their points, we have to specifically look for
        // that model type.  Create a new model for that type (false sets showPoints off).
        // For all other 'normal' types, we can follow through with OO and use the same
        // constructor template (no parameters).
        List<LiteBaseTreeModel> models = new ArrayList<LiteBaseTreeModel>();
        
        TreeModelEnum[] modelIds = getTreeModels();
        for (int i = 0; i < modelIds.length; i++) {
            if (modelIds[i] == TreeModelEnum.DEVICE) {
                DeviceTreeModel deviceTreeModel = new DeviceTreeModel(false);
                deviceTreeModel.setCommanderDevicesOnly(true);
                models.add(deviceTreeModel);
            } else if (modelIds[i] == TreeModelEnum.DEVICE_METERNUMBER) {
                DeviceMeterGroupModel deviceMeterGroupModel = new DeviceMeterGroupModel();
                deviceMeterGroupModel.setCommanderDevicesOnly(true);
                models.add(deviceMeterGroupModel);
            } else if (modelIds[i] == TreeModelEnum.LMGROUPS) {
                LMGroupsModel lmGroupsModel = new LMGroupsModel(false);
                lmGroupsModel.setCommanderDevicesOnly(true);
                models.add(lmGroupsModel);
            } else if (modelIds[i] == TreeModelEnum.CAPBANKCONTROLLER) {
                models.add(new CapBankControllerModel(false));
            } else if (modelIds[i] == TreeModelEnum.TRANSMITTER) {
                models.add(new TransmitterTreeModel(false));
            } else {
                models.add(TreeModelEnum.create(modelIds[i]));
            }
        }
        
        // add the group model
        LiteBaseTreeModel liteBaseDeviceGroupModel = dgtf.getLiteBaseModel(false);
        models.add(liteBaseDeviceGroupModel);

        //serial and route panel visible only when first item in tree is Versacom Serial #
        if (!TreeModelEnum.isEditableSerial(models.get(0).getClass())) {
            enableSerialAndRoute(false);
        }
    
        getTreeViewPanel().setTreeModels(models.toArray(new LiteBaseTreeModel[]{}));
            
        setRouteModel(); //fill route combo box

        dataSource.addDBChangeLiteListener(this);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { 
                exit();
            };
        });
        getYC().addObserver(this);
        getTreeViewPanel().getTree().setSelectionInterval(0,0);
        
    }
    
    /**
     * Returns true when setup is fully complete, all connections exist/valid.
     * @return boolean
     */
    private boolean isValidSetup() {
        
        if (!getPilConn().isValid()) {
            getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
        }
        
        if (getYC().getCommandMode() == YC.CGP_MODE) {
            // CGPMode - User determines validity, not code.
            getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
            return true;
        } else if (getTreeViewPanel().getSelectedItem() == null) {
            getCommandLogPanel().addLogElement(" ** Warning: Please make a selection from the tree.");
        } else if (getCommand() == null || getCommand().length() == 0) {
            getCommandLogPanel().addLogElement(" ** Warning: No command is specified");
        } else if (getTreeViewPanel().getSelectedNode().getParent() == null) {
            getCommandLogPanel().addLogElement(" ** Warning: Please select a specific tree item");
        } else {
            if (getYC().getYCDefaults().getConfirmCommandExecute()) {
                String message = "Execute '" + getCommand() + "' -- Are You Sure?";
                int response = areYouSure(message, JOptionPane.QUESTION_MESSAGE);
                if (response == javax.swing.JOptionPane.OK_OPTION) {
                    getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
                    return true;
                }
            } else {
                getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
                return true;
            }
        }

        return false;
    }
    
    public void keyPressed(KeyEvent event) {

        if (event.getKeyCode() == KeyEvent.VK_ENTER 
                && event.getSource() == getCommandPanel().getExecuteCommandComboBoxTextField() 
                || event.getKeyCode() == KeyEvent.VK_ENTER
                && event.getSource() == getCommandPanel().getExecuteButton()) {
            
            String commandString = loadPromptValue((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim(), this);
            if (commandString != null) { // null is a cancel from prompt
                try {
                    setCommand(commandString);
                    if (isValidSetup()) {
                        getCommandPanel().enter(getCommand());
                        getYC().executeCommand();
                    }
                } catch (PaoAuthorizationException e) {
                    update(yc, new OutputMessage(OutputMessage.DEBUG_MESSAGE, 
                            "\n ** You do not have permission to execute command: " + e.getPermission(), 
                            MessageType.ERROR));
                }
            }
        } else if (event.getKeyCode() == KeyEvent.VK_ENTER 
                && event.getSource() == getSerialRoutePanel().getSerialTextField()) {
            serialNumberAction();
        }
    }
    
    public void keyReleased(KeyEvent event) {}

    public void keyTyped(KeyEvent event) {}
    
    public static void main(String[] args) {
        try {
            ClientStartupHelper clientStartupHelper = new ClientStartupHelper();
            clientStartupHelper.setAppName(ApplicationId.COMMANDER);
            clientStartupHelper.setRequiredRole(YukonRole.COMMANDER.getRoleId());
            clientStartupHelper.setSplashUrl(CtiUtilities.COMMANDER_SPLASH);

            clientStartupHelper.doStartup();
    
            final YukonCommander ycClient = new YukonCommander();
            
            clientStartupHelper.setParentFrame(ycClient);
            
            ycClient.setIconImages(getIconsImages());
                        
            ycClient.setTitle(YC_TITLE);
    
            //set the app to start as close to the center as you can....
            //  only works with small gui interfaces.

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ycClient.setVisible(true);
                    ycClient.getTreeViewPanel().getTree().requestFocusInWindow();
                }
            });

        } catch (Throwable e) {
            CTILogger.error("Exception occurred in main() of javax.swing.JFrame", e);
            System.exit(-1);
        }
    }
    
    /**
     * Print the graphics from printTextPane.
     */
    private void print(JTextPane printTextPane) {
        
        PrinterJob pj = PrinterJob.getPrinterJob();
        if (pj.printDialog()) {
            PageFormat pf = new PageFormat();
            try
            {
                JTextPanePrintable tpp = new JTextPanePrintable(printTextPane);
                Paper paper = new Paper();
                paper.setImageableArea(30, 40, 552, 712);    //8.5 x 11 -> 612w 792h
                pf.setOrientation(PageFormat.PORTRAIT);
                pf.setPaper(paper);
                pj.setPrintable(tpp, pf);
                pj.print();
            } catch(PrinterException ex) {
                ex.printStackTrace();
            }
        }
        
        // FIX to keep the YC frame on top after calling the printDialog.
        // JDK1.4 should have fixed the issue but I(SN) have still seen inconsistencies with focus.
        SwingUtil.getParentFrame(this).toFront(); // keeps the main frame in front focus
    }
    
    /**
     * Forces a reload of databaseCache.
     */
    private void reloadDevices() {
        Frame frame = null;
        Cursor savedCursor = null;
        
        try {
            frame = SwingUtil.getParentFrame(getRootPane());
            savedCursor = frame.getCursor();
            frame.setCursor(new Cursor(Cursor.WAIT_CURSOR));
            
            //load all data from the database to the cache for update to catch anything new.        
            DefaultDatabaseCache.getInstance().releaseAllCache();
            //release the stored routes
            restoreCurrentTree();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            frame.setCursor(savedCursor);
        }
    }
    
    /**
     * Reselects the selected item in the tree, should the tree be refreshed from cache.
     * Not necessarily needed for new devices, but mainly when the current selection is a deleted device.
     */
    private void restoreCurrentTree() {
        
        TreeViewPanel tvp = getTreeViewPanel();
        Object selectedItem = getTreeViewPanel().getSelectedItem();
        
        if (tvp != null) {
            tvp.refresh();
        }
            
        if (selectedItem instanceof LiteBase) {
            LiteBase lb = (LiteBase)selectedItem;
            tvp.selectLiteBase((LiteBaseTreeModel) tvp.getTree().getModel(), lb);
        } else if (selectedItem instanceof String) {
            tvp.selectByString(selectedItem.toString());
        } else {
            tvp.selectObject(null);
            CTILogger.info("WARNING:  nothing reselected in the tree, dbChangeMessageListener is missing an instanceof");
        }
        // ** Add else if... here to include other objects that may be in the treeViewPanel
    }
    
    /**
     * Saves the textPane.getText() to a file.
     */
    private void save(JTextPane textPane) {
        //This will need to be updated someday for a new version of swing
        Frame parent = SwingUtil.getParentFrame(this);
        JFileChooser  fileChooser = new JFileChooser();

        FileFilter filter = new FileFilter("rtf", "Rich Text Format");
        fileChooser.setFileFilter(filter);
        
        while (true) {
            int dialogActionValue = fileChooser.showSaveDialog(parent);
            if (dialogActionValue == JFileChooser.CANCEL_OPTION 
                    || dialogActionValue == JFileChooser.ERROR_OPTION) {
                break;
            }
            
            if (dialogActionValue == javax.swing.JFileChooser.APPROVE_OPTION) {
                
                /* Checks to see if a restricted character was 
                 * used in the submitted file path 
                 */
                String filePath = getFilePath(fileChooser);

                FileWriter fWriter = null;
                try {
                    fWriter = new FileWriter(filePath, true);
                    PrintWriter pWriter = new PrintWriter(fWriter);
                    Document document = textPane.getDocument();
                
                    try {
                        pWriter.print(document.getText(0, document.getLength()));
                    } catch (BadLocationException ble) {
                        CTILogger.error(ble.getMessage(), ble);
                    }

                    fWriter.close();
                    break;
                } catch(FileNotFoundException fnfe) {
                    JOptionPane.showMessageDialog(parent, "Invalid File Name", "Error", JOptionPane.ERROR_MESSAGE);
                } catch(IOException e) {
                    JOptionPane.showMessageDialog(parent, "An error occurred saving to a file", "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    try {
                        if (fWriter != null) {
                            fWriter.close();
                        }
                    } catch (IOException e2) {
                        CTILogger.error(e2.getMessage(), e2);
                    }
                }
            }
        }
    }
    
    /**
     * Enter serial number string into tree.
     * If the entered serial value is invalid (null or 0 length), no tree items are entered or selected.
     * If the entered serial value does not already exist in the tree it is added to the tree's model.
     */
    private void serialNumberAction() {
        TreeViewPanel t =  getTreeViewPanel();
        String tempSerialNumber = (String) getSerialRoutePanel().getSerialTextField().getText().trim();
    
        if (tempSerialNumber == null || tempSerialNumber.length() == 0) {
            setSerialNumber(null);
            t.getTree().getSelectionModel().setSelectionPath(null);
            
            return;
        } else if (!((EditableTextModel)getTreeViewPanel().getSelectedTreeModel()).getEntryVector().contains(tempSerialNumber)) {
            ((EditableTextModel)getTreeViewPanel().getSelectedTreeModel()).getEntryVector().add(tempSerialNumber);
    
            // Refresh the tree selection.
            ((EditableTextModel)t.getSelectedTreeModel()).update();
    
            // Clear serial number text field.
            if (getSerialRoutePanel().getSerialTextField() instanceof JTextField) {
                getSerialRoutePanel().getSerialTextField().setText("");
            }
        }
    
        setSerialNumber(tempSerialNumber);
        updateTreePathSelection(getSerialNumber());
    }

    private void setCommand(String newCommand) throws PaoAuthorizationException {
        getYC().setCommandString(newCommand);
    }
    
    /**
     * Sets the ycClass.modelType
     * Valid types located in - com.cannontech.database.model.TreeModelEnum.DEVICE, DEVICE_METERNUMBER, 
     * MCTBROADCAST, LMGROUPS, CAPBANKCONTROLLER, COLLECTIONGROUP, TESTCOLLECTIONGROUP, BILLINGGROUP, EDITABLE_xxx
     * @param typeSelected int
     */
    private void setModelType(Class<? extends LiteBaseTreeModel> typeSelected) {
        getYC().setModelType(typeSelected);
    }
    
    private void setRouteID(int routeID) {
        getYC().setRouteID(routeID);
    }

    /**
     * Fills the routeComboBox with all of the created routes.
     * Retrieves the routes from cache memory and then sorts them (alphabetically).
     * Removes all items in the combo box (applicable when new routes exist)
     * Add a default of All Routes as the first item in the combo box
     * Add the database routes to the combo box.
     */
    private void setRouteModel() {
        if (getSerialRoutePanel().getRouteComboBox() == null) {
            return; 
        }
    
        int index = 0; //Index in the routeComboBox to be selected
        
        // Save the routeComboBox's currently selected item.
        Object saveSelectedRoute = getSerialRoutePanel().getRouteComboBox().getSelectedItem();
        
        getSerialRoutePanel().getRouteComboBox().removeAllItems();
        getSerialRoutePanel().getRouteComboBox().addItem("All Default Routes"); //default first item
    
        getYC().setAllRoutes(null); //dump the stored routes, will reload from cache on next getAllRoutes call
        LiteYukonPAObject[] routes = (LiteYukonPAObject[]) yc.getAllRoutes();
        for(int i = 0; i < routes.length; i++) {
            getSerialRoutePanel().getRouteComboBox().addItem(routes[i]);
            
            if (saveSelectedRoute != null 
                    && routes[i].toString() == saveSelectedRoute.toString()) {
                index = i + 1;
            }
        }
    
        // Set the newly selected item to the index of the previously selected item.
        //  If the prev item doesn't exist, set selected item to the 0 index (default).
        getSerialRoutePanel().getRouteComboBox().setSelectedIndex(index);
            
    }
    
    private void setSerialNumber(String newSerialNumber) {
        getYC().setSerialNumber(newSerialNumber);
    }

    /**
     * Updates visibilty of objects when model changes.
     */
    private void treeModelChanged() {
        LiteBaseTreeModel model = (LiteBaseTreeModel) getTreeViewPanel().getSortByComboBox().getSelectedItem();
        if (model == null) {
            return;
        }
            
        setModelType(model.getClass());
        Object selectedItem = getTreeViewPanel().getSelectedItem();
        
        if (TreeModelEnum.isEditableSerial(getModelType())) {
            if (selectedItem != null 
                    && getTreeViewPanel().getSelectedNode().getParent() != null) {
                getSerialRoutePanel().setSerialNumberText(selectedItem.toString());
            } else {
                getSerialRoutePanel().setSerialNumberText("");
            }
            
            enableSerialAndRoute(true);
            getSerialRoutePanel().getSerialTextField().requestFocus();
        } else {
            enableSerialAndRoute(false);
        }

        updateCommandMenu(selectedItem);
        
        if (selectedItem == null) {
            getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
            getCommandPanel().getExecuteCommandComboBoxTextField().setText("");
        }
    }

    /**
     * Updates the current tree selection to the parameter passed in.
     * Used for synchronization.
     */
    private void updateTreePathSelection(Object currentSelection) {
        TreeViewPanel t =  getTreeViewPanel();
        TreePath selectedPath = SwingUtil.getTreePath(t.getTree(), currentSelection);
        t.getTree().getSelectionModel().setSelectionPath(selectedPath);
    }
    
    /**
     * Gets the appropriate configFile for the object type selected in the treeViewPanel tree.
     * Parses the keys and Values in the configFile and fills the CommandComboBox accordingly.
     * Only updates the command boxes when the selected tree item differs in object type from
     *  the previously selected tree item.
     */
    public void valueChanged(TreeSelectionEvent event) {
        //TODO - On change of model, reset the recent devices.
        LiteBaseTreeModel model = (LiteBaseTreeModel) getTreeViewPanel().getSelectedTreeModel();
        if (model == null) {
            return;
        }
            
        setModelType(model.getClass());
        Object selectedItem = getTreeViewPanel().getSelectedItem();
        setTreeItem(selectedItem);

        return;
    }
    
    public void setTreeItem(Object selectedItem) {
        getYC().setTreeItem(selectedItem);
        
        String savedDevType = getYC().getDeviceType();
        String displayTitle = YC_TITLE;
        setTitle(displayTitle);

        if (selectedItem == null) {
            if (TreeModelEnum.isEditableSerial(getModelType())) {
                getYC().setDeviceType(savedDevType);
            } else {
                getYC().setDeviceType("");
            }
            
            return;
        }
    
        if (getTreeViewPanel().getSelectedNode() != null) {
            if (getTreeViewPanel().getSelectedNode().getParent() == null) {
                return;
            }
        }
        
        updateCommandMenu(selectedItem);
        if (selectedItem instanceof LiteBase) {
            
            getYC().setDeviceType((LiteBase)selectedItem);
            if (selectedItem instanceof LiteYukonPAObject) {
                
                LiteYukonPAObject lpao = (LiteYukonPAObject)selectedItem;
                setTitle(displayTitle + " - " + lpao.getPaoName() + " (" + lpao.getPaoType().getDbString() + ")");
            } else {
                setTitle(displayTitle + " - " + getYC().getDeviceType());
            }
        } else if (TreeModelEnum.isEditableSerial(getModelType())) {
            
            setSerialNumber((String)selectedItem);
            getSerialRoutePanel().setSerialNumberText(getSerialNumber().toString());
            
            if (getModelType() == EditableExpresscomModel.class) {
                getYC().setDeviceType(CommandCategory.EXPRESSCOM_SERIAL.getDbString());
            } else if (getModelType() == EditableVersacomModel.class) {
                getYC().setDeviceType(CommandCategory.VERSACOM_SERIAL.getDbString());
            } else if (getModelType() == EditableSA205Model.class) {
                getYC().setDeviceType(CommandCategory.SA205_SERIAL.getDbString());
            } else if (getModelType() == EditableSA305Model.class) {
                getYC().setDeviceType(CommandCategory.SA305_SERIAL.getDbString());
            } else {
                getYC().setDeviceType(CommandCategory.SERIALNUMBER.getDbString());
            }
            
            if (getYC().getLiteDeviceTypeCommands().isEmpty()) {
                
                getCommandLogPanel().addLogElement(" *** No commands were found for the device type: " 
                        + getYC().getDeviceType() 
                        + "  -  Trying a backup - " 
                        + CommandCategory.VERSACOM_SERIAL + " ***");
                
                //This is only temporary until all files have been changed from ALT_SERIALNUMBER_FILENAME to SERIALNUMBER_FILENAME.
                getYC().setDeviceType(CommandCategory.VERSACOM_SERIAL.getDbString());
            }

            setTitle(displayTitle + " - " + getYC().getDeviceType() + " # " + getSerialNumber().toString());
            
        } else if (getModelType() == DeviceGroupTreeFactory.LiteBaseModel.class) {
            
            DeviceGroup deviceGroup = (DeviceGroup) selectedItem;
            getYC().setDeviceType(CommandCategory.DEVICE_GROUP.getDbString());
            setTitle(displayTitle + " : " + deviceGroup.getFullName());
            
        } else {
            CTILogger.error("No DeviceType found, using empty String");
            getYC().setDeviceType("");
        }
        
        if (getYC().getLiteDeviceTypeCommands().isEmpty()) {
            
            getCommandLogPanel().addLogElement(" *** No commands were found for the device type: " 
                    + getYC().getDeviceType()+ " ***");
            getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
            getCommandPanel().getExecuteCommandComboBox().setSelectedItem(""); //clear text field
            
            return;
        }
        
        // Only update command boxes on a change in device type/ file name.
        if (!getYC().getDeviceType().equalsIgnoreCase(savedDevType)) {
            updateCommandSelection();
        }
    }

    public void updateCommandMenu(Object selectedItem) {
        getYCCommandMenu().locateRoute.setEnabled(false); //init to false, will change below if valid state.
        getYCCommandMenu().installAddressing.setEnabled(false); //init to false, will change below if valid state.
        getYCCommandMenu().downloadSchedule.setEnabled(false);    //init to false, will change below if valid state.

        if (TreeModelEnum.isEditableSerial(getModelType())) {
            getYCCommandMenu().installAddressing.setEnabled(true);
        } else {
            LiteYukonPAObject lpao = null;
            if (selectedItem instanceof LiteYukonPAObject) {
                lpao = (LiteYukonPAObject) selectedItem;
            } else if (selectedItem instanceof LiteDeviceMeterNumber) {
                lpao = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(((LiteDeviceMeterNumber)selectedItem).getDeviceID());
            }
            
            if (lpao != null) {
                if (paoDefinitionDao.isTagSupported(lpao.getPaoType(), PaoTag.LOCATE_ROUTE)) {
                    getYCCommandMenu().locateRoute.setEnabled(true);
                }
                if (paoDefinitionDao.isTagSupported(lpao.getPaoType(), PaoTag.TOU)) {
                    getYCCommandMenu().downloadSchedule.setEnabled(true);
                }
            }
        }
    }
    
    public void updateCommandSelection() {
        // Clear out the old, get ready for the new!
        getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
        getCommandPanel().getExecuteCommandComboBox().setSelectedItem("");
                
        // Add the keys to the availableCommandsComboBox, first one is default ("Select A Command")
        getCommandPanel().getAvailableCommandsComboBox().addItem("<Select A Command>");
        for (int i = 0; i < getYC().getLiteDeviceTypeCommands().size(); i++) {
            
            LiteDeviceTypeCommand ldtc = (LiteDeviceTypeCommand)getYC().getLiteDeviceTypeCommands().get(i);
            LiteCommand  lc = YukonSpringHook.getBean(CommandDao.class).getCommand(ldtc.getCommandId());
            if (ldtc.isVisible() && yc.isAllowCommand(lc.getCommand())) {
                getCommandPanel().getAvailableCommandsComboBox().addItem(lc.getLabel());
            }
        }
    }
    
    public void mouseExited(MouseEvent event) {}
    public void mouseClicked(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    public void mousePressed(MouseEvent event) {}
    
    public void mouseReleased(MouseEvent event) {
        
        if (event.isPopupTrigger()) {
        
            String clearCommand = "";
            String printCommand = "";
            if (event.getComponent() == getDebugOutputTextPane()) {
                clearCommand = CLEAR_OUTPUT_DEBUG;
                printCommand = PRINT_OUTPUT_DEBUG;
            } else if (event.getComponent() == getDisplayOutputTextPane()) {
                clearCommand = CLEAR_OUTPUT_DISPLAY;
                printCommand = PRINT_OUTPUT_DISPLAY;
            }
            JPopupMenu popup = new JPopupMenu("Popup Menu Test");
            JMenuItem clear = new JMenuItem("Clear Output");
            clear.addActionListener(this);
            clear.setActionCommand(clearCommand);
            
            JMenuItem print = new JMenuItem("Print Output");
            print.addActionListener(this);
            print.setActionCommand(printCommand);
            
            popup.add(clear);
            popup.add(print);
        
            popup.show(event.getComponent(), event.getX(), event.getY());
            System.out.println("show popup");
        }
    }
    
    public void update(Observable o, Object arg) {
        
        if (o instanceof YC && arg instanceof OutputMessage) {
            
            OutputMessage outMessage = (OutputMessage)arg;
            if (outMessage.getDisplayAreaType() == OutputMessage.DEBUG_MESSAGE) {
                SwingUtilities.invokeLater(new WriteOutput(getDebugOutputTextPane(), outMessage));
                /*TODO: HACK TO ELIMINATE TOO MUCH MUMBLE JUMBLE IN DISPLAY (TOP) PANEL 
                 * Parsing for " sent " helps eliminate the communication responses somewhat*/
                if (outMessage.getMessageType() == MessageType.SUCCESS && outMessage.getText().indexOf(" sent ") < 0) {
                    //send message to display also?
                    SwingUtilities.invokeLater(new WriteOutput(getDisplayOutputTextPane(), outMessage));
                }
            } else if (outMessage.getDisplayAreaType() == OutputMessage.DISPLAY_MESSAGE) {
                SwingUtilities.invokeLater(new WriteOutput(getDisplayOutputTextPane(), outMessage));
            }
        } else if (arg instanceof String) {
            getCommandLogPanel().addLogElement(arg.toString());
        }
    }
    
    public YC getYC() {
        
        if (yc == null) {
            yc = new YC(true); //load defaults from file
            yc.setLiteUser(ClientSession.getInstance().getUser());
        }
        
        return yc;
    }

    public void setYC(YC yc) {
        this.yc = yc;
    }

    public TreeModelEnum[] getTreeModels() {
        
        if (treeModels == null) {
            //Vector of ints (TreeModelEnum types), (Changed from array to remove size constraints)
            List<TreeModelEnum> tempModel = new ArrayList<TreeModelEnum>();
            tempModel.add(TreeModelEnum.DEVICE);
            tempModel.add(TreeModelEnum.DEVICE_METERNUMBER);
            tempModel.add(TreeModelEnum.MCTBROADCAST);
            tempModel.add(TreeModelEnum.TRANSMITTER);
            tempModel.add(TreeModelEnum.LMGROUPS);
            tempModel.add(TreeModelEnum.CAPBANKCONTROLLER);

            boolean needDefault = true;
            ClientSession session = ClientSession.getInstance();

            RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
            LiteYukonUser user = session.getUser();
            
            if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.DCU_SA305_SERIAL_MODEL, user)) {
                tempModel.add(TreeModelEnum.EDITABLE_SA305_SERIAL);
                needDefault = false;
            }
            if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.DCU_SA205_SERIAL_MODEL, user)) {
                tempModel.add(TreeModelEnum.EDITABLE_SA205_SERIAL);
                needDefault = false;
            }
            if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.EXPRESSCOM_SERIAL_MODEL, user)) {
                tempModel.add(TreeModelEnum.EDITABLE_EXPRESSCOM_SERIAL);
                needDefault = false;
            }
            if (rolePropertyDao.getPropertyBooleanValue(YukonRoleProperty.VERSACOM_SERIAL_MODEL, user)) {
                tempModel.add(TreeModelEnum.EDITABLE_VERSACOM_SERIAL);
                needDefault = false;
            }
            if (needDefault) {
                tempModel.add(TreeModelEnum.EDITABLE_LCR_SERIAL);
            }
            treeModels = new TreeModelEnum[tempModel.size()];
            treeModels = tempModel.toArray(treeModels);
        }
        
        return treeModels;
    }
    
    private String getFilePath(JFileChooser fileChooser) {
        
        String filePath = fileChooser.getSelectedFile().getPath();
        String fileName = fileChooser.getSelectedFile().getName();
        
        // Special Case where the user puts a file name with two double "s
        // EX.  "file.txt" should be file.txt not "file.txt".rtf 
        if (fileName.length() > 2 && fileName.startsWith("\"") && fileName.endsWith("\"")) {
            
            filePath = filePath.replace(fileName, fileName.substring(1, fileName.length() - 1));
        } else {
            if (!filePath.endsWith(".rtf") 
                    && fileChooser.getFileFilter().getDescription().contains("Rich Text Format")) {
                filePath += ".rtf";
            }
        }
        
        return filePath;
    }
    
}
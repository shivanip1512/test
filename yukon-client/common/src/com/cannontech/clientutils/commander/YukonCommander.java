package com.cannontech.clientutils.commander;

/**
 * This type was created in VisualAge.
 */
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.print.PrinterException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
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
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupTreeFactory;
import com.cannontech.common.gui.util.JTextPanePrintable;
import com.cannontech.common.gui.util.TreeViewPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.login.ClientStartupHelper;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.FileFilter;
import com.cannontech.core.authorization.exception.PaoAuthorizationException;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.model.EditableExpresscomModel;
import com.cannontech.database.model.EditableSA205Model;
import com.cannontech.database.model.EditableSA305Model;
import com.cannontech.database.model.EditableTextModel;
import com.cannontech.database.model.EditableVersacomModel;
import com.cannontech.database.model.LiteBaseTreeModel;
import com.cannontech.database.model.TreeModelEnum;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.roles.application.CommanderRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class YukonCommander extends JFrame implements DBChangeLiteListener, ActionListener, FocusListener, KeyListener, TreeSelectionListener, MouseListener, Observer {
	private YC yc;
	private PaoDefinitionDao paoDefinitionDao;
	
	private TreeModelEnum[] treeModels = null;
	private static final String YC_TITLE = "Commander";
	public static final String HELP_FILE = "Yukon_Commander_Help.chm";
	
    public static final URL COMMANDER_IMG_16 = YukonCommander.class.getResource("/Commander16.gif");
    public static final URL COMMANDER_IMG_24 = YukonCommander.class.getResource("/Commander24.gif");
    public static final URL COMMANDER_IMG_32 = YukonCommander.class.getResource("/Commander32.gif");
    public static final URL COMMANDER_IMG_48 = YukonCommander.class.getResource("/Commander48.gif");
    public static final URL COMMANDER_IMG_64 = YukonCommander.class.getResource("/Commander64.gif");
    
    public static List<Image> getIconsImages() {
        
        List<Image> iconsImages = new ArrayList<Image>();
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_16));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_24));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_32));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_48));
        iconsImages.add(Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_64));
        
        return iconsImages;
    }

	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JPanel ivjOutputPanel = null;
	private com.cannontech.common.gui.util.TreeViewPanel ivjTreeViewPanel = null;
	private CommandPanel ivjCommandPanel = null;
	private SerialRoutePanel ivjSerialRoutePanel = null;
	private javax.swing.JSplitPane ivjSplitPane = null;
	private CommandLogPanel ivjCommandLogPanel = null;
	private javax.swing.JLabel ivjCGPMode = null;
	private ClearPrintButtonPanel ivjClearPrintButtons = null;
	private AdvancedOptionsPanel advOptsPanel = null;
	private LocateRouteDialog locRouteDialog = null;
	private DownloadTOUSchedulePanel downloadTOUPanel = null;
	private YCCommandMenu ivjYCCommandMenu = null;
	private YCFileMenu ivjYCFileMenu = null;
	private YCHelpMenu ivjYCHelpMenu = null;
	private YCViewMenu ivjYCViewMenu = null;
	private javax.swing.JMenuBar ivjYukonCommanderJMenuBar = null;
	private javax.swing.JPanel ivjClearPrintPanel = null;
	private javax.swing.JScrollPane ivjDebugOutputScrollPane = null;
	private javax.swing.JScrollPane ivjDisplayOutputScrollPane = null;
	private javax.swing.JPanel ivjExecutionPanel = null;
	private javax.swing.JPanel ivjNavigatorPanel = null;
	private javax.swing.JSplitPane ivjOutputSplitPane = null;
	
	private JTextPane ivjDebugOutputTextPane = null;
	private JTextPane ivjDisplayOutputTextPane = null;

	private final String CLEAR_OUTPUT_DISPLAY = "Clear Display";
	private final String PRINT_OUTPUT_DISPLAY = "Print Display";

	private final String CLEAR_OUTPUT_DEBUG = "Clear Debug";
	private final String PRINT_OUTPUT_DEBUG = "Print Debug";
	
	class WriteOutput implements java.lang.Runnable
	{
		private YC.OutputMessage message = null; 
		private javax.swing.JTextPane textPane = null;
		private String style;
		
		public WriteOutput(javax.swing.JTextPane textPane_, YC.OutputMessage message_) {
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
                if( style != null) {
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
	
	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public YukonCommander() {
		super();
		initialize();
	}
	/**
	 * Displays the about information.
	 */
	private void about()
	{
	    java.awt.Frame f = (JFrame)CtiUtilities.getParentFrame(YukonCommander.this.getContentPane() );
	    AboutDialog aboutDialog = new AboutDialog( f, "About Commander", true );

	    aboutDialog.setLocationRelativeTo( f );
	    aboutDialog.setValue(null);
	    aboutDialog.show();
	}
	
	/**
	 * @see java.awt.event.ActionListener#actionPerformed(ActionEvent)
	 * @param event java.awt.event.ActionEvent
	 */
	public void actionPerformed(ActionEvent event)
	{
		if( event.getSource() == getTreeViewPanel().getSortByComboBox())
			treeModelChanged(); 
	
		else if (event.getSource() == getYCViewMenu().reloadMenuItem)
			reloadDevices();
	
		else if (event.getSource() == getSerialRoutePanel().getSerialTextField())
			serialNumberAction();
	
		else if (event.getSource() == getSerialRoutePanel().getRouteComboBox())
		{
			Object selected = getSerialRoutePanel().getRouteComboBox().getSelectedItem();
			if( selected instanceof LiteYukonPAObject)
				setRouteID(((LiteYukonPAObject)selected).getYukonID());
			else
				setRouteID(-1);	//set it to an invalid route
				
			CTILogger.info(selected);
		}
	
		else if( event.getSource() == getCommandPanel().getExecuteButton() ||
				event.getSource() == getYCCommandMenu().executeMenuItem )
		{
		    getYCFileMenu().updateRecentList(getYC().getTreeItem());
			String commandString = (String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim();
			try {
                setCommand(commandString);
				if( isValidSetup() ) {
					getCommandPanel().enter(getCommand());
					getYC().executeCommand();
				}
			} catch (PaoAuthorizationException e) {
                update(yc, yc.new OutputMessage(YC.OutputMessage.DEBUG_MESSAGE, "\n ** You do not have permission to execute command: " + e.getPermission(), MessageType.ERROR));
			}
		}
		
		else if( event.getSource() == getCommandPanel().getStopButton() ||
				event.getSource() == getYCCommandMenu().stopMenuItem )
		{
			if( !getPilConn().isValid() )
			{
				getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
				return;
			}
			getYC().stop();
			getDebugOutputTextPane().setCaretPosition( getDebugOutputTextPane().getDocument().getEndPosition().getOffset() - 1 );
		}
	
		else if( event.getSource() == getCommandPanel().getAvailableCommandsComboBox())
		{
			if( getCommandPanel().getAvailableCommandsComboBox().getSelectedIndex() > 0) //0 is default "select"
			{
				String rawCommandString = getYC().getCommandFromLabel(getCommandPanel().getAvailableCommandsComboBox().getSelectedItem().toString() );
				String commandString = DaoFactory.getCommandDao().loadPromptValue(rawCommandString.trim(), this);
	            if( commandString != null) { //null is a cancel from prompt
    				getCommandPanel().getExecuteCommandComboBoxTextField().setText( commandString );
    				getCommandPanel().getExecuteButton().requestFocusInWindow();
	            }
			}
			
		}
		else if( event.getSource() == getYCCommandMenu().editCustomCommandFile)
		{
			if( getTreeViewPanel().getSelectedItem() == null )
			{
				getCommandLogPanel().addLogElement(" ** Warning: Please make a selection from the tree.");
			}
			else if( getTreeViewPanel().getSelectedNode().getParent() == null )
			{
				getCommandLogPanel().addLogElement(" ** Warning: Please select a specific tree item");
			}
			else
			{			
				DeviceTypeCommandSetupPanel commandEditPanel = new DeviceTypeCommandSetupPanel(getYC().getDeviceType());
				commandEditPanel.setDialogTitle("DeviceType: " + getYC().getDeviceType());
				commandEditPanel.showCommandSetup(this);
				//set the deviceType in order to reload the deviceTypeCommands
				getYC().setDeviceType(getYC().getDeviceType());
				//update the CommandExecute panel
				updateCommandSelection();
			}			
		}
		else if( event.getSource() == getYCCommandMenu().installAddressing)
		{
			javax.swing.ImageIcon icon = new javax.swing.ImageIcon(COMMANDER_IMG_24);
			Object[] selections = null;			
			// Get an instance of the cache.
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
	
			synchronized(cache)
			{
				List<LiteYukonPAObject> allLM = cache.getAllLoadManagement();
				java.util.Collections.sort( allLM, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
	
				Vector lmGroups = new Vector(allLM.size());
				for (LiteYukonPAObject liteYukonPAObject : allLM) {
					if (liteYukonPAObject.getPaoType() == PaoType.LM_GROUP_EXPRESSCOMM ||
							liteYukonPAObject.getPaoType() == PaoType.LM_GROUP_VERSACOM	)
						lmGroups.add(liteYukonPAObject);
				}
				selections = lmGroups.toArray();
			}
			
			Object value = javax.swing.JOptionPane.showInputDialog(this, "Load Group Template", "Select the Addressing Template to Install", javax.swing.JOptionPane.QUESTION_MESSAGE, icon, selections, null);
			if( value != null )//OK selected	
			{
                try {
    				setCommand("putconfig template \'"+ value.toString()+"\'");
    				if( isValidSetup() )
    				{
    					getCommandPanel().enter(getCommand());
    					getYC().executeCommand();
    				}
                } catch (PaoAuthorizationException e) {
                    update(yc, yc.new OutputMessage(YC.OutputMessage.DEBUG_MESSAGE, "\n ** You do not have permission to execute command: " + e.getPermission(), MessageType.ERROR));
                }
			}
		}
		else if( event.getSource() == getYCCommandMenu().locateRoute )
		{
			getLocateRouteDialog().getDeviceNameTextField().setText(getTreeViewPanel().getSelectedItem().toString());		
			getLocateRouteDialog().showLocateDialog();

			Object selected = getLocateRouteDialog().getRouteComboBox().getSelectedItem();
			if( selected instanceof LiteYukonPAObject)
				setRouteID(((LiteYukonPAObject)selected).getYukonID());
		}
		else if( event.getSource() == getYCCommandMenu().downloadSchedule)
		{
			getDownloadTOUDialog().getDeviceNameTextField().setText(getTreeViewPanel().getSelectedItem().toString());		
			Object lShedule = getDownloadTOUDialog().showDownloadOptions(this);

			if(lShedule != null && lShedule instanceof LiteTOUSchedule)
			{
                try {
    			    setCommand(getYC().buildTOUScheduleCommand(((LiteTOUSchedule)lShedule).getScheduleID()));
    				if( isValidSetup() )
    					getYC().executeCommand();
                } catch (PaoAuthorizationException e) {
                    update(yc, yc.new OutputMessage(YC.OutputMessage.DEBUG_MESSAGE, "\n ** You do not have permission to execute command: " + e.getPermission(), MessageType.ERROR));
                }
			}
		}
		else if ( event.getSource() == getLocateRouteDialog().getLocateButton())
		{
            try {
    			setCommand("loop locateroute");
    			if( isValidSetup() )
    			{
    				getCommandPanel().enter(getCommand());
    				getYC().executeCommand();
    			}
            } catch (PaoAuthorizationException e) {
                update(yc, yc.new OutputMessage(YC.OutputMessage.DEBUG_MESSAGE, "\n ** You do not have permission to execute command: " + e.getPermission(), MessageType.ERROR));
            }
		}
		else if (event.getSource() == getLocateRouteDialog().getRouteComboBox())
		{
			Object selected = getLocateRouteDialog().getRouteComboBox().getSelectedItem();
			if( selected instanceof LiteYukonPAObject)
				setRouteID(((LiteYukonPAObject)selected).getYukonID());
		}

		else if( event.getSource() == getYCFileMenu().printMenuItem || 
			event.getSource() == getClearPrintButtons().getPrintButton() )
		{
			print(getDebugOutputTextPane());
			print(getDisplayOutputTextPane());			
		}
		else if( event.getSource() == getClearPrintButtons().getClearButton() ||
				event.getSource() == getYCViewMenu().clearMenuItem )
		{
			getDebugOutputTextPane().setText("");
			getDisplayOutputTextPane().setText("");
		} 
		
		else if( event.getSource() == getYCFileMenu().saveMenuItem )
			save(getDebugOutputTextPane());
	
		else if( event.getSource() == getYCViewMenu().searchMenuItem)
		{
			String value = javax.swing.JOptionPane.showInputDialog(
				this, "Name of the item: ", "Find",
				javax.swing.JOptionPane.QUESTION_MESSAGE );
	
			if( value != null )
			{
	
				boolean found = getTreeViewPanel().searchFirstLevelString(value);
	
				if( !found )
					javax.swing.JOptionPane.showMessageDialog(
						this, "Unable to find your selected item", "Item Not Found",
						javax.swing.JOptionPane.INFORMATION_MESSAGE );
			}
		}
		else if( event.getSource() == getYCFileMenu().getRecentItem0() )
		{
		    selectRecentItem(0);
		}
		else if( event.getSource() == getYCFileMenu().getRecentItem1() )
		{
		    selectRecentItem(1);
		}
		else if( event.getSource() == getYCFileMenu().getRecentItem2() )
		{
		    selectRecentItem(2);
		}
		else if( event.getSource() == getYCFileMenu().getRecentItem3() )
		{
		    selectRecentItem(3);
		}
		else if( event.getSource() == getYCFileMenu().getRecentItem4() )
		{
		    selectRecentItem(4);
		}
		else if( event.getSource() == getYCFileMenu().commandSpecificControl )
		{
			if( getYC().getCommandMode() == YC.DEFAULT_MODE)
			{
				// Turn CGP mode ON
				getYC().setCommandMode(YC.CGP_MODE);
				CTILogger.info(" ** CGPMODE IS ON ** ");
				getCGPMode().setText("CGPMODE ON:  Sending \'Execute Command\' string. (CTRL + F5)");
			}
			else
			{
				//Turn DEFAULT mode ON
				getYC().setCommandMode(YC.DEFAULT_MODE);
				CTILogger.info(" ** CGPMODE IS OFF ** ");
				getCGPMode().setText("");
			}
		}

		else if( event.getSource() == getYCHelpMenu().aboutMenuItem)
			about();
	
		else if( event.getSource() == getYCHelpMenu().helpTopicMenuItem)
			CtiUtilities.showHelp( HELP_FILE );
	
		else if( event.getSource() == getYCViewMenu().deleteSerialNumberMenuItem)
			deleteSerialNumber();
		
		else if( event.getSource() == getYCCommandMenu().advancedOptionsMenuItem )
		{
			YCDefaults defaults = getAdvOptsPanel().showAdvancedOptions(this);
			if (defaults != null)
			{
				getCommandLogPanel().setVisible( defaults.getShowMessageLog() );			
				getYC().setYCDefaults(defaults);
			}
			advOptsPanel = null;
		}

		else if( event.getSource() == getYCFileMenu().exitMenuItem )
			exit();
		
		else if (event.getActionCommand().equalsIgnoreCase(CLEAR_OUTPUT_DISPLAY))
		{
			getDisplayOutputTextPane().setText("");
		}
		else if( event.getActionCommand().equalsIgnoreCase(PRINT_OUTPUT_DISPLAY))
		{
			print(getDisplayOutputTextPane());
		}
		else if (event.getActionCommand().equalsIgnoreCase(CLEAR_OUTPUT_DEBUG))
		{
			getDebugOutputTextPane().setText("");
		}
		else if( event.getActionCommand().equalsIgnoreCase(PRINT_OUTPUT_DEBUG))
		{
			print(getDebugOutputTextPane());
		}
	}
	
	private void selectRecentItem(int index)
	{
	    Object obj = getYCFileMenu().getRecentItems()[index];
	    if( obj instanceof LiteBase)
	        getTreeViewPanel().selectLiteObject((LiteBase)obj);
	    else if (obj instanceof String)
	        getTreeViewPanel().selectByString((String)obj);
	    
	    if (getTreeViewPanel().getSelectedNode() == null)
	    {
	        getTreeViewPanel().clearSelection();
	        setTreeItem(getYCFileMenu().getRecentItems()[index]);
	    }

	}

	/**
	 * Returns an integer [YES/NO] indicating the option selected from a yes/no popupFrame.
	 * @param message java.lang.String
	 */
	private int areYouSure(String message, int messageType )
	{
		javax.swing.JFrame popupFrame = new javax.swing.JFrame();
		popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(COMMANDER_IMG_24));
		return javax.swing.JOptionPane.showConfirmDialog(popupFrame, message, YC_TITLE, javax.swing.JOptionPane.YES_NO_OPTION, messageType);
	}

	/**
	 * Delete the selectedNode from getTreeViewPanel().
	 * Removes the selectedNode (serial number) from getSerialNumberVector().
	 */
	private void deleteSerialNumber()
	{
		if(TreeModelEnum.isEditableSerial(getModelType()))
		{
			if (getTreeViewPanel().getSelectedItem() == null)
				return;
				
			else
			{
				if( getTreeViewPanel().getSelectedNode().getParent() == null)	// can't delete the parent node.
					return;
					
				((EditableTextModel)getTreeViewPanel().getSelectedTreeModel()).getEntryVector().remove( getTreeViewPanel().getSelectedItem());
					
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
	private void enableSerialAndRoute(boolean value)
	{
		getSerialRoutePanel().setObjectsEnabled(value);
		getYCViewMenu().deleteSerialNumberMenuItem.setEnabled(value);
	}

	/**
	 * Exit application.
	 */
	private void exit()
	{
		getYC().getYCDefaults().setOutputDividerLoc(getOutputSplitPane().getDividerLocation());
		getYC().getYCDefaults().writeDefaultsFile();
	
		System.exit(0);
	}

	/**
	 * @see java.awt.event.FocusListener#focusGained(FocusEvent)
	 */
	public void focusGained(java.awt.event.FocusEvent event) {}

	/**
	 * 
	 * @see java.awt.event.FocusListener#focusLost(FocusEvent)
	 */
	public void focusLost(java.awt.event.FocusEvent event)
	{
		if (event.getSource() == getSerialRoutePanel().getSerialTextField())
		{
			serialNumberAction();	
		}
	}

	/**
	 * Returns the advOptsPanel.
	 * @return AdvancedOptionsPanel
	 */
	public AdvancedOptionsPanel getAdvOptsPanel()
	{
		if( advOptsPanel == null)
		{
			advOptsPanel  = new AdvancedOptionsPanel(getYC().getYCDefaults());
		}
		return advOptsPanel;
	}
	
	/**
	 * Returns the ivjCGPMode.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getCGPMode() {
		if (ivjCGPMode == null) {
			try {
				ivjCGPMode = new javax.swing.JLabel();
				ivjCGPMode.setName("CGPMode");
				ivjCGPMode.setText("");
				ivjCGPMode.setForeground(java.awt.Color.red);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCGPMode;
	}
	
	/**
	 * Returns the ivjClearPrintButtons.
	 * @return com.cannontech.clientutils.commander.ClearPrintButtonPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private ClearPrintButtonPanel getClearPrintButtons() {
		if (ivjClearPrintButtons == null) {
			try {
				ivjClearPrintButtons = new ClearPrintButtonPanel();
				ivjClearPrintButtons.setName("ClearPrintButtons");
				// user code begin {1}
				ivjClearPrintButtons.getClearButton().addActionListener(this);
				ivjClearPrintButtons.getPrintButton().addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjClearPrintButtons;
	}
	
	/**
	 * Returns the ivjClearPrintPanel.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getClearPrintPanel() {
		if (ivjClearPrintPanel == null) {
			try {
				ivjClearPrintPanel = new javax.swing.JPanel();
				ivjClearPrintPanel.setName("ClearPrintPanel");
				ivjClearPrintPanel.setLayout(new java.awt.BorderLayout());
				getClearPrintPanel().add(getCGPMode(), "West");
				getClearPrintPanel().add(getClearPrintButtons(), "East");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjClearPrintPanel;
	}

	/**
	 * Returns the ycClass.getCommand().
	 * @return java.lang.String
	 */
	private String getCommand()
	{
		return getYC().getCommandString();
		
	}
	
	/**
	 * Returns the ivjCommandLogPanel.
	 * @return com.cannontech.clientutils.commander.CommandLogPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public CommandLogPanel getCommandLogPanel() {
		if (ivjCommandLogPanel == null) {
			try {
				ivjCommandLogPanel = new CommandLogPanel();
				ivjCommandLogPanel.setName("CommandLogPanel");
				// user code begin {1}
				ivjCommandLogPanel.setVisible( getYC().getYCDefaults().getShowMessageLog() );
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCommandLogPanel;
	}
	
	/**
	 * Returns the ivjCommandPanel.
	 * @return com.cannontech.clientutils.commander.CommandPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private CommandPanel getCommandPanel() {
		if (ivjCommandPanel == null) {
			try {
				ivjCommandPanel = new CommandPanel();
				ivjCommandPanel.setName("CommandPanel");
				// user code begin {1}
				ivjCommandPanel.getExecuteButton().addActionListener(this);
				ivjCommandPanel.getStopButton().addActionListener(this);
	
				ivjCommandPanel.getExecuteCommandComboBoxTextField().addKeyListener(this);
				ivjCommandPanel.getExecuteCommandComboBoxTextField().addActionListener(this);
				ivjCommandPanel.getAvailableCommandsComboBox().addActionListener(this);
				ivjCommandPanel.getExecuteButton().addKeyListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
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
    private IServerConnection getPilConn()
    {
        return ConnPool.getInstance().getDefPorterConn();        
    }

	/**
	 * Returns the ivjDebugOutputScrollPane.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getDebugOutputScrollPane() {
		if (ivjDebugOutputScrollPane == null) {
			try {
				ivjDebugOutputScrollPane = new javax.swing.JScrollPane();
				ivjDebugOutputScrollPane.setName("DebugOutputScrollPane");
				ivjDebugOutputScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				getDebugOutputScrollPane().setViewportView(getDebugOutputTextPane());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDebugOutputScrollPane;
	}
	
	/**
	 * Returns the ivjDebugOutputTextPane.
	 * @return javax.swing.JTextPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JTextPane getDebugOutputTextPane() {
		if (ivjDebugOutputTextPane == null) {
			try {
				ivjDebugOutputTextPane = new JTextPane();
				ivjDebugOutputTextPane.setName("DebugOutputTextPane");
				ivjDebugOutputTextPane.setBounds(0, 0, 11, 6);
                ivjDebugOutputTextPane.setContentType("text/html");
                
                for (MessageType messageType : MessageType.values()) {
                    Style style = ivjDebugOutputTextPane.addStyle(messageType.getStyleString(), null);
                    StyleConstants.setForeground(style, messageType.getColor());
                }
                
                Style style = ivjDebugOutputTextPane.addStyle("Font", null);
                StyleConstants.setFontSize(style, 12);
                StyleConstants.setFontFamily(style, "sans-serif");

                
				// user code begin {1}
				ivjDebugOutputTextPane.addMouseListener(this);				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDebugOutputTextPane;
	}
	
	/**
	 * Returns the ivjDisplayOutputScrollPane.
	 * @return javax.swing.JScrollPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JScrollPane getDisplayOutputScrollPane() {
		if (ivjDisplayOutputScrollPane == null) {
			try {
				ivjDisplayOutputScrollPane = new javax.swing.JScrollPane();
				ivjDisplayOutputScrollPane.setName("DisplayOutputScrollPane");
				ivjDisplayOutputScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				ivjDisplayOutputScrollPane.setViewportView(getDisplayOutputTextPane());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDisplayOutputScrollPane;
	}
	
	/**
	 * Returns the ivjDisplayOutputTextPane.
	 * @return javax.swing.JTextPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private JTextPane getDisplayOutputTextPane() {
		if (ivjDisplayOutputTextPane == null) {
			try {
				ivjDisplayOutputTextPane = new JTextPane();
				ivjDisplayOutputTextPane.setName("DisplayOutputTextPane");
				ivjDisplayOutputTextPane.setBounds(0, 0, 11, 6);
                ivjDisplayOutputTextPane.setContentType("text/html");
                
                for (MessageType messageType : MessageType.values()) {
                    Style style = ivjDisplayOutputTextPane.addStyle(messageType.getStyleString(), null);
                    StyleConstants.setForeground(style, messageType.getColor());
                }

                Style style = ivjDisplayOutputTextPane.addStyle("Font", null);
                StyleConstants.setFontSize(style, 12);
                StyleConstants.setFontFamily(style, "sans-serif");
				// user code begin {1}
				ivjDisplayOutputTextPane.addMouseListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjDisplayOutputTextPane;
	}
	/**
	 * Returns the ivjExecutionPanel.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getExecutionPanel() {
		if (ivjExecutionPanel == null) {
			try {
				ivjExecutionPanel = new javax.swing.JPanel();
				ivjExecutionPanel.setName("ExecutionPanel");
				ivjExecutionPanel.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsCommandPanel = new java.awt.GridBagConstraints();
				constraintsCommandPanel.gridx = 0; constraintsCommandPanel.gridy = 0;
				constraintsCommandPanel.fill = java.awt.GridBagConstraints.BOTH;
				constraintsCommandPanel.anchor = java.awt.GridBagConstraints.NORTH;
				constraintsCommandPanel.weightx = 1.0;
				constraintsCommandPanel.insets = new java.awt.Insets(5, 5, 5, 5);
				getExecutionPanel().add(getCommandPanel(), constraintsCommandPanel);
	
				java.awt.GridBagConstraints constraintsOutputPanel = new java.awt.GridBagConstraints();
				constraintsOutputPanel.gridx = 0; constraintsOutputPanel.gridy = 1;
				constraintsOutputPanel.fill = java.awt.GridBagConstraints.BOTH;
				constraintsOutputPanel.weightx = 1.0;
				constraintsOutputPanel.weighty = 1.0;
				constraintsOutputPanel.insets = new java.awt.Insets(5, 5, 5, 5);
				getExecutionPanel().add(getOutputPanel(), constraintsOutputPanel);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjExecutionPanel;
	}
	
	/**
	 * Returns the ivjJFrameContentPane.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJFrameContentPane() {
		if (ivjJFrameContentPane == null) {
			try {
				ivjJFrameContentPane = new javax.swing.JPanel();
				ivjJFrameContentPane.setName("JFrameContentPane");
				ivjJFrameContentPane.setLayout(new java.awt.BorderLayout());
				getJFrameContentPane().add(getSplitPane(), "Center");
				getJFrameContentPane().add(getCommandLogPanel(), "South");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJFrameContentPane;
	}
	
	/**
	 * Returns the locRouteDialog.
	 * @return LocateRouteDialog
	 */
	private LocateRouteDialog getLocateRouteDialog()
	{
		if( locRouteDialog == null)
		{
			locRouteDialog = new LocateRouteDialog(this, "LocateRoute", true);
			locRouteDialog.getLocateButton().addActionListener(this);
			locRouteDialog.getRouteComboBox().addActionListener(this);
		}
		return locRouteDialog;
	}
	
	/**
	 * Returns the downloadTOUDialog
	 * @return
	 */
	private DownloadTOUSchedulePanel getDownloadTOUDialog()
	{
		if( downloadTOUPanel == null)
		{
			downloadTOUPanel  = new DownloadTOUSchedulePanel();
			downloadTOUPanel.addItems();
			downloadTOUPanel.getTOUScheduleComboBox().addActionListener(this);
		}
		return downloadTOUPanel;
	}	
	/**
	 * Returns ycClass.getModelType().
	 * @return int com.cannontech.database.model.TreeModelEnum.
	 */
	private Class<? extends LiteBaseTreeModel> getModelType()
	{
		return getYC().getModelType();
	}
	
	/**
	 * Returns the ivjNavigatorPanel.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getNavigatorPanel() {
		if (ivjNavigatorPanel == null) {
			try {
				ivjNavigatorPanel = new javax.swing.JPanel();
				ivjNavigatorPanel.setName("NavigatorPanel");
				ivjNavigatorPanel.setLayout(new java.awt.BorderLayout());
				getNavigatorPanel().add(getSerialRoutePanel(), "North");
				getNavigatorPanel().add(getTreeViewPanel(), "Center");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNavigatorPanel;
	}
	/**
	 * Returns the ivjOutputPanel.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getOutputPanel() {
		if (ivjOutputPanel == null) {
			try {
				ivjOutputPanel = new javax.swing.JPanel();
				ivjOutputPanel.setName("OutputPanel");
				ivjOutputPanel.setLayout(new java.awt.BorderLayout());
				getOutputPanel().add(getClearPrintPanel(), "South");
				getOutputPanel().add(getOutputSplitPane(), "Center");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjOutputPanel;
	}
	
	/**
	 * Returns the ivjOutputSplitPane.
	 * @return javax.swing.JSplitPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JSplitPane getOutputSplitPane() {
		if (ivjOutputSplitPane == null) {
			try {
				ivjOutputSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.VERTICAL_SPLIT);
				ivjOutputSplitPane.setName("OutputSplitPane");
				ivjOutputSplitPane.setDividerSize(10);
				ivjOutputSplitPane.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
				ivjOutputSplitPane.setDividerLocation(190);
				ivjOutputSplitPane.setOneTouchExpandable(true);
				getOutputSplitPane().add(getDebugOutputScrollPane(), "bottom");
				getOutputSplitPane().add(getDisplayOutputScrollPane(), "top");
				// user code begin {1}
				ivjOutputSplitPane.setDividerLocation(getYC().getYCDefaults().getOutputDividerLoc());
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjOutputSplitPane;
	}
	
	/**
	 * Returns the ycClass.getSerialNumber().
	 * @return String
	 */
	private String getSerialNumber()
	{
		return getYC().getSerialNumber();
	}
	
	/**
	 * Returns the ivjSerialRoutePanel.
	 * @return com.cannontech.clientutils.commander.SerialRoutePanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private SerialRoutePanel getSerialRoutePanel() {
		if (ivjSerialRoutePanel == null) {
			try {
				ivjSerialRoutePanel = new SerialRoutePanel();
				ivjSerialRoutePanel.setName("SerialRoutePanel");
				// user code begin {1}
				ivjSerialRoutePanel.getSerialTextField().addKeyListener(this);
				ivjSerialRoutePanel.getSerialTextField().addActionListener(this);
				ivjSerialRoutePanel.getSerialTextField().addFocusListener(this);
				ivjSerialRoutePanel.getRouteComboBox().addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjSerialRoutePanel;
	}
	
	/**
	 * Returns the ivjSplitPane.
	 * @return javax.swing.JSplitPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JSplitPane getSplitPane() {
		if (ivjSplitPane == null) {
			try {
				ivjSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
				ivjSplitPane.setName("SplitPane");
				ivjSplitPane.setDividerSize(5);
				getSplitPane().add(getExecutionPanel(), "right");
				getSplitPane().add(getNavigatorPanel(), "left");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjSplitPane;
	}

	/**
	 * Returns the ycClass.getTreeItem().
	 * @return java.lang.Object
	 */
//	private Object getTreeItem()
//	{
//		return ycClass.getTreeItem();
//	}

	/**
	 * Returns the ivjTreeViewPanel.
	 * @return com.cannontech.common.gui.util.TreeViewPanel
	 */
	private com.cannontech.common.gui.util.TreeViewPanel getTreeViewPanel() {
		if (ivjTreeViewPanel == null) {
			try {
				ivjTreeViewPanel = new com.cannontech.common.gui.util.TreeViewPanel();
				ivjTreeViewPanel.setName("TreeViewPanel");
				// user code begin {1}
				ivjTreeViewPanel.addTreeSelectionListener(this);
				ivjTreeViewPanel.getSortByComboBox().addActionListener(this);
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTreeViewPanel;
	}
	
	/**
	 * Returns the ivjYCCommandMenu.
	 * @return com.cannontech.clientutils.commander.menu.YCCommandMenu
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private YCCommandMenu getYCCommandMenu() {
		if (ivjYCCommandMenu == null) {
			try {
				ivjYCCommandMenu = new YCCommandMenu();
				ivjYCCommandMenu.setName("YCCommandMenu");
				ivjYCCommandMenu.setText("Command");
				// user code begin {1}
				javax.swing.JMenuItem item;
				for (int i = 0; i < ivjYCCommandMenu.getItemCount(); i++)
				{
					item = ivjYCCommandMenu.getItem(i);
	
					if (item != null)
						ivjYCCommandMenu.getItem(i).addActionListener(this);
				}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjYCCommandMenu;
	}
	
	/**
	 * Returns the ivjYCFileMenu.
	 * @return com.cannontech.clientutils.commander.menu.YCFileMenu
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private YCFileMenu getYCFileMenu() {
	    if (ivjYCFileMenu == null) {
	        try {
	            ivjYCFileMenu = new YCFileMenu();
	            ivjYCFileMenu.setName("YCFileMenu");
	            ivjYCFileMenu.setText("File");
	            // user code begin {1}
	            JMenuItem item;
	        	
	            for (int i = 0; i < ivjYCFileMenu.getItemCount(); i++) {
	                item = ivjYCFileMenu.getItem(i);
	
	                if (item != null)
	                    ivjYCFileMenu.getItem(i).addActionListener(this);
	            }
	            // user code end
	        } catch (java.lang.Throwable ivjExc) {
	            // user code begin {2}
	            // user code end
	            handleException(ivjExc);
	        }
	    }
	    return ivjYCFileMenu;
	}
	
	/**
	 * Returns the ivjYCHelpMenu.
	 * @return com.cannontech.clientutils.commander.menu.YCHelpMenu
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private YCHelpMenu getYCHelpMenu() {
		if (ivjYCHelpMenu == null) {
			try {
				ivjYCHelpMenu = new YCHelpMenu();
				ivjYCHelpMenu.setName("YCHelpMenu");
				ivjYCHelpMenu.setText("Help");
				// user code begin {1}
				javax.swing.JMenuItem item;
	
				for (int i = 0; i < ivjYCHelpMenu.getItemCount(); i++) {
					item = ivjYCHelpMenu.getItem(i);
	
					if (item != null)
						ivjYCHelpMenu.getItem(i).addActionListener(this);
				}
	
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjYCHelpMenu;
	}
	
	/**
	 * Returns the ivjYCViewMenu.
	 * @return com.cannontech.clientutils.commander.menu.YCViewMenu
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private YCViewMenu getYCViewMenu() {
		if (ivjYCViewMenu == null) {
			try {
				ivjYCViewMenu = new YCViewMenu();
				ivjYCViewMenu.setName("YCViewMenu");
				ivjYCViewMenu.setText("View");
				// user code begin {1}
				javax.swing.JMenuItem item;
	
				for (int i = 0; i < ivjYCViewMenu.getItemCount(); i++) {
					item = ivjYCViewMenu.getItem(i);
	
					if (item != null)
						ivjYCViewMenu.getItem(i).addActionListener(this);
				}
	
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjYCViewMenu;
	}
	
	/**
	 * Returns the ivjYukonCommanderJMenuBar.
	 * @return javax.swing.JMenuBar
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JMenuBar getYukonCommanderJMenuBar() {
		if (ivjYukonCommanderJMenuBar == null) {
			try {
				ivjYukonCommanderJMenuBar = new javax.swing.JMenuBar();
				ivjYukonCommanderJMenuBar.setName("YukonCommanderJMenuBar");
				getYukonCommanderJMenuBar().add(getYCFileMenu());
				getYukonCommanderJMenuBar().add(getYCViewMenu());
				getYukonCommanderJMenuBar().add(getYCCommandMenu());
				getYukonCommanderJMenuBar().add(getYCHelpMenu());
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjYukonCommanderJMenuBar;
	}
	
    /**
     * @see com.cannontech.database.cache.DBChangeListener#handleDBChangeMsg(DBChangeMsg, LiteBase)
     */
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
                    if(msg.getCategory().equals(PAOGroups.STRING_CAT_ROUTE)){
                        updateRouteCombo(msg.getDbChangeType(), object);
                    }
                    
                    // Update the tree
                    Object selectedObject = getTreeViewPanel().getSelectedItem();
                    getTreeViewPanel().processDBChange(msg.getDbChangeType(), object);
                    
                    //if we had something selected we just lost it... so reselect it now
                    if ( selectedObject != null ) {
                        getTreeViewPanel().selectByString( selectedObject.toString() );
                    }
                    
                    // Update TOUSchedule list if a tou schedule msg is received
                    if (msg.getDatabase() == DBChangeMsg.CHANGE_TOU_SCHEDULE_DB
                            && msg.getCategory().equals(DBChangeMsg.CAT_TOU_SCHEDULE)
                            && msg.getObjectType().equals(DBChangeMsg.CAT_TOU_SCHEDULE)) {
                        getDownloadTOUDialog().addItems();
                    }
                    
                } catch( Exception e ) {
                    e.printStackTrace();
                } finally {
                    getRootPane().setCursor( savedCursor);
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
                        route.retrieve(CtiUtilities.getDatabaseAlias());
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
    
	
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */
	private static void handleException(Throwable exception) {
	
		/* Uncomment the following lines to print uncaught exceptions to stdout */
		CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
		exception.printStackTrace(System.out);
	}

	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("YukonCommander");
			setJMenuBar(getYukonCommanderJMenuBar());
			setContentPane(getJFrameContentPane());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
		  	
		//--------Setup treeViewPanel and tree models-------------	
		//This model is created in a weird way.  We can do the model creation similar for all
		// the models except for the Device model.  In order to use a different constructor
		// that shows the device types without their points, we have to specifically look for
		// that model type.  Create a new model for that type (false sets showPoints off).
		// For all other 'normal' types, we can follow through with OO and use the same
		// constructor template (no parameters).
		List<LiteBaseTreeModel> models = new ArrayList<LiteBaseTreeModel>();
		
		TreeModelEnum[] modelIds = getTreeModels();
        for (int i = 0; i < modelIds.length; i++)
		{
			if( modelIds[i] == TreeModelEnum.DEVICE)
				models.add(new com.cannontech.database.model.DeviceTreeModel(false));
			else if ( modelIds[i] == TreeModelEnum.LMGROUPS)
				models.add(new com.cannontech.database.model.LMGroupsModel(false));
			else if ( modelIds[i] == TreeModelEnum.CAPBANKCONTROLLER )
				models.add(new com.cannontech.database.model.CapBankControllerModel(false));
			else if ( modelIds[i] == TreeModelEnum.TRANSMITTER )
				models.add(new com.cannontech.database.model.TransmitterTreeModel(false));
			else
				models.add(TreeModelEnum.create(modelIds[i]));
		}
        
        // add the group model
        DeviceGroupTreeFactory dgtf = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
        LiteBaseTreeModel liteBaseDeviceGroupModel = dgtf.getLiteBaseModel(false);
        models.add(liteBaseDeviceGroupModel);

		//serial and route panel visible only when first item in tree is Versacom Serial #
		if (!TreeModelEnum.isEditableSerial(models.get(0).getClass())) {
            enableSerialAndRoute(false);
        }
	
		getTreeViewPanel().setTreeModels(models.toArray(new LiteBaseTreeModel[]{}));
			
		setRouteModel(); //fill route combo box

        AsyncDynamicDataSource dataSource =  (AsyncDynamicDataSource) YukonSpringHook.getBean("asyncDynamicDataSource");
        dataSource.addDBChangeLiteListener(this);
        
		addWindowListener(new java.awt.event.WindowAdapter(){
			public void windowClosing(java.awt.event.WindowEvent e){ 
					exit();
			};
		});
		getYC().addObserver(this);
		getTreeViewPanel().getTree().setSelectionInterval(0,0);
		
		paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
		// user code end
	}
	
	/**
	 * Returns true when setup is fully complete, all connections exist/valid.
	 * @return boolean
	 */
	private boolean isValidSetup()
	{
		if( !getPilConn().isValid() )

            {
                getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
            }            
		{
			if( getYC().getCommandMode() == YC.CGP_MODE )//CGPMode - User determines validity, not code.
			{
				getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
				return true;	
			}
			else if( getTreeViewPanel().getSelectedItem() == null )
			{
				getCommandLogPanel().addLogElement(" ** Warning: Please make a selection from the tree.");
			}
			else if( getCommand() == null || getCommand().length() == 0 )
			{
				getCommandLogPanel().addLogElement(" ** Warning: No command is specified");
			}
			else if( getTreeViewPanel().getSelectedNode().getParent() == null )
			{
				getCommandLogPanel().addLogElement(" ** Warning: Please select a specific tree item");
			}
			else
			{
				if( getYC().getYCDefaults().getConfirmCommandExecute())
				{
					String message = "Execute '"+ getCommand() +"' -- Are You Sure?";
					int response = areYouSure(message, javax.swing.JOptionPane.QUESTION_MESSAGE);
					if( response == javax.swing.JOptionPane.OK_OPTION )
					{
						getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
						return true;
					}
				}
				else
				{
					getCommandPanel().getExecuteCommandComboBoxTextField().requestFocus();
					return true;
				}
			}
		}

		return false;
	}
	
	/**
	 * @see java.awt.event.KeyListener#keyPressed(KeyEvent)
	 */
	public void keyPressed(KeyEvent event) {
		
		if( event.getKeyCode() == KeyEvent.VK_ENTER && event.getSource() == getCommandPanel().getExecuteCommandComboBoxTextField() ||
				event.getKeyCode() == KeyEvent.VK_ENTER && event.getSource() == getCommandPanel().getExecuteButton())
		{
			String commandString = DaoFactory.getCommandDao().loadPromptValue((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim(), this);
			if (commandString != null)	//null is a cancel from prompt
			{
			    try {
    				setCommand(commandString);			
    				if( isValidSetup())
    				{
    					getCommandPanel().enter(getCommand());
    					getYC().executeCommand();
    				}
                } catch (PaoAuthorizationException e) {
                    update(yc, yc.new OutputMessage(YC.OutputMessage.DEBUG_MESSAGE, "\n ** You do not have permission to execute command: " + e.getPermission(), MessageType.ERROR));
                }
			}
		}
		
		else if( event.getKeyCode() == KeyEvent.VK_ENTER && event.getSource() == getSerialRoutePanel().getSerialTextField())
		{
			serialNumberAction();
		}
	}
	/**
	 * @see java.awt.event.KeyListener#keyReleased(KeyEvent)
	 */
	public void keyReleased(KeyEvent event) {
	}
	/**
	 * @see java.awt.event.KeyListener#keyTyped(KeyEvent)
	 */
	public void keyTyped(KeyEvent event) {
	}
	
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args)
	{
		try
		{
	        ClientStartupHelper clientStartupHelper = new ClientStartupHelper();
	        clientStartupHelper.setAppName("Commander");
	        clientStartupHelper.setRequiredRole(CommanderRole.ROLEID);
	        clientStartupHelper.setSplashUrl(CtiUtilities.COMMANDER_SPLASH);

	        clientStartupHelper.doStartup();
	
			final YukonCommander ycClient = new YukonCommander();
			
			clientStartupHelper.setParentFrame(ycClient);
			
			ycClient.setIconImages(getIconsImages());
						
			ycClient.setTitle(YC_TITLE);
	
			//set the app to start as close to the center as you can....
			//  only works with small gui interfaces.

			javax.swing.SwingUtilities.invokeLater(new Runnable() {
			    public void run() {
                    ycClient.setVisible(true);
//                  ycClient.getTreeViewPanel().getTree().setSelectionInterval(1,1);
                    ycClient.getTreeViewPanel().getTree().requestFocusInWindow();
                }
            });

		}
		catch (Throwable e)
		{
			CTILogger.error("Exception occurred in main() of javax.swing.JFrame",e);
			System.exit(-1);
		}
	}
	
	/**
	 * Print the graphics from printTextPane.
	 */
	private void print(JTextPane printTextPane)
	{
		
		java.awt.print.PrinterJob pj = java.awt.print.PrinterJob.getPrinterJob();
		if (pj.printDialog())
		{
			java.awt.print.PageFormat pf = new java.awt.print.PageFormat();
			try
			{
				JTextPanePrintable tpp = new JTextPanePrintable(printTextPane);
				java.awt.print.Paper paper = new java.awt.print.Paper();
				paper.setImageableArea(30, 40, 552, 712);	//8.5 x 11 -> 612w 792h
				pf.setOrientation(java.awt.print.PageFormat.PORTRAIT);
				pf.setPaper(paper);
				pj.setPrintable(tpp, pf);
				pj.print();
			}
			catch(PrinterException ex)
			{
				ex.printStackTrace();
			}
		}
		// FIX to keep the YC frame on top after calling the printDialog.
		// JDK1.4 should have fixed the issue but I(SN) have still seen inconsistencies with focus.
		CtiUtilities.getParentFrame(this).toFront();//keeps the main frame in front focus
	}
	/**
	 * Print the graphics from printTextPane.
	 */
/*	private void print(JTextPanePrintable printTextPane)
	{
		PrinterJob pj = PrinterJob.getPrinterJob();
		
		PageFormat pf = pj.defaultPage();
		pf.setOrientation(PageFormat.PORTRAIT);
//		printTextPane.setPageFormat(pf);		
		Book book = new Book();
		printTextPane.getVectorOfLines();
		book.append(printTextPane, pf);//, printTextPane.getNumberOfPages());

		if( pj.printDialog())
		{
			try{
				pj.setPageable(book);
				pj.print();
			}
			catch(PrinterException ex)
			{
				ex.printStackTrace();
			}
		}		
	}*/
	/**
	 * Forces a reload of databaseCache.
	 * Creation date: (9/10/2001 3:10:42 PM)
	 */
	private void reloadDevices()
	{
		java.awt.Frame frame = null;
		java.awt.Cursor savedCursor = null;
		
		try
		{
			frame = com.cannontech.common.util.CtiUtilities.getParentFrame(getRootPane());
			savedCursor = frame.getCursor();
			frame.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			
			//load all data from the database to the cache for update to catch anything new.		
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();
			//release the stored routes
			restoreCurrentTree();
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			frame.setCursor( savedCursor );
		}
	}
	/**
	 * Reselects the selected item in the tree, should the tree be refreshed from cache.
	 * Not necessarily needed for new devices, but mainly when the current selection is a deleted device.
	 */
	private void restoreCurrentTree()
	{
		com.cannontech.common.gui.util.TreeViewPanel tvp = getTreeViewPanel();
		Object selectedItem = getTreeViewPanel().getSelectedItem();
		
		if (tvp != null)
			tvp.refresh();
			
		if (selectedItem instanceof LiteBase)
        {
            LiteBase lb = (LiteBase)selectedItem;
            tvp.selectLiteBase((LiteBaseTreeModel) tvp.getTree().getModel(), lb);
        }
		else if( selectedItem instanceof String)
		{
			tvp.selectByString( selectedItem.toString());
		}
		else
		{
			tvp.selectObject(null);
			CTILogger.info("WARNING:  nothing reselected in the tree, dbChangeMessageListener is missing an instanceof");
		}
		// ** Add else if... here to include other objects that may be in the treeViewPanel
	}
	/**
	 * Saves the textPane.getText() to a file.
	 */
	private void save(javax.swing.JTextPane textPane)
	{
		//This will need to be updated someday for a new version of swing
		java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
		javax.swing.JFileChooser  fileChooser = new javax.swing.JFileChooser();

		FileFilter filter = new FileFilter("rtf", "Rich Text Format");
		fileChooser.setFileFilter(filter);
		
		while (true) {
		    int dialogActionValue = fileChooser.showSaveDialog(parent);
		    if (dialogActionValue == javax.swing.JFileChooser.CANCEL_OPTION ||
		        dialogActionValue == javax.swing.JFileChooser.ERROR_OPTION){
		        break;
		    }
		    
		    if( dialogActionValue == javax.swing.JFileChooser.APPROVE_OPTION) {
		        
		        /* Checks to see if a restricted character was 
		         * used in the submitted file path 
		         */
		        String filePath = getFilePath(fileChooser);

		        java.io.FileWriter fWriter = null;
		        try	{
               
		            fWriter = new java.io.FileWriter( filePath, true );
		            java.io.PrintWriter pWriter = new java.io.PrintWriter( fWriter );
		            Document document = textPane.getDocument();
				
		            try {
		                pWriter.print( document.getText(0, document.getLength()));
		            } catch (BadLocationException ble) {
		                CTILogger.error( ble.getMessage(), ble );
		            }

		            fWriter.close();
		            break;
	            } catch( FileNotFoundException fnfe){
	                javax.swing.JOptionPane.showMessageDialog( parent, "Invalid File Name", "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
		        } catch( java.io.IOException e ) {				
		            javax.swing.JOptionPane.showMessageDialog( parent, "An error occurred saving to a file", "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
		        } finally {
		            try {
		                if( fWriter != null )
		                    fWriter.close();
		            } catch( java.io.IOException e2 ) {
		                CTILogger.error( e2.getMessage(), e2 );
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
	private void serialNumberAction()
	{
		com.cannontech.common.gui.util.TreeViewPanel t =  getTreeViewPanel();
		String tempSerialNumber = (String) getSerialRoutePanel().getSerialTextField().getText().trim();
	
		if( tempSerialNumber == null || tempSerialNumber.length() == 0 )
		{	
			setSerialNumber(null);
			t.getTree().getSelectionModel().setSelectionPath( null );
			return;
		}
		else if( !((EditableTextModel)getTreeViewPanel().getSelectedTreeModel()).getEntryVector().contains(tempSerialNumber) )
		{	
			((EditableTextModel)getTreeViewPanel().getSelectedTreeModel()).getEntryVector().add(tempSerialNumber);
	
			// Refresh the tree selection.
			((EditableTextModel)t.getSelectedTreeModel()).update();
	
			// Clear serial number text field.
			if( getSerialRoutePanel().getSerialTextField() instanceof javax.swing.JTextField )
				getSerialRoutePanel().getSerialTextField().setText("");
	
		}
	
		setSerialNumber( tempSerialNumber );
		updateTreePathSelection( getSerialNumber() );
	}

	/**
	 * Sets the ycClass.command.
	 * @param newCommand java.lang.String
	 * @throws PaoAuthorizationException - User does not have authorization for command
	 */
	private void setCommand(String newCommand) throws PaoAuthorizationException
	{
		getYC().setCommandString(newCommand);
	}
	
	/**
	 * Sets the ycClass.modelType
	 * Valid types located in - com.cannontech.database.model.TreeModelEnum.DEVICE, DEVICE_METERNUMBER, 
	 * MCTBROADCAST, LMGROUPS, CAPBANKCONTROLLER, COLLECTIONGROUP, TESTCOLLECTIONGROUP, BILLINGGROUP, EDITABLE_xxx
	 * @param typeSelected int
	 */
	private void setModelType(Class<? extends LiteBaseTreeModel> typeSelected)
	{
		getYC().setModelType( typeSelected);
	}
	
	/**
	 * Sets the ycClass.route.
	 * @param newRoute java.lang.Object
	 */
	private void setRouteID(int routeID)
	{
		getYC().setRouteID(routeID);
	}

	/**
	 * Fills the routeComboBox with all of the created routes.
	 * Retrieves the routes from cache memory and then sorts them (alphabetically).
	 * Removes all items in the combo box (applicable when new routes exist)
	 * Add a default of All Routes as the first item in the combo box
	 * Add the database routes to the combo box.
	 */
	private void setRouteModel()
	{
		if( getSerialRoutePanel().getRouteComboBox() == null )
			return; 
	
		int index = 0;	//Index in the routeComboBox to be selected
		
		// Save the routeComboBox's currently selected item.
		Object saveSelectedRoute = getSerialRoutePanel().getRouteComboBox().getSelectedItem();
		
		getSerialRoutePanel().getRouteComboBox().removeAllItems();	
		getSerialRoutePanel().getRouteComboBox().addItem("All Default Routes");	//default first item
	
		getYC().setAllRoutes(null);	//dump the stored routes, will reload from cache on next getAllRoutes call
		LiteYukonPAObject[] routes = (LiteYukonPAObject[])yc.getAllRoutes();
		for( int i = 0; i < routes.length; i++ )
		{
			getSerialRoutePanel().getRouteComboBox().addItem( routes[i]);
			
			if(saveSelectedRoute != null && routes[i].toString() == saveSelectedRoute.toString())
				index = i + 1;
		}
	
		// Set the newly selected item to the index of the previously selected item.
		//  If the prev item doesn't exist, set selected item to the 0 index (default).
		getSerialRoutePanel().getRouteComboBox().setSelectedIndex( index );
			
	}
	/**
	 * Sets the ycClass.serialNumber.
	 * @param newSerialNumber java.lang.String
	 */
	private void setSerialNumber(String newSerialNumber)
	{
		getYC().setSerialNumber(newSerialNumber);
	}

	/**
	 * Sets the ycClass.treeItem.
	 * @param selectedTreeItem java.lang.Object
	 */
//	private void setTreeItem(Object newSelectedItem)
//	{
//		ycClass.setTreeItem(newSelectedItem);
//	}

	/**
	 * Updates visibilty of objects when model changes.
	 */
	private void treeModelChanged()
	{
		LiteBaseTreeModel model = (LiteBaseTreeModel) getTreeViewPanel().getSortByComboBox().getSelectedItem();
		if( model == null )
			return;
			
		setModelType( model.getClass() );
		Object selectedItem = getTreeViewPanel().getSelectedItem();
		
		if (TreeModelEnum.isEditableSerial(getModelType()))
		{
			if(selectedItem != null && 
				getTreeViewPanel().getSelectedNode().getParent() != null)
				getSerialRoutePanel().setSerialNumberText(selectedItem.toString());
			else
				getSerialRoutePanel().setSerialNumberText("");
			enableSerialAndRoute(true);
			getSerialRoutePanel().getSerialTextField().requestFocus();
		}
		else
		{
			enableSerialAndRoute(false);
		}

		updateCommandMenu(selectedItem);
		
		if (selectedItem == null )
		{
			getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
			getCommandPanel().getExecuteCommandComboBoxTextField().setText("");
		}
	}

	/**
	 * Updates the current tree selection to the parameter passed in.
	 * Used for synchronization.
	 */
	private void updateTreePathSelection(Object currentSelection)
	{
		TreeViewPanel t =  getTreeViewPanel();
		TreePath selectedPath = CtiUtilities.getTreePath( t.getTree(), currentSelection);
		t.getTree().getSelectionModel().setSelectionPath( selectedPath );
	}
	
	/**
	 * Gets the appropriate configFile for the object type selected in the treeViewPanel tree.
	 * Parses the keys and Values in the configFile and fills the CommandComboBox accordingly.
	 * Only updates the command boxes when the selected tree item differs in object type from
	 *  the previously selected tree item.
	 */
	public void valueChanged(TreeSelectionEvent event)
	{
	    //TODO - On change of model, reset the recent devices.
        LiteBaseTreeModel model = (LiteBaseTreeModel) getTreeViewPanel().getSelectedTreeModel();
        if( model == null )
            return;
            
        setModelType( model.getClass() );
		Object selectedItem = getTreeViewPanel().getSelectedItem();
		setTreeItem(selectedItem);

		return;
	}
	public void setTreeItem(Object selectedItem)
	{
	    getYC().setTreeItem( selectedItem);
	    
		String savedDevType = getYC().getDeviceType();
		String displayTitle = YC_TITLE;
		setTitle(displayTitle);

		if (selectedItem == null) {
		    if( TreeModelEnum.isEditableSerial(getModelType())) {
		        getYC().setDeviceType(savedDevType);
		    } else {
		        getYC().setDeviceType(null);
		    }
			return;
		}
	
		if (getTreeViewPanel().getSelectedNode() != null)
		{
		    if( getTreeViewPanel().getSelectedNode().getParent() == null)
				return;
		}
		
		updateCommandMenu(selectedItem);
		if ( selectedItem instanceof LiteBase)
		{
			DBPersistent dbp = LiteFactory.createDBPersistent( (LiteBase) selectedItem);
			if (dbp == null)
				return;
	
			getYC().setDeviceType(dbp);
			if( selectedItem instanceof LiteYukonPAObject)
			{
				LiteYukonPAObject lpao = (LiteYukonPAObject)selectedItem;
				setTitle(displayTitle + " - " + lpao.getPaoName() + " (" + lpao.getPaoType().getDbString() + ")");
			}
			else
			    setTitle(displayTitle + " - " + getYC().getDeviceType());
		}
		else if ( TreeModelEnum.isEditableSerial(getModelType()))
		{
			setSerialNumber( (String)selectedItem);
			getSerialRoutePanel().setSerialNumberText( getSerialNumber().toString() );
            
			if( getModelType() == EditableExpresscomModel.class)
				getYC().setDeviceType(CommandCategory.STRING_CMD_EXPRESSCOM_SERIAL);
			else if( getModelType() == EditableVersacomModel.class)
				getYC().setDeviceType(CommandCategory.STRING_CMD_VERSACOM_SERIAL);
			else if( getModelType() == EditableSA205Model.class)
				getYC().setDeviceType(CommandCategory.STRING_CMD_SA205_SERIAL);
			else if( getModelType() == EditableSA305Model.class)
				getYC().setDeviceType(CommandCategory.STRING_CMD_SA305_SERIAL);
			else
				getYC().setDeviceType(CommandCategory.STRING_CMD_SERIALNUMBER);
			
			if( getYC().getLiteDeviceTypeCommandsVector().isEmpty())
			{
				getCommandLogPanel().addLogElement(" *** No commands were found for the device type: " + getYC().getDeviceType() + "  -  Trying a backup - " + CommandCategory.STRING_CMD_VERSACOM_SERIAL + " ***");
				//This is only temporary until all files have been changed from ALT_SERIALNUMBER_FILENAME to SERIALNUMBER_FILENAME.
				getYC().setDeviceType(CommandCategory.STRING_CMD_VERSACOM_SERIAL);
			}

			setTitle(displayTitle + " - " + getYC().getDeviceType() + " # " + getSerialNumber().toString());
		}
		else if( getModelType() == DeviceGroupTreeFactory.LiteBaseModel.class)
		{
            DeviceGroup deviceGroup = (DeviceGroup) selectedItem;
			getYC().setDeviceType(CommandCategory.STRING_CMD_DEVICE_GROUP);
			setTitle(displayTitle + " : " + deviceGroup.getFullName());
		}
		else
		{
			CTILogger.error("No DeviceType found, using empty String");
			getYC().setDeviceType("");
		}
		
		if( getYC().getLiteDeviceTypeCommandsVector().isEmpty())
		{
			getCommandLogPanel().addLogElement(" *** No commands were found for the device type: " + getYC().getDeviceType()+ " ***");
			getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
			getCommandPanel().getExecuteCommandComboBox().setSelectedItem(""); //clear text field
			return;
		}
		
		// Only update command boxes on a change in device type/ file name.
		if( !getYC().getDeviceType().equalsIgnoreCase( savedDevType) )
		{
			updateCommandSelection();
		}
	}

	public void updateCommandMenu(Object selectedItem){
		getYCCommandMenu().locateRoute.setEnabled(false);	//init to false, will change below if valid state.
		getYCCommandMenu().installAddressing.setEnabled(false);	//init to false, will change below if valid state.
		getYCCommandMenu().downloadSchedule.setEnabled(false);	//init to false, will change below if valid state.

		if( TreeModelEnum.isEditableSerial(getModelType())) {
			getYCCommandMenu().installAddressing.setEnabled(true);
		}
		else  {
			LiteYukonPAObject lpao = null;
			if( selectedItem instanceof LiteYukonPAObject) {
				lpao = (LiteYukonPAObject) selectedItem;
			}
			else if( selectedItem instanceof LiteDeviceMeterNumber){
				lpao = DaoFactory.getPaoDao().getLiteYukonPAO(((LiteDeviceMeterNumber)selectedItem).getDeviceID());
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
	
	public void updateCommandSelection()
	{
		// Clear out the old, get ready for the new!
		getCommandPanel().getAvailableCommandsComboBox().removeAllItems();
		getCommandPanel().getExecuteCommandComboBox().setSelectedItem("");
				
		// Add the keys to the availableCommandsComboBox, first one is default ("Select A Command")
		getCommandPanel().getAvailableCommandsComboBox().addItem(" <Select A Command>" );
		for (int i = 0; i < getYC().getLiteDeviceTypeCommandsVector().size(); i++)
		{
			LiteDeviceTypeCommand ldtc = (LiteDeviceTypeCommand)getYC().getLiteDeviceTypeCommandsVector().get(i);
			LiteCommand  lc = DaoFactory.getCommandDao().getCommand(ldtc.getCommandID());
			if( ldtc.isVisible() && yc.isAllowCommand(lc.getCommand()))
				getCommandPanel().getAvailableCommandsComboBox().addItem( lc.getLabel());
		}
	}
	
	public void mouseExited (java.awt.event.MouseEvent event)
	{
	}
	public void mouseClicked(java.awt.event.MouseEvent event)
	{
	}
	public void mouseEntered (java.awt.event.MouseEvent event)
	{
	}
	public void mousePressed(java.awt.event.MouseEvent event)
	{
	}
	public void mouseReleased (java.awt.event.MouseEvent event)
	{
		if(event.isPopupTrigger())
		{
			String clearCommand = "";
			String printCommand = "";
			if( event.getComponent() == getDebugOutputTextPane())
			{
				clearCommand = CLEAR_OUTPUT_DEBUG;
				printCommand = PRINT_OUTPUT_DEBUG;
			}
			else if( event.getComponent() == getDisplayOutputTextPane())
			{
				clearCommand = CLEAR_OUTPUT_DISPLAY;
				printCommand = PRINT_OUTPUT_DISPLAY;
			}
			javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu("Popup Menu Test");
			javax.swing.JMenuItem clear = new javax.swing.JMenuItem("Clear Output");
			clear.addActionListener(this);
			clear.setActionCommand(clearCommand);
			
			javax.swing.JMenuItem print = new javax.swing.JMenuItem("Print Output");
			print.addActionListener(this);
			print.setActionCommand(printCommand);
			
			popup.add(clear);
			popup.add(print);
		
			popup.show(event.getComponent(), event.getX(), event.getY());
			System.out.println("show popup");
		}
/*		else if (event.getSource() == getTreeViewPanel().getTree())
		{
			if( event.isPopupTrigger())
			{
				int numItems = getCommandPanel().getAvailableCommandsComboBox().getItemCount();
				for (int i = 1; i < numItems; i++)
				{
				javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu("Popup Menu Test");
				popup.add(getCommandPanel().getAvailableCommandsComboBox());
				
//				javax.swing.JMenuItem clear = new javax.swing.JMenuItem("Clear Output");
//				clear.addActionListener(this);
//				clear.setActionCommand(CLEAR_OUTPUT);
//				
//				javax.swing.JMenuItem print = new javax.swing.JMenuItem("Print Output");
//				print.addActionListener(this);
//				print.setActionCommand(PRINT_OUTPUT);
//				
//				popup.add(clear);
//				popup.add(print);
				
				javax.swing.tree.TreePath path = getTreeViewPanel().getTree().getPathForLocation(event.getX(), event.getY());
				if (path == null)//Path not found because
				   return;//right click does not occur on a node or a leaf
				getTreeViewPanel().getTree().setSelectionPath(path);
				
				System.out.println("show popup");
			}
		}
		*/
	}
	/* (non-Javadoc)
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg)
	{
		if( o instanceof YC && arg instanceof YC.OutputMessage)
		{
			YC.OutputMessage outMessage = (YC.OutputMessage)arg;
			if( outMessage.getDisplayAreaType() == YC.OutputMessage.DEBUG_MESSAGE)
			{
				javax.swing.SwingUtilities.invokeLater( new WriteOutput(getDebugOutputTextPane(), outMessage) );
				/*TODO: HACK TO ELIMINATE TOO MUCH MUMBLE JUMBLE IN DISPLAY (TOP) PANEL 
				 * Parsing for " sent " helps eliminate the communication responses somewhat*/
				if( outMessage.getMessageType() == MessageType.SUCCESS && 
						outMessage.getText().indexOf(" sent ")< 0)	//send message to display also?
					javax.swing.SwingUtilities.invokeLater( new WriteOutput(getDisplayOutputTextPane(), outMessage) );
			}
			else if( outMessage.getDisplayAreaType() == YC.OutputMessage.DISPLAY_MESSAGE)
				javax.swing.SwingUtilities.invokeLater( new WriteOutput(getDisplayOutputTextPane(), outMessage) );
		}
		else if( arg instanceof String)
		{
			getCommandLogPanel().addLogElement(arg.toString());
		}
	}
	
	/**
	 * @return
	 */
	public YC getYC()
	{
		if (yc == null){
			yc = new YC(true);	//load defaults from file
            yc.setLiteUser(ClientSession.getInstance().getUser());
        }
		return yc;
	}

	/**
	 * @param yc
	 */
	public void setYC(YC yc_)
	{
		yc = yc_;
	}

	/**
	 * @return
	 */
	public TreeModelEnum[] getTreeModels()
	{
		if( treeModels == null)
		{
			//Vector of ints (TreeModelEnum types), (Changed from array to remove size constraints)
			List<TreeModelEnum> tempModel = new ArrayList<TreeModelEnum>();
			tempModel.add(	TreeModelEnum.DEVICE);
			tempModel.add( TreeModelEnum.DEVICE_METERNUMBER);
			tempModel.add( TreeModelEnum.MCTBROADCAST);
			tempModel.add( TreeModelEnum.TRANSMITTER);
			tempModel.add( TreeModelEnum.LMGROUPS);
			tempModel.add( TreeModelEnum.CAPBANKCONTROLLER);

			boolean needDefault = true;
			ClientSession session = ClientSession.getInstance();

			DaoFactory.getAuthDao().getRolePropertyValue(session.getUser(), CommanderRole.COMMAND_MSG_PRIORITY);
						
			
			if( Boolean.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(session.getUser(), CommanderRole.DCU_SA305_SERIAL_MODEL)).booleanValue())
			{
				tempModel.add( TreeModelEnum.EDITABLE_SA305_SERIAL);
				needDefault = false;
			}
			if( Boolean.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(session.getUser(), CommanderRole.DCU_SA205_SERIAL_MODEL)).booleanValue())
			{
				tempModel.add( TreeModelEnum.EDITABLE_SA205_SERIAL);
				needDefault = false;
			}
			if( Boolean.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(session.getUser(), CommanderRole.EXPRESSCOM_SERIAL_MODEL)).booleanValue())
			{
				tempModel.add( TreeModelEnum.EDITABLE_EXPRESSCOM_SERIAL);
				needDefault = false;
			}
			if( Boolean.valueOf(DaoFactory.getAuthDao().getRolePropertyValue(session.getUser(), CommanderRole.VERSACOM_SERIAL_MODEL)).booleanValue())
			{
				tempModel.add( TreeModelEnum.EDITABLE_VERSACOM_SERIAL);
				needDefault = false;
			}
			if( needDefault )
				tempModel.add( TreeModelEnum.EDITABLE_LCR_SERIAL);
			
			treeModels = new TreeModelEnum[tempModel.size()];
			treeModels = tempModel.toArray(treeModels);
		}
		return treeModels;
	}
	
	private String getFilePath(JFileChooser fileChooser){
	    String filePath = fileChooser.getSelectedFile().getPath();
	    String fileName = fileChooser.getSelectedFile().getName();
	    
	    // Special Case where the user puts a file name with two double "s
	    // EX.  "file.txt" should be file.txt not "file.txt".rtf 
	    if(fileName.length() > 2 &&
	       fileName.startsWith("\"") &&
	       fileName.endsWith("\"")){
	        filePath = filePath.replace(fileName, fileName.substring(1, fileName.length()-1));
	    } else {
	        if(!filePath.endsWith(".rtf") && 
	           fileChooser.getFileFilter().getDescription().contains("Rich Text Format")){
	            filePath += ".rtf";
	        }
	    }
	    
	    return filePath;
	}
}
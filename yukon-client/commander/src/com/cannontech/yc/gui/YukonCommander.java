package com.cannontech.yc.gui;

/**
 * This type was created in VisualAge.
 */
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterException;
import java.net.URL;
import java.util.Observable;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.TreePath;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.JTextPanePrintable;
import com.cannontech.common.gui.util.SplashWindow;
import com.cannontech.common.gui.util.TreeViewPanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.cache.functions.CommandFuncs;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.command.CommandCategory;
import com.cannontech.database.model.EditableTextModel;
import com.cannontech.database.model.ModelFactory;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.application.CommanderRole;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.yc.gui.menu.YCCommandMenu;
import com.cannontech.yc.gui.menu.YCFileMenu;
import com.cannontech.yc.gui.menu.YCHelpMenu;
import com.cannontech.yc.gui.menu.YCViewMenu;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;

public class YukonCommander extends javax.swing.JFrame implements com.cannontech.database.cache.DBChangeListener, java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.KeyListener, javax.swing.event.TreeSelectionListener, java.awt.event.MouseListener, java.util.Observer {
	private YC yc;

	private int [] treeModels = null;
	private static final String YC_TITLE = "Commander";
	public static final String HELP_FILE = CtiUtilities.getHelpDirPath() + "Yukon Commander Help.chm";
    public static final URL COMMANDER_GIF = YukonCommander.class.getResource("/CommanderIcon.gif");

	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
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

	private String popupCommand = null;
	private final String CLEAR_OUTPUT_DISPLAY = "Clear Display";
	private final String PRINT_OUTPUT_DISPLAY = "Print Display";

	private final String CLEAR_OUTPUT_DEBUG = "Clear Debug";
	private final String PRINT_OUTPUT_DEBUG = "Print Debug";
	
	private final String VALID_TEXT = "Valid Text";
	private final String INVALID_TEXT = "Invalid Text";
	private final String DISPLAY_TEXT = "Display Text";
	
	
	class WriteOutput implements java.lang.Runnable
	{
		private YC.OutputMessage message = null; 
		private javax.swing.JTextPane textPane = null;
		public WriteOutput(javax.swing.JTextPane textPane_, YC.OutputMessage message_)
		{
			super();
			this.textPane = textPane_;
			this.message = message_;
		}
		private java.awt.Color getColor()
		{
			if( message.getStatus() > 0 )
				return java.awt.Color.red;
			else if (message.getStatus() == 0)
				return java.awt.Color.blue;
			else
				return java.awt.Color.black;
		}		
		public void run()
		{
			try
			{
				javax.swing.text.Document doc = textPane.getDocument();
				javax.swing.text.SimpleAttributeSet attset = new javax.swing.text.SimpleAttributeSet();
				attset.addAttribute(javax.swing.text.StyleConstants.Foreground, getColor());
				attset.addAttribute(javax.swing.text.StyleConstants.Underline, new Boolean(message.isUnderline()));
//				attset.addAttribute(javax.swing.text.StyleConstants.FontFamily, "rastor fonts");
				doc.insertString(doc.getLength(), message.getText(), attset);
				textPane.setCaretPosition(doc.getLength());
			}
			catch (javax.swing.text.BadLocationException ble)
			{
				ble.printStackTrace();
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
		javax.swing.JFrame popupFrame = new javax.swing.JFrame();
		popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(COMMANDER_GIF));
		javax.swing.JOptionPane.showMessageDialog(popupFrame,
		"This is version " + com.cannontech.common.version.VersionTools.getYUKON_VERSION() + "\nCopyright (C) 1999-2003 Cannon Technologies.",
		"About Yukon Commander",javax.swing.JOptionPane.INFORMATION_MESSAGE);
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
			{
				setRouteID(((LiteYukonPAObject)selected).getYukonID());
			}
			CTILogger.info(selected);
		}
	
		else if( event.getSource() == getCommandPanel().getExecuteButton() ||
				event.getSource() == getYCCommandMenu().executeMenuItem )
		{
		    getYCFileMenu().updateRecentList(getYC().getTreeItem());
			String commandString = CommandFuncs.loadPromptValue((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim(), this);
			if( commandString != null)	//null is a cancel from prompt
			{
				setCommand(commandString);
				if( isValidSetup() )
				{
					getCommandPanel().enter(getCommand());
					getYC().executeCommand();
				}
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
				String commandString = getYC().getCommandFromLabel(getCommandPanel().getAvailableCommandsComboBox().getSelectedItem().toString() );
				getCommandPanel().getExecuteCommandComboBoxTextField().setText( commandString );
				getCommandPanel().getExecuteButton().requestFocusInWindow();
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
			javax.swing.ImageIcon icon = new javax.swing.ImageIcon(COMMANDER_GIF);
			Object[] selections = null;			
			// Get an instance of the cache.
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	
			synchronized(cache)
			{
				java.util.List allLM = cache.getAllLoadManagement();
				java.util.Collections.sort( allLM, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
	
				Vector lmGroups = new Vector(allLM.size());
				for (int i = 0; i < allLM.size(); i++)
				{
					if( allLM.get(i) instanceof LiteYukonPAObject)
					{
						if((( LiteYukonPAObject)allLM.get(i)).getType() == DeviceTypes.LM_GROUP_EXPRESSCOMM ||
						   (( LiteYukonPAObject)allLM.get(i)).getType() == DeviceTypes.LM_GROUP_VERSACOM	)
							lmGroups.add(allLM.get(i));
					}
				}
				selections = lmGroups.toArray();
			}
			
			Object value = javax.swing.JOptionPane.showInputDialog(this, "Load Group Template", "Select the Addressing Template to Install", javax.swing.JOptionPane.QUESTION_MESSAGE, icon, selections, null);
			if( value != null )//OK selected	
			{
				setCommand("putconfig template \'"+ value.toString()+"\'");
				if( isValidSetup() )
				{
					getCommandPanel().enter(getCommand());
					getYC().executeCommand();
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
		else if ( event.getSource() == getLocateRouteDialog().getLocateButton())
		{
			setCommand("loop locateroute");
			if( isValidSetup() )
			{
				getCommandPanel().enter(getCommand());
				getYC().executeCommand();
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
	    boolean found = false;
	    
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
		popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(COMMANDER_GIF));
		return javax.swing.JOptionPane.showConfirmDialog(popupFrame, message, YC_TITLE, javax.swing.JOptionPane.YES_NO_OPTION, messageType);
	}

	/**
	 * Delete the selectedNode from getTreeViewPanel().
	 * Removes the selectedNode (serial number) from getSerialNumberVector().
	 */
	private void deleteSerialNumber()
	{
		if(ModelFactory.isEditableSerial(getModelType()))
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
	 * Exit application.  Disconnect getConnToDispatch().
	 */
	private void exit()
	{
		getYC().getYCDefaults().setOutputDividerLoc(getOutputSplitPane().getDividerLocation());
		getYC().getYCDefaults().writeDefaultsFile();
		try
		{
			if ( getClientConnection() != null && getClientConnection().isValid() )  // free up Dispatchs resources
			{
				com.cannontech.message.dispatch.message.Command command = new com.cannontech.message.dispatch.message.Command();
				command.setPriority(15);
				command.setOperation( com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );
	
				getClientConnection().write( command );
				getClientConnection().disconnect();
			}
		}
		catch ( java.io.IOException e )
		{
			e.printStackTrace();
		}
	
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
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88GE3E017AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8BD8DC4555D4E31B7E1F7D1B3634BF5A54BFD65B523F34A5A6D1EAD05120260AF1515850949595ADD1CCD0512252FAF9A4A19B9E8BA1BC92B4A41204980A24C943BB6119AC06908CC8082111E57786B6D9F657E5A1C4B1794F4C5CB3FB395CDDA0AD72FDA7F3674C63BC664C4CB9B377EE046CED7356A6A4E68BC242DAC17AABCF9004A02BA0FC7B095FFC97F1E13F55E885595FF600CCE15FA74910
		C6C1D9503B21DA1DB0D21E8279F84847EC8D55664273A6614B72288B70600E8E1479FE2B6E964FF44A8EA21D75B46F77D5AA246924023FC6DB84E087508941DF65C9F8BD645325E78289090210B71F54F9F1C71E8C3FD02A978D29G6501344E53DF95C8653D10A7F5C6A13DFEBE2BFBAB249BA8F757D5D5C875FA3EB7CF08FDA8A7B10D5012750F10FC9BEBDBAC250542CDD491C9C2C75B87105634DF68BFE4348D169A8DD62B4D6A940D8325170C257D43662A2A534397EC56C65B602041EA929D0260F39B61D1
		77FE8AED8FBCC23EE881414DFE1F68A59C7231G298B104F50349A098F6F8B730408DBB63865F3E081636F7A85AEBECB762FCE2CD9C87ACF391CAA358B0172E440B1DE5AAFA3AFDE8B11977BF6D2B7G729A00A80EAF5FC17042A20DB68F20EC9172D83B9BF96C7E7237C2DCDFE5E2069B9E750B98EF76C5AE9EE35E3EA6B11F5AC6E0B999DFBFA853GC482245C2E519683688136D21B3CF810683C3E4FC934EA33565B6D8D961363384DA4DA60392A8ACAA45CC951E8F3188401595D6677CBD478C1683DFD3C4D
		91E0321D9250CE7CCE7EDCF0BABFCCDC2BA25B42A772921DD29F33EFF74996FCBB51DBBC9DDBDFF8CE107AE5788DC5841FF2078B4F466A3FE9FD12AF023259F7201E3B53111756649B855D3E6A4475AA3C785CD71698FD8763A1B940458B53710FAA090E4BG5F8AE0829883188B909800BA3E77607E49F45C82F66E309AAC9D43FDF6034359B0EC369083E73ACE3F6CC957EA405A95CC3B9D530B6FFE799805AF28D255CB386F3644B0C99FB1B23DB05A1F38252D7B5DC45BC867E398C967CA516F5898A6F17CEA
		0ABFA04333319D21F812EF02B2DFGD67FD7F4EE255E496AA770FE96D2FC1E8C4F7A295D017339884AE6833071BDD0CD563FE84827AC56E88B81AAG3A811CG339763982716FFB845F954AE9BDF56767E729CC845D1274350E29DF29A2CC6314B60B09B7AAC6210A055D9D6CE745AB72E5D63F4FE9B0146D151AABA8CCE334D8A3D9B86C5B026A1D6A73CBC1354F108343453BCA88201C13B007DC616EF0254E298F2FEE2B7991C222B86AB0FAEA7B6B1E268029E2183F88EDBCC74D5C257DCDFF88EDE0C3AB254
		90DCC120C6FBA0907552DF118569B1B0EE51D2D555E4B692B68C0ECBE82309C5BAC875D03F97E09440BB9067676AEE344326341F89A5CFD42A5AA13553C0E6D34101CABB4C207DC783DEF797708550FD976E1D5E6FF72B74659DB851BEC7D5F0FA852ED711E7B4FE2DCAE3031BB92B44A5A972EB95F92677C7EB096D0E01DCBEG81F7216EFFDAD148EC3241B0A422951EF0008109A657C8303A7FDBC97698F1C434BA1BC43B885B2B55B9A459404FABB7386C731C0D5B0E9F4513752A918CE80863EF2B4CE4F868
		C3EE6DD8FEA7ADB7B81D8EF35F30D36C901DCE33355F557EBE4A4B503825F0C812B5749011D583B2C68144F1D90FD5ED1259D92B4DF6E1585EC9E610E42F963AAF275E8DFB6F5DB87EAFEEA73E141E67E3E95ED5BE36DDED6C5C8169E77AEB748F27C5C3E98FD323B1FF07401F6F14BDB76E188E6D69822351B323B458589F2AF6EF3B4D556A36FB2CDA5DF6BF2E76EC1B272B55EC131575D61379E93BE815593AD8CD56BF234DEA95690463CD8466732E6EA4F2182DE667A93329DFF4F6183F9041673C1B58ECA3
		DDE3CD70BCFAB72E09975F47B531680B1B05204F32D57DA71F1F6DCF54C87698F937EBCDE4BC1D2BA1F3C83408032028C65B30554970963AFECB7821A40A6BEBA0C1486946A3E60BF8DC348EB39B5CA4EC7D04613B4C62C5B97E4E3CB40A171CF4D6A4151DAEE6ED7422454E8A581CCE2EA37CA45F83FEB0C059BDB82723EAC8B94EE9E961EA34D9ACCC158246B10DC572B8C6FF8F0B033CA075A6FD52BA17F729557103B40857D9D42CD6A78C52E8DEA77A8335BA49C1D09E04FCBFD1CB761CA17173E19136F232
		889D93AF61FEDC1B25A86BB2D806DD7BFCE46DC6C8AD32750B61D79CD29F33A78F11B56B826F7F19C36B8432DF33BE190D799BC81FF607F99016FB32A0CBFA3B7632DC16B2601F440A1D108EG30B11BFD1E14A963AE2FA0527ED39EF7915F0761BE27386F0031362CC3FF7238CE9E9B1A06CAD4ED16C0F090335530A0251FDDD9CF7A0D83FC4A3D2867B30765EBFE7D50104DE826EE0E24B3ED1D5C1E99AE230E5860B933E388FCEE13B80AFA5FDCC7F8A5FE100416F4F7DABBAEA6B5778D168E0F8C169A7A1C25
		22496C2C2A7A08D8F217F9E858E0212A15E252AF0BD224BE8A6E45F86F611DE48CBBA1EF8118F5AF460C376F423AF3F95D44E2D2F7B9642381126F433AAF9773FE6F433A159F50FEA1EF8118456B966D657D723A76925AAF6423811217E834CC4EB73764211C678DA3065152210B306E01F8C708BF497A0B3B4CE9AF417EFCF414B664ADG331660BAF56A9F72B18F0AA8F4BB66F317303176DF229C73AB8D04DE8860A31660184F294B140DB9442460AE7743AC61FBC1FD921B8FD23E21FEAD4E878177A42FA235
		58A4D6221F92BCF19FE7314361DDC45602E0E5CC21277862E0E5CC913A93E30A83D066930CFE4562DDEA7463A53A3A7B5D519F21B6D1F43F127E950AAF3BDFC9BF25986957C0593C7B117E2B45EA74139FE0F4739EF0C77F57544ECA9ED0523F1D62AB9ED052BF7C8152EF02B25F87107E1E3DEA74D39F14467FC1F77457D05BBD702012FE9C4557BD2824BFD20274DB214C7FC1247FD70FF1F1765271740B17E2CC191AC97A2BD92A0CCDF3A83EF529B2B63DC90731E98F1485AD45B8E9381158F5516FA19E79BD
		5A750A7AB419DD375B4CD6A73769D98DE4CD37B94C7DE6AB623ECDF1C61B03BA9B4C477D9F3A66B4C11F1DG8C97CFF13D109F7DBD4A5F3CCB60F321CD763CE2674C7B03B1B3CA03497C58AE5443DC2E077D3BD18F3EDC8FE345AE592F13BD27974F3C9EF64FA88DA67306BD2807902E0773FBD08FE1DC8F8BBEF0499EA8FBEE7EE06675F0F6C6E9B0196B76229E62399EEE5E0BFAC8F9887570FA09CB763F491E47CAE6DE8FBF1CD19ACC666CABA8F349C328079F8884578EF99357C3D6A671A9E773FC814D7B72
		7C2D3A49F8A5ED7FBDFD5CB5A90D694642F2E0EB68970DE43D9E65794BB4FFC940F865B7C5C4EEA7840FA18F113B18839F911BE07E527C0B4427BE874E9138C99DF651A8189EA67A5C3493FDEA5D43CC87C5D25A84A96B6754B1528F2B7F51EE2C6FA7558B125288DE7F3AD6D77DEF6C417AC2882B17AD2587C2307EDD47DD758376E27DCE295E00144E06145559FB856B7882AE90A09CA09E2060910C837E0F5A89F5F69B8C8E6FC7C83BFB85ECD783794EC7D83FE30F30B396562E648C7157189759C8EEF34811
		101578E2AC86B91EA90FC142253642B20DB6FB997A120F1E117B123A9BF509384FAB4735E4996321E2194B17E43EEB654E94290D854AEEDDC6F07DBB33E5ED891FF7406016C24A4AEF98D7CE46FD0F404A99DF8F6F44FB1A7807FD854B22E237F7C95192BF3AC7D95A1B3BD548AE6CCAE63C59F7A3EF7AC711371BF6FB66CD33DB495BE209B75667AF76E01FDE3C4FA776F86EF37D874ABE8D8972BE1F5A0BFD8670BED3CBBC77F928C459E7E3223C4F0F2FE01FB13C4FABD764ED089DE50C6BF37E95A54D1D023C
		7C39F165E42CFE30CEDEBEA4A8F97A2E541E79ED2B84749B0BB59AED2F86E324EBAA37882AB112B4ABE02E5318E8D782E96F836D7C8116EA500EC7F7213D787D7CC621552BCA55DEA2BA37A7C6E8247B26D0179D073FFDCDA2636335BDEEE2B5B233ACCC2F57E5E22CD6970A315A49CD042FD1484F81708DC5FF346E84610E2D0189EDC417DD067352EDC5B38439B6879E9EFF8464CE741D07282B5350AF60DAFDE867555DB35D3B9E7531704E1F8A815F0CCA5868E67E84C9FA081052384DDBA91E71FFC3BB5107
		6EB15857G5A8186G46G3C9FC37F7AE89B6191C4A08255BB1D8663GBD95C04948B9168BCD96F4A65B706E2B3BFB735BF3F56D1ED30D0DA6EF57F5656A5A2D912E2E5DDF275D0E45CAB75239617B18726E7697949F7C18726E36A4816FEEA3202C69F13459D7BFA6E36E8B79E000F000D8006C659AED59F2FED650D6A6CCF2A757E933370AA322C5F6CD41647B2FCC25FCCD4BDDBECD77F2F7B16A83F40ED916B3B956F2FCA845FB4970CC3EEE0AA7F9EFA80BDB0E72D5F610B52A6789085BG6682788384BF01
		325DF3E28713CD45161BCD7DE42FA5B709CE1C57BFEDF7FFC67E4B76496F9FEFEBA767E6FDB62753B6C87AC7FCC0BB511753E627C8063B2B1D0C97042B83CED6D542AFE9A7EB12C5BCC751CC4F739248D9E984489587207B834C61BF201F79EBDA56847981006B817CF959BF92547CC352D60953296088BDF9BECE11E7B40E295278CF5D5DB11D3C7D317BF1C9BC29FEF711F2D27D6EA27D24673B0B1C136E6FAE5EBDA93F3B10E2A43A07AC877DC57D81630542CD98C3242E4031882138B2486BB9EE854519A0BF
		1B634EEED26A1334194E9A2277D01F5138B80146F4638465B8CD9C337A8E77E3266F18FCAEFD5CC14F25E18EF4DE320B8C5755C14FF84E1D9B92791CBD532163381621F65B10199CEEFF9D77B24CB70F5CA0774D7DD63098B192523895F89F5237D1DEC797466A68A16D8943BA5FCF1357F1CAF57CA18D66F5D2B3464581D21D6CBFEA3445FF44D8A2A0136848E238E49B869D319864D19EBF54C13DEEGAFGEFG922F7A7D113EE735C95E375ABC5DA4643511B955E13718B1BEE1780ADAB2561256DAED7D5445
		127433A1DB5EEF2CC433E0A56CC1ABD12E778A475D972DE4F53CA075DE09FE6548613D0227775ECAE1F59F2E2A128DB673718A34F87F623792FD3C5FEA49FE928979F8006CBFA10D5EF727C8039B0E74BEDBAA52A8799352F8BA1550B0C1FE8C401B53B83427F4EAB44699A12373D09A170553B91DCEE501FCBCC0F6B852E95BBAC5D938C1B39ACD99DC16F02471F886158572E3G5EGECDF7ECBA646GFE000B6434DFA85DAA48FC6FD243C5E7D5D5030D5CCD36B243E4348EE7E1DCD370A4529A4C22F7A51037
		G4CFA9263027397508FAE78550F292F6FAE0E1C7B24F4C67024ABAE40B5ABC978F1E901209A97F03316E2434D989B88CFA1EF5DB9748C9F726DG83CF213C5726F856F5D515F2EEB09ADD057CBDC8CE632EC27A9EA464B5GD1CF613E30DD2B66CB8ACF3BDF6B19CF757EC652DF76534A7B00920AAFFCDAF99F30C80BE3DA82E5F31EC67A1F242A5E87CDCA3FA30D3EEF9821245FC3715991CA7A4F27A27DC2A81B9501749FCDD3239F95B1997DC7687C081FC07F8F941F720C127EE769C8BF9B4AC61FC17AAD99
		EA74431F190C7ED1FA3F91730C12FEBB4547CF207F478C241F72AC4C01E779FD4C84DFFBB2E06DB6E6DDDD3B26E937E3F2BDC3672F66D9253CAFD2FC7833CAF90732D05EA8A8EBFD8EFD7B951DE4CF7007FC88C08CC0CAA46C9BG87A2513F0F69A06F0C2A466100BCE9335179CCCE548CE67059283B6FB010DBA3A97E5CCCE454C72A0FE10F8A1E4992C25781FB24B2FED9C17133A215714B599C0CDFFC20ACBE924774CC214777031FF7C7FF9FFD1F53793CD247D5946F753CD247CB8AD14757C3D94C736827D6
		503D31738570A3DE60770E64F8C1524FBFE44F0B35B3FF3E3FFCC6E9C86F3825224CF6AE7369B4171CFD326717D2E7DE66643419177905B414F90C4B7CF83ACB4EF0593318BE73B2DF1ED19ACC665E8C14F9B61779384EA5E7176C79690C1917B96EEB207167AC17CCB13267CBD9B3CF7B7BB3CA032D77911D6EE34ABFF7CE9ED346F42A1F4F3C51B971FC666DCE75731904CF6467B34C5616D0DF519B6C4C6F85DCDB53F2708CA0085B5FE394978D7914BFA36EC90AAB023C1E63CCB9D351A569C309A3FDCFED3C
		3E1A928D35B8FFAAE785CC870EC25401054BD63E5DE517F532675F974E3C0DBEBB23B4D8ACFCC2BB7EBE742BAEA2A3FB1EF74A753C245B751C7329405B2F5CCCB6DD13F9448C5192FD5155774FC4172FD29BA3E5BE109722683BEBB9059E7809166C03751B3511BCB70F0A46E176898489C3243243B9242C4DB0E26EB70083A5153158BCA3D7F977C84E2B74C03FA78A6377678E2955F1C2796CA864755B051EF81D97A567F5C38E71D57EA9609D483C0B723B4D8A6DAE376F49AD147AD9AA25A17A2DD21F75B964
		DC24D1A45F90E13F97C5F9CCD930ADB2A153CD3F42AA56DF492A71B1A50B630E66E22C583B0A3FC318CF6F37A06F8F30F4953EEF3875B0560D60F5EBF36977B6AFEA34E5G7A9771AC67670524DE87844C83825E631CB8C367945499FB113F6379A909BF5D79416D8E1BDDF4B8AFB1ACFD13D66A6B1A9EB2A6F3211FG00F000D8001417B45AC20083G2DAFA10D1BBFAD76C0038577AE871B4D3756DC25FFBB7012EBFD9AFB491DFF9BC9F5B767A525FF3D0662EFFDC969DF0F6423FF9DGE585513856949ED652
		770AF651740EF6C7FFBE9D8F5FE8A57DC00A8F0CD652EF4CC57A9AA8BB70B26A2C738C69131FB5B35563396599F2F7C15EE9B79BAC66AF68AB399DD0515A4F4A3F5363FE2F736E19FC2F7BC90F7ADEF7CB4F443D4E37C7FD2F7BCD0FFC2FE33885D23FE43C5B8C033C5F5F759047191DBB9EB7D88D7D32B3F2A31D9FBE20973F17F1FFFCB68F77864D4B7CDB13FC7ABDB06433D773770DA82E84726D9CB716BF59BACE7A186A3ACDE8CD5CABFEB8A90D691C19AB77D12603F23A96B8017E6C552897FF6EB778F1F9
		6F207A8B03FCAC475DC7F13AB5E0EFEB7899C52E12675855533BBBF087E11CEE5F849A1E6E822683C9822E7FDF64CEBC437E9E5F073AC1DEBB008F009FG711D225710BAEF6695CAF50257CC14BBEC0DFC0F914E12B9AB1D4B519D0F61F5B5E40FBDB16C348FBB6567583B3748773883B10C6EG24E3B1388FDE54CFF8CF876AF8C339EF8C2E69ED07A74AA69E46F6C1B16CFCBE8C5268986693B2599E64FBFD72AB93E54BFBC5AE5B4AB6925CF40830A4D8CE829DD1A61F78A919271D8ED1A4075852D6AF4998BB
		EE2FAFFE05516F0554798A4AF8A4DFDE67FA29CE8824912F6039706FF2713CA18EF0E52F62B9693E5EF1E72F6CDC782843ECEAB0743303E136C7A664613970402B38C7FE18C77A1C8779GG4D2B381FEE4F477DB46AD5547169FC12276B1900FB7DF91DFC2F8F3F74165B3D3E70B5264B5657544E0F1DE70B05A91D754A5EB379EC8B1E734EFA8D6589BECA779248AF8708FE8D65792D1E54A5F3ADB9966B465753F72AA15F89E009C5BDFF67B06AF9966082E3310FA78E237EE2F99FC9747B211257B55AA6001E
		57316E1A9C541F73F5545F6EDC624312F51F49DCB6D8B2094CCADBE3FD1764717B0A57110F9F50F10C04FCBCC0769BE89BC9177F610606113E18C06FAC40F535D4D5A90C17513A1C4F6FAD5EC0DA973651FB8B480F81F83F01EB58BD937C12490035FB64AA5B2D4B3B3A3354DB282E7C5ED01E7379D1FC409B4AF33EFF6621BD84C3D971DFD05EDD79F27A5783FE5EDFBC6F8B4CBF5CFF36905205D0D783908B109A873EA1C085C0AB00A98EFD1D56FFF972C179654A04B3D85396CF3A710AF36723D568092C5EF1
		CA5D54D37C3CB825EE9EBA4A6F2A20CC77A66AE621DECE5F897859EF3AE84EFD539DFDFB83694F7FCD250F78A545873F297491939B50C78C03321A37107E8FA67828D333291B2F325D4AE937E3F2FDCE4F30673D25547797946F7716D25F9B6AD05F81D0D670B67FFEB8C7CE5F8B705EEFFB36C556EE1F47B9642B528763EFBD0D8D825FD6722D237825EFAB793E014F2165D0D6910FFCDF1B6F097E5CF8F7741F26EB10FF3C12FE94458746AB69FF3E8D69AF0532123FA27DDF12EDD75245ED32676A3C19BFB769
		1CD19A4C6F2D256B779C107751E444B5D01CAF60562C655F2E1D75709D735949632236336AF1D1675909F15169336AF151E7E7A746C5A65A2FC36C07B8CE9E971DBF6BBEAE7AF28B7A7A41FC1C8F69DDE3FBD4762CB9BA73633CEAC6E9B0199F267ECD765FE0FF7A9B46BCD9AD84D78179D60E7B5EC7289B7B5FD0B7A374DB47A04807F35CB7A8AE8E72856FA0AE25C1A98729BF99FFC1D3286352557689B4269287CDB5E6145E534AF10D3F176CF943144EEE7FBDDB58B123B45898EF4C45B93F1963F439B867CF
		71F5C04F4F8A6A789827653B7411A3FB7E5F2FE1BD8C789A56439B737959B9177979EDAEB9D74B1EAFEE1BF919674CA88D96839868B742521958A93349B940708523E9AE3CDE14623A10F768FBF3DB1ABC740BFBAB1EAD78C09BBF00DCA99F740EEB7FE5314F33FA921F3373AD927B115F29FBD3CF6221ECF8E6F57634CC0C616BDAB0AEB6C1BD92430FC1BA87001CDBB1DEFDBF1A0C57A49978C25BC0004DD2DE834F63F9FD7798FD6F4EA29A9C6D8E33D5BA2FE0E54B9D240C1DE3385E58E3B17C028665B98561
		B19C7A0F81E0F5FE542CFCEF0F54C9C9885596G30B37D4F78F90B9A9C00FAA4E5F56767FB2A2B176AEE233C8B74BBAA7697F3F8A28E6FCD3AFA49B30B6F1FC9154777CDAB22925E9754637B9E09161F141278BE3E635AC426370C96D71F35227C5E216C4B1804A4B7FDC6CAFD45CAA939F7E07E5CC08B7AF3C509215A9EG962F7A37E00C6E8DB8962FBEC56B06C0BE92A09EE0EA7A08B94C74115FC7F22C7F179AB02E4FCE427E3FCFE320D6481BG46121017E54D24AE19E35E3CEEE0B9299B87F9DDF22836B8
		99EDFFAC0F7459E6F350BB89066B5CA63FDBE13163FAA33175BAE85783608560836087908C9086908DA02C85FB83A883E882688630G0C814C8138950079D1BF6EA55F15390BAF25C3A4D9F409FC8C1A485C8D607DC4746E7050CF234DA432D3F8DC772CADD797E70DDDE53BC553EE476209139FD15BD99B2A08B34ED0FC188C4F6CB2125BE5A414695705CAFBCCCC03123E5F3AD06E9785491E4753DFC7636E656BC29571F0A645C749700C3E4FD10C0763212CF7BD52FF3C196CB3416BA75289D341C52B60681A
		13922A4DCE99DFD68CF946D3D985BDCFCBD172DACD716DA9CADE6FABC7DECDD036B485EDE92B51FDAC3243B8F9AC3257289E0B6CB3CE0CC58E9A55E3119A6344D8245EC8F86D07B9514D6B9D6138539CF70C76C9F0F43365780FA9DF840F1BAD431F22FCDD2438EE8957A3431D16F086A3D98FC064811B835B1EB312F9F84EEC31C8F1912DE3368E09CE2E8F9B6551ECB74878F6F21C0B6F4FCD246D5038F95ACD3FBBE64F8659730BADB36F9BA5FDB4737E607AC60C11CAB604CAB152B71A50C7EC5F0073A71546
		D7E3101F43F13994678FF98D475DB46DD80F7429061F2302176E2D26CD43DDFC2496DBB19D3CCE772ED860A1D50BFAB9FC04608AA1DF47F15FBAC85F87033C1D639E2338D9103F15635EABC71D86F05CA90A0B04FC3C9675F730025E3FED8C559E580838FA0A6B067C9847ADADC79D10FA69D21B7F2C7DBD43E9201E7E1D6F7E15407C5B32C6748767EDE47EEDG24A1GA476E6BE1ECFD32107FEA2A539194F3004FEC358F8341DFCEAA861E6FFCC6EE4DC7EEC43B02C0CD672EDD6D9AA7143DB7472EF1263A45A
		85296833B6B749FD5651D4D69EGE9C8AA7AAF29C7545F2FA922B6C3FC6DC8DE376B8821E7348D5AEDD6D85ACFB84C70AF3D29E7ED6C34CDAC549752304DCD87B3DC7D375A0C6C3736D87DBB283DFD851B4B81DEFF55C172229E745FE99BB68EB40F5A8DD6937DC9B329CDD2B95EEBB425215F7D4FF2CFFA6ECC136B59DACE643539D8177A7DE6B93FE3633C7C2504485FA4768D77CB010351E133D846FD6B13E9A631D313F9488E714F04DA526F48D54847E1AE74CF4EE616C29A1E06E3B53FDA2DCEAC24A96921
		527DD6F7A37F96GF05D69682F4EEBC45E6724233F3A0C56DD8A79880038F414E9D1137C9D9A695B70F3EE3E8DEFB574615D15973F0753E504CA5F20FCF5C47E3B5801995B935DFD0B2D4FE0E3B010610AB1CA62715B74EF3F3BCF18648ED1B187701E167176D5935A04EF864A7FDC9311BF8272F1GBA9D4ADFFDA49152FDE598B3FDF0CE60E37945C1729DBBDE4516B62A98FA69A7ED475ACE1CEA63ED169DA4693E4AB2A93F251C3E6F669896D97EE439BC96E0381F14A39DC955252E2FEDCA3B1ACFF636B456
		37FE56515E5A524947283F79DD75B19AE71352EF43D5E02CD32CC3FD0CD492FD74C2FE94E00E8EEDE7D5B5567565F5FD69EFBDC6C0BE8EC01709EB7759462B7361ADD7596EE753EE477C5686BA764519CA5F7F98459F48D47A7E4B793C2A0332DB393C5769556827E4B19BAE0034ACEB7C5E45E22C777A49BA5984E53DG23GF3G7C3350CF0EB3CD7A5B5E4D239EFE5B7B1AA6CFFA51E4390BC9CEB5533B66AC25DEFAA9BEA6CB2917D5CD2817F8A24BA6544B628A249F3CC9A5AED141C95F3CD09B0C5E942A78
		AD34D80A0F1761997DB10AA77954EC70D332117E6AEA243FBCFBA22DA8959C23D3C0FF73B8BEDBC9FF974527EED6527FCD8D52AF00B22F4DC8DFCFC2F9E9ACBE16BD2FEC1CF95F7A359925417C3B39F42D0F84F9E3B7234F175002FE20100338CA0AAB01FCD38E5A762EFE77B1605F7B553FCD5F5F2F7EEDFAE53F675F042E6FF7776DBA636F869E8FF46760587D2B59B5DE67E44FEB1AE6FE6CD21A3F065F3123EB7FAC1057170F535ECA54C3A057C3CCA57DBFC1F87EAD1ACF49457CB515D3651574F1F57A7871
		14E9DC0D7F4EE4DFCBFF1F389864EA4DC5FD1C2DC1FD74F2F9F75492792F6779921A77677905B55399BB524F7475714834E8CCD7A74C77D8209F7FBE7E826286CAE752F9AD059E5AEBF265FEF4FB8B7DCDDC1BC36CF758062DA6744F87681A9081F523F351BF777798872566497BDDD5C17C69915161B49BE17B234ECD8378FEB60B59F8C92211CC6D796FG8550163D8F58D06909C64FB89AA525643CD86657F702BA288745622A3DD572384A8EEDC98C6B1307BEE4C14D64BC1657A00FFE1C47619ACFBCC60F63
		3168C39AFB48638A4EA546FF7F2AD572C9626CF84E67BFDB48D81F92072431FE763C407B5F5BC462AC192FEB31B9CA9D22894B9FE9A2ED1B8DC3CE69B7A2AA7789CA1F15FB1EB227357164095656067A131F55371E2ABF5D4169FDD6496E1148599E7A34673F9C77DD3C4B276512B29F352F8A7DFBDDBE4A56D6736F71737D9A12BAEDF60906F7AD525067631C6F2F7278AED63E3BF31E9BEABD3573D6E94756254BD57C77C673F12D722FC5DC886058EF6F7D308AE3459840A5EF813F9D20894034856BBCD80BF5
		E6814E87A0GA08CA0EE8B7A7E0B2BB0EE4C5B9AAA7D0E5A2AAA656F2811FB10D281FF87EDEB3572F7549246159F2FD67E0EDA6E3872AB554A5FD1437652EFD557A2CF6D1C272D07BC73F47C10E71E2E9C724C537CC36EF8E2639964518E5C2F672FFA3483B5E07F6757150E7B00813F170535CEA1197CFE87A46485CF2730DEFA19969064F7AAA6A99EF28899FC063750236508117DD5C94F5D1214A8A437886B58F4EC911249F9F90B30169E273788C9B0ADC66D12AAB88CFDAEDC96D2A6E09D423B9EA36687DE
		7CC5734DF30DA10960C28A9BE587AF1442FA27A0C09769639722041F65DE2B7B6E7E61C4A16508306E1C41E29FB0GFB749739DB050DFD14CF72FD0D45A968DAC95B8F216D0734EDF52B10E89CF28ADBDDF8A34133528E3B41A8C2158DA60363826D01C815F1C658F04E515FC789B80544B3C74EGD91AA345192D0256B940FE0AF2546994F2DDDD37134BB529201ACAA43C738E2C45748F6FCD77DC115F1B46DD375E6D77DA496F3135A16FBD57B9890BA90B3B68F9AD606D6F212FBD7A8959DF5C7D6EDE472041
		E2F19AAC976A2DA6729BFC8D171C224B377EFB9769FF886BC8052C6CC097617B3C2C4D7F83D0CB8788F7C832C313A1GG68EEGGD0CB818294G94G88G88GE3E017AEF7C832C313A1GG68EEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4DA1GGGG
	**end of data**/
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
	 * @return com.cannontech.yc.gui.ClearPrintButtonPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private ClearPrintButtonPanel getClearPrintButtons() {
		if (ivjClearPrintButtons == null) {
			try {
				ivjClearPrintButtons = new com.cannontech.yc.gui.ClearPrintButtonPanel();
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
	 * Returns the connToDispatch.
	 * @return com.cannontech.message.util.ClientConnection
	 * @see com.cannontech.database.cache.DBChangeListener#getClientConnection()
	 */
	public com.cannontech.message.util.ClientConnection getClientConnection()
	{
		if( connToDispatch == null )
		{
			String host = "127.0.0.1";
			int port = 1510;
			try
			{
				host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
	            
				port = Integer.parseInt(
							RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) );
			}
			catch( Exception e)
			{
				CTILogger.error( e.getMessage(), e );
			}
	
			connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
			com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
			reg.setAppName(CtiUtilities.getAppRegistration());
			reg.setAppIsUnique(0);
			reg.setAppKnownPort(0);
			reg.setAppExpirationDelay( 300 );  // 5 minutes should be OK
	
			connToDispatch.setHost(host);
			connToDispatch.setPort(port);
			connToDispatch.setAutoReconnect(true);
			connToDispatch.setRegistrationMsg(reg);
			
			try
			{
				connToDispatch.connectWithoutWait();
			}
			catch( Exception e ) 
			{
				CTILogger.error( e.getMessage(), e );
			}
		}
		return connToDispatch;
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
	 * @return com.cannontech.yc.gui.CommandLogPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public CommandLogPanel getCommandLogPanel() {
		if (ivjCommandLogPanel == null) {
			try {
				ivjCommandLogPanel = new com.cannontech.yc.gui.CommandLogPanel();
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
	 * @return com.cannontech.yc.gui.CommandPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private CommandPanel getCommandPanel() {
		if (ivjCommandPanel == null) {
			try {
				ivjCommandPanel = new com.cannontech.yc.gui.CommandPanel();
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
			locRouteDialog = new LocateRouteDialog();
			locRouteDialog.addItems(getYC().getAllRoutes());
			locRouteDialog.getLocateButton().addActionListener(this);
			locRouteDialog.getRouteComboBox().addActionListener(this);
		}
		return locRouteDialog;
	}
	
	/**
	 * Returns ycClass.getModelType().
	 * @return int com.cannontech.database.model.ModelFactory.
	 */
	private int getModelType()
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
	 * @return com.cannontech.yc.gui.SerialRoutePanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private SerialRoutePanel getSerialRoutePanel() {
		if (ivjSerialRoutePanel == null) {
			try {
				ivjSerialRoutePanel = new com.cannontech.yc.gui.SerialRoutePanel();
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
	 * @return com.cannontech.yc.gui.menu.YCCommandMenu
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
	 * @return com.cannontech.yc.gui.menu.YCFileMenu
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
	 * @return com.cannontech.yc.gui.menu.YCHelpMenu
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
	 * @return com.cannontech.yc.gui.menu.YCViewMenu
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
	public void handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg msg, com.cannontech.database.data.lite.LiteBase object)
	{
		java.awt.Cursor savedCursor = null;
		try
		{
			savedCursor = getRootPane().getCursor();
			getRootPane().setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			restoreCurrentTree();	// Refresh the device tree and the route combo box.
		}
		catch( Exception e )
		{
			e.printStackTrace();
		}
		finally
		{
			getRootPane().setCursor( savedCursor);
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
			setSize(841, 647);
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
		com.cannontech.database.model.LiteBaseTreeModel[] models =
			new com.cannontech.database.model.LiteBaseTreeModel[getTreeModels().length];
		
		for (int i = 0; i < models.length; i++)
		{
			if( getTreeModels()[i] == ModelFactory.DEVICE)
				models[i] =  new com.cannontech.database.model.DeviceTreeModel(false);
			else if ( getTreeModels()[i] == ModelFactory.LMGROUPS)
				models[i] = new com.cannontech.database.model.LMGroupsModel(false);
			else if ( getTreeModels()[i] == ModelFactory.CAPBANKCONTROLLER )
				models[i] = new com.cannontech.database.model.CapBankControllerModel(false);
			else
				models[i] = ModelFactory.create(getTreeModels()[i]);
		}

		//serial and route panel visible only when first item in tree is Versacom Serial #
		if (!ModelFactory.isEditableSerial(getTreeModels()[0]))
			enableSerialAndRoute(false);
	
		getTreeViewPanel().setTreeModels(models);
			
		setRouteModel(); //fill route combo box

		com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);
	
		addWindowListener(new java.awt.event.WindowAdapter(){
			public void windowClosing(java.awt.event.WindowEvent e){ 
					exit();
			};
		});
		getYC().addObserver(this);
		getTreeViewPanel().getTree().setSelectionInterval(0,0);
		// user code end
	}
	
	/**
	 * Returns true when setup is fully complete, all connections exist/valid.
	 * @return boolean
	 */
	private boolean isValidSetup()
	{
		if( getPilConn().isValid() )
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
		else
		{
			getCommandLogPanel().addLogElement(" ** Warning: Not connected to port control service **");
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
			String commandString = CommandFuncs.loadPromptValue((String) getCommandPanel().getExecuteCommandComboBoxTextField().getText().trim(), this);
			if (commandString != null)	//null is a cancel from prompt
			{
				setCommand(commandString);			
				if( isValidSetup())
				{
					getCommandPanel().enter(getCommand());
					getYC().executeCommand();
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
			System.setProperty("cti.app.name", "Commander");
			javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());
			SplashWindow splash = new SplashWindow(
					null, CtiUtilities.CTISMALL_GIF, "Loading resources...",
					new java.awt.Font("dialog", 0, 14), java.awt.Color.black,
					java.awt.Color.black, 1 );

	
			ClientSession session = ClientSession.getInstance(); 
			if(!session.establishSession(null)) {
				System.exit(-1);			
			}
		
			if(!session.checkRole(CommanderRole.ROLEID)) {
			  JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
			  System.exit(-1);				
			}

	
			YukonCommander ycClient;
			ycClient = new YukonCommander();
			
			ycClient.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage(COMMANDER_GIF));
						
			ycClient.setTitle(YC_TITLE);
			splash.setDisplayText("Opening connection to database...");
	
			//Test that a connection can actually be made.
			java.sql.Connection c = null;
			try
			{
				c = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
	
				if (c == null)
				{
					splash.setDisplayText("Error connecting to database, closing");
					Thread.sleep(2000);		//let them see
					System.exit(0);
				}
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				splash.setDisplayText("Error connecting to database, closing");
				Thread.sleep(2000);		//let them see
				System.exit(0);
			}
			finally
			{
				if (c != null)
					c.close();
			}
	
			ycClient.getClientConnection();
	
			//set the app to start as close to the center as you can....
			//  only works with small gui interfaces.
			java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			ycClient.setLocation((int)(d.width * .07),(int)(d.height * .07));


			splash.setVisible( false );
			splash.dispose();			
			ycClient.show();
//			ycClient.getTreeViewPanel().getTree().setSelectionInterval(1,1);
			ycClient.getTreeViewPanel().getTree().requestFocusInWindow();

		}
		catch (Throwable exception)
		{
			System.err.println("Exception occurred in main() of javax.swing.JFrame");
			exception.printStackTrace(System.err);
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
			setRouteModel();
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
			
		if (selectedItem instanceof com.cannontech.database.data.lite.LiteBase)
		{
			com.cannontech.database.data.lite.LiteBase lb = (com.cannontech.database.data.lite.LiteBase)selectedItem;
			tvp.selectLiteBase( new javax.swing.tree.TreePath ( tvp.getTree().getModel().getRoot() ),lb );
	
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
		
		if( fileChooser.showSaveDialog(parent) == javax.swing.JFileChooser.APPROVE_OPTION )
		{
			java.io.FileWriter fWriter = null;
			try
			{
				fWriter = new java.io.FileWriter( fileChooser.getSelectedFile().getPath(), true );
				java.io.PrintWriter pWriter = new java.io.PrintWriter( fWriter );
	
				pWriter.print( textPane.getText() );
				fWriter.close();
			}
			catch( java.io.IOException e )
			{				
				javax.swing.JOptionPane.showMessageDialog( parent, "An error occured saving to a file", "Error", javax.swing.JOptionPane.ERROR_MESSAGE );
			}
			finally
			{
				try
				{
					if( fWriter != null )
						fWriter.close();
				} 
				catch( java.io.IOException e2 )
				{
					e2.printStackTrace();
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
	 */
	private void setCommand(String newCommand)
	{
		getYC().setCommandString(newCommand);
	}
	
	/**
	 * Sets the ycClass.modelType
	 * Valid types located in - com.cannontech.database.model.ModelFactory.DEVICE, DEVICE_METERNUMBER, 
	 * MCTBROADCAST, LMGROUPS, CAPBANKCONTROLLER, COLLECTIONGROUP, TESTCOLLECTIONGROUP, EDITABLE_xxx
	 * @param typeSelected int
	 */
	private void setModelType(int typeSelected)
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
		int index = getTreeViewPanel().getSortByComboBox().getSelectedIndex();
		if( index < 0 )
			return;
			
		setModelType( getTreeModels()[index] );
		Object selectedItem = getTreeViewPanel().getSelectedItem();
		
		if (ModelFactory.isEditableSerial(getModelType()))
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

		getYCCommandMenu().locateRoute.setEnabled(false);	//init to false, will change below if valid state.
		getYCCommandMenu().installAddressing.setEnabled(false);	//init to false, will change below if valid state.

		if (getModelType() == ModelFactory.DEVICE)
		{
			if( selectedItem instanceof LiteYukonPAObject)
			{
				LiteYukonPAObject lpao = (LiteYukonPAObject) selectedItem;
				if( DeviceTypesFuncs.isMCT(lpao.getType()) || DeviceTypesFuncs.isRepeater(lpao.getType()))
				{
					getYCCommandMenu().locateRoute.setEnabled(true);
				}
			}
		}
		else if( ModelFactory.isEditableSerial(getModelType()))
		{
			getYCCommandMenu().installAddressing.setEnabled(true);
		}
		
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
		int index = getTreeViewPanel().getSortByComboBox().getSelectedIndex();
		if( index < 0 )
			return;
		setModelType( getTreeModels()[index] );
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

		if (selectedItem == null)
			return;
	
		if (getTreeViewPanel().getSelectedNode() != null)
		{
		    if( getTreeViewPanel().getSelectedNode().getParent() == null)
				return;
		}
	
		getYCCommandMenu().locateRoute.setEnabled(false);	//init to false, will change below if valid state.
		getYCCommandMenu().installAddressing.setEnabled(false);

		if ( selectedItem instanceof LiteBase)
		{
			DBPersistent dbp = LiteFactory.createDBPersistent( (LiteBase) selectedItem);
					
			if (dbp == null)
				return;
	
			getYC().setDeviceType(dbp);
			if( selectedItem instanceof LiteYukonPAObject)
			{
				LiteYukonPAObject lpao = (LiteYukonPAObject)selectedItem;
				if( DeviceTypesFuncs.isMCT(lpao.getType()) || DeviceTypesFuncs.isRepeater(lpao.getType()))
				{
					getYCCommandMenu().locateRoute.setEnabled(true);
				}
				setTitle(displayTitle + " - " + lpao.getPaoName() + " (" + PAOGroups.getPAOTypeString(lpao.getType()) + ")");
			}
			else
			    setTitle(displayTitle + " - " + getYC().getDeviceType());
		}
		else if ( ModelFactory.isEditableSerial(getModelType()))
		{
			getYCCommandMenu().installAddressing.setEnabled(true);
			setSerialNumber( (String)selectedItem);
			getSerialRoutePanel().setSerialNumberText( getSerialNumber().toString() );
			
			if( getModelType() == ModelFactory.EDITABLE_EXPRESSCOM_SERIAL)
				getYC().setDeviceType(CommandCategory.STRING_CMD_EXPRESSCOM_SERIAL);
			else if( getModelType() == ModelFactory.EDITABLE_VERSACOM_SERIAL)
				getYC().setDeviceType(CommandCategory.STRING_CMD_VERSACOM_SERIAL);
			else if( getModelType() == ModelFactory.EDITABLE_SA205_SERIAL)
				getYC().setDeviceType(CommandCategory.STRING_CMD_SA205_SERIAL);
			else if( getModelType() == ModelFactory.EDITABLE_SA305_SERIAL)
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
		else if( getModelType() == ModelFactory.COLLECTIONGROUP ||
				getModelType() == ModelFactory.TESTCOLLECTIONGROUP )
		{
			getYC().setDeviceType(CommandCategory.STRING_CMD_COLLECTION_GROUP);
			setTitle(displayTitle + " : " + selectedItem.toString());
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
			LiteCommand  lc = CommandFuncs.getCommand(ldtc.getCommandID());
			if( ldtc.isVisible() )
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
			if( outMessage.getType() == YC.OutputMessage.DEBUG_MESSAGE)
			{
				javax.swing.SwingUtilities.invokeLater( new WriteOutput(getDebugOutputTextPane(), outMessage) );
				/*TODO: HACK TO ELIMINATE TOO MUCH MUMBLE JUMBLE IN DISPLAY (TOP) PANEL 
				 * Parsing for " sent " helps eliminate the communication responses somewhat*/
				if( outMessage.getStatus() == 0 && outMessage.getText().indexOf(" sent ")< 0)	//send message to display also?
					javax.swing.SwingUtilities.invokeLater( new WriteOutput(getDisplayOutputTextPane(), outMessage) );
			}
			else if( outMessage.getType() == YC.OutputMessage.DISPLAY_MESSAGE)
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
		if (yc == null)
			yc = new YC(true);	//load defaults from file
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
	public int [] getTreeModels()
	{
		if( treeModels == null)
		{
			//Vector of ints (ModelFactory types), (Changed from array to remove size constraints)
			NativeIntVector tempModel = new NativeIntVector(15);
			tempModel.add(	ModelFactory.DEVICE);
			tempModel.add( ModelFactory.DEVICE_METERNUMBER);
			tempModel.add( ModelFactory.MCTBROADCAST);
			tempModel.add( ModelFactory.LMGROUPS);
			tempModel.add( ModelFactory.CAPBANKCONTROLLER);
			tempModel.add( ModelFactory.COLLECTIONGROUP);
			tempModel.add( ModelFactory.TESTCOLLECTIONGROUP);

			boolean needDefault = true;
			ClientSession session = ClientSession.getInstance();

			AuthFuncs.getRolePropertyValue(session.getUser(), CommanderRole.COMMAND_MSG_PRIORITY);
						
			
			if( Boolean.valueOf(AuthFuncs.getRolePropertyValue(session.getUser(), CommanderRole.DCU_SA305_SERIAL_MODEL)).booleanValue())
			{
				tempModel.add( ModelFactory.EDITABLE_SA305_SERIAL);
				needDefault = false;
			}
			if( Boolean.valueOf(AuthFuncs.getRolePropertyValue(session.getUser(), CommanderRole.DCU_SA205_SERIAL_MODEL)).booleanValue())
			{
				tempModel.add( ModelFactory.EDITABLE_SA205_SERIAL);
				needDefault = false;
			}
			if( Boolean.valueOf(AuthFuncs.getRolePropertyValue(session.getUser(), CommanderRole.EXPRESSCOM_SERIAL_MODEL)).booleanValue())
			{
				tempModel.add( ModelFactory.EDITABLE_EXPRESSCOM_SERIAL);
				needDefault = false;
			}
			if( Boolean.valueOf(AuthFuncs.getRolePropertyValue(session.getUser(), CommanderRole.VERSACOM_SERIAL_MODEL)).booleanValue())
			{
				tempModel.add( ModelFactory.EDITABLE_VERSACOM_SERIAL);
				needDefault = false;
			}
			if( needDefault )
				tempModel.add( ModelFactory.EDITABLE_LCR_SERIAL);
			
			treeModels = tempModel.toArray();
		}
		return treeModels;
	}

}
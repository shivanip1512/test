package com.cannontech.dbeditor;

/**
 * This type was created in VisualAge.
 */
import javax.swing.*;
import com.cannontech.dbeditor.menu.*;
import com.cannontech.dbeditor.offsets.PointOffsetLegend;
import com.cannontech.common.wizard.*;
import com.cannontech.database.model.*;
import java.awt.*;
import java.awt.event.*;

import java.util.Vector;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.tree.DefaultMutableTreeNode;

import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.MessagePanel;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.gui.util.SplashWindow;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.util.FileMessageLog;
import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.database.DatabaseTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.route.RouteBase;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.dbeditor.defines.CommonDefines;
import com.cannontech.dbeditor.editor.defaults.DefaultRoutes;
import com.cannontech.dbeditor.editor.defaults.DefaultRoutesDialog;
import com.cannontech.dbeditor.editor.regenerate.RegenerateDialog;
import com.cannontech.dbeditor.editor.regenerate.RegenerateRoute;
import com.cannontech.dbeditor.wizard.changetype.device.DeviceChngTypesPanel;
import com.cannontech.dbeditor.wizard.device.lmconstraint.LMProgramConstraintPanel;
import com.cannontech.debug.gui.*;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.roles.application.BillingRole;
import com.cannontech.roles.application.DBEditorRole;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.roles.yukon.SystemRole;

import java.awt.Dimension;

public class DatabaseEditor
	implements
		com.cannontech.common.editor.PropertyPanelListener,
		com.cannontech.common.wizard.WizardPanelListener,
		ActionListener,
//		ItemListener,
		WindowListener,
//		TreeSelectionListener,
		java.util.Observer,
		com.cannontech.clientutils.popup.PopUpEventListener,
		javax.swing.event.PopupMenuListener,
		com.cannontech.database.cache.DBChangeListener 
{
   //all editor frame sizes
   private static final Dimension EDITOR_FRAME_SIZE = new Dimension(435, 500);
   
	//gui elements of the app
	private DBEditorTreePopUpMenu treeNodePopUpMenu = null;

	private JMenuBar menuBar;
	private JDesktopPane desktopPane;
	private JScrollPane deskTopFrameScrollPane;
	private MessagePanel messagePanel;
	private com.cannontech.common.gui.util.TreeViewPanel treeViewPanel;
	private JComboBox sortByComboBox;
	private JPanel contentPane;
	private JSplitPane splitPane;
	private JScrollPane scrollPane;
	private FileMenu fileMenu;
	private EditMenu editMenu;
	private CoreCreateMenu coreCreateMenu;
	private LMCreateMenu lmCreateMenu;
	private SystemCreateMenu systemCreateMenu;
	private CapControlCreateMenu capControlCreateMenu;
	private ViewMenu viewMenu;
	private HelpMenu helpMenu;
	private ToolsMenu toolsMenu;
	private java.awt.Frame owner = null;
	//File logger
	private FileMessageLog fileMessageLog;

	//Allow editor frames at a time
	private JTreeEditorFrame[] editorFrames = null;
	private static final int INITIAL_EDITOR_COUNT = 4;

	private int currentDatabase = DatabaseTypes.CORE_DB;
	//Map of database types and treemodels to use
	//Keep and add to these in alphabetical order	
	
	private static final Integer[] CORE_MODELS =
		{
			new Integer(ModelFactory.PORT),
			new Integer(ModelFactory.DEVICE),
			new Integer(ModelFactory.IED),
			new Integer(ModelFactory.MCT),
         	new Integer(ModelFactory.MCTBROADCAST),
			new Integer(ModelFactory.TWOWAYCONFIG),         
			new Integer(ModelFactory.METER),
			new Integer(ModelFactory.DEVICE_METERNUMBER),
			new Integer(ModelFactory.ROUTE),
			new Integer(ModelFactory.RTU),
			new Integer(ModelFactory.STATEGROUP),
			new Integer(ModelFactory.TRANSMITTER)
						
		};
	private static final Integer[] LM_MODELS =
		{
			new Integer(ModelFactory.LMCONSTRAINT),
			new Integer(ModelFactory.LMCONTROLAREA),
			new Integer(ModelFactory.LMGROUPEMETCON),
			new Integer(ModelFactory.LMGROUPEXPRESSCOM),
			new Integer(ModelFactory.GOLAY),
			new Integer(ModelFactory.LMGROUPS),
			new Integer(ModelFactory.LMPROGRAM),
			new Integer(ModelFactory.LMGROUPSA305),
			new Integer(ModelFactory.LMGROUPSA205),
			new Integer(ModelFactory.LMGROUPSADIGITAL),
			new Integer(ModelFactory.LMSCENARIO),
			new Integer(ModelFactory.LMGROUPVERSACOM)
		};
	private static final Integer[] CAP_CONTROL_MODELS =
		{
			new Integer(ModelFactory.CAPBANK),
			new Integer(ModelFactory.CAPBANKCONTROLLER),
			new Integer(ModelFactory.CAPCONTROLFEEDER),
			new Integer(ModelFactory.CAPCONTROLSTRATEGY)
		};
	private static final Integer[] SYSTEM_MODELS =
		{
			new Integer(ModelFactory.ALARM_STATES),
			new Integer(ModelFactory.BASELINE),
			new Integer(ModelFactory.CICUSTOMER),
			new Integer(ModelFactory.CONTACT),
			new Integer(ModelFactory.HOLIDAY_SCHEDULE),
			new Integer(ModelFactory.LOGINS),
			new Integer(ModelFactory.LOGIN_GROUPS),
			new Integer(ModelFactory.NOTIFICATION_GROUP),
			new Integer(ModelFactory.SEASON),
			new Integer(ModelFactory.TAG)
		};
	private static final Integer[] NONLOGIN_SYSTEM_MODELS =
		{
			new Integer(ModelFactory.ALARM_STATES),
			new Integer(ModelFactory.BASELINE),
			new Integer(ModelFactory.CICUSTOMER),
			new Integer(ModelFactory.CONTACT),
			new Integer(ModelFactory.HOLIDAY_SCHEDULE),
			new Integer(ModelFactory.NOTIFICATION_GROUP),
			new Integer(ModelFactory.SEASON),
			new Integer(ModelFactory.TAG)
		};	
	

	private Vector messageListeners = new Vector();

	//During an item state change we need to remember previous selection
	//in order query the user whether we should save or not
	private Object lastSelection = null;
	
	private static int decimalPlaces;
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	private boolean connToVanGoghErrorMessageSent = true;
	private boolean copyingObject = false;
	private boolean changingObjectType = false;

	//Flag whether billing option should be present in create (core) menu
	private boolean activateBilling;
	private static boolean isSuperuser = false;
	private boolean accessOfLoginNotAllowed = false;
/**
 * DatabaseEditor constructor comment.
 */
public DatabaseEditor() {
	super();
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(ActionEvent event)
{
	if( !( event.getSource() instanceof JMenuItem) )
		return;

	JMenuItem item = (JMenuItem) event.getSource();

	if( item == viewMenu.coreRadioButtonMenuItem &&
		currentDatabase != DatabaseTypes.CORE_DB )
	{
		java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(getContentPane());
		java.awt.Cursor savedCursor = f.getCursor();
		try
		{
			f.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );		
	 
			currentDatabase = DatabaseTypes.CORE_DB;
			setDatabase(currentDatabase);
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			f.setCursor(savedCursor);
		}

		f.repaint();
	}
	else
	if( item == viewMenu.lmRadioButtonMenuItem &&
		currentDatabase != DatabaseTypes.LM_DB )
	{
		java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(getContentPane());
		java.awt.Cursor savedCursor = f.getCursor();
		try
		{
			f.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			
			currentDatabase = DatabaseTypes.LM_DB;
			setDatabase(currentDatabase);
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			f.setCursor(savedCursor);
		}

		f.repaint();
	}
	else
	if( item == viewMenu.capControlRadioButtonMenuItem &&
		currentDatabase != DatabaseTypes.CAP_CONTROL_DB )
	{
		java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(getContentPane());
		java.awt.Cursor savedCursor = f.getCursor();
		try
		{
			f.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			
			currentDatabase = DatabaseTypes.CAP_CONTROL_DB;
			setDatabase(currentDatabase);
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			f.setCursor(savedCursor);
		}

		f.repaint();
	}
	else
	if( item == viewMenu.systemRadioButtonMenuItem &&
		currentDatabase != DatabaseTypes.SYSTEM_DB )
	{
		java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(getContentPane());
		java.awt.Cursor savedCursor = f.getCursor();
		try
		{
			f.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			
			currentDatabase = DatabaseTypes.SYSTEM_DB;
			setDatabase(currentDatabase);
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			f.setCursor(savedCursor);
		}

		f.repaint();
	}
	else
	if( item == fileMenu.exitMenuItem )
	{
		if( exitConfirm() )		
			exit();
	}
	else
	if( item == editMenu.editMenuItem )
	{
		executeEditButton_ActionPerformed(event);
	}
	else 
	if( item == editMenu.deleteMenuItem )
	{
		executeDeleteButton_ActionPerformed(event);
	}
	else
	if( item == editMenu.copyMenuItem )
	{
		executeCopyButton_ActionPerformed( event );		
	}
	else if( item == editMenu.searchMenuItem )
	{
		executeFindButton_ActionPerformed( event );
	}
	else if ( item == toolsMenu.regenerateMenuItem )
	{
		executeRegenerateButton_ActionPerformed( event);
	}
	else if( item == toolsMenu.defaultMenuItem ) 
	{
		executeDefaultButton_ActionPerformed(event);
	}
	else if ( item == helpMenu.ptOffsetLegendMenuItem )
	{
		executePointOffsetLegend_ActionPerformed( event );
	}
	
	else
	if( item == viewMenu.refreshMenuItem )
	{
		java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(getContentPane());
		java.awt.Cursor savedCursor = f.getCursor();
		
		try
		{
			f.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			
			//refresh the cache and the connections state
			com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();
			
			//grab the current selected object in the tree
			Object holder = treeViewPanel.getSelectedItem();
			//do the actual refresh of the tree
			treeViewPanel.refresh();
			if(holder != null && holder instanceof LiteBase)
			{
				//reselect the object, wherever it may have ended up now
				//make sure it isn't a point, as this will result in a failed selection
				//which will leave the treeviewpanel on the final sort-by model
				if(! (holder instanceof LitePoint))
					treeViewPanel.selectLiteObject((LiteBase)holder);
				DefaultMutableTreeNode tempNode = treeViewPanel.getSelectedNode();
				
				if(tempNode != null)
				{
					javax.swing.tree.TreeNode[] path = tempNode.getPath();
					javax.swing.tree.TreePath newPath = new javax.swing.tree.TreePath(path);
					
					treeViewPanel.selectLiteBase(newPath, (LiteBase)holder);
				}
			}
			
			if( getConnToDispatch().isValid() )
			{
				f.setTitle("Yukon Database Editor [Connected to Dispatch@" +
							 		 getConnToDispatch().getHost() + ":" +
							 		 getConnToDispatch().getPort() + "]" );
				f.repaint();
			}
			else
			{
				f.setTitle("Yukon Database Editor [Not Connected to Dispatch]");
				f.repaint();
			}
			
		}
		catch(Exception e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			f.setCursor(savedCursor);
			fireMessage( new MessageEvent( this, "Tree view, connection state and cache have been refreshed", MessageEvent.INFORMATION_MESSAGE) );
		}
		
	}
	else
	if( item == viewMenu.showMessageLogButton )
	{
		getMessagePanel().setVisible( viewMenu.showMessageLogButton.isSelected() );
	}
	else
	if( item == helpMenu.aboutMenuItem )
	{
		executeAboutButton_ActionPerformed( event );
		//JOptionPane.showInternalMessageDialog(getDesktopPane(),
			//"This is version " + CommonDefines.VERSION + "\nCopyright (C) 1999-2000 Cannon Technologies.",
			//"About Yukon Database Editor",JOptionPane.INFORMATION_MESSAGE);
	}
	else
	if( item == helpMenu.helpTopicMenuItem )
	{
		//run and show our help program
		Process p = com.cannontech.common.util.CtiUtilities.showHelp( CommonDefines.HELP_FILE );
	}
	else
	{
		displayAWizardPanel( item );
	}
	
}
/**
 * This method was created in VisualAge.
 * @param listener com.cannontech.common.util.MessageEventListener
 */
public void addMessageListener(MessageEventListener listener) {

	if( !messageListeners.contains(listener) )
		messageListeners.addElement(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2001 2:36:20 PM)
 * @return java.lang.String
 * @param pointID int
 */
private boolean createDeleteString(int pointID, String nodeName, StringBuffer message ) throws java.sql.SQLException
{
	Integer ptID = new Integer( pointID );

	if( com.cannontech.database.data.point.PointBase.hasRawPointHistorys( ptID ) || com.cannontech.database.data.point.PointBase.hasSystemLogEntry( ptID ) )
		message.append("\nThis Point has historical data that will be lost if removed.");

	if( com.cannontech.database.data.point.PointBase.hasCapControlSubstationBus( ptID ) )
	{
		message.delete( 0, message.length() );
		message.append("\nbecause it is used by a CapControl Substation Bus.");
		return false;
	}

	if( com.cannontech.database.data.point.PointBase.hasCapBank( ptID ) )
	{
		message.delete( 0, message.length() );
		message.append("\nbecause it is used by a CapBank Device.");
		return false;
	}

	if( com.cannontech.database.data.point.PointBase.hasLMTrigger( ptID ) )
	{
		message.delete( 0, message.length() );
		message.append("\nbecause it is used by a LoadManagement Trigger.");
		return false;
	}

	//this point is deleteable
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 3:51:22 PM)
 * @return JTreeEditorFrame
 */
private JTreeEditorFrame createInternalEditorFrame() 
{
	// we can make the frame final because no one is supposed to reasign it to a new JTreeEditorFrame
	final JTreeEditorFrame frame = new JTreeEditorFrame();
	frame.setResizable(true);
	frame.setVisible(false);
	frame.setMaximizable(true);
	frame.setIconifiable(true);
	frame.setClosable(true);
	frame.setDefaultCloseOperation( JTreeEditorFrame.DO_NOTHING_ON_CLOSE );
	desktopPane.add( frame );

	// set up the listener so when the X box in the right hand corner is pressed	
	frame.addInternalFrameListener( new InternalFrameAdapter()
	{
		public void internalFrameClosing(InternalFrameEvent e)
		{
			// Call the events that make it seem like the CANCEL button was pressed
			if (frame.getContentPane() instanceof PropertyPanel)
			{
				PropertyPanel current = (PropertyPanel) frame.getContentPane();

				if( current != null && lastSelection != null && current.hasChanged() )
				{
					//prompt the user to save changes
					int c = javax.swing.JOptionPane.showConfirmDialog(
							getParentFrame(),
							"Do you want to save changes made to '" + frame.getOwnerNode() + 
							"'?", "Yukon Database Editor", JOptionPane.YES_NO_OPTION);
										
					if (c == JOptionPane.YES_OPTION)
						current.fireOkButtonPressed();
					else
						current.fireCancelButtonPressed();
				}
				else if ( current != null && lastSelection != null )
				{
					// act as though the cancel button has been pressed
					current.fireCancelButtonPressed();
				}
			}
		}
		
	});
	
	ImageIcon editorIcon = new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	frame.setFrameIcon(editorIcon);

	return frame;
}
/**
 * Insert the method's description here.
 * Creation date: (3/23/2001 3:14:22 PM)
 * @param item javax.swing.JMenuItem
 */
private void displayAWizardPanel(JMenuItem item)
{
	DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
	com.cannontech.database.data.lite.LiteBase selectedItem = null;
	if( node != null && node.getUserObject() instanceof com.cannontech.database.data.lite.LiteBase )
		selectedItem = (com.cannontech.database.data.lite.LiteBase)node.getUserObject();
	
	if (item == coreCreateMenu.portMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.port.PortWizardPanel());
	}
	else if (item == coreCreateMenu.deviceMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.device.DeviceWizardPanel());
	}
	else if (item == coreCreateMenu.routeMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.route.RouteWizardPanel());
	}
	else if( item == coreCreateMenu.pointMenuItem )
	{
		if (selectedItem instanceof com.cannontech.database.data.lite.LiteYukonPAObject )
		{
			showWizardPanel(
				new com.cannontech.dbeditor.wizard.point.PointWizardPanel(
					new Integer( ((com.cannontech.database.data.lite.LiteYukonPAObject) selectedItem).getYukonID()) ) );
		}
		else if (selectedItem instanceof com.cannontech.database.data.lite.LitePoint )
		{
			showWizardPanel(
				new com.cannontech.dbeditor.wizard.point.PointWizardPanel(
					new Integer( ((com.cannontech.database.data.lite.LitePoint) selectedItem).getPaobjectID()) ) );
		}
		else //selectedItem == null  will go here
			showWizardPanel(new com.cannontech.dbeditor.wizard.point.PointWizardPanel());
	}
	else if (item == coreCreateMenu.stateGroupMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.state.StateWizardPanel());
	}
	//new billing addition
	else if (item == coreCreateMenu.billingGroupMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.billing.BillingFileWizardPanel());
	}
	
	else if (item == coreCreateMenu.config2WayMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.config.ConfigWizardPanel());
	}
	
	else if (item == lmCreateMenu.lmGroupMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.device.lmgroup.LMGroupWizardPanel());
	}
	else if (item == lmCreateMenu.lmControlAreaMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.device.lmcontrolarea.LMControlAreaWizardPanel());
	}
	else if (item == lmCreateMenu.lmProgramMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.device.lmprogram.LMProgramWizardPanel());
	}
	else if( item == lmCreateMenu.pointMenuItem )
	{
		if (selectedItem instanceof com.cannontech.database.data.lite.LiteYukonPAObject )
		{
			showWizardPanel(
				new com.cannontech.dbeditor.wizard.point.lm.LMPointWizardPanel(
					new Integer( ((com.cannontech.database.data.lite.LiteYukonPAObject) selectedItem).getYukonID()) ) );
		}
		else if (selectedItem instanceof com.cannontech.database.data.lite.LitePoint )
		{
			showWizardPanel(
				new com.cannontech.dbeditor.wizard.point.lm.LMPointWizardPanel(
					new Integer( ((com.cannontech.database.data.lite.LitePoint) selectedItem).getPointID()) ) );
		}
		else //selectedItem == null  will go here
			showWizardPanel(new com.cannontech.dbeditor.wizard.point.lm.LMPointWizardPanel());

	}
	else if (item == lmCreateMenu.lmProgramConstraintMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.device.lmconstraint.LMConstraintWizardPanel());
	}
	else if (item == lmCreateMenu.lmControlScenarioMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.device.lmscenario.LMScenarioWizardPanel());
	}
	else if (item == capControlCreateMenu.capBankMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.device.capcontrol.CapBankWizardPanel());
	}
	else if (item == capControlCreateMenu.capBankControllerMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.device.capcontrol.CapBankControllerWizardPanel());
	}
	else if (item == capControlCreateMenu.capControlFeederMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.capfeeder.CCFeederWizardPanel());
	}
	else if (item == capControlCreateMenu.capControlSubBusMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.capsubbus.CCSubstationBusWizardPanel());
	}
	else if (item == capControlCreateMenu.pointMenuItem )
	{
		//showWizardPanel(new com.cannontech.dbeditor.wizard.point.capcontrol.CapControlPointWizardPanel());
		if (selectedItem instanceof com.cannontech.database.data.lite.LiteYukonPAObject )
		{
			showWizardPanel(
				new com.cannontech.dbeditor.wizard.point.capcontrol.CapControlPointWizardPanel(
					new Integer( ((com.cannontech.database.data.lite.LiteYukonPAObject) selectedItem).getYukonID()) ) );
		}
		else if (selectedItem instanceof com.cannontech.database.data.lite.LitePoint )
		{
			showWizardPanel(
				new com.cannontech.dbeditor.wizard.point.capcontrol.CapControlPointWizardPanel(
					new Integer( ((com.cannontech.database.data.lite.LitePoint) selectedItem).getPaobjectID()) ) );
		}
		else //selectedItem == null  will go here
			showWizardPanel( new com.cannontech.dbeditor.wizard.point.capcontrol.CapControlPointWizardPanel() );
	}
	else if (item == systemCreateMenu.notificationGroupMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.notification.group.NotificationGroupWizardPanel());
	}
	else if (item == systemCreateMenu.contactMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.contact.ContactWizardPanel());
	}
	else if (item == systemCreateMenu.loginMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.user.YukonUserWizardPanel());
	}
	else if (item == systemCreateMenu.loginGrpMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.user.LoginGroupWizardPanel());
	}
	else if (item == systemCreateMenu.holidayMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.holidayschedule.HolidayScheduleWizardPanel());
	}
	else if (item == systemCreateMenu.customerMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.customer.CustomerWizardPanel());
	}
	else if (item == systemCreateMenu.baselineMenuItem)
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.baseline.BaselineWizardPanel());
	}
	else if (item == systemCreateMenu.seasonMenuItem) 
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.season.SeasonScheduleWizardPanel());
	}
	else if (item == systemCreateMenu.tagMenuItem) 
	{
		showWizardPanel(new com.cannontech.dbeditor.wizard.tags.TagWizardPanel());
	}

}
/**
 * This method was created in VisualAge.
 * @param rPane javax.swing.JRootPane
 */
public void displayDatabaseEditor(javax.swing.JRootPane rPane) {
	initialize(rPane);
}
/**
 * Insert the method's description here.
 * Creation date: (6/25/2001 10:06:50 AM)
 */
private void executeAboutButton_ActionPerformed( ActionEvent event )
{
	java.awt.Frame f = getParentFrame();
	AboutDialog aboutDialog = new AboutDialog( f, "About DBEditor", true );

	aboutDialog.setLocationRelativeTo( f );
	aboutDialog.setValue(null);
	aboutDialog.show();

}
/**
 * Insert the method's description here.
 * Creation date: (10/4/2001 9:29:22 AM)
 */
private boolean executeChangeObjectType(WizardPanelEvent event)
{
	boolean success = false;
	WizardPanel p = (WizardPanel) event.getSource();

	DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
	com.cannontech.database.db.DBPersistent selectedObject =
		com.cannontech.database.data.lite.LiteFactory.createDBPersistent((com.cannontech.database.data.lite.LiteBase) node.getUserObject());

	try
	{
		Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, (com.cannontech.database.db.DBPersistent) selectedObject);
		selectedObject = t.execute();
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	boolean changeType = true;
	int currentType = 0;
	int newType = 0;
	String type = " ";

	if (selectedObject instanceof com.cannontech.database.data.device.DeviceBase)
	{
		type = ((com.cannontech.database.data.device.DeviceBase) selectedObject).getPAOType();
		currentType = com.cannontech.database.data.pao.PAOGroups.getDeviceType(type);
		newType = ((Integer) p.getValue(null)).intValue();

	}
	else if (selectedObject instanceof com.cannontech.database.data.point.PointBase)
	{
		type = ((com.cannontech.database.data.point.PointBase) selectedObject).getPoint().getPointType();
		currentType = com.cannontech.database.data.point.PointTypes.getType(type);
		newType = ((Integer) p.getValue(null)).intValue();
	}

	int confirm = 0;

	if (currentType == newType)
	{
		confirm =
			javax.swing.JOptionPane.showConfirmDialog(
				getParentFrame(),
				"Same type selected  '" + type + "'\n" + "  Continue to change type" + "?" + "\n" + "\n",
				"Confirm Type Change",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.NO_OPTION)
			changeType = false;

	}

	if(com.cannontech.database.data.device.DeviceTypesFuncs.isTransmitter(currentType))
	{
		String temp = "is a transmitter \n" + "and COULD be associated with a route." + '\n' + "Continuing may result in a route/device type mismatch." + '\n' + "Continue to change type?" + "\n" + "\n";  
		confirm =
			javax.swing.JOptionPane.showConfirmDialog(
				getParentFrame(),
				"The device '" + type + " " + temp,
				"Possible Device/Route Type Mismatch",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (confirm == JOptionPane.NO_OPTION)
			changeType = false;
	
	}
	
	
	if (changeType)
	{
		selectedObject = (com.cannontech.database.db.DBPersistent) p.getValue(selectedObject);

		try
		{

			//we actually may be INSERTING a new object
			if( selectedObject instanceof SmartMultiDBPersistent ) {
				
				SmartMultiDBPersistent smarty = (SmartMultiDBPersistent)selectedObject;
						
				for( int i = 0; i < smarty.size(); i++ )
					if( !smarty.getDBPersistent(i).equals(smarty.getOwnerDBPersistent()) )
						generateDBChangeMsg( 
								smarty.getDBPersistent(i),
								DBChangeMsg.CHANGE_TYPE_ADD );
								
				selectedObject = smarty.getOwnerDBPersistent();
			}			

			Transaction t1 = Transaction.createTransaction(Transaction.UPDATE, selectedObject);
			selectedObject = t1.execute();

			//always do this
			generateDBChangeMsg( selectedObject, DBChangeMsg.CHANGE_TYPE_UPDATE );


			//getTreeViewPanel().refresh();
			getTreeViewPanel().selectObject(selectedObject);

			//make sure there isnt already an editor frame showing for this node
			if (isEditorAlreadyShowing(node))
			{
				//set the current selected frame to the one found
				PropertyPanel current = (PropertyPanel) getEditorFrame(node).getContentPane();

				//just act as though the cancel button was pressed on the editor pane
				if (current != null)
					current.fireCancelButtonPressed();
			}
			
			
			String messageString = selectedObject + " successfully changed Type.";
			fireMessage(new MessageEvent(this, messageString));
			success = true;
		}
		catch (com.cannontech.database.TransactionException e)
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

			String messageString =
				"Error changing type of " + selectedObject + " in the database.  Error received:  " + e.getMessage();
			fireMessage(new MessageEvent(this, messageString, MessageEvent.ERROR_MESSAGE));
		}
		

	}	

	return success;
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 9:23:31 AM)
 * @param event java.awt.event.ActionEvent
 */
public void executeChangeTypeButton_ActionPerformed(ActionEvent event)
{
   int confirm = 0;
   DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
	if (node != null)
   {
	  //a DBPersistent must be created from the Lite object so you can copy it
	  com.cannontech.database.db.DBPersistent userObject =
		 com.cannontech.database.data.lite.LiteFactory.createDBPersistent(
			(com.cannontech.database.data.lite.LiteBase) node.getUserObject());

	  try
	  {
		 Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, userObject);
		 userObject = t.execute();
	  }
	  catch (Exception e)
	  {
		 com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		 
		 fireMessage( new MessageEvent(
				this,
				"Error changing type for " + node + ".  Error received:  " + e.getMessage(),
				MessageEvent.ERROR_MESSAGE));		 
	  }

	  if( userObject instanceof com.cannontech.database.data.device.DeviceBase         
		   && DeviceChngTypesPanel.isDeviceTypeChangeable( 
               ((com.cannontech.database.data.device.DeviceBase)userObject).getPAOType()) )
	  {
         showChangeTypeWizardPanel(
         		new com.cannontech.dbeditor.wizard.changetype.device.DeviceChangeTypeWizardPanel(
         	 	  userObject));
	  }
	  else if (userObject instanceof com.cannontech.database.data.point.PointBase)
	  {
		 try
		 {
			StringBuffer message =
			   new StringBuffer(
				  "Are you sure you want to change the type of '"+node.toString() + "'?");


         byte deleteVal = DBDeletionFuncs.deletionAttempted(((com.cannontech.database.data.point.PointBase) userObject)
                              .getPoint().getPointID().intValue(), DBDeletionFuncs.POINT_TYPE);
         
         if( deleteVal == DBDeletionFuncs.STATUS_ALLOW
             || deleteVal == DBDeletionFuncs.STATUS_CONFIRM )
         {
			   showChangeTypeWizardPanel(
				  new com.cannontech.dbeditor.wizard.changetype.point.PointChangeTypeWizardPanel(
					 userObject));
			}
			else
			{
			  
			   javax.swing.JOptionPane.showConfirmDialog(
				  getParentFrame(),
				  ("You cannot change this point '"+node.toString() + "'" + DBDeletionFuncs.getTheWarning().toString()),
				  "Unable to change",
				  JOptionPane.CLOSED_OPTION,
				  JOptionPane.WARNING_MESSAGE);
			   confirm = JOptionPane.NO_OPTION;
			   //make it seem they clicked the NO to delete the Point 
			}

		 }
		 catch (java.sql.SQLException e)
		 {
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			confirm =
			   javax.swing.JOptionPane.showConfirmDialog(
				  getParentFrame(),
				  "Are you sure you want to change '" + node + "'?",
				  "Confirm Delete",
				  JOptionPane.YES_NO_OPTION,
				  JOptionPane.WARNING_MESSAGE);
		 }
	  }
	  else
		 JOptionPane.showMessageDialog(
			getParentFrame(),
			"Cannot currently change the type of this Object",
			"ChangeType Error",
			JOptionPane.INFORMATION_MESSAGE);
   }
   else
	  JOptionPane.showMessageDialog(
		 getParentFrame(),
		 "You must select something to be changed",
		 "ChangeType Error",
		 JOptionPane.INFORMATION_MESSAGE);

}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 3:36:23 PM)
 * @param event java.awt.event.ActionEvent
 */
private void executeCopyButton_ActionPerformed(ActionEvent event)
{
	DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
	
	if( node != null )
	{
		//a DBPersistent must be created from the Lite object so you can copy it
		com.cannontech.database.db.DBPersistent toCopy = com.cannontech.database.data.lite.LiteFactory.createDBPersistent((com.cannontech.database.data.lite.LiteBase)node.getUserObject());
		if( toCopy instanceof com.cannontech.database.data.device.DeviceBase )
		{
			showCopyWizardPanel( new com.cannontech.dbeditor.wizard.copy.device.DeviceCopyWizardPanel((com.cannontech.database.data.device.DeviceBase)toCopy) );
		}
		else if( toCopy instanceof com.cannontech.database.data.point.PointBase )
		{
			showCopyWizardPanel( new com.cannontech.dbeditor.wizard.copy.point.PointCopyWizardPanel((com.cannontech.database.data.point.PointBase)toCopy) );
		}
		else
			JOptionPane.showMessageDialog(
				getParentFrame(),
				"Cannot currently copy that type of Object", "Copy Error", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	else
		JOptionPane.showMessageDialog(
				getParentFrame(),
				"You must select something to be copied", "Copy Error", 
				JOptionPane.INFORMATION_MESSAGE);  
}

/**
 * Insert the method's description here.
 * Creation date: (7/1/2002 8:57:21 AM)
 */
public void executeDefaultButton_ActionPerformed(ActionEvent event) {
	
	java.awt.Frame f = getParentFrame();
	java.awt.Cursor savedCursor = f.getCursor();
	f.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

	java.util.Vector defaults = DefaultRoutes.calculateDefaults();


	java.util.Vector routes = (Vector)defaults.get(0);
	java.util.Vector originalDefaults = (Vector)defaults.get(1);
	int totalRecDefaults = ((Integer)defaults.get(2)).intValue();
	int totalOrigDefaults = ((Integer)defaults.get(3)).intValue();
	
	java.util.Vector displayRoutes = DefaultRoutes.getDisplayReady(routes, originalDefaults);
	
	DefaultRoutesDialog r = new DefaultRoutesDialog(f,"Default Routes", true, totalRecDefaults, totalOrigDefaults);
	
	for (int i =0; i<displayRoutes.size(); i++) {
		
	r.addRow((Vector) displayRoutes.get(i));

	}
	
	
	
	r.setLocationRelativeTo( f );
	
	f.setCursor(savedCursor);
	
	r.show();

	savedCursor = f.getCursor();
	f.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
	
	
	
	if (r.getResponse() == r.PRESSED_UPDATE)
	{
		
		Vector newDefaults = r.getRecommendedDefaults();
		for (int i=0; i<routes.size(); i++) {
			((RouteBase)routes.get(i)).setDefaultRoute(newDefaults.get(i).toString().toUpperCase());

		}

		DefaultRoutes.updateRouteDefaults(routes);
		for (int i=0; i<routes.size(); i++) {
			updateDBPersistent((DBPersistent) routes.get(i));
		}
	} 

	f.setCursor(savedCursor);	
}


private void deleteDBPersistent( DBPersistent deletable )
{
	try
	{
		Transaction t = Transaction.createTransaction(Transaction.DELETE, deletable);
		
		deletable = t.execute();
	
		//fire DBChange messages out to Dispatch
		generateDBChangeMsg( deletable, DBChangeMsg.CHANGE_TYPE_DELETE );
	
		fireMessage(new MessageEvent(this, deletable + " deleted successfully from the database."));
	}
	catch (com.cannontech.database.TransactionException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		fireMessage(
			new MessageEvent(
				this,
				"Error deleting " + deletable + " from the database.  Error received:  " + e.getMessage(),
				MessageEvent.ERROR_MESSAGE));
	}

}

/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 5:28:42 PM)
 * @param event java.awt.event.ActionEvent
 */
private void executeDeleteButton_ActionPerformed(ActionEvent event)
{	
	TreeItemDeleter tid = new TreeItemDeleter( getTreeViewPanel() );
	int confirm = tid.executeDelete();
   DefaultMutableTreeNode[] nodes = tid.getNodes();
	DBPersistent[] deletables = tid.getDeletables();
	
	if (confirm == JOptionPane.YES_OPTION)
	{
		for( int i = 0; i < nodes.length; i++ )
		{
			//make sure there isnt already an editor frame showing for this node
			if (isEditorAlreadyShowing(nodes[i]))
			{
				//set the current selected frame to the one found
				PropertyPanel current = (PropertyPanel) getEditorFrame(nodes[i]).getContentPane();
	
				//just act as though the cancel button was pressed on the editor pane
				if (current != null)
					current.fireCancelButtonPressed();
			}		


			try
			{
				deleteDBPersistent( deletables[i] );
			}
			finally
			{
				//Destroy the frame on an ok or a cancel
				JTreeEditorFrame frame = getEditorFrame(nodes[i]);
				if (frame != null)
				{
					frame.setVisible(false);
					PropertyPanel current = (PropertyPanel) frame.getContentPane();
					current.setChanged(false);
				}
	
			}
		}//end for loop
	}

}



/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 3:36:23 PM)
 * @param event java.awt.event.ActionEvent
 */
private void executeEditButton_ActionPerformed(ActionEvent event)
{
   java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(getTree());   
   java.awt.Cursor savedCursor = owner.getCursor();


   try
   {
		owner.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));

		DefaultMutableTreeNode[] nodes = getTreeViewPanel().getSelectedNodes();

		if( nodes != null && nodes.length >= 1 )
		{
			lastSelection = nodes[0];

			for( int i = 0; i < nodes.length; i++ )
			{
				//make sure there isnt already a editor frame showing for this node
				if( isEditorAlreadyShowing(nodes[i]) )
				{
					//set the current selected frame to the one found
					getEditorFrame(nodes[i]).setSelected(true);
					continue;
				}
				
				if (nodes[i].getUserObject() instanceof String)
					continue;

				//do some mapping to get the compatible DBPersistent
				com.cannontech.database.db.DBPersistent userObject = 
					LiteFactory.convertLiteToDBPers( (LiteBase)nodes[i].getUserObject() );

	         try
	         {
	            Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, userObject);
	            userObject = t.execute();	
	         }
	         catch (Exception e)
	         {
	            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

					fireMessage( new MessageEvent(
							this,
							"Error retrieving " + nodes[i] + " from the database.  Error received:  " + e.getMessage(),
							MessageEvent.ERROR_MESSAGE));
	         }

	         PropertyPanel panel = EditorPanelFactory.createEditorPanel( userObject );
	         							//userObject.getEditorPanel(); //This call takes the most time

	         panel.setValue(userObject);
	         panel.addPropertyPanelListener(this);

	         JTreeEditorFrame frame = getAvailableEditorFrame();
	         frame.setOwnerNode( nodes[i] ); //set the editors ownerNode
	         frame.setTitle( userObject.toString() + " : " + panel.toString());
	         frame.setContentPane(panel);
	         
	         // sets the size of EVERY editor frame!!!!!!!
	         frame.setSize( EDITOR_FRAME_SIZE );
	         frame.setLocation( getVisibleEditorFrames() * 10, getVisibleEditorFrames() * 20 );

	         frame.setVisible(true);

	         try
	         {
	            frame.setSelected(true);
	         }
	         catch (java.beans.PropertyVetoException e)
	         {
	            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e ); //when does this happen??
	         }

			}
			
		}
		else
			JOptionPane.showMessageDialog(
				getParentFrame(),
				"You must select something to be edited", "Edit Error", 
				JOptionPane.INFORMATION_MESSAGE);
		 
   }
   catch (Exception e)
   {
	  handleException( e );
   }
   finally
   {
	  owner.setCursor(savedCursor);
   }
   
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 9:23:31 AM)
 * @param event java.awt.event.ActionEvent
 */
public void executeFindButton_ActionPerformed(ActionEvent event)
{
	String value = javax.swing.JOptionPane.showInputDialog(
		getParentFrame(), "Name of the item: ", "Search",
		javax.swing.JOptionPane.QUESTION_MESSAGE );

	if( value != null )
	{
		boolean found = getTreeViewPanel().searchFirstLevelString(value);

		if( !found )
			javax.swing.JOptionPane.showMessageDialog(
				getParentFrame(), 
				"Unable to find your selected item", "Item Not Found",
				javax.swing.JOptionPane.INFORMATION_MESSAGE );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/31/2002 11:02:30 AM)
 */
public void executeRegenerateButton_ActionPerformed(ActionEvent event) {

	java.awt.Frame f = getParentFrame();
	java.awt.Cursor savedCursor = f.getCursor();
	f.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
	
	Vector carrierRoutes = RegenerateRoute.getAllCarrierRoutes();
	RegenerateDialog r = new RegenerateDialog( f, "Regenerate", true, carrierRoutes);
	r.setLocationRelativeTo( f );
	
	f.setCursor(savedCursor);
	
	r.show();

	savedCursor = f.getCursor();
	f.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
	
	if (r.getResponse() == r.PRESSED_OK)
	{
		boolean all = r.getRegenerateAll();
		Vector routesChanged = RegenerateRoute.resetRptSettings(carrierRoutes,all,null,false);
		RegenerateRoute.updateRouteRoles(routesChanged);
		for (int i=0; i<routesChanged.size(); i++) {
			updateDBPersistent((com.cannontech.database.db.DBPersistent) routesChanged.get(i));
		}
	} 

	f.setCursor(savedCursor);

return;
	
}

/**
 * Insert the method's description here.
 * Creation date: (5/31/2002 11:02:30 AM)
 */
private void executePointOffsetLegend_ActionPerformed( ActionEvent event ) 
{
	java.awt.Frame f = getParentFrame();
	java.awt.Cursor savedCursor = f.getCursor();
	f.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
	
	try
	{
		final PointOffsetLegend ptLegend = new PointOffsetLegend();
		OkCancelDialog diag = new OkCancelDialog(
			f, "Point Offset Legend", true, ptLegend );

		diag.setCancelButtonVisible( false );
		diag.setResizable( true );
		diag.setSize( 640, 500 );
		diag.setLocationRelativeTo( f );

		diag.show();	
	}
	finally
	{
		f.setCursor(savedCursor);
	}

}

/**
* Insert the method's description here.
* Creation date: (7/9/2001 10:25:56 AM)
*/
public void executeSortByNameButton_ActionPerformed(ActionEvent event)
{
	DBTreeModel currentModel = (DBTreeModel)getTreeViewPanel().getSelectedTreeModel();
	
	DBTreeNode dummyNode = (DBTreeNode)getTreeViewPanel().getSelectedNode();

	currentModel.sortChildNodes( dummyNode, DBTreeModel.SORT_POINT_NAME );	
}
/**
 * Insert the method's description here.
 * Creation date: (7/9/2001 10:25:56 AM)
 */
public void executeSortByOffsetButton_ActionPerformed(ActionEvent event)
{
	DBTreeModel currentModel = (DBTreeModel)getTreeViewPanel().getSelectedTreeModel();
	
	DBTreeNode dummyNode = (DBTreeNode)getTreeViewPanel().getSelectedNode();

	currentModel.sortChildNodes( dummyNode, DBTreeModel.SORT_POINT_OFFSET );
}
/**
 * Insert the method's description here.
 * Creation date: (1/10/2001 8:22:00 AM)
 */
private void exit() 
{

	try
	{
		if ( getConnToDispatch() != null && getConnToDispatch().isValid() )  // free up Dispatchs resources
		{
			com.cannontech.message.dispatch.message.Command comm = new com.cannontech.message.dispatch.message.Command();
			comm.setPriority(15);
			
			comm.setOperation( 
				com.cannontech.message.dispatch.message.Command.CLIENT_APP_SHUTDOWN );

			getConnToDispatch().write( comm );

			getConnToDispatch().disconnect();
		}
	}
	catch ( java.io.IOException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	//There may be events in the EventQueue up to this point,
	// let them go first then we can Exit the program.
	SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			System.exit(0);
		}
	});
}


/**
 * This method was created by Cannon Technologies Inc.
 */
private boolean exitConfirm()
{
	boolean retVal = true;
	
	for( int i = 0; i < getInternalEditorFrames().length; i++ )
	{
		if( getInternalEditorFrames()[i].getContentPane() instanceof PropertyPanel)
		{
			PropertyPanel current = (PropertyPanel) getInternalEditorFrames()[i].getContentPane();
			if (current != null )
			{
				if( current.hasChanged() && lastSelection != null )
				{
					int confirm = javax.swing.JOptionPane.showConfirmDialog(
							getParentFrame(),
							"Do you want to save changes made to '" + getInternalEditorFrames()[i].getOwnerNode() + 
							"'?", "Yukon Database Editor", JOptionPane.YES_NO_CANCEL_OPTION);
					
					// act as though the cancel button has been pressed
					if (confirm == JOptionPane.YES_OPTION)
					{
						current.fireOkButtonPressed();
					}
					else if (confirm == JOptionPane.NO_OPTION)
					{
						current.fireCancelButtonPressed();
					}
					else
						retVal = false;
						
					//updateObject((com.cannontech.database.db.DBPersistent) current.getValue(null));
				}
				else    // act as though the cancel button has been pressed
					current.fireCancelButtonPressed();
			}

		}

	}


	return retVal;
}
/**
 * This method was created in VisualAge.
 * @param event MessageEvent
 */
public void fireMessage(MessageEvent event) {

	for( int i = messageListeners.size()-1; i >= 0; i-- )
	{
		((MessageEventListener) messageListeners.elementAt(i)).messageEvent(event);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/9/2001 1:49:24 PM)
 */
private void generateDBChangeMsg( com.cannontech.database.db.DBPersistent object, int changeType  ) 
{
	if( object instanceof com.cannontech.database.db.CTIDbChange )
	{
		com.cannontech.message.dispatch.message.DBChangeMsg[] dbChange = 
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance().createDBChangeMessages(
					(com.cannontech.database.db.CTIDbChange)object, changeType );

               
		for( int i = 0; i < dbChange.length; i++ )
		{
			//handle the DBChangeMsg locally
			com.cannontech.database.data.lite.LiteBase lBase = 
					com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);

			//tell our tree we may need to change the display
			updateTreePanel( lBase, dbChange[i].getTypeOfChange() );
         
         //select the newly added device if it is the first in our list
/*         
         if( dbChange[i].getTypeOfChange() == DBChangeMsg.CHANGE_TYPE_ADD
             && i == 0 )
         {
            getTreeViewPanel().selectLiteObject( lBase );			
         }
*/         
         
			getConnToDispatch().write(dbChange[i]);
		}
	}
	else
	{
		throw new IllegalArgumentException("Non " + 
         com.cannontech.database.db.CTIDbChange.class.getName() + 
         " class tried to generate a " + DBChangeMsg.class.getName() + 
			" its class was : " + object.getClass().getName() );
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 3:42:38 PM)
 * @return javax.swing.JInternalFrame
 */
private JTreeEditorFrame getAvailableEditorFrame() 
{
	synchronized( getInternalEditorFrames() )
	{
		int i = 0;
		for( i = 0; i < getInternalEditorFrames().length; i++ )
		{
			if( !((JTreeEditorFrame)getInternalEditorFrames()[i]).isVisible() )
				return (JTreeEditorFrame)getInternalEditorFrames()[i];
		}

		// didnt find an available one, lets create a new one
		JTreeEditorFrame[] frames = new JTreeEditorFrame[getInternalEditorFrames().length + 1];
		System.arraycopy( getInternalEditorFrames(), 0, frames, 0, getInternalEditorFrames().length );
		editorFrames = frames;
		
		getInternalEditorFrames()[i] = createInternalEditorFrame();
		return getInternalEditorFrames()[i];
	}
			
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 1:45:53 PM)
 * @return com.cannontech.message.util.ClientConnection
 */
public com.cannontech.message.util.ClientConnection getClientConnection() 
{
	return getConnToDispatch();
}
/**
 * Insert the method's description here.
 * Creation date: (12/20/2001 1:46:57 PM)
 * @return com.cannontech.message.dispatch.ClientConnection
 */
private com.cannontech.message.dispatch.ClientConnection getConnToDispatch() 
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
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

		connToDispatch = new com.cannontech.message.dispatch.ClientConnection();
		com.cannontech.message.dispatch.message.Registration reg = new com.cannontech.message.dispatch.message.Registration();
		reg.setAppName("DatabaseEditor @" + com.cannontech.common.util.CtiUtilities.getUserName() );
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 300 );  // 5 minutes should be OK

		connToDispatch.addObserver(this);
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
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}

	}

	return connToDispatch;
}

/**
 * This method was created in VisualAge.
 * @return java.awt.Container
 */
private Container getContentPane() {

	if( contentPane == null )
	{
		contentPane = new javax.swing.JPanel();
		contentPane.setLayout( new java.awt.BorderLayout() );
	
		contentPane.add( getSplitPane() , "Center");
		contentPane.add( getMessagePanel(), "South" );
	}

	return contentPane;
	
}
/**
 * Insert the method's description here.
 * Creation date: (8/7/2001 10:12:47 AM)
 */
public static int getDecimalPlaces()
{
	return decimalPlaces;
}
/**
 * Return the DisplayTable property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getDeskTopFrameScrollPane() 
{
	if ( deskTopFrameScrollPane == null) 
	{
		deskTopFrameScrollPane = new javax.swing.JScrollPane();
		deskTopFrameScrollPane.setName("EditorFrameScrollPane");
		deskTopFrameScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		deskTopFrameScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		//deskTopFrameScrollPane.setBackground(java.awt.Color.white);
		//deskTopFrameScrollPane.setForeground(java.awt.Color.lightGray);
		getDeskTopFrameScrollPane().setViewportView( getDesktopPane() );
	}
	
	return deskTopFrameScrollPane;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JDesktopPane
 */
private JDesktopPane getDesktopPane() 
{
	if( this.desktopPane == null )
	{
		this.desktopPane = new JDesktopPane();

		//this.desktopPane.add( getEditorFrame() );
	}
		
	return this.desktopPane;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 2:10:16 PM)
 * @return com.cannontech.dbeditor.JTreeEditorFrame
 * @param ownerNode javax.swing.tree.DefaultMutableTreeNode
 */
private JTreeEditorFrame getEditorFrame(DefaultMutableTreeNode ownerNode) 
{
	synchronized( getInternalEditorFrames() )
	{
		for( int i = 0; i < getInternalEditorFrames().length; i++ )
			if( getInternalEditorFrames()[i].getOwnerNode() != null
				 && getInternalEditorFrames()[i].getOwnerNode().getUserObject().equals(ownerNode.getUserObject()) )
			{
				return getInternalEditorFrames()[i];
			}
	}
	
	return null;
}
/**
 * This method was created in VisualAge.
 */
private FileMessageLog getFileMessageLog() {
	if( this.fileMessageLog == null )
	{
		this.fileMessageLog = new FileMessageLog();
	}

	return this.fileMessageLog;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 10:14:25 AM)
 * @return int
 * @param panel com.cannontech.common.editor.PropertyPanel
 */
private int getFrameLocationByPanel(PropertyPanel panel) 
{
	//Loop through all the frames on the desktopPane
	//and find out which one contains the PropertyPanel
	for( int i = 0; i < getInternalEditorFrames().length; i++ )
		if( getInternalEditorFrames()[i].getContentPane() == panel )
			return i;

	//should never get here
	throw new RuntimeException("PropertyPanel '" + panel.toString() + "' found that does not have a parent JInternalFrame");
}
/**
 * This method was created in VisualAge.
 * @return JTreeEditorFrame[]
 */
private JTreeEditorFrame[] getInternalEditorFrames() 
{
	if( editorFrames == null )
	{
		editorFrames = new JTreeEditorFrame[INITIAL_EDITOR_COUNT];
		
		//create the INITIAL_EDITOR_COUNT amount of JInternalFrames
		for( int i = 0; i < INITIAL_EDITOR_COUNT; i++ )
		{			
			editorFrames[i] = createInternalEditorFrame();
		}
	}
		
	return editorFrames;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JMenuBar
 */
private JMenuBar getMenuBar(int whichDatabase) {

	if( this.menuBar == null )
	{
		this.menuBar = new JMenuBar();

		fileMenu = new FileMenu();
		editMenu = new EditMenu();
		coreCreateMenu = new CoreCreateMenu();
		lmCreateMenu = new LMCreateMenu();
		capControlCreateMenu = new CapControlCreateMenu();
		systemCreateMenu = new SystemCreateMenu();
		viewMenu = new ViewMenu();
		helpMenu = new HelpMenu();
      toolsMenu = new ToolsMenu();
		
		JMenuItem item;
		
		for( int i = 0; i < fileMenu.getItemCount(); i++ )
		{
			item = fileMenu.getItem(i);

			if( item != null )
				fileMenu.getItem(i).addActionListener(this);
		}
		
		for( int i = 0; i < editMenu.getItemCount(); i++ )
		{
			item = editMenu.getItem(i);
			
			if( item != null )	
			editMenu.getItem(i).addActionListener(this);
		}
		
		for( int i = 0; i < coreCreateMenu.getItemCount(); i++ )
		{
			item = coreCreateMenu.getItem(i);

			if( item != null )
				coreCreateMenu.getItem(i).addActionListener(this);
		}
	
		for( int i = 0; i < lmCreateMenu.getItemCount(); i++ )
		{
			item = lmCreateMenu.getItem(i);

			if( item != null )
				lmCreateMenu.getItem(i).addActionListener(this);
		}
		
		for( int i = 0; i < capControlCreateMenu.getItemCount(); i++ )
		{
			item = capControlCreateMenu.getItem(i);

			if( item != null )
				capControlCreateMenu.getItem(i).addActionListener(this);
		}
		
		for( int i = 0; i < systemCreateMenu.getItemCount(); i++ )
		{
			item = systemCreateMenu.getItem(i);

			if( item != null )
				systemCreateMenu.getItem(i).addActionListener(this);
		}

		for( int i = 0; i < viewMenu.getItemCount() ; i++ )
		{
			item = viewMenu.getItem(i);

			if( item != null )
				viewMenu.getItem(i).addActionListener(this);
		}
		
		for( int i = 0; i < helpMenu.getItemCount() ; i++ )
		{
			item = helpMenu.getItem(i);

			if( item != null )
				helpMenu.getItem(i).addActionListener(this);
		}

		for( int i = 0; i < toolsMenu.getItemCount() ; i++ )
		{
			item = toolsMenu.getItem(i);
			if( item != null )
				toolsMenu.getItem(i).addActionListener(this);
      }

		           
			
		this.menuBar.add( fileMenu );
		this.menuBar.add( editMenu );		
		this.menuBar.add( viewMenu );
	}

	this.menuBar.remove( coreCreateMenu );
	this.menuBar.remove( lmCreateMenu );
	this.menuBar.remove( capControlCreateMenu );
	this.menuBar.remove( systemCreateMenu );
	this.menuBar.remove( helpMenu );				

	JMenuItem item = null;
	//the following create menus will change with the current view change
	if( whichDatabase == DatabaseTypes.CORE_DB )
	{
		item = coreCreateMenu;
	}
	else if( whichDatabase == DatabaseTypes.LM_DB )
	{
		item = lmCreateMenu;
	}
	else if( whichDatabase == DatabaseTypes.CAP_CONTROL_DB )
	{
		item = capControlCreateMenu;
	}
	else if( whichDatabase == DatabaseTypes.SYSTEM_DB )
	{
		item = systemCreateMenu;
	}

	this.menuBar.add( item );
	this.menuBar.add( toolsMenu );
	this.menuBar.add( helpMenu );
	
	return menuBar;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.common.gui.util.MessagePanel
 */
private MessagePanel getMessagePanel() {
	if( this.messagePanel == null )
	{
		this.messagePanel = new MessagePanel();
		addMessageListener(this.messagePanel);
	}
		
	return this.messagePanel;
}
/**
 * Insert the method's description here.
 * Creation date: (2/27/2002 11:45:12 AM)
 * @return javax.swing.JFrame
 */
private JFrame getParentFrame() 
{
	return (JFrame)com.cannontech.common.util.CtiUtilities.getParentFrame(
				DatabaseEditor.this.getContentPane() );
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JScrollPane
 */
private JScrollPane getScrollPane() {
	if( this.scrollPane == null )
		this.scrollPane = new JScrollPane( getTree());

	return this.scrollPane;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JSplitPane
 */
private JSplitPane getSplitPane() {
	if( this.splitPane == null )
	{
		this.splitPane = new JSplitPane();
		this.splitPane.setRightComponent( getDeskTopFrameScrollPane() ); // getDesktopPane() );
		this.splitPane.setLeftComponent( getTreeViewPanel() );
		this.splitPane.setDividerLocation(230);
	}
		
	return this.splitPane;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JTree
 */
private JTree getTree() 
{	
	return getTreeViewPanel().getTree();
}
/**
 * Insert the method's description here.
 * Creation date: (1/8/2001 10:14:31 AM)
 */
private DBEditorTreePopUpMenu getTreeNodePopupMenu()
{

	if (treeNodePopUpMenu == null)
	{
		treeNodePopUpMenu = new DBEditorTreePopUpMenu();
		treeNodePopUpMenu.setName("TreeNodePopupMenu");
	}

	return treeNodePopUpMenu;
}
/**
 * This method was created in VisualAge.
 * @return javax.swing.JPanel
 */
private com.cannontech.common.gui.util.TreeViewPanel getTreeViewPanel() {

	if( this.treeViewPanel == null )
	{
		treeViewPanel = new com.cannontech.common.gui.util.TreeViewPanel();
		
		//treeViewPanel.addItemListener( this );

		getTree().getSelectionModel().setSelectionMode(
			javax.swing.tree.TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
	}

	return this.treeViewPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 4:01:40 PM)
 * @return int
 */
private int getVisibleEditorFrames() 
{
	synchronized( getInternalEditorFrames() )
	{
		int count = 0;
		for( int i = 0; i < getInternalEditorFrames().length; i++ )
			if( ((JTreeEditorFrame)getInternalEditorFrames()[i]).isVisible() )
				count++;
		
		return count;
	}
	
}
/**
 * Insert the method's description here.
 */
//com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase is not handled in this method???
// I dont think this will be a problem
public void handleDBChangeMsg( com.cannontech.message.dispatch.message.DBChangeMsg msg,
				com.cannontech.database.data.lite.LiteBase liteBase )
{
	//see if the message originated from us
	if( !(msg.getSource().equals(
				com.cannontech.common.util.CtiUtilities.DEFAULT_MSG_SOURCE) ) )
	{

		StringBuffer txtMsg = new StringBuffer( 
			( msg.getTypeOfChange() == msg.CHANGE_TYPE_ADD ? "ADD" :
				(msg.getTypeOfChange() == msg.CHANGE_TYPE_DELETE ? "DELETE" :
					(msg.getTypeOfChange() == msg.CHANGE_TYPE_UPDATE ? "UPDATE" : ""))) +
			" Database Change Message received from: " + msg.getUserName() + " at " + msg.getSource());

			synchronized( getInternalEditorFrames() )
			{
				for( int i = 0; i < getInternalEditorFrames().length; i++ )
				{
					//be sure we have a owner for each editor frame
					if( getInternalEditorFrames()[i].getOwnerNode() != null )
					{						
						PropertyPanel current = (PropertyPanel)getEditorFrame(getInternalEditorFrames()[i].getOwnerNode()).getContentPane();

						//handle the GUI change in a seperate location
						new DBChangeGUIHandler( current, txtMsg ).handleGUIChange( msg ); 

					}
				}

			} //end of synchronize block

			

		//tell our tree we may need to change the display
		updateTreePanel( liteBase, msg.getTypeOfChange() );


		//display a messge on the message panel telling us about this event
		fireMessage( new MessageEvent( DatabaseEditor.this, 
			txtMsg.toString(), MessageEvent.INFORMATION_MESSAGE) );
	}
	else
		com.cannontech.clientutils.CTILogger.info("DBChange Message received that originated from ourself, doing nothing.");

}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 3:31:51 PM)
 * @param event com.cannontech.clientutils.commonutils.GenericEvent
 */
public void handlePopUpEvent(com.cannontech.clientutils.commonutils.GenericEvent event) 
{
	if( event.getSource() == DatabaseEditor.this.getTreeNodePopupMenu() )
	{
		switch( event.getEventId() )
		{
			case DBEditorTreePopUpMenu.DELETE_TREENODE:
			executeDeleteButton_ActionPerformed( new ActionEvent(this, DBEditorTreePopUpMenu.DELETE_TREENODE, "delete") );
			break;

			case DBEditorTreePopUpMenu.EDIT_TREENODE:
			executeEditButton_ActionPerformed( new ActionEvent(this, DBEditorTreePopUpMenu.EDIT_TREENODE, "edit") );
			break;

			case DBEditorTreePopUpMenu.COPY_TREENODE:
			executeCopyButton_ActionPerformed( new ActionEvent(this, DBEditorTreePopUpMenu.COPY_TREENODE, "copy") );
			break;

			case DBEditorTreePopUpMenu.CHANGE_TYPE_TREENODE:
			executeChangeTypeButton_ActionPerformed(new ActionEvent(this, DBEditorTreePopUpMenu.CHANGE_TYPE_TREENODE, "change type"));
			break;

			case DBEditorTreePopUpMenu.SORT_BY_NAME:
			executeSortByNameButton_ActionPerformed(new ActionEvent(this, DBEditorTreePopUpMenu.SORT_BY_NAME, "sort by name"));
			break;
			
			case DBEditorTreePopUpMenu.SORT_BY_OFFSET:
			executeSortByOffsetButton_ActionPerformed(new ActionEvent(this,DBEditorTreePopUpMenu.SORT_BY_OFFSET, "sort by offset"));
			break;
			/*case DBEditorTreePopUpMenu.ENABLEDISABLE_SCHEDULE:
			executeEnableDisableButton_ActionPerformed( new ActionEvent(this, DBEditorTreePopUpMenu.ENABLEDISABLE_SCHEDULE, "enableDisable") );
			break;

			case DBEditorTreePopUpMenu.UPDATE_SCHEDULE:
			getConnection().sendRetrieveOneSchedule( getSelectedSchedule().getId() );
			break;*/

			default:
			throw new RuntimeException("Unknown eventId received from " + event.getSource().getClass().getName() + ", id = " + event.getEventId() );
		}
	}
	
		
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 2:31:09 PM)
 */
private void initConnections() 
{
	// add the listeners for our popUp menu
	java.awt.event.MouseListener listener = new com.cannontech.clientutils.popup.PopUpMenuShower( getTreeNodePopupMenu() );
	getTree().addMouseListener( listener );
	getTreeNodePopupMenu().addPopupMenuListener( this );
	getTreeNodePopupMenu().addPopUpEventListener( this );


	//tell the cache we want to listen for DBChangeMessages
	com.cannontech.database.cache.DefaultDatabaseCache.getInstance().addDBChangeListener(this);

	
	// add the mouselistener for the JTree
	MouseListener ml = new MouseAdapter()
	{
		public void mousePressed(MouseEvent e) 
		{
			int selRow = getTree().getRowForLocation(e.getX(), e.getY());
			
			if(selRow != -1) 
			{
				//be sure this is not a multi selection attempt
				if( !e.isShiftDown() && !e.isControlDown() 
				    && getTree().getSelectionCount() <= 1 )
				{
					getTree().setSelectionRow( selRow );
				}

				if(e.getClickCount() == 1) 
				{
					//com.cannontech.clientutils.CTILogger.info( "---Tree single ---");					
				}
				else if(e.getClickCount() == 2) 
				{
					executeEditButton_ActionPerformed( new ActionEvent(e.getSource(), e.getID(), "MouseDBLClicked") );
				}
			}
			
		}
	};

	getTree().addMouseListener(ml);

}
/**
 * This method was created in VisualAge.
 * @param rootPane JRootPane
 */
private void initialize(JRootPane rootPane) 
{

	rootPane.setContentPane( getContentPane() );

	//make this call so it creates the minimal amount of InternalEditors
	getInternalEditorFrames();

	setDatabase( DatabaseTypes.CORE_DB );  //time hog for large DB's!!!!

	//Just make sure its instantiated
	addMessageListener( getFileMessageLog() );
	owner = com.cannontech.common.util.CtiUtilities.getParentFrame(rootPane);

	//get all the config values read in
	readConfigParameters();

	//connect to dispatch
	getConnToDispatch();


	initConnections();
}

/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 3:08:19 PM)
 * @return boolean
 * @param node javax.swing.tree.DefaultMutableTreeNode
 */
private boolean isEditorAlreadyShowing(DefaultMutableTreeNode node) 
{
	return ( getEditorFrame(node) != null );	
}

/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String[] args) {

	try
	{
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
      System.setProperty("cti.app.name", "DBEditor");

		javax.swing.JFrame f = new javax.swing.JFrame("Yukon Database Editor [Not Connected to Dispatch]");
		f.setDefaultCloseOperation( f.DO_NOTHING_ON_CLOSE );
		SplashWindow splash = new SplashWindow(
			f,
			"ctismall.gif",
			"Loading " + System.getProperty("cti.app.name") + "...",
			new Font("dialog", Font.BOLD, 14 ), Color.black, Color.blue, 2 );
		

		
		//Set the width and height 85% of max
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();	
		f.setSize( (int) (d.width * .85), (int)( d.height * .85) );
	
      //set the location of the frame to the center of the screen
      f.setLocation( (java.awt.Toolkit.getDefaultToolkit().getScreenSize().width - f.getSize().width) / 2,
                     (java.awt.Toolkit.getDefaultToolkit().getScreenSize().height - f.getSize().height) / 2);

      f.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
			
	  ClientSession session = ClientSession.getInstance(); 
	  if(!session.establishSession(f)){
		  System.exit(-1);			
	  }
	  	
	  if(session == null) 
	  {
		  System.exit(-1);
	  }
	
	  if(!session.checkRole(DBEditorRole.ROLEID)) {
	  	JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
		System.exit(-1);				
	  }
			  
	  if(session.getUser().getUserID() == com.cannontech.user.UserUtils.USER_ADMIN_ID)
	  	isSuperuser = true;	
      /* Cache loads as needed, do not load it all here!! --RWN */
		//com.cannontech.database.cache.DefaultDatabaseCache.getInstance().loadAllCache();
	
		DatabaseEditor editor = new DatabaseEditor();
		f.addWindowListener(editor);
		
		editor.displayDatabaseEditor( f.getRootPane() );
		f.show();
		
	}
	catch( Throwable t )
	{
		t.printStackTrace( System.err );
		System.exit(-1);		
	}

}

/**
 * This method was created in VisualAge.
 * @param event java.awt.event.MouseEvent
 */
public void mouseClicked(MouseEvent event)
{

	//If there was a double click open a new edit window
	if( event.getSource() == DatabaseEditor.this.getTree() && event.getClickCount() == 2 )
	{
		/*if( event.isShiftDown() )
			showDebugInfo();
		else*/
		executeEditButton_ActionPerformed( new ActionEvent(event.getSource(), event.getID(), "MouseDBLClicked") );
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 2:39:23 PM)
 * @param event javax.swing.event.PopupMenuEvent
 */
public void popupMenuCanceled(PopupMenuEvent event) {}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 2:39:23 PM)
 * @param event javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeInvisible(PopupMenuEvent event) {}
/**
 * Insert the method's description here.
 * Creation date: (3/14/2001 2:39:23 PM)
 * @param event javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeVisible(PopupMenuEvent event) 
{
	if( event.getSource() == DatabaseEditor.this.getTreeNodePopupMenu() )
	{
		//defaults value here
      getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(true);
      getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(true);
      getTreeNodePopupMenu().getJMenuItemDelete().setEnabled(true);
      getTreeNodePopupMenu().getJMenuItemEdit().setEnabled(true);
      getTreeNodePopupMenu().getJMenuSortAllPointsBy().setEnabled(false);
		
		
		//check for multi select
		if( getTreeViewPanel().getSelectedNodes().length > 1 )
		{
	      getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
	      getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
	      getTreeNodePopupMenu().getJMenuItemDelete().setEnabled(true);
	      getTreeNodePopupMenu().getJMenuItemEdit().setEnabled(true);
	      getTreeNodePopupMenu().getJMenuSortAllPointsBy().setEnabled(false);			
		}
		else
		{
	      DefaultMutableTreeNode selectedNode = getTreeViewPanel().getSelectedNode();
	
	
	      if (selectedNode != null)
	      {
	         if (selectedNode instanceof DummyTreeNode || selectedNode.isRoot())
	         {
	            getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
	            getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
	            getTreeNodePopupMenu().getJMenuItemDelete().setEnabled(false);
	            getTreeNodePopupMenu().getJMenuItemEdit().setEnabled(false);
	   
	            if (!selectedNode.isRoot())
	               getTreeNodePopupMenu().getJMenuSortAllPointsBy().setEnabled(true);
	         }
	         
	         if (selectedNode.getUserObject() instanceof com.cannontech.database.data.lite.LiteYukonPAObject)
	         {
	            com.cannontech.database.data.lite.LiteYukonPAObject litYuk =
	                  (com.cannontech.database.data.lite.LiteYukonPAObject)selectedNode.getUserObject();
	            
	            if( litYuk.getPaoClass() == com.cannontech.database.data.pao.PAOGroups.CLASS_CAPCONTROL 
	                || litYuk.getType() == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_RIPPLE
	                || litYuk.getType() == com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP
	                || litYuk.getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.LOADMANAGEMENT )
	            {
	               getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);               
	   
	            }
	            else if ( litYuk.getCategory() == com.cannontech.database.data.pao.PAOGroups.CAT_CUSTOMER
	                       || litYuk.getPaoClass() == com.cannontech.database.data.pao.DeviceClasses.SYSTEM)
	            {
	               getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
	               getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
	            }
	
	         }
	         else if (
	            selectedNode.getUserObject() instanceof com.cannontech.database.data.lite.LiteNotificationGroup
	               || selectedNode.getUserObject() instanceof com.cannontech.database.data.lite.LiteContactNotification
	               || selectedNode.getUserObject() instanceof com.cannontech.database.data.lite.LiteAlarmCategory
	               || (selectedNode.getUserObject() instanceof com.cannontech.database.data.lite.LiteYukonPAObject
	                   && ((com.cannontech.database.data.lite.LiteYukonPAObject)selectedNode.getUserObject()).getType()
	                        == com.cannontech.database.data.pao.CapControlTypes.CAP_CONTROL_SUBBUS) )
	         {
	            getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
	            getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
	         }
	      
	         
	   
	      }
		}      
      
	}

}
/**
 * This method was created in VisualAge.
 * @param rootPane JRootPane
 */
private void readConfigParameters() 
{	

	try
	{	 			
		decimalPlaces = (new Integer(
		ClientSession.getInstance().getRolePropertyValue(
				TDCRole.DECIMAL_PLACES,
				"3")) ).intValue();
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		decimalPlaces = 3;
	}

	//Decide whether to put the billing file option into the create (core) menu

	try
	{
		activateBilling = 
			ClientSession.getInstance().getRolePropertyValue(
				BillingRole.WIZ_ACTIVATE, "FALSE").trim().equalsIgnoreCase("TRUE");
	}

	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
		
	if( !activateBilling )
		coreCreateMenu.remove( coreCreateMenu.billingGroupMenuItem );
	
	//Decide which items to put into the view menu
	boolean showCore = false;
	boolean showLm = false;
	boolean showCapControl = false;
	boolean showSystem = false;
	
	try
	{
		showCore = ClientSession.getInstance().getRolePropertyValue(
			DBEditorRole.DBEDITOR_CORE, "FALSE").trim().equalsIgnoreCase("TRUE");
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	try
	{
		showLm = ClientSession.getInstance().getRolePropertyValue(
			DBEditorRole.DBEDITOR_LM, "FALSE").trim().equalsIgnoreCase("TRUE");
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	try
	{
		showCapControl = ClientSession.getInstance().getRolePropertyValue(
			DBEditorRole.DBEDITOR_CAP_CONTROL, "FALSE").trim().equalsIgnoreCase("TRUE");
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	try
	{
		showSystem = ClientSession.getInstance().getRolePropertyValue(
			DBEditorRole.DBEDITOR_SYSTEM, "FALSE").trim().equalsIgnoreCase("TRUE");
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	//check whether the user should be able to see anything regarding logins in the dbeditor
	try
	{
		accessOfLoginNotAllowed = ClientSession.getInstance().getRolePropertyValue(
			DBEditorRole.PERMIT_LOGIN_EDIT, "TRUE").trim().equalsIgnoreCase("FALSE");
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	//shouldn't be allowed to see login stuff
	if( accessOfLoginNotAllowed && !isSuperuser)
	{
		systemCreateMenu.remove( systemCreateMenu.loginGrpMenuItem );
		systemCreateMenu.remove( systemCreateMenu.loginMenuItem );
	}
	
	if( !showCore )
		viewMenu.remove( viewMenu.coreRadioButtonMenuItem );

	if( !showLm )
		viewMenu.remove( viewMenu.lmRadioButtonMenuItem );
	
	if( !showCapControl )
		viewMenu.remove( viewMenu.capControlRadioButtonMenuItem );

	if( !showSystem )
		viewMenu.remove( viewMenu.systemRadioButtonMenuItem );
		

}
/**
 * This method was created in VisualAge.
 * @param listener com.cannontech.common.util.MessageEventListener
 */
public void removeMessageListeners(MessageEventListener listener) {

	if( messageListeners.contains(listener) )
		messageListeners.removeElement(listener);
}
/**
 * Insert the method's description here.
 * Creation date: (3/9/2001 4:07:38 PM)
 */
private void removeUnneededEditorFrames() 
{
	synchronized( getInternalEditorFrames() )
	{
		for( int i = 0; i < getInternalEditorFrames().length; i++ )
		{
			if( getInternalEditorFrames().length > INITIAL_EDITOR_COUNT )
			{
				if( ((JTreeEditorFrame)getInternalEditorFrames()[i]).isVisible() )
					continue;
				else
				{
					java.util.ArrayList list = new java.util.ArrayList(getInternalEditorFrames().length);
					for( int j = 0; j < getInternalEditorFrames().length; j++ )
						list.add( getInternalEditorFrames()[j] );

					// we removed the unneeded frame
					list.remove(i);
					
					// remove the excess frame
					JTreeEditorFrame[] frames = new JTreeEditorFrame[getInternalEditorFrames().length - 1];

					editorFrames = (JTreeEditorFrame[])list.toArray(frames);
				}
			}
			else
				return;
		}
	}
	
}
/**
 * This method was created in VisualAge.
 * @param event com.cannontech.common.editor.PropertyPanelEvent
 */
public void selectionPerformed( PropertyPanelEvent event)
{
	try 
	{
		
		
	//these events tells us the user modified a DB object before clicking the OK/APPLY button
	if( event.getID() == PropertyPanelEvent.EVENT_DB_INSERT )
	{
		DBPersistent dbPersist = (DBPersistent)event.getDataChanged();
		insertDBPersistent( dbPersist );
		return;		
	}
	else if( event.getID() == PropertyPanelEvent.EVENT_DB_DELETE )
	{
		DBPersistent dbPersist = (DBPersistent)event.getDataChanged();
		deleteDBPersistent( dbPersist );
		return;		
	}
	else if( event.getID() == PropertyPanelEvent.EVENT_DB_UPDATE )
	{
		DBPersistent dbPersist = (DBPersistent)event.getDataChanged();
		updateDBPersistent( dbPersist );
		return;		
	}


	if( !( event.getSource() instanceof PropertyPanel) )
		return;
	
	PropertyPanel panel = (PropertyPanel) event.getSource();
	int frameLocation = getFrameLocationByPanel(panel);
	
	// if the input entered is not legit and the user wants to commit the changes
	if( (event.getID() == PropertyPanelEvent.APPLY_SELECTION ||
		  event.getID() == PropertyPanelEvent.OK_SELECTION) && !panel.isInputValid() )
	{
		if( panel.getErrorString() != null )
			javax.swing.JOptionPane.showMessageDialog( panel, 
						panel.getErrorString(), 
						"Input Error", 
						javax.swing.JOptionPane.WARNING_MESSAGE );
	
		return;
	}

	//result of our DB update
	boolean updateResult = false;
	
	//Update the object on an apply or ok
	if( event.getID() == PropertyPanelEvent.APPLY_SELECTION ||
		 event.getID() == PropertyPanelEvent.OK_SELECTION		)
	{
		com.cannontech.database.db.DBPersistent object = (com.cannontech.database.db.DBPersistent) panel.getValue(null);
		
		if( panel.hasChanged() )
		{
			panel.setChanged(false);
			updateResult = updateDBPersistent(object);

			if( updateResult )
			{					
				//getTreeViewPanel().refresh();
				//getTreeViewPanel().selectObject(object);

				if( event.getID() == PropertyPanelEvent.APPLY_SELECTION )
				{
					/* APPLY ONLY EVENTS GO HERE */
					getInternalEditorFrames()[frameLocation].setTitle( object.toString() + " : " + panel.toString() );
				}

			}
			else
			{
				//try and restore the original values to the Editor Panels
				panel.setValue( panel.getOriginalObjectToEdit() );
				panel.repaint();
				return; //the SQLUpdate failed, lets get out of here
			}
			
		}
			
	}
 
	// Remove the frame here	
	if( event.getID() == PropertyPanelEvent.CANCEL_SELECTION
		 || event.getID() == PropertyPanelEvent.OK_SELECTION )
	{
		if( event.getID() == PropertyPanelEvent.CANCEL_SELECTION )
		{
			/* CANCEL ONLY EVENTS GO HERE */
		}
		
		panel.setChanged(false);
		getInternalEditorFrames()[frameLocation].setVisible(false); //.dispose() ?? not sure!!???
		if( frameLocation >= INITIAL_EDITOR_COUNT )
			removeUnneededEditorFrames();
		
		desktopPane.repaint();
	}

}
finally{
	//just in case someone is waiting on this event
	//this.notifyAll();
	}

}

public boolean insertDBPersistent( DBPersistent newItem )
// throws TransactionException
{
	boolean success = false;
	
	try
	{
		//insert the newly created item into the DB
		Transaction t = Transaction.createTransaction(Transaction.INSERT, newItem);
		newItem = t.execute();
	
		String messageString = newItem + " inserted successfully into the database.";
		fireMessage(new MessageEvent(this, messageString));
	
		//fire DBChange messages out to Dispatch
		generateDBChangeMsg( newItem, DBChangeMsg.CHANGE_TYPE_ADD );
		
		success = true;
	}
	catch( com.cannontech.common.wizard.CancelInsertException ci )
	{
		//inside the getValue(), this exception was thrown
	}
	catch (com.cannontech.database.TransactionException e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );

		String messageString =
			"Error Inserting " + newItem + " into the database.  Error received:  " + e.getMessage().trim();
		fireMessage(new MessageEvent(this, messageString, MessageEvent.ERROR_MESSAGE));
	}

	return success;
}

/**
 * This method was created in VisualAge.
 * @param event com.cannontech.common.wizard.WizardPanelEvent
 */
public void selectionPerformed(WizardPanelEvent event)
{


	//these event tells us the user modified a DB object before clicking the finish button
	if( event.getID() == PropertyPanelEvent.EVENT_DB_INSERT )
	{
		DBPersistent dbPersist = (DBPersistent)event.getDataChanged();
		insertDBPersistent( dbPersist );
		return;
	}
	else if( event.getID() == PropertyPanelEvent.EVENT_DB_DELETE )
	{
		DBPersistent dbPersist = (DBPersistent)event.getDataChanged();
		deleteDBPersistent( dbPersist );
		return;		
	}
	else if( event.getID() == PropertyPanelEvent.EVENT_DB_UPDATE )
	{
		DBPersistent dbPersist = (DBPersistent)event.getDataChanged();
		updateDBPersistent( dbPersist );
		return;		
	}
	

	if( !( event.getSource() instanceof WizardPanel) )
		return;

	boolean objTypeChange = false;
	boolean successfullInsertion = false;

	if (event.getID() == WizardPanelEvent.FINISH_SELECTION)
	{
		if( changingObjectType )
		{
			objTypeChange = executeChangeObjectType( event );
		}
		else
		{		
			WizardPanel p = (WizardPanel) event.getSource();

			//get the newly created item from the wizard panel
			com.cannontech.database.db.DBPersistent newItem = null;

			copyingObject = false;

			//p.getValue(null) may throw a CancelInsertException
			newItem = (com.cannontech.database.db.DBPersistent) p.getValue(null);

			//try to insert tih object into the DB
			successfullInsertion = insertDBPersistent( newItem );
	

			//tell our current tree model to update itself so it can display the newly added item
			//getTreeViewPanel().refresh();

			//Bring the editor up for the newly created Object
			if (successfullInsertion)
			{
				getTreeViewPanel().selectObject(newItem);
			}

		}
	}

	
	if( event.getID() == WizardPanelEvent.CANCEL_SELECTION
		 || (event.getID() == WizardPanelEvent.FINISH_SELECTION && successfullInsertion)
		 || (event.getID() == WizardPanelEvent.FINISH_SELECTION)
		 && objTypeChange )
	{
		changingObjectType = false;
		copyingObject = false;

		//Loop through all the frames on the desktopPane
		//and find out which one contains the WizardPanel
		//responsible for this event
		WizardPanel p = (WizardPanel) event.getSource();

		JInternalFrame frames[] = this.desktopPane.getAllFrames();

		for (int i = 0; i < frames.length; i++)
		{
			if (frames[i].getContentPane() == p)
			{
				//Found a panel so kill the frame
				this.desktopPane.remove(frames[i]);
				frames[i].dispose();
				this.desktopPane.repaint();
				break;
			}
		}
	}
	

	if(successfullInsertion || objTypeChange) {
		 showEditorSelectedObject();
	}

}
/**
 * This method was created in VisualAge.
 * @param whichDatabase int
 */
public void setDatabase(int whichDatabase) 
{
	//First check if there might be changes to update
	//then remove any current editors that are opened
	exitConfirm();

	
	Integer[] models = null;

	//Get a ref to the rootpane
	JRootPane rPane = ((JFrame) com.cannontech.common.util.CtiUtilities.getParentFrame( getContentPane() )).getRootPane();
	
	//this.menuBar = null;
	
	switch( whichDatabase )
	{
		case DatabaseTypes.CORE_DB:
				this.menuBar = getMenuBar(whichDatabase);
				viewMenu.coreRadioButtonMenuItem.setSelected(true);
				models = CORE_MODELS;
				break;
		case DatabaseTypes.LM_DB:
				this.menuBar = getMenuBar(whichDatabase);
				viewMenu.lmRadioButtonMenuItem.setSelected(true);
				models = LM_MODELS;
				break;		
		case DatabaseTypes.CAP_CONTROL_DB:
				this.menuBar = getMenuBar(whichDatabase);
				viewMenu.capControlRadioButtonMenuItem.setSelected(true);
				models = CAP_CONTROL_MODELS;
				break;
		case DatabaseTypes.SYSTEM_DB:
				this.menuBar = getMenuBar(whichDatabase);
				viewMenu.systemRadioButtonMenuItem.setSelected(true);
				
				//check to see if user is allowed to see login stuff information at all
				if( accessOfLoginNotAllowed && !isSuperuser)
				{
					models = NONLOGIN_SYSTEM_MODELS;
					break;
				}
				
				models = SYSTEM_MODELS;
				break;

	}	
	
	if( models == null )
	{
		System.err.println("com.cannontech.dbeditor.DatabaseEditor:  Unable to switch to database " + whichDatabase );
	}

	DBTreeModel[] newModels = new DBTreeModel[models.length];
	for( int i = 0; i < newModels.length; i++ )
	{
		newModels[i] = ModelFactory.create( models[i].intValue() );
	}
	
	getTreeViewPanel().setTreeModels(newModels);
	if( models == CORE_MODELS )
		getTreeViewPanel().setSelectedSortByIndex( 1 ); //device is the default
	if( models == LM_MODELS )
		getTreeViewPanel().setSelectedSortByIndex( 5 ); //"load groups" is the default
	if( models == SYSTEM_MODELS || models == NONLOGIN_SYSTEM_MODELS )
		getTreeViewPanel().setSelectedSortByIndex( 3 ); //"contacts" is the default
	
	rPane.setJMenuBar( this.menuBar );
	rPane.revalidate();
	rPane.repaint();
}
/**
 * This method was created in VisualAge.
 * @param wizard com.cannontech.common.wizard.WizardPanel
 */
private void showChangeTypeWizardPanel(WizardPanel wizard) {

	//Set the cursor to wait
	java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this.desktopPane);
	java.awt.Cursor savedCursor = owner.getCursor();
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	wizard.addWizardPanelListener(this);
	
	javax.swing.JInternalFrame f = new javax.swing.JInternalFrame();
	
	f.setContentPane( wizard );
	f.setSize( (int)wizard.getActualSize().getWidth(),
		  		  (int)wizard.getActualSize().getHeight());//410,470);

	this.desktopPane.add( f );
	changingObjectType = true;
	wizard.setValue(null);
	f.setVisible(true);
	ImageIcon wizardIcon = new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	f.setFrameIcon(wizardIcon);
	
	try
	{
		f.setSelected(true);
	}
	catch(java.beans.PropertyVetoException e )
	{
		//?
	}
	finally
	{
		owner.setCursor(savedCursor);
	}
	
}
/**
 * This method was created in VisualAge.
 * @param wizard com.cannontech.common.wizard.WizardPanel
 */
private void showCopyWizardPanel(WizardPanel wizard) {

	//Set the cursor to wait
	java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this.desktopPane);
	java.awt.Cursor savedCursor = owner.getCursor();
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	wizard.addWizardPanelListener(this);
	
	JInternalFrame f = new JInternalFrame();
	
	f.setContentPane( wizard );
	f.setSize(435, 500);
	f.setResizable(true);

	this.desktopPane.add( f );

	//a DBPersistent must be created from the Lite object so you can copy it
	com.cannontech.database.db.DBPersistent userObject = com.cannontech.database.data.lite.LiteFactory.createDBPersistent((com.cannontech.database.data.lite.LiteBase)getTreeViewPanel().getSelectedNode().getUserObject());
	try
	{
		Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, userObject);
		userObject = t.execute();
	}
	catch( Exception e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}

	
	wizard.setValue(userObject);
	copyingObject = true;
	ImageIcon wizardIcon = new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	f.setFrameIcon(wizardIcon);
	f.show();
	
	try
	{
		f.setSelected(true);
	}
	catch(java.beans.PropertyVetoException e )
	{
		//?
	}
	finally
	{
		owner.setCursor(savedCursor);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/26/2001 3:45:30 PM)
 */
public void showEditorSelectedObject()
{
	java.awt.Frame f = com.cannontech.common.util.CtiUtilities.getParentFrame(this.desktopPane);
	f.validate();

	try
	{
		 
		executeEditButton_ActionPerformed(new ActionEvent(this, DBEditorTreePopUpMenu.EDIT_TREENODE, "edit"));
	}
	catch (Exception e)
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	finally
	{
		f.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
	}

}
/**
 * This method was created in VisualAge.
 * @param wizard com.cannontech.common.wizard.WizardPanel
 */
private void showWizardPanel(WizardPanel wizard) {

	//Set the cursor to wait
	java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this.desktopPane);
	java.awt.Cursor savedCursor = owner.getCursor();
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	wizard.addWizardPanelListener(this);

	
	javax.swing.JInternalFrame f = new javax.swing.JInternalFrame();
	
	f.setResizable( true );
	f.setContentPane( wizard );
	f.setSize( (int)wizard.getActualSize().getWidth(),
		  		  (int)wizard.getActualSize().getHeight());//410,470);

	this.desktopPane.add( f );

	wizard.setValue(null);
	f.setVisible(true);
	ImageIcon wizardIcon = new ImageIcon(java.awt.Toolkit.getDefaultToolkit().getImage("dbEditorIcon.gif"));
	f.setFrameIcon(wizardIcon);
	
	try
	{
		f.setSelected(true);
	}
	catch(java.beans.PropertyVetoException e )
	{
		//?
	}
	finally
	{
		owner.setCursor(savedCursor);
	}
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/00 4:52:57 PM)
 * @param o java.util.Observable
 * @param arg java.lang.Object
 */
public void update(java.util.Observable o, Object arg) 
{
	if( o instanceof com.cannontech.message.dispatch.ClientConnection )
	{
		if( ((com.cannontech.message.dispatch.ClientConnection)o).isValid() )
		{
			connToVanGoghErrorMessageSent = false;
			fireMessage( new MessageEvent( this, "Connection to Message Dispatcher Established.") );
			if( owner != null )
				owner.setTitle("Yukon Database Editor [Connected to Dispatch@" +
					 		 ((com.cannontech.message.dispatch.ClientConnection)o).getHost() + ":" +
					 		 ((com.cannontech.message.dispatch.ClientConnection)o).getPort() + "]" );
		}
		else
		{
			if( owner != null )
				owner.setTitle("Yukon Database Editor [Not Connected to Dispatch]");
			if( !connToVanGoghErrorMessageSent )
			{
				connToVanGoghErrorMessageSent = true;
				fireMessage( new MessageEvent( this, "Lost Connection to Message Dispatcher!", MessageEvent.ERROR_MESSAGE) );
				owner.repaint();
			}
		}
	}
}
/**
 * This method was created in VisualAge.
 * @param object DBPersistent
 */
 /* Returns of the DB transaction executed successfully, else returns false. */
private boolean updateDBPersistent(com.cannontech.database.db.DBPersistent object) 
{

	try
	{
		Transaction t = Transaction.createTransaction( Transaction.UPDATE, object );
		object = t.execute();

		//write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
		generateDBChangeMsg( object, DBChangeMsg.CHANGE_TYPE_UPDATE );
		
		String messageString = object + " updated successfully in the database.";
		fireMessage( new MessageEvent( this, messageString) );
	}
	catch( com.cannontech.database.TransactionException e )
	{
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		String messageString = " Error updating " + object + " in the database.  Error received: " + e.getMessage() ;
		fireMessage( new MessageEvent( this, messageString, MessageEvent.ERROR_MESSAGE) );
		return false;
	}

	return true;
}

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 11:29:42 AM)
 * @param lBase com.cannontech.database.data.lite.LiteBase
 * @param changeType int
 */
private void updateTreePanel(com.cannontech.database.data.lite.LiteBase lBase, int changeType) 
{
	if( changeType == DBChangeMsg.CHANGE_TYPE_ADD )
	{
		getTreeViewPanel().treeObjectInsert( lBase );
	}
	else if( changeType == DBChangeMsg.CHANGE_TYPE_DELETE )
	{
		getTreeViewPanel().treeObjectDelete( lBase );
	}
	else if( changeType == DBChangeMsg.CHANGE_TYPE_UPDATE )
	{
		getTreeViewPanel().treeObjectUpdated( lBase );
	}
	else
		throw new IllegalArgumentException("Unrecognized CHANGE_TYPE for " +
				DBChangeMsg.class.getName() +
				", CHANGE_TYPE received = " + changeType );
	
   
   //have the try 'ask' for Focus
   getTreeViewPanel().getTree().requestFocus();
}

/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowActivated(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowClosed(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowClosing(WindowEvent event) 
{
	if( exitConfirm() )
		exit();
}


/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowDeactivated(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowDeiconified(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowIconified(WindowEvent event) {
}
/**
 * This method was created by Cannon Technologies Inc.
 * @param event java.awt.event.WindowEvent
 */
public void windowOpened(WindowEvent event) {
}
}

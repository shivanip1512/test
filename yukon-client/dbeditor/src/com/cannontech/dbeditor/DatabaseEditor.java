package com.cannontech.dbeditor;

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
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.editor.PropertyPanel;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.MessagePanel;
import com.cannontech.common.login.ClientSession;
import com.cannontech.common.login.ClientStartupHelper;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.service.PaoDefinitionService;
import com.cannontech.common.util.ClientRights;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.LoggerEventListener;
import com.cannontech.common.util.MessageEvent;
import com.cannontech.common.util.MessageEventListener;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.common.wizard.WizardPanelEvent;
import com.cannontech.core.dao.DBDeleteResult;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.database.DatabaseTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceBase;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.devicemetergroup.DeviceMeterGroupBase;
import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.data.point.PointBase;
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
import com.cannontech.dbeditor.wizard.changetype.device.DeviceChangeTypeWizardPanel;
import com.cannontech.dbeditor.wizard.copy.device.DeviceCopyWizardPanel;
import com.cannontech.dbeditor.wizard.tou.TOUScheduleWizardPanel;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.message.dispatch.ClientConnection;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.roles.application.DBEditorRole;
import com.cannontech.roles.application.TDCRole;
import com.cannontech.roles.yukon.BillingRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IServerConnection;
import com.cannontech.yukon.conns.ConnPool;


public class DatabaseEditor
	implements
		com.cannontech.common.editor.PropertyPanelListener,
		com.cannontech.common.wizard.WizardPanelListener,
		ActionListener,
		WindowListener,
		java.util.Observer,
		com.cannontech.clientutils.popup.PopUpEventListener,
		javax.swing.event.PopupMenuListener,
		com.cannontech.database.cache.DBChangeLiteListener 
{
   //all editor frame sizes
   public static final Dimension EDITOR_FRAME_SIZE = new Dimension(435, 600);
   
   public static final URL DBEDITOR_IMG_16 = DatabaseEditor.class.getResource("/DatabaseEditor16.gif");
   public static final URL DBEDITOR_IMG_24 = DatabaseEditor.class.getResource("/DatabaseEditor24.gif");
   public static final URL DBEDITOR_IMG_32 = DatabaseEditor.class.getResource("/DatabaseEditor32.gif");
   public static final URL DBEDITOR_IMG_48 = DatabaseEditor.class.getResource("/DatabaseEditor48.gif");
   public static final URL DBEDITOR_IMG_64 = DatabaseEditor.class.getResource("/DatabaseEditor64.gif");
   
   private static final Logger log = YukonLogManager.getLogger(DatabaseEditor.class);
   
   public static List<Image> getIconsImages() {
       
       List<Image> iconsImages = new ArrayList<Image>();
       iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_16));
       iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_24));
       iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_32));
       iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_48));
       iconsImages.add(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_64));
       
       return iconsImages;
   }
   
	//gui elements of the app
	private DBEditorTreePopUpMenu treeNodePopUpMenu = null;

	private JMenuBar menuBar;
	private JDesktopPane desktopPane;
	private JScrollPane deskTopFrameScrollPane;
	private MessagePanel messagePanel;
	private com.cannontech.common.gui.util.TreeViewPanel treeViewPanel;
	private JPanel contentPane;
	private JSplitPane splitPane;
	private FileMenu fileMenu;
	private EditMenu editMenu;
	private CoreCreateMenu coreCreateMenu;
	private LMCreateMenu lmCreateMenu;
	private SystemCreateMenu systemCreateMenu;
	private ViewMenu viewMenu;
	private HelpMenu helpMenu;
	private java.awt.Frame owner = null;
	//File logger
    private LoggerEventListener loggerEventListener;

	//Allow editor frames at a time
	private JTreeEditorFrame[] editorFrames = null;
	private static final int INITIAL_EDITOR_COUNT = 4;

	private int currentDatabase = DatabaseTypes.CORE_DB;
	//Map of database types and treemodels to use
	//Keep and add to these in alphabetical order	
	
	private static final TreeModelEnum[] CORE_MODELS =
		{
			TreeModelEnum.PORT,
			TreeModelEnum.DEVICE,
			TreeModelEnum.IED,
			TreeModelEnum.MCT,
         	TreeModelEnum.MCTBROADCAST,
			TreeModelEnum.TWOWAYCONFIG,         
			TreeModelEnum.METER,
			TreeModelEnum.DEVICE_METERNUMBER,
			TreeModelEnum.ROUTE,
			TreeModelEnum.RTU,
			TreeModelEnum.STATEGROUP,
			TreeModelEnum.TRANSMITTER,
						
		};
	private static final TreeModelEnum[] LM_MODELS =
		{
			TreeModelEnum.LMCONSTRAINT,
			TreeModelEnum.LMCONTROLAREA,
			TreeModelEnum.LMGROUPDIGISEP,
			TreeModelEnum.LMGROUPEMETCON,
			TreeModelEnum.LMGROUPEXPRESSCOM,
			TreeModelEnum.GOLAY,
			TreeModelEnum.LMGROUPS,
			TreeModelEnum.LMPROGRAM,
			TreeModelEnum.LMGROUPMACRO,
			TreeModelEnum.LMSCENARIO,
			TreeModelEnum.LMGROUPVERSACOM,
		};	
		
	private static final TreeModelEnum[] LM_MODELS_WITH_SA =
	{
		TreeModelEnum.LMCONSTRAINT,
		TreeModelEnum.LMCONTROLAREA,
		TreeModelEnum.LMGROUPDIGISEP,
		TreeModelEnum.LMGROUPEMETCON,
		TreeModelEnum.LMGROUPEXPRESSCOM,
		TreeModelEnum.GOLAY,
		TreeModelEnum.LMGROUPS,
		TreeModelEnum.LMPROGRAM,
		TreeModelEnum.LMGROUPMACRO,
		TreeModelEnum.LMGROUPSA305,
		TreeModelEnum.LMGROUPSA205,
		TreeModelEnum.LMGROUPSADIGITAL,
		TreeModelEnum.LMSCENARIO,
		TreeModelEnum.LMGROUPVERSACOM,
	};
	private static final TreeModelEnum[] SYSTEM_MODELS =
		{
			TreeModelEnum.ALARM_STATES,
			TreeModelEnum.BASELINE,
			TreeModelEnum.CICUSTOMER,
			TreeModelEnum.CONTACT,
			TreeModelEnum.HOLIDAY_SCHEDULE,
			TreeModelEnum.LOGINS,
			TreeModelEnum.LOGIN_GROUPS,
			TreeModelEnum.NOTIFICATION_GROUP,
			TreeModelEnum.SEASON,
			TreeModelEnum.TAG,
			TreeModelEnum.TOUSCHEDULE,
            TreeModelEnum.SYSTEM_DEVICE,
		};
	private static final TreeModelEnum[] NONLOGIN_SYSTEM_MODELS =
		{
			TreeModelEnum.ALARM_STATES,
			TreeModelEnum.BASELINE,
			TreeModelEnum.CICUSTOMER,
			TreeModelEnum.CONTACT,
			TreeModelEnum.HOLIDAY_SCHEDULE,
			TreeModelEnum.NOTIFICATION_GROUP,
			TreeModelEnum.SEASON,
			TreeModelEnum.TAG,
			TreeModelEnum.TOUSCHEDULE,
		};	
	

	private Vector messageListeners = new Vector();

	//During an item state change we need to remember previous selection
	//in order query the user whether we should save or not
	private Object lastSelection = null;
	
	private static int decimalPlaces;
	private IServerConnection connToDispatch;
	private boolean connToVanGoghErrorMessageSent = true;
	private boolean changingObjectType = false;

	//Flag whether billing option should be present in create (core) menu
	private boolean activateBilling;
	private static boolean isSuperuser = false;
	private boolean accessOfLoginNotAllowed = false;
    
    private static DatabaseEditor editor = null;
    
    private PaoDefinitionService paoDefinitionService = (PaoDefinitionService) YukonSpringHook.getBean("paoDefinitionService");
    
/**
 * DatabaseEditor constructor comment.
 */
public DatabaseEditor() {
	super();
    this.editor = this;
}
/**
 * This method was created in VisualAge.
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(ActionEvent event)
{
	if( !( event.getSource() instanceof JMenuItem) ) {
		return;
	}

	JMenuItem item = (JMenuItem) event.getSource();

	if( item == viewMenu.coreRadioButtonMenuItem &&
		currentDatabase != DatabaseTypes.CORE_DB ) {
		java.awt.Frame f = CtiUtilities.getParentFrame(getContentPane());
		java.awt.Cursor savedCursor = f.getCursor();
		try {
			f.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );		
	 
			currentDatabase = DatabaseTypes.CORE_DB;
			setDatabase(currentDatabase);
		} catch(Exception e) {
			log.error( e.getMessage(), e );
		} finally {
			f.setCursor(savedCursor);
		}

		f.repaint();
	} else if( item == viewMenu.lmRadioButtonMenuItem &&
		currentDatabase != DatabaseTypes.LM_DB ) {
		java.awt.Frame f = CtiUtilities.getParentFrame(getContentPane());
		java.awt.Cursor savedCursor = f.getCursor();
		try {
			f.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			
			currentDatabase = DatabaseTypes.LM_DB;
			setDatabase(currentDatabase);
		} catch(Exception e) {
			log.error( e.getMessage(), e );
		} finally {
			f.setCursor(savedCursor);
		}

		f.repaint();
	} else if( item == viewMenu.systemRadioButtonMenuItem &&
		currentDatabase != DatabaseTypes.SYSTEM_DB ) {
		java.awt.Frame f = CtiUtilities.getParentFrame(getContentPane());
		java.awt.Cursor savedCursor = f.getCursor();
		try {
			f.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
			
			currentDatabase = DatabaseTypes.SYSTEM_DB;
			setDatabase(currentDatabase);
		} catch(Exception e) {
			log.error( e.getMessage(), e );
		} finally {
			f.setCursor(savedCursor);
		}

		f.repaint();
	} else if( item == fileMenu.exitMenuItem ) {
		if( exitConfirm() ) {
			exit();
		}
	} else if( item == editMenu.editMenuItem ) {
		executeEditButton_ActionPerformed(event);
	} else  if( item == editMenu.deleteMenuItem ) {
		executeDeleteButton_ActionPerformed(event);
	} else if( item == editMenu.copyMenuItem ) {
		executeCopyButton_ActionPerformed( event );		
	} else if( item == editMenu.changeTypeMenuItem) {
		executeChangeTypeButton_ActionPerformed(event );		
	} else if( item == editMenu.searchMenuItem ) {
		executeFindButton_ActionPerformed( event );
	} else if( item == viewMenu.refreshMenuItem ) {
		viewMenuRefreshAction();
	} else if( item == viewMenu.showMessageLogButton ) {
		getMessagePanel().setVisible( viewMenu.showMessageLogButton.isSelected() );
	} else if( item == helpMenu.aboutMenuItem ) {
		executeAboutButton_ActionPerformed( event );
	} else if( item == helpMenu.helpTopicMenuItem ) {
		//run and show our help program
		CtiUtilities.showHelp( CommonDefines.HELP_FILE );
	} else {
		displayAWizardPanel( item );
	}
	
}
public void viewMenuRefreshAction() {
    java.awt.Frame f = CtiUtilities.getParentFrame(getContentPane());
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
    	    //if we tried to refresh a tree that extends DbBackgroundTreeModel (CICustomer, Login, Contact)
    	    //then we are doing the select after the update and not right here
    	    if (! ( treeViewPanel.getCurrentTreeModel() instanceof DbBackgroundTreeModel ) ) {
    	        treeViewPanel.selectLiteObject((LiteBase)holder);
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
    	log.error( e.getMessage(), e );
    }
    finally
    {
    	f.setCursor(savedCursor);
    	fireMessage( new MessageEvent( this, "Tree view, connection state and cache have been refreshed", MessageEvent.INFORMATION_MESSAGE) );
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
		message.append("\nThis Point has historical data.");

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
	
	ImageIcon editorIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_16));
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
	else if (item == systemCreateMenu.touMenuItem) 
	{
		showWizardPanel(new TOUScheduleWizardPanel());
	}
    else if (item == systemCreateMenu.systemPointMenuItem)
    {
        showWizardPanel(new com.cannontech.dbeditor.wizard.point.PointWizardPanel(new Integer(0)));
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
	com.cannontech.database.data.lite.LiteBase liteObject = (com.cannontech.database.data.lite.LiteBase) node.getUserObject();
	com.cannontech.database.db.DBPersistent selectedObject =
		com.cannontech.database.data.lite.LiteFactory.createDBPersistent(liteObject);

	try
	{
		Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, (com.cannontech.database.db.DBPersistent) selectedObject);
		selectedObject = t.execute();
	}
	catch (Exception e)
	{
		log.error( e.getMessage(), e );
	}
	
	boolean changeType = true;
	int currentType = 0;
	int newType = 0;
	String type = " ";

	if (selectedObject instanceof com.cannontech.database.data.device.DeviceBase)
	{
		type = ((com.cannontech.database.data.device.DeviceBase) selectedObject).getPAOType();
		currentType = com.cannontech.database.data.pao.PAOGroups.getDeviceType(type);
		newType = ((PaoType) p.getValue(null)).getDeviceTypeId();

	} else if (selectedObject instanceof DeviceMeterGroupBase) {
	    DeviceDao deviceDao = (DeviceDao) YukonSpringHook.getBean("deviceDao");
        int deviceId = ((DeviceMeterGroupBase) selectedObject).getDeviceMeterGroup().getDeviceID();
        SimpleDevice device = deviceDao.getYukonDeviceObjectById(deviceId);

        type = device.getDeviceType().getPaoTypeName();
        currentType = device.getType();
        newType = ((PaoType) p.getValue(null)).getDeviceTypeId();
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

	if(com.cannontech.database.data.device.DeviceTypesFuncs.isTransmitter(currentType)
		&& !(selectedObject instanceof com.cannontech.database.data.point.PointBase))
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
								DbChangeType.ADD );
								
				selectedObject = smarty.getOwnerDBPersistent();
			}			

			Transaction t1 = Transaction.createTransaction(Transaction.UPDATE, selectedObject);
			selectedObject = t1.execute();

			//always do this
			generateDBChangeMsg( selectedObject, DbChangeType.UPDATE );
			
			//for some reason, the new 410 points never show up after a ChangeType
			if(com.cannontech.database.data.device.DeviceTypesFuncs.isMCT4XX(newType))
			{
				DBChangeMsg ptChange = new com.cannontech.message.dispatch.message.DBChangeMsg(
					liteObject.getLiteID(),
					DBChangeMsg.CHANGE_POINT_DB,
					DBChangeMsg.CAT_POINT, 
					PointTypes.getType(PointTypes.ACCUMULATOR_DEMAND),
					DbChangeType.ADD );
				getConnToDispatch().queue(ptChange);
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance().releaseAllCache();
				getTreeViewPanel().refresh();
			}

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
			log.error( e.getMessage(), e );

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
		 log.error( e.getMessage(), e );
		 
		 fireMessage( new MessageEvent(
				this,
				"Error changing type for " + node + ".  Error received:  " + e.getMessage(),
				MessageEvent.ERROR_MESSAGE));		 
	  }

	  DeviceDao deviceDao = (DeviceDao) YukonSpringHook.getBean("deviceDao");
      if (userObject instanceof DeviceBase
              && paoDefinitionService.isPaoTypeChangeable(deviceDao.getYukonDeviceForDevice((DeviceBase) userObject))) {
          showChangeTypeWizardPanel(new DeviceChangeTypeWizardPanel(userObject));
      } else if (userObject instanceof DeviceMeterGroupBase) {
          int deviceId = ((DeviceMeterGroupBase) userObject).getDeviceMeterGroup().getDeviceID();
          SimpleDevice device = deviceDao.getYukonDeviceObjectById(deviceId);
          if(paoDefinitionService.isPaoTypeChangeable(device)) {
              YukonPAObject yukonPAObject = PAOFactory.createPAObject(deviceId);
              showChangeTypeWizardPanel(new DeviceChangeTypeWizardPanel(yukonPAObject));
          }
      }
	  else if (userObject instanceof com.cannontech.database.data.point.PointBase)
	  {
		 try
		 {
			StringBuffer message =
			   new StringBuffer(
				  "Are you sure you want to change the type of '"+node.toString() + "'?");


		DBDeleteResult delRes = new DBDeleteResult(
				((com.cannontech.database.data.point.PointBase) userObject).getPoint().getPointID().intValue(), DaoFactory.getDbDeletionDao().POINT_TYPE);
			
         byte deleteVal = DaoFactory.getDbDeletionDao().deletionAttempted( delRes );
         
         if( deleteVal == DaoFactory.getDbDeletionDao().STATUS_ALLOW
             || deleteVal == DaoFactory.getDbDeletionDao().STATUS_CONFIRM )
         {
			   showChangeTypeWizardPanel(
				  new com.cannontech.dbeditor.wizard.changetype.point.PointChangeTypeWizardPanel(
					 userObject));
			}
			else
			{
			  
			   javax.swing.JOptionPane.showConfirmDialog(
				  getParentFrame(),
				  ("You cannot change this point '"+node.toString() + "'" + delRes.getDescriptionMsg().toString()),
				  "Unable to change",
				  JOptionPane.CLOSED_OPTION,
				  JOptionPane.WARNING_MESSAGE);
			   confirm = JOptionPane.NO_OPTION;
			   //make it seem they clicked the NO to delete the Point 
			}

		 }
		 catch (java.sql.SQLException e)
		 {
			log.error( e.getMessage(), e );
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
	DefaultMutableTreeNode node = getDefaultTreeNode();
	
	if( node != null )
	{
		//a DBPersistent must be created from the Lite object so you can copy it
		com.cannontech.database.db.DBPersistent toCopy = com.cannontech.database.data.lite.LiteFactory.createDBPersistent((com.cannontech.database.data.lite.LiteBase)node.getUserObject());
		if(toCopy instanceof com.cannontech.database.data.device.DeviceBase && !(toCopy instanceof com.cannontech.database.data.device.lm.LMGroup))
        {
            showCopyWizardPanel (toCopy);
        } else if(toCopy instanceof DeviceMeterGroupBase) {
		    int deviceId = ((DeviceMeterGroupBase) toCopy).getDeviceMeterGroup().getDeviceID();
            YukonPAObject yukonPAObject = PAOFactory.createPAObject(deviceId);
            showCopyWizardPanel (yukonPAObject);
		}
        else if(toCopy instanceof com.cannontech.database.data.device.lm.LMProgramDirect)
            showCopyWizardPanel( new com.cannontech.dbeditor.wizard.copy.lm.LMProgramCopyWizardPanel((com.cannontech.database.data.device.lm.LMProgramBase)toCopy), toCopy );
        else if(toCopy instanceof com.cannontech.database.data.device.lm.LMGroup)
            showCopyWizardPanel( new com.cannontech.dbeditor.wizard.copy.lm.LMGroupCopyWizardPanel((com.cannontech.database.data.device.lm.LMGroup)toCopy), toCopy );
        else if(toCopy instanceof LMScenario)
            showCopyWizardPanel( new com.cannontech.dbeditor.wizard.copy.lm.LMScenarioCopyWizardPanel((LMScenario)toCopy), toCopy );
        else if( toCopy instanceof com.cannontech.database.data.point.PointBase )
        {
            showCopyWizardPanel( new com.cannontech.dbeditor.wizard.copy.point.PointCopyWizardPanel((com.cannontech.database.data.point.PointBase)toCopy, currentDatabase ), toCopy );
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
 * @return
 */
public DefaultMutableTreeNode getDefaultTreeNode() {
	DefaultMutableTreeNode node = getTreeViewPanel().getSelectedNode();
	return node;
}

public void showCopyWizardPanel(DBPersistent toCopy) {
	DeviceCopyWizardPanel devicePanel = new DeviceCopyWizardPanel((DeviceBase)toCopy);
	devicePanel.setDeviceType( toCopy );
	showCopyWizardPanel( devicePanel, toCopy );
}

private void deleteDBPersistent( DBPersistent deletable )
{
	try
	{
	    // get dbChangeMsgs BEFORE execute
        // this may be a delete and the dbChangeMsgs may not be retrievable after execute
	    DBChangeMsg[] msgs = getDBChangeMsgs(deletable, DbChangeType.DELETE);
	    
		Transaction t = Transaction.createTransaction(Transaction.DELETE, deletable);
		
		deletable = t.execute();
	
		//fire DBChange messages out to Dispatch
		queueDBChangeMsgs(deletable, DbChangeType.DELETE, msgs);
	
		fireMessage(new MessageEvent(this, deletable + " deleted successfully from the database."));
	}
	catch (com.cannontech.database.TransactionException e)
	{
		log.error( e.getMessage(), e );
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
   java.awt.Frame owner = CtiUtilities.getParentFrame(getTree());   
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
	            log.error( e.getMessage(), e );

					fireMessage( new MessageEvent(
							this,
							"Error retrieving " + nodes[i] + " from the database.  Error received:  " + e.getMessage(),
							MessageEvent.ERROR_MESSAGE));
	         }

	         PropertyPanel panel = EditorPanelFactory.createEditorPanel( userObject );
	         							//userObject.getEditorPanel(); //This call takes the most time

	         panel.addPropertyPanelListener(this);
	         panel.setValue(userObject);

	         JTreeEditorFrame frame = getAvailableEditorFrame();
	         frame.setOwnerNode( nodes[i] ); //set the editors ownerNode
	         frame.setTitle( userObject.toString() + " : " + panel.toString());
	         frame.setContentPane(panel);
	         
	         // sets the size of EVERY editor frame!!!!!!!
	         frame.setSize( EDITOR_FRAME_SIZE );
             frame.setMaximum(false);   //don't maximize since we are setting the size of the frame already.
	         frame.setLocation( getVisibleEditorFrames() * 10, getVisibleEditorFrames() * 20 );

	         frame.setVisible(true);

	         try
	         {
	            frame.setSelected(true);
	         }
	         catch (java.beans.PropertyVetoException e)
	         {
	            log.error( e.getMessage(), e ); //when does this happen??
	         }

			}
			
		} else if (event.getSource() != null && event.getSource() instanceof DBPersistent){
		    showEditor((DBPersistent) event.getSource());
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

private void showEditor(DBPersistent userObject) {
    Frame owner = CtiUtilities.getParentFrame(getTree());
    Cursor savedCursor = owner.getCursor();
    LiteBase liteBase = LiteFactory.createLite(userObject);
    DefaultMutableTreeNode node = new DefaultMutableTreeNode();
    node.setUserObject(liteBase);
    owner.setCursor(new Cursor(Cursor.WAIT_CURSOR));
    try {
        Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, userObject);
        userObject = t.execute();   
    } catch (Exception e) {
        log.error( e.getMessage(), e );
        fireMessage( new MessageEvent(this, "Error retrieving " + liteBase + " from the database.  Error received:  " + e.getMessage(), MessageEvent.ERROR_MESSAGE));
    }
    PropertyPanel panel = EditorPanelFactory.createEditorPanel( userObject );
    panel.addPropertyPanelListener(this);
    panel.setValue(userObject);
    JTreeEditorFrame frame = getAvailableEditorFrame();
    frame.setOwnerNode( node ); //set the editors ownerNode
    frame.setTitle( userObject.toString() + " : " + panel.toString());
    frame.setContentPane(panel);
    
    frame.setSize( EDITOR_FRAME_SIZE );
    frame.setLocation( getVisibleEditorFrames() * 10, getVisibleEditorFrames() * 20 );
    frame.setVisible(true);
    try {
       frame.setSelected(true);
    }
    catch (java.beans.PropertyVetoException e) {
       log.error( e.getMessage(), e ); //when does this happen??
    }
    finally{
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
private void exit(){
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
	synchronized (getInternalEditorFrames()) {
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
    				}
    				else    // act as though the cancel button has been pressed
    					current.fireCancelButtonPressed();
    			}
    
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
 * Used only to retrieve the dbChangeMsgs before executing a transaction, since that transaction 
 * may be a delete - in which case you'll want to retrieve dbChangeMsgs, execute, then queue the changes
 * using queueDBChangeMsgs().
 * 
 * This is because when it is a delete the dbChangeMsgs may not be retrievable after execute.
 * 
 * Use the standard generateDBChangeMsg() when not executing a delete, it does both get+queue steps.
 * @param object
 * @param changeType
 * @return
 */
private DBChangeMsg[] getDBChangeMsgs(com.cannontech.database.db.DBPersistent object, DbChangeType dbChangeType) {
    
    com.cannontech.message.dispatch.message.DBChangeMsg[] dbChanges = null;
    
    if( object instanceof CTIDbChange )
    {
        dbChanges = DefaultDatabaseCache.getInstance().createDBChangeMessages(
                    (CTIDbChange)object, dbChangeType );
    } else {
        
        throw new IllegalArgumentException("Non " + 
         CTIDbChange.class.getName() + 
         " class tried to generate a " + DBChangeMsg.class.getName() + 
            " its class was : " + object.getClass().getName() );
    }
    
    return dbChanges;
}

/**
 * Queues dbChanges to dispatch.
 * @param object
 * @param changeType
 * @param dbChange
 */
private void queueDBChangeMsgs(com.cannontech.database.db.DBPersistent object, DbChangeType dbChangeType, DBChangeMsg[] dbChange) 
{
	for( int i = 0; i < dbChange.length; i++ )
	{
		//handle the DBChangeMsg locally
		com.cannontech.database.data.lite.LiteBase lBase = 
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance().handleDBChangeMessage(dbChange[i]);

		//if cache isn't able to return us the liteBase, then build it from the DBPersistent itself
		if (lBase == null) {
			lBase = LiteFactory.createLite(object);
		}

        // Special case for point deletion
        if (lBase == null && dbChange[i].getDatabase() == DBChangeMsg.CHANGE_POINT_DB
		        && dbChangeType == DbChangeType.DELETE && object instanceof PointBase) {
		    
		    lBase = LiteFactory.createLite(object);
		}

		//tell our tree we may need to change the display
		updateTreePanel( lBase, (dbChange[i]).getDbChangeType() );
     
		getConnToDispatch().queue(dbChange[i]);
	}
}

/**
 * Gets and queues dbChangeMsgs.
 * Not to be used when executing a DELETE.
 * @param object
 * @param changeType
 */
private void generateDBChangeMsg( com.cannontech.database.db.DBPersistent object, DbChangeType dbChangeType  ) {
    
    DBChangeMsg[] msgs = getDBChangeMsgs(object, dbChangeType);
    queueDBChangeMsgs(object, dbChangeType, msgs);
}
	
		
/**
 * Insert the method's description here.
 * Creation date: (3/13/2001 3:42:38 PM)
 * @return javax.swing.JInternalFrame
 */
public JTreeEditorFrame getAvailableEditorFrame() 
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
 * @return com.cannontech.message.dispatch.ClientConnection
 */
private IServerConnection getConnToDispatch() 
{
	if(connToDispatch == null) {
		connToDispatch = ConnPool.getInstance().getDefDispatchConn();
		connToDispatch.addObserver(this);
		updateConnectionStatus(connToDispatch);
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
 * Used to get a listener for logging database editor events
 * @return returns a loggerEventListener, which implements
 * MessageEventListener
 */
private LoggerEventListener getLoggerEventListener() {
    if(loggerEventListener == null)
    {
        loggerEventListener = new LoggerEventListener(DatabaseEditor.class);
    }

    return loggerEventListener;
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
    synchronized (getInternalEditorFrames()) {
    	for( int i = 0; i < getInternalEditorFrames().length; i++ )
    		if( getInternalEditorFrames()[i].getContentPane() == panel )
    			return i;
    	//should never get here
        throw new RuntimeException("PropertyPanel '" + panel.toString() + "' found that does not have a parent JInternalFrame");
    }
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
		systemCreateMenu = new SystemCreateMenu();
		viewMenu = new ViewMenu();
		helpMenu = new HelpMenu();
		
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
			
		this.menuBar.add( fileMenu );
		this.menuBar.add( editMenu );		
		this.menuBar.add( viewMenu );
	}

	this.menuBar.remove( coreCreateMenu );
	this.menuBar.remove( lmCreateMenu );
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
	else if( whichDatabase == DatabaseTypes.SYSTEM_DB )
	{
		item = systemCreateMenu;
	}

	this.menuBar.add( item );
//	this.menuBar.add( toolsMenu );
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
	return (JFrame)CtiUtilities.getParentFrame(
				DatabaseEditor.this.getContentPane() );
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
		getTree().getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION );
		getTree().setLargeModel(true);
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
			if( getInternalEditorFrames()[i].isVisible() )
				count++;
		
		return count;
	}
	
}

/*
 * Handles incoming database change messages.
 */
public void handleDBChangeMsg( final DBChangeMsg msg, final LiteBase liteBase ) {
	//see if the message originated from us
	if( !(msg.getSource().equals(CtiUtilities.DEFAULT_MSG_SOURCE) ) ) {
	    SwingUtilities.invokeLater(new Runnable() {
	        public void run() {
        		StringBuffer txtMsg = new StringBuffer(
        		    msg.getDbChangeType()
                    + " Database Change Message received from: " + msg.getUserName() + " at " + msg.getSource());
        		
        		if (!SwingUtilities.isEventDispatchThread()) {
        		    log.error("oops");
        		}
        
        		synchronized( getInternalEditorFrames() ) {
        			for( int i = 0; i < getInternalEditorFrames().length; i++ ) {
        				//Be sure we have an owner tree node for each editor frame.
        			    JTreeEditorFrame frame = getInternalEditorFrames()[i];
        			    if(frame != null && frame.getOwnerNode() != null) {
        					PropertyPanel current = (PropertyPanel)getEditorFrame(frame.getOwnerNode()).getContentPane();
        					//handle the GUI change in a seperate location
        					DBChangeGUIHandler changeGUIHandler = new DBChangeGUIHandler( current, txtMsg );
        					changeGUIHandler.handleGUIChange( msg );
        				}
        			}
        		}
        
        		//If we get an id of zero, then refresh the whole thing including cache.
        		if(msg.getId() == DBChangeMsg.RELOAD_ALL) {
        			//refresh the cache and the connections state
        			DefaultDatabaseCache.getInstance().releaseAllCache();
        				
        			//do the actual refresh of the tree
        			getTreeViewPanel().refresh();	
        		} else {
        			//tell our tree we may need to change the display
        			updateTreePanel( liteBase, msg.getDbChangeType() );
        		}
        
        		//display a message on the message panel telling us about this event...Only if its a Device Change OR INSERT/UPDATE...other wise don't bother printing out Point Add messages.
        		if(msg.getDatabase() == DBChangeMsg.CHANGE_PAO_DB  || 
        		        msg.getDbChangeType() == DbChangeType.DELETE ||
        		        msg.getDbChangeType() == DbChangeType.UPDATE ) {
        		    fireMessage( new MessageEvent( DatabaseEditor.this, txtMsg.toString(), MessageEvent.INFORMATION_MESSAGE) );
        		}
	        }
	    });
	}
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) 
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	Throwable rootCause = CtiUtilities.getRootCause(exception);
    fireMessage( new MessageEvent( DatabaseEditor.this, 
                                   rootCause.getMessage(), MessageEvent.ERROR_MESSAGE) );
    JOptionPane.showMessageDialog(getParentFrame(), "An unexpected error has occurred: \n"
                                  + rootCause,
                                  "Uncaught Exception", JOptionPane.ERROR_MESSAGE);               

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

    AsyncDynamicDataSource dataSource =  (AsyncDynamicDataSource) YukonSpringHook.getBean("asyncDynamicDataSource");
    dataSource.addDBChangeLiteListener(this);
    
	// add the mouselistener for the JTree
	MouseListener ml = new MouseAdapter()
	{
		public void mousePressed(MouseEvent e) 
		{
			if (e.getSource() == editMenu) {
				popupMenuWillBecomeVisible(new PopupMenuEvent(DatabaseEditor.this.getTreeNodePopupMenu()));
			} else {
				int selRow = getTree().getRowForLocation(e.getX(), e.getY());
				
				if(selRow != -1) {
					//be sure this is not a multi selection attempt
					if( !e.isShiftDown() && !e.isControlDown() 
					    && getTree().getSelectionCount() <= 1 ) {
						getTree().setSelectionRow( selRow );
					}
	
					if(e.getClickCount() == 2) {
						executeEditButton_ActionPerformed( new ActionEvent(e.getSource(), e.getID(), "MouseDBLClicked") );
					}
				}
			}
			
		}
	};

	getTree().addMouseListener(ml);
	editMenu.addMouseListener(ml);

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
    
    //add a loggerEventListern for logging database editor events
    addMessageListener(getLoggerEventListener());
	owner = CtiUtilities.getParentFrame(rootPane);

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
        ClientStartupHelper clientStartupHelper = new ClientStartupHelper();
        clientStartupHelper.setAppName("DBEditor");
        clientStartupHelper.setRequiredRole(DBEditorRole.ROLEID);
        clientStartupHelper.setContext("com.cannontech.context.dbeditor");
        clientStartupHelper.setSplashUrl(CtiUtilities.DBEDITOR_SPLASH);

        // creates a stupid anonymous class to create a unique class name for the main frame
        // the ClientStartupHelper will use this for picking a Preference node
        final javax.swing.JFrame f = new javax.swing.JFrame("Yukon Database Editor [Not Connected to Dispatch]") {};
        clientStartupHelper.setParentFrame(f);
        f.setDefaultCloseOperation( WindowConstants.DO_NOTHING_ON_CLOSE );

        f.setIconImages(getIconsImages());

        clientStartupHelper.doStartup();

        if (ClientSession.getInstance().getUser().getUserID() == com.cannontech.user.UserUtils.USER_ADMIN_ID) {
            isSuperuser = true;
        }	

        DatabaseEditor editor = new DatabaseEditor();
        f.addWindowListener(editor);

        editor.displayDatabaseEditor( f.getRootPane() );

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                f.setVisible(true);
            }
        });

    }
    catch( Throwable t )
    {
        log.error("Unable to startup", t);
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
		//If nothing selected, default and return
		if( getTreeViewPanel().getSelectedNodes() == null ) {
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
		
		//defaults value here
      getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(true);
      getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(true);
      getTreeNodePopupMenu().getJMenuItemDelete().setEnabled(true);
      getTreeNodePopupMenu().getJMenuItemEdit().setEnabled(true);
      getTreeNodePopupMenu().getJMenuSortAllPointsBy().setEnabled(false);
	
      editMenu.changeTypeMenuItem.setEnabled(true);
      editMenu.copyMenuItem.setEnabled(true);
      editMenu.deleteMenuItem.setEnabled(true);
      editMenu.editMenuItem.setEnabled(true);
      
		//check for multi select
		if( getTreeViewPanel().getSelectedNodes().length > 1 )
		{
	      getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
	      getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
	      
	      editMenu.changeTypeMenuItem.setEnabled(false);
	      editMenu.copyMenuItem.setEnabled(false);
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
	            
	            editMenu.changeTypeMenuItem.setEnabled(false);
	            editMenu.copyMenuItem.setEnabled(false);
	            editMenu.deleteMenuItem.setEnabled(false);
	            editMenu.editMenuItem.setEnabled(false);
	            
	            if (!selectedNode.isRoot())
	               getTreeNodePopupMenu().getJMenuSortAllPointsBy().setEnabled(true);
	         }
	         
	         if (selectedNode.getUserObject() instanceof LiteYukonPAObject)
	         {
	            LiteYukonPAObject litYuk = (LiteYukonPAObject)selectedNode.getUserObject();
	            
	            if( (litYuk.getPaoType().getPaoClass() == PaoClass.CAPCONTROL 
	            		&& !DeviceTypesFuncs.isCapBankController(litYuk.getPaoType().getDeviceTypeId())) 
	                || litYuk.getPaoType() == PaoType.LM_GROUP_RIPPLE
	                || litYuk.getPaoType() == PaoType.LM_GROUP_INTEGRATION 
	                || litYuk.getPaoType() == PaoType.MACRO_GROUP
	                || litYuk.getPaoType().getPaoClass() == PaoClass.LOADMANAGEMENT )
	            {
	               getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
	               editMenu.changeTypeMenuItem.setEnabled(false);
	            }
	            else if ( litYuk.getPaoType().getPaoCategory() == PaoCategory.CUSTOMER
	                       || litYuk.getPaoType().getPaoClass() == PaoClass.SYSTEM)
	            {
	               getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
	               getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
	               editMenu.changeTypeMenuItem.setEnabled(false);
	               editMenu.copyMenuItem.setEnabled(false);
	            }
	
	         }
	         else if (
	            selectedNode.getUserObject() instanceof LiteNotificationGroup
	               || selectedNode.getUserObject() instanceof LiteContactNotification
	               || selectedNode.getUserObject() instanceof LiteAlarmCategory
	               || (selectedNode.getUserObject() instanceof LiteYukonPAObject
	                   && ((LiteYukonPAObject)selectedNode.getUserObject()).getPaoType() == PaoType.CAP_CONTROL_SUBBUS) )
	         {
	            getTreeNodePopupMenu().getJMenuItemChangeType().setEnabled(false);
	            getTreeNodePopupMenu().getJMenuItemCopy().setEnabled(false);
	            editMenu.changeTypeMenuItem.setEnabled(false);
	            editMenu.copyMenuItem.setEnabled(false);
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
		log.error( e.getMessage(), e );
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
		log.error( e.getMessage(), e );
	}
		
	if( !activateBilling )
		coreCreateMenu.remove( coreCreateMenu.billingGroupMenuItem );
	
	//Decide which items to put into the view menu
	boolean showLm = false;
	boolean showSystem = false;
	
	try
	{
		showLm = ClientSession.getInstance().getRolePropertyValue(
			DBEditorRole.DBEDITOR_LM, "FALSE").trim().equalsIgnoreCase("TRUE");
	}
	catch( Exception e )
	{
		log.error( e.getMessage(), e );
	}
	
	try
	{
		showSystem = ClientSession.getInstance().getRolePropertyValue(
			DBEditorRole.DBEDITOR_SYSTEM, "FALSE").trim().equalsIgnoreCase("TRUE");
	}
	catch( Exception e )
	{
		log.error( e.getMessage(), e );
	}

	//check whether the user should be able to see anything regarding logins in the dbeditor
	try
	{
		accessOfLoginNotAllowed = ClientSession.getInstance().getRolePropertyValue(
			DBEditorRole.PERMIT_LOGIN_EDIT, "TRUE").trim().equalsIgnoreCase("FALSE");
	}
	catch( Exception e )
	{
		log.error( e.getMessage(), e );
	}
	
	//shouldn't be allowed to see login stuff
	if( accessOfLoginNotAllowed && !isSuperuser)
	{
		systemCreateMenu.remove( systemCreateMenu.loginGrpMenuItem );
		systemCreateMenu.remove( systemCreateMenu.loginMenuItem );
	}
	
	if( !showLm )
		viewMenu.remove( viewMenu.lmRadioButtonMenuItem );

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
	    try {
	        final com.cannontech.database.db.DBPersistent object = (com.cannontech.database.db.DBPersistent) panel.getValue(null);
		
    		if( panel.hasChanged() )
    		{
    			panel.setChanged(false);
                
    			if (DatabaseEditorUtil.showUpdateRouteName(object)) {
    			    DatabaseEditorUtil.updateRouteName(this, panel, object);
                }    
    			
    			if (DatabaseEditorUtil.isDisconnectCollarCompatible(object)) {
                    DatabaseEditorUtil.updateDisconnectStatus(this, panel, object);
                }
                
    			updateResult = updateDBPersistent(object);
                
    			if( updateResult )
    			{			
                    panel.postSave(object);
    
    				if( event.getID() == PropertyPanelEvent.APPLY_SELECTION )
    				{
    					/* APPLY ONLY EVENTS GO HERE */
    				    synchronized (getInternalEditorFrames()) {
    				        getInternalEditorFrames()[frameLocation].setTitle( object.toString() + " : " + panel.toString() );
    				    }
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
		} catch (EditorInputValidationException e) {
            javax.swing.JOptionPane.showMessageDialog( panel, 
                                                       e.getMessage(), 
                                                       "Input Error", 
                                                       javax.swing.JOptionPane.WARNING_MESSAGE );
            return;
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
		panel.disposeValue();
		synchronized (getInternalEditorFrames()) {
		    getInternalEditorFrames()[frameLocation].setVisible(false); //.dispose() ?? not sure!!???
		}
		if( frameLocation >= INITIAL_EDITOR_COUNT )
			removeUnneededEditorFrames();
		
		desktopPane.repaint();
	}


}

public boolean insertDBPersistent( DBPersistent newItem )
{
	boolean success = false;
	
	try
	{
		//insert the newly created item into the DB
		Transaction<DBPersistent> t = Transaction.createTransaction(Transaction.INSERT, newItem);
		newItem = t.execute();
	
		String messageString = newItem + " inserted successfully into the database.";
		fireMessage(new MessageEvent(this, messageString));
	
		//fire DBChange messages out to Dispatch
		generateDBChangeMsg( newItem, DbChangeType.ADD );
		
		success = true;
	}
	catch( com.cannontech.common.wizard.CancelInsertException ci )
	{
		//inside the getValue(), this exception was thrown
	} catch (TransactionException e) {
		log.error( e.getMessage(), e );
		String cause = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
		String messageString = "Error inserting " + newItem + " into the database.  Error received:  " + cause.trim();
		fireMessage(new MessageEvent(this, messageString, MessageEvent.ERROR_MESSAGE));
	} catch( DataAccessException e ) {
        /* Handle the DataAccessExceptions that daos might throw. */
        /* Usually catching DataAccessException is bad practice, but in 
            this case we want to catch all problems occuring due to dao method failures. */
        log.error( e.getMessage(), e );
        String messageString = " Error inserting " + newItem + " in the database.  Error received: " + e.getMessage();
        fireMessage( new MessageEvent( this, messageString, MessageEvent.ERROR_MESSAGE) );
        return false;
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
    boolean selectInTree = true;
    boolean selectWorked = false;
    DBPersistent newItem = null;

	if (event.getID() == WizardPanelEvent.FINISH_SELECTION)
	{
		if( changingObjectType )
		{
			objTypeChange = executeChangeObjectType( event );
		}
		else
		{		
			WizardPanel p = (WizardPanel) event.getSource();

			//p.getValue(null) may throw a CancelInsertException
			newItem = (DBPersistent) p.getValue(null);

            // Hack - Don't try to select new Categories or TOUSchedules 
            // created in the device config UI
            selectInTree = !(newItem instanceof TOUSchedule)
                        || (newItem instanceof TOUSchedule && currentDatabase == DatabaseTypes.SYSTEM_DB);
            
			//try to insert the object into the DB
			successfullInsertion = insertDBPersistent( newItem );
	
            if (successfullInsertion) {
                p.postSave(newItem);
            }

			//tell our current tree model to update itself so it can display the newly added item
			//getTreeViewPanel().refresh();

			//Bring the editor up for the newly created Object
			if (successfullInsertion && selectInTree)
			{
			    getTreeViewPanel().selectObject(newItem);
			    selectWorked = true;
			}

		}
	}

	
	if( event.getID() == WizardPanelEvent.CANCEL_SELECTION
		 || (event.getID() == WizardPanelEvent.FINISH_SELECTION && successfullInsertion)
		 || (event.getID() == WizardPanelEvent.FINISH_SELECTION)
		 && objTypeChange )
	{
		changingObjectType = false;

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
	

	if((successfullInsertion || objTypeChange) && selectInTree) {
	    if(selectWorked || objTypeChange) {
	    	showEditorSelectedObject();
	    }else {
	        showEditor(newItem);
	    }
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

	
	TreeModelEnum[] models = null;

	//Get a ref to the rootpane
	JFrame frame = (JFrame) CtiUtilities.getParentFrame( getContentPane() );
    JRootPane rPane = frame.getRootPane();
	
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
				//hex value representing some privileges of the user on this machine
				long show_protocol = Long.parseLong( 
					ClientSession.getInstance().getRolePropertyValue(
					DBEditorRole.OPTIONAL_PRODUCT_DEV, "0"), 16 );
				if((show_protocol & ClientRights.SHOW_ADDITIONAL_PROTOCOLS) != 0)
					models = LM_MODELS_WITH_SA;
				else
					models = LM_MODELS;
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

    int length = (models == LM_MODELS || models == LM_MODELS_WITH_SA || models == CORE_MODELS || models == SYSTEM_MODELS)
                ? models.length : models.length - 1;

	LiteBaseTreeModel[] newModels = new DBTreeModel[length];
	for( int i = 0; i < models.length; i++ )
	{
		LiteBaseTreeModel treeModel = TreeModelEnum.create(models[i]);
		if (treeModel instanceof FrameAware) {
		    ((FrameAware) treeModel).setParentFrame(frame);
		}
        newModels[i] = treeModel;
	}
    
	getTreeViewPanel().setTreeModels(newModels);
	if( models == CORE_MODELS )
		getTreeViewPanel().setSelectedSortByIndex(Arrays.asList(models).indexOf(TreeModelEnum.DEVICE));
	if( models == LM_MODELS || models == LM_MODELS_WITH_SA )
		getTreeViewPanel().setSelectedSortByIndex(Arrays.asList(models).indexOf(TreeModelEnum.LMGROUPS));
	if( models == SYSTEM_MODELS )
		getTreeViewPanel().setSelectedSortByIndex(Arrays.asList(models).indexOf(TreeModelEnum.LOGIN_GROUPS));
	
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
	java.awt.Frame owner = CtiUtilities.getParentFrame(this.desktopPane);
	java.awt.Cursor savedCursor = owner.getCursor();
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	wizard.addWizardPanelListener(this);
	
	javax.swing.JInternalFrame f = new javax.swing.JInternalFrame();
	
	f.setContentPane( wizard );
    f.setResizable(true);
	f.setSize( (int)wizard.getActualSize().getWidth(),
		  		  (int)wizard.getActualSize().getHeight());//410,470);

	this.desktopPane.add( f );
	changingObjectType = true;
	wizard.setValue(null);
	f.setVisible(true);
	ImageIcon wizardIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(DBEDITOR_IMG_16));
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
private void showCopyWizardPanel(WizardPanel wizard, DBPersistent toCopy) {

	//Set the cursor to wait
	java.awt.Frame owner = CtiUtilities.getParentFrame(this.desktopPane);
	java.awt.Cursor savedCursor = owner.getCursor();
	owner.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );

	wizard.addWizardPanelListener(this);
	
	JInternalFrame f = new JInternalFrame();
	
	f.setContentPane( wizard );
	f.setSize(435, 500);
	f.setResizable(true);

	this.desktopPane.add( f );
	
	wizard.setValue(toCopy);
	ImageIcon wizardIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(DatabaseEditor.DBEDITOR_IMG_16));
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
	java.awt.Frame f = CtiUtilities.getParentFrame(this.desktopPane);
	f.validate();

	try
	{
		
		executeEditButton_ActionPerformed(new ActionEvent(this, DBEditorTreePopUpMenu.EDIT_TREENODE, "edit"));
	}
	catch (Exception e)
	{
		log.error( e.getMessage(), e );
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
	java.awt.Frame owner = CtiUtilities.getParentFrame(this.desktopPane);
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
	ImageIcon wizardIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(DatabaseEditor.DBEDITOR_IMG_16));
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
	if( o instanceof ClientConnection )
	{
		updateConnectionStatus((IServerConnection) o);
	}
}

/**
 * Update the status of out dispatch connection for the user.
 * @param conn
 */
private void updateConnectionStatus(IServerConnection conn) {
	if( conn.isValid() )
	{
		connToVanGoghErrorMessageSent = false;
		fireMessage( new MessageEvent( this, "Connection to Message Dispatcher Established.") );
		if( owner != null )
			owner.setTitle("Yukon Database Editor [Connected to Dispatch@" +
				 		 conn.getHost() + ":" +
				 		 conn.getPort() + "]" );
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

    /**
     * 
     * @param DBPersistent The DBPersistent to update.
     * @return true if transaction is successful, otherwise false.
     */
    private boolean updateDBPersistent(DBPersistent dbPersistent) {
    	try {
    	    
    		Transaction<DBPersistent> t = Transaction.createTransaction( Transaction.UPDATE, dbPersistent );
    		dbPersistent = t.execute();
    
    		//write the DBChangeMessage out to Dispatch since it was a Successfull UPDATE
    		generateDBChangeMsg( dbPersistent, DbChangeType.UPDATE );
    		
    		String messageString = dbPersistent + " updated successfully in the database.";
    		fireMessage( new MessageEvent( this, messageString) );
    		
    	} catch( TransactionException e ) {
    	    /* Handle the normal Transaction exceptions that dbpersistence throw. */
    		log.error( e.getMessage(), e );
    		String messageString = " Error updating " + dbPersistent + " in the database.  Error received: " + e.getMessage();
    		fireMessage( new MessageEvent( this, messageString, MessageEvent.ERROR_MESSAGE) );
    		return false;
    	} catch( DataAccessException e ) {
    	    /* Handle the DataAccessExceptions that daos might throw. */
    	    /* Usually catching DataAccessException is bad practice, but in 
    	        this case we want to catch all problems occuring due to dao method failures. */
    	    log.error( e.getMessage(), e );
    	    String messageString = " Error updating " + dbPersistent + " in the database.  Error received: " + e.getMessage();
    	    fireMessage( new MessageEvent( this, messageString, MessageEvent.ERROR_MESSAGE) );
    	    return false;
    	}
    
    	return true;
    }

    /**
     * Helper method to update the tree 
     * @param lBase - LiteBase that has changed
     * @param changeType - Type of DBChange
     */
    private void updateTreePanel(com.cannontech.database.data.lite.LiteBase lBase, DbChangeType dbChangeType) {
        getTreeViewPanel().processDBChange(dbChangeType, lBase);
        getTreeViewPanel().revalidate();
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
    System.out.println("here");
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

    public static DatabaseEditor getInstance(){
        return editor;
    }
}

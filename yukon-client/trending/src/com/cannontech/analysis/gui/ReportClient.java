package com.cannontech.analysis.gui;

/**
 * Insert the type's description here.
 * Creation date: (11/3/2003 10:53:31 AM)
 * @author: 
 */

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import com.cannontech.analysis.ReportTypes;
import com.cannontech.analysis.report.DatabaseReport;
import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.report.EnergyCompanyActivityLogReport;
import com.cannontech.analysis.report.LGAccountingReport;
import com.cannontech.analysis.report.MissedMeterReport;
import com.cannontech.analysis.report.PowerFailReport;
import com.cannontech.analysis.report.RouteMacroReport;
import com.cannontech.analysis.report.StatisticReport;
import com.cannontech.analysis.report.SystemLogReport;
import com.cannontech.analysis.report.YukonReportBase;
import com.cannontech.analysis.tablemodel.ActivityModel;
import com.cannontech.analysis.tablemodel.DatabaseModel;
import com.cannontech.analysis.tablemodel.DisconnectModel;
import com.cannontech.analysis.tablemodel.LMControlLogModel;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.MeterReadModel;
import com.cannontech.analysis.tablemodel.PowerFailModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.analysis.tablemodel.RouteMacroModel;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.SystemLogModel;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.SplashWindow;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.database.cache.functions.RoleFuncs;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.db.point.SystemLog;
import com.cannontech.database.model.CheckBoxDBTreeModel;
import com.cannontech.database.model.CommChannelCheckBoxTreeModel;
import com.cannontech.database.model.DeviceCheckBoxTreeModel;
import com.cannontech.database.model.LiteBaseTreeModel;
import com.cannontech.database.model.MCTCheckBoxTreeModel;
import com.cannontech.database.model.ModelFactory;
import com.cannontech.database.model.TransmitterCheckBoxTreeModel;
import com.cannontech.debug.gui.AboutDialog;
import com.cannontech.message.dispatch.message.Command;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.message.util.ClientConnection;
import com.cannontech.roles.yukon.SystemRole;
import com.cannontech.util.ServletUtil;

public class ReportClient extends javax.swing.JPanel implements java.awt.event.ActionListener {
	
	private String HELP_FILE = CtiUtilities.getHelpDirPath() + "Yukon Reporting Help.chm";
		
	private javax.swing.JSplitPane ivjLeftRightSplitPane = null;
	private static javax.swing.JFrame reportClientFrame = null;
	private javax.swing.JMenuBar menuBar = null;
	private ReportsMenu reportsMenu = null;
	private FileMenu fileMenu = null;
	private HelpMenu helpMenu = null;

private com.cannontech.common.gui.util.CheckBoxTreeViewPanel ivjCheckBoxTreeViewPanel = null;
	private javax.swing.JButton ivjGenerateButton = null;
	private javax.swing.JPanel ivjReportDisplayPanel = null;
	private javax.swing.JPanel ivjReportSetupPanel = null;
	private StartEndPanel ivjStartEndPanel = null;
	private javax.swing.JTabbedPane ivjTabbedPane = null;
	private ReportModelBase model = null;
	//contains values of LiteBaseTreeModel
//	private Vector models = null;
	private com.cannontech.message.dispatch.ClientConnection connToDispatch;
	private Date startDate = ServletUtil.getToday();
	private Date endDate = ServletUtil.getTomorrow();
	
	//String representing the statType for StatisticModel type ONLY
	private String statTypeString = StatisticModel.DAILY_STAT_TYPE_STRING;
	private int [] currentModels = null;
	private static final int[] STATISTIC_MODELS = {
		ModelFactory.MCT_CHECKBOX,
		ModelFactory.COMMCHANNEL_CHECKBOX,
		ModelFactory.DEVICE_CHECKBOX,
		ModelFactory.TRANSMITTER_CHECKBOX
	};
	private static final int[] DISCONNECT_MODELS = {
		ModelFactory.COLLECTIONGROUP_CHECKBOX
	};
	private static final int[] POWERFAIL_MODELS = {
		ModelFactory.COLLECTIONGROUP_CHECKBOX
	};
	private static final int[] LOADGROUP_MODELS = {
		ModelFactory.LMGROUP_CHECKBOX
	};
	private static final int[] AMR_MODELS= {
		ModelFactory.COLLECTIONGROUP_CHECKBOX
	};
	private static final int[] DB_REPORTS_MODELS = {
		ModelFactory.MCT_CHECKBOX
	};
	private static final int[] EC_MODELS = {
		ModelFactory.ENERGYCOMPANY_CHECKBOX
	};
	private static final int[] SYSTEM_LOG_MODELS = {
		ModelFactory.SYSTEMLOG_TYPES_CHECKBOX
	};
	private HashMap modelMap = new HashMap();
  private static final int [] REPORTS_MODEL = {
	  ModelFactory.MCT_CHECKBOX,
	  ModelFactory.COMMCHANNEL_CHECKBOX,
	  ModelFactory.DEVICE_CHECKBOX,
	  ModelFactory.TRANSMITTER_CHECKBOX,
	  ModelFactory.COLLECTIONGROUP_CHECKBOX,
	  ModelFactory.LMGROUP_CHECKBOX,
	  ModelFactory.ENERGYCOMPANY_CHECKBOX,
	  ModelFactory.SYSTEMLOG_TYPES_CHECKBOX
	};
	/**
	 * Reportclient constructor comment.
	 */
	public ReportClient()
	{
		super();
		initialize();
	}
	/**
	 * Reportclient constructor comment.
	 * @param layout java.awt.LayoutManager
	 */
	public ReportClient(javax.swing.JFrame rootFrame)
	{
		super();
		setReportClientFrame(rootFrame);
		initialize();
		//	initializeSwingComponents();
	}
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */

	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == getReportsMenu().getTodayMenuItem())
		{
			enableComponents(false, false);
			loadTreeModels(STATISTIC_MODELS);
			setModel(new StatisticModel(StatisticModel.DAILY_STAT_TYPE_STRING));
		}
		else if (event.getSource() == getReportsMenu().getYesterdayMenuItem())
		{
			enableComponents(false, false);			
			loadTreeModels(STATISTIC_MODELS);
			setModel(new StatisticModel(StatisticModel.YESTERDAY_STAT_TYPE_STRING));
		}
		else if (event.getSource() == getReportsMenu().getMonthlyMenuItem())
		{
			enableComponents(false, false);
			loadTreeModels(STATISTIC_MODELS);
			setModel(model = new StatisticModel(StatisticModel.MONTHLY_STAT_TYPE_STRING));
		}
		else if (event.getSource() == getReportsMenu().getPrevMonthMenuItem())
		{
			enableComponents(false, false);
			loadTreeModels(STATISTIC_MODELS);
			setModel(new StatisticModel(StatisticModel.LASTMONTH_STAT_TYPE_STRING));
		}
		else if (event.getSource() == getReportsMenu().getLoadGroupAcctMenuItem())
		{
			enableComponents(true, true);
			loadTreeModels(LOADGROUP_MODELS);
			setModel(new LoadGroupModel());
		}
		else if (event.getSource() == getReportsMenu().getLMControlLogMenuItem())
		{
			enableComponents(true, true);
			loadTreeModels(LOADGROUP_MODELS);
			setModel(new LMControlLogModel());
		}
		else if (event.getSource() == getReportsMenu().getPowerFailMenuItem())
		{
			enableComponents(true, true);
			loadTreeModels(POWERFAIL_MODELS);
			setModel(new PowerFailModel());
		}
		else if ( event.getSource() == getReportsMenu().getHistoryMenuItem())
		{
			enableComponents(true, true);
			loadTreeModels(DISCONNECT_MODELS);
			setModel(new DisconnectModel(DisconnectModel.HISTORY_STRING));
		}
		else if ( event.getSource() == getReportsMenu().getConnectedMenuItem())
		{
			enableComponents(false, false);
			loadTreeModels(DISCONNECT_MODELS);
			setModel(new DisconnectModel(DisconnectModel.CONNECTED_STRING));
		}
		else if ( event.getSource() == getReportsMenu().getDisconnectedMenuItem())
		{
			enableComponents(false, false);
			loadTreeModels(DISCONNECT_MODELS);
			setModel(new DisconnectModel(DisconnectModel.DISCONNECTED_STRING));
		}
		else if ( event.getSource() == getReportsMenu().getCurrentStateMenuItem())
		{
			enableComponents(false, false);
			loadTreeModels(DISCONNECT_MODELS);
			setModel(new DisconnectModel(DisconnectModel.CURRENT_STRING));
		}
		else if (event.getSource() == getReportsMenu().getMissedMeterMenuItem())
		{
			enableComponents(true, true);
			loadTreeModels(AMR_MODELS);
			setModel(new MeterReadModel(ReportTypes.MISSED_METER_DATA));
		}
		else if (event.getSource() == getReportsMenu().getSuccessMeterMenuItem())
		{
			enableComponents(true, true);
			loadTreeModels(AMR_MODELS);
			setModel(new MeterReadModel(ReportTypes.SUCCESS_METER_DATA));
		}
		else if (event.getSource() == getReportsMenu().getCarrierMenuItem())
		{
			enableComponents(false, false);
			loadTreeModels(DB_REPORTS_MODELS);
			setModel(new DatabaseModel());
		}
		else if (event.getSource() == getReportsMenu().getSystemLogMenuItem())
		{
			enableComponents(true, true);
			loadTreeModels(SYSTEM_LOG_MODELS);
			setModel(new SystemLogModel());
		}
		else if (event.getSource() == getReportsMenu().getActivityLogMenuItem())
		{
			enableComponents(true, true);
			loadTreeModels(EC_MODELS);
			setModel(new ActivityModel(ReportTypes.ENERGY_COMPANY_ACTIVITY_LOG_DATA));
		}
		else if (event.getSource() == getReportsMenu().getLoadProfileMenuItem())
		{
			//TODO
			//loadTreeModels(EC_MODELS);
			//setModel(new LoadProfile(ReportTypes.ENERGY_COMPANY_ACTIVITY_LOG_DATA));
		}
		else if (event.getSource() == getReportsMenu().getRouteMacroMenuItem())
		{
			//TODO
			loadTreeModels(DB_REPORTS_MODELS);
			setModel(new RouteMacroModel(DeviceClasses.STRING_CLASS_CARRIER, ReportTypes.CARRIER_ROUTE_MACRO_DATA));
		}

		else if (event.getSource() == getGenerateButton())
		{
			if (getModel() == null)
			{
				showPopupMessage("Please select a Report from Reports Menu first", javax.swing.JOptionPane.WARNING_MESSAGE);
				return;
			}
				
			YukonReportBase report = null;
			getModel().setData(null);
			
			getModel().setStartTime(getStartDate().getTime());
			getModel().setStopTime(getEndDate().getTime());

			if (model instanceof ActivityModel)
			{
				int[] ecIDs = getLiteIDsFromNodes();
				if (model instanceof ActivityModel)
					((ActivityModel)model).setECIDs(ecIDs);
				report = new EnergyCompanyActivityLogReport();
			}
			else if (model instanceof DatabaseModel)
			{
				int[] paoIDs = getLiteIDsFromNodes();
				model.setPaoIDs(paoIDs);
				report = new DatabaseReport();
			}
			else if (model instanceof DisconnectModel)
			{
				String[] collGrps = getStringsFromNodes();
				model.setCollectionGroups(collGrps);
				report = new DisconnectReport();
			}
			else if (model instanceof LoadGroupModel)
			{
				int[] paoIDs = getLiteIDsFromNodes();
				model.setPaoIDs(paoIDs);
				report = new LGAccountingReport();
			}
			else if (model instanceof MeterReadModel)
			{
				String[] collGrps = getStringsFromNodes();
				model.setCollectionGroups(collGrps);
				report = new MissedMeterReport();
			}
			else if (model instanceof PowerFailModel)
			{
				String[] collGrps = getStringsFromNodes();
				model.setCollectionGroups(collGrps);
				report = new PowerFailReport();
			}
			else if (model instanceof RouteMacroModel)
			{
				int[] paoIDs = getLiteIDsFromNodes();
				model.setPaoIDs(paoIDs);
				report = new RouteMacroReport();
			}			
			else if (model instanceof StatisticModel)
			{
				int[] paoIDs = getLiteIDsFromNodes();
				model.setPaoIDs(paoIDs);
								
				getCheckBoxTreeViewPanel().getSortByComboBox().getSelectedItem();
				Object selected = getCheckBoxTreeViewPanel().getSortByComboBox().getSelectedItem();
				int statModelType = StatisticModel.CARRIER_COMM_DATA;	//default
				if( selected instanceof MCTCheckBoxTreeModel)
					statModelType = StatisticModel.CARRIER_COMM_DATA;
				else if (selected instanceof DeviceCheckBoxTreeModel)
					statModelType = StatisticModel.DEVICE_COMM_DATA;
				else if (selected instanceof TransmitterCheckBoxTreeModel)
					statModelType = StatisticModel.DEVICE_COMM_DATA;
				else if (selected instanceof CommChannelCheckBoxTreeModel)
					statModelType = StatisticModel.COMM_CHANNEL_DATA;

				((StatisticModel)model).setStatModelType(statModelType);
				report = new StatisticReport();
			}
			else if (model instanceof SystemLogModel)
			{
				String[] logTypes = getStringsFromNodes();
				if( logTypes != null)
				{
					int[] logTypeIDs = new int[logTypes.length];
					for (int i = 0; i < logTypeIDs.length; i++)
						logTypeIDs[i] = SystemLog.getLogTypeIDFromString(logTypes[i]);
	
					((SystemLogModel)getModel()).setLogTypes(logTypeIDs);
				}
				else
				((SystemLogModel)getModel()).setLogTypes(null);
				report = new SystemLogReport();
			}
			else
			{
				CTILogger.error("The model, " + model.getClass().toString() +", was not recognized...no report can be generated.");
			}
			if ( report != null)
			{
				

				java.awt.Cursor savedCursor = null;
				try
				{
					//Set cursor to show waiting for update.
					savedCursor = this.getCursor();
					this.setCursor( new java.awt.Cursor( java.awt.Cursor.WAIT_CURSOR ) );
					//TODO work on getting something other than a JFrame in the tab...something like a JPanel
//					ivjTabbedPane.insertTab(getModel().getTitleString(),null,report.getPreviewPanel(getModel()),null,0);
					ivjTabbedPane.insertTab(getModel().getTitleString(),null,report.getPreviewFrame(getModel()),null,0);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					this.setCursor(savedCursor);
				}
			}			
		}
		else if (event.getSource() == getStartEndPanel().getStartDateComboBox())
		{
			// Need to make sure the date has changed otherwise we are doing a billion updates on the one stateChange.
//			if( getStartDate().compareTo((Object)getStartEndPanel().getStartDateComboBox().getSelectedDate()) != 0 )
//			{
			setStartDate(getStartEndPanel().getStartDateComboBox().getSelectedDate());
//			}
		}
		else if (event.getSource() == getStartEndPanel().getEndDateComboBox())
		{
			// Need to make sure the date has changed otherwise we are doing a billion updates on the one stateChange.
//			if( getStartDate().compareTo((Object)getStartEndPanel().getEndDateComboBox().getSelectedDate()) != 0 )
//			{
			setEndDate(getStartEndPanel().getEndDateComboBox().getSelectedDate());
//			}
		}
		else if( event.getSource() == getHelpMenu().getHelpTopicsMenuItem())
		{
			CtiUtilities.showHelp( HELP_FILE );
		}
		else if(event.getSource() == getHelpMenu().getAboutMenuItem())
		{
			about( );
		}
		else if( event.getSource() == getFileMenu().getExitMenuItem() )
		{
			exit();
		}		
	}
	/**
	 * @param startDate_ enables the Start Date label and calendar drop down
	 * @param startTime_ enables the Start Time label and text field
	 * @param endDate_ enables the End Date label and calendar drop down
	 * @param endTime_ enables the End Time label and text field
	 */
	private void enableComponents(boolean startDate_, boolean endDate_)
	{
		getStartEndPanel().getStartDateComboBox().setEnabled(startDate_);
		getStartEndPanel().getStartDateLabel().setEnabled(startDate_);
		getStartEndPanel().getStartTimeTextField().setEnabled(startDate_);
		getStartEndPanel().getStartTimeLabel().setEnabled(startDate_);
		getStartEndPanel().getEndDateComboBox().setEnabled(endDate_);
		getStartEndPanel().getEndDateLabel().setEnabled(endDate_);
		getStartEndPanel().getEndTimeTextField().setEnabled(endDate_);
		getStartEndPanel().getEndTimeLabel().setEnabled(endDate_);
	}
	/**
	 * Add ActionListener(s) to all menuItems in each menu.
	 * @param menu
	 */
	public void addMenuItemActionListeners(JMenu menu)
	{
		JMenuItem item;

		for (int i = 0; i < menu.getItemCount(); i++)
		{
			item = menu.getItem(i);

			if (item != null)
			{
				menu.getItem(i).addActionListener(this);
				if (item instanceof JMenu)
				{
					for (int j = 0; j < ((JMenu) item).getItemCount();j++)
					{
						if( ((JMenu)item).getItem(j) != null)
							((JMenu) item).getItem(j).addActionListener(this);
					}
				}
			}

		}
	}
	/**
	 * Connect to dispatch, register with Message
	 */
	private void connect() 
	{
		String host = "127.0.0.1";
		int port = 1510;
		try
		{
			host = RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_MACHINE );
			port = Integer.parseInt( RoleFuncs.getGlobalPropertyValue( SystemRole.DISPATCH_PORT ) ); 
		}
		catch( Exception e)
		{
			CTILogger.error( e.getMessage(), e );
		}

		connToDispatch = new com.cannontech.message.dispatch.ClientConnection();

		Registration reg = new Registration();
		reg.setAppName("Yukon Reporting");
		reg.setAppIsUnique(0);
		reg.setAppKnownPort(0);
		reg.setAppExpirationDelay( 1000000 );
	
		connToDispatch.setHost(host);
		connToDispatch.setPort(port);
		connToDispatch.setAutoReconnect(true);
		connToDispatch.setRegistrationMsg(reg);

		try
		{
			connToDispatch.connectWithoutWait();
		}
		catch ( Exception e )
		{
			e.printStackTrace();
		}
	}
	/**
	 * Display the dialog with about information.
	 */
	public void about( ) 
	{
		AboutDialog aboutDialog = new AboutDialog(getReportParentFrame(), "About Reporting", true );

		aboutDialog.setLocationRelativeTo( getReportParentFrame() );
		aboutDialog.setValue(null);
		aboutDialog.show();
	}	
	/**
	 * Writes the current application state to a file for convenient default startup display.
	 * Sends CLIENT_APP_SHUTDOWN message to dispatch before exitting.
	 * Creation date: (9/25/2001 11:12:24 AM)
	 */
	public void exit()
	{
		try
		{
			if ( getClientConnection() != null && getClientConnection().isValid() )  // free up Dispatches resources
			{
				Command command = new Command();
				command.setPriority(15);
				command.setOperation( Command.CLIENT_APP_SHUTDOWN );
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF0F047B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DB8DD4D4D712A699B34733A759B506C484B428E80802E2A406389AD151680419B0B9E442E4F5564D38336E46E4BC9B13F193F77752C0CB8CEA7333E8B246A10A86843195508E63CC7C4189C6D4B6B091F790EC68A75D5AF4F73A9F8209B1DBF56F2B570F66B52893B9274EFB376ECF5D2ADB376A3B77B52C70F7D1599906C246B2321972F7A4033128CF98FB276F9F47914FBE2A4D40B4FF9F8165312F
	0B32605987F566E5ED869373FC1083657B316D52B643B6F8AFE0E5C67B91F889A26798545DB36D77FF98DA4E423DA4E7A3AF27FC1E8B4FADC023001700FD9EC4792BBE5F2E70530137C5F9C74A00D1C3F91BEFB236EB78F58F0AF6A14A33F8B436796F4FF7A8754B010FED5681650C760F79AE1F5DFD3596255C7AE094CB7B518B464DA8CBB3BE11DD69FB3F72E46C7F0CE808CCB676C4A5BC0FB6DE6B7AE3273B3B32536AF239DD3254E92F343A2C4EFE1F43D7595563282D7DC272383D72C927C3F2490CED6073
	1C10114347E0AC994AE2CEBF5DFCD41199FAAD0AA5CDDA9CF4CE479E92F331BD641F93CB0F5BD4980AE3171E49D57AC5C37DF2A0A153F306E436C03BF45EB601E15BD1709E831494CAF3794347B41716EFE630740EB70DDB034CE54743E28EF58F7B67B2F7D95C267779FA1FAB45B58B013AE820C42065C09B00F6BC52E678AD7733F7BF5E874F639D72C9F7F7375BF55C63B96134F94F3AED12935EEBEB21C661FDA1F53A3DB64604AFCD492850198FD175A3837D483F7697982DFD6C97D3D8337CAADF4FC05DC2
	DFCCB356ABE334BE6257CD6875EFE528578D6007814583AD85DA8D14B30674EA7A38EAB83DCE033FF841D91AFABABCD62FFC22476134C9DEC53F4AB3C36927C7225F515B6EA76C92D325354B5CD9EF99B76A58E5EE4CBF98CB4688FB9C9D63370B105D93D4F66B18416B41776F986E339968079EF84FD0FCD4708FF1FE3206AFFCFC8E77DBAC27C11DA50C7C36E58B695010954E561AFF232BC35F42B54648B0B11F04304035DDD20EB1E4A570B7G950EEDB3D4GB5G5901EE0C25759D7F7101DB745BC64D5A0A
	3E97CA0B60A97549DE6BE917CF363ABA25AF2DDE073543A979185266F2A94E3DE3C0BF096F27CE10711964123CD65961F6416856EE899C49A75A38CA73300DD762354D0EEE8984F4FB980DFB3D34G1ECE2BCF7E3347E615A5FF8BD1FF3394EDF95D7AA50C888340FB4AD83479BC9E33A261BD91C8586A0D8A6419425B8C2561E4177FA84D076799F0EC49D9DBDB676844E9D83D7DA47F0DB20CA552F58807F512C7F259604527543DDD762E5FAED75C6A1C59BE64639EB989937729FC633EBC4107B134D6267AAD
	3C5EAA4BDEC7C70FACB5C9326CF0F5797B976D43397886ECBF9FD37678FE74312320D7B310CD556F51FD859A7D9A5C6EEBBD1EE6DCB945CE7773B85B876DC705D35E2B4C40BC1890CEFE7AFA8B79E95D6649AC7745B7F57D94A9A5DC78672A70403DE661E37AE5A431961D7E89617A7B6EF6C94878392E0C4C6FE1FCA4E177CB87506E399160D3C07591E4771777950B7DF6426A13E8071D77420AC9B63F170936296541FD6C25F2FD9FDBD12E6FE33FAC9F5A07DEAD5773A1D1776F6518DB5C3DAE0DCF7E26BC1F
	6FE117CB6213D63BB001ABDEEFC6DBB8DC8E79024356A549CD0E2FA546C6C620EF2571BDF8995E658872255E5D64CBA5DFCFE0D16D293A792CEF722F0DA9914297D6C678FDC94CE9FD855ADCF2CA5DE028136E9E17AC787F45773742D7624B5A7D7AED5F5A0F7EF8CD6AD75A3D3D5F48B00EB8FBA4D537F86BFBE8E32F239B168AB72C026DA61D51E2BB96897884F0E089BCADC022EFEDAB4A5522A961AF0F18115F886DECC0B7A249DF2ED7E2BD6DD345D1CE3A1DCEE1F6F5BDBA8F18F07D242FFAA4884338114F
	28BADCBD109FD07725220F28779EC0FD1C1A98909A097A3C2A62BB4C9102E7B0A0AF9B96ADAF1BF26ED91396075679D66B2E9DD2FA24D833B51101796A3903B8EE6EB840D463C877578EE8E354F11F4F5D696069C0D907B7B8FE6158752A753A352F52578BBEDDDB7BF92395FA883C3839014F9748BC0E7062A7675156F6A807G052972A24D037DE402F993BC2FB83CBE40B9B6290FEC761899F5477C233089179D525AA2EA65722036089FA7EC10BCAE5096F9D5B80F1540DF274EED5741BC0DADGC3C23A6D02
	152279249E4C94FE2B14538EE6135FB20AFB798D0138A6E73CBF2E6E98BFB8468A7C92476DE59E9F08EB1E627CE36383F14D27678957B4C2DD42F84A339B2BD12F6D0FC2CCFC1474F29C5A2C512B516D4063856535AA5CFBEE2F234B61A25EEFB98F90B48FC6A2D71571B53640188DC002E7BC07BC8F14C7BECA39E556F95C5B31EAF99EAF272865D367837382367DBEF202BF77348D1271FD651DAD2A5DF22BF1BD7A543209177B95D9856CD803117BEE3F3B870C5D9A05763A50C031EBAD681C83BCB3D0DD1448
	7FE23F4D6C45B8F185009AEC33A60F5449AC93306F076728EFB434CF84CA87DA8F349D6A054CF006B128C13AA2FF6168324BCD9E27C346EDCA6762574FEBE3E74884A1B7861EF30104DFFD57C0E742B46065CE24F99D3C0CD8D70D831556DE393676B32F43F6425A554035943160D96EA7583FEEA24502B77918B7209C8A94BB1172D15FDEBABB46CE89E795A1EF691EE3131AD79813A78A9FD9BD513F0FE3606CA8E41EBCC7B2CBA63599DAA7A16F2BF305816B1F878BD289CFD1BFE9C0BD6E7BB2262DFFF1C0BD
	4E775B9CED7D46735A7AECCDFF215BF3FBC8378D4F453006A7D785BD23C7CF92BAADD51E0919DB0C6B9E38F71330FBC465G3B7B632F3A3A4286FB9BF3947FE751E477CE935A5D8265C6A0FBB46D417B864528DB5BD78F5DE13F145B6EA7E25F33E7F17EA151A276E5287CD44E3FDF43973E7B0E6A3BE1D037A11AE262429A747B4B13A167830582458225G2D1ECCB17261EAB39B660C566C76B4GD0F4EA0E8FC23F7AF3F7F60EDE3011FCA4E14EC4967643E863FBC1FCE443E4619BBB2608E723E10B911FC23F
	BF9FC17D9A204E8EF4BFD0B4D0A2D06A9452EFC5F505506F144DA19F17E5EB271DE7E0D26ECB07546BE7E360913A15B7E23FD5D374E3747AGFE6EE3106F799A3D44FD2F78B131C6596A5A3D4279559A3ED8BB8767E3399E6AE29FA3DF7D71D9267A514FB46F99E7754E975F9789999F5CD599E2DD1E2B89FE7EF821C67F7C11DE23FF7E78C54D50670FDF5584BFFF3CD623BDFF883F0A30E00CB3453499AA009A01ECC0FDB164D35F9C5EA5FCCA471FCE410164241B5FD848A43F6DC8F0DD25A3435FF3380EA0CE
	6CF04B323B9B47A73EFC8457CBF6FBB8D3707A0F200D3C1887C5D3057F5D910C13CE48114896B866840F1D2120D7EC8CE11BD397895BA429BC23095FE1BC8E76F81C7CF385679D0332CD652D663C91D00ED1F9EED320AFE11FBB759DDBC0DFA1E364A099FF8D6E49567557C91660EBB855B27C9A4E32F07C8F36EF6E77C80217E86198744A951F247A4ABC0BC9651D76B53AFD8EBC14881CF3319163244DF15D818E4C8F3D4B9F67B91F2FA32EFB8A146DD31157FA31CE19FF71544176267C1129D0B53429877A9F
	255CBA551FE3046C8FCDDB7D329B5C1D622CA4649F666BBE8E9A47CFA5CCE617F02C13F629735A89F7DF33D712B052724313BAE6298DF11BB82F56B9FC9EC00DCA83015BBE48536236A4989B71F44EB4B8478109B65373834F4558269A6A6B2751DC0EDFC4BD2A99651F28DE6DB9EA7B26D7021E23AECF93361315E7C94D9623409D8FDC2433DFAC547D0E4B01B38F233F351F6A71D6CD43EFC00BDA19BA17FF4A25DC38E3DA84ABF9A95A38BB48DCCAE3459C7AE27DD830F97A6B19C24733A6FC9EB29FD64E2FE9
	3EDA267F6D2513A7FA7C72D259A3BB60E094E411041EDF6451B9BDBE167055BBF91833D6C31945818E0F236FA73F4AD74F43F1543627081F07219C829496C7586429CB58B69E4AA9F194D79BBFA9076717562B8A8A41146D7672488A4CC62F5BA3F965FE4165378C4A7E9FFDD10BC35640F8EC7A50B1C6587E793FE87DE06D5894A379FCE07BC2E53C62690167E9CBBB6E33E660FB00C201E2011200520156G3199F046G2AG2A83EA81F28205G0D820A86CA1CC1B64839D4B6048D140387CFB0DE2BB84F8A9B
	DC489B8A0B254C88F67EF667637A2C1C9108B57BB8FF4D0CC02C1911CFD8F3BD543546D31CFFB5BFD0FED23CDFE6DAFCB0797F4A7DE3F5FCA0DEDA47799B62837152B7C50417CCB35B8C72CC325B5CCBB8260A8104FBA8B1F551A54C67F8DF6830BA9DDF735055848DDDDDA23FBD4E77F27ACCA1CB7026F35EDA05A756FA7CE5DCF9B1666936BD415689D04067F0E4B95FF1D56A141B2DDDEA1F067660B92429FD789C525A2E0F83BA5A8763002EF6FD9C505D2E45814A9DDDBB5A338BFC2BC5ED572B72AE2A3C9B
	FCCC64DDF058E43B4A3F174F8B7975928AA47EC8BE2FDE4EEBD1F88FE8F897955E68F64C0320325D6D253EE35A717E640A43699465885E46610234285AA30A4F516131EA66BDD965DDD4575215C718A6778972579ECCBD4A5E3929F95F18FF7771F571DD15A1F4B69470FBAB5037618942F17794129DBC2A9DFEDD443FD9C1B9D1653D4DF929D0DE27726EA99AEE4E58BFD8DD6490F5FE3B048FABA3901BE9E9B84CA77222F5EFAED3E381F7E3415F5137594FA7B7E6C7E7215D6E4BABD64658BEEB309E958ACF64
	61778B865FFF198B88230C18A56E3FA26199BFCB60E4A1A32C10E4AC551131FC008C5387089185EE0243CE0F07E7ECD1F75804FB31595A51A15934F7E0955B82F1924ADE8B63E6A590167A69CEED1B9D8922CDBDBCDB93A8474765D10E179388DF7DF39E6ED50CA9A22FDEB5E9B14D5C7E59C6BC1F696555588421D7FA02BF2F62D3447A16824AB526A7E19ECF529C228A685EAB64C94299BF61ED93211C8A34DAED3B3708664BE653FC5B0A306CD556D83433EDA38CED19CDE3366C665F5C21BC92A8EAB65D8916
	67FC48F46F2634E6D746FD7EA342CF096A38997C5B7CDAA867BC55E6A8FE0A76589383F23885788DCF8D3DEF143B6F8E741D4B507686D094D0A2D08A50AA2075C026C44A13455621B016FA3926C1984A5DF2411D5D01B96F305F645B6EA7F04309C234EDC5E2A01EB943791644C0BC13267AD89D5445A452DA1497E96567BE8D6B74742D44B047B66AD72D342F5774EB05F7CF40B8E2FEA95C57C6BC9D0803DE627C302783F1D05BEE42C131D0E71AC373CE7BE8A83B49F30261309E6E13A367845A6DA667074E89
	345BC6C6F60B023A2C3F279C33AC0F3094828D417BC99EE1297F4C26BC1D5F919C8B3D5FB1BC965A55210F05CABB86E3217D9D7AD8282AE3B0963270F13DD297E044F356EEF55CBFF6E0FE90F17D2C55E56D6277C54ABDA842CB657DFFB56FAF975CFDAC7146DD15215C9395511A1ED6577433A26D1A8ADE880F21F67005912ABF54E5A1AF8C4A31AAEFD1B66110D41537510D3C6239ED864BDC726597F6A32F814AF615370273C6C0B9DA6559F65F0E8DF03CC01EDD07B7504E5F5E168CBDACB294F691B6F875A3
	324BD2D537EF185F1FC6E85EB76B7E4E647B752D3DF7D5064845E336613E72F825AB1257AB5944CFB914FB25F86ECF5B673599CA66D16E7ECB56E0FC654DA25C52B8CF60ABBBBCEFGA1069577E1AFE753FDD8683C616F43E2204D5CF97E7B3054F90177E1BB5DF897578D00313B27DBF397F7187B71F2E83FDA1D730FAAD00672FBB469C48F44AC970D73E6A56A5FA4DB3D72A9170D6E479A1F41313616E83F3595BEA374B97A8C6135B3A6ADDE8BD36A5360397A99525B300B74E67375754E54D0A134A91D6F57
	7B68FC3F5E626EEADDA7D3ED70C9965EF139245EE27AB4DAD9676816DC78233852A3072BAA0A8F5714A979A2BC0B72C5BD0C99B21F7059ABD90465926793BEB2E5F3FC84651CA4483BC95476BDA3E1B9CB5270F7EE61E3716FF1654E2D845DCA516133ABFE9DFE3FFB889D8E194D9545D5200340FDDB1B90EF6960BEDAD285737D29046669C922EF3E5F7EC97B5B65482DAB02FE730AC9D23E0BA679B1717AE5F14AF75115178E30DB79CD75C00F92737A01093E1566ACA0DB6F5C49FFE7816596A04F8232F555AE
	52E164025B3D5F1C0DD79ECA8C094A3AB37CB77D8E7B7D4BED7793B8E78477392485017867814ECFD99008FF0A32491F53216E68C20A57A623D6FE947093965E8AEEBCD322B76F25CA5F65BAE3886CBF550EB1E2AD5467AE02B889F49428F591617D3F3BB26CEF2CCF758D719B6B11A61A570DC50367B08AF8510B74706CE86ED7090B826D994179490B826DD932136C198AF55549E4CF4BAE2DDD42009F1BFCAB767C1BEC3F8FBE24F95F11FD7773E64DDD15A13042474641F71695C66D1D2560F570D81484B6CB
	CDA62C75EC89610C654964A7CF58036371276D7A5FE3675B753F47AE368F7D1B639F5B03FD2F9573FB1447290A45F066DFCCF32EB6531CDBD55E3DB711978B65644564AFE1BB7DEBBDC1733EEF675DDF7713F7D50650797CAEDA77773308573C0B567D35EC4AEB7DA5F8A7A3432209187C69D5340540843FA8A1CC103EF8F8AC34865A30A58293707D3AA4908B4DB68547F04F7335AC06BE95CB88131CB1137C16A50365E78648F7C21B90C5BE5AB9F4107C31B7753158CC6E9FD150BEF68965352DFBA82F25AF51
	4BEBABF5729A61065EEB7E774988539479E566E29694C3D455G06B8D4F330CC483FE1A67926E7EFB72FF63A7C72FF68764B4F58B9840629C28CF3382ACC715142BD5AF8DA8FF3E8FD76D66269BDE6EDBF8F7499B1CCBF611B65FB8647A9731E41F1AA4CCC6D3E4AA45EB8B3350B0327781F3B35053B20D36CEBAC5B67E5DBD5FDCF738F83AB2FD755E67EF2D22601E51DE6C6F1B9F01AE561897FB44B6697G27D9266C767461BFFBE4E989C64B9FEC3D63FF7B7317E25B6B8A8D4CG835AA5B1C2F6AF5E9B73B1
	0EB3E6F84777953B4997EA600848457F78438F3ACB755B48ECAB9D8B0A45FD6EB0E33D83FE5F26369970273679945AD450B74E326F345FB8536FDB9474DB373CCC2C6748E5015FB83F626730E4602FDCC679E2C151FE967C7BD7D3375569142D4EEB47DDB67C96F622DF167C79617AB69C5FC7ED14CAD1776DB61C77D5CD1F7F87D0CB87884346DB4C5F93GG48B9GGD0CB818294G94G88G88GF0F047B04346DB4C5F93GG48B9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4
	E1F4E1D0CB8586GGGG81G81GBAGGG9994GGGG
**end of data**/
}


/**
 * Return the CheckBoxTreeViewPanel property value.
 * @return com.cannontech.common.gui.util.CheckBoxJTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.CheckBoxTreeViewPanel getCheckBoxTreeViewPanel() {
	if (ivjCheckBoxTreeViewPanel == null) {
		try {
			ivjCheckBoxTreeViewPanel = new com.cannontech.common.gui.util.CheckBoxTreeViewPanel();
			ivjCheckBoxTreeViewPanel.setName("CheckBoxTreeViewPanel");
			// user code begin {1}
			ivjCheckBoxTreeViewPanel.setStoreCheckedNodes(true);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCheckBoxTreeViewPanel;
}
	/* (non-Javadoc)
	 * @see com.cannontech.database.cache.DBChangeListener#getClientConnection()
	 */
	public ClientConnection getClientConnection()
	{
		if( connToDispatch == null)
			connect();	
		return connToDispatch;
	}
	/**
	 * @return
	 */
	public Date getEndDate()
	{
		return endDate;
	}
	private FileMenu getFileMenu()
	{
		if (fileMenu == null)
		{
			fileMenu = new FileMenu();
			addMenuItemActionListeners(fileMenu);
		}
		return fileMenu;
	}
/**
 * Return the GenerateButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getGenerateButton() {
	if (ivjGenerateButton == null) {
		try {
			ivjGenerateButton = new javax.swing.JButton();
			ivjGenerateButton.setName("GenerateButton");
			ivjGenerateButton.setText("Generate Report");
			// user code begin {1}
			ivjGenerateButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenerateButton;
}
	/**
	 * Return the LeftRightSplitPane property value.
	 * @return javax.swing.JSplitPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JSplitPane getLeftRightSplitPane() {
	if (ivjLeftRightSplitPane == null) {
		try {
			ivjLeftRightSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjLeftRightSplitPane.setName("LeftRightSplitPane");
			ivjLeftRightSplitPane.setDividerSize(4);
			ivjLeftRightSplitPane.setDividerLocation(206);
			getLeftRightSplitPane().add(getCheckBoxTreeViewPanel(), "left");
			getLeftRightSplitPane().add(getReportDisplayPanel(), "right");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLeftRightSplitPane;
}
	private int[] getLiteIDsFromNodes()
	{	
		Vector nodes = new Vector();
		//contains userObject(LiteBase) values
		if ( getCheckBoxTreeViewPanel().getTree().getModel() instanceof CheckBoxDBTreeModel)
			nodes = ((CheckBoxDBTreeModel)getCheckBoxTreeViewPanel().getTree().getModel()).getCheckedNodes();

		if( !nodes.isEmpty())
		{
			NativeIntVector liteIDs = new NativeIntVector(nodes.size());
			for (int i = 0; i < nodes.size(); i++)
			{
				if( nodes.get(i) instanceof LiteBase)
				{
					LiteBase liteBase = (LiteBase) nodes.get(i);
					liteIDs.addElement(liteBase.getLiteID());
				}
			}
			
			return liteIDs.toArray();
		}
		return null;
	}
	private javax.swing.JMenuBar getMenuBar()
	{
		if (menuBar == null)
		{
			try
			{
				menuBar = new javax.swing.JMenuBar();
				menuBar.add(getFileMenu());
				menuBar.add(getReportsMenu());
				menuBar.add(getHelpMenu());
			}
			catch (java.lang.Throwable ivjExc)
			{
				CTILogger.info(" Throwable Exception in getMenuBar()");
				ivjExc.printStackTrace();
			}
		}
		return menuBar;
	}
	/**
	 * Return the ReportDisplayPanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getReportDisplayPanel() {
	if (ivjReportDisplayPanel == null) {
		try {
			ivjReportDisplayPanel = new javax.swing.JPanel();
			ivjReportDisplayPanel.setName("ReportDisplayPanel");
			ivjReportDisplayPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsReportSetupPanel = new java.awt.GridBagConstraints();
			constraintsReportSetupPanel.gridx = 0; constraintsReportSetupPanel.gridy = 0;
			constraintsReportSetupPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReportSetupPanel.weightx = 1.0;
			constraintsReportSetupPanel.weighty = 0.05;
			constraintsReportSetupPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getReportDisplayPanel().add(getReportSetupPanel(), constraintsReportSetupPanel);

			java.awt.GridBagConstraints constraintsTabbedPane = new java.awt.GridBagConstraints();
			constraintsTabbedPane.gridx = 0; constraintsTabbedPane.gridy = 1;
			constraintsTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTabbedPane.weightx = 1.0;
			constraintsTabbedPane.weighty = 1.0;
			constraintsTabbedPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getReportDisplayPanel().add(getTabbedPane(), constraintsTabbedPane);
			// user code begin {1}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReportDisplayPanel;
}
	/**
	 * Called whenever the part throws an exception.
	 * @param exception java.lang.Throwable
	 */

	private javax.swing.JFrame getReportParentFrame()
	{
		return reportClientFrame;
	}
	/**
	 * Return the ReportSetupPanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getReportSetupPanel() {
	if (ivjReportSetupPanel == null) {
		try {
			ivjReportSetupPanel = new javax.swing.JPanel();
			ivjReportSetupPanel.setName("ReportSetupPanel");
			ivjReportSetupPanel.setPreferredSize(new java.awt.Dimension(763, 65));
			ivjReportSetupPanel.setLayout(new java.awt.GridBagLayout());
			ivjReportSetupPanel.setMinimumSize(new java.awt.Dimension(200, 35));

			java.awt.GridBagConstraints constraintsGenerateButton = new java.awt.GridBagConstraints();
			constraintsGenerateButton.gridx = 0; constraintsGenerateButton.gridy = 0;
			constraintsGenerateButton.weightx = 0.5;
			constraintsGenerateButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getReportSetupPanel().add(getGenerateButton(), constraintsGenerateButton);

			java.awt.GridBagConstraints constraintsStartEndPanel = new java.awt.GridBagConstraints();
			constraintsStartEndPanel.gridx = 1; constraintsStartEndPanel.gridy = 0;
			constraintsStartEndPanel.fill = java.awt.GridBagConstraints.VERTICAL;
			constraintsStartEndPanel.weightx = 1.0;
			constraintsStartEndPanel.weighty = 1.0;
			getReportSetupPanel().add(getStartEndPanel(), constraintsStartEndPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReportSetupPanel;
}

	private ReportsMenu getReportsMenu()
	{
		if (reportsMenu == null)
		{
			reportsMenu = new ReportsMenu();
			addMenuItemActionListeners(reportsMenu);
		}
		return reportsMenu;
	}
	
	private HelpMenu getHelpMenu()
	{
		if (helpMenu == null)
		{
			helpMenu = new HelpMenu();
			addMenuItemActionListeners(helpMenu);
		}
		return helpMenu;
	}
	
	/**
	 * @return
	 */
	public Date getStartDate()
	{
		return startDate;
	}
	/**
	 * Return the StartEndPanel property value.
	 * @return com.cannontech.pooky.test.StartEndPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private StartEndPanel getStartEndPanel() {
	if (ivjStartEndPanel == null) {
		try {
			ivjStartEndPanel = new com.cannontech.analysis.gui.StartEndPanel();
			ivjStartEndPanel.setName("StartEndPanel");
			ivjStartEndPanel.setPreferredSize(new java.awt.Dimension(350, 251));
			ivjStartEndPanel.setMinimumSize(new java.awt.Dimension(300, 63));
			// user code begin {1}
				ivjStartEndPanel.getStartDateComboBox().setSelectedDate(getStartDate());
				ivjStartEndPanel.getEndDateComboBox().setSelectedDate(getEndDate());
				ivjStartEndPanel.getStartDateComboBox().addActionListener(this);
				ivjStartEndPanel.getEndDateComboBox().addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartEndPanel;
}


	private String[] getStringsFromNodes()
	{	
		//contains userObject(LiteBase) values
		Vector nodes = new Vector();
		if ( getCheckBoxTreeViewPanel().getTree().getModel() instanceof CheckBoxDBTreeModel)
			nodes = ((CheckBoxDBTreeModel)getCheckBoxTreeViewPanel().getTree().getModel()).getCheckedNodes();
		
		if( !nodes.isEmpty())
		{
			Vector strings = new Vector(nodes.size());			
			for (int i = 0; i < nodes.size(); i++)
			{
				if( (nodes.get(i) instanceof String))
				{
					String str  = (String) nodes.get(i);
					strings.addElement(str.toString());
				}
			}
			return (String[])strings.toArray();
		}
		return null;
	}
	/**
	 * Return the TabbedPane property value.
	 * @return javax.swing.JTabbedPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTabbedPane getTabbedPane() {
	if (ivjTabbedPane == null) {
		try {
			ivjTabbedPane = new javax.swing.JTabbedPane();
			ivjTabbedPane.setName("TabbedPane");
			// user code begin {1}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTabbedPane;
}
	/* (non-Javadoc)
	 * @see com.cannontech.database.cache.DBChangeListener#handleDBChangeMsg(com.cannontech.message.dispatch.message.DBChangeMsg, com.cannontech.database.data.lite.LiteBase)
	 */
	public void handleDBChangeMsg(DBChangeMsg msg, LiteBase treeObject)
	{
		if (!((DBChangeMsg)msg).getSource().equals(CtiUtilities.DEFAULT_MSG_SOURCE))
		{
			CTILogger.info(" ## DBChangeMsg ##\n" + msg);
			((DeviceCheckBoxTreeModel)getCheckBoxTreeViewPanel().getTree().getModel()).update();

			// Refreshes the device tree panel in the GraphClient. (Main Frame)	
			Object sel = getCheckBoxTreeViewPanel().getSelectedItem();
			getCheckBoxTreeViewPanel().refresh();

			if( sel != null )
				getCheckBoxTreeViewPanel().selectByString(sel.toString());
		}
	}
	private void handleException(java.lang.Throwable exception)
	{

		/* Uncomment the following lines to print uncaught exceptions to stdout */
		// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
		// exception.printStackTrace(System.out);
	}
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ReportClient");
		setLayout(new java.awt.GridBagLayout());
		setSize(1086, 776);

		java.awt.GridBagConstraints constraintsLeftRightSplitPane = new java.awt.GridBagConstraints();
		constraintsLeftRightSplitPane.gridx = 0; constraintsLeftRightSplitPane.gridy = 0;
		constraintsLeftRightSplitPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLeftRightSplitPane.weightx = 1.0;
		constraintsLeftRightSplitPane.weighty = 1.0;
		constraintsLeftRightSplitPane.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLeftRightSplitPane(), constraintsLeftRightSplitPane);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
	private void loadTreeModels( int[] newModels )
	{
		//save the old model in the hash map.
		LiteBaseTreeModel[] models = getCheckBoxTreeViewPanel().getTreeModels();
		if (models != null)
		{
			for (int i = 0; i < models.length; i++)
			{
				Integer key = new Integer(currentModels[i]);	//key of current(soon to be old) model to save
				modelMap.put(key, models[i]);
			}
		}
			
		models = new LiteBaseTreeModel[newModels.length];
		for (int i = 0; i < newModels.length; i++)
		{
			Integer key = new Integer(newModels[i]);
			LiteBaseTreeModel tempModel = (LiteBaseTreeModel)modelMap.get(key);
			if( tempModel == null)
			{
				tempModel = ModelFactory.create(newModels[i]);
			}
			models[i] = tempModel;
		}
		currentModels = newModels;	//save the last model types loaded.
		getCheckBoxTreeViewPanel().setTreeModels(models);		
	}
	/**
	 * @param message
	 * @param messageType
	 */
	public void showPopupMessage(String message, int messageType )
	{
		javax.swing.JFrame popupFrame = new javax.swing.JFrame();
		popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("ReportIcon.gif"));
		javax.swing.JOptionPane.showMessageDialog(popupFrame,
		message, "Yukon Reporting", messageType);
		return;
	}	
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args)
	{
		try
		{
			System.setProperty("cti.app.name", "Reporting");
			System.setProperty("org.jfree.report.LogLevel", "INFO");
			javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());

			javax.swing.JFrame mainFrame = new javax.swing.JFrame();
			mainFrame.setIconImage( java.awt.Toolkit.getDefaultToolkit().getImage("GraphIcon.gif"));
			mainFrame.setTitle("Yukon Reporting");
        
			SplashWindow splash = new SplashWindow( mainFrame, "ctismall.gif", "Loading " + System.getProperty("cti.app.name") + "...", new Font("dialog", Font.BOLD, 14 ), Color.black, Color.blue, 2 );
        
//			ClientSession session = ClientSession.getInstance(); 
//			if(!session.establishSession(mainFrame))
//				System.exit(-1);			
//	  	
//			if(session == null) 		
//				System.exit(-1);
//				
//			if(!session.checkRole(ReportingRole.ROLEID)) 
//			{
//			  JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
//			  System.exit(-1);				
//			}
			
			java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			mainFrame.setSize((int) (d.width * .85), (int) (d.height * .85));
			mainFrame.setLocation((int) (d.width * .05), (int) (d.height * .05));

			ReportClient rc = new ReportClient(mainFrame);
			mainFrame.setContentPane(rc);
			mainFrame.setJMenuBar(rc.getMenuBar());
			mainFrame.setVisible(true);
			// Add the Window Closing Listener.
			mainFrame.addWindowListener(new java.awt.event.WindowAdapter()
			{
				public void windowClosing(java.awt.event.WindowEvent e)
				{
					System.exit(0);
				};
			});
		}
		catch (Exception e)
		{
			e.printStackTrace( System.err );
			System.exit(-1);    	
		}
	}
	/**
	 * @param date
	 */
	public void setEndDate(Date date)
	{
		endDate = date;
	}
	/**
	 * @param frame
	 */
	public void setReportClientFrame(javax.swing.JFrame frame)
	{
		reportClientFrame = frame;
	}
	/**
	 * @param date
	 */
	public void setStartDate(Date date)
	{
		startDate = date;
	}
	/**
	 * @return
	 */
	public ReportModelBase getModel()
	{
		return model;
	}

	/**
	 * @param base
	 */
	public void setModel(ReportModelBase base)
	{
		model = base;
	}
}

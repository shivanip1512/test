package com.cannontech.analysis.gui;

/**
 * Insert the type's description here.
 * Creation date: (11/3/2003 10:53:31 AM)
 * @author: 
 */

import java.awt.event.ActionEvent;
//import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.PowerFailModel;
import com.cannontech.analysis.tablemodel.DisconnectModel;
import com.cannontech.analysis.tablemodel.MissedMeterModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.gui.util.CheckBoxTreeViewPanel;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.model.ModelFactory;

public class ReportClient
	extends javax.swing.JPanel
	implements java.awt.event.ActionListener
{
	private javax.swing.JSplitPane ivjLeftRightSplitPane = null;
	private javax.swing.JPanel ivjReportDisplayPanel = null;
	private javax.swing.JPanel ivjReportSetupPanel = null;
	private javax.swing.JPanel ivjPage = null;
	private StartEndPanel ivjStartEndPanel = null;
	private javax.swing.JTabbedPane ivjTabbedPane = null;
	private static javax.swing.JFrame reportClientFrame = null;
	private javax.swing.JMenuBar menuBar = null;
	private Reportmenu reportMenu = null;
	private String reportType = null;
	private static final int[] statisticModels =
		{
			ModelFactory.MCT_CHECKBOX,
			ModelFactory.COMMCHANNEL_CHECKBOX,
			ModelFactory.DEVICE_CHECKBOX,
			ModelFactory.TRANSMITTER_CHECKBOX };
	private static final int[] disconnectModels = { ModelFactory.COLLECTIONGROUP_CHECKBOX};
	private static final int[] powerFailModels = { ModelFactory.COLLECTIONGROUP_CHECKBOX};
	private static final int[] loadGroupModels = { ModelFactory.LMGROUP_CHECKBOX };
	private static final int[] AMRModels = { ModelFactory.COLLECTIONGROUP_CHECKBOX };
	private CheckBoxTreeViewPanel ivjCheckBoxTreeViewPanel = null;
	private javax.swing.JButton ivjGenerateReportButton = null;
	private ReportModelBase model = null;
	

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
		if (event.getSource() == getReportMenu().getTodayMenuItem())
		{
			
			com.cannontech.database.model.LiteBaseTreeModel[] models = 	
				new com.cannontech.database.model.LiteBaseTreeModel[statisticModels.length];
			
			for (int i = 0; i < models.length; i++)
			{
				if (statisticModels[i] == ModelFactory.MCT_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.MCTCheckBoxTreeModel(false);
				}				

				else if (statisticModels[i] == ModelFactory.COMMCHANNEL_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.CommChannelCheckBoxTreeModel();
				}
				else if (statisticModels[i] == ModelFactory.DEVICE_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.DeviceCheckBoxTreeModel(false);
											
				}
				else if (statisticModels[i] == ModelFactory.TRANSMITTER_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.TransmitterCheckBoxTreeModel(false);
									
				}
				getCheckBoxTreeViewPanel().setTreeModels(models);
				
			}
			model = new StatisticModel("Daily");
		}
		
		else if (event.getSource() == getReportMenu().getYesterdayMenuItem())
			{
				com.cannontech.database.model.LiteBaseTreeModel[] models = 	
								new com.cannontech.database.model.LiteBaseTreeModel[statisticModels.length];
				
				model = new StatisticModel();
					
				for (int i = 0; i < models.length; i++)
				{
					if (statisticModels[i] == ModelFactory.MCT_CHECKBOX)
					{
						models[i] = new com.cannontech.database.model.MCTCheckBoxTreeModel(false);
												
					}				

					else if (statisticModels[i] == ModelFactory.COMMCHANNEL_CHECKBOX)
					{
						models[i] = new com.cannontech.database.model.CommChannelCheckBoxTreeModel();
						
					}
					else if (statisticModels[i] == ModelFactory.DEVICE_CHECKBOX)
					{
						models[i] = new com.cannontech.database.model.DeviceCheckBoxTreeModel(false);
												
					}
					else if (statisticModels[i] == ModelFactory.TRANSMITTER_CHECKBOX)
					{
						models[i] = new com.cannontech.database.model.TransmitterCheckBoxTreeModel(false);
										
					}
					
					getCheckBoxTreeViewPanel().setTreeModels(models);
					
				}
				model = new StatisticModel("Yesterday");
			}
		
		else if (event.getSource() == getReportMenu().getCurrentMonthMenuItem())
			{
				com.cannontech.database.model.LiteBaseTreeModel[] models = 	
								new com.cannontech.database.model.LiteBaseTreeModel[statisticModels.length];
		
				for (int i = 0; i < models.length; i++)
				{
					if (statisticModels[i] == ModelFactory.MCT_CHECKBOX)
					{
						models[i] = new com.cannontech.database.model.MCTCheckBoxTreeModel(false);
												
					}
					
					else if (statisticModels[i] == ModelFactory.COMMCHANNEL_CHECKBOX)
					{
						models[i] = new com.cannontech.database.model.CommChannelCheckBoxTreeModel();
	
					}				
	
					else if (statisticModels[i] == ModelFactory.DEVICE_CHECKBOX)
					{
						models[i] = new com.cannontech.database.model.DeviceCheckBoxTreeModel(false);
												
					}
					else if (statisticModels[i] == ModelFactory.TRANSMITTER_CHECKBOX)
					{
						models[i] = new com.cannontech.database.model.TransmitterCheckBoxTreeModel(false);
										
					}
					getCheckBoxTreeViewPanel().setTreeModels(models);
						
				}
				model = new StatisticModel("Monthly");
			}
			
		else if (event.getSource() == getReportMenu().getLastMonthMenuItem())
		{
			com.cannontech.database.model.LiteBaseTreeModel[] models = 	
							new com.cannontech.database.model.LiteBaseTreeModel[statisticModels.length];

			for (int i = 0; i < models.length; i++)
			{
				if (statisticModels[i] == ModelFactory.MCT_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.MCTCheckBoxTreeModel(false);
					
				}
				else if (statisticModels[i] == ModelFactory.COMMCHANNEL_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.CommChannelCheckBoxTreeModel();

				}				

				else if (statisticModels[i] == ModelFactory.DEVICE_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.DeviceCheckBoxTreeModel(false);
									
				}
				else if (statisticModels[i] == ModelFactory.TRANSMITTER_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.TransmitterCheckBoxTreeModel(false);
							
				}
				getCheckBoxTreeViewPanel().setTreeModels(models);
			
			}
			model = new StatisticModel("LastMonth");
		}
		else if (event.getSource() == getReportMenu().getLoadGroupAcctingMenuItem())
		{
			com.cannontech.database.model.LiteBaseTreeModel[] models =
				new com.cannontech.database.model.LiteBaseTreeModel[loadGroupModels.length];

			for (int i = 0; i < models.length; i++)
			{
				if (loadGroupModels[i] == ModelFactory.LMGROUP_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.LMGroupsCheckBoxModel(false);
				}
			}
			{
				getCheckBoxTreeViewPanel().setTreeModels(models);
				model = new LoadGroupModel();
			}
		}
		//else if (event.getSource() == getReportMenu().getControlLogMenuItem())
		//{
					//com.cannontech.database.model.LiteBaseTreeModel[] models =
					//	new com.cannontech.database.model.LiteBaseTreeModel[loadGroupModels.length];

					//for (int i = 0; i < models.length; i++)
					//{
					//	if (loadGroupModels[i] == ModelFactory.LMGROUP_CHECKBOX)
					//	{
					//		models[i] = new com.cannontech.database.model.LMGroupsCheckBoxModel(false);
					//	}
					//}
					//{
						//getCheckBoxTreeViewPanel().setTreeModels(models);
						//data = new SystemLogModel();
					//}
		//		}

		else if (event.getSource() == getReportMenu().getPowerFailMenuItem())
		{
			com.cannontech.database.model.LiteBaseTreeModel[] models = new com.cannontech.database.model.LiteBaseTreeModel[powerFailModels.length];

			for (int i = 0; i < models.length; i++)
			{
				if (powerFailModels[i] == ModelFactory.COLLECTIONGROUP_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.CollectionGroupCheckBoxTreeModel();
				}
				
			}
			getCheckBoxTreeViewPanel().setTreeModels(models);
			model = new PowerFailModel();
		}

		else if ( event.getSource() == getReportMenu().getHistoryMenuItem())
		{
			com.cannontech.database.model.LiteBaseTreeModel[] models = new com.cannontech.database.model.LiteBaseTreeModel[disconnectModels.length];

			for (int i = 0; i < models.length; i++)
			{
				if (disconnectModels[i] == ModelFactory.COLLECTIONGROUP_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.CollectionGroupCheckBoxTreeModel();
				}

			}
			getCheckBoxTreeViewPanel().setTreeModels(models);
			model = new DisconnectModel("History");
		}

		else if (event.getSource() == getReportMenu().getMissedMeterMenuItem()
				|| event.getSource() == getReportMenu().getSuccessMenuItem())
		{
			com.cannontech.database.model.LiteBaseTreeModel[] models =
				new com.cannontech.database.model.LiteBaseTreeModel[AMRModels.length];

			for (int i = 0; i < models.length; i++)
			{
				if (AMRModels[i] == ModelFactory.COLLECTIONGROUP_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.CollectionGroupCheckBoxTreeModel();
				}

			}
			getCheckBoxTreeViewPanel().setTreeModels(models);
			model = new MissedMeterModel();
		}

		else if (event.getSource() == getGenerateReportButton())
		{
			model.setData(null);
			if (model instanceof LoadGroupModel)
			{

				long startTime = getStartEndPanel().getStartDateComboBox().getSelectedDate().getTime();
				long endTime = getStartEndPanel().getEndDateComboBox().getSelectedDate().getTime();

				DefaultMutableTreeNode[] nodes = getCheckBoxTreeViewPanel().getSelectedNodes();

				com.cannontech.clientutils.CTILogger.info(nodes.length);
				int loadGroupIDs[] = new int[nodes.length];

				for (int i = 0; i < nodes.length; i++)
				{
					LiteYukonPAObject tempID = (LiteYukonPAObject) nodes[i].getUserObject();
					loadGroupIDs[i] = tempID.getLiteID();
					com.cannontech.clientutils.CTILogger.info("ID =  " + loadGroupIDs[i]);

				}
				
				model.setPaoIDs(loadGroupIDs);
				
				model.setStartTime(startTime);
				model.setStopTime(endTime);
				
				com.cannontech.analysis.report.LGAccountingReport report = 
					new com.cannontech.analysis.report.LGAccountingReport();
				
				try
				{
					ivjTabbedPane.insertTab("Load Group Accounting",null,report.getPreviewFrame(model),null,0);

					//report.showPreviewFrame(data);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}

			}
			
			else if (model instanceof StatisticModel)
			{
				
				DefaultMutableTreeNode[] nodes = getCheckBoxTreeViewPanel().getSelectedNodes();

				com.cannontech.clientutils.CTILogger.info(nodes.length);
				int paoIDs[] = new int[nodes.length];

				for (int i = 0; i < nodes.length; i++)
				{
					LiteYukonPAObject tempID = (LiteYukonPAObject) nodes[i].getUserObject();
					paoIDs[i] = tempID.getLiteID();
					com.cannontech.clientutils.CTILogger.info("ID =  " + paoIDs[i]);

				}
				
				model.setPaoIDs(paoIDs);
				
				int index = getCheckBoxTreeViewPanel().getSortByComboBox().getSelectedIndex();
		
				if (index < 0)
					return;
								
				model.setReportType(index);
				
				com.cannontech.analysis.report.StatisticReport report = 
					new com.cannontech.analysis.report.StatisticReport();
	
				try
				{
					ivjTabbedPane.insertTab("Statistics",null,report.getPreviewFrame(model),null,0);

					
					
					// Use below for new window of report
					//report.showPreviewFrame(data);
				}
				catch (Exception e)
				{
					
					e.printStackTrace();
				}

			}
						
			else if (model instanceof MissedMeterModel)

				// Need to get the collection group and dates to get missed meters
			{

				long startTime = getStartEndPanel().getStartDateComboBox().getSelectedDate().getTime();
				long endTime = getStartEndPanel().getEndDateComboBox().getSelectedDate().getTime();

				DefaultMutableTreeNode[] nodes = getCheckBoxTreeViewPanel().getSelectedNodes();
				
				com.cannontech.clientutils.CTILogger.info(nodes.length);
				String collectionGroups[] = new String [nodes.length];

				for (int i = 0; i < nodes.length; i++)
				{
					String tempGroup = (String) nodes[i].getUserObject();
					collectionGroups[i] = tempGroup.toString();
					
				}
				
				model.setStartTime(startTime);
				model.setStopTime(startTime);
				model.setCollectionGroups(collectionGroups);

				com.cannontech.analysis.report.MissedMeterReport report = new com.cannontech.analysis.report.MissedMeterReport();
				//com.cannontech.analysis.data.loadgroup.LoadGroupReportData data = new com.cannontech.analysis.data.loadgroup.LoadGroupReportData(get,endTime);
				try
				{
					ivjTabbedPane.insertTab("Missed Meters",null,report.getPreviewFrame(model),null,0);

					//report.showPreviewFrame(model);
				}
				catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			else if (model instanceof PowerFailModel)

				// Need to get the collection group and dates to get missed meters
			{

				long startTime = getStartEndPanel().getStartDateComboBox().getSelectedDate().getTime();
				long endTime = getStartEndPanel().getEndDateComboBox().getSelectedDate().getTime();

				DefaultMutableTreeNode[] nodes = getCheckBoxTreeViewPanel().getSelectedNodes();
			//	com.cannontech.clientutils.CTILogger.info(nodes.length);
				String collectionGroups[] = new String [nodes.length];

			for (int i = 0; i < nodes.length; i++)
			{
					String tempGroup = (String) nodes[i].getUserObject();
					collectionGroups[i] = tempGroup.toString();
					//com.cannontech.clientutils.CTILogger.info("ID =  " + powerFailIDs[i]);
//
				}
				
				model.setStartTime(startTime);
				model.setStopTime(endTime);
				model.setCollectionGroups(collectionGroups);

				com.cannontech.analysis.report.PowerFailReport report = new com.cannontech.analysis.report.PowerFailReport();
				//com.cannontech.analysis.data.loadgroup.LoadGroupReportData data = new com.cannontech.analysis.data.loadgroup.LoadGroupReportData(get,endTime);
				try
				{
					ivjTabbedPane.insertTab("Power Fail",null,report.getPreviewFrame(model),null,0);

					//report.showPreviewFrame(data);
				}
				catch (Exception e)
				{
					
					e.printStackTrace();
				}

			}
			
			else if (model instanceof DisconnectModel)

			{

				long startTime = getStartEndPanel().getStartDateComboBox().getSelectedDate().getTime();
				long endTime = getStartEndPanel().getEndDateComboBox().getSelectedDate().getTime();

				DefaultMutableTreeNode[] nodes = getCheckBoxTreeViewPanel().getSelectedNodes();
			//	com.cannontech.clientutils.CTILogger.info(nodes.length);
				String collectionGroups[] = new String [nodes.length];

			for (int i = 0; i < nodes.length; i++)
			{
					String tempGroup = (String) nodes[i].getUserObject();
					collectionGroups[i] = tempGroup.toString();
					//com.cannontech.clientutils.CTILogger.info("ID =  " + powerFailIDs[i]);
//
				}
	
				model.setStartTime(startTime);
				model.setStopTime(endTime);
				model.setCollectionGroups(collectionGroups);

				DisconnectReport report = new com.cannontech.analysis.report.DisconnectReport();
				
				try
				{
					ivjTabbedPane.insertTab("Disconnect",null,report.getPreviewFrame(model),null,0);

					//report.showPreviewFrame(data);
				}
				catch (Exception e)
				{
		
					e.printStackTrace();
				}

			}
			
			
		}
		

	}

	public void addMenuItemActionListeners(javax.swing.JMenu menu)
	{
		javax.swing.JMenuItem item;

		for (int i = 0; i < menu.getItemCount(); i++)
		{
			item = menu.getItem(i);

			if (item != null)
			{
				menu.getItem(i).addActionListener(this);
				if (item instanceof javax.swing.JMenu)
				{
					for (int j = 0;
						j < ((javax.swing.JMenu) item).getItemCount();j++)
					{
						((javax.swing.JMenu) item).getItem(j).addActionListener(this);
					}
				}
			}

		}
	}

	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData()
	{
		/*V1.1
		**start of data**
			D0CB838494G88G88GB30D02AFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDC8BD4D45739B637A65734B665A6A6D5EBEF688DE9CC2E3761A636A529E9C96BAAB6712E105424E6555CC5D2574A631206B42EDBF263CD8F83A86AA0830C86F98C83E264B18C630008A3BEF8A99A12D0A5CAD630F1E00EB0BA4CCCE78E8ABE6F7F6FFD7EB307618C280957357E1C337F7D789F7B5F7B7F76BEC384E3E9F4E61A4EA888E919027CEFD79A7C27DD90BE1D7564CE6289EB93F4026ADF99D0
			2E3023AE831E86282BBC1220B3885545D9D02E0072D0E7026E83F84F976A0A1BDA60A5029C83543D7C485B6264F27EF30C642CE565028EBDBCB782D5822F8BE876DA14EF6BA81079FEA8EF145F1152E0544AACEC73EEF9010A3FC2EEE74C624F052C4DC79D45B48E14314D8CF84E4B8A0D390EE9F72B23C9AE777D53ACE155D39BF459A8CBB5BE51EA392FF91D7C946EE20EC897E237D960595CFDF6F05F00EF58BA6074FAFDDEC99C98327AFD3E33E3D6C98CCA8E47D1516F8BC8ED9E3768152256210E25DB33D8
			FFC16805B25767596CE6D95E2C333304247B5ECF0B244FB2D90FD415BECD1FED5BB8FFBD0EADB17FE23FA6A87B01384CC59DA4B399F81E8D483317E35B8AF8EF866A59C03A6C5DCE3A74DE3CCFC86D376AF2A268923B016B30FCC3C8174E11E17D96B657E7AE62FC9975893AA620AEA0BFD094D0ACD0910B31ED972A615952AF35790607FD5E963F3F55638AFC6CF3099EF8F7B820C6669D9587FC819704999B3BE22BC5C39F22A57A71B1941A7793824D7B0223F385C92A50E5EA58367079EC5D8AF90C94FD48B6
			EE571A4BE817A1BBC1E781EA876A833A84B4AF1B6C72DD281F4A2E4390AB812F53F3EC245F6F8CC82DA3EE0FCB8C4876DD68184CBEAD627DE6DCF7BF6E177975EA3F2CF8340E2D57F03F2C18EF5445E5F3FFA4E6077C42E5BF99D1F6CA764479E0EBF7A346618FD99CE640FB1A9C231C9F4B783DAABE0F71A64647729054AD5DC8F15B3B11EC684A38C3B06C30E95AB07B175BF4369C2ECFE7CE785C3EF285778F8978D1C0730162011200D683998CB43F0FDF283B46386DD64DAD6F9B3C38991E622894F09E7686
			A527F7C0BC6E8C381D7D9EB1A848EDAEDDC45D7B47753B052D278110F1C8740A81276476F9E1F4673088019464ED3EF9B1975B84C4D65B639E06BD48B96C97E85CBBAF664353638CCA1F79DDCEC98C356075F3AF22AF4FB90F4308B0G3C1FB2204F7730BD4B866FDDC05CD7B98272E2203CC8714B26CBF9707C8882DB74B89C6D6E81D4439998A379B917F1AF914F41E658AE7AC52F8BDE02F25D664B6BC2FEB96DD3F4AEE6FCDCA3ED20F8D06117DF4E65FC98C36DE52A2FE675CEC98A387BC7A47118A8C9EE6F
			E0283F1D69929C377C02023C4653B04692412E5700D2957BFEF6B9DFE5DF976C73A37E9E1CB959CF4B59BE2B4FCD50D564D24EDBD00EB930B31762740F3D94276D59F3844B73D64DB8C5BA154B63F3B4B7FC2D7D040D9912312F3CD723FFE72E763A3BDE62B2CEE84AC87F9A46C762FE9F53215F23C16FC5C0ABF2496F7FFD594C57DB2BB3A8528A7BB4GB3A63AC2D1465BA6DF099CE36FDC510E31F72FE84758DF2ECC9EC3EB2FEA45902F5BF895F30B6F3CD7951379D773589A76FAC5263452C56018620FBD68
			8B3757AD1DF03B86C569187B82A02C62BC0C2D23EC8D2E821FE465D1AC1D5FCA31D4F161EEA17E4BF51A79EC767D153AD3F9BC96243CD0ACF11D0C827ADC7408436028B65F08D7627CC2363EE53E3C3F6853345B6625E1BC1E954764F61B535688380FF8C6C44536D8678674F140BD8CD3058BD646F5EFF528F15DAC508FB1E09CBC1782713E0EBE142B46D2BCDE9EC9C77ECAE8170ABE4A27F839136D6734CE65C0E973F9BC5C6D4AFCDC8579B0FF625FC7C45806F1A1FF2458F01BAEAF2C6E38EC8F2F3FC30776
			F8D4FBC0E5BE5A63D6709D66884E1B46F819B0E9AFE5D04E7D582046202EE019665CA1F972791CCD4B8F4FD72FE6603851400FD5ECDF172E5E23DA02C15F001B2583F99E5E29C2FFB35CFA46F94EB9EA8D1E0718F6B80EF4BB2187470B1F9F4C12577D62FC420BD1A67475EAA89B0D00FD0CA4AFAEFDE21CBC113E9E1E275D01A0609C17B8CABE7BD5BA5A0E79C7E693AE332BFD911F34A922AF5A0D5C873D46F0DF542EC1BDA4604FD0F45B17112B7285E0C8C837039ED1197F640CF49E37F2B9A5A31362D620FD
			6FEF8789574C13E5469AC37B6AE26344BD16631747CB51DF4B0D6138E6B1633FE68C47B57718885724C2DD5FA64A33DB5722DD71D0CE5CC4F65D1A192D322B5B6776CA0ACD76B53876FC8177205BCB3CDD0C87881AEDC6BCD70D30B9CE02B1D783F15E34A26499B6C37E5ACC3C13A5486B4472E64AB77125385E27AB6527D8B9C6A94FB305678AEC7BF56409D0BE7A718499DFD7AE5A287832F2AD4E5128D236307218ACABDF18F99067419F90CF0B8158E178F6AD889F205F6EE77EDD82F6AF83626D3FDB340645
			7F18EF84A6AC16355397537E578E65BE2069C033BF6098022F59FFBF0FFB4DE9GFB30D40F794581E1E681BB6F15D05FD4E82F871E85288B48D3C0B29FAA40FD2CCBBCAD9DF58F8EC9477C9E3704CB1D4E556F152A775FE585DC6EEA9CFFCB022EF28B1E6B75D8A55BBCD2E40627D7BCEF466D436ABCAFD95B81C6FA91B0D66E326F36186DBBECD5FCDDC71FD72F6B2675C5117738AD729EA7BFABFED6A82F0B06E2BA5346C1DD4A9672490ED3085515FD8C95F1B88E855C2ED667E09773B05F4BAE313B8D6CDFD9
			C8FB59C3EC4CD3D09E850AAA24FCBA7AA11DFD977C708E21EBDA1D663DC0D24FE65D7CC22E67524250BE54GE7DFAEF3EE89494CAACA509DA8C25E1292E3D82C66C2B0D861496B7FE7DCBD6EDBD5023A5EE2D257233E17335475FF9BD71F296A4FEDFBFA9B59367A69F9026747A5916FBCE694F11B96484F385B8B058A7DBB595C6F5F330E73FBA8FFA81145FDD0EFC69FE05C2EACA23FFF530EFEAFAECE50B5GF595135F1F79C29DA7063919ECEF54524DD54CF57297076F7FAF6430738870630BE91F7C6B469A
			F89EF71E11CFE39DAE37648BB0G894C6E004FAF8624B14EE559DD5EDB9C45B7F67E6E3C41FE37DCF7BF1ECBCCA5684F24628445DF1C3F1D7113D5FC3E16E21535148AF5FDA514E35666629E908F6595C02B0074A658CB00BACD644B399BED4294E75E9E1F3F8B0037C7F59C63765DDBF2E37792CF2E2518DD7878BDC25CEDAF25ED089097FDA69E8F1764E7770CC24667764D4DC37B96C05D9220E4200C5284DD81102D146CFBE523055B076150A2C94E01A106E8483863EE71FC080D1BB0372D490C7D5AE11C5E
			5209B94E833CE9AA7EACF867F374BEDB8B73CB799CE5AAF3372E9E790BD4FCBEF7D12CBD1693212E41CCF3778442BD4AD7B95BA245BBD8F084166B277DE7G8575B88715BE3F4FC13D14B38EDF86F25DABB9382760F9446D7438AFB098F98C9AFA8765FB3892C10961DA557BCF0A354E0ADF97F1994F5CD4995CFEC3EE64336487395AE74912DC6D3364565C494F12B539114F1276DC75D9124F7BD3C61C1B5990878B011683A58389E5944F713965BC1EB5E239838E17EDBEF67BA4117C7B72A25B7AD05E54F7D6
			0F66A1666F77C912EF9847A77EE3F9B8DF124F4F181C7773BC74D1G7188EFAA739767619E6E812C02EC0EBFBFE5F924986CEAA8A3CCBA47CC5835D3614D58CBB835CF6165FF043C85D0DE2C70EA6D48DB8E65D5E504D7B36A1117D58EF8271CF8B90C5785E53F423B479E9EF3587EC6E3ACAB2CAF177170849919DFA10E43B1B17773F34648F37D22F16A39FE45484EFCB0C7BDE3FE1173DEB532F3476953C1D10929E423C1619D8EF67B02EE5CC1B80EBCED463D5C65BE6706C06748196161ABECEEB1BEE601FF
			B86F170C978365D8200A2D483BF7EF3BEC538A0DB9780B7CAFDD76C18A344900FE1B64F24156D0EE647AD4FF047AG40F68F0F8C2B74E9E5B164017623DB89D3375BF3C23AF7798678791A37BF49626B7BB0F00C52BE3386B1B83F3FE8F7877DF082E027ED125DF28275EA9B92874E367AC6FB82220859C6EEA26374167A70FB92BCEFAD8499CB00F81BFFB62B5BA4ED65ED4C9530F6AAC8173B4DE8F3830E4B307DBF60F44BFADC5EEB96A64176BBG5B5B6CD59C5FA5327506315985E317327E823BB3627FD276
			EB71384CBF63A7C6B9473F2C279CDF7C2F5F915AFF77D25AD6C1BB47CF2F60733C2CA2047D16BE70FABA9F5339A5E4C739FD92BB6CBB9AAC669D3B6CD53C2DE447275D56A8F03B46583AC7EC2C5FC638755FEAB1777A21BC83A8FA9B1DC16E325805293F81523C9E410915A7154BFA41CCF7C5F10A2C2BE5A8AB854AD99F425CFDC8FB4F73FB55982A8978DD9FCE3E7770BC71E9A12E13D3507692D0B4D09C50D22015C029C07A6DA0872889289B4885B48AB493E89ED0AC50626D14D71A8B70DC9189D34B87B016
			DC82CEFEAF41753FD29F3E2FAD07B11337CFEE03D089F829F2FC1BCAA8B70771B8F6AA33226F3AAB43B1150571DD1561186A51DA42D423D0372212FC7DFB73E4F875C0D524BB1FF7585C75D4056371550CFF2AAA9C0F5FDAC6F85C83F509D5142312595AEA28666DB96FED466B12F9FCAE868AA36F63E78A275E4703055AB9FB2CF0E24E3ED2281D3327972AF3B6675DDE08BE9D04D868D55A5D2170BED7F8F731B111F7426D1206947E9C269772BBC594C87CFB19DE67992FD7664528F81F4B3C878BB1970149C3
			3EG757DD1A15ED11DF6FBBC3C7C88EB6376C2EAD27C71D326235B6FD4693DC861FD2E4CC5F4BD5D138DD5ABF7E70D481B8965980577DCA36114B805775EEE64AD0572CA0577310330CB2A423B4DC15825220662F23B95F9CDD06ED1F8F64673C0B9CA61BDD6BB95B6467E116A1AA6298B6114672614B199EE196A2E0E7B60CFE6724BC24536F77127A5328E3A72507B374AEE7E19A366264A60B9457921DED04EDCECD9F0FE71E8F608CF4BC23EC37C0D028D12EA789DE2AABC75964891654A5DE3A32EA9B8468D
			FA71AC00414C7ABE4CE234825AB6D988EF3D47F8C3D016AC54BF4FB12EFFAF752FE6F19A8D6D968225DA1067F218A731F325054717AC1F75BFD08B3A56122C473EC05C4871929C14C67CACEB723ADF5BD00F9EE7FF3F68D25FE33A2C617888ED9F05B1E75692BEB257285B445472B60970DCD9CB3A2FABA35DD757CEDC8786AB67717D7B44CE5A3F4D406F35526FED6E5DC977FFD1D64A6B7F415AAE01F292D02AD58BB3A56D17EDECC48C24E017B65F305F678527DB5BBABE6969B87AE5CB5761C31FFC8C6FB206
			2B41ABB9997B246CC67DA3E1B8DBBDE2B8DBD5E459AD0EEB154D5B5F6240788C487148B15D99039A532D98FBABADD75046F406BA6E4F663A9026E3B91E5995DF9232AB2AF6923B40A444F4B6991B4E339206136A487F5F2EE65FE3201C8214E5A38CD71305C2B46EB355A1A663355E9A7AAEE6365138BEF657568BE5BF508C9B45400EB2424751B60AEBF79966A04C15FCBFB93973466E8B4F5EE03F186B6E4771CBCBA3731DAD9C579CE17C1536F0DC73DBE5DD24C0DD4F8E3A2B3A5F04F3988765E5C029C086BB
			64B720E620EE20A1BB61CCFF71E4F7374AE5338AE9F29BFFE01D706D89461CED1FBC27AD007A7830B64B207C1ADD8BE7265530EF2176F01C29E7FCC3FDB84E3C2B1AF0E685544D2827DCF6320666A339FE22FEC283E05F86AD79FFE0B1D759907EED71CD46EFEA887F36B8D2C25F96BB21EEC18349EFACD34703E1A74C454EEB497D079A85653DDB757E624E1B1F037FF4D3E570580BB5C54655BFB5CD0D2B63CD5A387A572609387AE913B62EFE56B491D73F40468D08033067FD629CD646DDE1423D0EFFD77C58
			69F58E32FBC20E21FEE5A52C5A34133065DE5BC42CFA5AC6D835CFE9F7E54FC42C7A409E42E4FE255D60F6422A71BBA93656323DF29914D7A93CBC46B3B4A668EC0D441BD37DD567F315C64CAE13F9DC46A3DFD9069616A58C4BFDA055107FFA9A49AF5BCC642BA10557D7C53E0AD3FC70FA89660525CA799DD6DE2114AF16DC0B7E587F4657552CEB12F1235F5865F3CE991DEB6724916FE8991DEB718E0C6359E4854B893BB81EAD002785884FB99CB7FE0745EBBB703AF7511D604F77501D6025DD93FD213E6F
			C30A02B6B13BC2F702F13BC2F90C4B08E2F13D9878093BA8072F5F0C776E72EFB0C50ED1DBC7E0254B771AC733F1AD9E131C8129436B22BB3C26A69C6F4B9A75B75A5532BD95CD04CFDB46FD0B1F56446B9741B331096C7EBE0B3357203C2A096CEEB6115D665D5AF6272B28815AF46E8E595D37BB5C6EDBD90C8E81DF5ACDF66F5F02B1598DFB8B573542940E37717C998D6D9782713CE32CA39C3094F8998E42C6FDF5040F1B9D4A37E156D60272CC2098877D76007D74CF460041BD08DBA743360DBB2BE42C76
			569E42DF0B9CD35FCF4E1D037F9E146FA7FF464E187CFDD5F9C83E3FFA92798C035603FC0EABF30EA166514015BCD884421477307944FC334AC15F43AF9FD47FBD40221C4D3AC85FBDFB77483F915D93424F7E11E1BD57E16E69DAE16A7B4970D8156FC1CB153B49BDB4C7438C4724C0B92BB9C1E7EE26B9FAD09F72D1A143F811FCD48FF334831C44E58C1BE80E1A1A2F778EF9E9F6E80E566704649FAD19CCBEBBA7A8727B94797EE632713F586F3366C3B99EE8F9B36D4105F5B70639ABEF301F733A7B495F15
			59BACAE98E476AEBED8C33B607E375A8E50DE66D859F6CA5FBD76DD14BCF847E4A3D57028D3FD72315C7D2653E7AFDF21C6E9BBFD6433E09E3F7822F8F482F2A6367087759F76969400F860A83CA84CA59C7E7077FDDBF655FDFF40CCE727797D7CAA7733DE1FF247B5F17597A28589F6E7BB7985F36BF5C7741EA72FDB354C56FA75F87CCEA799987A07F9D38965FBBCD61FADBD47D8EE80C41757B524C6E2D8F046BBD4478278E046B7DF629F2EF8DF50987146F28789391D917EC557BBDF5B77FDC7070CD1541
			314A066609777206E6753DBC67BD417674061684DDFB8B613EF28B723A21BC2470FE4B6EB7AFC1F93642CB2BA09C9923707E45C2B8B2CE61FD1B6D4FD92D893A62D6621D2BA1ECD959CAEBE10DA172D9A65B207D9B06FC0376EF983698A67F1B0BB2C3245FB8F07D6EAEA5BB1ADBA9E6DE2D8E45C90A6AFD6CABE37629E3664E1BAA035BFC3B09E2E6D8413CF718A8E65E4CA45E28094EE9B31579FCCC71D72C62AF519C7211D7757E67521B6FAF0379E64A6079FBD98D62CC89021267769FE1C849F53BAD6C9B3D
			F3B46C9B7DDF597AD886BECAEAA53C7A06998102137D609F5300536B9288AFBC0B1F63697E1A2DB7FD9B6C31ED04279F33901EEEEF1B9ACF77C29BCF9B475308A52635F19D52953DE3CA57B03D836CB759DC0781E3C807FB593A1D897DE6ABBAACA8A59D96EB68109EC64B21CDF2DB88538B6D21BD1B1F29FE2060F5BD54D5344B3F2307A71EB37D6D6C1B7E3873C7B8F13DEF5B941A0FA5E61390990F6F34186DG0765FE8F2AFEE770DD4B357463986D8D8BE13438F66DBB7309BC12397403507BDB85A1799525
			1348EF98AF7F25D2121F22A9FFB20C1AD708A51EB3FFE3221CD959816B3B036E1F874B71B7FB8AC6F7778FDBC74E8DDB1D7D12D5F439811EFFB26271F449BF9997B8C6DD43726FEC98A396E8C9871D9B161CD47F4657DC731A665F91271E4C57A5F570794FB898BAB744BD70FABADF67EFD9467D56G5AF49D3C967C714AB85CF48A7AC857546F198938655A7623172E3B9F47B597187FE69C8C47BB5FE07CD98743710EDE41BB51D017F1086E33F794E1D08DC1F9BA50FC20F820E5C04907A87F9EAF2C94A25FE3
			73098DDDE273FDFFCD6DC41C3121F6A24EF837145ABD21A35E7B25546EDB0A1F26E1084A6F372B5E737E9FF2C64DCD1541FD6CA80A0CF1DA0A263E2FBDD224FDDF5BDDB4713E7633A26D7B5AAF0A5477357C7F21700D5AC7E132963CA9E48683C25E443D26650E97FF37604BF623CE509D96568C310F3A07054C737855F7947FEC77B9C1503D987C3BF08567CCF88ACA4F42C8B94AC807594F97124E55BB527FF0DF3ACE4800E1389907058C5485C68B70D153418CB6E6061A143197G3D2014160024DFA8250710
			2314DA14BA16E8547F14B6DB477578C72057276801ED16C8C28EDD2C19794D1AA4E491C799G7FC793FA19F44AG6CAF7EB4CA58A3D3CD635A54EA37113FE3D6DDD57FDDC26A2D2B53A27E7D4A91796FD70E047FDDC2A53B27DC847C65C7E8CF982D43FB19C83F53BEB66C74F8A42767EC0B57053F59EE9D134490862F356178C1EAA3D7723A869B6AFDC655677FGD0CB8788110FF5928D97GGA8C4GGD0CB818294G94G88G88GB30D02AF110FF5928D97GGA8C4GG8CGGGGGGGGGGGG
			GGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC797GGGG
		**end of data**/
	}
	/**
	 * Return the CheckBoxTreeViewPanel property value.
	 * @return com.cannontech.pooky.test.CheckBoxJTreePanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private CheckBoxTreeViewPanel getCheckBoxTreeViewPanel()
	{
		if (ivjCheckBoxTreeViewPanel == null)
		{
			try
			{
				ivjCheckBoxTreeViewPanel = new CheckBoxTreeViewPanel();
				ivjCheckBoxTreeViewPanel.setName("CheckBoxTreeViewPanel");
				// user code begin {1}
				// user code end
			}
			catch (java.lang.Throwable ivjExc)
			{
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjCheckBoxTreeViewPanel;
	}
	/**
	 * Return the RefreshButton property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getGenerateReportButton()
	{
		if (ivjGenerateReportButton == null)
		{
			try
			{
				ivjGenerateReportButton = new javax.swing.JButton();
				ivjGenerateReportButton.setName("GenerateReportButton");
				ivjGenerateReportButton.setText("Generate Report");
				ivjGenerateReportButton.setMaximumSize(
					new java.awt.Dimension(75, 25));
				ivjGenerateReportButton.setActionCommand("Generate");
				ivjGenerateReportButton.setPreferredSize(
					new java.awt.Dimension(75, 25));
				ivjGenerateReportButton.setMargin(
					new java.awt.Insets(2, 12, 2, 12));
				ivjGenerateReportButton.setMinimumSize(
					new java.awt.Dimension(45, 25));
				// user code begin {1}

				ivjGenerateReportButton.addActionListener(this);

				// user code end
			}
			catch (java.lang.Throwable ivjExc)
			{
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjGenerateReportButton;
	}
	/**
	 * Return the LeftRightSplitPane property value.
	 * @return javax.swing.JSplitPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JSplitPane getLeftRightSplitPane()
	{
		if (ivjLeftRightSplitPane == null)
		{
			try
			{
				ivjLeftRightSplitPane =
					new javax.swing.JSplitPane(
						javax.swing.JSplitPane.HORIZONTAL_SPLIT);
				ivjLeftRightSplitPane.setName("LeftRightSplitPane");
				ivjLeftRightSplitPane.setDividerSize(4);
				ivjLeftRightSplitPane.setPreferredSize(
					new java.awt.Dimension(721, 202));
				ivjLeftRightSplitPane.setMinimumSize(
					new java.awt.Dimension(214, 172));
				ivjLeftRightSplitPane.setDividerLocation(206);
				getLeftRightSplitPane().add(getReportDisplayPanel(), "right");
				getLeftRightSplitPane().add(getCheckBoxTreeViewPanel(), "left");
				// user code begin {1}
				// user code end
			}
			catch (java.lang.Throwable ivjExc)
			{
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjLeftRightSplitPane;
	}
	private javax.swing.JMenuBar getMenuBar()
	{
		if (menuBar == null)
		{
			try
			{
				menuBar = new javax.swing.JMenuBar();

				menuBar.add(getReportMenu());

			}
			catch (java.lang.Throwable ivjExc)
			{
				com.cannontech.clientutils.CTILogger.info(
					" Throwable Exception in getMenuBar()");
				ivjExc.printStackTrace();
			}
		}
		return menuBar;
	}
	/**
	 * Return the Page property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getPage()
	{
		if (ivjPage == null)
		{
			try
			{
				ivjPage = new javax.swing.JPanel();
				ivjPage.setName("Page");
				ivjPage.setLayout(null);
				// user code begin {1}
				// user code end
			}
			catch (java.lang.Throwable ivjExc)
			{
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPage;
	}
	/**
	 * Return the ReportDisplayPanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getReportDisplayPanel()
	{
		if (ivjReportDisplayPanel == null)
		{
			try
			{
				ivjReportDisplayPanel = new javax.swing.JPanel();
				ivjReportDisplayPanel.setName("ReportDisplayPanel");
				ivjReportDisplayPanel.setLayout(new java.awt.GridBagLayout());
				ivjReportDisplayPanel.setAlignmentY(
					java.awt.Component.CENTER_ALIGNMENT);
				ivjReportDisplayPanel.setPreferredSize(
					new java.awt.Dimension(703, 200));
				ivjReportDisplayPanel.setAlignmentX(
					java.awt.Component.CENTER_ALIGNMENT);
				ivjReportDisplayPanel.setMinimumSize(
					new java.awt.Dimension(200, 170));

				java.awt.GridBagConstraints constraintsReportSetupPanel =
					new java.awt.GridBagConstraints();
				constraintsReportSetupPanel.gridx = 0;
				constraintsReportSetupPanel.gridy = 0;
				constraintsReportSetupPanel.fill = java.awt.GridBagConstraints.BOTH;
				constraintsReportSetupPanel.weightx = 1.0;
				constraintsReportSetupPanel.weighty = 0.05;
				getReportDisplayPanel().add(
					getReportSetupPanel(),
					constraintsReportSetupPanel);

				java.awt.GridBagConstraints constraintsTabbedPane =
					new java.awt.GridBagConstraints();
				constraintsTabbedPane.gridx = 0;
				constraintsTabbedPane.gridy = 1;
				constraintsTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
				constraintsTabbedPane.weightx = 1.0;
				constraintsTabbedPane.weighty = 1.0;
				getReportDisplayPanel().add(getTabbedPane(), constraintsTabbedPane);
				// user code begin {1}

				// user code end
			}
			catch (java.lang.Throwable ivjExc)
			{
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjReportDisplayPanel;
	}
	private Reportmenu getReportMenu()
	{
		if (reportMenu == null)
		{
			reportMenu = new Reportmenu();

			javax.swing.JMenuItem item;
			for (int i = 0; i < reportMenu.getItemCount(); i++)
			{
				item = reportMenu.getItem(i);

				if (item != null)
					reportMenu.getItem(i).addActionListener(this);
				addMenuItemActionListeners(reportMenu);
			}
			//reportMenu.getItem(1).addActionListener(this);

		}
		return reportMenu;
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
	private javax.swing.JPanel getReportSetupPanel()
	{
		if (ivjReportSetupPanel == null)
		{
			try
			{
				ivjReportSetupPanel = new javax.swing.JPanel();
				ivjReportSetupPanel.setName("ReportSetupPanel");
				ivjReportSetupPanel.setLayout(new java.awt.GridBagLayout());
				ivjReportSetupPanel.setAlignmentY(
					java.awt.Component.CENTER_ALIGNMENT);
				ivjReportSetupPanel.setPreferredSize(
					new java.awt.Dimension(703, 65));
				ivjReportSetupPanel.setAlignmentX(
					java.awt.Component.CENTER_ALIGNMENT);
				ivjReportSetupPanel.setMinimumSize(new java.awt.Dimension(200, 35));

				java.awt.GridBagConstraints constraintsGenerateReportButton =
					new java.awt.GridBagConstraints();
				constraintsGenerateReportButton.gridx = 0;
				constraintsGenerateReportButton.gridy = 1;
				constraintsGenerateReportButton.fill =
					java.awt.GridBagConstraints.HORIZONTAL;
				constraintsGenerateReportButton.weightx = 0.5;
				constraintsGenerateReportButton.ipadx = 1;
				constraintsGenerateReportButton.ipady = 1;
				constraintsGenerateReportButton.insets =
					new java.awt.Insets(5, 5, 5, 5);
				getReportSetupPanel().add(
					getGenerateReportButton(),
					constraintsGenerateReportButton);

				java.awt.GridBagConstraints constraintsStartEndPanel =
					new java.awt.GridBagConstraints();
				constraintsStartEndPanel.gridx = 1;
				constraintsStartEndPanel.gridy = 1;
				constraintsStartEndPanel.fill = java.awt.GridBagConstraints.BOTH;
				constraintsStartEndPanel.weightx = 1.0;
				constraintsStartEndPanel.weighty = 1.0;
				constraintsStartEndPanel.insets = new java.awt.Insets(5, 5, 5, 5);
				getReportSetupPanel().add(
					getStartEndPanel(),
					constraintsStartEndPanel);
				// user code begin {1}
				// user code end
			}
			catch (java.lang.Throwable ivjExc)
			{
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjReportSetupPanel;
	}
	/**
	 * Return the StartEndPanel property value.
	 * @return com.cannontech.pooky.test.StartEndPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private StartEndPanel getStartEndPanel()
	{
		if (ivjStartEndPanel == null)
		{
			try
			{
				ivjStartEndPanel = new com.cannontech.analysis.gui.StartEndPanel();
				ivjStartEndPanel.setName("StartEndPanel");
				// user code begin {1}
				// user code end
			}
			catch (java.lang.Throwable ivjExc)
			{
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjStartEndPanel;
	}
	/**
	 * Return the TabbedPane property value.
	 * @return javax.swing.JTabbedPane
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTabbedPane getTabbedPane()
	{
		if (ivjTabbedPane == null)
		{
			try
			{
				ivjTabbedPane = new javax.swing.JTabbedPane();
				ivjTabbedPane.setName("TabbedPane");
				ivjTabbedPane.setPreferredSize(new java.awt.Dimension(233, 135));
				ivjTabbedPane.setMinimumSize(new java.awt.Dimension(200, 135));

				ivjTabbedPane.insertTab("Page", null, getPage(), null, 0);
				// user code begin {1}

				// user code end
			}
			catch (java.lang.Throwable ivjExc)
			{
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjTabbedPane;
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
	private void initialize()
	{
		try
		{
			// user code begin {1}
			// user code end
			setName("ReportClient");
			setPreferredSize(new java.awt.Dimension(729, 210));
			setLayout(new java.awt.GridBagLayout());
			setSize(1086, 776);

			java.awt.GridBagConstraints constraintsLeftRightSplitPane =
				new java.awt.GridBagConstraints();
			constraintsLeftRightSplitPane.gridx = -1;
			constraintsLeftRightSplitPane.gridy = -1;
			constraintsLeftRightSplitPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsLeftRightSplitPane.weightx = 1.0;
			constraintsLeftRightSplitPane.weighty = 1.0;
			constraintsLeftRightSplitPane.insets = new java.awt.Insets(4, 4, 4, 4);
			add(getLeftRightSplitPane(), constraintsLeftRightSplitPane);
		}
		catch (java.lang.Throwable ivjExc)
		{
			handleException(ivjExc);
		}
		// user code begin {2}

		// user code end
	}
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args)
	{
		try
		{

			javax.swing.JFrame mainFrame = new javax.swing.JFrame();

			ReportClient aReportClient;
			javax.swing.UIManager.setLookAndFeel(
				javax.swing.UIManager.getSystemLookAndFeelClassName());
			aReportClient = new ReportClient();
			mainFrame.setContentPane(aReportClient);
			mainFrame.setJMenuBar(aReportClient.getMenuBar());
			mainFrame.setSize(aReportClient.getSize());
			mainFrame.setTitle("Yukon Reporting");
			mainFrame.addWindowListener(new java.awt.event.WindowAdapter()
			{
				public void windowClosing(java.awt.event.WindowEvent e)
				{
					System.exit(0);
				};
			});

			java.awt.Insets insets = mainFrame.getInsets();
			java.awt.Dimension d =
				java.awt.Toolkit.getDefaultToolkit().getScreenSize();
			mainFrame.setSize((int) (d.width * .85), (int) (d.height * .85));
			mainFrame.setLocation((int) (d.width * .05), (int) (d.height * .05));
			//frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
			mainFrame.setVisible(true);

			//mainFrame.show();
		}
		catch (Throwable exception)
		{
			System.err.println(
				"Exception occurred in main() of javax.swing.JPanel");
			exception.printStackTrace(System.out);
		}
	}
	public void setReportClientFrame(javax.swing.JFrame frame)
	{
		reportClientFrame = frame;
	}
}

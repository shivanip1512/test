package com.cannontech.analysis.gui;

/**
 * Insert the type's description here.
 * Creation date: (11/3/2003 10:53:31 AM)
 * @author: 
 */

import java.awt.event.ActionEvent;
//import java.util.Vector;

import javax.swing.tree.DefaultMutableTreeNode;

import com.cannontech.analysis.report.DatabaseReport;
import com.cannontech.analysis.report.DisconnectReport;
import com.cannontech.analysis.tablemodel.LoadGroupModel;
import com.cannontech.analysis.tablemodel.StatisticModel;
import com.cannontech.analysis.tablemodel.PowerFailModel;
import com.cannontech.analysis.tablemodel.DisconnectModel;
import com.cannontech.analysis.tablemodel.MissedMeterModel;
import com.cannontech.analysis.tablemodel.DatabaseModel;
import com.cannontech.analysis.tablemodel.ReportModelBase;
import com.cannontech.common.gui.util.CheckBoxTreeViewPanel;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.model.ModelFactory;

public class ReportClient extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.JSplitPane ivjLeftRightSplitPane = null;
	private javax.swing.JPanel ivjReportDisplayPanel = null;
	private javax.swing.JPanel ivjReportSetupPanel = null;
	private javax.swing.JPanel ivjPage = null;
	private StartEndPanel ivjStartEndPanel = null;
	private javax.swing.JTabbedPane ivjTabbedPane = null;
	private static javax.swing.JFrame reportClientFrame = null;
	private javax.swing.JMenuBar menuBar = null;
	private Reportingmenu reportingMenu = null;
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
	private static final int[] dbreportModels = {ModelFactory.MCT_CHECKBOX};
	
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
		if (event.getSource() == getReportingMenu().getTodayMenuItem())
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
		
		else if (event.getSource() == getReportingMenu().getYesterdayMenuItem())
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
		
		else if (event.getSource() == getReportingMenu().getMonthlyMenuItem())
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
			
		else if (event.getSource() == getReportingMenu().getPreviousMonthMenuItem())
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
		else if (event.getSource() == getReportingMenu().getLoadGroupAcctngMenuItem())
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
		//else if (event.getSource() == getReportingMenu().getControlLogMenuItem())
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

		else if (event.getSource() == getReportingMenu().getPowerFailMenuItem())
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

		else if ( event.getSource() == getReportingMenu().getHistoryMenuItem())
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
		
		else if ( event.getSource() == getReportingMenu().getConnectedMenuItem())
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
			model = new DisconnectModel("Connected");
		}
		
		else if ( event.getSource() == getReportingMenu().getDisconnectedMenuItem())
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
			model = new DisconnectModel("Disconnected");
		}
		
		else if ( event.getSource() == getReportingMenu().getCurrentStateMenuItem())
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
			model = new DisconnectModel("Current");
		}

		else if (event.getSource() == getReportingMenu().getMissedMeterMenuItem()
				|| event.getSource() == getReportingMenu().getSuccessMeterMenuItem())
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

		else if (event.getSource() == getReportingMenu().getMCTMenuItem())
		{
			
			com.cannontech.database.model.LiteBaseTreeModel[] models =
				new com.cannontech.database.model.LiteBaseTreeModel[dbreportModels.length];

			for (int i = 0; i < models.length; i++)
			{
				if (dbreportModels[i] == ModelFactory.MCT_CHECKBOX)
				{
					models[i] = new com.cannontech.database.model.MCTCheckBoxTreeModel();
				}

			}
			getCheckBoxTreeViewPanel().setTreeModels(models);
			model = new DatabaseModel();
			
			
			
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
					//com.cannontech.clientutils.CTILogger.info("ID =  " + powerFailIDs[i]);
//
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
			String collectionGroups[] = new String [nodes.length];

			for (int i = 0; i < nodes.length; i++)
			{
					String tempGroup = (String) nodes[i].getUserObject();
					collectionGroups[i] = tempGroup.toString();
				
			}
	
			model.setStartTime(startTime);
			model.setStopTime(endTime);
			model.setCollectionGroups(collectionGroups);

			DisconnectReport report = new com.cannontech.analysis.report.DisconnectReport();
			
			try
			{
				ivjTabbedPane.insertTab("Disconnect",null,report.getPreviewFrame(model),null,0);

				report.showPreviewFrame(model);
			}
			catch (Exception e)
			{
	
				e.printStackTrace();
			}

			}
			
			else if (model instanceof DatabaseModel)
			{
				
				DatabaseReport report = new DatabaseReport();
				
				try
				{
					ivjTabbedPane.insertTab("MCT",null,report.getPreviewFrame(model),null,0);

					
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
 * Return the CheckBoxTreeViewPanel property value.
 * @return com.cannontech.common.gui.util.CheckBoxJTreePanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private CheckBoxTreeViewPanel getCheckBoxTreeViewPanel() {
	if (ivjCheckBoxTreeViewPanel == null) {
		try {
			ivjCheckBoxTreeViewPanel = new CheckBoxTreeViewPanel();
			ivjCheckBoxTreeViewPanel.setName("CheckBoxTreeViewPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
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
	private javax.swing.JButton getGenerateReportButton() {
	if (ivjGenerateReportButton == null) {
		try {
			ivjGenerateReportButton = new javax.swing.JButton();
			ivjGenerateReportButton.setName("GenerateReportButton");
			ivjGenerateReportButton.setText("Generate Report");
			ivjGenerateReportButton.setMaximumSize(new java.awt.Dimension(75, 25));
			ivjGenerateReportButton.setActionCommand("Generate");
			ivjGenerateReportButton.setPreferredSize(new java.awt.Dimension(75, 25));
			ivjGenerateReportButton.setContentAreaFilled(false);
			ivjGenerateReportButton.setMargin(new java.awt.Insets(2, 12, 2, 12));
			ivjGenerateReportButton.setMinimumSize(new java.awt.Dimension(45, 25));
			// user code begin {1}

				ivjGenerateReportButton.addActionListener(this);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
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
	private javax.swing.JSplitPane getLeftRightSplitPane() {
	if (ivjLeftRightSplitPane == null) {
		try {
			ivjLeftRightSplitPane = new javax.swing.JSplitPane(javax.swing.JSplitPane.HORIZONTAL_SPLIT);
			ivjLeftRightSplitPane.setName("LeftRightSplitPane");
			ivjLeftRightSplitPane.setDividerSize(4);
			ivjLeftRightSplitPane.setPreferredSize(new java.awt.Dimension(721, 202));
			ivjLeftRightSplitPane.setMinimumSize(new java.awt.Dimension(214, 172));
			ivjLeftRightSplitPane.setDividerLocation(206);
			getLeftRightSplitPane().add(getReportDisplayPanel(), "right");
			getLeftRightSplitPane().add(getCheckBoxTreeViewPanel(), "left");
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
	private javax.swing.JMenuBar getMenuBar()
	{
		if (menuBar == null)
		{
			try
			{
				menuBar = new javax.swing.JMenuBar();

				menuBar.add(getReportingMenu());

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
	private javax.swing.JPanel getReportDisplayPanel() {
	if (ivjReportDisplayPanel == null) {
		try {
			ivjReportDisplayPanel = new javax.swing.JPanel();
			ivjReportDisplayPanel.setName("ReportDisplayPanel");
			ivjReportDisplayPanel.setLayout(new java.awt.GridBagLayout());
			ivjReportDisplayPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			ivjReportDisplayPanel.setPreferredSize(new java.awt.Dimension(703, 200));
			ivjReportDisplayPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjReportDisplayPanel.setMinimumSize(new java.awt.Dimension(200, 170));

			java.awt.GridBagConstraints constraintsReportSetupPanel = new java.awt.GridBagConstraints();
			constraintsReportSetupPanel.gridx = 0; constraintsReportSetupPanel.gridy = 0;
			constraintsReportSetupPanel.gridwidth = 3;
			constraintsReportSetupPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReportSetupPanel.weightx = 1.0;
			constraintsReportSetupPanel.weighty = 0.1;
			getReportDisplayPanel().add(getReportSetupPanel(), constraintsReportSetupPanel);

			java.awt.GridBagConstraints constraintsTabbedPane = new java.awt.GridBagConstraints();
			constraintsTabbedPane.gridx = 0; constraintsTabbedPane.gridy = 1;
			constraintsTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsTabbedPane.weightx = 1.0;
			constraintsTabbedPane.weighty = 1.0;
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
	private Reportingmenu getReportingMenu()
	{
		if (reportingMenu == null)
		{
			reportingMenu = new Reportingmenu();

			javax.swing.JMenuItem item;
			for (int i = 0; i < reportingMenu.getItemCount(); i++)
			{
				item = reportingMenu.getItem(i);

				if (item != null)
					reportingMenu.getItem(i).addActionListener(this);
				addMenuItemActionListeners(reportingMenu);
			}
			//reportMenu.getItem(1).addActionListener(this);

		}
		return reportingMenu;
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
			ivjReportSetupPanel.setLayout(new java.awt.GridBagLayout());
			ivjReportSetupPanel.setAlignmentY(java.awt.Component.CENTER_ALIGNMENT);
			ivjReportSetupPanel.setPreferredSize(new java.awt.Dimension(763, 65));
			ivjReportSetupPanel.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjReportSetupPanel.setMinimumSize(new java.awt.Dimension(200, 35));

			java.awt.GridBagConstraints constraintsGenerateReportButton = new java.awt.GridBagConstraints();
			constraintsGenerateReportButton.gridx = 0; constraintsGenerateReportButton.gridy = 0;
			constraintsGenerateReportButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsGenerateReportButton.weightx = 0.5;
			constraintsGenerateReportButton.weighty = 0.5;
			constraintsGenerateReportButton.ipadx = 3;
			constraintsGenerateReportButton.ipady = 1;
			constraintsGenerateReportButton.insets = new java.awt.Insets(10, 5, 5, 100);
			getReportSetupPanel().add(getGenerateReportButton(), constraintsGenerateReportButton);

			java.awt.GridBagConstraints constraintsStartEndPanel = new java.awt.GridBagConstraints();
			constraintsStartEndPanel.gridx = 1; constraintsStartEndPanel.gridy = 0;
			constraintsStartEndPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsStartEndPanel.weightx = 1.0;
			constraintsStartEndPanel.weighty = 1.0;
			constraintsStartEndPanel.insets = new java.awt.Insets(5, 5, 5, 100);
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

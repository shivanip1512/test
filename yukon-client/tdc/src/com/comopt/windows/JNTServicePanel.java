package com.comopt.windows;

import javax.swing.table.AbstractTableModel;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.util.*;

/**
 * @author Owner
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class JNTServicePanel extends JPanel implements TableModelListener
{
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JPanel rButtonPanel;
   private javax.swing.JButton rStartButton;
   private javax.swing.JButton rStopButton;
   private javax.swing.JButton rUninstallButton;
   private javax.swing.JButton rInstallButton;
   private javax.swing.JButton rUpdateButton;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JTable rServicesTable;
   private javax.swing.JPanel rInstallPanel;
   private javax.swing.JLabel rServiceDispNameLabel;
   private javax.swing.JTextField rServiceDisplayNameTextField;
   private javax.swing.JLabel rServiceShortNameLabel;
   private javax.swing.JTextField rServiceShortNameTextField;
   private javax.swing.JLabel rServiceStartTypeLabel;
   private javax.swing.JComboBox rServiceStartTypeCombo;
   private javax.swing.JLabel rServiceArgsLabel;
   private javax.swing.JTextField rServiceArgsTextField;
   // End of variables declaration//GEN-END:variables

   private String[] aServices;
   private DataModel rServicesDataModel;
   /*    private TableSorter rServiceTableSorter; */





	class DataModel extends AbstractTableModel
	{
		private int iRows = 0;
		private int iColumns = 0;
		private Object rDataMatrix[][];
		private String sColumnNames[] =
			new String[] { "Index", "Full Name", "Short Name", "Type", "State" };
		public String getColumnName(int col)
		{
			return sColumnNames[col];
		}
		public int getColumnCount()
		{
			return iColumns;
		}
		public int getRowCount()
		{
			return iRows;
		}
		public Object getValueAt(int row, int col)
		{
			return rDataMatrix[row][col];
		}
		public Class getColumnClass(int col)
		{
			return (
				col == 0
					? (new Integer(0)).getClass()
					: (new String("")).getClass());
		}
		public void populate(String aTokenSeparatedDataArray[])
		{
			if (aTokenSeparatedDataArray == null)
				return;
			iColumns = 5;
			iRows = aTokenSeparatedDataArray.length;
			rDataMatrix = new Object[iRows][iColumns];
			int iIndex = 0;
			while (iIndex < iRows && aTokenSeparatedDataArray[iIndex] != null)
			{
				StringTokenizer sTokenSepData =
					new StringTokenizer(aTokenSeparatedDataArray[iIndex], "|");
				int iColumnIndex = 1;
				rDataMatrix[iIndex][0] = new Integer(iIndex);
				while (sTokenSepData.hasMoreTokens() && iColumnIndex < iColumns)
				{
					String tmp = sTokenSepData.nextToken();
					if (iColumnIndex == 3)
					{
						// Look at service type
						int iType = Integer.parseInt(tmp);
						switch (iType)
						{
							case JNTServices.SERVICE_TYPE_ADAPTER_DRIVER :
								tmp = new String("Adapter Driver");
								break;
							case JNTServices.SERVICE_TYPE_ALL_PROCESS :
								tmp = new String("All Process");
								break;
							case JNTServices.SERVICE_TYPE_FILE_SYSTEM_DRIVER :
								tmp = new String("File System Driver");
								break;
							case JNTServices.SERVICE_TYPE_INTERACTIVE_PROCESS :
								tmp = new String("Interactive Process");
								break;
							case JNTServices.SERVICE_TYPE_KERNEL_DRIVER :
								tmp = new String("Kernel Driver");
								break;
							case JNTServices.SERVICE_TYPE_RECOGNIZER_DRIVER :
								tmp = new String("Recognizer Driver");
								break;
							case JNTServices.SERVICE_TYPE_SERVICE_DRIVER :
								tmp = new String("Service Driver");
								break;
							case JNTServices.SERVICE_TYPE_WIN32 :
								tmp = new String("Win32");
								break;
							case JNTServices.SERVICE_TYPE_WIN32_OWN_PROCESS :
								tmp = new String("Win32 Own Process");
								break;
							case JNTServices.SERVICE_TYPE_WIN32_SHARE_PROCESS :
								tmp = new String("Win32 Share Prcoess");
								break;
							default :
								break;
						}

					}
					if (iColumnIndex == 4)
					{
						// Look at what state the service is in
						int iState = Integer.parseInt(tmp);
						switch (iState)
						{
							case JNTServices.SERVICE_STOPPED :
								tmp = new String("Stopped");
								break;
							case JNTServices.SERVICE_START_PENDING :
								tmp = new String("Start Pending");
								break;
							case JNTServices.SERVICE_STOP_PENDING :
								tmp = new String("Stop Pending");
								break;
							case JNTServices.SERVICE_RUNNING :
								tmp = new String("Running");
								break;
							case JNTServices.SERVICE_CONTINUE_PENDING :
								tmp = new String("Continue Pending");
								break;
							case JNTServices.SERVICE_PAUSE_PENDING :
								tmp = new String("Pause Pending");
								break;
							case JNTServices.SERVICE_PAUSED :
								tmp = new String("Paused");
								break;
							default :
								break;
						}
					}
					rDataMatrix[iIndex][iColumnIndex] = tmp;
					iColumnIndex++;
				}
				// System.out.println( "["+iIndex+"] "+rDataMatrix[iIndex][0]+" "+rDataMatrix[iIndex][1]+" "+rDataMatrix[iIndex][2]+" "+rDataMatrix[iIndex][3] );
				iIndex++;
			}
		}
	};

	/** Creates new form JNTServicesPanel */
	public JNTServicePanel()
	{
		rServicesDataModel = new DataModel();
      JNTServices.getInstance();

		rServicesDataModel.populate(JNTServices.getInstance().getAllServices());
		initComponents();
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	private void initComponents()
	{ //GEN-BEGIN:initComponents
		rButtonPanel = new javax.swing.JPanel();
		rStartButton = new javax.swing.JButton();
		rStopButton = new javax.swing.JButton();
		rUninstallButton = new javax.swing.JButton();
		rInstallButton = new javax.swing.JButton();
		rUpdateButton = new javax.swing.JButton();
		jScrollPane1 = new javax.swing.JScrollPane();
		rServicesTable = new javax.swing.JTable();
		rInstallPanel = new javax.swing.JPanel();
		rServiceDispNameLabel = new javax.swing.JLabel();
		rServiceDisplayNameTextField = new javax.swing.JTextField();
		rServiceShortNameLabel = new javax.swing.JLabel();
		rServiceShortNameTextField = new javax.swing.JTextField();
		rServiceStartTypeLabel = new javax.swing.JLabel();
		rServiceStartTypeCombo = new javax.swing.JComboBox();
		rServiceArgsLabel = new javax.swing.JLabel();
		rServiceArgsTextField = new javax.swing.JTextField();

		setLayout(new java.awt.BorderLayout());

		rButtonPanel.setLayout(new java.awt.GridLayout(5, 1));

		rStartButton.setText("Start");
		rStartButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rStartButtonActionPerformed(evt);
			}
		});

		rButtonPanel.add(rStartButton);

		rStopButton.setText("Stop");
		rStopButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rStopButtonActionPerformed(evt);
			}
		});

		rButtonPanel.add(rStopButton);

		rUninstallButton.setText("Uninstall");
		rUninstallButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rUninstallButtonActionPerformed(evt);
			}
		});

		rButtonPanel.add(rUninstallButton);

		rInstallButton.setText("Install");
		rInstallButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rInstallButtonActionPerformed(evt);
			}
		});

		rButtonPanel.add(rInstallButton);

		rUpdateButton.setText("Update");
		rUpdateButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rUpdateButtonActionPerformed(evt);
			}
		});

		rButtonPanel.add(rUpdateButton);

		add(rButtonPanel, java.awt.BorderLayout.EAST);

		/*        rServiceTableSorter = new TableSorter( rServicesDataModel );
		        (rServiceTableSorter.getModel()).addTableModelListener( this );
		        rServicesTable.setModel(rServiceTableSorter);
		        rServiceTableSorter.addMouseListenerToHeaderInTable( rServicesTable );
		        rServicesTable.getColumnModel().getColumn(0).setPreferredWidth(5);
		*/
		rServicesTable.setModel(rServicesDataModel);
		jScrollPane1.setViewportView(rServicesTable);

		add(jScrollPane1, java.awt.BorderLayout.CENTER);

		rInstallPanel.setLayout(new java.awt.GridBagLayout());
		java.awt.GridBagConstraints gridBagConstraints1;

		rInstallPanel.setBorder(
			new javax.swing.border.TitledBorder("Install Parameters"));
		rServiceDispNameLabel.setText("Service Display Name");
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		rInstallPanel.add(rServiceDispNameLabel, gridBagConstraints1);

		rServiceDisplayNameTextField.setColumns(15);
		rServiceDisplayNameTextField.setText("NULL");
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		rInstallPanel.add(rServiceDisplayNameTextField, gridBagConstraints1);

		rServiceShortNameLabel.setText("Service Short Name");
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		rInstallPanel.add(rServiceShortNameLabel, gridBagConstraints1);

		rServiceShortNameTextField.setColumns(7);
		rServiceShortNameTextField.setText("NULL");
		rServiceShortNameTextField
			.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rServiceShortNameTextFieldActionPerformed(evt);
			}
		});

		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		rInstallPanel.add(rServiceShortNameTextField, gridBagConstraints1);

		rServiceStartTypeLabel.setText("Start Type");
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 0;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		rInstallPanel.add(rServiceStartTypeLabel, gridBagConstraints1);

		rServiceStartTypeCombo.setModel(
			new DefaultComboBoxModel(
				new String[] { "Boot", "System", "Auto", "Demand", "Disabled" }));
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 1;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		rInstallPanel.add(rServiceStartTypeCombo, gridBagConstraints1);

		rServiceArgsLabel.setText("Program & Args");
		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 2;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		rInstallPanel.add(rServiceArgsLabel, gridBagConstraints1);

		rServiceArgsTextField.setColumns(20);
		rServiceArgsTextField.setText("NULL");
		rServiceArgsTextField
			.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				rServiceArgsTextFieldActionPerformed(evt);
			}
		});

		gridBagConstraints1 = new java.awt.GridBagConstraints();
		gridBagConstraints1.gridx = 3;
		gridBagConstraints1.gridy = 1;
		gridBagConstraints1.anchor = java.awt.GridBagConstraints.WEST;
		rInstallPanel.add(rServiceArgsTextField, gridBagConstraints1);

		add(rInstallPanel, java.awt.BorderLayout.SOUTH);

	} //GEN-END:initComponents

	private void rServiceArgsTextFieldActionPerformed(
		java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_rServiceArgsTextFieldActionPerformed
		// Add your handling code here:
	} //GEN-LAST:event_rServiceArgsTextFieldActionPerformed

	private void rServiceShortNameTextFieldActionPerformed(
		java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_rServiceShortNameTextFieldActionPerformed
		// Add your handling code here:
	} //GEN-LAST:event_rServiceShortNameTextFieldActionPerformed

	private void rInstallButtonActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_rInstallButtonActionPerformed
		// Add your handling code here:
		String sShortName = rServiceDisplayNameTextField.getText();
		String sDisplayName = rServiceShortNameTextField.getText();
		String sArgs = rServiceArgsTextField.getText();
		int iStartMode = rServiceStartTypeCombo.getSelectedIndex();
		int errorcode = 0;
		if (sShortName != null
			&& sShortName.length() > 0
			&& sDisplayName != null
			&& sDisplayName.length() > 0
			&& sArgs != null
			&& sArgs.length() > 0)
		{
			errorcode =
				JNTServices.getInstance().justInstall(
					sDisplayName,
					sShortName,
					sArgs,
					iStartMode);
		}
		else
		{
			System.out.println(
				"Service could not be added, check your names and args fields.");
			System.out.println(
				"Service added with; "
					+ sDisplayName
					+ ","
					+ sShortName
					+ ","
					+ sArgs
					+ ","
					+ iStartMode);
			errorcode = -10;
		}
		if (errorcode != 0)
			this.errorDialog(errorcode, sShortName);
		rServicesDataModel.fireTableDataChanged();
	} //GEN-LAST:event_rInstallButtonActionPerformed

	private void rUpdateButtonActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_rUpdateButtonActionPerformed
		// Add your handling code here:
		rServicesDataModel.populate(JNTServices.getInstance().getAllServices());
		rServicesDataModel.fireTableDataChanged();
	} //GEN-LAST:event_rUpdateButtonActionPerformed

	private void rUninstallButtonActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_rUninstallButtonActionPerformed
		// Add your handling code here:
		String sServiceShortName =
			(String) rServicesDataModel.getValueAt(
				rServicesTable.getSelectedRow(),
				2);
		int errorcode = JNTServices.getInstance().uninstall(sServiceShortName);
		if (errorcode != 0)
			this.errorDialog(errorcode, sServiceShortName);
	} //GEN-LAST:event_rUninstallButtonActionPerformed

	private void rStopButtonActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_rStopButtonActionPerformed
		// Add your handling code here:
		String sServiceShortName =
			(String) rServicesDataModel.getValueAt(
				rServicesTable.getSelectedRow(),
				2);
		int errorcode = JNTServices.getInstance().stop(sServiceShortName, 12, 10000);
		if (errorcode != 0)
			this.errorDialog(errorcode, sServiceShortName);
		rServicesDataModel.fireTableDataChanged();
	} //GEN-LAST:event_rStopButtonActionPerformed

	private void rStartButtonActionPerformed(java.awt.event.ActionEvent evt)
	{ //GEN-FIRST:event_rStartButtonActionPerformed
		// Add your handling code here:
		String sServiceShortName =
			(String) rServicesDataModel.getValueAt(
				rServicesTable.getSelectedRow(),
				2);
		int errorcode = JNTServices.getInstance().start(sServiceShortName, 0, null);
		if (errorcode != 0)
			this.errorDialog(errorcode, sServiceShortName);
		rServicesDataModel.fireTableDataChanged();
	} //GEN-LAST:event_rStartButtonActionPerformed

	private void errorDialog(int errorcode, String sServiceName)
	{
		String sErrorMessage = null;
		switch (errorcode)
		{
			case JNTServices.ERROR_ACCESS_DENIED :
				sErrorMessage = new String("Access denied to " + sServiceName);
				break;
			case JNTServices.ERROR_CIRCULAR_DEPENDENCY :
				sErrorMessage = new String("Circular dependcy to " + sServiceName);
				break;
			case JNTServices.ERROR_DEPENDENT_SERVICES_RUNNING :
				sErrorMessage = new String("Dependent service running");
				break;
			case JNTServices.ERROR_DUPLICATE_SERVICE_NAME :
				sErrorMessage = new String("Duplicate name service");
				break;
			case JNTServices.ERROR_DUP_NAME :
				sErrorMessage = new String("Duplicated name; " + sServiceName);
				break;
			case JNTServices.ERROR_FILE_NOT_FOUND :
				sErrorMessage = new String("File not found");
				break;
			case JNTServices.ERROR_INVALID_HANDLE :
				sErrorMessage = new String("Invalid handle");
				break;
			case JNTServices.ERROR_INVALID_NAME :
				sErrorMessage = new String("Invalid name");
				break;
			case JNTServices.ERROR_INVALID_PARAMETER :
				sErrorMessage = new String("Invalid parameter");
				break;
			case JNTServices.ERROR_INVALID_SERVICE_ACCOUNT :
				sErrorMessage = new String("Invalid service account");
				break;
			case JNTServices.ERROR_INVALID_SERVICE_CONTROL :
				sErrorMessage = new String("Invalid service control");
				break;
			case JNTServices.ERROR_PATH_NOT_FOUND :
				sErrorMessage = new String("Path not found");
				break;
			case JNTServices.ERROR_SERVICE_ALREADY_RUNNING :
				sErrorMessage =
					new String("Service(" + sServiceName + ") allready running");
				break;
			case JNTServices.ERROR_SERVICE_CANNOT_ACCEPT_CTRL :
				sErrorMessage = new String("Service can not accept control");
				break;
			case JNTServices.ERROR_SERVICE_DATABASE_LOCKED :
				sErrorMessage = new String("Service database locked");
				break;
			case JNTServices.ERROR_SERVICE_DEPENDENCY_DELETED :
				sErrorMessage = new String("Service dependency deleted");
				break;
			case JNTServices.ERROR_SERVICE_DEPENDENCY_FAIL :
				sErrorMessage = new String("Service dependency failed");
				break;
			case JNTServices.ERROR_SERVICE_DISABLED :
				sErrorMessage =
					new String("Service(" + sServiceName + ") disabled");
				break;
			case JNTServices.ERROR_SERVICE_DOES_NOT_EXIST :
				sErrorMessage =
					new String("Service (" + sServiceName + ") does not exist");
				break;
			case JNTServices.ERROR_SERVICE_EXISTS :
				sErrorMessage = new String("Service (" + sServiceName + ") exists");
				break;
			case JNTServices.ERROR_SERVICE_LOGON_FAILED :
				sErrorMessage = new String("Login failed");
				break;
			case JNTServices.ERROR_SERVICE_MARKED_FOR_DELETE :
				sErrorMessage =
					new String("Service (" + sServiceName + ") marked for delete");
				break;
			case JNTServices.ERROR_SERVICE_NOT_ACTIVE :
				sErrorMessage =
					new String("Service (" + sServiceName + ") not active");
				break;
			case JNTServices.ERROR_SERVICE_NO_THREAD :
				sErrorMessage = new String("No thread");
				break;
			case JNTServices.ERROR_SERVICE_REQUEST_TIMEOUT :
				sErrorMessage = new String("Request timed out");
				break;
			case -10 :
				sErrorMessage =
					new String("The arguments given were either NULL or zero length");
				break;
			default :
				sErrorMessage = new String("Errorcode; " + errorcode);
				break;
		}
		JOptionPane.showMessageDialog(
			this,
			sErrorMessage,
			"NT Services Error",
			JOptionPane.ERROR_MESSAGE);
	}

	public void tableChanged(TableModelEvent evt)
	{
		// System.out.println( "Table model event; "+evt );
	}


	public static void main(String args[])
	{
		JFrame rServicesFrame = new JFrame();
		JNTServicePanel rPanel = new JNTServicePanel();

		rServicesFrame.getContentPane().add(rPanel);
		rServicesFrame.pack();
		rServicesFrame.show();
	}

}

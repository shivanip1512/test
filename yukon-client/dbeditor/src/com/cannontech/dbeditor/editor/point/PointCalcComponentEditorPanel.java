package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import java.util.Collections;

import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.cache.functions.PointFuncs;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.CalculatedPoint;
import com.cannontech.database.data.point.CalcStatusPoint;
import com.cannontech.database.db.point.calculation.CalcComponent;
import com.cannontech.database.db.point.calculation.CalcComponentTypes;

public class PointCalcComponentEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener {

	private class DevicePointOperandLite
	{
		private LiteYukonPAObject liteDevice = null;
		private LitePoint litePoint = null;

		public DevicePointOperandLite(LiteYukonPAObject ld, com.cannontech.database.data.lite.LitePoint lp)
		{
			liteDevice = ld;
			litePoint = lp;
		}

		public LiteYukonPAObject getLiteDevice()
		{
			return this.liteDevice;
		}

		public LitePoint getLitePoint()
		{
			return this.litePoint;
		}

		public String toString()
		{
			return getLiteDevice().toString() + "/" + getLitePoint().toString();
		}
	}
	private javax.swing.JButton ivjAddComponentButton = null;
	private javax.swing.JButton ivjRemoveComponentButton = null;
	private javax.swing.JComboBox ivjComponentTypeComboBox = null;
	private javax.swing.JComboBox ivjDeviceComboBox = null;
	private javax.swing.JComboBox ivjPointComboBox = null;
	private javax.swing.JScrollPane ivjComponentsScrollPane = null;
	private javax.swing.JTable ivjComponentsTable = null;
	private javax.swing.JTextField ivjConstantTextField = null;
	private javax.swing.JButton ivjEditComponentButton = null;
	private javax.swing.JComboBox ivjOperationFunctionComboBox = null;
	private javax.swing.JComboBox selectBaselineComboBox = null;
	private javax.swing.JLabel ivjOperandLabel = null;
	private javax.swing.JLabel ivjOperationFunctionLabel = null;
	private javax.swing.JLabel ivjTypeLabel = null;
	//private java.util.List points = null;
	private javax.swing.JCheckBox ivjUsePointCheckBox = null;
	private javax.swing.JPanel ivjJPanelOperations = null;
	private java.util.Vector basilHolder = null;
	private Integer currentlyMappedBaselineID = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointCalcComponentEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getComponentTypeComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getDeviceComboBox()) 
		connEtoC2(e);
	if (e.getSource() == getUsePointCheckBox()) 
		connEtoC3(e);
	if (e.getSource() == getEditComponentButton()) 
		connEtoC4(e);
	if (e.getSource() == getRemoveComponentButton()) 
		connEtoC6(e);
	if (e.getSource() == getAddComponentButton()) 
		connEtoC5(e);
	if (e.getSource() == getOperationFunctionComboBox())
		functionComboBox_ActionPerformed(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void addComponentButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	boolean componentValid = false;
	java.util.Vector calcComponentVector = new java.util.Vector(3);

	calcComponentVector.addElement(getComponentTypeComboBox().getSelectedItem());
	String selType = getComponentTypeComboBox().getSelectedItem().toString();

	if( CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(selType) )
	{
		calcComponentVector.addElement( new DevicePointOperandLite (
																		(LiteYukonPAObject)getDeviceComboBox().getSelectedItem(),
																		(LitePoint)getPointComboBox().getSelectedItem() ) );
		componentValid = true;
	}
	else if( CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(selType) )
	{
		try
		{
			Double constantValue = new Double(getConstantTextField().getText());
			calcComponentVector.addElement(constantValue.toString());
			componentValid = true;
		}
		catch(NumberFormatException nfe)
		{
			com.cannontech.clientutils.CTILogger.error( nfe.getMessage(), nfe );
		}
	}
	else
	{
		if( getUsePointCheckBox().isSelected() )
			calcComponentVector.addElement( new DevicePointOperandLite (
																			(LiteYukonPAObject)getDeviceComboBox().getSelectedItem(),
																			(LitePoint)getPointComboBox().getSelectedItem() ) );
		else
			calcComponentVector.addElement("");

		componentValid = true;
	}

	if( ((String)getOperationFunctionComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION) )
	{
		currentlyMappedBaselineID = new Integer(((com.cannontech.database.data.lite.LiteBaseline)getSelectBaselineComboBox().getSelectedItem()).getBaselineID());
		
	}
		
	calcComponentVector.addElement(getOperationFunctionComboBox().getSelectedItem());
	
	if( componentValid )
	{
		getTableModel().addRow(calcComponentVector);
		fireInputUpdate();
		repaint();
	}
}
/**
 * Comment
 */
public void componentsTable_MousePressed(java.awt.event.MouseEvent mouseEvent) {
	if( getComponentsTable().getSelectedRowCount() == 1 )
	{
		int selectedRow = getComponentsTable().getSelectedRow();
		String type = (String)getComponentsTable().getModel().getValueAt(selectedRow,0);
		Object operand = getComponentsTable().getModel().getValueAt(selectedRow,1);
		String operation = (String)getComponentsTable().getModel().getValueAt(selectedRow,2);
		getComponentTypeComboBox().setSelectedItem(type);

		if( CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(type) )
		{
			getDeviceComboBox().setSelectedItem(((DevicePointOperandLite)operand).getLiteDevice());
			getPointComboBox().setSelectedItem(((DevicePointOperandLite)operand).getLitePoint());
		}
		else if( CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(type) )
		{
			getConstantTextField().setText(operand.toString());
		}
		else if( CalcComponentTypes.FUNCTION_COMP_TYPE.equalsIgnoreCase(type) )
		{
			if( operand instanceof DevicePointOperandLite )
			{
				getDeviceComboBox().setSelectedItem(((DevicePointOperandLite)operand).getLiteDevice());
				getPointComboBox().setSelectedItem(((DevicePointOperandLite)operand).getLitePoint());
			}
			else
				getUsePointCheckBox().setSelected(false);
		}
		getOperationFunctionComboBox().setSelectedItem(operation);
	}
}
/**
 * Comment
 */
public void componentTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	if( getOperationFunctionComboBox().getModel().getSize() > 0 )
		getOperationFunctionComboBox().removeAllItems();

	if( ((String)getComponentTypeComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.OPERATION_COMP_TYPE) )
	{
		getDeviceComboBox().setVisible(true);
		getPointComboBox().setVisible(true);
		getDeviceComboBox().setEnabled(true);
		getPointComboBox().setEnabled(true);
		getConstantTextField().setVisible(false);
		getUsePointCheckBox().setVisible(false);

		for( int i = 0; i < CalcComponentTypes.CALC_OPERATIONS.length; i++ )
			getOperationFunctionComboBox().addItem( CalcComponentTypes.CALC_OPERATIONS[i] );

		revalidate();
		repaint();
	}
	else if( ((String)getComponentTypeComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.CONSTANT_COMP_TYPE) )
	{
		// we want the textfield and the comboBox to be the same size so the
		// layout manager does not need to do any work!
		getConstantTextField().setSize( getDeviceComboBox().getSize() );
		getPointComboBox().setVisible(true);
		getPointComboBox().setEnabled(false);		
		getConstantTextField().setVisible(true);
		getDeviceComboBox().setVisible(false);		
		getDeviceComboBox().setEnabled(true);
		getUsePointCheckBox().setVisible(false);

		for( int i = 0; i < CalcComponentTypes.CALC_OPERATIONS.length; i++ )
			getOperationFunctionComboBox().addItem( CalcComponentTypes.CALC_OPERATIONS[i] );

		revalidate();
		repaint();
	}
	else if( ((String)getComponentTypeComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.FUNCTION_COMP_TYPE) )
	{
		getDeviceComboBox().setVisible(true);
		getPointComboBox().setVisible(true);
		getConstantTextField().setVisible(false);
		getUsePointCheckBox().setVisible(true);
		getPointComboBox().setEnabled(true);
		getUsePointCheckBox().setSelected(true);

		for( int i = 0; i < CalcComponentTypes.CALC_FUNCTIONS.length; i++ )
			getOperationFunctionComboBox().addItem( CalcComponentTypes.CALC_FUNCTIONS[i] );

		revalidate();
		repaint();
	}

	return;
}

public void functionComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	if(getOperationFunctionComboBox().getModel().getSize() > 0)
	{
		if( ((String)getOperationFunctionComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION) )
		{
			getSelectBaselineComboBox().removeAllItems();
		
			for(int i = 0; i < getBasilHolder().size(); i++)
			{
				getSelectBaselineComboBox().addItem(getBasilHolder().elementAt(i));
			}
			getSelectBaselineComboBox().setVisible(true);
		
			if(currentlyMappedBaselineID != null)
			{
				for(int j = 0; j < getBasilHolder().size(); j++)
				{
					if(((LiteBaseline)getBasilHolder().get(j)).getBaselineID() == currentlyMappedBaselineID.intValue())
					{
						getSelectBaselineComboBox().setSelectedIndex(j);
					}
				}
			}
			
			revalidate();
			repaint();
		}
		else
		{
			getSelectBaselineComboBox().setVisible(false);
		
			revalidate();
			repaint();
		}
	}
	return;
}
/**
 * connEtoC1:  (ComponentTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointCalcComponentEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.componentTypeComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DeviceComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointCalcComponentEditorPanel.deviceComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.deviceComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (UsePointCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointCalcComponentEditorPanel.usePointCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.usePointCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (EditComponentButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointCalcComponentEditorPanel.editComponentButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.editComponentButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (AddComponentButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointCalcComponentEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addComponentButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (EditComponentButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointCalcComponentEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.removeComponentButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (ComponentsTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> PointCalcComponentEditorPanel.componentsTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.componentsTable_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void deviceComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	int deviceID = ((LiteYukonPAObject) getDeviceComboBox().getSelectedItem()).getYukonID();

	if( getPointComboBox().getModel().getSize() > 0 )
		getPointComboBox().removeAllItems();


	LitePoint[] paoPoints =
			PAOFuncs.getLitePointsForPAObject( deviceID );
	
	for( int j = 0; j < paoPoints.length; j++ )
		getPointComboBox().addItem( paoPoints[j] );
}
/**
 * Comment
 */
public void editComponentButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	boolean componentValid = false;
	int selectedRow = getComponentsTable().getSelectedRow();
	java.util.Vector calcComponentVector = new java.util.Vector(3);

	calcComponentVector.addElement(getComponentTypeComboBox().getSelectedItem());
	String selType = getComponentTypeComboBox().getSelectedItem().toString();
	
	if( CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(selType) )
	{
		calcComponentVector.addElement( new DevicePointOperandLite( (LiteYukonPAObject)getDeviceComboBox().getSelectedItem(),
																		(LitePoint)getPointComboBox().getSelectedItem()) );
		componentValid = true;
	}
	else if( CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(selType) )
	{
		try
		{
			Double constantValue = new Double(getConstantTextField().getText());
			calcComponentVector.addElement(constantValue.toString());
			componentValid = true;
		}
		catch(NumberFormatException nfe)
		{
			com.cannontech.clientutils.CTILogger.error( nfe.getMessage(), nfe );
		}
	}
	else
	{
		if( getUsePointCheckBox().isSelected() )
			calcComponentVector.addElement( new DevicePointOperandLite( (LiteYukonPAObject)getDeviceComboBox().getSelectedItem(),
																		(LitePoint)getPointComboBox().getSelectedItem()) );
		else
			calcComponentVector.addElement("");

		componentValid = true;
	}
	calcComponentVector.addElement(getOperationFunctionComboBox().getSelectedItem());
	
	if( ((String)getOperationFunctionComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION) )
		{
			currentlyMappedBaselineID = new Integer(((com.cannontech.database.data.lite.LiteBaseline)getSelectBaselineComboBox().getSelectedItem()).getBaselineID());
		}
	else
		currentlyMappedBaselineID = null;

	if( componentValid )
	{
		getTableModel().editRow(selectedRow, calcComponentVector);
		fireInputUpdate();
		repaint();
	}
}
/**
 * Return the AddComponentButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddComponentButton() {
	if (ivjAddComponentButton == null) {
		try {
			ivjAddComponentButton = new javax.swing.JButton();
			ivjAddComponentButton.setName("AddComponentButton");
			ivjAddComponentButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAddComponentButton.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddComponentButton;
}
/**
 * Return the ComponentsScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getComponentsScrollPane() {
	if (ivjComponentsScrollPane == null) {
		try {
			ivjComponentsScrollPane = new javax.swing.JScrollPane();
			ivjComponentsScrollPane.setName("ComponentsScrollPane");
			ivjComponentsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjComponentsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjComponentsScrollPane.setPreferredSize(new java.awt.Dimension(200, 180));
			ivjComponentsScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjComponentsScrollPane.setMinimumSize(new java.awt.Dimension(200, 180));
			getComponentsScrollPane().setViewportView(getComponentsTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjComponentsScrollPane;
}
/**
 * Return the ComponentsTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getComponentsTable() {
	if (ivjComponentsTable == null) {
		try {
			ivjComponentsTable = new javax.swing.JTable();
			ivjComponentsTable.setName("ComponentsTable");
			getComponentsScrollPane().setColumnHeaderView(ivjComponentsTable.getTableHeader());
			ivjComponentsTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjComponentsTable.setPreferredSize(new java.awt.Dimension(200, 8000));
			ivjComponentsTable.setBounds(0, 0, 396, 177);
			ivjComponentsTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
			ivjComponentsTable.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
			// user code begin {1}
			ivjComponentsTable.createDefaultColumnsFromModel();
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjComponentsTable;
}
/**
 * Return the ComponentTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getComponentTypeComboBox() {
	if (ivjComponentTypeComboBox == null) {
		try {
			ivjComponentTypeComboBox = new javax.swing.JComboBox();
			ivjComponentTypeComboBox.setName("ComponentTypeComboBox");
			ivjComponentTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjComponentTypeComboBox.setMinimumSize(new java.awt.Dimension(130, 28));
			ivjComponentTypeComboBox.setMaximumSize(new java.awt.Dimension(130, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjComponentTypeComboBox;
}
/**
 * Return the ConstantTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getConstantTextField() {
	if (ivjConstantTextField == null) {
		try {
			ivjConstantTextField = new javax.swing.JTextField();
			ivjConstantTextField.setName("ConstantTextField");
			ivjConstantTextField.setMaximumSize(new java.awt.Dimension(130, 24));
			ivjConstantTextField.setColumns(0);
			ivjConstantTextField.setPreferredSize(new java.awt.Dimension(130, 24));
			ivjConstantTextField.setFont(new java.awt.Font("dialog", 0, 14));
			ivjConstantTextField.setMinimumSize(new java.awt.Dimension(130, 24));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConstantTextField;
}
/**
 * Return the DeviceComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDeviceComboBox() {
	if (ivjDeviceComboBox == null) {
		try {
			ivjDeviceComboBox = new javax.swing.JComboBox();
			ivjDeviceComboBox.setName("DeviceComboBox");
			ivjDeviceComboBox.setOpaque(true);
			ivjDeviceComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDeviceComboBox.setMinimumSize(new java.awt.Dimension(130, 28));
			// user code begin {1}
			


			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List devices = cache.getAllDevices();
				Collections.sort( devices, LiteComparators.liteStringComparator );

				for (int i = 0; i < devices.size(); i++)
				{
					LiteYukonPAObject pao = (LiteYukonPAObject)devices.get(i);

					ivjDeviceComboBox.addItem( pao );

					if( i == 0 )
					{
						LitePoint[] paoPoints =
								PAOFuncs.getLitePointsForPAObject( pao.getYukonID() );
				
						for( int j = 0; j < paoPoints.length; j++ )
							getPointComboBox().addItem( paoPoints[j] ); 
					}						
				}
			}

			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeviceComboBox;
}
/**
 * Return the EditComponentButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getEditComponentButton() {
	if (ivjEditComponentButton == null) {
		try {
			ivjEditComponentButton = new javax.swing.JButton();
			ivjEditComponentButton.setName("EditComponentButton");
			ivjEditComponentButton.setText("Update");
			ivjEditComponentButton.setMaximumSize(new java.awt.Dimension(150, 31));
			ivjEditComponentButton.setPreferredSize(new java.awt.Dimension(130, 31));
			ivjEditComponentButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjEditComponentButton.setContentAreaFilled(true);
			ivjEditComponentButton.setMinimumSize(new java.awt.Dimension(130, 31));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEditComponentButton;
}
/**
 * Return the JPanelOperations property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelOperations() {
	if (ivjJPanelOperations == null) {
		try {
			ivjJPanelOperations = new javax.swing.JPanel();
			ivjJPanelOperations.setName("JPanelOperations");
			ivjJPanelOperations.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAddComponentButton = new java.awt.GridBagConstraints();
			constraintsAddComponentButton.gridx = 1; constraintsAddComponentButton.gridy = 4;
			constraintsAddComponentButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAddComponentButton.ipadx = 71;
			constraintsAddComponentButton.insets = new java.awt.Insets(6, 5, 4, 5);
			getJPanelOperations().add(getAddComponentButton(), constraintsAddComponentButton);

			java.awt.GridBagConstraints constraintsRemoveComponentButton = new java.awt.GridBagConstraints();
			constraintsRemoveComponentButton.gridx = 3; constraintsRemoveComponentButton.gridy = 4;
			constraintsRemoveComponentButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRemoveComponentButton.ipadx = 43;
			constraintsRemoveComponentButton.insets = new java.awt.Insets(6, 5, 4, 3);
			getJPanelOperations().add(getRemoveComponentButton(), constraintsRemoveComponentButton);

			java.awt.GridBagConstraints constraintsComponentTypeComboBox = new java.awt.GridBagConstraints();
			constraintsComponentTypeComboBox.gridx = 1; constraintsComponentTypeComboBox.gridy = 2;
			constraintsComponentTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsComponentTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsComponentTypeComboBox.weightx = 1.0;
			constraintsComponentTypeComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOperations().add(getComponentTypeComboBox(), constraintsComponentTypeComboBox);

			java.awt.GridBagConstraints constraintsDeviceComboBox = new java.awt.GridBagConstraints();
			constraintsDeviceComboBox.gridx = 2; constraintsDeviceComboBox.gridy = 2;
			constraintsDeviceComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDeviceComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDeviceComboBox.weightx = 1.0;
			constraintsDeviceComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOperations().add(getDeviceComboBox(), constraintsDeviceComboBox);

			java.awt.GridBagConstraints constraintsPointComboBox = new java.awt.GridBagConstraints();
			constraintsPointComboBox.gridx = 2; constraintsPointComboBox.gridy = 3;
			constraintsPointComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPointComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPointComboBox.weightx = 1.0;
			constraintsPointComboBox.ipady = 1;
			constraintsPointComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOperations().add(getPointComboBox(), constraintsPointComboBox);

			java.awt.GridBagConstraints constraintsOperationFunctionComboBox = new java.awt.GridBagConstraints();
			constraintsOperationFunctionComboBox.gridx = 3; constraintsOperationFunctionComboBox.gridy = 2;
			constraintsOperationFunctionComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOperationFunctionComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOperationFunctionComboBox.weightx = 1.0;
			constraintsOperationFunctionComboBox.insets = new java.awt.Insets(5, 5, 5, 3);
			getJPanelOperations().add(getOperationFunctionComboBox(), constraintsOperationFunctionComboBox);

			java.awt.GridBagConstraints constraintsSelectBaselineComboBox = new java.awt.GridBagConstraints();
			constraintsSelectBaselineComboBox.gridx = 3; constraintsOperationFunctionComboBox.gridy = 3;
			constraintsSelectBaselineComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSelectBaselineComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSelectBaselineComboBox.weightx = 1.0;
			constraintsSelectBaselineComboBox.insets = new java.awt.Insets(5, 5, 5, 3);
			getJPanelOperations().add(getSelectBaselineComboBox(), constraintsSelectBaselineComboBox);
			
			java.awt.GridBagConstraints constraintsEditComponentButton = new java.awt.GridBagConstraints();
			constraintsEditComponentButton.gridx = 2; constraintsEditComponentButton.gridy = 4;
			constraintsEditComponentButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEditComponentButton.insets = new java.awt.Insets(5, 5, 3, 5);
			getJPanelOperations().add(getEditComponentButton(), constraintsEditComponentButton);

			java.awt.GridBagConstraints constraintsConstantTextField = new java.awt.GridBagConstraints();
			constraintsConstantTextField.gridx = 2; constraintsConstantTextField.gridy = 2;
			constraintsConstantTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsConstantTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsConstantTextField.weightx = 1.0;
			constraintsConstantTextField.insets = new java.awt.Insets(7, 5, 7, 5);
			getJPanelOperations().add(getConstantTextField(), constraintsConstantTextField);

			java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
			constraintsTypeLabel.gridx = 1; constraintsTypeLabel.gridy = 1;
			constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTypeLabel.ipadx = 98;
			constraintsTypeLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOperations().add(getTypeLabel(), constraintsTypeLabel);

			java.awt.GridBagConstraints constraintsOperandLabel = new java.awt.GridBagConstraints();
			constraintsOperandLabel.gridx = 2; constraintsOperandLabel.gridy = 1;
			constraintsOperandLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOperandLabel.ipadx = 74;
			constraintsOperandLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelOperations().add(getOperandLabel(), constraintsOperandLabel);

			java.awt.GridBagConstraints constraintsOperationFunctionLabel = new java.awt.GridBagConstraints();
			constraintsOperationFunctionLabel.gridx = 3; constraintsOperationFunctionLabel.gridy = 1;
			constraintsOperationFunctionLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOperationFunctionLabel.ipadx = 67;
			constraintsOperationFunctionLabel.insets = new java.awt.Insets(5, 5, 5, 3);
			getJPanelOperations().add(getOperationFunctionLabel(), constraintsOperationFunctionLabel);

			java.awt.GridBagConstraints constraintsUsePointCheckBox = new java.awt.GridBagConstraints();
			constraintsUsePointCheckBox.gridx = 1; constraintsUsePointCheckBox.gridy = 3;
			constraintsUsePointCheckBox.ipadx = 45;
			constraintsUsePointCheckBox.insets = new java.awt.Insets(7, 5, 8, 5);
			getJPanelOperations().add(getUsePointCheckBox(), constraintsUsePointCheckBox);
			// user code begin {1}
			getSelectBaselineComboBox().setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelOperations;
}
/**
 * Return the OperandLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOperandLabel() {
	if (ivjOperandLabel == null) {
		try {
			ivjOperandLabel = new javax.swing.JLabel();
			ivjOperandLabel.setName("OperandLabel");
			ivjOperandLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOperandLabel.setText("Operand");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOperandLabel;
}
/**
 * Return the OperationComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getOperationFunctionComboBox() {
	if (ivjOperationFunctionComboBox == null) {
		try {
			ivjOperationFunctionComboBox = new javax.swing.JComboBox();
			ivjOperationFunctionComboBox.setName("OperationFunctionComboBox");
			ivjOperationFunctionComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOperationFunctionComboBox.setMinimumSize(new java.awt.Dimension(130, 28));
			ivjOperationFunctionComboBox.setMaximumSize(new java.awt.Dimension(130, 28));
			// user code begin {1}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOperationFunctionComboBox;
}

private javax.swing.JComboBox getSelectBaselineComboBox() {
	if (selectBaselineComboBox == null) {
		try {
			selectBaselineComboBox = new javax.swing.JComboBox();
			selectBaselineComboBox.setName("SelectBaselineComboBox");
			selectBaselineComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			selectBaselineComboBox.setMinimumSize(new java.awt.Dimension(130, 28));
			selectBaselineComboBox.setMaximumSize(new java.awt.Dimension(130, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return selectBaselineComboBox;
}

private java.util.Vector getBasilHolder()
{
	if( basilHolder == null)
		basilHolder = new java.util.Vector();
	return basilHolder;
}

private Integer getCurrentlyMappedBaselineID()
{
	if( currentlyMappedBaselineID == null)
		currentlyMappedBaselineID = new Integer(0);
	return currentlyMappedBaselineID;
}

/**
 * Return the OperationFunctionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOperationFunctionLabel() {
	if (ivjOperationFunctionLabel == null) {
		try {
			ivjOperationFunctionLabel = new javax.swing.JLabel();
			ivjOperationFunctionLabel.setName("OperationFunctionLabel");
			ivjOperationFunctionLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOperationFunctionLabel.setText("Operation");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOperationFunctionLabel;
}
/**
 * Return the PointComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPointComboBox() {
	if (ivjPointComboBox == null) {
		try {
			ivjPointComboBox = new javax.swing.JComboBox();
			ivjPointComboBox.setName("PointComboBox");
			ivjPointComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointComboBox.setMinimumSize(new java.awt.Dimension(130, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointComboBox;
}
/**
 * Return the RemoveComponentButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRemoveComponentButton() {
	if (ivjRemoveComponentButton == null) {
		try {
			ivjRemoveComponentButton = new javax.swing.JButton();
			ivjRemoveComponentButton.setName("RemoveComponentButton");
			ivjRemoveComponentButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRemoveComponentButton.setText("Remove");
			ivjRemoveComponentButton.setContentAreaFilled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveComponentButton;
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 3:02:15 PM)
 * @return com.cannontech.common.gui.util.CalcComponentTableModel
 */
private com.cannontech.common.gui.util.CalcComponentTableModel getTableModel() {
	return ((com.cannontech.common.gui.util.CalcComponentTableModel)getComponentsTable().getModel());
}
/**
 * Return the TypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTypeLabel() {
	if (ivjTypeLabel == null) {
		try {
			ivjTypeLabel = new javax.swing.JLabel();
			ivjTypeLabel.setName("TypeLabel");
			ivjTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTypeLabel.setText("Type");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTypeLabel;
}
/**
 * Return the UsePointCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getUsePointCheckBox() {
	if (ivjUsePointCheckBox == null) {
		try {
			ivjUsePointCheckBox = new javax.swing.JCheckBox();
			ivjUsePointCheckBox.setName("UsePointCheckBox");
			ivjUsePointCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUsePointCheckBox.setText("Use Point");
			ivjUsePointCheckBox.setMargin(new java.awt.Insets(0, 2, 0, 2));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsePointCheckBox;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	com.cannontech.database.data.point.PointBase calcPoint;
	
	if(val instanceof CalcStatusPoint)
	{
		calcPoint = (CalcStatusPoint) val;
	}
	else
	{
		calcPoint = (CalculatedPoint) val;
	}

	Integer pointID = calcPoint.getPoint().getPointID();
	com.cannontech.database.db.point.calculation.CalcComponent newCalcComponent = null;
	java.util.Vector calcComponentsVector = null;

	if (getComponentsTable().getRowCount() > 0)
	{
		calcComponentsVector = new java.util.Vector();
		String type = null;
		Object operand = null;
		String operation = null;

		for (int i = 0; i < getComponentsTable().getRowCount(); i++)
		{
			type = (String) getComponentsTable().getModel().getValueAt(i, 0);
			operand = getComponentsTable().getModel().getValueAt(i, 1);
			operation = (String) getComponentsTable().getModel().getValueAt(i, 2);

			newCalcComponent =
				new com.cannontech.database.db.point.calculation.CalcComponent(
					pointID,
					new Integer(i + 1),
					type,
					new Integer(0),
					com.cannontech.common.util.CtiUtilities.STRING_NONE,
					new Double(0.0),
					com.cannontech.common.util.CtiUtilities.STRING_NONE );

			if( CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(type) )
			{
				newCalcComponent.setComponentPointID(new Integer(((DevicePointOperandLite) operand).getLitePoint().getPointID()));
				newCalcComponent.setOperation((String) operation);
			}
			else if( CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(type) )
			{
				newCalcComponent.setConstant(new Double(operand.toString()));
				newCalcComponent.setOperation((String) operation);
			}
			else if( CalcComponentTypes.FUNCTION_COMP_TYPE.equalsIgnoreCase(type) )
			{
				if (operand instanceof DevicePointOperandLite)
					newCalcComponent.setComponentPointID(new Integer(((DevicePointOperandLite) operand).getLitePoint().getPointID()));

				newCalcComponent.setFunctionName((String) operation);
				
				if( newCalcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION) && currentlyMappedBaselineID != null )
				{
					if(val instanceof CalcStatusPoint)
					{
						((CalcStatusPoint)calcPoint).getCalcBaselinePoint().setBaselineID(currentlyMappedBaselineID);
						((CalcStatusPoint)calcPoint).setBaselineAssigned(true);
					}
					else
					{
						((CalculatedPoint)calcPoint).getCalcBaselinePoint().setBaselineID(currentlyMappedBaselineID);
						((CalculatedPoint)calcPoint).setBaselineAssigned(true);
					}
				}

			}

			calcComponentsVector.addElement(newCalcComponent);
		}
	}

	if(calcPoint instanceof CalcStatusPoint)
		((CalcStatusPoint)calcPoint).setCalcComponentVector(calcComponentsVector);
	else
		((CalculatedPoint)calcPoint).setCalcComponentVector(calcComponentsVector);
	
	return val;

}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getComponentTypeComboBox().addActionListener(this);
	getDeviceComboBox().addActionListener(this);
	getUsePointCheckBox().addActionListener(this);
	getEditComponentButton().addActionListener(this);
	getRemoveComponentButton().addActionListener(this);
	getAddComponentButton().addActionListener(this);
	getOperationFunctionComboBox().addActionListener(this);
	getComponentsTable().addMouseListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointCalcComponentPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(427, 366);

		java.awt.GridBagConstraints constraintsJPanelOperations = new java.awt.GridBagConstraints();
		constraintsJPanelOperations.gridx = 1; constraintsJPanelOperations.gridy = 2;
		constraintsJPanelOperations.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelOperations.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelOperations.weightx = 1.0;
		constraintsJPanelOperations.weighty = 1.0;
		constraintsJPanelOperations.insets = new java.awt.Insets(8, 4, 15, 5);
		add(getJPanelOperations(), constraintsJPanelOperations);

		java.awt.GridBagConstraints constraintsComponentsScrollPane = new java.awt.GridBagConstraints();
		constraintsComponentsScrollPane.gridx = 1; constraintsComponentsScrollPane.gridy = 1;
		constraintsComponentsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsComponentsScrollPane.anchor = java.awt.GridBagConstraints.WEST;
		constraintsComponentsScrollPane.weightx = 1.0;
		constraintsComponentsScrollPane.weighty = 1.0;
		constraintsComponentsScrollPane.ipadx = 218;
		constraintsComponentsScrollPane.insets = new java.awt.Insets(11, 4, 8, 5);
		add(getComponentsScrollPane(), constraintsComponentsScrollPane);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//fill up Component Type combo box
	getComponentTypeComboBox().addItem( CalcComponentTypes.OPERATION_COMP_TYPE );
	getComponentTypeComboBox().addItem( CalcComponentTypes.CONSTANT_COMP_TYPE );
	getComponentTypeComboBox().addItem( CalcComponentTypes.FUNCTION_COMP_TYPE );

	getComponentsTable().setModel(new com.cannontech.common.gui.util.CalcComponentTableModel());
	((com.cannontech.common.gui.util.CalcComponentTableModel)getComponentsTable().getModel()).makeTable();

	getComponentsTable().getColumnModel().getColumn(1).setWidth(200);
	getComponentsTable().getColumnModel().getColumn(1).setPreferredWidth(200);
	getComponentsTable().revalidate();
	getComponentsTable().repaint();
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointCalcComponentEditorPanel aPointCalcComponentEditorPanel;
		aPointCalcComponentEditorPanel = new PointCalcComponentEditorPanel();
		frame.setContentPane(aPointCalcComponentEditorPanel);
		frame.setSize(aPointCalcComponentEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getComponentsTable()) 
		connEtoC7(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void removeComponentButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {

	int selectedRowCount = getComponentsTable().getSelectedRowCount();
	
	if( ((String)getOperationFunctionComboBox().getSelectedItem()).equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION) )
		{
			currentlyMappedBaselineID = null;
		}

	if( selectedRowCount > 0 )
	{
		int selectedRow = getComponentsTable().getSelectedRows()[0];
		for(int i=0;i<selectedRowCount;i++)
			getTableModel().removeRow(selectedRow);
		fireInputUpdate();
		repaint();
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)
{
	com.cannontech.database.data.point.PointBase calcPoint;
	
	if(val instanceof CalcStatusPoint)
	{
		calcPoint = (CalcStatusPoint) val;
	}
	else
	{
		calcPoint = (CalculatedPoint) val;
	}
	
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
//		java.util.List devices = cache.getAllDevices();
//		Collections.sort( devices, LiteComparators.liteStringComparator );

		//points = new java.util.Vector();
/*
		List cachePoints = cache.getAllPoints();
		LitePoint point = null;


		Collections.sort( cachePoints, LiteComparators.litePointDeviceIDComparator);
*/
//		for (int i = 0; i < devices.size(); i++)
//		{
//			LiteYukonPAObject pao = (LiteYukonPAObject)devices.get(i);
//
//			getDeviceComboBox().addItem( pao );
//
//			if( i == 0 )
//			{
//				LitePoint[] paoPoints =
//						PAOFuncs.getLitePointsForPAObject( pao.getYukonID() );
//				
//				for( int j = 0; j < paoPoints.length; j++ )
//					getPointComboBox().addItem( paoPoints[j] ); 
//			}
//		}

		//fill in the calc components of this point
		java.util.Vector calcComponents = new java.util.Vector();
		
		if(calcPoint instanceof CalcStatusPoint)
			calcComponents = ((CalcStatusPoint)calcPoint).getCalcComponentVector();
		else
			 calcComponents = ((CalculatedPoint)calcPoint).getCalcComponentVector();
		com.cannontech.database.data.lite.LiteBaseline temp = new com.cannontech.database.data.lite.LiteBaseline();
		basilHolder = temp.getAllBaselines();
 
		String type = null;
		Object operand = null;
		LitePoint litePoint = null;
		
		int componentPointID = 0;
		java.util.Vector calcComponentEntry = null;
		CalcComponent singleCalcComponent = null;

		for (int i = 0; i < calcComponents.size(); i++)
		{
			calcComponentEntry = new java.util.Vector(3);
			singleCalcComponent = (CalcComponent) calcComponents.get(i);
			
			//get and add the type
			type = singleCalcComponent.getComponentType();
			calcComponentEntry.addElement(type);

			if( CalcComponentTypes.OPERATION_COMP_TYPE.equalsIgnoreCase(type) )
			{
				componentPointID = singleCalcComponent.getComponentPointID().intValue();

				litePoint = PointFuncs.getLitePoint(componentPointID);

				operand = new DevicePointOperandLite(
									PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() ), 
									litePoint);

				calcComponentEntry.addElement( operand );

				calcComponentEntry.addElement( 
						singleCalcComponent.getOperation() );
			}
			else if( CalcComponentTypes.CONSTANT_COMP_TYPE.equalsIgnoreCase(type) )
			{
				operand = ((CalcComponent) calcComponents.get(i)).getConstant().toString();
				calcComponentEntry.addElement(operand);

				calcComponentEntry.addElement(
						singleCalcComponent.getOperation() );
			}
			else if( CalcComponentTypes.FUNCTION_COMP_TYPE.equalsIgnoreCase(type) )
			{
							
				componentPointID = singleCalcComponent.getComponentPointID().intValue();
				if (componentPointID > 0)
				{
					litePoint = PointFuncs.getLitePoint(componentPointID);

					operand = new DevicePointOperandLite(
										PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() ), 
										litePoint);

				}
				else
				{
					operand = "";
				}

				calcComponentEntry.addElement( operand );
				calcComponentEntry.addElement( singleCalcComponent.getFunctionName() );
							
				if(singleCalcComponent.getFunctionName().equalsIgnoreCase(CalcComponentTypes.BASELINE_FUNCTION))
				{
					if(calcPoint instanceof CalcStatusPoint)
						currentlyMappedBaselineID = ((CalcStatusPoint)calcPoint).getCalcBaselinePoint().getBaselineID();
					else
						currentlyMappedBaselineID = ((CalculatedPoint)calcPoint).getCalcBaselinePoint().getBaselineID();
				}	
			}
						
			getTableModel().addRow(calcComponentEntry);
			
		}
	}
	fireInputUpdate();
	repaint();
}
/**
 * Comment
 */
public void usePointCheckBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getUsePointCheckBox().isSelected() )
	{
		getDeviceComboBox().setEnabled(true);
		getPointComboBox().setEnabled(true);
	}
	else
	{
		getDeviceComboBox().setEnabled(false);
		getPointComboBox().setEnabled(false);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G62F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8BF4D45535D1A29F0D9A8BE804E89150C60D0A08ED1E65BDA926BE2C749557430AAD35F495DFF9ADFD25AD2EC5B52F3D791084A590C80020A0280081E9C8E2A2A1E0E572C1821144A4A491A2E1B2F31399184C8CB3F7B213E488EF6F733BF7A6F7A6895435F82C35B9776CF376BEFB6F334FB97BFCA6D26E0EA92B13D3F2A5A9F935447E9DCC1624182FA569C34B0EAFB9AECECECC17B47F5E8358A0
	ED49CA03B4814AACF3935333252A4FB3A1BF9F7271CFA626EF016F9C69D56742BA788851CEBC143D903DA1B9FCBBF5E7F9BB99A47F4775EBA1DD8730GF018EE6B447657E467B17CCA0667101CA2C919DDD8E7D776B606BB8FB4BE00C400C51D347E8AC8571016EFAAAFE3755A6E18A22D7C5107A9D948C74313C3FD9725D5D83AD3FAACF91B855B7AA2FBAD2309073225G28FC2A7409729924C76AAFF67D436468AEB0996DF607DD11CD168259ECD59C2E822743EAD74A4B6BB129B45ACC150EEE2743AE5B15EA
	D2DEEF344BB6A90634B1B1BB137084FDA03F4B0A386F7A305D4510DF8930564A7117FB91EF016F880098AB5775538F0FB0DDDA7B9F1312DE32272E8F216B8CAB55F111D555F50267750C2D24DFEEEFC37B67DEC8CCAF832885F082C4834CGF80778431DE777C1EA68D0C027EE075D60F4D6584C2EAF9CE659865F6565D042F08D324961B2CB926D73466C7DBA720483CAE79CB19D35C95C473C7F639B9E1096BFF1B2A5C347A60B638AD366DD2036D8F2C13589EDFBD24836250B017E9958F60B24EDFB79E8C76A
	EA1D36E73D38B7A51771493F28365DFA917BFA9F696B63701D4C7C0362CB88BED603273EBA1B788C6663311E0D7BC66BBAAECBED5AA4A93B28D6578EB1F38DA90A0D4A90E58B16A5398FF9A758E81B6B05AC9B88FE0986CFE599C5E444FCE6F7E2FAA4G752F18F3B8F6A5FBE27AB6009200DAG9B40D800D8BB7731DF1EBDB0CC9F2BB73A148A0F55E616DD92250DE95B8C296CD3DC469A3BDBB15ACDF23351E5B5F658E437446A5C5706B6688820FB00783E895A78DC364BAE23E2F558013B31DBD6E4171B56F9
	2CED8356F1492434495AADC3835DCE09737DDEDB8E24B623DBB96DB49B95D92DC14B1FEE43BE69B1B683C7E0G5F54AEBFECC7FB4D867D9782AC95F6A87F9A791D14DDB0DD141757BA9C97BD4EA654C4AAF1201D9FA7F347AA281F6D60B67E41F94479A09F6D60FCAEFD3DD143274AEAC2710D2EDEBE468AFBF1FE1E6E60BE73BF2D5CE72A326E17763FF8C257E79066B95858F1840F1DF24293652279393E56910E63E0186E509FF36ADC7173EBEEA3B5CC99356F018E6C2BCCA74C49GD5CEEE63782FF7D21F2D
	B03AE56E25F5AEF0B0592CDA1C4A1951370D59F3BB408E00A20037GBE8648927AECA1755A200DC800F80005GC95876A598BB1738ED4775BAC3744FDB0CDFB81D398CC35926084997E873B852361E5D306D75C370DC4D788CE7CE576AC1EDBE759C7217FBE0E12D121D325D8C9FEED67660B924E173C027C350BCC670CE18B7AAE1203A857E49F3B8360D0A6232F6F89439D1D6942B3DCBAD7FD752165B5361D4279C5ED662B954436105F9C354FF6E9C0EB91343EE17C9678B92D67EBC997332CD6E8621AB9D9E
	3BC271AF34633839A87732B96745F68C28FA0CB68F4EDFB47E18E1C45BBADD56EEF0A91C8225C517F0BE18A762A78485F098C745C3BA67929F5B5E8F78585E55F71FB427FDF70A5E5A94F3FFD54ACE979DEFA5AEF5ECD3191EBD2FAF7BFCB277B03CBB1560BEED46B8F2F0A4C4E3A0BA26ACA66457846DD8GA2DCFCCC9DB7E2B91F3758E02AF458EC54244256259D5958B772A50F8C53BDCE10A7057D0EF4EC8CAAEBE6362465D59DE8CB1BE6EEBD6FC6DB1E6A63F1E68C48B383E0010B5AB35BCD6DC16BED6C67
	7562A11F883054CD6B6DD23475FEAA6AB981BF96A0CE21751685541B646775CABCB04681EC9E560F9EED3DF317F93DA44067762875CAFA82649B6075A2819F2B29B7BB205E33225EDAAF488EF05CCB6BC5FAE9BD6AFB996D989369453EDD9EEB01C731628AE3D4E050B9BD3413E91CDB3CA11371186F6571ECEDAE76FB1A8F628100929F77519732F8BCBB616294E9493437C3466E35BEAA1B53277A684ED1BC1E1DE946B6E7C0599C9F772927BB34EB2A416DF6182CE4B9E77EE0B7E9FD18625C26B7A16D343A5C
	CA8DCCB2BE6EBB3DA6149757FB06E6710F1744C5286BC2E8F7EDEFE27A74DE443F1B1C2B113D8AF0B600E8GBAD6DE69AB46B908B8F781CD6AE5D7274355AD1B73AF98FB0C85C62FD2C0663B8283A92E466F3D3CEF0ACB2B481C4306D8D32FD30671566128F0785A8DC16CEAC373BB3C37197643541C23CC56790FCE120EDC49C9515BD70CDD3FBDE5F9AF35FFEE1F5A8F7E1969294B4F3D11C1EDF2381F5B64BC54096963B679D10F1ECD9674F11BCC6D3FB61BEC4E7B94D2335CE3B5DD2DB1C43F7ECD68309464
	5B564FF53075EBF5E86A27BA787B398E1D7EEB53A1FFG771B9E37CC27D10BEC3AF895DAD09D1E71F39D22C13EF9C207B62FD68749CFF54877F39D76DD3EB69DCE1C2940F1857BDF3184D4F89445E19FF1E7D0BD46DC66FA9C819995BF57A3B3D7CF0FF8BF5F3B67B56398F1495D0E9E795AA4E1FEBD40651883ED2C92F2D4D46849D1F8194B71EFD986342759FC2D66E076780310238A5A70835018607586EDCCB08B71GC9G59833CEF1B2F7C9D52EE87F8D881793F5EA53B5D037B758BAC635D4A7AF5555AF2
	6D1C63A6EBE87B97DAAE419A28EC0882B40EBC38A1F82F1DBF20467BC78686477EF48FBA072CA17583F48F3ADA601FA7F83386CF6D54104B6D6403324583FC5FE468C4BB9D3D12185E8620GC48144834C3F42570EE4B3463FE17725B530963A6CC6DBE3C020487707E634B744F6AC7FFDB34AC1E2BB095B61F2DF309D16DD8954BDADA2B0CF69EE99C477EE30BD0368E87FF559D096321EE3B7798A5CDE08FB4B4BCF7049124516291B34FEF4B4026EB94D107AG689AFFBA9957CEDCBB22A378DAFE7918F6AD1F
	3D3EB865AD1DB584E19E13EFD9043A066C97E7D33F6B4278A47F8658F381348178GE28192G965F407B28DA7EF428BEA22E576428845CF1087BAA03E3F4E1D3C37F5575C55DA03A603EC83AA1100E5AAF594B6D17FBA35F5B2D76DF1D8CB9036802E5283DD14F9F7E519C4A9F583243E41D564FE5B58BD91F3BCAD9DF9AD256D8DDD957B7EE551715CE25422E6309DDB1F619A5E43D75725549BAF990DD302CF923747A7633DCEE2F42D1DC864340D57A57A03AE099AC3AB274D6E4B29E91C206572ED20654ABC3
	4910B0CA2F4FAE7467697799D9B868DCE38AD833960C22F3CD922611FC2EA9EEC4DDF0AE490F647B09D4F286FA9E72BE00E8GFACEF60A6CBD62A0BFDB603666A0EE8964D7C5F2DBBCCB7AE85BCD0969E5B7F15C8B8407EBB72E1B94571AFBAD67CC1CFF28F25A4625EBEAA31559BBD4B91DABCFF406BEBF7932D37F7C2425D37F7C646B4E8D94CF76766A4A474B654E7067AB5D1DFA67AB5B09AE4FEDDECFF8C3B461A613B7EEB0D959EF14CD124EB94BA9E3374C6C10A34D6ADB436AD4AB0E4AA2610BFF5E9B42
	97B57B56431B783E3575A66EE79F99504F22A19F87B0C7784A19E46D18D89454474BA1AF0D063D7AE895CF57B61795E345FC40D7813481A8G93C673B5AD35EB4838235A97A66EF82DABF4BFA7F56977F30A85775CEE0747E512037BF20DA5CF14292BE70DA2F72359E91D0D04DEB13A3A64C13E3015505332D0746FDBC2798B6DD3DF3F36CF0B4538C97772BE1885761BB7BA308F96C3FEC5900E72BB6257727BD8700B8BD84B5346D05ABC166E07746898BDFE52E5AD3F9241AF462BB72E1B988F4B987DF9418F
	78E8D6B61D2589104E57EDBBB1206DCFC45BD539DADD16B05A1510E60E057D6C5841EDB3DB8FE879158AFE8395DAFE050C3E0A252DA1796D3F2265F7D00C472E6A6D7A63315164F258ECB8AAA53E16CDEE40F109BEE69B4B47E5F5830E4B8510DF86A00D636372E0A34A1987797D63864BF494F0CD8CEFE1A95DC3F849FA629FC7779049829FC170E3B5F83A37D8636377459320EC55B83E3F12B760992E4D586B70A890675E0CF57D9B789A5786F985A096E0464D5A3336473D383EF53A0C5DE006C627EC12265F
	023405B91CB69B723BG2AG4CG113770B68FD970CC144EE4F5CEF621454EE43DBBF13E919BCF37EADEEA2F231B025776653728F61322C26D45FEC766455CA8EA172D82AFD510BD1A86CF6DD5C46AE3BE8A4ADAEF65EB73A5320F58F59B64EF6338092DFC3DBEAEF0ABA44445C0FE0640BD5B0238C448AF3E0D4F1FFD5650735F8DD67D79EF0CD5FF1D3B559AFE1D1BE88DB5EFD1793613D8F4854816F6BB1739534B754D9338AFDB383E7B85AE055473C3FE0240BDCD6270B848A7885C76EA720E83720B856E46
	EA4EEF194055915A326844745AE80EFB0A34518AF90B406D70A1CE01FC14404DAAC55C94484F963807FB9037G72CB846EB051E3A564F36F6038B1C237FC01CB903A79846EAC093D22A09FABF0FFBDCB4EF4A1BFC760CCEDC4DF48AF9738475AF89B5237048F1178379E7267856E62A1229B64A385EE41A1CE9B7DADBE07CCE960737692402D7CD66878CD8B1963E1DE9D9FD8379072D5C1B8BACE7E054CDF2D63036F7C1FA1F84B7860BB7F138D7C4E5F87E50B470BF8174446C7A7C0ACB1016326911C9372D182
	D75E4F751CA2F0E77DE47CC0FE3140BDC96C3182725BA6F2FBDC4663EE368E0C56FC2F3F66BB58F0C05B487FC65B20361D3E014F65052033E5A2BF37ABBA0F6790E20D42E32D72724FDDD6F305312B164C70F4FDDACE769CC8BFE5A2DF1F5E5E08BC97C1FE85C0661D7C0C4437071F1144BFB0C92A0DB829FB5F312429B8E5671DEC0D3DD3BDA309733C1EC15BEC4E61EDC6C0F9421D080B4C511EC3222F7CA2C5128AA025655F8DA847398BE5D24B178514E3D9CB2A367C6D0073D63451DB0C1E6A767CEE2EDB52
	73F1126D49C3A16FF2243B28CE3BD8DA5F7C01346B66B7B2288F3610B83C8D4A1CF7F13F6C5B427DF742DD62ED192190522510CF0B017985209640829089908FB09FE0B9C05A5D604FGC76F66346995C80BF7E4F81F06F7FEF81F5786657E3B69DDDCAC2433819683A45DCD6F71D80CE11893421F62D9FBDA9C4A00ED6B554F8F2AFBD4275E523B424FBF350CC7A453BD38BCCD8FA76CF0A124AE380E66846150E73604289FFC373FD527CE38B7975C67B04DE23E95DC2E374F5DA471D8F3ECB25E27F7BA6CCA6B
	A47419FBC9EC318D3E73819690DCFCEBD5187679DEBA15C1AC504CG78006193A729F1A0EDB3A3996504204D5A6D69EE34764992EDFB87E9BBEFB24C8713F91C760B96D257688B2A7B9AD9A36B21DE1B28DB6245B85261B4DE4283E3DA6F24173F9F9C8B75A86EF6020B017C7449DCC7CBCBB89DD7876938F0F2200E4BA6876B38552B2FE391E9FBA55417E2395CBF6D8F6C0365E45D598F65E522CEC9B556D1A0903344629B5215782CC422325FDDBDD47FE4E8E41F21699F72F6B3B6D8764E7EF4E2F3D717558E
	6BAAE96F8C06F02C7C85EF20AC175DEA59A7BE7D7EBAC5E22484281FA8F4FA3E94FD566912BBE517CBB6135A3C5F161278E98554CD92756776845AE9910927726F01796E9EDE2721053CC143BBF73BE2F04946E3D61BCDB68B3E36966E8F66FBF85F5FBBE45F2F89E3BF659E4D7E07707B9569638840C78939DC3901320FA531DB8214A70ABA1B5A826D7A08225A352FAD3CDE935A38DEAB05DE4F360553ABE3883D526ECDCCEF89700B780AC09D6612F8B19B6AED3B176BF06321C09D96F8D4BF7D4D21217CB41C
	BC3550466D6C1B2EEFF3499E3795707EFB791A778E096322A11FA0F01EC69E0B4F9738DA9A5BC1FE0540F50ABDC05A37B94E554F636E26EF73797F69865451A2724F11BC3E892179AF9B069BFFA10F61548B6EB7D803065D46D06BC7A89A5817B762375123B89AE4B78CCEBCD1637EB650487DAD725B3C6F1FEA8CBD1E7F13541F8DF5678150FD7A177DFC1F3E8AF0EB2740DE8E209640B6C50C9B3F7EFCF2B309432320DE0C28BB7F500E007649361DBCE9324ADE274325E8687F1B446C7300F661945EEF8F8D3A
	E39DDEDFA4DCA55D9FC7CCC777BB4BC91C1CF6DF70394BEB841FFDDF70394B4E0DBCD65F86E5917771BB5DA7FA507ECBA1AFCD053DB5C0A1C0BD00EDAABF9B78688246FCE14FD61B9C4EDA39C736E9367BD43F33B9D777B67D8799BCA61E7554D4A9E174DF12C35D09C6CC2563E78ACB17B5FDC070D43FE3BE54EF8114AD87481E86B18D40910026E9DCBF77057DD4BF7C6901C1D10CA68BF9594415C357D151F848C4FD4D3EE9701955CD628E58B20D1ED9C5C0BAE19A150D4E01F7EC99C9AC958F34F32651B910
	449D5302576A6F9D4777FB06C16FA444985E0831DE035EFB8E41E352EE2CD32977AECA54F97BCD142FAA601D90AFDB6447F1CF9F5F84533D72B67605B8E1BB663113436CC175463D03F1F3756077AF2C3CA3E78311113CD9D71AE0FDB9E615EDE6C1FF5B6768BBA8FA2D31C336897C1F5EDB4B5B371BC99145DF16B66B494508D9FC6246B34253C16F0A687B155D25FA6F3216CC636FDB367B744AD70A725F17E84BB3273377AF53F9F93192C04F4AAD227C8F3A65FED17E8B5D72E8C8693A7C6100245A3E75FDC8
	6D32F727F809F28C96797C774DD6234D51757E7E1D057BF796156C6563F30AD77B5B2132B78F6A3E11C518BE1D1D4533F476188F48DBEA6C6BCC097FDB7159E09C0FBB6F38A209F961D7EB793C306D6149D255CBFFC96631C1F03BABD8FBC76FD74F8116DDC84A20BC5F10D45D3F5F32A3D877AAAB8CF57C81C2FE514720F8F159DEA60BA9C525FB24E7B8F44CF7E93C6C7248144F1B2BD5BE55AD43353D1E5DA83F3F272B762A4FD0F9E7168EDFB7F21C4368B6D40F4C9FFEF2D66B8F59135ECB61F3D4F03F78D8
	FFC4EA7A05365F1B256A903BCE15654130BA1402A09F97F19D1EDE2F52CD9932FFD43ABD9BD53A83436E8FDAFFD4CBE05F121F24B03E37642AFC97148CDF1E7AADAADD5419114973F0DB20BC2B72D4F9BA5FD379369F9AAEDFCABB6EFD157665869430C7F6A9D61351C6432C8A232B5EE1331AFAD9FC35124466788EAE61FE9E1F951377A896074B5A87CD8D266431A2BD1F45B71D2B6E67FB76FB892E6C0144742A87B83FAB45017B918F09551DD06EFF006F01AE8F39B756C2BC504DF9C0335E427713495A3D5D
	E43AEFG7CF2A1C774E1F43C4A40B7FE4CD6939B7177001A6B3A827A122340F03A292E3D213A3166CCF57B296A5626764A3A5A53DF1C62760E7FC035F7C17FFB21FD273890BA2A2C303090EDCD4FB93D7D7C1CB36FBB7C1C737E7E60F3CED332761C72257E60F3CE24D74B5778034FB937861CF39E76871FF3F2FA2A4F4FF6E9467421DD217579387860A787E1B4E03217527E6FEE1576D56FF0EDC1E350BF1131CD3F5F50FC6F59784D1F43FF760D36C1635759BD216F6E1E69513F3BFB2EC77F6E6EA7BD615F20
	3C54937AB7BE2F74E8FF63C3636F9F6FC11F28879F3481C483CCC7FF7C8E0F3DEDDE1CD470570A03636EEAF3174C23BD5EFE1DAF342E4D3E21FF57586E435FA9F4B8A02C6CC67E626D0D8F7BCBF1B8891262AE7850C6AEEB17C521D5995EC94E31ECF2A7C1537D15E9B35FDFAD845D564671B7D88FEF666330AA0E5FF13CCC6A7AA1BF81A09EE0F87190DF6B4F67E30E72EFAB656F5592C51BCDBE7E0EAEA90E5F496C4E8F3813513CA963E1B4255DD54A5F8C14BC48E97F5C99628D900914A91DD561F244BC4865
	58636138858217ABF0198F71BDE856DEF2978779360704DF8CFC84E93371825BED52DF05934FGE43D4B81D13B52CB316457CA4C5FEA7661DC6F87BEB1G33819682AC83109ECECC4F854887B8825084E08308G18G908710GB09FE04943DC0E1FDE599BC68EF629C51C54E534221BB2B99EC371193F1CDE3BBB948F707293E0523A0E8B32C9E9B2F6C9FC0EFB2B915F43842C996A930402666A06261ACAC3EDFBE3E5C3DDEDED0521215D5088CBC8F5D5F595BF37A98EC3FF3C2E2166CC5D2926G8E272A01AD
	1F2B5763F3F7A60B21FF2794FE3EADAAABE473748D87D43AB3430EA1C7667F5371B80249796FC5EAFB798D4311134E4B7EC16F0F07B7573E38A7747C33F84F50734F2BFB48EF27E05E40FDAA457DF68F3933686CF44BE21E7B431EEC012BF157BB5CD65C1AD2592D1B2F6E2C49FB15F4B3C6CCC74F223E5802735C4A07034F281A89DE0A8FBE237A2518C357C219B31E1F4999CB356DAF0C8FBCAFDD867924A09C257B4A27254BFEA4304EAE48178154EA7014EEEFE938B57BFC909F056547C29A9BD4B68372090F
	6849E6D7825EC132BA4BD9AABDBA78CC1852FD6451526532FA79ACBD9212EEEBGDDBD2BE7E629CF070E3DD15C0B5FA29E20D3ADFF23389757F62B9DDC53E8337611C314C628E86FE26FDD77059EAB66FDC30F156EFD7AEB35FB5F60355A37CFFF2D9E5827DD2BA9EE54BEDC573AE06EEE9575468A5CD782F7AB610938962BD931887CF8A297620F4B58A0475FCD647292DCAB435D2B41FD45F0537661F88715E19B44E9637611FD0C55E6E33F15A7F52CF6987E429E0F93992DCE23C66E6F8A5CD7222F9B495E11
	FE37E93EDF5D7C4D473BFF7DC65B2077B13ED27E96AC6AD1FEC733AFCF3C53913883E57C5EE6054051588DE9539E63387F90EF2D72846E2C0F3F1B33885C38D27EEE4EAFF0A7CB793B2FE8014BA23150F448A788DC33A26E1F844ED24C6F1F16885C05CDDC16158277D4B15723EA86475D24F05AD601FB2F0978BA647D82B73A09739BABF095A486DB8479E5823775B46212A03F76F10E9BB8AD6CA2F005045682F91F409510FAD1100F95387102B66EF13EFF3F0F58EA956453E6723D39A1EF28FBD76D3DF5494C
	40BB7763B3B56F40883FD65267CD00BFBF1337FBB7694BA8484708F6DF8DFB5F1BA1851E414F8BEAF7714C607B5EC85246B2402F946D4EA47A6EFCA2B1FD7F939C2790DCAB644DCF703303DD8A36E7F258BC5DF6B763F7D8617B06E8C1FB057A90646385EEA2710DC510DF7A84576D06CDA3F94710BBABD0377CD9413A7D1A34D182782333F83B0F92D9EC10774D62FA14B5855E7B7E96C3D0F667F1CF53C84EE0026FFD63200D9FCA5ABB0F2FBC0167BE8E621733205E9CA1CF6169C0F932CAD4F9A61E9E4AC661
	64D981EDCC8C106773A0F93EA672A4C13D4CA739BC55C17294162A72BC94D61E0CA164A903B64630EF4AFB8B9E47B2595E4D3B76736572C31260EDA9D7F9CB3EA14F178F72B34DF18615EE6EBF614CBB3EC21569A715AA6F01911CF97F29CA257B4CBBFC3ACD47D43A94E5C83A92FE8F5020107712C26E324FD5BEDF978F078F25CB28D1694EED3AF6DB7E7C04EA4B3F1ED4F9EF9A16EE6CFD50A98DDD53484E16AF57296D3F7C254AA76B744878A4B729FCBEEAD1793CBFC2BE27B00C11685F77FBA87391581844
	7FDEDA2DC8EBCE3B651D64AE506D12568BDEB5A4D0DB5273F1F96A2F272526C8E9B5D2BA8DDCEB24B40CBEEB2455A4B82D11D2A1F075B91979D398C80A14CE7FA096FC501BDAC5CAB5184D50CAD60044BF3C2F743F663F526CCE1552EB242DB861B44AAE92FF4BE67C4D977B43D94724B564AF6C61F3879041CD1255EE2557A657CA391D8D7847DE5C0AECB670485E2DC81B482F508F609FA8B9F05C6836A8980D9F2E15526C78A73E240D3458E634F79D20E17EE122C73261BC5105ECDCC05E9CA2AFF2A990DCF2
	3E3FFE77FBA716A624CAA94724B7ED8E2379185124B8DCB5529A4562125D96074D6CF3FEF614B04AA4EF27703EBF95583DA354C7E58B703CCA88FD51F7CFD456322CE3E0C21FCBBAC0EA627CDAA0EA18ABADC697B4A53BEA943C67F538F609329B5ED5767C7873ADEDA9D2B2D37212B4A0E57A1C50871D46EE2B2D97BE2A08FF3831E4ADF4BF6CD76CEE7C3383A0594EA049F811E820E07CF305939FFC7D8CE9C0A5D5249D5414C0FA20CBF6F49FB0F4F6DA6D32E0GF685765B85FB2CD390D4E7236F0ECDA7DE7C
	DB8778C115145ADDD8C87EAFA27F97637FC584D3C4B0C545E05C95D258FF4165BE2EB3F19E616D587D85472087151F7D7E3929BFBF32E2B9E8F7CCDACDFE228F1D102D56BA65696E10DD2363A7EEFFEB4212DB300F1C68AABE641BCD5973BFBCA0G5A59B8F826B15C7672CB716DD539A9D28A8CAC0BCCC752EAAFEE4B088F6261660D04633B813974C17E0958B787BB9A2257096E465C86D2B5077C3FF94446C32ABCC27B36507903D0BADF67F2FF244A1D49E4A6BFA694F26392F1E7C06EBA5422C45502BF3189
	347EF5AA7791D56EB4C98DCD532FBFC9AB87CB3ADAC88A965E01D24D6339ABFFC350A963FFA813734894BDB4CB54392E742E8B9C9FDC6E3782650609940FFC95A9033DC40255C47D5561F527541921A6ABBCFF3A8E07CB47E039D3A57DC9F6F0AEAFC46E3A5230DB55101FD310F3126BDDEECFB80F3A2EA4757F3F11F4D488EF17783B2C3DD7346F32D65D1497721DDE7CDCFA87B0E72E7AAE0B5EE52EDBC74EB2G1F778C3F97BD17059704216EB49B3B0DB61BE234DDB4584DF83FD9512B486A9B021CF5485F4D
	6B30C2DA7676BA147B0206667FGD0CB8788FB777094149DGG40DDGGD0CB818294G94G88G88G62F854ACFB777094149DGG40DDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4E9DGGGG
**end of data**/
}
}

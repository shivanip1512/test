package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

public class PointForeignDataEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener {
	private String currentPointType = com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.ANALOG_POINT);
	private javax.swing.JLabel ivjDirectionTypeLabel = null;
	private javax.swing.JPanel ivjForeignDataRoutingPanel = null;
	private javax.swing.JButton ivjAddButton = null;
	private javax.swing.JButton ivjRemoveButton = null;
	private javax.swing.JButton ivjUpdateButton = null;
	private javax.swing.JLabel ivjInterfaceLabel = null;
	private javax.swing.JLabel ivjJLabelOption1 = null;
	private javax.swing.JLabel ivjJLabelOption2 = null;
	private javax.swing.JLabel ivjJLabelOption3 = null;
	private javax.swing.JComboBox ivjJComboBoxOption1 = null;
	private javax.swing.JComboBox ivjJComboBoxOption2 = null;
	private javax.swing.JComboBox ivjJComboBoxOption3 = null;
	private javax.swing.JComboBox ivjJComboBoxOption4 = null;
	private javax.swing.JComboBox ivjJComboBoxOption5 = null;
	private javax.swing.JLabel ivjJLabelOption4 = null;
	private javax.swing.JLabel ivjJLabelOption5 = null;
	private javax.swing.JPanel ivjJPanelDataParameters = null;
	private javax.swing.JComboBox ivjJComboBoxDirection = null;
	private javax.swing.JComboBox ivjJComboBoxInterface = null;
	public static final int MAX_DATA_PROPERTIES = 5;
	//dataOptions[][] 
	// 	--[x][0] is the OptionJLabel
	//		--[x][1] is the OptionJComboBox
	private javax.swing.JComponent[][] dataOptions = null;
	private javax.swing.JScrollPane ivjJScrollPaneJtableFDR = null;
	private javax.swing.JTable ivjJTableFDR = null;
	private javax.swing.JPanel ivjJPanelTableHolder = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointForeignDataEditorPanel() {
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
	if (e.getSource() == getJComboBoxDirection()) 
		connEtoC3(e);
	if (e.getSource() == getAddButton()) 
		connEtoC5(e);
	if (e.getSource() == getUpdateButton()) 
		connEtoC6(e);
	if (e.getSource() == getRemoveButton()) 
		connEtoC7(e);
	if (e.getSource() == getJComboBoxInterface()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void addButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	java.util.Vector fdrTranslationVector = new java.util.Vector(4);

	fdrTranslationVector.addElement( getJComboBoxDirection().getSelectedItem() );
	fdrTranslationVector.addElement( getJComboBoxInterface().getSelectedItem() );

	StringBuffer translation = new StringBuffer();
	for( int i = 0; i < MAX_DATA_PROPERTIES; i++ )
	{
		if( getDataOptions()[i][0].isEnabled() )
		{
			//append the labels text
			translation.append( ((javax.swing.JLabel)getDataOptions()[i][0]).getText() );

			//append the values selected by the user
			if(  ((javax.swing.JComboBox)getDataOptions()[i][1]).getSelectedItem() == null )
				translation.append( ";" );
			else
				translation.append( ((javax.swing.JComboBox)getDataOptions()[i][1]).getSelectedItem().toString() + ";" );
		}
		else
			break;
	}

	//For all interfaces, add the PointType to the end of the translation string
	translation.append( "POINTTYPE:" + getCurrentPointType() + ";" );


	if( translation != null )
	{
		fdrTranslationVector.addElement(translation);

		fdrTranslationVector.addElement( 
			new Boolean( ((com.cannontech.database.data.fdr.FDRInterface)getJComboBoxInterface().getSelectedItem()).getFdrInterface().hasDestination() ) );
		
		getTableModel().addRow(fdrTranslationVector);
		fireInputUpdate();
		repaint();
	}
}
/**
 * connEtoC1:  (JComboBoxInterface.action.actionPerformed(java.awt.event.ActionEvent) --> PointForeignDataEditorPanel.jComboBoxInterface_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxInterface_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JTableFDR.mouse.mouseClicked(java.awt.event.MouseEvent) --> PointForeignDataEditorPanel.jTableFDR_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableFDR_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DirectionTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointDataOptionsEditorPanel.directionTypeComboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.directionTypeComboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (AddComponentButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointDataOptionsEditorPanel.addComponentButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (EditComponentButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointDataOptionsEditorPanel.editComponentButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.updateButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (RemoveComponentButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointDataOptionsEditorPanel.removeComponentButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.removeButton_ActionPerformed(arg1);
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
public void directionTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:34:07 AM)
 */
private com.cannontech.database.data.fdr.FDRInterface findInterface( String iName ) 
{
	for( int j = 0; j < getJComboBoxInterface().getItemCount(); j++ )
		if( getJComboBoxInterface().getItemAt(j).toString().equalsIgnoreCase( iName ) )
			return (com.cannontech.database.data.fdr.FDRInterface)getJComboBoxInterface().getItemAt(j);

	return null;
}
/**
 * Return the AddComponentButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddButton() {
	if (ivjAddButton == null) {
		try {
			ivjAddButton = new javax.swing.JButton();
			ivjAddButton.setName("AddButton");
			ivjAddButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAddButton.setText("Add");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddButton;
}
/**
 * Insert the method's description here.
 * Creation date: (5/29/2001 11:55:06 AM)
 * @return com.cannontech.database.data.fdr.FDRInterface
 */
private com.cannontech.database.data.fdr.FDRInterface getCurrentFDRInterface() 
{
	if( getJComboBoxInterface().getSelectedItem() == null )
		return null;
	else
		return (com.cannontech.database.data.fdr.FDRInterface)getJComboBoxInterface().getSelectedItem();
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 3:31:34 PM)
 * @return java.lang.String
 */
public java.lang.String getCurrentPointType() {
	return currentPointType;
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/2001 10:52:19 AM)
 * @return javax.swing.JComponent[][]
 */
private javax.swing.JComponent[][] getDataOptions() 
{
	if( dataOptions == null )
	{
		setDataOptions( new javax.swing.JComponent[MAX_DATA_PROPERTIES][2] );
		getDataOptions()[0][0] = getJLabelOption1();
		getDataOptions()[0][1] = getJComboBoxOption1();
		getDataOptions()[1][0] = getJLabelOption2();
		getDataOptions()[1][1] = getJComboBoxOption2();
		getDataOptions()[2][0] = getJLabelOption3();
		getDataOptions()[2][1] = getJComboBoxOption3();
		getDataOptions()[3][0] = getJLabelOption4();
		getDataOptions()[3][1] = getJComboBoxOption4();
		getDataOptions()[4][0] = getJLabelOption5();
		getDataOptions()[4][1] = getJComboBoxOption5();
	}
	
	return dataOptions;
}
/**
 * Insert the method's description here.
 * Creation date: (12/5/2001 9:34:07 AM)
 * @return java.lang.String
 */
private String getDestinationField( String trans ) 
{
	int i = 0;
	java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( trans, ";" );
	while( tokenizer.hasMoreTokens() )
	{
		String token = tokenizer.nextElement().toString();
		if( token.toLowerCase().indexOf("estination") >= 0 )
			return token.substring( token.indexOf(":")+1, token.length() );
	}

	return "(not found)";
}
/**
 * Insert the method's description here.
 * Creation date: (7/16/2001 12:10:34 PM)
 * @return java.lang.String
 */
/* Destination String MUST ALWAYS BE the second to last translation value */
private String getDestinationString( String trans ) 
{
	java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(trans, ":" );
	String value = null;
	int tokCnt = tokenizer.countTokens() - 1;
	
	//get to the second to last token
	for( int i = 0; i < tokCnt; i++ )
		value = tokenizer.nextToken();

	return value.substring( 0, value.indexOf(";") );
}
/**
 * Return the DirectionTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDirectionTypeLabel() {
	if (ivjDirectionTypeLabel == null) {
		try {
			ivjDirectionTypeLabel = new javax.swing.JLabel();
			ivjDirectionTypeLabel.setName("DirectionTypeLabel");
			ivjDirectionTypeLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDirectionTypeLabel.setText("Direction:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDirectionTypeLabel;
}
/**
 * Return the ForeignDataRoutingPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getForeignDataRoutingPanel() {
	if (ivjForeignDataRoutingPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Foreign Data Routing");
			ivjForeignDataRoutingPanel = new javax.swing.JPanel();
			ivjForeignDataRoutingPanel.setName("ForeignDataRoutingPanel");
			ivjForeignDataRoutingPanel.setBorder(ivjLocalBorder);
			ivjForeignDataRoutingPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsDirectionTypeLabel = new java.awt.GridBagConstraints();
			constraintsDirectionTypeLabel.gridx = 1; constraintsDirectionTypeLabel.gridy = 3;
			constraintsDirectionTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDirectionTypeLabel.ipadx = 8;
			constraintsDirectionTypeLabel.ipady = -2;
			constraintsDirectionTypeLabel.insets = new java.awt.Insets(5, 10, 7, 4);
			getForeignDataRoutingPanel().add(getDirectionTypeLabel(), constraintsDirectionTypeLabel);

			java.awt.GridBagConstraints constraintsJComboBoxDirection = new java.awt.GridBagConstraints();
			constraintsJComboBoxDirection.gridx = 2; constraintsJComboBoxDirection.gridy = 3;
			constraintsJComboBoxDirection.gridwidth = 2;
			constraintsJComboBoxDirection.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxDirection.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxDirection.weightx = 1.0;
			constraintsJComboBoxDirection.ipadx = 16;
			constraintsJComboBoxDirection.ipady = -7;
			constraintsJComboBoxDirection.insets = new java.awt.Insets(2, 4, 3, 15);
			getForeignDataRoutingPanel().add(getJComboBoxDirection(), constraintsJComboBoxDirection);

			java.awt.GridBagConstraints constraintsInterfaceLabel = new java.awt.GridBagConstraints();
			constraintsInterfaceLabel.gridx = 1; constraintsInterfaceLabel.gridy = 2;
			constraintsInterfaceLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsInterfaceLabel.ipadx = -72;
			constraintsInterfaceLabel.ipady = -6;
			constraintsInterfaceLabel.insets = new java.awt.Insets(5, 10, 6, 3);
			getForeignDataRoutingPanel().add(getInterfaceLabel(), constraintsInterfaceLabel);

			java.awt.GridBagConstraints constraintsAddButton = new java.awt.GridBagConstraints();
			constraintsAddButton.gridx = 1; constraintsAddButton.gridy = 5;
			constraintsAddButton.gridwidth = 2;
			constraintsAddButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAddButton.ipadx = 48;
			constraintsAddButton.insets = new java.awt.Insets(2, 23, 39, 8);
			getForeignDataRoutingPanel().add(getAddButton(), constraintsAddButton);

			java.awt.GridBagConstraints constraintsUpdateButton = new java.awt.GridBagConstraints();
			constraintsUpdateButton.gridx = 3; constraintsUpdateButton.gridy = 5;
			constraintsUpdateButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUpdateButton.ipadx = -47;
			constraintsUpdateButton.ipady = -2;
			constraintsUpdateButton.insets = new java.awt.Insets(2, 9, 39, 1);
			getForeignDataRoutingPanel().add(getUpdateButton(), constraintsUpdateButton);

			java.awt.GridBagConstraints constraintsRemoveButton = new java.awt.GridBagConstraints();
			constraintsRemoveButton.gridx = 4; constraintsRemoveButton.gridy = 5;
			constraintsRemoveButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRemoveButton.ipadx = 30;
			constraintsRemoveButton.insets = new java.awt.Insets(2, 10, 39, 27);
			getForeignDataRoutingPanel().add(getRemoveButton(), constraintsRemoveButton);

			java.awt.GridBagConstraints constraintsJComboBoxInterface = new java.awt.GridBagConstraints();
			constraintsJComboBoxInterface.gridx = 2; constraintsJComboBoxInterface.gridy = 2;
			constraintsJComboBoxInterface.gridwidth = 2;
			constraintsJComboBoxInterface.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxInterface.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxInterface.weightx = 1.0;
			constraintsJComboBoxInterface.ipadx = 16;
			constraintsJComboBoxInterface.ipady = -7;
			constraintsJComboBoxInterface.insets = new java.awt.Insets(2, 4, 2, 15);
			getForeignDataRoutingPanel().add(getJComboBoxInterface(), constraintsJComboBoxInterface);

			java.awt.GridBagConstraints constraintsJPanelDataParameters = new java.awt.GridBagConstraints();
			constraintsJPanelDataParameters.gridx = 1; constraintsJPanelDataParameters.gridy = 4;
			constraintsJPanelDataParameters.gridwidth = 4;
			constraintsJPanelDataParameters.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelDataParameters.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelDataParameters.weightx = 1.0;
			constraintsJPanelDataParameters.weighty = 1.0;
			constraintsJPanelDataParameters.ipadx = -10;
			constraintsJPanelDataParameters.ipady = -5;
			constraintsJPanelDataParameters.insets = new java.awt.Insets(3, 10, 1, 10);
			getForeignDataRoutingPanel().add(getJPanelDataParameters(), constraintsJPanelDataParameters);

			java.awt.GridBagConstraints constraintsJPanelTableHolder = new java.awt.GridBagConstraints();
			constraintsJPanelTableHolder.gridx = 1; constraintsJPanelTableHolder.gridy = 1;
			constraintsJPanelTableHolder.gridwidth = 4;
			constraintsJPanelTableHolder.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelTableHolder.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelTableHolder.weightx = 1.0;
			constraintsJPanelTableHolder.weighty = 1.0;
			constraintsJPanelTableHolder.ipadx = -483;
			constraintsJPanelTableHolder.ipady = -386;
			constraintsJPanelTableHolder.insets = new java.awt.Insets(5, 10, 2, 10);
			getForeignDataRoutingPanel().add(getJPanelTableHolder(), constraintsJPanelTableHolder);
			// user code begin {1}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjForeignDataRoutingPanel;
}
/**
 * Return the DestinationLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getInterfaceLabel() {
	if (ivjInterfaceLabel == null) {
		try {
			ivjInterfaceLabel = new javax.swing.JLabel();
			ivjInterfaceLabel.setName("InterfaceLabel");
			ivjInterfaceLabel.setText("Interface:");
			ivjInterfaceLabel.setMaximumSize(new java.awt.Dimension(133, 20));
			ivjInterfaceLabel.setPreferredSize(new java.awt.Dimension(133, 20));
			ivjInterfaceLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjInterfaceLabel.setMinimumSize(new java.awt.Dimension(133, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInterfaceLabel;
}
/**
 * Return the DirectionTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxDirection() {
	if (ivjJComboBoxDirection == null) {
		try {
			ivjJComboBoxDirection = new javax.swing.JComboBox();
			ivjJComboBoxDirection.setName("JComboBoxDirection");
			ivjJComboBoxDirection.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxDirection.setMaximumSize(new java.awt.Dimension(130, 28));
			ivjJComboBoxDirection.setMinimumSize(new java.awt.Dimension(130, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxDirection;
}
/**
 * Return the CatagoryComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxInterface() {
	if (ivjJComboBoxInterface == null) {
		try {
			ivjJComboBoxInterface = new javax.swing.JComboBox();
			ivjJComboBoxInterface.setName("JComboBoxInterface");
			ivjJComboBoxInterface.setOpaque(true);
			ivjJComboBoxInterface.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxInterface.setMinimumSize(new java.awt.Dimension(130, 28));
			ivjJComboBoxInterface.setMaximumSize(new java.awt.Dimension(130, 28));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxInterface;
}
/**
 * Return the JComboBoxOption1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOption1() {
	if (ivjJComboBoxOption1 == null) {
		try {
			ivjJComboBoxOption1 = new javax.swing.JComboBox();
			ivjJComboBoxOption1.setName("JComboBoxOption1");
			ivjJComboBoxOption1.setOpaque(true);
			ivjJComboBoxOption1.setMaximumSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption1.setPreferredSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption1.setMinimumSize(new java.awt.Dimension(130, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOption1;
}
/**
 * Return the JComboBoxOption2 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOption2() {
	if (ivjJComboBoxOption2 == null) {
		try {
			ivjJComboBoxOption2 = new javax.swing.JComboBox();
			ivjJComboBoxOption2.setName("JComboBoxOption2");
			ivjJComboBoxOption2.setOpaque(true);
			ivjJComboBoxOption2.setMaximumSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption2.setPreferredSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption2.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption2.setMinimumSize(new java.awt.Dimension(130, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOption2;
}
/**
 * Return the JComboBoxOption3 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOption3() {
	if (ivjJComboBoxOption3 == null) {
		try {
			ivjJComboBoxOption3 = new javax.swing.JComboBox();
			ivjJComboBoxOption3.setName("JComboBoxOption3");
			ivjJComboBoxOption3.setOpaque(true);
			ivjJComboBoxOption3.setMaximumSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption3.setPreferredSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption3.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption3.setMinimumSize(new java.awt.Dimension(130, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOption3;
}
/**
 * Return the JComboBoxOption4 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOption4() {
	if (ivjJComboBoxOption4 == null) {
		try {
			ivjJComboBoxOption4 = new javax.swing.JComboBox();
			ivjJComboBoxOption4.setName("JComboBoxOption4");
			ivjJComboBoxOption4.setOpaque(true);
			ivjJComboBoxOption4.setMaximumSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption4.setPreferredSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption4.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption4.setMinimumSize(new java.awt.Dimension(130, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOption4;
}
/**
 * Return the JComboBoxOption5 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOption5() {
	if (ivjJComboBoxOption5 == null) {
		try {
			ivjJComboBoxOption5 = new javax.swing.JComboBox();
			ivjJComboBoxOption5.setName("JComboBoxOption5");
			ivjJComboBoxOption5.setOpaque(true);
			ivjJComboBoxOption5.setMaximumSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption5.setPreferredSize(new java.awt.Dimension(130, 27));
			ivjJComboBoxOption5.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption5.setMinimumSize(new java.awt.Dimension(130, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOption5;
}
/**
 * Return the TranslationLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOption1() {
	if (ivjJLabelOption1 == null) {
		try {
			ivjJLabelOption1 = new javax.swing.JLabel();
			ivjJLabelOption1.setName("JLabelOption1");
			ivjJLabelOption1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOption1.setText("Option1:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOption1;
}
/**
 * Return the JLabelOption2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOption2() {
	if (ivjJLabelOption2 == null) {
		try {
			ivjJLabelOption2 = new javax.swing.JLabel();
			ivjJLabelOption2.setName("JLabelOption2");
			ivjJLabelOption2.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOption2.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjJLabelOption2.setText("Option2:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOption2;
}
/**
 * Return the JLabelOption3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOption3() {
	if (ivjJLabelOption3 == null) {
		try {
			ivjJLabelOption3 = new javax.swing.JLabel();
			ivjJLabelOption3.setName("JLabelOption3");
			ivjJLabelOption3.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOption3.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjJLabelOption3.setText("Option3:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOption3;
}
/**
 * Return the JLabelOption4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOption4() {
	if (ivjJLabelOption4 == null) {
		try {
			ivjJLabelOption4 = new javax.swing.JLabel();
			ivjJLabelOption4.setName("JLabelOption4");
			ivjJLabelOption4.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOption4.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjJLabelOption4.setText("Option4:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOption4;
}
/**
 * Return the JLabelOption5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOption5() {
	if (ivjJLabelOption5 == null) {
		try {
			ivjJLabelOption5 = new javax.swing.JLabel();
			ivjJLabelOption5.setName("JLabelOption5");
			ivjJLabelOption5.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOption5.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjJLabelOption5.setText("Option5:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOption5;
}
/**
 * Return the JPanelDataParameters property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelDataParameters() {
	if (ivjJPanelDataParameters == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Data Parameters");
			ivjJPanelDataParameters = new javax.swing.JPanel();
			ivjJPanelDataParameters.setName("JPanelDataParameters");
			ivjJPanelDataParameters.setBorder(ivjLocalBorder1);
			ivjJPanelDataParameters.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelOption2 = new java.awt.GridBagConstraints();
			constraintsJLabelOption2.gridx = 1; constraintsJLabelOption2.gridy = 2;
			constraintsJLabelOption2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelOption2.ipadx = 78;
			constraintsJLabelOption2.ipady = -2;
			constraintsJLabelOption2.insets = new java.awt.Insets(7, 21, 3, 0);
			getJPanelDataParameters().add(getJLabelOption2(), constraintsJLabelOption2);

			java.awt.GridBagConstraints constraintsJLabelOption3 = new java.awt.GridBagConstraints();
			constraintsJLabelOption3.gridx = 1; constraintsJLabelOption3.gridy = 3;
			constraintsJLabelOption3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelOption3.ipadx = 78;
			constraintsJLabelOption3.ipady = -2;
			constraintsJLabelOption3.insets = new java.awt.Insets(6, 21, 3, 0);
			getJPanelDataParameters().add(getJLabelOption3(), constraintsJLabelOption3);

			java.awt.GridBagConstraints constraintsJLabelOption4 = new java.awt.GridBagConstraints();
			constraintsJLabelOption4.gridx = 1; constraintsJLabelOption4.gridy = 4;
			constraintsJLabelOption4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelOption4.ipadx = 78;
			constraintsJLabelOption4.ipady = -2;
			constraintsJLabelOption4.insets = new java.awt.Insets(5, 21, 3, 0);
			getJPanelDataParameters().add(getJLabelOption4(), constraintsJLabelOption4);

			java.awt.GridBagConstraints constraintsJLabelOption5 = new java.awt.GridBagConstraints();
			constraintsJLabelOption5.gridx = 1; constraintsJLabelOption5.gridy = 5;
			constraintsJLabelOption5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelOption5.ipadx = 78;
			constraintsJLabelOption5.ipady = -2;
			constraintsJLabelOption5.insets = new java.awt.Insets(5, 21, 29, 0);
			getJPanelDataParameters().add(getJLabelOption5(), constraintsJLabelOption5);

			java.awt.GridBagConstraints constraintsJLabelOption1 = new java.awt.GridBagConstraints();
			constraintsJLabelOption1.gridx = 1; constraintsJLabelOption1.gridy = 1;
			constraintsJLabelOption1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelOption1.ipadx = 78;
			constraintsJLabelOption1.ipady = -2;
			constraintsJLabelOption1.insets = new java.awt.Insets(5, 21, 6, 0);
			getJPanelDataParameters().add(getJLabelOption1(), constraintsJLabelOption1);

			java.awt.GridBagConstraints constraintsJComboBoxOption1 = new java.awt.GridBagConstraints();
			constraintsJComboBoxOption1.gridx = 2; constraintsJComboBoxOption1.gridy = 1;
			constraintsJComboBoxOption1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxOption1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxOption1.weightx = 1.0;
			constraintsJComboBoxOption1.ipadx = 72;
			constraintsJComboBoxOption1.ipady = -9;
			constraintsJComboBoxOption1.insets = new java.awt.Insets(5, 0, 3, 26);
			getJPanelDataParameters().add(getJComboBoxOption1(), constraintsJComboBoxOption1);

			java.awt.GridBagConstraints constraintsJComboBoxOption2 = new java.awt.GridBagConstraints();
			constraintsJComboBoxOption2.gridx = 2; constraintsJComboBoxOption2.gridy = 2;
			constraintsJComboBoxOption2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxOption2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxOption2.weightx = 1.0;
			constraintsJComboBoxOption2.ipadx = 72;
			constraintsJComboBoxOption2.ipady = -9;
			constraintsJComboBoxOption2.insets = new java.awt.Insets(4, 0, 2, 26);
			getJPanelDataParameters().add(getJComboBoxOption2(), constraintsJComboBoxOption2);

			java.awt.GridBagConstraints constraintsJComboBoxOption3 = new java.awt.GridBagConstraints();
			constraintsJComboBoxOption3.gridx = 2; constraintsJComboBoxOption3.gridy = 3;
			constraintsJComboBoxOption3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxOption3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxOption3.weightx = 1.0;
			constraintsJComboBoxOption3.ipadx = 72;
			constraintsJComboBoxOption3.ipady = -9;
			constraintsJComboBoxOption3.insets = new java.awt.Insets(3, 0, 2, 26);
			getJPanelDataParameters().add(getJComboBoxOption3(), constraintsJComboBoxOption3);

			java.awt.GridBagConstraints constraintsJComboBoxOption4 = new java.awt.GridBagConstraints();
			constraintsJComboBoxOption4.gridx = 2; constraintsJComboBoxOption4.gridy = 4;
			constraintsJComboBoxOption4.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxOption4.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxOption4.weightx = 1.0;
			constraintsJComboBoxOption4.ipadx = 72;
			constraintsJComboBoxOption4.ipady = -9;
			constraintsJComboBoxOption4.insets = new java.awt.Insets(2, 0, 2, 26);
			getJPanelDataParameters().add(getJComboBoxOption4(), constraintsJComboBoxOption4);

			java.awt.GridBagConstraints constraintsJComboBoxOption5 = new java.awt.GridBagConstraints();
			constraintsJComboBoxOption5.gridx = 2; constraintsJComboBoxOption5.gridy = 5;
			constraintsJComboBoxOption5.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxOption5.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxOption5.weightx = 1.0;
			constraintsJComboBoxOption5.ipadx = 72;
			constraintsJComboBoxOption5.ipady = -9;
			constraintsJComboBoxOption5.insets = new java.awt.Insets(2, 0, 28, 26);
			getJPanelDataParameters().add(getJComboBoxOption5(), constraintsJComboBoxOption5);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDataParameters;
}
/**
 * Return the JPanelTableHolder property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTableHolder() {
	if (ivjJPanelTableHolder == null) {
		try {
			ivjJPanelTableHolder = new javax.swing.JPanel();
			ivjJPanelTableHolder.setName("JPanelTableHolder");
			ivjJPanelTableHolder.setLayout(new java.awt.GridBagLayout());
			ivjJPanelTableHolder.setMaximumSize(new java.awt.Dimension(856, 493));

			java.awt.GridBagConstraints constraintsJScrollPaneJtableFDR = new java.awt.GridBagConstraints();
			constraintsJScrollPaneJtableFDR.gridx = 0; constraintsJScrollPaneJtableFDR.gridy = 0;
			constraintsJScrollPaneJtableFDR.gridwidth = 2;
			constraintsJScrollPaneJtableFDR.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneJtableFDR.weightx = 1.0;
			constraintsJScrollPaneJtableFDR.weighty = 1.0;
			constraintsJScrollPaneJtableFDR.ipadx = 382;
			constraintsJScrollPaneJtableFDR.ipady = 70;
			constraintsJScrollPaneJtableFDR.insets = new java.awt.Insets(2, 2, 3, 4);
			getJPanelTableHolder().add(getJScrollPaneJtableFDR(), constraintsJScrollPaneJtableFDR);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTableHolder;
}
/**
 * Return the JScrollPaneJtableFDR property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneJtableFDR() {
	if (ivjJScrollPaneJtableFDR == null) {
		try {
			ivjJScrollPaneJtableFDR = new javax.swing.JScrollPane();
			ivjJScrollPaneJtableFDR.setName("JScrollPaneJtableFDR");
			ivjJScrollPaneJtableFDR.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneJtableFDR.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneJtableFDR.setMinimumSize(new java.awt.Dimension(468, 418));
			getJScrollPaneJtableFDR().setViewportView(getJTableFDR());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneJtableFDR;
}
/**
 * Return the JTableFDR property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableFDR() {
	if (ivjJTableFDR == null) {
		try {
			ivjJTableFDR = new javax.swing.JTable();
			ivjJTableFDR.setName("JTableFDR");
			getJScrollPaneJtableFDR().setColumnHeaderView(ivjJTableFDR.getTableHeader());
			ivjJTableFDR.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableFDR;
}
/**
 * Return the RemoveComponentButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getRemoveButton() {
	if (ivjRemoveButton == null) {
		try {
			ivjRemoveButton = new javax.swing.JButton();
			ivjRemoveButton.setName("RemoveButton");
			ivjRemoveButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRemoveButton.setText("Remove");
			ivjRemoveButton.setContentAreaFilled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveButton;
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/00 3:02:15 PM)
 * @return com.cannontech.common.gui.util.CalcComponentTableModel
 */
private FDRTranslationTableModel getTableModel() 
{
	return ((FDRTranslationTableModel)getJTableFDR().getModel());
}
/**
 * Return the EditComponentButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getUpdateButton() {
	if (ivjUpdateButton == null) {
		try {
			ivjUpdateButton = new javax.swing.JButton();
			ivjUpdateButton.setName("UpdateButton");
			ivjUpdateButton.setText("Update");
			ivjUpdateButton.setMaximumSize(new java.awt.Dimension(150, 31));
			ivjUpdateButton.setPreferredSize(new java.awt.Dimension(130, 31));
			ivjUpdateButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUpdateButton.setContentAreaFilled(true);
			ivjUpdateButton.setMinimumSize(new java.awt.Dimension(130, 31));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUpdateButton;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase)val;
	Integer pointID = point.getPoint().getPointID();
	com.cannontech.database.db.point.fdr.FDRTranslation newFDRTranslation = null;
	java.util.Vector fdrTranslationVector = null;

	if( getJTableFDR().getRowCount() > 0 )
	{
		fdrTranslationVector = new java.util.Vector();
		
		for(int i=0;i<getJTableFDR().getRowCount();i++)
		{
			newFDRTranslation = new com.cannontech.database.db.point.fdr.FDRTranslation(pointID);
			newFDRTranslation.setDirectionType(getJTableFDR().getModel().getValueAt(i,0).toString());
			newFDRTranslation.setTranslation(getJTableFDR().getModel().getValueAt(i,2).toString());

			com.cannontech.database.data.fdr.FDRInterface iface = 
				(com.cannontech.database.data.fdr.FDRInterface)getJTableFDR().getModel().getValueAt(i,1);

			newFDRTranslation.setInterfaceType( iface.toString().toUpperCase() );

			if( iface.getFdrInterface().hasDestination() )
				newFDRTranslation.setDestination( getDestinationField(newFDRTranslation.getTranslation()) );
			else
				newFDRTranslation.setDestination( newFDRTranslation.getInterfaceType() );

			fdrTranslationVector.addElement(newFDRTranslation);
		}
	}
	point.setPointFDRVector(fdrTranslationVector);

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
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxDirection().addActionListener(this);
	getAddButton().addActionListener(this);
	getUpdateButton().addActionListener(this);
	getRemoveButton().addActionListener(this);
	getJComboBoxInterface().addActionListener(this);
	getJTableFDR().addMouseListener(this);
}
/**
 * Insert the method's description here.
 * Creation date: (5/22/2001 5:20:05 PM)
 */
private void initFDRInterfaces() 
{
	com.cannontech.database.data.fdr.FDRInterface[] interfaces = com.cannontech.database.db.point.fdr.FDRInterface.getALLFDRInterfaces();

	for( int i = 0; i < interfaces.length; i++ )
	{			
		getJComboBoxInterface().addItem( interfaces[i] );
	}

}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointDataOptions");
		setLayout(new java.awt.BorderLayout());
		setSize(393, 422);
		add(getForeignDataRoutingPanel(), "Center");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initFDRInterfaces();

	if( getJComboBoxInterface().getItemCount() > 0 )
		getJComboBoxInterface().setSelectedIndex(0);

	getJTableFDR().setModel(new FDRTranslationTableModel());
	((FDRTranslationTableModel)getJTableFDR().getModel()).makeTable();

	getJTableFDR().getColumnModel().getColumn(0).setWidth(55);
	getJTableFDR().getColumnModel().getColumn(0).setPreferredWidth(55);
	getJTableFDR().getColumnModel().getColumn(1).setWidth(55);
	getJTableFDR().getColumnModel().getColumn(1).setPreferredWidth(55);
	getJTableFDR().getColumnModel().getColumn(2).setWidth(222);
	getJTableFDR().getColumnModel().getColumn(2).setPreferredWidth(222);
	getJTableFDR().revalidate();
	getJTableFDR().repaint();
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
 * Comment
 */
public void jComboBoxInterface_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJComboBoxInterface().getSelectedItem() != null )
	{
		com.cannontech.database.data.fdr.FDRInterface fdrInterface = (com.cannontech.database.data.fdr.FDRInterface)getJComboBoxInterface().getSelectedItem();
		
		//set the possible directions of data flow
		getJComboBoxDirection().removeAllItems();
		
		//add all the possible choices for the Directions ComboBox input
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( fdrInterface.getFdrInterface().getPossibleDirections(), "," );
		java.util.ArrayList tempList = new java.util.ArrayList(10);
		
		while( tokenizer.hasMoreElements() )
			tempList.add( tokenizer.nextToken() );	

		java.util.Collections.sort(tempList); //sort the list alphabetically
		for( int i = 0; i < tempList.size(); i++ )
			getJComboBoxDirection().addItem( tempList.get(i) );

			
		//set the rest of the data parameters
		for( int i = 0; i < MAX_DATA_PROPERTIES; i++ )
		{
			if( i < fdrInterface.getInterfaceOptionVector().size() )
			{
				com.cannontech.database.db.point.fdr.FDRInterfaceOption option = (com.cannontech.database.db.point.fdr.FDRInterfaceOption)fdrInterface.getInterfaceOptionVector().get(i);
				
				getDataOptions()[i][0].setEnabled(true);
				((javax.swing.JLabel)getDataOptions()[i][0]).setText( option.getOptionLabel() + ":" );

				getDataOptions()[i][1].setEnabled(true);
				((javax.swing.JComboBox)getDataOptions()[i][1]).removeAllItems();
				((javax.swing.JComboBox)getDataOptions()[i][1]).setEditable( option.getOptionType().equalsIgnoreCase( com.cannontech.database.db.point.fdr.FDRInterfaceOption.OPTION_TEXT ) );

				if( option.getOptionType().equalsIgnoreCase( com.cannontech.database.db.point.fdr.FDRInterfaceOption.OPTION_COMBO ) )
				{
					//add all the possible choices for the ComboBox input
					tokenizer = new java.util.StringTokenizer( option.getOptionValues(), "," );
					while( tokenizer.hasMoreElements() )
						((javax.swing.JComboBox)getDataOptions()[i][1]).addItem( tokenizer.nextToken() );
				}
				else if( option.getOptionType().equalsIgnoreCase( com.cannontech.database.db.point.fdr.FDRInterfaceOption.OPTION_QUERY ) )
				{
					com.cannontech.database.SqlStatement s = new com.cannontech.database.SqlStatement(
								option.getOptionValues(), com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );

					try
					{
						s.execute();

						for( int j = 0; j < s.getRowCount(); j++ )
							((javax.swing.JComboBox)getDataOptions()[i][1]).addItem( 
                           s.getRow(j)[0].toString() );
					}
					catch( com.cannontech.common.util.CommandExecutionException e )
					{
						handleException(e);
					}

				}
				
			}
			else
			{
				((javax.swing.JLabel)getDataOptions()[i][0]).setText( "Option" + (i+1) + ":" );
				getDataOptions()[i][0].setEnabled(false);
				
				getDataOptions()[i][1].setEnabled(false);
				((javax.swing.JComboBox)getDataOptions()[i][1]).removeAllItems();
				((javax.swing.JComboBox)getDataOptions()[i][1]).setEditable(false);
			}
			
		}
		
	
	}
	
	return;
}
/**
 * Comment
 */
public void jTableFDR_MouseClicked(java.awt.event.MouseEvent mouseEvent) 
{
	if( getJTableFDR().getSelectedRowCount() == 1 )
	{
		java.util.Vector transVector = getTableModel().getRowAt( getJTableFDR().getSelectedRow() );

		//set our Interface here based on the object in the JTable
		if( transVector.get(1) instanceof String )
		{
			for( int i = 0; i < getJComboBoxInterface().getItemCount(); i++ )
			{
				if( getJComboBoxInterface().getItemAt(i).toString().equalsIgnoreCase( transVector.get(1).toString() ) )
				{
					getJComboBoxInterface().setSelectedItem( getJComboBoxInterface().getItemAt(i) );
					break;
				}
			}
		}
		else
			getJComboBoxInterface().setSelectedItem( transVector.get(1) );

			
		//set our Direction here
		getJComboBoxDirection().setSelectedItem( transVector.get(0) );
		

		int i = 0;
		java.util.StringTokenizer tokenizer = new java.util.StringTokenizer( transVector.get(2).toString(), ";" );
		while( tokenizer.hasMoreTokens() )
		{
			String token = tokenizer.nextElement().toString();
			String value = token.substring( token.indexOf(":")+1, token.length() );

			if(((javax.swing.JComboBox)getDataOptions()[i][1]).getSelectedIndex() == -1)
				((javax.swing.JComboBox)getDataOptions()[i][1]).addItem( value );
			else
				((javax.swing.JComboBox)getDataOptions()[i][1]).setSelectedItem( value );
			i++;
		}

	}

}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		PointForeignDataEditorPanel aPointForeignDataEditorPanel;
		aPointForeignDataEditorPanel = new PointForeignDataEditorPanel();
		frame.setContentPane(aPointForeignDataEditorPanel);
		frame.setSize(aPointForeignDataEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
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
	if (e.getSource() == getJTableFDR()) 
		connEtoC2(e);
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
public void removeButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	int selectedRowCount = getJTableFDR().getSelectedRowCount();

	if( selectedRowCount > 0 )
	{
		int selectedRow = getJTableFDR().getSelectedRows()[0];
		for(int i=0;i<selectedRowCount;i++)
			getTableModel().removeRow(selectedRow);
		fireInputUpdate();
		repaint();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (10/12/00 3:31:34 PM)
 * @param newCurrentPointType java.lang.String
 */
public void setCurrentPointType(java.lang.String newCurrentPointType) {
	currentPointType = newCurrentPointType;
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/2001 10:52:19 AM)
 * @param newDataOptions javax.swing.JComponent[][]
 */
private void setDataOptions(javax.swing.JComponent[][] newDataOptions) {
	dataOptions = newDataOptions;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	java.util.Vector pointFDRTranslations = ((com.cannontech.database.data.point.PointBase)val).getPointFDRVector();

	//set the current point type
	setCurrentPointType( ((com.cannontech.database.data.point.PointBase)val).getPoint().getPointType() );
	
	for(int i=0;i<pointFDRTranslations.size();i++)
	{
		java.util.Vector fdrTranslationEntry = new java.util.Vector(3);
		fdrTranslationEntry.addElement( ((com.cannontech.database.db.point.fdr.FDRTranslation)pointFDRTranslations.get(i)).getDirectionType() );
		
		fdrTranslationEntry.addElement( findInterface( ((com.cannontech.database.db.point.fdr.FDRTranslation)pointFDRTranslations.get(i)).getInterfaceType()) );
		
		fdrTranslationEntry.addElement( ((com.cannontech.database.db.point.fdr.FDRTranslation)pointFDRTranslations.get(i)).getTranslation() );

		getTableModel().addRow(fdrTranslationEntry);
	}

	repaint();
}
/**
 * Comment
 */
public void updateButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int selectedRow = getJTableFDR().getSelectedRow();

	if( selectedRow >= 0 && selectedRow < getTableModel().getRowCount() )
	{
		java.util.Vector fdrTranslationVector = new java.util.Vector(3);
	
		fdrTranslationVector.addElement( getJComboBoxDirection().getSelectedItem() );
		fdrTranslationVector.addElement( getJComboBoxInterface().getSelectedItem() );

		StringBuffer translation = new StringBuffer();
		for( int i = 0; i < MAX_DATA_PROPERTIES; i++ )
		{
			if( getDataOptions()[i][0].isEnabled() )
			{
				//append the labels text
				String text = ((javax.swing.JLabel)getDataOptions()[i][0]).getText();
				translation.append(  text == null ? " " : text );

				//append the values selected by the user
				if(  ((javax.swing.JComboBox)getDataOptions()[i][1]).getSelectedItem() == null )
					translation.append( ";" );
				else
					translation.append( ((javax.swing.JComboBox)getDataOptions()[i][1]).getSelectedItem().toString() + ";" );
			}
			else
				break;
		}

		//For all interfaces, add the PointType to the end of the translation string
		translation.append( "POINTTYPE:" + getCurrentPointType() + ";" );


		if( translation != null )
		{
			fdrTranslationVector.addElement(translation);
			
			fdrTranslationVector.addElement( 
				new Boolean( ((com.cannontech.database.data.fdr.FDRInterface)getJComboBoxInterface().getSelectedItem()).getFdrInterface().hasDestination() ) );
			
			getTableModel().editRow(selectedRow, fdrTranslationVector);
			fireInputUpdate();
			repaint();
		}

	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA4F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FD8DDCD4E556B8961525EFE1D1E1D122D1D15146969619DBC476469615963B596696EF39456ECB3B6C66EEEE59FA799AD0D1B1B551B0C72352DD169486A52415AF95F0D4D292AAF6E3F4AEB0BA4C0CB3F778D0447FF91E671E675E193943072D6F5F6D57615EF34E731C0F6773BC9FF794D6ED884BCACBDFA588E9D9027C5F0EB4C198BFC690E2C35E3B97E981E21CCED07D3791E005704F6ACCF88681
	4F70FA1CAECFF8F7DD8E60610087A7466956407BCAC178F02E8DDE7C68198E3C372FCD328E2D275009FA32A9DE30B8971E4BGA200C61E73C4225F30B8DF26A748F40434F4C1C86AA0E98EAEAE10E96500B7810482C4F530747170DCC4B5DFD2D1AE27EB1B90A64CFB72337425C40ECAA64266CE1657A8BF7542BDE973BA092EBEEAA749938878F4G62FC06700E69CBF8D6B61D687C47B1FBF771B1134DE637C962312EE251EC116C4EE2075DE213AAAA1A48E30F5DA9DABAEDF5A649341BF21BCCB6119418230B
	6808DD1CC3E502BF00AF2034F969C4EFB43CA7G4C657407B2883D4A9227EB85F0D85057AFBF2B14FDE9BDFD0F907D02ADE3399FDF47DA180FD1964557E077BB59EBE93D18DD247CD30017F7BCCED788D08F5085B09660C35A9ECEB87F8A4F6A23D22D3D3B5BEE2BF6B8EA2CE667FE3BD93442FBC585F0E45AA171185DE99684D667030B0BB46C998AD83E71234E474AA422946BBF7250ED421C7B7625E7EB14491C082D69E147D9D9449CD74A0469FE5C2F6E5947BD5B27276E96C12DBB7CD2FBC616066E681FED
	C9CF116588A7945DDBCF103A6E22F52D0777B439FDB03A1352A5951D3555F7E81BA1F8A07012CFE05BE8DD063698B3EF90723699B54BA1A42EBA5DE8E5B6182D5E3614E69259C1D626F3A93725065223D5F4E64B2354C6024F86DEDDF71C0E352FEEB7693BD100A7G4C85D8GD0E00B53158398ED5846221D1F0F300DB5191CD20D5BE2B50BCE01656DF6FDGCF31CFF21A9AECAE49E4BBA69EB1B9AD2623D651A548E9FADC248C0EFA64BBCD5B7EB15031D73409CE13E4315BC03A29DB14C4270B2589F42DA0E9
	1CA265B6DB3AC5D0506D90D06EF8970C17025564123EF618CD122824E07CE09729139E5391108882601D154B8DBD243C2C60FFA0C0B0AF0765921137CFF44200D1D1E1345BCF389D4D4493E12E0D14739CBAF6C440FB2C8D4B783ADEC25BEC0F53D559D14E9E69FD151CBA4BB1E23E49590FFD4C16C14667E6BB36195F36E21B29DBFA2BD07433C64DB6C340E1E7EDE52C5D3B6F74D11944AE068B992D236C47884DF66D3E66BBD62CBDE79D99A3C8434AB7331F54D5945814G10424BF80C24E7ED364664923115
	9EF4C283934DCA09B3BB3F48A4ED0D0C89243C1748FDE583C0A977251E2649F444690AG1A8186G4281929CD826CFE4344B36154A7216F9499C4E278DA3C843F43D4AF5ADD515D7164CDFB482B9CBC6100615E52F1B4CDFE20FE8136AC407E8B3430BCB668D3A09AE39FFF758F91EC0CAF740F8D08B9D504569D738C91FB5C91253F254AD0907C5C93258BA957E55D4174BFD54218CA528EB221B78E76F057100273F59CD7A52B13B4DA652CA65D9E4FE986D4B22D56C8623EB6DEE1B44683775107EF0C26C1747
	12BBFBC82054E3323A493844620AA893A9A30753528DCD058CEDC26BC9524F5F62F19101F9248DC0FCD4843C2A1358E7FBBF46BEDBF8EA32907B7DA769DAF3CE482DF569E1A7D9FF0ABE2974D9E6530D3D5A36CF26E30ACCF7B1DB33737360C9A39C925A9CF41042F031BE7265A9A2A79164A78388CE6CA36B7B889F47A139F3545A2DD6D6143C0C97779359AE7124DB04611B8CF87BF8392D68FF5F0BF7C4AEC346DF5BCF4A502A9AAB131C248C87B3B1EE4C87FCABC0131315E310131583CBD71305690ADCB0F7
	8118DDACDD38CB1DCE2743F449C097A4A51DDED2271B4653F581BDC015AE4CA31DAD1B4B833A6086B9EE162E5D2DCED712036922009E8F10A2275B5C4352497573BD09DD34E254CE372558ADD948CCG9557E0F338A59A1232F8741FCBF2E499DDBD98F776AEA3759986F88C404C9EECF33316E25C99FCA2CCC83AE51D5F98BBD936AD37D7E9F37A0BB16E3C6CB451E984DEDBAF361560FE755CD76DF2590FD96834AB57F36DA9F51BE4347AD30B6159E1F13A248698B47A30CD9CB8C56CA57332CC1663145BE97C
	C2FCF5005E88002ABEC27FD57EAA156DC9C04F81FA91C0861537AC2B040CA934519633C713686C30BB3BC5736663269ED33129D7AA2663D7F1B5E56FA66FDB30EEBEBAD387CF3345495AFDF33FC304C867283D465E77FD3517B823FFF9BB379CE175B0E5E515ECEB420FEE90AA4F2ECC570A7F47AEDF1FBE36CF0E7F7B14FA9838D71711722FF733D9191C4842B2D9GE98C7DD8A6D3DD6AB2317633B2896E47B2F96E8716C97AFAB2FE194C669A37A459ED23AC87E67B67BA34BD826CCA62364777EA591EFF8A57
	F3B7966E04271B46EC6724DEEECF2FF17DC5A05BFC8A75BFDA2625BF146BFF6EDB225FA9F65BFBFE107E6B33D1FF9448CE667A47EE57EA4F7A53D8F7FF497EE1F5D7ED2A0667F1EC438DB04EB8BBCC474451FA017D723B9C7443GB6DA8158BCB8CDD04F0361C00F87C88190865017381C3F43335B6EF6094574EF2D55F26C042F9F7B898F5D107D8879070178C1270DBDF5073E5F2F4E6FED3BA2GCCE7B15251A55E6B457C81A5F6AD9A700DE359BA6AFF6978DAB5603D567BA325B78DF82F754EAC4335DEBB70
	E68CE06C7F4199D2BE1B4F403C855086A0818483C41F41F1756727C82CB8645A0A561F4DE4BD6C9194619A67B4518BF10C0C871DA6E5C7639881E3453BB33D4BE16699CF5FD33CF0166FA11FFC9FF961F9031E79D83DF5DA09ADF42EA31554D76C6A05982F222291DB249CC795ADD7371F4D03EC5DE404E7BBG1B7F8A5612F9050C2BFD03B84F1D59231E6726AFAFC9DF22B13E52F1D536EF6620B23E96717D1558C1B2F7671D053984208E209DA090A08CE046D92C23F583DF8ED7C73461B55BEB01D68F31CE2D
	F7BC4A4B74E456395545D91FFC5EF5B1772CD6DDACBF3CD63BAE5840A6603E573DBDB9322E85E7F1BDD326BBB7DBEBFD72F95BEA88704C47ECD8C863DAB2D79A835006DF1F238DEF8FEBC314268D734B303D45F39BEE4ABEB79B6E724967ED03B0C64B06C736939BC81B4E9F03B618F24E4D863BCFBEEF9B1A46E8351B632773355B8D9D0DD97F7D52E37C6F82B941GB1G89E3307F7EC1407D5439E3B0FE2D26FBE38597C1BC83E084E07BA77DB456B5835E47E9F5CB89AD9470680B30ACFE3F0C2C5DE6F37CDD
	0A13B9146197AFBB57FD87B59079DAF426A3745F22435FBE879BF7B61C713F6E7E740C763A3B680C763A3B644C8AC687996ADDBA64D71C99FADDDEF3C6EBDD3E1E7A70538F16D359AE17682283A1D948483CB7245502467A7C0029DB147DDFA94C4DDCA42759AD59EBF16FA4C3F8EB0B1FB628DA9F191773F16BA2ECDF818D24FD99AE063685E03E985B4546356A3EB0F031E77D86819E8E90232233F9E22FC062358420A7838801902B821487627CB07B4C30F3786E3EA16670A7877D57732CC16DFAFEE1102C6D
	DCF637731868DD17AF8F66F31EB293B5C8E2B7A9F616668D1ADFB2B9BBC51F3670DB1A1F717C65FFFB50DFFBE1F59A11252E53B25EEF9E68D157C1EBA0ABEB2B7C8C88746DF3CC1ED52716371D4B4B6C536A0741321C888DF9842683FD264C1BABBF6743B377924F74CC37BEDB2DFB87577DDE195A973D1C57G4FFA0036CB7C791268A1EF9B17B7E3BB4AF3C05EC02F7CA100C782442A68CCDEC80EDADE896FBF1D3B57EB771F43471CF62B1574A2D287F4AC4F123CE3B1B21EA6022E64CBF0ACF78BB8B78A17E2
	DF4B92C8DFAB873C89206BD26CEBEF8BC31D9385DC6AAF761ECA670660CBD90C1D46698FD3FA180A4EE26F3D69F8A698893C3C4BF05D9134046C67D9CD7DF63784EB2C4B687E429274D182FCBCC0B4C07CE56AFD1A9F7712F93743E96A0622BA6C900F89534752B550D24C3B9970CA00B600810050312873B745640CD6F540F888140398F642484A4532DCDDAE191743D8F239EACDF2393FF2A9E824EB124B197FABB93D18529DAABAAB17CB6998C870B060D5DE0173E9B90D27E7831E4CE9CB4E629C3B0053D2F6
	51BD20F1B0FE0EC35AAD83045687F850B89C7B8E8A7E472EA302765855A6E84FD1C70521672863023FB10759778C0DE1434136E8EE731E96C20B87FCB6275D55027E4E6534CF7B884DB09E56816311B60E521AG6F62348A9A9F8EG9E42E929D4EFA46053B96DDB2AE3A660291CF69B571B79DFC8FB01463C4D00DBB9ED950D7F82G8F6234ABE83D05839E4DE93F2334D84013B8ED67BA54B10F53DE22726A2E04F57B15C8AB5CC1E8D64087B8ADE9AD668D6234908321A581BE1F53DE1230ED64DE05E3431792
	4E437A2BF0EC38C8A2ED3015248198FB950E8D277858908634682BBC5BFCBC60F33CE82C2D775331A675AA6FF5794514AE84F92F4B178838AE4F831E940476AFCBC3BFE78721CD8F258B829F07D56F7B537F9DF11EBFE0BABA4E2B8ED6964FAE41F1F28178EC1800FBDD2F97FB2C554906CAC5C50D1D1C85985960496AF7CDEE0E1C5FBA816BF7F7AE11998E78F400448938BE6F7B94576711375DGEB39FD1A7B50C94DA569A993D8BD67DD2D2C4FA3E0FD4EF40ED90ABA0D408F3C1A506ED92A5EBBA3F53B9F7E
	944313717F4703CF468BE21342DF39CC4DA73C159E79F7FB70C999AD1173B35F1E7A84FDDB70D404E01D7A055FBD76845927D47959F464E321700AF733D99B8C20F16B66EBE0EC3E865BE57CDEC2EB87DC62343286EC2BA157E0DB5D54D8884F99A48DC0E6F01CCE8FD08E50845085B096A0A298539BE87A78E0F64E129C4C4ED184797C031CED64DC4B56EE729D0F0620A15A9351C7747A63937D34AED5F6903CBCF868F36399DEB21B02355633778DE9DB36201C3BEA41F2D53D133631D24607BB8B75DE27F91F
	6532797F60DA328E6D305B242EEBC95D09F4BCAD0277F200A4CA5BF436EE88BDF8FE1BA54B0C04BC31G9F4B34C4F8A7F7385EDA38F0A15319D1C5EC6FB675D93A5D5D07AD27C401697E300A362F6B202FDF07714934811A56E273CA7B9C1D575BA11D15277DD50F27AF6F52B9AE847861BC4DBFDBC89A89A678446B48BD33D8E23A407C0CEB994A4F8C394C514FD8151F24AF65DF9F27B3496FCC5717FD1E3ADEA366483CA9FD5AE5B00D4E5F05A0EB6B75E873D7E5246E9CCE31C3F4BAC5B3CDAD48690F53393D
	8D52F67174593D1E65301E4E5741408F63E9CE3650FBA864BC4FA6D5BBC5539E0B55AA1A395CF1AD3887B273FAEC87BF9932FC3265724970D3BEC9A06796E8836F71F6705BDE6D3A5551B2C8017473393DFF5B6E69D3B50D8F8AC3201CC2B04D64351E652DDB2C1437EE5D507E6A57213F6DA1686F21F5C3793BE898FF9DA067CB0F7670076DA43E37BBCCA749C6B7533BE8876A9D60FE6C5C61696BD7B44DF86007F05B5EDEBBDC1FC4A0B263C3BC7B641C906FBEF90FC13BAE1E2031D4B2241F8740E819EEC253
	CF0C5395CD447137C672DEBB35CED446C2AB3C87CC540A0F961C25EB27095E71110E46C161933D6323319246C751404B3F81671C89F41DB7GF898277D065222811F43E9F5BC0ECA6534EFF7619A216AC674672CC06FB3F17CF20A1333F2062F904E79AE520DA33D0B7457F356B1123BC8EC0D78C99A2E912DE0D7GC098C0ACC092777DE7BEE7D2A3337F35F34CD710BB5AFC2C4D2CD9CAE36AD06FF5381E52F3C33D57617BF2B13E4C875EC0A81E017D1A5E15C982FC81402A1B201D8398813AEE42B5241036D5
	98E67F2C596EB042A2532ADA96B27FAEDDFAEE77D19F4D46B8AEFA5A94A16652BF2779BBC39A38093531D079195C7CB125B37F86F209FF8940CB86483DB9CED788D08E50F4B37AD71FD6447CA3970E2BA549F42C0B5E12C0670ED844DE05CCB69D58B8F27DF2B2469D2541B86EE17C081F1919C17EGC0B4C07C4DEC3F0665FBE52F6F5E471F76E23EA4C8BB9FC0BF89E203C94C2776C9A40D31C1BFC439996534CC47736D645ECF1D7A4E0451F414DC24E67CD39F93BEBF716389113F79CBA20B9FCDFB647DD3A3
	2923EA3CFB40F57EEE83E9E3DF2B6E85B07A4F2D04FEC8F5DE4F6885A7BDEC604AD0DE45FA926B335DE432DD235CAFE57C5DDF2C60FCFA6CD1EF1777A358B9FEF723FA2D90B10955C142A4BC679FA85362A7CD42737342D22DBD37F9A4DD982C27683CDBC6C2D4595E05031B60E993FB757C6CFB8FCCEC1BB719ADA62B3DF3D311FEEB11FE1BE18B367130DE751D7B7245BBB46F2091500749FB1772F37AE59F533B08C4B98D3468FF293BFCE99013D2BBA791D7E53D7F130BFD2B602E9B053A977E1C06733837DE
	AB6A8BD356FF494797E4B3192BBFD0FC8F9958606DFB1D853A8B39383BF9DBA9B8DED23EC53625FC0D126F7B321167FBF12D122F22E5F4E57D6CF76A324E3BE1E3FA1A9F1F67493E8A13951F197E099B1472DC20D7EC79FCC89F4A401052ED684306CDCA3EDF758C673B126FF421126F4008FD1763EF368EF03AC5A66B66CF94BF9E7DD4119BBEECDDA8762C5F22649BDCBBBAFBF62C7334276A2F0AAC7D8ECF1EEE2B42CB9C319E2D3E4164ED592678EEACD1E44F14C65E8E6D25CA3E6C521164E33157B7F48F4E
	G6D2AFEB246E3335D9823D9B9EDC5A921CD87BCF1B246A569B4A6CB66F8AE0B51CEA3FE25240E1F12A78F9FF70DBED6395DC347C8622E5100FCAEA5104121C7F4CA16E3A6ABBB8829B1B91B6CD64B31FEB9CEFF0C56AB393714B385675434B4B2B8F4591D16D350A4FCF3629AE4BD0D09497D2B7AA9383E78AE8D5797E32778169B0E93383E8805B4D1G2BE5FC863C3F17AE887F45F5DCCCE36C19C01F436D3BD1A463D0A35E159270AE413EB45CC34B398577507A533C7750A6242977406ECA735EC3A379957E
	AF533C7750BC73AFCF775EC3437C2C1E5713A5387C7E296A7D2B5C733F57FA7C3C6AE0715E9F967BBF937973E26DB311050B354FC432168CFDAE3FF4097F7B722B16286F4B33F8F5170154CD953405B600G00E00008DBB0D61D32040C01640B9E5FB8F53739131CA7132FF8A454FFC92EFFDF47678E7F6D4F3539E4E361289D2236EEA29F69B766127A126C8ECA146F726712B2F2DABA3BA416D4265FC677262DE287A533754878BCDC5B4EG5F965C027B5DEF5009E568FB584D9649AA5648E1375C0F9273709E
	36A19C773FD76F255F89819E88908A40F4FFD40FEB21E8202504236EED6D85024FDE7BDE2745DCE36AE41B6DCCD7C7BD765942DBD1D7B01559863884B07ED66C771686926B570AA43645D824D7D447DF859B7E1806FD583B3D065FAA5F0F3BD509C5304FE688EFF57839FFC123F5E6EBF8A31E7BE45E06E7C17751FDB45231ABB92D2D9453055D06BE5DF10478B49B70D4001C5BF14DB819665783DEF9BB364BD3AB7F864FA32663726A10FD3BC9DBA6901B1CF6878C697D0CCA3FC0105B7BAF371331378D647481
	0482C4834483A4814C834809085395G98G0CG6DG9240D8009000C800588834E36E2AADC358A1D7A66DA4CE1305F493590EDA929E4A6DF57D3ACF7C4900DE5688C5FD7068F1D8AEB51BBA851C379C126F1A6EB2FEE61F984156F4A970946EE0FB79EC6E791DFB38FDD8820B64E7BE64AD3A036DED913C8A5EF7AED838703D8538373539D4FB2F350A562F9152375E01ED336A90DDDFA9378D9AA5F9FAE27CDF88A4D8D2B52DE2D55A62A33B8FB5B754D69B3FBFDCFB682051D8D3FD687BEAE3CB75B707F91998
	52CA0748DFFF70D04337878FB4EBC9E0EDF2FC9A7669C030F9E46B9D0C73CAAB88465E0F106B6232CD49E775025FF84D303D3CC8EF58D63E054DC5CDE9235D3BE06DE72F417F18FB40B07C185BC277A1C598AB4932DDBE13B7507D540E8E174847F613A10F539ADCCDF617052C60196DD7678DE77B74BB344EEF42064D67FDBF1165FB11661BFD4748624E94C847762BAE27E334F02777BE5655141EFB2777BE5607FB79BE9670866E447837307E5C765FCA4EB15F09D167E3FE6550FB9FC9BE7E2E2074949FFFEF
	60F355FC60355D05E3704ECA520EE3811F83A0C4466972810AG6AGDA819C11B8A63EE558AC789F9359C42A8C094C3FBB9A557E0D0FE4759AA6BF23A3FD6B19650BA9530A5F626574F3B47291C885FA665D1E3CFC400B6E56EA2326D235EDD51026D9152E6BEEDF9D2C0C3FEAA1E5B9F0B7AB63551CEE2674A8951D157DBDF47EA378DC60857D884B7336B2A21347FCECBA137B645DF4CF49E2032EE832DACE51CF308EC3C2DBA7637FFE3B7F31E17E76614706349D5A71D8768E5FF8ACF707F6BC767E8EF5BC46
	E8EBF610F136936647D61E2E00533E61340DD4A62135D84CD29727FFC66DA274FA11A8C47A5F29DD3D14560AF7DDD534EFE45A0E9DE4FC8317E18907F9AB59591045EAE5F895CDE33141F0474BE3B7355162B0296CEE6434EF143A14309FD55E837143BD98773CADE1AC97F98F46BD1F38C95A398487D895B55A3967B5F294A64FBD3ADD641D4DBDA27D66594F5CD3DE369D661EAFCA6539E702475CD384B6D4C58DBD36327B3C31AB1752B232DAECE2AD7BBADF407BD729B4C6EE86B956A85CEB38E98F3D4B84F8
	F0940E25D76D95F8DF8BD13DEF5CFB7E57FF957F87BA16B5A8BE2DD13D07551FFF5D8F1ED79D726F97D47A9FE376D78EBF469C29549EE35AAAFD4798D325769853D969BB46D8E539CEFE411C511D15244D3258F93F49E66A246BBA56BEE3697AA58C5AE694EF337ADAC21B81F8A227DDD20B7BE8C91C768B3ACE597AE368BBBFC65AAEFA16EE845C4CE9A7691D01BE4003B96DB51AB7827098CE7BE08D6AC860344BE82CD6F0AF2CCF6EC5DA9F0D356B81EF63347787715B09GCEBB456FF5065F0BFD2F25054C55
	73G4F3B8F66BF00FAGB3C0GC0A8C08CC0A2C06AFD1867B84D139B8D69810CGD60071G11G89GA9G39D301BF9573CCBD9226EAB7F9328E4483E49D0893FB546DAC743E214722988DFE7D90F90A3CF829B24EECCD7230956FFCA13ED4D06E8991705E17DC6EC7A7428A8D5AD03F4F106D052FD65121DCFD6E860CECDCF86C9C73AD98F5BE9673CC24F31CF12AF79CBA0552DB27FA4721FFF3637CE986DE7CD4EC4391F40DD6F4BFCB4FE8F7D3DA5D7D4AFD8608CFD610B215484E8D593397F02FF4438AFA37
	8952DA6F47F5E9741EACCCAB60BCEA2892382C979ABD6F0C3CC94708D0489F41E56C28753C9F53C236FC653BCF2135432D3933857FF7DDE6038E52E6143BBD3FAF7354F52AC6317548F66D75380D0EC3F3C1D6AA37F9EC2F7F3BCF6CB7A58A9E0853EDFEG533FD366D98EF9D4E6B3705B9F407D053F2F994E572CA1FC8D82B9A7414FBFF2DFEF76F3F766FEBA8E06C27A88EE5FD5253E67633771B35544875879F88ABC0598F647112559B908E972E3583E49D6F85645483F6BC075CD98721E9E6E19603EC99B64
	F544A87BA601707EA67874B6772BFEBBA9FB36F346FE1C2304572133491BD74AF93DE73CF9864E1B60132F0C73461D7566ED633C4FF611F5BDBF9717590C77FE0D862F04711E14B4F8250C37EB37864F40F8AFE7EA704A5899F9F80B4F99B914FFD08C1E21DFDC2D458F677CFD7B3478B11CBFCE131F4079AFE97213B83F4C630C1FFE2B8F7CF91CFF3BDB0B1F73A0727525DA7C824EFF4F25455F4A797FAA516257BD08736CA437A060D89D21FA5F66BE7F31E663F95541468D5327E4DE10573BF4C9486885FDCB
	95BAAE8965317CD552F5310F6CA1F773B44EF42BA1A835EEAA52EF130FF4653C7F582B64DDD5BB12736012AD1857D4AFF0B9018D0A1C0E5EA14FF0CB0B74A5DB716C37E41F12EF765067659E79A66ED772ADDEB3BA3B279FD06CFE6520A2A744B5927DEC7EFE26944FDBDA21ED86BD086B5F8F8C38479A7FA02EFF3FBFC37764264569728136CE43F313E30398835483ADE09A4A38EB905750B153D0460BB4EDAA60B98F4569748F0DF68D1D7E9541187C17DA703B367207D06734EA24B9B86D22FDC80B6034B8CE
	CB6234770DF85611BF9DE956C6BC93E96234175DC80B663416D224657F84E98689E9831CF6ED895212FE02E3C1E269396D493EBAECBE6DBD598F8DA4DF5D43A35B13EDFD9877E41F2775EFFD58BBB61CC369FD8FFB470662997E3B1F40CBFE987D358C1EDBCC5BFB0E7922C61D0F79351F5E4BB5BE626D6F91CAEFFD445B5FD7F8BFB083AF7E9174B72945E34FA69666880066D8D67EDD317E76CC9F2976786E99528581044B69E37C663BEC1FBADF8224CB8218A7274FF954DF3EA7BC7295C03A2DGF5F27AD63F
	79569855792CF23AG79996CB75F49C61C07A2B452CC57209118897439DE3C79006746E975115778DC270F7315E75020912887FA1B974F81F820260E36D2549122A1AF520F0ED820A7FA711281DF2029638BFE47A66FB1DFF9059AB48265C0B7FA715A81179E5352F1C3896A982BA1AF540F0EA8204F70624D86BC450B465600A5F4DE91E6409CB283570AEF506FD80AG2F1A01FD6645B382376BD5557B09B367BFBE8A7CBF50F1782C62D31B6A7DD70367DF77DF4E2B8ED627EF533959887559466BF9160E509C
	000F65340F33697A9070C8CE9B1B0D7BDFB11C76F3BA2F67BC8E6D66F1243597203CA2CE0B2434AA401BB8ED45FA145746E9B75379BF9270691CF63F9E654D6434740DF4BD8E78BCCE6B5C0872047FC65A4BD4DE93606D1CB6E5A34A13B86D3714B6967090CEBB3C096F77F1DAB10DC566811E79845246503C7900EF66345739AD86CE3B0646AC010007F05A1CC67C76B71253AE223F7D978B789CCE5B7B95212582AE44A36DAD3E3F390A535653FDD0B360831C16C53FB58B82BC1C538E74205E98A51D8175A6F0
	DA860D2912GCF65342F3FC63D19BFC55AB61A2E99F0AB278D5258ABG70E0CEFB229475C6F0DA0B04FA27F31AC863311900A7F15A5B2BD16FBCCE0B21696A1E04B674A4523E23729C0087F2DAB87F46BA04530CA528B7125312DD148678CCCE131AD16FDCCEEBE4F7C31F02F17CA924CD27725A8117B8AD0B7BBB1653963AD0EFA827FDCB73C6819E4BE9495C5FC4CE0B22698A9362F4158938EF53E0505E2BB3517AEC02F4EDBC6DDF86FC776ABEA1579B65B54A32D64FBD2A0D1F526F3BA0FF881711CCBFDEB3
	D9AD1DB6F21F2D957589F4FC0807F4F3F85A0AEC4F3D36FAF25DC45E931A147DC3763AF21F0E535DA42877755E2C7634BD230676CBC8D778B4FE9FF6F9815A587CB45A78D801270DA71A959BB72F7FA1FB0F01BEB6CE5D67E963D3F4FC8A02F4215C46D774E8E38237F16DC6CF9B2F7CDA31F1F063C876567C5938404746BFFA5938040EF9B94F40B8738C5A7854C634316E9934B1EB13270DBFBE2258F8FC53C83EDF75E723740C370D3B0D1EB6FEC7ED8C00F46339BDA1DEE516582258735E30E516B104BD53FD
	6C393351777B49C76958CE78713C5C32E8FD4F86FCAE37F362A97F7B48775239A06F59B8DD4133187EDFDFF97AAE5139219E784D4F62BE72627C613E41D47B97782C67B718A100CF00676F38FF06B26DFDE4A31DEF42A1FD94372F204737ACCAFB30AC66BC0BE551CBE9A9004F6734E2BAE6954E04B54ECC14374E6D696FD6BA5734813FEBA67AEB7DFAA45F1C223FA1B3BD7D0D1C696DEF3DDB5B5FF62ABB86524F6076DDDB6A6BEFE4A97A1BBA93FDCB25B4E1962C09E6A1AD004EA36500574DC2F983A51E7EDE
	CD47FC877087E6213F4BBE9849F76F68EF64ACCFFFE3E7F97B7BEC09363FEFD05D8910FEB6374FA9797AFB39047EE6BE073EBDCDE92BG57F31A1B4AB3825E7A9C4ADB687274F7B54D97887C2067505FF14D43796BB146BD67751D7FF35E7E1AA4EDFF7B296EA4C81F426DFB3E44575F5F14203F794F7336CB6BF7B36086CE5BCC653583EEFD1EFF5F6ED53F17D1D9A1408FFF9E7DDD38FAB8FF5563E562731E7EA6BF6F6D6F93FE6A77952AFB9E249792719B391D5F8A824683AF8D0CEE3F773F5B143C0F6BC63A
	EF6A3B2F18340B49D813CDFC6279EB6D5D8E3B0D7CBEED6D6E834D3B498554063D8776433B2C7F577F9438AD3B5B95DB1A8BC6EA8BCB9FD6B0BA3DDDA6C56F4D47953DF76AC72777CF7A5169FD74042277CFB6C56FF59BC72777550D23533B562168F5BB953D6E916BE5F9C3DCCA5E590D1E5F12BDA4A93C5ED343FE373583776EDFF6AB79AA3F9AF93E5DFDCA3EB7C63467BF7437EBDD7DCA990DBB25483E42BD921BD85A469E4FB27951E9C54EBB43FF4B46FD1BB520641BBF7C37F3BCDF79D9A5DF4357BF3CCC
	3E9314B299CCD3E4E70C4897B9DEAF75AC1349690A9C63A8FC1B1121647B36E4647974BAA55F7F2E7E61E5D21B2D14C9C70EA2BBE96873A20FB28913BC4B6472C50A1C6AD17876006AF7C9FEB9A27DAC9F3D46A8676BEA7E61E532FD19D2A65FAC9F2DAFACED1AD7BB9948D3644C9B05EF13D6A8798E0E22AC5FD5FDEBBE675F50CEBEDA2D1409D175AD77486A097DFBF78F6C3C863C09FCD34812040CEA33D992F46C87G6005FD716FF28ACBBDECFCFCF2596B89AF9FF1E5883A86E1AD89DB8E0BCEFADBDAB413
	4F7C65FF6EE90F30087E3BF0A438E890B2DD741165127A2D22D1D855F1087C13A6AEC9B4D763BDEC17A42C263FBA7FB979CC6C73FA132B0BFE4F3253A8E45A488F87886FB33655E46B7C1CDD4A5E59C7661D346AF6DAB0340D003DAB293DC4CAB117327221651FECEC1C9B13A1246F9196DB6DA6739E53B1496EEC9096C9DDCE5155E5371A7B9C3B2A28209CFADB07C6D4A06EC36EBEF136389996105C6893FDB70DDF1A3CF48F8CB9FDCE61F31A122CB00BF98AF3ED1749892AC4E703C47E21AD3B33107346FCA4
	FD7A745EB5ED69C21A6C64C9E1D0486AF3C069F7183AAD56FEF82923AD42C5B80BA4785FE9323948BFB0G16693DACC396D7D0FC4DCF37DEFB47BB5FD285CAD6C958400A9232FE5EA95A3BBF2F6E683058C4AEG4A95442F6762C91AE22FB46F77CDD85D783305C7219D5489995DDB37523F5B685F9272F79B25EC2314EDA5D0382942107FF9737B50E75AF8401E65549ED27D45FB208625DF3C7953A9AFD5262600F7FB04AC7A237CD089F9CA2A836E6E2322735248EB57AF89CE9AC76A48C11ACA9F113B340FCB
	A49EB650EB69C9BD2595992F5D1211AEE4B688BAF605969AAF39878B0D17DE13ED90B2A43B0336BB3A3C91589545F410763EEFFF2B3E724597A23F2FDB15AE240340AE11C9486AA5B7AD288C724962C55451828FAC4327DD93EA36EF83A564A53C1990EC99CD1AC77F2E9CF52113DC2BE95EB53C2CB6322AD8877C4FG485011C3FDC63B4900F31D87F681FA71D7CFAF480C1866634585E8F709275DE4C67E105BFDC1D97A05E2A90941143E615B3EBDB112A3C3150B67C81B821068A1F5318CD9BC4D1B1039D592
	D649BF8D2CA73310DE7EF1E0C9D84CFF9C2782907EABB5951A4517662778B2BD0B8FC7A69D7B8A06958349F3FE4A3521CFE5ACB3AD777FD7DDD74978A1252E33659A49D4578A7DD177CBB4308B4A0B6F94AFF4329799FE3CA0E95284257DE5C842F25A52145FB512049C79C6E785FFADD5DE8D4AEB19723A6D8735C402B512BF467353740EDF00CD4F21D49A31EB351CB67B423334D75BD25D05E769203625D9971E2517A8162E3F30AD9D6FB938F8DACA8697B20DDF2401DDD0137A351EAD037C37EEC4DEDC40BE
	CD728CD450274C7FE41F6E5076A963BF59277B34FDCA7FCF7669A70ACF857EFD12FFEBE64BD9756F5E4D3FA45F6FEF8C763D4E6E710DFFDD792D9976DB834D745EC3BC50673E0E5F4007D7135F5F72779BA9073BCDD62BE4321E283619496F255474CB22721BC85F549079AECCA3B3992F3D0658FDDC15677F81D0CB8788464F375318A6GG3401GGD0CB818294G94G88G88GA4F954AC464F375318A6GG3401GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586
	GGGG81G81GBAGGG52A6GGGG
**end of data**/
}
}

package com.cannontech.dbeditor.editor.point;

import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.fdr.FDRTranslation;

/**
 * This type was created in VisualAge.
 */

public class PointForeignDataEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener {
	private PointType currentPointType = PointType.Analog;
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
	private javax.swing.JComboBox ivjJComboBoxOption6 = null;
	private javax.swing.JLabel ivjJLabelOption4 = null;
	private javax.swing.JLabel ivjJLabelOption5 = null;
	private javax.swing.JLabel ivjJLabelOption6 = null;
	private javax.swing.JPanel ivjJPanelDataParameters = null;
	private javax.swing.JComboBox ivjJComboBoxDirection = null;
	private javax.swing.JComboBox ivjJComboBoxInterface = null;
	public static final int MAX_DATA_PROPERTIES = 6;
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

public PointType getCurrentPointType() {
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
		getDataOptions()[5][0] = getJLabelOption6();
		getDataOptions()[5][1] = getJComboBoxOption6();

	}
	
	return dataOptions;
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
			ivjJComboBoxOption1.setMaximumSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption1.setPreferredSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption1.setMinimumSize(new java.awt.Dimension(130, 30));
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
			ivjJComboBoxOption2.setMaximumSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption2.setPreferredSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption2.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption2.setMinimumSize(new java.awt.Dimension(130, 30));
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
			ivjJComboBoxOption3.setMaximumSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption3.setPreferredSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption3.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption3.setMinimumSize(new java.awt.Dimension(130, 30));
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
			ivjJComboBoxOption4.setMaximumSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption4.setPreferredSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption4.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption4.setMinimumSize(new java.awt.Dimension(130, 30));
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
			ivjJComboBoxOption5.setMaximumSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption5.setPreferredSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption5.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption5.setMinimumSize(new java.awt.Dimension(130, 30));
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

private javax.swing.JComboBox getJComboBoxOption6() {
	if (ivjJComboBoxOption6 == null) {
		try {
			ivjJComboBoxOption6 = new javax.swing.JComboBox();
			ivjJComboBoxOption6.setName("JComboBoxOption6");
			ivjJComboBoxOption6.setOpaque(true);
			ivjJComboBoxOption6.setMaximumSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption6.setPreferredSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxOption6.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJComboBoxOption6.setMinimumSize(new java.awt.Dimension(130, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOption6;
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

private javax.swing.JLabel getJLabelOption6() {
	if (ivjJLabelOption6 == null) {
		try {
			ivjJLabelOption6 = new javax.swing.JLabel();
			ivjJLabelOption6.setName("JLabelOption6");
			ivjJLabelOption6.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelOption6.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjJLabelOption6.setText("Option6:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOption6;
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
			constraintsJLabelOption1.insets = new java.awt.Insets(10, 21, 6, 0);
			getJPanelDataParameters().add(getJLabelOption1(), constraintsJLabelOption1);

			java.awt.GridBagConstraints constraintsJComboBoxOption1 = new java.awt.GridBagConstraints();
			constraintsJComboBoxOption1.gridx = 2; constraintsJComboBoxOption1.gridy = 1;
			constraintsJComboBoxOption1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxOption1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxOption1.weightx = 1.0;
			constraintsJComboBoxOption1.ipadx = 72;
			constraintsJComboBoxOption1.ipady = -9;
			constraintsJComboBoxOption1.insets = new java.awt.Insets(10, 0, 2, 26);
			getJPanelDataParameters().add(getJComboBoxOption1(), constraintsJComboBoxOption1);

			java.awt.GridBagConstraints constraintsJComboBoxOption2 = new java.awt.GridBagConstraints();
			constraintsJComboBoxOption2.gridx = 2; constraintsJComboBoxOption2.gridy = 2;
			constraintsJComboBoxOption2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxOption2.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxOption2.weightx = 1.0;
			constraintsJComboBoxOption2.ipadx = 72;
			constraintsJComboBoxOption2.ipady = -9;
			constraintsJComboBoxOption2.insets = new java.awt.Insets(2, 0, 2, 26);
			getJPanelDataParameters().add(getJComboBoxOption2(), constraintsJComboBoxOption2);

			java.awt.GridBagConstraints constraintsJComboBoxOption3 = new java.awt.GridBagConstraints();
			constraintsJComboBoxOption3.gridx = 2; constraintsJComboBoxOption3.gridy = 3;
			constraintsJComboBoxOption3.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxOption3.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxOption3.weightx = 1.0;
			constraintsJComboBoxOption3.ipadx = 72;
			constraintsJComboBoxOption3.ipady = -9;
			constraintsJComboBoxOption3.insets = new java.awt.Insets(2, 0, 2, 26);
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
			newFDRTranslation = new FDRTranslation(pointID);
			newFDRTranslation.setDirectionType(getJTableFDR().getModel().getValueAt(i,0).toString());
			newFDRTranslation.setTranslation(getJTableFDR().getModel().getValueAt(i,2).toString());

			com.cannontech.database.data.fdr.FDRInterface iface = 
				(com.cannontech.database.data.fdr.FDRInterface)getJTableFDR().getModel().getValueAt(i,1);

			newFDRTranslation.setInterfaceType( iface.toString().toUpperCase() );
			
			String temp = newFDRTranslation.getTranslation();
			temp = temp.toLowerCase();
			
			if(temp.contains("destination")) {
				temp = FDRTranslation.getDestinationField(newFDRTranslation.getTranslation());
			} else {
				temp = iface.toString().toUpperCase(); 
			}
			
			newFDRTranslation.setDestination(temp);

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
	com.cannontech.database.data.fdr.FDRInterface[] interfaces =
	        com.cannontech.database.data.fdr.FDRInterface.getALLFDRInterfaces();

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

public void setCurrentPointType(PointType newCurrentPointType) {
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
	setCurrentPointType( ((com.cannontech.database.data.point.PointBase)val).getPoint().getPointTypeEnum());
	
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
}

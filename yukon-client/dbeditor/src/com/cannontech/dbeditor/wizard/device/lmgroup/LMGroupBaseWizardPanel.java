package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointTypes;

public class LMGroupBaseWizardPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JComboBox ivjRouteComboBox = null;
	private javax.swing.JLabel ivjRouteLabel = null;
	private javax.swing.JPanel ivjCommunicationPanel = null;
	private javax.swing.JLabel ivjJLabelGroupName = null;
	private javax.swing.JLabel ivjJLabelGroupType = null;
	private javax.swing.JLabel ivjJLabelKWCapacity = null;
	private javax.swing.JTextField ivjJTextFieldKWCapacity = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JLabel ivjJTextFieldType = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisable = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisableControl = null;
	private javax.swing.JCheckBox ivjJCheckBoxHistory = null;
	private javax.swing.JCheckBox ivjJCheckBoxAnnual = null;
	private javax.swing.JCheckBox ivjJCheckBoxDaily = null;
	private javax.swing.JCheckBox ivjJCheckBoxMonthly = null;
	private javax.swing.JCheckBox ivjJCheckBoxSeasonal = null;
	private javax.swing.JPanel ivjJPanelHistory = null;
	private javax.swing.JPanel ivjJPanelAllHistory = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMGroupBaseWizardPanel() {
	super();
	initialize();
}

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMGroupBaseWizardPanel( boolean showHistory ) 
{
	this();

	//show or not show the history items
	getJCheckBoxHistory().setVisible( showHistory );
	getJPanelHistory().setVisible( showHistory );
	//for( int i = 0; i < getJPanelHistory().getComponentCount(); i++ )
		//getJPanelHistory().getComponent(i).setVisible( showHistory );
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getRouteComboBox()) 
		connEtoC4(e);
	if (e.getSource() == getJCheckBoxDisableControl()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxDisable()) 
		connEtoC6(e);
	if (e.getSource() == getJCheckBoxHistory()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxDaily()) 
		connEtoC7(e);
	if (e.getSource() == getJCheckBoxSeasonal()) 
		connEtoC8(e);
	if (e.getSource() == getJCheckBoxAnnual()) 
		connEtoC9(e);
	if (e.getSource() == getJCheckBoxMonthly()) 
		connEtoC10(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldName()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldKWCapacity()) 
		connEtoC2(e);
	// user code begin {2}

	//if( e.getSource() instanceof com.cannontech.common.gui.util.JTextFieldComboEditor )
		//fireInputUpdate();

	// user code end
}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC10:  (JCheckBoxMonthly.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC10(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JTextFieldKWCapacity.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JCheckBoxHistory.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBasePanel.jCheckBoxHistory_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxHistory_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (RouteComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JCheckBoxDisableControl.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JCheckBoxDisable.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBaseEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (JCheckBoxDaily.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (JCheckBoxSeasonal.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (JCheckBoxAnnual.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (11/18/2001 3:41:52 PM)
 * @return com.cannontech.database.data.multi.SmartMultiDBPersistent
*/
private com.cannontech.database.data.multi.SmartMultiDBPersistent createExtraObjects()
{

	return null;
}
/**
 * Insert the method's description here.
 * Creation date: (11/18/2001 3:41:52 PM)
 * @return com.cannontech.database.data.multi.SmartMultiDBPersistent
*/
private void createExtraObjects( com.cannontech.database.data.multi.SmartMultiDBPersistent smartDB )
{
	if( smartDB != null )
	{
		Integer paoID = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
		
		//create and add the points here
		com.cannontech.database.data.point.PointBase historyPoint =
			com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.STATUS_POINT);

		int[] ids = com.cannontech.database.db.point.Point.getNextPointIDs(5);
		
		//set default for point tables
		historyPoint = PointFactory.createNewPoint(
				new Integer(ids[0]),
				com.cannontech.database.data.point.PointTypes.STATUS_POINT,
				"CONTROL STATUS",
				paoID,
				new Integer(0) );

		historyPoint.getPoint().setStateGroupID( 
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS) );
		
		((com.cannontech.database.data.point.StatusPoint) historyPoint).setPointStatus(
				new com.cannontech.database.db.point.PointStatus( new Integer(ids[0]) ));

		((com.cannontech.database.data.point.StatusPoint) historyPoint).getPointStatus().setControlOffset(
				new Integer(1) );

		((com.cannontech.database.data.point.StatusPoint) historyPoint).getPointStatus().setControlType(
				com.cannontech.database.data.point.PointTypes.getType(
				com.cannontech.database.data.point.PointTypes.CONTROLTYPE_NORMAL) );


		smartDB.addDBPersistent(historyPoint);

		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"ANNUAL HISTORY",
				paoID,
				new Integer(ids[1]),
				PointTypes.PT_OFFSET_ANNUAL_HISTORY,
				com.cannontech.database.data.point.PointUnits.UOMID_COUNTS) );			
		
		smartDB.addDBPersistent( 
				PointFactory.createAnalogPoint(
					"DAILY HISTORY",
					paoID,
					new Integer(ids[2]),
					PointTypes.PT_OFFSET_DAILY_HISTORY,
					com.cannontech.database.data.point.PointUnits.UOMID_COUNTS) );			

		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"SEASON HISTORY",
				paoID,
				new Integer(ids[3]),
				PointTypes.PT_OFFSET_SEASONAL_HISTORY,
				com.cannontech.database.data.point.PointUnits.UOMID_COUNTS) );			
		
		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"MONTH HISTORY",
				paoID,
				new Integer(ids[4]),
				PointTypes.PT_OFFSET_MONTHLY_HISTORY,
				com.cannontech.database.data.point.PointUnits.UOMID_COUNTS) );			
	
	}

}
/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCommunicationPanel() {
	if (ivjCommunicationPanel == null) {
		try {
			ivjCommunicationPanel = new javax.swing.JPanel();
			ivjCommunicationPanel.setName("CommunicationPanel");
			//ivjCommunicationPanel.setBorder(new com.ibm.uvm.abt.edit.DeletedClassView());
			ivjCommunicationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRouteComboBox = new java.awt.GridBagConstraints();
			constraintsRouteComboBox.gridx = 2; constraintsRouteComboBox.gridy = 1;
			constraintsRouteComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRouteComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRouteComboBox.weightx = 1.0;
			constraintsRouteComboBox.ipadx = -5;
			constraintsRouteComboBox.ipady = -5;
			constraintsRouteComboBox.insets = new java.awt.Insets(5, 6, 22, 51);
			getCommunicationPanel().add(getRouteComboBox(), constraintsRouteComboBox);

			java.awt.GridBagConstraints constraintsRouteLabel = new java.awt.GridBagConstraints();
			constraintsRouteLabel.gridx = 1; constraintsRouteLabel.gridy = 1;
			constraintsRouteLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRouteLabel.insets = new java.awt.Insets(5, 11, 23, 6);
			getCommunicationPanel().add(getRouteLabel(), constraintsRouteLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommunicationPanel;
}

/**
 * Return the IdentificationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getIdentificationPanel() {
	if (ivjIdentificationPanel == null) {
		try {
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			//ivjIdentificationPanel.setBorder(new com.ibm.uvm.abt.edit.DeletedClassView());
			ivjIdentificationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJTextFieldType = new java.awt.GridBagConstraints();
			constraintsJTextFieldType.gridx = 1; constraintsJTextFieldType.gridy = 0;
			constraintsJTextFieldType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldType.ipadx = 138;
			constraintsJTextFieldType.ipady = 4;
			constraintsJTextFieldType.insets = new java.awt.Insets(5, 1, 3, 62);
			getIdentificationPanel().add(getJTextFieldType(), constraintsJTextFieldType);

			java.awt.GridBagConstraints constraintsJLabelGroupType = new java.awt.GridBagConstraints();
			constraintsJLabelGroupType.gridx = 0; constraintsJLabelGroupType.gridy = 0;
			constraintsJLabelGroupType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGroupType.ipadx = 19;
			constraintsJLabelGroupType.insets = new java.awt.Insets(5, 15, 3, 20);
			getIdentificationPanel().add(getJLabelGroupType(), constraintsJLabelGroupType);

			java.awt.GridBagConstraints constraintsJLabelGroupName = new java.awt.GridBagConstraints();
			constraintsJLabelGroupName.gridx = 0; constraintsJLabelGroupName.gridy = 1;
			constraintsJLabelGroupName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelGroupName.ipadx = 15;
			constraintsJLabelGroupName.ipady = 4;
			constraintsJLabelGroupName.insets = new java.awt.Insets(4, 15, 4, 20);
			getIdentificationPanel().add(getJLabelGroupName(), constraintsJLabelGroupName);

			java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
			constraintsJTextFieldName.gridx = 1; constraintsJTextFieldName.gridy = 1;
			constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldName.weightx = 1.0;
			constraintsJTextFieldName.ipadx = 90;
			constraintsJTextFieldName.insets = new java.awt.Insets(4, 1, 4, 62);
			getIdentificationPanel().add(getJTextFieldName(), constraintsJTextFieldName);

			java.awt.GridBagConstraints constraintsJTextFieldKWCapacity = new java.awt.GridBagConstraints();
			constraintsJTextFieldKWCapacity.gridx = 1; constraintsJTextFieldKWCapacity.gridy = 2;
			constraintsJTextFieldKWCapacity.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldKWCapacity.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldKWCapacity.weightx = 1.0;
			constraintsJTextFieldKWCapacity.ipadx = 72;
			constraintsJTextFieldKWCapacity.insets = new java.awt.Insets(5, 1, 2, 208);
			getIdentificationPanel().add(getJTextFieldKWCapacity(), constraintsJTextFieldKWCapacity);

			java.awt.GridBagConstraints constraintsJLabelKWCapacity = new java.awt.GridBagConstraints();
			constraintsJLabelKWCapacity.gridx = 0; constraintsJLabelKWCapacity.gridy = 2;
			constraintsJLabelKWCapacity.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelKWCapacity.ipadx = 15;
			constraintsJLabelKWCapacity.ipady = 4;
			constraintsJLabelKWCapacity.insets = new java.awt.Insets(5, 15, 2, 20);
			getIdentificationPanel().add(getJLabelKWCapacity(), constraintsJLabelKWCapacity);

			java.awt.GridBagConstraints constraintsJCheckBoxDisable = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDisable.gridx = 0; constraintsJCheckBoxDisable.gridy = 3;
			constraintsJCheckBoxDisable.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDisable.ipadx = 5;
			constraintsJCheckBoxDisable.insets = new java.awt.Insets(2, 15, 40, 1);
			getIdentificationPanel().add(getJCheckBoxDisable(), constraintsJCheckBoxDisable);

			java.awt.GridBagConstraints constraintsJCheckBoxDisableControl = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDisableControl.gridx = 0; constraintsJCheckBoxDisableControl.gridy = 3;
			constraintsJCheckBoxDisableControl.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDisableControl.ipadx = -1;
			constraintsJCheckBoxDisableControl.insets = new java.awt.Insets(28, 15, 14, 1);
			getIdentificationPanel().add(getJCheckBoxDisableControl(), constraintsJCheckBoxDisableControl);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIdentificationPanel;
}

/**
 * Return the JCheckBoxAnnual property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxAnnual() {
	if (ivjJCheckBoxAnnual == null) {
		try {
			ivjJCheckBoxAnnual = new javax.swing.JCheckBox();
			ivjJCheckBoxAnnual.setName("JCheckBoxAnnual");
			ivjJCheckBoxAnnual.setSelected(false);
			ivjJCheckBoxAnnual.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxAnnual.setMnemonic('a');
			ivjJCheckBoxAnnual.setText("Save Annual");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxAnnual;
}
/**
 * Return the JCheckBoxDaily property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDaily() {
	if (ivjJCheckBoxDaily == null) {
		try {
			ivjJCheckBoxDaily = new javax.swing.JCheckBox();
			ivjJCheckBoxDaily.setName("JCheckBoxDaily");
			ivjJCheckBoxDaily.setSelected(false);
			ivjJCheckBoxDaily.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDaily.setMnemonic('d');
			ivjJCheckBoxDaily.setText("Save Daily");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDaily;
}
/**
 * Return the JCheckBoxDisable property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisable() {
	if (ivjJCheckBoxDisable == null) {
		try {
			ivjJCheckBoxDisable = new javax.swing.JCheckBox();
			ivjJCheckBoxDisable.setName("JCheckBoxDisable");
			ivjJCheckBoxDisable.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisable.setMnemonic('d');
			ivjJCheckBoxDisable.setText("Disable Group");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisable;
}
/**
 * Return the JCheckBoxDisableControl property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisableControl() {
	if (ivjJCheckBoxDisableControl == null) {
		try {
			ivjJCheckBoxDisableControl = new javax.swing.JCheckBox();
			ivjJCheckBoxDisableControl.setName("JCheckBoxDisableControl");
			ivjJCheckBoxDisableControl.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisableControl.setMnemonic('c');
			ivjJCheckBoxDisableControl.setText("Disable Control");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisableControl;
}
/**
 * Return the JCheckBoxHistory property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxHistory() {
	if (ivjJCheckBoxHistory == null) {
		try {
			ivjJCheckBoxHistory = new javax.swing.JCheckBox();
			ivjJCheckBoxHistory.setName("JCheckBoxHistory");
			ivjJCheckBoxHistory.setSelected(true);
			ivjJCheckBoxHistory.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxHistory.setMnemonic('s');
			ivjJCheckBoxHistory.setText("Save History");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHistory;
}
/**
 * Return the JCheckBoxMonthly property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxMonthly() {
	if (ivjJCheckBoxMonthly == null) {
		try {
			ivjJCheckBoxMonthly = new javax.swing.JCheckBox();
			ivjJCheckBoxMonthly.setName("JCheckBoxMonthly");
			ivjJCheckBoxMonthly.setSelected(false);
			ivjJCheckBoxMonthly.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxMonthly.setMnemonic('m');
			ivjJCheckBoxMonthly.setText("Save Monthly");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMonthly;
}
/**
 * Return the JCheckBoxSeasonal property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSeasonal() {
	if (ivjJCheckBoxSeasonal == null) {
		try {
			ivjJCheckBoxSeasonal = new javax.swing.JCheckBox();
			ivjJCheckBoxSeasonal.setName("JCheckBoxSeasonal");
			ivjJCheckBoxSeasonal.setSelected(false);
			ivjJCheckBoxSeasonal.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxSeasonal.setMnemonic('e');
			ivjJCheckBoxSeasonal.setText("Save Seasonal");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSeasonal;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroupName() {
	if (ivjJLabelGroupName == null) {
		try {
			ivjJLabelGroupName = new javax.swing.JLabel();
			ivjJLabelGroupName.setName("JLabelGroupName");
			ivjJLabelGroupName.setText("Group Name:");
			ivjJLabelGroupName.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjJLabelGroupName.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjJLabelGroupName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroupName.setMinimumSize(new java.awt.Dimension(87, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroupName;
}
/**
 * Return the TypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroupType() {
	if (ivjJLabelGroupType == null) {
		try {
			ivjJLabelGroupType = new javax.swing.JLabel();
			ivjJLabelGroupType.setName("JLabelGroupType");
			ivjJLabelGroupType.setText("Group Type:");
			ivjJLabelGroupType.setMaximumSize(new java.awt.Dimension(83, 20));
			ivjJLabelGroupType.setPreferredSize(new java.awt.Dimension(83, 20));
			ivjJLabelGroupType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroupType.setMinimumSize(new java.awt.Dimension(83, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroupType;
}
/**
 * Return the JLabelKWCapacity property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKWCapacity() {
	if (ivjJLabelKWCapacity == null) {
		try {
			ivjJLabelKWCapacity = new javax.swing.JLabel();
			ivjJLabelKWCapacity.setName("JLabelKWCapacity");
			ivjJLabelKWCapacity.setText("kW Capacity:");
			ivjJLabelKWCapacity.setMaximumSize(new java.awt.Dimension(87, 16));
			ivjJLabelKWCapacity.setPreferredSize(new java.awt.Dimension(87, 16));
			ivjJLabelKWCapacity.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelKWCapacity.setMinimumSize(new java.awt.Dimension(87, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKWCapacity;
}
/**
 * Return the JPanelAllHistory property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelAllHistory() {
	if (ivjJPanelAllHistory == null) {
		try {
			ivjJPanelAllHistory = new javax.swing.JPanel();
			ivjJPanelAllHistory.setName("JPanelAllHistory");
			ivjJPanelAllHistory.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanelHistory = new java.awt.GridBagConstraints();
			constraintsJPanelHistory.gridx = 1; constraintsJPanelHistory.gridy = 2;
			constraintsJPanelHistory.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelHistory.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelHistory.weightx = 1.0;
			constraintsJPanelHistory.weighty = 1.0;
			constraintsJPanelHistory.ipadx = -4;
			constraintsJPanelHistory.ipady = -4;
			constraintsJPanelHistory.insets = new java.awt.Insets(1, 23, 23, 50);
			getJPanelAllHistory().add(getJPanelHistory(), constraintsJPanelHistory);

			java.awt.GridBagConstraints constraintsJCheckBoxHistory = new java.awt.GridBagConstraints();
			constraintsJCheckBoxHistory.gridx = 1; constraintsJCheckBoxHistory.gridy = 1;
			constraintsJCheckBoxHistory.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxHistory.ipadx = 18;
			constraintsJCheckBoxHistory.insets = new java.awt.Insets(3, 23, 1, 50);
			getJPanelAllHistory().add(getJCheckBoxHistory(), constraintsJCheckBoxHistory);
			// user code begin {1}
			ivjJPanelAllHistory.setEnabled(false);
			ivjJPanelAllHistory.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelAllHistory;
}
/**
 * Return the JPanelHistory property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHistory() {
	if (ivjJPanelHistory == null) {
		try {
			ivjJPanelHistory = new javax.swing.JPanel();
			ivjJPanelHistory.setName("JPanelHistory");
			ivjJPanelHistory.setBorder(new javax.swing.border.EtchedBorder());
			ivjJPanelHistory.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxDaily = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDaily.gridx = 1; constraintsJCheckBoxDaily.gridy = 1;
			constraintsJCheckBoxDaily.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDaily.ipadx = 30;
			constraintsJCheckBoxDaily.ipady = -7;
			constraintsJCheckBoxDaily.insets = new java.awt.Insets(9, 11, 1, 20);
			getJPanelHistory().add(getJCheckBoxDaily(), constraintsJCheckBoxDaily);

			java.awt.GridBagConstraints constraintsJCheckBoxMonthly = new java.awt.GridBagConstraints();
			constraintsJCheckBoxMonthly.gridx = 1; constraintsJCheckBoxMonthly.gridy = 2;
			constraintsJCheckBoxMonthly.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxMonthly.ipadx = 12;
			constraintsJCheckBoxMonthly.ipady = -7;
			constraintsJCheckBoxMonthly.insets = new java.awt.Insets(2, 11, 13, 20);
			getJPanelHistory().add(getJCheckBoxMonthly(), constraintsJCheckBoxMonthly);

			java.awt.GridBagConstraints constraintsJCheckBoxSeasonal = new java.awt.GridBagConstraints();
			constraintsJCheckBoxSeasonal.gridx = 2; constraintsJCheckBoxSeasonal.gridy = 1;
			constraintsJCheckBoxSeasonal.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxSeasonal.ipadx = 2;
			constraintsJCheckBoxSeasonal.ipady = -7;
			constraintsJCheckBoxSeasonal.insets = new java.awt.Insets(9, 12, 1, 20);
			getJPanelHistory().add(getJCheckBoxSeasonal(), constraintsJCheckBoxSeasonal);

			java.awt.GridBagConstraints constraintsJCheckBoxAnnual = new java.awt.GridBagConstraints();
			constraintsJCheckBoxAnnual.gridx = 2; constraintsJCheckBoxAnnual.gridy = 2;
			constraintsJCheckBoxAnnual.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxAnnual.ipadx = 17;
			constraintsJCheckBoxAnnual.ipady = -7;
			constraintsJCheckBoxAnnual.insets = new java.awt.Insets(2, 12, 13, 20);
			getJPanelHistory().add(getJCheckBoxAnnual(), constraintsJCheckBoxAnnual);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHistory;
}
/**
 * Return the JTextFieldKWCapacity property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldKWCapacity() {
	if (ivjJTextFieldKWCapacity == null) {
		try {
			ivjJTextFieldKWCapacity = new javax.swing.JTextField();
			ivjJTextFieldKWCapacity.setName("JTextFieldKWCapacity");
			// user code begin {1}

			ivjJTextFieldKWCapacity.setDocument(new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0.0, 99999.999, 3) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldKWCapacity;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJTextFieldName.setColumns(12);
			ivjJTextFieldName.setPreferredSize(new java.awt.Dimension(132, 20));
			ivjJTextFieldName.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldName.setMinimumSize(new java.awt.Dimension(132, 20));
			// user code begin {1}

			ivjJTextFieldName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * Return the DeviceTypeTextField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJTextFieldType() {
	if (ivjJTextFieldType == null) {
		try {
			ivjJTextFieldType = new javax.swing.JLabel();
			ivjJTextFieldType.setName("JTextFieldType");
			ivjJTextFieldType.setOpaque(true);
			ivjJTextFieldType.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJTextFieldType.setText("(UNKNOWN)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldType;
}
/**
 * Return the RouteComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getRouteComboBox() {
	if (ivjRouteComboBox == null) {
		try {
			ivjRouteComboBox = new javax.swing.JComboBox();
			ivjRouteComboBox.setName("RouteComboBox");
			ivjRouteComboBox.setPreferredSize(new java.awt.Dimension(210, 25));
			ivjRouteComboBox.setMinimumSize(new java.awt.Dimension(210, 25));
			// user code begin {1}


			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized(cache)
			{
				java.util.List routes = cache.getAllRoutes();

				for( int i = 0 ; i < routes.size(); i++ )
					getRouteComboBox().addItem( routes.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteComboBox;
}
/**
 * Return the CommPathLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRouteLabel() {
	if (ivjRouteLabel == null) {
		try {
			ivjRouteLabel = new javax.swing.JLabel();
			ivjRouteLabel.setName("RouteLabel");
			ivjRouteLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRouteLabel.setText("Communication Route:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{	
	LMGroup lmGroup = (LMGroup)val;

	lmGroup.setPAOName( getJTextFieldName().getText() );


	if( getJCheckBoxDisable().isSelected() )
		lmGroup.setDisableFlag( CtiUtilities.trueChar );
	else
		lmGroup.setDisableFlag( CtiUtilities.falseChar );

	if( getJCheckBoxDisableControl().isSelected() )
		lmGroup.getDevice().setControlInhibit( CtiUtilities.trueChar );
	else
		lmGroup.getDevice().setControlInhibit( CtiUtilities.falseChar );

	if( getJTextFieldKWCapacity().getText() != null
		 && getJTextFieldKWCapacity().getText().length() > 0 )
	{
		lmGroup.getLmGroup().setKwCapacity( new Double(getJTextFieldKWCapacity().getText()) );
	}

	//only set the route ID for certain LmGroups
	if( val instanceof IGroupRoute )
	{
		((IGroupRoute) val).setRouteID( 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRouteComboBox().getSelectedItem()).getYukonID()) );
	}

	//some status points are needed for control history
	com.cannontech.database.data.multi.SmartMultiDBPersistent smartDB = new com.cannontech.database.data.multi.SmartMultiDBPersistent();
	smartDB.addDBPersistent( lmGroup );
	smartDB.setOwnerDBPersistent( lmGroup );
			
	createExtraObjects( smartDB );

	return smartDB;
	
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
	getJTextFieldName().addCaretListener(this);
	getRouteComboBox().addActionListener(this);
	getJTextFieldKWCapacity().addCaretListener(this);
	getJCheckBoxDisableControl().addActionListener(this);
	getJCheckBoxDisable().addActionListener(this);
	getJCheckBoxHistory().addActionListener(this);
	getJCheckBoxDaily().addActionListener(this);
	getJCheckBoxSeasonal().addActionListener(this);
	getJCheckBoxAnnual().addActionListener(this);
	getJCheckBoxMonthly().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMGroupBaseEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(432, 416);
		setMinimumSize(new java.awt.Dimension(509, 472));

		java.awt.GridBagConstraints constraintsIdentificationPanel = new java.awt.GridBagConstraints();
		constraintsIdentificationPanel.gridx = 1; constraintsIdentificationPanel.gridy = 1;
		constraintsIdentificationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsIdentificationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsIdentificationPanel.weightx = 1.0;
		constraintsIdentificationPanel.weighty = 1.0;
		constraintsIdentificationPanel.ipadx = -10;
		constraintsIdentificationPanel.ipady = -7;
		constraintsIdentificationPanel.insets = new java.awt.Insets(14, 6, 7, 4);
		add(getIdentificationPanel(), constraintsIdentificationPanel);

		java.awt.GridBagConstraints constraintsCommunicationPanel = new java.awt.GridBagConstraints();
		constraintsCommunicationPanel.gridx = 1; constraintsCommunicationPanel.gridy = 2;
		constraintsCommunicationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCommunicationPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCommunicationPanel.weightx = 1.0;
		constraintsCommunicationPanel.weighty = 1.0;
		constraintsCommunicationPanel.ipadx = -10;
		constraintsCommunicationPanel.ipady = -9;
		constraintsCommunicationPanel.insets = new java.awt.Insets(8, 6, 3, 4);
		add(getCommunicationPanel(), constraintsCommunicationPanel);

		java.awt.GridBagConstraints constraintsJPanelAllHistory = new java.awt.GridBagConstraints();
		constraintsJPanelAllHistory.gridx = 1; constraintsJPanelAllHistory.gridy = 3;
		constraintsJPanelAllHistory.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelAllHistory.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelAllHistory.weightx = 1.0;
		constraintsJPanelAllHistory.weighty = 1.0;
		constraintsJPanelAllHistory.insets = new java.awt.Insets(4, 6, 25, 4);
		add(getJPanelAllHistory(), constraintsJPanelAllHistory);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldKWCapacity().isVisible()
		 && (getJTextFieldKWCapacity().getText() == null
		 	 || getJTextFieldKWCapacity().getText().length() <= 0) )
	{
		setErrorString("A value for the kW Capacity text field must be filled in");
		return false;
	}
	
	if( getJTextFieldName().getText() == null
		 || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("A value for the Group Name text field must be filled in");
		return false;
	}
	
	return true;
}
/**
 * Comment
 */
public void jCheckBoxHistory_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	for( int i = 0; i < getJPanelHistory().getComponentCount(); i++ )
		getJPanelHistory().getComponent(i).setEnabled( getJCheckBoxHistory().isSelected() );

	fireInputUpdate();

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMGroupBaseWizardPanel aLMGroupBasePanel;
		aLMGroupBasePanel = new LMGroupBaseWizardPanel();
		frame.setContentPane(aLMGroupBasePanel);
		frame.setSize(aLMGroupBasePanel.getSize());
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
 * Insert the method's description here.
 * Creation date: (2/1/2002 3:26:29 PM)
 * @param type java.lang.String
 */
public void setSwitchType(String type) 
{
	getJTextFieldType().setText( type );

	//do not show the route panel if the type is one of the following
	getCommunicationPanel().setVisible( 
		!(com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) == com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP
	     || com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) == com.cannontech.database.data.pao.PAOGroups.LM_GROUP_POINT) );

	//dont show the following options if this group is a MACRO
	getJLabelKWCapacity().setVisible( 
		!(com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) == com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP) );
	getJTextFieldKWCapacity().setVisible( 
		!(com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) == com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP) );

	getJCheckBoxDisable().setVisible( 
		!(com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) == com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP) );
	getJCheckBoxDisableControl().setVisible( 
		!(com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) == com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP) );

	getJCheckBoxHistory().setVisible( getJCheckBoxHistory().isVisible() &&
		!(com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) == com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP) );

	getJPanelHistory().setVisible( getJCheckBoxHistory().isVisible() &&
		!(com.cannontech.database.data.pao.PAOGroups.getDeviceType(type) == com.cannontech.database.data.pao.PAOGroups.MACRO_GROUP) );		

}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val)  
{
	LMGroup lmGroup = (LMGroup)val;

	String name = lmGroup.getPAOName();
	String type = lmGroup.getPAOType();
	Character disableFlag = lmGroup.getPAODisableFlag();

	getJTextFieldName().setText(name);
	getJTextFieldType().setText(type);


	getJCheckBoxDisable().setSelected(
		CtiUtilities.isTrue(lmGroup.getPAODisableFlag()) );

	getJCheckBoxDisableControl().setSelected(
		CtiUtilities.isTrue(lmGroup.getDevice().getControlInhibit()) );

	getJTextFieldKWCapacity().setText( lmGroup.getLmGroup().getKwCapacity().toString() );

	
	if( lmGroup instanceof IGroupRoute )
	{
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			java.util.List routes = cache.getAllRoutes();
			int assignedRouteID = ((IGroupRoute)lmGroup).getRouteID().intValue();

			for( int i = 0 ; i < routes.size(); i++ )
				if( ((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i)).getYukonID() == assignedRouteID )
					getRouteComboBox().setSelectedItem((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i));
      }
	}
	
	//show the needed entry fields only
	setSwitchType( lmGroup.getPAOType() );	
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G07F66BACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFD8DD8D457193FED6DAE3BB5D952585424F4EBD313A5DD7E3BECE23BEEC21F1816B4A63129CDCDE39B52A5D937B54FC3D2535004E4B1DE86C886C581C1C5030A0A919479927992A6FC2920E8082222E2E41839B0A373654CC5C7E3127FFB4E396F39F76E5C814110A7BE4F6B1D73BB9F6F47F94F773997A1EF4B4C7494C31EA024248B723F5A94C1087CDC906A53F33F06D87CEBCDC6C175EFABD00E70
	0B14B4F8AE0538E82F4918AD7CF79FC9141658E4AC92CD467570BBD7D85C17F789FE0460136091048B76A79E9D1D4F0EFE6473AE8DAFA94E0467EAA0A370AA857283E58BABCB72E53C9B422B655F04D28C02D077B712A651122F42E364F41990D78894C7533CDFD2A047CF01B0C9B3831ED1FFE3E9E36119C1257BE6C31D1C2E6F3669C262936B8C2B88AFD579C8C52F333C9D7233D078E7EA08D4A1312F9D1E0D5D43C3A60B5BD9E6B13BDCEE17A4DAECE5568151EA175C5E32AB76EBE62F354CAADE36DB44B207
	F3486B9E71B4B4F49DBDC4FE341AFDE23759A5BA663CCEE45FE8B552F2C1F688373FC130EFDB093D12A11C876152B7D07E5F2CEA14651BBEBCDD087FD1C6C879435EE0F2C73D21485F7D54B2C3DF92A97F4312CCB9DF8244978146E479EE894AB2B389E57969D912F691041782995FC4BC6C9C418BA1DC87547DA64A583493E56C7D64C7422CBBA38DD9A1E4343D49E48BFFD311316779FB8C9B284F4418C93D4F073825C0E9EFB5990B01BA00ECC06FD1DF3D523F8B1EAD83D21B5B69F43BDABC1ED60755FB54ED
	959D703B2181E2E46C38E8F1FB2D02407CF10B35D4C79E24A9EF857AA2A1265BE9817DA76A78DDC2C263375EC95751ED76B3790669F29911EFA93AA53FC56C56A0923B4D035FA45FDAD24F94BFCA71667FD3F0D6D7B328AF103084F10549E8675E55A8CBD75A2DC2F665FE433BBA324C78F905E1FEB213E1F132A28B33719D34EDE5AF839F83EA876A833A8E94318CED7CC87F5E31EC5C897E6FF5199DA7C686BCE62F54BAE2F7D8C52FEC6B13A52359DA0FD8BE4B3873B13BC46ED557D15C838D3AF6090B5CE218
	330C59E341B245AE0CF7C4C85ECB17857B46EADE073BE81BC8FB1B55E1AA472BA91E2742D95D465036C542C59097864452D77491BC61ED2D0F34D13CF03956C7EEBD07BE52331C7447A6A32B5FEA11740BD1901E87948F1484141FE2B2D683F5G31BA4EB4175FE0BB6AD655AF4B7B28F99DBCC53F64B5F73AFC1259E591CF193DF67300C37489F21A274C442E838179FECB5B3785F89C92DD2257AC595DAEA85D6C944119FCACCD1CB90724710AB436476E9401019306A0390C4566DCF8BA4CBE692447EA16C4A5
	850BCFB0931F38ECBE85A5C28124BD2410F12F067639B678BDB585EDE59EA4D89C049339DD068656407388B83768E8E8E837DB0898E66FD57451E631981EC60349B885A899481F8ACFA0FF1A49D814EEB24E84AAF847E41C81447C7315DE744F76D5F78A254F546B7AA7213CF7CD5457CA6527621FA7A85F9E40A52069C033019682A582659921FE01FA00A4A016671F2DB36109321072080C81BEECE0E1228B4AFE23ED0F680C3F7D29B7164FE888CE47E4FD1C4A1AAB7BB441D76924A3CF527FE651BCB75ECF2C
	D47D4E3841BC29BA98731BDF8F1136B79D6C9C8D3440083EF377C0A1EBD3E43C47D67421979A00E8D5BC09257D010568A0DE96DDD23B6891DDD6786113FD78DECB06523E865D5C7723A8CE7A5BB6E8GBE0E5FEF49E1B81421EE2D987FA00DB7CB1257BEB0A209A7C4C9323B06147CBF273278823AF21F2C6BEFECC457A5202310816DA883F5BDE24DD53513AE37FBF84453C3FAG393DBDC06795C5103EB1837D31629C19A37673F0838DAB71BFBF5BBB0EBA8DC5247C50E34CF3DF888FBD5FD0F854741136CA5A
	77A659DFB3E49F5CA27BB57AEFAE4F53C973CC855923009682A583952DB0993B00A4208820D8200495E82F6967E68ACABF122E726F15BAFC3432EE126539D9DB0CC5CC5607292C29AACCAD2BDA4E35BAE5248B417D41E49073ED13983AED9E90755B6691D13FEDFEA40E5E764E08FAED0F45750BC4EF7795172AAD0BA2991FACEE17CB249D8A4FA230354AABBDC48F3B4BAE1D36DB07C469047D1A88EDECA5E913C7699818873F0BD76258F4E5BB0ECDC5573EA34C3D10E7501B5F4E38E72FE14ACA562E262FD446
	A6A65360A069A3C4076884C33539C7DC92439DF4FC15F1DFAA45FE5BF01164ADBE5C83B6BCE8979D56E3B07E17DA2073124A687FECDCAF3EE83EEC7617792E00354AE8D5143511588364E7C9611EBD26C26D622AF4506E95BBDD1E91AC2264D4C9857339DCA1556924FC0F3B2196E04EB360EEF57BCB4D5406E56C51ADFA875DDE27E8253C4B4CD7A419F18B0D9EA7674AEA81F93765FAB4BA9FB95DE67618ADF6696A24E9DE45F8F7ED3C4CF83759C44BB0685CEE7711EE3A8DD630DE37E3B2ADD00D7ADBDA5D3A
	B2CCA673FDB26F87046F87726E307BE041FE758B63FDD1D3700596CD09DD210B5CCF7CE49F7713037B1C9ABB196D0EAFCED29DAB55F05E0F19BD013CCF08E69F4CD7A655C3EA996F0D2BDD013CDBDC2E114965DC07FA5754E8FC73A8B48C5B64DA3DDE1EDB25DDA47D66EC68CF6781452F44395512C1920FF3ABF9825966F6B8D8178FF16D1924BF3FC54CD37551C9103FD45EABE3717FB5C67C7F0E911FB3B8FAFC5398716ECB2347FFEF0C78474608FFED0C78AD5ED0714C4E4F8CE51331D73CB4A242921674C8
	C7442B388E9DDA2309BBE5F60CA86B5B45C3E45E62D04DFB997E2CCDFFACFC5EC6663A433C7C97EC6F88E41D4A4AE4E3F734191469715A1D30CCA093F9F9BF74099275FE68B4500D6C1D4E07E7BC9073634EABE4CD2E723E83F4E3127AA064D9C56C710DBE5CB3CDA276812CE295AB4B310A0D69AC5D417318EE91041792EC35DE3A959FE33AEE0837810569267B95CF978B71F1C0493A69EE3B00699A33E0AD1B95AA5D01FECCA7C11AE9C0D1D9ACDDC216BADD1E995355E5C3BC10A7DB2F3CC5BCDDB4444F8B19
	EE5AG26DB8271C2CE28F44D96CCD788E96AF452756610F4D63E8FDC4E31CFA87698B84749B45C5FBA1A2D5E4336A5B8F577A089C564307223F314B99A6BEB6A07C9398BG0F4F41FE661CDD3D06EB71795C96BB5DF611FDF945C5757C0DE1D997D74093BAB51F548993E3BF36073597092CE4DFC606E51F7E575DCDC270BE93F3CEDCA237DD219DE80F3B4DED95BB20C93571696DA93BF8C5C0BBFCFAD03D979E13355530B2049D92E57D7356A8F648FF7E9E8313694E86323F29371FEF21C2168D0D584BC6A4BB
	59GB2CBE65AFB330644764C2BF7913B10B5CC8F144F7646DB69DE6EB4884F820A81E27BF4D1F47F0E70DD88D8E6AE2427FD13F3C05DB735836EG8A4F45761CFB1E7844B8A707324DE3CB5630BA528CBCE8473BF31BE53E737F6DD62171F3036EFEFDD8D626E1D6AE335F7CDC450EC9E9779AB2536F154F955C67D13F7CBC7071BC54EFC70EDA3FB0402301966621FE3FBBDF854F718F32387F3977E3643B844AACDC0BFC0FF76B5935F7AD723D6B6309581571FC1D73F4C0F933B84FCD35EA1EF1EB994F34F548
	737C051B5375418B5C46D0E677BA647BC80D9E5F30F568FB2EFE4C978158C21E2FF83FB6DF66FA988B56233C717D931517795F4B27BF0067CDCE0CE55D1B4C28C3BB48F7FDBD6A70C12BD607599037086BB04BFCF3B6CF99C03E89D0E6D1BE72FDF11D9E5F3EFC3479CFF8BE8FE011BC5FD3B9FA7962F9BE3B8573A582D63A817359BCFA792C9BF07DFE3ACABDB64C0DAF54DD3F9372EF1047288D5A31E1131374E7110047EC4031A17CE20EEAECB88E3D3159B584339C3EC7B2CC6C8C7399B95CBACC7852790C00
	7B24220B483EG4ACCGCA5BE8B296GB5EEC49E0F8F0FF94EF440BF4AB9D37DAE6D19CD5FC6E56F4F833F27EE546CF753B395875D2F1881F1A9F2BF4EF0BF4523D5B82B1B8E5A371370BC08ABFD8F775F6E6F53728FFFAF10DF14A64C724D8D4AD72689AF5064E36DEAC7B119D750F1CB55DDB5B4A87B8AEC3E789F036A79621277589EECE6817413C0EC8C1BB5C06A1C6C29559560F948C3EED2FE84C98334GA881A8FB93688C543D896BAD5F21B7268756DB2881DD591BE63698B9BFB19BFEA3A81F56061ECD
	FAB69CF23C27EFC35C9312675C77556859B08C4A0C5804B66C4BA1B6A46DF076A674097A0FA7264F2120FCDAFD92B47AB0997E219B6710E91BD10664894A306242D8B2B4EE56332969D4A81B4AA300ACEBCFAD5A2B074BBAB708670D497A541832CE5B2CE72F249A14E19617C16A1F188CDF8F4A37D9930E534840FCEBF92F1EEFA5C2DA6396742D8B7B091CE4FEDB388565ECB3CFCC4E0F0272E9ED553BC5CF4E123DDAB949BC4183E927F2B9AF37221C91DC4EF787A6A6675A20FCDAB997EE512B538D6BD10684
	AE43C393146171B1E5A8AA5413A1FD8D4A50D808B238AD9313615320FCDA99A4DD99FE60C1992695E21F7EFF940B057042C26C3FC79C245F47313D5BEDF7C9820E6B5B1CE4DF426D358F59DD086D2418456D2587918CF33AC21F11C8AE7DB312EBAE7DB312A92E514FAF43DC214ED058D9E3B7DD0FAD867D120098362714E085DBCD466A2D089D5BC330DE88FB36E29DF96918BF95423300E2B97E994549FC1A2CDB085FB37C18C84E8FC9DA357D55B4F5143858D162147A9F22BC52C7C9976A7C914B980D47A61A
	7F71F5D95466BE1F682303B5D9694AF1FF16568ABAE7CC6420C640FD53177AB264B487A4F7DB2500E341EBF42FCAE7ACD05DFFFBE990574F71DBF17DBC5DCD6C1D3F8D56A1C09D5B505E778D0CF6DE5B3FAD501E129C8E03E704A6AE9A42319ACC3E4F628E6D4B735D7A3E7C341B34A11FFB44EB91357E7AFBF7BE0FD3E6BE1D1268A4A6E4E9DE2479A533F7C88C727717E8FE9697AA7FDF5D215A842B1F28736A7AA1E7D14C76AF54206D97EDC35B3B32096D33371B0C45C04D5B5176669C6C07FA81F3EC8F345D
	9488CF57E00C3767E3B56FAA6E5737F60FF63FABF2FBF05BE0656D3D20AEEF9FAF6FCC2D3A3C98483FGE831DCCED248725E88A82F0617372CC6DDDEE6914C17012A0BD8B99DC5214A7BE93F3A3C3D5C5635E70CF23B68ABC2DBFB2B685D9188C7814596212D2D7B896FB81947D2F8266DB09973F7487701F60462FD4D2C66DD4BF93FB704755C3C83F9CFEDA13C7D908E870A5C013CBFE9A53C674ABC9642F389D0329C4EFEBF94EF53001AF79D37636675243CA2B9DF9DBC3B007A653094323C3F8614D74F4BDB
	31061497A6670B00E7B4D02C9CDE94323CFBADABD465D5D6E0F933BC243C84B91F3053E44C83AA5E49420DBB034BE3EDCE38C45605FE9ECE23612B1C5F50AEB2CEBA4CD75DA3B020466CA4E34EE73B78FDE7885B004201E66DE4F3BE56E77E4795B2E68FFA4DCEE842A7BC22C518D1CC723617E25EB488678355817582790BF11FC35CC372C2C63BF344C98F7B73CBC85EB0BA5645C33A841E76E7A749FEEFA7695E6C03F60BFC7F0E6CEB4AE7459632EFC896F5A32E00E8B97E40E6F2DE42FA729607C35E57E1B6
	FDE5D05DF67AC086CF09E24729A541B6E5EB6EA28FDD0B1730B5F78E4777D1BCD605333578DDF41CA0612590F7FD970E6DCF52752CF1B734595D08C5B6902C9A429D9CFB15E2FD9076F3ACB31BE053A09C413128E11CBFC4F3ECC99A3D78DE8AF5D10AED67CF81FDC6A9604D250A2EBD252174FE11763936D226DFBA47DF2678F5954E741632F1FFA89C6292CBD1A6BFDD03766EB19925BD086D24977443A09C41B1D7936A934531DB5792AC9942F9E5085D530D690AB966216BB6BF0443B8F63802E091900E6658
	6D940B0570C20EA5D5E0F9719C4B2365D514032D4A79FC2D1CE0BD9036F16CB2452EC3F89A471EAB4772E614E39DB41E510EABCB4A953BA717072A03BF513EB83BC2DB872FD23C28C2DB877EAA2C03BA080B28C019BE21EBB521926CD809D8AC450AA1DC47310CBC02F5C1381FE347A9A6C1F8AA477EB38FF58C2FC49D8B0754BA46831ED82968E52C8A25E3926D738B2A18AE999CCFEFA5F81D8AE7BA7EA3CDCF576890B7278AE57AB6DD67956C057959DE4462A8568E619E0EDD59CE6F59C278BA471E24D8B804
	E7F22C22087BA3477651F5DCB204332B917B1CE2C5902E66D88EDD53F4C0380FE3E7A9660170940EBD6CC19E53B836152E5362A03C14E34394CB5B8775378F31BF24527A03F0A3474AE87B6F06F0BF47EECFC39E8E0EBDC47946C0F8814752AF51B58A0493B936097651469A1331208631C10AD5C03819E3CF6EC19EDDB538DFFC6FAEFC1FA3823005B5B816D476876C9112DD6E0606C3DE3B3555BC54C5C7A6B6BFF8052E19C87E4CDA1C9F946CA6E536C3388FC82A45BDE67F7BF86E96F56F2DC25714865DF7D0
	62FB369A26554A670F354A9E336D79FB8C0C6745D26419G71C5F5C43799256A7DEDB24E34243275AE0BFFA2A01E747BA502BABEB9A01E48EB8E48DF36C79D1F2E4A4FF4FBF2876A167CE4046078C9F14877EB9CF5CC27F079997F1F5B04399B5EB64C0330A05CC1268572B839C3AC0427CB3CD2488F9A5A6DCE51C5AE24976F2B28AFAD2C2E2DAAE17CE7DED16F5F572DF8BF647EFDE63DECD379597552B6F97D1BCE67FA3D00BB6A51E7265AE81B0370AC0E25DEC4BF1AD70F7D4AAE8F11B53F815A97D0B71083
	A88CA892A896A83E81536E27E95376C35D81356FE7F78848393FF5BFBB2FA7E77BAC6DCC77949D9D889FF5383AA1385F0A14F9BAF46268BCCA030749F26A25E5327CC2D7966D9ADAEF3D3DCA7639D0F74635EBED3C4B2A171679463F6552791A592F192FA552794BD430E378FE1C2FBD5CC04A37389DA3CE17CFCE77BBBAFFA1FBBB31BC5D2E81A22747AB8E0ADE2FE8E5D7BE65749B69B8349052467374B186F297E5506D12D81AFDF47E135988739A20A90D847B45F0FB88BDD097B5F5C11EFE206D329DA5787D
	93590F994F05C67DF9EACEBA61BD8F52AFECC4BFDDD6037EACB471F1258E7D3908E32F5623BFF7B4215FAD213A4C0070EC0E3DC6B1F20FE5BE472EE5633E82268D256BC40871381D73C88D61DFB7C34C2E7F14933A6E6B695819G3AA5B5E15D1B1A489AE5406D259B8E95A6126EE4937107A293567DB71BC62B7BF4CD5DCB10AF4C2454FD847CFEE379726504984FFB566A577D4F687CB392524F663CE33A4772BBB57FF8D3205FA5C27836G3F7B7B5AC0DF7FBE1DFB8A9F180C199F20CD4CF40E6AF409CEB72C
	FBD8BABF459E83EA6469FEDCC14A12C43F447A7C0AEB24A8A6F7D665E8F263F9A24A5D2FEAAFA42E8BF8003916ABF26F56485DC2656E05F4D6AECFFBF9305C67A876B850F41E6E1672C039CFFC22487D57721B11FB967040BBB82CCD1E20FB9E510047FE006D74693358CE17FC006DAF210AE0F54DB0EEB4A37686454879B63953A56F43D65D6C7B9A04079E2E707D6ECD7388359F0B3E1AABFC1B7643BE115C61942D2C2E6E5B0FE7F71EE62C2B677687567B2BB44D94081F465394668557FBAD457E8C94435359
	73826B7D9B5715FAFFAAEFBC7D7BFCA8139056FBBC7C5E91D06F35F48E12G385002757E881D0394C1380EE34DFC8E52570276FF3B056CABC4C1F89E50E22034D60CDB59CA62485DBDF20FEE1EFC6F0E450537286B0D641BE87DA1EF8CAB7C6F8B60B1565E7AD8BE229E83B238EF5C579A6C9BF3DBF12EDC580A7579DAEB20EFE434517D8D08EF66E9EE5D9E6C9B515B31CF7070F4AFEE8F740DFBBFD5FC63D0514D78469460C16E873C467B7F2E75417AD95623FE5338CCF72C8F54AF0A26198971D1BCCD31A7D8
	3F8FA876A4D09CCFF74993285F6A4F957DFE67994F782655AF21152D35947D6A56846B77619A54AF094B7475B5017A4D20E9B25BE05E5506E9DECD8D564FC857183FG6A60698E24856AC7CEE0D03F9F270D25DF7AA87A7536315F0AFECFFB0275FB49037AD939CC851782752B22EBDEBF44CF65E92A77846BF704E24F814D6669BE5D93281FBDC5516F45BD6319A3E8750BEDE3E723C4BF564623026E910DC5AC5F03934C775A387331BD1BD7687AFCC19B5B33C961789B940FD361EC0FE05BEE5CA3D882F1DD6D
	F8A65CE5A07B4A51901E8F3498A8992860G743187700C789FAED588E35C7169F1FB3A444B22C3F5E4AB1FFB17CE6CBD7EC75E4535786C876F90667C5D1214D077FB3B8E303E5AA1BF7DBF5DC6F1265FFF27927DA2A0EE8ED09CD0A210712049D8F890757B4D25D2269F390A5CA2C9E60B0D5EF2C6654845E385A61B72CC46DF1FD32F57336F4AB6240410317AA0132D7920763E5DA13A6611G8F6772BC675F8D4FD3660B41B724816C763ABD22D73A4AD07AA602BCF6948C2967736C4CE16F901E9BC4C3797300
	04C3A6E35EA1F6BF14657B1EAD782C62FE9B66ABBD444EAA5A6159AB67E3EDEFF376F846868F641DFA0835BD921E813FDF06F62774ADCF79C9BE6586DB4FD50F08636215823216BE5CE59E909D743D869A49625618B4F174F41A4575DB834BE48757ACEE76513C00B865CDBC967FC0E3B6AFD79B5712CF63B4EF313138573B576B4449EF59496F2378826EF24E126DBA6F905E75DC38C6AFBE0E47BFFAD2AFFEA90FCF5E29971F5681BCE89FCA5EAAC0BBFC2F61FDA1649ED5D5E5F5EDF55D5E1A6A5A52422A0A92
	4C73F203811E15D530DE276589C6253C146CAD214B2B5CDB458AE2796AD7A979FEB03CC31B6FA0982DF81B4F6C7279C42FFDF0DBE9E1C5E9E1E5B559375BA230F704A9E37AEF698741984A57C74CA077ADAFE4E25FD2702F7FA434BF3BA48547832D8F67F73076EA6BD05A6D757E3FFF0715794BACC576FD5DB722B3EBDB6F57E25B1A59415A649CF84E6FE077CBD81A19F518E6F187EBFF49704C6EE4E9D87BB3B60CE76CAB063C0D1DCA7B6B6EE46D6FAF3C7D7DFAAB290C0037E239FED74DABB00E3687FC2F6A
	73A62D1F91797ABA514FDED923582820E9949BD5D5C24DD66DAB11538EB7A97555A42A7B590ACF1626240628AFCF277C8D0BCEE5DFF4FCFE7203BC65774F57AA328F5C106CFA3CD8D9CB57A965BAB7A8651AAA36697B3C55EEF63807D48EAF6F71D41067CE4BCE392C700DCA39EF94A865EEAD9FDF395EF2DA2E954B5D38D9A96B6219607161EBE7783852497CF39EBC63BA597DA8661FAF1D9D4BBF557B89091057F8D8714F02434A9C064971474F08FF32B3E779401947BD39B720BF1623D95CDC3A0E163CA3A2
	53ADE32B225B325C715969D89E3513EF27CEDDE3BD54EED34ABF26B3A60BFCCCAE869D1B01A42030436CBEBF4B37C8E7CC7E8B9F13E79EE6B61F834F79F2BEE67353974753A7AC063CC9AA1BE79EB1995F047E60CD5EA7ACBB3DDAEDD77A96B26A36E1B04066725B32AC6E857A2D0320B70839DDFE1219250EE7EF794AF14F369299874D8E1F48ED1A3FC331697076715559E35B83FDFB7F7BCAD9575687733A3BD809CF770C0F57A98F65A5A22FC7CB143286578473EA2CD0621FCA9D9F2FDDE9141799F9DD2CD4
	4A92AE85736A2BD662F3760C0F57409E4A4B093CBEA7D72F64B5CC62EE6577B2556F625DDF4477D5C6DFBF35CCAA8FB69FBFE988FDA7304F20FFA7F04020FF3F55E6987D9BA09EC368EF00DCB1283F8142568F9F52773E72215DD6837582B900269C4135C4BA75A972852F60F54D816B901915122FF6C948FFC9EAE8DD7F12BA76373E12D249BCE140ADC9EEA7A99F7137D3C9FDC9EE8F8599161AC6EC64358F59A416D446B3689E07C39C24B05353C12E334971F726114EC0F7FD84DA9D86FF7EF06022E811FA4C
	C3BCCFD77AEA355D5932C90E7BB81D5810FCA785FC5FFE0DDEF6BA8189DDC3EC6DFCEE883F539681F6DDF8844F577F3C09B4EE3DF7C71517C6FB6C12C3EC153BFF79BE5E0F0678F9FB971E377F0672E807F09F105405E77BFF30613AFD9AE031DDF857FE1B9D4BC866E5F45BC95A6A23303E8732825D585C8767A87349B48B6F6E8E06383BCBE7FFCC069F53FD9FD207570F22DCABF389C64669599CCBBE09D87EB1447696A3F67D986EC5BCD8CF462EF888A78395FEE8B25681F581D9017CC05300E6824D81DAG
	3498A889A8335BE4ACG2AGEA6FC65F7F616A12D0FE8220FC9102B6G2F195DFBE7B6BFD60B755D8BE505F7234CF754224D67F5234DE3E9DA61A313B19F2862A32C37F9F5D8EF9D00CD79884B482E47B266FC04E5186AC95A2590B69E873D0F0F375EFE38DF603E75D3FA370B34353A63E857E30DE47E63B90EF62924F31ED0F6A16FE9B16FC7FB5CF786F5198AE5449CC7DDDEBD033AA49EC7DD3650EF9A161E00B0D05F09716AB21B4E7B995FDBF83B13201C2893586E528BF76878E6991BE4B5B49C10ACB651
	2AE9F3713C4DADBE01322E26ED2B20877ACE202E9E2C3B75F654578AD8C48FFF8F07373905BDD846ADB4ED66C9133188287164F875FD56C4C22CDECE57046EFBBF2E993B6F9D22E765A27419E4F3C47E6ED28DFD07E3F050A772BE7ED2CDB647BAFD5DEE1F1D74F872BD6F2131766BFACE6A3D27629EB31F76FDA816AFF24CFCDAE27B1C87E85DF9CEEA77BF0FD17C7AC96D7E6733C3D827E19017F0924FF462ED935B37FDE50279F65AA726EFAC75336ED3DAFDFFC5713ED3DAFDCFF19F36C15C7CD3286F3FFB10
	FF45E970615372B7A8642763F428EFB2E6676A4D33264869276B64A39489F80CA6EE8104971F56732114D3FABC126434193D7ABC8A812F5644F5C0382FD70FC7DD319E8F0F1CF6EA889EB3G0F52444D0570C28DA6FF6F34BEF4BBFE26FE6CF67C47FA7DB954627A60B954CB757AF3283F542B67D08CCB2CA77D67900CFB3DBC5D1B9CBB433137E91984BBED37CAB60E27B52C10718E11B0C4FC853D1BF405E23DB21625424E48D8DE83690BC0E51B1B3FBB3621011E93599D8E965EC45358DD50B5F1FBECEBA0B2
	5ABDE6155CBBB9F60657EDCF6D447A2E01B1736977DD5F9BB31FFEDB2E23F303A53D5A365CC27144DEEDDBFE22965B32F08666C2E730ADBFD5B731BEA8EE02795656CFCC5F7B697CE65E992D3E8FD1FC61992D3E6D7528EFBC44351F656FF1B62979C71EE5F599A3BF971C8D752E4671C662D3BEF9CEA37703C17744EF4C86BF1BE03E57471D0F5968F6BA1FDAFCD6EB3B880AAFBD2B355D0E33E83BE4086BBF473F7FBB41F1BDEA02637A7FCEF0DC7FBA9D3FE34FE9757D9645971C536A1B4757C7F190575C077A
	EE9A77B84B726D1EE0BE6B844775947A5D37D9FDDAFDD7D0BC3ACF2B6F6DFCDC0F01384273A6A3FBCFF3EEAED9CB59DD8E3BCBECE3DF5566329DA1DDB876112A5F4F8FCD7E7E4C6BDF820FA76C0ACE3FD57D6E33CFBE6F11C965416EC97D122E3327C2DD4FB80FF7273268FD0AA88847F0ECB57DF64B828847F34C6744BBD6899C2B22EB74460FE14D73B1E25F2598F9E72A17E339749EB2F9BF4A4131FB8C84A36FC7CD6158708872184E311F9969DA279466FA9C5BFE8A79A6F1EC6AA9641BF6813168BD48B71F
	E38F56A15FD20ED9F211EFB347F24FA25FF00E1D25FB8D33A0BC1BE38FD3ECAE0497F1EC0872C80030319F3103743EF08104CBB936759C674B312BB4DDB804A37B318FB255A93EF8C875FBC16D647B65CBDF820FB9750ACE0F28FE9F2E1FFC5E16C965A15FD525FBA9B1D01F73F9BD4F26FE9D8F61259C5B10017E104C6B7E4E330ABD6ED67D5E7D05FCF3FCF45B34FF89BC42D46348ED2A5F9BED134FFB6F1740635D0B0ACE592A5FF7FE89E34A7D134A031D67BC6F217BC29E73A5328FAE3FF76241BB48DDE6BC
	07FCDFF30F2F110EBBFD90EF63E9F6B8836F6725B793FB317348FFF40EF59E09673B0434F74AE7G0F35A96A7B311D39013C0ADB88AFA65BAF69F8B08B72C483B12C0D0EE1F190DE423127699816B7G732781BCB77D7A48D8F75FC713339B4A59A97FE6BC92E8BF518738EDG6FC21CEAD5E47DFFF45C1B8EF1B387D02E5F53F1EFBE0497F16CFF681814E6015F961475DA6ED832260EA2EB33056DD1E6F0D99A69185985F82F055F19AD8E347355B60157416F0A755FDBFA050E29D3210CF0DE4EE7F5215FC979
	AE55AD86524E63694FE7877A1B0F0E2B0990AFD8D17F3AF3E3691FBE0A7E95D6C5FF46B3BCDB7F1D14C84A3B9152F7D8D13E171A826523FFF0C71008ECBC4D1346C05B7DCB3B529EAAB246F3E7AECC75BE8D11BD5A2A3D933172D2605D060AC3CABD991C6422034E99EF0B57EEF6ECABAD2C443BD4723BA09D827FFD39D3A947B830D9DB0EF27725EE2FFCAF0C653B7330126FAE430D67BBF0D449F7E164C64F26D9DE5FB1C56EBBBED44A09BCF9637C7F2BDB49F74F1E517255826F9A1EAF6EA3A55F3F575DF83E
	369EA55F699B3E034272DEBA2968FB67E9251C832356D3A07F47FA15FCFD63107BEFE714FC7B33EFBC5F830212EF6D3971697BC20A22AF3D72A017B32B664679FFADDD65974671711FC56FCA323F0F386D53CF21C30BFAD9C877F905B541FCDBEE7D7D33D1975A738C0221D3F847C677BFBB0574ABE4A3556F01163B88E819A4E44937C28B65EB2112308A91FA05D592B2CF18AF0B0572C5A6C9D8C103747E8D2425813CD4A389ABE918DD22411C72F59C5FA561B3EA7B3C4F17AFDF2E04C8CF8DAA2C8AB04123BF
	5C772779FFBC65CB955E6994B610DE6E04682507622215FC94CA7ECBDA87058C8F79D29869ABBB05B49FFD247B242B8E31CB489BBCCE3E146E13C4EB8B9E377BA4E1AD7D44EBB9B995AF6FB07BEC9251F5FF171066A2FF73CBD843229DE657D0B9BBFD5CCF4CA5243474D3F9C96FA8003C39D4DED2CA99AFA577212C9DDB8FAF1E13AA988E8AAB9CEE3375205982D66B94B2A41BD774595C8E2B5F73C1B3AD48F805F4582443CF05625E636A93E54B48D5E0AE74307F7B53D6ADD9F550A088FE2FD0CED312CE3D0C
	273036594CDEE0A5FABBA572B74C5C5EA29E77354D527B3FBA343E4FA02448CA92532F72C34D3EB3E8F652EA4C6097DBE93D64GA2F981A21F2685618AB542E19467D1F67B63955F396F2D33140712D59236B0EBC25672A1516DACEF999C343BC4DEG18960A5F440BA7E94AB4E95678EFDBFB78196583608A6DC22A3322027EDFC97F2FA27FD7D22412A215D5E05F2542287F3471FE54197A8F4813C565A19ED0F690AAD17A434B0F5F7DDC635284506E2010CEBFA78B75102D24BAB6629C903DFF97751DCDAB27
	47FF0BD41307F80B1F143B564FCBA49AF6528305784BFB9BD27F67C72986A18D9AA7BBE0817FA527A4603FDE56D6D3A537073A9E99D8C94FC3CEE853B48D87C364007DE99AA2DD8AB95AA1FD85796CD3FA08B486395485BC4D4AA0B7A7E8D6303F92B8975C271DF6D3A4340EA64D277F3FB76E8AA039B6513CDB385DF67B3924C4967C98F841D7CF52AAC552E21524062F1E24750A24C4C2B210108F2125FE75A4ED8E141470A57E1E71551374D0202419DFDDC90FA9122ED2C97A957453130112E26D9BB8CA36A7
	C87FC6FA46559CCDD521C60EEED021E99CDDAFA87E1F8243DD7149E3C70EFDF87AD8894CE1683C2710DCDDE960A1723A8E04B207CF97627BB7904C8B78AED9A1FDAB21C1571EA9A16C1996E8CF9C0A5EE187564CAEA44F6498DA7487988F8799E548A0E6BF21939A373BD8A751DDAE85EA11A5CB9ACA8BF2676834CE68AB2553A70ACEE9AA1D52465462AB2C53B7951D8C02DEBD5D045C13599D5D22483DDE66CBBA947C9B1A02D072397A3D2024EF3E9B72FBC909DEF636ED74E95F3FFC106E8536835E6F437B11
	AB8CFB0450770BCFB84D8E07E4F68C37382C642EF16BD5C9D46EEFBFECA0657BB00D9C4962FEC96F04DFD465797FD0CB87885519571CCCA6GG50FFGGD0CB818294G94G88G88G07F66BAC5519571CCCA6GG50FFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG06A6GGGG
**end of data**/
}

}

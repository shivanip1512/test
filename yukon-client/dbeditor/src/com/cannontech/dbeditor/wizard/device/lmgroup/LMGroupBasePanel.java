package com.cannontech.dbeditor.wizard.device.lmgroup;

import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.data.point.PointTypes;

public class LMGroupBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
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
private LMGroupBasePanel() {
	super();
	initialize();
}
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMGroupBasePanel( boolean showHistory ) 
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
	if( getJCheckBoxHistory().isSelected() && smartDB != null )
	{
		Integer paoID = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
		
		//create and add the points here
		com.cannontech.database.data.point.PointBase historyPoint =
			com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.STATUS_POINT);

		int[] ids = com.cannontech.database.db.point.Point.getNextPointIDs(5);
		
		//set default for point tables
		historyPoint = com.cannontech.database.data.point.PointBase.createNewPoint(
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

		if( getJCheckBoxAnnual().isSelected() )
		{
			com.cannontech.database.data.point.PointBase annualPoint =
				com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.ANALOG_POINT);

			//defaults point
			annualPoint = com.cannontech.database.data.point.PointBase.createNewPoint(		
					new Integer(ids[1]),
					com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
					"ANNUAL HISTORY",
					paoID,
					new Integer(PointTypes.PT_OFFSET_ANNUAL_HISTORY) );

			annualPoint.getPoint().setStateGroupID( 
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG) );

			//defaults - pointUnit
			((com.cannontech.database.data.point.ScalarPoint)annualPoint).setPointUnit(
				new com.cannontech.database.db.point.PointUnit(
					new Integer(ids[1]),
					new Integer(com.cannontech.database.data.point.PointUnits.UOMID_COUNTS),
					new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
					new Double(0.0),
					new Double(0.0)));

			//defaults - pointAnalog
			((com.cannontech.database.data.point.AnalogPoint)annualPoint).setPointAnalog(
				new com.cannontech.database.db.point.PointAnalog(
					new Integer(ids[1]),
					new Double(-1.0),
					com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.TRANSDUCER_NONE),
					new Double(1.0),
					new Double(0.0)));

			smartDB.addDBPersistent( annualPoint );
		}

		if( getJCheckBoxDaily().isSelected() )
		{
			com.cannontech.database.data.point.PointBase dailyPoint =
				com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.ANALOG_POINT);

			//defaults point
			dailyPoint = com.cannontech.database.data.point.PointBase.createNewPoint(		
					new Integer(ids[2]),
					com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
					"DAILY HISTORY",
					paoID,
               new Integer(PointTypes.PT_OFFSET_DAILY_HISTORY) );

			dailyPoint.getPoint().setStateGroupID( 
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG) );

			//defaults - pointUnit
			((com.cannontech.database.data.point.ScalarPoint)dailyPoint).setPointUnit(
				new com.cannontech.database.db.point.PointUnit(
					new Integer(ids[2]),
					new Integer(com.cannontech.database.data.point.PointUnits.UOMID_COUNTS),
					new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
					new Double(0.0),
					new Double(0.0)));

			//defaults - pointAnalog
			((com.cannontech.database.data.point.AnalogPoint)dailyPoint).setPointAnalog(
				new com.cannontech.database.db.point.PointAnalog(
					new Integer(ids[2]),
					new Double(-1.0),
					com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.TRANSDUCER_NONE),
					new Double(1.0),
					new Double(0.0)));

			smartDB.addDBPersistent( dailyPoint );
		}

		if( getJCheckBoxSeasonal().isSelected() )
		{
			com.cannontech.database.data.point.PointBase seasonPoint =
				com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.ANALOG_POINT);

			//defaults point
			seasonPoint = com.cannontech.database.data.point.PointBase.createNewPoint(		
					new Integer(ids[3]),
					com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
					"SEASON HISTORY",
					paoID,
               new Integer(PointTypes.PT_OFFSET_SEASONAL_HISTORY) );

			seasonPoint.getPoint().setStateGroupID( 
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG) );

			//defaults - pointUnit
			((com.cannontech.database.data.point.ScalarPoint)seasonPoint).setPointUnit(
				new com.cannontech.database.db.point.PointUnit(
					new Integer(ids[3]),
					new Integer(com.cannontech.database.data.point.PointUnits.UOMID_COUNTS),
					new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
					new Double(0.0),
					new Double(0.0)));

			//defaults - pointAnalog
			((com.cannontech.database.data.point.AnalogPoint)seasonPoint).setPointAnalog(
				new com.cannontech.database.db.point.PointAnalog(
					new Integer(ids[3]),
					new Double(-1.0),
					com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.TRANSDUCER_NONE),
					new Double(1.0),
					new Double(0.0)));

			smartDB.addDBPersistent( seasonPoint );
		}

		if( getJCheckBoxMonthly().isSelected() )
		{
			com.cannontech.database.data.point.PointBase monthPoint =
				com.cannontech.database.data.point.PointFactory.createPoint(com.cannontech.database.data.point.PointTypes.ANALOG_POINT);

			//defaults point
			monthPoint = com.cannontech.database.data.point.PointBase.createNewPoint(		
					new Integer(ids[4]),
					com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
					"MONTH HISTORY",
					paoID,
               new Integer(PointTypes.PT_OFFSET_MONTHLY_HISTORY) );

			monthPoint.getPoint().setStateGroupID( 
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG) );

			//defaults - pointUnit
			((com.cannontech.database.data.point.ScalarPoint)monthPoint).setPointUnit(
				new com.cannontech.database.db.point.PointUnit(
					new Integer(ids[4]),
					new Integer(com.cannontech.database.data.point.PointUnits.UOMID_COUNTS),
					new Integer(com.cannontech.database.db.point.PointUnit.DEFAULT_DECIMAL_PLACES),
					new Double(0.0),
					new Double(0.0)));

			//defaults - pointAnalog
			((com.cannontech.database.data.point.AnalogPoint)monthPoint).setPointAnalog(
				new com.cannontech.database.db.point.PointAnalog(
					new Integer(ids[4]),
					new Double(-1.0),
					com.cannontech.database.data.point.PointTypes.getType(com.cannontech.database.data.point.PointTypes.TRANSDUCER_NONE),
					new Double(1.0),
					new Double(0.0)));

			smartDB.addDBPersistent( monthPoint );
		}
	
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder1.setTitle("Communication");
			ivjCommunicationPanel = new javax.swing.JPanel();
			ivjCommunicationPanel.setName("CommunicationPanel");
			ivjCommunicationPanel.setBorder(ivjLocalBorder1);
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Identification");
			ivjIdentificationPanel = new javax.swing.JPanel();
			ivjIdentificationPanel.setName("IdentificationPanel");
			ivjIdentificationPanel.setBorder(ivjLocalBorder);
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
			ivjJTextFieldName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
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
		lmGroup.setDisableFlag( com.cannontech.common.util.CtiUtilities.trueChar );
	else
		lmGroup.setDisableFlag( com.cannontech.common.util.CtiUtilities.falseChar );

	if( getJCheckBoxDisableControl().isSelected() )
		lmGroup.getDevice().setControlInhibit( com.cannontech.common.util.CtiUtilities.trueChar );
	else
		lmGroup.getDevice().setControlInhibit( com.cannontech.common.util.CtiUtilities.falseChar );

	if( getJTextFieldKWCapacity().getText() != null
		 && getJTextFieldKWCapacity().getText().length() > 0 )
	{
		lmGroup.getLmGroup().setKwCapacity( new Double(getJTextFieldKWCapacity().getText()) );
	}

	//might have to change the bottom casts!!!!!!!!!
	if( val instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon )
	{
		((com.cannontech.database.data.device.lm.LMGroupEmetcon) val).getLmGroupEmetcon().setRouteID( 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRouteComboBox().getSelectedItem()).getYukonID()) );
	}
	else if( val instanceof com.cannontech.database.data.device.lm.LMGroupVersacom )
	{
		((com.cannontech.database.data.device.lm.LMGroupVersacom) val).getLmGroupVersacom().setRouteID( 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRouteComboBox().getSelectedItem()).getYukonID()) );
	}
	else if(val instanceof com.cannontech.database.data.device.lm.LMGroupRipple)
	{
		((com.cannontech.database.data.device.lm.LMGroupRipple) val).getLmGroupRipple().setRouteID( 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRouteComboBox().getSelectedItem()).getYukonID()) );
	}
	else if(val instanceof com.cannontech.database.data.device.lm.LMGroupExpressComm)
	{
		((com.cannontech.database.data.device.lm.LMGroupExpressComm) val).getLMGroupExpressComm().setRouteID( 
			new Integer(((com.cannontech.database.data.lite.LiteYukonPAObject)getRouteComboBox().getSelectedItem()).getYukonID()) );
	}

	if( val instanceof com.cannontech.database.data.device.lm.MacroGroup )
		return val;  //Macros will not have record history capability
	else
	{
		if( getJCheckBoxHistory().isVisible() )
		{
			//some status points may need to be created for control history
			com.cannontech.database.data.multi.SmartMultiDBPersistent smartDB = new com.cannontech.database.data.multi.SmartMultiDBPersistent();
			smartDB.addDBPersistent( lmGroup );
			smartDB.setOwnerDBPersistent( lmGroup );
			
			createExtraObjects( smartDB );

			return smartDB;
		}
		else
			return lmGroup;
	}
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
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
		LMGroupBasePanel aLMGroupBasePanel;
		aLMGroupBasePanel = new LMGroupBasePanel();
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
		exception.printStackTrace(System.out);
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
		com.cannontech.common.util.CtiUtilities.isTrue(lmGroup.getPAODisableFlag()) );

	getJCheckBoxDisableControl().setSelected(
		com.cannontech.common.util.CtiUtilities.isTrue(lmGroup.getDevice().getControlInhibit()) );

	getJTextFieldKWCapacity().setText( lmGroup.getLmGroup().getKwCapacity().toString() );

	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List routes = cache.getAllRoutes();
		int assignedRouteID = 0;

		if( lmGroup instanceof com.cannontech.database.data.device.lm.LMGroupEmetcon )
			assignedRouteID = ((com.cannontech.database.data.device.lm.LMGroupEmetcon) lmGroup).getLmGroupEmetcon().getRouteID().intValue();
		else if( lmGroup instanceof com.cannontech.database.data.device.lm.LMGroupVersacom )
			assignedRouteID = ((com.cannontech.database.data.device.lm.LMGroupVersacom) lmGroup).getLmGroupVersacom().getRouteID().intValue();
		else if (lmGroup instanceof com.cannontech.database.data.device.lm.LMGroupRipple)
			assignedRouteID = ((com.cannontech.database.data.device.lm.LMGroupRipple)lmGroup).getLmGroupRipple().getRouteID().intValue();
		else if (lmGroup instanceof com.cannontech.database.data.device.lm.LMGroupPoint)
		{
		}
		else if (lmGroup instanceof com.cannontech.database.data.device.lm.MacroGroup )
		{
		}

		for( int i = 0 ; i < routes.size(); i++ )
			if( ((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i)).getYukonID() == assignedRouteID )
				getRouteComboBox().setSelectedItem((com.cannontech.database.data.lite.LiteYukonPAObject)routes.get(i));

		//show the needed entry fields only
		setSwitchType( lmGroup.getPAOType() );
	}

}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA8F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBD8DD8D5D5362456D8E9C3058DE368D0E1D1C305C9E5C5C5E41325EFF2E61849E7B4C3739CE36643496778E62C68F9783B022222A22222DE93C7E898C4C0C5A4159F9515941514DE1ADC398738FA7F3C77G57A2F9EB6FFD56BE675EFBAE88BE676977AD4ED9EB6F753B577EB97B6CF39532B78627A5A5E78BC2D21AA07F5B15A488815D0230AD6C8F171066A7C66B84553FCDG4B05BFE52742559F4A
	2AE223F5D9C24465F440C3GEF7A37E85DEA38DFA9BCF7B9249DEEFC686901325DE3FA1F69DB4F349C543310621F9F4B046BD200D020116BBC11687F72D82ECC1FAA5391121285A1360D54319F4B13E99500B78284820435317A13601AC1B55FDAD9A157BBFBF730B06F4D2D49CB089C15CC047CF646DBAFDF754213C9F35A092E9FD3BB890F9F6093810873A9C2EE539E38D6B5DEEA5F5BEA3394379A2CD61BD592DBBB0A3BCD5F9A9C46E22358E5EA950B4D16F6072D53DED9597045A1F2D3E3F00A0D862BE896
	4C9DC47EFB475229BCC1A887FCBE25F524901D61F0BF99A00E53372592FA15895A8640ECC2BFBF5FDAA57B517C5D13C2445B5614E5BE7C9CE6E27E051994BF83BABFDA380636C9D6AE09FDBC14E5DE0C5669816AG0CG43G5651DCD83D7AB338D6DF10EAED960B4DDAED3757180D0EAFECC6518C771515D0A253CE0A2DB607D190D8FB7F64D81106BDFD8163FBFC40FCACA621E55876E1A79F96E2A79CCDDE28911358501264200BAC9691971518B05D33FC6A0E39681E1B6E3A4F88EA5DA13759D252B4F4C77C
	3AB0B9DE16132852DDF40934F5AC4D01BC38CF12730351E7D13ADDC5E7F9EA25F4027BC1594CCB189B4DCB5116065451C25676864DB884C6D7A7571B198DADE6CFDB5E26F9B7424CF42E62367C095223D4F4E64B8ECAA778BCA88B36C46BD8FE2DD9CB7AED03B5DA578160879088B091A086A05E0AB95632FA5BB566D82341A155F41A4CC651A1B05EB539B9F095DD1243F0586A148C56D6713441E1B2DCB00BCEC12E73E9AE094185B73E2DB477DBC147A151AABA8C1249E68569860BA8098EA72BD3123B1C54F1
	08343449E491C10145AE205C0A5C15F0B59B1C5217F623C192159A2CFCFFAEE913AE43E9108882601E456560BA922FF49B74B900929B466185AA6F286800618286901B6DD2273D09F8A27859C81CBF21E3C79D5C1F35E10CEB7288ED926031DC4E5C5C95AAB9F526D6E23E41F1857B586FD34858BC4706B97367E64C193AA5630422DF9F514C998259F616ABC5F64F3E13C0E5923B98AE25B48F309FEB81315177D8B1727FC4C70A8FBA0BEF78FA52D6CD602F9D405F0EB17E679ABD4BD9B240E3169EF7C00209C6
	A5624C4EF7D38B64F82E87A0E383097BE2000DGE40EDAC66B9139685B9C4A938A7A26814C8750DF0E5655835881828122G6281329C9873BD29412AF6CB9314B1EA31069ECFFF574B76DCEFBC2FED1CEF23362628E8EADB55F62E521011A67BF323EDE5ED3DF9AD31C76C922DD21DE8972DC638F14AE51F2DA5B64B63CF1B0D7314D03A9D462BDA98A01C1C3EF3AD714BA0C98E5305CEC9BCA5CA12495A2E146F253A1C1D976C4AD0073A8E2CA5FE5B3AE13C62750F2CA5FD3D55E6350AB469B80BDC5EC0479A51
	ACDA4068DADB27D5E274136BC8FF3DA4DE11473A536B48A22E4BE06EA463A6DB73049BC8DB589DA68B24B299FA05A0879907320F672AE2D481B432F63343F5188347146E4DB82664FF7B0090F5FECB32561C98B82EAEF926037571798EE5CCE1B69D4A53367D8BBA6649F4E78A259D6EA67300EA51F540E812EC8E3674127B729FB790F9394EE8DD91C01D937B729BF9249C47CB3993575A4CE696D29E6BE756E7113691AFF70AB04D100179A80F5F0B6BD7F8141D16E3494ADFDBCFE2E9D60D69F31CA4161F2561
	5A76AC6092C0001345F31A134503551B4D6BD5C9309E8410A42DFA63F5D8AF021483A4E8561396E23D024EE85DFE0016CE2DFA9569D8AF984AA77A2C17406BCD0772B9GD9DD2CDEE3173ADEF886568B82FA84C0CC1726FD0B30DEF6B734D7372FFA850B31DE8354B17A2C974B6B8D03BAC19A75D84EAFBCCF5600DA6B7C76CED3F127E4A2B32AC1024EEE6F14E8F631F57D5563A4A749B896590D6B77409212EF73G4FF4C56B72DD58B7FE3184576F811702053807567AFCCE29F249EBB41752B774C3F17DDE26
	A7BA0321AC5C05393CFF03FA8DD16DF45ADACDF479A26761879B54FD075166EDD88457B61343A99D06414D05B97B5186E2AFD95F48E4F93DF7AAADD30E57A4501B8890F0055047F5E52BECAF875AD9GBFG56C71FCFA36B3CD603C3140A69DF36ECAA38E868B2380A1D5DB0909653D1363816149EA03705F23BCCAADC41EC94E964E54E42533886FFF065FED96F54A7C68BD53DAB13351E0906AFDB1F9CFB0545B275DBA526BDCF69D2923E79F8A173AFC7077EB5C21D615F22FF66FDEA7F22009E8F10799D7A77
	3D2E942E86BAE89433CB2368E833B9AC2211BAD9EC6816E48F2BE9316C229C5799DC2F9EE4B6FF07FAB3F6E845551F6B2DDEB81838B21DE396224E9010974BF5761629F5E677B01DD5BD28B3B57D7AFCB5247398034C61BD28D7AC55529B85D74637AA8379E2001679BD72AD52F97255C1D94777E8EFCF46E06DE579B77B4CBE38DE2C6D90DBAF5558DC75A6A74CAED74ED7FBC8EA70ADEA8FA4AB73615F398FBDE0DF8477A1A849530784A84B3A0ABE6CDFF4FDB19F33887566034C33D7D1EFCC2A165E002B9873
	930B11AF94E8F11C6F19B4AD3E5CDE64CB4BC43EA22099FB11EF15263E20DEDC478EBD6E799C9E5E2BBC87CC027B693D6E4F856C79B4FABB5117504B1ECF97F27A4FA9BD55CF21B37B4652711960F9D06667074FD4511B487AE6A66042AD608FC089C0A3007D969CE707ECAC937AF9E6BD8C730643EAB01FF2DB4C6133231E34A92C3FE43CCACF62C457DF822E113FCA730C4308DB5C7D8E754099DF3B97DF3187BE55030F65781682B2C75239CFB5F4D4D69EE902756DC113E8B632755F42D2757AEF66AD6CF9B4
	FDC834AE8F004D0731DDE41EA173DA79901C773EBF281E77A21715A6AF569823693AF08833EF6290555E845F37B2EFA6F64E07326C2130B6G2887B0838C87889B0AED74B37D677D35916DA0CD36DA2055435A2C56F39D4DE31A279BDCDBFC6645675596C3355A225D3CCE3BAD40178BB6980164357873E5DAEDB193E44E9F0AED7150BE5296248D3206E19F0B9B24BFFF6E571F7AE16EFC4C067DBBF05F33055B7050424159B05E0B4F530640E1DAB15DFB5AD7CC65D1DD367519B20CD7B83735BDFDF036F6FA71
	F95A9A2F992F20D234C13895EDD817B1B89B8A3D78B6F86095373A73315C9AFBD5AB379A212E71D64C2DEA9D311374639EEE676F86E967ECAFBE4FD8C5E85AF9205953CEB277CF053AF15C4EDFA5211D73391D8FAD9AE47EF971F95AD9F21BD61BAECEC39B6AEFC39B4EAC9E1C8D22971F278DFE1AB64C66B684F29B162F9E1C8D9B3D78BCED1826E9C3C3AA5A908F65ECEFF3B4FDFE527FA0DAD77E832409A7882D99F0BB275DF6125082G8F7B815ABF194E6FD10047818843119EC369E47DC85669A4A798BD24
	046C9B123ACBBD6CC5086A230C4877D5264466C52AA32D0FFA3E769DD1C6DFBA587CB6E5136FFD29DFEE525E171A3EC9FBDF6A371B16B3BA48D06F32E3792CCDFD6FDB7DF913563E55FA6A679BB94B28EC27D3F4528917BCC04BE56FCBE4FF4FEB7F6A1841A24AB1DAA97827E548F58EC8365A6D820E67FFA9A47DDEE3BCD7BD176FAB4567723C6178DC1EC677011AGB7838C637972DED7DF7B49C19E6D9EAE631360BA5D23AC81F061F6F79ADB3B7CA51F2CFD33212C84209E2085406FF6DC338C7B345FF56581
	D79F6B4A2E4D3EF3627B4D5AB9B1F4B3D927B9ED1D0ED651335D6F581C4B4B145551E1C93410A6E2F56E257C124151AEFA654DE84A4F4AFC71BF3459D7EE3176BF25D337FFB99FC76616E15B865F0EED7B74DE523653811F8310FE8736ED68BE9C2B74C0AB3F433DED6A81BF6BC1E33AD7AFD46BAE657D7B400E3E5ED35A6F709EA3183C99EE72F6F0F9AF1429658D837EC000B0D9CE14CFF9E3535572F6F2F98F142A6545GFFBCC022ACA76BCEDF72CEE628651571D87F66EA3A5C6F726F44D85FF5060E2F00CB
	GA36E44D89F24EB4490D947C438CE85081371F9BEF567ACD26B5E45F5FF5F0C6D1CBE82F53FA7905D7B81EF86300FC05D5313086E61A3180EA0380683CC127169A3FC360B1B6E8A9E47AC7AAC15A07389A361198320E0A4432BC67A12F757E2353C5DDC5EDCAA2FD166B3425585B0C24603FD4A2BDF3DC8A5EFFB894ABB11CA64C548FC13619A8BB0C746D36F72A5EF7CF1325FEFB6DC31F5CA10EFF71118CE667B7A390017GB483186FD26F0F0E6FA673F61B43E001AEFA4AAE368AADBFA43CBDA778B98E289F
	87A0GDD8F307F0738FFB166A46185C6132553F24A742DA844D15E27691C9E8A77913C6E0BDF12FD4C43E478B23519DA657733E4B31F15B734127DC0B2C8F6DA5D0A6572839B48DEBD1B892A4DE6F93F06452C4E2DEF647BC76BAA7C15B8B578FB470C6DA30C2273C60BBF5BC7D84E698FD37A88951D6DAFAC26F402CF04323A3BF18D1352CD7C1D86F8BC277D251C5092814F3A87E9D57419A99F707D1C96C81F2B1BGB7F2DA5699DCA739B8EDB62DB7997038FBF0EE79B85F775C103CC5FBEEC85F223DDED83A
	256F75C24E96DFE3BAEB033F390DF773404674FB1538675D6B2B8D8A771118165C4BE21D46696514DE272233B6F8E1AF6EC1B5C3D9643D981F1769B331BEA0DAD79500341E9CC2EB845C48E92F66E0ECA5CE3BE38FFD678EF8AC271D5D017566F05AAF697C34FF94BCB30DC2DAB915E7845C45E95FD35A884003B86D77DCEFA827CD24EB197900275F07B49D2D1787F889279D2734BA401BB8AD024BEB398F474AF72EFA6D335D275AE73B4FD79BA4B753B976BE4FB6D8C2698977F936C14099EC8361C7905B9F21
	CD67683363E440E3B96DE1CA1B8338900834C74F91DAB660851CB617522AGEF6034B35F200F4D01686310E6350F21C00F8CD47C1A96684B47AF13084DB38319AF991CFE0152059FABF466637BF4BEA2F89E14797D98ED7A00BEF74E84FCBE276DCEA6344C51513A3C51C8FB67A82115GDE47E9798D549F40BBB86D76861E0F1C3615BE53CD82FCBA27DD2634844093C73B47AC6B7EE8DD7E7DC85BC3E3D681F8BD278573B8B6F1DA86ED179040A7F25AA9CA1B8AF8AC27551D276D87F8EA9052C6D2DAAE60851C
	B677BC6AA867348E7ADCB88C70C0CEFB0A5242G0F6434AD540FE9004F64B4AB254D87BCFD8C52FEF58EF5E40F41BD69B94771FC1F91E8C1E370FC5F7696373DCF3213DED9F948E1B256985A9B68AC4956A2950D69B2FF4C98DC0B34B5921919E3A12EGD5E3F13F5375F75C6F8CFBF8345060F7D473BDDFDCD3E9F253D816072E314AFEE7E867C78B194E87CF204E88A8CF98CBE853CE287750491CD71D421E3FD9F972C9F5B9990B8B85F5F939DBB9315760465F66D61E2662E73ECD59023EA5CE89954CCF6F76
	798E337CA74C27E6795AF8FA3310FF47478B7DFFC24E0D86126EA94F595F156861EA953B757CE5C61D49A2DA496129029DA53B0B74653BCA8B197E60EE75D94F0AC53BB44F9750352F2CB7DE3EC67EE033FC1E6AEBBA1E6A03A18703B1E77655521C815C4CE9AF55E19E79876338F27F86E2EB9C6009G398F008C00C6GBB408800508730EE982DBB957084004C8759FBFE72EE3E68C176CE1D3CFF1777E2B678E978C074287144E06FF14BAE6BA4F6F81651B51D873DD136D32BAE3325DC53964FFD8B2DBD0EA5
	F24EE5E81421FCB50EE7FE346A325C78A4052E9D8DAE0F35E391DDCBB5C09C1B9F443563229DC4FE2B4D5CE931BA65FA1B687CCC1E41C770FACB7790BB6D8E31CDF4B8C4A315AA4875C5DABF886A06727A915D644CCC1B4DAA31BA2F50B5D68C14478154BFC44762B3F5BE7CC0DF54108DBC85G1B65B8D6417D53F29EB31DD15D5AEB669C6A37BF548FFA8873F4E9B96673E4CEF325E0BEA7F05A5CD44C672C904C3BEDF4BD5482F88F27D5D29AB9EB9240E92F6C45BD922C6B4B5741G53B1036BC87111DF5783
	AC2E3F59673B6DBF20FEC700EFD1A1587677661067258BB687DD644E8FA1759E23E75C93C2306DE764745576E99EEDDFB58E460BF1CA5B9B617EEF8B96ACA040F4BE30C73B6D1F236BCFBB546F99073A1DBB7A4BBB357E50F16EF99789785DEEF97747B277DCDFC057321321DE4CB80C49D13A2634D8C50B8D1E41D83D8B1476BAC06A43D8AF0046C902078BB666EFA55308406C7E304F18617BDA343BC055DFC8D9B66800F0ADD06CFE2B545D6EBF30B19E6A95F1FB56EB584D5665EFG1C65755ABC6CBE32CD31
	FB4AF559ED869D786ED36E13F4FF458574918FE3BFBDAFE0BF8DFF987BDFBA5D8B919E013563A3C8DBC9E9649D7A9CBE0F0CBEF33DE7E3098EAD3A22F742F56B7035870E393AD210689A53A91233F8221135551DBAFC17D871885FAF5039377B2FE81DFAA8EF62F5224EF9377B5B1476BE40B0DEAF639B77F67F36C4E9F76BB783995F83C0A681EC77D0385F62566EBF26ED978174490FE03B5794507684DC88C55A1305188B7921987F2F13489E078438BFC09840D4DE56C14B48793AA0797C9CB99BA73FE79354
	6DC678865BFE289BF1C53FAEC92D233F77997D6508FA8E48603921CB764E0D754938D60E8F4576ECCCF64F0DB6DAA7914A539F45BAF30FFA67C6C6830E89953C5E1E86775C9833DD490D1FB6DCCFEE54038EF2DE61AFFC7C5F1B666D5F17E968DF9337E9F81A3BFFE3E91D8EA8179ED5F25A5BBFBD25CD81886175CEFA64FEDA19625F33FD663E677C6669DF44236CD9CB71CF2F5137F769503FA8EE53C50F3E6DC76B44C0F99C2F7375F9EF7F9C1476AAC056E3D8AF643C3BFF85650AFF99AD7D79175607FF7A47
	583D62DFEF2A37FF772522FFC55C26B7D25D7D7B9D2D339F4A9BF81DAA0D316B94253D8350436B5DFA4E5D3F169D0AFFFFBAB710B50227FFA39EE3672308FF2C0F7F556B1CD6FF4078D29B874777790079581ECD458972FC9E78985B33C96274EACA8FD151599E01399177884221AC7B27F8AE2CF6A75963F6819EG9086B089E0A6009006FB2CE1DB48E451677B3BA61B3DC16C924D2AEDD3665FA3A786776D5BAB8B71D9BC627987050937FD18646BDCD5F6989B2B4B65EBFC53E6CAE77E7DE697714F88E5FEG
	A1G11G53G6239FF733694B17F48797AEAC9B234F6501344685CE913582D10498B82E663DBDF2B1F5733C6EFCACE72E1E322ECDB7A634ADE8833EF6FEE3A6787746647511EF70B7E815753060BF2245931FFEA8D909B9DB63B68102EB0AABD11AF4F9D0ED275FA1E3D7F381D1FEBF301FCFF0049G310F33B3280C6FF42D77FB93CBAD724DFB1C3DB749FCA2DA27FF0271313E776B3A014C8D95405B70846BFB744C945C4F06FE270CADBFF791BE65E4DE53953B0873E2F79EF916BE52E038A01A697790341015
	2D586BD1C65F34333296233BCC7692DEFE767DA25B2D6C6819DA035D50EA122E307289D5D9DC2EE7D9F5AEAD43D3D6F5A6A779G02157D35F135C6D9AD0C058E1BD9E067C5E7EED06FC51965387ABF0167C9B727E8150770725F6D50AA1F484BD33A354A2742150D2193AA858163B02B7C6F024FBD2A526D653B4AAB4AF6166FAA52171694A2CF450EE438EEAF0567F5AAAFE10FA2EFE457C65F72361715B2C10C6F437D8ADF4A19AD1EFC87A1E8851FBA8DD627D3F4185ABEAD52179469371713FD3B0D827B160A
	BEBB50FFF376F953503E3C9A010FAD6FE562581277537B053A37BFCC427940B307631EE07D3564C9255F46DFCC147779EE2BD5ECCFA83F961FD95F5AD50EFD2B63C956A77D609A70A4BB53436ABC1002F5421EE47DEF92DC274BF56439BDEDA0F35F9C60CD8DD77ADFEEB86BFF9F707E7756A632FFF092C681111F48C47F2E9896E1996D8F021C47DF77F8669931AFBF9C736C0903CA0CAE67749123526D503225BB8AF10FA8C7E92F4FC575B8DB72DDD2F20A0F762A88173F018FD776C58716A7CD754AFD77E145
	763DBD57E23B16AEA6EB7491C56EDF9B943985B91FEA673C51E4B05B5AD589AF37C38E39EEED5DAA4BDAF3CC115B5928487DE85540649EE5F20DA8B77004A2AB2357FBFE507772F9A51C6527BFDCC3B07EB4BF3B047E72D33D1F90893C53D479B9B3DCD943B0BBFEFB1564A7FB7FAD3F7C66E5D34A5C46E3391815BDC8575A1223D3E43E35B5A93E0DB9B730B87D799C0D13F32BC6DBE3BB5C7E25A2BF3C59FBCEFE1D1F6D1C83BE260F0F56D581B40EE75F8CB03E2F8A3CE3EEAF606346F896F3BF38860CD7477C
	E7058399934200B7EA3C927398387F9046038F7918704919256A385699CC66AB685B1AB63718FF81F16BB04BB1FFCFDC2EAEBBA59A1CB62B414C6372F466B2F5F935555AE940F39065743DC71B416C94F9CCAFB6ABB1FD6B68405ACC5F601E5BA33FD2E419533CF57D71EB25FC56G73E357B7D417083AB24FA9320E693CF5D9DA15720A738353F57BF92A4B003A6EB7AA329ECF7556B524DDA9DFB3C03FAE323C37202EF19D02004FB0FBCFA87707D4770E467F0BEF597BFEFE3A73066A103FB75E697B0C49291D
	5AE7CC3E5A29FD46647C4E3E4F1C366F747D2D34F5277ADBE9767C50E9A273EF9C745BC4G7DD3513AF2007A2770D9A2F1A7E9E772EB935E4FB5870C6DE4D5CAFEE1C2C27D3F5B655B57D93B7A7FDD0A8FF611F542851BA459ACC4BE527FEB97E9AF49E627C479DB4CDDA4C68ED3FB07442A4AF421020C29E6310D12191F77DC94F839FF85D9A7EABE9F01D7C7A01F0FDF38A836CACD06F64ED33BFB29BA6E6C31893FB75FCDE2E8321AA448FD5337746055A928E8EDE74F4E63CB71F7C90C90572027707D7A7B6B
	49BA346F6FEA1BCC12D92C1107FF796C5F0725F86EB066A9FC5FBE22063EEF1FGEBA7002A8978EEFFD4ADBE37B781ED4484BC7B7FEEAD6E39CC1A00B29657113A0900E7C7C46B0AG2EED6D03EB143974F71664F348EDBE4EA153559F336196BDFE4FD19701F67D8FFDB7C766699ECE4B590934B8CE2B6CC6DA5D53389791E7A6F3D7A86013G628104E7409700A2007DGCDG9DGFEG01GE1GD1GB1GB3816683E4BE0B39EF28A97415A7C0148FC2508E60B018C8971077CA4E17E3FB6BC1D67333E873A3
	6558EE7E4FE24CDFCBA6F5A7839EC7E84FE23B3D11026D16B5917A63C414311C6F1579CDC4997B68EF4DCC84FC9AC07C440136DB24D56039F5CF8F1EAB951E43381AECE47DD3719C4665BF8FFE5AC7DC48F7E3AC7BB19E753D68CB834898769C7A625F0B3EC4BE073E3CC46B4E83BC73F9486567876A4B8C3B405B6103E6EC07AA10A3BD0F7DAECD3FC5A3B70B59A22B327200545AA19ABD7A1CE1AB5A9A76BC5A7A432D74BC946009G59AFE05B8596203FC5C0B33E007E4695200C2097D04652C2D2B786708400
	5448017A7B49E50249E759CD3E475EE1267E475E11A6A2DF04B113EC0EB05ABDA6320E343535B9C5BE467F481445E9071D0DB627090CF8ECBEFD36343F7D3A3CC82D6FE6DE6B174F737BAC46775F142FA2725A5E57B4C0BD364FA950F131A552F37FB303524D111E7B1F7754E01B3A20ACB6121FF9299D5C3E6D2303647B7D007918DF3DF4EC5F7F22273F43A93D61C5CFFF1757213F4DD09679A27AFB1B1EFFB370922C95DE121F9D646B59173C5B01711D577CDDA23BDCFF18869F01C02007F914C5819E7312D6
	8E2D5B2925A3DE2E1B68C3C7564BB04E3C6CDED681F8434BDABA76F5EB69E8116BCAAFEB6B988E74A00F32F040A7F950647DE4336FFE7C31397FFE1CEA51DEC3AD32F82F2116D93457D02BAC6AB594232D351071339D663DE6DEEFA327FD45E979D4A6211DB199258ECE7F0C5AC56875A2D148FF97085A55CDE94DB2ED070A7615CC2B34103188DC6E30B910F71F053EA7B2194D72BA0D56B1D9E1E862712827B61A6C86155DDFF05AD73CED4572410DDD4EFE7934472EB04AB75DC74EF8C2424BB8F66D26EB83A1
	4A33AF57D0FAE614E7DF1E120CFDB9974AFA2230AF3F15B238B1685DC17265248EEE6C1AC057B7F1DE7EC6D2FA02173F87D2515F79D0F676957477C7BDEA7DD12F303847485778D73C5B01650A45C6F24AA92FE9182CE33D030B4159C1720D9AB09F0BD1A9BD031D780AE76C76D0FAFA34E76CA67135DAB6143922B1F69B378E2EEF7C33DFBE6D3E5156AF1F363FA9F46D959B6D69EFA62547FB79BBEAAB7ABB8F4A1ADFC5FF47968C2E1D42864977078173B13F065075E3642B1E7E5EC96913DF7574B73B907D0D
	01320AC951BA764DE954CA72ACE5321ACDD631167D82240067F1D651F3EEC1D0B7E2921E3D492E21E75FG0F1D444F375077996CFE196ABE3066466F630C7F97683835CE7169AE557D6A3A9B2F3B64066AE0EDBA06BE0F47C3FBA672F61EC5E9D92FC56B742FA1AD14BE471683DE47E9CB7837BE0D1CF68BFD168F86BC1C53E6D19A794EAB12537ED6C9E8641B2EE91CB606BE5F13EF3AE6F21A2E99F54C6734C7E9BD72CDD77D4F107679CE54FB1653466EC43D9D1C165809FAFBB86DD2B96A756734213928B704
	5352F7225EF91CE62036243F9E2D4BFD9DE9BF2334FC402BB8EDB50DD5A360E6CE1B6BA4B4BF407DB96D61D3DCAF27DD1288ED9E6019137977D86C0CB360951C76BAFD0EEFG3CE5B2764FFDA9CA4E562B6EFF11FC637377CF7F829D4F25AABEC52B6E0F265EF85D469B2A0335699334CD4D501EBD3C1D638F51B36F0087F35A977518B7E13C6D499BD03435DAF57FE66F0D0F4DFF7C8BF4E494A8BE2DD05D5F3F75466B0E78976898DE2878F4376AFEED610D57DDF6C3F53077C3C5E56419D1A23B5D64BD12005F
	9D545135C7A264F17A9B78EE739FB5E9D8D740F7D3FA728A844F6568691E155DF0196C51B3B9D374F8BE3A0A4B79C46FFE46F065A6BAE6C2F9B32F73F2397B5941DAE21A7C2EB43F5F6F8B705DB3814F736E43C1472AA475595D4094F7DDED1D820F43AA3A6F9BGBC41G0C3615FE73B09570691C76A61DEFD25F047BB7711C477A667E4E6577E567FE1033D53EE7BA26336F0B005E7CA61E53383DCB3175F5BAB78EA3E5EF22DDDB68F7C11100CF6634GBAEF4D83DC1802360E4E6D4F5614BEECAD194276CFB3
	38AD5B29ADD5C47E94ECD3D737FB1C433A155CF937DB7B1B2A7F22BECBA0430F4BA95E617BFB19C3F40E8F033A93F97D8C49BD5FB65239FCA6144F617E3FF82ABF7F537A70DFBFD5711F699CA2E9FFAFB31A6AAE027A95D3513E99655AFDF0AEEDA7A35433733A3F6D716E03B3DCCA9CDF6BF177759DBAF705GFFB817316010FBBBFCF2C569DB467A011C8DECD4FDB7C473F42A725C43E40BBD0AED8BEBC8BE7BFCF7DDFC41E6B6FEDA245F4EDEE0B31E5F6B5D4F007C612AA2AF28FC13363CEA8788D4A421ADEC
	971F5D7FA3C91113D03941D30EF2C62822CCBEBF47780EA5ABFC77F4DFBB5F53298A5FC7E72E759DBE631D1E26587D31CE11933D635A75531F5A15797E3333AF3EDD20FBA767BB38C861EBA93FF63E6F16A8FC5D398373F76CD2455FD716A9F206F7DD3B7E19D98ADFE61FF1F267DB38C261FB5FF96DFC56EC05EF74290179FB77AA455F089CC5CEFD69356B4FDA2370BD5FB310FEA61F53592470479E9A187D01243A407E3F1E394BE6C06B054D96521C8EE105375D55F77D7B5BE1676B3213056443022E036EB3
	9F96523A4906354B8EA347F400CFA4E119FC7AD6AF9F3F1504A5C821C705A5A1731421CB544B8746A4E191C569B9A728CB91BC3CA4890BA94E8EABA127FC6C49F9D920C30A105D3BE0418285839886AEACF18B41EB8F6C18B57537271DA9026E3030060C3227C487BDFCA09A498F09497F3B46C1A143EEB3C1A1508F8B29CEFAC9F3CAD74CE203105DF6127CB23BD3920D55F82C41A9892B684F75EEA3278F36559B1C9D9271F5CF0310EAA57F8F083002951B8D5676ED6CAD6F9E92AEA1293A055ACB3B8C583B12
	5ACB2494F3A9ABDFD836E553111993D304640342A2334DE0BCE8E805289D06193143A1BABBA09FDC76FD7B2920F4BA611249AB85442D636E93E70B4911EBEE74A5571891CB62179C049151659036511AE4D2A966B50C359D868728929D07A5727F1A589C793C6C168D525F7F6D506A3349C2126CA4897D9297342C2E4DE0214D18418F9053F6D98E944981A472B343E01C5E43B8AC62BA0A6FFD23E454239F7D8F552130CA42C696CDE05D56AE5AAC5B2A5B5ACCD6118B0050027875DCBC29D36CD1E7056B6ED5C7
	FE3D608224C21D10E2A9A921FF37533F25646FF6CA59CEA95BCBA13EF304BE7FF9163B50E71ABFE04FB2EA8F490062035008520C59EFBC78CE551C84706E2010C6FF2E975AA1CB29F52C53F2C1F45C96B6EA7D620038BBC9B359C93638085CD5AEAE11F8F8183E38096BAA2BCC790F07D21205D4681C6CC59664AFF99B85796BE0FDB5C53259E96A11990D4C22FB493DC747C10C0CCA3F249899A34858C666AF729DD71A0FBA493254E93C4EE22FB4A754E55E79CA48ABA1FD6A68B0C5309CDAB5177EDDB76086A0
	DC6BA96FC69E37FF38382544967CE1773C1B4F52D2455282152549B71F253B95CB0905E4A2A1BFEE17F273D93A5F5DD2221764FB464DE769A1F7CBB3EFDECB0FA916AED1D9FA936669176E16E26BA7F3AA5936A1639B999917F2EA0A0A1A4E29EBD454D4CEDDADA8791FA489CB5DFFF48E663F02AF0F9DBDF67C4C31C2D8545005101E1C992A649879CE8A304CCBE77478619320CB5CFE1CCE92325DF0BD7DBC24D2B360C9BE821E6A9EF01C2BF46C64G8B9C6139B1AD516E3A9EE38956C17EDC3CB15659DDEEED
	0FF324B263112CB925018DB85B0DB7B05B2F38FB31CC3654179764F96B0C86F6D379B4D471A9D565D3EA3FDE5C44BE0DD4FCCA96345A49375DB7C9FF99255D1B7FDF477A7B15282E16751251BA8D3FEDAB6CD5FF5BB67F56DC1F3F7965974B4EE78444FAFEC37C5EAA7AEE94688931F846F77B2AFF8A3E4F481F32984CE649E03ED4EDB5127372B5D7A4D17986E156AAA25F09F564C2D6B6FB95317B220A67FF81D0CB878864887585E7A4GG3CF7GGD0CB818294G94G88G88GA8F954AC64887585E7A4G
	G3CF7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG21A4GGGG
**end of data**/
}
}

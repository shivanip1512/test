package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.device.lm.IGroupRoute;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.LMGroupExpressCom;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointOffsets;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.point.UnitOfMeasure;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

public class LMGroupBaseWizardPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjIdentificationPanel = null;
	private javax.swing.JComboBox<LiteYukonPAObject> ivjRouteComboBox = null;
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
    private JLabel jLabelErrorMessage = null;	
	private JLabel priorityLabel = null;
	private JComboBox<Object> priorityCombo = null;
	
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
@Override
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
	if (e.getSource() == getPriorityCombo()) {
		this.fireInputUpdate();
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
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
private void createExtraObjects( LMGroup lmGroup, SmartMultiDBPersistent smartDB )
{
	if( smartDB != null )
	{
		Integer paoID = lmGroup.getPAObjectID();
		
		//create and add the points here
		com.cannontech.database.data.point.PointBase historyPoint =
			PointFactory.createPoint(PointType.Status.getPointTypeId());

		int[] ids = YukonSpringHook.getBean(PointDao.class).getNextPointIds(6);
		
		//set default for point tables
		historyPoint = PointFactory.createNewPoint(
				new Integer(ids[0]),
				PointType.Status.getPointTypeId(),
				"CONTROL STATUS",
				paoID,
				new Integer(0) );

		historyPoint.getPoint().setStateGroupID( 
				new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS) );
		
		((com.cannontech.database.data.point.StatusPoint) historyPoint).getPointStatusControl().setControlOffset(
				new Integer(1) );

		((com.cannontech.database.data.point.StatusPoint) historyPoint).getPointStatusControl().setControlType(
				StatusControlType.NORMAL.getControlName());


		smartDB.addDBPersistent(historyPoint);

		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"ANNUAL HISTORY",
				paoID,
				new Integer(ids[1]),
				PointOffsets.PT_OFFSET_ANNUAL_HISTORY,
				UnitOfMeasure.COUNTS.getId(), 
				StateGroupUtils.STATEGROUP_ANALOG) );			
		
		smartDB.addDBPersistent( 
				PointFactory.createAnalogPoint(
					"DAILY HISTORY",
					paoID,
					new Integer(ids[2]),
					PointOffsets.PT_OFFSET_DAILY_HISTORY,
					UnitOfMeasure.COUNTS.getId(),
					StateGroupUtils.STATEGROUP_ANALOG) );			

		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"SEASON HISTORY",
				paoID,
				new Integer(ids[3]),
				PointOffsets.PT_OFFSET_SEASONAL_HISTORY,
				UnitOfMeasure.COUNTS.getId(),
				StateGroupUtils.STATEGROUP_ANALOG) );			
		
		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"MONTH HISTORY",
				paoID,
				new Integer(ids[4]),
				PointOffsets.PT_OFFSET_MONTHLY_HISTORY,
				UnitOfMeasure.COUNTS.getId(),
				StateGroupUtils.STATEGROUP_ANALOG) );
				
		smartDB.addDBPersistent( 
			PointFactory.createAnalogPoint(
				"CONTROL COUNTDOWN",
				paoID,
				new Integer(ids[5]),
				PointOffsets.PT_OFFSET_CONTROL_COUNTDOWN,
				UnitOfMeasure.COUNTS.getId(), 
				StateGroupUtils.STATEGROUP_ANALOG) );				
	
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
			
			// Add priority components
			GridBagConstraints priorityComboContraints = new GridBagConstraints();
			priorityComboContraints.gridx = 1; 
			priorityComboContraints.gridy = 3;
			priorityComboContraints.fill = GridBagConstraints.HORIZONTAL;
			priorityComboContraints.anchor = GridBagConstraints.WEST;
			priorityComboContraints.weightx = 1.0;
			priorityComboContraints.ipadx = 72;
			priorityComboContraints.insets = new Insets(5, 1, 2, 51);
			ivjIdentificationPanel.add(getPriorityCombo(), priorityComboContraints);
			
			GridBagConstraints priorityLabelContraints = new GridBagConstraints();
			priorityLabelContraints.gridx = 0; 
			priorityLabelContraints.gridy = 3;
			priorityLabelContraints.anchor = GridBagConstraints.WEST;
			priorityLabelContraints.ipadx = 15;
			priorityLabelContraints.ipady = 4;
			priorityLabelContraints.insets = new Insets(5, 15, 2, 20);
			ivjIdentificationPanel.add(getPriorityLabel(), priorityLabelContraints);
			// End priority components

			java.awt.GridBagConstraints constraintsJCheckBoxDisable = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDisable.gridx = 0; 
			constraintsJCheckBoxDisable.gridy = 4;
			constraintsJCheckBoxDisable.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxDisable.ipadx = 5;
			constraintsJCheckBoxDisable.insets = new java.awt.Insets(2, 15, 40, 1);
			getIdentificationPanel().add(getJCheckBoxDisable(), constraintsJCheckBoxDisable);

			java.awt.GridBagConstraints constraintsJCheckBoxDisableControl = new java.awt.GridBagConstraints();
			constraintsJCheckBoxDisableControl.gridx = 0; 
			constraintsJCheckBoxDisableControl.gridy = 4;
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

	private JComboBox<Object> getPriorityCombo() {
		if(priorityCombo == null) {
			priorityCombo = new JComboBox<Object>(new Object[]{"Default", "Medium", "High", "Highest"});
			Dimension priorityDimension = new Dimension(210, 25);
			priorityCombo.setPreferredSize(priorityDimension);
			priorityCombo.setMinimumSize(priorityDimension);
		}
		
		return priorityCombo;
	}
	
	private JLabel getPriorityLabel() {
		if(priorityLabel == null) {
			priorityLabel = new JLabel("Control Priority");
			priorityLabel.setFont(new Font("dialog", 0, 14));
		}
		
		return priorityLabel;
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
            SwingUtilities.invokeLater( new Runnable() 
            { 
                @Override
                public void run() 
                { 
                    ivjJTextFieldKWCapacity.setText(String.valueOf(0.0));
                } 
            });
            
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
						PaoUtils.ILLEGAL_NAME_CHARS) );

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
private javax.swing.JComboBox<LiteYukonPAObject> getRouteComboBox() {
	if (ivjRouteComboBox == null) {
		try {
			ivjRouteComboBox = new javax.swing.JComboBox<LiteYukonPAObject>();
			ivjRouteComboBox.setName("RouteComboBox");
			ivjRouteComboBox.setPreferredSize(new java.awt.Dimension(210, 25));
			ivjRouteComboBox.setMinimumSize(new java.awt.Dimension(210, 25));
			// user code begin {1}


			IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized(cache)
			{
				List<LiteYukonPAObject> routes = cache.getAllRoutes();

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

private JLabel getJLabelErrorMessage()
{
   if( jLabelErrorMessage == null )
   {
       jLabelErrorMessage = new javax.swing.JLabel();
       jLabelErrorMessage.setFont(new java.awt.Font("dialog.bold", 1, 10));
       jLabelErrorMessage.setMaximumSize(new java.awt.Dimension(250, 60));
       jLabelErrorMessage.setPreferredSize(new java.awt.Dimension(220, 60));
       jLabelErrorMessage.setMinimumSize(new java.awt.Dimension(220, 60));            
   }
   
   return jLabelErrorMessage;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
@Override
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
	
	//only set the priority for certain LmGroups
	if( lmGroup instanceof LMGroupExpressCom ) {
		((LMGroupExpressCom) val).getLMGroupExpressComm().setProtocolPriority(getSelectedPriority());
	}

	if( val instanceof MacroGroup )
			return val;  //Macros will not have record history capability
	
	else
	{
	
		//some status points are needed for control history
		com.cannontech.database.data.multi.SmartMultiDBPersistent smartDB = new com.cannontech.database.data.multi.SmartMultiDBPersistent();
		
        int groupId = YukonSpringHook.getBean(PaoDao.class).getNextPaoId();
        lmGroup.setDeviceID(groupId);
        
        smartDB.addDBPersistent( lmGroup );
		smartDB.setOwnerDBPersistent( lmGroup );
        
		createExtraObjects( lmGroup, smartDB );

		return smartDB;
	}
	
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
	getPriorityCombo().addActionListener(this);
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
		
		GridBagConstraints errorLabelConstraints = new GridBagConstraints();
		errorLabelConstraints.gridx = 1;
		errorLabelConstraints.gridy = 4;
		errorLabelConstraints.fill = GridBagConstraints.BOTH;
		errorLabelConstraints.anchor = GridBagConstraints.WEST;
		errorLabelConstraints.weightx = 1.0;
		errorLabelConstraints.weighty = 1.0;
		errorLabelConstraints.insets = new Insets(0, 6, 0, 0);
		add(getJLabelErrorMessage(), errorLabelConstraints);
		
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * This method must be implemented if a notion of data validity needs to be supported.
 * @return boolean
 */
@Override
public boolean isInputValid() 
{
	boolean isValid;
	String newName = getJTextFieldName().getText();
    
	// just pick a PaoType that is a LM Group; only PaoClass and PaoCategory are used to check uniqueness
    if(isUniquePao(newName, PaoType.LM_GROUP_EXPRESSCOMM)) {
        setErrorString("");
        isValid = true;
    } else {
        setErrorString("(The name \'" + newName + "\' is already in use.)");
        isValid = false;
    }
	
	if( getJTextFieldKWCapacity().isVisible()
		 && (getJTextFieldKWCapacity().getText() == null
		 	 || getJTextFieldKWCapacity().getText().length() <= 0) )
	{
		setErrorString("(The kW Capacity text field must be filled in.)");
		isValid = false;
	}
	
	if( newName == null || newName.length() <= 0 ) {
		setErrorString("(The Group Name text field must be filled in.)");
		isValid = false;
	}
	
	getJLabelErrorMessage().setText(getErrorString());
	return isValid;
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
			@Override
            public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
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
public void setSwitchType(PaoType deviceType) 
{
	getJTextFieldType().setText(deviceType.getPaoTypeName());

	//do not show the route panel if the type is one of the following

	getCommunicationPanel().setVisible( 
		!(deviceType == PaoType.MACRO_GROUP ||
	      deviceType == PaoType.LM_GROUP_POINT ||
	      deviceType == PaoType.LM_GROUP_DIGI_SEP ||
	      deviceType == PaoType.LM_GROUP_ECOBEE ||
	      deviceType == PaoType.LM_GROUP_HONEYWELL ||
          deviceType == PaoType.LM_GROUP_NEST ||
	      deviceType == PaoType.LM_GROUP_RFN_EXPRESSCOMM) );
	

	//dont show the following options if this group is a MACRO
	getJLabelKWCapacity().setVisible( 
		!(deviceType == PaoType.MACRO_GROUP) );
	getJTextFieldKWCapacity().setVisible( 
		!(deviceType == PaoType.MACRO_GROUP) );

	getJCheckBoxDisable().setVisible( 
		!(deviceType == PaoType.MACRO_GROUP) );
	getJCheckBoxDisableControl().setVisible( 
		!(deviceType == PaoType.MACRO_GROUP) );


	if(deviceType != PaoType.LM_GROUP_EXPRESSCOMM  &&
	   deviceType != PaoType.LM_GROUP_RFN_EXPRESSCOMM ) {
		getPriorityCombo().setVisible(false);
		getPriorityLabel().setVisible(false);
	}

}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val)  
{
	LMGroup lmGroup = (LMGroup)val;

	String name = lmGroup.getPAOName();
	String type = lmGroup.getPaoType().getPaoTypeName();

	getJTextFieldName().setText(name);
	getJTextFieldType().setText(type);


	getJCheckBoxDisable().setSelected(
		CtiUtilities.isTrue(lmGroup.getPAODisableFlag()) );

	getJCheckBoxDisableControl().setSelected(
		CtiUtilities.isTrue(lmGroup.getDevice().getControlInhibit()) );

	getJTextFieldKWCapacity().setText( lmGroup.getLmGroup().getKwCapacity().toString() );

	
	if( lmGroup instanceof IGroupRoute )
	{
		IDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			List<LiteYukonPAObject> routes = cache.getAllRoutes();
			int assignedRouteID = ((IGroupRoute)lmGroup).getRouteID().intValue();

			for( int i = 0 ; i < routes.size(); i++ )
				if( routes.get(i).getYukonID() == assignedRouteID )
					getRouteComboBox().setSelectedItem(routes.get(i));
      }
	}
	
	//only set the priority for certain LmGroups
	if( lmGroup instanceof LMGroupExpressCom ) {
		int priority = ((LMGroupExpressCom) lmGroup).getLMGroupExpressComm().getProtocolPriority();
		setSelectedPriority(priority);
	}
	
	//show the needed entry fields only
	setSwitchType(lmGroup.getPaoType());	
}

@Override
public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        @Override
        public void run() 
            { 
            getJTextFieldName().requestFocus(); 
        } 
    });    
}

private int getSelectedPriority() {
	String priorityString = (String) priorityCombo.getSelectedItem();
	
	if("medium".equalsIgnoreCase(priorityString)) {
		return 2;
	} else if("high".equalsIgnoreCase(priorityString)) {
		return 1;
	} else if("highest".equalsIgnoreCase(priorityString)) {
		return 0;
	} else {
		return 3;
	}
	
}

private void setSelectedPriority(int priority) {
	
	switch (priority) {
	case 0:
		getPriorityCombo().setSelectedIndex(3);
		break;
	case 1:
		getPriorityCombo().setSelectedIndex(2);
		break;
	case 2:
		getPriorityCombo().setSelectedIndex(1);
		break;
	default:
		getPriorityCombo().setSelectedIndex(0);
		break;
	}
	
}

}

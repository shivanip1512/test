package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.JLabel;
import javax.swing.event.CaretListener;

import org.springframework.dao.DataAccessException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.point.alarm.dao.PointPropertyDao;
import com.cannontech.common.point.alarm.model.PointProperty;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.spring.YukonSpringHook;

public class StatusPointLimitEditorPanel extends DataInputPanel implements com.klg.jclass.util.value.JCValueListener, ActionListener, ItemListener, CaretListener {

	private javax.swing.JPanel ivjJPanelStaleData = null;
	private javax.swing.JTextField ivjJTextFieldTimeToStale = null;
	private javax.swing.JComboBox comboUpdateType = null;
	private javax.swing.JCheckBox ivjJCheckBoxEnableStaleData = null;

public StatusPointLimitEditorPanel() {
	super();
	initialize();
}

public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getJCheckBoxEnableStaleData()) 
		connEtoC5(e);
}

public void caretUpdate(javax.swing.event.CaretEvent e) {
	if (e.getSource() == getJTextFieldTimeToStale()) 
		connEtoC1(e);
}

private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		this.jCheckBoxReasonHigh_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

private javax.swing.JCheckBox getJCheckBoxEnableStaleData() {
	if (ivjJCheckBoxEnableStaleData == null) {
		try {
			ivjJCheckBoxEnableStaleData = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableStaleData.setName("JCheckBoxEnableStaleData");
			ivjJCheckBoxEnableStaleData.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxEnableStaleData.setText("Enable");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableStaleData;
}

public javax.swing.JPanel getJPanelStaleData() {
	if (ivjJPanelStaleData == null) {
		try {
			ivjJPanelStaleData = new javax.swing.JPanel();
			ivjJPanelStaleData.setName("JPanelStaleData");
			ivjJPanelStaleData.setLayout(new java.awt.GridBagLayout());

			javax.swing.JLabel timeLabel = new JLabel();
			javax.swing.JLabel updateLabel = new JLabel();
			
			timeLabel.setText("Time in Minutes");
			updateLabel.setText("Update Style");
			
			java.awt.GridBagConstraints TimeToStaleText = new java.awt.GridBagConstraints();
			TimeToStaleText.gridx = 2; TimeToStaleText.gridy = 1;
			TimeToStaleText.fill = java.awt.GridBagConstraints.NONE;
			TimeToStaleText.anchor = java.awt.GridBagConstraints.EAST;
			TimeToStaleText.ipadx = 25;
			getJPanelStaleData().add(timeLabel,TimeToStaleText);
			
			java.awt.GridBagConstraints UpdateTypeText = new java.awt.GridBagConstraints();
			UpdateTypeText.gridx = 2; UpdateTypeText.gridy = 2;
			UpdateTypeText.fill = java.awt.GridBagConstraints.NONE;
			UpdateTypeText.anchor = java.awt.GridBagConstraints.EAST;
			UpdateTypeText.ipadx = 25;
			getJPanelStaleData().add(updateLabel,UpdateTypeText);
			
			java.awt.GridBagConstraints constraintsJTextFieldTimeToStaleDate = new java.awt.GridBagConstraints();
			constraintsJTextFieldTimeToStaleDate.gridx = 3; constraintsJTextFieldTimeToStaleDate.gridy = 1;
			constraintsJTextFieldTimeToStaleDate.fill = java.awt.GridBagConstraints.NONE;
			constraintsJTextFieldTimeToStaleDate.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldTimeToStaleDate.ipadx = 10;
			getJPanelStaleData().add(getJTextFieldTimeToStale(), constraintsJTextFieldTimeToStaleDate);

			java.awt.GridBagConstraints constraintsJComboBoxUpdateType = new java.awt.GridBagConstraints();
			constraintsJComboBoxUpdateType.gridx = 3; constraintsJComboBoxUpdateType.gridy = 2;
			constraintsJComboBoxUpdateType.fill = java.awt.GridBagConstraints.NONE;
			constraintsJComboBoxUpdateType.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJComboBoxUpdateType.ipadx = 10;
			getJPanelStaleData().add(getJComboUpdateType(), constraintsJComboBoxUpdateType);			
			
			java.awt.GridBagConstraints constraintsJCheckBoxEnableStaleData = new java.awt.GridBagConstraints();
			constraintsJCheckBoxEnableStaleData.gridx = 1; constraintsJCheckBoxEnableStaleData.gridy = 1;
			constraintsJCheckBoxEnableStaleData.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxEnableStaleData.ipadx = 18;
			constraintsJCheckBoxEnableStaleData.ipady = -5;
			constraintsJCheckBoxEnableStaleData.insets = new java.awt.Insets(4, 20, 0, 3);
			getJPanelStaleData().add(getJCheckBoxEnableStaleData(), constraintsJCheckBoxEnableStaleData);

			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Stale Data");
			ivjJPanelStaleData.setBorder(ivjLocalBorder1);
			
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
	}
	return ivjJPanelStaleData;
}

private javax.swing.JTextField getJTextFieldTimeToStale() {
	if (ivjJTextFieldTimeToStale == null) {
		try {
			ivjJTextFieldTimeToStale = new javax.swing.JTextField(4);
			ivjJTextFieldTimeToStale.setName("JTextFieldTimeToStale");
			ivjJTextFieldTimeToStale.setEnabled(false);
			ivjJTextFieldTimeToStale.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-999999.999999, 999999.999999) );
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTimeToStale;
}

private javax.swing.JComboBox getJComboUpdateType() {
	if (comboUpdateType == null) {
		try {
			comboUpdateType = new javax.swing.JComboBox();
			comboUpdateType.setName("JComboBoxUpdateType");
			comboUpdateType.setEnabled(false);
			
			comboUpdateType.addItem("Always");
			comboUpdateType.addItem("On Change");
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return comboUpdateType;
}

public void setValue(Object val) {
	PointBase point = (PointBase)val;
	PointProperty attrib1;
	PointProperty attrib2;
	boolean staleEnabled = false;
	PointPropertyDao dao = YukonSpringHook.getBean( "pointPropertyDao", PointPropertyDao.class);
	try{
		attrib1 = dao.getByIdAndPropertyId(point.getPoint().getPointID(), 1);
		attrib2 = dao.getByIdAndPropertyId(point.getPoint().getPointID(), 2);
		staleEnabled = true;
	}catch( DataAccessException e) {
		attrib1 = new PointProperty(point.getPoint().getPointID(),1,5);
		attrib2 = new PointProperty(point.getPoint().getPointID(),2,1);
	}
	
	if( staleEnabled ) {
		getJCheckBoxEnableStaleData().setEnabled(true);
		getJCheckBoxEnableStaleData().setSelected(true);
		getJTextFieldTimeToStale().setEnabled(true);
		getJTextFieldTimeToStale().setText(Integer.toString((int)attrib1.getFloatValue()));
		
		getJComboUpdateType().setEnabled(true);
		getJComboUpdateType().setSelectedIndex((int)attrib2.getFloatValue()-1);
		
	} else {
		getJCheckBoxEnableStaleData().setEnabled(true);
		getJCheckBoxEnableStaleData().setSelected(false);
		getJTextFieldTimeToStale().setEnabled(false);
		getJTextFieldTimeToStale().setText(Integer.toString((int)attrib1.getFloatValue()));
		
		getJComboUpdateType().setEnabled(false);
		getJComboUpdateType().setSelectedIndex(0);
	}
}

public Object getValue(Object val) {
	// Getting the value to update the database
	PointBase point = (PointBase)val;
	
	PointProperty attribTime = new PointProperty();
	PointProperty attribUpdate = new PointProperty();
	
	attribTime.setPointId(point.getPoint().getPointID());
	attribTime.setPropertyId(1);
	attribTime.setFloatValue(Float.parseFloat(getJTextFieldTimeToStale().getText()));
	
	attribUpdate.setPointId(point.getPoint().getPointID());
	attribUpdate.setPropertyId(2);
	attribUpdate.setFloatValue(getJComboUpdateType().getSelectedIndex()+1);
	PointPropertyDao dao = YukonSpringHook.getBean( "pointPropertyDao", PointPropertyDao.class);
	
	//remove from database
	dao.remove(attribTime);
	dao.remove(attribUpdate);
	
	//add
	if( getJCheckBoxEnableStaleData().isSelected() ) {
		try{
			dao.add(attribTime);
			dao.add(attribUpdate);
		} catch( DataAccessException e) {	
			handleException(e);
		}
	}
	
	return point;
}

private void handleException(Throwable exception) {

	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}

private void initConnections() throws java.lang.Exception {

	getJTextFieldTimeToStale().addCaretListener(this);
	getJCheckBoxEnableStaleData().addActionListener(this);
}

private void initialize() {
	try {
		setName("PointStatesEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(411, 280);

		java.awt.GridBagConstraints constraintsJPanelStaleData = new java.awt.GridBagConstraints();
		constraintsJPanelStaleData.gridx = 1; constraintsJPanelStaleData.gridy = 2;
		constraintsJPanelStaleData.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJPanelStaleData.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelStaleData.weightx = 1.0;
		constraintsJPanelStaleData.weighty = 1.0;
		constraintsJPanelStaleData.insets = new java.awt.Insets(6, 4, 13, 8);
		add(getJPanelStaleData(), constraintsJPanelStaleData);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

public boolean isInputValid() 
{
	if( getJCheckBoxEnableStaleData().isSelected() ) {
		boolean ret = true;
		String time = getJTextFieldTimeToStale().getText();
		try{
			Integer.parseInt(time);
		}catch(NumberFormatException e) {
			setErrorString("'Time in Minutes' is not an integer. ");
			CTILogger.error("'Time in Minutes' is not an integer. ",e);
			ret = false;
		}

		return ret;
	}
	
	return true;
}

public void itemStateChanged(java.awt.event.ItemEvent e) {

}

public void jCheckBoxReasonHigh_ActionPerformed(ActionEvent actionEvent) 
{
	boolean checkBox = getJCheckBoxEnableStaleData().isSelected();
	
	getJTextFieldTimeToStale().setEnabled(checkBox);
	getJComboUpdateType().setEnabled(checkBox);
	
	fireInputUpdate();
	
	return;
}

public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		StatusPointLimitEditorPanel aPointLimitEditorPanel;
		aPointLimitEditorPanel = new StatusPointLimitEditorPanel();
		frame.setContentPane(aPointLimitEditorPanel);
		frame.setSize(aPointLimitEditorPanel.getSize());
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

public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	this.fireInputUpdate();
}

public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	
}

private static void getBuilderData() {

}

}

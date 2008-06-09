package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.db.device.DeviceDialupSettings;

public class DeviceAdvancedDialupEditorPanel extends javax.swing.JPanel implements com.cannontech.common.gui.util.AdvancedPropertiesGUI
{
	private com.klg.jclass.field.JCSpinField ivjJCSpinnerMaxConnectTimeMins = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinnerMaxConnectTimeSecs = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinnerMinConnectTimeMins = null;
	private com.klg.jclass.field.JCSpinField ivjJCSpinnerMinConnectTimeSecs = null;
	private javax.swing.JLabel ivjJLabelMaxConnectTime = null;
	private javax.swing.JLabel ivjJLabelMin = null;
	private javax.swing.JLabel ivjJLabelMin1 = null;
	private javax.swing.JLabel ivjJLabelMinConnectTime = null;
	private javax.swing.JLabel ivjJLabelSec = null;
	private javax.swing.JLabel ivjJLabelSec1 = null;
	private javax.swing.JComboBox ivjJComboBoxBaudRate = null;
	private javax.swing.JLabel ivjJLabelMaxConnectTime2 = null;

	//the original object set by the call to setValue(Object)
	private Object originalObject = null;	
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceAdvancedDialupEditorPanel() {
	super();
	initialize();
}
/**
 * Return the JComboBoxBaudRate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxBaudRate() {
	if (ivjJComboBoxBaudRate == null) {
		try {
			ivjJComboBoxBaudRate = new javax.swing.JComboBox();
			ivjJComboBoxBaudRate.setName("JComboBoxBaudRate");
			// user code begin {1}

         ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_PORT );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_300 );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_1200 );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_2400 );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_4800 );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_9600 );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_14400 );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_38400 );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_57600 );
			ivjJComboBoxBaudRate.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_115200 );


         //select the 300 by default
         ivjJComboBoxBaudRate.setSelectedItem( com.cannontech.common.version.DBEditorDefines.BAUD_300 );			

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxBaudRate;
}
/**
 * Return the JCSpinnerMaxConnectTimeMins property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinnerMaxConnectTimeMins() {
	if (ivjJCSpinnerMaxConnectTimeMins == null) {
		try {
			ivjJCSpinnerMaxConnectTimeMins = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinnerMaxConnectTimeMins.setName("JCSpinnerMaxConnectTimeMins");
			ivjJCSpinnerMaxConnectTimeMins.setBackground(java.awt.Color.white);
			ivjJCSpinnerMaxConnectTimeMins.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJCSpinnerMaxConnectTimeMins.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjJCSpinnerMaxConnectTimeMins.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJCSpinnerMaxConnectTimeMins.setEnabled(true);
			ivjJCSpinnerMaxConnectTimeMins.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}

			ivjJCSpinnerMaxConnectTimeMins.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(null, 
					new Integer(0)/*MIN*/, new Integer(9999999)/*MAX*/, null, true, null,
					new Integer(1)/*Increment*/, "#,##0.###;-#,##0.###", false, 
					false, false, null, new Integer(0)/*Default*/), 
					new com.klg.jclass.util.value.MutableValueModel(
					java.lang.Integer.class, new Integer(1)), 
					new com.klg.jclass.field.JCInvalidInfo(true, 2, 
					new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			ivjJCSpinnerMaxConnectTimeMins.setValue( new Integer(0) ); // Default value

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinnerMaxConnectTimeMins;
}
/**
 * Return the JCSpinnerMaxConnectTimeSecs property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinnerMaxConnectTimeSecs() {
	if (ivjJCSpinnerMaxConnectTimeSecs == null) {
		try {
			ivjJCSpinnerMaxConnectTimeSecs = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinnerMaxConnectTimeSecs.setName("JCSpinnerMaxConnectTimeSecs");
			ivjJCSpinnerMaxConnectTimeSecs.setBackground(java.awt.Color.white);
			ivjJCSpinnerMaxConnectTimeSecs.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJCSpinnerMaxConnectTimeSecs.setPreferredSize(new java.awt.Dimension(45, 22));
			ivjJCSpinnerMaxConnectTimeSecs.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJCSpinnerMaxConnectTimeSecs.setMinimumSize(new java.awt.Dimension(45, 22));
			ivjJCSpinnerMaxConnectTimeSecs.setEnabled(true);
			// user code begin {1}

			ivjJCSpinnerMaxConnectTimeSecs.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(null, 
					new Integer(0)/*MIN*/, new Integer(9999999)/*MAX*/, null, true, null,
					new Integer(1)/*Increment*/, "#,##0.###;-#,##0.###", false, 
					false, false, null, new Integer(0)/*Default*/), 
					new com.klg.jclass.util.value.MutableValueModel(
					java.lang.Integer.class, new Integer(1)), 
					new com.klg.jclass.field.JCInvalidInfo(true, 2, 
					new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			ivjJCSpinnerMaxConnectTimeSecs.setValue( new Integer(0) ); // Default value

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinnerMaxConnectTimeSecs;
}
/**
 * Return the JCSpinnerMinConnectTimeMins property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinnerMinConnectTimeMins() {
	if (ivjJCSpinnerMinConnectTimeMins == null) {
		try {
			ivjJCSpinnerMinConnectTimeMins = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinnerMinConnectTimeMins.setName("JCSpinnerMinConnectTimeMins");
			ivjJCSpinnerMinConnectTimeMins.setBackground(java.awt.Color.white);
			ivjJCSpinnerMinConnectTimeMins.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJCSpinnerMinConnectTimeMins.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjJCSpinnerMinConnectTimeMins.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJCSpinnerMinConnectTimeMins.setEnabled(true);
			ivjJCSpinnerMinConnectTimeMins.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}

			ivjJCSpinnerMinConnectTimeMins.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(null, 
					new Integer(0)/*MIN*/, new Integer(9999999)/*MAX*/, null, true, null,
					new Integer(1)/*Increment*/, "#,##0.###;-#,##0.###", false, 
					false, false, null, new Integer(0)/*Default*/), 
					new com.klg.jclass.util.value.MutableValueModel(
					java.lang.Integer.class, new Integer(1)), 
					new com.klg.jclass.field.JCInvalidInfo(true, 2, 
					new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			ivjJCSpinnerMinConnectTimeMins.setValue( new Integer(0) ); // Default value
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinnerMinConnectTimeMins;
}
/**
 * Return the JCSpinnerMinConnectTimeSecs property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getJCSpinnerMinConnectTimeSecs() {
	if (ivjJCSpinnerMinConnectTimeSecs == null) {
		try {
			ivjJCSpinnerMinConnectTimeSecs = new com.klg.jclass.field.JCSpinField();
			ivjJCSpinnerMinConnectTimeSecs.setName("JCSpinnerMinConnectTimeSecs");
			ivjJCSpinnerMinConnectTimeSecs.setBackground(java.awt.Color.white);
			ivjJCSpinnerMinConnectTimeSecs.setMaximumSize(new java.awt.Dimension(2147483647, 20));
			ivjJCSpinnerMinConnectTimeSecs.setPreferredSize(new java.awt.Dimension(45, 22));
			ivjJCSpinnerMinConnectTimeSecs.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJCSpinnerMinConnectTimeSecs.setMinimumSize(new java.awt.Dimension(45, 22));
			ivjJCSpinnerMinConnectTimeSecs.setEnabled(true);
			// user code begin {1}

			ivjJCSpinnerMinConnectTimeSecs.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(null, 
					new Integer(0)/*MIN*/, new Integer(9999999)/*MAX*/, null, true, null,
					new Integer(1)/*Increment*/, "#,##0.###;-#,##0.###", false, 
					false, false, null, new Integer(0)/*Default*/), 
					new com.klg.jclass.util.value.MutableValueModel(
					java.lang.Integer.class, new Integer(1)), 
					new com.klg.jclass.field.JCInvalidInfo(true, 2, 
					new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			
			ivjJCSpinnerMinConnectTimeSecs.setValue( new Integer(0) ); // Default value

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCSpinnerMinConnectTimeSecs;
}
/**
 * Return the JLabelMaxConnectTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMaxConnectTime() {
	if (ivjJLabelMaxConnectTime == null) {
		try {
			ivjJLabelMaxConnectTime = new javax.swing.JLabel();
			ivjJLabelMaxConnectTime.setName("JLabelMaxConnectTime");
			ivjJLabelMaxConnectTime.setText("Maximum Connect Time:");
			ivjJLabelMaxConnectTime.setMaximumSize(new java.awt.Dimension(154, 16));
			ivjJLabelMaxConnectTime.setVisible(true);
			ivjJLabelMaxConnectTime.setPreferredSize(new java.awt.Dimension(154, 16));
			ivjJLabelMaxConnectTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMaxConnectTime.setEnabled(true);
			ivjJLabelMaxConnectTime.setMinimumSize(new java.awt.Dimension(154, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMaxConnectTime;
}
/**
 * Return the JLabelMaxConnectTime2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMaxConnectTime2() {
	if (ivjJLabelMaxConnectTime2 == null) {
		try {
			ivjJLabelMaxConnectTime2 = new javax.swing.JLabel();
			ivjJLabelMaxConnectTime2.setName("JLabelMaxConnectTime2");
			ivjJLabelMaxConnectTime2.setText("Baud Rate:");
			ivjJLabelMaxConnectTime2.setMaximumSize(new java.awt.Dimension(154, 16));
			ivjJLabelMaxConnectTime2.setVisible(true);
			ivjJLabelMaxConnectTime2.setPreferredSize(new java.awt.Dimension(154, 16));
			ivjJLabelMaxConnectTime2.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMaxConnectTime2.setEnabled(true);
			ivjJLabelMaxConnectTime2.setMinimumSize(new java.awt.Dimension(154, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMaxConnectTime2;
}
/**
 * Return the JLabelMin property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMin() {
	if (ivjJLabelMin == null) {
		try {
			ivjJLabelMin = new javax.swing.JLabel();
			ivjJLabelMin.setName("JLabelMin");
			ivjJLabelMin.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMin.setText("minutes");
			ivjJLabelMin.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMin;
}
/**
 * Return the JLabelMin1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMin1() {
	if (ivjJLabelMin1 == null) {
		try {
			ivjJLabelMin1 = new javax.swing.JLabel();
			ivjJLabelMin1.setName("JLabelMin1");
			ivjJLabelMin1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMin1.setText("minutes");
			ivjJLabelMin1.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMin1;
}
/**
 * Return the JLabelMinConnectTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMinConnectTime() {
	if (ivjJLabelMinConnectTime == null) {
		try {
			ivjJLabelMinConnectTime = new javax.swing.JLabel();
			ivjJLabelMinConnectTime.setName("JLabelMinConnectTime");
			ivjJLabelMinConnectTime.setText("Minimum Connect Time:");
			ivjJLabelMinConnectTime.setMaximumSize(new java.awt.Dimension(151, 16));
			ivjJLabelMinConnectTime.setVisible(true);
			ivjJLabelMinConnectTime.setPreferredSize(new java.awt.Dimension(151, 16));
			ivjJLabelMinConnectTime.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelMinConnectTime.setEnabled(true);
			ivjJLabelMinConnectTime.setMinimumSize(new java.awt.Dimension(151, 16));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMinConnectTime;
}
/**
 * Return the JLabelSec property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSec() {
	if (ivjJLabelSec == null) {
		try {
			ivjJLabelSec = new javax.swing.JLabel();
			ivjJLabelSec.setName("JLabelSec");
			ivjJLabelSec.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSec.setText("seconds");
			ivjJLabelSec.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSec;
}
/**
 * Return the JLabelSec1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSec1() {
	if (ivjJLabelSec1 == null) {
		try {
			ivjJLabelSec1 = new javax.swing.JLabel();
			ivjJLabelSec1.setName("JLabelSec1");
			ivjJLabelSec1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSec1.setText("seconds");
			ivjJLabelSec1.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSec1;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 3:06:32 PM)
 */
public javax.swing.JPanel getMainJPanel() {
	return this;
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 1:55:11 PM)
 * @return java.lang.Object
 */
public java.lang.Object getOriginalObject() {
	return originalObject;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val)
{
	//just in case the spin boxes were being edited, tell the boxes to commit
	getJCSpinnerMaxConnectTimeMins().commitEdit();
	getJCSpinnerMaxConnectTimeSecs().commitEdit();
	getJCSpinnerMinConnectTimeMins().commitEdit();
	getJCSpinnerMinConnectTimeSecs().commitEdit();

	if( val != null )
	{
		//DeviceDialupSettings dDialup = ((RemoteBase) val).getDeviceDialupSettings();
		DeviceDialupSettings dDialup = (DeviceDialupSettings)val;

		Integer minConnectTime = null;
		Integer maxConnectTime = null;

		minConnectTime = new Integer( (((Number)getJCSpinnerMinConnectTimeMins().getValue()).intValue() * 60) + 
							((Number)getJCSpinnerMinConnectTimeSecs().getValue()).intValue() );
		
		maxConnectTime = new Integer( (((Number)getJCSpinnerMaxConnectTimeMins().getValue()).intValue() * 60) + 
							((Number)getJCSpinnerMaxConnectTimeSecs().getValue()).intValue() );
		
		dDialup.setMinConnectTime( minConnectTime );
		dDialup.setMaxConnectTime( maxConnectTime );

		//do the new BaudRate here!!!!!!
      if( com.cannontech.common.version.DBEditorDefines.BAUD_PORT.equalsIgnoreCase(
            getJComboBoxBaudRate().getSelectedItem().toString()) )
      {
         dDialup.setBaudRate( new Integer(0) );  //this truly is disabled
      }
      else
         dDialup.setBaudRate( new Integer(getJComboBoxBaudRate().getSelectedItem().toString()) );
	}
		
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceAdvancedDialupEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(393, 150);

		java.awt.GridBagConstraints constraintsJLabelMinConnectTime = new java.awt.GridBagConstraints();
		constraintsJLabelMinConnectTime.gridx = 0; constraintsJLabelMinConnectTime.gridy = 0;
		constraintsJLabelMinConnectTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinConnectTime.ipadx = 3;
		constraintsJLabelMinConnectTime.ipady = 2;
		constraintsJLabelMinConnectTime.insets = new java.awt.Insets(17, 7, 4, 2);
		add(getJLabelMinConnectTime(), constraintsJLabelMinConnectTime);

		java.awt.GridBagConstraints constraintsJLabelMaxConnectTime = new java.awt.GridBagConstraints();
		constraintsJLabelMaxConnectTime.gridx = 0; constraintsJLabelMaxConnectTime.gridy = 1;
		constraintsJLabelMaxConnectTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMaxConnectTime.ipady = 2;
		constraintsJLabelMaxConnectTime.insets = new java.awt.Insets(6, 7, 6, 2);
		add(getJLabelMaxConnectTime(), constraintsJLabelMaxConnectTime);

		java.awt.GridBagConstraints constraintsJCSpinnerMaxConnectTimeMins = new java.awt.GridBagConstraints();
		constraintsJCSpinnerMaxConnectTimeMins.gridx = 1; constraintsJCSpinnerMaxConnectTimeMins.gridy = 1;
		constraintsJCSpinnerMaxConnectTimeMins.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinnerMaxConnectTimeMins.insets = new java.awt.Insets(3, 3, 5, 2);
		add(getJCSpinnerMaxConnectTimeMins(), constraintsJCSpinnerMaxConnectTimeMins);

		java.awt.GridBagConstraints constraintsJCSpinnerMinConnectTimeMins = new java.awt.GridBagConstraints();
		constraintsJCSpinnerMinConnectTimeMins.gridx = 1; constraintsJCSpinnerMinConnectTimeMins.gridy = 0;
		constraintsJCSpinnerMinConnectTimeMins.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinnerMinConnectTimeMins.insets = new java.awt.Insets(14, 3, 3, 2);
		add(getJCSpinnerMinConnectTimeMins(), constraintsJCSpinnerMinConnectTimeMins);

		java.awt.GridBagConstraints constraintsJLabelMin = new java.awt.GridBagConstraints();
		constraintsJLabelMin.gridx = 2; constraintsJLabelMin.gridy = 0;
		constraintsJLabelMin.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMin.insets = new java.awt.Insets(16, 2, 7, 2);
		add(getJLabelMin(), constraintsJLabelMin);

		java.awt.GridBagConstraints constraintsJCSpinnerMinConnectTimeSecs = new java.awt.GridBagConstraints();
		constraintsJCSpinnerMinConnectTimeSecs.gridx = 3; constraintsJCSpinnerMinConnectTimeSecs.gridy = 0;
		constraintsJCSpinnerMinConnectTimeSecs.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinnerMinConnectTimeSecs.ipadx = 14;
		constraintsJCSpinnerMinConnectTimeSecs.insets = new java.awt.Insets(14, 3, 3, 2);
		add(getJCSpinnerMinConnectTimeSecs(), constraintsJCSpinnerMinConnectTimeSecs);

		java.awt.GridBagConstraints constraintsJLabelSec = new java.awt.GridBagConstraints();
		constraintsJLabelSec.gridx = 4; constraintsJLabelSec.gridy = 0;
		constraintsJLabelSec.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelSec.insets = new java.awt.Insets(16, 2, 7, 12);
		add(getJLabelSec(), constraintsJLabelSec);

		java.awt.GridBagConstraints constraintsJLabelSec1 = new java.awt.GridBagConstraints();
		constraintsJLabelSec1.gridx = 4; constraintsJLabelSec1.gridy = 1;
		constraintsJLabelSec1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelSec1.insets = new java.awt.Insets(5, 2, 9, 12);
		add(getJLabelSec1(), constraintsJLabelSec1);

		java.awt.GridBagConstraints constraintsJCSpinnerMaxConnectTimeSecs = new java.awt.GridBagConstraints();
		constraintsJCSpinnerMaxConnectTimeSecs.gridx = 3; constraintsJCSpinnerMaxConnectTimeSecs.gridy = 1;
		constraintsJCSpinnerMaxConnectTimeSecs.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCSpinnerMaxConnectTimeSecs.ipadx = 14;
		constraintsJCSpinnerMaxConnectTimeSecs.insets = new java.awt.Insets(3, 3, 5, 2);
		add(getJCSpinnerMaxConnectTimeSecs(), constraintsJCSpinnerMaxConnectTimeSecs);

		java.awt.GridBagConstraints constraintsJLabelMin1 = new java.awt.GridBagConstraints();
		constraintsJLabelMin1.gridx = 2; constraintsJLabelMin1.gridy = 1;
		constraintsJLabelMin1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMin1.insets = new java.awt.Insets(5, 2, 9, 2);
		add(getJLabelMin1(), constraintsJLabelMin1);

		java.awt.GridBagConstraints constraintsJLabelMaxConnectTime2 = new java.awt.GridBagConstraints();
		constraintsJLabelMaxConnectTime2.gridx = 0; constraintsJLabelMaxConnectTime2.gridy = 2;
		constraintsJLabelMaxConnectTime2.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMaxConnectTime2.ipadx = -33;
		constraintsJLabelMaxConnectTime2.ipady = 2;
		constraintsJLabelMaxConnectTime2.insets = new java.awt.Insets(8, 7, 14, 35);
		add(getJLabelMaxConnectTime2(), constraintsJLabelMaxConnectTime2);

		java.awt.GridBagConstraints constraintsJComboBoxBaudRate = new java.awt.GridBagConstraints();
		constraintsJComboBoxBaudRate.gridx = 1; constraintsJComboBoxBaudRate.gridy = 2;
		constraintsJComboBoxBaudRate.gridwidth = 3;
		constraintsJComboBoxBaudRate.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxBaudRate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxBaudRate.weightx = 1.0;
		constraintsJComboBoxBaudRate.ipadx = 31;
		constraintsJComboBoxBaudRate.insets = new java.awt.Insets(6, 3, 11, 8);
		add(getJComboBoxBaudRate(), constraintsJComboBoxBaudRate);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (9/20/2001 1:55:11 PM)
 * @param newOriginalObject java.lang.Object
 */
public void setOriginalObject(java.lang.Object newOriginalObject) {
	originalObject = newOriginalObject;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	RemoteBase rBase = null;
	DeviceDialupSettings dDialup = null;
	
	if( val != null )
	{
		//the first time this method is called we will get the RemoteBase object,
		//  every time after shoule be a DeviceDialupSettings object
		if( val instanceof RemoteBase )
		{
			rBase = (RemoteBase)val;
			dDialup = rBase.getDeviceDialupSettings();
		}
		else
			dDialup = (DeviceDialupSettings)val;
			
		Integer minConnectTime = dDialup.getMinConnectTime();
		Integer maxConnectTime = dDialup.getMaxConnectTime();

		if( minConnectTime != null )
		{
			getJCSpinnerMinConnectTimeMins().setValue( new Integer(minConnectTime.intValue() / 60) );
			getJCSpinnerMinConnectTimeSecs().setValue( new Integer(minConnectTime.intValue() % 60) );
		}

		if( maxConnectTime != null )
		{
			getJCSpinnerMaxConnectTimeMins().setValue( new Integer(maxConnectTime.intValue() / 60) );
			getJCSpinnerMaxConnectTimeSecs().setValue( new Integer(maxConnectTime.intValue() % 60) );
		}


      if( dDialup.getBaudRate().intValue() == 0 )
      {
         //this truly is disabled
         getJComboBoxBaudRate().setSelectedItem( 
               com.cannontech.common.version.DBEditorDefines.BAUD_PORT );
      }
      else
         getJComboBoxBaudRate().setSelectedItem( dDialup.getBaudRate().toString() );

	}

	setOriginalObject( dDialup );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD2F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DDC8DD8D44739A60ABFD1131004A4C451E0C2FA69F3390DB7B189CDCCCB28B757A6B6B5377AA4ED3D3D64467612FBC9EA9BEFEA2C2D4372A3609F8AA2C6C50218EACB101F457033C6910200A452C04C36B1098BFBE09716BD6B593330A86A7DE6664C1C334B598552A74F13DE1E67F3F65EEF661B6FE7661BB9F3F6C5F9872352D38CF9882524A3656F5D94049CE791FAEA6ACDB243C204048C2479FB8B
	E8A7FA64EA9A14E1404BDB141011033C6D19D00F06FA4BFD8999FB60F3AE4A6F0DB8819F820C63825EAFDF7FF5FE70F1EE4EE563ECA67599B5DB215C8E948398AE5789F87CFBEA8A94FC3902B3CAB1A034269737F92CE63F0255C03D83A88AE8F1AFED3F9C4AACB272343A9A255D05DB23503A277FE0580665E8E4B2AA6D23FDBB143288BD10323E8F0F7592519377198975F8A0ECFCAA8A2DBC8D2529E3206F3D9EF1302C4762F40ACED968311589D63BACCAE5D6E1485EA35455B5113241BAE4F1768856A63B45
	61F13DCF1AF4D81C0283A159064779D3EDA6118BB2210E6C98FB388A0F9D877595C0C99C9FB5E23C89BEDFG126D4C5E93FFB0A976182FBC0096BF6FCC5D91405EB9F6EA67E23BEAEF38E743663DA4B64F6661982C85DEDEFFC2469120F6A087509C20FDE4CE2C4A7DA3148D5DF223B8B8A8BA9BDC2E538E2B7401E8959C70392E8EB88A761150A3CAD604E85CDF2EA95551A79851FE3F19F4BF6A1318AAB687E2BF7A86DA7D50D943E69D1F2C0EA9B7C477D3DFAC69D7FDC247AE2F8DB4766AFE5FB96ABB76A7C8
	BBF674F4B1B5DDE76C452B0E9A56AAF2528654310D83B856C7C92CCB60F30AB2BFA8DEC5F02F86277375F1B2E7F0FDA6701287585CB0EFE73A34274DC5B9956D3AFE08C8E8B0F4B8288EB6073FAEEB4918618EBAE68A57658D02C7E9F02A4B94A3DBBB31404B99CC4820736B79FC3CFEE528078145G4583A582ED844AF132B9D618F7EC02F32C43A24927BDF607D510906D7BFC9ECEC802D716AC2DCE370C975FF90BE437F4BB84B7D25AA466E19FF47B747BB9197BBDB0469941A9C89659AEBAC13AE5D0108549
	CD5B3C1A3793371184426D328F8AB040208BB1392F6765C2693038650FDDD60BAC28ADA87F77F9B8A6C31673A0918440E76A1734826C2FF2303F85484C7DB0329B4BBBABC810AF6A6A5AC5F140636A42162098A776739912BB9C70B95449FC0C76E2AC996AE9A21333E877AE0D1CA6FB8FD65FA20D30B5F64C08F3F40148664C2BE6B6E71A365D0FCAD735694E99429769DC39A07A2F1DDAA2936BC56BCFD619032C5982D1FFBDCDAED77CA4689829FF037C6DBCD6A3EF62D805023ED1C0715C477F333B084E5953
	963740E669B989A698CE7E4C63D44F570D086B3CC973F926B1187EFFAB51B196FE25E3D0DF3C180F779FE1C8F04ACD02CBF0DA6103DB61A56563B9262C4FDE1177F91560AED84F0D30005C9CDF170F571CC516A5FB37C796BA85D936BB7BD47EC6B2165B536DD2D3819BABF58F4E3B62B02CE75EBEF38FDE8BBD2253A9102070AE8ABF1B2CC541A18C02520D2247A9D3BC27G4F6781E1C44985F9857830B3843BBA4EAB74EC304832855BA05987A154B8B5A1138B2F534FEB8BB4BEFA81668CBE6364G2F444556
	5C70A13666CAAEAFC471DF9CB6686D999177B799C2DDF42DC43854B5C7F5CA5F2B2F7B961293945C1DCA30DB0E9E04F23CC38A1D738F6EA3679198EFB9509A971B73B376E2BE4BAB4AE4EF949D8E6ADA6E736BEF6660988917BC828C039358D96E4769EF6E72631DD7FCCA79B73F09FD6A5064BE6A6B4DDF603DD96F9C56673117F9E4BB4EF49699A6194BA393E368F9EB994919D846C697BBD77D27895BE73C84679DA06BA59613673631F3D578C094CA3C6F4D00674851CBB496D117541894CDE5672A0D7B7118
	0940DBFB0979AEEB1FB637B7385DE20F1DECAB0A5DF17B34B12358937B32216C35CBEE39959615177968FB7B303EF85FD1E0E52D7FE04C79A5CDD273EC01B4B66752FD7D5E93589F25925D5777F27CD1025BB4B85D6F13087FF0BD9AF8AEB75B0BEA8B713CB7493027813900E68245GAD13199F4E6E2FC2636C752D90D749E9F1F47AACF24576148329F4DDAA75748369ECDDC2C97749B5A466EBE04C75F8FC02BDD00F317D1E048C23076DD1B1A74A21B4C35D8B9481948F1484040660BC8CF48A488C94829481
	34E40875FD0274DD8D750DC07B0793B2CCC09700C601A20116G2D865A88345F8BFCAF6B7B2AA99ECAAC93A972D9BC70786C3359233FC7C6A8F8D2G3E9E1DD24670CE220F160EE86C775DDF5EBA01ED598D340D44C36D1325799CE8AF5E2C14DBA6214BCE25CC7F92F6E0427B479EE57E7A6EE3278B836FE32D057A7B5839C27DFD6C63421D9487995AD3AB63FFD698FC1F938A7576B91A4BFFDB0B77A207E5C47440D37FB2AF1E5F3B6A581EF3816AB2D0A450A2AF0D974D217FBC0C7D5C8B07DC982A53A57420
	68915C7729FA56B7876AA5C0CDC0D620D0A0BA66D4B98F4A36F6CB376078406EEC249BAABE71D27E7BE68D5F629D437F45B1ECD7DBE3274B8E9C49378988E436BDDA615B4EE7A8CD3B9966AC2DBE4A9C4BC54F1E8E523FD368E17DE79FD07B834C7BD7B7E5EB718A0677F48451DFA3B722B3DB2B976FBF7F626EG7E2964E37F593C130CA18EF60B27C56FE90B477A91BCD6A0FCC6A04FDFA772B4F3B6F904BE8764G0F72133769719D1C3F53286507DC267C451C7FE1359EFF9967B7D56B49DF4D79A147757A2F
	657C0F2B747A27DDE17CD2239E3F00734D35FA72CBB9FFEF07DE7FD31C9FD92F476F627C139EAD5FF605724320247BDEDD9DFB4E0D86EC7595F6C7D4F191BF671233C93FE548D2E699166B6A4EC8F66BE9CBDFBBD92574ACB225AED3691FB74A4EA20B084CF628DB01C6C759D9447BB6BB0B44FEE3AEEA8FB92BFB96C96C2AB4040F52B59EB72A1EC5E2BC9BB653B1136B59186B00DFFA95E3EF1A34F66239778EB4A80312725BFD78F8CE9EC5DA7EB59FBE56B7B7D54BDF6443CF57742736BDF318593671199864
	F838B660B9CB3E2A1CFD1532637CA1D4B26B371B295FDF6BAD507A5DDB669E0644D6D7C7D799353F7C3DCC450695D719EF2FB6EB4FF9CBF6D49A3684985FF80D0E5BF5CD75EDA9776D216DCCF678B5B6972E9DFF9BE94E29830E3E327E9E3CB314755A85079514A3A92499D7581DE00B914B4179FEBD48215834EA06DD384E30AB8443B644F36C51633CAF4766D431F697C29856E3E4D8B847EE29E5FD17F32C230335AB3F01E1BF2AE7F65AEEE0F676B976697B1EE5AF4536A18F3B6FDCFE83BB7F1C2A7BF2F703
	9FFE49FEB3A65D0F1EFF2754633519B6457F9EECB641F326785F03BD427343FE6005CCE167E2F7911E27EB200E26C2BE83AA876AGF2CCE567628D0578BC9974DC5CA53A5A61F062509CA728FD3F2A7FF2F726CFEEE66BE071E377223869EF2484DA87A1D315679D25CC6ABAC4F0EADFC6B136EF85701201F2C2616C8CE4826A8AE5761DBED8CA6D430F1B8D32EC693111A79DE65CF93BB02C4278F0C2ED2BFECF6F7E4296CA759805B2ACF46C19B69A30B8BFFCB954930156E9F02A3F6B904ED71953E09FGB281
	F5814953186EF38E0E7B4C723ED77719055AF0E43B1E8D6153289EB1D0AE19B6560695002D714357C3FD6B74040C22697E67708F825E739821ED4BF47DF37F8540DDFE3CD028C7G450E99E3D6F520B162216D4AGE3A4813E510F17B383FC8DD4B343FF0C10EA6DDCEED7789715D206F2668CDFD9345F126301F40B04760BE6686B369470977CF849D0C7B393B272E67A6BF6EB15D637A38A5F24149DD0DAE76A69E6B3EA7BF915B6F314B28A4A453A7D42EB8359348C5A2F1E29EF53DA4053EE746595C03D9468
	540D7EB6FD5C21552DCB615B14F2944A309B75F4FB21DECF37E825ED9C144BEF9C2BDFA2E06B7C704CD970FC8CD4BE4BDF37AB1EE0F92DE956D8794A3DEABDD2E326793C246E2B3FEFDD75150EC17351E8D160675429C57A4F29B79669BF275ED2947CB9742E2240772D8B0A34772D4A5DE1894E25E6080D8C9481948B94BF0B655252370A914925BAFB407B56BE819E0C70DB9C190D3F23B8302D7945633F7BB9D80C1F473AC5D9968731FC069FAE467112C59781A97647E26CA3495EE713E9D385AFAFA66FDD04
	DE82537B24CF4839F2B55815BC0B5DB1C51F44981A8DFB7CEC065DCF4ED825D0BF4531D7895685F59747D66F42D8A85443B976FBF2F60A017A920E1D9942588A28A7F16CBB6D6CEEEB63EC66E76F2140FE3BFEC8FF0ECCBB24BFC7E69F8AFE17F17B21C0F3086A57361D792AFD8E53795F37B3DFDD645842B666AB19E3BFEFE33E1A49312957192FA2B9A619182F96F16C91937355D20ECD4FE53EFA01E3A2B99B9F3989E2F493436AC718FEDD9CDBD345F431F14C6BE13A0CF2ECE9A753A50CE34F1EE73AC4F36C
	07C3CC17B80E45D6B3DDB2EFE6583C4BCC177D9CAB2CE63A14F36C5890532509E3E74DCC97B347EEB1B3DD9C9C7BB157A504E3F711B1D6C0BD11E3118598DB877534DB98163217754D6158E172DCF2916AB247FEF61C59B613E3AF8DE3AC926A0BB956D30931E5D0DF230EA1E3ECBD54B34398F6310B0D3B1FE3AF12F68E2807F2EC8311978175D80E4510D846C3FD8547EA73493DB15457F1ECC1BE9B835D4A3067C85F8E28DBB9F61D4CA1AF54E7F26CE724EFA454E3B9563807471CE33F2B41D866EDE047EDFC
	6E56B2DF15F32C799A73D59347FEDE43FCE566D84BE566AB87473E5748634B311491662B95EA3BF34CD7EBB86668E073E0BD47DECB4158565BC1675B99E6B630B10EF02C391E4D83AF47B690EC8E54A3B97685DD3FD00F6758A932D6D7C2BD09E3B792ECA3548B4299D6C1FCD08AF5934732086FBB20EE6558C78473C2BD0CE3DB2A184E519C3B4D4330F561AC1FBED692B81FBED9B27EBE74F489F93F837BC75708CB20588FCB70BD2E585B6B9678FE3732A407E32D6E8E51ED47AF09687D2073BD7CCED2060164
	BB301E21E48FEA024F9DF730FB5A47CF62E738C10B57BE68996C34DF9690ED7BA332B7454167253C6D12DA3C878E595DF67C1ECDE9473F1F341637CB2F4377B3AEC96895A4C9309229C8E9DFC86E34501D70DCF2A7EB7F5AAE2CE72F68D4744CA2B12B81FE93EF33FB880FADB831132C0A2C52A1B6364C5BFDBEC46C31BB7D6C9922398E5A05812D3E93E32B5A1BD0603393FBD6CFD5E8897441F4C841D75CC96FA75239EDEE32C7155C85EB9008E299BBC85E057AC50EBD5D46741E43316DE749DA05FA8C47E249
	3E35926A099C1BE7E2FDF3A298769F84AB02FAE98433EDFE6EF8362586314D9CC16FAA52913BEB1EEEA467A2CB4FC01FA4FA1CD6E6738212472C505E45753112BC918675C80EEDA36DD6E07DB836102C1B4C3B615969EE063D52496CEB6158EF48DEE8063A75EEE65FAF06025917BA0EFDE1F72BF7B1F40CD5E46F0A84BC060F3B146445E5D0DF4931B324DD66DC50F9AE434CE4EE1D02FABB47E4B333A304E3F1848B03FA64DCE6475AF16D88B687174DD56D20F17AD6356FDADACA74DD896D926732B572D0814B
	8BB97370F7B2977C89AFC92ACF415EE07A240F23CF57BCFFFDCC4747660C4E634CB79767B15FECFE97E3AE280F4EE3FAEE98564FCD3B3CA4D650AE06373DDC99B8675CCE76735550B60937AF10FD7DD4C64E81F9118999C511ACB643DD41FC31F9BCDF001C83BE3ED8AF6B67269C328EAEC2FBC7A453EFE75558FCF7240A79AD02377BEE152F9D2BC81B9860AF66EDBA72FD637DE4299A6F0773035918B50E0DEBA27D631DD4B5B65E1B385EEB39CEF79AFD750EA579814D07FDE1BEEB733FFEFA3F710E2A77DF77
	0C27F73075EF1A6F2FF71E4E5E26FE7736E5BE4F3584B3C35D4AB1111C33C2219E46316D245DD2282F6058A512CFB6C2BDF3811BE3BFE99CEF0E8535E301BA476898FFA4393785702E85EC5C64F3BC97F16C8EF2C68B03FA6482666FEC032F3F779FD37DAD9A464B8B4174DC3E40BF5F0A75CC27977878BBCD3E633763ABEEA5BEC3A67DDCF0B3597B12C046BAAE67135C3131F46632710A6EE16D22F283670CEF1376655056445B3754784E594F49B95286FC1937A94A9F3BEE5F4DE7E3C773F676FC7DDCF083E9
	3B985A455F4366C8F155F8FB39567F7E3ECF86B95F5D34E9D3BA0F65B7BD636DB9691A39377E9ED59E4637C6A5E41CD4E453B8FD27C20D533C13783B92459FD0430A147756C57845F5DD31729EA1EAD87B7D7B1A6CF7F53F4B0629A00A5E5915AA6512990714B1872B54B16756626F2AB90561A276BA352C8946F362AF519E29282EA8AD2AB8FED4E97B5F35E40EC99E014AD9E4D465BC37EBA2F2E83F3BEB55FE3F76947B77EB06C9F22458EA37B8443E625222F268EABC4A5AB78E796A10D22F4AFA28FDA2BA9C
	C4743BB3A4E7AAFFD4D689FEC5237879452D6CFD4D7EFF1C071A1EFFA3054D89FFBFDB957FAE0ED25FDBA675EF5C2C58FAC255EF65F69ADF3A907D63CB5BAFF9CFEDFFE7DBD0FB0C5A583C58666B17B58D2A9C6F750948217DC21A54FE3D26494566C1132F8E773D2F4A3A12BBF19D1EEAD6E3D25A224A289A41771D3CFF2368902532E11BDDD6466BECD55BBEDF75F66071AAAB0C6F9AEB2A0E9B5FADAD2AACBF4A463A21CD6DBF68A98C5C3F9A6C2E2C647D4EFD2076FB3473303E4F5C96275BADC876DE0D5B14
	BEE9DD3EFE13BED465AD9D1A389ED167D43FE57CD915F17B6560FEFB67A3356D4E6A497BAD31D36DDFB289FDCD6755FE6F19A76F3759E6DF3FFD7109AA6F59C968B1452C7A2D71B3D5C6D841C466BF16C3F3468FBF5566661C39EF99583E6D1FB31215DC31DE13B3141C5A2D0E5FFF5C573EFC2B4A7B69F070181E90543667ABA79F530B3DEA7F575DC1FCE92C767165993B5A2F33EBB2B1257D7B7B55D85CB4204A7AE565C4F420EDCFD6797A6D65C1D54EBB79930DA96D3BD5D47B669BFD655AAE293CFF1B245C
	59125A77345F3E7AAB37E6DDDC8B9E67820F5A76051A494739FEC86DFF627244637C38D76D77C86364637C72089A672DD7D4D9F167A69E67548E3FF8DCD5657CD74AA463F1CD6DBB3D5ED76E372E2B3C6A896DE3346D34DCDFB94FA1D54E5BB5935F8FCF24297DFE37FBF2FB7207793EBADCCCD7E5212A096BB0157C7EF46C59E96DC94AA7D7G4AFB493732489D7142040C16056CCEB73592DF141C3774AB5F68203F04A0EFA3816C10C417A049A394A53FD2D0E4593271014486F2C201E2006201D682A58321FB
	93B272000EG1D82B283B900C200420122016200165F4B74301B0E8651C379A2A1F9B1AAD96C7855282247337811C339B71E715961C0B29A3A65B67065396EFE7C0DD3CB9F6FD3110573BCFFEFC7CDD4F8B5D9F80D4329DF06985AAF131FAAF4C2C3E71F728EBABB48BB686C716F4367E46B3F173DADFB6CFB593B32755F4B4E4F563E1725583D59F8B275014F4C3C5DBFF06CAF9C0BA5B2B176095DAA5BB87EA0518B63AD829E1061F1C42FE10219956C898D769785CB48461392CC3609926B7B2F5964B9536EF0
	507AB3240D5D6996E46E0F95C4C73B4B2251FB9547305E74B75A6F0F44EFC5A8769514AE234D703889F1F343A73750A3BA2D3E1FB8B79BFF133888FF1538CEC63BF59F655C925AB5F641B55C7C6367E33FE84AB3A0C3AB4A3091C3DBD17AB07618977F8E923F2B384BFD89DDC3993CA621EDBEE960DF96D67FE2794F4E3BD3D1C6AB5A0B1F36BBA17D62C9A5D871AFF614DFC7B623AC1788339BBFBB3722B4B7A9525D7208C3E8C7F93D9F61DF8C39E5415A4026AB98363B9D0FF48C7F7A67D80B45ED1371942BEF
	C7E9CE7CBBCE340B329D96E75FB1BAF76B0956A98D97C91E40339F013E39C4DFAC250CCB49FDFC4761375A564425A2C3B34AF60896EB332587D6D4AB4A12ED126036098E2B57F572949114C9DEE26255188A6276F1733131E5DD30903852835E79F336A5EDEBB6A0641550B15292DF1014719656C61BC502218429D5463FCB9525924E3B21D0FE7B87E776DCB0209445C86C7AADDE170CB2FAAD03F64788FC20C91784E5078C5ACA3061750A52A0E8D6642799E371814AEE7FFE799D5F5C70D7B2005AD5C68729AB
	216B31BEC19CBC56505BEBF78ADCG7895449F6062F11BB23FB63B3C376EEEDB35299B66C193CA9DACAFA77FD610FFAB713F958429A0C8C5A5B8F7AD8A7A674F77B21B496481FDF690FDF0784B1AA10272CFDE797E3DBFB52DCD866B1AD1BA794D9084A1C7ED7521E730DB1026475EF1E0CBF862EC9CA3971EAADEACB757BBE61E6F60763612351CB8D4D5177A52FD298614864B0566B6184AB8C141D416686AC9055CC6E6A14E89B8FF98F0AE476BD26F915CE74C39BA352CB17399235B46CECC8C6F6453835772
	C853FC726F5EC9BB9B775AC77A9E60BEBA625551907F3F90999ADBB340F35BFD8F0FA9776F191AF3CB154700B21B71CCF638EC16D6A5C26DE8CBB7F11DA0C1DB996528B311740565EE6801DC30CF45FBB0CE391DAECB8F4E88D9D60BB4C0A4E0150EFD0A32FA253EEEB2000C8C1FB6FF8A43129AE6D73423EC59063F0AC45E3522BD2A688E3C81AB8C1ACC50A678F311BFEEFC5EF540F88DEF0615DFAAD8D97C307A55442B462B23A40E973BFF4471CAA5E8595F39290D01CCE5572C58546D9A552922DBA7207AD7
	3224CEA97533DE2EA9DBC018F2A69914F73E16C179589BA48CFA2AFFCD0272595820EC65C179DAE9EAD5B54D52E81A7E7FE27AB8D56346A62C2F7174999A9B94838F4A5F4DE6FFBD50E64FDEF4621793199C5D22C4886B287C8E6B68F56D5D647AE985815F9B199751BBC947A275EE12BE837F93F9D79B837852C56CF97AF7D7710B65C04F421D039607C336B8869A1CD67CDCFCFAC496546F3ABEF88D4BF733B68A13723EFD8D6B5D2F6973FFD0CB878836CE84503B98GG14CBGGD0CB818294G94G88G88
	GD2F954AC36CE84503B98GG14CBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7598GGGG
**end of data**/
}
}

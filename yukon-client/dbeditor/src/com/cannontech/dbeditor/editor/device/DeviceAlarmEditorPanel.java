package com.cannontech.dbeditor.editor.device;

import com.cannontech.database.data.device.TwoWayDevice;
import com.cannontech.database.db.device.DeviceTwoWayFlags;

/**
 * This type was created in VisualAge.
 */
public class DeviceAlarmEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JPanel ivjCommAlarmingPanel = null;
	private com.cannontech.common.gui.util.TitleBorder ivjCommAlarmingPanelTitleBorder = null;
	private javax.swing.JCheckBox ivjFailureAlarmCheckBox = null;
	private javax.swing.JLabel ivjPercentLabel = null;
	private javax.swing.JCheckBox ivjPerformanceAlarmCheckBox = null;
	private javax.swing.JLabel ivjPerformanceThresholdLabel = null;
	private javax.swing.JTextField ivjPerformanceThresholdTextField = null;
	private javax.swing.JCheckBox ivjPerformanceTwentyFourAlarmCheckBox = null;
	private javax.swing.JLabel ivjAlarmOnLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceAlarmEditorPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPerformanceThresholdTextField()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (PerformanceTwentyFourAlarmCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceAlarmEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
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
 * connEtoC2:  (PerformanceAlarmCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceAlarmEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
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
 * connEtoC3:  (FailureAlarmCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceAlarmEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
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
 * connEtoC4:  (PerformanceThresholdTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceAlarmEditorPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
 * Return the AlarmOnLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAlarmOnLabel() {
	if (ivjAlarmOnLabel == null) {
		try {
			ivjAlarmOnLabel = new javax.swing.JLabel();
			ivjAlarmOnLabel.setName("AlarmOnLabel");
			ivjAlarmOnLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAlarmOnLabel.setText("Alarm On:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmOnLabel;
}
/**
 * Return the CommAlarmingPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCommAlarmingPanel() {
	if (ivjCommAlarmingPanel == null) {
		try {
			ivjCommAlarmingPanel = new javax.swing.JPanel();
			ivjCommAlarmingPanel.setName("CommAlarmingPanel");
			ivjCommAlarmingPanel.setPreferredSize(new java.awt.Dimension(263, 180));
			ivjCommAlarmingPanel.setBorder(getCommAlarmingPanelTitleBorder());
			ivjCommAlarmingPanel.setLayout(new java.awt.GridBagLayout());
			ivjCommAlarmingPanel.setMinimumSize(new java.awt.Dimension(263, 180));

			java.awt.GridBagConstraints constraintsPerformanceTwentyFourAlarmCheckBox = new java.awt.GridBagConstraints();
			constraintsPerformanceTwentyFourAlarmCheckBox.gridx = 0; constraintsPerformanceTwentyFourAlarmCheckBox.gridy = 1;
			constraintsPerformanceTwentyFourAlarmCheckBox.gridwidth = 3;
			constraintsPerformanceTwentyFourAlarmCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPerformanceTwentyFourAlarmCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPerformanceTwentyFourAlarmCheckBox.insets = new java.awt.Insets(0, 5, 0, 5);
			getCommAlarmingPanel().add(getPerformanceTwentyFourAlarmCheckBox(), constraintsPerformanceTwentyFourAlarmCheckBox);

			java.awt.GridBagConstraints constraintsFailureAlarmCheckBox = new java.awt.GridBagConstraints();
			constraintsFailureAlarmCheckBox.gridx = 0; constraintsFailureAlarmCheckBox.gridy = 3;
			constraintsFailureAlarmCheckBox.gridwidth = 3;
			constraintsFailureAlarmCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFailureAlarmCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFailureAlarmCheckBox.insets = new java.awt.Insets(0, 5, 5, 5);
			getCommAlarmingPanel().add(getFailureAlarmCheckBox(), constraintsFailureAlarmCheckBox);

			java.awt.GridBagConstraints constraintsPerformanceAlarmCheckBox = new java.awt.GridBagConstraints();
			constraintsPerformanceAlarmCheckBox.gridx = 0; constraintsPerformanceAlarmCheckBox.gridy = 2;
			constraintsPerformanceAlarmCheckBox.gridwidth = 3;
			constraintsPerformanceAlarmCheckBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPerformanceAlarmCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPerformanceAlarmCheckBox.insets = new java.awt.Insets(0, 5, 0, 5);
			getCommAlarmingPanel().add(getPerformanceAlarmCheckBox(), constraintsPerformanceAlarmCheckBox);

			java.awt.GridBagConstraints constraintsPerformanceThresholdLabel = new java.awt.GridBagConstraints();
			constraintsPerformanceThresholdLabel.gridx = 0; constraintsPerformanceThresholdLabel.gridy = 4;
			constraintsPerformanceThresholdLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPerformanceThresholdLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCommAlarmingPanel().add(getPerformanceThresholdLabel(), constraintsPerformanceThresholdLabel);

			java.awt.GridBagConstraints constraintsPerformanceThresholdTextField = new java.awt.GridBagConstraints();
			constraintsPerformanceThresholdTextField.gridx = 1; constraintsPerformanceThresholdTextField.gridy = 4;
			constraintsPerformanceThresholdTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPerformanceThresholdTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPerformanceThresholdTextField.weightx = 1.0;
			constraintsPerformanceThresholdTextField.insets = new java.awt.Insets(5, 5, 5, 5);
			getCommAlarmingPanel().add(getPerformanceThresholdTextField(), constraintsPerformanceThresholdTextField);

			java.awt.GridBagConstraints constraintsPercentLabel = new java.awt.GridBagConstraints();
			constraintsPercentLabel.gridx = 2; constraintsPercentLabel.gridy = 4;
			constraintsPercentLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPercentLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getCommAlarmingPanel().add(getPercentLabel(), constraintsPercentLabel);

			java.awt.GridBagConstraints constraintsAlarmOnLabel = new java.awt.GridBagConstraints();
			constraintsAlarmOnLabel.gridx = 0; constraintsAlarmOnLabel.gridy = 0;
			constraintsAlarmOnLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAlarmOnLabel.insets = new java.awt.Insets(2, 5, 0, 5);
			getCommAlarmingPanel().add(getAlarmOnLabel(), constraintsAlarmOnLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommAlarmingPanel;
}
/**
 * Return the CommAlarmingPanelTitleBorder property value.
 * @return com.cannontech.common.gui.util.TitleBorder
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.TitleBorder getCommAlarmingPanelTitleBorder() {
	com.cannontech.common.gui.util.TitleBorder ivjCommAlarmingPanelTitleBorder = null;
	try {
		/* Create part */
		ivjCommAlarmingPanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
		ivjCommAlarmingPanelTitleBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
		ivjCommAlarmingPanelTitleBorder.setTitle("Communications");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjCommAlarmingPanelTitleBorder;
}
/**
 * Return the FailureAlarmCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getFailureAlarmCheckBox() {
	if (ivjFailureAlarmCheckBox == null) {
		try {
			ivjFailureAlarmCheckBox = new javax.swing.JCheckBox();
			ivjFailureAlarmCheckBox.setName("FailureAlarmCheckBox");
			ivjFailureAlarmCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjFailureAlarmCheckBox.setText("Communications Failure");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFailureAlarmCheckBox;
}
/**
 * Return the PercentLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPercentLabel() {
	if (ivjPercentLabel == null) {
		try {
			ivjPercentLabel = new javax.swing.JLabel();
			ivjPercentLabel.setName("PercentLabel");
			ivjPercentLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPercentLabel.setText("%");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPercentLabel;
}
/**
 * Return the PerformanceAlarmCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPerformanceAlarmCheckBox() {
	if (ivjPerformanceAlarmCheckBox == null) {
		try {
			ivjPerformanceAlarmCheckBox = new javax.swing.JCheckBox();
			ivjPerformanceAlarmCheckBox.setName("PerformanceAlarmCheckBox");
			ivjPerformanceAlarmCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPerformanceAlarmCheckBox.setText("Low Monthly Performance");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPerformanceAlarmCheckBox;
}
/**
 * Return the PerformanceThresholdLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPerformanceThresholdLabel() {
	if (ivjPerformanceThresholdLabel == null) {
		try {
			ivjPerformanceThresholdLabel = new javax.swing.JLabel();
			ivjPerformanceThresholdLabel.setName("PerformanceThresholdLabel");
			ivjPerformanceThresholdLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPerformanceThresholdLabel.setText("Low Performance Threshold:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPerformanceThresholdLabel;
}
/**
 * Return the PerformanceThresholdTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPerformanceThresholdTextField() {
	if (ivjPerformanceThresholdTextField == null) {
		try {
			ivjPerformanceThresholdTextField = new javax.swing.JTextField();
			ivjPerformanceThresholdTextField.setName("PerformanceThresholdTextField");
			ivjPerformanceThresholdTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPerformanceThresholdTextField.setColumns(3);
			// user code begin {1}
			ivjPerformanceThresholdTextField.setDocument(new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 999));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPerformanceThresholdTextField;
}
/**
 * Return the PerformanceTwentyFourAlarmCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPerformanceTwentyFourAlarmCheckBox() {
	if (ivjPerformanceTwentyFourAlarmCheckBox == null) {
		try {
			ivjPerformanceTwentyFourAlarmCheckBox = new javax.swing.JCheckBox();
			ivjPerformanceTwentyFourAlarmCheckBox.setName("PerformanceTwentyFourAlarmCheckBox");
			ivjPerformanceTwentyFourAlarmCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPerformanceTwentyFourAlarmCheckBox.setText("Low Daily Performance");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPerformanceTwentyFourAlarmCheckBox;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {

	DeviceTwoWayFlags d = ((TwoWayDevice) val).getDeviceTwoWayFlags();

	Character failureAlarm;
	Character performanceAlarm;
	Character performanceTwentyFourAlarm;
		
	if( getFailureAlarmCheckBox().isSelected() )
		failureAlarm = new Character('Y' );
	else
		failureAlarm = new Character('N' );

	if( getPerformanceAlarmCheckBox().isSelected() )
		performanceAlarm = new Character('Y');
	else
		performanceAlarm = new Character('N');

	if( getPerformanceTwentyFourAlarmCheckBox().isSelected() )
		performanceTwentyFourAlarm = new Character('Y');
	else
		performanceTwentyFourAlarm = new Character('N');

	try
	{
		Integer performanceThreshold = new Integer( getPerformanceThresholdTextField().getText() );
		d.setPerformThreshold ( performanceThreshold );
	}
	catch( NumberFormatException n)
	{
		Integer performanceThreshold = new Integer( 0 );
		d.setPerformThreshold ( performanceThreshold );
	}

	d.setFailureAlarm ( failureAlarm );
	d.setPerformAlarm( performanceAlarm );
	d.setPerformTwentyFourAlarm( performanceTwentyFourAlarm );
	
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
	getPerformanceTwentyFourAlarmCheckBox().addItemListener(this);
	getPerformanceAlarmCheckBox().addItemListener(this);
	getFailureAlarmCheckBox().addItemListener(this);
	getPerformanceThresholdTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceAlarmEditorPanel");
		setPreferredSize(new java.awt.Dimension(500, 27));
		setLayout(new java.awt.GridBagLayout());
		setSize(397, 322);
		setMinimumSize(new java.awt.Dimension(500, 27));

		java.awt.GridBagConstraints constraintsCommAlarmingPanel = new java.awt.GridBagConstraints();
		constraintsCommAlarmingPanel.gridx = 1; constraintsCommAlarmingPanel.gridy = 1;
		constraintsCommAlarmingPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsCommAlarmingPanel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsCommAlarmingPanel.weightx = 1.0;
		constraintsCommAlarmingPanel.weighty = 1.0;
		constraintsCommAlarmingPanel.ipadx = 8;
		constraintsCommAlarmingPanel.insets = new java.awt.Insets(47, 30, 95, 96);
		add(getCommAlarmingPanel(), constraintsCommAlarmingPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPerformanceTwentyFourAlarmCheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getPerformanceAlarmCheckBox()) 
		connEtoC2(e);
	if (e.getSource() == getFailureAlarmCheckBox()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}

/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	DeviceTwoWayFlags d = ((TwoWayDevice) val).getDeviceTwoWayFlags();

	Character failureAlarm = d.getFailureAlarm();
	Character performanceAlarm = d.getPerformAlarm();
	Character performanceTwentyFourAlarm = d.getPerformTwentyFourAlarm();
	Integer performanceThreshold = d.getPerformThreshold();

	if( failureAlarm != null )
		com.cannontech.common.util.CtiUtilities.setCheckBoxState( getFailureAlarmCheckBox(), failureAlarm );

	if( performanceAlarm != null )
		com.cannontech.common.util.CtiUtilities.setCheckBoxState( getPerformanceAlarmCheckBox(), performanceAlarm );

	if( performanceTwentyFourAlarm != null )
		com.cannontech.common.util.CtiUtilities.setCheckBoxState( getPerformanceTwentyFourAlarmCheckBox(), performanceTwentyFourAlarm);

	if( performanceThreshold != null )
		getPerformanceThresholdTextField().setText( performanceThreshold.toString() );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GA9F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8DD8D447351860CF927AB491A3B5D8B192C3A2EDE8CBDFF8AFB61F2166D593535856B424350DE9ECC27BEC9EEDEDCBDA5B5EDD64C77CC3A1469F0445DF28387C78078495E40185C9C3049A12E79397768297165DF5772EACCA4CBBE7664E6C5DE59710567735FC5F615EB9B3E74E19F34E1CB9B3F3C148490FC855E8F384C113AAA8BF27B402F07922A0F4FFFA3A1861024478B4C175D3G30CD78E3
	43FAF8CE033A750F4727E5892F26E7C0B9924A46AF46273D8D6F59C2E2FAD8833C8460E3073A0B33B77DE1E4BED773981FF4D21E593B891EDBG22G074F35A272FF2CF72702DF266099E8340230328B5BBC531B2B60AA205C829086905DC55BAF06678642F9E2E505526E723D91425AE78EEBB7E3BF2ABE999C682634C66529939E53A4F5A32FDF91B911A6884AF1GB87894614450E9F8D63574F51F6D34756BBBCDD62B4DAA0B1DBDFA51AC49B6075EACDE13BA454ACA83F956D8CC0E7EBAD253E2320A962035
	74E07F6F74E5107E8461B8141389AE2481F946407B9200D59C5FEAC4FC15941F5686E01158B84F9C2ED24651FE63B1A176C5EB4A56G638C16687822A54FB8C3DD3FCB5FC5EC32B09FF51F88F51BFA6353F4G86GB3C0B0409E628BF1F9FF06E7CD07DCEB6B6F37D9EB6C7673963363DD1BD93440FBE5A554A8380BE2274DE19684EA6FB7FB0B7D48B392D03A743EDB2523BA09BA46EC9FFDF13E30722B973469FEF432B22AD49B5ECBF5915B6B518965DD9D10770ADEEF5F74667D0120669DB94916126A07F76C
	8BC55AC4251FF5AA5E45FDE8EB0158BA975EB50AFFD0FCA6415BD5F86A2777911F41F290542D6EE33E513E0549523CFE1610D5566CD78FE171B5DA23054AF04562AB4B3304E70805725C4CE579BE41472870D4168F899E4BAB202E3ABFBE0D7A575385B8EF23213C84E095C0B240CEEBFC5AF1G23157958267C12B17AD80B49A11FF7C996336890A86D5379BB60A93AE50729516A14CD56CE711249A119BAAC22D3D05AFCAB9FF55061C577DD627B1D4023C1340A8E13AC592C503B29DF14C5071336D9193F8D5B
	B8C4D25BA6750B40205FAE30FED767E7633CB6B9653F5A4DA6D974342075EB7251A657CC1720C768G5E29DEFE398F755583638F82184675502D437EAE088E889815154DB6DB1F4B5E06A391D6D9D14F9F105891896F8B2DCC47EF6CC75C81DBFCDA150D7573855DF6D5BF86299345B7B986599CCBB3E2ECEE31B11FF9231D790CE173C3C2718BCDFEFD86C136D1DF8931794E1DAC52A74AC54BE18D6DE31C3FADB67FF3EB74D87165B173C839457E37F0DBDDBE08360A85991783A4F19D3F2453D11FBDEFF20A4C
	CB5FF3000309E60F46291C5FB66ED47409F224AA72236EB772312CA2EDB26C71E925G6DGC1GD1G4B6DCC271F982F0C504F2D0EEFE45DDEA73C7C71597C8F64C5F579FC812EDF62B551AA9BC43BE8B5430BD3297BDE8172D566F7170D532CA4F8BB4403DA1800CE0E7FF1814ED913ACBB248E17AC360A32ACD93BBD75BFA33C1C2E8E3BA714B0DE3FA840315A86A09E70763FA9403954E933DAC5E2D4CE2254A71339ACDA44FE903A5666324A947F47FDB89F7A44C1A516E877E312F44DE4F1E1DC22B9C50C89
	75E9F7C87D60AA985A044BD7F11EDB7BF62AF4148C76475C28946A8CD7591C9D584F666C016BF305C51F9C547ADBF3429EB2E8A32F527995F755B3E729CC3F5A6FDF76B7C9CCD1704E9402339AB14E86CAEE68DC19F1887BCBGBEEBG56BB58DCF19D40FA960F14C9D2EB33D828CA392EBB8EE621ED442BAE9142B8863E8BDCFF52416DBEF51794DD527A2B87D11796D54CDC62C0DD6EE8E439E32E03464D6370EC863083448250F6D30D2CDD0E533BDDB114ADGB9B236FB0577978438B000B8A7354FDAA755AF
	ED9759445ADD0132DB8E542E7D82EB97866518006DB6B533F64B211C88106172EEC7FDAA7D934CE17C65295DAEC96F12A5DC91CCB2CCA63BCBA65623F9E982D9DB50378CAE16FFCE2DC3FB8EC1B994A05A45FC6F5B1BD97E995A97A12C1A37BBE01E3D58C5E5CBF4F9FCCFF7A74BBFDFBE0CBC8B2F4527D5DCE33E326620FA8D2CF1BAED1D92D9FE95BBBFF0C86D1B9497F1A8931EDD1243A9B7C270F0B31F18FF88654575D9C1AB794A3946CD0A3E1A01EFB0G75717984AFD9A5D9B2D9246BE2E1911D0FCFB62A
	6763C2E81F8C10BBGF3F2006D8F74369370F4D9A196C95DD6515CACB9E523E882D7EFB6F5089608C3ED90448A1BFBCD57CCFA0B495A2DEF0590E56DFE2768124076866B9A585E40B840F47D453CEA056F32AF4D92B2CC15DAFF39FE48CFF335C183D447D1839EDD072C78E6FA84G9DF71C110DBB895A943A196C7F7361B10C1963101C4A7D17C6A677956813724AECE23C42G379C20E790717AB3B9AA39B3GD78A50B648ECF03589F3C1C9967B75782BD5063C2A3687380A66C2A201E9C0561325C25F8875F5
	78D624787E62226D548FC4625DB4A9ABC485D3DDBE182D56E555E759FEF5B9E56BDEED4CA055E14220C717C3DFC9CBD973716F95DDBE4747F760FAFC5A656BECFC53CF799BDF68F52617899798DD9460D6F13AB9A77D516D3C41681EF81F5195834EFC0351051CD653055F20F40BEFB0FD6E3B087A6CB4B9C4D9CFFEABDAC1DD3A754E81B024224EDA2CD574C9F3009F727DD0428D2A07B5B7BC391AB084B1F148BBF7237B0414FA1435F048F7AF331960AB06FC77B2B36A58DE4688F551C3AC37FD328867DD5627
	9003818CG178106G42BFE571C2FF98FDF7443DC3A344BF0755E4E975DA74D98EFF987942BA2D14938F230E49BAAD30DC282E51F7DF9877297738D77814A9DD6BB0BA3DCFB959070E7A7155BF9F9554B1DC1654415BE5326094ED951565131DAF50D87512D72C4A3AC9578883BC5B8168FC158A58FC355FE4317EAC59CFC6C1F991C042CD16F3EA0CB806241BE34F51296DF20F60FA357E3378B49DC085C09B009BA094A06EB3E63F2B056F0CE6BF6213ED369AB25ADA5FCCCC517513467159E859B0BADF9BA5FC
	965836C91F0DDDAF4ABE29E9FCF2668D237315F3FD10BFDFBAFBE98F73A555142F2CEC2A6D91BB7B4E5B5C0A5F986A55FE139BC4F320EAF83685B13F914F309C4192445627CF6B553940422D65DA4C437C6582110AFC0B03D4678D7CAC6A65E262AF9340DFGAAG5AG5CG21GF193183F44971E99233F54824E887960F00FE1BA7D6EB8ED714A2836C81860CDC775774C298C250F2489ECFECD3EB0BE99E68C23731521708EFFB2FC6FA413217A8EA643D36F0FCF06670751794A603E431FCF9EAC4C756F1318
	E854CB2245CCE57D46E9A66BB468071E012CA5F96A92A82F66389FE4A0AE994A1B6EE438791908AB053211633E3991F196A8875F49463E219E77E9E13C1CCD4A3806527283F5A31DB1A05DD867FF601890BBA20F711CDD28014E1BEF96855EE3FF2B487F9EFBC5117FBD76770B36D1BC7421BE1163FB7022D17660C57E7660FB49D8176E58CA7AF6BAC5A7194838D9D16ADE175F927C6C457FE26A9795BDE58BC30D9B14B6F5326D5DB26EF353BB8E8ABEEB2A39C36F94E5179D97574A4AD6FC75BE2B5F56447CAF
	6ECE36CE4E5461BA1984650CE008D5414CCF2C85A31DDBD7848F77931A93FD052C3B4D41B4A752F07C93845F2E4253DC4950406EA74CD037A4187976CEA34ADABCB1BE2DEAA20B15070E84367951A37EEDFE6C886E651CB61723D37435EB65111D3C4E93D5B18DC79350B63504DEB6B93A45E1FE51C468E9DDA07A56A301FC075A7757C635FD4B611D0E3F176CC10CB0767609AC77EDE8D2352D7578422FC8DE69A77E287643C375589F2EE566094C76FF2BC25D07C3B996E059C4E67B5EB3EA5B2F1EC8ED1D2CBC
	B7CD72363DEE924BEF3B74183B9F07F28BC08FC090C044A4E63F5F9E99B5372DF30F105B7E34B8307D130A7D5B7FB745016D7F0762516D1FF6B4307DB79F9D5D7EBB0E0EEC7F4C8BEA1B168BFCADBA253641A2C567AB14E7622461730F767724D7FF47F8FFBFB8296EEF1DC21FB519BE8FCC8E54DF727B6A7E0E737E161CD677D761C35F6CD33EB2197970C68B6A536E903BC407C3B437CA57B156C5CFC6FFBCE0C19F1561BD08377FE5861EB3DACC03B6178CFB02A9582EFB834B6D1220DD8660CA818CD35467C6
	DF9EC0DF6FF2187AC15D2DF631D328388BE93F1E49E8A321FD9CC082C0B2404E3B984F9FEDC4DAA0147ADD7DC4C2AADF74A614AF885A057036BBDA513E6850649491183118CB753443AB370A833AE5F7F9F4336A2EC0F1F4E3814E4D35F7796EB95FA27875F77B6EB9E76B581EB3876A5CF733B8BA3100658DAB6FE673BB8EEF4395791ED23DB70D797EE17C39C167ED65C1EDD2C26EEEE0DA16955F03F779F6BC4AD26AA6B4FA57C51D740CFFC2BD33159D6A0281426E7158A79A5E975D63EDAFEA13D7C89CDDF1
	8F3549D60E7F85412FD361292DBA899E4B87C2A0BE06B0DBCDA07B115C4F452795FF0E616C3B90D78D65960EFBE1973369E50E2BA7F9F0B41497FD0E45DB2BBEF03CF4697D474B9BFA7FB952847D48B952DD7AC0F10E4A775AA9B63665DC66E77858D6F35C115DECECC99C774B130C367A5F986E91CE5B46F1191C768A473DFC1A5146F05CD0B6EB371863BE2AC3DC4E547834C2G6A9BC927BCFE33BCA39F1ED6F1C047CF6D8CD23FE8450B5042932525453A72D3C54A39DC44007A3B0D0A4CD3FE6FB2902A26D2
	FFEAD11E8BA76FD7FCE46A861697CD013AC5D35959E659952F3D868AD2D95960104C67CD5D4DA4CA52757E458D998AFD72D4365E27E6E21F2553E08F81F0E59A5B733A8F31BDEF747CD9C2F350853F675F2B5A4A356EE9D44E70E91EBDEF946CF9A94FD7B3994F84284F3A17641C9B5567F9284B09DA3A2F227535DE7568C3C5023A5E65D50F726EC9D157BF6CD51F2A2227E3FB76A09B5B3AE723844B57CE87BC5BEF3F170EE9C8F936DC5AAF9C387B7769549712CFFBFC41D1B796DF20FEF514645C31506762FB
	192F5564A1AE814AEBB84E1C4FFCB26BBE96273795E02CBA4E4BFBC8996FF0E8F91E0E45D2ECF3FB6275C24E636F5D377903A9776158438B8CCA394D4FB858FEA1C5019EE8037016029F3A4F332720B6FF7EFF5573AFEBD60116557B763DDC613772BE0FBFD3FB0411FCB9EBBA4402694CC60F12D8D28D65B60E6BABC11C9B4AA19C57D24EEC9943F1F78A0893C2A14F88E576FB3D8A67F2B114AF838C81C4GAC81D88B30E986EB77DB528E6F66E2143B3783B3681D19811EE65ECE2A42EFB910EEB43DA33FE205
	8F43B15E6AF6C3212377C5F9CFA13C370E4166290A9D55776E7E6E7C534750971D0FE1E7F0DEF4592CB24D533EC45651E050433499ACCF1B3C8B5B48306051797C909E53AA7E37E697733FC5FEF447FCAFDDF92E00B6887B1531A44E70789F65753713A4E735F51AAC185C5E0FBC641348FFAD3425B2EE2DC0998DD057F2BFDBEF16D4F9E4BA3F1B4994F43FFF19D2C710A974FE0FCC145F9E327EC6GBE1A737B6FB39EFE851C5F4AG7C52C76017B40CDFC6A572DB87780C1971E9144774EC4623EA267F7D4686
	41B34FDB200DF9260707FB262F1E2F5621BC1DB60B2B5F6AD478CE29E3E79C41B3593EB46CAC62C2219CB31365C6A7AA0E40731229D75905526BFF328D85E40B43E6979D72A045121BF9F6CE39E1FC5F814E9BA75D4ADB26233967A0D96B174F74BD07390360D74E74BD07591849720DC428EB8EE33ADBFA8E77CBB1D0DE86309AE09DC06E676353AABE4FF4F93322D498E5DF5FE633B7C3A2EAD1250EF4FC0919637BBE7229F4362E477E6703C25C24B7B5014E691BBFCFFD4D22BC935B76933C72BDD48D0EAF9C
	6A62GD6822C854818951F261B454677DF9545F4FC688EB532EC6A6CA13775ECF017A4F14003464DA675CFC313F7DC7A08648247215F6AD9ACAE35678D5F5F5A7254F797383F35C37B60D9D4FE3A0F3D114F5A0483BE86E0B9C092C056832C6F8507715B030BB6120FFB5D2E51FC6DB932B74439D58AB47904C6206B1472C3634454DA016B7D15CDCC6F398B9E908CAF3E29599FC06FBD8FA85F69BC60D9DB93FB57A977A3F7E2A8D0768ED51587834DC550EE9364D86FF5740278ED26EE014D7D476A831FA3ACA1
	EBF79270DD47F57154E954F3074DC1B6ADD44F8927198E8B4341A6GEE0010F0FA67CC696EA899EE1FBF1572B304F0FA7E30901E4B95BA9A6B3A4B8DFEF4220EF5EAC8845AF561B456E1B9EBF6FC5AB6C19D6BE6897E4FA43EC26C158B6D8BE7B319E798764133C5F4F4599C7D7885E95BG7865E03D4D65A007947C2ACC695BA4E38EDDEF12ACAE07685502564FA15FEF297A7353660B7BF779706CF1084E9E1B45CC3E3A6063D83AFFEF00F67C320437FD0DFC4F85EDBBC1FC528D45FFB59D71C408772C84CF3F
	9D08757AD621EDB63507FBB67B3660EBF57E6AC3F87D4C86FF75913359FA79F12340E5B36FC29DFB6D816AE122976EB3CBA60B2DFBDF318E3681E5470B58597B5E5DEC2E454E9EE92ECDE87270387B24E75E1D9155B9ED698D0DB6A5403CDBB3DB79A6FB36E7CFC97B965F73747D55DB145F4965AF7E82534714960F5C2BAFFA7A1EFD0BFDDF64FD1BF95F69ED1E3EBBAFF97A36E59F745F3753E4F5BAC50754256ADE69FFF1B66BBF7C8B7EF41FF4AE30BDA6FD6061BD278E750DE72AB4EE4DBE0B7A65E79EB4CD
	D06AA24F22AF71EF2F48A7DF74BBA33A6EFD061FF8315CD9753EAD73761F417D79367220637F4E3940E7CFAF1D73FF767472B97FE7CFBFB9B7727D5B4F4F857E86F66DB975B730F46D39540E36D9813E108410B387E2A7C0559C365E5FD10D7E0BDF698FDF6B6B4C5DF8D700DF664B0C3FD893F82C161A513F67F7552083F458E459560F7D7333389A3417EC3393A4458555200E9CD2F70FCC1BAA78C9B564AED86CA2E86A3B4F76F8FC77C45E2D4D4999F96CAC27FD8EBBBF7AC69E465DE9D00E82D8B407FDC7B1
	B71F1F7B4CE167BE4F677B1E7BFC5D6B5CC72373BD772951286B0F69FC4FFD8A3C4EFDC41D6F394F8D0156D39F7F0E577DE5D6846C19A3C65E4BD25F788F89576D8AE85B866086188690893090E0B940EA00E4002C3960B7GD5GADGE6G99E08AC078DC66CBABEAB18E855AE7A887F744219CA689DDCA09BBB37ABC361BF3DA67EBBB4FF9D37949E3E0380A52A21A478B15AC0F0F8199926732F3435777229CA3FFC75BA64996713C12A2A9FEE02CE431D377A07303B8BD394B05329DE04A036C1B3E43E50CF7
	8460929EE47EE1ABE37E11B50F75B11B34B5C2798A00FB5ED863F252B92A1FEFE0CA21686A35FE0F2E2A2B4622AB655E34AB403DA949A9286CDF2E645FEC4CE363D9437718CBB86EDB8D8CA7C4327318FD3B3D6E48G5F9CB9969F1CEE45FD4315C866C30B1B760D60C378DD90DDD9186F3B3D784A504F14D178D23A8F3C684201A6E6CCF4CB3355F40B01A6E194BABA2E0F5B8347CD31FD7438E9EDA75FCEC33CEB9B34AB772D4EF64C57EDDDDDCE1147E7F7FB9647B5BADBECCE89D7E3AA7B2C3C716D2FCF6523
	7F2E09745DDFD793FCF2246F7EFAE99E1B9B6B9F02FC73A166A37373EF15BFBDCF7CA9593BAD03FECAD2986E8D02DB8D3817B4EC6C4FC901F53CC29ADD47BF147C2FCD2FCA435726C4497F5A740624DE1B143F5510F08EF6C37CEB676D12B96EC30E7B836993F19FC8E6390763D3FAB3953CD1C4068C3F2197659AA038F68537C5057BD041E577221F4010FBEC8EC63B33176C57A50B0516F3C99B498AEE4375D1500BB2CAF613CA6EC39C77A11F93E91563BB6F4999A75D3B04CE18BF36FB009CE8C77D353804FC
	5BBC5F570FCF907C71793EFE7C241E79F1B554C54EE7FEFC34ECFCF23FB3CEBA79166968383615237C998F7B0EF7B74167BC6CBB5EF9FCCD53C1DD7043EC3C7F6EE527440715BD17724C0A9AEE87CA770BBAB55D8125DD0572EC8EC8775D86B55D95251D2CBC27782123B16174EE96A75E57B05C595DACCE6045B8756119D674F527320E50F6D3B9ED91278D752225B8ABEF77B247B97D34DB154D5AE9B86EA7592C1D060F758CAE7B4AFB3D6A7D793C5B3FB7CA3C2DBCE85EE1BA247A3B9C9256A8BE573D5903E7
	E1CD51131964A161E06758A8F627D42DA75F0EC2F9E594734B72920F3E5ED13DAF565FFE5D7D707F01475B651EB169D46F0F145DFE5E713715873569F3156CFB05C4EE67DE8D7B56E19D47BD2BE1F70C990FB0DCC6BD23ED673885FB982D0563B665B2DAB7477D0C642FCB201C40F1BFA66D56C0F99D47B56C65FC9FE5670DB7AAF10D16F17700FBCA011D43CEA7E3E907F6E65E7683FDAAEBAB307C7DA6DE9DAAB43FA1B4E150BE72D1F696B477AA5653F3559FEB58396A0AC77D5FD3296F104C2ABBA4F2377128
	6FBDD5995159DA408B8B983F458E8F3F7D390CDF6902617CD2C761E7D860FBE725B1FA5F893CCD3EF3ED01F61797B09D3D196BFDD7793A53A34F47FB19BCA1FE64713DAB0F00B688CC1ED815BC346F0F2E89DC36E37A434288E7847A8E1B453C8FB6D074200052BFB164217FD8F3EBE78C59DA3637DC44F57F23CF85AE5B5302276FB9FBEE2D6F36DC56F7B26FBBD163695B305E537777F2EF2D6F29FB793DC974C8E7395D647F4C505831D32BDE3FFB00EEA8FAACFB2B8FF355F45300A6F2CCF421FB3D3EBB841A
	E523505179DD229D3EC69F572A57E8653B345C616DDC39435BAD58BB3CDD4CDEF5BB7ABFFA5EB7BC8693A97A67C22AACEC7FAB7F33D39D393DF2D64AC2D60D8F4A6990368F5F63577C5BCBAFC6FFE248518A5AC6A12D07AC220DC26AG2E32EE65DFDDE856BA2F8AB785ADAB4942DB4D36811D41A4D986F52A2B89D9F89B716F02AF7478566C403BCE17D56AA4A73BCE1DF2FB82C2ED65C2B5121D432AEB47AAD3DE1D1722955603C4F4A755A82C476D90886620822640CE0A086775833DED76F24927671EF8ED59
	0FAEB9D30434C6E197C611D651C17F38570CFFD8207CE70EFAE1035DA6C1A560011513BCD21D7220C5EC96F23AAE62DF1DBBE551DC430ED41D288264D402A7C1A5C61333C746ED60BB4D42FAAB7E8F91E1BB2D463F17ADD17EDE16482B2939C2C45D22481BCD6445DE743C176CA7369EACE8DA9D17A2E86B05CC0B4DE42EB7F54AB6C72330C1E637BFEE7B39EA52D146G7BD0BD853A5B43070F0355639D91973A4FBDBBE4F36266FA30235BA114101698B4753C0539364764GD62223D1467F09E2F39C60F59372
	64C34FB53CFDD9ABE814C122CFECF25BE5A12D4B548F2EGAFB4G90EF4912C1DA07496AC49FG49F4BE1231AA4EC0BFFDE9690CC7FE7791E160A11505FC2ACAA0AD6996ED7DA5B5DDDD12D5649D00DE217B3D3CFBEC2377E9335DFD6FDBCDAF7C29837C40A024741716125FE564F7B97EAEA318B202A9AB8765A6797A16770FEF3D1B0D19B88F77EA72675275E0C1797BBFDF7A608F2A125640686A05D4727725E004ACCF2B3F387ABBC447246899FBB7062E3A87EDE4C7D7F1E33FB934FB96B1D3959088B6F318
	D7A1F64BF077C074F6EEA4AC3DC51A3ECDFE6F3E65A1A3D5AE214D63129E7817117408C752D4616F15F4833FCD3BBD42167B97B6751FCF2D275DFC2D08853152395C1FG6481201CA55EEDE4E10B776AA08B3BF1A1D1ADA0BAFE6D8DCB1B66A0E81CDCE76B5E33D67A5501A6008E56B39D983CA2D59ABD6C22E3C11A5B231CFA77BF1F415E7B17F12DCB7FD0C98D37D1523F790F02DA655B04224F545FA6A4CF5C9970FBF76363B4873D7C38671BA0FA6771EB328F8A83FC6C636C5E234E087FB7A050F7C12D7DA6
	0BC5B6D97AEA2CE67CC6687C20ACFA6E04FFE7447E1D2C0DD2C96BB464EFAAFBD5B47F87D0CB878823B94DC5D29BGG04D2GGD0CB818294G94G88G88GA9F954AC23B94DC5D29BGG04D2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0C9BGGGG
**end of data**/
}
}

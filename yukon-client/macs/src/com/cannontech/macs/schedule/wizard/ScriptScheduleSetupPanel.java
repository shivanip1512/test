package com.cannontech.macs.schedule.wizard;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.schedule.script.ScriptParameters;
import com.cannontech.database.data.schedule.script.ScriptTemplate;
import com.cannontech.database.data.schedule.script.ScriptTemplateTypes;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.macs.message.ScriptFile;
import com.klg.jclass.util.value.JCValueEvent;
import com.klg.jclass.util.value.JCValueListener;

/**
 * Insert the type's description here.
 * Creation date: (2/15/2001 12:44:42 PM)
 * @author: 
 */

public class ScriptScheduleSetupPanel extends DataInputPanel implements ScriptParameters, JCValueListener, ActionListener, FocusListener, ItemListener, KeyListener, CaretListener, ChangeListener{
    private int templateType = ScriptTemplateTypes.METER_READ_SCRIPT;
    private ScriptTemplate scriptTemplate = null;
    //String for holding the script file.
    private String scriptText = null;
	private javax.swing.JLabel ivjBillingFileNameLabel = null;
	private javax.swing.JTextField ivjBillingFileNameTextField = null;
	private javax.swing.JTextField ivjBillingFilePathTextBox = null;
	private javax.swing.JComboBox ivjBillingFormatComboBox = null;
	private javax.swing.JPanel ivjBillingPanel = null;
	private javax.swing.JLabel ivjBilllingFilePathLabel = null;
	private javax.swing.JButton ivjBrowseButton = null;
	private javax.swing.JLabel ivjDemandDaysLabel = null;
	private com.klg.jclass.util.swing.JCSpinNumberBox ivjDemandResetSpinBox = null;
	private javax.swing.JLabel ivjEnergyDaysLabel = null;
	private javax.swing.JLabel ivjFilePathLabel = null;
	private javax.swing.JTextField ivjFilePathTextField = null;
	private javax.swing.JComboBox ivjGroupComboBox = null;
	private javax.swing.JLabel ivjGroupNameLabel = null;
	private javax.swing.JComboBox ivjGroupTypeComboBox = null;
	private javax.swing.JLabel ivjGroupTypeLabel = null;
	private javax.swing.JPanel ivjIEDPanel = null;
	private javax.swing.JPanel ivjMeterReadPanel = null;
	private javax.swing.JLabel ivjMissedFileNameLabel = null;
	private javax.swing.JTextField ivjMissedFileNameTextField = null;
	private javax.swing.JLabel ivjPorterTimeoutLabel = null;
	private javax.swing.JTextField ivjPorterTimeoutTextField = null;
	private javax.swing.JLabel ivjResetCountLabel = null;
	private javax.swing.JLabel ivjSuccessFileNameLabel = null;
	private javax.swing.JTextField ivjSuccessFileNameTextField = null;
	private javax.swing.JComboBox ivjTOURateComboBox = null;
	private javax.swing.JLabel ivjTOURateLabel = null;
	private javax.swing.JButton ivjBillingFileBrowseButton = null;
	private javax.swing.JPanel ivjNotificationPanel = null;
	private javax.swing.JPanel ivjRetryPanel = null;
	private javax.swing.JTabbedPane ivjTabbedPane = null;
	private javax.swing.JTextField ivjDemandDaysTextField = null;
	private javax.swing.JTextField ivjEnergyDaysTextField = null;
	private javax.swing.JCheckBox ivjGenerateBillingCheckBox = null;
	private javax.swing.JLabel ivjMaxRetryHoursLabel = null;
	private javax.swing.JTextField ivjMaxRetryHoursTextField = null;
	private javax.swing.JLabel ivjMessageSubjectLabel = null;
	private javax.swing.JTextField ivjMessageSubjectTextField = null;
	private javax.swing.JComboBox ivjNotifyGroupComboBox = null;
	private javax.swing.JLabel ivjNotifyGroupLabel = null;
	private javax.swing.JLabel ivjQueueOffCountLabel = null;
	private javax.swing.JTextField ivjQueueOffCountTextField = null;
	private javax.swing.JCheckBox ivjReadFrozenRegisterCheckBox = null;
	private javax.swing.JLabel ivjRetryCountLabel = null;
	private javax.swing.JTextField ivjRetryCountTextField = null;
	private javax.swing.JLabel ivjSecsLabel = null;
	private javax.swing.JCheckBox ivjSendEmailCheckBox = null;
	private javax.swing.JComboBox ivjBillingGroupTypeComboBox = null;
	private javax.swing.JLabel ivjBillingGroupTypeLabel = null;
	private javax.swing.JLabel ivjBillingFormatLabel = null;
	private javax.swing.JPanel ivjMeterReadSetupPanel = null;
	private javax.swing.JLabel ivjExampleLabel = null;
	private javax.swing.JLabel ivjScriptNameLabel = null;
	private javax.swing.JTextField ivjScriptNameTextField = null;
	private javax.swing.JLabel ivjDescriptionLabel = null;
	private javax.swing.JTextField ivjDescriptionTextField = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JPanel ivjScriptNamePanel = null;
	private javax.swing.JScrollPane ivjScriptScrollPane = null;
	private javax.swing.JTextArea ivjScriptTextArea = null;
	private javax.swing.JPanel ivjOptionsPanel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ScriptScheduleSetupPanel() {
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
	if ( e.getSource() == getBillingFileBrowseButton())
	{
		String file = browseOutput(getBillingFilePathTextBox().getText());
		if( file != null )
			getBillingFilePathTextBox().setText( file );
		repaint();
	}
	else if ( e.getSource() == getBrowseButton())
	{
		String file = browseOutput(getFilePathTextField().getText());
		if( file != null )
			getFilePathTextField().setText( file );
		repaint();
	}
	
    fireInputUpdate();
	// user code begin {2}
	// user code end
}
/**
 * @return
 */
private String buildScript()
{
    //Simply retrun the text area if this is a noTemplate script.
    if (ScriptTemplateTypes.isNoTemplate(getTemplateType()))
        return getScriptTextArea().getText();

    //Before we do anything, load the parameters map from the swing comps
    loadParamMapFromSwingComp();
    
	String text = "";
	//Generate the header code
	text += ScriptTemplate.buildScriptHeaderCode();
	
	//Get parameter set code
	text += getScriptTemplate().buildParameterScript();
	
	//Get main code
	String tempText = ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.MAIN_CODE);
	if (tempText == null)
	    tempText = ScriptTemplate.getScriptCode(getTemplateType());
	text += tempText;
	
	//Get email notification code
	if(getSendEmailCheckBox().isSelected())
	{
	    tempText = ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.NOTIFICATION);
	    if( tempText == null)
	        tempText = ScriptTemplate.buildNotificationCode();
	    
	    text += tempText;
	}

	//Get billing code
	if(getGenerateBillingCheckBox().isSelected())
	{
	    tempText = ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.BILLING);
	    if( tempText == null)
	        tempText = ScriptTemplate.buildBillingCode();
	
	    text += tempText;
	}
	
	//Get the footer code
	tempText = ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.FOOTER); 
	if( tempText == null)
	    tempText = ScriptTemplate.buildScriptFooterCode();
	text += tempText;
	
    return text;
}
/* (non-Javadoc)
 * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
 */
public void caretUpdate(CaretEvent e)
{
	fireInputUpdate();
}
public void enableContainer(Container c, boolean enable)
{
	Component[] components = c.getComponents();
	for(int i = 0; i < components.length; i++)
	{
		components[i].setEnabled(enable);
		if(components[i] instanceof Container)
		{
			enableContainer((Container)components[i], enable);
		}
	}
}
/* (non-Javadoc)
 * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
 */
public void focusGained(FocusEvent e)
{
    // TODO Auto-generated method stub
}
/* (non-Javadoc)
 * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
 */
public void focusLost(FocusEvent e)
{
    if (e.getSource() == getScriptTextArea())
    {
        setScriptText(getScriptTextArea().getText());
        getScriptTemplate().loadParamsFromScript(ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.PARAMETER_LIST));
        initSwingCompValues();
    }
}
/**
 * Return the BrowseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBillingFileBrowseButton() {
	if (ivjBillingFileBrowseButton == null) {
		try {
			ivjBillingFileBrowseButton = new javax.swing.JButton();
			ivjBillingFileBrowseButton.setName("BillingFileBrowseButton");
			ivjBillingFileBrowseButton.setToolTipText("Browse to the directory to write the billing file to.");
			ivjBillingFileBrowseButton.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingFileBrowseButton;
}
/**
 * Return the BillingFileNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBillingFileNameLabel() {
	if (ivjBillingFileNameLabel == null) {
		try {
			ivjBillingFileNameLabel = new javax.swing.JLabel();
			ivjBillingFileNameLabel.setName("BillingFileNameLabel");
			ivjBillingFileNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBillingFileNameLabel.setText("File Name:");
			// user code begin {1}
			ivjBillingFileNameLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_FILE_NAME_PARAM));			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingFileNameLabel;
}
/**
 * Return the BillingFileNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getBillingFileNameTextField() {
	if (ivjBillingFileNameTextField == null) {
		try {
			ivjBillingFileNameTextField = new javax.swing.JTextField();
			ivjBillingFileNameTextField.setName("BillingFileNameTextField");
			// user code begin {1}
			ivjBillingFileNameTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_FILE_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingFileNameTextField;
}
/**
 * Return the BillingFilePathTextBox property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getBillingFilePathTextBox() {
	if (ivjBillingFilePathTextBox == null) {
		try {
			ivjBillingFilePathTextBox = new javax.swing.JTextField();
			ivjBillingFilePathTextBox.setName("BillingFilePathTextBox");
			// user code begin {1}
			ivjBillingFilePathTextBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_FILE_PATH_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingFilePathTextBox;
}
/**
 * Return the BillingFormatComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBillingFormatComboBox() {
	if (ivjBillingFormatComboBox == null) {
		try {
			ivjBillingFormatComboBox = new javax.swing.JComboBox();
			ivjBillingFormatComboBox.setName("BillingFormatComboBox");
			// user code begin {1}
			ivjBillingFormatComboBox.addItem("none");
			String [] formats = FileFormatTypes.getValidFormatTypes();
			for(int i = 0; i < formats.length; i++)
				ivjBillingFormatComboBox.addItem(formats[i]);
			
			ivjBillingFormatComboBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_FORMAT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingFormatComboBox;
}
/**
 * Return the BillingFormatLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBillingFormatLabel() {
	if (ivjBillingFormatLabel == null) {
		try {
			ivjBillingFormatLabel = new javax.swing.JLabel();
			ivjBillingFormatLabel.setName("BillingFormatLabel");
			ivjBillingFormatLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBillingFormatLabel.setText("FileFormat:");
			// user code begin {1}
			ivjBillingFormatLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_FORMAT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingFormatLabel;
}
/**
 * Return the BillingGroupTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBillingGroupTypeComboBox() {
	if (ivjBillingGroupTypeComboBox == null) {
		try {
			ivjBillingGroupTypeComboBox = new javax.swing.JComboBox();
			ivjBillingGroupTypeComboBox.setName("BillingGroupTypeComboBox");
			ivjBillingGroupTypeComboBox.setToolTipText("The type of group for billing generation.");
			ivjBillingGroupTypeComboBox.setVisible(false);
			// user code begin {1}
			for( int i = 0; i < DeviceMeterGroup.getValidBillGroupTypeDisplayStrings().length; i++)
			    ivjBillingGroupTypeComboBox.addItem(DeviceMeterGroup.getValidBillGroupTypeDisplayStrings()[i]);

			ivjBillingGroupTypeComboBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_GROUP_TYPE_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingGroupTypeComboBox;
}
/**
 * Return the BillingGroupTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBillingGroupTypeLabel() {
	if (ivjBillingGroupTypeLabel == null) {
		try {
			ivjBillingGroupTypeLabel = new javax.swing.JLabel();
			ivjBillingGroupTypeLabel.setName("BillingGroupTypeLabel");
			ivjBillingGroupTypeLabel.setToolTipText("The type of group for billing generation.");
			ivjBillingGroupTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBillingGroupTypeLabel.setText("Group Type:");
			ivjBillingGroupTypeLabel.setVisible(false);
			// user code begin {1}
			ivjBillingGroupTypeLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_GROUP_TYPE_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingGroupTypeLabel;
}
/**
 * Return the BillingPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getBillingPanel() {
	if (ivjBillingPanel == null) {
		try {
			ivjBillingPanel = new javax.swing.JPanel();
			ivjBillingPanel.setName("BillingPanel");
			ivjBillingPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjBillingPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsBilllingFilePathLabel = new java.awt.GridBagConstraints();
			constraintsBilllingFilePathLabel.gridx = 0; constraintsBilllingFilePathLabel.gridy = 5;
			constraintsBilllingFilePathLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBilllingFilePathLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBilllingFilePathLabel(), constraintsBilllingFilePathLabel);

			java.awt.GridBagConstraints constraintsBillingFilePathTextBox = new java.awt.GridBagConstraints();
			constraintsBillingFilePathTextBox.gridx = 1; constraintsBillingFilePathTextBox.gridy = 5;
			constraintsBillingFilePathTextBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingFilePathTextBox.weightx = 1.0;
			constraintsBillingFilePathTextBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFilePathTextBox(), constraintsBillingFilePathTextBox);

			java.awt.GridBagConstraints constraintsBillingFileBrowseButton = new java.awt.GridBagConstraints();
			constraintsBillingFileBrowseButton.gridx = 3; constraintsBillingFileBrowseButton.gridy = 5;
			constraintsBillingFileBrowseButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFileBrowseButton(), constraintsBillingFileBrowseButton);

			java.awt.GridBagConstraints constraintsBillingFileNameLabel = new java.awt.GridBagConstraints();
			constraintsBillingFileNameLabel.gridx = 0; constraintsBillingFileNameLabel.gridy = 4;
			constraintsBillingFileNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBillingFileNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFileNameLabel(), constraintsBillingFileNameLabel);

			java.awt.GridBagConstraints constraintsBillingFileNameTextField = new java.awt.GridBagConstraints();
			constraintsBillingFileNameTextField.gridx = 1; constraintsBillingFileNameTextField.gridy = 4;
			constraintsBillingFileNameTextField.gridwidth = 2;
			constraintsBillingFileNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingFileNameTextField.weightx = 1.0;
			constraintsBillingFileNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFileNameTextField(), constraintsBillingFileNameTextField);

			java.awt.GridBagConstraints constraintsBillingFormatComboBox = new java.awt.GridBagConstraints();
			constraintsBillingFormatComboBox.gridx = 1; constraintsBillingFormatComboBox.gridy = 1;
			constraintsBillingFormatComboBox.gridwidth = 2;
			constraintsBillingFormatComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingFormatComboBox.weightx = 1.0;
			constraintsBillingFormatComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFormatComboBox(), constraintsBillingFormatComboBox);

			java.awt.GridBagConstraints constraintsEnergyDaysLabel = new java.awt.GridBagConstraints();
			constraintsEnergyDaysLabel.gridx = 0; constraintsEnergyDaysLabel.gridy = 3;
			constraintsEnergyDaysLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEnergyDaysLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getEnergyDaysLabel(), constraintsEnergyDaysLabel);

			java.awt.GridBagConstraints constraintsDemandDaysLabel = new java.awt.GridBagConstraints();
			constraintsDemandDaysLabel.gridx = 0; constraintsDemandDaysLabel.gridy = 2;
			constraintsDemandDaysLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDemandDaysLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getDemandDaysLabel(), constraintsDemandDaysLabel);

			java.awt.GridBagConstraints constraintsDemandDaysTextField = new java.awt.GridBagConstraints();
			constraintsDemandDaysTextField.gridx = 1; constraintsDemandDaysTextField.gridy = 2;
			constraintsDemandDaysTextField.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDemandDaysTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDemandDaysTextField.weightx = 1.0;
			constraintsDemandDaysTextField.insets = new java.awt.Insets(4, 4, 4, 150);
			getBillingPanel().add(getDemandDaysTextField(), constraintsDemandDaysTextField);

			java.awt.GridBagConstraints constraintsEnergyDaysTextField = new java.awt.GridBagConstraints();
			constraintsEnergyDaysTextField.gridx = 1; constraintsEnergyDaysTextField.gridy = 3;
			constraintsEnergyDaysTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsEnergyDaysTextField.weightx = 1.0;
			constraintsEnergyDaysTextField.insets = new java.awt.Insets(4, 4, 4, 150);
			getBillingPanel().add(getEnergyDaysTextField(), constraintsEnergyDaysTextField);

			java.awt.GridBagConstraints constraintsBillingFormatLabel = new java.awt.GridBagConstraints();
			constraintsBillingFormatLabel.gridx = 0; constraintsBillingFormatLabel.gridy = 1;
			constraintsBillingFormatLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBillingFormatLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFormatLabel(), constraintsBillingFormatLabel);

			java.awt.GridBagConstraints constraintsBillingGroupTypeLabel = new java.awt.GridBagConstraints();
			constraintsBillingGroupTypeLabel.gridx = 0; constraintsBillingGroupTypeLabel.gridy = 0;
			constraintsBillingGroupTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBillingGroupTypeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingGroupTypeLabel(), constraintsBillingGroupTypeLabel);

			java.awt.GridBagConstraints constraintsBillingGroupTypeComboBox = new java.awt.GridBagConstraints();
			constraintsBillingGroupTypeComboBox.gridx = 1; constraintsBillingGroupTypeComboBox.gridy = 0;
			constraintsBillingGroupTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingGroupTypeComboBox.weightx = 1.0;
			constraintsBillingGroupTypeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingGroupTypeComboBox(), constraintsBillingGroupTypeComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingPanel;
}
/**
 * Return the BilllingFilePathLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBilllingFilePathLabel() {
	if (ivjBilllingFilePathLabel == null) {
		try {
			ivjBilllingFilePathLabel = new javax.swing.JLabel();
			ivjBilllingFilePathLabel.setName("BilllingFilePathLabel");
			ivjBilllingFilePathLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBilllingFilePathLabel.setText("File Path:");
			// user code begin {1}
			ivjBilllingFilePathLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_FILE_PATH_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBilllingFilePathLabel;
}
/**
 * Return the BrowseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBrowseButton() {
	if (ivjBrowseButton == null) {
		try {
			ivjBrowseButton = new javax.swing.JButton();
			ivjBrowseButton.setName("BrowseButton");
			ivjBrowseButton.setToolTipText("Press to find directory.");
			ivjBrowseButton.setText("...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBrowseButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G1BE910B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8BFC945555F80AD1D3CDEBD434C1E90D0AB656D8E37F582695ADAAEA54285146CAAD5AD8290D95ED7A495746FE34CEDE1004G61D58284D8A0C2103086F905A404C09E840892A0C0002859E4B74942A63BECB632BC547F39774EB9B3BBB33B5970AB7D744B6FF7B2FB4FBD2FFB6E394F39B3A34DDB96151D1EB1CF125233A579EFCBBA7CCB162459D16E23080BDAE64E15D4FF4B81E6CB19B333609A
	8BF9717FB46796C85718F2A0BD8E52093FB767AE005FF3257E0F240761079FBD639F1224995F292F8E2C6721FE54B30D27FFD3188F5719G71001B84D0EEE27A7FD2D8A8638BA0BDD37E4DA0BDC312A6F11A88D3218AFFDE269B8B57A40064650CA6FFE1111C3F9152CDGA381E2178B5AF8388AA3AF2DA817695A2E0E12A6BFB1BBE3865325120FD032C270B649D713B402BBA2D37A20F793DC2B1ACFF6D7F7BAFB4D1D163EBEE71F475659E3BEEDBFEBF1DB4D7D9E0B47D6D1F13053EDF7F98EF676582C838EDB
	33254F66G3EB1AB183DBF65FE4F848805F463CA06B3AFE7BE1260F7A1C059CA34795999D532CD43CFC6C94937E67935B9FC2530B5F625E27314897BB25A0B197CCD8B73E53ED448AF811CA84F358BF67AA9CF2F25335F5CAF97C0AE1D36DC87ED1E8117DCB8D136AF96E259C695E3597E350C691EG6949G796FA17EA50EAF06F495C04BFBD866ED2B314C2D676E1652BAB6E64C72D3E647FB222C435FD34ABC6F19FD998BF95CFDBC40E2A79172A683642CB2671682B481B8G9673F84F753C8F579D9D1EDAE7
	EF2F33EF074B3553E1F5FF64345A9C703B2282F2E45C81DB2753ED15A491537719CA8D6CC188DD659B4F8CC4590EC89803B187EE17BC1E77B3328D4AB67A79399911320C18D5CA59A62DE2FE3B0547CA827CE6FC538184BE16634BD6ABF8D1779BB91E251BA1EF74EA74F36BCC3425A96B8629E07D660CE98636C4BE34B62330C458D0D62258A2FCDCF21A79388D70DE008800E800F80014927471C14F0641FC3C9BE24E5DE7F19C9C68F0D95C1E1D83F607556616FDDD39B0102F0DC0706D9EB21F70CB7486F59D
	CD78FF1B8C7DB2A1FAD1C6569A610F22B50ADF046EE1FEF5D72D514746CC2A43ABF81BE8DEA36AB0137043B93EDD0597F5DB4471AC6D023CA4GC13F5F4470656F8B7AF412F30C636BD4F8A16755E558BFB4C35EE8GD13F0FFAD95FDA38561C3B912089408A908A9085B0FAAD56718FBC9F84590E1AD575ABF80F8C7C8B2EB62F47ED595D877DE7DF276D3045ED37F4B8EC7D12CC53BE407C5A6143E7636D3B93F46C3275595C960F5D598752AD3DB68826FEC153B7B01B5138EDBC37455EEB83853DAE8965FEBE
	B0972E8ECB3F67104B8ABD37C2A1723F9CE0B1713965B0C8848170BB2B14796BB95E07A7C179D357222F5EBE43F00D105FDE0AFEF9E7E08EDC77C2F05B9C9595F576CEE60645FD86E3B4FAB98C321287680B57C17D813418E1BCB6E39BFD2B95E331EE46EDD269739B8DE31137618FC4DCD9BF504662CF390EF04047GA4GCC8248AF437139E2F92BAC0369E7F6B0FB0652EE90186D6AB4AB0750F110F430F4369C339722A3D713E67D1A703F659C0B61D2A8D7A3C0CF995641464FCDA2B6F7DA7AED980D7B5C90
	C8B62BD2A302F67DE9B65658BE3775F96AECAEDB1F95FE744B31D0FEBAD70953AEA745D0B54733FE2B9682291F70F527E78BBC48D0C7BD66B771FC0B476336F78CF858E06A3177F5AB7CAD5C16FE1FAE31DFAEEB67F9D65690A8E3A4C0B415752553F3D5715664F41E9CF0353016A4476D88BEBE0F817AA42A7F08E5EC3E16C6699BF8DA49DF318C6BAED8E0320656D7D78ED947D062C768B84242C07E5D2E7A7D7A10F58F85040E8C534554A16ABE516BBFF6DF709A476ECBDE6358FD559BB8B65F709A4526487B
	AFAFEB5F4E53FD2AD87F1B1775031D4E3EBE9BEFF044A27785EF3530F2587B6C1EA3F6EB374DF350FE56A6C9B56BD94C3E4E7B5AC278DD3A9E7B4553AB31DFACBEFB33B47633D9067328485B4C99216BC5ACC62ED77AC5E1D35A995606EC8EDBAFB82A56B950679178FF72FEDC4677E7F2DC2EF4B363AD598363DB07F327535BE26BF5B9E040A835707218452559666EF23AFBED561293164FADE64BE90F195707F9874F2EE73F5718562D2BB699AC90CACFE805FF36C3A335493F584AB5075714496DE22E74134C
	DB053DBB87BC9EE7DFED0F2D7324D079EFB5D8A7FD28962F176D2DDFF2CA585B82B15AE037B92C8DF6076DE3984BCBBB2183751879FFB1C6F3D33D667E5390113254DA16BB0831DDF637EDF71FEBG2500768F49DF771D6AD374B3FA59D997D1798654BD4129523D8326AA7F915DE29C397E8B56866220EDA4GCCDC0F63485933AC9F4791F9302CF5BA9C2279C2DE5B072CED3E416796585E262C976B7BC64893790E95C67923A87FCD437CB84A6FDBE9141FC079FFB64CD77A446EF3852C1F321D9A304134128D
	04FBEDE7F0EEF8EE0EA66F3045B1204CB9BFBF4766968E5598AA707673467D0673BC9BB7CF127C7E73D3A5B6F794B2C5BF97EBE1B2DDEEFBAFCCB95824C05E6708DD285E67C8E6E5833CE9834C3BB760DAFD53C9B6369B6DACB4EDE0B224653876E6F3F101DB40F1F6B06415ACDC5FFCD4205E8731762FB66CBB99C4EC903E9C35C13B6EFB72CB3EF78478248D98B3EF1ED74FBDF67477BBBB6DFC5AAD7BED64976AFED560EE7EA28F2E90217D3086345A3C687B9FFE41ECE573F299AD7B6F4E355BA47DBA235EEA
	77B85DB8352BBD4D9BC40B339616860DD0FD35B46C9C365BCECB38D731E2857AABE5830EABDFB42877DB464CAA4A186E47B7260D42A7B59B955FB4BF33AFC3483E15E407C03E40952FC4DC9C61EEA3DC6AC62C679195EC3DEC34A753498BED6E9E301B87BCF6362030F8AC3CE90BB890B297AD4A117DD5328977DA9A167075A3243DG911B303CC7BED44742586445FEE3A1FA13A86718CD5AD878C4E27A4683FE52A60C0595DF4ED6454281703E252F9BDA904DD53E4C94EDC5CEA7FD190DEDC542B9536A95C3D9
	3FCB1BF19DF6C3BA6B9366C13A8C2091209DA0E4B35AF67C2BC177A36A3D8176A32207E4D754E61CC72FD9B9943E51447723A17145935F32C5DA3E891B15F9F96AE67D9CDD6C917C0247CA569631C710CD78C7B83ED00597FB87CD9C4F5225109731856B616799ACAEA6C3BA2B9CFDFF6FD7B3D4F1516C3477F9A8A6BAA456DFBA5D76EEFB9F755F9C5769F4734924D843D47336B58FE416115CDB527D4FFD6FC8B71E7B5E1DEEBC773DB7BD703A6D7EF4FFF3E3E15FAF493EBAG012BD90938D642BDC8B88761B6
	73369B826961041BDB44F0F110CEAC473A9D3B047B965279DB9177A44715C03A09F0871678DBF7B03E19320C8BDDF7889DA77D6A48162F973A56D7467187B2D84CF9A97D90CF1F11F0DC306DE0349E0B9B568E75FDD6B9304498F8085747FA987F6E179077EB0EDB1BA5E4885C91DE9FB70131DB5391B71E637E8838A599B8DF7BE8910BC907650CF3GF4CC584A682C0BF07CF6C1BA8CA096E05CD6314E96B65EF31A0DA5DDEECBAF044BC1172DD30A2BE03C6F97A1EFB1244B81DA813CG11GC26795A5EC1C04
	D9C8074D4A7AFA34E5C5B60BFBB1A7E033E5B18E08B945BF16286794D2253025942EB5G0266252DEA1AD61966BCDCA3GC47B8EDF04FB035100CB2E44722CEFE7FB03FC1C42C9FEC545AE375D3A53525D443DA34623A49E530C3F308A47230C45FC1F9352BDGA1D5B89EF9D761789BF37B8DD2D348A6437B9D49ADC59911D572F8D4250CC7920D3F4DC528B38D724B36B15C17C56A39A273DFB7543F992EA2BF56A71F35F1F6BFCB497F3DCFBE337733CCF57E3CA56AFC662377E4FED136A75F4332CDF9B2DAF2
	5C3B526F3D9C6FB6D1261172B5F55CA2A9A536BAC378FDED4E92355F6585C1C5459EF5F848F301CDCBF1FE11348DFD7F69B4667B7CEAF3EEB1C0D5B556675BEB8A0D657220927266D7605C22259A65DD4B7B7FF0C80F8298D30DFD4FB53A71A7387EA46A8279FEB3E4BEB12EA571D8CC2A566E153F447149555A3D721C45589EA6C1DE73F69C6FBCD94CFF23A11D881082A055409C9E202A864729DD19EC0D9EF06E5162F4B541C066D08DBD22FC070AAE6CFE460353B06EC67F6296A96E32D7537D4DE91BEBC43C
	3964EB58430BB8DE14EF5AD4D63EA8489B83108C1086103F8362E887166F2A4CD2D1BEB6A55F6171D8BAFB78EC9F8B47A6608A1AF502729E7452C0E5AB5F61EF2E729C0F65269D220E26917EE50E372A70226E2C9C4F5267A1AFF987466A228A2D7E3A1D0A4E561D7E74CFDB4A64B9F6EA6F454D6778701D5AFBF1A316603D389848AB28C57DAFB27749F17C2A6A774C4597FFEFF275C555A162620BAC7F733350AC63795995D9467333082C40FB1311D97E77A6EF4AD26FCD0A387EE89A3F8783F59181B09AA09E
	E0C2AD46743A6C95A2268D62395E5AED0365A53BEB65C17D85D37D17F54154416FF5192632313E436971B8FB19FC442F1A4A6A4B63F4F124402D1D4AFC84D3669E0FA015716B2732FE5AE16B62E8B10F5C3F946711E994F763F9FBE82C03F198A0248E7193B9FEA4244782CCA47C09E901E20549711757A36705842091BAAE8928A350DCB2DBA5479F0D7045FABE0E65541BF30B6AF11E9DDB097E292947724ED84AE2A3825231G89GA9GB98D183F18671BA0DD83508E908290C579B7994E0DA3E499EADC022C
	431F5DA986F9C2C71C218E662769867459FE64B3505EDBD35A6F21E97EE33AED5A60B1ED1B4677FEA096DB4E38EC82F7E29ADF6FF5F5755B286DB8279590EEF7FF33335F4E36EDF2F6317AB9392ACE36E9EC035E97F8DFFC1AFCCD821A940079F2FACA03B20E0B3933E7BB5B2F7A085DADBEE033D8D507D9C47E6BEBD99B5867E29674738C015FB31359087BF0EE274341B245FCFAC10EF6BE4D56E34DE03FF5974E396FA9B722B98F7991C473534DEA1A68DD02A6892EA93BF0CD50D821DF07FCD601FCD223D807
	94423534D12C89059F9F2BC4BF56B59A7B91FD442095E89C0D0A9FC39A35FEDCD8296CCB32B588ED614B79F9F3FC57A8CD168E7271325D4C17F536FE1E8B0E6619B25F51F93EF9A4D8645FF90A55CF3D57526BF28821E2CF38F929FA9E9EAE17FDF4A36E99CFAAD767474B7913A87F58D623FCE9B766FF3055C87EBC4A5F3554083FE4B76E790DDC05F368AA40AD63E511789E1A78CB5DEE04736CE6E76D3E62D5AB66F52F64633C2E684E9B253A97DECDDFA9994F6B62F60BFA4E59232C158ADFCC1557CA7BF2F2
	64F6D333876774437218EF3D100EG085E03363FDE0E34E309F6CEA523CD03F441DEA867DEDCCFF4577BD927A80DC642F54ADB1B51A7B5FBD16EADDB388D100EG085E0B6B514BABF07EBD96F0E9FB5136E7AAF04DD75604B27E41E9FBA09D8290590434E91528AF36A958BA00311757416BAC1C65FD0777CAFC56402A3598462A50F7B92D43D21AE83D5565E73D45E3D9702D5C0A7EB6FD0436DFCD38D642F5EDC57991047BF2A95245FF04635111556AF92E74B1446D470177FFC42CBDF3DC3DCFDCF043748C6C
	833474A5323C0D9FEB77090BE633FE27877021G51GE38112GA681C87B40960092009A00D6G87C08840F000D1GF1G897BF0DEB6EE669A389E361C505F85G243C9141A7E7EE0B58E794BEE849514E751377A9BE183C4F579FB94DF8266A2F3CCD94B5EB579A995399FEE333F62D911607EB0DA6480BED46BAF82BDC2BFF5EFEC5E749FEDF7DD57BD17FE53C1DB56F97FAF2C97F359C6FD061057EC20EE769B0A6E3BF6AFFFBF32035D67381DF7D5683287FDF653CBD9E501ECDB3F1FC58816D593468ADF8B6
	AD9272269CC07D5F512D35868341F7638572A58D19CF146B9477DF7BC16D3E40D9616F035AFD014CCA6C173C10B761A016779F15EA7DB5AD665C161640EDCE700587DC9377984890FCA5DB55FC67E53A8879BA4AAF5F75BEFCF1B2DD02FCCD764BF7F2EBA0BBA77B653B4427FCB90764331472356C101ECF4C537BD633FE2D6E10703F4075F3DC1B0C937D4DB0F6FBD71ED72EABE0FBF106FD852C6276C0773B2F63042D5353E269A61E32597E673CDBE68FBE672D19ED3C0EEB182DDF476D1DED3C0E5BBFDB3D0E
	933843335978518D7DDBAB519DA75CD14235F3198CF744EE7574903E0B5B45700DB62690718EEE57E90EEB15F127D43823F8DFFCB61BF7C211FB1CEE64BDB71B5D7B6E32BB9CA27D9527317741B41E7C914AED343BACAA3B3FCD3823D417A779FC448BF599FE8857E6FF4A4535D9AC6172727899CCC8279C4236B605773913A1DDF0581CDBFC9871B93C0F2E02F4BBC0A8613F1B27DEBBB17A217493BEF12E0A5782D90E5071C39F9D0156260301FA6D0B6BB8610B6C2D68336843681F2FAA50E789043B2982FD16
	CABE882FE4F370A910AEBAE24E5D8850F28473C670BCAF24C7820C85C8263C2515D82EA9AA3FB1FEFF76378448D3C3916958C2BA54E58F64C323B52C91E4136F7236201F24D674537B9CD78C6972D63425281C45F89D25D771F4BB25FF3C655F3D0F46F47976190F7D5BF5E87DBADBBADDA9516F90556F5C4A0B3F973962226A90F5BB2FCABFFEAC2CD20F9F82F7AB6D1705D0DCEC676DAC915249045B331D5F0BE56923083BFBBB7296906EAE4E5B8EE99761F6F13AD0C88FA7DC8C71C691EE58D64425926E0625
	E8CB4DB144C59FC5DBDA8817FB8CF95B0F615A43320A75076C3C255D626043034097F9E7A8F89C9E64357A309C7DFE43885A6F08BDA6769B6261BA81006D9DCEBE46E852738A835409103D1C77659ECBC70B5D633009F5566F5972CB4E3F22155152DE0D7C4C11481B484FB46F2E2FD36D51DCDB4A463883B68FBBE545E9453ECA65F46D5E8B33336A38B93779B86E3D3C3745082687724F934D8F4E9A518C077CE8G619BB35DDB9DFBDC78E6BC63BFAE76D405EF2E2F88649B6107B97395BF04FF4C763EBFF6FA
	6CDD76CE79098771EC19488FE9E6753A9346E1D8CA2A7C311E1FB5BF8813187ADE0B5D41CFEC6EF4FA096FA7BB5973FF72F394B6191F28C4191F2A54161915A32B4D1CDB5806FE09F85F08268C726B5AF0FFE47E96757EC8DB1B2089257C5FEDB64A0FECC31FAD5FB2F8BC6D58220F27337FD27CD83305510ABDA9BCF609E79A0F786C8F4534097D6F24B65CBF7B45D25CBFCBE9530FE7CC3EFA7FEC8A5094FCA2764FD89BAB7EC43BFFF6F8AB1FBBB97BBC0ECF18FC87EF6BE5703B6A935C6B33ECE7B49E30D414
	657B2C3AA4E14F235B511E58CF865F4FE33620BD8CBFCEE74FB80DBDAF0ABE8FFECFA0FBBE5558B336D03167E132A77FD37F7B347E6CB1FD2A58A3640D580A72CA8D64219CFC262466D3DFFF3728640972BC4C7B4DF640BBG047DE98B957BCD47D05FC883FD59D2E0FF46FE2ADD97BE18036B42B110B779D35C1F494D457D2A524FF03FEACBAE3FEF896973G43BF43F391711F284F8E18563E15EEF4F6A06D130599B11F891B93BFD376DFEAE82F2FFFBA6E89E535234E6EA3FCEE8C6996GC7BB5A774996248D
	A55A146D0CF68C2413GA6916D2F4F12DC8B52DE1F4965C23A8540E1417D3B43DB519F21009BE3C199BF580A7E18CCB292B8EDE987749FGED9DC81BDD057ABC9DC8DB4F1FED0B0174B80089C49B7FBE52CEA65A1AFAAE3793648234F56238D8FD182DCB8C766428AF94B21B48B71ECE5473AF4245103C273E70A30FBA9D417B5DF25C3F1BC8725ED80AFB14C556216ED136AFD6FEDFE21228EDD7EFE777B27BEC27CDF4AE3F819AFA498AAB0C774E6E95252632D2537A0DEBF05FAF6A34FA5F2FBCEF395F7321AD
	D691EFBD72356D2F0B64327DEDBB16A34A0A6D6D4D650AFDA3D6A976D586ED1F10BD19E40FA759E3D6B3F9A27F092D18DFE0B376216079DD09E2CF65DA451E11C7033167C332E77FB15457E8C3FBBEE55B9CB8FF576DBD86B7AFFE7E8279E666DE585E571D3C9F8A31E9773EC6F3FC38CD5B47ED4B45B69D89F9D3ECF8A626FC9D9B985A3B20BF8308820883C8G1858057B38D50B18737D6D630A13E44AB62EA817F73A3ADCE9A0AB3FDB692BCD5D7AFEDB583F3D156F55F68B7B17907EB047FBD5F8D12EDE5EB7
	327498480B6E4135DE4796357E61F64833ABBA47587D69EF2FE172926DC24F8C42BBB9FE328AAF743F457BDB16AEBC81F32493287F37E7557AD3811FF5D251D9F8521F7E19D93C5FBB29553F04631BCFEA750F4CC47D9E48CBBE097A5B826E79790F47939748F76B1079C439EA787D1FA607B60EF7F3FC2BC39B474F5158E4053CF88716B72FCA2D3F349756DC3D0A0FDBFA7D797B67FCCC6A69556E59BF4A71213D5ABD7B3DA65C330F023C2CBE547F547BEA7DBD00BF5F27680C687327BF3F01499B55A774ACA6
	7C8A0E1F28428B7D8DFCDCE469E2A774DDAE543F5FA75EA73864F30972B56B143FBD5E45BEFC05B2DD29FC2D714BF72D6E9E099ADA7C72257A6CA977G5DF983DAB508FE6A3819755B91C09BF38A7B25F8939B887C75CB4DEE274B66761C91D87E6410EC43146DEA9B92C0DE72A0B6883EF7FC78A683CF0EBB983EE7FC5AC69170148545F778181A2F8EF8DA03623B07CF8D446FDF28FE5766DE7C7D21360B2AC3547B0EF57E777D9B578D3E6F3FFF1D713E7F11F57AFD7F36F5467B7E9D6B7467372E62F3F78754
	D1089B77F6E64EE038D1100EA35C5B33F03FE7029B5BEFBDFF0FC0E93FB9378940819086908D10G108A1063B16716GB481D881C2GC68244F9284F67B246C3BA8D20F0401CDB8E508A608508840883988F1086D078B964833482F8BFC7997DC712E5DFB749FA557EAF1553517D0163509F044836335FA51E41E5B449B4A3D53489326EB0BFB6B05F4579111D63D16F7B7F184A4A660333B434386F2ABD2395AC30FEEB017CFBEE907473D5B10E389972555F1EB9AB4792BFF933652B0839B3FC6D9389759C73B9
	46E1D6B746E6A2613E6C41581CCCB151D5436608F527E19D86608508829883B061B4528CF01AD4C897F821FF83E886F081C4F811E67276F615FD0C1749D45B5C26122D46FBCF879FEFC2572C9A352E992A7AC448167DEAF486AFD8DD42E77FF596FD9B6BC5BF961DC15FA6916E7A33F43E13FCB20F1FC9EFBCE34E6D8188870885C8G18F486E9D6F21A7C33665CD200A6G87C0B8C04CD92439A533DDE39B13053FFBE439134E987B31518FBEC103973A6E5769D27B8C7DBA43C01E3ADDE941289EE6CB8D95927D
	5E277ABDFE6B459F4B7EF2D1F50818D84E77C992209E134FE21CC4EDE3382CF350C71FC35CB23A67D0F58E6B7C17A6F66FAF9C5231G89G13A8AF0167651F071881E882F01C2739E240F7DD444AF27C65078748CBA07DA7826A409871179F688C71B4725D4477315D051FC7BFFDDA0A6DAE16F06357216F124887F39AB64235788BF3EEA30083A082A096A089A08DE05E17500F81B48338GA2GC6FF097CA51CFFBC2453G8A3F0231942095408B90899087B09EA08D20B0847281DAC3103F29BE71826306592C
	2BB3839C3357083F4CG0FE5D66318CFD8399BE5DFA01E790379EA34019C795C4885174D285E0D465F05FE780D46D9568FE947F6E66782B98EE72B70736538DCECA0C744D28F9FC73DD00FE1A198DF5BF8FB9D896998427DE09B46DC9C61BEA45E1CEFA14ECC3CC5043B0AF8CB887723F244F989B7ED197EBEE97EB275FDD2791C091959CF4FE3082590BE3FEBE677D2647B0976337C0E55C1A06C6B96723ABA74BACEF6686F45965468694C86F42FED55533D31D5CF57FBCCCF67BE2626937BFF37CFE777BCBB1C
	EE7EG30382FF7BF5DC39C893E9A8D1082B085001DAF91FC6F4D505FAB2D1A01FC7306097B0125F02D99A678443D073C823C7750BA4C7F3D9C3C5761G1A10E14A596C61433477F2861671B36E767EFE9B3DD1C5FD7EBA3B1045A74BE936F8FA14135B489F7B518CD53E60443C3BBEE4F5FBF0203353565F6FA3DC3E2F65DA20532DBD9B3EE8FB21DE06661C77DB7C6C4ECE3773F43FCD3CF0C660BB163083DE4DCE374766E66FED83D74B860A7CBF71B3DABE792A828A1AA77AD8FBBCE86B6C77AD5B06DE666FDD
	EE6700CBF1191CF74B6E991807AF6CA13E91DF909FFB5041076F52E53355F9AADEF15F7106D66D39F19697D143703E688FD6E94F1D337C51145F18E9449FCF797B669865CF207C07AA0D6427D27EE5EB0C7233AE417C9E1F7B260517087C9A4A7FE409117E964AF7684E6D337C9E4A0F561D5BE77967A97F9A5D39FD169FC179579FD3670F126DCB207C6DC5C67C4914DFDDE444BF7992EC7391DB74FD458F69DEF5CE28B957845086608A956750844D3AEE24898B957DC294DCC74BB422DF382E877B050450416F
	29A6834D64D025DF4839D45BAF7C5D415AD44B3EC387AC9EF51B3D24020DE587EC7DB671E69BF5DF717C02C5BC4FE2EDF0BB4F5A7A8E583A6D7D5036144384F25BB9B3CB11CDD1AE7266F730B6DBE76B3574D9391603AEFB1F0A3764B89BBB45897875F23B3833C6DBAF4CAF73AE45FAB9E4B74AAF217CD9EE6DF98216DFC579AD2734754A721BA9FFD816BA5FFA29480F227C882B117E5117E27DAEBE07759BFF290801E438CE3ED41CC394B475E710A67FB2918345F0AD3FCC500898386BAC46C073E57E6F03A3
	D80146FB1992836117E9E3E06F023912FCC606573536EFBEBF13751F7B87EC8336FDDDDD9A92BC3B5041E8BE32F83918C667003BDFC7735A8733FC7428E32A24E80ED607CF1FF9636AB9DA79F23E707D51ACED1F417C95F99956CD7C27C679B1147F65E6237C31147F0E2E4FE179C914EFB64CCF217C1F9966CF398C7B0C72AAFD1F312F0A5E0BFD3918DF1440358A001D4394FCBF5B261FDFBC3D8D79DAAE97B16400EB084CA7E2E8B31D4D083CFC70FEA486E84682303353EC4E11F439B606DEAC61E3BC8C5E16
	EE5B4181F106DA93C743563335A2BF33F4060FFE2A7EA6E74EDC9D3FBABE3A8F4D7565257EC478D7DA2E2DBFD656644B517FD3AB347EE77913A93F7E28D1FECE98665FF8D45BB7307C22B0EC37B356224F4B42C4DDB5B25EB0F1DED650DCDE0AB41EB0D1AF61F00D12E9C43D7CF99D56CBDC18F13D60F30D8C9200A6B9CC291749E15AFAA9B231F64F4EF13103DC46733707177CCB2233E26AA9202ABF7E43478BFCE95453B09C974A667952E86601A725C2FD3EE60E17B2F33E0A46696E35F8F4F32B110D2C9F29
	6F33393B4F54D94E742B63E45CD216A74695EDDE7A5439BEF95A78FC40B457C72EB65FD4B8C7EB1BCF9F7540A6156D5A299FBEA3392D5000860BA8E24D56200DC5969F5237B1963F675146BA4B1FC779C7E7E867DFAC3F04722FB8E5A43F0A722FACB2125FCC792BEB35ED0165DBA97F1D55C6723D147F5D45C67961FE72D1FF94652F517555ACFFB46507984A0F277CEF9B66CF207CFA5D7C0E6527FE9B7B5C2C233E67622E666B6D2CAB607795C8735BBD3E676292582B7864F3E577EE43B6DEF345606774DA00
	2687E0390CBFFF05769C5BF12EBF8C70432F4033A6FFAED3745DCE7A467951172D52B79EE8A62974C9D7AA7A44FB0A9E24671673A14F84D083508E90F2A51E6D7A43F4FC66AB8AF0632F44F3D059A656D7E94FC1194540D7D1D16FE16FB35CA98F0372D91AF579F89E2CA09C4FED9D4F676FAB00F4BB00B79C75BEB1936F5BC7GAEBE9C4FD04D1905B2246F50592FD90C369C524DGBD5FC15A4A82BC3B93721D21BE17BB073DCAD4367B59D6BC8796C7321FDF05673BD28977D5A65215FC97F1174DC1DCBB615E
	29C45EC8425D3F86F1095FC59F4FBA38504047959572BBE0493E1B68995E1C2BD05E70A5748CB061EE25E7D63D047B9D7146926EF1FA9EB565AAFA7657616F9C9D766A027767F52843940172BE676FEEE27BF22D049BA8C29D9191D8674F5073057100132EC65D776715C9C13EBF111F8E96E1A774ACA84798A83F9A63E5FF97536302F498C05455287FD0B746DC9C60D22FC6DB3F5F0DB1D7F28D4AF8125336C35A8B90F18D52BE5A037A222FB10AB9C972FF462FE60392F33F2FC17FA4136CF0BB628A2FC5DC03
	9BF14D043B729456C1583568430F1ABED07B7024235BFC22133D0505BBCE29CD36FC7BF820374366D655E9964A9BCBBA9E33B29C5BEBC93B967DD7FB8E6B2FECB8344361E44FB9BAAFB89C7DE777B25A7110CE8548398EE5741E41BAB0812E75BA14F16F992C03086BD0462B1CB69E5249GE9C47B72D9BAA3F87DD04FD9F6EED46AA0024A5DF8BD4AFE77D34405906E5E4D080BA3DC993521D44259282D15FC8FF1EF912E7DFB680353D5741CAD6092097617D568031CC874411B1C3686522DG2EC8147157ED68
	47B0400D0DC4995555A8A30DE4B82A99ED5988C883340FC05AEEBA0B699DB1547E7332CD0A9F3750FBECE3C976368A6AE388F77EA862CCB7A06E3923485BFA8316ED53DA74CF84606209760B35B4B65C08E53B135316C33A992067C614F1F7A97AA784F0F1B7220C3F15220CD412715EBAC6DBB29246C7005611C8EBB223FFDCA3076A1FBBB6AB7E995900650EA659CFFB305C49040B18C16D7F7B08FB709435FF42BDD4C46D1FF01F56D29BA6DCD9897226916E114508AB7B01AF0E7176902E1DE2BE0AF0BFA35E
	71049B3B987B7E021B281E296FEF3C8967B92737CA924E2D57EF9B5A1961573651B8FF53D06B6186FE0FD80E59ED0A8DAB06E8438BE4C35C10ED3823DA0905BF16D37C5D046FB91C6F338F4A6C7B0B24FECF61AE1F7C59B23F12FF46A71F7559BB525579F77A64E72B78457D39B7FC4E669AC1C91451FD7FF78665DB2AC98B3E8F73AF6CEC68B3FCAED99725BD9BBA1E631B2334E7C3AD79588734C15E58A83C571AB873424E343EFC01FC8BE6DDD8F9C773F9EF714D5A725E4F71E5B7EB4B5BB08B4BDB85F9D1B7
	E3F9EB8A547A33EEB167965E924C3941DF377A1C2115F9EA64EB0B018C4177622A006FC1704B971A69739E8419AEC23E0E724BF7659C1F77A048F48972B559AF5FBFAB8359B959AF5FC3EB8271655C6A0FEF94BFA3DB2449AF1353F5FE79C6ACD16BEB15699C72B544AFDFF44077D18C774BB75167ACEF34CCB7C63EA67A65FB5A67DC6BC419AECD3E660F724777C0DDA0BBCDFE794E7177EBEE14739B65EB1BFCF541B5F414D13FF51673C59A48E59083783106FC4FEF3930FE7255C1790C7B49A2DE8FC9FE6C54
	C24AA8FC6F4BA3FC7DB3E514365FF81A63F3EE53769BC73A305F1887F95E5B305FB85EFDE17D1D7D8279EE99B21F7C0CC88F33FF42ED5A72B6F2FC0A2E3C4F52BAF0B264355ECE6F636ED16B9F8378445B0369A77FD8639377B2CF1AFC4D7F213F783D5A2E66B349F49B65EB23DF3E3DEEB5DF1BCC67122F21FE79A2CE8572EF24DF3E87328271457865CB302A79464AF4C972B5852ED3FEE854DEF61F3B30F6F6E4D0BE63F69641798A2203EBE74551584EDE62EB5C72E8ED5C3D4671F5515A38F3FAB16E1AA1EF
	F4B41D31BEF3E16D65DB9748770BA17309F29DB94B6CAF3CC3DB5EF60EAF3EC3DB5E57E92DDF86F9436F40723E6A73ACD49A6073FF94CCBB3BAEE09C1A8CE4883E2C4F8271ED744B770B006F5DEA744B3749E79CEC1369DC72B554AF5F891FF1B7D2260B112FE37D723D6D4317A453254857A9FE793E283A30F6F66520FC466DEC5C20FC46F1E761FB9D85F7EA636E8447975D290D3B372BB06ECAA0AF7CCEFA5750368BEBAFF9559746D7BBE4BED12EE43EDF132AAB6F6B9CBFC5D7DEF7B516B7A7461C6B084172
	BAFC1ECFCA847C4418E05AD95572C0F627994890FCBBFD5E5B16FF1720B349570DF779637B56D1B5DF23CC57A6DFDDFE79DEB89A484ED03FFC15EBAFAC6EF78F4AE79C77438665B30E03E5FC8FAB72AEED9C14F07C283B34F1F04FDA0C03D8481B77E30C03FBCBAFACFE9F3EC03EA9C36693659A4177613CBF561677960E8F7D31363CEB5751BDA4481BCC65FD4F6C33DE3A9B5A445D41447D4D8DEA3E9019E738FC0DB610A178FE6589D45EB1FE792E1B417892B5799365F41ADF3EC7FD66F17931F2BB132F9BE3
	7D71BD9670FB840DFE79BAEB83713579655BD42266F349F42172B552AF5F930B55FCB1B25DD8791A94A41F96D27C7259820E73D37C720D7151D7F00F20AB162F65777863FB0473B549796D7255A3DF4360BA729E237E67DB3C7EE28D64B29887787106FCEF780CFB1300C67AC9B06DE1028F5FBC60A9890AEFD240E7010D00BD3316A90D1DAB5EE565307759EA45D722C966732C2963BE1C6AF7F77E45FF6EA764BF2063973314B2BD2C7A5DB46B626B36DDD49D622C7E11827D797D36827DB97FB7DB7119082A1F
	60F38D637660B3914D04DB33971FFFE8A35CDFD6A1AE06F0B7E6223CA904CBCFC7F9857F8FF19D6948DBC438DB66A02F03F02DF310B704F0BF1F033C61041BDFC54F74926EB7EB90D7BA9AF177717B8167A1BD1CF0AB36220EA842DDC26F130BA1DC6C92AA9B61620ED2596EC55CC2FA1FDC9161FEC96F4AF3906EFF4AD15E9842BD369D65A592EEE787150DF0FF6076157E946A6D2708FB0EFFCF2E99526D045B12C9FA8977FC9D6246906E1AA274CB7E4F68B9E90EAB04F489616EBF42F0D510EEA25C0AD61457
	CA385F737BD451109EC3382BC9C7A2617E0B3E5DB601F0CFEFC65E52B844ED603E2A01F4AB619E6177089C108E0923BD25AE25ADA729FEFF52F5715B75677F819D75BDCA19BED67DFE217B626BCE3D28BA6438695177DBFF6C5177DB7F2C4136932E44C89E35E3428D4F23F6CC38E8BB35E342351A289D7F1C7A999335E342F538290FA25C17AB290FA25C6FD6D29FC5389F1E22D8A75C73D954C77D82F12F7233A067A19DCE38DE5ECE22A09DCB3867286D0CA15C1B5CD7797719F30B6FA33D67306D14936E2F27
	316DB45DC77BC4DEA51EA629FE8FF8AFFEECFD77BF20631333CA193AD43F7FFC66626B4EB9FB715B4E5F4F6A5B4E3BE775EDA706E22E1562E171E7080BA65C331B9117C638262D987F0DE390773DEA0C7FB64225D5A3EF8F613EC563DD9C615EA05E8442B5513356638917C7FACB6EC75C5F08370AF02D445BF8BF46F577B6252EDD2A5F6FD4DD7CFA1F336D626BF82DDA954B2A5F5E6A0B2F7B6A0B2AC31E0BED5747F558F6FDDC9FDC0EF59FC971F0348263A605F0E7785CF38A24739FC05C4BB49FADA4DC64D1
	646DA15C6F0877BC61EAB210B70CF0EF9D2598FE0076C9CA153AB12BFE7FEC6D452F27447F000E1B56A9E50AD67DDE3F6E626B5EF3D1F508BA2DB26B63F007D99F07F18D54D77D125E01DDC8FD95616EAC243E0AF0FF72D08C93AEE6864E1BD388B7E3A64A1BCC383E1948AB0DC55CCB275016E642DDDA023CD6423DD6023CAE423DCC730B3104BBDB0B380287293FAEC19D6D047336228E8F61FEC36B0490423D3298F99389773DA348BB11F0E9C710B7D5434B3FC77790624E531895C138DFF13AE4C82792AE1A
	4B4BFA581CBB6FE172A959E7A25C844E6B02F49861EA0BD05EF042DDCE72228917C2B60FA65C6BDCDE56B868E746D1BFD304BACA89778E793E1CF08FECC65EB042E5D46BE3F2DA35BEA65F5E2627CB5B2626934FCCAC926BD9109FBD8E4F92A6ECE7EDD9BCB3D133971F1998BF4E781989F69E8E1F191884B48CD64AF952A35AF797672C26F7A1BFE27C4C66B415BC53A3C29E3E3BF823CA1E30FF9CDF6F56G3E6991347F6CAA457EB369286F3C01BE6DF3E4DA7BA3F47A1E947B93000FA6FD5FDF2D68CB1CC37E
	7223EFFAGFD13F47EAA2DC2F9E9FE7C2576FF7E233E7EB2BD2A15173A065E9D7D2831BC35FDB59AF9ADAAF9621C67F3650C5E63F4BADA6CAE762018FC96F4EC897309787D6A9A45BFB7503B2B5B8D74E77B298FFC173467D1EDFD7C066F5104GBE7CD12C0F29EB95FDE5746E6AB1867A344F7CE86BA3D1673FDF533B4EA778711FDADE2A46FFD90FE96DAF66735C824097BD06767363EF323E239D282F6931417535BFA674ADC6FFBD262D2FA91AF7194F61732590208BFF8C1F59FABD4377192D26F50ABD9FE7
	12BF8D6C51BEAF1B88B48C501F939F537AF3D29D4ACB75D3BEB5E4458BF9684F42F8C51E3055E916282CF9CBFC4BEA6231D28CBCE571D85624A33EE53DC675FC5A56D60A5578411FCF531655932F757DBB0BD9ECFFEE6F37F7B8ECF27D9750F759C362317E6FD5BD33163B84ED98EEE0C3264606E80DFF4644EB636DEF1BD1DFBC79E0CFB96BC3BC6C519FF6C61BE8BB787AED825025906D04ED59C8CBBE9F3FDEA21E55BB789E6263665C624731BC7F7321D21EBB7330BCAD0F8F6ED3EDFB6CF9DCDB1EE7795E0E
	8770A124EFE53922AF51C45F827023CF5D06347A466974DD4977039281BF0174B5ECD5747DF5A50DEFCF786FEF300FB3814D46A704BE7E1C4C935A767140A97217013CCC49378F6BF942373FB92F13B7A98B65059948D377A58CC62A647167CF1E5046745F2D3EED2C40467CB38668621F401889AD72EDE3768A45DF2F9751786647DF53D47E42EF8BE07D64BF2935A74B1E476DF15B3A5D4E01BEAB56DB910F13C220AF869038C43ECFD6876966A731AE43AB955B9EA6DF05BCB9F85B9B7E242F6F231F547A7E43
	4FD05EE8BF7254902F11B7C1A7AFE5B3FDF7430FBCF5FDCE5148ABC8504676G9F0B0AGDF12007E383BCA7147ACFAD7C2EB42505B2EC327CF3CE34B8B78D05277C935222F13740D7623AFB3003EF81D3E8D3CFCC900CFA6FDA12A392E137495BCB57439C271D35A7AB9319C651599485356F79D5034BE25540F63A92D7D43445E8660C31F22F76C247B362DDBEB14727CB74DFDC77BA90F5A072D32FE721FCE7F03FC2D108478642750FF0F6ED0749D49A07FBDBDF8FDE97595BF2D75DFB64D5D4A8C64E95BDF5D
	533E715D7A34567ED778BA5D8AF85753E87F6BBB957B9FAAC4FDD1FE6C8FB4F79F2D33BFFD003E4F6D47FE353CA40D7DA9CFEB67B679CC1C7C7B03FA456ECBE7221E4986FA2679319B6754B94FE87DB4150FF173GEFFA867DF420C151770F92FA6F4EB3C3DF93DA1F517A693FE98CF0994843B58E363D50E7FC7D942913F7F5AD4A9B65C71E3AEF0C5348CB5079E3006FF70C877CC4724740AE459F29B4CF1C17B8789A33A451579FD509DAFDF9FCCE58887816C454975928681BC57AC27D68D377D3119AFDB109
	DAFF25AD26F72D9848530EA389AAF9AC1D2C13F7AD0DCB137C48D37BC3FA56577F731E550E69FFDA6CBB471038FFCCC0D77AAC76033F544C636F5B2D782B187C55766C607D128BE898203FC2F5763C3D58F84D7B729E096A5041ED0CG5E114FE29D26B7A9B63DFB045EDF66472674GB6A56BEC120A74EB0B85343618C4B694FF2458D0C0EB8B6939417B57F94F899B301ECA1E53466D9FE9ED3171B99ADF77C9E4E35C1E21BD93FA0A1FC1615F0EF94E78DDG7218574CFE8B1E2D2B11A74410871F99FDF43F42
	5F502258785F5250EC7CD1BA6A0B23B25B8FA9329FB92248EE1AB3B45969F3D1F6AA49CEE9D54A3A121EAFAF79D52032CEBF2670ACFF9FF95A8372180FABBCCF52B703A2FF05F66CEFD34A78134F14B2468475FDA0250C967ABED0A2493E552268966FDCE579B9C9816B33C331E705CD31673E203E1F24FC2F680A8E54D793D0DFFD17226F669EC55F2399C3AB3FF86789FFC70021BE54B14E2E78E4C9BD72448634710FA7947E5ADE4546FB0E8C4D46932D28AFA5896B282BCF11373A0865F5D99C7DB63231E289
	72181E8FE463371D0A1C19DB54F2C47E9FCEA979BF8F6A19F2D61702F707C7E5E35E504ABCA18F6DEFFD9E4BEC9DD07C7948D70A6CA653905B75F2149DC132D3D4F5DD172948F62F9C1A6C5545A8BB5E0F5F7D47D8F71692E3FFBE05F224DF872A3FAB32D5F1190DBC6581F96E1A2A68941F7393E535948D2D2C0BA846DC3FC6BF724F1C4A322F192E488EBD4B1E51A73935CE0753ED6EF0D8BACF8A5A6533945ABF1D9EDA1FB1B98B6D088ED86E3A8245D71D1FA1CFF2C01E6E598A4F42AD48D378C240BA192378
	6035F9CA392E2C9E1AFFEBE8FCE9FE817D1B7BAFC5362DD0117D3BA14A9ECE3243C8F658C2C5F6C911A27B0DA14A0EA259E3C9765EA50A9FEF24761766470F7E5BC85FD2657793A645465E74215938AA83EDA89BEF748D405D450A6C78A14A8EA359BDA45B711ED27E4DB466C60D8F94C7D72DD26CF923C431273AF0E8763CDD087A4613BDB35FD76C79F0G738BFE93481E75EB951E1F4E243961EFD0E6CB29E22F58C397761EA9991A3D66B5B407A459E20FDB2EBB6AF762825A7BBF2AF720BDD4CBF33900BCCB
	B7AAE5B83BD9A90323F5E8E5D8FE146606AF861AD7FCFF0B727B9D55BBE42E9C629C210666886D817595D5AABED9D3C473CAC39E744976AA05E75CE61AAF866469582668FCE03BD22E11C3AC57A1AAD74EEF83156B159A657736DAC55F39A16AABA3FDB53F4558EB2FD364198D66DABBA89E3D06B622CFAE29D7647CF333DA0EFC1EF92F7A3912D810B56E3741BC0FD23DDA4DB79EF8A68545F7F73A1ACF1AE04E1DB7A1983E29F355FCA540D39594DF094F731A4D40E38D0AEF154F37863D40939E94DF224FF34A
	D140B3BAA83EAE1F6F5045834F04207872172979D201A76B25E0782E6AD0739582CFE9D0FC3E6F1329811E162078D6542B79FA0067FCD0FCCE1F7765C4G4F282078367B3C5FA58EF8920262FBA5CF4D178CBC1303621B6A73FCEF4E4B665C22170361ABADD6731581CFDDD0FC13FD1ED7EC859EC7D0FCAB32FD1E47841E61C171AD7371E7B4700C890A6F9D1F778BA4824F442078FAFC5EAF1086BC793F8B06EF294FFBE1CC4033B1A83EFA1FFE2291F85A02629B614367821E502078EE73610B841E18207822FC
	62ECAC70A4854537B6C34D1782BCD302627B0E8FDFC132B937B8B9983ECA1F7E2C9CF81A0262CBABD47335830FA7A83EAD83EA3EB0609999945FFDBE6F9F0B851EF1C171ED70F9DF55F8601994945FDA1FF6A43D82635FAB4170BD66733CF08970D48545374A67F97EE660318645D76F536FFA01A7BCA8BE334F7350D140B3BAA83E47FD1E970E871E89C1719D7331B395F832FE9F8C5FA69F3EC260A98D0A2F41277EEA0027E590BEF18E73BBFB75E7B32F592BBF43F9E8351E6E58EABD5D2369FA3AA75375F4CB
	666A69D64C5553EDB7B8FFDFE7F07EFE5B7BFA3A1D6F6B697EE8708D2AC9865F207A42609BD4430E6969D69AFC072BE4191E6EF603EFE95DE9700DAC718C2BAFDDD4261EEEEB3D1E2E3ADECFF7D92B1E6E4AD6BD5D61A5FA3A63CB74F4E9F9FA3AFF6499546FF2037ADD2E272BAD5653B5966B69263B75F485EE833FE49B78A5DBCF3759203C158665ADB010B75FC05ED71F6969C2BF5353ED5A22272B582227EBB308FB0BC15C479A505DEBC0B746006EC1833A1F99545B9803FA5B1921275B1521279BE5C0F707
	815D810376F644203D4DDA28271B37D0CF7771001E6E60001E6E3919FA3A97E66A69762C51536DDBE3609703385AE590D7AF576A69DE2DB568570E9A74EBC78D6245201DDB8C5AF9FD111EEECF111EAEF9331E6E8F1B75F4278C74FEEE20374900EE3F815DA7867E6B70719F7B6649DC69316CBFAD162418B7256CFE37B40B76B3F677755BBC7D491FEF2848FC75564C8CA9EB37B4D5FC6CFB3714453E583DDB4A669F745EADE5FA1CAE2F0BA9D1C37FA969CB097D4DD2E1E7F9243C0FEC9E1B5B44BE306511266D
	F331EFA576FB24696C163BC9FCE65EA3659F6CF45BDD9E937B06CA85A46BEC7D9C8134101CDFE22A771ACDDC9013E36E74B8CCEBC8212F99EEE9064FA64DA3B7EFFAAD6165437D1952545D52C2F67E60204D4DBF5BE83356D9BC1643B6F7BF28E910F2DDCEBBE4829ECA5C4FAF597D1EB38EDB13B42F6B006D54002D5FE3336E408FC2C29966B7B1CD9F30F747FF50E8696F71308F26D7B6C9D9FDD08C0FB4C7E4BBACFD5D9F082FC3D6F237256FE867F67379A258BB175B4B5FC0CFD2665EB76B3D65FBA646E5CA
	998DD21E43E931B6D8BA41D53B25DCCF0F5B565F63F4D83D2E6DB5DCD08EFFAC189F8F82F10B29782C30E666E7B27A24777B61B3D2E6B4E4408C50ADFD40A959F18BB3D1D8EBFBACEED0E5F36F8630004AE24A7B56D24F2A27F6ADE84BE007A0F8A1D99564FBDD9EE9EA1725576EB883BF6A7816982F1C828FD86B367475F7B95D3DE019C9E399E6118273350F17DDF7475F0FF1858A2BC7DAA6DC892C9FF45B1C3D9F6C686A327759C8G789544AFA1710C462C2119633DFA7E1E675F6D00B82813B2FB4B4A787F
	757C7F076C7FFA0ED94FB16BBF846726CA817F3479DEACB38F9EEADC2C7A4D8DD0031E975FFC7C165FD625CE0252B5C8591F338F0AC2A594A8D46213831745DC37E47A7064ABD99D39D828F8195CF9DEBF714E30F93A28E258197A70E068B9D4C9ACB51F13AE607F978D39480C2B0873AEA5CB0B83D99A48FEE1ED7E7F167DB5F23A540F7D9EE941C1DB1F55D45FEB31BBCC7C63DD76CE7E69D76889F6597AD8EB3119640FA119589721066C076030D2G6C57AA92AA94CF7AD87B7533F4079FCBBD52EC7125D7DE
	1FB8BC65B5B86CB6937B2E98A466481FF97521A8ECF6B31447E902BE48EA325A5DB656FB1FB1FB24CC33997E4F929FF0B549DFF02DE03D062D130D0B7CB3DAA8E7CECB0F4D64B96332191CDDA6F6965EE5A6927629ADFFA4393B6B6BCC87ED1E81681AF3DB769DB2310FD68271DC7EF5CA1378EA1809FF189030735820EA621F3F341A707B179E292064235A9653E93B27473443616A312C71C8597C0305B0C073AB89A8601FBAB46D6B6AD2D09FD93CA6C1453FEF8828996203F7A6EEBEA7919F48B349DF48631E
	E5CE14BD3B4957099EA8A7B912E1CF3B6D5060BA64865745B8BDCE72F01E3ABE7245F750CC6CC3E8E2FE419DA0A727B3D27189B4C82D51B954846387696996AD9D1A3D2EAA02677C7A35001E8B6D353FA6763B3EB163636921FBFAA8582FD5D93F95ECD92D9A7B05357F6BF301EF6B6D5F717574F444B73C7DC6FE43E70DD15E2F1FCFEF7F467409F7FDE3AC9D9DE8CEEBEC7FB720D4777B7AFF1E04EB8A37B4537738E57AED8BAEA938EA43C006145340D67DECE238DBDE4DB6C953BB789E114D8D34B041D3D66D
	1C3732C94AE0F35C458A3E1361C56EC11725136D1E64DAAD6E13DC82B3696163D2EE173B3B03AB70C899479B0E03DA1EE2590FB4C9F9B0B12CE51FC274FAE0092608EE36587BBCF20658F8115E85F0713F2FDF8772443FEBDA70358A2CDFFDE39A760B5F98CBDF79068F752FFFE3BC1D7A8D77F45A7F45BEA5BD7D7FE429723EA92DA2FDF6307E3FFE906C572AD40B3EB17EDFBE2478974FDDCB521A2F4AE5635B2E0E1252AE4D4840EFDCEA4F37643FABFFAB812F5208F476D047EC696F627EAE603D6F0A77934D
	15764CE4AF96BAECB93143655A69303A45ADBDC8D4D46C68709C6C35B89C9E0B63640EBE6B9E18196DBC633175833D60FDF7A6135F0FB4F226481BBA0B59FDC245737F81D0CB8788840643A1FABBGGA860GGD0CB818294G94G88G88G1BE910B1840643A1FABBGGA860GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG34BBGGGG
**end of data**/
}
/**
 * Return the DemandDaysLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDemandDaysLabel() {
	if (ivjDemandDaysLabel == null) {
		try {
			ivjDemandDaysLabel = new javax.swing.JLabel();
			ivjDemandDaysLabel.setName("DemandDaysLabel");
			ivjDemandDaysLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDemandDaysLabel.setText("Demand Days:");
			// user code begin {1}
			ivjDemandDaysLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_DEMAND_DAYS_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandDaysLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDemandDaysTextField() {
	if (ivjDemandDaysTextField == null) {
		try {
			ivjDemandDaysTextField = new javax.swing.JTextField();
			ivjDemandDaysTextField.setName("DemandDaysTextField");
			// user code begin {1}
			ivjDemandDaysTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_DEMAND_DAYS_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandDaysTextField;
}
/**
 * Return the DemandResetSpinBox property value.
 * @return com.klg.jclass.util.swing.JCSpinNumberBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.util.swing.JCSpinNumberBox getDemandResetSpinBox() {
	if (ivjDemandResetSpinBox == null) {
		try {
			ivjDemandResetSpinBox = new com.klg.jclass.util.swing.JCSpinNumberBox();
			ivjDemandResetSpinBox.setName("DemandResetSpinBox");
			// user code begin {1}
			ivjDemandResetSpinBox.setValueRange(new Integer(1), new Integer(5));
			ivjDemandResetSpinBox.setValue(new Integer(2));
			ivjDemandResetSpinBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(RESET_COUNT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandResetSpinBox;
}
/**
 * Return the DescriptionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDescriptionLabel() {
	if (ivjDescriptionLabel == null) {
		try {
			ivjDescriptionLabel = new javax.swing.JLabel();
			ivjDescriptionLabel.setName("DescriptionLabel");
			ivjDescriptionLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDescriptionLabel.setText("Description:");
			// user code begin {1}
			ivjDescriptionLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(SCRIPT_DESC_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDescriptionLabel;
}
/**
 * Return the DescriptionTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDescriptionTextField() {
	if (ivjDescriptionTextField == null) {
		try {
			ivjDescriptionTextField = new javax.swing.JTextField();
			ivjDescriptionTextField.setName("DescriptionTextField");
			// user code begin {1}
			ivjDescriptionTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(SCRIPT_DESC_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDescriptionTextField;
}
/**
 * Return the EnergyDaysLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEnergyDaysLabel() {
	if (ivjEnergyDaysLabel == null) {
		try {
			ivjEnergyDaysLabel = new javax.swing.JLabel();
			ivjEnergyDaysLabel.setName("EnergyDaysLabel");
			ivjEnergyDaysLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjEnergyDaysLabel.setText("Energy Days:");
			// user code begin {1}
			ivjEnergyDaysLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_ENERGY_DAYS_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnergyDaysLabel;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getEnergyDaysTextField() {
	if (ivjEnergyDaysTextField == null) {
		try {
			ivjEnergyDaysTextField = new javax.swing.JTextField();
			ivjEnergyDaysTextField.setName("EnergyDaysTextField");
			// user code begin {1}
			ivjEnergyDaysTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_ENERGY_DAYS_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnergyDaysTextField;
}
/**
 * Return the ExampleLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getExampleLabel() {
	if (ivjExampleLabel == null) {
		try {
			ivjExampleLabel = new javax.swing.JLabel();
			ivjExampleLabel.setName("ExampleLabel");
			ivjExampleLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjExampleLabel.setText("( Ex. MeterRead.ctl )");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExampleLabel;
}
/**
 * Return the FilePathLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFilePathLabel() {
	if (ivjFilePathLabel == null) {
		try {
			ivjFilePathLabel = new javax.swing.JLabel();
			ivjFilePathLabel.setName("FilePathLabel");
			ivjFilePathLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjFilePathLabel.setText("Flie Path:");
			// user code begin {1}
			ivjFilePathLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(FILE_PATH_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilePathLabel;
}
/**
 * Return the FilePathTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFilePathTextField() {
	if (ivjFilePathTextField == null) {
		try {
			ivjFilePathTextField = new javax.swing.JTextField();
			ivjFilePathTextField.setName("FilePathTextField");
			// user code begin {1}
			ivjFilePathTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(FILE_PATH_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFilePathTextField;
}
/**
 * Return the JCheckBox3 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getGenerateBillingCheckBox() {
	if (ivjGenerateBillingCheckBox == null) {
		try {
			ivjGenerateBillingCheckBox = new javax.swing.JCheckBox();
			ivjGenerateBillingCheckBox.setName("GenerateBillingCheckBox");
			ivjGenerateBillingCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjGenerateBillingCheckBox.setText("Generate Billing File");
			// user code begin {1}
			ivjGenerateBillingCheckBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_FLAG_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenerateBillingCheckBox;
}
/**
 * Return the GroupComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getGroupComboBox() {
	if (ivjGroupComboBox == null) {
		try {
			ivjGroupComboBox = new javax.swing.JComboBox();
			ivjGroupComboBox.setName("GroupComboBox");
			// user code begin {1}
			String [] collGroups = DeviceMeterGroup.getDeviceCollectionGroups();
			for (int i = 0; i < collGroups.length; i++)
				ivjGroupComboBox.addItem(collGroups[i]);
			ivjGroupComboBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(GROUP_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupComboBox;
}
/**
 * Return the GroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGroupNameLabel() {
	if (ivjGroupNameLabel == null) {
		try {
			ivjGroupNameLabel = new javax.swing.JLabel();
			ivjGroupNameLabel.setName("GroupNameLabel");
			ivjGroupNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjGroupNameLabel.setText("Group Name:");
			// user code begin {1}
			ivjGroupNameLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(GROUP_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupNameLabel;
}
/**
 * Return the GroupTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getGroupTypeComboBox() {
	if (ivjGroupTypeComboBox == null) {
		try {
			ivjGroupTypeComboBox = new javax.swing.JComboBox();
			ivjGroupTypeComboBox.setName("GroupTypeComboBox");
			ivjGroupTypeComboBox.setToolTipText("The type of group.");
			ivjGroupTypeComboBox.setVisible(false);
			// user code begin {1}
			for( int i = 0; i < DeviceMeterGroup.getValidBillGroupTypeDisplayStrings().length; i++)
			    ivjGroupTypeComboBox.addItem(DeviceMeterGroup.getValidBillGroupTypeDisplayStrings()[i]);
			
			ivjGroupTypeComboBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(GROUP_TYPE_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupTypeComboBox;
}
/**
 * Return the GroupTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getGroupTypeLabel() {
	if (ivjGroupTypeLabel == null) {
		try {
			ivjGroupTypeLabel = new javax.swing.JLabel();
			ivjGroupTypeLabel.setName("GroupTypeLabel");
			ivjGroupTypeLabel.setToolTipText("The type of group.");
			ivjGroupTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjGroupTypeLabel.setText("Group Type:");
			ivjGroupTypeLabel.setVisible(false);
			// user code begin {1}
			ivjGroupTypeLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(GROUP_TYPE_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupTypeLabel;
}
/**
 * Return the IEDPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getIEDPanel() {
	if (ivjIEDPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 12));
			ivjLocalBorder.setTitle("IED Setup");
			ivjIEDPanel = new javax.swing.JPanel();
			ivjIEDPanel.setName("IEDPanel");
			ivjIEDPanel.setBorder(ivjLocalBorder);
			ivjIEDPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsTOURateLabel = new java.awt.GridBagConstraints();
			constraintsTOURateLabel.gridx = 0; constraintsTOURateLabel.gridy = 0;
			constraintsTOURateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsTOURateLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getIEDPanel().add(getTOURateLabel(), constraintsTOURateLabel);

			java.awt.GridBagConstraints constraintsResetCountLabel = new java.awt.GridBagConstraints();
			constraintsResetCountLabel.gridx = 0; constraintsResetCountLabel.gridy = 1;
			constraintsResetCountLabel.gridwidth = 2;
			constraintsResetCountLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsResetCountLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getIEDPanel().add(getResetCountLabel(), constraintsResetCountLabel);

			java.awt.GridBagConstraints constraintsReadFrozenRegisterCheckBox = new java.awt.GridBagConstraints();
			constraintsReadFrozenRegisterCheckBox.gridx = 0; constraintsReadFrozenRegisterCheckBox.gridy = 2;
			constraintsReadFrozenRegisterCheckBox.gridwidth = 3;
			constraintsReadFrozenRegisterCheckBox.fill = java.awt.GridBagConstraints.BOTH;
			constraintsReadFrozenRegisterCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsReadFrozenRegisterCheckBox.weightx = 1.0;
			constraintsReadFrozenRegisterCheckBox.insets = new java.awt.Insets(4, 4, 0, 4);
			getIEDPanel().add(getReadFrozenRegisterCheckBox(), constraintsReadFrozenRegisterCheckBox);

			java.awt.GridBagConstraints constraintsTOURateComboBox = new java.awt.GridBagConstraints();
			constraintsTOURateComboBox.gridx = 1; constraintsTOURateComboBox.gridy = 0;
			constraintsTOURateComboBox.gridwidth = 2;
			constraintsTOURateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsTOURateComboBox.weightx = 1.0;
			constraintsTOURateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getIEDPanel().add(getTOURateComboBox(), constraintsTOURateComboBox);

			java.awt.GridBagConstraints constraintsDemandResetSpinBox = new java.awt.GridBagConstraints();
			constraintsDemandResetSpinBox.gridx = 2; constraintsDemandResetSpinBox.gridy = 1;
			constraintsDemandResetSpinBox.fill = java.awt.GridBagConstraints.BOTH;
			constraintsDemandResetSpinBox.weightx = 1.0;
			constraintsDemandResetSpinBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getIEDPanel().add(getDemandResetSpinBox(), constraintsDemandResetSpinBox);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 0; constraintsJLabel1.gridy = 3;
			constraintsJLabel1.gridwidth = 3;
			constraintsJLabel1.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabel1.insets = new java.awt.Insets(0, 20, 4, 4);
			getIEDPanel().add(getJLabel1(), constraintsJLabel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIEDPanel;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabel1.setText("(MCT with Alpha)");
			ivjJLabel1.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMaxRetryHoursLabel() {
	if (ivjMaxRetryHoursLabel == null) {
		try {
			ivjMaxRetryHoursLabel = new javax.swing.JLabel();
			ivjMaxRetryHoursLabel.setName("MaxRetryHoursLabel");
			ivjMaxRetryHoursLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMaxRetryHoursLabel.setText("Max Retry Hours:");
			// user code begin {1}
			ivjMaxRetryHoursLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(MAX_RETRY_HOURS_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxRetryHoursLabel;
}
/**
 * Return the JTextField5 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMaxRetryHoursTextField() {
	if (ivjMaxRetryHoursTextField == null) {
		try {
			ivjMaxRetryHoursTextField = new javax.swing.JTextField();
			ivjMaxRetryHoursTextField.setName("MaxRetryHoursTextField");
			// user code begin {1}
			ivjMaxRetryHoursTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(MAX_RETRY_HOURS_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMaxRetryHoursTextField;
}
/**
 * Return the JLabel61 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMessageSubjectLabel() {
	if (ivjMessageSubjectLabel == null) {
		try {
			ivjMessageSubjectLabel = new javax.swing.JLabel();
			ivjMessageSubjectLabel.setName("MessageSubjectLabel");
			ivjMessageSubjectLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMessageSubjectLabel.setText("Message Subject:");
			// user code begin {1}
			ivjMessageSubjectLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(EMAIL_SUBJECT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMessageSubjectLabel;
}
/**
 * Return the JTextField41 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMessageSubjectTextField() {
	if (ivjMessageSubjectTextField == null) {
		try {
			ivjMessageSubjectTextField = new javax.swing.JTextField();
			ivjMessageSubjectTextField.setName("MessageSubjectTextField");
			// user code begin {1}
			ivjMessageSubjectTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(EMAIL_SUBJECT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMessageSubjectTextField;
}
/**
 * Return the MeterReadPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMeterReadPanel() {
	if (ivjMeterReadPanel == null) {
		try {
			ivjMeterReadPanel = new javax.swing.JPanel();
			ivjMeterReadPanel.setName("MeterReadPanel");
			ivjMeterReadPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjMeterReadPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMissedFileNameLabel = new java.awt.GridBagConstraints();
			constraintsMissedFileNameLabel.gridx = 0; constraintsMissedFileNameLabel.gridy = 4;
			constraintsMissedFileNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMissedFileNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getMissedFileNameLabel(), constraintsMissedFileNameLabel);

			java.awt.GridBagConstraints constraintsFilePathTextField = new java.awt.GridBagConstraints();
			constraintsFilePathTextField.gridx = 1; constraintsFilePathTextField.gridy = 3;
			constraintsFilePathTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFilePathTextField.weightx = 1.0;
			constraintsFilePathTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getFilePathTextField(), constraintsFilePathTextField);

			java.awt.GridBagConstraints constraintsFilePathLabel = new java.awt.GridBagConstraints();
			constraintsFilePathLabel.gridx = 0; constraintsFilePathLabel.gridy = 3;
			constraintsFilePathLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFilePathLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getFilePathLabel(), constraintsFilePathLabel);

			java.awt.GridBagConstraints constraintsSuccessFileNameLabel = new java.awt.GridBagConstraints();
			constraintsSuccessFileNameLabel.gridx = 0; constraintsSuccessFileNameLabel.gridy = 5;
			constraintsSuccessFileNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSuccessFileNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getSuccessFileNameLabel(), constraintsSuccessFileNameLabel);

			java.awt.GridBagConstraints constraintsMissedFileNameTextField = new java.awt.GridBagConstraints();
			constraintsMissedFileNameTextField.gridx = 1; constraintsMissedFileNameTextField.gridy = 4;
			constraintsMissedFileNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMissedFileNameTextField.weightx = 1.0;
			constraintsMissedFileNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getMissedFileNameTextField(), constraintsMissedFileNameTextField);

			java.awt.GridBagConstraints constraintsSuccessFileNameTextField = new java.awt.GridBagConstraints();
			constraintsSuccessFileNameTextField.gridx = 1; constraintsSuccessFileNameTextField.gridy = 5;
			constraintsSuccessFileNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSuccessFileNameTextField.weightx = 1.0;
			constraintsSuccessFileNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getSuccessFileNameTextField(), constraintsSuccessFileNameTextField);

			java.awt.GridBagConstraints constraintsBrowseButton = new java.awt.GridBagConstraints();
			constraintsBrowseButton.gridx = 2; constraintsBrowseButton.gridy = 3;
			constraintsBrowseButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getBrowseButton(), constraintsBrowseButton);

			java.awt.GridBagConstraints constraintsPorterTimeoutLabel = new java.awt.GridBagConstraints();
			constraintsPorterTimeoutLabel.gridx = 0; constraintsPorterTimeoutLabel.gridy = 2;
			constraintsPorterTimeoutLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPorterTimeoutLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getPorterTimeoutLabel(), constraintsPorterTimeoutLabel);

			java.awt.GridBagConstraints constraintsPorterTimeoutTextField = new java.awt.GridBagConstraints();
			constraintsPorterTimeoutTextField.gridx = 1; constraintsPorterTimeoutTextField.gridy = 2;
			constraintsPorterTimeoutTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPorterTimeoutTextField.weightx = 1.0;
			constraintsPorterTimeoutTextField.insets = new java.awt.Insets(4, 4, 4, 100);
			getMeterReadPanel().add(getPorterTimeoutTextField(), constraintsPorterTimeoutTextField);

			java.awt.GridBagConstraints constraintsSecsLabel = new java.awt.GridBagConstraints();
			constraintsSecsLabel.gridx = 1; constraintsSecsLabel.gridy = 2;
			constraintsSecsLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsSecsLabel.weightx = 1.0;
			constraintsSecsLabel.insets = new java.awt.Insets(4, 4, 4, 65);
			getMeterReadPanel().add(getSecsLabel(), constraintsSecsLabel);

			java.awt.GridBagConstraints constraintsGroupNameLabel = new java.awt.GridBagConstraints();
			constraintsGroupNameLabel.gridx = 0; constraintsGroupNameLabel.gridy = 1;
			constraintsGroupNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getGroupNameLabel(), constraintsGroupNameLabel);

			java.awt.GridBagConstraints constraintsGroupComboBox = new java.awt.GridBagConstraints();
			constraintsGroupComboBox.gridx = 1; constraintsGroupComboBox.gridy = 1;
			constraintsGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsGroupComboBox.weightx = 1.0;
			constraintsGroupComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getGroupComboBox(), constraintsGroupComboBox);

			java.awt.GridBagConstraints constraintsGroupTypeLabel = new java.awt.GridBagConstraints();
			constraintsGroupTypeLabel.gridx = 0; constraintsGroupTypeLabel.gridy = 0;
			constraintsGroupTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsGroupTypeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getGroupTypeLabel(), constraintsGroupTypeLabel);

			java.awt.GridBagConstraints constraintsGroupTypeComboBox = new java.awt.GridBagConstraints();
			constraintsGroupTypeComboBox.gridx = 1; constraintsGroupTypeComboBox.gridy = 0;
			constraintsGroupTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsGroupTypeComboBox.weightx = 1.0;
			constraintsGroupTypeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadPanel().add(getGroupTypeComboBox(), constraintsGroupTypeComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterReadPanel;
}
/**
 * Return the MeterReadSetupPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMeterReadSetupPanel() {
	if (ivjMeterReadSetupPanel == null) {
		try {
			ivjMeterReadSetupPanel = new javax.swing.JPanel();
			ivjMeterReadSetupPanel.setName("MeterReadSetupPanel");
			ivjMeterReadSetupPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMeterReadPanel = new java.awt.GridBagConstraints();
			constraintsMeterReadPanel.gridx = 0; constraintsMeterReadPanel.gridy = 0;
			constraintsMeterReadPanel.gridwidth = 2;
			constraintsMeterReadPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsMeterReadPanel.weightx = 1.0;
			constraintsMeterReadPanel.weighty = 1.0;
			constraintsMeterReadPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadSetupPanel().add(getMeterReadPanel(), constraintsMeterReadPanel);

			java.awt.GridBagConstraints constraintsIEDPanel = new java.awt.GridBagConstraints();
			constraintsIEDPanel.gridx = 0; constraintsIEDPanel.gridy = 1;
			constraintsIEDPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsIEDPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadSetupPanel().add(getIEDPanel(), constraintsIEDPanel);

			java.awt.GridBagConstraints constraintsRetryPanel = new java.awt.GridBagConstraints();
			constraintsRetryPanel.gridx = 1; constraintsRetryPanel.gridy = 1;
			constraintsRetryPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRetryPanel.weightx = 1.0;
			constraintsRetryPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getMeterReadSetupPanel().add(getRetryPanel(), constraintsRetryPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMeterReadSetupPanel;
}
/**
 * Return the MissedFileNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMissedFileNameLabel() {
	if (ivjMissedFileNameLabel == null) {
		try {
			ivjMissedFileNameLabel = new javax.swing.JLabel();
			ivjMissedFileNameLabel.setName("MissedFileNameLabel");
			ivjMissedFileNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjMissedFileNameLabel.setText("Missed File Name:");
			// user code begin {1}
			ivjMissedFileNameLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(MISSED_FILE_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMissedFileNameLabel;
}
/**
 * Return the MissedFileNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMissedFileNameTextField() {
	if (ivjMissedFileNameTextField == null) {
		try {
			ivjMissedFileNameTextField = new javax.swing.JTextField();
			ivjMissedFileNameTextField.setName("MissedFileNameTextField");
			// user code begin {1}
			ivjMissedFileNameTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(MISSED_FILE_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMissedFileNameTextField;
}
/**
 * Return the JPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getNotificationPanel() {
	if (ivjNotificationPanel == null) {
		try {
			ivjNotificationPanel = new javax.swing.JPanel();
			ivjNotificationPanel.setName("NotificationPanel");
			ivjNotificationPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjNotificationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsMessageSubjectTextField = new java.awt.GridBagConstraints();
			constraintsMessageSubjectTextField.gridx = 1; constraintsMessageSubjectTextField.gridy = 1;
			constraintsMessageSubjectTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMessageSubjectTextField.weightx = 1.0;
			constraintsMessageSubjectTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getNotificationPanel().add(getMessageSubjectTextField(), constraintsMessageSubjectTextField);

			java.awt.GridBagConstraints constraintsNotifyGroupLabel = new java.awt.GridBagConstraints();
			constraintsNotifyGroupLabel.gridx = 0; constraintsNotifyGroupLabel.gridy = 0;
			constraintsNotifyGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNotifyGroupLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getNotificationPanel().add(getNotifyGroupLabel(), constraintsNotifyGroupLabel);

			java.awt.GridBagConstraints constraintsMessageSubjectLabel = new java.awt.GridBagConstraints();
			constraintsMessageSubjectLabel.gridx = 0; constraintsMessageSubjectLabel.gridy = 1;
			constraintsMessageSubjectLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMessageSubjectLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getNotificationPanel().add(getMessageSubjectLabel(), constraintsMessageSubjectLabel);

			java.awt.GridBagConstraints constraintsNotifyGroupComboBox = new java.awt.GridBagConstraints();
			constraintsNotifyGroupComboBox.gridx = 1; constraintsNotifyGroupComboBox.gridy = 0;
			constraintsNotifyGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNotifyGroupComboBox.weightx = 1.0;
			constraintsNotifyGroupComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getNotificationPanel().add(getNotifyGroupComboBox(), constraintsNotifyGroupComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNotificationPanel;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getNotifyGroupComboBox() {
	if (ivjNotifyGroupComboBox == null) {
		try {
			ivjNotifyGroupComboBox = new javax.swing.JComboBox();
			ivjNotifyGroupComboBox.setName("NotifyGroupComboBox");
			// user code begin {1}
			DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
			List notGroups = cache.getAllContactNotificationGroups();
			for (int i = 0; i < notGroups.size(); i++)
			{
				LiteNotificationGroup lng = (LiteNotificationGroup) notGroups.get(i);
				ivjNotifyGroupComboBox.addItem(lng.getNotificationGroupName());
			}
			ivjNotifyGroupComboBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(NOTIFY_GROUP_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNotifyGroupComboBox;
}
/**
 * Return the JLabel51 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNotifyGroupLabel() {
	if (ivjNotifyGroupLabel == null) {
		try {
			ivjNotifyGroupLabel = new javax.swing.JLabel();
			ivjNotifyGroupLabel.setName("NotifyGroupLabel");
			ivjNotifyGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNotifyGroupLabel.setText("Notify Group:");
			// user code begin {1}
			ivjNotifyGroupLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(NOTIFY_GROUP_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNotifyGroupLabel;
}
/**
 * Return the OtherPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getOptionsPanel() {
	if (ivjOptionsPanel == null) {
		try {
			ivjOptionsPanel = new javax.swing.JPanel();
			ivjOptionsPanel.setName("OptionsPanel");
			ivjOptionsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNotificationPanel = new java.awt.GridBagConstraints();
			constraintsNotificationPanel.gridx = 0; constraintsNotificationPanel.gridy = 1;
			constraintsNotificationPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNotificationPanel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsNotificationPanel.weightx = 1.0;
			constraintsNotificationPanel.weighty = 1.0;
			constraintsNotificationPanel.insets = new java.awt.Insets(0, 4, 4, 4);
			getOptionsPanel().add(getNotificationPanel(), constraintsNotificationPanel);

			java.awt.GridBagConstraints constraintsBillingPanel = new java.awt.GridBagConstraints();
			constraintsBillingPanel.gridx = 0; constraintsBillingPanel.gridy = 3;
			constraintsBillingPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingPanel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsBillingPanel.weightx = 1.0;
			constraintsBillingPanel.weighty = 1.0;
			constraintsBillingPanel.insets = new java.awt.Insets(0, 4, 4, 4);
			getOptionsPanel().add(getBillingPanel(), constraintsBillingPanel);

			java.awt.GridBagConstraints constraintsSendEmailCheckBox = new java.awt.GridBagConstraints();
			constraintsSendEmailCheckBox.gridx = 0; constraintsSendEmailCheckBox.gridy = 0;
			constraintsSendEmailCheckBox.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			constraintsSendEmailCheckBox.weightx = 1.0;
			constraintsSendEmailCheckBox.weighty = 1.0;
			constraintsSendEmailCheckBox.insets = new java.awt.Insets(4, 4, 0, 4);
			getOptionsPanel().add(getSendEmailCheckBox(), constraintsSendEmailCheckBox);

			java.awt.GridBagConstraints constraintsGenerateBillingCheckBox = new java.awt.GridBagConstraints();
			constraintsGenerateBillingCheckBox.gridx = 0; constraintsGenerateBillingCheckBox.gridy = 2;
			constraintsGenerateBillingCheckBox.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			constraintsGenerateBillingCheckBox.weightx = 1.0;
			constraintsGenerateBillingCheckBox.weighty = 1.0;
			constraintsGenerateBillingCheckBox.insets = new java.awt.Insets(4, 4, 0, 4);
			getOptionsPanel().add(getGenerateBillingCheckBox(), constraintsGenerateBillingCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOptionsPanel;
}
/**
 * Return the PorterTimeoutLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPorterTimeoutLabel() {
	if (ivjPorterTimeoutLabel == null) {
		try {
			ivjPorterTimeoutLabel = new javax.swing.JLabel();
			ivjPorterTimeoutLabel.setName("PorterTimeoutLabel");
			ivjPorterTimeoutLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPorterTimeoutLabel.setText("Porter Timeout:");
			// user code begin {1}
			ivjPorterTimeoutLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(PORTER_TIMEOUT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPorterTimeoutLabel;
}
/**
 * Return the PorterTimeoutTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPorterTimeoutTextField() {
	if (ivjPorterTimeoutTextField == null) {
		try {
			ivjPorterTimeoutTextField = new javax.swing.JTextField();
			ivjPorterTimeoutTextField.setName("PorterTimeoutTextField");
			// user code begin {1}
			ivjPorterTimeoutTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(PORTER_TIMEOUT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPorterTimeoutTextField;
}
/**
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getQueueOffCountLabel() {
	if (ivjQueueOffCountLabel == null) {
		try {
			ivjQueueOffCountLabel = new javax.swing.JLabel();
			ivjQueueOffCountLabel.setName("QueueOffCountLabel");
			ivjQueueOffCountLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjQueueOffCountLabel.setText("Queue Off Count:");
			// user code begin {1}
			ivjQueueOffCountLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(QUEUE_OFF_COUNT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueueOffCountLabel;
}
/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getQueueOffCountTextField() {
	if (ivjQueueOffCountTextField == null) {
		try {
			ivjQueueOffCountTextField = new javax.swing.JTextField();
			ivjQueueOffCountTextField.setName("QueueOffCountTextField");
			// user code begin {1}
			ivjQueueOffCountTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(QUEUE_OFF_COUNT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQueueOffCountTextField;
}
/**
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getReadFrozenRegisterCheckBox() {
	if (ivjReadFrozenRegisterCheckBox == null) {
		try {
			ivjReadFrozenRegisterCheckBox = new javax.swing.JCheckBox();
			ivjReadFrozenRegisterCheckBox.setName("ReadFrozenRegisterCheckBox");
			ivjReadFrozenRegisterCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjReadFrozenRegisterCheckBox.setText("Read Frozed Register");
			// user code begin {1}
			ivjReadFrozenRegisterCheckBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(READ_FROZEN_PARAM));
			ivjReadFrozenRegisterCheckBox.setSelected(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReadFrozenRegisterCheckBox;
}
/**
 * Return the ResetCountLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getResetCountLabel() {
	if (ivjResetCountLabel == null) {
		try {
			ivjResetCountLabel = new javax.swing.JLabel();
			ivjResetCountLabel.setName("ResetCountLabel");
			ivjResetCountLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjResetCountLabel.setText("Reset Demand Count:");
			// user code begin {1}
			ivjResetCountLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(RESET_COUNT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjResetCountLabel;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRetryCountLabel() {
	if (ivjRetryCountLabel == null) {
		try {
			ivjRetryCountLabel = new javax.swing.JLabel();
			ivjRetryCountLabel.setName("RetryCountLabel");
			ivjRetryCountLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjRetryCountLabel.setText("Retry Count:");
			// user code begin {1}
			ivjRetryCountLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(RETRY_COUNT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRetryCountLabel;
}
/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getRetryCountTextField() {
	if (ivjRetryCountTextField == null) {
		try {
			ivjRetryCountTextField = new javax.swing.JTextField();
			ivjRetryCountTextField.setName("RetryCountTextField");
			// user code begin {1}
			ivjRetryCountTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(RETRY_COUNT_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRetryCountTextField;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRetryPanel() {
	if (ivjRetryPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 12));
			ivjLocalBorder1.setTitle("Retry");
			ivjRetryPanel = new javax.swing.JPanel();
			ivjRetryPanel.setName("RetryPanel");
			ivjRetryPanel.setBorder(ivjLocalBorder1);
			ivjRetryPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRetryCountTextField = new java.awt.GridBagConstraints();
			constraintsRetryCountTextField.gridx = 1; constraintsRetryCountTextField.gridy = 0;
			constraintsRetryCountTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsRetryCountTextField.weightx = 1.0;
			constraintsRetryCountTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRetryPanel().add(getRetryCountTextField(), constraintsRetryCountTextField);

			java.awt.GridBagConstraints constraintsQueueOffCountTextField = new java.awt.GridBagConstraints();
			constraintsQueueOffCountTextField.gridx = 1; constraintsQueueOffCountTextField.gridy = 1;
			constraintsQueueOffCountTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsQueueOffCountTextField.weightx = 1.0;
			constraintsQueueOffCountTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRetryPanel().add(getQueueOffCountTextField(), constraintsQueueOffCountTextField);

			java.awt.GridBagConstraints constraintsMaxRetryHoursTextField = new java.awt.GridBagConstraints();
			constraintsMaxRetryHoursTextField.gridx = 1; constraintsMaxRetryHoursTextField.gridy = 2;
			constraintsMaxRetryHoursTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMaxRetryHoursTextField.weightx = 1.0;
			constraintsMaxRetryHoursTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getRetryPanel().add(getMaxRetryHoursTextField(), constraintsMaxRetryHoursTextField);

			java.awt.GridBagConstraints constraintsRetryCountLabel = new java.awt.GridBagConstraints();
			constraintsRetryCountLabel.gridx = 0; constraintsRetryCountLabel.gridy = 0;
			constraintsRetryCountLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRetryCountLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getRetryPanel().add(getRetryCountLabel(), constraintsRetryCountLabel);

			java.awt.GridBagConstraints constraintsQueueOffCountLabel = new java.awt.GridBagConstraints();
			constraintsQueueOffCountLabel.gridx = 0; constraintsQueueOffCountLabel.gridy = 1;
			constraintsQueueOffCountLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsQueueOffCountLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getRetryPanel().add(getQueueOffCountLabel(), constraintsQueueOffCountLabel);

			java.awt.GridBagConstraints constraintsMaxRetryHoursLabel = new java.awt.GridBagConstraints();
			constraintsMaxRetryHoursLabel.gridx = 0; constraintsMaxRetryHoursLabel.gridy = 2;
			constraintsMaxRetryHoursLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsMaxRetryHoursLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getRetryPanel().add(getMaxRetryHoursLabel(), constraintsMaxRetryHoursLabel);
			// user code begin {1}
		    // user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRetryPanel;
}
/**
 * Return the ScriptNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getScriptNameLabel() {
	if (ivjScriptNameLabel == null) {
		try {
			ivjScriptNameLabel = new javax.swing.JLabel();
			ivjScriptNameLabel.setName("ScriptNameLabel");
			ivjScriptNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjScriptNameLabel.setText("Script Name:");
			// user code begin {1}
			ivjScriptNameLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(SCRIPT_FILE_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScriptNameLabel;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getScriptNamePanel() {
	if (ivjScriptNamePanel == null) {
		try {
			ivjScriptNamePanel = new javax.swing.JPanel();
			ivjScriptNamePanel.setName("ScriptNamePanel");
			ivjScriptNamePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsScriptNameTextField = new java.awt.GridBagConstraints();
			constraintsScriptNameTextField.gridx = 1; constraintsScriptNameTextField.gridy = 0;
			constraintsScriptNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsScriptNameTextField.weightx = 1.0;
			constraintsScriptNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getScriptNamePanel().add(getScriptNameTextField(), constraintsScriptNameTextField);

			java.awt.GridBagConstraints constraintsScriptNameLabel = new java.awt.GridBagConstraints();
			constraintsScriptNameLabel.gridx = 0; constraintsScriptNameLabel.gridy = 0;
			constraintsScriptNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getScriptNamePanel().add(getScriptNameLabel(), constraintsScriptNameLabel);

			java.awt.GridBagConstraints constraintsDescriptionLabel = new java.awt.GridBagConstraints();
			constraintsDescriptionLabel.gridx = 0; constraintsDescriptionLabel.gridy = 1;
			constraintsDescriptionLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getScriptNamePanel().add(getDescriptionLabel(), constraintsDescriptionLabel);

			java.awt.GridBagConstraints constraintsDescriptionTextField = new java.awt.GridBagConstraints();
			constraintsDescriptionTextField.gridx = 1; constraintsDescriptionTextField.gridy = 1;
			constraintsDescriptionTextField.gridwidth = 2;
			constraintsDescriptionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDescriptionTextField.weightx = 1.0;
			constraintsDescriptionTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getScriptNamePanel().add(getDescriptionTextField(), constraintsDescriptionTextField);

			java.awt.GridBagConstraints constraintsExampleLabel = new java.awt.GridBagConstraints();
			constraintsExampleLabel.gridx = 2; constraintsExampleLabel.gridy = 0;
			constraintsExampleLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getScriptNamePanel().add(getExampleLabel(), constraintsExampleLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScriptNamePanel;
}
/**
 * Return the ScriptNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getScriptNameTextField() {
	if (ivjScriptNameTextField == null) {
		try {
			ivjScriptNameTextField = new javax.swing.JTextField();
			ivjScriptNameTextField.setName("ScriptNameTextField");
			// user code begin {1}
			ivjScriptNameTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(SCRIPT_FILE_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScriptNameTextField;
}
/**
 * Return the ScriptScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getScriptScrollPane() {
	if (ivjScriptScrollPane == null) {
		try {
			ivjScriptScrollPane = new javax.swing.JScrollPane();
			ivjScriptScrollPane.setName("ScriptScrollPane");
			getScriptScrollPane().setViewportView(getScriptTextArea());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScriptScrollPane;
}
/**
 * @return Returns the scriptParams.
 */
public ScriptTemplate getScriptTemplate()
{
    if( scriptTemplate == null)
        scriptTemplate = new ScriptTemplate();
    return scriptTemplate;
}
/**
 * @return Returns the mainCode.
 */
public String getScriptText()
{
    return scriptText;
}
/**
 * Return the ScriptTextArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getScriptTextArea() {
	if (ivjScriptTextArea == null) {
		try {
			ivjScriptTextArea = new javax.swing.JTextArea();
			ivjScriptTextArea.setName("ScriptTextArea");
			ivjScriptTextArea.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjScriptTextArea;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSecsLabel() {
	if (ivjSecsLabel == null) {
		try {
			ivjSecsLabel = new javax.swing.JLabel();
			ivjSecsLabel.setName("SecsLabel");
			ivjSecsLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjSecsLabel.setText("secs");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSecsLabel;
}
/**
 * Return the JCheckBox2 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getSendEmailCheckBox() {
	if (ivjSendEmailCheckBox == null) {
		try {
			ivjSendEmailCheckBox = new javax.swing.JCheckBox();
			ivjSendEmailCheckBox.setName("SendEmailCheckBox");
			ivjSendEmailCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSendEmailCheckBox.setText("Send Email Notification");
			// user code begin {1}
			ivjSendEmailCheckBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(NOTIFICATION_FLAG_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSendEmailCheckBox;
}
/**
 * Return the SuccessFileNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSuccessFileNameLabel() {
	if (ivjSuccessFileNameLabel == null) {
		try {
			ivjSuccessFileNameLabel = new javax.swing.JLabel();
			ivjSuccessFileNameLabel.setName("SuccessFileNameLabel");
			ivjSuccessFileNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSuccessFileNameLabel.setText("Success File Name:");
			// user code begin {1}
			ivjSuccessFileNameLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(SUCCESS_FILE_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSuccessFileNameLabel;
}
/**
 * Return the SuccessFileNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSuccessFileNameTextField() {
	if (ivjSuccessFileNameTextField == null) {
		try {
			ivjSuccessFileNameTextField = new javax.swing.JTextField();
			ivjSuccessFileNameTextField.setName("SuccessFileNameTextField");
			// user code begin {1}
			ivjSuccessFileNameTextField.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(SUCCESS_FILE_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSuccessFileNameTextField;
}
/**
 * Return the TabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getTabbedPane() {
	if (ivjTabbedPane == null) {
		try {
			ivjTabbedPane = new javax.swing.JTabbedPane();
			ivjTabbedPane.setName("TabbedPane");
			ivjTabbedPane.insertTab("Meter Read", null, getMeterReadSetupPanel(), null, 0);
			ivjTabbedPane.insertTab("Options", null, getOptionsPanel(), null, 1);
			ivjTabbedPane.insertTab("Text Editor", null, getScriptScrollPane(), null, 2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTabbedPane;
}
/**
 * @return Returns the templateType.
 */
public int getTemplateType()
{
    return templateType;
}
/**
 * Return the TOURateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getTOURateComboBox() {
	if (ivjTOURateComboBox == null) {
		try {
			ivjTOURateComboBox = new javax.swing.JComboBox();
			ivjTOURateComboBox.setName("TOURateComboBox");
			// user code begin {1}
			ivjTOURateComboBox.addItem("rate A");
			ivjTOURateComboBox.addItem("rate B");
			ivjTOURateComboBox.addItem("rate C");
			ivjTOURateComboBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(TOU_RATE_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTOURateComboBox;
}
/**
 * Return the TOURateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTOURateLabel() {
	if (ivjTOURateLabel == null) {
		try {
			ivjTOURateLabel = new javax.swing.JLabel();
			ivjTOURateLabel.setName("TOURateLabel");
			ivjTOURateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjTOURateLabel.setText("TOU Rate:");
			// user code begin {1}
			ivjTOURateLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(TOU_RATE_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTOURateLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val)
{
	Schedule sch = (Schedule)val;
	getScriptTemplate().getParamToValueMap().put(SCHEDULE_NAME_PARAM, sch.getScheduleName());
	
	sch.setScriptFileName(getScriptNameTextField().getText());
	sch.getNonPersistantData().getScript().setFileName(getScriptNameTextField().getText());

	// filter line separators in the script text area
	setScriptText( buildScript() );
	java.io.BufferedReader rdr = new java.io.BufferedReader(new java.io.StringReader(getScriptText()));

	String endl = System.getProperty("line.separator");
	StringBuffer buf = new StringBuffer();
	String in;

	try 
	{
		while( (in = rdr.readLine()) != null )
			buf.append(in + endl);
	} 
	catch( java.io.IOException e ) 
	{
		CTILogger.error( e.getMessage(), e );
	}

	sch.getNonPersistantData().getScript().setFileContents( buf.toString() );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ScriptSchedulePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(431, 563);

		java.awt.GridBagConstraints constraintsTabbedPane = new java.awt.GridBagConstraints();
		constraintsTabbedPane.gridx = 0; constraintsTabbedPane.gridy = 1;
		constraintsTabbedPane.gridwidth = 3;
		constraintsTabbedPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTabbedPane.weightx = 1.0;
		constraintsTabbedPane.weighty = 1.0;
		constraintsTabbedPane.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getTabbedPane(), constraintsTabbedPane);

		java.awt.GridBagConstraints constraintsScriptNamePanel = new java.awt.GridBagConstraints();
		constraintsScriptNamePanel.gridx = 0; constraintsScriptNamePanel.gridy = 0;
		constraintsScriptNamePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsScriptNamePanel.weightx = 1.0;
		constraintsScriptNamePanel.insets = new java.awt.Insets(15, 4, 15, 4);
		add(getScriptNamePanel(), constraintsScriptNamePanel);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getScriptNameTextField().addKeyListener(this);
	getScriptTextArea().addCaretListener(this);
	getScriptTextArea().addFocusListener(this);
	getGroupComboBox().addActionListener(this);
	getPorterTimeoutTextField().addCaretListener(this);
	getFilePathTextField().addCaretListener(this);
	getMissedFileNameTextField().addCaretListener(this);
	getSuccessFileNameTextField().addCaretListener(this);
	getBrowseButton().addActionListener(this);
	
	getTOURateComboBox().addActionListener(this);
	getDemandResetSpinBox().addValueListener(this);
	getReadFrozenRegisterCheckBox().addItemListener(this);
	
	getRetryCountTextField().addCaretListener(this);
	getQueueOffCountTextField().addCaretListener(this);
	getMaxRetryHoursTextField().addCaretListener(this);
	
	getGenerateBillingCheckBox().addItemListener(this);
	getBillingFormatComboBox().addItemListener(this);
	getBillingFileNameTextField().addCaretListener(this);
	getDemandDaysTextField().addCaretListener(this);
	getEnergyDaysTextField().addCaretListener(this);
	getBillingFilePathTextBox().addCaretListener(this);
	getBillingFileBrowseButton().addActionListener(this);
	
	getSendEmailCheckBox().addItemListener(this);
	getNotifyGroupComboBox().addItemListener(this);
	getMessageSubjectTextField().addCaretListener(this);
	
	getTabbedPane().addChangeListener(this);
	getScriptTextArea().addFocusListener(this);
	initSwingCompValues();
	// user code end
}
/**
 * This method was created in VisualAge.
 */

private void initSwingCompValues()
{
    getScriptTemplate().getParamToValueMap().put(IED_FLAG_PARAM, String.valueOf(ScriptTemplateTypes.isIEDTemplate(getTemplateType())));

    //The read_with..._param is for showing options to retry during a multiple read schedule, 
	//so we NOT the result which is from a retry script, one that is a read once type script 
    getScriptTemplate().getParamToValueMap().put(READ_WITH_RETRY_FLAG_PARAM, String.valueOf(!ScriptTemplateTypes.isRetryTemplate(getTemplateType())));
    
    getScriptNameTextField().setText((String)getScriptTemplate().getParamToValueMap().get(SCRIPT_FILE_NAME_PARAM));
    getDescriptionTextField().setText((String)getScriptTemplate().getParamToValueMap().get(SCRIPT_DESC_PARAM));
    getFilePathTextField().setText((String)getScriptTemplate().getParamToValueMap().get(FILE_PATH_PARAM));
    getGroupComboBox().setSelectedItem((String)getScriptTemplate().getParamToValueMap().get(GROUP_NAME_PARAM));
    getMissedFileNameTextField().setText((String)getScriptTemplate().getParamToValueMap().get(MISSED_FILE_NAME_PARAM));
    getSuccessFileNameTextField().setText((String)getScriptTemplate().getParamToValueMap().get(SUCCESS_FILE_NAME_PARAM));
    getPorterTimeoutTextField().setText((String)getScriptTemplate().getParamToValueMap().get(PORTER_TIMEOUT_PARAM));
    
    getRetryCountTextField().setText((String)getScriptTemplate().getParamToValueMap().get(RETRY_COUNT_PARAM));
    getMaxRetryHoursTextField().setText((String)getScriptTemplate().getParamToValueMap().get(MAX_RETRY_HOURS_PARAM));
    getQueueOffCountTextField().setText((String)getScriptTemplate().getParamToValueMap().get(QUEUE_OFF_COUNT_PARAM));

    if( ((String)getScriptTemplate().getParamToValueMap().get(GROUP_TYPE_PARAM)).equalsIgnoreCase("altgroup"))
        getGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.ALTGROUP_DISPLAY_STRING);
    else if( ((String)getScriptTemplate().getParamToValueMap().get(GROUP_TYPE_PARAM)).equalsIgnoreCase("bill"))	//N/A 20041208
        getGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.BILLINGGROUP_DISPLAY_STRING);
    else //if( ((String)getScriptTemplate().getParamToValueMap().get(GROUP_TYPE_PARAM)).equalsIgnoreCase("group"))
        getGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.COLLECTIONGROUP_DISPLAY_STRING);

    //Billing setup
    enableContainer(getBillingPanel(), Boolean.valueOf((String)getScriptTemplate().getParamToValueMap().get(BILLING_FLAG_PARAM)).booleanValue());
    getGenerateBillingCheckBox().setSelected(Boolean.valueOf((String)getScriptTemplate().getParamToValueMap().get(BILLING_FLAG_PARAM)).booleanValue());
    getBillingFileNameTextField().setText((String)getScriptTemplate().getParamToValueMap().get(BILLING_FILE_NAME_PARAM));
    getBillingFilePathTextBox().setText((String)getScriptTemplate().getParamToValueMap().get(BILLING_FILE_PATH_PARAM));
    getBillingFormatComboBox().setSelectedItem((String)getScriptTemplate().getParamToValueMap().get(BILLING_FORMAT_PARAM));
    getDemandDaysTextField().setText((String)getScriptTemplate().getParamToValueMap().get(BILLING_DEMAND_DAYS_PARAM));
    getEnergyDaysTextField().setText((String)getScriptTemplate().getParamToValueMap().get(BILLING_ENERGY_DAYS_PARAM));
    
    if( ((String)getScriptTemplate().getParamToValueMap().get(BILLING_GROUP_TYPE_PARAM)).equalsIgnoreCase("test"))
        getBillingGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.ALTGROUP_DISPLAY_STRING);
    else if( ((String)getScriptTemplate().getParamToValueMap().get(BILLING_GROUP_TYPE_PARAM)).equalsIgnoreCase("bill"))	//N/A 20041208
        getBillingGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.BILLINGGROUP_DISPLAY_STRING);
    else //if( ((String)getScriptTemplate().getParamToValueMap().get(BILLING_GROUP_TYPE_PARAM)).equalsIgnoreCase("collect"))
        getBillingGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.COLLECTIONGROUP_DISPLAY_STRING);
    
    //Notification setup
    enableContainer(getNotificationPanel(), Boolean.valueOf((String)getScriptTemplate().getParamToValueMap().get(NOTIFICATION_FLAG_PARAM)).booleanValue());
    getSendEmailCheckBox().setSelected(Boolean.valueOf((String)getScriptTemplate().getParamToValueMap().get(NOTIFICATION_FLAG_PARAM)).booleanValue());
    getMessageSubjectTextField().setText((String)getScriptTemplate().getParamToValueMap().get(EMAIL_SUBJECT_PARAM));
    getNotifyGroupComboBox().setSelectedItem((String)getScriptTemplate().getParamToValueMap().get(NOTIFY_GROUP_PARAM));
    
    //IED panel setup
    getReadFrozenRegisterCheckBox().setSelected(((String)getScriptTemplate().getParamToValueMap().get(READ_FROZEN_PARAM)).length() > 0);
    getDemandResetSpinBox().setValue(Integer.valueOf((String)getScriptTemplate().getParamToValueMap().get(RESET_COUNT_PARAM)));
    getTOURateComboBox().setSelectedItem((String)getScriptTemplate().getParamToValueMap().get(TOU_RATE_PARAM));
    
    CTILogger.info("Set swing component values");
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	
	if(getScriptNameTextField().getText() == null 
		|| getScriptNameTextField().getText().length() <= 0)
	{
		setErrorString("The Script Name text field must be filled in or the Use Schedule Name check box must be selected");
		return false;
	}

	return true;
}
/* (non-Javadoc)
 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
 */
public void itemStateChanged(ItemEvent e)
{
	if( e.getSource() == getGenerateBillingCheckBox())
	{
	    boolean selected = e.getStateChange() == ItemEvent.SELECTED;
		enableContainer(getBillingPanel(), selected);
		getScriptTemplate().getParamToValueMap().put(BILLING_FLAG_PARAM, String.valueOf(selected));
	}
	else if( e.getSource() == getSendEmailCheckBox())
	{
	    boolean selected = e.getStateChange() == ItemEvent.SELECTED;
		enableContainer(getNotificationPanel(), selected);
		getScriptTemplate().getParamToValueMap().put(NOTIFICATION_FLAG_PARAM, String.valueOf(selected));
	}
	else if(e.getSource() == getReadFrozenRegisterCheckBox())
	{
	    if( e.getStateChange() == ItemEvent.SELECTED)
	        getScriptTemplate().getParamToValueMap().put(READ_FROZEN_PARAM, READ_FROZEN_COMMAND_STRING);
	    else
	        getScriptTemplate().getParamToValueMap().put(READ_FROZEN_PARAM, "");
	}
	fireInputUpdate();	
}
/**
 * Comment
 */
public void jButtonCheckScript_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	CTILogger.info("Check script is not implemented");
	
	return;
}
public void keyPressed(java.awt.event.KeyEvent e) 
{
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
public void keyReleased(java.awt.event.KeyEvent e) 
{
	fireInputUpdate();
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
public void keyTyped(java.awt.event.KeyEvent e) 
{
}
/**
 * 
 */
private void loadParamMapFromSwingComp()
{
    //Some of the flag parameters are set throughout the code, i.e. on item change and panel load.
    
    //Change the filename to include an extension if it doesn't exist.
	String fileName = getScriptNameTextField().getText();
	if (!fileName.endsWith(".ctl"))
	    fileName += ".ctl";
    getScriptTemplate().getParamToValueMap().put(SCRIPT_FILE_NAME_PARAM, fileName);
    getScriptTemplate().getParamToValueMap().put(SCRIPT_DESC_PARAM, getDescriptionTextField().getText());
    getScriptTemplate().getParamToValueMap().put(GROUP_NAME_PARAM, getGroupComboBox().getSelectedItem().toString());
  
    if( getGroupTypeComboBox().getSelectedItem().toString().equalsIgnoreCase(DeviceMeterGroup.ALTGROUP_DISPLAY_STRING))
        getScriptTemplate().getParamToValueMap().put(GROUP_TYPE_PARAM, "altgroup");
    else if( getGroupTypeComboBox().getSelectedItem().toString().equalsIgnoreCase(DeviceMeterGroup.BILLINGGROUP_DISPLAY_STRING))
        getScriptTemplate().getParamToValueMap().put(GROUP_TYPE_PARAM, "bill");
    else// if( getGroupTypeComboBox().getSelectedItem().toString().equalsIgnoreCase(DeviceMeterGroup.COLLECTIONGROUP_DISPLAY_STRING))
        getScriptTemplate().getParamToValueMap().put(GROUP_TYPE_PARAM, "group");

    getScriptTemplate().getParamToValueMap().put(PORTER_TIMEOUT_PARAM, getPorterTimeoutTextField().getText());
    getScriptTemplate().getParamToValueMap().put(FILE_PATH_PARAM, getFilePathTextField().getText());
    getScriptTemplate().getParamToValueMap().put(MISSED_FILE_NAME_PARAM, getMissedFileNameTextField().getText());
    getScriptTemplate().getParamToValueMap().put(SUCCESS_FILE_NAME_PARAM, getSuccessFileNameTextField().getText());

    //Billing in script
    getScriptTemplate().getParamToValueMap().put(BILLING_FLAG_PARAM, String.valueOf(getGenerateBillingCheckBox().isSelected()).toString());
    getScriptTemplate().getParamToValueMap().put(BILLING_FILE_NAME_PARAM, getBillingFileNameTextField().getText());
    getScriptTemplate().getParamToValueMap().put(BILLING_FILE_PATH_PARAM, getBillingFilePathTextBox().getText());
    getScriptTemplate().getParamToValueMap().put(BILLING_FORMAT_PARAM, getBillingFormatComboBox().getSelectedItem().toString());
    getScriptTemplate().getParamToValueMap().put(BILLING_GROUP_TYPE_PARAM, getBillingGroupTypeComboBox().getSelectedItem().toString());
    getScriptTemplate().getParamToValueMap().put(BILLING_ENERGY_DAYS_PARAM, getEnergyDaysTextField().getText());
    getScriptTemplate().getParamToValueMap().put(BILLING_DEMAND_DAYS_PARAM, getDemandDaysTextField().getText());

    if( getBillingGroupTypeComboBox().getSelectedItem().toString().equalsIgnoreCase(DeviceMeterGroup.ALTGROUP_DISPLAY_STRING))
        getScriptTemplate().getParamToValueMap().put(BILLING_GROUP_TYPE_PARAM, "altgroup");
    else if( getBillingGroupTypeComboBox().getSelectedItem().toString().equalsIgnoreCase(DeviceMeterGroup.BILLINGGROUP_DISPLAY_STRING))
        getScriptTemplate().getParamToValueMap().put(BILLING_GROUP_TYPE_PARAM, "bill");
    else// if( getBillingGroupTypeComboBox().getSelectedItem().toString().equalsIgnoreCase(DeviceMeterGroup.COLLECTIONGROUP_DISPLAY_STRING))
        getScriptTemplate().getParamToValueMap().put(BILLING_GROUP_TYPE_PARAM, "group");
    
    //Notification in script
    getScriptTemplate().getParamToValueMap().put(NOTIFICATION_FLAG_PARAM, String.valueOf(getSendEmailCheckBox().isSelected()).toString());    
    getScriptTemplate().getParamToValueMap().put(NOTIFY_GROUP_PARAM, getNotifyGroupComboBox().getSelectedItem().toString());
    getScriptTemplate().getParamToValueMap().put(EMAIL_SUBJECT_PARAM, getMessageSubjectTextField().getText());
    //Multiple reads script (one with retries)
    getScriptTemplate().getParamToValueMap().put(READ_WITH_RETRY_FLAG_PARAM, String.valueOf(!ScriptTemplateTypes.isRetryTemplate(getTemplateType())).toString());
    getScriptTemplate().getParamToValueMap().put(RETRY_COUNT_PARAM, getRetryCountTextField().getText());
    getScriptTemplate().getParamToValueMap().put(QUEUE_OFF_COUNT_PARAM, getQueueOffCountTextField().getText());
    getScriptTemplate().getParamToValueMap().put(MAX_RETRY_HOURS_PARAM, getMaxRetryHoursTextField().getText());
    //IED read script
    getScriptTemplate().getParamToValueMap().put(IED_FLAG_PARAM, String.valueOf(ScriptTemplateTypes.isIEDTemplate(getTemplateType())).toString());
    getScriptTemplate().getParamToValueMap().put(TOU_RATE_PARAM, getTOURateComboBox().getSelectedItem().toString());
    getScriptTemplate().getParamToValueMap().put(RESET_COUNT_PARAM, getDemandResetSpinBox().getValue().toString());
    
    CTILogger.info("Loaded parameter map based on swing components.");
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ScriptSchedulePanel aScriptSchedulePanel;
		aScriptSchedulePanel = new ScriptSchedulePanel();
		frame.setContentPane(aScriptSchedulePanel);
		frame.setSize(aScriptSchedulePanel.getSize());
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
		CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * @param scriptParams The scriptParams to set.
 */
public void setScriptTemplate(ScriptTemplate scriptTemplate)
{
    this.scriptTemplate = scriptTemplate;
}
/**
 * @param mainCode The mainCode to set.
 */
public void setScriptText(String text)
{
    this.scriptText = text;
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 5:13:39 PM)
 * @param file com.cannontech.message.macs.message.ScriptFile
 */
public void setScriptValues(final ScriptFile file) 
{
	try
	{
		int i = 0;
		for( i = 0; i < 25; i++ )  // 5 second timeout
		{
			if( this.isDisplayable() )
			{
			    CTILogger.info("		** TRUE - ScriptEditor isVisible()");
				break;
			}
			else
			{
			    CTILogger.info("		** Sleeping until ScriptEditor isVisible()");
				Thread.currentThread().sleep(200);
			}
		}

		if( i == 25 )
		{
			CTILogger.info("		** TimeOut occured while waiting for our ScriptEditor screen to become Visible.");
			return;
		}

	}
	catch( InterruptedException e )
	{
		handleException(e);
	}

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			getScriptTemplate().getParamToValueMap().put(SCRIPT_FILE_NAME_PARAM, file.getFileName() );
			setScriptText( file.getFileContents() );
			
			//Init the textArea to display the script text.
			getScriptTextArea().setText(getScriptText());
			getScriptTextArea().setCaretPosition(0);
			
			//Load parameters from the script.
			getScriptTemplate().loadParamsFromScript(ScriptTemplate.getScriptSection(file.getFileContents(), ScriptTemplate.PARAMETER_LIST));
			//load up the swing components with the parameter values from the script file.
			initSwingCompValues();

			CTILogger.info("		** Done setting script contents");
		}
	});
}
/**
 * @param templateType The templateType to set.
 */
public void setTemplateType(int templateType)
{
    //Set the tabs based on the template type.
    getTabbedPane().removeAll();
    int tabCount = 0;
    if( (ScriptTemplateTypes.isMeteringTemplate(templateType)))
    	getTabbedPane().insertTab("Meter Read", null, getMeterReadSetupPanel(), null, tabCount++);
    
    if( (ScriptTemplateTypes.hasBilling(templateType) && ScriptTemplateTypes.hasNotification(templateType)))
        getTabbedPane().insertTab("Options", null, getOptionsPanel(), null, tabCount++);
    
//    if( (ScriptTemplateTypes.isNoTemplate(templateType)))
        getTabbedPane().insertTab("Text Editor", null, getScriptScrollPane(), null, tabCount++);

	getIEDPanel().setVisible(ScriptTemplateTypes.isIEDTemplate(templateType));
    //The read_with..._param is for showing options to retry during a multiple read schedule, 
	//so we NOT the result which is from a retry script, one that is a read once type script 
    getRetryPanel().setVisible(!ScriptTemplateTypes.isRetryTemplate(templateType));

    CTILogger.info("Set TemplateType, component visiblity updated.");
	this.templateType = templateType;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	Schedule sched = (Schedule)o;
	setTemplateType(sched.getTemplateType());
	
	SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
		}
			
	});			
	//do not do the following because they are sent to us in a message from the server
	//getJTextFieldFileName().setText( sched.getScriptFileName() );
	//getJTextPaneScript().setText( sched.getNonPersistantData().getScript().getFileContents() );
}
/* (non-Javadoc)
 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
 */
public void stateChanged(ChangeEvent e)
{
    if( e.getSource() == getTabbedPane() && getTabbedPane().getSelectedComponent() == getScriptScrollPane())
    {
        setScriptText(buildScript());
        getScriptTextArea().setText(getScriptText());
        getScriptTextArea().setCaretPosition(0);
    }
    // TODO Auto-generated method stub
}
/* (non-Javadoc)
 * @see com.klg.jclass.util.value.JCValueListener#valueChanged(com.klg.jclass.util.value.JCValueEvent)
 */
public void valueChanged(JCValueEvent arg0)
{
	fireInputUpdate();
}
/* (non-Javadoc)
 * @see com.klg.jclass.util.value.JCValueListener#valueChanging(com.klg.jclass.util.value.JCValueEvent)
 */
public void valueChanging(JCValueEvent arg0)
{
	//do nothing	
}
/**
 * Comment
 */
private String browseOutput(String directory)
{
	javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
	chooser.setCurrentDirectory(new java.io.File(directory));
	int returnVal = chooser.showOpenDialog(this);
	if( returnVal == javax.swing.JFileChooser.APPROVE_OPTION )
	{
		return chooser.getSelectedFile().getPath();
	}
	return null;
}
/**
 * @param string
 */
public void setScriptNameText(String name)
{
    getScriptNameTextField().setText(name);
}
}

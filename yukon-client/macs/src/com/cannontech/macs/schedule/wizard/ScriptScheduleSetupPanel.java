package com.cannontech.macs.schedule.wizard;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeEvent;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.schedule.script.ScriptTemplate;
import com.cannontech.database.data.schedule.script.ScriptTemplateTypes;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.macs.message.ScriptFile;
import com.klg.jclass.util.value.JCValueEvent;

/**
 * Insert the type's description here.
 * Creation date: (2/15/2001 12:44:42 PM)
 * @author: 
 */

public class ScriptScheduleSetupPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.cannontech.database.data.schedule.script.ScriptParameters, com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, java.awt.event.FocusListener, java.awt.event.ItemListener, java.awt.event.KeyListener, javax.swing.event.CaretListener, javax.swing.event.ChangeListener {
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
	private javax.swing.JPanel ivjScriptNamePanel = null;
	private javax.swing.JScrollPane ivjScriptScrollPane = null;
	private javax.swing.JTextArea ivjScriptTextArea = null;
	private javax.swing.JPanel ivjOptionsPanel = null;
	private javax.swing.JCheckBox ivjAlphaFrozenRegisterCheckBox = null;
	private javax.swing.JLabel ivjReadFrozenDemandRegister = null;
	private javax.swing.JCheckBox ivjS4FrozenRegisterCheckBox = null;
	private javax.swing.JSeparator ivjSeparatorFrozenRegister = null;
	private javax.swing.ButtonGroup frozenRegisterGroup  = null;
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
 * Return the AlphaFrozenRegisterCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getAlphaFrozenRegisterCheckBox() {
	if (ivjAlphaFrozenRegisterCheckBox == null) {
		try {
			ivjAlphaFrozenRegisterCheckBox = new javax.swing.JCheckBox();
			ivjAlphaFrozenRegisterCheckBox.setName("AlphaFrozenRegisterCheckBox");
			ivjAlphaFrozenRegisterCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAlphaFrozenRegisterCheckBox.setText("Alpha");
			// user code begin {1}
			ivjAlphaFrozenRegisterCheckBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(READ_FROZEN_PARAM));
			ivjAlphaFrozenRegisterCheckBox.setSelected(true);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlphaFrozenRegisterCheckBox;
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
			ivjBillingFileBrowseButton.setToolTipText("Press to find the directory to write billing file to.");
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
	D0CB838494G88G88G2502C3B2GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E15DFD8DDC156559B89926159515ADAB36E8F39B7DF30D3DF3FBFDB7575866B636DC2FFB6716EB2CD139E20D0A4AADAA2A87B8A8FEA5FEA5A696AAAAA6A2AA96A2A9DFAA96AA8A62D1A9B8F08EF07440B99D8EF2D04A7FF55F77F3DD4F67B99C7C4D3D75727BDDBC67393E6F6B3E6E0F67FE6E67F924E5EFC7E52625AF132434CCC97EFBAFCD12E64D14240FDC9DEB90D77F50C50B247AE30445D2CC99C336
	824DF5DF18A5DB3A7163ACB87741F974DDE1169570FB299463AF78A57C88E0C7825A310B9FC68737734BE234B3071F572DDBG4757817C000B84C88448166C6BF2E4FCAC1C3FAE7FE61016AEC911FFE2BC4E13B9AAFC611F841F870E61G91710C67623ADC19BE93F029GF500EF0D973CFEB84E655E0DACAB15791AEF0C12E67DFAFF7AC2E6CB259FE106AC1BAA9F732487F8A0B2242D5DBB61D8DEFF36F3CF3B3B3B285D5653636E71B95A3B0A7A1D67EDDEFBD12F4F66F31415B534FB1D9EDFC3FB17435E67F2
	545BFA9CAE10F37D1979FB4349AC2ECB12CA60BC0563661CE2B10A015FF1G89FFC61FFF3B30DC76E96C59A8A961EB3B827ADC7E30703575E145670A08D19959FFE17A23F99DB039A8204FG906539F9C5D50072F45B5AFB0BFA6582482553172B41616B73480593E5CBDE07E5EB7C8B166D19934C7698B89F8FB019705F37B27CF4B8CF8230A4E0195F5F08E536DE7836145266CCDF94204C3989222CB589CA19A3C60C4AD849732EE1BB4B1DB0200D871884908F108A107B48C54BAA1E6F2FEFFB870E15ED3EEA
	F7F7373B27526329F2593D075CF6078BFE171581C5469DF3343B3DF6C992B9BD76E42109BF88A50FE8731901A85B8989F3F04231EFC8BEDFCBFA26C959A67E3EB63DD656517C08D23648C7D85CAE70DC99045FCCEEBE0040DF4D7189AA3C287B059C4F4E25C7AFDA6C0FE21C2D2F23AFF5165B256CEDE753671878B26E272D69F10F8A9F929ED5FC91B15ED242E23C60310B1682008AGAB009FE05CE398E36B369DC34578A0641C374766EA68EB73583C3E2ABE274B6E704A313EF7DD30D81B01101BBCECB99117
	689D6ABA0A7F6E99533844C79FC91F7818084754471438885B7F88E8BB69B1E3EE3CCEF5584E5B04B4D354E186613BB9BEDB0597F57B8F5E3658F99E504281847FBBA799BED166CFA3BDBBB8FE168AAF74CC36E27FA07D95F284C0546F7DBBD95F9A8767B381D28116BDFE51D282D08FE0FF9C6B78EB5B3606580E6AD575ABE41B373F81C7075F67359D6C017E33275D51E473BAEDEDAEC72FA473B836333836E9644E70765D8EB68EB8FA9CDE1B4F696E816D36EE87A4D32F60695D3E1871F89D1C5A686CF60001
	EE0F04FAAFECDF8AC7172D57F75CE3071EDB619074AB37331CB8E7EB820D2000354D47D93C6E61FDF8B87C0EFA9CE31572AE4325B07A939817D737AF016307105C8ED7D9D90D331D39E1738EE00EDEF18A86D9A99F78278304A7C2BF78370B16303FE19BFD5A0A39D833706BD2616F1D263948472A3F093CDA7624BE97EF60B64A815F8CB0889089B079C99C1F171E324ABA18FD668773E7B86D864172B72D9CAB0730D1C8B658F9261C3317E2E32E6E1C75EBA27E9D25AC076321DCA9GB9FF47BA5835A3CF64E6
	152D570159F8448B096430ABB5A2F85FADE1E30D631C2347D76370B8FA6C7023D74E0577CB66AAF95A61269C2A66F856EFD5C3A275927EE049E201879D6A2CC77AD1CE3779FCDEE7DB1F0F8D26BEE7CF27A2FF127B522B6992FB6532F66DE2E5AD06B25682D8292C0F16ACD565DB1D5BFD364F5348DA121C378B72D90E3A00BF7C6FD8B7CEAB1B2FC553F9AFBFD768AF1F403A8B95182E6175554B2C4335B11C7C91B60ACFCA74FB176A77CF07EDFBB8A0EC7C69325A90F57F7B1D01F3B7FE27F96EBE3A53BCF793
	F7864F4D641DE639A9E8AF6CE46D5B5D5F234A75541D2C9FECF77774B8F803A39139AFF83A1115435963741DF05ABB9D3E8667F907A4A57F1D656CCFF9DF9B873F637F0E7DE27FBA6C97734F5FA5453654184E2346FD3DBD3DA4C964E2ED1252AF8A1FDEF91735A1074B518D012AF677757884BE633DAC855F1B41F1F3253B18EC4187B03E35392B5C7EC6C737478583C6210D1727C89C6A9D5E8E37375BE1AFB8E3BBE7AB32757B0AF8FD94D5F272BE76FBD35E16ADFB72CCAE908A4F681537D46A345685D63BFB
	D35326E239DDAC157EA363EB425F2ABE1F4F5DD35D65E8BFABCC7ECB9DB6E89F2E475BE4FF772D7EC4785B88B93A5F69F05977BBDD0E43B0169736C3876AAB627F45984DDD75977576C3C64ADA2B99F588E7BB1CDE47419ECF9FEA816B5BA9DEBF7C24C7314F7865E0DDC663BB50F63CDBE53B9226AA7F965BE29C39230C3501AEE89B03GE313F09C39789E2363B8A28F1655EE17CBB4DF20ADF80A354DA9FCEE016DEDC212383ECF0123206FBBEDC6EFFC8A693FB025F7917DC03399FD1068F126F425CFF415E6
	33FE4A71C91F832615ECA07C50B100F34352A5BADA134D55274CB9AF14323905CBB5068AFC4FAE73FE43370B0D1BE7C93FFF57EC094D9D05CE514F455818CE0F57598DD38EB6A91057B956652B57B9A220ECECCDE49A9CE7BE05576ABB4F32315DECE5A11547620F342E40666282174CF1CEF064918BDE5F9C4AD62F0358FB1BCD7BCE8695CF0BD8B6BE2D3F6E1B361B2F9D81BE7CE94C1967F629679E153D3D6EF6A71FF64BF13B2BCC5D2F8A5CB74A664191B234972E816D8EBF46FEC29973154D4BE5349C3FFB
	B63FAF992FB376591DBE379727F6557D3CC1B43A2B615220962A2F1A061DA62723DF42350A17CFE13C46BD0D634A277B556BAD13979DCD1F9FA0B6536418A42BE2B3F544288C21FB70B46AAEFE6622C560DEA1DC9761BEEBC6DC54B3D84F3715316BE533B519F6DE68224EBEE7D11F4F49AEA8ECBE9BEF5AA28F044E6B789C0F691A718C2E357C57C6FE7D188C5769G9549D85E935B5539901BD09FB0972C49221C2EE4FDAE343D4F6C0D86FCE4B26642465D0BD539F08C22EF6B690496C4F3155D99222D4867FF
	5C1D09EDC542B913F4FAB8576FB149F89DF6679E56A7CC0173840094006CE7AFDA0A1FC55FDA4B07DC0F58678F329EF145307C2AFF966751694D431133135C15431273135CCD79FA39B14FAA737228E70DF3F431C6302500654A44E7459AC1A661CBB9BECE0597EB87BF6639454E6301D6719C5643FDFBD9DE0C0773094FE16CFFD03ED0159775EEE70F0FF22273FD56DF3A3D4ECEE78F755F9C576E767249243806D9415B51945019C0FA3F39A7705C775EBD66F35F09FB4C673EBF58937C3A6D27FB824D0D05FF
	63483FD92C6C9C373299F1D94FA36EF64265926EEE5EF60B613C06F0D7EDE038AEB88FFB9E6BF6C7810FAD1CCFA65C9E0E1B816729047B7946C0579DCC6EF5D94725DEF7889B8F873411A99FAF75DADF9947FF3617651C1F4EFF494F87A49C979C150C57E7734235433E9E3B1CD8E28CAC6275318D463F9FC9087BB647ED36889D82D74C6B63CEF0F6D79A623E43F1CFGEEF5BA4E57E273D9CE3AEC836EBE30B1E69663FB10466FBCB8AF85E885704D92575942476F7433312443EB6B06F4E970B85A252EFFB059
	AFEFC05969401F881085788200DAGE1731A82B6CE42AC244DE1E7FDBD7A32B613653D18933059329887441C42D3201ED3447CC378928F47E4G4113522666497A2760A904E38500E85F2F65635A2095F0917F4472ECEBE5EB03FC1C42C9FED959812F53DEE56B2C63519163510175D932FC5CBFF1BC6ADB4F57B9DF38E84981A8FE8147A37F869CFFA7FC63F629AE6C0C697D0E0446236935AF4863518B4AF8B4C8636F4FB6204DE820A73C40F04FEED04F95D97CBA217E0B18BECEDF37D1CDE7ED1C5D4FD26847
	B5F466EFCB061ABECAC3E7B1DAAF4B0B325D3F9E4B16FAFF34647A1EB5603D1C0294D126BA791838F715D490551FA1623EB9EB35BA6E7285C1D959876A741067825185B83F88CF41585FF11845FEB21CCF87C8CA417AFCFED30E39DE1ED4C2DFF1BB4EADACAF223E24A6262F9C4E9B81DCAFE25F13397E52566D17DE22DC5D30654438F6E08373BF7CC57DDAF9BD47C73C28DFAB3F309E5BC3A4502417F03C9B28E4713343F99840B8009800E9GC9AF61B8D5D7412E51034EBD9A5D1EBA9848DC2A21C714AFEE43
	255D4F78499C4C3B09BF782AB4692A1C00F3DA69E539AD494730BD2BB8DE14EFC1B5ABDFBD50DCG91G51G1381263F0C659BDBD1A84A47266415BE1F2D3D0B4F7631F0EC822E20D9A7A84A762D02E0E5CBFCB950DCE55191D69729AF0BBA1AC378D59C3FACD5410B3A3B0B67BEBBAF84DAC4AA666A4EF63D7DD9290A4D2CD78259EF6577DFF2DF515F0B6B617872D77477621EEB42FBF14DC00BFD856D9F5AA0D19EB729FEDF317E722FCD5EFED9ED083C98D199F8FEF6F525797C6C06CA737959AD154157A63F
	DC99F8EDF2FC25FAEDD26475319AFE8F66D59873G6CGFE00B12FE2CEEF2FDAABF25AA41F7759BB9DF0F9496EDA79507E726A40E54D2D9E7ADE577AEAB6563739FDBEF7B7530F784D552C3EFCEE8FC78ADCF1B50B91CC193BFC02D5463FDB4D57799D9D9CAD6611D38AF09E997DAA665DF25ECED260BC67B59807DFC37C9A0E2F03F38F40D842FF73F030DCE1FA825118FEF6ACFEADB00F30919B54C63039E426CACFA09E910BBABE0ECD82DF263E06736C7B6D981FE4AAEF4789169B95D2182595E090E09C40A4
	GC177F17AB4B8CF8648CE8B3394835427A1BD25492CAC95328EB5EED03691486FF1A6B4E1E331697D99962779A67C198174B3505FDBD35A6F291A40B9ED2B99BA274F5470359F484546810FC3605CB57CFA2F232357C1ED47D713CD38033D756EDEA7DB3619144676ABBDDCDCA37B64C9B346826F0B4F110F6140B38EE039FCBE81FE2729784056DE36DEF5085DADBE663059D51BD9847DEF1BD99BB862E19E74F20240FF70BA739157613CEE170B9145FC3A7108FEBE4D2E472474B04BB2G4173ED1B99CFA150
	AB086797ADEA9EAB6099CFB89C478188BF5A5B0D57A1675BD1AE867858F5C89C9C638158B521086323F60C632CF473B8E20C98E4E504D9F2B314B896E7686338F23732AE492EC1E889DF264FDB223DC6293335D10C5FBE48E2D9636865D488B4A74AF2A717E9E924D8506F7904554FBE3F2D5B6392CA451A705DA7547370728CD1F6BB9C857D4FB6B55DAF53A3093E274D0C9EC37402B6B37DD308BE6A04197C8CB80AB97422EDB807CE825C5B3CAC90E7897F12771A6172ADE1F035F243071204733AC796603CAE
	771EBB241A87F35257C966733AAE0B2867C97211DFAB4DDBAD7BB366C8165CEE1201AE6674BFBE4AE2DB1089B989E04DC45F1F30A12FA793F9336D0CB79A4EE381E6G086B094EFD812ED314C6A361F54A2BAD9813E4527B9F2D5C075960830095C04C213E5A0E73EF8F6022E723EFCF3663B5DF42EC541143FD4B19837DB2C06D9C64DD6AC0FB2DF3C22D8318A370BAF831CE12F05DE1FD01669AD8F5AD0639AA6CA51C203566B9981F67BB82DCEF71DC96F24B5AB05E534877BE42E5E5A12E258D75D7906E3E93
	48674F427168575B54735C9840CF498ABC666018A0C97FFDDA3DCE1CFDFBC5BA7681FA7E99323E19D9E13AF56242432C5F49199BE6A9813082F8G4281A281E2G26G4CGC886481A877D8EC0B1C08DC0A3C09740A00098AB26FF38890ECD36B3463BG001497A278644CEB936B0CA286B79E514F75A366A9B198BFCF9B0FC9F0AE6F1DBA46F2E96A3CB05D3546DE0E1F29420BBC7D79D13C56C885DA6BFC2C03A41B5E7E14790A4D997335761366237DF01E4B5282E1E7AE593F1163F3D5F8E17F8D0EE76725C0
	0BDC0076B3DA03DDEBC98B357617ADC47B5BEC3CBDAE8C536DCD5B4D7125AA3C30BF299577265582ED42C2349FED38569A8A045C77AFD16E19E14B09F20D61714BFEBDCC37AEF00B08378AAF4A7B169D7B2582200DF99D4B1B63D05BCF863CE5D170B6A7647EFAA2185FB9A6BA045C4AB635DC214CD7A19F9B834A1D5748F549FC0372B1A2201C3DAD181F63834A7DCCD33EC9B25FD47918E0A2A766690F7071F356A291FF01FB1C6396E48B1C68EFEEBA82875C3BF5082D45197695F0957781F43FC75A4EB85AFD
	0D36CE1229B89CF84EFB607050F35E7A436657F1CD070D57F1278F1BDF47598E2B2F63842E63B09BBFBA21FF33925FD9421DA41C076BE43893CE3B2F0B706738DF8CDF6BE08691BF487D6A67B82B0C3BC205BBA9632EBA4C661DD064AE3797E52FBD4C6EFDF7B8DDAE795A1D73B8FBE09ACF71984BFDF4FAECAA3FEFA35CC92A4BE8BE9FA9003AAC8790380DC7715A2C15F087B8AE9C4E47E5E3DB5B457B6271F09E8BB01D707BB8BE894E33974378339871779FD5DFBBB17E6174931ABCD749464A3F050D199A9B
	412ECD07827535AFDE4709D8E434E14C2C0BB1BE1F36E34C86897765F60CD99445609ABB1B03CF007329GB381ACCB10F6AB279540F99D0087A002E839F6AC9713C53FE6AE8E9CC326B714F2CEA55FF6128DF5590345506C9A568CB2A9F6EF36E21CE216E01C4AB8EEBA1CA7D2F95F33319C1FC567157CBCFBA91E5F577A2FDEC71B250A173071603F5C06BE2E50EF5AA57AFD076AF73E7D722FC53EFFD9ED083A5D6AB00E9FA58E7578A1F029A7B0AF0A17E2DE9473F696866791043B5E4F6F4532F342BD6FC759
	D8425D48E5331705D97216A16E964ED78267B504FB09E46B89F7218DF1D104FB68847A12CC389F2623AF1665084B4DC0596C65F86DE16377B658FECB274D450787014F75AFD470B8BC48576ABFBEE2DCEF78832DF734AE976B8DFEB80E81E0EB076317337506BDC7F3025409507DB16F4BFD3636C6274F659057D94F55CBC43F464AF8E92DC6FE66C850E62EE4328777552856E8EEAEE4E35CB1070F6D32623CE2DD45FFCC3F76427CCC82BF25B7F06D654163E6BCB9C0AFA41EA43F99CF8D502DGA2B605F4EF55
	7306084DE8A68F6021580CED8F969B9107354714B80CB94C563E8F3BFD4E8EE73B7C4403F836CC50436AD93DD641B88C17122AF8EC6BE7F28DB0095957EDF33A780E4DAA371F647E230ABD7FA7BFC761106509CB1479FE3B3E4C2C9C9321BCF19417BF171A71A4GFD569B38BE72F62BFAFDE4418A910F1295C87FDD0B993D960EA2E65BDA074E27232D46FC3A26C109E345FB0CD72CC96136CB5C5368552C8FB503DD367E9D8EC731FE36540A6BE763D69847B326DF3DFEB681F8E2D70875B35646262F502F1F9D
	E863F3A7F70FAFB707697F0837758460CBDA01EBFDDF77B39E9FF8AA4A722DC6D696614F3C817427B5E768753CD815BF8C6F4B517B932D7367BE5167416FB1B9684F841DBFFFBA2E7833187C199C401F0CA07ECCD379A3745D54067A62CD74219EFC26A4B9C79BEF4BCAC51FA8CF06157743004F85907EAFBD21783FA3836D552DB45A4B14024733F5257E3A7055A3F8DD68825A781538BE333F9E572B62D762FA150F375FE5EFC2DF83D083A056DF62BED26F9D485B3C4EF46FC04AC707531B5F94BE073D292C3F
	0C27353E18E338A6B471CDDAE76B67F3E3B8372C02182C22F54AA664ADD9053CD7FF4CF8DDF09E8E90C93C79832417F8E773E74A12605C128BFAF3F17D2E318D63D182B8D7AE6A08EC43F80C4FC59D3F663C71F0BE8BE041EA644DF0203D7C55485B4A6DB543398FE08C71AE5F053C6309B7EAAB578B6733G963C0563621EA6F6DDE232A6C7FD21505954CA765FC2BB738857CC7AFE73E9GFD546988593BED38FEB7167455D8F10DF26ADB43DD233CE0D5FEFF65A3095A7688BF3B175963684F23FD797B212197
	2C3543F86F6EDCDB18D7DC18372DE4932E7BC5752B577DCA67B5855C9FEAF9DB64DB0EFCEC7CE415DC365D83D80E7A37313DE535A87E5D5D2E78F7F5487E895DD524FBB4691E4A672C72BE20B624473EED9EC3A1730CC3716743AE451FEF2707624FF672471E0176D2481F9E27A4617C56D2FFE9EBD8CBAED16E43E14B09B52EC45E8F956769573E1E657872BCFD9FF73E9E5BF4AD50A6G08B5185AB3EC2C4EDE837D99C0BDC09740A04058B5380EFB62C40194F89DD76CA4D316F1C5393EF7CCDD2EE850B5F90D
	52D7CFDBE36C37057F577879DA6D9A617FEA425F457185EB953C3C06497BC6F66E821AF59DDE6BADEBD25B2F4987DA3EE253159F487E4A9626AFACDF58D9CC78F7B8FE028AAF6C5F407BDBF6BE83E87675E8FF5300E6CD9170168D0A4D1C8D016CFF467B48428D424EC242DF477175AA3C303F006359398FE8899B507E2920EBFE0173316B92656E9A361CA8579EFE2F28EE23BE0F8FF03CF523BE0F275158E487DA5CC6ACEF16E68D3730A04CD2D120443831A0D03C277378F59568576C9F617870827D1AFD0795
	576C2300E65904765F5C25D91B857C60A645E64426C076DD45FCBDEC1330330A70A3B8FE268AAF6CFF030F0B6CBC7F9D683BB6237D531AFB86711B053DE479E8A98C34463BC8A317A373954A470A00F2779A6E112821B1205CC12BA6CE40B7E8422B8651CF1DBD4B7A6D88601DD0087D52E39FED168277CB75DE374761758D88ACFFF2C87621C253B627023E04A1FC90F287B4F233C0A6EBCBA8F2F11A36118BB245A149B9B3B4FD9848D8C312FB1ADF170B5FAF28FE0755DF7E75215BAE2B8D79396CB301577D2D
	E706DE77371DB1DF776FBCE3DC77F71DB1DF77771EB16E5F1A4A676EAE282330AD383673E1A3DF6F0173C904AB26F52B78AD58FEA37CEC9ED7D894E62983F0810C860886188A108C1035B54CD2GD087E08788830884183495F5FC136B188167A9GB945E116D2GAB009FE09C40A40099GA9GB95B008EE08570EF23B5527E84B956F532DDF57C8B6573682260F9988842E45F596F022DC36B2813F9A2D53CD3E55B23837840E2B7A9006E2C2D6AF57F97292CECBE38C8470B6B2E7ABDDA21826B37D6483F1706
	403FDC156308DBA89F832D1933F22C8EC0DBAC9FC54EED66570A63201EA7EC43BC1C180939B90DF0C5F3B0B7E7D1CE3C5D1287471A6DE116E6GBFC0A440A40099G29G79BB103718735642F99740E800E800B800C4002C92486192643D656330CB0C2D9AD2E51F4CE84DAADFDF174B40FCE53FE7ECD7674224FF09AF0B244077BEB265FA44FC4AD05591563328030A812C2B5A922C17CD99D8D7AE42FDD5423A0A201806DB59FCBE814E251DD0D600DAGBBC078CE643919730C0773A9GB381AC6F82AFC04D3B
	4833445A2A731F69425F122CB7FC27F95C19FDB33CDD47AFECEDB158D24790633650C41F3A1D6A41ECEF65E22906DF4B4AFB3AD53F7F50F6794746E7AE2B8D1193EF73F597BB54237FDD4C132FF4B0DCA41C4790EEF597664EF42A73072C6CDEE27EFBD037GF6007077107684270D0773A9GB381AC25C8F385FDF7C62D2CA790BDBF884DCE76C7863501B992089EECCF729C0A5D36DD586E72CBB1CE7714E13B2BA55C7AF20C1D0BE260ADAE0163E4B8CFG306C02FE8E209640859081B081A08EA091A02B8C68
	GF5E5A8BF1A4BFB60BC82A086E09AC0B2C076EE18AF825483F8GA2GE2G2681A46FC67989DB27DDE25EB01F8DF5364B08E37E1A49471970E21955B80BDCEE961F89AADA229C2BBA937D22EC0FDDF2594C6A5DECBCDF99C05EEC5CE67D10FE2E407CDCA16761E295FE391C172BCC7408DC7272B5496C72B0CBDEB9665740B9FE4F914EEB887750B93A67C8388DA49BC5382D7DA8BB11F0917DF42F13F0BF3751BD47779157EAB55EF7F5D85577DD65FDABE7197F74FC07383412E97DE7593D99797E2473BC3F83
	56G0CBD1DC25F0981230D6681633D5D0FCD785E19EF647BCB1B11EFE61B11AFAC4348B7B2C34DA756939FBB466E2136393D7C01E2F11F70A53AA7D982312A838883988B40762B883936C6633D5701C6141B703E383FB8850EB3E4B9F1AF6340893C17117CFE60FBC3F86F443287666DFB143D5E05FB7477067A5E66FB661D3D3D8EFAC30BFABFF7E68E4BCFC62937793A141D60A89FF3E8210AAEA411762D6D2CEE9B7A5A5B9D3D3D9A65025E64D9E1302D5FEB7E665E9C238E5D3E71276773FB24DEF7FF2FC33C
	40C60E71EA76BE10FA37576770327740C128E587857D4945EF68692A828A1EDF7730765860E86F5516EDC7B70B77812F3B4F2304CC26FD7560C224618B00C86E36CFC90EBDB821119B7976E2B5CDA5AB6EE37E552F5F074E7222F48F5EE77DEC3FFE9FBB2357917D0A81B379D6224F311B51FDC40F5DE926FFB451E3561851A3097E3539EAFA4C9EC11FC1747506FD7A0C1EC474BB534D74CBFB11BE4930CF1F511791BD4CEAC6AFA07A79139A7DFB855DCE74F6D37F7CC4BF56EAA6BFE6AF36792AA6E3DFF13289
	5BFC9470CD82188510D5A17635891EBFE75233689522DFA805E31D4CA37A05FDF330DF30D78CFD0F568FBCE3AA14FEA12AC25FAF3C64E2ED2A714871E3B61F3A4DDED9464632E30EDE07F8D30E3A2FF8F799EF2BA57B3D6E730E1EE30ECEE7AF34AC3C51A6371B01C50ADE4AF0C1DB5E465AEB0D235B56E367969ABC4E9E5EG847D8D5EBF326DA24282F28ABB02A7290A0DAB15AECF174D5C8DD957DFEAD670F66D31F9EDBE37D74BAB37372E96FDFD33F8CF28407AFE22500C9ECB7424927D3E87C61FCE7407CB
	7479426809C47FE81D1A1EAA57F7E9A552AB8B4C6C57917D5615E676DB093ED153DEFD15029ED909F9571FC54F9ED50A5C0C03E3FC2558EFA9F80EE5A04F2CCA111B8B2AE0EED2A5F8C4EE7E170439D9DA9578FEBFC29D7034D6A939692B52676607AB16CA72DEA01E037AB1E370F5562F9F6DF374B90EF4F468D8E41E26B646F3486667EAEA5DFD5EDE834F63DB97E96C28F33DA0F7095E0626AF3FE363923DFE19AEE27FAB2B3EAFE3718A2B423AD9E0B3230FA57A4CA6B3FAB451538C4FC4B17AE4221FB625CF
	A3FA0D212FE37419D558173D6BB076E5079C188BA9D5E25E13DD8D57C6GEC3F29107BDE07F15EB33583654A2BC58EB54231CB1693B9D45205B994D6BDF47FB696F82281589EF1B6971ADC2D4F21078A785C83A69536CEC7C31F58AB2E4B23915B58B5AC5F1BB540C7E5D5BF1835E429C1DE1D9F1D4717EAE5292F9371DDF5D2DFFF2C2CF155987FDAC37C99BD1E68D1BDE674E422A7EC5577AD0CEE2941F6FBEA9746BC27C654D5B19CEBEA443EE041B33D8CF91AEBC43D7860B8DA669175D2DE0E7592D5E3DEAF
	787CA603094093D72354CBFC0D3EDEF273D83BE77B555806B573F9654FD63FA1511EB87554D4B5D67C75708AAD0FFAFAA8737C3EF81916C7B7BFBDAB6598693A39E7626B4BD5BCEEEF374DE71873C55632FEE4DF0F435BB9D0E39B68D56749143798CD0CE4FADA5A6C259A1ABEBF6F4BDB2A512B276765AC517B2669236E5B2972DDBFA545E7C15F4FB16141A20ADC7BF931BE97D9FEA456E0AE169B7ABAC6CFA57A93E77573C2C64F5E07741F9B7AAAC64FA73ABF5FCCFFA951378D6A5B82235791BD2C49CCFFAB
	51FFFA5C0C6EA37A030DE676C793FDF50B99BD12683FEFB4539FC37431267627907DCB474D4AB7E39F7639C786347B7F6E606BGC9C0CFA11E5D9D5A7DFF71AC85647DF34F1E43B6DE30FF687D086540D38F30C6465B776B776B9D6176BD009F5C0FFBEADE6BD66CFD339F6DCD88E0AFD3E5AF96F8262B6CA52A6C0977B145537359330016F5G7AC200DA002E833807EDED83BE5BB69AF03187F03FD7E69E6B2B747B3D0A4440D7D6364F475E5BD8A58F03721E21B38D386FAD65G6ECF9B4775975702DD0056DA
	34BB278977A7788197D30BFB450EB4210E44DA5471A9674DBB88398DD0FF90F97DA7F00FD2574161BEFF3C163D1EC376BB17767CC5116E6F6D47FDECB388B7097832BFC05C9EBB62EA897742CE148DA35C93EB90B769830C71220615A6B1AEAB135FF5CB7EBDB4171EEBA6FD4D746CEC4107087BC3BA5AE8A55CB3746C6CB8427D27151E457D101EF1F6855AAF083D3A107D8E3D1BB32B8E755D4EFDE16B0595043B1A76907A6B304E2FE842DC0B81DCE29D5A7E513CE234BD54FBA279AEE811F6424E2F1AB08772
	8EE13E24E572BDB1F06E8198FD886DE74E461C0B825C0CC3686B215918F35907D147854EDB8B672DG7E4348FBEE8E5A0BB8EC16F31294F8AF63BE0F12F3F736E0FC62C877DBDB90A79DC15C3A1D08ABA55CF24AAF4F910C61213A2D6A981EF5F5961DE9E7EF1B6101D3EA13DDDA9E6E6BEEF3F815BA9DCB5FBA09A69BFE7E0E95368614C03887D622AFB975E877C6DF0067466992D348FECC79DFDF0F75F1E79675BD000B2D273612C5FB8F6B31BE4E2573FE64A874A3G2DC7D147798C6AC7G97F394F57C64A2
	75A3C7D147B397F9BFF28C7A91007AE3743C3DC47D4831616ECF6D7AC429D3095AC694692E267D30B388370A1E214FEEC0DCA93D0F2016F0B791AE0CF097285DCFEA409874B9B00E4900ABE8C45E9FB9B0862D0D98033FF35EF1F09E83905708BAE6F5E09C93G17F39CF514F7220E7A632863CCA7639D8D67D1G1308575105F11CF6FC387D7135DEA50E4714BE2689F5DF24F2D7906E419E6AE388372298E5E31A30ECA3F6E1FC928117F782F9FF3D8B4BE6BD01E5CB613C91F09E8D90FB82F52416E1FCE6B01C
	1576B66FC69D35D654615D4D77C64279B80098626DAE477844D9079B1FFBFB15784C59C67D44C9545DEB45F217926EAD9772F98817CAB995CD38EF6723EC82613A869117F30A468D5AC7DFCF38970FA3EEB461DEBF0E323104EBECC1DC8A619610EC71E9445DFF9C4712AE427D067822CE633CE954B9C94239FA7739616D35DE5B0F75B06374F06B617F3157AE4BED3F652862433A7E6179B003FC48EE9E2E8FA30EA93910CE4F9A54029E717E46E24DB5B4736FD9C97DFE47B68DFD31AC2F5047EA68EC8C28CCD3
	53FF2121E72A6445FD48020621763806B51B6DEFF8F7C83937F467C24EBD241C9E445E51850D2C1DC6B66B7754AE677868E67D1E5A2FB4E09FB091E8B99F61BD6505CD4335AF64D6DD22DC63306564BD6FFC7EB578113E3CAFF17C680F74653D126668E30116C2657DD4733E03660FE11E75F1A87BAD57E87605064BB263646384939DC26E877B0315B7B6205C0FB476264BFC0972B1B520DC35DDAD175DA2787265E3E9CBA039541D417C2C8BA81734A618DCEBC0393F4CE5F2BE9DFD34FC9E99D04E2659E79BA3
	73CD110FB3824A7DA9BD181FC98165E6E972C5EA95FC4B64E3C1EBA039491ABD5065B2DF3DFC3487147BFE50EF5A79834A5D4963B2C626C749470972B18E0E712DE67D5675FCEFF83209DE86969B7495B6B339D1C336E373FE7216A1654C7B498738DC214D5CCFBDD45870FDB9FD7CFA2A51266FB7BE6378D61B3E5F1813C94F8982ED1A8D7B0D7933AF2D3FDBF909F24706ADA74A758C3F76ABE95317B70563AB5A74653D022EAB6B00965306653DB0C7EDFFD9BB4C4D5BC369A74737E872DE1629170FF6939DC2
	EE5D96351CDF669BA39F23824A95E87A2D09B2DF1CFC0C8FA877E6507EAEB9205CEDC13FE7E83187127BD473EE5D9C192FD0BED6403151EE56DEFE37D273AC054CB3A89FA3825AEB8F5AAF0F8FA877752CCBEB575F9DD24E3CDDA7F139C9A67E1841D4BB366BA779B5F502DD1F674FF3FC12DD1F6703691867A9C0EBF6E01E07E5DCDA7B3C7E9265FEF171525AF53B447C0FF5684B6B6278298EFDF97FF1112E7900D65E0165FDC6D25B0F827C440ED05AF5E650F1A94EC407102B8D7A8CD1FCC0393C206F5DCA8E
	A8773E461E25D3FE9ED6BE96F686123BD5A3D7A173B54A472E00F2E11A7948204C97A19F4787143B603834F6F67510F2666D6C27C34A1967DD8BDFDB1954294F3BAE0E1F52294F3BE79D18F75300D65205F977D2472535172C4ECB132B9C361CA85743FCFDE8DC173E3CCFF07C78AEFDF9BD1DD85E9820ADF3E2F94FF4E946B3400FF106524ECE84EDE7D1A6BA64E782B4F9B8D1660B130F71816562FB54F249B21F650C5CEE4E84124BAD8E66E7E1C039513BAEAD6F3FB4241CF95E7F7710F266F96062EBE695E7
	74F9504771F5E774F9706AAE4C83AB50A61F41BCC85FFDE9791BFD09F28707ADA74A75B8DF774BBB2BAF6FB39CDFF8D6DFDE7FEEACEFA950A24F529E79F24D75128B5A04AB143CDF20F9A7EA01ACD3AE9F6BCDF488B93FB5D8F96D81655639181CDFC79FA31FC78514CB5336B319AFCEBE4687141BE8788E149A12834AF58F861DEFF6873C7E54DCE76548FC057231A2205C2B4755F20DB2DF17FC9C8CA83774F8B0BFA3824A1DEC89A6B7BE205CE20D3DC9B25FD4791890D0EE9A171BA55333FA44B1D7BE964331
	26472C7F799FAEE76DB14FE5976043CC65B6762B7D9C8BBC5181F4E8657E2C111B8CB253C2121BB36CE77459330599D26CD2714EB1173347D1AD3E66CDBA1770779C496BFE2A5F639BAE7F73D4BF7AB758F821C9A913F4C279FD3569F7837E35363FFAD9ED08E7A0461EB0BE97F15B896373932583782C494C9EFCDEA43F831FB5C9A15C559D78DC09450D385F6EC7DCA36126133E71043B2D8175CDA25CCB8DA8BB05F0276CA83B4C0338873723EC8161CAF720EC8961163E0B38F042E52C25E7618877CF3EB7A1
	7F9338BE7A84F19F4DC39B35043BEF9E150DF07EB6AA9B619EFF074AC638831B29EC04FBAE9DF14B3C081BEFC5FDF6428DE620BEBF61D626D1598877BBAEBB9D4EE7926EDE4E178267D93D949745AE61FEF392F1F642FD3F8D63B211F0AB78BBCBE361FC9A619E634FAB4D0473D9042B256710A49F45195FBF2B07F3BB613E5E0AB67C041BCD4FAB05932E6FB84ACEA75CDF793D3CC4B8177AA82FF8F91741F9C19F2DE1E5AAEDB9DC75FBE16665EF579B7E8DB67EB1DBA9532B2A5F23E6DFFE5BD117550628531B
	66987B2DDB67987B2D9F36E05BA921FCF821005AB1612C1B289D932EEC8B35E342ED5BCB6D18F0B76D25F6CC383DBB290FBA07386AAA6A2388B73E0A7AA842ED5BC939CE3818F554C791EEACAFC7FEBF142D1F6C72FB7135F0EEA55CF8FADE55CE3896DE3609F0BE15F0CF72FDB089F01ECC38AC3E8F406207F27891573D9B7565916E7C9A44F98817B1975BE22C02CB4736B85D0FEDB1B9DD494F97D53FC724DF7EDC3D635FE0033FDECC7E6DD57DFE796265373DCC3A7CEDF1F61A31AD4ECFB33645DC9ACF92A8
	9FDA28DFAF9EC05CFB74FE14B042B55A30BDCDA35C35FEECCFB38917C46F66CDA65C3DA4DBF11E468412EDA41C72DE5FD6425DCAE3F98C613AC9F68A619EA059E967B12F9D9DCADD7728FE3F60387C751E5DF179ED4C6CD44A74146A77394E4BEF7B064BEAC3546995DD463C3E2A4B1857BD1457F997B09F6E203C71906EA17E7CF2B81C0FA55C5E85A89BC53867FBD0B619F0BF5C033216C1FAEFB40DBB59045BD1CCB9BCC86BAEE5CA5DDCD07D3614DD7EFADA7DEF301134DBA953BFD43FBF5BFD79ED5FFAD9ED
	4857D46546BC3C3E5C18076B36D1DFC57960ED243E0AF03FEB243E0AF0B7D8A907BF25790F8B67619E425D61C2FD0304CBF5216CE84265D07E4FA05C5E8DA81BC438A87AF6F68A616E4FC7DC6DE7088B9FC45CB8427D2E89EDA492EED7BA5AC8A55C6DF4ED13F591F14BC956CA386FC9A86BA25C9BF4CD65A75CBB479197C7B8CF8B62F24290D7A078605CCF387F64B64640F9A461F249C6B4611E6032A9F01EFD85628EB7213EDC423DC47A0A89F71A7415932E0C6B9B8367D1047B9B5918C8388F1AD0B616F0F1
	ADA81BBD82F1FB7A0DB9D95DEF4C49D9A6FCAFEA7844B3A457EF6539867A0BC760DE48987EBEF0718CC9D4873DC3FB0479B3A4ECBF9FBEC362839E866BE45A68917AF7D6A76EC7FDE3CD74E5486D9B75C54B7A701D5513D57A047F2F70750CB840CFA77FFF27FA877862C634E739F268676A747E67DC2937F7A5DF834987FC7115E86F1F2AF7FC9F59C1718AE0EFFE90FB3EAB7571FA67DDFADE53C4DF06AE7EE32F5446AB5A206F4D3528EFE2GFDEA7F62F47A62D57A44BE550573983F4F6DF6B5BABD6C41B9F9
	AF6BCA3EE6A7FE9F30AA71797DBC341FE8E2BFB3C0FD60BB44D38D75F1976F6F324261BAA99C6B634CC9455E471B51DECD78504FC0696B439A2E0F5FDCFA873A5DC4DF06CE1FBFDC9B3FB161FA7FCBF87FBF8E7063497F6BCEA97E9727213D69A158CB146D2D42F8056B6B6BC92B7699B6710CC156C8085FC8FC066D67E95AE758E2CEAB7E3C13CE719CB9747343D660E10071F40D5447F366C95467B75127FF26FD0C2C8F63992552A7377DE6094ADA542EADEB8DDFAF1BGB213282CAF1D5316751F9FA9E5ED21
	6767928314D5DDDEFDD9D3C79A5ACAAB4B6DF34EDEE71B4BA157FFBEDFF3E3BCD9D7E17D2FECD1FCD8EDC39FF22FB20F0F5A07622B34712939CA1FEFF79FC7FB75D7E18CD21BD89F62E30FC231BD66443B0CDF8F5A014FC33CB1E91948CBB1AFEF15C8E6A69F332200FF821527452614473D093ED9E0D29EFDCC756DB159D01EF63E1E128A782CD134173FC331F777DEFAE7422840791A9940DE5DA83D3DAE3EC6E4853C1D6C7D3FCE455E8F2BE8FC8BE00F459A7B38E86019BCCA58E36753C6695BC76DCE0A1709
	BE2CFF7A4643A8EDFFE3992D57774BF528AFFB34F97F2BEEEF792395FD7CD910517A1C3E27C05B46FE425746EA002FFEB466CCD339360D7D26CB95AFFA6EBD4C449F9C1FB1DE78CD09B5B2ED2C411FF3DB66F1FF3C0ECE2F3B2F47CE739C1EA7D140BF017CCAFBD36B7B0AB7994FD4204FA01E37F7EBFDCFF6AA3E076FC65F332E8EBC56236F394043GF32B78EAFD5D5C38867515875017E9228F6B3A7EEAFD2E9672F540E640F7DDCD5F73B82B783F037A5EF1A67674FD4B042B353995EB707F9C75D5D38368D3
	C302CE5FAC033E43740EAE691A21473EE557E87595DC230F4727FCBD2A84709557E0BCFE62D26211C15FE77118589B2AEF8AB758FB105B0BGFCA4591B29FABEDB799E4C54G76B20258CBB0580B65F321A44027103DB9EE455EDF7D747C7935431F8B15DF2B2F1F1114BF35A67A74755D8CBC1EEB157A893FD66F7F946EFF8460A32F4576776CFCED7BFB47231427E98145AFC0F954B1744876A9FE867B39EE9EBF4027DC0B71BB7409E2EF870DA585E3062EAF3D3D72B17A78FDC05FED2FB55127EFFF4DE33479
	6D9923773F0E2FC38C82FE7498745F63D57CFF122E05A68570BF583549D4037F5F206FBD4D88603FDADF124EFF69BA7D5C6DE67ECC22787DFD1F627752EE343360BA73BDA4E6FE63B5C35EF57AB8D572BCAB84FC69F59827BF76A9767EF3835A6BB231B754B56F60F57AB8BD1A0F7AC61B6843EBB8ECFB1157E96394E350F747A06A1B9CC01F3AEF1C2653B753900FD43E76108C78D40AC76AB9A59E5F21B9FF6175C3DFC3D7DC2F0DC76375FAFBFB383DD6403B2EC7FBEF7B95FBDB681AA7B200BDF5BF952333B7
	65FAFD3CAA0F233EE9A67A746348CC15BEF61EE250B72A055694EE98FA8DA36786ED7C8BEF504FD9769E574EC30E73B9FAA970555C007D608312369F2C9AD06235036265B371C75FAF0D869E86982FC803BF2F9EB73F26F71C17288E7BF99DC6036C449B308EC7DFD0FC7212CFB38378149644279403CFC70F9B2F1D3A68DA469201BE5CB32878E0EBC29FF2A2066EDF8BA3048FD8CF95917A3CFD36856D55113DB997A572B127E3F84F6C7E166F59E136DAA34C5F7DA00FF97C3710D9F9GE546194A60B33D0752
	947999990A0F57B58C73BBD20DE8EF9A1579E50B227B449CC5777D5B0727BBEC876A4E3A91F51F4FD24A3AE69752ABEE8CD656AF4FD5E4BEDC0BB27E20B2317394195973D1A6067CF8EC3ED2465559CA999FFAE7F8E53CE1B36ACEA45D950B955BE574DD2C3C1B02795B3EC47167474B94FFBC99437B8E17AD8D6DD9035AFBF439E2EFCF0EE26F2BE943AB7F69F43497E1EA8FED58D6AAB1D9FE8AE562027A987E26A21F102B787860B01F5F0F20E767251B310EB25ED2746DEEE5348E1B2B57C1BE9E3621CC694D
	41FC5C703622A77638DA0F200FD823502F8BA9362CAE056C436B94593F978C2F4CD11B50FF8F15F97696A51EEDDB955D6B778ECF77C3FBD1F7B4699ED92C68FEE43B227B3D2A6169CE2AC25D8981629EB847ACA5CA0E8D6CC4BDB9E303555F461D0ACC4ABA14298FAA73413B0A4DEF14AAE55DD1BE4C32527BBDC60F45B87EEA172A0E4A955DDE7E0ED252DB6DF6393DC5EDAEDB7BD941FB5F9E05B735ECF8FEFCEFB77A91EBDAEE2A573DCA2CFE3A86E5D248F7CB25425BDA2D7873C248F9AFFFCB70A46AAE3EA5
	D8BDDCD9237873CDEA2FDDC1E53E31CF1139C86F830B8AAA732B7DCA39569FD44AF5F1185FECAC21EFB64E3885E3B66683C5F7E21D227B3ACB7E9EE47617D0777C430A6ECE55B725ED43547D86692EA55D579CD56278FD2A2F30AF8D374DFE6B1872FB6EF1454787668F4F476BD6208F13484742A6C577E82B227B5BFB06275BCE7DDDB2693E6724D27E8B45C8AF38B5D89EBDF0CA71E7EB0B62CFC763707CD9F51C66153722BF47DA95FF9E246F270E8B6ACF1FCD11296926F9A0699C572678BB4B2E783BE34370
	7CCD5CC07340F128FBC507E2FB69FA1A870E8B66EFD927A2B3F5106601C1E5BE6AD24A70DFE714B2EC89697B26CA999E25F9CE0C29BD3491FFD679DD5A23583BDB9A1E3D0FE99E1798545EE955FB893D1497F937850B49E72A774ACDEA2179E2D0193BBED1EC3E522B146B1EE1164BCE650AB03507B6727A145F7B8794FBCF8F535E5748DE5CED18FB4E730A3E0B1AB91B7C9EB5FAC720F4FB30180C39206849524CA1459E26BFE81E3FAD85DDF5370772BC51DB8754F22DA0638BC96EC023DAEEB448C406A4B7F2
	07667DAEA0B3A5A4398D1A67BCE700CCD2C8F275EBB54FAB5E91E6D9F6C7A8F27367EB1ED7841972106446EED64B55030CBDA4393D1A778D78C1E6CCC8F29DE91A677DC1E6E2C8F22D1A77654401CCFCC8F2EF1E52BCCF8CB21648D064EE51BC6F1683B205A149B5E81F5B87194610643E3EC973DE8D10998CC96EAFFB55F291A0B3BEA439E72BB44FAB024C541064BE533C27A481E4E605A4770A66BDA5D9DF8E3364FEB9A4BFB54F6F9603CCCDC8F2BF50BCF7EC8599D7C8F23F543C27A48CE44606A46754BCA7
	9F8DB213C3121B204917E9A0B3B3A439F04D7BA8D2C0E641D7C2115B25F90FD79E481404A4277D8EEDAD48B407A437CAA3678119701064FE280967B8101990125CCDAB54F231A0B3BDA4B927269DA502CCEAC8F2E11A778EE45F8963681D214865E91E97AE85193A106462B54F1737020CAFA439035D1A718FE4A2C312FBEA03E67C8319A9A149652C570CFFA01394125CEFB54F17CBD1B07EC505A27704E6FCA8G197210645AB563D1BD4858C3126B57541F9FE44604A477A34D7365D1A0B3B1A4B927464FB810
	098FC9AEDAA3178CB2163BC2113BD57B3CBE48948EA1A77659BE55E15CFB7BDC07F10F6E4E83C63E3283C63E0F9B0DFC6D0DC63E489DC63E3BF698795E5FE5642B5AE5646BDCEB64F32DB5722D1DEF645BE8724D38FBB69B79E2B69B791ACC3E99F7BA434877294973CFA3CC1EFF6ACFB7728D269B79361DB2723DFB4A48B741E4EF75F7CC76E01F3199795CB6A35F64CDC63E1FECB2727DF32F116F653DC63E2CEAA35F42EAA35FADBB0DFC376FB472AD4BB7722D4CB7317B3E095D770DFC3F5DED647B43EEA35F
	43EB0CFC0F2DB172051DB4720DBCE9647B19C9FD441954479DA64F66C719BC1BDFEF722D4463A1FED3F13FC97B58EE42771E093E7FB1A96F03A6653DFB05116F5E95A671B3492B11A6F9F5EB3111AF325848775EF1A3DF79F1A35F73A65F3DCC319A79DA3B0DFC0EEEA31FE52311EF6EC6A35F5675C63E1275C63E44C1A35FD303C63ED7CD7A178C137E6A3CC9FFF551243F3A5EA4FEB71B44EFFA0B116F0FADA675EBE2F724095D9F1B70CDB1613B374948775DA6B59F7BE650D229F27B77F3A5E942D3D2E62FD7
	DAC46BB187FBFA9D3E5E04F3BB4AB29E7BDAC63AE4B9A84DEEF37BFC6E6E0312456568709D14B23D4E4EAEB8E6785C9EBFDB0D322821779369B3097DADD2E1977924F9079CBE07B70FFD584EA74DB962E15FC06D75C9735996063CFDF6274F6D75C98B9A5A3DCE0FAF0FFD03288CCEEB9C3D9C813CF03A3CA0EF1F3FA80FABE2FA0A5AFD2E3CCDE4D06B06D7DA28D9E47A79DDBB9F1F7AF0D3EF06B47B2034126D67E8F0F87967D89D769A1B4F5664707602197D52DC0F5B89C440C309FB79A13357B760F254C94B
	BA0EB9BE69F3747A9C76CA7C50AB14E1F99D333415FDFBE1EB2D2D374BE7EBF3B9F657C9169EA806CFDAA248AEDBCF67D67155575DBCECE9152D5CEFBE0F84FF17F2FF79979CC84B529FAEDA3F66031913B224747D52BC175BE65FEFEB07D09D14667A3A3C0E5EAE374B6E776C2D600A3278B04737DB013AD5D4FCD658A296E7F27A2C7F4BE396A6AE5C1F8EB3C32F3415F3326DABC544E12F6E32F9411443FB9000FD5AA11FE8D73C655B701B83AB1A5325B439102C8A9678BDBEE9F6072D5B699A009FB5FCC90F
	D7CE368F3C755AFAFABB5C5EEE70ACCF67991248C0514D3FAA3E656E17CEF1830A28CFFADB0492C437F6BA5C5DDBABBBBA1CBD8ED2GF185752BC9BD63A95271AC715F387C035F3F5686F9D0A3E5F49797737F5B787F6D6C7FB60E5946B15B36C3F0132520FFFA3A9F4B4C13079A972B7E227DD0033E071E7A55D77FD41E1C84255BAFE51EE39F8A06CA48D63844A7BB2E1AF04B6A79E3932EE5F564E12962E7FA17798364BB434EB3E495433EEECC8F06DEC215444E16F356957C7F1B43AEB2134A6532EF112779
	41BC8D663F70F6417F167F9572F9E1G7FFD520A86C70FBDEFDF374D694A639F3FF33673CFBAC3CFF0405143DA0BA3CF7E18D89E7B225A3063909AD68A027DDCE5C21992C90D371FBFCFAB83F86A13960BAFB8737A4461E95EFE175311473E4B87A7CB644FB7EBB8F26A3D8C65F367C19FE44F33BB3D8E56FB8F9479240C22A2783FC8FC18B9CF7EB2F3996BB59C6DECDC641F21C3BDCB9A3B9CF93E810FA34F5D11471E1D7094918B7BD4DDA0163987775564B5B8FCFD50B54FEDBCF2BC0FFD1096181772AF4F66
	092F6E65718FFB82F6E19D1CB9FB379C987066B5147824D5EC104D93DF0DC5DE7CFA2CCF4A645F1F05A37F78A78C567CC84A3279E7C3730EF4F4A828C3B6FF1E60625F8AE5A64547A373F8D1B80B7858E41E7C35C99EE596D0B94ABB358175C119A9288C5B6FF5C263EB139BDF87137439A95A7354F533C0FCD3B00FFDD4D04CB5F8816553790CD5FCCE904EB699021B87E3895969942D9E3AGC33504AE7979EB8D4ECB6D41BFA77EFF720599AB7D430F74F0301F2B320E88352CF61D7F425B7F75F941B5C67FAB
	BF1F113E718B5EFEEF7B024FA06F72FF7EE27A4DAFCC1FF86F9746536F851B5F1A7B7F85A855FD6625E2AB952FEB373226FDFD451559B76C68CB1732763395GB6AFBCA8DF5956C9735B78FA11438B3CB041D32E60396C6EBAA91D4DF7D7A978F6069754860F2D1D2D244C355B3CE7398666521453525C8EEFE79BB7601352CF6FBF8DE67999A37F22CE1A879343EA76D9D13F8FAE57945575B6E70FCFA608C59869B5G8F7F7B7CD5417D66CD407C426FF358817E6EDFB52D79DC55CA7C9726E3FA6C8B636913DF
	70294AF3DF18C83F78450EF4DA5A7F45BEA5ED5E7F49D2AD7D22340A34B7C30D7F170640FE2ECA356E8B937FF706157F6219FCC95AF431D4F63E7946A8A9E564FB6978FDDA7D5E1F187B445E20A972B1CFFAA00DBD5438D87AA65F5BA07DB84C127BE371EE2E25528BE56CE13BA65B19CA0F274AE5770A5B13F0D2D6D65966EB683639DCBE1B6BECE50F7D8318D9D68D789CECD33D101D301B696FC59E19A8E85F5D4D7CBE2312797FD0CB87888875DA8327BCGG9C65GGD0CB818294G94G88G88G2502C3
	B28875DA8327BCGG9C65GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG61BCGGGG
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

			java.awt.GridBagConstraints constraintsS4FrozenRegisterCheckBox = new java.awt.GridBagConstraints();
			constraintsS4FrozenRegisterCheckBox.gridx = 0; constraintsS4FrozenRegisterCheckBox.gridy = 4;
			constraintsS4FrozenRegisterCheckBox.gridwidth = 3;
			constraintsS4FrozenRegisterCheckBox.fill = java.awt.GridBagConstraints.BOTH;
			constraintsS4FrozenRegisterCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsS4FrozenRegisterCheckBox.weightx = 1.0;
			constraintsS4FrozenRegisterCheckBox.insets = new java.awt.Insets(4, 4, 0, 4);
			getIEDPanel().add(getS4FrozenRegisterCheckBox(), constraintsS4FrozenRegisterCheckBox);

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

			java.awt.GridBagConstraints constraintsReadFrozenDemandRegister = new java.awt.GridBagConstraints();
			constraintsReadFrozenDemandRegister.gridx = 0; constraintsReadFrozenDemandRegister.gridy = 3;
			constraintsReadFrozenDemandRegister.gridwidth = 3;
			constraintsReadFrozenDemandRegister.anchor = java.awt.GridBagConstraints.SOUTHWEST;
			constraintsReadFrozenDemandRegister.insets = new java.awt.Insets(0, 4, 0, 4);
			getIEDPanel().add(getReadFrozenDemandRegister(), constraintsReadFrozenDemandRegister);

			java.awt.GridBagConstraints constraintsAlphaFrozenRegisterCheckBox = new java.awt.GridBagConstraints();
			constraintsAlphaFrozenRegisterCheckBox.gridx = 0; constraintsAlphaFrozenRegisterCheckBox.gridy = 5;
			constraintsAlphaFrozenRegisterCheckBox.gridwidth = 3;
			constraintsAlphaFrozenRegisterCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAlphaFrozenRegisterCheckBox.insets = new java.awt.Insets(0, 4, 4, 4);
			getIEDPanel().add(getAlphaFrozenRegisterCheckBox(), constraintsAlphaFrozenRegisterCheckBox);

			java.awt.GridBagConstraints constraintsSeparatorFrozenRegister = new java.awt.GridBagConstraints();
			constraintsSeparatorFrozenRegister.gridx = 0; constraintsSeparatorFrozenRegister.gridy = 2;
			constraintsSeparatorFrozenRegister.gridwidth = 3;
			constraintsSeparatorFrozenRegister.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSeparatorFrozenRegister.insets = new java.awt.Insets(4, 4, 4, 4);
			getIEDPanel().add(getSeparatorFrozenRegister(), constraintsSeparatorFrozenRegister);
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getReadFrozenDemandRegister() {
	if (ivjReadFrozenDemandRegister == null) {
		try {
			ivjReadFrozenDemandRegister = new javax.swing.JLabel();
			ivjReadFrozenDemandRegister.setName("ReadFrozenDemandRegister");
			ivjReadFrozenDemandRegister.setFont(new java.awt.Font("dialog", 0, 14));
			ivjReadFrozenDemandRegister.setText("Read Frozen Demand Register");
			ivjReadFrozenDemandRegister.setForeground(java.awt.Color.black);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjReadFrozenDemandRegister;
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
 * Return the JCheckBox1 property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getS4FrozenRegisterCheckBox() {
	if (ivjS4FrozenRegisterCheckBox == null) {
		try {
			ivjS4FrozenRegisterCheckBox = new javax.swing.JCheckBox();
			ivjS4FrozenRegisterCheckBox.setName("S4FrozenRegisterCheckBox");
			ivjS4FrozenRegisterCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjS4FrozenRegisterCheckBox.setText("Landis-Gyr S4");
			// user code begin {1}
			ivjS4FrozenRegisterCheckBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(READ_FROZEN_PARAM));
			ivjS4FrozenRegisterCheckBox.setSelected(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjS4FrozenRegisterCheckBox;
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
 * Return the SeparatorFrozenRegister property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getSeparatorFrozenRegister() {
	if (ivjSeparatorFrozenRegister == null) {
		try {
			ivjSeparatorFrozenRegister = new javax.swing.JSeparator();
			ivjSeparatorFrozenRegister.setName("SeparatorFrozenRegister");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSeparatorFrozenRegister;
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
	getFrozenRegisterGroup().add(getS4FrozenRegisterCheckBox());
	getFrozenRegisterGroup().add(getAlphaFrozenRegisterCheckBox());
	getS4FrozenRegisterCheckBox().addItemListener(this);
	getAlphaFrozenRegisterCheckBox().addItemListener(this);
	
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
    String frozen = ((String)getScriptTemplate().getParamToValueMap().get(READ_FROZEN_PARAM));
	if(frozen.length() > 0)
	{
		if( frozen.indexOf("72") > 0)	//we have an alpha command (this way we don't have to have the register be exact (0 vs 00)
			getAlphaFrozenRegisterCheckBox().setSelected(true);
		else	//default rest to this one?
			getS4FrozenRegisterCheckBox().setSelected(true);
		
	}
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
	else if(e.getSource() == getAlphaFrozenRegisterCheckBox())
	{
	    if( e.getStateChange() == ItemEvent.SELECTED)
	        getScriptTemplate().getParamToValueMap().put(READ_FROZEN_PARAM, READ_FROZEN_ALPHA_COMMAND_STRING);
	    else
	        getScriptTemplate().getParamToValueMap().put(READ_FROZEN_PARAM, "");
	}
	else if(e.getSource() == getS4FrozenRegisterCheckBox())
	{
		if( e.getStateChange() == ItemEvent.SELECTED)
			getScriptTemplate().getParamToValueMap().put(READ_FROZEN_PARAM, READ_FROZEN_S4_COMMAND_STRING);
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
    if( getGroupComboBox().getItemCount() > 0)	//make sure there are items here before "getting" a selected item
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
 * @param string
 */
public void setScriptNameText(String name)
{
    getScriptNameTextField().setText(name);
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
	this.templateType = templateType;
	
    //Set the tabs based on the template type.
    getTabbedPane().removeAll();
    int tabCount = 0;
    if( (ScriptTemplateTypes.isMeteringTemplate(templateType)))
    	getTabbedPane().insertTab("Meter Read", null, getMeterReadSetupPanel(), null, tabCount++);
    
    if( (ScriptTemplateTypes.hasBilling(templateType) && ScriptTemplateTypes.hasNotification(templateType)))
        getTabbedPane().insertTab("Options", null, getOptionsPanel(), null, tabCount++);
    
    if( (ScriptTemplateTypes.isNoTemplate(templateType)))
    {
       	getDescriptionLabel().setEnabled(false);
       	getDescriptionTextField().setEnabled(false);
    }
    
    //Show text editor for all types of scripts, TODO - make this an admin role/property?
    getTabbedPane().insertTab("Text Editor", null, getScriptScrollPane(), null, tabCount++);

	getIEDPanel().setVisible(ScriptTemplateTypes.isIEDTemplate(templateType));
    //The read_with..._param is for showing options to retry during a multiple read schedule, 
	//so we NOT the result which is from a retry script, one that is a read once type script 
    getRetryPanel().setVisible(!ScriptTemplateTypes.isRetryTemplate(templateType));

    CTILogger.info("Set TemplateType, component visiblity updated.");
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
	 * @return
	 */
	public javax.swing.ButtonGroup getFrozenRegisterGroup()
	{
		if( frozenRegisterGroup == null)
			frozenRegisterGroup = new ButtonGroup();
		return frozenRegisterGroup;
	}

	/**
	 * @param group
	 */
	public void setFrozenRegisterGroup(javax.swing.ButtonGroup group)
	{
		frozenRegisterGroup = group;
	}

}

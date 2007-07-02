package com.cannontech.macs.schedule.wizard;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupRenderer;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupTreeFactory;
import com.cannontech.common.gui.tree.CustomRenderJTree;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.schedule.script.ScriptTemplate;
import com.cannontech.database.data.schedule.script.ScriptTemplateTypes;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.macs.message.ScriptFile;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
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
    private javax.swing.JTree meterReadGroupTree = null;
    private javax.swing.JTree optionsGroupTree = null;
    private javax.swing.JScrollPane meterReadGroupListScrollPane = null;
    private javax.swing.JScrollPane optionsGroupListScrollPane = null;
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
	private javax.swing.JComboBox ivjBillingGroupComboBox = null;
	private javax.swing.JLabel ivjBillingGroupNameLabel = null;
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
    private java.awt.Frame owner = (java.awt.Frame)com.cannontech.common.util.CtiUtilities.getParentFrame(this);
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public ScriptScheduleSetupPanel() {
    
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
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
//	else if (e.getSource() == getGroupTypeComboBox()) {
//		loadGroupComboBox(getGroupComboBox(), getGroupTypeComboBox().getSelectedIndex());
//	}
	else if (e.getSource() == getBillingGroupTypeComboBox()) {
		loadGroupComboBox(getBillingGroupComboBox(), getBillingGroupTypeComboBox().getSelectedIndex());
	}	
    fireInputUpdate();
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
 * Return the GroupComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBillingGroupComboBox() {
	if (ivjBillingGroupComboBox == null) {
		try {
			ivjBillingGroupComboBox = new javax.swing.JComboBox();
			ivjBillingGroupComboBox.setName("BillingGroupComboBox");
			// user code begin {1}
			ivjBillingGroupComboBox.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_GROUP_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingGroupComboBox;
}
/**
 * Return the GroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBillingGroupNameLabel() {
	if (ivjBillingGroupNameLabel == null) {
		try {
			ivjBillingGroupNameLabel = new javax.swing.JLabel();
			ivjBillingGroupNameLabel.setName("BillingGroupNameLabel");
			ivjBillingGroupNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBillingGroupNameLabel.setText("Group Name:");
			// user code begin {1}
			ivjBillingGroupNameLabel.setToolTipText((String)getScriptTemplate().getParamToDescMap().get(BILLING_GROUP_NAME_PARAM));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingGroupNameLabel;
}
/**
 * Return the BillingPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getBillingPanel() {
	if (ivjBillingPanel == null) {
		try {
			ivjBillingPanel = new javax.swing.JPanel();
			ivjBillingPanel.setName("BillingPanel");
			ivjBillingPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjBillingPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsBilllingFilePathLabel = new java.awt.GridBagConstraints();
			constraintsBilllingFilePathLabel.gridx = 0; constraintsBilllingFilePathLabel.gridy = 6;
			constraintsBilllingFilePathLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBilllingFilePathLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBilllingFilePathLabel(), constraintsBilllingFilePathLabel);

			java.awt.GridBagConstraints constraintsBillingFilePathTextBox = new java.awt.GridBagConstraints();
			constraintsBillingFilePathTextBox.gridx = 1; constraintsBillingFilePathTextBox.gridy = 6;
			constraintsBillingFilePathTextBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingFilePathTextBox.weightx = 1.0;
			constraintsBillingFilePathTextBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFilePathTextBox(), constraintsBillingFilePathTextBox);

			java.awt.GridBagConstraints constraintsBillingFileBrowseButton = new java.awt.GridBagConstraints();
			constraintsBillingFileBrowseButton.gridx = 3; constraintsBillingFileBrowseButton.gridy = 6;
			constraintsBillingFileBrowseButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFileBrowseButton(), constraintsBillingFileBrowseButton);

			java.awt.GridBagConstraints constraintsBillingFileNameLabel = new java.awt.GridBagConstraints();
			constraintsBillingFileNameLabel.gridx = 0; constraintsBillingFileNameLabel.gridy = 5;
			constraintsBillingFileNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBillingFileNameLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFileNameLabel(), constraintsBillingFileNameLabel);

			java.awt.GridBagConstraints constraintsBillingFileNameTextField = new java.awt.GridBagConstraints();
			constraintsBillingFileNameTextField.gridx = 1; constraintsBillingFileNameTextField.gridy = 5;
			constraintsBillingFileNameTextField.gridwidth = 2;
			constraintsBillingFileNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingFileNameTextField.weightx = 1.0;
			constraintsBillingFileNameTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFileNameTextField(), constraintsBillingFileNameTextField);

			java.awt.GridBagConstraints constraintsBillingFormatComboBox = new java.awt.GridBagConstraints();
			constraintsBillingFormatComboBox.gridx = 1; constraintsBillingFormatComboBox.gridy = 2;
			constraintsBillingFormatComboBox.gridwidth = 2;
			constraintsBillingFormatComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingFormatComboBox.weightx = 1.0;
			constraintsBillingFormatComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFormatComboBox(), constraintsBillingFormatComboBox);

			java.awt.GridBagConstraints constraintsEnergyDaysLabel = new java.awt.GridBagConstraints();
			constraintsEnergyDaysLabel.gridx = 0; constraintsEnergyDaysLabel.gridy = 4;
			constraintsEnergyDaysLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEnergyDaysLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getEnergyDaysLabel(), constraintsEnergyDaysLabel);

			java.awt.GridBagConstraints constraintsDemandDaysLabel = new java.awt.GridBagConstraints();
			constraintsDemandDaysLabel.gridx = 0; constraintsDemandDaysLabel.gridy = 3;
			constraintsDemandDaysLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDemandDaysLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getDemandDaysLabel(), constraintsDemandDaysLabel);

			java.awt.GridBagConstraints constraintsDemandDaysTextField = new java.awt.GridBagConstraints();
			constraintsDemandDaysTextField.gridx = 1; constraintsDemandDaysTextField.gridy = 3;
			constraintsDemandDaysTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDemandDaysTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDemandDaysTextField.weightx = 1.0;
			constraintsDemandDaysTextField.insets = new java.awt.Insets(4, 4, 4, 150);
			getBillingPanel().add(getDemandDaysTextField(), constraintsDemandDaysTextField);

			java.awt.GridBagConstraints constraintsEnergyDaysTextField = new java.awt.GridBagConstraints();
			constraintsEnergyDaysTextField.gridx = 1; constraintsEnergyDaysTextField.gridy = 4;
			constraintsEnergyDaysTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsEnergyDaysTextField.weightx = 1.0;
			constraintsEnergyDaysTextField.insets = new java.awt.Insets(4, 4, 4, 150);
			getBillingPanel().add(getEnergyDaysTextField(), constraintsEnergyDaysTextField);

			java.awt.GridBagConstraints constraintsBillingFormatLabel = new java.awt.GridBagConstraints();
			constraintsBillingFormatLabel.gridx = 0; constraintsBillingFormatLabel.gridy = 2;
			constraintsBillingFormatLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBillingFormatLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getBillingPanel().add(getBillingFormatLabel(), constraintsBillingFormatLabel);
            
            java.awt.GridBagConstraints constraintsGroupListScrollPane = new java.awt.GridBagConstraints();
            constraintsGroupListScrollPane.gridx = 0; constraintsGroupListScrollPane.gridy = 0;
            constraintsGroupListScrollPane.gridwidth = 4;
            constraintsGroupListScrollPane.gridheight = 2;
            constraintsGroupListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
            constraintsGroupListScrollPane.weightx = 1.0;
            constraintsGroupListScrollPane.weighty = 1.0;
            constraintsGroupListScrollPane.insets = new java.awt.Insets(4,4,4,4);
            getBillingPanel().add(getOptionsGroupListScrollPane(), constraintsGroupListScrollPane);
			this.revalidate();
		} catch (java.lang.Throwable ivjExc) {
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
 * Return the GroupList property value.
 * @return javax.swing.JList
 */
private javax.swing.JTree getMeterReadGroupList() {
    if (meterReadGroupTree == null) {
        DeviceGroupTreeFactory modelFactory = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
        try {
            CustomRenderJTree<DeviceGroup> customTree = new CustomRenderJTree<DeviceGroup>(DeviceGroup.class);
            customTree.setRenderer(new DeviceGroupRenderer());
            meterReadGroupTree = customTree;
            meterReadGroupTree.setName("GroupList");
            meterReadGroupTree.setBounds(0, 0, 160, 120);
            meterReadGroupTree.setShowsRootHandles(true);
            meterReadGroupTree.setRootVisible(false);

            TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
            selectionModel.clearSelection();
            selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            meterReadGroupTree.setSelectionModel(selectionModel);
            TreeModel model = modelFactory.getModel();
            meterReadGroupTree.setModel(model);

        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return meterReadGroupTree;
}

/**
 * Return the GroupList property value.
 * @return javax.swing.JList
 */
private javax.swing.JTree getOptionsGroupList() {
    if (optionsGroupTree == null) {
        DeviceGroupTreeFactory modelFactory = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
        try {
            CustomRenderJTree<DeviceGroup> customTree = new CustomRenderJTree<DeviceGroup>(DeviceGroup.class);
            customTree.setRenderer(new DeviceGroupRenderer());
            optionsGroupTree = customTree;
            optionsGroupTree.setName("GroupList");
            optionsGroupTree.setBounds(0, 0, 160, 120);
            optionsGroupTree.setShowsRootHandles(true);
            optionsGroupTree.setRootVisible(false);

            TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
            selectionModel.clearSelection();
            selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            optionsGroupTree.setSelectionModel(selectionModel);
            TreeModel model = modelFactory.getModel();
            optionsGroupTree.setModel(model);

        } catch (Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return optionsGroupTree;
}

/**
 * Return the GroupListScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getMeterReadGroupListScrollPane() {
    if (meterReadGroupListScrollPane == null) {
        try {
            meterReadGroupListScrollPane = new javax.swing.JScrollPane();
            meterReadGroupListScrollPane.setName("MeterReadGroupListScrollPane");
            meterReadGroupListScrollPane.setToolTipText("Select Billing Collection Group(s).");
            meterReadGroupListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            getMeterReadGroupListScrollPane().setViewportView(getMeterReadGroupList());
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return meterReadGroupListScrollPane;
}

/**
 * Return the GroupListScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getOptionsGroupListScrollPane() {
    if (optionsGroupListScrollPane == null) {
        try {
            optionsGroupListScrollPane = new javax.swing.JScrollPane();
            optionsGroupListScrollPane.setName("OptionsGroupListScrollPane");
            optionsGroupListScrollPane.setToolTipText("Select Billing Collection Group(s).");
            optionsGroupListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            getOptionsGroupListScrollPane().setViewportView(getOptionsGroupList());
//            optionsGroupListScrollPane.setPreferredSize(new Dimension(200,200));
//            optionsGroupListScrollPane.setMinimumSize(new Dimension(200,200));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return optionsGroupListScrollPane;
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
            
            java.awt.GridBagConstraints constraintsGroupListScrollPane = new java.awt.GridBagConstraints();
            constraintsGroupListScrollPane.gridx = 0; constraintsGroupListScrollPane.gridy = 0;
            constraintsGroupListScrollPane.gridwidth = 2;
            constraintsGroupListScrollPane.gridheight = 2;
            constraintsGroupListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
            constraintsGroupListScrollPane.weightx = 1.0;
            constraintsGroupListScrollPane.weighty = 1.0;
            constraintsGroupListScrollPane.insets = new java.awt.Insets(5, 5, 5, 5);
            getMeterReadPanel().add(getMeterReadGroupListScrollPane(), constraintsGroupListScrollPane);

		} catch (java.lang.Throwable ivjExc) {
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
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
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
            
            java.awt.GridBagConstraints constraintsSendEmailCheckBox = new java.awt.GridBagConstraints();
            constraintsSendEmailCheckBox.gridx = 0; constraintsSendEmailCheckBox.gridy = 0;
            constraintsSendEmailCheckBox.anchor = java.awt.GridBagConstraints.SOUTHWEST;
            constraintsSendEmailCheckBox.weightx = 1.0;
            constraintsSendEmailCheckBox.insets = new java.awt.Insets(4, 4, 0, 4);
            getOptionsPanel().add(getSendEmailCheckBox(), constraintsSendEmailCheckBox);

			java.awt.GridBagConstraints constraintsNotificationPanel = new java.awt.GridBagConstraints();
			constraintsNotificationPanel.gridx = 0; constraintsNotificationPanel.gridy = 1;
			constraintsNotificationPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNotificationPanel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsNotificationPanel.weightx = 1.0;
			constraintsNotificationPanel.insets = new java.awt.Insets(0, 4, 4, 4);
			getOptionsPanel().add(getNotificationPanel(), constraintsNotificationPanel);
            
            java.awt.GridBagConstraints constraintsGenerateBillingCheckBox = new java.awt.GridBagConstraints();
            constraintsGenerateBillingCheckBox.gridx = 0; constraintsGenerateBillingCheckBox.gridy = 2;
            constraintsGenerateBillingCheckBox.anchor = java.awt.GridBagConstraints.SOUTHWEST;
            constraintsGenerateBillingCheckBox.weightx = 1.0;
            constraintsGenerateBillingCheckBox.fill = java.awt.GridBagConstraints.BOTH;
            constraintsGenerateBillingCheckBox.insets = new java.awt.Insets(4, 4, 0, 4);
            getOptionsPanel().add(getGenerateBillingCheckBox(), constraintsGenerateBillingCheckBox);

			java.awt.GridBagConstraints constraintsBillingPanel = new java.awt.GridBagConstraints();
			constraintsBillingPanel.gridx = 0; constraintsBillingPanel.gridy = 3;
			constraintsBillingPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsBillingPanel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsBillingPanel.weightx = 1.0;
			constraintsBillingPanel.weighty = 1.0;
			constraintsBillingPanel.insets = new java.awt.Insets(0, 4, 4, 4);
			getOptionsPanel().add(getBillingPanel(), constraintsBillingPanel);

			
		} catch (java.lang.Throwable ivjExc) {
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
@SuppressWarnings("unchecked")
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
		setName("ScriptSchedulePanel");
		setLayout(new java.awt.GridBagLayout());

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
	getScriptNameTextField().addKeyListener(this);
	getScriptTextArea().addCaretListener(this);
	getScriptTextArea().addFocusListener(this);
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
	getBillingGroupComboBox().addActionListener(this);
	getBillingGroupTypeComboBox().addActionListener(this);
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

@SuppressWarnings({ "unchecked", "cast" })
private void initSwingCompValues()
{
    getScriptTemplate().getParamToValueMap().put(IED_FLAG_PARAM, String.valueOf(ScriptTemplateTypes.isIEDTemplate(getTemplateType())));

    //The read_with..._param is for showing options to retry during a multiple read schedule, 
	//so we NOT the result which is from a retry script, one that is a read once type script 
    getScriptTemplate().getParamToValueMap().put(READ_WITH_RETRY_FLAG_PARAM, String.valueOf(!ScriptTemplateTypes.isRetryTemplate(getTemplateType())));
    
    getScriptNameTextField().setText((String)getScriptTemplate().getParamToValueMap().get(SCRIPT_FILE_NAME_PARAM));
    getDescriptionTextField().setText((String)getScriptTemplate().getParamToValueMap().get(SCRIPT_DESC_PARAM));
    getFilePathTextField().setText((String)getScriptTemplate().getParamToValueMap().get(FILE_PATH_PARAM));
    getMissedFileNameTextField().setText((String)getScriptTemplate().getParamToValueMap().get(MISSED_FILE_NAME_PARAM));
    getSuccessFileNameTextField().setText((String)getScriptTemplate().getParamToValueMap().get(SUCCESS_FILE_NAME_PARAM));
    getPorterTimeoutTextField().setText((String)getScriptTemplate().getParamToValueMap().get(PORTER_TIMEOUT_PARAM));
    
    getRetryCountTextField().setText((String)getScriptTemplate().getParamToValueMap().get(RETRY_COUNT_PARAM));
    getMaxRetryHoursTextField().setText((String)getScriptTemplate().getParamToValueMap().get(MAX_RETRY_HOURS_PARAM));
    getQueueOffCountTextField().setText((String)getScriptTemplate().getParamToValueMap().get(QUEUE_OFF_COUNT_PARAM));

    //This must be set after the groupTypeComboBox is set, then we have the group values for the correct type loaded
    DeviceGroupTreeFactory modelFactory = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
    DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
    String groupName = (String)getScriptTemplate().getParamToValueMap().get(GROUP_NAME_PARAM);
    if(groupName != null && groupName.length() > 0) {
        DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
        TreePath pathForGroup = modelFactory.getPathForGroup((TreeNode) getMeterReadGroupList().getModel().getRoot(), group);
        getMeterReadGroupList().getSelectionModel().addSelectionPath(pathForGroup);
        meterReadGroupTree.makeVisible(pathForGroup);
    }
    
    //Billing setup
    enableContainer(getBillingPanel(), Boolean.valueOf((String)getScriptTemplate().getParamToValueMap().get(BILLING_FLAG_PARAM)).booleanValue());
    getGenerateBillingCheckBox().setSelected(Boolean.valueOf((String)getScriptTemplate().getParamToValueMap().get(BILLING_FLAG_PARAM)).booleanValue());
    getBillingFileNameTextField().setText((String)getScriptTemplate().getParamToValueMap().get(BILLING_FILE_NAME_PARAM));
    getBillingFilePathTextBox().setText((String)getScriptTemplate().getParamToValueMap().get(BILLING_FILE_PATH_PARAM));
    getBillingFormatComboBox().setSelectedItem((String)getScriptTemplate().getParamToValueMap().get(BILLING_FORMAT_PARAM));
    getDemandDaysTextField().setText((String)getScriptTemplate().getParamToValueMap().get(BILLING_DEMAND_DAYS_PARAM));
    getEnergyDaysTextField().setText((String)getScriptTemplate().getParamToValueMap().get(BILLING_ENERGY_DAYS_PARAM));
    
    if( ((String)getScriptTemplate().getParamToValueMap().get(BILLING_GROUP_TYPE_PARAM)).equalsIgnoreCase("altgroup")) {
        getBillingGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.ALTGROUP_DISPLAY_STRING);
        loadGroupComboBox(getBillingGroupComboBox(), DeviceMeterGroup.TEST_COLLECTION_GROUP);
    }
    else if( ((String)getScriptTemplate().getParamToValueMap().get(BILLING_GROUP_TYPE_PARAM)).equalsIgnoreCase("billgroup")) {
        getBillingGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.BILLINGGROUP_DISPLAY_STRING);
        loadGroupComboBox(getBillingGroupComboBox(), DeviceMeterGroup.BILLING_GROUP);
    }
    else { //if( ((String)getScriptTemplate().getParamToValueMap().get(BILLING_GROUP_TYPE_PARAM)).equalsIgnoreCase("group"))
        getBillingGroupTypeComboBox().setSelectedItem(DeviceMeterGroup.COLLECTIONGROUP_DISPLAY_STRING);
        loadGroupComboBox(getBillingGroupComboBox(), DeviceMeterGroup.COLLECTION_GROUP);
    }
    
    //This must be set after the billingGroupTypeComboBox is set, then we have the group values for the correct type loaded
    getBillingGroupComboBox().setSelectedItem((String)getScriptTemplate().getParamToValueMap().get(BILLING_GROUP_NAME_PARAM));
    
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
@SuppressWarnings("unchecked")
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
@SuppressWarnings("unchecked")
private void loadParamMapFromSwingComp()
{
    //Some of the flag parameters are set throughout the code, i.e. on item change and panel load.
    
    //Change the filename to include an extension if it doesn't exist.
	String fileName = getScriptNameTextField().getText();
	if (!fileName.endsWith(".ctl"))
	    fileName += ".ctl";
    getScriptTemplate().getParamToValueMap().put(SCRIPT_FILE_NAME_PARAM, fileName);
    getScriptTemplate().getParamToValueMap().put(SCRIPT_DESC_PARAM, getDescriptionTextField().getText());
    DeviceGroupTreeFactory modelFactory = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
    DeviceGroup group = modelFactory.getGroupForPath(getMeterReadGroupList().getSelectionPath());
    getScriptTemplate().getParamToValueMap().put(GROUP_NAME_PARAM, group.getFullName());

    getScriptTemplate().getParamToValueMap().put(PORTER_TIMEOUT_PARAM, getPorterTimeoutTextField().getText());
    getScriptTemplate().getParamToValueMap().put(FILE_PATH_PARAM, getFilePathTextField().getText());
    getScriptTemplate().getParamToValueMap().put(MISSED_FILE_NAME_PARAM, getMissedFileNameTextField().getText());
    getScriptTemplate().getParamToValueMap().put(SUCCESS_FILE_NAME_PARAM, getSuccessFileNameTextField().getText());

    //Billing in script
    getScriptTemplate().getParamToValueMap().put(BILLING_FLAG_PARAM, String.valueOf(getGenerateBillingCheckBox().isSelected()).toString());
    getScriptTemplate().getParamToValueMap().put(BILLING_FILE_NAME_PARAM, getBillingFileNameTextField().getText());
    getScriptTemplate().getParamToValueMap().put(BILLING_FILE_PATH_PARAM, getBillingFilePathTextBox().getText());
    getScriptTemplate().getParamToValueMap().put(BILLING_FORMAT_PARAM, getBillingFormatComboBox().getSelectedItem().toString());
    getScriptTemplate().getParamToValueMap().put(BILLING_ENERGY_DAYS_PARAM, getEnergyDaysTextField().getText());
    getScriptTemplate().getParamToValueMap().put(BILLING_DEMAND_DAYS_PARAM, getDemandDaysTextField().getText());

    if(getOptionsGroupList().getSelectionPath() != null && ((TreeNode)getOptionsGroupList().getSelectionPath().getLastPathComponent()).isLeaf()) {
        DeviceGroup billingGroup = modelFactory.getGroupForPath(getOptionsGroupList().getSelectionPath());
        getScriptTemplate().getParamToValueMap().put(GROUP_NAME_PARAM, billingGroup.getFullName());
    }
    
    //Notification in script
    getScriptTemplate().getParamToValueMap().put(NOTIFICATION_FLAG_PARAM, String.valueOf(getSendEmailCheckBox().isSelected()).toString());
    if (getNotifyGroupComboBox().getSelectedItem() != null)
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
@SuppressWarnings("deprecation")
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
@SuppressWarnings("static-access")
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
		@SuppressWarnings("unchecked")
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
	
	public void loadGroupComboBox(javax.swing.JComboBox comboBox, int groupType) {
		comboBox.removeAllItems();
		String [] groups = null;
		try {
			if( groupType == DeviceMeterGroup.TEST_COLLECTION_GROUP) {
					groups = DeviceMeterGroup.getDeviceTestCollectionGroups();
			} else if (groupType == DeviceMeterGroup.BILLING_GROUP) {
				groups = DeviceMeterGroup.getDeviceBillingGroups();
			} else {
				groups = DeviceMeterGroup.getDeviceCollectionGroups();
			}
			for (int i = 0; i < groups.length; i++)
				comboBox.addItem(groups[i]);
		} catch (SQLException e1) {
			CTILogger.error(e1);
		}
	}
}

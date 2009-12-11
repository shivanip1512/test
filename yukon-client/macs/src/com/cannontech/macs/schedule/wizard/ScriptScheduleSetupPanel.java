package com.cannontech.macs.schedule.wizard;

import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_DEMAND_DAYS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_ENERGY_DAYS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FILE_PATH_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_FORMAT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.BILLING_GROUP_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.EMAIL_SUBJECT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.FILE_PATH_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.GROUP_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.IED_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.IED_TYPE_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.MAX_RETRY_HOURS_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.MISSED_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.NOTIFICATION_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.NOTIFY_GROUP_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.PORTER_TIMEOUT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.QUEUE_OFF_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.READ_FROZEN_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.READ_WITH_RETRY_FLAG_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.RESET_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.RETRY_COUNT_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCHEDULE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCRIPT_DESC_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SCRIPT_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.SUCCESS_FILE_NAME_PARAM;
import static com.cannontech.database.data.schedule.script.ScriptParameters.TOU_RATE_PARAM;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.lang.StringUtils;

import com.cannontech.billing.FileFormatTypes;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupRenderer;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.device.groups.service.DeviceGroupTreeFactory;
import com.cannontech.common.device.groups.service.NonHiddenDeviceGroupPredicate;
import com.cannontech.common.gui.tree.CustomRenderJTree;
import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.schedule.script.ScriptTemplate;
import com.cannontech.database.data.schedule.script.ScriptTemplateTypes;
import com.cannontech.message.macs.message.Schedule;
import com.cannontech.message.macs.message.ScriptFile;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;
import com.cannontech.yukon.IMACSConnection;
import com.cannontech.yukon.conns.ConnPool;
import com.klg.jclass.util.swing.JCSpinNumberBox;
import com.klg.jclass.util.value.JCValueEvent;
import com.klg.jclass.util.value.JCValueListener;


public class ScriptScheduleSetupPanel extends DataInputPanel implements JCValueListener, ActionListener, FocusListener, ItemListener, KeyListener, CaretListener, ChangeListener, TreeSelectionListener {
    
    private int templateType = ScriptTemplateTypes.METER_READ_SCRIPT;
    private ScriptTemplate scriptTemplate = null;
    //String for holding the script file.
    private String scriptText = null;
    private int scheduleId = 0;    
    private JTree meterReadGroupTree = null;
    private JTree billingGroupTree = null;
    private JScrollPane meterReadGroupListScrollPane = null;
    private JScrollPane optionsGroupListScrollPane = null;
	private JLabel ivjBillingFileNameLabel = null;
	private JTextField ivjBillingFileNameTextField = null;
	private JTextField ivjBillingFilePathTextBox = null;
	private JComboBox ivjBillingFormatComboBox = null;
	private JPanel ivjBillingPanel = null;
	private JLabel ivjBilllingFilePathLabel = null;
	private JButton ivjBrowseButton = null;
	private JLabel ivjDemandDaysLabel = null;
	private JCSpinNumberBox ivjDemandResetSpinBox = null;
	private JLabel ivjEnergyDaysLabel = null;
	private JLabel ivjFilePathLabel = null;
	private JTextField ivjFilePathTextField = null;
	private JPanel ivjIEDPanel = null;
	private JPanel ivjMeterReadPanel = null;
	private JLabel ivjMissedFileNameLabel = null;
	private JTextField ivjMissedFileNameTextField = null;
	private JLabel ivjPorterTimeoutLabel = null;
	private JTextField ivjPorterTimeoutTextField = null;
	private JLabel ivjResetCountLabel = null;
	private JLabel ivjSuccessFileNameLabel = null;
	private JTextField ivjSuccessFileNameTextField = null;
	private JComboBox ivjTOURateComboBox = null;
	private JLabel ivjTOURateLabel = null;
	private JButton ivjBillingFileBrowseButton = null;
	private JPanel ivjNotificationPanel = null;
	private JPanel ivjRetryPanel = null;
	private JTabbedPane ivjTabbedPane = null;
	private JTextField ivjDemandDaysTextField = null;
	private JTextField ivjEnergyDaysTextField = null;
	private JCheckBox ivjGenerateBillingCheckBox = null;
	private JLabel ivjMaxRetryHoursLabel = null;
	private JTextField ivjMaxRetryHoursTextField = null;
	private JLabel ivjMessageSubjectLabel = null;
	private JTextField ivjMessageSubjectTextField = null;
	private JComboBox ivjNotifyGroupComboBox = null;
	private JLabel ivjNotifyGroupLabel = null;
	private JLabel ivjQueueOffCountLabel = null;
	private JTextField ivjQueueOffCountTextField = null;
	private JLabel ivjRetryCountLabel = null;
	private JTextField ivjRetryCountTextField = null;
	private JLabel ivjSecsLabel = null;
	private JCheckBox ivjSendEmailCheckBox = null;
	private JLabel ivjBillingFormatLabel = null;
	private JPanel ivjMeterReadSetupPanel = null;
	private JLabel ivjExampleLabel = null;
	private JLabel ivjScriptNameLabel = null;
	private JTextField ivjScriptNameTextField = null;
	private JLabel ivjDescriptionLabel = null;
	private JTextField ivjDescriptionTextField = null;
	private JPanel ivjScriptNamePanel = null;
	private JScrollPane ivjScriptScrollPane = null;
	private JTextArea ivjScriptTextArea = null;
	private JPanel ivjOptionsPanel = null;
	private JCheckBox ivjAlphaFrozenRegisterCheckBox = null;
	private JLabel ivjReadFrozenDemandRegister = null;
	private JCheckBox ivjS4FrozenRegisterCheckBox = null;
	private JSeparator ivjSeparatorFrozenRegister = null;
	private ButtonGroup frozenRegisterGroup  = null;
    private JLabel ivjIED400TypeLabel = null;
	private JComboBox ivjIED400TypeComboBox = null;
    private JSeparator ivjSeparatorResetDemand = null;
	private JCheckBox ivjResetDemandEnabledCheckBox = null;
    
    /**
     * Constructor
     */
    public ScriptScheduleSetupPanel() {
        this(false);
    }

    /**
     * Constructor
     */
    public ScriptScheduleSetupPanel(boolean editMode) {
        super();
    	initialize();
    	
    	if( !editMode )
    		//Don't init the connections during edit Mode, they are inited in the setValues call instead
    		initializeConnections();
    }
    
    public IMACSConnection getIMACSConnection() 
    {
        return (IMACSConnection)ConnPool.getInstance().getDefMacsConn();
    }    
    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */
    public void actionPerformed(ActionEvent e) {
    	if ( e.getSource() == getBillingFileBrowseButton()){
    		String file = browseOutput(getBillingFilePathTextBox().getText());
    		if( file != null ) {
    			getBillingFilePathTextBox().setText( file );
            }
    		repaint();
    	} else if ( e.getSource() == getBrowseButton()) {
    		String file = browseOutput(getFilePathTextField().getText());
    		if( file != null ) {
    			getFilePathTextField().setText( file );
            }
    		repaint();
    	}
        fireInputUpdate();
    }
    
    /**
     * Comment
     */
    private String browseOutput(String directory) {
    	JFileChooser chooser = new JFileChooser();
    	chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    	chooser.setCurrentDirectory(new java.io.File(directory));
    	chooser.setDialogTitle("Choose Folder");
    	chooser.setApproveButtonText("Select Folder");
    	int returnVal = chooser.showOpenDialog(this);
    	if( returnVal == JFileChooser.APPROVE_OPTION ) {
    		String macsSupportedPath = chooser.getSelectedFile().getPath().replace('\\', '/');
    		if( macsSupportedPath.charAt(macsSupportedPath.length()-1) != '/')
    			macsSupportedPath += "/";
    		return macsSupportedPath;
    	}
    	return null;
    }
    
    /**
     * @return
     */
    private String buildScript() {
        //Simply retrun the text area if this is a noTemplate script.
        if (ScriptTemplateTypes.isNoTemplate(getTemplateType())) {
            return getScriptTextArea().getText();
        }
        //Before we do anything, load the parameters map from the swing comps
        loadParamMapFromSwingComp();
        
    	String text = "";
    	//Generate the header code
    	text += ScriptTemplate.buildScriptHeaderCode();
    	
    	//Get parameter set code
    	text += getScriptTemplate().buildParameterScript();
    	
    	//Get main code
    	String tempText = ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.MAIN_CODE);
    	if (tempText == null) {
    		tempText = ScriptTemplate.getScriptCode(getTemplateType());
    	}
    	text += tempText;
    	
    	//Get email notification code
    	if(getSendEmailCheckBox().isSelected()) {
    	    tempText = ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.NOTIFICATION);
    	    if( tempText == null) {
    	        tempText = ScriptTemplate.buildNotificationCode();
            }
    	    text += tempText;
    	}
    
    	//Get billing code
    	if(getGenerateBillingCheckBox().isSelected()) {
    	    tempText = ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.BILLING);
    	    if( tempText == null) {
    	        tempText = ScriptTemplate.buildBillingCode();
            }
    	    text += tempText;
    	}
    	
    	//Get the footer code
    	tempText = ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.FOOTER); 
    	if( tempText == null) {
    	    tempText = ScriptTemplate.buildScriptFooterCode();
        }
    	text += tempText;
    	
        return text;
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.CaretListener#caretUpdate(javax.swing.event.CaretEvent)
     */
    public void caretUpdate(CaretEvent e) {
    	fireInputUpdate();
    }
    
    public void enableContainer(Container c, boolean enable) {
    	Component[] components = c.getComponents();
    	for(int i = 0; i < components.length; i++) {
    		components[i].setEnabled(enable);
    		if(components[i] instanceof Container) {
    			enableContainer((Container)components[i], enable);
    		}
    	}
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusGained(java.awt.event.FocusEvent)
     */
    public void focusGained(FocusEvent e){
        
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.FocusListener#focusLost(java.awt.event.FocusEvent)
     */
    public void focusLost(FocusEvent e) {
        if (e.getSource() == getScriptTextArea()) {
            setScriptText(getScriptTextArea().getText());
            if( ! ScriptTemplateTypes.isNoTemplate(getTemplateType())) {
                getScriptTemplate().loadParamsFromScript(ScriptTemplate.getScriptSection(getScriptText(), ScriptTemplate.PARAMETER_LIST));
                initSwingCompValues(true);
            }
        }
    }
    
    /**
     * Return the AlphaFrozenRegisterCheckBox property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getAlphaFrozenRegisterCheckBox() {
    	if (ivjAlphaFrozenRegisterCheckBox == null) {
    		try {
    			ivjAlphaFrozenRegisterCheckBox = new javax.swing.JCheckBox();
    			ivjAlphaFrozenRegisterCheckBox.setName("AlphaFrozenRegisterCheckBox");
    			ivjAlphaFrozenRegisterCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjAlphaFrozenRegisterCheckBox.setText("Alpha");
    			ivjAlphaFrozenRegisterCheckBox.setToolTipText(getScriptTemplate().getParamaterDescription(READ_FROZEN_PARAM));
    			ivjAlphaFrozenRegisterCheckBox.setSelected(true);
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjAlphaFrozenRegisterCheckBox;
    }
    
    /**
     * Return the BrowseButton property value.
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getBillingFileBrowseButton() {
    	if (ivjBillingFileBrowseButton == null) {
    		try {
    			ivjBillingFileBrowseButton = new javax.swing.JButton();
    			ivjBillingFileBrowseButton.setName("BillingFileBrowseButton");
    			ivjBillingFileBrowseButton.setToolTipText("Press to find the directory to write billing file to.");
    			ivjBillingFileBrowseButton.setText("...");
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjBillingFileBrowseButton;
    }
    
    /**
     * Return the BillingFileNameLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getBillingFileNameLabel() {
    	if (ivjBillingFileNameLabel == null) {
    		try {
    			ivjBillingFileNameLabel = new javax.swing.JLabel();
    			ivjBillingFileNameLabel.setName("BillingFileNameLabel");
    			ivjBillingFileNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjBillingFileNameLabel.setText("File Name:");
    			ivjBillingFileNameLabel.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_FILE_NAME_PARAM));			
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjBillingFileNameLabel;
    }
    
    /**
     * Return the BillingFileNameTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getBillingFileNameTextField() {
    	if (ivjBillingFileNameTextField == null) {
    		try {
    			ivjBillingFileNameTextField = new javax.swing.JTextField();
    			ivjBillingFileNameTextField.setName("BillingFileNameTextField");
    			ivjBillingFileNameTextField.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_FILE_NAME_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjBillingFileNameTextField;
    }
    
    /**
     * Return the BillingFilePathTextBox property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getBillingFilePathTextBox() {
    	if (ivjBillingFilePathTextBox == null) {
    		try {
    			ivjBillingFilePathTextBox = new javax.swing.JTextField();
    			ivjBillingFilePathTextBox.setName("BillingFilePathTextBox");
    			ivjBillingFilePathTextBox.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_FILE_PATH_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjBillingFilePathTextBox;
    }
    
    /**
     * Return the BillingFormatComboBox property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getBillingFormatComboBox() {
    	if (ivjBillingFormatComboBox == null) {
    		try {
    			ivjBillingFormatComboBox = new javax.swing.JComboBox();
    			ivjBillingFormatComboBox.setName("BillingFormatComboBox");
    			ivjBillingFormatComboBox.addItem("none");
    			String [] formats = FileFormatTypes.getValidFormatTypes();
    			for(int i = 0; i < formats.length; i++) {
    				ivjBillingFormatComboBox.addItem(formats[i]);
                }
    			ivjBillingFormatComboBox.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_FORMAT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjBillingFormatComboBox;
    }
    
    /**
     * Return the BillingFormatLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getBillingFormatLabel() {
    	if (ivjBillingFormatLabel == null) {
    		try {
    			ivjBillingFormatLabel = new javax.swing.JLabel();
    			ivjBillingFormatLabel.setName("BillingFormatLabel");
    			ivjBillingFormatLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjBillingFormatLabel.setText("FileFormat:");
    			ivjBillingFormatLabel.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_FORMAT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjBillingFormatLabel;
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
    private javax.swing.JLabel getBilllingFilePathLabel() {
    	if (ivjBilllingFilePathLabel == null) {
    		try {
    			ivjBilllingFilePathLabel = new javax.swing.JLabel();
    			ivjBilllingFilePathLabel.setName("BilllingFilePathLabel");
    			ivjBilllingFilePathLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjBilllingFilePathLabel.setText("File Path:");
                ivjBilllingFilePathLabel.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_FILE_PATH_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjBilllingFilePathLabel;
    }
    
    /**
     * Return the BrowseButton property value.
     * @return javax.swing.JButton
     */
    private javax.swing.JButton getBrowseButton() {
    	if (ivjBrowseButton == null) {
    		try {
    			ivjBrowseButton = new javax.swing.JButton();
    			ivjBrowseButton.setName("BrowseButton");
    			ivjBrowseButton.setToolTipText("Press to find directory.");
    			ivjBrowseButton.setText("...");
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjBrowseButton;
    }
    
    /**
     * Return the DemandDaysLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getDemandDaysLabel() {
    	if (ivjDemandDaysLabel == null) {
    		try {
    			ivjDemandDaysLabel = new javax.swing.JLabel();
    			ivjDemandDaysLabel.setName("DemandDaysLabel");
    			ivjDemandDaysLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjDemandDaysLabel.setText("Demand Days:");
                ivjDemandDaysLabel.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_DEMAND_DAYS_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjDemandDaysLabel;
    }
    
    /**
     * Return the JTextField1 property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getDemandDaysTextField() {
    	if (ivjDemandDaysTextField == null) {
    		try {
    			ivjDemandDaysTextField = new javax.swing.JTextField();
    			ivjDemandDaysTextField.setName("DemandDaysTextField");
    			ivjDemandDaysTextField.setDocument( new LongRangeDocument(0L, 999999999L) );
                ivjDemandDaysTextField.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_DEMAND_DAYS_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjDemandDaysTextField;
    }
    
    /**
     * Return the DemandResetSpinBox property value.
     * @return com.klg.jclass.util.swing.JCSpinNumberBox
     */
    private com.klg.jclass.util.swing.JCSpinNumberBox getDemandResetSpinBox() {
    	if (ivjDemandResetSpinBox == null) {
    		try {
    			ivjDemandResetSpinBox = new com.klg.jclass.util.swing.JCSpinNumberBox();
    			ivjDemandResetSpinBox.setName("DemandResetSpinBox");
    			ivjDemandResetSpinBox.setValueRange(new Integer(0), new Integer(5));
    			ivjDemandResetSpinBox.setValue(new Integer(2));
    			ivjDemandResetSpinBox.setEditable(false);
                ivjDemandResetSpinBox.setToolTipText(getScriptTemplate().getParamaterDescription(RESET_COUNT_PARAM));
    			
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjDemandResetSpinBox;
    }
    
    /**
     * Return the DescriptionLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getDescriptionLabel() {
    	if (ivjDescriptionLabel == null) {
    		try {
    			ivjDescriptionLabel = new javax.swing.JLabel();
    			ivjDescriptionLabel.setName("DescriptionLabel");
    			ivjDescriptionLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjDescriptionLabel.setText("Description:");
                ivjDescriptionLabel.setToolTipText(getScriptTemplate().getParamaterDescription(SCRIPT_DESC_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjDescriptionLabel;
    }
    
    /**
     * Return the DescriptionTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getDescriptionTextField() {
    	if (ivjDescriptionTextField == null) {
    		try {
    			ivjDescriptionTextField = new javax.swing.JTextField();
    			ivjDescriptionTextField.setName("DescriptionTextField");
                ivjDescriptionTextField.setToolTipText(getScriptTemplate().getParamaterDescription(SCRIPT_DESC_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjDescriptionTextField;
    }
    
    /**
     * Return the EnergyDaysLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getEnergyDaysLabel() {
    	if (ivjEnergyDaysLabel == null) {
    		try {
    			ivjEnergyDaysLabel = new javax.swing.JLabel();
    			ivjEnergyDaysLabel.setName("EnergyDaysLabel");
    			ivjEnergyDaysLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjEnergyDaysLabel.setText("Energy Days:");
                ivjEnergyDaysLabel.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_ENERGY_DAYS_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjEnergyDaysLabel;
    }
    
    /**
     * Return the JTextField2 property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getEnergyDaysTextField() {
    	if (ivjEnergyDaysTextField == null) {
    		try {
    			ivjEnergyDaysTextField = new javax.swing.JTextField();
    			ivjEnergyDaysTextField.setName("EnergyDaysTextField");
    			ivjEnergyDaysTextField.setDocument( new LongRangeDocument(0L, 999999999L) );
                ivjEnergyDaysTextField.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_ENERGY_DAYS_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjEnergyDaysTextField;
    }
    
    /**
     * Return the ExampleLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getExampleLabel() {
    	if (ivjExampleLabel == null) {
    		try {
    			ivjExampleLabel = new javax.swing.JLabel();
    			ivjExampleLabel.setName("ExampleLabel");
    			ivjExampleLabel.setFont(new java.awt.Font("dialog", 0, 12));
    			ivjExampleLabel.setText("( Ex. MeterRead.ctl )");
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjExampleLabel;
    }
    
    /**
     * Return the FilePathLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getFilePathLabel() {
    	if (ivjFilePathLabel == null) {
    		try {
    			ivjFilePathLabel = new javax.swing.JLabel();
    			ivjFilePathLabel.setName("FilePathLabel");
    			ivjFilePathLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjFilePathLabel.setText("File Path:");
                ivjFilePathLabel.setToolTipText(getScriptTemplate().getParamaterDescription(FILE_PATH_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjFilePathLabel;
    }
    
    /**
     * Return the FilePathTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getFilePathTextField() {
    	if (ivjFilePathTextField == null) {
    		try {
    			ivjFilePathTextField = new javax.swing.JTextField();
    			ivjFilePathTextField.setName("FilePathTextField");
                ivjFilePathTextField.setToolTipText(getScriptTemplate().getParamaterDescription(FILE_PATH_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjFilePathTextField;
    }
    
    /**
     * Return the JCheckBox3 property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getGenerateBillingCheckBox() {
    	if (ivjGenerateBillingCheckBox == null) {
    		try {
    			ivjGenerateBillingCheckBox = new javax.swing.JCheckBox();
    			ivjGenerateBillingCheckBox.setName("GenerateBillingCheckBox");
    			ivjGenerateBillingCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjGenerateBillingCheckBox.setText("Generate Billing File");
                ivjGenerateBillingCheckBox.setToolTipText(getScriptTemplate().getParamaterDescription(BILLING_FLAG_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjGenerateBillingCheckBox;
    }
    
    /**
     * Return the GroupList property value.
     * @return javax.swing.JList
     */
    private javax.swing.JTree getMeterReadGroupTree() {
        if (meterReadGroupTree == null) {
            DeviceGroupTreeFactory modelFactory = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
            try {
                CustomRenderJTree customTree = new CustomRenderJTree();
                customTree.addRenderer(new DeviceGroupRenderer());
                meterReadGroupTree = customTree;
                meterReadGroupTree.setName("GroupList");
                meterReadGroupTree.setBounds(0, 0, 160, 120);
                meterReadGroupTree.setShowsRootHandles(true);
                meterReadGroupTree.setRootVisible(false);
    
                TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
                selectionModel.clearSelection();
                selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                meterReadGroupTree.setSelectionModel(selectionModel);
                TreeModel model = modelFactory.getStaticOnlyModel();
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
    private javax.swing.JTree getBillingGroupTree() {
        if (billingGroupTree == null) {
            DeviceGroupTreeFactory modelFactory = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
            try {
                CustomRenderJTree customTree = new CustomRenderJTree();
                customTree.addRenderer(new DeviceGroupRenderer());
                billingGroupTree = customTree;
                billingGroupTree.setName("GroupList");
                billingGroupTree.setBounds(0, 0, 160, 120);
                billingGroupTree.setShowsRootHandles(true);
                billingGroupTree.setRootVisible(false);
    
                TreeSelectionModel selectionModel = new DefaultTreeSelectionModel();
                selectionModel.clearSelection();
                selectionModel.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
                billingGroupTree.setSelectionModel(selectionModel);
                // it is okay to include dynamic groups here because the string is simply 
                // passed to BillingFile and not interpreted by MACS
                TreeModel model = modelFactory.getModel(new NonHiddenDeviceGroupPredicate());
                billingGroupTree.setModel(model);
    
            } catch (Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return billingGroupTree;
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
                getMeterReadGroupListScrollPane().setViewportView(getMeterReadGroupTree());
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
                getOptionsGroupListScrollPane().setViewportView(getBillingGroupTree());
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
    
                // TOU Rate
    			java.awt.GridBagConstraints constraintsTOURateLabel = new java.awt.GridBagConstraints();
    			constraintsTOURateLabel.gridx = 0; constraintsTOURateLabel.gridy = 0;
    			constraintsTOURateLabel.anchor = java.awt.GridBagConstraints.WEST;
    			constraintsTOURateLabel.insets = new java.awt.Insets(4, 4, 4, 4);
    			getIEDPanel().add(getTOURateLabel(), constraintsTOURateLabel);
    			
                java.awt.GridBagConstraints constraintsTOURateComboBox = new java.awt.GridBagConstraints();
                constraintsTOURateComboBox.gridx = 1; constraintsTOURateComboBox.gridy = 0;
                constraintsTOURateComboBox.gridwidth = 2;
                constraintsTOURateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsTOURateComboBox.weightx = 1.0;
                constraintsTOURateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
                getIEDPanel().add(getTOURateComboBox(), constraintsTOURateComboBox);
                
                // Reset Demand Count
                //-----------------------------------------------------
                java.awt.GridBagConstraints constraintsSeparatorResetDemand = new java.awt.GridBagConstraints();
                constraintsSeparatorResetDemand.gridx = 0; constraintsSeparatorResetDemand.gridy = 1;
                constraintsSeparatorResetDemand.gridwidth = 3;
                constraintsSeparatorResetDemand.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsSeparatorResetDemand.insets = new java.awt.Insets(4, 4, 4, 4);
                getIEDPanel().add(getSeparatorResetDemand(), constraintsSeparatorResetDemand);
                
                java.awt.GridBagConstraints constraintsResetDemandCheckBox = new java.awt.GridBagConstraints();
                constraintsResetDemandCheckBox.gridx = 0; constraintsResetDemandCheckBox.gridy = 2;
                constraintsResetDemandCheckBox.gridwidth = 3;
                constraintsResetDemandCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsResetDemandCheckBox.insets = new java.awt.Insets(0, 4, 4, 4);
                getIEDPanel().add(getResetDemandEnabledCheckBox(), constraintsResetDemandCheckBox);
                
    			java.awt.GridBagConstraints constraintsResetCountLabel = new java.awt.GridBagConstraints();
    			constraintsResetCountLabel.gridx = 0; constraintsResetCountLabel.gridy = 3;
    			constraintsResetCountLabel.gridwidth = 2;
    			constraintsResetCountLabel.anchor = java.awt.GridBagConstraints.WEST;
    			constraintsResetCountLabel.insets = new java.awt.Insets(4, 4, 4, 4);
    			getIEDPanel().add(getResetCountLabel(), constraintsResetCountLabel);
    			
                java.awt.GridBagConstraints constraintsDemandResetSpinBox = new java.awt.GridBagConstraints();
                constraintsDemandResetSpinBox.gridx = 2; constraintsDemandResetSpinBox.gridy = 3;
                constraintsDemandResetSpinBox.fill = java.awt.GridBagConstraints.BOTH;
                constraintsDemandResetSpinBox.weightx = 1.0;
                constraintsDemandResetSpinBox.insets = new java.awt.Insets(4, 4, 4, 4);
                getIEDPanel().add(getDemandResetSpinBox(), constraintsDemandResetSpinBox);
                
                // Seperator for frozen
                //-----------------------------------------------------
//                java.awt.GridBagConstraints constraintsSeparatorFrozenRegister = new java.awt.GridBagConstraints();
//                constraintsSeparatorFrozenRegister.gridx = 0; constraintsSeparatorFrozenRegister.gridy = 4;
//                constraintsSeparatorFrozenRegister.gridwidth = 3;
//                constraintsSeparatorFrozenRegister.fill = java.awt.GridBagConstraints.HORIZONTAL;
//                constraintsSeparatorFrozenRegister.insets = new java.awt.Insets(4, 4, 4, 4);
//                getIEDPanel().add(getSeparatorFrozenRegister(), constraintsSeparatorFrozenRegister);
                
                // Read Frozen Demand Register label
                java.awt.GridBagConstraints constraintsReadFrozenDemandRegister = new java.awt.GridBagConstraints();
                constraintsReadFrozenDemandRegister.gridx = 0; constraintsReadFrozenDemandRegister.gridy = 5;
                constraintsReadFrozenDemandRegister.gridwidth = 3;
                constraintsReadFrozenDemandRegister.anchor = java.awt.GridBagConstraints.SOUTHWEST;
                constraintsReadFrozenDemandRegister.insets = new java.awt.Insets(0, 4, 0, 4);
                getIEDPanel().add(getReadFrozenDemandRegister(), constraintsReadFrozenDemandRegister);
                
                // 360/370 types
                //-----------------------------------------------------
                // S4
                java.awt.GridBagConstraints constraintsS4FrozenRegisterCheckBox = new java.awt.GridBagConstraints();
                constraintsS4FrozenRegisterCheckBox.gridx = 0; constraintsS4FrozenRegisterCheckBox.gridy = 6;
                constraintsS4FrozenRegisterCheckBox.gridwidth = 3;
                constraintsS4FrozenRegisterCheckBox.fill = java.awt.GridBagConstraints.BOTH;
                constraintsS4FrozenRegisterCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsS4FrozenRegisterCheckBox.weightx = 1.0;
                constraintsS4FrozenRegisterCheckBox.insets = new java.awt.Insets(4, 4, 0, 4);
                getIEDPanel().add(getS4FrozenRegisterCheckBox(), constraintsS4FrozenRegisterCheckBox);

                // Alpha
                java.awt.GridBagConstraints constraintsAlphaFrozenRegisterCheckBox = new java.awt.GridBagConstraints();
                constraintsAlphaFrozenRegisterCheckBox.gridx = 0; constraintsAlphaFrozenRegisterCheckBox.gridy = 7;
                constraintsAlphaFrozenRegisterCheckBox.gridwidth = 3;
                constraintsAlphaFrozenRegisterCheckBox.anchor = java.awt.GridBagConstraints.WEST;
                constraintsAlphaFrozenRegisterCheckBox.insets = new java.awt.Insets(0, 4, 4, 4);
                getIEDPanel().add(getAlphaFrozenRegisterCheckBox(), constraintsAlphaFrozenRegisterCheckBox);

                // 400 Series
                //-----------------------------------------------------
                // alpha/s4/kV/kV2/sentinel drop down
                java.awt.GridBagConstraints constraintsIED400TypeLabel = new java.awt.GridBagConstraints();
                constraintsIED400TypeLabel.gridx = 0; constraintsResetCountLabel.gridy = 4;
                constraintsIED400TypeLabel.gridwidth = 2;
                constraintsIED400TypeLabel.anchor = java.awt.GridBagConstraints.WEST;
                constraintsIED400TypeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
                getIEDPanel().add(getIED400TypeLabel(), constraintsIED400TypeLabel);
                
                java.awt.GridBagConstraints constraintsIED400TypeComboBox = new java.awt.GridBagConstraints();
                constraintsIED400TypeComboBox.gridx = 2; constraintsIED400TypeComboBox.gridy = 4;
                constraintsIED400TypeComboBox.gridwidth = 2;
                constraintsIED400TypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
                constraintsIED400TypeComboBox.weightx = 1.0;
                constraintsIED400TypeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
                getIEDPanel().add(getIED400TypeComboBox(), constraintsIED400TypeComboBox);
                
    			
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjIEDPanel;
    }
    
    /**
     * Return the JLabel7 property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getMaxRetryHoursLabel() {
    	if (ivjMaxRetryHoursLabel == null) {
    		try {
    			ivjMaxRetryHoursLabel = new javax.swing.JLabel();
    			ivjMaxRetryHoursLabel.setName("MaxRetryHoursLabel");
    			ivjMaxRetryHoursLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjMaxRetryHoursLabel.setText("Max Retry Hours:");
                ivjMaxRetryHoursLabel.setToolTipText(getScriptTemplate().getParamaterDescription(MAX_RETRY_HOURS_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjMaxRetryHoursLabel;
    }
    
    /**
     * Return the JTextField5 property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getMaxRetryHoursTextField() {
    	if (ivjMaxRetryHoursTextField == null) {
    		try {
    			ivjMaxRetryHoursTextField = new javax.swing.JTextField();
    			ivjMaxRetryHoursTextField.setName("MaxRetryHoursTextField");
    			ivjMaxRetryHoursTextField.setDocument( new LongRangeDocument(-1L, 10L) );
                ivjMaxRetryHoursTextField.setToolTipText(getScriptTemplate().getParamaterDescription(MAX_RETRY_HOURS_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjMaxRetryHoursTextField;
    }
    
    /**
     * Return the JLabel61 property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getMessageSubjectLabel() {
    	if (ivjMessageSubjectLabel == null) {
    		try {
    			ivjMessageSubjectLabel = new javax.swing.JLabel();
    			ivjMessageSubjectLabel.setName("MessageSubjectLabel");
    			ivjMessageSubjectLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjMessageSubjectLabel.setText("Message Subject:");
                ivjMessageSubjectLabel.setToolTipText(getScriptTemplate().getParamaterDescription(EMAIL_SUBJECT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjMessageSubjectLabel;
    }
    
    /**
     * Return the JTextField41 property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getMessageSubjectTextField() {
    	if (ivjMessageSubjectTextField == null) {
    		try {
    			ivjMessageSubjectTextField = new javax.swing.JTextField();
    			ivjMessageSubjectTextField.setName("MessageSubjectTextField");
                ivjMessageSubjectTextField.setToolTipText(getScriptTemplate().getParamaterDescription(EMAIL_SUBJECT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjMessageSubjectTextField;
    }
    
    /**
     * Return the MeterReadPanel property value.
     * @return javax.swing.JPanel
     */
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
    			
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjMeterReadSetupPanel;
    }
    
    /**
     * Return the MissedFileNameLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getMissedFileNameLabel() {
    	if (ivjMissedFileNameLabel == null) {
    		try {
    			ivjMissedFileNameLabel = new javax.swing.JLabel();
    			ivjMissedFileNameLabel.setName("MissedFileNameLabel");
    			ivjMissedFileNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjMissedFileNameLabel.setText("Missed File Name:");
                ivjMissedFileNameLabel.setToolTipText(getScriptTemplate().getParamaterDescription(MISSED_FILE_NAME_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjMissedFileNameLabel;
    }
    
    /**
     * Return the MissedFileNameTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getMissedFileNameTextField() {
    	if (ivjMissedFileNameTextField == null) {
    		try {
    			ivjMissedFileNameTextField = new javax.swing.JTextField();
    			ivjMissedFileNameTextField.setName("MissedFileNameTextField");
                ivjMissedFileNameTextField.setToolTipText(getScriptTemplate().getParamaterDescription(MISSED_FILE_NAME_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjMissedFileNameTextField;
    }
    
    /**
     * Return the JPanel property value.
     * @return javax.swing.JPanel
     */
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
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjNotificationPanel;
    }
    
    /**
     * Return the JComboBox1 property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getNotifyGroupComboBox() {
    	if (ivjNotifyGroupComboBox == null) {
    		try {
    			ivjNotifyGroupComboBox = new javax.swing.JComboBox();
    			ivjNotifyGroupComboBox.setName("NotifyGroupComboBox");
    			
    			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
    			List<LiteNotificationGroup> notGroups = cache.getAllContactNotificationGroups();
    			for (int i = 0; i < notGroups.size(); i++) {
    				LiteNotificationGroup lng = notGroups.get(i);
    				ivjNotifyGroupComboBox.addItem(lng.getNotificationGroupName());
    			}
                ivjNotifyGroupComboBox.setToolTipText(getScriptTemplate().getParamaterDescription(NOTIFY_GROUP_PARAM));
    			
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjNotifyGroupComboBox;
    }
    
    /**
     * Return the JLabel51 property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getNotifyGroupLabel() {
    	if (ivjNotifyGroupLabel == null) {
    		try {
    			ivjNotifyGroupLabel = new javax.swing.JLabel();
    			ivjNotifyGroupLabel.setName("NotifyGroupLabel");
    			ivjNotifyGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjNotifyGroupLabel.setText("Notify Group:");
                ivjNotifyGroupLabel.setToolTipText(getScriptTemplate().getParamaterDescription(NOTIFY_GROUP_PARAM));
    			
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjNotifyGroupLabel;
    }
    
    /**
     * Return the OtherPanel property value.
     * @return javax.swing.JPanel
     */
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
    private javax.swing.JLabel getPorterTimeoutLabel() {
    	if (ivjPorterTimeoutLabel == null) {
    		try {
    			ivjPorterTimeoutLabel = new javax.swing.JLabel();
    			ivjPorterTimeoutLabel.setName("PorterTimeoutLabel");
    			ivjPorterTimeoutLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjPorterTimeoutLabel.setText("Porter Timeout:");
                ivjPorterTimeoutLabel.setToolTipText(getScriptTemplate().getParamaterDescription(PORTER_TIMEOUT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjPorterTimeoutLabel;
    }
    
    /**
     * Return the PorterTimeoutTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getPorterTimeoutTextField() {
    	if (ivjPorterTimeoutTextField == null) {
    		try {
    			ivjPorterTimeoutTextField = new javax.swing.JTextField();
    			ivjPorterTimeoutTextField.setName("PorterTimeoutTextField");
    			ivjPorterTimeoutTextField.setDocument( new LongRangeDocument(0L, 999999999L) );
                ivjPorterTimeoutTextField.setToolTipText(getScriptTemplate().getParamaterDescription(PORTER_TIMEOUT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjPorterTimeoutTextField;
    }
    
    /**
     * Return the JLabel6 property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getQueueOffCountLabel() {
    	if (ivjQueueOffCountLabel == null) {
    		try {
    			ivjQueueOffCountLabel = new javax.swing.JLabel();
    			ivjQueueOffCountLabel.setName("QueueOffCountLabel");
    			ivjQueueOffCountLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjQueueOffCountLabel.setText("Queue Off Count:");
                ivjQueueOffCountLabel.setToolTipText(getScriptTemplate().getParamaterDescription(QUEUE_OFF_COUNT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjQueueOffCountLabel;
    }
    
    /**
     * Return the JTextField4 property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getQueueOffCountTextField() {
    	if (ivjQueueOffCountTextField == null) {
    		try {
    			ivjQueueOffCountTextField = new javax.swing.JTextField();
    			ivjQueueOffCountTextField.setName("QueueOffCountTextField");
    			ivjQueueOffCountTextField.setDocument( new LongRangeDocument(0L, 10L) );
                ivjQueueOffCountTextField.setToolTipText(getScriptTemplate().getParamaterDescription(QUEUE_OFF_COUNT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjQueueOffCountTextField;
    }
    
    /**
     * Return the JLabel1 property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getReadFrozenDemandRegister() {
    	if (ivjReadFrozenDemandRegister == null) {
    		try {
    			ivjReadFrozenDemandRegister = new javax.swing.JLabel();
    			ivjReadFrozenDemandRegister.setName("ReadFrozenDemandRegister");
    			ivjReadFrozenDemandRegister.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjReadFrozenDemandRegister.setText("Read Frozen Demand Register");
    			ivjReadFrozenDemandRegister.setForeground(java.awt.Color.black);
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjReadFrozenDemandRegister;
    }
    
    private javax.swing.JCheckBox getResetDemandEnabledCheckBox() {
        if (ivjResetDemandEnabledCheckBox == null) {
            try {
                ivjResetDemandEnabledCheckBox = new javax.swing.JCheckBox();
                ivjResetDemandEnabledCheckBox.setName("ResetDemandCheckBox");
                ivjResetDemandEnabledCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
                ivjResetDemandEnabledCheckBox.setText("Reset Demand");
                ivjResetDemandEnabledCheckBox.setToolTipText(getScriptTemplate().getParamaterDescription(READ_FROZEN_PARAM));
                ivjResetDemandEnabledCheckBox.setSelected(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjResetDemandEnabledCheckBox;
    }
    
    /**
     * Return the ResetCountLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getResetCountLabel() {
    	if (ivjResetCountLabel == null) {
    		try {
    			ivjResetCountLabel = new javax.swing.JLabel();
    			ivjResetCountLabel.setName("ResetCountLabel");
    			ivjResetCountLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjResetCountLabel.setText("Reset Retry Count:");
                ivjResetCountLabel.setToolTipText(getScriptTemplate().getParamaterDescription(RESET_COUNT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjResetCountLabel;
    }
    
    /**
     * Return the JLabel5 property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getRetryCountLabel() {
    	if (ivjRetryCountLabel == null) {
    		try {
    			ivjRetryCountLabel = new javax.swing.JLabel();
    			ivjRetryCountLabel.setName("RetryCountLabel");
    			ivjRetryCountLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjRetryCountLabel.setText("Retry Count:");
                ivjRetryCountLabel.setToolTipText(getScriptTemplate().getParamaterDescription(RETRY_COUNT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjRetryCountLabel;
    }
    
    /**
     * Return the JTextField3 property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getRetryCountTextField() {
    	if (ivjRetryCountTextField == null) {
    		try {
    			ivjRetryCountTextField = new javax.swing.JTextField();
    			ivjRetryCountTextField.setName("RetryCountTextField");
    			ivjRetryCountTextField.setDocument( new LongRangeDocument(0L, 10L) );
                ivjRetryCountTextField.setToolTipText(getScriptTemplate().getParamaterDescription(RETRY_COUNT_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjRetryCountTextField;
    }
    
    /**
     * Return the JPanel1 property value.
     * @return javax.swing.JPanel
     */
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
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjRetryPanel;
    }
    
    /**
     * Return the JCheckBox1 property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getS4FrozenRegisterCheckBox() {
    	if (ivjS4FrozenRegisterCheckBox == null) {
    		try {
    			ivjS4FrozenRegisterCheckBox = new javax.swing.JCheckBox();
    			ivjS4FrozenRegisterCheckBox.setName("S4FrozenRegisterCheckBox");
    			ivjS4FrozenRegisterCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjS4FrozenRegisterCheckBox.setText("Landis-Gyr S4");
                ivjS4FrozenRegisterCheckBox.setToolTipText(getScriptTemplate().getParamaterDescription(READ_FROZEN_PARAM));
    			ivjS4FrozenRegisterCheckBox.setSelected(false);
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjS4FrozenRegisterCheckBox;
    }
    
    /**
     * Return the ScriptNameLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getScriptNameLabel() {
    	if (ivjScriptNameLabel == null) {
    		try {
    			ivjScriptNameLabel = new javax.swing.JLabel();
    			ivjScriptNameLabel.setName("ScriptNameLabel");
    			ivjScriptNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjScriptNameLabel.setText("Script Name:");
                ivjScriptNameLabel.setToolTipText(getScriptTemplate().getParamaterDescription(SCRIPT_FILE_NAME_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjScriptNameLabel;
    }
    
    /**
     * Return the JPanel1 property value.
     * @return javax.swing.JPanel
     */
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
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjScriptNamePanel;
    }
    
    /**
     * Return the ScriptNameTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getScriptNameTextField() {
    	if (ivjScriptNameTextField == null) {
    		try {
    			ivjScriptNameTextField = new javax.swing.JTextField();
    			ivjScriptNameTextField.setName("ScriptNameTextField");
                ivjScriptNameTextField.setToolTipText(getScriptTemplate().getParamaterDescription(SCRIPT_FILE_NAME_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjScriptNameTextField;
    }
    
    /**
     * Return the ScriptScrollPane property value.
     * @return javax.swing.JScrollPane
     */
    private javax.swing.JScrollPane getScriptScrollPane() {
    	if (ivjScriptScrollPane == null) {
    		try {
    			ivjScriptScrollPane = new javax.swing.JScrollPane();
    			ivjScriptScrollPane.setName("ScriptScrollPane");
    			getScriptScrollPane().setViewportView(getScriptTextArea());
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjScriptScrollPane;
    }
    
    /**
     * @return Returns the scriptParams.
     */
    public ScriptTemplate getScriptTemplate() {
        if( scriptTemplate == null)
            scriptTemplate = new ScriptTemplate();
        return scriptTemplate;
    }
    
    /**
     * @return Returns the mainCode.
     */
    public String getScriptText(){
        return scriptText;
    }
    
    /**
     * Return the ScriptTextArea property value.
     * @return javax.swing.JTextArea
     */
    private javax.swing.JTextArea getScriptTextArea() {
    	if (ivjScriptTextArea == null) {
    		try {
    			ivjScriptTextArea = new javax.swing.JTextArea();
    			ivjScriptTextArea.setName("ScriptTextArea");
    			ivjScriptTextArea.setBounds(0, 0, 160, 120);
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjScriptTextArea;
    }
    
    /**
     * Return the JLabel3 property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getSecsLabel() {
    	if (ivjSecsLabel == null) {
    		try {
    			ivjSecsLabel = new javax.swing.JLabel();
    			ivjSecsLabel.setName("SecsLabel");
    			ivjSecsLabel.setFont(new java.awt.Font("dialog", 0, 12));
    			ivjSecsLabel.setText("secs");
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjSecsLabel;
    }
    
    /**
     * Return the JCheckBox2 property value.
     * @return javax.swing.JCheckBox
     */
    private javax.swing.JCheckBox getSendEmailCheckBox() {
    	if (ivjSendEmailCheckBox == null) {
    		try {
    			ivjSendEmailCheckBox = new javax.swing.JCheckBox();
    			ivjSendEmailCheckBox.setName("SendEmailCheckBox");
    			ivjSendEmailCheckBox.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjSendEmailCheckBox.setText("Send Email Notification");
                ivjSendEmailCheckBox.setToolTipText(getScriptTemplate().getParamaterDescription(NOTIFICATION_FLAG_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjSendEmailCheckBox;
    }
    
    /**
     * Return the SeparatorFrozenRegister property value.
     * @return javax.swing.JSeparator
     */
    private javax.swing.JSeparator getSeparatorFrozenRegister() {
    	if (ivjSeparatorFrozenRegister == null) {
    		try {
    			ivjSeparatorFrozenRegister = new javax.swing.JSeparator();
    			ivjSeparatorFrozenRegister.setName("SeparatorFrozenRegister");
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjSeparatorFrozenRegister;
    }
    
    private javax.swing.JSeparator getSeparatorResetDemand() {
        if (ivjSeparatorResetDemand == null) {
            try {
                ivjSeparatorResetDemand = new javax.swing.JSeparator();
                ivjSeparatorResetDemand.setName("SeparatorResetDemand");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjSeparatorResetDemand;
    }
    
    /**
     * Return the SuccessFileNameLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getSuccessFileNameLabel() {
    	if (ivjSuccessFileNameLabel == null) {
    		try {
    			ivjSuccessFileNameLabel = new javax.swing.JLabel();
    			ivjSuccessFileNameLabel.setName("SuccessFileNameLabel");
    			ivjSuccessFileNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjSuccessFileNameLabel.setText("Success File Name:");
                ivjSuccessFileNameLabel.setToolTipText(getScriptTemplate().getParamaterDescription(SUCCESS_FILE_NAME_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjSuccessFileNameLabel;
    }
    
    /**
     * Return the SuccessFileNameTextField property value.
     * @return javax.swing.JTextField
     */
    private javax.swing.JTextField getSuccessFileNameTextField() {
    	if (ivjSuccessFileNameTextField == null) {
    		try {
    			ivjSuccessFileNameTextField = new javax.swing.JTextField();
    			ivjSuccessFileNameTextField.setName("SuccessFileNameTextField");
                ivjSuccessFileNameTextField.setToolTipText(getScriptTemplate().getParamaterDescription(SUCCESS_FILE_NAME_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjSuccessFileNameTextField;
    }
    
    /**
     * Return the TabbedPane property value.
     * @return javax.swing.JTabbedPane
     */
    private javax.swing.JTabbedPane getTabbedPane() {
    	if (ivjTabbedPane == null) {
    		try {
    			ivjTabbedPane = new javax.swing.JTabbedPane();
    			ivjTabbedPane.setName("TabbedPane");
    			ivjTabbedPane.insertTab("Meter Read", null, getMeterReadSetupPanel(), null, 0);
    			ivjTabbedPane.insertTab("Options", null, getOptionsPanel(), null, 1);
    			ivjTabbedPane.insertTab("Text Editor", null, getScriptScrollPane(), null, 2);
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjTabbedPane;
    }
    
    /**
     * @return Returns the templateType.
     */
    public int getTemplateType() {
        return templateType;
    }
    
    /**
     * Return the TOURateComboBox property value.
     * @return javax.swing.JComboBox
     */
    private javax.swing.JComboBox getTOURateComboBox() {
    	if (ivjTOURateComboBox == null) {
    		try {
    			ivjTOURateComboBox = new javax.swing.JComboBox();
    			ivjTOURateComboBox.setName("TOURateComboBox");
                ivjTOURateComboBox.addItem("rate T");
    			ivjTOURateComboBox.addItem("rate A");
    			ivjTOURateComboBox.addItem("rate B");
    			ivjTOURateComboBox.addItem("rate C");
                ivjTOURateComboBox.addItem("rate D");
                ivjTOURateComboBox.setToolTipText(getScriptTemplate().getParamaterDescription(TOU_RATE_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjTOURateComboBox;
    }
    
    /**
     * Return the TOURateLabel property value.
     * @return javax.swing.JLabel
     */
    private javax.swing.JLabel getTOURateLabel() {
    	if (ivjTOURateLabel == null) {
    		try {
    			ivjTOURateLabel = new javax.swing.JLabel();
    			ivjTOURateLabel.setName("TOURateLabel");
    			ivjTOURateLabel.setFont(new java.awt.Font("dialog", 0, 14));
    			ivjTOURateLabel.setText("TOU Rate:");
                ivjTOURateLabel.setToolTipText(getScriptTemplate().getParamaterDescription(TOU_RATE_PARAM));
    		} catch (java.lang.Throwable ivjExc) {
    			handleException(ivjExc);
    		}
    	}
    	return ivjTOURateLabel;
    }
    
    
    private javax.swing.JLabel getIED400TypeLabel() {
        if (ivjIED400TypeLabel == null) {
            try {
                ivjIED400TypeLabel = new javax.swing.JLabel();
                ivjIED400TypeLabel.setName("IED400TypeLabel");
                ivjIED400TypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
                ivjIED400TypeLabel.setText("IED Type:");
                ivjIED400TypeLabel.setToolTipText(getScriptTemplate().getParamaterDescription(IED_TYPE_PARAM));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIED400TypeLabel;
    }
    
    private javax.swing.JComboBox getIED400TypeComboBox() {
        if (ivjIED400TypeComboBox == null) {
            try {
                ivjIED400TypeComboBox = new javax.swing.JComboBox();
                ivjIED400TypeComboBox.setName("IED400TypeComboBox");
                ivjIED400TypeComboBox.addItem("s4");
                ivjIED400TypeComboBox.addItem("alpha");
                ivjIED400TypeComboBox.addItem("kv");
                ivjIED400TypeComboBox.addItem("kv2");
                ivjIED400TypeComboBox.addItem("sentinel");
                ivjIED400TypeComboBox.setToolTipText(getScriptTemplate().getParamaterDescription(IED_TYPE_PARAM));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjIED400TypeComboBox;
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
        getScriptTemplate().setParameterValue(SCHEDULE_NAME_PARAM, sch.getScheduleName());
    	
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
    private void initialize() {
    	try {
    	    
    	    // hardcode filepaths to c:\yukon\server\export if we end up with a path
    	    // pointing to somewhere in "Documents and Settings" by overwrititng 
    	    // FILE_PATH_PARAM/BILLING_FILE_PATH_PARAM parameter values
    	    final String[] fallbackFilePathParts = {"C:", "yukon", "server", "export"};
            final String fallbackFilePath = StringUtils.join(fallbackFilePathParts, "/");
            if (StringUtils.contains(getScriptTemplate().getParameterValue(FILE_PATH_PARAM), "Documents and Settings")) {
                getScriptTemplate().setParameterValue(FILE_PATH_PARAM, fallbackFilePath);
            }
            if (StringUtils.contains(getScriptTemplate().getParameterValue(BILLING_FILE_PATH_PARAM), "Documents and Settings")) {
                getScriptTemplate().setParameterValue(BILLING_FILE_PATH_PARAM, fallbackFilePath);
            }
            
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
    		initSwingCompValues(false);
    	} catch (java.lang.Throwable ivjExc) {
    		handleException(ivjExc);
    	}
    }
    
    private void initializeConnections() {
        getScriptNameTextField().addKeyListener(this);
        getScriptTextArea().addCaretListener(this);
        getScriptTextArea().addFocusListener(this);
        getPorterTimeoutTextField().addKeyListener(this);
        getFilePathTextField().addKeyListener(this);
        getMissedFileNameTextField().addKeyListener(this);
        getSuccessFileNameTextField().addKeyListener(this);
        getBrowseButton().addActionListener(this);
        
        getTOURateComboBox().addActionListener(this);
        getResetDemandEnabledCheckBox().addItemListener(this);
        getFrozenRegisterGroup().add(getS4FrozenRegisterCheckBox());
        getFrozenRegisterGroup().add(getAlphaFrozenRegisterCheckBox());
        getS4FrozenRegisterCheckBox().addItemListener(this);
        getAlphaFrozenRegisterCheckBox().addItemListener(this);
        getIED400TypeComboBox().addActionListener(this);
        
        getRetryCountTextField().addKeyListener(this);
        getQueueOffCountTextField().addKeyListener(this);
        getMaxRetryHoursTextField().addKeyListener(this);
        
        getGenerateBillingCheckBox().addItemListener(this);
        getBillingFormatComboBox().addItemListener(this);
        getBillingFileNameTextField().addKeyListener(this);
        getDemandDaysTextField().addKeyListener(this);
        getEnergyDaysTextField().addKeyListener(this);
        getBillingFilePathTextBox().addKeyListener(this);
        getBillingFileBrowseButton().addActionListener(this);
        
        getSendEmailCheckBox().addItemListener(this);
        getNotifyGroupComboBox().addItemListener(this);
        getMessageSubjectTextField().addKeyListener(this);
        
        getTabbedPane().addChangeListener(this);
        getScriptTextArea().addFocusListener(this);
        getDescriptionTextField().addKeyListener(this);
        getMeterReadGroupTree().addTreeSelectionListener(this);
        getBillingGroupTree().addTreeSelectionListener(this);
    }
    
    /**
     * This method was created in VisualAge.
     */
    @SuppressWarnings({ "unchecked", "cast" })
    private void initSwingCompValues(boolean treeSelect) {
        getScriptTemplate().setParameterValue(IED_FLAG_PARAM, String.valueOf(ScriptTemplateTypes.isIEDTemplate(getTemplateType())));
    
        //The read_with..._param is for showing options to retry during a multiple read schedule, 
    	//so we NOT the result which is from a retry script, one that is a read once type script 
        getScriptTemplate().setParameterValue(READ_WITH_RETRY_FLAG_PARAM, String.valueOf(!ScriptTemplateTypes.isRetryTemplate(getTemplateType())));
        
        getScriptNameTextField().setText(getScriptTemplate().getParameterValue(SCRIPT_FILE_NAME_PARAM));
        getDescriptionTextField().setText(getScriptTemplate().getParameterValue(SCRIPT_DESC_PARAM));
        getFilePathTextField().setText(getScriptTemplate().getParameterValue(FILE_PATH_PARAM));
        getMissedFileNameTextField().setText(getScriptTemplate().getParameterValue(MISSED_FILE_NAME_PARAM));
        getSuccessFileNameTextField().setText(getScriptTemplate().getParameterValue(SUCCESS_FILE_NAME_PARAM));
        getPorterTimeoutTextField().setText(getScriptTemplate().getParameterValue(PORTER_TIMEOUT_PARAM));
        
        getRetryCountTextField().setText(getScriptTemplate().getParameterValue(RETRY_COUNT_PARAM));
        getMaxRetryHoursTextField().setText(getScriptTemplate().getParameterValue(MAX_RETRY_HOURS_PARAM));
        getQueueOffCountTextField().setText(getScriptTemplate().getParameterValue(QUEUE_OFF_COUNT_PARAM));
    
        DeviceGroupTreeFactory modelFactory = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
        DeviceGroupService deviceGroupService = YukonSpringHook.getBean("deviceGroupService", DeviceGroupService.class);
        String groupName = getScriptTemplate().getParameterValue(GROUP_NAME_PARAM);
        if(treeSelect) {
            if(StringUtils.isNotEmpty(groupName)) {
                DeviceGroup group = deviceGroupService.resolveGroupName(groupName);
                TreePath pathForGroup = modelFactory.getPathForGroup((TreeNode) getMeterReadGroupTree().getModel().getRoot(), group);
                getMeterReadGroupTree().getSelectionModel().addSelectionPath(pathForGroup);
                getMeterReadGroupTree().makeVisible(pathForGroup);
            }
        }
        
        //Billing setup
        enableContainer(getBillingPanel(), Boolean.valueOf(getScriptTemplate().getParameterValue(BILLING_FLAG_PARAM)).booleanValue());
        getGenerateBillingCheckBox().setSelected(Boolean.valueOf(getScriptTemplate().getParameterValue(BILLING_FLAG_PARAM)).booleanValue());
        getBillingFileNameTextField().setText(getScriptTemplate().getParameterValue(BILLING_FILE_NAME_PARAM));
        getBillingFilePathTextBox().setText(getScriptTemplate().getParameterValue(BILLING_FILE_PATH_PARAM));
        getBillingFormatComboBox().setSelectedItem(getScriptTemplate().getParameterValue(BILLING_FORMAT_PARAM));
        getDemandDaysTextField().setText(getScriptTemplate().getParameterValue(BILLING_DEMAND_DAYS_PARAM));
        getEnergyDaysTextField().setText(getScriptTemplate().getParameterValue(BILLING_ENERGY_DAYS_PARAM));
        String billGroupName = getScriptTemplate().getParameterValue(BILLING_GROUP_NAME_PARAM);
        if(treeSelect) {
            if(StringUtils.isNotEmpty(billGroupName)) {
                DeviceGroup group = deviceGroupService.resolveGroupName(billGroupName);
                TreePath pathForGroup = modelFactory.getPathForGroup((TreeNode) getBillingGroupTree().getModel().getRoot(), group);
                getBillingGroupTree().getSelectionModel().addSelectionPath(pathForGroup);
                getBillingGroupTree().makeVisible(pathForGroup);
            }
        }
        
        //Notification setup
        enableContainer(getNotificationPanel(), Boolean.valueOf(getScriptTemplate().getParameterValue(NOTIFICATION_FLAG_PARAM)).booleanValue());
        getSendEmailCheckBox().setSelected(Boolean.valueOf(getScriptTemplate().getParameterValue(NOTIFICATION_FLAG_PARAM)).booleanValue());
        getMessageSubjectTextField().setText(getScriptTemplate().getParameterValue(EMAIL_SUBJECT_PARAM));
        getNotifyGroupComboBox().setSelectedItem(getScriptTemplate().getParameterValue(NOTIFY_GROUP_PARAM));
        
        //IED panel setup
        String frozen = (getScriptTemplate().getParameterValue(READ_FROZEN_PARAM));
    	if(frozen.length() > 0)
    	{
    		if( frozen.indexOf("72") > 0)	//we have an alpha command (this way we don't have to have the register be exact (0 vs 00)
    			getAlphaFrozenRegisterCheckBox().setSelected(true);
    		else	//default rest to this one?
    			getS4FrozenRegisterCheckBox().setSelected(true);
    	}
        Integer resetCountParamValue = Integer.valueOf(getScriptTemplate().getParameterValue(RESET_COUNT_PARAM));
        if(resetCountParamValue > 0) {
            getResetDemandEnabledCheckBox().setSelected(true);
            setResetDemandFieldsEnabled(true);
        }
        else {
            getResetDemandEnabledCheckBox().setSelected(false);
            setResetDemandFieldsEnabled(false);
        }
        getDemandResetSpinBox().setValue(resetCountParamValue);
        getTOURateComboBox().setSelectedItem(getScriptTemplate().getParameterValue(TOU_RATE_PARAM));
        getIED400TypeComboBox().setSelectedItem(getScriptTemplate().getParameterValue(IED_TYPE_PARAM));
        
        CTILogger.info("Set swing component values");
    }
    
    /**
     * This method was created in VisualAge.
     * @return boolean
     */
    public boolean isInputValid(){
        
    	String scriptName = getScriptNameTextField().getText();
        if(scriptName == null || scriptName.length() <= 0) {
    		setErrorString("The Script Name text field must be filled in or the Use Schedule Name check box must be selected.");
    		return false;
    	}else if(!isValidFileName(scriptName)) {
            setErrorString("The script name cannot contain invalid file name characters.");
            return false;
        }else if (getIMACSConnection().isScriptFileNameExists(scriptName, scheduleId)) {
            setErrorString("The Script Name already exists.");
            return false;
        }else if(templateType != ScriptTemplateTypes.NO_TEMPLATE_SCRIPT && getMeterReadGroupTree().getSelectionPath() == null ) {
            setErrorString("A meter read group must be selected.");
            return false;
        }else if(getGenerateBillingCheckBox().isSelected() && getBillingGroupTree().getSelectionPath() == null) {
            setErrorString("A billing group must be selected or uncheck the 'Generate Billing File' checkbox.");
            return false;
        }
    	return true;
    }

    private boolean isValidFileName(String fileName) {
        return org.apache.commons.lang.StringUtils.containsNone(fileName, TextFieldDocument.INVALID_CHARS_WINDOWS);
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    @SuppressWarnings("unchecked")
    public void itemStateChanged(ItemEvent e) {
    	if( e.getSource() == getGenerateBillingCheckBox()) {
    	    boolean selected = e.getStateChange() == ItemEvent.SELECTED;
    		enableContainer(getBillingPanel(), selected);
            getScriptTemplate().setParameterValue(BILLING_FLAG_PARAM, String.valueOf(selected));
    	} else if ( e.getSource() == getSendEmailCheckBox()) {
    	    boolean selected = e.getStateChange() == ItemEvent.SELECTED;
    		enableContainer(getNotificationPanel(), selected);
            getScriptTemplate().setParameterValue(NOTIFICATION_FLAG_PARAM, String.valueOf(selected));
    	} else if (e.getSource() == getAlphaFrozenRegisterCheckBox()) {
    	    if( e.getStateChange() == ItemEvent.SELECTED) {
                getScriptTemplate().setParameterValue(READ_FROZEN_PARAM, ScriptTemplate.READ_FROZEN_ALPHA_COMMAND_STRING);
            }else {
                getScriptTemplate().setParameterValue(READ_FROZEN_PARAM, "");
            }
    	} else if (e.getSource() == getS4FrozenRegisterCheckBox()) {
    		if( e.getStateChange() == ItemEvent.SELECTED) {
                getScriptTemplate().setParameterValue(READ_FROZEN_PARAM, ScriptTemplate.READ_FROZEN_S4_COMMAND_STRING);
            } else {
                getScriptTemplate().setParameterValue(READ_FROZEN_PARAM, "");
            }
    	} else if (e.getSource() == getResetDemandEnabledCheckBox()) {
            if( e.getStateChange() == ItemEvent.SELECTED) {
                setResetDemandFieldsEnabled(true);
                getDemandResetSpinBox().setValue(2);
            } else {
                getDemandResetSpinBox().setValue(0);
                setResetDemandFieldsEnabled(false);
            }
        }
        
    	fireInputUpdate();	
    }
    
    /**
     * Comment
     */
    public void jButtonCheckScript_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    	CTILogger.info("Check script is not implemented");
    	return;
    }
    
    public void keyPressed(java.awt.event.KeyEvent e) {
        
    }
    
    /**
     * Method to handle events for the KeyListener interface.
     * @param e java.awt.event.KeyEvent
     */
    public void keyReleased(java.awt.event.KeyEvent e) {
    	fireInputUpdate();
    }
    
    /**
     * Method to handle events for the KeyListener interface.
     * @param e java.awt.event.KeyEvent
     */
    public void keyTyped(java.awt.event.KeyEvent e) {
        
    }
    
    /**
     * 
     */
    private void loadParamMapFromSwingComp() {
        //Some of the flag parameters are set throughout the code, i.e. on item change and panel load.
        
        //Change the filename to include an extension if it doesn't exist.
    	String fileName = getScriptNameTextField().getText();
    	if (!fileName.endsWith(".ctl")) {
    	    fileName += ".ctl";
        }
        getScriptTemplate().setParameterValue(SCRIPT_FILE_NAME_PARAM, fileName);
        getScriptTemplate().setParameterValue(SCRIPT_DESC_PARAM, getDescriptionTextField().getText());
        DeviceGroupTreeFactory modelFactory = YukonSpringHook.getBean("deviceGroupTreeFactory", DeviceGroupTreeFactory.class);
        if(getMeterReadGroupTree().getSelectionPath() != null) {
            DeviceGroup group = modelFactory.getGroupForPath(getMeterReadGroupTree().getSelectionPath());
            getScriptTemplate().setParameterValue(GROUP_NAME_PARAM, group.getFullName());
        } else {
            getScriptTemplate().setParameterValue(GROUP_NAME_PARAM, "");
        }
        getScriptTemplate().setParameterValue(PORTER_TIMEOUT_PARAM, getPorterTimeoutTextField().getText());
        getScriptTemplate().setParameterValue(FILE_PATH_PARAM, getFilePathTextField().getText());
        getScriptTemplate().setParameterValue(MISSED_FILE_NAME_PARAM, getMissedFileNameTextField().getText());
        getScriptTemplate().setParameterValue(SUCCESS_FILE_NAME_PARAM, getSuccessFileNameTextField().getText());
    
        //Billing in script
        getScriptTemplate().setParameterValue(BILLING_FLAG_PARAM, String.valueOf(getGenerateBillingCheckBox().isSelected()).toString());
        getScriptTemplate().setParameterValue(BILLING_FILE_NAME_PARAM, getBillingFileNameTextField().getText());
        getScriptTemplate().setParameterValue(BILLING_FILE_PATH_PARAM, getBillingFilePathTextBox().getText());
        getScriptTemplate().setParameterValue(BILLING_FORMAT_PARAM, getBillingFormatComboBox().getSelectedItem().toString());
        getScriptTemplate().setParameterValue(BILLING_ENERGY_DAYS_PARAM, getEnergyDaysTextField().getText());
        getScriptTemplate().setParameterValue(BILLING_DEMAND_DAYS_PARAM, getDemandDaysTextField().getText());
    
        if(getBillingGroupTree().getSelectionPath() != null) {
            DeviceGroup billingGroup = modelFactory.getGroupForPath(getBillingGroupTree().getSelectionPath());
            getScriptTemplate().setParameterValue(BILLING_GROUP_NAME_PARAM, billingGroup.getFullName());
        }
        
        //Notification in script
        getScriptTemplate().setParameterValue(NOTIFICATION_FLAG_PARAM, String.valueOf(getSendEmailCheckBox().isSelected()).toString());
        if (getNotifyGroupComboBox().getSelectedItem() != null) {
            getScriptTemplate().setParameterValue(NOTIFY_GROUP_PARAM, getNotifyGroupComboBox().getSelectedItem().toString());
        }
        getScriptTemplate().setParameterValue(EMAIL_SUBJECT_PARAM, getMessageSubjectTextField().getText());
        //Multiple reads script (one with retries)
        getScriptTemplate().setParameterValue(READ_WITH_RETRY_FLAG_PARAM, String.valueOf(!ScriptTemplateTypes.isRetryTemplate(getTemplateType())).toString());
        getScriptTemplate().setParameterValue(RETRY_COUNT_PARAM, getRetryCountTextField().getText());
        getScriptTemplate().setParameterValue(QUEUE_OFF_COUNT_PARAM, getQueueOffCountTextField().getText());
        getScriptTemplate().setParameterValue(MAX_RETRY_HOURS_PARAM, getMaxRetryHoursTextField().getText());
        //IED read script
        getScriptTemplate().setParameterValue(IED_FLAG_PARAM, String.valueOf(ScriptTemplateTypes.isIEDTemplate(getTemplateType())).toString());
        getScriptTemplate().setParameterValue(TOU_RATE_PARAM, getTOURateComboBox().getSelectedItem().toString());
        
        if (getResetDemandEnabledCheckBox().isSelected()) {
            getScriptTemplate().setParameterValue(RESET_COUNT_PARAM, getDemandResetSpinBox().getValue().toString());
        } else {
            getScriptTemplate().setParameterValue(RESET_COUNT_PARAM, "0");
        }
        
        if (ScriptTemplateTypes.isIED300Template(templateType)) {
            if (getS4FrozenRegisterCheckBox().isSelected()) {
                getScriptTemplate().setParameterValue(IED_TYPE_PARAM, "s4");
            }
            else if (getAlphaFrozenRegisterCheckBox().isSelected()) {
                getScriptTemplate().setParameterValue(IED_TYPE_PARAM, "alpha");
            }
        }
        else if (ScriptTemplateTypes.isIED400Template(templateType)) {
            getScriptTemplate().setParameterValue(IED_TYPE_PARAM, getIED400TypeComboBox().getSelectedItem().toString());
        }

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
    public void setScriptNameText(String name) {
        getScriptNameTextField().setText(name);
    }
    
    /**
     * @param scriptParams The scriptParams to set.
     */
    public void setScriptTemplate(ScriptTemplate scriptTemplate) {
        this.scriptTemplate = scriptTemplate;
    }
    
    /**
     * @param mainCode The mainCode to set.
     */
    public void setScriptText(String text) {
        this.scriptText = text;
    }
    
    /**
     * Insert the method's description here.
     * Creation date: (3/12/2001 5:13:39 PM)
     * @param file com.cannontech.message.macs.message.ScriptFile
     */
    @SuppressWarnings("static-access")
    public void setScriptValues(final ScriptFile file) {
    	try {
    		int i = 0;
    		for( i = 0; i < 25; i++ ) { // 5 second timeout 
    			if( this.isDisplayable() ) {
    			    CTILogger.info("		** TRUE - ScriptEditor isVisible()");
    				break;
    			}
    			else {
    			    CTILogger.info("		** Sleeping until ScriptEditor isVisible()");
                    Thread.sleep(200);
    			}
    		}
    
    		if( i == 25 ) {
    			CTILogger.info("		** TimeOut occured while waiting for our ScriptEditor screen to become Visible.");
    			return;
    		}
    	}
    	catch( InterruptedException e ) {
    		handleException(e);
    	}
    
    	SwingUtilities.invokeLater( new Runnable() {
    		@SuppressWarnings("unchecked")
            public void run() {
                getScriptTemplate().setParameterValue(SCRIPT_FILE_NAME_PARAM, file.getFileName() );
    			setScriptText( file.getFileContents() );
    			
    			//Init the textArea to display the script text.
    			getScriptTextArea().setText(getScriptText());
    			getScriptTextArea().setCaretPosition(0);
    			
    			//Load parameters from the script.
    			getScriptTemplate().loadParamsFromScript(ScriptTemplate.getScriptSection(file.getFileContents(), ScriptTemplate.PARAMETER_LIST));
    			//load up the swing components with the parameter values from the script file.
    			initSwingCompValues(true);
                // turn on listeners after we are actually done with setting up components
    			initializeConnections();
    			CTILogger.info("		** Done setting script contents");
    		}
    	});
    }
    
    /**
     * @param templateType The templateType to set.
     */
    public void setTemplateType(int templateType) {
    	this.templateType = templateType;
    	
        //Set the tabs based on the template type.
        getTabbedPane().removeAll();
        int tabCount = 0;
        if( (ScriptTemplateTypes.isMeteringTemplate(templateType))) {
        	getTabbedPane().insertTab("Meter Read", null, getMeterReadSetupPanel(), null, tabCount++);
        }
        if( (ScriptTemplateTypes.hasBilling(templateType) && ScriptTemplateTypes.hasNotification(templateType))) {
            getTabbedPane().insertTab("Options", null, getOptionsPanel(), null, tabCount++);
        }
        if( (ScriptTemplateTypes.isNoTemplate(templateType))){
           	getDescriptionLabel().setEnabled(false);
           	getDescriptionTextField().setEnabled(false);
        }
        
        //Show text editor for all types of scripts, TODO - make this an admin role/property?
        getTabbedPane().insertTab("Text Editor", null, getScriptScrollPane(), null, tabCount++);
    
        if (ScriptTemplateTypes.isIED300Template(templateType)) {
            setIED300AreaVisible(true);
            setIED400AreaVisible(false);
        }
        else if (ScriptTemplateTypes.isIED400Template(templateType)) {
            setIED300AreaVisible(false);
            setIED400AreaVisible(true);
        }
            
    	getIEDPanel().setVisible(ScriptTemplateTypes.isIEDTemplate(templateType));
        //The read_with..._param is for showing options to retry during a multiple read schedule, 
    	//so we NOT the result which is from a retry script, one that is a read once type script 
        getRetryPanel().setVisible(!ScriptTemplateTypes.isRetryTemplate(templateType));
    
        CTILogger.info("Set TemplateType, component visiblity updated.");
    }
    
    private void setIED300AreaVisible(boolean visible){
        for (int compIdx = 1; compIdx <= getIEDPanel().getComponentCount(); compIdx += 1) {
            Component IEDComponent = getIEDPanel().getComponent(compIdx - 1);
            if (IEDComponent.equals(getS4FrozenRegisterCheckBox()) || IEDComponent.equals(getAlphaFrozenRegisterCheckBox()) || IEDComponent.equals(getReadFrozenDemandRegister())) {
                IEDComponent.setVisible(visible);
            }
        }
        getReadFrozenDemandRegister().setVisible(visible);
        getSeparatorFrozenRegister().setVisible(visible);
    }
    
    private void setIED400AreaVisible(boolean visible){
        for (int compIdx = 1; compIdx <= getIEDPanel().getComponentCount(); compIdx += 1) {
            Component IEDComponent = getIEDPanel().getComponent(compIdx - 1);
            if (IEDComponent.equals(getIED400TypeLabel()) || IEDComponent.equals(getIED400TypeComboBox())) {
                IEDComponent.setVisible(visible);
            }
        }
        getReadFrozenDemandRegister().setVisible(!visible);
        getSeparatorFrozenRegister().setVisible(!visible);
    }
    
    private void setResetDemandFieldsEnabled(boolean value)
    {
        getResetCountLabel().setEnabled(value);
        getDemandResetSpinBox().setEnabled(value);
        getIED400TypeLabel().setEnabled(value);
        getIED400TypeComboBox().setEnabled(value);
        getReadFrozenDemandRegister().setEnabled(value);
        getS4FrozenRegisterCheckBox().setEnabled(value);
        getAlphaFrozenRegisterCheckBox().setEnabled(value);
        
    }
    
    /**
     * This method was created in VisualAge.
     * @param o java.lang.Object
     */
    public void setValue(Object o) {
    	Schedule sched = (Schedule)o;
    	this.scheduleId = sched.getId();
    	setTemplateType(sched.getTemplateType());
    }
    
    /* (non-Javadoc)
     * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
     */
    public void stateChanged(ChangeEvent e) {
        if( e.getSource() == getTabbedPane() && getTabbedPane().getSelectedComponent() == getScriptScrollPane()) {
            setScriptText(buildScript());
            getScriptTextArea().setText(getScriptText());
            getScriptTextArea().setCaretPosition(0);
        }
    }
    
    /* (non-Javadoc)
     * @see com.klg.jclass.util.value.JCValueListener#valueChanged(com.klg.jclass.util.value.JCValueEvent)
     */
    public void valueChanged(JCValueEvent arg0) {
    	fireInputUpdate();
    }
    
    /* (non-Javadoc)
     * @see com.klg.jclass.util.value.JCValueListener#valueChanging(com.klg.jclass.util.value.JCValueEvent)
     */
    public void valueChanging(JCValueEvent arg0) {
    	//do nothing	
    }
    
    /**
     * @return
     */
    public javax.swing.ButtonGroup getFrozenRegisterGroup(){
    	if( frozenRegisterGroup == null) {
    		frozenRegisterGroup = new ButtonGroup();
        }
    	return frozenRegisterGroup;
    }
    
    /**
     * @param group
     */
    public void setFrozenRegisterGroup(javax.swing.ButtonGroup group) {
    	frozenRegisterGroup = group;
    }
    
    public void valueChanged(TreeSelectionEvent e) {
        Object source = e.getSource();
        if(source == getMeterReadGroupTree() || source == getBillingGroupTree()) {
            fireInputUpdate();
        }
    }
}

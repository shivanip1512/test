package com.cannontech.dbeditor.editor.notification.recipient;

/**
 * Insert the type's description here.
 * Creation date: (11/28/00 4:03:33 PM)
 * @author: 
 */
public class NotificationRecipientGeneralEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JCheckBox ivjJCheckBoxDisable = null;
	private javax.swing.JLabel ivjJLabelAddress = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JPanel ivjJPanelEmail = null;
	private javax.swing.JTextField ivjJTextFieldEmailAddress = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JLabel ivjJLabelNotifyType = null;
	private javax.swing.JComboBox ivjJComboBoxNotifyType = null;
/**
 * NotificationRecipientGeneralEditorPanel constructor comment.
 */
public NotificationRecipientGeneralEditorPanel() {
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
	if (e.getSource() == getJCheckBoxDisable()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxNotifyType()) 
		connEtoC4(e);
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
	if (e.getSource() == getJTextFieldEmailAddress()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> NotificationRecipientGeneralEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldEmailAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> NotificationRecipientGeneralEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JCheckBoxDisable.action.actionPerformed(java.awt.event.ActionEvent) --> NotificationRecipientGeneralEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * connEtoC4:  (JComboBoxSendType.action.actionPerformed(java.awt.event.ActionEvent) --> NotificationRecipientGeneralEditorPanel.fireInputUpdate()V)
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
			ivjJCheckBoxDisable.setText("Disable Recipient");
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
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxNotifyType() {
	if (ivjJComboBoxNotifyType == null) {
		try {
			ivjJComboBoxNotifyType = new javax.swing.JComboBox();
			ivjJComboBoxNotifyType.setName("JComboBoxNotifyType");
			// user code begin {1}

			ivjJComboBoxNotifyType.addItem("Email");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxNotifyType;
}
/**
 * Return the JLabelAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAddress() {
	if (ivjJLabelAddress == null) {
		try {
			ivjJLabelAddress = new javax.swing.JLabel();
			ivjJLabelAddress.setName("JLabelAddress");
			ivjJLabelAddress.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAddress.setText("Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAddress;
}
/**
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JLabelSendType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotifyType() {
	if (ivjJLabelNotifyType == null) {
		try {
			ivjJLabelNotifyType = new javax.swing.JLabel();
			ivjJLabelNotifyType.setName("JLabelNotifyType");
			ivjJLabelNotifyType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNotifyType.setText("Notification Type:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotifyType;
}
/**
 * Return the JPanelEmail property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelEmail() {
	if (ivjJPanelEmail == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Email");
			ivjJPanelEmail = new javax.swing.JPanel();
			ivjJPanelEmail.setName("JPanelEmail");
			ivjJPanelEmail.setBorder(ivjLocalBorder);
			ivjJPanelEmail.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelAddress = new java.awt.GridBagConstraints();
			constraintsJLabelAddress.gridx = 1; constraintsJLabelAddress.gridy = 1;
			constraintsJLabelAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAddress.ipadx = 5;
			constraintsJLabelAddress.ipady = -5;
			constraintsJLabelAddress.insets = new java.awt.Insets(31, 17, 46, 1);
			getJPanelEmail().add(getJLabelAddress(), constraintsJLabelAddress);

			java.awt.GridBagConstraints constraintsJTextFieldEmailAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldEmailAddress.gridx = 2; constraintsJTextFieldEmailAddress.gridy = 1;
			constraintsJTextFieldEmailAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldEmailAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldEmailAddress.weightx = 1.0;
			constraintsJTextFieldEmailAddress.ipadx = 178;
			constraintsJTextFieldEmailAddress.insets = new java.awt.Insets(28, 1, 43, 52);
			getJPanelEmail().add(getJTextFieldEmailAddress(), constraintsJTextFieldEmailAddress);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelEmail;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldEmailAddress() {
	if (ivjJTextFieldEmailAddress == null) {
		try {
			ivjJTextFieldEmailAddress = new javax.swing.JTextField();
			ivjJTextFieldEmailAddress.setName("JTextFieldEmailAddress");
			// user code begin {1}
			ivjJTextFieldEmailAddress.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_100));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldEmailAddress;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			// user code begin {1}
			ivjJTextFieldName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_30));
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
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val) 
{
	com.cannontech.database.data.notification.NotificationRecipient gr = (com.cannontech.database.data.notification.NotificationRecipient)val;

	String locationName = getJTextFieldName().getText();
	if( locationName != null )
		gr.getNotificationRecipient().setRecipientName(locationName);

	String emailAddress = getJTextFieldEmailAddress().getText();
	if( emailAddress != null )
		gr.getNotificationRecipient().setEmailAddress(emailAddress);

	int sendType = getJComboBoxNotifyType().getSelectedIndex();
	gr.getNotificationRecipient().setEmailSendType( new Integer(sendType + 1) );

	if( getJCheckBoxDisable().isSelected() )
		gr.getNotificationRecipient().setDisableFlag("Y");
	else
		gr.getNotificationRecipient().setDisableFlag("N");
		
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldName().addCaretListener(this);
	getJTextFieldEmailAddress().addCaretListener(this);
	getJCheckBoxDisable().addActionListener(this);
	getJComboBoxNotifyType().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("NotificationRecipientGeneralEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(334, 344);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 6;
		constraintsJLabelName.ipady = -5;
		constraintsJLabelName.insets = new java.awt.Insets(25, 11, 7, 0);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.gridwidth = 2;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 245;
		constraintsJTextFieldName.insets = new java.awt.Insets(24, 1, 2, 27);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJCheckBoxDisable = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisable.gridx = 1; constraintsJCheckBoxDisable.gridy = 3;
		constraintsJCheckBoxDisable.gridwidth = 3;
		constraintsJCheckBoxDisable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJCheckBoxDisable.ipadx = 160;
		constraintsJCheckBoxDisable.ipady = -5;
		constraintsJCheckBoxDisable.insets = new java.awt.Insets(3, 12, 9, 27);
		add(getJCheckBoxDisable(), constraintsJCheckBoxDisable);

		java.awt.GridBagConstraints constraintsJPanelEmail = new java.awt.GridBagConstraints();
		constraintsJPanelEmail.gridx = 1; constraintsJPanelEmail.gridy = 4;
		constraintsJPanelEmail.gridwidth = 3;
		constraintsJPanelEmail.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelEmail.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelEmail.weightx = 1.0;
		constraintsJPanelEmail.weighty = 1.0;
		constraintsJPanelEmail.ipadx = -10;
		constraintsJPanelEmail.ipady = -47;
		constraintsJPanelEmail.insets = new java.awt.Insets(10, 12, 156, 8);
		add(getJPanelEmail(), constraintsJPanelEmail);

		java.awt.GridBagConstraints constraintsJLabelNotifyType = new java.awt.GridBagConstraints();
		constraintsJLabelNotifyType.gridx = 1; constraintsJLabelNotifyType.gridy = 2;
		constraintsJLabelNotifyType.gridwidth = 2;
		constraintsJLabelNotifyType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelNotifyType.ipadx = 4;
		constraintsJLabelNotifyType.insets = new java.awt.Insets(5, 11, 4, 0);
		add(getJLabelNotifyType(), constraintsJLabelNotifyType);

		java.awt.GridBagConstraints constraintsJComboBoxNotifyType = new java.awt.GridBagConstraints();
		constraintsJComboBoxNotifyType.gridx = 3; constraintsJComboBoxNotifyType.gridy = 2;
		constraintsJComboBoxNotifyType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxNotifyType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxNotifyType.weightx = 1.0;
		constraintsJComboBoxNotifyType.ipadx = 56;
		constraintsJComboBoxNotifyType.insets = new java.awt.Insets(3, 1, 2, 27);
		add(getJComboBoxNotifyType(), constraintsJComboBoxNotifyType);
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
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 ||
		getJTextFieldName().getText().equalsIgnoreCase(com.cannontech.database.db.notification.NotificationRecipient.NONE_STRING) )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}
	
	if( getJTextFieldEmailAddress().getText().indexOf("@") == -1 )
	{
		setErrorString("The Email Address you entered is invalid");
		return false;
	}
		
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		NotificationRecipientGeneralEditorPanel aNotificationRecipientGeneralEditorPanel;
		aNotificationRecipientGeneralEditorPanel = new NotificationRecipientGeneralEditorPanel();
		frame.setContentPane(aNotificationRecipientGeneralEditorPanel);
		frame.setSize(aNotificationRecipientGeneralEditorPanel.getSize());
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
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object val) 
{
	com.cannontech.database.data.notification.NotificationRecipient gr = (com.cannontech.database.data.notification.NotificationRecipient)val;
	
	String locationName = gr.getNotificationRecipient().getRecipientName();
	if( locationName != null )
		getJTextFieldName().setText(locationName);
	
	String emailAddress = gr.getNotificationRecipient().getEmailAddress();
	if( emailAddress != null )
		getJTextFieldEmailAddress().setText(emailAddress);

	int sendType = gr.getNotificationRecipient().getEmailSendType().intValue();
	if( sendType == com.cannontech.database.db.notification.NotificationRecipient.PAGER_NOTIFYTYPE )
		getJComboBoxNotifyType().setSelectedIndex(1);
	else
		getJComboBoxNotifyType().setSelectedIndex(0);

	String disabled = gr.getNotificationRecipient().getDisableFlag();
	if( disabled != null )
	{
		if( disabled.equalsIgnoreCase("Y") )
			getJCheckBoxDisable().setSelected(true);
		else
			getJCheckBoxDisable().setSelected(false);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD1F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BF095D5164189880A888242B238C48DCE5C21F41D415119C96DE08AC71D4119EC9556B0CAD9384BEAF4E1169A199DF462F87372859389A4A1AFA401C892A11289A11F428DC17217E04414E4A3EAF6241357C99E69777A253BC3DE90E14F391F6E4E4BFB89D06B56246A3C3E775CFB6EB9775CF34EBD77F607E46F0FC8CEF0E4931210CC785F3184C2426A88D9F67CCBD56042641894E27BFB87E097
	A95B13844F3950F6E8D9CCCAA679B4A98D6A11D00FDA9C13128B65AC72D95236AAA80460938BED618F6C7C3B49795C1CAD7824523AC3CD07675BGD1004367D699796FD51D9C3F066385A4B888096F47BE35EA81473D8F75CE00C7815674337E9B61391DF21EDE77BE6F57B3AF02EC7D45C1C7860EE39BD3C04BG239D644FA272E0C266A0727AAB1593E9A220ED9DGCEBE117C3C7E8BF856F78E8DFC5027FAAA7AA42FD7759AF25FE0056CF29B2AD661D58DF73F3BCFB25C2A37C2137B5CBE376CB56A6A4E5870
	1F8A740732D756A4251512F6CADED9A1E4B665FF1F1AC679915285F5279BF1BFBE0EB245C2BD8E003885BE2C9E71BDD0B6GE63B059ECE9C2C6773BC777503E465EF3C09BBC368A1524D663F46ED69E141486B29F9F44D1E484135C93B9013D28E50G608288870884482736722B6C77605958EBB42B9E0F6AED74791A941776316A1295A85755C18B474164D54DC588338717DA9EC41E490051E957CD47F492D5ADECE34527771175BF7C48119AC4A76B23AA9D519718AE56DE30F442F8FFBFA46F2D97465B6EF8
	5E1F91BB6F4899EAE2F2905EAB1FA9F3248D31F18A06AC5E9DC33856FF21EBDD8F6584EE9F8CFF0C62675A704C0EFFC6ED866B91501624885BB877361025A3E98949BC5A91D48F0BE39A9D038A13A1CC8914E5AB6539C2E1BC371B32A41CC07C13B6BC13E5866D0F750D50F64A9313426CABB6977DFA85541F8458G308D40690DC92981B86595B6161BF3649AED2CD3520C2691376212B542E8E3F37640D3769B1A546E558D495BA7F7CB1ADB6AD5E41D70BE3F4DC19D740E23FB1E5AFE9F70E03E0A6E8B23CB9E
	5910B51D75F9A9E7977651E45A5A657648404063A3E25C7F48490227A26946FF7BDC12A1DBBDD87B9FF3D0A79725EE98918600B2534B9BCE5457A04CBF8CE02E2987ED8F0E77112CC13C282B6BD0552191DF974E04EC70221EE3E86C0804F234D7687835BC441528B1A9752A98677EBD3BED6334387BD0FCC99B93BED6DD0F313BD395B6733BF342E6DAB2161372E7CE87359904F015594AD2B550F7CE50B1D1AED6FF7C7839EB745FCEB538EFCD9DAB1E3FE69E0957B97E5B66DA2D4D4135DA8DB2EEGA0BE2163
	0DFB0A184DB6C93AAC2C74938D8CCCF6D99AE7F23EDE1F497509F2A4F379D9DB6A89ECF342388DG03GF3FDC207336B4F85213B5EF9CC2E43651407FD7C6D7F07BC182E1E4D457DC93E889BE30B6C133DAEA8683C6D05DC64477D37DFB5E9DE22F89F78FBB3B818EE62B76722CFCA062139FBC78C792CEC98EE6F00557E874ACB9F6975D921C2700A4F45B92A23606FE67F84A77ACA1F6A754AF451CC925E1E18076BAFAB328704EED6C73C8643EF4FC3FB9F1247F82C484843A46922240CE05CE1B945BFCAEF61
	9CB43787CC81C397A998C6BF7E2D1993A1BC8A6B0D395196F8A68D8B1F9CAD96BED9F269EE326A2BD2C730BDE571729607EB18790FFF5872C9A6531FC3481E34B74D426BCC56542C03703C56E40679C6C6AE0E3F98782EGD8BDACFCA3ECAF360B78431D22D9D59426E24BAEF25076F5F9F8C406300D016EA3D31FAF646C8EE86B663AE56DAF6720EE95DB0C74E9285B63A7C4AE399E64598A102DB17DF6EACCBF2C5F6A8651AF9A6AEB35D07D7C0D225FD628276B10AB69417A55B709FE8A340783C4867463FA7E
	8AF30CE0F96640083BE244F0E344168CF0865F08C1154D7246E6DF9A9FE323AE72437F51D17F85866C27G9D06301DFF4E90796102218832615E3DA173E41741E41BE9D836D3749D119FFE66C41ECFC25BBAC32C2DEB5C9E55286BEA1F1B1A8BDF172CDC3BEDB11CB3F787BC7B5D1AEE3403737B459A6E23BE0E7BA7C773FC623D06F4BE57CD4037E4A4A6E57188623DB559B659FBC658BE988E4FC4BA5E3C864CA37AA4CDB6AA68AF5B168FDD10AECA7E8AFD9482C5850DC2954D585A0A45B23EAE2B4BF6B399E5
	2AF9CED956ADF23CFB329AB85FB58FACA175D7339C41F26E19BB8B9D9BC618AE53AEDABA3D7C031444CDFF7DD3AA1B5FE6F3FEDD50E776C5B13F225640792D0636B8GC6F7F723200B87DC4D28201BDFE527D3C619DE960C8A3D24B561F1CF224ED7419E1D32562FEA9E59C515D3A10D9ADCB30D34192B062F075CA478C641189BCC3EBFF25879BA7D0CEF03DF70DDF343FC595EF44A97180FF7792DDCE150BFB1EFE0F96AB354AFAE7BD91E1AE162B7D2FC048D4F6657C771D8DF8DED15E3A237FAB18F6374D228
	BF8A908B908710F6894E561704BF5C17DBCD2648DD5B413FB52F241C9D37A9F19F68F1222EE09F617573CE54B35DC70850C3E5C3209EDAAE0D1F7B7900BA237BE082DDC5C0BDBC000E79C26921829123EC2ED3D7F73A8B7669B6372C38D8CCDADEA34E32CBAF095873CD1BBD76C46F2CF23C95A2762C653C37DE327C245CBC1B4E4DC799EA3E067889B088908E9085309AA06EEB217FD71CA72652BFB52AAE3599F027E03FE88E5C6BCDFD3DFA03FACEE81CCA4FE917475331BC607E0F6DF9C051E566BFAD70BC87
	40F47CD92B50316F32488B3FB7015F3549797014F2C6DD8EE68F9FF46787370766C139EF28C975733B0DDFD425F1DE2BCCD9071AEECC563189F40132EE8BAA6B0092CAD655532BDA32EEF6883DE6FE83B10D1E2F3E22FB6CA92877183877B44405C1FD71B7E2CE4F6938EE51D0DF8B3085A07D8A6CD3G8DD7C41FD7E81F73D7D88E02F983467230ABACB7E0FD5C7A79A0FEB14EC73E31663845D6C6BE76FE693CDE92000FB6E58E1F12D7F2409A08F2C6C0BFF1BEF8ABG0F3A6467623C50797EFF66854F776373
	02677B0E3D3B989E46301F7EC57B0E3D131F87F66F8DF69EA82472BE35E7A79DDB57E51D8689CC3CF85BCBC68E89F2AEB8A3F9E4BE57ACB250301D77E9B5546623266D7EC34DE4F7D891D7A66EDBAC66659662FE3A925AE38162G48551814222BA256C56D1DF22FE975CF3257AC498F3DAEF76787DF176F65637A6B6A0856A7876A7E01FC2759E6C555F6C376201AD81FC7A83DA1E9837204357BA7CA4F5AC251BF119FEAFD599A3C57E8DF03AAB38E3CDA668C9E87ECB9758B9F0BF855F0D56454352D188B7821
	BE97A06A2A701F1E564956F4555509EB4A64FBFE1CFC55260D7C3A4ABE5EDACE9F471F71A1473B2749BEDE0DB95E6F9D1349179996EA3C0BBEBC37AA5218BAE290B2B78CF5F01BCF6CCBA5D02F87F08178436C671807C6517F7BB54983EABD6B137B08B28DE99BB5C13B8E7AEF82C887FCB9C047B411977EE08F1E87CEF7C83D32429C0A617F6CC2BAAB4B3037FDB206E7C5F347E9F16BF868E3EDDFDDC1D9584A36FAA43742E9624AB2ADBEF8329D6B9A73F19AC54A226371DD41EAA6BC97B76C396D4AE9ECEFDE
	B7CD643E0ABFD87B26E9E25FF82CC66C9B64A6613F7B8BC37B5F3B05417D2F3CB0F8DC2CAA1CBCAE569506729BE6AFAFFEEC3717CE1051F513E5A37E1BC2655677D37F18FD934B2113CD7CCA0A0F3061D9EEFD3CD51C9DD6C21B73BBC2BFFA2B504FE59377A24D9F56C0FD3D091B358FF1DB20CE42856E15FD02B65D44EDCF94381EF0F1DEFA58A75E752CG5C46F0712E676879F179AD1EC26A6ABE545C2EA6E92003FA820B8FEF50FCG698B260B78F0F898476C023A8290BEDD643D7EF7C55E3B623EA524A36C
	2320E76E8DDDD50E255319EED7CD37725EA848FB994F81CD700C07761A9908DB2859ED8D6D77A1874B07D87B93635AF15D6188E5EBFFEDDCBB4AFB57B87A433A3DBD59C64F66764BD2B13778DFC69165617F8AF91FF0F9861BD384FFF6F6971312DB7E144AE3F2FF0818CCBD14673B0608496BE6089C78DE3F40E55E8C76F2B3625655348461BFBE97341E27002687201863FCD07E7784B687E6BFEF503DE0B560575DACEC6A71E24445A33F198257D2AA6C2CE626589B9E24369C8E75889377930ADBBD135DADB1
	DCE36BE47755C8BFD9FE96B1C5BB63714524BC263A87CF1E221D697E1656EB513D000DA0773619166E53E745244889563DF6963945403B54FE95CEE933F07C1D5528BBA714CBE609BD435D0DFD8C88100BE7617B6865A54414A7E17FE472A4926B3E9D4BBDB6DBC0995643F80DD078631BEF3E4946ABCA1CEC3C5400B9464D9AEFDB71D09F66E333B5796904FB0C290051BDF503F40E616B25E3B17C8D9A5B52EEE1B1BC414427E8084F3661D9FCBDBAAC62EB9134055FA26ECDE616221F47C1BD6956181482009A
	00AEG5F2DE2AF0CAD2CA4D36432DD2A2F83B6CB45363D3179A95A0D3DA3FEACD5443115BF3907BCBA633504D0F7936137B2DF0B644F382EE20AE7734BBF0873DB8BEDDBG32E7C3FE8350GF0EE36181FF3DFB91B9FDE1BB79A0654B7C8EFC445643A5D72280546840A671906FDEE4AEC7E0E16BF974C1E988398DD145FCE97457BC573E7EC903A7DD4CED8EFA27E361C1C08A304795B9B070829479752059E8B3E7FDD52721B579204AF8572584879565CE66DF5F19762D319CFE71F415C33D755E86242F24F43
	E7C47E28GCDB8C0B4C06CED6C7C4C685697CF4CD9DFAD96F4F137311C154C013519436818DFFFD9BAD51C58CE2CB8D18E348DF318DF536FB5203C99E2443DGCC8EBD9377C136FBB53ADC1A2CDB671297AE61FBE9AB1725F929590965101BDA83F3C81475BC70393E3599F84FAA1723DBF1993E59F7G1EDEF934483CECED03A0FB68004BADA96A400172224A722223B5E5E2DDA3C66D5F573C3F63D850F7CE88469C7E5E00BF23EFAE66E37C7ACB7B9819CB5EF1A40498E39527DDB347328DB6076770A89D54BE
	D97B8EBCC6F13BCF542CF23A2DFCEC78DA5F8D5F88B09E1D5FAA8F96C7661506BEB3ACAE8DFEE6787B5260E7064852496FCAD61406FEF77A43D27B3BD396871F2DC67BA537C30C87E8G6882983CDD444046F7517E706B0D09712F55B500F786784506A178179C8CBD574387277E4E2366A01EABFBD543D0BDB83EF996BA086BE528BE0AE4381387D1C71AFBE050E0DDB93E75A05A1BA277D3B4BF2B9862DC92FE3B4883BF6D96671245A66E11EE119BC61A381F79856DD69377073302B6ED2E40F5F7895A6C39E2
	9FFD3F9663158B6AE1G11G51G31GF1G71G1973A0C782288768829884388CB097A082E0A540137344FAB415E1DE5FADDD603BADFB23CBD784107CGC497C5135C38ACFC1F7F71A762DB2375B0D6769D629C75D2A10EB7793B4CAE372148CDBC44733B5704CE71AE33638EF12E5ADD0CBC42213E94E0659D628C17DFA2F20CB500CB1AAF4E84FDA5621CD4BFDF0CB10F76B5308FC0647CEB0D23A22EE49FA3260C45ED624C91BDDF2C47E3479037956A198B04CE07AB8FCF2253CECD75491AB14630748DB677
	1DE88C135C669E29AC8DB586F84AE9085D1F74DE107B0CAEE9402479176A503E72F275543E72FB1AE34BE063F869413FD72A46FDD56D6F57E553A7632BB3CDDC3B5E296AEE7AAD944D53A328DFD402AE6A81188E8B6AF0DEEC3F7DD577D47B2D9D8273F28546447D9573F246AF0E7A129F7061A63F1347ADFE1E2E2978A593ABF78F6437F281BB4BA718BA6E288D2D633335D36B3827B6F8BC6A2D1D980F86EA0347A3CF2DBD9E716F44EAF18E83602F674CFE23A66EF393F7190E09384F5CAEE350445FC465C27C
	A999998A7CCCAA57A8451D63385BEC384FB96E0EDA34931872202A895AC535748C67D694D67FAE6D63760259187A0820B23AFD12CD6E65A66EF3A276551FFDB2D5FE3FEAC130F7C6CFCFC937AF204E68D2BA11EE7D02494F578236C0BFF6AE1AC1E3D652424073526D941F39B0703C64AC96312CG5A42960AFB01F7CAEE6C1CF7648669862F1B0E4DAB3994655FB8E13EEFD37C168973DDE4466EF8E8BBFF27186F21B6BB7F55F7B23D2F634FCDF7CEDC87E6ABBFBF06E5B3FFE1611437BDFDAC1B5A9B04A7C9F1
	DF221F241C050E5E8196A7B660EBE21E9737F5DFEBDE4CE81BCFA2ADABAFEB3446F9796C750D935304346C7D522A2C8CAA2F62764A4D6CABD4A26E228A697EC896413E36C86487CB8EA02E9C6A8D0B4C6F83B1F471B5DDE8AB6FAB7E76F35FE37F8FBC5EAA356614EDAB5FD5726D737E51374A032D6949B6119BF6186B3CFE2F488D4F1B389379A2B7D49609B3F0CDA746F68333AB34B9A833BD32035ACEB474FB526C7B5001E4511788DB7C37E6E2528CD2B936C17F2445E2DFCDEB45F6362FF665DF4FBE3E4DF6
	5F073EDF83E362BD8B6E63EC6C325304889F1AF3G7F1FA4088FB542E62A008B9D65AE44657EDD07C59B3B777A7CEF579924E57F8373D1168E18951BC932C1F67137C9C566A70F3AC6F61AE33653CDEC43456A3A44FF3DB751C1125AC98A5B545BC9926E4C6DA419EE5C6DA491B6F5BFFEA06EA0D6A26154074995DABBACD482D0GBCB246497D785D35AF2EF93EDBCFA4A96DA48F2FC34F4A9A0DE6328B5FCC734F485B48F61F8A6979D94003GBAFDA46B4618A2F7106C7ECF7113C95D10DD0DA2CE6A864969C0
	CEC7B0A5BFF2CA52878D1C6E718E1264458F5E49EE562CC85E01A3ACE89E2733C8E8BC0F9A234BCCC05EACAAAF0ED2E10E1275531D256F1C5E78E8A2F134119D0AAA395A24BEC8DF5B49F6E3D01375C1D5F179FDA79B68C0E923E8E9E88D09B0DC3EB9FD1CEC855E1518C28F796F1A1D911751860A74EB64886D09373F95E68FD773202481ABD9EBB77083FED5ABB15B266DB35EFD7A435C9E87C9601344A5C8717B8C1252AFF95C4A9894DA2815504559E100341A64557163B4102CA8C0B251E4B228187FD46542
	7BDF7F02B230C88D321F2992C80F8C482A67C8E3FFBFC4F8F3G50AB8CDFE88E0FFDAA827A6C764F4BB97D4C1B3DE087ADA451D3D9C9FF0F525FAA7CBDCAB1C7A966E895A8F78B1974AF305DAF66CC0D4734F5DC7E0AB6D8C163594DCF5D73DC7D16CDB03BB612CCBFC603C54834FA1D9971744A5A0C958B8B5FDA3061D6DCA39F1A0A9F47FD5BEF5ABB0628F46E8509A6566E9B96F6A96F0B868222A5634B5FBAB0CD7BA749C5F8D62923A4C9934C9331E9936D8C516966EAE3EDA76D3A0B7EE6DF376E10EA8F25
	F51AD3BEE0CDB91573453F3C3FBDC94BACC913EC12C290CC26B71486C963171441551C90C2782471428B83CBE18787A68452FCBB332A727F6DE97A3825698A425E4AE2F9BF3FA7AD3BEA3FA75DB65D99722ED5D9465F81AC336EC9599DC2917D56BA9A706B16097B885F1E3F1050F789E7BD12229812B25468F5613DC25318A1DB77FF2539B83EAE7A70C67E9D827DD67B020D66FF81D0CB878859DBAD0D3C94GG28BAGGD0CB818294G94G88G88GD1F954AC59DBAD0D3C94GG28BAGG8CGGGGG
	GGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG7694GGGG
**end of data**/
}
}

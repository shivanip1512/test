package com.cannontech.dbeditor.wizard.notification.group;

/**
 * Insert the type's description here.
 * Creation date: (11/16/00 4:03:11 PM)
 * @author: 
 */
public class GroupEmailSetup extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelFrom = null;
	private javax.swing.JPanel ivjJPanelEmail = null;
	private javax.swing.JTextField ivjJTextFieldFromAddress = null;
	private javax.swing.JEditorPane ivjJEditorPaneAdditionalMessage = null;
	private javax.swing.JLabel ivjJLabelAdditonalMessage = null;
	private javax.swing.JLabel ivjJLabelDefaultSubject = null;
	private javax.swing.JTextField ivjJTextFieldDefaultSubject = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
/**
 * GroupEmailSetup constructor comment.
 */
public GroupEmailSetup() {
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
	if (e.getSource() == getJTextFieldFromAddress()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldName()) 
		connEtoC2(e);
	if (e.getSource() == getJTextFieldDefaultSubject()) 
		connEtoC3(e);
	if (e.getSource() == getJEditorPaneAdditionalMessage()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTextFieldFromAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupEmailSetup.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupEmailSetup.fireInputUpdate()V)
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
 * connEtoC3:  (JTextFieldDefaultSubject.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupEmailSetup.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (JEditorPaneAdditionalMessage.caret.caretUpdate(javax.swing.event.CaretEvent) --> GroupEmailSetup.fireInputUpdate()V)
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
 * Return the JEditorPane1 property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getJEditorPaneAdditionalMessage() {
	if (ivjJEditorPaneAdditionalMessage == null) {
		try {
			ivjJEditorPaneAdditionalMessage = new javax.swing.JEditorPane();
			ivjJEditorPaneAdditionalMessage.setName("JEditorPaneAdditionalMessage");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPaneAdditionalMessage;
}
/**
 * Return the JLabelMessage property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAdditonalMessage() {
	if (ivjJLabelAdditonalMessage == null) {
		try {
			ivjJLabelAdditonalMessage = new javax.swing.JLabel();
			ivjJLabelAdditonalMessage.setName("JLabelAdditonalMessage");
			ivjJLabelAdditonalMessage.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAdditonalMessage.setText("Additional Message:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAdditonalMessage;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDefaultSubject() {
	if (ivjJLabelDefaultSubject == null) {
		try {
			ivjJLabelDefaultSubject = new javax.swing.JLabel();
			ivjJLabelDefaultSubject.setName("JLabelDefaultSubject");
			ivjJLabelDefaultSubject.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDefaultSubject.setText("Default Subject:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDefaultSubject;
}
/**
 * Return the JLabelFrom property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFrom() {
	if (ivjJLabelFrom == null) {
		try {
			ivjJLabelFrom = new javax.swing.JLabel();
			ivjJLabelFrom.setName("JLabelFrom");
			ivjJLabelFrom.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFrom.setText("From:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFrom;
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
			ivjJLabelName.setText("Group Name:");
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

			java.awt.GridBagConstraints constraintsJLabelFrom = new java.awt.GridBagConstraints();
			constraintsJLabelFrom.gridx = 1; constraintsJLabelFrom.gridy = 1;
			constraintsJLabelFrom.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFrom.ipadx = 18;
			constraintsJLabelFrom.ipady = -5;
			constraintsJLabelFrom.insets = new java.awt.Insets(6, 19, 7, 1);
			getJPanelEmail().add(getJLabelFrom(), constraintsJLabelFrom);

			java.awt.GridBagConstraints constraintsJTextFieldFromAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldFromAddress.gridx = 2; constraintsJTextFieldFromAddress.gridy = 1;
			constraintsJTextFieldFromAddress.gridwidth = 2;
			constraintsJTextFieldFromAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFromAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldFromAddress.weightx = 1.0;
			constraintsJTextFieldFromAddress.ipadx = 242;
			constraintsJTextFieldFromAddress.insets = new java.awt.Insets(5, 1, 2, 13);
			getJPanelEmail().add(getJTextFieldFromAddress(), constraintsJTextFieldFromAddress);

			java.awt.GridBagConstraints constraintsJTextFieldDefaultSubject = new java.awt.GridBagConstraints();
			constraintsJTextFieldDefaultSubject.gridx = 3; constraintsJTextFieldDefaultSubject.gridy = 2;
			constraintsJTextFieldDefaultSubject.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldDefaultSubject.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldDefaultSubject.weightx = 1.0;
			constraintsJTextFieldDefaultSubject.ipadx = 186;
			constraintsJTextFieldDefaultSubject.insets = new java.awt.Insets(2, 3, 10, 13);
			getJPanelEmail().add(getJTextFieldDefaultSubject(), constraintsJTextFieldDefaultSubject);

			java.awt.GridBagConstraints constraintsJLabelDefaultSubject = new java.awt.GridBagConstraints();
			constraintsJLabelDefaultSubject.gridx = 1; constraintsJLabelDefaultSubject.gridy = 2;
			constraintsJLabelDefaultSubject.gridwidth = 2;
			constraintsJLabelDefaultSubject.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelDefaultSubject.ipadx = 7;
			constraintsJLabelDefaultSubject.ipady = -2;
			constraintsJLabelDefaultSubject.insets = new java.awt.Insets(3, 19, 12, 3);
			getJPanelEmail().add(getJLabelDefaultSubject(), constraintsJLabelDefaultSubject);

			java.awt.GridBagConstraints constraintsJLabelAdditonalMessage = new java.awt.GridBagConstraints();
			constraintsJLabelAdditonalMessage.gridx = 1; constraintsJLabelAdditonalMessage.gridy = 3;
			constraintsJLabelAdditonalMessage.gridwidth = 3;
			constraintsJLabelAdditonalMessage.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelAdditonalMessage.ipadx = 14;
			constraintsJLabelAdditonalMessage.ipady = 2;
			constraintsJLabelAdditonalMessage.insets = new java.awt.Insets(10, 20, 2, 174);
			getJPanelEmail().add(getJLabelAdditonalMessage(), constraintsJLabelAdditonalMessage);

			java.awt.GridBagConstraints constraintsJEditorPaneAdditionalMessage = new java.awt.GridBagConstraints();
			constraintsJEditorPaneAdditionalMessage.gridx = 1; constraintsJEditorPaneAdditionalMessage.gridy = 4;
			constraintsJEditorPaneAdditionalMessage.gridwidth = 3;
			constraintsJEditorPaneAdditionalMessage.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJEditorPaneAdditionalMessage.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJEditorPaneAdditionalMessage.weightx = 1.0;
			constraintsJEditorPaneAdditionalMessage.weighty = 1.0;
			constraintsJEditorPaneAdditionalMessage.ipady = 124;
			constraintsJEditorPaneAdditionalMessage.insets = new java.awt.Insets(2, 20, 23, 19);
			getJPanelEmail().add(getJEditorPaneAdditionalMessage(), constraintsJEditorPaneAdditionalMessage);
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
 * Return the JTextFieldSubject property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldDefaultSubject() {
	if (ivjJTextFieldDefaultSubject == null) {
		try {
			ivjJTextFieldDefaultSubject = new javax.swing.JTextField();
			ivjJTextFieldDefaultSubject.setName("JTextFieldDefaultSubject");
			// user code begin {1}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldDefaultSubject;
}
/**
 * Return the JTextFieldFromAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFromAddress() {
	if (ivjJTextFieldFromAddress == null) {
		try {
			ivjJTextFieldFromAddress = new javax.swing.JTextField();
			ivjJTextFieldFromAddress.setName("JTextFieldFromAddress");
			// user code begin {1}

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFromAddress;
}
/**
 * Return the JTextFieldName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			// user code begin {1}
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
public Object getValue(Object o) 
{
	com.cannontech.database.data.notification.GroupNotification group = com.cannontech.database.data.notification.NotificationGroupFactory.createGroupNotification();
	//com.cannontech.database.data.notification.GroupNotification group = (com.cannontech.database.data.notification.GroupNotification)o;

	String stateGroupName = getJTextFieldName().getText();
	if( stateGroupName != null )
		group.getNotificationGroup().setGroupName(stateGroupName);
	
	if( getJTextFieldFromAddress().getText().length() > 0 )
		group.getNotificationGroup().setEmailFromAddress(getJTextFieldFromAddress().getText());
	else
		group.getNotificationGroup().setEmailFromAddress(" ");

	if( getJTextFieldDefaultSubject().getText().length() > 0 )
		group.getNotificationGroup().setEmailSubject(getJTextFieldDefaultSubject().getText());
	else
		group.getNotificationGroup().setEmailSubject(" ");

	if( getJEditorPaneAdditionalMessage().getText().length() > 0 )
		group.getNotificationGroup().setEmailMessage(getJEditorPaneAdditionalMessage().getText());
	else
		group.getNotificationGroup().setEmailMessage(" ");

	// just set the pager field to None for now
	group.getNotificationGroup().setNumericalPagerMessage("None");
	
	group.getNotificationGroup().setDisableFlag("N");
	
	return group;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldFromAddress().addCaretListener(this);
	getJTextFieldName().addCaretListener(this);
	getJTextFieldDefaultSubject().addCaretListener(this);
	getJEditorPaneAdditionalMessage().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GroupEmailSetup");
		setLayout(new java.awt.GridBagLayout());
		setSize(353, 387);

		java.awt.GridBagConstraints constraintsJPanelEmail = new java.awt.GridBagConstraints();
		constraintsJPanelEmail.gridx = 1; constraintsJPanelEmail.gridy = 2;
		constraintsJPanelEmail.gridwidth = 2;
		constraintsJPanelEmail.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelEmail.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelEmail.weightx = 1.0;
		constraintsJPanelEmail.weighty = 1.0;
		constraintsJPanelEmail.ipadx = -64;
		constraintsJPanelEmail.ipady = -6;
		constraintsJPanelEmail.insets = new java.awt.Insets(7, 7, 51, 11);
		add(getJPanelEmail(), constraintsJPanelEmail);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 232;
		constraintsJTextFieldName.insets = new java.awt.Insets(20, 2, 6, 18);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 2;
		constraintsJLabelName.insets = new java.awt.Insets(20, 10, 7, 1);
		add(getJLabelName(), constraintsJLabelName);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2001 4:17:10 PM)
 * @return boolean
 */
public boolean isInputValid()
{
	if (getJTextFieldFromAddress().getText().indexOf("@") == -1)
	{
		setErrorString("The Email Address you entered is invalid");
		return false;
	}

	if( getJTextFieldName().getText() == null
		 || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The NOtification Group Name text field must be filled in");
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
		GroupEmailSetup aGroupEmailSetup;
		aGroupEmailSetup = new GroupEmailSetup();
		frame.setContentPane(aGroupEmailSetup);
		frame.setSize(aGroupEmailSetup.getSize());
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
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5CF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DD89C553528792186ABB1C42372DA1AEA3F587214EA7A0AAD56345DBEE30DB576E11B77DEDA73BED3656BCBAD2F46B65658DCF6798BA106GC93092C3CC2209D28291BFF90816FF83A191E1C592B1B03083EC185D594C8E307C443CF36E1DBBB3AC33E072697B1A6FBB1939675E73F34FBD675CF36F8EA4FFDFCCDA0ABD1F1014B4227DFBAB05102E378909797776F00EDBA65A5209695FFE001D64
	474ED4F846C3DFF10CADBD075CD11B896DD5500E3A51163E875E7348725A04B760A5041CA8687BE8E5A0E1E6B93535DCCE86ED3F36B39B1E4F832C869CBE8B7BD1FE6D4E828D3FD143F3C8319312B900E3BE59D92861FA00A6GE083D8534F46A743F3AB15BC3732C29B5735B006EC7C692B766D48474413C36BG23D535E79139A325F090E54D23FAA2CDAC742D874049BB488E4F3B702CEA9D9AF837CF76147489DE2F6CD5453E4112D177382038CA3C326A6EF777892ADB76168CA872302F3272BDFCB4F88437
	54AE2A43BEC216D099BF5B19C9F942FC20FD500D384783A8F7AD34B782E43BB97E1F4790BF886FE1GCB5CFC2E6F3CDA254D25F3628E327917DE470E90F30DF333B92EF19BF30D9A7EF3468BF4DD2E2CC37B671F3325D7G34G78GA28162GDE247E70255A5760D9532B56499E0F6C2D7179EAA517F2D2F609923CD7D6C20F06EB937BE445C588DB73081D45967A4C840C6E26CB26E3B6D97686DF7F5836EF1035F71D30E7D858E46D32D27B4AF34C966B4E99B6E1327FAD24ECB2B455BF274A7E0818E5EF18A7
	BB52ACE4AFFF6408BDDF63F3F848105DB904EBFD062E75F1F8CF517C0361DD949FED42B3DF5DC0FD865B31B8CE623E5179BC5725A575A612D356E2E907A5BF2A312B9253A1C28A56E57FB8720E17184C2D3AAEA5941FE842B3DD6E22BAE2BB997A1C9EDBBA732F858D983B7150CE8458G106A855F862886F0FA390F7D3C7668E77431D6C1D1EB075D12CBD48823DDD0379B1EE2C0D504A62FDF953CFDE207203805DEC97493EDCCD49D5A20F78A5D0D54777BC046FB22D7D4E86882F741A32A2262E7E316566D44
	B10AC8FB1DEE0F8882BC904A9A0FEF566541D3927C6A07BE17200A468856FFE79D5AE4C468G0E40G5E19DD629B51DEAA4CBF82A0DA37435F6A115F89D1710AD2E5E50BAC8F8D7B1CB89332410BF67EAD4D9DF1703E524BED3C7CBD44154A367463B267632A4FB57129F777217A02B246E32CE78C73F3174CFD6649CE6EB3755BEFA5450FB4DB7A8CC2C0E63E92A987474EB94A9375E26DBF84BAAD683BE46BB83AD4E0B2360DD949F0FC8E7C1F57576A65A69A8B20F7A240C65D46DF2DAFE2BEDBAB78C56E256F
	AB60E0224B30B8537355719C4D1E28DB1A26BF6BAB27FD59BEDBFAB9C097409C9F37615DF4FE41F41F475C8C9B7E1C4AB0735F7AB94AE036DA5C007B13B8A2FA55FA51A7FADD7062577A229BD01E963F7D32CE331462FD906FF590E0FE9D3F2C81E3D2D0D5455DBB2C0A30372AEE6F00519FC7E57907FBFDC62A60323E5B00F314C7A15E7571898D98ABFD3257AB52C553C9347E9F50D895A55183CA574943DE1561ED0D686FC3621816ABD6B6E2A1B4A2C84318F7D85D90A7EC43B9A8EE8F388226AE12F39E63B8
	A53740E423D8D8EF2CFF5643F3130F476468819E1387473FC6D6F49F32DB6DA9CBEE2D37F71EE7B1A51DB7E21269F477FB563A5FCBF3060677B3DDDFF6215D020B9696839FB7A31F88609F8390FF1E47405B47311F67994D796BE4C9E22654EDFC3889FD5CAF1E9F96A1BDE3C2BB215B6DE8D3EED0DF07E6C3567FD6935AD0B265C21702B69C9C67F561EA50A789A0D3E1F62CD7189D583857A77838B8E82F84C8329C7724BE6E205F16DE658FB5EE69A49F5785E3D40BF14C1E995DD8CBD8550C83436E12E1550D
	19D9D04169FD43EA2BG96E47561D0EE2646A34A4F6B40393B507E2B203D9EE0331F7B48435BF99D98B594C356FD7D2FA1EB5EFC1569D6219ABED2F4952F83EFBD01B28350972E72353D33493C9755787DF21F1BEE035A3A884DE69FE2387E66ACF8763B953F5A84C19E60EBA8B5233E38CFEAE82DEED8C86B891CEBB448CD841006917F171772CD3AE7832E9C209340C179ED9A47FA21CFD0C435047E4F365F43670491A1D0629F0504D0C233CDC99D76B6606B91EDDDECC7F2190EA2353CC6F9240357F2CB732A
	B5392BEE3F09D4DD4C33DB555661BB765A178FB3DB2E9DB6ECBAF9E73AE34359E7B3587C1C93FCFE07C7EC69BDA3FCFE131D417343FDF349881F5F77A7AEE7FECC661EC9AEB396782D57E55EF75A4A260523G146E811D2E94F003231C2EB045CC97B34A68D60D72BD4A179BDCB32F9BB576780D235377FBD6CBBEC3FDBAB340EA498C9D1F369B71C5A6BC53EF819D0F6D8A680B8E707A277CA466D2B28673GA887E88110GA24638AFEFEC46A36D0C75E593442662952476A99B872F73CE603AC02E575A371FC0
	BB51DCCF78BEFA43F8309D160DCD1D3BAD284D68EE1DC6D79254CE8A22E3FEFC68F0A14FAFA637282CECF642DE5A6896A5975BEB5E5DED5EEB48B8733503702CGE0B9674DFD7CCC5ABA4EF34B05C6F3EEC958F14C3EADC4EE899FE77AAD9BB76220D8BFE37E7A946A1984FD299330C6G95GDDG810068893EC695AD6F4C36C6544F1DF29D600E43FED0973CE76BB6BDB3F1F9EBB1B80DAEF8AD92A62652B17B5D55496D17B84163E26F646569707AB43AE09D32A72DF4784BE92E4341C92E434317294323336A
	50B5E96513E559FB2DFD324165D6E585F7B9DD5F76942E2F8A3C5879E6BE5D7BE2213DC2473565A3EEBD34B76B735AC2F3C1518570A5G67850E5FC6F38766A75C67A59D3FF0D770B981E94DED67856BF3C9C5887CF41BFDF31A0C60F3C8285ABDED967E88ACC65EB9993A0E2EBFE9DDC7B71F342E23DBCF6EE4F860E1BED5737E4E13B35759BDA72D6A6C3DF45E776F5EC1F97B7D221F86AD96BADA5F936AAEE2D1EF1F020337E633BC7233712D5A1886D52EAB537D6B29A385567EE52A1FC6F6E7EA360E38406B
	274EEA748B9B345782EC547D22F25FCCF7CD591FCEDFFB165344D65C930B203F8A4089A081C4FC4AF3599FCF4E3A5FB484E658EF12CE05DE675F1F32DE6727CFE16D67170715BEB1F8ADB71FAA507B0C2C59240A9EB4BB9B1376BE522B02B2A0CE73056C778B743ED0743B5E8F65AFECCD97CE185774189EB7711DB32DC12C459AB0FE4EC9B33FB7F4FE0EB3B371DB9112DF6685B33FF21DDFE74ACC7C92C3727BCAAE1E87A5E1CC9ED6A13FDCC4BF7C0EFE6ECB82BA823843GD5974D6706EF0F62BE5B2F889EB0
	DF3BCF6CA325E1C86B4E673451B0BE8EE0B5C0B2728D63F563BC3AC733E82007B8CD1742E224B3AA8396EC2C6F4DE22C591BDB04DED1E2F8D677BDBAE22E7B82E12CEE088E6331B3315BEC17B8405B428CDB2C898BD5772D263138A10C55F75BF57C23941FF30501E7F55FD90A47F6B97445DC4177058F7671FDA17BCA9EF71E5650F12336DA474DF82BF5FE8CEB1DB9BF06370672F7265FC31DDC3F2EAB394EFFBD43F131BACECE517737ABF9AD7E2BDC7EBBC44BD53674B9D7715FA14AFA2654FCA542280A3FB3
	38DD3542C08B75B416838F65E5EA7489D7719C581E07BCB7C2BBFB8E606670BAAF708A2F73E23FF193E989BBE1F906DC67BCE62F1A435634E70ED167AD03BA0F493CB61F4B0C067E44B9083BA75F6CC368EF998ED6CB307E5FCF69C7FB9EA1667E7D3B4C7D286F0F6D667E76A97DE9A6FAB637878E7139EDFEE099113E733FA14F4705F3591C2A35E7EB4781F2706AE7B334FD27BF443EC323064DFF77A85FF782F3795ADEC77722B8E85BF45C535D08CB0276E69D278A5C870A6671787AC5B54A74C1BB9CA096E0
	B5C0B2C07EFCBE66B7F4CC55FCF67F01F796DD70BEB91F5DCB30B12E6A9E0B797AB4BEB3559CDCA63E074FB346A64F1BCE47E4DD3414655062F7369A0A5FBB1A6B21ED96E3D74F2777BF7B6AE76017C60CBBCDFCC683CD9C40818DE703775F24B0DFE07C9E6F7CAC7CB894045B520B438DFE55707E7094FEE5E7E66217914433A5FC2AFEBD509E1E42EFDE4ACC7C9C1A7DB234F7896843B47D309D954E726AB37ADABDB66D5CBE9BB03A7F39CC3A5772AE150E6D8B0772B1EF45E87A276878920A0FB561D96EBC1D
	47F3E7BC7495FE095F931CE843D88903F6B440F200D5G9BGB22F667B47CB9F1412D96AB6276CEB018DC6B2ED89ECFED17917773B658FB3F80EDA7E3D25A4FE5EB3A9214E5905D733382B521EC94E83942F1D335BF1FE92740583448258G56GA46B736B7F20184D8F4FE3B52AAA748D525BBBBE398E37B8EA203178E0F3FBF154BC375485CCFE0176ACDE902A863AEDB756C03D32C2B7CED6832D506FA32A012E93A0926016856CDE0951E577CC2F1D8E74F03A04852CF6CA04E712C647E2E401B0DB0C2491A3
	6EC884644088A3C68E437B95DB36ECF99A0069F10F47280D9A9559436735F5G4FB0C6BD05FDB5AE17A27A0DFAF7253BE04A18FA31DF9816D428774FC1794C46BCD01B2F73376A3F2DF0375E8F7C618C8DA70613A0C598506BB7EF8D56842693B69D68B60DE4755C0BB541759C5A2DBCC23F134C326ABF2E77D7BB2C7A3B20FF9F550310CC427FA57FFDBA8E761C887CD6E47F99227B7EFFE5F35FAF7C564D247E174F249C88617B4BA3180F25DEE3549EC967B6E7DC1A6C859F633B958D6B3F3DABD4FFE81A83E7
	31255D5D649B6F6526776EBC2B5FA9BFAFE0B282DF288C16BF1CED216B6B0F5B2C6BEB214D3A3E9EE81B79FEC1EE8B7DBB5EC81B79F7BC167F1EBA0B862F827F68841884088438659A1E7BD25BDFA6B477D9643D8657G1E0B716B81154BFF22BD74DC1FEC1F7D1B03BF35E36E6815D5D576A0FF0EFF2E9D57CB15FD9449F0F6A75AC8F18F8C2AEC28064FF4223FC9E2BFC533DA71982DB3D7403CD6DF436B47FBBAE87D886D0DBAAEFEBF2FA94917793962ABFB78B7A32D008B7FB2BFD7BC31976B6619FF2BF23A
	D5C92C55D239F60777789E7EDB550AAF73F3C6EDB74A58846D1CEB61FCFBAD17FD22076F4B55008BDC4B6F1E977570F3C35C351C478F6958A4E813C8DBFAC164E74DB1BCA6677590DD47369ADE1717C6F219F7E5F15C200E9BF5F0DCCCA4BF9BBD7431791B321C1B765BF92D919C9FEBA2D9DEDA9FE964A76617F753BD29E0A164CB00CEG9F409C00E800B8G9B409A00E4004C6B40DEG25G75GCE00C100496B74BD7C8C2ED507F0CE2BCED862271E8CC86DB0C81DD9915C684E1AAFBDFBB634BF5B3BE77767
	ED745CA2029FBA47FC5A1DCFCEB76E53F2FF3FDF5463E6D7F70E0EEB72374AFE3A99313D2AE994FFB36F875FDAF59D5A3C1B7AF2A43CC7DF47777E448E9C2302B2ECAD977761CA307D7D217D336D6FE6A8B6556838E6B90B183F735F15F612DB764CD63395AE323A9F3FF3D63A17025A0C6E3FA9DD4522596F1491DAE09C2BF57F0546D44F22609A78DE0A1796855740473BF92C8520EF6DA2FE967C206772EA776E4B24DBF249F4ECDEEF8A28FFF5D470FC5F2578162860795E276716CE68CB0862737D2846AC3F
	E8B133FB3976BC3EB854B9B6BE4BCC57250D73E94FB9A169A69DE63AA55A38D86D3942020E6508DA97BC347879E94FA192A23EE127EA96C60477E905689486F41A870550311DA84C9E5B3F922C772A470469FBD512E03DD7BDA91877AA06FBCA40F89B001C54290F5B2463CE6B3867A8CF44FD64F62903BA5E511B25610F0BA81063377622DE23945729611EB761CEEB383CDE4CCFB065C1D96134853DB48739A509358B699837975215EE0F7D3D28235BA71874FEC547215EACB7A9EEDCB716373E5D017505D79C
	AD62D7F3A50D10688E3F6CF28B12BC70F2F1D1E9F1D1D97991DE7B460C1A77160A2C37AC3F6FC1C8547CA6C9FBA64CBF20497F43B96444FED7D911371D4EC3F2FB45BA76D5A361F7117682DC1B226BED699557737A605E3308EB01F64F75BCDE923A096E4BBFB13D1F6A7E626B57017F8799EF8B461C6ACD6F8F76FC71329F7FC2E530B57552DAC702751C54575955C677D8E84768387B5B74FBE89D77D096275D2C63E23AB8ED4E8D9C17728927AD54F10B9C1CD652F1FFBC4DE943F45CC77A7DF7448D7CDE662B
	C308DB8B6DCDB7709A673B6E57C9689A27D511FD22220EB1AC7DDACC0B41978AB036D42CD4B176601D5599E5547F8B16C0FD390457993FB81B4647929E4FB99E748956BF37E6EA2DF2B3352D9368FBF49E93EDD3EB15G061CD62B2CEB3B14DAA54EF4BF09B9A092E460BE7027ADDB36B0791B687AC483FE99G13F733EC48AB7864D26EBE02652D811E3807BC274BCB20EB3A9E70493A3C9F788CF95E996FB79D44784655CADE510D36743D1ABEEC4DEF99C2BA7DBC4816D56B7B56901E6D5CDEA8A58549BDCEBF
	C3EB07015E812657503031EEF71D453FC1314843B53069C910064B34B42C2D79FD2386EDE75BE74D610C76E97C14CDFB97A78CBED1DD1746A7FC5260F3722241673727AF0D8FB5ADE1FF477368879100097D9DC973ABE407CE5FC4B73AF5A3EFD4BA9E7B3A43CED21BC8BA5B781BC8AA6E5ECDA40DEE6ECD44819BFFGD75C2EF1468DA64DFF1EFC0AD2C83A56031F53656B58ACE2DC785BF5AC2A14CF58F14D40729FED7142B3C3A55B684727C578A3E4A5683CFD4A1CFF7235B79FDF756B8E3F0324B71197B0AC
	5BC505FA0368422F8B34CF6C9B49D61F8C471EF64043047C741166D747A4310564773761E726FED5F455F0BF732BE4D78BCABA0A475C2347857F200A35515BADA4550BFF8CC0F2D937A4F8870EB227FB1BDAA52526074E825D16003EF9D4DF64D222F3497B7E0EC37B1B5747BB083D11E4C93260EA947AA0E9B5112D6A20A27A87E54995707D3D1AB24A2427A1B40683583D28CF9FA7DB0277083A52C301FF0A581E343D914C9FD048D1BA9233D009BE42D5B7A8A8A0CAD41AD47C6386D9B92877DD71127A4A036F
	6D69321394ED1238A0DB83BE1524778B9E37B486AF75544368D2EDD7C1DBC5707A7BE545831A9585E9463BF481A50B6EAFDDFC5B1F4FD0818629CA76B1D382695181D1769C2D696F07EAC5E7GF68576FBF576B826A4E8CCEEE0612E66C73674029F5493072734147EDFC67FBF067F17D1CC9945149D8363A613997F857787781C29736831034BDF5288AB287E476F6EDF7A1FD5499BE0F60DA40DFE84880B10E30CBAB56C699515F9310B77EE0BDA37G5748072E92C03EBB833A77E7E8510211152F12B47A03BD
	BC71B239D2A5B95A05F011F6A38C28BC63EA37C83B5B2D246CB2025CB105E237CD732BD4C46F18363A692CEA264D971186DFF2E91385761ECBB6A2D23D40986AF3BF9430D41604D056B1D559AFC62712296B11272DC77ABF1E26EF9A1AE6EB1A5262619FCF53AAC3D35C9FB06F5F8E10454EBDC7AE1A4FBD1B669604BC7778E23473F6CC701D5A77BE24F72000DF9B43EB462C8F7FC6C21F1B5BBD02A42902B4D463F561993AF6CC950D3B66FBBFC47EFEBEC66BE4FD7F7AA16AFD4EC473FFD0CB8788B4A0020665
	94GG04BBGGD0CB818294G94G88G88G5CF854ACB4A002066594GG04BBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9F95GGGG
**end of data**/
}
}

package com.cannontech.loadcontrol.gui.manualentry;

/**
 * Insert the type's description here.
 * Creation date: (4/19/2001 3:54:21 PM)
 * @author: 
 */
public class AckUserPanel extends com.cannontech.common.gui.util.ConfirmationJPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private String userName = null;
	private String notes = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonSend = null;
	private javax.swing.JLabel ivjJLabelNotes = null;
	private javax.swing.JLabel ivjJLabelYourName = null;
	private javax.swing.JTextField ivjJTextFieldYourName = null;
	private javax.swing.JEditorPane ivjJEditorPaneNotes = null;
	private javax.swing.JScrollPane ivjJScrollPaneNotes = null;
/**
 * AckUserPanel constructor comment.
 */
public AckUserPanel() {
	super();
	initialize();
}
/**
 * AckUserPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public AckUserPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * AckUserPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public AckUserPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * AckUserPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public AckUserPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJButtonSend()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonCancel()) 
		connEtoC2(e);
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
	if (e.getSource() == getJTextFieldYourName()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JButtonSend.action.actionPerformed(java.awt.event.ActionEvent) --> AckUserPanel.jButtonSend_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonSend_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> AckUserPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JTextFieldYourName.caret.caretUpdate(javax.swing.event.CaretEvent) --> AckUserPanel.jTextFieldYourName_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTextFieldYourName_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}
/**
 * Return the JButtonSend property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSend() {
	if (ivjJButtonSend == null) {
		try {
			ivjJButtonSend = new javax.swing.JButton();
			ivjJButtonSend.setName("JButtonSend");
			ivjJButtonSend.setMnemonic('s');
			ivjJButtonSend.setText("Send");
			ivjJButtonSend.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSend;
}
/**
 * Return the JEditorPaneNotes property value.
 * @return javax.swing.JEditorPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JEditorPane getJEditorPaneNotes() {
	if (ivjJEditorPaneNotes == null) {
		try {
			ivjJEditorPaneNotes = new javax.swing.JEditorPane();
			ivjJEditorPaneNotes.setName("JEditorPaneNotes");
			ivjJEditorPaneNotes.setBounds(0, 0, 296, 81);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJEditorPaneNotes;
}
/**
 * Return the JLabelNotes property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotes() {
	if (ivjJLabelNotes == null) {
		try {
			ivjJLabelNotes = new javax.swing.JLabel();
			ivjJLabelNotes.setName("JLabelNotes");
			ivjJLabelNotes.setText("Notes:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotes;
}
/**
 * Return the JLabelYourName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelYourName() {
	if (ivjJLabelYourName == null) {
		try {
			ivjJLabelYourName = new javax.swing.JLabel();
			ivjJLabelYourName.setName("JLabelYourName");
			ivjJLabelYourName.setText("Your Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelYourName;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneNotes() {
	if (ivjJScrollPaneNotes == null) {
		try {
			ivjJScrollPaneNotes = new javax.swing.JScrollPane();
			ivjJScrollPaneNotes.setName("JScrollPaneNotes");
			getJScrollPaneNotes().setViewportView(getJEditorPaneNotes());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneNotes;
}
/**
 * Return the JTextFieldYourName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldYourName() {
	if (ivjJTextFieldYourName == null) {
		try {
			ivjJTextFieldYourName = new javax.swing.JTextField();
			ivjJTextFieldYourName.setName("JTextFieldYourName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldYourName;
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:16:40 PM)
 * @return java.lang.String
 */
public java.lang.String getNotes() {
	return notes;
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:16:40 PM)
 * @return java.lang.String
 */
public java.lang.String getUserName() {
	return userName;
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
	getJButtonSend().addActionListener(this);
	getJButtonCancel().addActionListener(this);
	getJTextFieldYourName().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AckUserPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 171);

		java.awt.GridBagConstraints constraintsJLabelYourName = new java.awt.GridBagConstraints();
		constraintsJLabelYourName.gridx = 0; constraintsJLabelYourName.gridy = 0;
		constraintsJLabelYourName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelYourName.ipadx = 8;
		constraintsJLabelYourName.insets = new java.awt.Insets(15, 17, 9, 0);
		add(getJLabelYourName(), constraintsJLabelYourName);

		java.awt.GridBagConstraints constraintsJTextFieldYourName = new java.awt.GridBagConstraints();
		constraintsJTextFieldYourName.gridx = 1; constraintsJTextFieldYourName.gridy = 0;
		constraintsJTextFieldYourName.gridwidth = 2;
		constraintsJTextFieldYourName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldYourName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldYourName.weightx = 1.0;
		constraintsJTextFieldYourName.ipadx = 187;
		constraintsJTextFieldYourName.insets = new java.awt.Insets(12, 1, 6, 92);
		add(getJTextFieldYourName(), constraintsJTextFieldYourName);

		java.awt.GridBagConstraints constraintsJLabelNotes = new java.awt.GridBagConstraints();
		constraintsJLabelNotes.gridx = 0; constraintsJLabelNotes.gridy = 1;
		constraintsJLabelNotes.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelNotes.ipadx = 9;
		constraintsJLabelNotes.insets = new java.awt.Insets(7, 17, 75, 28);
		add(getJLabelNotes(), constraintsJLabelNotes);

		java.awt.GridBagConstraints constraintsJButtonCancel = new java.awt.GridBagConstraints();
		constraintsJButtonCancel.gridx = 2; constraintsJButtonCancel.gridy = 2;
		constraintsJButtonCancel.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonCancel.ipadx = 12;
		constraintsJButtonCancel.insets = new java.awt.Insets(7, 6, 5, 15);
		add(getJButtonCancel(), constraintsJButtonCancel);

		java.awt.GridBagConstraints constraintsJButtonSend = new java.awt.GridBagConstraints();
		constraintsJButtonSend.gridx = 1; constraintsJButtonSend.gridy = 2;
		constraintsJButtonSend.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonSend.ipadx = 22;
		constraintsJButtonSend.insets = new java.awt.Insets(7, 88, 5, 5);
		add(getJButtonSend(), constraintsJButtonSend);

		java.awt.GridBagConstraints constraintsJScrollPaneNotes = new java.awt.GridBagConstraints();
		constraintsJScrollPaneNotes.gridx = 0; constraintsJScrollPaneNotes.gridy = 1;
		constraintsJScrollPaneNotes.gridwidth = 3;
		constraintsJScrollPaneNotes.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneNotes.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneNotes.weightx = 1.0;
		constraintsJScrollPaneNotes.weighty = 1.0;
		constraintsJScrollPaneNotes.ipadx = 277;
		constraintsJScrollPaneNotes.ipady = 59;
		constraintsJScrollPaneNotes.insets = new java.awt.Insets(8, 60, 7, 15);
		add(getJScrollPaneNotes(), constraintsJScrollPaneNotes);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:18:59 PM)
 * @return boolean
 */
private boolean isInputValid() 
{
	if( getJTextFieldYourName().getText() == null || getJTextFieldYourName().getText().length() <= 0 )
		return false;
		
	return true;
}
/**
 * Comment
 */
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	setChoice( this.CANCELED_PANEL );
	
	disposePanel();
	
	return;
}
/**
 * Comment
 */
public void jButtonSend_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	if( isInputValid() )
		setUserName( getJTextFieldYourName().getText() );

	if( getJEditorPaneNotes().getText() == null )
		setNotes(" ");
	else
		setNotes( getJEditorPaneNotes().getText() );

		
	setChoice( this.CONFIRMED_PANEL );
	disposePanel();
	
	return;
}
/**
 * Comment
 */
public void jTextFieldYourName_CaretUpdate(javax.swing.event.CaretEvent caretEvent) 
{
	getJButtonSend().setEnabled( isInputValid() );
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AckUserPanel aAckUserPanel;
		aAckUserPanel = new AckUserPanel();
		frame.setContentPane(aAckUserPanel);
		frame.setSize(aAckUserPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:16:40 PM)
 * @param newNotes java.lang.String
 */
private void setNotes(java.lang.String newNotes) {
	notes = newNotes;
}
/**
 * Insert the method's description here.
 * Creation date: (4/19/2001 4:16:40 PM)
 * @param newUserName java.lang.String
 */
private void setUserName(java.lang.String newUserName) {
	userName = newUserName;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G51F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BD09C471548C69879A76262D20A22D4914BCE381C6E2CB84A05F214C466DCE7654CA53A1CD251B9F61DD2D145CAA2ABCAC53948B115EA96D83E4BC7A081B2F6A433D8A8A200BEAE4BEBAC810B2CC5A28A91D8C295E2B36C8E30303B331A1D65236F3D571F59E119DD891533D50F19FE5D7D7A7D7BF58F247A77E979B916EAC2F272897F3D13C3C86A9F8959B47221DD609264CCABB17C8EGD4127D
	F29EBCD3202F7D11CCEB8559B3D8886D47206D7AD42635865E2B4836CB4B8E43CB0CF50250773DE7FF79D47CF5D2CB45BA85347D19132570AC83C8879CBEF74A387E17CF56F2FC96478B48319032F99447E41D2C673893506E83D88130FA140DDF8F4FA23A72E24789BEEE68D3E9E467FF9C3458100E01260026B1B63717BF7713A7F2F60E61DA3F26FC621CC4E82F85C061F349BF845E07E7FB5F6458C93762EFF1CB01009250E477F80BCF11BCEEF8D795DF4BD8585B621782E149A783E2566168F4CFFE981255
	BEA9A07BE0FEF09C57F87FD4A1258972C0BB1B62C8892E3B865E33G366878E7A93E431BE99D8488FA052C6F9FEC67328CDEFB02E47FA010DB9EC356E4AF13F135B7A26B065CD78B6A28DDB65AD07F5B202FE2A253EA87F0810C83A4833CC67D610725FF00E7670866D47CFEA550998CF679BC6A1F940F6C03F790F2C463388B32DBD1BD04B01B3FFA3259041FF84066951DDA68BC261374E3427E2BAEFC013C7015F31682931D3C105EE6C91BE03A48180868042D5D95F36DCD93F37DF36E5A1708F16DEDC9CAEE
	3E495AEB3EFF48320D53A11311355BA651569A3575FEF84F617E4170B7A9DEB360192FDAA99E5B095037F5D27846E0196025B7EFB929B852EB2A07E5191D16DE9F6341630B66653DD2241D62E3EB96683C1C2178F4831E71721F1497EC2F03BE3BBF534A7C6B45F20C5DE4E8BF863096E0A340CE000AG0C8B889F335BDA6F50477AA4D56B8AFBFD9ED9A5EC6E0B363D7014E7B4D56A8904B4A96016AFCA2AD79A7149A142477C44063C0D4C1977F36A7BEED8630C9C10D5C973AA8120AE79E5CDD6C3EC4CCEDBA5
	0ED1E55ABB60754B3000BFC8845DECDB95BCFDD2C87BB06811B4B9B20275671521CD26240BC0918840BB53CBF1A56A2B9B649F827069FA38D506744E49AAA48C0723D7D1A643418114042C8D201EAF535C11G6F4B82C247565D08A3CA2635D691F47E35EC3701CE37570D6CCB6A2C08312BA5181F1B9561B35B87054FF45B9EA74D5FBFEB6AB334DFE13EB224C447CEA22509FC31F6E94960826338D9B10F31583922F141EB64AEE0AC532FD60536CA845E52G5669BA7EE559FE6633DDD2C896DE7AE7959CCC76
	C4B44E786CAC957B8A2E1F2F737FA70A5F84B4C9B0535A94943A7BEFAA17F17CC2E43CB35DED23EB987979A457E0BAFA299C77A5F98A7643EEB9A887BC709262FD5B4B5177F85C0EAA7A1C1D949F04B8F7C2E005F47CEF4BB196A5CDD33DA3E1CD6E17B54D9B980B74DBAAF02DD0F8A498C991E22D228A14D11906B8574717D5E00C405E9D10293174A93C3F0246286C137D4034D38987B4065FDB09FEBEA94F729C312F928B20A9499746FC436A0555D2894A20FA7D60821832C8C69063B7F82A562023BE40E15D
	73A0BC5302A296279BC4AC5A2FFE1E2C7B3851E23617ACFB3C5BC22E30F8293E9209C546D349EEF35EABE92E60789063F5769D9CEBACD5185F7FF3B5620F83ED9740709561778F6D46FE11DB3843BB951F0F29D1576F552A8A3407FCA5ACC3CA46A4F6CE5759222A5DD1FD97397ED87F12AA541F4F107FCE28283F04D2D18B4EGBFA9G2B2FB09D6E60BAE0633AEDE2DCA234D72811F1EBD56338DD7A383CD026359E20BD44464D0458B82623EF6CE9A366759F56FCE14D8B1AF0AA01D12F6A27BB45D92AC7D66F
	BDCFF3B652F9A6A46A3A1DDD285BECE8D7EB1956E6CD587E3FEC222ECB1DCCA31BD76E0BD943F6EB0C3FF1ADE27B21CAD1577DE68F2E198EFD991A30DBDE35F1EF698C05943717B24BF57E15EA23EFB0DCC6F5B1BCC12C10568341BBA36C734DEA649777BD0E66F540BF52FC0632E6413AF9E150E398710DFB2A8D3C3707597E6501E7AE0BB55BD10CED6AC0AD6C51A72B230A6A17BDCD93521454A2CDEBADB40F34F4526E5378FEC858E7799987BCA73A421A26847AA14FFC5C99C524B7B615778EDD94F55C23D5
	9D1C472CFFD9CE5AEFD5D94C6A6A647257AD6B42CC6FDB43917DDFFF521A5B74722B85CC97FE1B504571A958632704AE725A4CF4715814B8EBCCF85BA332B8311C71ADD09A367E01B231FE86505E212FAF390C6BB7CD33757B2605AD124B3096F3CB2A2C35503F2C3421E618E989CDC352652BBA31179B02716EEABA053C8FC0D67F40AB7BBC171530FA9EAA2A0F1D86C23D71A821B1587E743F73EAF14FF424BE18191EDFAB309A5567C4399F1CE6B52ACD47DF23785586BC5353EFE9ACE2FBA374F54C083AAA29
	86F375AAE82F87588C308B20F6B653FAE2D6445487FB0E115B542DBD10A75400646B1F33B1097AF18F6A9C76925EA6FBD10FF4AFA1E21FFE22B4DA8F8333F3E58FC635593CEF4C1B77C6D4BBB5EA9E33E1F9FF1D48F586C3B99CE719C792F10E9D22B98663E6552C48DFB7BEB0662F3565C7ADA5B17257963EF65E55C87C0C6B675236BA4C4B5D50B78CB083108A10813091E057D5217F7B773EFFBB7DD32F9AD01C00F341FE620C5E6FF5FD05ECF72767C57366C56B3976DA949D2A3F775A8AB90D36EB226EBBD8
	F6F7BC3CBDEFDEB48F63574CEC5D58D4EFEEEBBD02992F4BDC624EA281683033487F51FDAD835AD9BA6E69CE446D00F661F5A1136A44B8EA06368BE09CA081E085409AFD4CA2BAE643F536876F384E76EC5C5FABEE08B144B99CA5934E3F13DAF405E1DDA33EB92A6D5279F9ED5EDA36BBD88721045B24A88A9FFD6EC07DB39FDEDA933BC67EF40DF90D3C22463CC6FE3426126101067124AC7A3FD4933F06FE32462C06FE1D727F6D3D6514F6A8A407E8D0F5CB1A447BDE52769013DA9AD3BF179D4E6025C5FC4C
	E9CDF19E51FDD75A936D3B4741662E9B7C1C894F600D39F6E2B962EB7BF09FCB043EB4008C008DGBBEE085C602A3DEDEEBEBD93A7B7BB6AE25B25334E5CAEE76A50CFC2306B3965E85D1F2F2B557BA2D928C7137D28A6B666921D2FC96A18BC4FF69F51792CAF567C313AD876E5B678124DE803233A8DBA5A22EDD0F1B353EA3F49EB651B73630C517B284CC86F18CE6FF32EF8F7F83DB16959BA70AC6513E6153086B558CD4CA9768EF1E69A06768CC09A401A1B467A7C4B5398F7232A6487F17B03321B245F42
	391BBA455CEAE8B78138G468112EF09FA2727927B4F76CAA332CF54AB021F97CF62B9656C3CFAC674BFD5D7224FBD2FE8F000E1789BFFC3BB1EB556F03A0C0D4E12C89F562A9C7F46B9BCAF1D6DF72BF016427383A548EA362FB90DB55B0EDB2CE62BCE90B55D6F5EB56BEF02FEF66E7C6E85633933BD01750F4373FA02485DB3F4BFCF05F63A0EFB3386F1EB211D25636E2D91B9FE130E8BDDC0DC53A2B8BFAD9271772DFD3163675F771947CF56BE733C763DFD71735A737BE279BD636FD21B102DEF1160B92D
	CD486651F1B9C704ECC19D3746A5F06BG47EA465E8EF11FD3F18F7854BD22F6BFB2BCE7BF458A5D61B823FABDDD52D8AF75EA361F26F6947279897708B3E1A62519816D8DGDB6F91B5554CDB2226DA75056524B7611C6919F07340D18B391745047D5EC8CD3585EAAA36666FBA451A43503F6CDE44B5F7997D867D70928CE801A76B7FE8CEBF6A980EDC067E14B97D486F4B96E3FF661C7EFC43FCA65BF30DC2366C6752096F2B6F46BC6FEE66B265A532E75F4586B2DC1BDD40747E2B51DAE2DA475048E472EF
	F70AF3F8BB50E038379D8227A58A9B0E1DFE5D1CD6A4B20928618A7AC5DD34AAD158716B05E847AD588628DEAC72CC007A1BBFA043E664F537AF46311F27B17792C033BE6EC0B75EAFE9909C890B715B5A51B7D1D3DB687865B55DA63AB15AAF1FBF911E01B9882FF31DEE0277BF12483DA2544F97B0DF4B813CE271904E538561A74DC9904F943718727EACC0876098DF9FB6C4782ABD920F2F7C5B705517444EDCB9BA3D67DC3713B347C0F3A80951EB6038A03427F35898369FDD68383BEFD143F7B96FB38B1E
	474E28291D68BB57135899B5C747FF0E62138DF81607566BF9A8957AF6A509336B89BB464350FD30BFG24G24832C8758F21F484F415770CEABEEFDB42084FBA1017B8CA91749175BF5F75F681EAE90713E6629C7C9C652ABB9314E073B6EE3F1DE1B4C6FD9869A8C355FF30DA81F8B7ABCG49GE9GEBG321205FC992FB5B379CEFB3CDA2726C96EF1FA33A5043B681527A3E8AC9A18EC3FF21A4936197331B3F9FE6D4266B99CE6738A17307175CBE24DAB6D0F274BB613F94CA7DA0B68B724A5515F8B5F25
	7801A5515F8B1FAE945F8BBD5037FE0948B7EF546073E068A037D15A3471BB7A0962F7CC6F1F911A6FE73CF570675A7B9AB80D6F7E55C823E27981CBCE8C9AB3FCEE627D11BD6B7714AF509D097CD84E99E9A23AAF7D28D478D27DBFFD16F47F60151C0698EBE4F05A058FC456303F12DD40644F3800720DCA3ED0642C32220D6A44FD102F7BBF38ED1179FC6DB8457A337193B93767724E487BCA437BF7BA967A2DEA61F967253F6B9A7C6CE30FDD57396C66F55DF93BF9DDB7E00FFF9E9D32477E2663369B3F69
	303C10F5986B0D8E307390C082C0AAC07A837AB7BBBBEE2678B5F9FEBEB86D9943F399FEC156447A9B9BE34B7A43465BFFF75E52087767A38A147AFE24AF70BFEBC4FBE9CA10A2996E970D28A355BBB62E31219C7F6BC674B71FBCCA512CA6AD6E96356BB38F08BA35713428D3B768383FB844387D8F8A5C138EB12ED947B5748B5C7587459D485BC5D807E4696DD3343DD5EFBFDBE866EBB87E1373674D26EBAC649BEEBCE0BE7353E6143D70A1B86FBEA47CA469AD7C97280B52845F95F18BD1D46AA9006CD315
	202CEA338CCB3FDCF1DADB303C92E719379AE351GEFBB8B25651FC7A66090BFA00D693C1437E25D538DFC8C83A4822CGD88D309E6085009DGF98FE7DA6B810E8338G06G02G89G29G698F8BB93E58F4A80E9C7CE0C21DDE153C6876426F8F47717B43F76077076937A470570159A03F1B7869E1BC5BAB2323A1D90F2F6D07ABF4DCCF28CF89F971CE0365541FE3F9426B417653716AC13137C45717E2BFD88B7A28646F0C367BE80476379D37231D9B0776FA20DDC50C352B237FCE68957067C6183F75E1
	C68F5B5970FE984ED053C46CC5A726683DGB3DD543DC03048F83EAADC4A6ECB0FAF95765F5E0CF8BD3FB2D795F9259967FA8320F249673DCABF8576434040986BEFE80D6D83FFE83D3D8F9CEBB54FFD6F364E4FFDA7DB4DF3DFF72BB177B1DCCFAB667DB1705DC1FD5CF99DF7D94775D31A083B6475E863BA7EB2658B71AE999794780FA9DF5394B748F19E836EB247F9DB510FC164F1C595F3832D189BC73DBE9FEB2BF40CB7GEE2D6BE31A72688DCA863E2F6B384B3AEFFE962F51787BB70F633BD95D913B96
	794D5B5862779125B14E3291EFA262AC7B45A2F116F5AD95B9FED5D13C5AB4E1697CDC4F7613FFAB927BC9AA0CE1F7F14F9709BB288C40E581EC83A8CD01FDA8C51C47C931B80F772608BB0A934551F794AB09710EE122B87A0EA2E7CE7F0A6268BB0A3F4C392378CEF1749D05184FF410DFEC54C1A2721592FF0FE3BA2827322C0231389E439D2038E7G37D2371174D6E9A48E292BB2FC7D0CAD02972ECA587F68BE6E7E8952FE1964EBA48F6F90B5E2E5570CA115146B9F07FB2883EE1EBA66487D714ADC8B49
	6BA1D69610BDA48F232A076453206BA1399010B3C14E54A0FF96042E101B4442DB685265F330D62E2CA100D20D94637568FE3C9FF5GBFB4CDB97431C55CB878AB573167B836FE8EDDD4FCBBDF2DD0547B07EE994F943B965746BC17A4BC426C137AC8245EE77B6B1B23287F8D005F760848112A0C1BF62CFD3A5FAF79FC1A641B6C8CF8F04F6E1A45DBDE913F87C711FEC80C611D2C2FE594791EB04C79FFD0CB8788C16733175D90GG3CADGGD0CB818294G94G88G88G51F854ACC16733175D90GG3C
	ADGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9791GGGG
**end of data**/
}
}

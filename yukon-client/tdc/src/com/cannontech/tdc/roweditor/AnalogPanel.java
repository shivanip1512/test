package com.cannontech.tdc.roweditor;

/**
 * Insert the type's description here.
 * Creation date: (3/8/00 11:45:00 AM)
 * @author: 
 */
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.commandevents.ControlCommand;
import com.cannontech.tdc.logbox.MessageBoxFrame;

public class AnalogPanel extends ManualEntryJPanel implements RowEditorDialogListener, java.util.Observer 
{
	public static final double MIN_INPUT_VALUE = -99999.99999;
	public static final double MAX_INPUT_VALUE = 99999.99999;
	private javax.swing.JLabel ivjJLabelPtName = null;
	private javax.swing.JLabel ivjJLabelValue = null;
	private javax.swing.JTextField ivjJTextFieldValue = null;
	private AlarmPanel ivjAlarmPanel = null;
	private javax.swing.JLabel ivjJLabelPointDeviceName = null;
	private javax.swing.JOptionPane optionBox = null;
/**
 * EditDataPanel constructor comment.
 */
private AnalogPanel() {
	super();
	initialize();
}
/**
 * EditDataPanel constructor comment.
 */
public AnalogPanel( EditorDialogData data, com.cannontech.tdc.ObservableRow obsRow, Object currentValue, Signal signalData )
{
	super( data, obsRow, currentValue, signalData );
		
	initialize();
}
/**
 * EditDataPanel constructor comment.
 */
public AnalogPanel(com.cannontech.tdc.roweditor.EditorDialogData data, java.util.Observable obsValue, java.lang.Object currentValue) 
{
	super(data, obsValue, currentValue);
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (7/27/00 5:11:18 PM)
 */
private void destroyObservers() 
{
	getAlarmPanel().deleteObserver();

	if( getObservingData() != null )
		getObservingData().deleteObserver( this );	
}
/**
 * Return the AlarmPanel property value.
 * @return com.cannontech.tdc.roweditor.AlarmPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private AlarmPanel getAlarmPanel() {
	if (ivjAlarmPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Alarms");
			ivjAlarmPanel = new com.cannontech.tdc.roweditor.AlarmPanel();
			ivjAlarmPanel.setName("AlarmPanel");
			ivjAlarmPanel.setBorder(ivjLocalBorder);
			// user code begin {1}

			ivjAlarmPanel.setObservableData( getObservingData() );
			ivjAlarmPanel.setParentPanel( this );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAlarmPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (1/15/2001 11:40:23 AM)
 * @return com.cannontech.tdc.roweditor.EditorDialogData
 */
public EditorDialogData getEditorData() 
{
	return super.getEditorData();
}
/**
 * Return the JLabelPointDeviceName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPointDeviceName() {
	if (ivjJLabelPointDeviceName == null) {
		try {
			ivjJLabelPointDeviceName = new javax.swing.JLabel();
			ivjJLabelPointDeviceName.setName("JLabelPointDeviceName");
			ivjJLabelPointDeviceName.setText("POINT/DEVICE NAME");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPointDeviceName;
}
/**
 * Return the JLabelPtName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPtName() {
	if (ivjJLabelPtName == null) {
		try {
			ivjJLabelPtName = new javax.swing.JLabel();
			ivjJLabelPtName.setName("JLabelPtName");
			ivjJLabelPtName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelPtName.setText("Point");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPtName;
}
/**
 * Return the JLabelValue property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelValue() {
	if (ivjJLabelValue == null) {
		try {
			ivjJLabelValue = new javax.swing.JLabel();
			ivjJLabelValue.setName("JLabelValue");
			ivjJLabelValue.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelValue.setText("Value");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelValue;
}
/**
 * Return the JTextFieldValue property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldValue() {
	if (ivjJTextFieldValue == null) {
		try {
			ivjJTextFieldValue = new javax.swing.JTextField();
			ivjJTextFieldValue.setName("JTextFieldValue");
			// user code begin {1}
			
			ivjJTextFieldValue.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument
						( MIN_INPUT_VALUE, MAX_INPUT_VALUE ) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldValue;
}
/**
 * Insert the method's description here.
 * Creation date: (11/14/2001 2:39:29 PM)
 * @return java.lang.String
 */
public String getPanelTitle()
{
	if( getEditorData() != null )
	{
		return com.cannontech.database.data.point.PointTypes.getType(getEditorData().getPointType()) + " Point Change";
	}
	else
		return "Point Manual Entry";
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION AnalogPanel() ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("EditDataPanel");
		setPreferredSize(new java.awt.Dimension(417, 169));
		setLayout(new java.awt.GridBagLayout());
		setSize(455, 183);

		java.awt.GridBagConstraints constraintsJLabelPtName = new java.awt.GridBagConstraints();
		constraintsJLabelPtName.gridx = 0; constraintsJLabelPtName.gridy = 0;
		constraintsJLabelPtName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelPtName.ipadx = 6;
		constraintsJLabelPtName.insets = new java.awt.Insets(16, 11, 4, 2);
		add(getJLabelPtName(), constraintsJLabelPtName);

		java.awt.GridBagConstraints constraintsJLabelValue = new java.awt.GridBagConstraints();
		constraintsJLabelValue.gridx = 0; constraintsJLabelValue.gridy = 1;
		constraintsJLabelValue.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelValue.ipadx = 3;
		constraintsJLabelValue.insets = new java.awt.Insets(9, 11, 5, 2);
		add(getJLabelValue(), constraintsJLabelValue);

		java.awt.GridBagConstraints constraintsJTextFieldValue = new java.awt.GridBagConstraints();
		constraintsJTextFieldValue.gridx = 1; constraintsJTextFieldValue.gridy = 1;
		constraintsJTextFieldValue.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJTextFieldValue.weightx = 1.0;
		constraintsJTextFieldValue.ipadx = 186;
		constraintsJTextFieldValue.insets = new java.awt.Insets(5, 2, 5, 21);
		add(getJTextFieldValue(), constraintsJTextFieldValue);

		java.awt.GridBagConstraints constraintsAlarmPanel = new java.awt.GridBagConstraints();
		constraintsAlarmPanel.gridx = 0; constraintsAlarmPanel.gridy = 2;
		constraintsAlarmPanel.gridwidth = 3;
		constraintsAlarmPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAlarmPanel.weightx = 1.0;
		constraintsAlarmPanel.weighty = 1.0;
		constraintsAlarmPanel.ipadx = -162;
		constraintsAlarmPanel.ipady = -15;
		constraintsAlarmPanel.insets = new java.awt.Insets(5, 7, 0, 0);
		add(getAlarmPanel(), constraintsAlarmPanel);

		java.awt.GridBagConstraints constraintsJLabelPointDeviceName = new java.awt.GridBagConstraints();
		constraintsJLabelPointDeviceName.gridx = 1; constraintsJLabelPointDeviceName.gridy = 0;
		constraintsJLabelPointDeviceName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabelPointDeviceName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelPointDeviceName.ipadx = 92;
		constraintsJLabelPointDeviceName.insets = new java.awt.Insets(16, 2, 4, 4);
		add(getJLabelPointDeviceName(), constraintsJLabelPointDeviceName);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initReadOnlyData();

	if( getObservingData() != null )
		getObservingData().addObserver( this );

	try 
	{
		Double.parseDouble( getStartingValue().toString() ); // if not a float, goto catch

		getJTextFieldValue().setText( getStartingValue().toString() );
	}
	catch( NumberFormatException ex )
	{
		getJTextFieldValue().setText( "0.0" );
	}	
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 12:19:47 PM)
 */
private void initReadOnlyData() 
{
	getJLabelPointDeviceName().setText( getEditorData().getDeviceName().toString() + " / " + getEditorData().getPointName() );

	synchronized( getAlarmPanel() )
	{
		if( !isRowAlarmed() )
		{
			getAlarmPanel().setPointID( getEditorData().getPointID() );
			getAlarmPanel().setVisible( false );
		}
		else
		{
			getAlarmPanel().getJLabelDescText().setText( getSignal().getDescription() );
			getAlarmPanel().getJLabelUserText().setText( getSignal().getUserName() );
			getAlarmPanel().setPointID( getEditorData().getPointID() );
		}
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonCancelAction_actionPerformed(java.util.EventObject newEvent) 
{	
	destroyObservers();
}
/**
 * Comment
 */
public void jButtonControl_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	// ask for verification from the user first
	int retValue = optionBox.showConfirmDialog(this, "Execute the Control?",
									  "Verification", javax.swing.JOptionPane.YES_NO_OPTION);

	if( retValue == javax.swing.JOptionPane.YES_OPTION )
	{
		javax.swing.JButton buttonPressed = (javax.swing.JButton)actionEvent.getSource();

		long devID = getEditorData().getDeviceID();
		long ptID = getEditorData().getPointID();
		
		if( buttonPressed.getName().equalsIgnoreCase("JButtonRawState1") )
			 ControlCommand.send( devID, ptID, ControlCommand.CONTROL_OPENED );
		else // must be the button named JButtonRawState2
			 ControlCommand.send( devID, ptID, ControlCommand.CONTROL_CLOSED );
	}

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonSendAction_actionPerformed(java.util.EventObject newEvent) 
{
	try
	{
		if( !getJTextFieldValue().getText().equals("") )
		{
			// Create new point Here
			PointData pt = new PointData();
			pt.setId( getEditorData().getPointID() );
			pt.setTags( getEditorData().getTags() );
			pt.setTimeStamp( new java.util.Date() );
			pt.setTime( new java.util.Date() );
			pt.setType( getEditorData().getPointType() );
			pt.setValue( Double.parseDouble( getJTextFieldValue().getText() ) );
			pt.setQuality( com.cannontech.database.data.point.PointQualities.MANUAL_QUALITY );		
			pt.setStr("Manual change occured from " + com.cannontech.common.util.CtiUtilities.getUserName() + " using TDC");
			pt.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );

			// now send the point data	
			SendData.getInstance().sendPointData( pt );
		}

	}
	finally
	{
		destroyObservers();
	}
}
/**
 * Comment
 */
public void jTextFieldValue_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 5:06:20 PM)
 */
public void update( java.util.Observable originator, Object newValue ) 
{
	if( newValue instanceof ObservedPointDataChange )
	{
		ObservedPointDataChange value = (ObservedPointDataChange)newValue;

		if( value.getType() == ObservedPointDataChange.POINT_VALUE_TYPE && value.getPointID() == getEditorData().getPointID() )
		{
			getJTextFieldValue().setText( value.getValue() );
			
			getAlarmPanel().setVisible( value.isAlarming() );

			this.revalidate();
			this.repaint();
		}
	}		
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF5F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BAEDF49457F54266ABC0DBF048B9549F8D584416EBDA13961F288976A1AAEEF1A12DFBAAEA4765641062136A071D23B61CD35B25616D2EF6A5218F0490C6A2B28881B6484ACA829C30AC92C98801C0462AD94B32CDEA0D34335A9133B32B59D93492125DFB5F476CE8591530CF2543E5665D7B5E3B9F6F3EFB6FFB33247C50F23743D9CE0843CD785F9987A11B6A88B9382E7E578217A6E7FA086D6FB0
	409E7274E0AEBC97834DDC1269A9A569DD7950DE896D330BB2BD7B61FDAF79332E79EF42CB8ABEFDC07B6D938D2FCF4DE733CB7049236D2A22A2F816G24838E1F5BE564FF3A280263B7F13CG071310E78358670322AA0EBB8B6DEE00C5G4B832CFF86BC8BA867B9CDE7F93F6BCB16136DFF7716339867314DA920E2100DED664FEA72386345C1649523F26298935A2BGD0F997A9F3FB31FF770D415F8E68A16F0024E93AE64A83C12F699F709A7A086CD7CC5DE8EAEA55A4D59F6C16B4D985DB85F14EF5C579
	F48EC20EC37BA50ABBB406FCD6427BDA00AC8B7FB30AEFD0B2BD17G7A94215B39371A396C3D379EA7571F53DC3BD36896D318CE4B1538EECB232F65D552F528F6233D37G0D8CE5FA4A814E82748244GFEC5573FAA37961E2D7D66F9BD94523556F038CD759B6F6BFED905F7D0315F64388F65815D70934256780B223AA472CC85EC5C222FBC0E59A47D14D86FD59FBECC367C6595E7DE921BECC9EFF0AE9EE236C89F0A5B0471FEBAA56F8DC3137DF1B26F0F091DF757DC5D65CE42FB4D66934EADFC1E9C9B6F
	6A9B38565F24EBDD8A6F8E6E9F8C7FG457BEDF8661B47A89E5BA6501E3FA1FC2337C4485215FBAFA9ED6CCAEA07E5192D4EE6154960D393E57974964E1D26B21E8ECB16810ADFE443B3D9DE9A937BE49950F6GB07FBA1107FB35AF14699987D88A308AE093C0B6400E107031F0EE7D9D7AD837E418EDD1C5754B86E1E3CF38B17848B15310BA3508A9E983F20FE4A8D23FAAC7886FF3520DB6681FB46E8C757D8160F1D156E4C3B295DD03592510EC4AC60475E9F16F41BE06CC29BEA5A48303D01808F9AF3977
	42D315A266C7E13FE44A719E0C5E6D46B539A975400CB0813CB33B7464233D2A34CC4FE900F6CD58E1A31D6F0AECC03CE8EA6A52759B51308FB5A1CBB53473969ABBFA61BD2C899BFFD8003867211DE34D6374145966E9D786D0FC499895FB6C0F47B09E676A42E7FE5EABFC263D78BB24EE736524BEC3FDC4E73EF2D6CF5CBB8F51B9D1AE56FEFB2C77AB6E63BB01DC4E1771E81F919EAE7EE476DD378B572A8F780E835C2F8B9B1F73D4B31FED13A23270528F8CF0B0599F37B813336F9631E4D6EC6F7FB1A372
	CF0E270537E61287334529BC4CBF72CDD9B35B65302C7961A542E9CDF968E3FCFF86F4EBCC8B4507E1BF1F078D9431709DF93867A453B4147E28A9DF13CDD3518663742B14D7A45A9F0E0782416B23BC0C3B7A886CE72B7F27F9389786F4CD136922D8C3B87DBA5D0B32AA07C06873FAD4B3993EBF9F7D7906BC4AE3C1A09F8B1B1B129A4538426A0055D2A16AE0A8A1D8EA8CCD24BA0C7B7497D69DC363BE788C56B35B61199F96FBEE640858F3C747D610F51F9FF3A64B994B3E536E8C06194F0F0763FB0E4974
	F1C1F259FFCFE38247C7182C35EDD87FDC004224DDB2A5DE12F09FFF359067399F66DF8D30A1ACFC7C4785C897F104BB77F9DDD519A9AD9B7F4D2ED2DC93F9B8AAC3784500F545325B0FF615A550FA388D99FD73AE3421EA0BF5CC5E7C77CF13BB2A315E17342824DE50CCE374B2D50B55D93FAF4E67FBE0EBD854D34BF45433E2B85353GF0E9D82C45BF960BFAEA690D65E46BC387D2560AFD43EC8DE68F5BF2665D221EBACFF92E87DA563030E1F721BD2637C6A27A00C2538957FFE721FD2D98AE37E897BC83
	0A91B1BBE1B345042DF695213C18EFB81A6F717EE2D137E4F339F68C476B15D263765A0565734CB05A63284172F92105FF16625BEDF816677F045A8F5BFE20EDB0C48EDAD8027EDE97811B82748144G1681E4C4049D6EA9BAC52649711D303E86D44C57A6EDEE11EB0BDDECBF72764745EE319FF15DE8FE9C25764F821EB9G8CB7DFC5DC2E196929B085CE5690578CED1FA9725506B09CEEC88C5A0B8152815683EC31687FCC69A41A692986E8816883189D957415FA2AB80B73A443AFCE40275B5AB14EFBF2AC
	FFB2A50F0294F8F7C2BB4F766E62EBB9B916AFADC99D4B6FABC99E4BD714A40F650F146CE1F81843DE39897A63A5D3477A6F15A40B75AECADBDC0CFBB4EC4881590028F4CD99C3BF58924575FD147A41CAF8DF8D407AFFDB75503DB32AC7CD28E1EFE23F721008535550BE8B5087E05EE475950BC35F9DC19B8520B084512E056581929C4131631A981B857D33817281FF9C60520860D964457DF239CB6A1755EE73AA54168C7F583B05961EC6C32E53FF97223E17FDE0618EC5D67DF6DA64964604D6D5B2C2BCF8
	B33C7E793EB88FDD41D53B298C48488C63811535F0385C364E2BC718FED970E474F93EE4740618201F095A696DB1C6D76149725D434D76FC174669EB61792CB5C7EDCDB29E59C05F9143BB03C7D08D2E5304DA8DCFCD9E299E12EECADEE9447436C3E92DE191FD7CF463193A6A465F3CFB02456E65A376F37E59DDE71266D10416D1FE5E614F35730E7038569C926717C5C0DBBFAA563E318F4FAFB48789C11A1AAE9A0A3FCD9A6C221E44F24D6CD0BE9F3FE3D4641A3F20F31E9E4B74F4817847C42E093DA9F24D
	2A076FA5DDE9D712661A2D3E134E71B1FEF69F0B671AF4B83BB31E8FEA02679620175F22F1DB335B98ED29C387AFBC995D295B6938774E90BB7D74A4BA4A7BC7AEBBBDB0090EB6BA4747B35DB69E933A6D5818CE54A75ECD19C72F5FE2BA2571E7F74F91F2F441FF65B13B7FA2D0E137FB4C9B99014D5754443C1C695FB6AC72FB46ADE15B098EFB9EDF3B7B2433B0857F9C4E37F4BCEE5BBA4B36CF79445C5D6342970E9D2FCAAE1335DB59D8ADAA6EF766DBE36FA73FA6F7F6DFE45B5E7C3E68A74D795C566B46
	05EF4D69C55FFA855A259390F3GD8DF29C674ED1990B679074F6C7B23745E43CEC78A1B44A6F8EDB39137894B6FCF6E47F9B3G1FB5A17279CB7B5E06E70FB444B379852A82CD65006CB67430EC18238C4B62984BE1ED212FF7AF547DB547FD63AB0FE3B5501B9A5AF87BC4629DC8BD4513AF926FC0B22C98D28434709722B67AF9857AF29634F3G723F0418895082F07DCBE14BF7DEEFA05354C6BEBD5C85C9D93525D1265FA35A573BAF7BE11E58ABEB3E7FA049187B2AA355DE89FF497CE1B1FFE67B0ED0BC
	53EF568154EF9D5036G10B42833G6AG5A5304FE932F57B17D509DDACDD39A8852EAD7A8572348A3F1B4A6D9265B45E13BEE3DE90C3F4A1FE9E93757D5CC26EF17E11CDA8A7455G9BG368210D9C21E3FFDFD5ADA74C2ACE9AD4A647AEB1FDD2E6AD9CC0E537CF9E9566DF231F11168D4EBF5BD6538ED4D76F16104FE33130CE33E3A75B07A6432D909BE3C1D62D74ECA7461053D4207D7832D7CAED15B86EA12499D3B0B71DDF4D72AF5F88A0FBE3C165B3D7F98C991AF405C17A124FE50BF8467CA1FB4E80D
	495C0FF3D975A18BA31C764CFEDCE3C5D3CCC5D215B1FA243A869D35C1A6F31DC6AC7B3DE3FBFFAAB473F7AE4F4DA88F267FFFD6242E53FFD9113CCE4F2DCCDE276F2A1C3A8EAF2BCCFD67D2D9E93FF3E16B3E319C57E6B97845DA002DG5B81726F967B6F114A9AC277DF12D8F041BFA81F5769CD2EA978AFB810DA57A587263F7F3D77G564E7D3AE96AA11CDF60FDFBF03DCCBDCC118C77499E3411218C86CD561563BF5F03B9D01583944D4E117FCBEB10232057693B455972FB79083B846DDE8BD7D5A04E1B
	7E388D4AD26B7467E54957EFCDD972757BFE5954672C4C32D46B4B64FB41A774D8E349DCDBAB7458E06196568ABD32AC5C0228409D1DADF02F511A450476FC8B576F907DD64D96F1655F8F477765EB3677A53DB33FC77FF4C6F9B01DA7E8BD368374AD1AA36C702FB5428E95F304AF7CD3F9EADFF821FCFA7F5ED6CE6F1040AFFD23E11961324B713C2B878291595AC7AF1717DA384EC8379ED1B0EA32B3D7F4986FBB83D01732B374C26A5BC110511CA34E33FFCF6FC4CD90061D596E265FFD574F417E278A5A27
	300748B3AE8EABE18C42B10E4F00F7F1D6E07CDD9FA17F2239E02739620C785D7DF1FE8F56CE474FB5853FCBF3198FBA2C79D6C4271A4F15G3E3913678BC2BB6A08735DCB761CC37B776B865D086C3E6178B9F1CEB6217FA20075G4F4FE5F7C9CC0ED147F4FA39398FA31F9C981BBBAF53330F37AB607D49DF6E5C39ED674E1DCC0E25B513576C2795F19B3EDAB31D4E29568CE5B88A3C7E0A77E1F3D79DA4167E0F65E30C1FF4066FG67BADE63D7701BF8CDDDF5839C63CF1EA064906DCF882E3778E37375A0
	09572BBFAD9275EA55E377117667DEF59CA1496B5506F93CBE1C97BF47E48F6D48E3F3DEBC9A1771E06D1D4A48468E216AC9644D799D237F817ED20257D25D674477598C10A3E71EB88B7E5B9BB0DD523360E0D471C6CDC5EDEA72A926AA37F157616740E56744596E78FCF18EFC1C50EFD3500E81AC86C8A6DBEAF9F35E0A5BFAAF0A457337036E2DF418EFBDC0B6G79865403GF5GADGBE00A0C09A40B200D5G6BG32GDE84F88520E80108EF779D4249D31D9579058ACD7A0624E05A67B13A675CF4E71B
	6A8513EBCFB64EBF6D380389EDB66E81BA2EF9C1727B5AC46806FE2C1EBE62205F6897A45659F5949FDE10D8E7BFC1C41DBD8E342D161D7655264E83D535536701E335496B1C37EBEF2FF39AEA1357B93F293557B98C77DEAD7A5DA02CD32F552F4542FDE261AE50B991773162B703967E8A158B7117E4E4A8707FC3659A2138DE0E6B3161BE61384FEAB116024AC15D90E37BEAE93ED2D415357D340F22C1EA336C21D0991530E413DB33F0A8779BF4CF2E5BDBCC472A0A8635A77DC5G91B9FC8D5DF76D8B613C
	3ED06470B3F4AF0521BDFB2128E5CE60F2F2BFBAE9FB7F811979DAA6EBC6F930D839719C2E2D091189E3A491F9FA9B354FE230437D8BC51DF01546A95A170878120D57C57C7DB3AF31662DA59804D246E0EF3F2E7AA190B7423F53A7887BCD5AD9598B8CD63DCC5C9103145DFEC75C7A07BFFEEE55676D65CE626CA41EA07525CE629EC1270C614FB79E055D3742A4EEFAE18ECFFA32B4491E6E8FBA2F7A6A5BAF74F41E3FD0FD35757D8BB0FF71A4391EDE714E4FB67D24A762A21ECED2096552B55920A7D0590F
	9FD679CFBDBAC8C19867464F921DA4B7C29F6E08B92A4ADD24BC70A1FE8E0D18323FD5546491136C6BC2CE7518826AAFC9112009FB7B3DAE122B610FD2C899A32B12B6D84F8E3A6FD1DD9C2DADE8CD9AD288483B174A0B33F82DD9767EE077314317DF4CF091E78759256A123FC39A00905CC98A4C20A1C702E064D878F7EC227C91DCD13C8DF241F43F3254C7E53DF85BE989FDA3764022626C628EA7A1B10354531ED8E9F82D9E7E73C149GD63251E9620FECF46328C51BF550FC73C79777DFF79287D7F218FC
	C172E2E113F882D2C8D1C761251DFAC184A905A6C8EBC8DAA4209BA1102CBAC1B2C13298F83F79CC43379EFD6DD34AA0BE54A40718A9E1E87D202C076ADB838158795684E0D7187E8DEBFA6C63CD68D396DB326F72661D7D60876D4495EAE8207FB7527FCF627F0D9453C8B10DA74138B9E44A3FC4FACC68CC1D876459CD65416577F6408A1AAF3C7C4C037F521C7392E857C15C74C3A8ACC2E93C5755E828DFB6662E7A569B05CB37AE44B58A232B44F05E5DB1EBC6543013064D2DB7CFB53936BD64F212DC58B6
	AC0D0073E2AEG67B558AEF2C19A21FE174B1D84A6A625D4D877ED5E0858025B5D8A51455662E20BCDD0C67F5F7715CD0523765331151606554C00F8E96F36C9DAB0890B98AC1D5D96967F7278B30E4D65FD71D98A0253FB7A08CD69BEBA00D668A9144E1D2C34D8DD8F4BF4CCF89C737FEB0DBE5EFEBBA66A6893DF5A3F873CB227A26577320CA52C8E5A34A471FB405AA0EA15FF8F1C796F9135CBD1903FF1243A5F3B9612D45514549B2D1A9F6F7A5AC6CDB9FEFF7143A04E9F91FDB81151B684D16EA15B187F
	83D0CB87882F469C6D8791GG74ACGGD0CB818294G94G88G88GF5F954AC2F469C6D8791GG74ACGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC191GGGG
**end of data**/
}
}

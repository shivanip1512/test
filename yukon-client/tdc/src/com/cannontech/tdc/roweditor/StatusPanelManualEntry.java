package com.cannontech.tdc.roweditor;

/**
 * Insert the type's description here.
 * Creation date: (3/8/00 11:45:00 AM)
 * @author: 
 */
import com.cannontech.tdc.PointValues;
import com.cannontech.message.dispatch.message.PointData;
import java.util.Observer;
import com.cannontech.tdc.logbox.MessageBoxFrame;
import com.cannontech.tdc.TDCMainFrame;
import com.cannontech.tdc.utils.TDCDefines;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.message.dispatch.message.Signal;
import com.cannontech.tdc.commandevents.ControlCommand;

public class StatusPanelManualEntry extends ManualEntryJPanel implements RowEditorDialogListener, java.util.Observer 
{
	private javax.swing.JComboBox ivjJComboBoxValues = null;
	private javax.swing.JLabel ivjJLabelPtName = null;
	private AlarmPanel ivjAlarmPanel = null;
	private javax.swing.JLabel ivjJLabelPointDeviceName = null;
	private javax.swing.JOptionPane optionBox = null;
	private javax.swing.JLabel ivjJLabelValue = null;
/**
 * EditDataPanel constructor comment.
 */
private StatusPanelManualEntry() {
	super();
	initialize();
}
/**
 * EditDataPanel constructor comment.
 */
public StatusPanelManualEntry( EditorDialogData data, com.cannontech.tdc.ObservableRow obsValue, Object currentValue, Signal signalData ) 
{
	super( data, obsValue, currentValue, signalData );

	if( data.getPointType() != com.cannontech.database.data.point.PointTypes.STATUS_POINT )
		throw new IllegalArgumentException(
			"An instance of " + this.getClass().getName() + " can only be created with a status point.");

	initialize();
}
/**
 * EditDataPanel constructor comment.
 */
public StatusPanelManualEntry(com.cannontech.tdc.roweditor.EditorDialogData data, java.util.Observable obsValue, java.lang.Object currentValue) {
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
			ivjLocalBorder.setTitle("Alarm Panel");
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
 * Creation date: (1/9/2001 3:37:40 PM)
 * @return com.cannontech.tdc.roweditor.EditorDialogData
 */
public EditorDialogData getEditorData() 
{
	return super.getEditorData();
}
/**
 * Return the JComboBoxValues property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxValues() {
	if (ivjJComboBoxValues == null) {
		try {
			ivjJComboBoxValues = new javax.swing.JComboBox();
			ivjJComboBoxValues.setName("JComboBoxValues");
			ivjJComboBoxValues.setBackground(java.awt.Color.white);
			ivjJComboBoxValues.setVisible(true);
			ivjJComboBoxValues.setMaximumSize(new java.awt.Dimension(32767, 30));
			ivjJComboBoxValues.setPreferredSize(new java.awt.Dimension(130, 30));
			ivjJComboBoxValues.setEnabled(true);
			ivjJComboBoxValues.setMinimumSize(new java.awt.Dimension(126, 30));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxValues;
}
/**
 * Return the JLabelPoint property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPointDeviceName() {
	if (ivjJLabelPointDeviceName == null) {
		try {
			ivjJLabelPointDeviceName = new javax.swing.JLabel();
			ivjJLabelPointDeviceName.setName("JLabelPointDeviceName");
			ivjJLabelPointDeviceName.setFont(new java.awt.Font("Arial", 1, 12));
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
			ivjJLabelPtName.setText("Point:");
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
			ivjJLabelValue.setText("Current State:");
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
		return "Status Point Manual Entry";
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION StatusPanel() ---------");
	exception.printStackTrace(System.out);

	TDCMainFrame.messageLog.addMessage(exception.toString() + " in : " + this.getClass(), MessageBoxFrame.ERROR_MSG );
}
/**
 * Insert the method's description here.
 * Creation date: (3/10/00 12:19:47 PM)
 */
private void initData() 
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

	
	// this inits the JComboBox for Manual Entry
	String[] states = getEditorData().getAllStates();
	for( int i = 0; i < states.length; i++ )
		getJComboBoxValues().addItem( states[i] );
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
		setPreferredSize(new java.awt.Dimension(455, 163));
		setLayout(new java.awt.GridBagLayout());
		setSize(455, 183);
		setMinimumSize(new java.awt.Dimension(455, 163));

		java.awt.GridBagConstraints constraintsJLabelPtName = new java.awt.GridBagConstraints();
		constraintsJLabelPtName.gridx = 0; constraintsJLabelPtName.gridy = 0;
		constraintsJLabelPtName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelPtName.ipadx = 3;
		constraintsJLabelPtName.insets = new java.awt.Insets(16, 11, 4, 5);
		add(getJLabelPtName(), constraintsJLabelPtName);

		java.awt.GridBagConstraints constraintsJComboBoxValues = new java.awt.GridBagConstraints();
		constraintsJComboBoxValues.gridx = 1; constraintsJComboBoxValues.gridy = 1;
		constraintsJComboBoxValues.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxValues.weightx = 1.0;
		constraintsJComboBoxValues.ipadx = 55;
		constraintsJComboBoxValues.ipady = -6;
		constraintsJComboBoxValues.insets = new java.awt.Insets(5, 4, 2, 32);
		add(getJComboBoxValues(), constraintsJComboBoxValues);

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
		constraintsJLabelPointDeviceName.ipadx = 96;
		constraintsJLabelPointDeviceName.insets = new java.awt.Insets(16, 4, 4, 2);
		add(getJLabelPointDeviceName(), constraintsJLabelPointDeviceName);

		java.awt.GridBagConstraints constraintsJLabelValue = new java.awt.GridBagConstraints();
		constraintsJLabelValue.gridx = 0; constraintsJLabelValue.gridy = 1;
		constraintsJLabelValue.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelValue.ipadx = 2;
		constraintsJLabelValue.insets = new java.awt.Insets(7, 10, 7, 4);
		add(getJLabelValue(), constraintsJLabelValue);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initData();

	if( getObservingData() != null )
		getObservingData().addObserver( this );

	getJComboBoxValues().setSelectedItem( getStartingValue() );
	
	// user code end
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
 * Insert the method's description here.
 * Creation date: (3/10/00 3:25:00 PM)
 * @param newEvent java.util.EventObject
 */
public void JButtonSendAction_actionPerformed(java.util.EventObject newEvent) 
{
	try
	{
		// Create new point Here
		PointData pt = new PointData();
		pt.setId( getEditorData().getPointID() );
		pt.setTags( getEditorData().getTags() );
		pt.setTimeStamp( new java.util.Date() );
		pt.setTime( new java.util.Date() );
		pt.setType( getEditorData().getPointType() );
		pt.setValue( (double)getJComboBoxValues().getSelectedIndex() );
		pt.setQuality( com.cannontech.database.data.point.PointQualities.MANUAL_QUALITY );		
		pt.setStr("Manual change occured from " + com.cannontech.common.util.CtiUtilities.getUserName() + " using TDC");
		pt.setUserName( com.cannontech.common.util.CtiUtilities.getUserName() );
		
		// Now send the new pointData
		SendData.getInstance().sendPointData( pt );
	}
	finally
	{	
		destroyObservers();
	}
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
			getJComboBoxValues().setSelectedItem( value.getValue() );

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
	D0CB838494G88G88G66F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BD0D4D716FE0892A55154BA8915F126C8B4D9E6E3B6EE0DBBEB6D1AA907CACAE76356BAB326F4A7CC56293033EC56E4590D29B50935DC1A0F202DC15111C4E3BA2223E699C4C105564D8834C41098A63408CA999F7423F9766BFE4D6B57527841BD67FEDEBF1AEEB029112A43FB775CFB4F6F1EF36E3977B5597EF1DAE11EEDBBA1F905047F1D48A3247AA8A10F561476885C84B92308D87EBE8158
	CAFE58D2G4F1950376A510C22D272737AE2E84F05766A9999C5BB613D0C2C2C2F7E94DE9270A9463ED9EF7CF8ECBE439D024F865ADE56E007679600F44061F32D0C7C339A4AB9FE99478B483391125907E3F29BF6F3DC9D345B81D28166753171AF403304F2FE40D94747F5BF12C6567E7D815BE624E321A9E01F174DBD4B1F8E72DCDE0E97F99DEC3473B993203D90G154FA7EBCE564373D43B4F7BD92F662F6A158281ADE0483D7DD5062737CA5786E50FE2E83A5359E1C8C6B854AE85E4758BA9901654B381
	C39F0239EABF528FB696D3FA0454C2FB9D4575F5A14F79703E94E035097F51DE441FD2B20A5CG2AA2747C3F8327389EDD371EA36BD786725FCF20E71242741B27C4751C95FEF74387F4CDFE6CC65BE7C31F7DFAC6118340856081C88258C5FD61458EDC6E069E23C9737B35C0C3B05828FA74AFB40F2C423B5389BD9C77155C2B699EC258FAD7B5D64611E7ACE073CEFD63F94CA669B5E26D67FD75B4497C61B95B06B8B649CC2F364D3E4EEC3160FA54A60CF7B8A16F9557C77A66C85E970915771AA9DAFEE19C
	5E8BDEBEE44B66F456DBF8D77AF02DFFC9FDE0B73C67F17FE078AC0A8FDA704CCFD50A477684684B72895F685AA2E4E9ABF81C149EE90BEB07540C865BD91549F0CD0D1565538A241D22B21EF926ACB5941FEA41B3D9966D95B1B3977A0A7D99C54C3F16F4E25C2A50CE86C883D888308AE0ADC0F1C07858967761FB7431F6C9B79A430A6A11F54266AEF17F961EF244502516C04810823DF22724ABD20FAA07889F335C0DB66899B16FE56A7B3D4063F3B9A06B1221E88120AE79E5C356C3EC4C2B6E2DB8C617E9
	2FDB714B40409FA4026EEF5CE5281F94B2AE84BD12A1C7C7307EB75D38A6B724CE208884601D59E56DC53457C1507FB4C03BE987658252BBA76B10BB1C4EB6CD730503EE54042485504EA9B4F7F8607DF6C058B8278BF1D950DEEF52C93B304DC2472574227892A6A166672B3BB0B717EA42E75E6C92BE635A7C9429FC39B52E4FA0D4EA4CD7DCDAEC6C3CCEE922DC2C2DF7F5FD43B83E97A865FC990F697705C7BEFFB27BDE3D0CEB25825FE400F4CD5878158B8E66330DD2C896DE7A258E8EA6FB2296E7F2D6D7
	90D366FA4BFB465E7BA17F48FC7A4AFD6541EC7133CE5CFF649BF240F049C1B9600117906FDB5509BE466333CFB3673CCA71C10867A6882010094F6A4418138CC3D7FA42065CA19B069270C67B57D0DE21F0CFB01A8A842F37BAB16FEA03904F6678F51D988B3DDAA0A053C5B127707EF7E9AC4A2A6C87211B34F040E0F8C27D59A78F71DCD05005C54E8DC98DE3DEE1B541FCE9936A20ABFED8EACCCD24B208F1BA27315CE2234560B3D85B90685B9E94B1B7D8A1E2EE5F4DA748622F775B626D9929CF39ECC610
	79FCF2B09AF3CC26373B624B7E9E4D899C9FE232FE5A08355099A8D2DC12A15172447471472EA01DF4203F88E0C5D0787860A56C97F904BBF713262A4C14260DBD17CBF1CD6401308C69979356B953EE3E4B5BE27ABA398DD9FF6872A69ADF51DC4764AD7E22165CD33DE5A932DA29DA2C4E5A5C04B6C0DB2F890AFA6A1D9328673E010C22D3G6E813196BF5BAC6A29D93EB432FA4E07896BC6F5002D414C0168DAB8A60BFA6A575D48F3B9742D9E90B6FC7332B527B704C2DA2FC237932E7F772ED8570A611E3C
	32911EFD0A9EB2DAA018A242D669D7D0DE5CEFB81A477836A6D1372C65F2956B51FAE5B73CD76BA36B97361F7B6B509EAE1D6D671BCC7CAD0A77D870EC1F4F257643F69274E56BE28F5AF3957D3DAD14D1548FB081A095E0A1402A1030C3F5F78D99E70FEF01755583125AB1A23845DE5B1D4F621137DF6FAE947188CF36BF9E22EB3E96F8169AD0EFD05C33F5085B876DBA93B74D0938EEE89BA66E191308CB0576FCC36CE121BAB87C1097201D89308E20B41CD1D49B967D93E9FF9B34D500940079G4B4D7E42
	3AC43997E94543A36DD8DCA69F7B8267BFB23F6FC948E3C3827C26B838C2925D7BCAACF8E66B83D79367772A2B7173FB6D5578795DF9F5AB43838DEBB5A77A9B2F0E1D7FDB2F464B7F79346FF49346EDD0177BE49DB2D507F293FD63608DDC5FCB54B7F240FE6B815878976A0AE8BC8DE9E10310D5F4DCFD3D485DAA3413G6681AC3E41EAAE161B7EEA90ED588745A208569114FB4902C11C7B04D34CF5C03B96408DE0G4C9A94BCB3CE62DC18287843FEAAA113EF66A914EFBD0CAB0E083176AA0C3756B629C7
	D65B0D73D01BB2FC288CED508A115423B5EA911ED0D95F729B18BB9AD4C97773A44F70879CBB2234B485D7720652ABA3D1A246D45E59E40E21C4099A2167D06A67260FC618AD3A60497A0DF2EB7F825E1F89CF369FA657DA77439C5EDFB984756F10203167E2DC9EE67F010AF87DAA74CF98427B07B32886576349BA87BC837220633AF4C32A12860DAA97146681AC428F569EBBD669B8F23C66904B7DE90356BB033A0DA7626E43B47E06D86C2D624FC5D3ABF8DE146A45792774E6C651591B42CF0EDC43738F5D
	4304A0CE67673A62E9143CED546B58DE7583FA87C36F9CEE0A3DEAA52539905A4B8132EF0A3DAA72BB31D74DFB7AF152B661DC5C3DEA357B280D5CE2F26E3B955D2B52616C4FF8AEF18A1E5720BF7596625EF6DAED0C3654E0C095BCD97F011356FE0C53E3445AFFE9C4BF4ABBA35F5ABFFDC4BF5A28014FE73A3D34DF68367E25F4227EF5FD42FDF8B5572960B6FB36F7D610FD535E5B40FCC176C6FDA173443D7882DB2F377A4A2D6B95298A8DC200BB1DACA2185D5E207B896656D337451A5CE936568F0B5EBF
	EA5B14C0EE034BBB6B36654C8DEB4278FB55DD71790BC867F7C84F14238C1883D65C963EB61B7C1E5CD33DE4CD8D1C5E625AE23E9EF5F7047F79BA693D8334138152G58588B97057E8BCD5C558A014B39A3EC720FD72CF1D57A78A7363C84B6B9B84CECD1BB9C35892BAB6674A35DFE40A78D0BBAE21A7C3F706C142E738A628CD50B169000EC5735202C9BC38C4BF2985BA73D755F6EBEAA7CAD677D65B71E47EA2FF3CE347B5C61583B97B7454F9F0E3DFB791519FB96C11F632E2849D6A8684BC950CE83D888
	309CA08740BEC1587297F2B59927A6F3EB41B65878D54BD64D6FF31D5F6E1E6EA79BC42CAC785BA74942A96F64A50A954784668F2779B35BDDC171CCBF5D077A8521AF85E0BE40D200CC003526FE3B7AAA19FE688E8D06A1757653AADBA857294803D1B4EE64CC377F2A336AD6BC11715F4D1F5593C757E8CC26CC95731B8B7A2F81C4GE6824C1FA864F956BBEE8DFCA6923786E6F24DAFCFD46BAD879ED9G6B6248E61FB49A67GDCAD4059C93175640B35D65D3BE366866350E27E794FB4DFA4CD0A755BEC0A
	1FB9A956EF87BA055F4E06BEB2D9544C17AFDA79FBA6B3FE917ECC19BC1ABF1B57DBE11D174647AD604F25F16631B5FB4F0FDE494F535E7DA4C1EE0125E905147CE54FF5B87B3AA52FB9A73D9FE91975AACBB93C6F39FE7487A8758CC5D2151B745857818383DEA6739F1D44147F32657D357A7BFFAF347E3E72E07A7FCAC9FCEE48D2621F9BDED7621F9BF214314F856B144477C2390A75DE082DFB3106EB1389FE318EE0F7921C1181DCC9A2D6D32F6FA5B4D663640DB39E2F5C24515BE6C3703F65CB2C6BA45F
	78F75453FDD83777E8062179113E40BF624375B234A0C5B25C23BE34112EF87B8DB61463E77BF03FD465BE0AE6675A92DADBDE833D02C9622C1B590238A4E84FB2F18D2D62CC1CE65AA0CBCD2C533F2B71576FBF55786B775F6A58673EDCB5517AB279FECEEB1E9C102D7881A1730C8B085B8D6DEA9377992DCFDC506EB6F18BCA909704F60A09FB6F24292F09ABAC23BC20DDB0C5604ED3DCB9342BCDDCA62D17DC506EB2F1676E8AFA372708DC76AF1D5118FE5372BE58F97F63FB66FD6541F49E2036DE867AE6
	1AF618CF6D1083EDB2D560F690E19B3B093BD9A17C325B447D020E8BC2FB02092B4B93F3D3268A3FAC55927B65CEED7CD8F3E8744E8DE244BD941499EE3F06F781DADFDFC8B6E37AD32D544435045A351002991C1DAB9C75F8BF5C87B5B6BB3F9F27F11689B2E6CF956777376837F68304E167126F6227A3D2170C633BDADDE358D76C15799C4AE18E42FE0E2FCCE6BEA6461411FFAB2779C36A75F9F5AD9C7008FB0F3C8FD02E89B0BEA5D9487574851CFBC389A9F85F4A46BDFFC1FC171EE70E3BF0115ED5C811
	183B8ABFDD77C5B0EE29B976251244F7AFD9B47E5641580287B30A187E5BCE0D25FFFE8CD4BFB8D2FF9734B1673C1D1B1B4B786FAEC31D64GAE3407732DA993BA35BFA864144B625F3F5C21E32F41B8559C7BD6AD7AC40F2653C4456E07B69E93F79929B0EEBEC0B6407A87597D9F532D676EF8EBDB48639479EC1F16D1F490E087EF1F0277677FA7B777B5D337640BA3FD6D7B547EE7E11CFB1A10F5495D113E7652C05457AA49F836CE64EBA8E7927078899F43784F2131BB937033CD7E2F13117C5F51237C3F
	4C9B4F9E798978E35F32E96C0CCB6FC7A86D2F716ADB5CF73560BEBD62FC5F0C065A6B011ACB736E2DF4D443917F68A1C2BE2663894DB1620F511B04BF4F606713DF5B4579E477B35FA52E156F64D510786713D553D8CDF9F0FA745C1AFDFDBD3F03F876CED44629973056B065EB52D4CD2F9A6CD78CFE1F67246BEB68E1194DFDE5B8BAB73D6413D87D227797B5C7CF9CBFD1692829BEE46A7299094E4DB979F16239C7EA2A594C51F649798323A565C5E935160D14B149966D73550E456768F14607557DC70F09
	5FADDC833B25CE9777977F7A916A906F7E429BD62A420622BA1DEE45D065C69E0E7C6E6266B1F1F731FC3A383BC89DC69E45A9F09E8628830827E3E23D7FBCBF6A9B9F96C5751CFBF754DAB0DFEB008DC685D7BB42DD0D4FBD3BA1CAE7E1C9144E92F22FBE4B6656E34A62F56B81BB46D2BB6824824C84C887D8843082A09BE0BDC069C310D3GCE8338817A81EE8324G2481ACF8C86C25CB06D05EC477A97C3212963B3A24E0394B6B0B3AE3631D7F17BEB4724C4566B50FBBEFCFCC1B4D3BCB67E5BDB47A9C97
	8F5641B8F67E345FC5DF30BF9CFBAE5DC171658F471ECB3FBFAC4E257B20AF79E1E127137644B5C723FD7C1A631CBDFEFD7FC77B687A3E539E3F3E6F36DB6BFB06136C687BDED827AEF3DC1F093BE462FC14A662AEAA9E235F448FD03990FFD6C60682BFC8659A2438AE0E3BED41DD623809F68C90D039DF53455CA9F63ADFA92A4A6F6C6898A5GE514E90F99D4C6A5A8D9641EE562D06E0FE83CACAE5BCC672ACAG4EDC7457BAC4541F0F5258DF83EBD4B0C3540B0EE1DA2FC33BF2062843375DA5269F6D323C
	4F9D3E7FF578737715874BA74F9E4335B5B0BBE29EA1E21FFE115A278E6C601AA17669934305E2AC9179A59FC971777F40121753AD9BBEC04648D3D5BD1A6A61D92F7690E13F9D7D1B66B5C0E55E9B24B02413ED23770306077FE9653C2FDD5BED4456C20A7A29AF351042C1F44A885647F3A07A160486885D83D94BA0C574131553A0DB5B3FECB96FBE6CBA535952F446F13E610BB3865952945671D869401FDE4ACE603BF9043CAFBEF16CB74BDE6D8C65132296728116D19D32CEEFE4E48F7E98027FBC2B1914
	84118B9605AD24A0C49F05A1E3C8155B48763E2F70A78CA1C376B408B3EA48A0BB5A1053E15C1E8E1F15C27D86467C49B6D290409F1211ED2CDB15825E436C6267A455B12F61B4558ED38D81F94B283CC8254A24D276F76F6F7F24B5EBE1BE31B5130D2AA6F91A25DEC84DAD2444685765D0BF98BF927C83A3D4BC08AB0D5BC4BE105BE52A0F4AD639A1B319C27BA25FCB591C3D3999366F08CE8E5311D835D519A3BCCD7D128E2CE43D45409F46E97ABE33EF629E63F77F70794EEE9B4963CA62C295C70230C4FD
	12DFD1076045C53D03AE215DGE9F5A9906A53F4BFC8660811CCF4198C2A7E6C27555F79413B17A90368D403FC4CCC89D38FFBE54DFF38212F8FB202C9G6C8A64BFB24963182A18B15BA20F6CE8FDB937877C40C5727D5555747F917A7FA87EBFC2B1C7A86648D1B0EE8E9973AF36BFA2F4264E83723CCF6541652FEA0695B4FE7946CF1FFC65D44E9A502E1994529FAF40A214C6C71D8F7BFBE4FD4A3C6FFC34E9566A6938C6C1F4158852AD0D1894D143961ACED75F28F1663FB6A75FC68AA01C5876824E0BFB
	84B82F4E22AB9F36976AF7855CC9B085148235ADA3EB523C27F6CEAE1DD193361162E6DCCFB558AF35F0B95B48269ECACE56E12CC1CA236BC16702535BFAA1A2F6C5713D08E73D9DC12997632244A369BECA81B5F2DCA1A5FD3A3707B2B0086DCA7395E0CBDB583D370DECB47A71268D9D54A0BB23245B3196619DAC24881C16F203744FCC9ED0C95C4D353648A45A421D45464D61C0F30C98B3EC0EA1BC0B93F3CCD1CC7066D16C46D1D1D80068AD23E221086D1D3479A14E41172D34D9C67F7F769B3B9C4EAAE7
	84CDCF290C18AA94F03051950F2782C920C27EC8956E0F244723122221B7F2E9B798E4937DCE69209FAAADF2DF2334365C3B693175FB7CF778CF2B41B55E7EAC424E0D076EDA3FF72EFB20BC616F88F6BF463FA3BD967B3D73AF689D2C8A786447C45D79EE8BFE57CD74CD2243AF292AA1293E0600873FCFB48E99F2744E7539D6249F92E3F8A76B7BD1AB4AFD5DB2677F81D0CB87886E3E9FB2A893GGD8B2GGD0CB818294G94G88G88G66F854AC6E3E9FB2A893GGD8B2GG8CGGGGGGGGG
	GGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGE293GGGG
**end of data**/
}
}

package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * @author: 
 */ 
import com.cannontech.billing.*;

public class BillingFileFormatPanel extends javax.swing.JPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ActionListener, java.awt.event.FocusListener, java.util.Observer {
	private javax.swing.JComboBox ivjFileFormatComboBox = null;
	private javax.swing.JLabel ivjFileFormatLabel = null;
	private javax.swing.JPanel ivjFileFormatPanel = null;
	private javax.swing.JList ivjGroupList = null;
	private javax.swing.JScrollPane ivjGroupListScrollPane = null;
	private javax.swing.JPanel ivjGenerateFilePanel = null;
	private javax.swing.JLabel ivjOutputFileLabel = null;
	private com.klg.jclass.field.JCPopupField ivjBillingEndDatePopupField = null;
	private javax.swing.JButton ivjOutputFileBrowseButton = null;
	private javax.swing.JTextField ivjDemandDaysPreviousTextBox = null;
	private javax.swing.JTextField ivjEnergyDaysPreviousTextBox = null;
	private javax.swing.JLabel ivjBillingEndDateLabel = null;
	private javax.swing.JLabel ivjDemandDaysPreviousLabel = null;
	private javax.swing.JLabel ivjEnergyDaysPreviousLabel = null;
	private javax.swing.JTextField ivjOutputFileTextField = null;
	private javax.swing.JToggleButton ivjGenerateFileToggleButton = null;
	private javax.swing.JLabel ivjTimerLabel = null;
	private javax.swing.JLabel ivjTimeElapsedLabel = null;
	private javax.swing.JLabel ivjDemandStartDateLabel = null;
	private javax.swing.JLabel ivjEnergyStartDateLabel = null;
	private Thread timerThread = null;
	public static final String BILLING_VERSION = 
			com.cannontech.common.version.VersionTools.getYUKON_VERSION() + "2.3.13";
	private BillingFile billingFile = null;
	private java.text.SimpleDateFormat startDateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
	private javax.swing.JCheckBox ivjIsAppendingCheckBox = null;

	private String inputFileText = "";
	//private boolean. billingCommandLineInput = false;
/**
 * BillingFile constructor comment.
 */
public BillingFileFormatPanel() {
	super();
	initialize();
}
/**
 * Insert the method's description here.
 * Creation date: (9/10/2001 3:21:17 PM)
 */
private void about()
{
	javax.swing.JFrame popupFrame = new javax.swing.JFrame();
	popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("ctismall.gif"));
	javax.swing.JOptionPane.showMessageDialog(popupFrame,
	"This is version " + BILLING_VERSION + "\nCopyright (C) 1999-2002 Cannon Technologies.",
	"About Yukon Export Client",javax.swing.JOptionPane.INFORMATION_MESSAGE);
}
/**
 * Insert the method's description here.
 * Creation date: (5/6/2002 9:48:27 AM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event)
{
	if( event.getSource() == getGenerateFileToggleButton())
	{
		setFileFormatBase( FileFormatFactory.createFileFormat(getBillingDefaults().getFormatID() ));
		generateFile();
		getBillingDefaults().writeDefaultsFile();
		repaint();
	}
	else if ( event.getSource() == getFileFormatComboBox())
	{
		String formatSelected = (String)getFileFormatComboBox().getSelectedItem();
		//setFileFormatBase(FileFormatFactory.createFileFormat(FileFormatTypes.getFormatID(formatSelected)));
		getBillingDefaults().setFormatID(FileFormatTypes.getFormatID(formatSelected	));
		//enableComponents();
	}
	//else if ( event.getSource() == getInputFileBrowseButton())
	//{
		//String file = browseInput();
		//if( file != null)
		//{
			//getInputFileTextField().setText( file );
			//getBillingDefaults().setInputFile(file);
		//}
		//repaint();
	//}
	else if ( event.getSource() == getOutputFileBrowseButton())
	{
		String file = browseOutput();
		if( file != null )
		{
			getOutputFileTextField().setText( file );
			getBillingDefaults().setOutputFile(file);
		}
		repaint();
	}
	else if( event.getSource() == getDemandDaysPreviousTextBox())
	{
		getBillingDefaults().setDemandDaysPrev(Integer.parseInt(getDemandDaysPreviousTextBox().getText()));
		getDemandStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));
	}
	else if ( event.getSource() == getEnergyDaysPreviousTextBox())
	{
		getBillingDefaults().setEnergyDaysPrev(Integer.parseInt(getEnergyDaysPreviousTextBox().getText()));
		getEnergyStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));
	}
	//else if ( event.getSource() == billingFrame.getAboutMenuItem())
	//{
		//System.out.println("About1");
		//billingFrame.about();
	//}
	//else if (event.getSource() == billingFrame.getExitMenuItem())
	//{
		//exit();
	//}
}
/**
 * Comment
 */
private String browseInput()
{
	javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
	chooser.setCurrentDirectory(new java.io.File(getBillingDefaults().getInputFileDir()));
	int returnVal = chooser.showOpenDialog(this);
	if( returnVal == javax.swing.JFileChooser.APPROVE_OPTION )
	{
		return chooser.getSelectedFile().getPath();
	}
	return null;
}
/**
 * Comment
 */
private String browseOutput()
{
	javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
	chooser.setCurrentDirectory(new java.io.File(getBillingDefaults().getOutputFileDir()));
	int returnVal = chooser.showOpenDialog(this);
	if( returnVal == javax.swing.JFileChooser.APPROVE_OPTION )
	{
		return chooser.getSelectedFile().getPath();
	}
	return null;
}
/**
 * Comment
 * Input options removed from gui, this is not needed anymore!
 * Input dir is now a static string, More specific input file details can be implemented later.
 */
public void enableComponents()
{
	/*
	boolean showInput = true;
	boolean showOutput = true;

	if( getBillingDefaults().getFormatID() == FileFormatTypes.MVRS )
	{
		showInput = true;
		showOutput = true;
	}
	else if ( getBillingDefaults().getFormatID() == FileFormatTypes.WLT_40  )
	{
		showInput = true;
		showOutput = false;
	}
	else
	{
		showInput = false;
		showOutput = true;
	}

	
	getInputFileBrowseButton().setEnabled(showInput);
	getInputFileLabel().setEnabled(showInput);
	getInputFileTextField().setEnabled(showInput);
	
	getOutputFileBrowseButton().setEnabled(showOutput);
	getOutputFileLabel().setEnabled(showOutput);
	getOutputFileTextField().setEnabled(showOutput);

	repaint();
	*/
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/2002 11:25:28 AM)
 * @param enable boolean
 */
public void enableTimer(boolean enable)
{
	if( enable )
		getTimerLabel().setText("0 sec");
		
	getTimeElapsedLabel().setEnabled(enable);
	getTimerLabel().setEnabled(enable);
}
public void focusGained(java.awt.event.FocusEvent event)
{
}
public void focusLost(java.awt.event.FocusEvent event)
{
	if( event.getSource() == getDemandDaysPreviousTextBox())
	{
		getBillingDefaults().setDemandDaysPrev(Integer.parseInt(getDemandDaysPreviousTextBox().getText()));
		getDemandStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));
	}
	else if ( event.getSource() == getEnergyDaysPreviousTextBox())
	{
		getBillingDefaults().setEnergyDaysPrev(Integer.parseInt(getEnergyDaysPreviousTextBox().getText()));
		getEnergyStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));
	}
}
/**
 * Comment
 */
private void generateFile()
{
	if( getGenerateFileToggleButton().isSelected())
	{
		getGenerateFileToggleButton().setText("Cancel Generation");
	}
	else
	{
		getGenerateFileToggleButton().setText("Generate File");

		//Interrupt billing file generation.
		getBillingFile().getFileFormatBase().closeDBConnection();
		update( billingFile, "User canceled billing process" );
		return;		
	}

	// Gather new billing defaults and write them to the properties file.
	BillingFileDefaults	defaults =  retrieveBillingDefaultsFromGui();
	if (defaults == null)
		return;
		
	setBillingDefaults(defaults);

	if( getFileFormatBase() != null )
	{
		getFileFormatBase().setIsAppending(getIsAppendingCheckBox().isSelected());
		getBillingFile().addObserver( this );

		Thread billingThread = new Thread( getBillingFile(), "BillingFileThread" );
		billingThread.setDaemon(true);
		
		enableTimer( true );
		
		System.out.println("Started " + 
					FileFormatTypes.getFormatType(getBillingDefaults().getFormatID()) +
					" format at: " + new java.util.Date());

		//start our timerThread
		getTimerThread().start();

		//start our DB thread
		billingThread.start();
	}
	else
	{
		javax.swing.JOptionPane.showMessageDialog(this.getParent().getComponent(0), getBillingDefaults().getFormatID() + " unrecognized file format id", "Yukon Billing File Generator", javax.swing.JOptionPane.ERROR_MESSAGE);
		System.out.println(getBillingDefaults().getFormatID() + " unrecognized file format id");
	}
}
public BillingFileDefaults getBillingDefaults()
{
	return billingFile.getBillingDefaults();
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBillingEndDateLabel() {
	if (ivjBillingEndDateLabel == null) {
		try {
			ivjBillingEndDateLabel = new javax.swing.JLabel();
			ivjBillingEndDateLabel.setName("BillingEndDateLabel");
			ivjBillingEndDateLabel.setToolTipText("");
			ivjBillingEndDateLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjBillingEndDateLabel.setText("Billing End Date:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingEndDateLabel;
}
/**
 * Return the BillingEndDatePopupField property value.
 * @return com.klg.jclass.field.JCPopupField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCPopupField getBillingEndDatePopupField() {
	if (ivjBillingEndDatePopupField == null) {
		try {
			ivjBillingEndDatePopupField = new com.klg.jclass.field.JCPopupField();
			ivjBillingEndDatePopupField.setName("BillingEndDatePopupField");
			ivjBillingEndDatePopupField.setToolTipText("Maximum Billing End Date.");
			// user code begin {1}
			com.klg.jclass.util.value.DateValueModel dateModel = 
					new com.klg.jclass.util.value.DateValueModel();
			dateModel.setValue( com.cannontech.util.ServletUtil.getToday());
			ivjBillingEndDatePopupField.setValueModel(dateModel);
			ivjBillingEndDatePopupField.setEnabled( true );
			ivjBillingEndDatePopupField.setToolTipText("Select Billing End Date from the Calendar");
			ivjBillingEndDatePopupField.addValueListener(this);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBillingEndDatePopupField;
}
private BillingFile getBillingFile()
{
	return billingFile;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G5BEA51ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BD8D5D5564C0CCDFD937F0CCDB41F53B87FE8D9E1D1DAD9D211690C15A515251628D3E4D656CFC665778F4DB8FF072708A8A0A8100017B49F49DCC0F146C1D4649560D5C9482C2CBCF08FF0755C475CFB50EBD97DEB6D57B977F2AF08CE734977AD4E596B3557DAFB6D47597B1CAB652D9F151A1814A7C90929927B7BC722A4E5F7CAD2CACA476FB8AEE4D4CC06E478AB854811D22AD260BA9CE811
	09F3B33225DFFE118E65B1D0B63D9513118F7739D273DA57913889D2CFB350A67F2C38EE60FA16A772FA1611720A5D19F0DD85908E38E8000251B1D07F065D858C9F8F38D56C9EA1B1C91222894FFBA585867C715114EF94DCA7G0C398EF9766EAEE274BCA81781F881C22F53F5AEA756DDD63D0B6B39EA143464511C2415D817C1BF07B8A61BFDBD3D1A24DF13C0A4CBAF556C04EBCD5B291E3DDDCE3B39CBF6B81C8ECD696AB5F75AD4556668B15BE51B4365F67638E53B273A3A0EE21BEC2A02713A9E6DEDAE
	C9A73AA01ED04E9C033849A5982329D01E8F3084005A7C644A9AE6D35829D1D26C75A9C1EDEE9BC3ED554668B627BF521A327186545FC35AG65A6GBD8E007A53332B76C27DE9F23A6D3256A6BB94157AB5EEB7772B77866E576745D86FA8A8C7824C38017B35E7B3776B58B76324044E2A242CA0FEAD3A0135450D3ADF12B2BA39106456B5C9189F962039G06810483CC83D884300E64F4DB6287F05D5F2955BB6DF627E33F4BD527DA5D871DD6C5057B6AEA20B05C91254B6936CA924D5B03A5E5816C61A05D
	641B33FA1EFDA271BC0BB8F22324E99FA4258670ED426C5C24BC26E34BCD3AEF47EE4238D52D4738B543FDA20BA9456FA5781186BCED5F07C87EE0B9827942F91C0F2D6236D8D22E1532AB7F1E34AC00ADA3FE3FADC98B27B604066B365098BBD3B04611001F85908710B2B6A6E3A3C043D89E63EB93F78C96639648AB37C3D65B7BBADD32DB2B6B3329D6454DE2ED5FBDD02C8381157BE648F2B4AE61BB0CEDB47F4E1D8163B2BF7C5D2493E3E9BC3CE37538503AA7842DFB44587E3931CA34619324CFC40C75EF
	5B98020F9A6B5F36F2B1EF5BE820D55C9C13C15BE5E59A0EFBE1D09E8FB08DA096C03AA5A623882062965EB6AFA6161FE77E37995A054A7EF752DA38AADE4DAD37B8BC1A6C68D20E4AEE1B5C29AA9E09715CC4E25769A397C17AE59754712162D05C32E6F3BAC03BECD7A089BC14672E249C64F1AB045AE133ABD0015DA5F13D77A76542D515BD5A47AE2B2CA9BA8725BF1804EDF9DABE8A9AC1815C075C0271321271358D6E55DBF82C2A17A1EEAA14670B3854A62F066B81C8CAC52D2EEE30F52199327BAC4F2D
	6A75B80F455F427B5262E3BCE79AD65EA0154D2E8A18B3886991ACD7A27CF3260168EC0E60F3E3F44931A1662FBF20FD83771197AF3A0E6481E8B43E616918233D6057B000B191BC465FA41BE86E55499E05E75BE1B7A40AE255A3CEF93DA9B887A8279507562038940795EEBC2C2D3FCFD92E67E137D36448E5840F63C9BDA40AC7602FCC4921F850E14CEACE3F1A50E5CDF35BBA7BB425DD51B41856F4795F90DBBCBEC3150779FA57F252CF4147D9G8B052F9BD3F38D79E4F1BACF7539BA3027303C1CCE6647
	84604F3E15377F6CE24C07AAD1FE0E14F5FACF7145349D6A9DFC8C8D3928BA864A0DD5A21E79E9415B36A4ADF05BEECC8B5C369F248D5CF65B5382359D257DB38D67E2679907A197762661B85065F4B8941210C2C422EB24459D68074DE153BE31D9FB942D5D76B5AC6634DB31CD3FA1E30D856ECF5C4A4709B39B78B831716B5152642F32824E7FA3EEB0A7C53276093ED59FA728CD7B16E10EA92AE207C055BB7B9C9A45B712F10C61BD49847762B73888EFBD28B87ADAB445DE497B64228C5491873A255BE04D
	FD9B4F53A94B8A8DF94ABAE33DD3D5297BC0BBF19B7A5637BE4FE0EF45EDF46D2E3769ED695998E765DFFD8ACC8B1868871433FCECCFDF6DC7BBAA2BFD7A1CF1FF7A8A382A06BEC27177AD8F6C77146558AFCE897D8FAECF13F06C27BAE9BB0D17D1274BED334310021D1EBDC3AC58EDFC068887BF70F9A39A2E313771F57062E614B52E5693C88CFEA9566AB807D25CD802FB9D8CDA194C579387330D4F96D64F6600ED0ED0B30E4630ED1C7FBA2BB893753AG9FB20E37553E8C6318325F63F1F65948F4496235
	B843188F9477D68666C3374D6D01B517D57172183FCD729367D306E6F1FBE56DFBD27F75C1235526B95DE271560509C1FD7D364978CC9215D51434A2082FE14C478986DF931625A553FA5F5A0A6DC21EBDCE4A27E52F59F38602DFDD5DEAF826305424B3BB170C634F945EFD18A3E563E17D89F0FCBC6FF71FECB736416458F5C15BC09BCF6D39E23CFF9B0CDBC91E09819FB91E370136A2475086700CG4B209E72BCC7696FE5A6535CE465CD1929BCB7A55E9763D6E1DF0A861D0BG245B21BF826C3A1D57F1EB
	66206B67C66FG6B670FEA7C572247EE5747FE75767E73GDDF34611D8065CCE571C4B84BE0160C79870F4AD7A9D41E3F9BC50726E606B2B955998B7AF142F38037B34EC65CAC35C5A1CB607A6E2B6E1A576DF275B56E3F3F05CBD8487CFE0E4F02638A52B024F9BFFD995F85EC84C8ABCEFAC4B9AF8CD101595ECDE2173F7AF49459160DF84G45B56EC55C8CA8AF3A034F59EF1298E55E991331852079CE0E7FBF026F05F2A840F8019F360F4F3F48FF3E73EEA653BD989F2D6337220E5473544F6776E56741C7
	63171D0D39629565BCD2BE4B74409A2E865BD7154FBA7BA091EAA7E07CFED5434766D960CB9CC09E6037CC20EBA4BACE5CFE864717EEF8F80066E8F7A9DDD251DDA8FBFCAF173D8278C782C4814C87D88AC06B7CE885660F5FF68533E765B19CDBBFF4BB7BDC961BC7EB6FF2439C088C0CBE3B927301BD3FA82803CA733D294ADA7FF985ED3602EDDEG4AF379C1A34F703BA8CF94DCE7881EBD2D0174AC827AD2G5A6FA655707D1B3C3BE3B2ACF77358D41E40E7C0B1061A65B3DAF57507EE1B35CE6E3110C853
	F1B4EDCFBA139FF6B79FC72B7610F5AC14E781445D4D47D16FA6BE3EC75CF82DE4895999F04FA93623A8A9E5A25B3F18280F23F94A68E4DA670F7772BA2DC09FB991F163779A57949867E418C74DF0257485BEF40CBF76EB1D1E374FC8C7FBF7A69B69CDBEF40C51C4A6CFFD1B7EBE77ED6974F0C93DEBC3507D34054C2774C8FA9D1E3BCEAA4BBD10C2FD335526333ED19B4963F9399363690572F00070C8314756D8AC851C678C994777869FB8184EEC1DAC74CE34235E84A8E75F83FD83007276358A9B6E613C
	6F1DA0B6C0F9B8C078BDFC0C195AEFFEB83FF163298B14DB31E728F2F45EC95B0B76CF3E074EBB09821FC5705186BC6D9755FBF83F1883341AFB79FC74D08E7629D1D00E821885908710F99F446FBEBEBF7DBA3BC29AE44E6DF03AACB079280669027AF7795E8B5BF71A320C675F04FB2F13A2FF7AC2E23035D34DFDB46F0E336B16556B881E7A77E9AE7A978A34F000E80005G89G59D15C3F67334B28FF38145B2FE9F2D7AFD9A5F2670E5A14B3BA9A87D66A1B2356685BC65037ABCA1FE7ACD141569265AEEC
	0B93D1340D5688FC9D41079970346D7E0B7497ACCF831AF63F485503D83FE9D2CCC695C073A45F7A0ECF8AD67FB6876AF3CD725FD75BC970A1137C77556E3773FD3561C0DBB209577FF52B517FE38F44E4288F68F506BC902C7E9BC95F8BFB005613A570F793FC1481CF6B5FC270D8DE88349313F97D19FBA5510F56986EEB77DC6CBE4E607D76539F348E1A17CF6484DF53BD1D93F8CDB7BFA7701AEEC14E40FB812F66845F8BF8A3473897C07B556F72F07E188FED11GD0B4A5A6238220E18A6FD35F2FE64FCA
	817AD32335C729F712DDD20D575F199B5CD7DB6E60FB2B6EDCFC166BF4EA1A530E7AB95E1B0B6D25B9DD84C9F15F66E20CE0C95D2BD1D6067FC9AE4E932A52CD50F45DFA99193FCE00DF5A943E16AD2C606B5B9182F7F5A5DF5FC6CD6179395335852E193F0731942099408590869089B087E0A94046BFC05C7E40E51A090C954A43G46GCC85D88810B295F8819AG2CG4326F2192B1D2330BFB13DE48CE47A0365975A02F5AE8D400376DCC84EE6B27F866EB713082DB8162FE4F84C2D2CC17A5B9A092EF17C
	E93997583FF0ACDBBEG1D3669BC32469883F10E1A4A5B3966906226C1B9CE602EB944F3C4FA1063662C61793071C15ECE158E5C1B51C4F9B7A9070A729F6C97B326205E41472D7997D547E0FB1854772BDAF9BCA2C4BCC28AF81F19A1F0FFAE64B1DAA2E290C156698D8F41B88F9082B086E09AC0AC405207B8DF9461ABFA9856B2G47814E810C840882187FB067B31F40F392147BF70D4D8D86DD316CFE8C33F79A33191D0B123A874A6D0B09BF6FBF18475807D630B6E6E7CDF94147535B72869FCFA766117D
	B29887BB4E3A940A0B4AA3FB915D5D9EC50C5BD37232852E4553667458F0530DBE970E2CC57ECE271BEC9E5067583B1B79B35CD2E8233CE9700C86F082G1F77A9CFDD856771CE234F3A4361BA067144CFC31E1BAB9B8609139122C0E686409AD6DE88772F3C734EBBFF1378B3771CCFFD1FFFADF227F87E5DDD335A07864B60CEE71D534BE5BF580BF3E203E2179D5686792C274D2D1C36B97BBC9D90E2424678CA4E20DFEC6F3491F9B5254D696AF3B5599455CAF99E7EBABF20AE23BD1F1C4C6D2747C87FE0BB
	6AE804693A67ECB09D715F948664713779263EBCE1CF3B86EB83D199257F5519A7747852695E780FDD46674CDD0F50B6BC8ED7CAEF5CE6243B98FD0420E7FFE924C7B07AACC13F6300113E08510B9E6574470B8255DFA168F7968722B788FAF7CDA07AB1C1FF3AB810FD2A203FFDA010FC0820C7D88251439E657BB26336E13F561CCE354366424611429F45F7099E456E475A287D903677E9452D593AE415BEAC57496EB627EA6BBAAB15CD47FA9CE40E5A8A9077A86FE7138E71FE661ACE7B6295F09DB91D6E3B
	51FE767A1A417AD92A211FCDG59E95375FEB69F6EE7A6C95275425666DE9C2F44DE12444FD0D407CFBBB29BB223392D770A3D9FD3B435F597DCAD51F4FF10720CAD60BC56E8BAEE1C036BF046C37D49F9F7B0FF16997C89875928E85D1F9951F45CF88C007AE34E40F163F01F666A53F004B0763167FBF3FDE858FC242BB37A3E1C35BE74BA37730CC7296B03B52CC3927D94FDB26E2DF5B8FBFAD446C5F99628387645D3FFB72D1E494ECC4AE678C6D5F6F9942B215F3E3983EDEB41B5B9CC8EB00E5477AADD27
	30675376F86B04FFDEE28C67C7733C3D7F24FF5E13988BFAC116111E72982517BD4669ED2A11DE4B682A200F79B8D07DA1027E377CC074B0C19F6F53AF43197E990F71FD4230967ECC3B90F0C50F73BD28170B310F84FA4F2B274FE66E53ECEAF5F507CD0386E0539A5B0F1A5F424FDFBA9E67FBCC5D65D8C7A81447GC4BD4E6BBED94177E2E6A16F93FC1F6A0E8A3E8F5870845791CBF84FC1B98CA082E03DC4573D48CB7F6277854251F1E2B5EE2931BD1567B3791ECA51AD3F119A1EF9A1F10394F8CFE56A93
	F4DD13B7C35F2FDCA57A7ADB5DC14E23C8FED1DF6EF171BDB74B8C313F272E8BACA7A6D4AAFBEE9B0F65B0A17B7E1622403222D3D1598F3F6475C689D91B3259582627549E7349AEFCBAB6F723A09A20CF434C06D78E087334993CFDD20B396E32A7B96E55E23E8FE995385F56F03E11823732046B1BA5F095964E177994473DA0F08DCF71FD66A7BEB71E57E5DF3BA2096F6D79375B392758F95DD3FA3B517EFE5921AA389AC2E7B66C2A1A0FB69E69E8295FEF792A3D7E48E10B25EE7F112F7637FFF528313121
	31015AB06A0C5106DD9925C14F8C23D85DB358F5E943BA360F7352A15E6752E74218BD137BECBE4CE3631D49737E7DB548BB9E4A53GE2E78EB56F8FA209656B742D79C17295E7A0EA4362E6FEFE1DB0137753852D5C5E0AD9B0AF4F6276FEA0F2E354ACEE6FE37948BB874A71G6933390EA78BF8DFB7816E58EC2E23301077756133390E8F8B11F79A14E38192E68F5567EFCDD8E2B64ABC0F321F66F5AEB9A95E27FD1A770D2C7642403161539053F7B80B7B9CA2E4EF7CF2FD10BEE91822187C65AA37A7D258
	B36FE344617C99AFF056FC446133CD47B33C1E3DC7030D9BFC4E2232AF0B7E99798CFF863B36E5307D6778E7FC1F8D29DC7820F2A5FEE5AA77BC114B1CF3FE4F1BA66023FB1FF349DCD1B547FFBF7F85022F1D633F1F2F16733C32GED7C9C6E2F2B62424EA13E3BC03909C31623FEF5D4227DF9B17E7EFEC1702698FFFFDF92F3E69950064770730BD325381EDB82656C39B199D5G960093GDE00D000D1G93G22819682A4GE44E0B49B081D4GB4G9C1B4777ADBFA1737FD179E47F37BBG498E8C496625
	DB266774ECDF4BE50C03BA0F36FF883B064D6B1F8FD46EC75BD0AEBCG1D0C25C165ACDF225C0CA0F28B811FB0AFD0DEAFBEC072F3FEE0B9D3GBC154BA8B67AD745781A5975F8D03957091C0B5107316B88F60D88AAF71D4FF95AE446B70BDD978515DBCD5EC5DF9A443F6CBF86135BE971B94BE1FC3B585592D4EE110F5C894627316B958164E85EBE095B63EC8F6537456F874B397D1DDAAB8CF907BBCFAADDDA075CA3649F5A00E35DC8509F79C71E3BAF94EF9BA0F75B5CCE97BCA21E25D872169973A16E50
	050D11FF9ED4AE70983963B04A4D89901BC09077C7BEC67E1A2C8924E77D470C6B88BE73D97FB1E35B9ABEE694G6D5C33FC0CFC2BF9A03BE31F8D5A562D9796277F9DD4AEF01C4AC9DFDD92401EC0107E9C0F53FE3266A8FA4EBFCE2D843F65B97FB84D4C67F12A825A0867F81C1EA93830B961458B14ABA93C303964BE32EEDA5A4F5F29841F9E6B6F6F41C2710E88503C315C5FF259D87F7CD89A77F8F6CDF9BED8DE3CFDF2A03B8B024A4D4D8BA4D74678EB034A052A0164BA98FFAFDC4FBD9FA88FE3BF8E34
	77BF1C710E01EBE400BA91227D7031D0DE82103EC04753BAEC79016AA8B270D5AC88DCC7039F7E9814D50010FEF544794CC5E10C9E4E2ED1817463C3F032B4B9975715B607EAF3A875740B8B091FD9A410677799A03BE881BF1FD8ECC65CD2A8E73F40F3A496DFEFE5F63CE238F7177F70E74ED77E876A782CD277294BF09FD77143571DDA79431F29FFDF9A7CDC642725031F0B7C22B470B973B5257D4F192FAD8DFC4EBC2A54F84ECCF1B7146233CE8F2CAF0F893E1B856ED3019BC7F4A26E931BD56B95783B08
	DD08EFD630C20E3F0F58F506600EB15CE4836ED37E0DD0A91E55004B3DCE71CE692325F8BE59EDD3D5DAFE1C70589C9EC5937118C9EC3439E4035DF3848E6D26FDE9B6598FAA02FED476826FDF5F54A32E964AED8297DB4F4F840F8B5C08ED7C9CB6C2605E594679A2852E6BCB4E173E106326FE497972846E66F244D9216C9538D48B179DA6F05D96AEBBDC60CA0B399F318277244A7DD8A2F05B6DDCD6FA1163526877D6D06E95B8FB89572789DC3C156B3BC260BED2383EB0013B076CB3AD04729201FB1B541B
	7ED2CCC641CB9C7725C31CC58B5CD6E10BA6F0BFAB61FCA182E721B100F22C40E56FC35C92A8273C4CF1FD7B38EC36403DA3E4CF885C3D04CF0372B0017B7A3408734BFCADBBFD8376D9713E8ADD2E327CFAEAC31EC43FB331492A6DEB721AFFBBB0BAFA283ED6321F948B3A92C49D3712734C4CC5705C37080F653FDB230FEF61067B4AB5BF7CD8577A0356C1FD4EE861F1ADDB446350F4004726CD60BE4B6731310A58FCD82047234DF09F93F0FE7F7746664D7FC09DD38AF51F9EB55C37977E70F52BBFE89D2C
	1DE5BE2E38C4BBEF584A4715E182B7EC2B68FB8217FD0A63E2856E0DBCAEDB718A479D916F3854885C75E4AC8C0572C801CB97E3E13840B5EFA45F78C1B97BD50ECBBA0A380DD02E92389E022B05F207400DBF4AE5FB85EE6CA6441D03F22840BDA56A9DA5F05F2EC5DCBC14D37E07639EA63882A86F9238ABC91F30C079384045670BFA852E153C57F28E4A2182971AA26A9538EDE45E0807F2CA1C98B30B7B571BCC4660826059A278125390D783E50B40752671BA0E457133622CD6DC9BE8F8BA5564A4FBD374
	6CF7B3F99FA79C78A6885E5472D44E0BB432FF7C8F5C06E0E707F1AEB277039A2AA369DFE42E8179F82163177564FDA6D8B0D1794FB621BCBDE77DD37DD05E4FB03D866372EBF46CA74F212F694FECECEFFB332EFBED59C03A79F9B43F56B25D9B982E836EA7B29A55DD3DD557BD6F4B41F49B4F07CF78695660DEE1F65398CDA947B513DDF6136FF7E8FDDF604FAC307BDFED536B7E56B2D85D9CD0F708DF5DE12F517322C4516E2DA43FC681BE820056B735CC2F2F45BE1433F07FB61AEFE8A36AEBF1C960F795
	DE4657EC18CD3FA2793D88E417881BB24AF51BFA15213CEF60EF5346457EB64D28714D6317481A298278EA9673BC4EF67866715FAAF4FB66B48DE64F72816CD1977B67F1DB252EFBF449E06DED546DDD6C5B5E210B7DF32D4B626B6BD9327E9A81FCE3042F1B77797ABAF13BEECF683E016CD996A49F56B09BE699FC2575EFA16FCF4682BECE547F24DF7DF3F76875DFF5FA307A83451A57DF743AFF5B4B07830F07E7497AF08B70D73D4E6D1A1A57FFBC7CAB9E1932EF28CADB443BA5A0638598819089B087006A
	F51235D58214D362F9EE3F7F8FBD771DE4CE2E82DA2D20B76F527D7F4D56210C19AE5001407D9F962F7BCF75EDBEF5BEFD0977276186FD089B8377EFA79AF37754BFF5DBED833E9734CC726D277E360EB75C53785F2B7A66454F2B753A568FB8CE790F497EF5C5417D8E1F3A3E5AC83E7BF53A159E37334FE1656DF3062CC926817F2CF81E9705C7FD6D3AFD37A474FCF7946BBDED7358703BEC2A631A237C0CF5C9BC4F0B9802CBF9836E5F60FAA7EF726D2BCF1375CB8550EB5E606FAA3DB22471595FEF9574FC
	6463F758DA64D79C3821E1E5765E32161FC3FB05ED4F2E457365DE275B76B558A72B7BD5DB0F835F60E7B289C2A6D448DC176F9B27D9B5FA5E9796F07F23843FDC606B7F9942B39F680B047F5727DC0C7F85EF7A0F87F5457D7D7F34187B3271CDEE5B3BA53E36D510B9EB97509BF41EB4DFFFB76C5573757B34417A46C07DD8FB53FF4EF06D52F3EE01998F0E9C4A99937F0C510CE35B16772C70AC6C6CF92F4CD4D1E62A5C3E154BC6D76B324B6B4ED7367FBB86DC5F6BA47F033F0330E94F40740E3D8353E76F
	536B3A3BCE377DC1F5E8FE47546BFAF6B669FA96DA0726276BC3DD4F54033A1E576CC353737221017D4EE8536BA9EB576BC959B734FA8EF768FA7E7E092E6736A16A7974182E672E4FF4BD23734ED78FFB9F2B05772F10B779372DFDAD7E5F363EA1993FCD3D49071E4364F5FA0C8F9D67787D09C67A0AD67FEFDB39BC75A7678B5D1FC379C30B4BD5DF693279DBAF3EBFCDB8B1F0DEBCA36B3C29DD7A7D962BFE7FF3C53769E7643DC9E1CF3DD3F53A4D1D2A5CF50A52A368FC666ED3286C12EEC35BEEDA5DDF56
	8DC3A52B676313BA6FC3C34CFF6FA9DD7677EB31CD3AE5552388DCD6FE29647BFEDABB7E671F09F94C750D07BA9A0F887E3A0201EDFDF805DEDFC84A50ECFDB5D317F5959BED257426AC1D7E275421693661AB397C9D0EC15FB172877A5B132672D0A8C43CA625EA5206F685FF0E46443E86B069BFCFE3A2AFD9EF71ECB5FB5CD216302E05EC5F471E5ED11D7C427549C9D2CA0B14C60FB3DA2494BC13E811D24911C50B142CB9DDDE1724BF66E0CCD3BD7F123EA39DAA4500CD915865C62CA665F1437055E553E1
	97DA665124CCD2241FD3D4EB52BABFAF8CD433A6EDBF54E76FD45CA6E73789BFD5B071EF95CC9DCED33BA23B3BFACD7483855391C546F719BDE6E10E4F1FA6959E143DB6FB1FDD545488D2784982D43306A97129830C83319CFFF6C02F219FBD74E3BF8FEB192430D613B26C76F22B357CAC7C7995327DDBD413D6725741C91CB4E9ADFD051CC62D416686EE277BAC1812ECB61B7B3327D618BC40A2E5621B61A676EA38A62D222F021BB4A7610324D96913528F0E5E79D27433C7BD49D2DA0BD4082B23F645CDB6
	60957C82057F3ADC13345C053FE002BF7E849965A117D40FF6D6D5ACD2DE77917C25A30F26D877732DFD4806B5962C299C5F2EAEEF16BD3D9A2E50F6DB2494877E8E1E341A12D55951D3CE7779F71334CC5CDFCB1A15F4D730B717580BDA4CC2CB6EFDD96F1736AE0CCC16121A248C55A9DB1BE48CCF0B34DC6BF5AB1EDE27EA753A76D1C569E752D966A6033AF542FDF4560C9B9A4268D35E5F062EDC3432A989C6B637D4CEB8F1B1E8969C567ADE598DD5A96E968D236FF4EF94349F15E81B9E7BB07FF8121448
	1C44F4AD72C25B26F54BF61B8A6D1E56C086901248A68D2CF54B8E8FBC335841B2131FE51CA4AAB0DF7DC845B5E37F7C9929C097552475B414A0DA5E23B86D657B3B3BED8EC5A80038027AE2219EF94CFEBC2B3DD72DE91D7DCEA764C10314EC2F28A07FAB497F6D783F12E0AA8926F2BB84B75E3F9F7A7E79533D5CE712BCE2704266B7B7C18BEAF3DFFB643AF9B571F160DD1314CAFEBF889AA1DB6722BD70279157942F880B3D925B48052962C53D9B297AF77B658FE2CB7AA782225F934D012577896BE6727F
	03A1BB07D2E5C456ACEC5A617599BEF11643DD43CC01CDE5981CBCB27AE14997007D3078CDF85A25676B9EDFDFD7B1EBD3AFBDCB9BFCAD5D4523FD69D9FAC03794D711D94C52E5821B43B09917E6245BFDA30DD6FEF5E9DA7A19EF2439254BAEBDCB65CBF01C6B3984ED326BAD0AFF18FF3830AD93D87EFDCF92DAAD3038A44651786FBED8BCC8CAE53C99B0F32D72FDC0C93CA17FA7593F585117A425B761CC2F3AFA6596B603D924951DE45D2D38019756FD7ACCCDE4E1B9145485EB25F5BA3E8B71145A6E123B
	F045345CAA3BCF918DE852694F25655D6E1ECED201A6A5FD5E74B9D4CBCAC876DA248C2D97FF0A8317AE1A142F2BEE43175B9901AE3624F7GDC640F385BC7F06F7860AE29067DCEEFD8BCEF4347CBFCFCDAFD69D9FA19EEA9CEB278B2B08EAEA9171E25213A2558C9B055F1EAB151EF4436FEEF7CC6AC613224205F08ADCD1CCBFF2BBEE92E5F6FCA7E250A1C3B833E97003E6FF3208A8F90033D9F5FEE17D5D51355D37B9DD6FCD73E6E2C26683FEBA36DC47D9E4E43086CF78EF7225DA78DB27F8FD0CB87880F
	FB5076919FGGB8E2GGD0CB818294G94G88G88G5BEA51AC0FFB5076919FGGB8E2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGCB9FGGGG
**end of data**/
}
/**
 * Return the DaysPreviousLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDemandDaysPreviousLabel() {
	if (ivjDemandDaysPreviousLabel == null) {
		try {
			ivjDemandDaysPreviousLabel = new javax.swing.JLabel();
			ivjDemandDaysPreviousLabel.setName("DemandDaysPreviousLabel");
			ivjDemandDaysPreviousLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDemandDaysPreviousLabel.setText("Demand Days Previous:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandDaysPreviousLabel;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDemandDaysPreviousTextBox() {
	if (ivjDemandDaysPreviousTextBox == null) {
		try {
			ivjDemandDaysPreviousTextBox = new javax.swing.JTextField();
			ivjDemandDaysPreviousTextBox.setName("DemandDaysPreviousTextBox");
			ivjDemandDaysPreviousTextBox.setToolTipText("Number of Days Previous To Search Demand Readings.");
			ivjDemandDaysPreviousTextBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
			// user code begin {1}

			//set default value
			ivjDemandDaysPreviousTextBox.setText(String.valueOf(getBillingDefaults().getDemandDaysPrev()));
			ivjDemandDaysPreviousTextBox.addActionListener(this);
			ivjDemandDaysPreviousTextBox.addFocusListener(this);
			//ivjDemandDaysPreviousTextBox.getDocument().addDocumentListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandDaysPreviousTextBox;
}
/**
 * Return the DemandStartDateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDemandStartDateLabel() {
	if (ivjDemandStartDateLabel == null) {
		try {
			ivjDemandStartDateLabel = new javax.swing.JLabel();
			ivjDemandStartDateLabel.setName("DemandStartDateLabel");
			ivjDemandStartDateLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjDemandStartDateLabel.setText("mm/dd/yyyy");
			// user code begin {1}
			ivjDemandStartDateLabel.setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDemandStartDateLabel;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEnergyDaysPreviousLabel() {
	if (ivjEnergyDaysPreviousLabel == null) {
		try {
			ivjEnergyDaysPreviousLabel = new javax.swing.JLabel();
			ivjEnergyDaysPreviousLabel.setName("EnergyDaysPreviousLabel");
			ivjEnergyDaysPreviousLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjEnergyDaysPreviousLabel.setText("Energy Days Previous:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnergyDaysPreviousLabel;
}
/**
 * Return the Energ property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getEnergyDaysPreviousTextBox() {
	if (ivjEnergyDaysPreviousTextBox == null) {
		try {
			ivjEnergyDaysPreviousTextBox = new javax.swing.JTextField();
			ivjEnergyDaysPreviousTextBox.setName("EnergyDaysPreviousTextBox");
			ivjEnergyDaysPreviousTextBox.setToolTipText("Number of Days Previous To Search Energy Readings.");
			// user code begin {1}

			//set default value
			ivjEnergyDaysPreviousTextBox.setText(String.valueOf(getBillingDefaults().getEnergyDaysPrev()));
			ivjEnergyDaysPreviousTextBox.addActionListener(this);
			ivjEnergyDaysPreviousTextBox.addFocusListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnergyDaysPreviousTextBox;
}
/**
 * Return the EnergyStartDateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEnergyStartDateLabel() {
	if (ivjEnergyStartDateLabel == null) {
		try {
			ivjEnergyStartDateLabel = new javax.swing.JLabel();
			ivjEnergyStartDateLabel.setName("EnergyStartDateLabel");
			ivjEnergyStartDateLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjEnergyStartDateLabel.setText("mm/dd/yyyy");
			// user code begin {1}
			ivjEnergyStartDateLabel.setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEnergyStartDateLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (5/15/2002 9:39:47 AM)
 * @return com.cannontech.billing.FileFormatBase
 */
public FileFormatBase getFileFormatBase()
{
	return getBillingFile().getFileFormatBase();
}
/**
 * Return the FileFormatComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFileFormatComboBox() {
	if (ivjFileFormatComboBox == null) {
		try {
			ivjFileFormatComboBox = new javax.swing.JComboBox();
			ivjFileFormatComboBox.setName("FileFormatComboBox");
			ivjFileFormatComboBox.setToolTipText("Select Billing File Format.");
			// user code begin {1}
			String [] formats = FileFormatTypes.getValidFormatTypes();	
			for( int i = 0; i < formats.length; i++ )
				ivjFileFormatComboBox.addItem( formats[i] );

			//set default value
			ivjFileFormatComboBox.setSelectedItem((String)FileFormatTypes.getFormatType( getBillingDefaults().getFormatID() ));
			setFileFormatBase(FileFormatFactory.createFileFormat( getBillingDefaults().getFormatID()));
			//enableComponents();
			
			ivjFileFormatComboBox.addActionListener(this);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileFormatComboBox;
}
/**
 * Return the FileFormatLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFileFormatLabel() {
	if (ivjFileFormatLabel == null) {
		try {
			ivjFileFormatLabel = new javax.swing.JLabel();
			ivjFileFormatLabel.setName("FileFormatLabel");
			ivjFileFormatLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjFileFormatLabel.setText("File Format:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileFormatLabel;
}
/**
 * Return the FileFormatPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getFileFormatPanel() {
	if (ivjFileFormatPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 12));
			ivjLocalBorder.setTitle("Billing File Options");
			ivjFileFormatPanel = new javax.swing.JPanel();
			ivjFileFormatPanel.setName("FileFormatPanel");
			ivjFileFormatPanel.setBorder(ivjLocalBorder);
			ivjFileFormatPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFileFormatLabel = new java.awt.GridBagConstraints();
			constraintsFileFormatLabel.gridx = 1; constraintsFileFormatLabel.gridy = 1;
			constraintsFileFormatLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFileFormatLabel.insets = new java.awt.Insets(12, 82, 9, 6);
			getFileFormatPanel().add(getFileFormatLabel(), constraintsFileFormatLabel);

			java.awt.GridBagConstraints constraintsFileFormatComboBox = new java.awt.GridBagConstraints();
			constraintsFileFormatComboBox.gridx = 2; constraintsFileFormatComboBox.gridy = 1;
			constraintsFileFormatComboBox.gridwidth = 2;
			constraintsFileFormatComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFileFormatComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFileFormatComboBox.weightx = 1.0;
			constraintsFileFormatComboBox.ipadx = 91;
			constraintsFileFormatComboBox.ipady = 1;
			constraintsFileFormatComboBox.insets = new java.awt.Insets(10, 6, 5, 16);
			getFileFormatPanel().add(getFileFormatComboBox(), constraintsFileFormatComboBox);

			java.awt.GridBagConstraints constraintsDemandDaysPreviousTextBox = new java.awt.GridBagConstraints();
			constraintsDemandDaysPreviousTextBox.gridx = 2; constraintsDemandDaysPreviousTextBox.gridy = 3;
			constraintsDemandDaysPreviousTextBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDemandDaysPreviousTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDemandDaysPreviousTextBox.weightx = 1.0;
			constraintsDemandDaysPreviousTextBox.ipadx = 50;
			constraintsDemandDaysPreviousTextBox.insets = new java.awt.Insets(5, 6, 5, 9);
			getFileFormatPanel().add(getDemandDaysPreviousTextBox(), constraintsDemandDaysPreviousTextBox);

			java.awt.GridBagConstraints constraintsBillingEndDatePopupField = new java.awt.GridBagConstraints();
			constraintsBillingEndDatePopupField.gridx = 2; constraintsBillingEndDatePopupField.gridy = 2;
			constraintsBillingEndDatePopupField.gridwidth = 2;
			constraintsBillingEndDatePopupField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingEndDatePopupField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBillingEndDatePopupField.weightx = 1.0;
			constraintsBillingEndDatePopupField.ipadx = 4;
			constraintsBillingEndDatePopupField.insets = new java.awt.Insets(6, 6, 5, 111);
			getFileFormatPanel().add(getBillingEndDatePopupField(), constraintsBillingEndDatePopupField);

			java.awt.GridBagConstraints constraintsDemandDaysPreviousLabel = new java.awt.GridBagConstraints();
			constraintsDemandDaysPreviousLabel.gridx = 1; constraintsDemandDaysPreviousLabel.gridy = 3;
			constraintsDemandDaysPreviousLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDemandDaysPreviousLabel.insets = new java.awt.Insets(5, 14, 9, 6);
			getFileFormatPanel().add(getDemandDaysPreviousLabel(), constraintsDemandDaysPreviousLabel);

			java.awt.GridBagConstraints constraintsBillingEndDateLabel = new java.awt.GridBagConstraints();
			constraintsBillingEndDateLabel.gridx = 1; constraintsBillingEndDateLabel.gridy = 2;
			constraintsBillingEndDateLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsBillingEndDateLabel.insets = new java.awt.Insets(9, 56, 9, 6);
			getFileFormatPanel().add(getBillingEndDateLabel(), constraintsBillingEndDateLabel);

			java.awt.GridBagConstraints constraintsEnergyDaysPreviousLabel = new java.awt.GridBagConstraints();
			constraintsEnergyDaysPreviousLabel.gridx = 1; constraintsEnergyDaysPreviousLabel.gridy = 4;
			constraintsEnergyDaysPreviousLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsEnergyDaysPreviousLabel.insets = new java.awt.Insets(7, 15, 19, 6);
			getFileFormatPanel().add(getEnergyDaysPreviousLabel(), constraintsEnergyDaysPreviousLabel);

			java.awt.GridBagConstraints constraintsEnergyDaysPreviousTextBox = new java.awt.GridBagConstraints();
			constraintsEnergyDaysPreviousTextBox.gridx = 2; constraintsEnergyDaysPreviousTextBox.gridy = 4;
			constraintsEnergyDaysPreviousTextBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsEnergyDaysPreviousTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEnergyDaysPreviousTextBox.weightx = 1.0;
			constraintsEnergyDaysPreviousTextBox.ipadx = 50;
			constraintsEnergyDaysPreviousTextBox.insets = new java.awt.Insets(5, 6, 17, 10);
			getFileFormatPanel().add(getEnergyDaysPreviousTextBox(), constraintsEnergyDaysPreviousTextBox);

			java.awt.GridBagConstraints constraintsDemandStartDateLabel = new java.awt.GridBagConstraints();
			constraintsDemandStartDateLabel.gridx = 3; constraintsDemandStartDateLabel.gridy = 3;
			constraintsDemandStartDateLabel.insets = new java.awt.Insets(5, 10, 9, 95);
			getFileFormatPanel().add(getDemandStartDateLabel(), constraintsDemandStartDateLabel);

			java.awt.GridBagConstraints constraintsEnergyStartDateLabel = new java.awt.GridBagConstraints();
			constraintsEnergyStartDateLabel.gridx = 3; constraintsEnergyStartDateLabel.gridy = 4;
			constraintsEnergyStartDateLabel.insets = new java.awt.Insets(7, 10, 19, 95);
			getFileFormatPanel().add(getEnergyStartDateLabel(), constraintsEnergyStartDateLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileFormatPanel;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGenerateFilePanel() {
	if (ivjGenerateFilePanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
			ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder2.setTitleFont(new java.awt.Font("dialog", 0, 12));
			ivjLocalBorder2.setTitle("Generate File");
			ivjGenerateFilePanel = new javax.swing.JPanel();
			ivjGenerateFilePanel.setName("GenerateFilePanel");
			ivjGenerateFilePanel.setBorder(ivjLocalBorder2);
			ivjGenerateFilePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutputFileLabel = new java.awt.GridBagConstraints();
			constraintsOutputFileLabel.gridx = 1; constraintsOutputFileLabel.gridy = 1;
			constraintsOutputFileLabel.insets = new java.awt.Insets(44, 9, 0, 4);
			getGenerateFilePanel().add(getOutputFileLabel(), constraintsOutputFileLabel);

			java.awt.GridBagConstraints constraintsOutputFileTextField = new java.awt.GridBagConstraints();
			constraintsOutputFileTextField.gridx = 1; constraintsOutputFileTextField.gridy = 2;
			constraintsOutputFileTextField.gridwidth = 2;
			constraintsOutputFileTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutputFileTextField.weightx = 1.0;
			constraintsOutputFileTextField.ipadx = 252;
			constraintsOutputFileTextField.insets = new java.awt.Insets(1, 9, 8, 2);
			getGenerateFilePanel().add(getOutputFileTextField(), constraintsOutputFileTextField);

			java.awt.GridBagConstraints constraintsOutputFileBrowseButton = new java.awt.GridBagConstraints();
			constraintsOutputFileBrowseButton.gridx = 3; constraintsOutputFileBrowseButton.gridy = 2;
			constraintsOutputFileBrowseButton.ipadx = 10;
			constraintsOutputFileBrowseButton.insets = new java.awt.Insets(1, 16, 3, 54);
			getGenerateFilePanel().add(getOutputFileBrowseButton(), constraintsOutputFileBrowseButton);

			java.awt.GridBagConstraints constraintsGenerateFileToggleButton = new java.awt.GridBagConstraints();
			constraintsGenerateFileToggleButton.gridx = 2; constraintsGenerateFileToggleButton.gridy = 3;
			constraintsGenerateFileToggleButton.ipadx = 25;
			constraintsGenerateFileToggleButton.insets = new java.awt.Insets(10, 5, 41, 2);
			getGenerateFilePanel().add(getGenerateFileToggleButton(), constraintsGenerateFileToggleButton);

			java.awt.GridBagConstraints constraintsTimerLabel = new java.awt.GridBagConstraints();
			constraintsTimerLabel.gridx = 1; constraintsTimerLabel.gridy = 3;
			constraintsTimerLabel.ipadx = 40;
			constraintsTimerLabel.insets = new java.awt.Insets(18, 33, 42, 23);
			getGenerateFilePanel().add(getTimerLabel(), constraintsTimerLabel);

			java.awt.GridBagConstraints constraintsTimeElapsedLabel = new java.awt.GridBagConstraints();
			constraintsTimeElapsedLabel.gridx = 1; constraintsTimeElapsedLabel.gridy = 3;
			constraintsTimeElapsedLabel.insets = new java.awt.Insets(4, 30, 56, 19);
			getGenerateFilePanel().add(getTimeElapsedLabel(), constraintsTimeElapsedLabel);

			java.awt.GridBagConstraints constraintsIsAppendingCheckBox = new java.awt.GridBagConstraints();
			constraintsIsAppendingCheckBox.gridx = 3; constraintsIsAppendingCheckBox.gridy = 1;
			constraintsIsAppendingCheckBox.insets = new java.awt.Insets(32, 5, 4, 17);
			getGenerateFilePanel().add(getIsAppendingCheckBox(), constraintsIsAppendingCheckBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenerateFilePanel;
}
/**
 * Return the GenerateFileToggleButton property value.
 * @return javax.swing.JToggleButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JToggleButton getGenerateFileToggleButton() {
	if (ivjGenerateFileToggleButton == null) {
		try {
			ivjGenerateFileToggleButton = new javax.swing.JToggleButton();
			ivjGenerateFileToggleButton.setName("GenerateFileToggleButton");
			ivjGenerateFileToggleButton.setText("Generate File");
			ivjGenerateFileToggleButton.setForeground(java.awt.Color.black);
			// user code begin {1}
			ivjGenerateFileToggleButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenerateFileToggleButton;
}
/**
 * Return the GroupList property value.
 * @return javax.swing.JList
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JList getGroupList() {
	if (ivjGroupList == null) {
		try {
			ivjGroupList = new javax.swing.JList();
			ivjGroupList.setName("GroupList");
			ivjGroupList.setBounds(0, 0, 160, 120);
			// user code begin {1}
			ivjGroupList.setListData(billingFile.getAllCollectionGroupsVector());

			java.util.Vector tempCollGrp = billingFile.getBillingDefaults().getCollectionGroup();
			if( tempCollGrp != null)
			{
				for (int i = 0; i < tempCollGrp.size(); i++)
				{
					ivjGroupList.setSelectedValue(tempCollGrp.get(i), true);
				}
			}
			else
			{
				ivjGroupList.setSelectedIndex(0);
			}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupList;
}
/**
 * Return the GroupListScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getGroupListScrollPane() {
	if (ivjGroupListScrollPane == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 12));
			ivjLocalBorder1.setTitle("Collection Group");
			ivjGroupListScrollPane = new javax.swing.JScrollPane();
			ivjGroupListScrollPane.setName("GroupListScrollPane");
			ivjGroupListScrollPane.setToolTipText("Select Billing Collection Group(s).");
			ivjGroupListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjGroupListScrollPane.setBorder(ivjLocalBorder1);
			getGroupListScrollPane().setViewportView(getGroupList());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGroupListScrollPane;
}
/**
 * Handle a string to specify an input file
 * Creation date: (6/7/2002 10:41:22 AM)
 * @return java.lang.String
 */
private String getInputFileText()
{
	String tempNameHolder;

	if( inputFileText == "" )
	{
		try
		{
			inputFileText = com.cannontech.common.util.CtiProperties.getInstance().getProperty( com.cannontech.common.util.CtiProperties.KEY_BILLING_INPUT);
		}
		catch( Exception e)
		{
			inputFileText = "C:\\yukon\\client\\config\\input.txt";
			System.out.println("[" + new java.util.Date() + "]  Billing File Input Path was NOT found in config.properties, defaulted to " + inputFileText);
			System.out.println("[" + new java.util.Date() + "]  Add row named 'billing_input_file' to config.properties with the proper billing file location.");
		}
	}
	return inputFileText;
}
/**
 * Return the isAppendingCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getIsAppendingCheckBox() {
	if (ivjIsAppendingCheckBox == null) {
		try {
			ivjIsAppendingCheckBox = new javax.swing.JCheckBox();
			ivjIsAppendingCheckBox.setName("IsAppendingCheckBox");
			ivjIsAppendingCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
			ivjIsAppendingCheckBox.setText("Append to File");
			// user code begin {1}
			ivjIsAppendingCheckBox.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIsAppendingCheckBox;
}
/**
 * Return the DirectoryBrowseButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getOutputFileBrowseButton() {
	if (ivjOutputFileBrowseButton == null) {
		try {
			ivjOutputFileBrowseButton = new javax.swing.JButton();
			ivjOutputFileBrowseButton.setName("OutputFileBrowseButton");
			ivjOutputFileBrowseButton.setText("...");
			// user code begin {1}
			ivjOutputFileBrowseButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputFileBrowseButton;
}
/**
 * Return the OutputFileLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOutputFileLabel() {
	if (ivjOutputFileLabel == null) {
		try {
			ivjOutputFileLabel = new javax.swing.JLabel();
			ivjOutputFileLabel.setName("OutputFileLabel");
			ivjOutputFileLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjOutputFileLabel.setText("Output File Directory:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputFileLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOutputFileTextField() {
	if (ivjOutputFileTextField == null) {
		try {
			ivjOutputFileTextField = new javax.swing.JTextField();
			ivjOutputFileTextField.setName("OutputFileTextField");
			// user code begin {1}

			//set default value
			ivjOutputFileTextField.setText(getBillingDefaults().getOutputFileDir());
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputFileTextField;
}
/**
 * Return the TimeElapsedLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimeElapsedLabel() {
	if (ivjTimeElapsedLabel == null) {
		try {
			ivjTimeElapsedLabel = new javax.swing.JLabel();
			ivjTimeElapsedLabel.setName("TimeElapsedLabel");
			ivjTimeElapsedLabel.setText("Time Elapsed");
			ivjTimeElapsedLabel.setForeground(java.awt.Color.red);
			ivjTimeElapsedLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjTimeElapsedLabel.setEnabled(false);
			ivjTimeElapsedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeElapsedLabel;
}
/**
 * Return the CounterLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimerLabel() {
	if (ivjTimerLabel == null) {
		try {
			ivjTimerLabel = new javax.swing.JLabel();
			ivjTimerLabel.setName("TimerLabel");
			ivjTimerLabel.setText("0 sec");
			ivjTimerLabel.setVisible(true);
			ivjTimerLabel.setForeground(java.awt.Color.red);
			ivjTimerLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjTimerLabel.setEnabled(false);
			ivjTimerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimerLabel;
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 4:27:56 PM)
 * @return java.lang.Thread
 */
public java.lang.Thread getTimerThread() 
{
	if( timerThread == null )
			timerThread = new Thread("SecondsCounterThread")
			{
				public void run()
				{
					while(true)
					{
						try
						{
							this.currentThread().sleep(1000);
							String count = getTimerLabel().getText().substring(0, getTimerLabel().getText().indexOf(" "));
							getTimerLabel().setText(String.valueOf(Integer.parseInt(count) + 1 ) + " sec");
							//getTimerLabel().setText( String.valueOf((Integer.parseInt(getTimerLabel().getText()) + 1 ) ) );
						}
						catch( InterruptedException e )
						{ 
							return; 
						}
					}

				}
				
			};

	return timerThread;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception)
{
	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}

		//Get the billing file defaults from the text config file.
		//** Do this before initializing any JComponents. **
		//  This application stores it's own default text config file,
		//	 defined in billingFileDefaults class.

		billingFile = new BillingFile();
		billingFile.setAllCollectionGroupsVector(getBillingFile().retreiveAllCollectionGroupsVector());
		System.out.println("Yukon Billing File Client Version: "  + BILLING_VERSION);
		// user code end
		setName("BillingFile");
		setLayout(new java.awt.GridBagLayout());
		setSize(410, 513);

		java.awt.GridBagConstraints constraintsFileFormatPanel = new java.awt.GridBagConstraints();
		constraintsFileFormatPanel.gridx = 1; constraintsFileFormatPanel.gridy = 1;
		constraintsFileFormatPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsFileFormatPanel.weightx = 1.0;
		constraintsFileFormatPanel.weighty = 1.0;
		constraintsFileFormatPanel.ipadx = -12;
		constraintsFileFormatPanel.ipady = 22;
		constraintsFileFormatPanel.insets = new java.awt.Insets(5, 9, 3, 11);
		add(getFileFormatPanel(), constraintsFileFormatPanel);

		java.awt.GridBagConstraints constraintsGroupListScrollPane = new java.awt.GridBagConstraints();
		constraintsGroupListScrollPane.gridx = 1; constraintsGroupListScrollPane.gridy = 2;
		constraintsGroupListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGroupListScrollPane.weightx = 1.0;
		constraintsGroupListScrollPane.weighty = 1.0;
		constraintsGroupListScrollPane.ipadx = 361;
		constraintsGroupListScrollPane.ipady = 81;
		constraintsGroupListScrollPane.insets = new java.awt.Insets(4, 9, 4, 11);
		add(getGroupListScrollPane(), constraintsGroupListScrollPane);

		java.awt.GridBagConstraints constraintsGenerateFilePanel = new java.awt.GridBagConstraints();
		constraintsGenerateFilePanel.gridx = 1; constraintsGenerateFilePanel.gridy = 3;
		constraintsGenerateFilePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGenerateFilePanel.weightx = 1.0;
		constraintsGenerateFilePanel.weighty = 1.0;
		constraintsGenerateFilePanel.ipadx = -12;
		constraintsGenerateFilePanel.ipady = -26;
		constraintsGenerateFilePanel.insets = new java.awt.Insets(4, 9, 9, 11);
		add(getGenerateFilePanel(), constraintsGenerateFilePanel);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */

 /*
 BillingFileFormatPanel is best run through BillingFileFormatFrame
 or com.cannontech.dbeditor.wizard.billing.BillingFileGenerationPanel through
 com.cannontech.dbeditor.wizard.billing.BillingFileWizardPanel
 */
 
 
 public static void main(java.lang.String[] args) {
	try {
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());	
		final javax.swing.JFrame frame = new javax.swing.JFrame();

		BillingFileFormatPanel aBillingFileFormatPanel = new BillingFileFormatPanel();

		frame.setContentPane(aBillingFileFormatPanel);
		//aBillingFileFormatPanel.initCollectionGroup();

		frame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				System.exit(0);
			};
		});
		
		frame.setContentPane(aBillingFileFormatPanel);
		frame.setSize(aBillingFileFormatPanel.getSize());

		/*
		If BillingFileFormatPanel ever needs to be run by itself,
		then this event handler should also write default values to a file
		*/
		
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0); 
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} 
	catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Handle a string to specify an input file
 * Creation date: (6/7/2002 10:41:22 AM)
 * @return java.lang.String
 */
public boolean parseCommandLine(java.lang.String[] args)
{
	for (int i = 0; i < args.length; i++)
	{
		String argToLower = args[i].toLowerCase();
		if(argToLower.startsWith("in"))	//as in INput file(full directory path
		{
			int startIndex = argToLower.indexOf("=") + 1;
			String subString = argToLower.substring(startIndex);
			inputFileText = subString;
		}
	}
	return true;
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/2002 10:14:46 AM)
 * @return com.cannontech.billing.mainprograms.BillingFileDefaults
 */
public BillingFileDefaults retrieveBillingDefaultsFromGui()
{
	java.util.Date newEndDate = null;
	Object tempDate = getBillingEndDatePopupField().getValueModel().getValue();

	if( tempDate instanceof java.util.Date )
	{
		newEndDate = (java.util.Date) tempDate;
	}
	else if( tempDate instanceof java.util.Calendar )
	{
		newEndDate = ((java.util.Calendar) tempDate).getTime();
	}
	
	// Get all selected collection groups from the groupList scroll panel.
	java.util.Vector selectedCollGrps = new java.util.Vector(getGroupList().getSelectedValues().length);
	for (int i = 0; i < getGroupList().getSelectedValues().length; i++)
	{
		selectedCollGrps.add( getGroupList().getSelectedValues()[i].toString());
	}

	if( selectedCollGrps.isEmpty())
	{
		getBillingFile().getFileFormatBase().closeDBConnection();
		update( billingFile, "Please make a billing group selection." );
		return null;
	}

	
	//FormatID, demandDays, energyDays, collectionGrpVector, outputFile, inputFile
	BillingFileDefaults newDefaults = new BillingFileDefaults(
	FileFormatTypes.getFormatID(getFileFormatComboBox().getSelectedItem().toString()),
	(new Integer( getDemandDaysPreviousTextBox().getText())).intValue(),
	(new Integer( getEnergyDaysPreviousTextBox().getText())).intValue(),
	selectedCollGrps,
	getOutputFileTextField().getText(),
	getInputFileText(),
	newEndDate	);
	System.out.println(" end date from retrieveBillingDefaults = " + newEndDate);
	return newDefaults;
}
private void setBillingDefaults(BillingFileDefaults newDefaults)
{
	getBillingFile().setBillingDefaults(newDefaults);
}
private void setBillingFile(BillingFile newBillingFile)
{
	billingFile = newBillingFile;
}
/**
 * Insert the method's description here.
 * Creation date: (5/14/2002 3:58:56 PM)
 * @param newFileFormatBase com.cannontech.billing.FileFormatBase
 */
private void setFileFormatBase(FileFormatBase newFileFormatBase)
{
	getBillingFile().setFileFormatBase(newFileFormatBase);
}
/**
 * Insert the method's description here.
 * Creation date: (8/31/2001 2:57:51 PM)
 * @param obs java.util.Observable
 * @param data java.lang.Object
 */
public synchronized void update(java.util.Observable obs, Object data) 
{
	if( obs instanceof BillingFile )
	{
		System.out.println("...Ended format at: " + new java.util.Date() );

		//kill our timerThread
		getTimerThread().interrupt();
		timerThread = null;
		
		BillingFile src =  (BillingFile)obs;
		src.deleteObserver( this );
		enableTimer(false);
		
		getGenerateFileToggleButton().setSelected(false);
		getGenerateFileToggleButton().setText("Generate File");

		javax.swing.JOptionPane.showMessageDialog(
			null, data.toString(), 
			"Yukon Billing File Generator Results",
			javax.swing.JOptionPane.INFORMATION_MESSAGE);
	}
}
public void valueChanged(com.klg.jclass.util.value.JCValueEvent event)
{
	if( event.getSource() == getBillingEndDatePopupField())
	{
		java.util.Date newEndDate = null;
		Object tempDate = getBillingEndDatePopupField().getValueModel().getValue();
		if( tempDate instanceof java.util.Date )
		{
			newEndDate = (java.util.Date) tempDate;
		}
		else if( tempDate instanceof java.util.Calendar )
		{
			newEndDate = ((java.util.Calendar) tempDate).getTime();
		}
		getBillingDefaults().setEndDate(newEndDate);

		getDemandStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));
		getEnergyStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));
	}
}
public void valueChanging(com.klg.jclass.util.value.JCValueEvent event)
{
}
}

package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * @author: 
 */ 
import java.util.Date;

import com.cannontech.billing.*;

public class BillingFileFormatPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.util.Observer {
	private javax.swing.JComboBox ivjFileFormatComboBox = null;
	private javax.swing.JLabel ivjFileFormatLabel = null;
	private javax.swing.JPanel ivjFileFormatPanel = null;
	private javax.swing.JList ivjGroupList = null;
	private javax.swing.JScrollPane ivjGroupListScrollPane = null;
	private javax.swing.JPanel ivjGenerateFilePanel = null;
	private javax.swing.JLabel ivjOutputFileLabel = null;
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
	private com.cannontech.common.gui.util.DateComboBox ivjDateComboBox = null;
	private javax.swing.JSeparator ivjSeparator = null;
	private javax.swing.JComboBox ivjBillingGroupTypeComboBox = null;
	private javax.swing.JLabel ivjBillingGroupTypeLabel = null;
	private javax.swing.JCheckBox ivjRemoveMultiplierCheckBox = null;
//	private String[] billingTypeString = 
//	{ 
//		"COLLECTION GROUP",
//		"ALTERNATE GROUP",
//		"BILLING GROUP"
//	};
//
//	private String[] billingTypeColumn= 
//	{ 
//		"COLLECTIONGROUP",
//		"TESTCOLLECTIONGROUP",
//		"BILLINGGROUP"
//	};
	
	
	
	
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
	if( event.getSource() == getDateComboBox() )
	{
		java.util.Date newEndDate = getDateComboBox().getSelectedDate();
//		java.util.Date newEndDate = null;
//		Object tempDate = getDateComboBox().getSelectedDate();
	
/*		if( tempDate instanceof java.util.Date )
		{
			newEndDate = (java.util.Date) tempDate;
		}
		else if( tempDate instanceof java.util.Calendar )
		{
			newEndDate = ((java.util.Calendar) tempDate).getTime();
		}
		*/
//		if( newEndDate != null)
		{
			getBillingDefaults().setEndDate(newEndDate);

			getDemandStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getDemandStartDate()));
			getEnergyStartDateLabel().setText(startDateFormat.format(getBillingDefaults().getEnergyStartDate()));
		}
	}
	else if( event.getSource() == getGenerateFileToggleButton())
	{
		setFileFormatBase( FileFormatFactory.createFileFormat(getBillingDefaults().getFormatID() ));
		generateFile();
		getBillingDefaults().writeDefaultsFile();
		repaint();
	}
	else if ( event.getSource() == getFileFormatComboBox())
	{
		String formatSelected = (String)getFileFormatComboBox().getSelectedItem();
		getBillingDefaults().setFormatID(FileFormatTypes.getFormatID(formatSelected	));
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
	else if ( event.getSource() == getBillingGroupTypeComboBox())
	{
//		getBillingDefaults().setBillGroupColumn(billingTypeColumn[getBillingGroupTypeComboBox().getSelectedIndex()]);
		getBillingDefaults().setBillGroupColumn(getBillingGroupTypeComboBox().getSelectedIndex());
		getGroupList().setListData(getBillingFile().retreiveAllBillGroupsVector());
	}
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
private BillingFile getBillingFile()
{
	return billingFile;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBillingGroupTypeComboBox() {
	if (ivjBillingGroupTypeComboBox == null) {
		try {
			ivjBillingGroupTypeComboBox = new javax.swing.JComboBox();
			ivjBillingGroupTypeComboBox.setName("BillingGroupTypeComboBox");
			// user code begin {1}
			ivjBillingGroupTypeComboBox.addItem(BillingFileDefaults.getBillGroupComboBoxString(BillingFileDefaults.COLLECTION_GROUP));
			ivjBillingGroupTypeComboBox.addItem(BillingFileDefaults.getBillGroupComboBoxString(BillingFileDefaults.ALTERNATE_GROUP));
			ivjBillingGroupTypeComboBox.addItem(BillingFileDefaults.getBillGroupComboBoxString(BillingFileDefaults.BILLING_GROUP));

			ivjBillingGroupTypeComboBox.setSelectedItem(getBillingDefaults().getBillGroupComboBoxString(getBillingDefaults().getBillGroupColumn()));
			ivjBillingGroupTypeComboBox.addActionListener(this);
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
 * Return the BillGroupLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBillingGroupTypeLabel() {
	if (ivjBillingGroupTypeLabel == null) {
		try {
			ivjBillingGroupTypeLabel = new javax.swing.JLabel();
			ivjBillingGroupTypeLabel.setName("BillingGroupTypeLabel");
			ivjBillingGroupTypeLabel.setFont(new java.awt.Font("dialog", 0, 12));
			ivjBillingGroupTypeLabel.setText("Billing Group Type:");
			ivjBillingGroupTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G2EEC8EADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8BF49457F5F2224454D6E25C10E362129EECE31B5844D19B5A101A34B801965724A1095D50147AA8B1CDE8C29252280D1C036B11044C9AA490A08C9801D6C27C45EA7543E2917ACB6833C80A3400B04430520EF6D7DA6DAE3BA39098125E7B5E5CB7332BD9FDF05D63681C2B19F75F7D3F7BBE735E4CCAB9F933B3D253F2A4A9B5C3D27F5EC915A40FC7124AFE51F909F0565D71A6C977178F30D5EA
	28DC8F57DA28DB9497EF4A162A2EA211834A53C371261DF03FDD5A345BFA91EEE268B1C1DD613F3F7B1D71753CEEA1BD9BD859FCB2932E1B81FA81F79BE069EE54DFF9F2170A1F8D654D6ABDC2EA1AA45DFE9BE94EF9F7697019EFF33AAE383AG9CFB102667E42EDA3F98F0AB818E833EEA0FA673CDE65DA7EC15EA797203332564FF5C1A16053AF47289E6293C0B542BD97A9C8BC43A74232A8A38D6F58D392B7B834316FE275F9F70ABF23F47526775793CFE37E558697587C381F748B99C36596AB936596B13
	B1DE39E86F7BDE9313A5C9A9A0FFA1437D4A0BB1D260BE81E08EG37795BD9D52ACDB306E6CBC90F2D0FE93334175BDA38D733F976A73F117662DE14FF1B3581723920BC8B007B632E2C3DDBFF1A8321E1275265744BBE6E57621364572EFD645727BD28378B4A9E0038FD645769C364D76F7BDF12D27A4A53364470EB66BE35AD76E9FE2DBC30B3E3B74B2D7B2D189F6B204E1C97EFAA87688598851889300765F4F371D1385675A98D016161003FAE982C7739C2E783AE598777B69B542838EE39BF90F2C9924F
	5BB8EF1101BD8409F911B92B6559850972ECDE779312229CCD4BB070ED7ECB5B53962BB2D664E93E194C98B777A046AD857029EACCB97E9A435B4D9A1E376F2BACFF30FC996A9619A94E3D1B49967B7A07256C1293E99B8CEC197975E3E905795C062AFC4D969EE33994E36C81FCBC40AC0079GAF82A467D30C279717CD9463D6482B105F696B99698BBAC3CA7D08576712C3EA2CBFF6F23CD89B81671BBEE5BE9E1739E57AB6DA79658A43382C1C7BF6DAF6810F476182ADAEDC7773B1F537940C4D0D4D228D53
	D8DBDDAE08EE5BCC067795C4376D8C8F356DED28CB82606D32399C473D467D71262BG23G538192811681A46D273679576262C966FF172EDDB86F8C6BDBF015C7151033559FD61C7EFE793CB364F5767964302452FC5E0A71680B60FB14754BFE50F1C6764BA127628D78C13AF3D806A488F31A272DDB11A6A433DA07F7D8868543C10964FE593A9D63618CAB6702AE27A2EB943C7ED9AB3665F567F9108882605EDA0871FA080D2F925C6796D22CCED5A0CE01F2C2A14565C449B63836C3D24ABE1B2D515B0FE6
	B8C3B7A9370687F19E1BDDC8FD69673D14B30DD90FCBC5AF159B668C4202C25E7E4B8B23F366861319D2C8F3636B5E5EA966EFB44CAEB44EB52D0F6C9A7C20BA52472963717D42894C51DD87E08C86F09C2098DFABB1735C2BF706E54A364E90A40A6C52A24EE97D25B8874857E53F52A887E53F8BEE42EADB0F143E296561C0C064487B8C0F63C983A4CAD8606FA95D4A71A0C31F55D47FA72C5E29A8A1EF5F08A277480A82531A467FA033A59CB1D405D5DF1FFE0775D370B19EE00670756D326D3AFC328782C3
	A3C1877694B5AF13587CB89768979D2076B7F9B19FD60972B6D6566A9F76FC1036C339930F21DF7EC0BA464B0D4DA21E3B4AE337EDDE39F15B9EA8B7EE5BE3656337DDD939D15B71BADBB94E45019BFEDDAE541463B8509F707BE5161002C562EB241FBB508F2F5F2BDC703A5C3252633D850B39420358264F3031E69D44B97BA00D93B77653B8D1F86B91E95115AD06737F4C47ADE91E033CFDEE9F544689EED3ED8566186C1307A1D08D01913F222EE558B8266243698C776F6FEF04EB5BD959BF522A4843A554
	A7DF2EC49933C0F6A24052031427DF2F582D4BD335B3B684FCBE6EBE54E59FC25F668E66686CCDBA48576EDA1BFE69C4B646D93EB6A2433400095EAE5FD447763FBE31AD2A6E3C53B7224D99FFFBE29376A1DD9F61783FF847586F2F3C037DE2C848FF761DB7A49C7B39CC5ECE09CE14998CF907E1C841CE2FBEC37C7A247E992297FC43670D5BF01DFE085641BFEFC1DE7DEAFDAE0B419231D647B91463D6B25C1350F972B2E8BDF1B6DB7FEC618A9FB2ECF304B507F88C2543516B2C6DB614EB863C75B035D5C5
	25FECC298B07837DDEB6DD2A71DAD5294F870E7B0FCA4C0781EFA88CEBAE17BCCAB17FC5A55A0A73290AD663769FEF95C8E35787CDAE2F92880945DBBFA6867775CE337E19E461165C34CDB1FCEDD4FDF469FC6D1D6DCA67FAFFF5845B05BDFB8CBA2FBBC7AD619B90FC1B2DCD77CC61E4B1C17AB907691962D327B1C7D6C2B999A07388753B8B257AB6D81434A7E69B949E6176D49E09EE0347CF31E7E240FB0ED09B8C1E5C2AEB83F8C600E5101BBD4F29E31BAD1D6726DA4E35E5D0EECA5497FFD805FD69B648
	1C8910883094E035507118ED4275F35368B86B6781DB74DA54F4D49B7BF30F0E1D87781AB3359AE3E9BD4A571C99821F49700DBABCDF0BFE06459E4B3DD03770A82D2FD22BD96E9E035CBDC6BE3DE64B52452DAB6075ABA2E6CF1D427E9B88F95DDE3F98CF988E1E4058604CF1BF290ABDEF7C324AF85E78F515713C71DA5578EB020C53316695BEFF5FE139D88B7EB950CF06FB378AF123D01EFE0C666C8DAC9673213CDC6036B15CDAA86794916EF3276FE65EA5399351F19DFFF9D7BA5255B61E080E476D0DEA
	4C11D1D15E484AB7D5B9708CE143F675B9EF86C6A0816694E15C1E365118DC8965AE00D10004A23EB6626343DF5C40F1E5G9E9A2099FA02F23F94F71CBD83D7916F9A2017GE7862885388A40F57EF69366CD54B605EA4FCBA5584E6AF3090CB43C16761C9A2B23678B34E9A948DE8940E93E5AEEC4138C75A68BBC9B333E72DD9B6D39188157EBA13FCA2E62F31B9877AC4E9B0A4DF6A664F555BB5DF696A5BE766DE1E39172CF37505857FA8AE5AE05F292C00A05463E5103B4A64FFB62E1499ED7E138CF1464
	48CD4BAE6639D3DE2C0DFDEB8F6C4C60BA9F2AA21DC1281FDB4C6640AA7DBAGE378E7704F82D7DE7F6B08FA0CAD76C52D7E48E9FDBD5ADB9141FFA9229EE37495151F7B76428172ED5D8BF3A55FDF6D0F398736D675A9474A2FEBB76E110A96FFA30D7BF61B75C16C97F6AB4533166D0B45C3F99640FCGCE7B4BF622DDA6E8DFB6A36D7A92F82E84A8AF21FE7CAFE3465E49754D9F5FA51F7954D47978183E270A6D9D1570B1BDD5608B993ED70767795BF9CA6C99C25D52929A6B9756E06E9715461BDAG2E82
	0C824C8418DFCAE37F8355D6E902794C91885AE1E0776906E26E5F2C2A3B5B53F9EE8365497C3FF9D4DA7049D7D2E32DCB1616727CD82DDE273D39076139FF1DF5685F2EB2E8E300DE00A0C0BC402CB2726FFB55C55CBFDCA655A90A335F43D6E0645CF92FFCC3C3636045FD3B27DA6FDBA248DBDC260D65AB4AE24D533B58D832260C3751DE01AFE6F8EB390667ED574C729A4B8A54DD2D20DC7D6FF63DFE7B8928BB2169D4CE44526F4FC7F953CEF0BDDB857E8F8CBFDF07677AD7313E0265A428733DC37A7331
	0954BCBE2A3BBFF76A036ED1CC5CEF7C9F2A8E1E97CBEAE22FD71655982FD73ED3E33CDED9D1B37EF3EED2CD6C675C9F57681FF3F9DEFF369E475994E80B1C4AF8D3A5009D60EAA56574C82D7A9CE0104FCD70605B90E0BB008A696F2D0B6D6B153A09778DBDF5781C5297D01440B04AA73C3F8E5BCB898499126342F598A3D8AEFA94CE2A62EF5661B86D1387981A2F49E632F17EB678B53D1256E94BDBE86DB6DF605A5AE86D36221272F39FBBA3BAFCB25E54886082C8GC884D88E108C10EB0BB7D581DC86D0
	GE682AC84D8E9A39995CCC6B214F3CE41988260GD0GE6G4C87F891A099A0278A6A819CG8A404CAA12E1BF1984D754BB159CDC2E23C7FB8ECF129FFDC2DF499F748D71687BC2759E6301FEA7CF5126097B653D4C57CDD3644D525D63B8B751BE9441D6D56FCE35DF6C1034B9E40FFA1F41F2CB12367C9F790940F3AE4BCE3939200A72705E33141BAB85AE3513F2D3BACDB921641B615A8565A0408C0085G4B8156G182A213D2B0976930C568EE59FC08240BC00E5GEBGB2EB62CDC5B5C47B93F39CDCD1
	5EE4FD892A3A8DF3F09CB9E8BB5A087726EA0DEE0D70B1ABBFCEED1F495A1221361DD15DF8B9152E66426628F64E124439C6FD6C71EDCE7D44635B976B595E8C0CCB0E1BC119637E321EBD778E8C04E5B10EFE25BEDB60DA43DD013097B7F878334A172A113EAF90E28F2A7C596979E6FA7630C39B3AGE6834C874067CBCE73EA8B51AC2B614FD72B601A2252B456323D153646F162C3EB41F49532012790E007DA2E2C55560B5C569597A31F396C4EBE714CF52AEADBC49DAC8B7B827501D162BDDA0DFD31D19E
	F67ADD0D4E1B612E10FC5D9B9889BBA0440CCC257B07DBBB8D69743AAE8CE25FD577721A10CE117575C7CBD1C693CC0D6E1B31E42CFDFF37A1CD34BDCF0E6488FBFA94180705B2DE7FDAA0C748B12A1FD34676D9GC991617841ADBBB49F4E0482A3C14CA1EEA169CE5C364B00A6D2CED6AF4602D55A3DE12527BF94707970F1D715D1DA01FD27C706A504D38960DA016F7D06ED7AE73204DA1EB70918B72C7E5396FD7DE235FE0D28CF592C2F176AF87D61BA2ABFDDECA43FCA5497E79B55F7097AFF5EEED46F92
	75E9B9C67AC7C5FD7E99A37E84D17FBFEDC67533C57D7CF6FD7DFCD5FE32286F49B362B755D37D13E623786556537EC2D6447E02351E57DF06EBD048F8A1DFAFE31ACA13D8CFE34089BB49D8DC4F470915C8D34F7720B8CD62D922B1B570F1428C577286CE434709DD1D930D9369BA30836F5586ED1CD09A2247099FF8B0A7BBC714600802A3827629E62F6CF32975B5DB5F0A282F8F85EE04657A91D8A77AA551BF51EE7D9E0EA360F67BD4AACE73C39F2EAF7154B824F69C0E7FEEDA360AEF72B903E159256B0F
	160DDBA2F46B47047FE467FB2D38A60649G7ADC03C76E9F42510057FF3C8FED6A160783576533A3BE459B74F965909171B67BDAFEF4DEE21C639A28CD57C5645D8C359D960A7A67766B6B172B756BC47DA78EC6679D56E7B7D27DFF49C6750522FE48EFD4DFA96A5787747576C6DE2F087AF59E237AE90D34A7766499FA17E196629AE9CF6CEB9BF13F4068BD24FE3601E0F10FF8ADA30A57E733B93C8A34B31B9179DE4DAF4E501ED5E6936D556CEDE163AA14AF83A8CD24BF3F1576B492G37380976FB3C2DA4
	E31D10719023ADEF0667A4G97C01E4457055ACBEFEBEB0CF0371B713D386F34E2096FCD7CA01376A6F21F7EB32971FB2F246E170C77A696B573F5D17699EDFF2EFC43128C754CE4A0461989CBD46E4BD3B65A3BEAB9A35E73726DB1669393B167DDE2217316B841FB60F02EB12F68381C37F88B69DDF006622A9493EE2D40DD4BB629F1B95CC2382C9C223BAAF0A7F34816198257FB0668168BDC05582BB335124D9FEFAB19ECBE69A7DF095E517BE9BB1D7B5536121E7B72486ED101FB59CCF409C2373BE92FF1
	3CF433AF696992362FEE253C7BE49E661D398D7A9DC0D79B49FDD009518EE27A66B2538A48A6CF9B657DFBF66AF7338137220D6CFF42CEF91F5DCE7ADFE434ADD03E8AB05ACEB2D61C253EB39DF0CB5BC9063903E4C8F6125151013415D06E8270580D7A0EA49961F85F29EA17C40C1273491FB8BB45E2CB4FEE63D850B421721635D38EAD903CCF3C1797A31FF5D3084A1F32DF642F1D7CDDF890F1B8CF9CBECB382281446133C30240BD6A973CE7C9F77579D87D1066924EFBAAC03C07BBC8DE19D774118EFADE
	7A62190976CAE7F6C4BE0FF13EE7A7645B97D566FC499372C5835FA7FC100D4773BB22771E9FE678C59D51FB4F7BDBA85716217F1D646F21563B5BB32F38CBBE7F147938DF1B5A507E991D517E66B07C6C4EE8FFBFAF6625F9D0175DC567BA5F3A24BF534EFEF8D39A71C56BD53AF8BB47F5C51FE9876C38FE1B8F7865G49G193F05F59EC0A5009D60AA008210GB087E08140B200A40035G6B3B63CD3B3AE91FF0E75B91381EF78E0EFDD3G106AC1965BAC8CB97919AF0F6517A24EBB0A3A391D356A5551BD
	F65F0073D92D4867B128C7389D132FFC8B72CD0F41B787708B3A0D7A464DE23D1D4BD41AA4751A9CD35F75EC64B375707ADC75EAD52F0DBD317836656875752AF4BE759A97132F1A714DB028C7189B136FF791FDF821CA37DC3D2E0A49E70BB8BBC8D169B29DEA3EB9E2712DED9F2F8F154764FBA0CF4F572252DDD62FC1759A2F6367FC2F184753B7B3263EF34C4EF9D03F480066C5839C6F533FE17375EAC7747B3C6B5999C80AA37AFD5E04BC1A0BCD6762CD3EF3623D0102716CDEFEAE165DAE7B5D4D812189
	790C670027A764B30ED38BDBBF2CBA97BDA67E1661571E0B9E933FEF27B1F19D54DDBDCFF1FA796C5D0D65EB6E126FC0475D797BF53686DAFCBE5A5F9798FE7979E8FF9D9D646FCA282B3DC07E3E1ABF1E7EB997E265C525E13FDB2052AF0B49775AFEA33EA415BE992E26DE23FCFA6E205179DA2ECAEB05EBE32F71D8559B0577C1B98EE0C6EFF45FAE99B052B1D7C737B0060E65D178D5D0CE81483C982D63C93F5EFF33DADF2EDEDBAE460ADBEDC04FF7D9258B2A577818FC15915F194CD469662957C586FC7C
	3CE351F6DCEFFA7DBE2F9F9EE0581B76C266C3AD12381F2D3BBF5272611F33567FBF68585A267974366E7E31568FDF77428FD587DF333DF496FD626B2D387683D20C75D6DD1F52860F911DFD03F23F62F03A854F476D314FAE6E33CFFCF671993B71596C676CE34FE67F5CEEFCB6BB472EBF1B6538AF5871B9488DEB44DEC1770C40DD9438AFB319083B60F5A99E017FAA338B71ADB2AAA47C5FB13BEEB0DC2F0ADB2C43DDD4F14F5B71BC85DC768444BB067FE447F70B873CBE1FFA9E4FE83C7E302C08F8FC0F59
	688DBAF5F62F94B8341B1F5B7DBD5BFBF9917A6C2A0BF41677D0B762D220ECFA17F06B3A69FCAFC7604ED891670272ED01FB46CAF453842EFD8B512D9038F9DB08EE15407D0C1DA116DF0AB735DCA25C74E2324FA1F0770548BE0F407DB4C47294017B945B37D98C651582171CCD72D68B5C37EF113CF582D77ABE49B3DDA65C91363F538BE50F405D39434EC7211CA0F0BF3CC33CB3856EDC8E5932DA607EBA07681285CEE17BC50D3F83FFFFC73804B062BCD09E9538DF0609B7DE60FE5BCE3CAF8ADC13023855
	D0CE9138279462DD7F9E61B65889679038A7587E519CA84F973833BE44AD05728A017BC90BE8CB012BE77BCC65D7C05E95427D46CCF49E017335D3EEAC9038EFB25CE2A82F963803423EF5D7E81DA119319F582FD2F9A3AB3BC479CF73BE483807F2A79EDB1F7EC0BAA6FA87007B3E2FC03435702D030D377C7E026EBE497E614FA72F7EBF68D85A217974ED5D7D3B9D9F3E6E70072A0337E936E853972FD20EF73653B8D468A4DCE2BB51D985AE2790F1F1D01EAEF0B78B09EE1640D53371F48D145777916E9536
	97388B4A07852E65906B2BD0EE9138878F916FE5013B436600A01463846E3D5D24F70640B54908DB85659401FB0061B27B63CDC57D046BE1FB1035D06E92388583C22F40EDF3B33DD00E9338810B50ABF0BF9AE6FA211CA2F0D67FD83DFF6B27B9A053C5386D2594FB3340A9252423C860AE30FD4E5BD0CE9038F5C5081B8D65C401F39EA75E0582574976C3B365F8D32EAC4658230833C239D660B69DA3DE3BCC6F077F1B0D4D79813F32C2C61ADF31F9FB965C4F95B44FF4A30D828BA53EA7DD51097D061F09EE
	6F194A3B933983E063G5FBFC33CF5A07ACC74BDAB3E2B228482BE07B70867AA6ABE78D3DD1A4EA55671F4262B72B2547BAAD567FE9557255349E56F6F56E4FFE34BC432C9AE7B46DCA71B3DAB8E778FC578333E8B57D0434E907B3E036BEB6EC1FDBC5EDFE56B105B40B78D0057FB9D1ABDE96109622B37A7BA3E8B4644F7CE36F1FC9FB9275934F7AB1BA701F73930297502E6D36B7BD3B9870F36C9F2C75BD41B93190367597AA59368F639A987E75C094C410F75EA763CFAE7A2FBB60CE3CF0BBBBA9F9E3F28
	497EE64EC47920176DF0C76603479D1D8F771F0974759136AED300AECE787AD8B85257256FEA761814716C49084A07C4F7E46EAF56794A755FE27340F2402F947AAFAB117AFFFAC9537F5C047A75714874C46AB7FBB47DDC5EBEBB49AB720CDD97119C722996E89C9E7ECE8562BC1E68DC7ADADEE4FC3F4B561A8A5045F9483FD9AD117EED7E1D66DFEAAB5913E8E08F6A592433EBB150A0D0AE2D98E34FC5B31E6B41C316779658647455793CEEBF3ED12C46BFEC263354D59EFA4FE4EBBE59106DB50E49A6DDCC
	8A3D5C860AC925B7BA1FCB5E1304BD2B5A711D2F6BF2C87176BBFD7CA0365E996A8A783C7DB7D51BDEE7B901E79B0DDE326934E827F8831BD0D7162E6DE7824DBC2F766E4BA2EFF4DCDABC6833F89FCB22772DCEC53C8F344C4B5F87DEAB6C28A8A0BB568F9A5B71264E8EEA9F0A4D2E41687CBBD4B01931CC449A78AB87B5F9764168D84F3C226552BF34CF54D77463E425AA1B460E5E4168189DAB0C4C5BE72FEA3A2697CEE51C2A5479413EEB99236BDCA77BF6B690125D50C6FE970D9743EC6D97847A5B0354
	1FFEF3B0522E171DDA3EDDB904FA2FFB43DE7C36194B38FE084E31E7018C0E7B9B061B876585C26E0C6E483E7C94DBA72E007AD503148FA33B27B29EC57B1DB3946D77E59969E5BFEE8239280F4AF42EEF9EA25B1E10476F570B84CF3960999C080CD3EABF4609537FE0007CF7897AA36EC87FEB58FAB59E6A278F117FF7ACF7637FCE5536C5E37CFFEDF82C7FB9FE72E5599035570F99AE894AEB06E80DD0685272717ED232AB57E75CCF756B84AB50A0D03BB47A226D7287A2E3914F564BDDC0F759C7715AFEBC
	B23E76814D1E5163E4CFC28CFBB28C6C213EB8FB0CBD835EC8FBEE33B578BC20DBA06C795A51C8FB82EE4D1E02E3E44F6A9876EC98471EF53E68F127FE2356676E6F466FD77C728DB3FDD6E9E986C38F97383CCEDF40DDD0E4369619CBCA0F50BBD933EF684F56ABB766477CDE5CB42C1E692A574BD5FB46F9F7E59ECED3867574DE5837B20D6B097FBF33347B0EEC4D3F5F07A66B9F673D36D513D3132349D9F2EBEAF27AF6686C59254939F6E70A766C9E5F6F99EFEB77EBF3B5BDF161496A099D73FD7BB459FF
	A854E41FD4BE386CC7F1FBDEFDEF27136D2BE19EB906A77B3EA059356420AE070EE8B65679A6EBA337213415ECC8B03401F4FC6B282663BE33992E3AD712ACBD781FBE798ADB3ADB4F34B8387C7AFC123FB8060FEA9E9F53643FE5AF0F122FFB9B4EF23E295B515AD0E73F5253505DE9375757F5DF296B3952515454585448F57AC5DC57CDBA2EE4470E6312C4635C8BC7F08EB0FAFF92D7E3DCD7C83C53D76E27F72F868AC83F4FCF63FE258565CE2D0DAF95EB773F37EA770F14E8779B4A34388D3177F9C53BB7
	84FC0110254F676C9F62751FBA44465A50084CF9074BB55E116EEDE3F9C3B03D29FA1EBF2151F66F1EDADFE6BFF7C239A3E3DC861C3E30ACF05F1373474F2706268EC7D337205F689E5FD61FCD53772EE5EA36BEFCCA63BD66575B4A6B7F7B34D63F21F4EAFDEDE1A965459C7FF8FDEDF70DF6EF53793236E8EA7A3EF01C742D94F9F82CCA13FDEF35A67B6123D3135DF30CE46784C876DFB13B7919E4DFAD565B214E9320F7416E5D081DBC563BE0DD21C0909E39EEF2AC7B85A0F5CF5891718E49B410B7ABB019
	7301F7A25E47CB841E451362BB93718E458A60D9B3A93EF0041DD2B05E14931C8C5F0DF15FC1BAECA003738DC53C83D485F4DD135277DC291E4F85BC231362FB66381EAF81F8E6CF0A2F77181EEFBE70AC1D000F670B3B966F45B74DBCA5683B5FDA9C6B70F736A0BF3D37584F9C75G215F4D77F107589C30B24857009C97E4386488DCC76BD83A1E5631F46F650F256B4F9FCBF71F015E878C74FEDA1C47E5DF233D670F950C653D37E0AC6F0A5231F47FD6BA16EE497131F47FF8FCACDD6058D8BA65181E0E4F
	074E0319DA4C59D1BB47670EE6E9F8BAEAD7FFFF65F1B3FDB3D48EFE36GF8G26814C3DC673658BE69ACB16DE235F91C90F7876854728DFCA7A5F812908284745EBEAC43DBB229E5770F5297A7A4FC554E76878F94EAE0F6813D2887AF2E87C1C65F17B3119E2097AB8EE0D19E209BE705FCE3ED9D688CC73FEA6E504C35296B16636329789122E175952DFF9ACBDCDDA5FAA3D41DF2CE81556635B912DD286FBF922D5CAD78241512024EDE8B288DF13FE0F86C7E295A993BFD0B2736FA9ED0A3427C746DFF0B3
	2B9F841AF53596C5AA6D98996E13C3664000993FE7B453870DE6C7405CA3BBC37D9EB37F0A515CADBB71632630C5680D78D3249DAAE1049CB0G2A361272A6D62F48005E413FFE9CC3BE890BD4CEE111A2ED9C9EAEF6390AEF42DFD4E1BB29E61BEBE67C9492146C0F8A0A76B3F71C6EF07888C8CD37D860FF96FDCE46C227C899D6F398E825CC7CD44C2CFEAB26C8EF712F43F8FC9B3DA1A08904EE022E4D7C43B03392D085EC655F0219350F41A0B132A2A663A50FD47CE8594B674369529B2D52EEFC2E6E11C3
	EC7813713BD57AED53E6694DA07EFE967E74A0E4CD18DDB2424ACD1FEC17F2863A71F776420A6C2A2301959633BB6C282998BFF2A9EEF106BD8A6EED1C32CB6B7D78AB2C52B6DE6DF37A5D45FC14BD45D2AF352E1635B3FBC885FB37B3FBD10AC5C8597E6C168379ED2B9624CBE94D52C6DF4069EAF6E294DA25B795CFC88EFB82BE57E830068BB25D402E42360FC15C9E61BEBAEB412398E1745068679332D6E7B52741A89A120A99A5EEA3D884052B41638C01AAB9542A486C1317C2D1F74FBE6560B74F6C3C1C
	A6252ACEE27E6E9E8DAA529B834EE12F8F12640DC63674E1193DD781EBC3CEFFF8G7A88D8E60E320C2A04824BE71E37FE768B3FF917A950D895A90F0792D80B5DF2E03838EEE0406B1705G08AB085FAB44A30DA50AE65B6803BB5ADEFA3D8F7220D1CA9F36DA597F92763F947F17B0CC89431414C2F0570E6D1A7A3F687AD17219A50F98203079AD4D50024A77FF767C237FDA35F68DF857ACE530DF2F03C6485628F8F77D643C4F6E5DB4A369FEEC23A0264AA84A4D9FB59E24260C5DB9A683913BE7ECAAA1BA
	CFB4A8168A98E9A17BFFE84A61C12EA30C7718702AECBC2FBED2165A7EE8AC2D1BFA26FC246CEF7D2309F4579FF924FB7F2F46947CDB2F87C3DAE98AD8DC60ED0E5C2CC8FDFC6747339FA89BC913CC4DB85F78029EE72BBA0E5A25CDFDEC05A70700D611323579027142241C568FB3769E8D5F0FF8DE5B93F476633C7D264B999AE29250A457A5694D01103B0FA9D02434CB4D17C0ADABE17500DD5A28F870A71BF082D5241D1A68AE7CA8CB2D60D33E74BAC0107DB1F759794C6B2FC760BED22961B3CE0D0D9FBD
	CB434616CA9FBDCBEF0D0E53D9BED2165E23D92A0F6BC7B02653E2D84A4F1F0E7CC1FF7E147209341867CF6B6F614F293B6E097E365346FED35E817860BD34A7F7DFA5EEF8477ADE20E7586973A9CE5FD01D5F055F8E545FD4E46DB765EACE227CB05128156AEF0F1FC43B87F5BC7F8BD0CB87882FDF7CB2CEA0GG10E8GGD0CB818294G94G88G88G2EEC8EAD2FDF7CB2CEA0GG10E8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAG
	GG08A0GGGG
**end of data**/
}
/**
 * Return the DateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.cannontech.common.gui.util.DateComboBox getDateComboBox() {
	if (ivjDateComboBox == null) {
		try {
			ivjDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjDateComboBox.setName("DateComboBox");
			// user code begin {1}
			ivjDateComboBox.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDateComboBox;
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
			ivjFileFormatPanel = new javax.swing.JPanel();
			ivjFileFormatPanel.setName("FileFormatPanel");
			ivjFileFormatPanel.setBorder(new com.cannontech.common.gui.util.TitleBorder());
			ivjFileFormatPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFileFormatLabel = new java.awt.GridBagConstraints();
			constraintsFileFormatLabel.gridx = 0; constraintsFileFormatLabel.gridy = 0;
			constraintsFileFormatLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFileFormatLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getFileFormatLabel(), constraintsFileFormatLabel);

			java.awt.GridBagConstraints constraintsFileFormatComboBox = new java.awt.GridBagConstraints();
			constraintsFileFormatComboBox.gridx = 1; constraintsFileFormatComboBox.gridy = 0;
			constraintsFileFormatComboBox.gridwidth = 2;
			constraintsFileFormatComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFileFormatComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFileFormatComboBox.weightx = 1.0;
			constraintsFileFormatComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getFileFormatComboBox(), constraintsFileFormatComboBox);

			java.awt.GridBagConstraints constraintsDemandDaysPreviousTextBox = new java.awt.GridBagConstraints();
			constraintsDemandDaysPreviousTextBox.gridx = 1; constraintsDemandDaysPreviousTextBox.gridy = 2;
			constraintsDemandDaysPreviousTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDemandDaysPreviousTextBox.ipadx = 75;
			constraintsDemandDaysPreviousTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getDemandDaysPreviousTextBox(), constraintsDemandDaysPreviousTextBox);

			java.awt.GridBagConstraints constraintsDemandDaysPreviousLabel = new java.awt.GridBagConstraints();
			constraintsDemandDaysPreviousLabel.gridx = 0; constraintsDemandDaysPreviousLabel.gridy = 2;
			constraintsDemandDaysPreviousLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsDemandDaysPreviousLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getDemandDaysPreviousLabel(), constraintsDemandDaysPreviousLabel);

			java.awt.GridBagConstraints constraintsBillingEndDateLabel = new java.awt.GridBagConstraints();
			constraintsBillingEndDateLabel.gridx = 0; constraintsBillingEndDateLabel.gridy = 1;
			constraintsBillingEndDateLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsBillingEndDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getBillingEndDateLabel(), constraintsBillingEndDateLabel);

			java.awt.GridBagConstraints constraintsEnergyDaysPreviousLabel = new java.awt.GridBagConstraints();
			constraintsEnergyDaysPreviousLabel.gridx = 0; constraintsEnergyDaysPreviousLabel.gridy = 3;
			constraintsEnergyDaysPreviousLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsEnergyDaysPreviousLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getEnergyDaysPreviousLabel(), constraintsEnergyDaysPreviousLabel);

			java.awt.GridBagConstraints constraintsEnergyDaysPreviousTextBox = new java.awt.GridBagConstraints();
			constraintsEnergyDaysPreviousTextBox.gridx = 1; constraintsEnergyDaysPreviousTextBox.gridy = 3;
			constraintsEnergyDaysPreviousTextBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEnergyDaysPreviousTextBox.ipadx = 75;
			constraintsEnergyDaysPreviousTextBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getEnergyDaysPreviousTextBox(), constraintsEnergyDaysPreviousTextBox);

			java.awt.GridBagConstraints constraintsDemandStartDateLabel = new java.awt.GridBagConstraints();
			constraintsDemandStartDateLabel.gridx = 2; constraintsDemandStartDateLabel.gridy = 2;
			constraintsDemandStartDateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDemandStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDemandStartDateLabel.weightx = 1.0;
			constraintsDemandStartDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getDemandStartDateLabel(), constraintsDemandStartDateLabel);

			java.awt.GridBagConstraints constraintsEnergyStartDateLabel = new java.awt.GridBagConstraints();
			constraintsEnergyStartDateLabel.gridx = 2; constraintsEnergyStartDateLabel.gridy = 3;
			constraintsEnergyStartDateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsEnergyStartDateLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsEnergyStartDateLabel.weightx = 1.0;
			constraintsEnergyStartDateLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getEnergyStartDateLabel(), constraintsEnergyStartDateLabel);

			java.awt.GridBagConstraints constraintsDateComboBox = new java.awt.GridBagConstraints();
			constraintsDateComboBox.gridx = 1; constraintsDateComboBox.gridy = 1;
			constraintsDateComboBox.gridwidth = 2;
			constraintsDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsDateComboBox.weightx = 1.0;
			constraintsDateComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getDateComboBox(), constraintsDateComboBox);

			java.awt.GridBagConstraints constraintsBillingGroupTypeLabel = new java.awt.GridBagConstraints();
			constraintsBillingGroupTypeLabel.gridx = 0; constraintsBillingGroupTypeLabel.gridy = 5;
			constraintsBillingGroupTypeLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsBillingGroupTypeLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getBillingGroupTypeLabel(), constraintsBillingGroupTypeLabel);

			java.awt.GridBagConstraints constraintsBillingGroupTypeComboBox = new java.awt.GridBagConstraints();
			constraintsBillingGroupTypeComboBox.gridx = 1; constraintsBillingGroupTypeComboBox.gridy = 5;
			constraintsBillingGroupTypeComboBox.gridwidth = 2;
			constraintsBillingGroupTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBillingGroupTypeComboBox.weightx = 1.0;
			constraintsBillingGroupTypeComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getBillingGroupTypeComboBox(), constraintsBillingGroupTypeComboBox);

			java.awt.GridBagConstraints constraintsGroupListScrollPane = new java.awt.GridBagConstraints();
			constraintsGroupListScrollPane.gridx = 0; constraintsGroupListScrollPane.gridy = 6;
			constraintsGroupListScrollPane.gridwidth = 3;
			constraintsGroupListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsGroupListScrollPane.weightx = 1.0;
			constraintsGroupListScrollPane.weighty = 1.0;
			constraintsGroupListScrollPane.insets = new java.awt.Insets(5, 5, 5, 5);
			getFileFormatPanel().add(getGroupListScrollPane(), constraintsGroupListScrollPane);

			java.awt.GridBagConstraints constraintsSeparator = new java.awt.GridBagConstraints();
			constraintsSeparator.gridx = 0; constraintsSeparator.gridy = 4;
			constraintsSeparator.gridwidth = 3;
			constraintsSeparator.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsSeparator.insets = new java.awt.Insets(4, 4, 4, 4);
			getFileFormatPanel().add(getSeparator(), constraintsSeparator);
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
			ivjGenerateFilePanel = new javax.swing.JPanel();
			ivjGenerateFilePanel.setName("GenerateFilePanel");
			ivjGenerateFilePanel.setBorder(new com.cannontech.common.gui.util.TitleBorder());
			ivjGenerateFilePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutputFileTextField = new java.awt.GridBagConstraints();
			constraintsOutputFileTextField.gridx = 0; constraintsOutputFileTextField.gridy = 1;
			constraintsOutputFileTextField.gridwidth = 2;
			constraintsOutputFileTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutputFileTextField.weightx = 1.0;
			constraintsOutputFileTextField.insets = new java.awt.Insets(0, 5, 5, 5);
			getGenerateFilePanel().add(getOutputFileTextField(), constraintsOutputFileTextField);

			java.awt.GridBagConstraints constraintsOutputFileBrowseButton = new java.awt.GridBagConstraints();
			constraintsOutputFileBrowseButton.gridx = 2; constraintsOutputFileBrowseButton.gridy = 1;
			constraintsOutputFileBrowseButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOutputFileBrowseButton.insets = new java.awt.Insets(0, 5, 5, 5);
			getGenerateFilePanel().add(getOutputFileBrowseButton(), constraintsOutputFileBrowseButton);

			java.awt.GridBagConstraints constraintsGenerateFileToggleButton = new java.awt.GridBagConstraints();
			constraintsGenerateFileToggleButton.gridx = 1; constraintsGenerateFileToggleButton.gridy = 3;
			constraintsGenerateFileToggleButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getGenerateFilePanel().add(getGenerateFileToggleButton(), constraintsGenerateFileToggleButton);

			java.awt.GridBagConstraints constraintsTimerLabel = new java.awt.GridBagConstraints();
			constraintsTimerLabel.gridx = 2; constraintsTimerLabel.gridy = 3;
			constraintsTimerLabel.insets = new java.awt.Insets(10, 5, 5, 5);
			getGenerateFilePanel().add(getTimerLabel(), constraintsTimerLabel);

			java.awt.GridBagConstraints constraintsTimeElapsedLabel = new java.awt.GridBagConstraints();
			constraintsTimeElapsedLabel.gridx = 2; constraintsTimeElapsedLabel.gridy = 3;
			constraintsTimeElapsedLabel.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsTimeElapsedLabel.insets = new java.awt.Insets(0, 5, 5, 5);
			getGenerateFilePanel().add(getTimeElapsedLabel(), constraintsTimeElapsedLabel);

			java.awt.GridBagConstraints constraintsOutputFileLabel = new java.awt.GridBagConstraints();
			constraintsOutputFileLabel.gridx = 0; constraintsOutputFileLabel.gridy = 0;
			constraintsOutputFileLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getGenerateFilePanel().add(getOutputFileLabel(), constraintsOutputFileLabel);

			java.awt.GridBagConstraints constraintsIsAppendingCheckBox = new java.awt.GridBagConstraints();
			constraintsIsAppendingCheckBox.gridx = 0; constraintsIsAppendingCheckBox.gridy = 2;
			constraintsIsAppendingCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsIsAppendingCheckBox.insets = new java.awt.Insets(0, 5, 0, 5);
			getGenerateFilePanel().add(getIsAppendingCheckBox(), constraintsIsAppendingCheckBox);

			java.awt.GridBagConstraints constraintsRemoveMultiplierCheckBox = new java.awt.GridBagConstraints();
			constraintsRemoveMultiplierCheckBox.gridx = 0; constraintsRemoveMultiplierCheckBox.gridy = 3;
			constraintsRemoveMultiplierCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRemoveMultiplierCheckBox.insets = new java.awt.Insets(0, 5, 0, 5);
			getGenerateFilePanel().add(getRemoveMultiplierCheckBox(), constraintsRemoveMultiplierCheckBox);
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
			ivjGroupList.setListData(billingFile.getAllBillGroupsVector());

			java.util.Vector tempCollGrp = billingFile.getBillingDefaults().getBillGroup();
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
			ivjGroupListScrollPane = new javax.swing.JScrollPane();
			ivjGroupListScrollPane.setName("GroupListScrollPane");
			ivjGroupListScrollPane.setToolTipText("Select Billing Collection Group(s).");
			ivjGroupListScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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
 * Return the RemoveMultiplierCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getRemoveMultiplierCheckBox() {
	if (ivjRemoveMultiplierCheckBox == null) {
		try {
			ivjRemoveMultiplierCheckBox = new javax.swing.JCheckBox();
			ivjRemoveMultiplierCheckBox.setName("RemoveMultiplierCheckBox");
			ivjRemoveMultiplierCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
			ivjRemoveMultiplierCheckBox.setText("Remove Multiplier");
			// user code begin {1}
			ivjRemoveMultiplierCheckBox.setSelected(getBillingDefaults().getRemoveMultiplier());
//			ivjRemoveMultiplierCheckBox.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRemoveMultiplierCheckBox;
}
/**
 * Return the Separator property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getSeparator() {
	if (ivjSeparator == null) {
		try {
			ivjSeparator = new javax.swing.JSeparator();
			ivjSeparator.setName("Separator");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSeparator;
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
		billingFile.setAllBillGroupsVector(getBillingFile().retreiveAllBillGroupsVector());
		billingFile.retreiveFileFormats();
		System.out.println("Yukon Billing File Client Version: "  + BILLING_VERSION);
		// user code end
		setName("BillingFile");
		setLayout(new java.awt.GridBagLayout());
		setSize(410, 461);

		java.awt.GridBagConstraints constraintsFileFormatPanel = new java.awt.GridBagConstraints();
		constraintsFileFormatPanel.gridx = 0; constraintsFileFormatPanel.gridy = 0;
		constraintsFileFormatPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsFileFormatPanel.weightx = 1.0;
		constraintsFileFormatPanel.weighty = 1.0;
		constraintsFileFormatPanel.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getFileFormatPanel(), constraintsFileFormatPanel);

		java.awt.GridBagConstraints constraintsGenerateFilePanel = new java.awt.GridBagConstraints();
		constraintsGenerateFilePanel.gridx = 0; constraintsGenerateFilePanel.gridy = 1;
		constraintsGenerateFilePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGenerateFilePanel.insets = new java.awt.Insets(5, 5, 5, 5);
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
	
	//Object tempDate = getBillingEndDatePopupField().getValueModel().getValue();
	Date tempDate = getDateComboBox().getSelectedDate();
	newEndDate = tempDate;
/*	if( tempDate instanceof java.util.Date )
	{
		newEndDate = (java.util.Date) tempDate;
	}
	else if( tempDate instanceof java.util.Calendar )
	{
		newEndDate = ((java.util.Calendar) tempDate).getTime();
	}
*/	
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
	selectedCollGrps, getBillingGroupTypeComboBox().getSelectedIndex(),
	getOutputFileTextField().getText(),
	getRemoveMultiplierCheckBox().isSelected(), 
	getInputFileText(),
	newEndDate	);
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
}

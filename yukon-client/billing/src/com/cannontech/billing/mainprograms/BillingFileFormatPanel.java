package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * @author: 
 */ 
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
	if( event.getSource() == getDateComboBox())
	{
		java.util.Date newEndDate = null;
		Object tempDate = getDateComboBox().getSelectedDate();
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
	D0CB838494G88G88GC1FA62ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8DD8D45715F6EBD2F7E3D33BB1AD1B188DC9CCC23376DB133AADAD760BE94D97531254B49ACD1334B431A96D5A147CED5D2EED9EA08A48A04A702FF10CBF999487D4B408287C8C880A8AB2A20978B3B08FE660B1EFB273D0C8EC33675C7B6EFDEF06B7A0DA7B25FC5F613DFB4EBD777C5CF37F6F9B413CB1B2A5B149AC8809A9027A77C122A0CC39A0881B3EF1531F98AE559213AE687EB6812C95FE
	36FF85BC7301B6672FB169D9427E4ED4C817E0DEFFCCFA9E3CE78B659B663542CB98B911C07BF4C94B2251656CC8E5F2D61134141A814FCC00B240B9GA2ADA8FFE2EA3E0A9F02F4267A0E1018A4880ECD18E76F3B79BA7C92B51F95E8D5GA56FE11E3BD3572B74E9001B85A0G5E7C1ED6E69A516E264A8AB55D7955C8612D9F65A619D0162EFC860A4A9B21BEAD423F93C7A48B2FD66D05E7D5CBFF6F21EEF940566D74FAE52FA2F63BEDDD9EC972F8FBED83CE0F576717FB7D4E01C0E5E5AD45B6F8A4917D35
	9975BD7BEEAAA9CB9066C379D3896E59F751C74D706E823882C0F5FE46D4256AB435BFD2083BAFAD2C4E0BB7D3DD17EF56F4DE903FF9650CADD87EBCD28748D785E985005A53DBD1FD2D76B4487E81275262740A9235ABAB0D5915300559655D00F22DDBE3522B81BA36B23B8E3E4F6C6A7874BF05E5DD7B135604314B37D52D0B2D1ADD6539EF279410587ACE9A46478220AD8348589613DE8650826083A8A2B16DCF5D8E4F1AAE25CE9E98103DB5BEDF2D64729F17DD22846F1515C0D1F12744EE596F92849A37
	975FADB150074124ED41B12B4559990145590CD38F880AD21A14E2E05B2C0579C95354B2E6EE53EC0B5B06FEBB32917DB69F5E93D51FD2FCAB41EFF9DF43537AFD1E448F264B019671BE73F3C7A65325F9459DC2562E83C92B8CF40978C1D9527277298EE62B268B7571D7D7230F2B815F8920GCC86188190EBE5BE1E1D3AE7AC9FB7C1DC793DCE29ED304B6774AB35039E49A57AD5DFBF1DBA1A2F0D00723DB8EEBE6A1728BD7ABAFA71DB1506FEF9B1EAE3D23C6A8F21D8738B151D1396CE3625F8E4ECE472BA
	FC1934097262503ADDCA707662503AD5B6303AF5GAD9A0056CB25897B3D755BE352ABGDAG5CG13G2683C4EFE7F533AA2D74AA633FC5D7AF14F7CEDAAEBC45A1456FEC7286942737DBECF77ABD4EAEC98C88EA1EA753509FDDC1FCCF13F6598DB20E08DE516FD4BC3297CAF78E089084811AE7D15ADA4C63978955619990C14000CFE0657EA2AD9B1E12B3201C76391C0A28652074DF27E1DDDEF236C309D0G3C276EC0FFDDA67D6BE2307F2D6D4CD73D1908EB863AEB8773CBD77ABAF89E032094254ACA3B
	279B55F07A07D9EC9D5F086358508E5616DE6BE0B1E3B75DAF14AC5CEF98B388D3F6507A0F5A919AB3E7C81973F7303171976FF60CB3FEC3E1E807F12CE9EDE459F54BC89E05C67D3BF08D46E8826812D18263F2897371DD69969AFB354E0048226D249F82C5F4E99E27F96FD80DE300F8C974AAF651A7FADD7092D06B7A1E55E9DA9C7648BCC69EA1F86CCF6AA0D0829C9F3DFAAD45C3997A28E674B9046ED4943F27EBD0915BC4C501E1CD631FC7F48984F5D5815556C5D9241D020D1DG923735B5A3DB97CF4D
	325CBF68F3E0CBD16332040C0F93A0FFC4892B7F788D988F33F97A8D1256681FEC381E3A43F2476EC3EF3BAE992345C6A677275D943EEE0F190C6B3655E4DC37E7CC23575D8513D15DD1DA0F8947E27932D7978B7DA66C873AE52FD7A48149D984BAC7FA4D01F6F83C9E650C4755ABAAED1ECFE0B2373C846B74D65257AC0077789256CFDC5E4C7A09AD1F5CA34C39E0B69C7FA36E5F13D43D13560FE32756CFD01D24CC0CB1D1928740D1F57220D7D167B2249FD37101E4027B5527B889BFFADC748EB6A96240AE
	56A65357E1998A14BD89A0F2A70B5357B28BF4F12AB646BAD9122879C00B5F0936CD5AE856699B3D134E5D35BAFDE1CD967AD97CF8D004E181837D18B82C766DAF2FD997C2EBF7CA035A18715BB52B61A9695A88453F14E5EC77ABD958AE7AF9793F4EDAA9E05FCF4B2475B45309E57A7C1E8168D230512BEB083AB47D9A224C96130E6B8D87BC5DB6B68FFE2D91F975337589B6643D1F4F55F18C2538598477A0B41E0DA9ECBEF1BCCB3F36F085CA8C6B9CE12E0D7AF031ADF41EF5B89B4BDD867854D2D6D74E35
	7ABE25A690103BBDE438D47D1535CE9F8F9417338E632147638F401C4BA58EB11F972DC3DDF1BCD5512A5FFE137B1EB0F2FED06F72A8321FCF5E3AB1B0282DFFE9502FC9E62F591434BA0C2D6BCB290DD5251A2D76DB07D6D039EF96E33D1035C71F7312F34896388C4E2F2CBC2ADBD3FC137419187FCAA9DBD3BC1F01B1B28B5231GCBCAD93BBB33DBDF87F362ACE16BE0392ACFD6D9E89DBCE3A6EBE240D71731BA785A3A353ABA00B582CC03FA49FACE6D5B3213E9EC2A69466C94961B82EB0B193958169CD0
	268FE092C0A4409CAE63A759E34E1F6B07C619BFBFB2E2AE9AD726757DEF150D9C87681C733889FD193A0B4EB9D7F17C0784EF516169DC74974477182EG5A34DDECFE35B38F7DA66C06BC3B19CD45E6134EEFAD32472BF01FBDE74676AB7BBD3D9EAF6FCF888ED6E024F326385C5C70634606DC63F1E3F32E7138D11CBB7A1CE0D7EE38F1050E5FCF12D83400FD65G947740EA44F5C05A371B0D59ED44F7D3A1BD93E0BE47DFA07884C81B77401CFD8F437FAA030D3F187FEA475D29EA59E36523B27E4CE524DC
	E579ECECDFF59579287FF667E12C8C7174BE129ED64B4996625230FEA56730BC8801B0EF8F7A6F8F3CEF6E043482B08DE0669EBAC7227D44431731FF69014583D4C71BCF6C962617A36F77D7B35EACC8EF81308338GA682D0192DE49E9732DD216AE36A403E7508DF9E74B5FB82CADB379F46C04C2052976E42F8D057AFA216C139595E543F1AC2479554F9A94896764278C272385D7ABC79FBE99EBBBCBBF81E3B6CC6657800BE9100363B0D69EC7FE69A60966CE53E59F59157003C8F35B9AFAB1515C77C9ED7
	2D333719F81A76233D6929AAFF46BE560F5ECE4A34C33A93C059477A5121ED2CFF1F71409DC27304CA43BD27B8472624497B547D0BFDDABF5A187BF68A15794AEAA6F3A95037FC003835997AB9857A79CE78E703A725578551517F582EB57AE0909D755D9B447FE0909DFD749D151F5A76632D4C3665BF0E92247F32065DCF13BE20B6CDD11F55B9C5427477372C24367D48142A360DF99FB0FF2EB023BF050A1874FC00328A3E47D63FC1B09C67F491477686A35D292A2E0D952C5C2EDAACF78224A3GE682503C
	8B6DDC871EF7EE8B51E1BF68GD0361F75B1DB532FEDFFE54FB572C963662363CE6FEA543FF1BF9DF792B95EC7708E9D1E360BBBF93B388834397B59F8941F0FED4ADA89ED8D2093C081188AB033120DCFB1F9E54298E32EC376B54360A369068BF55D367A5A761D9EDB4562EF56776E9522EF0ECF8CB7F71ADBC963EE097ADC1CDDC470543ECF8B51BE738128E3G870084B081A062G33AFB93F045A07D3399AC5F1F63B49AC1199576E91AFEBE86CD829EDBFB169ED1B81654DB9200DB38B8E041BCB4CA0719F
	FF0056D18E47BFCA706B2BB4BC2D3BF40247F48B5016D731D83D5F0D72238E46244F8618FFB0D85E12036164BFC75A493203217BEAF1841FFAA8F4DF4DDD4B76557201F66590137FB33B5E7EB8403FF5C81319FAB81C7C46D6E267E1AAE78D47F7903CDD072772FFC45AAA26A520459FE67287B0A454F6F4C977FECF7A756E630C5DEE3FF7C3E55038FCBDBF7C1CEED93E711C6E4F7946F33A9585236F85E494045F8B48AE506F8550F675D8910E9F2EEAE8CBG53812281E22BD91B3A27C0DDAB993427FAD72FD8
	A713DDD205491FD8985E56DB8A475EDB1DDA08EB39AED9D16481AC1F616FA8447AD2E49FC1D25C5D0568A318D23B951AD5455FDF086304A47690B41D373ECA462FF830EBF9B51B4BCE4CE773DBCB8D437D39004DEF6DB5ACBE9FB1DB61B99552B3816683AC83D8DF9B13DE85F091E092C094403CDA4673B861090334D0876583D483DC8418G9089B087A08EC030B31EACF3A436E735EC0C9F14D7253E4F2B9D598ED117A8D507D05AA4835C55G5AB9F63BB9C0F4C57F2FD67198DBA675BDA78C9F4EBF57985056
	DEE37B9AEBAF0E5669ECB24730001FAB6C2C1EB7F890E707341B63AEF9D80CDC613856FE968F113C1EDE2C453D19841EFE1524D36BD95ADBFBBDFD8A16BB367F3FF4DDB22E4EEF2F5B19BF2C754C9F318D2C4DB4F35C74E13EA664BEB8510A7374B910FE91E0B90025815A94C0A740D0834BF71E640B00F4B440E200E5G79C7202EGBA0E30FC73DB701C84797ED6FDB36A465EBB55F2D1C7549775DC4EED79BD11ED9437FF8B7F33B603ED895B50EA350E55332622707D697C22317B53670B48FE9974030EE11F
	C8F1BFAFA2FB91BDBD8111775B2F94E5F1DCD320C58EF8F0530D2E8B7FE4427CDD321FEC9E50F5ECDEAE3F4BGF5B485A096A08EG577BB44FB773D91EE5C768DAB72B9156C50DB40F2F917364945A4770139E2A002785A0C7CD3B9A3579B155F57107416B5FE6E797DF7F9E28DA97C403E9F017DCAB8FB15E6D07F0CC340B83CE2F4B6E9C8E347845CB9EF9B060G9713ECEA3EA7BE49B34C2717F5268F7BBAF5FF359E73A9221E3EFDB716D18FC3F16FF038B292BEADB04C932A4F0303E62ECF1B8263BE972656
	1FEC66659851276FA173B5C0B20F507D70F74CC17B61EA3D3D88CFCA2FB0696989AABD3F0951338F6969A5CD146E60746F1EB3AA5F4D69F77599512FF0FA37ECC41F4269DF170D64CF6774CFB20D7823B97D4EFCA3FAAC271FA95453635472ADC759DE4C3DA6EC4B0AACCB8E0F8FABCDA8BF0A7707166214CA2D3BF28F4641A5512FF83A1D92DDA057BA7DAD3264699E96220F221C13E4DCAA4636F614352D3BBD2CEDADBFCA5B1F799874534768DE9BEDDB6E7E315AD60A2EEDD5832F631856365C47C25BD6239B
	7BA83EFFA430F3133CBA3D9F94D50708E3CC5742FA266B0CE3D4573970DCFC0C6E8952BCEFB730BC8947E8DF115A8C7320E61A075A93B3BC16BD2BF4761483EFE333E6CFE7F328BD36F46CABCE8EAA3EC185FB85FD5B7B25BBBB080655576091A517CABF3CB6B708DE6B17AF87445AC11837FA855EFE51A67DFE1AC36E6D1554DCB44F4B924EF771245FCF452B3C4FA6E52978FA49698B08AEDDFBFDE38F6A560473F0989020FF29F30B5D7D58A3507A383DB5B4EE51076EE69637D2FFE83BC07A95CEDFDC2B27CF
	D16BA01A531B0E6B6931AAFD9927FF7734117C0C630C7EFA36915D4269F79669696547A93D73B85B9BFCBB172DE3FD000BBA4E761D3E1F0E7B9BC6F73B3A4906072DF750E39BD4BCD2E5254323C0BD10910D6EAD9D4CE367DEF147595E52B3F9A8A77FC4CCFA99C06389A6FFE1BE5F0381DC448936BF3521006D7F4DBF414AB8D6C05662A741BE008A000D821D871373837217F05888C77B0A72AD1822FBA93F4CE0FBA96B7F63CE413EA8BEF133E03C1752F2124EE7A2CFEA7B14131333D4BF3D5993669C0A4498
	35E5DF8E5BEBDBFC126F6BC9C546FCFCA0253C32091DE1E535B05E2D5675463C3CE1D15E2307185C4696665704F38CB70163967431FD43E80E3BC3E67992B86EE91969E2EDE53807B3D93E0B9C7753FC060BE8E5BAFF71682E2B0DA77D60A9303D4DDB8B793D47D636577C13337AB33B2CBB3212D81FB4E28EFECA3D1FF6CA2BC35A7EFBBC6570543951265BD93535571FF2B45555B4DFE82BBBF5323939366654051A368BA76A6B6D75F62AC364E53D8E95695B421E9BB62A32BB55670C06A2F5AF67898FEB03D3
	0196FB0A79F1CD9F33F9C59BEB837F5E07F92BA06DG105A465B8664ED0240462A9F97670509DD9C11288EA76A188E935BD81B2D2DE77A4E865CDBEDCC5FFF29E7F9CB9CCC5F02864C6B02749544B9D8999B0E30F6BF83F0CB9C2C8C7188EB776BCF33B2EEA3F99D1016GA61E9E2F4DF73FAF70B80AEDE531B96DB413F9DFBF53FB41E9962FEB5A8A0CFD4306A5353CB5F52CCDE434B35E874EEF8C53BEF5C3164ABF61B853475E4E5B68E94461F8FA05632C64EC9957B7F1DC4E2176F0FD889B435476D844E51C
	E16B307F4B9DEB8F7A4A196075A165CB4B9B0B6F5D10B4652B9D13CF8F91A01B6EFF561371E1461950BD7D93849FFDA6F4CFFFD19E0B2B39C033F6B0FBDF483F3633085FDCA35FFB8557F60671A89963A6F7045ABB0F60A3BAC26DEDADE076CE87DA6A076C8C634BD61C5FC9101E84B083E0AE40E2008400959F415A822084408E50896083188890819085B09BE07EC7EC6FF2F88B4E195B1DFDA3EFF8GD2BDB4A49B18FEA7BD2BD77736F274FED872910D29E56AB3636C48BD884A2710F3840B811D4CE342721D
	BC04FC0DE178BA816FBBEB9457EF1F536BB9D14D9321BEE7041577FC9F724DD1698B54E73C7ADC9E166FEED9AFAF2B13665B22BEABBA4371BDCB781A8D6888974372FDBBD3AFCFD173CDD21F534272AD8A0A7F19EA3E396AF3F1D83E5B8AC70B7F2586FCB4EE5F44B0D377D19E5D32B5DC4C55F4A9C7214BBB5955A7F6AB8EE7AF67FFE9BB697F4F41FA679C0B5D142DBBC6095D963F6C03A563B045121BE62A8DCFF92E2D0FFCE1CCBE63BE525CB71EBE236A9C6BA35775E39F50F2AE344FA8A2784EF321FD4674
	BE56E7380196FB0E5FBD299BCDFE5979F0F55DD8FFEDFEEA9F134F58CFDF9913CF8F76734CCFDFA6F3110E7321FE7A9A1D231C8F7553468666A79F50961CE7FE5AFC645A4604DD574857B7EEBEEA17B80C7AD7DC883557CB70768B21767E9B1F1335GED5685E66FB32DFA797997E9DD16284F6A0B6162A22AFFB43D9DE1794C06F168D673DF894BF753F1A33EA9EA7E69700C3EE858771FB65A7F0FD573464173AD831988294EE07CFAC8178158F5F8AAE32759C8C607AE1F64B416B1A184BF9552D1G33C748D8
	D42437FF3ECADF22BE1799140FF7CB1205B959B82F74F8A50F9796DE642B8B011DDBFCC8567299DD30666EE271F08C173C2ACC076E7D253C9BFF467CBFFF87990F97E8B6BD2DFBBFD3F063E57BEE288C759C5A9A7E9CA44ABA76B94843D663F3656FD8C71EAB4F369A1FAB7F402ABFD72638A72C382E690539E4874F974BF19FF25CB324CC441D713894B747AFA2FAA13ED1C4018C7F92516BB241F5283817F5388F59B7C1D6BC1B81135DB23FC33A540A6711BD9EC92269841247638D088A77475BC4C70F4F2953
	7B0F9C07FA5336F40A6C8315C0BB2A6AE267FD952508EB0174C50E3B2B141D81CA9CF70B091D3B4E6638B7CCAC5FBC0E6BB9447265F7B35C9307D83EAD9C77279D08D3A0BD0963B24EB37D26F25C6FB7B27D22B82E45424A1B45F189E42FE2058B66DDAE06BB534F4A33F2DCC4B1AB2F0A637E3718155748F15FA6FBCD91100E6238B7094E33A19D4BF19DA50CF7B1477DD6E63AD8C506DB9BE0794AB96EA932F7B59152D3B9EECBA26222A09D4DF17F1C48F867F25CEB790C575C43F0BEF247578A698A0EDB1846
	6FC0F05CC3058CB735074DD1DF5E0E6D135FC52153D0B516FE335DAC50EFC8BCCE49738939425F86993D3D343C9DFD2C0FDE52437A687CFE2D5F5A24FBFF286F4677E1BF3C21B2284DFDF54C07895C2F8F54B3BF18FB199FEEEB50EC1F26FB5F5AF0637DF0686FA023FFD833C9513D5FFD64464B0E3E21B2E81D463732BE44524B6AF9F9A16BC34AB96EDC910B07EA0EFB145FB71942F1BB6A98EF9C47CD66313414632611FD39AAF7CCFA331B6192CF30FC9D9CD7350D7497101E43F1F96D643BAEC8AF6138AB84
	17G69959E06FB3C1D7166F3DC0C0D588B69AA0E5BF30949ED61389A723DCFA424E7F15CAD84B78F52719CF7126C9BAE07F4C69F434DB2F3399C17C947A6C8D7F11C1C4A65F2DCAE71F3A424E7F15C452211F2DF23B848331867FB040CD7CBA13D1C6372B2190C54FEF6566B4B4331D341E3A5F852BD5C93B835D54F73BCB9E4CC163D8ABD537D0C685E817CAEDE4649D24C2340C40672FF3C93796959672C52514EBE75671AE4CF9E4AC4A8D271D17D5A3A0916BD5526151D16B95639AA72313362D9EA591BD55C
	3C91E53716E9E57F7C50F84E2097684A467262617D6B027E4C56DC02F31981271FFCC7C365A9F8CC231E156FA573013700CF10202D917AB7F7EB7A74DA467223DE1FD0BF16C921E7482B7A0D4F78676F51F41AC05AF49570B6F21D5EAC57F44A5ABE16CE492368B4B4C227276460D8FB054CADA6C23EA9920B35741260D84B5D2769A3170C254F2AD17419A30546437E8F34322D01711C6147CA417190A7054643124CE0DB7FC85A66D248370C5BFAA0B158565E8ACD1F48445174C98909876BC0F06CD78DE836D2
	79F9648C3891700E81A67FCBE941721F5E2F49DF1AB616FC3DBFA606480F50492765BD466FD3CD9F9839F6E665B01B22A1CF6CG3DA301383801503AFB3D92F5255F9FFD210E5D51D88A7984AF0CEFG55G973DAC2E4DD5DA5C2FBDC53E7F865AE4CE2FBA24592E940D676E57ECA883A1C755EF3EB75476672E6A3E8B337DC5DDF908CB70065AFE6130266BFDF523153DAAA4CE182EACEE17FBC35B68370E874744BFD5EB3272CF0CA7A6C27D12AA074AEA5BC63E0F157DE22FDF9E743AD87D7413F9009972DBE4
	962B3676E03D6E2F91F8B93735235CCB1E00873FDF26E5BC544E4EA12B218C0AFB0D60DAA05D494B7D29AD381D4613B9439520CF12597D1E23174653B7045A9DBD426E0EEC4CAFFAF1A340256AABE733335A39DC37FB33718C9696501ECFC0BF27D4A3F9FA3DF853DD65194DF996F31E9EF3301F6655EAF17F1219591F6AE37937060C7F874878EF85FA390F59FFF36A35581F276A6672055A3F33E8247D76A2E60B0F6B1632BED8372275E43CG7A941E67C9D3303D7FDD27452BED547903DEE723FA1B638BEDF7
	268AAD66CACA71321CD73CECE11F7B599AC0D16BFBAED8D74A3D6F15D84ACAAC3BF6978B424833F4D6DE9969EF431F3537D59A53997F25835A7B2B87B55DDE59F8353AD15E3443DAB9BF54352B494563AB273FCEAB6709FA2D1CDF6F98DFB93FED985DEED323766EBD214949CF3C76BAE1BE1FF6D2ABFBD92BD636AB6D7A4BAEEA43943DC770C6A13B6B906B3B5ABB98CC2FC38E2D1C876BD83BD971B17B36F3E1503DC7547FF5C17FED66AAFB68371D09C1747DC1F45C9F2CC954537B0368A9BAFE2A5F56B31A5F
	928A2F3F1DB4F698478373EB7F07DA5EDBBF525E9FBE2B3D671D53F43A1B5C83647A5449126C37F5C94E6EFECAFF0C0EB57EC111727E653C467BAD5B3A113CFE6846D4B9CBAEEAF95F3FB43E38DF5525715ECCFE3B2047A985C40EFBB6FB1390FC3F2A8D7F33CF1D82363A7A930E7AD3BC7F4A1C51F57D12CB13571DBABEDD9F92B55E33C5FADDA9FD25DB23BFE09ADF59459E64257BA2FF88BA7B2E0298EF7978EA3EC319B16A792B9128EBF7B28E39BE26FDBF45E5935C9595C767D34EEDF83E276EA3122DF30A
	DFBFE45270EC6BDC203F69A86D1D85CCB3FEA724A84266B691FF6645225E4437E8BF7BE2A197192D01E2DB40AF2C619EEBA25B64F11776D4A63FF2DFF2123022C9D8C90F8D1A049538775FA424102301A6A1D911FDC3BEC11BD69288FCAC7C15AFA44740AA0219A9065703ADA7FD28D9C091B2C812FE26D029C00F96EC050EEAD3045DA786873AC43FC56E3160E78296761D0045A1DB5AC4273F5BED219F87D8CE09CE3CAF9C30F1F5027E94A1C74D98D48EA8G2435CCF8BD21ABA220F368C785A332DF05C6AAA7
	57C891528786CADD2E52E1788BC9E405561AA2185835EA628BC54825D73129E76C9EBF64167D4320CA324DE69B19BD254C9200ACC2865E303628D72C95A113DE2936A8B2498701E18AEACA0F5F337755581F3787120515CDC2814E185AC4BF596096718B8F764BEC8DC21A8FFF7D83FFB88922A6C09EA981E5D8921B85F34FA97C152000A23AEA5856B954F8CEB3CAAA456B24250D4E00DB41D95B01E6E10597FFC3CED8C74912535BDBCA77518F1050CB2C29A6D5C73E0287FD33093ED80A0D17129D33E66B2623
	CB2213052486A1DD121D2E86A73A27C9C8D35CFEB16016A557106FB0ADA875B2B6C25C54C806620A3879E82C8DB7B838527DC3F7CDB6451B9A1284E148AF14121CB8C134719C2EBA37538F22C4FF13025E177DDBB86D8B6FAA5B1EBA125719A4A42AC6E2C8968DC15D920771C609A65B9AC0EB65E73FFB725E972A921682CB0310C2FEEF8684E7E939E8545DBC63768D2B27465D02FA7950BDA458B7AA424A9E6700C702F8DAE9A79DA24327A9FE27B7G6B238190EA893118113862365B1EAC3B7D9BFF7C08A82E
	31AA42C6DAC540DA5AAB4A8325B5BDBD9E2F488B007A0262B77062B10FADA44F3A212F669CDD78CE974417DDC89EA8ABA37FF7117F3B717FAE0259C5B03BF6C325A5043661603FD07A50905AEC9DB26C0E04F1E0E907D6B0A282913BE1E4A8A1FA93A1EE466C78320D5783260A89F18779EF3BB62B7699DB75B954742026A94E7AF039033B1AE91FBFCD6D2379749F40534D7FB0B151768F6E69331A7E78172EEA1A4E3169BA2C90049D2B27718B1941136B447B733E187515BD03C9C2EA83766A124F6DECD2C704
	E6E1F597191F09FE488BF3872DD7A63CB024A6F543F8DB24613B91CF29EDBEE7B70E3AE9AE273F1F1400AAF51F9552FA7C3DDDC400A2A41DEDB88BE2C98A49A0B3DDF163CFA160702788F9DA51ADF831D8A55081DBF8C7403D977CA366123548BB6F8461BED795AB9987E67A67CF5340E8435A67CA53CF42E8CA3F4FA97ECC7FFD4E321BD6057DBEE745E7F40D167F59085F752BC4039C0077FD466EEBEC2A448345F0F7135B861C1224B8257E9A2F8B6FA9578EAB22763BA24FD5E2798116C7A52AF715AAD16FBE
	9D4F7F83D0CB87886165828ED29EGG38DFGGD0CB818294G94G88G88GC1FA62AC6165828ED29EGG38DFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0C9EGGGG
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Billing File Options");
			ivjFileFormatPanel = new javax.swing.JPanel();
			ivjFileFormatPanel.setName("FileFormatPanel");
			ivjFileFormatPanel.setBorder(ivjLocalBorder);
			ivjFileFormatPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFileFormatLabel = new java.awt.GridBagConstraints();
			constraintsFileFormatLabel.gridx = 0; constraintsFileFormatLabel.gridy = 0;
			constraintsFileFormatLabel.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFileFormatLabel.insets = new java.awt.Insets(0, 5, 5, 5);
			getFileFormatPanel().add(getFileFormatLabel(), constraintsFileFormatLabel);

			java.awt.GridBagConstraints constraintsFileFormatComboBox = new java.awt.GridBagConstraints();
			constraintsFileFormatComboBox.gridx = 1; constraintsFileFormatComboBox.gridy = 0;
			constraintsFileFormatComboBox.gridwidth = 2;
			constraintsFileFormatComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFileFormatComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFileFormatComboBox.weightx = 1.0;
			constraintsFileFormatComboBox.insets = new java.awt.Insets(0, 5, 5, 5);
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
			constraintsBillingEndDateLabel.insets = new java.awt.Insets(9, 56, 9, 6);
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
			ivjLocalBorder2.setTitle("Generate File");
			ivjGenerateFilePanel = new javax.swing.JPanel();
			ivjGenerateFilePanel.setName("GenerateFilePanel");
			ivjGenerateFilePanel.setBorder(ivjLocalBorder2);
			ivjGenerateFilePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutputFileLabel = new java.awt.GridBagConstraints();
			constraintsOutputFileLabel.gridx = 1; constraintsOutputFileLabel.gridy = 1;
			constraintsOutputFileLabel.insets = new java.awt.Insets(10, 10, 0, 5);
			getGenerateFilePanel().add(getOutputFileLabel(), constraintsOutputFileLabel);

			java.awt.GridBagConstraints constraintsOutputFileTextField = new java.awt.GridBagConstraints();
			constraintsOutputFileTextField.gridx = 1; constraintsOutputFileTextField.gridy = 2;
			constraintsOutputFileTextField.gridwidth = 2;
			constraintsOutputFileTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutputFileTextField.weightx = 1.0;
			constraintsOutputFileTextField.insets = new java.awt.Insets(0, 10, 10, 5);
			getGenerateFilePanel().add(getOutputFileTextField(), constraintsOutputFileTextField);

			java.awt.GridBagConstraints constraintsOutputFileBrowseButton = new java.awt.GridBagConstraints();
			constraintsOutputFileBrowseButton.gridx = 3; constraintsOutputFileBrowseButton.gridy = 2;
			constraintsOutputFileBrowseButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOutputFileBrowseButton.insets = new java.awt.Insets(0, 5, 10, 5);
			getGenerateFilePanel().add(getOutputFileBrowseButton(), constraintsOutputFileBrowseButton);

			java.awt.GridBagConstraints constraintsGenerateFileToggleButton = new java.awt.GridBagConstraints();
			constraintsGenerateFileToggleButton.gridx = 2; constraintsGenerateFileToggleButton.gridy = 3;
			constraintsGenerateFileToggleButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getGenerateFilePanel().add(getGenerateFileToggleButton(), constraintsGenerateFileToggleButton);

			java.awt.GridBagConstraints constraintsTimerLabel = new java.awt.GridBagConstraints();
			constraintsTimerLabel.gridx = 1; constraintsTimerLabel.gridy = 3;
			constraintsTimerLabel.insets = new java.awt.Insets(10, 5, 5, 5);
			getGenerateFilePanel().add(getTimerLabel(), constraintsTimerLabel);

			java.awt.GridBagConstraints constraintsTimeElapsedLabel = new java.awt.GridBagConstraints();
			constraintsTimeElapsedLabel.gridx = 1; constraintsTimeElapsedLabel.gridy = 3;
			constraintsTimeElapsedLabel.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsTimeElapsedLabel.insets = new java.awt.Insets(0, 5, 5, 5);
			getGenerateFilePanel().add(getTimeElapsedLabel(), constraintsTimeElapsedLabel);

			java.awt.GridBagConstraints constraintsIsAppendingCheckBox = new java.awt.GridBagConstraints();
			constraintsIsAppendingCheckBox.gridx = 3; constraintsIsAppendingCheckBox.gridy = 1;
			constraintsIsAppendingCheckBox.insets = new java.awt.Insets(0, 5, 5, 5);
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
		billingFile.retreiveFileFormats();
		System.out.println("Yukon Billing File Client Version: "  + BILLING_VERSION);
		// user code end
		setName("BillingFile");
		setLayout(new java.awt.GridBagLayout());
		setSize(410, 461);

		java.awt.GridBagConstraints constraintsFileFormatPanel = new java.awt.GridBagConstraints();
		constraintsFileFormatPanel.gridx = 1; constraintsFileFormatPanel.gridy = 1;
		constraintsFileFormatPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsFileFormatPanel.weightx = 1.0;
		constraintsFileFormatPanel.weighty = 1.0;
		constraintsFileFormatPanel.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getFileFormatPanel(), constraintsFileFormatPanel);

		java.awt.GridBagConstraints constraintsGroupListScrollPane = new java.awt.GridBagConstraints();
		constraintsGroupListScrollPane.gridx = 1; constraintsGroupListScrollPane.gridy = 2;
		constraintsGroupListScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGroupListScrollPane.weightx = 1.0;
		constraintsGroupListScrollPane.weighty = 1.0;
		constraintsGroupListScrollPane.insets = new java.awt.Insets(5, 5, 5, 5);
		add(getGroupListScrollPane(), constraintsGroupListScrollPane);

		java.awt.GridBagConstraints constraintsGenerateFilePanel = new java.awt.GridBagConstraints();
		constraintsGenerateFilePanel.gridx = 1; constraintsGenerateFilePanel.gridy = 3;
		constraintsGenerateFilePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsGenerateFilePanel.weightx = 1.0;
		constraintsGenerateFilePanel.weighty = 1.0;
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
	Object tempDate = getDateComboBox().getSelectedDate();

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
}

package com.cannontech.export;

/**
 * Insert the type's description here.
 * Creation date: (4/10/2002 3:22:51 PM)
 * @author: 
 */
public class ExportGui extends javax.swing.JFrame implements java.awt.event.ActionListener {
	private javax.swing.JComboBox ivjFileFormatComboBox = null;
	private javax.swing.JLabel ivjFileFormatLabel = null;
	private javax.swing.JButton ivjAdvancedButton = null;
	private javax.swing.JPanel ivjFormatPanel = null;
	private javax.swing.JPanel ivjRunAsPanel = null;
	private javax.swing.JRadioButton ivjRunConsoleButton = null;
	private javax.swing.JRadioButton ivjRunOnceButton = null;
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JMenuItem ivjAboutMenuItem = null;
	private javax.swing.JMenuItem ivjExitMenuItem = null;
	private javax.swing.JMenuBar ivjExportGuiJMenuBar = null;
	private javax.swing.JMenu ivjFileMenu = null;
	private javax.swing.JMenuItem ivjHelpTopicsMenuItem = null;
	private javax.swing.ButtonGroup runAsButtonGroup = null;
	private javax.swing.JRadioButton ivjRunAsServiceButton = null;
	private javax.swing.JMenu ivjHelpMenu = null;
	private javax.swing.JButton ivjBrowseButton = null;
	private javax.swing.JLabel ivjFileDirectoryExportLabel = null;
	private javax.swing.JTextField ivjFileDirectoryTextField = null;
	private javax.swing.JButton ivjGenerateFileButton = null;
	private javax.swing.JPanel ivjGeneratePanel = null;
	private javax.swing.JCheckBox ivjStartServiceCheckBox = null;
	private javax.swing.JCheckBox ivjInstallServiceCheckBox = null;
	private javax.swing.JPanel ivjRunPanel = null;
	private ExportFormatBase formatBase = null;
	private AdvancedOptionsPanel advOptsPanel = null;
	private String batchFileName = "export.bat";
	private String jarFileName = "export.jar";
	private String serviceName = "Yukon Export Service";
	private final java.text.SimpleDateFormat DATE_FORMAT = new java.text.SimpleDateFormat("MM/dd/yyyy");
	private int RUN_ONCE = 0;
	private int RUN_CONSOLE = 1;
	private int RUN_AS_SERVICE = 2;
	private int selectedRunAsButton = -1;
/**
 * ExportGui constructor comment.
 */
public ExportGui() {
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
	"This is version " + formatBase.getVersion() + "\nCopyright (C) 1999-2002 Cannon Technologies.",
	"About Yukon Export Client",javax.swing.JOptionPane.INFORMATION_MESSAGE);
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:15:16 PM)
 * @param event java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent event) 
{
	if ( event.getSource() == getFileFormatComboBox())
	{
	}
	else if (event.getSource() == getRunOnceButton())
	{
		setSelectedRunAsButton(RUN_ONCE);
		getStartServiceCheckBox().setEnabled(false);
		getInstallServiceCheckBox().setEnabled(false);
	}
	else if (event.getSource() == getRunConsoleButton())
	{
		setSelectedRunAsButton(RUN_CONSOLE);
		getStartServiceCheckBox().setEnabled(false);
		getInstallServiceCheckBox().setEnabled(false);
	}
	else if (event.getSource() == getRunAsServiceButton())
	{
		setSelectedRunAsButton(RUN_AS_SERVICE);
		getStartServiceCheckBox().setEnabled(true);
		getInstallServiceCheckBox().setEnabled(true);		
	}
	else if (event.getSource() == getAdvancedButton())
	{
		AdvancedOptionsPanel advOpts = getAdvOptsPanel();
		advOptsPanel.setPanelsEnabled(getFileFormatComboBox().getSelectedIndex());
		advOpts.showAdvancedOptions( this );
	}
	else if( event.getSource() == getExitMenuItem())
	{
		System.exit(0);
	}
	else if( event.getSource() == getHelpTopicsMenuItem())
	{
	}
	else if( event.getSource() == getAboutMenuItem())
	{
		about();
	}
	else if(event.getSource() == getGenerateFileButton())
	{
		String selectedFormat = ExportFormatTypes.getFormatTypeName( getFileFormatComboBox().getSelectedIndex() );
		formatBase = ExportFormatBase.createFileFormat(selectedFormat);
		formatBase.setAdvancedProperties(advOptsPanel);
		formatBase.setDirectory(getFileDirectoryTextField().getText());
		formatBase.setFormatType(selectedFormat);
		formatBase.setIsService(false);

		serviceName = getFileFormatComboBox().getSelectedItem().toString();
		generateFile();
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/16/2002 3:05:50 PM)
 * @param batchFileString java.lang.String
 */
public void createBatchFile(String batchString)
{
	char [] text = batchString.toCharArray();
	if (batchString != null)	//otherwords this is defaulted as a pdf format.
	{
		try
		{
			java.io.FileWriter writer = new java.io.FileWriter( batchFileName );

			for( int i = 0; i < text.length; i++ )
			{
				if( new Character(text[i]) != null)
					writer.write( text[ i ] );
			}
					
			writer.close();
		}
		catch ( java.io.IOException e )
		{
			e.printStackTrace();
		}
	}

}
/**
 * Insert the method's description here.
 * Creation date: (4/18/2002 8:47:35 AM)
 */
public void generateFile()
{
	if( getSelectedRunAsButton() == RUN_ONCE)
	{
		ExportFormatBase.runMainWithGui(formatBase);
	}
	else if( getSelectedRunAsButton() == RUN_CONSOLE)
	{
		String string = new String("java ");
		string += "-cp .;" + jarFileName + " ";
		
		string = formatBase.appendBatchFileParms(string);
		createBatchFile(string);

		try
		{
			Runtime.getRuntime().exec("cmd /c\"start " + batchFileName + "\"");
		}
		catch(java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
		System.exit(0);

	}
	else if( getSelectedRunAsButton() == RUN_AS_SERVICE)
	{
		String string = new String("JNT ");
		
		if( getInstallServiceCheckBox().isSelected())
		{
			string += "\"/InstallAsService:" + serviceName + "\" ";
			string += "\"/SD" + System.getProperty("user.dir") +  "\" ";
//			string += "-Dshutdown.method=stopApplication ";
			string += "-cp .;" + jarFileName + " ";
		}
		
		string = formatBase.appendBatchFileParms(string);
				
		if( getStartServiceCheckBox().isSelected())
		{
			string += "\r\n";
			string += "net start \"" + serviceName + "\" ";
		}
		createBatchFile(string);
			
		try
		{
			Runtime.getRuntime().exec("cmd /c\"start " + batchFileName + "\"");
		}
		catch(java.io.IOException ioe)
		{
			ioe.printStackTrace();
		}
		System.exit(0);
	}

}
/**
 * Return the AboutMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getAboutMenuItem() {
	if (ivjAboutMenuItem == null) {
		try {
			ivjAboutMenuItem = new javax.swing.JMenuItem();
			ivjAboutMenuItem.setName("AboutMenuItem");
			ivjAboutMenuItem.setText("About");
			// user code begin {1}
			ivjAboutMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAboutMenuItem;
}
/**
 * Return the AdvancedButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAdvancedButton() {
	if (ivjAdvancedButton == null) {
		try {
			ivjAdvancedButton = new javax.swing.JButton();
			ivjAdvancedButton.setName("AdvancedButton");
			ivjAdvancedButton.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
			ivjAdvancedButton.setText("Advanced ...");
			ivjAdvancedButton.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			ivjAdvancedButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedButton;
}
private AdvancedOptionsPanel getAdvOptsPanel()
{
	if( advOptsPanel == null)
	{
		advOptsPanel  = new AdvancedOptionsPanel(getFileFormatComboBox().getSelectedIndex());
	}
	return advOptsPanel;
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
			ivjBrowseButton.setFont(new java.awt.Font("dialog", 0, 12));
			ivjBrowseButton.setText("Browse");
			ivjBrowseButton.setMargin(new java.awt.Insets(0, 2, 0, 2));
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G2DECC1ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DFC8DD8D45735A831441804D6EC50982339441FDC9A091216A43E9B5F2B4D253D3635DF3D7D5256FBC3D3EF6B3DB739573424B1F92675A5C7FE948589AAAAAA515198C5A5888A8A0806FF41E0C2E4A2A4A189434C81C60619F1662028985FDAFB1F354F19439990FA7D3E72FD0BF376DAFB6F7533575EFB6DF54E99A9EBF7E44A6A44ACC9DA1DA229FFA7D6CBD238DD12DE794F27EF92EE659799E9126E
	EF8F40C669355EE43826FE19119633A0A3ADD37A3EBF954AB9D0F64C4BC85B86771B255E0F4BBF031BA0FC320076C254E96F50FCEE16921F352C7C234CF438E6GD8GD783907EA572FFBEB3DB45F7C3B9C33DC7D81DA8C9C5DFE11D9B7B33F578E755FA29C04B81D8406A3C1E19235287200CF5A2609A75155667BAA65D1DE5A5EA39EDC2243462A77B93B7A0AFDD7F84296D3CED1EFA35C8F318A112248879DD3816B775F41D36FBFA736DB6375B63D6E4FBF73E5C6F7578143232DAF6BD536704FAD35BD13E99
	7BD3D9DBC9F2C0B95706381D8750A6CB213C926BG0E4B784F9B4AD599A2FAA22525B336871551E56332055BB499A34736A4D6F4E07FFBB7262B6D9683BD95F0DC160B9BC916508E126549F7316EFCA8BF8B303C03E4B9F500E4E93DBED7DA59511078D690D9126D2A3D6C1AAC615B8EA6EFE7631FF69247309BE8E1G11GF1G4BG129D99E9BB195F7D287490DCABBB14EACFEF2F47DD6975D6399C3EF39E076C027B32B220283873325D63F3C892772D287DF9A6729064B8827DCA73059609FCA17A7CA31222
	3C1F18E222DB6CAFF3938B54BEEA9C1AEEA18E345BF8B6068E38DF2D764B71D399BECE0767E3320761313C94E8B5B25939B503E4E9CC1EA2E596FE103856C416494FD4A4C649DC068532A68B37713F17230D1783BE35B3A34D82D082508A505FC9B64EACBBB61C0D9B4017FDEE1B2B392F43EB73A9D5FDCE17C37629364E5DB8142D4D0037BBBE62F65CAED14774E3947F3D3326F6090FBA129851496D915D295905737684653D28F330EFE408B1BC341F4D4FCEBE06C982DF447089BABC9F5B79EC8EE1D96A0275
	94000F4BB7CE635A34904A4BGD681E4F5E7249581B481B83AE9ECBEAFBBF20B7E5F249B975EB66B64D6384A7D0A4F566076ABB637DD3EE073B9ED9DAE59AF29F55EBE09766888E837174DCBBB70B8A33BE51FCDF1FA5C503B2DD786A77073BA07CFEE44BABE19D12D4EDE9998746286406947CEEE16F0AD72AB1FF89DB6C556EAF07A4913B81617ED9720C768G67D4B75BFB581A988A77115DE42B187791379269CE324B13A7B7417583F0CA59D5D6D663342398B65FD5722D0987B2613A8F6A1783D8813A1DB4
	2F7E50CA7ED333E11614774B86D37FE17B06137BC2045368BF53D97F73819F8F308A40F21176358387DAC768578465AA1FFD4EC09F342AE55401732898B50F94751AE4B2A7E90EF13B6F2AC45F2D813D5A81C2AE126DDFBEE961BED9E5734B64059F794001E407B6923C6E9F4AF1EF10AF4BEE25C6764AEE875C78D59FF835FC1D661F1D9E61BBFFE1F8DCE72A41017C823F7A54C60E07BE745ECE74F50CEED3941F3323CF111BE5C5F13A3B34769BCF21AC7E00A54C2F6AEA29C2DDA7030EB1G8B042ED3CEED56
	79D9234753536735628CD27DF5AC5BB71F057A4B4578976C47B8A733074AE52C2C511FF977D6468E5B0FE6FCB90F67EE09075187EE9538ED3EF9BA7858C61CB69F5BA9274D47B6727450E397F55AEC6CB8ED6EE974E94F95374E97E2CF637AE077385DB2F3C851C462715A9F2C2807536DD4DA1C0EAED9E9F6DE13A52929874774D7EC8D4A057B729EDAB32E3CC3EB463EEB8FCB8B3E5AE79A97CC1ED51D98210EDBD40F36E6F0191EF89FFDCCF6493DE028EACF1FDB617827597A26627DC98C77474A75F0BDFBCE
	F677B5A8F2EFA19F2B4DD2C8F9G7EA86153FB310FBA27CBC692477D7CB2626AE51797F15C4F1B2BD10605A0DBBC400A9E727393955BF5FE2ECE66EA0F4B454D8734F297334D01AC1D3E69AE9E37E788FAE2EEB0BA0F5B73F375F1FB28CA1F8F5745G3C0E7310D91D97603A4AC5726E2B447557AFDF6A13E1DB43897901FC1576264ACD865A851B2BCF5B730ED5626144251B4B9C3F3F4AFCFC722AF07E76087E8B2A56C838F771BE39BF455830CF2F4F598BCB9FAECE2A3E53B66AF5496A05FD88F4A901EBA3G
	EFBBB997FDCCFFBEF074E25B174479600840A59FC0DCA9CC721FA7D1BCF4AED3FFFEF1784BCDFD1379E4AF9F0B793D46B8F1DC8D769B8F78653DE463C72B74EBDF255F6F31BB59F62F5A6B53EA3D5FF25C1755681F1DCE1F9FE2C6075CCFB6F7D4232C988F28E855EEAFEE5DAB8D0EEFEA9DCE45639341279D9D106BFA23CEFF8E1A7FD6E1E2DA90DDA5372A23DB53B5AEBDBD05737DF4BD1EAB44F94D59511B5FF739B75F5621644B403C2C0CCD318BCEFF1F4D4506D3BD33F566262A32373B45B969A05BBB209C
	833050CDE72A078FD15D25226ECB0731EE36A7A32DG205EC3BE707A0EEC1267226D322DBF5FFF8556BF90238E63AE5E5F5ACD589F2E6F6D9E6A6F97BB99EFA847GAC7450FA55F2D46F938B1696847509259EEE1F959E23CF6C28C3FE195E0C34FDDE72095FD6EF5479841C39A02C6C0299A476041AA4BED75472041A941AAB924595ADE7F00DA801BE2DGAE00D0001902C7F66D3067115A7EA14EA3BD39E631FD1C176B3744BBF8DFD5E37BC3E84B65DE9E432F9678E38C3FD207D7E37B03F416CE3E84737A92
	6D6F0F9CB2633F6892673B74D2B07E36BC6CEF45A5A37F0B8CAF790C7CDFB9CC7C53016675917F74CD487FD91FF97C7002813F8A4A1CCFDD8E761769B71EED1A985E62B71EED166C2433CD8150A27C942FDF2EE771BA14D3959AD3DF6D861D5FB4F91CEEC5784C6EBADCCFBDBEE71753CD38FD8C87A7FA3629F35CD7E702479B1DE74C638D579973F843F7E668D8725A19E071880F7B4630F5A09B744B8560387FE438A2A8B7AAA29644E5C6754BD25D7DB387C79B3F0FA0863C2DBC384E7F7B905941AB6C306BB0
	59E1C05821ABCF535D2B3BFF7D706D3743263C5BEF07F7585A9C09B690F6A85DC1F6D8A2F01F6DC45CAAA8E77611ED9AF3B03E4891658B2CDCAE4A4F6F9CCEFEEC7B5759687F8E4BE334E78584BE272E57637A51AF4AB7D9792AC4B16C833928378213383D8F7347459F211F707D7171AB384FF742E688533059AB5B2515FDE84FB7C4BCB9D33541BC753A842E3C1F56A66C0757AFBECC75ABAE73FAD6752A40154BD146461285GD5B65F724B586E63CD54AE824A51EA1BE7AF73F39F6F3BE98755A93A82FBBBC0
	B740B80028AB5477251D280F62D4581E4965FB72E3C95860479BF05D613BFEB5E610F1F9F2E3DD9ECF5E15230FA797DE617C1743D56A273D7F671F6B777E4CA916C45AB70C63125D2F669B7A353D1F479A7B8E262AEDDA01965E0F3873877531BD4AF990A09F0053C38368A9BABA176B276FD26C366A27D1126B091220B96CA52ABC2BC82E321DD2DEDCCBA2176D7343A4DB51D5126D6E43C34B7674E123EC5B12B91D6FE5D7B751DE6A02BEE7DE2538ECCC698E49B4AED33D834E09BC86E46D63215D8A0074EBB4
	967D7BC96768C726C80DA1E7CDF5DEEAADCC5CF70D6BDAFFCD9B0B195B8EA6F31DAF6FA01DC32F114EB3F768F5C27FF9A5D12F732FF698ED5293E013541DFABA4A7BC4C07B321DC61B9DDE4D695CB62F9C8C923322473A385C5F4F2198F87EB50AD97F341B45B9D0CE3F8E3165F50A9BCA1B51E2FCED78734778BC65026D22690960AC307B2863A2C41FD6DB17C4EB584103234BA77FE0AD0DD16CBFCC17660DDD39BA5819227CBA9F1BB675BA2FE4A743F3793FEDC5FB0481AD8AE09140B20015G19839487D51C
	C799B1028505AA9545E66FE627A7526C02D33E2221F121633A359F9EDD0E7D662651357B66085B29B174E69C5BFD83BCD6CC9178E206AF516155981A4D9BACB782ADE600FC613FBFC1DBE65D00F392C0BDC0BBC08840549BE44B3B4F97C8431C932C9EEFA3840CAEDD0847753B3EE3F4F6094BB1360B39216D3DF1B70245761D9654F3498D2E7FDA0177B1FC028E4F6D32124D91AC27FE8DE7242F49AEC7DBF0CEAE0672F2004C1BB0FFGCAEE12CD66B52B67E9935F2AF5F4611E02CF82941AE78FDA034754532D
	43BFBF18ED4573FE07C7D1BC3D58BF619F37226C0A474B10EA4E5E0A738D427CEE05D7D571732D38C63864CE06D663664D083302DE5DB7A98EBA49F083D00E9438E7B67DCF47F648932F9A5F677E47F9D0AC15A2EC30621360637017CF4C4FB62BAF181FED56DE98BA177A56056039542D977439D49E33BE475670B83047121B9447AE58CD316DAAE1A79773E5CB8878A3C0D388617B995E8565F000D801FFE3772D58957BB14317987096C3197358E639959EEB0360D38CE5E38C4CEDD65692FC6CAADB061FC3E7
	DBD89E8DFC5FFA55ABF35CC7AD6CCC5C59691745DC3536E48ADC033F4963F7E2B26ED6733FBCFE6C5D2C0F9F170470734AAA38E65EC179570DDB4D6A64823D7C8E0AE37F14037239ECD7BDFDA09F0F11573DCD6D2CD04F85908190056DA04EDDF0875679756EECA978B8703E972D417D374E636B3531605725621F7F9C571A73FD6ECABFC72B78BB9C98D3294FAAE51524464B33AD46F899E5C99D131116B306747DC93DD91DA220570BBA330A4C6A3483DD99C331623AED782ECC10D8D14D9FA6E5D1CEB7EC8C45
	C3133258FB81D00E87D8B10672078DDBA9F6CA7F8655FDED9BFBA68765B6G85006F9705EFD37C9A863838EF10CC056DB9FA1972EDD7143232B3BE27234A565548C60F737A6DAE0AAFD789DE9B589ED4941A1156886088251876E28F45CBB9339FE4F1EA30B8FEA0144F4948D0AD2645AB6779FB8B65B31706924F434DACBFFAA76C7BG75F712AD929BC46E73CE2A5B4F1ECFCE06F28C40C2D177B7C522DFD177824B21E50F05FE816A47529A141AE59687340F8DFE1E667B767AAD581FB25618AB4BE278503146
	DC5954AC4A15C5GED25607F61D6B37EEDF78572776A4AEACEB01B71394B98E775B17C643B0CF1562AED94E7C583ADF39C711F36EBF4F1506CD136DBB662F6DC2FBB18AFAA630C3951FB98BEE41CB1B73AF937188FC0CB98C7715362D61C47563BB3523CG91G51G8BG165CCDB1540BAD18FFB6BDFBG723C4743D6A5BC1D5B1C306630104AE7430CA65771850BDE4765502FF40FB6FED977840B917F0E79FE6EBD46B1FD14614B6FB10EE9E1B30DE99350E26F21B1DD5B20671FFDEFC6DA5E3D9A4F0AFB0371
	DF4E661375DE63FBCB2FB33C77DE63FBCBDFD752FBCBE1C0CB3817787F2EC84F5F9A86B1DC1846F3A0AC987F1F30B99A9EE6647FEB060F8EB372EF4BA37E8B011677CD0ACDF633B5B69C4A115FA419B237E0EEA9DA14F730F21CA8CF4F524789D8EFA4BEFAEB3E7FF4G0F3FA61FE686DCF7F9AB693ECC5863C78C17G6574EF113E5EEC163705F20940DDE7B8AB149501CB4C9EAAFE42362315B7DB7058B9A48FE38CB692C892363FEB37A46E43F5775BEFA9867D6B467D68ED6541572CFFE98D9E03AEED9DBE86
	FD31557C9C774756416738952D666738BF376A4FF15C775EE4FBFC840CF55437489F6B76A0EE9E1497895C8F7752F9A2C178452CE644D50C07B8EBBC61E298EEG4AD3856EC473E86C0BFD8623D568E81CC75328F88C653B7A792FBEB7E9A0DB4D9FCFF6E9EF90369238FFBDABECA5EC70A776BEC4C5B84405G83G1161C4FB1351E2217CAC408A002C89C48B2DECD55B99659B50615ACC68C608B4292346980C0759FA27BFF75D4ABCDFE70263B6F91D45FCA520D7638432D3C5B1625A206C95386F9FA75BC588
	9BAC4EC37BAC0372AAG4B37B352EA3ECD345FB01A834A21GB38162842D43743992368DA63FC5C7C3DE48F36835633AA98FB3BB843369D07B8CBFBF9D4F8AFC6E22601B7EAA6D57EBF00F2D6DF7AA744A91FF4F6703AD7A5C70B2508B73B2697751B317191FEBFD7CE6AB1E8FEC6CC5A2CC55595C8E091EFD7CD31BD66F99873E370367D52B476B347B09A351CFFF067DD4F64021C5A3296F9DBD16ED1487F3C4961007537D5B4D68C57751F9F66B9EBC4FF6F8FC6CA13075BEEC735E9E34CB3D2873451E41E7DE
	E58F1D8BA75F474F3CB1F00D43FBBCD3C7E01D783DB5C30C81452DC9AA44C35B8400ADEAB9B9C20BED399C27B6201D702C4F4F48E0538ECF15271F681F1DFBAB005EE86B0093B137436F1A75F6E80D60F206C4101DA2AA74748815BE8F2E5CA6E79A50A60B204C753F3B0174DF924175CF00EB72C41E6B617A3FFCF6B87D13F41083ED8BA6EA7A57CCB46A7FDA91362BF4DC461D4CD15587FB161BE86377207C4D0A4D27B44B3E4BCE3BDC5DAD5BFB40BA1CFE67CD5C8B59B668F299EAD09F39ECADB95F67E62984
	EE17E3D5FA3BB4297A75CFA43BC5DD525343D4FA2C20675B4D688BA7D2FE217204B13F0074F820A7CCA47F4BAC9E6C23870A59B7B51328CECB71E09FF597D35FD9137898654135E2924F4571B17A5571614608569204D66CE712B6C6A1130CE374159D570DAA1F670ADF66A344718BD8DE9FFD334669136DF02E3A4ADFFCE3CE4A6B2C3F3055D8478A0BD31DD3F6B9287FEFFD00F300F2BED8177C005BFB63FB4671C2DD43A651F8ACBEE874F324C78A7A17B9FAFA2CEA2BF8C15F3E5F2C7F04C914F7D91802B1D1
	10DC90DB39F82EE24C964A87A55FCF790BE55904AB9738C73793CE391F463A9ABF2BD21FE11D12754FFC8B2ECB09C912F9CEA67AFE759DC0759A563313F294FB684C3C9CE87BA68BF976D0EE28EDB265D3B66D453A91D00E860883584DE482DF1468AF61FDB39CDF776F7FD2137D5F527579241ADF2FDC7DCE905963A72B5F93BD20651332E33357F11BF7390258DCAC10DC1F6DD64A2BB99E2071FA25B34833CC66173C5DCFAB285D5487441E50009AF07BBD5EC076FB966869D348FE2FB7107DEA26107D8E1C45
	3A83D00EG081EB2D27BEDED17441CFC2BF93B396CB4A5D43DAFD6252AB60F1BC2FA1F3E904CE634A82A7941CB54F61550CB36932E7DC142DDBDC1B60AFB10F4FD75886A3A8A4AD9D3E16D19CA36BBD52C59EED2B1592E866883D3293F670B49F631D3293F74636CBDBDA8A7CF4BC84B19B6D25B5558A4E1137A77C80722E944F32DF8EF53B50D6C74EEEEC09ED53313D819F85B09B914FB1CA97AEB5ACF7D45CF2358712EADE6F9302C07060B87DF4CB6EB17BBEC3B395B4D5A158F510E674BDE36617E52749065
	4656FF014F970365462ABD8E39D97101E5086F74BD234B93460C325D0BA3EE47F3CCE359FA66F84818DF9C4F705E070C79456C3D642F83C0DB7A906597775AF19F2D79BB080381C281268244GAC840887C8GC80E843F85A8822887E883708204GC4GC4C5123D633FBCB8043D55C43EAE83C9F658E58DE6077950776248E0E7354595E66D162975D786ED77D44368466B0723EC7746085B7171FC102D3B698F9B47F9A643E7BFEC9C67774E52B86F835A3807E97E96578664F89F66F6112673EB16FA4D1DEE4C
	A3AC3BE4F61EAB1FBE7C793A49D0478165FE830E7338E8B76391F68BBCA28DF5E2213C50144757A70C760F5755CBB061456D6CACC0FBA64FB03E87514770B9B30C6FC17C65883D87D1843449B3487ED30BC767B70F0E325D6FC75C0E6B9542762AD5B30C7EF6B743274EB47A5B266364EFD9C06B1FC97A1EFBCF4FBFFEA637EF02FACD1E151116BD4BEC1C52CD5FC54FD36BD64C8AB61F27843C67E2D56BF52B5701206DBEB17D6EBADC2DBF53249D4F4BBC3405725673E6D12EC65ACE38B8017B1140AD1EC5EB64
	2BB6BC737883761DB05C2668DBD05D7D2E3D37BF5FFB62367260BA9F37874F771E328F1F6F2D331B67FB9B6D03733D9F5B4D733DADF6FD3E17635A6C9867F4413E54AA6A59846ED3016BE4FDA22E4569D03A855E4564C2FC3D0C8C457B7ACC2EAB8C572A622E68F01F2A3881BB7A82285C6D915F8D5CE14798265369F271721D2C0E536D1795E10FFB180CCE2FCDA777F8017BD478E9B1BBBB44036F2D90BE79DA8D6594D39F915F823033C2AB143B852E31126AA90F503CFE782C661B0F6A6E0F1D3D7DFE5AF8DB
	F928BA5732F707C05FF8E10707EA49B6898277E62DF81FC8602A581923716FB352DCFFCF38B5BE4405C0B9DC60FA98EEAA142385EE5AB56AEF01407D2203FD4F9485E75BA84205B19FAE01F23D403D4CF0D6A8BB842E30187A6B97387FBACE7DAD9438096C7C938F6584014BAA2536D95F21F177307D4E816590002982FF1D6167C3B99EE015402FAE986DD8B9547E0D78106F98E3139D2366C14F590CF8FDCE1F7398FBDC7371713A7B6C63375F5F8BEFAB8FBE568EF61E4CF9944E390F5278CFE05FC3D5C039C9
	60DE2BA71FE89338696C7B21E8A8AF9038F78ED15BC58297FAC8FCD7A2F00F33F3E76E6C0C34125904BB4D561BC6A837895C8C3186F58B1CEBBF7118A7F0BF5BCF7562E6D3CEBF8B17FEB5EFFF22D33B7F8CDF41D673F20D7CFDD01B3D274B676943C43FB51A59052D1BD9F0EF09261C63065A21F20E143F221C63E4E8979D4D77FC742B8551461C639B95014FB1EE6036A2F1DE764A11646097293C3628782503F859EBD1CF1B4B596546F7ECDBC94F6BEC9DDB8E7752E3E437C827A64756BA1223603141F3D22F
	B36AD883F5DA81A89F66FA4CA8472717B047811B1D739A3E0F622AA409D4F97C17A8FF527F9865DD5E76855A699FAFEA63B70651BA21AF5EFEB2DBDB2320ED6CE3B4EE3157C807E5C1F450670AD7C21D74B91A8E16B9C69DDE6C4079A93BB1C0F128F2CBF612BBEF8E49BD439EA877DC17A6F72AA3D06EDD8E66535036F18E49DDDFCCF28F4C99DE6EF028B3F30E668373F4F2F39E1BCF100CF1C246689303736F7FA8F27BCB66284FC8601ABC17BF0765329DAEA559F2668E16CDEFAF34D5815429192B655FDB67
	9AED9A6145E7E5677B5C9FC174A772001C961B1449E918B770388272738A4B2B33F7AC5527A301F975B9857A3C37C355A1FCAE6545FFDBE2C61FA96807CC5B4F1BCB763B7EDE60B8CEE06B69A220AF91F5226A837D60973D5A7C5A50B054BCCF118267F9EA8C44ECB1FCCDC1FCCE0C510E7FAB9747335766E35F26F2FE2F22A9557BEC2F467B41C3A3D9E308B74D0B5CC13C9766845A62F9364697C13D0A983245A7150136283B2449738FC32EF94675552B4A33C535D328CE9E5E375327753DEC7FF0FD6BA1D557
	B77697BE286F74BEC9687DE24DFE38526B2A794DD77D0A5CDB6DF1F9FC7978907932DCED7332DF9D822D6999C264957D730F12754703FE6FBEB5064F2F18986D79472D650FC9564B1771BEF8FE7901AB6674256FF37D3EDD7B0EDE3FEACF2F576306A5BB3F3A76C3EB6D792FAA9B9B4EFCF88E6E559C7519BA4A9F3F90B352DC776F4EE8F63D67529E152F1A3E4EEF467F74C908DF65AF744CBF27351DF08D8F51EE790AC548DD870E193B57613439BCDDFB73AC85F916422387A92F7E6F57C8E64B63E6B2AFB511
	F929AA73479FEAFC3731B3D8274D65573E17BC247BDE725D621149F5351864EA9D31DC674EEBFC8F1D504B45F3B77FFD02F2B76150774247A97FFF2C047689693B748C43D6423E0702F293C077F729EEDBA94997725D110E3517FDBF4777302DD53BA453679567ED8E270756F4F52C8A689948BCA14756D2FA0E11A0F036824265FE0FE21B0FAFE8B6F9336AD647A2785CD2DA347B7BDB357BB7F46F249C2D9CBD9F32551EB62D6F1AAF70DEFD5E718556ED877D863EC779250F7B8ECB41F370CDBE0FD776A9D7B9
	167D528C1D2F6B7439302950DF4C77064E4172F67FF5CD5FAE8E5AAC3925F6DF8764A817C39BA976D65A3DBCA817BB9C286F150D32DDC94968F2293FE4F3A6AB5618CBFD1E61AD3146DCEAD7894D47BC20054752D95AD62A673F827029CF5C523894E03B9CC35D8235DC23DEDB1FB01E2BF7161A696BD26B05285708C16DBA8B4C5AC52975662B57453AF65CDFDF7B826F4577DF5CA5D55A7F7B8263AF7C5DAB18AB4EEB6C67FC7893BBFE6E33333DE659935C169C574DF02B82F02B2F8F2E37667A60FA930B8757
	1BD22C2F27FEE3075BB7BDA3515D77165C7E3C40585B4A036B5CDFCAF6583D1AF0B7CA498E93B53B963277321F4CC84BFB124E62E78BA9DFD6A1F0338B69FC5EA8F05758BEB3954A73856EE8A935DDACF07709FCD43C407D71486036B78A28ED5ED304FB3E005A16885C45A2FDDB3E7F58586FE12AFE46D23C9C1F533FC143D30AD7DD573319CEFE99FFF3CCF6701877C8A13D77B26EA95AF3BA3EC27F60F168B305A309C3A7C39F8894F3C7BFE50C39A7148CFD1E1EDDC27B73BCA14F1D6D1ABC0F168EA7CF4A90
	72ACFB4A9897C759343EDFAD982E6F35C3741DB0C8573AC2F6F670754941776367D81EC7FDBF33846B8D91BFB65656D9E93F6E527A5843729F7CF724E7DDBC8DCC22DF12D294A999DF5B028B3E7129C81B78FBE096C4DA2ADDCE303931CAAAFD4B6877C9EF093D3F01A57217DEBED616747B99C909D2F2033406BF58E810127169C40314429EDEB4C8C90A475B6FD587E1BD017F12743534C60F419A66D81085DF06D524B4144542DFD4D5249476D62AA225B36979EB17E5D0241794AD797979D00B3D1728C8EB55
	778F95E98DFFC3CE70423FC495A4C5DAAFDE00034E36594B5A2E7675F85CEDF6E61DB67E4B52ED0A34015E03E3468363E4199D1ECEFF5D2E647F1F7B46C15F7DBEA9C3F77EG626AD95B3E11B97ED8DF2214DAA72D0138565BED833B319F6EEA1452BA188171DBA790BA33915B9E0136C7D85B130DD2A29AE1270637A31ED31B3DB63B8CD556B9EC3E9E5683AA1B7D39342E5357554198A8D26267F51F83DBD6C2720EC6E93D524DFF81259FAC36CD6B3A895FF3D089A77B69A747933D6CAF40107807CF4924G2C
	7861F201C54CF6751A82565B9090D67E7061627FD8747C85FF123426C15A0E2771E65947B6CF59D1E3D36897EC6B24F5DE7CD5AB7CA1C970BBBF3B2478152BAE30DFD667F97CB5C2988AC7A5ED4BE030ADDC2BBE4569BAD2EF73F7AB188183D3A53B7137F625CD1C6C32393B0E70BD1A6B3B3A3202A940FE958C645D4C6445DE72C5AF1B1FFE6B5DBDE7174DCB12926B2475AE0F4DD1E7C3C7EA1056A95DBE595F6DF1B97A3D6F730ED22F6044470FEE13203B1DC2FDD4B69FCF59C2681E7EE9E19BDE58D047FD68
	882B098311AFEAB82A3BEDBEE0A57B9A9474C90FEF1F205D31CB597F33B35B5A9225552A12B884FB7BE1EA2FD3FCB6373F93A68EC8E0B1C8C0A451D17EFDBFAE18781DBFFF46BA521AAA52EE4DCF3AE4CF6F114A4ECE27DB969D007D207B37C577D8A75FD0E7D37F04ADE7FF79E6870CF70D1454DBD0407E97327FC771FFA14394B2CC61D1B0E202B4641F115E4F9CEF3F421DC4ACE5B84C79F5B0D24A3F3E7463694F15A7AC876D6A54A988464E54EAFD58575BA17B46C6CFFCBBADE2693DB896DEF4896A9726EF
	2F53F595EEEA5801289F71477B0378BBE2AD033C8A317B863B8722F39971907B7F5E081547D6C7D85BC2E408B7C501121E8A1474C4002408CD56435F1E7C3501728B98827BB7A57F3991498F1B5C79BE3765A337EC415F78D6248CACB638ADEAF2D9C5D47AADEABEB9E0DF9D3C981BE3A5BD16F2FE87EF6AF3FEAB6F5C96B46737E281BFA3257E40789B17AF395136B500EF7F811D77CF395F13027F76D0F3AF6E5CB6D7CF255B013FC3D4F5D511355F5678139B7B77D39D1548E92F3BD16E0B3AB67F9FD0CB8788
	E4F8F11A22A0GG38E3GGD0CB818294G94G88G88G2DECC1ADE4F8F11A22A0GG38E3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5CA0GGGG
**end of data**/
}

/**
 * Return the ExitMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getExitMenuItem() {
	if (ivjExitMenuItem == null) {
		try {
			ivjExitMenuItem = new javax.swing.JMenuItem();
			ivjExitMenuItem.setName("ExitMenuItem");
			ivjExitMenuItem.setText("Exit");
			// user code begin {1}
			ivjExitMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExitMenuItem;
}
/**
 * Return the ExportGuiJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getExportGuiJMenuBar() {
	if (ivjExportGuiJMenuBar == null) {
		try {
			ivjExportGuiJMenuBar = new javax.swing.JMenuBar();
			ivjExportGuiJMenuBar.setName("ExportGuiJMenuBar");
			ivjExportGuiJMenuBar.add(getFileMenu());
			ivjExportGuiJMenuBar.add(getHelpMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjExportGuiJMenuBar;
}
/**
 * Return the FileDirectoryExportLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFileDirectoryExportLabel() {
	if (ivjFileDirectoryExportLabel == null) {
		try {
			ivjFileDirectoryExportLabel = new javax.swing.JLabel();
			ivjFileDirectoryExportLabel.setName("FileDirectoryExportLabel");
			ivjFileDirectoryExportLabel.setText("Directory:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileDirectoryExportLabel;
}
/**
 * Return the FileDirectoryTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getFileDirectoryTextField() {
	if (ivjFileDirectoryTextField == null) {
		try {
			ivjFileDirectoryTextField = new javax.swing.JTextField();
			ivjFileDirectoryTextField.setName("FileDirectoryTextField");
			ivjFileDirectoryTextField.setFont(new java.awt.Font("dialog", 0, 12));
			ivjFileDirectoryTextField.setText("c:\\yukon\\client\\export\\");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileDirectoryTextField;
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
			ivjFileFormatComboBox.setBackground(java.awt.SystemColor.activeCaptionText);
			// user code begin {1}
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
 * Return the FileMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getFileMenu() {
	if (ivjFileMenu == null) {
		try {
			ivjFileMenu = new javax.swing.JMenu();
			ivjFileMenu.setName("FileMenu");
			ivjFileMenu.setText("File");
			ivjFileMenu.add(getExitMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFileMenu;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getFormatPanel() {
	if (ivjFormatPanel == null) {
		try {
			ivjFormatPanel = new javax.swing.JPanel();
			ivjFormatPanel.setName("FormatPanel");
			ivjFormatPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFileFormatComboBox = new java.awt.GridBagConstraints();
			constraintsFileFormatComboBox.gridx = 1; constraintsFileFormatComboBox.gridy = 0;
			constraintsFileFormatComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFileFormatComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFileFormatComboBox.weightx = 1.0;
			constraintsFileFormatComboBox.insets = new java.awt.Insets(0, 0, 0, 15);
			getFormatPanel().add(getFileFormatComboBox(), constraintsFileFormatComboBox);

			java.awt.GridBagConstraints constraintsFileFormatLabel = new java.awt.GridBagConstraints();
			constraintsFileFormatLabel.gridx = 0; constraintsFileFormatLabel.gridy = 0;
			constraintsFileFormatLabel.weightx = 1.0;
			constraintsFileFormatLabel.insets = new java.awt.Insets(0, 5, 0, 0);
			getFormatPanel().add(getFileFormatLabel(), constraintsFileFormatLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFormatPanel;
}
/**
 * Return the GenerateFileButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getGenerateFileButton() {
	if (ivjGenerateFileButton == null) {
		try {
			ivjGenerateFileButton = new javax.swing.JButton();
			ivjGenerateFileButton.setName("GenerateFileButton");
			ivjGenerateFileButton.setText("Generate File");
			// user code begin {1}
			ivjGenerateFileButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGenerateFileButton;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getGeneratePanel() {
	if (ivjGeneratePanel == null) {
		try {
			ivjGeneratePanel = new javax.swing.JPanel();
			ivjGeneratePanel.setName("GeneratePanel");
			ivjGeneratePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsBrowseButton = new java.awt.GridBagConstraints();
			constraintsBrowseButton.gridx = 2; constraintsBrowseButton.gridy = 0;
			constraintsBrowseButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsBrowseButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getGeneratePanel().add(getBrowseButton(), constraintsBrowseButton);

			java.awt.GridBagConstraints constraintsFileDirectoryExportLabel = new java.awt.GridBagConstraints();
			constraintsFileDirectoryExportLabel.gridx = 0; constraintsFileDirectoryExportLabel.gridy = 0;
			constraintsFileDirectoryExportLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFileDirectoryExportLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getGeneratePanel().add(getFileDirectoryExportLabel(), constraintsFileDirectoryExportLabel);

			java.awt.GridBagConstraints constraintsFileDirectoryTextField = new java.awt.GridBagConstraints();
			constraintsFileDirectoryTextField.gridx = 1; constraintsFileDirectoryTextField.gridy = 0;
			constraintsFileDirectoryTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFileDirectoryTextField.weightx = 1.0;
			constraintsFileDirectoryTextField.insets = new java.awt.Insets(5, 5, 5, 5);
			getGeneratePanel().add(getFileDirectoryTextField(), constraintsFileDirectoryTextField);

			java.awt.GridBagConstraints constraintsGenerateFileButton = new java.awt.GridBagConstraints();
			constraintsGenerateFileButton.gridx = 1; constraintsGenerateFileButton.gridy = 1;
			constraintsGenerateFileButton.insets = new java.awt.Insets(5, 5, 5, 5);
			getGeneratePanel().add(getGenerateFileButton(), constraintsGenerateFileButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjGeneratePanel;
}
/**
 * Return the JMenu2 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getHelpMenu() {
	if (ivjHelpMenu == null) {
		try {
			ivjHelpMenu = new javax.swing.JMenu();
			ivjHelpMenu.setName("HelpMenu");
			ivjHelpMenu.setText("Help");
			ivjHelpMenu.setActionCommand("HelpMenu");
			ivjHelpMenu.add(getHelpTopicsMenuItem());
			ivjHelpMenu.add(getAboutMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpMenu;
}
/**
 * Return the HelpTopicsMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getHelpTopicsMenuItem() {
	if (ivjHelpTopicsMenuItem == null) {
		try {
			ivjHelpTopicsMenuItem = new javax.swing.JMenuItem();
			ivjHelpTopicsMenuItem.setName("HelpTopicsMenuItem");
			ivjHelpTopicsMenuItem.setText("Help Topics");
			// user code begin {1}
			ivjHelpTopicsMenuItem.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHelpTopicsMenuItem;
}
/**
 * Return the InstallServiceCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getInstallServiceCheckBox() {
	if (ivjInstallServiceCheckBox == null) {
		try {
			ivjInstallServiceCheckBox = new javax.swing.JCheckBox();
			ivjInstallServiceCheckBox.setName("InstallServiceCheckBox");
			ivjInstallServiceCheckBox.setText("Install");
			ivjInstallServiceCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
			ivjInstallServiceCheckBox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjInstallServiceCheckBox;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsFormatPanel = new java.awt.GridBagConstraints();
			constraintsFormatPanel.gridx = 0; constraintsFormatPanel.gridy = 0;
			constraintsFormatPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsFormatPanel.weightx = 1.0;
			constraintsFormatPanel.weighty = 1.0;
			constraintsFormatPanel.insets = new java.awt.Insets(10, 10, 5, 10);
			getJFrameContentPane().add(getFormatPanel(), constraintsFormatPanel);

			java.awt.GridBagConstraints constraintsRunAsPanel = new java.awt.GridBagConstraints();
			constraintsRunAsPanel.gridx = 0; constraintsRunAsPanel.gridy = 1;
			constraintsRunAsPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRunAsPanel.weightx = 1.0;
			constraintsRunAsPanel.weighty = 1.0;
			constraintsRunAsPanel.insets = new java.awt.Insets(5, 10, 10, 10);
			getJFrameContentPane().add(getRunAsPanel(), constraintsRunAsPanel);

			java.awt.GridBagConstraints constraintsGeneratePanel = new java.awt.GridBagConstraints();
			constraintsGeneratePanel.gridx = 0; constraintsGeneratePanel.gridy = 2;
			constraintsGeneratePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsGeneratePanel.weightx = 1.0;
			constraintsGeneratePanel.weighty = 1.0;
			constraintsGeneratePanel.insets = new java.awt.Insets(5, 10, 10, 10);
			getJFrameContentPane().add(getGeneratePanel(), constraintsGeneratePanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Insert the method's description here.
 * Creation date: (4/11/2002 3:05:31 PM)
 * @return javax.swing.ButtonGroup
 */
public javax.swing.ButtonGroup getRunAsButtonGroup()
{
	if (runAsButtonGroup == null)
	{
		runAsButtonGroup = new javax.swing.ButtonGroup();
		runAsButtonGroup.add(getRunOnceButton());
		runAsButtonGroup.add(getRunConsoleButton());
		runAsButtonGroup.add(getRunAsServiceButton());
	}
	return runAsButtonGroup;
}
/**
 * Return the RunAsPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRunAsPanel() {
	if (ivjRunAsPanel == null) {
		try {
			ivjRunAsPanel = new javax.swing.JPanel();
			ivjRunAsPanel.setName("RunAsPanel");
			ivjRunAsPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsAdvancedButton = new java.awt.GridBagConstraints();
			constraintsAdvancedButton.gridx = 1; constraintsAdvancedButton.gridy = 0;
constraintsAdvancedButton.gridheight = 2;
			constraintsAdvancedButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsAdvancedButton.weightx = 1.0;
			constraintsAdvancedButton.weighty = 1.0;
			constraintsAdvancedButton.insets = new java.awt.Insets(5, 25, 5, 5);
			getRunAsPanel().add(getAdvancedButton(), constraintsAdvancedButton);

			java.awt.GridBagConstraints constraintsStartServiceCheckBox = new java.awt.GridBagConstraints();
			constraintsStartServiceCheckBox.gridx = 1; constraintsStartServiceCheckBox.gridy = 2;
			constraintsStartServiceCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartServiceCheckBox.weightx = 0.5;
			constraintsStartServiceCheckBox.weighty = 0.5;
			constraintsStartServiceCheckBox.insets = new java.awt.Insets(5, 25, 5, 5);
			getRunAsPanel().add(getStartServiceCheckBox(), constraintsStartServiceCheckBox);

			java.awt.GridBagConstraints constraintsInstallServiceCheckBox = new java.awt.GridBagConstraints();
			constraintsInstallServiceCheckBox.gridx = 1; constraintsInstallServiceCheckBox.gridy = 2;
			constraintsInstallServiceCheckBox.weightx = 0.5;
			constraintsInstallServiceCheckBox.weighty = 0.5;
			constraintsInstallServiceCheckBox.insets = new java.awt.Insets(5, 55, 5, 5);
			getRunAsPanel().add(getInstallServiceCheckBox(), constraintsInstallServiceCheckBox);

			java.awt.GridBagConstraints constraintsRunPanel = new java.awt.GridBagConstraints();
			constraintsRunPanel.gridx = 0; constraintsRunPanel.gridy = 0;
constraintsRunPanel.gridheight = 3;
			constraintsRunPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRunPanel.weightx = 1.0;
			constraintsRunPanel.weighty = 1.0;
			constraintsRunPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getRunAsPanel().add(getRunPanel(), constraintsRunPanel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunAsPanel;
}
/**
 * Return the RunAsService property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getRunAsServiceButton() {
	if (ivjRunAsServiceButton == null) {
		try {
			ivjRunAsServiceButton = new javax.swing.JRadioButton();
			ivjRunAsServiceButton.setName("RunAsServiceButton");
			ivjRunAsServiceButton.setText("Run As Service");
			ivjRunAsServiceButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			ivjRunAsServiceButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunAsServiceButton;
}
/**
 * Return the RunConsoleButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getRunConsoleButton() {
	if (ivjRunConsoleButton == null) {
		try {
			ivjRunConsoleButton = new javax.swing.JRadioButton();
			ivjRunConsoleButton.setName("RunConsoleButton");
			ivjRunConsoleButton.setText("Run In Console");
			ivjRunConsoleButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			ivjRunConsoleButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunConsoleButton;
}
/**
 * Return the RunOnceButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getRunOnceButton() {
	if (ivjRunOnceButton == null) {
		try {
			ivjRunOnceButton = new javax.swing.JRadioButton();
			ivjRunOnceButton.setName("RunOnceButton");
			ivjRunOnceButton.setSelected(true);
			ivjRunOnceButton.setText("Run One Time");
			ivjRunOnceButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
			// user code begin {1}
			ivjRunOnceButton.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunOnceButton;
}

/**
 * Return the RunPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getRunPanel() {
	if (ivjRunPanel == null) {
		try {
			ivjRunPanel = new javax.swing.JPanel();
			ivjRunPanel.setName("RunPanel");
			ivjRunPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRunOnceButton = new java.awt.GridBagConstraints();
			constraintsRunOnceButton.gridx = 1; constraintsRunOnceButton.gridy = 1;
			constraintsRunOnceButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRunOnceButton.insets = new java.awt.Insets(0, 5, 10, 0);
			getRunPanel().add(getRunOnceButton(), constraintsRunOnceButton);

			java.awt.GridBagConstraints constraintsRunConsoleButton = new java.awt.GridBagConstraints();
			constraintsRunConsoleButton.gridx = 1; constraintsRunConsoleButton.gridy = 2;
			constraintsRunConsoleButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRunConsoleButton.insets = new java.awt.Insets(0, 5, 10, 0);
			getRunPanel().add(getRunConsoleButton(), constraintsRunConsoleButton);

			java.awt.GridBagConstraints constraintsRunAsServiceButton = new java.awt.GridBagConstraints();
			constraintsRunAsServiceButton.gridx = 1; constraintsRunAsServiceButton.gridy = 3;
			constraintsRunAsServiceButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRunAsServiceButton.insets = new java.awt.Insets(0, 5, 0, 0);
			getRunPanel().add(getRunAsServiceButton(), constraintsRunAsServiceButton);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRunPanel;
}
private int getSelectedRunAsButton ()
{
	return selectedRunAsButton;
}
/**
 * Return the StartServiceCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getStartServiceCheckBox() {
	if (ivjStartServiceCheckBox == null) {
		try {
			ivjStartServiceCheckBox = new javax.swing.JCheckBox();
			ivjStartServiceCheckBox.setName("StartServiceCheckBox");
			ivjStartServiceCheckBox.setText("Start");
			ivjStartServiceCheckBox.setFont(new java.awt.Font("dialog", 0, 12));
			ivjStartServiceCheckBox.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartServiceCheckBox;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ExportGui");
		setJMenuBar(getExportGuiJMenuBar());
		setSize(346, 377);
		setTitle("Export File Client");
		setContentPane(getJFrameContentPane());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getRunAsButtonGroup();
	for( int i = 0; i < ExportFormatTypes.formatTypeNames.length; i++)
	{
		getFileFormatComboBox().addItem(ExportFormatTypes.formatTypeNames[i]);
	}
	setSelectedRunAsButton(RUN_ONCE);

	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args)
{
	try
	{
		System.setProperty("cti.app.name", "Export");
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());		
		ExportGui aExportGui;
		aExportGui = new ExportGui();
		aExportGui.addWindowListener(new java.awt.event.WindowAdapter(){
				public void windowClosing(java.awt.event.WindowEvent e){ 
					System.exit(0);
				};
			});

		//set the app to start as close to the center as you can....
		//  only works with small gui interfaces.
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		aExportGui.setLocation((int)(d.width * .3),(int)(d.height * .2));
		aExportGui.show();
				
	}
	catch (Throwable exception)
	{
		System.err.println("Exception occurred in main() of java.lang.Object");
		exception.printStackTrace(System.out);
	}
}
private void setSelectedRunAsButton (int selected)
{
	selectedRunAsButton = selected;
}
}

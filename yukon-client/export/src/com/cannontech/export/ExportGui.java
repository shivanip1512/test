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
			string += "-Dshutdown.method=stopApplication ";
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
	D0CB838494G88G88GF4D859ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8BF49457F54445B91CD40DF1A3BB58A689F6E40726B8A50D6CC3925A9217B4B8D69AFA8EEE7A412D9C2B29DB13844734A109CEB37A0150878990A0C036A590E241C2889036D8AD207F872D7E2B8F3692EB259DC98B2B5D756E88D618583D773D39EFE6C7337A3574D467DC4D3CFB5FFB77736EFB6F3EBB33A3653D39A2ADA9B9CF121252A4756F1DA4C92A9F1124520277338957AC15E4CA3A3FA200
	BD5264D4AADC870096BBDC1219ABBD72FEBA14BDD09EFF37A473A05C6F13FEBD38759A5CC4603381E8153F2A2B1B19CFCE837159494A7FD11585575DGFE40C58344CB48BF5B162F6257C0F937FA0F1014ACC90B13304EEB9F676B7045C93C5EGDCBDC8CF46BA96DB01CA0F839C56C9G581E2475390BC9F72F35D2AD8F5E3FC25A765DBD49E830A4DD7F8483493C6D9D75DAA8BD448C11A2C54B4761EA6B3CB1F6F944B7D1B66274FAFDDEC59E99AF13C3FEDFC031DA9B5835E9528D7536B0798EC9692C2DA445
	C2B91402387BA534C9B95CB7838C81F0997FA65B264A90FDE305947F455408B22ECD61324527E8B2C6FF70EC6A52D46C3F4B162536AB0472GG1765A19B4932A915E4A97D986BE625016DGECE9A44B2593A44B4007FFA2ED9F2EC84E09A00BA3CD35D71AA6CB2870400EC3EC7C9FE944B1DC83344DGDB8172F614E4D6GB8G0EB03F6BEBB885571AE1254EB7B1617356787D359ED7204D6712BDF0EF3582C545F54BA33E00CB1238EF3DA1151A48C36059916ED71AAF74CB648B2B3A1F109465D4F21A09EE31
	4F6FCBDE2476913DC353ED638E34DBDA925AAD966E1354FEB9FEAF43676D5470FCCCBE4B469C4B65C00B5EC9F69E58CD3258D39F16F24F3E1D3C53C416E54F3C153CFDA717A1B7DD13055BF83299EDEC837CG009FE08940CA008D69E463958D67E733F18B78F2406B7474CC8E7B1D8125F6526DF1498155567D3619ECED863C1DBC6FF65CAEAB4F6B47E87357AE185AE5734A4349892ABD92F5F6613CBF9F11F7412E693E31DB0C618AB6A74AF771B1CC9178AFB3FC358E4F47769C43E33993E831GFCDC76DD45
	35A9BF837C9D408E6082D88C3082A0B60346661506B3F3747FCE5D387036B10D8760AA07140033459BD41C5E91394F99F0BB07BDF2D0D26BBC590876988EEB7727ECDE0EG0FA659AB871C0A5B67055E1D93B2B8C1105779E6639E2C93109955611E10014104DF22FE7F32F19F4EB3E7D069753B1C0A2C556074679AF1ACEFBA7B20C768G6ED3B351DE97581A3889745F1AC1362AEEC5DCB35007B249AE0DCDFB615A8ACEA9FB2C56FA77880A618CCC11EFE5A66542D5017AD1GB1GEBB2E9DE7DE4007C27BE7B
	F12974798AD37FE17BC6A6770504CC237F64337E3332CAB2CB816CG7EAC5A57BE87FB5C7C7C1AA0CA6523E4067BE00CDAC69DB80F27974CA3CD3D26184CC91AE35C6EDF31236FC603DE2B81B6E6116DBF51D448FD32569914498BBB8260C032CB9B89DE7723E65C9B641B32D729177D325785B7C155876EED5E25796728CF784E6FB3BC2EB3F560C0C1013F3FF98F47C39FFAEFA77AC30C6ED41400FBF8D211FBE4C5F1FB4734760FB2D902E1CBD8D055F5F59B6A7AB268A8E5039FE6132E27DA76697C4C6E735D
	1874BBF086297E7A945BB70B213EAD1B4666090FB14E999265552C2C51ABBF1E4B58E17B050CAF6751B5A79EC69F18ABF05B3CFEB5725896DFB59F5BD3D74D47367C6A4CE3D7F955EC6CB86D4AD574E95FAD2F4E976A2F627AB06273FAE5661022094463351FB8D08F37572D743BDDE33252633EADCB528A9CFB494E5620905CC7E5531AF16B982D9945379F15563D1FE39A97ACFB3CACB9C19D376D595A1A41E5EAE8C59F13BD7284982A4EB769D5B83E152DEFAABE1842F0BF2D49006B55B659BB59224893E779
	D86D139659427067881FB501FDB43ABDB212B86E7BB7593AA9FB7C0863FE7ED2BB4A103F9B56AD007A5D64675FE9BD2473F3F5B25779BC9EEEBE20C56DE636C94A5369BB381B476D3B85BD94114E637618A4FD5C3EC925E76514E4968270BA7FFE4B2CCEA5506DB9A4EF2C9D575F207C41248C5B9ACE48D6F90A76A67BDE832D4F691954763C3F34636144231B4B9C7FF41B79782CED4379FBC3743F2EED0704FB976F137B53EAA776698F38A7E0694345C955B74E26574585726319E689DC178370364BAC68E37A
	73C1EC8E369D916703B382F71B613E84137C4DB40A075AF2756797D7700429EFB21F4C61E31115EB0C9373BB315FD2405BF2494656F67D5AD7938C7AC65CEC3BD76D75EF6DFA3F65381F3423FF0E3A83C10899DDF208EC3E2D9DE545F8C0C52BF67B3783C7256971CD034B2D7882A2789CC187643A7E2ED1FF8EDA1BD3101C99C157CED5C73FCED7CB2B124A79DE4B40F305B82F3907A74AA6EFCE14B90715B29918DB2DEC0A75390313CE8F9BCE754C36B9A9DD15FDF52EB8A725303D834A521E124C7CBDF426BA
	9A223A65FB282EF78A6B0EC3F9914032BD64833FB81CCF72DCF75EF4064A0237E07D83B19AB16E627D3DFD897B4375FD3568AF35037106323497F86F25752A7F1C5EA756459F09689365FB39FD6A779AFD6229AE64B784F8E5AF79042FE30F4EA7604C85E165984CA031A7F426703922160BBA53E82EC894D77C2C9B5708A550E78C40DA00CDG5B840F2FF54EFA9EE9884DF09EF9B149AC364F5B4775336C1B3E2F2A31BD9BC75BBE9E43A7897C9FB1FC338E2F4676A9F416F6GED4DBE5A5FCF044C789764F13E
	65F911787F1079C6FD1E117FAB8C5F19E7642FCC917FC1202D4BA37ED51710FF71FE737821520037C3197389B1DF9A5AEFBC5B5C43707E7D4633CDC6871DED9681ADE1BF456BFB9CEC5FB9GE74E83B42659DD59BA3F6974393D0A7019EFF461FA6A8B38475CDE427D854341091EED6A9C774B6E4871C6D20FF93C313347BC5E486E19B9165C5F93A99E61F15FF6368E0C03FEA1G0E7BCDB26296C3F97981918B22F9D43F7C935DFDE54AC26377F94400F715875779F2086C30CE58618BA132C31C30434BD31A6E
	2F6A6EEFCF5DFDBBFC662E7260BAF73335F9AB5A60A05961C69B5941A2F0D1EC4F34C3F968A05966238E0CAFBC223C041523C4B937E3B679316D7F4CC6A7E76531503382821FD3879C38FE04C479B0ABCFC9944366A4235E8ACC625587B1FF3C71DD74933EBFFE75966E7323309942B46C714BA3D2F3BEDB8313A93E5A265AA0BDDFDD67614A7BF96DBD6C0757FFBAC475172A75E2546BFA38F2B95CECACD9GD06B8C588EE13BC51728DD8254CBD45B949F6267BE5E77BDEDD4E7B16017812C8178BB006D07286F
	AF3623BE0ADBE1FBA6172F6BB71230413359386E70DD3F8EB34838BCF931AE0FA73F5E210FA7738FF37EB638F69E263D7F7B6F69777E5C07B313E95FB00E4B78E1B55FF0D85B7BF92C71B0DB4B304DCA20459FC65C06E4FDEC0FF21E84A8836074DF065153F4F4AE57F347A9F6CBFCEE2564F96AD8449C36658817472EDE37E69C116EFC7559D4B5868B11EC0B0F10EC99D3B34BE61BB24AF6B0155379DE3672926D25EB214FEDC7A8AE3B6762E149B4AED33D837C1945E17F425A179640198AE0300046A2D4C2BA
	2FFA62E1493E6802294E710E02E425006B3A2CC09B0B284283BB384EAB5BC867CD8524735F3669F5C27FF9ADD92FF3FA3B51A6B742ECE28D23233CCF053577349BED76D69227F35B3CF6B2C24C0A9E6B61F23F5AC1B1F0566B9433967730B8874A03G4A6B94B7EC7AADDA0C2F8D8F7C861F2774B92F1B1E882E823B0E616B90FDBA1CE3922DE1DFC8DED8BE79DBBBE90CE23F7118346613AFA5C5BAD3C43D4E47E615FADD326B8843F379CF8F21BDB683EDBBC0419BA51995G4DGC3EFD09C740F3FC199B10285
	05EA9445B9B24ECECF24D91FDB3E2521F121D3E3DA5318F276F57DAB179656EE6D3C5B71D8715AE59CDB658D9EAB26893C0B61176870FC5E3D42668D161783CDFA13FC6113CE34258B4A0BG16812C8658883065CD32E571EF4B25D94E898E1F5F8E8123C79762F17D1EECDB18DD7E334358CEAA54765E3C42C8317DB2667316C22E7FCE010FE178EA9D1E5B659443E3F9GE8EB8B49AE5F9B46B9D9D884EBBE40900082302408EC52733EFA1EB6712D8657986EA978A4C021F97676D064187A52506C4F8F9A0770
	3CBF6CD3945F8476CFF87B904A2E787C8C49F1DDC3B85FA04C9FD7F8D5955FBF441EB74B238C4D6D7C7EE5444500DEEB0AA88E3A4EF0F1D05EAAF02FDE7A5F0E6D10A7DEB53E99177F37F9D0AC15A6ECF01FB372B8BC68B4BF5BACF71A1FED9EF34E1CCB7D32B3F2AE752BCEFDAE15472C59ACB714F79462582394477E2C1BE2DB7BD1325367194F7A213C84E00540FF196157C2B99E5B9FA3FCF94FDC6C0A7D1861179870FEC31973E819930F1D9170E90632B18666B6FBFEB87258ED9E1EFD8E7D4BB04B23016F
	BB267CB2477D7BB0BB930F0E86E5B1D7FFBA1CABF0AD41CEDF500D49383966FFF97C7805C6FD7CE8B9464FABF6388E9D237CEBDD0BD91D90DC230ED19CDB5C0972F91CD33EC9100F4748D51D54AE866A2D85C8G580E6D20FDEEB156595513AFC59E875EF75C8E5CFF9BFD0189A78BFEBDAA7E1F5E433526FB52DB9364E8957F899746D46A33CAD9A52971728BBD46F899E599GD9BC45246F9F761A55D98C74E522CE54C533BA2B013E3E18E245DD8771DD198831221ABF3C50C4B95D4D45948FEDE8E26F859CAF
	49AC8528BFCE79438FDBA8F69ABCCEF5AD6C99C2B414D7812C87607B45C8A7452F1B8117D7C2B21D9DAA504BD4663C25D82DCD81372B56B9E6E72347F96D69267852DEC23C2EF4E31F0B213C9CA03604E25A6BB7A8DEAA7863C7D81C9AA90E0FABD1637792AD2645AB67395FC1794C72934473876C7944B8149781ACBBC13678EE2F48FD0A3ACB5DAC7789E549D21219EF213A6BAF0AFEADD4B7D0487A0572A200E596DA032CCDE6F140EACB6473B45F37ED4D585FFA0BB1D75648701BAC46DC59DFB7D12EAC81E8
	4DA7097F2296B37E2BCE0673DF27ABF3BE4B199FECBAE90C333E4470AF1FB446D9E52D94E7A582ED6894717FFE574262209796586EC07742624A2FB1DFDCFF4A189BDD4B709BCF99F3238D5DE2BEG2D7AAD0A1FEE38F09E47C0F99DC082C0A2C0EEA96CED2594C3AD9E417C33695983105DBE9FDB1570F46EF4431A43C22A0093B31ADC47438E3D0EB6683733D49BBFD7E92498311565D3C32546B16DE37828D263183E50CEE33A82E829274518766A790F837E4EE90D675253117827DE477EE2CE9B5FDB5A4370
	6BCE9B5FDBFA50CD6FADED86DAF59971FF76221EFF8C605714E9BC634AA2717F911B2371E5C67E2FB1FCE21911FF281078671F819D4FD0EC52455658F8A8EFBDC3B255B6E3EEA9D1146D2C1CD7CE65CDCD7AB8816B4D47C767667BBF8E6371BF49271981577D33AD24FBC5B959E3BB43D5C3F9D0687B98FB66BC8E65A58277A44345C0F93D40D5344E94BFE15B054ABBAEF8344D484398034D87D2046D3F5EAD097BE7F4772D5DF7BF7F6C3A2BBC781A2538A247209F3AE60FC1973B4C4FF11FF2CDBF475D67B2BF
	47BD60521F63386F1DE5FBFC820C75F6610FA106CBBFDB12D9F016F0BF6F2573C475D9720BAE16BBDA8A655582B748F0F1D05EA2F0AF35AF443E58E7A45AD29D0D73C8DC900F19FCD7BF7F398DFE54CB364ABAC7F6317489DB895CE37D42D667449975BA1E8D16C2F995C09C40D6C1BB48E82967CBB20B816A81DC6709365CBD2036B34A9727432DB2219BE12BC99D4E63A94643EC3D531F3B66B24FF7196038CD62D94C378474DAFE1E6CF4420A38D5D0DEA7F0CFD4116D92048D369422FDAAAA609C846087082E
	A05AAF98AD964A1BG368164DD904FED8A4DFC825BC6125F2F23A12F0A996AF29E1FB565E1E607C8B61DE91F61672771267067AE292312207DE0876E318DA137C22F9C7177FCFE572C4F8DD700CDB0AFB3F8011E39D40CE9FD64DC457301133DC80429BA2757A551330F7EF12D5EB3AEFCEF874F2B8E1F5FBD92A40EC43F7CAE76D3B38C07960D243EF7744F2DC6F9B0C76407AB27BFEEB723AF3EC06759E6871EE707FD817690B8668236F95F01F6D9A66A5C6718FE66FD42C1674217AF70B32F74B67888G66BB
	065E46BA3B7B6AE798830ADBD3D4A805B655G7B553263EDAD3665F2DC4AC6BB61D91F1F114126433EDADF08686F366504516D4EE1B891F3BBFC3AC3EF07152A1C9B5FA6BB794E6B6989AABD7D1D12CCEE131CBE34C98114397EDDFD243F659D2EFFB5DC9D6F70DC8F577F6B7D33691F22838F34DD740E26FF74BBC67D7F33825B5538EE62CE662A1D04BD4BCB347B0AD07E9E4599D0FA6440CD7708DCB7AE0F5CG6BF07A3D9F63DE4836C10F47D0037A30D4609A51BD69E52984EE17566BFA3B2CD0755B708E59
	6D093067CB1BD5FAEAA551AB9DE6747CCA4AAF7C56EA4CAFA03D9468551564FFBF3FB85DC7F7DB59EFEAC41D4B5669BE5AAB7AF6D572B13A83572515BC974747684F2AE69BA3DACB90D6E2BF155A98ED2CB40E517BA338EE5486FC3702B29FA10EDF47F2AD681B756E00BC82672AA97E629BF3D2DEA7236F00310E839627C6376CF1D17F1FEE45B9C0B99F2CCBFE406DFD3A48B8DE286BE6B19E2F4AC6BFC77AD6C17FFC58332F540B1CDEFA11683DA74C7A2F3EC8F9178DE99893C5488531150B67AAE21BA99F64
	38C8790B83F642C5895CE601DBFF1146FA528197759956A5D97F4C377C43D712D2A4731CCC62C5759DC0AB3F8E5A0ED00E42C1E7E69B50942B102717F2C32B2C14CF296F453A89D0CE84482BAA49FC1349A4317793795F56ABE6B83E6E1F382E497EE216BE1FD47F0317120EC5103D34CA7DBDD1D5F8BE095BFC4C934166E201647AFCDBBC130C2D2271FAEDB44233CC66173C5D9367295D16AA9A0363FD2881375FD49F592F5886FB210D6C772DBE32DF340D6C77D3D6B78E4A89G0936795A6F89FC142B6A1D53
	F348DCF61A922A5E77DCCFD7ED1EF709743E5C9749E634A87236AFCCD1DB7BA5524B67A05CEA01FB50CAB64A3BCC3ABEF08CF535C35985F0673248A1DBB55B7D4ECA360B86FA5CE56A6F5BD632DD6A956A6FB5D65786E58700674AFCED77AC2607D53DDCC5244362AB4453B2CABA2C3DC2F6BAEE894B23EAF692AB93EF7BFCBB659E37097E22CED0FF255594BBFE3D59AC8F662A1EAD9EBCE2B7EB971A355D0B266D22E6E84773E5B7A7F0FFD9D1CD393107477179E2245CD81D4FA577A881308C715D64D8D81E70
	078BECF724F7E17945B5ECBD0B2DB666973F45706B2A0D79452B3D642FF1C0AB2F217C62173C380FC6C3F9B540A600ADGD2AD6C75G25G55G8EG8F40E200E5G2BG5681EC84C8G58DECB7676DCBFB9033D55C43EAE83C9F6786E34675484D9F5A51905F5114EEAFFFC5E2CDD05DA3FB9E23B0F7B96B6DEFF3040F6FFB56FF6FCBCC77AF15C866B0C636CE17871BA63B87F07D85395207DDD9D4D5FFDC5E1B95EFAEE17CE756AD22F21FAE39E6107266FA2C6554FFE3EDEE128938B658D869C6721B84CF8EC
	1E830F2D06BA298D60378DE6BC9E319A6DDF2A2BD75DB01D9737732EE234272341789E44FE0677B4985F03F864983D873198E8AFB7107DFFEF9A7F39794D7D8BEC775DF93763FA0D315F385B9B0D7E66E77801C6233F7DC26C03AE20EDE8A4FD5DE17ED65A446DDB2DDE9DF09DEFB29B275323E6F25FD16BAEED0AB41F7F21BDAC072F56DB23DE63A2363B7F04993FF8357EB613F6BCAF3331197256694D142BB9E7A7DC1E40ED973842E6DAA3EFCF6019A798366F7CF92FA4E47A0E6E3E33776E67FB5DF715077A
	9BA7EF64FC6FB35E59733D4FF94D733DFF631D1E6F7DDB2FF93EF733D71F6F6538973D98670C413EB4A06A7DC8602E895CAB2CCF44753BDD4A38403F4A64C2FC330C8C897FF3A657AD069BD0F109BA5CB57A9D008FFD81D49E77095F8D6C70E18CB36A76F8F8B90355F1FB0332A26C1163C3995DFE27CE6EBC013BA67C74C5F6F6A8855F2B97BE79156B14D39C9038E758D9E1A51457885C098A2A373ED94C6BFE4DB7833A7BDF755DFDBF4D6B3F7B6F451FE271F5DE8B44BEADE407BBC5E41BEA01FB21C83CCFA4
	F0556C4C319C4AEB85AE75B662B6C2B9DE605C8C37854A098217B5C87D65DEA55C669E76FBA6A8AB8277A0BB43AC0172B2017B954345C0B9D660AC966AEF03406D34D2FF792D047BD306AB05F235401D35D1DBD7AB0DFB8E5B6FE2213C91E00B409FE178ACBB588B40EEA77C234796BAD631EA7FC67C46D6E3EC720D85732067ECC63CBE274FF9CCD4E9BEFED3F77FEB6B5D77777C2A3B6F6F0B189FFBE05C6E58E97C6FE16F56AC05728A017BD7717E5CAA01FB0FBDFFCD04F2EE9B616AC729ED0140BDBEAAFED7
	A2F0096C5C99027292016B4CE773874A2B846E29FCEA3BC660D61EA09E696D045BA57CBD2F1DF27A8F61679D543C7D5A8F347BFF46D785553C5C768E362FB8C7EE0C85FC1318680FE9477EF259BB04AE387737D34E715B57E74AB9D27E0AF20EAFC33B44F63E6723DF65F698F30EFFFFBE7CB9C6B29E07A44E6BF245FCF270859D1C57FE95DFBE0DD7CD916A697438473C780E6DG69F90D0D3F8D6AF7F6105D0ECFEAF2ECABA2B996F5CC1F13FA1DD147E82833920072E1EB274951BF05B98E586C5C3771FD94CF
	8DC9244A73419465CFB6F4D05E65486DF0BBB55F5446EF91230DC2DF3C7D03ECED5D8EEDD3BBE95C0E8D128E951D66BA68F3454DD0E730D353415FE95461359E1C1F3297839417AAF70603643E53C9F2BF6988173BA224493D37B7DCEE8B4BD9AE05364B055CD7ACA4F75C9C640E07BA5BBAB59FC86F526466BC4AC5EEAA2F0BE4FC56A47F9EAF726F16AE759989DC9DDD7CB9AC172D59C632F93A264B2637975AEA9154096E52726FAB3B0CB60D766333326EC9EF87C4FFA28F48E931A9391C06F9831FA7ACBF2F
	303CBAFB47D2FDBA921ED7EFAF56673DE3D59D623BA8AF3E4BE6C65FA6682F9CB32327F7137DBEDB94BE0ECF3279D4GF40B2873D0F7389F74CFE9736B3F3AE71A67E9D278BC9F00BE910E28F8CF37510E01D69C4F89E7007DB615733BFD9B79717B4FFD2871DEBBBA1FB506F8533C88CD633D2FA35C96A7D96C39986AAD9536984B8F37453ABB1ABCF17973D9DF5729726CD76D34C9A78F6F7B255FE9FD97DAE66BDB8F833A3E313F78E9FDFF923BD675FE74FA89DC69F555321E2920A2CF5479BC3EC099BEC43E
	A957B97D6C2BC320B5BDC3D8F1CB7F7C23B223A8626F5D37F47379A575E84FBF6616BFA6D937E23726EDB8FDDF0AB9BD7E8A574FDFF4CC2FDF1DEF426F734212DDD657506EE868FE3F465E52545E8677EA0E7A78D14A9FD7764CB7573D613CE657A5D3C5AADFB5FDDD56037F69A7A1C1153F50734D0BDADBE990733EDE79D621103B919C53F25465F6FAFCE3C7CB8B4BCB8B4F1EBBC9F9F547A0496CB715B95EC466F8D566DA2B46374801FD0CBABDC16D77123917B4FA01E5FEF279ADA4574AF94BF57E32463749
	2A170B67EE522BA8F7938FFD67BBA87F5FD9C57BC427031EE1FC446AAE01728A00B5226E140D645B68186FD8BB2FC8926DE187EA5F10CC1FD7F4BBDDEE9F2D691C673D45744CA23D17642836912EDA600E0AFA21DE0AEDEAEBB51B5C6C1E6BD8C41EDBF21DF63F24DE3BCFE854785467AF1C8F59EAE733567729963C67797B9F30F7A5D703FEF13D14DFFACEFACB0A1C036F8C787CF2C01962D8762599753CF16A28BE9736857A137AE64E4172F65786756D72200DE5CE6DA6ADE1B9E9E853B927F63B2B961693AD
	D8E03B5EF93763B9531F31B9636AB366D2FF45707EBEE3AEF531180FF700965FA75E5B8A7B76DFFDBF6CCD7DF33153270A4D647628ED97295768FE63393A4174DB03AB55FAEB556B46E96DCE1872CBD06BEDD72FD983DABBB5AF5702776277DF5CA5D55ACBAD98FF61F72FE02E38EF334F7970A7F67C5CD743761A4281EECB0EEBE4384AB01CFFF0FA3D4941697522AC536B5DEF5157633AEE57658212EC5A7D276E6A191D7378627FC1DE60819B59614DA442BDECA3BBBCA06ABDDACC39B147G1D45330F53D9FC
	C860E2581E329A4A9B85EE50C6ED93846E4FC46EE92B402DBEB63D6DE541377A1A780EC731484989DC57C5FDDB3E57B8CB32B41FE269DC0EAF88E5EBF8CA672AEBEEF3AB0EB10FA50B0F4FA716DC8E32A0D05C3C7A1AB1EE7EE6554CE762672B44737DEB340F5D30EB72EC324DA6CF5A8C723CFC4D985BFE3ACD6B7B6962597A5EB9C35F5BF4FDC75EC31FE66F37F3DBEC2942FAB344FC7606C6075A6E6FFB34BE92593BC57C5B4F45B77107372BFEAC25A9D2AA3EEA8597FCCBD3117672F7378A91D9D867F1030D
	0DD552697707410014A3766B9616FC0F3FF95E1A725297D312255496E987FF9851A22562930596A90DBDF0E811D2941FBF64D70D1EC1907CC07AC85A2147E08DF3AC4802AF30AAD2A64AD248DFAED524B476A629A2E5B169792B12D6A8520B0505E5E5E5D00B3DCB28C8BB55F78695E987FF2BCD70423FE495A4C54A90AF2DC1E787C72C03D313B7FC5E4191E61DC17EB568C1C54A26F757187140B8F9E6871E4B9F8DA9E5FF7E19A550F7A8A06D561D9900187478415F4B3D6F7CE432145EA86D00D854BF6E843B
	310FED59254CE1E6C07CFD92881DEB4736E7206D9956364AAEA523910EE8789144F3EA0F5FB9A2C315DDAEE760866B81154DFFCF5AB59A989BE68C94A9793D467700ADABA17930DD4AD04679D7CBC2E03103DA571D78EE02CA28E203490C66E7FFE10644BFFC32A505E14547468596B10754EB9AD8AFBBAC947C762397FE9477CFFD4194E9C70BF488CF50BDF200EDF8322B5E2950D7E79B25DDFE7C9295FE7C917CAE48AEE9C1E54A83764B9B6D46AF8842D038EAE8AB85036D67DACDAAEE4F19E6E7F0DC412C95
	18AA550B5F471576F23247699DBB4377D52EEFD2CDB5D3A00BE9911076B1F93117B2514B3EEF669CAF3A1A30A6C5CAEE14B2BCBE272B51090E54A26DD24683F2F05C67F1057CD7F8C76937F062638FE5D3203BA3C2FDD4368CCF46C2689B214FC5E53F1C5D48FD688C2B0983D1A6EA386A461D81E0A587DA9474C9DF20D850BE7106D2723D26030349D2122AA48E415190CCEDF0398937E78AEE6AD9604D8627C481E983CEEFF094A694C8D6E8100CC802C159E71EADFF600FFE79AEE320B5D524B7B57F9913FD93
	E7EAC6C75DDED9F4GF6056EDF975DE31DB2C31D3D217B77DFFD7E5743608775D24AC4F9B97BFF167DBF077F4FB24CD906B9FB8E0C3BD51A714FC88F114E4CF944920743DF5688A3287C430F1FFD6C9FEDDB3700F60D6A5404C14855EA35CFCE8C4B01CF2EFA60754C68785F47B172232B04305FD321887E0E5842E9DE055862696E01E80B98A4AC3D452A1EE67F4B672DB236BA475AD688C92D61123E93A6A9E2D375707FCF7E5AF079854C007DFFA57F55F9498F1BDC7724373043AB9762F73995E9B796DB3C05
	EAC2D8C554848B559CF0583EBAFDB1B647CAFAAC6569CEFE2C4F53ED3FB7B97237CA0755734D08713B149E76CC2299702E91BA232F1DBAADC57EDED04F846E5CCE4F0D9A2F8B3F9DD4BB25485A77B0BE380D7D87290ECA643450ED147B3A2E4DFF83D0CB8788370AE2ADC0A0GGECE3GGD0CB818294G94G88G88GF4D859AC370AE2ADC0A0GGECE3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFAA0GGGG
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
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
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

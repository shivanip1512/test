package com.cannontech.billing.mainprograms;

/**
 * Insert the type's description here.
 * Creation date: (3/4/2002 8:36:18 AM)
 * @author: 
 */ 
import javax.swing.JOptionPane;

import com.cannontech.billing.FileFormatBase;
import com.cannontech.billing.FileFormatFactory;
import com.cannontech.billing.FileFormatTypes;
import com.cannontech.common.login.ClientSession;
import com.cannontech.database.db.device.DeviceMeterGroup;
import com.cannontech.roles.application.BillingRole;

public class BillingFileFormatPanel extends javax.swing.JPanel implements java.awt.event.ActionListener, java.awt.event.FocusListener, java.util.Observer
{
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
	private BillingFile billingFile = null;
	private java.text.SimpleDateFormat startDateFormat = new java.text.SimpleDateFormat("MM/dd/yyyy");
	private javax.swing.JCheckBox ivjIsAppendingCheckBox = null;
	private String inputFileText = "";
	private com.cannontech.common.gui.util.DateComboBox ivjDateComboBox = null;
	private javax.swing.JSeparator ivjSeparator = null;
	private javax.swing.JComboBox ivjBillingGroupTypeComboBox = null;
	private javax.swing.JLabel ivjBillingGroupTypeLabel = null;
	private javax.swing.JCheckBox ivjRemoveMultiplierCheckBox = null;

/**
 * BillingFile constructor comment.
 */
public BillingFileFormatPanel() {
	super();
	initialize();
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
		java.util.GregorianCalendar  tempCal = new java.util.GregorianCalendar();
		tempCal.setTime(getDateComboBox().getSelectedDate());
		tempCal.add(java.util.Calendar.DATE,1);
//		java.util.Date newEndDate = getDateComboBox().getSelectedDate();
		java.util.Date newEndDate = tempCal.getTime();
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
		getBillingDefaults().setFormatID(FileFormatTypes.getFormatID(formatSelected	));
	}
	else if ( event.getSource() == getOutputFileBrowseButton())
	{
		String file = browseOutput();
		if( file != null )
		{
			getOutputFileTextField().setText( file );
			getBillingDefaults().setOutputFileDir(file);
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
		getBillingDefaults().setBillGroupType(getBillingGroupTypeComboBox().getSelectedIndex());
		getGroupList().setListData(getBillingFile().retrieveAllBillGroupsVector());
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
		getBillingFile().addObserver( this );

		Thread billingThread = new Thread( getBillingFile(), "BillingFileThread" );
		billingThread.setDaemon(true);
		
		enableTimer( true );
		
		com.cannontech.clientutils.CTILogger.info("Started " + 
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
		com.cannontech.clientutils.CTILogger.info(getBillingDefaults().getFormatID() + " unrecognized file format id");
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
			ivjBillingGroupTypeComboBox.addItem(BillingFileDefaults.getBillGroupTypeDisplayString(DeviceMeterGroup.COLLECTION_GROUP));
			ivjBillingGroupTypeComboBox.addItem(BillingFileDefaults.getBillGroupTypeDisplayString(DeviceMeterGroup.TEST_COLLECTION_GROUP));
			ivjBillingGroupTypeComboBox.addItem(BillingFileDefaults.getBillGroupTypeDisplayString(DeviceMeterGroup.BILLING_GROUP));

			ivjBillingGroupTypeComboBox.setSelectedItem(BillingFileDefaults.getBillGroupTypeDisplayString(getBillingDefaults().getBillGroupTypeID()));
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
	D0CB838494G88G88GB7E98FADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BD8BD894573926A106A6C609B18D2DB617242631B7762BE96D0DEDF2DB1A52DB5B58565C1A565B52DBC839AD3D35293935ADCDCD9DE0F928A02BBC0D882BB18604A23ED2034608BC84DD11480A28A43230833BB07B486E200B22397FB9E77EB3333B33BCCC53AF617BFEE74E7FBE7F794FE34E1CD9050272180C543482C1C84D90143F3F278A025DAE8807DEFE3184F131EFA69A844D5FB6000D425981
	02DC8A347607938DC66105FD59D0CE023269130906E2384F973636541E071BB0F632017664525B560CEDE7F19A5A49226529AF6540F583C0B260F20EA69A3A0E927B773FD42260EB213CC139A7101AA688B9750467FCC90986BFC761CB006BAA00A44A731517CA953A854A9240A200A5752A4E35543B5B6A8EAA656E3BE304D55FADCA4BA536B47A912A0FB159F665EA926E2701C897FEF978GDC8F378F8D9C69F3BBEB7A2CAE175BA50BFD761ADE07A4B9DC83B5CE2B4365713A873CD6272F2E6E9843B6BBA411
	446B9871773D12EC2ACB90A221ECEEA038BF15109899603E94E0BFG7379695C430ACF3306E204044F2C8D6BF3D48373F5E1036AF34C5463E936C6227FD95A86C4EE95502B81476AB3F070684D5627596DF5DA65F62BCB14D83DF6ED41FA4DEB44FA4DACA6F693201CC27CE842FA3D71AA562B6B5A9704145E57537242542B22C9E90BA635DE712F4CB6EC2639E5B1107C18863485GF1G49G594D09068A00ADB4277352F741353ED7EEF0BB1DEED73D47F3CC32F9CF39ED228477F5F5C0D1F0E744BE3757A688
	ACEFA3CA2AF57CC1B85A9C1833EA1E1D93B04F661F792CA04B3B53B2F46A36701912B40B22435E2C56AD3A1944EDE8B309DBA45C27AAB1E578E10A0F5760D97B3EC8730714D7G2D7DB84639EB837AE2B6FCCAB06EB914162563CB741335E98B0FB39F169CD7FDE1B12E49A4B1CE81FCC18B648EC0A3008DA02285E3FCBEFD5FF8B1EE053C723A2CD247F02F476A150F8DBBA41B68D5E21D7C52D8315683A67747C94B31384C5B27ED2378AF556946A5FEDEF9DACC8B0B472296B5AE4C766EAD61ECAFEF894D0D8D
	3C8DB3E9DB2DE889EEDBA3452FEE89EE5B68E2EC5B1C56C4038700354B12ACB26EAD0372CA006C93B00E839C856886701C4036191B3EFB02795F2EE997A67B3621882E22DF76DADBDDBE596A6A93BB2DDE0735D792FD024253CF47795EG39A15AAF7B4046F151A5FA2D32436D826DD62788C960E3BC32E1A361710A14EAF1B8C5B060748828F754108FD7496A134FFAECD6D9D4B998BD026678E5EBA7E88485F09FFB0244EBB69DDF2360FE7E890C551F33894EF0924657139817B506CDF0BD89C9A9CAF5F50D0E
	BE620655BB0239756EE6B20F551E443E74DC9766CCE36EC3C275B32F6B668C0116132C7DFBCE86674CB52AB372A44E0D691B3BA6193F41D0FBD2BF5754BED27A3EED240FC1E3719DD8C7F2F49E7892871044E3DC17E1E239F74C6A93B15BCEFBA1D1C41B9AF1467B77CCB2870817C5175CA8FAC4178DEEFCCADB9F49DC2B66E13F1B67C8A3451371248192454771A7B2B7B2BC6850E6B5524FD03AD5163D0E5EE1D96C90E59926B5D57E82754597B0D479143ABA5653FE8AF5ACB0A79AF618312E3F484C57641359
	6D9E9A76D8C8CFD17232194E0F87013F5D0C6D1FD3C272C16665A2DAD66977973F1F36A3FA479FC39FFBDFB6464A0D8DBC1EBF4A8A5F36BF4B52EF5BDFE469376D2F3346EE3B6733745A0E51D23248DC6C3E625264420BD9E49C68F33BDCA2CDC8AEA230B552F396D28F074BA11FF35886C43943F19596F38B4D24CD3FC8471AE9F09FE346F1624A4BB8CE6C387A0090FB29C0F77E0FFEE8DFDA0A52BEB92754F102797457EC12E322A4BAA1D08D6EE1174C70063559AA5E17CEF17FF3ED9DDCCF1C92DD432D3268
	5C03FDF2CE8E51D18D3A0F82F41D42BC1D32F633A6CF154E586016A4D6FD20451CA2F51B3F39C0632F6794DB3B2BED6ADCE7A4F1965F9D96E1DAA009FED29C4131FD5D26A0DA27D59AD6670C51F56B61AAE97A88433B576B57FBF8BD6997C3DC7F487ACC010C7DCCA7EB2785D6225363F5B8E1C8A11DDEF906F8FB0B7699A2996AC11EB7F25AE03EEC43F570F3ADC4D63BDABF58C6E43F43576AE48EE538840AFB98BACFF9862EA7CE99354F96B6DF35EE1B9398EDE3B10CEA8BDEE7BD1DCB74AEGFCEC9B36556F
	57EB4714FA1F4F5D67205325922F87F3347940F08F6710FC68F7F8FD30663209FE0C7967F3082FE4BED550CA5CFEDD34DD88DD9FB4599C325B4B97EFFDA4B1D8DD2FB7EB1FC91E485B1636BECCDD17A9F5DC2529EBD7CCF29A337B7BCA52AE7459E350FA596A2F71DD016057551D50BCD3FC1B0E1904FF7FE9FC2678FD994991894A91GF3CEE33FBB37D75B8631892630ED3070B473E7716960B6684B234F4400CFB90DEDD0113BD15386700CG4B20817ABC270CED3969ACB71572CFF2B3B0B7856C0B0F98C9DF
	4AE90767920023GDDG7EF634E15DB06E7A3949BF467AF9C948DAB43ADD9D7B6737074E83EC4DB9D0CEE2995B4E561C999C6F2578E59ABCDB0B7E19461E141301E6FE8B57D75F5DC8F3974A31EFE11D9E5F102B09DB3B5B6112F94C6CF9247F3A3D0E81070B0FA79487CFE0F4F0E638AF99434F9BDFB56A4F9B31C67DF963BF0CE32F893EEF8CB72F30797B97B49717C27D12C8BDA9AEF6AB41E51F0131648C4E594EF202EB04F28F4779A9EE8A1467F25C1A321B19F7D16FF8FC4CC6C9794D58C8D75AF8BCBE96
	379FECA4B96267659F52720822A7DFF8E18BE9D749BA629E0684585FC162363194476445D0178400EC4017F63035919B9F9E3DC24615FEF8E800E6687008FD0251C2E4FF3C95E5C701BF8AE08140D20015G4C66DB6BC95E84EDD3A87EBC3307343372DCA2929EC645BD27FFAD8F1EAF08CFDDE0DB82E0BC07F5F9A2009E8D407A4A6B25386732GF049962C571E9E725C46473D9A6B95392E6E3857E1BBE69DB053A831316F1652ECC55EF496473EAFD01DDDD076GC41E45314F3F934764791F7D14E01ED2273B
	CF14E0591696F31665CE5CD9F56CDB794AEC83337962D634A9F4A69A8EF692DCD519F69DC0E2B89B7E2901AB233F9DC0A731A5FDD1254F882093FF8F8448FFA300CEE27418A24F6A7654ABD83755CF4D9324AFD7045D831B52496AB4D739FA0A378855F14753D85D1E25FD10740B78CE0C2771F5924F02F30906EA00C6G46ABF35E6EF3487B64A942BB8B4A7381624EE1BFFE33746676AECE5D245C54C94B31B17D162DF46F689C9B53D3B97E8E0ACF5660D97EFE19676FAA20F5F561D8FFDBBE497DA7203C9CE0
	A5C076F91857G9A4F63585F3529D698E7BE3338BDE69858A54DD04C6A17D6F6F3FBBA5F4842BCD978558705C5D313D3432DCB3A4E337C70AB576C22AD944F6AD7D2C86AB78FE8F1G49G4285F85683283E00750B59D44D6AC716C97532EC6D33539598D62E53A1DED151E470E2F57BD53936EEC7C11F6502BA165BAF041B2713E91E0EDEE0ED34156357D0FC2C864F5A2E0862C959F0915A68A2666AC8007D78EE20F52BB68DEF0733FF260D68ABF91B59594871FD945F2841B37B0BE9DF2173AD50D63F0D76EF
	A7AEA9F99C2539FF3C747D6ED10C5FEF16FD20B6D8DECC4F8F3FDE39B7DFFF3DB2BBDFFF3D72C07E584F391F4B8F7F1C3BA0DF7B1C4B727A1DA2B24EC63E83EB900045G71GAB5F411CBED220BC876864F393BC78B638698E200C763795062FEBC561787B06FB8A49F3CA2FDB165DCE229F71FFAFA46DA53BBD9449F0078BC90CE039E817992B02BFD6C846E9C96C27E836A67BAB9D3BF3AE411A6C922E53DE5B05EB37C60EDBD005EBB77BA54C4F67DEA74F900B203C8CE095C0E98F2C5781FAGA2G6683AC81
	D881E030423887E086102C28A3033EE70A0072DC00B800A4GC3EF22E187C0A3008DA082E0AEC09CC09200218FF475210E52579360CA6CCEA687FBB47C449FD20F094811BA113AE2FDD6A8FAC85DC95D48BD094792A5867F58FEF90E56F57DA4E5F3B577E4A099EF9F8AE123E237D869970502BA07ECD16EB3E8EE89C25EBF281E84D84EC5715CEC6943BCDC378DF3D36238086D181BD1BCA73A5ACCF0CD0032E003DC82E881688198850886D8E4C3DE87650D0772EA00D2B151F098209BE094E08E4093A272FE7F
	54943892FD932D0B2058564D41B174905F97A974E88D5FA8777BF96ACB56A4621A21341D9EED2C1CCAD7F2E1C3D0BB678A7C3DC6D178714DD9B47E78F639086E4D4038E49971088CF72D08BE7776777BC4BE0ECEA9B2F2DC2B2F5D6DF3108D9E762CD2E8A27C3DEEAFFDD0E54FCE3BAB7059A39E5AF095C0EDBF0CABG64791271DC5A05BC5D7D6C79CA06EB2442332C1F6E2DD4B50E919FDC8B26AB9083B28B818A157262FEF53D48FCDDFEBE70194BEC6D654FDC078EEF8A2041323057FD4C6DC759DDC7C8DFEC
	941DD6172D51BA62EB770A179D6EE11F85C2CC59943EEFDFAD5665535ABAB7C87A2E321757C478E4D1CB5F351768E80229F1E0A41C0E1557B66B72847B7370F0817727C306F9189BE374975D85DC0F9EFD6EBE3AFE82A4C6046187738A55BA9C773A07BDA4071807E8FB4126929D1EC0BD39DDA49614E8F678640EBE2FDB1248632E22E36F8152F7BAC4D8C2D8E5B7D9AB303D5F06BA6DB3D969G4B1B23F0E574FEC1CB37A874D1CE3FD42525C7A974C51C5E3DCBCF7F92CE7FCD0B9EBD015307CE6A51D7F1FA2E
	D94FFE369D69BFAD5313AF6574EF666B51EBB93DAEE0EF3C514E68911C3E70121EFCB4273B877562B75F0E7B8BEDF95A7D05D8C57F8A388A8E54315A2155D162D05A4801E340D7789E05454146890970B8589E946359378DF9229DEC1CD8G57B805070D9333370FB7CE24EBA09EE4D7BA54F142B098BCCEA45AC9CE1E9E16BD43B29991C81FEAF60812CD213F3931A800FE4C6B3E62930F8D43BA51A5707EC97C566E61D85C8383124245F81E1556513ABBC52F52F1987E07E9C6855FA4D9BDBE5126690FB56B72
	82ECEB47047F5BC77CEEA5EBE2188C204FB558453EA1B29AB07A2D3D4427B32253FDD9BCB5AC498E0F64903D48445A6C0136603CA4F1B68EE21BDE8D484B9D030CEE6674748EAD3DC721CF6374674F86679D2147F07A09A2BD7AC2CE1F9652AF88FDB1273F92C00FD76C9B06387F45FA7412A15C934B2D44338C5584B704FBE27F3E0E6C97681DC36A239B88B583430E1AE159A15555D99CB234B31D9159DE4D59CA5C331AB304FBB537D2BB4B203C8240A0217D5BF7611EC6A960AC926E777CE0976A18A6210E97
	A9EF9C146381D681148BECDDC8F8595F4AB775F0B9CEF2AE0EBD5F323D09449C5C1BA8FDE436507863645417857D3D09F6A7DB9745B8557D397D0FBD14213CB3698F734E04A6AA2B4B55BA5C3BDA6E64673C24AD7AF2FCA2E6321F9670FD0B5105322FD4146A4B720E4BE4BFD105F6DBDC9857FBF6A1EE8A47EDEF4DD6623208639EBC09FCABB92E510C3ED4389137096F4B75F05C8B79080BF6234F371E58B351FC52CE3E821E51FBF7B33E77DB4A6D7C66927A1D6DC15CE787116F2807473AE92BFE3CB433AF5A
	191A073E7BBD18F70F8D103CDBG6545G89DC6F5DF218F66053B753390F7B1462413CCFAC47FED77BAEBC6F3E0B7ED71AB06FE35EC57B67CC04F7B914D782E4FBD14745ED58F7CC006B72220E4FECC39DD1DE5471B465DD8C658400942FDE5F9184BD9C6BBBAF1489BCC64B5B30BEC69F46A22FE333FEACF01AD0E49FAD439CEA61321FFD27BCCCBEEB2690C53E2E03672F8F6BDB51C9F0E41ED844F1375035BEF9F6A81591774DB2AEAB236DA31D617AA14EA5CC361B4BAE627A9EAD66FDC44667257C4A7176CA
	AB06831F47185C36F1654A024ACC6E4238F2414076897FC447634661603DE7010E316D4341FB4F9F2F44DC6B86DA6CB056772EDDB737E7BE7BA66516CDDA0E556B5DAA627F0E4B41753DCA713517036B1BDE0575BD8C34184B78DE678797356F340D1FB22621DC30DD4395564E46AB416F348F1A487A2D9170BDG91GF3G96GAC860887D889E070437A97E0BFC08BC0B70087E08A40AC00F9FE5CA7FCE6EBA5DCBB2D0321A785G293C48221B05DEABFB674BE279DE1DB616CF78191FCB15EB12BFF45F0049E5
	279139949DBA011C11F0F2332B081CC907CEE0BF60DBC6747AC6F4C01BF7ABBC9E659A91565E2B2DC4AEDA214FD72E314AF5D9D83907CFEA6DA5ABFC2B152B71EAB839D6B3115B21C3A7F0B02CDCF140BBG33425723DC6530F2EB7235F2118A5F9C653AA02C5C654DE375213830F2ABAFE96516ABFCAB142BF00DDD8B2E2972CC6E1141316CD5DC8BE76F4EBCA2F798686DBABCB69D9C6B53D5F652F77D57024F739E207848EB4167F97FFDG6762E8202D3E06E35B41B17D6E998D67F760BBA55DBA6B4E81FF98
	D7CEFF8E28B15D5C187894DDD7482341E362B20A1FB29ABCA63EED42B1F19A50D60EE21C2C5BEEEEACF75F245C3CC94B31FAB5EDA77EDB2E8757370D62FB2E87577747FCFDA581ED69F52C6F4F5A46323F7FC6383C78B75D7728AD8AFFF7D839B7BB7464BC8AFF84DC23EF6865535FBA755E2F4DD7F8E3613ACC47A60164A07CEAA89B5FCBB46CF8AF38EFCFAD5233F1D043E7FECF5FC6CF90DE06F2A4401C909B0B836236C02147A95765BA7A153C8C10DB217089D314F1EACA58F6AA564AD5A8FC0715EB3B0E9C
	FB5F911BCF561B8E1764F041838CBDE94FF53EC826E865BEF317FABF237203FF4FBA6F1FE063CA15DA27A91A7B35D59F3C6D6D9F288D36E63B1FFCCA273C9FE8ADFFC588335E2A6F15CF40E344695EC131CF36D887380C59947E5DC507E97CF7975DA67DF7333D2650F73383A67DF733CE13765DAC43F9CD64B9E8G5608DD1C6F8A471D6738D12A13604EB9EC321D63BFC67DA278961198C4FCA4756B8A45F5A9383BB438738AEEA6FD1F82D5363B7999437BCC64ECF13FC3121477711447617209B20FC78C7551
	6131EA7CFE0863085F6C3D5DA7685E0B8D7A2CBC855F655DC177C5A2219C4DF13F636F1E67F2DCF99A412D02F24EAD081B1A06FCA59C7745AA6433F3DCFEB572499C77432B84978765659C7705DD68DF92476DAAC67FD2B86E4EE254E77898620E50FD9B8B14A50EFB28957579B9AE67886A1B46F14EA328AF1A633EC677FC12211C42F1D1EF507723374233482D087B439BA8DB41F1BDE674454FF15FB0A3DF844755507D22E5D0CE62381D9E02CB01F2F6842F2F87E58BB82E28GE5ED9C57FF1D607CD00E6438
	272E236CAC0E3BEBB36212B8AE2B0760765F16E8E83C0D7BC277AE3B20EC6738BBABF9DBF25CBA3A27938765A40EFBF29079D2B86E41BC4C0D162908CBB752D8C1594FF15DC6141DB6955799FB6C249F447372A1DADE454B5F9CF8BF639E51BB7E589A7F3EEC0CF78600557DB40FDB416DD83727CD827761A71A7BBE5387BF1F0C7E93EC1C5C2E564922397F59368F5E769FBFD09B2CCDD735E11B5AEE479C7FDE990EC38BEF8F9C8F88DFAC47F552BDBEE3E4224194093847BB102F1A63BE7FB2410DC2F996471D
	26FB0173203C086396D2DC9C1417F35C0B1DA83B0263FCBB693E6747617E63FC0CF0215D9D9C77A9FA46C906F2A447A5D15C9CA8BF41F173689E64D2A8A7F0DCEEB1374BF18394A75C8177F7F0DCBA374BF1D9B4FEB214A3B96E2D32D03BBFAF43B9E08E67FBA483E33F0063F2B25146939C672732B9F742F8FFA7623EC57703EB21FC1463F625222C19633EC76BB1874A73B9EEA99D0BE3213C14638E5CC05978BB71FC78676B681C6FF64976E9F42F164E5B55F0FFF09A72BCD0C53F4D0305925B137EAB6DB76C
	1DE8E675E44ECE4C871D8436A87858E9416FC4DF235F5048EE37E4F1F848FB95E59F5C37C33579693431EC26AB7AB2147BA50A4D1795DC0246A6533D6295D5772EB16B132E51CB3FB1576826E7456FCAB45C97D01F7F3DCA56D0CE2B17FE5F416C653DCAD8D93C1753F5C88E4815GB07A5E8A551F870B470B2F561F6078365C959C5F332D7A719D2CD4FD7ABEDB5F00EC8F77E9D3156AD34411493C878F76A9AA4427E7CE866660EF68FAE88E704D3B8BF37061B782F310EE95AA7E640C6BCF56987EAC3FABB89F
	DCFFD3F5AFB70F178FDA5DC9F78566C3CAC8BEB415875675A29D078D5361FEBA56758DCFE0DD6F2E516403E7ACFFB20272616874405C37CCD76B4A6C3FC957F6BD0017387D6B57836DAF5C2D5AFF767AF87635711893E4FF0146BE53B7F7B36AFBE2FA683A8875E01D1682CF52F4F6260260D2268767D2D9CFE0FC77533526E1865C4F60712D8C2C5F33351A78D622BFC7E7047AC36C2C5778E5819E8218CB7699417EEC23E73060A14BF195FC32CA7512E340C5CE94AB71BFB4086FD26599F84E643F865107989D
	9F52959FB0A68B959FB0A60BE78467F346DE017B7312111C793AACFAE5C71FD5E2AFE20FD93D6DEE4951B72278F4002E094900300C7B64B322CF85D17AF1495534FD85709C0ED24F3E34C785472545CE6A4C4FE389F85ECA32EB4FCDF4C7317340D322500FFE9E1BD9E17CD82B714392941BF9D14179377E74C446B29EEB10DF9C256A0B0F8A0E3534CF4D252672717A0AF61CDC2C684631A3B9A4E64E0E403C1D31DF35751F9D13992796EB6AC13FEB89319577B27DF6566D9587200DDCB69CAF62695ACF389B66
	053B31BF75F7867A754583EA3EBD55C96CDEF6789C645BE6266359CEFC0FDD8DBA98EE4FB902BB8C65962E770E1D01FDB9062E936DC0176F46FCA8F3CDE6BC8A2E775C3B036B7DA8BDF3AF3A48A610CD71F7F1A13E57DF40FD7BCB61587D3A3088E562384C570B8263744CEBA4CE0C7F35E22C7FAA4EFF23B8307E33A8CF41CCD81F4F447A5FC83F197A97AB3E354F8C99BF4BC26BFF208C6B52BD935BAB1B4EDB9EA80F4E44B5C24A5F55FCFCB9937D1ABFD33F1FEA57893140C3GF3FFD908DFB34A82E3719075
	A98178D64C44F8458B0171B555297E542722BF2577687B13B106BF3577847B139454B6AF3076833E16FB501FDF854D238D07D4FF4CB7509FFF98FF324670E75ABD4163CE6B6BEA1F3BD1C59668AE710A89BF2B2CE986C7AB365B9CD649BD303D5AD4DBED5A3337924FE445DC513EDBBF38EEE7586F452395DF67AB577816ADE31CDD799A0DFF78F3E109077469A8EFF8C33D6FF9D32DDFD971C46B47E4EFBD2A6A69EED07588C7A627673DC60DBF47D5BD91EFCC521F16316B7DE82BFA1FEBD66D3C6419281D70B1
	2FBD256A1E53216A3EF07D7D6B7E3A051458991DE7B663F92324FBA6FADE907DFA762CFA7F1DAECD7DABA76AA37361071568C369AC7DF3D74A735FF9554619C193DCB5C712EABA483F7849172F66CC6B71968B533FEE8875DBF475E3DC72AE2A7A4D467DC17AB52761EABA1B4ED8DA9B6A4D17BA9A4E1CB61B0F551F39D45FF1292D2929312911591CEBC41B5342588C6F475EEEC140F16E29CAB287681D1FA42BB165BC1B9D676938D9F8766A3EC1343FFA960E7BE75FA6FAA757467EF7547B5917547B2F752877
	BBFA55387D181E67656D5E60165C5E1ADE495AB74468FFE6EB906F3048E46F68D3E507F6EE8A157542742658F9DED4F97F621CDCDFBEB8204A7E3C1044255FAA79C40E2BAE5AB6F6BEB5B435D91A4EF07ECB45E37BBA4B215A3B18BEB9DF9F9B5464E11956D7C6AFF32A74FF4918DCDF33E4E2DE6C3FF72C3ED66FD26F691422587B0AB0B9FB5ED434A75D0BF978FC1E2A3BEB132ABBEE42E39B53FD7D866A1E4BF5DFA355D15EC9D650E766F820255C0BE741263FCEB6C8421D85EB773ABD7048B54230749700F0
	0FBD60CCD049A7928D551F18487B016581678F0F020CE5C2F2F9C3DAB9BB480CCEC86EF3C62DDC94484C1D105CBC7BD8E7B6966968E0F2F3824EGAD813E048959BB1F2915DB85B25977CDC46ECA2AD62E94E4EAA7A477319BDA39C610699AC70E654B5F8A48BD7F26192504C25BDFC046BA72BBDB101F0E2B74E70EBA0051B54076F11F27F32BF49FDB83B25C9FA9AEA2G773B4AD03ED51521FCBB07C2792A06C279E20D21FC5FB20672FD5D0E7B4CB177615E731786C3E5978D064AFAB2C37906B3C379262706
	725D139A4A77499B21FC775F50723179503AB3C70DB9FD554E70257EDC950F2F5A155FDF79119D3F990A83DD4B81D2GCA22938D8723F13EDCED473124AB9AFFC7E4FF4077CAE40CFADE507E8EC8FFG1DFC1B199AC00F8E2013B5FCFD2A167E5DGFA06C61E65EC2AA320AF03DFF323474ED996379287461258E3382D8E0CA529837B6D64BBFBF700507C5F8A99BE2F1047475CD6FA10A061723E3A74DFFCA6BDCDB0348A196CE0C52BE0A027A3DA058CFAF822D5C8175D9E3FC7D0B7B4A9785E95EE88C2B0D696
	F24887CAA676BDE51DACEC699049AF3819148F82CD9AEA0DAC6CED9BF6760ADE133B5FC43EE7B461870DA60B5B54A1DA3DFDF6937B0A51F4C634120F1BFCB55CEE401FAC94AA0C81FA4081A0EDC463CD14AE0B00AEE4DFBF0630CF40A3C512FBA48B6B1C4E5DB65B6E91788BAA6423E93A39E6A21FC20211170302227E4C9D632B70D50256741A9A78B797BFA7232113050CDA138FF8059C722918C979D6CC960A5857E1AC3E0D8EAF30383DA3E0EB837BB04CA43B95859B5937E0A675E3B0C80C5C004978DB8F9C
	7865127F6E74258B192D42E672DC5DA1FA6970A7126FD67137CD1B05359E727BD9642787A1EBFC74126113C7A451AC94741FA13F336713C5DBBD8E2C3018ADB493CB3B49C7AE3BDB2CBE3BCC76B68E1985030B7C8A2B300911A52BEBE0B79BE58F5154CB2DBFCA5B190C8B82781BCF7DA5DAEA38167C4773DE59F6A2E9D13A1056AC2C135CD6DB3315C421D5D8AB5B3D224F6E16ECFE4F1BCCD1769552D56876B1285B422BCFAADBC3DE45F027077C1F1E161B1C5B1C866BE42F301BF212ED049A4EE1EB30DB3DE0
	CA74364AA27D64E587275DD2A66F7C5E71626EB4A1D529A4495F4DFE0FACE476DB1D8E8912A43311AEFDE8E6EF1541DB2F55656B07BE821E1902BCC392B7D0F34FF7EA6F7D5C8B972881D5D4964AD9A8C1F4770068F66E2E6F6FF738C42EG628A6A37F2750427A608E7137F6E42934F2C6905BCE894521D3535745FBD745F3D645FBD943307E2766C05602E8C6D1A5A3FE03A9F6BCC13078FD02479EB1A2185651F7C76BB8F7E74704A95D03BE6A1037EFA9DB402D165E25DF56A7CFB372E1F15F0A7E9A38FC995
	BF513B4D2FBFC8CD9ADB9C1201843BA5B415883A1CB7A8A9ED272CBB683F2FCEBABCC42A124AD671DA6D9B2BD69FAACF6BBEB21E56CFBED3BED47E37FEE4A25D7E910FF457BFEACCA1FF86AD84ACEC10D7189456ABEC885C2CC8FD2878D6630CFD43E9C2F6B319EFA40F5D5A2A0C23E6E1FDAFDD6109DE60158523BADFD0D91814537AE0465E22627B881ED1BBBC56BEB2EF2F35D93DC3D483F149F6D1D85B6F9D682586E4A16DE273C5B0CBCB045CEF965649F67213CDE4821505E2D5F5BB79A8CBA130A9DFA07F
	2107077E516A52BD24B5EB82F09F2A54107C3A4D1273617354276F29706173746AC74653DBBEB21EC60671143DFF2AFCCF7B7EA965362C306F1FE6BD4C1ED367BD9C7CED2719BEE7A781DE1807FBF2F38648C11EF05F8BF4B82D12A4DB2521FA170DFCBBF0ECC496555F14FB4BCE747B10C7A1AA3F69C84FAE8CEAE47E9FD0CB87888110203FDCA0GG10E8GGD0CB818294G94G88G88GB7E98FAD8110203FDCA0GG10E8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1
	D0CB8586GGGG81G81GBAGGG16A0GGGG
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
			constraintsOutputFileTextField.insets = new java.awt.Insets(0, 5, 0, 5);
			getGenerateFilePanel().add(getOutputFileTextField(), constraintsOutputFileTextField);

			java.awt.GridBagConstraints constraintsOutputFileBrowseButton = new java.awt.GridBagConstraints();
			constraintsOutputFileBrowseButton.gridx = 2; constraintsOutputFileBrowseButton.gridy = 1;
			constraintsOutputFileBrowseButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOutputFileBrowseButton.insets = new java.awt.Insets(0, 5, 0, 5);
			getGenerateFilePanel().add(getOutputFileBrowseButton(), constraintsOutputFileBrowseButton);

			java.awt.GridBagConstraints constraintsGenerateFileToggleButton = new java.awt.GridBagConstraints();
			constraintsGenerateFileToggleButton.gridx = 1; constraintsGenerateFileToggleButton.gridy = 3;
			constraintsGenerateFileToggleButton.insets = new java.awt.Insets(0, 5, 0, 5);
			getGenerateFilePanel().add(getGenerateFileToggleButton(), constraintsGenerateFileToggleButton);

			java.awt.GridBagConstraints constraintsTimerLabel = new java.awt.GridBagConstraints();
			constraintsTimerLabel.gridx = 2; constraintsTimerLabel.gridy = 3;
			constraintsTimerLabel.insets = new java.awt.Insets(12, 5, 0, 5);
			getGenerateFilePanel().add(getTimerLabel(), constraintsTimerLabel);

			java.awt.GridBagConstraints constraintsTimeElapsedLabel = new java.awt.GridBagConstraints();
			constraintsTimeElapsedLabel.gridx = 2; constraintsTimeElapsedLabel.gridy = 3;
			constraintsTimeElapsedLabel.anchor = java.awt.GridBagConstraints.NORTH;
			constraintsTimeElapsedLabel.insets = new java.awt.Insets(0, 5, 0, 5);
			getGenerateFilePanel().add(getTimeElapsedLabel(), constraintsTimeElapsedLabel);

			java.awt.GridBagConstraints constraintsOutputFileLabel = new java.awt.GridBagConstraints();
			constraintsOutputFileLabel.gridx = 0; constraintsOutputFileLabel.gridy = 0;
			constraintsOutputFileLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOutputFileLabel.insets = new java.awt.Insets(0, 5, 0, 5);
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
			ivjGroupList.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			java.util.Vector listData = billingFile.getAllBillGroupsVector();
			ivjGroupList.setListData(listData);

			//Select one/multiple billing groups.
			java.util.Vector tempCollGrp = billingFile.getBillingDefaults().getBillGroup();
			if( tempCollGrp != null)
			{
				for (int i = 0; i < tempCollGrp.size(); i++)
				{
					for(int j = 0; j < listData.size(); j++)
					{
						if( ((String)tempCollGrp.get(i)).equalsIgnoreCase((String)listData.get(j)))
						{
							ivjGroupList.addSelectionInterval(j,j);
						}
					}
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
			inputFileText = ClientSession.getInstance().getRolePropertyValue(
					BillingRole.INPUT_FILE );
		}
		catch( Exception e)
		{
			inputFileText = "C:\\yukon\\client\\config\\input.txt";
			com.cannontech.clientutils.CTILogger.info("[" + new java.util.Date() + "]  Billing File Input Path was NOT found in config.properties, defaulted to " + inputFileText);
			com.cannontech.clientutils.CTILogger.info("[" + new java.util.Date() + "]  Add row named 'billing_input_file' to config.properties with the proper billing file location.");
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
			ivjRemoveMultiplierCheckBox.setSelected(getBillingDefaults().isRemoveMultiplier());
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
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
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
		billingFile.setAllBillGroupsVector(getBillingFile().retrieveAllBillGroupsVector());
		
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
 public static void main(java.lang.String[] args)
 {
	try
	{
		System.setProperty("cti.app.name", "Billing");		
		javax.swing.UIManager.setLookAndFeel( javax.swing.UIManager.getSystemLookAndFeelClassName());	
		final javax.swing.JFrame frame = new javax.swing.JFrame();
		
		ClientSession session = ClientSession.getInstance(); 
		if(!session.establishSession(frame)){
			System.exit(-1);			
		}
	  	
		if(session == null) 		
			System.exit(-1);
				
		if(!session.checkRole(BillingRole.ROLEID)) 
		{
		  JOptionPane.showMessageDialog(null, "User: '" + session.getUser().getUsername() + "' is not authorized to use this application, exiting.", "Access Denied", JOptionPane.WARNING_MESSAGE);
		  System.exit(-1);				
		}
				
		//Create a menuBar for running standalone.
		javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
		javax.swing.JMenu fileMenu = new javax.swing.JMenu("File");
		fileMenu.setMnemonic('f');
		javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem("Exit", 'e');
		exitMenuItem.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent e){
				System.exit(0);
			}
		});
		fileMenu.add(exitMenuItem);
		menuBar.add(fileMenu);
		javax.swing.JMenu helpMenu = new javax.swing.JMenu("Help");
		helpMenu.setMnemonic('h');
		javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem("About", 'a');
		aboutMenuItem.addActionListener(new java.awt.event.ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent e){
				javax.swing.JFrame popupFrame = new javax.swing.JFrame();
				popupFrame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("ctismall.gif"));
				javax.swing.JOptionPane.showMessageDialog(popupFrame,
				"This is version " + com.cannontech.common.version.VersionTools.getYUKON_VERSION() + "\nCopyright (C) 1999-2003 Cannon Technologies.",
				"About Yukon Export Client",javax.swing.JOptionPane.INFORMATION_MESSAGE);
			}
		});
		helpMenu.add(aboutMenuItem);
		menuBar.add(helpMenu);
		frame.setJMenuBar(menuBar);
		//End menuBar setup
		
		BillingFileFormatPanel aBillingFileFormatPanel = new BillingFileFormatPanel();
		frame.addWindowListener(new java.awt.event.WindowAdapter()
		{
			public void windowClosing(java.awt.event.WindowEvent e)
			{
				System.exit(0);
			};
		});

		frame.setContentPane(aBillingFileFormatPanel);
		frame.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("ctismall.gif"));		
		frame.setSize(aBillingFileFormatPanel.getSize());
		java.awt.Insets insets = frame.getInsets();
		java.awt.Dimension d = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation((int)(d.width * .2),(int)(d.height * .1));
		
		frame.show();
	} 
	catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/23/2002 10:14:46 AM)
 * @return com.cannontech.billing.mainprograms.BillingFileDefaults
 */
public BillingFileDefaults retrieveBillingDefaultsFromGui()
{

	java.util.GregorianCalendar  tempCal = new java.util.GregorianCalendar();
	tempCal.setTime(getDateComboBox().getSelectedDate());
	tempCal.add(java.util.Calendar.DATE,1);
	java.util.Date newEndDate = tempCal.getTime();	
//	java.util.Date newEndDate = null;
//	Date tempDate = getDateComboBox().getSelectedDate();
//	newEndDate = tempDate;

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
	newEndDate,
	getIsAppendingCheckBox().isSelected());
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
		com.cannontech.clientutils.CTILogger.info("...Ended format at: " + new java.util.Date() );

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

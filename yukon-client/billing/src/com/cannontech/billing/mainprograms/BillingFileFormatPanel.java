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
	D0CB838494G88G88GB3D06BACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155FC8BF8D45535F0142031460AD5CAC0AAD19E22A2C50DAD8A580874961576C7453F34257E71CADBFA1B36393DF81B2B549E7240700E90A4C810898910E09C9288A5FF6415F782C1C2B2042050B0491CA4831319E16604A4A448DDEB6F3376B9B3B913902C7DEC3EEF651C3D5EEB6F359FE76FF3C6CA599A1E38B4AEC51216A6CA6A5F7E251294ED1324CDD3C73EC338B6E7EC3C247BCB87F8CFDA541CG
	57AE20753E9C9B1FACFD37AC894A21D00E1A959B3F916E57CB91E51575F093400E89E8BF3EE548DF7A3773FEB159F917155F4AD88557B5G43G9701B2AE343FAEB3D5454F875C9A759EE1E91CA4C5B09EEFF62A8EDF6A62FCA1F09D8D906AC61E0FB353D47A924025G6EBC006256F4AEE75E8DADACD04BE76F8A17963F30A3EEB55A5269A718274A462AD713B419D5C43C746B837B60FA2066E26B21E6D73B39596AF43A1C0A5C5CE6EE32BB9CF6E72B395DEAF73ABD2ED60F355DDBD8D84C3165F6070C75F589
	7D9D191D44F4C9D2AE1497B35CFB3B300EA6417DAC0085G5C6717D69FD0FD3A67E23894B5F6D3C01F8BBC5C57339E4D6739F74C0FDB65C57D75192BD43951C01F874063E9ADB8F22371143BBC6DD625466A149DBC2E7BB2A92E9AAF45B5A98B6D06C0B99CE02A1762BA381362EA68792E945BD49537B6C0DCF33DEADBF83538663DBDFF79A616DB8F14E0FE64AB31718DG8E00E1GD3G6682ECE6B9FD2EF897DC0B1A1492D7FB3B4BD964F697BBEC1E63AE1B6C007B42C2202838DA395965013E4473763E6C9C
	83FF8896A93EB92B6559E90972ECD26D84C9D18E47A59A449671EAD65C92D5C7F207965B118E2C3721AC9FF261FE29DA279CFFA743876B703CFDB7B2BC166F81DAEC8755F3439A7225BAE114141CF7AC6EDD83DF42E69E09BBFB197B20DC56FC61F55CDB06F59C8E7869G73G2281D6F5464667F4D29DBFD83CF720BA2E023C72B82D0E3A0EA637552394F7589DB659235675594C7E6A5A88385C05C14B71FA1938D75FC68B1EB8EAD8AF8BA66E0E2B6C6475F13ED32B97EEFBECC05BC11DFDF3E30DE8C3D3B636
	55BD1D7EED3B0B6147F57A376D63D9543691C0CB6D0A0D676D72C1850EFB3DD08E831882B09BE091C0C2B770F5D35B7C29F877F566FF0D2EDD386CA3A56F43D56ED2BC56AA27D731BA1B65FA2B47EEEDF248DEC96509A8417AE87211FB0A754BE630D1A1BBE50FD5313B1C205D5AAEC392F8B94F4C12771047A3B32A455EAE0301F637C4FADFA8D98FD707552B1CF25B2C0A2CF1F07A4BA55816172D7520919440FDDBB7565713ECFCAD00FBCBB755D5C5A562A6C1F916281763256B60FA8C12D2F69496165A1B51
	8D2B271BF26B76DDB80F4D6F263E7407864A19525563251CD72B8CF386A1261B37FF52957F1C994EF466DE2139F14B2E06C1662FBF4C6FB64EB52D0F7C6DCB5B086F07466B7749AA4C519A084B8190FA056AB8245444F32F586A15A95BCEF8A0D1E41BD6631CF7E8994E8172E55929144AEE59E9039B2F5A5621E54B35BCECF1099C79964363F8D28209629578303277B89EF468331A68610CEED5940F3D29C3116BE4C501E9CD131F48FC717A8CD5DEB5566F9FE57D94E21C8EB0C7441AD73EDE17CF55AE57458E
	3785FB0A1A17CF3379B18A78E3C57B6F4846FCC86B21F2AEABEB7429D9DF266DD06F40E368CBDF4AC6FF3931C654E7E6C560367D284238ED73AA0C5BF6FFC57FEDF73842286DB82D3C8267E2D727D3178B47AAF09CE8F6B91DB2CBC8A1A271B5529FAC98075DE9D7CE5BED2D32D2E73F820B3933BD5826F358D8138F7715BDB4CEF4E650B811F965FEA932B153F07E8F9BDF92972E36CFC40FB6CEF01F2AABB147E4075C8E95D5626AF0AA9CDF4B46B1956F0DE738DF752C006B5163323323CA115B7328CF7E47B1
	54B18FF4AF8210FAA9CFFFD239C91727EAE7ACF1B99CBCFC20D576E2EC7F36ABC567EFEAAFDF3BEBEDBA2DAA996BD93E54A143340009FECC6E26313DEA1D9F2D5E6A6850660C972AD64255216BA39CBF6328F15C4F9D45FEF1D1687F5151E5920E7DDCA7EF27A9D6546976585BE1C841CE2FBEC33C1429FF069886F16073C684DCE77752BA788F15A82BDF2DC731BAF8C32C55F18E653837986EC168BCDB93E9BDF1BCD97FECE1739EB0ECF31693D7F99D96DC75DFE76DBA0EFA9BG5FF6155A2A6818FECCA972FA
	DD4DF6B6DD2A7595F3CC1F8F9C7766B14C07963B478BEBAE1B5CC5F57E76B1749567D3952D565BEF5F5FA675DD9F14596C0A4BA396EF4D1898BC562B657AE71269EB7362D6860835D70DF134AE5634EF3D13446D7E97FB36E34F9E972C172DDDE6EFA7D4FEE161D15DB345A75B13D4BF97DC25E70AD6369ECA7982561FG25DFD03FBB3DC75F8611D139815B606C975C9F77977EEDB07DD376CC8C7870AF288DEE2AFECF5786700CG4B20D676BC47693B0F47735CD44BFB0FA7D2EECA54977F678476258850B997
	E09100F4ADB65EF40DECBCFDFC4075F3D9D7BF6B67A35B7D5722C72EE9E33F65DA5FF9002FB9FF416A326D9ADFF3A68A7C2F993ED70767EB51F6064772B020AD3EC66B2B5413ACF703A0F703A82614CFD76B6A2D46E5F7AA224E1E7D947B2F4BE3EF35BB8977EF8C87CFE0ECF0663825B501670D249A63F9E3CD0D713C11D2537F1AA02DA6503C42676F53AC973BA03ED00C136172F7A2EE8A14E787511C7DFA966222211CF493612299AE8B4A5582673D21F917748E4447ED045E100DF8350D876263753665A466
	C817A82733F2372AE73D645C0E6D6A30F63BBAA081965C441EEF3753186C06F288409400D9B77135919F9F9E6B44F125859E9A20996A5CF233B475E614BD3613E4B321DCG50G5085908640ED1EDC09F96337CD217A73EA9E36337ADCA2A38F27521E5307597E73857AA48D090DCF8160BCF7579B7164G7D88G6FAB0FEE27BD1786408D9BC2F1651D4767B6B16E192D1DCAE1E10547EEAB3636D633DA62E35F1BAC5FD0FE76909A7BB2F620CEA9981E6FGF203E96C6B5AC1E372248923246A2023067BC4D116
	3C384AE01EBB0EE0ED6CDB74767C655C66251DE4F3A250230291B7E627FE9D00F5F8AF7CB323BEC6FF49070EF50BFDD12347E76969686FBE9F79FDBEF42C236F2B72BC369737D3ECCBDE1CA8B91EAC88388716B51447D42DDE9B7E36D9BA7B627CB89EDBBD1B3731DF848F257AFC6B845667F4A84F830886603CAF5693EF42AD447B1889F98FC059826038057A7193FD465E6B6B1B4F5E205CDB031663E37A1BACF702EF61E37AD201FF0761076970BCFF33F708BDC320C93752D87FCAAD663E8D4AC1GE1GD3G
	6682C45FCAE37F04133952G731945652E06015D219B0AF9FC17F75E581E4E336FD21EC4BC7D00B4751658250156A5D2081A7B6AB5A6FFB3437378DAAC98DF8D509CG43GA682CC871897C2717D61E48E0F8F17C9C50AE2EDEEE3ABB08A2E5EAEF7EAE89C3CF8EC1F7B6CA9AE82FDCBC23431BC7936C073744BB55896193771B65AA2703FE578B69D1E371D4470D81E843430EFD02E4E2D575B8F3E9DE837EBB6A75D9E487E3EF45497F9BB3733D6604B98FE118E4F6DFF1F7595ACA70742FC99CA765F5EA109BC
	CE58295D6F5E71E577A8866EB715DF298D1E978BEA832FD75E28B5DE2F7C3656F83D72475A7E1FF37FDC9B78B9771DDA7DF3AE4F6BC927F01CCD3AA3B6BE93209260AC007B8E4A6950BA75B940A01F4B6041374445F6G95326F32840E7532E560FD43EB96FCCEE9F2A90A2B9D7593FE28855BCBF1399912633EE141BA0265E21B42D9D57CB08B0E538E390521791A4C434679D008AB7C8EDA27A567535AED1640FDE39F2D5D965DC179B93F861FA18A3E89E3BC0082B09AA092A08AA061CE1863812A819CG41G
	938166GC45FC9BA7E48F4A48C03FA86A885308184830C8318853090A061AE2083148258G0281A65EC5BA1655C4E1FF7B66406BBCBDC46978511F026B144718B0D60A87E3C3BC46BEC73D477A4038936E9C1CCF83774BA2966B4AC14A2E565D63B8B750BE9441FB2A5D0DEA3F58A0E9F348E675BE1165969F3F7ED131721C7BCDBD65666C3BA88FE7B6D0EEC68B5C014FA8B7133FC5B971C9BA2E9B4FC33997E0B4C0A440C200A5G29F7935FB146D7G658600AE00B00029G7381E2855F8C936E35235CBFAAC6
	740D6E172877112A3F8BD51F396D854C76BF32FE756DCF3931DCED4B15EA1B2B679527820FDB53CE8DBCEE4DBA4576DCE03C31F43BE50EFB7E94FB1EEDE9714AE2FC7C4929E4012B7256383CF65C38614FA037E5A3FF134B439EC079B35158BCFA26C83D875676G8E00A0GFCEE64BC7FBF1FF8426ED11F1B60BACB65C99E0EBC776FAB6D271EE80D972F4282100D815820169306EB6BC06E6B7CB33E4FD25556A671AC754901F5BEB4D86EB5390ADDDDA43B6B907631D2395D6A3415DA3B3DB59E79325D556135
	C095B3B6156FC7D7B69A7269ED1D3E00FDD25D23ABC3BEC55653F76DC19DE5B06535F687529153334910475F1F87BBD204BFF58A4C2F4298277F4515A2749851476D45758FA229C6B87E425A8DDA8C959ED7079BF308FBC836272CCBB5607155333A816B02912B6DDE252E5963F2B870B1D65531E79F66FE1D8CCB832B6242B5G5F537D78247ED92BE1B84F1BDE38F27A982B1EBECC25CF9574EF3A7574B9AAFD31207F44ED24BF695BC44FC8B32227897AAB9F9A51F3857D0F9F9A59AF95749569C6728D02DE7E
	11915DA16839E6BDBD685B1C9EA96837279A494F937427520D6AEF5137E95F20F51BFE5FE0092ABFEBC4EC7C019124637D2BFA9D1691EA9B0D20B1A079B46998B6020F939361BAFD845FDB62BC3FEBA01EF9A378B8918DD7A90C7370F1226E33014609F89D2402ECCE18B6CE9C89739FA74CABF01CB8512138BB949C91747D6E753675BEB46CEF65F659E1D369075FFB5F07DE6CF1F5FA65628ED89BBAA551F7B1A67D3E0D45555A6AD039B84F3FBBF0CD09A7459E35D3F17CABF149AA3E4CE1F5FBE51B2E2F7EE9
	AF7AD605EBDD188C204F1534494D97F1B4606DF1F31F1C42BA2C8E23767AA05BBFA711FEDE50ABF274F4C5ED0351027E08CF1F18225297887AB23B117D98C1BF365D081EB012681DBEB91DBA12530F0C24FD2A1B76507B859640050E24FD2A99AB70995E685D20E676D0EFEE6D301BBB943B233050E2D7209D58EC4677CF1E5FC367A633C6527E49B9B6A6AE01F24AA84825D1E45F1ACF7B8C25006B9DC5FBB08F6553DED444A85271D3469B8D6504FBE15CG58AA71359A72723F1843C6387CFB71DD35574EE209
	6F973C3E0A768B529E39D7AA7DE96C528C49F83F206BDE3E1619FD2F36E796F474B7096AB9C6CB00F38C16E3BC965BC95ACF12C60BF72F9C1B0D6544A44AE57F2F154EC08A046C762CB4E3D95131386C8F5DE457BD1A6AB5719261A68A5CE7E9C9EA3DC4895C0A2D44177A9D426D58CA3E5488DCD1BA7105885C550F8897799D72F948513C6B4DA77D44A951FBF32DE64A29D8E1E7E6AA791DFB9F61E252092F6DBED157E5DB0C6BCBB7F3121DB15B4877707BA86FDE3A02F9B79F4AB1GC9E3C86FDDCA00F690D3
	AF57F97CAA79E49AC3F9DFF01A7ADD8360060DA17F03CE936F6CB1E4FFED835B778D07F5BEC0EEB869D8FF067ACEA5600242C94767E7286FCC8DA79D37B15E98A8A75D9F9BEF3A5F286FC812910E771D4A3FCB220E1E92EDDEF0BF59BCA94ECA5D77D37D2C2D5BE4DCBFB4342B7A3E48213C1AA8E4A77CFDEB009C578D7B2A7C3F2711BFD1421F03F644611C147AG61EEE6FB6A78FC507BG59B9D49F289F52BC40E5A71B050D8768B96656BD836DD5260E75FD3ED25F839AD06EC33FB2177B4900F2FA48875BFC
	1F6EA5B676D60E755F7B7DB943DB467A6F7DB66F253CBA8F34D9E3A95E567C9B5B336E38C13949031663F19D5B077EE70D730F57427079637C637D2518030EGED5CB8BAD779BF1F6B4F1413C71962C84E5FEE52F85E4EA9637D4F14FB9AF11DD589F89BC088C0B8C084409C00A80045GC99320CF8294GD4831C87D0G028142GA6CD20FD3A0CF359F02D37DE68FBD28FC875A009ED56F92C7C4C1557257D243EAEA3A7F0BF672957C5937A6E83F039DFD8D1EE09819DA17941C0F2AF38D1AE53000ED0G786A
	870D7A460ACBFABF4F2BBC0AFA8D89E82FB18D65C62B74A96AF516FA1D9FD0EE6DD63D3DE815CF1A28366744C0F29FB139AC83BA420100F26569FAFBB5AA1FCD3DF685143B79A33DDC284A972EDEA3824AB94C7D7521598165FE142A17DB2072452857040778B575A1CD1E4B3D195E1F3D1C0782599B378D650EG5DE24053E600D34FA0FA583B8A8F793FCFFB0261C39F72FF1F7627D7E85E9D8DB469E19A5BEA2F7667376D61C0FE9FBAFDE3F3C07500F246F340508165745075B04D81C3589AA274E17FB1B114
	61439E769F93B7B45018B88EE8C90FD0BDEDBAF3E3E3794E9B14BBBFE8B99E57DF58FA477108FF3C4BBEC3FC57A37E715EA956D241C0DBA462FD46A71F2BA7296F032BD7E5D2203C286F777B011000F2C3F20C64C62B7CD3824A7DA64DC8EE164ABF9F2E51130C723058EEF4AEA6BD2A0EFFF04DFAB4409867072F01328D206BD17FB161566DC6B6C2F5FC6181ECC47861E7C3F981C0CC9F9BCF780CF389135571C93D66CC6E2B1F1F9BC42E4775205D6930BB6121033D31AE51FE7D2F58F36C911035CC26FC78A9
	3E2A255A0C525D5B77FE75E718B77F93EC1C5C274574196E7E0D7C2F5E76122F5486DF0F3DFE9EE3D24FBC7F3EDD8A3016AAEAD20E42634009268BF233E231368A19BB9B831F9FFC3BF1607303B10D46671E639B7B1EFBBE5CE8FC6EB925D1FF6E49F1CFB66273CCAB2C7F9A845F5382F7C660FE40F4A26E345D2634897C8F19DF082F1451A061DFE4FEF5B2DC030A1B2B431DD1F12FB6621986045C66926F6F7D3C915F5BED31BB9C3C7C9A6331BB3D32A26A638D66235DED55793DC86050EF5E9703589E4AF968
	076E49F4CE164B4EE20221FC0F408DBFC5E7E7618277A73607928365246F926ECC937125885C6BEE623389DC71A562F38BDCE88B62A221BCCF6052AF11FFD182F7F7BE7937D860966713BEE98A61AE3367579AAC8B5CAF53C81FA2F03B8F113E90019BF608745DA3F0FC1FE6A114978BDC5BE144A5BD969B1F7A9861FEFC18E4B3852EF8AB7922881CFD9B7185895CB93687B0974AD18277FAAE6296C3B961F14215670933CC019B7991491E9738710508D3209CACF0ABBEA159E182B744CC38A801FB0D6D8365BE
	81EB1DA78897F2GF196A85B846E7787C4DB8A1C0545918965A801DBA562DDACF05336D1EE14C690AEAD0355951495016B48A0591088F1A65B433EA59265D32C9CA34A3F3C72E546BD54BB70587A5FDF4A46C0676BEA1EDC95ED7DA455476853B4071EFF12623D8DDDD17D7A166EFEF343D7BF47647F93EC44FF264534CAF7BF7C4CD7EFFB72D7EA0337692B69B4B6B9C4BB7F2A184626A0011BD3C97990AAF06D5904DBA0F023F2C8B667FB04FBBB07788A846E9DB6A606C0B9CC601A37905FB8018BD258B88465
	D55FA71C0D6D871A201CABF04FB35C81A857885C3A8BA4EB93381B4E31F7B4209CA2F09FE4125D5182F7AF43C5C3D91AAA6A0561D2201CAFF08F31FD48CAA8B7885C1A9D422E4035B1DC9714C3846E717D422E407D69E3E6974A52D3629D92F3DF3BAFF09C70E48A3E3B193E82A8D78ADC2C30E1F90A5E29DE328767E4850FFAA45A93AEEA425C667491A759DC6FF2AAD1CF210EC759FCBD8E6E27889D952710C7018592177FDEB34A737348C92786735EC26653904353FCCF8C7185CF7B1FC7AEEF4277BE94174B
	E1313B71DCC47579E7B64D263B29BF1B712A3EC4753ED4351921629AF4B6396E5A96CD7719CB8369A63D88B61DEE76BEBD5C0F7009E7EC8B2E1D5A2D9E764D843757014BD2095777D1367E888639E1G1C3E472E7933F24077C2747E7857EFE41F7A3DA34D38FECFDD50FCDA467A669C101DAFFC7A66C54D27878F8D668C5A5F2724E97EBE9D5C6A1B03F5ECAD1382FC26E91403578E7B6660E38E4D1F05078772675DFE7C2919661F8F735AB55DC15B864A873D6E3353FC7341BD4DBF9F14F45FD83F414664DE60
	8B913146667A467AE727664F393C7E7CC9744B072953FCF3FF0EAED6EEFFA6DB534D877CC2E1FFFA212F7D8F5C1A7D74CF86322F2F0F14693E763326EB76393ED1E65217BF3D6FFA0874D0CC1540F3F6BAFF1F81F16E697E3974E62AEF7D2E5C446A9778C226D3FCE38E7846F7701296DF42C172E72A01BFE8E7254E2FB94003C0399455471F0B69F8AE878FD776AB601355D1643037BA71ADDD357E072653D9E8B448F29FB2C44E254EB02E13153ABA491941FD20BAB9B243BF1F47612BC42ABFFBB370FD2B4B32
	C731B7DB9D7CA035586A29F1B96C4D5D2ACF1F3235B01ED7D44FA01FC2B34827D0831F5056EADD5B0783CF448C6D3D1359B37C6B25328DE3966FC2C9742E53ADBD7A779E664D606F58468ABF022E129F2B1EB176E3394E8FEA9F2A9B53B37EF573A3FCB5DB0D7B9D5696B940D3708C0DFB7F21E83971C665C0E3019E5AC087428655DE6FB37E399F1CFDBDF38D35F308CE9F62C67709E575E54D57F59F77277BDD49F71CA0DFE95C1A780CFFFBED58625BE776F5E9362EEC994C1860DFAF91FDEC0DD0583730AE0F
	5C8A796134D17BFC172D8FA301FF4EB35417FB6C3EFEB5F4EB399EE5C73B176DDEBBFE2B4CF57C59CE675D51200363BEE1EB47A5D0CE7A81698DBB67BB0ECCE6EB49AC20677F00F2B1AFF3B0F303FF5C369F78477DF8A6724BCE5CF832297EBE1FC96F843805EFCBB27B9FD35E975F20858B19486D3E7554F3C54B7B7DBBA87E8841FFED07EF7C43994FBC20C709781F5FFFA371EF24BE9059E76DE76E9B3F49CC3124C512EF2F1BFDFD7BB36349857A81417370FE5FF86F6B5172F5ED41C0FD233FFE6C08746FF7
	8BA4AD67B2CF617798CE3953C41F891A4B41512CEDB63B5561EA5D16E34A4DB1656D4926771942BB75E75585AB7E96707B672EC8BE5E052A57D92D1B7BF96F6366C3F89F781D2A71070D69A4BF7B08F63F3BC40B6F567C6B0D0F4B3614EAFACC951A1E0CC30353D3D22973671826676C614169292D6EBF6E0E635A7DCBA7B4BB2F66DE2F1D40F57EBF96CD7729531A6E1105DFDEF7975B0FD13F03B453FBB933A22F77DDBB726B5EB33A9CBA2B7958FB60FAFD64BE6CBBC8BEACB17401EC0CBF2759981DEE022B6E
	F59EF39D7E27CF183C665A2A0ACA8B57EF59C67A731FB50ED14D633FEB7A650CFCBF7D3AB7494C75E535162A12226A463A125A93555545C5350DC5F50D1F1615151615F21BB3B24826A3004D40FE7C2ED112E84EFDB19B47C023F78FF1A5245A6A21F9EA5C33749E54E3D7497E02E7E9FD32462A556505E6AD561F955FF8CE113EB0764C9DB826E932467BDA0BF63F34CD3B3F5D2E79945D162256BF73274465F0F94CCD8EEB73C5CEDFF3010D591E8E194B7E6785CD367B5C3A3E329E18A6D4BB475BB55EB51903
	9B935CCECDB6AA936B37456A704A82777176747E7332246CD3CBD92D60BF3F23FFDF6738B5FB93768F4E57BFDE52E4771B753EF27A0717B57A2D0354DD4746D5FE5EA6D533E77019317139B3699D2674259FC9015FE12A71385C70D8514D316C17E3543D4D39BEE73CB5204FB673FA763E8B8F6A653AC0A6743A644EEC534B0503CC44F5494D4C504B4D86198557A757535FBB83B186BA385C77FC5EDDC9F8AEB6BE75397E6D71F6C82C46FB710DA92FEA15362A9A7B987E6E9134317D8A7B5919BAE0F4362A5F32
	3139A167B93E066138E806AB75411DBC58172F7EE0DFBE77363EFC9D5B7A727DAC23AF5FEB9986FCBD3407F97EB9F15EF5352F6C4BD77532FCDC3532EF5F555859B1A94727F52D5670F4CC2A7EAE857FBE816BB398F40581CC85188F907B9C0D3B6F7752381BBA0BFEDF6153DE7F5FD778CFC97F7B88C1BEF47CFEF1298F7DF19FBA2E850B166A698BFD6809BAF9754C255727CF01DF36D97D678E2F37BD3DD417E80F637677D2DDE28C7CB7E5CB9A6F00CA1B74FBA9516B11560A71230A9D82C7DD5EDB987F4658
	78B8A921CADA468F452B2484BC592E12925941F715942F385CDDEEC9FB08E5603DA4FDA1C97ED8C5CAD1BF1DB261A7A22693EE4CDB188CD731A27FCC2DD011B657497803D7A69FE6CEB5AB521ECFBB5A1BE40F4955E2424F44CC741D184962B25549D6CFF31B09FF9CE62A152D784D0857AC5C7179D3248DAA230F9EF0GC86F1171B2C6D7E4C0EF609F1575E13F8E0FD4C96111A22DE8EF5FED336D6E06BF3F42FAB24D76CDCC7805999849702B946DD741B8DF16B79B342E26EFF0D825A9527B7C3B9DDE0525F6
	8FC838BC5D20AE5EECB6E0CF4CB5F901C5DA051F6318546FF194E98D7F7E462438989F644AEA1F396607776F7B751C576A3D715232AAE993BE3255499EB6B24978A9A07D8CE439345C0DBFB5043F52860964E517C42F526D102B251416DA7CC9B22FA25B0AE84C035458D00D16F66337873BAB2D5EB6859F9BBF2916921C78031552BACEF6D81D2D3B79G78894B4625C5C7D89B33D773415F754CDF54E296DA56CFDB3BBD7D68422971D2DC39344261325A4A2DD8BDD552F2254DA3FB5BDC8EDB177BB0D7145409
	3D076D8A023A4DA2FC8C560CBB6C42690BDD5F89DD3DE8F5F99CCCE69EE9B76344A7D43360301534D9BDE0CA76D4A9B27B92A1D350EE7AD05971630A0DE7632425EA10183B9FF43995704AE3F5FADB209F0087A6BF8F08A494196FFEBEF778C3EFFD4694E9220A3415D799086EEE15DD6D3B0BDADA6CCED9A8007A8375DB04FA64B17B712C6B3AEB4351D77F5A846DDDAA45376766327FF96C7F9E7C1F47B0F98C133787AAB126EF77537F79533BD80F7DD061C9A246A6ECE6F3B9341472735FBF7F40AF8E44C4C3
	F465D2A27BC1AF286CE40D0BF749DBA68D5F32721E285B31AD5C189224F7D90B355D6E004E362C142D123A90EF6EB29E37860DCD6D130108CD6B1BCA08B6B162B676FF7B20AB8A25F6B259ECB408B7B93ED16D6FAF2A2F1527077EE5BCAD9BFC26FC2D7CBF7EAFD35316FF711ABE7B0F9AD370AFC18FDF3FD8DB34D8FDE2B00CCA9A8496D727EBFC1F34170E5FB8A4794E3D9DF1D2D2B90E668EF71B35CA1D892A2515CDECF9AAFB00D7111235990F494272A12E9956961BB5FCB362B9354EEDED469546F21B55F3
	11E9C0179C1FCB4BDBBC2DCD4C00A245FDDE7EB918E5A5A4BB2B2595CA9B7E8E8FCE750A34D1D3DD03DF7A28843EB8117E8A60E6FFACDCF6687E573F7A603ED68D5BE91C44AB3EFE1EFEE16C29747573F4E8004EA25119C176B57D19C16C500D814F8CA2DFD63F97FB597F7B367FE7431812DF090D4FF90576F7F65BBE16823FD7DD57EEF5B8942B63E21153066FD897F7AB32763BD68BED285FCBBCAA1153FEE7C33FAF68E47E97D0CB8788F2E66F5E3E9FGG94E5GGD0CB818294G94G88G88GB3D06BAC
	F2E66F5E3E9FGG94E5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG789FGGGG
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Generate File");
			ivjGenerateFilePanel = new javax.swing.JPanel();
			ivjGenerateFilePanel.setName("GenerateFilePanel");
			ivjGenerateFilePanel.setBorder(ivjLocalBorder1);
			ivjGenerateFilePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsOutputFileLabel = new java.awt.GridBagConstraints();
			constraintsOutputFileLabel.gridx = 1; constraintsOutputFileLabel.gridy = 1;
			constraintsOutputFileLabel.insets = new java.awt.Insets(5, 5, 0, 5);
			getGenerateFilePanel().add(getOutputFileLabel(), constraintsOutputFileLabel);

			java.awt.GridBagConstraints constraintsOutputFileTextField = new java.awt.GridBagConstraints();
			constraintsOutputFileTextField.gridx = 1; constraintsOutputFileTextField.gridy = 2;
			constraintsOutputFileTextField.gridwidth = 2;
			constraintsOutputFileTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOutputFileTextField.weightx = 1.0;
			constraintsOutputFileTextField.insets = new java.awt.Insets(0, 5, 5, 5);
			getGenerateFilePanel().add(getOutputFileTextField(), constraintsOutputFileTextField);

			java.awt.GridBagConstraints constraintsOutputFileBrowseButton = new java.awt.GridBagConstraints();
			constraintsOutputFileBrowseButton.gridx = 3; constraintsOutputFileBrowseButton.gridy = 2;
			constraintsOutputFileBrowseButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOutputFileBrowseButton.insets = new java.awt.Insets(0, 5, 5, 5);
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
			constraintsIsAppendingCheckBox.insets = new java.awt.Insets(5, 5, 0, 5);
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

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
		getBillingDefaults().setBillGroupColumn(getBillingGroupTypeComboBox().getSelectedItem().toString());
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
			ivjBillingGroupTypeComboBox.addItem(new String("COLLECTIONGROUP"));
			ivjBillingGroupTypeComboBox.addItem(new String("TESTCOLLECTIONGROUP"));
			ivjBillingGroupTypeComboBox.addItem(new String("BILLINGGROUP"));
			ivjBillingGroupTypeComboBox.setSelectedItem(getBillingDefaults().getBillGroupColumn().toString());
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
	D0CB838494G88G88G73C66BACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8BF8D45519A041E515D62CD8D30BAD3628D8F1CB57D8E90BAD35342515F631C50DDD5AD21B56F49BED5C52AC6B476ACD06E0424BGC120C099A5E02090921EB189A10F496441GA1198C81024C1D191B19A173E266068C6F7D4FB977BF774E64CEC2D07B59FC5F1FFB4FFF7E67B97FF91FBBC26126713959B90502101DABA8FF873285A143A5881BDE7E4ACA444D5815E590B4FF6FG2C90DA7D8B61
	198AF9F36E4AB29488CD11BCC8E7C03A6656AC439AF8DFA52CB856E4031784FA0C1077E361ED4340FADEADC7BDEF5274F3F5CB61399CA093F00525D986FBA9517FCA5DDA85DF8E69654ABB016C9CB0210C50AC5936D6031F20502543F381C08625492F5F2064F7C1DA861886B03BCC1519CF2D9BD9D10EF4F70E97667DF8EB4EB222CBA39FE16FEE465B29BC0D423F5102B0883F2F5A874F2A569EF735B368AFF10A01C0B0A0CBCECF09436B73F9836E923F688D0442C1F7D874C7AAAA6A98566C75C9243CF693FB
	4B3665D1D902B0865256BD847770B6D2C6CB613D88208A00597C4C32AA452631BD630539DFDE17506664BD4C5629FBD41BE7FDAAB5473717485FDD3FD461DBG79FB81477CF117575C2CBF66E058AF4A2DE2C072B13FAE5523DF137722DF26E222B79D52423EAC435AFD68574177502F4E4BDF9566BB1AF35ECA60D769BE25AE7629FE19563E103F0E4656558A929FE3A1EF8A40CC00CC00027DD906D20075B4264B5F5F8E4FDA07DC9F747B03015AD0284E678A9F893AA49F3CD7D4C00E02EB131C41B034A59637
	7B376D50318721E9FFEC4C2AF1F6C240B81B547620A04B07F2F2F5FCCBF936B827CB11915A2F7AB6FEBFA9370D5BC9390D0177EC25CC997EBD0ACF5360D97DFE15468FC94F033C4E83D84E1D4B51964B42FB05023243B9EF6A58127CD4CD4E54834C065987D4DBD8991F28A2E5AC14E799B6G6C8530G78GC617E3995F727E1E414A389962AA9C90FD6D3D0E1098166BFA3DBE1794D64A3A3CFE2032568346E79EB29FAB1709FB34F5B46731965DF219B3F1D74E44F2D69E534A55F2E13A2FA754BD373CFFECAC
	67F5B8053609F96571F57B9445AFFCBF3EEEDB0A31EE8BA1AF8A406A65AF87C93FB7875273818AAA328C45GCDGAE00E885564DCF5E5FF503715F2A299746FBEB455B701422F2D8EC8EC4E4B16014ECE258ABBAFCD2C4D0E86E28A0656108619BCB5B2593F4B4CA81A9AC4A5EE0G240BFE8902A042E86E2BD8C1E842924D35FA7D92A8700784147BC045AAD286E2C46E8839C4D9D2A9D87E239524AEAF08B610888260FDC685A92FA334FFCD0677148AAC2BF78F915C52CA68DFAB31DC0AAB4900F99802D272D5
	D4183CCEE206983E083135F9BB99474AAB31AD3D5209B1E3DA7600306359E65D18A150DA496A5FD3999FB3BB294CB115B8B6BE3E2DF308719B8F65157A312636111FFEE89D068172D8790E29A1B1BA99EC198910414B58D9E9E431D7A7C6A40C36E3E18894492516B82335D711B1C03AA085E413941282AEF809A8F56D2E4AD76330BB48E344CF7124BF2907C009F0FC24EA8543038CEDD4E37EE51AAF4AF2586B681525F6C916E1D8D379C7D0DBA2B1DDD5C47175B335341DD6C1FF8830238AFDDDFAF015A61EAC
	41E0CFEF48CADA0A92171F2563E38D50F7D6E17DFF3B184443951E1EC153EAFEEB710729BBA2F770BE547B21F48C949B4BF9F93EF1B0F15DAE2A562F5B25557AF53B32FA603A7BEB35DE5D313CF72A49D89C6C8BE8E2E1EBB569871C41C0C0228149D984B6C7FA45CA7C70863C72892F4BAD496D5ECBB0191BDAC56A54C57B1A3170BE318A7B093E4D58CF94DD3ADF18F636C8F77CCFFE20BEC7B8486A2770205ACFB01B5EBBC4E2CC72C9FEA8287AE0EFC0E6781D349FD3709183457D6E72E2F8369C11823D4D32
	64AF43B679FDBA6F5E8B321BG6C87B1CE9FBE34CE9327CAE32C8F7AFC4CFD481BF81078764176C20D3D5103EC6E2E5669E7EB8AC8B9CB67FBA59896C8209F16AEE25FDE33B2AE4FA67AFA55B163011AA57074E95A8843A7576A7B7D05DA52AEFA387CAF55AE92C85F4FE432FA1AAC92192130578FDD8AE9744A9A6251FA6D9AA2937CA06B0D42EA98AF2BF19E7CCA9361554E56EB2A896FE5BED7A7E3A843FD06629E024633A9976793C78A34EB8BD724CA374E89A4D533B2CC2E0E1FE73DC67B07A9001FD10DF5
	3521CE5B2754C6A2C127178E17CAF9BDDD270D87061BD5C762215B9B0E401C4BA5C5314C1F2BA3361271D4C1AB657607375F957A4F8F9ADCDEB9986613B7A7898C666BD533F6CDB2752D321CA589FC1D237838C0636B3C0B57F3185EBF53359DDDFB1C93AF085112C89F94FEC5C50BE6CD3146142758D9F5885794750DA4C6E4C80F8618F0885B5D095D5ABA18B637B4E19DCCBD446C19F5A83E8EC6B750B5B160059A2C036375ABB4F5GEB8418863969FACE695B6A8DACB6153441140B31A9E0DB7C11193425C2
	10D98A5084E0879846F55CE69AF47E5C909DE07E3C4194BF979DDF23767DA9B57D4781B667FC0216650C9AB6674C657827A8FE0E864F66224D94CF521910E72D4579D5FAA30DDDC84F28C51F52CC4BB465569A7486E4DEE6F7B610769B8CFB5D5EG6FCFA88ED6E034F3E638E74C09470DD4337E3871AB337E3811E69EF8CE10E1CEB42E3071FB8F0D45D470AF0378C9F185E602AB28033E248E476CA7E9D9D8A06D61389994B78A5213B86ED8634D0C3BA8F7B0BA2643F3D3BA8CCA9D8FC6474A6D65C692A3D11E
	FE1526AFAAF2D689ADA6D22FBE71E2309782202A1E141BE842BEF99678128ED0G7822FAB6B7E27D43577AC83F528D0B86280676106494561A689A580C3CC9C01F8CB085A095E0BEG53F9FC89091B38ED8A451EE74BC8BDAB6B92095030DC5CF3FA2DB1FE3CA0B659C1378C40E8C6366B510C067C71G2C2D7C2B8977DC26GAE5304FE1559493A0D77FBA5E21FDCD15198763A6AC4370516926B7B1E2171C6780B9B306FFB034A34C3BA8AB026817B3E68D66C13A7BDF82FE09956223BCFB457DA16B33101454E
	4C86356F6BDC7BC2BE53F9440CBA734CD9069AB39D834D5AF9GA9434F433F92F8327CC7E372C959123628667FBAA61F583BAF06FFC523B61F1451938ABF736D69AD685B0227A78A3E474B936E010DB2B31FA6A94F4E7D6B052E27D3F318EF3B1B7214F611E646721CDFC14AF3C3E316E1AF00850051BE590E342EC6245DF816500E03F48A404CC6EC47F7746BFBEF2CED5EF713FC4F8D190F75694FB4503D23C65627E7F37CAFA8BED303E7711B5B40778CA14F5E04FD7D171BC96CCF0774DC0079G854DB02E81
	D81A316F3F50D4AA8CB21ED903A18BF46CBECDD74C7CEBB35F5C1E4EF75F44B8C979661704A9374E4FCEB4AF31B733789826BCB3772E27F8665F7E43443F4910B793A093A02F8556FAGFBDB503FEFB76DE07E11E9D22DAC0BCE8F1D0121F3B62F542722C96745FCBB5024752D8964F53528FDF928A551B8BD2B1254C5D28B2B230D9C7F92454F5060D95D3DCE631A24179E06BABA0C317AD43BD6FF1A8572AC2A4E25C792693F1F463E718853739647FF0D621BB4F826FFB34513F48872B20E207E679B849E47F3
	B56FCB9ABE6C9E45606D2668E3554162E2D2F362794AE34D7A7315EFB46B4FD73E53BC70BA77074D095739BFED562EF3D9DCDF3110FE56F3B44B1084B091E0AA402C239853DDAD4ABAC0A71E9BE061DB9F24BB00B26ABFF4B8312F6643036F9B9EBBCC56A90E20AC877DC4BE62BB8E137A1203A10AE438D307C99941F451A3B3D285EFBFCC7AE91F54CD51ECCEF6147673996017F08C67E999EDB8F7AB6238EEAB4E5D1A0E71332ACA3206C806F48A40EC0085G46D6988B81DCG2381A6814C84C88348BB1EE558
	81E0B90EB2DE26B2DC10CE8218GB09DA08DE0E19B6887B08138G12GA6GCC87C88348EBC7998BAB67127656BA78BCCF8BB59AFAE28F7163C67808CF44D774E726A20778CEFC23FD6AB16637EB08B68D5EAE8FD1DF178C11F719661D74F3036DC3A12CD0742ED15A45EAC19DC356AB6F39B436D87F75D1794AE26E05F60C4D62F60C43148E0CCD8B47151C40588C7118F868AC19B7CE03749C0085GC6AB4492C097C0540AF4CFD03AE4C8CF81C885188F3036A34BD08E505901F45B4E123DF64277D179C8EC43
	77AEC5AE311158CB6CDC40FDE9223ABF4A725556BF46C63ED217CB14BAE7FDC612A5F13FF53BE570FEEB2C056E39C0FFE33D9812986EF3963A1E6D6E0EC83CFF7C022500631AA32D410817EC5C30B50854C4689D41B0DD0032B55175D6DCD3C4210E46G4C84188BC0560D0CE6B5DFF74C6FD056CDB668D3EC0CA6E4A3B4D72D26814A8967F886852A002795E03512F65954F9A033B575E46CDA4AA2BA78DA2A32EAE5CC9ECC779C413AE094F937D713B6E6127CE240E592AFC6DA435285EF30B7E205A226E48A5D
	8FAF2D5125536ABAF10E34C9E50F2E01504912B6FF7BEEA2238106BC7745C4B2B2AF2F5325093767215EC2EECF3B8C63ABD746727FAFD848656865CF58C366BF8409A54270675EDA2D7A50988E7606C88CB18BD1776415EBF5E8E265AC6BA4E5C1B3AD5E085C6E8C87FDBE320CD5E46C5EC7E23FDD022901A887499C006D697E3DCA3B561A22444D9CF8327C23B17919CA7E5A9318DFE352666FB841722DBCBF29D2CF3E076717F56965DF61795B4E68650F61799F1C5153BF0167CF336A71CF61798D9DFA79B3F9
	FE29CD1BBFD711EF6C447C65CEBD7E3DBCFF2EDD2F7C1ABAF15F60598E6D3EC1D7A74B9F854FF1DC469207D6C60AC2B327937B009CAB4A486CE47DC45EC9D81F1CE4FBCB0C6617DC4F5E132C1F3040532E5030FE226144E07D04C183D1609DFDD26DA7461F0C6FA7CA9613FE62D82F9C6A15C90F20EDF73F712C0A49A36D4D6C15FCAEA57F500A37E3726B42413E08D4578BF34300405BAE71C93BEFE38D3A5DBE050A513C68A3F3CAF2D29CD69A95437FBC27C041B77844D0C4F2E95A6AFF6FA136B51339AE8C86
	50666ABD12330774862CBEDEB99BDF67248CA71D447AFADC0A0FC912BF0D675FF7CA1BBFDB290385BC7F9117B63F6014D20F27B0FF22DBCFFFAB4FAF5B22176F62794F6F0CB947D1640FBF057BD4E70E637D029440E51C42FD2AA79713B53C5E5DA0A7DD541738FB3DA53D3257D7D1E1754AD08FF4B4E37BA78F9C47F313225338FF72FE9B515385E999E074E954FF309D7799A6GEE4EE95C0399590EFBD105DDA863B1CAEB01348B608A40A6014D5588AD7B4BBC24079B5BC56E2AFD231D2458FE41EF1662FE41
	06C7BEAF181E1F1F3DD9505FAFC86DE2F31962B36A1E19F573FF65AA67985D894EB1E80CB1DF9CD5381FE4BF436FDE79566B7371C1D4A907AABC83C9663CDB0AB76873720645F87BCC28F75699AC57EFD5A2AE6F8344F93A7214F22961B87B99240BF25C5DE75016499C77BFD624CB6738AB9D08B31EC51BC734145DE8BCE987CE816F4D39ED98D31DE7D1CF03936D9E43F1EBC4241B41F53B9BB66A1717E664C4BD4BECE83BE047387B815D4FA807F4AB00470EF26F149354839FFA194CBA875AB44C0EF13F5D0A
	6DEE8A60B26DE8FF580A3445A26A4F6E207B3E100E820C91D1C62E8D5B4EC4404D95D146919B361D358E14F1195236C25A83B04CB154363339C360E534450EE5146CC01D99925ABD4B016573D67BBA7D72412EDD11B77B9446D51E93F99F7CE0D3029857F47B8A7F0BAE34274609767C5ACDF0E4CC0AF21CE78B411175419C2E275A16289D62B840F81717F09DAEDC47B80F8F36D799F5452E2F985F79C1797E9617E6FCDF9914CF8BE3C1B75B27FB14763D93DD71FB3FDF277894D77C5E6F21B60C2BE910D7A421
	3F756DC35D1BE5FC47EF126F36A173A977A4E95B9AA5457BDBCA71E325F8FF3F414720711037309B4FD57E633476CC3960DEE38E72456B75F433FA163B634F14EDB6B24F1A887869G69G029B6603GBBGEAG2CG9E00E1G49G13G26814C86C883188F3054037BF42F37EF0327CDBC577F249E104AC1925D2C8B0B6C4C1515253BCADB16C68F33F32F72EC72745F87D06A0072F569649388A5643BC24FA9129270A583FE12C72FEDCC2B545AB9CD21192DBC53936A73F7913E85CAFE0117BD0B14E739B791
	1FF4C62B4F22505915271C106FDE4AB7CAA71F403804FC8B2CDAFD13953A694AB3B5A15F708EADDF06C2A71CD36E901DCB4467338D54060A937235B835FCD58ADD2B72F4A94F2806DF390BA08E24EFF4C2FDAB291D63A1BFC507E6068E0E35698ABB3D2BF0AE7EBE2D0962B34E455F271DF996475D8510E76F413E2D51B1105D53FB9259DDEA3D39B120FAD0BE7DB120EFD0BEAD2476609850CB679099BD71FD62B50A1F5F935FA73E5901FD62C29F74CFBEAC27A58346139EB03EF5B74957B6E4BE66571F68FCE7
	26AF5E5F79941F6A0B77F7B81FCB25C1DE139F7D5DEE576A1F64D76E03AB4F597EC4F1712AB4105D698979DEB8254737C021AF88A462FB492547D7245017435392500B43B4375E3918DD211561B9CAC7A701F1F17849101E8E109A086F937CDB74F4E4E86804203E0E42B8FCB1242BGDA0371BAD615E87DF7A979D165B9DAC7BEBBB718360A4C873D811FB7G0B8EFAE3DD407D7A6F52F56CF860CD89E2BCBC56A6F01DD3B56F8DED9F7F9966877FG9DBB2D2ACF7BB56F5FE97F78F527FE2CBA58FCEC6A896213
	F22F3CE30B10E0AED56B10DBE0B9F04CF1CEF24AD6514DF94E59921F9F1C378DFEFEF04926FF6EB94C567F5CF324CD7F5C73F61B765C1361465848FA468D733FCECEF7B7471D6438FB29CC02BB61F5499E0E9FCF6DA278A609A8C47C03542EBE0A6BD4F00FE8F0A7955CE49BB953G17BDC1FEFF6FF19B39375B6D75791433EECA638DC4A419174737290D5E1028317BFB9CC76CE6ED719A5D8719866DF0D6904F497C74DCB78D5273B86EE7EDF8F6A60490F7BF65ED05340763728EA21D4CF1F71910EEBA474DB3
	A15DAC0E336DA1B86379AC435E7308FB3E926D2B61386AA3E81F15633EF8846559B9AE096E3FCCA6E90E7BE3974A1B4DF13F15D1DEBA4719FAD15EBC0E3BC277E9CCE1509BC65CFD14578369A84765F05EA40E7B5A9934E5B647255A11EEAE472D22FBBC259170B70238DB683E019552AE0EFBC960E7199C174CF927F15C4AEC021B8D69B40EFB7D9A72E6F25C889B62EAE44409F49FE88C2447F35C2B9788AE855253B96E3DFCDE179CF7197AE66C85F93D085BA3A21D15633659B0B6A6F05CED94B7995233B96E
	375C3E74DE9CB3EA6C74DB228B18EE2169D61E7E59598F536F913903772DFF7CD0BA86BBDFE73EFF6060F5FD814B6353D69CC327F1FFAF533DA4761E24F9DF5671710FB1DB7F81BA7EE2D3FDFADD737EAF360FDF77780FD5872B53DABB76CDB3F9BD07AFE05FB417636EDC0E711041F1F95D082B6AC3DC66A9649D4DF147CFA3DDB2473DCE777952A1BD1F631C9252AD0CA2EEB36535C25A47F10BBC84B78C52E3B82E076246C1FAB24725F810F7BA476552BEB19552699C7748E6543B00630697513B5F9721EF3F
	083827A9CE0674D80E3B036EC3CE0474940E1B37056B653823DB29DEC827F35C0B7B39DE0EEB5BC975DE823D1790B7EEE7FF3D5B699E268CB4C91CAE67G7DDE925293B94EFDGF524DC42BB5529474918AC13239EF832BD618DE4F8D64E586E2E22E3FDB0A057DC2665C3475C05647DB24AC8EAA3B4B2CC10987F1D2788BFBB0F7CC35BD06EADA401CC826B95FC7265787348A9D5645E079C8C7A2C5E90B997D1ECCEB92D6A5CF4F0A01D86C5DE2E72BEC15139D941CD5168E432F71ED1E5AFE998CCB64AA5B0DD
	A31B48CB0577FBE27C716FA1F3A73F98265FCCB0FD27BFA07AD8F98B343C5360BD9300652F352B76FC7D48E0652B35A73EFC0DD7624B7773DD7A65DBA92AB69552F63383F84B2F20CD32C33569F46FD04E2063ED727433692EB331B178B055AF835D30AB9803A9F2EC8C5E63D46DD9B728BDEF8EE04F64AB717170CD17AA7BCF764162C1ABFB6A1558F818F5A5BE9E36F7447ADAC367D4F300AE1D7B7AB9A15657DFCA2ABD2F8B8359139B978FEB2F46467E0E2B2A2FCC7FF7297EF2401B2E227EED59317A85372A
	FF75754174EB4BC30E53BFCA231F493B57067246DE6DBF9FC2B96853C42019FA155DE7A038D9D763E3A96A0CAD5FCF53B9669C20CB677E6575457A3759237AD71D0F762C3D565F9E22E709462E9DC0C3GE32966DA3CBD1F3113F3B9D8DCF9AF01CD222F5667F58748ADDD257CA75971AC54F28D6FF83408E8C3D4478603E2831649E84586AC137157626379B62F406D395FC66EDBDD10423257A97A58C1EA1D98EE8D7A3C4E0B0ACDD369DC181CD7CC62B63D466BA9A3C139AC53543DF0BD4BD0F8DD3DF7D2FCBD
	3EDC1ABC44E7FE97CA403BCE17E36E626C3D4E6E58F6DEC7BBFA9DE8076F3A3E9D799ABBF644154D306B71E57368B9013705F334AEC683CD72F56C77E6752831B1F179E0FD0196E6000C99CA9990FDF32E47477E266E9B99EB301E53B57288EEC1BFDF7E6CD3ED751C9EC8761BC2ECBF0136E23F15B7AC3E3ED6CB31EDE6FDD055F5C99ACA1F90DFAE057DF4D51C265F428643129B62A360427AE92373C3A3506F9806EDF923A7562E6A109A6B37F8085E8B5E0817FC2B4CE4FC4E03675D961041F03F2438AEC8FB
	385C8DF1E352EEBAA60D027C3143B0961F5ABC1431A15E6F697D7CCEAEA274D200ECBC3994FB3FDA04F782E6F15BDEAC9A38CFD940F952B84F03DBE24BC9BC2F467D462D68FF61F0241736467AFF19526C057C1A61687F7F6E3F997F57E09B989E6F3FF7E7FF7F076DC4DF2EF05BEACBE2EDBBC16747E3A0FF9C27D9F7A0565F24309A2F578E8C56B686EA47B30747373B29ADEA4C09ED64FB0C0054E7444F84CB4CE0E8713BAE2F688B3A5F5DE1AC5DE1AC5B3D8D6FB30D6F531ED517AF5E1F707B6754612C3F4B
	D01E535D6B057E77BE50262D96721E78DE087508FEBE72779DD55FFF5E2A7A377B480D7A47F8973729F226DBD5B94F4BC31373D207AAE7C627AA674EA14A49BAB9305F6BCE2B6F3DE7D4BD97049B5513384C6FF3283297CA2A6CC5599FDE76FB5DA4456E3CBCED43FBB9C5375C68FDA1342B452D3E171DD3ED7C5D059B351159904CC79B3AF4EDC09D569ED547776CC6F8EA2E731434137F7889D324242D3931494A64DF96D17E5884BEAAF16CD3657F5C36B7CE3E66A6D9092D214D5ADCDFEBB95BDE5FF64CE229
	2BEDBBDB5BFE76E8C303294144F47E7D846A1CF943650AF65C6B97849CF31F5EC67AC03D3B07E4A644F47D3B9D472905A3709E54038E54DFB58267A7CF86B431FBDE75357B066BABF15DD451BEB331CFE7A3AA6DAD327A7EC02F7A3E2BCF35695374CEAC3727BE688B06CB9CBE51594372A73331BC5CABB15E3B23AA6F7FF52D6C4F9B06E1C251736AA515760B1B0756A794DED1F9FFD0C44A37DB74C5A40EDB35651D0163323E612835210D531B378EECEB681A2A6F397DC333758B57D55EFBF6EAEDD57AC9131A
	1FB5C4590B4D04171D37BDC13FA917A14E460C40BBCC59077FAEA43E43549A8E06E0D9F111E169AF47A8FB1B33E26E82CC86F953C75C485E371C2F65CB851E0C9B629B96F317C5C802F9CD520D70151E50729583CF558D71BDE19F684EC02B0E8C4677C84C5D959750C58751476AE12A193C73EFCCD9D163999C7DEE1F7C6E91543177927D5919F6A08C3815F39DBAB60CCEE2F39865BBCB0A1B90033BBD3FBF5D187C7EF477087D6946097D699ACF7427331C68CF77D3FEE6B5AD8977B51FF274677D21C34B4B7A
	D5F16BD255F7FACC4A709B224BD4BC9E13AA3FCB71BBBBFEF311863266832C9D19E5A887689C097D6E92BE56C4C7626FAB540B713F2F702A207DFD045E18FC727DE2F6CC7EC3B179E4AED81B2D4DFFA1A6BFD7434FE227A0A646A703DD53C78E9CBB4AD99A2FAF220F61F6707AA2BE305F14FD4473A9A834C9AF8B39113070966FBF1A69A1705C8BFBAA8C3F7D32A1C7D858ACACE207624D42C2F2325DAC6452036FE641A087C3511020AEE2A9C44E8B34770B454AC22172691411FCA2E2BC96A2F18B0361D21AE4
	1F29D548427AF6097C6015B10618651648426E233DFE0794B6863B0D64B3B1A3FEA7E634860D6D1298F6FA0C6C63B0E31BA412EFC6A2A55C1C18BFD9D82D9046488183A0EB85AAEF2079328468556C2332FE64B7E0114249AD120545FE7FAE17EB57C5780BCB2CC255F45F44C83EB083A51B638AC57DD5B0C6D79C5986D21761B7B83450E461ED765D8EABC213B78C9C4170C590E7A8A951A14FADB5C600C4D8CABE47B1AA5F634842F2767D0DD18ED2BA0815E5B1E34D776F5F777B193F32C58C4222E6E19DD932
	35CBE15AB3C964D3C07C99C833109FA2BFB5C4FE258D82A9C29F39917922CF3288055DED64A749A232642A45BE8FC2E33505E85AC53EBD5855A4C6BCB2D9B6D6DA04058172031542CA166D93836EDD2C832C245118DDDBC36B18F49582583B0A5ACB2414F0A92B3E755616F7DA522698049C3330589794DDE61194CF3310AFFB42D2449374392221C3CCD0DE9FE9BDF4D790442D676E93E7CB488EBBB73AA7FA5F68E5694B4CB950110405DD14122CD0CBB8052B5EA306C11594EE16A57AA5C2914F9B7EB7F96BCF
	9A57F46588590A13A4F67F9A8D49E0D5D88CC43A219D008546B88BB00B8BAA396BC725F7BF7C5AA9AAC8E51505CD2C4800F517DB8A7AF755F6F7FB839297G658762B7F2710426A40EE6E5744E55AD4F3E61007AB68986FFE9A97DDFC67F6FA67F4BA8260CE24AF6C3A1E676EFFE5A3F787CA8ED31FF13D910703E09D4F30999EACA7E454BBF7A52FFD6E5E600F7E6A1177E209794F601CA451A642D136E5E38E46C5C5BC9DD04C8C8205CC55D225F6B03463648C4E7C9D102AF096A77DBC3462E6D9701843B21FF
	A8913411E63ECB7FEF99F2C1912E77A86FB622103C6C08756A40C0DEFD22AC2D7E273134E168117209327F48BFCDC9DB7F49CB3A6B236AD3485FC2ADFC72FC6DD6FDA5FF0B957B97F36CE28DD608416676434616C0D8D89E3B524EFEE04D0802BB76746688F9E6521B7BC29E31D99989AC4292871D1ECAE120150582F544233CB0FD48F1425CE23D0AF792BC4BED8F09CEB24348F709619EAA011864BBAD64F7075D8E2AC096F2CE1BCF03DA1AA259810B30D876105F61A1C33DAC2CD1C53712AFFD148CB6B9915E
	G8851BF6AAE5D9CFB630D985CA72AE27B7403F871A74F52EB7A168A1FBCCBC7A630D440B303ED5735E78673C72EC9F8E6E09C2BFCAFB6B67E7B3646G71A9847851E3F1FF679E7FCEA1713D6AF63F687349222F27B660A2F72C6BAE4A127A3BD616G119FC19AA513653587085D67B4BC7F8FD0CB8788ECE9BF7E099FGG94E5GGD0CB818294G94G88G88G73C66BACECE9BF7E099FGG94E5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81
	GBAGGG439FGGGG
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
	selectedCollGrps, getBillingGroupTypeComboBox().getSelectedItem().toString(),
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

package com.cannontech.dbconverter.converter;

import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;
//=======================================================================================
// This is just a GUI interface for the command-line database converter
// Creation date: (7/11/2001 9:49:29 AM)
// @author: Eric Schmit
//=======================================================================================
public class ConverterFrame extends javax.swing.JFrame implements ActionListener {

	//===================================================================================
	//this is a thread to do our work for us
	//===================================================================================
	class PrintThread extends Thread 
	{
		public void run()
		{
			getOurConverter().processStateGroupFile();
			getOurConverter().processPortFile();
			getOurConverter().processTransmitterFile();
			getOurConverter().processVirtualDeviceFile();
			getOurConverter().processSingleRouteFile();

			for (int myPassCount = 1; myPassCount < 4; ++myPassCount)
			{
				getOurConverter().processRepeaterFile(myPassCount);
				getOurConverter().processRptRouteFile(myPassCount);
			}

			getOurConverter().processRouteMacro();
			getOurConverter().processCapBankControllers();
			getOurConverter().processMCTDevices();
			getOurConverter().processRTUDevices();

			getOurConverter().processLoadGroups();
			
			// do the points for devices
			getOurConverter().processStatusPoints();
			getOurConverter().processAnalogPoints();
			getOurConverter().processAccumulatorPoints();

			addOutput("");
			addOutput("");
			addOutput("Database Conversion is complete.");
			finish();
		}
	}
	private javax.swing.JPanel ivjMainPanel = null;
	private javax.swing.JTextField ivjPathField = null;
	private javax.swing.JButton ivjStartButton = null;
	private javax.swing.JPanel ivjButtonPanel = null;
	private String thePath = new String("c:\\yukon\\client\\export\\");
	private String shorterPath = new String("\\yukon\\client\\");
	private javax.swing.JFileChooser chooser = null;
	private com.cannontech.dbconverter.converter.DBConverter ourConverter = null;
	private Vector ourOutput = null;
	private javax.swing.JScrollPane ivjOutputScrollPane = null;
	private javax.swing.JTextArea ivjMessageArea = null;
	private PrintThread printThread = null;
	private Vector outputVector = null;
	private boolean gonnaPrint = false;
	private javax.swing.JButton ivjBrowseButton = null;
	private javax.swing.JLabel ivjJLabel1 = null;
/**
 * ConverterFrame constructor comment.
 */
public ConverterFrame()
{
	super();
	initialize();
}
/**
 * ConverterFrame constructor comment.
 * @param title java.lang.String
 */
public ConverterFrame(String title) 
{
	super(title);
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getStartButton()) 
		connEtoC1(e);
	if (e.getSource() == getBrowseButton()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
//=======================================================================================
//Creation date: (7/11/2001 2:17:46 PM)
// @param output java.lang.String
//here, we pass our messages from dbconverter off to the eventqueue thread so they can
//get drawn before the whole damn thing is done
//=======================================================================================
public void addOutput(final String output) 
{
	javax.swing.SwingUtilities.invokeLater(new Runnable()
	{
		public void run()
		{
			getMessageArea().append(output + "\n");
		}
	});

}
/**
 * Comment
 */
public void browseButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getChooser();		
	return;
}
/**
 * connEtoC1:  (StartButton.action.actionPerformed(java.awt.event.ActionEvent) --> ConverterFrame.startButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) 
{
	try {
		// user code begin {1}
		// user code end
		this.startButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (BrowseButton.action.actionPerformed(java.awt.event.ActionEvent) --> ConverterFrame.browseButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.browseButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (12/11/2001 12:06:46 PM)
 */
public void finish() 
{
	javax.swing.JFrame box	= new javax.swing.JFrame("Complete");
	javax.swing.JOptionPane done = new  javax.swing.JOptionPane( );

	box.setResizable(false);
	box.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		
	done.showMessageDialog(box, "Database Conversion Completed");
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getBrowseButton() {
	if (ivjBrowseButton == null) {
		try {
			ivjBrowseButton = new javax.swing.JButton();
			ivjBrowseButton.setName("BrowseButton");
			ivjBrowseButton.setText("Browse");
			ivjBrowseButton.setBounds(415, 21, 85, 25);
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
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getButtonPanel() {
	if (ivjButtonPanel == null) {
		try {
			ivjButtonPanel = new javax.swing.JPanel();
			ivjButtonPanel.setName("ButtonPanel");
			ivjButtonPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjButtonPanel.setLayout(null);
			ivjButtonPanel.setBounds(84, 307, 522, 96);
			getButtonPanel().add(getStartButton(), getStartButton().getName());
			getButtonPanel().add(getPathField(), getPathField().getName());
			getButtonPanel().add(getBrowseButton(), getBrowseButton().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 11:24:36 AM)
 */
public void getChooser()
{
	File temp = new File(shorterPath);
	
	//This will need to be updated someday for a new version of swing
	java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

	//we set the chooser so it will only look for dirs
	fileChooser.setFileSelectionMode(2);
	fileChooser.setApproveButtonText("Select");
	fileChooser.setApproveButtonMnemonic('s');
	fileChooser.setCurrentDirectory(temp);
	
	if(fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
	{
		try
		{
			thePath = fileChooser.getSelectedFile().getPath();
			
			getPathField().setText(thePath + "\\");
			com.cannontech.clientutils.CTILogger.info("** Chooser path was: " + thePath);
		}
		catch (Exception exep)
		{
			javax.swing.JOptionPane.showMessageDialog(parent,"An error occured opening file","Error",javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}
	else if(fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.CANCEL_OPTION)
		fileChooser.cancelSelection();
	return;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("Conversion Messages");
			ivjJLabel1.setBounds(89, 21, 147, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the MainPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMainPanel() {
	if (ivjMainPanel == null) {
		try {
			ivjMainPanel = new javax.swing.JPanel();
			ivjMainPanel.setName("MainPanel");
			ivjMainPanel.setLayout(null);
			getMainPanel().add(getButtonPanel(), getButtonPanel().getName());
			getMainPanel().add(getOutputScrollPane(), getOutputScrollPane().getName());
			getMainPanel().add(getJLabel1(), getJLabel1().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMainPanel;
}
/**
 * Return the MessageArea property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getMessageArea() 
{
	if (ivjMessageArea == null) 
	{
		try
		{
			ivjMessageArea = new javax.swing.JTextArea();
			ivjMessageArea.setName("MessageArea");
			ivjMessageArea.setBounds(0, 0, 160, 120);
			// user code begin {1}
			// user code end
		}
		catch (java.lang.Throwable ivjExc) 
		{
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMessageArea;
}
/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 2:26:52 PM)
 * @return com.cannontech.dbconverter.converter.DBConverter
 */
public DBConverter getOurConverter() 
{
	return ourConverter;
}
/**
 * Return the OutputScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getOutputScrollPane() 
{
	if (ivjOutputScrollPane == null) 
	{
		try 
		{
			ivjOutputScrollPane = new javax.swing.JScrollPane();
			ivjOutputScrollPane.setName("OutputScrollPane");
			ivjOutputScrollPane.setAutoscrolls(true);
			ivjOutputScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjOutputScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjOutputScrollPane.setBounds(85, 39, 517, 231);
			getOutputScrollPane().setViewportView(getMessageArea());
			// user code begin {1}
			// user code end
		} 
		catch (java.lang.Throwable ivjExc) 
		{
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOutputScrollPane;
}
/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 10:41:00 AM)
 * @return java.util.Vector
 */
public Vector getOutputVector()
{
	if(outputVector == null)
		outputVector = new Vector();
	return outputVector;
}
/**
 * Return the PathField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPathField() {
	if (ivjPathField == null) {
		try {
			ivjPathField = new javax.swing.JTextField();
			ivjPathField.setName("PathField");
			ivjPathField.setFont(new java.awt.Font("Arial", 1, 12));
			ivjPathField.setBounds(25, 21, 369, 25);
			ivjPathField.setEditable(true);
			// user code begin {1}
			ivjPathField.setText(thePath);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPathField;
}
/**
 * Return the StartButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getStartButton() 
{
	if (ivjStartButton == null) 
	{
		try 
		{
			ivjStartButton = new javax.swing.JButton();
			ivjStartButton.setName("StartButton");
			ivjStartButton.setText("Convert");
			ivjStartButton.setBounds(230, 54, 85, 25);
			// user code begin {1}
			// user code end
		}
		catch (java.lang.Throwable ivjExc) 
		{
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartButton;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getStartButton().addActionListener(this);
	getBrowseButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() 
{
	try 
	{
		// user code begin {1}
		// user code end
		setName("ConverterFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(685, 439);
		setTitle("CTI Database Converter");
		setContentPane(getMainPanel());
		initConnections();
	}
	catch (java.lang.Throwable ivjExc) 
	{
		handleException(ivjExc);
	}
	// user code begin {2}
	
	//ourConverter = new DBConverter(thePath + "\\");
	ourConverter = new DBConverter(thePath);
//	ourConverter.setCf(this);
//	getChooser();

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
		ConverterFrame aConverterFrame;
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		aConverterFrame = new ConverterFrame();
		aConverterFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aConverterFrame.show();
		java.awt.Insets insets = aConverterFrame.getInsets();
		aConverterFrame.setSize(aConverterFrame.getWidth() + insets.left + insets.right, aConverterFrame.getHeight() + insets.top + insets.bottom);
		aConverterFrame.setVisible(true);
		aConverterFrame.setLocation(220, 150);
	}
	catch (Throwable exception) 
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
//=====================================================================================
//we null out any previous threads that might exist, then make a new one, and start
//the ball rollin'
//=====================================================================================
public void startButton_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	
	// get the text from the input field
	thePath = getPathField().getText();
	ourConverter = new DBConverter(thePath);
	ourConverter.setCf(this);

	printThread = null;
	printThread = new PrintThread();
	printThread.setName("PrintThread");
	printThread.start();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF7F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BBFDFCD4551561BBCA00A891A3E09BDDEDF125ADDBE10D2DFF5094373822D20B3FE5DB246CCF2C38BF34F10B7B231A2DB71F0498C890B49FFCCDA0A0EC6984A85A9863CAC808898CC94804C015ADF919F9C986DE66CD5EFB13CCBE88FB4E3D6F3EF973B293CA367873705EBD775EF34FBDDF775CB3AFE4475E044C54349D0424E6927D3FBF2592E27B0C10375E7871B60E0B92133208693F9200ED647E
	1E8CF846C25F52EFA6E565136FBA32217D90342366A6E595407BF65253367C93F809304EBC681BFE35780B515779154C5759C45BFB4E6642F3AB40BC4061F303086BFFF236D047AF53719CD25388D95501E34E1F5D25634A215D84908FB03F030DDF824F4DF465C995657A384BF7A5108D7F7CD15A9624E322492134134D2D531FB6723D5464CEDCEB007209F322201D88001BCFA7FF697D941E15CD57BA7F47A5F75BDD0257ABFBB55155E5F7373BE4EF2F28E822E2B75EAAAA2A79EB2DA2F40BB0D76AC27AC9E7
	33A9BDC2CEC0FBA345FDD20AEBAE007725GEB8C7CFFD1FC25A7A92B85C07270FDFE7ED1253E0FCB035FA3177F451B1E97E11F93BDECFF73BD41FD46795F5ED4C4F5F23087E53F8E7AF22FA6E559GEAG5CG9381F6D2DB78036F3F61D9552ED54B5D5D32374A67BBA53915F332DB14603D2282FAF4DC33681295B7A1CC5FD1E70F0461E7B4E073265F72BCA613F9FF643A1F5F7CAD326A3133E91B4248E4553C23E9F32FB2D9AC3C9A1489DB7BA79157DEF1B554B6C3573EC84CEBBBA64B6919E156DE78626134
	F5BA1D94535AC72E212E7F1E6AFA973C276A7641708BA95EE742B3BB2D24F8ECC7C15F5AEB5CB6AEED653CB8B2E6137CE30E30F208CF2ACA2B13988FED1215171E83C8BBC6E2EBE69A3C8CD1FC02894FF8C927F8ECAF043E426E24ACE6DFE5AA7A6D9034638196GAC85D887106105F1DEEEE33D3E323F52461A84C5BB6577C8EED1A1ECEED94F077094831AA254FBD5CD703A44D6C171886D122892FDCCF98F4A20BDE45E675476DD30461722D7D4844DA3FB01BA38AA382C4A465476E0B0F5A9A26DED71F40B30
	C0370FF03A8DBD5B512F85D53B60F38B1A989C417ADBFBD0A73DC2ABD08482704E64F2D9C3F915437E1BG5A8CB97C1452BBABAADED12A28F04872B53F2F85F7C296F8D14E2BE96C5060BD464BE5FC490F388D504E15B91DEC65FD931D9A0F8B599714FE6EE38BCAB1B65BE4EEB3EFDC62B6D33365E1F26445B3E1ED8621D2E63652A2DBFD678714A672455A27CAAF5D229F7F3500FC33B53E3EEDEB249B3A7A27DE5455902C998F10E84838DA31B11BBDA528A23752C6858CCCF487A54E786CBB102D4B9369B25C
	F80ADB8E74B69A7B59FC6012E95C5FFACFEC0DFDF48D241FF99B56E03279D44573C86C953DDA0D68933DEEF8D1753E13AA5A1C6E2F9D32B1271662FD605F5560D02A01F72868030226A91EF63FA6BAC5CD73F8BB037DADF4AD555F6E8B06863E5657AA6ED16E837FB6463729689BF0CEFBC52AA4E30A5E5FCEFDD31444EEE03ADA76FBB5066F52503E2F097DFAEC10B4CCFAFA85490FF10665888B0477F08F0A279BD40F210A78FC6837EF99B98E35DF40E12ED3834FCBBE6E03FD7B398F168EBCC096DFB910966E
	8C09FF38A6AD5147FCEF19AF68030C277F7507675DC5E3040ED7992F5F3E08F32DC98A3379F77B104EFA20CFFAA0CE77F01B7F259F7BF9DC510D3DDA16A4A6CAC3464BFA73D1A7E20FDF04F00C816C2CA1371F753EEF69EB55E5487AFF510BB214CC31AF39071E0387F88EF8945AF5GDDBDCC0E73FA189C583840C1BE2ED2013C89C0D35838058A9B47767F5BE254894D672E8A3DC2402E7601DDD5D41C2112E0395A951A9B235F24A8BCA7FB3A81E5F3C2CD4AF2G38D52E3F1FEE61B9D95C358432666F0AA366
	1EC3AA63A3C18D6A4FB60167E4757D38669A68CBD6395CDB7B4C67C2152A4AAE8FBD12F419E5864C7AE5381CC08EBCBBBC0A2A5503838638FC738348AF1ED9BADABF43DFB24E708CADA92B92E0050678A7AE6EB0712E81EEAE40E200F4CA2F6660F174CFEAGF676E892158ED969965D07D032F621CF3353D8E02F225D27717DB057C36C5795E88F9A75DECD133DD72AACC49C1129FCF6381567E08FEEBF2973386C3B33C9650D6DE961F262683C5DE93B7CCC6EB57E207C073E1F153E7EAF6FECE232F860A017C5
	AC0CD96A673208AB8CA78B524BE5117D517FCF96EDF5F005A36D0A5C270AE393860B71EE876631BBFA43672C07AC78CAE8337C6E73C65CF7D32FB5877D12625BFA2DB968738DBC8775C15F0ADE1E132C9CC05FAA6F837F8310GA682BC8430240F5BF4FC600F64A6B9DFBD5C2F942FA0B9C302BB4F3D7A53D9BC565B4D7D19BC9E93FE563DFC401A4F2F698B5DFB0A254D663DB9E25E9ECB5B96881D47741767AC8A9FD318B2D1CE74BCF82259FC9E14832D4BGD1GF1GAC767C44152D531D9F60B166FA2DB946
	BC19F7BC6D3D88B1E62D4EDFC63FA937B76EFD6307B0C656C0DF9BC0GA08EA091E0854046FE2E23D57D1F5FCCC75472DA64EA4055C14C2E361E2B06CC3FFDF0EC3AD8B4E21ED59705832173187C1EA96472BB8A7DACFF9E24713C8B5AB1866E850ACB00F62201BB5F0038D550CE9E6039D9F5A31E7FA9C67B9CED235ED87B4586EBAE1AB2905E7F461E3B3DB1E20D3FE51E48F47EEF8311F337F58361F337B7864267EE9B8636B1BC50B05F5CF8FF4A406839DD46E0385CEEB75D77539F66D15A2AAA2A5488EB84
	CD507BDE55BEA0E1F23C86D607E1313A7860E6FD4CE9CD2EBEC6388F7F66F0043C20C9005317796FF02355FF715E33E3B0A96B50A0775FB71BF96EF0F210678E64A2464BG34E381668DF25BD9668AD7F7D8BC486CE74560C8BBE2FE3CF9984F23F5501FB11414358B6084C04B90775F5903B70D31278323445818214836B0F3A83CAD5CB704F90FAA7B9517E855F742D02151970C94751A580D2AE1E39E2173A19D6894C7584B82BA1F75C51A7F44D0A41BE2FADF9D22776306EFADAF8C2783ED0849BEFAE8248E
	983D3B9DE8EBEE31C370CBE07E32AAB67A74F2826AEE88757E9D870FC5F307F8BD719F7AB0F6F7C032896CB9FD220B4C3F0EE3DFF3F05BAA047691G87008490F31D6799DD8DC8C373E8925A73066BD8FF5DCAEB342C7F6D0FF10F67840F179AAD61B1719F9BB99F654306CDB6537A92342381920639CD7EE733D99E0900DFB69C14411A611172E03902C6EDFC43B04B95B69978A80A4F3D9144339CA20B6231DD8AFD51B7F88CFE191EFF6B211DE2606A5CBCD6E7C7F19B5FB69C59C68B07435B681E6170712AF4
	F874F8F5E4B812EDB17EDE25E74E91602DB20A736CA862BC57C5F17DAD217919A93C58AFFA3CEE398FEC9AABD81AEA2F29F7B6B5BACFDFE9EC38D26D0097F67EA774196B5B65B9FF8AFBC724E7E09453CF147EFCF24AFE5D86724E71DAC4A274A5C7A1EE4EB9F3DE0BBC4A700F9DF5C47B1F8D69C7995CA0667EF7C37A11572FB34D7D479A4C7D4873B4FDBE5B57B387F8DE13724CBCA2AD7AB4625D29FC9C5B4FE57D59543A1F145E794EA6C667792F4D72491FDD12169A01CE1CBEBFE11C350E7543C6EB9CD8B1
	AEE86B6B46C534FB6AC7A963C658BD456F98BF426E1B395D9F013E58715CDE36B8E9BD8F5A5993B8AE26055A9534CF9A38E7AFF03BF2CF60BEBBC0FDA99A5A89G0986FEB245631DF97984F6A7E6785C0B23554F100E391DB0E1645E934360C2F31683232EE14DDF425524B65DE4BE44BB9766E29AB8BA0B05C5B8C50F27D75CBC1EA6832F99931332CA81EAG5C9339BF7E2EB0F4FEE5F6F03E2B084F1F886363819281D682EC1C486FBB2FE5630442C9F07ECE62F1491979FBF8368AD775D377345B23498ABDF6
	81592448F0CAE87D8CCBABA03A3C14F3E37B7D63C71B388FAD7C4103A4F172EFD3A35D8DDAA6B11DE97AF3DD4BFED3CE614C417DBE84FD0B81568224G64CF065CE63211D7E4A20F18BDG7BD51AA6383AE86501A7952D9E31AF0846430D5126259DBD162E49BC90C9BEC0618C444646762BF04037881D044757979A399FD4CE66FE705CF96E87ED138DBF203EB5915AF381928D7CE40A47BB7BB240A59B785C6631541111AEFF1FB8B9548F661A5A3A9F0CE98D336D0F66AB4C0ECB1DD813EC1795FAE8C5CFC179
	FC6664761B8F6DC3G1781FCD3D86D024DFB2D9167C9C23F6C57783CC5ADFC5EF2E82F1B427633AB1A4DE33E3176829FF38270F5GDDG51G7322B96DBF1720521D417A0E4EEB99BD531A842D2B56A3CAEEE38F2F6FC59D1DB255C0D86DE5D0B41FA1CBF5BE1223F93EBD494D4F350CBB783976185BFA2EE1BEECA77CDCFA45EDBD573AC37A8BC46B3976E8C8FF3DE8BD571ECDE57D0C27BB0AB8CF1586CF0F970D4E53EBC5D61EFE9F523F33F8F41E4E95DBF97AC2BF4B753BDAB6620D9C06059E3D6F2DEC1C6B
	71FAB40FA0F986E8926B04015ECE663F93F231DF033D4482AC84D88A306A8E9E977AB76FA3B4AE040989275D1DA24FEF78FA17F3A267EE2E1C1B7F2EA76564D0FB87AB69C67A9C2F64205FE8320FA2992EAF87E525F8BA3BB4B6D447DF4FC19DC9E287C5B35BA7CD5C2EB75E91347D7CBBD91D66501D54F64E1B6F00614FA5319BED1FD9B14B4499FE3C9F6B630DFE4D67571CAEC516A46CE5FDB34B1077B38E21DD140E6975229B456635FCBA9FF3613960CEDE1BBC3BAF44BF74B16B6149728D1713576A485424
	2C72297C9E12E943B8EC3D6F5AD9902928B82D393AC477A9BD2468F67D28135B35EFAA3FC77CAB25BF8F5A0B81D6CE65B52B40C1FEAE4D7F56EC6208BA9BB637DB53F2BCADF9AA13730E18E0CEB64F7F36DE13FD2905FF1751847D51B11C7F4EFD867F78DD06273D5B6E6F6D368B6D1ADD04A325222241AFC98EBD349146EB7185243598E82C84C80E61B1A427156FABFB9A775772D62B3F7EA8445F2E36DA7D35B34D5CBF37556A2F9FE4187B1FEB357A6BA3BAFD363F8307F60551CFC5451996708D9ABA996F5F
	970DFA43B4FE2E5D699E63B798C537BA0F7952545C487EFCF76E4D7DF9CEAE7D9D837C30255F275F6793F271EC12BBBAD451089B8F67669B38FA35C9D6BD9829181D2CEE62F692GB9AC18466DF367F9442F03B6191E14D5B81D5B6755EB5CBEF7BDBA075ADC243B4751697AEF5F53037649F586F6F8FAF7F8BD859D5C701F470BF11F989B6226F37E72ED485FB2E82F85C8194E6D7BB79D916AD1B4C2B0FA637771FB7D0E99DC779E674DF46818912E5E5AF753F9567A2B7ECD41CD67D9015D0F4ED13F6E1A416E
	C729863E1562B5931E697717CE637BAC68DBBB0367605736221C6AE2A12F8798820883D88830BC161FCF95DB0E121B5443DAE41F838CD8B2DD7F755F92DA4666CBEF5E72BC367F69B4C62D0D354A65DE0ACF0E354A45F6014BA5857A5A6E62F61057BAB63E9F974D73165C95EAABAB213D7E2EF0763336E9EC6B7D47AD4FE37218C17D1A5CED15D3BC45675EED15D349F9AE27C2689B3A1B5B4FD3F9E8BFEB201D82E01B89E7A4008340BD135B4F44AD182BC53A53B54B3206EE8ED90EA6F83C22C2539D85DEB5FD
	0FCF96639E83C0AF86A081E0614C208C174E0CD4DB18C8E3432A19565F6526D37C7A19565F65BE347133BE857A5A6238ADCC5997CE46CB62583AAB63C62E4F6A690B37E3CC74F8A58F6E0EFEB1E5684D59C20C774B2677D71C376B5B1C204D7C6E362E41F42EED0DFC268DEE8DDFDF9C37B5FCFDF14A5651FF6F1831B57237AC33361A3FE5E17B0F3B9014777D26770397EE3F6C3F382DEB68F7FB1A532C873BCC0963777D4282FE5F4F3F0763BE25F55793502E3B075B7ABD6703727806697D237337DFB6A7EF6B
	9A4C9E1E4B0BEC17AF665DBC57DA1D977E6E34B6EF645D69353C70F727DF67196FCECC97BF23394DE55003E668E777CE44C5C3FB3E016B25384550DEE760E66DC1DC8A3473E7F15CCBFB383EED330C3C5606753A9A239DC75B170D76AE9B17BF0E39BDBA7E58D863F6FC2B47767E8D1A578D419EE28CF9246F6772081F45EBAC2F08387F8EB8F7586FCD6FD03FF9927A174E62351A328263CE8B385CFB13320EGB8G24FBB91D26C216FFFBB4B495C62B3310FF2F9EE30C7B77E24CDD853FA62BB439D5755F3952
	F67231F30D31A7F6625F9460C7E69E17A031E4781420B4491247552F4FEB24737EGB05F18B7EDA71E4BDD3262994073F444CC7E5B5AA3F4AE7EB142FAE36EF7284D74FA44BE1F2CE86571B8EE499E145111F8AE0F8DFB38BC06G978B30GE0B9C0F2BC277344B65C67B9D1D505CE31CA9185426E5ACF594CF7367C7BD8BD317CBEBEEF513E50FA72847AB799EC5D627DFCDD870CEF830882C8GD88C40462FC07755EB853985787794DE314FE6FC79D38B82B9342F8A9CDA5AF744F68C7EBFFE18703B450582FE
	07DDF39F3F43CEA8983D663428E0741A532B8556BB6C4BA1F75C22C26B9D1653E7FB98E675F5452F57B80AF60636BBF6626F6F6672036956E4EFBD5D5CD2DFDD6538622CEEEEF4B8CED5B5DF29F2DC2C7A335320BFF94F71D16657B5B657FF55585092C22161B4106573FF0ED79A3D0E7E39E87E8D6968E0EADABA89FF9FAC1C4DE24B2159565F580A77F29DDC1A4DF5D037572A0367D34DB29C58EB55C1E3088E666F356A603D10BA426A3DD69D703AA03BC7BEFB9DE3F604BA8235ED46FB0A0D7BF06CECDE97B9
	391F6FE729319F6FFE6BFEF607701B30FFF41BFAE13FF5BF9F0548A323543A1F477479AC7EFED5F02B77851667AFA9C05BA3F3F8CEDFD000B54BC8B9BDE63B2C824A575D3BF7EC771B541251666D18B3721CE273CE6F9FDBADE6E98156CDD41DF7BDF7AA61E74721B94639D0E244A3403581C481A4GAC1E4375FF214408A9F30C18D2E2557F1BA17ADDD4E255FFEAC87F2BA5D67DD705683FE81FD57FFC3E2E7FD165998E585F36CD13BF85E6673FCEB2B572E1F5CB3D8D3F9BEA97D451E6FC8B8D57B57DDDA3D9
	6C17880DECE7A87CBB8E1BFEA2282AC23604C465279E7878D74BD6372A69A42B1E9461016C9495DACB975D380E7E07A035E433CF06EB21937075A4C3250FCCD56B17C4875951510C9FC72B1A682E62B785D5A39F50AFBF4B7C1AC7AA2B9354AEFAC4FF66A099DE7C139572BE6B1684EFE799B3584F827807AA29D5A7515C893D74823F5BA93FC845EED0597E433C83A5E756A6261334DA12A349023BD6F0019354134DDA17A22ADD32648E783EE004327BF86DAA83486DB4360F1B3553D842193E9638BFE64B3AAD
	35F0D48494D2C6C76219E8B7C6382B3B8485169215FA8D3F191115D223EF5C9E6D6033DF96DCCEA3297AA6FB48B0498A78C0A79DC237C76A07179A102D5C29E2CF8EB47BC4CCD1012DBC3A988A43DE8B7B51FE7E7A538F7E22B2F9BD902BA51974A3D1E0A9BFB82A415F5DAEAA13675F337B3D38B5D311E39FEE17D35E8C1934D745CFF001384D32E75EE530EE1F797451FB9EF97BAB4AFAF02AC676B2A5415432CED16EAE2B6A68009B35C1GB48664F79B64F10C5DB2667D40DD9F1CF9715DF630309A125EFD74
	A87D77987D77B87EFB0CE20ED14C31632036E4B26AFF567EG5FB3B5CB5A5806B69440372D81424F4943B74C676446C905913FC549CD506B2589413AA90B455FD468DD817001849E0FFF5D03F9E0245F051D5D02A4C1B6F82D4A6B465F08CF75EBE2707E7C100274D5BEC66FE4FD0FAA4877D5531C7F83D0CB87889DFE5FA4E595GGC8BBGGD0CB818294G94G88G88GF7F954AC9DFE5FA4E595GGC8BBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGG
	G81G81GBAGGG1F95GGGG
**end of data**/
}
}

package com.cannontech.palm.test;

import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;  
//=========================================================================================
// This panel has a table on it that updates as the user enters addressing info 
//  Creation date: (5/31/2001 3:06:01 PM)
//  @author: Eric Schmit
//=========================================================================================

public class AltAddressPanel extends javax.swing.JPanel implements ActionListener {
	private javax.swing.JTable ivjJTableAddress = null;
	private AltAddressTableModel addyTableModel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JTextField ivjClassAddress = null;
	private javax.swing.JTextField ivjDivisionAddress = null;
	private javax.swing.JTextField ivjSectionAddress = null;
	private javax.swing.JTextField ivjTypeAddress = null;
	private javax.swing.JButton ivjAddAddress = null;
	private javax.swing.JButton ivjDeleteAddress = null;
	private static String thePath = null;
	private LCR_Address lcrAddress = null;
	private String nameThing = null;
	private int	sectionThing = 0;
	private int classThing = 0;
	private int divisionThing = 0;
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.JPanel ivjAddressEnterPanel = null;
	private javax.swing.JPanel ivjAddressEntryPanel = null;
	private javax.swing.JPanel ivjJPanel1 = null;
/**
 * AltAddressPanel constructor comment.
 */
public AltAddressPanel() 
{
	super();
	initialize();
}
/**
 * AltAddressPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public AltAddressPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * AltAddressPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public AltAddressPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * AltAddressPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public AltAddressPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:19:00 PM)
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e)
{
	if( e.getSource() == getAddAddress() )
	{
		AddAddress_ActionPerformed(e);
	}
	else if ( e.getSource() == getDeleteAddress() )
	{
		DelAddress_ActionPerformed(e);
	}
}
//=========================================================================================
//this adds the functionality to the buttons Add and Delete
//=========================================================================================

public void AddAddress_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	//get the input out of the fields
	nameThing = getTypeAddress().getText();
	sectionThing = Integer.parseInt(getSectionAddress().getText());
	classThing = Integer.parseInt(getClassAddress().getText());
	divisionThing = Integer.parseInt(getDivisionAddress().getText());

	//clear the fields
	getTypeAddress().setText(null);
	getSectionAddress().setText(null);
	getClassAddress().setText(null);
	getDivisionAddress().setText(null);

	Integer valid = doValidation(sectionThing, classThing, divisionThing);
	
	if(valid != null)
	{
		//make an instance of the lcr struct	
		lcrAddress = new LCR_Address(nameThing);

		//fill up the instance with the data we parsed
		lcrAddress.setAddressClass(classThing);
		lcrAddress.setAddressDivision(divisionThing);
		lcrAddress.setAddressSection(sectionThing);

		AltAddressTableModel addyModel =
			(AltAddressTableModel) getJTableAddress().getModel();
		addyModel.addRow(lcrAddress);
		System.out.println("Add_Performed_OK");
	} 
	else
	{
		popWarning();
		System.out.println("Invalid Address");
	}
}
//=========================================================================================
//this adds the functionality to the buttons Add and Delete
//=========================================================================================

public void DelAddress_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	AltAddressTableModel addyModel = (AltAddressTableModel)getJTableAddress().getModel();

	int rowToDie = getJTableAddress().getSelectedRow();
	addyModel.removeRow(rowToDie);	
	System.out.println ("Del_Performed_OK");
}
//=======================================================================================
//this is just a little data-checking so the palm won't barf when we upload the config
//=======================================================================================

public Integer doValidation(int sectionIn, int classIn, int divisionIn) 
{
	int tracker = 0;
	
	//make sure the addresses are in the proper ranges	
	if( (sectionIn >= 0) && (sectionIn <= 255) )
		tracker = tracker + 1;
	else
		System.out.println ("Section Out Of Range!");
		
	if( (classIn >= 0) && (classIn <= 16) )
		tracker = tracker + 1;
	else
		System.out.println ("Class Out Of Range");

	if( (divisionThing >= 0) && (divisionThing <= 16) )
		tracker = tracker + 1;
	else
		System.out.println ("Division Out Of Range");

	if(tracker == 3)
		return new Integer(1);
	else	
		return null;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddAddress() {
	if (ivjAddAddress == null) {
		try {
			ivjAddAddress = new javax.swing.JButton();
			ivjAddAddress.setName("AddAddress");
			ivjAddAddress.setText("Add");
			ivjAddAddress.setBounds(35, 20, 85, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddAddress;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressEnterPanel() {
	if (ivjAddressEnterPanel == null) {
		try {
			ivjAddressEnterPanel = new javax.swing.JPanel();
			ivjAddressEnterPanel.setName("AddressEnterPanel");
			ivjAddressEnterPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjAddressEnterPanel.setLayout(null);
			ivjAddressEnterPanel.setBounds(229, 352, 257, 66);
			getAddressEnterPanel().add(getAddAddress(), getAddAddress().getName());
			getAddressEnterPanel().add(getDeleteAddress(), getDeleteAddress().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressEnterPanel;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressEntryPanel() {
	if (ivjAddressEntryPanel == null) {
		try {
			ivjAddressEntryPanel = new javax.swing.JPanel();
			ivjAddressEntryPanel.setName("AddressEntryPanel");
			ivjAddressEntryPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjAddressEntryPanel.setLayout(null);
			ivjAddressEntryPanel.setBounds(60, 236, 561, 99);
			getAddressEntryPanel().add(getTypeAddress(), getTypeAddress().getName());
			getAddressEntryPanel().add(getSectionAddress(), getSectionAddress().getName());
			getAddressEntryPanel().add(getClassAddress(), getClassAddress().getName());
			getAddressEntryPanel().add(getDivisionAddress(), getDivisionAddress().getName());
			getAddressEntryPanel().add(getJLabel2(), getJLabel2().getName());
			getAddressEntryPanel().add(getJLabel3(), getJLabel3().getName());
			getAddressEntryPanel().add(getJLabel4(), getJLabel4().getName());
			getAddressEntryPanel().add(getJLabel5(), getJLabel5().getName());
			getAddressEntryPanel().add(getJLabel1(), getJLabel1().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressEntryPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (6/13/2001 2:56:02 PM)
 */
public AltAddressTableModel getAddyTable() 
{
	if(addyTableModel == null)
		addyTableModel = new AltAddressTableModel();
	return addyTableModel;
}
/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getClassAddress() {
	if (ivjClassAddress == null) {
		try {
			ivjClassAddress = new javax.swing.JTextField();
			ivjClassAddress.setName("ClassAddress");
			ivjClassAddress.setBounds(405, 53, 50, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClassAddress;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDeleteAddress() {
	if (ivjDeleteAddress == null) {
		try {
			ivjDeleteAddress = new javax.swing.JButton();
			ivjDeleteAddress.setName("DeleteAddress");
			ivjDeleteAddress.setText("Delete");
			ivjDeleteAddress.setBounds(143, 20, 85, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteAddress;
}
/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDivisionAddress() {
	if (ivjDivisionAddress == null) {
		try {
			ivjDivisionAddress = new javax.swing.JTextField();
			ivjDivisionAddress.setName("DivisionAddress");
			ivjDivisionAddress.setBounds(480, 53, 50, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDivisionAddress;
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
			ivjJLabel1.setText("Please enter addressing information, then hit \'add\'.");
			ivjJLabel1.setBounds(21, 8, 288, 14);
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
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Addressing Type");
			ivjJLabel2.setBounds(19, 33, 95, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel2;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel3() {
	if (ivjJLabel3 == null) {
		try {
			ivjJLabel3 = new javax.swing.JLabel();
			ivjJLabel3.setName("JLabel3");
			ivjJLabel3.setText("Section");
			ivjJLabel3.setBounds(330, 34, 50, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel3;
}
/**
 * Return the JLabel4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel4() {
	if (ivjJLabel4 == null) {
		try {
			ivjJLabel4 = new javax.swing.JLabel();
			ivjJLabel4.setName("JLabel4");
			ivjJLabel4.setText("Class");
			ivjJLabel4.setBounds(405, 35, 50, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel4;
}
/**
 * Return the JLabel5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel5() {
	if (ivjJLabel5 == null) {
		try {
			ivjJLabel5 = new javax.swing.JLabel();
			ivjJLabel5.setName("JLabel5");
			ivjJLabel5.setText("Division");
			ivjJLabel5.setBounds(480, 34, 50, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel5;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(null);
			ivjJPanel1.setBounds(54, 18, 571, 214);
			getJPanel1().add(getJScrollPane1(), getJScrollPane1().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPane1() {
	if (ivjJScrollPane1 == null) {
		try {
			ivjJScrollPane1 = new javax.swing.JScrollPane();
			ivjJScrollPane1.setName("JScrollPane1");
			ivjJScrollPane1.setAutoscrolls(true);
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPane1.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjJScrollPane1.setBounds(6, 3, 563, 209);
			getJScrollPane1().setViewportView(getJTableAddress());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane1;
}
/**
 * Return the JTableAddress property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableAddress() {
	if (ivjJTableAddress == null) {
		try {
			ivjJTableAddress = new javax.swing.JTable();
			ivjJTableAddress.setName("JTableAddress");
			getJScrollPane1().setColumnHeaderView(ivjJTableAddress.getTableHeader());
			getJScrollPane1().getViewport().setBackingStoreEnabled(true);
			ivjJTableAddress.setAutoResizeMode(0);
			ivjJTableAddress.setModel(new com.cannontech.palm.test.AltAddressTableModel());
			ivjJTableAddress.setBorder(new javax.swing.border.EtchedBorder());
			ivjJTableAddress.setShowVerticalLines(false);
			ivjJTableAddress.setShowHorizontalLines(false);
			ivjJTableAddress.setRowMargin(2);
			ivjJTableAddress.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJTableAddress.setPreferredSize(new java.awt.Dimension(545,190));
			ivjJTableAddress.setBounds(0, 0, 545, 190);
			ivjJTableAddress.setRowHeight(20);
			// user code begin {1}

			ivjJTableAddress.setModel(new AltAddressTableModel());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableAddress;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getSectionAddress() {
	if (ivjSectionAddress == null) {
		try {
			ivjSectionAddress = new javax.swing.JTextField();
			ivjSectionAddress.setName("SectionAddress");
			ivjSectionAddress.setBounds(330, 53, 50, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSectionAddress;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getTypeAddress() {
	if (ivjTypeAddress == null) {
		try {
			ivjTypeAddress = new javax.swing.JTextField();
			ivjTypeAddress.setName("TypeAddress");
			ivjTypeAddress.setBounds(17, 53, 285, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTypeAddress;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}
/**
 * Insert the method's description here.
 * Creation date: (6/1/2001 10:44:04 AM)
 */
public void initConnections() 
{
	getAddAddress().addActionListener(this);
	getDeleteAddress().addActionListener(this);
	getTypeAddress().addActionListener(this);
	getSectionAddress().addActionListener(this);
	getClassAddress().addActionListener(this);
	getDivisionAddress().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("AddressPanel");
		setFont(new java.awt.Font("Arial", 1, 14));
		setLayout(null);
		setSize(679, 466);
		add(getJPanel1(), getJPanel1().getName());
		add(getAddressEnterPanel(), getAddressEnterPanel().getName());
		add(getAddressEntryPanel(), getAddressEntryPanel().getName());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	addyTableModel = new AltAddressTableModel();
	initConnections();
	getTypeAddress().requestFocus();
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		AltAddressPanel aAltAddressPanel;
		aAltAddressPanel = new AltAddressPanel();
		frame.setContentPane(aAltAddressPanel);
		frame.setSize(aAltAddressPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/8/2001 3:58:46 PM)
 */
public void popWarning() 
{
	javax.swing.JFrame box 			= new javax.swing.JFrame("About");
	javax.swing.JOptionPane warning	= new  javax.swing.JOptionPane();

	box.setResizable(false);
	box.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		
	warning.showMessageDialog(box,   "Invalid Address");
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 3:05:54 PM)
 */
public void saveFile()
{
	FileOutputStream addressFile = null;
	String bigString;
	String tackOnString;
	byte outPutBuffer[];
	int stringSize = 0;
	int index;
	int count = 0;
	Vector outPutStrings;
	
	//eh?
	AltAddressTableModel addyModel = (AltAddressTableModel) getJTableAddress().getModel();
	
	outPutStrings 	= new Vector();
	outPutBuffer 	= new byte[1024];
	bigString 		= new String();
	tackOnString 	= "\r\n";

	//this is where the file is created
	try
	{
		addressFile = new FileOutputStream(thePath);
	}
	catch (IOException e)
	{
		System.out.println("Output Error!");
	}

	if (addressFile != null)
	{
		outPutStrings.addElement("AddyName=");
		outPutStrings.addElement("AddySection=");
		outPutStrings.addElement("AddyClass=");
		outPutStrings.addElement("AddyDivision=");
		
		for(index = 0; index < addyModel.getRows().size(); index++)
		{
			LCR_Address storedAddress = new LCR_Address();
			storedAddress = (LCR_Address)addyModel.getRowAt(index);

			bigString = bigString + outPutStrings.elementAt(0) + (String)storedAddress.getAddressName() + tackOnString;
			bigString = bigString + outPutStrings.elementAt(1) + (int)storedAddress.getAddressSection() + tackOnString;
			bigString = bigString + outPutStrings.elementAt(2) + (int)storedAddress.getAddressClass() + tackOnString;
			bigString = bigString + outPutStrings.elementAt(3) + (int)storedAddress.getAddressDivision() + tackOnString;
		}
		
		//write the file up...
		try
		{
			outPutBuffer = bigString.getBytes();
			addressFile.write(outPutBuffer);
		} 
		catch (IOException e)
		{
			System.out.println("Bad Stuff Happened...");
		}
	}
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2001 3:09:20 PM)
 */
public void setFilePath(String newPath)
{
	thePath = newPath;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G76F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDC8DDC945735A75598B498CD0209DACD50202111440F10BE525897C3C8E32ACDECEBFFB1AD6F45765116761914575856A6B69D3E9664C3D031B1ADAA71A30AA222E26387C6C0C4D122D409B1A92F3230833BB03B336C4E42A2886F5CFB675C1D9DF79765057873B0F37F671CFB4F3D67FE1E19D9A1FFEBD8DAD2F23EA0A425896A3F0F1284A123C190EC7302BFC2ACC80CB1881AFF9FG2D93E2572442
	F5BC7066C4479872042F57E4C0BA9C520DD1B1067760FE031067083CGB7FE4A99893C17277D6C0E4065FC3A8D4BC92769308B59F0DD8B948198392E94C9795197B62978E295C7CACA9604A52DC46607978AD46C88246B01C20042DA19FCB4DC5750126FBCF6C415EB3CB7CCD8795DDD49B9A49FCD1EC81B5A18EE39FAAD94E6A745371132D6D3BB090E8269C8A0D2799461D0CD89112F6FE87B38C536163498EDB6592608AD66923BD13216A822D3B9F62CD2D2AACDA60768F4569BED22A488CDE612E775458C1A
	07A06C06748A0A2D4AA36504437DFC20259CFFF69D41CBADB10633C0CD962C5B09DD652A6DD7FBE78B2BDF3525647A291B5B426A94E67154AD5475F67A7B548F933613760E831E509EE34887BA82F495488D74B76A7FA95B76403532D9B9A5DB2D322D52EE2F12CC0ECFE413A841BDD433D9D131CBE20B6CB0898273F17245E29F7684A22617F75BFA2CCDA28E213FA3AF4D94621EBC1F1C6E23CD62A2CA134737333608E8773489AB3B56EF598B5B3D7B23F7591F895A32D70C12D352FC149D75232264B8B51FC4
	CD59059D44579275F59E5CA7297D0361DD94B7E9F056B75F25FD06249560AD6B403EF1F5AD5AD217BAD948BBD067339DA646D4A617CB4C0686C9EF4B2175A46FA009151944EDA927F80886E7363C308E4749C4602D86E27D6B211DE42CB6D9E38CFDC021C011C00B019200D6DB310F7DFC5B7EDB6CE375C607D26532C8A651A1B05D07367F852E22DBF198EBEDCE45E8EB912F989D96E333A4BA85D566516D248D1A3D74A2E95FEF01B24E08B651E1D4AC328DF2B7DAC5C5F4B8194CD35B57919907C8398D96AB8C
	EC2355AEE03E5F5E3E812E125129FCEAB7999551A34178AFECA7BE69B2DE019CA18338E76D3268C352DE853698C399D0358D5B21E287496F3C680079625831BAD96EF0599BC8CD04D09BE967A6BAF7DC05FB3B8D5B7845DD84DB8669C41ECF770E751AFC2AADAD44FC2323874718E39D190FD3E56CB3EFDE45BED31DB3C3A87E51B91FFD06D0014C7ACA192C9FBBFDB4CFE297CB272E3B2A51CB15FD0F1F2192ABE333D799A9DFE27EEB392F8E9791DFB5027DEE2009B236713C9D052C4FD6991DA276520B8E68E0
	224953624C4E23449D2A4DD51A7BC5DE76FF59444A089F56B2D8DBC46CA46B0F58A55A14EA51AE5ACCF063D4F933F712BE260E4FD6196BBCC5F1BB0C67D3B0001C9CFFE6A799F3C6C5F1D81ADD0AF8D9D4940B2D4D43FF1E1665F4B55BBDD38116755D1DE45E153BE1BCF379D7F61286EF11EDB611BA052B287C6F5331A8CA22950CBEA53BEC8A43DF7D1074678E31C71D8BFE72A15958F499A59719D758BEE00EB10B5441E131022B49548489B2CE3BAFEE5234519C68B3E4BF33942E89FC4CF5EF47B137637AB4
	E141351D493E560C09B32A134F5A5958E934FB469C33E971AE5F367F004E89AA6ECC21D8C1AE11556EC8D87F366DA1F89F64BD9EA8420E7D7B42EE4247B9C46D5827E4C9E24D485B77F8D19E7107586992E16AA51355F95EE6D5C56BF53CABEA7BB17E39A252FE12E61EE36D7AD691E9BF3A3FEAB7F6995DA54EEE6883470E1D23F633FD54CF69DCC87A7882BB6E17F21BC9DDD6919FF44698F6F7E2DBFFBF8777CB219DE14272C77E6AF7AFD851495A585429D993C760FEA9FAAF5D9F83AF2A935B6905A26D1CDD
	69F44AAD963ADC28F5D476E8FD41301EBD19F0ED35B81CCAAD8C96B73647409EE2AFD9CFD4D89D433F705A17AC043263BABD73E9E2674DF3ABDB2FC75076C8F53075BA1D6363A8DE204159BA3E1162A4DD863C098EDCE3FED7CC7C319A521B1CB106D22033C0E620E0A736C37A5EC342A0EBF8AD6C119DB623F459EB7062DA3AB7050DB7B57D5C5EB49CEF44EFF47D7B9375F9B81499854430A35788369052F19C0BEBA21820003D8A2EC723E95D4AA1DD8FE46778840A0F07F49450B20E7F2DB9503CC972710557
	7B41116CAA1F15D196308CFF6BD9DAG9D4C5BB3B73FDB6CFFEECE5B67FBEE4E5A67FBEEDE37EF9D43A18F6DCE8C796F6F8BBCF7FF304F575C4D66277CFAB25F3642B96A080B78F0CFBD7161EAE82F8CC8B3190B578CF4FC7448AE18EA17D33958EBB85776C1BA54455AF7210B6D1158FCB2371B4CB32D303983F3AE5B4596E1FE9751FD2B89F5F7C33A9C2891288FE8CA9716D9E1A36D49E61E832A2D130E13FA2AF369E9522B55A31E4AAFBF694DA70B83633375E1E133FEFDA03626F6C3FF85E2B2D62BAF19D2
	60D7F319AF3AFC49B4825F8E94CC640429E419D16DAA3EC64E63B6313B10CC2BA546EE2524869AFC7736CA58B0CB5B0A8B8F407F32A2814FB5E15D5AB374114C0FFC2ED10496F733F6DF2EDE675F35DD1DD3D6DC433341A6B70CBDB77A24ED5BCEC1F30636B4DBCBDCDD5692E333D2A21AACF0FE3E601224BA5555821B5F0B1AC8DE4110C798D0349B67F77707B83FC74E1CAC54851D77B93FAFEFB8183C442D469B5C1E79BD824E43ECCDF95F18216A1576407C56C330B346FC5D181C81FFCA604A78BDDEFC6277
	B30216BF4B0BCF6C3D1826657F07971F344D672ABE2B5B221DD837550BA284692923FE572EB9BD2CCECB55EB7D156D420EB16F28F57BE6CB061AFF76F5DCAF4F3712762C06F4A3108244E4E7D8F1ED8D61324FF79259D8C847812D643277F521ECF6AF4A3EFE1D668B69C6A025977D744ABF357DA9EF728749C9FE6A92524B6AB0315763A73696B90E117296831E500BEB4F9BC77642750A31DDDDF5CEC387129DF45981305EA15BC5075243D03A83D1675F5FDE9BDA0CC2E83ADDBD366ECE257DB723CFFF2E1EC9
	717CBE7D39FAFF930E1DC2600D6C4375781163E45CA7C0BA75C60C21G288C288148FE835B646FC7CB05C156638659DE878B0324194AD97D3698079603F9AE9D7BEA5437268B51237E181465473FA3EFB03F062B5704066D94E7757B7B89D23F2540CB844A6F0FB19483D5GDD6D477A759FADE675A3EE2DD494E30B196E3030F2D7ACE23787A613BE2B5BB3AD7A3AC97D1EB5BB285F5FDE6AD9BAD6C27B75711917A89E5E2F0F4F7C239563B3D1405BB4007B0A472C5A727BG9FBF60A9B3FC40DF799FB9C8FE51
	832C1CB40ED7D1FC3186E7653FC447ABC9AF87DEF590167FE017367C88406787F94ADC92642F7C5EDE12DFFC90ABE7834747D2BC7F8E8F4E4A7FB91D83C83A9AF84B6F403E7B79472427B09F469EA5EB004FF18B9EBC87EB544566F6B8EFB4985B38CE77B1E2935FE330612C727A0F113954E233A8301CD92E53ED78E5903435313ABF52A470B19C2939BF5CB47C67708B43DA069A53BA6EFF2F7764F15FFB3D270F7B5E6BBDFBBC70DE6E45637E4F61AF9F571E43195FDBCF925F74FD8D4E95C0730116G45FF8D
	47F3E379B6010EE79FE3793429CDBCA553681E02651FBC613F2EB5A7860F89DEBCC14EE04D322248D612BF621F1EA07ED2E4BB859976458952C68ECB1BD9E122AA5EF4022CC51258CAE1F6767881DDE70591B0EF0D407348A6914FA3C7C660B8FC21156CF79BF97A951AD6F87A3356AF233F107C7D77C97B17D2063FF38A2B7BF3F4AFB1916C08646DD1A193EC892463B9B64A0EED14B1925B600403E045102E6658D90AB5C1BA08E32FF45ECEBDC8DE017845BABEABA361364A986C0C26EFB35686CFF4E1DB050D
	44F6596D46360A6558749EEC2B845E86F7746E00EB761DB1069DC095C0D7019420E020A9C0F300E26FC4797B297CB2C82F825AB48A7AA650D5A0B750C420E820E523D07E3F2F0F5758CB7271D7379D81F8C8952ABD442EEC55CE64DDD5ED3E15F6E6362D6F555A36617F6197CF1E02102B7AA9CB43A373C04EAD7681B6C758CE7A1F27DCA7871F277ACF5278914CAF8DBDF611E1A3CE12F3305C5A6A9479FC98FCB20FE3354EFA59E9A12B9FBB777D2A6566F333E04233632AD11EF3F35EDD309F01FE454E477BC5
	FEBE863C9C2891288FE84ADDF8BEDE14CA5679F317DB9C32A411E36E81F6B6955A34E75379EA3E89F0DD4DF517D909DD4D3203869118DD3F61675E7CE0587781B581A941ACBE4374BE13EF2E0FCDC63D10E0CFFD42D5BDD61F29F614198F789220C4203C51F02E9E0DF94F2231B9B83B2B47F7BCBB6F7D14783DDA14C4C564ACA1E4B449773AC3FB062B9F4D4AF4431571C54597FFA9DC196D2F72F3FC42E8D6E7E18C2C9FE3D8BC0A497C430DB245E3D87D2A61FAD515E175FB3A87E56C000F848A870A850A9F03
	757B201974E7521730968CBF733FC46FB20B6E6A6AFE5ECDFCCC7787AA07616F74123EDDED6932B8B5CA0C77638A9AB729B3B60B52C195DBFD42039DD231759BBCD8190AA5E6F93043AA76C23687BB20367B62DEED3B265E4D5A2278EEEC77CBB7FC71ABB8FFD93FAFFE83677FCB7045B7F3FEFB3A165F277243B87F9FEBFC7123B87FC816AF7EC24E5F1A630B9F477906DCDF7CC43832BD78EE931E9FD3C3E07C066079DC2438826990207090BC4BFFAAE27CE3FE8846BFC6087A784763C95A7845D322BE7EC176
	749E7EAFC5FD7C23BBD54BFF3FD59F7F302A7AEA1C6874E6416733G4F0CA3603B95C75AB254B2D6723A7E3A1D5435F8AC0C9720463158B66F597873F340A247E2DCAA2DF0270F72CA58A4F56C58E92545AC1A2A54A9CBAD771A8D5BADFEAC16BB1666DFF08F1C4D016A6E41F29F1051A7A640265C0372BF2478D21286CA9D07BEF95E0EF11B02F16813F76CFA1F5C6955667BBA75BEF9AED94B3F562977C90A977F5ECE3DCF96A5E9FD12FB79FD5FBE29F241F65A465BE52E83EDEF9807B1209BB55AE7B173F38F
	A67B0B91040CE3F3F864B8CF8C2818476A26A818770AF15836FF6EC21F6E988F3E9E0FEDF8269B7D93BA9E5B5B55CD4F4D100E87DABD9E7368F123FF726F853F5D0BF9BC6746BCC65E0BF97C064A4E0774D202F15957FB5036547B50E76FDD577B6CC5AF1F55DE577B6CB52FF156FBDD6F33962FF17478F53D4FFA3C46514E5D853EFD56G7B0F9A0BA819186DA9FD78FED279FDD81F51B790D3B816BB00D8A447A68808455F0763692DD6BF4F7268CC4E74262573BEFEBF66953C06779D0E49D90805F06CCA8EE2
	319CBB110B586A7BF17FDAE29ADA2C2F7C3675D8DC64B5BA4716056AE3FDBF23F8C528BE565741676ABA604D89C53B3F9007E67709B62217BFA146309B28FC0277FE3AFE023FB84F3FBA687BC69374F1AEB34515897AB8D7E2BB0E391140CB1800F6EF368DE6F75D833E4ED9FB87555B62333E26C175FC57B7094E3B668774FE4A243872005ECF775B50CFC1400BFFG6BBBC59E1A1F9E3BEDBDE65F99BB31636C03FA3B6BA95E70205E6E57783A53843C5887516E05F6ED793BA7C2DF1998786C45743E6970E5F7
	3D2AEB7211870B837D25124CD3EE600782C582458245CF44B850160AC11FA31FF67BFC0E4C6C1A267832ABF59233E753241B6DE2FAE95DC36B2F9B8655735DDF6B8655736D77DF51F52BF8125E6F3F257811C9FA3F77F6235F2B0197B1897DBE609EDAFF9DBBC43D6F5C369E2BD7CB8F31BFFB323E3E9245B7CD565777F7FC7D5E813C6049D85F789EED79ABG973EFEAB7DBC23AF105D79BE72E0FA775C8824375B2FDE7EC0A03DF23FFA1305C0FA75FE75E6267B52B32972EE3FFAE35778528BD165437C6AA9D9
	3E7422D47905FE757E1963CBAFCE15CF742BD7156BF3BE18224E87D37C693DEE9278FD02663E53B47C4F8A468CEB99AC0658C04FCA45D07F6AA998D7FC08E2D7A15D4731BFD0ECBC2423B976EF960CBFAE1E02634B54C1625709BCED23696C29187EFD7B6D54A95107DF86EF375C0E5BA963F65F975DCCF596ECA0F1A00BCD325844D36C6DE1DEFE3A6C69A7F91A7BC936616FB3733E02B2BE31FB6AF4C5F37F3ABC7CE52F9A56B2D83FFE1D1E35CB214F1273B04362A9F69630BB395C776839568418B2954744C1
	A70E092029FCAFD369BF763B33527773383D153E1F479D288C7C6E5551CAFF4F6B18FD51743C9C8E36C5F11BB73B506685FC1C7E265B6357D51A7BBB3A075F47133F02B2ACBD1EBAB9B4776F38073F6C7C1E616F436F74E09FA67181068957318F3F48653676216F63F9FF18F103BF03FB88313D830895F3EC2E00D8BD473E1D4E47844736E470B1413170B5A8974E3134CC141B43313B33D0EEA947D6E4215CF20EB9F2D0AE75E144FE1803F2F99C7BBC97654AB8762DDC142BF89863639DEDA49EE5F4A9321386
	981D82FB9FF1C49B46B6CCDC7629B6720DDC1768D0ACADF09C2172D5C6C73DACD9DAFAD43DA5D4EF9FA9066B2DB5133310D9F6D82E4BB665E6CDC1555DE3A63AA701229E46987F4B960C712F84ACA38C4E77C0E7014CE118FF59DF499AF50E3ED26DF9D6411E4BBC5C21FDAE938CBA6419F3A4DC9704E14C46C85F09D75F23784CE2D24C8CAFF067F87033A2F99EAE60B302F887FDFE8D939AFB6FF0D695A91259DC6B44B217C199AB003227C1FF822A1B06B63F22F86B3FFC4A23DF66C2FD89640300420096GAD
	1F066D7277BE149126479872014A009A001469A8B37F864A048296813490A881A8639114299A604F3CGAB87EA846A831A42E5968A7CB992E0CB0092017242E38C2561785E651F2ABD769F4EC079FA601B00C682058345F279429A0FFCC1A64A4783FF95502699B106A3C0D7E7207C05B39E7914EC14F783BF84A892E8B1508AAE5FF956A37F6B9C144F1E99E3A8842AGEA828A1A09722F55FA643F1F0B72E1400F82DA8A3492A87FD1745F64B66ABF07CBE469DF369D002BB6042E095817DCB9FD292176D4E55D
	354B27AEDD2C2B2B2A3CF42D326E334A4FAF737E742865E0G7D3397AF55FEF171C203D78E97CEC33698FBDEE941F8ED592398035D562E0F41BE6295E3EDE85747E0493C6D610FEA5747E05B3CE230516D7A98EC0E200D4136777A090552114A6CFE3503583D117475C7B14EBCDBA263F18924930004889E8FF5E29DB7C5E09D27BA75F5BC64D507D71C7ABA96F9711394FD9DE7F9718FA97ABA627B2E4C26AFDC98FB2D67B65D678AECD32CAB30CD3FF78536E9F7175E262DC9DA1B9A7B301D0239CDE37A74B6F9
	17B93FCFEFD307977F4DBE3DCD473D1E21945E505B345D2B1DFEF683EDDA40ED4A6B8FECD3D5FFE01B14FE3DCD2297FFE63F5E268949DA1B4C83E853AAEE53838301EDDAB89058263787825BD4AC68ED5A1B223569CD013F137D8D3429A0A930CD9712825BE4C88FEC5311F43DCD4FF959744F8C34494CED9A1D2137E933D71E4FE468EDBA6545FFA3C3EF5333DE7C2DEB74B66D726ACFD519E8D39837491E9958266919FA1BCEFB7117E686EEA7C3D6607ED4140DB6AD63B6FD169D58265159FA1B3C5F27FFA6BB
	30CDEFE4876ECFF9EB51263C4750260F578636493EB6F0BBCDDF9B582625EB83772764BC3469AC3729B4AF30CD4DF9FA1B56F971C37374B6D5FA4DCF5F493B291DD4FDF6D67923E5E8F15EB89A038902FA0482C5BCF6F39CE7BEE0CBF4F8BC24D781E54F726005F0DFB64BDB0E45FE37DB69330FD97A18F0B1459BE669E342CFC97C598770E2E761D9D7F18E2D0E57DCDE4FCCA2E11F98F9AB31E4E340D832FF3DDF860CA57B5733860CA57B57FBABE0AC593FDED34650744EE48ECD2FA4FBE8FA9B578ECDAFBD6F
	F67558B973DD3A272B0FE47D12E18592FDF7983020C8BC0FFE1D3EAB9B8E69E80E7D153EAB9B8769559CEB23D85E63F05EF89C31A80AD5C33A11E36F4A783D241DE311DB88B69252E19C134CF88E0E6258D83AFFDE86694447F13C9411D7F35436B82839FFC69A7E584952E1AD0355394B053113D99C6BF5E16C046CD95959E423C4F4C80C60126834DC974953C9649D1268BBCC64F925006FA61E13707C147AC40C21G2891488EC46263CCEF1E0FF7A1DF626FC286BF415E8B8C03EB14175E81194C7BCE335CFD
	C50DC354D9EC6475D1B506F0DE4635E571932853AFEFD0F54E72988453E2BA93394EF22E33C8266F404B5D1F989DED969B4F7F3FE45C6F2F84D9066D22B1535459F0BF9B751F5FC24EC2F60758AABA9C2269B2B49B4FA30E76498A102D1B0DE76C91E6FE2685AC84A892E8B1508A1EE738C5350947A2D8FEDF23E30B6C89336760FBBD8E895FD3A8G2CE18E46BAE24F933F5E72EF7950139AFD9A2D2E5D5F12302DC26660F9AD0116958B69B820155C067F31228D59F3E38C55F3719CF7143FBF56B79773E8236F
	99C4C1FAB1D07CDC2CF333CC6CEEB5CACE11774759BE3037E5129FB9C33373FCD01D40F9E1DBEE61BB5003C5DC7711CFBC3AC673D63DEE3545AA5A489B203B8F179DAEBEF03434C8ED1BEF1831DE2B67623E66D533FEDFF3436B8C3E46225F5790FD8F7F38C53F2FB965F58637DA747B9A54D73F5D1512C93B14623C621606765CFF5420FA3E1F7BBFBB281E1E585E2611761D32F97ABD0FC8710AF97ABD4F1BFC4FD3873CB973F08E7F03F5E875CD9B62FBB99F8F713D1C1F507E1F7F243E3E7194AFFCD2DFDFAB
	FFAF2798F8631F447ABEE19E5ADA5F2CD95FBAB477AB3F0235AE47BA7CE57C546629532FB5774E2F60B9E248301641FE372EB33D86660548B704B427C348711A67DE18F678970B7F730AB3C5B0548A6F131F9C38ACBA68970322295A28604FB55588EB6C32851800578A29CEFAC9F3AABD12D8A76437DEA2BFEB62D4C4D3A5BEF3F3AA4246BAD252FE17E211761FB5BA4D8AD9AE0E578929B67243D242FA46160C36367D6C434463EE729CB5293209F8001A1F8476EE207612DCCAF8AE9B1E495D794139786894A1
	39C64814E42329465822480EDAE10DE2062549ACCBA6377DE4854DA8033E71CA1EE424C2F6FF6355A715ADA1EFFCF223BB5CD3C3F292F2EAE0D6F4BB047DD412C461CB3804691459680022C4C72DC2FEA8CBF66C603CBB36A89FFE6F4CFB0D49C212DA49CE21DFC8F65B954150EA34DA249E38290636155B1C04E300E4B7DD2C412CDCDA98E90C129A280F7263B7DE1A7E1372449510D90D10C6FF7A83CC4A73C8DDF0D91BC5472848891B33C2175FCDAC36136AE24EEB94075166EC159DD6483CD0D7E7E4F153CB
	6EFF29F442B75E7E021A6ED1D5042D4CC9203A3FCD142D7BABDBDBE1EF44B3G0FC1761BF976C426C4A7335EFD6F46F3BF7AF3B37430EAA145DADACA7F9E20FF8F123F87A8F200A2878E025B9205007F74FCB75619F6CB1A48A1FD48CD6EB25D0A10827B95527474FB95C548D537AF50638A49379F0A102EFE6A2188E974C133A2E460079C0AD0D6AF09C627D8A8125F36A8B4FAF4ADB6D2BD7AE1EDF121E296ED05E60BD2388BA4F615D0AB845A1BD9A72493B2C5D3EF6E16844E601D032432481FDC72A70FAA2D
	27FFB75EF653902D7754A629D0535B5D603375B76FD8AA6FF96D5548EB557949C2F22DE0B0531E89033C1BBC7322A3735F85A179837C3E3DE8C07B7B852B6E5C647777B0D6C633B5B463E97D6F97BCC363F45500B7BD0D6F89E4E51267037E3E033EECB5CA926C50BBAAEDA672CDF4D50FA2FA5E8BD810C972F7220C4AD43F514DA4F637EBF47E8FD0CB8788A73BC84A6E9AGGB0D4GGD0CB818294G94G88G88G76F854ACA73BC84A6E9AGGB0D4GG8CGGGGGGGGGGGGGGGGGE2F5
	E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGA89BGGGG
**end of data**/
}
}

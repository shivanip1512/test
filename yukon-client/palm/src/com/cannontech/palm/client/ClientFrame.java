package com.cannontech.palm.client;

import com.cannontech.palm.test.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.event.*;  
//==============================================================================================
//Second attempt at an app that builds the files the conduit looks for to config the Palm-RSU
//Creation date: (6/14/2001 10:18:16 AM)
// @author: Eric Schmit
//==============================================================================================
public class ClientFrame extends javax.swing.JFrame implements ActionListener, MouseListener {
	private javax.swing.JPanel ivjClientFrameContentPane = null;
	private javax.swing.JMenuBar ivjClientFrameJMenuBar = null;
	private javax.swing.JMenuItem ivjMenu_About = null;
	private javax.swing.JMenuItem ivjMenu_Exit = null;
	private javax.swing.JMenu ivjMenu_File = null;
	private javax.swing.JMenuItem ivjMenu_Load = null;
	private javax.swing.JMenuItem ivjMenu_Save = null;
	private javax.swing.JPanel ivjAddressPanel = null;
	private javax.swing.ButtonGroup swapGroup = null;
	private javax.swing.ButtonGroup shedGroup = null;
	private javax.swing.ButtonGroup configGroup = null;
	private javax.swing.ButtonGroup advancedGroup = null;
	private javax.swing.ButtonGroup saveGroup = null;
	private final String CONFIG_FILENAME = "prsu.cfg";
	private final String ADDRESS_FILENAME = "prsu.ady";
	private String thePath = null;
	private LCR_Address lcrAddress = null;
	private String nameThing = null;
	private int	sectionThing = 0;
	private int classThing = 0;
	private int divisionThing = 0;
	private String theWarning = null;
	private	boolean isFreqOk = false;
	private boolean isCapOk = false;
	private boolean goodPairs = false;
	private javax.swing.JTabbedPane ivjClientTabbedPane = null;
	private javax.swing.JPanel ivjConfigPanel = null;
	private javax.swing.JButton ivjAddButton = null;
	private javax.swing.JPanel ivjButtonPanel = null;
	private javax.swing.JButton ivjDeleteButton = null;
	private javax.swing.JPanel ivjEntryPanel = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JTable ivjAddressTable = null;
	private javax.swing.JTextField ivjAddressClassField = null;
	private javax.swing.JTextField ivjAddressDivisionField = null;
	private javax.swing.JTextField ivjAddressNameField = null;
	private javax.swing.JTextField ivjAddressSectionField = null;
	private javax.swing.JPanel ivjCommandPanel = null;
	private javax.swing.JLabel ivjJLabel6 = null;
	private javax.swing.JLabel ivjJLabel7 = null;
	private javax.swing.JLabel ivjJLabel9 = null;
	private javax.swing.JLabel ivjJLabel10 = null;
	private javax.swing.JLabel ivjJLabel11 = null;
	private javax.swing.JLabel ivjJLabel12 = null;
	private javax.swing.JLabel ivjJLabel13 = null;
	private javax.swing.JLabel ivjJLabel14 = null;
	private javax.swing.JLabel ivjJLabel15 = null;
	private javax.swing.JLabel ivjJLabel16 = null;
	private javax.swing.JLabel ivjJLabel17 = null;
	private javax.swing.JLabel ivjJLabel18 = null;
	private javax.swing.JLabel ivjJLabel19 = null;
	private javax.swing.JLabel ivjJLabel20 = null;
	private javax.swing.JTextField ivjEmailField = null;
	private javax.swing.JTextField ivjPagingCapCodeOneField = null;
	private javax.swing.JTextField ivjPagingCapCodeThreeField = null;
	private javax.swing.JTextField ivjPagingCapCodeTwoField = null;
	private javax.swing.JTextField ivjPagingFrequencyOneField = null;
	private javax.swing.JTextField ivjPagingFrequencyThreeField = null;
	private javax.swing.JTextField ivjPagingFrequencyTwoField = null;
	private javax.swing.JTextField ivjPagingNameOneField = null;
	private javax.swing.JTextField ivjPagingNameThreeField = null;
	private javax.swing.JTextField ivjPagingNameTwoField = null;
	private javax.swing.JRadioButton ivjAdvancedNoRadio = null;
	private javax.swing.JRadioButton ivjConfigNoRadio = null;
	private javax.swing.JRadioButton ivjConfigYesRadio = null;
	private javax.swing.JRadioButton ivjSaveAutoRadio = null;
	private javax.swing.JRadioButton ivjSaveQuietRadio = null;
	private javax.swing.JRadioButton ivjSaveVerifyRadio = null;
	private javax.swing.JRadioButton ivjShedNoRadio = null;
	private javax.swing.JRadioButton ivjShedYesRadio = null;
	private javax.swing.JRadioButton ivjSwapNoRadio = null;
	private javax.swing.JRadioButton ivjSwapYesRadio = null;
	private javax.swing.JScrollPane ivjAddressScrollPane = null;
	private javax.swing.JPanel ivjPagingPanel = null;
	private javax.swing.JRadioButton ivjAdvancedYesRadio = null;
	private javax.swing.JLabel ivjAdvancedLabel = null;
	private javax.swing.JMenu ivjMenu_Help = null;
/**
 * ClientFrame constructor comment.
 */
public ClientFrame() 
{
	super();
	initialize();
}
/**
 * ClientFrame constructor comment.
 * @param title java.lang.String
 */
public ClientFrame(String title) {
	super(title);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getConfigYesRadio()) 
		connEtoC1();
	if (e.getSource() == getConfigNoRadio()) 
		connEtoC2();
	if (e.getSource() == getAddButton()) 
		connEtoC3();
	if (e.getSource() == getDeleteButton()) 
		connEtoC4();
	if (e.getSource() == getMenu_Save()) 
		connEtoC5(e);
	if (e.getSource() == getMenu_Load()) 
		connEtoC6(e);
	if (e.getSource() == getMenu_Exit()) 
		connEtoC7(e);
	if (e.getSource() == getMenu_About()) 
		connEtoC8(e);
	// user code begin {2}
	// user code end
}
//=============================================================================================
//this is where we're pulling addresses off the fields and slapping them into the table
//we also do some testing to see if the user has entered anything in the field or not.  If
//not, we sub in zeros and in the conduit, we'll interpret those as XX in Eprom so we don't
//alter that particular chunk of mem
//=============================================================================================
public void addButton_ActionEvents() 
{
	String test 	= null;
	Integer valid 	= null;
	boolean secOk 	= false;
	boolean classOk = false;
	boolean divOk	= false;
	
	//get the input out of the fields
	nameThing = getAddressNameField().getText();

	test = new String( getAddressSectionField().getText() );
	if(test.length() != 0)
	{
		try
		{
			sectionThing = Integer.parseInt( getAddressSectionField().getText() );
			secOk = true;
		}
		catch(NumberFormatException e)
		{
			getAddressSectionField().setBackground(Color.red);
			theWarning = new String("  Invalid Section");
		}
	}
	else
	{
		getAddressSectionField().setText("0");
		sectionThing = Integer.parseInt( getAddressSectionField().getText() );
		secOk = true;
	}

	test = new String( getAddressClassField().getText()) ;
	if(test.length() != 0)
	{
		try
		{
			classThing = Integer.parseInt( getAddressClassField().getText() );
			classOk = true;
		}
		catch(NumberFormatException e)
		{
			getAddressClassField().setBackground(Color.red);
			theWarning = new String("  Invalid Class");
		}
	}
	else
	{
		getAddressClassField().setText("0");
		classThing = Integer.parseInt( getAddressClassField().getText() );
		classOk = true;
	}	
	
	test = new String(getAddressDivisionField().getText());
	if(test.length() != 0)
	{
		try
		{
			divisionThing = Integer.parseInt(getAddressDivisionField().getText());
			divOk = true;
		}
		catch(NumberFormatException e)
		{
			getAddressDivisionField().setBackground(Color.red);
			theWarning = new String("  Invalid Division");
		}
	}
	else
	{
		getAddressDivisionField().setText("0");
		divisionThing = Integer.parseInt( getAddressDivisionField().getText() );
		divOk = true;
	}

	if( (secOk) && (classOk) && (divOk) )
	{
		valid = doValidation(nameThing, sectionThing, classThing, divisionThing);

	}
	
	if( (valid != null) && (secOk) && (classOk) && (divOk) )
	{
		//clear the fields
		getAddressNameField().setText(null);
		getAddressSectionField().setText(null);
		getAddressClassField().setText(null);		
		getAddressDivisionField().setText(null);
		
		//make an instance of the lcr struct	
		lcrAddress = new LCR_Address(nameThing);

		//fill up the instance with the data we parsed
		lcrAddress.setAddressClass(classThing);
		lcrAddress.setAddressDivision(divisionThing);
		lcrAddress.setAddressSection(sectionThing);

		AddressesTableModel addyModel =	(AddressesTableModel) getAddressTable().getModel();
		addyModel.addRow(lcrAddress);
		getAddressScrollPane().revalidate();
		System.out.println("Add_Performed_OK");
	} 
	else
	{
		popInvalidWarning();
		System.out.println("Invalid Address");
	}

	return;
}
/**
 * Comment
 */
public void addressTable_MouseClicked(java.awt.event.MouseEvent mouseEvent) 
{
	int rowSelected = -1;
	
	if( (mouseEvent.getClickCount()) == 2)
	{
		rowSelected = getAddressTable().getSelectedRow();
		if(rowSelected >= 0) 
		{
			reloadAddress(rowSelected);
		}
	}	
	return;
}
/**
 * Comment
 */
public void addressTable_MouseEvents() 
{

	return;
}
/**
 * Insert the method's description here.
 * Creation date: (6/20/2001 12:16:03 PM)
 * @return boolean
 * @param v java.util.Vector
 */
public boolean checkCaps(Vector ourCapCodes)
{
	String testerString = null;
	javax.swing.JTextField testerField = null;
	int index = 0;
	int valider = 0;
	boolean isOk = false;
	Double theValue = null;
	int[] tracker = new int[3];
	
	for(index = 0; index < ourCapCodes.size(); index++)
	{
		testerField = new javax.swing.JTextField();
		testerField = (javax.swing.JTextField)ourCapCodes.elementAt(index);
		testerString = new String( testerField.getText() );

		if( (testerString != null) && (testerString.length() > 0) ) 
		{
			try
			{
				theValue = new Double(testerString);
				
				//check that the frequencies are in range
				if(theValue.doubleValue() < 1)
				{
					theWarning = new String("   Capcode Cannot Be Zero");
					getClientTabbedPane().setSelectedIndex(0);
					testerField.setBackground(Color.red);
					popInvalidWarning();
				}
				else
				{
					testerField.setBackground(Color.white);
					tracker[index] = 1;
				}		
			}
			catch(NumberFormatException e)
			{
				theWarning = new String("  Invalid Capcode?");
				getClientTabbedPane().setSelectedIndex(0);
				testerField.setBackground(Color.red);
				popInvalidWarning();
			}	
		}
		else
			tracker[index] = 1;
	}
	if( (tracker[0] == 1) && (tracker[1] == 1) && (tracker[2] == 1) )
		isOk = true;
	return isOk;
}
/**
 * Insert the method's description here.
 * Creation date: (6/17/2001 5:07:54 PM)
 * @return int
 * @param name java.lang.String
 */
public int checkForDuplicate(String name)
{
	
	AddressesTableModel addyModel =	(AddressesTableModel) getAddressTable().getModel();
	int numRows = 0;
	int index = 0;
	int returnValue = 1;	//we send back a 1 if there are no duplicates
	LCR_Address storedAddy = null;
	String checkString = null;
	
	numRows = addyModel.getRowCount();

	for(index = 0; index < numRows; index++)
	{
		storedAddy = new LCR_Address();
		storedAddy = addyModel.getRowAt(index);
		checkString = new String( storedAddy.getAddressName() );
		if( name.equals(checkString) == true )
		{
			System.out.println ("Duplicate Name!");
			getAddressNameField().grabFocus();
			getAddressNameField().setBackground(Color.red);
			returnValue = -1;
		}	
	}
	return returnValue;
}
//===========================================================================================
//checks that for every freq, there is a capcode..
// Creation date: (6/21/2001 10:46:28 AM)
// @param freq java.util.Vector
// @param cap java.util.Vector
//===========================================================================================
public boolean checkForPairs(Vector freq, Vector cap) 
{
	javax.swing.JTextField tester1Field = null;
	javax.swing.JTextField tester2Field = null;
	int index = 0;
	int[] count = new int[3];
	boolean allGood = false;
	
	for(index = 0; index < freq.size(); index++)
	{
		String tester1 = null;
		String tester2 = null;

		tester1Field = new javax.swing.JTextField();
		tester1Field = (javax.swing.JTextField)freq.elementAt(index);

		tester2Field = new javax.swing.JTextField();
		tester2Field = (javax.swing.JTextField)cap.elementAt(index);

		tester1 = new String(tester1Field.getText() );
		tester2 = new String(tester2Field.getText() );

		if( ((tester1.length() != 0) && (tester2.length() == 0)) || ((tester1.length() == 0) && (tester2.length() != 0)) )
		{
			theWarning = new String("Must Have Both Frequency And Capcode");
			tester1Field.setBackground(Color.red);
			tester2Field.setBackground(Color.red);
			popInvalidWarning();
			System.out.println ("Ouch!");
		}
		else
			count[index] = 1;
	}
	if( (count[0] == 1) && (count[1] == 1) && (count[2] == 1) )
		allGood = true;
	return allGood;
}
//===========================================================================================
//This method will double check that the frequencies that are entered in the fields are
//actually valid.  The possible ranges are 929.0125-932.2000
// Creation date: (6/19/2001 11:37:39 AM)
//@return boolean
//@param ourFrequencies java.util.Vector
//===========================================================================================
public boolean checkFrequencies(Vector ourFrequencies) 
{
	String testerString = null;
	String freqStart = null;
	String freqEnd = null;
	javax.swing.JTextField testerField = null;
	int index = 0;
	int charAt = 0;
	int[] tester = new int[3];
	double comparo = 0.0;
	Integer testValue = null;
	Double theValue = null;
	boolean isOk = false;

	for(index = 0; index < ourFrequencies.size(); index++)
	{
		testerField = new javax.swing.JTextField();
		
		//make a temp textfield and pull out the "words"
		testerField = (javax.swing.JTextField)ourFrequencies.elementAt(index);
		testerString = new String( testerField.getText() );

		if( (testerString != null) && (testerString.length() > 0) ) 
		{
			try
			{
				theValue = new Double(testerString);
				
				//check that the frequencies are in range
				if( (theValue.doubleValue() >= 929.0125) && (theValue.doubleValue() <= 932.2000) )
				{
					charAt = testerString.indexOf(".");
					freqEnd = new String();
					freqEnd = testerString.substring(charAt+1, testerString.length() );
					testValue = new Integer(freqEnd);
				
					//check that it's a multiple of .0125
					comparo = testValue.intValue() % 125;
					
					if(comparo != 0)
					{
						theWarning = new String("  Invalid Frequency");
						getClientTabbedPane().setSelectedIndex(0);
						testerField.setBackground(Color.red);
						popInvalidWarning();
					}
					else
					{
						testerField.setBackground(Color.white);
						tester[index] = 1;
					}
				}
				else
				{
					theWarning = new String( "Frequency Out Of Range!");
					getClientTabbedPane().setSelectedIndex(0);					
					testerField.setBackground(Color.red);
					popInvalidWarning();
				}					
			}
			catch(NumberFormatException e)
			{
				theWarning = new String("  Invalid Frequency?");
				getClientTabbedPane().setSelectedIndex(0);
				testerField.setBackground(Color.red);
				popInvalidWarning();
			}	
		}
		else
			tester[index] = 1;	
	}
	if( (tester[0] == 1) && (tester[1] == 1) && (tester[2] == 1) )
		isOk = true;
	return isOk;
}
/**
 * Comment
 */
public void configNoRadio_ActionEvents() 
{
	getAdvancedNoRadio().setVisible(false);
	getAdvancedYesRadio().setVisible(false);
	getAdvancedLabel().setVisible(false);
	getAdvancedNoRadio().doClick();
	
	return;
}
/**
 * Comment
 */
public void configYesRadio_ActionEvents() 
{
	getAdvancedNoRadio().setVisible(true);
	getAdvancedYesRadio().setVisible(true);
	getAdvancedLabel().setVisible(true);
	
	return;
}
/**
 * connEtoC1:  (ShedYesRadio.action. --> ClientFrame.shedYesRadio_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1() {
	try {
		// user code begin {1}
		// user code end
		this.configYesRadio_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (ConfigNoRadio.action. --> ClientFrame.configNoRadio_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.configNoRadio_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (AddButton.action. --> ClientFrame.addButton_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.addButton_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (DeleteButton.action. --> ClientFrame.deleteButton_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4() {
	try {
		// user code begin {1}
		// user code end
		this.deleteButton_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (Menu_Save.action.actionPerformed(java.awt.event.ActionEvent) --> ClientFrame.menu_Save_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.menu_Save_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (Menu_Load.action.actionPerformed(java.awt.event.ActionEvent) --> ClientFrame.menu_Load_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.menu_Load_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC7:  (Menu_Exit.action.actionPerformed(java.awt.event.ActionEvent) --> ClientFrame.menu_Exit_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.menu_Exit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC8:  (Menu_About.action.actionPerformed(java.awt.event.ActionEvent) --> ClientFrame.menu_About_ActionPerformed1(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.menu_About_ActionPerformed1(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC9:  (AddressTable.mouse.mouseClicked(java.awt.event.MouseEvent) --> ClientFrame.addressTable_MouseClicked(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC9(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.addressTable_MouseClicked(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
public void deleteButton_ActionEvents() 
{
	AddressesTableModel addyModel = (AddressesTableModel)getAddressTable().getModel();

	if( addyModel.getRowCount() >= 1 )
	{
		int rowToDie = getAddressTable().getSelectedRow();
		addyModel.removeRow(rowToDie);	
		System.out.println ("Del_Performed_OK");
	}
	return;
}
//===========================================================================================
//here we recieve a vector with all the details of the settings from the prsu.ady file that
//we loaded and we slap it back on the screen so the user doesn't have to re-type everything
//again
//===========================================================================================

public void displayLoadedAddressFile(Vector loadedAddressVector)
{
	AddressesTableModel addyModel =	(AddressesTableModel) getAddressTable().getModel();
	LCR_Address lcrAddress = new LCR_Address();
		
	for(int count = 0; count < loadedAddressVector.size(); count++)
	{
		lcrAddress = (LCR_Address)loadedAddressVector.get(count);
		addyModel.addRow(lcrAddress);
	}	
}
//===========================================================================================
//here we recieve a vector with all the details of the settings from the prsu.cfg file that 
//we loaded and we slap it back on the screen so the user doesn't have to re-type everything 
//again
//===========================================================================================

public void displayLoadedConfigFile(Vector configVector)
{
	Vector		radioVector;
	Vector		yea_nea;
	Vector		companyVector;
	int			index;
	int			count;
	
	companyVector = new Vector();
	yea_nea	= new Vector();
	radioVector	= new Vector();	

	//add the radio buttons to the vector
	radioVector.add(getAdvancedYesRadio());
	radioVector.add(getSwapYesRadio());
	radioVector.add(getConfigYesRadio());
	radioVector.add(getSwapYesRadio());

	//new-- add the text fields to the vector
	companyVector.addElement(getPagingNameOneField());
	companyVector.addElement(getPagingFrequencyOneField());
	companyVector.addElement(getPagingCapCodeOneField());
	
	companyVector.addElement(getPagingNameTwoField());
	companyVector.addElement(getPagingFrequencyTwoField());
	companyVector.addElement(getPagingCapCodeTwoField());
	
	companyVector.addElement(getPagingNameThreeField());
	companyVector.addElement(getPagingFrequencyThreeField());
	companyVector.addElement(getPagingCapCodeThreeField());
	
	//sets the radio buttons on the interface
	for(index = 0; index < radioVector.size(); index++)
	{
		if(configVector.elementAt(index).toString().equals("1"))
		{
			((javax.swing.JRadioButton)radioVector.elementAt(index)).doClick();
		}
	}
	
	//sets the savetype radio buttons
	if(configVector.elementAt(index).toString().equals("1"))
			getSaveVerifyRadio().doClick();
	if(configVector.elementAt(index).toString().equals("2"))
			getSaveAutoRadio().doClick();
	if(configVector.elementAt(index).toString().equals("3"))
			getSaveQuietRadio().doClick();

	//print out the email address(es)		
	index++;
	getEmailField().setText(configVector.elementAt(index).toString());		
	
	//set the text into the paging company fields
	for(count = 0; count < companyVector.size(); count++)
	{
		index++;
		javax.swing.JTextField tempField =(javax.swing.JTextField)companyVector.elementAt(count);
		tempField.setText(configVector.elementAt(index).toString());
	}	
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 11:31:07 AM)
 */
public void doAddressLoad() 
{
	java.io.BufferedReader 		buffIn 			= null;
	java.io.InputStreamReader 	inReader 		= null;
	FileInputStream 			loadedAddresses	= null;
	Vector						addressVector 	= null;
	LCR_Address					inComingAddress = null;
		
	try
	{
		//we're getting access to the file
		loadedAddresses = new FileInputStream(thePath);
		inReader = new java.io.InputStreamReader(loadedAddresses);
		buffIn = new java.io.BufferedReader(inReader);
		addressVector = new Vector();
	}
	catch (IOException ex)
	{
		System.out.println("Couldn't Load " + ADDRESS_FILENAME);
	}

	try
	{
		String inputString = null;
		String stringToTakeApart = null;
		String finalString = null;
		
		while( (inputString = buffIn.readLine() ) != null)
		{
			stringToTakeApart = new String(inputString);
			
			//decide what part of the address glop it is
			if( stringToTakeApart.regionMatches(true, 0, "AddyName=", 0, 9))
			{
				inComingAddress = new LCR_Address();
				int end	= stringToTakeApart.length();
				int begin = stringToTakeApart.indexOf('=');
				finalString = new String ( stringToTakeApart.substring(begin + 1, end) );		
				inComingAddress.setAddressName(finalString);
			}
			
			else if( stringToTakeApart.regionMatches(true, 0, "AddySection=", 0, 9) )
			{
				Integer value = null;
				int end	= stringToTakeApart.length();
				int begin = stringToTakeApart.indexOf('=');
				finalString = new String ( stringToTakeApart.substring(begin + 1, end) );
				value = new Integer(finalString);
				inComingAddress.setAddressSection(value.intValue());
			}
			
			else if( stringToTakeApart.regionMatches(true, 0, "AddyClass=", 0, 9) )
			{
				Integer value = null;
				int end	= stringToTakeApart.length();
				int begin = stringToTakeApart.indexOf('=');
				finalString = new String ( stringToTakeApart.substring(begin + 1, end) );		
				value = new Integer(finalString);
				inComingAddress.setAddressClass(value.intValue());
			}
			
			else if( stringToTakeApart.regionMatches(true, 0, "AddyDivision=", 0, 9) )
			{
				Integer value = null;
				int end	= stringToTakeApart.length();
				int begin = stringToTakeApart.indexOf('=');
				finalString = new String ( stringToTakeApart.substring(begin + 1, end) );		
				value = new Integer(finalString);
				inComingAddress.setAddressDivision(value.intValue());
				//slap the new Lcr_Address into the vector
				addressVector.addElement(inComingAddress);
			}
		}
		displayLoadedAddressFile(addressVector);
	}
	catch (Exception e)
	{
		System.out.println("Couldn't Read " + ADDRESS_FILENAME);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 9:57:30 AM)
 */
public void doAddressSave() 
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
	AddressesTableModel addyModel = (AddressesTableModel) getAddressTable().getModel();
	
	outPutStrings 	= new Vector();
	outPutBuffer 	= new byte[1024];
	bigString 		= new String();
	tackOnString 	= "\r\n";
	
//	if( (isFreqOk == true) && (isCapOk == true) )
	if(goodPairs == true)
	{
		//this is where the file is created
		try
		{
			addressFile = new FileOutputStream(thePath);
		}
		catch (IOException e)
		{
			System.out.println("Address Output Error!");
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
				theWarning = new String("        Files Written");
				popInvalidWarning();
			} 
			catch (IOException e)
			{
				System.out.println("Bad Stuff Happened...");
			}
		}
	}
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 4:06:56 PM)
 */
public void doConfigLoad()
{
	FileInputStream 	loadedConfig 	= null;
	Vector				configVector 	= null;
	
	try
	{
		loadedConfig = new FileInputStream(thePath);
	}
	catch (IOException ex)
	{
		System.out.println("Couldn't Load The File");
	}

	Properties fileProps = new Properties();

	try
	{
		fileProps.load(loadedConfig);
		configVector = new Vector();

		String advancedString = fileProps.getProperty("EnableSuper");
		configVector.addElement(advancedString);

		String commandsString = fileProps.getProperty("EnableCommands");
		configVector.addElement(commandsString);

		String configString	= fileProps.getProperty("EnableConfig");
		configVector.addElement(configString);
		
		String swapString = fileProps.getProperty("EnableSwap");
		configVector.addElement(swapString);
		
		String saveTypeString = fileProps.getProperty("SaveOptions");
		configVector.addElement(saveTypeString);
		
		String emailString = fileProps.getProperty("EmailAddys");
		configVector.addElement(emailString);
		
		String nameOneString = fileProps.getProperty("PagingOneName");
		configVector.addElement(nameOneString);
		
		String freqOneString = fileProps.getProperty("PagingOneFreq");
		configVector.addElement(freqOneString);
		
		String capOneString	= fileProps.getProperty("PagingOneCap");
		configVector.addElement(capOneString);
		
		String nameTwoString = fileProps.getProperty("PagingTwoName");
		configVector.addElement(nameTwoString);
		
		String freqTwoString = fileProps.getProperty("PagingTwoFreq");
		configVector.addElement(freqTwoString);
		
		String capTwoString = fileProps.getProperty("PagingTwoCap");
		configVector.addElement(capTwoString);
		
		String nameThreeString = fileProps.getProperty("PagingThreeName");
		configVector.addElement(nameThreeString);
		
		String freqThreeString = fileProps.getProperty("PagingThreeFreq");
		configVector.addElement(freqThreeString);
		
		String capThreeString = fileProps.getProperty("PagingThreeCap");
		configVector.addElement(capThreeString);

		displayLoadedConfigFile(configVector);
	}
	catch (Exception e)
	{
		System.out.println("Couldn't Read The File");
	}
}
//=============================================================================================
//this function will parse the controls and create the config file the conduit uses to config 
//the palm we put the radio buttons into a vector (along with their respective strings) and 
//then we can increment through and check their values, adding the 1 or 0 to the ends as we
//write them out then we parse out the text-fields and add those to the file as well
//=============================================================================================
public void doConfigSave() 
{
	FileOutputStream configFile = null;
	String enderPosString;
	String enderNegString;
	String bigString;
	String tackOnString;
	byte outPutBuffer[];
	int stringSize = 0;
	int index;
	int count = 0;
	int saveValue;
	Integer one;
	Integer zero;
	Vector outPutStrings;
	Vector radioVector;
	Vector yea_nea;
	Vector companyVector;
	Vector checkFreqVector;
	Vector checkCapVector;
	
	companyVector 	= new Vector();
	yea_nea 		= new Vector();
	radioVector 	= new Vector();
	outPutStrings 	= new Vector();
	outPutBuffer 	= new byte[1024];
	bigString 		= new String();
	one 			= new Integer(1);
	zero 			= new Integer(0);
	checkFreqVector = new Vector();
	checkCapVector 	= new Vector();
	
	enderPosString 	= "1";
	enderNegString 	= "0";
	tackOnString 	= "\r\n";
	isFreqOk = false;
	isCapOk = false;
	goodPairs = false;
	
	//add the radio buttons to the vector
	radioVector.add( getAdvancedYesRadio() );
	radioVector.add( getSwapYesRadio() );
	radioVector.add( getConfigYesRadio() );
	radioVector.add( getSwapYesRadio() );

	//new-- add the text fields to the vector
	companyVector.addElement( getPagingNameOneField() );
	companyVector.addElement( getPagingFrequencyOneField() );
	companyVector.addElement( getPagingCapCodeOneField() );

	companyVector.addElement( getPagingNameTwoField() );
	companyVector.addElement( getPagingFrequencyTwoField() );
	companyVector.addElement( getPagingCapCodeTwoField() );

	companyVector.addElement( getPagingNameThreeField() );
	companyVector.addElement( getPagingFrequencyThreeField() );
	companyVector.addElement( getPagingCapCodeThreeField() );

	checkFreqVector.addElement( getPagingFrequencyOneField() );
	checkFreqVector.addElement( getPagingFrequencyTwoField() );
	checkFreqVector.addElement( getPagingFrequencyThreeField() );

	checkCapVector.addElement( getPagingCapCodeOneField() );
	checkCapVector.addElement( getPagingCapCodeTwoField() );
	checkCapVector.addElement( getPagingCapCodeThreeField() );

	goClearReds(checkFreqVector, checkCapVector);
	
	isFreqOk = checkFrequencies(checkFreqVector);
	if(isFreqOk == true)
	{
		isCapOk = checkCaps(checkCapVector);
		if(isCapOk == true)
		{
			goodPairs = checkForPairs(checkFreqVector, checkCapVector);
		}
	}
	
	if( (isFreqOk == true) && (isCapOk == true) && (goodPairs == true) )
	{	
		//this is where the file is created
		try
		{
			configFile = new FileOutputStream(thePath);
		} 
		catch (IOException e)
		{
			System.out.println("Output Error!");
		}

		if (configFile != null)
		{
			//put the strings into the vector
			outPutStrings.addElement("EnableSuper=");
			outPutStrings.addElement("EnableCommands=");
			outPutStrings.addElement("EnableConfig=");
			outPutStrings.addElement("EnableSwap=");

			outPutStrings.addElement("SaveOptions=");
			outPutStrings.addElement("EmailAddys=");

			outPutStrings.addElement("PagingOneName=");
			outPutStrings.addElement("PagingOneFreq=");
			outPutStrings.addElement("PagingOneCap=");
			outPutStrings.addElement("PagingTwoName=");
			outPutStrings.addElement("PagingTwoFreq=");
			outPutStrings.addElement("PagingTwoCap=");
			outPutStrings.addElement("PagingThreeName=");
			outPutStrings.addElement("PagingThreeFreq=");
			outPutStrings.addElement("PagingThreeCap=");

			//cat up the string for the swap/config/advanced/shed bits
			for (index = 0; index < radioVector.size(); index++)
			{
				if (((javax.swing.JRadioButton) radioVector.elementAt(index)).isSelected())
				{
					bigString =
						bigString
						+ (String) outPutStrings.elementAt(count)
						+ enderPosString
						+ tackOnString;
					count++;
				}
				else
				{
					bigString =
						bigString
						+ (String) outPutStrings.elementAt(count)
						+ enderNegString
						+ tackOnString;
					count++;
				}
			}

			//put the save options in
			if (getSaveVerifyRadio().isSelected())
				saveValue = 1;
			else if (getSaveAutoRadio().isSelected())
				saveValue = 2;
			else
				saveValue = 3;
			bigString =
				bigString + (String) outPutStrings.elementAt(count) + saveValue + tackOnString;

			//parse out the email addresses (put if placeholder if nothing is entered)
			String email = new String(getEmailField().getText());
			count++;
			if (email.length() < 3)
			{
				String backup = new String("your@email.com");
				bigString =
					bigString + (String) outPutStrings.elementAt(count) + backup + tackOnString;
			}
			else
				bigString =
					bigString + (String) outPutStrings.elementAt(count) + email + tackOnString;

			//parse out the company names/freqs/capcodes and cat them to the bigstring		
			for (index = 0; index < companyVector.size(); index++)
			{
				count++;
				String parsedInfo =
					((javax.swing.JTextField) companyVector.elementAt(index)).getText();
				bigString =
					bigString + (String) outPutStrings.elementAt(count) + parsedInfo + tackOnString;
			}

			//write the file up...
			try
			{
				outPutBuffer = bigString.getBytes();
				configFile.write(outPutBuffer);
			}
			catch (IOException e)
			{
				System.out.println("Bad Stuff Happened...");
			}
		}
	}
	return;
}
//===========================================================================================
//this is just a little data-checking so the palm won't barf when we upload the addresses
//the point is to have tracker=4 when the method is over, 1 added for each level that the
//address input passes
//===========================================================================================

public Integer doValidation(String nameIn, int sectionIn, int classIn, int divisionIn) 
{
	int tracker = 0;

	tracker = checkForDuplicate(nameIn);

	if(tracker != -1)
		getAddressNameField().setBackground(Color.white);
	else
		theWarning = new String("Duplicate Program!");
		
	//make sure the addresses are in the proper ranges	
	if( (divisionThing >= 0) && (divisionThing <= 16) )
	{
		tracker = tracker + 1;
		getAddressDivisionField().setBackground(Color.white);
	}
	else
	{
		theWarning = new String("Division Out Of Range!");
		getAddressDivisionField().grabFocus();
		getAddressDivisionField().setBackground(Color.red);
	}
		
	if( (classIn >= 0) && (classIn <= 16) )
	{
		tracker = tracker + 1;
		getAddressClassField().setBackground(Color.white);
	}
	else
	{
		theWarning = new String("Class Out Of Range!");
		getAddressClassField().grabFocus();
		getAddressClassField().setBackground(Color.red);
	}
	
	if( (sectionIn >= 0) && (sectionIn <= 255) )
	{
		tracker = tracker + 1;
		getAddressSectionField().setBackground(Color.white);
	}
	else
	{
		theWarning = new String("Section Out Of Range!");
		getAddressSectionField().grabFocus();
		getAddressSectionField().setBackground(Color.red);
	}
	
	if(tracker == 4)
		return new Integer(1);
	else	
		return null;
}
/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getAddButton() {
	if (ivjAddButton == null) {
		try {
			ivjAddButton = new javax.swing.JButton();
			ivjAddButton.setName("AddButton");
			ivjAddButton.setText("Add");
			ivjAddButton.setBounds(20, 15, 85, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddButton;
}
/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddressClassField() {
	if (ivjAddressClassField == null) {
		try {
			ivjAddressClassField = new javax.swing.JTextField();
			ivjAddressClassField.setName("AddressClassField");
			ivjAddressClassField.setBounds(400, 70, 48, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressClassField;
}
/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddressDivisionField() {
	if (ivjAddressDivisionField == null) {
		try {
			ivjAddressDivisionField = new javax.swing.JTextField();
			ivjAddressDivisionField.setName("AddressDivisionField");
			ivjAddressDivisionField.setBounds(480, 70, 47, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressDivisionField;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddressNameField() {
	if (ivjAddressNameField == null) {
		try {
			ivjAddressNameField = new javax.swing.JTextField();
			ivjAddressNameField.setName("AddressNameField");
			ivjAddressNameField.setBounds(25, 70, 248, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressNameField;
}
/**
 * Return the AddressPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressPanel() {
	if (ivjAddressPanel == null) {
		try {
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setLayout(null);
			getAddressPanel().add(getButtonPanel(), getButtonPanel().getName());
			getAddressPanel().add(getEntryPanel(), getEntryPanel().getName());
			getAddressPanel().add(getAddressScrollPane(), getAddressScrollPane().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressPanel;
}
/**
 * Return the JScrollPane1 property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getAddressScrollPane() {
	if (ivjAddressScrollPane == null) {
		try {
			ivjAddressScrollPane = new javax.swing.JScrollPane();
			ivjAddressScrollPane.setName("AddressScrollPane");
			ivjAddressScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjAddressScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjAddressScrollPane.setBounds(79, 12, 552, 237);
			getAddressScrollPane().setViewportView(getAddressTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressScrollPane;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getAddressSectionField() {
	if (ivjAddressSectionField == null) {
		try {
			ivjAddressSectionField = new javax.swing.JTextField();
			ivjAddressSectionField.setName("AddressSectionField");
			ivjAddressSectionField.setBounds(320, 70, 47, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressSectionField;
}
/**
 * Return the ScrollPaneTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getAddressTable() {
	if (ivjAddressTable == null) {
		try {
			ivjAddressTable = new javax.swing.JTable();
			ivjAddressTable.setName("AddressTable");
			getAddressScrollPane().setColumnHeaderView(ivjAddressTable.getTableHeader());
			getAddressScrollPane().getViewport().setBackingStoreEnabled(true);
			ivjAddressTable.setAutoResizeMode(0);
			ivjAddressTable.setPreferredSize(new java.awt.Dimension(530,233));
			ivjAddressTable.setBounds(0, 0, 549, 234);
			ivjAddressTable.setShowVerticalLines(false);
			// user code begin {1}

			ivjAddressTable.setModel(new AddressesTableModel());
		
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressTable;
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 2:31:41 PM)
 */
public javax.swing.ButtonGroup getAdvancedGroup() 
{
	if(advancedGroup == null)
		advancedGroup = new javax.swing.ButtonGroup();
	return advancedGroup;
}
/**
 * Return the JLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvancedLabel() {
	if (ivjAdvancedLabel == null) {
		try {
			ivjAdvancedLabel = new javax.swing.JLabel();
			ivjAdvancedLabel.setName("AdvancedLabel");
			ivjAdvancedLabel.setText("Enable the user to do Advanced configs?");
			ivjAdvancedLabel.setBounds(18, 117, 309, 20);
			ivjAdvancedLabel.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedLabel;
}
/**
 * Return the JRadioButton5 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAdvancedNoRadio() {
	if (ivjAdvancedNoRadio == null) {
		try {
			ivjAdvancedNoRadio = new javax.swing.JRadioButton();
			ivjAdvancedNoRadio.setName("AdvancedNoRadio");
			ivjAdvancedNoRadio.setText("No");
			ivjAdvancedNoRadio.setBounds(418, 117, 39, 20);
			ivjAdvancedNoRadio.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedNoRadio;
}
/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAdvancedYesRadio() {
	if (ivjAdvancedYesRadio == null) {
		try {
			ivjAdvancedYesRadio = new javax.swing.JRadioButton();
			ivjAdvancedYesRadio.setName("AdvancedYesRadio");
			ivjAdvancedYesRadio.setText("Yes");
			ivjAdvancedYesRadio.setBounds(362, 117, 47, 20);
			ivjAdvancedYesRadio.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvancedYesRadio;
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
			ivjButtonPanel.setBounds(250, 402, 219, 55);
			getButtonPanel().add(getAddButton(), getAddButton().getName());
			getButtonPanel().add(getDeleteButton(), getDeleteButton().getName());
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
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getClientFrameContentPane() {
	if (ivjClientFrameContentPane == null) {
		try {
			ivjClientFrameContentPane = new javax.swing.JPanel();
			ivjClientFrameContentPane.setName("ClientFrameContentPane");
			ivjClientFrameContentPane.setLayout(null);
			getClientFrameContentPane().add(getClientTabbedPane(), getClientTabbedPane().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClientFrameContentPane;
}
/**
 * Return the ClientFrameJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getClientFrameJMenuBar() {
	if (ivjClientFrameJMenuBar == null) {
		try {
			ivjClientFrameJMenuBar = new javax.swing.JMenuBar();
			ivjClientFrameJMenuBar.setName("ClientFrameJMenuBar");
			ivjClientFrameJMenuBar.add(getMenu_File());
			ivjClientFrameJMenuBar.add(getMenu_Help());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClientFrameJMenuBar;
}
/**
 * Return the ClientTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getClientTabbedPane() {
	if (ivjClientTabbedPane == null) {
		try {
			ivjClientTabbedPane = new javax.swing.JTabbedPane();
			ivjClientTabbedPane.setName("ClientTabbedPane");
			ivjClientTabbedPane.setBounds(1, 1, 714, 623);
			ivjClientTabbedPane.insertTab("ConfigPanel", null, getConfigPanel(), null, 0);
			ivjClientTabbedPane.insertTab("AddressPanel", null, getAddressPanel(), null, 1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjClientTabbedPane;
}
/**
 * Return the CommandPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getCommandPanel() {
	if (ivjCommandPanel == null) {
		try {
			ivjCommandPanel = new javax.swing.JPanel();
			ivjCommandPanel.setName("CommandPanel");
			ivjCommandPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjCommandPanel.setLayout(null);
			ivjCommandPanel.setBounds(111, 31, 486, 197);
			getCommandPanel().add(getJLabel6(), getJLabel6().getName());
			getCommandPanel().add(getJLabel7(), getJLabel7().getName());
			getCommandPanel().add(getAdvancedLabel(), getAdvancedLabel().getName());
			getCommandPanel().add(getJLabel9(), getJLabel9().getName());
			getCommandPanel().add(getShedYesRadio(), getShedYesRadio().getName());
			getCommandPanel().add(getAdvancedYesRadio(), getAdvancedYesRadio().getName());
			getCommandPanel().add(getConfigYesRadio(), getConfigYesRadio().getName());
			getCommandPanel().add(getSwapYesRadio(), getSwapYesRadio().getName());
			getCommandPanel().add(getAdvancedNoRadio(), getAdvancedNoRadio().getName());
			getCommandPanel().add(getConfigNoRadio(), getConfigNoRadio().getName());
			getCommandPanel().add(getShedNoRadio(), getShedNoRadio().getName());
			getCommandPanel().add(getSwapNoRadio(), getSwapNoRadio().getName());
			getCommandPanel().add(getJLabel10(), getJLabel10().getName());
			getCommandPanel().add(getSaveAutoRadio(), getSaveAutoRadio().getName());
			getCommandPanel().add(getSaveVerifyRadio(), getSaveVerifyRadio().getName());
			getCommandPanel().add(getSaveQuietRadio(), getSaveQuietRadio().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommandPanel;
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 2:31:41 PM)
 */
public javax.swing.ButtonGroup getConfigGroup() 
{
	if(configGroup == null)
		configGroup = new javax.swing.ButtonGroup();
	return configGroup;
}
/**
 * Return the JRadioButton6 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getConfigNoRadio() {
	if (ivjConfigNoRadio == null) {
		try {
			ivjConfigNoRadio = new javax.swing.JRadioButton();
			ivjConfigNoRadio.setName("ConfigNoRadio");
			ivjConfigNoRadio.setText("No");
			ivjConfigNoRadio.setBounds(418, 85, 43, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigNoRadio;
}
/**
 * Return the ConfigPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getConfigPanel() {
	if (ivjConfigPanel == null) {
		try {
			ivjConfigPanel = new javax.swing.JPanel();
			ivjConfigPanel.setName("ConfigPanel");
			ivjConfigPanel.setLayout(null);
			getConfigPanel().add(getCommandPanel(), getCommandPanel().getName());
			getConfigPanel().add(getPagingPanel(), getPagingPanel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigPanel;
}
/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getConfigYesRadio() {
	if (ivjConfigYesRadio == null) {
		try {
			ivjConfigYesRadio = new javax.swing.JRadioButton();
			ivjConfigYesRadio.setName("ConfigYesRadio");
			ivjConfigYesRadio.setText("Yes");
			ivjConfigYesRadio.setBounds(362, 85, 50, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigYesRadio;
}
/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getDeleteButton() {
	if (ivjDeleteButton == null) {
		try {
			ivjDeleteButton = new javax.swing.JButton();
			ivjDeleteButton.setName("DeleteButton");
			ivjDeleteButton.setText("Delete");
			ivjDeleteButton.setBounds(117, 15, 85, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDeleteButton;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getEmailField() {
	if (ivjEmailField == null) {
		try {
			ivjEmailField = new javax.swing.JTextField();
			ivjEmailField.setName("EmailField");
			ivjEmailField.setBounds(30, 47, 505, 20);
			// user code begin {1}
			getEmailField().setText("placeholder@someplace.com");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEmailField;
}
/**
 * Return the EntryPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getEntryPanel() {
	if (ivjEntryPanel == null) {
		try {
			ivjEntryPanel = new javax.swing.JPanel();
			ivjEntryPanel.setName("EntryPanel");
			ivjEntryPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjEntryPanel.setLayout(null);
			ivjEntryPanel.setBounds(79, 271, 554, 105);
			getEntryPanel().add(getJLabel1(), getJLabel1().getName());
			getEntryPanel().add(getJLabel2(), getJLabel2().getName());
			getEntryPanel().add(getJLabel3(), getJLabel3().getName());
			getEntryPanel().add(getJLabel4(), getJLabel4().getName());
			getEntryPanel().add(getJLabel5(), getJLabel5().getName());
			getEntryPanel().add(getAddressNameField(), getAddressNameField().getName());
			getEntryPanel().add(getAddressSectionField(), getAddressSectionField().getName());
			getEntryPanel().add(getAddressClassField(), getAddressClassField().getName());
			getEntryPanel().add(getAddressDivisionField(), getAddressDivisionField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEntryPanel;
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
			ivjJLabel1.setText("Enter Addressing Information:");
			ivjJLabel1.setBounds(25, 13, 257, 14);
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
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel10() {
	if (ivjJLabel10 == null) {
		try {
			ivjJLabel10 = new javax.swing.JLabel();
			ivjJLabel10.setName("JLabel10");
			ivjJLabel10.setText("Type of \'Save\' the Palm should do?");
			ivjJLabel10.setBounds(18, 151, 228, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel10;
}
/**
 * Return the JLabel11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel11() {
	if (ivjJLabel11 == null) {
		try {
			ivjJLabel11 = new javax.swing.JLabel();
			ivjJLabel11.setName("JLabel11");
			ivjJLabel11.setText("Email Address(es)");
			ivjJLabel11.setBounds(30, 24, 133, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel11;
}
/**
 * Return the JLabel12 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel12() {
	if (ivjJLabel12 == null) {
		try {
			ivjJLabel12 = new javax.swing.JLabel();
			ivjJLabel12.setName("JLabel12");
			ivjJLabel12.setText("#1 Paging Company Name");
			ivjJLabel12.setBounds(30, 110, 201, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel12;
}
/**
 * Return the JLabel13 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel13() {
	if (ivjJLabel13 == null) {
		try {
			ivjJLabel13 = new javax.swing.JLabel();
			ivjJLabel13.setName("JLabel13");
			ivjJLabel13.setText("#2 Paging Company Name");
			ivjJLabel13.setBounds(30, 180, 193, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel13;
}
/**
 * Return the JLabel14 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel14() {
	if (ivjJLabel14 == null) {
		try {
			ivjJLabel14 = new javax.swing.JLabel();
			ivjJLabel14.setName("JLabel14");
			ivjJLabel14.setText("#3 Paging Company Name");
			ivjJLabel14.setBounds(30, 250, 193, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel14;
}
/**
 * Return the JLabel15 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel15() {
	if (ivjJLabel15 == null) {
		try {
			ivjJLabel15 = new javax.swing.JLabel();
			ivjJLabel15.setName("JLabel15");
			ivjJLabel15.setText("Frequency");
			ivjJLabel15.setBounds(320, 110, 117, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel15;
}
/**
 * Return the JLabel16 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel16() {
	if (ivjJLabel16 == null) {
		try {
			ivjJLabel16 = new javax.swing.JLabel();
			ivjJLabel16.setName("JLabel16");
			ivjJLabel16.setText("Frequency");
			ivjJLabel16.setBounds(320, 180, 116, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel16;
}
/**
 * Return the JLabel17 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel17() {
	if (ivjJLabel17 == null) {
		try {
			ivjJLabel17 = new javax.swing.JLabel();
			ivjJLabel17.setName("JLabel17");
			ivjJLabel17.setText("Frequency");
			ivjJLabel17.setBounds(320, 250, 130, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel17;
}
/**
 * Return the JLabel18 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel18() {
	if (ivjJLabel18 == null) {
		try {
			ivjJLabel18 = new javax.swing.JLabel();
			ivjJLabel18.setName("JLabel18");
			ivjJLabel18.setText("CapCode");
			ivjJLabel18.setBounds(480, 110, 61, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel18;
}
/**
 * Return the JLabel19 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel19() {
	if (ivjJLabel19 == null) {
		try {
			ivjJLabel19 = new javax.swing.JLabel();
			ivjJLabel19.setName("JLabel19");
			ivjJLabel19.setText("CapCode");
			ivjJLabel19.setBounds(480, 180, 65, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel19;
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
			ivjJLabel2.setText("Program Name");
			ivjJLabel2.setBounds(25, 45, 147, 14);
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
 * Return the JLabel20 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel20() {
	if (ivjJLabel20 == null) {
		try {
			ivjJLabel20 = new javax.swing.JLabel();
			ivjJLabel20.setName("JLabel20");
			ivjJLabel20.setText("CapCode");
			ivjJLabel20.setBounds(480, 250, 71, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel20;
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
			ivjJLabel3.setBounds(320, 45, 45, 14);
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
			ivjJLabel4.setBounds(400, 45, 45, 14);
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
			ivjJLabel5.setBounds(480, 45, 45, 14);
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
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel6() {
	if (ivjJLabel6 == null) {
		try {
			ivjJLabel6 = new javax.swing.JLabel();
			ivjJLabel6.setName("JLabel6");
			ivjJLabel6.setText("Enable the user to send swap frequency commands?");
			ivjJLabel6.setBounds(18, 53, 313, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel6;
}
/**
 * Return the JLabel7 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel7() {
	if (ivjJLabel7 == null) {
		try {
			ivjJLabel7 = new javax.swing.JLabel();
			ivjJLabel7.setName("JLabel7");
			ivjJLabel7.setText("Enable the user to send test shed commands?");
			ivjJLabel7.setBounds(18, 22, 297, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel7;
}
/**
 * Return the JLabel9 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel9() {
	if (ivjJLabel9 == null) {
		try {
			ivjJLabel9 = new javax.swing.JLabel();
			ivjJLabel9.setName("JLabel9");
			ivjJLabel9.setText("Enable the user to do basic configs?");
			ivjJLabel9.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabel9.setBounds(18, 85, 310, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel9;
}
/**
 * Return the Menu_About property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMenu_About() {
	if (ivjMenu_About == null) {
		try {
			ivjMenu_About = new javax.swing.JMenuItem();
			ivjMenu_About.setName("Menu_About");
			ivjMenu_About.setMnemonic('a');
			ivjMenu_About.setText("About");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenu_About;
}
/**
 * Return the Menu_Exit property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMenu_Exit() {
	if (ivjMenu_Exit == null) {
		try {
			ivjMenu_Exit = new javax.swing.JMenuItem();
			ivjMenu_Exit.setName("Menu_Exit");
			ivjMenu_Exit.setMnemonic('x');
			ivjMenu_Exit.setText("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenu_Exit;
}
/**
 * Return the Menu_File property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getMenu_File() {
	if (ivjMenu_File == null) {
		try {
			ivjMenu_File = new javax.swing.JMenu();
			ivjMenu_File.setName("Menu_File");
			ivjMenu_File.setMnemonic('f');
			ivjMenu_File.setText("File");
			ivjMenu_File.add(getMenu_Save());
			ivjMenu_File.add(getMenu_Load());
			ivjMenu_File.add(getMenu_Exit());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenu_File;
}
/**
 * Return the Help property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getMenu_Help() {
	if (ivjMenu_Help == null) {
		try {
			ivjMenu_Help = new javax.swing.JMenu();
			ivjMenu_Help.setName("Menu_Help");
			ivjMenu_Help.setMnemonic('h');
			ivjMenu_Help.setText("Help");
			ivjMenu_Help.add(getMenu_About());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenu_Help;
}
/**
 * Return the Menu_Load property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMenu_Load() {
	if (ivjMenu_Load == null) {
		try {
			ivjMenu_Load = new javax.swing.JMenuItem();
			ivjMenu_Load.setName("Menu_Load");
			ivjMenu_Load.setMnemonic('l');
			ivjMenu_Load.setText("Load...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenu_Load;
}
/**
 * Return the Menu_Save property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMenu_Save() {
	if (ivjMenu_Save == null) {
		try {
			ivjMenu_Save = new javax.swing.JMenuItem();
			ivjMenu_Save.setName("Menu_Save");
			ivjMenu_Save.setMnemonic('s');
			ivjMenu_Save.setText("Save...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenu_Save;
}
/**
 * Return the JTextField6 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingCapCodeOneField() {
	if (ivjPagingCapCodeOneField == null) {
		try {
			ivjPagingCapCodeOneField = new javax.swing.JTextField();
			ivjPagingCapCodeOneField.setName("PagingCapCodeOneField");
			ivjPagingCapCodeOneField.setBounds(480, 130, 115, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingCapCodeOneField;
}
/**
 * Return the JTextField10 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingCapCodeThreeField() {
	if (ivjPagingCapCodeThreeField == null) {
		try {
			ivjPagingCapCodeThreeField = new javax.swing.JTextField();
			ivjPagingCapCodeThreeField.setName("PagingCapCodeThreeField");
			ivjPagingCapCodeThreeField.setBounds(480, 270, 115, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingCapCodeThreeField;
}
/**
 * Return the JTextField8 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingCapCodeTwoField() {
	if (ivjPagingCapCodeTwoField == null) {
		try {
			ivjPagingCapCodeTwoField = new javax.swing.JTextField();
			ivjPagingCapCodeTwoField.setName("PagingCapCodeTwoField");
			ivjPagingCapCodeTwoField.setBounds(480, 200, 115, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingCapCodeTwoField;
}
/**
 * Return the JTextField5 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingFrequencyOneField() {
	if (ivjPagingFrequencyOneField == null) {
		try {
			ivjPagingFrequencyOneField = new javax.swing.JTextField();
			ivjPagingFrequencyOneField.setName("PagingFrequencyOneField");
			ivjPagingFrequencyOneField.setBounds(320, 130, 115, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingFrequencyOneField;
}
/**
 * Return the JTextField9 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingFrequencyThreeField() {
	if (ivjPagingFrequencyThreeField == null) {
		try {
			ivjPagingFrequencyThreeField = new javax.swing.JTextField();
			ivjPagingFrequencyThreeField.setName("PagingFrequencyThreeField");
			ivjPagingFrequencyThreeField.setBounds(320, 270, 115, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingFrequencyThreeField;
}
/**
 * Return the JTextField7 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingFrequencyTwoField() {
	if (ivjPagingFrequencyTwoField == null) {
		try {
			ivjPagingFrequencyTwoField = new javax.swing.JTextField();
			ivjPagingFrequencyTwoField.setName("PagingFrequencyTwoField");
			ivjPagingFrequencyTwoField.setBounds(320, 200, 115, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingFrequencyTwoField;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingNameOneField() {
	if (ivjPagingNameOneField == null) {
		try {
			ivjPagingNameOneField = new javax.swing.JTextField();
			ivjPagingNameOneField.setName("PagingNameOneField");
			ivjPagingNameOneField.setBounds(30, 130, 250, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingNameOneField;
}
/**
 * Return the JTextField4 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingNameThreeField() {
	if (ivjPagingNameThreeField == null) {
		try {
			ivjPagingNameThreeField = new javax.swing.JTextField();
			ivjPagingNameThreeField.setName("PagingNameThreeField");
			ivjPagingNameThreeField.setBounds(30, 270, 250, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingNameThreeField;
}
/**
 * Return the JTextField3 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingNameTwoField() {
	if (ivjPagingNameTwoField == null) {
		try {
			ivjPagingNameTwoField = new javax.swing.JTextField();
			ivjPagingNameTwoField.setName("PagingNameTwoField");
			ivjPagingNameTwoField.setBounds(30, 200, 250, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingNameTwoField;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getPagingPanel() {
	if (ivjPagingPanel == null) {
		try {
			ivjPagingPanel = new javax.swing.JPanel();
			ivjPagingPanel.setName("PagingPanel");
			ivjPagingPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjPagingPanel.setLayout(null);
			ivjPagingPanel.setBounds(38, 244, 630, 307);
			getPagingPanel().add(getJLabel11(), getJLabel11().getName());
			getPagingPanel().add(getJLabel12(), getJLabel12().getName());
			getPagingPanel().add(getJLabel13(), getJLabel13().getName());
			getPagingPanel().add(getJLabel14(), getJLabel14().getName());
			getPagingPanel().add(getJLabel15(), getJLabel15().getName());
			getPagingPanel().add(getJLabel16(), getJLabel16().getName());
			getPagingPanel().add(getJLabel17(), getJLabel17().getName());
			getPagingPanel().add(getJLabel18(), getJLabel18().getName());
			getPagingPanel().add(getJLabel19(), getJLabel19().getName());
			getPagingPanel().add(getJLabel20(), getJLabel20().getName());
			getPagingPanel().add(getEmailField(), getEmailField().getName());
			getPagingPanel().add(getPagingNameOneField(), getPagingNameOneField().getName());
			getPagingPanel().add(getPagingNameTwoField(), getPagingNameTwoField().getName());
			getPagingPanel().add(getPagingNameThreeField(), getPagingNameThreeField().getName());
			getPagingPanel().add(getPagingFrequencyOneField(), getPagingFrequencyOneField().getName());
			getPagingPanel().add(getPagingCapCodeOneField(), getPagingCapCodeOneField().getName());
			getPagingPanel().add(getPagingFrequencyTwoField(), getPagingFrequencyTwoField().getName());
			getPagingPanel().add(getPagingCapCodeTwoField(), getPagingCapCodeTwoField().getName());
			getPagingPanel().add(getPagingFrequencyThreeField(), getPagingFrequencyThreeField().getName());
			getPagingPanel().add(getPagingCapCodeThreeField(), getPagingCapCodeThreeField().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingPanel;
}
/**
 * Return the JRadioButton9 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSaveAutoRadio() {
	if (ivjSaveAutoRadio == null) {
		try {
			ivjSaveAutoRadio = new javax.swing.JRadioButton();
			ivjSaveAutoRadio.setName("SaveAutoRadio");
			ivjSaveAutoRadio.setText("Auto");
			ivjSaveAutoRadio.setBounds(290, 151, 48, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveAutoRadio;
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 2:31:41 PM)
 */
public javax.swing.ButtonGroup getSaveGroup() 
{
	if(saveGroup == null)
		saveGroup = new javax.swing.ButtonGroup();
	return saveGroup;
}
/**
 * Return the JRadioButton11 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSaveQuietRadio() {
	if (ivjSaveQuietRadio == null) {
		try {
			ivjSaveQuietRadio = new javax.swing.JRadioButton();
			ivjSaveQuietRadio.setName("SaveQuietRadio");
			ivjSaveQuietRadio.setText("Quiet");
			ivjSaveQuietRadio.setBounds(413, 151, 52, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveQuietRadio;
}
/**
 * Return the JRadioButton10 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSaveVerifyRadio() {
	if (ivjSaveVerifyRadio == null) {
		try {
			ivjSaveVerifyRadio = new javax.swing.JRadioButton();
			ivjSaveVerifyRadio.setName("SaveVerifyRadio");
			ivjSaveVerifyRadio.setText("Verify");
			ivjSaveVerifyRadio.setBounds(350, 151, 53, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSaveVerifyRadio;
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 2:31:41 PM)
 */
public javax.swing.ButtonGroup getShedGroup() 
{
	if(shedGroup == null)
		shedGroup = new javax.swing.ButtonGroup();
	return shedGroup;
}
/**
 * Return the JRadioButton7 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getShedNoRadio() {
	if (ivjShedNoRadio == null) {
		try {
			ivjShedNoRadio = new javax.swing.JRadioButton();
			ivjShedNoRadio.setName("ShedNoRadio");
			ivjShedNoRadio.setText("No");
			ivjShedNoRadio.setBounds(418, 22, 44, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShedNoRadio;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getShedYesRadio() {
	if (ivjShedYesRadio == null) {
		try {
			ivjShedYesRadio = new javax.swing.JRadioButton();
			ivjShedYesRadio.setName("ShedYesRadio");
			ivjShedYesRadio.setText("Yes");
			ivjShedYesRadio.setBounds(362, 22, 49, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjShedYesRadio;
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 2:31:41 PM)
 */
public javax.swing.ButtonGroup getSwapGroup() 
{
	if(swapGroup == null)
		swapGroup = new javax.swing.ButtonGroup();
	return swapGroup;
}
/**
 * Return the JRadioButton8 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSwapNoRadio() {
	if (ivjSwapNoRadio == null) {
		try {
			ivjSwapNoRadio = new javax.swing.JRadioButton();
			ivjSwapNoRadio.setName("SwapNoRadio");
			ivjSwapNoRadio.setText("No");
			ivjSwapNoRadio.setBounds(418, 53, 41, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSwapNoRadio;
}
/**
 * Return the JRadioButton4 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSwapYesRadio() {
	if (ivjSwapYesRadio == null) {
		try {
			ivjSwapYesRadio = new javax.swing.JRadioButton();
			ivjSwapYesRadio.setName("SwapYesRadio");
			ivjSwapYesRadio.setText("Yes");
			ivjSwapYesRadio.setBounds(362, 53, 52, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSwapYesRadio;
}
//===========================================================================================
//clears all the red fields (if there are some from previous save attempt) so it's obvious 
//what fields might still be invalid
// Creation date: (6/21/2001 10:46:28 AM)
// @param freq java.util.Vector
// @param cap java.util.Vector
//===========================================================================================
public void goClearReds(Vector freq, Vector cap) 
{
	javax.swing.JTextField testerField = null;
	int index = 0;

	for(index = 0; index < cap.size(); index++)
	{
		testerField = new javax.swing.JTextField();
		testerField = (javax.swing.JTextField)cap.elementAt(index);
		testerField.setBackground(Color.white);
	}

	for(index = 0; index < freq.size(); index++)
	{
		testerField = new javax.swing.JTextField();
		testerField = (javax.swing.JTextField)freq.elementAt(index);
		testerField.setBackground(Color.white);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 11:22:53 AM)
 * @return java.lang.String
 */
public String goGetSavedPath() 
{
	FileInputStream pathFile	= null;
	String savedPath			= null;
	Properties fileProps 		= new Properties();
	
	//this is where the file is loaded
	try
	{
		pathFile = new FileInputStream("c:\\cti.cfg");
	} 
	catch (IOException e)
	{
		System.out.println("Input Error!");
	}

	try
	{
		fileProps.load(pathFile);

		savedPath = fileProps.getProperty("palmDirLocation");
	}
	catch(Exception e)
	{
		System.out.println ("Path Problem");
	}	
	return savedPath;
}
/**
 * Insert the method's description here.
 * Creation date: (6/29/2001 9:50:27 AM)
 * @param path java.lang.String
 */
public void goSavePath(String path) 
{
	FileOutputStream pathFile = null;
	byte outPutBuffer[];
	String outString = null;
	String temp = null;
	int startIndex = 0;
	int endIndex = 0;
	int index = 0;
	char[] pathArray = null;
	char foundOne = 0;
		
	//this is where the file is created
	try
	{
		pathFile = new FileOutputStream("c:\\cti.cfg");
	} 
	catch (IOException e)
	{
		System.out.println("Path Output Error!");
	}

	if (pathFile != null)
	{
		//write the file up...
		try
		{
			temp = new String();
			for(index = 0; index < path.length(); index++)
			{
				foundOne = path.charAt(index);
				if(foundOne == '\\')
				{
					endIndex = index;
					temp = temp + path.substring(startIndex, endIndex) + "\\";
					startIndex = endIndex;
				}
			}
			
			temp = temp + "\\";	
			outString = new String("palmDirLocation=") + temp;
			outPutBuffer = outString.getBytes();
			pathFile.write(outPutBuffer);
		}
		catch (IOException e)
		{
			System.out.println("Bad Stuff Happened...");
		}
	}
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getConfigYesRadio().addActionListener(this);
	getConfigNoRadio().addActionListener(this);
	getAddButton().addActionListener(this);
	getDeleteButton().addActionListener(this);
	getMenu_Save().addActionListener(this);
	getMenu_Load().addActionListener(this);
	getMenu_Exit().addActionListener(this);
	getMenu_About().addActionListener(this);
	getAddressTable().addMouseListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ClientFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setVisible(false);
		setJMenuBar(getClientFrameJMenuBar());
		setSize(717, 651);
		setTitle("Cannon Technologies Palm-RSU Config Builder");
		setContentPane(getClientFrameContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
	
	getSaveGroup().add( getSaveAutoRadio() );
	getSaveGroup().add( getSaveQuietRadio() );
	getSaveGroup().add( getSaveVerifyRadio() );
	getSaveVerifyRadio().doClick();
	
	getShedGroup().add( getShedYesRadio() );
	getShedGroup().add( getShedNoRadio() );
	getShedNoRadio().doClick();

	getSwapGroup().add( getSwapYesRadio() );
	getSwapGroup().add( getSwapNoRadio() );
	getSwapNoRadio().doClick();

	getConfigGroup().add( getConfigYesRadio() );
	getConfigGroup().add( getConfigNoRadio() );
	getConfigNoRadio().doClick();

	getAdvancedGroup().add( getAdvancedYesRadio() );
	getAdvancedGroup().add( getAdvancedNoRadio() );
	getAdvancedNoRadio().doClick();

	getSaveVerifyRadio().doClick();
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
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		ClientFrame aClientFrame;
		aClientFrame = new ClientFrame();
		aClientFrame.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			public void windowClosing(java.awt.event.WindowEvent e) 
			{
				System.exit(0);
			};
		});

		aClientFrame.setLocation(200, 100);
		aClientFrame.show();
		java.awt.Insets insets = aClientFrame.getInsets();
		aClientFrame.setSize(aClientFrame.getWidth() + insets.left + insets.right, aClientFrame.getHeight() + insets.top + insets.bottom);
		aClientFrame.setVisible(true);
	}
	catch (Throwable exception) 
	{
		System.err.println("Exception occurred in main() of ClientFrame");
		exception.printStackTrace(System.out);
	}
}
/**
 * Comment
 */
public void menu_About_ActionPerformed1(java.awt.event.ActionEvent actionEvent) 
{
	javax.swing.JFrame box 				= new javax.swing.JFrame("About");
	javax.swing.JOptionPane about	 	= new  javax.swing.JOptionPane( );

	box.setResizable(false);
	box.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		
	about.showMessageDialog(box,   "Copyright (C) Cannon Technologies, Inc., 2001" + '\n'+"                         Version 1.0");

	return;
}
/**
 * Comment
 */
public void menu_Exit_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	System.exit(0);
	return;
}
/**
 * no Comment
 */
public void menu_Load_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( actionEvent.getSource() == getMenu_Load() )
	{
		//This will need to be updated someday for a new version of swing
		java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
		javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

//		fileChooser.setSelectedFile( new java.io.File(CONFIG_FILENAME) );
	
		if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
		{
			try
			{
				File temp1 = null;
				File temp2 = null;

				thePath = fileChooser.getSelectedFile().getPath();
				doConfigLoad();

				temp2 = fileChooser.getCurrentDirectory();
				temp1 = new java.io.File(ADDRESS_FILENAME);
				thePath = temp2.getPath() + '\\' + temp1.getPath();
				doAddressLoad();
			}
			catch (Exception exep)
			{
				javax.swing.JOptionPane.showMessageDialog(parent, "An error occured opening file", "Error",	javax.swing.JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	return;
}
/**
 * Comment
 */
public void menu_Save_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	if( actionEvent.getSource() == getMenu_Save() )
	{
		//This will need to be updated someday for a new version of swing
		java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
		javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

		fileChooser.setSelectedFile( new java.io.File(CONFIG_FILENAME) );
		fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);

		if(thePath != null)
		{
			//go to a file and get the path, then set it
			thePath = goGetSavedPath();
			fileChooser.setCurrentDirectory(new java.io.File(thePath + CONFIG_FILENAME));
		}
			
		if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
		{
			try
			{
				File temp1 = null;
				File temp2 = null;

				if(thePath == null)
				{
					thePath = fileChooser.getSelectedFile().getPath();
					//write thePath into a file
					goSavePath(thePath);
				}
				doConfigSave();

				//we want to save both files at the same time with no additional input
				temp2 = fileChooser.getCurrentDirectory();
				temp1 = new java.io.File(ADDRESS_FILENAME);
				thePath = temp2.getPath() + '\\' + temp1.getPath();
				doAddressSave();			
			}
			catch (Exception exep)
			{
				javax.swing.JOptionPane.showMessageDialog(parent, "An error occured saving prsu.cfg", "Error",	javax.swing.JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	return;
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getAddressTable()) 
		connEtoC9(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/15/2001 8:53:56 AM)
 */
public void popInvalidWarning() 
{
	javax.swing.JFrame box 			= new javax.swing.JFrame("Warning");
	javax.swing.JOptionPane warning	= new  javax.swing.JOptionPane();

	box.setResizable(false);
	box.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		
	warning.showMessageDialog(box, "   " + theWarning);
	return;
}
//===========================================================================================
//This method allows the user to double-click an address in the table and we'll pop it back
//into the fields so it can be edited.  Doing so removes the address from the storing
//vector
// Creation date: (6/17/2001 4:25:39 PM)
//@param rowPicked int
//===========================================================================================
public void reloadAddress(int rowPicked) 
{
	Integer value = null;
	
	//I don't suppose it's really ness. to make a new LCR_Address, but....
	LCR_Address lcrAddress = new LCR_Address();
	AddressesTableModel addyModel =	(AddressesTableModel) getAddressTable().getModel();
		
	lcrAddress.setAddressName( addyModel.getRowAt(rowPicked).getAddressName() );
	lcrAddress.setAddressSection( addyModel.getRowAt(rowPicked).getAddressSection() );
	lcrAddress.setAddressClass( addyModel.getRowAt(rowPicked).getAddressClass() );
	lcrAddress.setAddressDivision( addyModel.getRowAt(rowPicked).getAddressDivision() );

	getAddressNameField().setText( lcrAddress.getAddressName() );

	value = new Integer( lcrAddress.getAddressSection() );
	getAddressSectionField().setText( value.toString() );

	value = new Integer( lcrAddress.getAddressClass() );
	getAddressClassField().setText( value.toString() );

	value = new Integer( lcrAddress.getAddressDivision() );
	getAddressDivisionField().setText( value.toString() );

	addyModel.removeRow(rowPicked);
}
/**
 * Insert the method's description here.
 * Creation date: (6/14/2001 3:40:08 PM)
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
	D0CB838494G88G88GE3F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DFD8DDCD4D57ABF15991BD4D45632C5C5C5C5C5C52D355452C6C8C5E5457E2322224D4AADAB5A28EC432276720E0A0202020ABAAAAAA492AA9AAA957222A028A4283821B2B0230C8E4CB88C020A7AFF4EB977B9F36F1DBB971DFE2E1F4FA3F33F4F731C73BD6F675CFB4E3DC26EA23F544424DCC1C8CC9544FFBFA68A4250630210FD4F3DBB9173B006258912FF0BC1E68A5623A970579BF405CB4626E5
	8BAF77E74035BFDCF7AC9A1B16873FF30497B784BD8ABFDC4463893A0B679FFACDBB1E5797E2BC6974BA6A37CC78BB83A4GB072F7927C4D96E2FF4B9771F1A20E12188458A1E2135EDEA0E295F05D8C628962FB08590F013FD3E84C17EE2C906D5A2F7693A6BD37A2E9BA89C792A6CA6EE166DBA17E55898FA40EBFCC62AAEB4F94FD2CF09DGC2921FACBCB535847ED6B69FBD7CF317252734CB5F5BEB69359B3B3ACB2DFAB3DC1BCD46DE7B460DB5746F961B3E47585ECD429BEC4F207E02D0885793A966D5CB
	6270035F41A0119CEF29A3F809A9AC2D96245D04697AE9C525483B6D5483420857FA1333DC244BEEE26971B5B952B53A7F6B7439348C666AC8DEC703AE81A47BC8D8DAB9C8AB089DE4BEADFB5D226F616F66CEFB0D252747523B59EA2DB69BEC5BAD862399FEEF5C889A915BE56C32588C02404AF7FDFB098A9FADE1FE35676D4772A4E0AD16F5602EBB0518BFB7A6252B64C9CCC0D912579116977EC79CF94262BE6DB26E70A3723AA80FFB2FA00D5BBE52121C2A927718D70A132245F0E2A5F1979CA5E5AD5032
	4E045F09E27DE078D40AF7C8F0D6AF2F21F506DCDBC197F5946BC65B8C645214F203103D3AC9B59FFC42B6A7D5189907E633124B27B44EA1B30BB307F3C95CC270C0894E386C237664BA86F403BDE1E92CFEADD9C25AE9EEEFD8DA99C8BDC887088708AFC8D0AF56B153229F4E310EB56BED766AFE1359E034894CF709EE8E7CB58E5AED7A065EBE3B3E374B38C7EFB369BB4D46BEC134D929A3F950A973AB23F53F8B6258EA6CB55A74F6132597C207F6EAB75A7A184D865DCCE2E3B3D2ED2B2947889174D8858C
	F7330E24592C6F336F369A74F6234302699BF424CC0E6B77C00890G7CE67932F3A9492FC8C87FC410F81E8F61B43CC6232D57E85E383149E2B95AEFEDA5A9916AADA41FC7533EA393FE97DAB00F9B8B896685577E968C679B5DACC9B83526AEC2DFEFBB01EDAC2D0E744541962CB37FEA43BAD3BB7D8E216415ED2AF506C8240555150996E55B4926E192DE6C5A27366D9C5BEF30C53DED8D5FD7BCF84EF1A41FE778B3F8D9BD3A0C14D536B5AC2D8424490AF93CFE310E5559EAFD1F91EB69CE9BD4B023411163
	0C6717F505E2FE929E29A2FF127753C0481814C56DB0DD69DB083D816272829983928312F28CB80034028C01789F433CBED867FD0E611FEF9E7C9EE15CC6D2EE43714A799FF3E3E5D8320444EFBC8EA3F42D51EA6CB5400FBED137F68961A976AB07AC5CE783452D508F55C0436F6378AFCBC8BA74F63B4D5459EFB736986DF6D36FE107FEAB0D2B2F3F536A6842B02E9DCBC85ED886209F6276ADCBC89B6E3274769AE9E562AE22FEB76DC30CE6E38F102E3174775A997E5BD2528E0F9ACF08FD584125E4B2F6DC
	EF6EA77DA11B3B8469C97EDBED269E282224CB958AEC24FF3937BDDF12C76320FE11B9D89C7CCD31E1DFB130947B0A4213378A218717A5290DF5BEF754A699EC2CDD8F5A9CFD856354DC284EFD8F6D4BC43C0FF14D5AC338CA26D1E23B7CFCB9894387428E848937E13B7CFF05C40FFD1F58A0EBACE6B34BC61E3FCFAC4BA665E1BC56EF04A103F4320DBC4F1EDEB6CB215BA3669F533F380CF0B2CB7A6756BE12FFD5DBF0DE9A83FCA601647631BCAC6AE3F9406CA6F3BBBF388E6EF358C54B6C6E5B0AF6A97630
	34823B432E52AE355357231D8170A1091D2F4CEEF6835A05839E8312E097436B175A3D4E6DBCG7769F758854B6C2E5806F68900679EF758159F175AEDEEC43BC1403DC1020E0BF5CBE6175A44677583B02F87318FB03B2081E6476A4464F9241E523977917DF17DE0E95FG34350D9B37C966593377E708FD4C24811CCF7FFC145419364130B4AB0867A056693FCF4779746823FE42785B6739DCA7788DB29E61030EBA2D3B84675315C5A44E14931097A730BEB6AC170E691B7B7AACDDA6BA1D906BD27CF2E91D
	E758374B27425FC3A6DB1F3D81BA25C12CF749AB88DFB25F90E1F17EE52273B212B7C590EF87C8428902B7E764CA5BA4E061A093C112E9F811DBC8FF2C278D23D44C5B8F329732FE67106970BEE35FAE3D41E4B938191A54110E322F28F88F4EFBEF4B29423E627E9B044A33B9C9EA6B904F2C85C935A7D9FE19CFBA72ED6821346458835F2433B4AC590AE988861B38131806489C35B4941D42F518E751824EF7074585DDB11FDE65F1D4023F7D9446B17634B40EC04023C062F99C665FC8796A8D066AFE3B5D52
	6BB27C4B6BB17C4CA1D8338DE178771CD58BFFE88843AF4AA2759EE63BB077D40F02057FF383066F8D3E91BC7C16CEE97842E9489F102653D84EF7B42CF114B37B53EC349D3258FA0C06A25202CA758376D2BA8E16CAA2AE46769635037479D0C17B8F36680F9BC5F20EA01ADC07318939A7EDC36E9D404B67B4F25F5F2546BD72B4664D4E9FF85CCD963D412D389F61F1CF00F08B4EE05C078DEAF1371E4138A3FF64F1578D1A6CEE45FD2491636E06F07DF85CB7F6AB630EA63AB3D8E6FFEF7CFDE576CE6DC664
	3E3953526FC4FE75B06C31CE17B6A17F6C33300683E1F305EDF325F3858F407DC1A2C0E24EE29A2EEBDAC5B8D87A7B0C2574FF98463B0EBA735FCEF4C89F67A8DBFFE4ED4EE66C6B23036C416D52A0146C9DE19072EC6EB9EFBFD99FCC82BE999E4EEB0C8285D626E203D22B503541B5DB93BFC447943307F25D9EC271A18F653A3D76A82E5B3DC09767016B38670BC9FE36DE84E39308A7089FC8A848780BF0AC9935E22DB04CBA392197162F3DFAF30BEC22096B55A292AF4C8D456B3AA21257F4EEA860FC5D32
	C5F98FA461A2F95A73AF16DF0B77CE1C7CCA9557158ABFD64EF32A49F823B216532E1C750602B8BF5DF0DCDA67DAA13CC190DF10A090B66EDF1611A1069DF1B10E6F2737C84777102CB5C9E47E26B63E8B17086353A50EF12A045FAF6BDFC97A698E508D010C86898209820983499D016574D251CF4315932D292D169A406AE19ED9231C6773FCAD5A6ADEF92CF772D316C77988399F4B3F74DC4C3F2691383E0CF11343C743F298B1C22DCEE4354CD52F93D89D447A70659EE5FD700570C2C122C6E0FD787AB426
	A71627473B5E3D745C6C6427CCCF6125EAF97A4ED964D0F9A9F228EDF00F43AEA73F050AEB7B25EAF99A11BADFBDCF37439051E0B77608F99AF9D01A27DE90D690C8944844CBB1CFAF6F447488A3B1BD995B5CCB4F9CA7BFE5FAAAC62A656968AE64504CB90438496139E1B9F82AF23859009C7CB887EB23FB9C4EB879A9B9C4ABB83072BAE6D4AB2FB830CD398C4B6B7FF5A34F024B10E7F913FBBC2B1D7C14BCDBAFD32BFFC7CE39D83351311D256537B9523418AFE36D59877E867234DCBC172405746B611761
	3A6E759AB226A640F52EE7D8DA89883BDFDAC9570035F05D4D31EBA9B68457239DF6878896G57A19C9BC931C8CF161F8C3B43C4308900A5F03B270F90CC7787D8737E8173B618BED32B6257E1745A402F379DBD577B1667AB0EB83BAED89CE48E41463928E257774F5EACD63FFF76CE317A7D338F0AE7B29C42105E65C77D27455A77573EAAD63B3F360072FDF6CE968D3B2F4F58C7875EDA3DDDAF6ABE304F96D46E336DA0F71AD8DAF304BB37CC91ED6A6C161A553CCEFFD16C22CEB76BE1354F6A74C3D66598
	6589E59488927A872C53470EE7086DAB7A8FD8279FBDC96FBBDF8EEB92102A4B31DC3DB2541ED135DDAE2E2FAFF71E433239CBC289D9EFF8005E97E48C48B810584BF14EF24577434EAD6B86B56616E7D73A2E8BA3D72A57852F1564BECB1F255F56E5D4167735AB73394EB1BBA28395A99AE663CB7D6DFA5BE123D3FD391D7AB31DAB7F40152E6A94AB7703DB25653EC640BEB0B3D72D8CB2C709F346D14EE540424B291706C7721B5511B52DEAF5249C42299F05F5E455DE2CA39D23300EE41E2677C061BA88A4
	E29456117F1CD663B7DE64B549A53FE7E47C4AF9FA5FBF2B96DE06978B27404BD5F8279A24612D636DE6CADE1EFA3BA1F39141530B24ED4C814C13B2382E976330FA31E7DFACCFB2BAF0DC0B7442BC39C1CF72A4630A30B49DC8459518A7D7F5AA53508CBA43952E57FCAC8D3FEC1326E1BD4F13BFF5A9439B04303C068D6F8BD9F8BF7270EEB7A843730330468C9B5EDD0D5270487D8A16B7F79B79BA688A4C1B778F133C912E8CCB4B87A93B92736635EEE55C7520EB3FF238380FB6C9635E404B77F05D827572
	ED6932D94CE652BBF21E574E41FAED3D92F9DE17CFF88640F5A8C8B467F959DC42B38E2ED32EF266168FD80982E7EB716D74D9F955D5ECAD1E4871BD94EF1260EC0D7EF68D6E79E883DD48D5F8BF23E5BF99AB8C46C37AFEB38CDF16BE63CE2B78681858FA935F5E7D186FBAEF743BE7BF49A71B314FF412BEADC05BB0EACB62716536AD87C8DA0F1B7ACC4412599DB900F6C15C6EA60A5196D32D3731FCFFF0002CEB8F5974BD105DADD6E31750F6B53D97E14236948E7E51A071A079202B389A43FB6B888943EE
	32D3FEFED713BDBC6BC897A0167543E564B93B64B9CC8D596EC246EAD2182C9F7B6228329FA3EDF5820495FFB55EB73277CAED322F01B688FDDDA57CED3E865B75C7D64C436EEB30CE0458689E99388E86093C866B447163523ABB917004EB9CF5A07BDA157BBE346C6FBEC542AB3C16157DE20E07D15CAA41D91DF8020E4764BA84F49123F17EF7BB3D5F90F59D2473BA44D22660BCB16EBA9CDFD3D6399E9F272DD29F9FE72ED21FAB652D521EABADDA65EADCE37C1666A2675C6B117308DC642C3B9E4B61C8AB
	16C36D75D88E15FB68FDAD389E8D92F8BD16431A3D52F28887BC7AFAC75E47DE6F2A9CBC6898A67C1165F7AA47C7D1BCD70233F218F51ABF6BG1D579F117F1CB31826B80E8D1C6173F50EFDFB966DF4BE089D90502E84B096CF4C816C831A81737441FB4297EF18275E3709AD916AA04D27398709FFA878C5034479603D284165F8AFAA704E9B04A60FC655E7CD63DB57A4E57C0965D9491F9C7722827A3FCEE779BD3DC359E7F7035D501FDC7759ACDFE7F492DE5EFFE2791A4E71F9948F1060AC9F7C68F8CA2E
	C3C1D7F88366591D749E00A7DC7BF26C23CE4C5BA00E85F521DD8847A6F521DDA4479EB320DD8C473EB370B23D816BD96EE127F5AB6832EF9CAE5D7F6D266D7DC665FDDA83454BEFD45E270DBD0C77E9EBC197F0A3F27AD7B7724C74C5EC8DDD976A603A4C176FFF1903BCAB81EB761573B3783AEA8FD36612F887FD15BCF3A86E692B64F9E3BE727481DD8267F4B3DDA7F75C446C9996C231A140FCEEC23B1D55D87783B9F632062EFD61BA6EA6CCCFCB2DA060DC25DD727B1FB597EA1DB90347715D850D030DEF
	176EA765A5E95225FBCD3D864BG8CF3E4D7193D2F34362125F9E7CB5D411DBB8E56B4418F561E7D8624FBCCAB267E283A9F02C84A4D2C4C73453FA117AD95637E71G71B924B777B9561C9749B0569F3FEB42F9C0B30461F9B341320F64AA5212174A6ECBB2FD35CCCF4685B267F4686DB2BDC9530DB27FBBE5FA426983511F257F79E5581FA5BC9FA0189F5E607259F924186EC9625F66BDCB05424B3FC9E7613C741BB49F33EFD81C1468A21C12DB443D3A37B87AC516BFAB4DB8DE19EF41716A099E52BE7C61
	BA84A46A966C47D7E6381823366ABBBB0D86599CB53097776C4672F03F31507E448F66AEA02DFE58CEBE35AA7BA7331F232DFB7839EA777F6EA7610D76D30E030947E93F6C279C8743ECB88E0600CEF7AB365DAB683D0491F06D43314CC1020540F5A447C61E40F6BF69D6647E4029AAD2BFEF03F190248A249D448A6289628F928A928DB289A47B763034A210AA10D690AB08576D98D6848DABG2EA3C0A602E47803BDC83DBF5B9B538DFFBD7D597E17G789B81B291A4638E3083293F8343FA6AA42988675A1E
	89DF42077CA67C8877F3750D96EDC95AC2455F6D6760DF25E113AF66972D3B37317CD265AD41C9BE7AAB42946F331FBA1F3CA06B018562EFF29FE87AB97A9141BDF9F902E36F6014F370CB575011B6B4DBF074A573DD58AD127CA6F36EAC15F0F2040DFAF2DF558E93E736DE69A74BF3F14DB3FF8A2EB33A219E8D01780204004400B0FB1F9F9D76B76622FD8268336F04319C249944FAA756C3CB2B32EDFB5F69A8A37FBBDD356DA0BAE78E3ED3B9678A21F8641D4AB9D7759E1CF3CD84DD53DD58E69B76E25B8E
	625858B6EC5BD19CFB7D94366D783B107BC87A9C2CA0A0AC2DBCG312B99A66E99E3D87E696146DA62CF42B957FA64DC07D78F9BC72A605E1EDDA22CEC47D7B84A764AB3D836CD403B834483448FA4B4G6BC276C607FD3F007663C1BF89A477EE48B710563BB11F5EBD605E7906387376E3F5658A3A1E305F2D9C9FFCA86EF937F2FCD8F4904787DF50897760BE03AFB6123499605A834487A488A492A4769EDC2B8E151789435C8BEE35D81BE0B1EB16ACBFD95DAB6C407931F0AF564725FA3E5E3C174F6369FA
	C3875795A04D9C57D11C6CCD74917794B27CCE7DF0F5060461CA2793F595AEECD89CCF8C9B47F0FD9B56DDD77A9CE1934D1F1EDEA3E42C290B5D5FF9092E1B483C2A7BDE2C0FD7D4527AC8752F5375526BA003DC9F7413C37F9EDDA7FDC6721D6BDF7E1968D97E2FBC0CE562434B44F29857AC01BC6FBB3A6939GFEFD04DEFB73B2787C707F75BCBCD8D29EAC0EE95D7F57F1C87B9116DFF9F49EBD96A472DE3C7FB57230FCAC19D665483F8768BD4A71E01B9208791711C730FC382E62D8E79E763B2D0118E7D9
	F3C938037CFAAE3DA6FBDB59752D79671ADE924679261D457158B94761CE1FAB5E9F1CCBFE6B7B6D16DD64BE2711749628FBF9AE19F7D8ED46C3C61B4DE8E881B5AF0BD83AA67506347902307E77D0B5763F91004500A400945C87F36D7B303C2E2EA1F3123EEE4B409E234DEE6A521B1BCC3DE40B36986E3DB538D6EA3F8F7B3E60AA02D9615A6BBEFE6626FCA57C5D23BFA276F2F586135DE2235D9C004DB60B9502BF41D03A8BDC4C538FCC6E1DD3FBB29D57CBE39E3BCD889EB9B95155DE24407BD8F90F937F
	CEECDDCAF171EC954D238450655F8F7982528A528D62F1BF26ED5ABA4211BC39837A1B6DF6FDD7B75DE50C1D789E13F14081139B46AC6C3A9A421405716B3AE52E7287C25886CB271D1DC70CDD76D67DE1DE5F4A4C4A3C99FD3F236E865C6FEA1E343617747DA177AB6FFD7CC471F177AB6FFDBC550377BEE2C0D775G362987FB354A467F81D771DB2CF41E7600F20CBDC971F18FA84758C4AB0E3163C1D79B047DC05AB11C1385F0EC2B8D7B06F1C148B3011E350D036B0C8791CB276BB73247D94731881BD67B
	A53EA48CF77BC79647BB1AF17C1E7918230F70BFCE4E22F4DAEC74E6B5FB4E77D73E272F9652518E628D627FA05B674D7C8E18091FD9FF4252EFC73FF50368978236D1A0998FC19F719073E37D48CDA750269C70FA10EE90CF100007B06C665DA47D5B1A741DC673FA116772C687762308F52EA0FD58E643F1F24250C0D5BCDD49C98E7B8DA276D19E299BAD5DC6836E22E778FF8FE5CB42116B52FB88579A595EFB0CE356A08DEFC0EF157B5C1E19A589CF5C81AF7A2469277370D00361BD10BECD64A777F9F749
	B4B19EB97E4E328C1E4655E58CDB5AC242A7DB42B713FEDF96FEF3CF1628037E59F46804B42C2865B3C45D2F7DA623DDF4E47B22B30624771BA244720CFD8877CDFFFADACDAF7C99752F1ED55367F2FDD2121A3E086BFFCAD1BBEFD0497505E9EA7AE62EFFBCCBCDEF60FA7F19EA7AC12E7F4BECB53D97575F1C272677637ACB672A25EF8C571B672B79C7F0FD4B82B5FD8C57EFDC24260F7BB3BEB35ABC85475D14B1F80F52B2C5F90F126C73AE95709E632DD31477A86F1569DF1E223CC7E91469D327AA6FD13E
	1244740C531339482912F37AB4D71B5362595A1CDAE6ABB96D1569AF1D2D64D4174C74627D37D69CA36CE3F00CF8E6B70E91BEE3306FFF045E8B88016BA810D80E07D11C1CA349FC186D3DE7F87D1E6166FAA48CADBD4665FA2C508F9B0756BA89735B55BA09756F363DFC7F4743ECDC28A7FF9FE6E7E718CDEC9B5A589FE6E3G796DA55A30B1A06E945A78839E8C928D928F127F880E8117D510725B0C67FB846C43EAB612BC2E151C4BE175E94C992CCF950FE0FDFA770C32BE2D1555075C334A7A34C8265FF2
	D6D91F6475ED702C32BEE1FDE51CEE94101315F3FAD65066740D204DE9D522B6270309CACE9F0BFAD6CF2EBDC877D5821F50C730FE5ECC31A2713C8B43169DF4D51F082FD6BD8A9DC66F2833BF3B0CE338F90D2BFAAA95B6D7CE58687A99FF72A675E77CD3B629BF634F5A243D5FB1EF136B73448BB7C94F933375D0863D877382C874A3F80F608E7A9C0F5575B1A7880F6DF8460B634FEC64B8B9FF45712EF20E1333D19CFF3F137CDD5137C23C0E6B2257E63C7E5AC02F87D7083CDA69FD083F03CC643C7ED167
	6015DAB6956321671890AFB551F074986E53F348FD6A6362DA10EDB42A565B1AADE6D35789B12EF768BA1E341BF85E76976511B5CF3745E6BAE96935BBFBE27EED22EB7BDFC0AA7E02FD4B09BC6CDB8681738289849987927B978C3F679069C3B6CB4EB131B4EE596AC8E3EDB5E9F73D46819DBF54D4EB02A599F9E9C4513A35E5A5EB562EAD965B39D7B5BE634A8C46F69EDE2DEC672FA4CA5B693F2B156DBCD1106A0BEA146D3CB1C52A7FEF0D321D23BF639FD6A3FDD6496A6063D5B3247517AD1DC5DDF89589
	4F54EB321B74E653C93A012A858CFB8F3375671CFA222F0574F5000CG7181898C46F5ED7D66A582DD572A2CE96B8C070D784C163F7F235AF53BDCD3BD7C7BC3B6D513BA58E901912007044F5F7FD1CD7A963B45CAC106ED2926FB3DCC073B6D4CD4441BE839190D07A8AC2E894DB8870888664F6FFAF08E909B0C7D6306DEF24FA87BD13C2E21571EFC2F7773BDDA63B1719B2E7F9A3E7F1C2099476FD99B92E1E90F68657BF0F8DA7B69DE30E6386E66188745864573BE623A590A7C50767770D14F839D0FC3EB
	7E622E30B94D8F4730CF71F854B12771F9141DF58BFF14589C3021CD10CABAD91BDCC37731119B787ADE7AB45BAC4EF57A487A2ED9FF98DA9B05453DFB0B972B1D2D4BFCACACCD77986E09FA6FB85EEF29G4C7A98BECBCF5511FBC44AE76925EC213FF1E31D3D8B1619556232DF6C47D68E60FCA560B1FC263EEF0004BF9E2E63C1327F0A716E9F443DD8C500F57C9563BD3C0447CB5E41E56A6CA96DBF5ED32A6F34179A8DA67B460DBB7A4D66A6713601986F9D03982F4FDFB15E17E97811649AC48841354C58
	93E81B9F02FD6DE7A714FD6DA8D9DF3B7424322FA5E306C33F7B24322FFDA1D92A3F6C24322FFDDB74974F9C9CF2F166002E4C996FDF067859A81E1665E790736458F481775E06F2ECC392E2719C332720EF5963087D1606F65D9C1B14454FC3F16C4D197CEC83477EB59B6D327F0658ABF90855F30C6DD7A2188747EE1F0FD8B047AEDF00D89C470EADC22C68896CA336CCF16F195D532A7B7D07EF4B8F70FD7D9DCFB85A7260932C5FE2ED799F3B51467B89D7ED7981FA0F182DC0D81BE5785ABCB2AE55755A49
	DBDC88AC62372DA775CE1CF34876DC3336FEF5225A39C9FF08BF7889ECF3B76C45369E89D8C6A8666B3BFB31CDD405E27B49EE23FB56605A9BA4A094437870A4365BD04062397DF25AB715BD89E39EC8471398761CA18CFB70C9ECEF35C34A76E611352763C34A7636CC561E8206146D6DAF32767A4610323DFD19A8DD43BCFA463DBA63A71C2F9FAB3B2403FCDE3D45E4F64C43677D4C71FA23594A71C5FABA8FBE047360230769F5B7DE474FA11B38255D32E497FE691E3ADD2D8DB51B1B8E36546C5A5954D43D
	F957414DAD87F75455555655B27FDDF9EBB47C6BF76EEA786F4E9D2D3290F654C130585F5E1247772E73B2FDA1CFD926EF4A4A64BFF94AB21DABAB7355794AB27DD5B6DF6D4AD71669AD0234CC2F2E166D9987DE89CFEA4F9744F5D53D6BF964327A616711257574DDAFB07FEBBDE1954FA02D2DA77736AD078E759979FC3522BE1BE38DFD4D16BE9319A133BE255F0CEDB7BB4C5127940531FD74D5E1749EE3CF3EC6DAD839669513B858FEFE4716B7362EF91B6F51770470FCC1C2C0E2C09242F0DDD3C34F5033
	FB4B52BE69266912758ED5303EE06FB16C8B8A460625350E453EE0B4453D605A9FA4E4AC7685A3EC58FFC70D453D53917B25FB8DF5ABD3D4779A466F7FA1A9B31C65CBD938E30FE0541104F496763E45B8A6994211C797FD4F1BAFDC0F8199970EE346074707EB4793425576E9FDB52C1F725C2C786CEC403D7DA1AF5039137014720C4B2B944FFCCAF9466560G16C7BE68061E6267C6875D6B6F0E3969F74FF97B31F4EDBDC1670ACE69ED26F82CD3FADF67F348F85035BF4D1F2D1DF00FF76D101ADF68532CAE
	C4BD6D5C1FB03F152A6349C451BE41255FAC5571A47B99E6DF88FFAB1ED12B07BFA72979B501ED87087D99D7711DC8D17373946DFDDD7AF52429798509766170B7DA156764ACB53FD851D608F0955F7819EAFE3922FD917C2D0CD00B6F0B59EAFE4D222D41E5FCEF6429798D0A76DEAE7D1E1C2B6667A75A0FF16997B0DF4DAFC2340FF169F75582B53FB851BE65D9D7FE47972979650B76A5AAFE2CFD3D560A6D2B8AEC861F45FEB5F7B7410360BA82E47C33583F276C417EBD1EE34FA6E23FDC769CDF1B25927F
	EE3876G71F98E5B2D5FDE777A657B0775D36F17BF9856CFA92CBF9AC16746C14FA97B29ABA89E721C321F1A53067998813A1267793E72136E75D35F1E379F63F7831D23FBBF2F64FDBB45FD1FD772AEB9053C83C017B98EF94FD76DA707676D2F5A4E34045DC38A2077592C90BF390749307BA866899839AF4A785EE4C63E7E00470C437A768645F3DF003C8729FD81679EC70E625C23605E9B69FC4255B9077697D83D73F841B17720B88DBF550273615097F0BE3C56221C8F3FAD1B4F9AAD4A79B031F1687FE8
	D14E072F131D61F856221C8F23BF4B6B7D475CAB2323676D476AD086BD3B907F0232EE4D24F84A0B4A3AF5831FBBE6034E7EA2562DBF582471C7839E7B2276BC1E5DDF0EEBA073DB612530348210AA10B690B3480817709E73F375C5026BFDE16C26923D4FEC531BFA6D0C4F0145D2BEBE90D660CB8EBE21AFB9F3E3693E043E4BB46AA5166EE20E7BD13CAC52010B6FFB24F3D5F26D853A683F072531F7D9046610BAE96AB51BFA0DB56C4D563C2CDE9E90786F9849EF634005BF3FF566FF904776930EB46D117C
	FEFB7042479DFFC163E0FD58EFC338073A70E5FCD690B204774B4BB8D64D4F4136F26CF6FE8E3603E345825A0D6058657C9C2CB7476ECD44F7630C61586EA4740D6058A38C036BC99C1B1A0C61E5C4A1661D023EA59C7BAC856DAAB8B61006F66D9C0BCEC33BEE0ECDCEC7FEDE9C4B4EC6DFBF0E9D4B66679CB97655CCFE8E15E3ED3350AE06E3A7F2B08E61951E36B9FCCFBB470E4E61E728B976D99E5A55F2ECD7BE5AB5F3ACFAAE5A59B936E19E5AF9F06C0979E8674FB1DD815A85F16C4E85E89745317405E8
	B71EE3D7ACC23B14D7799957C5E8177DAA7635EFB91DFDB937367171F97B313E4F134E9B4BDFD50E91D7D33C6AD56598B1EFB70E91CD208B623C276C11461F7BDAD8DA51EB5AE38473FBB1D16AD789BE4D92BF03CA98629E52B4FAAF74B5653793AE22F80084975725F4FECB2E23C1E70FC65E1713AE8577D1C8FE9734DD787EEC5D7FA08E3FD30EB45DAD793D6654050F3B7102464166D7FABAF60B4F38771A8C76EE06978CCEF76075C6721C18733A5DE95F74392567417376E3F5F2875DBF1D7D3A320D6D25F8
	416B4AB6B61E4F190BC06775BA6E2BEFE8A47329B8384EFC8346A210FA10F6907B9BB81F1A55C0769E391ACF6D32D8681BC448CBC5E0BEE534B926D6E29A27DA5C4B1B99B6C75D4A137C76BBF6616B592397B48E56D77B59F03D53100C589DB6DC6FDC14087714A7B7383EF7ADEC9B7E5E7514ED6AFBA03236B96F01485D263E87E25EB667BD907D0B71CC23679BB87EFC3A8477C584F22C2C90472448B7787CFFF9B37C1D845799EF42DC9E248C2496248D448C6281B29AA4GA484A48AE492C8EECCD8DAB9C89B
	4890089FC8B8C8EC8CBFEFC6434EFE8B4284E9863102F8038401C4024401E47F8374A04DA0D690EF10A010C810B8106C37C18F528CE2FD1B1FA3DDD6A5B9435BA6F2A43F898735320EFA53017B09B631625F0CB7354733E17B82B14FB03FA6096111FC49D57089D0092BCC444AA536EDAE5221A6247C4E17B3A9CBF19F2CACAF278B0EF34BABDC784E157CDEA67EAD967F7E5EBD9D0493F9AE025FB541734DCB8547B9EC1459AA7EF83EDAF97E79DC63F7E79F8E59B74776243D5BA488F8867665A17EDC9A6A2DBF
	C8B8488410147138CF6F729D8E7B4778995BC250D70034018C0278F07B7BF6BA6CEF65E7EC03C1BF8EA496A4739DD8970030BDF7D109F82F24699D8CA377D7C140E7D8AFA411BC46BDFF1B4D26433DE4AF1800FB76BEC8C27F0EF7F04F5E62E41EAE407CC142C1A600243C0BF17C384B41B3A315278B7495A0EDA003A0BE5C7E3FAD8E7BCF53F83AC0BF8EA496A473BDC857FB182EA599BCDD6FE19827DB9DE1F8E673FD832037030C86998392456D7DF6BB6CAD3350FE9268B3A6409C97249E24FB8246F9DB8E46
	69B98143983B479146457C5D2B3E208F8209840983497EA75A3F3F57E1EF4A677B33C1DF8BE2G9981624F6D7FAD494766F97C9DE4200F86098749FF9F7274FD341FAB49039F8B503E8D74E690AF10C010F15C3EDB12878B9722FDAC68058FE08D86D2855271815A0F1224B7D54777D901FEB4489810A810C99FE05FF2A58D1F7D7E236437EE77051FBBEC3820F1086B46BDB8F798150A5832BDB8F778184FA77E1908E3F46EC49C0F8FA66398DD41B1BF3E0EEF1F48776F2511EFD9F94075E810G109010C81089
	A04207D0CEA0D59F72F77CD17B8E3876G71838985998FA2FC8436A0D5A09D9F217D5B295E12F411F806CBBB090F7C1D6042B6D264B8DA218FD0DCD7FD286EAF70342425C9391DCB5F0C6F59F0751E8EB23610B1C2B9CEA94FDB387A1ED6962FDF2DBD0E7A3CDF727BFD73052F5B7F3920F13039662EC657735DFD0D434FF7BB9B55673B5D0D4E735D1EC67579EEDF23F43E4B76EF5C4C77D9FAFC444E3578405F400F58F9CFE6133F1477159FB976F84408B62C7F5ADA487BFC40B3BE06BE9F249E247BE35C2371
	DD39644C5EEA06A5EC12E0EB98767DE6893616E13B17CA30F2F1DF59C98936CEBCB710AA4156B3EC41CA8976A3433C0AA4D805F85E51A041B6B0EC7CDA8736C6BCE738F3965D675623B71937180CE6830E7F37EEA25F69E2FBD949DBFDF7769AE57A5085F2FD6B00C5265F31BCD72E6F3699D988CCFF064E0D18FE0B0DFD8E6C840F04591C354D66B6B5FAAB396D6E20A15A4CDC25928EA743EC4A1A1D42F15095EDE6D74FD589071366F6C949F9CEE1B92C58B93E0F16AB4F89527A77B11E737BFB111A5E176B03
	3FD75387F17DADA5EA7AF02E9FD92A260F667A1EB2B5FDAC57372ED1538B31285FD42E264F657AA56B5574C5DC1FD523262F647AFF57CA754D31CCBF4875C3DB5474DEDC1FD42F267763FA4F86B57D982E1F5128260F607A51CDEA7A982E1F37C3CD9F4775FEBB5574A91F20FE792FEA7AFC2E8F5C2526AF79841F75CF66FBBD2BBE416720ABCEAB1F034A4FE46EBB2DFC8EFAD4269FF5DA799C34AFC92AFF7C3472B968B2C1FA06EEBCBF57B748B94DB82B4D294A69DC5FAA195E66F42E2FC546495F69DC1F1C53
	B37CDCDF906774255339BEB9279509CACE9F4B747B1C4E759D15F19A65F42E8FB9317E6563C4677795FE17C4562952FD22AD64FF476B93EB6AF63412ED21E23A36A6E33AA672F475A7EB276B2EE4E53A646791DFCFD6262BC7162EE9A95AF9DD1A0A1CF47FC2CE6D295A1C2ECC55AE7F50D4A527AE197E73D4A5A7795965596948290DF32AC95766541F2EDD7EF725ABB99D1467E33AABCE2C2CB7E4B83F1B727EEC64694DF946E4EB731CB1D33BBDD74DD472B4497436195AF9F75DAC649441B90D1D254D694BD9
	5AF937A2C749E927CC3FAFC749894FFD333CFBB447B96F8266A0CF61D364796A9CED1E99CEFB246575EEE31E1267BE195E1C670AA763F4D5BEBF634CB9BD1E2F4D6953FC6D72DCBC5729AF1469DB66EAF3BABE0F1FF1661C7C67A9B9AD1005796ABCA5279FE4FB4DB366ABB9BDAB53EF1C2F5DE76CAF606718B827D1855A7974F801B627CF8B141C7472FCDC6014CF32FADF3B90B90D671CEC8B35B979AF5466746AC2A5A7F959E6ACD2F2FADC46E9250E1F250AC3CE7BF45A1CC66934B9BD2E536E9FBE55A9B9ED
	4233A1F41DB5A38957D04DF1ECAD568DFF076258BB95180DA73FDFB8BA4E31968B94ED58DA6CAFA9FC3D86F8B4C8BCC87EE7E1E9951F61DAEC69A647BBE2D60BFD4137E79D589A913B3C5E012D953149739CD8390885ECF5E06B70EC759A76EE993A771EACE8A4EB998FFB0EC357423E06A2DDFFEC5AA2B9B3C40F224B5794973765BA7435A6720182B1GB65F8B153D97054C17E97EFC0673413BB25474E62E3FF22A1A5E036B7BB3557423393EFD061ABE006BEBE4E391190F92FD8857BFBBD3CD9F4975BBF354
	7493387E15DCB5FD7CE7F8966A6945F81EAA737330345A4FF19EBCE389360B214F79BB7617905BB1F0BD8EE4426798C655D2FC7E1AGD87997E8FF1A62E6389E81627B85065DD308E10F79825B5C0D054AB6F7D8B6C7FA21D05966BE141F4DA9D43639CFE4FB95D7AFD336396D02349FD83C82777D46F21E99450895CEC26C536F91EB67582BA5080D66586325080D6318FF99E2A9DFA2B6EA8DE2159C3315A3E667583E750885F2ECCB8DE2933E4433274B0A4837A0555E9FED9C3453F6A1FE3727AEC34C6702FF
	E3F8FF5E02D8AB47B654A3664531A79A908B67D8DDA3E2899CFB3E8931722F907BF587E2E60E3D3693B1FF0E6D7F9531980E3D378B315CF83C17791D1B7BF87F69667939F167FDFE0E79B5A53B6737B655BD3FF9696E797DB95BBD3F9BE63967F75F9C777CC6673B67F7EA1EFBFE1D856E79B5ACF44F2FCCF73EFE2CAFDE1F047DF9933411917158F64E70E70EA17158BF87D25BC9F01DB9B9AC2DF0B27667C149581FD782E61F0C761FD0BC902E43C1E2A6E358EF25E05813A6E3FFBE4DE9FD7A32A06D0FFFF2DA
	1FAE12757796277569EED9FFFE2B537A944FD2B2CEDF25E15FD872B5727C877F862F01E3CFCDC54C17E301197C0C9F472E1D01D866B7081D4C467E2816E342AC4406B8B6B28731B19C4B1C0DD86CB758170DDBBCDC192B1F9DF9E3D8BF7533A3B9CB5C5B1F95C4678105894A7DD90FD23CACC139BF2BFA892FCF2073CB60675E173A57B6DA4F5B0F717B0C4EC7D23ED5727E1A62595FAAF9DF4267B58520737896F95B8A5D633DFC051A5F04EFD91946FF6B6A6C5374E2B53F4C6F44F39C5F39721B743D1ADF39E8
	DF6F526F4D92B53FF6515E6A52AF2CD44DEF04E86F63526F6EB2B53FC051BE54251F779AB53FA851FE22CB3F01F2B53F8451BE7BBF2E7C8E2CD773AB946DABDC7AB556281EF5946DBBDC7A2D29532AE7F617FE41CE5FBC170A27CB3F1F1D3EA3AD95DF17FE61CE5FFE16CA10CB3FEDCE5F9616CA38CB3FC827EF7DCAA55A255F1E9DDAFE31AE7DE2F6EA798942D897FE1D3FEA79653A741B38CB4B2F4825DF256A19DA29280F9F5B0675D39FBFAE9956CFA92C3F5551F9C4A524C35E8F97D33CDE0233FE7821A46C
	07DBC1978C4262FF444DB9F578F97BB1FE7A943A7FB8D1497B88450B92153C3F607328F25079A4A26FF7D54FB08F4FBBC1758CF3C2A22B8F59C92E6A45FB2A5F66AD946DABDC7ABDB7D5355F926DBBDC7ABD182966E7976DBDDD7A794CD07373956D03DC7A1D55DC33043B74CB55DC33C43B749B1523590F38741B29FA369BC5C8F6657788F92D1C787B89496F7AA597FECF0C7EFF9047D7CB9DE9928A9D3F7F30744247FD5B850D036DBF7BE985BF1385E5ACFE87EF85BF13453142E2FEA60BE3179673B3D99C1B
	36121F496218F9A5BF1345314FCB7819AC0E352C62E732B876FAA9BF1345310DBF70B3D99C8BAD63E732D290DB3C1A1F496258DDEB502E1CE399EB512E0AE3D716231D01E31F16231D15E37D6B51EEB447DEDD0FF6FE9C6B2CC13BF00E0D2D617B73B956D00758A40E3D3485317CD444F657A3D64931B79B7819C50E699B919B41318F1A908B60D84F8E4446F16C0B1D0845F16C442F086526A1761FDD7CACE29A0EA34B139DED6587496F60248B5FEE7E7EBF0803FE7AC47CED157C1E1CFC61631E15F2617B051C
	F4FE16141733BD1D1FA56558577CDB3EDE9C7B0DFF4B5707E36FCCC53B900ED5CFC33B880EC5E422DDAC470A2723DDBC479E1801F6856908E5E721DD91472E67E7D81BB8D6404F2BDAB94676FA93ECB4470AE6A3968C985B8FFD3FE4BF7743AB7099D68C6863C032B346261501B4E7207DDBED8EFB5FE2343702FE8408BFC8B84884EE1F7A1B437E1215E81FB2E5ECDABEC885C89B48609434AFEAF7589FD90576BE208F849987928B12B9956D9B77BB6CFF7D816DCBC0DF85528162C1EC397D1F8FB86CABD623FD
	A86823C0A60164CE9B1BD6BE8D6DDFBD6830DF34966DDBC15F8D628992G92416DA7F7B86C5356217DC450A70094E40ECD2B84E94FC47B02CE077DA76B51FE88745EA0C1A011A0F15C7E17AE077D9BB57CBDE05347269502540298C0C600B01B0EBA7E6EF34002C122C162C172E7204D7BDB50268230A690B30897C8A037B157234DB840460388D9E353F4A0D5D9E873F983BF33G189DE4B4489810A8EEB35848772C821611BDB62D88249E24BB9BED3EEB626F9E834C97A484A486A4015B0C5801B685B3210E02
	B403D8C13CE7224D54E67E0ED44042C1A600244C023C1A05B65E3F72EF0F908CE4904487A4185B4CE9C11BE840E2C1B2F346261500B465E039F49A9C65B2AA9D6D4D2077A0B6A021A063397DD3C6073D0DFF27D048057A87D28E528AE24FC57B09079C767B26716751208FG09G198812B19B6D27F5BB6C2B2623FD9168ABC15AC106C0FC39FD2949E13FA20BFFD7857411A0F1A059F3A01F67E01E5C474F27B483E6G9981628F924EED5672B3A993G0B87494F03B68F521607B60F667077E78366859288B28E
	A4165B7CB29BEDB273214E01D401F400F800086F98DD0177457DGE3772D2794A96F0B4B7705ECAAD25E97DFAD537794A96F0BCF13BD873D25C8F9DF7CA7C1FADFBC2C98B9C5F3CEF1455A1C16FC2F647416CC5F7A3D125335B2CEA33F5766F477CA6414BD97B93D36D21B531492A5A713AC4ECDA5CACE174874BDA55A1C3CD7A127FA4E69EF2B34B9452DD2F27AD2966712D2EDCE2D255A1C86FEC0CE9E1C539DBFE8F3FA6D876D321BD22664A45FD73629CC4969A2597E19832B11D3B0676435DA1B535FD6EB17
	DD5CEAA5278F6575F10DB6273A3548A90EF36ADB2B4D690E355AE5775ADAA527D419FECA39B62712F54829E89EF27A6FBAEDCEDE6B344B6EEF6B141CCE496B63BAA527A7E57B56F2FE646790B9272DCE6F6C17F3BA61744EFEF9595DDB6DD447E57EEFD5EBF392EA11138F67D4DA2B4D2923D63B6C2E29D5F23ACA669FDE2B4DE9E49DF20A641C4654E96F91FB3FCE49C93E17B5FF0B125397327783EF5B225D97E4EEC5CE99731153462D5A1C4CDB151C64F578662DCACE72FC0C5C2A4D691AFA3EF71EF38A2D57
	6674F93D12535532B88B9B34B96DE95066143F0D6F1D671C36EC5366B438CD49E9016C5974BD5B34B945EC5366F4F3A35FBB4FB90DEB546674DD23F65915B5A9B9FDA0736FEC526E1F8A3773FD6F1C532E6D5A1CC6EC57667448F6EDCE9337EB373BFBF670FD6F8548A9FA07B62729BB356BD365CEEDCE3DBB3573292C196FFB671C8EB4EBF372EE566694562C4DE9D233F6BEBD72AB5F634EB9CD78D51B531CDD5AF53CFE17B627133B34732932056FF1671C0E34E8F372ED516674E20BB6A721D5BB1F9E64E796
	04854869ED27B38B7231E5E6061253FC193EA6C349A9D94639BFC349893FF545B879706F52D5F0CECFBBFD17CE4EA95E693BF43F49745FCFD5F2DAA12BEF6DCE5F2513F3BABB0D6FE7671C6E1B264D696DE95A1CE6E6AAB94975B5195A756950F43E1F1DF39ABDDD1B53535335B945CF5766747D8CA52745327A34BD0B6FE7671CCEE5E9F33AAFCB1B535BD95A1CE6E6EB57711779F9177C0548A9F126F63BDB6BF45E658A195E68F45E65B3D9FD7A2353F997F9FD5A474F3BB4F3CE178DF35E65D12773AEF2CE9F
	4FD2F2124F2F96B81DF7115727379D6F1D601C268F736D45DFF235B99D4BD5F2121F0F38BDD73BBE99E7A327D04E695A595A1C1EF27A9E241C53D7CE5F031467D3719CD775095D879F2F3AE7C9CB185FB32AFB1606773BFF25FBFE572DF24FEF6887777C3AD63B6737ED2DFBFE2B57396737305ABD3F54DA777CC6E96ED9F26DB7F32BFBFEFF545C33645AEF41B6777CEE575C33645A2FF83BFBFE8FE86ED9F26D372E59BD3F3FEA6ED9F26D373945BD3FC75C5C27FE131B5FCC9AB14D4DF7CACFF74FEF176A7783
	06770BF6F39F7E81B77761CF505C53625A6F08661E96B5A16F004891AEB2BD8263F160A7C22ADDD8D9236F6D35746ADA0DDD5D3D96336530495827EB561BFBD66DEA592DE35FF251095F92358B497B0CFDF6A1E90785FEEEB6986CC29A7B922EDDC8A7DF88ADADAD05DF641B2074D78A798A28DDC8259F6904CB7271A278C33ED554E793E6B9FF58F273152F3F96F830B6B7C9C8EA90523A691BB09B045481722A4CC172FD238F85A1E916DDA82B6BA5AFE651593B0D3A7EBE23CDE737687A0C3D86DD5F005E2ABB
	042F4150F531CF5D757D8269F465E20794697A3A0D8629758A95EB03C5071FD487D312AF4432D05D32D35FE76A1218310C137ECFB351715F9272799F1D6510EE85494895B4BCD28CC04D52EFB6C01890CA8A7926BAE4BA7BFCBA64AC7DD63ADD18C95F032293CF6295997B0A6D421C252BF56CF59FD00ABDD6FD6F899DB97EC794EBDCA9562AAB2670970B387E1DAE3ED4C47D57023ADEBB640DC81044C0BEF8EB6B211F795CE897B21BED1643B6FD0F98E13AF89891D2C80F9D5A058CBCDE4866D1B093BBDBA156
	69762B7D0478CB7313709B244ABA5F3D046D6D9A927F12F76F33F7149E265F5A8C86BC8A043D1F67AFC74919E955F712EE6E343774684DE63B5EFCF4F32FE19BD45F6A93F623637B3BE6B3893F8FEDC425785E9FB361FDC462737F81D0CB87880D4307969AB6GG243DGGD0CB818294G94G88G88GE3F954AC0D4307969AB6GG243DGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD4B6GGGG
**end of data**/
}
}

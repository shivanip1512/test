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
	private	javax.swing.ButtonGroup irdaGroup = null;
	private javax.swing.ButtonGroup autoGroup = null;
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
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JRadioButton ivjIRDANoRadio = null;
	private javax.swing.JRadioButton ivjIRDAYesRadio = null;
	private javax.swing.JLabel ivjJLabel8 = null;
	private javax.swing.JLabel ivjJLabel21 = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == ClientFrame.this.getConfigYesRadio()) 
				connEtoC1();
			if (e.getSource() == ClientFrame.this.getConfigNoRadio()) 
				connEtoC2();
			if (e.getSource() == ClientFrame.this.getAddButton()) 
				connEtoC3();
			if (e.getSource() == ClientFrame.this.getDeleteButton()) 
				connEtoC4();
			if (e.getSource() == ClientFrame.this.getMenu_Save()) 
				connEtoC5(e);
			if (e.getSource() == ClientFrame.this.getMenu_Load()) 
				connEtoC6(e);
			if (e.getSource() == ClientFrame.this.getMenu_Exit()) 
				connEtoC7(e);
			if (e.getSource() == ClientFrame.this.getMenu_About()) 
				connEtoC8(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {
			if (e.getSource() == ClientFrame.this.getAddressTable()) 
				connEtoC9(e);
		};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
	private javax.swing.JRadioButton ivjAutoNoRadio = null;
	private javax.swing.JRadioButton ivjAutoYesRadio = null;
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
	radioVector.add(getConfigYesRadio());
	radioVector.add(getShedYesRadio());
	radioVector.add(getSwapYesRadio());
	radioVector.add(getAdvancedYesRadio());
	radioVector.add(getAutoYesRadio());
	radioVector.add(getIRDAYesRadio());

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

		String configString	= fileProps.getProperty("EnableConfig");
		configVector.addElement(configString);

		String commandsString = fileProps.getProperty("EnableCommands");
		configVector.addElement(commandsString);

		String swapString = fileProps.getProperty("EnableSwap");
		configVector.addElement(swapString);

		String advancedString = fileProps.getProperty("EnableSuper");
		configVector.addElement(advancedString);

		String autoString = fileProps.getProperty("EnableAutoConfig");
		configVector.addElement(autoString);
		
		String irString = fileProps.getProperty("EnableFastIR");
		configVector.addElement(irString);
		
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

	radioVector.add( getConfigYesRadio() );
	radioVector.add( getShedYesRadio() );			//this is commands
	radioVector.add( getSwapYesRadio() );
	radioVector.add( getAdvancedYesRadio() );
	radioVector.add( getAutoYesRadio() );
	radioVector.add( getIRDAYesRadio() );

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
			outPutStrings.addElement("EnableConfig=");
			outPutStrings.addElement("EnableCommands=");
			outPutStrings.addElement("EnableSwap=");
			outPutStrings.addElement("EnableSuper=");
			outPutStrings.addElement("EnableAutoConfig=");
			outPutStrings.addElement("EnableFastIR=");
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
				bigString =	bigString + (String) outPutStrings.elementAt(count) + backup + tackOnString;
			}
			else
				bigString = bigString + (String) outPutStrings.elementAt(count) + email + tackOnString;

			//parse out the company names/freqs/capcodes and cat them to the bigstring		
			for (index = 0; index < companyVector.size(); index++)
			{
				count++;
				String parsedInfo =	((javax.swing.JTextField) companyVector.elementAt(index)).getText();
				bigString = bigString + (String) outPutStrings.elementAt(count) + parsedInfo + tackOnString;
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
 * Creation date: (7/31/2002 4:14:20 PM)
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
 * Insert the method's description here.
 * Creation date: (7/31/2002 4:14:20 PM)
 */
public javax.swing.ButtonGroup getAutoGroup()
{
	if(autoGroup == null)
		autoGroup = new javax.swing.ButtonGroup();
	return autoGroup;

}
/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAutoNoRadio() {
	if (ivjAutoNoRadio == null) {
		try {
			ivjAutoNoRadio = new javax.swing.JRadioButton();
			ivjAutoNoRadio.setName("AutoNoRadio");
			ivjAutoNoRadio.setText("No");
			ivjAutoNoRadio.setBounds(418, 218, 43, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAutoNoRadio;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAutoYesRadio() {
	if (ivjAutoYesRadio == null) {
		try {
			ivjAutoYesRadio = new javax.swing.JRadioButton();
			ivjAutoYesRadio.setName("AutoYesRadio");
			ivjAutoYesRadio.setText("Yes");
			ivjAutoYesRadio.setBounds(362, 218, 43, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAutoYesRadio;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G8DCB81ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDD8FDC15557937C6050B4A0A0A15ABD634EC23C59B95AB3661E60D0AAD2A5BEF2E5132F2C5E59BAD36390DDF3FD7C531C22524C481C1C5C179A382AA828A88A8A88A82AAAAAA976E852EDE6E3DF2AF0A0A7AFB4EB96FF36E7B3E77FDDF74B667677348FD3F4F731C73BDFF5E731EF35EF34EAB24E6854E1DB5BBD590E64D9544FF1BE789C277D4C178D150261D0895AFB5248B12FFABC03E90FE31A691
	7E5600AEAC4E101EA2BC32E8BEDCF740F570070674A570FB3150E6EBB6408F0DF8F2C0F75B8D7F3ADBBF1E0C8A0CA709DE9F4BC8063F8BC1BA819B8509DCCA62BF1D11A66201F03DD07CCDE456ECC1F0ACA3B6134CE992BCF999332B073F1DA029694466560C8CD19F869831098619AE5AA6405FCFA93B6BAA4AC53B2EDB6F948C4FA74CDBC06212040F52154EFC4709FF8541E79E4908B942B31F95405F2A56137D5BFA6D03C53DC61B4DEEF319FB870A9CC6ABDCDBADE61B2B22220E7E5DB1E49CB447D10E3FB5
	4F277E40912EC7B388F6F01464CB997CEE86B1E5A04F1796D409BC7CCF5EA9C4BC7097CD1E91990C5F748CB7CF4732D3C97E19A47C27E83E933F9C386E82E1DCE6E7A217291948E5E0AD31CDD9EEC84F8329D90E787AF5846F04EB87086FF26438B597B9F61EFFD0185A131534C803E360F246ADF2391BE3F07229242534EECC2AA4651B18857102D401F4030C838984C927F53237E29D7C2D6DF1555987876D36DA07E33B55B43447EEB2DB61F7C585E8C42C4D5CEB9FB289822BF72F188BD4782004E5496B9C91
	163603825613603609C2076B2B2439AAE973FF7513F9D1E29853325CE9AB4FA67976798512EF827CA6FE645EE2F89645C7A5B8AB2B2334AE106BD050B9D6E0BEF7AEC4AE4D099304386295C9C9AADCC6FE79F5D241CA715ED869664272785A2DA40F9D007B01840104034400A42C44BCFE2422F42CBCEE02FABEE4B3DA5B07FB9C46A15776E10B55E49E9273BAA2C3AF2F5504797D760A7DD83ECCAA1516D178CF32D573A5FC525C2434D5ACBF0AD7397305451D142995F76BAA4F3A311017E18C3DD7CC2BD8994E
	6178BB94F7C9F0D636B60A13EB9F504502B07B97683D551C436C9773F0DE27782884E761EC24B8398E86DD63EAC3BAAB5F6CEA527ECD07EBE10DA1BD8D24982495448AB2EE8D16714C0A7517F99F35CA4A1779BED679B57CB50F38060CCDB6274BE86BB59FB08ED90CBDD633D390ED1E2AA4795AA3737B853D3FFBA10E1DE61BF948683258ED90BA340BD0191C4CA632728BE2B3E4265A8E4B2099A298F48898EEF4A5C9335568F46DF7180CAE335B026927D612BAF15AF8GC204G60F7749A12DF6F53B6B790FE
	072F413C5AD3C33054DCC3FAC1AE66CBFB551770F7B7D4EE333522225E52CBE898074EE29D7D628229A7B5E05F8962G09844948B3248F000403F42CB5244FG71D987F8BE5E3BFF6E44BADA3F609EA17955AC55BACAA4A4DFECA37315F5F4B50DBB8E70548228B7A01DA0A3A081A0E1A0D3C1E6022495023E901F393D23A1E258041BB4AE128E69F5C63E7C3AD322A769D3F2F5C874B43DF9AC6DD7F28F23C4CA42A27948385F4639FF21301F2338DE22381EAB793DC0A53EA4496F2FC0BE976FA7EF38FFAA79CD
	1EFD2CFEBDDDCB1F17D08EAE907FC22CE3B7D7E533FBEF3B51E9463B2DE588EE943349DD63186D0DDBC9F86653500328B7BB4CB6937CF00AF57D362D1F3A6F43BEBB3FC782A8CE5A65BA38D11C9C3FF76B978C07B024F7B56A3FCF75C617EB4852B36CB2371BDDAE0B2D5F6D7FA34A45A9EB721DE2DAFF361D24B59852B8854440533AF95BE2497D54EC371F9CF6F41016C23CAF17537EC7AC588BC5D866EF2DA5FD469CFEBD03DE3B752E351D17D9AEA40CABADCB9647CD179D07329EDEFE9CAF2D4B91D89BB0C7
	2C2FC9E2DD7ADC2C330B84D66F197D5B543E8A52E482718389850981C9DC8FED98C8874868FA4C274AF593AEB3FCEF6A3C37423875D0EEE3717276FE74D6D81DDDD72DFD4FED28D63F67B6D72B5FF3DB2B756F2906EA35FB0A691A2BC95E584F58A477683EEA72FC6A355BECE65AD0F097010DC97E5CC152E131D9DC87AD26FE332B5DF24EAC88D16B493DB62B0F5CEBC170BBECBDBE174E2C4267D24E393B0440637F1A27563F9DF9F0E9D2CAB13B17F20A5D4FA546292D065C7BE62BF990B22A4EBEECF3B13C13
	BEDFC55CB907E2330F13F87718ED4347F7D82095E0F5E03130EC9B479B4DD6874773D31B60EFF6DE1D5D56E769BFE4F63699CD96FB0111A63EA83B30F0DB36E4FCD550ABB3BBDECB4D9AC8C9BA73569ED8DB4C435D3AF627A45C3D764B89D6347288F50398E67602EA9AE62D4934FD5865325BB443B32205C7D8A5E2D8D96D8DB42CFA48BE17F90C60CC92A30F90CBF91A2F3DB41E06C9F3395DF85A0C8132BF2D6621BE7B50205914F742F85AD8E4BC63AA2277C011A444359EF18FE2E0E2446ED01A35032984DE
	E51C57377AA53C1A6DC653FF0C97896CCAF9ED643CE266C8F8B50CD8DC7FB1DEA430AB653509733AF73D1F1BD7ED0FFD78BFC70C0626E436E1ACEA1B992FE34357E37D9FB2BB1D74B1DFB0E89FF61A0B687F90DB6FC9CFD6FB08CE0314D1925471BD52C014145C2190C66558870B2BA3EDD3BD34D91DA00EE26C1798EA089E7BA5E2672B4EEE3532E6D540B9276E8B52B9A75FE2B6E79581FF8DA4BCEAF34FA5B51B9970D758G4F6A8D446665D929921B348D4C067CE67ACF46502F1B2D2FBFB0067E5AB174BF9A
	C37F5A987AF9F37475A51AFAD69E13EBD348F350FCEA588C43C9D25C3B4DE7F1CCD87B25C2F740E89DF60FB523EBC93F41AA69DBB27CD75B551FCB4FEDA77D4913BC7C5FEC1FA710B1A38B13BDC7C30CA4CC4710E5903A622433AC56070D1952328E06F410794AE878BB9D0479AEBAC0FCA555D8F015905F0CFC1C1BCCG5BD4404ACA18FFE3894B8BE677AA370B006B29923B19B23B7185E817D3EAC82FAAF55BF515CA6D3696221D8F6093A5F661B23B0F385DF44093C0B24A44704A24F6BF6DC13BC040434A5C
	F6D3E5F6033DE8173191625C68366B58A835DBE1C23BG40C3C022B7B23B14CDD23BBFF13BCE409DA07E1B447C976DD2B7933B3523B827391EE3B594FB74B3685BA4613C511E94699CF03D73CB5539BD9A5FE6963EF533F2BEEDF58389578F70091B31ED59D9A79D7354BA1D76DE8B1D4E906B655F6B247DA2067D3B8E2889FD16A1272B89BA1CA3D837E755932EE43EC304457AF968B3B28F4A671EADBD03C54327870B0CBD509E1AAC2E0A0ADD6469F040629CB6DAE9759558FCEF400579E298A11BF1DEB776A2
	39CFE261DAA8B7242715635C70AB175036389CED33E6915B81389E8792D00EF57E0779DB854F79248620E29F42A1EE5D995ABEF7586B6CA6F3A35C2EF53C9BFB40E2BEA3E039DC58A11D731E38A8A969F30DF289A9E76591DDAE1973CEBE1544B87DF3D99A6691F9D80C94B94FC05F9B32C6B297FF9F9DFF12B1C74A96CC638B25F46E8E2E7DC002C0581CDBC3FE32189F610045EF4136FD43BEE95BD6D0814F9A10D19076DC8C4EA7639B7191AD165F3BA94B855A0F57692EE2FE5C3F38C68CBB6AC793040CCB31
	7354665787967DF55E048A168FE195127C7839B34987042561931E0644CAE8B3AAB18D0FF5292541D509656B1B174979EA7403851C1B7CF18146618B7E91BC0E288AE99CB3814F2EB22457D7E19C56A3641E526C9B73707B8AB17CCE701DD005617F24CAAD7CC89EFE5EA252FE68F516D9784B0AB07C9870CD5D0A61CFDBA78D3F99F097C840D6AC67EB0ACA5C65FC85DDB13C278DFBC9FB64ED071AF1FF2B873987832F691C7B7B79EA5CB336E15E342C67F1FB51E9E6F15F590BF197C338835BB06E7882353883
	F95C511BF95CDEF40CD95C3BCC98F7A804BB03471D37C199F7CEB5BCEF2B314C9EB3FD33B2FB333E8239FB55F746BA3D500C7C3B001BFFB576AD0F9D1076B722G0F83C92DB1246755E09A064D05044315F43479F3A4E467E6F64FF9534D46F7E4A55267E86034F91A4F511A9A56AEF554A81F231F6EA46D3083F01F9AFC0E0E56133993FC0E36411342E86B27F38662BCCD83C9BF7463446B228692AF6D4789B84F745D8BEA6FF582447803EBBC678759FB172ECB24BCA2EA58FB17D99CB7D3BCCA0233727B73C5
	FC3FBA95F4D535B887B77512DA7C0135AC5E50DA2D78EF274F5948DAE57C93A9EE28D5463FFA9646BF8DF4755B719D470EC6D27FA7417594109810F81054BA4C63479B46FC5757B022732EAFA8134C1316C1F80DF51EE969D2E08E959B949F05AE208E5F0DD517513965BA65BB36A60A0757A95F313DD40A6F5822C0D7DC0FE531BBDFD9967E7572B8C394574C6F1007DF11625A2070E37756575B49B3DE257FC19F1F6C8904634CE737C96F739990DECA837419C19A9B30BFB6308F7BE35D8DD83E8FB511E7DAG
	DC0702C4014402A46F00768124F98716F3628E357E193C1C35BAE76E7784ACBF928B3C4B47AFBD7C1479E85DA177E3690E6D42F47B6C40328C73124333E3F2885F21D6160B5A172A17A59623D80E2FECD4162381421B89123C934BF1CEA526A7E7A72647DA68DDFA4EF978A9535335D3ADCF5F2FC28EAE4EA13B48BB8EC59EFE4B955741BB5572B4F2EE3AFA1E1221CB134BBCA8666951BCE91ECE0130E2C1129B21FF5708F93AA49F53D35E0869096D71AEBDFF7770D3264751281627AB8A1003AF67B03157BB8E
	BF7070D3F208D06510DF089C26F28E2DA66FB89C7370D3F2C8E9D2346F343CBEA9D4AB2FBC302DEA4272BA3E81F9F6B4A14FD40FB5BA1747F325071F1227DF13DA7DBBF1DEE3FCC87BC1ACAD76F6E9DA021A587D9C8DFF2773345CF90024057405921AF08C79F89EDD4F358B5AE09053AEEC2F5F5DC5ECC7615AEF97364D469DE47C097D1CD63B458643F531CF33F0A7191B328FD97AADB64496D32C57BEC4DF8C31772821F4DCBF91428C86E158520B848BA3573BB00FFBAE89BC5FCECAFE47DF3C5C771D5E880B
	E341252B9987CB73DFAEE1BE4473FCE895B09F045D188F5F1965CE7B7D125F79332EFEBE54DF55B8D89D595B287D8EF3FF237ABB4CA30D6A6FB00D0D7A6B86AC0DDA6FB8D97EBFCB67D8F2A06F9BF7E319E4AEA7D897DC3BB876AC45FC61FAA247D2328896321B35A18CABA941721C426D5ACB69BA8238CE60E55CD0C666F967B763F58B3DAE60573FAB3DDA65600EB36E2A45C17A3719746FF3DFAF226563F41A1D3493D7EFF499C55D3BAE32364563FD71DEB2AB4D5F8B66FFAA5AB4386CF59B84EC9FBFDE2B51
	BE369A2125E26DE37596E5FF2795526E8271ED46767113ED73453A9F580C6D638E7ADEE6AA3181193F8773EBF01F5AF8A9FB8FEB3F4B76F80E9F44B5E9CD5A75BD2CC93D3E7F3C09343BCE7B70D02FD9D9271FE9CA63BAF7CF18F4CAC856B11B5FD2FF17F1285F6CF1CF3CCA7D19CE4B7F0FCDDA778DAB174A82E939148858FEFC582516C70DE25EF42964918B6FAF327048380F15E1CA29DA99BAA09C1FBDD8061FEF42B21C38874B702B8ABA96056B99A009FB318C67D72A714B58AB2E3D5C2B456F01C2A93FB2
	1E5E3FD42905D7AF065329995E41A2E9F89BF93D7EF4499275FACD7A1D42143DA4ED5B32B0CF2CF06DA346913C172DD3E2F9F2C79E76E112DBB0CFF2693A4CFA386E82F135E01EE4F80CA17DC0975822BD9E96578174CA53300967499A0FF1CCA80495B9E6F82F48425B4C43AB7698C744C0D8F1E306F703C99A9E1987E2F933298873A631957346D91CCC735D105E81E2ED453CB1EDD0466D833A005631625EED16463D0517EFFFC326FA7936778E592DD652FAF11E8F35E33D8E66BC9F5ECFF8CE076B841054FD
	48731E8304E781DC576C73645681580082E77394EB691A5F51FD4A391C920A7B6ED34E65BC1D07F3B97E201B398F7B09B3F6910C3C83E257F1747AAC4F0F97AE10671B8B9A3A2EB63297637B7B841EA70F1DA16FD07A060C0310956D8EF32F905FC662AA3E4877DD3409F3D262DF837CE561BC57C642E1767D825AD735B33BD6712F239D67A3DF4FA44F9C13394FB8EC052E045DE9EEF1084BFD810FECA7614C4C447AB1017B15ADA765B9E4F6DA4E513738E85BCC7B85A42D499DBC0E6524CCCEDB1C96E2496C62
	39DDB6374B257D87FAE7EFB78E31B4B415E09A4A402E91E4G4497E49277BBD1CA4746969765417261E00740737A71E23286DA720E370EEC7DA07DA3D239D83BBA2ECC592E1236A39262086940795DD91B25B67100137EC976FEC3FA59FEECE74EEC413CEA5E0FF534291254919F381E88923A9F6B681CEA693D948578347D6EBA993F5F335E323AF8311E04B77FG2B0B059C7FAE452D921C5551E67A7CA65751202B6A44FE188F1D231AF448109EF68831BF6D637DB50EA59EC53BD80E190F22DD7CA1CCFBE3A9
	26BD6DB026FD59C692F7ABDC8F000CBB0CE9CF59A4CDFBG6041075D690DB82C1576716CB9F61825F1AE477DA99EAB41D95A53ABF02EBE81F45DC710FFD6A526E98A4746D572B1984766EDC53BF80E75EF65E314AEFC5F581745776CG5655056FCB2E5D32CC3DFD93EBB934B5B41F9E207E7E60978CB2258B577C0D2C41772B415F1FA0940C4BCED25BD3945111149453A54E539FF54F57C7A41FCAE279FDF71E325D2E823B56235A6DB64B57FB68B352F41465EB92479FE2FB9DA4B84B07C20A53EB504DBC0AF9
	D6CA67FC3A0E413DF48C31B3791837BE9C5BDA00F693B8F6CD815A85F1ECE7A15A05F06C46C2DE2647301EFDEF3DB25D51201BFEEC2CF43FCB1FFFB30FA96731BF22787C634AF96C3345B80F1D863A516348697C8664B90DE33F20D8BCDCA7F6A34F60F6641982D8CE376259542DF5BFE49E20FBD13A15BCF3A95E552D647968FE64E985DDF4B7F27AB11D97A9B3B2FB06C5D32C95B02B916D72F3316E0F6358815A378A026BA9C6CC4FFA725ACA6C2F14CBFECF4E3B7AE376572EEA9C6C3EFC710874BDDA5CF77E
	BE8F7B85CA7B29C6F12F1F51FDDF32767240C5FC862675403D58C35B640B52F720E44CC1B22F08148355BFA1534F1568992F97D6E3FB1170423DC273638BD55B8B5A6676B0BE61625F30B96573021ADCC90C5BCD82F20B675C1E96145CBE16F17BD05062461E430B1613BAAEE9860B8EDAECA67B99F89C131DC9AEE7D1FDD3FBEBCBFB4371163D476B1A6187CBD36099E9DE27FD162C794E3220172525C67C1B3331FC9E0BFB72F26253E734BA5DE3694F332498CB373D8453BD520B697EEE09B4DD44F749DCE93A
	DFACD1664BE6D93E4CAA156AC91A6E11791716AA736DDD511FF18AAB636B00FA71395A3C09BE6F60DAB0995253CC783C491F2F511F6EB0767418CD327E74164D38762A5804617A1653B5C3F0BD8EA4400477736896E5BB9AE2F237C9D3CCDA6D5337365139831372F9FDB345E718144F6B16CAFCDE8BE6C33A490CED4C32EA42B5922EE3B8769D3AE6DD6803FEFF9FE20B37E37BD45E075CFD6AC9FB5085578E909F10C010D010A810D81044FEC3FA86C8B9C88708954487A4GA494A49AE48608B0GF98DD28CD2
	8F52B500F1BCC063F04135BFC8A80881A49EA455A22E9F043F9D9636C65085FF7DC1C2C18CA0714466845800F40038C07CC1C2C18CA7B00E363A6ED96E7CA57C83C4EE8B4574A94B2023DF0E4D6857FE6E0DA501AA6123107C0A5251DF0E10FC5552952B68FCB452C24A0B14B3A99BC162C74A56C764C84A3ADE54E179239D4B6BC7E4F9CD46E3643EDAA1385BGE53C4ABDED2B3EC1DEFC2A23A36FA6547648DD09646A68562AE0699A36D9823BD74958E235847FCAF06F59A36BAB73455F4ABDCC2C9D7E75DA79
	D8B81CAC9F934730897BF04C958F77417C130674BC10C6100113B8667D47C1377D68D13477857DC410F01098100413F89F6DA9D536E599D6F77997DB355A321B69D8265E2A6C8B5FCD71AE2B32AF1C3D917B42AE50CD35E29B153B895B322AC1443E37995B32EE0E3DD50EED194FA0F23F250276F161BA02E3F7D32CCADCAF4C309595E3750108BF89471B7A43622898B30E3902F7FB6E083032DDF648DD36F7D5E259CE855EB3C012EDB08687E936E1DD58FA58EDFFF1AB1F97847DB810C0108810E9B67EAEBF4B
	3B7D6F672E580F5515A55974FC813B72F938126229F665737061ACFC9E66014E4F0E6FA6730F10344541F53283468BA0D5A0DDA0A38E1C033A37036C2152DDC752E1F7B41BCF1B2D12D7C52C6EBD16076396BF8756471035B83E8AF47071959D6F8543759410980EBFC2F132AE1D2CB9A76B4999DE3AEE2CBAC3425052850B3AA99AB6AC0E66B163C85251E1FD1D2B23DFACC4527C993419A1E3AD3DECCE2B0B0EE7C95FAD5E01757119AED25CCC5FC747313F87C9BC057A7FBD6A56BB687875A3D28F38BE7FB851
	337C8FDB0FE512F38A4BA4E1BD0EA54BCFE19EFFD8CC5A53FAFE7D8FFA9D424B60D271FFFAFCC4621217C140FFBC8EE9BB4272AB080E23FFC57264944E85AEAE16BFCBCC5D6E7C7B951DBF36026D841EFF999DF46C8A57D19CBB5D016D6EB41E072B8E10F09307703A10DE13FD8D6C7A717D171BDE92461526DDBCF363326370264DE579775C81725BB86C32371139DDB3E9ADD0776E81526FF08C197B4CC3C3E6935B2EAA1605F01064D98624AD8F04353F5BF3317D6DGECG4497E492C864901657E09E698FB8
	876CE78E1807DC16DE233559E2A3DBFB44F0EF484371D86C9036FDDF98C91932B66ECF9D245FF140F8C2F59743AE988235741CB0773ABA0C7DBC9F0FDC746E6C1249C9B8368EFDAAC088387E37733476BAA408E51C6194675DE7174FA3FF997F93BDA4DF1AC1B7G628792849286926D4436BBFFBF61C8D67AC102EADDAEE36FG5D0102A9A31BB05CB0F9C9A01E41A0DCE95A98271BFAC9BECF07788597F2985A1F2F1537G368E599DD0E4E799CAF7F909796BAA716E197A33B225DF364BFD2F1439347AE591F4
	AC5C68D24E01BDCF71CE17F28E2CED934E018D00AE4A057770364DFA3C0B073562CF20E3667AE165B3BDE98B0DFFD879CC3F399C1F69D650C58FE33BF37B966C83161DC64CD001EDD167E964B91E3E239F05EBFF0E79D30C6C838966587ECA3D76027812B03CED0FD99CB65DB83EC97F4F5DA6ED28A6EB28FA6CC37425BDFB675BD80DEDCBB424A196A4638C0C4B4F303DB04C6F03B262E7B51E358F3B506F17B5685788365DA07EA013C4BF56EE2D5B0EB691009BC062C1D2C720DCC6B06C567DA47D3B1A0DBDE6
	6BA611671A5DEEEC3308756412B63356F41A1C3CE322AA1E2EB9335D76DBC46C7DA524EE348F18CD38530861077BD2A46148F5C903042BFC57BD467165888D6F0C51A177F9A0F911A4BCF1171068B34F380007079A8C6FD13A3F0B70137B3C3572F3B19EB97E666A79BC0D9B0A99362A1D04CF364D5412670CAC7C5641C5228E1E87163E3352308CEB960A3AFD439633CB164E3D875DF9D9AEE2635BC95A1B5A6AEB65E9FFEC4167A2AE67BAF3544D35E403186F4DA48C42D39E46DB431F0BB8064176303CD7AF1D
	4B6B98E175EAE4847738C46FD0537BF17D130DEA7AC02EFFFB171ABE146B176FD15B9B964975F3DB5474B1DCFFEF271ABE0E6B2FBB2C26CFBC0B7A00E3EA7AB42E3F542D26AF607A3E9E357455F07D9E131AFF8757177529698738BEFDC0F57F9F574FBE21967F842E7F70241AFF90579B865574E1DC7F34DDAD7CA82EFF6C141A7FB4F77EBA557471E7719D6C4AB66CAF4DBF0773572DED4A79EB79BB05EB5B147357BF1469C35B147357E6197E1DB6657C752B33257357779FC5CEB51CD374D1FDCE330F69F3AA
	BB2664F4D0266FBB2664C4B6A091BDFB2E1ACA71D9BBF28E1F358F166133F662B97EDC25F3B891F0BD95A40E630194A7FB96D34E33FDDE62DA1F0DE37551C998FAFA0CCB7B19DBBDE69CFA63DB4CEF2D71ADFBCE36EE626BAE4E3367EBB37C6DBE4F76E9B21B6779FA0811736CD9CAFECF90ED5833741F65E8B38970F010981084100CD1FC165ED0CE4A0F1F44A2E01BDEC75F5DCBCFD5E17569E715D81F2AC631BEFDD0292CCF25327A10D9252CCFD9B2FDCB1532BE496B5BB5D54A7A047515F18A5A0A1CDC1C53
	4BDB75B9A5EE536734E91BBE2701EDCACE9F08FAD6CFB2338927E060B3E5946BE79E45DC625ECA06BD1A25D51F082FDEBD1AB2065EDDE7A3B563982BFF28D5CF254246ABE5C7345715D69ED1DFD7DAFBC4FD9D754EA37AE7C1359C51BE8B6A409169D9D0EC9CFB871DBB7B8DC84CA84E6D5411E6D42C6B21E77919CA24034371DFD7F01C6C7565F8EF994749BEDC0EBB73495FDCE72EF8FD2100DEDB717A06C2FABD12AB72FA134E9F3D84B203736A6BF3731ADB7C9946C3774CA27EB46D9F659AB15C575A497B0F
	5362981EAD5E5BEE9CEA35DBAD3DE745387EC667DF48FD13406F7D528EB27E9C308FD94E59EDAECFCF4C3F96BAA7C34EEC2A3A00ED4BCD9D58360C82B681A484A49AA46E821FDF68237DB7491ED91646717D6EB4E667127B4EE6BE134DB75056DBE0D8CD8E254C5BD8DADCD0D2DA3AD63C4F87F271DDEC4AC53C4F83F31577792B332477694B394A7BFC16A0EB87721477796EC4D9BB10273C4F51DFBCE3A8CF7A9E1B0D678F51FECEA3F0B4017802CC84893908E37B894615821DDFD019DBE8B0751B711DBC56F7
	E70F763DF52EE76CB3C63F55C36AD10F9DDA71C192BE6263FB687B913B030262190BBDF4ED2125FF4045CCC55C3F074E5F197BA8AC1E2B583BD0FA1F32E988517EBB3DA48F4879EA962355F20EAEEAEC87C3DB3FB836AE43BEC054C57E1EF993768162AEE27B786CA6B25717FA892F2369F5AB5FB75039C96FF9CC7C46EA3F46EEBFCF694671CD4658C4D85A77EF46349773344E26F3929DF0ED6558820A11BD2F863E47533F9C79216DB761231E870F70B8747AAF5E8A6B530473BD92BE175CFD1A09701B24B772
	926DEBD420CD28CABA59BDD9F21E34BB64450B51C6D7DDD8457657C9460D2D46FE28059496571FDE9AD65BD31CB28E1E5363F00D21239A672D2AGF30D43B59FF333499C28F24DC7911BB02928E8F07542F0FD3BB8FD02EBCA2A313F92BC8E57FE3CC3431F8E5789A02957E03C6F57605A468240CC57E03C7DABF93C6A675A6C9D36DA1B456997B15E12DA0CF762B598EFB7BDFB55G57B3C0922F4531CC6BF6344D38965B5A0B5B15ED6D073236B6E43B322DA56F6C5D7A3736AB5B5A5F4C116AD36B14ED6D9B22
	3F384F26CFE31F8D1DB5E03C339B78FEC91E16241D0879F16C4F0D381EFD8A47166DC6AC01E37B7720EF5937902BEAC1BBAB47DE6DC43BA00E059FC63B980E3DF88C6DD2FD90FB221B2FDB6658F7FB78DEEE0E7918908B675849BE4462B956B600D841F508EDB9011809E3B9D644A6F26CF39BE2860E7D554E77EFDC0FD86CA9446AB976229331516B31ED4AED73EEBE7A2123D76A475A101B789E1D506B5DEDC854752C3DE4EDC8B8DFEFBE7DFA2DB66451D9242DE0A39F56D608FD26A564D95AE0F311534E88AC
	6277EFA275DD6C6B4876CF30B666EE0FF38B68FEF708BF79863C57C7785CF88EE0DDB7E03EC6EC42FB517F863CEF7FCCED2360FABAC8828F632F1B313DC87505F123AF7FA6811D7776016B09A0213E98F656968CBB4A976F73BDDB147739DDF69FDF5A223C4FD74B6E634736A86F73A7E56D445BDB1477795FE6C947CE3FD45D97B5F61DF9F86B157A31323B5233D60357527EF79F763FE72E275783F81D5CC176E4C99F87129DB5C5879A5ABA1A6AEA1B0F37573535B4B7EF2FEDBBDE5BFEFCEFC3C3FDC3BD736F
	6BA85171EFECE9EBBA5C3237C396425E8688965B791FF4E019A60E47B2FD33C3D926FF101549627D4AB2DDAAAB73067D4AB25DA76BA71F592FAC537B84E9198E66CA4B260A709A2F5FCF9177548EE877FD8786466E7B1E22ED21997A2C9DE79DE23F7574G5DAB5E5767B473BE767901940EB5B9DB6DCE8B693132B6E5B6BF2B42B45E5D260C0EE7FBD07C47931B8D1B52F452424AF549DA92875B8B63DE924A46D3D6BE1F9383614511B03F8DFDC11056EF63F82AEEB6BDAF113E9B1036C95FDDA099E7D185EB8B
	26EF41364081E1CC7AB63685AB685C4EB4381E8912F2A33685DF7073B672EE44BD9011C725EB1A057C5F2BFED7E56A5144246E9BD93E785C68DEEBCD7E32301F9C46E7E1580D48E372D942A38E2E677B9952337D7019F13AFA2C7B385CCF6DFC831FB17D14679D086FBE477473081F3EE3BCCC7BE69DFE4A7DEA3D946F76D36ED71BD10D65E185DDB4CFEFDC0DF76D5DA7DE7AED2E75AE3D865AD7AC3BC9195E57A9DEF313B23DFD35185EE650055C445F0DEE770E776A86B53F541BD9DD483B5933BDE1FE49BB55
	7C2AC47BD6CD3F0F9B557CCC227D887C753BD92D9EE66DD6738B845BD010C84D788E6FD1730B916D63B47DEADB547C92EFE176E970376096B51E2FF52A795508369D37E845B779301A5F00E8BF8AFFA72846E7B8266697A45A06E94677F4371ADF14E8BFCD536FFE0F73E908440B7673A7E8790D77B84F05C806E8DF2C696768D35DFFAD5AF7EA7A9D9CD073330A7663B47D361DD07373976DA7E97A2D352A79050B76511AFEA9B6B53F69227DCCCD3FFF58557C12EFE5765937EA79FDF8CA4D2FCC34EF5474B3B8
	557C3AC4FB070A9FEB2F9EAF45764A87EC42EF456754DFA99E8F5749379952F3EE436765DBFCEFC89547DE66E7B838EEC37FF759B7BF60FA8AC84CED588EDE5CB4D6BB287E1C739B53CF7DB9774C18FE9E7153763D070EA962EFD3367BA7A9AE5C2EEC7763B7E1BEA6034EF1BB26F7266E9A9FB5E1FE734A3D637DG9D7398EED7728E2678B48F5E9B4B11778C50357AA36F9C0F7DB71747BB2C63CA7D443D41F41EBB9462A7F3518C9BCF31A840487CB6633BEEA3720D85BC7B8E2CEF7D946F04EB87086F9D5817
	BBF1927BF299BF3C1554F24DEF64855EA12E753DC32DAFF75BE6BE3E38934797911B15630BB7E463033FEED60EAF080DDB1FD32E9CDF5CA15B27F520DCB93EC0FF16576FFBD9C6B32F580F5521BBE89F3A6ACEE55D3A17620DF7AA6BD6EE8516D58768426E443A7535EC2C1BF19774796F529F9731F78C25A748F8219EEC3BC1FCC0A6020400C45E056F9996D872846DB504ECF2103EEB98B2DAECAE46A7FCD84AA786420A1370C98C50DA077766B9123E34G166E359C7F0462AE894E726357346FCF2E23C117F1
	37A11D1D4393311854C90B4DEA31196B589750F8D9F557887C37C5727B2F55D7FFDF6B67B57F053D33356EB43DAD79ED2D3D7AF1DFF7D563E0EDD8D5836EE19838875F79FC37815FFB38B836EAA75A85F04CE7A75A85F16C33A6340B645849A634B3F04C0F1EA5C74E4B1A4931CD3B51B7F9A2E2F7D1AC8D2E4BB97641EE8C2F19E32776202F03E3AF6FC13BD10EF53670BD9B9C7BE98B5A85F36C35D664974D310FBA51F7BA475A8E7177E39C1BFA986DD23F0B5816A3E8174D3143DD98C7BD47BEB8063E1D9CEB
	BE0E3EA60E3D5C0DF63E9CABB5221DBF47FE5A03F6E19CDB560BF6D3B896E8C23BD80E4DB223DDBC47EE68C33B34FB910B6DC33B9C0E5986502E11E3AF8E20DD87477653B9709138766558D3A7F8BAB8D6E3C53BB00EFD4F4A534131DC9B5A45F24C5746534131058EDA2F6EB324675D07185301F6E59CCB9CC23BCE0E1907F8F9F06C83A75A79F22C594553F19FBEDB1E7658EBF7F9ED41F3D76C475A7ABEFA4EF058FD4AE7221D62D36ED3BE93E7727EF6B4684A8211773B323568810087876ABF93195F6F36C9
	7DA24047A0710BD589C35CA7CD4FE8C988D4FED3748E0A97FCCF79CD518FE9FF1EDCF701EE66771077G59FA0E6F0CA53F7F257B7E78BF53FEAF5EFC7563F8385C1D269FCBFED716DF7D38BB2EEA9C2CBFE9DCC34AD5DC33F050E2F28DB0BCE3E4019BEFB41335911CD771C66F6E31EDD76C476A649FE85F5E7FFE65BDB61D620177AB6F31C1BEC68886DD4A83380F476FA4BDF78F2EA7000402C401CC831979G769F77DB481AB92D7EE31B5DCECFF1A287BAC17F51BC646ECA0AE93C4B4B715EBD956E3A95A479
	DDB8661E57EFDE4FF6DC55B8D8DBDDD809633B26B90815D462786E1AD978CE226A04763B0F3A93E33F7BE8BE213E6E276D04673A1F03A754577D9CBDA1DD7743383EB20CFB281303707933678C2E67A966584DA37C999C445B76F32D70F7842E7DC082C1C2C1A2C1E2C062C0929F0467BDC881C88DC8A74888C8GC898C88C0870FDF87E03B402B83E0FE1A71EA7E17B43F5A80881A49EA475A1F83600F40038C07CC1C2C18CA071A02913C08F5281628271878985B10044CF4230FFF336C6F2A68161CAB8125F04
	03DAD9F7BE6846F3C49B077817247D1B5423C0B14FB03FC64438C83E84BCA88F3B60C14F38C2457843A43A9895BBAD997102BFA9CB1617596724F939C0E04FD272BBD743F72964B71EF500E7A5FC5335C80493F92F0667A360F98B648C09CC056DD7AA7EF8DEC41684DBF18571FB33FE0C2C77E4EBA9D78F12E2E76F90C39B709DE26A43505784E98499G71FD985717FEE1F75B5F33132F8F87FDB8C88CC882C8468F503E4C61363F36091FE9887AD69087488410909036D67471DDB8B7944D4368B9A588788E74
	C73BC99E63DA55DA2B255FC656BF8A3856B4F2B77ACF7F812EB51D5D4C5375C3C897C8A348G086F8FB10E6B065CBC5F5B4353857AF010981084100CE0341F64F45BC73770F4013E954481B281A4A49853B5E79FCF978F638517BB8C4BA13E268474B3C1529E01F1A4C867A3E87F67E137FD6B913EFE9F74FEA041A0D1A0B39E41B83F5505F1A67F08175F99F7983D47B10CEC501701F40038C07C39FD75085BFE17112F0D81FDA4C8AC487CC7A1CF9FC57B1B4F3A6DB774727DFAC48FB28AB291A41C5BBFF64EED
	1FE6666F30C19F871292824F8310569034FF633C5B7E7F7A505E81FA9F10A010A9A053397D428BEE7BF78650BE71B148DF10F210CE101147709D397504FC8FFA55C5377FCBA750BFG6C03C122C0E600A47F9863BBFE49ED9FE6C57B82505700F4030C83896476578AEE7B7BECFC6FA2688DA071A029BF0132798972DB6D10737B57EC377F379DFC4FB6588F007802CC82097C8946171968369F9DC27BD850A700E4049A522BC03AC2513EE62E243E38781AED50CFG89810986098B65E7F21025ABE2DFE8D8727B
	132BF6BE77C29E4712322B5F777A248C7BDEB74EC5EC4EC66CFBFD407BE3FF5E06FD1C144731BFB3404F3CAA63D8E8AD5FA37CB85FA3DCC73EBFBE8A5793C002C042C022C02601A400E4BC81657484BFAF175AF74175A848C410F010981084100CA74196246BC9347FF87B84C93AC8BCE3251D44C77ECE53300D92B9CED068039457D5CF287BA77034AF2B13F23B1CE79B1EC924F52E92F9B613E72C72B92F5CE725758D74C53CFE3D3951DD1F5F177CF60E39276B1B57ED3F2B9A876B2B5FF1D2FB3CB071645863
	0587CF2A0F97FEF852F33C90F2D2FD3C7044C969F8012D1FCA606B2BC71FE47B597C616F24A759FEF9E6F37A8CEFBB1FF42F311AAA5A30766B4191340983BCB18C46GA075A026B0ECDF597DAD6EF9967791A7D4CA3092062D2B15E0258C5B3FCA0215096BBA4FC9300D62FE213992EC9343B273A518781DE73FBC89D6AE6E97B7C930AD8C1BD6AA597BAC6E53EE79122EB39DB4DA2CBBACE62B897BCF5F2BA45F78E5EB58498979ADB633CC9F11A957F71C314B74FB57244A7583C3E6968253DF24FDCB265FB144
	BEA5FC16C742ECAE8DFD45ED6A0C8E721A46CDC3347922D0A59CCE06599437FA046326AB5AFC35FD29CAB81CB4371BBDE709C7D8EEAB36FF37713CF27FAF29DBBEE1383FF77D85B5FDG57FFFDC9CD9F4C7503DF2B69A3383EA3CDCDEF607A4AE5EA7AD82EAF4CD053A7F0FD6AF2B5FD4ACFD17F6FACB5FD8E573F35C2CDDF4E754F2D12FDA37227CC6F627A1655EAFADF2EFFF50D1AFEA2579F49D35307F07D1BEB5574D3383EBFDFCDBF156BBFACD0534F607A0BC5EAFA61A954A7962B69D3397E5B9B5474F9CF
	615A10579A785E5927703D797C9D4A7766723D5895BB146F4DCF4A74569D4A77664E59D27D3DBB146F4DD78B523D3313F7723D331C53DFF66AF3DA512864D4A85337B7AAB9354BB8DD57284FE9D2935F9F43B97DCF13BE27793B141CBE1069ABF6A9B91D14F1326E5262445A1728DD1EE7583E311B0C73256B345B497F6EA3F56B9A76F610E559E23A721A795816276BC033FE3AFC1B1569126FC3FE2AD9192EC1D93AE6B46B6775223DFC1F4F5348E96BDEFDCE763D7A657F3D3DCACE3DB27DABFB151C64E7967C
	3D85B9F5F0CEB92D7A1C8E346A173FEF2B1253F1F9BE36EAF19277826C73BC2F7816C348530F737C59A1FD1EFFB924FFBF2FB82C64E911695B8F6B675D69A348E98A6794F4C41F537F9C514F3B79DDCACEADB2FDC51712931E7740726E6EAE4F3C9BFF9CF9A6F01ECF9E5767795EF17DFA175E2D64F9C8265F532D4513F1F29811D3D9B8F23A57284FA95A28DF1E33FBBC5AC2193E2CC71F5341DE64E4651C2E6BD5F24A140579E42F1253FA59DE0F77FA151C1E136953CD7AED46B6B3F28A621C2CE67DFC3A572C
	4FA95A2C64E41467E31FC7BE496A7D6AFE649443B935776BF33A2EDF1F53137DCACE7232FD2FDF4969E7B2CE499664147AB364D4E15167E43568F33A57225FBEC4DB141CAAE575699FA710D3B367347A24BE2703A7151C6CB2CE63CFAAB9D54B1EC1CF1F542FCFFF34A2A79F4EA9F9D01F5336C1FDCE0EC1A5A77999B5778FAAB9495B51E7EC48A91CF37A58264FE935DD1F53C13BBE2771F67D36A044011C62B927779C7A1C52CFA9B9BDAE633467141253BED959DDBA25DFF6778FA127021F7373050674B94DF6
	6AF3AAF36A67D31FD33F6C463B101309F3FA52B5463345254FA9FDD81F531EE12D32E3739BCB7819F993FF4E66C0C261EFA488B94308595C4A4F471B76F3779C48CC51064D013CD00BB6A93F00319BC8BD0889446797B887322A52FD9E5B8671997C3F175CD80908FD3B510D150A58BF173931B2911B34530DED9431FF1530B371681EB3B211A019C39867DA6C563533AF7AC9477D15BBA4FBE56951AF72317C35749CB4D1DFEFA19F2F9283904F79126D93A763D412BFC13F6067FCB5286943387E7E1DEA7AA82E
	9F5F24261F46750E5DEA7AF82EBFA8BB870E0C8309FEFE846ADF5F2B264F607A5ED6B5FDB1573F37CFCDDF9F01FB00F3CE63BE62CE40A6C4607853FA9A6F0B4888DCABFEB73595A69B5253C00AA7E398771E41F5A30D000D1B0C76FF20F898DCC70344CE4630DF98413005DF62BD774F9165BDF7DC5636641FD55EF32BE47A23E715775C0732B565371CD55EF3FB84E9BBF069BC6E4FA87FA5726C3B001803E3FBAEA1B609E3C1DFA3B60DE3B726A11676AB440616A1564AB1D386E23E9C5B3D9C31880E15E4A3B6
	13E3CBD7A2D6BC854FFAD81DC73EDD2F76DD9173080B5E974CD74819AF667340948C6F7A550885F2ACB99731299C3BAD8F3114E790CBDB07D8A3476E4DC7EC9C47F28A918B635863C50845F12C3298313CE7911B3C81316EE7719D429B2A7B7C74C4BCABC6F51F5F58FE0FB4F967D7586C1DDF6ADE6F7C7E2D3A4FEFECBF7FC35E791DBF621D5F4D473D739BB6FA675765718D784B732B73789E7A65792D6B774E6F8B0BF7FE892A7B7C4676FBDBF51F5F58FE4F2B6E739B5BAF426165D99FC3DE56B357157A31E7
	DBB5BF4BA7G5A1CE85E965D404F65197FEB7EFDFAEADB83571DA00EDF6373F18ABF1747973088EEBF0B6EF91E8957A91106743CC88C7BA3FE76CECDA4BE9F33BC4E53F9C510BE5F76F91C2713A5FBBEFE4B63BC1D7D326763635B154FC7BC13C1BC6B259E1FB5A31C67DF7979B9A19C7BBDBFBFA716E391CD08E5BF0758C33B916B64584DFB30FD1F4031549644A2B9F6F7ABE2427308E537A1D67EBCBE9B561D76EE4FE47918FE6AFBA6CF0D696791BFDD0F3B1076139C4FAB5769AE217868734AF53A0127F9FD
	825D8C1E5E20B35E5D9BBF3EE2BF462F051ED95D7A0212F7A7453BDED072FE13779387C0977582BF27ED44BB5EBE67557C0A23D81956C7E96D79BDF9C14D2FD33437EA7A35DDD2739BA75A7BEB7ABD7C351A5FA451BEDC53EFC21A1ADF34E8BFDD536F4CB2B53F1922FD72EF347CAC99EAFE5922FD1926DF6BF2B53FC6513ECB53EFF3361A1FC334775174DB3ED24DAFC0348F56741B1B23D74FA2B47DEEDC2D67E750747BA2D74FAFD6536FAE0FEF40CAA5C153AFF31D1EDF4AEF357C9E70780E3AD4F2B47D4ABC
	3EEFAE15F2CD3F270A747C1AB57DEA0A757C3AB57D1E7578963AD4DC1AFE8DDE1EAD536E6559B2FEDE1EAD331676AFFCFF6B71ADF40A7B7FD6598EBF4D4F168984DD620B588EBF6F6599B1D32F580F719B267D1F0197153CAFD25C752212F792BFA3E69C6826F35EFF3F62B3EA44B3AA6AD55B2DE8315D0A56BC23C2F54C69906DFDB47D7E28BA668C906D03B57D1ED19DF3C6087686CD3FE05533E5E2C57B84CD3F5BD54F16C1C9F9C94BEF296E18B3C7536F3ED65DF6C453EF356EF9014D1AFE29F4DD3458E6CA
	FEFF7F7455DF5B770B7FC29C7BC75CE9BAA6797D6E192B9F37B0F27557655EF39E7770F63F443F2FF69E5756DAB9F66D85341B403137AF205DC40ED9AE21DD8447AA3EC23BA80EC5FC0DF6F19CDB31846D92B876D09A5AE53F0C587C25E8D740311B1721DDAB477E328C6D3AB8B6AE836D46F16C4D8C347363D87FF2348B61D864F2348B67585EEC341B4631C73351EE8647B62EC43B14D7903BF3A55AE5F0ACA387319A0E5D3F1A6FC566D89E7FA639AF47FE1407D8B0474A56A1964D31277279DEE90E6DAEC4AC
	4D0058CBC508D5F16CE0B1E25D9CFBFD83E2BE867EAD749A773DD2AE79BD793FF0E644EB7F05B84ECB4E0970117CDE70DFB8B3E24DFF614C085CFA2C2F13F8B97B56E3B907F2EC618E341B4AB17B8E341B4E313F7133A5663F0A5801C634CB65D88CBFDB221CE3DBF721DDBD47FE454F119860D8CEB35A39B87683FEBEC4G470A79D990619CFB229531E99C2BEAC3AC71FFF0DFC7AFB942D25C5731F8945F8966013E0A58000C02CC647657A7396D7FF5816D43C19F8D92871272BBC3FA716F507E6179EE7BFDDF
	21FDAB68CDA0BEA0C1A0D338FD72E7EE7B72A5FCAF9468E70224CD05FC84691C0A766BBFF75BE7AD65FB21C06F87928C9285B2035B37A53B6D672523FD726F21BD86298169869977FB349FDE60361F19017601208F85B1004403243E0676818B5D76EFAC675FDF81FDA348G08AF48A4EE7F74A2377D3359E89F897A981084100CBFC0D97C816D5FCBF15BBF32926D3BC06FG19G9282927D87FC4FDB1A43573A831698E3C84F812987B145204DCFD7737352818BG8983198AB2135BD4736FC0263D8E4FA910E6
	90AB085F6BE873CC9E5A84839681B28DC478A3646D9F51E677BA3429812C834485628F924AEDDE4AC79B83E031A0735FB0246701B43E01B6BD05E8B3G58A848C410F01098EE93DB04B6890025CC03FA89528A621806B636E23419GD89048941069A009EF22CD7C863449812C9C2493E484A460CDDEEF3FF4174BFD75E89F867AA81099A049EFC13E3D456B6DE2377D75BB503E99745DA063C082C1A238BD7DBC13E8BF58485F6F03BE9EA475EDC3FA99C8475B3C5EFE65366F580576AE507B02CC820984096576
	81DF3B6DAB79BE497953217E01D401F4010CCE47BC194C77C6CE84AC84A49AA48EA465CFE833136F07AC862C9E448462839244EDFE530AB6D3G1B8AB293A46D9D4867F750263D8DEDBA81330278018403C43D434FA59F45F971996F603CF87F287E7A366F0CAA6745B74874AF0CAA6745BF172D65785FD1653C78D6C1BAAF1EFD91B9653C0B1C76DE5467B46E2212536BB27D0FAFAAB95DAE63B47D22BE27B3DFA127AE4E69412F74B97D6EABA5A70BAC4ECF3FD6F27A16CCDF7935BE27E3CB1013BF676437C41F
	534F17A8B97DCD96E75C92FDCEAB5374B9B5ACC5CED11C13F329BE2787176A175D6F16AAB9495747FE3ACC49699A59FA2402F46414980B1C8E276BF372CB57AF3B6752151C5E13797FBBDD1F5372CC3E9701F3EA4E5467F4B1D33F6CC2B2151C664A74EFE56AF31A1B4577A2F0CE25D97A1C4CD97AE5F7D7161253F9197E39ACA5274932B5F7B33279DE844EE9598AFDCE4DAB744B6E628A0FBAAE738FD9214F691515FC9F8167B4EB15BE2752D57AE5E7DE2564F40BBC9FD769F3BA144377913C071CEE4851DFF3
	77539CA5A7791A7C8FF2141CBE16FDE7A0F335FEDB7046EA3E0F00F3DA30C61FD3759AA5A7F99DBE35C649C91E0F8F2C5167E44E65FB86B8275BBD3EE1A567740C47B72CEE1545790FDCFDCEEBBD3EE1A56774817F4E77944EE969DAFDCE3B57AAB9E54A5ECDDFD82B4F6951357A1CCE2D637B83B8277B5669F3FAE91DFE594D49D7F2FAD7663FB1DF3FFD7AC7BE5F9F70BEDFBBDD204F69E001BE2771857A1C1EAE503F6FFC8B797EG4E6909C2FDCE31057A75E9D911BE2766A27DFCDA381E6F8F601C2A566BF3
	322D57679438DE1F534B6B7573493F186F8F601CFED92C4FA93ED83F0E2F59204F29F303FEBEADAB617B83BEC0CE0DA57A1C4E1568F38AAE51677447927DFCFA261E6F8F601CBE2ED7F212BFDBD6B7A8B9254B6F0186A527B9B24E639B141C70DB1D0CD3708E3EBF00F3FA4DE35F241CD372CEA527A3B27D361DCACE39727D83BB75B95D5E487787F0CE139B75B9FD6C31EFD24EE9F5131213DCFF30C93FBE1D63E76F44F3CE13F669F3FA4DE35F24BC4E645D7A1C366DD6F2DAA12BCFC63E6731E086F23A45E34F
	231C53E40FBD0F72B8BF765873284847BD7AF57C5DBDFCFFG67745598FB9EF7FA6CF93CC926BF6B316771A3D9FD7A21471EC7F9FD9A64FB9EA7F2CE773468F37ACD0BBE277FEBD1F212772FB6F86CF91457270FDB111301F3DA31CF1FD37BBEFDCE576DD3F2126F73FAF21FFEFDBA4B3F311D7CA1F2FAE80CEFEC7F5E639B5BF2CE1F376B675356F62D7AA41E65B86A5D3B6A55973D739B7D4ABB3F5EA55E796DDA6A1D5F06F46F7CD6E5FA6777D916F7FE9F292ED99A5BEF2A6A1A25317D0E692ED952761B2E3B
	E6C95B6F046E1AA5ED3FB85DB5CB5AFE27F557ACE97BA5682ED952763BC9F74D12365F62755E79FDC7F74D1236DFD609176B31D557340C6D77588E6F7C82D43FFBB4365F25DD5E7975F9390FE2066E1A96ED3FA1AF77D14C54DD53226DF7710A3F01C94E32D9AC7CBC677F60F99C7C21B057A5A412AF93429F72A1C21710DFE734596C366C8EF36F004DEE3577DB4C4E6CD623F530302DFDFFB67BC2DC3678E5F417B06710596992E66F35434FDA1349A54C2BB7C3E090DC92795EF9D1D1917CA2DFB8273F9249B7
	4DDD42DC7A49F1F9444EA161CB4F4FF4575E7C7B5F859F2FCF1DAD4CEE92668D5073111B0439E748814AA36478487784E17617AE213841C60E1B4AF68D1833071D6621EC17BD5BE93619321DE70C0E6CBEBC5CA73B17FD3857D98D6954F2F1C10A321D83E61354BAD7455AE44F2EB51DA647DD91D312AF44B2C75D3247683474CA4CD846C97F2719687EEFA57928E0363DAFBB17E4E4AE8D0F9483D0338FDBCD90A60412D8BB6C825BF9874CC3163E3310337B06ADE64859E411C4D3DBFDED35AC02452246880EA2
	9D39419774E028ECF10BE41E593956A5FC3DEAC3B6BB7F88AA402043E8BB1BCD76E592C5091622D4DD71A9BFE9C97BF7D29DBBE5C97DD7E6034D8559AA92A4B1B4597A6CC30374BB6095107656A1FB7F10F1D00CB0C95CA58A19C377033A0479386FD3E05F2ED9FB29DC3CB73AEE3DD318FA5D077370DB6C4AFBA7A10E2D91CB79487D6D9AF6667593F46E2D9970010F700C6BF939648C8955B32EEBFBDC6D03C62B55E5341E2C3519F6C1455FFE56C50ECB934FBAFBBA17046FC49BD149F43F4CA53CCFC8FC7E9F
	D0CB8788C0D76EBD46BCGG0858GGD0CB818294G94G88G88G8DCB81ADC0D76EBD46BCGG0858GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGGBDGGGG
**end of data**/
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
			ivjCommandPanel.setBounds(111, 16, 486, 246);
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
			getCommandPanel().add(getJLabel8(), getJLabel8().getName());
			getCommandPanel().add(getIRDAYesRadio(), getIRDAYesRadio().getName());
			getCommandPanel().add(getIRDANoRadio(), getIRDANoRadio().getName());
			getCommandPanel().add(getJLabel21(), getJLabel21().getName());
			getCommandPanel().add(getAutoYesRadio(), getAutoYesRadio().getName());
			getCommandPanel().add(getAutoNoRadio(), getAutoNoRadio().getName());
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
 * Insert the method's description here.
 * Creation date: (6/14/2001 2:31:41 PM)
 */
public javax.swing.ButtonGroup getIrdaGroup() 
{
	if( irdaGroup == null)
		irdaGroup = new javax.swing.ButtonGroup();
	return irdaGroup;
}
/**
 * Return the IRDANoRadio property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getIRDANoRadio() {
	if (ivjIRDANoRadio == null) {
		try {
			ivjIRDANoRadio = new javax.swing.JRadioButton();
			ivjIRDANoRadio.setName("IRDANoRadio");
			ivjIRDANoRadio.setText("No");
			ivjIRDANoRadio.setBounds(418, 183, 43, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIRDANoRadio;
}
/**
 * Return the IRDAYesRadio property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getIRDAYesRadio() {
	if (ivjIRDAYesRadio == null) {
		try {
			ivjIRDAYesRadio = new javax.swing.JRadioButton();
			ivjIRDAYesRadio.setName("IRDAYesRadio");
			ivjIRDAYesRadio.setText("Yes");
			ivjIRDAYesRadio.setBounds(362, 183, 43, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjIRDAYesRadio;
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
 * Return the JLabel21 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel21() {
	if (ivjJLabel21 == null) {
		try {
			ivjJLabel21 = new javax.swing.JLabel();
			ivjJLabel21.setName("JLabel21");
			ivjJLabel21.setText("Enable auto config?");
			ivjJLabel21.setBounds(18, 218, 296, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel21;
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
 * Return the JLabel8 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel8() {
	if (ivjJLabel8 == null) {
		try {
			ivjJLabel8 = new javax.swing.JLabel();
			ivjJLabel8.setName("JLabel8");
			ivjJLabel8.setText("Enable IRDA?");
			ivjJLabel8.setBounds(18, 183, 287, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel8;
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
			ivjPagingPanel.setBounds(38, 273, 630, 303);
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
	getConfigYesRadio().addActionListener(ivjEventHandler);
	getConfigNoRadio().addActionListener(ivjEventHandler);
	getAddButton().addActionListener(ivjEventHandler);
	getDeleteButton().addActionListener(ivjEventHandler);
	getMenu_Save().addActionListener(ivjEventHandler);
	getMenu_Load().addActionListener(ivjEventHandler);
	getMenu_Exit().addActionListener(ivjEventHandler);
	getMenu_About().addActionListener(ivjEventHandler);
	getAddressTable().addMouseListener(ivjEventHandler);
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

	getAutoGroup().add( getAutoYesRadio() );
	getAutoGroup().add( getAutoNoRadio() );
	getAutoNoRadio().doClick();

	getIrdaGroup().add( getIRDAYesRadio() );
	getIrdaGroup().add( getIRDANoRadio() );
	getIRDANoRadio().doClick();
	
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
		
	about.showMessageDialog(box,   "Copyright (C) Cannon Technologies, Inc., 2001" + '\n'+"                         Version 2.10");

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
}

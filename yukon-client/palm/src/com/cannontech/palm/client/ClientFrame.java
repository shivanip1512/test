package com.cannontech.palm.client;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import com.cannontech.common.util.SwingUtil;
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
	private int retryThing = 0;
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
	private javax.swing.JRadioButton ivjAutoNoRadio = null;
	private javax.swing.JRadioButton ivjAutoYesRadio = null;
	private javax.swing.JLabel ivjJLabel22 = null;

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
	private javax.swing.JTextField ivjRetriesField = null;
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
	boolean retryOk	= false;  //ecs 11/02
		
	//get the input out of the fields
	nameThing = getAddressNameField().getText();
/*
	//ecs 11/02
	test = new String( getRetriesField().getText() );
	if(test.length() != 0)
	{
		try
		{
			retryThing = Integer.parseInt( getRetriesField().getText() );
			retryOk = true;
		}
		catch(NumberFormatException e)
		{
			getRetriesField().setBackground(Color.red);
			theWarning = new String("  Invalid Retry");
		}
	}
	else
	{
		getRetriesField().setText("1");
		retryThing = Integer.parseInt( getRetriesField().getText() );
		retryOk = true;
	}
*/
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
	
//	if( (valid != null) && (secOk) && (classOk) && (divOk) && (retryOk) )
	if( (valid != null) && (secOk) && (classOk) && (divOk))
	{
		//clear the fields
		getAddressNameField().setText(null);
		getAddressSectionField().setText(null);
		getAddressClassField().setText(null);		
		getAddressDivisionField().setText(null);
//		getRetriesField().setText(null);
		
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

		if(( testerString != null ) && ( testerString.length() > 0 )) 
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
	
	//print out the retries
	index++;
	getRetriesField().setText( configVector.elementAt(index).toString() );	
	
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

		String retriesString = fileProps.getProperty("Retries");
		configVector.addElement(retriesString);
		
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
			e.printStackTrace( System.out );
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
			outPutStrings.addElement("Retries=");		//ecs 11/02

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

			//retries ECS 11/02
			String retry = new String( getRetriesField().getText());
			count++;
			if( retry.length() < 1)
			{
				String defaultRetry = new String( "3" );
				bigString = bigString + (String) outPutStrings.elementAt(count) + defaultRetry + tackOnString;
			}
			else	
				bigString = bigString + (String) outPutStrings.elementAt(count) + retry + tackOnString;
			
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
/*
	//ECS 11/02	
	if(	(retryThing >= 1) && (retryThing <= 100) )
	{
		tracker = tracker + 1;
		getRetriesField().setBackground(Color.white);
	}
	else
	{
		theWarning = new String("Retries Out Of Range!");
		getRetriesField().grabFocus();
		getRetriesField().setBackground(Color.red);
	}
*/		
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
//	if( tracker == 5 )	
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
			ivjAutoNoRadio.setBounds(418, 215, 43, 20);
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
			ivjAutoYesRadio.setBounds(362, 215, 43, 20);
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
	D0CB838494G88G88GD1FDFDADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DFD8FDCD5657D3FB5EA542828D833E24E95B5EB34D8D1312222669A9595ABAA3778ED5428EC5966B63771DDD4D7C4C445C4C5C545A4C545D2C4C4C5C49139A8A8AA8AAAAA88976E852EDE6E3D5EFB7923D27EBE4FF94E67F34FB9771C035E4D79FAFD641E776773F91E777367BC7F4E731CE348D9B6FA6A64349C03E172D40378EF4BE403A16AAD0321357F338C44D2163552EF766FCB10B906652B5261
	EFE6FEEB4638FF37E6E49BA216E542F59EDCC77D2BB5E3A17C1EE7F845967CA67C50082787F45B4639AB7563A95F0271CC932E0F2E4B023F1F036483E684894EE7715B57650AF88FDCFFAE7EE6B2B94DE0A87B1259F41C4C15608922DD966872C192851B6B5665097A91F04DECC6425F70AF396DD878BBDDE0F7CDF919E857F2F308E1528B8F264DE2F1C942C7A9D84EFDEB453F7906ADE9ACA32698FEB5E3AD7C2DE8B8533D235351DB54E9325B9DF60F25332748E93241354DEA31FB4A4B2B053F3BDD26DECB54
	F246B170E426608F9C615A3802E1069616AF29F01D8357EBD7A04F5F4C2A90F9041C89B1A45DFD17A64F40951CDF788AAF4F4C132B53DB8AD8788F89794E7CA6003EGB04E655F6B104B688264123F1D590E036B09A019AB91FFDF4073613A8C24E1A5F25C3E9AB9B6DF385B105271EA5AEC8D0EBDABB9376015DE0E467D2B539789F563B7E52CFC63C0B791A4FDD5EBC6A1C8ADC88F489221CE86ED798A7ED6F5F82A9D3D3D8EFB1553394BE6F66DF718ADB678DDDE8E9A91BBEC69F4384C86832FF7B71CDC2B42
	87E544AAF91DE34253F64C0075A47C709D860FA7D6A8FBE55AA2DF7FF9DA08984618D55E3499D633FC0B944AB696F066476EAD0E478A38F1359767E515A1606CFA88F4E505184F4D1FA3177A749B8D599B92522629F099796C73E9138A45FBE30D178B4F63C74BD99E17815E84E28389G8983993B8673780DAD9B074B63BA2867AE3B4956585761B439BC3B7A2CB63345A566F554BA3D3CD6936E775CE57B71FC9933D1DAC6098F3F261AAF09E3225212447CC811648B0F7BB34D38F33F722D9B1FD39906887744
	5A2FF899CEA1FC1400D7C8F0DE362B841CDD5703AE82045BFFE78743F33E667673A81C6F8B38D10273F0BE9242E1579E50E52FED4D60655BD4415A3FF0380E85C982C98149DD571AD18AD23B8E4B784B327517F89FB5C84A177B7E2EEC817C358CFADC26BA3B5BE332F7DA0E1ADCD6D3074D62B608B6FFACE3795AA1739BAF5C5F1D90479E0B5D62B2F92C8EBB048E6DA2D4A6B73779EB599CE66332885AA6EB2F85A268F59AB05C7FABE3E936195C1EA3CE3349E371DAF07D642D2CCE741B0EC20890G3B9F5631
	7C8A935A5C9E789D308E736A2A6D8CCB046BC914AFC3DB67425FFDD039AD3672F223351351B0394EE19DFD3C0555134CA268F3C04AC002C112C10A57C358A0F9452D9911A0759BG5F00776E1F1B310E9AE75DEED87B7A2B2AF51449E00958C616A86B684BC25CD100A70298B68287101DA06DA0A3C0C6034400A40198CA314F4DEA099043E65C24F131F4C82F99DF69F53EC44F522764DAA6518B69AD62E93F1CFB98A5D892964BC74EFD9BF11F23301F2238DE28381EAA79BDCBA53EE9125F73C1E60A7713BF5C
	27CBFE333E0F572F42D3C2FF8965D08152D20AF56C592D797C5E5BE5F2DB70EEBB68029B45E27656B8EE7BF4B98B4F528FA3882345E9311B6107DB2C6B4F15CF775E07DD8E3AC762841C354B55F0233889FF35FC8E47A18C69DD0D7A5F89FA13476332F674F9AC0D960F47EA6F767A3FA3F0F14B1AFC3718562F5AD8DABD1046A010D0CA6B19ED73A477D33D43F1264F5944DA8A713EDCD0446E4188300F257AFA47F6B6E61CC057E14235D73FF6FB73A5168B8B63F24B1247D1F349F1A86B612547F15D0E8283EF
	83260875F51AD817E60AF5F6360157FBEEFF27E01F3E894607A0CDA0C3A0E1A0F1A013C072B643D8EDB366535F3787DFE2787E54F9FF05F3DB3CA37892F879FBBF7AAB3C4E1E2A503E67AC956A775C198A75FB6EEC057EBDF52EC26D1E623A9195ACEF9C83F649BDFACD856B1FBA9DF63BC5E8A848454067A4FFEEE269305A2D1EE3D6F3374553E8BDEF013943E6F62FBDA17401B678BDE2B376CB83AB30DFAAB87FFDC3CC5B832A635B117744250D5B4C6F25891B3D7D9267F42DB0963158AC3D10D1550EBE3BC7
	9C2B8B7D2B083B2788D8DA9B0BF73F455E57365B8A2DG2F837320BE90DEEB31B989AFDBDA4762AA2CF6583B2C5D47AD6E43A633553156A4A43EA8FF5D3A9D791279555ACE19D9DB15E0D643CA52DD3866681AE28A776962BD12F08FB8AEA5D8514AA7548DE218AF95D58AE1D6194D3B7ABC9E07DDB3BC93DA7804D5A206B57EE40D901691324FE399A6B8334448A7440D146659B32F934294F2395154EF41G791F860B2B4B616A35188BCF1B7ACDC52681CF11F08F94C9C2DC6393F7AF86A6C66C8D25DEBB18ED
	402B14F81D1AAF61D56FB0197FEB3CD8E0174BEB93717A58AA61D5B3E8757C57F83140AE1757E66275541AA0AF2F2A8EC75FFF0F18901A1259066128ED613C9EB7DD0B755FE5F13B05EEFEED2F234FEDA9927E0758BA4F78325A4FF49A24CC12205A76CB83D1D2720642981561982C351D35CDD9DBE02C8CD2368547A5D52DCC0F6392F170D56D3059F833EA40B527692D52B52706ADFC4DAA907E063270841B2CB6B51BC8789B8B32360C594CB465C8EC12C49B761B6B378C236F9CC6FF53B07AA706513FBF0CFE
	DE073E3EF2987D99CDBDAF0F22D3592C9F341C6D3340F4129577BE4BB9F1CE386954DC056E284956671DEBEEBF45468DB64958126345ED6A7D5266B6B61EBCC361173731054BFEB1CC5E0FC618D818CE1735970662EC30AC5607F5C5523276C05A58FA65C8789B8E42FDE79FE53E12EAEC28584AFC93F6605A64D830CD84C9957D3337723C60F6B7D422DDA0DC0F5E6A350B1159D56DC43B89F01DDE6E35ABA8175A7D036C6A81B7CB6C82E4F677D521DDB860E3C1124B457036C96D5C4DE857837808EDDE3B51B2
	3B554751AE9970D409DDDE05546EAD32EB87FC90E4E485379BA7337B5189344B5F8E779EC84BF6B17F37F33B446D4C6ED5DADFDDCF588782769798E4BC1F016BC67B3325EB40E6772FD447BEC2FCBBF87825BB146BE93718D838CD001BF7E05B9259A61D73D4395D0ECE2B301CA1564B4EF6693808E3BD6DB360EF17556576544100F3906B363D1DF1E56B9DA2AC564F63B358BAA82DBDDBBBFA0B7A7AFB0BCC9D509E1A2D1E72723D2CF7B8EAF57719ECC275B570755E2A56CCB10C419D382EFBDC1827C640F5AC
	C8528EDC9BB63621ED8A59B68B6DE9F1E5EBC6ADC8FBA556791FFE3D5D603B1ED483D49CAE1C62D68F886DF313235AE13654426DDACD435823D64B00814B651B5D52B56F68590F274D54A81741CADE9EA3ABA5EB5E7BD727F3CE7F37B8977308F59603C56E8198FBC356C85662FFA04CBF591CE3DCA52671458AE16DEEA74C31C0ECA0FC4D6D93E1AD0E059DGD874CEEC5B8F8DC85B36C90067D7C1D8A03CDFB4D732790D58C50B6577FE769786E19C2FB3DC457C38EB5ECEB16C381F5DE82838F8276A7AFA606C
	FB521A2BF8BE0C2872664704050569065CC271B94023151806B830C95F05E9C8CCD3CBC345AEAC5F40422544D7E39CEC40354953BBB10E86708F24B85EB8A10DA3867071A0D95598076DA43B27B44746947E97D598FEBE78B6D7E378CF3628059FCC61974EE66D075EE019077FAA059F863E09947E6B9B25616798A1FFC05A0DD84E154DA55EF23E0C21985E538987D8FB646F001AF38FEDC66E9E6095EEC46EFFA8D5631EEC443CB9381E6276E3504C63EEB80EF127C03845B59877871B54626E2941386337D05C
	FE8C0CF95C69A7B06EA188B71262DEDA2E0CFB826832F7E3191DB9711F15595B46F2646E5758996BF4AC71AFGEEAD3BF1EC696E170EB7C2G0F82C98419C8E9D8FFF29D63F0B983ED6AC7A276EC61771CBF43ECFCC6D6A26DC7E312FE21590FE66E616DD25E9EE5BF5A5F495A61B2406B77E0BF7AC1BBDBBB41FE74B07494A6FB3730E64075BFB0317443B8CE3C3E5344629546F186DCE75A5D2A76DC27DD0C5F3347F7FD10BFF7F9C9689383777067AE1389FFC340C3A4B8AF3F36D378FCF5B46852EBF18D2E2E
	CDAD7E1EDA9E6FD02DD67CF1C2BF9BDC2B0C7FF5818F2DD546FF2A9D639F833A2CBAFC4671301955FFA7DC87010401C403A456E19EAF6F9876D9DF4D204E333E50A236CE1A8A61E56F75CDCB0182ABD331C129D7685A77623331CAE1FD4933D7790C2DCE408376AA1F317D26821F310500AEE5AF164514CAE5D9346C1347B9283866FEF3FC7C0A9457218ABFFEEFAD58457AF815710750FD729E886719EF192577F9A404B78EA495A43B9E47E30183B89EAB2C47721D5E457A34F6389E82898109GC9G19881233
	9F4B79EC075A78CCDE4EDA03B36FF3821E9F51FE66638B4366E369FE399FCF773CA9186E7A7DD8164E1D7EF1785647CF49A1E03FDAD94EEEDC24DE16D80CE2B97E3FA34AF28C0570E2C09276E3B94EBA096919C069A92872AFBDA5BEFE4A74949CD04B53C9AD482162GF29867A70777FC7C3ED0DCFB8E2865E96C54A56AF94A26AEF59ECB2F182707B7C873B488420AGC9G99FFG73B42B9453E3B80869392B593F74C47878A953D3F6D0ADCF97ECC28E8D446168F17FB818FD7C149C82D5B964EFC68E230943
	0C937EF1487571D3F2982760404B6B6F1B554AEBA25826B7E0F9B515A34F3C866479321FBC930765595420D67FCEDF50189F8A63A01E16G37B4AD3686FEBF0F043F6114161F76333430315058861CC3768AFDD0AADC679C023168A1EC2F5B3A192D912E1B8EE15B7CF2871BFF62B82741E13543F4DD9C53DC68E4EBD38E17355BEAC7EC0400F5BADC4203A17E9C35D518571BA1CC8F88472E9730916C7A906671EB278C5EFC137C361C3A54671D7E085876DE51B8F81A5B5BB09F22A99F2EEB43FC0825FCF8355D
	1B7637A43F4D6DD7BE9F3E3922F170BA7204D97B996633E675E71831E675E718AF1B75778D3CE156FA4649737FA9E10DE58264FD76E1AC13796B99D6G579504BDA5E08DF0EDA6AC3318E10307F99B423112EDD81EC1E457D8A16CA3006B3107310CEB04FD8371F4FDD0381EC457EFD6DC29F27046B9710A45414637CB053F4FAF18AD140F5BEDF18B03B82349E392F56FFB585E961F674587582AB4BECFAA2F1CAE5A54F89C559B8C58BE7EFD0DC67B58E002160A370F153B14631D5CC6A8D31006C6EC9F67D9B2
	453A5F53086D63C9ABEB9FC7B39B1078C64C2F9B865466CB639BF97B1D5A68BBFF60F5F0DE17F6FDDF5C25DE5F733BD83B6BF67439BAAD4ABA3D3AAB17F45E11B09B14302C931FAF887E9E132B5B62F3CFEC917C39CE4BFFE71756FD434B656F32F2A9B1D03FBCC5AD0F321BF85E94B47966918F6FA7BB256131F99FAF43450DEAE5D8866154B7E1994EBF0AE5E8EE42B2DCA62CD90442F5A4C8DC931661DC553D3649A22F94CDFEA72B247CCAA93D7F572296DE5691F18D6108D6F833F7C9435BC475FA7A42056A
	751A0DBB8DC1C7D85AB697E31E1442F53D980767885F274473647A8DB806C9B802F91237D1F85EF834B523G2462A866491CD2E59A1AC057F3D4FBBE4C53705BE6E99AB6D31EAC59248CEF88428A9EB63C9B0ECB435BC2612D5C2C8CAF8C420A9AB63C2A93527058BA904F1B2F37E05E449D453C69594A72A67DD8EBC69EC869B14C1B63654A386BC157FEEC3838271E14463D154A373BE629FA79B6F63A9CB69BEB3D08E7349B6B35679872FC2A0F718C076B31A00944B3221F711C8457194D3E5C72GABD660FC
	1D62EDE1ACE8ECD62E657CC9409B1A15EBB96E8ADC4BE981DDCCB30E934D5D8CE34F0044E7C64275B94A8FCFAB6B5FBC5050959CE7E7B1CA6D864A131F8F30E7A8DDAED3AFE4C5235352E908BE4E627AB1BD5F6EB9AE2EC909FFC361AF8F6798EBB2C57B51742CBB7D84374B957F161D407548570BD81FE336F4197AECB014F038AD871D62F6DF4003CF30F0A696E17DE8A63F356BD9F93AACEE6BF961A9AE5A9A05F181CBEB02B70E752CCC7A2DEEAB3364F69348EEBC59AD93468F421D3D4B6462E9285B06E9C8
	E5FEA7215CC09AC01CA7516F3430774AE37588BCF8BE3C4B36A509F97DC8B15B83ADF946DB4D0EFE3071912BDC3CDD9D31DD592E3236A3986288BB096B3B13F7CAED2281E763137170B775A436B3833BB02FF2DA300E5699D99D2907EBB348D08B5651DC0B74DE8A81FCCC0B37CEC6377856DBDE97EFBD4D420BEF61F5F19D618F89F8E92B9767F554A4745F6CFAA468525BF09C96A62CD1B95BDBB3C634A3B6F51046EB042D1F02F6910485CFC13B68F6CC7B61C6CCFBD2BB263D60880BBB57846586D2EB4234AF
	BEAACDFBBB609E13B73D01A64D3473FE4C4453B81570D1829EA141F95AD752DA7DD85095F6A07FB5A7B1CDC104DDFF1266E004E53722DDB4E1E7E92E965B014F9BCF9553199D408ABA71F94955DB979BB47BAF286550568879F427605F82FE9E1020CE5C73B7388A1F2F065FFB2321FE44EB2AE70A121A9ECF8B6B64F9965B69DD2FEF5E3FBA1D6777CD9B146DF63A99468EE66DF61B67EB3050C72EB573FC1DC6F828009BA5B84F076582AEDC03AE460CF9D6A82C7994D860DE32A0E6AB453C2DA72CE4935AB593
	563F896DEC04D5ECC63BC142C6EC26B235E0BD3B2DCC196E11208B378C176EC4217F0B31A857315F9370F80BF29D5B3E95573113C0E76CC2CE4EF264B90630C8810B066B38AE64790C9BF90E83ECC21722EF6A563A9FCA7A0533A85DCA1E9582DE502D64B9368FF91602EEE4B7F27A3530AE12AA5AF36CDD814B6D017E3F876D7ED80EF53F16304F05791C8D2E03FAB0BD6F33EAEB40F915777739ABB6477A1C6238650A46416F4BD7CECA1F23E55FF1DF9A0E8B147623FB4433FEBD5E7B1237173FEE45BEB089F4
	39D6067D23D57A8C144DB958733FA2D68E02FE75A929FE2AC44FF93D38925B0B5497C38D36C71E51BC0358EE65FC82443F6D49DF98C68F9526F3EE4F35A137E8ABF21B54264FEDD53B125BA399DC4F7B61E96BD99D17B403C5472CF633E3G3AE3F6B24963AEB256B5B69CEC2CE9BBF8202D3A9EFE70B40D9E1066F5590C27B51FD9CE925312F91A7F0D1B3FCC0C7B976B19CF174966764E252794CBB11EEE47B6CC774E53186E9FEE13260B79AE1CAACD77AB5B147932C516AF13AB24FA1626BBE57E9BAB147976
	3E684FB9C5ED27FDC02731DF2D977606C440F5ACC852E96CEF3E4E5498CFB719BABAACE659F8FA6BCE5CFB15C261868A7BA90A4F407D8D52FE866F6721DD4AF6F4700C37CD8ABA2355BE0DB48BEB87E7147D755D829EF9C659DF9FB4E2FF9D8B3A35B69A03886B9241F09DC6584F3A98968B5709049576E07BE468C56E371FE66DC181DC1701540374008C010400C4004401A403986CB05F81A9852987E987998299899289928B12841282126500FE5E01F1C489F1D440F58B4890C8A8C8B4C822C35CBF88FF731C
	FC0FE0857CED81998289850986C984C98549BB8BFA109610A110503398C73735FD32B7FF997FF6115B67E27A14E510E717E311F66DFEEFB869D1899F0565D7080E7ED2A4C907DB0A0A2EDE431E1597ABE7D6B631929BD63675A2C7D6562C7C58EFACFF3463F97D2CAC2F59FC0C5DD7DF9A3CED00B2DE651936957FC1DECC575131E793EAE764AEC7D66B685628E0CBB4EC1799783D4A6696ABA578FC03774C9E5BDF793578DBF90609374351A5723970DE362CA64EE17386F14E958D77C1BC48C410EC974CA7DCB8
	67BD6F715A7FE48A5AB7005E8C92G9286B25605775131C6E5DB166C7216FF0ACB33AD9366B2D9EE65D8B8CC408B5C4A3170D7C7F0ACDC813A51EEEC23B69C4536AC5D0358CF0FE1DBD6C85804E6EC4B6ABD54161D904638F09DC8D81800250B7B05B97675096146C04C1F0563CF7D61F198070DE32A413FB3F7CCF8597E304FDB36BF26F7CA0C865E11A089A013C0F27A30AE4474FB6D83E93D24947435A0BDA001A0E37AB01F4C457E1DFFF7DD369F2FAB33B7886F9768D376070B85BC31CF599F7E38987B4309
	20EB6A47E713BF9BE0E90B026B841089A069831087A0BB87F08DEA6DD9F606CAF79FC91343D9EF693758A40F0AF85D3BF7834EDB1A8630BE5ED30273AB8FBD1B3BCD186F75004D88E6B7C04F73851C6DCB9FA16EA767F86146616A8C8BC3CB57A36AC6E85870B82A060DE31A0E8E6B6BD49D7DBC4313C27E745AAD103156CE3E26F5C8184F3231DB44G5647DA3664A72EF91D9466313F8509A17D0873DE3DC5183F7E95A41E74F7DFE0FA1E7F8F14E119A4D1197C358C671251D4A66FEC951E1DD1D9CC902E59DE
	FDFE6D597A5F1E9FCD9AD0761FB77C57631036A3BC3FEE9766223F84499A4435404C2D723E647DA1EF7E0D93560F7340F667A035459E61CCGDC8F9176BD8F363B410318E7E57DAC5CC83A5EA5DC33F38D62FE293ECBCDAF8B63F2534E637863A54761CF1B4B736F777D6C37294F63B84C56F6AD2C35C0DDEABF9BF7B8DD16AE0B4BE5B173632AE2D94C97569263A0ED63C0F87B7B6FF2DAFFBE87F3A910B210E61041F3D8DEAB05B3376E9E4740D10B4BE36DB4596A2DF6F634C78C37328267E3A34FE15B97728D
	ABD35E46394F32F147D153E955D38CFBE18AF4306334255353E46A26FC0CD35DE72AA7FC9E7A4CB41CDBC7BEFE1BA16A5A2BB44FBA041F93671F625F14F73E90F04E7F23EFD93E2432BCB98FE3FD10A6109E109167316D7E45D946116D740384D5F9BC264E9E61848A260C9D4270426CA181CF5B0B2AFBD87504F34AB630FC8E0178A30803417535D65E825860F2B821484EF1D4B865A5662FE71BFFFD6A135B15FE71673D77CA72F92DF1D90CB097CEB92FDC83FBC140B3AFA857408ED7629AD8AE6806AE60BD3C
	43E73F21D412AEE8451FAA4C19A7DDD0766953F689718FA97B749B694CC29E68C68CE13BF36BAE9C83A69316D00DEDD166B748F31419E1EB615AC858FD82464E013490F6442857DEB0DF96063F6DB10F432E9B47FFB27E7336C93BADEC8FD50743A5BC34674FFCDBAD58360C00B40C840983C97A061F0561FE9FEEE7FEB653B9C71F877D5E6CC23F94304D7E9672902479DB6E475B2D4A9E34F1829EG9286B296A479DB8C3B6188CB7F5EFAD3074536D96439EA1F975BA2E29D2BD91BD9E56EE7EF5EB18BAACA57
	14B42F7DD6917B53C2D6B79AFBACE6BCE9447193DD5912F0643AE93D0C2B7C54BD46B1F7D088EF4064147B5C1DB5DB921EF8CAC87449B04D2270D00361BDB8ED2648CF6E734E7219E2BCF27C6D151914468D459CDB514842E747E62AD8BFA38B3F21F73628037E405AF5CE9AD6422A4FC55D21BE2B45A3CB6701E35E3CAC93316B9AD95A6B8E9B2B64E97F792C19A2AE671AB26465DA32C14C77FA968663A98F631D3E19A20EE170B3AC1F1E162E6525DF64752A70A21EF1F95726262FA07D1BF6B5FD8369271CD5
	531BC93F432DF6B6EC1074AB7B5474C124FF7EDBB57DE852C798547411247FE53A1ABE167477E7286993C9FFF326DA7AA612FEF0061AFF7A885437E52969F3C9BFEB361AFEAD69A7E52B453F1374FF182B665FC47A67F25474BD24FFE001DA78C3249F192B669FCC7A0B0B5574E1A370996C2EC19CAFC50D4075EB6720F27DDA3EEEFF5720F27D7A27B2FD4220F27D5AA253E71CD32EDF3F1EA6DD3FFEE98AF21AC81CBE1E224FE9DD3ABE2736F4A527E3B27D4D69CACEB5D3381E772B76C6DAEF398A7B5A871BE8
	3D65AA6AEB05B59CA7DC87020CA67CFE81E7E796E32E62673CB8DEFCE438B1BA8BC3CF0FF1E97739F5434621B73F457C561A5F72FE7244D16C83132F627DEBAA7C4D3E1A1F5364B68947502670EA5E17325FBBC59B5E17CEB90EB64D005BC002C042C1622E463E743BE52C7C68CDAC86EC532B4B05E74C12B32062F33313D81FA6DCCD4F4DCEAA6B53C6D9FDD85D222CCF4BE47A63AD4A7AA42FEF5FEBD156A72C2F1C534F68D9DE41F710D3E22BBE274FCF69F35AF9CA1F134B67D96007221E571379429A853E7F
	0EE37982D6A11E2D6458CF0A356A13721DF3CA499FC66F2D33B11AF18CB7BE542A27D26173154786347715BEB5203E2FF46C007ABE6A9786745F85756A00763B20FEBBA0FD97941F47DEAF2C1D3D8452729D2C77116768BDCAECD0C3757E57650433733D04F716924E4E5E925ED34A7E2EF62F962FDD1B04EB9BDE8FED962E87D70BDC5E90560CFE83E2A3AED30BE7E0584259D88CBB22DC70B5E1D8FFF133679C7D62DC1DEF525BE5F2B5B8EC564EF3E278B3047593F6FF8CD178359EB64F6CF1382C679DF60F2F
	A76613D970E56FE60A7E8E36A1F7F9E88F98E059812D9945A08DA04EG8C3F37CB9827C94E4672751F5F88DB78384D38F2F6FF59AD8379F4D056E80569B3FB79E461260D45EBCBB6EEDC03E7F33661B35760G3C1F1BFD1E833FBED9FABFDE6F739CF8326CFE0D5E263C1F7725CB75FF5E263C1F511F73BFDAA1FDDE4D676D61423BGE201E3B2C866B52D99F9A04557609CFE5E374B8D42BA024A9AC20D395B024F5E31DE7F73DB6DFB6813EF07FF17E846C5D60FBA9C50DA773270914F3AA86C5BF4B88510E3F3
	AE8AFB882D5DBD9EEEAA628BAF8A7BC9ACDD824C3179C21E507D48179BC47BBCE1CC4A5E23E6B5592C67054D0B0DE0E86F9667505B312F2F3F867B7A45155857BB2F41F67039CA362697C2577142F5AE6DF7EF2E546BF7195FF06D54706D64D95DB87E1339B4931E76A3BBB16D911456B4E16DA1812EA790B6CB40460967A2B996D2057C5076BF61231E878FD09CFA6394FF050FDD2269ACC47A355E31CB5E357CACEFAD438C9FD6537323EB35762D17DCE06D8EFB40E2328B3BABEC6218464D66078D26EE280582
	AC6EB36D4AD3BBBB9C8C6107DD0BFB892F31607AD4B4E0132E453D9DD37359DA27F2EFC791DF98A9AF2F71F44234FC1738CC02E3EF8B0ECB56FE9777F8FCAC04EF066BA110106FE23C1FF5619E46F040123F0B71F6AF27F8555FDFF3204FE62B9717D944F877F4E33CF90198EFDFB78B3F892EEDA08101B8E7317420EDE8A036353776A85B5A0FE5ED6D2FFA14EDADFBB66F557F2BC759563EB4C52ADFE3D53635FF907D4573B4DD9A67E90455814EFB5399BAD3C0E9D95D0BD856F508FD6E40FD6BC6424A4FA2B6
	C4D81F9BFDA389EB69C33B890499AE22DD4175083DEFC03B96427E1D0EF6A1043D1DC1FB7D890B4DC4AC7DFB08BDB8034E9C93F6DB96E29E420C33918BA76C6BB908A59336E09E1DA18DC26C139C441A88CBDE00D890E12F64A2B61630C797A3169A04ED531EC17F561DDFD5BD23B3FC9B7283BA0BD3FA03378D293F0137173C8DF92E89ED4CB7E835A18FCEE6ED851F61703602639B97323E34466EE1EFB5E330085F351955F7F12CA3BBA7415B1830D3EA675A87A17E209B70DE3F7AA836B1E100A55E00797A42
	D13C97F3EE447BB6C5302D07EBB348500D98C66AB1ECAFC2G0BA37B2D42FBED52EF023188C869CD98761A6398F67DCDF81F9FBB2E3C4F9D327B783A634A7BFC256CBEFE623872BEFFCC56CEFCF4DCF91F7FE332F40E94277B2D816DBA7370E5BFC362E5776392E1BC5D0563690F4A046B9E3C7E52434ED2C91BF749C9182223B5071B6A2A2B6A5B9A2B8F9F2C2F5FD5F5382D2A316DC0CD0D3146487DAFF8CAF47CEB8F9E2EBBF170C013AC0483B590AC365B319EAC23GAA23FFFA14E57416AC0F8B7B14E534C8
	D606A77A14E5F4C8B66E8D6AD316510F8C52B2DAA9FB1E948D3C62EF529FF7086FF6CB539E4BBE11B67CD8F6EC9A4B8B8B0CC11B4EB945F16873E942996F2EAE3705464CAF27E593D667EEF0382DEC844A5B08349D340EF213370DC83D091F9D4989E6B69BAAF3F552424BF5619A9687BF43625D4A4967C7B6BA17528261B9C102C1A2C06203F1FED4ADBC2B64EB7A52B6660733A473A6C1416F6D64DDF8EFCF04B08AEF46FB7B4BEA06374375A0C8704DF8EF4FB1E2FBBC66E6BC3B905BAA5D0B1C7FF5106ADE64
	1456475212EE667912FE0BF70FB47B4B439E1507FDDB59AD48E3CC3E30AE875701A023EF41BE60FB16616E4B28DB545ECBF07B30FE4A779408FD4730FECA614F869D42D8AB619665B933C181CF3AC5F94EECB20D4DA6002E61D6CC6F54EE7F5A2FF9FE7A55DF369FCF57874258AF72D6E5FA7FA660B137AA53FB219B539B873A6290CC6F699E7FF8171FD1738B8961F5E1CC08EFFB427D56762A79C50B76711AFE739C2A67EAC57B94781B75FD35FAD8F9D64DAF9FECCBC1EA3F2F955FF9371ADF0BE86F5474B375
	2979850876A361EF38AA4F2997557CE2C45B844D78A698D44F0C0B762970B77BB63578BECBD773AB90ED4BEE530A6F3D8CB53FFA513EDD536F25CCB5BF0FE89F286997B9C34DAFD4340F50743BB3CB4DEF2CE8BFCE532FEE361A5F84515E7083AD3F62B9EAFEB922FD21265F12F9EAFE9522FD0326DF5AFCB5BF33E8BF2869774182B53FA051FE3426DFC22E1ADF24E89F2B697744E2B53FC451FE220A9FEF2F1EEE4476AAFDE4EBC645C86C27D2855C8357C1A0E1A3313F1C50047DE5B4E10927301F1BC47EFF6D
	904E5F5D8E57A0AD37E3BB98F8F438F6D03D1F3BEDD8BF75FE6E15E17D14425BF71BB0C7705C2EEC777B84FC446D4AF67F5323180FC1201BC869CD73F94F53F0427D32AF5B0F73FBC01843B45D21647D2800375F2164DD590C3CED200B3F83F997793C9B6A52F83F6439DCBF71DC2430EEDDFAE7EB86DBDB66D81500558346562B453D69BB10EF8F60236F447A56AD606360FAA2C8E6A80E65CE1F4131DC5ECFEF9246E7DA674A72C3F93DB3062A0D65EE59C9730BD01CDF446CD44EAF7EA01B9F7CED27F2FE41EC
	3C7A02AA657C627B3273D5C72B14738B7467F97DA71F33CD17D6C6A9176DA7FEA7CC98C3C707AA6B56A8810F8DD5562D5555D8D68920AB7BA1562D85C6E97C2100C77CD0FFDE441F99DC1B4E668BE341B689A4FD944C53C10AC1EAC761F30376A905866D3DFFFC31CFF8F660B2D96D9E4E676EBCA91F96884BB94A4BA7E014AFB71E6E24AFD97AC60ED21EE77FCC40D3C7A94F337FCC987B336B769F35E6440DEE4D606F4F0919476A2455EE335AAD557C4BE5D4D6FDDD867A7D2D6477342EABFF9EB57FFF9047DF
	3A3DE9CA117C3E2A7B4A47FD47950D0337E1074E605903323B70994EC3E770B90611306ADE346BA1AC2C976DBC04FD65C03B1104FD4F01F6E10405BB99465EF395CB585133681BC858E382B69E2EB36FC6EC268B434BA36CE6B77A6EA46CEFEE5AF7C258E09F5AB9897BED9F5A8D91766FFE64B71A303C0B689BC958B79751B70630A91351AE19B073E4341BC858B5E998C7CE98E28B2622EFA1E183D351371430CFB2502E1930D353504ECCD8F2A65A8592D6B39D6DC288FBF1865AC59136F6A65A0DA56C21AC34
	9BCF583CD9E8B709307659E817FB8FE24F4CC63B82427688EB5A35F05DCCD8789CCA87E1A57350AE1030DB67D1BA88DBB49F6D2288BBBF1F52C118E121D02F60BA05B04BC234CB3F97318FF3512E10307AC5D49E043D3A986D1A895B380452F1AF76AD3F72B9A3F7E9ED41EB176D475BFAE793EB53836FD576096784BC64DEE51F185604FD62E850257F84F97F35C99AFF8F60C3BF516F93395F5B2724FE416093A6710BD28903733B211371087B0972DB20BF9270D4894EF97FCB984F336B35E320CD99033CDDEC
	7B1A48650264F77A31AB5FFEAF7D9F4471F033B7CD51125FB54DD7BE6ED3D7B48EBE1EB42DE265AA6EC1B8E6B5FBFAB81EB7B84B0B57DA58DE8762D53C433FFBEC47E57B71BA7916B036B75F273C4712855CF91F729E6B25B9429068126E4373B7FF1A4A460F467BDBB35AC182C0C2C1A2C0E26F47716318A9EC2F1B5678713043A13CFD093D0889460F9617F7A8A92671873AE7B13473664EEAEF5D8A137CDED7FD656B596EAB9A87EF2B5799F1FED7B7853192A34E6F2E1A0C4FA4EECA57FE76F1DB7A704FBEFE
	142E3E0F671EF45FFDBC77272B6F6309C8176E6361DC9F4F43334F0977E37FD37A856E4FB17C94313EE554877F945B48773EE479DB8B57ADA0CE10GE68F928E928312GB29EA495A4BB9C7AC210DA109E10C010F010F810C9A0398F40988C448CB278G2D27AFE7F18443F584C8BC48C4106C1F417C8824814489928C9281928FB291A47BC15003B400B8C102C1A2C062C1A602E4C700BE8263084C173EDB00717FEF569316B67617250D7DCD1004DF9B2E9DD720C417AD79BDDE621F7B002FBF4B7BC21DF0EBD5
	52B7A88927C5ACC7761B159BAB0FF0C9F8B19A7993AF162BD3C34F73FA76174A77B87CB7739A5FF9006FCC103E5BC06B1D89CC16C9FECB5F9BA17D066AEA95BF5617AC5289D7EDFF58BA49EF56A7613BA558BB9A58793EAFA57A599A6132F7D9ACB5E86FAB67FBAD3F435462597B674F50FB98205E1BC182C042C046C66059662F27FB6D1F6825672420CF81497DB94C75C01AFF0E7687E6F86D6FF2207DA06803C042C162C0A600703D242FBBF12DA96BA18C6391D68544E71FAF1FE56505FBD92BEC56EEBB5B9F
	E9403D28634F22FF7EC33897F5390B52851899A4GA48CE4AC45B1AE4B4BF3BA6DDDCD86FD8AC86E4310AE106607517E63D9DE7B09FD14AE50870104034401CCF8985335E200529509E12C196D8DA364A206118F7AD210A6908FC8885957E7FB6DAFCCC67BC85047020C8749FC846E6BC7B04E1F25E11C350FE0980EB95EB0EE1CCA4F20C16FE4FEA091A071E4FF4BDC2FFD5FB4341F887A74C741864488E2FE145E65B04FEB5FB29D6D83C0BF92A48AE49CC88A597FB147EBDFBD936DF39FEB4DD88BD28FE28389
	FA8C6D37AF705AFFB58B6D43C19F831284E208EA4D480FC27B7605DE7BF13351FEA7681BC0BCA0A1A011D1784C7D549C79D9749BF33D7E3F1C037E89E0BF9EA47397105FA0353F4078E297F96D6F1FC76F7E847D90C8A8C8B4C8A2597FF331577E667934E79A74590FC37F8352G62FC9C79ADDCA86737A84F6BBF34907D0341BE8CE4ACC8B2C87A93985F1E25DE7B4EC5E8DFG7AB210E610C11011E49F394CEB3FF7895AC701BE8EE482C8D6B414D5B476137DCD8683366397A53FB31B2E7CD86E4BAB9A07783E
	74A3B8167B5ED444E69D41315C07B43ECBB905E3267AE8DA4FE947B113133027CC344EC0F9F8EBA77B8EF994DC4701A401CC8249FC32B5A38F248C2499E470C9343FC7309F8957D1A063C0D2C0F21F82DB10E610C11011CF217DA79D4112F431F806CBBB0B0F7D4DFBD25D16F11BA4721562C90A6BC18D7FB2CACBC127145B256C1D467EDB6B7DCA2C73E01D16F2CF3232DF547A967AEC2ADFEF6F705667BFC9FE3BF7DC793A9DF4C56360E37F3FCF551EFF7CFB6A70730FA9996A730F69993E730F4FB354679FB9
	995279875F0F75C75AFF9D7594BF57968FFF131F626766453365DFD05B89B86E594AF91A5B7076ABE8995A2C85FCA7C8BB48881051CFE37B7AE929646C33F81EB8F51B84AB6158D7D592ECA3470E2C10E025623E4F7392EC13F806E82A845B4C3125DFCBB071FB4FC105922CCCBCB7EE16E0DBB91634D1F286DABC2FFDF02E308F3557E4356D36DAECE65A4B380DF568FC0FBBFBD37EC13BC5260FD9AA57B78DB8E47A832BF2647A9E17050740755F8AE3E32E5F6D621F94BEC711F01B0B2E79E4D3EDF23247BEDE
	9A224D1CF5AA619099EED35C60930E172EE8B3FF57A215F008B45925CDD9689316570A5B1C7FC6124FE2BDF8B30B8D9C8FDBBCAE2B45AD9873B33F7B16AB4F8C33FA9873B41E89AEA9D053A710FE51AAB57DF8524F592826CFA5FDCA291ABEBB064EC4EFD65397107E39B2B5FD99699FA8D7535713BE24C2CD5FCE7A8BBB54749E52F7481E9F8746F0FD846947D7296947125EDE2D269FC77AC9C6B57D84521F5F2D26B7BC03FAC32D1ABE0774575629698BC91F35CFCDDFC17ADB6A55748D244FBD2026B7137E3E
	03EA79BBC87A10C3EA7EC14F603E163F1C41394368E770197FD21B72193F7C7C77C11B72197F99197EAA1B72193FBBCD2AFF5826FC663F52A0BD2F7BF6AFF29AC71CE65B75B9D559151C564974EE3B12D3230C53BDF6FDCE318E64147DAC6D3DF668F3AAF4AAB9FDA8531FF0AAB91D11F18AF2EAF162ED5284276FFBF3BFBB4B568C24FB4C9B597F5E5778D6579CE8E2DB4A45F4553AE86EC8696AF76927EB0CCB19AE7959675F3914696A1525AB4B2D1F576BE91F7B8862546A334FDD4E69A60FFE79BF63D1F26A
	14697F6ED1F2123FA7E1C1BFF20AA2CE46FEFDCE7D7D7A65BF26DF4929CD1E0F7DDA1CF8D9178F783EA379710B48F3A2719CFFD11F67E2037E7DDCEFD072344A745F9A74736E471311D361AF1153AB1375B9CDCE534F3B0DE9CACE87E5FACB1A12933EE302679D7FC63DBC6F78F80D71ECA71E6FCC5567191321DF6FF6E7A8F99E176987B334F8F2CEF7CEC3CEA30953735374B9FDBCCD3FBC57E57A3405B2FDDB26BE272B27A327F86294B1DD49E929AC4CF726ABB92D171DD349192164743CCC3FFB06FE1BE11D
	091CB247A2279F4C544F2767E76AF37AF8261213C91E0FD9BE79A42B778D331053CE62746DACFDCE913374B93DB3CB49C9DE36EFFD2E6474240C5327331153A0F1DA1B2D4F69D436BE2760EC7D766159ECA527ED327A74419C6494C11C964E5567D4B7D74949A163F4E12E12D3252C8FFAE02EFEFD0A1F071C469327CF6769F3AA4E516754112364A4FFAFCEC80E12133C9DFDECBE2DF97F8AB93DBBDF1F5342857A1C6A9668F33A30C03FAD38EBA1F2EAA6CE718B75B92565AAB9BDA863D41A2B64F4C8D6F6DD39
	7AE5F75DA26494CC1C9EDB244F6903C57A1C16AC564F277D0B754B4E3984B9459227D1CB74B945AF51671416274F29B4CF2B6C78DA4A17748DAA43F3FC3DA5977E2EFD0E3FB7095B5C5B0EB646673C6BADAD228DDFEFF95D04B69E4083C1460044020CFF8E57DBD6EC733E83EE035887FFF2510B1508587535DEEC23087D5FE2AFD6AAE2E376F831CDA2766F927E9EBE613C9CDB34102CD70C704C736A9A79D78425F37EED3BA567F605574D48578D2EEE4E716A0DD67641ACB1GBE4F7AFD27F29E4672A77357B8
	8F7B15C5CD1FC77A70EEB5FDB169EF352A690D24BFFFC6CD5FCCFAD32FF29E4874B6527F4D2E269FC17AC1271ABE04741F1ED5530F79B51EC71E39145E1BG5824DF637C33F1A93D83A1967739DFA5581A61FA88A4A4165E3D70853D838130E432FFD640731EEF4DA88529FF9E43FEE89906EDFE9E6F3924E54AFB2ECF56364C4DD75EF3DF4B7446FC65BD7711ECBFFCFF3E721E5BEF100D1D16635912D062E9AD20F7959056300A5ED57082E2359B91B392361E3E15BDC458827AE6E0A4E11F14A1B60130E47AF6
	F4410B083DD801D88BE18F6D20F7953C086F1DD8D9489EC829FD4B44B268916E8B6EFBBBFD47A40E42DB368B3174B8446E29C62C02302FEB90739076735D08059336259631E4421E2CC3AC6FA5442A77A156C4587375088591F670GE2E389CBBAC86FB4A06C45C3E8D77AB2BE571824FA16D1CF447DB32AE7990777FBC975AC6370FE7BDD7E7915DE76599771ECDA3FFFFECF2B1EE59C5E6F5E497E797DE22AFFFE23277967775D697E7959E77A67F7E416FFFED3327D737B336AD946617DDED7BD4BB83CDF346A
	D946617DFE3250BF3F9B977967573764F27DF89FD857067D288D5A1C301731AD8A25EF02A53E0C7D628B02ED4EAB50C70398DF41FE7465F66CC7DBG8BF9856DE71A047D52F01D8C127A8A067D31095EBB920F7D681A8EE5BF7A1AC15A8F1E6CD076234BE47D688D9D4AFE74082C9FFD3AC3590F62FBA7446FA81B31BD2E0FC71E29964482887BE3B71D65A46C85ABE213887BB93DAFE86DEF909B590B6D3B1930B58E44C289FB5009D882E11BDC08E53DCA5FAEDE6A5F3950E5437A291F8BED9E56CFA9FC4F71BF
	04711771D565DE64CF853C61D565DE646FAE257A843A98CA6F773E706F5E387D327DB83F4D4B980F0257143CAB853C78B5A56FD89ACFD600EE74EB487B31E5FE36654B557C5293F81966A6E81DEB369728792D956DF7EA7A9DD9256657A45A77E87A6D5F2866B7A45A87EB7A157AFCAB1BC918E89F256937E40B1ADF1CE81F24691736D54DEF12E81F793A6639F41F6FE0B349936D0BB57D62AB547C0C22FD33265FE32AFB7EED227D88CD3FD13A7B68C3B47D8AF44F360E51743B3FDA4FAFDA532F38C64FAFDE53
	6F515DFAFE491AFE9535FAFEA91AFE4F566979E53D2165D73BCF4FAFDF536F65FABD3FD2CD3F2683FAFE351AFE6F9ED4FD0F08E86F54740BBF2497DF00265F41E13FD7225EDF358E6B275EDF7D78323F0F425B779D42F8E6649B4AF6FF0F00073D21EC77635B305D0FGDD4E1B586EA7347B576E3FF359FE1C5FC5E13CB5782612F72000873C2964BD47043C03C1B709F89BBA7C633D582C6657BC0E1729ED1CD63D185C2566B7C2348F51749B2F7AFE22B122FD34265FABD6B53FF851BED9536FD15539F80AE81F
	75DBAD3F9F6A4E4573B57D0AF46762251AFE11CE5D76C0532FDCF78E5F2269773130B7D8BC43780577778DCB2F7C7E46FB7F87F1D4AC732629C6727B65AF2EFC5C6FDF51B878DE62D14B697C79EFF1FF71E74B697CB9E15794205DE8425EABC03BF042EC2B50AE1E305FEFC03BC4424C9B514E70BB44FE35916D32885BC75F04AFA6ACFC935AD590D632856D5A893BF58B5A5988DB34956D0289BB3F956DC289B3ECC33B9842CC5B50AE0E3077AB50EEA2E1355B51AE153017F720DD7EDB742DE3FAB775DA429E21
	F7B6B79036ED97E29E421E216FC50DA4ECCF8DE251043D349B31A4429AEB69DCF9A23DF3208E31C2425A76A1D6CF583B7508B989BBF30076239316AA2CF1C641F5ACE1268634CBA06C7DC30825A4E29FE4EC775EE77BA53F5FE87B9F3CDF63FF9047B5A6EF1AEE147CDEE83A72F197DF51B844EFAD1A312E677DBFAC67DB4CD43F884B6DC23BFA424EF521DDB3E11FF5235D90E16D747D6FC0425E3322DDB8E1FBCE23DD94E1AF51BBC69289AB32215DF842A269FDA2D93F27F7D5503BC30A89FB5109D893E10768
	1DA0033F47F3B125ECBB1DF8AEE6418AFC4E998A7A8810F81009A059FFC07B5C95DE7BCF8A503E9874C690B3C8GC89859EFAB705A7FE3835A0F857DB81094105C24560C32A434BF3552EB1F38113E5F82FA9BC890C8B8C89C59872D725A7FF2935ACFG3D61EDE857C0F602343F0D76FFD96D353FFF8BFD478D74A1A011A089A01348FE6E9A2F7D4DDB51BE679D28B3A035A0BDA0016F207D262F3C76035B50FE8C6823C192C1D2C1727E08761DDFFB6D5B3623FD85689BC01CA041A091E4FF6DDA2F7DEEFAF7EB
	BC6813C15213418E44180C4F2E63687D2CE64086C1C602C4010CA31B4374BE7A9440325E05369F249E4476AE5A0C2377518781B69AA486A48944709E5A1C2A2133FF001502B401F8C0C248A6F9B71D7783AC96E4BCC8667B101F6F234D69DA3429852C85E488A4146949E6E29D5AA482B691A4FBBC54FF100671E8533F0F3E2587D8GC89848D810E432C92D27F771FFGF591248C2499E4708334397AG5A0C84AC9CA48EE482C8561F70797F3C8679F93648F512FA50C067B540FEA7C8BB48881051FF42B8BEBD
	04B6B100A500CC824979907271C3AC7BF70A3CE15EE7C67BA650770084020C8109A57B2945DE7BE07A7660F850270264CD00318AC84B8434AF5C60359F206F908E01BE98A482A49EE4A2596FAB715A1FBA0D76599F019E4488E2868978886D6D9B3D76B5B6348F83FD9448B81094105CBFE31E3CC167F24BG2B87310184010413CD8B1D450D83AC89447097386FC1F67E85ED5EF122CDBBE09E109010C81084326921333513G4B1C887591249624E7A22EF9D72C406784019371B94169957A7B82C3D778BCA710
	69DFDE21FCCEB0D3368746D020FCCE305DA0FDCE30E2A5F20AA1CE07D66AF37A4ECAA5272932B89FD92964F42BCC7F7ECAFDCECBCA10D38AF12AAB5167F421C449699BD91C8F14A8B97DC0267FC309BE2734D264D47CD764D4DC2A4F2923D449699F32B8C3CA151C3EA353BFD72A4F6903CD482907B8AD5C2C4F29EE33FE59DD582C64A45FD77C40E6A5272BE47B38623750F98962746996FDCE45E57AE557D1266474014CBF24CC1F53E3DB693C84F1FAF72BBE2705657AE5D7D72E64244847F2FDCE233651F909
	3FA127B81FEFB449B9FD6A730DA6F91C4595CACE97647531C249699959DE45EB3653D987627470F6FDCE6FEE57AF3B05BBFC6A383CBE6E5067E42B24338E44690ECAFDCEF1157AE577E9251253CD727A38D31F535F68BD20D144A93FCA49C93ED771F01512539E19BE4067BD203D32673D0F7A3C87D45E968C6E22F38D44695EDD7A1C466DD2F2DAA74B279955CACE72FC2C28566774E9B51DEB78BB6D25B66AF3BAE5D4F23AC73E175A284F69D923BE27009ABA43C01C9E2A516714DC236434D4762CFE41EEFDCE
	353B75B94D58C3E79808D3599EFDCE27776817DD689EA52777E57EAF6F51EF1F02EB698C83F1FA2AD61F5344DAFDCE4B6B74B9B555695FF78B76529906C9F446E82FBE277E3D7A75E94CDEFDCE3F5B2B1FCF217B688C83F1FAE11FBE2754FD7A1C0A6A75B935576B6753727DF40601B8B56C5767F475FE7DBA9E39DF1F53FB7B7573E94C81BA43C01C5EB8204FA97320BE2772037A1CEC877573E9D4831DE1A0CEF18D4374418DCACEB748744507141C3EAB9F4B9C52EF8B8A8E539906FFA02743077573A9603012
	53A3B27D2307151C646327710775B93DE226B38C44699333FEBE2D37A8B9AD1169CD96A527A932323D55226434D1466951AEBA43C01C123B74B9AD68D6F2BAA95357F6ABB92D16155D79EEFDCEBF24EF4145922797FD3E85A76774094F3760641C56DB151C64FA134F37606477DD40E964E478A775F72775B9A51F566734600CBE275AB3CACEDF4A6E3BB3B664D4C61CEE77B9D3AD6774224F19EAF91C1F781C29D664E32FFEDB10CAE72A1D44692BE14ED437781C29163785B77A1C297E2B2CBE45781C29165727
	2B69CC75986294B14C196AF79C7A1CF2FC4ED44B4F546E76B9D3AD2FCF1974DE31A46234792CBE271E337A1C6EB82B64A4BFC798F7D63FBE5DC8E734F37F051C2207B9237DE71FB35AF2CE79EE7DFCBA6CF3C69B6B93FFA6D135423FBD8705AB7D734BAF714FEFDA291F7B16D5773A8D6F773A6ADE37617D22D5773A8D6FF777B67F7C2E5F6E1F5F59CA7F7C7E6567BBE33F71737B8FD30C7E79DD273B57CD5BEF769E7F7C3E2F3B57CD5BEF49DE7F7CFE2C3B57CD5BEF75FE7F7C9E505D6B266DF7F703FFFEEB8E
	7B6717203A97EAF83FA7D477C28D6FF7171F5FCA3B6E34FFFECE1BFFFE136DFE6E057673DC52AC55EF2C8D6FF76BE51FE7E26F011AE748AB188B7DDA78C706299EC37AEE2B4D82FFEAADB6274770F53549EEF758731BAC1DBDF6074D51ED3538739BCC365EF5079B0F64732FB5666F6A335A4C961747B06538456DB1249DF0404FAA3359E348B0DAA0B088EEDA23295FD2D4D4843F6A9DA63370AB3DE6506A719826D6F5B87ABCEE17E12E6FC76F2BEE7C6D1B61ED461CB4C3DA1DA123C7F8BBF91DE16AGFBFD79
	2053A04CF15266FA8C45B5F676F236FCCF0FA53F4FEDF165FB9C79EE0B5D1C6F9EB0B9733B70D5D8791D7CB3586ECAC819160B8752106F6E311825562BD52C4D0E7CAAF3BFFBB99CB3E5B941AC8B54ADBBCCEEEB27440CE71574FFA15B3C7FADE71F744CF7F4652FE6D937DA880FE5BCD0F374594C90A60412DE5567815B0C239617356B9C646521BE2B8572B4CBA4D1F758D8D5A90BE01E28B10123C8C7EEB0C7F80DDA3EF848375062DE63B1ACD831A11F3FAD8C0A3C57E9321F4BE7A70B1922C4CB31D1DDB11D
	5ECB267DFB1A78CEB275DFDF9C686B6D00FCBD5815AF3E09AC9F7E9566EFD8372138B89F58AE2D31FBC0AF2600D12833F7B9DC3DA6F65C399CB227416568F619FAC5C65344035010FB4211E70FA1930FB673B1A10CAAAF160977CB4B4DA10614EBC68B6FCD192CF2BF65FDAC6E2F7F587BEDA97ECE7A0255EC7CEA83BC70E3FC873DF595FBG2C7A8E7A2A8ECFE32F49E67318ECE72A6C663DF0EB6CBA67E12F9F945F9D38E6B58B5F0DB622126B0AD7B35E27A5BE7F9FD0CB87880497CDF5FDBDGG605BGGD0
	CB818294G94G88G88GD1FDFDAD0497CDF5FDBDGG605BGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG37BDGGGG
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
			ivjCommandPanel.setBounds(111, 16, 486, 253);
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
			ivjEmailField.setBounds(30, 35, 566, 20);
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
			ivjJLabel11.setBounds(30, 15, 133, 14);
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
			ivjJLabel12.setBounds(30, 130, 201, 14);
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
			ivjJLabel13.setBounds(30, 190, 203, 14);
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
			ivjJLabel15.setBounds(320, 130, 117, 14);
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
			ivjJLabel16.setBounds(320, 190, 116, 14);
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
			ivjJLabel18.setBounds(480, 130, 61, 14);
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
			ivjJLabel19.setBounds(480, 190, 65, 14);
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
			ivjJLabel21.setBounds(18, 215, 296, 20);
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
 * Return the JLabel22 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel22() {
	if (ivjJLabel22 == null) {
		try {
			ivjJLabel22 = new javax.swing.JLabel();
			ivjJLabel22.setName("JLabel22");
			ivjJLabel22.setText("Number Of Retries    ( 1-100 )");
			ivjJLabel22.setBounds(30, 70, 195, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel22;
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
			ivjPagingCapCodeOneField.setBounds(480, 150, 115, 20);
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
			ivjPagingCapCodeTwoField.setBounds(480, 210, 115, 20);
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
			ivjPagingFrequencyOneField.setBounds(320, 150, 115, 20);
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
			ivjPagingFrequencyTwoField.setBounds(320, 210, 115, 20);
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
			ivjPagingNameOneField.setBounds(30, 150, 250, 20);
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
			ivjPagingNameTwoField.setBounds(30, 210, 250, 20);
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
			ivjPagingPanel.setBounds(38, 277, 630, 314);
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
			getPagingPanel().add(getJLabel22(), getJLabel22().getName());
			getPagingPanel().add(getRetriesField(), getRetriesField().getName());
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
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getRetriesField() {
	if (ivjRetriesField == null) {
		try {
			ivjRetriesField = new javax.swing.JTextField();
			ivjRetriesField.setName("RetriesField");
			ivjRetriesField.setBounds(30, 90, 116, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRetriesField;
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

	getRetriesField().setBackground( Color.white );
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
	javax.swing.JOptionPane about	 	= new javax.swing.JOptionPane();

	box.setResizable(false);
	box.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		
	about.showMessageDialog(box,   "Copyright (C) Cannon Technologies, Inc., 2001" + '\n'
		+"                         Version 2.12");

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
		Frame parent = SwingUtil.getParentFrame(this);
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
				javax.swing.JOptionPane.showMessageDialog(parent, "An error occurred opening file", "Error",	javax.swing.JOptionPane.ERROR_MESSAGE);
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
		Frame parent = SwingUtil.getParentFrame(this);
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
				javax.swing.JOptionPane.showMessageDialog(parent, "An error occurred saving prsu.cfg", "Error",	javax.swing.JOptionPane.ERROR_MESSAGE);
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

package com.cannontech.palm.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;
//=============================================================================================
//This application deciphers input from the user and builds a text-file based config file called prsu.cfg that
//the Palm-RSU uses when the Conduit reads the file and sends it to the palm during HotSync
//Creation date: (5/1/2001 1:25:35 PM)
// @author: Eric Schmit
// Copyright (C) Cannon Technologies, Inc., 2001
//=============================================================================================

public class DaPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private String tempFile = "c:\\temp\\";
	private javax.swing.JScrollPane ivjJScrollPane1 = null;
	private javax.swing.ButtonGroup commandGroup = null;
	private javax.swing.ButtonGroup swapGroup = null;
	private javax.swing.ButtonGroup advancedGroup = null;
	private javax.swing.ButtonGroup configGroup = null;
	private javax.swing.ButtonGroup saveGroup = null;
	private javax.swing.JRadioButton ivjSwap_No = null;
	private javax.swing.JRadioButton ivjSwap_Yes = null;
	private javax.swing.JRadioButton ivjQuietRadio = null;
	private javax.swing.JRadioButton ivjVerifyRadio = null;
	private javax.swing.JRadioButton ivjConfig_No = null;
	private javax.swing.JRadioButton ivjConfig_Yes = null;
	private javax.swing.JRadioButton ivjCommand_No = null;
	private javax.swing.JRadioButton ivjCommand_Yes = null;
	private javax.swing.JRadioButton ivjAdvanced_No = null;
	private javax.swing.JRadioButton ivjAdvanced_Yes = null;
	private javax.swing.JRadioButton ivjAutoRadio = null;
	private javax.swing.JLabel ivjJLabel2 = null;
	private javax.swing.JLabel ivjAdvanced_Label = null;
	private javax.swing.JLabel ivjJLabel10 = null;
	private javax.swing.JLabel ivjJLabel6 = null;
	private javax.swing.JLabel ivjJLabel7 = null;
	private javax.swing.JLabel ivjJLabel8 = null;
	private javax.swing.JLabel ivjJLabel9 = null;
	private javax.swing.JLabel ivjJLabel71 = null;
	private javax.swing.JLabel ivjJLabel81 = null;
	private javax.swing.JLabel ivjJLabel91 = null;
	private javax.swing.JLabel ivjJLabel92 = null;
	private javax.swing.JLabel ivjJLabel93 = null;
	private javax.swing.JLabel ivjJLabel931 = null;
	private javax.swing.JLabel ivjJLabel3 = null;
	private javax.swing.JLabel ivjJLabel4 = null;
	private javax.swing.JLabel ivjJLabel5 = null;
	private javax.swing.JTextField ivjEmailField = null;
	private javax.swing.JTextField ivjPagingCapOne = null;
	private javax.swing.JTextField ivjPagingFreqOne = null;
	private javax.swing.JTextField ivjPagingNameOne = null;
	private javax.swing.JTextField ivjPagingCapThree = null;
	private javax.swing.JTextField ivjPagingCapTwo = null;
	private javax.swing.JTextField ivjPagingFreqThree = null;
	private javax.swing.JTextField ivjPagingFreqTwo = null;
	private javax.swing.JTextField ivjPagingNameThree = null;
	private javax.swing.JTextField ivjPagingNameTwo = null;
	private javax.swing.JPanel ivjJPanelMain = null;
/**
 * DaPanel constructor comment.
 */
public DaPanel() {
	super();
	initialize();
}
/**
 * DaPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public DaPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * DaPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public DaPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * DaPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public DaPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 3:19:00 PM)
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e)
{
	// user code begin {1}
	// user code end
	if (e.getSource() == getConfig_Yes())
		connEtoC2();
	if (e.getSource() == getConfig_No())
		connEtoC3();
		
	// user code begin {2}
	// user code end
}
//=================================================================================================
//hide the advanced label and it's buttons and set the advanced No button to true
//=================================================================================================
public void config_No_ActionEvents()
{
	getAdvanced_Yes().setVisible(false);
	getAdvanced_No().doClick();
	getAdvanced_No().setVisible(false);
	getAdvanced_Label().setVisible(false);
	
	return;
}
/**
 * Comment
 */
public void config_Yes_ActionEvents() 
{
	getAdvanced_No().setVisible(true);
	getAdvanced_Yes().setVisible(true);
	getAdvanced_Label().setVisible(true);
	
	return;
}
/**
 * connEtoC2:  (Config_Yes.action. --> DaPanel.config_Yes_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
	try {
		// user code begin {1}
		// user code end
		this.config_Yes_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (Config_No.action. --> DaPanel.config_No_ActionEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3() {
	try {
		// user code begin {1}
		// user code end
		this.config_No_ActionEvents();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
//=================================================================================================
//here we recieve a vector with all the details of the settings from the prsu.cfg file that we loaded and we slap it back on the
//screen so the user doesn't have to re-type everything again
//=================================================================================================

public void displayLoadedFile(Vector configVector)
{
	Vector								radioVector;
	Vector								yea_nea;
	Vector								companyVector;
	int										index;
	int										count;
	
	companyVector 			= new Vector();
	yea_nea							= new Vector();
	radioVector					= new Vector();	

	//add the radio buttons to the vector
	radioVector.add(getAdvanced_Yes());
	radioVector.add(getCommand_Yes());
	radioVector.add(getConfig_Yes());
	radioVector.add(getSwap_Yes());

	//new-- add the text fields to the vector
	companyVector.addElement(getPagingNameOne());
	companyVector.addElement(getPagingFreqOne());
	companyVector.addElement(getPagingCapOne());
	
	companyVector.addElement(getPagingNameTwo());
	companyVector.addElement(getPagingFreqTwo());
	companyVector.addElement(getPagingCapTwo());
	
	companyVector.addElement(getPagingNameThree());
	companyVector.addElement(getPagingFreqThree());
	companyVector.addElement(getPagingCapThree());
	
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
			getVerifyRadio().doClick();
	if(configVector.elementAt(index).toString().equals("2"))
			getAutoRadio().doClick();
	if(configVector.elementAt(index).toString().equals("3"))
			getQuietRadio().doClick();

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
 * Return the JLabel6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAdvanced_Label() {
	if (ivjAdvanced_Label == null) {
		try {
			ivjAdvanced_Label = new javax.swing.JLabel();
			ivjAdvanced_Label.setName("Advanced_Label");
			ivjAdvanced_Label.setText("Enable the user to do Advanced configs?");
			ivjAdvanced_Label.setBounds(40, 137, 308, 14);
			ivjAdvanced_Label.setVisible(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvanced_Label;
}
/**
 * Return the Advanced_No property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAdvanced_No() {
	if (ivjAdvanced_No == null) {
		try {
			ivjAdvanced_No = new javax.swing.JRadioButton();
			ivjAdvanced_No.setName("Advanced_No");
			ivjAdvanced_No.setText("No");
			ivjAdvanced_No.setBounds(450, 134, 42, 22);
			ivjAdvanced_No.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvanced_No;
}
/**
 * Return the Advanced_Yes property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAdvanced_Yes() {
	if (ivjAdvanced_Yes == null) {
		try {
			ivjAdvanced_Yes = new javax.swing.JRadioButton();
			ivjAdvanced_Yes.setName("Advanced_Yes");
			ivjAdvanced_Yes.setText("Yes");
			ivjAdvanced_Yes.setBounds(400, 134, 40, 22);
			ivjAdvanced_Yes.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdvanced_Yes;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2001 11:54:00 AM)
 */
public javax.swing.ButtonGroup getAdvancedGroup() 
{
	if (advancedGroup == null)
	{
		advancedGroup = new javax.swing.ButtonGroup();
	}
	return advancedGroup;
}
/**
 * Return the AutoRadio property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAutoRadio() {
	if (ivjAutoRadio == null) {
		try {
			ivjAutoRadio = new javax.swing.JRadioButton();
			ivjAutoRadio.setName("AutoRadio");
			ivjAutoRadio.setText("Auto");
			ivjAutoRadio.setBounds(351, 159, 47, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAutoRadio;
}
/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCommand_No() {
	if (ivjCommand_No == null) {
		try {
			ivjCommand_No = new javax.swing.JRadioButton();
			ivjCommand_No.setName("Command_No");
			ivjCommand_No.setText("No");
			ivjCommand_No.setBounds(450, 59, 43, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommand_No;
}
/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCommand_Yes() {
	if (ivjCommand_Yes == null) {
		try {
			ivjCommand_Yes = new javax.swing.JRadioButton();
			ivjCommand_Yes.setName("Command_Yes");
			ivjCommand_Yes.setText("Yes");
			ivjCommand_Yes.setBounds(400, 59, 48, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommand_Yes;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2001 11:54:00 AM)
 */
public javax.swing.ButtonGroup getCommandGroup() 
{
	if (commandGroup == null)
	{
		commandGroup = new javax.swing.ButtonGroup();
	}
	return commandGroup;
}
/**
 * Return the Config_No property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getConfig_No() {
	if (ivjConfig_No == null) {
		try {
			ivjConfig_No = new javax.swing.JRadioButton();
			ivjConfig_No.setName("Config_No");
			ivjConfig_No.setText("No");
			ivjConfig_No.setBounds(450, 109, 42, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfig_No;
}
/**
 * Return the Config_Yes property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getConfig_Yes() {
	if (ivjConfig_Yes == null) {
		try {
			ivjConfig_Yes = new javax.swing.JRadioButton();
			ivjConfig_Yes.setName("Config_Yes");
			ivjConfig_Yes.setText("Yes");
			ivjConfig_Yes.setBounds(400, 109, 40, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfig_Yes;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2001 11:54:00 AM)
 */
public javax.swing.ButtonGroup getConfigGroup() 
{
	if (configGroup == null)
	{
		configGroup = new javax.swing.ButtonGroup();
	}
	return configGroup;
}
/**
 * Return the EmailField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getEmailField() {
	if (ivjEmailField == null) {
		try {
			ivjEmailField = new javax.swing.JTextField();
			ivjEmailField.setName("EmailField");
			ivjEmailField.setBounds(40, 204, 492, 20);
			// user code begin {1}
			ivjEmailField.setText("place@holder.com");
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
 * Return the JLabel10 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel10() {
	if (ivjJLabel10 == null) {
		try {
			ivjJLabel10 = new javax.swing.JLabel();
			ivjJLabel10.setName("JLabel10");
			ivjJLabel10.setText("CapCode");
			ivjJLabel10.setBounds(402, 250, 63, 14);
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
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel2() {
	if (ivjJLabel2 == null) {
		try {
			ivjJLabel2 = new javax.swing.JLabel();
			ivjJLabel2.setName("JLabel2");
			ivjJLabel2.setText("Enable the user to do basic configs?");
			ivjJLabel2.setBounds(40, 112, 284, 14);
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
			ivjJLabel3.setText("This application will build a config file for Cannon Technologies\' Palm-RSU");
			ivjJLabel3.setBounds(40, 16, 485, 20);
			ivjJLabel3.setForeground(java.awt.Color.black);
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
			ivjJLabel4.setText("Enable the user to send test shed commands?");
			ivjJLabel4.setBounds(40, 62, 265, 14);
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
			ivjJLabel5.setText("Enable the user to send swap frequency commands?");
			ivjJLabel5.setBounds(40, 87, 308, 14);
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
			ivjJLabel6.setText("Type of \'Save\' the Palm should do?");
			ivjJLabel6.setBounds(40, 162, 241, 14);
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
			ivjJLabel7.setText("Email Address(es)");
			ivjJLabel7.setBounds(40, 187, 150, 14);
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
 * Return the JLabel71 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel71() {
	if (ivjJLabel71 == null) {
		try {
			ivjJLabel71 = new javax.swing.JLabel();
			ivjJLabel71.setName("JLabel71");
			ivjJLabel71.setText("#2 Paging Company Name");
			ivjJLabel71.setBounds(40, 320, 157, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel71;
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
			ivjJLabel8.setText("#1 Paging Company Name");
			ivjJLabel8.setBounds(40, 250, 164, 14);
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
 * Return the JLabel81 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel81() {
	if (ivjJLabel81 == null) {
		try {
			ivjJLabel81 = new javax.swing.JLabel();
			ivjJLabel81.setName("JLabel81");
			ivjJLabel81.setText("Frequency");
			ivjJLabel81.setBounds(258, 320, 78, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel81;
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
			ivjJLabel9.setText("Frequency");
			ivjJLabel9.setBounds(258, 250, 82, 14);
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
 * Return the JLabel91 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel91() {
	if (ivjJLabel91 == null) {
		try {
			ivjJLabel91 = new javax.swing.JLabel();
			ivjJLabel91.setName("JLabel91");
			ivjJLabel91.setText("CapCode");
			ivjJLabel91.setBounds(402, 320, 67, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel91;
}
/**
 * Return the JLabel92 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel92() {
	if (ivjJLabel92 == null) {
		try {
			ivjJLabel92 = new javax.swing.JLabel();
			ivjJLabel92.setName("JLabel92");
			ivjJLabel92.setText("#3 Paging Company Name");
			ivjJLabel92.setBounds(40, 390, 160, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel92;
}
/**
 * Return the JLabel93 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel93() {
	if (ivjJLabel93 == null) {
		try {
			ivjJLabel93 = new javax.swing.JLabel();
			ivjJLabel93.setName("JLabel93");
			ivjJLabel93.setText("Frequency");
			ivjJLabel93.setBounds(258, 390, 81, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel93;
}
/**
 * Return the JLabel931 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel931() {
	if (ivjJLabel931 == null) {
		try {
			ivjJLabel931 = new javax.swing.JLabel();
			ivjJLabel931.setName("JLabel931");
			ivjJLabel931.setText("CapCode");
			ivjJLabel931.setBounds(402, 390, 64, 14);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel931;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelMain() {
	if (ivjJPanelMain == null) {
		try {
			ivjJPanelMain = new javax.swing.JPanel();
			ivjJPanelMain.setName("JPanelMain");
			ivjJPanelMain.setAutoscrolls(true);
			ivjJPanelMain.setPreferredSize(new java.awt.Dimension(500, 1000));
			ivjJPanelMain.setLayout(null);
			ivjJPanelMain.setBounds(0, 0, 646, 487);
			ivjJPanelMain.setMaximumSize(new java.awt.Dimension(500, 1000));
			getJPanelMain().add(getJLabel3(), getJLabel3().getName());
			getJPanelMain().add(getCommand_Yes(), getCommand_Yes().getName());
			getJPanelMain().add(getCommand_No(), getCommand_No().getName());
			getJPanelMain().add(getJLabel4(), getJLabel4().getName());
			getJPanelMain().add(getJLabel5(), getJLabel5().getName());
			getJPanelMain().add(getSwap_Yes(), getSwap_Yes().getName());
			getJPanelMain().add(getSwap_No(), getSwap_No().getName());
			getJPanelMain().add(getJLabel2(), getJLabel2().getName());
			getJPanelMain().add(getConfig_Yes(), getConfig_Yes().getName());
			getJPanelMain().add(getConfig_No(), getConfig_No().getName());
			getJPanelMain().add(getAdvanced_Label(), getAdvanced_Label().getName());
			getJPanelMain().add(getAdvanced_Yes(), getAdvanced_Yes().getName());
			getJPanelMain().add(getAdvanced_No(), getAdvanced_No().getName());
			getJPanelMain().add(getJLabel6(), getJLabel6().getName());
			getJPanelMain().add(getAutoRadio(), getAutoRadio().getName());
			getJPanelMain().add(getVerifyRadio(), getVerifyRadio().getName());
			getJPanelMain().add(getQuietRadio(), getQuietRadio().getName());
			getJPanelMain().add(getEmailField(), getEmailField().getName());
			getJPanelMain().add(getJLabel7(), getJLabel7().getName());
			getJPanelMain().add(getJLabel8(), getJLabel8().getName());
			getJPanelMain().add(getPagingNameOne(), getPagingNameOne().getName());
			getJPanelMain().add(getJLabel9(), getJLabel9().getName());
			getJPanelMain().add(getJLabel10(), getJLabel10().getName());
			getJPanelMain().add(getPagingFreqOne(), getPagingFreqOne().getName());
			getJPanelMain().add(getPagingCapOne(), getPagingCapOne().getName());
			getJPanelMain().add(getJLabel71(), getJLabel71().getName());
			getJPanelMain().add(getPagingNameTwo(), getPagingNameTwo().getName());
			getJPanelMain().add(getJLabel81(), getJLabel81().getName());
			getJPanelMain().add(getPagingFreqTwo(), getPagingFreqTwo().getName());
			getJPanelMain().add(getPagingCapTwo(), getPagingCapTwo().getName());
			getJPanelMain().add(getPagingFreqThree(), getPagingFreqThree().getName());
			getJPanelMain().add(getPagingCapThree(), getPagingCapThree().getName());
			getJPanelMain().add(getPagingNameThree(), getPagingNameThree().getName());
			getJPanelMain().add(getJLabel92(), getJLabel92().getName());
			getJPanelMain().add(getJLabel93(), getJLabel93().getName());
			getJPanelMain().add(getJLabel91(), getJLabel91().getName());
			getJPanelMain().add(getJLabel931(), getJLabel931().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelMain;
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
			ivjJScrollPane1.setAutoscrolls(false);
			ivjJScrollPane1.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_NEVER);
			ivjJScrollPane1.setMaximumSize(new java.awt.Dimension(562, 1111));
			ivjJScrollPane1.setVisible(true);
			ivjJScrollPane1.setPreferredSize(new java.awt.Dimension(518, 1018));
			ivjJScrollPane1.setBounds(51, 25, 585, 478);
			getJScrollPane1().setViewportView(getJPanelMain());
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
 * Return the PagingCapOne property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingCapOne() {
	if (ivjPagingCapOne == null) {
		try {
			ivjPagingCapOne = new javax.swing.JTextField();
			ivjPagingCapOne.setName("PagingCapOne");
			ivjPagingCapOne.setBounds(402, 270, 130, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingCapOne;
}
/**
 * Return the PagingCapThree property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingCapThree() {
	if (ivjPagingCapThree == null) {
		try {
			ivjPagingCapThree = new javax.swing.JTextField();
			ivjPagingCapThree.setName("PagingCapThree");
			ivjPagingCapThree.setBounds(402, 410, 130, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingCapThree;
}
/**
 * Return the PagingCapTwo property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingCapTwo() {
	if (ivjPagingCapTwo == null) {
		try {
			ivjPagingCapTwo = new javax.swing.JTextField();
			ivjPagingCapTwo.setName("PagingCapTwo");
			ivjPagingCapTwo.setBounds(402, 340, 130, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingCapTwo;
}
/**
 * Return the PagingFreqOne property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingFreqOne() {
	if (ivjPagingFreqOne == null) {
		try {
			ivjPagingFreqOne = new javax.swing.JTextField();
			ivjPagingFreqOne.setName("PagingFreqOne");
			ivjPagingFreqOne.setBounds(256, 270, 130, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingFreqOne;
}
/**
 * Return the PagingFreqThree property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingFreqThree() {
	if (ivjPagingFreqThree == null) {
		try {
			ivjPagingFreqThree = new javax.swing.JTextField();
			ivjPagingFreqThree.setName("PagingFreqThree");
			ivjPagingFreqThree.setBounds(256, 410, 130, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingFreqThree;
}
/**
 * Return the PagingFreqTwo property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingFreqTwo() {
	if (ivjPagingFreqTwo == null) {
		try {
			ivjPagingFreqTwo = new javax.swing.JTextField();
			ivjPagingFreqTwo.setName("PagingFreqTwo");
			ivjPagingFreqTwo.setBounds(256, 340, 130, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingFreqTwo;
}
/**
 * Return the PagingNameOne property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingNameOne() {
	if (ivjPagingNameOne == null) {
		try {
			ivjPagingNameOne = new javax.swing.JTextField();
			ivjPagingNameOne.setName("PagingNameOne");
			ivjPagingNameOne.setBounds(40, 270, 200, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingNameOne;
}
/**
 * Return the PagingNameThree property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingNameThree() {
	if (ivjPagingNameThree == null) {
		try {
			ivjPagingNameThree = new javax.swing.JTextField();
			ivjPagingNameThree.setName("PagingNameThree");
			ivjPagingNameThree.setBounds(40, 410, 198, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingNameThree;
}
/**
 * Return the PagingNameTwo property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagingNameTwo() {
	if (ivjPagingNameTwo == null) {
		try {
			ivjPagingNameTwo = new javax.swing.JTextField();
			ivjPagingNameTwo.setName("PagingNameTwo");
			ivjPagingNameTwo.setBounds(40, 340, 198, 20);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagingNameTwo;
}
/**
 * Return the QuietRadio property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getQuietRadio() {
	if (ivjQuietRadio == null) {
		try {
			ivjQuietRadio = new javax.swing.JRadioButton();
			ivjQuietRadio.setName("QuietRadio");
			ivjQuietRadio.setText("Quiet");
			ivjQuietRadio.setBounds(475, 159, 56, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjQuietRadio;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2001 11:54:00 AM)
 */
public javax.swing.ButtonGroup getSaveTypeGroup() 
{
	if (saveGroup == null)
	{
		saveGroup = new javax.swing.ButtonGroup();
	}
	return saveGroup;
}
/**
 * Return the JRadioButton4 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSwap_No() {
	if (ivjSwap_No == null) {
		try {
			ivjSwap_No = new javax.swing.JRadioButton();
			ivjSwap_No.setName("Swap_No");
			ivjSwap_No.setText("No");
			ivjSwap_No.setBounds(450, 84, 42, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSwap_No;
}
/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getSwap_Yes() {
	if (ivjSwap_Yes == null) {
		try {
			ivjSwap_Yes = new javax.swing.JRadioButton();
			ivjSwap_Yes.setName("Swap_Yes");
			ivjSwap_Yes.setText("Yes");
			ivjSwap_Yes.setBounds(400, 84, 48, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSwap_Yes;
}
/**
 * Insert the method's description here.
 * Creation date: (5/2/2001 11:54:00 AM)
 */
public javax.swing.ButtonGroup getSwapGroup() 
{
	if (swapGroup == null)
	{
		swapGroup = new javax.swing.ButtonGroup();
	}
	return swapGroup;
}
/**
 * Return the VerifyRadio property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getVerifyRadio() {
	if (ivjVerifyRadio == null) {
		try {
			ivjVerifyRadio = new javax.swing.JRadioButton();
			ivjVerifyRadio.setName("VerifyRadio");
			ivjVerifyRadio.setText("Verify");
			ivjVerifyRadio.setBounds(410, 159, 54, 22);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVerifyRadio;
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
	getConfig_Yes().addActionListener(this);
	getConfig_No().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ConfigPanel");
		setLayout(null);
		setSize(680, 538);
		add(getJScrollPane1(), getJScrollPane1().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//add the yes/no buttons to the group swap
	getSwapGroup().add(getSwap_Yes());
	getSwapGroup().add(getSwap_No());
	getSwap_No().doClick();

	//add the yes/no buttons to the group shed
	getCommandGroup().add(getCommand_Yes());
	getCommandGroup().add(getCommand_No());
	getCommand_No().doClick();
	
	//add the yes/no buttons to the group config
	getConfigGroup().add(getConfig_Yes());
	getConfigGroup().add(getConfig_No());
	getConfig_No().doClick();

	//add the yes/no buttons to the group config
	getAdvancedGroup().add(getAdvanced_Yes());
	getAdvancedGroup().add(getAdvanced_No());
	getAdvanced_No().doClick();

	getSaveTypeGroup().add(getAutoRadio());
	getSaveTypeGroup().add(getQuietRadio());
	getSaveTypeGroup().add(getVerifyRadio());
	getVerifyRadio().doClick();
	// user code end
}
//==============================================================================================
//here, we are trying to open a previously saved prsu.cfg file so we can parse the info from it 
//and fill out the fields in our interface
//==============================================================================================

public void loadFile()
{
	FileInputStream 	loadedConfig 	= null;
	Vector							configVector 	= null;
	
	try
	{
		loadedConfig = new FileInputStream(tempFile);
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

		String advancedString		= fileProps.getProperty("EnableSuper");
		configVector.addElement(advancedString);

		String commandsString		= fileProps.getProperty("EnableCommands");
		configVector.addElement(commandsString);

		String configString 				= fileProps.getProperty("EnableConfig");
		configVector.addElement(configString);
		
		String swapString 				= fileProps.getProperty("EnableSwap");
		configVector.addElement(swapString);
		
		String saveTypeString 		= fileProps.getProperty("SaveOptions");
		configVector.addElement(saveTypeString);
		
		String emailString	 				= fileProps.getProperty("EmailAddys");
		configVector.addElement(emailString);
		
		String nameOneString 		= fileProps.getProperty("PagingOneName");
		configVector.addElement(nameOneString);
		
		String freqOneString 			= fileProps.getProperty("PagingOneFreq");
		configVector.addElement(freqOneString);
		
		String capOneString 			= fileProps.getProperty("PagingOneCap");
		configVector.addElement(capOneString);
		
		String nameTwoString 		= fileProps.getProperty("PagingTwoName");
		configVector.addElement(nameTwoString);
		
		String freqTwoString 			= fileProps.getProperty("PagingTwoFreq");
		configVector.addElement(freqTwoString);
		
		String capTwoString 			= fileProps.getProperty("PagingTwoCap");
		configVector.addElement(capTwoString);
		
		String nameThreeString 	= fileProps.getProperty("PagingThreeName");
		configVector.addElement(nameThreeString);
		
		String freqThreeString 		= fileProps.getProperty("PagingThreeFreq");
		configVector.addElement(freqThreeString);
		
		String capThreeString 		= fileProps.getProperty("PagingThreeCap");
		configVector.addElement(capThreeString);

		displayLoadedFile(configVector);
	}
	catch (Exception e)
	{
		System.out.println("Couldn't Read The File");
	}
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
//		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

		javax.swing.JFrame frame = new javax.swing.JFrame();
		DaPanel aDaPanel;
		aDaPanel = new DaPanel();
		frame.setContentPane(aDaPanel);
		frame.setSize(aDaPanel.getSize());
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
//=======================================================================================
//this function will parse the controls and create the config file the conduit uses to config the palm
//we put the radio buttons into a vector (along with their respective strings) and then we can increment through
//and check their values, adding the 1 or 0 to the ends as we write them out
//then we parse out the text-fields and add those to the file as well
//=======================================================================================

public void saveFile()
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

    companyVector 	= new Vector();
    yea_nea 		= new Vector();
    radioVector 	= new Vector();
    outPutStrings 	= new Vector();
    outPutBuffer 	= new byte[1024];
    bigString 		= new String();
    one 			= new Integer(1);
    zero 			= new Integer(0);

    enderPosString 	= "1";
    enderNegString 	= "0";
    tackOnString 	= "\r\n";

    //add the radio buttons to the vector
    radioVector.add(getAdvanced_Yes());
    radioVector.add(getCommand_Yes());
    radioVector.add(getConfig_Yes());
    radioVector.add(getSwap_Yes());

    //new-- add the text fields to the vector
    companyVector.addElement(getPagingNameOne());
    companyVector.addElement(getPagingFreqOne());
    companyVector.addElement(getPagingCapOne());

    companyVector.addElement(getPagingNameTwo());
    companyVector.addElement(getPagingFreqTwo());
    companyVector.addElement(getPagingCapTwo());

    companyVector.addElement(getPagingNameThree());
    companyVector.addElement(getPagingFreqThree());
    companyVector.addElement(getPagingCapThree());

    //this is where the file is created
    try
    {
        configFile = new FileOutputStream(tempFile);
    } catch (IOException e)
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
            } else
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
        if (getVerifyRadio().isSelected())
            saveValue = 1;
        else if (getAutoRadio().isSelected())
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
        } else
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
        } catch (IOException e)
        {
            System.out.println("Bad Stuff Happened...");
        }
    }

    return;
}
/**
 * Insert the method's description here.
 * Creation date: (5/8/2001 2:44:42 PM)
 * @param newFilePath java.lang.String
 */
public void setFilePath(String newFilePath) 
{
	tempFile = newFilePath;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAEF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135FD8DD8945755BFC9B05204A426B189B1A445468F521A06A4B4A12DE98875CDEDE2DA5E3E26B10DEDF9735A1676218DFD6B5B3FC9C8BBA2A2AAA80A09441838C623280800280828FC8B0A087235828A83BB302B4B6E3ABB8828517F39F3675C5D191D99A2EDFC1E736C5E5FB9675E5F5CFB6E1DB3F76EA237EED3585245496BB8EE71D2CE7EF7E0B147E53BB86EC2EB479644820418D44E675FE610B5DC
	DBEE8AFCCEGDD745C1854CCEEDAC69A1427C0B9B4B6A6F5BDFC4F623E1B11F0813E6834B38BF45B5E7A59C7466D4CBA096DAC134A7BB35261F395C8B8E064F321C05AEF48481671B9B20E32B81963E677919BDB46C699AB06F2A3C8A0C8C89F350F004F65D24B63CA0AE53B4EFB4338052FECCF4EA0757854091259CFFD0B64CF93776462797D242DEFCABC090F9D4AD3C04845AF61B674ED034F52460B7DC7FA1D83F93D3C4361F408C22FB54F455B8772C441A31614D4710D3CC330F31C59CA6A7A155437CBC8
	7DD0DEA0E1DB3AC97D2170BD92E48E437FA461B9361854B290338D2F697076D21933796A13DC649B0EA52BF52E49E5235792E273DE5344417716FDAC75FF4AA9524FF3C13788A47DC2CCEABEC8A3088B6493E95CD7B66C044F729E3152B9B060F4143BDC95F60B7B2453A25861FBC989E8E46C0C506BF4DBB80E0EEDC8C62E869FA321FE53EE5A0F76C978DE9C6799E726F1731FB9113CCC23CF660767A787DD20FD91F6415BA7346D5F6A369DFDC199074A365BB85F36275C6EDC32D4236D4857F7A44F156B0977
	E9BB7BA2996B872531CE036F0B65782078E4896F7441E9CC6E10F0D23603AE76A24606F995F229CF1944E59654EB76C3C8CCF9F2111DF228372B391C67C95D43F65A66D24645A66141BEB86512A8F1A165B050258C442452784AB8CD6628954A01A021A011A0F3C192C1D29C98E34D8DFB3EE20CB572EE31E250E63788EE0E7AE61CA20B0EB0AC3A79BA07C7649D3DC2AB6F3671BDF6414349B69F1DA2FD5023707BC40A7DDEE8631860905C3CE8F3BA20F6FEC0908537075AECBD3506583885C95BEC9B00894D8F
	4012AA5711FBAA0B74BF6F91DBDC96DE943C96D4DFF40A0C49E53E95EA048A60BB6D1712B3243F36427517025433FEF8D22A6F046006752224245E693CB868EAA6D74205BAC8BF7FDDDABB3A617B30837BF8FF934162203C0855737BD3EBFD6A293275927A3CFB846758A35DE49DCEF7E24C7C450CB1D315B1154BFD7D38E64C90596A2431D26AD44F1D69D21D0497AD6F69B64BBE69CE6D39732F88ED23163531645FDC7FAAB6D6D31A49D8D9609A82C0421C58471B9BCDB4E6ABF80F00D1FA4A8D81A6D83CBDCE
	F9DAF8DC7317B26ECE890B027A621C58E7FFD7DC4F522F254FD673DFE79B34EF561FA677A3613260902B8417603040970F2C7B6CB409B9F93E76B9194FD689F7417C2E048965E1784E53E48E72226836758C0AC213A00AB6C73FD7DFA83565996CF1F91786EC6B60E9F2FD4EA1185F4C7E48E9B2B7FA1D8E07A08D92F31175E5525C946C42G102EF48EBAC40A571CA171FDD19811570663E7C802F319378F12F5066683917CCAF28DEE5BG8CBDD92AB8330B4C5B0CD559BEFD14869849EBF261334C05F3F0E88B
	4E412DD7A6F351DDDFA4EB5DC3C226D6A5073968FC0AF4F967A065F428C91BFB3934C64838E709048517902E15CEC71F2D1FA6A8B45EFF5DC26A188BF54703F017B05EDFE8A2FADCD364C02FF45A6D349BD97FFE27B9130C07F0E9D0002518ACDEA7D81FBD553CD625EB157B0F6A1FEFA61C6CBE6BDE70A5699E48E32E178D65FC1046CB348FAD17E89FD03B769E341B8765C49F3BCCB735236374379DE4BC243C6D82FF199F4E738CC1CC15149C17BA0266E9C32B52E47BE2B766E3975624CB639B139A84926646
	317B4F8C4C47A6DE8C63629E5B201BE3C649BC663B3DE3E73A8D73316DED244D0D9E088D8F76FBD10B6FBD215C63F1765A245B115CE77F5362BB36940BEFD9811FFDB637C72C0349B70C7D7B179642175C2FE4D83EFF3F42637D3B8C5A9D86D9A7927C2D3AF5BE5CA3G1F8F42010E46D0C18FD9D3F8A9G72643E1D1A32014EAF082B2E3302272BDCD2D713454013332395F326EFE71549754E795EA42E74C6D632D68E9B347A5364CE1176D520686D33EBCF27AEC9B87F7EB24A7F369E649F8BB6E90348BF6604
	967F32C14C5F8F719FFB39B6B87D294AEB6635D56A3C30F950FBCF338E7A5F5FE83E749BA9DE2E8D2AF33AF8898F9ED467F42EB5185305020E3B0C7778E74DA4DEADD08EG898109G0985C93C0CF1F239F5AFB7CA8ED5874FA6EE87EFEFD2AC16184B34AD216B1BDC4EEFDB0A6B9B6B07D8DE5D8F69C34AEB4FD115295FEF7C7C72D465FA159F9D1F0FAABE531E27E7F80B4DD9B1A80AE46121EB6C12F2759AEB01BA03C0C2C1681CDEFB9C67F464904E5DAFEBFC676E4C550549ABF566EE024CB1FD589B07396C
	D92A3B1D2CBD7520330284000402C4034C8761C6F01CFE50F6F834F112823059D989D8AD2C05156AFB956B57EF740CEDBC9E7473D30F07E9C469C77B6F5D93ECCD84BD4DC96F1356C9971483997666EA0205C3B9FA84F31B85EB487DF381AB7FC5AA13B122654BEB4C1CFF1F5F2CAC98514EB5E99B777CDB5A504BB569783EE8564FFDDEB6EB67BEF34C5A394FAF4CEBA88EF578BE79207ED7E6635C68ED33D6EE7429F48DBF7DE835D43747A3F824002B62C5DE567DC17C105348119A6061C83E7EACEED94FF259
	26DAF4D696F2B88FB72D751D07C9A374D9A5678ABC6BDC41F9B83902448719E74DD7705EDAF5D4DA9321BC85E466950C0B7547351E4967DE111F232F780FB71D0FFF68A0EBFF9268332FC24EGD28BE23D0A73F0786C286BE5753041FAF921DDFF1CAF35EB0F73D0BB498BBC4EC1F72F209E4B6B6D59CC671D71F522B0C03A1D5A0C177CC55E5DAF7845425D12BF556979BF582E97AFF4CC2395E33A174D1BFF1C509A03202B346FC32F7A0F812D6FBB2BC80EEC67C71C03A2472D3BC64679A5168BC7005FEC59B7
	69AA0D933A6EBFB5C4E2238F9E4601DE13CB686592A55F4455685B89E597488410881058EB1813AC3ACE6268F8D32F9BF2D3124F9550BA7B2BC9ECEB5CCB3C169C2EF3EDEBC9FC123EAE7E92633321CC5A2303F2A4481CAFB1BE27D5103ED9G65C5DF4A77076B5E7EB04177226B4A7E21B9C0FEB9292F76BA4D81CEB07C3C04477B60B4B7B8AE4D8FD28E3A811CEE4135B6CF3A271585C22E9B0858BD47F17D6D8C44F8FF27C3BFDE7F5E219D2FC99D5A6BD2F227713A3422D3AF4EA83F8F6B11F3G63FC399EB9
	8783C62F790F2BF05FB5823078C00C0F7E4F3F60FC76DDEDBD83F903178772789EB1CF305844121206C13B3DDE8EBB0E0E5F365524AE53381854E21046F1F87F9F5E06777F9953A6F17581A7B4F377386642E46BB879D9E31C777E9F8E77FF9AB3E2E61A6CB79B745C6D847BF626EF7EC962F3DF8A3D57D27DDC05FE0DACDEFD6A9ADFBD61CB9EE83D7AE2051E744DE6591FDE5B2BDF6035A53D9A4E593FFFD077392476F6FACDF6793331F58B37750E771751986FDCCB6E1BD3C097F53B7FDC1F83D81C863ED083
	CB9B2F17AB47C873EB63F8F52EBCC742F3472BF36566B24C15CBC197B69E636885695E129294133AB18831E515844B07F2BD43FE16AD6D25C1D9E4583A8D9803A18C3BE6A558CCA84F8DE26FAD4AA712B98A65A410F55F0067B510B210E690AB48B51089A061A051A073C19602245F9113EA82A986E98431028400CC8489871985B28FA481A46DCE3085A982E984310084000400C4024402A400F0414081A4A79879D9A4FEE5D06E8689G898309860983616E02FA6F224F516499398C3EF70384000401C4034401
	F0F7039DC899C8B7C8GC898C8B4C89C88F78F68C14AC03AC182C042C022C162C038892087A98369868918007C9A0FB6A69B67D8243F3ECAAEC67A0374A7169B6F70771B670311BEA51F4D9A75974BF633B46A701504BB3D7A22BB1536A1F77A7B46EAE0C45649B1C24635CC0EA192D3A4A6C8DC21DD3A8F9F13DCD723CE1DE39192BF61BA57FCED14F1A0319D69430544ED384A0744664D70D92872377A1483E41EE43E64286A25317507D1E36B632F4803BC27ED72A9EF5130596373FD2F7C1921E137D97E5CBD
	CA1B55BE5F8F28F4C7B56CABF56AA1FDC1760D8EF1B44FA6778332D6127B266F1E6F27BE5F77491F87EFE22CC613E3A03BF4F4EBC671A56F1D4BFD4A85A09F2AEC6AEE124FA72A72EA1F6F5B654F9D9C4D33FC6DE8EE59DECB1ED9C5C81442A610775B5D3DD014F353E947B1B7DDG3AC5A0597742BA8FE286217637D93C76F755237DB06803C1E6004C81C938975749F77C761DC6936A7741CD7B51FB6DA4A9C7CA7FA63D57AEE678E3921E6D0353FBF06EEA4C4F3602AE681B38DF55A710EBCB00F25AFD30BE03
	940318C1447BB09F8D6B4C67C6F97EEAF63A6AA1E1357B2418747AC6B247760E7825E5188FC57E605BDC546D6FAE565B6B893A0F2EAD6172E7FC739689275777EE9F393EF920DB8812BD916E25A0D5A01D93717A62BBF36975D5C3C6DAAE0AFC2FD55A21450BEB3589C3DE18BC6450FC633A746C6102FA02A6E28EF2C79641C2219C4D3037A5ECAE1493987643C602F177C3BEF0BFE2A92789168365AA06FDF294739C737D98EB334BC8ECDFE365DFC8E532DFCA4B6DE57F4A5E8929F77438757CCBED0C768E104E
	613995E44C79C15169119E7CBC5C147BC91FA4D460BB3A0977633364E715E4ADF73905BE416D96ACCD36AB6499115A9F10F24AF8783E10597FAE5B7FF9B7A99B677956873C4F3B258F503DE83A966C5A00B64D00DBC102C042097D83D877FDD642FB009F368D8C8EC8CCA80F6FCA79E9BC58ADE4362F14137EBCDE4F7788763D72F5BF1DC25F778C8C708E8B591817718D523B82449B1C58CFCD9FFB6BA812318985DEEC1F0CED135EADB58D71AED2A9453E14FCA58CAA24D8C6115737D0461E155B46F785587687
	1716FBF1C2C9469F4CA3FDD4EE39CC5E72DB3A243AD0D733A95DD747383C7961CADF1C71B9E0717259AFE3B185245DF2088DE90319357B7AD9D2C72B6036750DD08D45D776927E2787ED02A8BBD07C0D0D842F9E60ED769A1BE037A05E136AED73000C2D673DD8310CF5FFC1622E116F0727E632FBF34A615DF7934EFA6D8F4AD86DE7E98CAB482798E77256D16396AE7956B1DD5ACB203ACA5E85AA79FAAC5EFA8E94D06C3D94A51766A1EF3FD47858974B767F68D3366BEBDF6ED2348BAA0ACFB93ADA69E3F58B
	026CD3D637CA61A329E4DD6C5555CAEEBEFE778FF8399DAC147737767AE0FBA9F65C6E03495730C25A0FC5BB12F75077C94F177BBEEFEFFD104E615A8771FDD374C12D77CD1DCCBFF0C4CB6FE27A66B22D7A83197ED00516BE0469BF2F522AFF8653AF2F5152C7B3FD62B12D7A67B27D63A7357471CC5F5B22D5FF92535F5326254F8CC17DBFF6EA553F15697F182B65DF4C7431F9DA7AFA26FFAEDFCB5F4D740F967A6A45902A8FE57A2DC5DA7CA218FE65FEAD7DAC267F5F83DA754FE37A6103DA7E89CC5FF5C8
	CB4FBD047A1A43DA752FE37AB9C73474B9CCFFCEB37ECA193E36C24B3F1169FFD62965EFE17A6FD7EB798FB37D21E3DA7AE026FF29D6CB9F4674A7DB347411CC7FDF2DDA7A59CC5FEB5662BF1F699B5A357409CCDF592925CF19047A3C73DA7A6CC9389779738E5F332A1913B6A7E33E2D4EE5F2A751BC22F812F7AF126EC34EB90E7B1076C9388F795EF175BEA4F98E4DA3CDCA7ADC45FBF272DC7616C2FFDE211776A1957A898A3D348F39046AA927E76A11539446693F6B0DB9E51DB466D4FDD24D29C5219FBA
	2966B4CE66C4F34DB0F26B103FBF6E733DF0753F639D211E50B6CEFC2DED50678F27203F5FFFD9505E6F3FA6E86F775FAA983FE73CC350BF03F52F60FB860BBEBF85DA49584C07D8D8843271E1F8FE8229FA981F1DB675FD4EC94FCE9A4FCD5516FE72FE0C1C7494597B08FE7DEBDD5EBF7A7950357DA4DF6BF10A22F300540FF8F6BF99AF516912C00AFD5ACF7A48ED6B370A54D44637741398378BFD92AC67A0EB351FB51ED80B73217BE14C59A7E7117D0E4B02DB347572F67AD22A02F7B7BA6D365E9159EF26
	744CC776D3C46677EE1676F3411AF544B6826CE6B25BDFB4126B39EC73584859AAEA7747C6FC6EC9E0F61F1C56FF6EA9121EA71340B6AD941FDF8A0F6273CBB1E075A0F610E0109921D86749D65247740455C95E66409CEEC9253427629E94689A98B6643B86962FB820F98E0E481CD03A7645491FB347EF6168BECBB4792582667459E46F4DA18C1948EB3FBCFEC84C2B32C1C4118339B97B7273F3CD85797979E44F076EE9EC7D9877B4329F01FB5BA3787E0F1770A0A80701C4BD026F32CFEF40310CFD845736
	9B9B54EB5B4F17782ECD0B7A55EB9B71776AF7DA55EBDB06623DCF27D53D3621BF3D7E440B5E6B5FF6742BDE3FFCB62ECF67EC1C148D53EBCEACA757CCE2A96DD16C077FF7904F0614B1EC4A91440699F6E799E2918CF3D7A0964F3033D508193E05D8D98DE2E606ED2FC5EC824362CEA23F598C1B5402989706584BED0895B3EC5FAE2C4F45B053EE44429936EC8FE2F18C7BCBBEE2591371DD6597B99B357B339956A46969107A3EDE087330F1B25657D1047585B12CE2BFE2518C5BD10C3E0B9876A49B0F7CEF
	A37640A1442C8C7B72B07A06B1EC49D14466B1ECA29B374C47903B4176CCEA9936140DDBG43922A51AE0AE16EE308A5B2ECD1ADE2B9D3908BECC12C1BE1AB5A908BE158A4B3369B4B30F16D0825CDC56CCAA70BD3068967D91CCE45BD4CC76CA4D4685A7ECCBF59B7EF65AF483B14E4CB4F6946BB52F19856D3BD9704DE31196F67709E7F1F1A67D6C64F8D226AEF560FF28C366A5F836F330EFE8F8C1576118438F7B50F386473A2132DE4BF4B59576791583DF61AB513E1F51EC62747C64E195035E656DA5207
	FD3C5D635DEB782FAC324F6B937AF9BEA7AE72DA2B4FB457D51657F7B5D51EB9D5DFDFD1FE262B219AC03A0EFDB060DD471EECB4DA47769742BA867FF6B0FB6EB45ED7E8DDA627372E5506EBE2C1B1D9938B0AC9DDB4BE4CC7F10D0F180AEB7C6DC755EB7C8B0AB5BE6A28FA0DA76F073C7ABF9DD52F7177A94E86ECACD32F717D32BF0D4F571C523E9F70C91906F953C15BAE3DB88530516DF4C1FAB1C2D16964359CF3BBBF9EDB2C965F349FFD1F703AF4AF5DBACD7D1E61B7921EBFCD7D1E211F5D13CBC197B6
	8D7724DBB70C0D777F15EB7925CC27F9C4B6FC66CE57FA3718F4D04B2FCC36ED1E6E3F47ADBFDF9C5172334A7657F47DA614E979CD106D27687A8DD5E879C54976F3F47D4ED7E9794549768BF57D2AEB347C5242297D46F0BD3F5CDAAD3FFC593ECA576FF7A7357C4C323D9DBE834235466FDBADDAFE93E55BF0781C296977EA1B16DF2CEC3BGBE97E97A156C5272CBFFDCFEE6FFDC6F7A3E582D65D7A45B576A7A256F51726B146DDD3AFEFF4B57728B146DC3F47DFED5E8B47FE6687AF597E979C54B76F3F57D
	0E6D57720B176D13F47D720A0DF8E6FEC74FAFD2F33EEF156D0BF57D9EBE2465D7AF5BF76B7A5DFA580827286B17F65448AFC8576FA14D75A5D4360F50758B54DCDFD0E6697A2D50DCDF664976893AFEFF2DB6EA0F7B2E6EBAF84C48EF1D2E5F871A6B92CA0E2E5FB7B4579714D2DD3F551A6B8BCA232E5F374CDAFE9659FED8576F0EF6AD3FE059BECC576FC62716DF24ECBFDB576F42F9AD3F7932FD222E5FB943385692795C3C13FCE77BD5B45511F5EFBAC9DEE5F3C05A4A5BEDD7241FD2B50121231F367996
	C92F64769778FC9F78786B5F973C7DEBED033E7BCF2767A3E744242E1B016781389BF89E404430DD9C412A205C49302C9274F5B1CCACC15FEB8C0BBE02F6E18C5B4C4E9C4CE05874B2340BE5D8DAB95A4DE7585D0CB37784E26FD420DDBA43862B50AE1FE13F2CC23BD206F55620DDB743DE28C1BBBB43AAEB711C728406055722DDA84312247D06E8A847B20CEFE0574130DBCFC967B33E87FD7FBD441EEDC15F9C062DEFC55FA2069537212F19E1EF37212F1DE115E6749DE658741D689B4A305352BEC7841423
	3DDCF6216F9C06156EC63BC406BD329B6D129836E58F5A191EC4ACF08F5A65B2ECC5815AB5B26CE281BB2F4E303F96A296403021A2741D4230F9C5E8974130737B51EEAE43DE5C0FF6F18C2BAEC6ACA58231D78F22EF8E43F2D90C97B16C69C3E8E7E6D8D6A95AD9987650E14402983649BB979896A14D05D8A847B16CC3B697929876B00B7B4C27585C2AC25FA2065D4A623E0CE1E9AC46AD8C3B7B98634730F5F50C9F4342DA900BE55836B64496B26CD9A97E321F067EFB1A1DB7BA4B7A0FE113253D99B3142D
	8CCB6DC0BB11E18FC87BB5A1D08EE7587B1DE8974930DB24BD1CF9D00EE7586F4FB3FECF63BE74168A65993E9872DAC2BE13377E866EE3E7BF83B18BD28FE28789FE867DABB8693735CE37506FF68EBAAC38AF5EAE2D17B340AE6A99BC6F77BB0F376EBBCB306EF920CFGC90F04B6C06AA351BEED50EBDF4C76546D208FA0B6A051A0F14CFE57E52F7DA6767BC76E7B303E009401B403085FC77B06A12FFDEAA55ACF84FDB848EC10F81034E7517E5230577E4FD5E81F837AD210CE10EBA0214CFE5E0857FEEE8D
	5A4F84FDACC8A2C866F330B6BF07766FDF715A3FD80B760D2037008402CC8119759C76FDD603F26CBE3B66759FEEE067AF41FE91C8F6D4CCEAB108B98A7D9FBA657F7EA362946EA58EC7A12F72AF3D756635E23DC1208F851989B29FA48904564137617E504667318E4BF5EF9D6DE62CA39F74D5A09610C010A94FE39D31BB310ED94FA3DF6BAE65757E187356795AAE96CFE01FG127E8308A7107A9FA0075FAF765AC76EE671847AGE283928D92476C17AF715ABF3C0745538FA11EC00AC01AC1449F223D74E7
	23E47BDB8BD8BC01BE9CE4B6C8BCC85ACCFCCF1436174590E025A01DA057C0C2E7E21DD717FA6BFC2708459068E3C9BDA019AFC08C3D00760F26FA6DFF391F459068ADA001A0D3C0E63D009CBA0BD91CG3688A47BC790A7A0669FE11DAFAD7356D949666DB06803C1E6004C81C9E07609E9DE7B1D25E81F7EA25C8BC14AC03AC182DEC48E910751A68C30C81039A08BC156C5234DFEB67F0BG2B853102840104C7E3FC7C24CC999FD9AB3CBCF259FAB09B6C6703A401ECFC897A7DA5EC63698A76FBBA40DCA093
	C0A2C0E2DF42EB5B3F52DB67C3EC8DC884FDCACCCC6AD610AA90CB8C5639094D7BC040C2C022C06601ACE2B6D30F31F576479033A075A0F610609F33B311F5E8B38330E810B890EE967469AC34F92E85ED4AGEB8691C1A602C4B21B43EDE8B39730F810347F00FB93C86DFFE0BF3EE6D67623A55DFB4D05E7515F8A7657C0C2C1E6024CE77ED16DCA7FB999DE7FCFBB50BF896CB3DF065885E984F13D0C7ECFF4AA7D5FD96575DFFB8E7DA7007D9410D9A08BC0D2FE027EA167157EEBB33D7E9F1CE767CF413E98
	448CB28C927293FC7FF178863E7F087A896EEF8F5ED06FEF137D70BCD231241FFEC33D3F3DD921FF7306FAFF7BD5457E78CACE3D3F0DE7CA68B5C411AA657D7AE95239A836D7DF6934BB5DF9BDF63E77A2657FD4892E1D090C7FEFCB0C4F172CB92466BF2450AFBF22664FAB74070E981FF9B94F5E99644CC6CE41FE6F8C141CFE64774EC049A9516F1DC11BC27F395FBB03F18ACE55656C5C816364A9D7F37AD4D16754F2B527AF957AB74A551C3212957DD821B74E14D3EEA5F28AE11C5AAB0DB985D72AB9BD29
	6C47CAB5A71B02D3E225B1272CEA76CE1FF1AA2FB69EBBCF351A53634AFE2CB60E27B72A0D63693D9A641476D36414F34C18D37BB1E3CE4147541C14E728FEF44CB81EFED70B1C4A98272CBA63312B2ED3F37AEF05FE28CE4D6920E26C9E2FB39E3BC79B1053B063B427411853BF9A541C32967B6A73CF9947D34FA9BDCE72FD7A943E0B249C1BDB597BC4467156D663310CECD5F34CD0F07CFD2B1A23720CDEF61BDEFCD10E43ED3E6FEEA94FGB3720CE7BC3FE7B66699E7B666593253B866466F540BB94A738F
	BB7DF97EED9772B43D02BCB7679A0FF9D32E1AE735026738DCB54F7FD1681F4BB50E43DF6DC6CEE646A9AD4F386FCA72541C0EAA5A346729B9F5A97C9F4DD3F3FAB1591753CBFB10538446691DBD461CB6679BF3EA4AD7F39AD0780F4BD7F35A27D8C3428B1053EC46691785461C528A0DB915942AB9DDD2785B8B551C1ED3F04A5B0B1C38D711D367DE63FC686E3DEACE8FA9B8C56FD5F3FAD3118F7D6FDE63F87AF09FF2AAE61CAA7799477860BEB527A18527697B541CF6AB74EF6EB36614349FB93998271D87
	0CB9F59ED0F37ABFC51BF79FD0F32AD3766381E3CE3796A327F046A92258F86C5EAED6F37AD4313E2FBB2866F42F02D34DC1E3CEB6161F46B1CE8F7A65274AFE7AE909F1BF3DDF2266B4D5613F7B101A53D245DA50D80A1C3267A0276B25461CA2CA0DB93DDD2A66F4C3613F6E301A53970AF9774B43482911F1CAB9E2BCF67B0F980F1D55AF0FDF2A60BC49AF0FDF2C6827CE164787B1CEF7786571CACEB37D72F8A527F77D72F8A527DDFEF93C321F4E30BCBE1AF18A98A50FFF5AAF0F3FC541E901DF9EFFC841
	6923D17278F99548E99163146A1747AB7B2958AF0FFFDC416902DF9EFF1702D368A8F97C0BD548A97FB5647467AA63FE5A641747AB7B6934DF9E7F08427FB63FBCDEB96F2AD99EEFE51CBCB5467DB43546381F5E28B10E2765FEF93CB246DFBD061C429827F77D1EAD141CF6559AF3BAD7EB4C691EDA6398BFDD071C66B1CEB77C1EAD141C1E72FB36582738477E31591853A7AD467D746B96641479B3643452AFEFD7F2BA6C1737ABB9B97D72F6A527492D467D545B06FBB7350C5343ED461CDEEBB366744FB663
	F9D7E0B61EF7663358CF810C536DE70D67DD54D9B5278C8527BF1DD5F3AAD7F05A58EE4C2932036DF1B1CED7BB0CB9BD51E14C69379DEACE0FA97C57F49A2F854567583E956354F74E18D348B963756915F3EACE63957EC9670C63E9CB9776D34E4F11D3C717B1273B3A0C7B6945AEB527DB947EFF6D5263C44FC5D45C70BD87528D3C441FFF153FA770EC49584E07765E7439936AF7DC73DC6968FE8515E3734BB6BC97256F77CF4D731A237B7D4970DC143E5FE38DE373BB5BBAB63FF11A6722C677DB24F9DEF3
	F43F5FEA1E579C5D6FE54D731A237BBDD1B0B63F3DFB476637E15F587C969F981B5FF843731A7AFE0EB14E77165231793DE5F8DED35F0F9F637A52B6467565570667B5757DE6E91E579C5D6F78985717D843731A7AFE2DF5E373FB5B703C263E1FD5F1DEB3886EC521DF69FEF46E6C585A2B6D70750B0036E6FD25768E1F9BDBFBB9DD3EFE73202D04D15A2367ED4ECA6F91395818D4722ECFBE0BA5E159006DE0F6B75A500E6C2348E7455A50EE8F336B6AC03B4798E669C03BDD8CBB5A05F637B02C2A8B6DC82E
	C2FF0B47EFA3AFF665331FEDB60BE8257846618CAFDEAB105F76F2746F0FE5F1CBF7E627F05C0CBFF3CBC5EE49D941A3F2498DCE0F1BDB6B7F5321723B5FFCE3C6D7553AE4AE390ECB35CA7554F1CB07C8C343AEE8A89E123D051ECB5CF5AED9DDCAE9633864C226FB8FCA29504AF62D3FEF1A576C669D9EBBAF129FF051332B9C7D870FABCB5B392CBE72334286613260C6CB210EDBA321E7847AA706490F3C6A39758A2CE250E1318B648F29085C5AFAF2FDFB6C3C23FF8FED62D0BD1729B0BFA10C903AE975F2
	0308D3F3C9C52E9A7E39DCA2F72259EA7318F8174BEE6B158E5D1A06ECF63BC97ACFF9CC3C097E91FED31F4DAE187A1CEED32574FFE9191A05DE2B43E9F7765B844FF6D3A3EF9F58FD262945E7A4C4EED735037403C9348A26C10F60B609CE13C7F0D8CC643F5FB2F92C0285AA177E3A11672848656BD9FB06F817294FCD7FEF0A91DF971F3636EAF8DB1C269E5EE36B15AF01F8EC57B643BFC264E369D37767E4C8CC4EBE5376A67E3230DDF2A6978C17609C04BE32B841BE057CF1A211CB25FF0BC86416CAFFFC
	C86456C8FFEC88DA3038850FA7C7706C90390F369418685F46B1D5BA87DC3CE344C447F6F98DDE2648AD2B64DD648F4D937342514DBF5A3277E66A44EF90492B142F6F97CFDDFFDB66BDFB871339349AAE350F373B2CBC4C95697F9B2867D676C8BF30975CE0ABF219B49641F70F648B11185C8B5D7609976FA5B855B63978DE81CC16DBF877C52986920ADBBB3865FD6E7E9E298168710E1A8EE8D6AA917536FAEE85F4F8A5796F5B0621BF57FB2BEE64ED64BFD3119407E8C07FD30AE9720F7EE6FD478D5F5F2C
	AF9A172D7B1B751457655F183D6E7D3B9D7477FC8D527B22E640DD2F636F799E4DA56FBD74FEF75AB4405B6DA2EF3FD86E30105F20D60C080277EFB2B46DA675FB50C6D6D2DD7BEE427B020F4F7F87D0CB87880CED523A05A2GGC0F0GGD0CB818294G94G88G88GAEF954AC0CED523A05A2GGC0F0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3FA2GGGG
**end of data**/
}
}

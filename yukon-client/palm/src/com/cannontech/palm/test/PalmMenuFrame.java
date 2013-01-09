package com.cannontech.palm.test;

import java.awt.Frame;

import com.cannontech.common.util.SwingUtil;


//================================================================================================
// This is just a wrapper for daPanel (which is the Prsu config file builder) 
// Creation date: (5/7/2001 1:22:38 PM)
// @author: Eric Schmit
// Copyright (C) Cannon Technologies, Inc., 2001
//===============================================================================================

public class PalmMenuFrame extends javax.swing.JFrame implements java.awt.event.ActionListener, javax.swing.event.ChangeListener {
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JMenuBar ivjPalmMenuFrameJMenuBar = null;
	private javax.swing.JMenuItem ivjMenuItem_Exit = null;
	private javax.swing.JMenuItem ivjMenuItem_About = null;
	private javax.swing.JMenuItem ivjMenuItem_Load = null;
	private javax.swing.JMenuItem ivjMenuItem_Save = null;
	private static String thePath = null;
	private final String CONFIG_FILENAME = "prsu.cfg";
	private final String ADDRESS_FILENAME = "prsu.ady";
	private javax.swing.JMenu ivjFileMenu = null;
	private DaPanel daPanel = null;
	private AltAddressPanel addyPanel = null;
	private javax.swing.JMenu ivjHelpMenu = null;
	private	javax.swing.JTabbedPane theTabs = null;
	private javax.swing.JTabbedPane ivjJTabbedPane1 = null;
	private AltAddressPanel ivjAddressPanel = null;
	private DaPanel ivjConfigPanel = null;
/**
 * PalmMenuFrame constructor comment.
 */
public PalmMenuFrame() {
	super();
	initialize();
}
/**
 * PalmMenuFrame constructor comment.
 * @param title java.lang.String
 */
public PalmMenuFrame(String title) {
	super(title);
}
//===============================================================================================
//just pops up a little box indicating the company and copyright stuffs
//the dialog box sits on top of the frame...
//===============================================================================================

public void About_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	javax.swing.JFrame box 				= new javax.swing.JFrame("About");
	javax.swing.JOptionPane about	 	= new  javax.swing.JOptionPane( );

	box.setResizable(false);
	box.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
		
	about.showMessageDialog(box,   "Copyright (C) Cannon Technologies, Inc., 2001" + '\n'+"                         Version 1.0");
	return;
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e)
{
	// user code begin {1}
	// user code end
	if (e.getSource() == getMenuItem_Save())
		connEtoC1(e);
	if (e.getSource() == getMenuItem_Load())
		connEtoC2(e);
	if (e.getSource() == getMenuItem_Exit())
		connEtoC3(e);
	if (e.getSource() == getMenuItem_About())
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JMenuItem1.action.actionPerformed(java.awt.event.ActionEvent) --> PalmMenuFrame.jMenuItem1_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.Save_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JMenuItem2.action.actionPerformed(java.awt.event.ActionEvent) --> PalmMenuFrame.jMenuItem2_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.Load_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JMenuItem3.action.actionPerformed(java.awt.event.ActionEvent) --> PalmMenuFrame.jMenuItem3_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.Exit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JMenuItem4.action.actionPerformed(java.awt.event.ActionEvent) --> PalmMenuFrame.jMenuItem4_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.About_ActionPerformed(arg1);
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
 * Creation date: (6/13/2001 11:18:55 AM)
 */
public void doUpdate()
{
	this.invalidate();
	theTabs.invalidate();
	daPanel.invalidate();
	addyPanel.invalidate();
	this.validate();
	theTabs.validate();
	daPanel.validate();
	addyPanel.validate();

	this.repaint();
}
//===============================================================================================
//pretty basic idea, if user clicks on Exit, leave normally
//===============================================================================================

public void Exit_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	System.exit(0);
	return;
}
/**
 * Return the AltAddressPanel1 property value.
 * @return com.cannontech.palm.test.AltAddressPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private AltAddressPanel getAddressPanel() {
	if (ivjAddressPanel == null) {
		try {
			ivjAddressPanel = new com.cannontech.palm.test.AltAddressPanel();
			ivjAddressPanel.setName("AddressPanel");
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
 * Return the DaPanel1 property value.
 * @return com.cannontech.palm.test.DaPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private DaPanel getConfigPanel() {
	if (ivjConfigPanel == null) {
		try {
			ivjConfigPanel = new com.cannontech.palm.test.DaPanel();
			ivjConfigPanel.setName("ConfigPanel");
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
 * Return the JMenu1 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getFileMenu() {
	if (ivjFileMenu == null) {
		try {
			ivjFileMenu = new javax.swing.JMenu();
			ivjFileMenu.setName("FileMenu");
			ivjFileMenu.setMnemonic('f');
			ivjFileMenu.setText("File");
			ivjFileMenu.add(getMenuItem_Save());
			ivjFileMenu.add(getMenuItem_Load());
			ivjFileMenu.add(getMenuItem_Exit());
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
 * Return the JMenu2 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getHelpMenu() {
	if (ivjHelpMenu == null) {
		try {
			ivjHelpMenu = new javax.swing.JMenu();
			ivjHelpMenu.setName("HelpMenu");
			ivjHelpMenu.setMnemonic('l');
			ivjHelpMenu.setText("Help");
			ivjHelpMenu.add(getMenuItem_About());
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
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(null);
			ivjJFrameContentPane.setVisible(true);
			getJFrameContentPane().add(getJTabbedPane1(), getJTabbedPane1().getName());
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
 * Return the JTabbedPane1 property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane1() {
	if (ivjJTabbedPane1 == null) {
		try {
			ivjJTabbedPane1 = new javax.swing.JTabbedPane();
			ivjJTabbedPane1.setName("JTabbedPane1");
			ivjJTabbedPane1.setBounds(5, 0, 670, 557);
			ivjJTabbedPane1.insertTab("ConfigPanel", null, getConfigPanel(), null, 0);
			ivjJTabbedPane1.insertTab("AddressPanel", null, getAddressPanel(), null, 1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane1;
}
/**
 * Return the JMenuItem4 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMenuItem_About() {
	if (ivjMenuItem_About == null) {
		try {
			ivjMenuItem_About = new javax.swing.JMenuItem();
			ivjMenuItem_About.setName("MenuItem_About");
			ivjMenuItem_About.setMnemonic('b');
			ivjMenuItem_About.setText("About");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuItem_About;
}
/**
 * Return the JMenuItem3 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMenuItem_Exit() {
	if (ivjMenuItem_Exit == null) {
		try {
			ivjMenuItem_Exit = new javax.swing.JMenuItem();
			ivjMenuItem_Exit.setName("MenuItem_Exit");
			ivjMenuItem_Exit.setMnemonic('x');
			ivjMenuItem_Exit.setText("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuItem_Exit;
}
/**
 * Return the JMenuItem2 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMenuItem_Load() {
	if (ivjMenuItem_Load == null) {
		try {
			ivjMenuItem_Load = new javax.swing.JMenuItem();
			ivjMenuItem_Load.setName("MenuItem_Load");
			ivjMenuItem_Load.setMnemonic('o');
			ivjMenuItem_Load.setText("Load");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuItem_Load;
}
/**
 * Return the JMenuItem1 property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getMenuItem_Save() {
	if (ivjMenuItem_Save == null) {
		try {
			ivjMenuItem_Save = new javax.swing.JMenuItem();
			ivjMenuItem_Save.setName("MenuItem_Save");
			ivjMenuItem_Save.setMnemonic('v');
			ivjMenuItem_Save.setText("Save");
			// user code begin {1}
//			ivjJMenuItem1.addActionListener(this);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMenuItem_Save;
}
/**
 * Return the PalmMenuFrameJMenuBar property value.
 * @return javax.swing.JMenuBar
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuBar getPalmMenuFrameJMenuBar() {
	if (ivjPalmMenuFrameJMenuBar == null) {
		try {
			ivjPalmMenuFrameJMenuBar = new javax.swing.JMenuBar();
			ivjPalmMenuFrameJMenuBar.setName("PalmMenuFrameJMenuBar");
			ivjPalmMenuFrameJMenuBar.setVisible(true);
			ivjPalmMenuFrameJMenuBar.add(getFileMenu());
			ivjPalmMenuFrameJMenuBar.add(getHelpMenu());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPalmMenuFrameJMenuBar;
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
	getMenuItem_Save().addActionListener(this);
	getMenuItem_Load().addActionListener(this);
	getMenuItem_Exit().addActionListener(this);
	getMenuItem_About().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PalmMenuFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(673, 583);
		setJMenuBar(getPalmMenuFrameJMenuBar());
		setContentPane(getJFrameContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
/*		
		daPanel = new DaPanel();
		addyPanel = new AltAddressPanel();

//		theTabs = new javax.swing.JTabbedPane();
		setTitle("Cannon Technologies Palm-RSU Config Builder");
		setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("CTILogo.gif"));
//		theTabs.add("Configs",  daPanel);
//		theTabs.add("Addresses", addyPanel);
//		theTabs.addChangeListener(this);
//		setContentPane(theTabs);
*/	
		// user code end
}
//==============================================================================================
//this function calls up the CTI filechooser box so the user can look for a previously saved prsu.cfg file and load that
//into the interface for editing
//==============================================================================================

public void Load_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( actionEvent.getSource() == getMenuItem_Load() )
	{
		//This will need to be updated someday for a new version of swing
		Frame parent = SwingUtil.getParentFrame(this);
		javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

		if (fileChooser.showOpenDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
		{
			try
			{
				thePath = fileChooser.getSelectedFile().getPath();
				daPanel.setFilePath(thePath);
				daPanel.loadFile();
				System.out.println ("something");
			}
			catch (Exception exep)
			{
				javax.swing.JOptionPane.showMessageDialog(parent, "An error occurred saving to a file", "Error",	javax.swing.JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	return;
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
		PalmMenuFrame aPalmMenuFrame;
		aPalmMenuFrame = new PalmMenuFrame();
		
		aPalmMenuFrame.addWindowListener(new java.awt.event.WindowAdapter() 
		{
			public void windowClosing(java.awt.event.WindowEvent e) 
			{
				System.exit(0);
			};
		});
		aPalmMenuFrame.show();
		java.awt.Insets insets = aPalmMenuFrame.getInsets();
		aPalmMenuFrame.setSize(aPalmMenuFrame.getWidth() + insets.left + insets.right, aPalmMenuFrame.getHeight() + insets.top + insets.bottom);
		aPalmMenuFrame.pack();
		aPalmMenuFrame.setVisible(true);
	} 
	catch (Throwable exception) 
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		exception.printStackTrace(System.out);
	}
}
//==============================================================================================
//this action is called by clicking Save on the menubar and calls the saveFile method in the DaPanel class
//==============================================================================================

public void Save_ActionPerformed(java.awt.event.ActionEvent actionEvent)  
{
	if( actionEvent.getSource() == getMenuItem_Save() )
	{
		//This will need to be updated someday for a new version of swing
		Frame parent = SwingUtil.getParentFrame(this);
		javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();

		fileChooser.setSelectedFile( new java.io.File(CONFIG_FILENAME) );
	
		if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
		{
			try
			{
				thePath = fileChooser.getSelectedFile().getPath();
				this.doUpdate();
				daPanel.setFilePath(thePath);		
				daPanel.saveFile();			
			}
			catch (Exception exep)
			{
				javax.swing.JOptionPane.showMessageDialog(parent, "An error occurred saving prsu.cfg", "Error",	javax.swing.JOptionPane.ERROR_MESSAGE);
			}
		}

		fileChooser.setSelectedFile( new java.io.File(ADDRESS_FILENAME) );
	
		if (fileChooser.showSaveDialog(this) == javax.swing.JFileChooser.APPROVE_OPTION)
		{
			try
			{
				thePath = fileChooser.getSelectedFile().getPath();
				this.doUpdate();
				addyPanel.setFilePath(thePath);		
				addyPanel.saveFile();			
			}
			catch (Exception exep)
			{
				javax.swing.JOptionPane.showMessageDialog(parent, "An error occurred saving prsu.ady", "Error",	javax.swing.JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	return;
}
//===========================================================================================
// This should just cause all the panels and components to be redrawn to get rid of left-
// over parts
// Creation date: (6/12/2001 2:32:13 PM)
//===========================================================================================

public void stateChanged(javax.swing.event.ChangeEvent chE)
{
	this.doUpdate();
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GDAF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BD0D4D716287899A50806A8AAF1CC4619E1EA18445945DD56E1EA8C2B35A6A19BE74BD41C9AF7162A35F65C9A13F5B64EAE19186C8381B17103828A8E09A8FFE88982CE88418F728988EA93503009D9FD508F3A213BDF5B7D78E8C858F36EFD6775236986F5142AD36F5DF36E3D675CF34F6F5E57886947D7A445A7248BC2FC12207EFD94AF88659F8BC2CE498BED0473132213855D5F8900C34242
	F309708C825A66A72313D305A74AD2203D925A2150BE8A6FE94252326D9F410B8FBE6B007666F311BBA7671352C8FC7630760B07764173GC098607039D3C27E7FFAA8D3454728F80278844075E21FF78EE52B38EAE89B8166G0476727E91705C4BB84F2C29D67B5DD830C25879C2E142FE1CC7B7A7C1FA9F9FDB2DBEF304E763E37B10D76161FD6A98873443GF0713B05C57FEB00E72DF1206FDC0FECAB6B916DF6592EC8BD66B207E83515A912CB2929B1426BE549BE586494ED528DB34EA79FCEE16385A18F
	5A5B982E69B472D8816F11G9BB57C7E8A449BAC51498DGB7AC342E3305352A5CDDDFBFAB783FEA5FFD50473A948BDFCF28453D2E6041BF6E49E2FB505C043A5E8C34DDG297D5149D5G1DG8A401F585EDFEDAC01E7DD37D2AF5BEC323D4E6138E8B5B9AF4BA6498A6FB5B5C0D1F11FC9BD3253A488FCFFCF9DB6F811E7B26063BE396FF1DCA7E1153457611F7DC05872572D89FB3C68E4CBD8F9C2C0BF57454AFE37CEB8EF45A76FF57D63EDF1BC6F4F85BDEFE5163CBB498B6F08D70A93B62B73EC57714E9E
	403DDE43ECE09F3C472B7641716B987E168E4F6D320D61316DG5A2681320D2E83A4CBDB62D2A175F41BD7BD04C457A5D4DB398CC62B27AC53185D0DDAB94F3D1AAC018C9F2243F3D952989E5B91C04B34C5A7F37BEAEFC6BF9D05F68840AA008DG5BG926D504FCEB696527861BD5A18D1F4AA9787ADD613649478587626A37014C6942758E2F7A9223DC73AAEBAADE237D5F289EA1FAF1AD0875D63465DE4365F83BCBE156C12D3D4AC329DE687BFD5A4270B77B1B79D42BECE09D1BBADB6F0EA516690E8DEC7
	D39ABC2D22CB3966B0090A646E4169A3CD2813A171BA4C889340BB574B37AD282FEAD83F9160162607B758FC2D1253AED9EBEA5AE4F9E0505109AB91D659D14F73D86CD060BD40CEBA3EFB89F1BB213DCF26F9CABEBD2C1B27415203620B4EBB64E3EBCFE3AC4E1149E6DE6FA21BE9587FFD41704AA52FB603D0ABF3DB6914BDFD6785B6A74A455B1F1D6ED2F95C0FAF5FDF2C98E0BCF6BF02798FE8FB352D1579822CA384A0D2533174E98E37590B22CBA2AB3D6A848313CCEE0DF3B9E7D7242A7AC4D913D479B9
	AD1851B6423CF1GB90E6864E68769B02B224B4B38072B430A0AAE0F7977BEC49EDCD7DFB6E3FE1206A43B52A0B9A43B89DEDCAA2D3B9979297E5BABEBE34C8C6FGFF2F8787F3E9F8FBB37A2428A8CECB7720A2F5C80AE231773969C30C17EB305B618E95446B1BE6DC23BC8C7E2E751F5E0C3E52A35B6D925BB4ED08CA77E73EAAD9A59B88DDAF8F5A950E1F5B0276BEA05DD1E3C5E08B96BDC322F590638E2F91D6096F619A1C969B188206AEE12D837D78694319BA9DB9G0735CEB0BC439C641343396413F9
	F71F92565E4CCF7016D3C23E5F1010F21B7BD876ED37CFF21946AEF917FDA60B99AA5E45E51DFE9EEB23F1850B6683A7AE63BC35B03F9140FC1BFCA03E956994E7D4632F172DD62ECACD472F37220D3B245B039204E78CE82D1A5E7E50FA5803F6DD55A1273F5D0ABA346AE2E13593F5382102EAC0BF10A784A072B657E31C2A875EAF20127A79C3BB94A04A497B6DF26A7B992B28DF2D8BEAA0173B1F625277CBBCC37DA2G9F236B37DD6D47757966B15CE3D69F760BC362C819EB986C342666122E965C171622
	7A1CC1211AEFCEBE6A5A846DD100E0056C61177B29668B9ED8A144FE6F184FDAB6DC61F2ECD45C7610B303EA3E01AB48B3F590E24DA06D63C81BBE6F5439DCF20F0525BCF58F8AAF6B6D05638C175F05E72F4569D2DA4021C7E83FAAAE233C1893D534DAA37C1E5588281BAA60EB85C89CC2FCD7D63ACE7615000F815881301B4D57D5D9017E4E8C2A0CBF0C1233D7F65AA4D391EA36CC9CD64AD8ECA92BE364C6FCAF26FD30B7D74233C39C12EE56F90CEE73BD7C13626BEAED77F45A85D53618E7168A35E3E989
	5EEA6DB9875FCFE89B627AD6065CFA9F7D2B645D5B7F6F2DBDDC872BABC987EB21CF5C9069207718B79D148F138E322B7EB29D1C3F05BAE813C5537D6A004BFD270A64EEG19C607C96E5A9CEFF2C78C5399E9499872ED9C31A88F46775499623B816694C6082FB54F13EFB550EE0C905F1764AA81F333BCF83F0CF9CEBC140667G4708775A455F839F82EDDE9F6FE23E9BBE62D943A797A0BEEA4433068F4C279ABE86E865F728267B63D50C9D4150DE853081A096C0388BBEFB17FCF64D65CAE10A1A39854E23
	CE3BE86D98178C29F63D02FCA1FF29ED658A6A0E65AF01EA037F2E70BC8F555E9D3F76AE0FB69F17D469B96E830F762847B83EEF9B127E64BDE6E2CAEAD1A41B1ABF7D0E6A73E7B04C95893085E087G0F2D7333D254F9D33E26987ACD13BE06C69D2CC8F84FC78C3D70B5176F5657EEDFB6E86766156D1883D6GAD8AE093408E0074D158C3004ED15A23F72E1C1DEA0F1861F54A7500EB069CD76FD907E8BA7D59841D5E5BDEBCBF65DEA8236347F17DBDF90C7497B0CA7568D8550349705804F11EB22C73AA03
	AB07E4582249D0F96641E4281BB04ED30674EF460F633696FE521B2D99206F05EF48563E17C7F2F6810E1F550AD8EE8F02F604069BE3388D500E55F06F24A3AE71DBBEAF17B521GEB4B4CEF293D0F352B35F6F0FE17971B55830E1D0CBE99F09EBF191247DFF26E627E315B687BDC702E517B39A055687DDCF044F80863E18E7DAD8151BFB0CEFEEEA8B0FABBB73C4F56797C1103ECEE17CBF2B107ED9095D1257DD649903C1C9F2E707A19676F324A3DEA1FC6C52EBFADD03C5BFB946FD7FD44BBE14337E89F06
	8C323F9B50D6GC281D6FD4B6FG387DC59EA57BCB9D23BAB2B893F3CBB334EF810C0CE93613656987C1C0DBB9665BDEF84CAB6D401C9B897DB681EC87C8740BCE4E7323D897E51CB29FB50ECC120F1EE977ED8B2B5B3D5B424F5A3186F649034E9E49F33F235BB3B51ABB2AE292412D61FDDEE463955159A7CD3017FFE263B94D5778DF377B32A93E6F7FB9EE5FABB45F329F775C038650E3171F6FBD60737DCD15FE3ECAED3E5F1C701C4F8AF379CDB9DF7F997DFCD8B3F1FB5AFA126CA9580F6C69EFF3511EE2
	203D95E0179F59D3D81EA76F74E95149C5532662FDBE8D7D46A4750A03D6F0E559A5DDF52857F02877E948730BB41223F69A55F6BF9D461C5D8B07A8D0F507C36A915A26E35FC5E964A71150B786E0BB40BE209926D39D1D110173B16F3AA8BAB96E9F6C38077CD4D60FB77898BE6C683BBC7E1FAD98977F27F33F0B04E78CG2F63BAB3689B41D640E54FA0D927FFFC4C37FF0384246F508C7AA650B903747D7C911CB3G5AAB8122E6D08DB3D2C0B5CC788F168AEDFE2DDE4F01311D95891BE670BD081B612EE1
	422006617B7673231E7BD6659F1D5C6C6FFB5FF8AD1B1605F25D7067356C9E8DFF1C61959D1E6BE6A90BC3588EG5A8EFF4AFBBFE675D9674C68E473CC427D87430DC2BBE0A645154A8E5FF1212643FBDC385061BDC7B4F5CC1EA32EF47872E7AE5F4C63A4F308A6737347C966F08D77079354AFD243FDFC127A454CA45F7977131EFB3099E85BE7CE358725F928D3E11667B9638C43274E72BCE77CBC174E99B9C0731FC5B27DA20F645C2661F667131C3BE6115F5CE667A11D8917FDEE311B64E170973CF1D7DC
	E58DAD9D462B9D0DB72FDE39D95F86AF5CD6D78C6B3F3FD53F7B1157BBAC0414596A9D167A0C1A1D2B7AC6940B81B82693E8813391571C2EBF7F210C7321C0AAC39BE3743B63683827788D52CD8F9FC7C7D96B1374745F0C23234CBF0D67F43E2E97734987F73D98A6D8D7FF6C73AEE6133A1EB875E93C1EAB644DFDEB8F0F8FEF947B38A34288E4656B9FAC20BB2272B9949B1EA8427DB4C35B8FA0E48E595397998F764D4D7C00631E1AF45C2AB9936D185B695923A87F3AB95CCE13B4FCB343EF5661397D3EFC
	046E8136834D789DBAA7F7DDC37DC5C0FBA340B6GE1AE1C1181EA67D27C585AD1AECCD117F44A0EB688B0D6DDC8603E3069A87907F1AE79477B1908B3C1DB192B5D53335867876DD000C88D1F4F70F8B709770EF8274871ABB22936463E87543177227750FB6847F97C1D46033E995CEB3D1EF48FFDF8DC789D530F4DAE01E2ADBDBCE7EFE2B17CB5347D3994BF120F6179C501C03DF5AE7EB6E0EEB7FA916F7F8C0B1F7F82D0B48FEA8BC67FC90F1B7E810B1B3F872856689BCDC8677BA164529EB54F23BDEA48
	25BD3AB10FF4FEBA8FE5B0EB6DCFD8BBC65B133F4F75BC6FE05F7B71092977E44B849E8F735B955757B69653578378EB7ACA69C5FDF11FD95D0B74E05455BC7291C3E7A9BC2F0B7D2AF7B41AAC0A6CE46E81C823D3064ACC3943316CF6DEDD4F7174877BD67F5C9E0A1F91EB1E96A2E73D996F6B9EE6233A87BB5467364EDC0667EBB9EEC67F4F89GFF873885B08290G30B20056666AC4997174816257A90A58E3E63758647C57AD52309B0D85A5DF5B5D824F35C586386DA1865EE383465B870FE1FB0B5167F7
	86F86666B406CFFC4CB3B7AFAB225C1C8E3411474806A3D9CD38855ABB9EA39B2DE77E1698981D1C99C838AB8C97235E01F25CABC726BA3B5FAFA0CFFC3A793E7E50F910AFE07C61FBA7DE73DD7B192FF92F7D6C573C57FE4A35494F7F2357FCFFB71CF1CD7F5D107BD9824B83AF819882A94ED5E50E0FF36BAD02C031B22B84EB843A483C099FA4B45A650B63E8F8D12F51AE7EF99C8DAF53B55A6F3270D9B8D4287A554BDF6A6B2D5425A79262FD78D52D3A1F4D016E33811FF37DF1B627DC2872B8F31235C78A
	55F59733587AAB0051C00AAF27865C6BFCA17E00DEDEF689CF347F49E3F3F5972AFADD53FBC03FA7BC34283467FA31863358AD0AC534DA6E3203E187F43477F15FFFFBG6981A0C398C08CC0AC404EC0727B463E138273FBAFBE5FE86A1328F6A5FEC5E65F367621F96A5F18D41B71FBD8372CA8328D67A77CB9B37A0BA2BB9812636A4D38DFCECB1FD961DDD5FC0B99F5EB15FA991A4701852C7ECB1D9F1D1CB71FE2432B05F40F582C61EA0A58D8E87B4DA79FBDD80C76982C3533D8BBDCEBBFD5646943587761
	663CB593F8BC4C1C476D6A658C64B5E4F1D9707BB03753FF534E549B6713FF1E949057E431CAE81F9C974A6C39D932BA5C382CCCE6273D05EA1E4937895A9E6D6D4F97FC64CA302CCBD046DF6D6E17FA14CE31CF136FC2AE1B4FCAF61FE475EDEB7B2CD35BDA0615FD0F859B693C63D06F22722D787B83393757A5E9B6DDE2CD55F0ADAE236C322097F119428A303FD53C437C135FB57EC23B6B08851D452936E088025C82742EA06C73FAA175E9881AE8ABFC6E7622097BE1AE227DD891C47BD1ADE26E3B54A9F6
	F7CBA6BC731C66F7AE170A75F7AE2BD47EDB6039C39B7BF6860B354EC10967515F96509DCC4A0268648B8B688E266FA46E1776BBCDCB372DECF048D6A6F6ABE592ECDDCD4D15C12B35CDD503404FD7F905B857884C91849036006ED9530B68BEEA6D823A0F699F20DAA67B474BD89C75F5960CDB201EF1973AE32EDBE7E3C5349EAEF7E94A7B02573321DBDB825D9FCDAFA65D5ED8C8E744DF16609A02219D8E30EEA1797CEB936A1AFB7363376EFB9C2FFBC218AE37AC64F5CF3C06FF0A61376970FC9FCB8AE99F
	F7822D6BF1ADF6956979C781FE63631347AA6E3BC1F634252D50D78806988AD08BE084B087D32E78112D5217CF8372B3D9E6D702F8BBA8DA2086E02943892F0A2A1B59451369E6B45857B9383194579A946CF98EB6B27C4AE04FF3702BA5F48E0E84DA7693241B65050236CFBF543D1797BE6C9AED22DD34BCD29EBC3715B33F73037562BD8D471DE1389040C909946F1658FD4755957629636A0F6C5EF378337609B9BC426EBD072F31CF4C614B0BA9DFC7E8FB56DA0A384D505E21612EB3DC4A2268E443A2427D
	F38969177ABE7CBD7CAF0D877D8E70E1726071CCE471D92D67BE371894B347E70F6CF7631BA554A734791D7EEAAD8F98C19FA6GFF0015G6B96D19ED8DE0DE7C7F049DECB9F3BB6D363E95BF164D7E7B2B9A5170B91F8EE19D32A4FADDBE19E2C07B297034E81F81F44D37ABE8D0BF99FBFF886AF26DC706BE23A178CDFCC7712FBCBBC6FA53F9CF7AF79C909673DA40EF753EDA51E7712E7475DCBFE374473DE72BDF5BC5765315E82E14A7FC9E890391A545C313614F247564514BB5A8D283B6C106864AA0036
	904A2515CDE5D34FDFE7D574DAA7BE3527080FA90478BCDE0EFCC2219D819093C2B69FBBE9BC75ED476BCBA79B37B544D79CDE4F569B976219237E11619317F866284F8D1423D20126ACA1396BCECD46FF7392DF7C77305F72EFDF6249BF156163A670DFD2CE7C9317C29EDBCAE700AD2594E7E216EA7FE703A2297A5924FB7F4A7068F34350A36541575CF50A563CC3DB737E536EF59E513DFF377C512FB9721172A8DA06EBFEC30BE1694BA65A946FA3EBB14C61258F7749AC8355337E4B281E5D70157E5BC6CE
	E92257EF9BF1DFFD1890254E9B3B4CDD4FEE6A5F35074F7DE5B94D1D9ECA7B7201C16F9FED003F95FAAFB5DC3E03FD0B073EC1G91GB1G31GF121D443DD120B845FB59CAF705D059B1767D639DE1EFDCFC6A767BCB939BCEA6DE120FAE4F8B761EA8CD40F4C0827DA204C613B9EB963183A9EB96770DE0FB4B8A656A3AD8E6F7548D54744FAE4381C64BF2155D32365A47FCB30A67EFFFF0DE7717FF242DF931294A191EF2C600117D37040FBA8C5C8E257BB5044B3B5BC70982D5EC5950F696F2262E6E67A7C
	76173D1C6B3CFC395B0E783EFF67BC139D70F31653BE7F6A5CA9DF7B8C676F8E1BE835AA22F5204EEE42337845BB0A643E638BBC0F733B280FCA6434A74E235C7D3AB17F8FD0CB8788FA5CAC3B2393GGD8B9GGD0CB818294G94G88G88GDAF954ACFA5CAC3B2393GGD8B9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5D93GGGG
**end of data**/
}
}

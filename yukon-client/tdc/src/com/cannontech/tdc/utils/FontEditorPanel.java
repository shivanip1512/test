package com.cannontech.tdc.utils;

/**
 * Insert the type's description here.
 * Creation date: (2/7/00 11:13:37 AM)
 * @author: 
 */

import java.awt.Font;

public class FontEditorPanel extends javax.swing.JPanel {
	private Font selectedFont = null;
	private java.awt.GraphicsEnvironment graphEnv = null;
	private javax.swing.JComboBox ivjJComboBoxName = null;
	private javax.swing.JComboBox ivjJComboBoxSize = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JLabel ivjJLabelSize = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	protected transient com.cannontech.tdc.utils.FontEditorPanelListener fieldFontEditorPanelListenerEventMulticaster = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == FontEditorPanel.this.getJComboBoxName()) 
				connEtoC3(e);
			if (e.getSource() == FontEditorPanel.this.getJComboBoxSize()) 
				connEtoC5(e);
			if (e.getSource() == FontEditorPanel.this.getJButtonCancel()) 
				connEtoC1(e);
			if (e.getSource() == FontEditorPanel.this.getJButtonOk()) 
				connEtoC2(e);
		};
	};
/**
 * FontEditorPanel constructor comment.
 */
public FontEditorPanel() {
	super();
	initialize();
}
/**
 * FontEditorPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public FontEditorPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * FontEditorPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public FontEditorPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * FontEditorPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public FontEditorPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * 
 * @param newListener com.cannontech.tdc.utils.FontEditorPanelListener
 */
public void addFontEditorPanelListener(com.cannontech.tdc.utils.FontEditorPanelListener newListener) {
	fieldFontEditorPanelListenerEventMulticaster = com.cannontech.tdc.utils.FontEditorPanelListenerEventMulticaster.add(fieldFontEditorPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * connEtoC1:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> FontEditorPanel.fireJButtonCancelAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonCancelAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> FontEditorPanel.fireJButtonOkAction_actionPerformed(Ljava.util.EventObject;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireJButtonOkAction_actionPerformed(new java.util.EventObject(this));
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JComboBoxName.action.actionPerformed(java.awt.event.ActionEvent) --> FontEditorPanel.fontChanged()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fontChanged();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JComboBoxSize.action.actionPerformed(java.awt.event.ActionEvent) --> FontEditorPanel.fontChanged()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fontChanged();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonCancelAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldFontEditorPanelListenerEventMulticaster == null) {
		return;
	};
	fieldFontEditorPanelListenerEventMulticaster.JButtonCancelAction_actionPerformed(newEvent);
}
/**
 * Method to support listener events.
 * @param newEvent java.util.EventObject
 */
protected void fireJButtonOkAction_actionPerformed(java.util.EventObject newEvent) {
	if (fieldFontEditorPanelListenerEventMulticaster == null) {
		return;
	};
	fieldFontEditorPanelListenerEventMulticaster.JButtonOkAction_actionPerformed(newEvent);
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/00 11:50:28 AM)
 */
private void fontChanged() 
{
	selectedFont = new Font( getJComboBoxName().getSelectedItem().toString(),
									Font.PLAIN,
									new Integer( getJComboBoxSize().getSelectedItem().toString() ).intValue() );
	
	getJLabel1().setFont( selectedFont );
	
	getJLabel1().invalidate();	
	getJLabel1().repaint();
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('C');
			ivjJButtonCancel.setText("Cancel");
			ivjJButtonCancel.setMaximumSize(new java.awt.Dimension(100, 27));
			ivjJButtonCancel.setPreferredSize(new java.awt.Dimension(100, 27));
			ivjJButtonCancel.setMinimumSize(new java.awt.Dimension(100, 27));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}
/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('O');
			ivjJButtonOk.setText("Ok");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JComboBoxName property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxName() {
	if (ivjJComboBoxName == null) {
		try {
			ivjJComboBoxName = new javax.swing.JComboBox();
			ivjJComboBoxName.setName("JComboBoxName");
			ivjJComboBoxName.setBackground(java.awt.Color.white);
			ivjJComboBoxName.setMaximumRowCount(7);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxName;
}
/**
 * Return the JComboBoxSize property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxSize() {
	if (ivjJComboBoxSize == null) {
		try {
			ivjJComboBoxSize = new javax.swing.JComboBox();
			ivjJComboBoxSize.setName("JComboBoxSize");
			ivjJComboBoxSize.setBackground(java.awt.Color.white);
			// user code begin {1}

			ivjJComboBoxSize.addItem("10");
			ivjJComboBoxSize.addItem("12");
			ivjJComboBoxSize.addItem("14");
			ivjJComboBoxSize.addItem("18");
			ivjJComboBoxSize.addItem("21");
			ivjJComboBoxSize.addItem("23");
			ivjJComboBoxSize.addItem("26");
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxSize;
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
			ivjJLabel1.setText("This is some sample text.");
			ivjJLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
			ivjJLabel1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
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
 * Return the JLabelName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setText("Name");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}
/**
 * Return the JLabelSize property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSize() {
	if (ivjJLabelSize == null) {
		try {
			ivjJLabelSize = new javax.swing.JLabel();
			ivjJLabelSize.setName("JLabelSize");
			ivjJLabelSize.setText("Size");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSize;
}
/**
 * Insert the method's description here.
 * Creation date: (2/7/00 1:09:17 PM)
 * @return java.awt.Font
 */
public Font getSelectedFont() {
	return selectedFont;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxName().addActionListener(ivjEventHandler);
	getJComboBoxSize().addActionListener(ivjEventHandler);
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJButtonOk().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("FontEditorPanel");
		setPreferredSize(new java.awt.Dimension(417, 150));
		setLayout(new java.awt.GridBagLayout());
		setSize(406, 189);
		setMinimumSize(new java.awt.Dimension(413, 150));
		setMaximumSize(new java.awt.Dimension(417, 150));

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJLabelName.ipadx = 12;
		constraintsJLabelName.insets = new java.awt.Insets(14, 12, 25, 74);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJComboBoxName = new java.awt.GridBagConstraints();
		constraintsJComboBoxName.gridx = 1; constraintsJComboBoxName.gridy = 1;
		constraintsJComboBoxName.gridwidth = 2;
		constraintsJComboBoxName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxName.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJComboBoxName.weightx = 1.0;
		constraintsJComboBoxName.insets = new java.awt.Insets(12, 52, 25, 16);
		add(getJComboBoxName(), constraintsJComboBoxName);

		java.awt.GridBagConstraints constraintsJLabelSize = new java.awt.GridBagConstraints();
		constraintsJLabelSize.gridx = 3; constraintsJLabelSize.gridy = 1;
		constraintsJLabelSize.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJLabelSize.ipadx = 8;
		constraintsJLabelSize.insets = new java.awt.Insets(14, 16, 25, 12);
		add(getJLabelSize(), constraintsJLabelSize);

		java.awt.GridBagConstraints constraintsJComboBoxSize = new java.awt.GridBagConstraints();
		constraintsJComboBoxSize.gridx = 3; constraintsJComboBoxSize.gridy = 1;
		constraintsJComboBoxSize.gridwidth = 2;
		constraintsJComboBoxSize.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxSize.anchor = java.awt.GridBagConstraints.NORTHEAST;
		constraintsJComboBoxSize.weightx = 1.0;
		constraintsJComboBoxSize.insets = new java.awt.Insets(12, 45, 25, 15);
		add(getJComboBoxSize(), constraintsJComboBoxSize);

		java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
		constraintsJLabel1.gridx = 1; constraintsJLabel1.gridy = 2;
		constraintsJLabel1.gridwidth = 4;
		constraintsJLabel1.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJLabel1.insets = new java.awt.Insets(26, 6, 17, 15);
		add(getJLabel1(), constraintsJLabel1);

		java.awt.GridBagConstraints constraintsJButtonOk = new java.awt.GridBagConstraints();
		constraintsJButtonOk.gridx = 2; constraintsJButtonOk.gridy = 3;
		constraintsJButtonOk.gridwidth = 2;
		constraintsJButtonOk.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonOk.ipadx = 34;
		constraintsJButtonOk.insets = new java.awt.Insets(17, 74, 18, 8);
		add(getJButtonOk(), constraintsJButtonOk);

		java.awt.GridBagConstraints constraintsJButtonCancel = new java.awt.GridBagConstraints();
		constraintsJButtonCancel.gridx = 4; constraintsJButtonCancel.gridy = 3;
		constraintsJButtonCancel.anchor = java.awt.GridBagConstraints.SOUTHEAST;
		constraintsJButtonCancel.insets = new java.awt.Insets(17, 0, 18, 15);
		add(getJButtonCancel(), constraintsJButtonCancel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Initialize the names.
 */

public void initializeFontNames() 
{

	if( graphEnv == null )
		graphEnv = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
			
	initNameComboBox();

}
/**
 * Insert the method's description here.
 * Creation date: (2/7/00 11:22:48 AM)
 */
private void initNameComboBox() 
{
	int fontCount = graphEnv.getAvailableFontFamilyNames().length;
	
	// Visual Age takes forever to return all fonts
	if( System.getProperty("java.vm.name").equalsIgnoreCase("IBM VisualAge VM") )
		fontCount = 5;
			
	for( int i = 0; i < fontCount; i++ )
		getJComboBoxName().addItem( graphEnv.getAvailableFontFamilyNames()[i] );
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		FontEditorPanel aFontEditorPanel;
		aFontEditorPanel = new FontEditorPanel();
		frame.setContentPane(aFontEditorPanel);
		frame.setSize(aFontEditorPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * 
 * @param newListener com.cannontech.tdc.utils.FontEditorPanelListener
 */
public void removeFontEditorPanelListener(com.cannontech.tdc.utils.FontEditorPanelListener newListener) {
	fieldFontEditorPanelListenerEventMulticaster = com.cannontech.tdc.utils.FontEditorPanelListenerEventMulticaster.remove(fieldFontEditorPanelListenerEventMulticaster, newListener);
	return;
}
/**
 * Insert the method's description here.
 * Creation date: (4/7/00 11:02:52 AM)
 * Version: <version>
 * @param newFont java.awt.Font
 */
public void setSelectedFont(Font newFont) 
{
	getJComboBoxName().setSelectedItem( newFont.getFontName() );
	getJComboBoxSize().setSelectedItem( String.valueOf( newFont.getSize() ) );
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GD6F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8DD4D46715A4411FA8C9C80ABD1C041EA509C9CC4BE94C161C1056B606581E34711CE47718A669F13B6E2E6D3ADD5BF89A3647A6F47B4DA0FF919514A6EA4C890D241144A2482F28D58494042D84C60391028F66410C3C19B73EF983430FE16F7DFE5E3C995EA03ADD0E57773E7B5DFB3F7B5D6FFE773B771BC70A5FCF4E36590B8931E5937ED7E7A3E42A0A10130BDA1B85AEC6CE4FA1263FBF8294
	11DF1F5D824FF868ABCDCC4FA9A42F9E4D0376D2E8A7AEC94FF9875EF7111F9E5DF088DE220C33927A5EF864CC4B6C63447608F1F2E97B494282F8EE87D886B8FCE648B87E8B053BB9FEB5478B305989F9F188E9FED1380F636A215D89908710BC4468536019CFC71E57D04F69FA6FCEA699BF7A483E8D6518E48A58BD4CF80F71E789F94C36FE98475AD5D440F9F4E827G60643348667CF2246F9C997E4B206AA99F143CDE552B4B032EF25DB9D89E505D0A3F2161B420DA1CEEDD55BAA52F2C90F25905B2AF95
	65D199049C0076C60A7BEEB50E33945ED7G2CB17031B5082FF027673482DCF60B399D7F6898573DE772B1127A32B7EBC71439855DECCE496E505C9282EF646E216BF0FB975AFBAD7411AB69B945G75GBDGC100F7697ABF5F73B1BC9B8774A65563D13D0DBE5FA945297D0F6A1495F8EFE8009E0E6B1287D54DC988DB63BDC587AD74198D985FC7B74C47EC322CCA2CF7CA5743E46D375A6D3996B6D93B2C429EFF0559E2591510CD585817230E7D5C15F0FF8C9F7BB3E29EFB69FCB5AB5BE26C54174A6CEB39
	1CCD2631CBC6F02DAB69DA97423B0D7B87439F25F827894FFC73D96AB35856216F1591619BBD5B05AE9DDB6EA505159D16F6C8CCEF349FD3188E8EA5D2174DF44C98050D19EF6812DD0B78A4931E69320852E3BB8D7A76F952F318FF5D63403D9A836DA400D40055G9B81723CC06795BE766A27076668E31D12261F8A3895272C9146FBCF575B7014033AA635F97D3A649D14AFC81ADB9AD0E4BF61B477F5218D86427812296F8F4298E7E42F2CC93ADB7502F449A36B3266E7B40FF695A10DA653DE075BA343G
	9E9F91F29F6F5A85CFC5726B67FDCEC917C3942C7F29AE344928F481A402GF8E7F6F97ABC5A6B984C5F8160B46CD05D0D725AE58D62C5C3C3072A0E84FC8E1C89C975221D47E86C8842FB3CD75878BB979037997BD5A1E724FB27C9CE33FB90551734F131476AEAB09E172A42E7FE55A3FC26F95BC36460CBE7ADFD8661244AFC25C70D5CBB0DD4A66A455ABF2C6E3160AFD52D77510D829B63DFAD47487A9B485FEE2CD5D98F5D8B20F792408A43468FF717B01FBDA579E5612567B4F0B0599932B853733FEB8A
	61194D77489E2EBF6B5BDA0BFDEBC0EEA6C029AFBD2743A7EC98DFE31EDFB667491F035EF3376123E1E3E4F17959F3601D4B984CD6C98EBC1F64D1592BB74BBE596B0497BF6FFB500163717DBB249ABC0FD23C8F76FB93ECB03F01FF4C01FBD252F54DBD905065EED9575D5E61D07F13F4ACFFE040978A95E22C6FB9F0DE6A986CF7037ED9876E15C1556B15692299AC3C7F7BF42F4A0A6C81251B5400D7676762F9747791F91C470A974EE362B3AAA9810CBBACCFD8AEED45B9E8EE8F388206AE324A077B784E22
	5DA69B690043FCA7911EA9BE31A747760BBDD9BAF1BFD9597F215D6ACCC9FC2859DEF0156F452B21BD49F4DAF941DA779F50184171FE26EB73A85AADB2E9E1FBE060A24AB989729DG4AD53187CEFD067DA24EF067EFD2950519523071219E74F13FFCB5A0C3F8460056EE582D36E7E7C45F85EEC3567F179E3421E20A053BB5346109DA11874602BEC9GAB2EB2BBE6F2BBB03A2CBAC167047614960D6EC903AE895A29GEBB4C6D76C07988D40686CC784DD90F0717E905DF2F84F740B9C7175B2DCE31AA3DE11
	C625E039FF8C7C342161ACB5AC4B85B753F80E73AB54C54E77538934F5AB34AF8384F5618F7F30CD64FC89A349E45D03FB23663371BA1BD72AEEBA57EF93B9DF4A67B8E68674ED8908F5FC2A47FC6EB47A7D6A201B9EF9FC8DDC974D7E42F01E0BEF41F3482D7975B65850C131DE7E0B28AF1E099C4DF384974D9D50B63BE15CCE00D58144472945A65D97865859369C1ED9ECDF559E467DCE9D2A1CBDBAE5EDC855BC3273GDA36DC9A534BE9ECA9EF245DAD78DEA676E079A7A8F7887C3A49A5F90701296C0248
	5B9E58F5120F3B7A1B7712E3533B6CD6F974429D6F598BC619AD2BC7C3B61D7A7B1C2C0DDF3C194B66F730CE4CAF86E852C6457CFEDCEDB53FCD504F78565509BCEE4BD8FA4E31B141D79F46270FB13E24B1E117B3757FB73B3CD6564856CFBEFBAA206B2A3789D3A7051176C78A6C20923132A927E24E8DDC01BDFE94CD496B1A7975222ED9BEA666764991E1138D00AB890A39ED9F351ADBCFD06C1DAB6D4743F5BBB7F253FABDF3C468258445397ABBA34FDF9A64F5E3B014172C0677F5G99A69C4BFF87A6F1
	BEF963AC7F5DE160B50A2FB061597C57533D0C6D9E68DBB3AEF236233D989BBAA7A016824482A4812CGD8BBA176641B9721041FBDA7EE031AD373CACAF758E1A7F2534FF14EF0BE7176931F23ED6979C444596F2D0D2CF7B6CF0467E7451361ED46B7B5032FBC22DD9F414756F5D8F957BAA6429C8754D3EA102F5933EA9E9F2BF3D2443FEB274D71EF450E43762DD1625FC2BE76324950DEBDE8543CBF6B43783D817A36CCC18D82D08F508B9084C81A9276BFF16978756CCFBD4E21B6812E954E2726489C4230
	57613A1B33734999FC11F6DEB1954E476C372DA60F5BFC4D1470791F4F10958956BAE4DC172F601A55DA6F685EE33D56ECC79B3A2A5528AB1EE52557042E0B6BF14C055766DEABB03E4AA3B7E76793B378A26D1CF44D4A4EAD2342CFD38D5DFFC0E5ED3C815DD9EDD73C93E511AF41A781986E74B862AA205DEC6076CFA24E856D988357BCC56F6D203D4240FDE382F1AFC2FB4317C2B757A47AFEA6340B2753F3AAGDA811CGC10078E9C19724F4A953AC2FDABD4D72260D53AC5F415C08517DE242E7B19F14B5
	433778B8713C2F5502A692AAACE8B20D3964CFDA0D3D4D82077E0831E287EFDF2FFE92FE910943F84AE2C3F3EF74FA263D573A1E7934573A1E792C370861C10679F6C37477774EDE6F8C77DA55BB6FD1FDFF78768EAA5B6F177DB4D8B5CB3A447B7EDD7F833128FB7EAAF9E4BE57DD64215AFCCE53222BCDD546FEF9CE0D96938AE2601C87280E89DFBB96FB15FEBC77DB214F89B08510G109AA3E26E6FFA2FFB663584E7B973DE6B0B3EAE3F6933DE174CBE74993F9A508665C85BE7FD315B688BC577B6DD7620
	1998CD4197482FCB5A30BCE36DF6D1FE56970DFF5F975156172D013B4E3C86B5469A6C2C093686EB401E9BE2E66E9DA66F3DFA333CCAC31EBE63AEA593E4945E4264145EE23DDF6BAD709D003B9C01E7B169811AA36A5097FB0B48E3DDC74C7A9CB674B9BB9AB93F8460D946652E3098170D71CFB45FFB917A5799E3FC329357482749C3328695EA37FB82FD7C402DB4F652383B89E8B38D7A3EF12C6595E9DC8D68043CC2697EF1DC546C8A34E381D2GD65E4AF2D7F666BDBE06F1E6C813BC30045DBEF910245E
	063C8D130237845A55G8EG9DA061B6B16625A96485C637A760219AB27D4A2E21FE19C01797ABE853A7A82D940C207D890D7955C0F752207D609456E9E7BB2481D9E11B1B61938E23CD0C4CABEC5F771D8B71B0E3B1BC298C6361F6E4FD278BF38D1ECA0EFB2D90575648618D5AEFEE5F9642333A835A2CB6F8EC52DC9BA8314C3E713122F6D832572AFF2951AFD7D97527450AF3711FE9EE399A5A6B8D5CF79B45193A5940556FC5DC6BBC08D573C4FC527B23471749FE6B78F2CB3FF55CDF50BFFB5C3F2BBFDA
	DCE07A155508F98467891DBF9D977308B3F0DF257955DAE8EFB2F0BF2138AD73A18F18AFF0C7EBC43ED0E2603E9390B9C93501BBDEAB46E8B5F04F0F0A7CA3F13E703B276A71FE8CC2DF65AB7369EFFB341DE270C823C22FFD8B846E6B23C22F8A8337F5CC0CF7EC01101DF1A4DC76DB8DC2F65C827C7DB1E608983EF6F43C841EDEF92C44A80A1B5D9E590B9772872AAB6B8E16D456D414319AA3F94C7C1BE17DDBF516F7EE88AFAEE071E7BDFF2ED8301F57FCEF0C0B5F8C77AD04FD3ED044064A4B18D39979B0
	AA5250F0C6F3BBCFC9439DB452301C72945D43489F37D05C8979E89D398A5AEB81B2960A1AA878A7D19325BCFCAF6908E9373C93DA67B8ECAF381D69D9F1FB28A6DA86B5919B73722498D3017E253753BB4A30FD08361C34313C18752F8C6BC7DFADA3667EDF0775233EFF4EB27717CE197B33CD7CECEE4FFFA86616797CB222BCF1A46AFD57C6BE2742C56C59F9E1BFA9DD74E6AE7305E4D7489712A7E67505AA668B0C6FD9F7086F195979388F71FB3F21A877FEB4B2B2FB7FFCD254E72D20B343557689DC0C01
	CB2A4A6376C8DBA4FC7FB7AA466681E9F0E4D8D383DE27B847367B68B986F419G1D0B913726315942DE66B54DA222DE83FF851E8A008FB92EF9B13B4F3099F6B83797639FBFF73A54B1FA254C47F751F8097D094047F0DDB426AC03F66AE22113EBFCB61DF2A3F4DA43F55A43F19BE6689424B1436315A1E1F48F32F11F2671ED81C046E2E137BDD398A3F48850ECFDDF3F026B4B681DD44FADF1709EA7F4DFCFE35FB1E83746895D9D3533691EEF529F21026B7E9E57D70F8B694E4648203A47GBEAECE687933
	7A774D3E96F935F5C0F7397DE5FC0F9C174D712AE252E64F0A32C75262585EF8AEAE942F188EF7D19D7E8EE0F31C081F9771B8A6EC4E9F0C4DB6E7711B14D82F48B9675D111E53425F194EAFDCB26BDCF86F9F6D36A8BA975F41E3559D2118456A08F68F6A6CG3C6F8EF1B6FFFF6893F8DE102E702A017D0AC14B86C0F6EA2ACF5674F106253FB070BC13CC5C5CF78A5BEF126F4C8D7331BBC6F61798FFC74807987E17941FE44233F3E8F4C21CC34B202F60CEF177B86545786403F69C40D20095GEB81B65FA9
	EC39FD3802DC278EF3283E8EC80494D36A4266A7CF5E5C779B4F640AF81F7A6D87C85A7C5F5A225D6F955C457C61A0FFEEF06C27F8B63F9DBE1CDFAF74858112GD2811E83D8F717185F056103ECFE688E0D3AAE8D3A68AF9BE2F2975C72D8880D09BC1B5BFFDA4EAD036B1197BF33A6E1FCC77B2C7876F17A0A28FC77D7D971B5F37A9E7ED46099435F597E4D45B4114747A79A711CB54E2DA6D5D13572B117DB175F277D20B791FF1BCE4C4489199F6B44D82BFFA990EB356F9B77116617FFEB5B9FE52D9238DE
	6B63C3FBF843154CDC265B74DE6429B346F8B990523BED7CC674E67CEF0C0678FFDC0D4F0F1ABE623225E028EFF9A0AC46061F6715959587CB8E5715F13E5871905F615A3973BDB291325DCB13A1994955F31141FC79D6FAE66D3BDB78ED6D4887A4FA0C43FB68EEA8973C43E22C07C7298D4E899BEC38C60CFD3264CBFCE7F23F26FC98CDAE6C063320A7BBFC9C52304153694175B46A9396D2451D1687578ECAD95DADA96E897AD3A253CC5C9B924347DFB73DCFCDD8FD4F713782B646127F57B1587C67F92357
	09F1DE6BBA719E2FF51D18681D7D7EAF599B7DFB07C73C666F9D583ABFB00DEBD381BE558160830885C8B4FC2CC6C59F43AF2EE646459667B05E0361D7D63A98FF37AF7ADC5F77DD7F5B2C83BE1A4F2A3A2EFAD03E40177BF03DF455C7118CD765C39BE96EE1174EC8B97E884DFD95F90822D95DE81F9235E46A5D22169C1E923564CA835755A7688A6F9138EF75893AFD86AE2DCA50A986EECF35208B9A38D61AB7BE876D8D866E910A4B04F649D744B9CCEC18D7D5C33B93C0870883C886C883D88D308EA08320
	A0A1BD2794209E208360B2008FA096A0B1C12C1B0ABF6DC60D8D3C50240B27C9EEDCBEEE3B79D3F8576567F103613EBE9DFDCD9F1F3E7E1AFEFB1AFEAFGEB61987771FB4E2727F19CF5E848AF9B3E33EA3A50403579BBD53F9BA3850BD5EB6D9897D88E7A6554DC6A18483C9BE39A1EAFA9894C6634262632FF1F95127D233E3954A31132053CB410DD94A67B036C106C0A6A6B69AD3E89B34B7E83472FCA082CF57A83F86E39D44DBD21FAF5C9E9D45C43DE5C21BC179F8F08BA7E4584D1CFFCAF70F6980F8316
	CED8DA54A12F9AFCE4097027E2DBF49FF857F6FD9FA835D96F6B0FEDB37775A11B753E2E311977B543B5508F5007411FFB8C3A93866E2201EB26B29177195B293B8C7CD92A9762DBE59C50B84F28DEE3945743F167CD380B9C77398D7D98268CA6B5FE233161B9BD64D694569E24B4EEAF3835E18F9755516D13CCFAFB8C5CC543FFDA6877736CFDF5DE687DB51AFA31B53DAF005F551B2E974A3B71FF911A7D65CDADFFF534F499741B66CC4F3E79AFEA3B863BAF6517A45B2F111DB3731446BBDFFDB9253F3958
	CE6CEDA447C56D53C632475050C17CD96E2B60CD777B2F921AE350565742DA776BA4075D7B6A44FEEE84C6599E1E69599EFA6736423B2A82F612F71A648CC90A4FA541887473058E32F5007A0A2C812DCE8A69C7A00700779065BD5AC16C03FE1D3C9B428FA21E75F67B24C199C8721D12B6C2A520E23917C87E10B6BCC087G1DAE1D3E84435296F667F71037F4971EFDF8012013F7C222BBB11A720E23F4726477706723FFB0319D46445A28BF2C9B2DEA487A3787336CE48B580E6D0FB632851D9CA628B1D3E6
	417E2032B04ED483E01CAC20A64C099881F1851C724F045D40949846C6BFF3DA3410E3A7E1FF48D1B48B4DD942F26DA21DEC411F896001B77FBA5963F0397DA5704F2FFA64923F6471A9F2895A21BCCC2B82AEE3FB98361077043098C7F338BEFB0928B54A264D77851B676D0EFAA76BCAE4B546D4E264FD41FF34237EE900DF13A8625B11F634D6341C375BA3A98AC44B11C62F93735FD3633A9C4A39FE550E727D0206F7323E7FEA4F67FBCD707CAFD0CB8788FA962D46C693GG24B3GGD0CB818294G94G
	88G88GD6F954ACFA962D46C693GG24B3GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0093GGGG
**end of data**/
}
}

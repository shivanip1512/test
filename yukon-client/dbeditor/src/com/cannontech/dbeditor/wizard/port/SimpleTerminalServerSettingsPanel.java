package com.cannontech.dbeditor.wizard.port;

/**
 * This type was created in VisualAge.
 */
 
import java.awt.Dimension;

import com.cannontech.database.data.port.TerminalServerDirectPort;

public class SimpleTerminalServerSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox ivjBaudRateComboBox = null;
	private javax.swing.JLabel ivjBaudRateLabel = null;
	private javax.swing.JLabel ivjipAddressLabel = null;
	private javax.swing.JTextField ivjipAddressTextField = null;
	private javax.swing.JTextField ivjPortTextField = null;
	private javax.swing.JLabel ivjSPortNumberLabel = null;
	private javax.swing.JLabel ivjDescriptionLabel = null;
	private javax.swing.JTextField ivjDescriptionTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public SimpleTerminalServerSettingsPanel() {
	super();
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
	if (e.getSource() == getBaudRateComboBox()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getipAddressTextField()) 
		connEtoC1(e);
	if (e.getSource() == getPortTextField()) 
		connEtoC2(e);
	if (e.getSource() == getDescriptionTextField()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (ipAddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SimpleTerminalServerSettingsPanel.data_Update(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.data_Update(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (PortTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SimpleTerminalServerSettingsPanel.data_Update(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.data_Update(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (BaudRateComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> SimpleTerminalServerSettingsPanel.data_Update(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.data_Update(null);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (DescriptionTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SimpleTerminalServerSettingsPanel.data_Update(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.data_Update(arg1);
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
public void data_Update(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
}
/**
 * Return the BaudRateComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getBaudRateComboBox() {
	if (ivjBaudRateComboBox == null) {
		try {
			ivjBaudRateComboBox = new javax.swing.JComboBox();
			ivjBaudRateComboBox.setName("BaudRateComboBox");
			ivjBaudRateComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}
			ivjBaudRateComboBox.setMaximumRowCount(5);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBaudRateComboBox;
}
/**
 * Return the BaudRateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBaudRateLabel() {
	if (ivjBaudRateLabel == null) {
		try {
			ivjBaudRateLabel = new javax.swing.JLabel();
			ivjBaudRateLabel.setName("BaudRateLabel");
			ivjBaudRateLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBaudRateLabel.setText("Baud Rate:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBaudRateLabel;
}
/**
 * Return the DescriptionLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDescriptionLabel() {
	if (ivjDescriptionLabel == null) {
		try {
			ivjDescriptionLabel = new javax.swing.JLabel();
			ivjDescriptionLabel.setName("DescriptionLabel");
			ivjDescriptionLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDescriptionLabel.setText("Description:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDescriptionLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDescriptionTextField() {
	if (ivjDescriptionTextField == null) {
		try {
			ivjDescriptionTextField = new javax.swing.JTextField();
			ivjDescriptionTextField.setName("DescriptionTextField");
			ivjDescriptionTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjDescriptionTextField.setColumns(12);
			// user code begin {1}
			ivjDescriptionTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PORT_DESCRIPTION_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDescriptionTextField;
}
/**
 * Return the ipAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getipAddressLabel() {
	if (ivjipAddressLabel == null) {
		try {
			ivjipAddressLabel = new javax.swing.JLabel();
			ivjipAddressLabel.setName("ipAddressLabel");
			ivjipAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjipAddressLabel.setText("IP Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjipAddressLabel;
}
/**
 * Return the ipAddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getipAddressTextField() {
	if (ivjipAddressTextField == null) {
		try {
			ivjipAddressTextField = new javax.swing.JTextField();
			ivjipAddressTextField.setName("ipAddressTextField");
			ivjipAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjipAddressTextField.setText("");
			ivjipAddressTextField.setColumns(12);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjipAddressTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the PortTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPortTextField() {
	if (ivjPortTextField == null) {
		try {
			ivjPortTextField = new javax.swing.JTextField();
			ivjPortTextField.setName("PortTextField");
			ivjPortTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPortTextField.setColumns(4);
			// user code begin {1}

			ivjPortTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 999999 ) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPortTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200 );
}
/**
 * Return the SPortNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSPortNumberLabel() {
	if (ivjSPortNumberLabel == null) {
		try {
			ivjSPortNumberLabel = new javax.swing.JLabel();
			ivjSPortNumberLabel.setName("SPortNumberLabel");
			ivjSPortNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSPortNumberLabel.setText("Port Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSPortNumberLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	//Should be of a terminal server variety
	String name = getDescriptionTextField().getText().trim();
	String ipAddress = getipAddressTextField().getText();
	Integer portNumber = new Integer(getPortTextField().getText() );

	Integer baudRate = (Integer) getBaudRateComboBox().getSelectedItem();

	((TerminalServerDirectPort) val).setPortName( name );
	((TerminalServerDirectPort) val).getPortTerminalServer().setIpAddress( ipAddress );
	((TerminalServerDirectPort) val).getPortTerminalServer().setSocketPortNumber( portNumber );

	((TerminalServerDirectPort) val).getPortSettings().setBaudRate( baudRate );
	((TerminalServerDirectPort) val).getPortSettings().setLineSettings( "8N1" );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getipAddressTextField().addCaretListener(this);
	getPortTextField().addCaretListener(this);
	getBaudRateComboBox().addActionListener(this);
	getDescriptionTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SimpleTerminalServerSettingsPanel");
		setFont(new java.awt.Font("dialog", 0, 14));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 300);

		java.awt.GridBagConstraints constraintsDescriptionLabel = new java.awt.GridBagConstraints();
		constraintsDescriptionLabel.gridx = 0; constraintsDescriptionLabel.gridy = 0;
		constraintsDescriptionLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDescriptionLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getDescriptionLabel(), constraintsDescriptionLabel);

		java.awt.GridBagConstraints constraintsipAddressLabel = new java.awt.GridBagConstraints();
		constraintsipAddressLabel.gridx = 0; constraintsipAddressLabel.gridy = 1;
		constraintsipAddressLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsipAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getipAddressLabel(), constraintsipAddressLabel);

		java.awt.GridBagConstraints constraintsSPortNumberLabel = new java.awt.GridBagConstraints();
		constraintsSPortNumberLabel.gridx = 0; constraintsSPortNumberLabel.gridy = 2;
		constraintsSPortNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSPortNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getSPortNumberLabel(), constraintsSPortNumberLabel);

		java.awt.GridBagConstraints constraintsBaudRateLabel = new java.awt.GridBagConstraints();
		constraintsBaudRateLabel.gridx = 0; constraintsBaudRateLabel.gridy = 3;
		constraintsBaudRateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsBaudRateLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getBaudRateLabel(), constraintsBaudRateLabel);

		java.awt.GridBagConstraints constraintsDescriptionTextField = new java.awt.GridBagConstraints();
		constraintsDescriptionTextField.gridx = 1; constraintsDescriptionTextField.gridy = 0;
		constraintsDescriptionTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDescriptionTextField.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getDescriptionTextField(), constraintsDescriptionTextField);

		java.awt.GridBagConstraints constraintsipAddressTextField = new java.awt.GridBagConstraints();
		constraintsipAddressTextField.gridx = 1; constraintsipAddressTextField.gridy = 1;
		constraintsipAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsipAddressTextField.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getipAddressTextField(), constraintsipAddressTextField);

		java.awt.GridBagConstraints constraintsPortTextField = new java.awt.GridBagConstraints();
		constraintsPortTextField.gridx = 1; constraintsPortTextField.gridy = 2;
		constraintsPortTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPortTextField.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getPortTextField(), constraintsPortTextField);

		java.awt.GridBagConstraints constraintsBaudRateComboBox = new java.awt.GridBagConstraints();
		constraintsBaudRateComboBox.gridx = 1; constraintsBaudRateComboBox.gridy = 3;
		constraintsBaudRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsBaudRateComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsBaudRateComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getBaudRateComboBox(), constraintsBaudRateComboBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_300 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_1200 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_2400 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_4800 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_9600 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_14400 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_28800 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_38400 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_57600 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_115200 );
	getBaudRateComboBox().setSelectedItem(com.cannontech.common.version.DBEditorDefines.BAUD_1200);
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() {
	if( getDescriptionTextField().getText().length() < 1 )
	{
		setErrorString("The Description text field must be filled in");
		return false;
	}
		
	String ipAddr = getipAddressTextField().getText();

	if( ipAddr == null )
	{
		setErrorString("The IP Address text field must be filled in");
		return false;
	}
	
	java.util.StringTokenizer t = new java.util.StringTokenizer( ipAddr, ".", false );

	//ip addresses have 4 1 byte #'s - in decimal anyways
	if( t.countTokens() != 4 )
	{
		setErrorString("An invalid IP Address was entered");
		return false;
	}
	
	//handle the exception that could be thrown by Integer
	try 
	{
		while( t.hasMoreTokens() )
		{
			Integer i = new Integer(t.nextToken());
			if( i.intValue() < 0 ||
				i.intValue() > 255 )
				return false;
		}

		//see if the port number is ok
		Integer i = new Integer( getPortTextField().getText() );
	}
	catch(NumberFormatException n )
	{
		setErrorString("The IP Address text field should only contain numbers and periods");
		return false;
	}

	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleTerminalServerSettingsPanel aSimpleTerminalServerSettingsPanel;
		aSimpleTerminalServerSettingsPanel = new SimpleTerminalServerSettingsPanel();
		frame.getContentPane().add("Center", aSimpleTerminalServerSettingsPanel);
		frame.setSize(aSimpleTerminalServerSettingsPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF1F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8BD4D4C716A47E08B20A89C6E3CC4628C918445DB13BCE46493ABB06F5CFC8C2B266C4CF1C09336BCEBC93F6E2F61993F74719F1B2558DB4BFD17CE4E2A2A678CB50E1908CFE864D8802A84A46C6D885B46003FEC0E3F73F663D5752A8513D376AD5756B269BBFC94E49F84E65F55D2A7B29DB37EE5DDB6FC9B6FEB8B55DE25DC808A51D987F8ED888D972A9A123361F1A40F1D1F2020D187EED85D8
	C7FE5917864FD8689BBEAD4116C7CE57E5C2FB8634136ECB303D8B3FB710279C79E570A3021C951F90322C745A422165384FF1B999347D793E5570DC83908FB8FC2E10D13EEF5FA6831FE460B9D82CA0279347446E4BB7F08721ED87188AB03B130DCF02E79615BC227C20B16E427829E445F39FDBF3110F09A70722AEC6EBB71E85643B1615DDA86B87D4CF24090676BCG1CFCAAC97B74A8BC8F5BAFF57D25C3F197F7C89E0F6251650E6E62BE6795C9F594FB95D5AFAFEFF03A3DAE39D1D65DCE0F64EA10554B
	325AA06B3A53532559A50F6CA2C46FC6B9167D1914AF7004B6F1A22E41063267C0FB81C032403F1F0178EA78FD81C0F77279FE7A71E1E3BECDD73FCBAE3C62C9DD9BE13EB1CEB64F594E40FC63FC3F49F80F2E4D5FB4619A24C05F461E84DBA1C09D008BA086E0B375095967F6433332DD2FD25CEE45D369759EF5B9547FD59C328BFE1717C30F01BBA3F7A82A0390366EBF5CD794C61F2100513DF04BF44CA6711FF09F18F9669132646FCFD9B34258E4C9FC09F5C68F33455C1E00CD18EC5B7EC8321774847BE8
	306CF344AC3BEE24121A9EC6766CC53B2CA9861F34CB8159E517F02D1F21BE3083FEDB8C7FE07817A85EEF42B3FFED26F8ECC7C35F72CB5CB71A56F0DD6A52A6133C523A30F61814D0E9353B188E5D2ED0DD46D23F0BF3B119D9C2177BA9FE16894FF4A920F8ECA7C1DF09BB41467C6B49965C3FF1501E851088309400F892EC79GA59E6EE33FBB3767A6FD4CAE297AD11F536510D542E81FEC7A83BCE53F2ECA359ECD17BC9D72D9C9F5CA6DAED9A346189FB6218D5A036866D35F6FG9947E50F2CCA3AD371G
	F749AD6B322A31B1AFB42D43B12ACCFB9B1DEE9984383D0473DD5434811EAEC9537F4F6B10F4B9B002757F3489EDF2D9BA8B9C01817CE6F6F96DBC5A4B8E736F86708BBBB4B5A33FD3328A71223C3CCED1AE793C0DB8931268C1BBEF2131A386FE4F70F09B7F5B85442DD692ECBB944EE7DC73FA931FEAE7872AAF297DE20F59B0C69FD4384F7CE79377196A5C07C9512213E1FD067A0A42FCA5CA895DBB2FD01E2897EB37591A0658338715707B6956E205FFC89929DF027F9A31D6CD2D38D671206FFC00E54246
	4FB597B01FBDAAE9B2775253AAB8186C88D81C69991711E75893F5CAB774E7FD8F513ED538FEDE083FG3A175B70230C26B0F4DFE6CE03EDF8044AB0734F7A8AE5B0DB4DEB4173C93EACFB74EA59ABFB9C70C3B37A1EE9C1F9467E6DD4844D8B946F057DDE859BCC937817DBF0CFCA3A2EBA5BFD3A4C4FD8517FAA1525795A3D01D041E57D2C8567287441FE9763DFEF413D5221F8BCB2DDB4C1E2747F875D2B32CBF60352D50A4F23B37CFF1DC7FF3FA4779B31622D73188CDD16DCBE0CBBACF718A565609CD427
	9BDC81C397A13D380F4B76EFB259E88A2CB766C08B61196C65FB32EFBB5F13BB2EBCC466356D3406BBD3A6BDDCEDBD514B7652055E401EE4BAAD3B90DE77DF5018E160B5266B6E824C5FEEA20DB1764578B664BDGB2E38162FB793E687D9C7BF96CB1B6C41562F2B1738A3B3736225FEBF22FCF06100DC16E1430255C3ABE246F2CE1D7566FEEC53B3ACC71512522DD97E570FCF19E683398E0E5AF33ED194AEC4346CD4A646366C0FB81C00A9AEE5C79ACBE2EC8CB30D583B834403899701B0D7BF63660873885
	GA9DAB0BFE6630CB64CAF426512DDBEE731CFF7E23416F45888DE1FCE4D4DF24687E95C473DD924735CF0CCBD5A5F816D8100B81D7B4DCB39BCB70C3BB415AC1D7EFE44DCF8264EF4DB20877C26E0984F8DDF3D08B273FC8936829FDF5B64D67379D429E9CA07139E0D463ACCECB37B9543CDE94B06E727D35574DA5878FE3E06535AD0DFBCBB8D3411CB3CCDF38934D79948F581AC75A17EF94BC6136EB3FC6C8CCC02E7AA7319CC4CA1BAA4D5560B69DFF6A49776C817A5FF3156879EDCCCA3D0F195769E431F
	3B0CF5B951046B824325B6032AEEA832C33B4EB2DBCF5BD0E1681374371349616B9B2C6172706835DB2C05170D3A63F2405683FFE7CBDD5E7A5B8CB66F27B3793C2740180517793C2352434DFBF59F1FF7E1566D4C1B499C1E45E596G3F26BEAE731C4DACB3360F491CA3E43E113597E3B05D50456CE11755CEC5F54B8EAA38D86A538D2915345B90EB54C03B33F98D1484BC19AE0959DC17B4FF026D301F6BE28F3B6E3A1F6B32A767F6664F4E3E69C179FEB4701C628F64A5337C03F394A3A7BF033A4E7733BC
	F81D407F0C62D3CCF8B63771F42FE2BB3F9FE2E5BF4F5DFA5A719CA83B8235ADC0B7C09440D400F9D7781E5BFD914A6E21F363DA08A1AA066B20C34F58E7CBAF221D601CB25A4BAE220D69B9C5F88E101699DA77AC3992BC7715A1EDC637FE905D87A16D7C2B41F4EC3F6DAC4C67F15034CC6565A79BA18F28F14AAE8773932B957D04569FD7F9FC7B22469C5F662E5DEB4D09905FDC066C182B01BDD7A4EA5F0D8E54A1897A128148G54A4G95G97G2286387DBD5227B732BF7529C6258AF0A760CC2A8A4DA5
	043D1E39CDBBAF3A211D278E8453B17BFDBF1D1F9BF386F8CEB9BA6B76F418B4082EB8241DB290EE2D3BDC1B432FB55833DDB92A788DDD475AB28D1A4CAF382EFB32D1E64597B7DFABB03A9733EFEF0ECB8651055A39710BF0F69EE561F6F6C1BF2B551E22E7768CE84F96382B1FA1AE915ACB842E490EB8F2AD4136699A1F77ABE78A50E650EE825081E281E682A481AC87483C4E47A65331BB20DD8D60G98FE1D65A0187FE0EE03F98B9BBB31BE8AF53EB6581E078D7EC359164B8D5757E46818F4839E81DBBF
	C5F589D74FE03C4A39810F35044553F0FD68C398ABF2EF42DF589E575AA357A7D75A4357A7D16D616B13116D6B989EF818EFABF87F5876216B17FB5B4355AFDB28DD1E7D43DA4ADB53E40D869DCC960D3E5775F7C818BA66B349AD9BB65DC08EE5E699E30E69CAD529D80B8C2B596FE798EBBC47F8A6052C79526B7C7C3ADF4633F98534732292EC05G95G9723F86CAC6A386159F54CBF44597501A3723A6CF404DF17BFBAF05DB54527F64821365F6B58A47A82D13AD6175DE8A6B666902557A535CB9E34F6C7
	A9BD6B0BC4FF4A91E9FD599A3C9D3486FB459A4CCDB72FC1D4D4304D63C25A71D1BC7FF9BA4B4C6F93416F9E1B195F5C907A85D10377A90BC7B5B4B7DB867DAB040C749C330CB2A1A346E21611F6C7B04FCDA16D22BBB83F1776E36D5C89354F409DA873F59AABAB60F71D98F377E7D8CF3B24FE4587E5F3511DB8AE63B42F1B67413845G84709B6FE4B9A1BBEF1E6843585129CAD041F6B5F8658E12B98CE995BB2755E1FCB4C0BCC0A2C0721DDCE6CDBA562455325621BA3D3834F5D23B6CE2FD7377A12D53DB
	69F0283226519E3E26F97B112E412E287AE7BEF73B2C1A683E359B7D692864F31C013454A0B3682E55ED8C16A7729F567F53FD6B4DB2C5AF277F55BE640DE2CDDDC65FA33B73CCF279E14BF26A0286F3CEED9FE6645443F84E3DF6FF387EE8517FDCEE387EA9227F097EF07D330671B36F831A574D0776C2012BEEC1DCB234D78A5CF7DA785918B61C63F6531C26915A5D43F91C19AEC70E930F49616344ACB9FC7CFED29EBAFE7F139CE9FFB37DDE267A8D00EEB1C2676F3772F9CC95B80B0D4FA3C5609E233834
	916053A3B84E5546E98BC7F0BF7DBC9B778F0421526891B8C623ED3F2019CE7311A4E8AF9138AD4D1CCF0A40F9ADDC070A118936461178BEAF3517889FD2BE5B86CF0F5CD7A00A3A9A5834055B9CCE4925F4EDABAAA8A9AAA8AD5B455F03CC6DB33F07BB18FDA06C1D95422411ACB64CB41EF3C7EDB7EA25BD2779FB38E55017BF0A6F6B520BC1B583AAD3DEFEDCF5BA0ECADDF5B4CA30DC2C6FF426C15FB80A5F15BCCCF946C0FB86405CD13C1670FF44EB09190FCCA6F5D127425E15ACED5CEBDDB80A6939F2D4
	20160807DA02491CE067B24B2293EC2EE8447D335D3C87501E4DD616B3337E7F8E6AC71FBAC04C7D9F571B7BD15FFF8C22EF8E6ACFB7513339BD3F134FED55737144753DBFC73C871A9D4D663458F85A4FEEA7BBC67FB603597D2D4ECDA4EC7ECEC3991B7F39861E7F665D85F10462EA441D558901332C6138A801CB6E97B54ADD5C3F7BC34E0763B4DE24C07FAAB166FC8B0E51E1E3B33F4D42ADC89C23F17C3C16EAE3EEA523871FF329A1D08DE390369B76E99A4D6AF7FE6F3E8172FB542FC3F1795C9E0DB01D
	9A68BE47BCE866E82ED3591AE03DD751B1F321BF91006976E2AB57EDD5845D520750ADEFCCC0B7A6E3370D574879E3C4CEE08B566395BA26887A8F0E61FA8CEB63FAF40F892FC7C684BDD03F01B121B63A0B46A00F4F65B25ED14B66D8D0F255E2CD0D60FF51067CC9E382310071FC4A92BC0F85B4DEA5423805E29EC71A79BCB2E3424F4392E19E084F0F89356703963E2E05B1BCCE7C683CF9AEF91337DAAD9166F2505021BAA690A7D82E7C979D797A819F97434F2D065EBF42732C54E3E446479CCEDDD1E9EA
	8CC83B2AF8E5D56FE7D8FA83EE64DD7BCE5F5E3B752A5B249B38E5BAF69F761E9D636EEC130D99FE9B454FB361D96CFFDC447EA468AB79962FB3949F4620B8E84F82C884D88AC0464279B0165BF2CAEF8939C12D5128F86BA0C9F0190EF5B63F49765B7B6660698C9EE3E7BFB50D4C9979EBCB243B281231C6ECB51E490D5BA91E4D4F5A07730B023EA9G73G9683AC87C89B4767372C37084D8F5D21D257250EEEFA4B4EA7F756A977855018783239DDE8B04FED53B8A63F48F8D60C9B3CE7985D29203B51C6E3
	DC37719C08C8E7DBE3260BB5464DB01EF3A2523D59EF26CBB246ADB51EAB425031387F6E95DC8996AF8E346CA4E17395CD72C04D2CBABBCDA90BF1F7734B96F16F941BE07B1072015FA4700F49B9F61508F57F7755FC5D739F3F1FD43F72EB8B0FA721BAD644B23DE3E38371A039E7D5865337A79D0EF4D23A174B78533540DC9EEB3D75397C3E154FA5B11667F3315783BCAB69FEAB93F37A15A550F7316DE6F3BDA6EBF69B17C546F3D90F52A67369C6760D41F86E3FF59A720F946B706E3481D2EACF97576D6D
	4C00EEF94D37EE8BEFB3572FCE6837B5BB4073FB96EEFB266F0B340F699A2B6D0C24AB6C3413204369769EA87E9B25AEC1D325637A0A3A0005EB236F240E391A53635441164EAB7415991BAD737D8F6DC46C03C2536FA7CE8F75FD431785A663392FD5861B3F5F9739BE3B6E8BDF1F0D70052F4F4678063EBF3B4797797D7FE41F797D3F711EB38757E6B87848D40079G0B81168B3FAD3C0CFE0BDFA08D0E39479CDDF80F04DF9D69DCFECEDF64392E6F3B7137CA1B7B50915B95DDD75C481F63377661FA690A17
	A2996E23BE34116A6C6A5659D083DF54073E6F12BBA91A55FA9A4D51B36FCE30ED3ADB540475B4AF0476E1013B331E5709A7842EFDBF2F57A7895C42A3081B896D398277678ACE1B9810310657431B6E9175F08D158B6D438217FFDC48953834AB081B846DF801FB788C4D29211DA4F0598D1CF671BD7C2E7BFFDA4C67C9E19C480A9B7AEE1A79C3F5AE6613F6986B8208850887188B3090A099E095C05E84284781AAG9A813A8186GE2G26824C1E407DE77455DD11E289A00D8216BA11AAB9510D78BB39B633
	7E0940EF49042175E7F4BF8822CB811A34FB0726B33EBD3361FD1BE644E0464B579C14BBG1F221B6275DACE64BD700B1C9B6F0137F268F784603B0D7DDE63DEF5E58E6A27F4F6EA3258EB3F4B499338DA4D2EE8CE0C2CAC3E0FEE882E9D26D29F29G7D6B6E9575DAFDF08DD900EA9BB564FD753C2E9888B3E7F36D932EBE0A3DB7342E98C777509440478B79EF9C8916DF159710EF2F607297C7109F2906C5794B8749BFC97761CA4027CD6472DF2C89165F39AEA07F1363DCFE59447072A3552908BFB1B134BE
	5CFCA5F8BDCA695EEE04F19705BEF34F846B73407A00BEDB9B38BE319174992ADE1DE15207450C5C73A8FF56C4D663B15CFA0ACB845C2E403E480D6C4BED39B776656E5C70715C13BBB81EEB396163F9FF2EB91EB35C35DC1CD797440FA6B1EE1840B58BDCB4650938F3CE075EAD70E329DE08BFA123C00E0F23FA75D1DC13013B4F04EBB6F08F6662FE04A9F7ABAA271D1ECB575869F2317623F40C53835BD358E3A655516915CCFABFA1F04DE2ADB6378DDE1F02B6737AB05C82B1AE5D42F10B443883E2DCCC73
	E0FE631B4D7C184F1D4357E8064F2E2B3F351C7839FA1EF3AE1B48F34E9168A2866F4E77823C930F5C9A6FBBAAB86F7CC91C77DD6F87F89F78B0407B711ADB636DB84EF9B78A5EAD85815ECFEE8B703E6F4C2D71BE5340F947885EBF46EDE9448F692355C4644654C599BE5F1F9B40F397B54E259F551B4F257940F771241BB99BC7D6186916838D396FE6681C474DF49B01267026684E85555E070146FE83BA6617BF2F673E7A32403DD14FFDF52B40BDD041470D9738E9950347C59D67639E95389147870F73B4
	70F163840E650DFC9C7BFFC2A3744781B973CD122E29A4B7488F7E6521FDBFCF7A5733DAAA31551277B05CB7C0FD06D50F6C403755462770B5A44B2BC03A53G78DA122651C73A26773B64BA3231738CFE7A296932231267791ACE5E219F656D414FA4771C1034EE9D636C21BA1266410F7649FA566D12BCDDFBD8F1F5480F1F6ADBAAAF2237D1739250F783559739948BAE9B7EE16D4E2DA7174DC9A5569A126DD2A4C70D5481E9DAAD49523BD5D96BD6DC8E3F77C885E514C933EB3C69C985F61B4574F1324578
	A2CEA8FD497FC0CCEEF2EE8DD41AFE156C21A371F4AB96A39CD55D128A22E435D6477F042028BBC45F9D9F689F3DF07C5D8BD6E2B1A659CB2E912B5F2B93DB2764F63A7A61875BF59A7658F450D6050A98BF44834D8AC2B463DDC2C071BD4F16CC784EEFDA2800G29CEBEE4268452BDDD32625ED359596974480281589558EF916CF1CCF14818757E716F1CDC747BF67003EA126AAEA921FFCB695F3D783714E2CAA926F4AF98B7058C79AF345F4F67CC1D8774D9CB754165AF2E019554FF72663353DEBD1C329C
	E6D7C3526987EF3088F901D16CD56D481993366444AD9D03EB64C5D771A35FECBFC49BAA018CF2A144268D7685C4E7429208BB89C46450513974EF5EADCF912956D35A0DC233777DE22EE87BDAFA38AE3D7CC9F96AEB53D32DA42D1658D832815B8AB38658D6B42128A52910EC509D0181B65B10117E0D1C64B6363A9FFCA54A9AFB646B51F457D7296957ED5612884A6AE43569A304F21D64545A8B0C0F8E20359A3FB0A8E05B851A5978D1C181FED5D07E4DDB0FC301A9867D7B66E9FA742FC6535A3F9ACD6D91
	B4B56E6DF7DDB73F47DBB9E2D3446F93922731BCED49345077F849FE1C512669893632697C0E67207FCFA472FDF103DBF239F449F5295263403B63237D3A9C382FFC530F7CB5BE4668E4FDAB7C28F70F09667F81D0CB87881A96409CC295GG84BEGGD0CB818294G94G88G88GF1F954AC1A96409CC295GG84BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFC95GGGG
**end of data**/
}
}

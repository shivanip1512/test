package com.cannontech.dbeditor.wizard.port;

/**
 * This type was created in VisualAge.
 */

import java.awt.Dimension;

import com.cannontech.database.data.port.DirectPort;
import com.cannontech.database.data.port.LocalDirectPort;
 
public class SimpleLocalPortSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjBaudRateLabel = null;
	private javax.swing.JLabel ivjPhysicalPortLabel = null;
	private javax.swing.JComboBox ivjBaudRateComboBox = null;
	private javax.swing.JComboBox ivjPhysicalPortComboBox = null;
	private javax.swing.JLabel ivjDescriptionLabel = null;
	private javax.swing.JTextField ivjDescriptionTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public SimpleLocalPortSettingsPanel() {
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
	if (e.getSource() == getPhysicalPortComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getBaudRateComboBox()) 
		connEtoC2(e);
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
	if (e.getSource() == getDescriptionTextField()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void comboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	fireInputUpdate();
}
/**
 * connEtoC1:  (JComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> SimplePortSettingsPanel.comboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.comboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JComboBox2.action.actionPerformed(java.awt.event.ActionEvent) --> SimplePortSettingsPanel.comboBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.comboBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DescriptionTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SimpleLocalPortSettingsPanel.descriptionTextField_CaretUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.descriptionTextField_CaretUpdate();
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
public void descriptionTextField_CaretUpdate() {
	fireInputUpdate();
}
/**
 * Return the JComboBox2 property value.
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
 * Return the DescriptionTextField property value.
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getPhysicalPortComboBox() {
	if (ivjPhysicalPortComboBox == null) {
		try {
			ivjPhysicalPortComboBox = new javax.swing.JComboBox();
			ivjPhysicalPortComboBox.setName("PhysicalPortComboBox");
			ivjPhysicalPortComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalPortComboBox.setEditable(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPortComboBox;
}
/**
 * Return the PhysicalPortLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPhysicalPortLabel() {
	if (ivjPhysicalPortLabel == null) {
		try {
			ivjPhysicalPortLabel = new javax.swing.JLabel();
			ivjPhysicalPortLabel.setName("PhysicalPortLabel");
			ivjPhysicalPortLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPhysicalPortLabel.setText("Physical Port:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPortLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200 );
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	//commonObject must be of the local variety
	String name = getDescriptionTextField().getText().trim();
	String physicalPort = (String) getPhysicalPortComboBox().getSelectedItem();
	
	Integer baudRate = new Integer((String) getBaudRateComboBox().getSelectedItem());

	((DirectPort) val).setPortName( name );
	((DirectPort) val).getPortSettings().setBaudRate( baudRate );
	((DirectPort) val).getPortSettings().setLineSettings( "8N1" );

	//be sure this is a local direct port
	if( val instanceof LocalDirectPort )
	{
		((LocalDirectPort) val).getPortLocalSerial().setPhysicalPort( physicalPort );
	}

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPhysicalPortComboBox().addActionListener(this);
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
		setName("SimplePortSettingsPanel");
		setFont(new java.awt.Font("dialog", 0, 14));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 300);

		java.awt.GridBagConstraints constraintsDescriptionLabel = new java.awt.GridBagConstraints();
		constraintsDescriptionLabel.gridx = 0; constraintsDescriptionLabel.gridy = 0;
		constraintsDescriptionLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDescriptionLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getDescriptionLabel(), constraintsDescriptionLabel);

		java.awt.GridBagConstraints constraintsPhysicalPortLabel = new java.awt.GridBagConstraints();
		constraintsPhysicalPortLabel.gridx = 0; constraintsPhysicalPortLabel.gridy = 1;
		constraintsPhysicalPortLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPhysicalPortLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getPhysicalPortLabel(), constraintsPhysicalPortLabel);

		java.awt.GridBagConstraints constraintsBaudRateLabel = new java.awt.GridBagConstraints();
		constraintsBaudRateLabel.gridx = 0; constraintsBaudRateLabel.gridy = 2;
		constraintsBaudRateLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsBaudRateLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getBaudRateLabel(), constraintsBaudRateLabel);

		java.awt.GridBagConstraints constraintsDescriptionTextField = new java.awt.GridBagConstraints();
		constraintsDescriptionTextField.gridx = 1; constraintsDescriptionTextField.gridy = 0;
		constraintsDescriptionTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDescriptionTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsDescriptionTextField.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getDescriptionTextField(), constraintsDescriptionTextField);

		java.awt.GridBagConstraints constraintsPhysicalPortComboBox = new java.awt.GridBagConstraints();
		constraintsPhysicalPortComboBox.gridx = 1; constraintsPhysicalPortComboBox.gridy = 1;
		constraintsPhysicalPortComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPhysicalPortComboBox.anchor = java.awt.GridBagConstraints.EAST;
		constraintsPhysicalPortComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getPhysicalPortComboBox(), constraintsPhysicalPortComboBox);

		java.awt.GridBagConstraints constraintsBaudRateComboBox = new java.awt.GridBagConstraints();
		constraintsBaudRateComboBox.gridx = 1; constraintsBaudRateComboBox.gridy = 2;
		constraintsBaudRateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsBaudRateComboBox.anchor = java.awt.GridBagConstraints.EAST;
		constraintsBaudRateComboBox.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getBaudRateComboBox(), constraintsBaudRateComboBox);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//Load the available serial ports into the combo box
	getPhysicalPortComboBox().addItem("com1");
	getPhysicalPortComboBox().addItem("com2");
	getPhysicalPortComboBox().addItem("com3");
	getPhysicalPortComboBox().addItem("com4");
	getPhysicalPortComboBox().addItem("com5");
	getPhysicalPortComboBox().addItem("com6");
	getPhysicalPortComboBox().addItem("com7");
	getPhysicalPortComboBox().addItem("com8");
	getPhysicalPortComboBox().addItem("com9");
	getPhysicalPortComboBox().addItem("com10");
	getPhysicalPortComboBox().addItem("com11");
	getPhysicalPortComboBox().addItem("com12");
	getPhysicalPortComboBox().addItem("com13");
	getPhysicalPortComboBox().addItem("com14");
	getPhysicalPortComboBox().addItem("com15");
	getPhysicalPortComboBox().addItem("com16");
	
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_300 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_1200 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_2400 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_4800 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_9600 );
	ivjBaudRateComboBox.addItem( com.cannontech.common.version.DBEditorDefines.BAUD_14400 );
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
	if( getDescriptionTextField().getText().length() > 0 )
	{
		setErrorString("The Description text field must be filled in");
		return true;
	}
	else
		return false;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SimpleLocalPortSettingsPanel aSimpleLocalPortSettingsPanel;
		aSimpleLocalPortSettingsPanel = new SimpleLocalPortSettingsPanel();
		frame.getContentPane().add("Center", aSimpleLocalPortSettingsPanel);
		frame.setSize(aSimpleLocalPortSettingsPanel.getSize());
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
public void setValue(Object val) 
{
}

public void setDisplayItems( boolean hideItems ) 
{
	getPhysicalPortComboBox().setVisible( !hideItems );
	getPhysicalPortLabel().setVisible( !hideItems );

	getBaudRateComboBox().setVisible( !hideItems );
	getBaudRateLabel().setVisible( !hideItems );
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G63F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BA8BF49457F5B2FF03E28B879C82789C0CC90E12E223BA6414A624F5E22505C4E97189581CC6F555943BEA0B93CE232644217153CADA7DC18199A4A10278B9D2A5AF62E3C988591110786883D6110CC50C85A36DE835E8F6E71919C5ABFE6A3D6F4D1B1DDD4DCAC013F3DC1DF3B57B6EFB77736E3B773E7B5E8CA95C3BA8A355D6C8C8EA86517FEAD38999FC0F10D90E3FFC1963E245F8BBB17D6D8378
	B539BC1A8E4F9550D7B7BF5EDEC066F833201DG6D64A7626D3B60778E72C5EF413B70A3021C64AAC2A635BD771571653CF60F4B49246D7451BCF8EE85D883B8FC168E207C5251A29D1F2263B9245A8849F261185A51D29DF78DE882G71G6B875878CD704C261227565769632E4CDEC4D23E77DBDBBE72B171645069E2341A7EACA34BD2CB87D156B52AA752AC053E8DGB879B412F37BFDF8B6F48E39BE6817BD0EFE416B153D1A58BF6898F65F92942743A7ABDAFDFD175B6313448E39DF10BA8151A5EA1A5B
	6BD2BB852FA8817DBCAAA3F6B40B720479C07B209BF14BEAD0EEA234D3G725C9CFF3396710370BB8AE01E1B4F757D5FB668F369393D0CECF9451B36AD42DC635CEC0E6B5D41394E713F19D9CC576590C17B97DE87FFG68G708144G44816C267ED0B57A5F70EC6A531AE50FC776B679FC27A427F2DEF60A927C2E2F079E9DF7D16C1795A7A1EC4D87C62BAC74998F981D7640F44CA63147787AAF3D78E5127835F6DB2605CD92E32BED2B2EB3DBA4DD8F5A0449FEB622ECB2946A1F2132BFA6E6599B2749E999
	9632172F2B30956AFC4A070232FB06F02D8F5035BE873FD3F57FE078F7A9FE21894FFC756B54E7303D9447C95CB7FA36F2DDBA52671302A39D16F618771DA61BA6B19D2225F0DD7E154ADCA1B1191906AEBF2778B5A6BC53658E45E3BB997ABABC71F666DF8E9B46EE9C3493GB6GEC81A875023F81F4F8390F79C78F5F270FF58A0AF64A6F161C22C2982D036C042798509421552BEA0237DF3CA4A8EE21CF92D5220F29253A750550B5D05F6F8799E7C52F28881ADB7682F741A3EA222232B127E18EB0C691E9
	EF375BA302G0F0FF03E6781881184D57B486794B4B1B80275FFC47D6326F089B88283784D6C72C99A5ACB0279CF8118E358E18D6557AEAA10B06A6BBBE4F9486F6B4619108DDE34F3BC4D9D31707B85AF37F1DFBA624A65F8FB234C79E426EEB771E9F177237A02B242E32C229673F3374CFD66A7BD5CE7DA723FC42A5635D97A8CC2C0E63E92A307474EB14A9375E26DEF5674CC90375D32F5CC5DFF2ED8BB210C3407643F55D82BB599B496C05735GA9060DCF2516B11FBDA528A277528F95F0B0519934B853
	F3D3AD5FD7D01F8CC37FAD941F670B379F87386263361BD45B93B67EE1679059F68B288C33BEFFC8994CC6F5B65C17441B22D7EB91FD225789BFD43D6FF7B674BDBDEE87E40366B4457BA04E1BA130D4835FEE43D894B4CDF17779B511EF2EC6FF9715257A7BFC419441E57D5E06F31407A14E0D71D7ED98A37D3257AB5245B2C874FE014628A8099ED03AD976FBB506F725211F8F09A3FA0E98CA43826826A079B15F30FAA1CE4845B9A8EE8F38G26AC126C43787D11D16FA0CC819C56BDAB61394647E3F178G
	0F4503371EA6AFDCBDE4335ACB66FD2945D6FD0345CF4B0DE0AC6ABA26DB6B6E2139C2472BCC577737E15D47CA1731D58B0B016CCC646784B98100189BBC867EA59D7BF91E511D3FD916A4E6D24356AFE794605A08B77CA224E7CCE86D067D7EBEE3FBD85FA55D162C7F358C3425E44A05CE85EDF9380E570531204F8BGC9B718BD4B94E68FB66E4FCF72F1F150DE25C49A67E960631221CDD468D75938ABAA9B476C15F995EB842BDA5065F7BB7C1A9BB32E2001D37B7C9AB5A22B7BCE13ACBD4E22D5DE5F75F4
	20DD63211D0832D46E83AF65737AEE4E50A21274CCC944DAB6CFE33AD5EBC19FA81B4C6B3B2EAC1469033EA80D2F59B53BF90FE9D2D5395FCD37B75D5E5BB24DBE42F00519B9709CF0AB2A568AC19C60EBD312097A627E272375FA2027AECF37579C101BG50682775FEDD21C977F4BF5B472A6019C679CDBDF994E31CBA0F03BDBAC5E5C0D6BC22337C3AF0D3F088431A036693C7936DBE0D3FAB785A58AAEAE9CCFB7A64D3F260EAD398070E48ACCED6DC52EB3945BB9AF57D921E1BCF9AC6F7582CEA6B995B76
	58E678194D637CC15B5FF95E1E36317717194C8EBFBE496D3085469C3F496D10D2E3E587897A995D138D1CAE8AF0AB8C3A37CE1869B65DE4F4C5435CFE7F5500F5D43F20081A037EE7E589B5DD40218EC3DC6BD3EF46DE5DF84CF6AFD69C40D89055FE456D23658E6466B3EED1F2DEEDB63182BB317D230D18EB65AA5021F1B8381FF78F0F5D5BD98DB96DBCBD538C339AB25F404F2578E8931E59221746883663202FB4406B1E9FE7E3AE1D816DA5GAB81568224G940EF0DFDF1CF90CCCD0D736C26CAADEC16A
	8A59B8F47F6E49C23BC22E575BD73250D6B457933E0F7656051F992AC7C2675E9156E6F452983A5F0435FDE1F4EC1DDC52EE1EFFCC4BDADF5F562C3B3DFE4E6C2A6379E646884FABF74F18734A4AEDC7ED3991724A8B3A6C241120EFD799674668DC4C15E537626D8DG5DGBE00B9GF1GEBEFF17B3F1E757EC47627BE58ADB7836E9C647866707D5830D772490733734F4750055BB965D6A89D33DFE5ED16EE733C5B3CAEDB5870F0BAFCF58C1DA32C5DF25BEA2D8F1517DA2F35911BCC571AFA3E56D7008FBB
	AB88F43F19836DA5866EDCAB62D6C0BB41409DEAC75CA6E8E75D61730CBE0F31D5856DF3G03GD3G16G449BE39650B1C950A6F7E12F3C4B76CC5C07F10FE5E31C9D57ACFC0B83720D54F7AECCE6F8FF158947E40D0EABEB3C7A96ED0D71013110EF51EFBEC3B03F7E376C48F56D3FE7DB57351B332D6B5ADFE57F1A61010779F44B7B6DB963573DDBF32C6A5EBDD46F55BB37D15E2AAA2AB45030B0517B7ED9FB1BD8543F9760G2C4FF98719DB172D0FB92D494DC78CDB7704642E463B216B538D6D41B09C4B
	9BBB72F17F3A83FD73G62GD681A45F6579E2C94E04797AF4E01CFC7DE4EE64F5F9AA57FADD1E4945B5D7E53F52AF065B7E2B39C5C6DFB0B335EA22874D4446FC0D52EB026292472C5DB7A8BD6B0BC47FE2EE2475E5EB3037413C86C70DB5282EB52FC1563DD0FB175E9B9BB30C5F8FC3789DB3781D2CB7732B8E23EF89EB775C63B5C70060F9E8GEA6058FB18CF9E23F9C7025F81E34C55D6BCA3C942086C5700F694472DED65E7210DB0AE9DF0D5G0D232C26E1796FCF07B1A687944183A66972097D64F894
	52B634F35AC5B0FEB940DA00940042A8AE7323F7714C51922CE7BA04BED1E2FDB1F5E8734E4191552D5F5552CE43C69F37611A1E927C4E0BD07304763DB8DC984A5748452C7F155D05E13C79464C7ABBAB8ACC3CF99F2B6FDEBAEF2E6F6EC4B1DBAC0A62755FBF64D975AFB77AFFD5E8553FAA0A67FA19568409505EE8605695717CCF9E6138AF56A02E935A4EC7F8EC6E4E0F9CDB7B732DE32BBC5FBA67B972474FF9B57911E20269F736164FA3E068ECAF667308B6F097693E18886D0D862E2A44186FA40E7BB6
	655789EDA7C0E0923E53180DAE262FF9C36BFEF8FA4561B26384F1869C3EFC3F53ADC832EBFFD5D9F5D5591163957CBEF8513079DDC4DDCE2D65999EE165A496D7EB74674A6987743AF7CBAB3FB34A1A8C6BBB19474411EBA1B581AAD3DFFFD6F1BBCF892E8E9AE12CA6E8E94B5269831379F9D3E9C31EF150DE8510B419570501F7F8DD38744B73C9C7D43B65F9B3297B28ED73E47D7D4D14E0DD988BF5A11359574EE576C0FF4C944445341BFD926DF959466A9F567F1D10FEDC773D445C1F92520F7AFEAB047E
	1D8EF3BF5A28C227E7F37B7EA1BE37AD5F0FA55257CFC4BCCB2F576734C5FFF6DEBAC08E4E7CE5A6337B4F860A08E5ADC653831BFF5685DE079D1F8AFB9C456D4C67B84940AD5849F1CB2672FCB52DAEB40FAEE4318A7D49G9D5390373128C55735E81AF5FEB7C3950CC1582D5B25F19ABBDB71FB459DA40E463968F4EBF83142647EF58DFFBFB58746B31C0F464792E847CD633A6CA9663AA4C550A5E39CDDD24668F272C2687CEF5358A553636DF953394C6692AE33F13A354CD48B1987F4195D5343E52E2F45
	B56817A53F472B92A67727F42ED82FDC1B4E6363EFBFB147F3417CFDB64EA7DC871F2E571D694178506F7CE95E0983FC42F41ED3DFAE2A0467A5613ADE691C06151095DA6AG32D311FD22220DB0AC3D955377EADB5B433DDBABF8C83A568726E3E77A57E93E59E832BB437F1462B71B70AC67855AF84ECB1F81B5648CFE565F308BE3AF815A49G6451F8FB91C09DC077235C16FB0B2B498435E3376C6B008DCCB2EDB9ECFE2E7607FB67786DCC1EDB16FFE3B1D9B16D9729114E53030FB2FF1821BF133B8F186A
	6261E21C5F8A68DB8B309920E0A664EC000619FCFE2E62AAB6BFF407A6CD937A87698D9D1F5CA537B89CC4E331446616FF41BC374E197A1D277E8C4C9C9BC70CEEF73E19AEDA9F37C8FFAE0FC837E82719EE15BEAED1FFEE3220E37BEDB4EEEBFADC2EAE3A5F7D166553BD457C2C1EBEAB5E3E1772A0348E65FFCC46B2BC7A68EB770FF9FC6DCA7FE481E9F965972987A22CDD43AC7DBD692CE0CCA7DF5F1249F4FD358673162678C5A6E3F1D9F09EEFBE60BCAE9873081F456B106F6E8B6AAF9D8C725EDBF2481A
	37AAF8613C273887CC6CF57EDA89673F4560DFDA9E64E92F032D159CB94A7DF2B56DE37E77ECB14A334CD560FDED204307FD57610053AD388C1AEBA5E8AF230EE3A9CC6FF315603E6D763AB530057B963D026E0201DE9773230DED4470297FB07D3E57B651BB3F7F8BB099DF78234AE073FFE3D764FABAE517F5BD7D66AE6BFA1A940DFFC710D594795D5836A2733BB1366EAFD7605A9C0F0E37F782E8GB3G96C673DC749475EBFCABBFB68F1DF63A702C0CEF62B5AEBF2AB872DC2797CF7C7EBE26985FBF7449
	1AA6FB10BF47BFD90C6B2549BE0AE4388545E8A3456D9A5458D09D7FB40DADC99C20E8D6779F3C404F87AB2279D9200D56BF8950CEB4F04BEA7819A159409D4D6777EBF51F63383F7894F19D503EE2608E74F25AC18377E7BBB96D8A83F7C966348941F1B227DD7FB9FE27363F581CDF4B9F03BD6231715F2333B5BDDE0935D3A70C15GE2GE281D6822C85C8865882D070F83C3D9C2091209BE090608EC0B440A200650FF39FC859D391A99FGD2BF10D0C7D084B73A029EDBDDA17A2F82FE090F0F2FBF239BA9
	B153ED821A74187169181E2FD6C4763D57AAA6763D1FD4309A59A5F60F7874BB1B1FD560FD06BCB0200A060FEF2667FA06EBD5BBE5550D990D55147FC37D2C8874BD98436BFD016D9BA01C65E17A559DE1F56EDF5570BA375BE20E669A562A4E9D0C8956D5CC7EA754D7B540C7997257FD9AAA7F18A3A83F3E174B0F0BA05F6A1C4165470F111FC47DFD8D6093E3783EF369F0D05E5F48DCDE5EECEBF9D6E789AE2FECF6D09E0B1B93B4562BG1F114AF19FD0DC8B60EA8D5FB8D79959B7BE2C1C58B7FAAA2D73D2
	EF65583C54DFE91D175C15663C44F05ECA1C178BE2284798279A384B86AEC0F9A26EE337D39BB4707728DE08BFA722C00E1FC2759A26389E9D772889F7D947BDD6097E8DD39E1495CE7B79CAFABEF3CB92EBFF010EF1FB415D8DFBAC24BA3AFD02C96F278D5CE5E3FD2EDB2C0F9C32BE8C97D34247E59B3839A5FC9C669F66CB5F422BE93D0679206641EA2E7F2C61B55135595CB713701AD5677D5CFBC15E4FFF7AE03C07FAB96FF9866F17EA023C1FBF91645DA6BF986F37E44EFB0D41FBDDBDB1E2C5F8A70F98
	7595F58F06AF8D6487715CBD7877C8B5A1F74ACF445BCB1F381F5C6C6B8D39BB861A167B22CB771969FA00C61A000E79400EDA6E97BFB4F0C5355CAF769938693DFC5C3386AE3AF76C385DBEBE6EF183D766B30FE35FC61F3DF38A94DF7A8649D095129F32CE5FFD7A3D5793DE3D242691FBABA946EB0CAE28413132951D7856C57F84708C497649309DF6813E15242B741121EAA31258C18A87AE62E7B02AA6BA1BF8AD27EA64ED7A5D42E17C346470B9C19D2417B5A7BBC83A97BFD6A45BD937A4F8DD07D981FD
	B2001FA826B6DDC3EF20E6A3206F8E2AAFF2F198DCF6FCF35B21FDED9BD624915B9912A34902730C508F5BF8AB49568695D19D14A5E740773BC64AA8EB98AD02A75CB4E0375B18BECE560137600652C30127227313734F40E9A2201043F4A46EB48EE30433F9D0D0C0142834EA7871252C9CB47A9E790D764E8F4E6E3AE2A3297AA4EF10FB449670E944BEA0F85C52887CE0D121E20FDD83ED95B87560E79E20D9D918E63C4B9060787C6A6A39DFF97377D4C010D4A3FB19A9017430CB14BD071B86865CDE51E0G
	F68576FB8C76B84691B6E6FBE0765BED6B5E6A83BFE8A1E91E6AEA7A7F887DFF947F9F2118A394F364A898F7939977AF3CBF4067CC1D877459C6754165F71C0195547E6E0D550BFF5430E9A34C6E8C49209FAB40A29484C7DD70FB7AC4E55A5239FBF267A44D42B572212B8410EF8EE3CF46788FE25347BA8222330C6540D6AE9D1ACF7F97BC706410EABB25ADB4F4AA8E9833C42B3752ADA76966317A34FFFAA64DC652DB091DED4190D0380FC2C051ED3615244196CCE381D3DF0EAEA36333B743FD4C6AFB7EA0
	1AE69B97GFF9CE54BA3A82B11BC53DB31FA0DEC65AF414A70AD98A0F2702DD7993E762A7F6C2D41516034C27EBEFB1A1E78FF23E9E384CD799DDC452879DEFD735422086F496E4C573FBB1F9FFE2F5EC96F7992G1FBC1F1FCD970D62E1AA52DDD517C710A4CD10061A3CCE3C37BAB52209413B120F28BC150F51BB756F3F285E57CDB47F8BD0CB87883624558C9C93GG10B5GGD0CB818294G94G88G88G63F854AC3624558C9C93GG10B5GG8CGGGGGGGGGGGGGGGGGE2F5E9EC
	E4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGD693GGGG
**end of data**/
}
}

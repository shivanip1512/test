package com.cannontech.dbeditor.wizard.device;

import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;
 import com.cannontech.database.db.*;
 import com.cannontech.database.data.device.*;

 import com.cannontech.common.gui.util.DataInputPanel;
 
public class DeviceTapTerminalPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JLabel ivjPagerNumberLabel = null;
	private javax.swing.JTextField ivjPagerNumberTextField = null;
	private javax.swing.JCheckBox ivjPasswordCheckBox = null;
	private javax.swing.JLabel ivjPasswordLabel = null;
	private javax.swing.JTextField ivjPasswordTextField = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DeviceTapTerminalPanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getNameTextField()) 
		connEtoC1(e);
	if (e.getSource() == getPagerNumberTextField()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceNameAddressPanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.eitherTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (PasswordCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceTapTerminalPanel.passwordCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.passwordCheckBox_ItemStateChanged(arg1);
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
public void deviceNameAddressPanel_ComponentShown(java.awt.event.ComponentEvent componentEvent) {
	return;
}
/**
 * Comment
 */
public void deviceNameAddressPanel_ComponentShown1(java.awt.event.ComponentEvent componentEvent) {
	return;
}
/**
 * Comment
 */
public void eitherTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	fireInputUpdate();
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
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
			constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
			constraintsNameLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getNameLabel(), constraintsNameLabel);

			java.awt.GridBagConstraints constraintsPagerNumberLabel = new java.awt.GridBagConstraints();
			constraintsPagerNumberLabel.gridx = 0; constraintsPagerNumberLabel.gridy = 1;
			constraintsPagerNumberLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPagerNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
			getJPanel1().add(getPagerNumberLabel(), constraintsPagerNumberLabel);

			java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
			constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
			constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsNameTextField.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getNameTextField(), constraintsNameTextField);

			java.awt.GridBagConstraints constraintsPagerNumberTextField = new java.awt.GridBagConstraints();
			constraintsPagerNumberTextField.gridx = 1; constraintsPagerNumberTextField.gridy = 1;
			constraintsPagerNumberTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPagerNumberTextField.insets = new java.awt.Insets(5, 8, 5, 0);
			getJPanel1().add(getPagerNumberTextField(), constraintsPagerNumberTextField);

			java.awt.GridBagConstraints constraintsPasswordLabel = new java.awt.GridBagConstraints();
			constraintsPasswordLabel.gridx = 0; constraintsPasswordLabel.gridy = 3;
			constraintsPasswordLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPasswordLabel.insets = new java.awt.Insets(5, 0, 5, 0);
			getJPanel1().add(getPasswordLabel(), constraintsPasswordLabel);

			java.awt.GridBagConstraints constraintsPasswordTextField = new java.awt.GridBagConstraints();
			constraintsPasswordTextField.gridx = 1; constraintsPasswordTextField.gridy = 3;
			constraintsPasswordTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPasswordTextField.insets = new java.awt.Insets(5, 10, 5, 0);
			getJPanel1().add(getPasswordTextField(), constraintsPasswordTextField);

			java.awt.GridBagConstraints constraintsPasswordCheckBox = new java.awt.GridBagConstraints();
			constraintsPasswordCheckBox.gridx = 0; constraintsPasswordCheckBox.gridy = 2;
			constraintsPasswordCheckBox.gridwidth = 2;
			constraintsPasswordCheckBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPasswordCheckBox.insets = new java.awt.Insets(5, 0, 5, 0);
			getJPanel1().add(getPasswordCheckBox(), constraintsPasswordCheckBox);
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNameLabel() {
	if (ivjNameLabel == null) {
		try {
			ivjNameLabel = new javax.swing.JLabel();
			ivjNameLabel.setName("NameLabel");
			ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjNameLabel.setText("Device Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getNameTextField() {
	if (ivjNameTextField == null) {
		try {
			ivjNameTextField = new javax.swing.JTextField();
			ivjNameTextField.setName("NameTextField");
			ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjNameTextField.setColumns(20);
			// user code begin {1}
			ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNameTextField;
}
/**
 * Return the PhysicalAddressLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPagerNumberLabel() {
	if (ivjPagerNumberLabel == null) {
		try {
			ivjPagerNumberLabel = new javax.swing.JLabel();
			ivjPagerNumberLabel.setName("PagerNumberLabel");
			ivjPagerNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPagerNumberLabel.setText("Pager Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagerNumberLabel;
}
/**
 * Return the AddressTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPagerNumberTextField() {
	if (ivjPagerNumberTextField == null) {
		try {
			ivjPagerNumberTextField = new javax.swing.JTextField();
			ivjPagerNumberTextField.setName("PagerNumberTextField");
			ivjPagerNumberTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPagerNumberTextField.setColumns(20);
			// user code begin {1}
			ivjPagerNumberTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_PAGER_NUMBER_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPagerNumberTextField;
}
/**
 * Return the PasswordCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPasswordCheckBox() {
	if (ivjPasswordCheckBox == null) {
		try {
			ivjPasswordCheckBox = new javax.swing.JCheckBox();
			ivjPasswordCheckBox.setName("PasswordCheckBox");
			ivjPasswordCheckBox.setText("Password Required");
			ivjPasswordCheckBox.setActionCommand("PasswordCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordCheckBox;
}
/**
 * Return the PasswordLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPasswordLabel() {
	if (ivjPasswordLabel == null) {
		try {
			ivjPasswordLabel = new javax.swing.JLabel();
			ivjPasswordLabel.setName("PasswordLabel");
			ivjPasswordLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPasswordLabel.setText("Password:");
			ivjPasswordLabel.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordLabel;
}
/**
 * Return the PagerNumberTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPasswordTextField() {
	if (ivjPasswordTextField == null) {
		try {
			ivjPasswordTextField = new javax.swing.JTextField();
			ivjPasswordTextField.setName("PasswordTextField");
			ivjPasswordTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPasswordTextField.setText("None");
			ivjPasswordTextField.setEnabled(false);
			ivjPasswordTextField.setColumns(14);
			// user code begin {1}
			ivjPasswordTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_IED_PASSWORD_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPasswordTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	PagingTapTerminal tapTerm = (PagingTapTerminal)val;

	String nameString = getNameTextField().getText();
	tapTerm.setPAOName( nameString );

	String pagerNumber = getPagerNumberTextField().getText();
	tapTerm.getDeviceTapPagingSettings().setPagerNumber(pagerNumber);

	if( getPasswordCheckBox().isSelected() )
	{
		String password = getPasswordTextField().getText();
		tapTerm.getDeviceIED().setPassword(password);
	}
	else
		tapTerm.getDeviceIED().setPassword("None");

	//Tap Terminals cannot be slaves like some IED meters
	tapTerm.getDeviceIED().setSlaveAddress("Master");

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
	getNameTextField().addCaretListener(this);
	getPagerNumberTextField().addCaretListener(this);
	getPasswordCheckBox().addItemListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceNameAddressPanel");
		setLayout(new java.awt.GridLayout());
		setSize(350, 200);
		add(getJPanel1(), getJPanel1().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() {
	if( getNameTextField().getText() == null   ||
			getNameTextField().getText().length() < 1 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( getPagerNumberTextField().getText() == null 	||
			getPagerNumberTextField().getText().length() < 1 )
	{
		setErrorString("The Pager Number text field must be filled in");
		return false;
	}

	if( getPasswordCheckBox().isSelected() &&
			( getPasswordTextField().getText() == null ||
				getPasswordTextField().getText().length() < 1) )
	{
		setErrorString("The Password text field must be filled in");
		return false;
	}
	

	return true;
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPasswordCheckBox()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceNameAddressPanel aDeviceNameAddressPanel;
		aDeviceNameAddressPanel = new DeviceNameAddressPanel();
		frame.getContentPane().add("Center", aDeviceNameAddressPanel);
		frame.setSize(aDeviceNameAddressPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void passwordCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {

	if( getPasswordCheckBox().isSelected() )
	{
		getPasswordLabel().setEnabled(true);
		getPasswordTextField().setEnabled(true);
		getPasswordTextField().setText("");
	}
	else
	{
		getPasswordTextField().setText("None");
		getPasswordLabel().setEnabled(false);
		getPasswordTextField().setEnabled(false);
	}
	
	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G78F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8BD0D4D716A462A7EA22C948860DD3C3E2E60A59308919B8DBD4E22A8CF1B6A643CE1C2DFC180D15189A13B895A7CB6DA6D912E1E7EEB7AD2002B6CA94C7CCC87C8CA4AC02F1C6020E6127B60A099B89A188520F6E87B4B47D5A6E57F42388FB4E3D6F5EFE5DF403BA1B2AD92A8E2F6F3977FC6E39671EFB6EF95D246803047586E391A10675C47BBBECA0C4BDC6C849BFCCFB1C63E264549C227B7B
	9020106C8B1860B99F7ACA962766180969F49E346F05F67122541C9D70F99B59F03A7F4F70A10A1CBA687B3A7FF71FCCAE670DB6AEA71736F3BB72613999A091F078FCD3C6799FF494EB78B48D4F41E0A4E4D5AF0E296DA851F0B550BE8B908F10544B46AF076786AAF9FAED0DB62E7356847266D3FFB496A09F9DCF8EFB7B982DC5FB1612878C99FDA84BC675C41A98E827G606433C92062B3F89EBDBB58F74C268CD55AA417CBF129322D3F526F38A4F96C15F6F958E113EBEB1B6833D5F2374A1EA107CBF21E
	15DC3293E81D7D487F3E0EBC4A0F10C3504E2438CB23A8B399BE2FGD8AD7075D790FF54111A538A60F470F97E650FC735F934DFFE00E4BD6F4A5E92E51E318EB63FA4C7F01EF13EF7F3F752B529ECC75B2F053E7C01541CD200A6GBBC0AC40AE6A8BFF7A66E3F85677280D4A5010622AF73B9B1CF64F9945AEBB61F3EDAD74E8382FE41B62319342565B51D191C11F490051792F190E59A471D33E76C9DF7D102CFA6834B1B702CDD6A5D699978DB0DBAC99885A0449CE0EAA7B1901D05F8C157D8D514BDEB7C3
	49DE9FC17612E74B0DEBB5BED9BA5995033856EFD09FA8014F864DBF98BE13625DBABC7353980A47F68C742D99643E513E196B52E2DAC84C55AD916D901FDAEF34B8198ED6E738AEBF21FEB75749E4668ADD9E2378FBF5F8264BC90A4776D268AB9DCA4DE17EF5186E5BD8E8A7G24G2C8448G48F741B897773131EF8EDC250F1D15BCEA034F61344B9E42E88F37EF07279CD0BDD2334B2BCAAE1B5CA6F99CD20FD37692EDCCDDBB6A5693C2F71C7A3E8DE41C14DD32C7D29D0A8B38CBC332AAFB3CEC4C976D05
	B846A3535ED647108C82065C0473EDEB5F067BDA722ADF3B6D12AA87C7307E8B6D38A643D29BF0848670195945FE9E6DD58773EF876897F678A565F7DA76C040282DEDD114C11F3B95E7C2D2DCE867A7E86C98054FF1AEEEE3EBA7623220DD24F0BE66EF376A78B4B9EC283E649961FB6C4DD10C4DE58A7719B75A394FB4957C00D4BCFBAA224FD01FD2182F5815703D73BB4A9375E26D99236DD36C5BB2A5721E3A7AD8717DA9E5E4DFA77F4DE22DFE5EC57782681A8830C258787437254CE79BA42F4C3D74CB8F
	B818EC8FDA1C69794895FE2620BE6B057ECFD07CEA601967CE4D29F2F35B1D39529EB67EFA67905DF63DD406DE1F7FCB994CC6759DF8AE494332CBED125D324B8E9F3CDA1F2583FDCF5B373D0A207902625D304F9BE1E3F9853E35837722242A9EC70FCF154F492A6AF075857BBB29AC2F2F479D8C91DC162D83672878E11F0B717D9D38C7EC0A4BA5534592A4DA7FG5D2332D39E82259B951FCBE5F877F97473C1F9C40B916AF9CCFE06A5278F638D4B951225CDB8870FE388DCGC396D96A467D7B2248F5905A
	8107B94FFCF85E6B66FB513F076F453D176EA64B3A7799A31DA571BFE8B21AAE327DD3FCB1389719CEF2E7E45D87E92C5070DE266B179FE37CE4C94B97907D6A6DF60F6C75EAC98B5B83395D482F866434G58AF72BD70DAA77673B823B9FF2362F4B2138ADBBF53E5463511AF7AE4884F9850CE8B7B3D5035B52C2FCD33A56B7FD5975A52290B05B59E34653BD7F8CEB88A7A4481AC39486C192159030DFBF40C0F0B0576A200251EC863E2467938CCECFBE1BFFA59B827170DE3764A6D469CA1D29E5867F3D47A
	D487C6DCC98527F67BD4EAC51677D94E67E9BCD23CBC3FD3AF20DDB3201D2742F928F29F78C5814F6F628693486A45FF081A475628CC37CEB568832553F8FE77BFD61439887A12D43EE697AE684F18FA2FD731B9687126593B20DB6FA38C3735FBA3BCFB9D9E2F5A8C1BB8405726389B754573CFC3EB79401E31FCED2E4BC0EEA640FC9F6257FCDE24533D8AF06DG23G59145F6CB14C83EC12C7D6AB69FFF62C168DC843D220526B078DDFC923C9E5A3769E470F655A3A7C6747E8B35921764B1ED688ABA79C32
	535E5D286352B2991BA365ED4C7677EC2B5374CB3BFFA1B9BA3E4D98A9371E35E537B15D47ECCE0603369F7DF1CE763A8B3F4DE5F6F8D558218546448EF3BB3CD59B498ECB07399D4E0CDF0F9D184C9F0DF319AB01DF1E1F4B7C4C2217D967E7B27B7DDC261920CC07AA8FD5623FF3AA88EC6C17DCFD321D4A2D147C2AA634997AC3EC7F485EE3B4E6FB3DFE3881B4764B364186A5505D9C4E27A59AA330BEBF174E1C8F4F41C77D41BCE13EFFE24E40F25335F4AF5D6B8F4F1F7F1D621778437367CB97F87E3C9C
	7A4A82BC1FDAADE10C1E8B6DC400E5G69G19G45A3FC8FA5F4FFCA2648D71BA1A6F86072F7AE64C062F923956542992235BB2CE8C7FA0690EE077231F0BB9C9A891D7B5930B623BBBC016E7D30369AC64756F0DFD9890FEBBAB72B2DBDA5B693BB434A3B75E7585C91160BA643F3B9G0BF53B6A72B4FDD60D7018F66504BE26AD5DF25038A9CACCAB3A4474BBF4C9F7AF91F75659B654D3053E79G09G4B81568218AE43BADE66EB34467A172956083ADE2B5288B88B1CAF0D61390030693A6BDC0BF727DC8B
	4B65D0BAE63F0AA35CFE564BBCA7FCE07C7AF4F8F482DD388E7117A379433136DD117D01EFEFCD574B962EEBB270E17794BBBD6B56C2BBCB606A3B90D7BA1A1AD3B34A677446854C25BAC5BB1336B1AE31F64C054972FB243B5E7CB4E81F85134A78EB6E8F884C97FFADC54F03B32448F9705BD264BCB8CBAAE4F860213F8D73FED34F64F9F2FECF24BCF9B71D6B135B37909647E5AF5D9C1848E8FD2F296F118879B2260C1A1D36916B5886ED4CF1D5E92C963E749F654511FDC9179FC95DBCBFF20F727C68E08F
	4668C4E8AF83C897FED2DA2777690CB0BFB0DD89ED97DF61313DD046B330825A96GABC0GA07E8A0F997774CC99570F87A6096BF136686BFC17AD72BA5FED435C4E2B78BCB6B9FCAD93ED4522AF981D7054C43333B18FD2FAD572744993FCA10552333EE8740F5922798BDB53BD637AB5BDA876CD75917D9A240459BCAD2C3D7A8A4FB3DE8D6177296097E309D4FBFBD36313B7461EA5E3C13E0C5FE5FA7FF2CAA30ACF05DCF78C7D6636737C0C2A023685408D90BB464EAA66138F7A717C6975C8C3B05DF3EE59
	C68263C85B5E45E9574078CC00E24057G340F73B9BC5004BAB36F2D6677696EF07D170D87F5FDE6BCDA5E720F5447570E337CE40B407F93451BE302F8163754D0BC36EB20EFC98C0F2F076919DBF5C3EACE5D8D9CF7F7AE0F392D82373016567B31FD83777B1DF2F43FAD15A37B6DFEB9F2FC2A10A70FCF0764E87E4674339C6173C893BA2FB073F92C96B8E3831FC7550D9CF7A7BD0B1A205DAAF0D996CEEB95B8B20A38E4E82781B03BBEFC1E57E85281E71E467D64696E1D7A580559EBED6DC90F435E22391C
	F68E9A695D9A69AD53F87C2A22777098E84783A4CF6339D0E0BF4F0512FE381034441C0EF83FDB5DFA5038E29A731BF5530239D0A264C2CC66D89717D9968B730BC5DCD217BE4FC7FF3D9586D44213753F98520FEB3107687B4D97747D286F23C6FDFFC3C8BF5AE837C64F667633FDFCEED9BFCBA44E1FFC96756E1A984B661426BD4F366DA1FBE77FB6172D5195FA9E1826437E1B4E576D4792DF4B235379D930348773C1AB3483GF1G49GAB815681644F6063526838B2E8DBE630FA8156827A61732C996C3E
	4F469D6BF1EB7AA1BD57B5F97A35659AABA799EF557A62B41D03BAF6895985E1EBF0AD32B7CD52477319DCED6D36909EC707EC18FB75AAAEF5AD5A03D8E9BCC9044FCBG2CB39117125B2451D74C1CB83FEC922CD9A254419804BD9A3EF5A60BE586A17341DA544326B8FDC3AEAFE1F2D75078D40E32E1BC43DD24B1A1855A4BE7F2DDDEB2F3DDB2236812BD09AE7933023AB0991FB4F0394533F81C3F5F02B6D1A178154D42F71C5F4757F804495F564C65BBE7CD14EF506C3BDE139F83E390387C38D9613658B1
	0AEBAE51939A538F49E5C7FF25F21F9FA2041F134BA65C0F2782C677736B244B6D3CD6BAF6FE99E95DBCC1B7CF065FC471C9BABC0B3FFF6E6471B7857ACAEE62B95B43AEBC0FE3203D88E089C09A40BA003C597C7CFACA2EA2D364ED2D0A3B858EB8276EC8E2731B56F5FD6F9B9F4B65F1EE4943771014996F98225DE7CBE6B37FB82ABD57366E21F8B63FB3EE1C1F937AE681A481AC87C8874890735B5ADB4166F75C6ED06BD5D53275536A9C1FDC1BC3768751184CB01F0A6B1E189B5DA76E6F26B9E0E700A600
	76B92C9E94BB8747BCA971B16EB9117756B67277F634836611ADD28FFD3F2D612DD86BBDAB75415D40B754A3FBE8B76BFB7DDC01C6A30A8A026EDCDED1A81D98427A93F69750FED6E38A15F9E3E5212EEFA2EF0BE45675738BAC2B0BE55B7467D91AE6070CB93CEE765AF024FE53DC5E5F5E90293FD8744FED0CD46B2B00FE3637E77A0850B3AEF79FBCDD323FD4944ACEC0C0AE7B48AB3960CE66F174FED4D1DAD5D1DAFD28DC78E102DF7F1D071A0D07A33EAFC0281B4B5632D5FBAE1D39C74BDDFE1D4B6FDEF3
	216F832A8B645CA47847F4DD02A5DECD6E4B795C7FCB6E3B0BB4BD7F0EE1CF94394BB5F9C5B787F3183583D939EC4EAF55C2FAC18ED6F1998FDD8E5A6391733558034D45DC486752F2B33771F323C11E8D8DDC9E1B4753677576B3AF7C50E808B20FD8CD7F793AF9B019CD4DDC66721BA7335F31B17C4C62CEDED770F3216E73514E295EE97EB540E4FC7D1D4AE0B16BF1D774BB45D32E48F70A152E48F70A67DC1357B4DEF4C5FF77770ACB7F6E0F455C4D6338A733E02D0AEF01DC9260AC007D969EEF3D4AC704
	465B883176383D8F6F6278CD83D5FC8F409DFD2E7D6E293F1FF0510D6FD7FA94D5D50610BF477B5D38DE2A6226C8069BF5230DBC0E3EFE158D5570B73A515F1CF2AFC533B375A62B78AE934CAD6596FE2729360654FA03F71A86290FDDEB18DF7F424A6F97993770BB4D6FA94F32F910CF81344F63B55FFF13C47DE69E3F672C68525F85CABFB1C53C8BE4F69DB0A64E536E827302FB6B990891DA7D28B7CA7D089E83CCFE2D0D57A149FC2E537BFE0E2B9138F9B44F23FEA6F00FB7A28E4F6F4479BC6FF0537D3B
	9A5AD97339DF247970CBEFED520016E160F12CF828E7G722CC7F14B9EF504E169DBCCED3F3D112B4FB1CAEE05BB6B2D135FA7188EC9864CB11BE02C95A096E091C0B2408A00D5G99G265B609C87B884E0816884F08344G4481A45E4675FFA7D0BE097E1A83D06776C88EF4EFCD7F778A757AAF85FEABEF3B9A7D37E248557C738FD776C5138D3B6A94AC6B17BD8332CDED157A045C66E63D5CB5A0B3F38A39CC567671687BF17778547BF17FB8FD878A7B28F5442D5549BE9947DCCA696D754AE25FD70D1B85
	2E59FBD671BAB0FBE73A4F33DEDF3E7D3D6B24FB6E1A69D8BE6E15F0BF675F9E1E270FD2FC716D61F97AFA0947143D50B76BF6FEF7FDD3524BDF87F892F7B5BED2EE423DDD84E32BGDA811CGB1G71F15CEFD3474BA671DB963102EE4B74795C2657A7C953E51976FCA602EE0CEE0FDFCF37D69B1725BD4DF7C4230BEB50536D554655E84F96F8DA43E8995D0AC6BD1D2A0D1925BD9741B3D9C7377C8EEE939FED0BB311059BFEC690BC5F9CAEF0C7496938C4DF6F1F03012EBED6C7D91F077E1DFE870B538C37
	1162B2GD7AE7651B2C374FD7404E16AFD34529079DCFB4EB071DCFB419079DCFB5920BF57986ED57A0564BE08D76DE25C6B82772D40E5D01E087B46E1D77B857EED2A9762ADB28A946FD628DEFE0AEB57F086E390772D064BB1E2AC00A977AB9ECE3B49C86BA78E2713353750B18E970486E10FA21A1BBB5C12CE6F1D82772DD80B0A42096BD3DD28DF9F0633B673F18601131B79B87E9D4DC2B2A2917179C65D67CD52F71FEB6E7DCEE530B9EF6A61F640DAA54319FB389D307E49ECFD50945D1FBFB3CD6D4F8D
	26487EECB1CD7467B326487EFC4E2477E726EB1B0D573C737F0E57BA8F301AB7342B84AECE54410F8ADC2D1F534E953859031CB6C160DE9F6434C982F79F4D0532201DFFA7473D321D0E03F615401D5AA1648ADC954D1966C2BBC160DEEA647C1688DC67AE442D00F63A40BD360B73DBA3F08BCEF2FE0762B96EC50A33C03BB31E672E527EFCA2E29B5D568CDF92A88862793656EAFB5328FD5D40E3B41E5776CE18C3EBFB2974B78E2C36F71F28ADA645C72E6DC52BF3526FBC45075796EDF40D52G1F9E4F6F24
	2F648465991DDCDE5E0248722255B2D1DE490270DAE2EAC3E8ADB71D2EF3990CBB34004FBFEBFB687CE978577411F6F0FD6CD17449556993DE5B8CCC5067E4A30E17DD38196C1ACDAEB4723BC56C82DE674DEBC2DCBC346F95FAFAF7056AB93CA128E77AAE2EE7FA84BD334374DC37A0D4CF32B0FC1DD2CFF21D7297F279EF1D1CD8234E276F14703DC769C23E1E7F0257877E4E540C774788F58BBB78265227ABDA307B47AAB33FBBD589FEEF211BEBF5034EADC15E458357467BD3A7675DAFF81F5F9A64DD7EDE
	1077AF37DF9B6F5BF6F05E890237EFE7101F2F917BFAA527D7967212BE88765FD4FC6D751D8DA5DCE67A4249EAADCF176A66F5D22F0736C6962C83D59F6267544BE6FD9EE63EABB5E76FDD13672F0C2E4629272B811A162B223BF3071E4E8AB46AD551EDAE51534D821AC5D35031D81AE966E764838277DFE6FEC6FEA8F0DFBA793817852E55B9F15C7DBBA6669E8F6D50679E8C372FE462380FCB746358EF4E767B5EC66F788DD92F12C2DE2AAD7DCA3E68F3F8E43BCA4C6165DB2F079404384DCF6F7E532BE9AF
	36F933C9CEB35909516F9C3890A640329D3F48227D8A63845960D6601AF08E704D4464250F75DEF544A9371022DE14A8FBD559DE4FD3EB2FCA5E235FDCBC005F6EBDE0113C7DAAC632A3AD4464425F0B102D2C5BA9397A8E30BC7BC8G5F64996A2D54A3F19588683B0D6A0BDCAA8517ED0FEC59776129B5A95944F802ECF4AA127D04E4035BCDB3592076FBE4EF3F6234875C1F57D1C6F9FEDE284886F63B4474F13215D87796CA8F863EB737E0ED4189F07200079C20A3B108D60A91E0C8498322E4CF330A3FFF
	D1BCFBC55F8D6F2B7B7F79640ECEA3B1E8133CC84608A960D6C9CE2FB464F00E28E4035824DE6C4C838C64CEAEEF2F62998265CA431463DDC2C6656DCFD65D7123F7BB280CA029CABEE056845283FD32B2F4203E375761128583B0AD305FAD5863184A30B1DB83373EF76A595F7700ABB4116C212AAA7A3F1A7EBF087F2BA9261AE22A8F02FDB34824FF617D81BEE76ABF204F962A8FFAC06589D8C475DFFF7364BDAF9C4DD88733BBC156532FF642BA18032358CB0E99C9F76C5E9437FA8EAE139B3DA500FC7383
	02A34E3019DEA2D68FFFDA1B7D2B4559C6E2EAA6B9AC89857F45CC927C17A61A4DA49B12D06AFA9870F8D0DCCF15B5CEF0C84466CC74ACC4EF542FAFA28A6868AD747F56EB3696D295D15A6DE2123B198DF7C6D416E3799F8405FC769B05D2FCFDD42B12827A2A281419915A9BF8FC004F26AF9417FCDD134EE1A75DF7B36372600CC37E7E76B42D7AFF236961C0E4FF791BF479E3214AEE644A72F7D165637AF7D119530B233E4B33AEE6E73E3AB8D8AFE7753412917A5DA64027AD6635A2E7603FC9745A6B39A1
	4969D4A567E03D4B0EF5580691D58E3EDB79F08479FB79982D1375150D205E83BA1A7F85D0CB87881D0CD6EC5A95GG1CBEGGD0CB818294G94G88G88G78F854AC1D0CD6EC5A95GG1CBEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9496GGGG
**end of data**/
}
}

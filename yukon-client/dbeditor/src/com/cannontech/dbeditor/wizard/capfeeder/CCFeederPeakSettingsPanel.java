package com.cannontech.dbeditor.wizard.capfeeder;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;
import com.cannontech.database.data.capcontrol.CapControlFeeder;
import com.cannontech.common.gui.util.DataInputPanel;
 
public class CCFeederPeakSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjOffPeakSetPointLabel = null;
	private javax.swing.JTextField ivjOffPeakSetPointTextField = null;
	private javax.swing.JLabel ivjPeakSetPointLabel = null;
	private javax.swing.JTextField ivjPeakSetPointTextField = null;
	private javax.swing.JLabel ivjBandwidthLabel = null;
	private javax.swing.JTextField ivjBandwidthTextField = null;
	private javax.swing.JPanel ivjJPanelPoint = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CCFeederPeakSettingsPanel() {
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
	if (e.getSource() == getPeakSetPointTextField()) 
		connEtoC1(e);
	if (e.getSource() == getOffPeakSetPointTextField()) 
		connEtoC2(e);
	if (e.getSource() == getBandwidthTextField()) 
		connEtoC6(e);
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
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DistrictNameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyNamePanel.eitherTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (BandwidthTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> CapControlStrategyPeakSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.fireInputUpdate();
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the BandwidthLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBandwidthLabel() {
	if (ivjBandwidthLabel == null) {
		try {
			ivjBandwidthLabel = new javax.swing.JLabel();
			ivjBandwidthLabel.setName("BandwidthLabel");
			ivjBandwidthLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBandwidthLabel.setText("Bandwidth:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBandwidthLabel;
}
/**
 * Return the BandwidthTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getBandwidthTextField() {
	if (ivjBandwidthTextField == null) {
		try {
			ivjBandwidthTextField = new javax.swing.JTextField();
			ivjBandwidthTextField.setName("BandwidthTextField");
			ivjBandwidthTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjBandwidthTextField.setColumns(6);
			// user code begin {1}
			
			ivjBandwidthTextField.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 99999999) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBandwidthTextField;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPoint() {
	if (ivjJPanelPoint == null) {
		try {
			ivjJPanelPoint = new javax.swing.JPanel();
			ivjJPanelPoint.setName("JPanelPoint");
			ivjJPanelPoint.setPreferredSize(new java.awt.Dimension(196, 100));
			ivjJPanelPoint.setLayout(new java.awt.GridBagLayout());
			ivjJPanelPoint.setMinimumSize(new java.awt.Dimension(196, 100));

			java.awt.GridBagConstraints constraintsPeakSetPointLabel = new java.awt.GridBagConstraints();
			constraintsPeakSetPointLabel.gridx = 1; constraintsPeakSetPointLabel.gridy = 1;
			constraintsPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakSetPointLabel.ipadx = 51;
			constraintsPeakSetPointLabel.insets = new java.awt.Insets(11, 16, 5, 4);
			getJPanelPoint().add(getPeakSetPointLabel(), constraintsPeakSetPointLabel);

			java.awt.GridBagConstraints constraintsPeakSetPointTextField = new java.awt.GridBagConstraints();
			constraintsPeakSetPointTextField.gridx = 2; constraintsPeakSetPointTextField.gridy = 1;
			constraintsPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPeakSetPointTextField.weightx = 1.0;
			constraintsPeakSetPointTextField.ipadx = 115;
			constraintsPeakSetPointTextField.insets = new java.awt.Insets(9, 6, 3, 25);
			getJPanelPoint().add(getPeakSetPointTextField(), constraintsPeakSetPointTextField);

			java.awt.GridBagConstraints constraintsOffPeakSetPointLabel = new java.awt.GridBagConstraints();
			constraintsOffPeakSetPointLabel.gridx = 1; constraintsOffPeakSetPointLabel.gridy = 2;
			constraintsOffPeakSetPointLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOffPeakSetPointLabel.insets = new java.awt.Insets(5, 16, 6, 32);
			getJPanelPoint().add(getOffPeakSetPointLabel(), constraintsOffPeakSetPointLabel);

			java.awt.GridBagConstraints constraintsOffPeakSetPointTextField = new java.awt.GridBagConstraints();
			constraintsOffPeakSetPointTextField.gridx = 2; constraintsOffPeakSetPointTextField.gridy = 2;
			constraintsOffPeakSetPointTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsOffPeakSetPointTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOffPeakSetPointTextField.weightx = 1.0;
			constraintsOffPeakSetPointTextField.ipadx = 115;
			constraintsOffPeakSetPointTextField.insets = new java.awt.Insets(3, 6, 4, 25);
			getJPanelPoint().add(getOffPeakSetPointTextField(), constraintsOffPeakSetPointTextField);

			java.awt.GridBagConstraints constraintsBandwidthLabel = new java.awt.GridBagConstraints();
			constraintsBandwidthLabel.gridx = 1; constraintsBandwidthLabel.gridy = 3;
			constraintsBandwidthLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBandwidthLabel.ipadx = 63;
			constraintsBandwidthLabel.insets = new java.awt.Insets(9, 15, 36, 21);
			getJPanelPoint().add(getBandwidthLabel(), constraintsBandwidthLabel);

			java.awt.GridBagConstraints constraintsBandwidthTextField = new java.awt.GridBagConstraints();
			constraintsBandwidthTextField.gridx = 2; constraintsBandwidthTextField.gridy = 3;
			constraintsBandwidthTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsBandwidthTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBandwidthTextField.weightx = 1.0;
			constraintsBandwidthTextField.ipadx = 115;
			constraintsBandwidthTextField.insets = new java.awt.Insets(5, 5, 36, 26);
			getJPanelPoint().add(getBandwidthTextField(), constraintsBandwidthTextField);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPoint;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the OffPeakSetPointLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOffPeakSetPointLabel() {
	if (ivjOffPeakSetPointLabel == null) {
		try {
			ivjOffPeakSetPointLabel = new javax.swing.JLabel();
			ivjOffPeakSetPointLabel.setName("OffPeakSetPointLabel");
			ivjOffPeakSetPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjOffPeakSetPointLabel.setText("Off Peak Set Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffPeakSetPointLabel;
}
/**
 * Return the OffPeakSetPointTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOffPeakSetPointTextField() {
	if (ivjOffPeakSetPointTextField == null) {
		try {
			ivjOffPeakSetPointTextField = new javax.swing.JTextField();
			ivjOffPeakSetPointTextField.setName("OffPeakSetPointTextField");
			ivjOffPeakSetPointTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjOffPeakSetPointTextField.setColumns(6);
			// user code begin {1}
			
			ivjOffPeakSetPointTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.999, 999999.999, 3) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffPeakSetPointTextField;
}
/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPeakSetPointLabel() {
	if (ivjPeakSetPointLabel == null) {
		try {
			ivjPeakSetPointLabel = new javax.swing.JLabel();
			ivjPeakSetPointLabel.setName("PeakSetPointLabel");
			ivjPeakSetPointLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPeakSetPointLabel.setText("Peak Set Point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakSetPointLabel;
}
/**
 * Return the NameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getPeakSetPointTextField() {
	if (ivjPeakSetPointTextField == null) {
		try {
			ivjPeakSetPointTextField = new javax.swing.JTextField();
			ivjPeakSetPointTextField.setName("PeakSetPointTextField");
			ivjPeakSetPointTextField.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjPeakSetPointTextField.setColumns(6);
			// user code begin {1}

			ivjPeakSetPointTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.999, 999999.999, 3) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPeakSetPointTextField;
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
public Object getValue(Object val) 
{
	CapControlFeeder subFeeder = (CapControlFeeder)val;

	if( getPeakSetPointTextField().getText() == null || getPeakSetPointTextField().getText().length() < 1 )
	{
		subFeeder.getCapControlFeeder().setPeakSetPoint( new Double(0.0) );
	}
	else
		subFeeder.getCapControlFeeder().setPeakSetPoint( new Double(getPeakSetPointTextField().getText()) );

	if( getBandwidthTextField().getText() == null || getBandwidthTextField().getText().length() < 1 )
	{
		subFeeder.getCapControlFeeder().setOffPeakSetPoint( new Double(0.0) );
	}
	else
		subFeeder.getCapControlFeeder().setOffPeakSetPoint( new Double(getOffPeakSetPointTextField().getText()) );


	if( getOffPeakSetPointTextField().getText() == null || getOffPeakSetPointTextField().getText().length() < 1 )
	{
		subFeeder.getCapControlFeeder().setBandwidth( new Integer(0) );
	}
	else
		subFeeder.getCapControlFeeder().setBandwidth( new Integer(getBandwidthTextField().getText()) );	

	
	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPeakSetPointTextField().addCaretListener(this);
	getOffPeakSetPointTextField().addCaretListener(this);
	getBandwidthTextField().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CCFeederPeakSettingsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(330, 213);

		java.awt.GridBagConstraints constraintsJPanelPoint = new java.awt.GridBagConstraints();
		constraintsJPanelPoint.gridx = 1; constraintsJPanelPoint.gridy = 1;
		constraintsJPanelPoint.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelPoint.weightx = 1.0;
		constraintsJPanelPoint.weighty = 1.0;
		constraintsJPanelPoint.ipadx = 122;
		constraintsJPanelPoint.ipady = 29;
		constraintsJPanelPoint.insets = new java.awt.Insets(7, 7, 77, 5);
		add(getJPanelPoint(), constraintsJPanelPoint);
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
public boolean isInputValid() 
{
	return true;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) 
{
	if( val != null )
	{
		CapControlFeeder subFeeder = (CapControlFeeder)val;
		
		getPeakSetPointTextField().setText( 
			subFeeder.getCapControlFeeder().getPeakSetPoint().toString() ); 

		getOffPeakSetPointTextField().setText( 
			subFeeder.getCapControlFeeder().getOffPeakSetPoint().toString() );

		getBandwidthTextField().setText( 
			subFeeder.getCapControlFeeder().getBandwidth().toString() );
	}

	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G74F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8BD0D4D716447841CF22A6D8639AD3EB8CC91111CA1C4459359C264AE15CA95DB1D9D32593F7465DA53B49442AE5324E044D1009D5F3E93041DFE879E80398711740AC03E00CA0B209FC64A7D18456102CEBF8522F21353BDF737A810DE05CF36EFD77766B269B44EC2AC255613DFB4E3D671CFB6E39671CFB7B111C43CBF62418F288C959C1743F0A94C252AA8949D8E4F90C63A264F8B3B17C9D81
	58C7DE4ECA0367BC2045AE0EB7DB081212816DE8E8CFDD90EFBEG6F59C4CB59F892DE42480985DA496F663EBE3E1C537B381CF45A9E3934871E6F8144G8E1FC9B24AFF7412D5472F57719CD2CC04A458314F33178AF4DCA5345BG9683AC37337EEB613913CA1ED6D529773BB6FF89C97A7B8FCDFB110F01A70722DEB636D5FF961267D212FAD156ABD4CF9CB3955AF1GB879D4F22E7FD3F8D6375D6A7D24C7F11576C8EE376256641E3E52A147EDC9358146E317E51B2CD6D55555B55037B6D93A55A1EB1A43
	5D6BED135C3213F87AD046614B9914A74C875A5BA9CEEBC439AB60FDBD40EB82FF2E8971679D7166AEG0F034F75AF9FD66BF3699AF90EECFF451D1A99E62E118EB647658E7FDC23867E101EC757C54BC27BA7824DF2B35EFC9C2089208FA09260A0750711FDFF02E74D8D2DCEF13994F70D47D36B34291FA9B659896FD5D5C051F16DF20F225A88E1EB7ECF17CAC268B39E30F17FBE69F14CA6B19F71750FEDFF1AECF96122A9BD04CD36441419165CE436D8F953EF93A67B7865F032B75F8C744FC0591F93236C
	4469CA6A0E9032D7ECAAB6A56AFC48AD3F6C32DB3856DB298F9442FB0A6E9F8C7FBB0A578CF8662B37A99E5BD301367596770D2E7738AE2DE90B08253CB5249D96465718DA1DCC871BB3D81767285F4DF3B21969C2179FD1FC0C814FF42921F8EC2F865AF1D73C1979571D9C5C3B11500E86088358881084E0F1C3BFB77731B57BCE5E230F35C92AD6BB60F04286A3EC6C1D2C7D7014FD1AAAB53B3D1A646E112FCA2AC33A61143DC46FB3BD8BF53B91B0EEB6757D9E1051A83BE5D5529C0A9B38CBAED913D5AF6B
	9315358F7B28B225F6BADCB288F0F988673BB8AB9B1ECE492B7D3747A6E9323F8723C7E7611A8CCAD701A3B000F7E6176F67223D6AE17E57G1C428E0733115FC5D905F0D1D5552AA83786BC1DB8939267267100460E88F8DF6866B6FEFABF620892EF36AA1C4F556CAC831FFAC78F2AAF2943FC0F3D59087139C461BE73E69777197A3DCF1112CDADA1FD8621D3E13E62D40277CEB26509FA31F6C4E357A476F00992FAFF0D9FAB7EEAD2B2D2A75177BD31D6B9D6DC2BA850EFB94086E1637959054CE7EBA52F4C
	3D74328A8EA65B7C96E7FA7E3009679554E137507FA7943F95F8DABC7166CA8F37DDE3D317213FD1275D131CC3F85BF5D39948FFC7887AB71541ECB4A3877312BCA83B35FA59A33BED706255E98F67206F697B562E08B1D1946F01FDDE879B4BAB700BF2F0AFCA1A26BAEE8CE8B2CF2D023E144A728E5C7078C3841775CC8E4ED19902FDAE7ABF1B03FB24C7F13BE53AD8E208CEDFCE77286C14DD20F41DB26056987EEFF2514FEF4943FA0CD8150B855020649C40F8436A0565922E13C7F5384085B0E411B59E5C
	3F6D172D869BF5838E6B1E05700C75703DB8F40C6F4563371FA02B3FBAE18A15CB96BED5EF5A532F6F41FE7FDEE4BAAD5B9FDA779F50D821633D29EC1EA56867612B963E8FBE4B233588486A84F07673FD706783C86731C65FGF50A53494CAA6C1DEF35607A487D83B204E88CEA97058D0FD83302E8D7F5FBB2FA31956D69B444C32B0A76CCEA623561D450E7B1C0DCBF33E932EE93566FEF1BF9BF9B34C755F07D86DBF83F4550DE8130D18D6C4756B77DAB2C93C255033D830E52814D01D1D7524031BD839AB5
	A32B7D62E87CC69E96AF2F71FE510EF6ED02F6B7004F4B7D60653D3C460B3A3504A4BC199F361E1D67E53A2D707A7D2070C1DE63FDAF9FE5A681ADCD63EB96EDB566199A2FD769F15094275B3BA34F68A78C5715378B1EF6076A551AE1A37B785ADC4FC3FDB1876AE83DA6B8543CC71F2B95643681EC55907FD4ED0EC177C840AD875888407CF1EAB35682BD12AAEB2574BFCB2DC5B725C149D76A9D824FAC259125348E29977035D8DF17B545D9CCC719DADE9FD9FC1557E9CB334F6BF257BF3B08D45F4DB6052A
	1BA3B38F194A8618AD3B867CB69DFD5E1C1AF87D5DF4B63F84B13FE86833E5004F2F3EAE547CF286216E21637E3A050FAB82DC77A09F57FFA1543805035CAE660B77E397BD4F0C29EBE3877D79F67560585C4B6A3D168E54F563E0F0CD5AC9712F8D865724DB5AF9CD1A84342EA1DE23DC4A453897876D4DG5BGB2FC7166A200A69F7749DF67FDC4A628811BE10F29EE4959919064F9AD160FF2A1AE6B6D9FE7230DE9DCA6BC679DEB8E36034D97B8770820B69BD7BEE6DCE9D0BBBAE89C734793C585BCCE9816
	292A2A259372DE03C3F65AD8DE185DE14C8BF1BED65FA540B38900450EAD35993ABEFB06F90C3853E00C91F11927CC3B4344087AE13D569F767BF309B887A66FC7BD2301369AE0B3C0920075F63C79B4C057ED3EC66DB9FF19E80D285FF5AAF500EB02F8DD971CDF05CDFFFB1FEB7147895742F7BBF09C335FC89D375F3C5B3C4EFA2A657EF4F8E14C38E09D560654A1668257A181682C265FCCF302F5A45EDCB242F1CFDEC1DC9B347BC6382EED9DD8F74C916DAFE99BE384EB7FEB7BE4EA61D0003C4351180CF7
	3F310C71EAF066E31F67062F992F67062E99ED3921EB461B397B989EF898CF0E1C2E660EDFD35E4E8DD5D39E2273D837BF137276FAE5AFF5FACC78BA6D8DAD170428ADAF4161D237C1B67932F9275E6702265415939EB35EAA46D890A2E6986A081FF620FF60786891DEC7AC580731F78B3413G760CF23FF83556682B47C7835738B2285DBA4AE376DDAB6623EEE80F82C48144822C9F65314032FF42F8FD41B7CE3CB69D883F4EBB8F04DE674C83D883F9158135C78EDE4B7D872C02660FBA4D1A6CC2336B67EB
	BADE1354DEF90CAFFCC047B3DA3871A78F0473973626C5AD46B5BDA576CDC41DF18D9203ECCE6E8436F36E701C3FA5005F99414F5BE464D794B43EBA285DA6787D47E5BC2BB825E1E5C0A3E4439D749BF39B4FBDB6E80F82C4832C3CE32CF7FFB004F9452ECAAE18EE07C76EA131DF6358D5D778D8AB34CBGDA811CGF33E9675DCA60EE55E5B26B85C1ABE07359DFC8EC9DFB3D999F77DFA9740FB595D40F9305AE3EF963DBB394BEA0F3D82DFC071FD86BC2BC99E237B825BF30016FC174758E41ACFED9171E6
	AD02634CE9BC6EC68A5C1CCA44ED06F6E284777D9956F14E3556B067DAEB18F32DF57C9835549A4E6718FE5F23F90DBCG6F8FF01DD32DFC9E4785CE227DB4E8C78A5C2CA644AD06F62C40ADA860E3D78BDCDEA362ACD3005F94DE47163671BB0DCA40F926703B72726E001A27D49A522A2A9AD5072DD66AED251E47E258E0DB06BEBEE68A0FE15114E782341381AC8F72BA4777812FF3E21FDEC4DAA3AE06BC8BA5F41EB295BD285F6DBF682FF3E2204EE1B2932EF01911C0DF7DA0625EEBB7565D68334BD2D8BE
	E7747381F4DC0BE344C8D78368286F8213119E9DC0C79B9D524733397D7C841F5B761F4790678F4F06BD67E5CCE5F3AA511FEDD70F1163335ECDE7EB74AFB4A7580096B1152F1B299FF1D1500E91B8C5656B3BDE60B635A32EE09A54FB53F85E48267723CD506E831883908B3081A0D174B9CA7B6C194E4E5EA55359593AF5BABBBB33BECF6C6B8EB117D000326E29CE51F55A206B6457A76E1EE55D6BDDE03A3EE63B75755ACBF82C0F273F2559615C9FBB9DEDF8046EAFA73C7BG0E4F20E760347AF1F86F50F9
	F158866352E05CB11DE60577B7087F9EB01BBCD9097A74A84E81175BCB185CD5B4BE21DE4E99BC66DEB99B285BEA2A5BA850A3E7F05D722D9369E654AFEEC620EE9B4668662B9B2B5B5CBA2E5B96215B8775013A3DC0E3D0A2501305EE3185936916EE502DA4B2D0377311413A15B50E552D31116B569A497D67F45B7D7DE6D5FB1F63C6A6BD0E653A41AB98A72FC532DC17A270F7A93E4F00E7317A7BA2D6EBC05B9C49EBBCB91F561AB3610C87608108840881D8B313673A4A3CB2B2C11D5729F8DAA199BA8D69
	0B4D6F75AB7777DB5ECF52F9CCDC31EAA9D9B97D1D14F0675A4DB3D94CC8561FDBBB0FD13CFE2EBD0873AB1A95EF2E87688308G0882081D45679711D74266F7416650EAB4CD6A692337DEFCF2D79D72109F0D458F732937BB50F7BC2AEC17D5D536F5B8EEE3ED4FFCAB3393FDEBB548D8BF0B7BFDDE7F585A2B2C1F57CF4933D86DE5198DFBE0B61B8B5B8B53557176C2EA90D44358B600DC3DED03777F0467B93197925B5116D048B9DC83AE2AB553B90D668F8F749F1D4DF5CEBB0A79CE3F932645DB2BF4035E
	89B37ACCA5B708AEAEB6C41F76C3187FAE5B6DE358B07A1F722CE369028D6B3369FD1CEB2D64368DB9ECDA1F2E84233D79FE1611E6D0005D2BF5E59973EF4CECF62F36FEB63FF7FB23B294BDC150636AC251136019B1873FFD30FDC844DC5FC9BD81CF37BCD448CB1F528688F4C5C73D129B4E092A43FE3424302C24307CF4317075A5C3466F96AAF7D5043C6FC7E81D437C22DBFF464DB8C68ED339D09B907E376D5331B8F8273A3ED4C4045CD77770BDD6306CB1D27F4ABBA9474248DD284B5BBA47DFD3ED3D39
	BD1D4D7965CA9CF3C648F8663F7C76F87B40E46C91EAAE0C677A92BF4F276B826539CE7AE911857FBF72FED56667F93D114BE3B1ED748A916F538C6F4B5B3E695D484479E25D37AA0345C3F7FE78334DC0FE68334DC8FE68334D8379635F2F4C4C8F7F1B5D3CFC63EFF6AC1EAFAB473DD878D03C399A209BC08308FC084772778F9EA5B41607086397EC3DF8AF00DF88E8DCFECAC17839E694CC7CDD413E827CCD640622E90A8B79F37C0182DCAFCD71D0A4439594200DD4C7EF1F463A6A7823743C65146D944D7C
	6F130F7C7E77FBCFE130CF57BBE0E678C1C1D17959B3604DE5E545AC4FF77573BC9F8DB65972903FBBDE524F4FBDB98F733356CB34EFAB34EDG23G7796CB789E11CEE30B3DCF7D582F2FEF5F3D682B5FF559435CF551884FF41F1B456FCC239E6673F97FAC472D973837447DEE5ADCFEEE7C077FB146D64B22A3A6DE97857BFE75DC965B1A667AE39C733941B334F6867C42395C4F1E7990035CD56926DE8DE16920285453G5926AA9ED9550699167E12297B6F7395184BD7821F0DG49G16F9106F812A815A
	GECGBE00F9GB1GF1G9BGDE83C88248189FEFAE184F7548AEAE9EC78F7D50CD1DDE157066057B5C8B65617DFED57944FE7F53F27A7BA078EB6730C73F9BDBDB0E750DE237FBE5313FDEAC3788DC3337CD71BA70CE0D45326B7D77D7B3BB6FF35CD3139E47EA6ADA9577C9597C60DA7BA245D74F8F2E35B70ABD58843458797CEC9115E514EFF9C42F7174E765A3E34F3E7AEFD6E70D63DA75FE5D7AD38BBBAEB9604EB1D26F37D8FFAE8FB10E792875AC91FE32766393617C8BA2EA8BEC594BB7EE4ABDDA27
	54AB460C1CC1DEA266B37757E9D34EE07C0652D3F3C8CE47ED7A13F387F4F477B27A4FAA427B650B95937B65AFAAC24763AD95E363712B952163719B9546F84CF00995982FFAE1BFF509FE3F9538AF846EED4A93F11FE3ADAA705B29DE08EF12D1A0471B68F9FE08623AF4DC3A8177050E5BD309FB8B265C2728A277D452F3344369E46DDC5A4761062DA66C11DF09BABABC12C16F438277057015A6CC317A7B95437BAF7B3F7D1A662DEFD5863B5FDA16456FFA57BC426F3C0EF8683DAE34378ADCE2BF3F9BCB92
	382A337CFE357ED10EF3D89057856DBE01FB494247EA8217D94F6FD75788DCAA7D36E2B334378ADCDF1E108BB8F69E5BCCF52E0F023C90454FE3159EDCF38DB6814B3B3EAAD40D1D93D3A7F5CECCB35C67E02C0AG993D7ABB135FCB67BD87708B057C88CB207C22CF7C72DF37CCA4FF47B87257C67963A813DFCDED37917089C2FEFEDE207C0B1F7A655F4D1BC8FE7AB872AD8B624DF775F7467B799AC2F8CD136B413AB244394286F1CC69B59CAA5858CD35C470F976021F4F7FDAA647676586BF1F9FB77B797C
	5EBAB9BE89ADB816FD5F3FAE930B53585F109DDE156C8D987FF7CFFC7C6B757FFC551BCA4C4DA48F2F68BA604C0411DA36612F1B7AE72C8DE427878F69F8F950CC523C7431432B8DBB65D612E3EF474F383C1AEC2B61B9402B115CD614F492BF0DBA59A4F97BB40C5B67DAC91A9BBF38A5D90C6C145C3DA7D9C2B867434FECD3EA3AD1F33A0484744D267AA217D241A57BC719A70E343C36B215189A48AE27A2599A249EC8D34DE42756274A5EBE45E973F9BEBDCF99E58C21D7618DC8AA30BBA8260F13AD453B83
	2174AD5F63F376EE5D5BE002ED26121334A7FADE2968E12B6B13D490A52B4D9AFEC02C2847856D0177358FDEECBCF04DC4D274C976132F094967510859AE399C4EE10D6C9427D2AF924D00016466765A9555854A9586A947C9C2C669236B4A96BC7307AF298C7FD00D9CE6560421A7FBE545F53246EEF738E541GCC8B6C8F89765827B428CF16EFFEEE4B26BF5EGD728A7292E32B27A3F1C7EBF057F4BA9261CE24ACF01FD3711F17F0269BEBEE76ABF20CFA65587BD20348196D17B65EF56AD7DD57536C418DD
	8359C13F6301F53078FBDD9AF05D105569318B8E6D0ECA180D4B64C1EF71A15FDDBE4191E758CC33E24260C7D5297F76E42A0924B593B32B9240FFB155037F52CA201924C215C0DD8FF79AE6D42C0AF6D2E5499807C4EC5AD84FC2F406F1FD91311B765ECB7FDBA6EDAD9C15C5476608C99EE4B644ED1DE6D0B675BB296CD13F3247894B85797804F54A9AFBD02CF9789FDF097D2ABE47C4CC30D4FDB2DB1B9D74060F2E8E5EAF2E919CBFA37EDFEA52A976C35EC7A319F831D858A1EB0574EA31CAA359176D7642
	B158DD628E316A3EACE8E641775BB1DF195FFC7473D14256FC77F7CF538A3F26E6DD53C2547A3B2769A701AE09C78F3C4E5E496E8E0A6F9A6F8E1227D9435E4BEEDB4C4ED2E90F875F9D2C4D46891D877C354779195D1A7DE7927ECC5561121CCECDF25E2AF15B70FCD5BB2C497E7B329733113F177751090C36B19B753EE99873FFD0CB878868C7CC0B6093GGACB8GGD0CB818294G94G88G88G74F854AC68C7CC0B6093GGACB8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0
	E4E1F4E1D0CB8586GGGG81G81GBAGGG9A94GGGG
**end of data**/
}
}

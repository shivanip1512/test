package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

public class LMProgramTypePanel extends com.cannontech.common.gui.util.DataInputPanel 
{
	javax.swing.ButtonGroup buttonGroup = new javax.swing.ButtonGroup();
	private javax.swing.JRadioButton ivjJRadioButtonCurtailment = null;
	private javax.swing.JRadioButton ivjJRadioButtonDirectControl = null;
	private javax.swing.JRadioButton ivjJRadioButtonEnergyExchange = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramTypePanel() {
	super();
	initialize();
}
/**
 * Return the JRadioButtonCurtailment property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonCurtailment() {
	if (ivjJRadioButtonCurtailment == null) {
		try {
			ivjJRadioButtonCurtailment = new javax.swing.JRadioButton();
			ivjJRadioButtonCurtailment.setName("JRadioButtonCurtailment");
			ivjJRadioButtonCurtailment.setMnemonic('c');
			ivjJRadioButtonCurtailment.setText("Curtailment");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonCurtailment;
}
/**
 * Return the JRadioButtonDirectControl property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonDirectControl() {
	if (ivjJRadioButtonDirectControl == null) {
		try {
			ivjJRadioButtonDirectControl = new javax.swing.JRadioButton();
			ivjJRadioButtonDirectControl.setName("JRadioButtonDirectControl");
			ivjJRadioButtonDirectControl.setMnemonic('d');
			ivjJRadioButtonDirectControl.setText("Direct Control");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonDirectControl;
}
/**
 * Return the JRadioButtonEnergyExchange property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonEnergyExchange() {
	if (ivjJRadioButtonEnergyExchange == null) {
		try {
			ivjJRadioButtonEnergyExchange = new javax.swing.JRadioButton();
			ivjJRadioButtonEnergyExchange.setName("JRadioButtonEnergyExchange");
			ivjJRadioButtonEnergyExchange.setMnemonic('e');
			ivjJRadioButtonEnergyExchange.setText("Energy Exchange");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonEnergyExchange;
}
/**
 * Insert the method's description here.
 * Creation date: (2/5/2001 10:32:24 AM)
 */
public int getLMSelectedType() 
{
	if( getJRadioButtonDirectControl().isSelected() )
	{
		return com.cannontech.database.data.pao.PAOGroups.LM_DIRECT_PROGRAM;
	}
	else if( getJRadioButtonCurtailment().isSelected() )
 	{
	 	return com.cannontech.database.data.pao.PAOGroups.LM_CURTAIL_PROGRAM;
 	}
 	else if( getJRadioButtonEnergyExchange().isSelected() )
 	{
	 	return com.cannontech.database.data.pao.PAOGroups.LM_ENERGY_EXCHANGE_PROGRAM;
 	}
 	else
 		throw new Error(getClass() + "::getLMSelectedType() - No radio button is selected");
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	//create a new LMBase here
	return com.cannontech.database.data.device.lm.LMFactory.createLoadManagement( getLMSelectedType() );
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("VersacomRelayPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 300);

		java.awt.GridBagConstraints constraintsJRadioButtonDirectControl = new java.awt.GridBagConstraints();
		constraintsJRadioButtonDirectControl.gridx = 1; constraintsJRadioButtonDirectControl.gridy = 1;
		constraintsJRadioButtonDirectControl.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonDirectControl.ipadx = 41;
		constraintsJRadioButtonDirectControl.insets = new java.awt.Insets(48, 105, 8, 101);
		add(getJRadioButtonDirectControl(), constraintsJRadioButtonDirectControl);

		java.awt.GridBagConstraints constraintsJRadioButtonCurtailment = new java.awt.GridBagConstraints();
		constraintsJRadioButtonCurtailment.gridx = 1; constraintsJRadioButtonCurtailment.gridy = 2;
		constraintsJRadioButtonCurtailment.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonCurtailment.ipadx = 53;
		constraintsJRadioButtonCurtailment.insets = new java.awt.Insets(9, 105, 8, 101);
		add(getJRadioButtonCurtailment(), constraintsJRadioButtonCurtailment);

		java.awt.GridBagConstraints constraintsJRadioButtonEnergyExchange = new java.awt.GridBagConstraints();
		constraintsJRadioButtonEnergyExchange.gridx = 1; constraintsJRadioButtonEnergyExchange.gridy = 3;
		constraintsJRadioButtonEnergyExchange.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJRadioButtonEnergyExchange.ipadx = 21;
		constraintsJRadioButtonEnergyExchange.insets = new java.awt.Insets(9, 105, 152, 101);
		add(getJRadioButtonEnergyExchange(), constraintsJRadioButtonEnergyExchange);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	buttonGroup.add( getJRadioButtonDirectControl() );
	buttonGroup.add( getJRadioButtonCurtailment() );
	buttonGroup.add( getJRadioButtonEnergyExchange() );

	// default selected button
	getJRadioButtonDirectControl().setSelected(true);
	
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * setValue method comment.
 */
public void setValue(Object o) {
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getJRadioButtonDirectControl().requestFocus(); 
        } 
    });    
}

/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G54F854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13599EDCCDCD516AAF45DC433B56A262B4D86D7B4983BE93538A586E3E59B2DDB546E2E46A676873BF613E697B7EE16981BA51B4BG83EABF28E5ADDAD494AAF6C524353B62C8DBBE8679A82E880823B611474CE3F870F86F71664D871436FB4E7DF873409928A61D64645EFB4E3D77FC5CF34FB9778D29FAA33D2C58D5C5C8F199613F8F0B89D9D5CB4805379EFFC060D264DCB7F17C5E84B8C06A432550
	2E82DACBDA2E3B12F826AAE01C81637954DC77A1689FA4272772DE07CE92BE99C0FBE66C768DCB7379DD0B60D3CE47837A9EE87781E4828E5BC29979CF68559C1F4771820ADD04640F631CAB759A0EEB0171G409A007563EC7E96E81F271C53BCADFC5E59EB53C961EF9ADCFBF19F471E825E8932357D3C2DA56B0A8B0348EBAD1593572442B887G15AFA1672ACFC35BBAB095BC6557271B7C1226691AA57BA71A224A1CE4861A82F2C4714BCD6A34E16AC1D31A76F87ABF9DE05D61D9C39E10B4D9A5468472A8
	54AB681E208F0C0BA86EF68B79E6C1BF8FE0270DB74208EFD3F25DBEGC3913A1EECE8653A784E2DA3C5CFE8A57B1368FA1542F4DC2F44F55DD6720F72D768393C64C17B97G2DF2B257DD8F5083B081F09540E16A8F2FFC74EFE85B472CCEFDFADA575A8D23C38D181F6A81D9053E4783940E9B147D3A99A0041D79DDFAE382F9168236EE5377DE47EC127901B87F350337119D9B4E384A9358E4C7E633ABFD1259A2FBB2EE9346FBF7D25E5BA7977A67C25EDF92A76F0215FAC9D9825ED90F9FF59570FD48D41C
	F773941E75EB742CEB21DF4C7D03619BA85EF260192F2E23BE0363D4206D1A923E615BA7E469AF3D11D49E6BCFE80755396D2EFE154990D0974B321372DC25B21E4536ACCFD37C9A871E4992890BFB138934BD5339EE66DF07DB716E9AB03E9AA083A087E08740F3GFBB461E3E39F3DFF09BEB6A019D6C7D8D18332C9585A431E172115E316A975EAA1CB527C7208E4AA5218AA07881FD36FC19B0CADD8F7147A3E9FF8F44B1AECCA1622EB303BB4ADDB3299E2F30EFB8E609CD32654E1E5DA86865386917BFABC
	8721D5251075059110ACB9BE0351BBBCF8A691E984F6048D204F6C52F3926D55887AFB8106EDBB5CCB77BBA31B90AEA004687AD45898C6CDC80606F62EB4B0F6CCC0BFC593B6761EC25CAE9893DD6CD368F949310FD771237812B9AB6ED89D4D83D53A70193F7A044FF8775ECA9A9F6FCB68B388A7F49EBB7545F767BD3AA74A4546F7DB3E6FF90FAF95D0EE4663114B462344BE2B3BCE537B87BCD7812C37ED7C7E47354CE7BB2410AC3C74B3939CCC8E44AD4E647CF3982D4C7AFFF774674317CB7EF8BC3D6132
	72E036B8520A79C70E481A65158DD98BC0A744E90D2D68E37CFE0E6B761A6394EF40FD6E048B943271AD2DF867A44BB2153130A58F491625E841B83D0D728A05470CF8A8903C3ADBB16E6AD1384F767C7ED63C8BFEDD53E4FAA87692CE7F9F3D0B32AACF03501DFAD83398FE78A47A7314BC4BE3016FA496BA91C98DE3DCE1F541FA69C55441D4266128B1B411C60346EC235AE123AD60B3D85F9020D599624EC56B441D2B1F3B19EC7A7608ABD14ED8FD2B57E5996C2EDDE5446F9C132977D4E259BF23B10163C3
	CC569BAA518E9809A428E186E5D51AE5E58977733F3461DE1940A38760B1C3787956D3C8973104BBF8272E2A4C1C361D33CFD7623948B3E199C2B0862DB3366D6EBB7D52A25A8837A323BFF89A6D28BA629D13397CDB4C47096A2EE0D8E98ADB8AC6B7498247B24296550655D85752B809FB949822163AA3047A554F643ADB8106E744B97CF62F28252E1FCAA779373C1A34EED4E7187DD74D444F616C81D1CB7D2783F9EE83DA7E0C30DDC71BB31E3707C23ADF2129046B7D37B667B9B15C33EDAFC0BB2E18A12B
	97AED2CC5828288D6545DC43517CFE7F54D8DC33944E44EB158A733BF58B4B65EF507A2146E4393C5C463FCB71A79CF816636F247643F1BF50561BA27F1C6FC4DF2F8A653A1B81FAGC681D2G52C3428E296D9F10E572FBAF1C2B29C96A50020BAD72ECC7893B0BFCFC2A23CC5CC5E8D9EE7CA5BD739C60398D0061E2A6628AE13C4792B96388552F9946BE00F99BFF0C62B3E03C99E0270D5F98DAAEFE62BE1792AB1B793CF947FC46E35B32BC4AAEE1FF1137443E8BE3744A4E64B17A1A4E44B17A3A4E44B17A
	E71D87989E76F0D6E402FEF36752B17C764EC4B13C0452B69BEE6A6733FA58829EE1BC47A76DF8DA8A639AGAF00AF4C6C48E2451DD10CA163D040812BA1C376137E882E7D09A956E6437CBC008200BDC0EB84E0BC157750177B86250022F704ADCB573C0A89712D93220D292B5C2E87498BAD07964D6B8C1B162428F8FF791C432F3F3AE8CE97242360ECD74CBFA1E9C1F0584728DC812B4AF1C657C7D8CD3789DAC65F9BCDC42F098AFAD7A4913D99686C3E164C081A5C8B387928F84B9E9B451A1C4654C9A9A2
	B5C9D14B6369B615C007946C27D6E7B1744CCC85DF1F9595B1F4166ED9G635298489293B1B476360821EBEF3B1174271CC998C373070F3BCE44581979E271983A935E230C675D2660F9BD5073E208FB4AF46A09B67E4AC5C893340C7E2F10130EFE779AF1523B9750D15EFB972C1FDDC0C79BB57075CC372DC704EEC5DBB309FA77C7C973C355AC5329053783A3F5E4343A281C597D650E571DF60FB5052260289E0F53D3887FC6F013D5416F30B1ABECFC7E93E71E4A59FF5C75E292B9B2B97FAD33F19BCF58B6
	76C6455E95F339EE061B0F88DC5B1C08FB4F4F7C30777B219F38EE60FB2FE3796A0C09BE991BDB7CD69D2678543945EF550B763DD8853442B91147B6F6E11C701D4BF5DBGD783E4GEC8648BFA77258A9EFB3D9A60F8D6BC6BF84D955919619FE7718BF6C3B467D65625C336E798549DE393BB85939971EE367DDB54F5ADD43F5944F746B6BC67D3CC09B85C885D883309EE04B3C50EF253711695795D02CF64B127C9334B2914A0DA8F2B40E46A04BF4CB37966B368376FC064BD1FA7E3B791149145503F72F9A
	68A7G7A8182G296705BC5B3C4B568DDD3104F583132BB73AD82E8C58BB1B4B1317C0AE36EEC5E4713AFC18DB4867D7DCC8366EF3CC013C7F35231FB6F3795FF9BF3F2CBC58F9EC6ACADEC3BC58153806F838ABF18D717B2E25EB04A73B123F737E5865FC67B1DFFA3697FD29864E268560AC008110FAC178127149DB4C179278F5D7A0A8C32E472FC71660BF541DDC572F3B177F66646F46BAFECC0738BE0D7B8B3C520D67E56986C5B21C560DB6B21560044526F23C550DF1D91547A91A55349BE9EEC8873D32
	AE08BA77D7E144ED0171F69BF7B8A2EA5F1D368DF2FA126B746B1E446777D0CF6273FB34E7699A70091EE4674B649B0D8ABDCEDC94B2E7C4059EBDB62EACA6749836F12BA3E2ED060D5B9191EB33ED5C4928D83B79223041BFFB135B202CEF79F35D57C75FECF09E78E51F618E76E14D2A0F0F07E45BFF2A7BAAEDDCEFE8C08FA97870E3B562A3748C27B599021A622FCFC1D97FC4F18F81D425083A74E17ACD4D8201A6D270FF162787B02664517947A35EA56EEB49A2D88BEB906A884B9D1B20BF447B0C7F0F29
	2D9EC47EABF25D0C5F1141B83F1A58F27C4A1660D78FFBCEAD60F7BFBD2F2DG4DB63F1E21B83F6168F27C4A17605786FB3E4B7B4C16F70531ED88B4703C1A9EF57EDF54724207893F3FA0742FE071FE1437B9BF2A63FBBC7A0DF30F4A9B5FF495A759E3B5DF1B31A2DE133DC16502FCC262BFE6876D8B6CB39EFF58A372FF4D9DB7916F933B0B6B1270584E77FE670AB80F7A5DC565CC7F74A8555F5F40F9EC9FA136ED36C7A8CD96B4B726929ED7B778D03ED158F37E8AF1072E99C25B0EC813BCA3E339201BB4
	1A82F240548D5934E69916FE6961FBBDC07D17477F21A3497600085C87A572E7E31370869B160236ACC51F23CF2C81B93281F6G9482D4DC196B2E85B8815083F0964082389AA09DA08BE08B40F60082G12AA7458704551A574600FA21A98CCC94154A03EF579503EF6ECE3AAF25AFDBE4CC30A8657DDD215B97A39E788A6EAC1C62F9CCC9EFF8E8DAE9FFFEA8793671537873F1BD75E9DCC1CD70E8DBA738A437DF7907DBF88BA7B6CF99F5B382FECDC9B5D93F1DFAA81EB4246FFC265C2FC0F0C8C6D6F2FD42E
	A84579B86EF3876EAB0E9B9944988AAACF6826D87B4DA0463FF1C5D55978DBBAC751A0245A7688D09995C3F2482D58B8141B7D2F3DB1348E634453244CA27B58733E163F6FAD7222639DEF117D6C45DEAB1E6CA1136C97EF6526DE4ABDBF7201276429DBCADC2434173859E97412D2B4E9AFA923966FA5A5F09AB1035FF085CF9620B9B4C3AE50FB6744BEEFE3D7BB31BC869C3D680CB34F25D5A70DD573E97C9BE5DABC86707F62E83C4D837C2EB4617FD36177163803C353122ADA12BA552E8570BEF64CDAF2BC
	7F57C6F07F101843090C5690C139A79DEB7E8FD0CB8788D776FB8BE98CGG38A0GGD0CB818294G94G88G88G54F854ACD776FB8BE98CGG38A0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG238CGGGG
**end of data**/
}
}

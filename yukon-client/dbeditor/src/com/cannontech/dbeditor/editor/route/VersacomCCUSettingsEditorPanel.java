package com.cannontech.dbeditor.editor.route;

/**
 * This type was created in VisualAge.
 */
public class VersacomCCUSettingsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener {
	private javax.swing.JLabel ivjAmpCardSetLabel = null;
	private com.klg.jclass.field.JCSpinField ivjAmpCardSetSpinner = null;
	private javax.swing.JLabel ivjBusNumberLabel = null;
	private com.klg.jclass.field.JCSpinField ivjBusNumberSpinner = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public VersacomCCUSettingsEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (BusNumberSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> VersacomCCUSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(com.klg.jclass.util.value.JCValueEvent arg1) {
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
 * connEtoC2:  (AmpCardSetSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> VersacomCCUSettingsEditorPanel.fireInputUpdate()V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(com.klg.jclass.util.value.JCValueEvent arg1) {
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
 * Return the AmpCardSetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getAmpCardSetLabel() {
	if (ivjAmpCardSetLabel == null) {
		try {
			ivjAmpCardSetLabel = new javax.swing.JLabel();
			ivjAmpCardSetLabel.setName("AmpCardSetLabel");
			ivjAmpCardSetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAmpCardSetLabel.setText("Amp Card Set:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAmpCardSetLabel;
}
/**
 * Return the AmpCardSetSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getAmpCardSetSpinner() {
	if (ivjAmpCardSetSpinner == null) {
		try {
			ivjAmpCardSetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjAmpCardSetSpinner.setName("AmpCardSetSpinner");
			ivjAmpCardSetSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjAmpCardSetSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAmpCardSetSpinner.setBackground(java.awt.Color.white);
			ivjAmpCardSetSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjAmpCardSetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(2), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAmpCardSetSpinner;
}
/**
 * Return the BusNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getBusNumberLabel() {
	if (ivjBusNumberLabel == null) {
		try {
			ivjBusNumberLabel = new javax.swing.JLabel();
			ivjBusNumberLabel.setName("BusNumberLabel");
			ivjBusNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBusNumberLabel.setText("Bus Number:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBusNumberLabel;
}
/**
 * Return the BusNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getBusNumberSpinner() {
	if (ivjBusNumberSpinner == null) {
		try {
			ivjBusNumberSpinner = new com.klg.jclass.field.JCSpinField();
			ivjBusNumberSpinner.setName("BusNumberSpinner");
			ivjBusNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjBusNumberSpinner.setFont(new java.awt.Font("dialog", 0, 14));
			ivjBusNumberSpinner.setBackground(java.awt.Color.white);
			ivjBusNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjBusNumberSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(8), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBusNumberSpinner;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) {

	//Assume o is of the assumed type
	if (o instanceof com.cannontech.database.data.route.VersacomRoute) 
	{
		com.cannontech.database.data.route.VersacomRoute vr = (com.cannontech.database.data.route.VersacomRoute) o;

		Integer busNumber = null;
		Object busNumberSpinVal = getBusNumberSpinner().getValue();
		if( busNumberSpinVal instanceof Long )
			busNumber = new Integer( ((Long)busNumberSpinVal).intValue() );
		else if( busNumberSpinVal instanceof Integer )
			busNumber = new Integer( ((Integer)busNumberSpinVal).intValue() );
		
		Integer ampCardSet = null;
		Object ampCardSetSpinVal = getAmpCardSetSpinner().getValue();
		if( ampCardSetSpinVal instanceof Long )
			ampCardSet = new Integer( ((Long)ampCardSetSpinVal).intValue() );
		else if( ampCardSetSpinVal instanceof Integer )
			ampCardSet = new Integer( ((Integer)ampCardSetSpinVal).intValue() );
		
		vr.getVersacomRoute().setBusNumber(busNumber);
		vr.getVersacomRoute().setAmpCardSet(ampCardSet);
	}
	return o;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getBusNumberSpinner().addValueListener(this);
	getAmpCardSetSpinner().addValueListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("VersacomCCUSettingsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(371, 336);

		java.awt.GridBagConstraints constraintsBusNumberSpinner = new java.awt.GridBagConstraints();
		constraintsBusNumberSpinner.gridx = 1; constraintsBusNumberSpinner.gridy = 0;
		constraintsBusNumberSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsBusNumberSpinner.insets = new java.awt.Insets(5, 20, 5, 0);
		add(getBusNumberSpinner(), constraintsBusNumberSpinner);

		java.awt.GridBagConstraints constraintsAmpCardSetSpinner = new java.awt.GridBagConstraints();
		constraintsAmpCardSetSpinner.gridx = 1; constraintsAmpCardSetSpinner.gridy = 1;
		constraintsAmpCardSetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAmpCardSetSpinner.insets = new java.awt.Insets(5, 20, 5, 0);
		add(getAmpCardSetSpinner(), constraintsAmpCardSetSpinner);

		java.awt.GridBagConstraints constraintsBusNumberLabel = new java.awt.GridBagConstraints();
		constraintsBusNumberLabel.gridx = 0; constraintsBusNumberLabel.gridy = 0;
		constraintsBusNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsBusNumberLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getBusNumberLabel(), constraintsBusNumberLabel);

		java.awt.GridBagConstraints constraintsAmpCardSetLabel = new java.awt.GridBagConstraints();
		constraintsAmpCardSetLabel.gridx = 0; constraintsAmpCardSetLabel.gridy = 1;
		constraintsAmpCardSetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsAmpCardSetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getAmpCardSetLabel(), constraintsAmpCardSetLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		VersacomCCUSettingsEditorPanel aVersacomCCUSettingsEditorPanel;
		aVersacomCCUSettingsEditorPanel = new VersacomCCUSettingsEditorPanel();
		frame.add("Center", aVersacomCCUSettingsEditorPanel);
		frame.setSize(aVersacomCCUSettingsEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * setValue method comment.
 */
public void setValue(Object o) {

	//Make sure o is of the assumed type
	if( o instanceof com.cannontech.database.data.route.VersacomRoute )
	{
		com.cannontech.database.data.route.VersacomRoute vr = (com.cannontech.database.data.route.VersacomRoute) o;

		Integer busNumber = vr.getVersacomRoute().getBusNumber();
		Integer ampCardSet = vr.getVersacomRoute().getAmpCardSet();

		getBusNumberSpinner().setValue( busNumber );
		getAmpCardSetSpinner().setValue( ampCardSet );
	}	
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getBusNumberSpinner()) 
		connEtoC1(arg1);
	if (arg1.getSource() == getAmpCardSetSpinner()) 
		connEtoC2(arg1);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAAF954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8DD4D45719A4C1C3ACE64D29765844EEB5A1B9346BB6B621BD5A75ECAC1B6EA6A959EED2EDCCEBDB5B5AAD6952DDF76359C3D24E49E5108194649F910C92FF87357C28111F50C68491F0B6328191FFF675B1731899F8B3EFFC7306997C5D6F3B775DB70FE186B1E7F3CEB967633D7B5D1F6F7F3B5F3DEFC8596E45591926B2C2B23309765714C9483286C21ABDA9CBB8AEC6CCB693435F87G45E40F
	F9B3BC67C15F5002E4F3917956F8AE3493203D6C51E473F6F8AFA55F99A7357092054EDA8BA1D73F1114B3BD1D2B651CCE8EED1B8F6443F39BC0A2607039C9C47A3B8ED4EA78948D4FA153C4487A919C53F2E02706EB06F69F40A200E5A3EC7CCB704C2314E735B6EB632EBC3618ECFA652029G57B12C494162E0F32D5A3306BC1B3949013406A91FB8A7965AAB81D078AC1252F6921EEDFDE30EBF59E4F71DCD70F8E40FAA5A1CF5225D254ACA1DA27BD53135F5D0D4FC820C6868B85FAF2A2A4B63701D26837A
	840FA8F91DC8636B87F2691AA08F34B3A8EE6EB852CD027794008DBAFEA0007813AE3097005745E57D6341B6CD16211B4F1224B7BCD905D1E40DF3B1991739C2324E773F1B33035A652B95287FB468AB9ACDB66F836886F0824481D4D3FFF8267C90BC5B07558E596D16BD6DDE6FA9492E7C17EC97A5F8EFED059E8D7729E81395BBA14C664E83C7A270B39D30F96377BD0F69A47198377F52CF1FA16B1EBFEB4A092013F5098D264523CC974BC7C3BAE1341F0DCAFB6D68E47F1CCC7B82B152CE18ADE7E5C7201D
	34265614262DC346C2349B465056195456B5701E29798743EF25F85500E73E7AC56AB3580E053E54B16E9BC35BB8AF564D0F1322C6EBC4BDACCCEEB7D9A54603DD8A6765A5CAF31E44E8E66B3C7C1062938CF8464BA50A4776F2685B69CEB6B37FCA2C44580D0176A200A4009400B400DC8F0C73F09F7B37727A997AD81F2028277CAE49AEAA044DCD2C28002798D495214763D3850FCD9C949417B0AC09BE220D79E6856AE0F8523CEFD35F37810DB322C7D48455A5FBE0F541AD2A9045EC4C8B9545B8C691E96F
	004BAD8281371770F57F2122941E1260D34FFB6D02AA06C6307E9FD420CD46854C8B30G3CB33D2C59017AEA837987G6C3A9EFA3763FAE7C58532C5EB2BD516477C5E811404A4F9D04FFFCFF3C7905E67F9F49D97A2AE9D7BE53E4E835BCB8C6BF43AEC483E20CC70984B89E0FE5EA7F31F7957A16EB31D85CF13A3EBFAA37A8CC21F4CFC45A90747CEB1DD9379E26DAF0F8F5DE79C4F9410EFC663394F0DC616EE2B0AE29A8BC0F3A9C00A2E63155BEB184F1E92FCA27752F38AB818E88FE91C71790F01DCCD1F
	38AE433DC9F19BE03DFCAF1767DAE048B06E7FDBA6C6E30252407533BF879ACCB74FD562FEA40E0B9E35D3740A9EBB3C78343E951568F3DA3C0E487A1C97A85E8B715D81816553715F2B449894D4D5F18D43A64B77D53D7FD5CA4B679F7606D28327353A92E51483905F7A78B7ABB1B6EC3247A3D2A369D3347E1F505894A5518DCCF748FE0F4A703F58017EBDA6CEE8396157BB30709997A4BF6699D6A7AC9336228C0A4B8D2647D4C596FAB1EEFF2A57B988478107750E844F98AF0F4140DE9E037BEEACA12B2E
	6EB7C55AC396BE5DE9DA6F55EAA8EFA88699CFFFDD98197767E90E5070BE466B5D46DD701C3EE261B1F02E8457AD3B1EEC36G1C3C4EE3203E887BF91E511C3FC316A426DADD67BB0B0B50C662F53F886999935AD9DD0FFB0BCB427A86B51D327E434528D349108B532F53FDB1486B42A1E8FB8166DDE7FADDFB1D69050DFBCD9F5729A41B2FA8D347B1FD64DC453DBFD20D67703B6A7C2A8BB329200253FA7D2A211EBBF3B0D7DB6316426B36BB07E8BD876D94008D8A3771EB853CEE1BBF36182CFF2AAAEA0D1A
	21E8367505ECDC73902F5B3EDC0AB42FC11F572757E945463D235D6713EDAE3AEDE9FABCDFE27481063BD432851EA3AE452776C01086394E2F15A03F382FE9E8ED1FBF9E647BFCAC50DD895026529A7DF0190177CDBE36BFD920AF0B2E77C4301547CA9D7D5F6194BC8E51EEC1CD0FC90E3AD19B46BC55B29BD0575BC15D60B46610DAEE2397EBCB983FA235825BF4AD35033C8EFB32742446C34AB79EA7EDF7CBCD116A62384277CD31AA536BD2B5245FDB4F1933527E7777B9CC56F7F5D953E1CC031F4B3A78DC
	A4D91DFE1E432D8743EB48DB7ED06DB84FBF350EE43555FFD01FC970335AAACF473FC7712B8CF846DFAC45E3FB9D741D9C677500259CF3CD8234D7812C855888D094G99825CD7D2CB0E11FB54DBBD607B0AC7107AA7A5D64DBF7E32B40B65C22D7DE4E9B637AF617BCCEBB0DC8F5640E4591DE1EDB62FFB4A3C3A30F6FC58BC669BF7CE9CB0462F412BC6DC22E4EFED6D6D68773ABCDD58009CCD6354FB98E38273CBC20047686DAEE30C2EACBCEA5A9AA5C657E9FCE484C2BEF4C4BFDB3DDE01F927AD88E7B7G
	AFC0BC40B20094000DC1EE0BA3E5FF3C17ADE8908C489D006B06BC58913EF7693AA3931FCD675B264C8B57F9514464F9CCFFEF1C637A33C0BF2BD1EDB49FDE03362A634E56A2EEA13417CD70B8C9BF04FB42EAE8EF84A83A11ECBEF20377E59F46BE4C69182F3B816F547B669C427A85474E249ED9BDB133F1C8FF234E5BD314C65E8C66EDA3B32F55F2B4DFE5F6EFAC0FDEA3B517C72E117ED49E39C66AACAFE6F8D843F8C2627D567269EB280172C8B5547BD43617AB8A695ABE1F68230E081B2056779B351CC4
	2825BE01C3142617D2F2BD18270DB92D4A9D0D3A4F798FF31F0B835DAFB258F3993C3F8830B64C46AC2EAED6E27ECD033E5C1B49669A00E600211BBC1EFEDBFE4F5CF6BAB8CDEEDBD7915DAE3F2A08EC1737AAF09F7749FE45A6066BFEA3BDE7323ED0647628229B5544463CC3672B02629027580EEC2F547B224D4F5B9E4D3E4C86CD93C69B9C55ED306E9C37C1907497FFB3246F45701E8470524D49B6E06B5DB808352FA4CC48FE15106EDB98630F9D64B56EBA183389E0A7609BEE317D116568EF86B0D70C40
	A19B586B770AB6326FB63DF72B65F367417884009781B6GEC3E4DEB0872BE4C89277C3ECF7C6EE1D141DC8EE5BD6B2B9FC1795B5D5E8EC1310374DA274EEF7361ED4639D6E198EBDB5677CAED6124392C136D75CD79463DDE3A4DE419F71B57822F7BA375A75C66F9AF1E6EAF4B213DDA47A5B6A3AE955A69BAEECEAF62726FA41B775D613871FA1EB31BF55CAF37A1AE8E5A0B6FF0FF9726B9EBB9231C35BCD14EDAEA6574F964D6543398636F6DF3DC5EA41D67C339DC5E17F45C2ECBDC5EF5BA6E3B19DC5E0D
	BA4E71A1177764DD70A9DA772CB8486FE887G97FF177BD663B53A5F627EBFAA0C8BF5C2C0EDEDBD23386C27840715FAAA5B33B2AD7CAC7E62DDDE2F37DAF04D8CE8174540BE9443EB016081DE8BACFD66F1E20DB99B31DEDFBFF0545499235D0B4404EA01C42885984D38DACEB3817A5745A06E5B35C65FC1DFDCE2E27BAE6BCF1D540FFA47EB79D0FF55A1E3BF727B1BC9732D137A338D7319EC5F5F4FE54B78FEA2113E559275AC326F81A6D337766C9B5CCB764D79FD8E537B5BA315C63D876BFC817089A837
	E8A0B179BD7939DA4E0901B598AEEF1C6316BF40E37C29434803D791C7C485CE127D2E9B380FD89E4471ABE9ACED0431997A78971A71CCBE8267B4B6E68D0DAD895E038F72B1CBFA710CB3AC58469C8A1C086DFC3D95B4669261BD89A077A1FA8FDF5F29493C7A4129F5C396895D4DA0244298043D9ABE63C1D62373B1105737E13E03E467F27B5DD49AC63B181E477B006695G065B59C7FD9D5A73F5DCBF0D1DD550CE51F1E7E86CE4C0BBB716733DB61373ED090D4CF756B4FC37450678E6B46651386B867CC0
	AC2F09DE18F22E19C93DD3CCDE7D0C734C167B1D474ECB7935984BB18619983E1C62638DF816CFCEDAF8BED988FD99317C9C75F1957A7535D94966DBG7381168224G244E623937E4C78339C72DB1A0FB2D101CA5C3BAE5724D2D7DEC5FB93E1B4363B669BBCF1265335F490CF6BE491845FCE167EC76CC9D58EB2823DE2FC6792C5067840887C8GD88E70CF3339FC17F69CE172611DD03B2A8AB6A73DBD60428D3A44C0880D9BBA134D17EF14ED03C6BFDDFB66BFBC55C735F34838F1DE0DB66E38766C0EB06F
	4A43787D7137FB089EF3DFE946FBAD0F9828615BC0DD278B58438BF84B078DC7EA0E9E2D653ADC9CB0FEB3EC5E5294714E0D56C39A6DA46D3972613D9A6DACCB08761D5EBD61343BB0B96D313B84C9F66CB9D283749B0F57F23E078F0766FA6AB1DF6B73BAE4C9D66A82CE17AA32FC7937757C1EA98D666E26F38849A521BF3666AB28C2CD3EDF64F3DF5979D7CF104EB75E4964B1BFA54747B1395438507E15BA1A114356BCDE97627321F3B351AF1BF721BEB4EF05F966735EEE8C4DAB39F4FFBA3DF84CC0B3F3
	FA1D661938CE17025CEC7E2D63A1DDFE754350DAD579B3611F79F189EE355AFB35613D4B72F9FD2F884550554F15864B8B5DD551EB4EF3D511EB4E73D511EB4E4BD5531FCDEDD5516F77C72B0C777BAC2F49A750669B401E64912827811A812C0F701C16DE057E04DF9427663353F6871E5170AB222AFF8F280EAE6BAF2B6F7D6D71DF2AE97DA12B2A6C4675B97E5F2B51DE2A6C25C8067B4FEA541162F2B8D5B6D443E7D4533AC69C21E86D4C104F6BEA67A33CDEF61FC75CADE8476B38574F733AFA210E5BBF4E
	67266A381F6BF3B3F45C49C1BEB7F78E2F09FFF05918B30B9E7F40447753F0DF991A23FDDF1E934AA94CC62FB551F5811FB4C77F163097136120B02A6D226CB384358CA07B9459ABAA6A8443522BE14D5F7F2E5E38E72C0675D267CCFF6743F8A8E8411AA98346D6FE814EF3G83GCE00980079G09GAB81D683248110F8B89FGD8G1A812CG5762B97F05FF281D06FF6DA0C21DCB91DC68DEFC4FB39979D7E13D38786979E77482A7227B651D9377764B07CF506F8160CF8393DE6D9EA17E841E6B6511911F
	287B7FE3A70AF4DC0F2FCF7639707E0155094FD39FDC847CA64673FABB76BC5664AA90E7B974AD3C7AA72CCEFD6BBC2FD357C61051D8BBC72AD35362436B54CF289F2783FE73DC1E3F691627513BB048699D1F9B195EF4F5F1775C90BD66B3AB685A5AF72B267D516C8D1924977608F3432322CD9D909C7A1C9FB5EDB366BA6646DA5FCF1BB0EF41D1C105FD4DF503FE3A6907019E870B459F537DFFC063C90BD90A13B49CDB67031668FEE1E93937DFB436C44ED7CDADD373D5DBCB64FCF52A45182F986ECC8B66
	EA87444110BE4E2A63AE6A387F26EBA26E024B2EBAF57C854A97623BC5A448717FC3798AD05C10069BB660AEEAB8C78B7AB6086C1495BED7EA2167C717A43136170EF1F94055F5FD78A90FAE2FE0607B060E3B285BE23FE92AFD8E190C76E17E79694704701AC2B8BF535A0655ABD5033CDE09537DBD66A451577E24A334768F6FF36DB8FD6DD57A5A57B1FC346FAB827DED1C661F54B48C3FB3D89042F353E839ED7720B13725433A790FCE1F5B34335124F9B5B06778BD66B1FD1F981CEA030F860DB6E0380488
	633EB6E99C7BADE4EB56932005253FA359BE0514CC2D7B5A9FFD730D25D7BB4BCC4454C34CCEAAFC8F498E20F60278B3A18C3723B037E0128D3E376443FFCE795920AF0B18FB488E3C3E6897959A75229DEF57351F8BF511BC2F8CDB85DE9C74104DBE7A4876299312E8A5E5A31F62A7F31FAA5A5BF9BE7129245C0A146A71C3EAFD3760F32A985B9FD949E68F7E30091430EEC970B86AD9F27908721959FE8DADCDD5C9005FD24AAF7D9C2B2FD27AB7057BBF685D30BC0B183A4896C9966CDD028DD2DE8F49D31D
	0A68F34A12BD687D183AA3498D70AFC3D930DC35AEBE8ADBB7G99C8E7FAAC781578025402AE93A1C10554531138FB5469A36C9DCEC181D2225223628F35E4E51F5E7740AE75402BE736DFB111CCCD486B648E599C742A44BCA238DD52843CB00F77E1CF2E8A5CAA0247B7A2ABEE602CA60CB35E2593287B624B8D8B3E766EA5CAA0B4D5A53B19AAE1EA3DC3145D756DA3A3AE0F28AFGFA05655F571747B1F5E1E3CA020F15772EF9EF987C2013E4399B9A687FC67A7FA87EEF2418C60AE9BC8A4A5DC8267D8B6F
	8FF2192973GBF05149FB4FFDD97D8D07D716FDEFE72A7ED9B53C03AAE12CDBF6002910AC2235805796C258B5E5FBAFF7D9750C6DEF415A02E1B9F049537CDBEDDE5BE3D7D21223FB866B7115CAE340064F58ABD5A4AD632F5186E952282E3D5D29452A01D8BEEEA32018FD70770B644335EFE2FE0C3CF4E338B4A98DD81F9283ACC72C69447B0A5209253652E4BC0163630FB2715ECD11DD85323CB29E4FBE869BE2C42348E968464BD787352BFDDD5A8D88F5D8F560F9FEB4D7A55D3D9A63299E21D6D0F901538
	4901008A8B7DAC589F69DA189B4BB4ABE4D3AB90BD9DCCED192604A0E24DD3E38951DBF40F46D6819DDAC87F175C37FF602CB2BA37C2B76BFE66B5BB42B402385D53CB26122D277C3E9A66B32DAAA9E8F7FBEB7033CA8D1C6ADA7F7CA4ED88CE120A7FE57F79F15A9405D36D1E27762E71CC16BE2BB26ABD5622855A6F4197041F49F66420C4E9004F7D92BF53384CFFA051EB5CFE37A0C92AA00D35FB6CD86F1E1AD0455039FDD78E2E6F63E334CE5637BF87799EB54C79BFD0CB8788D4EF78380792GG58B0G
	GD0CB818294G94G88G88GAAF954ACD4EF78380792GG58B0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4192GGGG
**end of data**/
}
}

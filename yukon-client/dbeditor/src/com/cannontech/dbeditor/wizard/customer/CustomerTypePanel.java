package com.cannontech.dbeditor.wizard.customer;
import java.util.Enumeration;

import javax.swing.JRadioButton;

import com.cannontech.database.data.customer.CustomerFactory;
import com.cannontech.database.data.customer.CustomerTypes;

/**
 * This type was created in VisualAge.
 */

public class CustomerTypePanel extends com.cannontech.common.gui.util.DataInputPanel {
	javax.swing.ButtonGroup buttonGroup = new javax.swing.ButtonGroup();
	private javax.swing.JRadioButton ivjJRadioButtonType1 = null;
	private javax.swing.JRadioButton ivjJRadioButtonType2 = null;
	private javax.swing.JRadioButton ivjJRadioButtonType3 = null;
	private javax.swing.JRadioButton ivjJRadioButtonType4 = null;
	private javax.swing.JRadioButton ivjJRadioButtonType5 = null;

	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public CustomerTypePanel() {
		super();
		initialize();
	}


	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC6C9C5AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8BD0D4579956A9A63435B336E3DA5352569A1B5046A6A4A189E3CC4478C284C585E2A2C60CF6EAA6B631AD1D5899A7BD3CDC44C784D6CBDB8D8888081B85D692ADA18F910DA809A4D2DD9BBB7532FB012B4B6E3AFB170751247FFF9EF7AFF0971AB4E1661FF34F771FF37E47794FB97FB98BB117CC4D4D4CB2931219CB785F51CCC2B69CA46414ABA5DAE061CF58CDC477F7G28000C8F64C0B985F8
	D35659CD3B096BDC9E54E7C0BD66B93B69AF70DDC42E1FA3957091C24E4AF2C242464FAB99DD4E6D7B059C932D67976C00F297D094E049C02533D1FED9C1B147B783360BFFA3E5E69112C05B2476956BF067EC56AE06175ED8EC73F641BE4EB7C39D5BD820FCAF163531C3390DEAB7217198EFF7793B11A4A35E123593E569469794437B26713214C4D3C7E4136936D3D0B635DF6BFE47616935BAA4375B63D6E5C70F5559A9BB9555633376ABB7A41F536A147B9407ECF5847C2A27D776B5B61E60DF9D03DE39DD
	F24BAEC25A66207EDB7A726858207F9C216B321DCDDC174957A2C97ADDA5A1F51DB4076998BDA728EBDDDCDA0E73C99C3B0B7A9E7BE5G5F829813F9DF01109989D875DC44AE8730EDBA54B7E35F3982AF6FC35C855FE1C091F3050EEFD7899D6D1FBECA72324373DE8D21E374DC6E4F39C19D274FCC4B79AB0D0F22921C6362F9F6D393D0BB108AB499A89A686FB4AE176C2F0172F827FA42535B6BF19F77FA1BDDCE5F879E276C026F46C660F06CE3596171B989E1311759EFB150C7D0423C21F10744EC3BC8C4
	2C4C7878BE327901703CDC835B22D32CD969FC0C0CF9C15B18DD771722DD6679F6D39D50E920ABC06300264E97F6953FF6E4AC3BDAA13EFCEE49F5BE506915FCEAF3C0F1B9E59F374FD5B01AFDC6447A5D78427D18DFA20F687D1276D0FB164940AFE911EFE44D1A4F7C11BCBF6897A67B211032B3660F1C8F2462859807C9B4BE7360BB1347AE431F2178E59D4EE23F1362D8F781AFE90108DB7BAEE1C3DB4EB4323B7EA3C39B26BCF9AA2BE1A15327EDE15086A67387F4CD8CACE4B2B3B5DD26D2BCCC07B3DD8E
	7509F5B889F899C0ACC63EDD06FB17BD8EE29EE8A250F420F9C069C099F1A2CE9C2F557E0F715F2E0B9156F7DF49DEA8658155A7353A7D2A64F64897A41FA2F53AE4BF61ED8E16208FBA07742B216B5281B25E175D32CFD2950F9BC617FAE588C8BFEBF32424G5B78E44A6DD0FAE590506BA5E25C37CA0A507F12DF7D2757A929F23085631FA841B96913AE4008B0GFC37ACC2FF9577637EE4066F3AC542D7B1650805C3FD6AA26117A7CA8A21BC8B8BC4F6B5B636A88ED4C3728D8A79B12538A749FD32DBED11
	3D325B899FFE4E1BDD3AAD6817AE0F2673FC0A63DABB810A7BB5FCF1E98143E18C3D1785FF8565CB2A6AD3BA832AFCDED6D5455D9D6C3F0A6A629F320C7D046F9595989F51E0D782D01AE61F73C0114E3EB60F67DA405B01B3477D94CF7771CD701D97AF4E5BD4FA4EDB62C53C3FE897715E3273D74412F24EB05EE91C4733B8F7468FDF3369F44C200C1E001D7731449B2F597F07180C711A0C6C2FF8FCA4667732CA747BC430A192A8AEDE787D2787CA59FAEB167C32D8E167FCB0E332B398E5CC4F9F7791CD67
	3BF55F35FD762FD0DFE39F1D7EDAE5B0DF5CD196FA8DC514992F21E9E546EBA832EC74B592D5E634C6986F51B2BC03BD7DEE5D1A1BD5066B5F61F13BE5BAA9DA9742723597BB508E452D289795E7372C1ED7EE4890470BF16D5452BDA69D66BCC3DBAB7D65E22DD45C7885097DCFCD165139BF65672DD9ED0BD95CDFD99CDCABCC27474BB126E4175C8B0EBA618938D5064F257B9747797E79F005F15B47AAF03DDD1387F93B5995DB896E132E002C5996A5EDC79F7B14DE88C55C10F84E3C36C01FB30F837DB03F
	0E05B299A81B0E1760C53F1D21F928BEB1956B6213AA54EBA334A789F6D3F102D897EF9DC43E580F7802B861F139187B35F9292E5C0D73A8DF8F48F05C6006F5D633253632F0986F82370B710FD622DDAE5DDED71700F61D6A97B9AB1E058C7BB7456AE1B13C14ADF2148FF66B737C305F5FB01CC3A4B5014DDDF842705CEDD2B50E9B89F80CE67B03157A3D78385F6FF1A874586373F1E7A366F2C6F78387BD342D5D81459AD095BC28A495560BB720320BG4B494F9622CC9CABA1C1645E7DFBF09EF2926D26FD
	C08D09C29729EAE4ACF5D565C35925787C10B3BA658161CFD7957A854FE08E0B9C572677D3EC7A1BA17D541648E3BDF1381FD28F219EB7811F2469761B2A821D1FA09F0714239BE6D15BCF2A33D9EC737A1D5539A2360958575A8B07670853930367CCCC6248B30765DFAFD0FFA5A4327CEB3B067F09629BF5B84B4BEED19C6BBB1658CD5EA5A257C828C13B12213EFE09304BFAE8274E2EF60F62D6B51BD69D42756971A95D0ADBE06994035B885D3058F95DCD67B883464CDBEAB7B12C450C18856BCB45797A3B
	BDF079A6BDD08F870A840A835A20717FCC79F9C990A7C02701FA004213847F9BFB061F8BB88E0E075FF1CB0D4F6C489038208D1C4FE4C40C1011CB02E7753690E318C699FF971F0B6059332C8667EBC02B27507AA017D3C4BEAB5CCAE3FB50930049B0AFC3FF5ED1A8763FC870C7AC508620ADC9ACC7E2BABE500FF23AA0190563673CD7F610CD346FA7E65177B2543DC0130022001216093D53D30371FD66E34929F81A832A8AF9AF6C1C751ACE8B2E9871ED9A3F75B3A37E910DBF6D10913FC163BFD5E744FF83
	7D349CEDE858EB56797358F2E63793CA46DF324D089F2B719B870C78499A3F46EA445F20715F35997137C049565C630562AE34FB85446E8AB1A775D770AEC4774F2BD21FE4157A55464677FD0A33D96AEE23B34C76495C22BC5EFF5C8A31CF9EA942B1E3201E8434E105B84F87AA459B450C3B271136F16D066FA8699DC733C8B20B6D0A64609E97B3B3AD07499CEF96B22FGFFCAB2E2771B75F6625CDC4AA2448AA5632F9D4247B95BCF747C22BDFABE6A7B78107EA7077051C7D53CBF33ED71C1E15B1645D144
	7570BBA15F085239CDB9A9FC7D874A49465BD767B03F6FEDFECD6F77812B3F9F96C5E323BE4244DB55353DF9DCFED30A70712DD37A372AD92F9E4D5A9EC28F154B1F1C9274F11366635C7CBC3EBFA42708F72A6B83825B11AAB049AA30968D6B33896CE62A5883670EB8C346A256EF59176C17DF74C57B3133A957CC6FFE29435F92F6D1BCB6F578DB42BF0A447AC9GDE5DD3624CDAE1413DEBB25423006200520148CA489DD60AB36CDF076B48986F889D9EEF9BA47BAE5D951759F71B794B3D994DB10978081E
	79CB92F35BAB19216223EEA50B0BF6DEDED14BA94E6C6B3222FD63009781948B3492E8A3D04E53423EE907AD4C3E13CEC5BD2E2A122307E6C842388B0A5C9F04F163E736F977E23F622703671F853E5F63750E27C71E0BCC2724DAFACE83BF8CA882A89AA8D953E76361B15F77CE8E0C723E171FBF5C6799B0F65EB3CC1FFD4F0C540B756B9B985E2F8E5A36707676107D3A2C437B39206DB85EFEF248FE1F5A06770B043633F87B24107D36237B7837D9777DEE5157FFF736FF2DB2587C27D9C25F1D57D90C6F4E
	4FDB0C6F4EAFD9C63FBB3FEC89FDF7FE45223FBB3358AD2CC7072F0739A12B6C268A20E3C0ED2BC46CCEFC3D0C452E41BABA891769939E7A1A288A799EEBE8DB7B2CE33FC1FEEE457BCB27874E1BDE9CDF60932CB8DF2A47CBC106FD4B0ABE02F43BC7E5CDB9BE091E8DAE390B42AC3F3E1D1ED9BDE0574DD5A2677EE689E21320BEDD4316D3AC866AF19AF634D46466491ADFD6560636F3CD2D711C7E3A56F8CEDF289D7DCD718F3521661C69F7A0DF58D111A6F41673059D47B4ECC13EF64750306F8C083E13B5
	EC5900689B29E136C151B7DA43EED1BF672C361B0AD7EBB2A8E601FA13069D2E95FDCFEB5877EC226F948DCB35093EB3B4ECCF2E689B33DA787EEF7521FDFF20FE6C982B292777E6088D4C1979FBC4BD3D3BF5F579E5AD169B6AF7EBD82B3F5D63D770224F726F1F50F8724B7866A1BB0B1FC5DD9F5EAB5EA6721E95770429B41EFA5DB2EC7C0A0335FB14E20B00AEEB6D1EAF41379E9594F7BD0B3F278E16C315A451769F16360C3247E48F23A86803F4103073F59EFC5F4B3F197CB3B48E96E2FD0D5D4464C5D4
	84652D4E9FCBDE6EA8722A578C1777880D2745C08D1A3C59870372DA874712E79AC5DE0BCE9E7365E46BC81F5FEB95BEEFDFA3FC3E5ABA5467AFD786F5EA2E9DCB27ED2368F4E5048F9E33891F3BB49FD4D58765FD14BB163C6D23483BB9428793D8BC7982B24B3F26766B5FF90E65B705FC6789DF434E6988DEB65CD34E47DCD202E515330A67F4CBBF510F397B0715D919A146CC66E3252F89667CA5F4CCD8ABA478477C328FFF6665B2566E90F9633EFB6FA2ADCF3D12D99EC206B91D7FEE119E14E17E636A9C
	263B14CFF5F7D4F1991B8E934DD7098314A78BDE756BC15E13D6E1B3631D3586F94BEDC3478C43A7C9FE2E565721DD9120CBCC3A5837DC55A87402F415E700A7697F8A50539440F61F47AB7B54C1065257D0BE16851F95781E74E07541D0E34009FC862EEE67BA2F421E54A1F5EB3ADC34E1EC2D84BDB2000A5742FD81288568B21097A88CE88AD094D09CD09A50A6201C676CA6B3D0B5D053F35A3BE34D21D16C6017F51A9878A47D7B581BF5B8AF5AB946CC643C376BB08F41B7FAC5F2A9B768136AF9E8686EE6
	7CEB3650FB3E4FB6761E7F294DB82F98EF9B19D75CE6B34EABA65A74F985433EEB43F5548DB65B35F65F5730CB9A76A3BAA6E29795275A2361BF23FAA1FEDAC601823F076A55CFB1BB47E66830CB9CFB40066796185C6371093E0F58F06DF7A9AE972B4F24ED94B79CE31ABFE6D39D952F2453FB1E06215E6CFF015E3D349C4A993FA539AA4925CFDC3C34717288AF9BF879065FC7DE95CFB756D6AAB43D6FC8E3763A3B3233C8CEAB594AA621156420A7DBC9AEF5F4AB4906C99870728D819F7B5071797E6B64B3
	FA5C6851ED9A3A55905537B5E968077A36E2FFB97439FE8F5BBCE1FF48FDB1ED9D5BD7B62D9B7E7EBDC07FF747329E721B75E20D4485DE9FE51D1E6F15DCAED5F2DDBB6EF6621AED9ED465E0BEF82B8F47770BB61C49F8E1FD2877D5DD1F7F82D0CB8788B5CDD39FC68EGG90A6GGD0CB818294G94G88G88GC6C9C5AEB5CDD39FC68EGG90A6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG008EGGGG
**end of data**/
}


	/**
	 * Return the JRadioButtonDirectControl property value.
	 * @return javax.swing.JRadioButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JRadioButton getJRadioButtonType1() {
	if (ivjJRadioButtonType1 == null) {
		try {
			ivjJRadioButtonType1 = new javax.swing.JRadioButton();
			ivjJRadioButtonType1.setName("JRadioButtonType1");
			ivjJRadioButtonType1.setSelected(true);
			ivjJRadioButtonType1.setMnemonic('d');
			ivjJRadioButtonType1.setText("Type1");
			// user code begin {1}
			
			ivjJRadioButtonType1.setVisible(false);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonType1;
}

	/**
	 * Return the JRadioButtonCurtailment property value.
	 * @return javax.swing.JRadioButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JRadioButton getJRadioButtonType2() {
	if (ivjJRadioButtonType2 == null) {
		try {
			ivjJRadioButtonType2 = new javax.swing.JRadioButton();
			ivjJRadioButtonType2.setName("JRadioButtonType2");
			ivjJRadioButtonType2.setMnemonic('c');
			ivjJRadioButtonType2.setText("Type2");
			// user code begin {1}

			ivjJRadioButtonType2.setVisible(false);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonType2;
}

	/**
	 * Return the JRadioButtonEnergyExchange property value.
	 * @return javax.swing.JRadioButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JRadioButton getJRadioButtonType3() {
	if (ivjJRadioButtonType3 == null) {
		try {
			ivjJRadioButtonType3 = new javax.swing.JRadioButton();
			ivjJRadioButtonType3.setName("JRadioButtonType3");
			ivjJRadioButtonType3.setMnemonic('e');
			ivjJRadioButtonType3.setText("Type3");
			// user code begin {1}

			ivjJRadioButtonType3.setVisible(false);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonType3;
}

/**
 * Return the JRadioButtonType4 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonType4() {
	if (ivjJRadioButtonType4 == null) {
		try {
			ivjJRadioButtonType4 = new javax.swing.JRadioButton();
			ivjJRadioButtonType4.setName("JRadioButtonType4");
			ivjJRadioButtonType4.setMnemonic('d');
			ivjJRadioButtonType4.setText("Type4");
			// user code begin {1}

			ivjJRadioButtonType4.setVisible(false);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonType4;
}


/**
 * Return the JRadioButtonType5 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonType5() {
	if (ivjJRadioButtonType5 == null) {
		try {
			ivjJRadioButtonType5 = new javax.swing.JRadioButton();
			ivjJRadioButtonType5.setName("JRadioButtonType5");
			ivjJRadioButtonType5.setMnemonic('c');
			ivjJRadioButtonType5.setText("Type5");
			// user code begin {1}

			ivjJRadioButtonType5.setVisible(false);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonType5;
}


	/**
	 * Insert the method's description here.
	 * Creation date: (2/5/2001 10:32:24 AM)
	 */
	public int getSelectedCustomerType() 
	{
		Enumeration elementEnum = buttonGroup.getElements();
		
		while( elementEnum.hasMoreElements() )
		{
			JRadioButton button = (JRadioButton)elementEnum.nextElement();
			if( button.isSelected() )
			{
				return ((Integer)button.getClientProperty("cti.customertype.id")).intValue();	
			}
		}
		
 		throw new Error(getClass() + "::getSelectedCustomerType() - No radio button is selected");
	}


	/**
	 * getValue method comment.
	 */
	public Object getValue(Object o) 
	{
		//create a new Customer here
		return 	CustomerFactory.createCustomer( getSelectedCustomerType() );
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
		setName("JPanelCustomerType");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 300);

		java.awt.GridBagConstraints constraintsJRadioButtonType1 = new java.awt.GridBagConstraints();
		constraintsJRadioButtonType1.gridx = 1; constraintsJRadioButtonType1.gridy = 1;
		constraintsJRadioButtonType1.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJRadioButtonType1.ipadx = 85;
		constraintsJRadioButtonType1.insets = new java.awt.Insets(10, 120, 8, 101);
		add(getJRadioButtonType1(), constraintsJRadioButtonType1);

		java.awt.GridBagConstraints constraintsJRadioButtonType2 = new java.awt.GridBagConstraints();
		constraintsJRadioButtonType2.gridx = 1; constraintsJRadioButtonType2.gridy = 2;
		constraintsJRadioButtonType2.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJRadioButtonType2.ipadx = 85;
		constraintsJRadioButtonType2.insets = new java.awt.Insets(9, 120, 8, 101);
		add(getJRadioButtonType2(), constraintsJRadioButtonType2);

		java.awt.GridBagConstraints constraintsJRadioButtonType3 = new java.awt.GridBagConstraints();
		constraintsJRadioButtonType3.gridx = 1; constraintsJRadioButtonType3.gridy = 3;
		constraintsJRadioButtonType3.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJRadioButtonType3.ipadx = 85;
		constraintsJRadioButtonType3.insets = new java.awt.Insets(9, 120, 6, 101);
		add(getJRadioButtonType3(), constraintsJRadioButtonType3);

		java.awt.GridBagConstraints constraintsJRadioButtonType4 = new java.awt.GridBagConstraints();
		constraintsJRadioButtonType4.gridx = 1; constraintsJRadioButtonType4.gridy = 4;
		constraintsJRadioButtonType4.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJRadioButtonType4.ipadx = 85;
		constraintsJRadioButtonType4.insets = new java.awt.Insets(7, 120, 8, 101);
		add(getJRadioButtonType4(), constraintsJRadioButtonType4);

		java.awt.GridBagConstraints constraintsJRadioButtonType5 = new java.awt.GridBagConstraints();
		constraintsJRadioButtonType5.gridx = 1; constraintsJRadioButtonType5.gridy = 5;
		constraintsJRadioButtonType5.anchor = java.awt.GridBagConstraints.NORTHWEST;
		constraintsJRadioButtonType5.ipadx = 85;
		constraintsJRadioButtonType5.insets = new java.awt.Insets(9, 120, 78, 101);
		add(getJRadioButtonType5(), constraintsJRadioButtonType5);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	
	buttonGroup.add( getJRadioButtonType1() );
	buttonGroup.add( getJRadioButtonType2() );
	buttonGroup.add( getJRadioButtonType3() );
	buttonGroup.add( getJRadioButtonType4() );
	buttonGroup.add( getJRadioButtonType5() );
	
	Enumeration elementEnum = buttonGroup.getElements();
	boolean selectedStart = false;
	
	for( int i = 0; i < CustomerTypes.STRING_ALL_CUSTOMER_TYPES.length; i++ )
		if( !CustomerTypes.STRING_ALL_CUSTOMER_TYPES[i].equalsIgnoreCase(
					CustomerTypes.STRING_INVALID) )
		{			
			JRadioButton button = (JRadioButton)elementEnum.nextElement(); 
			
			
			//DO NOT SHOW RESIDENTIAL CUSTOMERS FOR NOW
			if( CustomerTypes.STRING_ALL_CUSTOMER_TYPES[i].equals(
					CustomerTypes.STRING_ALL_CUSTOMER_TYPES[CustomerTypes.CUSTOMER_RESIDENTIAL]) )
			{
				continue;
			}
			
			//set the first visible button selected
			if( !selectedStart )
			{
				button.setSelected( true );
				selectedStart = true;
			}
						
			button.setText( CustomerTypes.STRING_ALL_CUSTOMER_TYPES[i] );
			button.setMnemonic( CustomerTypes.STRING_ALL_CUSTOMER_TYPES[i].charAt(0) );
			button.setVisible( true );
			
			//use this to store the customer id with this button
			button.putClientProperty( "cti.customertype.id", new Integer(i) );
		}



	// user code end
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CustomerTypePanel aCustomerTypePanel;
		aCustomerTypePanel = new CustomerTypePanel();
		frame.setContentPane(aCustomerTypePanel);
		frame.setSize(aCustomerTypePanel.getSize());
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
		exception.printStackTrace(System.out);
	}
}


	/**
	 * setValue method comment.
	 */
	public void setValue(Object o) {
	}
}
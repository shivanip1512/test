package com.cannontech.graph.menu;

import com.cannontech.database.db.graph.GraphRenderers;

/**
 * This type was created in VisualAge.
 */

//import com.cannontech.graph.model.GraphModelType;

public class ViewMenu extends javax.swing.JMenu {
	private javax.swing.ButtonGroup graphViewButtonGroup = null;
	private javax.swing.JMenuItem ivjRefreshMenuItem = null;
	private javax.swing.JSeparator ivjViewSeparator = null;
	private javax.swing.JRadioButtonMenuItem ivjLineAreaRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLineAreaShapesRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLineShapesRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjStepAreaRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjStepAreaShapesRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjStepRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjStepShapesRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjBar3DRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjBarRadioButtonItem = null;
	private javax.swing.JRadioButtonMenuItem ivjLineRadioButtonItem = null;
/**
 * YukonCommanderFileMenu constructor comment.
 */
public ViewMenu() {
	super();
	initialize();
}
/**
 * YukonCommanderFileMenu constructor comment.
 */
public ViewMenu(int viewType) {
	super();
	initialize();
	setSelectedView(viewType);
}
/**
 * Return the BarGraph3DRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getBar3DRadioButtonItem() {
	if (ivjBar3DRadioButtonItem == null) {
		try {
			ivjBar3DRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjBar3DRadioButtonItem.setName("Bar3DRadioButtonItem");
			ivjBar3DRadioButtonItem.setText("3D Bar Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBar3DRadioButtonItem;
}
/**
 * Return the BarGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getBarRadioButtonItem() {
	if (ivjBarRadioButtonItem == null) {
		try {
			ivjBarRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjBarRadioButtonItem.setName("BarRadioButtonItem");
			ivjBarRadioButtonItem.setText("Bar Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjBarRadioButtonItem;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G88F561B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135DA8DF0D4559586C990CA88A1D00890EB824121AD232821268E362925B3CC4111DA3458E6ECCA19962751BA1DF40A95474BAE1100810284888DCE290220060890A0DD8379DBC4B010CDF6498F84495B6CCB3230FF6E3EC030E56CB9776F3DACEF135A8AB3E76E3D5F39775CF34FBD6F5CF3B610123F27952C31149032260070FF47569032FAAFA12F7E3C6012408AC764D909615FBF003610EC47DAE8CB
	01D7BEAB4FDACC9EBCDA8863F298BBB2722CBB203F15BCFC34F48FF4E26C73625B043C1564AC9BFA1F56A3E21FF5F4FC312E885ACDC08781F381250D407DA3F5259C9F0071A65EC7DAE3A144BE92670C6DA8B560AB783CD26015832D24F3926BF6F1FE820CF1CE8634D9A3595CA5502E275A455B2A783C4E09E9A47F2715160D3817C13E2003F73035AE5E6EA67329A12C6441F776C3DB6D385AFB22BB682F68D68201E0C0D33B7BAAFA43CA282F422F867AED36D62FFA7DAC74965C017A5D5FD1C85702FEB0F60C
	C2EC5F8534C9A174F783D50D92BA7EECE3B557E17255B412BBF3DFCC9DA70FE23AE50D52F54C1CB052529707729F26B647F52B00FF90B0264B2BF5C217B9F1C2174D6DB8F7850CD78315448B7CE90A1743588E5499AFF4BC3ECF68587E2F7B49AAF71BE5F38C9D35F826DBEA3C2E636211A3ADBB29DFACBF0AF73B8CF82B010AC767D9AB01DCC09AD09975473B0F3C87ED1DDBEB887A7D41C0DDA8D46F73044F86BD2A8F7AB69BF0B85622F687439EC2184FE5F6141B68A3A8E174E0FFC3E2E7EBA342C766345CCB
	B46D2C254064EC19CF9D3524F199192375332D9C0DF61BCE6FF6A974F19D2EE7783D94AF3FD34759DD9D20B80EEB0117F12730F37BA621CB635AE92478438E4BBA93DDD29EBBE7A99C43F598236B42ECBC3F8AED5C88F89F5088209420AC20E5E3040DFFFF6470F0B6BE8DBE9E8EA8BEE73FBB2404357AFE2F4F2306392D9F2D9B4A56E64456AD7A4A6B18DDE69FB65ED14EC36D26F64919FD5C124F6DD1B4D6378B5BFBEDFD2C3D4B475E6A9B1B649D0E225FC46DD8F607D6090F27384300333B5DC6F19CF781EF
	81901BFF2683717D5FE07357C8B9CE0AD79AF0A6A727DDFC4F35404BGE277FB4106312FF0DC1EF58FD0B5108BC883CA824A98A76E7850518F7E4B6F48E138DF36768FC737C32B8EE8E165F4A022A901EE35D589FB9537CF0D90BEA77FA85A55BDE85DCB747B6E06BDCE2981B52CE85EE0G24ABFE951CA94266D80EED41B9E115F2DDDE3F8A9B78C3C4485DF0ECAB34BEA5221D8FF994CD55E7B07E9B4750A72EA92DA09184C0BFFF9C5AEB910D398B201FBBCE586A4E0F902BCA007816A06CB27E589B50FE8A4E
	2D7AECB63B379B55D042B7040F26DF40B59E18BF10A03E4B975A057F59B74EA265CF35197A9F0DFB89BCC6A4C47B5F9CAAB787F0B2DE3C1307AF34FF456FE1B89A607B227EEC0F462FFD0F825E220FB3BB7F399AFDF5B71C2B9A28F33C307542E33B198F56AB91D5F8DDD3989CC6756816E7F3FFD205EF0AFACD8DE8F6B52486BC500970BBDFDC35DE77471E207415A7A90E7129819CA6A271E5D5DB988EB20C5EAD7839142FE8DA586B6E57D4272AE95EC02F3EFEA555A5B2A874C578D97FF29C4F2A41999300D2
	65D9BF7F67D603DFB58603D77BC3AE7CE238FF1655637D4F05798B657D0FEDDFBB682EA651314EAFEB7F3A6FCE103EE765ED5B837D036D6168A0325FE168AF3FEDFBEF12FB3CFCDB77E0F76F3145765D2BB6F35F7D5CE66E3B57EDC37B6617B6B35FE43C51B67CB603578386DF9FE743F8579D8C84D47A4149A5046537AF38709C5E00D7EB73FAFAD54D697DC205F2A091FDF6B90D29F9600FA4D144426BEF0BD838670BF412FD7910E93E14B22B59521E48E211AFD10F05CC27040F709BD2FDAAA44F70BE7587B4
	06A753F84D7188536FD489E2A23F3652F92DFB36C1FB26C571F803757D1A968CA06F34267ABFA4A2A63DD703FBA4405EE9C0D90962BB5DD40DFC711D72E05490747918F90828C31E3DE02CC316A432BA26F0C21EF59710E7824E59F92944B027F2821B237B454B5545F8D76A677DAABC21988CBED5EF08F770789BD13CD645572F3F2F9B0E3F0E36B34491063FF25C5CF6569398BB2ECA796BCE3CC6701DE4B219AD67AAA8B394767AA16CE2E064E7BDD4EFBC6B9CB88756E7CB20DD81341A1E75A5D953E0DE4130
	E27A269500E3ECA990395A59E2E34D65091CB1758F245AA4DEF3A5C567300B1AD12E86F8DC12383711A70C7135AE9289F6FBE98A416DF2304668BB8CBBD233815A9EEFB882792CC79D90367D2886F5459C03435CBE9D9B307610351E576D2F683F662FD05CDA056A71EAB65B997433D6EF24DF71D17391D6E3ED3FD848E524A409DA2A27936FE9890CD7821109F9D63647F35B5FA23766E5A7C1FCB0AC136FEEF4C4E64F1B9F9B6B43791BEBAC2F4730E769C4E6472A093ABD8B6F98E9E17B2E5F31C31C6D0AF2CD
	992808DC0738420F444F11DAAF4EB1B0D156048DF80E39B0DE8814BBD1440136C346FB4E4EBD91731ED7F1FD0A12236F791293775B8FF8F53238676F55ECB15CB354F810FE7602770B3BDB52ECE5FE4E47CB1B8B041F9391734BAE1A657A8E3E3FA77956FC1F65623F24F7B6109C1D237F1662F14951B93A2FD36468C9404BCF96F967B497460576C9101783A5GE5GAD18A44EF831E5587A6B64409075D77AAD354C8A103DEA12F99D231FEF0A9D75AD1A4C4E31D3625F2578A7861C57A0746EF19C87BC47B745
	793EFF9E6FED5714BCEB7994F126F9AD9B8D776688FA831A3CB33793E3D1B06C6D75868456CBB1286069E345F224C374BB2A86190DC08C1BCA31F6980726081CA53713483BFE4E508FF75E7EFCE25CED5D0359E606ABF6BE71ED17F9BEF13F4BBC1F18679ABA97FE5495AB5FE0764F22B1E1B258FE363C13B58D084D0771E2F9A729760350AE0371AA205DD3732CF62090D092D096D08ED0E1CA1EF5BF50A7C01E943176813AF6840C53003201F20148DDA0872896288BE884D09AD0B6D06EDDE26D7206EC037DD0
	667FEAFB5489750F46718C46F18EBF93766DD13C7D067D5186E80B2C29374AAC0C52131DE51B5DF8161D7F079FE12C371A602FC70D716EB7727E7AA85E36A87DB04EEEB61139555407FFF09EE349009CBFC647B738DEDB49690B182FE86048E49A7E26BCC03FE376D6BDF09D756C89ABFEF0C5E7C86DA6A953506F6E3BA872947BB4E63FF65E8EC04B64BEC17D559FD0A130FA3BDF246BFEC7316701F2653C5206417BCF273F25B35E9FCF61199A3D815510E2E2FA49799DEBF7F23E33CF89291168D9C2CEF6718E
	BE2FAE2CAA6632261EF853B0E7E8F9B77F06FAB9B5B5E4AEEB2B3A1373071673424D9DFCDEEC3D8ED43FE918B3343C64F3184B56AB61DBA6F0BB78CB987F30BDEA86637F2699FD90F3FC278A11CA01D4C84A4EBC01BE5722760455C81FC873894B3F1F378F4A3F27335A3AEF3A484F2D674C787F167CD1CDE67CA449BF6DB063E7C87E56E6B3FE1664BFE34ADFAC7977341871F3A5DFF51A7173A57FFD17993FB0D5705BDA4D783BA47F4DB6A37FE0AA6353B84A4EBF085F4E79A3D2C58E7844C5E38ED8BC2D42A2
	72236898B1B91549CDCB55F3C016F3AE256FBB2ED98A3C223B9133F49A77459875AED0859063D78E62979878CC2FC5FBC54E3CFA51EC621B77F14C3F29386E66710337CBEA4BC8ED69E58B733DD76A315DF7ED9F171BF65DF85E2A8D7618B96FDCAEEF81EF2B43E5BC36BDFB609DE21A071BD433223E58F3D264652B6F96FF57311D9375D865378476C81340FC929B6190D806444EB48BACC7E245AD82AB39C7E0CFCB2CD1E229CE0145C94C6D9258FC09A5368AAC7F9ED17BCC54F6191FDDFE76EC5E4202B273F9
	52B07CECEDE20F72B4111B16DD461BE2EF4E2FAF6D453822DCB12D324E0068A6779528B0DCCAAF91EF70B3263543D044FC63C76B044FE5BEB203E40DFEADE61D66C9E3BE91475B3E1AB2795EEFA1DA973EF11940DB82148FD4148EF990D0D53A483577FE06BAE215G872A53B4253B0FD63D62E498C9F5989FD0A6FB2A0276EA84B9FDD2166F3377E35988C0C7B898D2435A8D0652DFA8381D5EBD09BA0CC99F1C472426474E3F583A1AF3E6761DBB6C3A9FB6192DDBB06C3AF807593AE5432EEBEAB6DB17B76C3A
	EDADE66B480C61567D4AF4DD49306B521DE66B768F3B6E7173E66B2A07DDB7295534369EF6DDFC1B59BA4F906B184F4EF3E32C9F00F9130132009683654E907EBB6DB27FBD4564BBB86969D59B0274AFD81A0889B73BE257D6F1DD437F5DEBFC977E26648EC21C7623FC01CF6A42F3EA4190859916520531814A5EBE0DCD6578373A70BB7429BD94E6D82A02E716F5987B54787C198A5AGFF2375AABE6F9774E7AFA7CC8C7432FA2C163E8F2B41A645B3C50D16F09631BDB02E14D8ABFDB3EAE16C105853CD826B
	14D8AAFDB37AE0BCA0311EE6B1AFCEE265AD82CB12581FA416AA31AC2740E6CBEC45F921DF26443E53AA764D56F5E99373964F94F77E28BB769DAEF08FFF070F3B69EF05E0FB570D90377F13EE7A9BC5CFCFC4153E728BF73144CEC79C410897AD4FF203B6EA73080A3F9B2B9ED6E784CE0A77E859CCD1E74CBCBB384E70FB8891392D7F5CE05EACF491427AFF6D5167BD6498BC6FF41FBE4F67557B932E687DA21F5E7F402F777B82FAFFDCD06F5F9752779B43F48E772B6C7F88FD5A748434F31EA7859AD90B0F
	0BECB6E061327B947ECF980DEC4141872CD49058E60AE189A1106D92091A3A810B8DA98B875132A8B6C816C422262E07E2C3740B8E5BF79B066BF8AD415F6DF73FB466F42B6277464C69C233783BBDAB7AF74C076937118DF84EAC61236B1A8F10583907532F78FC1A623BDA9770E09ED2FFC3D3755FEC66B7237C081843190C7763E6547B0AE14DFFGD0CB8788B091973B288DGGA4A6GGD0CB818294G94G88G88G88F561B0B091973B288DGGA4A6GG8CGGGGGGGGGGGGGGGGG
	E2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG628DGGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (6/11/2002 5:18:21 PM)
 * @return javax.swing.ButtonGroup
 */
public javax.swing.ButtonGroup getButtonGroup()
{
	if( graphViewButtonGroup == null)
		graphViewButtonGroup = new javax.swing.ButtonGroup();
	return graphViewButtonGroup;
}
/**
 * Return the LineAreaRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLineAreaRadioButtonItem() {
	if (ivjLineAreaRadioButtonItem == null) {
		try {
			ivjLineAreaRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjLineAreaRadioButtonItem.setName("LineAreaRadioButtonItem");
			ivjLineAreaRadioButtonItem.setText("Line/Area Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineAreaRadioButtonItem;
}
/**
 * Return the StepGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLineAreaShapesRadioButtonItem() {
	if (ivjLineAreaShapesRadioButtonItem == null) {
		try {
			ivjLineAreaShapesRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjLineAreaShapesRadioButtonItem.setName("LineAreaShapesRadioButtonItem");
			ivjLineAreaShapesRadioButtonItem.setText("Line/Area/Shapes Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineAreaShapesRadioButtonItem;
}
/**
 * Return the LineGraphRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLineRadioButtonItem() {
	if (ivjLineRadioButtonItem == null) {
		try {
			ivjLineRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjLineRadioButtonItem.setName("LineRadioButtonItem");
			ivjLineRadioButtonItem.setSelected(true);
			ivjLineRadioButtonItem.setText("Line Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineRadioButtonItem;
}
/**
 * Return the LineShapesRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getLineShapesRadioButtonItem() {
	if (ivjLineShapesRadioButtonItem == null) {
		try {
			ivjLineShapesRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjLineShapesRadioButtonItem.setName("LineShapesRadioButtonItem");
			ivjLineShapesRadioButtonItem.setText("Line/Shapes Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLineShapesRadioButtonItem;
}
/**
 * Return the RefreshMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JMenuItem getRefreshMenuItem() {
	if (ivjRefreshMenuItem == null) {
		try {
			ivjRefreshMenuItem = new javax.swing.JMenuItem();
			ivjRefreshMenuItem.setName("RefreshMenuItem");
			ivjRefreshMenuItem.setText("Refresh");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRefreshMenuItem;
}
/**
 * Return the StepAreaRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getStepAreaRadioButtonItem() {
	if (ivjStepAreaRadioButtonItem == null) {
		try {
			ivjStepAreaRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjStepAreaRadioButtonItem.setName("StepAreaRadioButtonItem");
			ivjStepAreaRadioButtonItem.setText("Step/Area Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStepAreaRadioButtonItem;
}
/**
 * Return the StepAreaShapesRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getStepAreaShapesRadioButtonItem() {
	if (ivjStepAreaShapesRadioButtonItem == null) {
		try {
			ivjStepAreaShapesRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjStepAreaShapesRadioButtonItem.setName("StepAreaShapesRadioButtonItem");
			ivjStepAreaShapesRadioButtonItem.setText("Step/Area/Shapes Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStepAreaShapesRadioButtonItem;
}
/**
 * Return the StepRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getStepRadioButtonItem() {
	if (ivjStepRadioButtonItem == null) {
		try {
			ivjStepRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjStepRadioButtonItem.setName("StepRadioButtonItem");
			ivjStepRadioButtonItem.setText("Step Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStepRadioButtonItem;
}
/**
 * Return the StepShapesRadioButtonItem property value.
 * @return javax.swing.JRadioButtonMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButtonMenuItem getStepShapesRadioButtonItem() {
	if (ivjStepShapesRadioButtonItem == null) {
		try {
			ivjStepShapesRadioButtonItem = new javax.swing.JRadioButtonMenuItem();
			ivjStepShapesRadioButtonItem.setName("StepShapesRadioButtonItem");
			ivjStepShapesRadioButtonItem.setText("Step/Shapes Graph");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStepShapesRadioButtonItem;
}
/**
 * Return the ViewSeparator property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getViewSeparator() {
	if (ivjViewSeparator == null) {
		try {
			ivjViewSeparator = new javax.swing.JSeparator();
			ivjViewSeparator.setName("ViewSeparator");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjViewSeparator;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * This method was created in VisualAge.
 */
private void initialize() {
	try {
		// user code begin {1}
		getButtonGroup().add(getLineRadioButtonItem());
		getButtonGroup().add(getLineShapesRadioButtonItem());
		getButtonGroup().add(getLineAreaRadioButtonItem());
		getButtonGroup().add(getLineAreaShapesRadioButtonItem());
		
		getButtonGroup().add(getStepRadioButtonItem());
		getButtonGroup().add(getStepShapesRadioButtonItem());
		getButtonGroup().add(getStepAreaRadioButtonItem());
		getButtonGroup().add(getStepAreaShapesRadioButtonItem());
		
		getButtonGroup().add(getBarRadioButtonItem());
		getButtonGroup().add(getBar3DRadioButtonItem());
		// user code end
		setName("ViewMenu");
		setMnemonic('v');
		setText("View");
		add(getLineRadioButtonItem());
		add(getLineShapesRadioButtonItem());
		add(getLineAreaRadioButtonItem());
		add(getLineAreaShapesRadioButtonItem());
		add(getStepRadioButtonItem());
		add(getStepShapesRadioButtonItem());
		add(getStepAreaRadioButtonItem());
		add(getStepAreaShapesRadioButtonItem());
		add(getBarRadioButtonItem());
		add(getBar3DRadioButtonItem());
		add(getViewSeparator());
		add(getRefreshMenuItem());
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ViewMenu aViewMenu;
		aViewMenu = new ViewMenu();
		frame.setContentPane(aViewMenu);
		frame.setSize(aViewMenu.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JMenu");
		exception.printStackTrace(System.out);
	}
}
/**
 * @param viewType
 */
private void setSelectedView(int viewType)
{
	switch (viewType)
	{
		case GraphRenderers.LINE :
			getLineRadioButtonItem().setSelected(true);
			break;
		case GraphRenderers.LINE_AREA :
			getLineAreaRadioButtonItem().setSelected(true);
			break;
		case GraphRenderers.LINE_SHAPES :
			getLineShapesRadioButtonItem().setSelected(true);
			break;
		case GraphRenderers.LINE_AREA_SHAPES :
			getLineAreaShapesRadioButtonItem().setSelected(true);
			break;					

		case GraphRenderers.STEP :
			getStepRadioButtonItem().setSelected(true);
			break;
		case GraphRenderers.STEP_AREA :
			getStepAreaRadioButtonItem().setSelected(true);
			break;
		case GraphRenderers.STEP_SHAPES :
			getStepShapesRadioButtonItem().setSelected(true);
			break;
		case GraphRenderers.STEP_AREA_SHAPES :
			getStepAreaShapesRadioButtonItem().setSelected(true);
			break;					

		case GraphRenderers.BAR :
			getBarRadioButtonItem().setSelected(true);
			break;
		case GraphRenderers.BAR_3D :
			getBarRadioButtonItem().setSelected(true);
			break;

		default :
			getLineRadioButtonItem().setSelected(true);
			break;
	}
}

}

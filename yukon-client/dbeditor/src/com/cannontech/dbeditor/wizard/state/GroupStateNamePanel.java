package com.cannontech.dbeditor.wizard.state;

import com.cannontech.dbeditor.editor.state.GroupStateEditorPanel;

/**
 * This type was created in VisualAge.
 */
public class GroupStateNamePanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjStateGroupNameLabel = null;
	private javax.swing.JTextField ivjStateGroupNameTextField = null;
	private javax.swing.JLabel ivjStateNumberLabel = null;
	private com.klg.jclass.field.JCSpinField ivjStateNumberSpinner = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GroupStateNamePanel() {
	super();
	initialize();
}
/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStateGroupNameLabel() {
	if (ivjStateGroupNameLabel == null) {
		try {
			ivjStateGroupNameLabel = new javax.swing.JLabel();
			ivjStateGroupNameLabel.setName("StateGroupNameLabel");
			ivjStateGroupNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateGroupNameLabel.setText("State Group Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateGroupNameLabel;
}
/**
 * Return the StateGroupNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStateGroupNameTextField() {
	if (ivjStateGroupNameTextField == null) {
		try {
			ivjStateGroupNameTextField = new javax.swing.JTextField();
			ivjStateGroupNameTextField.setName("StateGroupNameTextField");
			ivjStateGroupNameTextField.setPreferredSize(new java.awt.Dimension(150, 21));
			ivjStateGroupNameTextField.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			ivjStateGroupNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_STATE_GROUP_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateGroupNameTextField;
}
/**
 * Return the StateNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStateNumberLabel() {
	if (ivjStateNumberLabel == null) {
		try {
			ivjStateNumberLabel = new javax.swing.JLabel();
			ivjStateNumberLabel.setName("StateNumberLabel");
			ivjStateNumberLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStateNumberLabel.setText("Number of States:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNumberLabel;
}
/**
 * Return the StateNumberSpinner property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getStateNumberSpinner() {
	if (ivjStateNumberSpinner == null) {
		try {
			ivjStateNumberSpinner = new com.klg.jclass.field.JCSpinField();
			ivjStateNumberSpinner.setName("StateNumberSpinner");
			ivjStateNumberSpinner.setPreferredSize(new java.awt.Dimension(50, 22));
			ivjStateNumberSpinner.setMinimumSize(new java.awt.Dimension(50, 22));
			// user code begin {1}
			ivjStateNumberSpinner.setDataProperties(
				new com.klg.jclass.field.DataProperties(
					new com.klg.jclass.field.validate.JCIntegerValidator(null, 
						new Integer(2), new Integer(GroupStateEditorPanel.STATE_COUNT), null, true, null, 
						new Integer(1), "#,##0.###;-#,##0.###", false, false, false, 
						null, new Integer(2)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, 
							new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), 
							new java.awt.Color(255, 255, 255, 255))));

			ivjStateNumberSpinner.setValue( new Integer(2) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateNumberSpinner;
}
/**
 * getValue method comment.
 */
public Object getValue(Object val) {
	com.cannontech.database.data.state.GroupState gs = com.cannontech.database.data.state.StateFactory.createGroupState();

	String stateGroupName = getStateGroupNameTextField().getText();
	if( stateGroupName != null )
		gs.getStateGroup().setName(stateGroupName);

	Object stateNumberSpinVal = getStateNumberSpinner().getValue();
	Integer numberOfStates = null;
	if( stateNumberSpinVal instanceof Long )
		numberOfStates = new Integer( ((Long)stateNumberSpinVal).intValue() );
	else if( stateNumberSpinVal instanceof Integer )
		numberOfStates = new Integer( ((Integer)stateNumberSpinVal).intValue() );

	gs.getStatesVector().removeAllElements();
   
   com.cannontech.database.data.state.State
         tempStateData = new com.cannontech.database.data.state.State();

	// Add an 'Any' state to all state groups!!!  12-6-2000
	tempStateData.setState( new com.cannontech.database.db.state.State( gs.getStateGroup().getStateGroupID(),
										new Integer(com.cannontech.database.db.state.State.ANY_ID),
										com.cannontech.database.db.state.State.ANY,
										new Integer(com.cannontech.common.gui.util.Colors.BLACK_ID),
										new Integer(com.cannontech.common.gui.util.Colors.WHITE_ID) ) );

   gs.getStatesVector().add( tempStateData );
   

   com.cannontech.database.db.state.State tempState = null;
   
	// add the rest of the states below
	for(int i=0;i<numberOfStates.intValue();i++)
	{
      tempStateData = new com.cannontech.database.data.state.State();
      
		tempStateData.setState( new com.cannontech.database.db.state.State(	
                                 gs.getStateGroup().getStateGroupID(),
                                 new Integer(i),
      									"DefaultStateName" + (Integer.toString(i)),
      									new Integer(i),
      									new Integer(com.cannontech.common.gui.util.Colors.BLACK_ID) ) );

		gs.getStatesVector().add(tempStateData);
	}

	return gs;
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
		setName("VersacomAddressingEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(336, 256);

		java.awt.GridBagConstraints constraintsStateGroupNameTextField = new java.awt.GridBagConstraints();
		constraintsStateGroupNameTextField.gridx = 1; constraintsStateGroupNameTextField.gridy = 0;
		constraintsStateGroupNameTextField.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateGroupNameTextField.insets = new java.awt.Insets(5, 15, 5, 0);
		add(getStateGroupNameTextField(), constraintsStateGroupNameTextField);

		java.awt.GridBagConstraints constraintsStateGroupNameLabel = new java.awt.GridBagConstraints();
		constraintsStateGroupNameLabel.gridx = 0; constraintsStateGroupNameLabel.gridy = 0;
		constraintsStateGroupNameLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateGroupNameLabel.insets = new java.awt.Insets(5, 5, 5, 0);
		add(getStateGroupNameLabel(), constraintsStateGroupNameLabel);

		java.awt.GridBagConstraints constraintsStateNumberLabel = new java.awt.GridBagConstraints();
		constraintsStateNumberLabel.gridx = 0; constraintsStateNumberLabel.gridy = 1;
		constraintsStateNumberLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateNumberLabel.insets = new java.awt.Insets(5, 5, 5, 0);
		add(getStateNumberLabel(), constraintsStateNumberLabel);

		java.awt.GridBagConstraints constraintsStateNumberSpinner = new java.awt.GridBagConstraints();
		constraintsStateNumberSpinner.gridx = 1; constraintsStateNumberSpinner.gridy = 1;
		constraintsStateNumberSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsStateNumberSpinner.insets = new java.awt.Insets(5, 15, 5, 0);
		add(getStateNumberSpinner(), constraintsStateNumberSpinner);
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
		GroupStateNamePanel aGroupStateNamePanel;
		aGroupStateNamePanel = new GroupStateNamePanel();
		frame.setContentPane(aGroupStateNamePanel);
		frame.setSize(aGroupStateNamePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * setValue method comment.
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
	D0CB838494G88G88G5CF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF09457119844A7094F60BA61900754010D9DDDC509398AF7C5E2A5A1F73AAA62F095DCE58ADF2224C8C5E5F3F5640AD4289C49A9616D2EFE984988A1875916500ABFF70ABEA01F098CC2C22B4F2E0422C232D8BE9598E9E62511E6F7D63333528AE4146E77664D0E76A3306F2C2A564C6BFE7DF97D3A7B75BC0914D52D4933594B883165916367FD9BA12F1EA5643526FD87B9AECD4A4CA7161FEA
	00C3646221DCF8EE84DA5A931979A524B0379046DBE0DC74594C7CA370FE189467B61E0017A4FA0A0016757A4D9D736BA93B4475945071F9EF91BC5F84588AB8FCD678D0FF37375C406FB1709CECF678B502F3FC5E8A83F797F8A2G99GDBFDEC7ECEF86E271A97B7DDB0665DFEF28D597352E9FBB14A31486450B542F8956369A4AF584AC6D157F2EAA7722C83DA96GAE5EC1BE18F88F1E977B46C71A07D5FF6D3090882881DD9A9E2D1D126F891AD89B5285DDEAEA6A5454F0309F5FFB853F54A784A48578D6
	D0595B3D05D496A1DE98D74808FB75BC6AFB85463BGF2E50E7F1BC6445F057788408A192F7152690B469A3C77DFA0B95B830E03C95618A63335ED1523EBCC8D7F2660ED3A9FF71A516FC5E3197975GAE00D100E5GE9G6F52B870DD7AEFF8368E69ED2A5F2F86DA0341AB0A28DDD5C5C9017726A6209838EB52302A0904303DDE692DC9E04FFC40781E7D58FC4CA7E967793E27DFFB1EE4FD3D5BDE1040A7D9E97576CDE34C972F0EC5FD42F4E7A5553DF7ECEEDC4E55FD1DD8F56FDA22BA7292685E306D2C3D
	E85C50B99E555DB70EFB5DC3773A855EEDC6FCB07CA04527DA70ACC6FFC2E386476B00D6287058703E49ED6949FD1A14B474A474432AFFEC35AB8A33A1C509356570FBA8FB3D42F45ACCDBAAA8BE4302E736BC574873E5B350EA7C1979AC3EC6DBB0E7D740B89DA093A08BA08720BCG7382BC46DE38D47708B156A7E87A15302C0812C6986FE8736F61A9C5F4CDF087A07F8243522020494210A20508B1E72299FDB0B407EF12467EB06868148212A668329AG69102F3A2405581C5966C3B8C713A8F5C076CB20
	409FA4DC6E52664370D404107EE1D004EC0F4EE074654D38A71342A0C88481704E7C3232957D55836B9781F4538FEFDCC6F95D128685232929C7D547434181DC89498C201FFFC9EB47B2F8DF9360BEFE728A628A554CFC274A6534DEAE3548F14943E83E20CD739C3B5608F539D165B1739FDE9EB32E6267C84DB6CF42182135D7E53192D1E3F367B61509F631F176F9EF12DCEAD4936752472F953FCD2A4371FF147F2639D77B5AF02F56004D99GD9260FE7AEBBD94CDE91C2920F52BFE990E0129875B83373ED
	BCB68C1B0FD95E3F52184C7E7F8FE0BAB2BFD59D4C97DA8B1EBF52249450DDD2D08A087092B2E853AD98E3C6FE7AD413E7166203104FED10C0A1933F3885F3CE50F5CD9E8A6BD23F246BF2E0A4CA7FAB2AAB949E8AC6CB8157155A02F5D71D02FCB667FF2185F3E1D88D84A43AA9A60BC17FA24DC5C9117CE0F41B9A8E688C3F369563F9DC1AB6EA41F32D5860CC8ACA986B8A6B87568B87F08D1A6C072D4652C43CC14CD3D7E53945C7FBA0E6302F2981DACB10675C54F11EF3A76E2DA51B6E1C34A7BAB3D6BD67
	322F8932FC59900C669C336929AB09EDDFCDEB02018FB1DBA79AAB610915C8005E25D594B5A994824F360B322EEA3CC7E1B1FF211D76A320EFA7G1960B1DF5E06F4DED70CE0EFD39505395674F91E2B8477C81A88CBD00E3100F51BFEACF2154650868D1FB2FA198BFD2AD8EA9F333F608E1E4D09FA2F1130DC9B56E52CF4028EC1968C6BF4B92C5F7ADAA57A82E514CF703E6A07CEDCDF9F0CC78196CC70BDF9391877D52963EB480EE75FC95ABB2E1AE0FB11B1915D9367A25ED7AD6CC01DB9C0AB52386F163B
	2C353DB594D207E5FA2C986BEEEE376E994335353F81CF1F2C05F4B7A4D5047B485D0E766239E3200DDC7FFAE5EC7F6254223536D10B2F3B6CDC6F2DC6FFF4E96CDC5FEF62EFD13CE8413373FE87759F0E97GADDB63E751F7BAB16E3BC2F02E814CG2482ECGF8A5447D70F27BF97210335E8D7B2A858425FFCE1273B3374341725298FF2EA30F67A5BC59B979925D735D20B3D7073E03623EDC0538B3B0EE517999B5D00DF1AA42F899C09A4EB5E98225E543B8B71C19FF8240956634571C31B59579925547E5
	963CE8F9CFCBB2BFDA37774569581FE4FE1E655D91A3E77E3A3D39B3F95D7EC1E7623A3D2DB3F15D7ED167A106879956AE0D53FF56B9FFDD7F774EC4F55DC1E9BF2D4C2771BE2D06F578168963FE6EB5EB2C88639900F5G9B432CB7E2B5636F26301678202983D57DC1E91824CFA26F372BB8EFB90CEBGFAG94004781184E47CA71BB18FE44514FB97C129BGF7F44812A29AF6FD7E546138B9BD4290D4D2C62FB8D242693DE17F1024D1A223118FCA67527A03F2009EDC75D3E8634A6AB24BFE6D19E46B2AG
	9A231709D6FAE40A51D71B74F6DBA27AFA137E4DD9ABBD53206F05A74BF167D15E5397C5A076A35C278DF7312727F5F8CC1894EA05A99DBFF8E5710AB052C3F708555D512305867FCC04575D27284C8DB05E82109D61F5B7F20A575D74671FA6BD8B3A93565D9D83676CB991235ECEC76BEE9AFC4FB21D59D5DC67ED2027CEA32E54E9DDA77A9E6F94EA6149682EB9F40CD1BCA7237449B9F434F76D9C7E34B9F47451D9031F2D6D7BA7795AF23E1FC6143F7FE352B3A54BD89339471EFD034749094F7ED781737B
	49B3F37C9E298DCDC1D0B7B5F94C20E4BE58D2DDE8647E05FB5C3F9FF5D84F350C03676C87125890B4F42F389775EF8D78175970CBDFF9E29BE8DCB37D5DA2573F95E4B05C0C0D638A6F73B8FA703F270865BC9FD7C6EA47063122547AF0B1A0348DB302251BF1F7720BD9AE2765BE2F45270EFE32FB0646CF48677F58FC6CAC9D2D42580F5C0F7D261E2078147B315F542B4D7CDB8134BD7779991B6B41BD704E40F7A34063G6BGB281F64C70B3F6C1D7BDF94899BB2086FB2070AB16D24D5677EFD51F6C7E65
	3B85BC46B6FC739932F1492FED49E2EC4F8C0B2D320F0C3B3B0163944F567779EEDC1F8BE8F781D2GD6832C87587C91DF5F662E9A36BEECDEDBF5DD989E25DD93DF5C20ACCDC551D878595ADE2E66EB4B7AA87E7C5D8D385C87F3714CA65BD54C71F220B58274G08GA98F38BD256E0776B46D1104BD8D332BCF347AFC1DE143C6633965C13C2D0C6FF53B15EF07B1EF0F71AC1CCD46772BD94ED711E0CEBD60DCB1F8469728A2667B14657D19231F7EF76C0B1F2A8E360F451E647DD019A7F1BF740EA7F1BF64
	744C5F6F1C71A47F0E2D73D83FE3D98C3675E28CFAE1DFF400D5G69G1BE6F98CFE4BF30C45E002FCE897C724B6155E0E69DC7F1A6E64EB7DF277436F5456F7633749102A6B2A9F65F37CBFF463FE69EA10A2996E45EE7411A60F0C6AEC2A017FEEB756F3C572D1B46B53F75273AB8B5635F716776E7FB449FB7722859CF7AA00385BB05697F09F6C3F1AFCCDA5D7936F5F6F2FA65E3F0A2B737733A72EA65BDFE65F88BD871701ED2BCC1B973A91178E638C93773A1B2FED33097B1D1D7316BF46F183B791D783
	630BA66E9BB7B9EF1709ABA608DB85638DA6EE5B202943447DB60C387205508FAE647E6B6ECD6E3FEB3D8F0F09DB3D749B9676F2E0BAA8B15C5DDE6C5DD51FAFA41931A7751618B8F728CF8D4978A14C7A62FD55589F85B549A7E91AA4764B77B0DEE696224D6547D03E8B5E7BG8A9751EF79C91711431B964657B25A6F99CF04D7E04ECE006386BEE7E1745CE57AAF85705C028F86599F76D36DCC7707B45E7AC0676DC57C9B610108F7C4BEB52033B929EC4F011EEA4E79029B676860384CC578B732668FE1C8
	186D3FF3F35B8BD362ED3745583EB346760A14286DCC7FE9875DE740B726F07D63B7676A1F9C0C6A5FFA136B971368F74C23BF12926B3B93A4795EDD22F11982BCABD2785E6D9A647AB39268F74440D618034075E74769AF1BCC3CF7B534FE6C0679B9G4C8F5F3ECECC3E0D13F8188524A9A77F2429F549D0C171527ECC43FB75B54EF38DE70DBEE74D14756F0B975EF8BF613D9D421945ECCD970DE7465263C69FFBE00A5FC969C02B22B6906A9F7E1388476C7E56B5FCE7367C340877DC95DF79A2F1ED7F350D
	7BA65616F443061C45513EFE67D8CE8113F9FAA06A0B7F9CFE94DFB03EF7EEC479A65D47E279BAA0B14E9C93E5C1D1C70E55B801352171AC377D87372CFEACF93A5AEECBE2FB6392E6F34B12286D4C0FF59E530FCB783EAE3C99755519BBD17B3AEEBE2AFDCC366D96171DEE4A6E3E9B157DB5A1AA7B25412349FDC6DDF64E14DBF91D4B4DDE12F86F197C1707226FFB06233ADEBC7FA83AD84F301656230A2519799716729A6F707E8FBC8705B123CBE5773F34D3GE41F2686A5CD1FE6D8FAB3EB74DFA5D356DE
	538B721425737F9D075970E389F3FC814CDD8D308960D500DDG394B60FC832881E88170828481D2GD6812483E4GEC8548DE466D37771F1D47FE63A202B6BB1AA0E33B437B279174A11371679BA7134900EE4983FE7D535018B42C8F8848E4FCAFFA2C6B5F8B7694FDE67E75B33E7CDBD6BEA770B4BE925F316BD63EAE60397D90BE363667C5FCB77B923605866D2B744E884A238EB1AF5F23574F7DB0B1B042FA025334AE85BF436AAA4355D05CB28347647CC2CA5EA76C159E5EA758FC09FB4782DFFC6FD864
	CB5CBB167A2C3DA3439D71E1DE0DC08CF84DF995A66E06092B26B291F7DD9675D193FF1A5A0578AE8995F27C9F28DDD39467B5F067AC389B866EFD9F76BA3064D1D5633C97FD34C71095050DDB689CB9G2D0F690FF6EA239C94ACF6FBCC5C8DF3AF3ABD717B5367316E8F43D55E0A1FF76CD67C3CBFDE0F1F57FC5DBA0F5D5588270A22F1C3DD4670951162A81E3B0C307F9F5A7E668AD8F87A4FC91ECE8E51EBCEA73DA3F562A5E99360582D27D375B9A9B5549452C8E9FCCDEBFD62DF37275FF11559095DCD72
	C72992B7491BC2ABA278277837G1E825E03A62F1BBAF7476479A6474F1EF558C9AEB032E0F313DC0C986057189C87849B15028B627FB515971AA08F085DA23BF84E19714FEB5FFBED4BCF86C38E126FA6EFE37B57AFE9B423A4917F84E57C393F036C8F2AD00230918145A17A488B69530A54C34AFC5770CFE0A1DD92DBF92E06F472D68FEA2A43BFE755F58921D19D7366039E129B40FFCCA0250C2C880111BA16389FD06BED2DF72965187A846CBDCC6DC5A935261443AF9EBCD96D495E68A0768E7206228AE2
	07B08CE54FCD766B231A949AD595B1923C5CC28595522C47EE4A81625EB5170F0B2D459BC753687148179EAF5ED95C816E0AE8240E4E44EE33561CA1360D8A9A2812B4370E7FE821EAA7CC5AE3C775D37F52F964361D580CC5226B77C702BA497789FED919069797BD216926946BE02DA684C2BED57303E54E984BB849D4D07B575F2BDF79373F39C995C4D9F5D245DC892CF5A3126A2FEB75796400E48AG3F0278CAD3BC4E290D19D39AF972AD4F36FDC39087AE62705757535F8D7477B97C5DC0B18D9453F08E
	1C3B1B4C7B93CB0F70B55360B1E39C373F3683F6D07F614F3F774C0FAE6E5E852B6BA0F9740FE03089A551D9AC4916242F2CBC103A63F338C7C18C15884ABD4444679A514F7F3F0FC5FFC1DCD4A176C0FCF8A07A2039C9B8AA25D34B686FA39FFB494875B665FD573434FABECBB1CF7148B9B7E7CEFCAE53B92C77BCBBEB6DBD77AEAECF5A436FFA423813DF9E6DBDD9BFF13E94DDF091705E653CB7D9F4688FA4F9DF516F9794C5971471560008BD4615E9DD0A5EB7DCA8C579A1BE47A0B25A65D234FB4C427397
	D0CB8788F5C03E00BE90GGGAAGGD0CB818294G94G88G88G5CF854ACF5C03E00BE90GGGAAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF890GGGG
**end of data**/
}
}

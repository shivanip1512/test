package com.cannontech.common.gui.unchanging;
/**
 * Insert the type's description here.
 * Creation date: (2/14/2001 2:59:30 PM)
 * @author: 
 */
public class JCheckBoxDayChooser extends javax.swing.JPanel implements java.awt.event.ActionListener {
	private javax.swing.JCheckBox ivjJCheckBoxFriday = null;
	private javax.swing.JCheckBox ivjJCheckBoxMonday = null;
	private javax.swing.JCheckBox ivjJCheckBoxSaturday = null;
	private javax.swing.JCheckBox ivjJCheckBoxSunday = null;
	private javax.swing.JCheckBox ivjJCheckBoxThursday = null;
	private javax.swing.JCheckBox ivjJCheckBoxTuesday = null;
	private javax.swing.JCheckBox ivjJCheckBoxWednesday = null;
	public static final String MONDAY_STRING = "Monday";
	public static final String TUESDAY_STRING = "Tuesday";
	public static final String WEDNESDAY_STRING = "Wednesday";
	public static final String THURSDAY_STRING = "Thursday";
	public static final String FRIDAY_STRING = "Friday";
	public static final String SATURDAY_STRING = "Saturday";
	public static final String SUNDAY_STRING = "Sunday";
	public static final String HOLIDAY_OFF = "Holiday off";
	public static final String HOLIDAY_EXCLUSION = "Exclusion";
	public static final String HOLIDAY_FORCE_RUN = "Force run";
	protected transient java.awt.event.ActionListener aActionListener = null;
	private javax.swing.JCheckBox ivjJCheckBoxHoliday = null;
	private java.awt.FlowLayout ivjJCheckBoxDayChooserFlowLayout = null;

/**
 * JCheckBoxDayChooser constructor comment.
 */
public JCheckBoxDayChooser() {
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
	if (e.getSource() == getJCheckBoxMonday()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxFriday()) 
		connEtoC2(e);
	if (e.getSource() == getJCheckBoxSaturday()) 
		connEtoC3(e);
	if (e.getSource() == getJCheckBoxSunday()) 
		connEtoC4(e);
	if (e.getSource() == getJCheckBoxTuesday()) 
		connEtoC5(e);
	if (e.getSource() == getJCheckBoxWednesday()) 
		connEtoC6(e);
	if (e.getSource() == getJCheckBoxThursday()) 
		connEtoC7(e);
	if (e.getSource() == getJCheckBoxHoliday()) 
		connEtoC8(e);
	// user code begin {2}
	// user code end
}


public void addActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.add(aActionListener, newListener);
	return;
}


/**
 * Comment
 */
public void anyCheckBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	//create a new ActionEvent with the one received
	java.awt.event.ActionEvent ev = new java.awt.event.ActionEvent(
		this, 
		actionEvent.getID(), 
		actionEvent.getActionCommand(),
		actionEvent.getModifiers() );

	//tell all of our listeners that a value has changed
	fireActionPerformed( ev );

	return;
}


/**
 * connEtoC1:  (JCheckBoxMonday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (JCheckBoxFriday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC3:  (JCheckBoxSaturday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC4:  (JCheckBoxSunday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC5:  (JCheckBoxTuesday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC6:  (JCheckBoxWednesday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC7:  (JCheckBoxThursday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}


/**
 * connEtoC8:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> JCheckBoxDayChooser.anyCheckBox_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.anyCheckBox_ActionPerformed(arg1);
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
 */
protected void fireActionPerformed(java.awt.event.ActionEvent e) {
	if (aActionListener == null) {
		return;
	};
	aActionListener.actionPerformed(e);
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G10D3A3AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E16DDCFBD0D4D71AC757EC4CAEBBEBB6EC0AE42C9912309312A58913B09BE74AD4199DE642A4E4576C189D32E19233EBB2CE06E4588D153899EB6A52B436AF2091C4B428AD2200A2C184B4084ACB85C5EDA3A2AABEAE5D973A3569A65D973B1B206EF71E77F6F31B5754D47947D275556D73BB67BB5F63FC67FDAFC24156282C8CDD01A0E4E48974EFFF06A0ACBDAE88B3E626C7B12CE9EB12C1D07DED834A
	93EE6E5143538CF9667EA403D130EFDC89E9AB24432DC9068D70BBDFF0EEACBE8ABFC248C9EF9304365B0F440CAF6775E1A6470053EFF95742B3874806E481A5EFC5727F5BDBC471B8C867505F08B2F40290E1C2E5EC1B0BD4F83109143341F3946840B6D4E62D2F186627G064A24832D5CC64A1A60398AEBF7DF4381DA2E774198A17D55CADDB612252A1FD198650DE6F58871589119C2D46DE1F8B61AEF8E9C3638862BAC225369F24A1245D68549C11733EAE058DEB56C3458C467005DB95050F02255A6D9EE
	3638FCED223F5566F2F9A4B7383784697D1D4DABF11D20B7480942580AAD48D7BD70FB88E8F689537D0D6CC62ADB444D98E17193DBC36A3E2404683CF23B227BA25BBB7AF95BD17DF53E35142F9D52E1C0C4175BBE264B726DCC17D1A3AADBDB1AE468843216B23CB58F6123100EG0AADE5BA9EAAE3BA76FC7B22F0BD62E72BF3C36818D0CAF4CBADD5F4EC3F7C0EFEA30E170BB8AE4AF700DC206BC03300220192003E40F1DAF0EB97BC1B7B64D66C76662121960755FD4AE5159C703B2181F2A87635E4F1392D
	02C0E271214D159A7AB0CA599198070808ED978496BB31DFBFAB18DBFF3EBACB433678B72BF4E9348E7DCE45B673CE643707F15B16426F8C2C8B74910CBF09718895CE5AEAAD46D1FA8164F516B1BF7764B0DDBA750F8B463DE7F5868DDDA27F7918AE390C6810D62668C2FC3C4F0FFCDCDC8EFD88488CE4831A8D94D34EFCFC75563E09FC5C81316FF60A0EF343FDC322DBEE9936BB2C9060445787FD6379DA0B88DF47147908DFE276295BA865853326DFD2E26AF48965449FA9650ADF086C19A1E5AFAF9F9B9B
	B93C8D53F0DFB1D610B64C64786798B729F05236B3B10E5255109781C44A7F92772DE59541B1328463253B02E324A30F45089972D200C87B260C20B151369B7A8E50DC20F820C5C0E9C07ACA5646CF5D7AF2127D482CEADF425B67AD042764135DE20753A30BCE0B54AD3A6DE21FC37288344CCDAF72EBDFG5F906E5F9610F1DCF2CAEED1363B1CD03BB8A8C1B0F9C8993F9759E4F1CBB8374BBEA8010041A10155BB431B8FCF0768114F8FD9C5D9D2CA107C873CA8A6EE095DD0A3D4G3F3BAA113F1E46E3EEA9
	7CEE2AE43EDAB50A30C8C847F13F68FD6B60F99202DBF2B4B43459ADC88D516DE7B1BAF70B891E89D05E318746502AA443FCA06B17C906355560EB205E3D2C3FFE5C436232AD7B9921724DB31AF109470C9A92E345B541F1798C1657863895E896D0B4D0825092A07D3EA4C369BEB65F2E5B9296D4AF5293692746101E6CF7C115F6ECA35B109D1369037134BE648325AF570D51A51376D7B5162D7AED88D1FF960D73496812CB1FEB6813343377B66AABED60276BC0E17BD8DB9F7119C89FE891BD920B7AB3EE88
	D8492A34BCA95B68C7F5CB37A4275CA68DC9CEAB7C705018EB752FD27AC33F0B47EA87465178588A816B617899FF9E41218EF56FE27997F03EA84BEEFB5F30AC1D13E49916948A7FB52C0BA7E06875D0DB7DF7102D11E0E39C5082EE6BD2FF3EAA2EBBDD2E1B43C3DD2847527E7188DE87AC027229BC1EBEB522751C31162505BC14D672E79BFBA6E88F44BB1571FC6C1C705804B2B227D93FA2632DBC9487EC1C4E2278AA9AC759B42EC8598FF159EA302B87A88CA886E8A150B2A0D3DD12213D0E7967CCDE58A4
	6A1D2E6ED333510DF599CF0F497631BF05C8ACFEB0923AAFFDB4225D17BE99516ECB7FBBB2FEDF4978D62B2F103C155FA2DF383CCED55F4B7E960D7F961753A96181003388E49F70F19732436E344B976C5681C9BEE79F11203F5521BE74B21EE30660772CBAB687F83733B920F464A9E14135DDBA2D75E364BFF468D26A681A2DCE198308CEB92328CFCB8EE9909C556A9AF64AF4AD0C67AF0AFBB2B1966FFA847116736D47A91753AA7AABC5ECCD95F918A5F73F4BBDA8D94BEF083744AA51ABD7615628EA4659
	C7516F8A531EBD07CD9A3B18CA5169E7683566207ABAC3D7F830223B221A46E03EB0F3EB101E475C76FBC74F3DDCCF47796F866AF9CE14075D770E26B5D44F140DC17EBCB7FC8F357BBE6E4FED7B9E8D5433EBD8725CBB0A56B2BD1F3FB8B7D04F8B1255F9AFE9DAC774EC4F8F0A4FAE5B307B9ED2331E45E7D5D03B373B9C77D0075F4F560A31F771FE2E9E5655C00D75EC7DB4E79465337593DDA4363A9C8EB2CDC0DEFCBD1A835E5FD2209A573B6A4939D794BCC97EB7DB357217737C7F514CB76EE7799E13D6
	FEA94FCF574CBF40730737E965F7727CCFB5732F73FCEF09D63E3C1F796976EDA31AEF25EF06A55896228566C949CF77F633EE2F8B4A6B969D434A1EB17CB6DA0FB8D4EBD3025F3D23BD7F5DF7872DC7EF727A9F3833C2C0FBBFD2A7192F63C4D467105BBE88CBF9345826670BF1BE757962EC509F1DC54642F38190613D5F06564CDA87F91187D09DA56B5879E3B2702481998F10FA4C87489CCE4AFD404BAD01747283214ABD1A4F4A95FF15E4287DAAD43953852CDC9714F104AC27DB4F4A05C3196810657E09
	17CB00B2A9A14B5DA8E46516C3190206D06536ECE065EA21CCA71023A130DCAC62951279B968179C7BBD467642827523CCF63EF34A28BE3FDD76709D4DB594DEBBB550B338034167DE6F89F87F8FF86DC1962B19F755FB22E60F47E53163E3879AF74F5FD52F2F8876625D55706C373BBDF287ACDCFDACF6DF3A0BF4C56792942671770F3B8F89E34FD90EDA6D324B4D361C2DDEBC8EF539DADDD6299D423F15AFBF3B6D12D7E06746E5DB193F5A8F3275656DE36A736179397BF5EBC278A60C7AA4CA651B45175E
	51133AE3F85D0B8F3233618A93430A9A9976B4474C9C5B3D0DE133B9964B31859C5BD34230E59CFB16E35507D83F7B248275CDFC560EC6EDDF1547CB0E544DA23494615B64DFC9FDECBB444E27DF4CC263C494246781ADC4CFFCD6F6F25DDADAF6A9E02643ECACFBF805FAAC6BB9CC763EE17024EB00F5B5701C7A84455AFE34298171CFFBD6E2717FF8FE9355B169594705463BC5BA2D73755939A53A084324ED679FD65A587A46282EC77FAE3D87484DE73ED8F9A44950F40479620FF5DA3E709DE1BE7CA9678B
	87AC0173C567E871AD67FC5E8246E7EC023DF19363DB26A9AF2C09716DDE4F78A2GCB62FCD9BEADBEFDB363FB17739581E6EEE6FC9BBA3478E6F33E078A995FDC4096F1BECF36965F5A9646F7FC8363B381565342787ED353AFF3DAD8ECBD3BE13A31C56FA5EA54E362024575A147446896928B73DA02474405BAFC6683785296B6A69E4067GECCC7C9AC6A151B9GB3B15F3B633E8AF3AECDFF08ED47F32E404E9AAA37CE659CFFF98BBB0F4D56A31F99DB138C15C0EDC03DC0232DCC37510C896FA50E7A4639
	17F8E6CAFA4DEDE567B8D526297045F13E6726441740792A37CD05AF19737DF0CAFC291C2F26E4AAFCC29B63FBE1CAFC851CEF2BBF182F3CCDB9A7ECEC9BFBE6C86EC20E2FC0FD4D5C96FCC7520371513660BB121732589DC98C64559EE5724F2E8B163F60E8204C1420B4613BB606EFCB30EF0E857211F960F0B70ADF0D790C4D8BF4FEFFFD856A0F68BC2E68986B9316AC34169A05F4A4D09CD092D09A505A63C9066A632CEF3C1621354E886C9B2196994A79B331F3FD7E747C33FD8CDF30FF4C478379085DF5
	754CEE5BF156C63F18268E3F1AD007A4CD9D5E4EE1BAAC61BA5CAE181E8EFFBD06AFD80776F6AD9D3E64FE385E4EF4583BFEFABA9C9943972CC302268E0DBE26C38A57617DE96A704904BAB4F5E869F03A0369505341F4F824F0FABABCB906AFD807791ABABC1D4DF4D844F5B835E1FABADC9E43972C430193DABAEC4CC5BA20795EFC020D87CF60B1E2B624A3CF303EDF0E5F89E1F33659E5F74A821B2F5F5421BD314BED9F30BB99769F9833385C78601B5C7F346275FF8C5419406BDD2F8FFDCEDF2C57BE27AF
	51EB1F53176B473F732A5607BA47A77AA5F07D120188F6544430D40E7D1CE342C9069D5F4630820EA5F22C1CE39DA58CEB64582B9CB3F32C9E6FBD2C1076F16CDFB291B6975271A7F93C2CB041B3995269C046CE982F014CC08E20F02018CED676AAAE0B4E9022699E9C6D2F51D9C1AD5D6323FDB4A97B532CB0DAAF0A95D407BAF69C1DE367CFC4C60A271FD4722BC314D513D923CCB8577B832C8B3B038B3EC34A8ED1A73B6B4DD2E141F743DA34C683C3B13A996B700B42DC9CE39E0F644113DD1BA80BB46FB7
	72FAC163DE6734B8A8896C4CF7763AD5344CD159553A17775FFFDB91FC8719G76A7D33F24067021F0EAACDEGD8F990CE62FFB0ABF4DF1333347B5AC896722F47B56C36C8417DE9C6D6914FD3E67A8ED99AC4A613B2FF0579E551BDA00D690F8FE2FE12970A7F512CD0FD167833B3DF6D4F9A6E4F067AE0FFB602CF4C54AFD68D3FA172E9606100C58561C436D020163D0F4BDE1C93ACBB9E7893E99DA9A1E427E9607A53C9062253DA326383E457F25935E36C2E847EA6DAC757E9ED59B68DFC9430B91A32ED6B
	55326B386CE6DF306CE8601FC76BC88AA1FB31861E8E584AB3DA320B8B55326B396CF39D41320B013F1A5651F6C6DBF60F866EGACCCD37642G597B396C673203E5C7GFF8C2DE3FE88598BB570A500AD5314BDEB03DA7681AEFBCBAE1A77571A959E937C2E2569F673D89924DF452CC07B989FCF476234DFE07B89299915A5FD732853CAA7FCF2FEFA894F132FG43D3998CFB9BE30FC17AA247FA71BC799024ADBA067D9AE34FC17A8C472E62F972C5208A4E7BAE46DE07B21FF16CBA1EA7DFC67AF15E77B0F6
	904A7CD247FCF54A0F569E8E516F9A861D934D281C554F4E62FBA1AD834D850AB313B393E2738F3DE86C6FF70B03B0441C9B12ACC274D9FC5F1249F80D10AE85EA8332824DBA4B4EDA9E3C064656209BF12A4FCF060C6ABCFA9BC97206372DD367719BC0E64B8379F9813C74660D47C5E3EB00DCF2434D6E972F857032DBE54E9B199340CBEEF3E95E07B783F426B7E9E49D120E570729E03B70B5DB1B9CB2A02CG52259CFB9CE35510EE67585F6CC0D8AF24FD9C5B39012DEB42B9F68CE3F110CE64D8EFBD131B
	423128FAA6B70DE3997B91369C52856798768A5EFB15C33A0DE32B6A195C9E0ED5E0EC9452919C5365B239B19CBB164B644E675813394C5EC40E6D2EE77626F0ACBC0749CD63D8EC8E3337200BE15D5C5EF20EBD4A6DED64589F6A193D6D9C0B677676F26C3F6A185CA10EE5AA76F24C626376F26CC99F3717E3AB7DCCEEA2474E6EE2F2D3B8A696F2FBB9B6231049ADB84F30618EEEAF47FE5C416D65582693BC2EB876D98973F3AF4756AB76F22C2E0E79B902E3EFE4F3FB955E9CEEAF47EE65707665D8EAADEF
	DF0E9DB7F2FBB9B6E0646D5B4DF54171D28E69A60E4D45D8A7242FF36CFD0C49101E453193DC461CEEF6CE9C6EE76F5F4783165A4D4635FDB5E8DF474F065029EFC343B1074B5BC907C83A97DC045B934DAB259758FD479719284EDEC84BC06197581D10EFA73B930A7D4163C2E718D9731B0145DD7BF55197489C14F8C1B99B6E3D4C6E045668D7D2BEFD8F34F18F423E522B4F3551F8A482D5A19BF07E4D00FCB446D584641F4BD267A3FDFF2BD3675F97101F2562A7363D360359367C35B841712343A13F0718
	55C377C77419B83CDD103FFCD7CF4650B5F87C9BF4CA03AE275D1206E517E06CED641B9EB6F7F499509D338C0B6F5A0B681B17CF11B8BA8E7F58006624C151E79F9C9E44AF0211FA5E407578207C2C0B2C1E689D08F148AD75CBEE37E4A52F0D5172AF61F1B78E4A4E87DAFA910F599B0AB4EC8ADCF7DC5900655B1DC172077178DCFD895A8C08E077613164F5A09BE044160D390AADDB7D5A366C4263358FF8E6DDE23658F60536A50D0C15D0F67EA5E60B29FEE2DBA28B35ED799A6B3D946ACA67366C46FD6EB7
	A8DEAFB3DB4E22A91172ACA4E3BD64B5DEE6BCDD389E9B24654BCC2FA7F246530B703D096D09821ED8DE57DDDC576F001239FCB9CF11DF0AFD36947252B94F2FF1BD265EA4C3E5AF137FF95D4472AB312C9E603176323A4271F87FDB20B93DCCFE74BAC57EFEBCD6C4C1DEAC671901477EE4C8AF6172FD05934B0F43F30171CA1221788A2B6BFBF85C7FBDD07B95A67F15FCC5FEAB1E83FAA04F4AF9CC5B91B68752F32FB0791513307FAA1E939201E7912F6BF3BCA73C0184DEE572F357AB723F0FFDED043C622B
	0C074CB76D106E3A4A64DFB5CEACFF8E9E4B4300A77CAA0B7F4EDC942B324B6568328FF54150A0909DEA8A157E74C26E587165D50CFD82B40F5735BCB7F0FC3901BE479308FE62387A11BD465F0D39CB190888DF54B4795E8A60CB3C9AF47E097C9D0411BB1A3F45F3157E9A39234960F8A4460DAA1C4C175BB259FCD98CF9E1575879E7CF91724992C88B57E17F8DD48DE486F2DCE76716FB8D68BD53F16FC23ADCC31D52AD4921BA0AA4769975537B8669E5831B1762FF7234B06FAF7F189172BD0C6B74BD8C7A
	3CAEEF47B8316FF79B11FD8BA1AF9548A842FA8B289128CBE476798D1544BE749AC933AC0B969BFEC3059907DE9AD1E034D9A23176950E393241B22AF314D7FD07FCE0F5E348BBE41BC8F49D254F5AA73753BA7E75323A8E6323BBF599A16A08273C8BC4E52DC1E27E41E2255F4C5C014E461D125744AF415B6CE0867A18277C2B3D15261A9AD890903E784D8A5F979BA643C7F4CE3C22DEC318F62F52DCC3AC3BD22BCB273A9675A9BA274DFDD7CF6497EFD564AF75CEC6BEA9BB379E37C3FF99356308C929E7FE
	4E646D9F5C26707D31EE72FCF3CA943E7BA7A50F14ED4945FAFB185E1FEDD76AB155CFDE7E53BB943E99D33077751D8A5F5B13321714FD1878FB18691DD626547354946467D7A8FC49D370F77DAE056FE7D3707793BE2C374C743E38DB2967582E494B9F2DD4783A8BA74F777DBD8A5F676D13577BC79DD8EFAF537B63AA251E5412494B4F2BD678D6CF415F35FBD576E6CFDE6F3C9C2C370D69ED29D16AF92FF67272CF287AD723F18A7DD9551F7EFD8A717D9E69177EB2FAD7160657FF1DFDE4CEA5584D91FCE7
	85185C477629AF6377FFE6C3BA320F5D677CBD2A0A4E755FD37DAE4B1C68BB2CBF05080C26BF2B8CB2F70AC5216FD9ECC55A77AC4EA26DBBCD39E87CEF0FC60BC2FFFB7497C56AEF0F485CFF28946DFDE321AD9201D20184CB12214042667D3C0DA830501739E3677C235681F44F033E4615197C740D21ED7D4346093F6155EFC26F3A7639E459B5086AE7786ACD283DE457908689163B897948ED9F304924A84557EFC2B166107AB1CC6C3C04773B55E0D7A7376D871B506056AD5E987B06AC00E637EBC8F24BFE
	02623746294F4E63B90154113AE9C728BA40C3A7209F1D693BA1D964AEF1007B5CDB0656DB8E50E3B6D0ACD082D0B2D09A57ED596672F1F4A387B55877EE516E14C91FB4606F55C407FD40095A09776B19A3E83DFFEBC09CA2FB024891FE46E2E5FB02E49DAAE363E5BE5631B285D62653515250ED5BD9BAF15B1EAF456FB6C31BF4790768FD5E25D27C2EC0FF3FC762B1F42D5448318E0F59653123FBC01A37A3C79D7F24D9A8DF7F8E944B68BBB9BBB8E1843FD6FD8E8ABA87C8FED8D9E87D6FAF1BD87FB9E55A
	3179F05958587CEE19F6ECBED6260ECDFA9FB0027C2CFC2716154952647F244C5F7DA23A7378C84812059564A681FE1023FDD9D8494E7181A3C77632E020F78A32300A5FA0C0C9F6DBG8568813EAC2CB96F11CCB465F18B6B468E7D4D5FF96BD731575A8AF4022EC3D8E11310CD9DC21657EE15EDBE7415BC725CE54FB7429D3C567D7FD4C00A2E7FAB6E86FCCFF05F66107B1E08FEFA06589F7C6EECDF950A1DA5008B832CCFBE3FE74FB8634F39C1516110C5474DE627950DC5ADFED9D2660A01AAD43F071521
	19F47E29C2FA5FD0717C9FD0CB8788338BB5550996GG90C8GGD0CB818294G94G88G88G10D3A3AE338BB5550996GG90C8GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4396GGGG
**end of data**/
}

/**
 * Return the JCheckBoxDayChooserFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJCheckBoxDayChooserFlowLayout() {
	java.awt.FlowLayout ivjJCheckBoxDayChooserFlowLayout = null;
	try {
		/* Create part */
		ivjJCheckBoxDayChooserFlowLayout = new java.awt.FlowLayout();
		ivjJCheckBoxDayChooserFlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
		ivjJCheckBoxDayChooserFlowLayout.setVgap(0);
		ivjJCheckBoxDayChooserFlowLayout.setHgap(0);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJCheckBoxDayChooserFlowLayout;
}


/**
 * Return the JCheckBoxFriday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxFriday() {
	if (ivjJCheckBoxFriday == null) {
		try {
			ivjJCheckBoxFriday = new javax.swing.JCheckBox();
			ivjJCheckBoxFriday.setName("JCheckBoxFriday");
			ivjJCheckBoxFriday.setMnemonic('f');
			ivjJCheckBoxFriday.setText("Friday");
			ivjJCheckBoxFriday.setMaximumSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxFriday.setPreferredSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxFriday.setMinimumSize(new java.awt.Dimension(91, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxFriday;
}

/**
 * Return the JCheckBoxHoliday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxHoliday() {
	if (ivjJCheckBoxHoliday == null) {
		try {
			ivjJCheckBoxHoliday = new javax.swing.JCheckBox();
			ivjJCheckBoxHoliday.setName("JCheckBoxHoliday");
			ivjJCheckBoxHoliday.setToolTipText("Holiday");
			ivjJCheckBoxHoliday.setMnemonic('y');
			// user code begin {1}
			
			ivjJCheckBoxHoliday.setText("Holiday");
			ivjJCheckBoxHoliday.setVisible(false);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxHoliday;
}

/**
 * Return the JCheckBoxMonday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxMonday() {
	if (ivjJCheckBoxMonday == null) {
		try {
			ivjJCheckBoxMonday = new javax.swing.JCheckBox();
			ivjJCheckBoxMonday.setName("JCheckBoxMonday");
			ivjJCheckBoxMonday.setMnemonic('m');
			ivjJCheckBoxMonday.setText("Monday");
			ivjJCheckBoxMonday.setMaximumSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxMonday.setPreferredSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxMonday.setMinimumSize(new java.awt.Dimension(91, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxMonday;
}

/**
 * Return the JCheckBoxSaturday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSaturday() {
	if (ivjJCheckBoxSaturday == null) {
		try {
			ivjJCheckBoxSaturday = new javax.swing.JCheckBox();
			ivjJCheckBoxSaturday.setName("JCheckBoxSaturday");
			ivjJCheckBoxSaturday.setMnemonic('s');
			ivjJCheckBoxSaturday.setText("Saturday");
			ivjJCheckBoxSaturday.setMaximumSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxSaturday.setPreferredSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxSaturday.setMinimumSize(new java.awt.Dimension(91, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSaturday;
}

/**
 * Return the JCheckBoxSunday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxSunday() {
	if (ivjJCheckBoxSunday == null) {
		try {
			ivjJCheckBoxSunday = new javax.swing.JCheckBox();
			ivjJCheckBoxSunday.setName("JCheckBoxSunday");
			ivjJCheckBoxSunday.setMnemonic('u');
			ivjJCheckBoxSunday.setText("Sunday");
			ivjJCheckBoxSunday.setMaximumSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxSunday.setPreferredSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxSunday.setMinimumSize(new java.awt.Dimension(91, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxSunday;
}

/**
 * Return the JCheckBoxThursday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxThursday() {
	if (ivjJCheckBoxThursday == null) {
		try {
			ivjJCheckBoxThursday = new javax.swing.JCheckBox();
			ivjJCheckBoxThursday.setName("JCheckBoxThursday");
			ivjJCheckBoxThursday.setMnemonic('h');
			ivjJCheckBoxThursday.setText("Thursday");
			ivjJCheckBoxThursday.setMaximumSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxThursday.setPreferredSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxThursday.setMinimumSize(new java.awt.Dimension(91, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxThursday;
}

/**
 * Return the JCheckBoxTuesday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxTuesday() {
	if (ivjJCheckBoxTuesday == null) {
		try {
			ivjJCheckBoxTuesday = new javax.swing.JCheckBox();
			ivjJCheckBoxTuesday.setName("JCheckBoxTuesday");
			ivjJCheckBoxTuesday.setMnemonic('t');
			ivjJCheckBoxTuesday.setText("Tuesday");
			ivjJCheckBoxTuesday.setMaximumSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxTuesday.setPreferredSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxTuesday.setMinimumSize(new java.awt.Dimension(91, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxTuesday;
}

/**
 * Return the JCheckBoxWednesday property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxWednesday() {
	if (ivjJCheckBoxWednesday == null) {
		try {
			ivjJCheckBoxWednesday = new javax.swing.JCheckBox();
			ivjJCheckBoxWednesday.setName("JCheckBoxWednesday");
			ivjJCheckBoxWednesday.setMnemonic('w');
			ivjJCheckBoxWednesday.setText("Wednesday");
			ivjJCheckBoxWednesday.setMaximumSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxWednesday.setPreferredSize(new java.awt.Dimension(91, 22));
			ivjJCheckBoxWednesday.setMinimumSize(new java.awt.Dimension(91, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxWednesday;
}

/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public Object[] getSelectedDays() 
{
	java.util.ArrayList list = new java.util.ArrayList(7);
	
	if( getJCheckBoxSunday().isSelected() )
		list.add(SUNDAY_STRING);
	if( getJCheckBoxMonday().isSelected() )
		list.add(MONDAY_STRING);
	if( getJCheckBoxTuesday().isSelected() )
		list.add(TUESDAY_STRING);
	if( getJCheckBoxWednesday().isSelected() )
		list.add(WEDNESDAY_STRING);
	if( getJCheckBoxThursday().isSelected() )
		list.add(THURSDAY_STRING);
	if( getJCheckBoxFriday().isSelected() )
		list.add(FRIDAY_STRING);
	if( getJCheckBoxSaturday().isSelected() )
		list.add(SATURDAY_STRING);

	return list.toArray();
}


/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return java.lang.String[]
 */
public String getSelectedDays8Chars() 
{
	StringBuffer buff = new StringBuffer("NNNNNNNN");
	
	if( getJCheckBoxSunday().isSelected() )
		buff.setCharAt( 0, 'Y' );		
	if( getJCheckBoxMonday().isSelected() )
		buff.setCharAt( 1, 'Y' );
	if( getJCheckBoxTuesday().isSelected() )
		buff.setCharAt( 2, 'Y' );
	if( getJCheckBoxWednesday().isSelected() )
		buff.setCharAt( 3, 'Y' );
	if( getJCheckBoxThursday().isSelected() )
		buff.setCharAt( 4, 'Y' );
	if( getJCheckBoxFriday().isSelected() )
		buff.setCharAt( 5, 'Y' );
	if( getJCheckBoxSaturday().isSelected() )
		buff.setCharAt( 6, 'Y' );
	if( getJCheckBoxHoliday().isSelected() )
		buff.setCharAt( 7, 'Y' );

	return buff.toString();
}

public String getSelectedDays7Chars() 
{
	StringBuffer buff = new StringBuffer("NNNNNNN");
	
	if( getJCheckBoxSunday().isSelected() )
		buff.setCharAt( 0, 'Y' );		
	if( getJCheckBoxMonday().isSelected() )
		buff.setCharAt( 1, 'Y' );
	if( getJCheckBoxTuesday().isSelected() )
		buff.setCharAt( 2, 'Y' );
	if( getJCheckBoxWednesday().isSelected() )
		buff.setCharAt( 3, 'Y' );
	if( getJCheckBoxThursday().isSelected() )
		buff.setCharAt( 4, 'Y' );
	if( getJCheckBoxFriday().isSelected() )
		buff.setCharAt( 5, 'Y' );
	if( getJCheckBoxSaturday().isSelected() )
		buff.setCharAt( 6, 'Y' );

	return buff.toString();
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}

	// user code end
	getJCheckBoxMonday().addActionListener(this);
	getJCheckBoxFriday().addActionListener(this);
	getJCheckBoxSaturday().addActionListener(this);
	getJCheckBoxSunday().addActionListener(this);
	getJCheckBoxTuesday().addActionListener(this);
	getJCheckBoxWednesday().addActionListener(this);
	getJCheckBoxThursday().addActionListener(this);
	getJCheckBoxHoliday().addActionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("JCheckBoxDayChooser");
		setLayout(getJCheckBoxDayChooserFlowLayout());
		setSize(372, 48);
		add(getJCheckBoxMonday(), getJCheckBoxMonday().getName());
		add(getJCheckBoxTuesday(), getJCheckBoxTuesday().getName());
		add(getJCheckBoxWednesday(), getJCheckBoxWednesday().getName());
		add(getJCheckBoxThursday(), getJCheckBoxThursday().getName());
		add(getJCheckBoxFriday(), getJCheckBoxFriday().getName());
		add(getJCheckBoxSaturday(), getJCheckBoxSaturday().getName());
		add(getJCheckBoxSunday(), getJCheckBoxSunday().getName());
		add(getJCheckBoxHoliday(), getJCheckBoxHoliday().getName());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 * @return boolean
 */
public boolean isHolidaySelected() 
{
   return getJCheckBoxHoliday().isSelected();
}


/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		JCheckBoxDayChooser aJCheckBoxDayChooser;
		aJCheckBoxDayChooser = new JCheckBoxDayChooser();
		frame.setContentPane(aJCheckBoxDayChooser);
		frame.setSize(aJCheckBoxDayChooser.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


public void removeActionListener(java.awt.event.ActionListener newListener) {
	aActionListener = java.awt.AWTEventMulticaster.remove(aActionListener, newListener);
	return;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
public void setEnabled(boolean value)
{
	getJCheckBoxMonday().setEnabled( value );
	getJCheckBoxTuesday().setEnabled( value );
	getJCheckBoxWednesday().setEnabled( value );
	getJCheckBoxThursday().setEnabled( value );
	getJCheckBoxFriday().setEnabled( value );
	getJCheckBoxSaturday().setEnabled( value );
	getJCheckBoxSunday().setEnabled( value );
	getJCheckBoxHoliday().setEnabled( value );

	super.setEnabled( value );
}


/**
 * Insert the method's description here.
 * Creation date: (10/17/2001 3:22:22 PM)
 * @param value boolean
 */
public void setHolidayVisible(boolean value) 
{
	getJCheckBoxHoliday().setVisible( value );
}

public void setHolidaySelected(boolean value)
{
	getJCheckBoxHoliday().setSelected(value);
}


/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 */
public void setSelectedCheckBoxes( String days )
{
	if( days == null || days.length() < 8 )
		days = "NNNNNNNN"; //we have a bad value, clear all check boxes
		
	for( int i = 0; i < days.length(); i++ )
	{
		boolean setSelected = (days.charAt(i) == 'Y' ||  days.charAt(i) == 'y');
		
		switch( i )
		{
			case 0: //SUNDAY
			getJCheckBoxSunday().setSelected( setSelected );
			break;
			
			case 1: //MONDAY
			getJCheckBoxMonday().setSelected( setSelected );
			break;

			case 2: //TUESDAY
			getJCheckBoxTuesday().setSelected( setSelected );
			break;
			
			case 3: //WEDNESDAY
			getJCheckBoxWednesday().setSelected( setSelected );
			break;
			
			case 4: //THURSDAY
			getJCheckBoxThursday().setSelected( setSelected );
			break;
			
			case 5: //FRIDAY
			getJCheckBoxFriday().setSelected( setSelected );
			break;
			
			case 6: //SATURDAY
			getJCheckBoxSaturday().setSelected( setSelected );
			break;
						
			case 7: //HOLIDAY
			getJCheckBoxHoliday().setSelected( setSelected );
			break;

			default:
			// ignore the value
		}

	}

}


/**
 * Insert the method's description here.
 * Creation date: (2/14/2001 3:23:45 PM)
 */
public void setSelectedDays( int[] days )
{
	if( days != null && days.length > 0 )
	{
		for( int i = 0; i < days.length; i++ )
		{
			switch( days[i] )
			{
					
				case 0: //SUNDAY
					getJCheckBoxSunday().setSelected( true );
					break;
				
				case 1: //MONDAY
					getJCheckBoxMonday().setSelected( true );
					break;
					
				case 2: //TUESDAY
					getJCheckBoxTuesday().setSelected( true );
					break;
				
				case 3: //WEDNESDAY
					getJCheckBoxWednesday().setSelected( true );
					break;
				
				case 4: //THURSDAY
					getJCheckBoxThursday().setSelected( true );
					break;
				
				case 5: //FRIDAY
					getJCheckBoxFriday().setSelected( true );
					break;
				
				case 6: //SATURDAY
					getJCheckBoxSaturday().setSelected( true );
					break;
				
				default:
				// igonre the value
			}
		}
	}
	else
		getJCheckBoxMonday().setSelected( true );
}


}
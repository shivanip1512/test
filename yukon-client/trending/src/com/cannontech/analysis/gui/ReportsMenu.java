package com.cannontech.analysis.gui;

/**
 * Insert the type's description here.
 * Creation date: (5/28/2004 10:14:16 AM)
 * @author: 
 */
public class ReportsMenu extends javax.swing.JMenu {
	private javax.swing.JMenu ivjAdminMenu = null;
	private javax.swing.JMenu ivjAMRMenu = null;
	private javax.swing.JMenu ivjCapControlMenu = null;
	private javax.swing.JMenu ivjDatabaseMenu = null;
	private javax.swing.JMenu ivjLoadManagementMenu = null;
	private javax.swing.JMenu ivjOtherMenu = null;
	private javax.swing.JMenu ivjStatisticsMenu = null;
	private javax.swing.JMenuItem ivjActivityLogMenuItem = null;
	private javax.swing.JMenuItem ivjRouteMacroMenuItem = null;
	private javax.swing.JMenuItem ivjSystemLogMenuItem = null;
	private javax.swing.JMenuItem ivjCarrierMenuItem = null;
	private javax.swing.JMenuItem ivjDisconnectMenuItem = null;
	private javax.swing.JMenuItem ivjLMControlLogMenuItem = null;
	private javax.swing.JMenuItem ivjLoadGroupAcctMenuItem = null;
	private javax.swing.JMenuItem ivjLoadProfileMenuItem = null;
	private javax.swing.JMenuItem ivjMissedMeterMenuItem = null;
	private javax.swing.JMenuItem ivjPowerFailMenuItem = null;
	private javax.swing.JMenuItem ivjSuccessMeterMenuItem = null;
	private javax.swing.JMenuItem ivjMonthlyMenuItem = null;
	private javax.swing.JMenuItem ivjPrevMonthMenuItem = null;
	private javax.swing.JMenuItem ivjTodayMenuItem = null;
	private javax.swing.JMenuItem ivjYesterdayMenuItem = null;
	private javax.swing.JMenuItem ivjConnectedMenuItem = null;
	private javax.swing.JMenuItem ivjCurrentstateMenuItem = null;
	private javax.swing.JMenuItem ivjHistoryMenuItem = null;
	private javax.swing.JSeparator ivjSeparator1 = null;
/**
 * ReportsMenu constructor comment.
 */
public ReportsMenu() {
	super();
	initialize();
}
/**
 * ReportsMenu constructor comment.
 * @param s java.lang.String
 */
public ReportsMenu(String s) {
	super(s);
}
/**
 * ReportsMenu constructor comment.
 * @param s java.lang.String
 * @param b boolean
 */
public ReportsMenu(String s, boolean b) {
	super(s, b);
}
/**
 * Return the ActivityLogMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getActivityLogMenuItem() {
	if (ivjActivityLogMenuItem == null) {
		try {
			ivjActivityLogMenuItem = new javax.swing.JMenuItem();
			ivjActivityLogMenuItem.setName("ActivityLogMenuItem");
			ivjActivityLogMenuItem.setText("Activity Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjActivityLogMenuItem;
}
/**
 * Return the JMenu6 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getAdminMenu() {
	if (ivjAdminMenu == null) {
		try {
			ivjAdminMenu = new javax.swing.JMenu();
			ivjAdminMenu.setName("AdminMenu");
			ivjAdminMenu.setText("Admin");
			ivjAdminMenu.add(getActivityLogMenuItem());
			ivjAdminMenu.add(getSystemLogMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAdminMenu;
}
/**
 * Return the JMenu5 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getAMRMenu() {
	if (ivjAMRMenu == null) {
		try {
			ivjAMRMenu = new javax.swing.JMenu();
			ivjAMRMenu.setName("AMRMenu");
			ivjAMRMenu.setText("AMR");
			ivjAMRMenu.add(getCurrentstateMenuItem());
			ivjAMRMenu.add(getHistoryMenuItem());
			ivjAMRMenu.add(getConnectedMenuItem());
			ivjAMRMenu.add(getDisconnectMenuItem());
			ivjAMRMenu.add(getSeparator1());
			ivjAMRMenu.add(getMissedMeterMenuItem());
			ivjAMRMenu.add(getSuccessMeterMenuItem());
			ivjAMRMenu.add(getLoadProfileMenuItem());
			ivjAMRMenu.add(getPowerFailMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAMRMenu;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GC6D43CB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DDC8DD4D4571D3709E96809491AACE9E8161C250676108D5938DD33E5DB1A25895DB555CDEC1774D8EBD272511424561AA65DE39A52BEE5C0502098D1D1C99C15CFA100486770B9FC8986948595A3098E4383A68EB3531987E2C35A7D5FFB5F7F3E47738EE3BD321E73BF6F5D5F7F737E6F475CFB5FC5A9F3FFD872060D191234A1D9D27FD5EF102436AEC9B2AD7B3BF908251A03CD126E5F81206DD2C9
	B2813300270C851BB2241FFAD221DC8C651811E053EEF85FA1BD6F896A0097BFFEAE814F567EF3E9F6BF5DB968E793ADDFC8FD971EDB014AG6B878AB3937F3E54AC951F0272D6751D50060D1254FD0048549F495261AF2BF2E660D5G2D24B2F725E62B7CE0A89319C8F8469C50EC265168EE33542865413B4324389F2ECCC9A73EF476114C8719EE1B7A041A2710C4A4C9CFD5FE884F7A5E4B230DF677F8095D66F239DD0AEC9FAB3139EC4E2BBE072FE4F442E1311C16BDEE2F626B11DD93A03F64A009F368C8
	AA35A1C953D0EEBEC430D5C7C8EE5693BE148B00D82CBFCA2F47BADD8E13E29EDC66B7567907D80C910734D833E24D26019CE27F5BB477C4AF8178E640D8ACFFCC45D8C2F3B01609B2A29B8765B5C026DC0C25A19FE399787CD1E965505BA95B7C44121D2B66ADD70BA57C9133E98F6D87ABF3C9DB4E83DEB8D09450EA20C4206C3CE053BE5A7F7EBE67B0BC2DC3CA2BFBFC5C6D32FABCAD4EE1EF0FFBD8F6423B45829C95BBAD5B5D5EE1C9E2FDEC74C831A09E240A3C197DCB6B9367A46C9311273FA975AB2F53
	F6B656AD7859D5A91D2A0D41BC2DEE11F9A4EF1365A4EF53703E015681ECD1BC08626BF4B8EB13F85A6624EC4E8FB6C567E31E8736E2AC5D267B25F5E56F24EC9244B27544EFD3868AD88C1E82AD961663EF65139C07829E853482A881A835907AD6A16678391C2301F25C85FD5A8B9D3AEFE248E373AAAD938E67306CD5F37D3D5459F2ADA226376CEF56E3F909B82AEF2368FFCD9466A5BA62051436C2350D8A353CB05F26B4FF3E278B2F6D9BDBF99B3ECA474442C25606C99CFF0362E1BA1C356D6794A76545
	404BBC9C2C767D5EC2B2C7CDC1B998A892A89668E5A02988640A30ED8AF2CB2F337F776A5A05693E103B8B1E72146235F539FC0A4DE5174F5A3C8E5B10D376C92A4CEB39A49FC3B374FEC347259DFC9C13DD325726B85CAE30EE9B17219378184C7A5C6DC446ABD3EE3FE3DC8687639E896DCAF924BFBBEDBE650CE75826481A8463276611361C341D850BE0G5E070BC83E7EC267448AF86FAD42DCFDF118E091D00E61F939B56FBDF8FE841DD2F6DAACED8EBB8943663D0AFDEB3ABC871EF1A03F8648D48CF38A
	D0BDD0BF1087E8BED0E8B10E35358358275A523FA13D7B6C5B42BEC5A82A18750F954546BEF59775399E702C8F419750A52079C091C04B015681E516606F60ABC7968A6CE32C22BED8EF40C97D74E5D2D72C5F750C27792ADC28CE1E4516DEAE0AED13BA86C53636984AC906B219C749EF56366B08EBAB0D01BDF768B0569FFED5C446D48D64319FC8A941BE71F81E190D15961BCF4651F1528B9DDB9E56FA88137DEEBE09C31E14DDCA1B6C11DD437062D37B669379E95A3899F173BE3D1462E47EEB050E6D6378
	53795B998EB674239079BF21FC1B22F89DC3930A5CA7AB0A43B52A69BFCFE3714D18DAFDEADDFFDDCC6A3AG6A988E340457F534E0072E7FF73B5D17A7BC7DE4E42B63E8DF9A99B3CBC1BE1E77375F141954BE424AEF5132467FD259002E9F7DAD7372754F7977F39F467EF1B308656663C27FEDEBAB94376DE82138ED478BE7EF3B09C2D15BB15EF4A169736EABAEDDDF78D2A119376CEE17CB269D122BC8ECBD39361F544361F2A8679C432332526778338CEB72D2522677523951846FD925B8EFDDB90473D64E
	1F3FAE05DD7C09F03DB2750D5F25F8CAD91B4EAF5566AD9653AD07C99F131D72B8A42A55BD61D2989EC467DD9577A5D1AC5D3E991E4749BA36CB11470FE05CFDB2997F7AD5AE13FF341B44F9D299133D6A5A17616DA7C89E3A5D36619ED8A80FD297CC0071D7505F4DB61BE29B0241CCB88C7F1ACCE2EF35F9DAE1196DF5BB75BAF6BB69E3561E53BA7F7797917F5661F107EB067F20E4E227CF011FAF1F62302B8BF3B6664CA544F7B864A98AE8C5A90EB9D3519E5D18D3A716D63753491A92F8D9E5241D7E2BBC
	D3177B04D23647A16FEC8F73C639FE8FF3290C71C360998944E4EAABC5B23170DC49E5BC55A219F570B49DC11907EBC5B25940AF63B2AF5609E4BA01BF48E576B40AE4946087F1195EE611CC987017F0195BDBC4B24B019FFF84737CFAD18699D3721FA6E4D83210C96DA379AA2EBB0A5EB3704E5A1C935AFAC6AAA67D50291B8F997EC631380F3FD5CC66404B5C7EBB45A992D917B01B2CEFAF36911B9E2FE39CFEBE48842F36E3F91A3EAE6BA1FE5C37E61743FE341C48C472BD9ED947B16CC90AA5C3475C1E0C
	EB5A1E8C7DDE344D37D6B80E69D8AEE7BE96149B577AE7CA095DC840234BB117D7BE547FCED8FDBE375DC117ECEADE72CA74E31CE1C5A5E40C0FB83CBED8778F4BD3185BA3A5A4D63226D3E1B5BF2F6EBAA8DD3BC6ED9FF6A8EEAF5FC05849GE1F57D22C33F178D5D76D24A96BFF5DD2156F11D2E2E7130FFE4FE4FEFA6FBC33E77F68C0D17CCCC0E1758061492991CDBACF41ABA6B70CD581C345954FDF75151D4B576322338577DF285699FE3D01E8794F2947745FF2AC45945DC76FBD5C4B6814AD2854C4595
	A89BD14D6DD6206C7A9AEA974A7300C2386CCB355CAE17AD2D23F621ACD5025DCA145DDB4F6DD6226CC883358B65F9C0A1DC76D4A3374BE59FE822F621ACD5015DAA148DEA66F62BD0B64ECA6DC2F99ED088177D0F96EE174BEEEB23F621ACD503DDA056AEE93BF7E33BFCE61B34CD15782E40EF265A9C92533DBB0D681601CEA75025EA7CBDBBD72E9F87E171AF7A9F87556AB828B60E03DF16517D82605155B88EA2CB366B4641E91831ED2ED1189D781A21B4094D83EA795352E41C87A47CDD4821737A8A3039
	8648D48373A8D0FD8D7A78C5E940FDF47B54AC7B68322322BD69E00D3A372F39F6CD46761E3F27FDF7FE8D5BFBEE60F8D2A55D2B6AF036A7DDD001E7C0214013EAF17DB7D9A972BFDC4B7CCE557A73FF241A2ED1EB0D7EAD948F29B57AFF328A7D0783AF55027E9F299679F7DA185FF996FF7E1D3544DE304568FF0262E196237FBF54207FC860E554217F04DA11FF250E798D2A73677FC1BAEEC36B0C7EA3A99ED1E774DFD9077E17GAF2B9E7D6F2F97791F2EE7FE97567B733F0A4E856175C67FAFD1FCF13D51
	3F33817DC7834F5C007E4FB60A7C4FEFE0FEC39A7C795F5ECC6CC5B6987D6F25F8D403517F03CD68BF96F8850D687F4EE6117F850D4CEFD823BF7F1DAD445E12C6237FD3940FE9B47ADFE5C57F4B01D756047EE3DBC47E031B547354A6FF7EEF2573E0F413517F820AAFEDB27A5F59067E6300D7538C6BBEAA3FF3B36DC74DCCFE9B3713CB71D89D4E6C5CC767D0D2DE833CB1AB1E1734525F3717217C2695672626B2327746792F576DF0414F21BA572DA9A3EB9E3757B16AF0A176BA456CEEAF5D3CB02C385C7F
	3E6AE839F8DFD5DBAE5ED7B5154F3EE7BED66EEF5F45763747E89DCDAD4126CCA006C5D24C8C651A96ECD329929E6A7C1A26FB3F27827721F3C14C4743F36A0355794ECA4C43A04F435ACA4C43984FC3F535D677E65D7BD3D5F31F07976754872BF3ACDDFBAD007A0672BC9456E09EA2F89E3C35DA5DBF573D1BEA67BE8F6F4F298FD667BF56E29E1673BCDC32E09ED672BCBCD427557DDB3A773A3A394FC37F1C7AE0F52E2A47BCA472BC5CD70FF9C8ED45BC44B7E8F5FFD5776EED187BBC5CB127BED81D479BB1
	8F452D1807279B79BC4973303BD92B3BD9777ED0535C676109B975416A9C5E0CF99864F9C83672F9126761C40BD677F33A77F86B5C67612DB975416A7CB33A0FDBG758D65F9E8EE6573A44F43D75A343A5F23FB5F57B677F92878FF7031EFB3797E369C6A7BB250FA204CB698A3C05DC0E3ED1807A2AAB78F4A61C0CB0156GE534871BAA00865AD16E01B40F409731CBEC1177CC75D95CA6BEDB47D80CF8379FF92DAE51C25F04075FDA701B0D719B8D92597BA68B7074D9724856E91DC7491AF40A173BE9792A
	04672C1F9EA567EB8AAC8C275B495D105D96C942F323FF39C26299715A46E1E95767116DD2E6875D9F9E45F33518F6D677B87579A6BC199D8B7D4DE27249D5A8BF58216E155567C2F832B8FED6BDB38EEF3D265FDC037A0BD53DD87579F2877A3B37C1133FE2C1F953B1A61725BEEB0E217CEB0D1A7CBF57237C00AA67D41F8B38FCFA33A67F8BFE569A294A45284FF8AEDFE65564332DA8AFF52AFDCDFDD6F4E27DFF5BB2337EFDAD1A7E39D6546FD7754654E7905777F70EAEBAF31F6D0C5E5F39FE206F816273
	FAFF5F9858796C50E66379AC79569581F50A6E447368E7F70AE462001F40E5124DA299298B660CAE14796B8111CCB1701B394C778F09E486006F64B24B72C4B273019F42E59CC5A21945400F6D42F334E7AE684F5156FD6D47A93887B50E6355DD2C2D933A34F3B4F66677AA5DCF911D6C6341267E63845BDD217F36C176E805C0A5246FD0FE7F8CFE320E4F62DA160BE73F094BA2246E47DE73FB0FA974385A7755E7D4D2E68AF92AE738D5985BFA9E5B79AAE3EC0FED5447F6C715B136C75458184D119A34D976
	915A8C2EB15AEC1AE173BFEB0CB6FF37C1EF73818B5A9C63B617D90CB67BB668ED3EE3B15A3C10243799D70FB643384D0D8DC61B07E634CBC50351666D9B75B637B4214D155CE6ED1351663299B6AFB799ED66A76BED36D851E6C6B75A74D8E76FBF0B2CC61B69B3ECFE510AB6BB394D07DBE73779DC2B516613B3EC5E3F993F514C6B46336D329D746C8A4AAB00923A719CBCFD974ACABDA89B3A0B5EC700F2AF50D88F4AB61839DDAE3B5CCC6DC2F985D082177D7681EE77844AF69C24F6215C8BB4F682E55FBA
	446DF259209CEA974AAB009238ECD2BE37FB92E53F1ACF6DC23997E86CA44AFE3D085B65323F275FC223203C82A861A41EB1ADE8A5A659EF7EFDAD3970BCEB7BCC78656738ED52F6F268B3592E745BC6A5DC332C951EF54FC6EC7E787EA61CBF16FCE711947265FF727B6DC86AD56F062A4F5E0DEC7EE0712FEBA3F3E8AF701CC08B01A200220162FA793DB8FA9EC8CE6D21C2D6C531594768D7B22C59D907FCC5034982C6BD472F3A317BF017EBEECCEF11654674FEAABCEF8E2C1751F4E3FA472CB726F7CB6B0D
	697D63E611DEC22F7F75B4BBEF5D18C97AFCE2AFBBEFCD66F8BA45B3CEE9B8BB072D22F385A967GAF68946E895E5BA5723F66D4A07FEBF793FB52E9637DD1A90BFEB3BBED3CBF3AE8975E9F3D843C4453683F45AC729F529748FF7381FA1E5FE7BC4F6E22F8D41F71BC7B47E6FE1E8F3C02FE749FFDC0F81E5F9F487FF7683C9756EF3CE399CB71457D46BB165D87710EE58C70F24E207FBF9C92798FBA9348FFA21DCBC34F985BFFD3AE75FF46587EF765E07B47822F60AC7ACF4D97567FECA07F1B8BE87D4FB2
	BFE99C5FCD71E89D4E7C0752791C14D7822F7E9C7AF716087C071D8B645FD5CA3F271CB366FF0A6231670C7917CAB17FF1402B99405F082A8E723B9B8A65A82055C06B00CC67F1FE5D55265E9B904C2D6D4323B2EC9F482DD585FFE75EEA737F7DC1EA8FFC57B52D1D5CEB98F2AB0AFB1C58C7FCDBBB49176276D010E1196DA41FDE47681842C4D5FCEFBB790DF44AA394E667A77FC6578FE628D759F95E7619842B0772A047F6D0EC9A4A219C2B5FF1334EBA08AFFC9FD45F190FB3B74D077E4E9E1E9728E76D3B
	B087513CEEEF6EA677E117C2B9814874B10CCD20B620C120E920D020680FD1BEA50B482F06F2A2D07685684BC003C053C021C051C02BAF20FC44AEF2C715587597EF1B00B7ED40F254F82EB78724BEA4EED227E8019E0B6D999A5B26D96C084EE20CF7E3C99F1C6D8CC654BE5B553659A1603336BAEF4636CA64396CA0FF2A2346FED2773E52BC77E7F12F4F298FD6677FB900F5CE9D44BA479CA4E7A2193C7CC3DAAE63655387EFD6DC4407386E17EF1A8F51FDDAD6771DB9D877DEDE370D39849B0472B44752A9
	968665980E8567840AEFD0D0B7C253FE70EBF370C8C09F7E6E8A0B4EAC45B958CF77C1719053BADE370C82F24FB0914AD91F406F96D07FA748FB1F7294A88783AD810A633CC5797AF8092E5EDF22215EB51FDC7F5C22797F6E8C9F46FA9A73B15BBCE324B41E13DB695D3FB7412769D3742B1412B110414BFF2165B2DECEA93D19E315783936BF6C3F29BE0C39E23F630D9D7E57926D9D015792A7BA683DC9D88374DF75488C6B6B2077A8C6C6FCB2DF338CF4E4F02C4B576B76B94865CBF6EE795F1BE71E49BEFF
	CC1270CCB3E2B7193F2DF645B169D02EF63BC77146354AB71D2567C6D7FDG6939F88739BDD3FF66518BF9A65FB0266049785BF60A788BBE4573658DDDDAAC0FCF90595689AF392C8179D0E4F447786F3710DCF6BAFC306B3FEA0873859B0933155DF71707F5F12A753413F35FB607CF3D922F89B07EB7AF12B5DB1F6C31F9EDE07E8862F7507C74B8FCBEE2D4E1A73FBA3F8F5BC94CFD93F63B6C734D90E07C7CA422CF4E1BFB3D6E9107D3B668E7FDC0626EF5DF113D9DB607731A7CB6E59973C73E8F05717C26
	6D917117F07EB3C27ED24E3FF72F083F1A73AF6C537357297C2DBC3E5B77097433AE22FECF3608DF4C79BB5E97711BB93FE93F085FFF11FF1FB9CE7A8FFBDF741156173ABEA7FD25556675BAF4ED46EC3EFD50E813FCEB9B63B6FF502D5939FB0C48F677281F98F4C30071FF193CD3ED5FE3DE770447EA57F7ADEC43DC23BF72DDEF1A57B1B9CF44DFF8913F09D4654F9C3F89BDDAFC371CA06B36FE77306D6AB5FD7E69AB24EF1D17E16CFAC57C0AF3A4C7BDD033B127F1BC7DC3856D17DEF912722F29532982E3
	4C64BBE5B82F536EC291BF0A731F937217F37E0307C57CF81E137BCBE666E44BC9ADA7D1FFA5E37D34FB82A6901B5D6B5602E7369FADB55AA65FD75F3C08770A17A613B9554F3DE2EA081DE3CEE762B9E66AA5BC5B6C5809D8B347A633901B62D8739E4496F3ACE5AFE2899CDB4EB1338D633BDB4996474727AF267B78BE549D30213D795908AD6458717791DB4A31497D7CBCF78831F2FE9EDC433149DC44BC9C2B4FC3AC02E3C1850845F36CC4A1E2D9F644B28FA3564B31D59C8B6258AB25084558795F15E50A
	4EA1565B83FD577EE827C86F5D61C0FA1FE709744C81755A770874AA826AED59AB526B8C2877A3215EE0C03DA7770974BC81753E12AD3C5F9BD06F318F046708015BE13FC8AFB220DE35705CAEA6303FDC11DEDCC0BDEB1E701CB8205E1D85A23D4400FAFD05A23D8CB9105E1E43A23D1C00FABF976A5584547BD529C82FFB96BD362F8821736FA5107348F847E8E8A7E2539C3B074E3FC1D08E6158609E4442B9F6F0AFE20BB9164031E80E2D5A0758D20E05E5A3964731659FA0962F45329F31B59CBBF1883175
	1ADCAEE22691448672904B60D8F881E2599C7B2C9031820E159DC62C02E37F4B31E60EEDAC437CF50F60EF6D2319B3FFEB9F2B02223A664AA61F7F555F5D0732E64AB555EAF2C3966D7D36BA6DBD22DEFB6FED545EFDCD5A7BD71B3577FF37EA6F292D5AFBD11B76FE22DDFB7F22C3FB8FBB263DBF59293D3F52255325EBC876FF525C1A9431816611DFCB490A34C577A7270A14CCAF01A852B63270B3EBB7C994A9952F1C00CA2B4DE3D6570D0A14E46DB98D0A74E60BA2ED56EE18E8A09319292633F72D3319D6
	54185E454D1C99962BC4066E5DD8A1DD5DDC1909A2185C24EE2694A90DEF1DC0C55BA601B93677B1533D8DD8D037BAD8FE1786256EEDC0156EE44CE4AB8356556537A2ED6D6E41AA31C0F6D2B53AE4B6431A197C1DA4ACE12092F4A58B6170D5AB18D1D728E0062CC79D6E8970CEA052AAE411E7262BBC765D36702F7A7BA8ABEFFB4A6F5FF5C50D3179E57918716F3AF2D34898C8F5841BF29C788DE1A2197C7FBB7E3EBD770D5B1CCE45663CECF58D136F50ADD795D97B9B044374FB308FE5D4267A370EA9A46E
	4FF4BA7F87D0CB8788156EDC190396GGECC9GGD0CB818294G94G88G88GC6D43CB0156EDC190396GGECC9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG3D96GGGG
**end of data**/
}
/**
 * Return the JMenu4 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getCapControlMenu() {
	if (ivjCapControlMenu == null) {
		try {
			ivjCapControlMenu = new javax.swing.JMenu();
			ivjCapControlMenu.setName("CapControlMenu");
			ivjCapControlMenu.setText("Cap Control");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCapControlMenu;
}
/**
 * Return the CarrierMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getCarrierMenuItem() {
	if (ivjCarrierMenuItem == null) {
		try {
			ivjCarrierMenuItem = new javax.swing.JMenuItem();
			ivjCarrierMenuItem.setName("CarrierMenuItem");
			ivjCarrierMenuItem.setText("Carrier");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCarrierMenuItem;
}
/**
 * Return the ConnectedMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getConnectedMenuItem() {
	if (ivjConnectedMenuItem == null) {
		try {
			ivjConnectedMenuItem = new javax.swing.JMenuItem();
			ivjConnectedMenuItem.setName("ConnectedMenuItem");
			ivjConnectedMenuItem.setText("Connected");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConnectedMenuItem;
}
/**
 * Return the CurrentstateMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getCurrentstateMenuItem() {
	if (ivjCurrentstateMenuItem == null) {
		try {
			ivjCurrentstateMenuItem = new javax.swing.JMenuItem();
			ivjCurrentstateMenuItem.setName("CurrentstateMenuItem");
			ivjCurrentstateMenuItem.setText("Current State");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCurrentstateMenuItem;
}
/**
 * Return the JMenu3 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getDatabaseMenu() {
	if (ivjDatabaseMenu == null) {
		try {
			ivjDatabaseMenu = new javax.swing.JMenu();
			ivjDatabaseMenu.setName("DatabaseMenu");
			ivjDatabaseMenu.setText("Database");
			ivjDatabaseMenu.add(getCarrierMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDatabaseMenu;
}
/**
 * Return the DisconnectMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getDisconnectMenuItem() {
	if (ivjDisconnectMenuItem == null) {
		try {
			ivjDisconnectMenuItem = new javax.swing.JMenuItem();
			ivjDisconnectMenuItem.setName("DisconnectMenuItem");
			ivjDisconnectMenuItem.setText("Disconnect");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDisconnectMenuItem;
}
/**
 * Return the HistoryMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getHistoryMenuItem() {
	if (ivjHistoryMenuItem == null) {
		try {
			ivjHistoryMenuItem = new javax.swing.JMenuItem();
			ivjHistoryMenuItem.setName("HistoryMenuItem");
			ivjHistoryMenuItem.setText("History");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHistoryMenuItem;
}
/**
 * Return the LMControlLogMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getLMControlLogMenuItem() {
	if (ivjLMControlLogMenuItem == null) {
		try {
			ivjLMControlLogMenuItem = new javax.swing.JMenuItem();
			ivjLMControlLogMenuItem.setName("LMControlLogMenuItem");
			ivjLMControlLogMenuItem.setText("LM Control Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLMControlLogMenuItem;
}
/**
 * Return the LoadGroupAcctMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getLoadGroupAcctMenuItem() {
	if (ivjLoadGroupAcctMenuItem == null) {
		try {
			ivjLoadGroupAcctMenuItem = new javax.swing.JMenuItem();
			ivjLoadGroupAcctMenuItem.setName("LoadGroupAcctMenuItem");
			ivjLoadGroupAcctMenuItem.setText("Load Group Accounting");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadGroupAcctMenuItem;
}
/**
 * Return the JMenu2 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getLoadManagementMenu() {
	if (ivjLoadManagementMenu == null) {
		try {
			ivjLoadManagementMenu = new javax.swing.JMenu();
			ivjLoadManagementMenu.setName("LoadManagementMenu");
			ivjLoadManagementMenu.setText("Load Management");
			ivjLoadManagementMenu.add(getLMControlLogMenuItem());
			ivjLoadManagementMenu.add(getLoadGroupAcctMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadManagementMenu;
}
/**
 * Return the LoadProfileMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getLoadProfileMenuItem() {
	if (ivjLoadProfileMenuItem == null) {
		try {
			ivjLoadProfileMenuItem = new javax.swing.JMenuItem();
			ivjLoadProfileMenuItem.setName("LoadProfileMenuItem");
			ivjLoadProfileMenuItem.setText("Load Profile");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLoadProfileMenuItem;
}
/**
 * Return the MissedMeterMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMissedMeterMenuItem() {
	if (ivjMissedMeterMenuItem == null) {
		try {
			ivjMissedMeterMenuItem = new javax.swing.JMenuItem();
			ivjMissedMeterMenuItem.setName("MissedMeterMenuItem");
			ivjMissedMeterMenuItem.setText("Missed Meter");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMissedMeterMenuItem;
}
/**
 * Return the MonthlyMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getMonthlyMenuItem() {
	if (ivjMonthlyMenuItem == null) {
		try {
			ivjMonthlyMenuItem = new javax.swing.JMenuItem();
			ivjMonthlyMenuItem.setName("MonthlyMenuItem");
			ivjMonthlyMenuItem.setText("Monthly");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMonthlyMenuItem;
}
/**
 * Return the JMenu1 property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getOtherMenu() {
	if (ivjOtherMenu == null) {
		try {
			ivjOtherMenu = new javax.swing.JMenu();
			ivjOtherMenu.setName("OtherMenu");
			ivjOtherMenu.setText("Other");
			ivjOtherMenu.add(getRouteMacroMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOtherMenu;
}
/**
 * Return the PowerFailMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getPowerFailMenuItem() {
	if (ivjPowerFailMenuItem == null) {
		try {
			ivjPowerFailMenuItem = new javax.swing.JMenuItem();
			ivjPowerFailMenuItem.setName("PowerFailMenuItem");
			ivjPowerFailMenuItem.setText("Power Fail");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPowerFailMenuItem;
}
/**
 * Return the PrevMonthMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getPrevMonthMenuItem() {
	if (ivjPrevMonthMenuItem == null) {
		try {
			ivjPrevMonthMenuItem = new javax.swing.JMenuItem();
			ivjPrevMonthMenuItem.setName("PrevMonthMenuItem");
			ivjPrevMonthMenuItem.setText("Previous Month");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPrevMonthMenuItem;
}
/**
 * Return the RouteMacroMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getRouteMacroMenuItem() {
	if (ivjRouteMacroMenuItem == null) {
		try {
			ivjRouteMacroMenuItem = new javax.swing.JMenuItem();
			ivjRouteMacroMenuItem.setName("RouteMacroMenuItem");
			ivjRouteMacroMenuItem.setText("Route Macro");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRouteMacroMenuItem;
}
/**
 * Return the Separator1 property value.
 * @return javax.swing.JSeparator
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JSeparator getSeparator1() {
	if (ivjSeparator1 == null) {
		try {
			ivjSeparator1 = new javax.swing.JSeparator();
			ivjSeparator1.setName("Separator1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSeparator1;
}
/**
 * Return the StatisticsMenu property value.
 * @return javax.swing.JMenu
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenu getStatisticsMenu() {
	if (ivjStatisticsMenu == null) {
		try {
			ivjStatisticsMenu = new javax.swing.JMenu();
			ivjStatisticsMenu.setName("StatisticsMenu");
			ivjStatisticsMenu.setText("Statistics");
			ivjStatisticsMenu.add(getTodayMenuItem());
			ivjStatisticsMenu.add(getYesterdayMenuItem());
			ivjStatisticsMenu.add(getMonthlyMenuItem());
			ivjStatisticsMenu.add(getPrevMonthMenuItem());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatisticsMenu;
}
/**
 * Return the SuccessMeterMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getSuccessMeterMenuItem() {
	if (ivjSuccessMeterMenuItem == null) {
		try {
			ivjSuccessMeterMenuItem = new javax.swing.JMenuItem();
			ivjSuccessMeterMenuItem.setName("SuccessMeterMenuItem");
			ivjSuccessMeterMenuItem.setText("Success Meter");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSuccessMeterMenuItem;
}
/**
 * Return the SystemLogMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getSystemLogMenuItem() {
	if (ivjSystemLogMenuItem == null) {
		try {
			ivjSystemLogMenuItem = new javax.swing.JMenuItem();
			ivjSystemLogMenuItem.setName("SystemLogMenuItem");
			ivjSystemLogMenuItem.setText("System Log");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSystemLogMenuItem;
}
/**
 * Return the TodayMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getTodayMenuItem() {
	if (ivjTodayMenuItem == null) {
		try {
			ivjTodayMenuItem = new javax.swing.JMenuItem();
			ivjTodayMenuItem.setName("TodayMenuItem");
			ivjTodayMenuItem.setText("Today");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTodayMenuItem;
}
/**
 * Return the YesterdayMenuItem property value.
 * @return javax.swing.JMenuItem
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JMenuItem getYesterdayMenuItem() {
	if (ivjYesterdayMenuItem == null) {
		try {
			ivjYesterdayMenuItem = new javax.swing.JMenuItem();
			ivjYesterdayMenuItem.setName("YesterdayMenuItem");
			ivjYesterdayMenuItem.setText("Yesterday");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYesterdayMenuItem;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	// exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ReportsMenu");
		add(getAdminMenu());
		add(getAMRMenu());
		add(getCapControlMenu());
		add(getDatabaseMenu());
		add(getLoadManagementMenu());
		add(getStatisticsMenu());
		add(getOtherMenu());
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
		ReportsMenu aReportsMenu;
		aReportsMenu = new ReportsMenu();
		frame.setContentPane(aReportsMenu);
		frame.setSize(aReportsMenu.getSize());
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
		System.err.println("Exception occurred in main() of com.cannontech.analysis.gui.ReportsMenu");
		exception.printStackTrace(System.out);
	}
}
}

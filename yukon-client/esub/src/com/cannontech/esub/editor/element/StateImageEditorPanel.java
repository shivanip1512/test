package com.cannontech.esub.editor.element;

import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.event.*;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.gui.util.*;
import com.cannontech.esub.editor.Drawing;
import com.cannontech.esub.util.Util;

/**
 * Creation date: (1/14/2002 3:37:58 PM)
 * @author: 
 */
public class StateImageEditorPanel extends DataInputPanel implements TreeSelectionListener {
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private PointSelectionPanel ivjPointSelectionPanel = null;
	private javax.swing.JTable ivjStatusImageTable = null;
	private javax.swing.JScrollPane ivjStatusImageTableScrollPane = null;
	private StateImageTableModel ivjStateImageTableModel = null;
	private StateImage stateImage;
	private LinkToPanel ivjLinkToPanel = null;
/**
 * StateImageEditorPanel constructor comment.
 */
public StateImageEditorPanel() {
	super();
	initialize();
}
/**
 * Creation date: (1/21/2002 12:44:18 PM)
 * @param pointid int
 */
private void changeStateImageTable(StateImage stateImage, com.cannontech.database.data.lite.LitePoint p) {

	// Set states and images
	getStateImageTableModel().clear();
	
	java.util.List l = com.cannontech.database.cache.DefaultDatabaseCache.getInstance().getAllStateGroups();
	java.util.Iterator iter = l.iterator();
	
	while( iter.hasNext() ) {
		com.cannontech.database.data.lite.LiteStateGroup lsg = 
			(com.cannontech.database.data.lite.LiteStateGroup) iter.next();

		if( lsg.getStateGroupID() == p.getStateGroupID() ) {
			java.util.List stateList = lsg.getStatesList();
			java.util.Iterator stateIter = stateList.iterator();

			while( stateIter.hasNext() ) {
				com.cannontech.database.data.lite.LiteState ls =
					(com.cannontech.database.data.lite.LiteState) stateIter.next();

				String stateText = ls.getStateText();
				String img = stateImage.getAbsoluteImagePath(ls.getStateText());

				if( img == null )
					img = StateImage.INVALID_STATE_IMAGE_NAME;
				
				getStateImageTableModel().addState(stateText, img);
				CTILogger.info("added a state: " + stateText + "-" + img);
			}

			getStatusImageTable().tableChanged(new TableModelEvent(getStateImageTableModel()));
			break;
		}	
	}
}
/**
 * connPtoP1SetTarget:  (StateImageTableModel.this <--> StatusImageTable.model)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connPtoP1SetTarget() {
	/* Set the target from the source */
	try {
		getStatusImageTable().setModel(getStateImageTableModel());
		getStatusImageTable().createDefaultColumnsFromModel();
		// user code begin {1}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G0B03B6ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD8D4571544284104A4261A2DB636B18D31E64337B1D933A509DF5774A31BB4F19B12180D6DE732E6EBDAB6EB3AB632DBB6A571727FB7030320210ABA09209891B5DA2B14287C98452092C081C1654D4CE36641B0B34EBC8494F04F39775D779E438C84DA6773785EBD776F5CF34FB9771CF39FA4EF5B5C140444BCC292D208727BF382A1315504FC7764B313B8AE7AD4DAA6517DBE8548A1BFCBCA
	06678A28DB951616E9A22FF952219C8B65F6A8FF826F3964CDCFFB953C841967B0544DFB5B37F374F90E9E65732451F2E713811E59GF100AB28C94BF454607C131A72957CF1A8E7AB6F88890904941E46B60F6F485761A314F6F170B4C07D0B344D771B8A14FAA714314DE4F84EBA4D5AAE00E78625EECA59E125DD5BFDB3C96CCBD909C61CCBB7BE8753572C6FC165E9A6B3A8A31248EB42D1F81657F5590FDA5D5D25D64165F23BE4516AA895FDBD16D251A649EEEF2968943BC517DCD656A08B32D853AD5845
	933426CEF009CEC2423FC63A5F5811CE47843A213C2F96F1E7F6A22FE220CC201CDF4BE9FF55D82E50B633EBA6D97170478941E817EB994D33EAB55A17BD7BDE6229B3B87E3F503D40FE4BF0BE40B1DA321BB9ADE1E7B8AD078A31ED94141783442A785DC508B71CCD4BAC84A8BF4BE97CF2A72731794643A44ED21A38AE880D0DE799ED83E7B59ACBF2DB127FC8656525E3386711D0379CE0B5C05E39344C03G0DG1B291CCEB97AB9BCABACF2153B3B5B6D2A70F8AA1DB66F9937CDF442FBD99954A838FA516A
	765A88E132784B9DA581686160BCB7DC8E91585A1A88171D087A6F91D9AECCCC89303605EF9893C314B1E61E53567662B9645BF3F4EF9742BB76437E8C3F04628DF59A1E6DD58345E3598CF5136BB81F1B33B9AD35497790537EFD09E981E8197553224418BAC68379C6230571F857A07238847027G5A817AGE681C4FE43F97C4A5183E3713886E45F6B921C8DBD960F6015ABFBA427CD74AA3CDE52B49A2F830132C7636E4778B27F00FE0F167F635E00FCD9BEBFAFF129420FD5BA3E3039F31B034DED289FA9
	9B596A9E7EBD5595F33D7F5EBEC571876B7D77363C106F6DA9280BGE06D9F243AE5EAE06D1354F19E25F8338E4F4659CF71D85E87F5B381587E3EB004B6B18E4AF90DE919A5G55GB6009000190DFC0FB79D5B7BAD7528CE373F2C6F6B47B642D36C133DC20D4BA78BAE2BF8C170CA0245A97A0852E679B1642BE5D83FF728FEDBE10E1322CB748A3264F64168C237884264E3ED5EBB1603ED3CA22DED143AC518205BC378389F9C4BC5FD95FC72F90F8DEC2C560255FFF88CE5621AF081C6048160FDE5A3726B
	F75466AE047768C64E2B581B08ABBE1F16F97CBC674B07B7564373EB90EE51D9D6D6ADD9118C415B4FE574051D5827915A4B6739DE7E36194BDF35F1B6A9F923B4207CA1ACBA4FE4E969F9FF79FB0D0E9B8F78028B7C7CEC59592C4CB59E1D98F1AE84694F667052B9127E0A713395F98E04E7FC3E1E0032DA8E6BEA86683B40F97D2781B31341CA41A7F229BB67850191ED9A67D95BAF8671CC912F4169DAADFAC4178DDEFC4A1E9F994C506431432D4A4AB10AC77BD4858263D371A786F3989E4650CBB72FBFC3
	6B85D976CA169ED9EC90E5D9F259357E67A9AD3EE1264F272CF5A09157BA9D56B89FA0D2DD6B5B0339BA392AF53B3BFABC0D28B10AFCFECE4F61A550FE253A7F8B8A91175E84675FF90EFB166230CDC99347D596FEDBB94131463B37EC0ED6BAC730FD5643C4E4094D71C8917A0F89EA79F1DA2ED557796722E692D82F7E96406628AB7ADB68C3B0E032706AD0F0D9DEB694D8165F9E8AAC4BAB07C61755D5C301E415552D99427DF4773AF4329FB704764F6AF639C42A00EA97427C605FB662BAA417A4B7C9B63B
	A8B7C857C5C266B4218C27D39BA643FBE893370D3D5B39EDAC3CFEBFD9FCA5A3207FB46B116244950AEC46B6E936115174FFB7D127945F3A4A5D631299FEAD355F8A5E17C4F1C959903C904D7D26BAC60FB5D67FF179919C3BB8D00B9259A1794C65FB769CB5E3F50FCF2BAF6946863B7691EE539ECFC21A721A53B20B810EB7F3BD1FC16B391EAB462C4A6DF4B2F6C25D1CE664D5F2F11EEE7D4D4DACBE8A05A72B3F27D4DF3FD22957E4A8A441047BAADE6D916178C5923F967B15B3740E0475FEF59784E70FF6
	B65F1B1085CF274E86B17C244440FC8ECDC4BD6CD2478FCBCCA5F84632B119DCAC90F0CC0FD76A86130DC6D50963AA1A75F11C3985F8856BE80627930075BD33F74330BD9B96B2A97CDADB286747E4E59CFC5FF7916BD79471D8B03A05594698F8468194C07DD6408F50F65BF672F8E72F0ABBC4F17F8D7FB525F01F720CC99FB35AFC1F8614DB04459715B37D223F2FEDCD21F1B66013DB39FC24A6694F018A1F4FED15282B236C41D3C9FA19E638E712B0586E103CBE703BEDE29F5F4745C9C8AB7AC28ADA590B
	7F5C789999695BB12E2A8E3C9505114500FFA946B9517E6537F23D9D7CCA9FF7AEDA37B1B1AB487A6BDA593ABD2D5A7A431EFCAF1151F2332FF4CCDA2AFA2BD4FBF3C192FB79BA522E20CF9EA8663677C825BD32049E1AA00321747448D4DC58FA9E23E7A90E312015472E4F3752789A4A248D626BB6AEFBF35A3E8CB24798F13DE6B6887749B3F628BEB90C6FGE0B647338375870D8B530913D9FA1D188B6D22GE2G6255BE33F6A0FD5D4A78ECCFBE28B82E74D9728F771048342200F13663C77B9373AE313D
	A83F246D09B367C8F288009257FE21176F45ABB684156FB0E50C39177C653BA68D793C8870511738FC7FA5B9C7A75F9003C3F8E0878B43E556111244EC09D2F62624F0DBC27899FC3C58BF96DBF9C973DB62F46F064B6C1D51B3B59DED0499F065G0DGCE00304B1C36B5A9E34635A77AC609EB970C0891A3AEEB34AC3EBC5237E2B15C11B6140B25177DE344EA0A0F396C9FA33E5C4AE344F828EB3F427D1FF52366B090222E8C2701755BB2E63F983FFECCB7BAEF64F35D68942E89FD253EDE70AD4A4ACED359
	E732FCEE18AC13F6303DGF5G36F61EE33A1945ED6EC0BB57471F9BE8CE844ACBGD6367318ECF9865A71F8A8678B509620964083908630C060FBD915DAB956DED6F02728512D3ED679BBB6DCF6B6D19ADF04711729F3BC14EA5449F21DDBF2492A9C1FCD43B3506D156C120B639AA84E6A76D2070A7975CD54364640186996D8A745156FC1DC89142BD55C25D244B920BCE061735F139E5CCF3CBFBD301F78C0FAE0BF71217451E31EC7530379110C3E9728CD1D8334ADD0E97E4EC53E0E2596AE23475A909787
	E5031563CED31C594AE40361DEEDED9EC59E312F2A6F160946AC6F0DBA07BE1619083F3F05767B0D3C018470174F8A5D22B2EEAE79EAC7065226CEF6576DD7754BFEE2CBE07DEA30FA41E344930D70FCF82AC32FE755D696A7BA60B9E065FA763E011F63B32DDC4F3E1309FA36845B83442BFBB2B9EBB41B10E78BE64BAA0DB8DE310D592C84957FB5459F546119ADDBEA60F96763D097EE63327D6B0C60327DFEC6E059DE13017A68F377F82D223F7CFE18112F56E9CADEA30B5D38952CCDD2A6761785AF44AD7E
	7DB3B373553AE07DF3B3518732C8B6492BF8738C3FAD8B5757270E351D167B55FDF63723BFEE778A9E07E475D50BBE2BD77200DB81670708BCF295635C6970DE879082B0D754784F46F82297676E708A5DA0A28D9E51CAC2BBE82EF88F170BD868138E38E200D3G0E8E6E63BCFEG1FCE41C2FD89A613F26968F1525B4D2934CF3F3B870CDE399D677A9F55170F0631E3F0FE40975A992D0C1EC7AF72B61381BF8BA092E099C01C1D7BB63503A8032729477605C20FBDC7436DD7E4FE20AD104C67BB400EB8785A
	566C8AF2C6697C41238E6E8FD6BB38DEEC6BC4399C0072CC008887570B8F8CDCAF22G374CB1DC8FD6B90269C5EA960EC7A47F7C2D0962CD12FF7EF6D6A677954DD0B7D962B67501ACEED3E3A4CE53A994DB4526FD23FB7F25E1B47B7657821BE34DAD1D036D7D34E20C4947F63FCB83096B7EC350B72693F9675BDDC858DDA4C20682F82E9920FE96G3EC7F6F2B95C3687F5601AE031CB9D3EB61B586169393E7FD0A914D87D11BD04F05F3BB51347C3E14BE7103C7037025E4315F4B199296B527C6558F7AFD4
	B3D96C27E7BF768B057A45DD087B519EFDEC0BF36399D18A406AFFB12C9E577027E4FDFD5630DCC146087EDF8E2BC7BD78B2015533353DDC4457967F729C62FCEACB507B51F4A7DBD30972B415EEA2ABFB2FE4B2195E5242ED3D8D6A67B8390E7E3A946D222A2325C2AFC4D215EE4C0D54B28343F8A336F05EACF5F2BD1DC147B4F4031D81A86F6671E95F8ECEF3443CFBC8ED485E00B42FE85C1858584DE86D6B5676A3BF37A5194D794AC5BE6742EE3E9F9FDD745F0F3DC9FAFE6EED755F0F5B0671DBE8755F0F
	7EE17DE7347A6F478925BF23E9E19B37D72B3AB99F9A6C4807C29770G205145EDC315437F9CF73AB49B95628AE62FE6D07BB753456CD21A0AFF1062A3F4F83637C5940F65A828ABF6F3FB15E1C4DA43203CC045D9A8AE8A4A2BD45CA2A337EB4443E9B7E4E1DCDC2716B752B266ECD87987196331BDB84E786D5B0F47B547F87DC4F6A6BE2946C8CE20B184E0AE40E200959E7E7D45FD3BC360B93523837EB9C22D3327074C1E3DB154D2199E1AD4159AEA2DCA8FCD2CCC8D35D41B4941AA12EE2D2E7C223AB243
	ECB9517B49556A6AFD56137D3E5E3C03E64B495E5E5E7516FD562FBE693D5AC0D2AD155F64EC28CCF5D82B0B4E9742B816AF70179AAAD09D5A988A13F560FC7C97FE15FEAB301B4BA701B25A52302B0C374C4EDEA9515B59A422595A98E8F7502B6D835355EDC6FD2E4179735FA56412A05F87A8FDA7AB4FD507B6A9715985A37DB6G70AF82AC870885C87701DD70F11FF2DE56AEF8D6F8BA6AC566881E91DCF0D6304C8D20694921F73F9499985AB331FB676752388DDA784C5F114829EFA5844B119D77317534
	AB4F79A51BA81E2DEDE5B62EEDBA54C5GC48344G44836449FCED6FD3FF9DB394F0A4C2B8ABD89DB4C34803DD4C1BE9E8F490595A3637CC6CAEFE7F847BF50E3B9F33BB62C59AD7487EF1050B628F4A7EF145EC752CB98EF561B24F9FFCE9223EF48F4885C0B9C0A3008CB03D07737210E19F99A3F750687654C2F06254054AECFDCBAFCE0CAFCF3779778B6F5174A432A7181DFE015A7DA5BD7EFC092678E5BD7EFCE931F33E2C043AD357387D3DC8FD6088A8AFD6F1CBA5EE13D7DE63B6F7B6357326DEB8F7G
	1AFBB97E910A777432DC65CC953F3BF3AC7B09E3E4ABE3FE9BFE6D5335E3F3D40FB9C79A991FFD760F7DF3095908F6781A6815A52B60E481F825602DF3BBA5EBBF096B25F9F2FA0621D398594BFD48FEA34A1D436D152E0363BA32A7D17A5ECF7BD6G1477818F7B90773A31DA21E120EFA4EF127CE0BA348987582014A37B343DE734BC3D8B6F047C2F2E18CF31AC0B7BB906FE6E67249A7C7D1C7B9274FE4AE1033F1F037D357ACE033F1FB33C7F9C033F1F437B33B3EF85BD73743F90C0AC26BFC2664DFB6925
	552B89D93B768FFF4067DC780507E39B7C3176EB57EA6D57529F99770F694DE7A5DCEF2A01BF837D5C4F94877859352FA50C6C1B3DBC280DDF582F5C8375EBE797DB67E049F1F88E469172D1BC480382EBBFE6740FG4447EBF0B30E51494E669F1628E773F566DF97DC57674067666059FC90F027G5AGBCG21GF38196GC4812C384EED618DC3B1BC19A1BC29E4A84A4A2A6C5562B549AA623D12121C2182457ADCB1221F66D273A2D623BE072F64790DF8E765D58E629A9B473B0C282BB62713E648984EE7
	C45FDDF279C417CF1225EB3439D2F75DE89ADEC72FB259DAE356A3AF62E1AD79B7405683548158EE7035E5E6AB77E0814E4B93B6BB6609706BA5194F75FEF6701CD4EC76585FBCFD140DEB3638E5595D0D63F3FC52BADC336C76D0A443252FC37972CAF6074C1AAA786CF52827CE310322197D6B3A085B4D5EE55D7B03AD37BE7E0F3C25F330B5FFE58A4E77B32640394086D360BCF70BE9746FA184D3706FA19CA67D77906C0C0CEE6367E6488DFEEE265B793999FE031F037FEA5776655FF46FD76C37FE0F7AEE
	699C6CEC79CD5B48FC606DEACEBA9A789083E09AG3FEBC039839A40B67FA519C7993BFC0059AEB6477CDE0C8B68BDCB0328FCB0413E82E075A9E51B484873CD1FACE76D129DE823EBA5D7D7231B75E77944E51D7AFC62AC054E95700CC53AE91B5B0C7AB606C15626961E6D037C4C2FB7E2FEC517EEAC55E56CCBAF1C28EF2C292A283D52D0D5FF2E363632227ECAC5EDD3C5CB8361F1435CDEFDBCF4B8F3F3503B573EC1461FB065B9F0E5133246886301D1E8B8F52E3E666239330D0128E07EC0F2B64FB1C441
	585B681860B7937EDBFDAC90AEFE903F7B6F47EDD056720E0147C0850F5DCB2A5FFCABE1FB10359C9FE2EB18BE2465E1087DC8B2136137C63D0F88AE7B45A5936BF7E85C7D5819B3FDB31EB9AF429A1683448398EE026C5C64E7CE2C8965FD64F9CA3F5090EDF50293AC12487C02C5A5A6227D387F427C117FD07D11F298DBD6470F4841BD6B15EC3203F01B76C48E6A26C3444304637E09360353DB576E599C548B1FD5F0B9A517D8A758947CF3B9B81725DF96FDF5229774C6B957DF4841F35EA64882AB3FCCF3
	749E368425EFF48E2E812C334B27DCAECAFD2213553D69D7E76DBFA3783AD85D6A7541ED7E6F570FFD5612DC7A1D8C1C110D7D9E055E14DCFAA75B516193D5BEE4661AD4DC0D2F4E0DCE045B456CD70248F5BD62A633DFD170DC8600F7154CFEED331BC7110955B719AC33BDFB4C4177ACB9847C11901E7F5C5F0EF75AFE794F13DE49D6A958D9821469649A87775165901E777BD42279B8A8C781AC8F61B6A83D9357DAA562F778DC97FBC53DAE96EC7BC58277597D690E8DD1F470B6CD977149687DD5FBB99947
	B7B1818CB6DB0F290B5F7BB75E4657B3075EFB04C1B99CE051ED1CC7A54241714C29B36EEC2E270DFC2E256ADC37EF273963C9201FG6513F80E601D827A8DB714BD1338DEFDBFF7B7BCAF881DA33FD082E41D576D0150301FE1298B2A58110CEC3D9D198E6305CF9ABD4EE5FB75CA2B7E8CB0FDAFA51147F27E6DA3157122A679FFFFE3590A71E39C6073EECF4BBC8CD087E08398G188E908E9089908D9083908FE01A8C7CGB888F08A20EDB26741CFB66D9A05870A40D22759AB683F051835195A9F41E5F32A
	BE6F77B72FD7ECCA3D6801B0GF60DFD662654BF3299ED0BCF3A2E686E231B83FF137878E65CC3372F7EE4658997B8F5220F71BDE602F67FFF475D0F69763F0BDC37BD402F7913791D7B4E8E2E33ABA6F339BB5F01ED7327C0996054942E3365F6147533EEAF35490C1EA7C6646F3F5DBA7EF9027D62465D0F651B6E27F628FD0AFF9E6A87946F1C621F075AA9F1FE75C15D72A9DC77724DE6F89E1F8AF2879082B08BA082A08AE099406A29DC9E8F94940FA20F6C82C993C7363E8DDD232D4FF0C730BC9B3B27B5
	5F613F3EBFD2FC499D7E6B7BE1A6DF5FE1281BF3870F83FEE29CEDFE929AEC7E5FD3DB15976ABFBFE1FFDF946ABF7F346DFC7E92281B9E2AFED7D100E32A718FB3D5BC3EADC05F853F5D168427F41D6AE383B4F459D9DCE326BEE12CB2974395D11CE19A4331BDBC0DE9FF9E3BEFA98A36C790E51F06636E1C2593CE06C6412E763936B57859BF38F56C33FF6A5640F1F6585611F176FDDB8347595F5D2A0F3399EE4ED654CFBB5839E6355DC3AA2EC5454D23E3A22EE9189F96C169C27C29E13E5813142EDE0AEB
	D6F0BF5661DA945C222DE8C7E1498E3C1CD0FC332DE8A7BBA42762D3BDCF5BE0BEC4D679719225D17288BA3ADFD1F1AD2A6E3FABCE4CE62C19E03F120E09591A1FD17BD9BC4DDF97DE237803537CF52135C33D8B003A70E9DC979F1008CACB386EFD0FF46BE3722FEE699C4AF7C66660FA7439F9ECBDBAE08E2CC7C74CA3756828B9309ED51BC76AD10DB22ED77D900E614F19B14F47F2886CEACE4B3314F4711CCA7A1D5AFD3036E75BF46F7347F5E7BB317DFB6E164E41567C06FAA75E2C2E79236D5ABAD38B35
	77FB375F7AB54F3F25F3B0BFDF205F48ABE790B51F8CDF50E754705CFCAAF43D522171E1196EDD683875BC39FECB67E077C0D5DD7C9EE87E1DA37D83FE3F43E19134093ED33B87DAF927E68F59F82B54FB2555C146635F1BE37F643B52B2736F5246AB39CB9B0F7938F2974F959D3EEB3C39221FE28ADE99E70B1AF372849C0710C0B8B64E3BBA3D689F66634D0731965DB5FA4C457AFDB6ACD60B06BEAB47684774F4FA97770BDECF64387B3B38DF34B901630E287E533BAA6E286ABFB591FE6F65C89C02E7447B
	A44567A56BF8CA2034069E6AAB2E9DA8CB7A5543C909A4390624B263DCC3125142561094EA00EBC8929867BE7C2368E4BD782E12A14A32EF0165F1682E1B7AB8B4EECAFCC230DC643E3B992F2A6F760FC397A7639AC3GBF67EE6E071A12711EB418DF5850ADB81D32606C2AF05950C72C445C936167447349B83E0F37D1AAD95D927A77AB1D3ABE7F8FD0CB8788568D4DD35097GG9CC5GGD0CB818294G94G88G88G0B03B6AC568D4DD35097GG9CC5GG8CGGGGGGGGGGGGGGGGG
	E2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG8A98GGGG
**end of data**/
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

			java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
			constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 1;
			constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsPointSelectionPanel.weightx = 1.0;
			constraintsPointSelectionPanel.weighty = 1.0;
			constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getPointSelectionPanel(), constraintsPointSelectionPanel);

			java.awt.GridBagConstraints constraintsStatusImageTableScrollPane = new java.awt.GridBagConstraints();
			constraintsStatusImageTableScrollPane.gridx = 1; constraintsStatusImageTableScrollPane.gridy = 1;
			constraintsStatusImageTableScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsStatusImageTableScrollPane.weightx = 1.0;
			constraintsStatusImageTableScrollPane.weighty = 1.0;
			constraintsStatusImageTableScrollPane.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getStatusImageTableScrollPane(), constraintsStatusImageTableScrollPane);

			java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
			constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
			constraintsLinkToPanel.gridwidth = 2;
			constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getLinkToPanel(), constraintsLinkToPanel);
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
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the LinkToPanel property value.
 * @return com.cannontech.esub.editor.element.LinkToPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private LinkToPanel getLinkToPanel() {
	if (ivjLinkToPanel == null) {
		try {
			ivjLinkToPanel = new com.cannontech.esub.editor.element.LinkToPanel();
			ivjLinkToPanel.setName("LinkToPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjLinkToPanel;
}
/**
 * Return the PointSelectionPanel property value.
 * @return com.cannontech.esub.editor.element.PointSelectionPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private PointSelectionPanel getPointSelectionPanel() {
	if (ivjPointSelectionPanel == null) {
		try {
			ivjPointSelectionPanel = new com.cannontech.esub.editor.element.PointSelectionPanel();
			ivjPointSelectionPanel.setName("PointSelectionPanel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointSelectionPanel;
}
/**
 * Return the StateImageTableModel property value.
 * @return com.cannontech.esub.editor.element.StateImageTableModel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private StateImageTableModel getStateImageTableModel() {
	if (ivjStateImageTableModel == null) {
		try {
			ivjStateImageTableModel = new com.cannontech.esub.editor.element.StateImageTableModel();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStateImageTableModel;
}
/**
 * Return the StatusImageTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getStatusImageTable() {
	if (ivjStatusImageTable == null) {
		try {
			ivjStatusImageTable = new javax.swing.JTable();
			ivjStatusImageTable.setName("StatusImageTable");
			getStatusImageTableScrollPane().setColumnHeaderView(ivjStatusImageTable.getTableHeader());
			getStatusImageTableScrollPane().getViewport().setBackingStoreEnabled(true);
			ivjStatusImageTable.setBounds(0, 0, 200, 200);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusImageTable;
}
/**
 * Return the StatusImageTableScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getStatusImageTableScrollPane() {
	if (ivjStatusImageTableScrollPane == null) {
		try {
			ivjStatusImageTableScrollPane = new javax.swing.JScrollPane();
			ivjStatusImageTableScrollPane.setName("StatusImageTableScrollPane");
			ivjStatusImageTableScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjStatusImageTableScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			getStatusImageTableScrollPane().setViewportView(getStatusImageTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusImageTableScrollPane;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	stateImage.setLinkTo( getLinkToPanel().getLinkTo() );
	stateImage.setPoint(getPointSelectionPanel().getSelectedPoint());

	String[] states = getStateImageTableModel().getStates();
	stateImage.setStates(states);

	for( int i = 0; i < states.length; i++ ) {	
		String image = 	getStateImageTableModel().getImage(states[i]);
//		String imageRel = Util.getRelativePath( stateImage.getDrawing(), image);			
		stateImage.setAbsoluteImagePath(states[i], image);		
	}

	stateImage.setState(states[0]);
	
	return stateImage;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
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
	connPtoP1SetTarget();
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StateImageEditorPanel");
		setLayout(new java.awt.BorderLayout());
		setSize(444, 407);
		add(getJPanel2(), "North");
		add(getJPanel1(), "Center");
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);

	getStatusImageTable().addMouseListener( new java.awt.event.MouseAdapter() {
		public void mousePressed(java.awt.event.MouseEvent e) {
			int row = getStatusImageTable().rowAtPoint(e.getPoint());
			int col = getStatusImageTable().columnAtPoint(e.getPoint());

			//must be a image column
			if( col == 1 ) {
				final javax.swing.JFileChooser fc = com.cannontech.esub.util.ImageChooser.getInstance();	
				
				final String iPath = (String) getStateImageTableModel().getValueAt(row, col);
				if(iPath != null && !iPath.equalsIgnoreCase("X.gif")) {
						
						SwingUtilities.invokeLater( new Runnable() {
							public void run() {
								fc.setSelectedFile(new File(iPath));		
							}
						} );
						
//						fc.
					}
				 
				
				//fc.setCurrentDirectory( new File(stateImage.getDrawing().getFileName()).getParentFile());			
				int returnVal = fc.showDialog(StateImageEditorPanel.this, "Attach");				
                if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
                    java.io.File file = fc.getSelectedFile();
                    String img = file.getPath();
                    getStateImageTableModel().setValueAt(img, row, col);
                    getStatusImageTable().tableChanged(new TableModelEvent(getStateImageTableModel()));
                } 
                

			}
			
		}
	}
	);
	// user code end
}
/**
 * Creation date: (1/23/2002 11:12:06 AM)
 * @return boolean
 */
public boolean isInputValid() {
	return (getPointSelectionPanel().getSelectedPoint() != null);
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		StateImageEditorPanel aStateImageEditorPanel;
		aStateImageEditorPanel = new StateImageEditorPanel();
		frame.setContentPane(aStateImageEditorPanel);
		frame.setSize(aStateImageEditorPanel.getSize());
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
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) {
	stateImage = (StateImage) o;

	// Set link
	getLinkToPanel().setLinkTo(stateImage.getLinkTo());

	// Set selected point
	getPointSelectionPanel().selectPoint(stateImage.getPoint());
	
	// Set states and images
//	changeStateImageTable(stateImage, stateImage.getPoint());

}
/**
 * Creation date: (12/18/2001 4:16:51 PM)
 * @param evt javax.swing.event.TreeSelectionEvent
 */
public void valueChanged(TreeSelectionEvent evt) {
	com.cannontech.database.data.lite.LitePoint p = getPointSelectionPanel().getSelectedPoint();

	if( p != null )
		changeStateImageTable(this.stateImage, p);
		
	fireInputUpdate();
}

}

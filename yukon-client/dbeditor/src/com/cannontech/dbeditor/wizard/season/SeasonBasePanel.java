package com.cannontech.dbeditor.wizard.season;

import java.text.DateFormatSymbols;
import com.cannontech.database.data.season.Season;
import java.util.StringTokenizer;
/**
 * Insert the type's description here.
 * Creation date: (5/5/2004 11:23:43 AM)
 * @author: 
 */
public class SeasonBasePanel extends com.cannontech.common.gui.util.DataInputPanel {

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SeasonBasePanel.this.getJComboBoxMonths()) 
				connEtoC2(e);
			if (e.getSource() == SeasonBasePanel.this.getJComboBoxDays()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == SeasonBasePanel.this.getJTextFieldSeasonName()) 
				connEtoC1(e);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxDays = null;
	private javax.swing.JComboBox ivjJComboBoxMonths = null;
	private javax.swing.JLabel ivjJLabelDay = null;
	private javax.swing.JLabel ivjJLabelMonth = null;
	private javax.swing.JLabel ivjJLabelSeasonID = null;
	private javax.swing.JLabel ivjJLabelSeasonName = null;
	private javax.swing.JPanel ivjJPanelDate = null;
	private javax.swing.JTextField ivjJTextFieldSeasonName = null;
	private javax.swing.JLabel ivjSeasonIDField = null;
	
	public static final DateFormatSymbols DATES = new DateFormatSymbols();
/**
 * Season constructor comment.
 */
public SeasonBasePanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextFieldSeasonName.caret.caretUpdate(javax.swing.event.CaretEvent) --> SeasonBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JComboBoxMonths.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (JComboBoxDays.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF5E125B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8BD0DC4711A416FDCA2278C8D93ED392D2C5E4DD8E4744A5DFF0C515A3A96272DD648BD731D372D91733F3A451453AB3F148C52ED3DC0AB36C0297D9BF1010054CEFA5A1E0510A3F3430AC181F789949C8A00CA5AC71E09F30E259DD6FBEFE7AD857BD73663D4772160F92DF1DCA4DFB53B3BD5DBD535353BD7316E466C726A69BB289C9CEA572BFDBB2A1C9ED04FC7E9FFE5242F12450E6A21AFF47
	GB2C8F9CF8ABC5321AE4BEDB32513FDF5E9D0AE0072D6274DF4845E8F1103F5D18D709202CF9854457E75764445793CF60473B15172CB1F6C0567FE00E240F5832CABC47E2FFD12A563FDD05EAF3FA3A49B8869A842B6F176AC8D7EC539DD8E541583D497E31B743BB9F2FDB86022G12GD20A6536705CCD253B5FDEA3379B7CD2945971CFF906FD48CB53BF075BB2ED247CB413EF5001B0121B831570F474CC0CB50CF8A7CBC7840F476B11441171D2673068F4C9DEFF690C6B2660F71686C4A16075586D3D74
	8963E1C1D9FFE5CF237D9032957A0E2038EBF5B8BEFD706E86D8E56172BE374FA14B33F6A20AA4BC1291D25E848B13B325C415F7CB2FA9B526847B2F3D3BD726EB0572EDG264B0D2BF57724CB339090FB840F6816F57A4CDD2E534E922ED32A8DF9161F3219BAG06CEF1FCBC454FC2B99CA06A9457353E086BBAF06BBBE467F01361C088DD63CEB19D371FD2F5F55B1BD20FD2DB7B4CD1341782AB708598848883088408834826B66E48AA01E7533054621D1C74FA1AFC3EE63753FF5E6B945D70EE37C30D0C3B
	A40EF87DCEC2189D3FE2376A4843E12BF53E8DA3B05DBEA05C6E22AFBDCEA449E6C85551AD66F933A1D16E031CD6F52BB90D637693EAA3E6F8C7BA140761FFC67137B5F8B667CE0A4772DA285BF91A0F7340FEAECBF74AFA12DE51E2B06948326E299A43E0A913C1AAD5E5E1E3FCB59B47B8927031GDBG9281761659CC56B2BE468FE4D5ADB5461DE0EBFE0F606E1D9A7689FE29F94A65F60AFEF92C5F3D3B58D86B8123BB3FE2BAB6AED1D55AB9FA715B4D3A6372E2D41121230C0D47D019BAAE0C7783A1F907
	15AD340D7D4A9C4E51B531360C4D21D1415FC7719BB4F8B637FBA81E1621AE2B5CE6E26D9BEB28AD14875BC8B7452FAD8F36111F58380DC4C3DDFA054D44667777B968CFA5A80783C4814481A4G6C82C82F64F37CDCD659B257D10FE6FE996D670F3E8DCFF1D6728B1D1E00A4F8C644FE416F92065DE20048ED9EBA0A328D4F23DBC757778870E897BD22DF10DCDE8F74AECC0AE0CC8156666BC7B3300DDF2435FD2EC99198CC7A886F775123076069968252E51FD310C435852BFF62A85A4434508FBD3A50F513
	21CA9CAF835DAF4B613D3B120FD5E19E62B6C0B9D699976BBB8761799E983768365BDBDDA3A806601F63B63A152E4DE7311FAA1BE9F395DF1B2F8EF09BEC5D77ADE2FD3EC95786919A2B19BD8DD4875B60B65AF79860A381622A799EFC23EEG1E48EBA5EB037A09C5E8D087F56D5DC7F918D658BFC26AA2F5C6794946FBEDBE6A17GFA6D8248B94347FC6C9DB333C55C1C38753D6F8743911D6A8C30364EEC5CDB44E951A3350ABE5163041700BC7793593BD53B9C75AAB673B145231FEA814389A878196C8C06
	07BE34D64E6B6F52FAC1127C2E61A9C96C95A5496599D3697F024A9218678283322EFFE5C6DD2BC147EE00C1C557741CC39A7B6A76FAA726FCFD38F2E4BB7D9A5D0FFD50FE7599BE7FF7ED9813C5AB654F5132DA1FE19B3807F9437E166F1BCB6E0907F1196D980F369A9E6B98E53BE278CB949F8772EE87C8BF4B755ECAE55236DF29FCCB4B74DB4AC3AB4F1F13873389E3EEE81BFEABD75F2653F375ED7A6D5C45EDB6AFD74FE6D95D49DC5451BB63512C0112DC7407A3DE0FC7248BD1A1A1AC8EFD358F75F0F9
	DC5287AE6718A8753AEE0A60F34F22AD9F25BE37865E3B4FF2BFB9F3027B49021B0F12386BB93A71543A3F29B3ECB84B6CF353D955CFB219CE6521BD08EEF192862A45BB6511983E027AF3999FB0D25CCBE7F04C4C45DDFDB006EDAE516DE4F1708558F52CA3307425D27A176DA645B704E9E136B4B083E3D6CAA7243485EB4F612B45FC7AF403B9A80C360E3A7CE22747B745FB3074DB4A89639D7161A4638DFB6C303759BBFB9EC2747100D520A3D94A9EBD22FF546B1F941D14F729B0A3490C1BE8754AB8D748
	36FA089CBDBCB11FF72BB077E9F22E147D5EB347F0EE36421CA5822458385F7BD69E56F33FA7BB77962F5B4D4C8A6A46EDE8B3C79C199ABB4831311CF33FD25F32C43DB7E4BD1363AB7969E87F6247D3A204AD68F85F9367F86C11FFB0282EDFF0CF29B14D2379FB60695678EC060FB06B5B63FAB37A6989257FEF185FA4981B30BE597A59A8E01FBE3FEB9236B85C04645CB9662EB6F75E88FAE01E1D844F149A1E837EE39B7647F3540DB5C8F7FF8D4F27F320DC8D50D743E843EB58B830F69D353C5DAEA8E756
	C25BDA3DF6FB6D3C1D8475EBF45AC557E23B72BA1EC716A938F60A7B8DB84D2F2570D87DFC3AB60FF7862AF557BFC2D22D1C975B03F318B70E53338F40D75B39FD190FE9775526C040BB6222A124BCC7BFBE26758D8C376558DB7084E38EC0BE6394E779BC7F74984A0AB1260C16676A31D375E4E14CFC0EE65BBCECE919216B264F5B82E1EE8718CA0B62BA7BDD628C6163F027CDFBBE90FB40E25893E29CBAE47D47B563D08EB9B313694BF64C55744E81C6281025E3D32E52A94905412CA089F455EA737FBC12
	A66B97E567F97E2B869A27C1F9A7C0FA1D4D44F20E4769BE0CED0B81B78440565CE3C66D1ADBD747E217B8F8B2DFF43896EDE2054ED69E7327AD87599C85B99CBE0E6B8FB54AFC639FDFCF9CF75F52BDFBD8FDA05330250E0D5F2EBAF59C2B0F1527F6GB07DBE2E657AF5B8EC26B08757AF5C2655EF936093GD26A39FEDBEB71DCE9653E1567F2E76C1CEF967459D34F79EE56653B3A1E6F259FD4EA57D2DCC2F1483594D1AF2F697A6035740B821C7FF840A7547335F479F806E6ADDD826B95BCE3606D14F865
	041179B539ECBA114A7D9A6131FEFEA14A3E837ACCEF30190A819A81069AB80F87CFACF946F0EEF611B30693A4B8DF8FEBD06359358D8BE3DB16CF8F9BF12C239AD8BE3DC7417BA8FE33864F66E687DD8BD85E8EF55D6F7258B326A6187F46F767730C8FAAB33A27965015861513026858BAB8D91C435739E659586DEADC435673AF0CE9B22F34C61E67CE95A1EDA3148781A400F0009800E781F6B472B9F969041EFF1BBFA7211C1B1ABBB1BDE7EA6FEDFC6EDFC097BCBE19CDFA63B3664E569F9F9E7F487BEA47
	AD6D3EEAEDE23E2A871ECEGB606BF34F11FF83B094FF75D0273436569533900AED81F18A0FD188CFFD4E408EF627378E4B162D21AE13FEE66F3E6AF5827D92BBDDE17C7D256694B85989FF87D2EB117076392A9EE446B27C1BE439595064EBFCA8B75730F6AC27D7C43DE38F8CE5DDC98AABFE1792F1D6EC78520DF8DG43FD140C38C1A8CB4DFCBE46691E338E4A1BG36GA4816CED617553349E631F82403582B8DB187FE4752F9B7472D96CA7982705CD1AFA4EAF384DBAC546C3C6BD9ECB65105ABC9647F8
	0FCE9B743F3974794C5B8768F88782E200AECE5C65653A172543C4A7F743540770BC61151A5DF21BF3123725C2315B170D5CEED76969883868A03CFCE6D2945A169EA8523725B5C5E843816F14FFC48C3617070A3214BA555BF4CA62A42AA1475F14DE927C10FF8653BFC269D9DDA87A6822D0B649466871DA6D98D5926EF3FEE7413EF5FC8E8D3058BA8E3B05360B7245357078EA4601FBCAC12B4D64G68EB657692EFDB6C7C5B8D6D42DA670FB9134FEF574AD7254CE14A023DC68BEB212F28D63DB97CEEB16E
	0133CA797B34BC27703BC850073805B96F94B81B74F32853B8517A55D868F79B40CE39FFB6DECF4C20AD0F7A05C99826DE1FB8C292A92D144CE987206C86D883908D90FF0E47D8248B5733DE964F6A3B0FE0DE5755AD8C0B6E603A01B37B036ABADB39AEFFF7816706E3E9570A1EB565189F305904E5A5729C731BD35A382C3A0D6DA183EDBCEEFB2DCA2F5E2D5437D9746A435A383FFB036E61EB213CD141DDA864BE70E985571005384CF61B495A4EED687EDBDA1F5388783EF6F50E475B757D5BED9DFCB860B6
	8461D92C748A355FCD6DACD65A2D60F7D27C968D1E45D0F7A89E4BC9A8CF87177F17B62E53A68557174DF5CA6B64386C9A5EAEC741BD24686E6E6479F045503C988923EE3B3D5D6FF2B68BE35D54DA591AFC106EB1A8E3D8A7DF1331046A8465E78136F77298FB361067E9515FD8CF3A431AF56FF1937AAC865245462A20CB0D3143A0CFE3BCF7A6F31EC3D03F2E8BF105862D9D204DBDE7E071812B9F18D70FBE8AD286CD7D676755233C5F1BC79FBB2FBED5C34FF47B51C92E5B2E9FEDA46EA74BC35FD14BBA25
	3C471EE9AF641008974A659C74F723A17CA0DDF1CC7F1FCFF1BF68F80F47D956EA0E0BD4F0F7ACBCFECD69D6724794EC63730B23225FAFBA5991A3113A31FDE98A36F740FB87C0A24509052DBABAE847412801CDC0938FF0D82ECB68E6764B4F15A19F4AC23F85CE4BB5B9B5C939B35E1354862B4FC3EEF31E5B655FD2FF6F06F218027B6AB40FE9B6A8383BB79137994A898A6E77E9FC5D6FD0F0DF394EEDBF6D82477D70AC377D018BFC5D7F56017E4F8D65D5G91G1B143AB7E85D96A86F38404EFA709CC73E
	77F1F0FF0134CA1CF3C15FCFC40440EBE115268D63912170505E876C8EC13F1CB3F6ED0C44667CF55BCA66BC737D79F3DE7C3EBA67ECEE6FD0BFD48D78467739DDD556AC45C34B479D44A3EC810FD8EABFEBG3F6EFDBE9F8FAD48EB178266FB4B1351FFBD2D61417035947F2C864FFC72D3045F353C08BA76709C43D702EBF9831463G3682A48124DF04BD66A24FB9DAAC65E409BC314F6B6B06A0502DC98118FEFFC83E37BB7E9F18382F0A796EB74926871212C31D17B5DE146F7865E76473B9944F74B3DAD1
	3FF0280B86F896E0BB40AE004CCBDC3FAFD82CCCBFBC5AEB12A4E1E41C1E9AF26570A0CFC5E3A043F47B5514DE3CDFFC09496138243FF6FAG6F8C2A1B05721ACBFA79E4C5359E0FC839EDCC889E1B817FE2D0DD9214D3FA75F8FC56221D23AC390DD5FEB676AE6451570B5F12E5E368A97B4C1642FCF8FA449933F2G566A026142CB79620A6AF22B39324AA24FED640C769C2B662D521067D86BE45E5172D3CA4BA579149F21FBBA7B17746EC29C1771B755C431291F6F65B6157358A3247505246493A178EE13
	79D57729FB7B2EFED3AA6BF3742C2A7797271623B7EB7B7DDB98C78FFB7DB4E1E1F174734A79049BF82D82088358A273E5ED1E1E8E0E3593211EDCE6EDD85F2B26691EA54C866DD9D1F45F49023685G8CF7FCB762FA20BCF8197B3AA72EAF666B0C322DF0FF379A68A2AEB3DF07650D1703FDDD925D33E2813F79B25F577B86D1C696A5502BB1D976566B3B95FC2B20669D3B86B1378C3ACD136BF2BE5E272D435BAE4263754A7A60F89B4F81F6DC667178A78E3D7A34FE1BC94F36581CFF4E265AD4F40DBA7F0F
	59961B7FAAF97EC3777B63DA355F7CBA355F6DE71753AFDB3FE7F1587937D81A774D64DE6E70178B0C474FBFD59E4CBF4F1504BEE388AB51BFE3F820C47F3CEACD49627765EBCBC25F17FF35C4FBDF4E76175F5421AD65006D54G8C8278GD675733D45F26AB821FB0B4E3EF24EB906E79C78C51544796F3506567510F5696F30F22C18438EFBA549BB097DF37CF1AB4E176475D124FC8EE845B1723B4646A556D4461F365218D69C25E89603265118B692745A584F635249E99E5346AA3824EC9E53BE2B600E66
	A3EE9B14F7A8387DF714B877830E3B2FD309F395DCEFB5676BD6F0B1551C6F2C024B3B49792ED1F07FFD04730DD4F01307B95F988597554E79268CF05C17AD1CB6CB41F5CDF0D92C8A6E31895EDF4DG4FA1FFFCD53B3F25FFA54B40FDE330FD2F9EE0BEFC6DC070BD4D45FA2AA360378DF0DB9A2ABA854FFE6146421BC8C076783DBE51AF4DB1AC3D4916ED740B8D685BD3BE34194CG2DG03GBE0055G91G9B811E86F891E087C05A95483D814A819A817AG462FF0B92A4FD89611C3CE4029E17B85F6BE4D
	E250BF5C625FC25E063EE22E70BC7EFBEFE1FC392CBB4DBE1764961B698E4A7247F3377939F962951E5B3FBEC54F5B2E5ACC8E003E2BBC575CB847F3F277D55E76E7F3743BCDA84782EC417654973B9A89613E7855B4B389690B2B2A4FD84D3051DB985D7AA6156EAF2FAF07EEF9718C5FAF3A686F86587BE42B4A6B0AE3B93C58B83828ED61DE1C280C4D138E0EAB9864737CBFF521FD519BF5CB7BA2936DD3849F52B767134FDE77BA689D4568E8C0D4FCDE864DD7992EB350638D38704C166915BF0D5FD4C840
	08699CFE8EF5E6B1C68475BF03A06F78A00FED0E66D0BFG6548C19EF71467E39F23E0D32CCD9B75C771D03FF51047BFA76FAC957FE8A1DEF30600761476D1F07C934DFC99604D9FF11B7AF61BAA7BCB5407F5C35D40C7DC76F66A43D6C1B9DC41E5531CB1964A1BBF62322EBA3C142C290B483AF3012C4FD17F17F28D5E2FF13ECFD17F6700F247B5AE7F3F34237CCC0627A6D6129B874BB08BFD7E277C4EFC54FD9D5837FC066FB8C9C2789758B53B403E5F9F3EA10EC8FD4218C253DF0FFC15FD1D39BF396EA3
	9A8F62B7EBAE416D3AC9BF936905061EB179BB200650F69E5530341DBF51203F67FE27E1611E9B5B203F67BE55205DF3996E078DB806E360CF8714F6718A6EC385779C6D93F19F381C90D5F37C8BD4AE44F708481063DF22F24DD05C000C7B058677A13FCFEC40358AAA0FFB153B4344863A16DCEEB7ABA751B6AE8FACDDE5BCDE23B23AFC02C66E57959C4A4DE2C7636D254E86D6DD534BD7B316244B8BAAB33AAECA97F9ED69B3AD0418EB7CDBFD4FB47DD660DA70D94B8C45EF3996FC56C22679D9CB825435DE
	67E7BDCF4E5D5BD94F251B749B066B417C2FD2FC4C75E07E5B66B87FB828AB98627CBD751C7F6AA1F58CA264770D70FC9AE06BD070F95EF79D1CAED1D3C784761E894FE2G072056B1FFD154054D188F7E0F6C457219729A48E7AAADB2DD57FBAA5D7E7C65533DF2D1250B38F5BC182E8DB6006263CE70815E3163D6F3B9EE0996AE6B7EDE2248DD5127763335EA797C67AE2BF407EEAEC5D7E163F4917DAA5D4BC716CF37F3C025938E2FCC5F6CAB2A3E8DD755FEAC1665734F3A2652B5CC9C53675F844E528D6C
	AB60FF25C56179B04DD3593DF55CA14C65DD9E374BA33630DF0B907EFD00C0633B9E302DF101475C1F25F78B37219CAEF05B162609E22FF7B46FEFCEFF7AB9F07E7F810F011B2ACE02667D3F66BEFD5E461B1FA68FB6277757737CADC2196748BCA567D4F04979BC5F0AD5F0BB9DBC9F5CBB4CF1B9F9BC27B3AB38679C1CC735027BFDAE675121602E64F19EFD8AEED66131C1414567F09E1B06F92CB9FB9B77E289539434FF42E32CF0EA477150EE2B52764BD3293CAD616BE21301A8B40F53B1C99B3119B2C7F8
	8CB6045B2F9C03E5657FA9B1EB63C8F09CF80C726B867C40880F834F5131D88565F08577BA9D0BD8A8EFD6643AAFD5156B633CBFA5BE5D31C02E2FD3FE4489736B64B23CC6473E864A2DCEAE433F535FB733311D1EDA19BFB46CD6E99F4E4B8B26D5FD6059B37368CEE208A53FF76CD17B48D8C19F93FBD5BA4131FC3A877729F42F66AE1F6E097D2A4C098754BEFE10337CBE8EE428F4815BF26858EF560F1F2F429354DF1354001F9CDC583E6941FFFDA17AFAEB260198BA491B63B49E6DA429B398584E624F82
	311BB5C0FBC0216D2441E742F4155D784BC70C86128204AC986FA4A998D1833D1F75E304E01C7602C1AB2E24EE00F7829F13CFD09B39EFAC61AA4459445945DD5DBC2F4DE715B6C51AB6FBF55A603FB5E04B9F6A14107E8600C19E4803735BC8E49F7BCC450CDF36584D9259A317BBDB31E40481D7A8B0C9F187157CE45FBC5B7F072F1F79D57C4F7A83C672E6A7B90A69F02F6827991168442F48641FD036115DBE7C2C90AF91E0G8374119A10665CE2B7499C3D04BF1788C822330967DA81099C6EC6CEE5F8C6
	D356A18446A54C0F6A3AC90A87FF6CC98E32EA376099ABE309D79D1D056426A1AAAF35C71077901597FBA9D5FAB9747787CE9E6B5A3649C88CED64AD37D7F03689A3108EF6125D5238DF8C0CFB5D4ED95F3B0D34233499FE0AE1046E329575D159D27C16C891FAE276EBEB76ED5F578603BF6BA7E534A51EB214AAAD1CAD63029FD8097ECE89FF3C6A7597A8F51F49138A7F397D482001A44BCA6250E74E7AA4104AAFF88278B1B8C8E08E1200D7A99D15BE74CC79430F3EFE05F62412CAA41F8D9910160D095E49
	322651D1080E148EE07C207BDC25FBECD39A5466606C178EF7BD7F47E1186FD6E21CACAF27FFAB685FCA7CDBC1B19594D3D1890318C4967D97DCBFCB8DAECBE2C6222CC91C6652B618A969273FFEE67D3FB91292C13BB612CABF980741CED7DBDD181A9C967D8FC4BF1C3BE7ED4297F0AEFCE8923C5FB7C705C917FB8EDEDA29571EC5FC712C32FA505F9F14D772BBA156C2C60852418576075837979A9222D967B974EF7E0A0789290ED15A934890DF8A676BD4296A1421514994D2EEA6A5CD73C2481D228CA2CB
	6A99FFEC7567D528D1D721FAD521CC223ADA13C4B2E4A756AB89FE490C1F59C944C42F76502925368AF3F6737F3F89EA1FD52682E5E9D234436D3314309B6613BAED96ABA17DE98D7D0145A864BBEC4BDD6D5D424E7B8F043CBB9FF433FC5C678E3EDBA83A08E3978D784DEEFE56BB53031204BAF36B1D945CEEC9F0CFB4F91CF87E56BCA7096A1D1975A2769F60ED64CAD6D7F591653E212179DFD0CB878826E4A2C12998GG98C7GGD0CB818294G94G88G88GF5E125B026E4A2C12998GG98C7GG8C
	GGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6398GGGG
**end of data**/
}
/**
 * Return the JComboBoxDays property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxDays() {
	if (ivjJComboBoxDays == null) {
		try {
			ivjJComboBoxDays = new javax.swing.JComboBox();
			ivjJComboBoxDays.setName("JComboBoxDays");
			ivjJComboBoxDays.setMinimumSize(new java.awt.Dimension(130, 23));
			// user code begin {1}
			ivjJComboBoxDays.addItem("1");
			ivjJComboBoxDays.addItem("2");
			ivjJComboBoxDays.addItem("3");
			ivjJComboBoxDays.addItem("4");
			ivjJComboBoxDays.addItem("5");
			ivjJComboBoxDays.addItem("6");
			ivjJComboBoxDays.addItem("7");
			ivjJComboBoxDays.addItem("8");
			ivjJComboBoxDays.addItem("9");
			ivjJComboBoxDays.addItem("10");
			ivjJComboBoxDays.addItem("11");
			ivjJComboBoxDays.addItem("12");
			ivjJComboBoxDays.addItem("13");
			ivjJComboBoxDays.addItem("14");
			ivjJComboBoxDays.addItem("15");
			ivjJComboBoxDays.addItem("16");
			ivjJComboBoxDays.addItem("17");
			ivjJComboBoxDays.addItem("18");
			ivjJComboBoxDays.addItem("19");
			ivjJComboBoxDays.addItem("20");
			ivjJComboBoxDays.addItem("21");
			ivjJComboBoxDays.addItem("22");
			ivjJComboBoxDays.addItem("23");
			ivjJComboBoxDays.addItem("24");
			ivjJComboBoxDays.addItem("25");
			ivjJComboBoxDays.addItem("26");
			ivjJComboBoxDays.addItem("27");
			ivjJComboBoxDays.addItem("28");
			ivjJComboBoxDays.addItem("29");
			ivjJComboBoxDays.addItem("30");
			ivjJComboBoxDays.addItem("31");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxDays;
}
/**
 * Return the JComboBoxMonths property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxMonths() {
	if (ivjJComboBoxMonths == null) {
		try {
			ivjJComboBoxMonths = new javax.swing.JComboBox();
			ivjJComboBoxMonths.setName("JComboBoxMonths");
			ivjJComboBoxMonths.setMinimumSize(new java.awt.Dimension(130, 23));
			// user code begin {1}
			for( int i = 0; i < DATES.getMonths().length; i++ )
				if( DATES.getMonths()[i].length() > 0 )
					ivjJComboBoxMonths.addItem(DATES.getMonths()[i] );
				
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxMonths;
}
/**
 * Return the JLabelDay property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDay() {
	if (ivjJLabelDay == null) {
		try {
			ivjJLabelDay = new javax.swing.JLabel();
			ivjJLabelDay.setName("JLabelDay");
			ivjJLabelDay.setPreferredSize(new java.awt.Dimension(65, 14));
			ivjJLabelDay.setText("Day: ");
			ivjJLabelDay.setMaximumSize(new java.awt.Dimension(65, 14));
			ivjJLabelDay.setMinimumSize(new java.awt.Dimension(65, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDay;
}
/**
 * Return the JLabelMonth property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMonth() {
	if (ivjJLabelMonth == null) {
		try {
			ivjJLabelMonth = new javax.swing.JLabel();
			ivjJLabelMonth.setName("JLabelMonth");
			ivjJLabelMonth.setPreferredSize(new java.awt.Dimension(65, 14));
			ivjJLabelMonth.setText("Month: ");
			ivjJLabelMonth.setMaximumSize(new java.awt.Dimension(65, 14));
			ivjJLabelMonth.setMinimumSize(new java.awt.Dimension(65, 14));
			// user code begin {1}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMonth;
}
/**
 * Return the JLabelSeasonID property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeasonID() {
	if (ivjJLabelSeasonID == null) {
		try {
			ivjJLabelSeasonID = new javax.swing.JLabel();
			ivjJLabelSeasonID.setName("JLabelSeasonID");
			ivjJLabelSeasonID.setText("Season ID: ");
			ivjJLabelSeasonID.setMaximumSize(new java.awt.Dimension(108, 17));
			ivjJLabelSeasonID.setPreferredSize(new java.awt.Dimension(108, 17));
			ivjJLabelSeasonID.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSeasonID.setMinimumSize(new java.awt.Dimension(108, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeasonID;
}
/**
 * Return the JLabelSeasonName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeasonName() {
	if (ivjJLabelSeasonName == null) {
		try {
			ivjJLabelSeasonName = new javax.swing.JLabel();
			ivjJLabelSeasonName.setName("JLabelSeasonName");
			ivjJLabelSeasonName.setText("Season Name: ");
			ivjJLabelSeasonName.setMaximumSize(new java.awt.Dimension(108, 22));
			ivjJLabelSeasonName.setPreferredSize(new java.awt.Dimension(108, 22));
			ivjJLabelSeasonName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelSeasonName.setMinimumSize(new java.awt.Dimension(108, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeasonName;
}
/**
 * Return the JPanelDate property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelDate() {
	if (ivjJPanelDate == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Season Start Date");
			ivjJPanelDate = new javax.swing.JPanel();
			ivjJPanelDate.setName("JPanelDate");
			ivjJPanelDate.setBorder(ivjLocalBorder);
			ivjJPanelDate.setLayout(new java.awt.GridBagLayout());
			ivjJPanelDate.setMaximumSize(new java.awt.Dimension(345, 140));
			ivjJPanelDate.setPreferredSize(new java.awt.Dimension(345, 140));
			ivjJPanelDate.setMinimumSize(new java.awt.Dimension(345, 140));

			java.awt.GridBagConstraints constraintsJLabelMonth = new java.awt.GridBagConstraints();
			constraintsJLabelMonth.gridx = 1; constraintsJLabelMonth.gridy = 1;
			constraintsJLabelMonth.insets = new java.awt.Insets(37, 46, 15, 9);
			getJPanelDate().add(getJLabelMonth(), constraintsJLabelMonth);

			java.awt.GridBagConstraints constraintsJLabelDay = new java.awt.GridBagConstraints();
			constraintsJLabelDay.gridx = 1; constraintsJLabelDay.gridy = 2;
			constraintsJLabelDay.insets = new java.awt.Insets(15, 46, 45, 9);
			getJPanelDate().add(getJLabelDay(), constraintsJLabelDay);

			java.awt.GridBagConstraints constraintsJComboBoxMonths = new java.awt.GridBagConstraints();
			constraintsJComboBoxMonths.gridx = 2; constraintsJComboBoxMonths.gridy = 1;
			constraintsJComboBoxMonths.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxMonths.weightx = 1.0;
			constraintsJComboBoxMonths.insets = new java.awt.Insets(32, 10, 11, 85);
			getJPanelDate().add(getJComboBoxMonths(), constraintsJComboBoxMonths);

			java.awt.GridBagConstraints constraintsJComboBoxDays = new java.awt.GridBagConstraints();
			constraintsJComboBoxDays.gridx = 2; constraintsJComboBoxDays.gridy = 2;
			constraintsJComboBoxDays.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxDays.weightx = 1.0;
			constraintsJComboBoxDays.insets = new java.awt.Insets(12, 10, 39, 85);
			getJPanelDate().add(getJComboBoxDays(), constraintsJComboBoxDays);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDate;
}
/**
 * Return the JTextFieldSeasonName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSeasonName() {
	if (ivjJTextFieldSeasonName == null) {
		try {
			ivjJTextFieldSeasonName = new javax.swing.JTextField();
			ivjJTextFieldSeasonName.setName("JTextFieldSeasonName");
			ivjJTextFieldSeasonName.setPreferredSize(new java.awt.Dimension(140, 23));
			ivjJTextFieldSeasonName.setMinimumSize(new java.awt.Dimension(140, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSeasonName;
}
/**
 * Return the SeasonIDField property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSeasonIDField() {
	if (ivjSeasonIDField == null) {
		try {
			ivjSeasonIDField = new javax.swing.JLabel();
			ivjSeasonIDField.setName("SeasonIDField");
			ivjSeasonIDField.setFont(new java.awt.Font("Arial", 1, 12));
			ivjSeasonIDField.setText("new");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSeasonIDField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	
	Season legendsOfTheFall = (Season)o;
	
	if(legendsOfTheFall == null)
		legendsOfTheFall = new Season();
		
	legendsOfTheFall.setSeasonName(getJTextFieldSeasonName().getText());
	
	for( int i = 0; i < DATES.getMonths().length; i++ )
		if( getJComboBoxMonths().getSelectedItem().toString().compareTo(DATES.getMonths()[i]) == 0 )
		{
			legendsOfTheFall.setSeasonMonth(new Integer(i+1));
			break;
		}

	legendsOfTheFall.setSeasonDay(new Integer(getJComboBoxDays().getSelectedItem().toString()));
	
	return legendsOfTheFall;
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
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldSeasonName().addCaretListener(ivjEventHandler);
	getJComboBoxMonths().addActionListener(ivjEventHandler);
	getJComboBoxDays().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Season");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsJTextFieldSeasonName = new java.awt.GridBagConstraints();
		constraintsJTextFieldSeasonName.gridx = 2; constraintsJTextFieldSeasonName.gridy = 1;
		constraintsJTextFieldSeasonName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldSeasonName.weightx = 1.0;
		constraintsJTextFieldSeasonName.insets = new java.awt.Insets(26, 4, 6, 76);
		add(getJTextFieldSeasonName(), constraintsJTextFieldSeasonName);

		java.awt.GridBagConstraints constraintsJLabelSeasonName = new java.awt.GridBagConstraints();
		constraintsJLabelSeasonName.gridx = 1; constraintsJLabelSeasonName.gridy = 1;
		constraintsJLabelSeasonName.insets = new java.awt.Insets(26, 18, 7, 4);
		add(getJLabelSeasonName(), constraintsJLabelSeasonName);

		java.awt.GridBagConstraints constraintsJLabelSeasonID = new java.awt.GridBagConstraints();
		constraintsJLabelSeasonID.gridx = 1; constraintsJLabelSeasonID.gridy = 2;
		constraintsJLabelSeasonID.insets = new java.awt.Insets(7, 18, 17, 4);
		add(getJLabelSeasonID(), constraintsJLabelSeasonID);

		java.awt.GridBagConstraints constraintsSeasonIDField = new java.awt.GridBagConstraints();
		constraintsSeasonIDField.gridx = 2; constraintsSeasonIDField.gridy = 2;
		constraintsSeasonIDField.ipadx = 116;
		constraintsSeasonIDField.insets = new java.awt.Insets(7, 4, 20, 76);
		add(getSeasonIDField(), constraintsSeasonIDField);

		java.awt.GridBagConstraints constraintsJPanelDate = new java.awt.GridBagConstraints();
		constraintsJPanelDate.gridx = 1; constraintsJPanelDate.gridy = 3;
		constraintsJPanelDate.gridwidth = 2;
		constraintsJPanelDate.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDate.weightx = 1.0;
		constraintsJPanelDate.weighty = 1.0;
		constraintsJPanelDate.insets = new java.awt.Insets(18, 2, 106, 3);
		add(getJPanelDate(), constraintsJPanelDate);
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SeasonBasePanel aSeason;
		aSeason = new SeasonBasePanel();
		frame.setContentPane(aSeason);
		frame.setSize(aSeason.getSize());
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

	Season legendsOfTheFall = (Season)o;
	
	if(legendsOfTheFall == null)
		legendsOfTheFall = new Season();
	else
	{
		getSeasonIDField().setText("#" + legendsOfTheFall.getSeasonID().toString());
	
		getJTextFieldSeasonName().setText(legendsOfTheFall.getSeasonName());
	
		int month = legendsOfTheFall.getSeasonMonth().intValue();
	
		for( int i = 0; i < DATES.getMonths().length; i++ )
			if( i+1 == month)
				getJComboBoxMonths().setSelectedItem(DATES.getMonths()[i]);
			
		getJComboBoxDays().setSelectedItem(legendsOfTheFall.getSeasonDay().toString());
	}
}
}

package com.cannontech.dbeditor.wizard.point;

/**
 * This type was created in VisualAge.
 */

public class PointPhysicalSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ItemListener {
	private javax.swing.JLabel ivjPointOffsetLabel = null;
	private javax.swing.JCheckBox ivjPhysicalPointOffsetCheckBox = null;
	private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
	private java.util.Vector usedPointOffsetsVector = null;
	private javax.swing.JLabel ivjUsedPointOffsetLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointPhysicalSettingsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (PhysicalPointOffsetCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointPhysicalSettingsPanel.physicalPointOffsetCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.physicalPointOffsetCheckBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (PointOffsetSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> PointPhysicalSettingsPanel.pointOffsetSpinner_ValueChanged(Lcom.klg.jclass.util.value.JCValueEvent;)V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(com.klg.jclass.util.value.JCValueEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.pointOffsetSpinner_ValueChanged(arg1);
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
	D0CB838494G88G88GF8F891A9GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8BF0D3571524E1B72C57CDC8CA9AF6C33324F13A34F5266C2E1B218D5375F4C9C3F6C8CA26B4CB9A1A8DA95B5046C9BDD3B2E136F4F2E59BEC4C4F81A7A1FC85B8468EC67E0311E5DB3664EFECE3ECE1CC7CFD161E2D07E5C9BCBDE319F5BAFB4E3D6FBEBD4B1279EC161963776E391F7339671CFB4EFD02E49FDF3A5D1012CD08E1BBD17F1DB790322E02100918DF58B8EE6B1118CC227BF7826083
	621DCA03E74651184CAD0E184CAC52644E007691E8F7DC0C497C985E8F10B6F7DE893CC421C320EFE2771A25F353791BF31CCEBAED5B5BF643F3AF0091F0F60005C711FEDF5BC1956F0676DE759D4110C2C853B19C13B3F4D007DF270EBB82FDC5GE547F14CE45B9135FFA1601681A483249DD74742F3A7656E7A458AF55C4A0C07497C12B78CFB1016EEFD8E536A5C256A53C8E228A2D2C9DAEEA5BCAD9DE32355CE5F384929C8A65730A9A0AA93FE536804E4727BA42FE2B6F7602343BD95101C0227CBD49449
	BB9A68903C2207E499116716218C3A9EA16B0146920A7B69906A49816F9E0079C64E77BBED96152F21373FCD2C2F3D95156F570D0C5F3493A13E7DAF3C121AF08257DFCA7781675921BD8D40E4CAA8344C10C970FAFDDEC5F43ACD1352CDC1F65DDAA8A65366B6AE535693DC26237DC83368E4CCE6934050C90E5FC471C1E8AF84D8F6124B7AC68D1755637E8EC9ABCAB66C0BA2EB62C9A6636613A1D90D6D07B3BF21B677E1855ACD4EA9208B5087B08FE0A9C0A240E1EA6BEB4FFD864F5AE145669B9F77F9EB7D
	7EBA0FCBEE7339C48F3C1B4D502362BAC527CFF691426C7962D0C184FEB82CBFB553169118ECD788373F45A90F1245477FC4EDAADCB65748DAC3123A86498949D61603FABBA7205E0C700E73F0BE4357D2FCD007E7FB3E1A5A9436E321AFB9076BF9D19E67E577C38F1215232F9A52A37062FF6EBD0363D34603675390AFCC47FF3800BADE8C788400B5G1BG52F2E3B2F3F2390E8F1DAF3D150E1B4156E4AFD8544430DF10153A89496392E5D5575FE91BCB5711004DCB3863F9CCAF4BCA75FBF470197F0C2817
	05AB5FB2D8F319BEFAF2C3FAE1345F0ECABB18BB5BB676EAFB78A0750958DC3607299A7ED10ADF2243333DBDC571580E073E2C53908F69781E8144AB2743ED44C9718BCE075B486F7B390DAC01BE1297237A5057ABB12E3A20BD8FE0B140F20035GC9GA41F6F31F83E70B67D28C3373FEC6E310A0F60A98695D9E8768694416B943B85D99206BDE20028E3CED720DE07E74CAB207E6D849A0D22D71485C572F9E1F5E1DC84E38A30B165959F6098D9243D8EE9DC848263FE42572D2AB800F6AD84144BFE172008
	21912C3F3E82ED6206508DAB4282705E110F7A9A21315B886F16FC2E2B9FDB9037885A711ADE92AB764373F3B0EE51E3B65BA5A732A148D35CC6AD824EC9047173BF53E2FD8F373F2D7F75A8597C740608760750FA0659126BCC387DB552F5E3819FFF061F41AF8E75287310DEA49BC49E6E44DFE67B5C1B9A0D54A863365F657A88CC4FF955E82BEBC02E4DG9985DC57FF2AB0B29B2C9382A2373A0BB2980C688AE91E0D5DFA814F94710668D56C22DF743A60A5206E797B97F6066CF14427594AF6B362B1BE59
	40E0829AFE1779830607B574564D7B8F50FEC1D1E4E9F8C291792929759FB6A3AF019921AF204A7AD98D0D75A0E385C013A6EB1C79004E2EDAFD3E31893F83BDC6354F34F65C7FBE982F94707DFF249FF33245DA7BF15A8E756777733D6300E36FF62FA25BC75DAC9A516C646EE98C8C206CBCCEB21CC4F109A04FE6CD5EE44A0B61AB66E1A6AFBB87FA54757FAFF69F8D18ADBCD2995D169F2B0CEC4BF11511ED79FB15F35B6ABFD7C632D5567723CA1451B76955597ECFAAB17EB9FDDE2FC89DD01BC2D85E790E
	8365103C12F2C5F20D0ACA17F4D3A42462AC5A7017B446E641FB4ED99E9BA7CF7158686A7EA66978F2CD447CE979876F9A264FB25BDDF8B6949B99CF7FEAC11F92BD62B8A84A661B70AA8C7FAC0D5FAABE10CAF1FF9CD801F373F84ACB735F0BA3A3106D5B5C22F32C4E97AC1094F15C04FF3A94B8C1ECEE41BBAA3A722E89B78413B029186846181A213F9E5F720DE74FD69B236751857E6824861B432934C6A3D3195F1DDF44E438D4728C13A134E017DF029D118BEE881E8951C47F7295B17D9F730C1A2EB951
	B0CC930A64E183CCAD36EEFC5E269433088DF66B49345E9E9D9423D80D057DD65C3765301F2BGB61C653130368A7BF9ACD4833E4D6771B01303BE2B896DE9CA4856594816332C8E5D2B75BF9931FF010953B1D5E7216D0B57A7C4C8D1B058FEAECE71BC23FAFFD89F15C14BDF2A2AF74153230B538CDFD49359964F55E0EC9E5356B7576CA0180730B5196FAC97F0CD3FAC0D433106870FDAAF9FEE57574BCB00FF2C2D5741F3330957FB0F080760B9C709CA5694523CF3005755845A8781AA8A59FACAA1539D9B
	B7B048479DAC023A9C2023080DDBDC4446A5976138C5DA4DDB28613ECFF1CFC140FCBE0D6760EDD97ABAFD7A2537A27AB9C2D9B1DB3F29B83CB6F9289657F583BED84C77B021C6FFEE568682BE27C4D3C3F5BF1CD6FD8CE0B83795AFB8C6A4B9GF50ACB8C72BD75DA11D74C9DD5343AAF5FBFD3C5E6674275AEC97149BCAD31CDD273F678EC103EB601D958349059AD0913046B616FCB74F57F123E978D3B2268A1D615BFDE2707BE2805D9DD3EC160FACFAA66B610BE08B5CA241A9FEFB150A1A1E39594B8B87C
	930A2E467FAB1ADB607916D342EB7947E8CE5687ED85A0960055952B86F8DD9187388DG4C570E5A743E16DD4A72948BBCD94CBAB30876F10711D5553F343F997D622B8A235ADE108E2B4AEF3C7DDBA4785946087792C9522F8C9D25EC8F0225213D18AF9D49EC9ABF226A653EC12E17D5B0E6DBA9574B9B03FA3D1415B13D384B38DE5E99B473B8F0D7E11B5B6856C3AD28232FACC8EB779BA5B37C764FBBD322796DF419FABE17057B6D8D9B5AD7BC60934B385F7E4E7A014EEFBB41D300C30822DA8ED41B4A62
	255AFE34F6BB579361F5433F58D1376BE04DE4000C7218CCA300351C53B8D9F74BFB0A7A609C77949B05BB39EF98AA67F9E8CCBB1F2714B3DDACA88F655DCB4AE767602C16DFCD4F0E65652C165F2961FFC971EBF4F8E6F3F154C7313D997ADA4FF17A7FBE904E7772F3B3E92E89EB33F96BE74DBB9656CE8E1B47EC2F3A7BB00FBB68F6C1D3E0924E9D3339057B27FABF6A20F6007CE61C67B5764E86BCE72C506E83D0G9682A4G2C8558F21E6F65753AC8B1F866DEC68B40217A0D497975413B534F23336605
	6BA73BA212FE7EF2AE176862324E7FC6A45163C2C52137B6E003283A7AE4086B2A20026B2A3991F1AEE8AB95DCAFAF566D5179910D0D1A8F7D0F8DF38C1FAC0DCADE0E1BC7F1CE1FCC13FA06FB469E3D5E7829BDF23D315A9E395EF851BEF76D7C32BDDABD426A5C2D74DC1B8F72AD85E03847697914G6D359A6E7ECA44ED0276D68DB7D6C53FFDDC0849AC3B205D1154E00E5783ED85E08940CA2D6FD55A3761824B0336DDE0F98E6B7BDEF5F8DD9B8DF06DDB0D61F495F5AC2371EF37CD63F66A59F455DEF8DD
	FA144EFB7E23FDF4BF8281B1C09D8493G356FCD65C3922116EB9746C5156681E2985C290E29D7FC36E20D772BB67405A36619329729ED3B7A6C51752BF7A78D51ED6DD0C3E4DBB3B6200D87FC9332D38C3727538D87353E104763F10FE228B9B91D2F88B2542361734F53792CAF5AFCEBC3B41BE5BADAB5C3C7A51A0E0EE95F94BCE1FA1A876DC5G4B42700CDEFBA3467920566E246DA9426309D83BD763395E6BD21D1E655E7B0651878E43409A0D3F956DA83FDF96C7C499AAA9D61213ADE69C7F92752D1570
	3E5A4CF312A7ABB1C6F804A95F842C9BD709638ED9F88DD2816D8E00A0C0ECA54BE798DF7FB409396C08AC0C03FA3A7C22134C33605C2FD57139C9B01EG4E88E0859832F01ACFCD23AF6AF23BD6E1D874303EBF1CC2BD4CD1312BF2D6CFE1BEF0B9A03AA22F735BAB7BE752D0F3A4DEAF6E336AF3355596A6DB120567F26FF5C56AA7D5BC7E786979168D6DBC8D77A30A33C03BD5439D3373B8552361BE6EC45CB2E82F52F065748CD88B6DA48D17BE05B8D28DB12B1A63DE6B676B95E8387EAB081B0676A200E5
	5578CD771EB62259443D15C6F8FA45C92316A95BA5281170D2BC2F343C2840D8D2D4144FF2400DC41F839AD726C44D81132A55EFDCB5EA5E3371101A2B54D978F74A8A687357F01BA99E4273DF4B9D10913339D116DCF542E8AB35C0F69E46D8789D60329ADE232C20EB3E8E6D2DGD9D6FE27F4B31557D85B721FA031BB36C47C36EAF9668DC31E157159EA8D652C3B359AEBD9952739G7A932D08FB39CAEF83284BFBE100891E2CBF23DA5F0FF183E3ED285FBC239F79FDAFC55F6F19510FBABA234EE7321D69
	6532B94E7DA3E93AF7FD546F469935CC268275D936619019FE72042A77BF0E9CA491F3B66AAFCC7E86AB2F8BDD308643199C0843983E24167B6EFD544683A25E4F08AE96D79E33717A145471F1EBED74FE9A82BC33476E0B58ED2F2336EE372BFCE7554DBECF8DAA242BEB66409804D35C266AD8EE4C6FFE8F90D3A76AC520519CD3C6416BC25B21F4CD9DA1BFE89D0A9E9BC56AFF313076E2CD06E3D3F8F7B0ACB847C6E55F84AE4A461E23FE398A462D85E0B23D5B4FE5CA0EA0D3EA982459E2B29138CC87ED61
	B2A9D7B03EC2F01546A746A9270CF6AC75759C98DFE463BED0D8217759F5C7FFEE60EB057351E7E33C39ED61F51B71922E3B8470ABECBC5F7CD99B5AE337F0ED76AD88A0BBE41FDF1415A906258525FA96B6D96E6E1BEF77DD4EFB600E6731DA2B208AE34AFA1D6E993E1C62B76970AC2E7DC40BEB4950673073DC7DD2A77A578AE82F83C882C82B871B853056F3DD6EEBAFA23728E99CBEFFABA4CB9EDDAA4D64FB2A6A6E3E0527BBF87CD8740307C85073732877CF0EFAE68F0A7ACCFC619045B379B6F521FCF1
	5017883089E09BC0D6831CFD8DDC3EAB348E464A8C4C21D6D184271B5E42F16170E2AC04460401497639D5AF1B2501516FD01F2E06593E446615BAA26553C1F5FC2C7ADC9AB6BF815A2B43F09B201D9C06E3BE78C39643648911454391FA16E3CCCDEB0C49BCCE47C06CA521FFAC664C3FCC34BDB89260FB105478F7E47E2FFFEF60BE9F4E3B25D1653DB1641B4A32D7D2591ACF60D5164A5736A05622D14E7372F2B84ECB72B9AF2DBD21F9AF7763F9234D3179BCBE59B4691694D53E5C81AE5F2AB9654B399AFA
	5FF6B5343E790B5B610B5954F73F40F94CF6640ECFC914B883165382EBDD9C3E86670DC3985566FCFA89F5246569AC7C705F96DCC2B9713B14A4F8241B740A388B86FAC7196D14D4914D0EAAF56F2BAC37DB235D8DB09A3F7EFF2541647F22B3FA5DA5F4C62E3BC6BBA35778631DF3FFD31C680C7ECDF13AD37FCD116D7B533D38B75B403E8EB645E4DAGBAGDCCDBC0EE4F61DA4B40EC408A1752ED12C7B70D7A68A277FF6D7F4D937F45D7A37A9FF6E22E73CCFD1FC6338BE4727BAF03F941F1FA299AE4301BA
	1225D13742062A783DB4CF7208A3944DF272E11ACF4D833996B671BC5D1C0E38B8E8A7E838FFC86779FC22063BCC574BEA065A291963FECCF1855036EB389F50FB119EE8CFEB38D41ABBAF04F63C061BB4536F6750DE2B61B676F23A1BB41CF09DF1E5ADD03F34703B11B13BBEBE8F81DEE9197B8E046DF3B64D07964058B80055G2F83EC81587DB94885D0865084B08490840885D88A10G308AE0BDC052675CAE1E3F1C9FADC6GD2ADA228F148823B97E37C1F19F14FC9DAE3B233DB6766DF7D765D9B5D2E1E
	6D3D35DD3DC875AB02BDB8267C6A5D42AFFA699D9DADC77938753DD99A2EB950618BC8982DD8BC1D326279B36273AAAC77FB005AD49E70DF564AF354CB69B3736CA787D0CC1613BE1D4EF3D2D78419F92E49A188E3907879945B9A1E13FA9CB3E9FDB9C8343E179CB4DF9502E1796A6F285DAE063562B41EFFD6097AF37BE469A648A6F89C30A2171C67589BAB79D934D61BF74892BDFFAF25F63F8D46E6347171771BE76AAF0E7AC19974DB5BF85E3E35176BC8E90B2C236D045F4500BD4318A5EDA19DADEF8B57
	D170FA643CFDA1752D15B0FEF59B77372F51FCE28B341FA37CCE74BE0A5B5D9E13D92A59E2DEFFF4DBAC6C3F35ADD674C70EF1557D33E31C2DBFF20CEB6E5747B8756E2A9F75B28A3E57230D6B52F0D7B5DC8FDD93F1D7A4176256708314AF44B709C81063DD142FC90A6BD1F1128EF715FF436DC7FF82114114785CG7DDD540864713076A49DA3F9413DB4FDCCD39EA53F20637B9E8DF7D55B0B3A813EBF868D57B84077075ECFD0BF7806987283D3BA1EDFB3F22086B0403C13AE48C5FC23A78B0CC585466232
	FCFE777FE6BA37733C7639F22E3111503BEDB4C4AF2E72F8B83DD04ED5DCC16FF6725531DB69D85D9500298B7F72A3B5E0EA2DEFF028B48E3BC3B4BC665B2141669D3D961A372E7774DC3AB08D7BBCAED0C8B1D38853C7FE9F5747D094FDC457513B57C26FAF0C0578F8F670F678E7BEAE646EA6DA6EC84D15610F847704705CDC55B3A6A9DDFF46AC863E635B67BEE358BCD31FFEDEA24CD9F70BF96A991F416D522961B2B238DD1E50F075FD336D3725CFEF3F6C7F0CBCF7631BA0E47C5BE4FBC0A67BE76B2876
	7ED77EA3FE501E1DC2D21A498EB7953E19EC1FC46D847167382BA1828EA864C3FEED4BFEDBE4E4972F8AB9BC47F5AED05C27D1EC26EE7A7A0DD2F36AEF1EC8CDA1E9C00E052DE612063187284A0CFAAA04ADCADB7B7FAD789E38CE7EC25FB0A81E0735774E2CF68CCFFEFCEF56832593A9E4C78359B1A2F87CEE8156241F1ADBC90AB3G4C521F93944214C2A7CEA924933B7C02D32C6CA5BBC76451E1BADCA1A93D8D3D3088EDA15D1D2DA4D3F1E3060AC724C2BE8E2D5401B9095AD1C959A66F43BFBF7C23AC27
	2902E4C496C4A1FB749AB5ABE497FBB36285B80839E70603BD7BF879EF573C569DC8A5BB1A49A7F87EF509B2AD05C497FE92D2FF8F5DC0F6525F9060A9857A8D50477600B26581DDE40FF4626F21820A682A6549BFE867C3A6937E162030C9883895BCE8AADBC91A97FF39CD7633EE0F609DADE495971356D0BBC42521B99870FB0072CBFF11202DF26019FD1F1EE85934A21524B410CC0FCFF0B588CE48711A49CE45AD0B81B7C40B203F46CA974A18C4D561D11B8A4B9D5644C7E1CD18CFE8CC0F853F953BE773
	1E069448F0E4D2C8C762B6183491AE1BDB100114A8B7AB78CBF41F1C23755DF3CC49FD2171633E94E2D005440D599F74ABE8BE6312E78ADED8B823DB7611825C4A02B7B062134701B3E398E73CCBA3E07A4673C58FFF773F3F2084C2D395F2BCE4BD2322EF3C30F6E4C4720A5A8220D7D87E2836BC0EB1050D599FFC704316DF3EBF8CF6E0A72963C5C574EFB17DDB02FF0BA92618E20ACBC03949E44EFF617DC1AEB3B59E4D15F17BCD8D30034A2F5EFE7E5B2FDA1213C03A86554DE09332C2235AA74607C5792F
	639FBE3AEB516BFF0BFB64C7D389623A0702B3FCE33613E3A51990211595E6F1860A4D1EE5BAE908BE889BE81F3101BBD816C11BB9B887DF8E5366D17A57F847FA42D9A759021AF88537A59E3FC7DAD92F3F6335E47FA66AEF6AAAAE2B5FB9AE075F710AB7D17285000F3B4C6B2A9FCE1DA5516FDE3A46850F87B27431DA2F8B6FE16A2694B1D47BDF3B096B87789835D37D8D47CD647B1AEE4E7F82D0CB878804CFCE790A95GG7CB8GGD0CB818294G94G88G88GF8F891A904CFCE790A95GG7CB8GG
	8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG4495GGGG
**end of data**/
}
/**
 * Return the PhysicalPointOffsetCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPhysicalPointOffsetCheckBox() {
	if (ivjPhysicalPointOffsetCheckBox == null) {
		try {
			ivjPhysicalPointOffsetCheckBox = new javax.swing.JCheckBox();
			ivjPhysicalPointOffsetCheckBox.setName("PhysicalPointOffsetCheckBox");
			ivjPhysicalPointOffsetCheckBox.setSelected(true);
			ivjPhysicalPointOffsetCheckBox.setText("Physical Point Offset");
			ivjPhysicalPointOffsetCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPointOffsetCheckBox;
}
/**
 * Return the PointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointOffsetLabel() {
	if (ivjPointOffsetLabel == null) {
		try {
			ivjPointOffsetLabel = new javax.swing.JLabel();
			ivjPointOffsetLabel.setName("PointOffsetLabel");
			ivjPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointOffsetLabel.setText("Point Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetLabel;
}
/**
 * Return the PointOffsetField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPointOffsetSpinner() {
	if (ivjPointOffsetSpinner == null) {
		try {
			ivjPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPointOffsetSpinner.setName("PointOffsetSpinner");
			ivjPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjPointOffsetSpinner.setBackground(java.awt.Color.white);
			ivjPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			ivjPointOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetSpinner;
}
/**
 * Return the InvalidPointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsedPointOffsetLabel() {
	if (ivjUsedPointOffsetLabel == null) {
		try {
			ivjUsedPointOffsetLabel = new javax.swing.JLabel();
			ivjUsedPointOffsetLabel.setName("UsedPointOffsetLabel");
			ivjUsedPointOffsetLabel.setText("Offset Used");
			ivjUsedPointOffsetLabel.setMaximumSize(new java.awt.Dimension(180, 20));
			ivjUsedPointOffsetLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjUsedPointOffsetLabel.setPreferredSize(new java.awt.Dimension(180, 20));
			ivjUsedPointOffsetLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjUsedPointOffsetLabel.setMinimumSize(new java.awt.Dimension(180, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsedPointOffsetLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {

	Integer pointOffset = null;
	
	Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
	if( pointOffsetSpinVal instanceof Long )
		pointOffset = new Integer( ((Long)pointOffsetSpinVal).intValue() );
	else if( pointOffsetSpinVal instanceof Integer )
		pointOffset = new Integer( ((Integer)pointOffsetSpinVal).intValue() );
	
	//Assuming either a REAL AnalogPoint or an AccumulatorPoint
	if( val instanceof com.cannontech.database.data.point.AnalogPoint )
	{
		if ( (getUsedPointOffsetLabel().getText()) == "" )
			((com.cannontech.database.data.point.AnalogPoint) val).getPoint().setPointOffset(pointOffset);
		else
			((com.cannontech.database.data.point.AnalogPoint) val).getPoint().setPointOffset(null);

		if( ((com.cannontech.database.data.point.AnalogPoint) val).getPoint().getPseudoFlag().equals( com.cannontech.database.db.point.Point.PSEUDOFLAG_PSEUDO ) )
		{
			//((com.cannontech.database.data.point.AnalogPoint) val).getPoint().setPseudoFlag( new Character('P') );
			((com.cannontech.database.data.point.AnalogPoint) val).getPointAnalog().setTransducerType("Pseudo");
		}
		else
		{
			//((com.cannontech.database.data.point.AnalogPoint) val).getPoint().setPseudoFlag( new Character('R') );
			((com.cannontech.database.data.point.AnalogPoint) val).getPointAnalog().setTransducerType("None");
		}
	}
	else
	if( val instanceof com.cannontech.database.data.point.AccumulatorPoint )
	{
		if ( (getUsedPointOffsetLabel().getText()) == "" )
			((com.cannontech.database.data.point.AccumulatorPoint) val).getPoint().setPointOffset(pointOffset);
		else
			((com.cannontech.database.data.point.AccumulatorPoint) val).getPoint().setPointOffset(null);
		
/*		if (pointOffset.intValue() == 0)
			((com.cannontech.database.data.point.AccumulatorPoint) val).getPoint().setPseudoFlag( new Character('P') );
		else
			((com.cannontech.database.data.point.AccumulatorPoint) val).getPoint().setPseudoFlag( new Character('R') );
*/
	}

	
	return val;	
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPhysicalPointOffsetCheckBox().addItemListener(this);
	getPointOffsetSpinner().addValueListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointPhysicalSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(379, 193);

		java.awt.GridBagConstraints constraintsPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsPointOffsetLabel.gridx = 0; constraintsPointOffsetLabel.gridy = 1;
		constraintsPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPointOffsetLabel(), constraintsPointOffsetLabel);

		java.awt.GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new java.awt.GridBagConstraints();
		constraintsPhysicalPointOffsetCheckBox.gridx = 0; constraintsPhysicalPointOffsetCheckBox.gridy = 0;
		constraintsPhysicalPointOffsetCheckBox.gridwidth = 3;
		constraintsPhysicalPointOffsetCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPhysicalPointOffsetCheckBox.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

		java.awt.GridBagConstraints constraintsUsedPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsUsedPointOffsetLabel.gridx = 0; constraintsUsedPointOffsetLabel.gridy = 2;
		constraintsUsedPointOffsetLabel.gridwidth = 2;
		constraintsUsedPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsedPointOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);

		java.awt.GridBagConstraints constraintsPointOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsPointOffsetSpinner.gridx = 1; constraintsPointOffsetSpinner.gridy = 1;
		constraintsPointOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetSpinner.insets = new java.awt.Insets(5, 8, 5, 0);
		add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPhysicalPointOffsetCheckBox()) 
		connEtoC1(e);
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
		PointPhysicalSettingsPanel aPointPhysicalSettingsPanel;
		aPointPhysicalSettingsPanel = new PointPhysicalSettingsPanel();
		frame.getContentPane().add("Center", aPointPhysicalSettingsPanel);
		frame.setSize(aPointPhysicalSettingsPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void physicalPointOffsetCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if ( getPhysicalPointOffsetCheckBox().isSelected() )
	{
		getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
		getPointOffsetSpinner().setValue(new Integer(1));
		int temp = 2;
		while( getUsedPointOffsetLabel().getText() != "" )
		{
			getPointOffsetSpinner().setValue(new Integer(temp));
			temp++;
		}
		getPointOffsetLabel().setEnabled(true);
	}
	else
	{
		getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(0), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
		getPointOffsetSpinner().setValue(new Integer(0));
		getPointOffsetLabel().setEnabled(false);
	}

	revalidate();
	repaint();
	return;
}
/**
 * Comment
 */
public void pointOffsetSpinner_ValueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	if(usedPointOffsetsVector != null)
	{
		getUsedPointOffsetLabel().setText("");
		if (usedPointOffsetsVector.size() > 0)
		{
			for (int i=0; i<usedPointOffsetsVector.size(); i++)
			{
				Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
				if( pointOffsetSpinVal instanceof Long )
				{
					if( ((Long)pointOffsetSpinVal).longValue() != 0 &&
						(((Long)pointOffsetSpinVal).longValue() == ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointOffset()) )
					{
						getUsedPointOffsetLabel().setText("Used by " + ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointName() );
						break;
					}
				}
				else if( pointOffsetSpinVal instanceof Integer )
				{
					if( ((Integer)pointOffsetSpinVal).intValue() != 0 &&
						(((Integer)pointOffsetSpinVal).intValue() == ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointOffset()) )
					{
						getUsedPointOffsetLabel().setText("Used by " + ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointName() );
						break;
					}
				}
			}
			revalidate();
			repaint();
		}
	}
	return;
}
public void reinitialize(Integer pointDeviceID, int pointType) {

	getUsedPointOffsetLabel().setText("");
	usedPointOffsetsVector = new java.util.Vector();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized(cache)
	{
		java.util.List points = cache.getAllPoints();
		java.util.Collections.sort(points, com.cannontech.database.data.lite.LiteComparators.litePointDeviceIDComparator);
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		for (int i=0; i<points.size(); i++)
		{
			litePoint = ((com.cannontech.database.data.lite.LitePoint)points.get(i));
			if( pointDeviceID.intValue() == litePoint.getPaobjectID() && pointType == litePoint.getPointType() )
			{
				usedPointOffsetsVector.addElement(litePoint);
			}
			else if( litePoint.getPaobjectID() > pointDeviceID.intValue() )
			{
				break;
			}
		}
	}

	getPointOffsetSpinner().setValue(new Integer(1));
	if (usedPointOffsetsVector.size() > 0)
	{
		for (int i=0; i<usedPointOffsetsVector.size(); i++)
		{
			Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
			if( pointOffsetSpinVal instanceof Long )
			{
				if( ((Long)pointOffsetSpinVal).intValue() == ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointOffset() )
					getPointOffsetSpinner().setValue(new Integer(((Long)pointOffsetSpinVal).intValue() + 1) );
				else
					break;
			}
			else if( pointOffsetSpinVal instanceof Integer )
			{
				if( ((Integer)pointOffsetSpinVal).intValue() == ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointOffset() )
				{
					getPointOffsetSpinner().setValue(new Integer(((Integer) pointOffsetSpinVal).intValue() + 1));
					i = -1;
				}
			}
		}
	}
	revalidate();
	repaint();
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getPointOffsetSpinner()) 
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
}

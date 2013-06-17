package com.cannontech.loadcontrol.popup;
import java.util.Collections;
import java.util.List;

import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.util.SwingUtil;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.loadcontrol.messages.LMCommand;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */
public class GroupMsgPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjPercentLabel = null;
	private javax.swing.JComboBox ivjJComboBoxAltRoute = null;
	private javax.swing.JComboBox ivjJComboBoxPeriodCnt = null;
	private javax.swing.JComboBox ivjJComboBoxPeriodLength = null;
	private javax.swing.JLabel ivjJLabelAltRoute = null;
	private javax.swing.JLabel ivjJLabelCyclePercent = null;
	private javax.swing.JLabel ivjJLabelPeriodCount = null;
	private javax.swing.JLabel ivjJLabelPeriodLength = null;
	private javax.swing.JTextField ivjJTextFieldCyclePercent = null;

	
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public GroupMsgPanel() {
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
	D0CB838494G88G88G10F81DAEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDC8DF0944715166BF0A2DFC88EBB58D1ECAE05ED92931B78648AC9C8C24E64C22E7C4359380E585C9DF6698EAAA1D544664EA4273AF06716D682E1A431003005C1526A974908552FEDACFE241596A1A18984925202E550C8BB1296C63B2B5991DA018C773AFB3AE7F6B43B8B4438CC5553CCFF2F3B5FFB5D2F3BDFF74F0232726626A6A5E7A114140A54FF9FA5A13431952141394F9EE7D8DAC3228569
	7E6583EDC727DCA9704C86DE79BD0996ABF2B426C13A9C526DF7A5DA5E05771D2839B14B81AFE1642CEBC068611355EFC516F3B5174959CC527FD9148E4FCC20CA403A0166B6E079DB0A33D5BC8869CC759DD3D2B2C21D8E1CC72A4F5661EB54FCC540BB88340A64A9AA5E2B7263A00D734443F329C32BF38B516E6E3A03EA3A6F5E79E843F39FA6E7E0D93A7A9995B55232AD6A53067E1EB404854579EB61F93873727051C15F28FDD070FAFDDEC59C9C31CBBE41BD886F32CF327BFD7EF1FFDD5DF159B76E6F88
	8CF78ADED1DA5408F53D2FBE0D5403D08C241DCD98FB3B81374FC6C8E7C13A9C086A7BD246E1D51F5917672304C74B426A9B5BC475DC502469BBCBDA1D5A674475F794272B6556813F88B02A4B7D45CC1739CE264BF70F623CABA13D9EA86D98536548BE260B6B5ACFD02C6D77495B4268E2BB26365DB1CD17140B2BD3F793DF58E4437DB983F873019683A5G2166C40B8DE88F7141DF663E8FCF470052649B9D75F99DFEFF236416BBFCEED102773ABA60285899F150A73B9122FE762D7AF293FD989DEC8E75B1
	4DAFFA91730B85E71EC44197FF1F1CEAE25B425F9CCCEED76B68EF5635F3B3EE376B248FE360BDC9EDD30A4792FC058E27FDE2A5B8CE2F81DE4BF1564E2ECC26CBFB4AA348DA75C972E693DD627E21B3F941F12A4352631AAE340D9372F19B2F87BC3D857A9B68B0D09F50D48BEB632DF9B5515A38957CDA768AD2577800DF101546F10F6496E5352D3B0AA335359951F242AD17236DB23FC65FC72BFEF85E34DDD64D2FCF0EEB216D915F22358B157DE0D8594BDB267BC6A66F435D75382F5634987B3610609BDA
	0CFD7B44D15637A92D091691A05AAF578B71BC358C52EB015009C4CBB650C120EE2011932CEFFA72AAEE527FBBF57DC24BEE333D83CFB12848C22BB720885EC131C710BD4200A486101AA75B065BE3A024DC8E991703A063386895E5C171783CD03BB0AA029384E81E925BF61CC79689375BB3AA0200D1BFE2755AEDBB61A98981652C5FADA822160372BF34613E3CA274C00DD0813CAFBA015B6BC9B2A746427B3C932C2D1EA84258C6C8E73531F6F94A36831EED601422D4D767748CE2B584F91279560C863C6E
	14363131743A0B790CB36387287CB767CCFD0678D41BBA165A0CBE738DD2673AB636163D53603AC57FB52352B6F3FF23B2AABE979916B0B8ED5F2A926C23696D0916D22016F656466BECB66AFB0DC2C0E45EF6CA86C7915DDA0B533C3F4F47EB00F8C574AACE51AFFA5D7092D07BFAFD7E964D8F07FC5CC77EC4F0BC1FB4012384B83EB1FFBB45218E3DD7B3FECA81668B0AA2FB86469531CBD4940FF7D8AB1FDE00F58904CCD58155561AFD5856FE30B188944BED3D3FE0274E1F5AFD3E4B637EEEBCD2D43FFC0A
	2C0FF3A07F42F6567FF975589FD672F499C9EB7C270E7EA5FD076B0DBE07BE7797498867970CE85B4496066F5B3FA9B46F5B7B8B4D7B76214248FD77E821D95FD15E6305F8AD76CDF8F53E70F705F89E00D84BAB920764C5900D51DE6F46F6F83C9E2557639E9615AE4FD591213D9D38CFFFCD661A8D504EE99DEC1E18A8E473C451556F23A597EBCD577F386FF6A577F7507ED1BA34F9026A94CFE2DCD192C721211AFC63DE0562BFA673180A87AC846B293A024B16F60A7210CF9E45F3F47708AC86C6FC123B9B
	1A3559A3CA6E72C1988B0A1D7C2553E969A5610A903487A620716C24E76CCD18FB8C3F16598E9CB8EA8B89B84B07BC32586A750F3372E5BDE59F3071BFD40A751D8DF64C87DA524146C4F571EE5D18D087FE13CF12E8D3836F60C95C0ECFB7E4697D36034675C5A7D9BD9F15D8F11F0AE363A2180787D51BB8292EA3C7CBF698F8BD02B42E2DCF4DA5DB61A96946A345BF5EE75E46757B70983C4C6BEF5A378961F50656C9FDA2DE40F57AE54FA8CCDFF802D177974F946B7797B120BF5E0BAC01678AA0DAF65147
	B86E8EE9DBC82FA36D300A4773959CFB13E0EF0292FBD2D97C52E1556FBDECE90A454C47B0D51E223E65BCE50C6B9EAA43753A81D7CE31F67E45BE7D9C668884FC039E32BC2BEDA6156A7D0FE2E325EF43933CA3GB11EDB8C32F61FA8453A6275DB055536FBE27F91B4BD9EB96676A8BE19AD91CD93448D3BFDCD90C234C057B57161596391A720BC5DF37C290135D76CA9B67EBEEB566FF396EFF3A4EF8D53B6CB54B6C950354D1C8B2BD3291E5F2E43B12D595EEC10A8EE9F9E775847958F8E9E84C5A0C30376
	A6552D3AA4CD3559564976C349E55867DC10768345F6B21DFB2B757D39A42141F44E40B437136A3A3053581F8776E3F94B81DF53497A733FCA376B7A9376B790428D4388E0FDB4374CC2FDDDCD4FABCBE53E0E587A706706DB093193BBD94CF5299C0F6974539093GB5G3900022719EE7B5F0F3AE7B8960C30E778284428D75CE9ED3DDBF0FA7A5AC7636C6F10BED8F21A4659BBB99ECF7075BA1C465FE98447E95B9958E31EE1765DF3G37372B8BE26DAEE6535D6FE7685A3B536771AA3C2D2B7763F944A7FB
	06BDDE06FDC4B058F51285094689BB9C385EA054B91B57AB1707DFBFA74B4D574F9B65666B675D6511E3232F1707DBDF29FEA709DF4F875D9683D1ECEC9F4692A01D5845E217F9E5C5705C5B8DFD8F64828A824581AD82DA81148814FD16657FB149FF9052AE20A0D09C50A22015C009C059BD303F69E179B34BE65D02DFC6A32C9B71DFD5C7172ADB34F2588EE6A3360B6A7641672A5B9675097BE52BBAAEF75EC4B96CB3F8FC63B11D417B779B8730FF87F97ABE121EC4ECFE969DA4A691E408F50EF95D2A9353
	35280C78E69BD4FAD92DFBA792CB308FCB4224EF9C72AD68417926CA746B208B30982059BD34BD291C27A630ADC330A984776A720B03A836171CF16CE3E5B7C07EB440CA01DA00C600AC2A6DF8DFFA0207B7CD1303128881502008F5E5F97038D541F6E1C014980E63059384B1AF371D7301657139C9F000B47E8E833FDD748EABA39ABF13739D12F2861AC554F83BB08FE63C81DF23AF98DABC347E50BCCD5374E3FCCD049ACB3DD1260F251677527DC6A2BCA97F43DEBDBF5DC57915AE46C7F6B33E1373D73CE7
	46F7F1FE0FA9DF62FCB1470C9F43793BC27833D57EE24E77561B7117F37E45BCB37E9A970B3F2A7A713989D957F188EC97A694FC366AF1B78A436D440B697A7D8819677058C8F43175FBB95967D61E83FF846ABF47566FE0890BB996FC6F91549EF35E74ECB5215B119CBCC747401CF35A7A1DFDF1F5AA1519DE4AE42E843E75BC466ACA7576E09FF885488EC479D2889F7BC6D9887FBBA1FC2C6F35E4BDFFF988BFD5D71E5A76FCB133ED637371C87AD1CB58F3E357F9EA53147ADC2B48C96D65EB54F82A38B424
	5D552DC7DD1DB60AE99B143C0F5BG4F8D8B4F33BEFBF3A85B3CAC99E5345CE8AFAB177D89BB137E484E30A90EED5D4330840EFD7DBD06B97B98B6A707E1737B189E43529EF3BD58E8D4E33FF6D2964F53691F327A3EDB4FB0BF479E48E3580ACF595A71583438A69A517845C976B387AF5078A509639D84F76AF09A573CDC424E71BB013770820BEB1C95382D33AFA6DAEA015A015CC0B30066DEE471483B76CA94A5F66B76795BA1E011F4A18635EFFB696D1D597EF2B3734305BFFB9CAD7ACADA5838FE61C56A
	FFAB54E7CCC0CE42CFEA5FAB15583E147EC40B8D4889548764870A6DE776DD3417D37B70364421A84260885971B06370A6C4037162C5ED5B7F3E71ACE5CEFF7898A2DE47DBEA483792526B8C9815A1771A1D57A001664D92424BABB5708EC33AD3B013F158EEA6432D668DC610B153401B8B690526B22CFB4CE4BC2366DD95C146FA83AFE5GE23581B3992E9CB39965EA5E060170B23A8D3C91C8CF194AF858D446ACB56F3C88B29699F84BA01DE0AA6374B4999BA0DF5AE0747854C8FBCD4AD0990F579BE5D4C2
	DE67ED48F0051571D01ED10684F9E3EEC3466C30B2CE6CC37C3DCB773E2A6473B8878FC7D4469F6F288CBAB7F5D4045F8BF6D7186F854FD7186F857BABA21F250ED404BFCB75D5684FD2695C5AD80DE3097950B7CB015682ED84323A593C7A3F9585084C2BA6F36AB177305864A337CC8A131FD0995E565FD5C63F1BDADF094F2686FC0A629B457573FB03CA5CDF0A4FCFC00A7D39923791EC4FC7941AD545132A709AAD09C384267B5D8E92231403DDCEB75B83F754E2EC8452D39CFB00E033A0BD17E32F5959DE
	F9A147FEE945D80A88FBC911E1BFA571CCA5249BB8566642D8B724A50ED565B399B1DA3EDDCC46EC0E9537B199EBB8F6414EE4A4F26C8DBB1311BE44306C43CC060DE30B0FB099359C9BB94CE44C60D8759EA6A30EE33F5A43E4AC6058A5AEE389476EBB44E42C6058CF8EB199C5438C3B1A43E49C645856DCA6231DE3AF9FE1B27AB9766AD1A6C361585DC7190C259C7B95173112E3E7390CF59CFB114BC0A38C7BA1171145312FB5B099EE0E85080CA024E7F26C27C4C6BC2417F26C2DBCD65FDA0EAD2BE7E58F
	FA981661E0E57BB8F63211150D6158AF72D859659C7BBF9BAB3B01E375852CEC76A5B626CFD4079F23272B230F51735564CC9946D67724DFD4632FEAF2D6B6B4949079DC602E36F22CB550698BF870F1B05D933B5F473160880CDD890FDF181168B9440DEAF616B0827A46DC22EB46B3704467CCEA59DABC978C7A2471D1EFG2D3F044BDD2BE5F1F942A5166F873578DEE04867D5E81EDF11BC3517E1FC824D3F0C31BF5A1D9166EF3A0750C801B231C005AAEF8E3C77A29A53D219AB2D582ED10F57B3BABECAAE
	1E286C75E43ED88F79B7DEE67AAD6F457AA95058B44FEF497CD1AA417814D81E819729CF889A6A3BCA6695A7646B842258F9B22FCCC1BAD6E2768D6C0AE45FE603FD31BA7B30CD0BA4ED8FC2E51CA4F352D24017F339DFA5734FC6C8270DB2AC3B82E3C5102E6418034C97FD109E99E57A7D7311C87AD92268978775140568F7AF11B18F70F8AE7793B2F72D0274DA0E2DA3D80A975E3D8C7BA6A9FB9852ADDE265F778FC553EFF3847D02D0CFCD08FE89E4AE1C8178ACAE77D1B207AC0674B39C2BA5F3489A1C66
	58DBE41E5A6BCB34147A18FE9B0EC653AFB502FEAE28E7DF08FECF12F94F8D385F475B0A68B7874B6458F5225FB310DE4131C4229F7243BA64E77ABD97D53FAD9174BB8C756C8C51EFA11903DBG6F7633B17257B9F86E9149FD94BB4FFCB8074DA3B110AF4E2F5E9B79693934BA7E088DB21EGE5D12665FE174B4AA5C2DE6B98AD57B7C64BD11B5E31C51BB39811B9E3ACF44E0805F4C035094A7CA37F46F17618266B02B1FAFECE73FC68E0F9160EE9FA2DD273D03D4446E8FAED5669354120D71AEC546B37F9
	CCE6162C69D5AE53B3FD1AE7DFBE4B6314F56D256621FA7594C453EB0B3E3DE4C3FB69742267A00FD5336F2BE683EF194C4EFADE987B98CDBF6B317BA5E148BEA084BC03F5F50D78010F2F4E36961177965D8A84B610B34387EBF07DA981086D00EA834CDF6E2B45BA54A0F63EB1F7C2FF87F8706D6630F7006D81753BC175597DBDB9A9AE003F1DFD83DF2D2AEB47CB76E2F8FA45899BBB6634B74362D5DA90903C0100A8FB068A4AED1565362ADABC6765A1FA36CC469C79373EFEBA86F3A41133239EF13D7FBD
	1D1D4B6CFD62D164FCA5AD190DB7235E2BD4FD4B956D6CD379761AD4DA677C86CD775D3DB9C65D1D9E58AD608F3DCAEB4BED55B5E5DC17558E2D5CF197FE969497B0BD1BB4DEC9FEEE78BA2B2A21A1BEAEE3F57E7018D62EFED701F9BB3ABD02649B56B5A2AD7BFA3356CE5BDB34FAD69F38F979352DDA3935F69C7794102B8A1BFAD7D1D7C073FD6B0416AF63F0047ABF8E2DBF3BC3AB97FF64566CBBF4D2336F42A92D1E1F3CF77372B33B34F20B77607831005EF35848DDCB9D6B3F2AEEAD5F45C8769D7CA824
	7E2F77E8653EF268566C0B6F556C7B1DCB2B6763C8769964971C574A2564DAF576514B9E56FF0FFE2265FB7EC824762B8929FF46855DB8BFFAEB76BDFDD1336FBF7A35FADAF2EEDE7E1FD234F29D39783E2A40A1113BFAC534116BA868C2B56F6ECDDA5E25C772A20C01CAD588ADB71E2615FB6C96ED0C5B225978733735FA92775E0CFC3AAEBC1B435685B74CD7F39436AEB8F38E21F06B42282888D2DDDD87FE30AB817AE5CB515956B3E240B78E43D7DD97B66512EF39215EF5C0A96330C7A3F23765B139FB81
	6B9E675F8D116FE6222FC7CC6E99B59251CBE6EBD2BB11B1936A1E87349848EC7E276D779A5EB228ED12433FB3DF81E5522F30FB317384F7C23A8FC83942EC3960E079E782361467FF08609BA11DB511E8A91F08A4BFA9C3131FE9E375B9214C54842B2F0D608BA13D8CE84D8413FFAA1F655F88D8ED10651FC9F08952B3006684A349AF42CB15FA3EB7D8936D6EE2E1506C7CF6ACEA39DCC31A5E5DE456E2BD17870DF7BA6F92FCD550F827B337065D692C85DE7BA43B53F9A9136C65A13DEC126D5B7F3BFABFBC
	FB04CB533F408230D37679C5D91924A87922CEDD6FDF7CC49F27D89F2CCCE6BA98EDDAB729FED3B1E97CD6690C9547F065D7E1EF84A4814DG0A830A87DA8A3492E89DD04AB5081B00AA011CC0AE209120A920D95718AD3D75E591ECD1AFF7496122AC50EFF2E83F3C18F3FB7D79EA54F2667DF9150C79F9570C7D79D7840F3FE66C4F74DC561F0B01E71BE2F7043B72EECF6F4228654C75DEC2660B99D3C63D1FA5782CA9235EE772185EF30007BEE3FA97C67D164CDC6F2A28654C75FE014CCB6E4F0CFA3FC2F0
	7FE7C63D7B9B185ED340CB60FA3B9D3727379C351C395E5B9A49FCFA5D28F7B641BB2F9B75FE5841746E835E126BCC6FAC5B6D69DD90351C395ECF13F9386806D16FE788DEF9432877E99B537BB070665EE0FA1F4D3FBD3D872316B3579B9510BDDA0CD16FAD84374698753EAF1F693D97F8B1C0F83FE2C1CBF662454A6315BCDE31097E7A056B1605FF6A273E67685E3FD7F3676F251E3E23B2689C7CF2A6FE67F7C9F449D0F92FE6E6A17A8DBD44EB1E2B641362AE48689D266B41A2FC8D25669D5C06772D26F3
	39E3C0B981214F2901CB622052AD8C73B2C35670E750235668E750D72C6677C4D3566977C4F7D94D6F093EEA555F93D1EC269547EE43307E38F83EFBB9F60EE38F10BAB1566BF113EF41A8BE0768057196918BE478A3C42F890239D46CB19DF6CE4596D8F1708DA60F6033AC75F7CFD6F20E6D11A41A7E9149637186C40537474F080E9E3F20537BE90E1D63FE95132B7971BD3A779D39F75E2777FF81B27EB1CF3369C55D3BEB5ABD7667AFFB6C8B103122C13369D55D7B5067721B314832EFFC81B2F6B5EAB665
	6A5E9FF35CF959CB3EG993F36E9B63D20FB6F355DF9597EAFC04626824D26ED3A77387CBBAF7B29BBAA037E7F883B4F2EGE18B7E00D283B24A88B95B7875439F7CF659EBBD818B5A540AF663EB47AED1A62B2768463FB7D1FF765A0C367871975A78B22F95258448A3B520CCCAE2BB4A9ABA03FF9695D0C437032D4B81856DEA4712AA70EFD7AADA044008025742C36DA8450BFF200BF6D036A4F807AB68A2FDA808FF161B646847BDC054C7206FCE22AF2E454EEB597973ED4579A756AC322064E674B67ECF8C
	1A05C1D83EDB5196057DA8AD682FEFA09525CD30AFACADD05D9EEEBEB6560E0F5539521703FFBBB3E3EDC6F3B2C2C199D5101C7826534EF3381BC68499C409F22B02FFF06C130BB86F2EDC25641F0E3F5B170C12D4A34750F5149C74ABE85310B06A11A69534051F0A87B0F393A090B0F88378A7F5201C4D209CE3F19976EFBEDBF97F777F67BC112195D5D09EEDCDA8DAB1AC7AC6AB9CC3C3909C728A20E9217A9CDEBD4EE3B76459913CF757095F3CB5G2E60C416514ACA72370A7C2D46FF2B88D2C5102AEAE8
	5F75A862BFA3BF48ECA67E837AECA37AE08F30B7C3A7AA7F7A07E79F7937436B5701F54DA8157CBE887A412A65BAB9BEBAA04ADFD9F0FF4E565989DF435D64475E9244753E9D64B5E28BDBC92411F025264E327AD1CBB2CAE9C51BE864857E0B43A770DF92DD35A28BC4DE5475903D3D010AC1C7996D18FEFC6778463F3C326022B3AB99A5C305A3A22DA1F58247D3248EBC62D29414D4CC0C5D0D545F8810941A665E98CD196E2798CE833721670EB83515E44DA0FF2D375C7038548ED2B60B37D79E6D8E2CAF3B
	49449AEE8A55DBC11921D782A16C3EAF1FD9E521E66120B3E51AD9FD8A4A883989886176FE792C2AB1374A922AB7F4D66879FF887B53AF1FD9C7EE462CCF95E4B59E7B07E4987E7299F6DCB3ECF3D84306F99EB671107F6AA18A1A2AC769D9E9598D7DD9E9625D7B425E697A6F216799B36EB11E15B61F438D348470D57730734E87DC87D078BDF25728A0C90AA0DDF6F85DF83F5CB829085AB71DED67F07D8116C7E5D25E19F3D86FCB3AB27F8FD0CB878894D107127497GGA4CAGGD0CB818294G94G88G
	88G10F81DAE94D107127497GGA4CAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGAE98GGGG
**end of data**/
}

/**
 * Return the JComboBoxAltRoute property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxAltRoute() {
	if (ivjJComboBoxAltRoute == null) {
		try {
			ivjJComboBoxAltRoute = new javax.swing.JComboBox();
			ivjJComboBoxAltRoute.setName("JComboBoxAltRoute");
			ivjJComboBoxAltRoute.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
			ivjJComboBoxAltRoute.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
			// user code begin {1}
			
			IDatabaseCache cache = DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				List routes = cache.getAllRoutes();
				
				//sort routes by name
				Collections.sort( routes, LiteComparators.liteStringComparator );
				
				for( int i = 0; i < routes.size(); i++ )
					ivjJComboBoxAltRoute.addItem( routes.get(i) );
			}
			 
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxAltRoute;
}

/**
 * Return the JComboBoxPeriodCnt property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPeriodCnt() {
	if (ivjJComboBoxPeriodCnt == null) {
		try {
			ivjJComboBoxPeriodCnt = new javax.swing.JComboBox();
			ivjJComboBoxPeriodCnt.setName("JComboBoxPeriodCnt");
			ivjJComboBoxPeriodCnt.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
			ivjJComboBoxPeriodCnt.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
			// user code begin {1}
			
			//period count
			for( int i = 1; i < 49; i++ )
				ivjJComboBoxPeriodCnt.addItem( new Integer(i) );
			
			
			//default the period
			ivjJComboBoxPeriodCnt.setSelectedItem( new Integer(8) );
			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxPeriodCnt;
}

/**
 * Return the JComboBoxPeriodLength property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxPeriodLength() {
	if (ivjJComboBoxPeriodLength == null) {
		try {
			ivjJComboBoxPeriodLength = new javax.swing.JComboBox();
			ivjJComboBoxPeriodLength.setName("JComboBoxPeriodLength");
			ivjJComboBoxPeriodLength.setEditor(new javax.swing.plaf.metal.MetalComboBoxEditor.UIResource());
			ivjJComboBoxPeriodLength.setRenderer(new javax.swing.plaf.basic.BasicComboBoxRenderer.UIResource());
			// user code begin {1}
			
			//1 ~ 60 minutes 
			for( int i = 1; i <= 60; i++ )
				ivjJComboBoxPeriodLength.addItem( 
							i + 
							(i == 1 ? " minute" : " minutes") );
			
			//default
			ivjJComboBoxPeriodLength.setSelectedItem( "30 minutes" );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxPeriodLength;
}

/**
 * Return the JLabelAltRoute property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAltRoute() {
	if (ivjJLabelAltRoute == null) {
		try {
			ivjJLabelAltRoute = new javax.swing.JLabel();
			ivjJLabelAltRoute.setName("JLabelAltRoute");
			ivjJLabelAltRoute.setText("Alternate Route:");
			ivjJLabelAltRoute.setMaximumSize(new java.awt.Dimension(103, 19));
			ivjJLabelAltRoute.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAltRoute.setMinimumSize(new java.awt.Dimension(103, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAltRoute;
}

/**
 * Return the PerformanceThresholdLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCyclePercent() {
	if (ivjJLabelCyclePercent == null) {
		try {
			ivjJLabelCyclePercent = new javax.swing.JLabel();
			ivjJLabelCyclePercent.setName("JLabelCyclePercent");
			ivjJLabelCyclePercent.setText("Cycle Percent:");
			ivjJLabelCyclePercent.setMaximumSize(new java.awt.Dimension(92, 19));
			ivjJLabelCyclePercent.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCyclePercent.setMinimumSize(new java.awt.Dimension(92, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCyclePercent;
}

/**
 * Return the JLabelPeriodCount property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPeriodCount() {
	if (ivjJLabelPeriodCount == null) {
		try {
			ivjJLabelPeriodCount = new javax.swing.JLabel();
			ivjJLabelPeriodCount.setName("JLabelPeriodCount");
			ivjJLabelPeriodCount.setText("Period Count:");
			ivjJLabelPeriodCount.setMaximumSize(new java.awt.Dimension(87, 19));
			ivjJLabelPeriodCount.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPeriodCount.setMinimumSize(new java.awt.Dimension(87, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPeriodCount;
}

/**
 * Return the JLabelPeriodLength property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPeriodLength() {
	if (ivjJLabelPeriodLength == null) {
		try {
			ivjJLabelPeriodLength = new javax.swing.JLabel();
			ivjJLabelPeriodLength.setName("JLabelPeriodLength");
			ivjJLabelPeriodLength.setText("Period Length:");
			ivjJLabelPeriodLength.setMaximumSize(new java.awt.Dimension(93, 19));
			ivjJLabelPeriodLength.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelPeriodLength.setMinimumSize(new java.awt.Dimension(93, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPeriodLength;
}

/**
 * Return the JTextFieldCyclePercent property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCyclePercent() {
	if (ivjJTextFieldCyclePercent == null) {
		try {
			ivjJTextFieldCyclePercent = new javax.swing.JTextField();
			ivjJTextFieldCyclePercent.setName("JTextFieldCyclePercent");
			ivjJTextFieldCyclePercent.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			ivjJTextFieldCyclePercent.setColumns(3);
			ivjJTextFieldCyclePercent.setFont(new java.awt.Font("sansserif", 0, 14));
			ivjJTextFieldCyclePercent.setMinimumSize(new java.awt.Dimension(4, 23));
			// user code begin {1}
			
			ivjJTextFieldCyclePercent.setDocument( new LongRangeDocument(0, 100) );
						
			//default
			ivjJTextFieldCyclePercent.setText( "50" );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCyclePercent;
}

/**
 * Return the PercentLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPercentLabel() {
	if (ivjPercentLabel == null) {
		try {
			ivjPercentLabel = new javax.swing.JLabel();
			ivjPercentLabel.setName("PercentLabel");
			ivjPercentLabel.setText("%");
			ivjPercentLabel.setMaximumSize(new java.awt.Dimension(12, 19));
			ivjPercentLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPercentLabel.setMinimumSize(new java.awt.Dimension(12, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPercentLabel;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	LMCommand cmd = (LMCommand)val;
	if( cmd == null )
		cmd = new LMCommand();
	

	Integer cnt = (Integer)getJComboBoxPeriodCnt().getSelectedItem();
	Integer length = SwingUtil.getIntervalComboBoxSecondsValue( getJComboBoxPeriodLength() );	
	Integer percent = Integer.valueOf( getJTextFieldCyclePercent().getText() );
	
	//the alt route may or may not be available
	LiteYukonPAObject liteRoute = null;
	if( getJComboBoxAltRoute().isVisible() )	
		liteRoute = (LiteYukonPAObject)getJComboBoxAltRoute().getSelectedItem();
	
	
	if( getJComboBoxPeriodCnt().isVisible() )	
		cmd.setNumber( percent.intValue() );  //cycle percent
	
	if( getJComboBoxPeriodLength().isVisible() )	
		cmd.setValue( length.doubleValue() ); //period length in seconds

	if( getJTextFieldCyclePercent().isVisible() )	
		cmd.setCount( cnt.intValue() );  //number of cycle periods

	cmd.setAuxid( (liteRoute == null ? 0 : liteRoute.getYukonID()) );//this auxid will be used for the alt routeID soon
	
	return cmd;
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
		setName("GroupMsgPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(317, 107);

		java.awt.GridBagConstraints constraintsJTextFieldCyclePercent = new java.awt.GridBagConstraints();
		constraintsJTextFieldCyclePercent.gridx = 2; constraintsJTextFieldCyclePercent.gridy = 1;
		constraintsJTextFieldCyclePercent.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldCyclePercent.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldCyclePercent.weightx = 1.0;
		constraintsJTextFieldCyclePercent.ipadx = 43;
		constraintsJTextFieldCyclePercent.insets = new java.awt.Insets(4, 5, 1, 2);
		add(getJTextFieldCyclePercent(), constraintsJTextFieldCyclePercent);

		java.awt.GridBagConstraints constraintsPercentLabel = new java.awt.GridBagConstraints();
		constraintsPercentLabel.gridx = 3; constraintsPercentLabel.gridy = 1;
		constraintsPercentLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPercentLabel.insets = new java.awt.Insets(6, 3, 3, 126);
		add(getPercentLabel(), constraintsPercentLabel);

		java.awt.GridBagConstraints constraintsJLabelCyclePercent = new java.awt.GridBagConstraints();
		constraintsJLabelCyclePercent.gridx = 1; constraintsJLabelCyclePercent.gridy = 1;
		constraintsJLabelCyclePercent.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCyclePercent.ipadx = 20;
		constraintsJLabelCyclePercent.insets = new java.awt.Insets(6, 6, 3, 4);
		add(getJLabelCyclePercent(), constraintsJLabelCyclePercent);

		java.awt.GridBagConstraints constraintsJLabelPeriodCount = new java.awt.GridBagConstraints();
		constraintsJLabelPeriodCount.gridx = 1; constraintsJLabelPeriodCount.gridy = 3;
		constraintsJLabelPeriodCount.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPeriodCount.ipadx = 25;
		constraintsJLabelPeriodCount.insets = new java.awt.Insets(3, 6, 3, 4);
		add(getJLabelPeriodCount(), constraintsJLabelPeriodCount);

		java.awt.GridBagConstraints constraintsJLabelPeriodLength = new java.awt.GridBagConstraints();
		constraintsJLabelPeriodLength.gridx = 1; constraintsJLabelPeriodLength.gridy = 2;
		constraintsJLabelPeriodLength.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelPeriodLength.ipadx = 19;
		constraintsJLabelPeriodLength.insets = new java.awt.Insets(3, 6, 3, 4);
		add(getJLabelPeriodLength(), constraintsJLabelPeriodLength);

		java.awt.GridBagConstraints constraintsJLabelAltRoute = new java.awt.GridBagConstraints();
		constraintsJLabelAltRoute.gridx = 1; constraintsJLabelAltRoute.gridy = 4;
		constraintsJLabelAltRoute.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAltRoute.ipadx = 9;
		constraintsJLabelAltRoute.insets = new java.awt.Insets(3, 6, 7, 4);
		add(getJLabelAltRoute(), constraintsJLabelAltRoute);

		java.awt.GridBagConstraints constraintsJComboBoxPeriodLength = new java.awt.GridBagConstraints();
		constraintsJComboBoxPeriodLength.gridx = 2; constraintsJComboBoxPeriodLength.gridy = 2;
		constraintsJComboBoxPeriodLength.gridwidth = 2;
		constraintsJComboBoxPeriodLength.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxPeriodLength.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxPeriodLength.weightx = 1.0;
		constraintsJComboBoxPeriodLength.ipadx = 57;
		constraintsJComboBoxPeriodLength.insets = new java.awt.Insets(1, 5, 1, 7);
		add(getJComboBoxPeriodLength(), constraintsJComboBoxPeriodLength);

		java.awt.GridBagConstraints constraintsJComboBoxPeriodCnt = new java.awt.GridBagConstraints();
		constraintsJComboBoxPeriodCnt.gridx = 2; constraintsJComboBoxPeriodCnt.gridy = 3;
		constraintsJComboBoxPeriodCnt.gridwidth = 2;
		constraintsJComboBoxPeriodCnt.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxPeriodCnt.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxPeriodCnt.weightx = 1.0;
		constraintsJComboBoxPeriodCnt.ipadx = 57;
		constraintsJComboBoxPeriodCnt.insets = new java.awt.Insets(1, 5, 1, 7);
		add(getJComboBoxPeriodCnt(), constraintsJComboBoxPeriodCnt);

		java.awt.GridBagConstraints constraintsJComboBoxAltRoute = new java.awt.GridBagConstraints();
		constraintsJComboBoxAltRoute.gridx = 2; constraintsJComboBoxAltRoute.gridy = 4;
		constraintsJComboBoxAltRoute.gridwidth = 2;
		constraintsJComboBoxAltRoute.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxAltRoute.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxAltRoute.weightx = 1.0;
		constraintsJComboBoxAltRoute.ipadx = 57;
		constraintsJComboBoxAltRoute.insets = new java.awt.Insets(1, 5, 5, 7);
		add(getJComboBoxAltRoute(), constraintsJComboBoxAltRoute);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	//do not show the route info by default
	isRouteVisible( false );
	
	// user code end
}

public void isRouteVisible( boolean val_ )
{
	getJLabelAltRoute().setVisible( val_ );
	getJComboBoxAltRoute().setVisible( val_ );	
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		GroupMsgPanel aGroupMsgPanel;
		aGroupMsgPanel = new GroupMsgPanel();
		frame.setContentPane(aGroupMsgPanel);
		frame.setSize(aGroupMsgPanel.getSize());
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
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
}

}
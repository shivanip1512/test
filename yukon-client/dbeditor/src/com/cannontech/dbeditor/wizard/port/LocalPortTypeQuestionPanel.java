package com.cannontech.dbeditor.wizard.port;

/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.common.gui.util.DataInputPanel;

public class LocalPortTypeQuestionPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private javax.swing.ButtonGroup ivjCommLineTypeButtonGroup = null;
	private javax.swing.JRadioButton ivjDedicatedRadioButton = null;
	private javax.swing.JRadioButton ivjDialupRadioButton = null;
	private javax.swing.JLabel ivjSelectLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LocalPortTypeQuestionPanel() {
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
	if (e.getSource() == getDedicatedRadioButton()) 
		connEtoC1(e);
	if (e.getSource() == getDialupRadioButton()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (DedicatedRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PortTypeQuestionPanelB.radioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.radioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DialupRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PortTypeQuestionPanelB.radioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.radioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (PortTypeQuestionPanelB.initialize() --> CommLineTypeButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getCommLineTypeButtonGroup().add(getDialupRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (PortTypeQuestionPanelB.initialize() --> CommLineTypeButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getCommLineTypeButtonGroup().add(getDedicatedRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (PortTypeQuestionPanelB.initialize() --> CommLineTypeButtonGroup.setSelected(Ljavax.swing.ButtonModel;Z)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getCommLineTypeButtonGroup().setSelected(getDedicatedRadioButton().getModel(), true);
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
	D0CB838494G88G88G31F5FEA7GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF09467F5CE0C5D54D1EB5991A935F1460EC9C2BCCCC39A92B3366C1016BA38A6991C416E357E232EDCB3AD24F4CCD2B94613CFA76E90C6985988DBB6421C7E3004456907C39CF7125069DFF040C5BAABB2081815EEA59DBE5D1EF67724136A38FD6F7B765BDB1D6E2440B8B3514C3B5DFD5F5F7B7F5E77F6C58A8F5FED49B1979212E3A15A5F499CC22EB490724E4759AE0E4B33194C4470D78270
	86F97570CE3896C1DB7A07A6F38139E9DF9EBCD743F32D4FE4FE9B6E7713DB76E55740CD0AF5B6C0DB744252367957899E676B6C224F0F14644375F500DA406175D39B2E7FECC9110677EBF88EB9B0EB20847B3CDCD22C6156C09F93008D700E92565F8B575DF465098BCE2DDFE65EE292280FD87762BC06B9B92C5046EE502EC029B5D60AEB151666EBE372603985G194FA5174B5CF0ED74FDB25ABCAC0D5B0705F0D88A2B627018FDAAB8A34881FBC412D5174BAB8D8BA19F5C7A27A362EF222222862530CF88
	0BA1321D4EFF35B40F4E87B442738DE508FBAB02EBBA615E87B0D64671FBA6903F966E1F8658DE4679FC7EF42346C7E86C1BA42B66B36B3E94FC6617B37E9C65F1BE17CFBC11738E5549AA874ABE82ED99G4B815683EC81482FB0198FD2DBD8F97CA8DCBDC3EA1BB4BEAE05BD11C8EBA8A01F1382E2886EDDAEE851F03D6230A48788E17A2EAC2DCEC24FFC4046B5DC73B8A6136547396E17186FA4598F0CDBF7A511C9A4F35ADADE41E451D8911789DBDBCA3976E045EC5B1C3D76EF09F16D5861BF37DA122C5D
	BE72BBEBC41BE711E16D5595286BDB288DAC03FB9C076399FEB1456FB060191D96D3BCBE979CB11955A35CB6B2AAB9AD79375FC6160E86134A4177631B2C26A30C06EDC792E939135ADD717BEC4DB7F4DAD6D0FC37814FE8F9036271D905B6D2E9B2B37BDA5700FE1B864F4B8156G18G32818A2BCC664AAAEEE3BB9DB5FF200D7984D9ED0D86C381D1A6EC6CC30E3760AA46D4D96889AB2A909E967B85B9A88C05C405E8FD9EF1208C06E60DFB0C5A7EB02C51A506C5D9E0FEAA8B6322AA4A8A6B335101BC8D4B
	A2ED758747411D0571886173BE6D580FBEA8A86A071100200A719E2CBD4B01BA19947AE1C618G6E19DCFEF1124A91783F829051657071891C6F2CA8C33840D0A2FD920D78119332268A65FCB30D9D0B60FEE9951771FFBB9137732849DCFC144FF34F09B78D7334870711FCC11E66BE169E41B135505FFF145B4D4F87385DE43FF0A7D977032BC96D86A1F61459CB7A51C47F39134E0B342DBE4A63FEE9E4609AFCF9BE60744E0D996E4FED0DDC4D270CB8A6670FDD28B3932C3F9D207083AE6B27CF58186D368A
	0A482D75028C06A6866212E7F45EB3E153E4EA51E8C759639E35CF731F7DBACFFFCB7BD643BAFE00CF8116812C87588ED0DCEDB237D7F3993FBDF103C1D74639DFCFB27F67A12754715BC5E949CDC14B1E84FA7ED834B05D7C2C815791A745305AAEC644F0GEE942D6D1F9B10A6ADEE0CC87A1867A8BE82F1268D9CDB5171AFB4A03D022A4A412128AA76092A9A8C0F465B3752351468D0A49E2278DA5B9BD08E529444993D7F4B8D682343D2B8ACD2A35107E86D2F5098A10644F1A03ACD0A06D5064FF1E2DE75
	09B82D45A80B53CAB0460422986FD82E32D2C0B9C76460B8189E06CC32219A6347DD25C5869905G07B957B2382E2E66F1E02A0C4701C07FD7C96D6FAFA65D4B525F3851DAF40C79E27531F89CE0B47D52191C769DB4D6E9F8C54B2BBAB1FEA74D18DA199D6DA75F17B0AF454D419B8C0B582FB52A2AD238CB1622914DBF1738F1CDAF50F285A0F60C7BE76C943673D828B9E61B948AB131C3DB66B1144DF311C2A3FF4718EC6236A43A8AD03F62C4D404AD860372D9DD87A1571B89ED7D1ABED83B6AC2FD048C71
	BCE047B57FE3026735BB6DAC26975BD96CF458595A2C5FB21977DB19221F2D867B8DAB3C5FBABB0B9B1B603A8D6700F63B5E2FCE657DC2700CB1E691DCB3EA627D583A2F687DD6554CDEF7BDBC13DA484369FC35133CDFE1AD0BD15535EC5D46DAB65F36DA6C775A946F67535A47EA191C1757EA39886D17B1437BB556B13A927BB15B5975B166EB49F27251E8509ED503387B89AAB8F8A42AD213E2B9782E32BCADAE796BF82E1DD6067613864F4BGB26B74FD719C4F352FEC7DBA31BDBB1D72CC31310E5116DD
	9777070C1BF92E7D41A9DC53D9EFB2F757F35B7CAF17F13F77A80AB49C242906E6B7DFF39B7D05616EF53F865711202C28BD9050E25C463E6DC6FAB19751501A0CDE359F62B23AAACC8AB13BB285F14B65B2FA904FE36F28B7727A696020F5CF8ADE876B35B3DCFD1C57ACB8D730FCF03F1CCF34B38E34EF87E0FEBCC37141F0D08D8A21600CD8D945E82C8D1742D58884AA3D869A6D1EA1852273304AC8F5D775F3BA1FFF2BC523E3C57BDDE44377EFCEFA7EA9F876A6EB75F1C61F7FF81CCE5E4EE8B56934AE05
	BE9B8198BECD6178FC876C358E4E43CB8A46DFC19E2DE3F1A354E80C9B1FBA181FAC577B9FD4313FBA96D4986DABDBB86D2B4D3712316A3414E737CC8723B94B110A760BAA2751F142E48E1D60EB7EC81DCD23A7120C46E5A7B82F65D1FE16D88D38EDA77819F7223812E0DCD37BC48C17E202FE18DEE81A6E3ED8554FFC6B01A8BF5FD6B6707983131C4EAB00DB524069FCEC12532949F692579D4789F59E0FCFF15AB640383C13FC6C9DD375B81686F1BB3B78C4F9C4124701CC2452AECC29F63A775BBD343983
	6F2B38DFFC47090592D98884A54647471E04C93C29E7B158A27199EDF1602814D41F6B03E3561613CC0FE3A7637AAC18AEB2AFF97FC94DEF9E1A613C2E02BEDBCE727CAE3F4CF8AEADF71A4CCEE7BC8F76BA671ECD59F96D8765B85F95A7BB2F6D5271FFC771B1831E2DFFBB0D01781C8EED5B1D7C0CF11F9B730601D3F04E84C883D8863096A06B940FE5B34E63E401B3DC8F44E6B9AC047AE6A5C9DA7C2ABD05FA053CC6FBF61CC2B9503C06709C3C28896D1838E09F82F0GF881C2AECE430327962421A31614
	86A667F76405EA8FE92EE4670F77979C37B2E19C63A75C0C7B5CBAE84B825881D06406BD93E0G60D3B767EB374B3DA0DFE816FE0959E8DBE2D62951796CF57237E541F1CB5D49465D2EDC5FFAF7AFB8EED542FA4C275FEAFD0FA45B6B4E74465D1CE8F538FB9BB1CE218F2FF7F3BB7F79E954474EC648EBG1CGFE0098C086406AC62E0FCB2E560574211792C0A77C4CB0D7AD9A3FD7546B13D3F841F19B9AE70FE33C47A21C774D0DBC469C0DA64EE5EB1ABDB6B98D4EB9636C894F0DCD49467DF04E3879405F
	44693CBAF9FD320AADB8EEACA91D27262ECFAEDDF3463D1770FCC3D33218B06CC19BDC82ED2BG5683EC8648EFB619AB813C4D5C867F465534108DEAB1218DF05DF076C8E2FE9A2DFFBFF3FDBCBEBEE7DCA20F21665963D89D2B0C665F0B20AD8300617E52063865701C2963BE22752ECD701C5D4C751FC9772E6253907F817C27B9FE9D452B275939824F8C8C3F24EC215AD031B64FFCFD9CA95A599A5FDDF00DDC2D6FFCFD926BCE9C183E3F67CEDD4BF8401D3C1631461D3C1671087B8D0607B90C95D55E7ED3
	777C350EA75D29EB9D362654F43E5F141C4EE3CD4969BC5EB4BF1D6E26D4F49E22B2FFD1BDC01254D84EC3E4A63C366013F7EBFDBAD469DC1DBEF72CB9B59F7F571C1C0F1B1A71EC28C8D1F9D8CC24B52D39C8EF0BFBE20FAA0EA3B92C4FE2BADE0504D81C436BD2BA1E35259A7F0D66D472E03C9ED00C3C9E57710FAAFBB43CEFC41646FDF51ADDAFEFB47AF70666834B35EB26F65DE0708DC6C7766954327B7569643233342416DDFE4B4232BB5012DAF645AD8B4B2E2285E5B0948C84E52DB244E473D76AEC59
	54EB3279FD44A81BACCD86595AB53FC52B4B3444E543EC72513776D11BD694D121819A8B945ADAC254289F87617172C9FA26F240DC6DAD7C7CF34F64EC3A9C5A9C11A9439CEDF1FB9E772456096AC92E1399CFEA1DFC413330CEEE71245649ED1E05F5F207E7FEFB7E550C1157FA9D6FAA43F3650894D6B3DAD0FEBE3A5F8C40FDC0175FB7ECD8B78D8953D2D4A5245D037D5E555FC51B205FB600A240D7FB0CB5276FCE215CC7A03B83D27AA262B03135625808DEDBCD037EF7832C815884C0DA791A21CE9C4B8E
	53DEE188CE398CFFDF831EEB5A45C0F0D800D336A1DBE56DDF6AC3B9348761B09419154AEA354BD74A0D677B31D6F63ECFEB657B688F9B79FEFB370E5BF590F12B60F92D0ED38E727DF6030E1B8CA34E56E6B23734F1DCC6044F6757F1239EBE5F180EDB67617329BA6EA949B89F63EFEBD42B8354B3FD65CE729A42DA6833318D3FAF7845D9226B770BB62C970545A91BFEC46F84E5D71686C0C452E8E9352D365AD6D7DF454F687FCA0C5F85104C7424B5ED0422F62D262DDD3377BFEEE1E76356927EDDC0G5A
	1636739AC95D954C5774B3879263F2F54941C02BB06A255645F26EDBCA7234719B5BF94DF1B51DB33F43E4AE87E86C60B54719DCDE075BD1F5AF89D93F183466D87CE0446A6FE0F446BA8CB54709A7F2581ADFAD65EB2E0276AD9D08DBDBEA34951467EDB9ACE7E76D3F1C550EF667A146764AB2E3BB527B105958FEF1D63BC51B43CE38AEBE3844F973B7FC0BD45EB812321E6A68E4BC8DE857E52FBFEE097B61CBA3C5A469D90FBA96633F3E0257F953E18EE6DB1B29AF2C03670D1D5C7EBAAC34C6878110595A
	4AF3C829330B6A6DE03BC64F4E2E39B9E00EA6C38EC5508721CC23333A0B55D8F83F7D64AF42D8739D1364608C182C9072040223E12C6F92C65F37427CECF6C3972779A16AEFAB60F9F59777072F1D0F53781C075338B9850D1694B4E25B0EB9B43ECB6BF22A9C95194EEA1CC67FD9F568321577CFDCAB2F1B2DDF589D37C7F62EC93F00BCF483BE504D4FB0AE6F87F06D972EEA27170EC0D015E4FAFC8124CF16A2222CCEB3ACFD23226D3D5DA55777EDC97FF50E3B751A4731BADBB57D16E8D1F7DC3E8CFF0262
	538DF896E39E56E34CD2E8A3BD3CAE71A39F5AFAG1EEFGD8823092E08340169EAE4B9B3D35E4017320DF0AF8A1818899D2E96D3D7DF5FEF3334B4FFDB963BB3713015065146F8B4899AD9F522E350479B2634F5F0B7C7920AD84108E309CA093E063994E5FE35EEA469F1A03C7D50561B17AD604B3579F942762E85C1899EFA7AB0C3CED56565F21DD8B4E4E759766DF6FFCC8F4FFF8655DBFE43FF1D4717A4A03F49FC51FAB07798F93765EBD0F447F589A8D7DC417E53142E539256B2FC9601918157BE7A2FD
	039A4DAB4E4EFEAF40E8FDB8EC032B31CC5F073F10E26147B12AE26F6D696A7633753FB790677112675AF8ACE865BCE61D65B1694B9F91DD274E0BF84FF4777C798A12424FC1F3E7E03D8BC3D7A1A7728B237A18459710A7BD77E36E4F4FC297F00F515FA351D7E6FD50B1BC4AF4DEDFCAF45FF59B6E5716FCDE5F09240E954F7CD157E07C7F23AFF5AE7F38AFF9AE7FCF3E64E76CE7FC731F75DF7025BE6BEF71993FEBE0FA6F38043AA9761A4CCE00C1008840A22F7E1E60B728907C22EB2E8FF784C6C5EE28FC
	7D4B3D29F98D74AE7C6D57F8AF3EAF9D12A0E19E4779B9DE69C5FD29D204A235B3FFAF4AC88E0E0E292C2B067F2C97EDBDA40ED0B44BE34FD768B935176736F68B4F01D769382FD8F88E3C464BEBD20F9DB4462402F3107F1D1B3F7E44E4FB6FA06E73CE686B8708812483AC83488458G1085108DD0F05EE42E84E884708184GD400AF81ACBD4FF5713F7DD5297C9210DA92CB95A28BC1D409E66387DA0D742F0479561E1F1FFE365E39CB29F5693F3430AEAFDF226F43C187788D88438DDD42F31CB4B222083A
	4D0CDEAA50F1BD0ACFD20298A1D8AEB5C575E682FA334E733865789D468816CB5DE76139D46105643914B11FCA4C77AAAF4477F4267F6769DE60G7C7DBA6EC50A73826E04AE1B7B87D34B66614105E573634164F67E134139F67E334164F6EE9AB45A39969F86112FD13005813D5F73BA6EA39D77A21D93F13F8D8654B19D3F15520578EE9197647817A8DDD394B7406BC6865CC79A6E55C154AF308CA9B39FEB3ECC4F7741D008BDDBAFE31FE098542D4BE34FE524B198918CF49768380FF4DDD437F27D7C190E
	2BEBB56A0759487DC304703D3254F2EDFB65D3963ED70EDD60B6F76BB0516DC8B8E278AE020A0F610BE3FB63F8AEBE4D87B7D90CBEF8B74C3BAA093D9A017176BF964E6FFF66F05C8E8B67973F5DE77F83B0FA288F08D83115D894196C1D4567BF5CF3625F57BF5B2F6492EB8FF9874BACFD224C3E6888E0B5CB7B5C35136C0EC890B67AG5FC3F6AA74E2D154691068A505A33D783914220A817D7D182212837425FB8DFE3AD353ADA8E3AA5A385BCBF60671ED9AF913B5070470E88D5B705DB17CFCA547530252
	A4A8C682746E277462ACF6FD167D8F6E2BA8B91335BA1718BB49EBA1C988F48A43906EFA48EEF5CC9615B1A99408C5CE33097226D0A2184D6642F487F57611D93B9FBCD1A77A1358DDE9FBB76F6D841546E4D2C3FB62C9522E7788340D8978990BA87728782131A4176BEDDFF8CFBD72132E37874DA4C7E3F202FCC64C3108CA2CA342F8B0B48DB74C2A94EC312AC02DAC04957CC681A833A5D0461B74856CDFF934F67137FFF511AE909F2A1243CC14B034E6D41446EBBCA3A378ED9D6FGF205698F6953E39FFB
	C21FB7E3379DB873442F07408E5AC96EF8EDAD7D2D233F7578DBC7B1F594D3D78F425DC6667DCBEC0FF11E2971GBD7BA8BD28FEFBA7E8D07D172D0FFE7D2946EDDB003BCEE2219F8B00928A623D4EC74707C47966950B8F6D495874E554D184CDA506733E9683E16AC5B710B3AB3051B589B52AAB74CAF3EC0A5DB957BA901D276B881F76502EFB69EF41B5F30C235E24E38BF1F97AFCB026330E2A6821F1EE5364F1D76E3F5D1BEBA6BBFB081545FD70B28C5E60E5B4367710DC087B54C1E67D7D69F1D846B4FD
	B8F130CA5EE755D21BBAA65AD45874ED5208ED98CE3851B02A8D220CE28B01CD3A7E74F87AA085CF5AF9AF3343D8D3A9AE1CC8D9135C78A10B6B1BBFCC2C29EC276FCC4B7BCD6616FE1E53355B0E1154E72D3EF1A194D20550A71EF0G4FDD2D532A984F75DF36617C8A6F23B532B6D202F4DFB50C79FFD0CB8788CAEED7605593GG08B6GGD0CB818294G94G88G88G31F5FEA7CAEED7605593GG08B6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGG
	G81G81GBAGGG8F94GGGG
**end of data**/
}
/**
 * Return the CommLineTypeButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getCommLineTypeButtonGroup() {
	if (ivjCommLineTypeButtonGroup == null) {
		try {
			ivjCommLineTypeButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCommLineTypeButtonGroup;
}
/**
 * Return the DedicatedRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDedicatedRadioButton() {
	if (ivjDedicatedRadioButton == null) {
		try {
			ivjDedicatedRadioButton = new javax.swing.JRadioButton();
			ivjDedicatedRadioButton.setName("DedicatedRadioButton");
			ivjDedicatedRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDedicatedRadioButton.setText("Dedicated");
			ivjDedicatedRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDedicatedRadioButton;
}
/**
 * Return the DialupRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDialupRadioButton() {
	if (ivjDialupRadioButton == null) {
		try {
			ivjDialupRadioButton = new javax.swing.JRadioButton();
			ivjDialupRadioButton.setName("DialupRadioButton");
			ivjDialupRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjDialupRadioButton.setText("Dialup");
			ivjDialupRadioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDialupRadioButton;
}
/**
 * This method was created in VisualAge.
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 */
public Dimension getPreferredSize() {
	return new Dimension(350, 200 );
}
/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectLabel() {
	if (ivjSelectLabel == null) {
		try {
			ivjSelectLabel = new javax.swing.JLabel();
			ivjSelectLabel.setName("SelectLabel");
			ivjSelectLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSelectLabel.setText("Select the type of communications line:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectLabel;
}
/**
 * getValue method comment.
 */
public Object getValue(Object commonObject) {

	//We can assume that the DBPersistent commonObject
	//is simply an indicator as to whether the port
	//should be created a local type or a terminal server type

	try
	{
		
		if( getDedicatedRadioButton().isSelected() )
			return com.cannontech.database.data.port.PortFactory.createPort( com.cannontech.database.data.pao.PortTypes.LOCAL_SHARED );
		else
//		if( getDialupRadioButton().isSelected() )
			return com.cannontech.database.data.port.PortFactory.createPort( com.cannontech.database.data.pao.PortTypes.LOCAL_DIALUP );

		/*else
			return com.cannontech.database.data.port.PortFactory.createPort( com.cannontech.database.data.port.PortTypes.getType("Local Radio") );
		Josh Wolberg commented this out on Sept 20th 1999 to reply to a
		request by Corey Plender to remove panel from port editor*/
	
	}
	catch( Throwable  t )
	{
		com.cannontech.clientutils.CTILogger.error( t.getMessage(), t );
		throw new Error( t.getMessage() );
	}
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
	getDedicatedRadioButton().addActionListener(this);
	getDialupRadioButton().addActionListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PortTypeQuestionPanelB");
		setFont(new java.awt.Font("dialog", 0, 12));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 200);

		java.awt.GridBagConstraints constraintsSelectLabel = new java.awt.GridBagConstraints();
		constraintsSelectLabel.gridx = 0; constraintsSelectLabel.gridy = 0;
		constraintsSelectLabel.gridwidth = 2;
		constraintsSelectLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsSelectLabel.anchor = java.awt.GridBagConstraints.WEST;
		add(getSelectLabel(), constraintsSelectLabel);

		java.awt.GridBagConstraints constraintsDedicatedRadioButton = new java.awt.GridBagConstraints();
		constraintsDedicatedRadioButton.gridx = 0; constraintsDedicatedRadioButton.gridy = 1;
		constraintsDedicatedRadioButton.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDedicatedRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		add(getDedicatedRadioButton(), constraintsDedicatedRadioButton);

		java.awt.GridBagConstraints constraintsDialupRadioButton = new java.awt.GridBagConstraints();
		constraintsDialupRadioButton.gridx = 0; constraintsDialupRadioButton.gridy = 2;
		constraintsDialupRadioButton.anchor = java.awt.GridBagConstraints.WEST;
		add(getDialupRadioButton(), constraintsDialupRadioButton);
		initConnections();
		connEtoM1();
		connEtoM3();
		connEtoM4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isDialup() {
	return (getDialupRadioButton().isSelected());
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
		LocalPortTypeQuestionPanel aLocalPortTypeQuestionPanel;
		aLocalPortTypeQuestionPanel = new LocalPortTypeQuestionPanel();
		frame.add("Center", aLocalPortTypeQuestionPanel);
		frame.setSize(aLocalPortTypeQuestionPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void radioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	return;
}
}

package com.cannontech.dbeditor.wizard.copy.device;
import com.cannontech.database.db.device.*;
import com.cannontech.database.data.*;
import com.cannontech.database.data.device.*;

/**
 * This type was created in VisualAge.
 */
 import java.awt.Dimension;

import javax.swing.JLabel;

 import com.cannontech.database.db.*;
 import com.cannontech.database.data.device.*;
 import com.cannontech.database.data.capcontrol.CapBank;
import com.cannontech.database.data.capcontrol.CapBankController;
import com.cannontech.database.data.capcontrol.CapBankController6510;
import com.cannontech.database.data.capcontrol.ICapBankController;
 import com.cannontech.common.gui.util.DataInputPanel;
 
public class DeviceCopyNameAddressPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ItemListener, javax.swing.event.CaretListener {
	private javax.swing.JTextField ivjAddressTextField = null;
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JLabel ivjPhysicalAddressLabel = null;
	private javax.swing.JCheckBox ivjPointCopyCheckBox = null;
	private javax.swing.JLabel ivjJLabelMeterNumber = null;
	private javax.swing.JPanel ivjJPanelCopyDevice = null;
	private javax.swing.JTextField ivjJTextFieldMeterNumber = null;

   private int deviceType = 0;
   private JLabel jLabelRange = null;


	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public DeviceCopyNameAddressPanel() {
		super();
		initialize();
	}
	
	
	/**
	 * Method to handle events for the CaretListener interface.
	 * @param e javax.swing.event.CaretEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void caretUpdate(javax.swing.event.CaretEvent e) {
		// user code begin {1}
		// user code end
		if (e.getSource() == getNameTextField()) 
			connEtoC1(e);
		if (e.getSource() == getAddressTextField()) 
			connEtoC2(e);
		// user code begin {2}
		// user code end
	}
	
	
	/**
	 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
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
	 * connEtoC2:  (AddressTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceCopyNameAddressPanel.fireInputUpdate()V)
	 * @param arg1 javax.swing.event.CaretEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
	 * connEtoC3:  (PointCopyCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> DeviceCopyNameAddressPanel.pointCopyCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC3(java.awt.event.ItemEvent arg1) {
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
	 * Insert the method's description here.
	 * Creation date: (2/23/00 10:40:51 AM)
	 * @return boolean
	 */
	public boolean copyPointsIsChecked() {
		return getPointCopyCheckBox().isSelected();
	}
	
	
	/**
	 * Return the AddressTextField property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getAddressTextField() {
		if (ivjAddressTextField == null) {
			try {
				ivjAddressTextField = new javax.swing.JTextField();
				ivjAddressTextField.setName("AddressTextField");
				ivjAddressTextField.setFont(new java.awt.Font("sansserif", 0, 14));
				ivjAddressTextField.setColumns(6);
				// user code begin {1}
			
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjAddressTextField;
	}
	
	
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88G64F2CEADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF494E5128861C788EEC0C01675BCD65163B42B5C135B63F6B9E5DD3C439515FD62093BBC173D4DE96E0CAEFA6C931553AF139992A0018402028498A0EA50988822E4472C8AC900C0C0108881A3501969498C19CCF73AFBB293FE642A3E1F6E4EE4A6915EFA4F3CD7697E2A3E1F2A7A6A2B2F2AE60694EF9C6F4AF69493126DA27CEFD7B6A1251F92F27A45D9EE014BBC6349A5363FCDGC5647E28
		931E8B013673A6CFEEA1395D1B876D7C331E5CA3B7F8F25F0077D564AEEF55AEF8C932CEA1501A7DCBA6773E4E2193E21DDC5AFED3490767F200C2D8EBA7C03F33387E8EE58D47B7C1FBB9FFC748F69032DF42BE9FEEDEE343CF677DCA00369DA00B76B92414F0FA9A34314FC4F8CE15D85F5970DCC2399B6829627D1AC70CA78BFE750E23G57324DAFE0E78B9B5B441FEEF297D5C48EF97C42C1F8D69FE9EB7D442B34D7F825F0D8899B32B7D061EB11FDC1C351AA22417312662B70AAEAD705CF6E8CFAE50F27
		16BEEBGF5D8EA17F77BFC1A2C6BC724309CA2B3DAD0062FB6675179C186E8B7FA917768E654DBBE3C1782D4FB059C3FAE2866FC0EEA9BCF665D72E6D2B946FA997FD33D169CE3FF1E668C79F07ECE3AAFB8EE9150F7820E71F20FA2F81964933CDC5800FDB321CDE4CF6E9AD96077ECC47CF6E86F87B8AB8B9E7F720E60316942DDA4D07920EBC5929EE3B263ED3CEC7118FFE71AF3AD351DA71AF07F67820D78BD3945GD5GCDGB100F554E6C71DF8971E3BDB0C9A253DDD896FD655BDA11FF6C87149A1F877
		F80042F1DF48DEC57391426C726C6672847C88C873F737C984A65B89A26CA87D0B5BC8533C87DD2E8432CDFEEC2BE3821FE30A5F12ED3E9F7536EF8B6AED8E3C63B86407610FD17C4ED68B4F766A89EA8B58EEG5A24D62167266502178667B8D2D8596048CD404B587BF6BB0A830C077200458B53715D27D047CD00B7GD281A6GCC874888889DBFF962033EF4DC8F67C08BCB21E31196D5520CBD11E048A7EBDC5797155EF41D88583821D7BC0E69E5628776BD1AFB774104FA19BBF13B038419BE5684AD3D30
		35FF19F46D2AE0CF5BD8EE6E619F69D9E98832BD4CB171AFD1FC338D4F76769A0A27ED204D82E07DFFCA4F564EF371B6F29F45B71C0B37117D9B050D7800B683006D6F51E6740F65EDF0C6814E8244G4682CC861851A676F8EB5336EFF90E0E5876170D7D0F265761A9478CCD2A8F6B0694764A47A5ADA83504E41D70BE4FB4A1EFAD5D464D27675B8BEB6C134332A699C1A58C33031384E352D91F171A0A300FA6D3EAE330DD06855AD5A2664DBE398AF5A06946172ACFB2E42B8723AFB909B651A99D0799E182
		5C6B906AABCAFDEEA6486F8C89DDFD73B5621A21ED0404DED2CE2E0467C1B0EEB9842EBC68C5B6A42DCB5868A1FAE607C07F7CF6B8BF6D62ECBE57A4EC303660D6D27E580104B6C86F45B0332732F03C8D1E24F39F813C8A109696F76A6FB6B775F18E109F710EBC59E9796D096D377B59DB58679AB9F1E36CB4D79FE3193E33CE23FC13C02E19GD9E12173FBCF3A19AD6E11F4D9D85F679A980E6C33F600751DFA8A6F9639D38E9B3532AA07FD7022733D7F6529A516DD7A9553E69E20F874D3B5E0B83A09FF64
		D49143439CF6AB977447A9DDB28CAD5892B164E332E184432D56785FD1DE74EEAED06732BEF786E5F5AA1E5C9200F2C5489A78EA154D3E9A9425AD22B6624961F67A9BFA9FFF867D1B94317FFF5A00B156C5337D9AEDDB74419B7A5ABB9CFBA5FE3527FD0C6FF30D1C2B1C5FDAE3AA75E9AEBE57DA93BF036247038CB3G661BF29F5E58E4DB3BAF9B345B6237672955DCC37075ED47FE1BFE4CA63EEACEEE53AD4D09EDBA501C5826434D3D5BECE7F3A21BE534CB4DA823928D5B4EC07FE67407DEA59C1669C1B4
		07F0FDBC5708F28443C163C45057AA9B470267E530DF95ED79BA6AF35740FB39AA7CE4F40B70132567FFCC261DF94B11A81E9AFBEB2DA3C5E5B6BBD6357CA463E94057E88FF2C8EE87C555A81130417057D2FF4E71FA8E45BDF9B40AE34B0EA8C16C2BF65584E4EF5B9EA5D69EB464768A7CF74C00FB24A6A005DBE5DF59B929D32A1022C6855D0E0AFA2057615BD6777B6FFF62CE9E5A177B031ADC9FD6A37CDEAA5BFAFC6B8E42F8F837CB27BC6018C6584A3DC1B964AB77025FB1AA68FFBE84570ED568D158B0
		3EFC8DD22FFA7DCA3EFE57C626835E7F7F15059D5C9FEE12F04F2640DE4E8448D005BFF41DC63A70075C6957A82190B3B7206D6FC0DBCA5DD2EC330FC5AA4BAD171B74A9FD50B31252999FAF1CAE44F3A1F7C4E488E750A19F143BC4CCF2FAE59C6D38940AD831CE7619E5708C59FCB943AFB81358CEDFBE037E3B4D1C7F55B30B8946ACECCEF62EA6C9B8272A855B616A434B0967484FAB769CB98DE443FCFA96BCB3G58584F5F45780CEDD40F941424E9B84FAB9BC59E3D88469583EE274666D2B52697566F9E
		3768B7975A8BG0A75C47D2E59A47A9D81FAC06F59EF080E7DEEB5F35FEDA6EEAA45C5C089831CA226BFD4E84F5FCBC81EB3111FC018293379676971394E71961C17983006A16CADF656FE7F6E56F5459B2421A65F33F7A43B8FE138F2E9A9BC413AF548FBFCF2CC6C7B8E89F945D81423795E5D715EDFC84F583A0ED68CC4F8D39325E72AD12901F0F8BF18CE0D69E20F87E5BC2FCC8F17765A737F29ABF6B816A551C32941642FB5ACBDCC78C91A137174C38F66F409EA99DE4AE4C5EBA4D891B1029874CA06C4
		0FF1371A051A4765CBB1C4BDE0300E76BD955A338132GD8EED26A967738B362492D86E0E7F0FD1D7D8C9A003F91E09AG73D3435CE893D76817394E27EFDD4976A84E8389BD5E3C6AB3E1AFF70EA35517D7B99255A8062CD86F189DE17ADB943174985A3558D9F2E2842F25BCED4A373F53135B2FD3483740EB17EFCAA713EFDE27106F70A6146F8A1DAE4F77EE5FA456DCG7315C7451AA3CFA5DA539795776D099D76F3B4EDDEE5427B96A196E57226C6634F114707FBBF9170D323629C7DC4AA321D23AF40F2
		E11F407319B1CDCB8E73F13CFD2645A5FC9C9179C0D4C65EE7411CD9G4E98444AG55B13146D3ADFD56A16AE23D54A14E2A71B9FDF34C0A0355D84F1818655C2FD03BCE09456774AE837123E271B97DF0DD647493001657A56253F75D716B073A3A2FB9A42E4D46D57598D79157CE0F9B474E40A7475709B3EEB3A90F670008F9789DD2EDF42B33F631DCA3931E0B3AC4DD3031AE0F73D3FCDE644B8F795147B7C0BBG508F60C600E9GF381488531EF9FFA93793F6E7B164C79D9B998534533D72943D77A5421
		7BC2A29D3ED5D612D807E6544474731ECF68276A02586FC91B2E0E57FBFA0C0B67D50D6315717040A94143108BE20F8A82081B866DD966FE0C7496584EAA0D044DF37A298FE38FC58B36864382D7CBF1DEC52309G43DD161367A8036444B9CA2A1CB8C799A9771EF70F131365B03C16C36F22F9A05F82G06EB54E82EF151133B7D22580FEC83632CC6333D0C3651BF327675FADFB96D550235E67AF736867A1B757479406BAB28BEF5DD566941429B1C531EB6D61384799B0605FC0ED5C4F2AF61FD6A8C256683
		73AC7C4F56B5094F828D8C188D9A06088B540BA2AEF832937D61C4E8CF8318E36A62E4DD229A6B7C0B4C361D17E0AFAFF537F3DE0771A7373DA77D09EDAF430FB62FAB914DAB475B57B37EB5A64D72BC98D220DAF86CCE479B1286B9ED7C78D768F8C6CBB65E591A4C06194ECBB759F5DEC904AF586E336B28BCCE9F1F45359BAF09586269EE736DB067FB7814FD3EC05C780BF16D34CB42E72C8C609D97B3E5FE1D363BCC7E2FD351C70424AEA5824E24709B9A5B2B62DE19G73CC8548GD8F8095DAF4C267EA1
		0A712BDF135AC19D47D459CB326858B64D7CFC905AA100D400F400195F8899DF48459C0DD99FA620AC43E1F2F6997136357332A55B7E4B496E7A16A82D53DFE6F77A8A932FD1FC0A8D4F6E7A8CEA5B58CE87DAD9BF61FFD4FADFCE0076E49377D81D70C953CCDCE191628A2F716416DEA3F07B68DDD2856D7DA6EE7A47E2EC33095BC57DFDBA342719387FBCA5464EB6F1110F4458CC9377EF35E2ECD5FF9127BF240A4F849BG1752DF6C51A3E7565A4FBD065D9E4FBEAD68EB605B4D6F1F559DF9FC7C547E626C
		57F4601C8B201DBFGE49CA06250585BA20FC93FED9CE968F7B0616718739AF7B82A87303DBABB400AC35DF726B9591AA3B431660DC01FBDGF177EA769898EDE5C4B63BC3997D79EEF4BC17DB081D3EC53753115FFFF15869DFF623230E567371CC36875F9232ADFAF0A2897D631F13FEC6DB1242E47A0CBF1B0EEFA1D57DC7B8599E25D1DF9A83DAEA0A5837E027584BC9A9420F862298234C06F696C07EC030C300DAG9FC04AC051AFC67BCD04760C01ACF727F9B9BCB3211DB7C8747B6FCE5546E3D6CA7733
		0273464BD116GE70794CE777167ECBEE73EED9CDBFBC9543EF6FC8DF7D902391774326EF2FEB671D9E03387311E5DC6D61141F5988F98F0F9148D426FB6E8F8FD1054C128772C3A5ADE564861FC64F088419804ADFC5F7B8DE271D8363956BBC5383E9C4672100FE452B5BEA99236BCFB1070F1EBE348139FF2F356C7253E21FCB01CDBG465BEFBF6E0DB7170DAF4A5BE00BB7247503F6A45B4E5B61D3481BD789C55A43BA67CD26FE85E5C8057E8C377CAB445D886D09A6AF299F75450B1D1F39F13C4C377142
		5618CF7D946E5D224142BE9F6D11477585EC5C6F2FF25C1B9DD7BA0E5D992BB57A9D0EA16C4E48B6719BA8FE0D8D4F7CEFFD07703F25C09BB2C44470DF52FBB48B5AF957FAF25DGD5GCDG463562FE7ED1E0BB69A347EAD45486881AC236901B49B7C63B3A4F59FF11AB7C5C641F5DCC268CFAA9BBD9ADEA48356C1CCF644F790DDBA81E49175901724D815A8200B5C3A1E6872885E89EAA647BC3201C4907E5335D06A1F98334A2A70443A2190546C00259725F99BD6312FB3A653BAA2CB196A0FDA863ED4ED0
		6C532FD37419BE3427EFE3F35F6EC3BDE00C57A035E0A91563CF98F8BF9C89F469C12F946255D65A05511FB9D6404719C924B936FA9346B47115F9C61BF4896F2A9E1FDC18E3E3DFA37DGDD6990FE06FEB8525EC263D4C65F5C06313F1539F66F436AC7F7C56DF71BFB982B9F558E9375257B7C0968CDA6BDD414089EB269572ECBD41F6AB7CC44850FFCE52FCF950E5B6490E7A4FE8FC68D635F9D9AE645859B291C0450BAAF7FE33EFC9906A95CEEFF1FAF6C36640E9F115A47DF4A5E12E40D59FC6E52D4EB0D
		B9FF9C61E47A7C3288FD16DF8A6932B97778281D7F2A253B1256571A791CAAFFCE9F3C05733A75BCB1776C1FBEFE8B1EE1B96AB6AB1EFB61B2A85B2CCBE1486F3420FFF339FBFB393BF267D66857D37E791F32B99BAED8F3DEBC85618C295CA1F4637C462285BE7A6B2C774EE5EB4E5CBA147FA3A27C69BAB4B77EDEEAFB7F3C633B4A0DADDF9E78CE57E03E6A341ABCBF15554479E91B1A38B65221767E79ED171A7C735BFE2A7D73DB66EBDF3C0C67EFA2585CF400CC00C5G0543051FDD5E3119D0BF1B404756
		79DAB1BF46EF7898E27D67BB124B3A30236F6F85E5EB78D9CA0BE298CABB4EAF703974EEB29415A299AEDFC39DE9415600413AF27CCA1A8304E4BFC533782272EC373A1D15336C11DAD95A427207F19191F3158F97B94B0C885E43BEE8DF8498B5DC54C0EF6B94350D746142DF4D785A9E6B3B5FF3A60C7597FE3D4D11B11C1D733C6BACDFB29DFC892F2D781354D628B7E76BBF9595753D6A6B84CFAF79854EB0F1379489DC3A091B36CE60B22E93F145C9E4136B70E68D4F7DF169DC42CF278EGCB1F371C133D
		C62354EA5A528F9C986B157CG629100E6G83E08840F80049GB3G6682ACG48CF035885E0A740FE00A60090C03FB4E1FBC70C2D49F8GA45FBCEA001A44EA160C0FBF6520EC23E01EC966DCB7986F75B25791CDD1E54D68E2D87A69AD5743EB17135B73E25237BD2F203ED086BBEC6CD2F96D2718501A2A5F2F4B6639D9CB8ACDDC3DFEC45103D8B3E2BE6AEFA3D797EFD653DA442C3478F8330E62672645471B8FC7C43C1989340691A23E3E23533E7EA440CF9F51FBFE4776E13D0B46F150F781401A119047
		815482B40F947BF287A96BE5DF58493436187173CC544E0FBA127112421FE3C776640D0DFB55EF9F174E7BCD634F59C947CDAC320F4B647D9671E7617549465D3F4EBE2EB42EDFD5DC3B61FA2113DB9C49ED6EF6C75FB6F737A3318F7DE7C7CF9FFA0FA3318F1D6E307BD07EBDB99A3B344239EBB27BBDEC62CE1A38C7691C08BB91749981937F84658B717BE5DC507C1E9C65ABCAF1CD22BEEB431D64387FF260398111830A793943F38E1A8F87C3A17EBD8D5AA79806E3E46A63C54AE3D015ECFCAFB2F1965FAF
		6760BE18F7ABF38362DEF2969376BD2620948A1E27DF89B8869D432DEC2F77F492F35F8F5A5EE7C73E7B9866596FF48DD6E73A0F5EF5E741C6317645EB069497839C5653181E5EF6A5375D77DDFD5B6EC72E4436DB6D6AE93B3BDD09EDF71F4BEE3B0CD7C7CC54C1C7DDAFEAE7B9CB696756501EE26282CBC5BDED3A09DB5DAA46960F923801D1B1364C44317B98476EB4F177523BB7855A23CCDC7F23F4AC34271838270F1A6B1A38396B4458625182F7FF3368D7E6625C9B68BEC0BBF5342851CAEF67DB76CA0F
		A14317448AAC3CB8063C36320D6AE8BC4C11BEDA541B5EDC5A3D8676D3BCDE3C86F6F9E9DFB530EEF526389A585C5156BB2FF7D1BDE782FE81GDBAFA357DA6FFF23FD2D676AE53D52B11EDC09586BDA7FDE543D1616C577EABB742B9EA3647F61516E7233B00F71736A513E78496D051F509876BBABCBFED7895DC740270C917533394D5FE68DA17714B15D6BE7B347445772F6ED68D94BBB3CC15454660C91FA3F85CBB7FC4C0BCBB087C810837A400614D6DB8248467EAB1A9EFF3F21481A27ABFAE5737CEC25
		B54F072B2DF9BA0FDE59BC7467887CFD7DDAEB1E97CE7EF5F25B8DD5561C7286115B325F50BDECBC86A7A67DD962B248B22C5238E93DC657B53242DC351EDED773BABF70647C61169C87F15613454C855693A77A31FA6222EE2E1E64008B0CE1C97C4FGEFF3C6DCB0DBC1B7996E7F7107CF4DFC72381EC396571335E86247C09E3C39E49FFE7A4BFF7E3017ACD1119D7CD6AEAC255307CBB73AC2F283A976FF01DFE9548D5937DB5C093AC1D6B760CA5B703BE05B76CBFA40C07F7DF183F10671079AE4A5A30724
		F06BB6F6C1FECC794D5EFD16720AE6CB005FD514DF1C25421CE5554FD73C3569C046149C6258CB1606944937D772C2B8D8CF169881CD5683CA4897D3BF7D0CCE14C73DAD1AE98ECC374E949F052D401A1649F4DB6CEFD28BB28B764245955348B65A93CFF4055943D79310B4D8CA566A8D7C610922151A34EBB698EFBF346F0DE6874966C2F610EF08AB269A4015861662D734F660409D4701A01993D5DC7F407651FF7F72A9BA11B554A09B194AE06836D6D9E95F365B6F8F06E5F382509FCC3F5E1C9E7BD44475
		D9999B317A40E32F3540FE57121C766D5B697FCA7AFF877E2F2418CA0A295C81CA1CCFFA7D0B2747F414FA09410C4434DE5C660A3D30D346EF1EFD60665FD64F4F82697692977DA297A83B506A458A0B0352C72FDFB6EA5EB05C8B95CDC24C3B58AF3587C3DD20D47374451034162D1C4DBD9A7E21DBF813E2F3FA9896E2F3FBDA882217D1E2817DDFF84572632815F4ECB1AE08AFEF74E056EDB11BE3E3964EFEBE7BB05A0DD5DE0FC172F8C597DE8BE859554D546379FE083518F9D2CAFD5712892F8E61F3CA3E
		FF1CD6F6B79541E96E770F53AA0B5345B6CE97FFBFB80DB36ACFADE6B1969ACE58BD086F2C663575323D463DF0601A645FA13D09FF077426786F10CE2531678C40E75CA472607C587BA4F98D6AD83B948A99D228EDF758077528BDDD06EC55C1FF914379F551079399EDC68C79BEE79B73FFD0CB8788283F59CDA896GGCCBFGGD0CB818294G94G88G88G64F2CEAD283F59CDA896GGCCBFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81G
		BAGGGE296GGGG
	**end of data**/
	}
	
	/**
	 * Return the JLabelMeterNumber property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelMeterNumber() {
		if (ivjJLabelMeterNumber == null) {
			try {
				ivjJLabelMeterNumber = new javax.swing.JLabel();
				ivjJLabelMeterNumber.setName("JLabelMeterNumber");
				ivjJLabelMeterNumber.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelMeterNumber.setText("Meter Number:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelMeterNumber;
	}
	
	
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelCopyDevice() {
		if (ivjJPanelCopyDevice == null) {
			try {
				ivjJPanelCopyDevice = new javax.swing.JPanel();
				ivjJPanelCopyDevice.setName("JPanelCopyDevice");
				ivjJPanelCopyDevice.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
				constraintsNameLabel.gridx = 1; constraintsNameLabel.gridy = 1;
				constraintsNameLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsNameLabel.ipadx = 17;
				constraintsNameLabel.insets = new java.awt.Insets(28, 38, 7, 0);
				getJPanelCopyDevice().add(getNameLabel(), constraintsNameLabel);
	
				java.awt.GridBagConstraints constraintsPhysicalAddressLabel = new java.awt.GridBagConstraints();
				constraintsPhysicalAddressLabel.gridx = 1; constraintsPhysicalAddressLabel.gridy = 2;
				constraintsPhysicalAddressLabel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsPhysicalAddressLabel.ipadx = 48;
				constraintsPhysicalAddressLabel.insets = new java.awt.Insets(7, 38, 5, 0);
				getJPanelCopyDevice().add(getPhysicalAddressLabel(), constraintsPhysicalAddressLabel);
	
				java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
				constraintsNameTextField.gridx = 2; constraintsNameTextField.gridy = 1;
				constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsNameTextField.anchor = java.awt.GridBagConstraints.WEST;
				constraintsNameTextField.weightx = 1.0;
				constraintsNameTextField.ipadx = 186;
				constraintsNameTextField.insets = new java.awt.Insets(26, 0, 5, 18);
				getJPanelCopyDevice().add(getNameTextField(), constraintsNameTextField);
	
				java.awt.GridBagConstraints constraintsAddressTextField = new java.awt.GridBagConstraints();
				constraintsAddressTextField.gridx = 2; constraintsAddressTextField.gridy = 2;
				constraintsAddressTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsAddressTextField.anchor = java.awt.GridBagConstraints.WEST;
				constraintsAddressTextField.weightx = 1.0;
				constraintsAddressTextField.ipadx = 186;
				constraintsAddressTextField.insets = new java.awt.Insets(5, 0, 3, 18);
				getJPanelCopyDevice().add(getAddressTextField(), constraintsAddressTextField);
	
				java.awt.GridBagConstraints constraintsPointCopyCheckBox = new java.awt.GridBagConstraints();
				constraintsPointCopyCheckBox.gridx = 1; constraintsPointCopyCheckBox.gridy = 4;
				constraintsPointCopyCheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsPointCopyCheckBox.ipadx = 12;
				constraintsPointCopyCheckBox.insets = new java.awt.Insets(5, 38, 57, 0);
				getJPanelCopyDevice().add(getPointCopyCheckBox(), constraintsPointCopyCheckBox);
	
				java.awt.GridBagConstraints constraintsJLabelMeterNumber = new java.awt.GridBagConstraints();
				constraintsJLabelMeterNumber.gridx = 1; constraintsJLabelMeterNumber.gridy = 3;
				constraintsJLabelMeterNumber.anchor = java.awt.GridBagConstraints.WEST;
				constraintsJLabelMeterNumber.ipadx = 11;
				constraintsJLabelMeterNumber.insets = new java.awt.Insets(6, 38, 6, 0);
				getJPanelCopyDevice().add(getJLabelMeterNumber(), constraintsJLabelMeterNumber);
	
				java.awt.GridBagConstraints constraintsJTextFieldMeterNumber = new java.awt.GridBagConstraints();
				constraintsJTextFieldMeterNumber.gridx = 1; constraintsJTextFieldMeterNumber.gridy = 3;
				constraintsJTextFieldMeterNumber.gridwidth = 2;
				constraintsJTextFieldMeterNumber.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextFieldMeterNumber.anchor = java.awt.GridBagConstraints.WEST;
				constraintsJTextFieldMeterNumber.weightx = 1.0;
				constraintsJTextFieldMeterNumber.ipadx = 186;
				constraintsJTextFieldMeterNumber.insets = new java.awt.Insets(4, 141, 4, 19);
				getJPanelCopyDevice().add(getJTextFieldMeterNumber(), constraintsJTextFieldMeterNumber);
				// user code begin {1}
				
	         java.awt.GridBagConstraints cpg = new java.awt.GridBagConstraints();
	         cpg.gridx = 1;
	         cpg.gridy = 5;
	         cpg.anchor = java.awt.GridBagConstraints.WEST;
	         cpg.fill = java.awt.GridBagConstraints.HORIZONTAL;
	         cpg.gridwidth = 2;
	         cpg.insets = new java.awt.Insets(10, 15, 10, 15);
	         getJPanelCopyDevice().add(getJLabelRange(), cpg);
				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJPanelCopyDevice;
	}
	
	/**
	 * Return the JTextFieldMeterNumber property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getJTextFieldMeterNumber() {
		if (ivjJTextFieldMeterNumber == null) {
			try {
				ivjJTextFieldMeterNumber = new javax.swing.JTextField();
				ivjJTextFieldMeterNumber.setName("JTextFieldMeterNumber");
				ivjJTextFieldMeterNumber.setFont(new java.awt.Font("sansserif", 0, 14));
				ivjJTextFieldMeterNumber.setColumns(6);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldMeterNumber;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	
	/**
	 * Return the NameLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getNameLabel() {
		if (ivjNameLabel == null) {
			try {
				ivjNameLabel = new javax.swing.JLabel();
				ivjNameLabel.setName("NameLabel");
				ivjNameLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjNameLabel.setText("Device Name:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNameLabel;
	}
	
	/**
	 * Return the NameTextField property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getNameTextField() {
		if (ivjNameTextField == null) {
			try {
				ivjNameTextField = new javax.swing.JTextField();
				ivjNameTextField.setName("NameTextField");
				ivjNameTextField.setFont(new java.awt.Font("sansserif", 0, 14));
				ivjNameTextField.setColumns(12);
				// user code begin {1}
				ivjNameTextField.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_DEVICE_NAME_LENGTH));
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjNameTextField;
	}
	
	
	/**
	 * Return the PhysicalAddressLabel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getPhysicalAddressLabel() {
		if (ivjPhysicalAddressLabel == null) {
			try {
				ivjPhysicalAddressLabel = new javax.swing.JLabel();
				ivjPhysicalAddressLabel.setName("PhysicalAddressLabel");
				ivjPhysicalAddressLabel.setFont(new java.awt.Font("dialog", 0, 14));
				ivjPhysicalAddressLabel.setText("Address:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPhysicalAddressLabel;
	}
	
	/**
	 * Return the PointCopyCheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getPointCopyCheckBox() {
		if (ivjPointCopyCheckBox == null) {
			try {
				ivjPointCopyCheckBox = new javax.swing.JCheckBox();
				ivjPointCopyCheckBox.setName("PointCopyCheckBox");
				ivjPointCopyCheckBox.setText("Copy Points");
				ivjPointCopyCheckBox.setEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjPointCopyCheckBox;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public Dimension getPreferredSize() {
		return new Dimension( 350, 200);
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.Object
	 * @param val java.lang.Object
	 * Note: Cap Bank pointID assignment should be handled by utilizing a CommonMulti
	 */
	public Object getValue(Object val)
	{
		DeviceBase device = ((DeviceBase) val);
		int previousDeviceID = device.getDevice().getDeviceID().intValue();
	
		device.setDeviceID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
	
		boolean hasRoute = false;
		boolean hasPoints = false;
		boolean isCapBank = false;
	
		String nameString = getNameTextField().getText();
		device.setPAOName(nameString);
	
		com.cannontech.database.data.multi.MultiDBPersistent objectsToAdd = new com.cannontech.database.data.multi.MultiDBPersistent();
		objectsToAdd.getDBPersistentVector().add(device);
	
		//Search for the correct sub-type and set the address
		if( getAddressTextField().isVisible() )
		{
			if (val instanceof IDLCBase)
				((IDLCBase) val).getDeviceIDLCRemote().setAddress(new Integer(getAddressTextField().getText()));
			else if (val instanceof DNPBase)
				((DNPBase) val).getDeviceDNP().setMasterAddress(new Integer(getAddressTextField().getText()));
			else if (val instanceof CarrierBase)
				 {
				 	 Integer addressHolder = new Integer(getAddressTextField().getText());
				 	 if(val instanceof Repeater900)
				 	 	addressHolder = new Integer(addressHolder.intValue() + 4190000);
					 ((CarrierBase) val).getDeviceCarrierSettings().setAddress(addressHolder);
				 }
			else if (val instanceof CapBank)
			{
				 ((CapBank) val).setLocation(getAddressTextField().getText());
				 isCapBank = true;
			}
			else if (val instanceof ICapBankController )
				 ((ICapBankController) val).assignAddress( new Integer(getAddressTextField().getText()) );
			else if (val instanceof Ion7700)
				 ((Ion7700) val).getDeviceDNP().setSlaveAddress( new Integer(getAddressTextField().getText()) );
			/*else if (val instanceof RTCBase)
				((RTCBase) val).getDeviceRTC().setRTCAddress( new Integer( getAddressTextField().getText()));*/
			else //didn't find it
				throw new Error("Unable to determine device type when attempting to set the address");
		}
	
		//Search for the correct sub-type and set the meter fields
		if( getJTextFieldMeterNumber().isVisible() )
		{
			if( val instanceof MCTBase )
				 ((MCTBase ) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
			else if( val instanceof IEDMeter )
				 ((IEDMeter) val).getDeviceMeterGroup().setMeterNumber( getJTextFieldMeterNumber().getText() );
			else //didn't find it
				throw new Error("Unable to determine device type when attempting to set the Meter Number");
		}
	
		
		if (com.cannontech.database.data.pao.DeviceClasses.getClass(device.getPAOClass()) == com.cannontech.database.data.pao.DeviceClasses.TRANSMITTER)
		{
			com.cannontech.database.cache.DefaultDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized (cache)
		{
				java.util.List routes = cache.getAllRoutes();
				com.cannontech.database.data.route.RouteBase newRoute = null;
				DBPersistent oldRoute = null;
				Integer routeID = null;
				String type = null;
	
				for (int i = 0; i < routes.size(); i++)
				{
					oldRoute = com.cannontech.database.data.lite.LiteFactory.createDBPersistent(((com.cannontech.database.data.lite.LiteYukonPAObject) routes.get(i)));
					try
					{
						com.cannontech.database.Transaction t =
							com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, oldRoute);
						t.execute();
					}
					catch (Exception e)
					{
						com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
					}
					
					if (oldRoute instanceof com.cannontech.database.data.route.RouteBase)
					{
						if (((com.cannontech.database.data.route.RouteBase) oldRoute).getDeviceID().intValue()
							== previousDeviceID)
						{
							type = com.cannontech.database.data.pao.PAOGroups.getPAOTypeString( ((com.cannontech.database.data.lite.LiteYukonPAObject) routes.get(i)).getType() );
							newRoute = com.cannontech.database.data.route.RouteFactory.createRoute(type);
	
							routeID = com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID();
							hasRoute = true;
							break;
	
						}
					}
				}
				
				if (hasRoute) 
				{
					((com.cannontech.database.data.route.RouteBase) newRoute).setRouteID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
					((com.cannontech.database.data.route.RouteBase) newRoute).setRouteType( type );
					((com.cannontech.database.data.route.RouteBase) newRoute).setRouteName(nameString);
					((com.cannontech.database.data.route.RouteBase) newRoute).setDeviceID(
						((com.cannontech.database.data.route.RouteBase) oldRoute).getDeviceID() );
					((com.cannontech.database.data.route.RouteBase) newRoute).setDefaultRoute(
						((com.cannontech.database.data.route.RouteBase) oldRoute).getDefaultRoute() );
	
					if( type.equalsIgnoreCase(com.cannontech.database.data.pao.RouteTypes.STRING_CCU) )
					{
						((com.cannontech.database.data.route.CCURoute) newRoute).setCarrierRoute(((com.cannontech.database.data.route.CCURoute) oldRoute).getCarrierRoute());
						((com.cannontech.database.data.route.CCURoute) newRoute).getCarrierRoute().setRouteID(routeID);
					}
	
					//put the route in the beginning of our Vector
					objectsToAdd.getDBPersistentVector().insertElementAt( newRoute, 0 );
				}
			}
	
		}
	
		if (getPointCopyCheckBox().isSelected())
		{
			java.util.Vector devicePoints = null;
			com.cannontech.database.cache.DefaultDatabaseCache cache =
				com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized (cache)
		{
				java.util.List allPoints = cache.getAllPoints();
				devicePoints = new java.util.Vector();
	
				com.cannontech.database.data.point.PointBase pointBase = null;
				com.cannontech.database.data.lite.LitePoint litePoint = null;
	
				for (int i = 0; i < allPoints.size(); i++)
				{
					litePoint = (com.cannontech.database.data.lite.LitePoint) allPoints.get(i);
					if (litePoint.getPaobjectID() == previousDeviceID)
					{
						pointBase = (com.cannontech.database.data.point.PointBase) com.cannontech.database.data.lite.LiteFactory.createDBPersistent(litePoint);
						try
						{
							com.cannontech.database.Transaction t =
								com.cannontech.database.Transaction.createTransaction(com.cannontech.database.Transaction.RETRIEVE, pointBase);
							t.execute();
							devicePoints.addElement(pointBase);
						}
						catch (com.cannontech.database.TransactionException e)
						{
							com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
						}
					}
				}
	
				java.util.Collections.sort(allPoints, com.cannontech.database.data.lite.LiteComparators.litePointIDComparator);
				int startingPointID = ((com.cannontech.database.data.lite.LitePoint) allPoints.get(allPoints.size() - 1)).getPointID() + 10;
				Integer newDeviceID = device.getDevice().getDeviceID();
	
				for (int i = 0; i < devicePoints.size(); i++)
				{
					((com.cannontech.database.data.point.PointBase) devicePoints.get(i)).setPointID(new Integer(startingPointID + i));
					((com.cannontech.database.data.point.PointBase) devicePoints.get(i)).getPoint().setPaoID(newDeviceID);
					objectsToAdd.getDBPersistentVector().add(devicePoints.get(i));
				}
				hasPoints = true;
			}
	
		}
		if (hasPoints || hasRoute || isCapBank)
		{
			return objectsToAdd;
		}
		else
			return val;
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
	 * Initializes connections
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initConnections() throws java.lang.Exception {
		// user code begin {1}
		// user code end
		getPointCopyCheckBox().addItemListener(this);
		getNameTextField().addCaretListener(this);
		getAddressTextField().addCaretListener(this);
	}
	
	
	private javax.swing.JLabel getJLabelRange()
	{
	   if( jLabelRange == null )
	   {
	      jLabelRange = new javax.swing.JLabel();
	      jLabelRange.setFont(new java.awt.Font("dialog.bold", 1, 10));
	      
	      jLabelRange.setMaximumSize(new java.awt.Dimension(250, 20));
	      jLabelRange.setPreferredSize(new java.awt.Dimension(220, 20));
	      jLabelRange.setMinimumSize(new java.awt.Dimension(220, 20));            
	   }
	   
	   return jLabelRange;
	}
	
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("DeviceNameAddressPanel");
			setLayout(new java.awt.GridLayout());
			setSize(350, 200);
			add(getJPanelCopyDevice(), getJPanelCopyDevice().getName());
			initConnections();
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
	public boolean isInputValid() {
		if( getNameTextField().getText() == null   ||
				getNameTextField().getText().length() < 1 )
		{
			setErrorString("The Name text field must be filled in");
			return false;
		}
	
	   if( getAddressTextField().isVisible() )
	   {
	   	if( getAddressTextField().getText() == null
	          || getAddressTextField().getText().length() < 1 )
	   	{
	   		setErrorString("The Address text field must be filled in");
	   		return false;
	   	}
	   
	   
	   	try {
		      long addy = Long.parseLong(getAddressTextField().getText());
		      if( !com.cannontech.device.range.DeviceAddressRange.isValidRange( getDeviceType(), addy ) )
		      {
		         setErrorString( com.cannontech.device.range.DeviceAddressRange.getRangeMessage( getDeviceType() ) );
		
		         getJLabelRange().setText( "(" + getErrorString() + ")" );
		         getJLabelRange().setToolTipText( "(" + getErrorString() + ")" );
		         return false;
		      }
		      else
		         getJLabelRange().setText( "" );
	   	}
	   	catch( NumberFormatException e )
	   	{} //if this happens, we assume they know what they are 
	   	   // doing and we accept any string as input	      
	      
	   }
	
		return true;
	}
	
	
	/**
	 * Method to handle events for the ItemListener interface.
	 * @param e java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public void itemStateChanged(java.awt.event.ItemEvent e) {
		// user code begin {1}
		// user code end
		if (e.getSource() == getPointCopyCheckBox()) 
			connEtoC3(e);
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
			DeviceCopyNameAddressPanel aDeviceCopyNameAddressPanel;
			aDeviceCopyNameAddressPanel = new DeviceCopyNameAddressPanel();
			frame.getContentPane().add("Center", aDeviceCopyNameAddressPanel);
			frame.setSize(aDeviceCopyNameAddressPanel.getSize());
			frame.setVisible(true);
		} catch (Throwable exception) {
			System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
			com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
		}
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @param val java.lang.Object
	 */
	public void setValue(Object val ) 
	{
		int deviceClass = -1;
		
		if( val instanceof DeviceBase )
	   {
			deviceClass = com.cannontech.database.data.pao.DeviceClasses.getClass( ((DeviceBase)val).getPAOClass() );
	      setDeviceType( com.cannontech.database.data.pao.PAOGroups.getDeviceType( ((DeviceBase)val).getPAOType() ) );
	   }

	
		//handle all device Address fields
		boolean showAddress = 
				(val instanceof IEDBase)
				 //|| (val instanceof ICapBankController)
				 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.GROUP)
				 || (deviceClass == com.cannontech.database.data.pao.DeviceClasses.VIRTUAL);
	
		getAddressTextField().setVisible( !showAddress );
		getPhysicalAddressLabel().setVisible( !showAddress );
	
		
		//handle all meter fields
		boolean showMeterNumber = (val instanceof MCTBase) || (val instanceof IEDMeter);
		getJTextFieldMeterNumber().setVisible( showMeterNumber );
		getJLabelMeterNumber().setVisible( showMeterNumber );
	
	
		setPanelState( (com.cannontech.database.data.device.DeviceBase)val );
	
	
		int deviceDeviceID = ((com.cannontech.database.data.device.DeviceBase)val).getDevice().getDeviceID().intValue();
	
		
		com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
		synchronized(cache)
		{
			java.util.List allPoints = cache.getAllPoints();
			for(int i=0;i<allPoints.size();i++)
			{
				if( ((com.cannontech.database.data.lite.LitePoint)allPoints.get(i)).getPaobjectID() == deviceDeviceID )
				{
					getPointCopyCheckBox().setEnabled(true);
					getPointCopyCheckBox().setSelected(true);
					break;
				}
			}
		}
	}


   private void setPanelState( com.cannontech.database.data.device.DeviceBase val )
   {

      Integer addressHolder;
      
      if( val instanceof CarrierBase )
         {
          	addressHolder = new Integer(((CarrierBase)val).getDeviceCarrierSettings().getAddress().toString());
            if(val instanceof Repeater900 )
         		addressHolder = new Integer(addressHolder.intValue() - 4190000);
          	getAddressTextField().setText( addressHolder.toString() );
         }
      if( val instanceof IDLCBase )
         getAddressTextField().setText( ((IDLCBase)val).getDeviceIDLCRemote().getAddress().toString() );
   
   	  if( val instanceof DNPBase )
   	  	 getAddressTextField().setText( ((DNPBase)val).getDeviceDNP().getMasterAddress().toString() );
   
      if( val instanceof MCTBase )
         getJTextFieldMeterNumber().setText( ((MCTBase)val).getDeviceMeterGroup().getMeterNumber().toString() );
   
      if( val instanceof IEDMeter )
         getJTextFieldMeterNumber().setText( ((IEDMeter)val).getDeviceMeterGroup().getMeterNumber().toString() );

      if( val instanceof Ion7700 )
      {
         getPhysicalAddressLabel().setText("Slave Address:");
         
         getAddressTextField().setText( 
            ((Ion7700)val).getDeviceDNP().getSlaveAddress().toString() );            
      }
      
      /*if( val instanceof RTCBase )
      {
      	 getAddressTextField().setText( ((RTCBase)val).getDeviceRTC().getRTCAddress().toString());
      }*/
   
      if( val instanceof ICapBankController )
      {
         getPhysicalAddressLabel().setText(
            (val instanceof CapBankController ? "Serial Number:"
             : (val instanceof CapBankController6510 ? "Master Address:" : "Address:")) );
   
         getAddressTextField().setText( 
            ((ICapBankController)val).copiableAddress().toString() );            
      }

      if( val instanceof CapBank )
      {
         getPhysicalAddressLabel().setText( "Location:" );

         getAddressTextField().setText( 
            ((CapBank)val).getPAODescription().toString() );            
      }


      getNameTextField().setText( val.getPAOName() );      
   }
   private void setDeviceType( int devType_ )
   {
      deviceType = devType_;
   }
   
   private int getDeviceType()
   {
      return deviceType;
   }
}
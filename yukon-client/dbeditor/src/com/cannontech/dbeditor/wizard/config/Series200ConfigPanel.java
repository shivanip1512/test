package com.cannontech.dbeditor.wizard.config;

import java.awt.Dimension;
import com.cannontech.database.data.config.ConfigTwoWay;

/**
 * This type was created in VisualAge.
 */

public class Series200ConfigPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjNameLabel = null;
	private javax.swing.JTextField ivjNameTextField = null;
	private javax.swing.JRadioButton ivjKY2WireButton = null;
	private javax.swing.JRadioButton ivjKYZ3WireButton = null;
	private javax.swing.JLabel ivjMpLabel = null;
	private javax.swing.JPanel ivjMultiplierPanel = null;
	private javax.swing.JLabel ivjTimesLabel = null;
	private javax.swing.JLabel ivjEqualsLabel = null;
	private javax.swing.JLabel ivjKeLabel = null;
	private javax.swing.JTextField ivjKeTextField = null;
	private javax.swing.JLabel ivjKhLabel = null;
	private javax.swing.JTextField ivjKhTextField = null;
	private javax.swing.JTextField ivjMpTextField = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	javax.swing.ButtonGroup channel1ButtonGroup = new javax.swing.ButtonGroup();

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			fireInputUpdate();
			if (e.getSource() == Series200ConfigPanel.this.getKYZ3WireButton()) 
				connEtoC2(e);
			if (e.getSource() == Series200ConfigPanel.this.getKY2WireButton()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			fireInputUpdate();
			if (e.getSource() == Series200ConfigPanel.this.getNameTextField()) 
				connEtoC1(e);
			if (e.getSource() == Series200ConfigPanel.this.getKeTextField()) 
				connEtoC4(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public Series200ConfigPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void actionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * connEtoC1:  (NameTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series200ConfigPanel.nameTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.nameTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (KYZ3WireButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series200ConfigPanel.kYZ3WireButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kYZ3WireButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (KY2WireButton.action.actionPerformed(java.awt.event.ActionEvent) --> Series200ConfigPanel.kY2WireButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.kY2WireButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (KeTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> Series200ConfigPanel.keTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.keTextField_CaretUpdate(arg1);
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
	D0CB838494G88G88GD6E213AFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BC8DD8D45715692ECD6D56EE530676B32DEDDDCD3BA6A10DCDED6257357B59546E279B3BF13FB5DF4DA6ED59AD5D3ACD42E69B3BF5376C76B2007C88B27CA82828A8202008A0A8087C03484F98D0D0C9EAAB0F19870C8EB3631B07E0C213BD677E3DB7439BD05B70FD477B6E39771EFB6E39671EF36EF9EFA4B98796A74659F28809CBA47C2FA60E103CF6C2BE257CEA0B40F51218D4E27ABB8810C9A2
	4FA5C0B988ED735BE2D26D247A7DE428FB206EE80CC94D0567EC723D4A0D8D7090E61EC8E80B784E0F155967795AF9B1CF922D9FEBC803F287009FF091GD18467EFEB48637815D05F411F9162EC04AC0E43BE8335F9A6FCD19C6B27C3B99F60A19B7679FDC3BEEF5F8CB882B88740100D751D02B205F277513ADA5E6F5A271711ADFF3F57160EF319688B58444746723200AC250208A74FFD508AE5FD5F4D51C627EF3C5C29F83DBE2F2EBA474ADD432A4B2D7B3472C9773B0A66AAF729375DCE352E2E1D16EFA9
	63EA0B4B252901C01F62D5BDA077F8647D5D5AE4CA1710EA28EF2538DF54223C164373DA0028F8417FBF2657F37EA2EEAEA3D1CBF704653FBE0171BD14E070BF705E030976C4247FB13A9FB8EEB134EF82E0EBAB6CEE3E4B35B9FD5E9177E8DDDD3F2A395540490A0AB60A20ABE36BFA21C12CEBA0D12C6BA7D5B86F8228BF8A30CA629F2FC67CC6283F861030CD2C376108D86F60FB4F122D430D360CB06B4D5F4656593E4DD86F22272F5BF6D37D2B6BC29DFAG5A9E85D889708AC0ACC0FED2CC6ADE2A773FBC
	FF944A16E13D4DB7BE6E7336787D2D9E17F64167D2BD70DCD787AD9CF7C9F57AB497A1CC37BF28AD3360C7C0F5D230DEA33035DDA5C297A3AFFD0D68FA35AD51E2EDABDE5CE76B64B42EA599EBDB1804F29BB903F21B0667B8CE176183947F0A894F76FDAB55AB2CEF0536EB49C24E03BB84AF0E04A544DE59ECCB3260E561F327EC2B13998F1B128DDE180CF3FAD04689A9B12985G35GFDGFE0007D2040C1BBA2B6612F197681B66D5BC7D9343FEC553DBA75C9E172AF1D9FF32E1B6D9DB819B7705FB9E4764
	322C4A3CC72FFC2349D2AE2FACAB32C526B0F92CC9B16442665E98F66E6814193A31C36E61D5FAA6362630BD0C17F8056213D38DBC5B5BFF27E78B6BF950B68D407A6FBF05782854D09DB9C671312921BA323CDA68C85EF62083407677E9875A58A8284782642445241681B8G46G2653449E7FC567093BBCC7FD267DE5E35B3AF6C129CE691A52658D680A5729DED1B437B26CD1830477F1F421DC070346DD24675B89F31CD73D2A2668EE1F9728034585E58A30BE6FF4E5E29FCD252D836EF195A6987793C1F7
	38AB9BCA0F9250AF7BDD0A2E9ABDD87B0DAE1449ED658AD08482703CBE8D6575B9EA3F9741734AB4A12B9FDFC01CBDBDA635A4DD486527DDD9D07600F22B9E7089EEA73221E8F7040EFA4E58212C077E51G8BF64424D6E4087399B3A87430BD7DC9D276E223259EA2EC48E0BA35B9A3D48F6FD07AF6BB50856883705B05FF7E3F5AC1A8451CF31D8D41633D1D415CB3037CB965AE46209EA75E43F9E5F23F500B3A7B902CAB92E03DDD483E203B0069E42B92D0059676EA20C02A4B588956F7DF8F7A9875366A55
	5BD53F6AF541C3006BC0D1CF0A211FA3BE29BBC7A99E6DD59BA8D0C062ABFAB2999EE81835DD345752F6C557B57770042E762B3A6E760E9A631BA9AF01A0D39860EB6D6D4335C6439AE38152B245DA5775E41B744C61735D1C708F6089627A7AAA754B85503FB6D36C7F6F2AB1DE3BA66B2334EE347FC755E0083C316F3D58533975A339BAF40E78BF91FDE30E22D3F613EE6D66F856D6C95B26E1DD0F822C8758A2577F0C64CD0CCD3ACB1D3CFB5EDE18B1475D1C133B61E3075427D50E703A7DDD0735EE2FF3D8
	6B768B0E59F577878EAB5DE5EDBFF2607AFC13DE53D97809836DA34415DE159EC8B9043058B4E6G5761763A752BEE57282A773B5FD5419EE621CEBFC9EDF0BEBCD7E4891BB9D9A8ECE6513B0F115557F7DB46D78B3FD2EB1B1F45F4F9D116E1B399CF5197700C299EF59C8455661B706A8C7FF3EA5FB9BE90CFF15F4CD904E3CBFA5EFE276A2ADBD3DBA7F45D67ADD368E24AD95127EAA3BEEDDCF5155CD0EEAB654A24DECEB7233C05B61F4367520263479B8B2C626A321BC1242F370450F404A7FA3674CAE9A5
	637323051F67FC1E7C5058BCF97FDC1EE47A8F7EF274F314CF3CB78D00DAF638D50F2B4C8936D4AF277FB2B7CDD91CAA8FCC0272F10AED58BA9713DEB3596BEDA6020E59A8D2C1CA9EBF5B74E9A64B8F03431B774BDF95917E704FAE227E2E823D5EG30B9CB7808113730DD78886E885BFC9E8FBBFA50664826676ACC0E692C10ECF6F75FA15BDF1A23BD31EE76764A306D0C4F7EDE3449817556048A21A0BA319E750E0867FA33C25A2EA81E89A3CE9C6E5D8E2547647F987EF21F75197EED9F7A3C1B127E753E
	ED8463BDC6135920658A5274EB6EF188975021739C052D511C230800F5E0BEE3A3141B81182E2C6D4535321D2E8A5A69B5B9C8C3BDA5F298846AF9G35B90C4ED48E3FDB50FE2527C5BF7BCE08AFF79A7DBAF71A7B7DD076FB88704BG566EE47D92F6197BAD2E917D9C00F76DB27A4D637D566E42FEEBE41E6204443DCCF1AF03937AF80238B3DD301BF3AC2EC025255DC5C84B6576B6B774AE796CA5245B8C783EDC2133AF7519631A16C04067F453D01E6F6B7C0BE61B4CF01F3C08492991379600FB25CB1D92
	3A7170C56495E3FD0E667B7B44B1CCFD055EDD4E514C07889B5BA669C19B703541F52393542BCD3A2CABEEF51288B97C21431C2BD915F15836BD0C9CDCFC7D73CCF298FA7A3A0D7174C89D1EE72B1C0C13B2D9BEBA61AE1F505DF829D0F4705D7E891DA518D89E4653184C5737A6D764DB1636503B9F54F372609E87406EFEFFC96F0458379DF053G6C1C26E71A4F698A40C781A46C963A7D4BD3E092493D7B90A177B5D7D06EFF14FB13F2DF125D4C79DC7F35A5247E03A44B1C517C0CCCDB5EEEA66F665D065C
	4BFEF541568C4064F169341047826833E63710C753D9ABF9446E96F934E70E5553755C3F9FE473EF2B91735B7740795EA366BFD2ED1E7F01BDAC16DF36C76C47F88D1E51FB74C4FCAF2AF36B203CFF77A872886BA46F2B002FAD12770F7406720E32AB5BABE4770D62DAEA076FEFF61EDBBBE936C12B230A435A20663DEC6F8776065A20AF8C60397183FE5EDEE103F6DC4AB45920CBF06A956FA8F89699DFDF0A273C5F61755517F0DE6AC30870491BAE637A97824D95G6B8122819272459C677B674C111D1B1A
	A5C7F62BB1B45FD414EF5C55EA73E75E5BD8BEA83795774B114F72C1C992DFC871AE931E6D6FE35406E0FD9A5A227245DDA962D4687C6D7B02679C8A2933F1CBE60C5B9FD21F97B20E694B2E567D42BE1AF4232E2E7B1262F27B5861936F92528B4DFEFA51BEF6EED6C3399100594B764CE44E53EB7BC4BE277CAA7A48227DB1294DG03GBA40C20095G1B760B3D7BDC3F157F885E3BF04E43488DB0F98C1E3EBFB90E4C9897AA472D7B034731F55FBEAB7C047D00584B341A7B63E14F0CF121BCF49E305A4B62
	12FC6B3D144600717A7BEA31C7C31257976E13579F4E496B4283D672DA59AB62216587041EF8879137996ADB8F881DF8339F5F51881B5167F3FBF5A26C4562810CB1FD1AFB546D95382FD01C5327514BB143C5DD8EFFEF7F59E56BFB7B6B172D6F6DEFDE1EBDA7756B4B616E75ACFFB41F46934985B12979G8C67EF26719A54878B44FE6C23F6E39A6A91G4BA53E14625797301894EDBF43BF55921A7F41F121364A8C53F334A3AC67FD589CEBE74CB1D70E71EEF2D0A287330F76DF37AB034AB790D0837430E3
	D4465BFE266FA496B98E3C6292F127FC7ED48A6FF3CE77351D14E7634D52BC6B33E1FA67F62ED01C61578AC42C17E5C7F9979D0CC92D8798B8A8645D1509FA6FB998AC4387F8BD6260CC59721C6555703A58F455DA974FDD45B39070CDE8CEB5D45F9CD773E41BE18D3BF4F59C45426FECF43C2EE8236A8CFD3DC6473336F063552B61F41A497CD3274DB22F127A725ED92BF7B74B38ECD6D9480851EB2BB1532B14741C55E6FA9BC246C70754379E94715C36DAB33DEAC96F5B3D333D57CABB94EE8F8303684B27
	E4FD12566FC87E3F5C08B648235C71CD0031DAF088756899F947AD00FAAD409000FE0879D02623DF1F44BB4E08260C03F87B7D2A130C9552774D4DE26CC6683F99A09970A5G1D05E20D0F14639AF14F9C4AB03E9E673CFC3DBC1D63259310ED7FC9E37B8B939E5D6D7738D50D5DFBD81CF92D559CE7AEAFE4FCEEAC947A5FDD98AA3B4D05063CE28B4345C95F226749DE4462211D927FFD0AEFB761D91C14C771D81F023622E2E1B307E81C31816A5192779845ED05FA72E1015B11A5ECEB0E44FD662C68671238
	B74F0AFEFE09FB177A4955D05FA0F18BEF0BFE51927774A144D59F017DB8A2F09BFBC53FEBC7447D70C70D623B01C840C59F917BDEB994643771D6D3D7F7DEF33BDA15D18755A2E61F8EB7A5737179A542BE8DB4A14D8128FBG6615087C6D54E1F1FF0E7C6A9262F8204972DB0728014336C5A5EC2FD61798B17CF238BF33B91FEC96F346C2FBF5A94DDBB41BF583F569F99BA165A8835A1E5EE2EEC75BD1C84C6DCDC16D486F3703465F8AEAC7996D6363595A1EAF96EB0BFDFEB971FC73F8586FB826CB591A96
	73F26A05BDA4624937F8EE604D11B0BE019EA1367E07DBC52C3429D46C2DC27D047DA86863D1016BB8AC763B7328B8A32B6DA5285FD0DF813081E0B3GB996131A87D086508EB08860BFA6467C939D0379AB4CCD4D837C2347D8DEE98D1451G89E5B0AFC0FB1998335F3E98CA1CC31E3523F37BF6B3A0EF480FD59B725C4E5B86CDFD72CC4F7EB0E39148B1F38C51C5F9B5478D777A1E8A69E404E9CB617A064F59A6FC3A69F967AC34371BF8823BCE6DC9C0453C2E6A524BE80EA7D3442D2EB2E1FBD7E7E26ED3
	877B3A328C3F67FA71BAAEBB7FB876AF4EEA1FE53EF88EA93C0C057188BB39BE5B01C6A9FFE634539503F6EA7DEC3437F31AC9E1E83B00C683FFE66B783BD6DC47084F2B3335AE25764F8F7D2681581CCF5D1EEBBDF1C444DC60B70E9BF3529C9A547F90E71EF35DA1644F6973CC0CFB83044D9BC56DA95E89B648F9DF6D1DED5E8C531C88D1A173EE017A6FB8EFCC8F23E75C736FC66FB249AF6EF35C3126FB9D477CE2E5B35AF7D24E7CE21C4457D1FC1A894FFC4CBBCD424764C15BF43938DB3D21202D0B02FA
	ACC04E89B86FG8E003193622E95BBD8C16638FF8F787C8E88DEBD262B8FDB5F534D77770D50F71204ADDF712D47494A87DF0F8B17671DBE415FB372527E8FFBA81E2D6F4F1D383E75503699A02D82ECA4C0ADC0DF05D8DF7B5BE5ECFD1812EE51F545B9C6335DE2F11800B65098003135ADECB52F4DD541FD89AF97D4040BE3278AE746011F12B9144595AC8EDC89657A8AF617E33A7E3B4333693AB8BB62DCC743582D95CC57311ED6E968846363E2B60E8BFE33AA5AEEE4537B35B957AA47F50C61DEDC709B11
	A343BFB00C781E31D07CAFEED13C9A0A7F12A70952E111A943BD76BE3EDB20DF8971001561ED03C9143639EF49BE24FB6E560462891871FD1534AF0FF8D95CDA52ED159F4D2B947953CA4DB2FFAA5B8F6406463D585EAC5B2F1D36EA9F106D3EAC2B76B1597EEA07D57B34ECBF16E3557ED02570B5DFE13EC61BD05979D9BCE94E5556269E8B1B2BFD34126969CADEC6436D0551F86137E69A764F6F3445052171869F9BDBE9440A87887335494478E33E6A7BF8AD67F37CCB1AB86779CFACA56DBFF8BD2EB04C9C
	95A7996D87CE9AF3CC3F7FE0A2237971D1037EBBF84D613A70095B45D0FA55498279E220839CD9492100628D84D44DBDF2282C20222C20329A7D69CC1E5FE86274478D1A3FBA84E1A9417755617BC762F161B63E2659F8EEB6BD8FB50566CD7E14403FCF7BD067E076733F1570398BB2EC1D3B58B6EC1DC7CB9B1E7D7B171C61705F3F6C9DB6FF7F426C7F348F4F4DCA50190DGDB81522AE2D28B2A046DFFEC789021365F426E1FF30DE26E843F1854457C8B1C61577A30F36E6FAC97B9513E8D7B40200EA3FD01
	DFC2FD0A6E73D3A4437D3593E52439C747F45615631FF4525F9728A3944D6E99CF350ABBE9FD1538FB3450F737FDD09F12382FDE947791CF1538FFBEDBA8E203D5005BD2A5F2763FA994F73C32EAF12FFC38183E37007AB4C08400559988FFAEB2EEE10D513F5EAD6ED01155E24EB1CD6022A56ECA2E483F651F923807CF8B5C0044FDB5CB6096C85C217372BD2D448D6788DC6CA991872D3AC56F6A27A1AEB8ADF42351F58C4AAB4A0D19EF2C815927797C2A265FE1D87A15845739E7D17D78DE4DF3950723819A
	568DB629F778865CA38614D129337F9AC0BF6F87BE96GAC87D88B708A409600649A0887GAAGBA8106GF40079G0BGA281D683EC2C916B489AA91DE59DBC21C095DDD34CF974F93E703A7E895F5C3A9E61235F8E000E8E5C71731C61A3BE1A1B9F99892872CCFD49E717382EC01FAF6046DCA30B79DF21FA3D99563225C6783A410B41772A3F1DC0F93398695B97670A11C47CE3F577A92AB5E2A41E8B1C114F3ABB3B7B7CFD0E3BD1FCFF77012443F8AEABEAC36F83F60A2F2F8D3D8FBCD2AC4EFCA734C556
	0A1C012B5BEAFE7B9966F30B202CBDE3753EE7DC339A6760FD072054AD477DB657EA5CFC5EF7511919B1B40F3DE73C0FC3D84E7B2F89BB6E09AC2BF11BF87F57420EBBF25EEA9C29E37DF36A420D9B4D319AD7427B57DB0CE3E762BB3770D97AC1E6DEF85B5ADB6853701BCD3762F13FCBBF5769070E5ED1567EDF01706776FF83F31F5BED3A350FCA53E77A28CC5D5AC7656AE69F45F079BA5A5CD130B703325FC109FBDB628ED31A083B6AF669E392DFC679C2FC270A938A7CC94A57A4458DF2DC0D8977B64755
	6BE8FBE049E3BE798E30C527398D3747435F2950BEEEAF18A2A90FEE4A235B2F18786E15B8641B592134AED489E623CEDD443842A26EF4411E79C6CDC1A79B6B6BA6D2E7528F9B4F3BCC4FDFAE7E706345159F6A9CAC86F90F7A7DBE507D21BA91173C5AAD639209FB117A64D5D05FA0F173B4B1B6DA62FE2A0931EFC85C51DEB136792C40E9B40E98003ACB62FE10AB737B927754E951EF354459EBC43F7592779C0DB7726B611ED8AFF0C732C53FDA09EBBCAF684D1738AF1F977DA2A46E7D9C51AFDA62FE1EA3
	57D6AF7C5F973B037D5FE7FAD0C7187F33754C667F92C9F0BE2C39A1B89FB6G75DEE24E913CF68B75C2752201F071BCDC3CA66242A1684FF878264360A1D61BAB7FE866C18F61E13E0987B6DFFF2F18AF22C14860633941B258F44118BFA6F72EDC24F97E5098E0994CA13EEDE334FF7D16C17BC84DEC341366203D82E80BBC96DB47CBD9416B585FE74C5518BD5BDCA9F34C353A41F8E634DBAE9A341FB9BF9B6D74B9E82FEFE03FEBB6E83BAE993463F67EB134B781ED715EA67CBD62E37DC44AF00F0674C694
	CFC01DF54C638346189B3D66B1DC07AE93E34F2F984F7B870D6716370DE757BB784CFE5FFD206D84900CFC1DA46AA42D6AB8E64D680F96CAF5327D242C1686B412A19DC29775B2D1372B6A62FF32B45EC6923A60DACC3DEE97C9C05759C59229E76DA27160F52770D7F528AD74ADA8C0C66096F91F58B8962105E2B5129E64F43E77D7277EED7D0F2E84624936AE329B7378783DB4068E2A8BBF23603F3D6BA0A9FE7C0E86FF82828C84E81198506FF8D487499939045F0487F45555A2021200CEF652AF87CF6007
	32A7BA1540180EF6622C03A460170EBA49E24D9E45BBFA02C5A8E769AA62DA06684E50988A784D267CA215F2C9A57BEFB20A8FF6C72F0CA7368E126A71A92E8E45897716AE12220FE9EAE04C67F1CD791B1AA92164C95CEE7AEA83486D154B474516E35ED1B2FDF36A0B8B52B727F7005826B4F202764463D7AEFB385A46948D26D235AE9DFF7568530AE45BC7766B073FFFBE771A0D4471C56296A4CD79F560CAD33C817C8C95B8A88861C0B4C9C2658F2F2B786CE37F738EA5E48C554981A6B298FAE2D4750D1F
	E89999F1FBD5C9G64876477C9725827BC24CF565427F7F63F781BE1586FF692BFDED1C17F2D247F1E44FFABA92612E2AACF02905FA0337E0536CFD1455B2EB3A511BA0D5BDC5E81BB257F736B6B163CDC7F46EB303A8E12C83F5484E15B0DDEEFCD0C8F2B5A03111F5D37BDA26A933897FED489C1F75B08B26E765C81214AB45D94B66D1D12AA1F4CD5BEA568A06C620683AF6E8992ABDEBD51E32213EC76AB0189760B56827C88220E76CD1C211A08CD1929E308CE270DEC6A2CFB16A00E4A21E3F7610478309B
	564D22C35ADAEC2CB515982F6DB2F512DCD0D0F09C7F029E63AF786BF492B788CFBDE37854G7B532356954C90CEC210F084B6BE883B475432AD881B4005990C957F1DC5B0B61E1741589C51176700CBBFB0671937FEB4ADEC2EFAFD9B3BAFC53505FED73C340346E26D90F7354BEF844F9DA761F3AF7D630A4723AB1E1BADDE9766E1DA6F682A11E7DC56017483228FEFE4EDCFF5A05FB7CCE37E9FD0CB87884D33D22C6297GGC0C4GGD0CB818294G94G88G88GD6E213AF4D33D22C6297GGC0C4GG
	8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9C98GGGG
**end of data**/
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEqualsLabel() {
	if (ivjEqualsLabel == null) {
		try {
			ivjEqualsLabel = new javax.swing.JLabel();
			ivjEqualsLabel.setName("EqualsLabel");
			ivjEqualsLabel.setText("=");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEqualsLabel;
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKeLabel() {
	if (ivjKeLabel == null) {
		try {
			ivjKeLabel = new javax.swing.JLabel();
			ivjKeLabel.setName("KeLabel");
			ivjKeLabel.setText("Ke: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeLabel;
}
/**
 * Return the JTextField12 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKeTextField() {
	if (ivjKeTextField == null) {
		try {
			ivjKeTextField = new javax.swing.JTextField();
			ivjKeTextField.setName("KeTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKeTextField;
}
/**
 * Return the MpLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getKhLabel() {
	if (ivjKhLabel == null) {
		try {
			ivjKhLabel = new javax.swing.JLabel();
			ivjKhLabel.setName("KhLabel");
			ivjKhLabel.setText("Kh:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhLabel;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getKhTextField() {
	if (ivjKhTextField == null) {
		try {
			ivjKhTextField = new javax.swing.JTextField();
			ivjKhTextField.setName("KhTextField");
			ivjKhTextField.setText("   -----");
			ivjKhTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKhTextField;
}
/**
 * Return the KY2WireButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKY2WireButton() {
	if (ivjKY2WireButton == null) {
		try {
			ivjKY2WireButton = new javax.swing.JRadioButton();
			ivjKY2WireButton.setName("KY2WireButton");
			ivjKY2WireButton.setText("2-Wire (KY)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKY2WireButton;
}
/**
 * Return the KYZ3WireButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getKYZ3WireButton() {
	if (ivjKYZ3WireButton == null) {
		try {
			ivjKYZ3WireButton = new javax.swing.JRadioButton();
			ivjKYZ3WireButton.setName("KYZ3WireButton");
			ivjKYZ3WireButton.setSelected(true);
			ivjKYZ3WireButton.setText("3-Wire (KYZ)");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjKYZ3WireButton;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * Return the MpLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getMpLabel() {
	if (ivjMpLabel == null) {
		try {
			ivjMpLabel = new javax.swing.JLabel();
			ivjMpLabel.setName("MpLabel");
			ivjMpLabel.setText("Mp:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getMpTextField() {
	if (ivjMpTextField == null) {
		try {
			ivjMpTextField = new javax.swing.JTextField();
			ivjMpTextField.setName("MpTextField");
			ivjMpTextField.setText("   -----");
			ivjMpTextField.setEnabled(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMpTextField;
}
/**
 * Return the MultiplierPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getMultiplierPanel() {
	if (ivjMultiplierPanel == null) {
		try {
			ivjMultiplierPanel = new javax.swing.JPanel();
			ivjMultiplierPanel.setName("MultiplierPanel");
			ivjMultiplierPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsKYZ3WireButton = new java.awt.GridBagConstraints();
			constraintsKYZ3WireButton.gridx = 1; constraintsKYZ3WireButton.gridy = 1;
			constraintsKYZ3WireButton.gridwidth = 4;
			constraintsKYZ3WireButton.ipadx = 20;
			constraintsKYZ3WireButton.insets = new java.awt.Insets(15, 21, 4, 16);
			getMultiplierPanel().add(getKYZ3WireButton(), constraintsKYZ3WireButton);

			java.awt.GridBagConstraints constraintsKY2WireButton = new java.awt.GridBagConstraints();
			constraintsKY2WireButton.gridx = 5; constraintsKY2WireButton.gridy = 1;
			constraintsKY2WireButton.gridwidth = 3;
			constraintsKY2WireButton.ipadx = 30;
			constraintsKY2WireButton.insets = new java.awt.Insets(15, 4, 4, 65);
			getMultiplierPanel().add(getKY2WireButton(), constraintsKY2WireButton);

			java.awt.GridBagConstraints constraintsMpTextField = new java.awt.GridBagConstraints();
			constraintsMpTextField.gridx = 2; constraintsMpTextField.gridy = 2;
			constraintsMpTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsMpTextField.weightx = 1.0;
			constraintsMpTextField.ipadx = 39;
			constraintsMpTextField.insets = new java.awt.Insets(4, 1, 22, 4);
			getMultiplierPanel().add(getMpTextField(), constraintsMpTextField);

			java.awt.GridBagConstraints constraintsKhTextField = new java.awt.GridBagConstraints();
			constraintsKhTextField.gridx = 5; constraintsKhTextField.gridy = 2;
			constraintsKhTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKhTextField.weightx = 1.0;
			constraintsKhTextField.ipadx = 39;
			constraintsKhTextField.insets = new java.awt.Insets(4, 1, 22, 3);
			getMultiplierPanel().add(getKhTextField(), constraintsKhTextField);

			java.awt.GridBagConstraints constraintsKeTextField = new java.awt.GridBagConstraints();
			constraintsKeTextField.gridx = 7; constraintsKeTextField.gridy = 2;
			constraintsKeTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsKeTextField.weightx = 1.0;
			constraintsKeTextField.ipadx = 46;
			constraintsKeTextField.insets = new java.awt.Insets(4, 1, 22, 43);
			getMultiplierPanel().add(getKeTextField(), constraintsKeTextField);

			java.awt.GridBagConstraints constraintsMpLabel = new java.awt.GridBagConstraints();
			constraintsMpLabel.gridx = 1; constraintsMpLabel.gridy = 2;
			constraintsMpLabel.ipadx = 7;
			constraintsMpLabel.insets = new java.awt.Insets(6, 22, 26, 1);
			getMultiplierPanel().add(getMpLabel(), constraintsMpLabel);

			java.awt.GridBagConstraints constraintsTimesLabel = new java.awt.GridBagConstraints();
			constraintsTimesLabel.gridx = 3; constraintsTimesLabel.gridy = 2;
			constraintsTimesLabel.ipadx = 5;
			constraintsTimesLabel.insets = new java.awt.Insets(6, 4, 26, 5);
			getMultiplierPanel().add(getTimesLabel(), constraintsTimesLabel);

			java.awt.GridBagConstraints constraintsKhLabel = new java.awt.GridBagConstraints();
			constraintsKhLabel.gridx = 4; constraintsKhLabel.gridy = 2;
			constraintsKhLabel.ipadx = 10;
			constraintsKhLabel.insets = new java.awt.Insets(6, 5, 26, 0);
			getMultiplierPanel().add(getKhLabel(), constraintsKhLabel);

			java.awt.GridBagConstraints constraintsEqualsLabel = new java.awt.GridBagConstraints();
			constraintsEqualsLabel.gridx = 6; constraintsEqualsLabel.gridy = 2;
			constraintsEqualsLabel.ipadx = 11;
			constraintsEqualsLabel.insets = new java.awt.Insets(6, 3, 26, 26);
			getMultiplierPanel().add(getEqualsLabel(), constraintsEqualsLabel);

			java.awt.GridBagConstraints constraintsKeLabel = new java.awt.GridBagConstraints();
			constraintsKeLabel.gridx = 6; constraintsKeLabel.gridy = 2;
			constraintsKeLabel.ipadx = 6;
			constraintsKeLabel.insets = new java.awt.Insets(6, 20, 26, 0);
			getMultiplierPanel().add(getKeLabel(), constraintsKeLabel);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjMultiplierPanel;
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
			ivjNameLabel.setText("Configuration Name:");
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
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
public Dimension getPreferredSize() {
	return new Dimension( 350, 200);
}
/**
 * Return the TimesLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getTimesLabel() {
	if (ivjTimesLabel == null) {
		try {
			ivjTimesLabel = new javax.swing.JLabel();
			ivjTimesLabel.setName("TimesLabel");
			ivjTimesLabel.setText("X");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimesLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	
	ConfigTwoWay conMan;
	
	if( o != null )
		conMan = (ConfigTwoWay)o;
	else
		conMan = new ConfigTwoWay();
	
	conMan.setConfigName(getNameTextField().getText());
		
	conMan.setConfigType(ConfigTwoWay.SERIES_200_TYPE); 
		 
	conMan.setConfigMode(ConfigTwoWay.MODE_NONE);
	
	if(getKY2WireButton().isSelected())
		conMan.setMCTWire1(ConfigTwoWay.TWOWIRE);
	else
		conMan.setMCTWire1(ConfigTwoWay.THREEWIRE);
	
	conMan.setKe1(new Integer( Integer.parseInt(getKeTextField().getText())) );
		
	return conMan;
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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getKYZ3WireButton().addActionListener(ivjEventHandler);
	getKY2WireButton().addActionListener(ivjEventHandler);
	getNameTextField().addCaretListener(ivjEventHandler);
	getKeTextField().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("Series300ConfigPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 357);

		java.awt.GridBagConstraints constraintsNameLabel = new java.awt.GridBagConstraints();
		constraintsNameLabel.gridx = 0; constraintsNameLabel.gridy = 0;
		constraintsNameLabel.insets = new java.awt.Insets(17, 11, 10, 3);
		add(getNameLabel(), constraintsNameLabel);

		java.awt.GridBagConstraints constraintsNameTextField = new java.awt.GridBagConstraints();
		constraintsNameTextField.gridx = 1; constraintsNameTextField.gridy = 0;
		constraintsNameTextField.gridwidth = 2;
		constraintsNameTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsNameTextField.weightx = 1.0;
		constraintsNameTextField.ipadx = 190;
		constraintsNameTextField.insets = new java.awt.Insets(15, 4, 8, 9);
		add(getNameTextField(), constraintsNameTextField);

		java.awt.GridBagConstraints constraintsMultiplierPanel = new java.awt.GridBagConstraints();
		constraintsMultiplierPanel.gridx = 0; constraintsMultiplierPanel.gridy = 1;
		constraintsMultiplierPanel.gridwidth = 3;
		constraintsMultiplierPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsMultiplierPanel.weightx = 1.0;
		constraintsMultiplierPanel.weighty = 1.0;
		constraintsMultiplierPanel.insets = new java.awt.Insets(4, 5, 1, 4);
		add(getMultiplierPanel(), constraintsMultiplierPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	channel1ButtonGroup.add( getKY2WireButton());
	channel1ButtonGroup.add( getKYZ3WireButton());
	// user code end
}
/**
 * Comment
 */
public void keTextField_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * Comment
 */
public void keTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	return;
}
/**
 * Comment
 */
public void kY2WireButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * Comment
 */
public void kYZ3WireButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		Series300ConfigPanel aSeries300ConfigPanel;
		aSeries300ConfigPanel = new Series300ConfigPanel();
		frame.getContentPane().add("Center", aSeries300ConfigPanel);
		frame.setSize(aSeries300ConfigPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void nameTextField_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	return;
}
/**
 * Comment
 */
public void nameTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	return;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val ) {
	
	ConfigTwoWay conMan;
	
	if( val != null )
		conMan = (ConfigTwoWay)val;
	else
		conMan = new ConfigTwoWay();
	
	String name = conMan.getConfigName();
	if( name != null )
	{
		getNameTextField().setText(name);
	}
	
	Integer temp = conMan.getMCTWire1();
	if( temp != null )
	{
		if(temp == ConfigTwoWay.TWOWIRE)
			getKY2WireButton().setSelected(true);
		else
			getKYZ3WireButton().setSelected(true);
		temp = null;
	}		
	
	temp = conMan.getKe1();
	if( temp != null )
	{
		getKeTextField().setText( temp.toString() );
	}
	
	return;
}
}

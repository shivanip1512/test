package com.cannontech.dbeditor.wizard.state;

import com.cannontech.core.image.dao.YukonImageDao;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */
public class YukonImagePropertyPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelCategory = null;
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JComboBox ivjJComboBoxCategory = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public YukonImagePropertyPanel() {
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
	if (e.getSource() == getJComboBoxCategory()) 
		connEtoC3(e);
	// user code begin {2}
	// user code end
}


/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTextFieldName()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}

/**
 * connEtoC1:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> YukonImagePropertyPanel.fireInputUpdate()V)
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
 * connEtoC3:  (JComboBoxCategory.action.actionPerformed(java.awt.event.ActionEvent) --> YukonImagePropertyPanel.fireInputUpdate()V)
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
	D0CB838494G88G88GFDBE9CADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BA8BF0D457F524D592CF4B44A446090CC9ABC86CD0D7F5E92BA61A9437B8A59DE81C31520114561A96F7E8CADAECEB8635C3DDE2DF2DA4A4101096FDE075C5881100ACD69FABE3D59620BF129012C0C2A8C6843D55BE2D566C6EDB3FFD4B6E22DF4F39775D37EFD73BB272B41AB93A6F1EF3BF671CFB4E39673C372468CCDCE61A21081034CC227EFD10C6C8F7BDA1F7B6A5B5F25C3A165AAC227BBB8B
	F012D466E7C09B8734A3B335D9C672D7A49B7A89502F1A2E4DBA8D4F05E487192A0507A87B34G6D03E77BDED8F99F3B116F13C57B02AF975ABC00C440A583DC78DF5CBF60B329F8827DBC7599A14DC04881BAE6C31AC907F7286336C11B84303695472C77152B7486686318BEE8A7DB59D8855AE3143B4F37362863A63F9CC7D27F365AF08277522D4FE137BA77305AD6119728A2524987A51D50360DBC30DD19161C66E9416512DC0AB8BDEB36DAC42BDD11E4334F7EC8102DE60FA2A8E2EB6B2F3C8FA4D73FD3
	3009A3326496E5A5B0A238C487A1339FA26F5B5332693A40BB74D3A92E45007A0A07679DG499FF27EFFF422CD65EF7D03B8326F9B45D179EFE9E3FCCF368579B70D4DE564DE4675DF22670173B682FDB7G63A5534FF999394CF9F9FA9147BE817DB800C48DBF390478A4689FGA0D7B80F17FF41F91C187BE332F1B8A5B3BF8A0F26AB0C3776ABC19E57764EE514D01B794F13F86E73C00B83C8G588BF09840F435B62B0C5A6AD6A31AEA07C569121CCE495561F6F7BA2C728D49AABA60393595A8AA6E16B8AD
	49D6C218BDFEBF2DAE82BF9C9A2E065AA282136D8E617693FF6BF932761D144C4C8832A56C29B1F42BEBCCDC5549F6957596CF4F568F4FB88F67B37CF7A8BEC907E7E7F50562313F9FE8FD6DDC4F93F91C17410C8D4458F84D10950117586F3599625B998FBB5A033CB09D3FDA08BACE81FCEEC7EDD68DC09B40A440FC8757715B4677BFCD477DE07F32CBF00CFAADEEC1D6BA3DF607D514D5DD7F35EFA5DDC782B66FC72B1E47743265FD7D99A57FD9EFC43DA4EFF94F905B41743135A328973677F1FF343D13BA
	165BC61EF6064728CF6C6FE0E7182E618BA9BED507E7E77BAC75AD6CE7F4028F823071C9F47CAB1D61B6729A456F6B8C37914B923711D420CDF455E63173F596E3DC5C867D3DGA9G595D90FB813A81A63A7999771C2CFFCCBF9A511DAF1B7B4E49D3500AFEC5967ADD9072DC536238A05B850BC37490F5CCD6816A5592B2AF37GEDEF9A7638A63AC4D9D06C128BD6971CA29813070DA9AEB809E3E411D2476CCE91B6F03A89DF37222090DA0760D1EE3B2D90EC03A3987DFC815A44C3E19CD6048550BA3AD1DF
	49B4667A61F9FDB7575506D308BB887D0C9E2E17380282E82F03F10B0E5656EE7BB432A14881EE23FFEA40B9A6989F5B437D724D89EEFF5DA71EA3F5FBFAA25A9FC2F9AF3325665EF07BFB092EBB897879DEFE07F69BA638CF77C43641E8E0FA0C71EC0FFB5A9E192B743D74479843741CEDC2DB5D88F2A5826C6D653AFE2D200A59E027609139558D4BE0B022B528F9B6F6EFA15EA962C35125F40BEE51E505870FFA667FDCF8ACE80FB312E6AB7FCA71981F3A40E0BC9A7EBF8ACFB2BC2C2137EECEBFC4690222
	48F60BD791C7C5C5313BEC41797FC3F9710404BE0FAA6B719214B585E44C3E867756B5AEEBCCD1214E2E86A5690157BD069E235A67D9FA8F5741786EEB7C7C7317B0271A557AA5349F24EFDA64E7978EB8E7B5E7995DBE3E9BF50F47B97F475B6347CBA8BB0FEF8C77BA452D87B936E9724EAECD1020DF2D56DE9F0F174F51BD52FFC3FBB0DB7095C73765456248369CD39C5916FF27F8E5DB7DCAF1A4DBE534E70AD1BE496752597E6F95E37C1B16DCAE11BA20B60530DC784DB114436E32ABF76CD61B280C5A9F
	09F0A775218D9F22B1F6BF1C5761EBBCB67A4E7158D873E89359FE3F4A90A9FF0AFD6E2AE1240F592C35AF989B99CF3FFF8AFDCAF408CED0D41764F5A98C7F9C0D5FAA5E13CEF1EB8BBE4139978646C0073DF651E19D027B25EE9A1CDDB1537F6C5E38703170D0701BBDBE5016199E05398B29BD78F8316A5225ABD5D132783A993BAC763B5CDE3E5245710B8D2A4D941224819F5B9F6ED50B54A9793BE008CD1283F582552719B5A322BCA349CE51CA79B08BBEC5E5220312BF9B97CDEA8C3ADC0A7A0A81BD4682
	EC6D63B1686CE924739824865AAE4961E0C78D3422FEBC47373088544EE6E79F2B5F72B47A6B191168137DFC1FBC1391EDCE7C44ABC2EA00C16E3A9860773B29A00CB6AEB83C413C214214832DC3979F995ED8925986CE15E0CCFC202DDFDAF214607D4F56E4B63BD540B55D325D89578986FC354EEC716B6BCCB7700FB569D6E8F7G30391FB4D7C00B5904G35DF0755AA0B9E8F58CC8F2D7B54320E3887E84EC7F8CD1A8C73D3810A86581AA383CCFFEC5C0F35F15B203F9BA0B5ECDC73F59CF75C40EB33FA8D
	F716623CA064EFE7707C7706D1DF6326EC10A37A98027FBADBFF6D75703AC0AE43F563813F6DBABF4775A57ABB2B436311266DB4ADD34F242BD46FFF8C57D7FA9CDAB0CE8F5488D6514F4FF52894F9453CCDC52BE773076FDDA64B73D026DD1E92F47928BF0CC9DD10BA7601E9F4E961E95CAE223FB1BDAC746AEB65ED79178CB9D17410244A1F2253C38D54212CA6B69D556A73C1EE874F34E2CD94699D40B4E55CEC735A4DDE450EC923208854B319F530B5B7537B9D571A9D647534BC07B69D877DC400A4G16
	5B37911E5B9FGDC558D30A36AF7176675FEB7F103658AEB20E531679B54CED6996A54F358F131001DDBD8D0613A5DDC582E6E7B4A0B9BC85BD21EA1D20D7FC43E49307E067A3E6606CE37FF67B6A43E455FC53C2B4917BDD41B55BE44657B1B90797CC3CC3E38A1AE5FCCDA93342B0F23DAAD5F2477136D7B6A227AC9429063FD67D0381F0CD750BB8E700707381F6CAABD297313DBE019024B8611CB3B6F4B52D90CD27BF3E519BCC6911EA3BFD50932678EC32D86508EB081609F66FB984ABF35A66F712FD013
	D79CDDCDED9DBB4CF32EEFAD2B0B6307033965766165F9A62BDBFFC2EDFB77B02BDB0FE978C3941F2243B31B70D3BC6D0F54E639C7787E1DA4FC7F5DA321FB260475593C11E5734CE17D221B2173189F1C3FD04E7DDC67B62D2D41FC027933F5BEDB5D2B6EA62FA7FFFA8667DA213F86A096A081E0AFC0AA006996BF4B1B6511E2DE68D9C68BF841DA054919396C3D56636927F0593CF07D345C0A249F1B23AC32FEF83E23463B1F4D73F8B7F20B6B6762D944BD817DD8CD97AF161F50794E08E4F7A91A5F5C2D40
	3BDC126DB63B0B6326A8EEDA12E9524AF04FD6C64F27B7D7C64E2737D4C64E27DF2CDC39B67CF6E534FC1B55F14D746E5882726DGE0383F2431FFAF748FEA38578308B30E023F0FF2DBBFB807F953A074E78156GEC54E87FCDE918D3EC83DC72A84B9B98EDF1AEDA0D06EBE0BBBB9A399E9C0C02C7582852589EDF0B3AC7164EBEA251D72A930F29ED859D376BD4BE553D47A3FA288360CD2A527ECDA9A6916A92CC6E894F3D83F4BD9C5323C8DD0D9A6F76F9EE6BC7A248EB9C2B4D2A998B452B6F814EC4372B
	374FC436ABD205766C113C723498EEBBD9D5A60D967468FEC5F42298EA1ECB67AB028C35D5787C53F4BE23C51BDFD5954DBE190E5A5274BAFADF5311E1E1257B218574B398D1C7663398477DDA3F09768304478B31A3CF633947E5D59D1C653257292F3CEAA064C90D3FBDFE74EF0790103CB0EE7B980EBB981227CF81EEBD4096159F364F1F78B0571B1185A708BB6A962749465BB877F0004F4D06FEB9C09B40A4407CED1ED7EC97109EDAB1F2FEF60A184F8E8C8A965121470FBF4C53703C44633429BEBC03E5
	95A0617553798CFD1E337FB64BF33247F99E54DB9909DEBE4EE3479A9ADB9B20BF286146F3F8BC1952F03EDC4445C0FF2306ABD9606336EA3856C5446D067E01F11E6F5A2A4F93DD3EEB37B84D5E07CE33E001848B12735656A12F43B1289E139A738B827CBD4E11F11E63368770DEEF3E833C82D86F7077827E1F73DCBD7E798DE4F0CDEF446FDA7B46AE99666F30F30E3B934C456234DCFD7E915FB39968C59308FB6111DEFFF8A65FA26CFEE37464907AC9B57E8469F9F3FABA727B3EC1CFEF8F21233EAE2A73
	19ECBFB84FE5BB7203AD4471674DD13F59CDCEB0195628ED6C2DF3E4F769FC86BB03C3B3A692712E25D647641F4C60F9C862847F2637D049F1A9935C463FB60FBC38E5F1C614216AE72FED48542FF07CD6EACF9760399900790DB3077B4DB7811788309760B00069A33E6617F2516740616CCE2F132E4856FB1E5ADD8B0C6B7E082F57304057F383AE86601BGBBG76EB6B5D24F608660505BFDBEBF61157E299F779FDF7BA305A6FC4AC77CC2036D8F417651EE99A7EA50A2F5261D9CE5A9B605FCC6B00366EAE
	4FE376D723AC2950B7CE427D8D508EB085B0BF49731A6F1C45F7C2AB66F8E312FB90AE9707AE4DD05FC1BE7AEC5F41DE4E62F617705D4DA4718B29E9516A5FF5136AF7B035ED38F90E6219FC3E9A14EF9F500EGD45D2B4DEA819884305E63723DFB360E490725FA072288533374ADG978E8B73A09A83327A3B008C3DEC7EFB6A7B88350D3B377CFEE673FE2BD2BFAFC19D37D3ED13A34CCB39073FD3F872BDC2F8DC3E1EF386DA17682B520A45EEBB30079F0AAEB47E32212E2A2971222A4BB81F3ED6ECB95E94
	35D6EC7EB55B3BCFEDB3E24E297170299C9E1B567C1A472617F342E393B2980CAD07F242E35369907A795C7058341426275F498D0FCD4F2A7319BEAAAF8575111D71B87AE0320CAFF0D992B5D9E29642E5298F61755B8B61325C8C219FD8D8D9165245E832B07E1796715D1FA2FBC575DB85DE317C9B346EF9B090ADF77D7FG3607789B5D03795C9B55517351546A4879685B55116B9CF2EE656F86596722FFB748BF277FEE40E2461B17702CD240B6F26F57E6B5GF483CC5C6771626B672A890D9791E2C50F55
	0679B0FEC9D6787EEBEA224B7A451ACF7F7E3C2E8673B60B24A812935767782F5660F9A9121BA2557A3186F584E5662C42062A78CDB5E8738EF10622D92E6427772D82F2455C6779D39FFDF79A0BFD8D77C7E5BC4FCA50F04FD47239FBB51CF90EFE03067E918D77549C1F1BBD457325D76F6A6311F1C339015FD56136D2B74562D04BD4783B2B11FAAA8F601F186267327BBD0CBF6342474B5F3C8212FFF2E0D87A26DE3577581C4F769B0F3BAB569951ED7F3FAEC537071FDD7AF4FBB8EAE63905CD9C8B3855DA
	A947CC5FAF4C4CF8C44D6E0CE623066B770CC89EBB56D8AC5EBC190171E6C6F2A9ACB7F90E5A429650E542944FE16E175118844C30F87BB22681EAAD32230C67C07BE17CE1G13GF7AB4084GDB73FB54C67C500F91781ABDF321EB16B78557FCE10E2F19G63F7829CG4836C0DEE261794AAEBADEFD9FE2BE9F6D3C419387609E9836FC8C056518E053665C2E4F53478AE686AAED329E7D9E3F875A85077D917DBCB08A83DDB6466B8DB4DBDEFB16E18C8839EFFAC119972A2DB0D732D557D541155358FC1157
	937FDE466F1CBE8B3FF3CC6561F74EAF087E4E58DA91FE671404507F25A27C4E79E2883D28B27C4EA9A47A7B53F0B5A8CF755C6A64C91A537281CD1EF766D61627E1BEDC1E5BA1F44B7C4A722C1F0FA68F7347374ABE1B9F6F19DB6DBC76DB531F66A6011071EF10CC0FCC8A16679D9DDF7A073F0F3F5FDDE4A006FEF2F4D644205CCFB2FDF62BB26B471F8161EB202FFABEA10BF88E2C2710CCAC4DDB95F20C575A2D307A0910D3797E26DF7E6415FF9A772413237D24841DF8D414294D0AD6FC4F247EE028179C
	F363BB8AACE77AC90607B6199EA5609087C9514CAD7CF069D1C4EB8777860FC20A87F127FA7C7AD35FA7F8E6E9797261A049F061CF1BC881A3BB84172D1E394607D412340EA9AA83AA07G3F0514DFDC452C2DD2789779674F8E6CCFCCA706DEF25CA18956DEE19A9C361F9CD3E6E551B3ABB92CFE7755F63AD0368F6D8BF37EF4D82ECC939F05B5635B8D0D69877E2F2FBDF160C4AF1435FE1954531198DF4C5A88EB572CA043D6225C2F60CF35A439C623FD2ED279798F2F1D1EB410B4D5C8BC0EAC3FDBA1C7E7
	84275D910007EE6A837420F260E0A0B63BBC782986B82B8A630C13348D4CCF6DEAF87A8F5E7E08EE901C2A10B3CC15B0355EA6CA4E7A0E19993BCB5496G3D4272955A72B8469CB626407F656201BD6FDA408E3AC93A3321017EEF247F1B70FFA345B4D2CCE393A8B7052C7897CE77F3192971GBF79149FBCFEF3AF1C20720FEF6C5A7CDADB4AC1102E17E4524FC0F088466028A12F53A24ADF08FF3AA2E77D3E5F45B3F2232978F15DFC36FC66B27BC16C3165060068935AF1E04FC80796507F452B968EE71D26
	F3CBB41EAA7DACC73938244FD18EFF3EB0EA4D54EEE3394918ADBCC7A9B6A26B6BG9FEF63B90A180FC5CB34BBEB54A9B89C0A60F8506132627D5599D044E09EDBEA4475BDFC0CCAD4FF8FE1C43EBF564D79BFD0CB87885A1FC518A192GGECAFGGD0CB818294G94G88G88GFDBE9CAD5A1FC518A192GGECAFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDB92GGGG
**end of data**/
}

/**
 * Return the JComboBoxCategory property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxCategory() {
	if (ivjJComboBoxCategory == null) {
		try {
			ivjJComboBoxCategory = new javax.swing.JComboBox();
			ivjJComboBoxCategory.setName("JComboBoxCategory");
			ivjJComboBoxCategory.setBounds(77, 40, 191, 23);
			ivjJComboBoxCategory.setEditable(true);
			// user code begin {1}

			//ivjJTextFieldCategory.setDocument(
						//new com.cannontech.common.gui.util.TextFieldDocument(20) );

			if( ivjJComboBoxCategory.getEditor().getEditorComponent() instanceof javax.swing.JTextField )
			{
            javax.swing.JTextField edField =
                  (javax.swing.JTextField)ivjJComboBoxCategory.getEditor().getEditorComponent();

				edField.setDocument(
						new com.cannontech.common.gui.util.TextFieldDocument(20) );            
			}

         getJComboBoxCategory().addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );

			for (String category : YukonSpringHook.getBean(YukonImageDao.class).getAllCategories()) {
				getJComboBoxCategory().addItem(category);
			}


			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxCategory;
}


/**
 * Return the StateNumberLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCategory() {
	if (ivjJLabelCategory == null) {
		try {
			ivjJLabelCategory = new javax.swing.JLabel();
			ivjJLabelCategory.setName("JLabelCategory");
			ivjJLabelCategory.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCategory.setText("Category:");
			ivjJLabelCategory.setBounds(9, 42, 67, 19);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCategory;
}

/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
			ivjJLabelName.setBounds(9, 12, 67, 19);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelName;
}

/**
 * Return the StateGroupNameTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setPreferredSize(new java.awt.Dimension(150, 21));
			ivjJTextFieldName.setBounds(77, 11, 191, 21);
			ivjJTextFieldName.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			ivjJTextFieldName.setDocument(
						new com.cannontech.common.gui.util.TextFieldDocument(80) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}

/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 1:33:45 PM)
 * @return java.lang.String
 */
public String getSelectedCategory()
{	
	return getJComboBoxCategory().getSelectedItem().toString();
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 1:33:45 PM)
 * @return java.lang.String
 */
public String getSelectedName() 
{	
	return getJTextFieldName().getText();
}


/**
 * getValue method comment.
 */
public Object getValue(Object val) 
{
	return null;
}


/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.error("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( "Throwable caught", exception );
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJTextFieldName().addCaretListener(this);
	getJComboBoxCategory().addActionListener(this);
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
		setLayout(null);
		setSize(281, 83);
		add(getJTextFieldName(), getJTextFieldName().getName());
		add(getJLabelName(), getJLabelName().getName());
		add(getJLabelCategory(), getJLabelCategory().getName());
		add(getJComboBoxCategory(), getJComboBoxCategory().getName());
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
public boolean isInputValid() 
{
	if( getJComboBoxCategory().getSelectedItem() == null
       || getJComboBoxCategory().getSelectedItem().toString().length() <= 0 )
	{
		setErrorString("An image category must be entered");
		return false;
	}

	if( getJTextFieldName().getText() == null
		 || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("An image name must be entered");
		return false;
	}
	

	return true;
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
		exception.printStackTrace(System.out);
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 1:33:45 PM)
 * @return java.lang.String
 */
public void setSelectedCategory( String category_ )
{
	for( int i = 0; i < getJComboBoxCategory().getItemCount(); i++ )
		if( getJComboBoxCategory().getItemAt(i).toString().equalsIgnoreCase(category_) )
		{
			getJComboBoxCategory().setSelectedIndex(i);
			return;
		}

	//did not find the category so add it and select it
	getJComboBoxCategory().addItem( category_ );
	getJComboBoxCategory().setSelectedItem( category_ );
}


/**
 * Insert the method's description here.
 * Creation date: (8/27/2002 1:33:45 PM)
 * @return java.lang.String
 */
public void setSelectedName( String name_ )
{  
   getJTextFieldName().setText( name_ );
}


/**
 * setValue method comment.
 */
public void setValue(Object val) {
}
}
package com.cannontech.dbeditor.wizard.device.lmscenario;

import com.cannontech.database.data.device.lm.LMScenario;
import com.cannontech.database.data.device.lm.LMProgramDirect;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramDirectGear;

import java.lang.Integer;
/**
 * Insert the type's description here.
 * Creation date: (3/31/2004 12:15:45 PM)
 * @author: 
 */
public class LMScenarioProgramSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JComboBox ivjDefaultGearJComboBox = null;
	private javax.swing.JLabel ivjDefaultGearJLabel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JScrollPane ivjProgramsScrollPane = null;
	private javax.swing.JTable ivjProgramsTable = null;
	
class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getDefaultGearJComboBox()) 
				connEtoC2(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getStartDelayJTextField()) 
				connEtoC3(e);
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getDurationJTextField()) 
				connEtoC4(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == LMScenarioProgramSettingsPanel.this.getProgramsTable()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
	private javax.swing.JLabel ivjDurationJLabel = null;
	private javax.swing.JTextField ivjDurationJTextField = null;
	private javax.swing.JLabel ivjStartDelayJLabel = null;
	private javax.swing.JTextField ivjStartDelayJTextField = null;
/**
 * LMScenarioProgramSettingsPanel constructor comment.
 */
public LMScenarioProgramSettingsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (ProgramsTable.mouse.mousePressed(java.awt.event.MouseEvent) --> LMScenarioProgramSettingsPanel.programsTable_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.programsTable_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DefaultGearJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
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
 * connEtoC3:  (StartDelayJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC4:  (DurationJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMScenarioProgramSettingsPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(javax.swing.event.CaretEvent arg1) {
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
	D0CB838494G88G88G9BFBFFB0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBB8BF0D44711D005DDDCD9C720024BD841F598FFCE0EE59F3ED3E292D3AEFCC7AEB8A631624331F32192FC27444A8567380BBF2AAB6AB2DAC9C8007E92C882FDD6BFD8C94B6A8BAB2D846887C8A0C00756C2465C41536E1334303F3CFDC2ABA2076B1EF97376E935ABA1DF79362A755E74747474747474F44FBC117C32F5A9093AFCC292D30872B3A792B2F10610B73F56174CF193E91129C473AB8748
	A1D29B927821AE61DCE4EAB6794551B4A8C7C0595B9D19DAG6FF9A46E683BED7092261F8DD0577D137FFAE67EFE7EFCAF6FA715161FADC907E7A640CA4045E01BFD587F2BA5058A7EDD40E5AA6F8809BAC236D31A5F998BB5785EFD0CAE8A1E1BG7C7B116627A545CAFDB1141126851EFD7B996DADF86E2352BDE4E9D568EE2C0AA29F7E20D41701FDE978F3582C340DD31EFA72ACD5C49279B37327706C983CB3FE5A66F119EC025B6DF14B22ED42E49F956D8E59A319269C778449EE320BF79DB65164F479EC
	22DB109C9E0BE560521072BEA8F946A541B5A44A3243BD6E9B945C229378DA8F60F80A0E27513EE0BCD05EC9F12B0E238E57407B86002D877818FE1C5121483C7ACE945979D4CE58B1D5253331742587463476253DA9896948DFD71A2E34DB8E654DGCC168B25DC160E8CAE0B4F0034B214A3G1ED671169A44EF0272F600DD99DC46D347380CA3FFFC19A40CF669324248181C49E4B3E686E4AC0CEDCFA922F624ABC6DB18003A8800F5GDBG62G1233A2D30FD07B5D3E37961EDD23F20F476572383B3C5EEE
	27DD3A64310BCEF837D820C641DD95ED9E49CE8833515263469072F0A84E1AED1F88ECEC57883729682BAF92D9B66BD2C20CAD66ED3D2EC161511B95985B12AC54DB0A8975E60777C405AF4367D27C968D1E4D55E35496303C936AFA33391EC7B239AC83496BC9F6E30FAEB504ACEB7E36D597154DE4581C9D100569F887DD133BG1F1693192A87E88598817067F09D1726B6AF2463FED89312DBF08ECD0EFA85C96E1EF4B86D222468BA2DF4BEDD0782566E50225BB13DC4B5EB67E8475FF407544B0E28E33A55
	B9CC9F51B9813D303E2F046DFBDB4EDC5B48D46770FD3AD6F66630B9CCD2717FC6713BB5F8B63753940FE5129B19EA87E074AF523535B5B758C6DE2178385CE09B692C61B6C272008FG1B5FFBE568AB37C2B98EE08FC0FEBE78CB00C1GFBBE1FE3C7EA7D832E23C14D7C3236870A8F41D3744B12506F7649025BA68E035393C61D220FA8B47945285751D96D0A687A36C19F67C537A889324363866E02CB84E372B11A0A629C2411C4DAEBF538C4684065A51CEFEDF19EBC1D02CF7E54EB97E4B1C0416A1B0AD1
	A7F705E160888CF0ED66233ECED01F3B8C5E5765F3DD7D259EF189D8FF106B650562DCF8DE8463961D96CB2F4306E28852B437518326ECF8D6817DB6009B07A2D3638F7175795B91EE073D99AF90635BDDA16D9041DB40ECEAF9C1309D96D27E9BG9F8B309BA03B106F33CF9C9FF940F501F221FC6A5EF0104939709A7C7683770174A91AF212E20F7335E1FABFD801365BG639AGF096F25D47976B19CDF68BBE11DB6195898CC83487E602517E32847798712E6816FBC52F6836430BCF3101DDA57B8276B966
	D1ED675FA99E7DD58F9810CF45A71464B0BC7050DABB2FA725D8AF483264981D14C53E292B75E925A80BEF16AB74A9E3AD284431AE03B12E8508D6473A2AB4CFE3E7839E4F1DC92F95D710E22FE3F4DF5E8C746F2A737F0181632EBDEA7993DA8E545FB7BC685CE5AAFC966F2357AC2A0F24C550867A082941B5407DE60A02E7F52F51BAC39178B3GBF403AA2BE7EF3B5A3A15AAD267F05E53BC97B5072DF486697834CB6161505376DC74AC25B762A3250367DD8597C367B97E521EC1755BDDB06E374CC39B5EB
	6179B2740FB60F5BAD528529B6A1ACFE7E2D954761F0BB64EB8E7B38A88FB96E09308F9621CD8FD09F3C955EF794F11FB9D549FDE6553D6748661B45A1632BB54F346BDA8EAB716D61004FE4B2C56BF10D09CE51850A6A71CC3AE5067FEB6A5F953CAF0962DA733F01ED8DCA006E23EB4E6872CC7AC4937DBBA809BE1FE8B75C966E8AA6E1CAB651F9B0DD423A3378DA23BFFE7C34FE7ED05F6855323FF9C94BF8A0AC67761A611A8642643CFFFAA91533D79C93A61D72F9D110AE40D6BD6A6976780D82D53D09BD
	86C5E94CA339660A5CC52B9FDC66B107A4763B3D134A4EEAGE99A95DB4FA35529FF22728C49E0A73D22D3183EE085ABBC67901DF6238DDC27EC22FF1536A80A5F641B826E0AB4BDD87BFF9226094B52BFBD43F4B349E2087FF7C91A19EF3FD005F6B78176B883307AB0776D26F22C673EDD5940FABCCEA7DBB2D017F0182E87D33E4646238F335CB9D32D77AFD07F74827531E16B191CC7AA50177A44BFCC0A90422125DE9427F99CD6119BD4B7ACB8A78371DDED4581F8BAB57B964317D406DE0B151538D75DD1
	799F2B5CCBB0CEE3BC196F58A0A0CF2F64F041B60F9B31F2C670C529760C2073886C09B00EA570DC8D405AC69ACA6139D082BED10C7CBE2A6167895150FE8BC03C42332A18690751ED2C65F4B1D05E86302B185199CB34F4B3F51CEEG70F600E5A521684E98B95DE62C8FC117D002F4B675FC20DE455D2338FF058D6709E41E77DC4A561EF758FDADA1FDA7C2C7A963BFD89A1C8F5E2FC63EDE40AFA965767BE425B6B6697279BCB687DDE84A9C8FD6E97DAA43FDDA359F1E30D6FC109B5AC5BF37134F2BD0D60C
	579534B2574F571DA2F3730F3374040507FEBDD3F415DABDBD10B274012975285B4E30C31CA2DC8FDF1C531E11EC4A2A519D882307554A78E3B4FA5871E6FB8A1369F18B663D214EFEECD4C853782443B4A9BBB0B190E401BA85E6D86CAC65FD9A33E15F9F1672B31348ACDC8F46B2486381EEG307C2D34B6DD51058C38E8GE66B372CDADB0F83FC1A9E6C83209300793F07EA0F43F3D1FB162A77BBC92DF40DFD396D0969FBFDDE27A25F369757130E7B7BC31E77AC4F4A570D68191E1768837A76FF3BB6A582
	006961BDD58F5B00A6399C74C3FDD7FE0556F7F51433B858DE4EF550D7875B81D97CC648753025A61759EB10EF677975B3F5DCAEBF7439C115EB352415ABDE11AB3B024B15EE44B53B48EDC869F342487B2C82FEB7AAF81F6F34056AF3F5850F212EB5E97D40661D0630FEA02A024D43260AE0BFE0BB06367BAE60F7D5F0BF70132A9C0D9F388AABCFF00F0327D76354EA9CB378F4253C3C9A7B25BE1D70DCEF1D81E5A715112945GADG83G4ECA5EC75651854F1A4E7A67B9EB5A251E5BC4D432B12D2B8C64B5
	B1703E95E00786474ED5CA336979CCA5BBD749D271A60AAF2C8A60595C2C236B984B1DD037290A679CD5354167C62D553373AAEBD01935EB1A532EB4286C8FEA476C76769F8B390F52181445F201AEE566C36EE4EBFD484AEAE6AF1B6099DB4D4FFD57D8392F0A2F663ECAA847B16A0FC62636828C9E6567A49F537DF2824A4BG5682EC84588110GD0FC0C4F657F9C8D6553E74FE5B8079E483919FE5E7F12FA7DE8CEBBD3D03963D8A83D0EBB0F0456AB8F3A959FFF31026F4D56E35C861E2F7BF232FEE7CE3B
	E0D9A3C24ADAEDA88EE383BC8EE6323EA1F1D91FD6E5F59B3F1C2CB3F35A854B3AA3C8D6A6C326054B7061B1EECFA76B90E7B4C426F698386D3CF2946FB5382F9974B85CB261FEE66298460A9E49B16EF0F31C0B62EC9E09A6278C97E5881FB77F15A1F45E7CADC3683C7995437CE7C2FFE78817D733731B24B29C63G0CEF820061EE67A1EE864AABD55CEF7291978365EDAAAE7D9062F6C3B92D06635EAAC0DCB114DBEAD45F130DF6508765890065GD1GDB81628112EBB9DD8B2543B882E3D8FDAD0BD1B17E
	6C0477DBAA5D33D95E20B9D5771C1A054F0A16ABFDE6EAE45A2A340B1A27BD6B7BFBE17B4ED820DF4EA3CBF967E7B3871472FC67B3A5147E75C3D9F42EA17E71D18705511DD2773EFC1004B86F38AC38C44273514FEB77A9B4E7E5CFCF233ACE474F1604DE27C3B68932C18C9D55F572D4F61AB256A5355C9FBFBFC44F7E213C87A03FCEBD873662DAB4C23933EE2EDE2D001BD070B34A136DEDEFD37F3E320E6DED09AA7E67943FD603E7FB5EF8B93FB30C023AF4A3DF2B24B67CBACB2D8D3D4EB2EAF1ECBE4F24
	E4930357D2FEED21DA9758917AE55105EAD672CA5ADE9624F1F14EDA2C226DD9DD387647EB432DD7B60725F55AB9ECD26DF230E23EBBA92FF12E7E993FFF1A452FD96597AB4D47EFD9D8FE4F98357CDAD4FEF91679782D89410F69233D8E67552F164F5072345A5F61B25437D79247C4C9926D6C980F449951AE1A29EF0B06770DC69E77AE4DC3FF6C9426BD136038E70EA35D47F95AB844G38C1G7BF1A693DBA3AFCD61BA9D03E48426E348AB5A4808095ED79F64ED37G7D3BG89G05D057EA62FDD69E4236
	5050611AF4D18919FC6D54FF2E833AE81576578514D6708751A6965238976862D45ABBD64C8D788921F631327AFF284218B654499C2BB7366484558F8823747E1F5527F4E17BD0A7E92CBE628B6CFF6E6996EF7FD6C3766C7614BD2BDBB51329ED2BF44C7208EB855ABCA23B1E4505AD75BC4FF86BD8287ABE35BE7FFA287A9B755CAFC5E5733B81AF601655BF587D429A200B8E225D8C656DC1B8662B4A2C6813626B036FDBEBA8BE21BE783E7525A1FE5F1A56GF9C7835FCFE3E87C9B5B406815BD1662F681AE
	2D1153C594727D39D8453DDC0438CEA80F28381DBA44B9213C26116B63E778390C32CE63B46F2E72455C21AC96D89F8FFF25FD30B10FD7F05DC42BFAF8DF6238BDAA2E5642F183CD10BFB461B7C5E568E295FB3E19270727DB1C522B09FB2F43A53A718257505254E054B71FEC28E1396E3AA9ED2E5B3A5F94B6575D59446C67C3659911F4C471B90D37E6454F582145F2DEF2583B0571816A41581EDCCCE3B364EDEC66FB7205FC7AAD8714E7GD6B7739C5CFF141FC1C5BF3B1E8CAC698E791D4ECEEB0DAE3A19
	49935B9C48410D316DA92CCF43C15EE7FACBE4EAEF8B62068FEA579D6A6C47BA96FF337A65336A517760F9CF207E3B336AD15E7EA4ED7D2ED975A91A76ECECEFD473316DF9E383F1FE2BA16CB7C891ADECCC9B14277E2D43E42733AE054D791916401CFF3F60C1661C359BBD91E877C5560334E3737C3B31B039B2750ACC5FABE8DC0D7471ADBCB75EE760B87D890E3BF81D63C6CEF03B7E810D59D6E2D945D56650BBAEA86FD6F1DFD0DC72C9083DCFF25C527D08EB01F22F0A7B64A2DFA7A3AA4EECC6DC9414B7
	1F64FE255A0AE7695B55F2BDAD63B9B6AB3FBC3458350F3C9667DFE2975D47E26E0259DC3FC16F9205C959F3D57441160DA7893C6E57567071096ED31ABF411876G30F86229C29ECF984C10F7825CG1881D8EB667B7E0F0AC247883F227EFE9350EDD5E9BF51154D6A1F8689741A44A1CEF9BD122CE91F1FC43FB30136492D901B37A26ECF953D730C1F5FADA7A95082ED7AG8EAAF5B7DA195F41B548787D27B49FBF1E93F1FE9B0278ED017736D97C3AAC7371CB8D62979B44AFCE430F7910B7BF577A6D6C48
	C29D4FB502F96FEEE5363827B5780C52FA92755850867BC89B4FC56CCDF5709C96EE4F3DC1GA444F0DED1122799165EE0A976DB1E3F58EF4DD81C71AA757F4E36609C6A7B946FEF8B4E218673F98E358C6A625B789948C3E63447818B5883409200B5G9B8136DB54EF111A9A488267E9D60FF7G92B1276608020D6F7841AF77AD5DEB295C5747FC771BE463433B93435D2544DB587C24353367465843944F46D7580663EB05BAAB009FE0A54053G1B5A7978BEEAB232716174F44932E01B20B7CAFCF0381482
	E8CC8E58583EDE90EAEC310A9CF16DF3FD99EB17E98855AEC121CF6F8857EE70FA28F6FA053EA5C4BB66A35E4B46EF2E6F0281BAEC02133908EEC19A74B89D36E945B76C21FB886E37FD9D5C37D8F2D007939E49F14F6316673664FEEE186EABE7G162BEDD76704770BAF303D89E8372B74EB76635EB1867DB01A97693EC4CEC13EFF0A7B2A4C0105FCCB9289782B8EE8B7F80A7982AC5B617D714440F7B9F96432B9F45EB6C677BAAF504F1C62722D3F05631975358614D9DC702A0528BC17966AC95838203975
	0471C4C3C3C38D5B4BDFAF64FBF954A99EE3A5968547D8B94734B1D2F3D1F00C056389540BC541B1D6522C986B312260980B37E7E3F92FBDB0166D3A2AF94652E4BEE906A8879FCAFC54FFAA5036273CBCFC5B2686080FDAD191C0D3C669E17D927E5BFDE6AE0E4B3774B4A15CC73C174EFDC47173CF115EF7F6A7D612B0B178A936B69ACE87FC7916183DA9217AE67DFC0C669CD2B6D63F82C5D7FCCCBBFE8A4E3FD5BAD1CD426C8360C9AE00AE2E0C5E96ED32D598D75BFCF61246977CCC08ED8FCA5D2D13B8DF
	789D13C3F0BA6E51ECFA88885D634CA7EC2157DA6C7D071A77617C2FBE3773FC25FD30712FB007BF63FB549C7A0C6F9B6650E769CF1A677F866DB9F378EF50B618355F2031F96F2BC305CF00CDAD830882588490FB1A6FAB27DBAB885DD7C26CA9E76D63F84608DFAD4B3C7F723670E3B534AD7C2DF3E39BFE7BB06A11E50F8B79F33C398D67CB76F8A91261BA5AD0C712E3FCC2E6248A3E3B0D1E9F08E3944DE27FEF525CE4970CEB4FE91E8FE4296791E9E7B82E5A4069C6D45C4386CEE7D7F16357B9DD0C0A5B
	FA1D53EDBE43FDEEDE8EA6EA5A9BEB4D0935E9786CD5EBFFCF57404D211E2BD786863A3B2E5E6C9A3AF9796C595E333D2C3DAB27E91E76FDD72E76DF3FF259BA0B43653340167B1ADB78F903624F6E6E5713797DD9E3AB4F93639B836DFEF331A2385DB958648C95F6D84F1E718A239E1B3654BC289F647E67909E65AAF21EBE9968EF4550034849566F0F8A3571C4F6E7E4EAD5677C799733FB23997761D62035827881D682BC8D3089A096A08EA081A03BABB255G5081B088E087108116832C6D626B646FCF23
	B38D97BBAB87A2F431C80276BEEE30AE7CFA39D63770FA39C56F7FC430F36B34D7B91F976B689D5F5818CFD457656D3AEC95576F9B74789C6805193EDFAB4038C5060E58DC24E13A2164AEC78B970AD73439D51FA6F741BDECC3D7601D713EE38970FE423018BCAB1877A660DDB60B770A76G6FDFDCDFA8A71C0F7756AE76BFFE01734CDFD2DF319D70C5C99C77AF94378BF036C42E674EF9FC7DF9734273B9E88E6D7F064DF37D5F6766507ECFB0EB7D9F430DD15FBF8EF6B822525DD1F11F29B8AF650938EB8E
	3BBC21626FD239905FA7E2079CBFC3651A2238918537D4037BCC41BDECC61B04A1C3A84E5BBEE22671324369E4652FD19A079BCCD45547EAAA2343ABE864FEDC45FD264E7DCB9D81DF327DD4607D939AEB3139960E2693B58E2143E378E2FFC6G4F07C7587FF8661AE7C048688FC80ACFA239F37DD1570A1F3E93FD33B7DFC7F47DE46F84ED5CCFD226103B5F8B9684AACCFC5A7787021F7C4EDB4ADB8E25253CF47F77883CD45609C68992E4299274D3B56D3C5BECC97A672712F4A4993AE7E653CF12F16EC18A
	09C91384E6033210FEE539E24A10CBF9B3A78A4622546C1F05455FFF2BD87C45A8F2E44C5A893E776409DFED7B7930AF096C6DA7C518G8D09920D75C4BB5E7FAA7FC0F40E6C7362478818260010BE7AC8714953CEF100640FDD458FE4FD32E86F62510DCFA68769B7F4757825E0FD1F601B10F135348F10E4B77E2B9349E555CE41BDDE4FC24AF6BA52442EDBD4DEDC6C84644D2372A297134AA56F152C6A728BF19B13086E9C596F748876F3028D1CF6BF59A7CFC822EF4263347B3DE7BAA92334A97E0DCF9230
	BB228E9F87EB42DBA5D568BB7EA7A2B262B34E698871CB241ED222EFB52994761E89C1022EC429DF46FF5D72C8D5EA5D52D279688F4F975C5011C4E510287ACC3FD7A6FB47841743B98DAF3DF45F251312AB03341260766167FAA019BEC8B2DE25F6E07A7A6B8D0FBE779F57E9870126B2A9E32A042675632247D55FB5B666F00BAA8350AB30AFD159A30DA908A6573F6A600537FFBF8AF650CB12DC8D8D74EFA37D5B04FF9BA92611E29A1BC0393B493C3F60FABF9FB3B59EF5BD60741B4E418C4A7F78416B6BFF
	5631FB970C6E9CC9211F4F41A4E48728AECF3AC6C5696168C7CB8E2C5E79884E1197CD450FFC8F79D56BC7E71726403E5956AF13CC65BAD20F7711963DCCB2687D211EDEA0E2F9BF3FB33468D50E17GB026C297D420E2B1B0420F55B0404A1C059DD2061C4972441A7B5AB33F04073242FF1FFCDE79BE79FC70191F035E9DC5GBE6ABC0FDB3E530681E238DCEF48A5B81D32603C536536E35E57BDAD0B01DC43EDC17EBECE23D4323AC98B4AFDDB5366FF81D0CB8788165CB4B3B197GG10BFGGD0CB818294
	G94G88G88G9BFBFFB0165CB4B3B197GG10BFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGEB97GGGG
**end of data**/
}
/**
 * Return the DefaultGearJComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getDefaultGearJComboBox() {
	if (ivjDefaultGearJComboBox == null) {
		try {
			ivjDefaultGearJComboBox = new javax.swing.JComboBox();
			ivjDefaultGearJComboBox.setName("DefaultGearJComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultGearJComboBox;
}
/**
 * Return the DefaultGearJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDefaultGearJLabel() {
	if (ivjDefaultGearJLabel == null) {
		try {
			ivjDefaultGearJLabel = new javax.swing.JLabel();
			ivjDefaultGearJLabel.setName("DefaultGearJLabel");
			ivjDefaultGearJLabel.setText("Default Gear: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDefaultGearJLabel;
}
/**
 * Return the DurationJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getDurationJLabel() {
	if (ivjDurationJLabel == null) {
		try {
			ivjDurationJLabel = new javax.swing.JLabel();
			ivjDurationJLabel.setName("DurationJLabel");
			ivjDurationJLabel.setText("Duration: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDurationJLabel;
}
/**
 * Return the DurationJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getDurationJTextField() {
	if (ivjDurationJTextField == null) {
		try {
			ivjDurationJTextField = new javax.swing.JTextField();
			ivjDurationJTextField.setName("DurationJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDurationJTextField;
}
/**
 * Return the ProgramsScrollPane property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getProgramsScrollPane() {
	if (ivjProgramsScrollPane == null) {
		try {
			ivjProgramsScrollPane = new javax.swing.JScrollPane();
			ivjProgramsScrollPane.setName("ProgramsScrollPane");
			ivjProgramsScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjProgramsScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			ivjProgramsScrollPane.setPreferredSize(new java.awt.Dimension(200, 180));
			ivjProgramsScrollPane.setFont(new java.awt.Font("dialog", 0, 14));
			ivjProgramsScrollPane.setMinimumSize(new java.awt.Dimension(200, 180));
			getProgramsScrollPane().setViewportView(getProgramsTable());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProgramsScrollPane;
}
/**
 * Return the ProgramsTable property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getProgramsTable() {
	if (ivjProgramsTable == null) {
		try {
			ivjProgramsTable = new javax.swing.JTable();
			ivjProgramsTable.setName("ProgramsTable");
			getProgramsScrollPane().setColumnHeaderView(ivjProgramsTable.getTableHeader());
			getProgramsScrollPane().getViewport().setBackingStoreEnabled(true);
			ivjProgramsTable.setAutoResizeMode(0);
			ivjProgramsTable.setPreferredSize(new java.awt.Dimension(385,5000));
			ivjProgramsTable.setBounds(0, 0, 385, 5000);
			ivjProgramsTable.setMaximumSize(new java.awt.Dimension(32767, 32767));
			ivjProgramsTable.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
			// user code begin {1}
			ivjProgramsTable.createDefaultColumnsFromModel();
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjProgramsTable;
}

private com.cannontech.common.gui.util.LMControlScenarioProgramTableModel getTableModel() {
	return ((com.cannontech.common.gui.util.LMControlScenarioProgramTableModel)getProgramsTable().getModel());
}
/**
 * Return the StartDelayJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartDelayJLabel() {
	if (ivjStartDelayJLabel == null) {
		try {
			ivjStartDelayJLabel = new javax.swing.JLabel();
			ivjStartDelayJLabel.setName("StartDelayJLabel");
			ivjStartDelayJLabel.setText("Start Delay: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDelayJLabel;
}
/**
 * Return the StartDelayJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getStartDelayJTextField() {
	if (ivjStartDelayJTextField == null) {
		try {
			ivjStartDelayJTextField = new javax.swing.JTextField();
			ivjStartDelayJTextField.setName("StartDelayJTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDelayJTextField;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	
	LMScenario scen = (LMScenario)o;
	
	if(scen == null)
		scen = new LMScenario(); 
		
	java.util.Vector allLMPrograms = initProgramList();
	java.util.Vector theGears = new java.util.Vector();
	
	java.util.Vector thePrograms = new java.util.Vector();
	
	if (getProgramsTable().getRowCount() > 0)
	{
		java.util.Vector programEntry = null;
		String name = null;
		Integer progId = null;
		Integer delay = null;
		Integer duration = null;
		Integer gearID = null;
		String gear = null;

		for (int i = 0; i < getProgramsTable().getRowCount(); i++)
		{
			LMControlScenarioProgram singleProgram = null;
			
			name = (String) getProgramsTable().getModel().getValueAt(i, 0);
			delay = (Integer) getProgramsTable().getModel().getValueAt(i, 1);
			duration = (Integer) getProgramsTable().getModel().getValueAt(i, 2);
			gear = (String) getProgramsTable().getModel().getValueAt(i, 3);
				
			for(int j = 0; j < allLMPrograms.size(); j++)
			{
				if(name.compareTo(((LiteYukonPAObject)allLMPrograms.elementAt(j)).getPaoName()) == 0)
				{
					progId = new Integer(((LiteYukonPAObject)allLMPrograms.elementAt(j)).getLiteID());
					
					java.sql.Connection conn = null;
					conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
			
					try
					{
						theGears = LMProgramDirectGear.getAllDirectGears(progId, conn);
						conn.close();
					}
					catch (java.sql.SQLException e2)
					{
						e2.printStackTrace(); //something is up
					}
				}
			}
				
			//grab the gear ID
			for(int x = 0; x < theGears.size(); x++)
			{
				if(gear.compareTo(((LMProgramDirectGear)theGears.elementAt(x)).getGearName()) == 0)
				{
					gearID = ((LMProgramDirectGear)theGears.elementAt(x)).getGearID();
					break;
				}
			}
			
			singleProgram = new LMControlScenarioProgram(scen.getPAObjectID());
			singleProgram.setProgramID(progId);
			singleProgram.setStartDelay(delay);
			singleProgram.setStopOffset(duration);
			singleProgram.setStartGear(gearID);
					
			thePrograms.addElement(singleProgram);
			}
		}

		scen.setAllThePrograms(thePrograms);
		return scen;
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
	getProgramsTable().addMouseListener(ivjEventHandler);
	getDefaultGearJComboBox().addActionListener(ivjEventHandler);
	getStartDelayJTextField().addCaretListener(ivjEventHandler);
	getDurationJTextField().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMScenarioProgramSettingsPanel");
		setPreferredSize(new java.awt.Dimension(420, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(420, 360);
		setMinimumSize(new java.awt.Dimension(420, 360));
		setMaximumSize(new java.awt.Dimension(420, 360));

		java.awt.GridBagConstraints constraintsProgramsScrollPane = new java.awt.GridBagConstraints();
		constraintsProgramsScrollPane.gridx = 1; constraintsProgramsScrollPane.gridy = 1;
		constraintsProgramsScrollPane.gridwidth = 3;
		constraintsProgramsScrollPane.fill = java.awt.GridBagConstraints.BOTH;
		constraintsProgramsScrollPane.weightx = 1.0;
		constraintsProgramsScrollPane.weighty = 1.0;
		constraintsProgramsScrollPane.ipadx = 204;
		constraintsProgramsScrollPane.insets = new java.awt.Insets(9, 8, 10, 8);
		add(getProgramsScrollPane(), constraintsProgramsScrollPane);

		java.awt.GridBagConstraints constraintsDefaultGearJComboBox = new java.awt.GridBagConstraints();
		constraintsDefaultGearJComboBox.gridx = 2; constraintsDefaultGearJComboBox.gridy = 3;
		constraintsDefaultGearJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDefaultGearJComboBox.weightx = 1.0;
		constraintsDefaultGearJComboBox.ipadx = 4;
		constraintsDefaultGearJComboBox.insets = new java.awt.Insets(18, 7, 73, 11);
		add(getDefaultGearJComboBox(), constraintsDefaultGearJComboBox);

		java.awt.GridBagConstraints constraintsDefaultGearJLabel = new java.awt.GridBagConstraints();
		constraintsDefaultGearJLabel.gridx = 1; constraintsDefaultGearJLabel.gridy = 3;
		constraintsDefaultGearJLabel.ipadx = 10;
		constraintsDefaultGearJLabel.insets = new java.awt.Insets(23, 49, 77, 12);
		add(getDefaultGearJLabel(), constraintsDefaultGearJLabel);

		java.awt.GridBagConstraints constraintsStartDelayJTextField = new java.awt.GridBagConstraints();
		constraintsStartDelayJTextField.gridx = 1; constraintsStartDelayJTextField.gridy = 2;
		constraintsStartDelayJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsStartDelayJTextField.weightx = 1.0;
		constraintsStartDelayJTextField.ipadx = 28;
		constraintsStartDelayJTextField.insets = new java.awt.Insets(10, 108, 17, 7);
		add(getStartDelayJTextField(), constraintsStartDelayJTextField);

		java.awt.GridBagConstraints constraintsDurationJTextField = new java.awt.GridBagConstraints();
		constraintsDurationJTextField.gridx = 3; constraintsDurationJTextField.gridy = 2;
		constraintsDurationJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsDurationJTextField.weightx = 1.0;
		constraintsDurationJTextField.ipadx = 28;
		constraintsDurationJTextField.insets = new java.awt.Insets(10, 0, 17, 93);
		add(getDurationJTextField(), constraintsDurationJTextField);

		java.awt.GridBagConstraints constraintsStartDelayJLabel = new java.awt.GridBagConstraints();
		constraintsStartDelayJLabel.gridx = 1; constraintsStartDelayJLabel.gridy = 2;
		constraintsStartDelayJLabel.ipadx = 15;
		constraintsStartDelayJLabel.insets = new java.awt.Insets(14, 30, 19, 34);
		add(getStartDelayJLabel(), constraintsStartDelayJLabel);

		java.awt.GridBagConstraints constraintsDurationJLabel = new java.awt.GridBagConstraints();
		constraintsDurationJLabel.gridx = 2; constraintsDurationJLabel.gridy = 2;
		constraintsDurationJLabel.ipadx = 11;
		constraintsDurationJLabel.insets = new java.awt.Insets(14, 83, 19, 0);
		add(getDurationJLabel(), constraintsDurationJLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getProgramsTable().setModel(new com.cannontech.common.gui.util.LMControlScenarioProgramTableModel());
	((com.cannontech.common.gui.util.LMControlScenarioProgramTableModel)getProgramsTable().getModel()).makeTable();

	getProgramsTable().getColumnModel().getColumn(0).setWidth(80);
	getProgramsTable().getColumnModel().getColumn(0).setPreferredWidth(80);
	getProgramsTable().revalidate();
	getProgramsTable().repaint();
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMScenarioProgramSettingsPanel aLMScenarioProgramSettingsPanel;
		aLMScenarioProgramSettingsPanel = new LMScenarioProgramSettingsPanel();
		frame.setContentPane(aLMScenarioProgramSettingsPanel);
		frame.setSize(aLMScenarioProgramSettingsPanel.getSize());
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
 * Comment
 */
public void programsTable_MousePressed(java.awt.event.MouseEvent mouseEvent) {
	return;
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	//the interactions with direct programs in this method needs to be redone.
	//Use a hashmap or matrix to hold program names and their gears
	//Make initProgramList do more of the dirty work 
	
	LMScenario scen = (LMScenario)o;
	java.util.Vector programsOfThisScenario;

	programsOfThisScenario = scen.getAllThePrograms();
		
	java.util.Vector allLMPrograms = initProgramList();
	 
	String name = null;
	Integer progId = null;
	Integer delay = null;
	Integer duration = null;
	Integer gearID = null;
	String gear = null;
	java.util.Vector theGears = new java.util.Vector();

	java.util.Vector programEntry = null;
	LMControlScenarioProgram singleProgram = null;

	for (int i = 0; i < programsOfThisScenario.size(); i++)
	{
		programEntry = new java.util.Vector(4);
		singleProgram = (LMControlScenarioProgram) programsOfThisScenario.get(i);
			
		//get and add the name
		progId = singleProgram.getProgramID();
		gearID = singleProgram.getStartGear();
		for(int j = 0; j < allLMPrograms.size(); j++)
		{
			if(progId.intValue() == (((LiteYukonPAObject)allLMPrograms.elementAt(j)).getLiteID()))
			{
				name = ((LiteYukonPAObject)allLMPrograms.elementAt(j)).getPaoName();
				
				java.sql.Connection conn = null;
				conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");
		
				try
				{
					theGears = LMProgramDirectGear.getAllDirectGears(progId, conn);
					conn.close();
				}
				catch (java.sql.SQLException e2)
				{
					e2.printStackTrace(); //something is up
				}
	
				break;
			}
		}
		
		delay = singleProgram.getStartDelay();
		duration = singleProgram.getStopOffset();
		
		//grab the gear name
		for(int x = 0; x < theGears.size(); x++)
		{
			if(gearID.compareTo(((LMProgramDirectGear)theGears.elementAt(x)).getGearID()) == 0)
			{
				gear = ((LMProgramDirectGear)theGears.elementAt(x)).getGearName();
				break;
			}
		}
		
		programEntry.addElement(name);
		programEntry.addElement(delay);
		programEntry.addElement(duration);
		programEntry.addElement(gear);
		
		getTableModel().addRow(programEntry);
			
	}
		
		fireInputUpdate();
		repaint();
}
	
public java.util.Vector initProgramList()
{
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		java.util.List progs = cache.getAllLoadManagement();
		java.util.Collections.sort( progs, com.cannontech.database.data.lite.LiteComparators.liteStringComparator );
		java.util.Vector newList = new java.util.Vector();
		
		for( int i = 0; i < progs.size(); i++ )
		{ 
			if( com.cannontech.database.data.device.DeviceTypesFuncs.isLMProgramDirect( ((com.cannontech.database.data.lite.LiteYukonPAObject)progs.get(i)).getType() ))
			{
				newList.addElement( progs.get(i) );
			}

		}

		return newList;
	}
}

}

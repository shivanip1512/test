package com.cannontech.esub.editor.element;

import com.cannontech.common.gui.util.*;
import com.cannontech.database.data.lite.*;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
/**
 * Creation date: (12/18/2001 2:05:01 PM)
 * @author: 
 */
public class DynamicTextEditorPanel extends DataInputPanel implements TreeSelectionListener {
	private DynamicText dynamicText;
	private JColorChooser colorChooser;
	private PointSelectionPanel ivjPointSelectionPanel = null;
	private JButton ivjColorButton = null;
	private JLabel ivjColorLabel = null;
	private JComboBox ivjFontComboBox = null;
	private JLabel ivjFontLabel = null;
	private JPanel ivjJPanel1 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private LinkToPanel ivjLinkToPanel = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DynamicTextEditorPanel.this.getColorButton()) 
				connEtoC1(e);
		};
	};
	private JComboBox ivjFontSizeComboBox = null;
	private JLabel ivjFontSizeLabel = null;

	private static final int[] availableFontSizes = {
		6,8,9,10,11,12,14,18,24,36,48,60,72,84,96
	};
/**
 * DynamicTextInputPanel constructor comment.
 */
public DynamicTextEditorPanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void colorButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	JDialog d = JColorChooser.createDialog(this, "Select a color", true, colorChooser, 
		new java.awt.event.ActionListener() { //ok listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
				getColorButton().setBackground(colorChooser.getColor());				
			}
		},
		new java.awt.event.ActionListener() { //cancel listener
			public void actionPerformed(java.awt.event.ActionEvent e) {
			}
		}
			);

	d.show();
	d.dispose();
}
/**
 * connEtoC1:  (ColorButton.action.actionPerformed(java.awt.event.ActionEvent) --> DynamicTextEditorPanel.colorButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.colorButton_ActionPerformed(arg1);
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
	D0CB838494G88G88GAB04B6ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD8D4571598B5F5F7C9EA2AEDEDBE3BC99A1A10AFECEA3AF663F6691666B30DED5C2FE6EBB636CBF6E9E23E5A442434EBD35A356EE3989865CF88082844E0A48DEC90C1B0DA420FA8A692D1D1C7441F000F19870C4C4C1BF973067FA4FB6E4FF96FB173E67C496A779D5F3B671E1FFB4FBD775CF36E9B0462B76F4DCC37948BC2FA26407F9DC89784F733A0A40C447990376218ADDBB07C5B85D0A8
	3434DA611982FD4E59366C826165BD9B213D8E5AA9B3ED59DB613DC8C85D135288AF9174AC013E4D73623E9ADD4FE36F239E9BEDA76E4D05E7BEC09A604ABAEC59C39DC47F4A3D259CFF885A797C1DC03AC590AABEA2B4CF16149A70891CAE8D1E39503F0C527CFAEF996FF7C31B504C0267020F986DA2F8EE22231B5DF81053DD3EEB3E306E47B916BC224BA09F2160B863ED604FF2E19EB5C406B03723951ECDDD4303AD8E59D363903DDE592BCA0E219AA9906C2F111CAED5D6EAA43764113CEAE3E37B38D774
	389CF6E9CCBDCA3B3AC42F6496620E13F13FD232114A04F1C33B361360242D44D6EB20ADC03B3493477EF4DE939F5B7C6179C24A7D9B52A30DDD6DE4E3DE50290FFD55BF3DE4B9F60248AF2D4D65FC2B08BE403131DC294531441E4031FC211850AE05F6B240BA8DFFF28B41671E34E5D7GB41D44B1B63F03E36C193CDFC86B2F31EC0EB0C67BC9B63629137A982B0BAED837D1FF3954CC56FC8974A5832482941F32E5B7G5881F6D0BF5D707E7F42332DDFBDA2FBBC32374D67BB6CF6AAA7E42764067746C668
	6138B312C3D61C0240FC7137A555A663C1F01F1A6E0784585C4E8B68BB89E76E96D4354212E9B237454F64D9E2380C79277439ADBDC56C76B33A360B61BD1D5B1A61DF20780DDDBA1E2D55B04513F6A974CDF5211DFB72F1AC1D56BB05023ADA0B4DE4AC8B9EFF5B12524577CB17BE96E6637D2D44469527ED5907GFAGFCGF381961DC69B9FEB2A3F160DBB4077952F68BE9B6C77090AFAB868F2BBA5055BFAE7EDB4DB1B81632B3EE1BEE61778FA639AA57F7DDED33BA44797DB161FE676D8FDDA378B53ADC5
	54EDBD936E9B795A9A7EB75DAB25E7C257B6136E154AB321EBFBC7B12E6DC168DB8840689F27744259D0B94BA93E60EC289C7B9614D38EFD33G587A667AC9CCDC83ED2B9DE2AEC083009D4087B04B0EEB7CF0535E6B5CC7DD0675E53C4E6692F8C2E8D3448EEFC0953D8E29DBD4DCE23FDB8A881C4653CC6C5ABF0DCF217B5B81BABE143C12A22AAE598B52C50F844E94E0B4134D0504C611E82F5D6511C001079CF02CFFE6F391592FE2C0BD67F30A2A24D3307E3FE9A6BEB1A2F603C490G6FAB6D44DE1B3612
	B897876FC9F63455DF9402ABBD87716D9C5AA52B65F5F89E8767165C9046DD8EB28CD199C79F7D4F2D640CEBBF07FB72559E743D763C2F89554F54187A9E01B567189F895D213E77ABAA33329B4FCD7F561EA8BE5FFE4E5C9FEFEC2F7DF5D49D991FC3BE81E65F878344C70F413C1CG333A5146EDAD654C778E0B81893D6D14820EA2B9F50BB35A16D6F216C8A3F02A36CBBE496B0417GDF6B232D1BF4BF9C10B59FB9CE71A4AE9D81C789E878532D058C8FB20CDE0D7D97E83F282A0A2BBF28CAE7A5D5F5F987
	F5FE110EA5B0AD648578DC9FD249DC97429C9783AC55667AD2EB11411FBAE5F9B86833131D427DF20A1E3F2B00BED5DB7F74AD449F0A4FE3BB0736757EF94538F60466732CD3E47FC850F4FCDEDF082CE3991DD73A567ED75ABE284D7B62169E41FCAF7D7F0DA1C0F55C0AB966EBFED3620F6C43EF784DFDF8375F5C072B7C51FD344EEF66232C6F201F64AE72285760734DFE926F9C3257AB510D273188AC6FFD55CE6661723A5473AE6720241EF5CDC802B067BC715DF29AD32F403BFA9E636168EE0C079593DF
	96127AB61966CB8B3ED9E9D94EFDB7653C9E8F791ED748DE62397491B968D5993E1D46EB0E8FE4D0DC6A2185043772086C161543C1D5153D55A21DC98DFBF4C94A002CF8A4E765D5F1C42C91C7559A3A92B5ED347BA8F92FAA5F3327255CBCE72FF668127B5AC2C4F6C61679FED5F7D5AD0B8D49C1FA3677C0BE8BD05D0331A1C9A17D989BF8G04093859D2C0DFDC8F31737652E2036D5AFBD8ADB5D71373F70182326612BFA841D1CC024BF1E91C1F270B832F0774F50B6E20FECEA786F26069B644A5067F16EA
	3E865FD1499E9D56647F231AA510731649E4BE33C8A4B2FD0A4B83E11C84DADE534D2BB556F4EBE17C247E2B38E04BBE8840F8FFEAA739C50472C9A838C864E414E05D5785FCC3GF3AEB0D94BAEB0DF3ACC698EEC455AE62F06BBC9F13F064D7FD5AB660FA78A0C7521B3304B54E7895CFB1149DFFCB1B42FAE9CA5F2D782FE75C5DC177A20B1763785823243C5539AEE636703C6FFE638DF86C9E1BD60D28210E3BB25B1DC27D702E42CA46F61E8EE6B075FED964273B8E6B4BCDA0F0CD20F344BC7A075BA86CB
	FDC45B6E5DAEE91478992D85CF17223D52AE62FE7E6483E37D19383944129341B6351718CD3AAE6936097D76CB96B6CEDB9F492F4D6A6741202BA6283AC836A52A9084FDC1152EB69B531F6879C8E44C3D04F5E8D583714DE550CE81C883E0B9F1F5C9AE27AD388CB8G36FF1E7F5838FFE2G9F8F309C004510F9A57BC8CC3D612801EB704731C69AC3EFB6CE209FFE2368909F6372C76E941ABE7BFD3AD99DBDE7F33AA565B23371460FF5DB3B5F4AEF0F8160F56BBE23CFA725EC096853F6AEE36863D01F7E5D
	98317DAC40AF7898FDFAE824506053D0E3C37ABF88D1C3BB6BC6B3D8FC606D6D2319989F84BCF33BCBEF244ECB78987375FD93E49D13201D8C308EA0379762E9AF0E6D3E51EB562DC74722542D163A50F1F5766A7955155E70DC0B55E8DF277E2876329A4D26619F2278D8831E7969DE0AA76DB8685B5807794E3E30BB8AF75FF41DF3C25A0C6FD0985F4ED09B0670B13F5DFCF69B6EC9621FE3B501D148A59A9BBFE2DEAC60DD5C23AD46383D340F652AEB60393E8F6F11367592DFA13ED7FC8557EB4EA409531D
	509E820881D8881084108CA00838EE2923E6B1EC7A3AC58AE0FA8E4FF4AE1BA263A88759351A7C93E3F9861F6D12DDDED57357BF0E1173CBD6DC03AEAF627E07629C32C293A51627FBE8ECBC84B2AFEBF29BA7A267F5ED9366F95D079366F95D09096835493909C8F99F9B5F363A0D149F6AACG06FBED9F4145C3BBC9C49F3B1F7A5EEAE8DB7BED59257D087F964557763358E857707B9B225509A8A31F4B3C399C7CE4D49D191CAEDF3839BC7D8D4A7723124D54BE010094204EC48E9B5E77CBF50BE012831F94
	BD9257D9A438CAB6F11A232AFC24CE5BC37FD5D5EA3E078CE7582FDA708C9B6B47B3AC46CB6C3D885A4BGD2B4FB6F6C453D1C8638DC870F497C59608837B1730FAFCDC676452FCD1A7B62BD13E4DF8464206210C27D6D0149D22DCF5FED9D2A64A1E661B9A265D7C5856A02D07EFF207C2CAF927F77A72357B27FB2C562661856FE1A3647B55BCBED791A0E23DEA75F58AC4F1BC5776B3B10631DCF47B52C2DA37B5AAD0E4BC120DB6CA0F49F56E1ECBB866DABGF3G96BAD80CE3FA9E9DA5B94F0082A929F770
	2CCFF288F31D04B72F9EF95793D9002B85688270B9B137F86788498169504ECABC31E749AC6BFF6992D9F766ADF59CD7D9CC74F53A3C43F6195132B87CFC03B18E2FF232B116CB366C86GC69377061126D3E2B431703CD7C29FFB35A5743CD8A269FE35DC0AF44E25D11FCD1158F9162761EDB241DB87F4BCBB67EE277424DD89FD7387B0AE6D27E705B088F54CA06272A92E965A6D9A4EC1F1F6E8BBB55C52DE0CE93EC14C7B64BA7C3611843834C1DC373AABE5463DC9722F46468F95177330B858C93D006D4F
	47E86CA47C35C338BFDF24B207209D8330E088736131BFE3BE1C70401DC2E74CDE53EFC9A976924B22A1E64715C3FADED5DAF4414AF45657234E82974C43C5F0E23DB1CFA53E70947CD7C36CC97B67CD6BA7FB679941587F4434FEB25EA20B317F77537A090D7E0A733339BD75B64EED7DD38B8577F75E08781DEC2E0B57C17CD9D033DDD8BD5A476D7E4215A661863E1519EC8EE6236D7B090D084F2EF5613AECE8A66BD2F155165D84E03F0AEBDDADB65C08CE4366E23A125F503E49DDC5DDAB0B082EB8E8A782
	2C3C0ABE1859CAE857C2DB98C61CDD734B0AE15CE79B3CB486C2BB96A08EA011508324EAB4F9B22149F5C39E8B5069E6752C9B1E31EE240975DEB1D983B3A0BAD42E83F1097CBD5600CB9D8E5F63CC5703572D4B466D159362F368DB084BB361E5F12E3317747994E9C0D29449492E160455EE7ABD145AB3815E1738B1163E5DCB62F73F2C5003466DA1F4CD3D746E5C03B4C5FE52D66130DB64A15F28DFA7CD2152CBE87F3D371D6BDF67891FFF0600678A030D40C3E087EF17F9D9FC536FAB03E175427565BBB3
	EF12AF710679D8FCFEEC9F71E31FE17C8C1FC471D386BC0B27AFEB71F48E742D75E2BE7FBBAB7155AE997C93E09640C200C400D5B266445D1F548A5748656D322F93929537A14DE573BBD8FFF35F7FFEE0433835783BDF91165C7EDCFA24BB0A35B2DB77DC9FFB46D7EF27F8DE2FE451DC8E7AFAG26G66834483A479F0FEFF7BC9B51B9F39E2E9D3D551B1C4EFEFF0F264C2C5C713E4014D6D4D7DB837153EF05FDB833875A1F836C6CA8B7D7E678FDD3BC90AAF73072E1D3585572E927AE27D98C39EAC0FE65B
	D4FF24B37F987DDEE9D5C2751F267882A5D47F4F0BD0FF997445A838AF4BDA43F32F7AD64CD5D6ABAC77DA8F4F02G2BC1581E9D5676ECE5A0FC0C78CD942189E83AG367036B3200F0F0DA363D362B39F405950A97613ABC50EDFD0194B7160387D72E1F90C617F43C36E63686DB2E7E0786663B9086755B243579766F1B9A41CCD5771587B8506BE54436E280E4846335947EDB0BF00F7D8B943467EF85E3FC26BDFBFE1463FDA6B1F791ED9FF1A567F5B574D64672A36EC96C7E750140D5B30175CAFF82551F2
	6DDA2B5D856ECEBE38D6566F2FAE5F372F8A77663D2346FB280359BBA25EC3152B3C46614F293E6DDC5F195E8386FD3C442AE167C08D1C6452E17A2ADF5551E52969F70B0E61AABE0EE649B80E5A499753B3A20C430D7A8D63E0B2FEF249A823606ECCCBFA8499891CF7092A67FEAC4F486D43DAF0AD743DC96787E70D207FE376FE027E3E0A697DC5AE463732073FA834BF7BDC7A6E88FAAB02CC1F9A54758A03FF31329870BDF24D494F129F9A5EFFD3972D7E7E3C40F4E46F3B15BA78EF032C11EB604CCC739A
	B8A7537CBE26B0B3FAED3AADB372F73672CC63F7B67E8DA8076C2F84D817E5GEB810491DBF671881EAB0FE43EA550F34564CCB96A9CA4B5B87925050A7A6F4A0ABC5785D9577EFD46FDD944417AE588DD9EA29F718FE61175D2E59FC5B2DCC2963111629A9CD299A947FFBB0B782DDB9A20E816873FC7F376DA18D767886646F764E3EEFCC5437DA00F6096C1FB59881ED1E3AD3ACF4EB03CE7375CFA7F5CFDCBF530B97FF3B95AA1D91B7349E2FD1E678D6FA9C537FE4EFF3825BAD8BEF0299573016AD12863C7
	713E787ED62CFF970EE23D75B425DD896D35G56B124DD580BB133EC4CACE62636C60E23B2B90E712EEEA042DD9DBD26192E949F6A320FE1EDB9685EE14E07C7B87FFEF6F8D87BAD25B6767AC91437D443FD77BD442D57F0D5C5485BB00E79F6995DB7EEE84F9947B871430D6F4233DB3C9A7E258F10DD0A6C1394F51CE16917DE9EFF36EEA605D03CA6A7E753DBD16410FB7633907A3C03983FECA4F5617B7E542637A371C2347A88CCF42A7FAAD473F6F1D0733542BEE37E198863D8B19E7D3E18FF73A9A4795F
	EA2095A6203686E882688298821882188B908710883092E08D40FA0002C9DBF6B9C0834031C91CF7F6CED514F9734BA19AEC9551781DE0F5CE64F87BF24E35636D3A9C7A2D976224FD5C476FBF7F10C33FBD8C8C84A4AD2EEF48A950F09D01AEB960A217A2AC8FBA31BFF23DBCC0636EE5186750A4664EF17918BB4F1FB44F1D0D75EEBC50A4CE6A39730A49505CF9D59E49F9BDAE2F4B9374D06DCCF7AA0D6549C03FC6537DCCB96AAE1EB257ED4C5DAB0126E9CA575DB595DAEB0B2DB7D7B34BB74997FF43FC2C
	AE5A52CBE258652950FAE9A7458FCD0556CB4FEA71D1053ED5D3F8AE5C57EB545F7489787CA751778B63FB51E7B6EEA7679DB3114178BE9C0EB65F5808FC87A74D786E65740BA372FD7FBDB33EE51CBEB9A2DFCDD154BA56040F6D2BDD7964DD4B67D8E864FD15796460A53FF1F209EE57847D09860BFE2CFF47E632CF83BC9E32333C12667156CF59FAB25C9E0AAB63B8467BC1E16438F122705AF1233B50BCCF3BDC981E27090566F95AE021B1CFE3B8F7A1597B03906FFAB4BA3F063B2061C628CC02BB6FF22A
	C39AFE0A0E0B600FC9C4A162EF23639A25389E0E7B0281F70163E28B09CDE14AC33222653405640C9AF0395D2C7DE5CA6372C2A8546CF1B79D234BA79A46FD0F06A363E6673E032483BCEE5D16DFAEC42CD76B7655D61757B7B4D431F37819FCBC1B8FFEFA3D39C6EAAB53F91504AF2E7F9BA53A7EDFB4DF3F7E2CDD285FE72ADF9022540C64CFF978BE59512B3FD7985E1FEB3D75F965EB37D4875BEBCBFA43775F77FA0D7B0F61567B709BD27C27D8FB08FE4C4397EB385EE144D9BFC35C29C96435EB38C513DA
	AD23617E0D66F30B201D24615EBF00F44BB55C51A2441545E0EEA97E1944FF9EFB6896E378B27A1B850E47AD466D7B73FCE38CF4032C18186BB99BF2F699796683CF7CB57818DFEDA0E9B33FC39F7363391EE8429BFA2E639DBA5ED72C08514FBFA6AFB80C7212CD64E50448CB0D516F64091D2CB3F4F9EC2C3B366BE3FDF292E5974C38F67E93BA560A9921794FAB6FCD7F3670239D3A2EB387D0D7ED84DD46FB4AF620A1003AFAE6045A65F2914AF31A484B8801B1AE8F6D92EB1047FC6FB77938DFFEEAC15CEB
	7938DFF624A36E2DDDC8772206FBE797521D977037028F79B1CEAD1801F704D672F38A5E7F40B0762F18E19EC7994FBBBBF51EB42DD65AF89B4A7C68CD3DFF66816CBFF4DB343B33C74BF51EBB0A345FDE187260B85612BFE1647B64913FF11FAC823E2537DD4F7EFAF8DA8E358AF856DE975F1F2665D042CCDBF6714C6B611BF34048D789BCCD57457725E9B9D49770B82F41477C6089FFF84C7D31BFBC66BEBE9CCE7764F0B8DD71D4B85D3629F03AFB8E0453459D8827FB28A81C6E11A2A39D7BDB57FB1AC720
	11700A1089B1B520882F079F4BEDF77E6C59043E76E20BE069903206E87C6D90B2C7C9009EA3FF0DC40E775D403BD9636D20F9CA4AC8FDE3468B77E7D884ABB0323C2DC33012648B7895A6A78372B6AA45EA0400DF7814C5A283D620D898EEBAB9D347B41AF04EEB5028C2A6797250288AD974FA9CDEB2C98D5798C564AD40621DF655E7463B7434596B53A3FD97081F4DFCB3F1F6686FD36F22F5E79A600BEF477AFEF66B9EA1725D44D90F68F62B22FB384D6BA477940747D5C93F537DCA9E119FC09A5E497A3E
	1EC746FD554073FFD0CB8788954FDBF67694GG14BDGGD0CB818294G94G88G88GAB04B6AC954FDBF67694GG14BDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB095GGGG
**end of data**/
}
/**
 * Return the ColorButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getColorButton() {
	if (ivjColorButton == null) {
		try {
			ivjColorButton = new javax.swing.JButton();
			ivjColorButton.setName("ColorButton");
			ivjColorButton.setPreferredSize(new java.awt.Dimension(35, 22));
			ivjColorButton.setBorder(new javax.swing.border.LineBorder(java.awt.Color.black));
			ivjColorButton.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorButton;
}
/**
 * Return the ColorLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getColorLabel() {
	if (ivjColorLabel == null) {
		try {
			ivjColorLabel = new javax.swing.JLabel();
			ivjColorLabel.setName("ColorLabel");
			ivjColorLabel.setText("Color:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorLabel;
}
/**
 * Return the FontComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFontComboBox() {
	if (ivjFontComboBox == null) {
		try {
			ivjFontComboBox = new javax.swing.JComboBox();
			ivjFontComboBox.setName("FontComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontComboBox;
}
/**
 * Return the FontLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFontLabel() {
	if (ivjFontLabel == null) {
		try {
			ivjFontLabel = new javax.swing.JLabel();
			ivjFontLabel.setName("FontLabel");
			ivjFontLabel.setText("Font:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontLabel;
}
/**
 * Return the FontSizeComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getFontSizeComboBox() {
	if (ivjFontSizeComboBox == null) {
		try {
			ivjFontSizeComboBox = new javax.swing.JComboBox();
			ivjFontSizeComboBox.setName("FontSizeComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontSizeComboBox;
}
/**
 * Return the FontSizeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getFontSizeLabel() {
	if (ivjFontSizeLabel == null) {
		try {
			ivjFontSizeLabel = new javax.swing.JLabel();
			ivjFontSizeLabel.setName("FontSizeLabel");
			ivjFontSizeLabel.setText("Size:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjFontSizeLabel;
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

			java.awt.GridBagConstraints constraintsFontLabel = new java.awt.GridBagConstraints();
			constraintsFontLabel.gridx = 0; constraintsFontLabel.gridy = 0;
			constraintsFontLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontLabel(), constraintsFontLabel);

			java.awt.GridBagConstraints constraintsFontComboBox = new java.awt.GridBagConstraints();
			constraintsFontComboBox.gridx = 1; constraintsFontComboBox.gridy = 0;
			constraintsFontComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFontComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFontComboBox.weightx = 1.0;
			constraintsFontComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontComboBox(), constraintsFontComboBox);

			java.awt.GridBagConstraints constraintsColorLabel = new java.awt.GridBagConstraints();
			constraintsColorLabel.gridx = 0; constraintsColorLabel.gridy = 2;
			constraintsColorLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsColorLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getColorLabel(), constraintsColorLabel);

			java.awt.GridBagConstraints constraintsColorButton = new java.awt.GridBagConstraints();
			constraintsColorButton.gridx = 1; constraintsColorButton.gridy = 2;
			constraintsColorButton.fill = java.awt.GridBagConstraints.BOTH;
			constraintsColorButton.anchor = java.awt.GridBagConstraints.EAST;
			constraintsColorButton.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getColorButton(), constraintsColorButton);

			java.awt.GridBagConstraints constraintsFontSizeLabel = new java.awt.GridBagConstraints();
			constraintsFontSizeLabel.gridx = 0; constraintsFontSizeLabel.gridy = 1;
			constraintsFontSizeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsFontSizeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontSizeLabel(), constraintsFontSizeLabel);

			java.awt.GridBagConstraints constraintsFontSizeComboBox = new java.awt.GridBagConstraints();
			constraintsFontSizeComboBox.gridx = 1; constraintsFontSizeComboBox.gridy = 1;
			constraintsFontSizeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsFontSizeComboBox.anchor = java.awt.GridBagConstraints.EAST;
			constraintsFontSizeComboBox.weightx = 1.0;
			constraintsFontSizeComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getFontSizeComboBox(), constraintsFontSizeComboBox);
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
			ivjPointSelectionPanel.setPreferredSize(new java.awt.Dimension(120, 344));
			ivjPointSelectionPanel.setMinimumSize(new java.awt.Dimension(120, 344));
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
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) {
	dynamicText.setPoint(getPointSelectionPanel().getSelectedPoint());
	dynamicText.setFont( new Font( getFontComboBox().getSelectedItem().toString(), Font.PLAIN, ((Integer) getFontSizeComboBox().getSelectedItem()).intValue() ));
	dynamicText.setPaint(colorChooser.getColor());
	dynamicText.setLinkTo( getLinkToPanel().getLinkTo());
	return dynamicText;
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
	getColorButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DynamicTextEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(265, 485);

		java.awt.GridBagConstraints constraintsPointSelectionPanel = new java.awt.GridBagConstraints();
		constraintsPointSelectionPanel.gridx = 0; constraintsPointSelectionPanel.gridy = 2;
		constraintsPointSelectionPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsPointSelectionPanel.weightx = 1.0;
		constraintsPointSelectionPanel.weighty = 1.0;
		constraintsPointSelectionPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getPointSelectionPanel(), constraintsPointSelectionPanel);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 1;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(0, 4, 8, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsLinkToPanel = new java.awt.GridBagConstraints();
		constraintsLinkToPanel.gridx = 0; constraintsLinkToPanel.gridy = 0;
		constraintsLinkToPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsLinkToPanel.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getLinkToPanel(), constraintsLinkToPanel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getPointSelectionPanel().getIvjDevicePointTree().addTreeSelectionListener(this);
	Font[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
	for( int i = 0; i < fonts.length; i++ ) {
		getFontComboBox().addItem(fonts[i].getFontName());
	}

	for( int i = 0; i < availableFontSizes.length; i++ ) {
		getFontSizeComboBox().addItem( new Integer(availableFontSizes[i] ));
	}
	colorChooser = com.cannontech.esub.util.Util.getJColorChooser();
	// user code end
}
/**
 * Creation date: (12/18/2001 3:46:27 PM)
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
		DynamicTextEditorPanel aDynamicTextEditorPanel;
		aDynamicTextEditorPanel = new DynamicTextEditorPanel();
		frame.setContentPane(aDynamicTextEditorPanel);
		frame.setSize(aDynamicTextEditorPanel.getSize());
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
	dynamicText = (DynamicText) o;

	if( dynamicText.getPointID() != DynamicText.INVALID_POINT ) {
		LitePoint point = dynamicText.getPoint();
		getPointSelectionPanel().selectPoint(point);
	}

	getLinkToPanel().setLinkTo( dynamicText.getLinkTo());

	for( int i = 0; i < getFontComboBox().getItemCount(); i++ ) {
		if( getFontComboBox().getItemAt(i).toString().equalsIgnoreCase(dynamicText.getFont().getFontName()) ) {
			getFontComboBox().setSelectedIndex(i);
		}
	}

	for( int i = 0; i < getFontSizeComboBox().getItemCount(); i++ ) {
		if( ((Integer) getFontSizeComboBox().getItemAt(i)).intValue() == dynamicText.getFont().getSize() ) {
			getFontSizeComboBox().setSelectedIndex(i);
		}
	}

	
	Color textColor = (java.awt.Color) dynamicText.getPaint();
	getColorButton().setBackground(textColor);
	colorChooser.setColor(textColor);
}
/*
DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
	
	if( node.getUserObject() instanceof com.cannontech.database.data.lite.LiteBase &&
			((com.cannontech.database.data.lite.LiteBase)node.getUserObject()).getLiteType() == liteBaseType &&
			((com.cannontech.database.data.lite.LiteBase)node.getUserObject()).getLiteID() == liteBaseID )
	{
		getTree().getSelectionModel().setSelectionPath( path );
		return true;
	}
	else
	if( node.isLeaf() )
	{
		return false;
	}
	else
	{
		for( int i = 0; i < node.getChildCount(); i++ )
		{
			Object nextPathObjs[] = new Object[path.getPath().length +1];

			System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );

			nextPathObjs[path.getPath().length] = node.getChildAt(i);
			
			TreePath nextPath = new TreePath(nextPathObjs);
			
			if( selectLiteBase(nextPath,liteBaseType,liteBaseID) )
				return true;	
		}

		return false;
	}
	*/
/**
 * Creation date: (12/18/2001 4:16:51 PM)
 * @param evt javax.swing.event.TreeSelectionEvent
 */
public void valueChanged(TreeSelectionEvent evt) {
	fireInputUpdate();
}
}

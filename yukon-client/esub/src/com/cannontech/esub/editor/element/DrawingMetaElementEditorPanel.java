package com.cannontech.esub.editor.element;

import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.functions.AuthFuncs;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.esub.element.DrawingMetaElement;
import com.cannontech.roles.yukon.SystemRole;

/**
 * Creation date: (12/5/2002 4:16:03 PM)
 * @author: 
 */
public class DrawingMetaElementEditorPanel extends DataInputPanel {
	private javax.swing.JLabel ivjHeightLabel = null;
	private javax.swing.JLabel ivjHeightPixelLabel = null;
	private javax.swing.JTextField ivjHeightTextField = null;
	private javax.swing.JLabel ivjJLabel1 = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JLabel ivjRoleLabel = null;
	private javax.swing.JLabel ivjWidthLabel = null;
	private javax.swing.JLabel ivjWidthPixelLabel = null;
	private javax.swing.JTextField ivjWidthTextField = null;
	private javax.swing.JComboBox ivjYukonRoleComboBox = null;
	
	private JSpinner widthSpinner = null;
	private JSpinner heightSpinner = null;
	
	private DrawingMetaElement metaElement = null;
	
/**
 * DrawingPropertiesPanel constructor comment.
 */
public DrawingMetaElementEditorPanel() {
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
	D0CB838494G88G88G660305ADGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BB8BD0D4D716D6166CB2D948182C9309E1B24C46CCB995E6466CE84549B0BB4CAC35CBE6129AD3B1952742EC610C356BE4B5F1B3D4162DE5A70F1F2208E2C4C3943511861A0FED23A4A222C2537C942101D6F1029B9B7A892DCDF7CF77EB01044D6E39773EF35F6B6E5700CE39D5CB5529776E39675C7339671EFB6E6D07D0F2B4A1BFBB27C490327385796FF436A02CE991043F28EA5E0238359F4596
	8A2A3FF2003D026FE89EBC5321EF65155842E221E7F78134B3202D334596FE886F7B043E5D3AE6F809A0A7730CA044ADDD7A43396558391C6D347D12FF97BCF783E4822E6CE0EC6144C1A2FF033FD44637C2FB377CCEA0BB877422B42F0E142A7009B2DD3A7CDCF40850E4794B647E96E8939A9BBCA78EA9E36E205ABD5C5CA437C79EDBA2E47C2CB0270848D20D0F10AA73EE161FBA612F28A3F2058B1515C40E754E787996CBC7E75B15DE5BF91B68171A1BADBE7B1453BDEE75F93C22CFF20AFEA2270C6877EF
	A38514D7907450E63A3CDA54A24BDAF2E709107E4C6F33A36912D046F4C8A9D3F4E929D916A79DA6E3D7CA3BE43EBCE837GB01913924ADC833826A38437F30450EE05F68134F5C7D017F355284B70974F881923461CBD91F419BD42F4D8FEC451A54DB11973911D67ADBAB2D7695017F7B43630942085E084E09660900D2F95C7EB6159B6AA35FBA6A7BD6EB62F576CF278AEFB9C228B5E1B1B21C7468D0AE39E1FC390D88CE50D54EB68033064E8F07CA8F3FECD40B9CF9CDCA6C812BEA7DF4336D52F97652C10
	47C8BA2A58466C7A4AB1E2D79A60B3810AF531058600EE00899D5A756E51D37359556316C41F5B6E9A8A0CFA6DBE499CF03A9C22CF362FCE1A4BBEADE0FCE76E190F79E545A935DF523EFFA2E73B06DF52D61464C46918BF62F50ADF18ECDFC459C93A7079206BD6C76270CB9A07EB61BDDB0ED1060F26F8FD3902E7B1DECC71245D88FDF1659837433B51065E3CC705628613268DF1BF2D4C49AAE77A149C8B1DDBD1CFF2C7AB60C7GA4009800C400D40034E3B83F67F4A7969837D6555CB25E77F48760A9CECB
	BEFB0F5BAF595DE362D53B4FE99FF511A441E85E57915DC70378724AC95C0D010CAE51AD7A6C12536306516D13A284121F5194156FA5B4BE11765A1C13100B6C13DE81475DDF3E8F1EAE3BDF3A62F558A5D121E07D074B09AF6F5A2F4208B0G3C57D7901F6F3BC172C79658DF8AED662B47AB894E8B6D688A74CBDC7987703C8401AD3AA087BA47089AF65F8C4AFF7C9849A562DD51ADD9C42F68F6400BDF6EFB72588E45AF37BCDC67EFD0BCD9A36D20381F631FBD361761E18C3517317FBB345FAEC9BE67E8C0
	9207C4C902042C702F263A7803161FDF1057F8950931F830EB95C08A376FFC45BE15FD3D9E4F1D0057C6E6CE7653D7E81EDD8F6FDBABF03F0B9AA17BDF099E63746DE10CD3CB5113C27D6BC64DB8A55028E771592B8FDDEB0F52B1959987C60667D863A5FA6D75F7EF3965441CB2F27F047189B03FE7D5933FCB206FE20015FA747BCC050E2DB7335DAF628A6B77410C098EA54A982DC79FB9466E6A35E3ECD62F9DE38F69670E21C774DAB1447A9E53133D45B365D64564937A8F689AF63BC52AB4E791D8BD7136
	0D784269F6CA571C0EF1D19AF2FEAE42BC6BC9EC5552B5188A6FE9BC1626AAB016741FFFCDC87EEC0766FE96772CA127311245C0F72592CBCC272515446722CB1C84C735FB82EE09613FCE57370C17734BD72B34E93FD6C562710EB8A353BDD935CDA0F944959039EDAB6D24B8747A1C13B0D5E4414AB5DD02242E69A6C0BFD27F4540B3810071F65713F9502A29D85CFCDCC374DA8374AF83EC2C443859DF7D11EA3D4A81536EF139187B793C64980A49BC0AFF8C08100E49023E44ED59E178A0246F2AEC976B5F
	EBA0F639D439A02B125855F683EBAE32D7B05CB045F9C059FF580EFB6F65E2F59D6A7017EB4EA10156AAB6F7362A50FD6BF6AD99D782FCD4955A9EE3D0672AB63F5FB366245B02BC9F3F2BA1FE27356BED7BDD7B3451CF1C5B5CFC51EA878ED6B76E8C10F189FDDC95560D7AE93ADFC3FB9300D00D724E560447CBEB4DCEF85EF27A7CD06FB844E974D9E78D311D6CC3B29A6B3313EADFA4276F0F680B52EA66037A6AD0DF2C2CA3FAF483FE046B76131A3DAADFC0AD895B6EB84C946A63294DE571AB377D357998
	3F82663FBD01507A46DB2D6455286A709C4B6A18B86A2F38EAD64764F37CB3941F2842337A261662C9BB997A74865CEF3FD5CF6C0A06F61C816DAA28ABD259E575B85D9237E9F59DD9039E1FF35C69C65C0B948715B4CDCAEC4F2A22F33C8246CC82E038FF3DCBF069504EB4609E7374B441155544961AEA9077ED0A33C0FB0263CC53F36D93049F5FCDB5773EAFB199BDF34AD06FC57977B87EEE6E3F676A493CCC737677E8FBC69E9FEA4040B69A43B31EGB8BD3306784D9640DC96836D65GAF83244BF632F5
	777C9449973720F00365B664954704F5350437E00A1FFF21ED8310G16G2C82E0B2179CA07633955AA06BF1E7C4411D14F36A1B5321B915548AE5F5B0F7G0C66A93D96CDB7740FG30D81C8D60F9D282DCE29D6663061B24CE663943E81F02A3EF174F69B05B47FB29C7D87EC82131C578B756E17EF8170ED9D69FDB588850DB0FFB59F4951EBB933F7528503B680466F9BC5DF6A047D14F7C99DD2F2C7D84B8F7B21946A91419827DD97584B7B2D592929FAF8101911E2C7F5120FE12AB4839DE69CF8E6AA77A
	5664287B5F8D6AA7BEAA177919EDBF2FC45B32FE9EAF38D69F0EF857E0B94EEC1A101F452603425A3F8E48366D1BA91057C042F174678D3A772D0776D60082A31E79DF53A32D4E08348DBEC2BB8CEDAFC03491576CC3E139EDE16BF071FD723DF44FFCACE7265CA57AA798D94E4C6678D7A8BED105E7711B4963F78D74151D40DC6AB21258DF846DF800D5GAF83EC86A8B0E1EEEDBCEE9266B96359BC5EDEA8B4DD2A6387336F54547D1D43FF329D63E4550BCF88EB7E7CD759059162244C446223C5FE2EB09E24
	F8E65F7BA708FDAE680B86C884C881D88F30155BB7FB3C1E595761F0CAED12E49F1B20BBB79AF755A9CEA9E8122818ED25B3EA5B729A60CC582064717A06C87B606BB4CEDB9B589C95F17CC60AF72870EC6E46A81E342321AF2381E3F53DDEAD7F66C948D5A7951951A7A349FF1646FE7CC9A6E7BB47BFCF712BD4F8A67FB44513764B50E7BA0572FFF6D760F17C1A6A7D7701397624BF95180C8F6FBEC899ACAE8246486727D923767969A1237679A95AB87779E931B17279E929D1FDFEE2F15D51C0726C92180B
	15G6BGB681E41D42182EBCF10C4534C6BCF740012A5DC3EFDCA414DFE00AEC6B9E537C77B425A6D25F0EFAA449B3C946C77C91930DA30F17A2992E42C4FC8465580444C8E5FC0D0964E917F80B22D97DF502667952C628479A31A61B35E21DE66538778630CE139AB1BE5F24F5FD8A3453GB2G0ABF86FA006E0F1166ADCA6300F69440F20094000DGF91FA00D3366A6D94FB2DFE82C10F149B32DF1E1B5DCCA88DD31BC26FCC7C2E511DCD794A17649F32135DC68DD448E7949FC760E9EFD2B7F847D38268A
	FD5B4DF1193568DB977749F3B4A7A4C1FB9DC086473FC0718527E38B8DG5D2791FF4EC756AB21A5BC8BDD07998B24E5B22ED299799AF64F473B701CF039A172BA31B54C3FCEEEB45033A14437ED46ABB25CE883BD1F5C3A659779FA9CEFA866389E3F5563F71283AB2BF3BBE7365135662317FC2C5EAE6F439AF682FC3E88A089E06DE966BFC6732C95E9B61DE6B5395094DBD882C04EADD29321A99A34E00CB4057B9EEB8A74F1AB5058G764B6D09A6256EE03A7E668B12972EB99D52C42FFDD4F4A17E05B192
	635DA2C9822C03618B3377F03C8DDC596994DD8E396F7B6D453C4F6A1C96DD141175FD7D4BBDA8C7691279E20AF7E31FB22410C16D55552A6B55A459976970E47D27DDDA7DADE7307F3747547D56B32CFF1A77EF1954620F617DBF2E506ACF607D1F393546DF85CFB6272515B82729E7583C27935EB36AF95FD005B43B1A593C6B6159544C4E2AEC5E7754613CDB1B4367BDB784C2675D519CBA6F5F3BCEF24D2047A54A53AE63FF5DC776416B01BB9EB769040AF254E376CC737E0C7D748CC7199A64B9FA4B976A
	A322373799FD3457206E0F126DCA647DDBB479131B71EC77FE9F7E0E328EF03B4E62596EC7BBC95DCB4FF6E31EC963185D6D7638A5F1EC42B8C68BE163F840E98CC8CED7F3334DA939C4B3DD116C4C51550FF7DE1633F866F8331F4819057692004433A87F95AB5666A9004BBA0B67167D83F8FEEBEC41B15A87882D8B5AD1G71ADC8FBFE906F27D6816EA875A564E1817F36DE5042EDEEA13FD3BFF50134D85D3DE197565DE54FFDD5303C712B6C8AC13B6EB61D13FFEBBE271CBD058B4DF9721D5A2D88F7EAF4
	E6194E57EAD167B567706C7B5F1D6A5FFF13769C4809D477EF1565961FD764170AF876F53BF0EC6BF91C4FCAC31936CEBCABB0DEDD851E97234F23FF75EE9CAF19635E2BC0DCA647BD61C6DE53851C5F56CA0C2F8940A5DCC05A68CA0C117597F0FEBF24774A452D30FF8234366298BB2AB1C606813738954798284698C9ED45B1BAEA892D50866791GD39B523656213E5D00E3F3BF52C6C8D9FF399F7B9735214F46DD0734FD46D7AE636DB6705F5539BCBD9FEFEB9B5681797D731D878D66607C4378764D4BF7
	A4244D78FA6765D30385E43333D0BFDD134366507375B045BB4C21676B8D7DB8BFDE68DBE7C6FBDF335E5F3D40067B64BBB8F0FF7789AF503C52549EEAEFB245DB5AC36D6D99C0FB2D50372A9D6DED9FD44BAF35C08DE9193B06E36B7E971F2A6F3B0B176567203CD07AD6F93CDECB68FD77B2B359439681BE9EA099E0BD40E6003C8ED88BG75G2DG43GDE00A800B800C400A40035G9BBB708C15FE21861ED76D37659B8FF2BD607151D394A065CBBEFA10725959FDB37341C82D569CE4F6B0FDF7F5067B03
	695FE5A17B148E7ADBGECGAE0018CE542736F55EEFA9BA26677816621713DAFAA54A7AA4EB684578AAAB34785649741BC27832BAB196EA5CDAFC45DD0CDE5F95C95EFFE84AEB1269FBA372BD25A96F26CCAF0570C5F7211E1D15771713EC7372E9672458F97954905F05B969AF695E3032ABF40DAE2178242E50B5FA388A57E8AA7455F7233DFB2A6FAF379C3ECF3EB24D75B097B03BFEC07731455D21767E1462633BC36D3DD40B762E003EDDBDE8EFC71DDA3E9770D1BDF367A446D7655752BBCE66CD549803
	71DDB64CE5EFF2C43E9ACDF96BE47ACD9AFC07853267A66FA3F785CE374B6906021AFE95A36099FF0C5621D940DBFC917DF145AAF0197F25FA7F677E87FFEFA7D89F3C0C9F8DA8B67D3D6A3DFF60414B1EF820B2581C7E01567AE597D97CB35CE79457A86358FEF22B03584E760E04B672BB0E66DE865B46C5A86D7AC7EF0BE3124DBE4EF93EEB0EFC57315ABC7FDD475F1A356F84D34C61F702AF19356F84FFE1D65F89B25CBAB329A947E17F9D66F43F64386B9C770FF4CC0223E7FF0E5FC875A2F8F617007837
	28DED394B7AC635ED1612E4B388CB339FF8113A7BCBE647DF7B3796E63165365E26D7F24B4CE37DF1438BFF25A090ECE2FDD2577F60E3B4E6752CF4FC5D6184B1B97718EEE3DF287F7916B01FECB647939EA513E1F9E31E85FCF0FDA667E066C36A5527DB553EF6BA46A1C44F59EF0234E299C57C24FCC251760FCF389F15F2CC03AC60EFB1C0E97856DB80EEBF5230CC405174BD843F19F68112E2097F1FEBD5215F05C5F38D19787476D73A01D1763BA7428CBB247AD2F443C3A2E97736AE3D5824F834BD46F95
	D58FBE6F347C9F4878F12DE2D32A6AFD2076414B3E7DC0E530B99D298B4F2B76BAF5DEE538F3FE0C2FF49E8FCF7BB1EEB6F31C430038164BBC06B9EF8C47FD137346DD466F9BBE6F275F4110EBA5F832337A2B1D44676CDCEE27359D79BE4EE9F751744670E553C58A9E531BFC3776C8AD49D192A485B65E2EEEB29E3B7FFB27966F7FB6DD0EFC6F0B77FFD9C0C3G6F060A7B14DA0C0DAD75A8E3CF38F16C32BE6D31F367985B5497FA37B8D491EC47520B0A2CB595A82BC5C3D668BD2635AFD816A3CC5637A7C9
	7C0DF9DC01C937DFD854C74F44B4B751EF787A709E6545CBAA9D383DA99ABA648B41B62F8F51E1F318AF5F6DD546FEC40FE3E7C698BBF70E31F775070E3D3FCF993B5603E31776EB7BCE2DF7286F6AD5E3B3BF3975682726FE74D3DF3F40E54FDA95592B7DA83BDBC3F624785BAF63C7D43259F8DF98F03C098558B25B2F0CC7FC38B84CCF5F98D04DAF57B5DEE36C5079DD599F2CEB0AEAEC793E115F4B2D6D573A3BC59DDE9994B84F137CBE32400A3E5DBA24747F165FCD36DA679A734895C140D8FFE3126874
	3100F7B289D36ABB19261D07A2FE03B8EDE5FE08111F337603288757B3C5D38F3C039E3926687EC3BD72E4F15B9E99D67ABF75E0FF63G76FF673AE24743FA3403517F933F07F4F17A35FFD046CBB4E07F720139FC753BCFD57E65E326E97220DD87EE1016FC0F92F48EAD99049AE8F0A1675027036EB5DA00473AA03E37036ED19C40B3BD8F9FBBB77C9D7DB6B786E8D78CE22DB9F625CE08FC97A6FF08BC4330742BE054A1C877D498AFEDC13AA787FD5B3295F872069642F75393F47F88BC758B627B172033F7
	AB7058964477FC50D9FF82F8E6974477F6103C454033FC9EBE39FE5628D15AB4EA14556EF03A975D61F4797AF03A1DFAB59D1B7F549BC487DE7F33A946B35A8DF2EFA9579C4E4F692755C3C0689E17E5F8B4E4F842F5F9C3C317B4FDB85D1446F8339A6365C861F45B25F03AD4C3B85DAB868DDFE90C37B3E8BC763F22EB8F7F99B492378879FE1F3087BF8BB5765043E07A5DD34D393FF9A6B7C7486B91363143F10F10C7CE38BDC2BEBDG778839F0B81EA67F2C1427867F9F05AF85A194AB89F9641FA3A4E19B
	AD831BA5E1BB2B7B60AD77931389BA3C6497F13F6AA52F41C4BA8A064431004FA94DC8C2A17DC9D6C7FE1385BEC1DB547FB72C3C0F557C0FFA9F4BFCB8ABBB52BE16F1052D41022B213FADB8F5747BEE405F3C0AB97187C70F8B116FD406A66DAE17E4F75DE9F3BB487D0AF9C692157336DBC74677A30D5CA9FFF726A3FA5FD6717CAFD0CB878875918E176193GG60BCGGD0CB818294G94G88G88G660305AD75918E176193GG60BCGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2
	A0E4E1F4E1D0CB8586GGGG81G81GBAGGG9B94GGGG
**end of data**/
}
/**
 * Return the HeightLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHeightLabel() {
	if (ivjHeightLabel == null) {
		try {
			ivjHeightLabel = new javax.swing.JLabel();
			ivjHeightLabel.setName("HeightLabel");
			ivjHeightLabel.setText("Height:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHeightLabel;
}
/**
 * Return the HeightPixelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHeightPixelLabel() {
	if (ivjHeightPixelLabel == null) {
		try {
			ivjHeightPixelLabel = new javax.swing.JLabel();
			ivjHeightPixelLabel.setName("HeightPixelLabel");
			ivjHeightPixelLabel.setText("pixels");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHeightPixelLabel;
}
/**
 * Return the HeightTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getHeightTextField() {
	if (ivjHeightTextField == null) {
		try {
			ivjHeightTextField = new javax.swing.JTextField();
			ivjHeightTextField.setName("HeightTextField");
			ivjHeightTextField.setText("600");
			ivjHeightTextField.setColumns(5);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHeightTextField;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabel1() {
	if (ivjJLabel1 == null) {
		try {
			ivjJLabel1 = new javax.swing.JLabel();
			ivjJLabel1.setName("JLabel1");
			ivjJLabel1.setText("");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabel1;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitle("Size");
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setBorder(ivjLocalBorder);
			ivjJPanel1.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsWidthLabel = new java.awt.GridBagConstraints();
			constraintsWidthLabel.gridx = 0; constraintsWidthLabel.gridy = 0;
			constraintsWidthLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsWidthLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getWidthLabel(), constraintsWidthLabel);

			java.awt.GridBagConstraints constraintsHeightLabel = new java.awt.GridBagConstraints();
			constraintsHeightLabel.gridx = 0; constraintsHeightLabel.gridy = 1;
			constraintsHeightLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHeightLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getHeightLabel(), constraintsHeightLabel);

			java.awt.GridBagConstraints constraintsHeightTextField = new java.awt.GridBagConstraints();
			constraintsHeightTextField.gridx = 1; constraintsHeightTextField.gridy = 1;
			constraintsHeightTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHeightTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsHeightTextField.fill = java.awt.GridBagConstraints.NONE;
			getJPanel1().add(getHeightSpinner(), constraintsHeightTextField);

			java.awt.GridBagConstraints constraintsHeightPixelLabel = new java.awt.GridBagConstraints();
			constraintsHeightPixelLabel.gridx = 2; constraintsHeightPixelLabel.gridy = 0;
			constraintsHeightPixelLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsHeightPixelLabel.weightx = 1.0;
			constraintsHeightPixelLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getHeightPixelLabel(), constraintsHeightPixelLabel);

			java.awt.GridBagConstraints constraintsWidthPixelLabel = new java.awt.GridBagConstraints();
			constraintsWidthPixelLabel.gridx = 2; constraintsWidthPixelLabel.gridy = 1;
			constraintsWidthPixelLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsWidthPixelLabel.weightx = 1.0;
			constraintsWidthPixelLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel1().add(getWidthPixelLabel(), constraintsWidthPixelLabel);

			java.awt.GridBagConstraints constraintsWidthTextField = new java.awt.GridBagConstraints();
			constraintsWidthTextField.gridx = 1; constraintsWidthTextField.gridy = 0;
			constraintsWidthTextField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsWidthTextField.insets = new java.awt.Insets(4, 4, 4, 4);
			constraintsWidthTextField.fill = java.awt.GridBagConstraints.NONE;
			getJPanel1().add(getWidthSpinner(), constraintsWidthTextField);
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
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("Security");
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setBorder(ivjLocalBorder1);
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsRoleLabel = new java.awt.GridBagConstraints();
			constraintsRoleLabel.gridx = 0; constraintsRoleLabel.gridy = 0;
			constraintsRoleLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getRoleLabel(), constraintsRoleLabel);

			java.awt.GridBagConstraints constraintsYukonRoleComboBox = new java.awt.GridBagConstraints();
			constraintsYukonRoleComboBox.gridx = 1; constraintsYukonRoleComboBox.gridy = 0;
			constraintsYukonRoleComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsYukonRoleComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getYukonRoleComboBox(), constraintsYukonRoleComboBox);

			java.awt.GridBagConstraints constraintsJLabel1 = new java.awt.GridBagConstraints();
			constraintsJLabel1.gridx = 2; constraintsJLabel1.gridy = 0;
			constraintsJLabel1.weightx = 1.0;
			constraintsJLabel1.insets = new java.awt.Insets(4, 4, 4, 4);
			getJPanel2().add(getJLabel1(), constraintsJLabel1);
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
 * Return the RoleLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getRoleLabel() {
	if (ivjRoleLabel == null) {
		try {
			ivjRoleLabel = new javax.swing.JLabel();
			ivjRoleLabel.setName("RoleLabel");
			ivjRoleLabel.setText("Yukon Role:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjRoleLabel;
}
/**
 * Return the WidthLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getWidthLabel() {
	if (ivjWidthLabel == null) {
		try {
			ivjWidthLabel = new javax.swing.JLabel();
			ivjWidthLabel.setName("WidthLabel");
			ivjWidthLabel.setText("Width:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjWidthLabel;
}
/**
 * Return the WidthPixelLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getWidthPixelLabel() {
	if (ivjWidthPixelLabel == null) {
		try {
			ivjWidthPixelLabel = new javax.swing.JLabel();
			ivjWidthPixelLabel.setName("WidthPixelLabel");
			ivjWidthPixelLabel.setText("pixels");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjWidthPixelLabel;
}
/**
 * Return the WidthTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getWidthTextField() {
	if (ivjWidthTextField == null) {
		try {
			ivjWidthTextField = new javax.swing.JTextField();
			ivjWidthTextField.setName("WidthTextField");
			ivjWidthTextField.setText("1000");
			ivjWidthTextField.setColumns(5);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjWidthTextField;
}

private JSpinner getWidthSpinner() {
	if (widthSpinner == null) {
		try {
			SpinnerNumberModel model = new SpinnerNumberModel(1000,1,Integer.MAX_VALUE,1);
			widthSpinner = new JSpinner(model);
			widthSpinner.setName("WidthSpinner");	
			widthSpinner.setPreferredSize(new Dimension(75,24));					
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return widthSpinner;
}

private JSpinner getHeightSpinner() {
	if (heightSpinner == null) {
		try {
			SpinnerNumberModel model = new SpinnerNumberModel(600,1,Integer.MAX_VALUE,1);
			heightSpinner = new JSpinner(model);
			heightSpinner.setName("WidthSpinner");		
			heightSpinner.setPreferredSize(new Dimension(75,24));				
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return heightSpinner;
}

/**
 * Return the YukonRoleComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getYukonRoleComboBox() {
	if (ivjYukonRoleComboBox == null) {
		try {
			ivjYukonRoleComboBox = new javax.swing.JComboBox();
			ivjYukonRoleComboBox.setName("YukonRoleComboBox");
			// user code begin {1}
			//allow them to pick the 'everybody' role
			LiteYukonRole sysRole = AuthFuncs.getRole(SystemRole.ROLEID);
			ivjYukonRoleComboBox.addItem(sysRole);
			
			Iterator i = AuthFuncs.getRoles("Esubstation").iterator();
			while(i.hasNext()) {
				LiteYukonRole r = (LiteYukonRole) i.next();
				ivjYukonRoleComboBox.addItem(r);
			}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjYukonRoleComboBox;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	 System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	 exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DrawingPropertiesPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(276, 228);

		java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
		constraintsJPanel1.gridx = 0; constraintsJPanel1.gridy = 0;
		constraintsJPanel1.gridwidth = 2;
		constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel1.weightx = 1.0;
		constraintsJPanel1.weighty = 1.0;
		constraintsJPanel1.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel1(), constraintsJPanel1);

		java.awt.GridBagConstraints constraintsJPanel2 = new java.awt.GridBagConstraints();
		constraintsJPanel2.gridx = 1; constraintsJPanel2.gridy = 1;
		constraintsJPanel2.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanel2.weightx = 1.0;
		constraintsJPanel2.weighty = 1.0;
		constraintsJPanel2.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getJPanel2(), constraintsJPanel2);
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
		CtiUtilities.setLaF();
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DrawingMetaElementEditorPanel aDrawingPropertiesPanel;
		aDrawingPropertiesPanel = new DrawingMetaElementEditorPanel(); 
		frame.setContentPane(aDrawingPropertiesPanel);
		frame.setSize(aDrawingPropertiesPanel.getSize());
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
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#getValue(Object)
	 */
	public Object getValue(Object o) {
		DrawingMetaElement e = (DrawingMetaElement) o;
		if(e == null)
			e = getMetaElement();
		
		e.setDrawingWidth(((Integer)getWidthSpinner().getValue()).intValue());
		e.setDrawingHeight(((Integer)getHeightSpinner().getValue()).intValue());
		
		Object selected = getYukonRoleComboBox().getSelectedItem();
		if(selected != null) {
			e.setRoleID(((LiteYukonRole)selected).getRoleID());
		}
		else {
			e.setRoleID(SystemRole.ROLEID);
		}
		return e;			
	}

	/**
	 * @see com.cannontech.common.gui.util.DataInputPanel#setValue(Object)
	 */
	public void setValue(Object o) {
		DrawingMetaElement e = (DrawingMetaElement) o;
		setMetaElement(e);
		getWidthSpinner().setValue(new Integer((int)e.getDrawingWidth()));
		getHeightSpinner().setValue(new Integer((int)e.getDrawingHeight()));
		getYukonRoleComboBox().setSelectedItem(AuthFuncs.getRole(e.getRoleID()));		
	}

	/**
	 * Returns the metaElement.
	 * @return DrawingMetaElement
	 */
	public DrawingMetaElement getMetaElement() {
		return metaElement;
	}

	/**
	 * Sets the metaElement.
	 * @param metaElement The metaElement to set
	 */
	public void setMetaElement(DrawingMetaElement metaElement) {
		this.metaElement = metaElement;
	}

}

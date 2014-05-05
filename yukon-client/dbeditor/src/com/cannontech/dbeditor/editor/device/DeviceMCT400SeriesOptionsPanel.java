package com.cannontech.dbeditor.editor.device;

import java.util.List;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import com.cannontech.database.data.device.MCT400SeriesBase;
import com.cannontech.dbeditor.DatabaseEditorOptionPane;
/**
 * Insert the type's description here.
 * Creation date: (11/19/2004 9:07:11 AM)
 * @author: 
 */
public class DeviceMCT400SeriesOptionsPanel extends com.cannontech.common.gui.util.DataInputPanel {
    private MCT400SeriesBase mct400 = null;
	private javax.swing.JLabel ivjJLabelDisconnectAddress = null;
	private javax.swing.JPanel ivjJPanelDisconnect = null;
	private javax.swing.JTextField ivjJTextFieldDisconnectAddress = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		@Override
        public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == DeviceMCT400SeriesOptionsPanel.this.getJCheckBoxEnableDisconnect()) 
				connEtoC2(e);
		};
		@Override
        public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == DeviceMCT400SeriesOptionsPanel.this.getJTextFieldDisconnectAddress()) 
				connEtoC1(e);
		};
	};
	private javax.swing.JCheckBox ivjJCheckBoxEnableDisconnect = null;
/**
 * DeviceMCT400SeriesOptionsPanel constructor comment.
 */
public DeviceMCT400SeriesOptionsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextFieldDisconnectAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> DeviceMCT400SeriesOptionsPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JCheckBoxEnableDisconnect.action.actionPerformed(java.awt.event.ActionEvent) --> DeviceMCT400SeriesOptionsPanel.jCheckBoxEnableDisconnect_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jCheckBoxEnableDisconnect_ActionPerformed(arg1);
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
	D0CB838494G88G88G6EF2F3B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E145BB8DD8D5D5364E9BF31C06E7B6B95F500C6FC50A06C90ACA7E050524193F2161E4B3B46314EF46193179EC72752871553E77020079830209AFD2D2AAA8624728982484C185870BD1B21A18A707FB8F5C8317FB6F1CFB008BE13E3576BE7B1C4365DE401A6671FD6B1E33577EDBEB6D3557DEEB6D8349BC9A12E4B2E792E2CAA26A5F7BA6C28ABEA4E4421C371E6238345BE2CC447077AAC0BAC9B91E88
	4F9C283345471852C8DDDA8A14CB213CFDE30C69903CE7100F52A25E0317G730C073A7CB9718F8DBF4F8AAF1FA719166B14BD705C87D08638E600305BF0FEC149D271E3203CCFFDC7B01989F107E01B47BB328C78EDEA3B9A28EB82F05C0EED8614EC35FEA160E28172GDFF1BBEB5B884FD4CA5D8D65E5EA3B661B43480ED5934CFBF1AE43789CA2553E1B54E78EF9490C023010362E73702CE868EC7F506A6AAA348ACE274B2908D6FB212DD534C90ACBAE6C157A8559D6E893FBA42BD8DEDEC31F972A9BCB0B
	0BAF0932A4FABEF6AB124B69E9901C2203105AA948C3CAE78A9D9FE48A65B80A9B5309F28B0277D00028A91C0F75FBABD4BAA7F506114DD33F95100F2C290C7E0A29BA9F59F7BD1F34EDAA0EBF0D2E8B769B00F2A4G637100B8A20F6A637AF8FCD261BC9607F29E9FEBC79A6CD09E83902C61C36D080F007203G1BC3B96F9F3C41F9EF7AE281C9E81DE35E9F007704D01567E9BA6F314FDE329C26BAF9299F752A996A46GCC82D8883089A08160E53A9776753D894F2AD6255A555565F2D6395D279DB679024B
	A6BA603D3C9CEAD45CC5516A12ED04B0FD5F5BD960079E8EE953876BBA82636DD34275B37C62AC22A8F7D01D7365ADF263C4F32EBAC659F41DB747F4145B8E2AB3CD700E7D30BF433FC0719186BC5301F1940F65E5D017F78717F353BECECBFD629412D6B24F1C6C07166018B0F3509D0C0650BBF4DA180C5F9EC0994782FE9BC0E2D80CA99B208220B90C4B38296F1D11E4DC873AA7BB8547256ED637A0AB273BA507CD14D5D94FD4061335BFE07D96DDF7BFA61730F70CEB94BFEF2EDF3944077D506C8DE37218
	98264B054D7D0700F3C7048D550DFD5A9A7E056E09E5E1EC8DAD9A7EF30A0FB360595ABEC177961637C0DD6D8C301734FD16047870993EBAF21C6223E7786A481DF62EA3F1D0D7F6E70C092D6F6A2BE8F7A7C3F9A1C0AC40D6001419B126DC003219FC0D2776950DF29FB598561775ED6EFF891E22D71105BA27C7911CD67132A0CBC22BC37490350D580FF2ED9D542F036EEFAB4CF1CEF40A32005688C6973AC4D0A68FEB6369CF47B632C8EB9B25AE91A668F293BE6E977D99A89B4123FC6236890A2837E0755F
	6EC71D6891AE4308B0G3C0F1F09724A25363C895E1519DCD689DFA2EE8D1437E8F2F93E7FGBC7F8C4AADBA4006CAD6A4C3107B380EBEC747C90076C161FCDF7E3E096BDF4D5E3BC84146B9FE750F1EFBF7B1DDF25C652BFFC98E9CF79260A36F62E7F4D0E753B03A0FB40CE60FA01D01775C34E167300CF29FA68540B3B937913A97002F6DGE933382C7F74C58E53415302C764DA77318C8AA35AF44933366F8D6019A27608CE25C6F40BCE9B3CF854B57FE0A0D55747B61726AB55940F76299A944623616B86
	52999E46B0EAB72F7F8B2D9794C516DA3B95711228A81233DD6F7F992545B34874F9D4DE6DA664B58FF82C84E850F8FDFCA04320D775AED7E7373B91F70C2A1F295D387EB6E8BFB00B2F7F27F67459C23432C04BFA7DEF5A07DB3B11G479D59A61B6DDFE70E1174074DF1C4C25E391DE438FC0ADB83B46E50780D203418C6B16ED781B647CABA07E5981D7EBA40F4E16355403A9CFF553FAEBFFE553FAEEF3DBA3C2EEE3F6ACFD7D95DFFDDC59EDD3DCE036E6F3C0A764F6AF2BAC53A8135AE0479213FEFC4BEA4
	2724FCAA595AC5651254AF025D1B0DBAFC125A46ECF8AF1E4DEDE36FF1EE9BF37BE71068ABC1FE7D2760E9B34D63E7B3FD1CBCDB370D0C267F7E92771468903BC0D0552EEE27427016EBA9BA5EE3213801BDB7E05F3C730DA043331268GCB62D159293259E45163A930424ED78A69AFBBC472BA049E41DB686985519552F5A92C465AB3781A1F73765B9F668C6FCD9734C932D867F4F7738173AF67972BFA14C1E67DAD0A51D4ED972D1D27DD5EB3CE5C79BAE1858215F5A1FBB408F21BCB6E92ED142CC221D7D1
	E92A225523A72AA350FCD72AFCA62A8FBC53C9E0250459369F1BF19D22E0FDE281364E66366D5AB52C6736CDB56055AE070329905455C720FE3C6F48B42CB909E0F167BE2D5E9A301E4D63A0E9284B625F3BC5F0B91025BF0BFD5CEFA087FC6AAE8B0EEE5D9F79023C88CF07416EB23C5364DF3794935A5ACEEDFC2FE997C13F020D4976C20400E33AE5298B0EA9BCC85478B8275B989FCF82BEB0160E036756889E4F1DE8BD824F11E234C9F328AFA971D81AC0B98B20EC8E9BD3194364435A9D69606DB6C3B981
	5B4EE56D1A6632F661F3315DC5AD2EAD52F092457D8C0C60F79339DFFDA14D981B5BBC21FE77AE42F6F57C44F93E71C62C85474D85FC69BC2EAFFFB0994F42AA0F47E515283B272E496DE6633EE63869665D7004ED66015843A6FA793A061B11D67487D5343AB6B35F7A008C75EF4F5090193B9A553DD4439BDD5560125602EAD4EBE66F32A47692AE072BE70D31FD547EDB4DAF8610C32D4A3F5DA007C9905FB21AEEAD47384ADF8CEF25C49636F7CB055D0A040E272088541EB0C5E0317A0D54C7403142667118
	7C2FDE545F4DD0CEGC80B04380E4687E3A59E9F64812E85006D312B12F10F8583BE9AE08BG33D53F13D0A72E53C22AB2DF16FF002D110FA964F21C12D1294E3BE65694D2F12D5FEF1EE07C7E316604C8D5FEF76BF25C1AD6ED59894078BB574179F3C01B503BB9FF325548DF7C5D0C3F44791C3FD99DA760F97D76166BD5D6CBADBC7FD116D54DB11CB06E33684D418177D956FCA60F02793E7BACB289F52396704D7379BEFB4312EE58E79701BC41598E16CF73C3AC96E663547223D4CF290DA35CF7FFEA974A
	538DE386810482C48144EBF334A40E18ABB8639DA6D7F0D337EF5C3DF33E6E1726AD986A233238F89D557B5C853EF177A60AAFDD609BF737F8F95CDD83F5E18B38CFF92364BB7F1E0503674C73A933FE41C37A957A146BFD7AB17DF9ADAF1B5BG4316AAAF577D95F6066C6FB31EA13605ACAE988F4F49G4C9EC4D29F99F5A8F2A10FE94F2560785B201C32A8461483D0815082B0G9032082F5B6CC4FF76F1703A85B20EFA3C44E4F121632B497073A17D0E781423977913610717DF76AFC33EABD5FBB9430A72
	41BC6C26C5DCBE5FC9C5DC45E230970B39AC76A46DB56C13861764D434BD32A8894FFD17AC35CBCE0E3B17622CAE19BA4E8C776C2E40BEBDC9766F536FCA766F536FC99EBEBE4DCC8E647333D8F2BABDA71A01BFB7G432D6C217985A807AF663A9FC76F84E235724FE9996DA5AB37F93FCE6C37CF9DBBD09D1B635A571EE338D87095DA3F72257DD41E9E0F6821CA0D272BDA77EB65A071936FFCA4F409047B6FCB25D4354D9945D5DD2269615379D97E75109E5ACC874F75215CF17EED0B79197D931A3BEB38A7
	46E48798F38F1777A24D26845FB3D4EE61000B8608B35429790A14403A571462DF777EB785F55E636A162D222FFE09A9D9DA1D3E6B6B94318B45227A4D343FA24890837A766F267DD9DD207EDF268452E1A673319DC6191F5074E516551F3EECD5E532530F5C68B99295E34A0D9ADCD78665FA9F9C23ADA8954F8E2FD61EC84BFD9AEDCB3AF17FBB04BED7B7980A48A8DC5338C1BEFF8B604683CCD647E77AB227977D46B6D96882D6AF39C5AB1938847BEE6E61FDF762D800AB86E8GF0AF61FE4B31EA0CFD18E6
	696E8613C9FD1FD1A61B204F36A5BADF64DE7FB24984FC1ECFDD85149BFCF06CDCBDCF7DC95B3D6CDCCD567017A9FE4000E767EDBC55F1ACCF023A04FB399D0A25E7D673FDB1A66FFD9C575442EDD310067B5E954445C3F90706DBE2C3DCCAB41CEB515AF8544617C239C6437D318DF18DD036EB381B69F883D08E56F08FF890978E65A88D67CA663444EA385A43084BD98AF157D29E3F15348CBA475143ABAFBFA7CB3653C2FBBD558C367F1F6B66392D7A25FC7F677520EC46C2F9B2C064D21EAB713E4E630C70
	69D3C87D1839FE6F90B7B75EEADE33142D5176253A9F3885628CB6272D174FD99093E33245A06E7B3DC63F98F5EA3D1979AC2CFE5920FA5C1BEF91E37DB3036A115EA5037A67F90D75A823A235BF63ED75EB1C371D2BA340E78F89F8BF9A9E43F80AD51FA96107C8430AEA8BDB8BA73527A9774718326F676B73EEA562AA215C28611A2A387EF8B51C75A8573DA08D77999DAF844A919A4E1B4DF5E019064BE8653A17390C5B6B862A7BF9D02E87D0B4FCB345CFDE46E2F50C43997E71D165D2F1AC9C131715E501
	4F502167699FC7B5C79299FCEEA60F228F571B8C52FF94574EAD0BED22AC0BB61682A4BB16211C1E257BBB8A5E9F84F0AC27FB2A25E684DAB8BD88E550279E60382A9FAD4B19EDB1E9733B2A500EF689DE292B3B0B4E4E660E24FBBC925AC7AF67F6734DD65AD6F27A347D9535A55B20DD02567657D674FF94B8ACF39F40EFA8FE0BA6D71D73C03B7FB95FEC47F16C50DEF9000F93F6A530FC16D29AC320ED38567EB18F4E5B66F2AA2C4D5354AEED027AAD8FF0990ECB9ECE06969F287711E1668ADF99DEB96CDF
	AE3D4C56C17B0295DCEFDF9892230D846CEC18CC6DDC13E1EE061FC671B6831E1D9905BD7C5B87B754452D60BE7B5AF4BC9F4B9E047D8D60G988B908A907DA077618F6E46FC6B3071CC234BDD8FCE124360D2B37E245E2FF66F3CB4195B344845F7120563DA826605629EE4FBF8077A8C39739045B37EDAB2103F5C15B126CAG9B40G40C40030151C3F3F6DAEE07CE18A2BCAD1842B1DE647B8F31830525168DC30F53E218FFD15D617CC9DB4662B5CE6627EC6940C9F8710368A7C23D5ACD7427A55D78E75F1
	2C153EBECE8574E9D82572C67567BB546E5A8127GB05CAD546E06C0B9FC959F5F5DB7D86747D1BB3C896A372C62BAFFD3767568FC66EA301BG8755F245EADD3F589C27298D2F85FC63EACE471277719B2B7375C22B512FD2EF8A543DF243E73886435CA8286D3AF761FA864C72931E673EE9D08EEE4CEAA677D0F8B23BF37220EEF7AE349C0527D36C4D5192C9B592ACB1DEEE67153CDBD210F322B8DF554710DEE39E29EC773F874CA3ADDC4D56EC0D7AD49EBBC40E5279600CA37CEF7B2921B850D3CAA37DB4
	CD1D7717FB78BE481EB91554BC52E266F647F75E44B56A77DFEBF43F25A66279A4B6E6E7164E77DC5BE878E67DA69C567BFDE29D3D3C9E7A5CA82F349F0DB71B82509D24529BEC201B4D7D4C7F68F30F3BB25C5C256F435CA7B84DFF4A567B5D6399FD3F50D775FE5664D7FD7B1D058D15F72CCA16844731029C603684FA72B5DBF1CCDF3F5F9E574709EE9F4D7C4CD6CE6941FE6AF7BE0677521E2F9B470F6C5BDC78C667E0F676A76901E36947527D4752CF247B4F637CA6FD783B592752835F4DA6249B6FE659
	B931B8976D469A50412DGFB56C29C84D03A161F91638E60826317BBC34F07B336F60C6571EB9D054FAFE58466551DB172B7BEFD99F8A7536AD294D7970E4F7157B2F03D94171BA299EEEC864AC8165A6D8AEB2A62EF4CC05B62905BA81A7981ABCCBCD75BG3C0DDF4BE32EA5740C9F557DC92324B844537454E3B151F2B33FBB09DE4B6330FFB90573A4C0B9ED9D1C9D6BF8DE753B15BCFE2ADC475BAE284436DEA8CF8488DB47ED75055775FD54F4B4A770BEFA273494F6637BE5792CDF449BFA3F0F7AC65F4F
	D12077F3E40FF67F0F4E36EFBF45663869EDAC317739057AFC0B6C2321D34D0D3585480D5113164979810967E72357F1DF6FCA913DAFFC88FC210738DE0FB9083168E521E368AD9EA09BE417DB1415BE0625373E6AFE79F1B17A8FADB04EGC08840C200B5GDBG92G7644429E82A883E8G3081F88102GA683C4GAC0B65F41CBD14BF8C9DEA70CFB719AC309CB223430469ABD55FB79CFEAD509830CB4F0328BEEE6DGE721D1E8577A4464865E27ABF3C75E279BF269FDAA6C2F46BE371AFFFBA41766355B
	5ABC22E68F62F353B4DC1D27416511B0EF47EC6F374DA379481BE27D6578EF9E311FEF4E1F75DBC57B6D089DDD6C1B321EFFC7798C5D53596BFDE30E97A8BEEF3DEF4CB17E940FB9CA21AEF8BD0FF9A6D4FE3518672DAA9CEF671079CBA9BEE5036F7CCBAA797C19D0675D4067DFAD79BB6B62B728F91F8D7E651278F00CA96B6141F585D02EFC58DF1E53D6046F5A7944360EDA57D904E78DFE678326C46A27577317202133DD3DBFAA8E2C13730BC756497B0A7D1F9D4B0B071E9D2B0A7D1F9D31454633C33D53
	ACC67B538EFB31C9EB77D38D7719067B859D93F11FCAB6452E617F03520578DA91A764785FD13AFAA92E09FFDBE540FD26627E3398779530ECF7E9F7C34F9553D8C7F2B854EFCCE99B49895BCC13072584E9145C02016E948D07F4337B0A680C3D342FC3F20A556CCBDA426FFDFEC94F38C6D8EB4743BCEF357B946246C0F95243DC37F61EA25ABECB2A545FA71C7A66FD38BB7E89F314D469BC159B5E6F2F7C66670E7FC667E0EB7AA4BDB7C3E0BDA334F53E5841731DD19A6EA1BB4FF746EA389F49BC5F39C543
	952672FCE702061B5649731D35F1BCD6CE205F5AA868DA211FC1F8DC1DC63F71E906F6F62D6D2B2712F8DB42FD8981BF95D0FD09440E4139363125C49B6F8B4ACBB00C959A47F388891417D8A847EB387BA9AFA99BE3CC199BF94E60CC6AC8B983E32E51B7CFD63351B7E7300E4A2181704D9B394F3779BD1D0F27BF9C058FD62A47E0DB776BFD1759070D3B30E7990F7B0E266B7DEE12C75F6F518C3D5F3154E37EE93581292EF6A036D8A516755D09C7G0FBBDF5247E911C6B3BF7B5F3537F3C4B0F661CF12A4
	0524330CC80E1E927148644050B12AA6BC7AC878151ACCB3B15711DDF6EADF6BC8D2AF9AEAAF7EABG5275C3683BDF6BDBC70D69661EF74AADCFCC3518C9A2F4E407CB9DC94493827A4BEC9C8B9CAEF414CB047DA121FBBCFFA7DF32E3180E6D3F04B2384527A4133D0324397C76F7FF35666717BD96322B0E9C468497FE5807E728E843FBE7759F934E12D4B7FEE80029D7A051C39FC99E254FA156134C360B78A123C791EDD57CF476A864A07D182888A31C22DA41E3D7D01AA76BC922937F05029CE0558E4159
	DE440E6A1314CFD3D5A56513BA9AC0EF862597C7A954C64938E77FEB2F1E5F3250C24CE749EE07CB301D952C60185611D445AE0B9E3B4BE1733ACF3101D2687D9266BA93E13817B5761159C24C12E9C4F7FA7FADE86F563DE74D90BA4924083644BDDF283530D55B8599269265BA857FA544A567EAF55FBA223C3E765C21E6B3B129CC2268DF743A95322BCD68129CFD70D2C3F517AE4AFEA83253D8538D94C661D960C7796913AB277C2CE27BB6986CACC9229FB981C9E9FA2B0F3A3BDAC5F9DC788FDEF9F15266
	6FA145EEE4170F1C8A5E0C5303DFF54160B9BEBC73AA0D7442DBD6967FE046F3FF25246BDD95F214AD92F4ADEA97DDDDC5D5EDEDF012EB83400A41702FE843E31BC21FB6873CB79FBC3F7105D650309AE269AAAE263FA57477847E16D0CC8945141C00E55BCE067D732D77F21E29DA5242CB28C3DEFC4BE03596D5218E29FDDE8930C966024EB579A9258ED1CC446E9D2AE108CE575699CB19781305BF07E92717696F116B6692FB6578F0172FF3B78F400C94GD886F320105D3A8DD3C826DE48D1B34265B986AE
	DB68FC7B7E7F39ECD14B6FF8B5FE700B4DFF75D372251C84205CB21472FFBC3DA775D57904B01DC38FFA4FE03A894FC767DFB366F593EE3896B08F9E964F622228F85F6FA9DFB8CE7F4F8B70190F71787F6363189889943BDF6A929C8EC5F0F4D6B9ED98471F6ED3C4BD5717180B63FBF89B3512553D180BF4F7987A7C9FD0CB8788243F5A27B496GGD4BEGGD0CB818294G94G88G88G6EF2F3B1243F5A27B496GGD4BEGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1
	D0CB8586GGGG81G81GBAGGGEE96GGGG
**end of data**/
}
/**
 * Return the JCheckBoxEnableDisconnect property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxEnableDisconnect() {
	if (ivjJCheckBoxEnableDisconnect == null) {
		try {
			ivjJCheckBoxEnableDisconnect = new javax.swing.JCheckBox();
			ivjJCheckBoxEnableDisconnect.setName("JCheckBoxEnableDisconnect");
			ivjJCheckBoxEnableDisconnect.setText("Enable Disconnect");
			ivjJCheckBoxEnableDisconnect.setMaximumSize(new java.awt.Dimension(147, 22));
			ivjJCheckBoxEnableDisconnect.setPreferredSize(new java.awt.Dimension(147, 22));
			ivjJCheckBoxEnableDisconnect.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJCheckBoxEnableDisconnect.setMinimumSize(new java.awt.Dimension(147, 22));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxEnableDisconnect;
}
/**
 * Return the JLabelDisconnectAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDisconnectAddress() {
	if (ivjJLabelDisconnectAddress == null) {
		try {
			ivjJLabelDisconnectAddress = new javax.swing.JLabel();
			ivjJLabelDisconnectAddress.setName("JLabelDisconnectAddress");
			ivjJLabelDisconnectAddress.setText("Disconnect Address: ");
			ivjJLabelDisconnectAddress.setMaximumSize(new java.awt.Dimension(141, 18));
			ivjJLabelDisconnectAddress.setPreferredSize(new java.awt.Dimension(141, 18));
			ivjJLabelDisconnectAddress.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDisconnectAddress.setMinimumSize(new java.awt.Dimension(141, 18));
			// user code begin {1}
			ivjJLabelDisconnectAddress.setEnabled(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDisconnectAddress;
}
/**
 * Return the JPanelDisconnect property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelDisconnect() {
	if (ivjJPanelDisconnect == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Disconnect");
			ivjJPanelDisconnect = new javax.swing.JPanel();
			ivjJPanelDisconnect.setName("JPanelDisconnect");
			ivjJPanelDisconnect.setBorder(ivjLocalBorder);
			ivjJPanelDisconnect.setLayout(new java.awt.GridBagLayout());
			ivjJPanelDisconnect.setMaximumSize(new java.awt.Dimension(344, 178));
			ivjJPanelDisconnect.setPreferredSize(new java.awt.Dimension(344, 178));
			ivjJPanelDisconnect.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJPanelDisconnect.setMinimumSize(new java.awt.Dimension(344, 178));

			java.awt.GridBagConstraints constraintsJLabelDisconnectAddress = new java.awt.GridBagConstraints();
			constraintsJLabelDisconnectAddress.gridx = 1; constraintsJLabelDisconnectAddress.gridy = 2;
			constraintsJLabelDisconnectAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelDisconnectAddress.insets = new java.awt.Insets(12, 23, 85, 6);
			getJPanelDisconnect().add(getJLabelDisconnectAddress(), constraintsJLabelDisconnectAddress);

			java.awt.GridBagConstraints constraintsJTextFieldDisconnectAddress = new java.awt.GridBagConstraints();
			constraintsJTextFieldDisconnectAddress.gridx = 2; constraintsJTextFieldDisconnectAddress.gridy = 2;
			constraintsJTextFieldDisconnectAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldDisconnectAddress.weightx = 1.0;
			constraintsJTextFieldDisconnectAddress.insets = new java.awt.Insets(12, 0, 83, 23);
			getJPanelDisconnect().add(getJTextFieldDisconnectAddress(), constraintsJTextFieldDisconnectAddress);

			java.awt.GridBagConstraints constraintsJCheckBoxEnableDisconnect = new java.awt.GridBagConstraints();
			constraintsJCheckBoxEnableDisconnect.gridx = 1; constraintsJCheckBoxEnableDisconnect.gridy = 1;
			constraintsJCheckBoxEnableDisconnect.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxEnableDisconnect.insets = new java.awt.Insets(29, 23, 12, 0);
			getJPanelDisconnect().add(getJCheckBoxEnableDisconnect(), constraintsJCheckBoxEnableDisconnect);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDisconnect;
}
/**
 * Return the JTextFieldDisconnectAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldDisconnectAddress() {
	if (ivjJTextFieldDisconnectAddress == null) {
		try {
			ivjJTextFieldDisconnectAddress = new javax.swing.JTextField();
			ivjJTextFieldDisconnectAddress.setName("JTextFieldDisconnectAddress");
			ivjJTextFieldDisconnectAddress.setPreferredSize(new java.awt.Dimension(151, 20));
			ivjJTextFieldDisconnectAddress.setMaximumSize(new java.awt.Dimension(151, 20));
			ivjJTextFieldDisconnectAddress.setMinimumSize(new java.awt.Dimension(151, 20));
			// user code begin {1}
			ivjJTextFieldDisconnectAddress.setDocument( new com.cannontech.common.gui.unchanging.LongRangeDocument(0, 4194304) );
			ivjJTextFieldDisconnectAddress.setEnabled(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldDisconnectAddress;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
@Override
public Object getValue(Object o) 
{
    mct400 = (MCT400SeriesBase)o;
	
	if(getJCheckBoxEnableDisconnect().isSelected() &&
		getJTextFieldDisconnectAddress().getText().length() > 0)
	{
		mct400.setDeviceID(mct400.getPAObjectID());
		mct400.getDeviceMCT400Series().setDisconnectAddress(new Integer(getJTextFieldDisconnectAddress().getText()));
	}
	else
	{
		mct400.deleteAnAddress();
	}
		
	return o;	
	
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
	getJTextFieldDisconnectAddress().addCaretListener(ivjEventHandler);
	getJCheckBoxEnableDisconnect().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DeviceMCT400SeriesOptionsPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsJPanelDisconnect = new java.awt.GridBagConstraints();
		constraintsJPanelDisconnect.gridx = 1; constraintsJPanelDisconnect.gridy = 1;
		constraintsJPanelDisconnect.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelDisconnect.weightx = 1.0;
		constraintsJPanelDisconnect.weighty = 1.0;
		constraintsJPanelDisconnect.insets = new java.awt.Insets(3, 2, 179, 4);
		add(getJPanelDisconnect(), constraintsJPanelDisconnect);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jCheckBoxEnableDisconnect_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTextFieldDisconnectAddress().setEnabled(getJCheckBoxEnableDisconnect().isSelected());
	getJLabelDisconnectAddress().setEnabled(getJCheckBoxEnableDisconnect().isSelected());
	
	fireInputUpdate();
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		DeviceMCT400SeriesOptionsPanel aDeviceMCT400SeriesOptionsPanel;
		aDeviceMCT400SeriesOptionsPanel = new DeviceMCT400SeriesOptionsPanel();
		frame.setContentPane(aDeviceMCT400SeriesOptionsPanel);
		frame.setSize(aDeviceMCT400SeriesOptionsPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
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
@Override
public void setValue(Object o) 
{
	mct400 = (MCT400SeriesBase)o;
	
	if(mct400.getDeviceMCT400Series().getDisconnectAddress() != null) {
		Integer addy = mct400.getDeviceMCT400Series().getDisconnectAddress();
		getJCheckBoxEnableDisconnect().doClick();
		getJTextFieldDisconnectAddress().setText(addy.toString());
	}
}

@Override
public boolean isInputValid() 
{
    String addressText = getJTextFieldDisconnectAddress().getText();
    if (getJCheckBoxEnableDisconnect().isSelected()) {
        if (StringUtils.isBlank(addressText)) {
            setErrorString("You have enabled disconnect.  Please specify an address.");
            return false;
        }
        
        List<String> devices = MCT400SeriesBase.isDiscAddressUnique(Integer.valueOf(addressText), mct400.getPAObjectID());
        if (!CollectionUtils.isEmpty(devices)) {
            String message = "The disconnect address '" + addressText
                + "' is already used by the following devices,\n"
                + "are you sure you want to use it again?\n";
            
            int res = DatabaseEditorOptionPane.showAlreadyUsedConfirmDialog(this,
                                                                            message,
                                                                            "Address Already Used",
                                                                            devices);
            if (res == JOptionPane.NO_OPTION) {
                setErrorString(null);
                return false;
            }
    	}
	}
	return true;
}

}

package com.cannontech.dbeditor.wizard.device.lmcontrolarea;
/**
 * This type was created in VisualAge.
 */

import java.awt.Dimension;
import javax.swing.JDialog;

import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.device.lm.ILMControlAreaTrigger;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;

public class LMControlAreaTriggerModifyPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.CaretListener 
{
	private javax.swing.JComboBox ivjJComboBoxNormalState = null;
	private javax.swing.JComboBox ivjJComboBoxType = null;
	private javax.swing.JLabel ivjJLabelNormalStateAndThreshold = null;
	private javax.swing.JLabel ivjJLabelType = null;
	private javax.swing.JTextField ivjJTextFieldThreshold = null;
	private javax.swing.JLabel ivjJLabelMinRestOffset = null;
	private javax.swing.JTextField ivjJTextFieldMinRestOffset = null;
	//a mutable lite point used for comparisons
	private static final com.cannontech.database.data.lite.LitePoint DUMMY_LITE_POINT = 
					new com.cannontech.database.data.lite.LitePoint(Integer.MIN_VALUE, "**DUMMY**", 0, 0, 0, 0 );
	private com.cannontech.common.gui.util.JPanelDevicePoint ivjJPanelDevicePointPeak = null;
	private javax.swing.JCheckBox ivjJCheckBoxPeakTracking = null;
	private javax.swing.JPanel ivjJPanelPeakTracking = null;
	private com.cannontech.common.gui.util.JPanelDevicePoint ivjJPanelTriggerID = null;
	private javax.swing.JButton ivjJButtonProjection = null;
	private LMTriggerProjectionPanel triggerProjPanel = null;

	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public LMControlAreaTriggerModifyPanel() {
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
		if (e.getSource() == getJComboBoxType()) 
			connEtoC1(e);
		if (e.getSource() == getJComboBoxNormalState()) 
			connEtoC4(e);
		if (e.getSource() == getJCheckBoxPeakTracking()) 
			connEtoC2(e);
		if (e.getSource() == getJButtonProjection()) 
			connEtoC6(e);
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
		if (e.getSource() == getJTextFieldThreshold()) 
			connEtoC5(e);
		if (e.getSource() == getJTextFieldMinRestOffset()) 
			connEtoC3(e);
		// user code begin {2}
		// user code end
	}


	/**
	 * connEtoC1:  (JComboBoxType.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerPanel.jComboBoxType_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.jComboBoxType_ActionPerformed(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoC2:  (JCheckBoxPeakTracking.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerModifyPanel.jCheckBoxPeakTracking_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC2(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.jCheckBoxPeakTracking_ActionPerformed(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}


	/**
	 * connEtoC3:  (JTextFieldMinRestOffset.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlAreaTriggerModifyPanel.fireInputUpdate()V)
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
	 * connEtoC4:  (JComboBoxNormalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerModifyPanel.fireInputUpdate()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC4(java.awt.event.ActionEvent arg1) {
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
	 * connEtoC5:  (JTextFieldThreshold.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlAreaTriggerPanel.fireInputUpdate()V)
	 * @param arg1 javax.swing.event.CaretEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC5(javax.swing.event.CaretEvent arg1) {
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
	 * connEtoC6:  (JButtonProjection.action.actionPerformed(java.awt.event.ActionEvent) --> LMControlAreaTriggerModifyPanel.jButtonProjection_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC6(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.jButtonProjection_ActionPerformed(arg1);
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
	D0CB838494G88G88G4FEE0FAEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D45731263509AFB5FD745536B635ADB5745534A4350DEDECCB7BC8EAD21358577455905B5ABE521A46A62665FDB12DDF1BA61785DC7CC110DD9485F513E8820670232038007233A2A0AA4A0AE8D0D9F66F426A323B593D04A5B1714D1C3F3D3B5C65AF4F3E664BF86F19F34E1C19B9F34E4C19FB964938ABB6A3D5E71424548C097DF7B0D512961D17244B9FFCB01B63967F7DB13D247A6FE500
	4DD2D7DFBABC17C15D022EA4FD0EF4F4DDA614D7C1F96509A47DD6F8B7C8261614D37092E51C45D077642DFFFDF26CF11E5F4F4751137205339B613989A085F046979F53778264C87E33798CDF8D65CD6C9DA1D587381730CD13B5DF050FE36DB6C21D89E0B9E9F35B59C2D6BF8D4A58E6B6BC63DF22ED9741F39D616EE366EA562E67137324558F9F52E561D8AA7A9CF6CBC924EF8BFB1A247B08A252248B2F032A255A0E2B7D076D5E41D23B4D6371FA9459BED06A68139DAE456BAF9DF63DEB73BBCA9D72BBAE
	3BDC6A9E34C3833F57ED734BB633397DC493ADB6C0516AF775774B7E93DE074BB952E17348EE203DD04239F1D9B349F8A09314AD2908FB420AFAFC814AC6A817GD039FE16D54B781EF5F51E14FCC7C9D439E624D2F962D3C3F295FE31AF23C707743FC366897B2D027A5D00233C3CF81673922B633C3CFA925BAE03F28AC0E69A47BFCD70A6A8D783F424F19E6BDE67BCF63FF72FD474438753332370B810C6F90BC98B7198935317310D58524CAA3407A5D01782101E1E24AF82E88198GA8A0B6DCDB39971E8D
	FDCA13F7F0506BE970799A5D8E54326C06F73399EA986E0CEC777A9D12C46D54E3AD516007433474A4FDA40E4AF6CE62F695FF66EEA9FF704769999A32ADF82CC6B70B5118179E12EDD5BA6A6DBC517DE2F847FEB8FF94BFC0706599A1BC1D2B2708ADE0598AF50999DC4F5D1BB8AF6D69F3251C0ACBBA3D86AF336F6F54152C25BCD85606F821BAF69EC49D7BGBF93A08EA081E0B9408BEB390EBFD939FFBC9D37423A70FBEC6E4E21BE1F4D2FB48E395C8E594FF41DF0F6ACDDEB816D7770247BD13D4C5B2F1E
	236577DC54544B72F90D3AFCBD55C739BE2497BAF6DAD73431BB7423EDE31318434F12B5615053B9CC9378AF933C22425339ADA6F8ACCF073A15G34FD5FA94437E7C65A485B846F4B0C341114135CC6E6C11D34AEC9CF67B77D906E17EEA84FG0885D888308CE0B5C066FABE474BAB4BA6380EBAD473CB7BFE3AEA8BBC6520623735FA820A4DE3173BECFE172D4FAD87A456660BD528573E30FEF71075ED07B10E499E59EFD3DCDE8FD0378D4AE0CC815AA63EEAB336018D94EB2D2EC199869874C91C6E02AA83
	4AE78BA8E7FD8E1BA207DA507A04AA3409F7ECDDC09188602AD90F7A7A9859F30B605D321E6B2A560C38B9D0DEA074F270C0AEBC0F03F14BEE33596232A39BB67F8837514B442FAD067657B600BEB7A669E78394E541BCE671F57A47EEEE0F162C3B241247FAB46D116C951B28EDA5EE0A34C78F99E7A560B33301BEC08B40G408CG5A66D9EBAF4A149545BEA37048EFE49B1483E508363ED0B6BAD666D99CEBC314F6D991658C0DB6E8737AF156B21D172F5520ED4F83B997812C4866F363BFE022B65BE88B48
	5CCACF794140E4C7E826E8DB6FC174C172BB32C73148BE59630017G3311F78E2E8B592F53ABEC6B9A4163FE5684869690780F9C5CCC71C0C33D9AF87D2D245E26A8FED75F10A2F74A0A627274077A7F9B61A59036D586182C7135446F030C469C704BB9DC5602EA034A8E5B3D5E2BC3BEAB2EB0E64F5B3B50F62B21FDC78E1F23AF1C4458CC91652F12F2283E6CE477987A9F8B106644767016A90F11B601B6F40C01D338B6D2194D6CG5885D0416C133699A6EDE2016F45GA9G791B137475G831B39BE7EFC
	32175129083043E8F62E86BE6E44F85EFC2A174928260D6BA4F26DC42331EE826D286DECBA945D76738EE95BFE41A1ED5BFF79505836DDFCC84B36E9DD79A1144FBB6CD12D152AC3383FC2586C11490295DDA49A037F510AF238BCAE651C4B51AFAB1D2EF7E5081BB7234D4FA1FBF88C3C47ED66FB6D702BFC2F5D7D6E1DD2626503BA2D78EC76D73AF4AFEC26B61C139B5AEBA9CF8DE6DC0332DB9E84C5B5F907BC8A459FA37E016183E98477EB4FD730EFD19B784DBEEF23B7E89D7149A5B6A2CBA9FDF448FE27
	57BFA8BB0A2E585E31155A0615D2B297258D247AA83E971B5EFC7330E91C83C3499575A0179BA22837C7A7DFD35CD5DC4E6C4DA0BDFF7C834A3395663D59A53B9D56813F9C98703A9DA5F6585514D272AFF5280469E0E9E098261991EE425AC9306DF47965D60FEF08D384E6AAB9AF2E210FC670F24265B983AE7C1453991015FF94BF95150C1F5F6D7BE878FC1E8465C2DC2BC0439BB52DDA7CD4B1FE2AAEFD0771B3A05B2F82BF9D326D2A55EF33DF8579EF1C1DE98CB6D9FB5BA766780153F7D1999A079485A8
	783DD768E23FE17CC78EB4D95E2BA49EFB7CF79DB913419EE1816849657E78C7E62C677E18859DCDDE371B8E88F5CBF3F10F0ABBEBD46DBB8A60B0A71288CFDA3F27CB2B3E50406BCBFA346A4BC57DDD1A7596D1DFFAD12B3EDB54FFC3335EED60F24E2F49417DDCFEFBC806309E830E6372880B4D6F2D490D286B323907C2B17FFF5420EFF32BE2950A7FD62D767E3A309663132B02FEC26DDA89E3F7CA137A0379B62469733B86A13443600B650EAA3A543923E940BF661992D1CEGEA07165750BF0F171509B7
	923D1E62F926D550BF9DF03B0D1426434872AA245D5B9D3C5D9AAC67A5694B73E8BBF71E3ADD65E95EEEA66063G96E9367B0BE8A7ED01988520FA0BD63B6F1F616D02D09F83B01F35CB5922EEF73D13372B5F1A246F8688EE256D96EE256D5273315DBDA287D5A6F08F935CF3B08985997CECFDA2C71DD76B153A56EA79D70416FCCA3FA7BFB267E0BC02F42F81FEE6BE373383F56A7836A190705ADD644847EC6649BA356F2538276B564393F64F00528AC1CD105BDDCA9D720AE7C206E6367375B76A2451E75C
	23A413470F8FCD43E49330FA1B60D85A82265BA4C213AE17BCAC711C5D2FC2F932FC9EFB3C5F2C4E4DA5E4DBF49B2268E69953499A15EE0AE27AB2A86D5F3E45E9D7EF63F9B9FB8F477984EE05404D93B879A247A58B5C6F85AEFFFB121E6A62F3E64C69E865B66DC4B9257DC32E52A1450507DE1BE2A36E092E8FCA7373643C0172F5EC6779C0339157758CA84782AC84203909A4123340F19F85DCE6815FFB320A55FBCFFD81BD3B8C947075DA57318F1E137791FC3E4DFA1C0F8F93B451F91DEB28E7BCAE39FB
	2ED4FBFD0FCEAB1FB9A33BCCF72D004EE7DC01EADEEB2CEB937659D95E75768E2E0B94E8D3D448F531A9EC9F6EAD243A18D148F511FA9A5765A423A12607C54539F41DC4C499BCF7F471B467E98E72A3F85AE95062E9638E4E5322A971C447B41D61E31A00DE778EBE660FF5DAB691A3463C654CD4ED020E3BC20C9B87B4170BF18D856AF10DBB137435G3DBB793847BBBF1CAD9E3BE4A136786183ABAA473CCEAE0782BC466F64F2F48455F22C584966EC9717A35B7A616458F59A5742078D30D8AEFD1F5A17A4
	A6B7E91E5548BA5AC557D3652EC8DF32269E77218E40BBF6F1DFF27570E615AFB9839C5ABC7D90FDF07F306D886A82628CD65EF18447A5F106444F6749134ABD87F7719CE2D5A36AFFA6146781A482AC83D8A3F80BBBB2EEAE7DE8F00CDC7AD3EFCD062FFC934FED8C74CC26DF09687707C9752B977D2EDC1CCCBF2B68774C247A8D08FE31A4DFFF8D4AB1A636871BC2B92A05703E44941E332279745F921F15EC227974BC01FF1E600DAF0770F45D5593BC16DB20EE754BFC7C6527A2790EFDA5FC3C0408B26D77
	7428FEBBA34A49917D68BA6CF723BD917F2D5AEE4DE6F1EEE5FE5AD24CFD7F1AD7380DFE3C89E34272D713746DG8E00E9GF1G09GABDE65367A5C912DF8A95CD62385CB211CA81B1F0E2969676ED17DCAA34A693B3574332728D0DBBFA24FC075F3D444C6053B79DC5ACFCF0D5737C7750B64559A41AB65612C01736096BC3CF6E6EABCD40E6A9749C31CA68FAF6AB88F890207272748431AD17DA2EDFA35661C9D6E0AE6534CEB32335E65D675D9EF639E6A634A61E95943E35E3602CC46CF4F9EAE4F5D1DD3
	13A7E1D43FC8F9E66D5112A73BF31B36BC54FB8A5D5F92643C466F61EB74918B62D6C0F9759E3E9EBB6AB1B74C7DDA075765D1A46E537EDE0FE7E5AF9CE7DD9E0E53B5A04E6E751304AB7DCE7097F2EEC9FFADC9EFFA0D532DEB0C1E9F36B4EA67075B9A3573439D0DE3FF9B69EE0C16BF267C3D7896672F9200622E77F0DC0B40C997B92EC760EE92B81F4069AEF15CF4013B171CDDE6C3F93E406DBC073884A8AF9338BA195C19013274BA47FD4D09385DD02E95381B8690578B65606B5C56D69A0B60B9934AF1
	G09G2BGB696A569CBG5A81FCGB1G71C53C4F5FC81FA5C5B45F302A081E7FB127303118668CB0A6ADA92667F85A2759988B4F92C6EFACDF09FC6093F9E99F276DA6463B3A3CD155A7063D2F0AE8939F05AE5B9F892FD964FD6C31B3D47D3247E93BC99A7D2DC87DCD45401E115FCB7222505AC168BD34A51B58F1A0A087081343D3B02BFBCA4913B43ED91C348D4A924FF99ABA5631B6C795EFD30550C1EB71D871CDC771E87D31EF6CCD5157681CA66DB5BA3789F59D708E796DF264BA3C33A9DF54053CF6
	2BA28F2298ACCFC77AABB6FF3FBCEA9DFF177427F5517A5F5F94ED2DD39DBD9E2623AA212336B1E3C007060EA83D5BCF2B696D9774AE984622970CCA6F6299B53DCAC1AFADEDACFAB32352FB39D3CDEF1F447D44F34528CB8DBFC192AE54C7FC36B51359F0ECB14F3774ECA77732201C821039176F89278AA279B3C1DD65DEF64FEDEFB47E1E8C636F0010F7E6906975C474733172F48DFAF47E9F35E06C9C94652524BCA264169BB6899BBA6AF1B007C6F30F1B489E7D930007F49C17C77C407340ACFEF323384F
	905CF540BDAEF0DB099FF886F037885CEC02CBCD1724BF24F25CB6629BFE8A6DDC29DC877F5905FB0762753A2DAE9F460A52EC108F24BCAE89BDD51D43F561734BCE596F179D741324E4FC83E9D61DC71A4B214F4A3D7C4CED15518F3BEDA35EA110EF91E9F7D566B1CCB714FDG3381625F203A247A7966B07262745B86419CBAFD32DD0AAD413E77B9F95F0DD0B68154837482CCAB61E34E9E403E5051B5B8B4C8B8247C4DA73EEAA534DBAD5ABE1605F95436F6DB1F6C46A4991773D6B34A9976E5D368A0B7FF
	0768237AC4566089FDCB94ED676EB02A69281A53FA4909FB344657C8563F2C44A0468A7BBA486A7583DB437AEB35F9400CB9B03A225449974E23653DECD14FBE8A34DAD85D7DFBF19F9B7575CA625FF82A7254B997531BB49E353C49F3B2F3BA346A3BC57D2135DA75EEF852F837599076ED0355AF026732B7F98C52DB4C6312940133DD64387AD20EFB5B40F1B18237BD0D6312CB799EB227D53D07141701BCG5DE554BE5DE52357BCBDB7BFCA7624E9E5745C1CA1703FA278D9AABCBDCF77ED67F92CF9D03731
	1C7364A4713B8F4A53856E9B84B79B4A73844ED848F9DFA0F0F710F8BA3DA2C9DFD841F16BC748FDAFA8D78B1CF7047735D4709C7ECF5FD26724F2EE5F2FC3FE35F2D233AA288EE2ABC2B9A93AD7341F886D957BE493BCBD7230C9A444AC2EC1590317510A76176FAFB1D59C2AAC26E3478E2B472EDE5F9AB59F36140D3912BD67BC66CE257A7C144C6F751A76011C7BF83E3F22B76CBC0C0C184D477CAEC7232D3F1D6CCC6C3B8609F931FF4CBE6EE756B949FDD5A8A7832C5947F595FC0DFF6F087F7739D27B34
	0B1AF711132D96DD4EFE163F5B9F52D51C785E7114130F69037AF97B49DE5F2FDE93284B869DBD4B527AE3E17538BEF1BF8A550F0455A33F770675378D286BD1C72F337ED436C776F059DEF8E43E647E767128772CD318CC46CAF617B44E1D3A6C22BD035A4219D3A1DB58E15C995D96AA4A41944AC51E75B9E71418006C02D4EF6EBCFEEEEC2F64675E1FF7F05CF401732D653804CAEE8F771FBAA0CD707B8F5D94AD640ABB7351146E8786CEF7F5A53715AF8C232D14D4C1992087E0DE9562070BAD9A3AD36B1F
	5F87442782FDE6G3C4A6A66407B9E26FFCAAFF8F1ACFAFA560F53CB092017FEA0C97F23D4DACF694DB00CC5EF1D0AA6524BB990CEEFB714BF1E2A662FA2EDB27C15C75033C07927CCDE3AD75DC3623F8E40BB8E707D6BC1A362E6C0F90E4025EFC3DCA2149F95389FDC627B5C0A83FCFFCF5A0E384A035066A047BDC1F078AD791A408DEC67777730EDB4F9A6835DAABAF40C1B4498933973B791D05FB725EB6FFD62BB86BDB258364BCEE321D984778CC06CC19E8BBDD8C06ECCC2A8C1576FC7BAA4B127EF940E
	B527E991B0C3E5F3B80F73E10CF570721717DEFA090E753D20F62CD9FAC692FC3FBC924E4F694E90BFB70F0C454F06A87C94B0FE928ED2DF0CC0F3CEAF925AB4D6DFFC9C57DB176D0A66D772B65812CE75E19CE435750B7EAB49FD0145C0F745C1715B0193EFC42383C88826FC32DF9921D8F2FB0459446764297DE6602BD36C17BC69FEB42EF91F7846746A10AEA97EE6024FD16129FF4EF6F27FDC88F5532AF93EEF9B393F32824A5221A4FDBEC0B9C087007B905765AFCF609D3EB17371D62F2FDDFE87C25CD0
	6A0D4A374AB9355FD25C2767FEF041773EA6AD3CF9C354BB8D538E31F808BD974FF527EE923691FF8E65FB946AD68164181374C5G35GD6B3176F1513A5D4BEB40786C531598748ED8FAE9CDE40882171C04472CAF9EA59864CF47CEB6C99E39E3DEF51FE07437254F12C5DC276DC92355F50DAF53FE456EEB5FBE656C46B775130BCC4A134ABD73535E874233634B908B653DD93E9E35B895ED193E9E3F78CF39B73C15DD200DD924D03E5CA7C3F14A323F1925BFFAE604F5E587CBF31114FFF61576F10AC0FEF
	50717DAC12576ADA4A7F0C5AD00CA7BDE24F509A1B0EE3FD8BCB516BEF6B993B7E7B9731C4774907DB710C5D677513A495BDE3BFA13E93A482CF4BG0CF5E0F7F5B40F4A6E6F9AC71F4DD79BF93F7ABAFAB66F06273B0E4AC57B95EC43FD5989B19375A595446FCE07B6B1F5BC66D9FAE9BCFF31C18A6D530950EFE99D7597D8DE896F4F032FC895E3BEF79D730E1AB7C1C51B5F3EC75AC406EC58D6623F333C479CF91EC4FD2C0671B28FF33DFC3DG1F2F8F3D4EEC62B717C276115A0B6FF44DBECB6EE70B9C9F
	5D42D95DEAAB4658F8F75BE5F33B5EA55739BA2121271F2D87A752E1B9A655FB1EF32A3F991878FEDFFAC347E07BD9D7741CAD3D83B1BAE7DB56257DDD252AEB6CFB77F5DD516F5DDB3A54776E69FE7C33DE1C9B934CF7ADC0AF0082B063B05F0B7F2B6B95096C459A7B70D1C7BF660C7117DE8A9F7F5B67224B1AF0EE7C5F07BDF0EEBDD93F0A629DC47A9C7F48B91CAF456BA3C816D7BC07BA723B7A87945A14611348B79C376CA4E89A7B4EA0E724B810EB41E19E8F97DDA0F18A1417895CED97C40CACF0E74F
	703E15C7B86E59A65E37C560CEDAF8DF2B404DA4E72EB8A8AF90384F54103ED0DEA6F0FF2B91638A5C6D869E6BD756F35C5CDE9E57F78B5CCF4437A8F7BD0F573786C36B4824FA3FF378462F297BEF689854EE4B7AF0CFCDGF917813CGE0EC00BD9D209E408AB0GF08DE096C0BCC0A240D20015GEBGB6B6A669CD0D5C4E3FF49E0E5CD163D316A0A1466E37516F2D54472EEB633F5B2C84DA6EC6FE2E7E41FA0C51A6F42E363A94375CC8BC96BDCF1FEE4379A4F7539A79F95AC06E1BAC0772EA000DCD7CCE
	E5E19E77713B9157444F7AFB73F9AEE5D69327E14F4736CB203C826085G3A0F5F6C12A46EB73EF989FF4F9F161BE886460BDEE900056AFE2544D4817F6FC3DDCDAC1660BE7A4EABD8223CBDE3667299AD1C37CF65125F39C25981F04D426765973D517713645E717713BF1075A243BE00E9EB0A7BE3AF79C6CE53422C5DF33DB982579A687086DC682528CE3E14976E633FCD7612D940E32C057B49A397424FE7CB504D3273D95C058FF3DEFC9446C01D7E9DE2GBA7E87E4BFDA8E781596BE07FFF2074613AD63
	0D17A105F2981163659FCD52E7337128ECF72C8D177F3BE4CF5B8D6D4A0FF2796F2A89175FAA0778492D9D0F9F3D944A01C47253FB146EE9488F0D4BFEA472606E23B41E1B8E4F59C769B7F14A63A3E45E6783AE61A80F43D6B84763037F460F7CDE9B7AC94D21B84C886F77B01E284DB475C5374D363E71ED3333CF5B571D6F9B6D6BAE75E97BBAC71F5A57D11C2B8F67369F762CEE514EA3F06785AEC0E8A26E1C4B218C887C88618B71ADB28E48715789DF4384574DF053D538738C77AFFD383EC064812F1F77
	7D8479BBA4CE175B4DFE7BCA5A38BC305C04BEBECBF8F479ECAA3E67885CF9097B176C36714E3026E62D7BBBBB466D97F91F07766BA27DEA1BA716776AE8663F5D3F056C590E664873DA8C417B1AA34FEB5B0CFCAF3F86F5494D628E49562965B27EBC45FE667C2965C09EA77E45F2ACD25EE4026FB896A92FAB1F4B5B83F50947383CA9E675783BDB4275DE5D926DFC2CB7909F5292B9FE8E41F737C40E7F39DCBE3E836A9637703DF4BE9E2318BFAC3C00712F06BFF400BB747603C344FCFDE5B1BF937D9C1791
	FBEF3A9A2273C4536468B85DA1BAA6EF084E2DB5132333987FC482137799830FD5CADA414F3672D8E13F117B63B92D5C9F7F69B236DD8A65D5G69EDDCB73B83A1DE6EF35E18D8619806D76C0ECD22813F3738BCEE17C7EE22FFA1C2623970C192A79582EF15EDBC967EBC0919DB205C53466D69B6BC62B05B7E0C6AFD1771464742557F00B1FE1D9F1269772AF7EF7E0D9FFB468D9D034E291E1C0D86E0BE2F09F9BEA94ECBB345BCE799C232EFD13DFFA977466BE161BFE00C7F99737E9202365FFBE95CFE5AFE
	2F5A087D1637CD4C6F2534F13FE76FA5397063117B301B60F30EC76E434FDF9639F0281BF61C4714535BB0BED170F8047B0A446F564CA16BFE8534CB91ED3FE54860EDA53E77DD99C29B2075BBEB54F94112F6F2DF03442C26F69E9F4AB593091BF91C6E03FE535BC37161ECF8EF12683DC6EA2327483E9A877804F6EE3743179177A814D734F33BF57506EC552FFA7F5B659BEFD3465E9BB9861D078707C373702F0689788CF4DF2C7D719A6C4B7E1E182C3E97D17E1EC117A6C579965FCEEDD6BA913A97B1B99F
	D471FE08E727D15B8EBFA073DB8EE3541E60F678094B236D500AFFE60165ADD22F234D39CF70736D3AA07A73E8F98774F8B4D34AE9452606783AFAF9223A24E3E5DC47B590E074685F7EFB72528FC10878E7258C10AB6017F2C7DFC9E87844AF9E0F3FECB16AA4DD2B34F600047A2DD246B01E990298DE5C8F13D096F8DB7A004C851E9D56AB52663381590469E6934FCDAB52EB8DEE37F7B8E032596DF2A0E0D23CA6E5G9A091BD4A67A9BEFC54A8A61CACBCB012DEC41D6ABB9B2A43F335F1C763BBB52F4D2BA
	70C40FD02DD2BA1E0300B5BFE5B18D0ED004C13EF4113D3504D1B4FE9D4362A46A4230628A42B8ED799526C323A8ECE694B200772C3059F9604BD53FDF721B2EC01A3436D55A06076ACE59CF726232836F9633BFD055AC2D73E1429E2F6C01E081724888A8A3EE39DDB2BA4F600F6D830A6CE86019E050DA9E7931DD99E6154ADAEC018185CF1BB56DD23A877F141614CB2B5DB6CFFF99B52A9A221D5406FAE27AE49386FE8D04DF24D2AA28983E1F3D676536958B53A4DD33345E6D35B91AEDF6B054D6E91D42EF
	6985FDC7A8214CE17E1B60F4A0D7A044C7E1CB717B36E07AEA708BB333D6E6B5034A02FE290C34449CC229E861E89A30017DA8323FD541BF8D66756F96F5B76DD4DE7B49312DBDBAA9158909D3109374A952DA27ED50659E01978B090B49649821C8CF2A4222D099254DA00F724BE79F1A7B2B5A14D5C02CD94AA00688AC6504DA1D9C9A6C137DB747FFFA4706D9499FC70EFDA8AE273C8ECE7A1EG7E6A91081BA2E466D5027552CFBDD47E69BB7FFA01309E6A2AC83B68A4C15732FE59BBD85660F4C264AB8840
	0C81799D02BC36A90DE8139B7CE4DE5BE3AF750105D924344172F272EF8579F79F7EDBC1B09584D3318F26ADC59A733F487AA0171918A5A994208D8571ADBFA89682DFB038FB26A9D286E6604C30386945CF93397989E5438917470497B53DFE59C4AFEB1A890DCD44B8FB4918E4B7A7585CD1D6160668ADE22E313413B4B511FFDF1D34FC58EB8F132B1715CBC3F2E5B2401DDC7F4F47E9556899A0793EFFBECEEBC31C26B1CE53A2B825FC512DC91B5374511CEE43FFF67C5F72DC4F4ACD41300D1E5B378E1D16
	5A5BE9EA7B7FD9862E77635A96122AC8DB581DE7D3ABA6B3DDCE171DFC2D7DE79626B3486343626B6A78F04D4776C63DB73B260B46059B4FC57E0E3C5BCE728700F71C63B15A3C3EB72568F720BAE1AFF3ABB67755860F836FC3B50EA8F268FB6CC5BB528F70B62C1265EE6D487795D51F7F85D0CB8788FF100016B19DGG6CD9GGD0CB818294G94G88G88G4FEE0FAEFF100016B19DGG6CD9GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81
	GBAGGGEB9DGGGG
**end of data**/
}

	/**
	 * Return the JButtonAdvanced property value.
	 * @return javax.swing.JButton
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JButton getJButtonProjection() {
	if (ivjJButtonProjection == null) {
		try {
			ivjJButtonProjection = new javax.swing.JButton();
			ivjJButtonProjection.setName("JButtonProjection");
			ivjJButtonProjection.setToolTipText("Allows changes to the Projection values");
			ivjJButtonProjection.setText("Projection...");
			// user code begin {1}
			
			ivjJButtonProjection.setText("Projection..." + ILMControlAreaTrigger.PROJ_TYPE_NONE );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonProjection;
}

	/**
	 * Return the JCheckBoxPeakTracking property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getJCheckBoxPeakTracking() {
	if (ivjJCheckBoxPeakTracking == null) {
		try {
			ivjJCheckBoxPeakTracking = new javax.swing.JCheckBox();
			ivjJCheckBoxPeakTracking.setName("JCheckBoxPeakTracking");
			ivjJCheckBoxPeakTracking.setMnemonic('u');
			ivjJCheckBoxPeakTracking.setText("Use Peak Tracking");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxPeakTracking;
}

	/**
	 * Return the JComboBoxNormalState property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JComboBox getJComboBoxNormalState() {
		if (ivjJComboBoxNormalState == null) {
			try {
				ivjJComboBoxNormalState = new javax.swing.JComboBox();
				ivjJComboBoxNormalState.setName("JComboBoxNormalState");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJComboBoxNormalState;
	}


	/**
	 * Return the JComboBoxType property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JComboBox getJComboBoxType() {
		if (ivjJComboBoxType == null) {
			try {
				ivjJComboBoxType = new javax.swing.JComboBox();
				ivjJComboBoxType.setName("JComboBoxType");
				// user code begin {1}
	
				ivjJComboBoxType.addItem( ILMControlAreaTrigger.TYPE_THRESHOLD );
				ivjJComboBoxType.addItem( ILMControlAreaTrigger.TYPE_STATUS );
				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJComboBoxType;
	}


	/**
	 * Return the JLabelMinRestOffset property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelMinRestOffset() {
		if (ivjJLabelMinRestOffset == null) {
			try {
				ivjJLabelMinRestOffset = new javax.swing.JLabel();
				ivjJLabelMinRestOffset.setName("JLabelMinRestOffset");
				ivjJLabelMinRestOffset.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelMinRestOffset.setText("Min Restore Offset:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelMinRestOffset;
	}


	/**
	 * Return the JLabelNormalStateAndThreshold property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelNormalStateAndThreshold() {
		if (ivjJLabelNormalStateAndThreshold == null) {
			try {
				ivjJLabelNormalStateAndThreshold = new javax.swing.JLabel();
				ivjJLabelNormalStateAndThreshold.setName("JLabelNormalStateAndThreshold");
				ivjJLabelNormalStateAndThreshold.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelNormalStateAndThreshold.setText("Normal State:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelNormalStateAndThreshold;
	}


	/**
	 * Return the JLabelType property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelType() {
		if (ivjJLabelType == null) {
			try {
				ivjJLabelType = new javax.swing.JLabel();
				ivjJLabelType.setName("JLabelType");
				ivjJLabelType.setFont(new java.awt.Font("dialog", 0, 14));
				ivjJLabelType.setText("Type:");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelType;
	}


	/**
	 * Return the JPanelDevicePointPeak property value.
	 * @return com.cannontech.common.gui.util.JPanelDevicePoint
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.JPanelDevicePoint getJPanelDevicePointPeak() {
	if (ivjJPanelDevicePointPeak == null) {
		try {
			ivjJPanelDevicePointPeak = new com.cannontech.common.gui.util.JPanelDevicePoint();
			ivjJPanelDevicePointPeak.setName("JPanelDevicePointPeak");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelDevicePointPeak;
}

	/**
	 * Return the JPanelPeakTracking property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelPeakTracking() {
	if (ivjJPanelPeakTracking == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Peak Tracking");
			ivjJPanelPeakTracking = new javax.swing.JPanel();
			ivjJPanelPeakTracking.setName("JPanelPeakTracking");
			ivjJPanelPeakTracking.setBorder(ivjLocalBorder);
			ivjJPanelPeakTracking.setLayout(new java.awt.GridBagLayout());
			ivjJPanelPeakTracking.setFont(new java.awt.Font("Arial", 1, 12));

			java.awt.GridBagConstraints constraintsJCheckBoxPeakTracking = new java.awt.GridBagConstraints();
			constraintsJCheckBoxPeakTracking.gridx = 1; constraintsJCheckBoxPeakTracking.gridy = 1;
			constraintsJCheckBoxPeakTracking.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJCheckBoxPeakTracking.ipadx = 132;
			constraintsJCheckBoxPeakTracking.ipady = -5;
			constraintsJCheckBoxPeakTracking.insets = new java.awt.Insets(2, 5, 1, 57);
			getJPanelPeakTracking().add(getJCheckBoxPeakTracking(), constraintsJCheckBoxPeakTracking);

			java.awt.GridBagConstraints constraintsJPanelDevicePointPeak = new java.awt.GridBagConstraints();
			constraintsJPanelDevicePointPeak.gridx = 1; constraintsJPanelDevicePointPeak.gridy = 2;
			constraintsJPanelDevicePointPeak.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelDevicePointPeak.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelDevicePointPeak.weightx = 1.0;
			constraintsJPanelDevicePointPeak.weighty = 1.0;
			constraintsJPanelDevicePointPeak.ipadx = 33;
			constraintsJPanelDevicePointPeak.ipady = 6;
			constraintsJPanelDevicePointPeak.insets = new java.awt.Insets(1, 8, 5, 5);
			getJPanelPeakTracking().add(getJPanelDevicePointPeak(), constraintsJPanelDevicePointPeak);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPeakTracking;
}

	/**
	 * Return the JPanelProjectionFlowLayout property value.
	 * @return java.awt.FlowLayout
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private java.awt.FlowLayout getJPanelProjectionFlowLayout() {
		java.awt.FlowLayout ivjJPanelProjectionFlowLayout = null;
		try {
			/* Create part */
			ivjJPanelProjectionFlowLayout = new java.awt.FlowLayout();
			ivjJPanelProjectionFlowLayout.setVgap(2);
			ivjJPanelProjectionFlowLayout.setHgap(0);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		};
		return ivjJPanelProjectionFlowLayout;
	}


	/**
	 * Return the JPanelTriggerID property value.
	 * @return com.cannontech.common.gui.util.JPanelDevicePoint
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.JPanelDevicePoint getJPanelTriggerID() {
		if (ivjJPanelTriggerID == null) {
			try {
				com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
				ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
				ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
				ivjLocalBorder1.setTitle("Trigger Identification");
				ivjJPanelTriggerID = new com.cannontech.common.gui.util.JPanelDevicePoint();
				ivjJPanelTriggerID.setName("JPanelTriggerID");
				ivjJPanelTriggerID.setBorder(ivjLocalBorder1);
				ivjJPanelTriggerID.setFont(new java.awt.Font("Arial", 1, 12));
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJPanelTriggerID;
	}


	/**
	 * Return the JTextFieldMinRestOffset property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getJTextFieldMinRestOffset() {
		if (ivjJTextFieldMinRestOffset == null) {
			try {
				ivjJTextFieldMinRestOffset = new javax.swing.JTextField();
				ivjJTextFieldMinRestOffset.setName("JTextFieldMinRestOffset");
				// user code begin {1}
	
				ivjJTextFieldMinRestOffset.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(-99999.9999, 99999.9999, 4) );
				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldMinRestOffset;
	}


	/**
	 * Return the JTextFieldThreshold property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getJTextFieldThreshold() {
		if (ivjJTextFieldThreshold == null) {
			try {
				ivjJTextFieldThreshold = new javax.swing.JTextField();
				ivjJTextFieldThreshold.setName("JTextFieldThreshold");
				// user code begin {1}
	
				ivjJTextFieldThreshold.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( -999999.99999999, 999999.99999999, 8 ) );
	
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldThreshold;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (3/21/2001 2:08:57 PM)
	 * @return int
	 * @param state java.lang.String
	 */
	private int getNormalStateIndex(String state) 
	{
		for( int i = 0; i < getJComboBoxNormalState().getItemCount(); i++ )
		{
			if( getJComboBoxNormalState().getItemAt(i).toString().equalsIgnoreCase(state) )
				return i;
		}
	
		//error
		return -1;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (2/6/2002 9:51:17 AM)
	 * @return java.lang.Integer
	 */
	public Integer getSelectedNormalState() 
	{
		if( getJComboBoxNormalState().getSelectedItem() != null )
		{
			return new Integer( ((com.cannontech.database.data.lite.LiteState)
						getJComboBoxNormalState().getSelectedItem()).getStateRawState() );
		}
		else
			return null;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (2/6/2002 9:51:17 AM)
	 * @return java.lang.Integer
	 */
	public Integer getSelectedPointID() 
	{
		if( getJPanelTriggerID().getSelectedPoint() != null )
		{
			return new Integer( ((com.cannontech.database.data.lite.LitePoint)
						getJPanelTriggerID().getSelectedPoint()).getPointID() );
		}
		else
			return null;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (2/6/2002 9:51:17 AM)
	 * @return java.lang.Integer
	 */
	public Double getSelectedThreshold() 
	{
		try
		{
			return new Double( getJTextFieldThreshold().getText() );
		}
		catch( NumberFormatException e )
		{
			return new Double(0.0);
		}
	
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (2/6/2002 9:51:17 AM)
	 * @return java.lang.Integer
	 */
	public String getSelectedType() 
	{
		return getJComboBoxType().getSelectedItem().toString();
	}


	/**
	 * Returns the LMTriggerProjectionPanel property value.
	 * @return LMTriggerProjectionPanel
	 */
	private LMTriggerProjectionPanel getJPanelTriggerProjPanel() 
	{
		if( triggerProjPanel == null ) 
		{
			triggerProjPanel = new LMTriggerProjectionPanel();
			triggerProjPanel.setName("LMTriggerProjectionPanel");
		}
	
		return triggerProjPanel;
	}


	/**
	 * getValue method comment.
	 */
	public Object getValue(Object o) 
	{
		LMControlAreaTrigger trigger = null ;
		if( o == null )
			trigger = new LMControlAreaTrigger();
		else
			trigger = (LMControlAreaTrigger)o;
	
		
		trigger.setPointID( getSelectedPointID() );
		trigger.setTriggerType( getSelectedType() );
	
		if( trigger.getTriggerType().equalsIgnoreCase(ILMControlAreaTrigger.TYPE_STATUS) )
		{
			trigger.setNormalState( getSelectedNormalState() );
			trigger.setThreshold( new Double(0.0) );
		}
		else
		{
			trigger.setNormalState( new Integer(ILMControlAreaTrigger.INVALID_INT_VALUE) );
			trigger.setThreshold( getSelectedThreshold() );
	
			try
			{
				trigger.setMinRestoreOffset( new Double(getJTextFieldMinRestOffset().getText()) );
			}
			catch( NumberFormatException e )
			{
				trigger.setMinRestoreOffset( new Double(0.0) );
			}
			
		}
	
		if( getJCheckBoxPeakTracking().isSelected() 
			 && getJPanelDevicePointPeak().getSelectedPoint() != null )
		{
			trigger.setPeakPointID( 
				new Integer(getJPanelDevicePointPeak().getSelectedPoint().getPointID()) );
		}
		else
			trigger.setPeakPointID( new Integer(0) );
	
	
		//get the projection panels values
		getJPanelTriggerProjPanel().getValue( trigger );
	
		return trigger;
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
	
		getJPanelTriggerID().addComboBoxPropertyChangeListener( this );
		getJPanelDevicePointPeak().addComboBoxPropertyChangeListener( this );
				
		// user code end
		getJComboBoxType().addActionListener(this);
		getJTextFieldThreshold().addCaretListener(this);
		getJTextFieldMinRestOffset().addCaretListener(this);
		getJComboBoxNormalState().addActionListener(this);
		getJCheckBoxPeakTracking().addActionListener(this);
		getJButtonProjection().addActionListener(this);
	}


	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMControlAreaTriggerModifyPanel");
		setToolTipText("");
		setPreferredSize(new java.awt.Dimension(303, 194));
		setLayout(new java.awt.GridBagLayout());
		setSize(347, 288);
		setMinimumSize(new java.awt.Dimension(10, 10));

		java.awt.GridBagConstraints constraintsJLabelType = new java.awt.GridBagConstraints();
		constraintsJLabelType.gridx = 1; constraintsJLabelType.gridy = 1;
		constraintsJLabelType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelType.ipadx = 9;
		constraintsJLabelType.ipady = -2;
		constraintsJLabelType.insets = new java.awt.Insets(7, 8, 8, 4);
		add(getJLabelType(), constraintsJLabelType);

		java.awt.GridBagConstraints constraintsJComboBoxType = new java.awt.GridBagConstraints();
		constraintsJComboBoxType.gridx = 2; constraintsJComboBoxType.gridy = 1;
		constraintsJComboBoxType.gridwidth = 3;
		constraintsJComboBoxType.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxType.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxType.weightx = 1.0;
		constraintsJComboBoxType.ipadx = 18;
		constraintsJComboBoxType.insets = new java.awt.Insets(6, 5, 3, 1);
		add(getJComboBoxType(), constraintsJComboBoxType);

		java.awt.GridBagConstraints constraintsJLabelNormalStateAndThreshold = new java.awt.GridBagConstraints();
		constraintsJLabelNormalStateAndThreshold.gridx = 1; constraintsJLabelNormalStateAndThreshold.gridy = 2;
		constraintsJLabelNormalStateAndThreshold.gridwidth = 2;
		constraintsJLabelNormalStateAndThreshold.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelNormalStateAndThreshold.ipadx = 7;
		constraintsJLabelNormalStateAndThreshold.ipady = -2;
		constraintsJLabelNormalStateAndThreshold.insets = new java.awt.Insets(5, 8, 5, 5);
		add(getJLabelNormalStateAndThreshold(), constraintsJLabelNormalStateAndThreshold);

		java.awt.GridBagConstraints constraintsJComboBoxNormalState = new java.awt.GridBagConstraints();
		constraintsJComboBoxNormalState.gridx = 3; constraintsJComboBoxNormalState.gridy = 2;
		constraintsJComboBoxNormalState.gridwidth = 3;
		constraintsJComboBoxNormalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxNormalState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxNormalState.weightx = 1.0;
		constraintsJComboBoxNormalState.ipadx = 103;
		constraintsJComboBoxNormalState.insets = new java.awt.Insets(2, 5, 2, 8);
		add(getJComboBoxNormalState(), constraintsJComboBoxNormalState);

		java.awt.GridBagConstraints constraintsJTextFieldThreshold = new java.awt.GridBagConstraints();
		constraintsJTextFieldThreshold.gridx = 3; constraintsJTextFieldThreshold.gridy = 2;
		constraintsJTextFieldThreshold.gridwidth = 3;
		constraintsJTextFieldThreshold.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldThreshold.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldThreshold.weightx = 1.0;
		constraintsJTextFieldThreshold.ipadx = 225;
		constraintsJTextFieldThreshold.ipady = 3;
		constraintsJTextFieldThreshold.insets = new java.awt.Insets(2, 5, 2, 8);
		add(getJTextFieldThreshold(), constraintsJTextFieldThreshold);

		java.awt.GridBagConstraints constraintsJLabelMinRestOffset = new java.awt.GridBagConstraints();
		constraintsJLabelMinRestOffset.gridx = 1; constraintsJLabelMinRestOffset.gridy = 3;
		constraintsJLabelMinRestOffset.gridwidth = 3;
		constraintsJLabelMinRestOffset.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinRestOffset.ipadx = 3;
		constraintsJLabelMinRestOffset.ipady = -2;
		constraintsJLabelMinRestOffset.insets = new java.awt.Insets(5, 8, 3, 3);
		add(getJLabelMinRestOffset(), constraintsJLabelMinRestOffset);

		java.awt.GridBagConstraints constraintsJTextFieldMinRestOffset = new java.awt.GridBagConstraints();
		constraintsJTextFieldMinRestOffset.gridx = 4; constraintsJTextFieldMinRestOffset.gridy = 3;
		constraintsJTextFieldMinRestOffset.gridwidth = 2;
		constraintsJTextFieldMinRestOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMinRestOffset.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldMinRestOffset.weightx = 1.0;
		constraintsJTextFieldMinRestOffset.ipadx = 197;
		constraintsJTextFieldMinRestOffset.insets = new java.awt.Insets(3, 3, 2, 7);
		add(getJTextFieldMinRestOffset(), constraintsJTextFieldMinRestOffset);

		java.awt.GridBagConstraints constraintsJPanelPeakTracking = new java.awt.GridBagConstraints();
		constraintsJPanelPeakTracking.gridx = 1; constraintsJPanelPeakTracking.gridy = 5;
		constraintsJPanelPeakTracking.gridwidth = 5;
		constraintsJPanelPeakTracking.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelPeakTracking.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelPeakTracking.weightx = 1.0;
		constraintsJPanelPeakTracking.weighty = 1.0;
		constraintsJPanelPeakTracking.ipadx = -6;
		constraintsJPanelPeakTracking.insets = new java.awt.Insets(3, 8, 5, 9);
		add(getJPanelPeakTracking(), constraintsJPanelPeakTracking);

		java.awt.GridBagConstraints constraintsJPanelTriggerID = new java.awt.GridBagConstraints();
		constraintsJPanelTriggerID.gridx = 1; constraintsJPanelTriggerID.gridy = 4;
		constraintsJPanelTriggerID.gridwidth = 5;
		constraintsJPanelTriggerID.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTriggerID.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelTriggerID.weightx = 1.0;
		constraintsJPanelTriggerID.weighty = 1.0;
		constraintsJPanelTriggerID.ipadx = 89;
		constraintsJPanelTriggerID.insets = new java.awt.Insets(2, 8, 2, 9);
		add(getJPanelTriggerID(), constraintsJPanelTriggerID);

		java.awt.GridBagConstraints constraintsJButtonProjection = new java.awt.GridBagConstraints();
		constraintsJButtonProjection.gridx = 5; constraintsJButtonProjection.gridy = 1;
		constraintsJButtonProjection.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButtonProjection.insets = new java.awt.Insets(5, 17, 2, 7);
		add(getJButtonProjection(), constraintsJButtonProjection);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	

	//act like the ComboBox was changed	
	jComboBoxType_ActionPerformed( null );

	// user code end
}

	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 */
	public boolean isInputValid() 
	{
		if( getJPanelTriggerID().getSelectedDevice() == null
			 || getJPanelTriggerID().getSelectedPoint() == null
			 || getJComboBoxType().getSelectedItem() == null )
		{
			setErrorString("A trigger type, device and point must be specified.");
			return false;
		}
	
		if( getJComboBoxType().getSelectedItem().toString().equalsIgnoreCase(ILMControlAreaTrigger.TYPE_THRESHOLD) )
		{
			try
			{			
				if( getJTextFieldThreshold().getText() == null
					 || getJTextFieldThreshold().getText().length() <= 0
					 || Double.parseDouble(getJTextFieldThreshold().getText()) > Double.MAX_VALUE )
				{
					setErrorString("The threshold for this trigger must be a valid number.");
					return false;
				}
			}
			catch( NumberFormatException e )
			{
				return false;
			}
		}
	
		return true;
	}


	/**
	 * Comment
	 */
	public void jButtonProjection_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
	{
		OkCancelDialog d = new OkCancelDialog( 
				CtiUtilities.getParentFrame(this),
				"Projection Properties",
				true,
				getJPanelTriggerProjPanel() );
		
		d.setLocationRelativeTo( this );
		d.setCancelButtonVisible( false );
		d.setSize( new Dimension(280, 200) );
		d.show();
		
		d.dispose();

		//set the text of the button to the type of projection used
		getJButtonProjection().setText( 
			"Projection..." + getJPanelTriggerProjPanel().getSelectedType() );
		
		fireInputUpdate();
		return;
	}


	/**
	 * Comment
	 */
	public void jCheckBoxPeakTracking_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
	{
		setTrackingEnabled( getJCheckBoxPeakTracking().isSelected() );
	
		fireInputUpdate();
		return;
	}


	/**
	 * Comment
	 */
	public void jComboBoxType_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
	{
		if( getJComboBoxType().getSelectedItem().toString().equalsIgnoreCase( ILMControlAreaTrigger.TYPE_THRESHOLD ) )
		{
	
			getJLabelNormalStateAndThreshold().setText( ILMControlAreaTrigger.TYPE_THRESHOLD + ":");
			getJComboBoxNormalState().setVisible(false);
			getJTextFieldThreshold().setVisible(true);
			getJLabelMinRestOffset().setEnabled(true);
			getJTextFieldMinRestOffset().setEnabled(true);
			
			getJCheckBoxPeakTracking().setEnabled(true);
	
			//initDeviceComboBox( LMControlAreaTrigger.TYPE_THRESHOLD );
			int[] ptType =
			{
				com.cannontech.database.data.point.PointTypes.ANALOG_POINT,
				com.cannontech.database.data.point.PointTypes.DEMAND_ACCUMULATOR_POINT,
				com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT,
				com.cannontech.database.data.point.PointTypes.CALCULATED_POINT
			};
	
			
			getJButtonProjection().setEnabled( true );
			getJPanelTriggerID().setPointTypeFilter( ptType );
		}
		else
		{
			getJLabelNormalStateAndThreshold().setText("Normal State:");
			getJComboBoxNormalState().setVisible(true);
			getJTextFieldThreshold().setVisible(false);
			getJLabelMinRestOffset().setEnabled(false);
			getJTextFieldMinRestOffset().setEnabled(false);
	
			getJCheckBoxPeakTracking().setSelected(false);
			getJCheckBoxPeakTracking().setEnabled(false);
			setTrackingEnabled(false);
	
			//initDeviceComboBox( LMControlAreaTrigger.TYPE_STATUS );		
			int[] ptType =
			{
				com.cannontech.database.data.point.PointTypes.STATUS_POINT
			};
			

			getJButtonProjection().setEnabled( false );
			getJPanelTriggerID().setPointTypeFilter( ptType );
		}


	
		updateStates();
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
			LMControlAreaTriggerModifyPanel aLMControlAreaTriggerModifyPanel;
			aLMControlAreaTriggerModifyPanel = new LMControlAreaTriggerModifyPanel();
			frame.setContentPane(aLMControlAreaTriggerModifyPanel);
			frame.setSize(aLMControlAreaTriggerModifyPanel.getSize());
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
			com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
		}
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (2/22/2002 12:22:36 PM)
	 */
	public void propertyChange(java.beans.PropertyChangeEvent evt)
	{
		if( evt.getPropertyName().equalsIgnoreCase(getJPanelTriggerID().PROPERTY_PAO_UPDATE)
			 || evt.getPropertyName().equalsIgnoreCase(getJPanelTriggerID().PROPERTY_POINT_UPDATE) )
		{
			if( evt.getSource() == getJPanelTriggerID() )
			{			
				updateStates();
			}
	
			if( evt.getSource() == getJPanelDevicePointPeak() )
			{
				fireInputUpdate();
			}
		}
	
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (5/15/2002 1:25:53 PM)
	 * @param value boolean
	 */
	private void setTrackingEnabled(boolean value) 
	{
		for( int i = 0; i < getJPanelDevicePointPeak().getComponentCount(); i++ )
			getJPanelDevicePointPeak().getComponent(i).setEnabled( value );
	
	}


	/**
	 * setValue method comment.
	 */
	public void setValue(Object o) 
	{
		LMControlAreaTrigger trigger = null ;
	
		if( o == null )
			return;
		else
			trigger = (LMControlAreaTrigger)o;
	
		com.cannontech.database.data.lite.LiteYukonPAObject litePAO = null;
		com.cannontech.database.data.lite.LitePoint litePoint = null;
		com.cannontech.database.data.lite.LiteState liteState = null;
		
		// look for the litePoint here 
		litePoint = com.cannontech.database.cache.functions.PointFuncs.getLitePoint(trigger.getPointID().intValue());
		
		if( litePoint == null )
			throw new RuntimeException("Unable to find the point (ID= " + trigger.getPointID() + ") associated with the LMTrigger of type '" + trigger.getTriggerType() + "'" );
	
		// look for the litePAO here 
		litePAO = com.cannontech.database.cache.functions.PAOFuncs.getLiteYukonPAO( litePoint.getPaobjectID() );
	
		//set the states for the row
		liteState = com.cannontech.database.cache.functions.StateFuncs.getLiteState( ((com.cannontech.database.data.lite.LitePoint)litePoint).getStateGroupID(), trigger.getNormalState().intValue() );
	
		if( trigger.getTriggerType().equalsIgnoreCase(ILMControlAreaTrigger.TYPE_STATUS) )
		{
			if( liteState == null )
				throw new RuntimeException("Unable to find the rawState value of " + 
						trigger.getNormalState() + ", associated with the LMTrigger for the point id = '" + 
						trigger.getPointID() + "'" );
	
			
			getJComboBoxType().setSelectedItem( trigger.getTriggerType() );
			getJPanelTriggerID().setSelectedLitePAO( litePAO.getYukonID() );
			getJPanelTriggerID().setSelectedLitePoint( litePoint.getPointID() );
			getJComboBoxNormalState().setSelectedItem( liteState );
	
			getJCheckBoxPeakTracking().setEnabled(false);
		}
		else
		{
			getJComboBoxType().setSelectedItem( trigger.getTriggerType() );
			getJPanelTriggerID().setSelectedLitePAO( litePAO.getYukonID() );
			getJPanelTriggerID().setSelectedLitePoint( litePoint.getPointID() );
			getJTextFieldThreshold().setText( trigger.getThreshold().toString() );
	
			getJCheckBoxPeakTracking().setEnabled(true);
		}
	
	
		getJCheckBoxPeakTracking().setSelected( trigger.getPeakPointID().intValue() > 0 );
		setTrackingEnabled( trigger.getPeakPointID().intValue() > 0 );
			
		if( trigger.getPeakPointID().intValue() > 0 )
		{
			com.cannontech.database.data.lite.LitePoint lp =
					com.cannontech.database.cache.functions.PointFuncs.getLitePoint( trigger.getPeakPointID().intValue() );
					
			getJPanelDevicePointPeak().setSelectedLitePAO( lp.getPaobjectID() );
			getJPanelDevicePointPeak().setSelectedLitePoint( lp.getPointID() );
		}
	
	
		//always do the following settings
		getJTextFieldMinRestOffset().setText( trigger.getMinRestoreOffset().toString() );
		
		
		//set the projection panels values
		getJButtonProjection().setEnabled(
			trigger.getTriggerType().equalsIgnoreCase(ILMControlAreaTrigger.TYPE_THRESHOLD) );

		getJPanelTriggerProjPanel().setValue( trigger );
		
	
		//set the text of the button to the type of projection used
		getJButtonProjection().setText( 
			"Projection..." + trigger.getProjectionType() );
	}


	/**
	 * Comment
	 */
	private void updateStates()
	{
		getJComboBoxNormalState().removeAllItems();
	
		if( getJComboBoxNormalState().isVisible() && getJPanelTriggerID().getSelectedPoint() != null )
		{
			//set the states for the JCombobox
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List allStateGroups = cache.getAllStateGroups();
				
				int stateGroupID = ((com.cannontech.database.data.lite.LitePoint)getJPanelTriggerID().getSelectedPoint()).getStateGroupID();
				
				//Load the state table combo box
				for(int i=0;i<allStateGroups.size();i++)
				{
					com.cannontech.database.data.lite.LiteStateGroup stateGroup = (com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i);
	
					if( stateGroup.getStateGroupID() == stateGroupID )
					{
						java.util.Iterator stateIterator = stateGroup.getStatesList().iterator();				
						while( stateIterator.hasNext() )
						{
							getJComboBoxNormalState().addItem( (com.cannontech.database.data.lite.LiteState)stateIterator.next() );
						}
	
						break;
					}
	
				}
	
			}
			
		}
	
		return;
	}
}
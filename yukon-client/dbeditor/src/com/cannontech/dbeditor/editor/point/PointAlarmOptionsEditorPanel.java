package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import java.util.List;

import com.cannontech.clientutils.tags.IAlarmDefs;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.customer.Contact;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.dbeditor.wizard.contact.QuickContactPanel;

public class PointAlarmOptionsEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private PointAlarmOptionsEditorTableModel tableModel = null;
	private javax.swing.JPanel ivjConfigurationPanel = null;
	private javax.swing.JScrollPane ivjJScrollPaneAlarmStates = null;
	private javax.swing.JTable ivjJTableAlarmStates = null;
	public static final LiteContact NONE_LITE_CONTACT =
			new LiteContact( CtiUtilities.NONE_ID, 
					null, CtiUtilities.STRING_NONE );
	private javax.swing.JCheckBox ivjJCheckBoxNotifyWhenAck = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisableAllAlarms = null;
	private javax.swing.JComboBox ivjJComboBoxGroup = null;
	private javax.swing.JLabel ivjJLabelGroup = null;
	private javax.swing.JButton ivjJButtonNewContact = null;
	private javax.swing.JComboBox ivjJComboBoxContact = null;
	private javax.swing.JLabel ivjJLabelContact = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JCheckBox ivjJCheckBoxNotifyOnClear = null;
	private javax.swing.JLabel ivjJLabelNotifyOn = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PointAlarmOptionsEditorPanel.this.getJCheckBoxDisableAllAlarms()) 
				connEtoC1(e);
			if (e.getSource() == PointAlarmOptionsEditorPanel.this.getJCheckBoxNotifyWhenAck()) 
				connEtoC4(e);
			if (e.getSource() == PointAlarmOptionsEditorPanel.this.getJComboBoxContact()) 
				connEtoC3(e);
			if (e.getSource() == PointAlarmOptionsEditorPanel.this.getJButtonNewContact()) 
				connEtoC2(e);
			if (e.getSource() == PointAlarmOptionsEditorPanel.this.getJCheckBoxNotifyOnClear()) 
				connEtoC5(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointAlarmOptionsEditorPanel() {
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
	if (e.getSource() == getJCheckBoxDisableAllAlarms()) 
		connEtoC1(e);
	if (e.getSource() == getJCheckBoxNotifyWhenAck()) 
		connEtoC4(e);
	if (e.getSource() == getJComboBoxContact()) 
		connEtoC3(e);
	if (e.getSource() == getJButtonNewContact()) 
		connEtoC2(e);
	// user code begin {2}
	
	if (e.getSource() == getJComboBoxGroup()) 
		fireInputUpdate();
	
	// user code end
}
/**
 * connEtoC1:  (AlarmInhibitCheckBox.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
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
 * connEtoC2:  (NewEmailButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.newEmailButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.newContactButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JComboBox1.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (JCheckBoxNotifyWhenAck.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JCheckBoxNotifyOnClear.action.actionPerformed(java.awt.event.ActionEvent) --> PointAlarmOptionsEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
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
 * Looks the first email notificatoin type in the list passed in.  Returns a NONE_ID if
 * no email type is found.
 * @param contact
 * @return int
 */
private int findEmailContact( LiteContact contact )
{
	if( contact != null )
	{
		//find the first email address in the list ContactNotifications...then use it
		for( int j = 0; j < contact.getLiteContactNotifications().size(); j++  )
		{	
			LiteContactNotification ltCntNotif = 
					(LiteContactNotification)contact.getLiteContactNotifications().get(j);
						
			if( ltCntNotif.getNotificationCategoryID() == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL )
			{
				return ltCntNotif.getContactNotifID();
			}
		}
	}

	//no e-mail notif found
	return CtiUtilities.NONE_ID;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G0AF5B7B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D4D71531CD953F72D52A3826CA15D2DC29460AAEEDD997B5A22E34C5451536D8D12366F3D98BAEF52332964B4B64E7C289A1A4D052965A948612C07E8990C28A197CC082791FA1BF10163C4C3CA4C3DEE626B3AFA4D0E8771C7BB7AF13B7892125DB3E6FF05FBD775EF34E3D775CF34FBD77CE246D2F4DCE0CB3ED172438C4097DBB9AA7C94B3AA469402C5D439C97315BE216F47FDE8758A67D
	F7CB82247320EC7AE50BB9CD6A73A6C3FE916423D50BF9A7FCE7C86F9D49EC060F90FC56B5C9526F3E730B1FCC4CE76D9E4E47CC729F2BCC01B495A08AF0B1GC52FA27FFBAAB399BE9972296C9BA14EA4C931244E4C6ECC9DDEE37516C23A8AA062B52C3320F297AB2F00BC5631C3BA709A2D9B8669A6A25D9D65E52CDE775D73245847DEB1EDC1DEBA7A9C22D95B04AC1AE6C97713010817F63EF99A79348C761FF0F8068A9C325B6DF1EB0AE32040592BB8DD1A47D740922F4765564A4BDBB0312A32EF28592B
	39BCEEFFADA9EE115D0AAAE9D9A87F373B1389ED208BF969F544153C09E33690724B81D6E771BE7CECCB8513F16660BCA97A7E5410FD28F91D4ABE70FA208FD1EDCFA6E56EC17A3FA2F3026D66C1F9B4G15E51F154B523D074B72F0AB561D8979C800A582FF3D8D712BA09F8B1032174B78C6B61731731DC724753DA7CCDBC348E859CBE5EB589B10D1DD7C1669A522B7D7ACB8775320AC92A08AA086A041E2B1DBGDEA17ADA3A77G2456DE2D5AB3B464F1DB3D5EAA5569EB74B895953E4B4B2104615A940747
	6714A42A135F6B4EB31007C305E52CBEA2503EF5C8DC07A25B9E14B42D5414E8503785CF6CB635B09ACECB20EF11969C37EF2562380543B7364331217845843FCE0727F3D5C6F4016477D94CB37771F16ECC6532B4A44C11520AABCDE683D9A2969FB6256D27B2646C8F48C24758369F47588EF8AF40B40059GD1G517B79983F3B7750E4E3DC8FEB406716D55BF02FD776E9D543AE55297858D87F3EF22231B6825A6E59A9372363B26F10FE0ED67D4BC943F1D9B54FE25A4046E3FBF6E0DCA86F8356D03CCB33
	476BC62A184383E4CD54E553B90C9778D2026F5461695CAEA2EB8B7383D0368C0056FF1654AF4A2175B78BBA9B893EC50727F4EEA7F84CFB21EC85G1D5FE73351B6166502CD84D08142816682C4812C4865F37C685E429BDCC7AD3A7925EDEF333C8829B22A7964FA37DF135D8E25DD763964DED571CB2C4E1DE423689D53EE86D95F8E60F1DAF1ABBE994DA3D0170794D0A6BF2DF30FE59B5671A924546E9AD2004110D762F46F33E4C02A4AFE6D1C57A9EBCA2086ADFF4002BAF1D9EE870AC0G3EAD87F03C1E
	A4B6B7967A1FF6000F552B39089B00FC5881BEAE16FD69101E8565D65472729A17834510FDD7380EAECC45711D8975AD87AD6685G15F99673FA0030FC20DD40576A1FBA39CE56EC793A14774489C31DC418DDC075EBE1C130CEAEA53C2281AF95DA4CD9G15GBDGE1G338B79FEDB1E6AE534D0AEE1EB8F0E57DBAEE3B09E7B90EA0D2DB7A843FED35EDD02F73C2EBC51008E6EDBDBC32C61D03CB79B6010AF1D3F6987F08DC441B82C8258D0486770623EAC2A5BD532DF615A5C6C83C5D41C019925F53B7723
	CC4AE5452D55A8DE456D048FBF53A5477ECD81BD6F73889DF491BC5A3DEAD0C43F40FB76EF23F82021DFB53CFC04144B1A66F3758EEB0ACD51B4173BBF507EBDA20BFF0CC975333E5EF5907B3A3D48E24E8328A862FDCD4E4E5069EB0347B3B86C3563CAE4FAEFA57BFB8B549FA862F3D654067E5AF411AFA779C07962564E8966E42A00FC465B7ADFFE20BC62037214475F5A4021A5EB4E4C463810699BADCF34E1798210EF95C0C2B10CAD003D180F43A75BBAD97BC29D5D09741543ABB7D0077218C3F8446B68
	A60650FB2376B7A2C70674DCF6E89DB765986BF8F20E310E2766CC2C43AF669869B0AD5B1D03FD730C38F5EBE2CF8E5ADB07476DD6484294CDA46AFF7F490E7DF039DDDA074B592FE8B657D5C512669623EE2FA5B67D9AFC4FA866F6F7E4AF373B162B8FC8D197DFB27457A23EDCE6DADDCC75F2C3F1406ED219766662DAD3D4E588862A5AB36C56A8BE176C978C6F0FA738F75E894736B9E72A879447E015E73446654765E7D5D5F28870674924DF85B4E9D1FCFD9E5F10624C39A4DF168B64912D004CCB011594
	5762F7EED6FE7E092C094E91F9FDAE1FD26F768E33CDB0A737BD37084A7373BD9F9BABCF13C7F375DD6998D05CD6476007A54CA126539952FF9DFC174963996A7500BC60163B3CAE207CE149D2FC084B72C773D729ACD5431A66F1B7A9A355F016837E37C49437A0CF59DD3486D1EF88CD7EB848DDA2647E4469EBE34770D60AEDB802D40ECF9D3AED825DAA7E3044A96563F2345BD0571B5D552AA27BBEC4F1589E7D7DBC349B3BCAAC66A2003A923EC7FFB9974B799E4D9C11EA0F2AD21387E5D1A54457CF5D2E
	33D1BDA5B4F69289A9AD8F5BEAD41EFC0817472599156F926537991697097278EDC66529C23E42C94AAF04AC2763706983E938B7A8EF8FABF0E4C0ABF9D639427D7E8369C1E56D32BA9CB8CF5CF7G77C4D5675FD07C4C0346367A738751279994743FF8B0C942F38125C9771679B2B9BF79DCC3608E224346E2D01DD6FD8C2A86468763D56110C6G5036A96F20EFBE112E5415A22DF29B0FD54D0736CB81D6B3FAB925F4ECE83D67C53D19100F84D8D6EAD4EF311D57CBB98CE790001A43C675D2AE70FA61D0BE
	8BE0912B3761303E5EF2D12F6E881CADG420E0C2597F5846BA53C4963C40582778A417D929C11CFA67033F8E31ABE666674979B6E3F8889C7A97D4C2341B10A3F66A35DB240579D65EBA8B3CF6F67DA7DFE0F43C50E08CC8FFE14275F1BA9EED95E8B10421275EB7560740CF2DD7A0FBC14954F108C4D7461EB875F10461F09691472E3C47588B195F6CFB59CE36BC09D2B056B526ED2C620BD0D2B9D59AA62EAC739EFF27D14BE1637E86BFE536690E3B3130D49825D5894ADFE4BC4E9BFA2E8479E65F13C32B4
	0E4BAB6338FF95B8E799575BFB4AF1EFB60AE7BAC8278B7A07DD85431A8B8F3F32A693DB270FDDAEA6678924B52B0C4785BFDD03EBF0856457GA49C3318E90C42E1CBE1F5F7814EFE0C5B1963DDFABBB17D98BD1BAC00145A7547ECA510CE5DFC73F9DC121BCE67BD48D4F39D1D13D149F8AFFBF00ED4711E59B0DEB9ED6BB65352E3F47C579D8B4CC36779C6138AC07B18ED67FD2CA807F1AF67FDFC5A915C47C8A8DBDE4E7BF8177DE67BC879BEA37846G4DDD47B95FE77274FC6D47A95FEB47B95F15675F9F
	5F4397B85F99C0F329601B182467ABD5D03EB9951C6F2C8B6FEFCE3F317FA4246F5327E2FD78DF51078A10CF2B60FDB09DB61A33C851071E6E1B6D830BA51768EDE3D4743E1036B12A026A5B0A0AE05B78F4A12E3FF500CFF8035B46467CEDBA5B888E36A63B7BE10794673FFC149F76C2165F180FFC49DEA871F368712DD3093DE63E41E3E8BB0BF14C72A0DF875083B08A90A1E47BF8412431645A5189E2490B26A45742B778597D0D342934DBA65AFDF7CA6D22C53B9F0D0B9F473E9108FBA41F989FC323715D
	24DA62B31E2071DD33402793FC298ECFF5B70258DA4CB7C0597C931C3F4B964CFF7B4931BC0BCE9A45E1C64635AB884A5B035A5175F8229D67176CA3287923857E91173B3F3CDC7862EC3F70F5213E6EGD0CFF21D99AAC11FE3B96457GA4D4DA4CD9GB5GBDG6115DCF71EA8B05A0F476ACE284DB890FB237D4C375F5C789C9F57AEF8FC66D60EEDC7FD483335FA9FF2D1A55D6322A10D2D647B73B6E7B26313E26573786CCD4A39F1D2B9EB2CC673582F061AC7F656E47338A7DF78B3C2566397EECE567AF1
	6DDE8D4A4FB414F52B6DA5E3D969E6A0F15F2BA2090FEB2410F563CD4A3AE55C3DE17038468449CAE5B8F6184B30414A757E6B0790D7DA853ADE45F53C249063625CF6137304446D76EF8A714C6271397ADDEE0E0BA1B80747C702E8B4465BC67CC1BB50748A3A2F95070E796DAFB60E7965959B477CCA0AA70EEB1797070A89D279FEAE648B8720B8FB9A47C5885C938297A9F03FA67EE69464D788DCDCB56256C3BE2D1A4FEF8A35171037830C8AFCA64163BEBE3B1A1E3FA87E0BB513451F118E262355216FC6
	B814D69B694EA31372C854551F0CC7F00CFBB741BD7A62D6B2B7FE3F62A746903DF6D6761F5A8E49A0365AA48FA9920F214C36EDE2F5EAB5CFF53110FD244BC876A52C1FABD93AC657EF2946E25E8E10D3B3F6ACAAA05F826084982D514783C6CAC26B683BA546BA9ADE026B456F9976B914E0BD3C33A4D31485F602FACD9942AE33F881E92F493EFEE51C9EFF013427E5215A4FAD89256BF4BC1DF67DF816CA5CF67DCFAE52B630DD64F0C5ED466D355CE6CC2F61E72B67FAD1FF17C0FE95C0EC8D575F971DFA3B
	14D24B7CC41616560E57D9AADF4EB93DFC0744FCE767CF6477540524673B2027D7A268D91374743AD9FBAFCB43C352BB522D27F7D850BBF1D8CFAF02350FE4E91481BDBADF5FBA047E532848BFCC72D7443CA856D4B16735EEA7B340B43E61A2F66BBBA05893A60E7BA341BD8438ADF19CB7C86C561762753111B584F7B2C1126E96FD7B0B95ED37AADF718C830F1CD3A4CED3257793D6C0BFB6G241D227D21B2FEF3846384FDBEF988D44666D59CD242E9EC1BD94D5BFA21FEB840DC00A5GB1277819655EC3F8
	9686ED6EF3758F73ABF7BC4EB319AA5B71AE7F0C4D617328AA1610E816CD0393331F57D9F696772F10B79C9277F936276B7B52F01A7ABC2310CEBF4DED77BD449F1C8B79858277BC41C5C1FE2540BD29A0EE8D64136BB84E5D0738DDF554A726386984D786B83B2877CC953957077C2840BD71A26226C3FE3640DDA177B91110DFA2F01F3C0A3815F5B4DEC2F1732E727DE6FD9DDFFF53EA757AD8D7EFB1F75787F450DBBFDE9F29DFFF27033C852A27FD58A6703388FE3E8ECF7D7DB802477CAAA89BB843654CA7
	3EE3AC64934E0ABE3A901789799C011BBD48E5AF953893799C97AEF01D44E7DA817998017BC1A329877964860E4BE962ED378B5CB9626BB8A12F89DCD4996226C1BEC260769D63ED67B670F8D4F18F1E8504DD4493F4F979E91F4BD9A577B71015C2EDE38919D39C1B5886EE9BFB2AF0EC729A411F8268EE64F136517DBC4E96794FF32406301306EF66226D7BCDDAA31D27084640B9BBCC4459E6D5F31EAB20BC2589F10FD56BE38438BE1EB2D11F1016EF98D30E738647FFDDF9C10D3E9C650D0B5717770CA9CF
	543527FDFBFC9F6F5B064767CB6AC379A15F835A1B187F42531F3EACC525B519185FDF3BDBB25C0F8216C0623EECC3FAB263BF3F090FFDC3BB0E7DEA4CB7DB4C194D5C5E4C6F42383B869BF7D1B3BE815AFDDC9276EF47F934E3C37228EBE8F808DC64C6B693DDBE0F74B5788E93F456AA58AF9AE441631F6CF662FC127AC9E44D2F036F8D227E9DFDD83F570317ABADB298F145A9785ECF4A3CBE25CF717994A73DC2E6349691DA19AD2023AD1C16299A7B5067F1EB34CEA6510396A86F96F5563C18C47A62A33E
	B96D6B1D95013E4E48A5FDF539037ABA0F581C69C0A7C2501AFD95E35B836068DF45609618D5D73F9B6358D2CC8B36795E559A0379556B20192588CB208D428E2697AB617BBA7B266B70CB444F48EB3518ABDB795AEC3211B58C79D101FB5B4E576B2CD6EE776EF6E4C13A8672E9EDE0E7GEAGFAG2E814C82D858466B4EA1F571AEE39560B6G6C3251BB8B3C0F2830517B86DA576C881B200F938172546753D89EE5C2B9C335DB28137F9AEB435E825E34AC416F1B38FF3FD99A6F3F6BE11B94F8DB32C5B72F
	99D20DF32CAE1EA7F63F9B46EE40A6F451B5F64DBD08E19326BB0F8CCE24BB091368CE040D5A69FFEC5C381152BB17BF99BD8EC8EF2EADC08FF18BA17F945062EFA2B3A4CF52587E5DC17621A5D0EF39685F530DE37B776B93017E1DEC1AC81ECDBAD90C7AA75969FE9B10C7BDBCD61E5B485E168275B26DDC1E274A464A93D79910276258C4721899A40610278EF83858F8D3DDBCDAB555B731541FD8C176C63B2EFF947FAB026F5161291F51D7455F072BD0365C4EE396B1644DFE69B9B843818CG04814C86D8
	F40E479A0E1796C91344696C9EEF839C74D4DD7800766F3E6A1BFB737BFDB35F8797FC7B2B52420F2F0D8BF517367C9C3B43E1E95ACFDEA6F85A3F9162FFE73503BD8268819885188EB0371D776F7652BC5ABF3C8A34EA1A6C98A0370C3CF3F87197C02303CE7B561A4E7B3690E8AD8308EE8F58235876D0BEE29459DB13BB025F94BFC670199D41EF0A5B5A791B629CA81B5E417D201FDE505FB724FDFE07094FF9B05FF59D6CADD4076E3E116876B7D01D595AF8DCF9CDA2F7B0D96232234685DD460736B945C5
	25F9D9A5C7F2597C4C9E5173AEFB61E0483B0E22CE4A330625B1F0D2233C8BCE85F8777721ED5DC3CE9CD9756E81D72FCB5B436A351E8E540B2E3A9199E93BFFB4845ABD7A621E60F6274088646CF13AE455533FA7AF2BA8AF2B38B4176F75D9CD01366FDE3D911E2F113AB02FD2609F25D50D3F40E163765B942E573B3EF63FD473545A383DA1466D9A9B2FE51D01B9DBFD6E49A4AA534335681FD0FF081FB517B77273D9C2976C45G1DGEA973D67226D945B78B36AB5DB7099B59C5AC4F43179A2F6EF1AFDB2
	9FC58F73216D12AEEA77B03F923E57024D7B91G15A3121ECB8DDF9A71F1FF3592577C198639D7D1CF7BBC43DEBE9F36D45216C530E991ABDB593DC534E1D709E29EB3B725695B31E2DAB62D8C7768F1AF47C45B25A52902AEFF06A4F4EB07C1DF58ABA57EF6465D22777BE3581CA41F67F7133D2DC6773A3BC4F9CA0ED17BA2D1FE702CD1F98D14D39D5C5F26DB8B2E9BD98BA5E2AD545B306DF87DDEF712162B76002EAFED8F70F924F1EAFC1E698850096D8E50D9D0B6B5BA5FE8443654C6BFDD95787E3D6EDB
	2D7AA05FA79B79815B24BBEEA98F3AF72D2D8C9DE30D2DB40E317E3D52789EC032CE7C76B7599A7A6D6FD62B7E6DAF5DFB5FEBC21D6C849D54GA2GA2D1DF4F73FD77DEAB4EAB7E92E57C1EDB6B6C4798AF7E7AC4637C432AC2777593D5137FE6E5C6953E976A7540929FC27A9C7FCFD5B8DF1A47CB10ACD6DC05E364F3758FE834AAFF27C662942A52C750749CB5C04E4F2B20DF5285FE367ABE41ED07FC05403D12C14E1910F78A5CC3577999EC5A85FED67A77F674BFD70A7CF212DFAF721D6D9F04DEA17D50
	3A7B768742A35419086A47AC32AF52F1AD2E5E87E93BFC4970D55699D8774D3D179407E6177BC57B0F3710F8E13745DC564DF5AA3666E0A8BA00EC71F93C0ACF3BC2316485A2237563D65C5FBA010E869081B09FE08940AA00F5G89EF4299812088208E209B408B908EB093E09E4022B7399C531AF3A71003854008027BE4FAA7C8FD3D750D5C57DB8E34125F62F1346F3C00BE788D3D6B323BB4D529A21E810D6F7C35093F692AFC0B47F73EC0FEDFB68A7999G735E62F756F3ECBCA696A56AAEA76F3C56C3BE
	6DA21C29810C7602507B4383E47FE077A4FDA16EC9484ECAE578F28B3FA7293C48653A271577E1D4602AF3F83D798257FB1663D6DF64F371092650F6632E26496D46674878A9305E6DD73C6C1EE8D693393B6D6B73AB42BE5D5714A6F0757E960F5F0551AD3A073D170E3F473E8CD362F248AA8D42D5493E960F6AF2DCE131234F931B118F1055A35EF32523FE86E2C741ADF91CAB16343582740836A59963FD447A8C6E6B4D6E21BE62C2C81775507BD36A6BAD3BBE192F27BFF347C05B75BD81DFAFC536186FB3
	C9520EB83EFF571DA63E151D5EAA682E9F583DC2DC075E7F4C1429AC95103648FC8E6FEC41B231B751254B5F9D935F8AFFC3827E006BAA398431C1C5F7BFAD7FEDEB6879DF5DBA797CBF5BEA3CEF7C2DF57C3E312155F85FB03569778D0AB33761B87603AD689475D2842ECB603635EDE2388E17D39B90781DEDAFB0FC1D028CB9FEF79B4AB5C2F01D7CF7B8BADC9743E53721FEC017C13D449B8172DB31BE172A52FC912963F203CA0B71B8CCE4F4F9E51D5C658257A571BDE4C153E4E77F815968DEFD71246D02
	5F1350F64F11F661F26818209EA2E47E5B5C2D443EC54A413114CC02DFA88747D26EEB633114A5D016534B77652F0CFBEBB599D0BEB5642D67745EE07E0D849F519B4C7FC99B67BF974A129D1C7FD7DB747CD5877BFB89AC1D6998BFAE6C2DC46BC4F24F03F60B9CC673D41723EF371C5509E1697A107C7A4F0E395BF63273ADCBCB1D216E0E3F190E6F97F45BC6016EB623203D364DDEDFEDED38E82BEEEBEEE8283236DD34B6F4D83BEC227D660CC393342FEBEE2BBF5F5CE4B722C0477D6D8C3EB757010CE1CE
	718EBB036FCD8B1DFC4FB4DDA73F7D07FC32E2B1E7A9D35DB38F1C13A4EEE7AF3D93E24FA496153DC76A606F374A94AE4334CE14418B79E9G3381683B11288CBCA33B5C2A4B2DD453DFEBCB7C6EA03D9969AC043A4B956E236EA536AB86726B952EEBBB481ECE3FDF55FDFF256D561F2B22BE849EE76C01BE59F45F2BEC371E77DFEEA98FF6BEE991774BFDFC1E9B3B787DF22640BD58454FA79601FB281563B4017BE6B6096BC3FE2540DD4C61BC56885C0C2B1C07544FF14F0A7B779401FB1778F15D10D7852E
	EFA76242A0BFC3609E5FA96E307A39EE0EE45C5C3EF347246D0C771D6F12F6EB7AEFEC5F1186783EF31958086D8341F67FBD024F9A88367B4957395D4F033299833C3FA7BAA612BBF6A0D49CFA83318F492EE07E09DDC4AED7B07F3BBAB97FACA88BF7F1BFB337997D938D0F9D98171138CFEAA536E4B5540B95F5291FC06ACABCDE53C8E2C6344D9F09DEDAAE41B96B920FDDF9C9DC087A26BF6C1ACA9CB2787E25679235019B45FD573256496E3B747E2EFAE96CFDD75825607B2E2559B8A70A9BAD33137569B7
	595CB6CF077A94B70D2C0DD9101FA77A79194EC0BF135F190A8FBE6ECE7892751B83F2FD766C58FB2F2F117595837556DD62F3F3FD6758FB2F073B82727CEE677B39234E9A243A04FA425E32E470FDA6EF102F65CFDD637BCC65A0576DEBF8354D57276EBB757A2D374359371487FBDB53416DD703683329AB504F4D3A6F191D373E4FDF3F25BC28DEFDCE975B5D59B635FB19675F8A3435F6CD2D6D6BBD58B6344FD5A387E87FB51B2C855F30C24BE6BB82E561D72746778778D405FDBF549F20F3FE6754682C44
	239F33A94F137563851D39B6486D47F3F8354C5E8E2A62CC9D8965D1G51G128A3E284A6BF72881D9EE3FF62332D0FF73539DBCCED4297237D3D19D41EF276E0E533FFD7AF3C7705BA9EC9FA837F4863F1D9A5B3E3DB3786D94EFCFF577F3936E03C6C07FC65E2946FB01D964DAA9D11352596B47AC2B2AE651770FFE1F34D58CCABDB989C7DFBED49E7F7B7B63CDD2C23D14C4A38375D2829E6F6B25C4F27A2F1762B50FF7947FFA0EC98F1A34596A98F4FBC6D48CA8E95216EA7CDBC8CA968CFDC1C101A6A512
	EBA36019BE7E3251FA57AF1E0A3CD8335DA419006D00C279A40EE0D400F0FA99E0GE80E39C6753FAD3DABA571D2203CE54C947F703E43FFD8766BF6FF3C14D4AF3D04BB04CD7111C00A624447666CAFAD1D12B6113F0007EF1D202BFE12A47A35AB2A52A0ED6FEB43DFF67BB545E965A19A3FA66DA0BF57AB4430E4E11D6C9F50F0E40FB7C889EE7C1BD0D2BAADD6E5F7FFA10D579CA73D08331205CAE6C882F9B3083CC825C0D04978372D7BDEBF9333B0DEB21D12DED0BD3273146C50BC3EFAE913B660D37C83
	9E55B96ABDD9C988A50F203212C7BAC06E9551FD6CEC019D76BBA17460683D53372C5EF28A26EA54A795121A38A196089A4E6A8159872C94DF3D06FF634A633308320F3D2A6D7F71691D5DA6A90EF59207BEE5542BC9C9FD7210CB3D829FF4C111C9C953C0DA1F6C7663EF8CC1322CA049F811E0D07019C70BBE7B405F4F938601261A749A9DCAE8DA582FF8068A2DFDFDF0389384E0DC017CEEC19E6B948455C99F3DFB4719A7B676029E54C871C3C5C5647FE272FF897EDFCCB04584D3DC82033BCE1A70DFF079
	A86FB3D19E312AF07A8BCE418CEACF2FFDF44EAFAB562D015E1D1292496F97E1925282351A0607FA955F47A3BF3BFB734C68BBF10E3C28AA23C8F77BE8101EE38E6D4396E6CBFEC6269F31AF909D232A11C2F48C31296315855169B0E5B5E44AB03703D45DC97EFFF94A83002DF611362FA2FB125FB7AAA4CDE5B6A2CEC84A7DB7936BC742C7CF7E7C007CFC3471AFB81ABEFA12160E9F69D3E3C69A7F212620541B8456244346E9309561753E4BC1224CA0B1F9FDD02EC9496C45GFCEE26AFG321A5D65D93AE1
	68A18CD27F3F062107652D239FBD19CE87268637752DEC38DF99B389FC913C7451133FF93C7CA8ED52C7CF5276310B00CB8AEEC9B2F19A70427FA3A774DB787B9E7AFEAA77BD7D3B3375F72404FCF7367CB20BC7DF8E7E0D7D9FC87CAAEB44E22E986177CBC7DA715EAD547D33EDC8D6D5CDD6872DEEA75EC5D7DD2177D7346DB32DC85F4F6B30C2DA76E7F29FF4C957667FGD0CB87880E5925C0D19DGG74D9GGD0CB818294G94G88G88G0AF5B7B00E5925C0D19DGG74D9GG8CGGGGGGGG
	GGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0B9DGGGG
**end of data**/
}
/**
 * Return the ConfigurationPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getConfigurationPanel() {
	if (ivjConfigurationPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Notification");
			ivjConfigurationPanel = new javax.swing.JPanel();
			ivjConfigurationPanel.setName("ConfigurationPanel");
			ivjConfigurationPanel.setBorder(ivjLocalBorder);
			ivjConfigurationPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJCheckBoxNotifyWhenAck = new java.awt.GridBagConstraints();
			constraintsJCheckBoxNotifyWhenAck.gridx = 2; constraintsJCheckBoxNotifyWhenAck.gridy = 3;
			constraintsJCheckBoxNotifyWhenAck.ipadx = 2;
			constraintsJCheckBoxNotifyWhenAck.ipady = -2;
			constraintsJCheckBoxNotifyWhenAck.insets = new java.awt.Insets(3, 2, 14, 1);
			getConfigurationPanel().add(getJCheckBoxNotifyWhenAck(), constraintsJCheckBoxNotifyWhenAck);

			java.awt.GridBagConstraints constraintsJLabelGroup = new java.awt.GridBagConstraints();
			constraintsJLabelGroup.gridx = 1; constraintsJLabelGroup.gridy = 1;
			constraintsJLabelGroup.ipadx = 14;
			constraintsJLabelGroup.ipady = -1;
			constraintsJLabelGroup.insets = new java.awt.Insets(27, 15, 5, 10);
			getConfigurationPanel().add(getJLabelGroup(), constraintsJLabelGroup);

			java.awt.GridBagConstraints constraintsJComboBoxGroup = new java.awt.GridBagConstraints();
			constraintsJComboBoxGroup.gridx = 2; constraintsJComboBoxGroup.gridy = 1;
			constraintsJComboBoxGroup.gridwidth = 2;
			constraintsJComboBoxGroup.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxGroup.weightx = 1.0;
			constraintsJComboBoxGroup.ipadx = -21;
			constraintsJComboBoxGroup.insets = new java.awt.Insets(25, 2, 2, 13);
			getConfigurationPanel().add(getJComboBoxGroup(), constraintsJComboBoxGroup);

			java.awt.GridBagConstraints constraintsJLabelContact = new java.awt.GridBagConstraints();
			constraintsJLabelContact.gridx = 1; constraintsJLabelContact.gridy = 2;
			constraintsJLabelContact.ipadx = 13;
			constraintsJLabelContact.insets = new java.awt.Insets(6, 15, 5, 2);
			getConfigurationPanel().add(getJLabelContact(), constraintsJLabelContact);

			java.awt.GridBagConstraints constraintsJComboBoxContact = new java.awt.GridBagConstraints();
			constraintsJComboBoxContact.gridx = 2; constraintsJComboBoxContact.gridy = 2;
			constraintsJComboBoxContact.gridwidth = 2;
			constraintsJComboBoxContact.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxContact.weightx = 1.0;
			constraintsJComboBoxContact.ipadx = -21;
			constraintsJComboBoxContact.insets = new java.awt.Insets(4, 2, 3, 13);
			getConfigurationPanel().add(getJComboBoxContact(), constraintsJComboBoxContact);

			java.awt.GridBagConstraints constraintsJButtonNewContact = new java.awt.GridBagConstraints();
			constraintsJButtonNewContact.gridx = 4; constraintsJButtonNewContact.gridy = 2;
			constraintsJButtonNewContact.insets = new java.awt.Insets(2, 13, 1, 42);
			getConfigurationPanel().add(getJButtonNewContact(), constraintsJButtonNewContact);

			java.awt.GridBagConstraints constraintsJLabelNotifyOn = new java.awt.GridBagConstraints();
			constraintsJLabelNotifyOn.gridx = 1; constraintsJLabelNotifyOn.gridy = 3;
			constraintsJLabelNotifyOn.ipady = 4;
			constraintsJLabelNotifyOn.insets = new java.awt.Insets(2, 15, 14, 2);
			getConfigurationPanel().add(getJLabelNotifyOn(), constraintsJLabelNotifyOn);

			java.awt.GridBagConstraints constraintsJCheckBoxNotifyOnClear = new java.awt.GridBagConstraints();
			constraintsJCheckBoxNotifyOnClear.gridx = 3; constraintsJCheckBoxNotifyOnClear.gridy = 3;
			constraintsJCheckBoxNotifyOnClear.gridwidth = 2;
			constraintsJCheckBoxNotifyOnClear.ipadx = 52;
			constraintsJCheckBoxNotifyOnClear.ipady = -2;
			constraintsJCheckBoxNotifyOnClear.insets = new java.awt.Insets(3, 2, 14, 70);
			getConfigurationPanel().add(getJCheckBoxNotifyOnClear(), constraintsJCheckBoxNotifyOnClear);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjConfigurationPanel;
}
/**
 * Return the NewEmailButton property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonNewContact() {
	if (ivjJButtonNewContact == null) {
		try {
			ivjJButtonNewContact = new javax.swing.JButton();
			ivjJButtonNewContact.setName("JButtonNewContact");
			ivjJButtonNewContact.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJButtonNewContact.setText("Create new...");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonNewContact;
}
/**
 * Return the AlarmInhibitCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisableAllAlarms() {
	if (ivjJCheckBoxDisableAllAlarms == null) {
		try {
			ivjJCheckBoxDisableAllAlarms = new javax.swing.JCheckBox();
			ivjJCheckBoxDisableAllAlarms.setName("JCheckBoxDisableAllAlarms");
			ivjJCheckBoxDisableAllAlarms.setText("Disable All Alarms");
			ivjJCheckBoxDisableAllAlarms.setMaximumSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setActionCommand("Alarm Inhibit");
			ivjJCheckBoxDisableAllAlarms.setBorderPainted(false);
			ivjJCheckBoxDisableAllAlarms.setPreferredSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJCheckBoxDisableAllAlarms.setMargin(new java.awt.Insets(2, 2, 2, 2));
			ivjJCheckBoxDisableAllAlarms.setMinimumSize(new java.awt.Dimension(104, 26));
			ivjJCheckBoxDisableAllAlarms.setHorizontalAlignment(2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisableAllAlarms;
}
/**
 * Return the JCheckBoxNotifyOnClear property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxNotifyOnClear() {
	if (ivjJCheckBoxNotifyOnClear == null) {
		try {
			ivjJCheckBoxNotifyOnClear = new javax.swing.JCheckBox();
			ivjJCheckBoxNotifyOnClear.setName("JCheckBoxNotifyOnClear");
			ivjJCheckBoxNotifyOnClear.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJCheckBoxNotifyOnClear.setText("Clear");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNotifyOnClear;
}
/**
 * Return the JCheckBoxNotifyWhenAck property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxNotifyWhenAck() {
	if (ivjJCheckBoxNotifyWhenAck == null) {
		try {
			ivjJCheckBoxNotifyWhenAck = new javax.swing.JCheckBox();
			ivjJCheckBoxNotifyWhenAck.setName("JCheckBoxNotifyWhenAck");
			ivjJCheckBoxNotifyWhenAck.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJCheckBoxNotifyWhenAck.setText("Acknowledge");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxNotifyWhenAck;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxContact() {
	if (ivjJComboBoxContact == null) {
		try {
			ivjJComboBoxContact = new javax.swing.JComboBox();
			ivjJComboBoxContact.setName("JComboBoxContact");
			// user code begin {1}

			refillContactComboBox();
			
			ivjJComboBoxContact.setToolTipText("Will use the first e-mail for this contact");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxContact;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxGroup() {
	if (ivjJComboBoxGroup == null) {
		try {
			ivjJComboBoxGroup = new javax.swing.JComboBox();
			ivjJComboBoxGroup.setName("JComboBoxGroup");
			ivjJComboBoxGroup.setEnabled(true);
			// user code begin {1}

			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			synchronized( cache )
			{
				java.util.List notifGroups = cache.getAllContactNotificationGroups();

				for( int i = 0; i < notifGroups.size(); i++ )
					ivjJComboBoxGroup.addItem( notifGroups.get(i) );
			}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxGroup;
}
/**
 * Return the JLabelEmail property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelContact() {
	if (ivjJLabelContact == null) {
		try {
			ivjJLabelContact = new javax.swing.JLabel();
			ivjJLabelContact.setName("JLabelContact");
			ivjJLabelContact.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelContact.setText("Contact:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelContact;
}
/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelGroup() {
	if (ivjJLabelGroup == null) {
		try {
			ivjJLabelGroup = new javax.swing.JLabel();
			ivjJLabelGroup.setName("JLabelGroup");
			ivjJLabelGroup.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelGroup.setText("Group:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelGroup;
}
/**
 * Return the JLabelNotifyOn property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelNotifyOn() {
	if (ivjJLabelNotifyOn == null) {
		try {
			ivjJLabelNotifyOn = new javax.swing.JLabel();
			ivjJLabelNotifyOn.setName("JLabelNotifyOn");
			ivjJLabelNotifyOn.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelNotifyOn.setText("Notify On: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelNotifyOn;
}
/**
 * Return the JScrollPaneAlarmStates property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneAlarmStates() {
	if (ivjJScrollPaneAlarmStates == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("Alarming");
			ivjJScrollPaneAlarmStates = new javax.swing.JScrollPane();
			ivjJScrollPaneAlarmStates.setName("JScrollPaneAlarmStates");
			ivjJScrollPaneAlarmStates.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAlarmStates.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneAlarmStates.setBorder(ivjLocalBorder1);
			getJScrollPaneAlarmStates().setViewportView(getJTableAlarmStates());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneAlarmStates;
}
/**
 * Return the JTableAlarmStates property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableAlarmStates() {
	if (ivjJTableAlarmStates == null) {
		try {
			ivjJTableAlarmStates = new javax.swing.JTable();
			ivjJTableAlarmStates.setName("JTableAlarmStates");
			getJScrollPaneAlarmStates().setColumnHeaderView(ivjJTableAlarmStates.getTableHeader());
			getJScrollPaneAlarmStates().getViewport().setBackingStoreEnabled(true);
			ivjJTableAlarmStates.setBounds(0, 0, 200, 200);
			// user code begin {1}
			
			ivjJTableAlarmStates.setAutoCreateColumnsFromModel(true);
			ivjJTableAlarmStates.setModel( getTableModel() );
			ivjJTableAlarmStates.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableAlarmStates.setGridColor( java.awt.Color.black );
			//ivjJTableAlarmStates.setDefaultRenderer( Object.class, new ReceiverCellRenderer() );
			ivjJTableAlarmStates.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			ivjJTableAlarmStates.setRowHeight(20);
			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableAlarmStates;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/00 4:58:59 PM)
 * @return com.cannontech.dbeditor.editor.point.PointAlarmOptionsEditorTableModel
 */
private PointAlarmOptionsEditorTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new PointAlarmOptionsEditorTableModel();
		
	return tableModel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) 
{
	//Consider commonObject an instance of com.cannontech.database.data.point.PointBase
	com.cannontech.database.data.point.PointBase point = 
			(com.cannontech.database.data.point.PointBase) val;

	Character alarmInhibit;
	if( getJCheckBoxDisableAllAlarms().isSelected() )
		alarmInhibit = new Character('Y');
	else
		alarmInhibit = new Character('N');

	point.getPoint().setAlarmInhibit( alarmInhibit );

	// Set all the values for the PointAlarming structure
	String alarmStates = new String();
	String excludeNotifyState = new String();

	int i = 0;
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List liteAlarmStates = cache.getAllAlarmCategories();
		
		for( i = 0; i < getJTableAlarmStates().getRowCount(); i++ )
		{
			int alarmStateID = com.cannontech.database.db.point.PointAlarming.NONE_NOTIFICATIONID;
			
			for( int j = 0; j < liteAlarmStates.size(); j++ )
			{
				if( ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarmStates.get(j)).getCategoryName() == getTableModel().getGenerateAt(i) )
				{
					alarmStateID = ((com.cannontech.database.data.lite.LiteAlarmCategory)liteAlarmStates.get(j)).getAlarmStateID();
					break;
				}
			}
				
			char generate = (char)alarmStateID;
			boolean notify = getTableModel().getDisableAt(i);

			alarmStates += generate;
			excludeNotifyState += (notify == true ? "Y" : "N");
		}
	}
	
	// fill in the rest of the alarmStates and excludeNotifyState so we have 32 chars
	alarmStates += com.cannontech.database.db.point.PointAlarming.DEFAULT_ALARM_STATES.substring(i);
	excludeNotifyState += com.cannontech.database.db.point.PointAlarming.DEFAULT_EXCLUDE_NOTIFY.substring(i);
	
	point.getPointAlarming().setAlarmStates(alarmStates);
	point.getPointAlarming().setExcludeNotifyStates(excludeNotifyState);
	
	//notify on acknowledge	
	if( getJCheckBoxNotifyWhenAck().isSelected() && ! getJCheckBoxNotifyOnClear().isSelected())
		point.getPointAlarming().setNotifyOnAcknowledge("A");
	//notify on clear
	else if ( ! getJCheckBoxNotifyWhenAck().isSelected() && getJCheckBoxNotifyOnClear().isSelected())
		point.getPointAlarming().setNotifyOnAcknowledge("C");
	//notify on both
	else if ( getJCheckBoxNotifyWhenAck().isSelected() && getJCheckBoxNotifyOnClear().isSelected())
		point.getPointAlarming().setNotifyOnAcknowledge("B");
	else
	point.getPointAlarming().setNotifyOnAcknowledge("N");

	// get the selected contact from its combo box
	LiteContact contact = (LiteContact)getJComboBoxContact().getSelectedItem();
	
	point.getPointAlarming().setRecipientID( new Integer( findEmailContact(contact) ) );
	

	// get the selected notificationGroup from its combo box and insert its id
	LiteNotificationGroup grp = 
		(LiteNotificationGroup)getJComboBoxGroup().getSelectedItem();

	point.getPointAlarming().setNotificationGroupID( new Integer(grp.getNotificationGroupID()) );
	
	return point;
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

	getJComboBoxGroup().addActionListener(this);
	
	// user code end
	getJCheckBoxDisableAllAlarms().addActionListener(ivjEventHandler);
	getJCheckBoxNotifyWhenAck().addActionListener(ivjEventHandler);
	getJComboBoxContact().addActionListener(ivjEventHandler);
	getJButtonNewContact().addActionListener(ivjEventHandler);
	getJCheckBoxNotifyOnClear().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointAlarmOptionsEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(384, 363);

		java.awt.GridBagConstraints constraintsConfigurationPanel = new java.awt.GridBagConstraints();
		constraintsConfigurationPanel.gridx = 1; constraintsConfigurationPanel.gridy = 1;
		constraintsConfigurationPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsConfigurationPanel.weightx = 1.0;
		constraintsConfigurationPanel.weighty = 1.0;
		constraintsConfigurationPanel.ipadx = -10;
		constraintsConfigurationPanel.ipady = -29;
		constraintsConfigurationPanel.insets = new java.awt.Insets(9, 9, 0, 10);
		add(getConfigurationPanel(), constraintsConfigurationPanel);

		java.awt.GridBagConstraints constraintsJScrollPaneAlarmStates = new java.awt.GridBagConstraints();
		constraintsJScrollPaneAlarmStates.gridx = 1; constraintsJScrollPaneAlarmStates.gridy = 2;
		constraintsJScrollPaneAlarmStates.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneAlarmStates.weightx = 1.0;
		constraintsJScrollPaneAlarmStates.weighty = 1.0;
		constraintsJScrollPaneAlarmStates.ipadx = 336;
		constraintsJScrollPaneAlarmStates.ipady = 138;
		constraintsJScrollPaneAlarmStates.insets = new java.awt.Insets(1, 9, 2, 10);
		add(getJScrollPaneAlarmStates(), constraintsJScrollPaneAlarmStates);

		java.awt.GridBagConstraints constraintsJCheckBoxDisableAllAlarms = new java.awt.GridBagConstraints();
		constraintsJCheckBoxDisableAllAlarms.gridx = 1; constraintsJCheckBoxDisableAllAlarms.gridy = 3;
		constraintsJCheckBoxDisableAllAlarms.ipadx = 62;
		constraintsJCheckBoxDisableAllAlarms.insets = new java.awt.Insets(3, 9, 13, 209);
		add(getJCheckBoxDisableAllAlarms(), constraintsJCheckBoxDisableAllAlarms);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initJTableCellComponents();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (11/15/00 10:52:29 AM)
 */
private void initJTableCellComponents() 
{
	// Do any column specific initialization here
	javax.swing.table.TableColumn nameColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.CONDITION_COLUMN);
	javax.swing.table.TableColumn generateColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.CATEGORY_COLUMN);
	javax.swing.table.TableColumn notifyColumn = getJTableAlarmStates().getColumnModel().getColumn(PointAlarmOptionsEditorTableModel.EXNOTIFY_COLUMN);
	nameColumn.setPreferredWidth(120);
	generateColumn.setPreferredWidth(120);
	notifyColumn.setPreferredWidth(50);
	
	//Create new TableHeaderRenderers		
// DOES NOT WORK IN IBM's JRE1.3 DECAUSE THE getHeaderRenderer() IS NULL
/*	nameColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );
	generateColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );
	notifyColumn.setHeaderRenderer( new javax.swing.table.DefaultTableCellRenderer.UIResource() );

	//Assign the tableHeaderRenderers som toolTips
	((javax.swing.JComponent)nameColumn.getHeaderRenderer()).setToolTipText("Alarm Name");
	((javax.swing.JComponent)generateColumn.getHeaderRenderer()).setToolTipText("What group the alarm belongs to");
	((javax.swing.JComponent)notifyColumn.getHeaderRenderer()).setToolTipText("Click to enable/disable notification");
*/

	// Create and add the column renderers	
	com.cannontech.common.gui.util.CheckBoxTableRenderer bxRender = new com.cannontech.common.gui.util.CheckBoxTableRenderer();
	bxRender.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
	com.cannontech.common.gui.util.ComboBoxTableRenderer comboBxRender = new com.cannontech.common.gui.util.ComboBoxTableRenderer();

	// Get the alarm data from the cache	
	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List allAlarmStates = cache.getAllAlarmCategories();
	
		for( int i = 0; i < allAlarmStates.size(); i++ )
			comboBxRender.addItem( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(i)).getCategoryName() );

		generateColumn.setCellRenderer(comboBxRender);
		notifyColumn.setCellRenderer(bxRender);


		// Create and add the column CellEditors
		javax.swing.JCheckBox chkBox = new javax.swing.JCheckBox();			
		chkBox.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
		chkBox.setBackground(getJTableAlarmStates().getBackground());
		javax.swing.JComboBox combo = new javax.swing.JComboBox();
		combo.setBackground(getJTableAlarmStates().getBackground());
		combo.addActionListener( new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent e) 
			{
				fireInputUpdate();
			}
		});

		for( int i = 0; i < allAlarmStates.size(); i++ )
			combo.addItem( ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(i)).getCategoryName() );

		generateColumn.setCellEditor( new javax.swing.DefaultCellEditor(combo) );
		notifyColumn.setCellEditor( new javax.swing.DefaultCellEditor(chkBox) );
	}
		
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	return true;
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
		PointAlarmOptionsEditorPanel aPointAlarmOptionsEditorPanel;
		aPointAlarmOptionsEditorPanel = new PointAlarmOptionsEditorPanel();
		frame.add("Center", aPointAlarmOptionsEditorPanel);
		frame.setSize(aPointAlarmOptionsEditorPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void newContactButton_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	QuickContactPanel contactPanel = new QuickContactPanel();	
	int userResponse =
		javax.swing.JOptionPane.showInternalOptionDialog(
			CtiUtilities.getDesktopPane(this),
			contactPanel,
			"Create New Contact",
			javax.swing.JOptionPane.OK_CANCEL_OPTION,
			javax.swing.JOptionPane.PLAIN_MESSAGE,
			null,
			null,
			null);

	if( userResponse == javax.swing.JOptionPane.OK_OPTION )
	{
		Contact contactDB = (Contact)contactPanel.getValue(null);

		fireInputDataPanelEvent( 
			new PropertyPanelEvent(
						this,
						PropertyPanelEvent.EVENT_DB_INSERT,
						contactDB) );

		refillContactComboBox();

		//select the newly created contact in out JComboBox, seems reasonable
		for (int j = 0; j < getJComboBoxContact().getItemCount(); j++)
		{
			if( contactDB.getContact().getContactID().intValue()
				 == ((LiteContact)getJComboBoxContact().getItemAt(j)).getContactID() )
			{
				getJComboBoxContact().setSelectedIndex(j);
				break;
			}
		}
	}

}
/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 2:54:35 PM)
 */
private void refillContactComboBox()
{
	getJComboBoxContact().removeAllItems();
	getJComboBoxContact().addItem( NONE_LITE_CONTACT );
	
	
	DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized( cache )
	{
		List contacts = cache.getAllContacts();
		for( int i = 0; i < contacts.size(); i++ )
		{
			LiteContact contact = (LiteContact)contacts.get(i);
			
			//be sure we have an Email notif for this contact
			if( findEmailContact(contact) != CtiUtilities.NONE_ID )
				getJComboBoxContact().addItem( contact );
		}
	}


	
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) 
{
	
	//Consider defaultObject an instance of com.cannontech.database.data.point.PointBase
	com.cannontech.database.data.point.PointBase point = (com.cannontech.database.data.point.PointBase) val;
   int ptType = com.cannontech.database.data.point.PointTypes.getType( point.getPoint().getPointType() );
	
	Character alarmInhibit = point.getPoint().getAlarmInhibit();

	if( alarmInhibit != null )
		CtiUtilities.setCheckBoxState( getJCheckBoxDisableAllAlarms(), alarmInhibit );
		
   //be sure we have a 32 character string
	String alarmStates =
      ( point.getPointAlarming().getAlarmStates().length() != point.getPointAlarming().ALARM_STATE_COUNT
        ? point.getPointAlarming().DEFAULT_ALARM_STATES
        : point.getPointAlarming().getAlarmStates() );
        
	String excludeNotifyStates = point.getPointAlarming().getExcludeNotifyStates();

	com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
	synchronized( cache )	
	{
		java.util.List allAlarmStates = cache.getAllAlarmCategories();
		java.util.List allStateGroups = cache.getAllStateGroups();
		String generate = new String();

		if( allAlarmStates.size() <= 0 )
			throw new ArrayIndexOutOfBoundsException("No AlarmStates exist, unable to create alarms, occured in " + this.getClass() );
	   
      
		if( ptType == com.cannontech.database.data.point.PointTypes.STATUS_POINT )
		{
			String[] stateNames = null;

			// get all the states the status point may have
			for( int i = 0; i < allStateGroups.size(); i++ )
			{			
            com.cannontech.database.data.lite.LiteStateGroup stateGroup = 
                  (com.cannontech.database.data.lite.LiteStateGroup)allStateGroups.get(i);

				if( point.getPoint().getStateGroupID().intValue() == stateGroup.getStateGroupID() )
				{
					stateNames = new String[stateGroup.getStatesList().size()];

					for( int j = 0; j < stateGroup.getStatesList().size(); j++ )
						stateNames[j] = stateGroup.getStatesList().get(j).toString();
						
					break; // we have all the states, get out
				}
			}
		
			// insert all the predefined states into the JTable
			int i = 0;
			for( i = 0; i < IAlarmDefs.STATUS_ALARM_STATES.length; i++ )
			{
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( IAlarmDefs.STATUS_ALARM_STATES[i], generate, notify );
			}
			
			for( int j = 0; j < stateNames.length; j++, i++ )
			{
				if( i >= alarmStates.length() )
					throw new ArrayIndexOutOfBoundsException("Trying to get alarmStates["+i+"] while alarmStates.length()==" + alarmStates.length() + ", to many states for Status point " + point.getPoint().getPointName() + " defined.");
						
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( stateNames[j], generate, notify );
			}		
			
		}
		else
		{  
			// All other point types are processed here
			// insert all the predefined states into the JTable
			for( int i = 0; i < IAlarmDefs.OTHER_ALARM_STATES.length; i++ )
			{
				if( ((int)(alarmStates.charAt(i))-1) < allAlarmStates.size() )
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get( (int)(alarmStates.charAt(i))-1 )).getCategoryName();
				else
					generate = ((com.cannontech.database.data.lite.LiteAlarmCategory)allAlarmStates.get(0)).getCategoryName();
						
				boolean notify = ( Character.toUpperCase(excludeNotifyStates.charAt(i)) == 'Y' ? true : false );
				
				getTableModel().addRowValue( IAlarmDefs.OTHER_ALARM_STATES[i], generate, notify );
			}		
		}


		// assign the correct contact to the JComboBox component
		java.util.List contacts = cache.getAllContacts();
		for( int i = 0; i < contacts.size(); i++ )
		{
			LiteContact ltCnt = (LiteContact)contacts.get(i);
			for( int j = 0; j < ltCnt.getLiteContactNotifications().size(); j++ )
			{
				LiteContactNotification ltCntNotif= 
						(LiteContactNotification)ltCnt.getLiteContactNotifications().get(j);

				if( ltCntNotif.getContactNotifID() 
					  == point.getPointAlarming().getRecipientID().intValue() )
				{
					getJComboBoxContact().setSelectedItem( ltCnt );
					break;
				}
			}
		}
	
		// assign the correct notificationGroup to the getJComboBoxGroup() component
		java.util.List notifGroups = cache.getAllContactNotificationGroups();
		for( int i = 0; i < notifGroups.size(); i++ )
		{
			com.cannontech.database.data.lite.LiteNotificationGroup grp = (com.cannontech.database.data.lite.LiteNotificationGroup)notifGroups.get(i);

			if( grp.getNotificationGroupID() == point.getPointAlarming().getNotificationGroupID().intValue() )
			{
				getJComboBoxGroup().setSelectedItem( grp );
				break;
			}
		}
		
	}

	if( alarmInhibit != null )
	{
		switch(point.getPointAlarming().getNotifyOnAcknowledge().charAt(0))
		{
			case 'A': 
				getJCheckBoxNotifyWhenAck().setSelected(true);
				getJCheckBoxNotifyOnClear().setSelected(false);
				break;
			case 'C':
				getJCheckBoxNotifyWhenAck().setSelected(false);
				getJCheckBoxNotifyOnClear().setSelected(true);
				break;
			case 'B':
				getJCheckBoxNotifyWhenAck().setSelected(true);
				getJCheckBoxNotifyOnClear().setSelected(true);
				break;
			case 'N':
			default:
				getJCheckBoxNotifyWhenAck().setSelected(false);
				getJCheckBoxNotifyOnClear().setSelected(false);
		}
	}
	getTableModel().fireTableDataChanged();
	
	return;
}
}

package com.cannontech.dbeditor.wizard.device.lmgroup;

import java.util.Collections;
import java.util.List;

import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.device.DeviceTypesFuncs;
import com.cannontech.database.data.device.lm.LMGroupMCT;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.multi.MultiDBPersistent;
import com.cannontech.database.data.multi.SmartMultiDBPersistent;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.DeviceTypes;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.lm.IlmDefines;

/**
 * This type was created in VisualAge.
 */
public class LMGroupMCTEditorPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.ItemListener, javax.swing.event.CaretListener 
{
	private javax.swing.JCheckBox ivjRelay1CheckBox = null;
	private javax.swing.JCheckBox ivjRelay2CheckBox = null;
	private javax.swing.JCheckBox ivjRelay3CheckBox = null;
	private javax.swing.JPanel ivjRelayUsagePanel = null;
	private javax.swing.JCheckBox ivjRelay4CheckBox = null;
	private javax.swing.JComboBox ivjJComboBoxLevel = null;
	private javax.swing.JLabel ivjJLabelAddress = null;
	private javax.swing.JLabel ivjJLabelAddressLevel = null;
	private javax.swing.JLabel ivjJLabelMCTAddress = null;
	private javax.swing.JPanel ivjJPanelAddress = null;
	private javax.swing.JTextField ivjJTextFieldAddress = null;
	private javax.swing.JComboBox ivjJComboBoxMCTAddress = null;


	//just fill in some reasonable random values
	private static final LiteYukonPAObject NONE_PAO =
		new LiteYukonPAObject( 
				CtiUtilities.NONE_ZERO_ID, CtiUtilities.STRING_NONE, 
				PAOGroups.CAT_DEVICE, DeviceTypes.MCT370,
				DeviceClasses.CARRIER, CtiUtilities.STRING_NONE );

	/**
	 * Constructor
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	public LMGroupMCTEditorPanel() {
		super();
		initialize();
	}
	
	
	/**
	 * Method to handle events for the ActionListener interface.
	 */
	public void actionPerformed(java.awt.event.ActionEvent e) {
		// user code begin {1}
		// user code end
		if (e.getSource() == getJComboBoxMCTAddress()) 
			connEtoC3(e);
		if (e.getSource() == getJComboBoxLevel()) 
			connEtoC2(e);
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
		if (e.getSource() == getJTextFieldAddress()) 
			connEtoC9(e);
		// user code begin {2}
		// user code end
	}
	
	/**
	 * connEtoC1:  (Relay4CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC1(java.awt.event.ItemEvent arg1) {
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
	 * connEtoC2:  (JComboBoxLevel.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupMCTEditorPanel.fireInputUpdate()V)
	 * @param arg1 java.awt.event.ActionEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC2(java.awt.event.ActionEvent arg1) {
		try {
			// user code begin {1}
			// user code end
			this.jComboBoxLevel_ActionPerformed(arg1);
			// user code begin {2}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {3}
			// user code end
			handleException(ivjExc);
		}
	}
	
	/**
	 * connEtoC3:  (JComboBoxMCTAddress.action.actionPerformed(java.awt.event.ActionEvent) --> LMGroupMCTEditorPanel.fireInputUpdate()V)
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
	 * connEtoC5:  (Relay1CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC5(java.awt.event.ItemEvent arg1) {
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
	 * connEtoC6:  (Relay2CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC6(java.awt.event.ItemEvent arg1) {
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
	 * connEtoC7:  (Relay3CheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
	 * @param arg1 java.awt.event.ItemEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC7(java.awt.event.ItemEvent arg1) {
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
	 * connEtoC9:  (JTextFieldSerialAddress.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMGroupVersacomEditorPanel.fireInputUpdate()V)
	 * @param arg1 javax.swing.event.CaretEvent
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void connEtoC9(javax.swing.event.CaretEvent arg1) {
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
	 * Return the AddressUsagePanelTitleBorder property value.
	 * @return com.cannontech.common.gui.util.TitleBorder
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.TitleBorder getAddressUsagePanelTitleBorder() {
		com.cannontech.common.gui.util.TitleBorder ivjAddressUsagePanelTitleBorder = null;
		try {
			/* Create part */
			ivjAddressUsagePanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjAddressUsagePanelTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjAddressUsagePanelTitleBorder.setTitle("Address Usage");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		};
		return ivjAddressUsagePanelTitleBorder;
	}
	
	
	/**
	 * 
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private static void getBuilderData() {
	/*V1.1
	**start of data**
		D0CB838494G88G88GB0E634AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DDC8DD4DC5535C62B5675C40D2BD8E3DF5A051A363426ED7ADE1E0D153EC7DFE9DF5458C60BB5EA0CC4B1BF9615EA0C8844DC987E7F828192C8C89888A4E0107F44F472A38412C0CA14A698B1E6A9976682A3434CF4669206C81A3777B9771CFBE7388301BEFD4DDABB771E6F1C3377BE7B6C73374F9D047C1240247804FCC108CF9214FF6F468B427CF302B09A3971A60665BC9EE390B47FCA01F20594
		D3A2BC0D1037626DD8C30EF0E7EDAA241BA09D34BB56308D5E7304754621776145071CE6487B74216575D34B095C4C642410F4C8F2A6BC33018E82F681658165884B138B955C8F52594ABBD2FC02A0B87E00E5CA7696EA70C825DC9B6475G65BF01E5DECBAED672978306E51641B3628F34EC9CBC5308F6571B8EAA65AE5C96A42CFF68F0C2964A5270E7F46189DA57CFF99A05D0E28803303A35831E073B06870F765BC7EA7AC51B4DEE13257E219AF31FE43648F6E74D18651268B45718250B16FE2946BAB268
		340FBACC264E53A77165F4EB77F1D22ECB34C9D66095C55AF0FCEFAA618FED007444F244FE31976D56846F1DC066652C9D0FE69DD6748C988E92426F2975590E1065D47F0865EABB8E5E7FC2D24013483FB8A5D329D78E698BC0D497F38A53E57913CC17CF0DD8B6672998C3A5D073D38C2FA8C53C87528E209B1FE2BA9E594BF46C7962E7C24FEFBB12B67B50B170A92A5B1227D49D975C7BC2D291719DF7AE76FF6253A09768B0D0AF109FD0A0508E62330DB9EF433325CFEE350F0C58EDAD8E47B12B59F95AEE
		162C70EEB2C10E021D117A6DCE33A0D03FBC3537DAC79FC60B1F7674C9A45A360F8466C78B4E7CD8E83FFCAAA9C927ED0B9EBF1A30D461917E345A364A95E83716CA341BG6FD88FEDC3713F925C2A41E9DF7D0A7882C9E339E7181DFB3219AE1D09F78BB975E2C20A0EAEF37F7BDC027FB3D407794F283AD09B1F49C39BAF83BC92A8F1E50C219868B05005154C4617F39A2733F1870C8327CD341E9D6DF308CE79582845EA161C0A2D4BD22632359E51FAF5B32EC76D9254286D23957F5E2BEB1795C147935CAB
		29BD662CD46DC2E5DBFC4ADE38F232EFE473BEFC0C0C155015348F8D9CDFC170B08DCE7B56CCF0CCC7C0DE7B33606B247C8F4858DA702C370F5CCF7030E73DFDE4DFA97311B848F3074798E87FC665637C389E52B92B00B7D0B3D0AF5084501CD52C0F176454DE65B86A5274AF2DFBCD6ED6F8CAEE59A9F658DC32E86B174E09CE0B58E715DC02D266DFF2512EFD9E75EEA5633B9FE41C14EC12D314ADF69BF097C7A4F0A697ADB3B7B7974BB8A5125BED9911C04008C3E0FC83F373F04C08AE79C307D914A53584
		4D8F4AC51F38A81E830E40G4763AA3417C8665C05703EF49533554E2D0815BF877E7F9C334B6EADDB607997F0EE49EAB235D97AD18D51B94EFC74E4A55A378B4AAF85EAFFBE46908A549C81CF20769758D8FD390779E4DB560F046A47BF5375C92475AFD07F4ADC6D6D139F91D9CD00F78349C081C00B004200562FE6EB6E03FB9D8A2FF605976AF7B56387F547A753FF2A326D4F2B7958F6AAFBAD173DD9F99AE6B0F671198114B5CD59944DBB4E3F347FEE5A06E3A0FFCD0C218E28EB8D6B43CF379829EF9F93
		DD9273668F1C600812D96DD1DA7613BC54DB3AA85964B649A1594C7062D2FC29AFAFCD757381BB7741A102633C578A0E686238ADAF176240C3BBEAD87EC512AF4A325352B7AACBE7A5D9365886557AFFA73A38BC26D41752561B8B312DC350C6BF20B93C2DA979F99AFF6D345B07C79D5DB892953F77A36B7BFCA89F3C067559F2A36E5942F97AB912D673BFB776F8599F4B4EE4DE1EFE2E7F5AA499D76BB7D7AF63A725450A1FB19F2BA0B85D3B6E67657E0B14CBDD8BFE84548364879484348CA8FAAD33CBFD29
		C3C71616FF3642FFAA5DCA15E745B46576FB3425CBD736C1998FECFCE4CC43136918B6CDB9B616290F45647B9EA371857AE3A425C0FF0CE495CCBD868A8A7446G4DABAE40365A47EC1AB1D5DA0073F53F5DE613484066D5843A07FF399B5BE131D9640FAC66C1C9BEEB39A441DC368E47061B2C89C6F8EFDA47666D315DEC5EAE3F7483A164B3D3025EFEEF6EF7BFCE70DFC747C460BAF55E26BA15ED4531AAD92591B0D42BFD54A6D33C122CB78A6EB2904C1806B6B3D61E112C62F8FD6B1054BFFC4C6E2E3648
		52C88D7EF7D606C52EF5C8348DCA664A4F450BE20DB8A657103E2869007C6378D6E5FC671D23C65DC3C7750045A9F5589C234AFAD9D9F52E2AC920322FE95448EE78EAE46FD7E4DFDB26115D785548BE20481EF85F214AEE7AEAE4579F2432C30DEEA27BD4B70C19939649EABEAB4156495AE2B6BBA5172B3A9FD68F39067C2FD4C6855CB52EB198A60A8E2D18BB9BA5949DF2BFD2F4003DDF1F9D1A8EB519FC110C229A7A68121C83F66748E4AB34106CD95961G556141126FF86A50891C2D7F3762BF77607DD9
		0B9753CE5FDC8F01268D8758BEA3248847F3880C73B02048F5EC6D1D3F9573595A2BECB0DA6DD6AB1D0AA04F7CA24EB337D466EB660E4497E9DC248B1EB4FFF315DE7E821E3F35D6AFBF04675F211B9F46738B6B747257727CBBF57363F87E76FA3DFCF5FD38EDDB8E4E59525FC6A5B88A606663AF52B85B4FEF5B6215F7CE340E2A670440ED38E6D9B57B960AFF23D0FF8E3D339077AA431C7F378B13855C6FD31EF44EDFA8A2CF0753B2825BBC5C08A97125BBD23471251CBF42BA0F7DGCFB3902DFB47975B61
		2978939EA244FE7B08C790A7A792F9EDA8E5F12889289BG34C8619775C7EA9BDA6E7EB2D66E822465C8DF65C6F633F28110DE6033DCE9B9AB37945291C009AF69150B60651AA13F87482DDB6E7B952C5CA248DF8A9429DBEEF88FABD77DF20C216DE5351C79E5DAEE614BD86ED69E5B2A65587788769AF4540EA4F6E6BF1D230D4F39656EE43DF596E93D42BF75956FD8466B5B11EFA56087DFE1E3F2DB11F6BF5C62F2597BAD64A8297855EF0A34EBB0459EAECA07A7CCD6AE3983B6B7EE661B0F96212EF856D4
		E0453F6E5DF7C418FCF626CE420E9B2DE3E4A269363742F1379D5C3B15EFD14ED924B1014582FFDD496C55798A5B03DCBE210D7985EFBE19106143B657A9B6192F31CD5B3DAFA4D15ECBEB986F08D7D83C6FF4AD430A239876DB0EF5F36C03BA0679F3EC9947C22358F839530431A23D98E9BFB1CE4D602825E6D43660E1DA14C5329A5001C4F9462726AAF60D0BE2F1469D99A4967A2798C3A710990846BCCA4AB215329300AD7A931B1F8AE46D7C9489F87E2B6093C0F42D39218CFDF1064B3B525721D5DB28EF
		F8ADE44C0F6F4EEBE6637147F78B072FD4A66845BEEF5C5C105079AA6DA379D5CDFCF66919646527D7ABB15A67F87BC220CC742B2CFDA5FDFA6D2BFB0D59651B3BD93DB640A6DEE37592CCFA759671FA274AD93DA500896BD9BD31D2571E6B19BDFFDABE9BFBD219D995CCE6A7703B0E4BF41C574A8C86BC82A875F5A6D32EC019B35C9DA9B217F11945402F7BF5A67366E12DCC7F57294CC5DC663BFB9A60B973AD890B51856C6176851E4286A6F72D2CA7776086B626BE5C02734BBF34FB91F87C3AC1BB4F0604
		1F50BD4FA0F5EE20FE593B41FB1EFD329847688460F3B630F936F3FB2EE61EBD83B39B74BF2C5E7C4C389DED886B3412FEEBBB4AA56B34404E348FD54EA45EBBFF830B5B156EC27BAD06F498D0A4D0629BB10672B718EE576F18B6FEFD5CBDC57C7A511A19685576860B8F1C291DC93D9EDE6F77B32AE765753A6BE6D24F0F57FBFCC67582F83D64D46FFAC1EF28F12160B7A64708E88C7BD6B21FAFFB03463013B87EAD02C7E8F0BAEECAB3583809063C8B51CCFEC519377C10E8CF19AB3C5234DE6324FAB5DEE9
		61CD4FFAF49C9EBD077EC456B64DB4E3B21DE2C7C6E5DF39F1C83B2FACFC13465A8E43336BCD36F6F64AEC1D9BFA137971D8896E298231BCD09816854A0F01399D28BB0679F3588E3D7D0627BF7B5AEC28B1C8A5DEBBCB9BEE185606F20CE7BD5A6EF3FD2C5D7EB12C9F676D1E1D8EBF1CD44FDB07D0DD9D7EECE2BA2C60BAF4154FCE07CFA7557356A1BFD6CF0739E41F07BC2AE3198EF995335321F4D2BDEF9D4C31FA7EDCC16E93F47C1947A228BFBF1621674F9340F3CEAC73676F1F4776609A93445B7307D9
		36A7E25A76046B36E7506AEBFCAAAB27E27B50E1E67BE82E6B37764CCE5720C975F6F9256B62747A3FE4146D1D5B6358787F1991B1BFC84F09E3E33D3E98E334ECDD6D32DBEC324056541545F85635BBAD03969B431EA7D83F5DC90222B4663F0A78DAA0708C66FC2BF6790E61566D520F619E58259F43BD3CEB6AFB0E36DD3EE23CD43F35B5CC3FE5C0943354B2AC02E3AFF2AC1AE343F58C4B5C4830C80E99B936131CAB1AA05D4E3135E908D9A1FD5DC6668737E7A0968469D00E85928C77E4386F25585E8C6F
		7B8A0FF9E7635477A3C15364D3994DD34AC81AA2AFDB187E7EE4A7A97360564D246FDCAE49C5968DBC15A9F9EB6482C1A7167E3EB8A2A97C73048BE5E9CA1963323DF5BF9F0B2FD5956A0FC5F25823E3E03F050D43700D6C2CF75E05EBFD61DB306681353F456C7DD7D9EF0F527B16F206FAEB32BD153B69925FFEFEE3093E1F5FD202E34EE59FF576CB5E3EFCFBC9A14FD3D7D5BC5620D9E8196F107A32689C14A60D056F127AB44FD77D9F17789AAF5466A53B35B6BF407D6513BEBD9B5D2858E61E0E0DA83F5F
		FB70FB1773BBEA526337D061936A135F574B357C6AB93FEF6B6E6F17ABFCA2152730C965CB791D2A50727B3340665074CC66BF791B187FACA45F1BF5C25A0C58A666BF8B4E236C00CD1EBA87A969E0F8AE734A23323376E8E5B77236BCB80C7CA2B6690F63E840B305D89DFE0FFA70EB607C4AC7275A039B3DF8297E9DEC443D321B27C3C8FA1C0FC329A51B7B5BF11BD9D9C0E89CAE1A2C8B23205849F806EDA2F3711D007DAB477C483C7B8D408C898C0BA7F36CB0E0239C3B16E0B78176B501E106FA446E826C
		FF388D1AD2F10D320A6376D150A5D0406F917F124A7698CD106E841201EED45ACD5B7253B10CFB8DB84591988EE79DD23FB0910FF5B725313AEB21FC9CD0B1600701AEGD119EBEAF1CC5259C7395B607A941BF00EA037BF9F3A44C1094623E83F18AC5AFE8985FE4B63553E080C1F5CAF74AC73DBB20F89893164AC1346712789DE2C41699927176018BE88F97300689AB5C44E8902815AE9E058A3B2DB370AB9666CE3652C9C7BFD9FAB67665807A6D6EE89476EB331F2E19CDBC4FC23BCB1565014483022AAD6
		2E19E391A5084D017482A0EA334D19DA1BAD86FCE922EA2770C4DFB62BA45FA42ECF24364962783B84AFCCD2F1EA337B48D84774E1488BCAE2BA7D0A6CC57313E38D55498CFB7B93448EC23A1DE337FE425A534D31FB9B48779110DE4C3170E146EFA947DA2C2C6EF20E5DB5C4624FA960FBA98CBBB44A4AF581C675F62532EFAF8381DB8EC47D3B3E57E3BF0E419C13692453E2BEA68EF61251C2673D8748DE866B672446AA735EBA42339D523DC0EEA09A6FF16FE13163855F3BDB68746B557DDEB43C7BE4421C
		D45AAF0B14A746FBC2F86C38A21D490C027C4AB49CF7E75335F1AB9CCBEB9268DE1D66DF6F110FEBDA15204D7F19C7BE6A7BA30F7A6BBC7213B475E95B9E2EE0ED0BFBF821E07D0F77FDFE8B6BC823ED8AD01EB7DE34472FB72FCE22FD74D7328FD98AF961E92C5F72A790CBCC0FB59426B36C164B2CAF8F27B3DF8FF361DD17BF240300C231AC4FDBC57270AEC8D66EF070FE06668D38F0EF076535FA7A27CF9E9BE41D7201737E734A27B22E9099A9DEE5FD6D8B275BCFFAFFA3C2674EABB25E3F4930090A839D
		84E1EBA576CCEF8676DF045CB685BF0397B5C39D245DCA9FF6E750710EC4F97F39CA657DE25FF43C1326609DB409775F5ED6F9BFE21A0EF74A943C97E9F8D39BBDDC69E92300FD2A2C3A2A69E425CDA1ABE4122CD832772441E98C8F09B6B3EF636FF0DAD64EDCFBB2194FAF4DE07E1DD547FCBE0AE3176A194F67E7B23F2EA17726E6C87B81858205G2DG0A66E50E10B205D931642E71A0BC710E30871EF827C84B84A77742936BE5AB7C2672BFADC5CFD3D62B9715758411453E071A4937E553FD0B483E37E2
		7D14A7BCF59E6548F63B355B6240D80AE27FE61C3A14B2AD1FF87AC4FF2D6A935F7BE4AA1FC87272EDB75854BF0B7A84620170FECD3C56A72E3423FE128D2F534DC2F0964A88EBE07107851026BA5CD0276A50E31DCE876D989032BDF54807744D9EFE39F8E8328E3F9BE2EB0A914AD339E7C627132B1DB31A3C6436EB6452FE9F1F94231E0EE83D2F4F32DE480C6B51BD4E83E938FEF7E72B63196221846F5560F48F718A5FC3D8A1EF99900DA1D96AF15CB5ED0EB5F4818D8179818582858351B34271D26C68A9
		63475DF68739E65204F3E87B5ECE1F5DB7793FCCE1EB75221F7FD0D8FCC3364FBB6CE51B69580ED21E46D1FBBCBEE97B629A30FD469C58E381758239017C01666730765DDCDACD5B07D770AD32AC768F115BFD56B83CF0D7E1BCC8D09FBD12096712BE3B139C5869396412051DAD96837FE5C019393186E3AE550D56AB2F1BFC1EB9B2416A9D4C256719AEF81A01B006C4FD7D3B17E73206CDC05DB9395457B1BD9F5EBF86835E3AE953A6657B3DABD84F734BC12663FCB34FEB704E3BEBB34FEB744E7B70BD1E57
		246651BE7D5DA75A3B441CEF9DC8E0BE6A5D16050A3D02F3553DE58961AF8864BC227C2373D093061194992BB2195F945FFB0F5076C4F642EE9FB222945E4DDBD499450F2DCE22BC979DD079BF712E7AFE854389CA1FFD144F7A4C3D0576759CF84ED77851B2076AD81960AD34DF436039D6A9C37B75367A196CFB04BC7009BC35DFAB617DA56853D53CDF230E1033EB2758B7796C7A11817755EA445EBB7FF449966F3AE4E033FC9399DB1EDF5D313C858DB93C2E7AC92062CFE21E361E1A2DFC6366CA516E5D1B
		20CD98BF6D01A74D0FEF544B37727C18DE3DFCBFF8CE761B2877FC7B525DA6015B620F8DE44DF50ECA54378247347EFBB03D5367DDF8C09E6D5FA065397E7BF69D7F0DFA0F4A5CF6D81539FA88F98E08D69746CCFD57B161151E32067CB2CDFDFFC8737EFA6AD47160FF14280C6C34AFD3861D1F1D753EE33263757AB159AB757AF78F5757CF7D7D784D753E3F9F8F2857FEBFCE5717742368F761504FF1C0467CD8C393D0FBBEDBDB4A9A4A843236682CAB474D03989346DFC049CCFED2236F36E6B6CE7F3B2902
		C67C962D4FAE4B7859612044709D0D58DF325DC1C00A999B51C6CE4B6010CC0BAA781EC63207178688CC778FF7DB586F4E4C5036F979AC86710BF45C9BDC5577DB5D9659AA9DA32BA50D913CE8E1772AE179ACAE617A8265E496449A4A018E97309808B44176AF5D007983D19EBF1AE0FB4250824663E9D2B68E5279DBE38D55DB2FF68EE0E3EB55FB826F5FDB1A85EE177B9A71B79767444FF53F58BBA5DE94BF687BDC6A173B45C1DE7F7E961C5B5BC007DE206B006681AD84DA823482A88AA8F1DB2C21982889
		289D68821083488FA8GA8E89B7327717DD53EF4GD0897E9027F20A746E0F5AEFFF816B3FE06035FE9B335F6582568755054CFE56DAACEB46B4D0C0A16B037EBA56878BG0BA8E4BC6E2DE3FDD0DC44F8BCC94AF6C35A8AF4DD51CC7BA030D590785D50000F3BA13252D09DFE18426E136795B13D8EB6B2AC0CE375A243B23733BD7069B6445AA05D3B1D59F90DE95F94F66EF25A9D12D39E27A879A2D97111ADC7FD0F5F2223530F5F3223647BD298F75D638E65FEE76FD1F2EFBBB0601278BCD1FDB407E39D2E
		AE3B4B02A7F8650E59B25D1E5BEF3BDE7C673EE96BF95FD553FA2F10FA7336DF5DF9FCA11423E715B5E42C07EE77BE43C491FC59F66FB34C78974C7FD6C0DE738EF6D69F1E185D59EBFC16757EED4675E83BBE380C7A4F5F615D5E9E02AF5C615D5ED5FC4E8B06BCE3B1734DC7DAFC7BD8D84B74BE76CC0B7E9A71FC4B64B5E2ED0B7E9A71F20BF60D2058EBADB8DE87E18E6A616522B976B147B6921E08FDE4B14BC39CB79CCBD770F68985B2BC6D986AB5C6309E854B56E09FABD85EB19CA75064A13B1355ADBC
		06FB38810B552A7C860C143158E058F0FB149EC39DAD8ED123779E0EFD4C7B7DD0416C46D547347574475535245EF545D7B72E669633F195CE666F85455EFE360E600B0B3D7D4CD94B7CAC94F96CE4634AB2236F7CD43D47E6D9EF610C6B51F61D2EC77D7DF7FA37779C41676E74EE6F33FC9D1B8FF9293BD8FBFF62719BFE6BAEEAF7BF6599G4F20DDFA7DFB24D1DBAFD8A933CCF9C640B3DA375E81D1DBAF33C4B907AA4FA6F83617F856233EF9268D5F799E16AEBFCA5E47ED79827D3D22C534DAAE111FAE1C
		050236C17A0DC6C8DE964937DAECD2AB7DA53D40E23C491F63183EGB26592964F953EC06CC6C84FABE1F6FA698B0137E103667D4A97DF7EF9E36ED7A063434BEA1BBE553C2F197872E58B173FCC9934CF6FACE07D9C447BF9FD8D6B67E05E4F2BEB55363F22F9F757FE79F6386DAB10F136DEED53854D7B8BF5DF3E6C373ED49934CFFFC37644CB20BF1773FE3EE7A7E211100E63D86AAEF6E713EA645F913433FB6D9E0EF5B7B2FED60E4513EF627CA0BD07E3837CBB39F99C3B19FC039089E92114E127C546AF
		1FE306F8F2A78E69039C9B0AE77C5AB8B6CC6E8282A09DD44A3E59D8C8FEB3AE63B173041D1CFD683DC1AD195702215C92DEB623B6091595D86C67BF0F8B3C4E92B2C62221FCE2993B6F08BC0179347EB3A9E4DF81E2E87957082D1B20ECF3991371731D1EF7B61B5B31BE0D79156C1AC9AC57DFF3E7036B16230C6E85B7EE5A34094A7FE6BB3B1F1900BC0ADDA67D658F6939DC2742B4CF1D1E4BD4F53A1CB613B82437CECBC0C62E203D4319AFCF3E43F9C0E6FA06F1BDAF92BF0800F49457F3FB3C271EC9592A
		1ED762E7F25F6A2D27F1B7BD7729FADE2BF3577459B0BB53D66FE67DFFCB3E5A7F6BAFE1DC47A60D99796FE85067AA4B4C306E5A874B2A0DF555467A26AA66DBAFEED56B2E2D19D95D73C5EA5DBBF6DECD5D86DE77696DEA5D7367D9BC1366856FD473DECF1D995FE7F729F59F13BD791EA8D5735E90E64657D226563DB82C0D05523FB997F7E08FF456021704A4D9C8A1578C46FA7E56405F9A79DB1376ADDF89BA9B6D83C6F9C8B212DF694AC2FC1DACD41C1570F75784BE5D5AED24FF124DB8EE9FB50E09B640
		6D46D11764F28A1BF9C3BA48A9A27CE22349707CBD0684A131C3C826272A8EA1910FC69DC292B9B9F58886B8D539712F86603F8A050C2E3F89FF67318F068A9C0D57C5CBB5E882C751E2890A158C9CAD52C593B4E892C7B1E6152ED0AAC7D3B5E822ACE4D0D3124F5E40300AB9E5A1D5F9B349C2B633B1399BGA0934D49724100D99E1E706B3B762FDE3A721C4BA0A4F788C5B82152BFE8E039A419715BE2658FB11D90529C78ADA27E7D8D302E0BBC12DC7238D56A9472874E608F44DD32E4EEE13BE2D02A007C
		462D96230F356D22EBC84681FE28D3C834611F0C923650EC2BE89B2C25DB64C3240B62DBFAC907109987744DA37AA2179A4EA56F014D952527A2969B040493C23A55AE1ACF087D325D59A12449C35050A13B556CF63C57CC98250E2195B3C89FB8059D3C7958589A3C0961CA8F3B3F6D1F35B66B84740E5B896B961644B9300617B0378E09CE90A5B9BBE47C93D8F6E7B94F3BE6173C6711135BAEA48871CAA331970B5C60614983620845BA8EAFEDE45C117E5DA90336CE5166421F8402E6C6AF4DD89697D0730D
		876BEE7FC14CF9A2C02DAA8BA55414D035F6D0320F54368C8C407902B3G3B827B1D1CBD162971AA3345FDDB412947B775019F348906113ABA72FFBD793F817F2FA7C8BDC16A9B4038D142147F3C735D2C4D44F978C0466E2FB981BDA8BF755203F7BFFDB8AA92DAF7C2C8A2BFB504CE48D1CB3DBFBA52A7B9EFD8F07B4E0C00701B300F9C68AAEE64DBCA59EF1B64BF086E186C888817706E40D499A9DACE7E5FBB6346E12DAAD2F79F5729CE2B9355A0737FCB27E6A5FD404D27968C446034022BD072BF1F26C7
		A6EB0A13E66ABF1F26ED2A26BBB51AA633BB712AAB5AFB4B686B5F71F9EFE95C27449E7629F762B4C67E9679665781387FBE9607FC5F740E607BBE646C08E8354A22F53845E6463B11E36332245E33998EA1FF97AB23E4523C74C32877671ABA7F8BD0CB8788A27728A6D89CGG4CD6GGD0CB818294G94G88G88GB0E634AEA27728A6D89CGG4CD6GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG129CGGGG
	**end of data**/
	}
	
	/**
	 * Return the JComboBoxLevel property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JComboBox getJComboBoxLevel() {
		if (ivjJComboBoxLevel == null) {
			try {
				ivjJComboBoxLevel = new javax.swing.JComboBox();
				ivjJComboBoxLevel.setName("JComboBoxLevel");
				// user code begin {1}
				
				ivjJComboBoxLevel.addItem( IlmDefines.LEVEL_BRONZE );
				ivjJComboBoxLevel.addItem( IlmDefines.LEVEL_LEAD );
				ivjJComboBoxLevel.addItem( IlmDefines.LEVEL_MCT );
				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJComboBoxLevel;
	}
	
	
	/**
	 * Return the JComboBoxLevel1 property value.
	 * @return javax.swing.JComboBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JComboBox getJComboBoxMCTAddress() {
		if (ivjJComboBoxMCTAddress == null) {
			try {
				ivjJComboBoxMCTAddress = new javax.swing.JComboBox();
				ivjJComboBoxMCTAddress.setName("JComboBoxMCTAddress");
				ivjJComboBoxMCTAddress.setToolTipText("Select the MCT device you want to use");
				ivjJComboBoxMCTAddress.setEnabled(false);
				// user code begin {1}

				//put some dummy that means zero id				
				ivjJComboBoxMCTAddress.addItem( NONE_PAO );


				DefaultDatabaseCache cache = DefaultDatabaseCache.getInstance();
				synchronized( cache )
				{
					List mcts = cache.getAllDevices();
					Collections.sort( mcts, LiteComparators.liteStringComparator );
					
					for( int i = 0; i < mcts.size(); i++ )
					{
						LiteYukonPAObject dev = (LiteYukonPAObject)mcts.get(i);
						
						if( DeviceTypesFuncs.isMCT(dev.getType()) )
							ivjJComboBoxMCTAddress.addItem( dev );
					}
				}
				
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJComboBoxMCTAddress;
	}
	
	/**
	 * Return the JLabelSerialAddress property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelAddress() {
		if (ivjJLabelAddress == null) {
			try {
				ivjJLabelAddress = new javax.swing.JLabel();
				ivjJLabelAddress.setName("JLabelAddress");
				ivjJLabelAddress.setFont(new java.awt.Font("dialog", 0, 12));
				ivjJLabelAddress.setText("Address:");
				ivjJLabelAddress.setEnabled(true);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelAddress;
	}
	
	/**
	 * Return the JLabelAddressLevel property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelAddressLevel() {
		if (ivjJLabelAddressLevel == null) {
			try {
				ivjJLabelAddressLevel = new javax.swing.JLabel();
				ivjJLabelAddressLevel.setName("JLabelAddressLevel");
				ivjJLabelAddressLevel.setFont(new java.awt.Font("dialog", 0, 12));
				ivjJLabelAddressLevel.setText("Address Level:");
				ivjJLabelAddressLevel.setEnabled(true);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelAddressLevel;
	}
	
	
	/**
	 * Return the JLabelMCTAddress property value.
	 * @return javax.swing.JLabel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JLabel getJLabelMCTAddress() {
		if (ivjJLabelMCTAddress == null) {
			try {
				ivjJLabelMCTAddress = new javax.swing.JLabel();
				ivjJLabelMCTAddress.setName("JLabelMCTAddress");
				ivjJLabelMCTAddress.setFont(new java.awt.Font("dialog", 0, 12));
				ivjJLabelMCTAddress.setText("MCT Address:");
				ivjJLabelMCTAddress.setEnabled(false);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJLabelMCTAddress;
	}
	
	
	/**
	 * Return the JPanel1TitleBorder property value.
	 * @return com.cannontech.common.gui.util.TitleBorder
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.TitleBorder getJPanel1TitleBorder() {
		com.cannontech.common.gui.util.TitleBorder ivjJPanel1TitleBorder = null;
		try {
			/* Create part */
			ivjJPanel1TitleBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjJPanel1TitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjJPanel1TitleBorder.setTitle("Addressing");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		};
		return ivjJPanel1TitleBorder;
	}
	
	
	/**
	 * Return the JPanel1 property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getJPanelAddress() {
		if (ivjJPanelAddress == null) {
			try {
				com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
				ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
				ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
				ivjLocalBorder.setTitle("Address");
				ivjJPanelAddress = new javax.swing.JPanel();
				ivjJPanelAddress.setName("JPanelAddress");
				ivjJPanelAddress.setBorder(ivjLocalBorder);
				ivjJPanelAddress.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsJLabelAddress = new java.awt.GridBagConstraints();
				constraintsJLabelAddress.gridx = 1; constraintsJLabelAddress.gridy = 2;
				constraintsJLabelAddress.anchor = java.awt.GridBagConstraints.WEST;
				constraintsJLabelAddress.ipadx = 13;
				constraintsJLabelAddress.ipady = -2;
				constraintsJLabelAddress.insets = new java.awt.Insets(6, 10, 6, 29);
				getJPanelAddress().add(getJLabelAddress(), constraintsJLabelAddress);
	
				java.awt.GridBagConstraints constraintsJTextFieldAddress = new java.awt.GridBagConstraints();
				constraintsJTextFieldAddress.gridx = 2; constraintsJTextFieldAddress.gridy = 2;
				constraintsJTextFieldAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJTextFieldAddress.anchor = java.awt.GridBagConstraints.WEST;
				constraintsJTextFieldAddress.weightx = 1.0;
				constraintsJTextFieldAddress.ipadx = 192;
				constraintsJTextFieldAddress.insets = new java.awt.Insets(3, 2, 3, 23);
				getJPanelAddress().add(getJTextFieldAddress(), constraintsJTextFieldAddress);
	
				java.awt.GridBagConstraints constraintsJLabelAddressLevel = new java.awt.GridBagConstraints();
				constraintsJLabelAddressLevel.gridx = 1; constraintsJLabelAddressLevel.gridy = 1;
				constraintsJLabelAddressLevel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsJLabelAddressLevel.ipadx = 8;
				constraintsJLabelAddressLevel.insets = new java.awt.Insets(8, 10, 6, 2);
				getJPanelAddress().add(getJLabelAddressLevel(), constraintsJLabelAddressLevel);
	
				java.awt.GridBagConstraints constraintsJComboBoxLevel = new java.awt.GridBagConstraints();
				constraintsJComboBoxLevel.gridx = 2; constraintsJComboBoxLevel.gridy = 1;
				constraintsJComboBoxLevel.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJComboBoxLevel.anchor = java.awt.GridBagConstraints.WEST;
				constraintsJComboBoxLevel.weightx = 1.0;
				constraintsJComboBoxLevel.ipadx = 70;
				constraintsJComboBoxLevel.insets = new java.awt.Insets(5, 2, 2, 23);
				getJPanelAddress().add(getJComboBoxLevel(), constraintsJComboBoxLevel);
	
				java.awt.GridBagConstraints constraintsJLabelMCTAddress = new java.awt.GridBagConstraints();
				constraintsJLabelMCTAddress.gridx = 1; constraintsJLabelMCTAddress.gridy = 3;
				constraintsJLabelMCTAddress.anchor = java.awt.GridBagConstraints.WEST;
				constraintsJLabelMCTAddress.ipadx = 12;
				constraintsJLabelMCTAddress.insets = new java.awt.Insets(6, 10, 29, 2);
				getJPanelAddress().add(getJLabelMCTAddress(), constraintsJLabelMCTAddress);
	
				java.awt.GridBagConstraints constraintsJComboBoxMCTAddress = new java.awt.GridBagConstraints();
				constraintsJComboBoxMCTAddress.gridx = 2; constraintsJComboBoxMCTAddress.gridy = 3;
				constraintsJComboBoxMCTAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
				constraintsJComboBoxMCTAddress.anchor = java.awt.GridBagConstraints.WEST;
				constraintsJComboBoxMCTAddress.weightx = 1.0;
				constraintsJComboBoxMCTAddress.ipadx = 70;
				constraintsJComboBoxMCTAddress.insets = new java.awt.Insets(3, 2, 25, 23);
				getJPanelAddress().add(getJComboBoxMCTAddress(), constraintsJComboBoxMCTAddress);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJPanelAddress;
	}
	
	/**
	 * Return the JTextFieldSerialAddress property value.
	 * @return javax.swing.JTextField
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JTextField getJTextFieldAddress() {
		if (ivjJTextFieldAddress == null) {
			try {
				ivjJTextFieldAddress = new javax.swing.JTextField();
				ivjJTextFieldAddress.setName("JTextFieldAddress");
				ivjJTextFieldAddress.setToolTipText("Address of the Group");
				ivjJTextFieldAddress.setText("0");
				ivjJTextFieldAddress.setEnabled(true);
				// user code begin {1}
	
				ivjJTextFieldAddress.setDocument( 
						new LongRangeDocument(0, 9999999999l) );
	
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjJTextFieldAddress;
	}
	
	/**
	 * Return the Relay1CheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getRelay1CheckBox() {
		if (ivjRelay1CheckBox == null) {
			try {
				ivjRelay1CheckBox = new javax.swing.JCheckBox();
				ivjRelay1CheckBox.setName("Relay1CheckBox");
				ivjRelay1CheckBox.setText("Relay 1");
				// user code begin {1}
	
				ivjRelay1CheckBox.setSelected(true);
	
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRelay1CheckBox;
	}
	
	
	/**
	 * Return the Relay2CheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getRelay2CheckBox() {
		if (ivjRelay2CheckBox == null) {
			try {
				ivjRelay2CheckBox = new javax.swing.JCheckBox();
				ivjRelay2CheckBox.setName("Relay2CheckBox");
				ivjRelay2CheckBox.setText("Relay 2");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRelay2CheckBox;
	}
	
	
	/**
	 * Return the Relay3CheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getRelay3CheckBox() {
		if (ivjRelay3CheckBox == null) {
			try {
				ivjRelay3CheckBox = new javax.swing.JCheckBox();
				ivjRelay3CheckBox.setName("Relay3CheckBox");
				ivjRelay3CheckBox.setText("Relay 3");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRelay3CheckBox;
	}
	
	
	/**
	 * Return the Relay4CheckBox property value.
	 * @return javax.swing.JCheckBox
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JCheckBox getRelay4CheckBox() {
		if (ivjRelay4CheckBox == null) {
			try {
				ivjRelay4CheckBox = new javax.swing.JCheckBox();
				ivjRelay4CheckBox.setName("Relay4CheckBox");
				ivjRelay4CheckBox.setText("Relay 4");
				ivjRelay4CheckBox.setActionCommand("Relay 4");
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRelay4CheckBox;
	}
	
	
	/**
	 * Return the RelayUsagePanel property value.
	 * @return javax.swing.JPanel
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private javax.swing.JPanel getRelayUsagePanel() {
		if (ivjRelayUsagePanel == null) {
			try {
				com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
				ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
				ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
				ivjLocalBorder1.setTitle("Relay Usage");
				ivjRelayUsagePanel = new javax.swing.JPanel();
				ivjRelayUsagePanel.setName("RelayUsagePanel");
				ivjRelayUsagePanel.setBorder(ivjLocalBorder1);
				ivjRelayUsagePanel.setLayout(new java.awt.GridBagLayout());
	
				java.awt.GridBagConstraints constraintsRelay1CheckBox = new java.awt.GridBagConstraints();
				constraintsRelay1CheckBox.gridx = 1; constraintsRelay1CheckBox.gridy = 1;
				constraintsRelay1CheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsRelay1CheckBox.insets = new java.awt.Insets(32, 37, 0, 37);
				getRelayUsagePanel().add(getRelay1CheckBox(), constraintsRelay1CheckBox);
	
				java.awt.GridBagConstraints constraintsRelay2CheckBox = new java.awt.GridBagConstraints();
				constraintsRelay2CheckBox.gridx = 1; constraintsRelay2CheckBox.gridy = 2;
				constraintsRelay2CheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsRelay2CheckBox.insets = new java.awt.Insets(0, 37, 0, 37);
				getRelayUsagePanel().add(getRelay2CheckBox(), constraintsRelay2CheckBox);
	
				java.awt.GridBagConstraints constraintsRelay3CheckBox = new java.awt.GridBagConstraints();
				constraintsRelay3CheckBox.gridx = 1; constraintsRelay3CheckBox.gridy = 3;
				constraintsRelay3CheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsRelay3CheckBox.insets = new java.awt.Insets(0, 37, 1, 37);
				getRelayUsagePanel().add(getRelay3CheckBox(), constraintsRelay3CheckBox);
	
				java.awt.GridBagConstraints constraintsRelay4CheckBox = new java.awt.GridBagConstraints();
				constraintsRelay4CheckBox.gridx = 1; constraintsRelay4CheckBox.gridy = 4;
				constraintsRelay4CheckBox.anchor = java.awt.GridBagConstraints.WEST;
				constraintsRelay4CheckBox.insets = new java.awt.Insets(2, 37, 33, 37);
				getRelayUsagePanel().add(getRelay4CheckBox(), constraintsRelay4CheckBox);
				// user code begin {1}
				// user code end
			} catch (java.lang.Throwable ivjExc) {
				// user code begin {2}
				// user code end
				handleException(ivjExc);
			}
		}
		return ivjRelayUsagePanel;
	}
	
	/**
	 * Return the RelayUsagePanelTitleBorder property value.
	 * @return com.cannontech.common.gui.util.TitleBorder
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private com.cannontech.common.gui.util.TitleBorder getRelayUsagePanelTitleBorder() {
		com.cannontech.common.gui.util.TitleBorder ivjRelayUsagePanelTitleBorder = null;
		try {
			/* Create part */
			ivjRelayUsagePanelTitleBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjRelayUsagePanelTitleBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjRelayUsagePanelTitleBorder.setTitle("Relay Usage");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		};
		return ivjRelayUsagePanelTitleBorder;
	}
	
	
	/**
	 * getValue method comment.
	 */
	public Object getValue(Object o) 
	{
		LMGroupMCT group = null;
		
		if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
		{
			group = (LMGroupMCT)
						MultiDBPersistent.getFirstObjectOfType( 
									LMGroupMCT.class,
									(MultiDBPersistent)o );
		}
		else if( o instanceof SmartMultiDBPersistent )
			group = (LMGroupMCT)((SmartMultiDBPersistent)o).getOwnerDBPersistent();
		
		
		if( o instanceof LMGroupMCT || group != null )
		{
			if( group == null )
				group = (LMGroupMCT) o;
			
			group.getLmGroupMCT().setLevel(
					getJComboBoxLevel().getSelectedItem().toString().substring(0, 1) );

			if( getJTextFieldAddress().getText() != null && getJTextFieldAddress().getText().length() > 0 )
				group.getLmGroupMCT().setAddress(
					new Integer(getJTextFieldAddress().getText()) );
	
	
			StringBuffer relayUsage = new StringBuffer();
			if( getRelay1CheckBox().isSelected() )
				relayUsage.append('1');
			
			if( getRelay2CheckBox().isSelected() )
				relayUsage.append('2');
			
			if( getRelay3CheckBox().isSelected() )
				relayUsage.append('3');
	
			if( getRelay4CheckBox().isSelected() )
				relayUsage.append('4');
	
			group.getLmGroupMCT().setRelayUsage( relayUsage.toString() );
	
			
			group.getLmGroupMCT().setMctDeviceID( new Integer(
				((LiteYukonPAObject)getJComboBoxMCTAddress().getSelectedItem()).getYukonID()) );
		}
	
		return o;
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
		getRelay1CheckBox().addItemListener(this);
		getRelay2CheckBox().addItemListener(this);
		getRelay3CheckBox().addItemListener(this);
		getRelay4CheckBox().addItemListener(this);
		getJTextFieldAddress().addCaretListener(this);
		getJComboBoxMCTAddress().addActionListener(this);
		getJComboBoxLevel().addActionListener(this);
	}
	
	/**
	 * Initialize the class.
	 */
	/* WARNING: THIS METHOD WILL BE REGENERATED. */
	private void initialize() {
		try {
			// user code begin {1}
			// user code end
			setName("LMGroupVersacomEditorPanel");
			setLayout(new java.awt.GridBagLayout());
			setSize(342, 371);
	
			java.awt.GridBagConstraints constraintsJPanelAddress = new java.awt.GridBagConstraints();
			constraintsJPanelAddress.gridx = 1; constraintsJPanelAddress.gridy = 1;
			constraintsJPanelAddress.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelAddress.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJPanelAddress.weightx = 1.0;
			constraintsJPanelAddress.weighty = 1.0;
			constraintsJPanelAddress.ipadx = -10;
			constraintsJPanelAddress.ipady = -16;
			constraintsJPanelAddress.insets = new java.awt.Insets(4, 10, 8, 10);
			add(getJPanelAddress(), constraintsJPanelAddress);
	
			java.awt.GridBagConstraints constraintsRelayUsagePanel = new java.awt.GridBagConstraints();
			constraintsRelayUsagePanel.gridx = 1; constraintsRelayUsagePanel.gridy = 2;
			constraintsRelayUsagePanel.fill = java.awt.GridBagConstraints.BOTH;
			constraintsRelayUsagePanel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsRelayUsagePanel.weightx = 1.0;
			constraintsRelayUsagePanel.weighty = 1.0;
			constraintsRelayUsagePanel.ipadx = 172;
			constraintsRelayUsagePanel.ipady = -57;
			constraintsRelayUsagePanel.insets = new java.awt.Insets(8, 10, 103, 10);
			add(getRelayUsagePanel(), constraintsRelayUsagePanel);
			initConnections();
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		// user code begin {2}
	
		// user code end
	}
	
	/**
	 * This method must be implemented if a notion of data validity needs to be supported.
	 * @return boolean
	 */
	public boolean isInputValid() 
	{
		if( getJTextFieldAddress().isEnabled() )
			if( getJTextFieldAddress().getText() == null 
				 || getJTextFieldAddress().getText().length() <= 0 )
			{
				setErrorString("A value for the Address text field must be filled in");
				return false;
			}

		if( getJComboBoxMCTAddress().isEnabled() )
			if( getJComboBoxMCTAddress().getSelectedItem().equals(NONE_PAO) ) 
			{
				setErrorString("A real MCT must be selected in the MCT address combo box");
				return false;
			}
		
		return true;
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @param event java.awt.event.ItemEvent
	 */
	public void itemStateChanged(java.awt.event.ItemEvent e) {
		// user code begin {1}
		// user code end
		if (e.getSource() == getRelay1CheckBox()) 
			connEtoC5(e);
		if (e.getSource() == getRelay2CheckBox()) 
			connEtoC6(e);
		if (e.getSource() == getRelay3CheckBox()) 
			connEtoC7(e);
		if (e.getSource() == getRelay4CheckBox()) 
			connEtoC1(e);
		// user code begin {2}
		// user code end
	}
	
	/**
	 * Comment
	 */
	public void jComboBoxLevel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
	{
		boolean isMCTAddy = 
				getJComboBoxLevel().getSelectedItem().equals(IlmDefines.LEVEL_MCT);
		
		getJLabelAddress().setEnabled( !isMCTAddy );
		getJTextFieldAddress().setEnabled( !isMCTAddy );
	
		getJLabelMCTAddress().setEnabled( isMCTAddy );
		getJComboBoxMCTAddress().setEnabled( isMCTAddy );
		
		fireInputUpdate();
	}
	
	
	/**
	 * main entrypoint - starts the part when it is run as an application
	 * @param args java.lang.String[]
	 */
	public static void main(java.lang.String[] args) {
		try {
			javax.swing.JFrame frame = new javax.swing.JFrame();
			LMGroupMCTEditorPanel aLMGroupMCTEditorPanel;
			aLMGroupMCTEditorPanel = new LMGroupMCTEditorPanel();
			frame.setContentPane(aLMGroupMCTEditorPanel);
			frame.setSize(aLMGroupMCTEditorPanel.getSize());
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
	 * Insert the method's description here.
	 * Creation date: (6/8/2001 10:41:08 AM)
	 */
	public void setRelay(String r)
	{
	
		if (r.charAt(0) == '1')
		{
			if (!getRelay1CheckBox().isSelected())
				getRelay1CheckBox().setSelected(true);
		}
	
		if (r.charAt(1) == '2')
		{
			if (!getRelay2CheckBox().isSelected())
				getRelay2CheckBox().setSelected(true);
		}
	
		if (r.charAt(2) == '3')
		{
			if (!getRelay3CheckBox().isSelected())
				getRelay3CheckBox().setSelected(true);
		}
	
		if (r.charAt(3) == '4')
		{
			if (!getRelay4CheckBox().isSelected())
				getRelay4CheckBox().setSelected(true);
		}
	
	}
	
	
	/**
	 * setValue method comment.
	 */
	public void setValue(Object o) 
	{
		if( o instanceof LMGroupMCT )
		{
			LMGroupMCT group = (LMGroupMCT) o;
			
			for( int i = 0; i < getJComboBoxLevel().getItemCount(); i++ )
				if( group.getLmGroupMCT().getLevel().substring(0, 1).equals(
						getJComboBoxLevel().getItemAt(i).toString().substring(0, 1)) )
				{
					getJComboBoxLevel().setSelectedIndex( i );
					break;
				}

	
			getJTextFieldAddress().setText( 
					group.getLmGroupMCT().getAddress().toString() );
			
			String relayUsage = group.getLmGroupMCT().getRelayUsage();
			getRelay1CheckBox().setSelected( (relayUsage.indexOf('1') >= 0) );
			getRelay2CheckBox().setSelected( (relayUsage.indexOf('2') >= 0) );
			getRelay3CheckBox().setSelected( (relayUsage.indexOf('3') >= 0) );
			getRelay4CheckBox().setSelected( (relayUsage.indexOf('4') >= 0) );
	
			for( int i = 0; i < getJComboBoxMCTAddress().getItemCount(); i++ )
			{
				LiteYukonPAObject mct = 
							(LiteYukonPAObject)getJComboBoxMCTAddress().getItemAt(i);
				
				if( mct.getYukonID() == group.getLmGroupMCT().getMctDeviceID().intValue() )
				{
					getJComboBoxMCTAddress().setSelectedIndex( i );
					break;
				}
			}
			
	
		} 
	}


}
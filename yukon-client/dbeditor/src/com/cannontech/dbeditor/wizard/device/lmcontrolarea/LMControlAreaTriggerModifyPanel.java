package com.cannontech.dbeditor.wizard.device.lmcontrolarea;
/**
 * This type was created in VisualAge.
 */

import java.awt.Dimension;

import com.cannontech.common.gui.unchanging.LongRangeDocument;
import com.cannontech.common.gui.util.OkCancelDialog;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.db.device.lm.IlmDefines;
import com.cannontech.database.db.device.lm.LMControlAreaTrigger;

public class LMControlAreaTriggerModifyPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.beans.PropertyChangeListener, javax.swing.event.CaretListener {
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
	private javax.swing.JLabel ivjJLabelATKU = null;
	private javax.swing.JTextField ivjJTextFieldATKU = null;

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
	if (e.getSource() == getJTextFieldATKU()) 
		connEtoC7(e);
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
 * connEtoC7:  (JTextFieldATKU.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMControlAreaTriggerModifyPanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(javax.swing.event.CaretEvent arg1) {
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
	D0CB838494G88G88GD0E569AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DF8D445358828A82989B5C82408818224B57551A7D65A07FD546213DA3474FB70C4C5CBDB34BC25BEFCE24B6BC74BCD3279A5C00488D101ACFF92B40670A7B1D0A590A041G815690B0CAB6591BE449E6F75D3DA19BC848BBE7E64E6C5D4D5D044462A79FA7774E1919B3671CB9B3F3664C5CD532F2221362933294A5BEC9917F7645ABCAC10D220C5CBEE3A861E24F0647AB3AFF9B815ED01E5FE2
	0267A44CFBA0BCBED319F9A6955253A11D797D707835703EDA09CBAD7C8CDEC2341389F9FFFD7239FB3BEF67C18F35134252CF2F4F00678A009900DB84F050026D3F3ABEDB6033A13DC23CA344A7G6EB3AC736714EC9DFE20A837G1E8A64CFE6E5B2B62C9379B51046B2BEF806FD464B4640B30DF1F7F3D121A8D7BDA4D6D974F8E542F2ECCBC71F40D4496B660A27D9F91EA9A2D139306DBCBC8FD4B4B5FCDC67EA4E2F33BA1DAE272656B5665BEAD51BDDF3F972DB6DD72C9EDB3ECD3DEC2FD3739D4DF5D040
	63F2D8BD2A3528287C44E11EAC062445E3EFE8D0BDA7DCB6FBFDDB055529BA00F6D4A54A34AEB5153587B2C1FA4BB944C526229E67C0FA91C046B9126BB74B8F883EA31BE21539E34AC24A55F80E4B93F64EAFD7E967384483D5C8FF6F068CD1EFBA641BG47F96958C03C742BA2DEBA2FE259C9101E81B0DF620F36A3DEB99F9E1F8DD0F01EF87C47B662316A6AC34A2414E92615A1F8ACBD4FF9F31F7773B8792BF109EFB1DBAA56509E62A0EF8640BC00548B617139G25G6FB09BDE62F98F1E45355AE1D7F3
	334BD96CF69FF258D04B2A835E0B0AA0C7604E28F5AE0FCDD1381DEECC4DB360072066C2200DA2F0594EA9E4D7F1E76ED7E2FFBC4D14E4A05B44D9D5891A20B16802DF369997D0EF59E92837D8F847FA289B0E4FE5782C4F7DF85ED74319AD30B464CD7A1C74DC3502F8A9B70DD6B2F7DECDC8B160A56A91F7421EEA4EC3F9351F972E63F7AE230EFD000F840883188AB08F407485697828F7F7CFBAAE03F161F1DA9DE7DBEA5DD60FF628456E30299E216B7D9B3A53359170FAE53D2E4775923BDB5FC7F39E38E2
	2817B93197934CDFF0FD94FE61578BEF3BFF48362B3E68EA9BABE49F3E46460A638B5E0709927FB706EF5761F95F5E4C70188E033C05G3C7C63ECECD93E8C3611E7983E7D4BE09BA9EFA79B0901BC7345F0513FBFE94579F28424A7814CG188F10DA83E3982030067AF808F747F50E238ADD7F723A4735B570D4FD1A47DA6674EAD6E71DDAE975582D358E552B08B2E7B554EBEDC03DAAB63E6B200DE32AD375D8B53B4B89542D4DAA9813171731EAEFE09918C0B157E2EFD62101E637C2F46D5AEA5437552BFD
	6636D9B555DF0267FF2521CDDC36D682C5A00063318675D548665CAAF857EAC8D7AF7A589A8969F9D2AF8B34D5707C940CDBF5949515586B108D2B270DECF4C39A6AF7B1140F3302BEEB43632781D45505472F3305474F3651D8FD250AEC32E4798F153CD9ED06B649669E155B17CF8D3649FC56D6B46027G4C85D06A212F818E825454539A9C1D6A5651C3BE103FG9B2E8BCCA34F740EF28473D4A872D11E6E476C3F87343DDC58363E5CB203B1CB6FC9A1682E90369D2AED5EFF16ABB8867A019EC6GCC2E27
	BE5C56E26636FD486AD5491ACFF94090D51B3FC7F959AD17319D75326A54CAD4376A34410BD758527B17537CF6DE6F12B6381361F15EBB8C06681578FD175F60F820219FB5147F894B37EA1A47DE5B2229E7D5CD33BB9B7C750FB1DE3C81D32AD748DA49660D19A0638200A5D2569917D76B6C355C65EAEAF1DBF0A48A3B3F1B2D6F198D300EB6103D7C5FD574614AE5BA319D537E7C41D72B3A51FDF700B42FEF2E0F69F39B4176953A0D996DB8FE62054D2D87488158A9ECF239ACB7171573816F51G53G9682
	E4B742B883E8ECA43D1CE9F7ABDD47F01A0EDEF7BCD17BBD713E53D0162FD85B4132A709F63B9BABC4A36DBA5A8E9E2B5C46FE519AFA0CBC5EEABCC6E6349A0F117FEA6DFE8CBC5FEAB486F85E8B2DA82F2B55299BD3AF3562FC8DEE38D3E583DBD6D138CF7F0A856530BB6D5AB93B2DC1554E5A2F28605BB66258B845560488F80FE924F93BF5B34D5BDB2EFCDF19F25112E0646FC50D75A6ACE964361EE9774F5B1C2717FDB8D6D5075A8C0ABA6CEAF1EA9C7FAADBEF845E1B48F04FB947E25D5C63308E573A8E
	39FC16B6371AE7E5326473C7056A29F7F91AD5DB6EA56BE5EB3E35D54BE7FD11DF4C320F607BF673879FFCEC6EE18312F7C95F484562A06A6521496F5FDE393DC0585FEA6575CF2FF11EAD506FC76D2A43E6E974285EC6174316D787331F164F7E72851AB16D4B7736C2B78B4207B13797EC575BBDEA19535DC2940119BD440B3DE5C090AFA76C4EB360921C2A2F772A5AB7454F4EBD021FBF6C9A90581FA7C13960A7EBD070C6F52B91BFFB85BFFB3F7C316027D12DEB82FEAAD4EB1345E32DEB8279EF1C1D99B4
	56DBFB5BA57B785153BF64B29CEA51B42060F1DD62037D06719F5CD0EFF95FAB474A94FBDB10FD96DBBE7D6C1BB24B8F957215EEE37B0582182BCAG2A6D64BFB437E1BE798F42C9BA6CF2B8386010B7530EF3251A16251B7FB4BB0F75CC01A74FFF51B0FF5DA54AEFC8B74AAF1079738D73CBE43EFD19D1FE154CFF49B05FA1731BB20C727BDDA2BDDC3812096B0E7AD50B8A5B99F420BED55BE8CFF2E5D5D0DE255551625F6B582F603A695079DE9CDFFD55F89D28E57ED71324DFFFB5D941BD8B2749572D89D6
	246976581B41D5C5E7D2444CA6EC50474CA201FF0C2F4D0467FCGEEF3A56F221F51D3B4EACA9352DB5BC171B5855259G05CD1C262F092FEB3C5C3332DC06837612GA58EDE2E1FC3DFEE64B5AA978D78C9G338D4BD9BB29DCD6F3F8FC9EC0F933D1392D8A158B037C980029221C4929AF67CA26F29640BBG421C3C5C7400F23BD328DC368B6C8F20429558EE188B4B554B985D8E096BE0385720335EC922584309CCFD5CB1F77C19A4A3FF81E1060B531F678A0E497C2C83691A5C6171D9EE324739D775FEFD
	31576B2A3333AD31302D70F63DAF41F1116D697004E9402B15011366A37B9C5E0E3C621ED920050D5D777EBF142EB100A3AC52C95B26432DEC8632388E43363D94CC7C30F435AA6DEA2BC2B14D91E9242FDCB779D29DC775314B49AB2D894BC26826464DF5524F6D570D652BF1091C767AF422BD45CDF14B119237D862F216912E60AB42C5CBDC2344ED4CA0DC34440D16385900637A393B8863E0C67160BA26307C0696FBFE0BE647C001D5333239170FADCE736D9C2409B2E7FAA806DA6544B9219452B5GBEG
	9E4FB95811A17AA582F053BCB4EF7D6812FE5ED23CFC9F176B2531BE3EE397BCFB3F8E128D9425E09FFD9D47107775685587850F536F9F2D9C68BC14E094839E3432BC61001777314D6B6F6BA9692713662F9EAFE25526EB240B89D0E63E17F471E932DE97E60D6B22D4A3DDB4DD43315A4B25D568E16A76D5FC6C84AD2194EFFBCC72D48D6DC5EA44533EFDC6BCCD17BC65F77605A75E666D1D5466DC20175DC2ED0EBAE2E49395AD54662B1DFD35895E6EC9055A35814D91325D874F695B1D8978A5G664B546E
	7E642FE70B473EACE13678751DC7AEC7D4B2493187F8F4DCA6B934GDFA3663268B3A947522FA9C74EE99C8BDF570964B2D427C89BG7E32DBC90667AA0C6C2E2215E478C5CADF6CCE1CF96C522FE9D36656986E015998EE95314B5660B5EDF4A74EFD11000FED25B56D4D8E5C7B531AF68634E2F5B60037A4638C9D48BB78C5A27DD887364B7CA20562A02352FAF3C6B03915E23D3FD5D0DFB3A03DG4064835F9460000FF8BBF92D47B30FA33EEE4EBCB62577062FEA9F451446742A1EDB565B322CB775864AFA
	E3FBD5AFCA56FBB723B77562E43D713D2AB7C5564B4941FAB3A1BD5F47EDEB094F9FDB4CEC037534ADB056A84E5ADCB8DE8A5B78F949DB927F8143EBBABC9FDFDF3275985393A02F7D8A35FF34A3186F522B016D5902523C1E25CB3D8DC169C1C175781C5160C0BBE43E06EE08969549B80270A966DF123E4BD5326D3D8968534E0674A2000CF6188BG4A819A81425A49466F39E6646F855AF8A8E74F9F4B66F226DF6B1BFE323B544B8F361DF6A37DEC4DDDE72C9F392F667A691FCC7A195ACEFD79C49FF9FD36
	C7DE1784714AF9F06EA39E32BA0807084E3E71B032CB3DE09E4ABB0CF818FD04F830C99E1EC96A9B8F733B548B3669919DC6FD76F1E5A81B96AB3C58531A1275FB5A099DFCAD1B8D4F859D641FAFB817AA78B1DDA3F9C6A677CD1E38AE750265B9F04DC81E15E75FB216072F74D2777725932F9649EBEDCA5FF8F5F729975C7FE1570C7ABF6BF42A60BB669A4D93AD09081B83698557E8CE786BB5BCBF20B53942E5F7EA8A2D4763BBB1AE6172589B6CCE4245B1DC1D4B4302707C0CE9925B7BAD811A591DC4770F
	CA68B303D79463B303C50A711941DF156E4F559262C31DA9F07EBECAA77E7200BF0E7B29449D14387D4B88E71138499257A8F17F48A0DC3B447DCC62A2A46EC3367F0B0174E4093B076126C1FA1E443D404E6896C3BA239F61D24D08AB00F429447DF6A3629C109E580F6CEA038B6372D1101EGB09DE0B1C076CD617105GD5G6DGA3GA6814C86D8F493555D4D6AE67467F11F3C7EBC8E03311D727EBCF6037E3D23BF0F27E02CA422BF5535B9278459E1A8C03E689D792177C562BDDB704866111B82EDD98F
	537B054E1B242B073A18AD5205A168AD963438AC432EDB967DF9357E4C6DAD71DC46687177555D50C969A66FCD5DBB4E5FEB45FB2873C5BA57DB4F4A7FF24DCAB6D63CDE554B96F30CCC083CBFEAEFAA86E7E5A72D4D2AC2716A0E0EB4D1660866BA3CD36A68CF173A7363A6746F2AE7F1661D90FA9EB81AE0BC8F1433BBEEDED70B27CE8D9E6BE79332E51E5FBBA95354E694C344DDD9FD4D6AE9D03B4C95F52CBE4F8BD55F1190EABE61BAAA3E265751DE2923DB3B2CABFA18EA20A3CE4F54292737DB52736E6B
	0E5E6C10749E8B2037C752FB6EC8F77496042477C6321E5EAE055643573623AE8D56C39684636B5081660F31FD65G0A017D3D127968102EGE89CC0F34A2B6702796B87F91183B8DF3183C271B7AB003F8F253C93585EEAF2D03D99A2BDAFA43D3BD3747476C9FAEF1DC6FA0B8D6AA1E48C8CB5862EA4621E43A75357D83ACD6AD1ADDEA1ED720853A696E19EF37EB5DBDB7719787866388A36F63D8E4C0E15385FB0DCA7601E1238B3EC6DFA99F037CA5CFFB2DCBC507B6FF842D9585A359E18B9A14B4DE23807
	216E32F8524B099C1C5FB4174BE1313B516FD64C83710EF0BE061DC51909B9B8765C9E35DE75F8D49BBFEED7C68CC41ACF30354F866F6E019427D832817D8907354D5582B26F39994B2D4E517B03F321ECAA60575D4C754BF57623D664255EE3ED8613BB6BD66B144CDBD88C58CCF55B21FC8440840099G8BEF26B67F3C916BC2C5FBF3CBB363107337E2933BA386F4EC37D059D94BD138636556DA550141D1127376A214B36054DE6AE0D576FAD9C7F77CDB6C741F134B322357E76969680A73FC259E57910313
	F6D1FFC75EEA59D640493748CFE9DC9BD05F284C23C5987B6423D69FF4A39ECB2E2E147962A0292CC464BD729E4E15DDCEE625FCE569AB825A4753CF4A0B5F6757AD477358164B250FEDC55C42778393EF21585768AC237CE9B2FF4BBE237CB970647B0DD9D5FA7BDAA872775C8A3E562DC4A3F13B910D1ADB4997B3B511FF26C95C48E442CD15385B7791AEEB9061969DA11CED904DFD4581EB47C440CF8318B3085B7C42C1DD67969E57701C433954F49B0FEBA4C9FC8743E76B70A236FE0E6209F91097F11B74
	B72B1027C510CE3D1DF02799EE9D2473A46E47D544FB2144BD46661EC1100E1638FCB64F44C1FA32445D390C6ACE13388236CF499E8C7E65E0428DBBCD658E8C263324DFFF2E0FC9E68E2FC8C0390CE212E103392E22867BE312FC1E7A73B27FBC754C86B3BC1DEA2BD986E1CB6C4D2A93AF0D666EAE581DE75E79511E6D3C6D68D6FD5B056975A1632153C51BF345F36263ED62ACE9E7CDC07C829BACAABA66315B8ED99B4A596C47574B0F589C09343343E83DEC4841BE2B00F4A3C03FB052096FDDBADF0B9BB7
	DAA96FF745705B003996EBC2D49867E7F2186EEE3ABCDFBBE226B697C3FE41F79067B56B6D9FF5D61640771EBCFFECC0BE4E81B86779731F8A48C7FE9F8A281F36D11F0FBA5AA66AF359FE3515E4DB722B890A634146105FBD783EA3768E62D9FAF7DB3C697D71093C4FDFDD6E6F737D4E8D217BFCE781F4F9010C613FD69F4237E13395575BB8976D73E7FE076294CFE791AE638E42FD3E0FF0A5F7505962A327BED42E73EC11CF3CA56C1393619BF03A4B4F935D76BB48D6FE4E3EA918886969G73A48FC55B49
	8FDB823846F0443D53D4E220CFFD1FA48A7A785C83F5CA81B60B3CEAF8FFF969522558A71C5E03495D51CB917552843D89C17426427B23713C9C27B7EADFF77452F43C31FB77C17466417BCD42E6B83DE5C7FB435F42A0FA260870785F88FAFC1E2AE273FDA66037C4505CB53482F125102E1638E7AC086B876988099B370966381888DA83FCAC4635885259C388B70061705E438109CBBFC7F7E731ECA8F9FA83581EDE8FEFA81BE59B57F35F76FAC03FF76663B1072DA54DCE956C5DDE47FD308F996EE5001AA1
	6403B5D661FD998DDC88BE260F22BBA27A74092A6E7AB4B1885CC093E133684701701E86AFFF81BB66EDBD1FEE6C637EAFDE8F92FC9FD996484F1B2B7C7C7CE0D9F77CAC93B4E8EC158A9BFBC77093B1042F5771322DE3270D79A9DC6DEF737E535D35C9771CC987A306785BE4E76C109EA05A64F1429798ED3ED77124EF05E7257512616D1063B0B51E2AC51F4FE2ED1075FF4D664049C0F776900A876E4FF8BF949DC0026368D6BDDA9B47325BD5428EBF4E696DB7C15C7FF9162D190BF57A6478F98CEF7A2E9F
	4F7D22669C7ABEAE9372346FD24CD4D9036B40ECC8AF8248389346BAC089C04D1DA45B408C0C23F5FB2EE2F1394B554B60DE7B4317DC3E63663EFD73747394DA9FA77E74874A24DBF2C35E2D516E64E3BBD2BC678EEBE3CFAE5F1DEBD13E2910B7974094897BAC00820052C812EFBABB434353A1681EE2CD3356B532DBC7A49CDE847223F1B346E53B4F25172DBAD2FC7BA71E83A33B4EDD3C5E73D97AFAD122DC1CF8CE89D92FA6A0DEB2D3141BAF1ECBC256DBF5DEDFAFF3A87471D0FF59422121FC6F3D69E8B3
	65C303ED6CE3062F9A9AECE30F25110D59A0EF5AD01A4FBF590E6D675D455BB9A81E9671EC3C2BEB7BB98A0FAF26AA74EF61A7DDF10A18AB7E833BDA584D6FB248EE565DB7C6A9F9AAB701664260B65AC53B536F727B0CF9D163930D5A9673B21A0BA1EFBC5F3E2E7B7CB0F4A1453C67CA4638C02D4B4302F7BCAEF00BBCA7B28D83BB852882F08C63F1EADEAF2E22EBBC619795D4EFA01445F84288F8CE8828772A856774FA704178BA1472990B53C319194348DFFAF8D3CFEB8DFD87026F26A8D8472378DA0369
	BCF87F0B7017F81B2FF5E2BC5670E636AC737B2B2CCC308B08E5957A0E3A3CB3F8BF0BF295C2FB65D1A45F09AAFCEEEB59E620FF3213FF5960FFFFA9277B3EDA0E1F030BB5E2E69636AB6325FC7A96F94F323DBAFEFFE137BA6CD758954633D0505940ED7FF72C9D7E7E126E5D1B53576F032E87F89BE1B7348DAEFF5A1A50716F4CB546716FB5EB0C4F41562D697E5B19ADEBC2FFBB737E9A7D37B3FC6EFF0E6D41ECE0A37DGE2GA681CC0F22F97FA7EBB6A9EC5EB718730F589AB07E0EDFFFEA547E083521E5
	9D333667EFC66356E2BC285625E92EE624CF78FFDD0B7D25395C8C49F18F2DC59DF96C8D0D9AAFAA708F2FE5E7E4EABDC3F3DF7B9136CF1B8FF2AD0E12319A9362B26E06F964EE42A5A611CF1EABF12FE7D1DD1F4445ABD4B7CC62EA952A9BA5F12DEC5FB79F520BA56E09ED2C6EF0E8F7B861F6654AF6A5EE7BF95ADB78A46E31B45AC70C705755286E0461341E6CCF770F23925D7B93E9B7FECC3DF8C35B60B2D7EE27181172BD5203C3E5FA0574BA09AB2EE43A0174C1099BD7C97AAA7F9E79F87BB7633CBE82
	5213G66GAC82C89D919EEF865883D08AD08D20810481C483CC841886B09BE08100F28F0D1BE5EFEFD7C27B3AA26843860F474A4F5B39FFF0FFB2FD9B1E85344A6F21D841436968DFDED72C40E2579C6AA136EA72FD7FBBA9F48FE060BD94A3787DA9ECE7B224E782AC38076EA03FDCC17E09E9A460C6D26CE077E90A8FE9A30946B68B160D0374D400B9GFCDD18072E0CD83B06EF42C5A2A05EF294984F5DD48C835F31A94F3C937E6FC2DDDD1FBFC3EBCFCABE26B8EF83BBC93EC51237CDD548DB5E3D602783
	D45FCB7D724246507353020DBD4FCF2F3373F79566958C770B3B899B599D899ECE976516EE4C1438B2EF054BEB47D50F6B64E9D7201F71BFECEE520047FE7752DA7D20A9F0FF799EDE6594FB3D3713FA335FAD5545B0307FA6C09B28533F019F425B5F70A65B2B81FE5A3D5407A78A7C6D35A9BD351724F45D4FD2FB0B0166CA519E176D0EFD01724730B952948D6F51A47F3CED01723BF77A79A94B6D091F94453F5F8D6667E0B47749119F6E9B1EB9CF3ED3F9B477A9ED7074C573FB8F1C4705EC5E0DG5C08E8
	72854B341E78407509FC4169D0EFEE345F97DC846F8F8A1E380CF76E720F1B81EA200E6EE6F31BB28A761F23C8C7D51501BAFAF237DFC78FD776940350478302E39087C671F584F5446D795466506326F2F34F636662E663F5DD5D5CF5DD3F3459F8DDF7EF562F6B9C5732996D2E816653AAD96E0A441D1738CEC693F1676CB62DD162EFE6FCA13ED4458689BF1871554AF0D5829721431D9738211BF16C03480DAE0F7C56E6B36BBB3B434153A3D9993B932682290FB10CC73B5B2A63BBD6624EAB3416CE6C76BE
	81C269A823BBE30F74D8AF783E9B2FE7E6F74DEC232EAF26689BC53FDDD249560D30D1417B600B8C9FB5AAF89FBC7F942DB3B11017BADA1E7FD7746DF7E31676315E5F4F76AD3674BD3676B9C6874B9B43703E514172FEE0A1F987C25EFCA96FED81F7BB4B47846A3DE6CC283843DDECFD53468437FFAF43779B935CFEEE95359F86F98B4750BC7F9E9EC108B9673BA674758D56EA9BAC552E86D82C71FCE44FF61A2FCE603588713E3B48CF67754E5E51295C6F2723FD6C277373ED3D23B36C93C54AFB65BC79D1
	16987057E2480FF964BC798AD3E348D748E2B119A5104E9A9B9E1FB716F473DA311F17F7359B63472CB96C173D354C5F5E5E4A5E49BE7CB85665F73C262C46B5476EF4581D6AE17ECBBB8A794D7B18AFD882B2D60FA5FF7852A94439A1BDF0AC59E5F505A247082AFBFF65540D5FBF24D45C78B6C6D97CB27DC0773E5BF2635BAE3F21ED70BE0DE6BEF18474E70C6C67BFF6523EE7226C6791D5FE59476A5E732BEE3C9E0EFD83ED74BB5F3775730E9E6B992F1F0F31FA26F15737FEE60F2375F3901B037246854F
	67C3983EF0DC70FC7E360CA317C0DE6CB87289CF32FBEB9AEE81F13ED0685ED5BD9B77996343633347D3591BCEA5D1D905661107BFC59B60798FF56A632F1671C86749CE74EDCA47138F7C774E6B599B101F99857562467BFD60A9700E9360F2E9374F3379F9BA60670DA73B9D413E39DE8C690CD8325BBB527C36BACC77EE7E86629B9F5D50B6F8BFCCBE696F07AFCFDD475A03CB01A8BF2F936B0A5FE5D47577D68A2E96A4A4AA46317E6CD8711B18317EFBAB3DDB4BB6D578F9FE7D3C319DAE3F406E57C19B36
	D8324387D23B5A611153C80B47E6524F204DC546529EFE69C9DC9BC345D6F0456351E5227524454FD7C46A756A123735628C0E812F2047FFC3F54B46F9A0DC5C1F14A4104B6BD1D6F53DCAD2FC4753CF45DDAC49CAD0924A1464C636E5A8D312DAF16F6143DFF1FA84BAE10777AB659A6B8B5C0324EB4A9B1FF9D5B306754DF48620A96F96BB9C2ED62F59DAD727FA3DE64DE5569A21103CE5E7663F9921A94B7D387C7CFCCD49ABEE013D16D53357C18DF1E1505CE42FEBEAF11BDD7575D08509552A66962FEA83
	A9D6CAA94A58CEE56E655DC509FF9813182018C0843EF3ABD3CC387D82C9BCDC22C458397968D7293097D13B49CCAE6C67840145F113A031A6E1016CA43C0732F4F36D23GAC164579977BA6A900EE0250E6A2C834BC200B9F9D357745694FD7FA931564B265AD5CD91FD5BD6CC0C2356185F971EB01C715B4B71E2E601DD090574B9EC9DE2D4D2116ABD975E77097C03C1AEAAB2690BC287ECD76956F8E8C3F6DA835FA9BB55C7A6EAFD7CCCE7CDDC3E5954FF6D81D8DBB38E56EE7BA0BAFBE48468F1B49015F55
	0CDF2412AF292C7E3715DBB79E1FB7A9D1C9B82A24BBDCD65BD1EB9DD8FB1912A6FB55677E0493CAED259F9BB08139F72478A8ECBEDE9C10CCB7796E89DBBEFF79D1D0174F236CE0A5B11010AFCB588EB7DA4188B555D326616FB43ABCDBE45ECD9B34F71FB83636BAC10997C2E2972C7239B52EB0E9CDA8F27ED160DAFB66CF3F9C7D6C01058B204AD1A50959ACB41C69AFF5322539D6755C92B7F47D3248390311AFB72A07F56E9ACDC92E37B65B9DED70D2429CE009675BF116FC9B35C099E91A476A7462D756
	400DB9C89514A5A54A3F731785C33F7F578BCCA2FFD5CD4961FD87D5F7B4282E669D45757560D5CB825011C0FE3DA40FE572034A2C728DF973782C2535E0F8A5CAE2F3C1817B3B137D5D05FFF7B24CCE0659398BFAF321526D3F60FC1F8FE54E71490110AC26E7B6D4B4A589C315C5B0C3701B45E6F63598522BCF581DE63C896C7228E6FE9B38281773869047DB3BC52C69CCE653B50CA7DCC1F8DFE4F5B14EC4C4E773AE43627832DE76AC26B6334C2D6CEFEE5F54315B2F8E821C9FD23EFD1C9679B9CD921C32
	F86A370F53622E1CAAC11CF23E780CE64C29C94F69C1A3CE4D78E753BF0967D2DF40DAC23670AD546E29C06D12FF1F04CB3B5EA7B0647C3A34ED4879BFD55B670CEDB9DED356082F864CE598BA36575B6B58BD00EF33B097FD5C075E5E297721975FFCAC645D6F1287386FDC75C070EFE14C5A0AC2C482FE42836447EE5D7A01927ADE5DD9182A9D1A5551D46C34619D3BC3ED1A6ABF67FFE6AB5277D299114973FE3795793E242B737FD0CB8788GBE833C789EGGD8E0GGD0CB818294G94G88G88GD0E5
	69AEGBE833C789EGGD8E0GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGB29FGGGG
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
			ivjJButtonProjection.setToolTipText("Allows access to the Projection values");
			ivjJButtonProjection.setText("Projection...");
			// user code begin {1}
			
			ivjJButtonProjection.setText("Projection..." + IlmDefines.PROJ_TYPE_NONE );
			
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
	
				ivjJComboBoxType.addItem( IlmDefines.TYPE_THRESHOLD );
				ivjJComboBoxType.addItem( IlmDefines.TYPE_STATUS );
				
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
 * Return the JLabelATKU property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelATKU() {
	if (ivjJLabelATKU == null) {
		try {
			ivjJLabelATKU = new javax.swing.JLabel();
			ivjJLabelATKU.setName("JLabelATKU");
			ivjJLabelATKU.setToolTipText("Automatic threshold kickup offest to be used");
			ivjJLabelATKU.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelATKU.setText("ATKU:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelATKU;
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
 * Return the JTextFieldATKU property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldATKU() {
	if (ivjJTextFieldATKU == null) {
		try {
			ivjJTextFieldATKU = new javax.swing.JTextField();
			ivjJTextFieldATKU.setName("JTextFieldATKU");
			ivjJTextFieldATKU.setToolTipText("Automatic threshold kickup offest to be used");
			// user code begin {1}
			
			ivjJTextFieldATKU.setDocument( new LongRangeDocument() );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldATKU;
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
	
		if( trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_STATUS) )
		{
			trigger.setNormalState( getSelectedNormalState() );
			trigger.setThreshold( new Double(0.0) );
		}
		else
		{
			trigger.setNormalState( new Integer(IlmDefines.INVALID_INT_VALUE) );
			trigger.setThreshold( getSelectedThreshold() );
	
			try
			{
				trigger.setMinRestoreOffset( 
						new Double(getJTextFieldMinRestOffset().getText()) );
			}
			catch( NumberFormatException e )
			{
				trigger.setMinRestoreOffset( new Double(0.0) );
			}

			try
			{
				trigger.setThresholdKickPercent( 
						new Integer(getJTextFieldATKU().getText()) );
			}
			catch( NumberFormatException e )
			{
				trigger.setThresholdKickPercent( new Integer(0) );
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
	getJTextFieldATKU().addCaretListener(this);
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
		setSize(347, 292);
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
		constraintsJComboBoxType.ipadx = -11;
		constraintsJComboBoxType.insets = new java.awt.Insets(6, 5, 3, 33);
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
		constraintsJComboBoxNormalState.gridwidth = 4;
		constraintsJComboBoxNormalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxNormalState.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxNormalState.weightx = 1.0;
		constraintsJComboBoxNormalState.ipadx = 104;
		constraintsJComboBoxNormalState.insets = new java.awt.Insets(2, 5, 2, 7);
		add(getJComboBoxNormalState(), constraintsJComboBoxNormalState);

		java.awt.GridBagConstraints constraintsJTextFieldThreshold = new java.awt.GridBagConstraints();
		constraintsJTextFieldThreshold.gridx = 3; constraintsJTextFieldThreshold.gridy = 2;
		constraintsJTextFieldThreshold.gridwidth = 4;
		constraintsJTextFieldThreshold.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldThreshold.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldThreshold.weightx = 1.0;
		constraintsJTextFieldThreshold.ipadx = 226;
		constraintsJTextFieldThreshold.ipady = 3;
		constraintsJTextFieldThreshold.insets = new java.awt.Insets(2, 5, 2, 7);
		add(getJTextFieldThreshold(), constraintsJTextFieldThreshold);

		java.awt.GridBagConstraints constraintsJLabelMinRestOffset = new java.awt.GridBagConstraints();
		constraintsJLabelMinRestOffset.gridx = 1; constraintsJLabelMinRestOffset.gridy = 3;
		constraintsJLabelMinRestOffset.gridwidth = 3;
		constraintsJLabelMinRestOffset.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelMinRestOffset.ipady = -2;
		constraintsJLabelMinRestOffset.insets = new java.awt.Insets(5, 8, 5, 0);
		add(getJLabelMinRestOffset(), constraintsJLabelMinRestOffset);

		java.awt.GridBagConstraints constraintsJTextFieldMinRestOffset = new java.awt.GridBagConstraints();
		constraintsJTextFieldMinRestOffset.gridx = 4; constraintsJTextFieldMinRestOffset.gridy = 3;
		constraintsJTextFieldMinRestOffset.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldMinRestOffset.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldMinRestOffset.weightx = 1.0;
		constraintsJTextFieldMinRestOffset.ipadx = 64;
		constraintsJTextFieldMinRestOffset.insets = new java.awt.Insets(3, 1, 4, 11);
		add(getJTextFieldMinRestOffset(), constraintsJTextFieldMinRestOffset);

		java.awt.GridBagConstraints constraintsJPanelPeakTracking = new java.awt.GridBagConstraints();
		constraintsJPanelPeakTracking.gridx = 1; constraintsJPanelPeakTracking.gridy = 5;
		constraintsJPanelPeakTracking.gridwidth = 6;
		constraintsJPanelPeakTracking.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelPeakTracking.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelPeakTracking.weightx = 1.0;
		constraintsJPanelPeakTracking.weighty = 1.0;
		constraintsJPanelPeakTracking.ipadx = -5;
		constraintsJPanelPeakTracking.insets = new java.awt.Insets(2, 8, 6, 8);
		add(getJPanelPeakTracking(), constraintsJPanelPeakTracking);

		java.awt.GridBagConstraints constraintsJPanelTriggerID = new java.awt.GridBagConstraints();
		constraintsJPanelTriggerID.gridx = 1; constraintsJPanelTriggerID.gridy = 4;
		constraintsJPanelTriggerID.gridwidth = 6;
		constraintsJPanelTriggerID.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTriggerID.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelTriggerID.weightx = 1.0;
		constraintsJPanelTriggerID.weighty = 1.0;
		constraintsJPanelTriggerID.ipadx = 90;
		constraintsJPanelTriggerID.insets = new java.awt.Insets(5, 8, 1, 8);
		add(getJPanelTriggerID(), constraintsJPanelTriggerID);

		java.awt.GridBagConstraints constraintsJButtonProjection = new java.awt.GridBagConstraints();
		constraintsJButtonProjection.gridx = 5; constraintsJButtonProjection.gridy = 1;
		constraintsJButtonProjection.gridwidth = 2;
		constraintsJButtonProjection.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButtonProjection.insets = new java.awt.Insets(5, 30, 2, 6);
		add(getJButtonProjection(), constraintsJButtonProjection);

		java.awt.GridBagConstraints constraintsJTextFieldATKU = new java.awt.GridBagConstraints();
		constraintsJTextFieldATKU.gridx = 6; constraintsJTextFieldATKU.gridy = 3;
		constraintsJTextFieldATKU.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldATKU.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJTextFieldATKU.weightx = 1.0;
		constraintsJTextFieldATKU.ipadx = 65;
		constraintsJTextFieldATKU.insets = new java.awt.Insets(3, 2, 4, 10);
		add(getJTextFieldATKU(), constraintsJTextFieldATKU);

		java.awt.GridBagConstraints constraintsJLabelATKU = new java.awt.GridBagConstraints();
		constraintsJLabelATKU.gridx = 5; constraintsJLabelATKU.gridy = 3;
		constraintsJLabelATKU.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJLabelATKU.ipadx = 3;
		constraintsJLabelATKU.ipady = -2;
		constraintsJLabelATKU.insets = new java.awt.Insets(5, 12, 5, 1);
		add(getJLabelATKU(), constraintsJLabelATKU);
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
	
		if( getJComboBoxType().getSelectedItem().toString().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD) )
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
		boolean isThresh = IlmDefines.TYPE_THRESHOLD.equalsIgnoreCase(getJComboBoxType().getSelectedItem().toString());

		getJComboBoxNormalState().setVisible(!isThresh);
		getJTextFieldThreshold().setVisible(isThresh);
		getJLabelMinRestOffset().setEnabled(isThresh);
		getJTextFieldMinRestOffset().setEnabled(isThresh);
		getJTextFieldATKU().setEnabled(isThresh);
		getJLabelATKU().setEnabled(isThresh);
		getJCheckBoxPeakTracking().setEnabled(isThresh);
		
		if( isThresh )
		{	
			getJLabelNormalStateAndThreshold().setText( IlmDefines.TYPE_THRESHOLD + ":");
	
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
			getJCheckBoxPeakTracking().setSelected(false);
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
	
		if( trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_STATUS) )
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

		getJTextFieldATKU().setText( trigger.getThresholdKickPercent().toString() );		
		
		//set the projection panels values
		getJButtonProjection().setEnabled(
			trigger.getTriggerType().equalsIgnoreCase(IlmDefines.TYPE_THRESHOLD) );

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
				int stateGroupID = ((com.cannontech.database.data.lite.LitePoint)getJPanelTriggerID().getSelectedPoint()).getStateGroupID();

				LiteStateGroup stateGroup = (LiteStateGroup)
					cache.getAllStateGroupMap().get( new Integer(stateGroupID) );
				
				java.util.Iterator stateIterator = stateGroup.getStatesList().iterator();				
				while( stateIterator.hasNext() )
				{
					getJComboBoxNormalState().addItem( (com.cannontech.database.data.lite.LiteState)stateIterator.next() );
				}
			}
			
		}
	
		return;
	}
}
package com.cannontech.dbeditor.wizard.customer;
/**
 * This type was created in VisualAge.
 */
import java.awt.Dimension;

import com.cannontech.database.data.customer.CICustomerBase;

public class CICustomerBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	private javax.swing.JTextField ivjJTextFieldCompanyName = null;
	private javax.swing.JLabel ivjJLabelKw = null;
	private javax.swing.JLabel ivjJLabelCurtailAmount = null;
	private javax.swing.JLabel ivjJLabelFPL = null;
	private javax.swing.JLabel ivjJLabelKw1 = null;
	private javax.swing.JTextField ivjJTextFieldCurtailAmount = null;
	private javax.swing.JTextField ivjJTextFieldFPL = null;
	private javax.swing.JLabel ivjJLabelWebHome = null;
	private javax.swing.JTextField ivjJTextFieldWebHome = null;
	private javax.swing.JComboBox ivjJComboBoxEnergyCompany = null;
	private javax.swing.JLabel ivjJLabelEnergyCmpy = null;

/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public CICustomerBasePanel() {
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
	if (e.getSource() == getJComboBoxEnergyCompany()) 
		connEtoC7(e);
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
	if (e.getSource() == getJTextFieldCompanyName()) 
		connEtoC1(e);
	if (e.getSource() == getJTextFieldFPL()) 
		connEtoC4(e);
	if (e.getSource() == getJTextFieldCurtailAmount()) 
		connEtoC5(e);
	if (e.getSource() == getJTextFieldWebHome()) 
		connEtoC6(e);
	// user code begin {2}
	// user code end
}


/**
 * connEtoC1:  (JTextFieldCompanyName.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextFieldPhoneNumber.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC3:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC4:  (JTextField2.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC5:  (JTextFieldCurtailAmount.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
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
 * connEtoC6:  (JTextFieldCurtailWebHome.caret.caretUpdate(javax.swing.event.CaretEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(javax.swing.event.CaretEvent arg1) {
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
 * connEtoC7:  (JComboBoxEnergyCompany.action.actionPerformed(java.awt.event.ActionEvent) --> CICustomerBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88GAEE1C7AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DD414571518E8248D0946E8E2ACC9294654B4549AE336B435FBEC13ECB52109B9B52D1BA5DD5338092735B96E46133ADDFB7298C0D0C1D0B1628FBA889ACC88BF0A060CA888C8C45010C815346C2E9F4C87B3F17E1C7900A1925DFB5F5F774D708D94F332A71CF3F95F3B773D7B6E3B6F3E7B6E3B5FB7A4F7EFDCDA32A5171064B4427F5ECFA6A46F94A1AF5C7E61A3829765EACEA7063F82004D64
	113329D0CE845A02B94D69B9E4EED586546F07FA792C66746D703C157C30AA6A98BCC498279DE877BC7A5673430FF3F71E98A71D56E766E7C23989E096609281725CB87ECF7273B8FE9560B671E704E48BA1C9344D5BA7728C788EB7EBB7874AA7G22BC58E6E97EEECEAF02BA3629063255435ABA20DCCF259BE72B92FC6E18C5D6BFF5401205E399788BD8487B2E6425154C260AC8A1E73453D0D637DE6EB95165F517F6A99E0F572329DD0ED2FB27EAF7EADEFFE93F73F345EFAF352BFD4EAE35342BB720F95D
	2A5FE62BEF2A674FF5CAC0EDD5BC2A2B430B721FBD11C1F9037CD0DFC1F1CF1EC01D4541F38240E22F1843F3D955DC464917E711E5B3F6C41CC3090F495E6A5367907753CD99648A72CF5E13497BCD847AC2GA6CB539EA1CB4395A14BEF2B306DA054A78344CB7C039FA07E8928A7812C3EA2E4BC7E3610317D6A8F496AC90B5232A3481869E732D57AF599F367ED4A58C16D26F0A72E3D8FE81381E681A4822C84488CB4276F22767A1B3CF720BC592955FB5DEE2F67244FD767327B4FFA6D2A8B1EEDB620F05C
	A7EA1757EFA7045964A7A7CACC6491D0948835C784B6370BC458D07CA733497213CF2525194CED5E734516AA4E23A5204FADA600FA7B2086756602E76C37910061BF24784586BCDB2B2729ADE0FD95505AB4216776CDC216165469A427225912EEA24B541F55DA92B4A643E2CD170569784F3BD047EB811F578BFEG60B40083A03AD76838F8476111F45C84FB406FD1DC67FBBBFD0ADF2B6BF53A6C2A1F6BBAFD4FF03AB68356EF5B287BB13D4CBAECDC2324C74E186AA5E9D619657EDE260F793D3ADE585867A2
	0E1D54BB54B6B649B53CC077442ADE3606A992FF0962C91F0EE7EB3B026EAD2C67GCD83E06D73ED08DF5A97EEA36FD27C1A3EF09B79418742C6727A1B538781587AFE0B7A46E5D0DF8B109BECCEAF81E881F0G8C86459A87F6147D1D7B2855303E2CEF494E3720D4031ADFE97284B4455325DED07CCE2553258688EFF3E4A76A35B3241F0D6E6FAE9863C355237A954D6975GF7452D02B185D81B7A1D1B310DDF2554B627DB05815CBEA278B66F5C0AFBC989E8FF715995CD55DBB07A791DE893FD4A8560888C
	60B9B1087AF2D21F9B8B4F89C1212B055477658CB427978F883DBC33F38B144DE05C2A4BE6EBF0F621980AFFC058E8CD8D9C12249A5A4F87287C9C7881D4DE95FB7435F6E10B8DD95FA7A54FB7195AA2C242D53EC72F065B62D9BA469A40678DB627D7813483848745797A5C09F6839F14C172644F15D7075AAC4ACCEDFF405C1EC377626FC246C88F5BF31B386D0577CD61E55A887B16697F2895EDF8AA4CEB9E4012C131866BF7D919ED6239A92C71A3BF9812EA57D70435DD379B4F9A35CF75E88D2ACF755861
	A140EDA1EB77FA5DCE3B3D520636D2BC7A2DFAB02400446F583D19610107516A853D005295CD73BBBBFBB5753C2AE9CECF0F5E7FA015A590629283FC2ED585B85715B04754AFE0DFFEA166BAF777D6033D35F83D17FBFDED3813385D2A54C60B21FD6D97E27DBBBE40F84BAE6B5D342E53DF2BB22EDDB8E03F5178D8F37BC87FE038B1D23E84FFFD0CC29B6E03E4DEDF4FEDAECB52ABA8FDAC4CA79EE08940DA0022EBC28F0FD2991343789A79FCF9991F21E398671B96B64E17990359484C7C48B69E1FEFEE63F3
	734DEDBCA1FFF89BDE10EFE6430C7673FC1C1B375FE35893097968AF3B3C9E0FCAB7266CC2D87C7CDA9B4E4369F1EA971D769ED5BB6F7CDCA5C43B06369DC5FDF28BBCDF3AA67CE7FF21701FC51FBFC8967CEF1145AC5E1ABA335E12F00D59F162B55DFFB219FEC1E3DD55253AC1D1755EDE0F46703F24FB096383A99477E81E06FD0B4F34018E9B1D2A4B8E8722CF718C1C03E329248BFC01D6CA7F33632678B325CF8916867AC1E925F4C5CA6B11FA8A9F8FDA5FFB6F0455A46CAF69F67A55A60F2FD7F0B9F861
	E0B99B7FD71AABEC7C4656162FF25C43EC5C8379FD61736E75EB0A53F5520D8A7BAAA528E47BE5AB795BDEDF188C97554E535E2FD66FC758586E26A09B9B163B53DB678D1E822B6E99600BDF22D0E3AEE5C52B6A6F767A5D2A1DCAD12A74EBDC041314BCFA990E32B36172BEFAA6007D126BF056DF97E742C7FB11AE4E84FE7055FBDDAE364500B6F89D774FCDB4CE90FB227CBA3BEBEF127459A350DF98013E2EF6F87AA1D37A93F29E55569C74A56A15DE95C2C4BC541A55819167D9370451AEA82EDEBDFEEC36
	226FF4994EC306BFD1E03E379B8A708C3CAC791FAED8C7B08EE4BC19AF1A23A0CF1F5F690670818FF81EF3985CE34CB9AC8579B1BF11978531B3G3317D72F62B9ED323EE4E994726836091CC48354BBG8623981F04A826B3566E60B1193B98531CDEBDA6D23B97E53B8EE82381CC9EE356AE2EDA34DB8C7495G99B719356BBAAE6583FA8740E0D83B79B7E13B3BE5EE22CC62E6D3DC859CEAFFC891F72E33B9465CCA51AA4D54CFA3101B997F5C1B436F22F105483792708DB78B1B79D901B19EBA9988F83B1C
	74AA40577F4ABE238FE7383EFD9B2004FD9600FB29DD8D8A9B9A5C073262DD0123399DBC746EF1B2746EF30AE6F3C438D95FCF37F51B379E2EAB27418C6B6591F7412976932107AF9A0D7919795995160D9174504E679FB468A17E11CD99AC8F632C95FACFGBA13739E9B5E434DF2CFDDD470521EDEE7E92F66440B0A22A95449B01BE4BCDF21679D46A22BEE96B91CFC6AF30A47C2BC89508E406E139368BD93477581EE96G5B6371A946BD3EECAC0BC3B34609FD71068D6DE4147E1A2F4393873730F58B730F
	C23753375672F193E7CFA7555737D84C72CA51593BAD3B47B11DB60C53F53B601D002576C2B64FFFFDF2CC4CEFA234D9B8CE4C4F1FE3B6BFF20B504B3AEA51AF97F0AD3708FEA9B633FEE3EF91FAF1D75F08DE58180B6418D3015FE2B966E31BCD57E23C98337C780D0F99F3DC0C399B78350D97E3AE5BE09CB3E6BC9BF31E9C73756307209C7DF1A973EB070CFEE441327208FEE461F83636C963437D48587DE867EBG1F99AD7C489F77EDB67811CFE067AA1E9EB8C5E4CCDD087243F9416B8F966238743CA062
	FE7A5383B8F7AB702C86E883F0814448B14A0BC64C131D8A8E13A773540EA65F959FAD6EB53F27392A85517ABDEE0961F9C574507B9D4BA53DC8777E5AE816CB5AA1717FC171D55F50714CA68AA99E6B9A50EA3FA9461FE28B17BB735650B10B436A2C5F0CA17DCA436AADE17D181F58DF0CE797750386D33659743811793B17D3048F355FAAF2BC5BE95FD828A7GAC86D889109383E7BBC0EB0CD84B6BC5E6E7C268DAC6BA9074BC811BE77B319B53CF7710FE617A71450476E373FEB0C74CBBBAC62CD1E6750D
	4930F3C83FF09996184AE033899916CA991E3DC199DE9CD1061C5B4CE478774D623CAB3ECD48B069780D4990B724DF388C17EEB333559E57AEF3DB95778CAE6B760DC25660ED42D6AF9EC45C9C28AF38CD58E5D29166A9049FEB75BABDF8DFE534EF6C4798576BF776B8BD82F7BB45F5F97D74124EF00B8EC44E9FACBEE01EBF785581737CC1520161F3E44B8FC44AAF30FC56B89A67AC06792D84E0B84BDE44115BA1663EDD601C94D78475AA097B1D95F19DD057EE976B7B723EF2A827C2FD8E409200D5G3993
	1A534B81DA819CGE38126824C81D8B2C174DDC37B2E1E4062755C09ACAE2E04328DC01B48627349D04E1B48E2F00C2FD95F037B960459830E916EF7F1EC14271C0F9BCE1F4AF12DE6B4BE0FB9A634F040390F54A69C16C89DF68C194BD61376382EE893E80FBBCC685BF819EDC2C33F00F171763FC32E11F247E60073482763ACFAAB1B5AE7A02086284346481853DE55369113DC95E6FA0848C9A45A5673B627B4EFFD85917EE0C54AF0E7751209C3754FF346451177DEF131795EABADC69D873C3D7EAEB5FCFF
	9DA94E13B47D84EA52D4B7CE035F11E9FFCD717728C37667A95A1F51A275BFD79CE98FB39DCDB8E654D12554516C1C61F43442C4C70CDFFD35115F91492FE6C83CE0043591792DBBEE64F7D472FBE3B3724B39A334DF912FD75D91095F2290FE6FCBFE3BB7A23F9613FE74FCG3C96C6E37A6DB808F1D6D056AF517AG91E708FAF213DC23D39EBBF77A6CAE9ECB7D67FF02E07F1DAC645B3197771666753A5A1CBE0C09C8749D781DC68E3AB75EA6EAAF1E8BAEE5405B8B3C2C1368DDFF2FB16F1188FDD683E4CC
	E2B2B3D99E6EC75E5DFE458DA6F05E27F6113534EFF201686B007AA0C0AC40FC0024C962EE7058DBF8AFBF5322F42AAE3649987EC9957721E946D54ABB78EE0E6C4BD3F3EE5FG23BD54006F9F4DB3F7227F75F3D932BF4FAACA5EBFCF59AAE9A1D9CFCE4F5E3EDE529BDBDBE43F474EE60548ED247D974D1130BE4D7D8C77D25BFA832EC2367DD1667650791B48705B6D1BC35A68F3E077324AC2633D2C61CEF6AFF35CA96EEDFD1A99FDD052FB9CE6740912BEE903997DFEC9BF3D590C1EA06955B6B3FA22243F
	7E0199FD591D622CFFB3C51C752BA56E499C01EB18ACF097EC82B7D9623236A0EEB95457CA1C0762B226C00CB1C560FE39C574AD1238439BC5DFCD6226ED946D22A4AE47C1639828AF1FA25EDDBC7BB763DDB6E71A55027B5A6CAE3BE68A5BE329F705FFEFD4F1A9642E0337E71B6DC33F53DE277434505D4B62471F507888F9D75DA5F2BBCD74FD2A8F6A51GF1F789390287C40EAA7E016924A56A0C69B7C44B5AAAAC89DC1EA403DC4573C40E6A6D82B1E65E5D90DB5D0D383F94985791ED37A019454A0CBEBE
	040EB6DDC80C749F0550D15E6494A37D77A1F45451FB3CBF1B5B537B455C56BEBD07389E2D0A78FD54543BF91E1317AB820524E3CCF686537B6B5DF944B4EE273B174D7FF2210859D75FAD3E1F7A3E1641E5A919AAF077B9C4BB3B44AD5AA0F03192F7DD5ED716C8DC40A6F01977885CBB9257G38B977A06E2D548613B99A632194AE97160350E7A2C0A157617D707CF8B25393631714B39C3F0DC41C878C5604714B1C561CBE01987939EDA371CBB7704B1B964A2F44400F7943B39B4D4F38B118FE62ED46ED41
	F7G9A90996DE9CC4FF0F9FE37E5B8F952087E3E96F9D50549538275948B130771FE2DD2677D704661F88B3EE23E6DE13CDDD01F99B257A36B312D6A41579EF692B30DFEC36690FBAFB8CDFE6B0321920FA15A4A77C3F9C1794CB41BFD86CC7A234E4FA058EBD3FA0808DF2C5651FEEF4572C5BF21FEE671B416AFCA1678FF2278A4831E6511BE320AEF8FD7G2D65DBC27E9F9F45BDB7876A09G4B8156826C0E05BBDF2C387F4616E390B3ECDE264D6BEB01G5BE5380E327915945C5877E4BFCD97BEE55EE35F
	A389372CC90E1453EF09E53E44454BA8FFA14533791D2B42794582AD81A089E0B5C0463D4D6956FB457C161717307961021D54B4254BC15FEC0849614B869D0D81981B1B2F50B8374AFB597827F959F16F501814751B2B997B79F83B313C1C8AE57C3DE67910998EE33F853C4D92DE2E08B85EB39B0C7D5672F6B9773132683EC87D46EEB6762B626DDAF8F9A9E23FE0481DC1636D22F9999B31DFD9C83FB93C5D933CDCEA52EFAFE177E41A3B20FF2BEA0662043FB8EEA3523EDE4A9476357B2199246157EB128B
	A35857CABEEE75377573F86CEB5999E6E333F13E0BDFF31B4A4668CFD58FCF371C981EBE3786EB4CCF35BA50FFF5AB2E00AA679657EF0C012AB69C0D184F57781C22F959B633102F4563D6A257A551707C976BF05F9CFDD960B9792FF48C36671FB89AB9A77074D1731C40F3C74D73F13FBEBA7C77BC7FFAB47277BC2F9CB5FE4F43FC56A27ACEFF812CC992G09EBCE4F85A80E937ECAFBFF9F217E4A44D71D3277E0CE823F88554478972BA24F75FF2AC67E0E34278A6F101DDECD733A113F403B2BF03DB42F0F
	A2994EDF05BA72BBFB9C9AEB4A71C17A3B87175ACD51ACFE9FCFE3398618D7DB1C0869F756A04E81752044DD2F91317FD809DBC4E33EA5D0DFAEF10F5653F7D0D04F780E6457A07A66CA5C1E75625EE017384768599E04FA34446514516FD5219EAFF13DE502DF02447D9B0DABB326433DE63A40E96D08AB01FA35447DF33B68FBDA623EC5634F29D01FA5F1F3E93B79D817386D1F0A3ECBA5EE02CD74AD1EA1F07FF8C8742D16383D0765389257E893B2CF15385C43C266F809CB6814731DA1E206376BB0564A38
	9F6EECG2781AE81E8GB1GF1G73G9682AC83D88310B9934EDB00CA008600F6G57CCE14FB7598E1288F193A079450B9A35DFE139F7A647EDF43D2300CF2C6475471AF70761556A777AD43FB6403074EB973137EBA36F112535A36F1117E8CEC8855BEE9B70719C5EAB35745DC0F7F7C015FB717735B9925794E875861C187BE33E7C3EC20C1D3B3D9E0D451B6E923A16B03705B3C5BE2722A6B43E4EBD85D59E835FD5B3DC8CAC62FCF10718E838BF60593086464073664FEF3E79A69BEB1996AA4F9BF44F25
	BEG4F8F88F9926BC365F93FD1172722E1A4F90CB1F938BC358F303BB34A43461F3BC1445FADF27C679C21B23ECA7771A5203BE41B374BC2E57C6B073A0CB1E5A349A86EEFE6B24642989BC3F436F6C328BCDBE85EE296341BA7653935BDD41EAFCE6B722CEA9FC91EF403CC6172ACB7680C0DB5F7F328BC8B293FD9856D56CAF99605499357244BD37A69C872248DE3D3A55FEDCE4F8AD943846AF72AG5FG4046FB7C0CBE5E6E4E61464BA221F7607071B46079EE48F8DEBADE9460E364F86F346863BDBB62F8
	69430CB75FB09E5B5F476AA27B113A3A117DC8F31D79D97BF15D503376C21D79D95BD1E7BCEB99CE29C33D7500AFED176D3AA56ED3093BCCF9A26E22532EB9A47E8A158B7127D59CD0607B29DC7D9457AE3E79B260BE6538B1F5688BE14A8E2FFCA7FACB9D35CD274B456A5F24ED1C9EF00DD29F93280CCE1FE210FB324469F2CF2A477512B198F36D1CF6CFFDAEE15FBFBB951773F37AA95BF9E86869E176F073F9C2C4BC3D2986E3AB0F5AEF159F4EB44266A95EE7073E5E1EFDA5567292EBC565C1F127F8208D
	7B32671D97F5BE8F560F0E4F71F63D6F228D46181E51377769745432517136756BB2E6FC2E73F1340F0E4F3BD7F5BE1957F4BE8F0C12CF59F51D4F2D49BA1F39077E9EBE07A41F59961D8FD5996713F1F8F4FC7CE85A047D967763B3B74166097F83C9839357082D4169D73BA0D698307686D43B35F3402AB9D4EBC0757729FEEB37578FD5E740AAFEF09A307A64EF29B412D1E6D73B15DE17261183979D0AE6D569DB92EB97FBCDE20528BF90565FCF32254CCDF4A3AC6BBBECCBF9F9C60A0524B611F54CB1B411
	D45C5DCDA40DEE7EA612820EA16863CE09F0A7199938C22E11D42E96115A32C8EC32811B9252D63875E40945C34E4231299A4962EFFA2C783E482611ED9F6185464A5E82D9B9917059783E44AAF58818DD8DCCA5D67E4285AFAFD676568528DB9BD4376231DBDB3D7D400EE614GEB39FC117E836DE4052C68135FB972CA62EFAE84D2483AA632838F39732A1F6EF1550E6F1C798F509A49FA9F3E3F435FBD006A8234C88BE883AE350564F6FF029FC087B455FED2F88FD83BEDADB8D299FE59D9F6DA89B8B47452
	47DAC82A871717ECE1E4176269A9E32E6498537F49CBD47F680C88483B154A0BDCCAA5172DBF4E5EDFF0E6F9C28A31B4128DAE2FE2EFD4D0BDCDE43D66702B810757E58F7AEAEAA9230C7E8C2E7E94E037CBCE9FA7DB0AB9D8A97465603DB1D9AB329AE11902FED2C6DB6219DEAADB586B9DE01DDDE01CCD9A7E744F6BAF1234B1FB34833F78F0FB0705A473C96252EF8D7AB4104A2FF88278B9A1C8E08D13C010A42352BB9715CFF970CFFF250C742E9A594BD486DD4BFAD42F3B6CE4F7375323CA8620BFE01FAF
	59E31B5230B6DB02F7ECBB737C1B1D305E8DA445DDDECE7FD7507F0770FF8545D4D0CC45A1D062AAB26CDFB8BDC88DFE3B460CC46EC6DC6652C6D8A96D05BFAC1A7EAF552BD6426C9AC99A7DD491141D2337BA576B6ED47D3744CF495FB8F9592D3896BEB48941F7DD3762F63A866021017A22A0625F8E4AFD06FB8C8FC1CC7EEC22584DC32C8A315B061A8722F7D062AE7AFF4F28A70F3D2C346FBE9C909F7607CADA2ACB1A4DA5C58F326E6BA76991DD52D4AE296E673ED612D68FDDFD7CCB7B7AC9DABFD42778
	17EA10144915797FA7E92D2924E7F4C957F1E9DB42F47AB5117463D01D8AC95709DC6E416B46DC6E1AF15BA366F2D39FE6B95C3C07433F4D7E5EB91CD29B60FD8F0BBC491F4E62BB5CC86F2A4E3B9517CBD3DC17CFFA6C785E2AEEC0D375FC668F4EA17F00E843090C768F67D06E4F8CFD7E8FD0CB878818BFFE653997GGC0C5GGD0CB818294G94G88G88GAEE1C7AE18BFFE653997GGC0C5GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81
	GBAGGG7397GGGG
**end of data**/
}

/**
 * Return the JComboBoxEnergyCompany property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxEnergyCompany() {
	if (ivjJComboBoxEnergyCompany == null) {
		try {
			ivjJComboBoxEnergyCompany = new javax.swing.JComboBox();
			ivjJComboBoxEnergyCompany.setName("JComboBoxEnergyCompany");
			ivjJComboBoxEnergyCompany.setToolTipText("What energy company owns this customer");
			ivjJComboBoxEnergyCompany.setVisible(false);
			// user code begin {1}
			
			getJComboBoxEnergyCompany().addItem( com.cannontech.common.util.CtiUtilities.STRING_NONE );



			try
			{
				java.sql.Connection conn = com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
				com.cannontech.database.db.company.EnergyCompany[] companies = 
						com.cannontech.database.db.company.EnergyCompany.getEnergyCompanies( conn );
				conn.close();
					
				getJLabelEnergyCmpy().setVisible( companies.length > 0 );
				getJComboBoxEnergyCompany().setVisible( companies.length > 0 );

				for( int i = 0; i < companies.length; i++ )
					getJComboBoxEnergyCompany().addItem( companies[i] );
			}
			catch( java.sql.SQLException e )
			{}
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxEnergyCompany;
}


/**
 * Return the JLabelCurtailAmount property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelCurtailAmount() {
	if (ivjJLabelCurtailAmount == null) {
		try {
			ivjJLabelCurtailAmount = new javax.swing.JLabel();
			ivjJLabelCurtailAmount.setName("JLabelCurtailAmount");
			ivjJLabelCurtailAmount.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelCurtailAmount.setText("Default Curtailment Amount:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelCurtailAmount;
}


/**
 * Return the JLabelEnergyCmpy property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelEnergyCmpy() {
	if (ivjJLabelEnergyCmpy == null) {
		try {
			ivjJLabelEnergyCmpy = new javax.swing.JLabel();
			ivjJLabelEnergyCmpy.setName("JLabelEnergyCmpy");
			ivjJLabelEnergyCmpy.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelEnergyCmpy.setText("Owner Energy Company:");
			ivjJLabelEnergyCmpy.setEnabled(false);
			// user code begin {1}
			ivjJLabelEnergyCmpy.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelEnergyCmpy;
}


/**
 * Return the JLabelPDA property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFPL() {
	if (ivjJLabelFPL == null) {
		try {
			ivjJLabelFPL = new javax.swing.JLabel();
			ivjJLabelFPL.setName("JLabelFPL");
			ivjJLabelFPL.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFPL.setText("Demand Power Level:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFPL;
}


/**
 * Return the JLabelKwh property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKw() {
	if (ivjJLabelKw == null) {
		try {
			ivjJLabelKw = new javax.swing.JLabel();
			ivjJLabelKw.setName("JLabelKw");
			ivjJLabelKw.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelKw.setText("kW");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKw;
}


/**
 * Return the JLabelKw1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelKw1() {
	if (ivjJLabelKw1 == null) {
		try {
			ivjJLabelKw1 = new javax.swing.JLabel();
			ivjJLabelKw1.setName("JLabelKw1");
			ivjJLabelKw1.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelKw1.setText("kW");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelKw1;
}


/**
 * Return the JLabelType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Company Name:");
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
 * Return the JLabelWebHome property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelWebHome() {
	if (ivjJLabelWebHome == null) {
		try {
			ivjJLabelWebHome = new javax.swing.JLabel();
			ivjJLabelWebHome.setName("JLabelWebHome");
			ivjJLabelWebHome.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelWebHome.setText("Home Directory:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelWebHome;
}


/**
 * Return the JTextFieldThreshold property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCompanyName() {
	if (ivjJTextFieldCompanyName == null) {
		try {
			ivjJTextFieldCompanyName = new javax.swing.JTextField();
			ivjJTextFieldCompanyName.setName("JTextFieldCompanyName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCompanyName;
}


/**
 * Return the JTextFieldCurtailAmount property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldCurtailAmount() {
	if (ivjJTextFieldCurtailAmount == null) {
		try {
			ivjJTextFieldCurtailAmount = new javax.swing.JTextField();
			ivjJTextFieldCurtailAmount.setName("JTextFieldCurtailAmount");
			// user code begin {1}

			ivjJTextFieldCurtailAmount.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( 0.0, 99999999.9999, 4) );

						
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldCurtailAmount;
}


/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFPL() {
	if (ivjJTextFieldFPL == null) {
		try {
			ivjJTextFieldFPL = new javax.swing.JTextField();
			ivjJTextFieldFPL.setName("JTextFieldFPL");
			// user code begin {1}

			ivjJTextFieldFPL.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument( 0.0, 99999999.9999, 4) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFPL;
}


/**
 * Return the JTextFieldCurtailWebHome property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldWebHome() {
	if (ivjJTextFieldWebHome == null) {
		try {
			ivjJTextFieldWebHome = new javax.swing.JTextField();
			ivjJTextFieldWebHome.setName("JTextFieldWebHome");
			ivjJTextFieldWebHome.setToolTipText("Directory used by the server for this customers parameters");
			ivjJTextFieldWebHome.setText("/default");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldWebHome;
}


/**
 * getValue method comment.
 */
public Object getValue(Object o)
{
	CICustomerBase customer = (CICustomerBase)o;

	customer.getCiCustomerBase().setCompanyName( getJTextFieldCompanyName().getText() );

	if( getJTextFieldFPL().getText() != null && getJTextFieldFPL().getText().length() > 0 )
		customer.getCiCustomerBase().setCustDmdLevel( 
				Double.valueOf(getJTextFieldFPL().getText()) );

	if( getJTextFieldCurtailAmount().getText() != null && getJTextFieldCurtailAmount().getText().length() > 0 )
		customer.getCiCustomerBase().setCurtailAmount(
				Double.valueOf( getJTextFieldCurtailAmount().getText()) );


/*FIXFIX
	//WebSettingsDefaults, only used if we do not have a CustomerWebSettings row yet!
	if( customer.getCustomerWebSettings().getLogo() == null )
		customer.getCustomerWebSettings().setLogo( com.cannontech.common.util.CtiUtilities.STRING_NONE );

	if( customer.getCustomerWebSettings().getDatabaseAlias() == null )
		customer.getCustomerWebSettings().setDatabaseAlias( com.cannontech.common.util.CtiUtilities.STRING_NONE );

	String home = getJTextFieldWebHome().getText();
	if (home.length() == 0)
		customer.getCustomerWebSettings().setHomeURL("/default");
	else
		customer.getCustomerWebSettings().setHomeURL(home);
*/
	//set the EnergyCompany only if one is selected
	if( !(getJComboBoxEnergyCompany().getSelectedItem() instanceof String) )
	{
		customer.setEnergyCompany( 
			(com.cannontech.database.db.company.EnergyCompany)
				getJComboBoxEnergyCompany().getSelectedItem() );
	}
	else
		customer.setEnergyCompany( null ); //doe not have an EnergyCompany

	return customer;
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
	getJTextFieldCompanyName().addCaretListener(this);
	getJTextFieldFPL().addCaretListener(this);
	getJTextFieldCurtailAmount().addCaretListener(this);
	getJTextFieldWebHome().addCaretListener(this);
	getJComboBoxEnergyCompany().addActionListener(this);
}


/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("CICustomerBasePanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(384, 144);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.gridwidth = 2;
		constraintsJLabelName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelName.ipadx = 9;
		constraintsJLabelName.ipady = -2;
		constraintsJLabelName.insets = new java.awt.Insets(14, 6, 2, 31);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldCompanyName = new java.awt.GridBagConstraints();
		constraintsJTextFieldCompanyName.gridx = 3; constraintsJTextFieldCompanyName.gridy = 1;
		constraintsJTextFieldCompanyName.gridwidth = 4;
		constraintsJTextFieldCompanyName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldCompanyName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldCompanyName.weightx = 1.0;
		constraintsJTextFieldCompanyName.ipadx = 225;
		constraintsJTextFieldCompanyName.insets = new java.awt.Insets(12, 0, 1, 5);
		add(getJTextFieldCompanyName(), constraintsJTextFieldCompanyName);

		java.awt.GridBagConstraints constraintsJLabelEnergyCmpy = new java.awt.GridBagConstraints();
		constraintsJLabelEnergyCmpy.gridx = 1; constraintsJLabelEnergyCmpy.gridy = 5;
		constraintsJLabelEnergyCmpy.gridwidth = 3;
		constraintsJLabelEnergyCmpy.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelEnergyCmpy.ipadx = 4;
		constraintsJLabelEnergyCmpy.ipady = -2;
		constraintsJLabelEnergyCmpy.insets = new java.awt.Insets(6, 6, 15, 1);
		add(getJLabelEnergyCmpy(), constraintsJLabelEnergyCmpy);

		java.awt.GridBagConstraints constraintsJComboBoxEnergyCompany = new java.awt.GridBagConstraints();
		constraintsJComboBoxEnergyCompany.gridx = 4; constraintsJComboBoxEnergyCompany.gridy = 5;
		constraintsJComboBoxEnergyCompany.gridwidth = 3;
		constraintsJComboBoxEnergyCompany.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxEnergyCompany.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxEnergyCompany.weightx = 1.0;
		constraintsJComboBoxEnergyCompany.ipadx = 82;
		constraintsJComboBoxEnergyCompany.insets = new java.awt.Insets(4, 2, 11, 5);
		add(getJComboBoxEnergyCompany(), constraintsJComboBoxEnergyCompany);

		java.awt.GridBagConstraints constraintsJLabelWebHome = new java.awt.GridBagConstraints();
		constraintsJLabelWebHome.gridx = 1; constraintsJLabelWebHome.gridy = 4;
		constraintsJLabelWebHome.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelWebHome.ipadx = 5;
		constraintsJLabelWebHome.ipady = -2;
		constraintsJLabelWebHome.insets = new java.awt.Insets(3, 6, 4, 3);
		add(getJLabelWebHome(), constraintsJLabelWebHome);

		java.awt.GridBagConstraints constraintsJLabelCurtailAmount = new java.awt.GridBagConstraints();
		constraintsJLabelCurtailAmount.gridx = 1; constraintsJLabelCurtailAmount.gridy = 3;
		constraintsJLabelCurtailAmount.gridwidth = 4;
		constraintsJLabelCurtailAmount.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelCurtailAmount.ipadx = 4;
		constraintsJLabelCurtailAmount.ipady = -2;
		constraintsJLabelCurtailAmount.insets = new java.awt.Insets(5, 6, 2, 0);
		add(getJLabelCurtailAmount(), constraintsJLabelCurtailAmount);

		java.awt.GridBagConstraints constraintsJLabelFPL = new java.awt.GridBagConstraints();
		constraintsJLabelFPL.gridx = 1; constraintsJLabelFPL.gridy = 2;
		constraintsJLabelFPL.gridwidth = 2;
		constraintsJLabelFPL.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFPL.ipadx = 6;
		constraintsJLabelFPL.ipady = -2;
		constraintsJLabelFPL.insets = new java.awt.Insets(4, 6, 4, 0);
		add(getJLabelFPL(), constraintsJLabelFPL);

		java.awt.GridBagConstraints constraintsJTextFieldFPL = new java.awt.GridBagConstraints();
		constraintsJTextFieldFPL.gridx = 5; constraintsJTextFieldFPL.gridy = 2;
		constraintsJTextFieldFPL.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldFPL.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldFPL.weightx = 1.0;
		constraintsJTextFieldFPL.ipadx = 71;
		constraintsJTextFieldFPL.insets = new java.awt.Insets(2, 1, 3, 3);
		add(getJTextFieldFPL(), constraintsJTextFieldFPL);

		java.awt.GridBagConstraints constraintsJLabelKw = new java.awt.GridBagConstraints();
		constraintsJLabelKw.gridx = 6; constraintsJLabelKw.gridy = 2;
		constraintsJLabelKw.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelKw.ipadx = 21;
		constraintsJLabelKw.ipady = -2;
		constraintsJLabelKw.insets = new java.awt.Insets(5, 4, 6, 76);
		add(getJLabelKw(), constraintsJLabelKw);

		java.awt.GridBagConstraints constraintsJLabelKw1 = new java.awt.GridBagConstraints();
		constraintsJLabelKw1.gridx = 6; constraintsJLabelKw1.gridy = 3;
		constraintsJLabelKw1.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelKw1.ipadx = 21;
		constraintsJLabelKw1.ipady = -2;
		constraintsJLabelKw1.insets = new java.awt.Insets(6, 4, 4, 76);
		add(getJLabelKw1(), constraintsJLabelKw1);

		java.awt.GridBagConstraints constraintsJTextFieldCurtailAmount = new java.awt.GridBagConstraints();
		constraintsJTextFieldCurtailAmount.gridx = 5; constraintsJTextFieldCurtailAmount.gridy = 3;
		constraintsJTextFieldCurtailAmount.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldCurtailAmount.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldCurtailAmount.weightx = 1.0;
		constraintsJTextFieldCurtailAmount.ipadx = 71;
		constraintsJTextFieldCurtailAmount.insets = new java.awt.Insets(3, 1, 1, 3);
		add(getJTextFieldCurtailAmount(), constraintsJTextFieldCurtailAmount);

		java.awt.GridBagConstraints constraintsJTextFieldWebHome = new java.awt.GridBagConstraints();
		constraintsJTextFieldWebHome.gridx = 2; constraintsJTextFieldWebHome.gridy = 4;
		constraintsJTextFieldWebHome.gridwidth = 5;
		constraintsJTextFieldWebHome.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldWebHome.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldWebHome.weightx = 1.0;
		constraintsJTextFieldWebHome.ipadx = 257;
		constraintsJTextFieldWebHome.insets = new java.awt.Insets(1, 3, 3, 5);
		add(getJTextFieldWebHome(), constraintsJTextFieldWebHome);
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
	if( getJTextFieldCompanyName().getText() == null || getJTextFieldCompanyName().getText().length() <= 0 )
	{
		setErrorString("The Company Name text field must be filled in");
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
		CICustomerBasePanel aCICustomerBasePanel;
		aCICustomerBasePanel = new CICustomerBasePanel();
		frame.setContentPane(aCICustomerBasePanel);
		frame.setSize(aCICustomerBasePanel.getSize());
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
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null )
		return;

	CICustomerBase customer = (CICustomerBase)o;

	getJTextFieldCompanyName().setText( customer.getCiCustomerBase().getCompanyName() );

	getJTextFieldFPL().setText( customer.getCiCustomerBase().getCustDmdLevel().toString() );

	getJTextFieldCurtailAmount().setText( customer.getCiCustomerBase().getCurtailAmount().toString() );

/*FIXFIX
	String home = customer.getCustomerWebSettings().getHomeURL();
	if( home != null )
		getJTextFieldWebHome().setText(home);
*/

	if( customer.getEnergyCompany() != null )
		getJComboBoxEnergyCompany().setSelectedItem( customer.getEnergyCompany() );
}
}
package com.cannontech.dbeditor.wizard.device.lmgroup;


import com.cannontech.database.data.device.lm.LMGroupSADigital;
/**
 * Insert the type's description here.
 * Creation date: (2/25/2004 10:52:28 AM)
 * @author: 
 */
public class SADigitalEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjAddressPanel = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjHyphen1 = null;
	private javax.swing.JTextField ivjOpAddress1JTextField = null;
	private javax.swing.JTextField ivjOpAddress2JTextField = null;
	private javax.swing.JLabel ivjOpAddressJLabel = null;
	private javax.swing.JPanel ivjTimeoutPanel = null;
	private javax.swing.JLabel ivjNominalTimeoutJLabel = null;
	private javax.swing.JComboBox ivjNominalTimeoutJComboBox = null;
	private javax.swing.JLabel ivjVirtualTimeoutJLabel = null;
	private javax.swing.JComboBox ivjVirtualTimeoutJComboBox = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SADigitalEditorPanel.this.getNominalTimeoutJComboBox()) 
				connEtoC3(e);
			if (e.getSource() == SADigitalEditorPanel.this.getVirtualTimeoutJComboBox()) 
				connEtoC4(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == SADigitalEditorPanel.this.getOpAddress1JTextField()) 
				connEtoC1(e);
			if (e.getSource() == SADigitalEditorPanel.this.getOpAddress2JTextField()) 
				connEtoC2(e);
		};
	};
/**
 * SADigitalEditorPanel constructor comment.
 */
public SADigitalEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (OpAddress1JTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (OpAddress2JTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC3:  (TimeoutJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (VirtualTimeoutJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> SADigitalEditorPanel.fireInputUpdate()V)
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
 * Return the AddressPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressPanel() {
	if (ivjAddressPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Addressing");
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setPreferredSize(new java.awt.Dimension(344, 154));
			ivjAddressPanel.setBorder(ivjLocalBorder1);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());
			ivjAddressPanel.setMinimumSize(new java.awt.Dimension(344, 154));

			java.awt.GridBagConstraints constraintsOpAddressJLabel = new java.awt.GridBagConstraints();
			constraintsOpAddressJLabel.gridx = 1; constraintsOpAddressJLabel.gridy = 1;
			constraintsOpAddressJLabel.ipadx = 9;
			constraintsOpAddressJLabel.insets = new java.awt.Insets(72, 49, 68, 4);
			getAddressPanel().add(getOpAddressJLabel(), constraintsOpAddressJLabel);

			java.awt.GridBagConstraints constraintsOpAddress1JTextField = new java.awt.GridBagConstraints();
			constraintsOpAddress1JTextField.gridx = 2; constraintsOpAddress1JTextField.gridy = 1;
			constraintsOpAddress1JTextField.weightx = 1.0;
			constraintsOpAddress1JTextField.ipadx = 25;
			constraintsOpAddress1JTextField.insets = new java.awt.Insets(68, 5, 66, 2);
			getAddressPanel().add(getOpAddress1JTextField(), constraintsOpAddress1JTextField);

			java.awt.GridBagConstraints constraintsOpAddress2JTextField = new java.awt.GridBagConstraints();
			constraintsOpAddress2JTextField.gridx = 4; constraintsOpAddress2JTextField.gridy = 1;
			constraintsOpAddress2JTextField.weightx = 1.0;
			constraintsOpAddress2JTextField.ipadx = 25;
			constraintsOpAddress2JTextField.insets = new java.awt.Insets(68, 2, 66, 85);
			getAddressPanel().add(getOpAddress2JTextField(), constraintsOpAddress2JTextField);

			java.awt.GridBagConstraints constraintsHyphen1 = new java.awt.GridBagConstraints();
			constraintsHyphen1.gridx = 3; constraintsHyphen1.gridy = 1;
			constraintsHyphen1.insets = new java.awt.Insets(69, 2, 71, 1);
			getAddressPanel().add(getHyphen1(), constraintsHyphen1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAddressPanel;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G8CF9D9B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BD495573576557651D629ED344FE45916A826245284FF8DB1181844B4A4D16387932B541014B4F6AD347655371E6F4577BA7CA286D194CC7C8B08FA516B95949324444065F7B11890C0C2D27262409DE0747EFA6FA09723716DFD4E1C193943DCD09B43F2B3F376BE676C6F796DB348659E88CBCFCE49653864F4CE7EF9B71963224FF35C67173F1845F051BF8F4D64B4BF79G5B38AB56B4F84682
	EDFCD2E8E6B6B762C6861463211C7B07504C3761BD07FB7618398EDE0270C9835A14316386066613340B7159C84AA5564D705C8210G383430504C26B0645FE05DA963CDD05EA23FA3A427008877E21DAD661D9A7CD8395EAAF8EEG08A6F56C563DB23D834AD8C702E7483D34EE98BCB791693ED3DE466A7DA802DB776C1E14AC6425691F81B71136AD141FF95CA4E208D4EE72999BBCAB1AAE771C6DF2BBADDD3C4B65F6C9C2D72F455EA958C5496D35740BD7F92F5DE2972E08DD0245616C713A7BBC6565ADD5
	B5E20FA8710EDAD22D09F7890E3193D17ED2F386699B6402F25EA444C51A51E64B203C96A0E39253E1D1D68533456588AEE1521620BA58A7D159C3A629BA0C7DF4CACAD9B876BF1A78845B45821D839C15E5291549F2AD1C49B27A88561D8165F9G098A3E5504788DD04E1D9C1AE91E4CE4FC6F8813317D4B0738751DE7D336861131E6B2154DB1D915B1EDCDF37AAE92B75F5E033E0FG5ABC008400347BC2B38B81EAG7610F8BD3B7BA8BC2BBA25EA375369F6D5F9BCD6075DFB5EED979C70DEDE8E949957AC
	F4393DF60E23B1F946ECB61007C147FD017108C0F57B04E3B194597CGA7C925A969863A4DD83AAF45A377B172BED5373977215D720E235D42619D5B61G22F88B41E77CC245D3DFCDA33100E59350C67D025939FD8B1325B1EDA217DDD2193251C01671CF1CCED97B8BAAC3C604AA8B3571677B50462500EF82688598819086B0A70259B8E4772961EC5CGE3406B629DADFD1D9E5EABD97BC407DD704A363E4BBA142D0D00363B77165BD13BC41C527AA8FE7A7B06F6090FA8CC09176D31CEE3974AFBF9D05E39
	778F0E0DAD0A8F134918B05DCFFD182A60B3893ECC03273E3D0B0CADAC5B009689C06B47127A593F2475B7A97DACA5F83386CF7B790C6031EC83DAA4G756F6B7BF1EE4C1E82639320862083609A40F80048A94C47CBF61F3849F1542471AFEDBBEA4FDB70947C1217EFF079A45E55A53471DE116FF488BECE2EB3EE8F5A35B3205DBDE4FCF7810FFA41A5F8F9C9F43B20F75EA9C0B079E81DC9FB36E19D2FC0282D22D3G86CE8F477A1D32A7871E8E5EA7DD7058F9C9D0EBD07A74BDE813ABFC9B74889D407B06
	A9E82FA6B2674E0377D5D3182DCCF9082B0C8C4DEC0FE4F6316C5D8E4F8FA03885C7F9F90D5805E2705E819623070F67A2FF281F84607895BC8124875898DD534EE231A66BD71CF969D943D8C45870G0D2B5C877531D8CAF8D4G3E83609A4084001887597A9AEDEE577545E43819B143E4D74A3F8AECC7F9AC9A4483754B3C49F109711D2E6BD3CB27767FC9BE46F0BC68359EE0E794730138B70F4626157789AC9ABF72C2A089F655A334EE77BEDCEB04AB02CB2A91BC024B8EAFBEB9961C7BB629F15A6DD6E2
	48C770B8EFD5C3A079947C403EED948FFDE8231E513FC5683CA4F9454EBEC9E891A4C9F475286D3FC7E47185CC09BED957718528EBB168E883E8D7F45D3EBFC793EF0DEE7765BECFAB0EA4B9EE7FCC56E787549F99457C3F51047B2D88253C0D14D57A5DC7743E7BBAG792971B16D0E70C88D08418526EC0E0E9D96CF9B955A8AC20B851956826C1C9A1AD9B91569FF5154EE506E6B12117298A0BC347DEF7A9AF95058C8BF90BC36B79FB00E6D6D870CE3FB670121E3B76F00D16CD25A118328233B5F25998B66
	83B8CFF639DDAE018CC82589C7774DEBDAD18F51A5CA1F08769EC1EA912F8A9CB7E6AA46F48F190BFB617D5AD4B6EF769FE473E661557B39B9DF6CB25CE70D1FDC1692B7D5BE93CCD567CDAA5371BC9CE302C3F002212A5DFDAE0962CF11F9DE467BD2894EF6D54236260FBCD5F63BD77079CA4E3502B56BC441E1B7F74194A0D948EF3A4A18AE71D7F83F45578FB6339007D82A11DA0B2FC5F9470F1F4DB35A6E1B3BC52F5060727431EE0A5A0A0AA97F3BAE69781F7C067817D07E532E7B88FFDCBF5DFD124A1B
	A76E3450C713606DF6FB1D021D88E0617BA519FB95A15F867B139C657F76192B04FF1B68157AF847B7AD46A9CE16E30DC86578506D94DD5F3C9CA759DA785CC1F29618961A399720F49ADB9F9E4CC3BADB9F64C5305A6DF050E107E3E29A0EA9116C9958B8E91CC64F5DDB947A38E1680F8FC3DFBD8CFD17A5981D6AB1A19F67EB1F700FBE81360B3840FDA08C30BDDF7EF69D2D0DF77429FB49A979386FF0E856C60A7FD90171F88FAF4075703252FFC4419B9C6E89E91FF4FE0A6231CF0FD7F442D68297FBB97F
	10D12D4DBFA4009E182B481B8EBE8120EDFB33D1D7A397A7CC47BE3696317CC4A3146DGA32753FE66CC27B6217596AB754CB3E02FBEA3D83D6F9EE5756CD0679A400499C6754E9DE37562013E8EA0F7E6E03D5619D86FABA57FF0C2410DA338BF41C27653B4F62EBA1F2D4D1F58FDA506F3B2090119347F48197A73A6F7086C3B819FBF1345F441C16D1E274A67F3F709E43BAF7BF551C16DFCCDF1AF9CFC931EB002FCF076348BFE969B7187D1D6BC8F48E8593FD30E3D478DBE5FD0D731ADE5F5BF99312D6EEA
	B81258A03C2A15652CCD947AB9E6076BF55A9CCC4C5643A919C16C30D656BF7B572A9DE2D7B727D3196EAE47F335D1AE290B88E9696993ADFD1208878FDE6249D4C1420A23399AEFF5062CDF532FD9CE46DA07E3A0844AE1G51G74FCD8D634D92E3B90F0998F41790F0C53BF0B5AF1DA8978DE00D1GF4BE7CF9914644ADAEC0324D6796ED27BE524DF24C0E93F3AAE53E739E1848D55C58E818A78A593AADA56CA1EA3F5807D4BB0E79C1D5FAB8G556FF5C53F5C68504C46E8265F1AEDC67A0D0CE6F6F970A8
	EBB79EF08B15F6875B346DB29E8E4DAC86E8FD1859652DE3B0D5F3373E9A30733A6F98635B8BFD06BD4C78C6E79871DD2670DDFE5BFC657C55C96D981E13F0A86898CEFA185A1A1B259F430907B16E76823EF8969B431F94EE530C61E698B53C2B87E6E6E56FFA886507B9D8AEEFBA04FC499C4C31B5AF5F043A57C01F97817CGE3G22949EE38E8D1B072A758F11076A2F5667F4964ED24F3A0933861F17E94E6557E4CC2D1FC5F3AE698A7EF1025F7C080A273E3555B15F6681ED64A36C9CF236C84FFF55A301
	BC53E20C4E5E0D035AD9F4E5132E9D9DFF07CCFB597C22992E65656A4E122EF1EBCFE957384A98FA363443539F43724226CBEC2E99934372A57DE43D0A05F2A24086001D33C3B3ABG5A812E4DE6FEFB6910515C9B6837E0932FFA662636C8BEFAFBB65CBA281D5E0693E6873623FA9F5A4E741EB11B79F166ED4A706430B2A44DB672E30FE30F319F411E1DEE2B5BAF6FD57E2B4E480FE90F02DF9EE5FE7C2CADC3162778D1264F40315B5367DF8735536B537B2811CD7F1441E49871A80B25070EA2AE9A4A310F
	323829B80CF79BECCEE9F20BAEBCA3D15A9F8F639E4E6D05AD170B615690DC175BCB8E2694D7E48AFEE6AEB6991F195FB1991F19ABCCC36703EACC414E54B4F793CA566BE520DF92G45CD2DC1DC76E330AFFB0C79A3268EF10DD036AB3827898E5782DC3FA86E43BAED4E856BEAED5D785860796CD60072688960112E2B137ACF740F300F74714CDBDB09DDFDBE41C7A6825C6D483457249D1CC1DE61C35EA9F06C4C56D734C92ED3AB392BCB14F17317221D4663C6F377E6AAC53B627E6D5AE3EC8F759FE47E1E
	8165F9G090A8F0EDFE2713E41403659F3866364BCE1D170988CA9B20E4150A20CFD1F3B4F5BA56863EC9C510B52541932C1921CE89679ACC45ACB3C37C798942713C9FBCA8B567E0122E031CCEDDEF6D4EB7313CA3C9C5D4EECD4E8E00FB2839C6DAFA920BFDC6729FFCA6A19FF9A67B07F74DEC07F0C0472840099F318FF3EE8E33C679970C9G5CBA9D1E720EBA2665DD2A6832AE0375177178607E76822ED80727360BB90A7B813FD21EC34A838A3F4955B8CFB97881F79FCCE8639FC71DE2824E19B500F3G
	0C107B2771BC359F6D506D651D600A960F5045C9CF103B229A56B6816A2F834885FCA9C06B93EC5F98D301B6135399A47EA9BE0E60651C93414B31DE4AF40F01BE9682A4BE216A3A9E5EB7BF9928BB5D93097558DF5E137AFB2BAB84DF71247E5E6A75BAF6EF558E3418A7593CD8C3769BF9F3E1BFB117613EFF1A4D15CD8A2EEABB2B9726603E28E375E64CE53171CEBD53A76DA9D88F814CCF2972D742FB6BD3C67A4413D86BFDCA2F4F9FE98CBE255747F50169938634345FB0192E10F537974AA3955CBDCDCC
	4E718A6EEF3B90378A4A5C538C173909354DD5F04F1A14F5C34195FAD8DB7F53ACB7D1F2B1E08F0827037272FA2FE83772BD0DA48269B8FB052CCD284B282759B84B2EC1DD66C2B99EE05D53ACB76BBF4C4E3F1177CD649AC73CEF788DC2C26B61144D31542645316A99E2671A66F44A33311671F4GBDBC96F1D7EB35679F0C51C5A9F4DFC1691181F41C2360E823212F8C20233C33835AE757E969E8239239BD556D39C3CC378D4FC5F10E194703FEDF11A46B147D8CFD7A1F5F4D2D2F32264B71DB4A6252768C
	734F7732905781E50F025B18033831D00ED0F05F4AE53E1D2760C2AE13B35733B02E1FE5B16D2E479C1A8D4AF60011G618A6D9A21C5BFCB73CD18CBDA2650126BEF772E067158A27355536D8A0F5D374DC3FBF7E2343FC86758BC7A77D2740D47ABF48BDE2FE027D78ADC71F3E8AFA719BF5603BCE9D00EA038FBCF578C4117754BC002B6A1GBBE47FCF0077D372BB65F1094CBDEB61FD034263CA5DD0BCB669F8186685722818277220FEBFCF56BF9B606D73D8AC74080873C3F914027B320545D114027BD9AB
	0B23B885B77FA396C70573192FDE390031D286658E00EBG93945A1F888DF30CF3G17B01F6691A9CD3AB01C0F31AFED39E37E601819A0634A669B1DB1C68F4BC3FF17275F23B23B9B45930D2351ED416368BEB2EFF38BE03DDB40FCBC3569D662288C5AB5AE20BEC65A4585F44DC8D678B76F42F59D96F5515967A45C6578A2EB01836A7B81A82E142C8561D00ED264F959B41CBCDA1962F5722C5548C3F91CA76B488640E7BC4FFC7D5A205C49F0C0574911E48DA9FDDE65C17123893ED203276B67DBB56C3B34
	C620C5BD4F4E07DE729DE06EC23083008D60A2408800898B59F93176D8B1B7CC1E21556DE9040D38C3F3FC237AFDDCFBFB5F9ABD3E112D97B3E67D120B7E6E6A64E039602805B43E974A4F6C853B891E6A17EEC17D38C52119FB81AA815A819CGA397B17DC61373B6E6CB6C22D4A5C9FCD7AF4908B365B0C92D22F103C87DA91135E8BC74933E086D874332F08F5A69761203815D03BE144576118B215EAA0042452119E50B691913367BF14B603DEBF18EEB5784F5F16F5A8B4FEBF2BB9A2363F3070A51D49D0C
	0536910BE90C22FFE2966B47CC62C746E3E6AAD9A7E321FE5CE2A6F3E4BB1E3F141B55F30DFC2760107B393EA7D7CB535C7AD2FA6E61G7AC9BD7D07DDA81FED4053AB38CA643B2F28FA6D9E03DBC26DD0380465F813B69B514B605938843F27B51D6694BDEF1C5E8FCF17501F27A4F7EBE0936F420F19CCA74F1873CE96AB395D30FEEDEE376C4DE3C1F33BE31650988C171F09F0D2B9C07841D907E3BFEB5F9F0C03F1C9E45C708E270C0117B733B130F74AA42E664555498703700D17791963547DE0CC471494
	5A272FCC553B356EE674B61219767513B3EADF767A60737BF5323E5AC19ECF9C0B172F4441E3E31C48E2FCC29C9D9BB360999BC77373344E7DAD7AB3DC825057493A52F1B033F538F1201D2BB7AF053D78D2BA8E30DC3AD4BFD7EFA06BF9A5609B17B279276C40EF39035CE04BB689A3773701F5684020745367109EF89B2F25CF5A7776A03A4AC33EE32E5247B95A4A3F140D0328FCA37A2825EC9CBCDC2E7A7007CD870247C3C971BB9090270AEEB1063FD3213E4FFECF65652F3A99DE34DD42D9355DE3265B15
	11767533CAD51E472B54FE171C3A19FE69BA76FD8CAF765D2D66FDEF4D1D78BEABF02FF67A0E7220EB601766603934FFB19B6752C24C46795CD073505FC07D449C7C9B281F1A355FC051B57CC7D60C67B0085F98008400F5G99AF30757B5871820E2C5F86EBF72D3D87F3F978752C4478EF318457F507E578EFEE77DB700E3C53ADC9EEA7764F7005967417647690241CA737200D3CE2CF2FC42B4A7893963287953A891A46E6E10D9A1BDFE565850F4DD32525663C125325C5F4EF77D2967B563990EC5271824B
	D14CFE9377EDB7F58F5EAACA8E41CAE6E61AC3B832055D4F0CFD0165ADE6F5A01FF9D0CE845870823B6BED4FE1FB4B5C97E10EF9115D09046530BC444897D99F8BC85D98A84781A4815C5C5C4246F2FD2DBA16AF552976DAD6F7B3761273575DC17257E49E26323FD64F74CFDB4674B91845F0954BD84E66793FEB778659776CC8E16B08FE1CC94B68FAB5E2197E5E3703701A8178054BD8CCBBCF9F03E79BFFE970979500EC723ABD02D79A20D872958F7B7E5506C134967A597CDB187781ECG9DG92C0884084
	00A800D8008400F5G994BE1FF8CE0862884E8DD4E6428FA37E888B964049799E0DE1E5EA3D15FC867D45FF408C37A66A478665DF765D81EAD32D86E85994216B35B27AB77244B1633D80AEDA65F722F884DB481D42EE0F51FEFE1F1573E02557D1F962CBB864A91GF3D65CEA5C2D78C00D3B7FEDD4F5FB3565E6F423F24D2B42FEB1EE16A9323E164FF059714C66F7DB034FCFE1566167277B49374C824CAB2D839EF66FE0A577FA5D5DBEC1198727DB3395DC032F496D93713E022EB92FE68DF7EE33459B6595
	7EB2EC3B7D3AB2EDE75E026D6C71037398C6600FE7FF4FE1799C7DBAAADEFF8EFE176047476B4F41F3BA58DC958E340C5F31F3787F655C5E5F52B8EE335D64DBEEC7753266227C0E5F69757D00607D3F536B9B274C4DA30136CA51B7295E08FF63CAEA7F0BAB877B0136B3997AD7126B07983423735214737010636E013AC3DC10B985D66CF3B00EBE6A3CA4F4C92DFC0F52062F473E14BD851D72E4DAAFB99762B750A26F902F12CF5CDA20222B0752A3ED414753F45B7063A946E63C5EBFE19B3C5EBFEDB3DE6F
	675B346BBD45AD3661FC5683F3E83BD26FC5857729020BA7FDA26E9351AE75AA78D708DC083789481061D791397A892EDD462D5660BE15F17FEE43B981D46EF5AB777A7FE943EFBA3BC5070316DFA7F5C497CC910ABD388614D174709A3953955C27CA2CBCA95E5E9C32E858F646F3481B649EF902C18C9AC154CAB607D41035E16ECA7D182AA57805AB75E3EAC1B39BD371C02BFC090D2938165B9B537FE8A5FF3778121E7FD5020FFAC94FBF3515710F81DADE82633F284A087FC8200F8708CC20F61913205AC7
	1E8B720D5A45C13DD5G9B647A59AF2B6D68B784F3F232C8ECB8C417D0CD7F5A0CE37975EA32FFAC04B695AF339C79D757915784E57B4BCC66524FB9C50E77B46F3F693873672A173E819E36DCD5270FB56F4BF36EBC6F3F5CD19E542729E4DE7300BFC7A8FE6E35317B11D18A6EFD32F70E07F212028B4FE27556AB38FC324F333D921AF9719506EBEAC61C846510D7D85CD8DBD4DBFE20F90FEB3E73F6DD7D8D7068EFD5F59A21F95F54FA67F9975CD19E54272B48DEF8AC78B3DC7173F932EF0D0272C285F72F
	1545C82202EBA0FB66525F07E6367F1E61D28B487A8A65EB8A2E1760C2209C2160E28AD8FFB18A6EE3725D5C66C4B8AFA532DC5FE7DBF04D1570D8DC67A6679A1A977412F92D866AB5A9F547DDCFE7F5B965EC5A04BE23745DE4CD15E013C1E923BFC69A4DDB5E305DCA7EBEC4F7F7BA82E4486134797B362CC0DED14DAA2FC732066235F198DE239275F785E5170CED54C44670F8289F2E582827F9300D163428B67AEBD5205C8597D41A5D0A7924ABF060953BF8C715C36CF1E10EC7617715151D776292593DC8
	3475D66C5A28393B43FE52DE8D4DE477CDD4060B7938CF040D12F895D4659D1813E5A7A6A6471779CC0E6CD7195E3B8B82752AEDD3753AD1B034DEF797307EAADFE5FAADA93815BC37DEAF8F7433AFC02F5E0C61757AF68613635A2BAC075C7EF7D58F6FF57C7FC9824E40988F2602AA58C8BB8A60888C7FCA0B943FFF5941A96F97EB5477B375EA1FB54DB75B27BC862DD8DF7B5793AD781B257EFC166A5A8FDBEB1B95DE9DB615570F73EF362D7CDDDB412D713A5120723AA763E65B527FB7665F5E774386BCF2
	B5176E73F25B8767982AC2FF7BE26497B539A9DCCA8377C6AF595FB7F0697DF8D070631F73631EE39F345D2A34EDA01B7904AB274AD3FFBFA9B505CB030674F05340256189855AFBE9BF29F038A13DD86540CA8128717D037B8AB5127B46D20ADC871F97826A209A1714BA1932A1B0F3D7EAD0E7F00BB31A9619C1DA5C8351592E34E003008439C19D7C198A25B625C52EF658686AF839A318BC95BB1D163EABCE8B5FA9D9707F233134FAF9174F41CBF8D22407CD0E7EA427F0691FF1B95DF8E173A19CBA3D2C26
	5040EDA358E601FEE30A5919C66E6DG1C354FE5F788783D2844EDEFC41F1DF0702E1E9314455F9A396C006A9F8883A8EECE234C1061E9F5C2A27E03216F1138B7655BD6A0C95C0E0FBC727F8D41BB72E4C2F91E44A59F1738B7DAC5A72934CD3E494A132F223042B679F6CA45011132820669D3771E7E433C15ED3ED46E0D86EED7B7E0DA84AFB91C8BF67C88D97EDFA36A38CD9E7C7A9CEF8EA176FC64116E13869CE0175C6EE67CCBC51FA4582B58315F87E2D323E05A7A040D7775928700DD52DCE0B3D56164
	2A0BC4ED1C9513C13E9CA29F3632A82DF29E59FAA87FDCE2F4AA17D2473D69F07376BA3ECBF2FB9B38CDD2AF1823576D307BBD6FD7120EB27A598733E9505D9EC5DDD44E023326A264E57FCFC7E52D4A2A034872FB399324A6AEG9625063D3A177782AB415BA0218F5C5EC205762D7D52617975EFF724F04901CA01D73AF927689800179A30253B07C4AAB8648DFA2CD786B9860C258E7411162FFEE6620A0A35C950D99D17CE7EAA88C44AD6EBFD58676C943C5F0D9C37AFF3EC428FD0E28F2A4BFA5EA4E16C62
	DFC1C167F9BA1D99C9915DF257B345636E7F6F4F0868EAD309BBC0FD86CDCF7488EE67092A6EEEB8B2AA9D7C4D6F016E77A95DE39D0B2E4EF67F0FF61CDB7A57CE08289AAE55D9DCCCFE17105FA771F789411490CC49C9F05BDAEE489FBD5D4FF4A6E1C88AC7B0067C78D668D7668ADC2F7053C6DC56ED8A36DE1E7BF04E29A558BD030290317B87C793228B941FE269907EAA42DFC7C9A3B379FD6216B542D6A5BACD4AD4CDD02AF7E5394B95EC05863B0D5EF1945D505EF12C7F4E66205FBF44A7519C415AA47D
	9D0713FC87EBDE9D1AE9DB4D725C332D473960F94096A76FF0400E64F2154B0EB9C16B00A428F7085EEA6C5F476A48C4797F9729C639AFE95A7CBFD0CB8788F32108DE219AGG60CDGGD0CB818294G94G88G88G8CF9D9B0F32108DE219AGG60CDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5B9AGGGG
**end of data**/
}

/**
 * Return the Hyphen1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHyphen1() {
	if (ivjHyphen1 == null) {
		try {
			ivjHyphen1 = new javax.swing.JLabel();
			ivjHyphen1.setName("Hyphen1");
			ivjHyphen1.setText("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHyphen1;
}
/**
 * Return the TimeoutJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getNominalTimeoutJLabel() {
	if (ivjNominalTimeoutJLabel == null) {
		try {
			ivjNominalTimeoutJLabel = new javax.swing.JLabel();
			ivjNominalTimeoutJLabel.setName("NominalTimeoutJLabel");
			ivjNominalTimeoutJLabel.setText("Nominal Timeout: ");
			ivjNominalTimeoutJLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjNominalTimeoutJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutJLabel;
}
/**
 * Return the TimeoutJComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getNominalTimeoutJComboBox() {
	if (ivjNominalTimeoutJComboBox == null) {
		try {
			ivjNominalTimeoutJComboBox = new javax.swing.JComboBox();
			ivjNominalTimeoutJComboBox.setName("NominalTimeoutJComboBox");
			ivjNominalTimeoutJComboBox.setPreferredSize(new java.awt.Dimension(106, 23));
			ivjNominalTimeoutJComboBox.setMinimumSize(new java.awt.Dimension(106, 23));
			// user code begin {1}
			ivjNominalTimeoutJComboBox.addItem("7.5 minutes");
			ivjNominalTimeoutJComboBox.addItem("15 minutes");
			ivjNominalTimeoutJComboBox.addItem("30 minutes");
			ivjNominalTimeoutJComboBox.addItem("60 minutes");
			ivjNominalTimeoutJComboBox.addItem("2 hours");
			ivjNominalTimeoutJComboBox.addItem("4 hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutJComboBox;
}

/**
 * Return the OpAddress1JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddress1JTextField() {
	if (ivjOpAddress1JTextField == null) {
		try {
			ivjOpAddress1JTextField = new javax.swing.JTextField();
			ivjOpAddress1JTextField.setName("OpAddress1JTextField");
			ivjOpAddress1JTextField.setPreferredSize(new java.awt.Dimension(29, 20));
			// user code begin {1}
			ivjOpAddress1JTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddress1JTextField;
}
/**
 * Return the OpAddress2JTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddress2JTextField() {
	if (ivjOpAddress2JTextField == null) {
		try {
			ivjOpAddress2JTextField = new javax.swing.JTextField();
			ivjOpAddress2JTextField.setName("OpAddress2JTextField");
			ivjOpAddress2JTextField.setPreferredSize(new java.awt.Dimension(29, 20));
			// user code begin {1}
			ivjOpAddress2JTextField.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 9) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddress2JTextField;
}
/**
 * Return the OpAddressJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOpAddressJLabel() {
	if (ivjOpAddressJLabel == null) {
		try {
			ivjOpAddressJLabel = new javax.swing.JLabel();
			ivjOpAddressJLabel.setName("OpAddressJLabel");
			ivjOpAddressJLabel.setText("Operational Address: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJLabel;
}
/**
 * Return the TimeoutPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeoutPanel() {
	if (ivjTimeoutPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Timing");
			ivjTimeoutPanel = new javax.swing.JPanel();
			ivjTimeoutPanel.setName("TimeoutPanel");
			ivjTimeoutPanel.setPreferredSize(new java.awt.Dimension(342, 177));
			ivjTimeoutPanel.setBorder(ivjLocalBorder);
			ivjTimeoutPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeoutPanel.setMinimumSize(new java.awt.Dimension(342, 177));

			java.awt.GridBagConstraints constraintsNominalTimeoutJComboBox = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJComboBox.gridx = 2; constraintsNominalTimeoutJComboBox.gridy = 1;
			constraintsNominalTimeoutJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsNominalTimeoutJComboBox.weightx = 1.0;
			constraintsNominalTimeoutJComboBox.insets = new java.awt.Insets(52, 3, 11, 70);
			getTimeoutPanel().add(getNominalTimeoutJComboBox(), constraintsNominalTimeoutJComboBox);

			java.awt.GridBagConstraints constraintsNominalTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJLabel.gridx = 1; constraintsNominalTimeoutJLabel.gridy = 1;
			constraintsNominalTimeoutJLabel.ipadx = 5;
			constraintsNominalTimeoutJLabel.insets = new java.awt.Insets(58, 55, 14, 2);
			getTimeoutPanel().add(getNominalTimeoutJLabel(), constraintsNominalTimeoutJLabel);

			java.awt.GridBagConstraints constraintsVirtualTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsVirtualTimeoutJLabel.gridx = 1; constraintsVirtualTimeoutJLabel.gridy = 2;
			constraintsVirtualTimeoutJLabel.ipadx = 14;
			constraintsVirtualTimeoutJLabel.insets = new java.awt.Insets(17, 55, 60, 2);
			getTimeoutPanel().add(getVirtualTimeoutJLabel(), constraintsVirtualTimeoutJLabel);

			java.awt.GridBagConstraints constraintsVirtualTimeoutJComboBox = new java.awt.GridBagConstraints();
			constraintsVirtualTimeoutJComboBox.gridx = 2; constraintsVirtualTimeoutJComboBox.gridy = 2;
			constraintsVirtualTimeoutJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsVirtualTimeoutJComboBox.weightx = 1.0;
			constraintsVirtualTimeoutJComboBox.insets = new java.awt.Insets(11, 3, 57, 70);
			getTimeoutPanel().add(getVirtualTimeoutJComboBox(), constraintsVirtualTimeoutJComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjTimeoutPanel;
}

/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object o) 
{
	
	LMGroupSADigital digital = null;
	
	if( o instanceof com.cannontech.database.data.multi.MultiDBPersistent )
	{
		digital = (LMGroupSADigital)
				com.cannontech.database.data.multi.MultiDBPersistent.getFirstObjectOfType(
				LMGroupSADigital.class,
				(com.cannontech.database.data.multi.MultiDBPersistent)o );
	}
	else if( o instanceof com.cannontech.database.data.multi.SmartMultiDBPersistent )
		digital = (LMGroupSADigital)
				((com.cannontech.database.data.multi.SmartMultiDBPersistent)o).getOwnerDBPersistent();
	
	
	if( o instanceof LMGroupSADigital || digital != null )
	{
		if( digital == null )
			digital = (LMGroupSADigital) o;
		
		//some annoying but necessary verification of the address string
		StringBuffer opAddress = new StringBuffer();
		if(getOpAddress1JTextField().getText().length() < 2)
			opAddress.append("0");	
		opAddress.append(getOpAddress1JTextField().getText());
		opAddress.append("-");
		opAddress.append(getOpAddress2JTextField().getText());
		digital.getLMGroupSASimple().setOperationalAddress(opAddress.toString());
			
		digital.getLMGroupSASimple().setNominalTimeout(com.cannontech.common.util.CtiUtilities.getIntervalSecondsValueFromDecimal((String)getNominalTimeoutJComboBox().getSelectedItem()));
		
		digital.getLMGroupSASimple().setVirtualTimeout(com.cannontech.common.util.CtiUtilities.getIntervalSecondsValueFromDecimal((String)getVirtualTimeoutJComboBox().getSelectedItem()));
			
	}
	return digital;
}
/**
 * Return the VirtualTimeoutJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getVirtualTimeoutJLabel() {
	if (ivjVirtualTimeoutJLabel == null) {
		try {
			ivjVirtualTimeoutJLabel = new javax.swing.JLabel();
			ivjVirtualTimeoutJLabel.setName("VirtualTimeoutJLabel");
			ivjVirtualTimeoutJLabel.setText("Virtual Timeout: ");
			ivjVirtualTimeoutJLabel.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
			ivjVirtualTimeoutJLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVirtualTimeoutJLabel;
}
/**
 * Return the VirtualTimeoutJComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getVirtualTimeoutJComboBox() {
	if (ivjVirtualTimeoutJComboBox == null) {
		try {
			ivjVirtualTimeoutJComboBox = new javax.swing.JComboBox();
			ivjVirtualTimeoutJComboBox.setName("VirtualTimeoutJComboBox");
			ivjVirtualTimeoutJComboBox.setPreferredSize(new java.awt.Dimension(106, 23));
			ivjVirtualTimeoutJComboBox.setMinimumSize(new java.awt.Dimension(106, 23));
			// user code begin {1}
			ivjVirtualTimeoutJComboBox.addItem("7.5 minutes");
			ivjVirtualTimeoutJComboBox.addItem("15 minutes");
			ivjVirtualTimeoutJComboBox.addItem("30 minutes");
			ivjVirtualTimeoutJComboBox.addItem("60 minutes");
			ivjVirtualTimeoutJComboBox.addItem("2 hours");
			ivjVirtualTimeoutJComboBox.addItem("4 hours");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVirtualTimeoutJComboBox;
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
	getOpAddress1JTextField().addCaretListener(ivjEventHandler);
	getOpAddress2JTextField().addCaretListener(ivjEventHandler);
	getNominalTimeoutJComboBox().addActionListener(ivjEventHandler);
	getVirtualTimeoutJComboBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SADigitalEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsTimeoutPanel = new java.awt.GridBagConstraints();
		constraintsTimeoutPanel.gridx = 1; constraintsTimeoutPanel.gridy = 2;
		constraintsTimeoutPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeoutPanel.weightx = 1.0;
		constraintsTimeoutPanel.weighty = 1.0;
		constraintsTimeoutPanel.insets = new java.awt.Insets(3, 5, 20, 3);
		add(getTimeoutPanel(), constraintsTimeoutPanel);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.VERTICAL;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.insets = new java.awt.Insets(3, 5, 3, 1);
		add(getAddressPanel(), constraintsAddressPanel);
		initConnections();
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SADigitalEditorPanel aSADigitalEditorPanel;
		aSADigitalEditorPanel = new SADigitalEditorPanel();
		frame.setContentPane(aSADigitalEditorPanel);
		frame.setSize(aSADigitalEditorPanel.getSize());
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
	
	if(o instanceof LMGroupSADigital)
	{
		LMGroupSADigital digital = (LMGroupSADigital) o;
		
		StringBuffer address = new StringBuffer(digital.getLMGroupSASimple().getOperationalAddress());
		getOpAddress1JTextField().setText(address.substring(0,2));
		//skip that hyphen at position 2
		getOpAddress2JTextField().setText(address.substring(3,4));

		
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
			getNominalTimeoutJComboBox(), digital.getLMGroupSASimple().getNominalTimeout().intValue() );
	
		com.cannontech.common.util.CtiUtilities.setIntervalComboBoxSelectedItem( 
			getVirtualTimeoutJComboBox(), digital.getLMGroupSASimple().getVirtualTimeout().intValue() );
		
	}
}

public boolean isInputValid() 
{
	
	return true;
}
}

package com.cannontech.dbeditor.wizard.season;

/**
 * Insert the type's description here.
 * Creation date: (6/22/2004 4:44:37 PM)
 * @author: 
 */
public class SeasonDateCreationPanel extends javax.swing.JPanel implements java.awt.event.ActionListener {
	public static final int PRESSED_OK = 0;
	public static final int PRESSED_CANCEL = 1;
	private int response = PRESSED_CANCEL;
	private javax.swing.JButton ivjJButtonCancel = null;
	private javax.swing.JButton ivjJButtonOk = null;
	private javax.swing.JPanel ivjJPanelHold = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxEndDay = null;
	private javax.swing.JComboBox ivjJComboBoxEndMonth = null;
	private javax.swing.JComboBox ivjJComboBoxStartDay = null;
	private javax.swing.JComboBox ivjJComboBoxStartMonth = null;
	private javax.swing.JLabel ivjJLabelEndday = null;
	private javax.swing.JLabel ivjJLabelEndMonth = null;
	private javax.swing.JLabel ivjJLabelStartDay = null;
	private javax.swing.JLabel ivjJLabelStartMonth = null;
	private javax.swing.JPanel ivjJPanelEndDate = null;
	private javax.swing.JPanel ivjJPanelStartDate = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == SeasonDateCreationPanel.this.getJComboBoxStartMonth()) 
				connEtoC1(e);
			if (e.getSource() == SeasonDateCreationPanel.this.getJButtonOk()) 
				connEtoC2(e);
			if (e.getSource() == SeasonDateCreationPanel.this.getJButtonCancel()) 
				connEtoC3(e);
			if (e.getSource() == SeasonDateCreationPanel.this.getJComboBoxEndMonth()) 
				connEtoC4(e);
		};
	};
	private javax.swing.JLabel ivjJLabelSeasonName = null;
	private javax.swing.JTextField ivjJTextFieldSeasonName = null;
/**
 * SeasonDateCreationPanel constructor comment.
 */
public SeasonDateCreationPanel() {
	super();
	initialize();
}
/**
 * SeasonDateCreationPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public SeasonDateCreationPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * SeasonDateCreationPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public SeasonDateCreationPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * SeasonDateCreationPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public SeasonDateCreationPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJComboBoxStartMonth()) 
		connEtoC1(e);
	if (e.getSource() == getJButtonOk()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonCancel()) 
		connEtoC3(e);
	if (e.getSource() == getJComboBoxEndMonth()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxMonth.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonDateCreationPanel.jComboBoxMonth_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxStartMonth_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonOk.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonDateCreationPanel.jButtonOk_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonOk_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (JButtonCancel.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonDateCreationPanel.jButtonCancel_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCancel_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JComboBoxYear.action.actionPerformed(java.awt.event.ActionEvent) --> SeasonDateCreationPanel.jComboBoxYear_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jComboBoxEndMonth_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Comment
 */
/* Override me with an anonymous class !!! */
public void disposePanel()
{
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G59FE57B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155BC8DF8D445350028C1520ACF2534469A955B34C51BFE2615BE3126CAFD5846D7ECD12372ACFD753564BD347849E7296569A51BC0C0FE022086A590C21020B16407109096489FE413EC7EC808C988301B6CCDF6137DF377A659C014F7E6661E39F7B7F773277435FC5F615EB9E74E1CB3E74E1C19B9F7B6420EBD6109EB63F788425AC4C17ED738D690ACBD02704046875EC05C2CC3F1BAC175EFAF40B6
	611E6E84F80683ED576CB8DD3270DFC5C9D00E0232BBB4CE779E3CEF97969AA2BB60A5081C902035546D3FEDF4B955B9A8E7BDAD6F695F844FADG73813794A0B30748AF6CCF1171821437486F8456468B42F2DA27401822425B65FA8B603998A0B41754B9533FCB2667C01954B14033A3175515603901EAF7EDF111DC2F632688E155AF3363B793D92A769116483C2B65E71A7093EA881DE0C8AC07273E212F67C4174B115DE5F6BADDCEC96C32E6DBBAC50BCDF2F93287ED174C9ECB36D7B4FBDD4E6262A67A2C
	B0CBE239C7B4CBB61733416C946D02B50F68FE4E18C45B855D213C0A62FEEBA4761A876F8B811666217ECFEE564B7A5D5297A1AC3BFBEFD07D0B72195E9D790A7E56EF8EA7EEBACC5A3FCC4703700581FD89G536551FE542561B06AF2FFA5299B8265F000799CFF49C0700B213C9CC0A8C09D0F9FC09DDBAFBFA02C6EEC0C5F9AC447148226DBC90122E3589D8FA66C24BE73F4AE9977E1200583C48144822C86C8B992277B007A6ACDB99F413334D3AAF7B99CAEE7295BDDE637F8EADD96518E6F4545C011F1E7
	44AE1747A2884C9FCD46AC8DFD90F20E787BA2815637CF85741FF9E76E93A429B2BED123EFD1CF6544D748ED349ED1756D8831DBCDAD311B8F5E899F99BF06EF2778E5AABC9B2BE729AF10F2C2A14C53C234F36B96544518F017101C5B943FDEC317B90F98621796B29DD695AA3AB09BC766939B2795456974G4DGF600D000F9C5E86367F3724732F195783F47E936B775F73A4D9E292C5FE637089E5956D37BC7333596B03E9BA74C476C92112F9E2325771F51344B52084278C5C54C9E4B0A943BB0590B024A
	960E0E740DADFC8C8F50B93163A89BC39D4767D2FC068A4F467627F4EE1172E1204D81E075DF2675574875B770F67EC671A9458A1E35B30562C979B05066G30712DABA0F1F18D14D30E4569F2G8CGD60069GF30E6198FF18FBE81C7328C1B53E0C77F7396F42D374C99EF31553AB191DDDE20B59E3B3F75AC52FA057D91ECB6C5A694777921D5FDDA023D2F40A9EB9AEFA4C8E911C494B6A3C163B0D541123E6334DA102G07DB40F65F485D8ECF3B59AB1DF5DBA03EAAB598FDDD9E3149003985DA048660FD
	79B1E22FC19AF367437B12E3E82B4BC788AECB9F272B5023DD26663D8D4F9AF0EE51DEDCDCE16BA2EA18BDC368230D35248F2DD0BF61F81CAE82207984444213F13A4813B8CFDFEEC5FF2C58FC2F1075D42326BF92B015B05F7215847A23094A8987FCB440B2G21B4CE1786D0D20AEB6C6F0CA692A3D4FA8436CFF4727365135A3E9C8824FF7EF373F5AAABD1AEEF5060599CD0FEEB1CF3D827FAE76392D7C8FC5B847D9C8688AB4531D9101746FC364C6C9551CB6BBD60E022C599A9D6772779E48D9287C427D4
	A13AC52785DE3C320FC467EFD07C375B45FD6BB10AA7712C9C9C4C4B71B1795B989E5AD04F8624AF21F433A4F9EC1D7D1258A4CA124D592370AF253AF87DC225D76E6B7F94113EC6C19FE3G16723E0A07372B7C5068F275753B1B498C137D79F7F45DDE8975134A509FBEB110BDD896AF975232C2FF3832F51CE3B29690995AB1BC76AB13215340B1992B8C49821BCFBA15CFB25A5AAAC2EBG7DFCG61G0BF87F2FB334EA70FD953AAA3A5DC1E5285B571AAB1385669B2D8541FD7BE20136EF0B855A3E5DD7B0
	3A6FFA8B34FC1751AE9510BE3A861D2A39F02500444FAE1753A9528949D9843617FE391974436634C91F5AACBD2254E43BA4427AD6CEFC7A6BB4C62F0471CAA847D8BA388FE3E946256F89519733B477DFF36E29096FA8E7BE69AED7E2A953E94A91B247C43B68GC3153B7A1D9243DFC7633F0C776AA8EE5F31BB88EFE6B52C0B1D2EB2172F96367A56ACB36DCCB6FBB4081EEE1747A1DAB2FB4D8366EC732014CD87A33B1412CF117703E91FFCF2A2AD48B9A02B572F750B25814D9A0337FB6CE04B419C01697A
	9B5F2DCC5732FEC9F2B96B7B3EF2B5316109EA18AB4F016D423DD9372BF5ACA71B857BD55213B5BED1DD73382EFFFA7BF67F31EF934D1E2BB6742471096A1A4FF53DB5ED2E3F2ECD92CC50FFC4E7E57A3E3E7DCEFFFDCFB9ADD7D75B495A172D4B3B0BC94C188531E4AE4002F2DC17638B899D57E5F973D16E325BD938A3FD2CA031ECF66D8ED5FCDAD24EF2A0DBB87D11B168AF0FC1FF3FEEF47A69B1681E20F4564FBF9712F554ABFE56AF4224A29B0F9AF188776805EF8750DA4C76FEE56F7FC6A1597B59D5FB
	9606FF25C8BB8E7F25086CD37AF87BFFABDAA710BDBCEB132D9B11E652265BE3F340960FEC4264DC51EB7D6ADC11957AC672CA21708C87E03C0FEEA1F58CAE3B4DE29E9A61B14327E84E298A73C97301EFB140CA392D1CD34CAE2C5EDFF93DC5D0DE86A01CE675CACE2B6BBDD40D752683BE6C34D2EF01DF3D4FEA30DEC2E51CEE97003E12559B2EE47596D612FA77701C51A10EDBC0F14F42A6635A84BC8B57A62B73DD96EF0566FAC9A049405A5FE5884C91B49DA36D6A81EFB4205F779FD56FC7CB3DDED7170D
	5AD09E5B0CE275DA4AF0870BB742335B6671CAD530C97121FF9CAAA63A12B31C0C164778879F9F97C61EC9CF518C9BEE774B8769E4EDF61543B152GAED64E379AADB6F1D0C0BBFCFEDA1DB7DB3075E87CDBC16CE0127BBFC5E587539D8FA6B01DDEBDC8FC07667EC86870E5FB87E1FFD4DCDCCDFD076550CC83246F247DE883664A0E5B092F2F06F2F2D51CAE93001D5BAFD4ED127BDB82B89FG1B0FCD296A79988978D800B5GACFE7E220A2CD3930F15E85BA7AC4436DFAA14F35B5E353DC456B3663E3B847D
	1577B573FDA1DBB362132B19CD0B2A955B06C4FEBFA18C0059A33D9A6DB19D6AAC28C6FB2464296D318A703BEAE00E5520BDE6D4FFB9FB2C2AA073E4329B9C267B9F396E4D20D7E88D6AFEFD35166E8BEBB09E755A740A6CC9EDDA187CEF55207CD8E8BB590872DF298C74A583506CC6345DDA6317335D3DA774EADF1A44C2AB67D6735431AAFAD9D150D8B545487C6796E3E02CAABCCE66DEA460979AB1D6FD3FF81BAAD61D0168E0F67640AA444FCF47089DE03D114B9D4708DC3A5E88387ECBA7088DE3214DD5
	G1BEA63F499G9535A8638F47464C111E720D12A39587B05FE82915F3C0354A19BA945E438122D4B8168F7C910DAB0BEAD9BE70AD0EFF1862172B704CC74AA91E14136A40876A70ACEC298A4CF7AE2E73BF532F88A8B3BE4F883E5D816564FAFFBE66B3BD768F3463A8FA113C1E8F56852E67A4FF1189ED6A6B711B458D293896B657E3AE6F17A749BA9A8A658800E80025GC2834CBD0012869C37BC3D569A63BFEE419698A56F43EC11D9BDB99B9E994197E8C3D303168D37B66D5436A10BA482FEBFCBE85332
	218F5A8CEDC09B5667919B121C5D5C86740995136C4F6BE376A729511F0F6970336AA4390D34C654A13CE6F2BA5CB702AFD00756G9D182D7625EA594A8DF527B7222DB28D68EFE10D68EF9BCB89EE8914DFECC45F3AD1CF6C09F12741E5F3CA82469C7DF13227F5F9ECBDB6A762CAA92E4B65218994069BBE91BC37F34D896D5C4E0C935A391D9BCF0C1E377C4609E0399F16E39C267B1AD5503F64B3F1BA06B35988AE834AC59CF73C0F60BA20ACF15C85BB414D01F2A4475516915CC2A8AFBD03637D001D4459
	15D0CEEE0A53E581D4G181A107E8BCA9F06F2D893DBC7E365750C512B6C6AFCA3618DDCBBC66C771A18ACACE78561D949F56C7413B11EFC5EDB721CD57B28D6BD62D3297479783BDB69D8F93D221786A0B82718E55A1F24F7840D1CDA1D59A18A188F382DEA03DC671464AA4F65F36C07F5B84F97B4615E5410CE560434E698C30006E66C670D2923FDFF33B60F34131C7FBE995CE71FBF296D333FBFC9F4723A7ABDDDE220DFAEBF1942E9CA742D12C4876932FC16247C125953A30E706B55141F5102712FAD89
	667B4C1E972B55764C63765437A549E3BB5C0C76B467529CBE14E3GDE6476EC4E8B4C052F81DA72D9E6470C335AFED72401B7824E9400177315B5EABD73850C070F0E084992700748ED04E948E06DBD63575EE15E6FF32968C711E7315FDB6A68374F961837G95AD4AF708517C2823A5189FEDADA5FB979FAFEF2765A12E5B4D83C43E5BA3F60B9E0FE8E129E3A1220568B20F46AAB73C8F3760BEF750C6622B5DBC646A07C05C7AA92937420671FE8554CBGDC96C04927AC6E33F576C703E47EF6FB4C8EF009
	A63758A59CEEA53C4DFD489B8E7523G16G2C8258510AB27B6D0457E176599C7D8E2AA1532F5CC1749B06FAA13C2E250C5635B9836A8E53B899857522F95D3CAA92872B1BE1A61D3609F68BCBFF91FF16695DA4DFD3EDB4F70A76C05AC389649C4ECE2C86175D02764CAD5C4A71B4FDC8F2A3B2ED79871BB96D1453C2A918BBFA5D2EBEDF2CE8E5365B5106670FEF14296919ED0CEEECC37F082AD34F8B936025B645A7C25A3467C51886BE92F08B8370ECBF3C0046385836406F690FD37C2A36406F69F56978BD
	BD399D74E947B56BBB2938364536237E25ED017E1DF9CE51C1FFEE241ECC76D3B4CEB41CE33293B9FE9945DBD4F826D3CFAE1E6FFCC0DBFA8EF59A20FB37120EB8DDF387629E26B8AB14FD9C1711CFF053219C46F13F2F2167A5A847F05C52BD5847E59C370662924E431AFB9EF14FD5235C9C0E5BDD03F2ABB86ECA8D3657F09EEDE6CAD50F39847810732A334DF96DB10F5440AF84DC6CF92DB11FC663510A7301E3FEA345A7DD889C739DF5B86699C08B39007AD798D07F950053DFA4775DDE2D94F89CBAEBCB
	0327D39CCC63075C8A1BC3F4128B8719797939D9E97907738F32B3EE78207A0CDB3471C470B36EC5790CABBF67B558564AFA76625D2D9820A518B0CE651A4819071F8708A24545159E1B254C5CE324D10F456E0E5EA419DFEF4258FD83EDD302F2A840DC93FEEF73FD08F933F95F39CBB0CEB923F95FECD973517885A679AE0DC9B913C75E71E08213F9298FE56EB2031F1A89AE22CF9D9708ADFF98AF885970E474677C68E4FDBEA828691B7C628E51779BFE7CA77C6809AAFE5637DF6D473E2D79D524E07FF1C9
	503BF4E1E65627E879D9324736F6E5778F9218AFEC2FD6FC212CECBC3E40780E54A8FCE976717031F15E1F69B74E4A390F2F864C66398EBCCF2DB6230F3C5A9DA4FFC9D78A46776FE548175309F360E93AEF34C2F918639287F16F9F4EF1D7FC384FDF48F17975F8EE08653801F4BE27B82E36964F837AAE0C930F24536F77D0368304GC4F05A2FA9ED911417F7319CFD12056F3552035DBBE8105B40323D4BBFF604F40DFDE6E0B29CC1E568847FF3C122867FD8B2BAAC743BC1EA45A8F592E5D98819402387F8
	C726B5407B4732CFB39B37527D6ADC404F37205D1FE94731D84A6D77FCAE3D8BA5C29C979177C70AA35FB848778B59863993395FC15A9BCF3D8A391EFCCFE3C2B2346E0984339D565FAEFC0D2E178E2768F0B9EDDDEC5FB517621E81B009386FFABA0FFC1B12E02231B95CED94F89B47723577FACDF45DF5C39B433C1D55B541772E5B68F71C88289B8530CCA438D777240C5297566676B46D7D6347E9243D4C6EB85DE1G065351B5795FC85F3AF1CD1BD1CB7A42644D2E99CD9E731BE9F4AF64867E695D68CBC7
	689C0E0072820E7BF19D4604E59CF7E9BF4604D59C77DE9D7AE1D28F5F0B6C4318505A03BE97D2C7663D9D4A5381428122B96DD6CADB8A65B5BD6C7B59AEAB525E2F5372A342BF1A1F201C68B16AB199791AB2461A6FF8BFE82CDC820B89678C930989FAEBC0CC007213EF3E79A66EFD8461576754FB1264EFE546931A56BA58E1E5B630DA83736E1769580581FE3E9573DFC58692FBDA4C3DA33F5C81324163F20B9EE908E16997E359066F77CE6E3EF066A4794C9366E37B48B7681EA9564A7611EBB9FEFD9F41
	3F284233FD5BAC3EEFDB8934E69B668E67551339B69F4ACBGD6GA4744669B2GCAFA79BDD4C30EB0C61E3A5965B60A8322DD155AE37D3B52B739FB54BFDF0F7B2328FF7D3EB07FBAA168B75366DE79F79672B3B6553616BCD97FCEB6127E4D85DAB4400BGEBG127B62F419FD583F47E90E00E45B419DCAA5495CE525DF0E31F364E32E02A607F0B9F76E403E697BC64E4F8640D982700CAF6CDD35CDFCF21DD079992E51960B31AF55119CC2274BC393CCAC0710D00F31B28A78E2G126C90F76CACB7496AE4
	8FE21D43F616E7B04253A457E1B1B9026E1DFC001BEEC7DC875DBBC5C2F981G1B7FDFCB9FED7EE3AC41B8309C785658597CA765E4076217CC46G0D593BG1F694075690DD2D57E00DFA5E2346E54AD7E348A73905A6726BEF2AFC0639612CC9FEE5FB602CE5965DC02EEDFE02E0058275901390417EA3468D6CE3FF14437CEC29F867A9EAA9F4EFD827EDBF9F2A48E7C082E0529CD821F873F5F0473E0578F6E96AA1E916277851987339C729948214447D9E143092C6F2E361179276B78371994A71C2381DA81
	6CCE165BE6637DF27BD8633DC1B55E5301F70ED3996FF970DE8B4A6E61BE1C331654673784B93EA5ED3302E75F21B96D41BC72BC50F7C036CFD83340E9CF5710EFABC14EAE0579D9E9F98587512E8F1FD5781E5CB39EBEB6860B4E2B4FEAE99F3F25F9D6DBFD7EC87CFCA7337DD227B286969883A67F78270A7C5E6A7148E7F55F2F217D6FBAA0173F19BAB28EC472EFC749AE9687B26129F7316F930C2F36F6248FD8EA112F5945E2039D1ED3E4BEB6C7DFE0FBA8404DF5A16EEE3A07DA8C6525AE74156C3A31FC
	E53D4AD7125C60E7EE45D7F25C0131A1B1035E7D813C410DFD18CD6F993079FF4AE9311807E43FFAE70B9A4F263DEC2F1FED741B778AD9265F5336B500CE22821B530F57854EF9E297490DF37EB93D96BD0453D3C75C25A174B037F6CCE0FEE2A25BB8F97D5A5CC65E59BA75C46DFEA1481E86D629EA70237A4EDE31CBEAB677F01EA9F5C4AE7F364036BAB26DFA5AB7F27F59E6365BAE51ABD4CDD0515943562984B2BD65B5EB336AFDF66FD7F52FBE783E6807D7D5866B7F0F6B03FFABDAD02F7D2D6811FA6D6F
	1B0B6AC73F3B3E38BE785D75E76A55F75759383F5FCE46A6927CA486E0A5C042E7F13A144F78EFB29A52853AE751582F1C327410EFD56457D0924AB7B4866FEBFD63583F21EAEDA4F97BCE9704E787E99F71979A49F8C9AEB7C5B2DCD7A331114756E315D8D599EFEBA43EEE973BA9DA4E3B50BC51E168D743E7F86E1ADE06E7B18B47CD257522209C43F1773FCBF382D0DE49F129BBE92E5893275B61C15C341D58DE1A876F3EE55563EF5F5C000B70E00E6A210DE42F2B757B5AAE3A794D6E6937E577CBB6FBF1
	F133CD320BE5B48A335C15D40F5F6316F9B037F9E6007EE64E8B6BAB00410B72DB86F1BFEE825C9CAFFE5F7F76A066AA97FB310D98DAB7C9827D810AA4BCB36711DFCA4BB1AC331E4413E0B93C5C3C2C3423058765F5706CF9052FAFFDBCFC635B3FE07C72DDA0A576FE5BC5C556476312456CD03C8F3F71B5C8E81B8CA36222B86E31CA3C8F11520FE71A99A6328EB5C0598EB08BA082E08140920065GEBG1287E00D84A881E88630828C8304820483C48D60DC6BECA2395BE0E7C9B949CDA71C474C6ED930B1
	3E2F8D47F89134A58C220F6567795D1DE1397253F65720D1DE1AE59B3C56063ED0B4083E1051CE5AF4C3B984A0FCF03CFBCB9C1B6FF58ABC46DC53C54FFE5046920055G49BEB87B8168FD58F7DDCB66A8FDE78AABDDE77D0ECD453955G6D0478F02C52F6637C0876E1FF4E52F369EAA8A78F41F88C617C882DC55B15G4EB704EDAC29C51BC48EE19B71745B49F2A88B17A0C6DE4279F1C0D4FC4F35FF7C73233ADB615BD477554F8FC70FB2BFE6D995D92F540EFFFE8875B897F2AE21ED369DC75CAD9C574C6F
	272D3E04E31A5E9EFCADB858BE76DA10C7F336A2447066A137FC0721301D5EA96A6E760AFC4D5137A7F3DC1537416535119D067C3D5991BC8779B90D75B917E19CAF635E72363209641D2C177D73CE43D03ED9D0FEEF37DD1863504ED7C6D259A1D07F164B38677C653BA46F5A8D6BG2B330C2EBB51C00F61F58E6F744F4D5EEFA3634AF47F664E317645FEB9B3D59E1D683B86E4EC95147D73B6A12FFE2C1CCA5230D65E21E4CC3E40FBEA0CEF0872E58C0FAFFFDEB40C3FA3FF0C2EFD4661409C552FA93EF5B8
	B0C755BA00F35E8234C54318BB6D981CDCEE2DF712FCF7CF180F752B44C7744F7ABC303FD594DF74F9E0FFFF4377G95C00B789C7BFBF21F565EF753977219728B6DF1B88CF8C3G2D834A6EAF34FC6120D1CB46F4396E1CA0B26681BEBA0036844A4BB5E57C2AF2B4BB2E51104162466D266071E92EE96C78F42FC9FB2FFA3FE964DEF53EC9FB2F7AB313FA2F4AF00F1AC8AC6901753E1557DB44F1ED9C7784ED1360BE35D968B91361FFCB75A2F803C884A27ED92A57A045354A3897D438B699778793093150E5
	2B0B5F51DCEE223149E63733728ADA476604104B6D718A555166B62B74FE0D635A78F8C50D38CBB23E797248A4797EB6E1BEB61F6650BDD0721540F9F6A7456F3A92B84FBEEA47F916893450AB388E06F68577331B3B46763330AEEDBF3B2BEB241FFD37CB5B4F6E6DD27B99334B8B235EFDA4209D5FD70C49279D5F776FA6FCAB2F0CAF3EA7CC41787E977A37E1D226840E03C077EE99D38247619C7F86118334DB26E03C7B175A4979CF78A4791E1DB09F6B57G5DE72E9E515FAB941FB0B5303FC9F5585FE420
	C9D3313F0975DA31B7F6AA337F0A295A63A0CC03B36DB4FFDAA614755334FC615D63DAB29A643A16E95AB2FC008F8D2005C3B9CAD3C65B287E36C8C3865B9BBD5DCB3A4376CDC3E5236CF9F38BF2325272F23577572C2D3F523C94FB5F65D05A3DB42EF699DF7AB605EF5B4E74C03E5330714BCC37584CF6D7CFFAD69A306666711C72A97273D379EEF474F6322735B96DB627D84E7E320C005F2BE7517D7AD230492AE9181728ABA73824EB62F43B2EC15F08999038FD1FD23DDF98387AB937413F030C53BE25CF
	752A7758412BAF7B152BAA030D291966ADB2E1BC0B2E41F1FEEC9F5EC12860381F9B718EC2AB478D6D25EB811407B9EE4001392BD99C77D6BA5ECBD846F1B52CBDA8A7CCC7DC249165A6F35C015DA8B703635E21F78C0B20EC60382692145B41F1FB8CA8F79E477DE7A5560B1E0EBE7C6DF6E5DC3F2BFA4FE93F7AE3DCF5D5E530BE6B298D132F0D53E5DE0BF6788F7ADB94BD149B2EC5BB7C2FD1697B3ADA655D7CF7481FDF7ABB48982ED37AF42D6AFD0B66BD142FD676012BAAC31EABB4E6F740F85A79B807D2
	1C8F4A33B82E11FE271A8B65E80E4B255F4C16C079C50E9BCA4139321A6336F6931C613AB81D69BA449551BC0E8465699CF77BF11C4B33B82E388E6772DC0EAB20772D66C3F99147357772BBF69CD7DD0AF3B96BFA4439797D72126BB12F71DD7A3DC3A229EE328E8A1823F9185AE196548B63F54572C42CAB607AE8A54B363C367A767967C8E612CF9CF2FBFF26B1B2865A0A3D9E3FB17EB70DDF89A1709E02B2E66D754F45FC0746CCBD508DA17C0E02634B64DBFCA1015FA18F53F8BB9D7033389E1DC67F3E5C
	770E5217E7295E8B206E22906CCB830B3BD096E6E09BD9867F3E1451781A8574229958175E12715C378856976B0C403E98288C8970D3380C3FD60E15DBD34B18B743BF3796BDC33953457A5534DB5BE77A288DE220FEAC3741AA5BC81FF972DD45E7CE547B5BF955FB8A6D6EFA6DBB0F8FD25FCC3A81764BB7209CEBC670FC63D4BAFFCB202E1157B754790F4D85BA57FCC01FFE835AEDD64A17991B289B1431E1B2BF6C564EC91E20B1A29A6A47F07D6A0F7B5B653D1D0A7FBDC6E345EA281BB0937D2F0B6A1F83
	E57DCCEC4318665F47CE9ABB2CC017E6E29FBF6B7DB2F3E96E4CC07F73523893897885DC4675E35EB3D65BF179CCFF7FDBB3D371BFE60B159FA8BED2D6BE1E7D7CE13E1F5F1E2A707E44B84A9D0D02C260AB64FCC7F6ABFC4E3D6367FBEF2F42576B1858592318D4175F4F6FD35AD9BC5A1D18G79D3B2943EDD3B474FF777FE05EF23E17CFCEF9ED078AACBA6565F7403CAFFCFFD2434F3A4F5BC72031FA12D9FAB6DAE7CC4E977C95B44FC47F3C861BD1DB1FE1B5C1A2370FDD8B7FE3E570EA8FC0E140959F2E7
	1152E77DD1251D07746317FF31D861BBB4813D279DD378BECA9BBF5F8B47953E3A5E0975F753C9253F07CA14F616D4FEB95FB9D3CACA6CEF172F693C9D963EF9AF8909DE0F70764876CA3F7E6CB373AED66C0897622B04F5D61A5B2D929287C912586706A879A9C00D77B3618B5226D6C992626B7BA4E19D3BA087D236F2A9D5B4653BECA03FD877073BF571C28208E02949AAA1016497C11207C954C9AEB71147C3AA39971702126847612AB1CB6BD47C537C784D20E911C609F0100D41C67939BEC89D12D10917
	C12B4EC80EF52AD6B778571104CD6CA728E964B728451CC8D60F593C817C1B38E4FC37865471881B7DDC6C97F7967CB16605962FCED8D7A56CA42BE71368219728C47253D47CEB3F27058DEE72D79748BA8EA360250FC42FB4E4970D420E6EB364AF16F9A551D20AD7063C12708E7DCBB507487512C3863357AA11F473B1231060A4FF17D8F81B116DE6E74FA1F6CF6B989D46352554218512ED94C05F6DD4DF52CAB6EFE57B03DB776F2DFEF13ECE08BFADEC343B4C165366AE49652992B6C8D60F68353A6C961F
	7BA4EBA809DEC2A11B919DB477816FBE69ECB6792197D73A4FF7FB6866651BCF43D879BC42A1DA13ECCA32F98DCB39556C81D12227CAA2FFE75965496034293B258F1F28FC2FA3DED8ABF712187E831F9BDC3C5B6C305907602502CEE8BAA8FBA45056E3F6FA499F76814D5282B4C3929710FD7363B937FE6F75F6AAC0E11504BD4C1440FA28C7F4B98E15F6F75B1CA2EFG6C8A4D277266C91D6C00BAEF7BEEFA277A29B7BB418FAA841DA3A7077E1FCB7F4FA37F67D2CCAE45646601F1D78A237E8B247B304F54
	F978E4A7431FFD9AC6D0FA6E25476FFADE3FF285746E3410C87FE6908CC232D22B2E5F51A9FA2E1BF7EB6ADB37AC1BC94648CDDC45C75A5D679BB1A9498C58A6899B694568B472B3EBB0BF3DAAD98CCAD418076011F44AE911C9098413A6896BD915B45A5ECE6A0FA65A01AD549F89B6F524E39174DEBE3C2414C12BFEC87FBFB8E1E3912E0FA96FA7C4BCAD9F757139CCD23CC7B5CABBC74C1F7F87FDF1CCCAFDFEA18D836187E376629F38CFB5CA1F5ED375E93D065E248EC989601212700F67D14D7FE49E55B1
	FE6B7F537429DB6953FAD51F5E9F0FC789F8277F6095756F4ED6DF3BA768EF61CD33655F974D8E7C5DD9D88FBD4B82BEE6B6FED3DE51CD8E90416EE8B7B94CF63BE4367715BAAD643EF65910A4AAF7046F6CA16DFB310ECCE434089E22F72F0A677FGD0CB878860E0D62CC1A0GG0CE7GGD0CB818294G94G88G88G59FE57B060E0D62CC1A0GG0CE7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGFBA0GGGG
**end of data**/
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 5:15:35 PM)
 * @return int
 */
public com.cannontech.database.db.season.DateOfSeason getDateOfSeason() 
{
	com.cannontech.database.db.season.DateOfSeason sDate =
		new com.cannontech.database.db.season.DateOfSeason();
	
	sDate.setSeasonName(getJTextFieldSeasonName().getText());

	sDate.setSeasonStartMonth( new Integer(getJComboBoxStartMonth().getSelectedIndex()) );

	sDate.setSeasonStartDay( 
			new Integer( Integer.parseInt(getJComboBoxStartDay().getSelectedItem().toString()) ) );
			
	sDate.setSeasonEndMonth( new Integer(getJComboBoxEndMonth().getSelectedIndex()) );

	sDate.setSeasonEndDay( 
			new Integer( Integer.parseInt(getJComboBoxEndDay().getSelectedItem().toString()) ) );
	
	return sDate;
}
/**
 * Return the JButtonCancel property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCancel() {
	if (ivjJButtonCancel == null) {
		try {
			ivjJButtonCancel = new javax.swing.JButton();
			ivjJButtonCancel.setName("JButtonCancel");
			ivjJButtonCancel.setMnemonic('c');
			ivjJButtonCancel.setText("Cancel");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCancel;
}
/**
 * Return the JButtonOk property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonOk() {
	if (ivjJButtonOk == null) {
		try {
			ivjJButtonOk = new javax.swing.JButton();
			ivjJButtonOk.setName("JButtonOk");
			ivjJButtonOk.setMnemonic('k');
			ivjJButtonOk.setText("Ok");
			ivjJButtonOk.setMaximumSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setPreferredSize(new java.awt.Dimension(73, 25));
			ivjJButtonOk.setMinimumSize(new java.awt.Dimension(73, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonOk;
}
/**
 * Return the JComboBoxDay property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxEndDay() {
	if (ivjJComboBoxEndDay == null) {
		try {
			ivjJComboBoxEndDay = new javax.swing.JComboBox();
			ivjJComboBoxEndDay.setName("JComboBoxEndDay");
			// user code begin {1}

			ivjJComboBoxEndDay.setMaximumRowCount(5);

			for( int i = 1; i <= 31; i++ ) //January should be our starting month
				getJComboBoxEndDay().addItem( new Integer(i) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxEndDay;
}
/**
 * Return the JComboBoxYear property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxEndMonth() {
	if (ivjJComboBoxEndMonth == null) {
		try {
			ivjJComboBoxEndMonth = new javax.swing.JComboBox();
			ivjJComboBoxEndMonth.setName("JComboBoxEndMonth");
			// user code begin {1}

			for( int i = 0; i < SeasonDatesTableModel.DATE_SYMBOLS.getMonths().length; i++ )
				if( SeasonDatesTableModel.DATE_SYMBOLS.getMonths()[i].length() > 0 )
					ivjJComboBoxEndMonth.addItem(SeasonDatesTableModel.DATE_SYMBOLS.getMonths()[i] );
				
/*			ivjJComboBoxMonth.addItem("January");
			ivjJComboBoxMonth.addItem("February");
			ivjJComboBoxMonth.addItem("March");
			ivjJComboBoxMonth.addItem("April");
			ivjJComboBoxMonth.addItem("May");
			ivjJComboBoxMonth.addItem("June");
			ivjJComboBoxMonth.addItem("July");
			ivjJComboBoxMonth.addItem("August");
			ivjJComboBoxMonth.addItem("September");
			ivjJComboBoxMonth.addItem("October");
			ivjJComboBoxMonth.addItem("November");
			ivjJComboBoxMonth.addItem("December");
*/			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxEndMonth;
}
/**
 * Return the JComboBoxDay property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxStartDay() {
	if (ivjJComboBoxStartDay == null) {
		try {
			ivjJComboBoxStartDay = new javax.swing.JComboBox();
			ivjJComboBoxStartDay.setName("JComboBoxStartDay");
			// user code begin {1}

			ivjJComboBoxStartDay.setMaximumRowCount(5);

			for( int i = 1; i <= 31; i++ ) //January should be our starting month
				getJComboBoxStartDay().addItem( new Integer(i) );

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxStartDay;
}
/**
 * Return the JComboBoxMonth property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxStartMonth() {
	if (ivjJComboBoxStartMonth == null) {
		try {
			ivjJComboBoxStartMonth = new javax.swing.JComboBox();
			ivjJComboBoxStartMonth.setName("JComboBoxStartMonth");
			// user code begin {1}

			for( int i = 0; i < SeasonDatesTableModel.DATE_SYMBOLS.getMonths().length; i++ )
				if( SeasonDatesTableModel.DATE_SYMBOLS.getMonths()[i].length() > 0 )
					ivjJComboBoxStartMonth.addItem(SeasonDatesTableModel.DATE_SYMBOLS.getMonths()[i] );
				
/*			ivjJComboBoxMonth.addItem("January");
			ivjJComboBoxMonth.addItem("February");
			ivjJComboBoxMonth.addItem("March");
			ivjJComboBoxMonth.addItem("April");
			ivjJComboBoxMonth.addItem("May");
			ivjJComboBoxMonth.addItem("June");
			ivjJComboBoxMonth.addItem("July");
			ivjJComboBoxMonth.addItem("August");
			ivjJComboBoxMonth.addItem("September");
			ivjJComboBoxMonth.addItem("October");
			ivjJComboBoxMonth.addItem("November");
			ivjJComboBoxMonth.addItem("December");
*/			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxStartMonth;
}
/**
 * Return the JLabelEndday property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelEndday() {
	if (ivjJLabelEndday == null) {
		try {
			ivjJLabelEndday = new javax.swing.JLabel();
			ivjJLabelEndday.setName("JLabelEndday");
			ivjJLabelEndday.setText("Day: ");
			ivjJLabelEndday.setMaximumSize(new java.awt.Dimension(57, 17));
			ivjJLabelEndday.setPreferredSize(new java.awt.Dimension(57, 17));
			ivjJLabelEndday.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelEndday.setMinimumSize(new java.awt.Dimension(57, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelEndday;
}
/**
 * Return the JLabelYear property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelEndMonth() {
	if (ivjJLabelEndMonth == null) {
		try {
			ivjJLabelEndMonth = new javax.swing.JLabel();
			ivjJLabelEndMonth.setName("JLabelEndMonth");
			ivjJLabelEndMonth.setText("Month: ");
			ivjJLabelEndMonth.setMaximumSize(new java.awt.Dimension(57, 17));
			ivjJLabelEndMonth.setPreferredSize(new java.awt.Dimension(57, 17));
			ivjJLabelEndMonth.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelEndMonth.setMinimumSize(new java.awt.Dimension(57, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelEndMonth;
}
/**
 * Return the JLabelSeasonName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSeasonName() {
	if (ivjJLabelSeasonName == null) {
		try {
			ivjJLabelSeasonName = new javax.swing.JLabel();
			ivjJLabelSeasonName.setName("JLabelSeasonName");
			ivjJLabelSeasonName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSeasonName.setText("Season Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSeasonName;
}
/**
 * Return the JLabelDay property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartDay() {
	if (ivjJLabelStartDay == null) {
		try {
			ivjJLabelStartDay = new javax.swing.JLabel();
			ivjJLabelStartDay.setName("JLabelStartDay");
			ivjJLabelStartDay.setText("Day:");
			ivjJLabelStartDay.setMaximumSize(new java.awt.Dimension(57, 17));
			ivjJLabelStartDay.setPreferredSize(new java.awt.Dimension(57, 17));
			ivjJLabelStartDay.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelStartDay.setMinimumSize(new java.awt.Dimension(57, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartDay;
}
/**
 * Return the JLabelMonth property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartMonth() {
	if (ivjJLabelStartMonth == null) {
		try {
			ivjJLabelStartMonth = new javax.swing.JLabel();
			ivjJLabelStartMonth.setName("JLabelStartMonth");
			ivjJLabelStartMonth.setText("Month:");
			ivjJLabelStartMonth.setMaximumSize(new java.awt.Dimension(57, 17));
			ivjJLabelStartMonth.setPreferredSize(new java.awt.Dimension(57, 17));
			ivjJLabelStartMonth.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelStartMonth.setMinimumSize(new java.awt.Dimension(57, 17));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartMonth;
}
/**
 * Return the JPanelEndDate property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelEndDate() {
	if (ivjJPanelEndDate == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder1.setTitle("End Date");
			ivjJPanelEndDate = new javax.swing.JPanel();
			ivjJPanelEndDate.setName("JPanelEndDate");
			ivjJPanelEndDate.setBorder(ivjLocalBorder1);
			ivjJPanelEndDate.setLayout(new java.awt.GridBagLayout());
			ivjJPanelEndDate.setMaximumSize(new java.awt.Dimension(312, 97));
			ivjJPanelEndDate.setPreferredSize(new java.awt.Dimension(312, 97));
			ivjJPanelEndDate.setMinimumSize(new java.awt.Dimension(312, 97));

			java.awt.GridBagConstraints constraintsJLabelEndday = new java.awt.GridBagConstraints();
			constraintsJLabelEndday.gridx = 1; constraintsJLabelEndday.gridy = 2;
			constraintsJLabelEndday.insets = new java.awt.Insets(7, 18, 23, 2);
			getJPanelEndDate().add(getJLabelEndday(), constraintsJLabelEndday);

			java.awt.GridBagConstraints constraintsJLabelEndMonth = new java.awt.GridBagConstraints();
			constraintsJLabelEndMonth.gridx = 1; constraintsJLabelEndMonth.gridy = 1;
			constraintsJLabelEndMonth.insets = new java.awt.Insets(26, 18, 7, 2);
			getJPanelEndDate().add(getJLabelEndMonth(), constraintsJLabelEndMonth);

			java.awt.GridBagConstraints constraintsJComboBoxEndMonth = new java.awt.GridBagConstraints();
			constraintsJComboBoxEndMonth.gridx = 2; constraintsJComboBoxEndMonth.gridy = 1;
			constraintsJComboBoxEndMonth.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxEndMonth.weightx = 1.0;
			constraintsJComboBoxEndMonth.ipadx = -24;
			constraintsJComboBoxEndMonth.insets = new java.awt.Insets(24, 3, 3, 130);
			getJPanelEndDate().add(getJComboBoxEndMonth(), constraintsJComboBoxEndMonth);

			java.awt.GridBagConstraints constraintsJComboBoxEndDay = new java.awt.GridBagConstraints();
			constraintsJComboBoxEndDay.gridx = 2; constraintsJComboBoxEndDay.gridy = 2;
			constraintsJComboBoxEndDay.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxEndDay.weightx = 1.0;
			constraintsJComboBoxEndDay.ipadx = -24;
			constraintsJComboBoxEndDay.insets = new java.awt.Insets(4, 3, 20, 130);
			getJPanelEndDate().add(getJComboBoxEndDay(), constraintsJComboBoxEndDay);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelEndDate;
}
/**
 * Return the JPanelHold property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelHold() {
	if (ivjJPanelHold == null) {
		try {
			ivjJPanelHold = new javax.swing.JPanel();
			ivjJPanelHold.setName("JPanelHold");
			ivjJPanelHold.setLayout(new java.awt.FlowLayout());
			getJPanelHold().add(getJButtonOk(), getJButtonOk().getName());
			getJPanelHold().add(getJButtonCancel(), getJButtonCancel().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelHold;
}
/**
 * Return the JPanelStartDate property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelStartDate() {
	if (ivjJPanelStartDate == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog", 0, 14));
			ivjLocalBorder.setTitle("Start Date");
			ivjJPanelStartDate = new javax.swing.JPanel();
			ivjJPanelStartDate.setName("JPanelStartDate");
			ivjJPanelStartDate.setBorder(ivjLocalBorder);
			ivjJPanelStartDate.setLayout(new java.awt.GridBagLayout());
			ivjJPanelStartDate.setMaximumSize(new java.awt.Dimension(312, 97));
			ivjJPanelStartDate.setPreferredSize(new java.awt.Dimension(312, 97));
			ivjJPanelStartDate.setMinimumSize(new java.awt.Dimension(312, 97));

			java.awt.GridBagConstraints constraintsJLabelStartMonth = new java.awt.GridBagConstraints();
			constraintsJLabelStartMonth.gridx = 1; constraintsJLabelStartMonth.gridy = 1;
			constraintsJLabelStartMonth.insets = new java.awt.Insets(27, 18, 6, 2);
			getJPanelStartDate().add(getJLabelStartMonth(), constraintsJLabelStartMonth);

			java.awt.GridBagConstraints constraintsJLabelStartDay = new java.awt.GridBagConstraints();
			constraintsJLabelStartDay.gridx = 1; constraintsJLabelStartDay.gridy = 2;
			constraintsJLabelStartDay.insets = new java.awt.Insets(7, 18, 23, 2);
			getJPanelStartDate().add(getJLabelStartDay(), constraintsJLabelStartDay);

			java.awt.GridBagConstraints constraintsJComboBoxStartMonth = new java.awt.GridBagConstraints();
			constraintsJComboBoxStartMonth.gridx = 2; constraintsJComboBoxStartMonth.gridy = 1;
			constraintsJComboBoxStartMonth.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxStartMonth.weightx = 1.0;
			constraintsJComboBoxStartMonth.ipadx = 21;
			constraintsJComboBoxStartMonth.insets = new java.awt.Insets(24, 3, 3, 85);
			getJPanelStartDate().add(getJComboBoxStartMonth(), constraintsJComboBoxStartMonth);

			java.awt.GridBagConstraints constraintsJComboBoxStartDay = new java.awt.GridBagConstraints();
			constraintsJComboBoxStartDay.gridx = 2; constraintsJComboBoxStartDay.gridy = 2;
			constraintsJComboBoxStartDay.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJComboBoxStartDay.weightx = 1.0;
			constraintsJComboBoxStartDay.ipadx = -24;
			constraintsJComboBoxStartDay.insets = new java.awt.Insets(4, 3, 20, 130);
			getJPanelStartDate().add(getJComboBoxStartDay(), constraintsJComboBoxStartDay);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelStartDate;
}
/**
 * Return the JTextFieldSeasonName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldSeasonName() {
	if (ivjJTextFieldSeasonName == null) {
		try {
			ivjJTextFieldSeasonName = new javax.swing.JTextField();
			ivjJTextFieldSeasonName.setName("JTextFieldSeasonName");
			ivjJTextFieldSeasonName.setPreferredSize(new java.awt.Dimension(150, 21));
			ivjJTextFieldSeasonName.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldSeasonName;
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 5:15:35 PM)
 * @return int
 */
public int getResponse() {
	return response;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

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
	getJComboBoxStartMonth().addActionListener(ivjEventHandler);
	getJButtonOk().addActionListener(ivjEventHandler);
	getJButtonCancel().addActionListener(ivjEventHandler);
	getJComboBoxEndMonth().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("SeasonDateCreationPanel");
		setPreferredSize(new java.awt.Dimension(331, 343));
		setLayout(new java.awt.GridBagLayout());
		setSize(331, 343);
		setMaximumSize(new java.awt.Dimension(331, 343));
		setMinimumSize(new java.awt.Dimension(331, 343));

		java.awt.GridBagConstraints constraintsJTextFieldSeasonName = new java.awt.GridBagConstraints();
		constraintsJTextFieldSeasonName.gridx = 2; constraintsJTextFieldSeasonName.gridy = 1;
		constraintsJTextFieldSeasonName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldSeasonName.weightx = 1.0;
		constraintsJTextFieldSeasonName.ipadx = 73;
		constraintsJTextFieldSeasonName.insets = new java.awt.Insets(15, 1, 7, 8);
		add(getJTextFieldSeasonName(), constraintsJTextFieldSeasonName);

		java.awt.GridBagConstraints constraintsJLabelSeasonName = new java.awt.GridBagConstraints();
		constraintsJLabelSeasonName.gridx = 1; constraintsJLabelSeasonName.gridy = 1;
		constraintsJLabelSeasonName.ipadx = 7;
		constraintsJLabelSeasonName.ipady = 1;
		constraintsJLabelSeasonName.insets = new java.awt.Insets(17, 10, 9, 1);
		add(getJLabelSeasonName(), constraintsJLabelSeasonName);

		java.awt.GridBagConstraints constraintsJPanelHold = new java.awt.GridBagConstraints();
		constraintsJPanelHold.gridx = 1; constraintsJPanelHold.gridy = 4;
		constraintsJPanelHold.gridwidth = 2;
		constraintsJPanelHold.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHold.weightx = 1.0;
		constraintsJPanelHold.weighty = 1.0;
		constraintsJPanelHold.ipadx = 166;
		constraintsJPanelHold.ipady = 15;
		constraintsJPanelHold.insets = new java.awt.Insets(7, 2, 18, 2);
		add(getJPanelHold(), constraintsJPanelHold);

		java.awt.GridBagConstraints constraintsJPanelStartDate = new java.awt.GridBagConstraints();
		constraintsJPanelStartDate.gridx = 1; constraintsJPanelStartDate.gridy = 2;
		constraintsJPanelStartDate.gridwidth = 2;
		constraintsJPanelStartDate.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelStartDate.weightx = 1.0;
		constraintsJPanelStartDate.weighty = 1.0;
		constraintsJPanelStartDate.insets = new java.awt.Insets(7, 10, 9, 9);
		add(getJPanelStartDate(), constraintsJPanelStartDate);

		java.awt.GridBagConstraints constraintsJPanelEndDate = new java.awt.GridBagConstraints();
		constraintsJPanelEndDate.gridx = 1; constraintsJPanelEndDate.gridy = 3;
		constraintsJPanelEndDate.gridwidth = 2;
		constraintsJPanelEndDate.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelEndDate.weightx = 1.0;
		constraintsJPanelEndDate.weighty = 1.0;
		constraintsJPanelEndDate.insets = new java.awt.Insets(9, 10, 6, 9);
		add(getJPanelEndDate(), constraintsJPanelEndDate);
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
public void jButtonCancel_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	response = PRESSED_CANCEL;
	disposePanel();

	return;
}
/**
 * Comment
 */
public void jButtonOk_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	response = PRESSED_OK;
	disposePanel();
	
	return;
}
/**
 * Comment
 */
public void jComboBoxStartMonth_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int maxDay = 31;

	java.util.GregorianCalendar gc = new java.util.GregorianCalendar();
	gc.setTime( new java.util.Date() ); //just set it to todays date just in case
	gc.set( gc.MONTH, getJComboBoxStartMonth().getSelectedIndex() );

	maxDay = gc.getActualMaximum( gc.DAY_OF_MONTH );

	//only reset the day selection if the current
	//  selection is not a valid day for the new month
	Integer oldSelection = null;
	if( getJComboBoxStartDay().getSelectedItem() != null )
		oldSelection = (Integer)getJComboBoxStartDay().getSelectedItem();
	
	getJComboBoxStartDay().removeAllItems();
	for( int i = 1; i <= maxDay; i++ )
		getJComboBoxStartDay().addItem( new Integer(i) );

	if( oldSelection != null )
		getJComboBoxStartDay().setSelectedItem( oldSelection );

	return;
	
}
/**
 * Comment
 */
public void jComboBoxEndMonth_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	int maxDay = 31;

	java.util.GregorianCalendar gc = new java.util.GregorianCalendar();
	gc.setTime( new java.util.Date() ); //just set it to todays date just in case
	gc.set( gc.MONTH, getJComboBoxEndMonth().getSelectedIndex() );

	maxDay = gc.getActualMaximum( gc.DAY_OF_MONTH );

	//only reset the day selection if the current
	//  selection is not a valid day for the new month
	Integer oldSelection = null;
	if( getJComboBoxEndDay().getSelectedItem() != null )
		oldSelection = (Integer)getJComboBoxEndDay().getSelectedItem();
	
	getJComboBoxEndDay().removeAllItems();
	for( int i = 1; i <= maxDay; i++ )
		getJComboBoxEndDay().addItem( new Integer(i) );

	if( oldSelection != null )
		getJComboBoxEndDay().setSelectedItem( oldSelection );

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		SeasonDateCreationPanel aSeasonDateCreationPanel;
		aSeasonDateCreationPanel = new SeasonDateCreationPanel();
		frame.setContentPane(aSeasonDateCreationPanel);
		frame.setSize(aSeasonDateCreationPanel.getSize());
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
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 3:26:23 PM)
 */
public void resetValues() 
{
	getJTextFieldSeasonName().setText("");
	getJComboBoxStartMonth().setSelectedIndex(0);
	getJComboBoxStartDay().setSelectedIndex(0);
	getJComboBoxEndMonth().setSelectedIndex(0);
	getJComboBoxEndDay().setSelectedIndex(0);
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 5:15:35 PM)
 * @return int
 */
public void setDateOfSeason( com.cannontech.database.db.season.DateOfSeason sDate )
{
	getJTextFieldSeasonName().setText( sDate.getSeasonName() );

	getJComboBoxStartMonth().setSelectedIndex( sDate.getSeasonStartMonth().intValue() );
	
	getJComboBoxStartDay().setSelectedItem( sDate.getSeasonStartDay() );
	
	getJComboBoxEndMonth().setSelectedIndex( sDate.getSeasonEndMonth().intValue() );
	
	getJComboBoxEndDay().setSelectedItem( sDate.getSeasonEndDay() );

	
}
}

package com.cannontech.dbeditor.wizard.point;

/**
 * This type was created in VisualAge.
 */

public class PointTypePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
	private int pointType = com.cannontech.database.data.point.PointTypes.ANALOG_POINT;
	private javax.swing.JRadioButton ivjAnalogRadioButton = null;
	private javax.swing.ButtonGroup ivjButtonGroup = null;
	private javax.swing.JRadioButton ivjCalculatedRadioButton = null;
	private javax.swing.JLabel ivjSelectTypeLabel = null;
	private javax.swing.JRadioButton ivjStatusRadioButton = null;
	private javax.swing.JRadioButton ivjAccumulatorRadioButton = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == PointTypePanel.this.getAnalogRadioButton()) 
				connEtoC1(e);
			if (e.getSource() == PointTypePanel.this.getStatusRadioButton()) 
				connEtoC2(e);
			if (e.getSource() == PointTypePanel.this.getAccumulatorRadioButton()) 
				connEtoC3(e);
			if (e.getSource() == PointTypePanel.this.getCalculatedRadioButton()) 
				connEtoC4(e);
			if (e.getSource() == PointTypePanel.this.getJRadioButtonAnalogOutput()) 
				connEtoC5(e);
			if (e.getSource() == PointTypePanel.this.getJRadioButtonStatusOutput()) 
				connEtoC6(e);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JRadioButton ivjJRadioButtonAnalogOutput = null;
	private javax.swing.JRadioButton ivjJRadioButtonStatusOutput = null;
public PointTypePanel() {
	super();
	initialize();
}
/**
 * Comment
 */
public void accumulatorRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJRadioButtonAnalogOutput().setVisible(false);
	getJRadioButtonStatusOutput().setVisible(false);
	
	pointType = com.cannontech.database.data.point.PointTypes.PULSE_ACCUMULATOR_POINT;

	return;
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void actionPerformed(java.awt.event.ActionEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getAnalogRadioButton()) 
		connEtoC1(e);
	if (e.getSource() == getStatusRadioButton()) 
		connEtoC2(e);
	if (e.getSource() == getAccumulatorRadioButton()) 
		connEtoC3(e);
	if (e.getSource() == getCalculatedRadioButton()) 
		connEtoC4(e);
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void analogRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJRadioButtonAnalogOutput().setVisible(false);
	getJRadioButtonStatusOutput().setVisible(false);
	
	pointType = com.cannontech.database.data.point.PointTypes.ANALOG_POINT;

	return;
}
/**
 * Comment
 */
public void calculatedRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	pointType = com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;
	getJRadioButtonAnalogOutput().setVisible(true);
	getJRadioButtonAnalogOutput().setSelected(true);
	getJRadioButtonStatusOutput().setVisible(true);
	getJRadioButtonStatusOutput().setSelected(false);
	
	return;
}
/**
 * connEtoC1:  (AnalogRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.analogRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.analogRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (StatusRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.statusRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.statusRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (AccumulatorRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.accumulatorRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.accumulatorRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (CalculatedRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.calculatedRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.calculatedRadioButton_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JRadioButtonAnalogOutput.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.jRadioButtonAnalogOutput_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jRadioButtonAnalogOutput_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC6:  (JRadioButtonStatusOutput.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.jRadioButtonStatusOutput_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jRadioButtonStatusOutput_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM1:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getAccumulatorRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM2:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getStatusRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM3:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getCalculatedRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoM4:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
	try {
		// user code begin {1}
		// user code end
		getButtonGroup().add(getAnalogRadioButton());
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAccumulatorRadioButton() {
	if (ivjAccumulatorRadioButton == null) {
		try {
			ivjAccumulatorRadioButton = new javax.swing.JRadioButton();
			ivjAccumulatorRadioButton.setName("AccumulatorRadioButton");
			ivjAccumulatorRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAccumulatorRadioButton.setText("Accumulator");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAccumulatorRadioButton;
}
/**
 * Return the AnalogRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAnalogRadioButton() {
	if (ivjAnalogRadioButton == null) {
		try {
			ivjAnalogRadioButton = new javax.swing.JRadioButton();
			ivjAnalogRadioButton.setName("AnalogRadioButton");
			ivjAnalogRadioButton.setSelected(true);
			ivjAnalogRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjAnalogRadioButton.setText("Analog");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjAnalogRadioButton;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G94051EB1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBC8DD8D4E5566C77D951175B323B6E1716FDCB7E94A5DBD6561AE9D1598F06E91B2DD4D66CCAE51B95957BAD5B5AFAE7868895929515D20BD28B0B90504AA55B64CF2030D0494CA839B3F3E1C60619694E85866467BB677D39F3E7668EA3244F970F47FB5F731E771C731E773C677D3DA3643D911DE1B0668902A1C3E0FF9F99603F6F84612CF9311BB8EEDFF43CC1507C3D8930C638A9BB9D1E0D10E7
	DB96EF489566F6E5C15A8A69512971060D703ECE786B8F1B1B60A5041C544302706A4EFF9A0716735477DC4E4BA43D3CB19B1E2FG58D096C0E2B44A5FF8B01F6127C27A95760EE0G89D117A0CD1E102F41EF3E0452D961698558BD81E94A8EEEE6790900C31A9400748914F6B3BCD7906D4EAA5F43681AFF99A3244EB95F180332B47CB9743132516CD9A0ECB022A1CC42D91D7F02675E467636FFDBDC9D45965169F4B99549E2AB361AA52BDDF149455D76DED136963BDDF627D2DE5E080F269E3754A8BAA587
	144D1D08BA178BD9049F688CB246935CB5825A29895E9DG23A6F23D6F4D594B749A539EA3A4CD7CD9C83D13A6D2FD53A77974F6B48B1953A6A17FEF8FE633F255106EC39C11BB71A057A5ED9257A5410D34A513638DF5GAD13B97E9C8571DEC8C78144CC66BAFE728E577168495F8BE9662B0D2BC36898B7196A36E432CF47060499692F92DF69EE41765EF6A94885E88608G0886088358C4FC346C783B702CB0ABD52E0E8E1733426D2EF4D86583AE2B64007772F248E138C31245A5DB85017A6127C2110EBE
	9C92AF7577C184DA372F856EB731072E949465B2628B01F51BF6FF14F1A963A1DC662B1B6DB2345BFF9B50EE85700E655E403EC070D792FC2C86CF5BEABA7185CC27C2DECA8C377351D738AE8D6993045C52EB0DAF6B68B276569863524B19AFDC6153055A38590CB6AE81FC830095208FE0BC404CAB380D7B0E6F8AE763FA70FB59A9BA8EF71A5D222CD4F65A9DD6C9E636968F8EE5EBBD2065DC43AEC76D9233CB5BC60B2E3DC657AE0BE2AEB0A6B2FB24E86CC2E54704141DBBA558B7DED15BB0137409EDD3E8
	9BE62878B5843FC70327ED7B875237B05DGF9D381A87D71CE446745D27AADAA1FAE02BF2A41D3BEBFA77CB1BD9E72A2FF97EF206D7B22856321F2A574D1009800B800A400E5G39D771B63E73784ED36CC70D1A7625E5A3DAB640D372AA32D86F74A82253A29D91E53BE8F6C89E01511C5B02F6B57B15BB1F74EF8B4828151C12ACAAF61793380B9D92B8130752DC503286E9E40964B659BBA490506196B85F5F362C0327C374A8DF392D22A279A8E87EE5AD6893DD629160888C603D65AA34579192F3CB603D61
	AAEE2BF7A444CD0274CC55AEEF09EB6179B9B83764A8AF2F36DBD08DD16E61BEBA5E0031EEBE50AF81281B8AB686483A9A5A91E0A9007B1AF8C39AC04435710642E9710679G232E0BB76C399E7A72753CAFBFF3147BECF54E6F04227B2F5675D904185FD37F0B7BFD204FCEA33AAC81FC56F41085D087E08308840881388BE089C0568D108FD087E08308840839010F51B789A9A8E7AA55DF5757B5F82CBB56856B4EF1D85740B2D8E7ED9A6DB0D4DFC6FBEA53D99A19E8CFED1EBBA0DD9806B75ADDBF663CC46A
	0EED32BAC4599C4D3B09753917B53875BAE5B68524DF9DC2B7040C0074AA4D7B3A003CB581690DE1F83F2103237E7F1BD60CA1F7019FA483244F60FD40A69688A4B6D40A9E09C703AFE56848125557A3A82DE4C63BC9DD12D329165C12538AAF9E56979DE634AB0B932DAE0177E11960F15C28020E6CD171DE739A0A879E5A284373A3C83E28A8325D5C29C807A5C531BB5BFC65A309AE9E3FA149436AFAC19B56F5B354F1B7C035DA57B59634AF6F6F8DAED7FB273B89A3990B9BB7137951D120F74F607DA5DBC1
	5C98C8E3DF20380D8407B4D3D53AC47751B0ED2387481777332168280C67C6A44394426F74E5C81D982B8C2CDC86432FE0FE0A7DE3B573CB3465FA66132F32F621BC7A880F79D037B400029BA1FEG388146824481AC81481D8931852085A092E0AAC062CCEE4FDFABC9BFC20F615A687F8BE8DD6FA1F59DC9BD377C846AF02AC07BE03AB5F48CD9E5550FA1EB2C7AB1E423F568987106D5AFC6503C372DE8EBD737D393F35E3362FC4062F2BAA5927854A282DD93BE53047530BB6D4A57F6EB1B249C3677CAB09E
	4D425851C1669C823C674D6263FE77D6BE6EEF6B3DDC08BBBEDAF7BDB1F6729423E3966D7F9133FC63BE5529C842D8A6B9248EB0D4152B5329303591194FB03C47C4F011BB51E7AABB9545652C15DD1DEE016217FDF688F9967AAFD10B30A2F6518115A85CD1707E7B7FAE5014AC922D56420693E21768AD76F403D50BAB4C9EE8970BC209BE5EF1E4C70940781FFF6C0C70AF657C4F79720C70AF637CE72EB8A37CF7F17EC71BBB897F53AE21C8145BCAC9418AD81F395A8E09D63B0BE697A9B63BE747EE016B70
	6E5EB32E43E1C5D4BABDC1BAFC206AB0C56CBD63F630D8BABBBA9D226212039479106BB1B12767CC6BD1A5BAAC2806E48DD263A3559E0E7D63099E410DA71278D3CC9F0D125C6A12BBA4EBA1CAAF963B15E292BF0BABC8F68D3ED3F5832AA38672BDDE914030A1B4C72C4F9E2A6713F5D44F6086BEBDFAFA82798ED74FFFD1BDE561B2EACFFDA7B8CDC655E5BEDC0DCBCB188FBC6B19CCF456F718532332C50F7730B5AED57DF6DA76942273E79AF654073F6CD45C1D4AE9D27BC4887643563C0C699D792FE03D29
	CF1FB13D356C072D77AE361E19E343B9C0B44C8D2681A44C626B19ABA54C676B9936E82BF2B99CF47A82F905B7615C6485C31EE63E11BC0B6E4D67C19E4D2F4F50663BEF22792F2865BB4364DF92A6FFEE187C3F7B65CF8D4A5F1EB9F47E41B0797DE172AF774BDF9A147FED7650796704493FB6CC7E233A79FBEE666D5312932ABC75030BDAF3F11EAB7D50A9B9AD92AEE8BF17FA589E5B04563581F9C7C4C727EF6F6E0AD61CB3BAB4EBE10A3F38CDFF5EB9318D4FFC5AD57E1735E58A3887C7F952F972D491F9
	3AE5FB07A87760621E1D717C7120760C27816A0767C12361998DC04BFE1805F90107A92C9D942D9D26337278DE9D0779B3BA7979D0724DF48D3F6CE63AC64F0B5352A9DD1CAEAAEEA83A42EECE97B3A45DA215AEAE84DD49ADC89765D54F2F62681AB8851E826495G942BF45F77F03A3EDB689AFA8CBCA785519576F23A5910467576A2F8AE8D22FBDE254B3E152E473741F3772DBE3A34DB11EE66C9CE57F4AB554BF1AB35F16C2D349E14AE220F53D55FC67562F44D37E969F6772B763B0D6A4569627C681628
	F4F93329DE1C2EE83616EE7C00EA97400F8118B61B52257A51358CF23A3A5B638DAD377B68A2EE27F48BEEC73AD16A595CCE95F79141BD840B70F35379194301DC6DF92255B3C9F79D0710FF8765DFF4C760D94CF2BB72EDGFC739DBCFE3FE9536EC7D5F8BCAE0B1DEC0533BEB85F26DD63D15C7D361570EC354B9E259E964FDE5E0F9F32212E38D74E502CAFBE3961ADA1F86F3F061CE02A87BC969CAC6899613BC61409FB906EBB78BA36FF3F76DCF2666AF146D5A16A9F436AFD17267E3989B35229AE0D35FA
	E7CD7E3AD4F513712FCADD049F31CB5D2CAE035E62D3A89FD014151D725EA727AA3B49D56532CAF5909283D9B19BED4861FE13F5A70FDB634A31EEFA674596A230382D53DE5C29587120C1D444FAA7CC88E8F023BC779D62F65FF7A7BF03FDEA101C41C2BA8AA0C61575D27196AECB339EA9AF572E7C795951E2F226043A46C179B4GBA977A2C8B715A858F65DD62C4FF893F5661FD63F72F6FE3ED1FF06584A1CB3CC477BCF874137FE9AC390B7A44513BFC3EB1262F519483C0F53D380B6B9A8DB409G94FF30
	1B63F363A1EE46F3FC061763A3GB7DD455F2062D3GD7B8075739208747AEDC2351716435DE6DF852B207466E51AAFD9D2147C59B2DEB5FB73C2E918953047CF51707BC7B0E1E437A419CDFDD478EB41AE287F8DD4F6961BA665F9DEFE83C1B4BFCA6C04787BE5753B172EE55B63D1C4FF840ADD079CC6F7567133AD70FCFF682673369A467D3G3823891C4FFF1C746793DF2D47A7CA657358C9FE66B989F00BD4BE8DFDE548E75893EC5EFF4794206DFF6C0A16366304F5DA1F5DBB582B5B0E112BC799736652
	765BBB57570E56451727FB81E8FDAF6F63750D841A38393C0FD6FE0FB2FE6C4A164A782E1F4BC8837EA577F0990505B5442627E1C12A460BB583DC168D64C42B3266B5D74373F42CA4291C5BD5B909A0A3F79E1F6BFA5B757C2BEE9E772322419F67C75BABEA61F91A16164CE6678E723AB4031EE3543A8832DEDD9266F11B6EBE90284B0FD8AE72BBACE55A39CADC525810F31524F95427D367854ED5FE654031A8F7BE4C936773394A91BB6E3573394AA19895C5E79B2C9654FD7D9358BE30AEE0692C93A8172C
	8B84FE4E315398F86FE24FFC5F39D703663D053DD3FD66F5209D9540C58144G4481A4293A7D62C458BBACB55EA16E30EC4A994EDD9461DEFEC6D5F308174B3B172D676E7555E12F661D833D6761A2F7FE9A6F2577BCD62B78C10277EA70543F1EA37E0569E8485B7C87AE7F494EF0FA0F7D033F8E345C5F42161B99D00E3643F6974E2B97C0DE8AC056FD609FGF5GD600487BF8FBACB971F158762093AD6A42D501A7AECC4F09DDA32B5F55E14B455C27D76E6B6E11491342160B5315374ABBB2F92F06AD1728
	AB6F66914A1B9B36DCCA00BC9A5FB6D43EAE684DD3357BEFFCCEFD3097E38E46302C857C4E4EE4B77A5BBEC8B783A8GD1G53G668324AE607ED65EDE994E5F542BC760F37C3CB0586DD8FDFF5EB3B2BBDD9436DC767D7E65E85DF39B785CFF5B7D3CEFFF5DBBC2DF8CDB2ED9D70777BE61BAA82A8EEF1E9C198EBB43160B5555C12861BEB0DB556129A0DE4B824A6A6B707720F22F8724737F289763DE135167CAA02F81408A9081B089A08EA0790F5C67FE5E2E371E540BF1410BC95FDD972A6BD5FDA32B632C
	20F201F5CCDF28D74E52BFB2F9BF84158B1437D9D75E460111495B9ED4AED05EEEDDF9F70FD05E83E165B5AC54735B2B5AF95F31AE64FE7B0B411169F0F1D039C09D466B6A70114CF5183610474FE39E44AD057432055CEF7F520EF7CA78BC0E6C338AFC8EF7018377F2DD323D4D6E64387184E7F1496422843DA7351F6C91E5A7469B0AGA86E3BF444B5C25A2662DE4BC05CE8C8C7A7F2798BBBC25F7FF824C37F7EC7F2077E7D0F3FF48CFD076C790ED077C328FEE5E49FE7BA6836C05539196C43AC01F47A83
	9C77D1BF6272A1DD2462268F707AB6BE405B3CD3A077F1A19D85B095E0BEC0AAC07E0371063D8FF23A3388DD4B03F40FF5925B3345FDCF5C53447DCAB6CF9BF48734BD8272656F48B30A49C199C345DC8E4A8341F4299A9C153DC150132D3D8B096D0863BC36CD4EA948C5B0696034779F37901AA71475024EBD1CAFE084977899E4C1578AC6D323388E142AED3E53955A3FBEF4697B57DE976A6FF1F54A96A950072ADC79EA1EAF0257ABD2872AC3E99AC8F9C5145B24A09FBCCC4A533CD0651BDD217C145675C1
	3F3A162978733B3578DDAA3E522B457B6E007C2FF79543B7364A2E0E46D25656C7481CA102794F98760CE14F19BAFEC5753E4A9D5A56573B75EDBD539D5A563739435BBA419D5A567739435BFA119BEDE036DB6DB2BBA52536195D63EF1BB2E61B13873471F8BE33C5B2FB2669580635CD2FBF3FDD0CDF49A7DAFE598F3135B7FB6EFEA8943F23A77D796DE67CB2EA347C6AD879E676F443F3D4GCF5A4762B7E05B39A11CCA9EB2D141DDE9A66B2DBE0D7FD4797A56B6B9F4FB3FA76B37F7299C3A3D7748615B3B
	C28E5D5EF5F27876BEA88F5D379E772B2B2FEFDD582F457B7A569783DA3C2FEF998735F83C73446E6D37EB5BE7ACEB0BD8760C8B595EF778717BD06537D75672DB404AAFE14FE5BA7C289D0EFBF0CF412B26ADA40D772068DA4D55026744ED32683659AD1EEA49E3116DEE4585469E33080C6FC634F5AE3C6F813081C4AE724922BC2E6EC69FED051596187830DB3288A58F137356F4FE6E3B0815C9E14F5C0779DE58D53B7069904D645BB9EA03520C407B0C14D62AF8C5ED779A2715CDE26879DDBD99530F8361
	47864E47F388FD48ADCB2D12ACCBD6FAEDD318CAF49BCD46FBAF3C0FFA186FB1FE511FC9F469F1F5825F3AC7106E36FE6D59F5AA50EE86FC49A36CFC17701AB740E922GBF89E0B6C0B2C07A236A7EFE99762143925E5140836E86D2E31AF7CDB37AEA50EDB0559EADE41C883A052566EF2E44EFF574EFBFF11AAB1CB8366A5EB6D279FCE547FA043A5DC3E9D6FD98C8235D3B247B1FB3856D7E6736C759DE6E23FC8E55C1567C4D08D7F157935CA8C80FD1F16FF4A3AE865253D5DCE4B71FFF4DD6F13FBA404B66
	A5F15C2EE33CEC210A9BF30C175D2D62EABF61E5C729380BCAF95931AAEEF18DAFBBC945ED2E61E5D3D55C0CDADEB66BCF9C37290E174DD3F1DB485A418AE92F0A3B1B60C6C3FA3C0ADBCD6620D3A19D276214FE4EAFC1459D16F9594DFF6638BB87F8599295D7B8484B6E7DB377CDAF597F559E7BE97ADEA946A05BE2E2BF63B601FE830C3002817A66E860158FB4783EE7B1673BAF9D7D20CBB43759DBBD4DD629555D59DB7AE1B124E87EB1BCE2E27B575F2E64E74D2397DDA664CDEA89798DEC6CE2766D44E2
	5F7E75785409A63A9E3A102CC730DCD6F23CE1DFB2626ECA577AA36A3883209800663F68170F31A647C47329EEF7EF673AAD3BFB2A6038AEBA24EE1149AC4E33E7742E2DC24238AB52131371BB6AC5F88D167535227E82F8BA256E82F5F73F5A5EA1B971033642DD9F1494951415146C2072233B35FB7BFBD67ECF483D7D4247583995FBE6CBDBD96CACED4135073AA707824B4BEBE53B35D2ECEBA0910F5A6F49FEEEBF65B1FE9E3D111C5944C2FAB6402247787DG6F5B5CAE3117CE909AA22E5135CBD253B8E3
	AA53A77FF1DF1B35A44CC827B27FBA40E5B6BD4E5BEC6B2036CD50E6CF9834ED76556050EDFA0EDFBE6ABB4B284D3FE1B0304DF9F9EA2F97DA73855DBDCC9238295E0B8CFC6DBD7AF16E7323C89C7310B8AFD969D8F3D1AF3FB7B0DB25DBEDC467EFF5B995CA73A821D9G79C90F7B4ECE49B9B4CC2E287FDC5707D6AFF98269FF5BDD2D537EDAFBE530A7B9BF47B2G6B191D37BDC14F9A90287C9BC98C7582FE94G1517586F13B765D8B8F9AF8FA1EFDA10BCB7093BF100CFD065E58D7A643DD893CE5E0AA164
	AD8B12D7C3E2F556920841CB383C77841F3C323AF072D68DA12FFA09CF9EED3B5D6DE88F4909D76B2CCC074F5B399FB4AE61ED7C67F6FFBFC8A3B4AD106FD0E95E6E777703AF8CBE3D4F6B8F27F74E90FA0F8799187F5272654B297CBEB20E44GFE1AEA276B0DBEF91F8E061337FA88F94940F39D13C7FF0F629AE384BC4FEEED05FF36C84B382808F163B6C41AAB33A2CDD51911162A2CC8E3E5E624393AC058DDA5E4D92AAB4B2AABD79418EB3AB77ED0DDDDE2296D71F4676DAEB057F6F7F72FB517D876EF6C
	7E613010E92EBC38E6FDE5264DD23D7D2BED4047DC06FF1911A2E9718D11A02C9565713FB1CFE25D1EB072181082FA62985777A40DD7F47C0BB0E947BF6E038809C0376DAF3EF9B10D0FEF64E86336639E4538CE500F5B552C2C421EFDFB3732FD697BCC9807E2GBF9DA081A099A08DA06FA93E37763887E3D7053B7510C497D587C42758C6EE0F1517833A3E83D25A29BDDBD37CB6FDE45F625F72B20F7B53E6DCA1CCBF3BA5643D301227E8FD9A59F3CF59D60227F593B330EE9110B79EA08EA091E0A9C07AD2
	DE3777955491F7552DF625C2D1C40B0D5C0A631B6DF8C14B0746458755F15E37DA5B67DE98E9B40450B1FFA955ED5B5240737078CC72DDA86095D51FB7CFE254B8A21E883EC98648C6596516E42507E2490DDBE66BE70276504381BD9BBE0F0C3DE3167A628B450FA178E88D1E1E99EF99603F81928BF939CF2B3F9970B2D9B3C3BA8AA086A08EA089E059533CEEA7CE1688E14EB61ADC6E8629CBF2E836DDE97DDE8C5AC38F87345C1F8C5AF2394F3036E04FBD4F04DAA7DFC36EB9A9F227C4E3149543984B5B54
	0DF7597C66D67BA140963EE53563F2662D228218DE156E5EC13F1B46BEC93E61E3FF14DF820AE27E7E27EC6E6F1B27CC942A9736983706702586266F78E7B4F75D0E8B19146734F5BE9D5FB9F62ABA52322507FC3A0DED727179C56970785C78150F4F4623BEBED7568E0FCF79B73E3266F67F36D8576C4BB3751F8A5FB2156F075FE974EB717139F1E0F8FC4AC92827E30C4194A1787FC5C4601DED7C9304CBAF1DB3A7A5C59016AF7F47BF70998DFF13A6A18D7ED17A654BFD744B491FB06CBF5ABF2BCC3CFFAE
	G9F49FE164F2525BE9E2FCA3E0996CA469D8F99D32B1F253EE5FD56B7D650FA7E0FE99FBC7B53E0FCDDA688E98C167F136A9F8408DF660341B42AA79D8BEFB37131B06AD93AD7937B2C76EE4E5BE69C8BE783AE91E0A9001082EBC0009200FDGCDG6E949EDB520C0570240125166DB61517D735D5CBDDF60B04F7175946A2992C581E2D897D4A296E69A51934F70BA86EB1935E0B165940D7EF657827CC64DC4E61A027879417E242A0E1F7FAF01925583B88B94B7B1BA957BF0F84EFDAD72785EDB19A6A9283
	10G1084102A566D1CF4F657DAE7FC2A3136619EA77E020A42E51D48883D1F7AC3C6785FDD694D40BA1BDD30C66FC07E6AEFA7E410F3FC171BA0A96E2C8C74AF595EE6D3A8A9431FC74E689CD2ABC153FA1E0766E0791BB2378BA146B92855E750873FB41F403DAC31CDADF3CFA67A073A67CA07BF16F7BF7956C63DF7CC2E3B9F86C2E79B9D87FEB90032697B051A772D83A3796D03610DC1FB4F288CDAFFD3D668B6DF19253F071E1B25FF7E39A1EB686F5FDF4F8A7D7D7B76AC6D776FF4CFA84D407731F21F63
	7BC49F7471FD2CEDAA6E42BE3EF7D4729C1FD7DCAF61FAA08252638162G9281168224BF8F71G2088E09F40D1GB740A80031G31GB3816683A4BF2F7ED6476A9D217C8E10EC4302F4A8D924E758348D6BCDA31BFFFCBD6CF2345E1F3621BF27015E59AFC09D817682B43D406B51AB9688FA710EFC25A1D99BC587341AC4637643A47670BFBE3E507142144E478B87701E2472AFB3E3BBF65B2D0ACDED770F4C38FFED1330332BE71C048E222B062E420CE30B47A2BA9DF62754A8DA993E560C324CBD0A64E914
	648648237883E60C43D6D19159590A19FCA7C22B404A9EB1E39D20D11C9EF6B0EF77CA8E1A77DDC01E25670068E927F9DD16507D225F92BE961EEDA15F4AC08C43DDF5965BAC64BEC3EB2BC7D26D90E549D5F1751EC69786F917132EF93713FB8D0B4036CBDE60EB5E84B497DB73DE5D97EE4D2B0540B5EFFE2AEF0ECEF519A3052E733DD278BABFA869477FA4A9B87EBFA669477F27A4ED7CE76323043E5286FD6C28CA77D795770D0AFB1170C45C57FE7EA7342EE478BABF9F4CECC53D3A896EA87F4D8F8D6E9B
	9E6FDA315D204AB6DC24D25C7AD632BFE2F7B0DFFA155060B82D2876F83D95F5343BC50D5EDBD51CCF6FDA724D22CDF4DA9D2A4D9A5A56B21FBFA439E19A81CDC1BF45E279CD643BC70F3D175968E81B7EF7135F136FA6DD1EC33515B5CE884C120746179543DEE3333BD343AEC77D3630DD90781CF9C75F7066741F63919C1BCB1E3BE2AEA8927B34106111053541FB549567BF30B076F8F51ED1B0568B19B466548B99A4A0F9718A49DD60715FABC2A6BDC20297FACE24882BB487D50A3052F7A42588B9143800
	9EA6C11A16E1E9D0A4472FA237DF724163890F9C7118044CFAF08C409C16E47AD913950F35596F436D97D6105F6A44733FFAA15DC39E999E2547A1B588F92D07709BD98F48D7AF0802126B49257B1D7861564EBA51E3D33029BFEE9052F1B62A88EBE936C3F4366D2433180FC9258D95ADE8C2529E82683B0E680BDC0AD5AE6BEEDC3D7D4D4F12271B8463FEE12543A5DA770B9630C53D30C23101875ADC8E2B577D69BE42A80B9CF7112DC7E037C92DBED636388942042AF43B776251B9CBF2769B8541AB8BBB89
	A5EE99962B9456AA1B085FF2C9F23D023F4C67123729F9BFFBDDF9FBEE6D46E623E0E0157CC19890D6FA5D50F02DE2075D5183AF54EDBC18730A825AE2C047EF94C03302G4DF816AA20785771A53F397C45E3C4002F28A23CC1CD89C5F736C92E0E1D952D2DB070288C402E40FE0B4A9EE90A83E856FAFF397E337B171B418F2A85D3C7C989793F147CDF067F1792CCA941141601F1D304A17F82733D3C4E44F9C01F55C49FEC7E627D50024A03CF47CFF8E8EF4AD2285DFEA1032CD02091F2FDD4DFF4F618A579
	6C585FECD9B5A669BCECA3B73A0A97792E752A9C310675A436A5F56DAAB7AD1EE8B28A69503FE82C876745008D4EAB536EE602D8CF7C0EA889F08971BBE41A95640D084D8EF6ABC4672A0D0B29F504F4BD79BFFF582642D22F11321B559AEE2586FCC32369641F26266F86EBBA78535434B4D8537F8A50146A15FDAA1AD61F81CD7731749E1F26B8873AC840DF5C05771F1E261F7AB4FDD32B29A23CCAEFEE94A8B629C00109E6012B35007EG74CF4FB3EA7DBD839775B86F437D9A3A5734E3D03BB71EF656E048
	736458E56C3E5232403D716B48FD1BE5004FFF092FC9B2F25E97C26F999C6E909D8EC5F434D7B82D38FFD0096B8C014F33E664A0FF8F27E119B46F169C547B04264C7F81D0CB8788EA9EE2BC4A9DGGA8DDGGD0CB818294G94G88G88G94051EB1EA9EE2BC4A9DGGA8DDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG849EGGGG
**end of data**/
}
/**
 * Return the ButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup() {
	if (ivjButtonGroup == null) {
		try {
			ivjButtonGroup = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup;
}
/**
 * Return the CalculatedRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getCalculatedRadioButton() {
	if (ivjCalculatedRadioButton == null) {
		try {
			ivjCalculatedRadioButton = new javax.swing.JRadioButton();
			ivjCalculatedRadioButton.setName("CalculatedRadioButton");
			ivjCalculatedRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjCalculatedRadioButton.setText("Calculated");
			ivjCalculatedRadioButton.setEnabled(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCalculatedRadioButton;
}
/**
 * Return the JRadioButtonAnalogOutput property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonAnalogOutput() {
	if (ivjJRadioButtonAnalogOutput == null) {
		try {
			ivjJRadioButtonAnalogOutput = new javax.swing.JRadioButton();
			ivjJRadioButtonAnalogOutput.setName("JRadioButtonAnalogOutput");
			ivjJRadioButtonAnalogOutput.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJRadioButtonAnalogOutput.setText("Analog Output");
			// user code begin {1}
			ivjJRadioButtonAnalogOutput.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonAnalogOutput;
}
/**
 * Return the JRadioButtonStatusOutput property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonStatusOutput() {
	if (ivjJRadioButtonStatusOutput == null) {
		try {
			ivjJRadioButtonStatusOutput = new javax.swing.JRadioButton();
			ivjJRadioButtonStatusOutput.setName("JRadioButtonStatusOutput");
			ivjJRadioButtonStatusOutput.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJRadioButtonStatusOutput.setText("Status Output");
			// user code begin {1}
			ivjJRadioButtonStatusOutput.setVisible(false);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJRadioButtonStatusOutput;
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @return int
 */
public int getPointType()
{
	return pointType;
}
/**
 * Return the SelectTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectTypeLabel() {
	if (ivjSelectTypeLabel == null) {
		try {
			ivjSelectTypeLabel = new javax.swing.JLabel();
			ivjSelectTypeLabel.setName("SelectTypeLabel");
			ivjSelectTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjSelectTypeLabel.setText("Select the type of point:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSelectTypeLabel;
}
/**
 * Return the StatusRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getStatusRadioButton() {
	if (ivjStatusRadioButton == null) {
		try {
			ivjStatusRadioButton = new javax.swing.JRadioButton();
			ivjStatusRadioButton.setName("StatusRadioButton");
			ivjStatusRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
			ivjStatusRadioButton.setText("Status");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStatusRadioButton;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Ignore val - we're determing the type of the DBPersistent
	//Figure out the type, create it, and then return it

	int type = getPointType();
	
	com.cannontech.database.data.point.PointBase newPoint = com.cannontech.database.data.point.PointFactory.createPoint( type );

	newPoint.getPoint().setAlarmInhibit( com.cannontech.common.util.CtiUtilities.getFalseCharacter() );
	newPoint.getPoint().setServiceFlag( com.cannontech.common.util.CtiUtilities.getFalseCharacter() );
	newPoint.getPoint().setArchiveType("None");
	newPoint.getPoint().setArchiveInterval(new Integer(0));

	return newPoint;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getAnalogRadioButton().addActionListener(ivjEventHandler);
	getStatusRadioButton().addActionListener(ivjEventHandler);
	getAccumulatorRadioButton().addActionListener(ivjEventHandler);
	getCalculatedRadioButton().addActionListener(ivjEventHandler);
	getJRadioButtonAnalogOutput().addActionListener(ivjEventHandler);
	getJRadioButtonStatusOutput().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointTypePanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 243);

		java.awt.GridBagConstraints constraintsSelectTypeLabel = new java.awt.GridBagConstraints();
		constraintsSelectTypeLabel.gridx = 1; constraintsSelectTypeLabel.gridy = 1;
		constraintsSelectTypeLabel.insets = new java.awt.Insets(36, 100, 0, 101);
		add(getSelectTypeLabel(), constraintsSelectTypeLabel);

		java.awt.GridBagConstraints constraintsAnalogRadioButton = new java.awt.GridBagConstraints();
		constraintsAnalogRadioButton.gridx = 1; constraintsAnalogRadioButton.gridy = 2;
		constraintsAnalogRadioButton.insets = new java.awt.Insets(0, 120, 0, 161);
		add(getAnalogRadioButton(), constraintsAnalogRadioButton);

		java.awt.GridBagConstraints constraintsStatusRadioButton = new java.awt.GridBagConstraints();
		constraintsStatusRadioButton.gridx = 1; constraintsStatusRadioButton.gridy = 3;
		constraintsStatusRadioButton.insets = new java.awt.Insets(0, 120, 0, 165);
		add(getStatusRadioButton(), constraintsStatusRadioButton);

		java.awt.GridBagConstraints constraintsAccumulatorRadioButton = new java.awt.GridBagConstraints();
		constraintsAccumulatorRadioButton.gridx = 1; constraintsAccumulatorRadioButton.gridy = 4;
		constraintsAccumulatorRadioButton.insets = new java.awt.Insets(0, 120, 0, 127);
		add(getAccumulatorRadioButton(), constraintsAccumulatorRadioButton);

		java.awt.GridBagConstraints constraintsCalculatedRadioButton = new java.awt.GridBagConstraints();
		constraintsCalculatedRadioButton.gridx = 1; constraintsCalculatedRadioButton.gridy = 5;
		constraintsCalculatedRadioButton.insets = new java.awt.Insets(0, 120, 0, 138);
		add(getCalculatedRadioButton(), constraintsCalculatedRadioButton);

		java.awt.GridBagConstraints constraintsJRadioButtonAnalogOutput = new java.awt.GridBagConstraints();
		constraintsJRadioButtonAnalogOutput.gridx = 1; constraintsJRadioButtonAnalogOutput.gridy = 6;
		constraintsJRadioButtonAnalogOutput.ipadx = 6;
		constraintsJRadioButtonAnalogOutput.ipady = -2;
		constraintsJRadioButtonAnalogOutput.insets = new java.awt.Insets(1, 155, 1, 87);
		add(getJRadioButtonAnalogOutput(), constraintsJRadioButtonAnalogOutput);

		java.awt.GridBagConstraints constraintsJRadioButtonStatusOutput = new java.awt.GridBagConstraints();
		constraintsJRadioButtonStatusOutput.gridx = 1; constraintsJRadioButtonStatusOutput.gridy = 7;
		constraintsJRadioButtonStatusOutput.ipadx = 9;
		constraintsJRadioButtonStatusOutput.ipady = -2;
		constraintsJRadioButtonStatusOutput.insets = new java.awt.Insets(1, 155, 33, 87);
		add(getJRadioButtonStatusOutput(), constraintsJRadioButtonStatusOutput);
		initConnections();
		connEtoM1();
		connEtoM2();
		connEtoM3();
		connEtoM4();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Comment
 */
public void jRadioButtonAnalogOutput_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	pointType = com.cannontech.database.data.point.PointTypes.CALCULATED_POINT;
	getJRadioButtonStatusOutput().setSelected(false);
	
	return;
}
/**
 * Comment
 */
public void jRadioButtonStatusOutput_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
	
	pointType = com.cannontech.database.data.point.PointTypes.CALCULATED_STATUS_POINT;
	getJRadioButtonAnalogOutput().setSelected(false);
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame = new java.awt.Frame();
		PointTypePanel aPointTypePanel;
		aPointTypePanel = new PointTypePanel();
		frame.add("Center", aPointTypePanel);
		frame.setSize(aPointTypePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @return int
 */
public void setPointType( int ptType )
{
	if( com.cannontech.database.data.point.PointTypes.isValidPointType(ptType) )
	{
		pointType = ptType;
 	}
 	else
 		throw new Error(getClass() + "::setPointType() - Invalid point type specified (" + ptType +")" );
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * Comment
 */
public void statusRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJRadioButtonAnalogOutput().setVisible(false);
	getJRadioButtonStatusOutput().setVisible(false);
 	
 	pointType = com.cannontech.database.data.point.PointTypes.STATUS_POINT;

	return;
}
}

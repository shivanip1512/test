package com.cannontech.dbeditor.wizard.device.lmprogram;

/**
 * This type was created in VisualAge.
 */

import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.database.data.device.lm.LMProgramBase;
import com.cannontech.common.editor.PropertyPanelEvent;
import com.cannontech.database.data.lite.LiteComparators;
import java.util.Collections;

public class LMProgramBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JLabel ivjJLabelName = null;
	//This is for the timed operational state
	private boolean isAWizardOp = false;
	//Control Methods
	public static final String TIME_REFRESH_CONTROL = "TimeRefresh";
	public static final String SMART_CYCLE_CONTROL = "SmartCycle";
	public static final String MASTER_CYCLE_CONTROL = "MasterCycle";
	public static final String ROTATION_CONTROL = "Rotation";
	public static final String LATCHING_CONTROL = "Latching";
	// Stop Types
	public static final String RESTORE_STOP = "Restore";
	public static final String TIME_IN_STOP = "Time-In";
	private javax.swing.JComboBox ivjJComboBoxOperationalState = null;
	private javax.swing.JLabel ivjJLabelOperationalState = null;
	private javax.swing.JTextField ivjJTextFieldName = null;
	private javax.swing.JLabel ivjJLabelActualProgType = null;
	private javax.swing.JLabel ivjJLabelProgramType = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JComboBox ivjJComboBoxConstraint = null;
	private javax.swing.JLabel ivjJLabelConstraint = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMProgramBasePanel.this.getJComboBoxOperationalState()) 
				connEtoC1(e);
			if (e.getSource() == LMProgramBasePanel.this.getJComboBoxConstraint()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMProgramBasePanel.this.getJTextFieldName()) 
				connEtoC2(e);
		};
	};
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public LMProgramBasePanel() {
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
	if (e.getSource() == getJComboBoxOperationalState()) 
		connEtoC1(e);
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
	if (e.getSource() == getJTextFieldName()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JComboBoxOperationalState.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		if(!isAWizardOp)
		{
			/*java.awt.Frame owner = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
   
			//This makes sure that the user applies the Timed state before going to the control window tab
			StringBuffer message = new StringBuffer("You have selected a Timed operational state.  Please click \n" + 
													 "the Apply button before specifying your control times or \n" + 
													 "the control window panel will not reflect the current operational state");
			int optional = 
						 javax.swing.JOptionPane.showConfirmDialog(
								 this, 
								 message,
							  "Changes should be applied.",
							  JOptionPane.OK_OPTION,
							  JOptionPane.WARNING_MESSAGE);*/
							  
				/*fireInputDataPanelEvent( new PropertyPanelEvent(
										this, 
										PropertyPanelEvent.EVENT_FORCE_APPLY));*/
		}
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
 * connEtoC2:  (JTextFieldName.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMProgramBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JComboBoxConstraint.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
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
 * connEtoC3:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxFallAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.util.EventObject arg1) {
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
 * connEtoC4:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSpringAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.util.EventObject arg1) {
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
 * connEtoC5:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxSummerAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.util.EventObject arg1) {
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
 * connEtoC6:  (JCheckBoxSeasonChooser.JCheckBoxSeasonChooser.JCheckBoxWinterAction_actionPerformed(java.util.EventObject) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.util.EventObject
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.util.EventObject arg1) {
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
 * connEtoC8:  (JComboBoxHoliday.action.actionPerformed(java.awt.event.ActionEvent) --> LMProgramBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC8(java.awt.event.ActionEvent arg1) {
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
	D0CB838494G88G88G11E5AAB1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF4D45519D1D1D341AE9E282287AD455422C68F35F4450A2758CDF7C569CA2DA8DDF3142EF4CBCF234D59F276303B74F0B3B301A4A4A6A1C1638AB204E0938D1384E89BA3D6C2B0A698A50588C1D1664D3CB713173C19B7F973C2A6084DFE77773D193CC904531ED34E79725E7D6EF73F1FFB3F7B5D6F3BEFC055BB679596382A91AAA8C46C5F9F8B904AEBC36832871F6964385E53A59EE47B378B
	2092AD9A46C8BF744933CABC95A86BEC9134CDE8EBB3CABCAF41FB953A7DEC5EBEF8C9A3A7877A86DD8FEC1FDC4E9BE7391C4D24BDF33C941EAFGA4GB797E06DE9ACFF41F88D43AF05768B6C9DC3018B216C0FB1CDE0F70D8D5F78B1254B0067EC005ECFB04DBF0DEFE77D6BG07E9CA816F7D045236423318E8F7E5DBAB23BBF35D82346E618FDDE5D8160DBF07B9EC6C92767422A748C438514A9DDD70BC58BB947ED3D00F7802D2B42AC7CDA5B86013830A2C1A3A619BD54FCB066C1315F3EAD071E9111821
	078DA952565673C1AFFDBDAC4515DEA92AE80DE7307E193B0B88EF509F64ADA6B8D40767AC866F19G734EF09BFEDCF61069B8FBE881DAFDEBFBDA9BB61CE13AFFEA59107B1D0F5CABBE457C73481A60F1FEE84F81203A9CC0DC176A4F38AEB76C4434BD501681AE887CE7DE0C1F8D6DEC001C4F380EEF7F0E6B587F793DE8FDE058D51EC6475C4F28EE1E33160EB56ED92E17095F5C6B47EBFF887AE4008440DC00A5G39G2F90FFB54E3E8E4F76005921C7A2FA34BD96BB2C4946873A2CE8705E5686BD8CF7DC
	896A060C907549EBF7B7BA6843E15D59E4FF44C0EDBB05388FE59F3F9B19E658D568E05B22153D2EA2BF65315DEF5956674773E61235BD886FF89C560762B388FE0E8DCF57EA93416376C2682B1678BC773F40F569714CC795ADF157E687DD667CE0401510288EB3A5CB97BA475785709CAF817CAA00FC0022C009279E20AB4067F849597DD34DF1B76C81A3AAE9A7C682B149B08F0F281A2C98EC2E7FF3FC3239F682BA8E767BC50E237332E03FFD0DF23FE3B84ECB6E02D3AEB9C06763C2401A97AA7B4D343267
	86A67A468BE28D3710BD31B0C05750AD705B88FE298DCF5776E63237C89B7A8E84CBBC147EB1C2BFA7186AA3CFD37E41D49F113D5CC79E023EC63944C35737C94631F1A6343381F2GF281B6G54A8C02770B55E6CEF7A127B2857363EF46CD27F0B70D49226A1F5C76326948DAAA7A5C315821A92C70CE6199F6B96C89A772FE4FF87C146FBCAD4B1A4D3552340DD0AA860CCF1CA73249F7BC150D0C8EF1F9AD1C0C0A406B85FE77CD57054243879D1CC16CC4522207D3F70E31FB8A71D840E40G2F35C24EBE92
	F3FB60FDC061F3F5C641382550DEA566A5A0ED05677B605C0A5656F6C48DE2B5A4E30C7B68F932B773005E8C15F872C2FCEFBE5F4FFD70C859DD28F16530238FE25042541F2605D3FD70AA42BB9B704B8172427CBCAD2D6B1FE29FE0FD6CED2E5B175FFBFB261461CEE9973231DFC6861D6F07C3583E528108B7GDD83FC4EEF163C549771B9493D6F18810E2348568AD05A1B8278ECD14EA9D1730892D322B23C44595AFFA3D0EC79E5C897BE73AD0247F12A839CA7AE70F785AAA99EF8583D1C77FF1774CB26E9
	280191D3B92118269A8DDB639FA03A4413C2E01C593ABC0CED75030D8900CCE1EBE7304A66DFBD3ABEB4926B43BB0779E9AFB90F67827D22813E7E15DE1CDF2D926DD7C85B6A3FF567D4EB6784185F17770FA517A4A355E7A617314A4BF394374D3FAA519A02DF8F7AFAD5709B155BBD686DCF213FD87D2656698AA2432E4FDFD3867509B76574BEFDC8F67669CE595927FB6449FD36CFF672D95A77090CED54C72336BD6017F1BC8C6A5128C2B6229802E8FE7CFC9F36C30D2A66A9D58EAB6689753C8267180AFD
	79FE92F3E370BECD65F1F2F4B70F13F567EFC7CB4F76B866D3F33EB9644AD529FF662BD61C24BAC98A7687C5D3A2B0D19D7AC8542478B009678C9FF793DCD74DF9BC367EA81CF3817D301EB896E3A78FA4AFA69CA00D92314BC79F3D0A91520D08A2578FCA67A41FB4EA7A483A785AC9773B783D413BF76F1F3C8E49FDC8B5146EE8EC049DCD758DA79B1A296FD4212AF7B42AC79F2CE5272AE87207F06EB586A17818BE72178D42F2933E78A82C9A935D01FBAFDAF28B153B3558CC363FC307A35B105428793735
	FC9F0D039B8770DAD58C16F86AG8E8E72B8782C027BF99CE4413EC357B46AE650B7EF907B50FDF555B63F689B2475648B227F67D374D76EC957CF75D8912A407BC1999ED1A00D410178FDE50C67A2212DA9FDA7A5ED444AF11E89ED0127E60B61947FD85859BFD705F15C9E927CF3431B904ED5A8CF3A1F96CA18E74CD0A3F06461C3095545E1E42F0B57029D38062E9F02B98520EB7D4D8BD85F094B83794190667152AEDEB777C1DB8318B1C479AC9F22F3C2691E92F42D9A545CGB14D096E16DACE178D7DB9
	GEBD2686AA2A4379575EE13407D1260FE8D8175E68F4F63BF2830576BF2DCF60C8D9886A2AC0F0F2456B78786497989786C887735138376B337BD9E5703AA8981EC3D8AD5FB5C20389235841E6057F128F5E4A541573CC2453A627C1321593A5D79465BE8E2BE7DAE39A160A9CD47A859C5FDFA872440DD60B69DA22C1ED415D14467610BCEFB4D3F247C34EBCB1AF9D84A6CDFED1B07DC57AC97556946B6DC47B95DDD8409123E70086A9BB1D51C68CA26C4B6B0F598FAB770E3120BE0DE5EA83F83782D9B7BF6
	BF34E3G19G349EB9342B14514E835CAAG3A7FFE5FEA5FFF95BA4DEBDAF56E335F5A052F1FAEBE9271F94FE958CA57A9A57E70391CDFF50849DEFE77FCF4F03C4D71EEA2337C082BC727F3E86A56DC6657DC6E723CFC390B5A682965B6AE851A75BA37F16ED13B0D4DB1EAE3FF0C5BB8D40B7D7EA2C3BD2B737EC5485400DFD60C4B3C7114533C668A193E5D17BA2F6C5EE31FFDAFAEDD5D1FF6AF6647581D4EF06ADE2C51307FF881FFE0186F4569EA25EDAF9E876F1722E1081CA2979AF45398495AEF8F9672
	9809F89D71D1845B5E85BCE5008BG3381968999B903D35EDF3C1B18647E620989F7810F8FDB39725A6109F9B32D552F7560355AB0CCEB75CA013F05606B8D8BCF5756CD70048E7AD69BBC2F6D5C15AADF0BA74B4CCCE953F1FD934679D25A59A963689E8AEB2F70B8E1F34936B611B330F870581BBC0E6744F98DFDE7941F819B20DDE316F81A81FAGE2GB3G961AFCCD2A061C62E3721A248B0ED6DDC66DAC293D34792919B0AEF5FED618CE733327FE3B737C08DC0E4E4FCF0E72F8395664EB7923CB547529
	A9F5750EA40F23BA2CB8D544F8340E70B57A15CE72F0E8E70C7075088D155976E12F8E79205803BBB41C1768069AD6239CD7CBF0C15DA0458145FD9AC9DF374891673AE5B062DC3744A213576209C83A3A06564D4F1133EAA6581785C0F18F3BB06EA1E82F9E616BB14B63052767DC09E7BBC0AB409900E9GD9GB9G2B4FF15AACC20BC6E98E649D25B953A1F87A81B2C6E94EC3E90BBD99A96BE3874C5F5EEE354963386DCCF6BA9E1E143EB38E34D41746C9F55962B0867BC2F90A5FD7CE42A3352EAFF62059
	C168163DD8CE56B59ED762E40363CC03757D425C069CEACBDC98A1DEB7FD312B18513CEB6A9DAD4246A75F1C6C2EF85E684439E1F7BA5174BE3BA46A6C33BF086279096BA3C6D0C9754BE5519A51E7C52CEED309E0B3D8BDC0461B128175F16A781F127134AF5D78FF0F2673FDBAC76D3576B95AA766E855D1BCC70B9C668243C38EF84A4F335B4EEF3F603772941F73DC0731790073A41271D47FFC9D1FFB8951DEC75AE3163C829CDBB4E9CC9F01A0B4AD01776EAB853C5E2901F6B3C0BF001620F98E0D4B5F9E
	453E9C02D2872663C4CC89A27F989E7BB3979F3B9C68D783A040FB818E0D717C6878A84E6F0F76C881C5238EC7714F5461F149D5BA6F7B554E97451854EB84CEF33499475C34378D42E62537D270E275DA5FD84C6A1F7EEA3568077CECC4A2D4180476DFDBD3A17A2D121E766575D659F5303AD9CD1C6B31670E8B46681CAE9F63396512E3CE7D2BC57F4038D37FBA51FF2A45293F68BC6FFFE05829FF7BF99E371FFF13476DE6017BB5B9DBB320BDC760CE3E4F693284AE0F1CFF6B20DD71B9473DDE4B696A846E
	069879968E6D991F739A2623005FBF4E835C2A4F391F3578134EFF1CB937353DE72872E1A95CC33C161E3D538B0A58780A8B3CDE3A0F706C02369FA0F1015F03A5DE63B5DE76ED73D1CF066178DDF7F55FE9574C8BF4CFAD3AE065570D6ED9AEAAF3331B4B4C077E7AAFC84D65364FAF7609CAB74D9FE8FFB8299F7BA10E47D67F0DC97DD85FE7DD767EC7127A719C6DE56329ED0F6C6136EDF8E4A1523E6BCF7B4DBA768535E9B6FB36B6CDAF68FF74F2D63F7EA6D4039C73AE6275547E2AC21E6FAC7F02FF537E
	4DB10E737C05633EBE4EF187856E2BAD9CE78A5C3A61A2E6C7B660EA46B12E306D08036E7673F3B36239A2C2EBE05CFA00ED8CD78ABC9EC156BDF5955A74BE0EDB262EEBFDEA8CC79A649F473F2BA841A4084A0C3CBF19CCAEEBB3FBE60DA74BDC8C6DA617DD263B9F2FC14CD0C20AE1A8B23D26C59B082C6D7D58669CF8DFB14EEDEE2F1DCCFEB183B703C39949727BB2E82DE3493FBB06ED06C02DC6C6A2C4BA157D4FE49F7A01DE4B6039D4F74125FDBFBEF50963E6DE74B8DA57CDF711EF36B6DBA9FEB641E7
	5970B43EFCDF44170550D7FD994F4F1FA3E7DD825A33813281F2G56G204BF93EBE96EBC6D354CFFDFA2C87929B4D16BE336F076ECB7B2E7E60E63E0F97FD6F8E3478AAEF5AFB286A4B697EBD401EAB76CEAF40CFEADF069B5BA7C3DF86C09640920095GEB05FD8F469A29FD784A2C5DB42560G390D6346618BB28B0D8FFBD653955AED5B4064D7DC416E9F2FC817A32FBFE69F574A68FA58531FF6DCD652EF9BCCC617491EF3530E1B55E29F3710516530672A34637EFB58BEAE0F5121E974D98D4F7AE949E3
	F71279C4249E227F725F1908C3AC66C86477DEF43DFFD64A57FB7B1D3722A3CFFADD3B532C77C1A6F346B46BCC725C7B111B72F974937B5DD145CDDD2E02B4FCF2587895B6BECE7AD3DDABB6DBFA5FC03AE9EC59D90B47C415D12F3858BA22021B609FG546FEBBE50685D373F0159B8EF542EDBEB49E7E96F35BCD7D21DEA58B34BBFFD925D8ACAAC5D6A36D83A1D5F79E5F423EBFD1D8B09F53F49767E4302CB7976FAF1B170693F298C3A477F2BA0FD6D755B82675A4B6DF23EAFA8F6CD7E1D3352157EBBE70D
	4B7E1D134628F5DBF0CE538BEB2D814C844882D8FCA50FCF3739EA9109CF8E3169DDB92C10A4DB4217A8147EAB6E74364EF4CF7D7B199B5D5821823AE96A914C1F633F6EA6790A9EA3C80A4BA27B4ED04383A6A5E578BBC8EE28A9A10226F9EF9349179E873B56DC49F36145643B69FAE897DD45F1BBC2BCE72E963872DEBED696387927B1AE816DCC015B7CB19FBBDB606EA779579AE82F93382B09FEC5D7C34EF135485F5DA2D79738BBDA78D8D360DADAB1AE935AF3846E7AD6D18F88DC05C16A81E8FBB2B96E
	9EE2C78D346B856E25BF733187846E67649B55B4E84F4D64E77571B21C97AD0476F200FC00222FC06D82F0G208B608CC08CA093E09E40A20007GF28172G50B5B02757883FAAEC0067C9E9F06297A3C032A243D641F1FFDDCC0E814AA32FF0CFBA9E601DC7A16E9C8B8CAAC133CF8A0B71F715E35B6AC10FA3C2175C22B7A651856ACFA8E64DB10AA5DFC2197C8DDB527B3A3BF4EADFAFADA55F5F40C7F1FD4B76EFA939038C05620A58D3AF16D688DCF73CD70F2B383226F1363390FFDB8D69D1136615E7081F
	4BE0DB6C9ADE73BF2AE09A931421F1FABF8EA1AC5FBE901AAC5FF52740FADB0ECF6A45EB686F297FF763460DD4FEAB7131EC40AF96726FBB1DAC7F64D6CB7E2B9FCFA55FDEE3244ADFFB8D4D1DC17CC6AA6B31716479D8CB765FBA20C35339BE373813757907AACB9F37E7AAFD8AA7512799E4ECE1FDD47ECF489EBE8878AEA13F2BB5D97EBD55167C3BDA272A39A6131FG99CD85D60DD205FEE7A44F475BA4AECC833A19C21FE53D497A2C5CE669637B73D473D10C2CFAACD51F25ECBE4A04BE9BF613FAC8CA24
	54C355F5D82F65C03FEABA5F172132747BABD2B6757EBAD766FC16DCA81BF816DCD666FC16DCDDE6BFCBA8EEC6991E7FB04428FEC1F71D401D96386B89CF0CBB254A6600404FA5FAE1FC170285F27CFC2257A84175735F7059F02799AE3B8C4788B0F9C0975FA43EDDC656D955B4F65FCBE854A8048CB19F5FA3BA2AB149267777854E527B1EF23C36A24720A10F755DD7DE0D686F1ED4C9D34F13BB3E93C0988DD35FB90D4958DAE7ABB8F7C84A81BB41916BEBE5982B07EB9B3D4D0D5E16838DBCFF3CC07E3F85
	FD2F59E971397E744571F94BEB71E95CE371795CFDF1FC0E3DE671E9E830786CE95D654C279D9CD083B6AD0C8D9B5B733A45E76FDE0B4FED3D9727CF2F4F6293EF3278DCB8B5C92E5D323F05665A747F3DAC0E7E929CA37BB9D4E822F2FC496B55C3DEF6919BB7D0396050CDDCE775397DED6EE7EFF53B10279BED22DB299BF970FE68C605E43BF4A3B7EC25847E29B23F12436ED3929FC6FFA12D92E4FDEC598DB236CE54327D2BBFFDB27B6C11EA97F20118810572AD9C455B06F0464AAF979C1FC0562718E202
	75F09A9395E22B5ACC3455F6876DA5175000ACE326FA71098B6D627A1168D094D2628693D573BEAB6DE873023EE5C92B7343EF7C7EBF16BFFDB26EC61B3A514BB8301ED08C32979499FF63E1BF406EC445B17CF19107B81830B8F994464DB1CD69C15521637847D5F1D3115B79AE0B1BE8DB8F165404FF195254A54587CC9C4D5E6AC11EA87E29B85ACA3BB5A99AEE22DB6EADB2B7856DFE6C8BE4B69068DBC57445DCFC02CB557D65FBF69DDD33580DDC1D28C453A539D38AC2BA530D0A4D81C3098F681A1C083D
	F308B0AA9A458BC94EB1E0770AB09F9B6B4377F6C26921444DB34A5616F5C25116B0D09321446708CFD0489D8312812294235B444B219BF5226F32D74D57FE745ECBE7DC280099091732BC91B3690489FF44A67BBAC1EB735F1EDBB67F290379F9B0249395121FC700608A0B6A4311C8C0B12E4A7E5A0EAD33D7CF47FA45707490A729B251261094D135B1F8B9C27698479743FAC76378E7A2A0541BE2B16F920A7BE6ADEB7E5A6D7F73B1D15C9AEA221DF409E0E8D3D851A3CD6D21109AD58483D8AFE03FC33047
	B43E941A2D096B369DDD39B1G7EF58439A34D4D64EF8B793B8F7FEDA1189602E959870B160FA67D175A1FC8E01B37A744EEA23B88E093591F98DBB6412BB036F222FBE074B6527992A647AF7FA7669637F6104E1D64EF6D25E9DAEFE9DA0678158D0FAFFFD71AB6A512E29E5634AA2D2685E9B475CC2A2970E31A91D0B24C622FE4429F12CDA05FF47EBEA77B10256963484AF8130F03AB90F71806F17B7D577AAB5FC9FB7F35E0967BBF1133D2FF57750FA4F7DE8F786A59BCC75DE46083BDDD8DFAA2A2E91AA9
	E9C36DD19957230747CC453A2B395F407C631C06F532EF6F8656FB50B6667F81D0CB8788415A0C998E95GG94BBGGD0CB818294G94G88G88G11E5AAB1415A0C998E95GG94BBGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC895GGGG
**end of data**/
}
public boolean getIsAWizardOp()
{
	return isAWizardOp;
}
/**
 * Return the JComboBox1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxConstraint() {
	if (ivjJComboBoxConstraint == null) {
		try {
			ivjJComboBoxConstraint = new javax.swing.JComboBox();
			ivjJComboBoxConstraint.setName("JComboBoxConstraint");
			ivjJComboBoxConstraint.setPreferredSize(new java.awt.Dimension(204, 23));
			ivjJComboBoxConstraint.setMinimumSize(new java.awt.Dimension(204, 23));
			// user code begin {1}
			com.cannontech.database.cache.DefaultDatabaseCache cache = com.cannontech.database.cache.DefaultDatabaseCache.getInstance();
			java.util.List constraints = cache.getAllLMProgramConstraints();
			Collections.sort( constraints, LiteComparators.liteStringComparator );
			for( int i = 0; i < constraints.size(); i++ )
				ivjJComboBoxConstraint.addItem( constraints.get(i) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxConstraint;
}
/**
 * Return the JComboBoxControl property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxOperationalState() {
	if (ivjJComboBoxOperationalState == null) {
		try {
			ivjJComboBoxOperationalState = new javax.swing.JComboBox();
			ivjJComboBoxOperationalState.setName("JComboBoxOperationalState");
			// user code begin {1}
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_AUTOMATIC );
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_MANUALONLY );
			ivjJComboBoxOperationalState.addItem( LMProgramBase.OPSTATE_TIMED);
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxOperationalState;
}
/**
 * Return the JLabelActualProgType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualProgType() {
	if (ivjJLabelActualProgType == null) {
		try {
			ivjJLabelActualProgType = new javax.swing.JLabel();
			ivjJLabelActualProgType.setName("JLabelActualProgType");
			ivjJLabelActualProgType.setFont(new java.awt.Font("Arial", 1, 14));
			ivjJLabelActualProgType.setText("(unknown)");
			// user code begin {1}

			ivjJLabelActualProgType.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualProgType;
}
/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelConstraint() {
	if (ivjJLabelConstraint == null) {
		try {
			ivjJLabelConstraint = new javax.swing.JLabel();
			ivjJLabelConstraint.setName("JLabelConstraint");
			ivjJLabelConstraint.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelConstraint.setText("Program Constraint: ");
			ivjJLabelConstraint.setMaximumSize(new java.awt.Dimension(131, 23));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelConstraint;
}
/**
 * Return the SelectLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelName() {
	if (ivjJLabelName == null) {
		try {
			ivjJLabelName = new javax.swing.JLabel();
			ivjJLabelName.setName("JLabelName");
			ivjJLabelName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelName.setText("Name:");
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
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelOperationalState() {
	if (ivjJLabelOperationalState == null) {
		try {
			ivjJLabelOperationalState = new javax.swing.JLabel();
			ivjJLabelOperationalState.setName("JLabelOperationalState");
			ivjJLabelOperationalState.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelOperationalState.setText("Operational State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelOperationalState;
}
/**
 * Return the JLabelProgramType property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelProgramType() {
	if (ivjJLabelProgramType == null) {
		try {
			ivjJLabelProgramType = new javax.swing.JLabel();
			ivjJLabelProgramType.setName("JLabelProgramType");
			ivjJLabelProgramType.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelProgramType.setText("Program Type:");
			// user code begin {1}

			ivjJLabelProgramType.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelProgramType;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldName() {
	if (ivjJTextFieldName == null) {
		try {
			ivjJTextFieldName = new javax.swing.JTextField();
			ivjJTextFieldName.setName("JTextFieldName");
			ivjJTextFieldName.setToolTipText("Name of Program");
			// user code begin {1}

			ivjJTextFieldName.setDocument(
					new TextFieldDocument(
						TextFieldDocument.MAX_DEVICE_NAME_LENGTH,
						TextFieldDocument.INVALID_CHARS_PAO) );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	LMProgramBase program = (LMProgramBase)o;

	program.setName( getJTextFieldName().getText() );
	program.getProgram().setControlType( getJComboBoxOperationalState().getSelectedItem().toString() );
	
	if( getJComboBoxConstraint().getSelectedItem() != null )
		program.getProgram().setConstraintID( new Integer(((com.cannontech.database.data.lite.LiteLMConstraint)getJComboBoxConstraint().getSelectedItem()).getConstraintID() ));

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
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxOperationalState().addActionListener(ivjEventHandler);
	getJTextFieldName().addCaretListener(ivjEventHandler);
	getJComboBoxConstraint().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMProgramBasePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(369, 392);

		java.awt.GridBagConstraints constraintsJLabelName = new java.awt.GridBagConstraints();
		constraintsJLabelName.gridx = 1; constraintsJLabelName.gridy = 1;
		constraintsJLabelName.ipadx = 11;
		constraintsJLabelName.ipady = -3;
		constraintsJLabelName.insets = new java.awt.Insets(35, 9, 14, 6);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 2; constraintsJTextFieldName.gridy = 1;
		constraintsJTextFieldName.gridwidth = 3;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 279;
		constraintsJTextFieldName.insets = new java.awt.Insets(35, 7, 10, 13);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelOperationalState = new java.awt.GridBagConstraints();
		constraintsJLabelOperationalState.gridx = 1; constraintsJLabelOperationalState.gridy = 3;
		constraintsJLabelOperationalState.gridwidth = 2;
		constraintsJLabelOperationalState.ipadx = 3;
		constraintsJLabelOperationalState.ipady = -1;
		constraintsJLabelOperationalState.insets = new java.awt.Insets(10, 9, 13, 0);
		add(getJLabelOperationalState(), constraintsJLabelOperationalState);

		java.awt.GridBagConstraints constraintsJComboBoxOperationalState = new java.awt.GridBagConstraints();
		constraintsJComboBoxOperationalState.gridx = 3; constraintsJComboBoxOperationalState.gridy = 3;
		constraintsJComboBoxOperationalState.gridwidth = 2;
		constraintsJComboBoxOperationalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxOperationalState.weightx = 1.0;
		constraintsJComboBoxOperationalState.ipadx = 101;
		constraintsJComboBoxOperationalState.insets = new java.awt.Insets(10, 1, 8, 14);
		add(getJComboBoxOperationalState(), constraintsJComboBoxOperationalState);

		java.awt.GridBagConstraints constraintsJLabelProgramType = new java.awt.GridBagConstraints();
		constraintsJLabelProgramType.gridx = 1; constraintsJLabelProgramType.gridy = 2;
		constraintsJLabelProgramType.gridwidth = 2;
		constraintsJLabelProgramType.ipadx = 5;
		constraintsJLabelProgramType.ipady = 1;
		constraintsJLabelProgramType.insets = new java.awt.Insets(10, 9, 10, 19);
		add(getJLabelProgramType(), constraintsJLabelProgramType);

		java.awt.GridBagConstraints constraintsJLabelActualProgType = new java.awt.GridBagConstraints();
		constraintsJLabelActualProgType.gridx = 3; constraintsJLabelActualProgType.gridy = 2;
		constraintsJLabelActualProgType.gridwidth = 2;
		constraintsJLabelActualProgType.ipadx = 151;
		constraintsJLabelActualProgType.ipady = 4;
		constraintsJLabelActualProgType.insets = new java.awt.Insets(10, 1, 10, 16);
		add(getJLabelActualProgType(), constraintsJLabelActualProgType);

		java.awt.GridBagConstraints constraintsJLabelConstraint = new java.awt.GridBagConstraints();
		constraintsJLabelConstraint.gridx = 1; constraintsJLabelConstraint.gridy = 4;
		constraintsJLabelConstraint.gridwidth = 3;
		constraintsJLabelConstraint.ipadx = 5;
		constraintsJLabelConstraint.ipady = 6;
		constraintsJLabelConstraint.insets = new java.awt.Insets(9, 9, 212, 3);
		add(getJLabelConstraint(), constraintsJLabelConstraint);

		java.awt.GridBagConstraints constraintsJComboBoxConstraint = new java.awt.GridBagConstraints();
		constraintsJComboBoxConstraint.gridx = 4; constraintsJComboBoxConstraint.gridy = 4;
		constraintsJComboBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxConstraint.weightx = 1.0;
		constraintsJComboBoxConstraint.insets = new java.awt.Insets(9, 3, 214, 14);
		add(getJComboBoxConstraint(), constraintsJComboBoxConstraint);
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
	if( getJTextFieldName().getText() == null || getJTextFieldName().getText().length() <= 0 )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}


	return true;
}
public boolean isTimedOperationalState()
{
	return getJComboBoxOperationalState().getSelectedItem().toString().compareTo("Timed") == 0;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		LMProgramBasePanel aLMProgramBasePanel;
		aLMProgramBasePanel = new LMProgramBasePanel();
		frame.setContentPane(aLMProgramBasePanel);
		frame.setSize(aLMProgramBasePanel.getSize());
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
public void setIsAWizardOp(boolean wizard)
{
	isAWizardOp = wizard;
}
/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	LMProgramBase program = (LMProgramBase)o;

	getJTextFieldName().setText( program.getPAOName() );
	getJComboBoxOperationalState().setSelectedItem( program.getProgram().getControlType().toString() );

	getJLabelProgramType().setVisible( true );
	getJLabelActualProgType().setVisible( true );
	getJLabelActualProgType().setText( program.getPAOType() );

	for( int i = 0; i < getJComboBoxConstraint().getItemCount(); i++ )
		if( ((com.cannontech.database.data.lite.LiteLMConstraint)getJComboBoxConstraint().getItemAt(i)).getConstraintID()
			== program.getProgram().getConstraintID().intValue() )
			{
				getJComboBoxConstraint().setSelectedIndex(i);
				break;
			}

}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	//fire this event for all JCSpinFields!!
	this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}

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
	private javax.swing.JButton ivjActionPasser = null;

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
			getActionPasser().doClick();
			
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
 * Return the ActionPasser property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JButton getActionPasser() {
	if (ivjActionPasser == null) {
		try {
			ivjActionPasser = new javax.swing.JButton();
			ivjActionPasser.setName("ActionPasser");
			ivjActionPasser.setText("");
			ivjActionPasser.setVisible(false);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjActionPasser;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GDAEDE9B1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBB8DF4D46515FA9ADD36E28F2E285855AEC56AE2C545165DD2576E09ADED3046236D8125155D26B5E735AD762427D4234BD93ECC92C9C88279A7C2008889C9E018E472CF8CBF49CCC292820CC98C512505174CCBB2B0FF4E3C1081D1775E6F673DB713B709F05ABD651C1B773E7BFD773BBF5F7D6EF76F7786D238E7E1FA0A29101014F4427FB5279092F30410475EFA6AD9014B589B17C2F47F5E8548
	A32DDDE970AC013E65FF0ECB49A51F55E7C05B8C6D45E763D23643FB8179D23D3B91DE227031C3DF691E216119797C26C37059CC5B2FEF4E0667D600DA408D82AC5C0B7C37E614F0FC885ADB79BBC20A099067BE9C73C6F3098E1F484735C0DF9FC0C6B90E294EA8657DCB8107E3D642F36DBEB6B6891E19D43ADBDADB7838F3F7ACA19BFE7C95D38E72524DAF40D14EE8957EACA345A6B4C4AAD9332B9B1EED0ECB63C7C7FD9E4B2864757A3C0ABCBAE1F10E48CE17628BD826DCD72500536214AF3BC6E50B5B63
	8F784683122735356F14033DF6C8C159A1F9E5F7A215FFD7CB861D9B6407363F82F1F735204D2A607DB840F0055061A7B9EDDC46791716100487EE0E2A43728AA67B5A8ACD07850E4F52676C47797BB332B9DD89349DGCC162FE688D962778BD94C8DB8B62DB2AE25942021D2607FC071FD50F682DC2B94B29E2994B28EFF729D12BC7238E9DB949967D7B2591E2C54E4ACC9DF137A8E751B4BF6DC7BE464DB9517D28BB08820G4C8758C97D55EABBG4F7691456673F8FC5EF63F3F436D8C1C72B9E5B73C3736
	C28F47754B233E0013906613FBDB4C867288D8DA956E0F88CC3733C47850527EC709227CB3750BC85D162F19E70A65F32C2E52F44B2EC63BE59EC63BEDGBC5221BC8CDFC4710EEA8D4F566A9F29AFE0FB827A622B051D07378AD97A529611DC6B37CD1B8DE4D9707DA5265A83CC066E831AAC4C460EAE34318278F9GCBGE28192G48C1E163CFEC753359388776C040AB3987A6C77CD2C06918F4391DF2005BFAB8E3A6DB9B81239B3BEEBAE617A5757AB5DA776DEF995AE55D12FBCC258719BD9A8EEAF6E13C
	7FA9AA6F410353FDE32B3A068B691E18B84856B0D545FF1362C3BABCDB5BBA0A27ED68DB8F40460FB7D2DFB0C77AC81062A74C11BE72E60370111B20AF91002D6FE3F9989B3B8F419E853886B09FE0B94073G0907449A7F48D673857711C3373E0C76F3FBB1BC65109210FA3CC1C5720E4AC3D240A50D3865A061E36658D13611B03A5B697E9E859EA7E42F9C1094174F8B33CB9E991CA948465CED4F43B18119768E3ABCB2B07078091877EB7682F83A252072015FA9A932B60275BFE8C71F38AC8D410CB0812E
	35856DF518465CB4F8AF3388DB2D4BC7DC885A73AC42AE6FF6664373A4B8376CEEED353BC6D18CA9F0C57868B63AE79743F8F38D1C23B5E2EF7EEED87820BD6791E2DE7338218FA21457B2FFEA2B0D74411DF4EEA760E3G96570A73746BAD43336C8314C75F9632FD713D77442CBCD2A35A691C760B70E0766EADC47DD602DE89G44AAECFE2E330C79A21E13427B4E8440F1E4273682EC6C07DDF8364817E52FE2177D325789AFC13E76973AB2B53F9C7329BEB3C6719827EC60B8C1956F6E4AE3F818C36F6522
	DF217D122284DCA3130ABCA0AB0A4BBB2E51DF23328443C2E0106B5AD7043A16000E35G47D5DD53CF94687C2B4F673BB4699F441D437D74DF69F9BC88635DD6317E8FB6E0FEB5CFEDAF23ED2D3F25E13635B3821C6F0B7B477BB744A352E7E66661E994B9CA2A4E3F7248950ADF86722E85A0F5C26FCDD4A67D786B15EFF619CA9A07A36479CB72E0BE715D3C68BE9D1BE76C53BF4AB37669783C19FDF6F51E114F323E1772D0C75F14D737879272B09E0E7A3CDE19EEC41504307C78F70328074B6BD24E3A1C63
	32B2603AAA43B9D6073E6C20B1378A5E5B6AC41C1C5AA762E4795507C86C793906795402EFBCEC1ADF477CF3C91D96A719CC3F4CC7FF105D32878CE573CDFA9506FF0546F30E8F26D2DCF749D5242D6A05F3EE4457618B1D717313871297858E9033C475323007C38E0C79829E59D9F5D13AACD924A945C2574552CE3BBB713D3A6C502123E55313FB7318ABA077F87D137CE82A2A9E2A2EE53ED3C08A0E39199C033016DDAE596DBC8D671EF994020FE221FFB991728DD902D330EA1C358DFB2F1B3315714D4FD4
	4275377960488EC8AE2F7257553C0E45414F0BF12DD6419A2E8358D0A76260E881760BB848033D4D67F6B3B7033E61FA742153078BF5FE11DD4F6A492DEA7F55D97A9F0C5A4F64F0946662FE10BF1E14A10D41C0FCD23E42F31133057991FDC312FBD24BF14E97E641532D0B618C5FDFE46C1F43C59837AF29731FABFA1BE02E4266E47BE91904F37A83AE8F9CF9F8A8713A78598CFDDD3C9074409ABA811E9BG58DAFF639A4ABBFDF9A0BFE840B916B60A3AB99B5A65G7686B6CFCC8333899B67EF926312201D
	519817D255E8B42E3ED90CF3C27FB5007BA2462DEB24392DDA6F5628384FA86EA58828FF1FA67278D3397AFA5D99DCEC989BE8ACE862F9FCD3E4FD33FCBBBDBF816FEC923E76C2117E4CED8F86FD23AE9A8278FA5DD22C0F9B8C776562AD70843F8EC22D6314C3E24D6795232C18FFF2B4DF37078F9EA1537369CEFAC3A0D29A5B945DC503BE9B24405D60B6B6B52C8E3964A9A26C70E9173E66FFF25B3D262CA8F688F17D97686C30AEEDCDAA13695ED62C630C6EAEC629101671C917E5D2F1E122ABA9925D404C
	E1585DC0F3E68657EFED133883984DC65F4EED861FGB88E406A11B5740C463143001B8740765F9F8F68775F2AE61657A4B58B1FEDEB2A036775C7A2E177155579EC1DA2620F30652202631CF77C230BC85B67D78D6FA666EC0BB12535B09B1ADBB4DB56162ECC3583B09D6FED92BA06E04C329621630B7DFA9DDFEDE1BA669E96BA12E6747B6B8C753C4EBB57ACF81643FC678E8B1EDB6A0D6CBADF657903669B35AB3F7728536F45580485D1776212437CCE67F064DEDC708E7A4FDA402FBFAC76E25676BC5DDE
	6C876F173C6390B955DCE8FBAA0B113C7D3D6D69A2C692D1C72C59013A13563814D2008600BEGF72B60E1AF19757E22B3B4437DC5DBE664DD405CD6ADD7DE58BABDEFE6357AA63AB7163732DA3DC0456765A0BED107E7EB6B226331DD789E7853FBA22FFD29B112FFDFDBB84F0908B6237B75B4BACB249E91F4EC8F0D3BF70AB821F34956D6B5E76071609F4CA20EAFEE93B57441D2BC8356C1BB99A077885445G8EGB7403CA3E2CD6EACB10A0F61EB92ADB8EAF5993F2BE93AB17BBCB20DAE52BE4B0E985927
	222A5458BEEAAE476C33F5C0444BD5C744DA0EB45F182C7EE9F41132122361F4CC06F6FA470FF3149C95EBF4E997629C503EF0D42CC7D2C90EEE9FBAFC108F2AFB707EF7B0AF7185DC63AE2F40AD2238D1DF0096878C779FBB2257AD09BB0C6B165F6CB02EDB12F64CDC0BA76F08D657303AF9001ED5FE50AF860061EA339037845A31C745FA246714232D203D9120649854A6GCE00980045G31G894744786DF4FC72B11687159CE7F9D38BBC0781C247D95E336038986F4B1E9731CEB8FFE43C90601C21CF40
	C65DBA23DCA8FB8C27AB61B24FC49F2B6AF277B459B2E7204B1AC5AE31BF44FB81E131A1C767475BE62147B55DC5691EAE5EC657BF981403B490E0C6427BDED10A08C18D0A85949175D5FED3A69F532978ECD6F59F5DE31E69CEF945716936E37E56D89A5D375BCA0DFD3B2394F58F7AA68323F2247F76141628FDDAE46BD1E48F2A416B86CA2FC8812823A369BF24742CAF9A7DC8E934BD42EC74C2180D6AD49B958C200D628DECC1FD4EG4F663B37D9BFDF3DBADFD73D307986835A0CF6B8375B4371CCFE7FAE
	BC9FC3EADB216DABAA3F0B1998035C52955FA4842B7B5AF10F5F1CA96A22DAE8F7837881E25AD9BE44627763D3684BE3D09201B9867C72A889F5A06D09ACC11B88631381CAG5F82B05CA172287EA92C83FA7B2491594D9C0E61FFD10EF461553C687B6D1EE215A6723AC10C692D45589C75D6C255D9F66429F3713AEE700ADF6B3FEDF7215A8FF95C24C4C7619056FFFBC92E5A2F157E2CEF3D23C0AF03564D673E5B0E79894B8E9D38B383AC77F46468F34F588EE66B448E111BAEEEB46ACFD67B5F2AB36A4F36
	097E2A23C67DE5EA7F5CC9237E86351FD4687B3BED2C5FEF93674223E6F1AE44283883746CDE8D6D75AA6E0E81B1AED9457D091E2F5DF6B8B36C82173BD70C8B2938F79B90378A5A89F6D1A3BD14A96EB7B7G4E5CA97C53FAA1AC3F404C3C3575C440656C10467B283733337D77740CC3FAE727284776EC41B96F0376F20078CEF14F965AAFEA48258FAEA2FDB7FD4B703BF142603D2644CE3697B33A347C5D1C3EA6157174E4891EF6683F56C53FCFE6696D0B3E12174A7293567FECD83F0871DA7F2630FE1477
	E5133E5F1A2D6FC79B9D6274CC37E7AA04EE9B1FD9C65C7F32B06AB77115DC27D705EEF76EA38BFE3A125747FF98ABA106F99D5DADCC7F3F4B9179D45989714D7C25C6019BD6F1D6B56F1A27624E9F9538D5AA6E871399C20FEE317E5B865EB11623E3D2D168DD982F47AB44FCADC03B389BF1BFB45B8D74565BCEG2E139FE8E2G0AB8EE813CBFC334BB748252BE0031D271795C03AEBFC6B7323E9BFF73718C26C10471DCB2B0934FD4BE5FE67EDE5B934E538E6D9A131E27D20F6B678F48E3F2A0A0BB5995B2
	1953033CEE27F6F5407B399E21F3655E1978EF5671263FEB08601F50436AB00DFFCB836A8C07034BB369215C99EF875D436B61BD39C764D00FCD2B8DE783C6773D9B24FB7D3A69D84D79FB9A974AFBB5DD997EAD0A2F5561D9EC2A58A2E2D39B74AD6C953543239538C752CEC27C85E881F0G7881661E9435441B3BEA49AC355D204F5F874914DB175AB37D82D9B7764D7F294DA286ACFF621BE4452D97225E11ADBC4976FEACFF2E38E39F45B37D5AABD13FA4684B6E0BCB29856886B8872074897D5AF71B19FE
	F81D572EA85268843DA9944A61651D0646840369B6B7C72F5B1CBE467FBE7EDC56B7BDB7E2F489E1F57CCABEEEADFF2E0FCA57D42F27A3275838C27E2CBA950D4EF9D4CF574647B978539915EE55241EAE44474D654F0570DCFE4A2836ED2BB0DA73D5FC6CBAF8A6C5506DA12C3E48A062DF523169B842E3555EEA227A49AF33051F14BE7CG313FF84134AF0A1F241DE6BC87CFEB67E06C40E769EC1E677ED7FF9F167B55B92614A8734C65748BF47398494FE47D4981CD6EE1B48B0FC969FB1146ABCF152917F5
	F69738977E28212A2E3641DCD6D7DF4DF5DCB825172DE54B57225E5545F3199278F36E4AFD0636E572FC7F10A6DB12C5136D6572AFA29BDB6BB7B0FD666F3424646F87375C4877646B0B1D763FAA8F969B3ED3913DCE7C778A63BAF1E505719D480FABE67EF67B530A685FEEFFD6217FF64BE2DBEC8D66D1A5E7A0A7859886F0835CF4C64435717DFB890DEB86B12D53B9AE5302C0420BA1B63E37B23A2E6FD74E7E1B200FAA5199C6FC1054F8F0FE013FD0096B25787C9449F0F2A55AA8609A1FD058D00E3FD409
	FE6A1647A81A655A8F509C6DBE50EB699911FFFF5A0B38D8E8BF2F62FE55AB7274F5AA6E0DC6C1EBF6885C33B6449D0736C3455559856DB995F70A66A6CB217D240A7B63EE44BD8F6D849577519EC13BDE45FDF2D450363DAFF0CE7A9D4A816D8BAAEEF53120F52B38DF509C72C9E8472B381DA79017G6DA495F75389C13BD1451DACC7DCDFBF54AF7D82B7C0734A9034672838959502F63E0AFB1E6A1B886D64FE119F6C36E2AE16BD9017D2855087608488814C83D88C3082A09EA091E0A3C06EE0DCCAB9C08B
	C0B740B9G7F206A1355983C07240B533F20811297C53ACAD578FA8CDEE7F09FF9243AA25A9C60593D90334E0CDC14C715C1E9DC2577D25DE2C00E052AAC771B8F4EA08B5459D034ABD79816FE99667CD755C45FA74F554C3ECF5656506F11605FD847733B569AFAA7BBB69614557D782B1ADC955793F47802AE2C54D90CFEA2873FB50F793C8A4BE51F27FBA496F40B9F94F79B73FBF10C824230983F2181ED48F26CB7FBE74A317579B3C20C2E2EC07C46C1767B727F5E34E99363DF56047CB3BE08CBA97CC070
	CF3405735F5E24716F314F46DFDF57C47237FF4072F5E03F0971F256075B63AA2BABE05CB9D51E6CBD6172B81AB5F9BE5EB31BBC29B348B397F8E471BE46FFB15D7F8BG3FD8657FC9F1B87FE00B467F37453355F9B371DF8DBCEAD2D8CE44F83DBE99EE0F6C4BB4FE4038752ABCF5A74265397B30A64FBDA7E633073E8E0B1427F40859A3C715E7241C56E0D2A822863BCA63D5950CEF9892F2DD2C88176B3B2D1ADC4FD58839AE8C99672D7A5AD001B188C22EB9C3113561AB75B87E32AB68425FF7B0197E27DE
	5C094C9F9231224E9AFD4F9F364E3E67BB2C46E7E337F57A59F84AEAFCB68ED875E7A37F4D0995FDE29C626630BA6ECFAA6EC395B7C267C45CD917D319D07193D4AE44F74B48D0603DD42EA90A9B6638208E77A147052C9837C065891F7A5D68D3AB75BD175B4D5AB753B1AEAF04B1559E37D299DDFEC9A777EDAACE13BBD80F6B2A66CCAC8C733E2B749BB97EB64DA539DDD7697D6AG8C740EB33FB9F21C286B7CF1AF66C2E1B9ED97EC0E2A3DCE20750D6FB517551A4B2C8D55A29F3E500E346CFD23CD1B67A9
	5B754D3327D31BE7E72FB64F2FF7DF5FBCC7CFEA73141C566699AEFE57F81EF6F0C0B7CCE36553F05A774EE873D476EB73ECBBF1FD72748EE873F88635F9DE2B1B21F63056DBD96D406E3B48FE71BDF5896C3368755E3C8F34790B9AF07C18648E4AC45481697B7575E6824C3541A0B6680165BB2514FF46D99693783DAE4515F19C0B37527EEC4D4F6876E17852D00E06975B07307FF755E2439FFDAFFD0D24ABE49BFEBCA8730D15718B7EE000ECD32D53C337C542657A5654179FC8B511349E72B68B93BDA48D
	77FA8FC92721200724C21888793909C47937A578B1790C3636F00320D07B00C77E7495E87F4A4FDEDCFA5EDEE8A2A6E0B3A133F95327D0A7BAB38A9F2F4E788251FE621BC931E9E20CC252D12BD605646B3EED14510F9B004C612A16E106836D4C2AC96FA5AF14AF558AA994FDDA1A57DA86168311F3421C6F07DFEF7C2F787F9C8A261237FB48BBF8168D48813A55E5A7FEBE647F87200BE47A717BB61EAAE033A0FD248715ABEE390F940E75636F7B020A6CEC97C1A4281022BE64D403BF0E2A691602938A866B
	777AC81A977F378249E75DEE49BBDE43A24AFB54BCA96D9728DF22C1D05E82AAAF4EE2D1E7A97837ED956F76A62EC8A526AE32456D131CDD52A8E410BDA4D319884841891F5B99729F233109E4CC61DA525481265B292A0F4ADA70FAD6957AD2687E39B92F66F419A0138F109ABA920FC80BBA42E91B108240CA8E74A838A23EC0395AF773EEE57F33A7361FB311942EA42EE6EE488FCB83CB608D62AF07C002328889C417BA11654E27EB6FFA682D0F68C49A29C276B01381E94D386C7354340F0D393C32BA8158
	8F265F25CE0FE3AC91E372C3F794752E59B4826BEDA7291E5ADA7A57CA7F5661DFAB45D8A946DA87C6CCA2B37E0B6C8FD19FAED41813285B9017595285AB257C7C3527973D5416349E346BA26974D7C9E06CDCED5469C94F089C38F569DD3B3266A75C06EB61C71790733EBDA6F9DC6EAB70E227E1B3047872103A2168C682F81BEED144EE1D66D5084D1F6E9E08AE261DA5746F4E6BD69E29F6D15ABD4890DF7606CBFAD013B407089BB691E27E26A42D8B0505BD1434A02A24A9D1A4CDD3170D65B40CA30E7A7F
	D021ADDC85FC667EED9A5B26C93A1AE8C5C6780970A5F5CCDAB8B0AF2A7EDCFF0719FC4B67D16FB0137F4C4E6E6C73113FB774DA6837D940BB4F0B3C7C66AE3C440CF697B060115CEEC5F2DFEA77BA71DE20630AA2EB77EDC18B4E9F94E3F8A74FADADA877C59D4D7F81D0CB878853695964A096GG2CBDGGD0CB818294G94G88G88GDAEDE9B153695964A096GG2CBDGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGDA96GG
	GG
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
		constraintsJLabelName.gridx = 0; constraintsJLabelName.gridy = 0;
		constraintsJLabelName.ipadx = 11;
		constraintsJLabelName.ipady = -3;
		constraintsJLabelName.insets = new java.awt.Insets(35, 9, 14, 6);
		add(getJLabelName(), constraintsJLabelName);

		java.awt.GridBagConstraints constraintsJTextFieldName = new java.awt.GridBagConstraints();
		constraintsJTextFieldName.gridx = 1; constraintsJTextFieldName.gridy = 0;
		constraintsJTextFieldName.gridwidth = 3;
		constraintsJTextFieldName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldName.weightx = 1.0;
		constraintsJTextFieldName.ipadx = 279;
		constraintsJTextFieldName.insets = new java.awt.Insets(35, 7, 10, 13);
		add(getJTextFieldName(), constraintsJTextFieldName);

		java.awt.GridBagConstraints constraintsJLabelOperationalState = new java.awt.GridBagConstraints();
		constraintsJLabelOperationalState.gridx = 0; constraintsJLabelOperationalState.gridy = 2;
		constraintsJLabelOperationalState.gridwidth = 2;
		constraintsJLabelOperationalState.ipadx = 3;
		constraintsJLabelOperationalState.ipady = -1;
		constraintsJLabelOperationalState.insets = new java.awt.Insets(10, 9, 13, 0);
		add(getJLabelOperationalState(), constraintsJLabelOperationalState);

		java.awt.GridBagConstraints constraintsJComboBoxOperationalState = new java.awt.GridBagConstraints();
		constraintsJComboBoxOperationalState.gridx = 2; constraintsJComboBoxOperationalState.gridy = 2;
		constraintsJComboBoxOperationalState.gridwidth = 2;
		constraintsJComboBoxOperationalState.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxOperationalState.weightx = 1.0;
		constraintsJComboBoxOperationalState.ipadx = 101;
		constraintsJComboBoxOperationalState.insets = new java.awt.Insets(10, 1, 8, 14);
		add(getJComboBoxOperationalState(), constraintsJComboBoxOperationalState);

		java.awt.GridBagConstraints constraintsJLabelProgramType = new java.awt.GridBagConstraints();
		constraintsJLabelProgramType.gridx = 0; constraintsJLabelProgramType.gridy = 1;
		constraintsJLabelProgramType.gridwidth = 2;
		constraintsJLabelProgramType.ipadx = 5;
		constraintsJLabelProgramType.ipady = 1;
		constraintsJLabelProgramType.insets = new java.awt.Insets(10, 9, 10, 19);
		add(getJLabelProgramType(), constraintsJLabelProgramType);

		java.awt.GridBagConstraints constraintsJLabelActualProgType = new java.awt.GridBagConstraints();
		constraintsJLabelActualProgType.gridx = 2; constraintsJLabelActualProgType.gridy = 1;
		constraintsJLabelActualProgType.gridwidth = 2;
		constraintsJLabelActualProgType.ipadx = 151;
		constraintsJLabelActualProgType.ipady = 4;
		constraintsJLabelActualProgType.insets = new java.awt.Insets(10, 1, 10, 16);
		add(getJLabelActualProgType(), constraintsJLabelActualProgType);

		java.awt.GridBagConstraints constraintsJLabelConstraint = new java.awt.GridBagConstraints();
		constraintsJLabelConstraint.gridx = 0; constraintsJLabelConstraint.gridy = 3;
		constraintsJLabelConstraint.gridwidth = 3;
		constraintsJLabelConstraint.ipadx = 5;
		constraintsJLabelConstraint.ipady = 6;
		constraintsJLabelConstraint.insets = new java.awt.Insets(9, 9, 212, 3);
		add(getJLabelConstraint(), constraintsJLabelConstraint);

		java.awt.GridBagConstraints constraintsJComboBoxConstraint = new java.awt.GridBagConstraints();
		constraintsJComboBoxConstraint.gridx = 3; constraintsJComboBoxConstraint.gridy = 3;
		constraintsJComboBoxConstraint.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxConstraint.weightx = 1.0;
		constraintsJComboBoxConstraint.insets = new java.awt.Insets(9, 3, 214, 14);
		add(getJComboBoxConstraint(), constraintsJComboBoxConstraint);

		java.awt.GridBagConstraints constraintsActionPasser = new java.awt.GridBagConstraints();
		constraintsActionPasser.gridx = 3; constraintsActionPasser.gridy = 3;
		constraintsActionPasser.insets = new java.awt.Insets(4, 4, 4, 4);
		add(getActionPasser(), constraintsActionPasser);
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

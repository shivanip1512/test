package com.cannontech.dbeditor.wizard.device.lmgroup;

/**
 * Insert the type's description here.
 * Creation date: (2/19/2004 4:54:55 PM)
 * @author: 
 */
public class GolayEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JPanel ivjAddressPanel = null;
	private javax.swing.JPanel ivjTimeoutPanel = null;
	private javax.swing.JLabel ivjHyphen1 = null;
	private javax.swing.JLabel ivjHyphen11 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JLabel ivjOpAddressJLabel = null;
	private javax.swing.JTextField ivjOpAddressJTextField1 = null;
	private javax.swing.JTextField ivjOpAddressJTextField2 = null;
	private javax.swing.JTextField ivjOpAddressJTextField3 = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == GolayEditorPanel.this.getVirtualTimeoutTextField()) 
				connEtoC4(e);
			if (e.getSource() == GolayEditorPanel.this.getNominalTimeoutTextField()) 
				connEtoC5(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField1()) 
				connEtoC1(e);
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField2()) 
				connEtoC2();
			if (e.getSource() == GolayEditorPanel.this.getOpAddressJTextField3()) 
				connEtoC3(e);
		};
	};
	private javax.swing.JLabel ivjNominalTimeoutJLabel = null;
	private javax.swing.JComboBox ivjNominalTimeoutTextField = null;
	private javax.swing.JLabel ivjVirtualTimeoutJLabel = null;
	private javax.swing.JComboBox ivjVirtualTimeoutTextField = null;
/**
 * GolayEditorPanel constructor comment.
 */
public GolayEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (JTextField1.caret.caretUpdate(javax.swing.event.CaretEvent) --> GolayEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (JTextField11.caret. --> GolayEditorPanel.fireInputUpdate()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2() {
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
 * connEtoC3:  (JTextField12.caret.caretUpdate(javax.swing.event.CaretEvent) --> GolayEditorPanel.fireInputUpdate()V)
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
 * connEtoC4:  (TimeoutTextField.action.actionPerformed(java.awt.event.ActionEvent) --> GolayEditorPanel.fireInputUpdate()V)
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
 * connEtoC5:  (JCheckBoxRelay1.action.actionPerformed(java.awt.event.ActionEvent) --> GolayEditorPanel.fireInputUpdate()V)
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
 * Return the AddressPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getAddressPanel() {
	if (ivjAddressPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Addressing");
			ivjAddressPanel = new javax.swing.JPanel();
			ivjAddressPanel.setName("AddressPanel");
			ivjAddressPanel.setPreferredSize(new java.awt.Dimension(346, 160));
			ivjAddressPanel.setBorder(ivjLocalBorder);
			ivjAddressPanel.setLayout(new java.awt.GridBagLayout());
			ivjAddressPanel.setMinimumSize(new java.awt.Dimension(346, 160));

			java.awt.GridBagConstraints constraintsOpAddressJLabel = new java.awt.GridBagConstraints();
			constraintsOpAddressJLabel.gridx = 1; constraintsOpAddressJLabel.gridy = 1;
			constraintsOpAddressJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJLabel.ipadx = 9;
			constraintsOpAddressJLabel.insets = new java.awt.Insets(85, 35, 63, 4);
			getAddressPanel().add(getOpAddressJLabel(), constraintsOpAddressJLabel);

			java.awt.GridBagConstraints constraintsOpAddressJTextField1 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField1.gridx = 2; constraintsOpAddressJTextField1.gridy = 1;
			constraintsOpAddressJTextField1.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField1.weightx = 1.0;
			constraintsOpAddressJTextField1.ipadx = 20;
			constraintsOpAddressJTextField1.insets = new java.awt.Insets(81, 5, 61, 1);
			getAddressPanel().add(getOpAddressJTextField1(), constraintsOpAddressJTextField1);

			java.awt.GridBagConstraints constraintsOpAddressJTextField2 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField2.gridx = 4; constraintsOpAddressJTextField2.gridy = 1;
			constraintsOpAddressJTextField2.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField2.weightx = 1.0;
			constraintsOpAddressJTextField2.ipadx = 20;
			constraintsOpAddressJTextField2.insets = new java.awt.Insets(81, 1, 61, 1);
			getAddressPanel().add(getOpAddressJTextField2(), constraintsOpAddressJTextField2);

			java.awt.GridBagConstraints constraintsOpAddressJTextField3 = new java.awt.GridBagConstraints();
			constraintsOpAddressJTextField3.gridx = 6; constraintsOpAddressJTextField3.gridy = 1;
			constraintsOpAddressJTextField3.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsOpAddressJTextField3.weightx = 1.0;
			constraintsOpAddressJTextField3.ipadx = 20;
			constraintsOpAddressJTextField3.insets = new java.awt.Insets(81, 1, 61, 82);
			getAddressPanel().add(getOpAddressJTextField3(), constraintsOpAddressJTextField3);

			java.awt.GridBagConstraints constraintsHyphen11 = new java.awt.GridBagConstraints();
			constraintsHyphen11.gridx = 5; constraintsHyphen11.gridy = 1;
			constraintsHyphen11.insets = new java.awt.Insets(82, 1, 66, 1);
			getAddressPanel().add(getHyphen11(), constraintsHyphen11);

			java.awt.GridBagConstraints constraintsHyphen1 = new java.awt.GridBagConstraints();
			constraintsHyphen1.gridx = 3; constraintsHyphen1.gridy = 1;
			constraintsHyphen1.insets = new java.awt.Insets(82, 1, 66, 1);
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
	D0CB838494G88G88GA5F1D9B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8BF4D4553581C1C7C49BEAC0C45470514687CF63AB2F22252BE90BBE347409AD2ED2CBEDDC4D2B3CAE343CD75A32DA3AB819FC788C898102A0A0248A9AA48C89C49B838A79909292A0048881D3484D4C1DCFF267434C9DB201GEF6FF36E39774E4C1D0450C7D96C5CFB7659E77F4EBE3FFDEEC28A37A767E41A8B89494CA14A3F3FE59262BAC9482B5F9656F2DC11E032905D3F3F822CA5950EECF8
	16C2DD6AD3A6CB81A9BE3B824A35D0EEFE42E459886F6BC979C9D28FAFF1644CBBCAC8F2E3F3EA7FF2D24EF3B94BE9F9F655EAF866835483CE8218AE207C0C2ACD8ABE914A794ABBC2261910F0A7527CCC5E2443AFD5681A202E83E02B8DE916D5EDD56A53G07B4736039301351E6834DCA2A5D704A8A052EFDD48AD97C5296F39E4A527167E0373136A665D9C4A6D1C7E41127D6554273E0F337738B1B4FE33589DE2F4FAB0BB617555EA95A5D32AFE06DF1DF9182F62BDD3C6C3609D649638C78C27E4A4A23BE
	C968BDC2C91A852FA8ADE13A4BABA8DF509D4A436C086B89213F8E42FBAB00E4677A3F12F7D051AF29BB0524CF4A0F2B7FACBB53FB21DD533F64429473F81176FB75EA25DD89146DGCC97FFB557A5DD643A8CE9C45ACD8E132594205E417175CD086F00F298A05141F57C7CA32EE35B55E748124EAF4CEB62681862E03A4DF1E8BAAEFC76256CF7E94C1437E33F2FF602DC00DAG970089A085E08B0D555F1E7F981ED51DF20D4F6371F92B7C7EEA499EB86E330B923CD7D6C20D02EB91ED3E001D90960F592192
	83FDB824B9A3E3910159F6167078195A72A41165F2F30E01ED53DEFD4FBCD76131402959D66EC23FAD3F0CFEDB816F19D497C298FE934507F5F856D757E8ACE0F9B254153A391F5B7239AECD5993C9C159E173F283DD46FE7F93738CB753E12EDB530579B87F827A18F441B882A887E88210GC6F6F19FFF76557E01FC5CG719F708A5269D027DF884855A137E497830A2F2D557D795A88D83B0303EE477C1232DF5FC7733FF94850AF73D3F618D33A18BF523AB43FB059213832533BE2E3A3DF6D435F53313238
	0B75E1160ADFC671595D9A1E756DDD940F65C2288B83B07A99F4EC4DD768ADAA1F17A8FE298E4F781C20F8ACEF15CC1684G563FD3AF623C1881E562B1D9B6811482B483C8G899E5E47433E5AFB136328D9573F2C6D676D6F40D38C4B8121419B14852FCDBCA3845CC227A48609C2D3570EFE6D0CE857C847378DE49C953DE2C0105DBEAFF097BCA284D310513436E35C5B82A22DEDF5FBC4906071934E373DFDBDBCA5A1A8FF69378B3228D130FAFBBB4644E5618CF0848670BE47037E5A9242F9AE995EE7F838
	2FFE560138C22F49D26265FE79E57BBAF8B6C2F00BD2E5E52D5B06EA8801DE9E23F9170B60F998682781901F49A283AC82107CA6CB9A4041CBFC3C3E5D466332B66FDFC9492BDF9846A50D47CBAC4666DD0A0E4BEDD49E8918ACC5G0781BAG92G128152G5283FC4D3DA3142070C35DD02FFCC5D7FC6AF59C4C9853835AC6FC7D37E73ABC2E6A12230C53FCA56E73153845F5EC4DAD6A11CF587C38FCG9A564FC7BBF12C10A04CF9G87033C2FB77F3D080D01EAA1A87228BF990000956DDA4FB35A0D97D0D7
	71326815EBC53F683543CBD00939ED97D6EA634161D3E3F58745637CD883819BD4719FDFD84B7040C3BF3AF87DBEDAAF48F2405D9912455322AC3B3DCE2DFDA555A598B1758695DB9BECE8EBAB58A8817429367E6042FADDDCB779FC5DA1FFAB0ED8E5FC0C247B00C420CF8972F87AF3A36E69E62B651CA6ACEB7577B6368D324F10576056044787ADA3EB9034CC46771BE053CB78BC1D2360D72A75F3E87D82503DC0B6D9AAGBAGCCB277C3EBD3DB1C76379A5B31BAFA280CE87E167FA799ACC63EF5B1FE0CFF
	6722F10C7F6022F10C3FF8317F98FE6522D18C333AF99751C6DF0FD7B7A6FEF19167E51B4F6B956940D41B9036C7FF3B956DF0FB5D72D9375DA94A275DD7C418CFE50C6DFDF46ECF057719B21F1BFBBE60F3730EABCF10340B6F9A6E6B46BED6E15E9AE2B1DB9A5266E626536B9DB856C4C9740023EAFCA12F4C7099F4DDD17041AC0A7B5EA9A736ADBE568ABE2CF30B123D2C4486B300EC25BF59E2D65CA5DC964256E08F384ACA7B41DA0335C770F5D7511EBDDF94C51FA8CA9C6E0058607507B80BDDE7F61592
	A67397BD0722E5F221B74D2BCC6155D5640A66356F3699300F494C2F77D11938467BC232AA39C420A1E0E50FE6B1607085BC221D8A378ABD32A2390AD68FD274FE455E546BFEAA7B243F4AEE8F084160BF2563186C5C0F0E46133D6FD67B6F6BA7635A337FF677E3F147E5AA7B044F6306B343BF33BB4B799E204F0EE3359946309D20AF4457458E816B793A28AC7EB5BEC9E2538C542507F08EB9F339D0B7AFA405D8CEA3DF2D9FBAC07D1383543FB6C0FDEECF7F757B86283FB0C07D0838754CCF47BB8BF03E96
	AF05C4586E634621D16C65FB764EF5D1F5E784A9241D855ABBF11D12F4FB8E06BFE9B31EBF3F3461BE23DB65FF4E16CBF0CF4FF83279BED5C01E7E005B83DBB45CC4A93923C9557A5CD171E5138573CCAEF876813036E71DE8CBF4789477E07B1C63BC2F148474D381E6ABBCD674B0FFB03AD9AA1D8C6511GD3FB0C689E8E28F9AAA84F6B09476FD41053AD41F2984EEDE1A33A8DB22773C33D89A0A50AEEC198699AC2BC2F33D745F5D05CEFE17370F0B6BFBB9FAF5067476C41B243F5902124D7B9CF74C66794
	1E9611EF87607D3DFCDC7D44265FEFD68503BE1B1B9E69143E9FEA572F118CF717FD95BCE19487658658FC04F97C5CEBC7DD714C2720159818327BF392FB06E5DD4A3773B5BDF446E875554031339EC230C65DC21CF10BBD047B61DA1DBE4FB6E34D07E6CB9CBF8CD36C1F2C73C36A33AFE5B31D9E2C445C09D12E50C61534BAC3EEEBC8F66361D21085BADD5170A3AC9F17D733C231AF2317675DFEF184474956ABA6CBB9C093G4B81D80F2FD6E86D0883E0E3392FDABF166782FEA9C051D513054D47230FE3CC
	8CF252D7FCBEF357BA56C7D1B3AD77634475079539331F1CC88E5ED8EE188BB42DD9EBAE3F4A7C57FED573E3786C3359A38118FD7F235A1782B469D739FDFF2BB232EFD39F372F6DC42EEA9F4F173C90603CCA004E5E47F9DDBCED44EBFC9F777115A0EF178A3885EA3B84C95F2E681A49D28FA0DD63BA3C915C874F412FECDC5FAFE4AEB781F8CE3F46653EFC4CC86EA2D56EE3722D4AD5721D7B7473C1DA7A4E3873C176F556EF1B2EC74F87231D984365002F3F4E67039562DA5DFC5082A3D070BAE1A5D04F9E
	A26A8FF33ED2FECEC439F44EA7FC8D7F318BEDEF871EE100C4009400D92A0CAA4700F94BA361FE72166BEA22F3008B2EEB67552557E34F2EACC7F72A8E7B2A60864B51652878F60A5F2143333E7D55915E3795D0B77E86BFC7169F0F16CF92A2E5EE0DAA33F655B16D2CD16543D16D58D85ED93C154FD53A21DFD929ED8D591A1AD427DFD35B92D80E208F1E09897C9E611B6ABDC2CA824F2F95F7A17FF4A8AF85A89C82EB9AC0AB009FA0E9886F37C40751BC9E59EF71A6F1ADF742FC7147DB74E14E0901FCB8ED
	C8E4BBE637370A5BBDFB08FA779538B59DDE0BE9972DC376D0A39D461C66BAEC9D4AF5C8885E1A8E7745340B5621F528D1ACB925AD463184FD5A692B7605157D591BF623D81200E742D09ECB4FC85C1E312ABD35722D59539253AE5A1EB906BE4DED64BAAC984A6359550D3822BBA00E6F60317B16837318FCDEEB76393DB261F35AB8A72EC33E005B6976F25C2394E77385E8F20361DEF3454F3B3C61B24E3B3C69B24E3B3C656ABF3778FBD73C3C8C4B836E227B0F43E0DFABG43B555A2AE8C6544BBF8FF1C2D
	C35CB4A84FD6F11D94076BA72EC98C775FC77479BB24577BFA769D31F36AE00049B05769E5C4677FF27E817E886FD11EAF3E33067AB5989403F4B2425D1BD277263C01986426CE889ED151E7BD69BE3ED221B9A27BEA4A54F1733BDD1B0C470D6E3EF8B675AB6EC7B36E60FB4245746C34F5986C89811A06F1FFCF6F778E4DBEAC565FCA4E39ABFE8CFE56E59C03073AB076033ED040A6C647D9DD57A635CE1B2D9BE45103EED14EFF343DAC841CE2CC1C1E216DD9DD3C76973B6245B2733975045E677B54F889D6
	75672330010F983F2C201E5FFE155FC3277B63B7B2AE3FE7A2781593BE77EC97F9FFA78F637D7DFCB77677BCA8AF82D8B11C77778C29BF59C54363493EAA6BE5FF225AE2E96C0FDF3981BF56971EEE5C63045572A5DA6ED5659D2E41F98FCE5C3E90CC10630723CDE7EB74EBC1BD607CG439499ECFCBC5D03FEF084848FF46DE93FE8A361BB312D2D16374DG7AA5G1BGDF8150F6A75F8BEF79BB7ACC49F7517144706F6D20F7A4AC8DC5714C76D76A386DE940E3AE4002BBF57BBF14FBD7247DEC1FA7515C52
	0E3B58BEAFCF45DF2578FA9D1E6D7F7EC447AE16A5281BF7971FE7DF2E6673EC2D0963C6D4F1DC120A7B37539C17E162F130C3643A175F8D73AFC07B5D1A2EFEF89FF637116E1FC9284B583B190EABD5FC9D45CF5361196EBF24F10865B9D0D7BE026B7493096BB4D6453D5F48F18BC6705CCBD9C7441E93CFA31515C783EEFB3560EC2251414640B53A8E216C0AFB789818D203327DD0B681A45F43F379618F7959FD6A6393C9D342A143EFE452DBBFB4CF3F0779E07EBD5A1905BC77D2B61379E6AD1739E92449
	52BC1266DB0E684FDB98BF2F18599E02559F0D28477988778ADAFDEFC4BD6A7B1D0876D3A26A51C7E5CAFBE65B8FF7F25B167EB015C87F3EA76E77BFE3C7B21BE6A84F704B1BC9FA5EF3592CAF9650B5E4B14A3C17774F04D3085B81658A95D7F68EF16DD0762B385673EA8C2A38F29F62B2203C74DE9E03813A37AB384FE4A981E88210GC682A45F47E906D01AE977315C5AC8A5A7B6974A0BD59AE26FD078686D1394BE716626A6C5D63C7A127B0C7607DB282C68BD43CAE5FC466305ED7B3BFB0B5EF354FF8D
	FD766B6A5A815AE46920905A94836CD47A7BA03C6FD25E993F89D57D71DB9945AFB10A5F6498FEAF1E6E0FDFDE943F54A8FEB3F57CD80C9C206B589C40AF7C9A0F9B16ABA409F09ECC6438D1A5BC66AAD45CD3FBF84C352B388FEE70181B1A48E3651004395CB4A8E7GE40F821DC6713AA6DA0739547AD1ACD76AD26BDE699E68DE9979694B48B71AA6CD21499EE594DB3F99D0C6F41C985D99E769F8464654EF2401E2CA9FD726D111FDB69E4AEFACDB36CC0B014F9A879323A9D17CE66878B19F8C0D49298C84
	EC7D59DE0BF37D2CD1EC7D49D4714594BFD707E76B5253B57C7B46F4282B7DBABF33FD604179A6854AB3816683AC86A83C9F4EF07773B35C9F3ACB49G7907D61F3F89B647126EC8456CDBD8FBEB5F2CFDEFB91F5727BD77AFE47A1DEFE5464BB7575E4FE24B2EBC8B7EF3B345B37B3E65C57B1220AE95E0AE40C2G12847B21A4EE5F97F40F08D9943BDB2E12E5416622D9F7EE9CA642B5B4EE3258BEE9D99D0EF9FFC0F4080100E8E7D76A441EC43FDDBC0AE331845EAB1278FEEB7AA95C63F57A82F4A34F76F8
	AF1F627B349420CB83D80A6DC733BCABEBD7FFAEF6EFF871DC745E30985A9C9C4DFC40E2357BFCFF311A95852D50568530C1A9770D56E20869516843BE000D265B9372D0EB192DB63A561900BEE9B4577949A2BCAFE93729CDC227A8A9FCCAB696C654E9B7472C7EEB6F4729D76ECB27B547295F4F6A43B654273E576F923DE51C27ED3906D36E741E316B7796E90A4FB3C673DCF2204A28FE09DA5FECD83FFA0C49321D4AA4044E65745F22C331B858DF0D41EF455F8E91B56E5FD84D63FE6B14C9247627EFE5FE
	90A76E6DE3D8DFCF9B236D55DA05A9E6A359CC0E8D2FF3C87C7ABB71F82274D10B98BF363B699A35905AAC99437BFB16989B5B3F94F90C6EF8004576C1F8B6BF40729F0C66391268B8F6C1FD5F837AB8FEE14FC0F12C1FF71320ED4A83DA9C4F0077E54A1C4B74EF226B64AC404FFD006B3FEBA5FE6BFF469D10C331B7700ACF7A8E9546503040E675FBBFC79EF1EE71959E6FF88AE3E8988F96D332949DD368ABB216475C0F0F9955970EB50EB9562FFF6E6D3F5F37DD45921B2FF756EA6FD65D7BB3B5037D4EEC
	B020FCC7F6DBE530393E4493BF0F335FE31C47794CE31CCBBC64697F9B2E231E785FF01D70683F61E2EB555BD731EF0B21AFEB81DCGFDG09E379BAB55F73BE216B14419AF5446E44BC92FEEDACF3795F75463775856F405FA83F6C457B66CE1FAC7BBC481F63DF75E2FF49BEBFC5AAB9DAAF7AA860F63AE4C62A60533D98EF126820E896EFC32E91426719B1270A606995FB0A544B37DA9895DE7CB839F8FF7927A5C5E51F166EE2F3E3F20F7E6E2DE2556E38F7EFD347AA5F31AB4F8C50066D037EF80AFFDF3E
	846ACA9F64676E6F2C42BD4ECD5DCB373AE5C92C26B39DBB979FEA61F7522E8779D97CFA9B4AC90672F400B98F725C7D70F37C8C3DC0257D66B924AD9A87FB850066F1B73BFE7071CBEEE0093D27904D3717BAEF4637CA1E5591A74FCA67A6267B5BF6554EF15C1EA72AB8AED9455D2562660D63B908173F5277DD41388DE63E37086E3B0207D81FEDFDA87A5E746719B8A1B583DE82C884C8811881B097E08140D2000271309782B88C508A608268839889108CB0EDBC9FCF7BAF63F96C0C50957BE584A015248B
	9DD481015DDBB09F7FA8CB7371636200BE7E6C53DDAC7616BBF86C4D829D48435CDFA79D5CDF958F7318F8FDB74D65C05984102C5226177078192E52666FC15AC5D0DE710849D27448E063E7D13696BF6BF3B55BD23AEF46B62657941A5F423E2EF8046B3A3211637C0F28E7092B7167243FDC9DF8CE4A6D235F4540DC525A6BD7725C16BEFA0F64F084C5F56EAB68ABD0F18D41E6DF500D79F196C7E93948A385F41A296A7542355D7D44C2F3406797833272B9947DFACBD9337EF2EA2073CD46A3C6E7DFCB4BC0
	6D36C51515FBBE5A0EBCFAF377C2050F725F1F79DFBA9F95BF9AFDDE7CB34517BF9AFDDE9C56464F0B0721EE7223BCAFB0625C2D1DD7BFBACFFF47ABB9DAFEA94597A6C74BFF769C17DF84F54312397C5F590D64AFC8E67ED81A9C6B97566EC943BB0D02890CFE4704F86DC69A362BD0681B8C5A3198FB811BAB3138106C0C97DB30F29F0331F53233CB34492D02D3ED735B4C7863648F19830F13E51946EBF70EB9F66DDEE5B6DE3B8B4C7A351B61B650B55789F3E31BCA37D9451DD3F15BA9CF441DF55BE5170A
	5FE9DE25606BC59448713B29DEBD94572660CAF538F38A6E93B30EF5B05965D36F07AB4DB801B95C12241C67A90D5B8BC3DF75C78D55516D97F4FA572B38F3EAFF67B9EEEDBCEF99301D71F8EE256DBA8C6248886489FCBC37146078B0CD089ECFDFD1FC5204687114311B0F27E4284B1E484753FF155C5AF89ECA579C5744E87977D03CBCB1DAFE619EAEBF816AB2D479290677F37513CC16F6G7FA46617E113B47F30F6790677F0E301EEAAC01AC2BFD7570E75797339782E6E5959D42E544D4EADA46CF7A45C
	02643EC2BF47BC8D04DEA73B47CEDB1FC76BA537D72CE13F25C7F8AE77AB3A87DCG7248E41E0FCD225F1396C239F8B237F789061FE2C366B96DFDC45B6DBFDFCD7AA74858F3DE3369D35D7BF74F5DFE5973EE2B8C56272BE86E239C7A33D66DE71F48F3714DAA6EF93A87CE06F22A0A7BD83D3F1C2162A6D13A550F19ACDB9F633895AADD310A6B26FB44B01493D51CE3B762A6C3F97AE3BC3E5ACAB41F8B3A775F6C3E7D7E4FAA397DB2CCFBB41BC6695EB76F397D32775FD6992CCF7F036E23E7C2FF4ED57B39
	440338F4A81347B9AE4143E324D045390EA14E8EE5130A5BC977606321BCD5450D203869D01E2762DED76F22972A384D94F7709BA6CB47B7F81EAC50026B3A0CC760BA9FBD93311C5A88BA7F49C0172052CE8965F0DA424F97AF62722F1CAF7EAA623AAD43C60655AD24FFB703657C6E9487133BAE0E3AEBCB859D5693FD6EFAFED5242CB5F91A2C2AFE6F9DADF1E4EDD0784F78C6F41E7C39A8D97B72B5D95B7B1535FCGD933E2E48DF5987747B8BADF4C857AF435BF2E6D0E6D0FAF57E87D713A9429F738C02B
	2B21392E4BF088F25B8429CAF2BB3D18FFD2657D5D434F2585A9A68B3375814FE0723651FDF8B8C5FBE7BA5CB8067BDE5878392F0029020439D5FE32D36DBE4675E8CA61F69FEC0C346B21351ADD939A7B376B5B0D1CDF1FEA576F7B3D839CA8B627821F3C883B4A9A87362BC955E3BA34E7F69C5A2859E18A615F1909B82BE3BC943FDF85878369FDB82A437F72DDEA3FF752BFF3415E073F2B3D27EC565EB73D2771BF3F7BE679B3FA5903747A5F18B80DBFF90AB2E82DB9F22275C80BAA6B40B6CD565347EE36
	AD237FF16360E44D582E49325DF4DB763700BAED2DF038187A96498986489A55978D743011FEF9FFE5562FA6E519C9F68349E5072F86120DA7288612C38FD88DA48B8EDFE17CE5434F1581001DFAAAF809DCC72DA8B6B3AE36A582DB21E0DBF1582958B5BA6C8150F2DDEC76266A3E1F7DF46A455AC2B3B10322AE11E9165303C7B52ADBB230B82FF2C4EEE685B2A9B6B70286CBB38DDBE4C534E0B481729126D85D1D9EEB683247AAF44AD67CABC1565600608DCA020C67D8366DA66CDF2619641CA76B9DF80DF2
	820E4481CEA9B61035945BA232AFA9B1A754C4B6C6602AC3DE3BA462D711B2D957043E58AB89DE67DEA662C093A908A0EF94FBD15D7CA6C5A047B3F2DAC5BDC4085FAF13D54A9DA4D449E443C93F72D7BB8429C82928AC12C9669E86392DEE8FA5DC2B5CA195A997C0C834D6399352F060283C0841767C04CFFEBD7B75B341AC125BC05EF5G66349820C795510E1F5BAAFF53230E2C7463F756F8E7875197240F1C205CAB01EF8A9DAD78FB08C1D934D771C3D090D4E70E4184775EFAA168221DG3E4976025FB4
	23B32BBA28697934DB83E4BD558FDBD955D66B3F3DE667DF0FE5CC4FA266BA32CA728976BA41A67B828DE4256C821738FC12BD6CBFF418B2DA5143BF654C86F6DBD4F351B8AB4E032A125D6107C766AD482B433F0295A0FBA9A5CE69D61542DE639282A0CA8CB448588F3E408E35EE48B679439F9D5D58EEA61911C6C12F308403BA1CB1B82CF520377C5ADBAFCE7C7941C58B21D19D49213FEB83228BB42A93A1CF279838F36A6877ACC9697720E6FEB40B467BD6996C45B8455FAD8266C5D13671AAD5C56B7DAF
	160EFE62CF67290ADAD319ECE7FD83CD77BAC51FE7EF15438187D0154101309F583F2732C79AEB944D3A70288D47DEDD568911D3CB32BC252574E7997D398FFF16D1CC9945146D036ED9C47A7D97DD9F66B653F02305A20C950E4EF5889E37548BAF359030BEE79871FB436AFC013F080F975D38D0FF29E22BF5F3E1CBBF580DB12109584D31B10668ED7863FD7C7181A55BC9FF968F5AE2EC7531E6A93E6C09342FC2336FGC03152729D953BCE4162ACD9456EC9F65D505F13AC993EBA6E9D576227D8EEE3C5EA
	74BD49DE7A7BE535006FC865796D9B8EBCAC454BC11E76881284BB0F6EAA2F9D7311553D32285DE316BB11FF1053A8152C6E0093756E523579BFD0CB878823G4EDE209AGG10CFGGD0CB818294G94G88G88GA5F1D9B023G4EDE209AGG10CFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG5A9AGGGG
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
 * Return the Hyphen11 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getHyphen11() {
	if (ivjHyphen11 == null) {
		try {
			ivjHyphen11 = new javax.swing.JLabel();
			ivjHyphen11.setName("Hyphen11");
			ivjHyphen11.setText("-");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjHyphen11;
}
/**
 * Return the NominalTimeoutJLabel property value.
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
 * Return the NominalTimeoutTextField property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getNominalTimeoutTextField() {
	if (ivjNominalTimeoutTextField == null) {
		try {
			ivjNominalTimeoutTextField = new javax.swing.JComboBox();
			ivjNominalTimeoutTextField.setName("NominalTimeoutTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjNominalTimeoutTextField;
}
/**
 * Return the JLabel1 property value.
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
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField1() {
	if (ivjOpAddressJTextField1 == null) {
		try {
			ivjOpAddressJTextField1 = new javax.swing.JTextField();
			ivjOpAddressJTextField1.setName("OpAddressJTextField1");
			// user code begin {1}
			ivjOpAddressJTextField1.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField1;
}
/**
 * Return the JTextField11 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField2() {
	if (ivjOpAddressJTextField2 == null) {
		try {
			ivjOpAddressJTextField2 = new javax.swing.JTextField();
			ivjOpAddressJTextField2.setName("OpAddressJTextField2");
			// user code begin {1}
			ivjOpAddressJTextField2.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField2;
}
/**
 * Return the JTextField12 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getOpAddressJTextField3() {
	if (ivjOpAddressJTextField3 == null) {
		try {
			ivjOpAddressJTextField3 = new javax.swing.JTextField();
			ivjOpAddressJTextField3.setName("OpAddressJTextField3");
			// user code begin {1}
			ivjOpAddressJTextField3.setDocument( new com.cannontech.common.gui.unchanging.DoubleRangeDocument(0, 99) );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOpAddressJTextField3;
}
/**
 * Return the TimeoutPanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getTimeoutPanel() {
	if (ivjTimeoutPanel == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder1.setTitle("Timing");
			ivjTimeoutPanel = new javax.swing.JPanel();
			ivjTimeoutPanel.setName("TimeoutPanel");
			ivjTimeoutPanel.setPreferredSize(new java.awt.Dimension(346, 196));
			ivjTimeoutPanel.setBorder(ivjLocalBorder1);
			ivjTimeoutPanel.setLayout(new java.awt.GridBagLayout());
			ivjTimeoutPanel.setMinimumSize(new java.awt.Dimension(346, 196));

			java.awt.GridBagConstraints constraintsVirtualTimeoutTextField = new java.awt.GridBagConstraints();
			constraintsVirtualTimeoutTextField.gridx = 2; constraintsVirtualTimeoutTextField.gridy = 2;
			constraintsVirtualTimeoutTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsVirtualTimeoutTextField.weightx = 1.0;
			constraintsVirtualTimeoutTextField.ipadx = -16;
			constraintsVirtualTimeoutTextField.insets = new java.awt.Insets(16, 1, 56, 84);
			getTimeoutPanel().add(getVirtualTimeoutTextField(), constraintsVirtualTimeoutTextField);

			java.awt.GridBagConstraints constraintsVirtualTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsVirtualTimeoutJLabel.gridx = 1; constraintsVirtualTimeoutJLabel.gridy = 2;
			constraintsVirtualTimeoutJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsVirtualTimeoutJLabel.ipadx = 18;
			constraintsVirtualTimeoutJLabel.insets = new java.awt.Insets(22, 40, 59, 0);
			getTimeoutPanel().add(getVirtualTimeoutJLabel(), constraintsVirtualTimeoutJLabel);

			java.awt.GridBagConstraints constraintsNominalTimeoutTextField = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutTextField.gridx = 2; constraintsNominalTimeoutTextField.gridy = 1;
			constraintsNominalTimeoutTextField.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsNominalTimeoutTextField.weightx = 1.0;
			constraintsNominalTimeoutTextField.ipadx = -16;
			constraintsNominalTimeoutTextField.insets = new java.awt.Insets(59, 1, 15, 84);
			getTimeoutPanel().add(getNominalTimeoutTextField(), constraintsNominalTimeoutTextField);

			java.awt.GridBagConstraints constraintsNominalTimeoutJLabel = new java.awt.GridBagConstraints();
			constraintsNominalTimeoutJLabel.gridx = 1; constraintsNominalTimeoutJLabel.gridy = 1;
			constraintsNominalTimeoutJLabel.anchor = java.awt.GridBagConstraints.NORTHWEST;
			constraintsNominalTimeoutJLabel.ipadx = 6;
			constraintsNominalTimeoutJLabel.insets = new java.awt.Insets(65, 40, 18, 3);
			getTimeoutPanel().add(getNominalTimeoutJLabel(), constraintsNominalTimeoutJLabel);
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
public Object getValue(Object o) {
	return null;
}
/**
 * Return the TimeoutJLabel property value.
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
 * Return the TimeoutTextField property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getVirtualTimeoutTextField() {
	if (ivjVirtualTimeoutTextField == null) {
		try {
			ivjVirtualTimeoutTextField = new javax.swing.JComboBox();
			ivjVirtualTimeoutTextField.setName("VirtualTimeoutTextField");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjVirtualTimeoutTextField;
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
	getOpAddressJTextField1().addCaretListener(ivjEventHandler);
	getOpAddressJTextField2().addCaretListener(ivjEventHandler);
	getOpAddressJTextField3().addCaretListener(ivjEventHandler);
	getVirtualTimeoutTextField().addActionListener(ivjEventHandler);
	getNominalTimeoutTextField().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("GolayEditorPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);

		java.awt.GridBagConstraints constraintsAddressPanel = new java.awt.GridBagConstraints();
		constraintsAddressPanel.gridx = 1; constraintsAddressPanel.gridy = 1;
		constraintsAddressPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsAddressPanel.weightx = 1.0;
		constraintsAddressPanel.weighty = 1.0;
		constraintsAddressPanel.insets = new java.awt.Insets(1, 2, 1, 2);
		add(getAddressPanel(), constraintsAddressPanel);

		java.awt.GridBagConstraints constraintsTimeoutPanel = new java.awt.GridBagConstraints();
		constraintsTimeoutPanel.gridx = 1; constraintsTimeoutPanel.gridy = 2;
		constraintsTimeoutPanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsTimeoutPanel.weightx = 1.0;
		constraintsTimeoutPanel.weighty = 1.0;
		constraintsTimeoutPanel.ipadx = -1;
		constraintsTimeoutPanel.insets = new java.awt.Insets(1, 2, 1, 3);
		add(getTimeoutPanel(), constraintsTimeoutPanel);
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
		GolayEditorPanel aGolayEditorPanel;
		aGolayEditorPanel = new GolayEditorPanel();
		frame.setContentPane(aGolayEditorPanel);
		frame.setSize(aGolayEditorPanel.getSize());
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
public void setValue(Object o) {}
}

package com.cannontech.dbeditor.editor.device;

/**
 * Insert the type's description here.
 * Creation date: (4/4/2004 11:31:17 AM)
 * @author: 
 */
public class LMIExclusionEditorPanel extends com.cannontech.common.gui.util.DataInputPanel {
	private javax.swing.JLabel ivjCycleTimeJLabel = null;
	private javax.swing.JTextField ivjCycleTimeJTextField = null;
	private javax.swing.JComboBox ivjImpliedDurationJComboBox = null;
	private javax.swing.JLabel ivjImpliedDurationJLabel = null;
	private javax.swing.JComboBox ivjOffsetJComboBox = null;
	private javax.swing.JLabel ivjOffsetJLabel = null;

class IvjEventHandler implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LMIExclusionEditorPanel.this.getOffsetJComboBox()) 
				connEtoC2(e);
			if (e.getSource() == LMIExclusionEditorPanel.this.getImpliedDurationJComboBox()) 
				connEtoC3(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == LMIExclusionEditorPanel.this.getCycleTimeJTextField()) 
				connEtoC1(e);
		};
	};
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
/**
 * LMIExclusionEditorPanel constructor comment.
 */
public LMIExclusionEditorPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (CycleTimeJTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> LMIExclusionEditorPanel.fireInputUpdate()V)
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
 * connEtoC2:  (OffsetJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> LMIExclusionEditorPanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
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
 * connEtoC3:  (ImpliedDurationJComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> LMIExclusionEditorPanel.fireInputUpdate()V)
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
 * Comment
 */
public void cycleTimeJTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G6B0804B0GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBA8BF0D4D516A09F03039AB083A881A2818990B116A8299D2C42A9EB5759E11429E8496CB0B53803B31603AE6BB0EBF60A593A69A692B0C0A0C123C2CDE6641310AF04C826898869C4A79A1788998C109DF969FEC9DE686E573EFECDBA011959F36EF97775CB67F582163B29BAF96F1EFB6F39671E5FBD673EE6C53FCB494DF194B116134B0C3F63B90C35D5B3B6277A69B182D7BCA64B49ACFF3F8758
	45DED7B64333947A9217E4B98B58714EBCE8B7C03B6141AC675B70DE489ABAB7D640CB1CF5D6C25F1533EF670D3C4E44BE314E965E7E8FD7BEBCF7G1C861C97A0E38C2E3F63C43101CF00768E639DA14741D8F0AC0EF96DF231853F51985784FD9DGC53761183293FB0D7ECC406118D570DCB71646EE0267D64E5D84D73DB12EF3CA9A5B784F6FB93663DA967A82BAEF2339E30CE789CB6502F0321DFB5CA833362BBD27BC2A3F4AA38582EAC017BD3DD55EAE592B682AD6E5BC3C72B545A33BDC2D1F35B4C7BC
	3EF0C8D1834D3C2BCD8A48BE360173DE4CF500C24C04760DF108CB6CC4F99D0377D6G6FB8417F8F37B7987CA5DDCDE3EBD3F74465FF45B862FB5D38A87F4B9F4C4DCD9A0F744F1D48B7666D07F6A7G71F25F89414B6A710217639DB836E0C2163394607484017FAF0E6F00F690A0E102607164A141E34775E5AC3B6B14E3E79C9ED3A690EFCFCD0872185037BC779DEEB33BFD28774D93E1DD008600ABGE3GD2G5E63367A033E4370BC53253BD53FDF8D1C89869BFDDE6DB355AB7B605D65029E83F7CE7628
	1A17B13247FD1D95B67C88481CB8549691E8EFDFB0E1BF696716B0DDBF664835595B526776B9D69AB456CE0C6E2D78F6145B667FC139B1F847F9488F618BB83E5302A7DDCD633600ED9D7AD65FAE645C31C370523AF9AEABB8F25A31450617995F2DF3B4A4908F6D89D1DEC8466B7DA863B100CF86D88A3092E0BDC07EA4A163372F560EA6639630FFADA0794E073B021226B706951FD7568CD9F71C98C956F6C0733AEFF99E49A52D562A23B50FFCE8AB17B5E97B9D2513C89E27A7C565C2EB4F093B7615C943ED
	E3072943455CA774C924C32709FF146347DB70245B139C4F5B50178DC063350B5C966E0835113FF13CFEC72C0DEC3EA8ECE4B274EDGA07D5E21E2DCEC7F9678A8C082C08A408A00B5G9B3EA5F45CF5357AA67D284D22DF1A7B1E6FADF84A91DD13DA82A1DD8AF8648B1226C8DDBEB9440CB17BFD48DB5710F9E55C3FBD304647F2C056A49D22A3D0177CB298D308469C756D42B11A4CFB5B953F8C8B7803CC50F5798A616913C27A1F03DEC91723A3283F5107B6F1CD3AG9401G6AFAB24A6B980F398570DEB1
	D948EAD990F163211DBCD94865F15FEEF8FE8A46AD7BDC2EA64503ECC85A003051ED5CE7B3E07C15BB613C39D378662B9D42861B36BF48AA1EBBE5EB0388413B481E926E0A35413708B660D781EC38CB1C25491D9DF1ECD370F1ABBE434F884B9CDA635138EB202D665EA2FDC1D73C133C8FE87CDC399BF4G50FA3710F9362F04EC31D18A49427A4EEAE0B832B72A819A7BEBBF1EAD72B5B920B749C1B960051710217BCD7E2DD13B6CD6CD1BF184900FF14A8D0693B271F901DD04879AD6AB977D853CDF52F5CD
	698A6B72F9D95715C0CFF4FEF1GF9898D8901A1E32F07C238D7AF6C718640E4F32F77878AAD76552A2AD74341F6749C43CE9F6367F18A0C4F3CDB687F618E4C2D56186D6F70F6345F5DA1F407E3EED5CFB7971BBB4DB51C5FB07D689A110BA2C791B6C778719C4F92A1AFGE8CB947B5EF631A3E67CB74F537BFC8DABBF5F649AE493C9EAFC1B1E295A5B747D2A3DCDAFD0C7365907D4BB1B253E47D45C235A9F307840F7D40C079EB5901039A31AD39865462F3663BE140022FF21F8FBE47D3CB2A8037DA622AD
	FF4AE3EE845EA7A70AB8597F3E08137B87973295DF3EE31BCF4D18DF6FD817C8361C1D980D13445372A05A036C137DA0A8379A8E68047FAE0F6786BE646438D74F4E4339256E810F0F9F961F3403B0BFD2E41F3742831E2FD7717FF4181476C95724C8D5289FC4D64575D165465EE6FCADAB292CBCD592A7332F708C277F255BC239F5A452A74AAE1455903F87EF04B93FE73B3BC3327E891C4EDDEA239A291038742B6851A6EB5D2A66173D1C61AA29DFB7C81E615DB741EE372249AD01E0D830878C9CB16C3C10
	7DC87F9B6721459F74A9900243F4D47FFFB2F3D41C9917F454F57194B8B3G1A2608B8FA6AAB6C97F154B8AC5C2A4FC7E68AFDE9D350865F5DD5E4312B4EA9D40B6EB07BBF9A25DF0B5BCFFC14EB8568CF72D7E19952A08C641F4A83A21751F64774DD10FC61E80E64525E04274FF286903EB2E4EF5F35A10C7BD7CD7A47C3EFB04CF508A679E30604B4031A6207A3938FB523267E4189EBCD3D81760175F74DD410A9G4DFD7DA45E87442DDD27A21DE597C55D5D89ED9DA0E9AA51DAB51564C26386AF09F18D77
	E4B95B8174FB6C461D3AAC46AD057E15B663565F03633A4C1A395A448DF05C3FC1D01E35D954821F95D86BFDEF6808EDFCC128C9B26E9512E2EB240DE1246B853C1EA46C6D6110755CBE938A299E05FB052133AB3AB57690CE563741932CBB847512D70E883DDF5511D74CE18D34213B4565A759701C1CF4A052A2F7BFF728F6558DE9741F40F45CE6E83E20487DCC48612F9FD96F8D166FBC68F8B30E9C920C7D27D964508AB5B971F42F8BEBC13B3B8F8FE7322AA72CD405F58513E5C917389BDB6FBA0EB76499
	BC3D14A46E91FED90576DD726DACE7BDC09BG55B4EB79B90F6B7682AE8500FCB07B385587D7FF1BF2A3B6AD4BC9716A63CBE89337984D8D19BFD9361BF4949301049C67961EB656DD39E4AEEB787B965B3B0D041D3B9CC553C8FE8D5322F2EC7BFCF12E97007617FAC96CAF81462C18A67677C7553A3F6CE9343F1269E2FF5BAEC3B8E4379EF4C52D58F3D92CFB8CE87A260BF527C66C561DB7DD1C55DF9C357A520A3587627AD266F45A734A69313E343A9F757F9260B7CD973E74F3FD1745174E0175CA019E08
	FE42BF7EB36C24B8E734FF9B4E95F10E09F3E14FG72DEB0834E8500A600CE009BB3449A632F0DFA07519C99618E2321C15C87A44F20BDE54C086656CF427B6A99C3F3ED2A57BD55A86B75B328DE5FEE62B50EAF38B70AA75D7CBB7785EC9F033E14FBC5EE7B730B31779179778DDD2FB426CD73B68E1BD79553EE0D19C7FEF020F42F70F30B5B38DC51F4067C393ABE4F18633DCF5451D787F1EEB234B381D6812C87A818893A81E81BA9F412F54DAE3E8D55C93C60962D4DE81F8BAFFDBD79AC9BB6AFD6BE4119
	F67269713DE7AF9F11949971AE280AF3AAE12650257F725763757A30F9313C2E086115F878DE443C531DA9F43478BA3F7FC8063812AC74D1533F5D6207ED2A9250CD9F7CC9BF66962A2674A881017BD70E73289AAF908837EFA0FE6D32FF403EF6B9BCE0DF3B548C0CDC0F578F442BED28F6BE4F4F1A9A58DFABG61FE5D0838AB5056CD5C35A6444D00F606091B5E0C38E7213DBED968CD29461C094D4AF26E85E8GE8878882A44D92E3A2FCCC66AC4A05D64F22DCA7FB9665B2B466752AABB1FA43795C56E659
	576BAC8E5E8AFB0DB102AFDAEBF7F56CDA5B0D671BF1685CCCBDAFEAEBDC83F5370F4F7B5EDBBB393EC2A1B9449D97B3G236F977A9EE6D3B77E3764171928B376DD5AEA0CE956D57791536E0F555B5D45964F267DD64C9EAE9B320F70E0FCDB3CB1E8EF0B378D22FCC2EAD873483176B6E930586C0BC62296DD7663B6E84CD4BEDF17B428FDE3675F4B67D3DF3C79F3876359B449E86D90999DB5E5746360C8F76527EDE4C47466DD36522BB569BD9D9909DEFBDC19E7DC4773ACE2361772760039DEDF834AA728
	495D3286959ADD8F30145968F363383F76423BBEDB64BA79EE0CB1BEE9C08DC3B02A1F0363463A2D354BBA981B8F7812B944934567077B513E3BB5498F62BB9F14BD2CA885675E59A4660E0171C9GCB811E8558B8C72C3924996742C4459F76F38E093FFF64B121936874260831D9FFC19B0B5EA634CADD106BD25F938D05C37A4C235368FFD941335E286C79C442BFAE6F0C624D73047A5EE9C0BF0A2D44E9D5A163BD5B0AED4688C2146BAD293666FAC9A9146BE52608DC700923F67DAB4D7E47AE5B752FCD91
	7173D31E97EC0476667B85AE23DE4459829377DAD0602E183897A3E6BC869C1B0B5F1715034C5C5F4B0DA5708C487DA5E6325B84728DE005DADADBD7D3D1D27BC1CD99652CA97D561C35FEDBF95C1C35E9AE59CF0771DC8796CC36F4644A10DC8897F439BE5694EF2354534AAD134E59856EBC03F662DCD15BBC67C69DBF856D35G59F3C5AE9DB9A86A317487663256B19F5AFE47DD5BFE50D110CA7C9CCB0D665249D00F511A0FB409B503501F160A38971A2C3AC1197D50C139827597B6DB7BB15E9CE0567E66
	A17D486F63C366778F69C7999DB266535E3EFFC06CED5377B3186F514A385F28B798FBAA1AC74F48B36F324CAD69392473C925D11DBFDDF5B3BAA7FD7D2A3B185966E65CCDC8EEAFD60B3C35F51E78761DFDD460461B38D7AE8B5C72F942673B6A715EC587EFA6BE534B104F5279B87E1F6A1BEC76E9151553829B01E69E4C5BE3746D0577C78CD9913D2DDF0DC64FCAB3B8FFA83D7170FEF2883DCD115168ED3150CB0C21B74FC20FF47D4CE52BFF955C37471193C7579973C9474B67475604338F230CB7GBE7F
	81110BAEB9D88E4F8BD25F70DB8FC036E9EAD05674814272DBB4635CCAF75F6AB7E32A47DA1A50CFAB9E20FAAC47441F6378FA8B1E6A349F3B453709A668CBFBC064554F14236D65AF48F216821C8668G08GA4AD90FBFB7DE08D9B256EE9D7032D103878ACE9AF6DAF33696BFD93FFE20B7049257F3008E5CEFCA5A75E7DCF5A8263B79946336069F7B91E7657D5097B4B063E2234ACE7BDC09B009760C61A585F0707AAE8FF281EB33AAEF9FA79AD18589CDECCC551F8B8535EDE2E365BDBE29A71B1AFEDF84E
	C173FEFB54EEDE26B1FEE55CF92F8D2B27F84CB546EF341977BB3EBF464C9FBF318D9F8E47419F0F8B1F61B514A17F9772057C77AECEE5CD4F3F1273FE9C7967ADB44E2105D1BFD917111BEB37B62D338C4D4916B76A7F97CC258D7D5D0EA923111BEDA84338EF6BE32025CFA0F41E6D6A03A435DD6AB167249EC61E4C7C14DC4F68DBF4984F857C7623C8BEE510279D67E1E0200764FD96C30F78CD08657D85F73CEF4D5F84509A3F79BFDD03763F3ABCFE3D7153F27BFA6345F27B5A771765A3FF377BD5F97C6F
	F63FA937FE37A33DA75522EE9241265281D6812C8348DEA8FCB621628F0C7B2C0D3FB6FBFB305E41DFF668661DF5E57C3DEA15237F9E64FAA55E05F7293A2E7A113E400F2D643FF3D2039CC9380915A8A3CD696955E928011FDC09366E133BB91AF2471C9A4495AD4AF26EDFA472496E9A11CF56183822DAB1EE0C098B570AF113CD5C56CEB1EE0D096B6B9463DEDAA4627EEB4750206B97C36D8D9081C8841887309CE09540BA00EC0002F4B89BG9AG5AG3CGBAC082C0F23A5047546A32F83E89C8A37964CA
	51A46BBDCDF2EDFC3D24560E2E17F4AE83996459BE90B4EA5207EB795D8F2FC7443847EA8BCCDCCB28CD8DA9686D149F95578C4D0FFE76817ABA659FFF2D99A97FF046C01EA57F40D8198172A9B45E09769B75D15A13EBC7231DBB826D6569145FC5E9BF7947A86D311D23515EB2826D2752691BDA0E2927B76A626BE9FB5D68FAFA2B4E5EFF76568D771F5F57597B4F21BA2B7F902E228E796C817B6AB0479DB1F197CD5C871CA6623ED03CFA2F09EF60FCA17ECFB2AEA8700D1C2FFE0E6BB0F09FDBF0978DDCEB
	9D5A9AEC39D7B56F94BF2FC3FB6AD6FCBEEA7F190FD182E0FA26BCAEF19E1520E4617BCB93F7515469AFDC4CFC5F5690FD2F443416516F1F3D3D4D202874D7D8EEC8E33B07579BE76E7A517369DFB695B91823053D514B79EFE1397DA808C890348C624879CB68AB061F4D0D164E3656157815G447290D05DE9D2ED615BD8FB2D56657CD92A5341B683C9D2EB8B5B0C3A814A9A2D608435A2FDFE0507DBC3377F83DF8785344D405621D11A58E7AD58B7F5164F2FA3CA70BE42D52233BC3AE540575D4605C10938
	B1F01518C450570F83607B81930B167F3941CE2109FD1E51190379A51EE1147F147D5DDAA7E4CF480FDB07BF3B04721E17164456892F76213D6EFF886270C366F77F3ECA96BF07B96F17FCBEDD72DDBD9370E2BE53B8204B16B3348F690744982353B8C37B106FBE4B1C7F85D0CB878833B849A5F291GGF8AFGGD0CB818294G94G88G88G6B0804B033B849A5F291GGF8AFGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2C91
	GGGG
**end of data**/
}
/**
 * Return the CycleTimeJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getCycleTimeJLabel() {
	if (ivjCycleTimeJLabel == null) {
		try {
			ivjCycleTimeJLabel = new javax.swing.JLabel();
			ivjCycleTimeJLabel.setName("CycleTimeJLabel");
			ivjCycleTimeJLabel.setText("Cycle Time: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleTimeJLabel;
}
/**
 * Return the CycleTimeJTextField property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getCycleTimeJTextField() {
	if (ivjCycleTimeJTextField == null) {
		try {
			ivjCycleTimeJTextField = new javax.swing.JTextField();
			ivjCycleTimeJTextField.setName("CycleTimeJTextField");
			ivjCycleTimeJTextField.setText("5 minutes");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjCycleTimeJTextField;
}
/**
 * Return the ImpliedDurationJComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getImpliedDurationJComboBox() {
	if (ivjImpliedDurationJComboBox == null) {
		try {
			ivjImpliedDurationJComboBox = new javax.swing.JComboBox();
			ivjImpliedDurationJComboBox.setName("ImpliedDurationJComboBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImpliedDurationJComboBox;
}
/**
 * Return the ImpliedDurationJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getImpliedDurationJLabel() {
	if (ivjImpliedDurationJLabel == null) {
		try {
			ivjImpliedDurationJLabel = new javax.swing.JLabel();
			ivjImpliedDurationJLabel.setName("ImpliedDurationJLabel");
			ivjImpliedDurationJLabel.setText("Implied Duration: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjImpliedDurationJLabel;
}
/**
 * Return the OffsetJComboBox property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getOffsetJComboBox() {
	if (ivjOffsetJComboBox == null) {
		try {
			ivjOffsetJComboBox = new javax.swing.JComboBox();
			ivjOffsetJComboBox.setName("OffsetJComboBox");
			// user code begin {1}
			ivjOffsetJComboBox.addItem("0 sec");
			ivjOffsetJComboBox.addItem("60 sec");
			ivjOffsetJComboBox.addItem("120 sec");
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffsetJComboBox;
}
/**
 * Return the OffsetJLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getOffsetJLabel() {
	if (ivjOffsetJLabel == null) {
		try {
			ivjOffsetJLabel = new javax.swing.JLabel();
			ivjOffsetJLabel.setName("OffsetJLabel");
			ivjOffsetJLabel.setText("Offset: ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjOffsetJLabel;
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
	getCycleTimeJTextField().addCaretListener(ivjEventHandler);
	getOffsetJComboBox().addActionListener(ivjEventHandler);
	getImpliedDurationJComboBox().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LMIExclusionEditorPanel");
		setPreferredSize(new java.awt.Dimension(350, 360));
		setLayout(new java.awt.GridBagLayout());
		setSize(350, 360);
		setMinimumSize(new java.awt.Dimension(350, 360));

		java.awt.GridBagConstraints constraintsCycleTimeJLabel = new java.awt.GridBagConstraints();
		constraintsCycleTimeJLabel.gridx = 1; constraintsCycleTimeJLabel.gridy = 1;
		constraintsCycleTimeJLabel.ipadx = 9;
		constraintsCycleTimeJLabel.insets = new java.awt.Insets(37, 55, 16, 4);
		add(getCycleTimeJLabel(), constraintsCycleTimeJLabel);

		java.awt.GridBagConstraints constraintsCycleTimeJTextField = new java.awt.GridBagConstraints();
		constraintsCycleTimeJTextField.gridx = 2; constraintsCycleTimeJTextField.gridy = 1;
		constraintsCycleTimeJTextField.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsCycleTimeJTextField.weightx = 1.0;
		constraintsCycleTimeJTextField.ipadx = 89;
		constraintsCycleTimeJTextField.insets = new java.awt.Insets(34, 5, 13, 116);
		add(getCycleTimeJTextField(), constraintsCycleTimeJTextField);

		java.awt.GridBagConstraints constraintsOffsetJLabel = new java.awt.GridBagConstraints();
		constraintsOffsetJLabel.gridx = 1; constraintsOffsetJLabel.gridy = 2;
		constraintsOffsetJLabel.ipadx = 1;
		constraintsOffsetJLabel.insets = new java.awt.Insets(19, 81, 14, 13);
		add(getOffsetJLabel(), constraintsOffsetJLabel);

		java.awt.GridBagConstraints constraintsOffsetJComboBox = new java.awt.GridBagConstraints();
		constraintsOffsetJComboBox.gridx = 2; constraintsOffsetJComboBox.gridy = 2;
		constraintsOffsetJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsOffsetJComboBox.weightx = 1.0;
		constraintsOffsetJComboBox.ipadx = 30;
		constraintsOffsetJComboBox.insets = new java.awt.Insets(14, 5, 10, 53);
		add(getOffsetJComboBox(), constraintsOffsetJComboBox);

		java.awt.GridBagConstraints constraintsImpliedDurationJLabel = new java.awt.GridBagConstraints();
		constraintsImpliedDurationJLabel.gridx = 1; constraintsImpliedDurationJLabel.gridy = 3;
		constraintsImpliedDurationJLabel.ipadx = 2;
		constraintsImpliedDurationJLabel.insets = new java.awt.Insets(17, 25, 215, 11);
		add(getImpliedDurationJLabel(), constraintsImpliedDurationJLabel);

		java.awt.GridBagConstraints constraintsImpliedDurationJComboBox = new java.awt.GridBagConstraints();
		constraintsImpliedDurationJComboBox.gridx = 2; constraintsImpliedDurationJComboBox.gridy = 3;
		constraintsImpliedDurationJComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsImpliedDurationJComboBox.weightx = 1.0;
		constraintsImpliedDurationJComboBox.ipadx = 30;
		constraintsImpliedDurationJComboBox.insets = new java.awt.Insets(11, 5, 212, 53);
		add(getImpliedDurationJComboBox(), constraintsImpliedDurationJComboBox);
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
		LMIExclusionEditorPanel aLMIExclusionEditorPanel;
		aLMIExclusionEditorPanel = new LMIExclusionEditorPanel();
		frame.setContentPane(aLMIExclusionEditorPanel);
		frame.setSize(aLMIExclusionEditorPanel.getSize());
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

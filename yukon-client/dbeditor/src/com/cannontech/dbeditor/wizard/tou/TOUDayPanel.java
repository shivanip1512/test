package com.cannontech.dbeditor.wizard.tou;

import com.cannontech.database.db.tou.TOUDayRateSwitches;
import com.cannontech.database.data.tou.TOUSchedule;
import com.cannontech.common.gui.util.JTextFieldTimeEntry;
import javax.swing.JTextField;
import com.cannontech.common.gui.util.TextFieldDocument;
import com.cannontech.common.gui.util.ComboBoxTableRenderer;

/**
 * This type was created in VisualAge.
 */
public class TOUDayPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
	private javax.swing.JButton ivjJButtonRemove = null;
	private javax.swing.JButton ivjJButtonCreate = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JTable ivjJTableRateOffsets = null;
	private javax.swing.JPanel ivjJPanelTOU = null;

class IvjEventHandler implements java.awt.event.ActionListener, java.awt.event.MouseListener, javax.swing.event.CaretListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == TOUDayPanel.this.getJButtonCreate()) 
				connEtoC2(e);
			if (e.getSource() == TOUDayPanel.this.getJButtonRemove()) 
				connEtoC4(e);
		};
		public void caretUpdate(javax.swing.event.CaretEvent e) {
			if (e.getSource() == TOUDayPanel.this.getJTextFieldTOUDayName()) 
				connEtoC5(e);
		};
		public void mouseClicked(java.awt.event.MouseEvent e) {};
		public void mouseEntered(java.awt.event.MouseEvent e) {};
		public void mouseExited(java.awt.event.MouseEvent e) {};
		public void mousePressed(java.awt.event.MouseEvent e) {
			if (e.getSource() == TOUDayPanel.this.getJTableRateOffsets()) 
				connEtoC1(e);
		};
		public void mouseReleased(java.awt.event.MouseEvent e) {};
	};
	private javax.swing.JLabel ivjJLabelDayName = null;
	private javax.swing.JTextField ivjJTextFieldTOUDayName = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public TOUDayPanel() {
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
	if (e.getSource() == getJButtonCreate()) 
		connEtoC2(e);
	if (e.getSource() == getJButtonRemove()) 
		connEtoC4(e);
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
	if (e.getSource() == getJTextFieldTOUDayName()) 
		connEtoC5(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (JTableRateOffsets.mouse.mousePressed(java.awt.event.MouseEvent) --> TOUScheduleBasePanel.jTableRateOffsets_MousePressed(Ljava.awt.event.MouseEvent;)V)
 * @param arg1 java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.MouseEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jTableRateOffsets_MousePressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonCreate.action.actionPerformed(java.awt.event.ActionEvent) --> TOUScheduleBasePanel.jButtonCreate_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonCreate_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonRemove.action.actionPerformed(java.awt.event.ActionEvent) --> TOUScheduleBasePanel.jButtonRemove_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonRemove_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JTextFieldTOUScheduleName.caret.caretUpdate(javax.swing.event.CaretEvent) --> TOUScheduleBasePanel.fireInputUpdate()V)
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
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GAB11FDB1GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBC8DF8D4453578C0099A91B488E25008D80286CDA0A0AAE0CC29450AADFDAFFEC51B365872FC74AB3634653D07AD2F5CCD028465A7C294D45408A8094610C0549829A464078496326C9FD10164E677A63B3059DDF66F1284D15EB9B3F7665E7D4BA6D11A6FBB39F74E4C1CB9F3664C19B3E7662ED078D652DADDCE21A068568A4A5FA7BAC198FFC2909E7FEB62BB8C3742132993B4FFEF833CA4541D49
	4304B7D3177AC8262EC0986B4807E4A124F7BD1C295B866FDB8547713C63080C5CCE36C190EE7F67CECF7F6DF4FE4E5AD9C75266239B603989E0BB60768344F931FD57516D8A3E9D521B14F784DD0EA0541F43B2352737EB708B15F25BA12F94205807E5C69E5D21644783EEB240D200D567E85982A8331EF0F7E5F515D26E6418E4E165CFC665EC4436B47499F8FD34EE0272AC92DE49C1C1648AEFD47F931E7B8DE73BBE37F93A4BEC225B6DF14B124DD1E66F106CCE5963AB6BF1DE90FD76B25993282EB637D8
	CCB607E48F38240322DFB208EE498575737C48F75769FCC2937A856D0CA6385F1CC6D9B541FBBBC02F1F717E730D7B955E924EA68BCB6E38A2AA6FD9FE4A73AA3F4A7BEC1F6E79E4996927123140FAD510768250FE95D7D68E32DF75E29F698E6D4B2A232CAF4BE556174F4C58562EG0CB900B94070B996443BA0BD82A0B1407A784F5D2C0F2DDF5FA72C6AC84B599C250F69815A376C005A4738F9265CD7093E7D65G6A4C767350AE0081C086C8GC887F81D6879344F5F07E7ED07DC67696E7638EB3D5E03AE
	3B6F2847AE39603D3A9AF2941CD132F9FCF6C1203A6CBADD9A019F2E1F6703759801766D0440F4AF45F80FA04BD308BE0576AD7D0951B9CB949AAB4F2BFDEB6AA1FA5E06F2AB04F72C77B26A8941CFA078F88D1E0E75FF92DD42F4866439FA191CDBB7B1DE74F91304020A99B96BA270B2BEB3B9E7FB9F65213CCF65054AF8E1AD4A78A460FB81C683A483ACGD85647E47C3F1F6F0BA563E650B11FDBF419829DDE51A79F8CB8DDF6492748FA56517EE49D89E83D79032EC765123CCFBBC659B326C714CBF67238
	1C3C8BD49EBBAE28F221ED7FDF543677DF88570DCDFC8C6FA5F345F0010EE1AE475FCF706D9ABC9D5BDA024734977232GE87973E4EED5FDCD4B2F67F406933CD90327F472899E53DE484B822063FB268EEDEA55C51837GAE0091G1381B2G32AE32B1BEF8E04FG6711C1B33E346E5F8F3C82CF29D776094DEE3FAC3AED12D574B9458E17649714B2F9B5A8570E20FA6BEBD077EC5046A149AD79C45969F183F531DB82E57253B2DBEADE42B2BE09641A1D5D92B4506D95985D57EA364253A57AE50B57AE4A12
	DA02666F2CC1191C972DC09188407B2EEF089C094DDE897DAF780649EADCBD629C109E768D134B441AAD70BC824AAD392A2B6B1DB6E4C37475B19D3D2E2D981E89D03E725BCCDDB64008CB193A7CE130B683D4DD81B286107FADD3D7B81C4D5BBF36B27D2C5FB8CDA8FDA2AD22FEA20C9ECEF5ADF9F828FEA611F69781FE85C04108CCDDB90081408BB09AA095E091400A91EC7D7E4369249873877B415E1177014C392CA865D09E58E714C7E89E4A861FA8A7148D1F135FB2DEDFE90B45EB2E66FD73G785CC468
	C646238DFFD919BF21F99B15670BC49F02ED94551B350DB8778A2E84DF86C0FFA5531D45B5C5F4CE214B40E6D10B8FA600E4D7B50916FD22967BA01D175CF23D64155CF6F871ABBA1CDD3BDE1DDF1D9E2E7B3FA5F83437F5B0817C9C3F2C76A50A879A5A594A72D710FCD116FD4E0E00AC19A4D9F63A3B547A2B88AF7EA0D36ED77A3A659076558EFD3C88904F7BFA45412D1AF92277F84E863CE634G4AFCABBC06F3AB894A4F3E120D71CCB37A1759BC7D90C92B79074C2DB146936B8EE6FD88DFE35AE33611BB
	C47AEA9B97AD85821D5F398AEEBD4F3B1A642D04FE6C38AAD3D78F603A0A75FF1325D5D3EF13266ED0798967EDB7E9A3D719835FF79BD4B7643A683A7DF5DDE45D3E22AE32EE0F2C6BDFF72F2F0B243BB46F26BA6C2327472D198B375421FD37F95CEE09CCC8DEC5207E749F4D588F275BA91FF05A3BA459643CA089421C2B083FC6561084F8CF3E0A59761EF718ED5FF5E12A1051969F51BF9CFF675DB942C82A3B05A3D55BCEF9CA2C47B9A63924EE90D41DA76016A9FE92D91F943CBF17605E7B60C62CDBFC18
	CC36A60F4BE9977B7C255D1E00DFAAA37F8DBE496F176C45E74473E21958A3171101A8BB0AF98D78DAD27461071F97C55A61141E89225AF6D4CBCF9F1560E7A55612F2017277CC47844A5F4100ACFB5CF53810CB25A211F599FD98A4DF2747579D4EE2AD490E492316F2DBED8849D0AEB5B4114D8AC161F3553EA0BE0DD237677CE56113D29EAC1F7B981F773AFA147106B9536814DCF6E5F4CC36E360B8155A406A4BE564BFF50888333DE57E9E18A68A6DBA4C0D45EE2753A7B53B3D81C6861828D4F818FA72E6
	0D4E99A137253353AF495F335AE9888FCE73182CC6FC5097A2ABCDEB7F82E9B1BE74DF0C7EBE6545B66C5FD1CC14B71DE94C77BABFD5663E6334D4E4F7DD47186FF5DE0672B764692870B74E603DBCBA46990D24DA5497EBE842F52292560FA60076114C97FB33817319AF26B81CF59E170BAEF11037F8A42EDFCF3595EA56245E11B426B79F1EB47F2B76C879C5F1ACBFBBE2FEA54FBFA5C64A5F445B3FBAC67E4C9879CF47485F5851FF7E27B172252879D44EAFB6226F66174E85A4587262DCBCA27531FDEB63
	1610BC2B688A287B61579AF1BF61527849943F7DD064357F4DC3689B1F6574F79EFADE40FDAD25C9FD15D491E9FAFD4EEE58D620632F44DEBFB8260D3DB64178E01CB68E1E09GD40F7F26473A9A3D93642B316EDC8B0B4926C35905GAB147A25D7D3D950F2432C2CDC862497832C3A1A162B3AC6DB2E7E84ABB78C70632FD14B4D8EAA776DA9D6CE38B6D3379D206ADADACE3ED60969DE0B6552F86CF58F47ADA038BF01A3BBB10F45040E96E8E346F67F6408FE9999FB057E72EBC3E3E5971A116E06F8705563
	59DCCBBC245D77547A7D9E1B1304AA1471BC52247559A84E507482BCE1DE7B65E6F006FB19CED81B10D70CE5A8E8E5DC6F7EGE35521311986924DE55B4A3A9EE2BE4C1EBA0F9D9CB64F59BA6E52DA1D927A8CB43E7BD2BB13D7F9BC73D93FE954462467EC3EB96745A832F144D31944452B32514FB365D25AB7F05A8BA01F62360A8CA7DC47E254B7D7237D0C942336118E16F5851CE58159098197D196097123BAC9E90EA5FBC924D5F59D0BC5BFFC9C670D9752F1GC9GB4AE768C090BE1595900DB81C06704
	55281D934523E05F8E608598B10A4D0923568F61B928B51A0F59532F21CC3F4B8ACD47E652569A05470577CC9276DF3A90B1169E37F9C4CE62A8BAA673C729E3137444415CF4GAA0B29D6A60B026BE14D381E5969A40B564E8D83FCB240426B199C8ACE6C05676057DCA60BA381A20B6F6090D07E3BCFB07E17GEF5BC7B37ED719A371EF984D742D2AD8537E1096FC5A7E14D32CFDBB50CE646DDFB0EA5B4F9ACDE39C2BC7B379EDBA0D36E910FE00A23B79A5DB282D88718178F944DE2DDD4BD8B2BE2ADDAB98
	C375E25718D03BF67A884E279A401B47B03B76732617B4F64D8816C4F4F741AA45EC5525E66C9B2CC7CAFAF8B336CB56A3014596DEE99FCC7C5EB10645E1EF5123BCAFC2FABCC0AA408200E51C37978E47BC0FE8686D67BCE25C20784A3F0145E3360B03293703571BF0AC34DE798DEA5C2A7E067098960D75FFE4C4FDEB3D01467A57F1FCAD41FBB5F82A1FBFA6B69053F110371C373F5C925A3E614660B6DDA1E9DA6FEFE1755E8CC90F8E29C7F5764C57E8DB09FD5728F8F5B55D7FD07BFB562C353F49B752B9
	1B854FE5B73235E314B1DF61C5C8E0F6BE4EC44EC5A0EDGF0A4B01D79B3B9BB9E8669C400F400C5GAB818A4602CFBA16694E9707A32D25413A93EDA1D56328D4BE77DA07A65739E175C265EA9E9BC92E1BCD2FC616AB35EC82BBEB9F5321152DEBAC35C72361B9F9AC136DAFACA8DBDCCB660CE5FA725C492175E75D29D87DA93AA9389E65E18F67216AA6464334B0DA8363614130FAE5A169441BA249745D629DD1F4156D67A82FE9DC9753EFE2BAD75102386510DEFD93532FD907713C0059CD0347691685E6
	B3EDC750E777781CDDCEB743B9884E66711120A40D15FFC87C288D63B2F54563985DBB7451E328F769A347D0D37511E32833747D1F8F4C53C70B31D27E6E617C6D872038BDA24399B8AE0D78E9F6C877F25CA8836292A11DBE0E0D79E7441EAD0674AA004271BC7ECF70687B170E277601629FBF9EA9E60F753569D563426DAA520EE537E99B4FC4ECA374ACE0ED147A315AF80350FA74154DE49C4097749323035E2E1277DF724BC20438B4C6948496FBF8077808D826C17654D5F01D6EEAF8A332CE1BECBE0F4B
	052EB41F03F718F1BCD0E77563190D3D1D2CA32910DEG30040F470DC61CE32BA03D6166F0B996C5405175EAB5315915B75375CA4771EB4959F70D86CF5731D1A6F637C58FF929B733B97077235167C04E3148F3A07F98764D6F8978ECD2281EEFA67BE21A27DA79E6D96AC6B1AB7BF1D2DF96FDDDD258BCF90B5427F9516A979F0BB6171478F850986E65E3785712A8EB28662E4E239DEC6C8AA73031BBC0E45A0BE9006489EC6C32AD217A1C81F9D9936818ADD31E2BA7840FE141AD61E33A8BF0D5A1F85A1772
	D35A3ED47239D49956369E6A37ABB4E4659997C2B3B1C25B29001B9F316D25C1EDFF445B1EE58CDD9796EB6AAF07F7A1B1189E9D370FDBD07FFAF93A1A247B7878C8351B7858B7386D0A21277107FAE29F77C141DC9D437D0860AA40581F92982E11584C2F738461619C560F8547F0BDF009FD1EG509B16C86E127178CDA124CB814CG0EC43A2ED3FE52FAF04F5E69933BC1CDCCDE49A61C1C08F56FB4303A8B20FCB640EA009D1037FFA25B1FBC66C0FFBBA802D9AF76218E537C13E4CDBB2C97BBA417124165
	7EF3BB4A1DEAA55493D86C6CF33DF6CFB4F9A265F541C4260F497C5CB59BF0ABG72EE0DED1B37C7A8D38A381A90BC35AFAF137543FCEB68DD1C37885EFEEB68DD1CA94759DD9C9972965F4A562C3B485A3F7F36CCDD6BED8C370660DC100E63B80B112DED093731FE6618852E0BAFEA5E93CC314E1D3F8BA83E543F200D1FF6E85B4800FEE75D260E45325BA20F696A887802A410E3D224F11CC1ECDAD5D268BE68A1FA57AAA9F49F54E0E67BA08764ADCCE263738F62EF165F1E292B3F1D618EDA91578AE98747
	7DBB41DD04F48247CDA7F513A1BD1F63DEA06596C3FAA547F5DA188E64CFE2380AD65286243D9CE7653C0C60385BAC0C17719C372A15711242F1C6AB63A50363D2B9AFD99CD7E2E53CAC6338959646CB559D8C77B851DBBD246D9C7793B32B9B47F14B8E303A0B6FE07135F5ADEC2DDE0E65A6B3FBD4519E64D7E3E4253A7A104FE9BFA8F66909F523EBD88B7121317E4949EC8D7B0650CC00F48A407C49ACCE597B9E0BD3267CE012209FB6BD625D59A5661BF332A7D3BD926ED4E39D09BCCE79180135399F7265
	BB91771CC1EB2F5086B46452BD844D7F6038B69F57F4DC97547CD3C179486F33B95A7C6B0372D1C61FA975E95F9EFB1775ED75E32902EBE6D254FB41F314BEADD11E85E95B047C783ADC45EF680C62B7906BCD7B9F50E37FF13D587E036071B6C0DA6638F7082E44C1BA15633EA5387910DE4CF13693CC77D6F3DCBD1F8785D3986E8602EB07F46F94E69FDFB76239C6BCCF3FCB52F84E20583008FEF8A4C09AC333E10F8C380D215C85226B6443C6FC97833247A8791D97A40CB8884252A9649C0E4CBF540314A9
	ECCDFE4504636F75C91D124FA759693593257C9E9316AFCE4E54D58250F55E4563596D00138192G52813212795CA77E62CAC867CFE5382B0A91D78A699A0EDB7BBE33B9F60E5B79BA9B63DE0E7B1FF3EC0C63B9EE0D1959086C29EC8C695D5795105EF0976CE96FE2789F91BC1E8161F991465FA97EC83FF732980DD0BC529F78185B7BEDE32D90F99FB7507127E358CD743D5BADF5FB5CCE9B9D3B5D44A60F84283F0B0D75AC8B6A3C8C6E97ADF31614B1C3FEBBAFF3279967EB37586B6C8EF493ED20E57FCE4A
	76C239913C2C530A3AC383759866925DF62EBB3F34B05A293C7C272FC75735FD047E82A81B85D0F4B7314F167A98FD5702GF590F0BF0B762C70EEF53FC75BDFF300744D698E695B17E46DF3C0F9990062C6907E2781A4F15CC202CB05749C0EFB7B8B44AD02F4B6477D1B15154BCFE138A789EE87240BD3D87FEA2C7D75EFED0C7E19D3547E51B6DEA5ED34835E1542E43E5408FDF6B83B9CAEG99F6155457DF4D6771F8A81B1A22FC8312C24FC0A9FFCF1B8743DF41B415BF4C5B856919C17CED2BA13E886077
	CFE3F3F0E34B506E6C3FBE44FA66C15723BEE00B81ED08E3DAE8EC21156065E9213105615CDF9986F9CB2731D85EF232CFEA3A87EC9E40C5008400F400C577307843C3ADF837285F5831596355CB67A517A62CC67B3748B034EF999EDE477C0274876E92E6DF559E75ECF669BDD4D7726ED56E0C5F3B0D60E97FAE913D2A04BC83008C908F1084B07BDE563FF504C70C005B1DF22DAC0BB687B935E61D4303E4950D1BCC5A378BFAED5F96AA6DAFD11EAB61191F8AFB4ED455BE53B97034993FD5BBAF79E427CDF4
	516054C151E770381C36BEC5B7D71375844FE14BD3595C2936E01F9C9E1F730247AD0757E4FACDFDEC0C77FBF95D31965CDFF7F8FCA410C367DC0A054D39F4A83790A0BF8D78CD23EB922595CFFC1CCA40552431F978D5CCBB38D6B39FDDD0EFD89A1D0718CEC8233AB9174BE32AA5329DFC1874A3894A272431FE5C7E8D7A20E1B74AB82DBF9B713BA86D89A3ADDBD6EBB13734999BCC4DDFB634556A75EDF5ADFA4B51E3A626EFC9BD5A73442A97EE0DFECFA2CD39A721BCAF7EF61B30EBBAFEC7786BAF844E47
	E163DB70F4CBBDC57C00355E896A039FE3946F2D7A24F45F277BCA14D8648DC66617BB26B33FBC43986A17EF8D724B7FE28C754BD106EA7EAED328DFFEA048AF371AC27DF21DD21F723F4E962EAFEF5A183EA4CD277AB29B1F53E90C1A561BD89C9E47C9AFE67516CE27F11C5570AC18C16B5131AEA33ED7B160AAE7B01C1178DEDEC8DF1C41F4EF23EFB03A178C75664CD0F5EF518C2AFBFFD833E68DED231868D8B660174DE0FD203145201BF7DCAEC1F974CC1F674DBC077722B451E622F9B46FF3AB08DF7878
	93C15115773ED2F5E5219547EEA725D6D6D636D3A953FCD2ADB345520FBED56CADAFAD5A7BD109D2B6AF8A3D3ED3AA3D2F86C46FAD254F79027AC7E9BDFDDA90585CF9FA833355BB6E3EC3287FC5FB4EBBD166CEE1BA1DB376F4F5FF3AA420FB1E52BC522E72F74CBC707E5EE2C159D6FD4CF89BAE2AF47ECAFC291DF494F7527C9FF42879072C83EF67EEEBB82D5F58D4DAEF8E08962D17A329323C2BD325B1FCC063C2577427CCACEE1788724CCAE7B1081F1E7BD4885F8B17F9DDE2E7D90768F75A2A2B8F6283
	831F1666DD643DC9631189820DC7ECA37B148DB3E10F83309FA0DC9FD69C20FDF83CCB6D4F8A075A1F37757D7567A36CCFC509628BD98CD80F3E1F563CDF5DF27963F913AEEB9B544F78EFC374F31C55064867B8B947A31FE5669F6F7FFB100263513F8759F6DC7BBD887513B21C389E18E10C3DG89G49GF3E6B29F694E63BB8562A3C5700F9A6CDDF80E04DFC04A2C7DB8E3743EDEEF0C7D5D64F8A35E956C7000196DC67A8CFF2B9147CB76F889D2B9A3B6220CFC283D3428021FCAE355D2A7C153FD7A9892
	83DA8C7DDAB6136D5D1FA3F10555105EB00B1F571AD9AC200863A6B8589E3F12635A6CAC9650B40B2D9FDBADF8A699640328EBC2193541E8EE2E2B5537196A0CADFA7D41DAA378A3A7EA3FB071B953ED79381F7ACDAD4666AFDB0E1903A9185A0EB5B454B754B3FFBF4B42EC43F860EB51ACE69B663ED0AE8C708E2259A93B2403448320FE4A8EAB3B5318FF9F0BD926123BF5B510EE857082D09BE0BA2B5A00CFADC551ED403E0A0A5222FD659F15C432AD516D4D8378B3840A3D79D5375A56086383E90B6A7CB5
	EE9267831EA75F47743C5A7481BC2D6219705B2600B478BCDEF05B7BA8165CECD656401FFD25759F8BA64465307DD728AD18FF9FDD7B965D977AED6B3830B33BD8C0473A514646BAEFB65869597C9E110D5D9715E733717A0F5DD8B68552738132E733F8F6F6B15B9F2E84DC697D0C462E92661B5A6FE7B40CA5D8B6915269G8B6FE7E35E64D547E16B6BE55147E1EFC565A715D57BBE2E7C844DBFFBAFE163F9761CCA678F677A9B4F3D15B01E9FFFB2C85D9965D3F5A74D2F363553BC1036286E5CA6239F3A94
	7AAEBCG7D87588FE0GF0GDC84988DB099E08EC09640B20055G858F02CD812884E8FA10695DFBEDA57D681DF200C00C2CCF24F7EB284EACB1455219138FC63AB774471875C26FB251784235741C7D415038C382410FF8B0B46E7006196955E848DB497B3B4099FDFD7819B3767A3058C96E1703DDB777F915BB873FF212FBC6E4C7483F95F696F0DC335F6071BBB1BAC847BBD90F7147CE30FFF4AFB00F2C8D2DF3B2F576B97C8E30B9B8C6799ED99792A0BFE98E5B274CA538B910DE40F1BFA4EB45CAC88BF3
	595E653C7D3B448FCB018EBB9B21E378D458BD45010D7D321875C2471E56FBBB6C7EDEFFD0B5177D2643AEF2FFD9BFB7D4E7BEA478563921BAB34B4AF4468EF98B66B21D395E0DF4394FC34DAF7B46550D7E8BFE2F6A94DD4E8B64D3849394F4F7A95F2A1FC7115232BBBC6FC61BEB604D9C06395F52F1C63249E6310B77DDEF9B1A2C3F0CD9AF322CEFA475CA678DAC36DFB30F497A90316366F92132B690FC7B3CD0D9BF391B495A85F90B6631B868EF0B07964FDCB144FAA5A5C30B03BEC256224A07C27B7BB3
	022FF9A8343F2DA57C0E956425BE44F4EB069C5D9EBDA04736C7BF14A37B2B0F4A617E6ACF65487EEA162C75D7A96EC999576FAE307F2D3C5C2FB96E8B0E7B9D21093893CE3B6C60785F933E905FA4E1038C7FA742D78F4135AA38159A5C978A6EB999EDAAF4596171313A7F10095DF43ADC4A9EA500E51CEE9A08D3E22A8164516995B5FCEF60B8641B5E774B583A1154F5B95DD29D7DC5942E93E3B477C6A6E85E5F09795B855F85E89B7BAFEB9B5477F824C990186F712C8977D7C13EC7A3ACCC45BB6DE04FBC
	DDBBCB0BE02BDCD149FD245FB72BF5C71AF10D5B493E3EAC420BCA55CAFCE2439135DCC9D7FFFB7172FD504027ACBE31C72F56B35B87DE4FF8946B513DD19559AF94E6E46A0AB318DDF97F04BA0E7BB46FF32C17FFCC1F382CEDA8F726084FDD897D55E730BD62D73B49B99824E5AE072345EA5F2D1A775F6C3E7CF2D875AFE8E3E1095A27A7B46F27CBAEFF5BBD17358DBAA7DA2CE4DF05DBE4014D4940B10167BFECDB4F72F19E8B6CEC66C9229F43C08F62B3180F695B9DCEEBFC0B405FFFF2DA255B327BEDA1
	221D2805854985E682F63895FBD160747790A7F5DD8262A94F8AEB7DBEE1CB787C2D9D7564AFD25A6A8BF3041CE661F987317D4D425A9EDCC4FAE1BF273B9A60ADD9D84F23497EF34237488F899C750624FC42E65EC6B3D990161C5FD71D7B3BBBF2F304BCE800AE104DC29E2EF2500E0F36178B8BA436A66042020E9C39G9FCCDB961E271157FE994054DA05902EAC3B438971B2ECE70811344D3C8C7A5BB731142E826093DEC3D0A8635866C5A8A3DCDA33E68D824F73G54737A38963F069CE16BA1E5FC4246
	20A17D516D9FBD337057D6FF2E70FC3370AA6ED2CC120FB837129DAF4CAB3F6C55A82C7762A5DCBCB783397A49E32DDF6EF3C9FA21305308DFE27BE549DE4B5CE63FAC3CCC3E7A5B03E11EBDCD225FA123AB7319DE48F363EF1089DBE836CBF4F76D21BE74E7E4F0F4356DC482B88B8460F7AB61972914F1AADB9F5C7C6E5B07174E4E95F29A0597DC9E515EA85A403BEE96564B8E1F64F7F8DC76DE6F019AC2A83F07DD484A85F22F736EE3E74BB0BA4D19BE5BBBB1FE63320D0DB9B0FEBEE18FA9091B32B2DE42
	DE6790FD501464EB1671B755BC3EDDBC6F0AB765779EBF346DE40E20D3BA09225F5E6B851D6A943B1D2EBEF8218BAD99149D206F606C397D7811A4F0D69442994B628D145D78E8795829FF7B12B420D615053728A8216A1EAE49533D273633939C9DCEG648A645F606431CCD9C819AD3DE3DEBE7C441A8E5003FAA1373B3C1C7C2FA07F77627F8A0229A0180A3DA05C95C23FFF21793D2C4FC4F978944661AFEB0491141FFA7651C93F5C3FE2B9742E91B6D2386D02C1A8D0CB9D8BF4F7C83E2BD2463E71E24212
	EBF10C3C28AA3DC83788A66E26E05704CFB58C4FF4C5C82DD526981B2085E0BFE5E1831E7796A9873E245CABC4A929AE91371EE0DF8B532EBCC43F89E3DC9FB4464FD3D717A45F47BA7872B6C93EC37E3FB7E8D1E22DE2CA9079A2690FFA1598E149A5ED0CF0551517221EB12FFCC4791D389F07FE33DAFD96BBD58F78769F333D4BB5E7701EC0347D2C29DBF439E451F53656ED473D6D41BED9D24F898E1CC57AFED6C64924F90DE7116FB31ABA7F8FD0CB878890921209EE9DGG40D7GGD0CB818294G94G
	88G88GAB11FDB190921209EE9DGG40D7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG289DGGGG
**end of data**/
}
/**
 * Return the JButtonCreate property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCreate() {
	if (ivjJButtonCreate == null) {
		try {
			ivjJButtonCreate = new javax.swing.JButton();
			ivjJButtonCreate.setName("JButtonCreate");
			ivjJButtonCreate.setMnemonic(67);
			ivjJButtonCreate.setText("Create...");
			ivjJButtonCreate.setMaximumSize(new java.awt.Dimension(120, 25));
			ivjJButtonCreate.setActionCommand("Create...");
			ivjJButtonCreate.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjJButtonCreate.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCreate;
}
/**
 * Return the JButtonRemove property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonRemove() {
	if (ivjJButtonRemove == null) {
		try {
			ivjJButtonRemove = new javax.swing.JButton();
			ivjJButtonRemove.setName("JButtonRemove");
			ivjJButtonRemove.setMnemonic(86);
			ivjJButtonRemove.setText("Remove");
			ivjJButtonRemove.setMaximumSize(new java.awt.Dimension(120, 25));
			ivjJButtonRemove.setActionCommand("Remove");
			ivjJButtonRemove.setPreferredSize(new java.awt.Dimension(120, 25));
			ivjJButtonRemove.setMinimumSize(new java.awt.Dimension(120, 25));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonRemove;
}
/**
 * Return the StateGroupNameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDayName() {
	if (ivjJLabelDayName == null) {
		try {
			ivjJLabelDayName = new javax.swing.JLabel();
			ivjJLabelDayName.setName("JLabelDayName");
			ivjJLabelDayName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDayName.setText("Schedule Name:");
			ivjJLabelDayName.setMaximumSize(new java.awt.Dimension(103, 19));
			ivjJLabelDayName.setMinimumSize(new java.awt.Dimension(103, 19));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDayName;
}
/**
 * Return the JPanelHoliday property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelTOU() {
	if (ivjJPanelTOU == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 12));
			ivjLocalBorder.setTitle("Rate Offsets");
			ivjJPanelTOU = new javax.swing.JPanel();
			ivjJPanelTOU.setName("JPanelTOU");
			ivjJPanelTOU.setBorder(ivjLocalBorder);
			ivjJPanelTOU.setLayout(new java.awt.GridBagLayout());
			ivjJPanelTOU.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
			ivjJPanelTOU.setPreferredSize(new java.awt.Dimension(324, 367));
			ivjJPanelTOU.setMinimumSize(new java.awt.Dimension(324, 367));

			java.awt.GridBagConstraints constraintsJButtonRemove = new java.awt.GridBagConstraints();
			constraintsJButtonRemove.gridx = 2; constraintsJButtonRemove.gridy = 1;
			constraintsJButtonRemove.insets = new java.awt.Insets(24, 13, 4, 39);
			getJPanelTOU().add(getJButtonRemove(), constraintsJButtonRemove);

			java.awt.GridBagConstraints constraintsJButtonCreate = new java.awt.GridBagConstraints();
			constraintsJButtonCreate.gridx = 1; constraintsJButtonCreate.gridy = 1;
			constraintsJButtonCreate.insets = new java.awt.Insets(24, 20, 4, 12);
			getJPanelTOU().add(getJButtonCreate(), constraintsJButtonCreate);

			java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
			constraintsJScrollPaneTable.gridx = 1; constraintsJScrollPaneTable.gridy = 2;
			constraintsJScrollPaneTable.gridwidth = 2;
			constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneTable.weightx = 1.0;
			constraintsJScrollPaneTable.weighty = 1.0;
			constraintsJScrollPaneTable.ipady = 94;
			constraintsJScrollPaneTable.insets = new java.awt.Insets(4, 4, 0, 9);
			getJPanelTOU().add(getJScrollPaneTable(), constraintsJScrollPaneTable);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelTOU;
}
/**
 * Return the JScrollPaneTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder1;
			ivjLocalBorder1 = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder1.setTitle("");
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneTable.setBorder(ivjLocalBorder1);
			ivjJScrollPaneTable.setPreferredSize(new java.awt.Dimension(311, 305));
			ivjJScrollPaneTable.setMinimumSize(new java.awt.Dimension(311, 305));
			getJScrollPaneTable().setViewportView(getJTableRateOffsets());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}
private RateOffsetTableModel getJTableModel() 
{
	if( !(getJTableRateOffsets().getModel() instanceof RateOffsetTableModel) )
		return new RateOffsetTableModel();
	else
		return (RateOffsetTableModel)getJTableRateOffsets().getModel();
}
/**
 * Return the JTableRateOffsets property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableRateOffsets() {
	if (ivjJTableRateOffsets == null) {
		try {
			ivjJTableRateOffsets = new javax.swing.JTable();
			ivjJTableRateOffsets.setName("JTableRateOffsets");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableRateOffsets.getTableHeader());
			getJScrollPaneTable().getViewport().setBackingStoreEnabled(true);
			ivjJTableRateOffsets.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
			ivjJTableRateOffsets.setPreferredSize(new java.awt.Dimension(287,393));
			ivjJTableRateOffsets.setBounds(0, 5, 287, 393);
			// user code begin {1}
			ivjJTableRateOffsets.setAutoCreateColumnsFromModel(true);
			ivjJTableRateOffsets.setModel( getJTableModel() );
			ivjJTableRateOffsets.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
			ivjJTableRateOffsets.setPreferredSize(new java.awt.Dimension(385,5000));
			ivjJTableRateOffsets.setBounds(0, 0, 385, 5000);
			ivjJTableRateOffsets.setMaximumSize(new java.awt.Dimension(32767, 32767));
			ivjJTableRateOffsets.setPreferredScrollableViewportSize(new java.awt.Dimension(200, 8000));
			ivjJTableRateOffsets.setGridColor( java.awt.Color.black );
			ivjJTableRateOffsets.getSelectionModel().setSelectionMode( javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION );
			ivjJTableRateOffsets.setRowHeight(20);
			
			javax.swing.table.TableColumn switchRateColumn = getJTableRateOffsets().getColumnModel().getColumn(RateOffsetTableModel.SWITCH_RATE_COLUMN);
			javax.swing.table.TableColumn switchOffsetColumn = getJTableRateOffsets().getColumnModel().getColumn(RateOffsetTableModel.SWITCH_OFFSET_COLUMN);
				
			switchRateColumn.setPreferredWidth(20);
			switchOffsetColumn.setPreferredWidth(40);
		
			//create our editors/renderers for the fields
			ComboBoxTableRenderer rateBox = new ComboBoxTableRenderer();
			javax.swing.JComboBox rate = new javax.swing.JComboBox();
			rateBox.addItem("A");
			rateBox.addItem("B");
			rateBox.addItem("C");
			rateBox.addItem("D");
			rate.addItem("A");
			rate.addItem("B");
			rate.addItem("C");
			rate.addItem("D");
			JTextFieldTimeEntry timeField = new JTextFieldTimeEntry();
			rate.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
			timeField.addKeyListener(new java.awt.event.KeyAdapter() 
			{
				public void keyTyped(java.awt.event.KeyEvent e) 
				{
					fireInputUpdate();
				};
			});
		
			timeField.setHorizontalAlignment( javax.swing.JTextField.CENTER );
			javax.swing.DefaultCellEditor ed = new javax.swing.DefaultCellEditor(timeField);
			javax.swing.DefaultCellEditor ed2 = new javax.swing.DefaultCellEditor(rate);
			ed.setClickCountToStart(2);
			ed2.setClickCountToStart(1);
			switchOffsetColumn.setCellEditor( ed );
			switchRateColumn.setCellRenderer(rateBox);
			switchRateColumn.setCellEditor( ed2 );
			
			//create our renderer for the Integer field
			javax.swing.table.DefaultTableCellRenderer rend = new javax.swing.table.DefaultTableCellRenderer();
			rend.setHorizontalAlignment( timeField.getHorizontalAlignment() );
			switchOffsetColumn.setCellRenderer(rend);
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableRateOffsets;
}
/**
 * Return the JTextFieldTOUScheduleName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTOUDayName() {
	if (ivjJTextFieldTOUDayName == null) {
		try {
			ivjJTextFieldTOUDayName = new javax.swing.JTextField();
			ivjJTextFieldTOUDayName.setName("JTextFieldTOUDayName");
			ivjJTextFieldTOUDayName.setHighlighter(new javax.swing.plaf.basic.BasicTextUI.BasicHighlighter());
			ivjJTextFieldTOUDayName.setMinimumSize(new java.awt.Dimension(150, 21));
			// user code begin {1}
			ivjJTextFieldTOUDayName.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.MAX_TOU_SCHEDULE_NAME_LENGTH));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTOUDayName;
}
/**
 * getValue method comment.
 */
public Object getValue(Object val) 
{
	/*if( getJTableRateOffsets().isEditing() )
		getJTableRateOffsets().getCellEditor().stopCellEditing();
		
	TOUSchedule tou = null;
	if( val != null )
		tou = (TOUSchedule)val;
	else
		tou = new TOUSchedule(
					com.cannontech.database.db.tou.TOUSchedule.getNextTOUScheduleID() );

	tou.setScheduleName( getJTextFieldTOUScheduleName().getText() )	;

	tou.getRateOffsetsVector().removeAllElements();
	for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		TOURateOffset ro = new TOURateOffset();
		ro.setTOUScheduleID(tou.getScheduleID());
		ro.setSwitchRate(getJTableModel().getSwitchRateAt(i));
		ro.setSwitchOffset(JTextFieldTimeEntry.getTimeTotalSeconds(getJTableModel().getSwitchOffsetAt(i)));
		tou.getRateOffsetsVector().addElement(ro);
	}

	return tou;*/
	return null;
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
	getJTableRateOffsets().addMouseListener(ivjEventHandler);
	getJButtonCreate().addActionListener(ivjEventHandler);
	getJButtonRemove().addActionListener(ivjEventHandler);
	getJTextFieldTOUDayName().addCaretListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("TOUDayPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(374, 418);

		java.awt.GridBagConstraints constraintsJTextFieldTOUDayName = new java.awt.GridBagConstraints();
		constraintsJTextFieldTOUDayName.gridx = 2; constraintsJTextFieldTOUDayName.gridy = 1;
		constraintsJTextFieldTOUDayName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldTOUDayName.weightx = 1.0;
		constraintsJTextFieldTOUDayName.ipadx = 90;
		constraintsJTextFieldTOUDayName.insets = new java.awt.Insets(15, 0, 8, 17);
		add(getJTextFieldTOUDayName(), constraintsJTextFieldTOUDayName);

		java.awt.GridBagConstraints constraintsJLabelDayName = new java.awt.GridBagConstraints();
		constraintsJLabelDayName.gridx = 1; constraintsJLabelDayName.gridy = 1;
		constraintsJLabelDayName.ipadx = 6;
		constraintsJLabelDayName.insets = new java.awt.Insets(16, 8, 9, 0);
		add(getJLabelDayName(), constraintsJLabelDayName);

		java.awt.GridBagConstraints constraintsJPanelTOU = new java.awt.GridBagConstraints();
		constraintsJPanelTOU.gridx = 1; constraintsJPanelTOU.gridy = 2;
		constraintsJPanelTOU.gridwidth = 2;
		constraintsJPanelTOU.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelTOU.weightx = 1.0;
		constraintsJPanelTOU.weighty = 1.0;
		constraintsJPanelTOU.ipady = -11;
		constraintsJPanelTOU.insets = new java.awt.Insets(8, 26, 10, 24);
		add(getJPanelTOU(), constraintsJPanelTOU);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (6/22/2004 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{
	com.cannontech.common.gui.util.OkCancelPanel o
			= new com.cannontech.common.gui.util.OkCancelPanel();
	
	if( getJTextFieldTOUDayName().getText() == null
		 || ! (getJTextFieldTOUDayName().getText().length() > 0) )
	{
		return false;
	}

	if(getJTableModel().getRowCount() < 1)
	{
		setErrorString("A TOU Schedule must have at least one rate offset defined.");
		return false;
	}
	/*for( int i = 0; i < getJTableModel().getRowCount(); i++ )
	{
		TOURateOffset d = getJTableModel().getRowAt(i);
				
	}*/
			
	return true;

}
/**
 * Comment
 */
public void jButtonCreate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getJTableModel().addRowValue("A", JTextFieldTimeEntry.setTimeTextForField(new Integer(0)));	
	repaint();
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jButtonRemove_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	if( getJTableRateOffsets().isEditing() )
		getJTableRateOffsets().getCellEditor().stopCellEditing();
	int[] selectedRows = getJTableRateOffsets().getSelectedRows();
	
	for(int u = selectedRows.length - 1; u >= 0; u--)
	{
		getJTableModel().removeRowValue(selectedRows[u]);
	}
	
	repaint();
	fireInputUpdate();
	return;
}
/**
 * Comment
 */
public void jTableRateOffsets_MousePressed(java.awt.event.MouseEvent event) 
{

	int rowLocation = getJTableRateOffsets().rowAtPoint( event.getPoint() );
	
	getJTableRateOffsets().getSelectionModel().setSelectionInterval(
			 		rowLocation, rowLocation );

	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		TOUDayPanel aTOUScheduleBasePanel;
		aTOUScheduleBasePanel = new TOUDayPanel();
		frame.setContentPane(aTOUScheduleBasePanel);
		frame.setSize(aTOUScheduleBasePanel.getSize());
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
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseClicked(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseEntered(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseExited(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mousePressed(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getJTableRateOffsets()) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the MouseListener interface.
 * @param e java.awt.event.MouseEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void mouseReleased(java.awt.event.MouseEvent e) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
/**
 * setValue method comment.
 */
public void setValue(Object val) 
{
	/*TOUSchedule tou = null;
	
	if( val != null )
	{
		tou = (TOUSchedule)val;

		getJTextFieldTOUScheduleName().setText( tou.getScheduleName() );

		for( int i = 0; i < tou.getRateOffsetsVector().size(); i++ )
		{		
			TOURateOffset ro = (TOURateOffset)tou.getRateOffsetsVector().get(i);
			getJTableModel().addRowValue(ro.getSwitchRate(), JTextFieldTimeEntry.setTimeTextForField(ro.getSwitchOffset()));
		}
	}
	else
	{
		//make sure that afledgling TOU Schedule has at least one RateOffset
		getJTableModel().addRowValue("A", JTextFieldTimeEntry.setTimeTextForField(new Integer(0)));	
	}*/
}
}

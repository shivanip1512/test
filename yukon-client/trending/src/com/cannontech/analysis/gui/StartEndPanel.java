package com.cannontech.analysis.gui;

//import java.awt.event.ActionEvent;
/**
 * Insert the type's description here.
 * Creation date: (10/30/2003 10:57:32 AM)
 * @author: 
 */

//import com.cannontech.util.ServletUtil;
import java.util.Date;
 
public class StartEndPanel extends javax.swing.JPanel
{
	private javax.swing.JPanel ivjDatePanel = null;
	private com.cannontech.common.gui.util.DateComboBox ivjEndDateComboBox = null;
	private javax.swing.JLabel ivjEndDateLabel = null;
	private com.cannontech.common.gui.util.DateComboBox ivjStartDateComboBox = null;
	private javax.swing.JLabel ivjStartDateLabel = null;
	private javax.swing.JLabel ivjEndTimeLabel = null;
	private javax.swing.JTextField ivjJTextField1 = null;
	private javax.swing.JTextField ivjJTextField2 = null;
	private javax.swing.JLabel ivjStartTimeLabel = null;
/**
 * StartEndPanel constructor comment.
 */
public StartEndPanel() {
	super();
	initialize();
}
/**
 * StartEndPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public StartEndPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * StartEndPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public StartEndPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * StartEndPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public StartEndPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G35DEE6AFGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135BB8DD4D45719ECE3F3E8CA0DFBCAFAC8621E43C6F757F3CA37EE57CD5D2E6714CDBDB9B909C9B0C5C1200AC20CBFA8ECA4A9DA63BE180119D1C4C0CCC8259B8453E8CA895158D896D3D1EC842952C8953B764040FBB3BCF8735EB07310C191773BFF6FBDE65E9B0DCDB8674BFD777B39777B3E7B5DFB3F6F0E61EA5EC9ACADAE29613862D20E7EFDDC8C7FB946F13FFE2A270B61EA1F4FF4F1063FC3G
	D5DC65A1BB34C700F6FAD7262B127BDA35937AED500F591969FA8B3E77F3332BCFFEG9F9673D482EDA43B3DB97ABC9F7433F99C381FBFDD8EED8540E94071G8B1FC773BB6E54D2FCAC74AB68B70262920E6BF881711C5AD7EB4097D23E1697C81B02F99A6E54D1FA9C74914FFCE8973F200FC9147CEA6BC95A3F7E3771DC6A3339E5FB51DC067199543EC8E45BE84BF1D299F2040DFB4C73EB046F9975FEB224781A075C322C482AB0B45C6CD7145149E6D5882A2D2D17D5F7C0BDA773BDEED91000BF69C52467
	50BEA79E0363D468931D166FED23F3460F46F3497352ACF5AA203A542468BA1DFEF4411E25A9E86C8BF74A29DCAF7463G481C5F3C4366F4AE4FF445AEC7389B950877B4FC77817817B3DDCE3D47F4693BB50FCB9FF4B876D96832ECB95121F0392ECB5FEB3263ED3C5E7FB100562C6525CCD7AF40B0C09CC09240B20003B84EEE743F8FED7B20FAD6717994395D6FBFA37101BFA83CA041F7EBABD0A86EB3E1C889709CC7E229393A49C49F8639AF4D0CA3FD6D2FF2EC6D13BEFB0C6BD55F406B99EEDB7C0A75E5
	C5F40C729F6B36913B2E8EA23B1AGFF9EE0GA08410G3078474C2E647E63F7332B9362A5A03B254B6303FE8813B36322448B81EA5F9F6FC4334F8C08DC7F6716A3FED9F05C6817A57F6AF4B8CC7C32E4411632D46A0FBC03DF485C8916F31727C62E875E3F29A88EB761B82C076FE29A2384DF08712A81CFE2FC8A63D1FF9650B62432386D2BE0B6F45BE7F385ADAED39BC2CF6DAA6BDEC17461D7042F6DF19E1DA1F3G1F843094A08BE0A7C06D4ACCD753CA363EFB86BE3847386DB12CAD11DDBA70A634C2C8
	8D38BB65206A160704AB6E00689E1404A0C7F91E9FC03A8F4E10FB896F27A11863D3C19682EED5D4E4985D6D93A0100204A7F3208A718484CC6D95FDF08E39FDFE0E0D1BB3309FD96B8E2AFF7273EED550B988FD73G72658D7795989186006F2915486767709D518D5F43G44D7838242A5C3BFDD730B47DD8D6D8588ECC1EAED6D9007109A6E40A41BFFE0901DA5428DC1D6BB843FA073709124B46F20CB770BC751F476E1BC5AA3E7C17120068F8ED691BC0CE174B2235F42F4372A864441F1D538AC282AA8FB
	F5798730AE41995BAF4851BD6EC1B11687F65953B2DDB5E94C3E72217D867B3A612C9F7777221523FE7AAEBEE70FG7F69B4F66F95D420FB10CFE3F13A350F45E9475EC73972958E53B8C59013C662B3A1ADFC2F6D40E36AF3FC25226FF36E7BF060534C7728FE36BC7ED74FE10BC2A3FEFF560B7C1E8C3AE4816C547C7E69D0BD59EFE75CC1016D30CB81D8B10157230C701E622DE32C03B70F31AE5EBC46FA7868B154471B459021757368EED1A6E4C3CC72FCB55E4332ACE025B5910E6495DBFB11AFC4D9D42F
	0A3CD7D0AF0BB78548B352D1ECDD47FB30863E0F2433D81AE8E431F47866A3DCE27FBA537BAC748F450E18F4323E7169FAAC911D848179DC10849FB86A2CB2AE2B84BF0A77B745537365AF9EF35EA18F0A47D1E11272F9BDE59CBAC724F1C133ED21FB8F72F1C07441D2218DCBF33B276F98F33BC5209F4A8353214D83A0325FB90F722170FC0A440CCDC4BA59D741998D50320A45CC0E875159DE2541F2D611A462FAEDCDD23C15E88D0531F1810EE23419AFE8F624F92B43E8D728CD043E460BEC128C674079D5
	4826E0B54B3750BDC1F08FE1DCA9AC5CCF6C6C5E7DC325B1976D886EB0DDBF84F12B483AA52E8A3F33CE0E60F39870A91A6DFF9EB61ED36D4120B2A462AB012E4536234867B8FF9DF15FF0071A039390572D2DDD5835A4E7EC61303EG392BD84EF85D06FCDD1711697A8820BB034D6792A3E325C25C8D2DC78C84A15761059073D90D08ECC7F790C5335C6C1851970959DBADFD41E7909F04B242FDB1BD0A7408877C82CD370745AA03AFA00F04AB578BAB4574B9B5E2A331CB7B6DA325ACF6B9F6761D6042F31B
	A5997AF9191291F9F6129CA6857BABB7A3BC37494078420C705CE65046F29BFBA65C35196C2ED5A5E457B268E7E5B23BD60E6CB558552308322A597440A85AFFCAC0740AB24345E29CE45178C0A277D5B7DE63BC98134B4AF4915C519204BB8C7D36ACF63F2C34217DBD207533F1BF247507EDFF6D5DD0C147373EFF7E668B19436A8EA2BE1A1450FA04347EED5C1F24F2F04EF1E5B8F6A715F1F03673A748DFB7B9F6FE4D857D9781E48194E4119C006C37FF19C067048712B558E6177D4290170BE5CF17B0599E
	6873G33G6683AC85A0F33EF98B6D0F8E48565839C74E4DAAFB78390966EBDA8DADG0927A7B8D68F7681EE4EEA264F0783A84F5576FF33FB824A55CF83A2FF466D6D465611B3E0930E8FA47F4CEAF686549623B139B5F05E82342CE1F7D168172CEECC7A4759DCD30C53E1D643A67766171DDFC356D9DA236F5F8E289B491C1E92B6678220E72FC138F87488B1E38D3F83508CC068CF4F20237D5EBE03DEB4030E749D3399696F5B0CF46423C6AACFECFB6EDDE65B4E6766F25D7F763AE5FDFEA41B5854CD5B45
	367A32A31FA9954436CE3B1346732CEC664FEB23481FCB201F8A10174D76540208B367EEC0641E3CCF397C4FADC76BB79C8F763564ACAB56705BB03E5200A7B179BF45ACA66B009633161DF19F48A81EF3214F2D035882E8816881105631B32F4457425D256E6AD57C5D107CC906120058B7DAF2FF3571FFBA585AAF7A41373804871FAC5BE33176B16B689DCD5B1E127AB254927B1ED210FDA9C04B83284C01BC8E208D20B707595765EBA2761D63C535DDD55DC34378C6E546DD9105899D0D8E92E25BC5BB33ED
	B8074CBD15231F25F3F22C6E270DB876666710B52A5270BBB0BE4500A7EB570F71283F93E83139DA0D0358E89CEDB7FC9FAE7EA24E6C68F1FB6ACB1D032C4BC9593A26781DECDED31C134DEB0A8BF2741A6232ECDDD37CD9B65694A42E1CE3686C2AF999768BC0AF40B040544BAC26B6488DA426CC62699C9499E7957C8A2132791FD32CEDFDC9397B5BC52602723EC1C5D5959F9A1F6157AAE83DD4450F118437DEC1BE02B4E5D8A52C941F2720F3D292BC98CD7292811F1D89E0572217D92E72090365AF4FE838
	9FB9D97E1277327676BCDA8FED5BFA708F0084908B10883084A09520F0BD6365A444DB798A78932087408FB087E0A1C08AC041AB0CD7980D890B17D8BA3ED9AC95D27C928BFAB4C03A36D1DDFB436409AE31913A38A84DEC3CAA5A6E3D0FBD014EBFF48723B8A9B55DAF15E356B17456585DE328FE8C579330763D13FE01605E9D43B92D47939434D8FDFF2CD243F586FB1420080A9C12B7CD5911ED7E00609182D07911D21ABB3F814548A3B816AA613B8E0064F1DE874B2762B610BCEEBE340B81D01EDB3B9167
	6D4E8E4A1332A1F28D59BDE723108BBCC5G87E83FFCA34969986C67B6615A95CADA7490576D9E94A4021FF79079382B973C74FBD11078E314BF66E3B48F2E03B589B656333F1AA973A1450F1F51E4D092B9C3C6992773EB9402BF9928B24A40EDBB281CD1C21A5EBF6A58274BE954846C231312B1179A58C87CB78BDAC27F6E0CDCAC1B521D1B983D751019FC1DC6EFEFB413EF51680F551849F7E874CB8EB3FA1FC6FF234AC817B691FA12C6FF44EEC6CF06B6F5937A9D6C6748CD54CF479D75504A42C4BDCB33
	1BBBE05DE47438FA6458473FE92ABFF62265A849B392A70C75EF6D6EC24B7A37F6B3093DA65A9EBED1DFC66E67079DAC376201B6F7B37B7DE71D036554291BD95ED969C43C65F9192E43GEDF90CF70F0B557D3D00FB875B4161BA007C957C4E8CC7EC5F0ECAA51AB72DADE7F9D35D379F668E27BDD956E8E1CFDC9E31A3ABCF2F87AAFFF850C95FAABC96EF95B8DE094E0A44F42E59426A115B3FB77E26B6F7DF3EE55E563D0556A3DB7479E3209EA1733FFB244EFCFEED27919D561730B71F24AD4C17E30734B7
	948D97EAE4FCF5DB99EEF98D2B8BFAB55C038E060B5B4A6A36873ABE644CFFF79C4289F133F7DCEC9ED7C57478BEF35F12B1BF525E2516EA73AC33B35C4E2D4CF7AF7E1FB1968B9EDDDD464E36F05FFD14CFFCF6BABF62AD66969AD7857C1CFC160B7407FE856D9577884D5CD11A2B84F0B682481E006297826AA4416217B6FA37A5BA6E2F2679677B14FB65F34B113CF9965ED38B7343EB21EFE07C127C70DA68CDA75B2F4FG2D2900659515AE637CF3GBF3FA0FA4EC0FC7E03A9F4BFAD865ED400A2004A7F02
	7D8D50865083408304G66GACGD8821082108BD08860FC954E5AD759FAFDF273E81475228F86B8018C38497B9351DF154C7C5772AA31216355C8DB087E5EE9F4367681DD850883188F30D453A77666DDFFD7BD970A723B6A6628B5E02E09DEC46E46A1B339A24ADF3E4DCA6EF6E334796AAD65D6561849FD847C67812EDB4AFD5DB4EE7D40BF8BA041D26E37D1FFCFCF3214DBEE0FA617ECA2C7567941BB503077771B6FDA4598ACE7979C391786C704A135576D55E49E38054652EAA8F2D4D05AC337D08C215F
	D0C437A45E444F5E1701D176921D9F425796797E26613B4E7965572AA73E54B9C87D739E3E3FD337113306601AB0AE0F62081FDE18324E51D7CC5DBDC7DFBDE5DE6765CEC556F99B274C6B3C7CA9E31DC7F005D3682E734259522771ED57F057B45CAEBCA642DD95F9F5D8435BEF6F267873821A1061F75FC6FACDE0DC9F456DB3602E315FA7EE23BA83CC9ED682CC766D5B683D5BA3CA92697F827308B2149D1ABF9AEEA39DC53F5B2077FB9A6E1A3696DF47390A3390EA0AC2D63396C82CE6BDDC484EBAE55AFA
	FDEECC1B3FB9CCCD1B3FB9FCE5BA7AEF658FCDDB3DC9907DCAECCCBFD553799B07189DCBB55C56C30CAFC54345B7B2DC7DEB8C378567BBFE6847E8383F9CC038B868A7E938858719EC3206FB4241E4ABDFE7B89B2E796B20FFD243DD545E813AB5DCCFB5434D55F09B6C5AFB416BAC4FF27F32DCBFC3F0F891FCDDE82F0EE761C5F37DA7A494A3AAAC8A490537F1680F540D8E9F2B9B5BFE9AF9A616F2FACD082087F8909C20B47E27FA5EC046FB5B4646731B0C67889BEF9670A49846CBB20CC7F4FD33C4573534
	010D3D58C257E28BDD91FED9042E5FEEE46325DB68EA9CAFAF4CF62EC89F0F7859FEE0261FFF0B5014269EC0E7BF6442FEC506D03F9490613F7886A0F57FEC9CE365B0E6ED912B69FFF8106957DDF4771ABE5C5E0122F0DFDE376B7A7C5C09466015F1B8B84F0CFBBC68A7E87686F15C4972EEC953C5505E9726ACF4310A93C41BD3946EFBEFB59BEF2E49F83630B197824F52A25D776991637D371D0D17EBB11E910A4246AB5F2E0FC7EA0F13BED69F55EEB72BB519CF63F1A89019AE9B1369B11509D61FFE3FCC
	9F67B78DEC1C586D2CF60FF16A742CC6C6CF361807484CF369FABE3DDBDF7B478F223CC62B7B4F1A84E5731FAEDE38F8696AC5ED4E0203EC4EA2CD270DFB74B13B1CB876836382617F161371376CE07CEF6D55EDC828E674619D776AABE64B57AA74F1DACA59B8099663D07F22AB0B3D4B7B0C39E1BA48656D08DE439039CBB6239C7733CCD74D4F6EC56EFF9B0CF2C7C0266D1E64564E4851FBC0063FA739578E9A65C2A093F7CFF20FBB0DF209A033681E647EEEC60E7E8C48E45D135C09D223DC814858F7C617
	A377E22FAFB20F3C66B36611846731C5720D5AA279BEEB08643B5A9049D754984977C6E3A4DFF9DDA4DFD5DDA45F771C11FC5FF7C672AD2A0E647B776AC83E6E52C83EBF169A79483F219FFC67FB4568AD08AB8D86386A48773F76592B52127ABBEACA3812CE2EEC98A7931DDC69844AB6C2681FEC6E00133286E477E9321DB8614C3EF13C5516B34FD64259C110A4601D1C9DE551A09FA06358A08147234C78D3B957B9192FC7EF472DAA3787BF700675503BD1AB65435F41B1EE1AC37981BAFA4AE8EB1F01355F
	97B6407DA26A69787426FFBA7777F3071615F0451D5C9EC5A7E03B9CDF008626D876FEF9740E718DAC7D2BABAC5FE3E76D227FCEF3D7789B180344BC608BF6313C7E6AA1747F8ED8553B17FDEEC9D25D52E83B4C235A774C24AA68F9FCF9839ABF48F8A8115076B7A03DC78CB27F8FD0CB878887A5FC8EF591GGA8B2GGD0CB818294G94G88G88G35DEE6AF87A5FC8EF591GGA8B2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGG
	G2F91GGGG
**end of data**/
}
/**
 * Return the DatePanel property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getDatePanel() {
	if (ivjDatePanel == null) {
		try {
			ivjDatePanel = new javax.swing.JPanel();
			ivjDatePanel.setName("DatePanel");
			ivjDatePanel.setPreferredSize(new java.awt.Dimension(276, 250));
			ivjDatePanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsEndTimeLabel = new java.awt.GridBagConstraints();
			constraintsEndTimeLabel.gridx = 2; constraintsEndTimeLabel.gridy = 1;
			constraintsEndTimeLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsEndTimeLabel.insets = new java.awt.Insets(4, 6, 4, 4);
			getDatePanel().add(getEndTimeLabel(), constraintsEndTimeLabel);

			java.awt.GridBagConstraints constraintsJTextField2 = new java.awt.GridBagConstraints();
			constraintsJTextField2.gridx = 3; constraintsJTextField2.gridy = 1;
			constraintsJTextField2.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField2.weightx = 1.0;
			constraintsJTextField2.weighty = 1.0;
			constraintsJTextField2.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getJTextField2(), constraintsJTextField2);

			java.awt.GridBagConstraints constraintsStartTimeLabel = new java.awt.GridBagConstraints();
			constraintsStartTimeLabel.gridx = 2; constraintsStartTimeLabel.gridy = 0;
			constraintsStartTimeLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getStartTimeLabel(), constraintsStartTimeLabel);

			java.awt.GridBagConstraints constraintsJTextField1 = new java.awt.GridBagConstraints();
			constraintsJTextField1.gridx = 3; constraintsJTextField1.gridy = 0;
			constraintsJTextField1.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextField1.weightx = 1.0;
			constraintsJTextField1.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getJTextField1(), constraintsJTextField1);

			java.awt.GridBagConstraints constraintsStartDateLabel = new java.awt.GridBagConstraints();
			constraintsStartDateLabel.gridx = 0; constraintsStartDateLabel.gridy = 0;
			constraintsStartDateLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getStartDateLabel(), constraintsStartDateLabel);

			java.awt.GridBagConstraints constraintsEndDateLabel = new java.awt.GridBagConstraints();
			constraintsEndDateLabel.gridx = 0; constraintsEndDateLabel.gridy = 1;
			constraintsEndDateLabel.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getEndDateLabel(), constraintsEndDateLabel);

			java.awt.GridBagConstraints constraintsStartDateComboBox = new java.awt.GridBagConstraints();
			constraintsStartDateComboBox.gridx = 1; constraintsStartDateComboBox.gridy = 0;
			constraintsStartDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsStartDateComboBox.weightx = 1.0;
			constraintsStartDateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getStartDateComboBox(), constraintsStartDateComboBox);

			java.awt.GridBagConstraints constraintsEndDateComboBox = new java.awt.GridBagConstraints();
			constraintsEndDateComboBox.gridx = 1; constraintsEndDateComboBox.gridy = 1;
			constraintsEndDateComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsEndDateComboBox.weightx = 1.0;
			constraintsEndDateComboBox.insets = new java.awt.Insets(4, 4, 4, 4);
			getDatePanel().add(getEndDateComboBox(), constraintsEndDateComboBox);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjDatePanel;
}


/**
 * Return the EndDateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.DateComboBox getEndDateComboBox() {
	if (ivjEndDateComboBox == null) {
		try {
			ivjEndDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjEndDateComboBox.setName("EndDateComboBox");
			// user code begin {1}
			
			//com.cannontech.clientutils.CTILogger.info(" DAY -> "+ivjStartDateComboBox.getSelectedDate() + " and  " + getStartDate());
			//ivjStartDateComboBox.addActionListener(this);
						
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndDateComboBox;
}
/**
 * Return the EndDateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEndDateLabel() {
	if (ivjEndDateLabel == null) {
		try {
			ivjEndDateLabel = new javax.swing.JLabel();
			ivjEndDateLabel.setName("EndDateLabel");
			ivjEndDateLabel.setText("End Date:");
			ivjEndDateLabel.setDoubleBuffered(true);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndDateLabel;
}
/**
 * Return the EndTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getEndTimeLabel() {
	if (ivjEndTimeLabel == null) {
		try {
			ivjEndTimeLabel = new javax.swing.JLabel();
			ivjEndTimeLabel.setName("EndTimeLabel");
			ivjEndTimeLabel.setText("End Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjEndTimeLabel;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField1() {
	if (ivjJTextField1 == null) {
		try {
			ivjJTextField1 = new javax.swing.JTextField();
			ivjJTextField1.setName("JTextField1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField1;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextField2() {
	if (ivjJTextField2 == null) {
		try {
			ivjJTextField2 = new javax.swing.JTextField();
			ivjJTextField2.setName("JTextField2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextField2;
}
/**
 * Return the StartDateComboBox property value.
 * @return com.cannontech.common.gui.util.DateComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public com.cannontech.common.gui.util.DateComboBox getStartDateComboBox() {
	if (ivjStartDateComboBox == null) {
		try {
			ivjStartDateComboBox = new com.cannontech.common.gui.util.DateComboBox();
			ivjStartDateComboBox.setName("StartDateComboBox");
			// user code begin {1}
			
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateComboBox;
}
/**
 * Return the StartDateLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartDateLabel() {
	if (ivjStartDateLabel == null) {
		try {
			ivjStartDateLabel = new javax.swing.JLabel();
			ivjStartDateLabel.setName("StartDateLabel");
			ivjStartDateLabel.setText("Start Date:");
			ivjStartDateLabel.setComponentOrientation(java.awt.ComponentOrientation.UNKNOWN);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartDateLabel;
}
/**
 * Return the StartTimeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getStartTimeLabel() {
	if (ivjStartTimeLabel == null) {
		try {
			ivjStartTimeLabel = new javax.swing.JLabel();
			ivjStartTimeLabel.setName("StartTimeLabel");
			ivjStartTimeLabel.setText("Start Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjStartTimeLabel;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("StartEndPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(440, 86);

		java.awt.GridBagConstraints constraintsDatePanel = new java.awt.GridBagConstraints();
		constraintsDatePanel.gridx = 0; constraintsDatePanel.gridy = 0;
		constraintsDatePanel.fill = java.awt.GridBagConstraints.BOTH;
		constraintsDatePanel.weightx = 1.0;
		constraintsDatePanel.weighty = 1.0;
		constraintsDatePanel.ipadx = 1;
		constraintsDatePanel.ipady = 1;
		add(getDatePanel(), constraintsDatePanel);
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
		StartEndPanel aStartEndPanel;
		aStartEndPanel = new StartEndPanel();
		frame.setContentPane(aStartEndPanel);
		frame.setSize(aStartEndPanel.getSize());
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
		exception.printStackTrace(System.out);
	}
}
}

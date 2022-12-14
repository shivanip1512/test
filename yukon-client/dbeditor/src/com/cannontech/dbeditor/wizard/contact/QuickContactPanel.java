package com.cannontech.dbeditor.wizard.contact;
/**
 * Insert the type's description here.
 * Creation date: (11/21/00 4:08:38 PM)
 * @author: 
 */
import com.cannontech.common.gui.util.DataInputPanel;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.database.data.customer.Contact;

public class QuickContactPanel extends DataInputPanel
{
	private javax.swing.JLabel ivjJLabelAddress = null;
	private javax.swing.JTextField ivjJTextFieldAddress = null;
	private javax.swing.JLabel ivjJLabelFirstName = null;
	private javax.swing.JLabel ivjJLabelLastName = null;
	private javax.swing.JTextField ivjJTextFieldFirstName = null;
	private javax.swing.JTextField ivjJTextFieldLastName = null;

/**
 * DestinationLocationInfoPanel constructor comment.
 */
public QuickContactPanel() {
	super();
	initialize();
}


/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G94D7C3AEGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E14DBA8BD09C4715CED9C9F49765AC1765BAD7CA3952593A84BBBAC73910C4C9DCE5DDECD76433FC312FA47F4229C862582A0B65E0C936B889C94D6E4272DBBE82474812CF4842326CF08BC832009509D931AC30A0010048B826928D6C400EB44C2CF78731B85817777AB3BB8B3B9067622AD0751869773A5F3F3BDF77AC29BA32AAAF57D6C5C8EE9E61FFCD3904045FA4E4176306C785AE7B6781A7317C9D
	8528A057B5B860D91095F0AEBF5863ACA725478A20FD985AAB2AFA1CAF41FBA529BC265642CB8AB9BE20BD757D6A538B4BF933CD48F15276ADCDA570AC8528811C8F6583141335CD559C9F02F6A9FFC748359152769C76E9ED2E3660B771FEF54F316713344F83CD07B9FDA934314FAAF82673BE9B61D9C8357B342719779B39B10D6C7C37BA1B8BE5D9788B285B4E467673E78DC9270E301374019EF83676DF1D787518BE699E13B4CD578CF9AC648E0E4AC14550A36EE96583A992F4EB3A210CABE31221681ABB
	A20FA9E1C5560C2847F3C11A14BBA6A5C56D17B4D9A56B37238D234D8514BF5830DD687B302B156B3362EA9A493C6DC8CAFD9DBB181EA7F64475DD7DF40663319D48FBA775BF0E8BC1FB95G1359EA4ABC3CB360DC3993F15FBB03FD0760DD85D8E2627F0672480477ECECBFAFF47C556BC24761BFFC0B1C7EF1A03FAC050EA71E67BEFDBE2EE37593990E03B4C73E6945B8AF835ADA00FB813682943C90F01EGB8C4F3737DB6C8F3629DB55A7549C9DD7306436754E0643C9E14D5F877F80042F18372189E8992
	4272EF2239AE09BE825ADEC84CBD84E65BBBC4644B1A01BBC96D0DBD79F9C9ECCB5F6C368DF19E21976236B13B865A512E2500DF8DF09740A6001DGD53B04DD5FEDBB3518DD5D10E391CDD28727C643D244B8B72528C1B9426D5B5D3410FD49000DABD850AF490079A56D1455AF99DF6F37B91378A5A36DADDB43AE660F2EDDF13FB0595E1432C33B664783E159AE4C43B3B48FE761BD1767AE4337D17CC396BC4B7D8DB41F311D89B4DF36485B61D2E1C32F63D6D25EF8B1298D37FC4FEFCB4BE67A2C4F0E5B40
	E46E25B21F49E6B2F3CDDD721B911FED41B3DDEEB06721633F834E90G4B91F7872EDF9B213D95006C8EB82B811A8106GC23BC51E64FB6B7F447C6F3764889BFB3777977014E3C6C46A562206240D49172408A20D2AF21470BE9B3C68035104F18F52F9B986B2BAE5CD0E50558C384392868999E5FD9E71D6E01F08CC29C34A248C82A643C4705D622D0427AAC50D5F040312A147FBB07AD3DE0C49B56992F08486703EEEB77A6BA33ABEAD0577553B052F5E73A3AE875AD5FB04DF24F38760598393C4D6BD9E1F
	B206EAC81199A17F3DF6DC1364EB307C7A64302C85F19D663451764238DF46F5D367904563DCEB874523A6DEEB2FE0F860E1753220DF23F449B0A24A6814A18F4A0621E89371719FD1DD228953B8CA78DA510979F1826CEA8368B76DF37AAAAD76756A7A55297090C60E7BE9187AA9887DE77708BD3735897760D5FBC43EBFBBAC725D677AE7D23739AFE93EA3EC584372BCF34F5CB96B27BC63B27E7D4CF00AB93CEACF72B97C71572827D34A307FBF79A3B03F5F5705FEAF4889B8EB81FCB9426F67FCB5EC3E1D
	13223218E1FD9108189C0CE79953530E6E65BA17D85EEFEECA257FDF82180CBBBFD1994C974D9D2967505B9D4967D0C7C772B9545331709C996CC8B6C7986D379D38876B531AE54E7D3E83677F182EE9B28D0AB904301A6D59A134C35194639DA5B8A19B034A87B2A1CBF2F06E5CCC674E883C07F344DC19BEA666CA6D873713753FFF5316EC5F3F651F3AED9BF2D81EE76444678A5369329FF3CAD665C9F0D43BBE25998CBFCE57AF0E676B67F0E7723E3F6B4479F6D51E617DC6BB73892E136A14EC5A36D6AAC6
	9FC714C9C8C5DC10F85D2CA7544DB9209F5658B5FB834E53GEC6CF3FD386F78E0ADD6B43AF4776A2CDC45A85262144D0FFD5D28DFAF0C3B8C905BAB664716CE240BF509CF0CF6DDD5D9984C78BC50D50E71145F1F12E15B41052B473469BF3A8E4C21DD6276B17A93DDE81FEAD973166FC57B561835AB6E098CF78F45B5422458E697354A79F2EB4D2F7A7F10B416881B763298EE5DBB3706BB5503FC8B76413E3CCF587E1BEE6B1A6C0DC675B1057A0E47E5479B68FFFACE38A2DD13E26E68B46438479320FEE5
	757763D4DF6CDF37CF5459371FC5DF8721BD8B30421467884C4F1B42C091BC4715C8946A43209C93BEAB8B206D385FF23428E7CFDAFD31BE73CCCADF2C5947EB1EFDF3FDB1530BFAE4GBE4B546DEF839596DFC06D8D654584C44ADCBBFA6CAC0FF93B35A7CF64B191EB5816F97520E3FFFC8F295EBFFFBFE1355651D674D75DFED6EB39CCFC834577DB702C867BAA75AF368D20E56E97F505FE9E6D6AA5BD4E91G266743BDAE0BDD7D3A229926CD9F7562DC54A34A0422895C759487A78F3AB8313D79F99AE395
	F8DE8740F0559E442D0676DDG4C7E57DBB1DFB221CDF2FB1CB5G2DG43GC62E68F30676D9816DF400CDGBB812AECBD4E53B651679BE7AFDBFC04FC667A8DE508F70353DB137493B04CE944520749FAA8C116B349D86B9E6D624F4294F2900A79B34F02E33EB54EE39CE3E6BBC65BB3DCC6A5A9E94927B9BF23CFC11052ECF83FF12C453A868E81EE96E0390D594166695726D166B89434B0BD87437298D9E24731759EB1B68B7ABBGD78B60838882B0190FB4629A9098021D4F2F48EA500FB391CF77DC2713
	D39589F4EFB0981169BE4868C7DA50A701DEE9948A78B934FB5A4ACC5ADC3EBF9EABB5E93D92A7F15AFDEF9DC810992797B850AE35354A62130DE08B56F3BB6149685EA349687D8EC14F34DB69C18723AFB5690FF7A4232FB46957EB4968EBCD7A67DD49645F8BB4962F464B38E7196B2ADB1AB6BC1E4E0892BCA7CD7452683335353B856798BDFB41D83636CE37605CAF4F6BF11EGE883E06BE26C383843D87325DBC96FF57DC96FD9B2071AEC43F9ACF7E6799357453AA7B29CCC6657BDC266BA20E76521BDBF
	72D86D41987D84400DF9C56915E72DF44C172684FAC7829D755DE5335227936868235779F8E65B832F895BF29ED8CB54EF7CBA659DD2F3BE33E904BFB7CC9DA3173F7C1F8E667757CEA478BD3E1F191946FCF0DB2B5817D7648B7FFE68375EE35DD556E4ABCE2143C6AE7B197C38FF57BEAE7C6B2E913C6B1CA29FF60C2758E7696CE063DEF260B8DC1F02B00E614A7D82375244FDC79338CDA66EDE1710D9D2A056588BAD9F775E046DC9BF23796783BE097785BBA93E5F02E7FB5507AD623EE084E86B8158DE75
	F7FDE8F3EDA164B040B0C098E0B9401AC231F76DBD5FC09639AB9852433DD0502B16E3AC33EF1D674F3B977AD72708F37AF76EA06BBE33AFB7D51C57973278E67267E5639845B37B0EDEC47B4A0BFA1CF5G7DGA100D90095C5423E0E8BF54C3E0E20E2F88DC39A8B514AC898F7C9112763E8DC5819EDE92D8B5916D6B4FF9FE4BAFDEEG6759DDC0FF8CA08B2020985611E2214F8D97963D436B08ADF007F72AE6A13D3A0A676B45469DF6D8470D70FEE17EDC12F25C0BFE6B38DBF83FB57C39BE65383BB56B38
	CD3C5FD67E4CC9B96EFE57C2761517249A77DC8BB15FF7DB5E2FEF79644F55ABBFD199ACEF7E3EAF75397A9F7A121F2BD777A5BFD75F513770397ADF7AD21F2B3F5DE7BDD7331CFFF090F33E96E2538690840881ACAB91B95F597FAA4B79A4732F838E5A6DBA3DE9B4047CB7AF26363571626277131E0BF85E99558DC31FC47E825FF69163E568E10A646B43C574911467A103F595F78F97E9BDAE0FD3B42B476FEE9575F81AE95BCD8329F57D42C072382C9AC89E17AF8DACFCE7F867C02A38B17D82B5C23F6C92
	F106B8C57734AE972C7FAE017BA15D2BD4E84F1A3836D2B1F629092B23FB5FE3505EEA629EBEC46FD521DDD2AAF0E78E0B3155A66E913AC706209DB3F1B755A3EE9934D71A386CFA5327A6AE53E55AD1AA7C3CF9B0351FB386974F091F8E52F3B144F2E8A6ACB35CFF8D52335978F8D4B6F36F670365A62EBB5A2FC795BC2C4F16511A5567DBE01E32BAA38EDDB0E68460982705617DAB39EC1FE37C241A4578B9AD7C6E1D436FB18BBFD653879CF88FB42EEBC6CDB9723FCCE37FA4744BAA93E7116DAE6CE30033
	025007102FDEA526BE6F14AE240FFD0EBEA396FDD0073472F8DD42E4357B9375F91766D3BA3C2FAF977AECBD14284F53EADC1F73079753473E00BED9406FFF93745926A56AE357E9AE43FBC10550670E7AC4FD0A3438BEE58D0B69133700BE4DA0231ED8633F4B35D87C2D7CFC9509719F2E08477F88559D4EEEC47CE53DBD9F87F5AD35E989AEC53C967BD10928458EFF65B662FBF4DF2E50F72EBE418A367726D744EB6E25D5DB9C49E4B3B9275E0F4BBCFE0D18F178018B7783CD1E2E91C7B3379F02F2625520
	A2297A442BF5B58DF5B50D275F90BA2E1A365EC7B59735263C0FFA066BC68E306769AF9F63BCFE701E15C779970E5BF2D370E866E3FD8762F6B29BB6CF476D39E9B6EE4FFD077ED4FB5858EFFE9867F3054479FC367E637179BC2A1FB476DCDF9B36589EFD0F9DF3AD86B62DGD88770904013GB9G6515BD4EDA00E600FE009040AC40F20055G69G9BGB2AA45FAFC3F8EEA10CB5295DE41F6505FB450DD9D107C20CDB77608446E43189EEF64211ED9402724CA703AFA6D178B706A0F68E1B9E24CB0AC3D75
	653CEAF179666B76572E3D160A87D4998170E75F6895F94C9812A64CB10F5AD36FA9DB6C0B6FA9CF5B1357995B6C736B0C6D7664F5C6365DDAE770B3199D73FB82FCB8EC7633B9846EDD9367F494F25CBBCA508819F8172308633BE494A870158E54EB1A6206B92E5A02FB1763DEF120E34164109E91E3DFE5FB0B222A2CFD1C76D1B458A6CDFF7C126A2804A50B5E8DA6AE2E37BB8F6715D9473010F25ADBF9D504FD03011CD7BE20D7650350D11BE03FBB323B1F85C556ECA3F986A9253F0E2961D7E29E039453
	9B309A3C4702D6915EE931C6B4C24A4CB955CD154E3CF64AE37F49EDF69BF1F413FC9644EE6240C8F413BC9A28EEE207A0467083B54EA4F440C9DCDB226F130F6814BDE8415AC106ABE15E5E770FEFBD35F14B25281D64F71303386D8C4A91EA159C74C906780C6DA705E13CB2462FCD20C014BE7222460CAA77122A71817C6491B56420D778ABEA1097FBD1D27D14212875DDD2B4E4E0DE1E6DA58E8DBF3813830C2CCA5AC4BDF35ED9EAC52E77B2DDA06892897AD6D2FD110B5B64D2795D32570E861EDCE7A7B6
	BFA9D2F5A9681746E0AAF513C2A384FE8E69EAB096FE3B0DB2AA18466CAD66669FB24DC7E35DF863E3AAFDB57645E52E2DAEBFB8B296A17534A76E32EE33C730BDA4C5C0149C69B6708784FA245624FD6AFF0C638FF63EB4E2A3395CC88CC1C1ACECB0071971C5135DFE505AF8E25B7D377E30B56B999862A7F974A38288AE0F773AB0B5B9AAC7BE336666D70AD7E4FE8E758A23FBE8F0CB8C12BFAECDAA6A8C3C78686AAB700530A2E851F1BDB289C2EB66D8ACC826626E1B6EEF3879765D3F250A47079A64888B
	918C2D1F107549FA6F78382249A683089730FF45E40FFD5CF37A9C085D78E2E0737ED148AF9F31CFB6B4507F0D747FC97C5FC8B10D9453F8920216C5967C1BCB0F45506697E3E63E3F8CD044F35ECE317973328A31C5735383512514D80E5D71255274AD36FEC10955747F21BFCF5323F1CD6D96CD8F7C75E9FAA23926F693EBDDEF4CEF14EF7C1F3506497E74AB293FD19E6477AD8763B58C5BB3279B51505A17612C7E3258FF3F5F00F5F22A3DF3F0D2D2D5C3D22FFA35A06E2367E68CB9FEE61DEDC47ED15107
	9379F72EC6547B0AE54C9F81D0CB87880E524F01CA90GG18AAGGD0CB818294G94G88G88G94D7C3AE0E524F01CA90GG18AAGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG0490GGGG
**end of data**/
}

/**
 * Insert the method's description here.
 * Creation date: (8/9/2001 9:41:30 AM)
 * @return java.lang.String
 */
public String getEmailAddress() {
	return getJTextFieldAddress().getText();
}


/**
 * Return the JLabelEmailAddress property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelAddress() {
	if (ivjJLabelAddress == null) {
		try {
			ivjJLabelAddress = new javax.swing.JLabel();
			ivjJLabelAddress.setName("JLabelAddress");
			ivjJLabelAddress.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelAddress.setText("E-mail Address:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelAddress;
}


/**
 * Return the NameLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFirstName() {
	if (ivjJLabelFirstName == null) {
		try {
			ivjJLabelFirstName = new javax.swing.JLabel();
			ivjJLabelFirstName.setName("JLabelFirstName");
			ivjJLabelFirstName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelFirstName.setText("First Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFirstName;
}

/**
 * Return the JLabelLastName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLastName() {
	if (ivjJLabelLastName == null) {
		try {
			ivjJLabelLastName = new javax.swing.JLabel();
			ivjJLabelLastName.setName("JLabelLastName");
			ivjJLabelLastName.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelLastName.setText("Last Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLastName;
}


/**
 * Return the JTextFieldEmailAddress property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldAddress() {
	if (ivjJTextFieldAddress == null) {
		try {
			ivjJTextFieldAddress = new javax.swing.JTextField();
			ivjJTextFieldAddress.setName("JTextFieldAddress");
			// user code begin {1}
			ivjJTextFieldAddress.setDocument(new com.cannontech.common.gui.util.TextFieldDocument(com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_60));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldAddress;
}


/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFirstName() {
	if (ivjJTextFieldFirstName == null) {
		try {
			ivjJTextFieldFirstName = new javax.swing.JTextField();
			ivjJTextFieldFirstName.setName("JTextFieldFirstName");
			// user code begin {1}
			
			ivjJTextFieldFirstName.setDocument(
							new com.cannontech.common.gui.util.TextFieldDocument(
									com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_30));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFirstName;
}

/**
 * Return the JTextFieldLastName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldLastName() {
	if (ivjJTextFieldLastName == null) {
		try {
			ivjJTextFieldLastName = new javax.swing.JTextField();
			ivjJTextFieldLastName.setName("JTextFieldLastName");
			// user code begin {1}

			ivjJTextFieldLastName.setDocument(
							new com.cannontech.common.gui.util.TextFieldDocument(
									com.cannontech.common.gui.util.TextFieldDocument.STRING_LENGTH_30));
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldLastName;
}


/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
@Override
public Object getValue(Object val)
{
	Contact cnt = new Contact();

	//set the contact data first
	cnt.getContact().setContFirstName( getJTextFieldFirstName().getText() );
	cnt.getContact().setContLastName( getJTextFieldLastName().getText() );

	
	//finally, set the notification data if they actually put an email address in
    
    if(!getJTextFieldAddress().getText().equalsIgnoreCase(""))
    {
    	com.cannontech.database.db.contact.ContactNotification cntCn =
    		new com.cannontech.database.db.contact.ContactNotification();
    		
    	cntCn.setNotification( getJTextFieldAddress().getText() );
    	cntCn.setNotificationCatID( 
    			new Integer(ContactNotificationType.EMAIL.getDefinitionId()) );
    
    	cnt.getContactNotifVect().add( cntCn );
    }
    
    
	return cnt;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DestinationLocationInfoPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(379, 113);

		java.awt.GridBagConstraints constraintsJTextFieldFirstName = new java.awt.GridBagConstraints();
		constraintsJTextFieldFirstName.gridx = 2; constraintsJTextFieldFirstName.gridy = 1;
		constraintsJTextFieldFirstName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldFirstName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldFirstName.weightx = 1.0;
		constraintsJTextFieldFirstName.ipadx = 213;
		constraintsJTextFieldFirstName.insets = new java.awt.Insets(18, 4, 2, 50);
		add(getJTextFieldFirstName(), constraintsJTextFieldFirstName);

		java.awt.GridBagConstraints constraintsJTextFieldAddress = new java.awt.GridBagConstraints();
		constraintsJTextFieldAddress.gridx = 2; constraintsJTextFieldAddress.gridy = 3;
		constraintsJTextFieldAddress.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldAddress.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldAddress.weightx = 1.0;
		constraintsJTextFieldAddress.ipadx = 213;
		constraintsJTextFieldAddress.insets = new java.awt.Insets(3, 4, 25, 50);
		add(getJTextFieldAddress(), constraintsJTextFieldAddress);

		java.awt.GridBagConstraints constraintsJLabelAddress = new java.awt.GridBagConstraints();
		constraintsJLabelAddress.gridx = 1; constraintsJLabelAddress.gridy = 3;
		constraintsJLabelAddress.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelAddress.insets = new java.awt.Insets(3, 10, 26, 4);
		add(getJLabelAddress(), constraintsJLabelAddress);

		java.awt.GridBagConstraints constraintsJLabelFirstName = new java.awt.GridBagConstraints();
		constraintsJLabelFirstName.gridx = 1; constraintsJLabelFirstName.gridy = 1;
		constraintsJLabelFirstName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelFirstName.ipadx = 22;
		constraintsJLabelFirstName.insets = new java.awt.Insets(18, 10, 3, 4);
		add(getJLabelFirstName(), constraintsJLabelFirstName);

		java.awt.GridBagConstraints constraintsJLabelLastName = new java.awt.GridBagConstraints();
		constraintsJLabelLastName.gridx = 1; constraintsJLabelLastName.gridy = 2;
		constraintsJLabelLastName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLastName.ipadx = 23;
		constraintsJLabelLastName.insets = new java.awt.Insets(3, 10, 3, 4);
		add(getJLabelLastName(), constraintsJLabelLastName);

		java.awt.GridBagConstraints constraintsJTextFieldLastName = new java.awt.GridBagConstraints();
		constraintsJTextFieldLastName.gridx = 2; constraintsJTextFieldLastName.gridy = 2;
		constraintsJTextFieldLastName.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJTextFieldLastName.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJTextFieldLastName.weightx = 1.0;
		constraintsJTextFieldLastName.ipadx = 213;
		constraintsJTextFieldLastName.insets = new java.awt.Insets(3, 4, 2, 50);
		add(getJTextFieldLastName(), constraintsJTextFieldLastName);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
@Override
public void setValue(Object val) 
{
	
}
}
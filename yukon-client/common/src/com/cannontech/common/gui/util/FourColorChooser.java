package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
 import java.awt.*;
public class FourColorChooser extends javax.swing.JPanel {
	private java.awt.Color selectedColor;
protected transient com.cannontech.common.gui.util.FourColorChooserListener fieldFourColorChooserListenerEventMulticaster = null;
	private javax.swing.ButtonGroup ivjButtonGroup1 = null;
	private ColorChoice ivjColorChoice1 = null;
	private ColorChoice ivjColorChoice2 = null;
	private ColorChoice ivjColorChoice3 = null;
	private ColorChoice ivjColorChoice4 = null;
	private GridLayout ivjFourColorChooserGridLayout = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public FourColorChooser() {
	super();
	initialize();
}
/**
 * FourColorChooser constructor comment.
 * @param layout java.awt.LayoutManager
 */
public FourColorChooser(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * FourColorChooser constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public FourColorChooser(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * FourColorChooser constructor comment.
 * @param isDoubleBuffered boolean
 */
public FourColorChooser(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * 
 * @param newListener com.cannontech.common.gui.util.FourColorChooserListener
 */
public void addFourColorChooserListener(com.cannontech.common.gui.util.FourColorChooserListener newListener) {
	fieldFourColorChooserListenerEventMulticaster = com.cannontech.common.gui.util.FourColorChooserListenerEventMulticaster.add(fieldFourColorChooserListenerEventMulticaster, newListener);
	return;
}
/**
 * Comment
 */
public void colorChoice1_JRadioButtonItem_itemStateChanged(ColorChoice cc) {

	getColorChoice1().JRadioButtonGetModel().setSelected(false);
	getColorChoice2().JRadioButtonGetModel().setSelected(false);
	getColorChoice3().JRadioButtonGetModel().setSelected(false);
	getColorChoice4().JRadioButtonGetModel().setSelected(false);
	cc.JRadioButtonGetModel().setSelected(true);
	setSelectedColor( cc.colorLabelGetColor() );
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE2EDFEA7GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13D998BECD35735AACCE2AB92E94B90ABCCC5EDB668C6B7DAD8572E56C6D73602C485C8E80A5AE8CABBDAA115EECC4BC65A326D8608C95BE072F1026358098301FC880979ECC4999BFF8803365188A96B32454F76337D925B4FBCBF138431EE675C7B6E730BF98E14AA35F4F45FBD675CFB4EBD775CF34E3DA67606F9A545DB6C049417906DF73418108D0D04ACFB3E68A047151667EFA1069F10C9851931
	E39B348E20ED277DAB03E5505AG3C004376A63465A4B6685070FD9A1EC3B14C5ADB01BC5386DD9AEEA970648114815ED341783B20FD17CA3E76D11746E7A97DAA899ECAD8F760BC06B9B94C5646DA3496B43586ABD15693D4CF9CD38834F64061623712475FED0676D86FF078EF81B95691D0250EF0CA6AC829D2343B7B1C1CD2CE4BD1D9B99D116524281082BA57DB0325F4ACA18B209F2DC25CF3C3B8FF93FC9F83682B62782706913F883E178394D471B53DFA7C1826F3B472CD322E73D36B4EACEBAA2AE2EB
	715853EBDA12FF222A165AFF158D6DBCG34FBGE682BC8D1087D084D08734AA72339DED501E742B2765D8CC0E1FCCA4CEC5034A87F2D00C42F7F7B7D0B45CBF44002C8489E1FB7B7D41F693FDA682B66EA71FF99C33490243FC1FE7EF19C30AFE9033EEB731C942B2E6AD2FE636E829CE5B04497EC3D659BD55637DF03C6C0F09D176E843172DA5A6324F047EE39D5066C998E467D663DE7F096EF58EFC63B89C4F70A7A8FE3D814FFC72856AB3585F8CB4DF8D770D19ADDC1732876EA7F34312299DFADF7812F5
	D98D53A13FA6D3171C984EDD3A0B49AC56F5799A455B8DF8260BE5181F8F8F502682B07F7AC5B91E5175505F66485F6282B882508710G186A60BE56E06B3CC39F6B959475D4CA0A8661BC3131F9369AE845D1D591AE44132A908F087D02A2897E2818A49A4FABB63401FF5C3857296F87C046F9B1AEAA02AA49F118DD08092A28A4994F2FEC95482308145AA745C49090CB90BE6FA6DB951EC1A129DEC98485D5CCF3B07A9FED38A75705FE1891A600EFE61772CA34D7AE2C5F8230D237430D1DB85FC5D1090B90
	C0AE4972F0AA5107ABA12EDA34F3358FE3C7817097B9380DF7D5A14E877451DABE0FE5E725E11EB3D2G559714B1FE467EB20CE326837F02DA6EB73F3C4A7D266835B9E479D3C326FEC363EDAD73173C5A4C73F30E4E0B3A91A70F71AF8EDD4553D9EBFEFE26E741FF361871BA15D11205E76B67185F266FD90E9D774C836BBA86B06064365E345343FC771410943937FE24002309413465191E33E2C45779E143770F072FFECE9DEFEF2317A7D58633451B651807446BE2DCBDA3A644F890BE129A2D309C7DCCBB
	27A1D99F73B645A760DC1F060314547124824F1E202A0A64CF2962E5D1D525F8B8CDAF2DC0D94914BF118E89DC564E8A7485F9844E354EEF2F40B3911063F111EE0ABEC4235750B3A9C64598A8FDDACE45D506F7D5E24DB2AC0EE9B141D3E9A5F82623A90CAF2C8ED8A43C0FEBD02498ECB506A8324C0967355D67B058C8859C56B33321DD686467EEE48FBFF7417ED9447B5FCFCCF3C7CE4594EBD91D164B6B5267CE53314ADC775DB4B6E878A4535552DF8EEDE6F13215724F5E03731CCA292A9CBF2F482944C1
	667BAB2A91FF82647681C46B386FBFC66B889EE7B42707E9234C44C0DBD407F6784A305D301629F54C8EE93F19E5C71D1262351488619B835EC55D5EDF37D7E6507AB55BB37AB76CE87B28A1D632F57E2695EDCFEB33A1613AB05A119C817F696E7EFB2FG5A32DAEC960D2748DFD447EB2E63A22E556B4A5F528360F369717183DEF3795EFC04F8FEBE16350E9CF531354DF52577E95E2FF94DD5358BE56683ED030B5B51D3ED0C7BA713C9B9A05114232DF1D535F19F99EEED75FB5006A4A529DE0003B64A6D71
	D2B56A0BB9C9C3EB7678FD075B549E065DA63C1EF96810F12DB787862C6FE7D9ABF1EBBE69CE2FF5A35417AC664C71E556D3BEF7BA5E2B6EDBE3BF2BB39653BD1866E6F5463BBAFEA945679A702C7EB8CB7158DF8A3426FA1E9B67BA508726C1BF97408230962090405E40ED6F2FB9CCEED3FBDC08C3C99097221747859B4D5E0EDDDBD9FC507A2EDDA5BCBE901EBB5EF3227FFBC166A500A040CD00393A8E0F6E3A2D8EE7C7CDF5E0F54113540FADB05FEAG066B8FA02E907AE59E1E0B1F91711CF9215F836083
	9855E9BF25349C68AF814883582C53AE0A193983475EC9DE984D42675570BD3A0C60ADB2365F417CB6ED5F4D70BC6F63FEE8314211BDBFADF4186727A79D667969E9C78543439C462A145317BAA64EDF4B9D5973D725B33B1E35CEF3BD9B1C66FA6EF5CE2C6781E7B6BDD9CED86143FC97954664148A75DAA37AD6011E57BC506F827081280DECCFD93CF9E284E3C6888AEA186EF2C28C1048EEFA378F7031EB01FFBDC0A960DBGFAG184C68BEBC4FBCBFC98171A043AF12B37007987E1CB783FF1861E77CAF83
	FF04656537C5E3DEDE341B555385509682B01E6BE1B31E32BD79DBBCFBB84F0C0899CF9750AF69BC8BA4B31EA050C781D87CAA95787DA987F04B77707705D50398BF74185DA10C285D5D6795A9F8C95B8E6DCD21D7A8554E8BE962F96CFBFE1C338B7A3DG11A61E47EEEC6531FDF36B23A4EA3D57B40F391ECDD86FE9E27B195BE448E3905B194C87E9FC41F185C0F76CC55CC8501867F18FFE845001A3A97D31F1F4FA379AC7FFF99C9D751DB10E3EC3B45251C6EF94B3BADB5B7EFF7135757DF9BEE919924A1A
	23FDFB591A26EAED2EEDCD495A01EF57B03B7F6E54EE1275DD4760CE5C7E07C27CCD4732175B7F79905A3F907AE57BA0A781B05E6F04395D0E6D633C79E164D521BF9DA0D767FDA142F9ADBA6FEFA3F4DE6817B5433C4D1C37C05267ED663CB67AC620C2FFBAC0EEB30F35B6616E5EE6DCF7B96E4A38F1166671B9E1F5C6FF9D6ACA73FDEBG75AFEA4EFC07B8C4715BDAB25FA11E7573F3D48EB4350557819B6B708C66C13F90202C956C86F08640574AF37073CEAF39CD8E6E139317A0A8C78DE1142D6F43605D
	3DD9ED6F637EBA733B8F102B51FFE72D3354D6E62319DA6BA5EBCA30E56B1B624675AD83DA814036B62881G3CGBDEDFCFD6F573533751D8DCA6AC9D5958291DACD724575CB62C89A0D811B2D6DE8E822358D34E52B610ED21FCE34E56E5D890A3F591639F7ABC2FC6F2681EDC39B775545614FEADBA627171E135E7D19727FC971837BB3653F9A6672A3C0DB391F4BDF9939BB7912C46F916D1972950ABF561EA97F1D88175F8334056DDC7EEB525D493F171E7D728319726F23F85701CC7976A1AE3F85E8538F
	70BAB2CAF34BC668138E0E2B74A34E8E7DA69D778945F5C17FD287573DB3C03D1CFEF7993E1F73CF7EFB47CB13AA031D29376A3257E944E5DE27D9DD66F5DA19EB62FA516E4ADEAF56390C6F9D5A1D3D9EF3258F7662A640BC00A5G4BBB743B3B8BF39C3E2C5E9A8B4E864362E9193E262ADC7EF4F77635BE683E7D9B6CDCB75ED77DB25CB6E3B8BF47BF62467DD26584C5B25CFCB75AC81142911531EA78475D68E3D1B1C4514C6FBE8C71FB4EBA5D6F7E9ACE7B5AB943772A50647B5D7AAFC0C6FFA43D26C143
	777A70644BAE0ECC3E8CD9CA2FE95470DDACCD3EEC6797A0E35AD0FACD779B3E1DC313AF7B70244AE0772EA9823E19AA72C8525109E7F4934DA1F826CBBB799D2F550FBC5743C202719C77F31E811D671A9FF58E4851D4AC1EA40CEF1A1F575A51CE9E4B5654E70FCDF975F7707FD0BDFDDF031852B71690357F076A715EAB07C2C9D10F01EF54176B388B49DEB9A961A3DD8E55CB8C71FB5E4D4EDBEBA2369E1B9F736B91426B3FD7747F8D713FE9E249B1FD7BC54853665C2035C5D5EBCA581C7D7A1C2C7FB06D
	639F716CDEF0DF083FF1B6F9799D709D45F8CF5A1CBB4B741E54372F5FBA60E5726E71266F0058329C23F8F06EA5C0DB6D65FB71E943FEE87B05A12D0246F2D3D6E8E681E42FA2A7C4C59DE3D87A122B79E54E38BAF3A34C376D6044EFCCCC07079A516E8E60BD82F095A081B095E06EC12E5332061689F4E217E91A6F94C162EF67A39E142167E3262A66871FFAB037CAF158FEA1AA5D20CF351701B19EE6B9E9BE3579A210FF920C4AF08BA9EEB9600E92BE4F230D597D76DB0D37775B450D66B9751946DBF36A
	8F9B4DF36A32C6E3CEE577DF013E3FE86B7BD88A2A9106F70D6EC863FBC41C8B7A7F87D0CB8788A947AFC4318BGG94A2GGD0CB818294G94G88G88GE2EDFEA7A947AFC4318BGG94A2GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG6B8BGGGG
**end of data**/
}
/**
 * Return the ButtonGroup1 property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup1() {
	if (ivjButtonGroup1 == null) {
		try {
			ivjButtonGroup1 = new javax.swing.ButtonGroup();
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjButtonGroup1;
}
/**
 * Return the ColorChoice1 property value.
 * @return com.cannontech.common.gui.util.ColorChoice
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ColorChoice getColorChoice1() {
	if (ivjColorChoice1 == null) {
		try {
			ivjColorChoice1 = new com.cannontech.common.gui.util.ColorChoice();
			ivjColorChoice1.setName("ColorChoice1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorChoice1;
}
/**
 * Return the ColorChoice2 property value.
 * @return com.cannontech.common.gui.util.ColorChoice
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ColorChoice getColorChoice2() {
	if (ivjColorChoice2 == null) {
		try {
			ivjColorChoice2 = new com.cannontech.common.gui.util.ColorChoice();
			ivjColorChoice2.setName("ColorChoice2");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorChoice2;
}
/**
 * Return the ColorChoice3 property value.
 * @return com.cannontech.common.gui.util.ColorChoice
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ColorChoice getColorChoice3() {
	if (ivjColorChoice3 == null) {
		try {
			ivjColorChoice3 = new com.cannontech.common.gui.util.ColorChoice();
			ivjColorChoice3.setName("ColorChoice3");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorChoice3;
}
/**
 * Return the ColorChoice4 property value.
 * @return com.cannontech.common.gui.util.ColorChoice
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private ColorChoice getColorChoice4() {
	if (ivjColorChoice4 == null) {
		try {
			ivjColorChoice4 = new com.cannontech.common.gui.util.ColorChoice();
			ivjColorChoice4.setName("ColorChoice4");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjColorChoice4;
}
/**
 * Return the FourColorChooserGridLayout property value.
 * @return java.awt.GridLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.GridLayout getFourColorChooserGridLayout() {
	java.awt.GridLayout ivjFourColorChooserGridLayout = null;
	try {
		/* Create part */
		ivjFourColorChooserGridLayout = new java.awt.GridLayout(4, 1);
		ivjFourColorChooserGridLayout.setVgap(4);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjFourColorChooserGridLayout;
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Color
 */
public java.awt.Color getSelectedColor() {
	return selectedColor;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("FourColorChooser");
		setLayout(getFourColorChooserGridLayout());
		setSize(71, 95);
		add(getColorChoice1(), getColorChoice1().getName());
		add(getColorChoice2());
		add(getColorChoice3());
		add(getColorChoice4());
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	getColorChoice1().colorLabelSetColor(Color.red);
	getColorChoice2().colorLabelSetColor(Color.orange);
	getColorChoice3().colorLabelSetColor(Color.green);
	getColorChoice4().colorLabelSetColor(Color.blue);
	setSelectedColor(getColorChoice1().colorLabelGetColor());
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		com.cannontech.common.gui.util.FourColorChooser aFourColorChooser;
		aFourColorChooser = new com.cannontech.common.gui.util.FourColorChooser();
		frame.getContentPane().add("Center", aFourColorChooser);
		frame.setSize(aFourColorChooser.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * 
 * @param newListener com.cannontech.common.gui.util.FourColorChooserListener
 */
public void removeFourColorChooserListener(com.cannontech.common.gui.util.FourColorChooserListener newListener) {
	fieldFourColorChooserListenerEventMulticaster = com.cannontech.common.gui.util.FourColorChooserListenerEventMulticaster.remove(fieldFourColorChooserListenerEventMulticaster, newListener);
	return;
}
/**
 * This method was created in VisualAge.
 * @param newValue java.awt.Color
 */
public void setSelectedColor(java.awt.Color newValue) {
	this.selectedColor = newValue;
}
}

package com.cannontech.common.gui.util;

/**
 * This type was created in VisualAge.
 */
public class DateTextField extends javax.swing.JTextField implements java.awt.event.KeyListener, javax.swing.event.CaretListener {
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public DateTextField() {
	super();
	initialize();
}
/**
 * DateTextField constructor comment.
 * @param columns int
 */
public DateTextField(int columns) {
	super(columns);
}
/**
 * DateTextField constructor comment.
 * @param text java.lang.String
 */
public DateTextField(String text) {
	super(text);
}
/**
 * DateTextField constructor comment.
 * @param text java.lang.String
 * @param columns int
 */
public DateTextField(String text, int columns) {
	super(text, columns);
}
/**
 * DateTextField constructor comment.
 * @param doc javax.swing.text.Document
 * @param text java.lang.String
 * @param columns int
 */
public DateTextField(javax.swing.text.Document doc, String text, int columns) {
	super(doc, text, columns);
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == this) 
		connEtoC1(e);
	// user code begin {2}
	// user code end
}
/**
 * connEtoC1:  (DateTextField.caret.caretUpdate(javax.swing.event.CaretEvent) --> DateTextField.dateTextField_CaretUpdate(Ljavax.swing.event.CaretEvent;)V)
 * @param arg1 javax.swing.event.CaretEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(javax.swing.event.CaretEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dateTextField_CaretUpdate(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (DateTextField.key.keyPressed(java.awt.event.KeyEvent) --> DateTextField.dateTextField_KeyPressed(Ljava.awt.event.KeyEvent;)V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.KeyEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dateTextField_KeyPressed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (DateTextField.key.keyTyped(java.awt.event.KeyEvent) --> DateTextField.dateTextField_KeyTyped(Ljava.awt.event.KeyEvent;)V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.KeyEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dateTextField_KeyTyped(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (DateTextField.key.keyReleased(java.awt.event.KeyEvent) --> DateTextField.dateTextField_KeyReleased(Ljava.awt.event.KeyEvent;)V)
 * @param arg1 java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.KeyEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.dateTextField_KeyReleased(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (DateTextField.key. --> DateTextField.dateTextField_KeyEvents()V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5() {
	try {
		// user code begin {1}
		// user code end
		this.dateTextField_KeyEvents();
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
public void dateTextField_CaretUpdate(javax.swing.event.CaretEvent caretEvent) {
	com.cannontech.clientutils.CTILogger.info("caret update");
	return;
}
/**
 * Comment
 */
public void dateTextField_KeyEvents() {
	com.cannontech.clientutils.CTILogger.info("key events");
	return;
}
/**
 * Comment
 */
public void dateTextField_KeyPressed(java.awt.event.KeyEvent keyEvent) {
//	if( keyEvent.getKeyCode() == 8 )
		keyEvent.setKeyCode( java.awt.event.KeyEvent.VK_UNDEFINED );
		keyEvent.setKeyChar(java.awt.event.KeyEvent.CHAR_UNDEFINED);
	com.cannontech.clientutils.CTILogger.info("key pressed");
	return;
}
/**
 * Comment
 */
public void dateTextField_KeyReleased(java.awt.event.KeyEvent keyEvent) {
	com.cannontech.clientutils.CTILogger.info("key released");
	return;
}
/**
 * Comment
 */
public void dateTextField_KeyTyped(java.awt.event.KeyEvent keyEvent) {
	com.cannontech.clientutils.CTILogger.info("key typed");
	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GF0EDFEA7GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E12D99EDEC945731A850C0E3D5001C16A6043A95ED110A54A4A22D15DA08CA288105D2C82035D2D7F25220C4E9523AA9D2E94B0F07318F93C80231B17E829CE340B849615824F50813E2ECB7B850600F43B9605C323E5BBB2F3DF7FB6C6D61B3985319773137F75E33930425E16F4D4C1B2FB7B3EFF6A12507B28A37651792322D10703FFFEEA3E4C3A5A10B97A857852EE8CFCEBE317C3D85D0CCAA7C8570
	AC87DAC1C3CE7E9EF2FD2288566F40BA6FF8CE7EFE78DDC2568FE4D6400F94FA66822D67650D5F19DACF34C76859C1577A68EEF83E815088B8FC0E6FC17DDF9BAD67F897478B5886D24F97A34F77C62BB9EE8570E48394813E3A18711F0467CE2A795A05964E17D5F4BF71B5059D2F239C0BCC8169FCEFA6FF02250E2BFBD157DAEAA76E4983DABD6050796D64695ABAF836F60F84BE76EAC12757D01C0128620C9A0AFA6AD407E448AEB9E67CDB11D5DF8E15D396AB227B60CCE0BDD4023857EF226C9A785D8250
	DDA2709BA8FEA97CDE8910D3A27CF96EDF2D5CDEF5687B647993930EB7D37833390479D1DD9A77A7F75669128A9A7B47FAB146EE200D834C85D8863081E0B3C095BD732C1E86F8368D9A6DDAB028055A4261B32ACF7F0F6613D578FD6A94D0B82ED776EA3A0F90F62E9BC65F3131E7AAE07B1E7B527BD8CC16B40BB3DE107FA05972F8503143A6A6612CB1476EFDAC96F57B62B1E13A2BE229F4F76CCB4C41C45D1F932B6E5821B90EC29B5D9D7E7FBA5CDC0EEA513DF09F1E758CFA56E970FB9B4F8F063F0F62F3
	ADF8160FA5940F6BBC20394AC4EE24578BDBF64F1FC79686945BB8F4BFB14B11D946ECD8D716EC4B59F1143D350C695CE55A52CF71557B63F8E64BB35496DC3782AD83006557F29756E7C1F9CEFE8DC08BC0B7C098A08DA023DC64D8E16F09AF18E35D12EE1C09AA2ACF56895B3B3C378C1ED0E33A54950A98D248AB77CB3AA28D2AF204F01EA7FAB18603897B56505C77020ECF64102CCB062205C03A94148DD90FB01E0D3D4548234B146AD202B2A8880609107B5B5E92BCCBA9E2DC8C7B3054CD8EC65F540BE7
	F2DD6A8709A0GFE33387C7EA246EB85781F8D702299072BFDA86F1C2C07E4E89E67B5EDA49AF621A764E485463909760E2D40DFDAA1E27CC2BF62E2301EDBA164FC33EF2FC5CE0762C573A5FDCC54583DB73187AF2E90B97387374819AD1B9EA42BFEBCEC1BB3344FD630DC492EC82E1DF9D4A65A455695B75DDF22062702459513EB0F69E83AEBBA365B60D8FCAF3A712CC8A55CABG2D15A246CF7555301CBDA3C5E4112597F4C8B059970FB833733371D29ECF54D3486D5FE97A6121F497489E87D88C3086E0
	F32508E5AE7553EE7F5D701D6978AB5511ACFF57DD5241E2364A057714FCDD8E999DF2D88E7960C70453FE69C2FD3C0E7D1A39E7A34507216E5B2150A2A67EB7AE2CCD49B0F4E5B0EA48FD32E1A821C01C7EBB2AAB929D8C47DB065075128BFD54C6216ECD7EBF3A30E63CDAA8A45343B337F07A1FE84D4A2A9C84235B35E848E0783FDC443C9F1147F84F787BC587411AD72358FF580C703004F18C6BCA90D282DB98E92C42FA6E0A15DAE204738B4EBA55D522AEC78F0B3A7475FF03B45E3AECFB2F4CAD3E4791
	2BE2B512D6952FCBE64F4B7D76F63FCA7B8647C7181D0F6C4439A8E1E8B16B606B1768AC8272D7GE4D709BA88FD0EF451EBF88134EB2A4A42E846770AFB8F1E07FCADAAC30B4626F64E0C5920FBEF922D1F470F51075D98BF55528F532AB1FE7346457C17D745FAE2D1B50BE1C7B50F8765FBEDC270E5D7B33EBC4ED7F7404A77244967BB4078468FB03E07937852EE1BFAB9DF51C14657FA504A676E91FCDFB9487896F03EA51C0F4D040F523E0EB5376E20B87B871A440CF775156F12F10F47314B666C910A8E
	B1D9750762E7DF7DD5B163E5DFC659C3C00B9D92E73649ED3DE75AA2914D2B50AB0E47BB7D12B5B7986EDB17DE0327DF51A3C69794EECC1C4D37AF213DF887F2B41F899E27338266D59A68DD89301D727F7A965E75DEC9178DA77D17DD1D754352F5A9660C0CC291BBE907F036A33593FF9E67F5D4357F941E0DB5BF7F57EE11F1FEAAA19F9E6F97739A69BE4DE3373463A162EE50EC6767B54A10E3AB0FE9DDCDBC36AB97ADAADB77E1DB897333F9C27899861EA5B5424F87A60E313E6084685665C8C476D1A71D
	5228410D038AC8706F5D2113137CBB975FFDBE45F6700C59325934A5976CA8FFCB58F265761138AD2E3170D416081E75D0CF131DADEC7794160873FF6C3630259B6C18ED5AF22C27BEEECBAF6418B4D5E018ADDD236F5B59E26E1E529CEE4F0C9EE14F8A30E50BE94F0BBD3CFFBB192E7BBAAA6DF4D1F911FA94486E4B1F583EABB45632BC6928CD1CD95C35E2DE73FB30672B301E8D1081108910DDABEA735865E6B24DAC5C990391B5A429FD89171B18C9AFEFE77E7075331751AFFA9F91E17B0A71E45B37A459
	DBFAB8F14D762D1D346FE0523AA5E99F73B9EBG7BE0B750D40019G99GAB81F2818A6A046FA3D7BE1A56F7BC8717568E3833F09F34A75F57262D9FCC5C190F1FCC5A176CE3DD1D5D3E3F5D213E225B5369EB35557753C97B3E183E55536AF3596A3B37674E745DBFE91FB3E92DA669E36FBAEB691DB893E869G8C57BC06388CD8E71A38C7EFA0AE8756F9E69E05BC29674C2847FE4E3C69311FB3E7F80A999EE4D85FBE85FD0EA7559CFA00E0BFD9DD76A65D8F9DBCC2139746AB0953DEB076911B1973B3F889
	A5EC960779F2FCA76769B4347677CC3DBF9FC86D63AF866CFDFCFAG673B0896553DF2329F4F8C141B34F8D5F599F2904D61B3B55DEFC8FAC01E94071768FEC6CB357F4F8329E245FC8DCFD8FDEDB2716F5D36621BCD7C2B897813A6FEF90F957F3E095F9443DEEFC0E08A0E603749B69CEF79FD7748A84ED7FEDD8A02C9FDE159CBF20EE0FEFDBAA63EC31E053587E09CE0A1C05691F1072C3E31036AD32341D0041CBC0A7BF2E9DE223FF5C745CCF722457A9DF2590181874E9CF6B3DD6CA82B0919C763F3871B
	95433486F0CFA65072282EC5E35677883469B02FB3C6F75E305269B70D847A5584BA5ABA12C01F1FC047F84D61F466575BD7442C6A7A478FC87DBD7E145FD7DB0EB1FFBC7C39780D75059BBCBFAC6339FE83773418F21FA556F8112C39366FBF8869DC5E927E5CD2323E105DBDBF43D262B9BA4B07F3D03FB44CEF16CE1FE2E83A38DABE05A1624260B0A42ECB8A90513F22E3F776BDF207CB44A47DC77309DB9DC8B93F67F21BC9BDFBB6127505713B33B2007957883473GE10059G8B8116550BFE77248FED44
	5B919CEAB38C49BBC4A7F761D93FA20F4651185CCC76C7C318CB2BC04E73262C3D725B29E28448EEDD8B4B3AB1463074AD0D5BF9C5419CA9B81E13DF8350816086D00F8B39277D354CC69B7BBAFD816CB978754E90E7F5B8103A3FB584267F66579C407715C14DB034A04A97788F82F82E069626C8067BB800E726AB01A10331F2FCC7G63234AFE0AE638B3C3F8B6E68FE3A1607CDDC39883A524980A24AAB768ABD59FB006820C6ED5D27BB42CCC6FD3C4216FE5E08B0E618C97D370FB036677C7E4B3F63714BD
	A62EAB522DC59434047D9F4E2FFC687F52D7C821E166EAD627354E2ACB2F252C33338D3CFE9B62FD091DFF950D558240AFEBB03FE77A5F1D225E7A02122A9A12BA529672E16D1D99B364783DDB93C07991414309FCC62667B5EC5973FFD0CB87889D88D97EDB8AGG7C9AGGD0CB818294G94G88G88GF0EDFEA79D88D97EDB8AGG7C9AGG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG158AGGGG
**end of data**/
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
	this.addCaretListener(this);
	this.addKeyListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("DateTextField");
		setText("");
		setSize(53, 17);
		setColumns(8);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyPressed(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == this) 
		connEtoC2(e);
	if (e.getSource() == this) 
		connEtoC5();
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyReleased(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == this) 
		connEtoC4(e);
	if (e.getSource() == this) 
		connEtoC5();
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the KeyListener interface.
 * @param e java.awt.event.KeyEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void keyTyped(java.awt.event.KeyEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == this) 
		connEtoC3(e);
	if (e.getSource() == this) 
		connEtoC5();
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
		com.cannontech.common.gui.util.DateTextField aDateTextField;
		aDateTextField = new com.cannontech.common.gui.util.DateTextField();
		frame.getContentPane().add("Center", aDateTextField);
		frame.setSize(aDateTextField.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DateTextField");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
}
